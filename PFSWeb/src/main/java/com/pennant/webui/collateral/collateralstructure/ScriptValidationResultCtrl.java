package com.pennant.webui.collateral.collateralstructure;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.codemirror.Codemirror;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennanttech.pennapps.core.script.ScriptEngine;
import com.pennant.backend.model.ScriptError;
import com.pennant.backend.model.ScriptErrors;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/finance/parameters/projectSummaryDialog.zul file.
 */
public class ScriptValidationResultCtrl extends GFCBaseCtrl<ScriptError> {
	private static final long serialVersionUID = -546886879998950467L;
	private static final Logger logger = Logger.getLogger(ScriptValidationResultCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ScriptValidationResult; // autowired

	protected Codemirror condition; // autowired
	protected Grid fields; // autowired
	protected Rows rows_Fields; // autowired
	protected Button btn_Stimulate; // autowired
	protected Row rowResult; // autowired
	protected Label result; // autowired
	protected Textbox textbox;

	private JSONArray variables = new JSONArray();
	private String scriptRule;

	public ScriptValidationResultCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ScriptValidationResult(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ScriptValidationResult);

		if (arguments.containsKey("variables")) {
			variables = (JSONArray) arguments.get("variables");
		}
		if (arguments.containsKey("scriptRule")) {
			scriptRule = (String) arguments.get("scriptRule");
		}

		Label label;
		for (int i = 0; i < variables.size(); i++) {
			JSONObject variable = (JSONObject) variables.get(i);
			if (!"errors".equals(variable.get("name"))) {
				Row row = new Row();
				label = new Label(variable.get("name").toString());
				row.appendChild(label);
				label = new Label(":");
				row.appendChild(label);
				textbox = new Textbox();
				textbox.setId(variable.get("name").toString());
				row.appendChild(textbox);
				row.setParent(rows_Fields);
			}
		}
		if (variables.size() <= 0) {
			this.window_ScriptValidationResult.setHeight("100px");
		} else {
			this.window_ScriptValidationResult.setHeight(variables.size() * 55 + "px");
		}

		this.window_ScriptValidationResult.doModal();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Simulate the Existed rule
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws ScriptException
	 */
	public void onClick$btn_Stimulate(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		Map<String, Object> dataMap = new HashMap<String, Object>();
		
		try(ScriptEngine scriptEngine = new ScriptEngine()){
			for (Object variable : variables) {
				JSONObject jsonObject = (JSONObject) variable;
				if (!"errors".equals(jsonObject.get("name"))) {
					textbox = (Textbox) rows_Fields.getFellowIfAny(jsonObject.get("name").toString().trim());
					dataMap.put(textbox.getId().trim(),
							textbox.getValue() == null ? BigDecimal.ZERO : textbox.getValue());
				}
			}
			String rule =scriptRule;

			ScriptErrors errors = new ScriptErrors();
			dataMap.put("errors", errors);
			scriptEngine.getResultAsObject(rule, dataMap);
			// Print the results

			String errorMessage = "";
			if (errors.getAll().isEmpty()) {
				errorMessage = Labels.getLabel("message_NoError");
			} else {
				for (ScriptError error : errors.getAll()) {
					if (StringUtils.isNotEmpty(error.getCode())) {
						errorMessage = errorMessage.concat(
								error.getCode() + " (" + error.getProperty() + ") : " + error.getValue()) + " \n\n ";
					} else {
						errorMessage = errorMessage.concat(" (" + error.getProperty() + ") : " + error.getValue())
								+ " \n\n ";
					}
				}
			}

			// make result row visible and set value
			this.rowResult.setVisible(true);
			this.result.setValue(errorMessage);
		} catch (Exception e) {
			MessageUtil.showMessage(Labels.getLabel("label_ExtendedFieldModule_ScriptValidation.value"));
			logger.debug(Literal.EXCEPTION, e);
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
