package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LMSServiceLog implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private String event;
	private long finID;
	private String finReference;
	private BigDecimal oldRate;
	private BigDecimal newRate;
	private Date effectiveDate;
	private String notificationFlag;

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
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

	public BigDecimal getOldRate() {
		return oldRate;
	}

	public void setOldRate(BigDecimal oldRate) {
		this.oldRate = oldRate;
	}

	public BigDecimal getNewRate() {
		return newRate;
	}

	public void setNewRate(BigDecimal newRate) {
		this.newRate = newRate;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getNotificationFlag() {
		return notificationFlag;
	}

	public void setNotificationFlag(String notificationFlag) {
		this.notificationFlag = notificationFlag;
	}

	public Map<String, Object> getDeclaredFieldValues() {
		Map<String, Object> customerMap = new HashMap<>();

		return getDeclaredFieldValues(customerMap);
	}

	public Map<String, Object> getDeclaredFieldValues(Map<String, Object> customerMap) {
		customerMap = new HashMap<>();
		for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
			try {
				// "ct_" Should be in small case only, if we want to change the case we need to update the configuration
				// fields as well.
				customerMap.put("rc_" + this.getClass().getDeclaredFields()[i].getName(),
						this.getClass().getDeclaredFields()[i].get(this));
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
			}
		}
		return customerMap;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
