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
 * FileName    		:  SelectFinanceTypeDialogCtrl.java                                     * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  16-11-2011    														*
 *                                                                  						*
 * Modified Date    :  16-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-11-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.financemain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.Interface.service.CustomerInterfaceService;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.customermasters.CustEmployeeDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.financemanagement.FinTypeVASProducts;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.rmtmasters.CustomerType;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
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
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.service.rmtmasters.CustomerTypeService;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.service.rmtmasters.PromotionService;
import com.pennant.backend.service.solutionfactory.StepPolicyService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.constants.InterfaceConstants;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.App.Database;
import com.pennanttech.pff.core.TableType;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/SelectFinanceTypeDialog.zul file.
 */
public class SelectFinanceTypeDialogCtrl extends GFCBaseCtrl<FinanceDetail> {
	private static final long					serialVersionUID	= 8556168885363682933L;
	private static final Logger					logger				= Logger.getLogger(SelectFinanceTypeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWiredd by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window							window_SelectFinanceTypeDialog;
	protected ExtendedCombobox					finType;
	protected ExtendedCombobox					wIfFinaceRef;
	protected ExtendedCombobox					promotionCode;
	protected ExtendedCombobox					preApprovedFinRef;
	protected Button							btnProceed;
	protected Button							btnSearchCustCIF;
	protected Textbox							custCIF;
	protected Radio								newCust;
	protected Radio								existingCust;
	protected Radio								preApprovedCust;
	protected Row								promotionCodeRow;
	protected Row								customerRow;
	protected Row								labelRow;
	protected Row								wIfReferenceRow;
	protected Row								row_selectCustomer;
	protected Row								row_EIDNumber;
	protected Row								finTypeRow;
	protected Row								row_custCtgType;
	protected Row								preApprovedFinRefrow;
	protected Combobox							custCtgType;
	protected Uppercasebox						eidNumber;
	protected Label								label_SelectFinanceTypeDialog_EIDNumber;

	protected FinanceMainListCtrl				financeMainListCtrl;														//over handed parameter
	protected transient FinanceWorkFlow			financeWorkFlow;
	private transient WorkFlowDetails			workFlowDetails		= null;
	private List<String>						userRoleCodeList	= new ArrayList<String>();
	private String								productCategory		= "";
	private String								requestSource		= "";

	private transient FinanceTypeService		financeTypeService;
	private transient PromotionService			promotionService;
	private transient FinanceWorkFlowService	financeWorkFlowService;
	private transient FinanceDetailService		financeDetailService;
	private transient CustomerDetailsService	customerDetailsService;
	private transient StepPolicyService			stepPolicyService;
	protected JdbcSearchObject<Customer>		custCIFSearchObject;
	private RelationshipOfficerService			relationshipOfficerService;
	private BranchService						branchService;
	private CustomerTypeService					customerTypeService;
	private CustomerInterfaceService			customerInterfaceService;
	private PagedListService					pagedListService;

	private String								menuItemRightName	= null;
	private FinanceEligibility					financeEligibility	= null;
	private boolean								isPromotionPick		= false;
	private boolean								isRetailCustomer	= false;
	public String								wIfFinaceRef_Temp	= "";

	/**
	 * default constructor.<br>
	 */
	public SelectFinanceTypeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected FinanceMain object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_SelectFinanceTypeDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SelectFinanceTypeDialog);

		// READ OVERHANDED parameters !
		if (arguments.containsKey("financeMainListCtrl")) {
			this.financeMainListCtrl = (FinanceMainListCtrl) arguments.get("financeMainListCtrl");
			setFinanceMainListCtrl(this.financeMainListCtrl);
		} else {
			setFinanceMainListCtrl(null);
		}

		if (arguments.containsKey("requestSource")) {
			this.requestSource = (String) arguments.get("requestSource");
		}

		if (arguments.containsKey("menuItemRightName")) {
			this.menuItemRightName = (String) arguments.get("menuItemRightName");
		}

		if (arguments.containsKey("role")) {
			userRoleCodeList = (ArrayList<String>) arguments.get("role");
		}

		doSetFieldProperties();
		fillComboBox(custCtgType, "", PennantAppUtil.getcustCtgCodeList(), "");

		if (StringUtils.equals("QDE", requestSource)
				|| StringUtils.equals(FinanceConstants.FINSER_EVENT_PREAPPROVAL, requestSource)) {
			this.wIfReferenceRow.setVisible(false);
			this.labelRow.setVisible(false);
		}

		if (arguments.containsKey("fromEligibleScreen")) {
			isPromotionPick = true;
			if (arguments.containsKey("finEligibility")) {
				this.financeEligibility = (FinanceEligibility) arguments.get("finEligibility");
				this.promotionCode.setValue(this.financeEligibility.getPromotionCode());
				this.promotionCode.setDescription(this.financeEligibility.getPromotionDesc());
				this.finType.setValue(this.financeEligibility.getProduct());
				this.finType.setDescription(this.financeEligibility.getProductDesc());
				this.custCIF.setValue(this.financeEligibility.getCustCIF());
				this.productCategory = this.financeEligibility.getFinCategory();//TODO : Re-check is it Finance Category or Product Category
			}
			Events.postEvent("onClick$btnProceed", window_SelectFinanceTypeDialog, event);
		}

		if (StringUtils.equals(FinanceConstants.FINSER_EVENT_PREAPPROVAL, requestSource)) {
			this.preApprovedFinRefrow.setVisible(false);
			this.preApprovedCust.setVisible(false);
		}

		if (!ImplementationConstants.PREAPPROVAL_ALLOWED) {
			this.preApprovedCust.setVisible(false);
		}
		showSelectFinanceTypeDialog();

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		String whereClause = getWhereClauseWithFirstTask();

		// Finance Type
		this.finType.setMaxlength(LengthConstants.LEN_MASTER_CODE);
		this.finType.setMandatoryStyle(true);
		this.finType.setModuleName("FinanceWorkFlow");
		this.finType.setValueColumn("FinType");
		this.finType.setDescColumn("LovDescFinTypeName");
		this.finType.setValidateColumns(new String[] { "FinType" });

		Filter[] filters = new Filter[1];
		if (StringUtils.equals(FinanceConstants.FINSER_EVENT_PREAPPROVAL, requestSource)) {
			filters[0] = new Filter("FinEvent", FinanceConstants.FINSER_EVENT_PREAPPROVAL, Filter.OP_EQUAL);
		} else {
			filters[0] = new Filter("FinEvent", FinanceConstants.FINSER_EVENT_ORG, Filter.OP_EQUAL);
		}
		this.finType.setFilters(filters);

		if (!"".equals(whereClause)) {
			this.finType.setWhereClause(whereClause);
		}

		// Promotion Code
		this.promotionCode.setMaxlength(LengthConstants.LEN_MASTER_CODE);
		this.promotionCode.setModuleName("PromotionWorkFlow");
		this.promotionCode.setValueColumn("LovDescPromotionCode");
		this.promotionCode.setDescColumn("LovDescPromotionName");
		this.promotionCode.setValidateColumns(new String[] { "LovDescPromotionCode" });
	//	getPromotionwithAccess(true);
		if (!"".equals(whereClause)) {
			this.promotionCode.setWhereClause(whereClause);
		}
		
		setPromotionFilters();
		
		// WIF Reference
		this.wIfFinaceRef.setModuleName("WhatIfFinance");
		this.wIfFinaceRef.setValueColumn("FinReference");
		this.wIfFinaceRef.setDescColumn("FinAmount");
		this.wIfFinaceRef.setValidateColumns(new String[] { "FinReference" });
		this.wIfFinaceRef.setTextBoxWidth(120);
		if (!"".equals(whereClause)) {
			this.wIfFinaceRef.setWhereClause(whereClause);
		}

