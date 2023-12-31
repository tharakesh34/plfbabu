package com.pennant.backend.model.rulefactory;

import java.util.HashMap;
import java.util.Map;

import org.graalvm.polyglot.HostAccess;
import org.zkoss.util.resource.Labels;

public class RuleResult {

	@HostAccess.Export
	public Object value;
	@HostAccess.Export
	public Object deviation;
	@HostAccess.Export
	public Object provPercentage;
	@HostAccess.Export
	public Object provAmount;
	@HostAccess.Export
	public Object vasProvPercentage;
	@HostAccess.Export
	public Object vasProvAmount;

	public Object getValue() {
		return value;
	}

	@HostAccess.Export
	public void setValue(Object value) {
		this.value = value;
	}

	public Object getDeviation() {
		return deviation;
	}

	@HostAccess.Export
	public void setDeviation(Object deviation) {
		this.deviation = deviation;
	}

	public Object getProvPercentage() {
		return provPercentage;
	}

	@HostAccess.Export
	public void setProvPercentage(Object provPercentage) {
		this.provPercentage = provPercentage;
	}

	public Object getProvAmount() {
		return provAmount;
	}

	@HostAccess.Export
	public void setProvAmount(Object provAmount) {
		this.provAmount = provAmount;
	}

	public Object getVasProvPercentage() {
		return vasProvPercentage;
	}

	@HostAccess.Export
	public void setVasProvPercentage(Object vasProvPercentage) {
		this.vasProvPercentage = vasProvPercentage;
	}

	public Object getVasProvAmount() {
		return vasProvAmount;
	}

	@HostAccess.Export
	public void setVasProvAmount(Object vasProvAmount) {
		this.vasProvAmount = vasProvAmount;
	}

	public Map<String, Object> getDeclaredFieldValues() {
		Map<String, Object> ruleResultMap = new HashMap<>();

		for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
			try {
				Object result = this.getClass().getDeclaredFields()[i].get(this);
				if (result != null) { // FIXME Code should be defined in Rule Constants
					ruleResultMap.put(
							Labels.getLabel("label_RuleResult_" + this.getClass().getDeclaredFields()[i].getName()),
							this.getClass().getDeclaredFields()[i].get(this));
				}
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// Nothing TO DO
			}
		}
		return ruleResultMap;
	}

}
