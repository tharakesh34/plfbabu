package com.pennant.webui.applicationmaster.rulebuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;
import org.zkoss.codemirror.Codemirror;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.JavaScriptBuilder;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.model.GlobalVariable;
import com.pennant.backend.model.applicationmaster.RBFieldDetail;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/RuleFactorry/Rule/RuleResultSimulation.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class RuleResultSimulationCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -546886879998950467L;
	private final static Logger logger = Logger.getLogger(RuleResultSimulationCtrl.class);

	
	/* * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	 
	protected Window window_RuleResultSimulation; // autowired

	protected Codemirror condition;
	protected Grid fields; // autowired
	protected Rows rows_Fields; // autowired
	protected Button btn_Stimulate;
	protected Row rowResult;
	protected Label result;

	List<String> variables = new ArrayList<String>();
	protected JavaScriptBuilder ruleBuilder;
	protected Textbox textbox;
	protected Decimalbox decimalbox;
	protected Intbox intbox;
	protected Datebox datebox;
	protected Checkbox checkbox;
	Map<String,Object> fieldObjectMap = new HashMap<String, Object>();
	List<GlobalVariable> globalList = new ArrayList<GlobalVariable>();
	String ruleResult="";
	private RuleExecutionUtil ruleExecutionUtil;

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Rule object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_RuleResultSimulation(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		final Map<String, Object> args = getCreationArgsMap(event);
		// READ OVERHANDED parameters !
		if (args.containsKey("javaScriptBuilder")) {
			this.ruleBuilder = (JavaScriptBuilder) args.get("javaScriptBuilder");
		}
		if (args.containsKey("fieldObjectMap")) {
			this.fieldObjectMap = (Map<String,Object>) args.get("fieldObjectMap");
			variables.addAll(fieldObjectMap.keySet());
		}
		if (args.containsKey("varList")) {
			globalList =  (List<GlobalVariable>) args.get("varList");
		} 

		ruleResult = ruleBuilder.sqlQuery;
		ruleResult= getRuleExecutionUtil().getGlobalVariables(ruleResult,globalList);
		
		String[] strings = (ruleBuilder.sqlQuery).split("[\\s\\(\\)\\+\\>\\<\\=\\-\\/\\*\\;\\,]");
		Map<String, String> usedMapValues = new HashMap<String, String>();
		List<String> resultList = new ArrayList<String>();
		
		for (int i = 0; i < strings.length; i++) {
			if(variables.contains(strings[i])){
				resultList.add(strings[i].trim());
			}
		}

		Label label;
		for (int i = 0; i < resultList.size(); i++) {

			if(fieldObjectMap.containsKey(resultList.get(i)) 
					&& !usedMapValues.containsKey(resultList.get(i))){
				
				usedMapValues.put(resultList.get(i), resultList.get(i));

				RBFieldDetail details = (RBFieldDetail)fieldObjectMap.get(resultList.get(i));

				Row row = new Row();
				label = new Label(details.getRbFldDesc());
				row.appendChild(label);
				label = new Label(":");
				row.appendChild(label);

				if(details.getRbFldType().equalsIgnoreCase("nvarchar")){
					textbox = new Textbox();
					textbox.setId(details.getRbFldName());
					row.appendChild(textbox);
				}else if(details.getRbFldType().equalsIgnoreCase("bigint")){
					intbox = new Intbox();
					intbox.setId(details.getRbFldName());
					row.appendChild(intbox);
				}else if(details.getRbFldType().equalsIgnoreCase("decimal")){
					decimalbox = new Decimalbox();
					decimalbox.setId(details.getRbFldName());
					row.appendChild(decimalbox);
				}else if(details.getRbFldType().equalsIgnoreCase("smalldatetime")){
					datebox = new Datebox();
					datebox.setId(details.getRbFldName());
					row.appendChild(datebox);
				}else if(details.getRbFldType().equalsIgnoreCase("nchar")){
					checkbox = new Checkbox();
					checkbox.setId(details.getRbFldName());
					row.appendChild(checkbox);
				}
				row.setParent(rows_Fields);
			}
		}
		if(rows_Fields.getVisibleItemCount() == 0){

			// create a script engine manager
			ScriptEngineManager factory = new ScriptEngineManager();
			// create a JavaScript engine
			ScriptEngine engine = factory.getEngineByName("JavaScript");
			// evaluate JavaScript code from String

			// Execute the engine
			String rule="function Rule(){ "+ ruleResult +"}Rule();";
			BigDecimal tempResult=new BigDecimal("0");		
			String result="0";		
			try{
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
			}catch (Exception e) {
 				logger.error(e);
			}				
			this.btn_Stimulate.setVisible(false);
			// make result row visible and set value
			this.rowResult.setVisible(true);		
			this.result.setValue(result);
		}
		this.window_RuleResultSimulation.doModal();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On click event for stimulate button
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
				if(rows_Fields.getFellowIfAny(variables.get(i)) instanceof Textbox){
					textbox = (Textbox) rows_Fields.getFellowIfAny(variables.get(i));
					// bindings to the engine
					engine.put(textbox.getId().trim(), textbox.getValue().trim());
				}else if(rows_Fields.getFellowIfAny(variables.get(i)) instanceof Intbox){
					intbox = (Intbox) rows_Fields.getFellowIfAny(variables.get(i));
					// bindings to the engine
					engine.put(intbox.getId().trim(), intbox.intValue());
				}else if(rows_Fields.getFellowIfAny(variables.get(i)) instanceof Decimalbox){
					decimalbox = (Decimalbox) rows_Fields.getFellowIfAny(variables.get(i));
					// bindings to the engine
					engine.put(decimalbox.getId().trim(), decimalbox.getValue());
				}else if(rows_Fields.getFellowIfAny(variables.get(i)) instanceof Datebox){
					datebox = (Datebox) rows_Fields.getFellowIfAny(variables.get(i));
					// bindings to the engine
					engine.put(datebox.getId().trim(), datebox.getValue());
				}else if(rows_Fields.getFellowIfAny(variables.get(i)) instanceof Checkbox){
					checkbox = (Checkbox) rows_Fields.getFellowIfAny(variables.get(i));
					// bindings to the engine
					engine.put(checkbox.getId().trim(), checkbox.isChecked());
				}
			}
 			// Execute the engine
			String rule="function Rule(){"+ ruleResult +"}Rule();";
			System.out.println(rule);
			BigDecimal tempResult=new BigDecimal("0");		
			String result="0";		

			if (engine.eval(rule)!=null) {
				tempResult=new BigDecimal(engine.eval(rule).toString());
				result = tempResult.toString();
			}else{
				if(engine.get("Result")!=null){
					result=engine.get("Result").toString();
					try {
						if(this.ruleBuilder.ruleType.equalsIgnoreCase("Decimal")){
							tempResult=new BigDecimal(result);
							tempResult = tempResult.setScale(2,RoundingMode.UP);
							result = tempResult.toString();
						}else if(this.ruleBuilder.ruleType.equalsIgnoreCase("Text")){
							result = result.trim().toString();
						}else{
							tempResult=new BigDecimal(result);
							tempResult = tempResult.setScale(0,RoundingMode.FLOOR);
							result = tempResult.toString();
						}
					} catch (Exception e) {
						//do Nothing-- if return type is not a decimal
						result=engine.get("Result").toString();
					}
					
				}
			}	

			// make result row visible and set value
			this.rowResult.setVisible(true);		
			this.result.setValue(result);
		} catch (Exception e) {
			Messagebox.show(e.toString());
			e.printStackTrace();
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
			this.window_RuleResultSimulation.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	@SuppressWarnings("rawtypes")
	private void doClose() throws InterruptedException {
		logger.debug("Entering");
		final String msg = "Do you want cancel simulation";
		final String title = "Cancel Confirmation";

		MultiLineMessageBox.doSetTemplate();
		if (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES
				| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true,
				new EventListener() {
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
			this.window_RuleResultSimulation.onClose();
		}
		logger.debug("Leaving");
	}

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}
	public RuleExecutionUtil getRuleExecutionUtil() {
		return ruleExecutionUtil;
	}


}
