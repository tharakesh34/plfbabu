package com.pennant.backend.service.collateral.impl;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.pennanttech.pennapps.core.script.ScriptEngine;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.pennant.backend.model.ScriptErrors;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.rits.cloning.Cloner;

public class ScriptValidationService {

	private static final Logger logger = Logger.getLogger(ScriptValidationService.class);

	private List<Object> objectList = null;

	public ScriptErrors setPreValidationDefaults(String script, Map<String, Object> paramMap) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> dataMap = new HashMap<String, Object>();
		ScriptErrors defaults = new ScriptErrors();

		try (ScriptEngine scriptEngine = new ScriptEngine()) {
			dataMap = setObjectsTobindings(dataMap);

			dataMap.put("defaults", defaults);
			if (paramMap != null) {
				dataMap.putAll(paramMap);
			}
			scriptEngine.getResultAsObject(script, dataMap);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION);
		}

		logger.debug(Literal.LEAVING);
		return defaults;
	}

	/**
	 * Setting the multiple objects into bindings for setting the default values to
	 * components from prescript
	 * 
	 * @param bindings
	 */
	private Map<String, Object> setObjectsTobindings(Map<String, Object> datMap) {
		logger.debug(Literal.ENTERING);

		List<Object> objectList = getObjectList();
		if (CollectionUtils.isNotEmpty(objectList)) {
			for (Object object : objectList) {
				if (object != null) {

					// Cloning the object for original data.
					Cloner cloner = new Cloner();
					Object clonedObject = cloner.deepClone(object);

					if (clonedObject instanceof FinanceDetail) {
						datMap.put("fd", clonedObject);
					}
					if (clonedObject instanceof CustomerDetails) {
						datMap.put("cu", clonedObject);
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return datMap;
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

		Map<String, Object> dataMap = new HashMap<String, Object>();
		ScriptErrors errors = new ScriptErrors();

		try (ScriptEngine scriptEngine = new ScriptEngine()) {
			dataMap.put("errors", errors);
			if (fieldValueMap != null) {
				dataMap.putAll(fieldValueMap);
			}
			scriptEngine.getResultAsObject(script, dataMap);
		} catch (Exception e) {
			logger.error(e);
		}

		logger.debug(Literal.LEAVING);
		return errors;
	}

	public List<Object> getObjectList() {
		return objectList;
	}

	public void setObjectList(List<Object> objectList) {
		this.objectList = objectList;
	}

}
