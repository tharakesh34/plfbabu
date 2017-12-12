package com.pennant.webui.rulefactory.rule;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import org.apache.log4j.Logger;
import org.zkoss.codemirror.Codemirror;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.JavaScriptBuilder;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.model.GlobalVariable;
import com.pennant.backend.model.applicationmaster.RBFieldDetail;
import com.pennant.backend.model.rulefactory.RuleResult;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/RuleFactorry/Rule/RuleResultSimulation.zul file.
 */
public class RuleResultSimulationCtrl extends GFCBaseCtrl<Object> {
	private static final long serialVersionUID = -546886879998950467L;
	private static final Logger logger = Logger.getLogger(RuleResultSimulationCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_RuleResultSimulation;
	protected Codemirror condition;
	protected Grid fields;
	protected Rows rows_Fields;
	protected Button btn_Stimulate;
	protected Row rowResult;
	protected Label result;

	protected JavaScriptBuilder ruleBuilder;
	protected Textbox textbox;
	protected Decimalbox decimalbox;
	protected Intbox intbox;
	protected Datebox datebox;
	protected Checkbox checkbox;

	Map<String, Object> fieldObjectMap = new HashMap<String, Object>();
	List<String> variables = new ArrayList<String>();
	List<GlobalVariable> globalList = new ArrayList<GlobalVariable>();
	String ruleResult = "";
	private RuleExecutionUtil ruleExecutionUtil;
	private RuleReturnType returnType = null;

	/**
	 * default constructor.<br>
	 */
	public RuleResultSimulationCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Rule object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_RuleResultSimulation(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_RuleResultSimulation);

		if (arguments.containsKey("javaScriptBuilder")) {
			this.ruleBuilder = (JavaScriptBuilder) arguments.get("javaScriptBuilder");
		}

		if (arguments.containsKey("fieldObjectMap")) {
			this.fieldObjectMap = (Map<String, Object>) arguments.get("fieldObjectMap");
			variables.addAll(fieldObjectMap.keySet());
		}

		if (arguments.containsKey("varList")) {
			globalList = (List<GlobalVariable>) arguments.get("varList");
		}

		ruleResult = ruleBuilder.getSqlQuery();
		returnType = ruleBuilder.getRuleType();

		Map<String, String> usedMapValues = new HashMap<String, String>();
		List<String> resultList = new ArrayList<String>();
		String[] strings = (ruleBuilder.getSqlQuery()).split("[\\s\\(\\)\\+\\>\\<\\=\\-\\/\\*\\;\\,]");

		for (int i = 0; i < strings.length; i++) {
			if (variables.contains(strings[i])) {
				resultList.add(strings[i].trim());
			}
		}

		Label label;
		for (int i = 0; i < resultList.size(); i++) {
			if (fieldObjectMap.containsKey(resultList.get(i)) && !usedMapValues.containsKey(resultList.get(i))) {
				usedMapValues.put(resultList.get(i), resultList.get(i));
				RBFieldDetail details = (RBFieldDetail) fieldObjectMap.get(resultList.get(i));

				Row row = new Row();
				label = new Label(details.getRbFldDesc());
				row.appendChild(label);
				label = new Label(":");
				row.appendChild(label);
				
				
				switch (details.getRbFldType().toLowerCase()) {
				case "nvarchar":
					textbox = new Textbox();
					textbox.setId(details.getRbFldName());
					row.appendChild(textbox);
					break;

				case "bigint":
				case "int":
					intbox = new Intbox();
					intbox.setId(details.getRbFldName());
					row.appendChild(intbox);
					break;

				case "decimal":
					decimalbox = new Decimalbox();
					decimalbox.setId(details.getRbFldName());
					row.appendChild(decimalbox);
					break;

				case "smalldatetime":
					datebox = new Datebox();
					datebox.setId(details.getRbFldName());
					row.appendChild(datebox);
					break;

				case "nchar":
					checkbox = new Checkbox();
					checkbox.setId(details.getRbFldName());
					row.appendChild(checkbox);
					break;
				}

				row.setParent(rows_Fields);
			}
		}

		if (rows_Fields.getVisibleItemCount() == 0) {
			Object result = ruleExecutionUtil.executeRule(ruleResult, null, null, this.returnType);
			getRuleResult(result);
			this.btn_Stimulate.setVisible(false);
		}

		this.window_RuleResultSimulation.doModal();

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

	/**
	 * On click event for stimulate button
	 */
	public void onClick$btn_Stimulate(Event event) throws InterruptedException, ScriptException {
		logger.debug("Entering" + event.toString());

		HashMap<String, Object> map = new HashMap<String, Object>();
		// evaluate JavaScript code from String
		try {
			for (int i = 0; i < variables.size(); i++) {
				if (rows_Fields.getFellowIfAny(variables.get(i)) instanceof Textbox) {
					textbox = (Textbox) rows_Fields.getFellowIfAny(variables.get(i));
					map.put(textbox.getId().trim(), textbox.getValue().trim());
				} else if (rows_Fields.getFellowIfAny(variables.get(i)) instanceof Intbox) {
					intbox = (Intbox) rows_Fields.getFellowIfAny(variables.get(i));
					map.put(intbox.getId().trim(), intbox.intValue());
				} else if (rows_Fields.getFellowIfAny(variables.get(i)) instanceof Decimalbox) {
					decimalbox = (Decimalbox) rows_Fields.getFellowIfAny(variables.get(i));
					//map.put(decimalbox.getId().trim(), PennantApplicationUtil.unFormateAmount(decimalbox.getValue(), PennantConstants.defaultCCYDecPos));
					map.put(decimalbox.getId().trim(), decimalbox.getValue());
				} else if (rows_Fields.getFellowIfAny(variables.get(i)) instanceof Datebox) {
					datebox = (Datebox) rows_Fields.getFellowIfAny(variables.get(i));
					map.put(datebox.getId().trim(), datebox.getValue());
				} else if (rows_Fields.getFellowIfAny(variables.get(i)) instanceof Checkbox) {
					checkbox = (Checkbox) rows_Fields.getFellowIfAny(variables.get(i));
					map.put(checkbox.getId().trim(), checkbox.isChecked());
				}
			}

			Object object = ruleExecutionUtil.executeRule(ruleResult, map, null, returnType);

			// make result row visible and set value
			getRuleResult(object);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Get the Result from Object
	 * 
	 * @param object
	 */
	private void getRuleResult(Object object) {
		String resultValue = null;

		if (object == null) {
			resultValue = "No Result";
		} else {
			switch (returnType) {
			case DECIMAL:
				BigDecimal tempResult = (BigDecimal) object;
				//resultValue = PennantApplicationUtil.amountFormate(tempResult, PennantConstants.defaultCCYDecPos);
				resultValue = tempResult.toString();
				break;

			case OBJECT:
				RuleResult ruleResult = (RuleResult) object;
				if (ruleResult.getValue() != null) {
					String result = "Result Value : " + ruleResult.getValue();
					if (ruleResult.getDeviation() != null) {
						result += "\t Deviation : " + ruleResult.getDeviation();
					}
					resultValue = result;
				} else {
					resultValue = "No Result";
				}
				break;

			case STRING:
			case CALCSTRING:
				resultValue = object.toString().trim();
				break;

			case INTEGER:
				Integer integerValue = (Integer) object;
				resultValue = integerValue.toString();
				break;

			case BOOLEAN:
				boolean tempBoolean = (boolean) object;
				if (tempBoolean) {
					resultValue = "TRUE";
				} else {
					resultValue = "FALSE";
				}
				break;
			}
		}

		// make result row visible and set value
		this.rowResult.setVisible(true);
		this.result.setValue(resultValue);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}
}
