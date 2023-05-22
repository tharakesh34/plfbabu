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
 * * FileName : SelectCDFinanceSchemeDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 16-11-2011 *
 * * Modified Date : 16-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 16-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.financemain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.customermasters.CustEmployeeDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.financemanagement.FinTypeVASProducts;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.rmtmasters.CustomerType;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.Gender;
import com.pennant.backend.model.systemmasters.MaritalStatusCode;
import com.pennant.backend.model.systemmasters.Salutation;
import com.pennant.backend.service.applicationmaster.BranchService;
import com.pennant.backend.service.applicationmaster.RelationshipOfficerService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.service.rmtmasters.CustomerTypeService;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.service.rmtmasters.PromotionService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTMobileNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.dedup.dedupparm.FetchCustomerDedupDetails;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.external.CustomerDedupCheckService;
import com.pennanttech.pff.external.CustomerInterfaceService;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/SelectCDFinanceSchemeDialog.zul file.
 */
public class SelectCDFinanceSchemeDialogCtrl extends GFCBaseCtrl<FinanceDetail> {
	private static final long serialVersionUID = 8556168885363682933L;
	private static final Logger logger = LogManager.getLogger(SelectCDFinanceSchemeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWiredd by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SelectCDFinanceSchemeDialog;

	protected ExtendedCombobox finType;
	protected ExtendedCombobox promotionCode;
	protected Button btnSearchCustCIF;
	protected Radio newCust;
	protected Radio existingCust;
	protected Textbox custCIF;
	protected Combobox custCtgType;
	protected Uppercasebox eidNumber;
	protected Space space_EIDNumber;
	protected Label label_SelectCDFinanceSchemeDialog_EIDNumber;
	protected Label label_SelectCDFinanceSchemeDialog_MobileNo;
	protected Space space_mobileNo;
	protected Textbox mobileNo;
	protected Button btnProceed;

	protected Row promotionCodeRow;
	protected Row customerRow;
	protected Row row_selectCustomer;
	protected Row row_EIDNumber;
	protected Row row_MobileNumber;
	protected Row finTypeRow;
	protected Row row_custCtgType;

	protected FinanceMainListCtrl financeMainListCtrl; // over handed parameter
	protected transient FinanceWorkFlow financeWorkFlow;
	private transient WorkFlowDetails workFlowDetails = null;
	private List<String> userRoleCodeList = new ArrayList<String>();

	private transient FinanceTypeService financeTypeService;
	private transient PromotionService promotionService;
	private transient FinanceWorkFlowService financeWorkFlowService;
	private transient FinanceDetailService financeDetailService;
	private transient CustomerDetailsService customerDetailsService;
	protected JdbcSearchObject<Customer> custCIFSearchObject;
	private RelationshipOfficerService relationshipOfficerService;
	private BranchService branchService;
	private CustomerTypeService customerTypeService;
	private com.pennant.Interface.service.CustomerInterfaceService customerInterfaceService;

	@Autowired(required = false)
	private CustomerDedupCheckService customerDedupService;
	@Autowired(required = false)
	private CustomerInterfaceService customerExternalInterfaceService;

	private String menuItemRightName = null;
	private boolean isRetailCustomer = true;
	private boolean isNewCustomer = true;
	private String isPANMandatory = "";
	private long promotionSeqId = 0;

	// Properties related to primary identity.
	private String primaryIdLabel;
	private String primaryIdRegex;
	private boolean primaryIdMandatory;

	/**
	 * default constructor.<br>
	 */
	public SelectCDFinanceSchemeDialogCtrl() {
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
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_SelectCDFinanceSchemeDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SelectCDFinanceSchemeDialog);

		// READ OVERHANDED parameters !
		if (arguments.containsKey("financeMainListCtrl")) {
			this.financeMainListCtrl = (FinanceMainListCtrl) arguments.get("financeMainListCtrl");
			setFinanceMainListCtrl(this.financeMainListCtrl);
		} else {
			setFinanceMainListCtrl(null);
		}

		if (arguments.containsKey("menuItemRightName")) {
			this.menuItemRightName = (String) arguments.get("menuItemRightName");
		}

		if (arguments.containsKey("role")) {
			userRoleCodeList = (ArrayList<String>) arguments.get("role");
		}

		doSetFieldProperties();
		fillComboBox(custCtgType, "", PennantAppUtil.getcustCtgCodeList(), "");

		showSelectCDFinanceSchemeDialog();

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// String whereClause = getWhereClauseWithFirstTask();

		// Finance Type
		this.finType.setReadonly(true);

		// Promotion Code
		this.promotionCode.setMaxlength(LengthConstants.LEN_MASTER_CODE);
		this.promotionCode.setModuleName("CDScheme");
		this.promotionCode.setValueColumn("PromotionCode");
		this.promotionCode.setDescColumn("PromotionDesc");
		this.promotionCode.setValidateColumns(new String[] { "PromotionCode" });

		Filter[] filters = new Filter[4];
		Date appDate = SysParamUtil.getAppDate();
		filters[0] = new Filter("StartDate", appDate, Filter.OP_LESS_OR_EQUAL);
		filters[1] = new Filter("EndDate", appDate, Filter.OP_GREATER_OR_EQUAL);
		filters[2] = new Filter("Active", 1, Filter.OP_EQUAL);
		filters[3] = new Filter("ReferenceID", 0, Filter.OP_NOT_EQUAL);
		this.promotionCode.setFilters(filters);

		/*
		 * if (!"".equals(whereClause)) { this.promotionCode.setWhereClause(whereClause); }
		 */

		if (!StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_BFL)) {
			this.eidNumber.setMaxlength(LengthConstants.LEN_EID);
		} else {
			this.eidNumber.setMaxlength(LengthConstants.LEN_PAN);
		}

