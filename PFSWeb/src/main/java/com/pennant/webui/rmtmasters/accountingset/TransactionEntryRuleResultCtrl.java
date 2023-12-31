package com.pennant.webui.rmtmasters.accountingset;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/finance/parameters/projectSummaryDialog.zul file.
 */
public class TransactionEntryRuleResultCtrl extends GFCBaseCtrl<Object> {
	private static final long serialVersionUID = -546886879998950467L;
	private static final Logger logger = LogManager.getLogger(TransactionEntryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_TransactionEntryRuleResult; // autowired

	protected Codemirror condition; // autowired
	protected Grid fields; // autowired
	protected Rows rows_Fields; // autowired
	protected Button btn_Stimulate; // autowired
	protected Row rowResult; // autowired
	protected Label result; // autowired
	protected Decimalbox amountValueBox;

	JSONArray variables = new JSONArray();
	protected TransactionEntryDialogCtrl transactionEntryDialogCtrl;

	public TransactionEntryRuleResultCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected TransactionEntry object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_TransactionEntryRuleResult(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_TransactionEntryRuleResult);

		if (arguments.containsKey("RuleVariables")) {
			variables = (JSONArray) arguments.get("RuleVariables");
		}
		// READ OVERHANDED parameters !
		if (arguments.containsKey("transactionEntryDialogCtrl")) {
			this.transactionEntryDialogCtrl = (TransactionEntryDialogCtrl) arguments.get("transactionEntryDialogCtrl");
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
				amountValueBox = new Decimalbox();
				amountValueBox.setId(variable.get("name").toString());
				amountValueBox.setScale(PennantConstants.defaultCCYDecPos);
				amountValueBox.setFormat(PennantConstants.amountFormate2);
				row.appendChild(amountValueBox);
				row.setParent(rows_Fields);
			}
		}
		this.window_TransactionEntryRuleResult.doModal();
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

		Map<String, Object> dataMap = new HashMap<>();

		try {
			for (int i = 0; i < variables.size(); i++) {
				JSONObject variable = (JSONObject) variables.get(i);
				if (!"Result".equals(variable.get("name"))) {
					amountValueBox = (Decimalbox) rows_Fields.getFellowIfAny(variable.get("name").toString().trim());

					BigDecimal compValue = amountValueBox.getValue();
					if (compValue == null) {
						compValue = BigDecimal.ZERO;
					}

					compValue = PennantApplicationUtil.unFormateAmount(compValue, PennantConstants.defaultCCYDecPos);

					// bindings to the engine
					dataMap.put(amountValueBox.getId().trim(), compValue);
				}
			}

			Object result = RuleExecutionUtil.executeRule(transactionEntryDialogCtrl.amountRule.getValue(), dataMap,
					null, RuleReturnType.DECIMAL);

			this.rowResult.setVisible(true);
			BigDecimal tempResult = new BigDecimal(result.toString());
			tempResult = PennantApplicationUtil.formateAmount(tempResult, PennantConstants.defaultCCYDecPos);
			this.result.setValue(String.valueOf(tempResult));
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(false);
	}

}
