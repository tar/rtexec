package ru.spbstu.rtexec.server;

import java.io.*;
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
                System.out.println("waiting connection...");
				Socket socket = serverSocket.accept();
                System.out.println("connection was accepted");
                InputStream inputStream = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                System.out.println("start message receiving from input stream " + inputStream);
				String line = reader.readLine();
				if (line == null)
                {
                    System.out.println("error while receiving message");
                    continue;
                }
                System.out.println("new buffer was received: " + line);

                System.out.println("deserialize object");
				RealTimeTask task = gson.fromJson(line, RealTimeTask.class);
				if (task == null) {
					System.out.println("cant deserialize");
				}
				if (task.isPoisonPill()) {
					List<Runnable> queuedTasks = _executor.shutdownNow();
					// TODO stop service
				}
                System.out.println("schedule new task id=" + task.getId());
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
