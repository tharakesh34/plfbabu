package com.pennant.backend.service.collateral.impl;

import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.apache.log4j.Logger;

import com.pennant.backend.model.ScriptErrors;

public class ScriptValidationService {

	private static final Logger logger = Logger.getLogger(ScriptValidationService.class);

	private ScriptEngine scriptEngine;

	/**
	 * Method for setting Pre Validation Script default Values to Extended field Details
	 * @param script
	 * @param paramMap
	 * @return
	 * @throws ScriptException
	 */
	public ScriptErrors setPreValidationDefaults(String script,Map<String, Object> paramMap) throws ScriptException{
		logger.debug("Entering");

		Bindings bindings = new SimpleBindings();
		ScriptErrors errors = new ScriptErrors();
		bindings.put("errors", errors);
		if(paramMap != null){
			bindings.putAll(paramMap);
		}
		getScriptEngine().eval(script , bindings);

		logger.debug("Leaving");
		return errors;
	}

	/**
	 * Method for Validating user Entered Field values using Post Script Details
	 * @param script
	 * @param fieldValueMap
	 * @return
	 */
	public ScriptErrors getPostValidationErrors(String script ,Map<String, Object> fieldValueMap) {
		logger.debug("Entering");

		Bindings bindings = new SimpleBindings();
		ScriptErrors errors = new ScriptErrors();
		bindings.put("errors", errors);
		if(fieldValueMap != null){
			bindings.putAll(fieldValueMap);
		}
		try {
			getScriptEngine().eval(script, bindings);
		} catch (ScriptException e) {
			logger.error(e);
		}

		logger.debug("Leaving");
		return errors;
	}

	public ScriptEngine getScriptEngine() {
		return scriptEngine;
	}
	public void setScriptEngine(ScriptEngine scriptEngine) {
		this.scriptEngine = scriptEngine;
	}

}
