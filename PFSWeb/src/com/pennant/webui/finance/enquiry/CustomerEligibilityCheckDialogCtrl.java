/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  CustomerEligibilityCheckDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */

package com.pennant.webui.finance.enquiry;

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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.BMTRBFldDetails;
import com.pennant.backend.service.finance.EligibilityRule;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.lmtmasters.FinanceReferenceDetailService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseListCtrl;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the 
 * /WEB-INF/pages/Enquiry/FinanceInquiry/EligibilityCheck.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CustomerEligibilityCheckDialogCtrl extends GFCBaseListCtrl<CustomerEligibilityCheck> implements Serializable {

	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(CustomerEligibilityCheckDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_EligibilityCheck; 		// autoWired

	protected Borderlayout	borderlayoutEligibilityCheck;	// autoWired
	protected Groupbox 		gb_BasicDetails; 				// autoWired
	protected Groupbox 		gb_KeyDetails; 					// autoWired

	//Basic Details
	protected ExtendedCombobox 		finType; 						// autoWired
	protected Textbox 		custCIF; 						// autoWired
	protected Label 		custShrtName; 					// autoWired
	protected Rows			rows_KeyDetails;				// autoWired

	protected JdbcSearchObject<Customer> custCIFSearchObject;
	protected FinanceType financeType;
	protected Customer customer;
	
	private FinanceReferenceDetailService financeReferenceDetailService;
	private RuleService ruleService;
	private FinanceDetailService financeDetailService;
	private RuleExecutionUtil ruleExecutionUtil;
	
	protected List<FinanceReferenceDetail> elgRuleList;
	protected List<String> feildList = null;
	protected Map<String , BMTRBFldDetails> fldDetailsMap = null;
	private boolean fldsFetched = false;
	private List<String> resultList = null;

	// not auto wired variables
	private Textbox textbox;
	private Intbox intbox;
	private Datebox datebox;
	private Decimalbox decimalbox;
	private Checkbox checkbox;
	
	private int formatter = 3;
	private Textbox elgModule;
	private long custID;
	private String oldVar_FinType="";
	/**
	 * default constructor.<br>
	 */
	public CustomerEligibilityCheckDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, 
	 * if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_EligibilityCheck(ForwardEvent event) throws Exception {
		logger.debug("Entering " + event.toString());
		this.custCIF.setValue("");
		this.finType.setValue("");
		
		this.rows_KeyDetails.getChildren().clear();
		this.gb_KeyDetails.setVisible(false);
		fldsFetched = false;
		fldDetailsMap = new HashMap<String, BMTRBFldDetails>();
		feildList = new ArrayList<String>();
		resultList = null;
		doSetFieldProperties();
		this.borderlayoutEligibilityCheck.setHeight(getBorderLayoutHeight());
		logger.debug("Leaving " + event.toString());
	}


	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.finType.setMaxlength(8);
		this.finType.setMandatoryStyle(true);
		this.finType.setModuleName("FinanceType");
		this.finType.setValueColumn("FinType");
		this.finType.setDescColumn("FinTypeDesc");
		this.finType.setValidateColumns(new String[] { "FinType" });
		logger.debug("Leaving");
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	/**
	 * onChange get the customer Details
	 * @param event
	 * @throws InterruptedException
	 */
	public void onChange$custCIF(Event event) throws InterruptedException{
		logger.debug("Entering" + event.toString());
		
		this.custCIF.clearErrorMessage();
		
		Customer customer = (Customer)PennantAppUtil.getCustomerObject(this.custCIF.getValue(), null);

		if(customer == null) {	
			this.custID = Long.valueOf(0);
			if(this.rows_KeyDetails.getChildren() != null) {
				this.rows_KeyDetails.getChildren().clear();
				this.gb_KeyDetails.setVisible(false);
			}
			throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID", new String[] { Labels.getLabel("label_EligibilityCheck_CustCIF.value") }));
		} else {
			doSetCustomer(customer, null);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when clicks on button "SearchFinType"
	 * 
	 * @param event
	 */
	public void onFulfill$finType(Event event) {
		logger.debug("Entering " + event.toString());
		
		Object dataObject = this.finType.getObject();
		if (dataObject instanceof String) {
			this.finType.setValue(dataObject.toString());
			formatter = 3;
			setFinanceType(null);
		} else {
			FinanceType details = (FinanceType) dataObject;
			if (details != null) {
				this.finType.setValue(details.getFinType());
				//	formatter = details.getLovDescFinFormetter();
				formatter = 3;
				setFinanceType(details);
			}
		}
		
		if(!oldVar_FinType.equals(this.finType.getValue())){
			this.rows_KeyDetails.getChildren().clear();
			this.gb_KeyDetails.setVisible(false);
		}
		oldVar_FinType = StringUtils.trimToEmpty(this.finType.getValue());
			logger.debug("Leaving " + event.toString());
	}
	
	public void onClick$btnGo(Event event) {
		logger.debug("Entering" + event.toString());

		if("FINANCE".equals(this.elgModule.getValue())){
			if(StringUtils.trimToEmpty(this.finType.getValue()).equals("")){
				throw new WrongValueException(this.finType, Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_EligibilityCheck_FinType.value") }));
			}else{
				Clients.clearWrongValue(finType);
			}
		}else if("CUSTOMER".equals(this.elgModule.getValue())){
			if(this.custID  == 0){
				throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_NUMBER", new String[] { Labels.getLabel("label_EligibilityCheck_CustCIF.value") }));
			}
		}

		this.rows_KeyDetails.getChildren().clear();
		this.gb_KeyDetails.setVisible(false);
		
		setElgRuleList(getFinanceReferenceDetailService().getFinRefDetByRoleAndFinType(
				this.finType.getValue(), null, "_AEView"));

		if(getElgRuleList() != null && getElgRuleList().size() > 0){
			if(!fldsFetched){
				List<BMTRBFldDetails> list = getRuleService().getFieldList("ELGRULE", "");
				for (BMTRBFldDetails detail : list) {
					if(!fldDetailsMap.containsKey(detail.getRbFldName().trim())){
						fldDetailsMap.put(detail.getRbFldName().trim(),detail );
					}
				}

				feildList.addAll(fldDetailsMap.keySet());
				fldsFetched = true;
			}
		}

		resultList = new ArrayList<String>();
		for (FinanceReferenceDetail referenceDetail : getElgRuleList()) {

			String[] strings = (referenceDetail.getLovDescElgRuleValue()).split("[\\s\\(\\)\\+\\>\\<\\=\\-\\/\\*\\;]");
			for (int i = 0; i < strings.length; i++) {
				if(feildList.contains(strings[i].trim()) && !resultList.contains(strings[i].trim())){
					resultList.add(strings[i].trim());
				}
			}
		}

		//Design Feild Details List on Screen with Filled Data or Without
		Label label;
		Row row = null;
		String fieldValue = "";
		for (int i = 0; i < resultList.size(); i++) {

			if(fldDetailsMap.containsKey(resultList.get(i)) &&
					!resultList.get(i).startsWith("custPD")){

				BMTRBFldDetails details = (BMTRBFldDetails)fldDetailsMap.get(resultList.get(i));

				String fieldName = String.valueOf(details.getRbFldName().charAt(0)).toUpperCase()+details.getRbFldName().substring(1);

				if(i%2 == 0){
					row = new Row();
				}
				label = new Label(details.getRbFldDesc());
				row.appendChild(label);

				if(details.getRbFldType().equalsIgnoreCase("nvarchar")){
					textbox = new Textbox();
					textbox.setId(details.getRbFldName());
					textbox.setWidth((details.getRbFldLen()*12)+"px");
					textbox.setMaxlength(details.getRbFldLen());

					try {
						if(fieldName.equalsIgnoreCase("CustCtgType")){
							fieldValue = getCustomer().getLovDescCustCtgType();
						}else{
							fieldValue = getCustomer().getClass().getMethod("get"+fieldName).invoke(
									getCustomer()).toString();
						}
						textbox.setValue(fieldValue);
					} catch (Exception e) {
						textbox.setValue(fieldValue);
					} 
					row.appendChild(textbox);
				}else if(details.getRbFldType().equalsIgnoreCase("bigint")){
					intbox = new Intbox();
					intbox.setId(details.getRbFldName());
					intbox.setWidth((details.getRbFldLen()*12)+"px");
					intbox.setMaxlength(details.getRbFldLen());

					try {
						fieldValue = getCustomer().getClass().getMethod( "get"+fieldName).invoke(
								getCustomer()).toString();
						intbox.setValue(Integer.parseInt(fieldValue));
					} catch (Exception e) {

						if(fieldName.equalsIgnoreCase("CustAge") && customer != null){
							intbox.setValue(DateUtility.getYearsBetween(
									customer.getCustDOB(), DateUtility.today()));
						}else{
							intbox.setValue(0);
						}
					} 
					row.appendChild(intbox);
				}else if(details.getRbFldType().equalsIgnoreCase("decimal")){
					decimalbox = new Decimalbox();
					decimalbox.setId(details.getRbFldName());
					decimalbox.setWidth((details.getRbFldLen()*12)+"px");
					decimalbox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
					decimalbox.setMaxlength(details.getRbFldLen() +2);

					try {
						fieldValue = getCustomer().getClass().getMethod( "get"+fieldName).invoke(
								getCustomer()).toString();
						decimalbox.setValue(PennantAppUtil.formateAmount(new BigDecimal(fieldValue),formatter));
					} catch (Exception e) {
						decimalbox.setValue(BigDecimal.ZERO);
					} 
					row.appendChild(decimalbox);
				}else if(details.getRbFldType().equalsIgnoreCase("smalldatetime")){
					datebox = new Datebox();
					datebox.setId(details.getRbFldName());
					datebox.setWidth("100px");
					datebox.setFormat(PennantConstants.dateFormat);

					try {
						fieldValue = getCustomer().getClass().getMethod( "get"+fieldName).invoke(
								getCustomer()).toString();
						datebox.setText(fieldValue);
					} catch (Exception e) {
						datebox.setText("");
					} 

					row.appendChild(datebox);
				}else if(details.getRbFldType().equalsIgnoreCase("nchar")){
					checkbox = new Checkbox();
					checkbox.setId(details.getRbFldName());

					try {
						fieldValue = getCustomer().getClass().getMethod( "is"+fieldName).invoke(
								getCustomer()).toString();
						checkbox.setChecked(Boolean.valueOf(fieldValue));
					} catch (Exception e) {
						checkbox.setChecked(false);
					} 
					row.appendChild(checkbox);
				}
				row.setParent(rows_KeyDetails);
				this.gb_KeyDetails.setVisible(true);
			}
		}

		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Method to prepare data required for customer eligibility check
	 * 
	 * @return CustomerEligibilityCheck
	 */
	private CustomerEligibilityCheck setCustomerEligibilityData(Customer customer) {
		logger.debug("Entering");
		
		//Customer Eligibility Amounts Calculation
		CustomerEligibilityCheck custElgCheck = getFinanceDetailService().getCustEligibilityDetail(customer,"", 
				customer.getCustBaseCcy(), BigDecimal.ZERO, 0 , BigDecimal.ZERO, null,"");
		
		if ("FINANCE".equals(this.elgModule.getValue()) && 
				!StringUtils.trimToEmpty(this.finType.getValue()).equals("")) {
			custElgCheck.setReqProduct(getFinanceType().getLovDescProductCodeName());
		}
		
		logger.debug("Leaving");
		return custElgCheck;
	}
	
	/**
	 * On click event for stimulate button
	 */
	public void onClick$btnStimulate(Event event) throws InterruptedException, ScriptException {
		logger.debug("Entering" + event.toString());

		try {

			List<EligibilityRule> eligibilityRules = new ArrayList<EligibilityRule>();
			EligibilityRule eligibilityRule = null;

			// create a script engine manager
			ScriptEngineManager factory = new ScriptEngineManager();

			// create a JavaScript engine
			ScriptEngine engine = factory.getEngineByName("JavaScript");

			//Setting Data
			CustomerEligibilityCheck custElgCheck = setCustomerEligibilityData(getCustomer());
			HashMap<String, Object> fieldsandvalues = custElgCheck.getDeclaredFieldValues();

			ArrayList<String> keyset = new ArrayList<String>(fieldsandvalues.keySet());
			for (int i = 0; i < keyset.size(); i++) {
				Object var=fieldsandvalues.get(keyset.get(i));
				if (var instanceof String) {
					var=var.toString().trim();
				}
				engine.put(keyset.get(i),var );
			}

			// evaluate JavaScript code from String

			for (int i = 0; i < resultList.size(); i++) {
				if(rows_KeyDetails.getFellowIfAny(resultList.get(i)) instanceof Textbox){
					textbox = (Textbox) rows_KeyDetails.getFellowIfAny(resultList.get(i));
					// bindings to the engine
					engine.put(textbox.getId().trim(), StringUtils.trimToEmpty(textbox.getValue()));
				}else if(rows_KeyDetails.getFellowIfAny(resultList.get(i)) instanceof Intbox){
					intbox = (Intbox) rows_KeyDetails.getFellowIfAny(resultList.get(i));
					// bindings to the engine
					engine.put(intbox.getId().trim(), intbox.intValue());
				}else if(rows_KeyDetails.getFellowIfAny(resultList.get(i)) instanceof Decimalbox){
					decimalbox = (Decimalbox) rows_KeyDetails.getFellowIfAny(resultList.get(i));
					// bindings to the engine
					if("reqPftRate".equals(decimalbox.getId().trim()) || "tenure".equals(decimalbox.getId().trim())){
						engine.put(decimalbox.getId().trim(), decimalbox.getValue() == null ? BigDecimal.ZERO : 
							decimalbox.getValue());
					}else{
						engine.put(decimalbox.getId().trim(), decimalbox.getValue() == null ? BigDecimal.ZERO : 
							PennantAppUtil.unFormateAmount(decimalbox.getValue(), formatter));
					}
				}else if(rows_KeyDetails.getFellowIfAny(resultList.get(i)) instanceof Datebox){
					datebox = (Datebox) rows_KeyDetails.getFellowIfAny(resultList.get(i));
					// bindings to the engine
					engine.put(datebox.getId().trim(), datebox.getValue());
				}else if(rows_KeyDetails.getFellowIfAny(resultList.get(i)) instanceof Checkbox){
					checkbox = (Checkbox) rows_KeyDetails.getFellowIfAny(resultList.get(i));
					// bindings to the engine
					engine.put(checkbox.getId().trim(), checkbox.isChecked());
				}
			}

			for (FinanceReferenceDetail detail : getElgRuleList()) {
				//Rule List Execution
				if ("CUSTOMER".equals(this.elgModule.getValue()) && 
						StringUtils.trimToEmpty(this.finType.getValue()).equals("")) {
					engine.put("reqFinType", detail.getFinType());
					engine.put("reqFinCcy", detail.getLovDescFinCcyCode());
					engine.put("reqProduct", detail.getLovDescProductCodeName());
					engine.put("reqCampaign", "");
					engine.put("reqFinAmount", 10000000);
				}

				eligibilityRule = new EligibilityRule();
				eligibilityRule.setRuleCode(detail.getLovDescCodelov());
				eligibilityRule.setRuleCodeDesc(detail.getLovDescNamelov());
				eligibilityRule.setFinType(detail.getFinType());
				eligibilityRule.setFinTypeDesc(detail.getLovDescFinTypeDescName());
				eligibilityRule.setRuleReturnType(detail.getLovDescRuleReturnType());

				String rule= "";
				if(SystemParameterDetails.getGlobaVariableList() !=null &&
						SystemParameterDetails.getGlobaVariableList().size()>0) {
					rule = getRuleExecutionUtil().getGlobalVariables(detail.getLovDescElgRuleValue(),
							SystemParameterDetails.getGlobaVariableList());
				}

				// Execute the engine
				rule = "function Rule(){"+ rule +"}Rule();";
				BigDecimal tempResult= BigDecimal.ZERO;		
				String result="0";		

				if (engine.eval(rule)!=null) {
					tempResult=new BigDecimal(engine.eval(rule).toString());
					result = tempResult.toString();
				}else{
					if(engine.get("Result")!=null){
						result=engine.get("Result").toString();
						try {
							tempResult=new BigDecimal(result);
							tempResult = tempResult.setScale(2,RoundingMode.UP);
							result = tempResult.toString();
						} catch (Exception e) {
							//do Nothing-- if return type is not a decimal
							result=engine.get("Result").toString();
						}
					}
				}	

				if(result.equals("NaN")){
					result="0";	
				}
				eligibilityRule.setElgAmount(new BigDecimal(result));
				eligibilityRules.add(eligibilityRule);
			}

			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("formatter", formatter);
			map.put("elgRuleList", eligibilityRules);
			Executions.createComponents(
					"/WEB-INF/pages/Enquiry/FinanceInquiry/EligibilityRuleResult.zul",
					this.window_EligibilityCheck, map);

		} catch (Exception e) {
			Messagebox.show(e.toString());
			e.printStackTrace();
		}
		logger.debug("Leaving" + event.toString());
	}


	/**
	 * When user clicks on button "customerId Search" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		doSearchCustomerCIF();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * To load the customerSelect filter dialog
	 * 
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	private void doSearchCustomerCIF() throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents(
				"/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul",
				null, map);
		logger.debug("Leaving");
	}

	/**
	 * To set the customer id from Customer filter
	 * 
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) throws InterruptedException {
		logger.debug("Entering");
		
		this.custCIF.clearErrorMessage();
		
		String oldVarCustCIF = this.custCIF.getValue();
		final Customer aCustomer = (Customer) nCustomer;
		this.custCIF.setValue(aCustomer.getCustCIF());
		this.custID = aCustomer.getCustID();
		this.custShrtName.setValue(aCustomer.getCustShrtName());
		this.custCIFSearchObject = newSearchObject;
		setCustomer(aCustomer);
		
		if(!oldVarCustCIF.equals(this.custCIF.getValue())){
			this.rows_KeyDetails.getChildren().clear();
			this.gb_KeyDetails.setVisible(false);
		}
		
		logger.debug("Leaving ");
	}
	
	/**
	 * when the "refresh" button is clicked. <br>
	 * <br>
	 * Refreshes the view by calling the onCreate event manually.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnRefresh(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Events.postEvent("onCreate", this.window_EligibilityCheck, event);
		this.window_EligibilityCheck.invalidate();
		this.finType.setDescription("");
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public FinanceType getFinanceType() {
		return financeType;
	}
	public void setFinanceType(FinanceType financeType) {
		this.financeType = financeType;
	}

	public void setFinanceReferenceDetailService(
			FinanceReferenceDetailService financeReferenceDetailService) {
		this.financeReferenceDetailService = financeReferenceDetailService;
	}
	public FinanceReferenceDetailService getFinanceReferenceDetailService() {
		return financeReferenceDetailService;
	}

	public List<FinanceReferenceDetail> getElgRuleList() {
		return elgRuleList;
	}
	public void setElgRuleList(List<FinanceReferenceDetail> elgRuleList) {
		this.elgRuleList = elgRuleList;
	}

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}
	public RuleService getRuleService() {
		return ruleService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}
	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}
	
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}
	public RuleExecutionUtil getRuleExecutionUtil() {
		return ruleExecutionUtil;
	}

}
