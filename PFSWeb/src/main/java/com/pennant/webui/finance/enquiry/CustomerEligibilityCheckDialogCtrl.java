/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : CustomerEligibilityCheckDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 12-11-2011 * * Modified Date : 12-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.enquiry;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.BMTRBFldDetails;
import com.pennant.backend.model.rulefactory.RuleResult;
import com.pennant.backend.service.finance.EligibilityRule;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.lmtmasters.FinanceReferenceDetailService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.constants.FinServiceEvent;

/**
 * This is the controller class for the /WEB-INF/pages/Enquiry/FinanceInquiry/EligibilityCheck.zul file.
 */
public class CustomerEligibilityCheckDialogCtrl extends GFCBaseCtrl<CustomerEligibilityCheck> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = LogManager.getLogger(CustomerEligibilityCheckDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_EligibilityCheck; // autoWired

	protected Borderlayout borderlayoutEligibilityCheck; // autoWired
	protected Groupbox gb_BasicDetails; // autoWired
	protected Groupbox gb_KeyDetails; // autoWired

	// Basic Details
	protected ExtendedCombobox finType; // autoWired
	protected Textbox custCIF; // autoWired
	protected Label custShrtName; // autoWired
	protected Rows rows_KeyDetails; // autoWired

	protected JdbcSearchObject<Customer> custCIFSearchObject;
	protected FinanceType financeType;
	protected Customer customer;

	private FinanceReferenceDetailService financeReferenceDetailService;
	private RuleService ruleService;
	private FinanceDetailService financeDetailService;
	protected List<FinanceReferenceDetail> elgRuleList;
	protected List<String> feildList = null;
	protected Map<String, BMTRBFldDetails> fldDetailsMap = null;
	private boolean fldsFetched = false;
	private List<String> resultList = null;

	// not auto wired variables
	private Textbox textbox;
	private Intbox intbox;
	private Datebox datebox;
	private Decimalbox decimalbox;
	private Checkbox checkbox;

	private int formatter = SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT);
	private Textbox elgModule;
	private long custID;
	private String oldVar_FinType = "";

	/**
	 * default constructor.<br>
	 */
	public CustomerEligibilityCheckDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_EligibilityCheck(ForwardEvent event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_EligibilityCheck);

		this.custCIF.setValue("");
		this.finType.setValue("", "");
		this.finType.setTextBoxWidth(161);
		this.custShrtName.setValue("");

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

	/**
	 * onChange get the customer Details
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onChange$custCIF(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		this.custCIF.clearErrorMessage();

		Customer customer = (Customer) PennantAppUtil.getCustomerObject(this.custCIF.getValue(), null);

		if (customer == null) {
			this.custID = Long.valueOf(0);
			this.custShrtName.setValue("");
			if (this.rows_KeyDetails.getChildren() != null) {
				this.rows_KeyDetails.getChildren().clear();
				this.gb_KeyDetails.setVisible(false);
			}
			throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID",
					new String[] { Labels.getLabel("label_EligibilityCheck_CustCIF.value") }));
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
		Clients.clearWrongValue(this.finType);

		this.finType.setConstraint("");
		this.finType.setErrorMessage("");
		Clients.clearWrongValue(finType);

		Object dataObject = this.finType.getObject();
		if (dataObject instanceof String) {
			this.finType.setValue(dataObject.toString());
			setFinanceType(null);
		} else {
			FinanceType details = (FinanceType) dataObject;
			if (details != null) {
				this.finType.setValue(details.getFinType());
				setFinanceType(details);
			}
		}

		if (!oldVar_FinType.equals(this.finType.getValue())) {
			this.rows_KeyDetails.getChildren().clear();
			this.gb_KeyDetails.setVisible(false);
		}
		oldVar_FinType = StringUtils.trimToEmpty(this.finType.getValue());
		logger.debug("Leaving " + event.toString());
	}

	public void onClick$btnGo(Event event) {
		logger.debug("Entering" + event.toString());

		if ("FINANCE".equals(this.elgModule.getValue())) {
			if (StringUtils.isBlank(this.finType.getValue())) {
				throw new WrongValueException(this.finType, Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_EligibilityCheck_FinType.value") }));
			} else {
				Clients.clearWrongValue(finType);
			}
		} else if ("CUSTOMER".equals(this.elgModule.getValue())) {
			if (this.custID == 0) {
				throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_EligibilityCheck_CustCIF.value") }));
			}
		}

		this.rows_KeyDetails.getChildren().clear();
		this.gb_KeyDetails.setVisible(false);

		setElgRuleList(getFinanceReferenceDetailService().getFinRefDetByRoleAndFinType(this.finType.getValue(),
				FinServiceEvent.ORG, null, "_AEView"));

		if (getElgRuleList() != null && getElgRuleList().size() > 0) {
			if (!fldsFetched) {
				List<BMTRBFldDetails> list = getRuleService().getFieldList(RuleConstants.MODULE_ELGRULE,
						RuleConstants.EVENT_ELGRULE);
				for (BMTRBFldDetails detail : list) {
					if (!fldDetailsMap.containsKey(detail.getRbFldName().trim())) {
						fldDetailsMap.put(detail.getRbFldName().trim(), detail);
					}
				}

				feildList.addAll(fldDetailsMap.keySet());
				fldsFetched = true;
			}
		} else {
			String msg = Labels.getLabel("label_NOEligibility_Rules_Defined");
			Clients.showNotification(msg, "warning", null, null, -1);
		}

		resultList = new ArrayList<String>();
		for (FinanceReferenceDetail referenceDetail : getElgRuleList()) {

			String[] strings = (referenceDetail.getLovDescElgRuleValue()).split("[\\s\\(\\)\\+\\>\\<\\=\\-\\/\\*\\;]");
			for (int i = 0; i < strings.length; i++) {
				if (feildList.contains(strings[i].trim()) && !resultList.contains(strings[i].trim())) {
					resultList.add(strings[i].trim());
				}
			}
		}

		// Design Feild Details List on Screen with Filled Data or Without
		Label label;
		Row row = null;
		CustomerEligibilityCheck custElgCheck = setCustomerEligibilityData(getCustomer());
		for (int i = 0; i < resultList.size(); i++) {

			if (fldDetailsMap.containsKey(resultList.get(i)) && !resultList.get(i).startsWith("custPD")) {

				String fieldValue = "";
				BMTRBFldDetails details = (BMTRBFldDetails) fldDetailsMap.get(resultList.get(i));

				String fieldName = String.valueOf(details.getRbFldName().charAt(0)).toUpperCase()
						+ details.getRbFldName().substring(1);

				if (i % 2 == 0) {
					row = new Row();
				}
				label = new Label(details.getRbFldDesc());
				row.appendChild(label);

				if ("nvarchar".equalsIgnoreCase(details.getRbFldType())) {
					textbox = new Textbox();
					textbox.setId(details.getRbFldName());
					if (details.getRbFldLen() < 5) {
						textbox.setWidth("60px");
					} else {
						textbox.setWidth((details.getRbFldLen() * 12) + "px");
					}
					textbox.setMaxlength(details.getRbFldLen());

					try {
						if ("CustCtgType".equalsIgnoreCase(fieldName)) {
							fieldValue = getCustomer().getLovDescCustCtgType();
						} else {
							fieldValue = getCustomer().getClass().getMethod("get" + fieldName).invoke(getCustomer())
									.toString();
						}
						textbox.setValue(fieldValue);
					} catch (Exception e) {
						textbox.setValue(fieldValue);
					}
					row.appendChild(textbox);
				} else if ("bigint".equalsIgnoreCase(details.getRbFldType())) {
					intbox = new Intbox();
					intbox.setId(details.getRbFldName());
					intbox.setWidth((details.getRbFldLen() * 12) + "px");
					intbox.setMaxlength(details.getRbFldLen());

					try {
						fieldValue = custElgCheck.getClass().getMethod("get" + fieldName).invoke(custElgCheck)
								.toString();
						intbox.setValue(Integer.parseInt(fieldValue));
					} catch (Exception e) {

						if ("CustAge".equalsIgnoreCase(fieldName) && customer != null) {
							intbox.setValue(
									DateUtility.getYearsBetween(customer.getCustDOB(), DateUtility.getSysDate()));
						} else {
							intbox.setValue(0);
						}
					}
					row.appendChild(intbox);
				} else if ("decimal".equalsIgnoreCase(details.getRbFldType())) {
					decimalbox = new Decimalbox();
					decimalbox.setId(details.getRbFldName());
					decimalbox.setWidth((details.getRbFldLen() * 12) + "px");
					decimalbox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
					decimalbox.setMaxlength(details.getRbFldLen() + 2);

					try {

						fieldValue = custElgCheck.getClass().getMethod("get" + fieldName).invoke(custElgCheck)
								.toString();
						if (fieldName.equalsIgnoreCase("CustAge")) {
							decimalbox.setValue(new BigDecimal(fieldValue));
						} else {
							decimalbox.setValue(CurrencyUtil.parse(new BigDecimal(fieldValue), formatter));
						}
					} catch (Exception e) {
						decimalbox.setValue(BigDecimal.ZERO);
					}
					row.appendChild(decimalbox);
				} else if ("smalldatetime".equalsIgnoreCase(details.getRbFldType())) {
					datebox = new Datebox();
					datebox.setId(details.getRbFldName());
					datebox.setWidth("100px");
					datebox.setFormat(DateFormat.SHORT_DATE.getPattern());

					try {
						fieldValue = custElgCheck.getClass().getMethod("get" + fieldName).invoke(custElgCheck)
								.toString();
						datebox.setText(fieldValue);
					} catch (Exception e) {
						datebox.setText("");
					}

					row.appendChild(datebox);
				} else if ("nchar".equalsIgnoreCase(details.getRbFldType())) {
					checkbox = new Checkbox();
					checkbox.setId(details.getRbFldName());

					try {
						fieldValue = custElgCheck.getClass().getMethod("is" + fieldName).invoke(custElgCheck)
								.toString();
						checkbox.setChecked(Boolean.parseBoolean(fieldValue));
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

		// Customer Eligibility Amounts Calculation
		String currency = "";
		String productCode = "";
		if ("FINANCE".equals(this.elgModule.getValue()) && getFinanceType() != null) {
			currency = getFinanceType().getFinCcy();
			productCode = getFinanceType().getFinCategory();
		} else if ("CUSTOMER".equals(this.elgModule.getValue()) && customer != null) {
			currency = customer.getCustBaseCcy();
		}

		CustomerEligibilityCheck custElgCheck = getFinanceDetailService().getCustEligibilityDetail(customer,
				productCode, null, currency, BigDecimal.ZERO, 0, null, null);

		custElgCheck.setReqFinAmount(BigDecimal.ZERO);
		if ("FINANCE".equals(this.elgModule.getValue()) && StringUtils.isNotBlank(this.finType.getValue())) {
			custElgCheck.setReqProduct(getFinanceType().getFinCategory());
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

			Map<String, Object> fieldsAndValues = new HashMap<>();

			// Setting Data
			CustomerEligibilityCheck custElgCheck = setCustomerEligibilityData(getCustomer());
			Map<String, Object> fieldsandvalues = custElgCheck.getDeclaredFieldValues();

			ArrayList<String> keyset = new ArrayList<String>(fieldsandvalues.keySet());
			for (int i = 0; i < keyset.size(); i++) {
				Object var = fieldsandvalues.get(keyset.get(i));
				if (var instanceof String) {
					var = var.toString().trim();
				}
				fieldsAndValues.put(keyset.get(i), var);
			}

			// evaluate JavaScript code from String

			for (int i = 0; i < resultList.size(); i++) {
				if (rows_KeyDetails.getFellowIfAny(resultList.get(i)) instanceof Textbox) {
					textbox = (Textbox) rows_KeyDetails.getFellowIfAny(resultList.get(i));
					// bindings to the engine
					fieldsAndValues.put(textbox.getId().trim(), StringUtils.trimToEmpty(textbox.getValue()));
				} else if (rows_KeyDetails.getFellowIfAny(resultList.get(i)) instanceof Intbox) {
					intbox = (Intbox) rows_KeyDetails.getFellowIfAny(resultList.get(i));
					// bindings to the engine
					fieldsAndValues.put(intbox.getId().trim(), intbox.intValue());
				} else if (rows_KeyDetails.getFellowIfAny(resultList.get(i)) instanceof Decimalbox) {
					decimalbox = (Decimalbox) rows_KeyDetails.getFellowIfAny(resultList.get(i));
					// bindings to the engine
					if ("reqPftRate".equals(decimalbox.getId().trim()) || "tenure".equals(decimalbox.getId().trim())
							|| "DSCR".equals(decimalbox.getId().trim())) {
						fieldsAndValues.put(decimalbox.getId().trim(),
								decimalbox.getValue() == null ? BigDecimal.ZERO : decimalbox.getValue());
					} else {
						fieldsAndValues.put(decimalbox.getId().trim(), decimalbox.getValue() == null ? BigDecimal.ZERO
								: CurrencyUtil.unFormat(decimalbox.getValue(), formatter));
					}
				} else if (rows_KeyDetails.getFellowIfAny(resultList.get(i)) instanceof Datebox) {
					datebox = (Datebox) rows_KeyDetails.getFellowIfAny(resultList.get(i));
					// bindings to the engine
					fieldsAndValues.put(datebox.getId().trim(), datebox.getValue());
				} else if (rows_KeyDetails.getFellowIfAny(resultList.get(i)) instanceof Checkbox) {
					checkbox = (Checkbox) rows_KeyDetails.getFellowIfAny(resultList.get(i));
					// bindings to the engine
					fieldsAndValues.put(checkbox.getId().trim(), checkbox.isChecked());
				}
			}

			for (FinanceReferenceDetail detail : getElgRuleList()) {
				// Rule List Execution
				if ("CUSTOMER".equals(this.elgModule.getValue()) &&

						StringUtils.isBlank(this.finType.getValue())) {
					fieldsAndValues.put("reqFinType", detail.getFinType());
					fieldsAndValues.put("reqFinCcy", detail.getLovDescFinCcyCode());
					fieldsAndValues.put("reqProduct", detail.getLovDescProductCodeName());
					fieldsAndValues.put("reqCampaign", "");
					fieldsAndValues.put("reqFinAmount", 10000000);

				}

				eligibilityRule = new EligibilityRule();
				eligibilityRule.setRuleCode(detail.getLovDescCodelov());
				eligibilityRule.setRuleCodeDesc(detail.getLovDescNamelov());
				eligibilityRule.setFinType(detail.getFinType());
				eligibilityRule.setFinTypeDesc(detail.getLovDescFinTypeDescName());
				eligibilityRule.setRuleReturnType(detail.getLovDescRuleReturnType());

				// Currency Conversions if Courrency Constants Exists in Rule
				String rule = detail.getLovDescElgRuleValue();
				rule = RuleExecutionUtil.replaceCurrencyCode(rule, null);

				String returnType = detail.getLovDescRuleReturnType();
				RuleReturnType ruleReturnType = null;

				if (StringUtils.equals(returnType, RuleReturnType.BOOLEAN.value())) {
					ruleReturnType = RuleReturnType.BOOLEAN;
				} else if (StringUtils.equals(returnType, RuleReturnType.DECIMAL.value())) {
					ruleReturnType = RuleReturnType.DECIMAL;
				} else if (StringUtils.equals(returnType, RuleReturnType.STRING.value())) {
					ruleReturnType = RuleReturnType.STRING;
				} else if (StringUtils.equals(returnType, RuleReturnType.INTEGER.value())) {
					ruleReturnType = RuleReturnType.INTEGER;
				} else if (StringUtils.equals(returnType, RuleReturnType.OBJECT.value())) {
					ruleReturnType = RuleReturnType.OBJECT;
				}

				// Execute the engine
				Object object = RuleExecutionUtil.executeRule(rule, fieldsAndValues, null, ruleReturnType);

				String resultValue = null;
				switch (ruleReturnType) {
				case DECIMAL:
				case INTEGER:
					eligibilityRule.setElgAmount(new BigDecimal(object.toString()));
					break;

				case BOOLEAN:
					boolean tempBoolean = (boolean) object;
					if (tempBoolean) {
						resultValue = "1";
					} else {
						resultValue = "0";
					}
					eligibilityRule.setElgAmount(new BigDecimal(resultValue));
					break;

				case OBJECT:
					RuleResult ruleResult = (RuleResult) object;
					Object resultval = ruleResult.getValue();

					eligibilityRule.setElgAmount(new BigDecimal(resultval.toString()));
					break;

				default:
					// do-nothing
					break;
				}

				eligibilityRules.add(eligibilityRule);
			}

			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("formatter", formatter);
			map.put("elgRuleList", eligibilityRules);
			Executions.createComponents("/WEB-INF/pages/Enquiry/FinanceInquiry/EligibilityRuleResult.zul",
					this.window_EligibilityCheck, map);

		} catch (WrongValueException e) {
			logger.error("Exception: ", e);
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
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug("Leaving");
	}

	/**
	 * To set the customer id from Customer filter
	 * 
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
		logger.debug("Entering");

		this.custCIF.clearErrorMessage();

		String oldVarCustCIF = this.custCIF.getValue();
		final Customer aCustomer = (Customer) nCustomer;
		Customer customer = (Customer) PennantAppUtil.getCustomerObject(aCustomer.getCustCIF(), null);
		this.custCIF.setValue(customer.getCustCIF());
		this.custID = customer.getCustID();
		this.custShrtName.setValue(customer.getCustShrtName());
		this.custCIFSearchObject = newSearchObject;
		setCustomer(customer);

		if (!oldVarCustCIF.equals(this.custCIF.getValue())) {
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

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceType getFinanceType() {
		return financeType;
	}

	public void setFinanceType(FinanceType financeType) {
		this.financeType = financeType;
	}

	public void setFinanceReferenceDetailService(FinanceReferenceDetailService financeReferenceDetailService) {
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

}
