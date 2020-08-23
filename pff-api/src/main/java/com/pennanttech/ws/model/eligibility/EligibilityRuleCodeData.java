package com.pennanttech.ws.model.eligibility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

public class EligibilityRuleCodeData implements Serializable {

	private static final long serialVersionUID = 1L;
	@XmlElement
	private String elgRuleCode;
	@XmlElement
	private List<FieldData> fieldDatas;

	private Map<String, Object> map;

	private Map<String, Map<String, Object>> dataMap;
	private List<String> ruleCodes;

	public EligibilityRuleCodeData() {
		super();
	}

	public String getElgRuleCode() {
		return elgRuleCode;
	}

	public void setElgRuleCode(String elgRuleCode) {
		this.elgRuleCode = elgRuleCode;
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

	public void setFieldData(String fieldName, Object value) {
		if (this.map == null) {
			this.map = new HashMap<String, Object>();
		}
		this.map.put(fieldName, value);
	}

	public List<FieldData> getFieldDatas() {
		return fieldDatas;
	}

	public void addRuleCode(String ruleCode) {
		if (this.ruleCodes == null) {
			this.ruleCodes = new ArrayList<String>();
		}
		this.ruleCodes.add(ruleCode);
	}

	public List<String> getRuleCodes() {
		return ruleCodes;
	}

	public void setRuleCodes(List<String> ruleCodes) {
		this.ruleCodes = ruleCodes;
	}

	public Map<String, Map<String, Object>> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, Map<String, Object>> dataMap) {
		this.dataMap = dataMap;
	}

}
