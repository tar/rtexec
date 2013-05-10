package ru.spbstu.rtexec.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ru.spbstu.rtexec.model.ExecutableTask;
import ru.spbstu.rtexec.model.RealTimeTask;

import com.google.gson.Gson;

public class TestServerSocket {

	private static final Gson gson = new Gson();

	private static ScheduledExecutorService _executor = new ScheduledThreadPoolExecutor(2);

	public static void main(String[] args) {
		try {
			ServerSocket serverSocket = new ServerSocket(10000);
			while (true) {
				Socket socket = serverSocket.accept();
				SocketChannel channel = socket.getChannel();
				ByteBuffer bb = ByteBuffer.allocate(8 * 1024);
				StringBuilder sb = new StringBuilder();
				while (channel.read(bb) != -1) {
					bb.flip();
					System.out.println(new String(bb.array()));
					sb.append(new String(bb.array()));
					bb.clear();
					bb.flip();
				}
				RealTimeTask task = gson.fromJson(sb.toString(), RealTimeTask.class);
				if (task == null) {
					System.out.println("cant deserialize");
				}
				if (task.isPoisonPill()) {
					List<Runnable> queuedTasks = _executor.shutdownNow();
					// TODO stop service
				}
				ScheduledFuture<RealTimeTask> future = _executor.schedule(new ExecutableTask(task), 0,
						TimeUnit.MILLISECONDS);
				String result = "";
				try {
					result = gson.toJson(future.get(task.getDirectiveTime(), TimeUnit.MILLISECONDS));
				} catch (InterruptedException e) {
					System.err.println(e.getMessage());
					result = "Error";
				} catch (ExecutionException e) {
					System.err.println(e.getMessage());
					result = "Error";
				} catch (TimeoutException e) {
					System.err.println("Time is over!");
					result = "Deadline crossing";
				}
				PrintWriter pw = new PrintWriter(socket.getOutputStream());
				pw.println(result);
				pw.flush();
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
