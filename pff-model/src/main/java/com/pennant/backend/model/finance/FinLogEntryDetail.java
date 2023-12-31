package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class FinLogEntryDetail implements Serializable {
	private static final long serialVersionUID = 587826313209520957L;

	private long finID;
	private String finReference;
	private long logKey;
	private String eventAction;
	private boolean schdlRecal = false;
	private Date postDate;
	private boolean reversalCompleted = false;

	private List<FinanceScheduleDetail> financeScheduleDetailList;

	public FinLogEntryDetail() {
		super();
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public long getLogKey() {
		return logKey;
	}

	public void setLogKey(long logKey) {
		this.logKey = logKey;
	}

	public String getEventAction() {
		return eventAction;
	}

	public void setEventAction(String eventAction) {
		this.eventAction = eventAction;
	}

	public void setSchdlRecal(boolean schdlRecal) {
		this.schdlRecal = schdlRecal;
	}

	public boolean isSchdlRecal() {
		return schdlRecal;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public boolean isReversalCompleted() {
		return reversalCompleted;
	}

	public void setReversalCompleted(boolean reversalCompleted) {
		this.reversalCompleted = reversalCompleted;
	}

	public long getId() {
		return 0;
	}

	public void setId(long id) {
	}

	public List<FinanceScheduleDetail> getFinanceScheduleDetailList() {
		return financeScheduleDetailList;
	}

	public void setFinanceScheduleDetailList(List<FinanceScheduleDetail> financeScheduleDetailList) {
		this.financeScheduleDetailList = financeScheduleDetailList;
	}

}
