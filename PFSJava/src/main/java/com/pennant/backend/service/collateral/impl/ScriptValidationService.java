package com.pennant.backend.service.collateral.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.model.ScriptErrors;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.util.RuleReturnType;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennapps.core.util.ObjectUtil;

public class ScriptValidationService {
	private static final Logger logger = LogManager.getLogger(ScriptValidationService.class);

	private List<Object> objectList = null;

	public ScriptErrors setPreValidationDefaults(String script, Map<String, Object> paramMap) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> bindings = new HashMap<>();
		ScriptErrors defaults = new ScriptErrors();

		setObjectsTobindings(bindings);

		bindings.put("defaults", defaults);
		if (paramMap != null) {
			bindings.putAll(paramMap);
		}

		RuleExecutionUtil.executeRule(script, bindings, null, RuleReturnType.OBJECT);
		RuleExecutionUtil.executeRule(script, bindings, "defaults");

		logger.debug(Literal.LEAVING);
		return defaults;
	}

	public ScriptErrors getPostValidationErrors(String script, Map<String, Object> fieldValueMap) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> bindings = new HashMap<>();
		ScriptErrors errors = new ScriptErrors();
		bindings.put("errors", errors);

		if (fieldValueMap != null) {
			bindings.putAll(fieldValueMap);
		}

		RuleExecutionUtil.executeRule(script, bindings, "errors");

		logger.debug(Literal.LEAVING);
		return errors;
	}

	private void setObjectsTobindings(Map<String, Object> bindings) {
		logger.debug(Literal.ENTERING);

		List<Object> objectList = getObjectList();
		if (CollectionUtils.isNotEmpty(objectList)) {
			for (Object object : objectList) {
				if (object != null) {

					if (object instanceof FinanceDetail) {
						bindings.put("fd", ObjectUtil.clone((FinanceDetail) object));
					}
					if (object instanceof CustomerDetails) {
						bindings.put("cu", ObjectUtil.clone((CustomerDetails) object));
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public List<Object> getObjectList() {
		return objectList;
	}

	public void setObjectList(List<Object> objectList) {
		this.objectList = objectList;
	}

}
