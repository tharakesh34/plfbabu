package com.pennant.backend.model.rulefactory;

import java.util.HashMap;

import org.zkoss.util.resource.Labels;

public class RuleResult {
	
	public Object value;
	public Object deviation;
	
	
	public Object getValue() {
		return value;
	}


	public void setValue(Object value) {
		this.value = value;
	}


	public Object getDeviation() {
		return deviation;
	}


	public void setDeviation(Object deviation) {
		this.deviation = deviation;
	}


	public HashMap<String, Object> getDeclaredFieldValues() {
		HashMap<String, Object> ruleResultMap = new HashMap<String, Object>();

		for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
			try {
				Object result = this.getClass().getDeclaredFields()[i].get(this);
				if(result != null){				//FIXME Code should be defined in Rule Constants 
					ruleResultMap.put(Labels.getLabel("label_RuleResult_"+this.getClass().getDeclaredFields()[i].getName()), this.getClass().getDeclaredFields()[i].get(this));
				}
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// Nothing TO DO
			}
		}
		return ruleResultMap;
	}
	
}
