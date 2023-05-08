package com.pennant.webui.rulefactory.rule;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Window;

import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.webui.customermasters.fincreditrevsubcategory.FinCreditRevSubCategoryDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/RuleFactorry/Rule/RuleResultView.zul file.
 */
public class RuleResultViewCtrl extends GFCBaseCtrl<Object> {
	private static final long serialVersionUID = -546886879998950467L;
	private static final Logger logger = LogManager.getLogger(RuleResultViewCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_RuleResultValues;
	protected Rows rows_ruleValues;
	protected Decimalbox decimalbox;
	protected Row result_row;
	protected Label result_label;
	protected Button btn_Simulate;
	protected RuleResultDialogCtrl ruleResultDialogCtrl;
	protected FinCreditRevSubCategoryDialogCtrl finCreditRevSubCategoryDialogCtrl;

	private List<ValueLabel> variablesList = null;
	private String unKnwVar = "Math,round,pow,sqrt";

	/**
	 * default constructor.<br>
	 */
	public RuleResultViewCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Rule object in a Map.
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_RuleResultValues(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_RuleResultValues);

		try {
			if (arguments.containsKey("fieldsList")) {
				this.variablesList = (List<ValueLabel>) arguments.get("fieldsList");
			}

			// READ OVERHANDED params !
			if (arguments.containsKey("ruleResultDialogCtrl")) {
				this.ruleResultDialogCtrl = (RuleResultDialogCtrl) arguments.get("ruleResultDialogCtrl");
			}

			// READ OVERHANDED params !
			if (arguments.containsKey("finCreditRevSubCategoryDialogCtrl")) {
				this.finCreditRevSubCategoryDialogCtrl = (FinCreditRevSubCategoryDialogCtrl) arguments
						.get("finCreditRevSubCategoryDialogCtrl");
			}

			String amountRuleFormula = "";

			if (this.ruleResultDialogCtrl != null) {
				amountRuleFormula = this.ruleResultDialogCtrl.formula.getValue().trim();
				amountRuleFormula = "Result = " + amountRuleFormula;
			} else if (this.finCreditRevSubCategoryDialogCtrl != null) {
				amountRuleFormula = this.finCreditRevSubCategoryDialogCtrl.formula.getValue().trim();
			}

			if (variablesList != null) {
				for (ValueLabel valueLabel : variablesList) {
					createComponent(valueLabel.getValue(), valueLabel.getLabel());
				}
			}

			if (rows_ruleValues.getVisibleItemCount() == 0) {
				BigDecimal result = (BigDecimal) RuleExecutionUtil.executeRule(amountRuleFormula, null, null,
						RuleReturnType.DECIMAL);
				this.btn_Simulate.setVisible(false);
				setRuleResult(result);
			}

			this.window_RuleResultValues.doModal(); // open the dialog in
		} catch (Exception e) {
			logger.error("Exception: ", e);
			this.window_RuleResultValues.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	private void createComponent(String value, String description) {
		Label label;
		Row row;
		Pattern pattern = Pattern.compile(RuleConstants.RULEFIELD_CCY + "[A-Z]{3}[0-9]+");
		Matcher matcher = pattern.matcher(value);

		if (!unKnwVar.contains(value) && !matcher.find()) {
			row = new Row();
			label = new Label(description);
			row.appendChild(label);
			label = new Label(":");
			row.appendChild(label);
			decimalbox = new Decimalbox();
			decimalbox.setWidth("160px");
			decimalbox.setConstraint("NO EMPTY:" + Labels.getLabel("const_NO_EMPTY"));
			decimalbox.setMaxlength(15);
			decimalbox.setFormat(PennantConstants.rateFormate9);
			decimalbox.setId(value);
			row.appendChild(decimalbox);
			row.setParent(rows_ruleValues);
		}
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(false);
	}

	/**
	 * On click event for simulate button
	 */
	public void onClick$btn_Simulate(Event event) throws InterruptedException, ScriptException {
		logger.debug("Entering" + event.toString());

		Map<String, Object> map = new HashMap<String, Object>();
		String rule = null;

		// evaluate JavaScript code from String
		try {

			for (ValueLabel valueLabel : variablesList) {
				decimalbox = (Decimalbox) rows_ruleValues.getFellowIfAny(valueLabel.getValue());
				map.put(valueLabel.getValue(), decimalbox.getValue() == null ? BigDecimal.ZERO : decimalbox.getValue());
			}

			if (this.ruleResultDialogCtrl != null) {
				rule = this.ruleResultDialogCtrl.formula.getValue();
			}

			if (this.finCreditRevSubCategoryDialogCtrl != null) {
				rule = this.finCreditRevSubCategoryDialogCtrl.formula.getValue();
			}

			// Execute the Rule
			BigDecimal result = (BigDecimal) RuleExecutionUtil.executeRule("Result = " + rule, map, null,
					RuleReturnType.DECIMAL);

			setRuleResult(result);
		} catch (final WrongValueException e) {
			logger.error("Exception: ", e);
			throw e;
		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showError(e.getMessage());
		}

		logger.debug("Leaving" + event.toString());
	}

	private void setRuleResult(BigDecimal ruleResult) {

		this.result_row.setVisible(true);
		if (ruleResult == null) {
			this.result_label.setValue(Labels.getLabel("NOVALUE"));
		} else {
			this.result_label.setValue(ruleResult.toString());

			/*
			 * try{ if(ruleResult.toString().contains(".")){ BigDecimal fractionValue = new
			 * BigDecimal(ruleResult.toString().substring(ruleResult.toString().indexOf(".")+1));
			 * if(fractionValue.compareTo(BigDecimal.ZERO) == 0){
			 * this.result_label.setValue(PennantApplicationUtil.amountFormate( new
			 * BigDecimal(ruleResult.toString().substring(0, ruleResult.toString().indexOf("."))),
			 * PennantConstants.defaultCCYDecPos)); } } }catch(Exception e){ logger.info(e.getMessage()); }
			 */
		}
		// make result row visible and set value
	}
}