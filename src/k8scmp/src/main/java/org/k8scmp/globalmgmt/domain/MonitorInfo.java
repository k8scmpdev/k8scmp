package org.k8scmp.globalmgmt.domain;

/**
 * Created by feiliu206363 on 2016/1/20.
 */
public class MonitorInfo {

    private String transfer;
    private String graphy;
    private String query;
    private String hbs;
    private String judge;
    private String alarm;
    private String sender;
    private String redis;

    public MonitorInfo() {
    }

	public String getTransfer() {
		return transfer;
	}

	public void setTransfer(String transfer) {
		this.transfer = transfer;
	}

	public String getGraphy() {
		return graphy;
	}

	public void setGraphy(String graphy) {
		this.graphy = graphy;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getHbs() {
		return hbs;
	}

	public void setHbs(String hbs) {
		this.hbs = hbs;
	}

	public String getJudge() {
		return judge;
	}

	public void setJudge(String judge) {
		this.judge = judge;
	}

	public String getAlarm() {
		return alarm;
	}

	public void setAlarm(String alarm) {
		this.alarm = alarm;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getRedis() {
		return redis;
	}

	public void setRedis(String redis) {
		this.redis = redis;
	}

	public MonitorInfo(String transfer, String graphy, String query, String hbs, String judge, String alarm,
			String sender, String redis) {
		super();
		this.transfer = transfer;
		this.graphy = graphy;
		this.query = query;
		this.hbs = hbs;
		this.judge = judge;
		this.alarm = alarm;
		this.sender = sender;
		this.redis = redis;
	}
    
    

}
