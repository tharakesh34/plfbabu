package com.pennanttech.ws.model.eligibility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EligibilityDetail {

	private List<EligibilityRuleCodeData> eligibilityRuleCodeDatas;
	private List<FieldData> fieldDatas;

	private List<String> ruleCodes;
	private Map<String, Object> map;

	public EligibilityDetail() {
		super();
	}

	public List<EligibilityRuleCodeData> getEligibilityRuleCodeDatas() {
		return eligibilityRuleCodeDatas;
	}

	public void setEligibilityRuleCodeDatas(List<EligibilityRuleCodeData> eligibilityRuleCodeDatas) {
		this.eligibilityRuleCodeDatas = eligibilityRuleCodeDatas;
	}

	public List<FieldData> getFieldDatas() {
		return fieldDatas;
	}

	public void setFieldDatas(List<FieldData> fieldDatas) {
		this.fieldDatas = fieldDatas;
	}

	public List<String> getRuleCodes() {
		return ruleCodes;
	}

	public void setRuleCodes(List<String> ruleCodes) {
		this.ruleCodes = ruleCodes;
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

	public void addRuleCode(String ruleCode) {
		if (this.ruleCodes == null) {
			this.ruleCodes = new ArrayList<String>();
		}
		this.ruleCodes.add(ruleCode);
	}

	public void setFieldData(String fieldName, Object value) {
		if (this.map == null) {
			this.map = new HashMap<String, Object>();
		}
		this.map.put(fieldName, value);
	}

}