		this.mobileNo.setMaxlength(LengthConstants.LEN_MOBILE);

		logger.debug("Leaving");
	}

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
				if (App.DATABASE == Database.ORACLE || App.DATABASE == Database.POSTGRES) {
					whereClause.append("||");
				} else {
					whereClause.append("+");
				}
				whereClause.append(" LovDescFirstTaskOwner ");
				if (App.DATABASE == Database.ORACLE || App.DATABASE == Database.POSTGRES) {
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

		if (dataObject == null) {
			this.promotionCode.setValue("");
			this.promotionCode.setDescription("");
			this.promotionSeqId = 0;
			CheckScreenCode("");
		} else if (dataObject instanceof String) {
			this.promotionCode.setValue(dataObject.toString());
			this.promotionCode.setDescription("");
			this.promotionSeqId = 0;
			CheckScreenCode("");
		} else {
			Promotion details = (Promotion) dataObject;
			if (details != null) {

				this.promotionCode.setValue(details.getPromotionCode());
				this.promotionCode.setDescription(details.getPromotionDesc());
				this.finType.setValue(details.getFinType());
				this.finType.setDescription(details.getFinTypeDesc());
				this.promotionSeqId = details.getReferenceID();
				CheckScreenCode("");
			}
		}

		logger.debug("Leaving " + event.toString());
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
	 * When user clicks on button "btnProceed" button
	 * 
	 * @param event
	 */
	public void onClick$btnProceed(Event event) {
		logger.debug(Literal.ENTERING);

		doFieldValidation();
		if (checkDedup()) {
			logger.debug(Literal.LEAVING);
			return;
		}

		processCustomer(isRetailCustomer, isNewCustomer);

		logger.debug(Literal.LEAVING);
	}

	protected boolean processCustomer(boolean isRetail, boolean isNewCustomer) {
		FinanceDetail financeDetail = null;

		// Customer Data Fetching
		CustomerDetails customerDetails = new CustomerDetails();

		// Customer Data Fetching
		customerDetails = fetchCustomerData(isRetail);
		if (this.newCust.isChecked()) {
			CustomerPhoneNumber customerPhoneNumber = new CustomerPhoneNumber();
			customerPhoneNumber.setPhoneNumber(this.mobileNo.getValue());
			customerPhoneNumber.setPhoneTypeCode(PennantConstants.PHONETYPE_MOBILE);
			customerPhoneNumber.setPhoneTypePriority(Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH));
			customerPhoneNumber.setNewRecord(true);
			customerPhoneNumber.setRecordType(PennantConstants.RCD_ADD);
			List<CustomerPhoneNumber> customerPhoneNumList = new ArrayList<>();
			customerPhoneNumList.add(customerPhoneNumber);
			customerDetails.setCustomerPhoneNumList(customerPhoneNumList);

		}

		// Check Customer Details exists with entered data or not
		if (customerDetails == null) {
			logger.debug(Literal.LEAVING);
			return false;
		}

		financeDetail = this.financeDetailService.getNewFinanceDetail(false);
		FinanceDetail befImage = new FinanceDetail();
		financeDetail.setBefImage(befImage);

		// Resetting Finance Event based on Request Resource
		String financeEvent = FinServiceEvent.ORG;

		// If User requested through What-if Reference
		FinanceType financeType = null;

		String fintype = this.finType.getValue().trim();

		// Fetching Finance Type Details
		financeType = this.financeTypeService.getOrgFinanceTypeById(fintype);
		Promotion promotion = null;

		// Fetching Promotion Details
		if (StringUtils.isNotBlank(promotionCode.getValue())) {
			promotion = this.promotionService.getPromotionByReferenceId(promotionSeqId,
					FinanceConstants.MODULEID_PROMOTION);

		}

		financeDetail.getFinScheduleData().setFinanceType(financeType);
		FinanceMain finMain = financeDetailService.setDefaultFinanceMain(new FinanceMain(), financeType);
		if (promotion != null) {
			finMain.setPromotionCode(promotion.getPromotionCode());
			finMain.setPromotionSeqId(promotion.getReferenceID());
			finMain.setRepayProfitRate(promotion.getActualInterestRate());
			finMain.setProfitDaysBasis(promotion.getPftDaysBasis());
			finMain.setNumberOfTerms(promotion.getTenor());
			finMain.setRepayProfitRate(promotion.getActualInterestRate());
			finMain.setRpyMinRate(promotion.getFinMinRate());
			finMain.setRpyMaxRate(promotion.getFinMaxRate());
			financeDetail.getFinScheduleData().setPromotion(promotion);
		}

		FinODPenaltyRate finOdPenalty = financeDetailService.setDefaultODPenalty(new FinODPenaltyRate(), financeType);
		financeDetail.getFinScheduleData().setFinanceMain(finMain);
		financeDetail.getFinScheduleData().setFinODPenaltyRate(finOdPenalty);

		// Workflow Details Verification and initiation, if not found
		if (this.financeWorkFlow == null) {
			FinanceWorkFlow financeWorkFlow = this.financeWorkFlowService.getApprovedFinanceWorkFlowById(
					financeType.getFinType(), financeEvent, PennantConstants.WORFLOW_MODULE_FINANCE);
			setFinanceWorkFlow(financeWorkFlow);
		}

		try {
			// Fetch & set Default statuses f
			if (financeDetail.getFinScheduleData().getFinanceMain() != null) {
				financeDetail.getFinScheduleData().getFinanceMain().setFinStsReason(FinanceConstants.FINSTSRSN_SYSTEM);
				financeDetail.getFinScheduleData().getFinanceMain().setFinStatus(FinanceConstants.FINSTSRSN_SYSTEM);
			}
		} catch (Exception e) {
			logger.debug(e);
		}

		// Workflow Details Setup
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

			Date appDate = SysParamUtil.getAppDate();

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
					vasRecording.setValueDate(appDate);
					vasRecording.setAccrualTillDate(appDate);
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

		// Fetching Finance Reference Detail
		if (isWorkFlowEnabled()) {
			financeDetail = this.financeDetailService.getFinanceReferenceDetails(financeDetail, getRole(),
					this.financeWorkFlow.getScreenCode(), "", financeEvent, true);
		}

		if (financeDetail.isLegalInitiator()) {
			financeDetail.getFinScheduleData().getFinanceMain().setLegalRequired(true);
		}

		Date finStartDate = financeDetail.getFinScheduleData().getFinanceMain().getFinStartDate();
		if (finStartDate != null) {
			String finEvent = PennantApplicationUtil.getEventCode(finStartDate);
			financeDetail.getFinScheduleData().setFeeEvent(finEvent);

			// Fee Details Fetching From Promotion/Scheme
			financeDetail.setFinTypeFeesList(this.financeDetailService.getSchemeFeesList(finMain.getPromotionSeqId(),
					finEvent, true, FinanceConstants.MODULEID_PROMOTION));

		}

		if (this.newCust.isChecked() && isNewCustomer) {
			CustomerDedup customerDedup = doSetCustomerDedup(customerDetails);
			String type = financeDetail.getFinScheduleData().getFinanceMain().getFinType();
			String curLoginUser = getUserWorkspace().getUserDetails().getSecurityUser().getUsrLogin();
			List<CustomerDedup> customerDedupList = FetchCustomerDedupDetails.fetchCustomerDedupDetails(getRole(),
					customerDedup, curLoginUser, type);
			customerDetails.setCustomerDedupList(customerDedupList);
			financeDetail.setCustomerDedupList(customerDedupList);
		}

		// Business Vertical Tagged with Loan
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		SecurityUser user = getUserWorkspace().getUserDetails().getSecurityUser();
		financeMain.setBusinessVertical(user.getBusinessVertical());
		financeMain.setBusinessVerticalCode(user.getBusinessVerticalCode());
		financeMain.setBusinessVerticalDesc(user.getBusinessVerticalDesc());

		showDetailView(financeDetail);
		return true;
	}

	// GUI Process
	private void showDetailView(FinanceDetail financeDetail) {
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them with a object of the selected item. For
		 * handed over these parameter only a Map is accepted. So we put the object in a HashMap.
		 */
		if (isWorkFlowEnabled()) {

			StringBuilder fileLocation = new StringBuilder("/WEB-INF/pages/Finance/Cd/");
			/*
			 * if screen code is quick data entry (QDE) navigate to QDE screen otherwise navigate to Detail data entry
			 * screen
			 */
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("financeDetail", financeDetail);
			map.put("financeMainListCtrl", this.financeMainListCtrl);
			map.put("menuItemRightName", menuItemRightName);

			// Customer dedup
			if (financeDetail.getCustomerDedupList() != null && !financeDetail.getCustomerDedupList().isEmpty()) {
				map.put("isFromLoan", true);
				map.put("isInternalDedupLoan", true);
				map.put("customerDetails", financeDetail.getCustomerDetails());
				map.put("SelectCDFinanceSchemeDialogCtrl", this);
				Executions.createComponents("/WEB-INF/pages/Finance/CustomerDedUp/CustomerDedupDialog.zul", null, map);
			}

			if (financeDetail.getCustomerDedupList() == null || financeDetail.getCustomerDedupList().isEmpty()) {
				fileLocation.append("CDFinanceMainDialog.zul");
			} else {
				MessageUtil.showError(
						Labels.getLabel("message.error.productNotFound", new String[] { FinanceConstants.PRODUCT_CD }));
				return;
			}

			// call the ZUL-file with the parameters packed in a map
			try {
				if (financeDetail.getCustomerDedupList() == null || financeDetail.getCustomerDedupList().isEmpty()) {
					Executions.createComponents(fileLocation.toString(), null, map);
					this.window_SelectCDFinanceSchemeDialog.onClose();
				}
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		} else {
			logger.fatal("work flow not found");
			MessageUtil.showError(Labels.getLabel("Workflow_Not_Found")
					+ financeDetail.getFinScheduleData().getFinanceMain().getFinType());
		}
	}

	/**
	 * Opens the SelectCDFinanceSchemeDialog window modal.
	 */
	private void showSelectCDFinanceSchemeDialog() {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_SelectCDFinanceSchemeDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doFieldValidation() {
		logger.debug("Entering ");
		doClearMessage();
		doRemoveValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (StringUtils.isBlank(this.finType.getValue()) && StringUtils.isNotBlank(this.promotionCode.getValue())) {
				throw new WrongValueException(this.finType, Labels.getLabel("CHECK_NO_EMPTY",
						new String[] { Labels.getLabel("label_SelectCDFinanceSchemeDialog_FinType.value") }));
			}
		} catch (WrongValueException e) {
			wve.add(e);
		}

		try {
			if (this.existingCust.isChecked()) {
				if (StringUtils.isEmpty(this.custCIF.getValue())) {
					throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_EMPTY",
							new String[] { Labels.getLabel("label_SelectCDFinanceSchemeDialog_CustCIF.value") }));
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
							new String[] { Labels.getLabel("label_SelectCDFinanceSchemeDialog_CustCtg.value") }));
				}
			} catch (WrongValueException e) {
				wve.add(e);
			}

			eidNumber.setConstraint(
					new PTStringValidator(Labels.getLabel(primaryIdLabel), primaryIdRegex, primaryIdMandatory));

			this.mobileNo.setConstraint(new PTMobileNumberValidator(
					Labels.getLabel("label_SelectCDFinanceSchemeDialog_MobileNo.value"), true));

		}
		try {
			this.eidNumber.getValue();
		} catch (WrongValueException e) {
			wve.add(e);
		}

		this.eidNumber.setConstraint("");

		// Mobile Number Validation
		try {
			this.mobileNo.getValue();
		} catch (WrongValueException e) {
			wve.add(e);
		}

		this.mobileNo.setConstraint("");

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving ");
	}

	private boolean checkDedup() {
		if (newCust.isChecked()) {
			String primaryId = eidNumber.getValue();

			// Check whether the primary identity exists in PLF.
			/*
			 * String cif = customerDetailsService.getEIDNumberById(primaryId, "");
			 * 
			 * if (cif != null) { String msg = Labels.getLabel("label_SelectCDFinanceSchemeDialog_ProspectExist", new
			 * String[] { isRetailCustomer ? Labels.getLabel(primaryIdLabel) : Labels.getLabel(primaryIdLabel), cif +
			 * ". \n" });
			 * 
			 * // The user doesn't want to proceed with duplicate found. if (MessageUtil.confirm(msg) !=
			 * MessageUtil.YES) { return true; }
			 * 
			 * existingCust.setSelected(true); custCIF.setValue(cif);
			 * 
			 * return false; }
			 */

			// Check the de-duplication in external CRM.
			try {
				if ("Y".equals(SysParamUtil.getValueAsString("EXT_CRM_INT_ENABLED")) && customerDedupService != null)
					return !checkExternalDedup(primaryId);
			} catch (Exception e) {
				MessageUtil.showError(e);

				return true;
			}
		}

		return false;
	}

	private boolean checkExternalDedup(String primaryId) {
		CustomerDetails customerDetails = new CustomerDetails();
		CustomerDedup custDedup = new CustomerDedup();
		String primaryIDType = null;

		if (isRetailCustomer) {
			primaryIDType = SysParamUtil.getValueAsString("CUST_PRIMARY_ID_RETL");
		} else {
			primaryIDType = SysParamUtil.getValueAsString("CUST_PRIMARY_ID_CORP");

		}

		if ("PAN".equals(primaryIDType)) {
			custDedup.setPanNumber(primaryId);
		} else if ("AADHAAR".equals(primaryIDType)) {
			custDedup.setAadharNumber(primaryId);
		} else {
			custDedup.setCustCRCPR(primaryId);
		}

		List<CustomerDedup> customerDedupList = null;
		try {
			customerDedupList = customerDedupService.invokeDedup(custDedup);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			throw e;
		}

		if (customerDedupList != null && !customerDedupList.isEmpty()) {
			customerDetails.setCustomerDedupList(customerDedupList);
			customerDetails.getCustomer().setCustCRCPR(this.eidNumber.getValue());
			showDetailViewforDedUp(customerDetails);
			return false;

		}

		return true;
	}

	private void showDetailViewforDedUp(CustomerDetails customerDetails) {
		final Map<String, Object> map = new HashMap<String, Object>();

		// call the ZUL-file with the parameters packed in a map
		try {
			map.put("parentWindow", window_SelectCDFinanceSchemeDialog);
			map.put("customerDetails", customerDetails);
			map.put("SelectCDFinanceSchemeDialogCtrl", this);
			map.put("isFromLoan", true);
			Executions.createComponents("/WEB-INF/pages/Finance/CustomerDedUp/CustomerDedupDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	/**
	 * Method for remove constraints of fields
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.custCtgType.setConstraint("");
		this.eidNumber.setConstraint("");
		this.mobileNo.setConstraint("");
		this.finType.setConstraint("");
		this.promotionCode.setConstraint("");
		this.custCIF.setConstraint("");
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
		this.mobileNo.setErrorMessage("");
		this.finType.setErrorMessage("");
		this.custCIF.setErrorMessage("");
		this.promotionCode.setErrorMessage("");
		logger.debug("Leaving");
	}

	public void onCheck$existingCust(Event event) {
		logger.debug("Entering" + event.toString());
		setCustomerRowProperties(false);
		Clients.clearWrongValue(this.finType);
		Clients.clearWrongValue(this.eidNumber);
		Clients.clearWrongValue(this.mobileNo);
		Clients.clearWrongValue(this.promotionCode);
		logger.debug("Leaving" + event.toString());
	}

	public void onCheck$newCust(Event event) {
		logger.debug("Entering" + event.toString());
		setCustomerRowProperties(true);
		Clients.clearWrongValue(this.finType);
		Clients.clearWrongValue(this.promotionCode);
		logger.debug("Leaving" + event.toString());
	}

	private void setCustomerRowProperties(boolean isNewCustomer) {

		this.finTypeRow.setVisible(true);

		if (isNewCustomer) {
			this.custCIF.setValue("");
			this.custCIF.setDisabled(true);
			this.customerRow.setVisible(false);
			this.row_EIDNumber.setVisible(true);
			this.row_MobileNumber.setVisible(true);
			this.row_custCtgType.setVisible(true);
		} else {
			this.custCIF.setDisabled(false);
			this.customerRow.setVisible(true);
			this.row_EIDNumber.setVisible(false);
			this.row_MobileNumber.setVisible(false);
			this.row_custCtgType.setVisible(false);
		}

	}

	/**
	 * Call the Customer dialog with a new empty entry. <br>
	 * 
	 * @param event
	 */
	public CustomerDetails fetchCustomerData(boolean isRetail) {
		logger.debug("Entering");

		CustomerDetails customerDetails = new CustomerDetails();
		boolean isCustFromTemp = true;
		// Get the data of Customer from Core Banking Customer
		try {
			this.custCIF.setConstraint("");
			this.custCIF.setErrorMessage("");
			this.custCIF.clearErrorMessage();
			String cif = StringUtils.trimToEmpty(this.custCIF.getValue());
			// If customer exist is checked
			if (this.existingCust.isChecked()) {
				Customer customer = null;
				// FIXME comment need to be removed when the version issue get resolved
				// check Customer Data in LOCAL PFF system
				// customer = this.customerDetailsService.checkCustomerByCIF(cif, TableType.TEMP_TAB.getSuffix());

				if (customer == null) {
					isCustFromTemp = false;
					customer = this.customerDetailsService.checkCustomerByCIF(cif, TableType.MAIN_TAB.getSuffix());

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

				// Interface Core Banking System call CRM
				if (customer == null && "Y".equals(SysParamUtil.getValueAsString("EXT_CRM_INT_ENABLED"))
						&& customerExternalInterfaceService != null) {
					customerDetails.setNewRecord(true);
					customer = new Customer();
					customer.setCustCoreBank(cif);
					if (isRetail) {
						customer.setCustCtgCode("RETAIL");
					} else {
						customer.setCustCtgCode("CORP");
					}
					customerDetails = customerExternalInterfaceService.getCustomerDetail(customer);
					getNewCustomerDetail(customerDetails);

					if (customerDetails == null) {
						throw new InterfaceException("9999", "Customer Not found.");

					}

				}

			} else if (this.newCust.isChecked()) {
				customerDetails = getNewCustomerDetail(customerDetails);
			}

		} catch (InterfaceException pfe) {
			if (StringUtils.equals(pfe.getErrorCode(), "----")) {
				if (!StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_BFL)) {
					int conf = MessageUtil.confirm(Labels.getLabel("Cust_NotFound_NewCustomer"));

					if (conf == MessageUtil.YES) {
						return null;
					} else {
						customerDetails = getNewCustomerDetail(customerDetails);
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
	private CustomerDetails getNewCustomerDetail(CustomerDetails customerDetails) {
		customerDetails.setNewRecord(true);
		Customer customer = customerDetails.getCustomer();

		CustomerStatusCode statusCode = this.customerDetailsService.getCustStatusByMinDueDays();

		if (customerDetails.getCustomer().getCustSts() == null
				&& customerDetails.getCustomer().getLovDescCustStsName() == null && statusCode != null) {
			customer.setCustSts(statusCode.getCustStsCode());
			customer.setLovDescCustStsName(statusCode.getCustStsDescription());
		}

		String custCategoryType = this.custCtgType.getSelectedItem().getValue().toString();
		if (!StringUtils.equals(custCategoryType, PennantConstants.List_Select)
				&& customerDetails.getCustomer().getCustCtgCode() == null
				&& customerDetails.getCustomer().getLovDescCustCtgCodeName() == null) {
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

		if (customerDetails.getCustomer().getCustBaseCcy() == null) {
			customer.setCustBaseCcy(SysParamUtil.getAppCurrency());
		}

		PFSParameter parameter = SysParamUtil.getSystemParameterObject("APP_LNG");

		if (customerDetails.getCustomer().getCustLng() == null) {
			customer.setCustLng(parameter.getSysParmValue().trim());
			customer.setLovDescCustLngName(parameter.getSysParmDescription());
		}

		Filter[] countrysystemDefault = new Filter[1];
		countrysystemDefault[0] = new Filter("SystemDefault", 1, Filter.OP_EQUAL);
		Object countryObj = PennantAppUtil.getSystemDefault("Country", "", countrysystemDefault);

		if (countryObj != null) {
			Country country = (Country) countryObj;

			if (customerDetails.getCustomer().getCustCOB() == null) {
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
		}

		// Setting Primary Relation Ship Officer
		RelationshipOfficer officer = this.relationshipOfficerService
				.getApprovedRelationshipOfficerById(getUserWorkspace().getUserDetails().getUsername());

		if (officer != null && String.valueOf(customerDetails.getCustomer().getCustRO1()) == null) {
			customer.setCustRO1(Long.parseLong(officer.getROfficerCode()));
			customer.setLovDescCustRO1Name(officer.getROfficerDesc());
		}

		// Setting User Branch to Customer Branch
		Branch branch = this.branchService
				.getApprovedBranchById(getUserWorkspace().getUserDetails().getSecurityUser().getUsrBranchCode());

		if (branch != null && customerDetails.getCustomer().getCustDftBranch() == null) {
			customer.setCustDftBranch(branch.getBranchCode());
			customer.setLovDescCustDftBranchName(branch.getBranchDesc());
			customer.setCustSwiftBrnCode(branch.getBranchSwiftBrnCde());
		}

		CustomerType customerType = this.customerTypeService
				.getApprovedCustomerTypeById(PennantConstants.DEFAULT_CUST_TYPE);
		if (customerType != null && customerDetails.getCustomer().getCustTypeCode() == null) {
			customer.setCustTypeCode(customerType.getCustTypeCode());
			customer.setLovDescCustTypeCodeName(customerType.getCustTypeDesc());
		}

		CustEmployeeDetail detail = new CustEmployeeDetail();
		detail.setNewRecord(true);
		customerDetails.setCustEmployeeDetail(detail);

		Filter[] systemDefault = new Filter[1];
		systemDefault[0] = new Filter("SystemDefault", 1, Filter.OP_EQUAL);
		Object genderObj = PennantAppUtil.getSystemDefault("Gender", "", systemDefault);
		if (genderObj != null && customerDetails.getCustomer().getCustGenderCode() == null) {
			Gender gender = (Gender) genderObj;
			Filter[] saltufilters = new Filter[2];
			saltufilters[0] = new Filter("SalutationGenderCode", gender.getGenderCode(), Filter.OP_EQUAL);
			saltufilters[1] = new Filter("SystemDefault", 1, Filter.OP_EQUAL);
			Object saltuObj = PennantAppUtil.getSystemDefault("Salutation", "", saltufilters);

			customer.setCustGenderCode(gender.getGenderCode());

			if (saltuObj != null) {
				Salutation salutation = (Salutation) saltuObj;
				customer.setCustSalutationCode(salutation.getSalutationCode());
			}
		}
		customer.setSalariedCustomer(true);

		Object salutionObj = PennantAppUtil.getSystemDefault("MaritalStatusCode", "", systemDefault);
		if (salutionObj != null && customerDetails.getCustomer().getCustMaritalSts() == null) {
			MaritalStatusCode maritalStatusCode = (MaritalStatusCode) salutionObj;
			customer.setCustMaritalSts(maritalStatusCode.getMaritalStsCode());
		}
		customerDetails.setCustomer(customer);

		return customerDetails;

	}

	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
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

		isPANMandatory = SysParamUtil.getValueAsString(SMTParameterConstants.PANCARD_REQ);
		if (StringUtils.equals("Y", isPANMandatory)) {
			this.space_EIDNumber.setSclass(PennantConstants.mandateSclass);
		} else {
			this.space_EIDNumber.setSclass("");
		}

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
		this.mobileNo.setValue("");
		// ### 01-05-2018 Tuleapp id #360
		doSetPrimaryIdAttributes();

		logger.debug("Leaving");
	}

	private void doSetPrimaryIdAttributes() {
		Map<String, String> attributes = PennantApplicationUtil.getPrimaryIdAttributes(getComboboxValue(custCtgType));

		primaryIdLabel = attributes.get("LABEL");
		primaryIdMandatory = Boolean.valueOf(attributes.get("MANDATORY"));
		primaryIdRegex = attributes.get("REGEX");
		int maxLength = Integer.valueOf(attributes.get("LENGTH"));

		label_SelectCDFinanceSchemeDialog_EIDNumber.setValue(Labels.getLabel(primaryIdLabel));
		space_EIDNumber.setSclass(primaryIdMandatory ? PennantConstants.mandateSclass : "");
		eidNumber.setSclass(PennantConstants.mandateSclass);
		eidNumber.setValue("");
		eidNumber.setMaxlength(maxLength);

		space_mobileNo.setSclass(primaryIdMandatory ? PennantConstants.mandateSclass : "");
		mobileNo.setSclass(PennantConstants.mandateSclass);
		mobileNo.setValue("");
	}

	/**
	 * When user clicks on button "customerId Search" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug("Leaving " + event.toString());
	}

	public CustomerDedup doSetCustomerDedup(CustomerDetails customerDetails) {

		String mobileNumber = "";
		Customer customer = customerDetails.getCustomer();
		if (customerDetails.getCustomerPhoneNumList() != null) {
			for (CustomerPhoneNumber custPhone : customerDetails.getCustomerPhoneNumList()) {
				if (custPhone.getPhoneTypeCode().equals(PennantConstants.PHONETYPE_MOBILE)) {
					mobileNumber = custPhone.getPhoneNumber();
					break;
				}
			}
		}

		List<CustomerDocument> customerDocumentsList = customerDetails.getCustomerDocumentsList();
		if (customerDocumentsList != null) {
			for (CustomerDocument curCustDocument : customerDocumentsList) {
				if (StringUtils.equals(curCustDocument.getCustDocCategory(), PennantConstants.PANNUMBER)) {
					customerDetails.getCustomer().setCustCRCPR(curCustDocument.getCustDocTitle());
				}
			}
		}

		CustomerDedup customerDedup = new CustomerDedup();
		if (customer != null) {
			customerDedup.setCustCIF(customer.getCustCIF());
			customerDedup.setCustFName(customer.getCustFName());
			customerDedup.setCustLName(customer.getCustLName());
			customerDedup.setCustShrtName(customer.getCustShrtName());
			customerDedup.setCustDOB(customer.getCustDOB());
			customerDedup.setCustCRCPR(customer.getCustCRCPR());
			customerDedup.setPanNumber(customer.getCustCRCPR());
			customerDedup.setCustCtgCode(customer.getCustCtgCode());
			customerDedup.setCustDftBranch(customer.getCustDftBranch());
			customerDedup.setCustSector(customer.getCustSector());
			customerDedup.setCustSubSector(customer.getCustSubSector());
			customerDedup.setCustNationality(customer.getCustNationality());
			customerDedup.setCustPassportNo(customer.getCustPassportNo());
			customerDedup.setCustTradeLicenceNum(customer.getCustTradeLicenceNum());
			customerDedup.setCustVisaNum(customer.getCustVisaNum());
			customerDedup.setMobileNumber(mobileNumber);
			customerDedup.setCustPOB(customer.getCustPOB());
			customerDedup.setCustResdCountry(customer.getCustResdCountry());
			customerDedup.setCustEMail(customer.getEmailID());
		}
		return customerDedup;

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

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public void setRelationshipOfficerService(RelationshipOfficerService relationshipOfficerService) {
		this.relationshipOfficerService = relationshipOfficerService;
	}

	public void setCustomerInterfaceService(
			com.pennant.Interface.service.CustomerInterfaceService customerInterfaceService) {
		this.customerInterfaceService = customerInterfaceService;
	}

	public void setBranchService(BranchService branchService) {
		this.branchService = branchService;
	}

	public void setCustomerTypeService(CustomerTypeService customerTypeService) {
		this.customerTypeService = customerTypeService;
	}

	public PromotionService getPromotionService() {
		return promotionService;
	}

	public void setPromotionService(PromotionService promotionService) {
		this.promotionService = promotionService;
	}

	public long getPromotionSeqId() {
		return promotionSeqId;
	}

	public void setPromotionSeqId(long promotionSeqId) {
		this.promotionSeqId = promotionSeqId;
	}
}
