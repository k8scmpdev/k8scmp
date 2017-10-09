package org.k8scmp.monitormgmt.domain.alarm;

/**
 * Created by baokangwang on 2016/3/31.
 */
public class StrategyInfo {

    private int id;
    private int templateId;
    private String metric;
    private String tag;
    private int pointNum;
    private String aggregateType;
    private String operator;
    private double rightValue;
    private String note;
    private int maxStep;
    private String createTime;
    public StrategyInfo() {
    }

    public StrategyInfo(int id, int templateId,String metric, String tag, int pointNum, String aggregateType, String operator, double rightValue,
    		String note, int maxStep, String createTime) {
        this.id = id;
        this.templateId = templateId;
        this.metric = metric;
        this.tag = tag;
        this.pointNum = pointNum;
        this.aggregateType = aggregateType;
        this.operator = operator;
        this.rightValue = rightValue;
        this.note = note;
        this.maxStep = maxStep;
        this.createTime = createTime;
    }

    public int getTemplateId() {
		return templateId;
	}

	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getPointNum() {
        return pointNum;
    }

    public void setPointNum(int pointNum) {
        this.pointNum = pointNum;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public void setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public double getRightValue() {
        return rightValue;
    }

    public void setRightValue(double rightValue) {
        this.rightValue = rightValue;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getMaxStep() {
        return maxStep;
    }

    public void setMaxStep(int maxStep) {
        this.maxStep = maxStep;
    }
}