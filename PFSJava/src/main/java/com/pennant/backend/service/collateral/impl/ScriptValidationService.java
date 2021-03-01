package com.pennant.backend.service.collateral.impl;

import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.model.ScriptErrors;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.rits.cloning.Cloner;

public class ScriptValidationService {

	private static final Logger logger = LogManager.getLogger(ScriptValidationService.class);

	private ScriptEngine scriptEngine;
	private List<Object> objectList = null;

	/**
	 * Method for setting Pre Validation Script default Values to Extended field Details
	 * 
	 * @param script
	 * @param paramMap
	 * @return
	 * @throws ScriptException
	 */
	public ScriptErrors setPreValidationDefaults(String script, Map<String, Object> paramMap) throws ScriptException {
		logger.debug(Literal.ENTERING);

		Bindings bindings = new SimpleBindings();
		ScriptErrors defaults = new ScriptErrors();

		setObjectsTobindings(bindings);

		bindings.put("defaults", defaults);
		if (paramMap != null) {
			bindings.putAll(paramMap);
		}
		getScriptEngine().eval(script, bindings);

		logger.debug(Literal.LEAVING);
		return defaults;
	}

	/**
	 * Setting the multiple objects into bindings for setting the default values to components from prescript
	 * 
	 * @param bindings
	 */
	private void setObjectsTobindings(Bindings bindings) {
		logger.debug(Literal.ENTERING);

		List<Object> objectList = getObjectList();
		if (CollectionUtils.isNotEmpty(objectList)) {
			for (Object object : objectList) {
				if (object != null) {

					// Cloning the object for original data.
					Cloner cloner = new Cloner();
					Object clonedObject = cloner.deepClone(object);

					if (clonedObject instanceof FinanceDetail) {
						bindings.put("fd", clonedObject);
					}
					if (clonedObject instanceof CustomerDetails) {
						bindings.put("cu", clonedObject);
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Validating user Entered Field values using Post Script Details
	 * 
	 * @param script
	 * @param fieldValueMap
	 * @return
	 */
	public ScriptErrors getPostValidationErrors(String script, Map<String, Object> fieldValueMap) {
		logger.debug(Literal.ENTERING);

		Bindings bindings = new SimpleBindings();
		ScriptErrors errors = new ScriptErrors();
		bindings.put("errors", errors);
		if (fieldValueMap != null) {
			bindings.putAll(fieldValueMap);
		}
		try {
			getScriptEngine().eval(script, bindings);
		} catch (ScriptException e) {
			logger.error(e);
		}

		logger.debug(Literal.LEAVING);
		return errors;
	}

	public ScriptEngine getScriptEngine() {
		return scriptEngine;
	}

	public void setScriptEngine(ScriptEngine scriptEngine) {
		this.scriptEngine = scriptEngine;
	}

	public List<Object> getObjectList() {
		return objectList;
	}

	public void setObjectList(List<Object> objectList) {
		this.objectList = objectList;
	}

}
