/*
package com.pennant.webui.applicationmaster.rulebuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Window;

import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;

*//**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/RuleFactorry/Rule/RuleResult.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 *//*
public class RuleResultViewCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -546886879998950467L;
	private final static Logger logger = Logger.getLogger(RuleResultViewCtrl.class);

	
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 
	protected Window 	window_RuleResultValues; 	// autowired
	protected Rows 		rows_ruleValues;	 		// autowired
	protected Decimalbox decimalbox;
	protected Row 		result_row;				// autowired
	protected Label 	result_label;			// autowired
	protected Button 	btn_Stimulate;

	String[] Variables ={};
	String Values;
	protected RuleResultDialogCtrl ruleResultDialogCtrl;  


	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	*//**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Rule object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 *//*
	public void onCreate$window_RuleResultValues(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("Variables")) {
			this.Variables = (String[]) args.get("Variables");
		}
		// READ OVERHANDED params !
		if (args.containsKey("ruleResultDialogCtrl")) {
			this.ruleResultDialogCtrl = (RuleResultDialogCtrl) args.get("ruleResultDialogCtrl");
		}
		String unKnwVar = "Math,round,pow,sqrt";
		Label label;
		Row row ;
		for(int i=0;i< Variables.length;i++){
			if(!unKnwVar.contains(Variables[i])){
				row = new Row();
				label = new Label(Variables[i]);	
				row.appendChild(label);
				label = new Label(":");
				row.appendChild(label);
				decimalbox = new Decimalbox();
				decimalbox.setWidth("160px");
				decimalbox.setConstraint("NO EMPTY:" + Labels.getLabel("const_NO_EMPTY"));
				decimalbox.setMaxlength(15);
				decimalbox.setFormat(PennantConstants.rateFormate9);
				decimalbox.setId(Variables[i].trim());
				row.appendChild(decimalbox);
				row.setParent(rows_ruleValues);
			}
		}
		if(rows_ruleValues.getVisibleItemCount() == 0){
			
			// create a script engine manager
			ScriptEngineManager factory = new ScriptEngineManager();
			// create a JavaScript engine
			ScriptEngine engine = factory.getEngineByName("JavaScript");
			// evaluate JavaScript code from String

			// Execute the engine
			String rule="function Rule(){Result = "+ this.ruleResultDialogCtrl.formula.getValue() +"}Rule();";
			BigDecimal tempResult=new BigDecimal("0");		
			String result="0";		

			if (engine.eval(rule)!=null) {
				tempResult=new BigDecimal(engine.eval(rule).toString());
				result = tempResult.toString();
			}else{
				if(engine.get("Result")!=null){
					result=engine.get("Result").toString();
					tempResult=new BigDecimal(result);
					tempResult = tempResult.setScale(2,RoundingMode.UP);
					result = tempResult.toString();
				}
			}	
			this.btn_Stimulate.setVisible(false);
			// make result row visible and set value
			this.result_row.setVisible(true);
			this.result_label.setValue(result.toString());
		}
		this.window_RuleResultValues.doModal(); // open the dialog in
		logger.debug("Leaving" + event.toString());
	}

	*//**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 *//*
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {
			doClose();
		} catch (final Exception e) {
			// close anyway
			this.window_RuleResultValues.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	*//**
	 * Method for call the closing Window
	 * @throws InterruptedException
	 *//*
	private void doClose() throws InterruptedException {
		logger.debug("Entering");

		final String msg = "Do you want cancel simulation";

		MultiLineMessageBox.doSetTemplate();
		if (MultiLineMessageBox.show(msg, Labels.getLabel("common.Close.Window"), MultiLineMessageBox.YES | MultiLineMessageBox.NO,
				MultiLineMessageBox.QUESTION, true, new EventListener<Event>() {
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

			this.window_RuleResultValues.onClose();
		}
		logger.debug("Leaving");
	}

	*//**
	 * On click event for stimulate button
	 *//*
	public void onClick$btn_Stimulate(Event event) throws InterruptedException, ScriptException {
		logger.debug("Entering" + event.toString());

		// create a script engine manager
		ScriptEngineManager factory = new ScriptEngineManager();
		// create a JavaScript engine
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		// evaluate JavaScript code from String
		try {
			for(int i=0;i< Variables.length;i++){
				decimalbox = (Decimalbox)rows_ruleValues.getFellowIfAny(Variables[i].trim());
				engine.put(decimalbox.getId().trim(),
						decimalbox.getValue()==null?new Double(0):decimalbox.getValue());
			}    
			this.result_row.setVisible(false);

			// Execute the engine
			String rule="function Rule(){ Result = "+ this.ruleResultDialogCtrl.formula.getValue() +"}Rule();";
			BigDecimal tempResult=new BigDecimal("0");		
			String result="0";		

			if (engine.eval(rule)!=null) {
				tempResult=new BigDecimal(engine.eval(rule).toString());
				result = tempResult.toString();
			}else{
				if(engine.get("Result")!=null){
					result=engine.get("Result").toString();
					tempResult=new BigDecimal(result);
					tempResult = tempResult.setScale(2,RoundingMode.UP);
					result = tempResult.toString();
				}
			}	

			// make result row visible and set value
			this.result_row.setVisible(true);
			this.result_label.setValue(result.toString());
		} catch (final WrongValueException e) {
			logger.error(e);
			throw e;
		} catch (final Exception e) {
			logger.error(e);
			MultiLineMessageBox.show(e.getMessage(), Labels.getLabel("message.Error"), MultiLineMessageBox.OK,
					MultiLineMessageBox.ERROR, true);
		}
		logger.debug("Leaving" + event.toString());
	}

}*/