		this.preApprovedFinRef.setMandatoryStyle(true);
		this.preApprovedFinRef.setModuleName("PreAppeovedFinance");
		this.preApprovedFinRef.setValueColumn("FinReference");
		this.preApprovedFinRef.setDescColumn("FinType");
		this.preApprovedFinRef.setValidateColumns(new String[] { "FinReference" });
		this.preApprovedFinRef.setTextBoxWidth(120);
		if (!StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_BFL)) {
			this.eidNumber.setMaxlength(LengthConstants.LEN_EID);
		} else {
			this.eidNumber.setMaxlength(LengthConstants.LEN_PAN);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Checking is promotion Access allowed for this user or not
	 * 
	 * @param setFilters
	 * @return
	 *//*
	private boolean getPromotionwithAccess(boolean setFilters) {

		Filter[] filters = new Filter[4];
		Date appDate = DateUtility.getAppDate();
		filters[0] = new Filter("StartDate", DateUtility.formateDate(appDate, PennantConstants.DBDateFormat),
				Filter.OP_LESS_OR_EQUAL);
		filters[1] = new Filter("EndDate", DateUtility.formateDate(appDate, PennantConstants.DBDateFormat),
				Filter.OP_GREATER_OR_EQUAL);
		filters[2] = new Filter("LovDescProductName", StringUtils.trimToEmpty(this.finType.getValue()), Filter.OP_EQUAL);
		if (StringUtils.equals(FinanceConstants.FINSER_EVENT_PREAPPROVAL, requestSource)) {
			filters[3] = new Filter("FinEvent", FinanceConstants.FINSER_EVENT_PREAPPROVAL, Filter.OP_EQUAL);
		} else {
			filters[3] = new Filter("FinEvent", FinanceConstants.FINSER_EVENT_ORG, Filter.OP_EQUAL);
		}

		this.promotionCode.setFilters(filters);
		if (!setFilters) {
			JdbcSearchObject<FinanceWorkFlow> searchObject = new JdbcSearchObject<FinanceWorkFlow>(
					FinanceWorkFlow.class);
			searchObject.addTabelName("LMTFinanceWorkFlowDef_PTView");
			searchObject.addField("LovDescPromotionCode");
			searchObject.addFilterAnd(filters);
			List<FinanceWorkFlow> list = this.pagedListService.getBySearchObject(searchObject);
			if (list != null && !list.isEmpty()) {
				return true;
			}
		}
		return false;
	}
*/
	/**
	 * method for Checking First Task Owneraginst assigned Role Details for the user
	 * 
	 * @return
	 */
	private String getWhereClauseWithFirstTask() {
		StringBuilder whereClause = new StringBuilder();
		if (userRoleCodeList != null && userRoleCodeList.size() > 0) {
			for (String role : userRoleCodeList) {
				if (whereClause.length() > 0) {
					whereClause.append(" OR ");
				}

				whereClause.append("(',' ");
				if (App.DATABASE == Database.ORACLE || App.DATABASE == Database.PSQL) {
					whereClause.append("||");
				} else {
					whereClause.append("+");
				}
				whereClause.append(" LovDescFirstTaskOwner ");
				if (App.DATABASE == Database.ORACLE || App.DATABASE == Database.PSQL) {
					whereClause.append("||");
				} else {
					whereClause.append("+");
				}
				whereClause.append(" ',' LIKE '%,");
				whereClause.append(role);
				whereClause.append(",%')");
			}
		}
		return whereClause.toString();
	}

	/**
	 * Method for Checking Access for Allowed What-if References
	 */
	private void getWIFFinancenwithAccess() {
		logger.debug("Entering ");
		
		Filter[] filters;
		if (StringUtils.isNotBlank(this.productCategory)) {
			filters = new Filter[3];
		} else {
			filters = new Filter[1];
		}

		if (StringUtils.isBlank(this.promotionCode.getValue())) {
			if(StringUtils.isBlank(this.finType.getValue())){
				filters[0] = new Filter("FinType", this.finType.getValue(), Filter.OP_NOT_EQUAL);
			}else{
				filters[0] = new Filter("FinType", this.finType.getValue(), Filter.OP_EQUAL);
			}
		} else {
			filters[0] = new Filter("FinType", this.promotionCode.getValue(), Filter.OP_EQUAL);
		}
		
		if (StringUtils.isNotBlank(this.productCategory)) {
			Date appDate = DateUtility.getAppDate();
			Date wifAvailableDate = DateUtility.addDays(appDate, -SysParamUtil.getValueAsInt("MAX_WIF_BACKDAYS"));
			filters[1] = new Filter("lovDescProductCodeName", this.productCategory, Filter.OP_EQUAL);
			filters[2] = new Filter("LastMntOn", DateUtility.formateDate(wifAvailableDate,
					PennantConstants.DBDateFormat), Filter.OP_GREATER_OR_EQUAL);
		}

		this.wIfFinaceRef.setFilters(filters);
		
		logger.debug("Leaving ");
	}

	/**
	 * When user clicks on button "SearchFinType" button
	 * 
	 * @param event
	 */
	public void onFulfill$finType(Event event) {
		logger.debug("Entering " + event.toString());

		this.finType.setConstraint("");
		this.finType.clearErrorMessage();
		Clients.clearWrongValue(finType);
		Object dataObject = this.finType.getObject();
		
		if (dataObject instanceof String) {
			this.finType.setValue(dataObject.toString());
			this.finType.setDescription("");
			CheckScreenCode("");
		} else {
			FinanceWorkFlow details = (FinanceWorkFlow) dataObject;
			/* Set FinanceWorkFloe object */
			setFinanceWorkFlow(details);
			if (details != null) {
				this.productCategory = details.getProductCategory();
				this.finType.setValue(details.getFinType());
				this.finType.setDescription(details.getLovDescFinTypeName());
				CheckScreenCode(details.getScreenCode());
			}
		}
		
		this.promotionCode.setValue("", "");
		this.promotionCode.setObject("");
		this.wIfFinaceRef.setValue("");
		this.wIfFinaceRef.setObject("");

		setPromotionFilters();

		this.wIfFinaceRef.setFilters(new Filter[0]);
		if (StringUtils.isNotEmpty(this.finType.getValue().trim())) {
			getWIFFinancenwithAccess();
		}
		
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method for Selecting Promotion Code
	 * 
	 * @param event
	 */
	public void onFulfill$promotionCode(Event event) {
		logger.debug("Entering " + event.toString());

		this.promotionCode.setConstraint("");
		this.promotionCode.clearErrorMessage();
		this.promotionCode.setErrorMessage("");

		Object dataObject = this.promotionCode.getObject();
		if (dataObject instanceof String) {
			this.promotionCode.setValue(dataObject.toString());
			this.promotionCode.setDescription("");
			CheckScreenCode("");
		} else {
			FinanceWorkFlow details = (FinanceWorkFlow) dataObject;
			/* Set FinanceWorkFloe object */
			setFinanceWorkFlow(details);
			if (details != null) {

				this.productCategory = details.getProductCategory();
				this.promotionCode.setValue(details.getLovDescPromotionCode());
				this.promotionCode.setDescription(details.getLovDescPromotionName());
				this.finType.setValue(details.getLovDescProductName());
				this.finType.setDescription(details.getLovDescFinTypeName());
				CheckScreenCode(details.getScreenCode());
			}else{
				FinanceType aFinanceType = new FinanceType();
				this.productCategory = aFinanceType.getProductCategory();
			}
		}

		getWIFFinancenwithAccess();

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method for Checking Screen Code Object to avail Customer
	 */
	private void setPromotionFilters() {
		logger.debug("Entering ");
		
		Filter[] filters = null;
		if(StringUtils.isEmpty(this.finType.getValue())){
			filters = new Filter[4];
		}else{
			filters = new Filter[5];
		}
		
		Date appDate = DateUtility.getAppDate();
		filters[0] = new Filter("StartDate", DateUtility.formateDate(appDate, PennantConstants.DBDateFormat),
				Filter.OP_LESS_OR_EQUAL);
		filters[1] = new Filter("EndDate", DateUtility.formateDate(appDate, PennantConstants.DBDateFormat),
				Filter.OP_GREATER_OR_EQUAL);
		if (StringUtils.equals(FinanceConstants.FINSER_EVENT_PREAPPROVAL, requestSource)) {
			filters[2] = new Filter("FinEvent", FinanceConstants.FINSER_EVENT_PREAPPROVAL, Filter.OP_EQUAL);
		} else {
			filters[2] = new Filter("FinEvent", FinanceConstants.FINSER_EVENT_ORG, Filter.OP_EQUAL);
		}
		
		filters[3] = new Filter("Active", 1, Filter.OP_EQUAL);
		
		if(!StringUtils.isEmpty(this.finType.getValue())){
			filters[4] = new Filter("LovDescProductName", StringUtils.trimToEmpty(this.finType.getValue()), Filter.OP_EQUAL);
		} 
		this.promotionCode.setFilters(filters);
		
		logger.debug("Leaving");
	}
	
	
	/**
	 * Method for Checking Screen Code Object to avail Customer
	 */
	private void CheckScreenCode(String screenCode) {
		logger.debug("Entering ");
		
		if (StringUtils.isEmpty(screenCode) || "DDE".equals(screenCode)) {
			this.row_selectCustomer.setVisible(true);
			if (this.existingCust.isChecked()) {
				this.customerRow.setVisible(true);
				this.custCIF.setDisabled(false);
			} else {
				this.newCust.setSelected(true);
				this.customerRow.setVisible(false);
				this.custCIF.setDisabled(true);
			}
		} else if ("QDE".equals(screenCode)) {
			this.row_selectCustomer.setVisible(false);
			this.existingCust.setSelected(true);
			this.customerRow.setVisible(false);
			this.custCIF.setDisabled(true);
			this.custCIF.setValue("");
		}
		
		logger.debug("Leaving");
	}

	/**
	 * When user clicks on button "btnSearchWIFFinaceRef" button
	 * 
	 * @param event
	 */
	public void onFulfill$wIfFinaceRef(Event event) {
		logger.debug("Entering " + event.toString());
		Clients.clearWrongValue(finType);
		doClearWIFData();
		Object dataObject = this.wIfFinaceRef.getObject();
		if (dataObject instanceof String) {
			this.wIfFinaceRef.setValue(dataObject.toString());
		} else {
			FinanceMain details = (FinanceMain) dataObject;
			if (details != null) {
				this.productCategory = details.getLovDescProductCodeName();
				this.wIfFinaceRef.setValue(details.getFinReference(), "");
				this.finType.setValue(details.getFinType());
				this.finType.setDescription(details.getLovDescProductCodeName());
				//Getting the wifCustomers information
				WIFCustomer wifCustomer = getWifCustomers(true, details.getCustID());

				if (wifCustomer != null) {
					long custId = wifCustomer.getExistCustID();
					String cutCIFValue = getWifCustCIF(custId);
					if (StringUtils.isNotBlank(cutCIFValue)) {
						this.custCIF.setValue(cutCIFValue);
						this.newCust.setDisabled(true);
						this.existingCust.setChecked(true);
						setCustomerRowProperties(false, false);
						this.custCIF.setDisabled(true);
						this.btnSearchCustCIF.setDisabled(true);
						this.finType.setReadonly(true);
					} else if (StringUtils.isNotBlank(wifCustomer.getCustCRCPR())) {
						fillComboBox(custCtgType, wifCustomer.getCustCtgCode(), PennantAppUtil.getcustCtgCodeList(), "");
						this.custCtgType.setDisabled(true);
						this.finType.setReadonly(true);
						this.newCust.setChecked(true);
						this.existingCust.setDisabled(true);
						setCustomerRowProperties(true, false);
						changeCustCtgType();
						this.eidNumber.setValue(PennantApplicationUtil.formatEIDNumber(wifCustomer.getCustCRCPR()));
						this.eidNumber.setDisabled(true);
					} else if (StringUtils.isBlank(this.custCIF.getValue())
							&& StringUtils.isBlank(this.eidNumber.getValue())) {
						this.finType.setReadonly(true);
						fillComboBox(this.custCtgType, wifCustomer.getCustCtgCode(),
								PennantAppUtil.getcustCtgCodeList(), "");
					}
				}

				if (financeWorkFlow != null) {
					CheckScreenCode(financeWorkFlow.getScreenCode());
				}
			}
			wIfFinaceRef_Temp = this.wIfFinaceRef.getValidatedValue();
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method to Enable the Fields when WifReference is Cleared
	 */
	private void doClearWIFData() {
		logger.debug("Entering");

		if (!StringUtils.trimToEmpty(wIfFinaceRef_Temp).equals(StringUtils.trimToEmpty(this.wIfFinaceRef.getValue()))) {
			this.wIfFinaceRef.setDescription("");
			this.finType.setValue("");
			this.finType.setDescription("");
			//this.custCIF.setValue("");
			this.eidNumber.setValue("");
			this.custCtgType.setValue("");
			this.newCust.setDisabled(false);
			this.existingCust.setDisabled(false);
			this.custCIF.setDisabled(false);
			this.custCtgType.setDisabled(false);
			this.eidNumber.setDisabled(false);
			this.finType.setReadonly(false);
			this.btnSearchCustCIF.setDisabled(false);
		}
		wIfFinaceRef_Temp = this.wIfFinaceRef.getValidatedValue();

		logger.debug("Leaving");
	}

	/**
	 * Method to Get WIFCustomers Data
	 */
	public WIFCustomer getWifCustomers(boolean iscust, Object custId) {
		logger.debug("Entering");

		WIFCustomer wifCustomer = new WIFCustomer();
		JdbcSearchObject<WIFCustomer> searchObject = new JdbcSearchObject<WIFCustomer>(WIFCustomer.class);
		searchObject.addTabelName("WIFCustomers_AView");
		if (iscust) {
			searchObject.addFilterEqual("CustID", custId);
		} else {
			searchObject.addFilterEqual("CustCRCPR", custId);
		}
		List<WIFCustomer> wifCustomers = pagedListService.getBySearchObject(searchObject);
		if (wifCustomers != null && !wifCustomers.isEmpty()) {
			return wifCustomers.get(0);
		}

		logger.debug("Leaving");
		return wifCustomer;
	}

	/**
	 * Method to get the CustomerCIF when WIFReference is entered
	 */
	private String getWifCustCIF(long custId) {
		logger.debug("Entering");

		String custCIF = "";
		JdbcSearchObject<Customer> searchObject1 = new JdbcSearchObject<Customer>(Customer.class);
		searchObject1.addTabelName("Customers");
		searchObject1.addFilterEqual("CustID", custId);
		List<Customer> wifCustomers = pagedListService.getBySearchObject(searchObject1);
		if (wifCustomers != null && !wifCustomers.isEmpty()) {
			return wifCustomers.get(0).getCustCIF();
		}

		logger.debug("Leaving");
		return custCIF;
	}

	/**
	 * Method to get the CustomerData From WifCustomer When WifReference is Entered
	 */
	public void processWIFCustomerData(WIFCustomer wCustomer, CustomerDetails customerDetails) {
		logger.debug("Entering");

		Customer customer = customerDetails.getCustomer();
		if (customer == null) {
			customer = new Customer();
		}

		CustEmployeeDetail detail = customerDetails.getCustEmployeeDetail();
		if (detail == null) {
			detail = new CustEmployeeDetail();
		}

		if (wCustomer.isElgRequired()) {
			customer.setCustMaritalSts(wCustomer.getCustMaritalSts());
			customer.setCustNationality(wCustomer.getCustNationality());
			customer.setLovDescCustNationalityName(wCustomer.getLovDescCustNationalityName());
			customer.setCustDOB(wCustomer.getCustDOB());
			customer.setCustGenderCode(wCustomer.getCustGenderCode());
			customer.setCustSalutationCode(wCustomer.getCustSalutationCode());
			customer.setNoOfDependents(wCustomer.getNoOfDependents());
			customer.setCustBaseCcy(wCustomer.getCustBaseCcy());
			customer.setCustCRCPR(PennantApplicationUtil.formatEIDNumber(wCustomer.getCustCRCPR()));
			customer.setSalariedCustomer(wCustomer.isSalariedCustomer());
			customer.setCustFName(wCustomer.getCustShrtName());
			customer.setCustTypeCode(wCustomer.getCustTypeCode());
			customer.setLovDescCustTypeCodeName(wCustomer.getLovDescCustTypeCodeName());
			String custCategoryType = this.custCtgType.getSelectedItem().getValue().toString();
			customer.setLovDescCustCtgType(custCategoryType);
			if (StringUtils.equals(PennantConstants.PFF_CUSTCTG_INDIV, this.custCtgType.getSelectedItem().getValue()
					.toString())) {
				customer.setCustCtgCode(PennantConstants.PFF_CUSTCTG_INDIV);
				customer.setLovDescCustCtgCodeName(PennantConstants.PFF_CUSTCTG_INDIV);
			} else if (StringUtils.equals(PennantConstants.PFF_CUSTCTG_CORP, this.custCtgType.getSelectedItem()
					.getValue().toString())) {
				customer.setCustCtgCode(PennantConstants.PFF_CUSTCTG_CORP);
				customer.setLovDescCustCtgCodeName(PennantConstants.PFF_CUSTCTG_CORP);
			} else {
				customer.setCustCtgCode(PennantConstants.PFF_CUSTCTG_SME);
				customer.setLovDescCustCtgCodeName(PennantConstants.PFF_CUSTCTG_SME);
			}
			customer.setCustCIF(this.customerDetailsService.getNewProspectCustomerCIF());
			customerDetails.setCustomer(customer);

			detail.setEmpDept(wCustomer.getEmpDept());
			detail.setLovDescEmpDept(wCustomer.getLovDescEmpDept());
			detail.setEmpDesg(wCustomer.getEmpDesg());
			detail.setLovDescEmpDesg(wCustomer.getLovDescEmpDesg());
			detail.setEmpName(wCustomer.getEmpName());
			detail.setLovDescEmpName(wCustomer.getLovDescEmpName());
			detail.setEmpStatus(wCustomer.getCustEmpSts());
			detail.setLovDescEmpStatus(wCustomer.getLovDescCustEmpStsName());
			detail.setEmpSector(wCustomer.getCustSubSector());
			detail.setLovDescEmpSector(wCustomer.getLovDescCustSubSectorName());
			detail.setMonthlyIncome(wCustomer.getTotalIncome());
			customerDetails.setCustEmployeeDetail(detail);
		} else {
			customer.setSalariedCustomer(wCustomer.isSalariedCustomer());
			customer.setCustFName(wCustomer.getCustShrtName());
			customerDetails.setCustomer(customer);

			detail.setEmpStatus(wCustomer.getCustEmpSts());
			detail.setLovDescEmpStatus(wCustomer.getLovDescCustEmpStsName());
			customerDetails.setCustEmployeeDetail(detail);
		}
		logger.debug("Leaving");
	}

	/**
	 * When user clicks on button "btnProceed" button
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnProceed(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		if (!isPromotionPick) {
			if (!doFieldValidation()) {
				logger.debug("Leaving " + event.toString());
				return;
			}
		}

		//Customer Data Fetching
		CustomerDetails customerDetails;
		if (isPromotionPick) {
			customerDetails = this.financeEligibility.getFinanceDetail().getCustomerDetails();
		} else {
			customerDetails = fetchCustomerData();
		}

		// Check Customer Details exists with entered data or not
		if (customerDetails == null) {
			logger.debug("Leaving " + event.toString());
			return;
		}

		FinanceDetail financeDetail = this.financeDetailService.getNewFinanceDetail(false);
		FinanceDetail befImage = new FinanceDetail();
		financeDetail.setBefImage(befImage);

		// Resetting Finance Event based on Request Resource
		String financeEvent = "";
		if (StringUtils.equals(FinanceConstants.FINSER_EVENT_PREAPPROVAL, requestSource)) {
			financeEvent = FinanceConstants.FINSER_EVENT_PREAPPROVAL;
		} else {
			financeEvent = FinanceConstants.FINSER_EVENT_ORG;
		}

		// If User requested through What-if Reference
		FinanceType financeType = null;
		boolean promotionFlag = false;
		if (StringUtils.isNotEmpty(this.wIfFinaceRef.getValue())) {

			financeDetail = this.financeDetailService.getWIFFinanceDetailById(this.wIfFinaceRef.getValue(),
					financeEvent);

			financeDetail.getFinScheduleData().getFinanceMain()
					.setCurDisbursementAmt(financeDetail.getFinScheduleData().getFinanceMain().getFinAmount());
			financeDetail.getFinScheduleData().getFinanceMain().setNewRecord(true);
			financeDetail.getFinScheduleData().getFinanceMain().setRecordType("");
			financeDetail.getFinScheduleData().getFinanceMain().setVersion(0);
			financeDetail.getFinScheduleData().getFinanceMain().setWifReference(this.wIfFinaceRef.getValue());

			//overdue Penalty Details
			if (financeDetail.getFinScheduleData().getFinODPenaltyRate() == null) {
				financeDetail.getFinScheduleData().setFinODPenaltyRate(new FinODPenaltyRate());
			}

			financeType = financeDetail.getFinScheduleData().getFinanceType();
			financeDetail.getFinScheduleData().getFinODPenaltyRate().setApplyODPenalty(financeType.isApplyODPenalty());
			financeDetail.getFinScheduleData().getFinODPenaltyRate().setODIncGrcDays(financeType.isODIncGrcDays());
			financeDetail.getFinScheduleData().getFinODPenaltyRate().setODChargeCalOn(financeType.getODChargeCalOn());
			financeDetail.getFinScheduleData().getFinODPenaltyRate().setODGraceDays(financeType.getODGraceDays());
			financeDetail.getFinScheduleData().getFinODPenaltyRate().setODChargeType(financeType.getODChargeType());
			financeDetail.getFinScheduleData().getFinODPenaltyRate()
					.setODChargeAmtOrPerc(financeType.getODChargeAmtOrPerc());
			financeDetail.getFinScheduleData().getFinODPenaltyRate().setODAllowWaiver(financeType.isODAllowWaiver());
			financeDetail.getFinScheduleData().getFinODPenaltyRate()
					.setODMaxWaiverPerc(financeType.getODMaxWaiverPerc());

		} else {

			String fintype = "";
			if (StringUtils.isNotBlank(this.preApprovedFinRef.getValue())) {
				FinanceMain financeMain = (FinanceMain) this.preApprovedFinRef.getObject();
				fintype = financeMain.getFinType();
			} else { 
				fintype = this.finType.getValue().trim();
			}

			
			// Fetching Finance Type Details
			financeType = this.financeTypeService.getOrgFinanceTypeById(fintype);
			if (StringUtils.isNotBlank(promotionCode.getValue())) {
				promotionFlag = true;
				// Fetching Promotion Details
				Promotion promotion = this.promotionService.getApprovedPromotionById(promotionCode.getValue(), FinanceConstants.MODULEID_PROMOTION, false);
				financeType.setFInTypeFromPromotiion(promotion);
			}
						
			financeDetail.getFinScheduleData().setFinanceType(financeType);
			
			// Step Policy Details
			if (financeType.isStepFinance()) {
				List<StepPolicyDetail> stepPolicyList = this.stepPolicyService.getStepPolicyDetailsById(financeType
						.getDftStepPolicy());
				financeDetail.getFinScheduleData().resetStepPolicyDetails(stepPolicyList);
			}
			
			if (StringUtils.isNotBlank(this.preApprovedFinRef.getValue())) {
				setPreApprovalRequiredDetails(financeDetail, financeType, this.preApprovedFinRef.getValue());
				this.productCategory = financeType.getProductCategory();
				customerDetails = financeDetail.getCustomerDetails();
			} else {
				financeDetail.getFinScheduleData().setFinanceMain(new FinanceMain(), financeType);
			}

			// If promotion Pick, Set user Entered Details from Existing Data
			if (isPromotionPick) {
				FinanceMain elgFinMain = this.financeEligibility.getFinanceDetail().getFinScheduleData()
						.getFinanceMain();
				if (elgFinMain != null) {
					financeDetail.getFinScheduleData().getFinanceMain().setNumberOfTerms(elgFinMain.getNumberOfTerms());
					financeDetail.getFinScheduleData().getFinanceMain().setFinAmount(elgFinMain.getFinAmount());
				}
			}
		}

		// Workflow Details Verification and initiation, if not found
		if (this.financeWorkFlow == null) {
			FinanceWorkFlow financeWorkFlow = this.financeWorkFlowService.getApprovedFinanceWorkFlowById(
					financeType.getFinType(), financeEvent, promotionFlag ? PennantConstants.WORFLOW_MODULE_PROMOTION : PennantConstants.WORFLOW_MODULE_FINANCE);
			setFinanceWorkFlow(financeWorkFlow);
		}

		try {
			//Fetch & set Default statuses f
			if (financeDetail.getFinScheduleData().getFinanceMain() != null) {
				financeDetail.getFinScheduleData().getFinanceMain().setFinStsReason(FinanceConstants.FINSTSRSN_SYSTEM);
				financeDetail.getFinScheduleData().getFinanceMain()
						.setFinStatus(this.financeDetailService.getCustStatusByMinDueDays());
			}
		} catch (Exception e) {
			logger.debug(e);
		}

		//Workflow Details Setup
		if (this.financeWorkFlow != null) {
			workFlowDetails = WorkFlowUtil.getDetailsByType(this.financeWorkFlow.getWorkFlowType());
		}
		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
			financeDetail.getFinScheduleData().getFinanceMain().setWorkflowId(0);
		} else {
			setWorkFlowEnabled(true);

			if (workFlowDetails.getFirstTaskOwner().contains(PennantConstants.DELIMITER_COMMA)) {
				String[] fisttask = workFlowDetails.getFirstTaskOwner().split(PennantConstants.DELIMITER_COMMA);
				for (String string : fisttask) {
					if (getUserWorkspace().isRoleContains(string)) {
						setFirstTask(true);
						break;
					}
				}
			} else {
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			}
			setWorkFlowId(workFlowDetails.getId());
			financeDetail.getFinScheduleData().getFinanceMain().setWorkflowId(workFlowDetails.getWorkFlowId());

			FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
			doLoadWorkFlow(financeMain.isWorkflow(), financeMain.getWorkflowId(), financeMain.getNextTaskId());

		}

		// Preparing Mandatory VAS Products for defaulting
		if (ImplementationConstants.ALLOW_VAS) {

			List<FinTypeVASProducts> finTypeVASProductsList = financeType.getFinTypeVASProductsList();
			if (finTypeVASProductsList == null) {
				finTypeVASProductsList = financeTypeService.getFinTypeVasProducts(finType.getValue());
			}

			List<VASRecording> vasRecordingList = new ArrayList<VASRecording>();
			for (FinTypeVASProducts finTypeVASProducts : finTypeVASProductsList) {
				if (finTypeVASProducts.isMandatory()) {
					VASRecording vasRecording = new VASRecording();
					vasRecording.setNewRecord(true);
					vasRecording.setProductCode(finTypeVASProducts.getVasProduct());
					vasRecording.setProductType(finTypeVASProducts.getProductType());
					vasRecording.setPostingAgainst(finTypeVASProducts.getRecAgainst());
					vasRecording.setProductCtg(finTypeVASProducts.getProductCtgDesc());
					vasRecording.setManufacturerDesc(finTypeVASProducts.getManufacturerDesc());
					vasRecording.setValueDate(DateUtility.getAppDate());
					vasRecording.setAccrualTillDate(DateUtility.getAppDate());
					vasRecording.setFee(finTypeVASProducts.getVasFee());
					vasRecording.setRecordType(PennantConstants.RCD_ADD);
					vasRecordingList.add(vasRecording);
				}
			}
			financeDetail.getFinScheduleData().setVasRecordingList(vasRecordingList);
		}

		financeDetail.setNewRecord(true);
		financeDetail.setCustomerDetails(customerDetails);
		financeDetail.getFinScheduleData().getFinanceMain().setCustID(customerDetails.getCustomer().getCustID());

		//Fetching Finance Reference Detail
		if (isWorkFlowEnabled()) {
			financeDetail = this.financeDetailService.getFinanceReferenceDetails(financeDetail, getRole(),
					this.financeWorkFlow.getScreenCode(), "", financeEvent, true);
		}

		Date finStartDate = financeDetail.getFinScheduleData().getFinanceMain().getFinStartDate();
		if (finStartDate != null) {
			String finEvent = PennantApplicationUtil.getEventCode(finStartDate);

			if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeDetail.getFinScheduleData()
					.getFinanceType().getProductCategory())) {
				finEvent = AccountEventConstants.ACCEVENT_CMTDISB;
			}

			financeDetail.getFinScheduleData().setFeeEvent(finEvent);
			
			// Fee Details Fetching From Finance Type
			if (promotionFlag) {
				financeDetail.setFinTypeFeesList(this.financeDetailService.getFinTypeFees(
						this.promotionCode.getValue(), finEvent, true, FinanceConstants.MODULEID_PROMOTION));
			} else {
				financeDetail.setFinTypeFeesList(this.financeDetailService.getFinTypeFees(this.finType.getValue(),
						finEvent, true, FinanceConstants.MODULEID_FINTYPE));
			}

		}

		showDetailView(financeDetail);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method for Preparation of Finance Details Data for Pre-Approval Process
	 * 
	 * @param financeDetail
	 * @param financeType
	 * @param finref
	 */
	private void setPreApprovalRequiredDetails(FinanceDetail financeDetail, FinanceType financeType, String finref) {
		logger.debug("Entering");

		FinanceDetail preApprovalFin = this.financeDetailService.getPreApprovalFinanceDetailsById(finref);
		FinanceMain preAppFinMain = preApprovalFin.getFinScheduleData().getFinanceMain();
		preAppFinMain.setLovDescFinTypeName(financeType.getFinTypeDesc());
		financeDetail.getFinScheduleData().setFinanceMain(preAppFinMain);
		financeDetail.getFinScheduleData().getFinanceMain().setFinPreApprovedRef(finref);
		financeDetail.getFinScheduleData().getFinanceMain().setNewRecord(true);
		financeDetail.getFinScheduleData().getFinanceMain().setRecordType("");
		financeDetail.getFinScheduleData().getFinanceMain().setVersion(0);
		financeDetail.setCustomerDetails(this.customerDetailsService.getCustomerDetailsById(preAppFinMain.getCustID(),
				true, "_View"));
		financeDetail.setDocumentDetailsList(preApprovalFin.getDocumentDetailsList());
		if (financeDetail.getDocumentDetailsList() != null && !financeDetail.getDocumentDetailsList().isEmpty()) {
			for (DocumentDetails details : financeDetail.getDocumentDetailsList()) {
				details.setReferenceId("");
				details.setFinEvent("");
				if (!details.isDocIsCustDoc()) {
					details.setNewRecord(true);
					details.setRecordType(PennantConstants.RCD_ADD);
				}
			}
		}

		FinODPenaltyRate penaltyRate = financeDetail.getFinScheduleData().getFinODPenaltyRate();

		//overdue Penalty Details
		if (penaltyRate == null) {
			penaltyRate = new FinODPenaltyRate();
		}
		penaltyRate.setApplyODPenalty(financeType.isApplyODPenalty());
		penaltyRate.setODIncGrcDays(financeType.isODIncGrcDays());
		penaltyRate.setODChargeCalOn(financeType.getODChargeCalOn());
		penaltyRate.setODGraceDays(financeType.getODGraceDays());
		penaltyRate.setODChargeType(financeType.getODChargeType());
		penaltyRate.setODChargeAmtOrPerc(financeType.getODChargeAmtOrPerc());
		penaltyRate.setODAllowWaiver(financeType.isODAllowWaiver());
		penaltyRate.setODMaxWaiverPerc(financeType.getODMaxWaiverPerc());
		financeDetail.getFinScheduleData().setFinODPenaltyRate(penaltyRate);

		logger.debug("Leaving");
	}

	// GUI Process
	private void showDetailView(FinanceDetail financeDetail) throws InterruptedException {
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them with a object of the selected item. For
		 * handed over these parameter only a Map is accepted. So we put the object in a HashMap.
		 */
		if (isWorkFlowEnabled()) {

			StringBuilder fileLocation = new StringBuilder("/WEB-INF/pages/Finance/FinanceMain/");
			/*
			 * if screen code is quick data entry (QDE) navigate to QDE screen otherwise navigate to Detail data entry
			 * screen
			 */

			if (StringUtils.equals("QDE", requestSource)) {
				fileLocation.append("QDEFinanceMainDialog.zul");
			} else if (StringUtils.equals(requestSource, FinanceConstants.FINSER_EVENT_PREAPPROVAL)) {
				fileLocation.append("FinancePreApprovalDialog.zul");
			} else {
				String productType = StringUtils.trimToEmpty(this.productCategory);
				if (StringUtils.equals(productType, FinanceConstants.PRODUCT_CONVENTIONAL)) {
					fileLocation.append("ConvFinanceMainDialog.zul");
				} else if (StringUtils.equals(productType, FinanceConstants.PRODUCT_ODFACILITY)) {
					fileLocation.append("ODFacilityFinanceMainDialog.zul");
				} else if (StringUtils.equals(productType, FinanceConstants.PRODUCT_DISCOUNT)) {
					fileLocation.append("DiscountFinanceMainDialog.zul");
				} else if (StringUtils.equals(productType, FinanceConstants.PRODUCT_MURABAHA)) {
					fileLocation.append("MurabahaFinanceMainDialog.zul");
				} else if (StringUtils.equals(productType, FinanceConstants.PRODUCT_IJARAH)) {
					fileLocation.append("IjarahFinanceMainDialog.zul");
				} else if (StringUtils.equals(productType, FinanceConstants.PRODUCT_FWIJARAH)) {
					fileLocation.append("FwdIjarahFinanceMainDialog.zul");
				} else if (StringUtils.equals(productType, FinanceConstants.PRODUCT_TAWARRUQ)) {
					fileLocation.append("TawarruqFinanceMainDialog.zul");
				} else if (StringUtils.equals(productType, FinanceConstants.PRODUCT_MUDARABA)) {
					fileLocation.append("MudarabaFinanceMainDialog.zul");
				} else if (StringUtils.equals(productType, FinanceConstants.PRODUCT_MUSHARAKA)) {
					fileLocation.append("MusharakFinanceMainDialog.zul");
				} else if (StringUtils.equals(productType, FinanceConstants.PRODUCT_WAKALA)) {
					fileLocation.append("CorporateWakalaFinanceMainDialog.zul");
				} else if (StringUtils.equals(productType, FinanceConstants.PRODUCT_QARDHASSAN)) {
					fileLocation.append("QardHassanFinanceMainDialog.zul");
				} else if (StringUtils.equals(productType, FinanceConstants.PRODUCT_MUSAWAMA)) {
					fileLocation.append("MusawamaFinanceMainDialog.zul");
				} else if (StringUtils.equals(productType, FinanceConstants.PRODUCT_SUKUKNRM)) {
					fileLocation.append("SukuknrmFinanceMainDialog.zul");
				} else if (StringUtils.equals(productType, FinanceConstants.PRODUCT_ISTNORM)) {
					fileLocation.append("IstnormFinanceMainDialog.zul");
				} else if (StringUtils.equals(productType, FinanceConstants.PRODUCT_STRUCTMUR)) {
					fileLocation.append("StructuredMurabahaFinanceMainDialog.zul");
				} else if (StringUtils.equals(productType, FinanceConstants.PRODUCT_ISTISNA)) {
					fileLocation.append("IstisnaFinanceMainDialog.zul");
				} else if (StringUtils.equals(productType, FinanceConstants.PRODUCT_SUKUK)) {
					fileLocation.append("SukukFinanceMainDialog.zul");
				} else {
					MessageUtil
							.showError(Labels.getLabel("message.error.productNotFound", new String[] { productType }));
					return;
				}
			}

			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("financeDetail", financeDetail);
			map.put("financeMainListCtrl", this.financeMainListCtrl);
			map.put("menuItemRightName", menuItemRightName);

			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents(fileLocation.toString(), null, map);
				this.window_SelectFinanceTypeDialog.onClose();
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		} else {
			logger.fatal("work flow not found");
			if (isPromotionPick) {
				this.window_SelectFinanceTypeDialog.onClose();
			}
			MessageUtil.showError(Labels.getLabel("Workflow_Not_Found")
					+ financeDetail.getFinScheduleData().getFinanceMain().getFinType());

		}
	}

	/**
	 * Opens the SelectFinanceTypeDialog window modal.
	 */
	private void showSelectFinanceTypeDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_SelectFinanceTypeDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 * 
	 * @throws InterruptedException
	 */
	private boolean doFieldValidation() throws InterruptedException {
		logger.debug("Entering ");
		doClearMessage();
		doRemoveValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		if (!this.preApprovedFinRefrow.isVisible()) {
			try {
				if (wIfReferenceRow.isVisible()) {
					if (StringUtils.isBlank(this.finType.getValue())
							&& StringUtils.isBlank(this.wIfFinaceRef.getValue())) {
						throw new WrongValueException(this.finType, Labels.getLabel(
								"CHECK_NO_EMPTY_IN_TWO",
								new String[] { Labels.getLabel("label_SelectFinanceTypeDialog_FinType.value"),
										Labels.getLabel("label_SelectFinanceTypeDialog_WIFFinaceRef.value") }));
					}
				} else {
					if (StringUtils.isBlank(this.finType.getValue())) {
						throw new WrongValueException(this.finType, Labels.getLabel("CHECK_NO_EMPTY",
								new String[] { Labels.getLabel("label_SelectFinanceTypeDialog_FinType.value") }));
					}
				}
			} catch (WrongValueException e) {
				wve.add(e);
			}

			try {
				if (StringUtils.isBlank(this.finType.getValue())
						&& StringUtils.isNotBlank(this.promotionCode.getValue())) {
					throw new WrongValueException(this.finType, Labels.getLabel("CHECK_NO_EMPTY",
							new String[] { Labels.getLabel("label_SelectFinanceTypeDialog_FinType.value") }));
				}
			} catch (WrongValueException e) {
				wve.add(e);
			}
			try {
				if (StringUtils.isNotBlank(this.promotionCode.getValue())
						&& StringUtils.isNotBlank(this.wIfFinaceRef.getValue())) {
					throw new WrongValueException(this.promotionCode, Labels.getLabel(
							"EITHER_OR",
							new String[] { Labels.getLabel("label_SelectFinanceTypeDialog_PromotionCode.value"),
									Labels.getLabel("label_SelectFinanceTypeDialog_WIFFinaceRef.value") }));
				}
			} catch (WrongValueException e) {
				wve.add(e);
			}

			try {
				if (this.existingCust.isChecked()) {
					if (StringUtils.isEmpty(this.custCIF.getValue())) {
						throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_EMPTY",
								new String[] { Labels.getLabel("label_SelectFinanceTypeDialog_CustCIF.value") }));
					}
				}
			} catch (WrongValueException e) {
				wve.add(e);
			}

			this.eidNumber.clearErrorMessage();
			this.eidNumber.setConstraint("");

			if (this.newCust.isChecked()) {

				try {
					if (getComboboxValue(this.custCtgType).equals(PennantConstants.List_Select)) {
						throw new WrongValueException(this.custCtgType, Labels.getLabel("FIELD_NO_EMPTY",
								new String[] { Labels.getLabel("label_SelectFinanceTypeDialog_CustCtg.value") }));
					}
				} catch (WrongValueException e) {
					wve.add(e);
				}
				if (!StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_BFL)) {
					this.eidNumber.setConstraint(new PTStringValidator(Labels
							.getLabel("label_SelectFinanceTypeDialog_TradeLicenseNumber.value"),
							PennantRegularExpressions.REGEX_TRADELICENSE, true));
				} else {
					this.eidNumber.setConstraint(new PTStringValidator(Labels
							.getLabel("label_SelectFinanceTypeDialog_TradeLicenseNumber.value"),
							PennantRegularExpressions.REGEX_PANNUMBER, true));
				}
			}
			try {
				this.eidNumber.getValue();
			} catch (WrongValueException e) {
				wve.add(e);
			}

			this.eidNumber.setConstraint("");

		} else {
			// validate for pre-approval reference
			try {
				if (StringUtils.isBlank(this.preApprovedFinRef.getValue())) {
					throw new WrongValueException(this.preApprovedFinRef, Labels.getLabel("CHECK_NO_EMPTY",
							new String[] { Labels.getLabel("label_SelectFinanceTypeDialog_PreApprovedFinRef.value") }));
				}
			} catch (WrongValueException e) {
				wve.add(e);
			}
		}

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		if (newCust.isChecked()) {
			if (StringUtils.isNotBlank(this.eidNumber.getValue())) {
				String eidNum = PennantApplicationUtil.unFormatEIDNumber(this.eidNumber.getValue());

				String custCIF = this.customerDetailsService.getEIDNumberById(eidNum, "");

				if (custCIF != null) {
					String msg = Labels
							.getLabel("label_SelectFinanceTypeDialog_ProspectExist",
									new String[] {
											isRetailCustomer ? Labels.getLabel("label_CustCRCPR")
													: Labels.getLabel("label_CustTradeLicenseNumber"),
											custCIF + ". \n" });

					if (MessageUtil.confirm(msg) != MessageUtil.YES) {
						return false;
					}
					this.existingCust.setSelected(true);
					this.custCIF.setValue(custCIF);
				}
			}
		}

		logger.debug("Leaving ");
		return true;
	}

	/**
	 * Method for remove constraints of fields
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.custCtgType.setConstraint("");
		this.eidNumber.setConstraint("");
		this.finType.setConstraint("");
		this.custCIF.setConstraint("");
		this.wIfFinaceRef.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Method for clear Error messages to Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.custCtgType.setErrorMessage("");
		this.eidNumber.setErrorMessage("");
		this.finType.setErrorMessage("");
		this.custCIF.setErrorMessage("");
		this.wIfFinaceRef.setErrorMessage("");
		this.promotionCode.setErrorMessage("");
		logger.debug("Leaving");
	}

	public void onCheck$existingCust(Event event) {
		logger.debug("Entering" + event.toString());
		setCustomerRowProperties(false, false);
		Clients.clearWrongValue(this.finType);
		Clients.clearWrongValue(this.eidNumber);
		logger.debug("Leaving" + event.toString());
	}

	public void onCheck$newCust(Event event) {
		logger.debug("Entering" + event.toString());
		setCustomerRowProperties(true, false);
		Clients.clearWrongValue(this.finType);
		logger.debug("Leaving" + event.toString());
	}

	public void onCheck$preApprovedCust(Event event) {
		logger.debug("Entering" + event.toString());
		setCustomerRowProperties(true, true);
		Clients.clearWrongValue(this.finType);
		logger.debug("Leaving" + event.toString());
	}

	private void setCustomerRowProperties(boolean isNewCustomer, boolean preApproved) {

		if (preApproved) {
			this.customerRow.setVisible(false);
			this.row_custCtgType.setVisible(false);
			this.row_EIDNumber.setVisible(false);

			this.finTypeRow.setVisible(false);
			this.labelRow.setVisible(false);
			this.wIfReferenceRow.setVisible(false);
			this.preApprovedFinRefrow.setVisible(true);
			return;
		} else {
			this.finTypeRow.setVisible(true);
			this.labelRow.setVisible(true);
			this.wIfReferenceRow.setVisible(true);
			this.preApprovedFinRefrow.setVisible(false);
		}

		if (isNewCustomer) {
			this.custCIF.setValue("");
			this.custCIF.setDisabled(true);
			this.customerRow.setVisible(false);
			this.row_EIDNumber.setVisible(true);
			this.row_custCtgType.setVisible(true);
		} else {
			this.custCIF.setDisabled(false);
			this.custCIF.setMaxlength(LengthConstants.LEN_CIF);
			this.customerRow.setVisible(true);
			this.row_EIDNumber.setVisible(false);
			this.row_custCtgType.setVisible(false);
		}

		//FIXME: preApproved will be only if requestSource is Preapproved. why it is required again?
		/*
		 * if (StringUtils.equals(requestSource, FinanceConstants.FINSER_EVENT_PREAPPROVAL)) {
		 * this.labelRow.setVisible(false); this.wIfReferenceRow.setVisible(false); }
		 */}

	/**
	 * Call the Customer dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InterfaceException
	 * @throws Exception
	 */
	public CustomerDetails fetchCustomerData() throws InterruptedException, InterfaceException {
		logger.debug("Entering");

		CustomerDetails customerDetails = new CustomerDetails();
		boolean isCustFromTemp = true;
		// Get the data of Customer from Core Banking Customer
		try {
			this.custCIF.setConstraint("");
			this.custCIF.setErrorMessage("");
			this.custCIF.clearErrorMessage();
			String cif = StringUtils.trimToEmpty(this.custCIF.getValue());
			//If  customer exist is checked 
			if (this.existingCust.isChecked()) {
				Customer customer = null;
				//FIXME comment need to be removed when the version issue get resolved
				//check Customer Data in LOCAL PFF system
				//customer = this.customerDetailsService.checkCustomerByCIF(cif, TableType.TEMP_TAB.getSuffix());

				if (customer == null) {
					isCustFromTemp = false;
					customer = this.customerDetailsService.checkCustomerByCIF(cif, TableType.MAIN_TAB.getSuffix());

				}

				//Interface Core Banking System call
				if (customer == null) {
					if (ImplementationConstants.ACCOUNTS_APPLICABLE) {
						customerDetails.setNewRecord(true);
						customerDetails = this.customerInterfaceService.getCustomerInfoByInterface(cif, "");
						if (customerDetails == null) {
							throw new InterfaceException("9999", "Customer Not found.");
						}
					} else {
						throw new InterfaceException(InterfaceConstants.CUST_NOT_FOUND, "Customer Not found.");
					}
				}

				if (customer != null) {

					if (isCustFromTemp) {
						customerDetails = this.customerDetailsService.getCustomerDetailsById(customer.getId(), true,
								"_TView");
					} else {
						customerDetails = this.customerDetailsService.getCustomerDetailsById(customer.getId(), true,
								"_AView");
					}
				}
				
				
				Branch branch = this.branchService.getApprovedBranchById(getUserWorkspace().getUserDetails().getSecurityUser()
						.getUsrBranchCode());
				if (branch != null) {
					customerDetails.getCustomer().setCustSwiftBrnCode(branch.getBranchSwiftBrnCde());
				}

			} else if (this.newCust.isChecked()) {
				customerDetails = getNewCustomerDetail();
			}

		} catch (InterfaceException pfe) {
			if (StringUtils.equals(pfe.getErrorCode(), InterfaceConstants.CUST_NOT_FOUND)) {
				if (!StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_BFL)) {
					int conf = MessageUtil.confirm(Labels.getLabel("Cust_NotFound_NewCustomer"));

					if (conf == MessageUtil.YES) {
						return null;
					} else {
						customerDetails = getNewCustomerDetail();
					}
				} else {
					this.custCIF.setValue("");
					throw new WrongValueException(this.custCIF, Labels.getLabel("Cust_NotFound_System"));
				}
			} else {
				MessageUtil.showError(pfe);
				return null;
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
			customerDetails = null;
		}
		logger.debug("Leaving");
		return customerDetails;
	}

	/**
	 * Method for Preparing Prospect Customer Details
	 * 
	 * @return
	 */
	private CustomerDetails getNewCustomerDetail() {

		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setNewRecord(true);
		Customer customer = new Customer();

		CustomerStatusCode statusCode = this.customerDetailsService.getCustStatusByMinDueDays();
		customer.setCustSts(statusCode.getCustStsCode());
		customer.setLovDescCustStsName(statusCode.getCustStsDescription());
		String custCategoryType = this.custCtgType.getSelectedItem().getValue().toString();
		if (!StringUtils.equals(custCategoryType, PennantConstants.List_Select)) {
			customer.setCustCtgCode(custCategoryType);
			customer.setLovDescCustCtgCodeName(custCategoryType);
		}
		customer.setLovDescCustCtgType(custCategoryType);

		customer.setCustCIF(this.customerDetailsService.getNewProspectCustomerCIF());
		if (isRetailCustomer) {
			customer.setCustCRCPR(PennantApplicationUtil.unFormatEIDNumber(this.eidNumber.getValue()));
		} else {
			customer.setCustCRCPR(this.eidNumber.getValue());
		}

		customer.setCustBaseCcy(SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY));
		PFSParameter parameter = SysParamUtil.getSystemParameterObject("APP_LNG");
		customer.setCustLng(parameter.getSysParmValue().trim());
		customer.setLovDescCustLngName(parameter.getSysParmDescription());

		Filter[] countrysystemDefault = new Filter[1];
		countrysystemDefault[0] = new Filter("SystemDefault", "1", Filter.OP_EQUAL);
		Object countryObj = PennantAppUtil.getSystemDefault("Country", "", countrysystemDefault);

		if (countryObj != null) {
			Country country = (Country) countryObj;
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
		RelationshipOfficer officer = this.relationshipOfficerService
				.getApprovedRelationshipOfficerById(getUserWorkspace().getUserDetails().getUsername());
		if (officer != null) {
			customer.setCustRO1(officer.getROfficerCode());
			customer.setLovDescCustRO1Name(officer.getROfficerDesc());
		}

		//Setting User Branch to Customer Branch
		Branch branch = this.branchService.getApprovedBranchById(getUserWorkspace().getUserDetails().getSecurityUser()
				.getUsrBranchCode());
		if (branch != null) {
			customer.setCustDftBranch(branch.getBranchCode());
			customer.setLovDescCustDftBranchName(branch.getBranchDesc());
			customer.setCustSwiftBrnCode(branch.getBranchSwiftBrnCde());
		}

		CustomerType customerType = this.customerTypeService
				.getApprovedCustomerTypeById(PennantConstants.DEFAULT_CUST_TYPE);
		if (customerType != null) {
			customer.setCustTypeCode(customerType.getCustTypeCode());
			customer.setLovDescCustTypeCodeName(customerType.getCustTypeDesc());
		}

		CustEmployeeDetail detail = new CustEmployeeDetail();
		detail.setNewRecord(true);
		customerDetails.setCustEmployeeDetail(detail);

		Filter[] systemDefault = new Filter[1];
		systemDefault[0] = new Filter("SystemDefault", "1", Filter.OP_EQUAL);
		Object genderObj = PennantAppUtil.getSystemDefault("Gender", "", systemDefault);
		if (genderObj != null) {
			Gender gender = (Gender) genderObj;
			Filter[] saltufilters = new Filter[2];
			saltufilters[0] = new Filter("SalutationGenderCode", gender.getGenderCode(), Filter.OP_EQUAL);
			saltufilters[1] = new Filter("SystemDefault", "1", Filter.OP_EQUAL);
			Object saltuObj = PennantAppUtil.getSystemDefault("Salutation", "", saltufilters);

			customer.setCustGenderCode(gender.getGenderCode());

			if (saltuObj != null) {
				Salutation salutation = (Salutation) saltuObj;
				customer.setCustSalutationCode(salutation.getSalutationCode());
			}
		}
		customer.setSalariedCustomer(true);

		Object salutionObj = PennantAppUtil.getSystemDefault("MaritalStatusCode", "", systemDefault);
		if (salutionObj != null) {
			MaritalStatusCode maritalStatusCode = (MaritalStatusCode) salutionObj;
			customer.setCustMaritalSts(maritalStatusCode.getMaritalStsCode());
		}
		customerDetails.setCustomer(customer);

		// when WIF reference is selected
		if (StringUtils.isNotBlank(this.wIfFinaceRef.getValue())) {
			WIFCustomer wCustomer = getWifCustomers(false,
					isRetailCustomer ? PennantApplicationUtil.unFormatEIDNumber(this.eidNumber.getValue())
							: this.eidNumber.getValue());
			processWIFCustomerData(wCustomer, customerDetails);
		}
		return customerDetails;

	}

	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) throws InterruptedException {
		logger.debug("Entering");
		this.custCIF.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;

		Customer customer = (Customer) nCustomer;
		if (customer != null) {
			this.custCIF.setValue(customer.getCustCIF());
		} else {
			this.custCIF.setValue("");
		}
		logger.debug("Leaving ");
	}

	public void onChange$eidNumber(Event event) {
		logger.debug("Entering" + event.toString());
		if (!StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_BFL)) {
			if (isRetailCustomer) {
				this.eidNumber.setValue(PennantApplicationUtil.formatEIDNumber(this.eidNumber.getValue()));
			} else {
				this.eidNumber.setValue(this.eidNumber.getValue());
			}
		} else {
			this.eidNumber.setValue(this.eidNumber.getValue());

		}

		logger.debug("Leaving" + event.toString());
	}

	public void onChange$custCtgType(Event event) {
		logger.debug("Entering" + event.toString());
		changeCustCtgType();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Changing Customer Category Code
	 */
	private void changeCustCtgType() {
		logger.debug("Entering");
		isRetailCustomer = this.custCtgType.getSelectedItem().getValue().toString()
				.equals(PennantConstants.PFF_CUSTCTG_INDIV);
		Clients.clearWrongValue(this.eidNumber);
		Clients.clearWrongValue(this.finType);
		this.eidNumber.setValue("");
		if (!StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_BFL)) {
			this.label_SelectFinanceTypeDialog_EIDNumber.setValue(Labels
					.getLabel("label_CoreCustomerDialog_EIDNumber.value"));
			this.eidNumber.setMaxlength(LengthConstants.LEN_EID);
		} else {
			this.label_SelectFinanceTypeDialog_EIDNumber.setValue(Labels
					.getLabel("label_CoreCustomerDialog_TradeLicenseNumber.value"));
		}

		logger.debug("Leaving");
	}

	/**
	 * When user clicks on button "customerId Search" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug("Leaving " + event.toString());
	}

	// Getters and Setters

	public void setFinanceMainListCtrl(FinanceMainListCtrl financeMainListCtrl) {
		this.financeMainListCtrl = financeMainListCtrl;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	public void setFinanceWorkFlow(FinanceWorkFlow financeWorkFlow) {
		this.financeWorkFlow = financeWorkFlow;
	}

	public void setFinanceTypeService(FinanceTypeService financeTypeService) {
		this.financeTypeService = financeTypeService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setStepPolicyService(StepPolicyService stepPolicyService) {
		this.stepPolicyService = stepPolicyService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public void setRelationshipOfficerService(RelationshipOfficerService relationshipOfficerService) {
		this.relationshipOfficerService = relationshipOfficerService;
	}

	public void setCustomerInterfaceService(CustomerInterfaceService customerInterfaceService) {
		this.customerInterfaceService = customerInterfaceService;
	}

	public void setBranchService(BranchService branchService) {
		this.branchService = branchService;
	}

	public void setCustomerTypeService(CustomerTypeService customerTypeService) {
		this.customerTypeService = customerTypeService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public PromotionService getPromotionService() {
		return promotionService;
	}

	public void setPromotionService(PromotionService promotionService) {
		this.promotionService = promotionService;
	}
}
