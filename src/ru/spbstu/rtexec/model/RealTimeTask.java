package ru.spbstu.rtexec.model;

public class RealTimeTask {

	private long _id;
	private long _startTime;
	private long _directiveTime;
	private long _endTime;
	private boolean _poisonPill;

	public RealTimeTask(long id, long directiveExecutionTimeMs) {
		_id = id;
		_startTime = -1;
		_endTime = -1;
		_directiveTime = directiveExecutionTimeMs;
		_poisonPill = false;
	}

	public RealTimeTask(long id, long directiveExecutionTimeMs, boolean isPoisonPill) {
		_id = id;
		_startTime = -1;
		_endTime = -1;
		_directiveTime = directiveExecutionTimeMs;
		_poisonPill = isPoisonPill;
	}

	public long getStartTime() {
		return _startTime;
	}

	public void setStartTime(long startTime) {
		_startTime = startTime;
	}

	public long getDirectiveTime() {
		return _directiveTime;
	}

	public void setDirectiveTime(long directiveTime) {
		_directiveTime = directiveTime;
	}

	public boolean isPoisonPill() {
		return _poisonPill;
	}

	public void setPoisonPill(boolean poisonPill) {
		_poisonPill = poisonPill;
	}

	public long getId() {
		return _id;
	}

	public void setId(long id) {
		this._id = id;
	}

	public long getEndTime() {
		return _endTime;
	}

	public void setEndTime(long endTime) {
		this._endTime = endTime;
	}

}
