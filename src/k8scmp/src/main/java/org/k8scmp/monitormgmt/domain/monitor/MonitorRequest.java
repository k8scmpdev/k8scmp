package org.k8scmp.monitormgmt.domain.monitor;

import java.util.List;

public class MonitorRequest {
	private long startTime;
    private long endTime;
    private String dataSpec;
    private String name;
    private String type;
    private int step;
    private List counters;

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public List getCounters() {
		return counters;
	}

	public void setCounters(List counters) {
		this.counters = counters;
	}

	public MonitorRequest(long startTime, long endTime, String dataSpec, String name, String type, int step,
			List counters) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
		this.dataSpec = dataSpec;
		this.name = name;
		this.type = type;
		this.step = step;
		this.counters = counters;
	}

	public MonitorRequest() {
		
	}
    
    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
    
	public long getStartTime() {
		return startTime;
	}
	
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	public long getEndTime() {
		return endTime;
	}
	
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	public String getDataSpec() {
		return dataSpec;
	}
	
	public void setDataSpec(String dataSpec) {
		this.dataSpec = dataSpec;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
