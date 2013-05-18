package ru.spbstu.rtexec.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import ru.spbstu.rtexec.model.RealTimeTask;

import com.google.gson.Gson;

public class TestSocket {

	private static final Gson gson = new Gson();

	private static class KnockKnock implements Runnable {
		private int _index;

		public KnockKnock(int index) {
			_index = index;
		}

		@Override
		public void run() {
			try {
                System.out.println("connect...");
				Socket socket = new Socket("localhost", 10000);
                System.out.println("connection was established");
                System.out.println("create new task id=" + _index);
                RealTimeTask task = new RealTimeTask(_index, 300 + _index);
				if (_index == 3) {
					task.setPoisonPill(true);
				}
				PrintWriter pw = new PrintWriter(socket.getOutputStream());
                System.out.println("serialize task and send as json");
                System.out.println(gson.toJson(task));
				pw.println(gson.toJson(task));
				pw.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String resultLine;
                StringBuilder sb = new StringBuilder();

                while ( (resultLine = reader.readLine()) != null ) {
                    System.out.println("buffer was received: " + resultLine);
                    sb.append(resultLine);
					System.out.println(resultLine);
				}
				socket.close();
				String result = sb.toString();
				if (result.startsWith("{")) {
					RealTimeTask exTask = gson.fromJson(result, RealTimeTask.class);
					System.out.println("Task " + _index + " was executed. time = " + (exTask.getEndTime() - exTask.getStartTime()));
				}else {
					System.err.println("Task " + _index + " isn't executed");
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			new Thread(new KnockKnock(i)).start();
		}


	}
}
