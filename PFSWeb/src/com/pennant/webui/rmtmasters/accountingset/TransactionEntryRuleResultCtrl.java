package com.pennant.webui.rmtmasters.accountingset;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;
import org.zkoss.codemirror.Codemirror;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Window;

import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/finance/parameters/projectSummaryDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class TransactionEntryRuleResultCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -546886879998950467L;
	private final static Logger logger = Logger.getLogger(TransactionEntryDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_TransactionEntryRuleResult; 	// autowired

	protected Codemirror 	condition;							// autowired
	protected Grid 			fields; 							// autowired
	protected Rows 			rows_Fields; 						// autowired
	protected Button 		btn_Stimulate;						// autowired
	protected Row 			rowResult;							// autowired
	protected Label 		result;								// autowired
	protected Decimalbox 	textbox;
	
	JSONArray variables = new JSONArray();
	protected TransactionEntryDialogCtrl transactionEntryDialogCtrl;

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected TransactionEntry
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_TransactionEntryRuleResult(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		final Map<String, Object> args = getCreationArgsMap(event);
		// READ OVERHANDED parameters !

		if (args.containsKey("RuleVariables")) {
			variables = (JSONArray) args.get("RuleVariables");
		}
		// READ OVERHANDED parameters !
		if (args.containsKey("transactionEntryDialogCtrl")) {
			this.transactionEntryDialogCtrl = (TransactionEntryDialogCtrl) args
					.get("transactionEntryDialogCtrl");
		}
		Label label;
		for (int i = 0; i < variables.size(); i++) {
			JSONObject variable = (JSONObject) variables.get(i);
			if (!variable.get("name").equals("Result")) {
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
		this.window_TransactionEntryRuleResult.doModal();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Simulate the Existed rule
	 * @param event
	 * @throws InterruptedException
	 * @throws ScriptException
	 */
	public void onClick$btn_Stimulate(Event event) throws InterruptedException,ScriptException {
		logger.debug("Entering" + event.toString());
		
		// create a script engine manager
		ScriptEngineManager factory = new ScriptEngineManager();
		// create a JavaScript engine
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		// evaluate JavaScript code from String
		try {
			for (int i = 0; i < variables.size(); i++) {
				JSONObject variable = (JSONObject) variables.get(i);
				if (!variable.get("name").equals("Result")) {
					textbox = (Decimalbox) rows_Fields.getFellowIfAny(variable
							.get("name").toString().trim());
					// bindings to the engine
					engine.put(textbox.getId().trim(), textbox.getValue()== null ? BigDecimal.ZERO : textbox.getValue());
				}
			}
			// Execute the engine
			String rule="function Eligibility(){"+transactionEntryDialogCtrl.amountRule.getValue()+"}Eligibility();";
			engine.eval(rule);			
			
			Object result=engine.get("Result");
				
			// make result row visible and set value
			this.rowResult.setVisible(true);		
			BigDecimal tempResult= new BigDecimal(result.toString());
			tempResult = tempResult.setScale(2,RoundingMode.UP);
			this.result.setValue(String.valueOf(tempResult));
		} catch (Exception e) {
			logger.error(e);
			Messagebox.show(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {
			doClose();
		} catch (final Exception e) {
			logger.error(e);
			// close anyway
			this.window_TransactionEntryRuleResult.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Close the Existed window
	 * @throws InterruptedException
	 */
	private void doClose() throws InterruptedException {
		logger.debug("Entering");
		
		final String msg = "Do you want cancel simulation";
		final String title = "Cancel Confirmation";

		MultiLineMessageBox.doSetTemplate();
		if (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES
				| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true,
				new EventListener<Event>() {
					@Override
					public void onEvent(Event evt) {
						switch (((Integer) evt.getData()).intValue()) {
						case MultiLineMessageBox.YES:
						case MultiLineMessageBox.NO:
							break; //
						}
					}
				}

		) == MultiLineMessageBox.YES) {
			this.window_TransactionEntryRuleResult.onClose();
		}
		logger.debug("Leaving");
	}

}
