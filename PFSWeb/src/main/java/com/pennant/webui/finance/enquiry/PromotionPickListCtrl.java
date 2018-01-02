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
 * FileName    		:  PromotionPickListCtrl.java                                                   * 	  
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
import com.pennant.backend.model.customermasters.CustEmployeeDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.CustomerType;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.BMTRBFldDetails;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.Gender;
import com.pennant.backend.model.systemmasters.MaritalStatusCode;
import com.pennant.backend.model.systemmasters.Salutation;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.applicationmaster.BranchService;
import com.pennant.backend.service.applicationmaster.RelationshipOfficerService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceEligibility;
import com.pennant.backend.service.rmtmasters.CustomerTypeService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the 
 * /WEB-INF/pages/Enquiry/FinanceInquiry/EligibilityCheck.zul file.
 */
public class PromotionPickListCtrl extends GFCBaseCtrl<CustomerEligibilityCheck> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = Logger.getLogger(PromotionPickListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_PromotionPickList; 		

	protected Borderlayout	borderlayoutEligibilityCheck;	
	protected Groupbox 		gb_BasicDetails; 				

	//Basic Details
	protected Textbox 		customerCIF; 					
	protected Label 		customerName; 					
	protected Hbox hbox_custSelection;					   
	protected Label label_CustCIF;					       


	protected JdbcSearchObject<Customer> custCIFSearchObject;
	protected Customer customer;

	private RuleService ruleService;
	private FinanceDetailService financeDetailService;
	private RuleExecutionUtil ruleExecutionUtil;

	protected List<String> feildList = null;
	protected Map<String , BMTRBFldDetails> fldDetailsMap = null;

	protected Groupbox 			gb_Cusotmer;
	protected Intbox  installment;
	protected CurrencyBox  finAmount;
	protected Radio		prospect;
	protected Radio		existing;

	private int formatter = SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT);
	private transient CustomerDialogCtrl customerDialogCtrl = null;
	private CustomerDetailsService customerDetailsService;
	private RelationshipOfficerService relationshipOfficerService;
	private BranchService branchService;
	private CustomerTypeService customerTypeService;
	private CustomerDetails prospectCustomerDetails;
	private FinanceDetail financeDetail;
	private Tabpanel tabpanel;
	protected Component 	childWindow = null;
	/**
	 * default constructor.<br>
	 */
	public PromotionPickListCtrl() {
		super();
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, 
	 * if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_PromotionPickList(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_PromotionPickList);
		
		tabpanel = (Tabpanel) window_PromotionPickList.getParent();
		tabpanel.setVisible(false);
		
		this.customerCIF.setValue("");
		this.customerName.setValue("");

		fldDetailsMap = new HashMap<String, BMTRBFldDetails>();
		feildList = new ArrayList<String>();
		doSetFieldProperties();
		appendCustomerDetailTab();
		
/*		this.borderlayoutEligibilityCheck.setHeight(getBorderLayoutHeight()+100);
		menuWest = borderlayout.getWest();
		groupboxMenu = (Groupbox) borderlayout.getFellowIfAny("groupbox_menu");
		menuWest.setVisible(false);
		groupboxMenu.setVisible(false);
		this.window_PromotionPickList.setParent(groupboxMenu.getParent());*/
		setDialog(DialogType.EMBEDDED);
		
		logger.debug("Leaving " + event.toString());
	}


	public void doSetFieldProperties(){
		logger.debug("Entering");
		this.finAmount.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.finAmount.setScale(formatter);
		this.customerCIF.setMaxlength(6);
		logger.debug("Leaving");
	}
	
	/**
	 * Creates a page from a zul-file in a tab in the center area of the
	 * borderlayout.
	 * 
	 * @throws InterruptedException
	 */
	public void appendCustomerDetailTab() throws InterruptedException {
		logger.debug("Entering");
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("roleCode", getRole());
			map.put("promotionPickListCtrl", this);
			setFinanceDetail(new FinanceDetail());
			FinanceMain financeMain = new FinanceMain();
			getFinanceDetail().getFinScheduleData().setFinanceMain(financeMain);
			setProspectCustomerDetails(getNewCustomerDetail());
			getFinanceDetail().setCustomerDetails(getProspectCustomerDetails());
			map.put("financedetail", getFinanceDetail());
			String zulFilePathName = "/WEB-INF/pages/CustomerMasters/Customer/CustomerDialog.zul";
			childWindow = Executions.createComponents(zulFilePathName, gb_Cusotmer, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * onChange get the customer Details
	 * @param event
	 * @throws InterruptedException
	 */
	public void onChange$customerCIF(Event event) throws InterruptedException{
		logger.debug("Entering" + event.toString());

		this.customerCIF.clearErrorMessage();
		Customer customer = (Customer)PennantAppUtil.getCustomerObject(this.customerCIF.getValue(), null);

		if(customer == null) {	
			this.customerName.setValue("");
			throw new WrongValueException(this.customerCIF, Labels.getLabel("FIELD_NO_INVALID", new String[] { Labels.getLabel("label_EligibilityCheck_CustCIF.value") }));
		} else {
			doSetCustomer(customer, null);
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
		BigDecimal financeAmount = PennantApplicationUtil.unFormateAmount(this.finAmount.getActualValue(), formatter);
		
		BigDecimal curFinAmount = BigDecimal.ZERO;
		if(this.installment.intValue() != 0 && financeAmount.compareTo(BigDecimal.ZERO) > 0){
			curFinAmount = financeAmount.divide(new BigDecimal(this.installment.intValue()), 0, RoundingMode.HALF_DOWN);
		}

		CustomerEligibilityCheck custElgCheck = getFinanceDetailService().getCustEligibilityDetail(customer,"", null,
				customer.getCustBaseCcy(),curFinAmount, this.installment.intValue(), BigDecimal.ONE, null);
		
		custElgCheck.setReqFinAmount(financeAmount);
		custElgCheck.setCustEmpDesg(getFinanceDetail().getCustomerDetails().getCustEmployeeDetail().getEmpDesg());
		custElgCheck.setCustEmpSector(getFinanceDetail().getCustomerDetails().getCustEmployeeDetail().getEmpSector());
		custElgCheck.setCustEmpAloc(getFinanceDetail().getCustomerDetails().getCustEmployeeDetail().getEmpAlocType());

		logger.debug("Leaving");
		return custElgCheck;
	}

	/**
	 * On click event for stimulate button
	 * @throws ParseException 
	 */
	public void onClick$btnStimulate(Event event) throws InterruptedException, ScriptException, ParseException {
		logger.debug("Entering" + event.toString());

		if(this.existing.isChecked()){
			if( StringUtils.isBlank(this.customerCIF.getValue())){
				throw new WrongValueException(this.customerCIF, Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_EligibilityCheck_CustCIF.value") }));
			}else{
				Customer customer = (Customer)PennantAppUtil.getCustomerObject(this.customerCIF.getValue(), null);
				if(customer == null) {	
					this.customerName.setValue("");
					throw new WrongValueException(this.customerCIF, Labels.getLabel("FIELD_NO_INVALID", new String[] { Labels.getLabel("label_EligibilityCheck_CustCIF.value") }));
				}
			}
		}
		
		// Customer Details Tab ---> Customer Details 
		if (getCustomerDialogCtrl() != null){
			if(getCustomerDialogCtrl().getCustomerDetails() != null) {
				getCustomerDialogCtrl().doSave_CustomerDetail(getFinanceDetail(),false);
			}
		}

		String promotionElgRule = SysParamUtil.getValueAsString("PROMOTION_ELGRULE");
		
		Rule rule = getRuleService().getApprovedRuleById(promotionElgRule,RuleConstants.MODULE_ELGRULE, RuleConstants.EVENT_ELGRULE);
		
		if(rule == null){
			MessageUtil.showError(Labels.getLabel("PromotionPick_ELGRULE_NotDefined"));
			return;
		}
		
		try {
			//Setting Data
			CustomerEligibilityCheck custElgCheck = setCustomerEligibilityData(getFinanceDetail().getCustomerDetails().getCustomer());
			HashMap<String, Object> fieldsandvalues = custElgCheck.getDeclaredFieldValues();

			// Fetching Finance Type Details
			List<FinanceType> promotionDetailList = getPromotionDetails();
		
			if(promotionDetailList == null || promotionDetailList.isEmpty()){
				MessageUtil.showMessage(Labels.getLabel("PromotionPick_Promotions_NotDefined"));
			}else{
				List<FinanceEligibility> finElgRuleDetailList = new ArrayList<FinanceEligibility>();
				FinanceEligibility financeEligibility = null;
				for (FinanceType detail : promotionDetailList) {

					//Rule List Execution
					fieldsandvalues.put("reqFinType", detail.getFinType());
					fieldsandvalues.put("reqProduct", detail.getFinCategory());
					fieldsandvalues.put("reqCampaign", "");
					fieldsandvalues.put("finProfitRate", detail.getFinIntRate());
					fieldsandvalues.put("tenure", detail.getFinDftTerms());
					fieldsandvalues.put("noOfTerms", detail.getFinDftTerms());

					fieldsandvalues.put("stepFinance", detail.isStepFinance());
					fieldsandvalues.put("alwDPSP", detail.isAllowDownpayPgm());
					fieldsandvalues.put("alwPlannedDefer", detail.isAlwPlanDeferment());

					//Currency Conversions if Currency Constants Exists in Rule 
					String sqlRule = rule.getSQLRule();
					sqlRule = getRuleExecutionUtil().replaceCurrencyCode(sqlRule, null);
					
					String returnType = rule.getReturnType();
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

					//Rule Execution Process
					Object object = getRuleExecutionUtil().executeRule(sqlRule, fieldsandvalues, null, ruleReturnType);

					financeEligibility = new FinanceEligibility();
					financeEligibility.setCustCIF(this.customerCIF.getValue());
					financeEligibility.setProduct(detail.getProduct());
					financeEligibility.setProductDesc(detail.getLovDescPromoFinTypeDesc());
					financeEligibility.setPromotionCode(detail.getFinType());
					financeEligibility.setPromotionDesc(detail.getFinTypeDesc());
					financeEligibility.setFinCategory(detail.getFinCategory());
					financeEligibility.setFinAssetType(detail.getFinAssetType());
					financeEligibility.setRepayProfitRate(detail.getFinIntRate().compareTo(BigDecimal.ZERO)==0?BigDecimal.ZERO:detail.getFinIntRate());
					financeEligibility.setNumberOfTerms(detail.getFinDftTerms());
					financeEligibility.setProductFeature(detail.getRemarks());
					financeEligibility.setElgAmount(new BigDecimal(object.toString()));
					finElgRuleDetailList.add(financeEligibility);
				}

				FinanceMain main = getFinanceDetail().getFinScheduleData().getFinanceMain();
				main.setNumberOfTerms(this.installment.intValue());
				main.setFinAmount(PennantAppUtil.unFormateAmount(this.finAmount.getActualValue(), formatter));

				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("formatter", formatter);
				map.put("finElgRuleDetailList", finElgRuleDetailList);
				map.put("promotionPickListCtrl",this);
				map.put("financeDetail",getFinanceDetail());
				Executions.createComponents(
						"/WEB-INF/pages/Enquiry/FinanceInquiry/FinanceEligibilityRuleResult.zul",
						this.window_PromotionPickList, map);

			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	private List<FinanceType> getPromotionDetails(){
		logger.debug("Entering");
		
	    //Fetch Workflow Defined Promotion Types
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<FinanceType> finWorflowSearchObject = new JdbcSearchObject<FinanceType>(FinanceType.class);
		finWorflowSearchObject.addTabelName("LMTFinanceWorkFlowDef");
		finWorflowSearchObject.addFilter(Filter.equalTo("ModuleName","PROMOTION"));
		finWorflowSearchObject.addField("FinType");
		List<FinanceType> finTypeList = pagedListService.getBySearchObject(finWorflowSearchObject);
		if(finTypeList == null || finTypeList.isEmpty()){
			return finTypeList;
		}
		List<String> finTypes = new ArrayList<String>();
		for (FinanceType financeType : finTypeList) {
			finTypes.add(financeType.getFinType());
		}
		
		//Fetch Promotion Type Details
		JdbcSearchObject<FinanceType> jdbcSearchObject = new JdbcSearchObject<FinanceType>(FinanceType.class);
		jdbcSearchObject.addTabelName("RMTFinanceTypes_AView");
		jdbcSearchObject.addFilter(Filter.equalTo("FinIsActive",1));
		jdbcSearchObject.addFilter(Filter.notEqual("Product", ""));
		jdbcSearchObject.addFilter(Filter.in("FinType", finTypes));
		
		//Field Details
		jdbcSearchObject.addField("Product");
		jdbcSearchObject.addField("LovDescPromoFinTypeDesc");
		jdbcSearchObject.addField("FinType");
		jdbcSearchObject.addField("FinTypeDesc");
		jdbcSearchObject.addField("FinCategory");
		jdbcSearchObject.addField("FinCcy");
		jdbcSearchObject.addField("FinDftTerms");
		jdbcSearchObject.addField("FinIntRate");
		jdbcSearchObject.addField("Remarks");
		jdbcSearchObject.addField("AlwPlanDeferment");
		jdbcSearchObject.addField("StepFinance");
		jdbcSearchObject.addField("AllowDownpayPgm");
		jdbcSearchObject.addField("FinAssetType");
		
		
		List<FinanceType> returnFintypes = pagedListService.getBySearchObject(jdbcSearchObject);
		logger.debug("Leaving");
		return returnFintypes;
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


	private CustomerDetails getNewCustomerDetail(){

		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setNewRecord(true);
		Customer customer = new Customer();

		customer.setLovDescCustCtgType(PennantConstants.PFF_CUSTCTG_INDIV);
		customer.setCustCtgCode(PennantConstants.PFF_CUSTCTG_INDIV);
		customer.setLovDescCustCtgCodeName(PennantConstants.PFF_CUSTCTG_INDIV);
		customer.setCustCIF(getCustomerDetailsService().getNewProspectCustomerCIF());
		customer.setCustCRCPR("");
		customer.setCustBaseCcy(SysParamUtil.getValueAsString("APP_DFT_CURR"));
		customer.setCustLng(SysParamUtil.getValueAsString("APP_LNG"));
	
		Filter[] countrysystemDefault=new Filter[1];
		countrysystemDefault[0]=new Filter("SystemDefault", "1",Filter.OP_EQUAL);
		Object countryObj=	PennantAppUtil.getSystemDefault("Country","", countrysystemDefault);
		
		if (countryObj!=null) {
			Country country=(Country) countryObj;
			customer.setCustCOB(country.getCountryCode());
			customer.setCustParentCountry(country.getCountryCode());
			customer.setCustResdCountry(country.getCountryCode());
			customer.setCustRiskCountry(country.getCountryCode());
			customer.setCustNationality(country.getCountryCode());
			
			customer.setLovDescCustCOBName(country.getCountryDesc());
			customer.setLovDescCustParentCountryName(country.getCountryDesc());
			customer.setLovDescCustResdCountryName(country.getCountryDesc());
			customer.setLovDescCustRiskCountryName(country.getCountryDesc());
			customer.setLovDescCustNationalityName(country.getCountryDesc());
		}
		

		//Setting Primary Relation Ship Officer
		RelationshipOfficer officer = getRelationshipOfficerService().getApprovedRelationshipOfficerById(getUserWorkspace().getLoggedInUser().getUserName());
		if(officer != null){
			customer.setCustRO1(Long.parseLong(officer.getROfficerCode()));
			customer.setLovDescCustRO1Name(officer.getROfficerDesc());
		}

		//Setting User Branch to Customer Branch
		Branch branch = getBranchService().getApprovedBranchById(getUserWorkspace().getLoggedInUser().getBranchCode());
		if(branch != null){
			customer.setCustDftBranch(branch.getBranchCode());
			customer.setLovDescCustDftBranchName(branch.getBranchDesc());
		}

		//Setting User Branch to Customer Branch
		CustomerType customerType = getCustomerTypeService().getApprovedCustomerTypeById(PennantConstants.DEFAULT_CUST_TYPE);
		if(customerType != null){
			customer.setCustTypeCode(customerType.getCustTypeCode());
			customer.setLovDescCustTypeCodeName(customerType.getCustTypeDesc());
		}

		CustEmployeeDetail detail = new CustEmployeeDetail();
		detail.setNewRecord(true);
		customerDetails.setCustEmployeeDetail(detail);

		Filter[] systemDefault=new Filter[1];
		systemDefault[0]=new Filter("SystemDefault", "1",Filter.OP_EQUAL);
		Object genderObj=	PennantAppUtil.getSystemDefault("Gender","", systemDefault);
		if (genderObj !=null) {
			Gender gender=(Gender) genderObj;
			Filter[] saltufilters=new Filter[2];
			saltufilters[0]=new Filter("SalutationGenderCode", gender.getGenderCode(),Filter.OP_EQUAL);
			saltufilters[1]=new Filter("SystemDefault", "1",Filter.OP_EQUAL);
			Object saltuObj=PennantAppUtil.getSystemDefault("Salutation","", saltufilters);
			
			customer.setCustGenderCode(gender.getGenderCode());
			
			if (saltuObj!=null) {
				Salutation salutation=(Salutation) saltuObj;
				customer.setCustSalutationCode(salutation.getSalutationCode());
			}
		}
		customer.setSalariedCustomer(true);
		
		Object salutionObj=	PennantAppUtil.getSystemDefault("MaritalStatusCode","", systemDefault);
		if (salutionObj!=null) {
			MaritalStatusCode maritalStatusCode=(MaritalStatusCode) salutionObj;
			customer.setCustMaritalSts(maritalStatusCode.getMaritalStsCode());
		}
		
		
		customerDetails.setCustomer(customer);
		return customerDetails;

	}

	/**
	 * To set the customer id from Customer filter
	 * 
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) throws InterruptedException {
		logger.debug("Entering");

		this.customerCIF.clearErrorMessage();
		final Customer aCustomer = (Customer) nCustomer;
		this.customerCIF.setValue(aCustomer.getCustCIF());
		this.customerName.setValue(aCustomer.getCustShrtName());
		this.custCIFSearchObject = newSearchObject;
		setCustomer(aCustomer);

		final CustomerDetails customerDetails = getCustomerDetailsService().getCustomerById(aCustomer.getId());
		if(customerDetails != null){
			customerDetails.setNewRecord(this.prospect.isChecked());
			getFinanceDetail().setCustomerDetails(customerDetails);
		}
		if(getCustomerDialogCtrl() != null){
			customerDetails.setNewRecord(this.prospect.isChecked());
			getCustomerDialogCtrl().doSetCustomerData(customerDetails);
		}
		logger.debug("Leaving ");
	}
	
	@Override
	public void closeDialog() {
		super.closeDialog();

		if (childWindow != null) {
			((Window) childWindow).onClose();
			deAllocateAuthorities("CustomerDialog");
		}

		Tabpanels tabpanels = (Tabpanels) tabpanel.getParent();
		Tabbox tabbox = (Tabbox) tabpanels.getParent();
		if (tabbox.getFellowIfAny("tab_PromotionPick") != null) {
			Tab tab = (Tab) tabbox.getFellowIfAny("tab_PromotionPick");
			tab.close();
		}
	}
	
	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		if(doClose(false)) {
			
		}
	}

	public void onCheck$existing(Event event){
		logger.debug("Entering");
		this.hbox_custSelection.setVisible(true);
		this.label_CustCIF.setVisible(true);
		
		if(getCustomerDialogCtrl() != null){
			getCustomerDialogCtrl().doClearErrorMessage();
		}
		logger.debug("Leaving");
	}

	public void onCheck$prospect(Event event){
		logger.debug("Entering");
		this.hbox_custSelection.setVisible(false);
		this.label_CustCIF.setVisible(false);
		this.customerCIF.setValue("");
		this.customerName.setValue("");
		if(getCustomerDialogCtrl() != null){
			getCustomerDialogCtrl().doSetCustomerData(getProspectCustomerDetails());
		}
		logger.debug("Leaving");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public RuleService getRuleService() {
		return ruleService;
	}
	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
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

	public CustomerDialogCtrl getCustomerDialogCtrl() {
		return customerDialogCtrl;
	}
	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		this.customerDialogCtrl = customerDialogCtrl;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}
	public void setCustomerDetailsService(
			CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public RelationshipOfficerService getRelationshipOfficerService() {
		return relationshipOfficerService;
	}
	public void setRelationshipOfficerService(
			RelationshipOfficerService relationshipOfficerService) {
		this.relationshipOfficerService = relationshipOfficerService;
	}

	public BranchService getBranchService() {
		return branchService;
	}
	public void setBranchService(BranchService branchService) {
		this.branchService = branchService;
	}

	public CustomerTypeService getCustomerTypeService() {
		return customerTypeService;
	}
	public void setCustomerTypeService(CustomerTypeService customerTypeService) {
		this.customerTypeService = customerTypeService;
	}

	public CustomerDetails getProspectCustomerDetails() {
		return prospectCustomerDetails;
	}
	public void setProspectCustomerDetails(CustomerDetails prospectCustomerDetails) {
		this.prospectCustomerDetails = prospectCustomerDetails;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}
	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

}
