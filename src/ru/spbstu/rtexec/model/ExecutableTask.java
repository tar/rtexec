package ru.spbstu.rtexec.model;

import java.util.concurrent.Callable;

import javax.realtime.AbsoluteTime;
import javax.realtime.Clock;
import javax.realtime.RealtimeThread;

public class ExecutableTask implements Callable<RealTimeTask> {

	private static final Clock _clock = Clock.getRealtimeClock();

	private RealTimeTask _task;

	public ExecutableTask(RealTimeTask task) {
		_task = task;
	}

	@Override
	public RealTimeTask call() throws Exception {
		System.out.println("her");
		_task.setStartTime(_clock.getTime().getMilliseconds());
		System.out.println("Executing task with id = " + _task.getId());
		RealtimeThread.sleep(new AbsoluteTime(30, 0));
		System.out.println("End executing task with id = " + _task.getId());
		_task.setEndTime(_clock.getTime().getMilliseconds());
		return _task;
	}

}
