package com.pennant.webui.configuration.vasconfiguration;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;
import org.zkoss.codemirror.Codemirror;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Window;

import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class VASConfigurationResultCtrl extends GFCBaseCtrl<Object> {
	private static final long				serialVersionUID	= -546886879998950467L;
	private static final Logger				logger				= Logger.getLogger(VASConfigurationResultCtrl.class);

	protected Window						window_VASConfigurationResult;

	protected Codemirror					condition;
	protected Grid							fields;
	protected Rows							rows_Fields;
	protected Button						btn_Stimulate;
	protected Row							rowResult;
	protected Label							result;
	protected Decimalbox					textbox;

	JSONArray								variables			= new JSONArray();
	protected VASConfigurationDialogCtrl	vasConfigurationDialogCtrl;

	public VASConfigurationResultCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected VASConfigurationResult  object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_VASConfigurationResult(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_VASConfigurationResult);

		if (arguments.containsKey("RuleVariables")) {
			variables = (JSONArray) arguments.get("RuleVariables");
		}
		// READ OVERHANDED parameters !
		if (arguments.containsKey("vasConfigurationDialogCtrl")) {
			this.vasConfigurationDialogCtrl = (VASConfigurationDialogCtrl) arguments.get("vasConfigurationDialogCtrl");
		}
		Label label;
		for (int i = 0; i < variables.size(); i++) {
			JSONObject variable = (JSONObject) variables.get(i);
			if (!"Result".equals(variable.get("name"))) {
				Row row = new Row();
				label = new Label(variable.get("name").toString());
				row.appendChild(label);
				label = new Label(":");
				row.appendChild(label);
				textbox = new Decimalbox();
				textbox.setId(variable.get("name").toString());
				row.appendChild(textbox);
				row.setParent(rows_Fields);
			}
		}
		this.window_VASConfigurationResult.doModal();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Simulate the Existed rule
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws ScriptException
	 */
	public void onClick$btn_Stimulate(Event event) throws InterruptedException, ScriptException {
		logger.debug("Entering" + event.toString());

		// create a script engine manager
		ScriptEngineManager factory = new ScriptEngineManager();
		// create a JavaScript engine
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		// evaluate JavaScript code from String
		try {
			for (int i = 0; i < variables.size(); i++) {
				JSONObject variable = (JSONObject) variables.get(i);
				if (!"Result".equals(variable.get("name"))) {
					textbox = (Decimalbox) rows_Fields.getFellowIfAny(variable.get("name").toString().trim());
					// bindings to the engine
					engine.put(textbox.getId().trim(),
							textbox.getValue() == null ? BigDecimal.ZERO : textbox.getValue());
				}
			}
			// Execute the engine
			String rule = "function Eligibility(){" + vasConfigurationDialogCtrl.preValidation.getValue() + "}Eligibility();";
			engine.eval(rule);

			Object result = engine.get("Result");

			// make result row visible and set value
			this.rowResult.setVisible(true);
			BigDecimal tempResult = new BigDecimal(result.toString());
			tempResult = tempResult.setScale(2, RoundingMode.UP);
			this.result.setValue(String.valueOf(tempResult));
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(false);
	}

}
