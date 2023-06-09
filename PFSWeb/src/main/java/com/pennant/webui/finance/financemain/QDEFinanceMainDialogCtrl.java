/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright hvaer, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : QDEFinanceMainDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 * *
 * Modified Date : 12-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.financemain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.SplRateCode;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.mandate.MandateUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTPhoneNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.dedup.dedupparm.FetchBlackListDetails;
import com.pennant.webui.dedup.dedupparm.FetchDedupDetails;
import com.pennant.webui.dedup.dedupparm.FetchFinCustomerDedupDetails;
import com.pennant.webui.finance.payorderissue.DisbursementInstCtrl;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.overdue.constants.ChargeType;
import com.pennapps.core.util.ObjectUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/QDEFinanceMainDialog.zul file.
 */
public class QDEFinanceMainDialogCtrl extends FinanceBaseCtrl<FinanceMain> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = LogManager.getLogger(QDEFinanceMainDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_QDEFinanceMainDialog; // autoWired

	// Finance Main Details Tab---> 1. Key Details
	protected CurrencyBox downPaySupl; // autoWired

	protected Label label_QDEFinanceMainDialog_FinType; // autoWired
	protected Label label_QDEFinanceMainDialog_ScheduleMethod; // autoWired
	protected Label label_QDEFinanceMainDialog_FinRepayPftOnFrq; // autoWired
	protected Label label_QDEFinanceMainDialog_CommitRef; // autoWired
	protected Label label_QDEFinanceMainDialog_PlanDeferCount; // autoWired
	protected Label label_QDEFinanceMainDialog_AlwGrace; // autoWired
	protected Label label_QDEFinanceMainDialog_StepPolicy; // autoWired
	protected Label label_QDEFinanceMainDialog_numberOfSteps; // autoWired
	protected Label label_QDEFinanceMainDialog_FinLimitRef; // autoWired

	protected Textbox custFirstName; // autowired
	protected Textbox custMiddleName; // autowired
	protected Textbox custLastName; // autowired
	protected Datebox custDOB; // autowired
	protected Combobox custGenderCode; // autowired
	protected Combobox custSalutationCode; // autowired
	protected Combobox custMaritalSts; // autowired
	protected Intbox noOfDependents; // autowired
	protected Checkbox salariedCustomer; // autowired
	private String sCustGender;
	protected Textbox eidNumber; // autowired

	protected Textbox phoneCountryCode;
	protected Textbox phoneAreaCode;
	protected Textbox phoneNumber;

	protected Textbox custPassportNo;

	protected Label label_PromotionProduct;
	protected Hbox hboxPromotionProduct;

	private transient String oldVar_custFirstName;
	private transient String oldVar_custMiddleName;
	private transient String oldVar_custLastName;
	private transient Date oldVar_custDOB;
	private transient String oldVar_custSalutationCode;
	private transient String oldVar_custGenderCode;
	private transient String oldVar_custMaritalSts;
	private transient int oldVar_noOfDependents;
	private transient String oldVar_phoneCountryCode;
	private transient String oldVar_phoneAreaCode;
	private transient String oldVar_phoneNumber;
	private transient String oldVar_custPassportNo;

	protected JdbcSearchObject<Customer> custCIFSearchObject;

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	protected transient BigDecimal oldVar_downPaySupl;
	Date startDate = DateUtil.addDays(SysParamUtil.getAppDate(),
			-SysParamUtil.getValueAsInt(SMTParameterConstants.LOAN_START_DATE_BACK_DAYS));
	Date endDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");
	int finFormatter = 0;

	protected transient String nextUserId;

	/**
	 * default constructor.<br>
	 */
	public QDEFinanceMainDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "QDEFinanceMainDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_QDEFinanceMainDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_QDEFinanceMainDialog);

		// READ OVERHANDED parameters !
		if (arguments.containsKey("financeDetail")) {
			setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
			FinanceMain befImage = new FinanceMain();
			BeanUtils.copyProperties(getFinanceDetail().getFinScheduleData().getFinanceMain(), befImage);
			getFinanceDetail().getFinScheduleData().getFinanceMain().setBefImage(befImage);
			setFinanceDetail(getFinanceDetail());
			finFormatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		}

		// READ OVERHANDED params !
		// we get the financeMainListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete financeMain here.
		if (arguments.containsKey("financeMainListCtrl")) {
			setFinanceMainListCtrl((FinanceMainListCtrl) arguments.get("financeMainListCtrl"));
		}

		if (arguments.containsKey("financeSelectCtrl")) {
			setFinanceSelectCtrl((FinanceSelectCtrl) arguments.get("financeSelectCtrl"));
		}

		if (arguments.containsKey("tabbox")) {
			listWindowTab = (Tab) arguments.get("tabbox");
		}

		if (arguments.containsKey("moduleDefiner")) {
			moduleDefiner = (String) arguments.get("moduleDefiner");
		}

		if (arguments.containsKey("eventCode")) {
			eventCode = (String) arguments.get("eventCode");
		}

		if (arguments.containsKey("menuItemRightName")) {
			menuItemRightName = (String) arguments.get("menuItemRightName");
		}

		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		doLoadWorkFlow(financeMain.isWorkflow(), financeMain.getWorkflowId(), financeMain.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().allocateMenuRoleAuthorities(getRole(), "QDEFinanceMainDialog", menuItemRightName);
		} else {
			this.south.setHeight("0px");
		}

		setMainWindow(window_QDEFinanceMainDialog);
		setLabel_FinanceMainDialog_FinType(label_QDEFinanceMainDialog_FinType);
		setLabel_FinanceMainDialog_CommitRef(label_QDEFinanceMainDialog_CommitRef);
		setLabel_FinanceMainDialog_FinRepayPftOnFrq(label_QDEFinanceMainDialog_FinRepayPftOnFrq);
		setLabel_FinanceMainDialog_PlanDeferCount(label_QDEFinanceMainDialog_PlanDeferCount);
		setLabel_FinanceMainDialog_AlwGrace(label_QDEFinanceMainDialog_AlwGrace);
		// setLabel_FinanceMainDialog_CcyConversionRate(label_QDEFinanceMainDialog_CcyConversionRate);
		setLabel_FinanceMainDialog_StepPolicy(label_QDEFinanceMainDialog_StepPolicy);
		setLabel_FinanceMainDialog_numberOfSteps(label_QDEFinanceMainDialog_numberOfSteps);
		setLabel_FinanceMainDialog_FinLimitRef(label_QDEFinanceMainDialog_FinLimitRef);
		setProductCode("QDE");

		/* set components visible dependent of the users rights */
		doCheckRights();

		this.basicDetailTabDiv.setHeight(this.borderLayoutHeight - 100 - 52 + "px");

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getFinanceDetail());

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	public void doSetFieldProperties() {
		logger.debug("Entering");
		this.custFirstName.setMaxlength(50);
		this.custMiddleName.setMaxlength(50);
		this.custLastName.setMaxlength(50);
		this.custDOB.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.finAmount.setMandatory(true);
		this.finAmount.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.finAmount.setScale(finFormatter);
		this.finAmount.setTextBoxWidth(200);

		this.downPaySupl.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.downPaySupl.setTextBoxWidth(200);
		this.downPaySupl.setScale(finFormatter);

		this.phoneCountryCode.setMaxlength(3);
		this.phoneCountryCode.setWidth("50px");
		this.phoneAreaCode.setMaxlength(3);
		this.phoneAreaCode.setWidth("50px");
		this.phoneNumber.setMaxlength(8);
		this.phoneNumber.setWidth("100px");

		this.custPassportNo.setMaxlength(50);
		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities("QDEFinanceMainDialog", getRole(), menuItemRightName);

		this.btnNew.setVisible(false);
		this.btnEdit.setVisible(false);
		this.btnDelete.setVisible(false);
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_QDEFinanceMainDialog_btnSave"));
		this.btnCancel.setVisible(false);

		// Schedule related buttons
		this.btnValidate.setVisible(false);
		this.btnBuildSchedule.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnSave(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		String prevRecordStatus = financeMain.getRecordStatus();
		String recordStatus = userAction.getSelectedItem().getValue();
		if (!PennantConstants.RCD_STATUS_REJECTED.equals(prevRecordStatus)
				&& (PennantConstants.RCD_STATUS_REJECTED.equals(recordStatus)
						|| PennantConstants.RCD_STATUS_CANCELLED.equals(recordStatus))
				&& StringUtils.isEmpty(moduleDefiner)) {
			boolean allow = DisbursementInstCtrl.allowReject(getFinanceDetail().getAdvancePaymentsList());
			if (!allow) {
				MessageUtil.showMessage(Labels.getLabel("label_Finance_QuickDisb_Cancelled"));
				return;
			}
		}

		Long capturereaonse = null;
		String taskId = getTaskId(getRole());
		financeMain.setRecordStatus(userAction.getSelectedItem().getValue().toString());
		capturereaonse = getWorkFlow().getReasonTypeToCapture(taskId, financeMain);
		if (capturereaonse != null && capturereaonse.intValue() != 0) {
			doFillReasons(capturereaonse.intValue());
		} else {
			doSave();
		}
	}

	public void doFillReasons(int reason) {
		logger.debug("Entering");
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("financeMainDialogCtrl", this);
		map.put("reason", reason);
		try {
			Executions.createComponents("/WEB-INF/pages/ReasonDetail/ReasonDetails.zul", getMainWindow(), map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * When record is rejected . <br>
	 * 
	 */
	public void doReject() throws InterruptedException {
		logger.debug("Entering");

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("financeMain", getFinanceDetail().getFinScheduleData().getFinanceMain());
		map.put("financeMainDialogCtrl", this);
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinanceReject.zul",
					window_QDEFinanceMainDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering " + event.toString());
		doEdit();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		MessageUtil.showHelpWindow(event, window_QDEFinanceMainDialog);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		doDelete();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering " + event.toString());
		doCancel();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnEdit.setVisible(true);
		this.btnCancel.setVisible(false);
		this.btnValidate.setVisible(false);
		this.btnBuildSchedule.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain financeMain
	 */
	public void doWriteBeanToComponents(FinanceDetail aFinanceDetail, boolean onLoadProcess) {
		logger.debug("Entering");

		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		CustomerDetails customerDetails = aFinanceDetail.getCustomerDetails();
		Customer aCustomer = customerDetails.getCustomer();
		int format = CurrencyUtil.getFormat(aFinanceMain.getFinCcy());

		this.finType.setValue(aFinanceMain.getFinType());
		this.lovDescFinTypeName.setValue(aFinanceMain.getFinType() + "-" + aFinanceMain.getLovDescFinTypeName());
		this.finReference.setValue(aFinanceMain.getFinReference());
		this.finAmount.setValue(CurrencyUtil.parse(aFinanceMain.getFinAmount(), format));
		this.downPaySupl.setValue(CurrencyUtil.parse(aFinanceMain.getDownPaySupl(), format));
		this.numberOfTerms.setValue(aFinanceMain.getNumberOfTerms());
		this.finCcy.setValue(aFinanceMain.getFinCcy());
		this.finBranch.setValue(aFinanceMain.getFinBranch());
		this.finDivision = aFinanceDetail.getFinScheduleData().getFinanceType().getFinDivision();
		this.custID.setValue(aCustomer.getCustID());
		this.custCIF.setValue(aCustomer.getCustCIF());
		this.custFirstName.setValue(aCustomer.getCustFName());
		this.custMiddleName.setValue(aCustomer.getCustMName());
		this.custLastName.setValue(aCustomer.getCustLName());
		this.custDOB.setValue(aCustomer.getCustDOB());
		this.noOfDependents.setValue(aCustomer.getNoOfDependents());
		if (StringUtils.isNotBlank(aCustomer.getCustCRCPR())) {
			this.eidNumber.setValue(PennantApplicationUtil.formatEIDNumber(aCustomer.getCustCRCPR()));
		}

		if (StringUtils.isNotBlank(financeType.getProduct())) {
			this.promotionProduct.setValue(financeType.getProduct() + " - " + financeType.getLovDescPromoFinTypeDesc());
		} else {
			this.label_PromotionProduct.setVisible(false);
			this.hboxPromotionProduct.setVisible(false);
		}

		this.salariedCustomer.setChecked(aCustomer.isSalariedCustomer());
		fillComboBox(this.custGenderCode, aCustomer.getCustGenderCode(), PennantAppUtil.getGenderCodes(), "");
		fillComboBox(this.custSalutationCode, aCustomer.getCustSalutationCode(),
				PennantAppUtil.getSalutationCodes(aCustomer.getCustGenderCode()), "");
		String code = custGenderCode.getSelectedItem().getValue();
		fillComboBox(this.custMaritalSts, aCustomer.getCustMaritalSts(), PennantAppUtil.getMaritalStsTypes(code), "");

		List<CustomerPhoneNumber> list = customerDetails.getCustomerPhoneNumList();

		if (list != null && !list.isEmpty()) {
			CustomerPhoneNumber aCustomerPhoneNumber = getCustPhonebyType(list, PennantConstants.PHONETYPE_MOBILE);
			if (aCustomerPhoneNumber != null) {
				this.phoneCountryCode.setValue(aCustomerPhoneNumber.getPhoneCountryCode());
				this.phoneAreaCode.setValue(aCustomerPhoneNumber.getPhoneAreaCode());
				this.phoneNumber.setValue(aCustomerPhoneNumber.getPhoneNumber());
			}
		}
		this.custPassportNo.setValue(aCustomer.getCustPassportNo());
		this.recordStatus.setValue(aFinanceMain.getRecordStatus());
		// Filling Child Window Details Tabs
		doFillTabs(aFinanceDetail);

		logger.debug("Leaving");
	}

	private CustomerPhoneNumber getCustPhonebyType(List<CustomerPhoneNumber> list, String phoneType) {
		if (list != null && !list.isEmpty()) {
			for (CustomerPhoneNumber customerPhoneNumber : list) {
				if (StringUtils.trimToEmpty(phoneType).equals(customerPhoneNumber.getPhoneTypeCode())) {
					return customerPhoneNumber;
				}

			}
		}
		return null;
	}

	/**
	 * Method to invoke data filling method for eligibility tab, Scoring tab, fee charges tab, accounting tab,
	 * agreements tab and additional field details tab.
	 * 
	 * @param aFinanceDetail
	 */
	private void doFillTabs(FinanceDetail aFinanceDetail) {
		logger.debug("Entering");

		// Joint Account and Guaranteer Tab Addition
		// if (!finDivision.equals(PennantConstants.FIN_DIVISION_COMMERCIAL) &&
		// !finDivision.equals(PennantConstants.FIN_DIVISION_CORPORATE)) {
		// if(moduleDefiner.equals("")){
		// appendJointGuarantorDetailTab();
		// }
		// }

		// Eligibility Details Tab Adding
		// if(moduleDefiner.equals("")){
		// appendEligibilityDetailTab(true);
		// }

		// Scoring Detail Tab Addition
		// if(moduleDefiner.equals("")){
		// appendFinScoringDetailTab(true);
		// }

		// Agreements Detail Tab Addition
		// if(moduleDefiner.equals("")){
		// appendAgreementsDetailTab(true);
		// }

		// CheckList Details Tab Addition
		if (StringUtils.isEmpty(moduleDefiner)) {
			boolean finIsNewRecord = getFinanceDetail().getFinScheduleData().getFinanceMain().isNewRecord();
			appendCheckListDetailTab(getFinanceDetail(), finIsNewRecord, true);
		}

		// Recommend & Comments Details Tab Addition
		// appendRecommendDetailTab(true);

		// Additional Detail Tab Dynamic Display
		// if(moduleDefiner.equals("")){
		// appendAddlDetailsTab();
		// }

		// Document Detail Tab Addition
		appendDocumentDetailTab();

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceSchData (FinScheduleData)
	 */
	public void doWriteComponentsToBean(FinanceDetail detail, boolean increasePhoneVersion) {
		logger.debug("Entering");

		doClearMessage();
		doSetValidation();
		doSetLOVValidation();

		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		FinanceMain aFinanceMain = detail.getFinScheduleData().getFinanceMain();
		FinanceType fintype = detail.getFinScheduleData().getFinanceType();

		if (StringUtils.isBlank(aFinanceMain.getFinSourceID())) {
			aFinanceMain.setFinSourceID(App.CODE);
		}
		aFinanceMain.setFinBranch(getUserWorkspace().getLoggedInUser().getBranchCode());
		try {
			if (StringUtils.isBlank(this.finReference.getValue())) {
				this.finReference.setValue(String.valueOf(ReferenceGenerator.generateFinRef(aFinanceMain, fintype)));
			}

			aFinanceMain.setFinReference(this.finReference.getValue());
			detail.getFinScheduleData().setFinID(aFinanceMain.getFinID());
			detail.getFinScheduleData().setFinReference(this.finReference.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setLovDescFinTypeName(this.lovDescFinTypeName.getValue());
			aFinanceMain.setFinType(this.finType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setFinCcy(this.finCcy.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setFinBranch(this.finBranch.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setNumberOfTerms(this.numberOfTerms.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceMain.setDownPaySupl(CurrencyUtil.unFormat(this.downPaySupl.getValidateValue(), formatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceMain.setFinAmount(CurrencyUtil.unFormat(this.finAmount.getValidateValue(), formatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.custCIF.isReadonly()) {
				if (this.custID.longValue() == 0 || this.custID.longValue() == Long.MIN_VALUE) {
					throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID", new String[] {
							Labels.getLabel("label_" + getProductCode() + "FinanceMainDialog_CustID.value") }));
				}
			}
			aFinanceMain.setCustID(this.custID.longValue());
			aFinanceMain.setLovDescCustCIF(this.custCIF.getValue());
			aFinanceMain.setLovDescCustShrtName(this.custShrtName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		Customer aCustomer = detail.getCustomerDetails().getCustomer();
		if (detail.getCustomerDetails().isNewRecord()) {
			aCustomer.setNewRecord(true);

		}
		try {
			aCustomer.setCustFName(this.custFirstName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustMName(this.custMiddleName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustLName(this.custLastName.getValue());
			aCustomer.setCustShrtName(PennantApplicationUtil.getFullName(this.custFirstName.getValue(),
					this.custMiddleName.getValue(), this.custLastName.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustDOB(new Timestamp(this.custDOB.getValue().getTime()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.custSalutationCode.isVisible() && !this.custSalutationCode.isDisabled()) {
				if ("#".equals(getComboboxValue(this.custSalutationCode))) {
					throw new WrongValueException(this.custSalutationCode, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_CustomerDialog_CustSalutationCode.value") }));
				}
			}
			aCustomer.setCustSalutationCode(getComboboxValue(this.custSalutationCode));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.custGenderCode.isVisible() && !this.custGenderCode.isDisabled()) {
				if ("#".equals(getComboboxValue(this.custGenderCode))) {
					throw new WrongValueException(this.custGenderCode, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_CustomerDialog_CustGenderCode.value") }));
				}
			}
			aCustomer.setCustGenderCode(getComboboxValue(this.custGenderCode));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.custMaritalSts.isVisible() && !this.custMaritalSts.isDisabled()) {
				if ("#".equals(getComboboxValue(this.custMaritalSts))) {
					throw new WrongValueException(this.custMaritalSts, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_CustomerDialog_CustMaritalSts.value") }));
				}
			}
			aCustomer.setCustMaritalSts(getComboboxValue(this.custMaritalSts));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setNoOfDependents(this.noOfDependents.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setSalariedCustomer(this.salariedCustomer.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			List<CustomerPhoneNumber> list = detail.getCustomerDetails().getCustomerPhoneNumList();

			boolean createNewRecord = false;
			if (list != null && !list.isEmpty()) {
				CustomerPhoneNumber aCustomerPhoneNumber = getCustPhonebyType(list, PennantConstants.PHONETYPE_MOBILE);
				if (aCustomerPhoneNumber != null) {
					if (increasePhoneVersion) {
						aCustomerPhoneNumber.setVersion(aCustomerPhoneNumber.getVersion() + 1);
					}
					aCustomerPhoneNumber.setPhoneCountryCode(this.phoneCountryCode.getValue());
					aCustomerPhoneNumber.setPhoneAreaCode(this.phoneAreaCode.getValue());
					aCustomerPhoneNumber.setPhoneNumber(this.phoneNumber.getValue());
					aCustomerPhoneNumber.setRecordType(PennantConstants.RCD_UPD);

				} else {
					createNewRecord = true;
				}
			} else {
				createNewRecord = true;
			}
			if (createNewRecord) {
				CustomerPhoneNumber aCustomerPhoneNumber = new CustomerPhoneNumber();
				aCustomerPhoneNumber.setNewRecord(true);
				if (increasePhoneVersion) {
					aCustomerPhoneNumber.setVersion(aCustomerPhoneNumber.getVersion() + 1);
				}
				aCustomerPhoneNumber.setRecordType(PennantConstants.RCD_ADD);
				aCustomerPhoneNumber.setPhoneTypeCode(PennantConstants.PHONETYPE_MOBILE);
				aCustomerPhoneNumber.setPhoneCustID(this.custID.getValue());
				aCustomerPhoneNumber.setPhoneCountryCode(this.phoneCountryCode.getValue());
				aCustomerPhoneNumber.setPhoneAreaCode(this.phoneAreaCode.getValue());
				aCustomerPhoneNumber.setPhoneNumber(this.phoneNumber.getValue());
				if (list != null) {
					list.add(aCustomerPhoneNumber);
				} else {
					list = new ArrayList<CustomerPhoneNumber>();
					list.add(aCustomerPhoneNumber);
				}

			}
			detail.getCustomerDetails().setCustomerPhoneNumList(list);

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustPassportNo(this.custPassportNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (detail.isNewRecord()) {
			detail.getCustomerDetails().getEmploymentDetailsList();

		}

		if (wve.isEmpty()) {

			// Fee Details Validations on Customer data
			if (buildEvent && getCustomerDialogCtrl() != null) {
				try {
					getCustomerDialogCtrl().doValidateFeeDetails(custDetailTab);
				} catch (WrongValueException e) {
					throw e;
				}
			}

		}

		// FinanceMain Details Tab Validation Error Throwing
		showErrorDetails(wve, financeTypeDetailsTab);

		logger.debug("Leaving");
	}

	/**
	 * Method to show error details if occurred
	 * 
	 **/
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug("Entering");

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			tab.setSelected(true);
			doRemoveValidation();
			doRemoveLOVValidation();
			buildEvent = false;
			// groupBox.set
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 */
	public void doShowDialog(FinanceDetail afinanceDetail) {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (afinanceDetail.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitNew();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		// setFocus
		this.finReference.focus();

		// Reset Maintenance Buttons for finance modification
		if (StringUtils.isNotEmpty(moduleDefiner)) {
			this.btnValidate.setDisabled(true);
			this.btnBuildSchedule.setDisabled(true);
			this.btnValidate.setVisible(false);
			this.btnBuildSchedule.setVisible(false);
			afinanceDetail.getFinScheduleData().getFinanceMain()
					.setCurDisbursementAmt(afinanceDetail.getFinScheduleData().getFinanceMain().getFinAmount());
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(afinanceDetail, true);

			// stores the initial data for comparing if they are changed
			// during user action.
			if (StringUtils.isEmpty(moduleDefiner)) {

				// Set Customer Data
				if (getFinanceDetail().getFinScheduleData().getFinanceMain().isNewRecord()) {
					doSetCustomer(getFinanceDetail().getCustomerDetails().getCustomer(), null);
				}
			}

			setDialog(DialogType.EMBEDDED);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Returning Finance Amount and Currency Data for Contributor Validation
	 * 
	 * @return
	 */
	public List<Object> prepareContributor() {
		logger.debug("Entering");
		List<Object> list = new ArrayList<Object>();

		this.finAmount.setConstraint("");
		this.finCcy.setConstraint("");
		this.finAmount.setErrorMessage("");
		this.finCcy.setErrorMessage("");

		list.add(this.finAmount.getActualValue());
		list.add(this.finCcy.getValue());
		logger.debug("Leaving");
		return list;

	}

	/**
	 * Resets the initial values from memory variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");

		// FinanceMain Details Tab ---> 1. Basic Details

		this.finReference.setValue(this.oldVar_finReference);
		this.finType.setValue(this.oldVar_finType);
		this.lovDescFinTypeName.setValue(this.oldVar_lovDescFinTypeName);
		this.finCcy.setValue(this.oldVar_finCcy);
		this.cbProfitDaysBasis.setSelectedIndex(this.oldVar_profitDaysBasis);
		this.finStartDate.setValue(this.oldVar_finStartDate);
		this.finContractDate.setValue(this.oldVar_finContractDate);
		this.finAmount.setValue(this.oldVar_finAmount);
		this.downPayBank.setValue(this.oldVar_downPayBank);
		this.downPaySupl.setValue(this.oldVar_downPaySupl);
		this.custID.setValue(this.oldVar_custID);
		this.finBranch.setValue(this.oldVar_finBranch);
		this.finBranch.setDescription(this.oldVar_lovDescFinBranchName);

		this.phoneCountryCode.setValue(this.oldVar_phoneCountryCode);
		this.phoneAreaCode.setValue(this.oldVar_phoneAreaCode);
		this.phoneNumber.setValue(this.oldVar_phoneNumber);

		this.custPassportNo.setValue(this.oldVar_custPassportNo);

		this.recordStatus.setValue(this.oldVar_recordStatus);

		if (isWorkFlowEnabled()) {
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	public boolean isDataChanged(boolean close) {
		logger.debug("Entering");

		doClearMessage();

		if (this.oldVar_custFirstName != this.custFirstName.getValue()) {
			return true;
		}
		if (this.oldVar_custMiddleName != this.custMiddleName.getValue()) {
			return true;
		}
		if (this.oldVar_custLastName != this.custLastName.getValue()) {
			return true;
		}
		if (!DateUtil.matches(this.oldVar_custDOB, this.custDOB.getValue())) {
			return true;
		}
		if (this.oldVar_custGenderCode != this.custGenderCode.getSelectedItem().getValue().toString()) {
			return true;
		}
		if (this.oldVar_custSalutationCode != this.custSalutationCode.getSelectedItem().getValue().toString()) {
			return true;
		}
		if (this.oldVar_custMaritalSts != this.custMaritalSts.getSelectedItem().getValue().toString()) {
			return true;
		}
		if (this.oldVar_noOfDependents != this.noOfDependents.intValue()) {
			return true;
		}

		if (this.oldVar_phoneCountryCode != this.phoneCountryCode.getValue()) {
			return true;
		}
		if (this.oldVar_phoneAreaCode != this.phoneAreaCode.getValue()) {
			return true;
		}
		if (this.oldVar_phoneNumber != this.phoneNumber.getValue()) {
			return true;
		}

		if (this.oldVar_custPassportNo != this.custPassportNo.getValue()) {
			return true;
		}

		// To clear the Error Messages
		doClearMessage();

		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceType().getFinCcy());

		BigDecimal oldDwnPaySupl = CurrencyUtil.unFormat(this.oldVar_downPaySupl, formatter);
		BigDecimal newDwnPaySupl = CurrencyUtil.unFormat(this.downPaySupl.getActualValue(), formatter);
		if (oldDwnPaySupl.compareTo(newDwnPaySupl) != 0) {
			return true;
		}
		logger.debug("Leaving");
		return false;
	}

	/**
	 * Method for Executing Eligibility Details
	 */
	public void onExecuteEligibilityDetail() {
		logger.debug("Entering");

		doSetValidation();
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		showErrorDetails(wve, financeTypeDetailsTab);
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		Date appStartDate = SysParamUtil.getAppDate();
		Date startDate = SysParamUtil.getValueAsDate("APP_DFT_START_DATE");

		if (!this.custFirstName.isReadonly()) {
			this.custFirstName
					.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustFirstName.value"),
							PennantRegularExpressions.REGEX_CUST_NAME, true));
		}
		if (!this.custLastName.isReadonly()) {
			this.custLastName
					.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustLastName.value"),
							PennantRegularExpressions.REGEX_CUST_NAME, true));
		}

		if (!this.custDOB.isDisabled()) {
			this.custDOB.setConstraint(new PTDateValidator(Labels.getLabel("label_CustomerDialog_CustDOB.value"), true,
					startDate, appStartDate, false));
		}

		if (!this.phoneCountryCode.isReadonly()) {
			this.phoneCountryCode.setConstraint(new PTPhoneNumberValidator(
					Labels.getLabel("label_CustomerPhoneNumberDialog_PhoneCountryCode.value"), true, 1));
		}
		if (!this.phoneAreaCode.isReadonly()) {
			this.phoneAreaCode.setConstraint(new PTPhoneNumberValidator(
					Labels.getLabel("label_CustomerPhoneNumberDialog_PhoneAreaCode.value"), true, 2));
		}
		if (!this.phoneNumber.isReadonly()) {
			this.phoneNumber.setConstraint(new PTPhoneNumberValidator(
					Labels.getLabel("label_CustomerPhoneNumberDialog_PhoneNumber.value"), true, 3));
		}

		if (!this.custPassportNo.isReadonly()) {
			this.custPassportNo.setConstraint(
					new PTStringValidator(Labels.getLabel("label_QDEFinanceMainDialog_custPassportNo.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_CODE, true));
		}

		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();

		// FinanceMain Details Tab ---> 1. Basic Details

		if (!this.finReference.isReadonly() && !financeType.isFinIsGenRef()) {

			this.finReference.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_FinReference.value"), null, true));
		}

		if (!this.finAmount.isDisabled()) {
			this.finAmount.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_FinanceMainDialog_PurchasePrice.value"), 0, true, false));
		}

		if (!this.numberOfTerms.isDisabled() && !this.numberOfTerms.isReadonly()) {
			this.numberOfTerms.setConstraint(
					new PTNumberValidator(Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value"), true, false));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);

		this.custFirstName.setConstraint("");
		this.custMiddleName.setConstraint("");
		this.custLastName.setConstraint("");
		this.custDOB.setConstraint("");

		this.phoneCountryCode.setConstraint("");
		this.phoneAreaCode.setConstraint("");
		this.phoneNumber.setConstraint("");

		this.custPassportNo.setConstraint("");
		// FinanceMain Details Tab ---> 1. Basic Details

		this.finReference.setConstraint("");
		this.finAmount.setConstraint("");
		this.downPaySupl.setConstraint("");
		this.finBranch.setConstraint("");

		// FinanceMain Details Tab ---> 2. Grace Period Details

		logger.debug("Leaving");
	}

	/**
	 * Method to set validation on LOV fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");

		logger.debug("Leaving ");
	}

	/**
	 * Method to remove validation on LOV fields.
	 * 
	 **/
	private void doRemoveLOVValidation() {
		logger.debug("Entering ");

		// FinanceMain Details Tab ---> 1. Basic Details

		this.lovDescFinTypeName.setConstraint("");
		this.finCcy.setConstraint("");
		this.finBranch.setConstraint("");
		this.custCIF.setConstraint("");

		logger.debug("Leaving ");
	}

	/**
	 * Method to clear error messages.
	 */
	public void doClearMessage() {
		logger.debug("Entering");
		this.custFirstName.setErrorMessage("");
		this.custMiddleName.setErrorMessage("");
		this.custLastName.setErrorMessage("");

		this.custDOB.setErrorMessage("");
		this.custGenderCode.setErrorMessage("");
		this.custMaritalSts.setErrorMessage("");
		this.numberOfTerms.setErrorMessage("");

		this.phoneCountryCode.setErrorMessage("");
		this.phoneAreaCode.setErrorMessage("");
		this.phoneNumber.setErrorMessage("");

		this.custPassportNo.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a financeMain object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		FinanceDetail afinanceDetail = new FinanceDetail();
		BeanUtils.copyProperties(getFinanceDetail(), afinanceDetail);

		FinanceMain afinanceMain = afinanceDetail.getFinScheduleData().getFinanceMain();
		afinanceDetail.setUserAction(this.userAction.getSelectedItem().getLabel());

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ afinanceMain.getFinReference();

		MessageUtil.confirm(msg, evnt -> {
			String tranType = PennantConstants.TRAN_WF;
			if (Messagebox.ON_YES.equals(evnt.getName())) {
				if (StringUtils.isBlank(afinanceMain.getRecordType())) {
					afinanceMain.setVersion(afinanceMain.getVersion() + 1);
					afinanceMain.setRecordType(PennantConstants.RECORD_TYPE_DEL);

					if (isWorkFlowEnabled()) {
						afinanceMain.setNewRecord(true);
						tranType = PennantConstants.TRAN_WF;
					} else {
						tranType = PennantConstants.TRAN_DEL;
					}
				}

				try {
					afinanceDetail.getFinScheduleData().setFinanceMain(afinanceMain);
					if (doProcess(afinanceDetail, tranType)) {
						if (getFinanceMainListCtrl() != null) {
							refreshList();
						}
						if (getFinanceSelectCtrl() != null) {
							refreshMaintainList();
						}
						closeDialog();
					}

				} catch (DataAccessException e) {
					MessageUtil.showError(e);
				}

			}
		});

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	public void doEdit() {
		logger.debug("Entering");
		// if (getFinanceDetail().getFinScheduleData().getFinanceMain().isNewRecord()) {
		// this.finReference.setReadonly(false);
		// } else {
		// }
		this.finReference.setReadonly(true);
		this.finAmount.setDisabled(isReadOnly("QDEFinanceMainDialog_finAmount"));
		this.numberOfTerms.setReadonly(isReadOnly("QDEFinanceMainDialog_numberOfTerms"));
		this.custFirstName.setReadonly(isReadOnly("QDEFinanceMainDialog_custFirstName"));
		this.custMiddleName.setReadonly(isReadOnly("QDEFinanceMainDialog_custMiddleName"));
		this.custLastName.setReadonly(isReadOnly("QDEFinanceMainDialog_custLastName"));
		this.custMaritalSts.setDisabled(isReadOnly("QDEFinanceMainDialog_custMaritalSts"));
		this.custSalutationCode.setDisabled(isReadOnly("QDEFinanceMainDialog_custSalutationCode"));
		this.custDOB.setDisabled(isReadOnly("QDEFinanceMainDialog_custDOB"));
		this.custGenderCode.setDisabled(isReadOnly("QDEFinanceMainDialog_custGenderCode"));
		this.noOfDependents.setReadonly(isReadOnly("QDEFinanceMainDialog_noOfDependents"));
		this.downPaySupl.setDisabled(isReadOnly("QDEFinanceMainDialog_downPaySupl"));
		this.salariedCustomer.setDisabled(isReadOnly("QDEFinanceMainDialog_salariedCustomer"));
		this.phoneCountryCode.setReadonly(isReadOnly("QDEFinanceMainDialog_phoneCountryCode"));
		this.phoneAreaCode.setReadonly(isReadOnly("QDEFinanceMainDialog_phoneAreaCode"));
		this.phoneNumber.setReadonly(isReadOnly("QDEFinanceMainDialog_phoneNumber"));
		this.custPassportNo.setReadonly(isReadOnly("QDEFinanceMainDialog_custPassportNo"));
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.custFirstName.setReadonly(true);
		this.custMiddleName.setReadonly(true);
		this.custLastName.setReadonly(true);

		this.custMaritalSts.setDisabled(true);
		this.custSalutationCode.setDisabled(true);
		this.custDOB.setDisabled(true);
		this.custGenderCode.setDisabled(true);
		this.noOfDependents.setReadonly(true);

		this.phoneCountryCode.setReadonly(true);
		this.phoneAreaCode.setReadonly(true);
		this.phoneNumber.setReadonly(true);

		this.custPassportNo.setReadonly(true);

		this.downPaySupl.setReadonly(true);
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");

		this.custFirstName.setValue("");
		this.custMiddleName.setValue("");
		this.custLastName.setValue("");
		this.custGenderCode.setValue("");
		this.custDOB.setText("");

		this.phoneCountryCode.setValue("");
		this.phoneAreaCode.setValue("");
		this.phoneNumber.setValue("");

		this.custPassportNo.setValue("");
		this.downPaySupl.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws Exception
	 */
	public void doSave() throws Exception {
		logger.debug("Entering");

		FinanceDetail aFinanceDetail = new FinanceDetail();
		aFinanceDetail = ObjectUtil.clone(getFinanceDetail());

		boolean isNew = false;
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		recSave = false;
		if (this.userAction.getSelectedItem() != null) {
			if ("Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| "Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| this.userAction.getSelectedItem().getLabel().contains("Reject")
					|| this.userAction.getSelectedItem().getLabel().contains("Resubmit")) {
				recSave = true;
				aFinanceDetail.setActionSave(true);
			}
			aFinanceDetail.setUserAction(this.userAction.getSelectedItem().getLabel());
		}
		aFinanceDetail.setAccountingEventCode(eventCode);

		// force validation, if on, than execute by component.getValue()
		// fill the financeMain object with the components data
		doWriteComponentsToBean(aFinanceDetail, true);

		// Validation For Mandatory Recommendation
		if (!doValidateRecommendation()) {
			return;
		}

		isNew = aFinanceDetail.isNewRecord();

		// Finance CheckList Details Tab
		if (checkListChildWindow != null) {
			boolean validationSuccess = doSave_CheckList(aFinanceDetail, false);
			if (!validationSuccess) {
				return;
			}
		} else {
			aFinanceDetail.setFinanceCheckList(null);
		}

		if (StringUtils.isBlank(this.custCIF.getValue())) {
			aFinanceDetail.setStageAccountingList(null);
		} else {

			// Finance Accounting Details Tab
			if (getAccountingDetailDialogCtrl() != null) {
				// check if accounting rules executed or not
				if (!recSave && !getAccountingDetailDialogCtrl().isAccountingsExecuted()) {
					MessageUtil.showError(Labels.getLabel("label_Finance_Calc_Accountings"));
					return;
				}
				if (!recSave && getAccountingDetailDialogCtrl().getDisbCrSum()
						.compareTo(getAccountingDetailDialogCtrl().getDisbDrSum()) != 0) {
					MessageUtil.showError(Labels.getLabel("label_Finance_Acc_NotMatching"));
					return;
				}
			}

			// Finance Stage Accounting Details Tab
			if (!recSave && getStageAccountingDetailDialogCtrl() != null) {
				// check if accounting rules executed or not
				if (!getStageAccountingDetailDialogCtrl().stageAccountingsExecuted) {
					MessageUtil.showError(Labels.getLabel("label_Finance_Calc_StageAccountings"));
					return;
				}
				if (getStageAccountingDetailDialogCtrl().stageDisbCrSum
						.compareTo(getStageAccountingDetailDialogCtrl().stageDisbDrSum) != 0) {
					MessageUtil.showError(Labels.getLabel("label_Finance_Acc_NotMatching"));
					return;
				}
			} else {
				aFinanceDetail.setStageAccountingList(null);
			}
		}

		// Document Details Saving
		if (getDocumentDetailDialogCtrl() != null) {
			aFinanceDetail.setDocumentDetailsList(getDocumentDetailDialogCtrl().getDocumentDetailsList());
		} else {
			aFinanceDetail.setDocumentDetailsList(null);
		}

		aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		// Finance Eligibility Details Tab
		if (getEligibilityDetailDialogCtrl() != null) {
			aFinanceDetail = getEligibilityDetailDialogCtrl().doSave_EligibilityList(aFinanceDetail);
		}

		// Finance Scoring Details Tab
		if (getScoringDetailDialogCtrl() != null) {
			getScoringDetailDialogCtrl().doSave_ScoreDetail(aFinanceDetail);
		} else {
			aFinanceDetail.setFinScoreHeaderList(null);
			aFinanceDetail.setScore(BigDecimal.ZERO);
		}

		// Guaranteer Details Tab ---> Guaranteer Details
		if (getJointAccountDetailDialogCtrl() != null) {
			if (getJointAccountDetailDialogCtrl().getGuarantorDetailList() != null
					&& getJointAccountDetailDialogCtrl().getGuarantorDetailList().size() > 0) {
				getJointAccountDetailDialogCtrl().doSave_GuarantorDetail(aFinanceDetail, true);
			}
			if (getJointAccountDetailDialogCtrl().getJointAccountDetailList() != null
					&& getJointAccountDetailDialogCtrl().getJointAccountDetailList().size() > 0) {
				getJointAccountDetailDialogCtrl().doSave_JointAccountDetail(aFinanceDetail, true);
			}
		} else {
			aFinanceDetail.setJointAccountDetailList(null);
			aFinanceDetail.setGurantorsDetailList(null);
		}

		// Write the additional validations as per below example
		// get the selected branch object from the box
		// Do data level validations here

		if (!recSave) {
			// Customer Dedup Process Check
			boolean processCompleted = doCustomerDedupe(aFinanceDetail);
			if (!processCompleted) {
				return;
			}

			// Finance Dedup List Process Checking
			processCompleted = doFinanceDedupe(aFinanceDetail);
			if (!processCompleted) {
				return;
			}

			// Black List Process Check
			processCompleted = doBlacklistCheck(aFinanceDetail);
			if (!processCompleted) {
				return;
			}
		}

		String tranType = "";
		if (isWorkFlowEnabled()) {

			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinanceMain.getRecordType())) {
				aFinanceMain.setVersion(aFinanceMain.getVersion() + 1);
				if (isNew) {
					aFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinanceMain.setNewRecord(true);
				}
			}

		} else {
			aFinanceMain.setVersion(aFinanceMain.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
			if (doProcess(aFinanceDetail, tranType)) {
				if (getFinanceMainListCtrl() != null) {
					refreshList();
				}
				if (getFinanceSelectCtrl() != null) {
					refreshMaintainList();
				}

				// Customer Notification for Role Identification
				if (StringUtils.isBlank(aFinanceMain.getNextTaskId())) {
					aFinanceMain.setNextRoleCode("");
				}
				String msg = PennantApplicationUtil.getSavingStatus(aFinanceMain.getRoleCode(),
						aFinanceMain.getNextRoleCode(), aFinanceMain.getFinReference(), " Finance ",
						aFinanceMain.getRecordStatus(), getNextUserId());
				Clients.showNotification(msg, "info", null, null, -1);

				// Mail Alert Notification for User
				// if
				// (!StringUtils.trimToEmpty(aFinanceMain.getNextTaskId()).equals("")
				// &&
				// !StringUtils.trimToEmpty(aFinanceMain.getNextRoleCode()).equals(aFinanceMain.getRoleCode()))
				// {
				// getMailUtil().sendMail("FIN", aFinanceDetail, this);
				// getMailUtil().sendMail(1, PennantConstants.TEMPLATE_FOR_AE,
				// aFinanceMain);
				// }

				// If Next Role doesn't have Queue Assignment
				// if (aFinanceMain.getNextUserId() == null) {
				// getFinanceDetailService().updateUserCounts(PennantConstants.WORFLOW_MODULE_FINANCE,
				// getRole(), getUserWorkspace().getUserDetails().getUserId());
				// }

				closeDialog();
				if (listWindowTab != null) {
					listWindowTab.setSelected(true);
				}
			} else {
				// updateFailedRecordCount(aFinanceDetail.getFinScheduleData().getFinanceMain().getLovDescNextUsersRolesMap());
			}
		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	// WorkFlow Creations

	private boolean doCustomerDedupe(FinanceDetail aFinanceDetail) {
		logger.debug("Entering");

		String corebank = aFinanceDetail.getCustomerDetails().getCustomer().getCustCoreBank();
		CustomerDetails details = aFinanceDetail.getCustomerDetails();

		// If Core Bank ID is Exists then Customer is already existed in Core
		// Banking System
		if (StringUtils.isBlank(corebank)) {

			String curLoginUser = getUserWorkspace().getUserDetails().getSecurityUser().getUsrLogin();
			details = FetchFinCustomerDedupDetails.getFinCustomerDedup(getRole(),
					aFinanceDetail.getFinScheduleData().getFinanceMain().getFinType(),
					aFinanceDetail.getFinScheduleData().getFinanceMain().getFinReference(),
					aFinanceDetail.getCustomerDetails(), getMainWindow(), curLoginUser);

			if (details.getCustomer().isDedupFound() && !details.getCustomer().isSkipDedup()) {
				return false;
			} else {
				return true;
			}
		}

		logger.debug("Leaving");
		return true;
	}

	/**
	 * Method for Process Checking of Finance Dedup Details
	 * 
	 * @param aFinanceDetail
	 * @return
	 */
	private boolean doFinanceDedupe(FinanceDetail aFinanceDetail) {

		boolean isProcessCompleted;
		String curLoginUser = getUserWorkspace().getUserDetails().getSecurityUser().getUsrLogin();
		aFinanceDetail = FetchDedupDetails.getLoanDedup(getRole(), aFinanceDetail, getMainWindow(), curLoginUser);

		if (aFinanceDetail.getFinScheduleData().getFinanceMain().isDedupFound()
				&& !aFinanceDetail.getFinScheduleData().getFinanceMain().isSkipDedup()) {
			isProcessCompleted = false;
		} else {
			isProcessCompleted = true;
		}
		return isProcessCompleted;
	}

	/**
	 * Method for Checking Process of Black List Details
	 */
	private boolean doBlacklistCheck(FinanceDetail aFinanceDetail) {

		boolean isProcessCompleted;

		String curLoginUser = getUserWorkspace().getUserDetails().getSecurityUser().getUsrLogin();
		aFinanceDetail = FetchBlackListDetails.getBlackListCustomers(getRole(), aFinanceDetail, getMainWindow(),
				curLoginUser);

		if (aFinanceDetail.getFinScheduleData().getFinanceMain().isBlacklisted()) {
			if (aFinanceDetail.getFinScheduleData().getFinanceMain().isBlacklistOverride()) {
				isProcessCompleted = true;
			} else {
				isProcessCompleted = false;
			}
		} else {
			isProcessCompleted = true;
		}
		return isProcessCompleted;
	}

	private String getServiceTasks(String taskId, FinanceMain financeMain, String finishedTasks) {
		logger.debug("Entering");

		String serviceTasks = getServiceOperations(taskId, financeMain);

		if (!"".equals(finishedTasks)) {
			String[] list = finishedTasks.split(";");

			for (int i = 0; i < list.length; i++) {
				serviceTasks = serviceTasks.replace(list[i] + ";", "");
			}
		}
		logger.debug("Leaving");
		return serviceTasks;
	}

	private void setNextTaskDetails(String taskId, FinanceMain financeMain) {
		logger.debug("Entering");

		// Set the next task id
		String action = userAction.getSelectedItem().getLabel();
		String nextTaskId = StringUtils.trimToEmpty(financeMain.getNextTaskId());

		if ("".equals(nextTaskId)) {
			if ("Save".equals(action)) {
				nextTaskId = taskId + ";";
			}
		} else {
			if (!"Save".equals(action)) {
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
			}
		}

		if ("".equals(nextTaskId)) {
			nextTaskId = getNextTaskIds(taskId, financeMain);
		}

		// Set the role codes for the next tasks
		String nextRoleCode = "";
		Map<String, String> baseRoleMap = null;

		if ("".equals(nextTaskId)) {
			nextRoleCode = getFirstTaskOwner();
		} else {
			String[] nextTasks = nextTaskId.split(";");

			if (nextTasks.length > 0) {
				baseRoleMap = new HashMap<String, String>(nextTasks.length);
				for (int i = 0; i < nextTasks.length; i++) {
					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode.concat(",");
					}
					nextRoleCode += getTaskOwner(nextTasks[i]);
					baseRoleMap.put(getTaskOwner(nextTasks[i]), StringUtils.trimToEmpty(getTaskBaseRole(nextTasks[i])));
				}
			}
		}

		financeMain.setTaskId(taskId);
		financeMain.setNextTaskId(nextTaskId);
		financeMain.setRoleCode(getRole());
		financeMain.setNextRoleCode(nextRoleCode);

		financeMain.setLovDescAssignMthd(StringUtils.trimToEmpty(getTaskAssignmentMethod(taskId)));
		financeMain.setLovDescBaseRoleCodeMap(baseRoleMap);
		baseRoleMap = null;

		if (!nextRoleCode.contains(getRole())) {
			financeMain.setPriority(0);
			if (StringUtils.isBlank(financeMain.getLovDescAssignMthd())) {
				financeMain.setNextUserId(null);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Processing Finance Detail Object for Database Operation
	 * 
	 * @param afinanceMain
	 * @param tranType
	 * @return
	 */
	protected boolean doProcess(FinanceDetail aFinanceDetail, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = true;
		AuditHeader auditHeader = null;
		FinanceMain afinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		afinanceMain.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		afinanceMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		afinanceMain.setUserDetails(getUserWorkspace().getLoggedInUser());

		aFinanceDetail.getFinScheduleData().setFinanceMain(afinanceMain);
		aFinanceDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			afinanceMain.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			// Check for service tasks. If one exists perform the task(s)
			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, afinanceMain, finishedTasks);

			if (isNotesMandatory(taskId, afinanceMain)) {
				if (!notesEntered) {
					MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
					return false;
				}
			}

			auditHeader = getAuditHeader(aFinanceDetail, PennantConstants.TRAN_WF);

			while (!"".equals(serviceTasks)) {

				String method = serviceTasks.split(";")[0];

				if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doDedup)) {

					FinanceDetail tFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
					tFinanceDetail = FetchDedupDetails.getLoanDedup(getRole(), aFinanceDetail,
							this.window_QDEFinanceMainDialog, getUserWorkspace().getUserDetails().getUsername());

					if (tFinanceDetail.getFinScheduleData().getFinanceMain().isDedupFound()
							&& !tFinanceDetail.getFinScheduleData().getFinanceMain().isSkipDedup()) {
						processCompleted = false;
					} else {
						processCompleted = true;
					}
					auditHeader.getAuditDetail().setModelData(tFinanceDetail);

				} else if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doBlacklist)) {

					FinanceDetail tFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
					FinanceMain tFinanceMain = tFinanceDetail.getFinScheduleData().getFinanceMain();

					// If Core Bank ID is Exists then Customer is already
					// existed in Core Banking System
					if (StringUtils.isNotBlank(tFinanceMain.getLovDescCustCoreBank())) {
						tFinanceDetail.getFinScheduleData().getFinanceMain().setBlacklisted(
								getFinanceDetailService().checkExistCustIsBlackListed(tFinanceMain.getCustID()));
						tFinanceDetail.getFinScheduleData().getFinanceMain().setBlacklistOverride(false);
					} else {
						String curLoginUser = getUserWorkspace().getUserDetails().getSecurityUser().getUsrLogin();
						aFinanceDetail = FetchBlackListDetails.getBlackListCustomers(getRole(), aFinanceDetail,
								getMainWindow(), curLoginUser);

					}

					if (tFinanceDetail.getFinScheduleData().getFinanceMain().isBlacklisted()) {
						if (tFinanceDetail.getFinScheduleData().getFinanceMain().isBlacklistOverride()) {
							processCompleted = true;
						} else {
							processCompleted = false;
						}
					} else {
						processCompleted = true;
					}
					auditHeader.getAuditDetail().setModelData(tFinanceDetail);

				} else if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_CheckLimits)) {

					processCompleted = doSaveProcess(auditHeader, method);

				} else if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckScore)) {

					FinanceDetail tFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
					tFinanceDetail.getFinScheduleData().getFinanceMain().setScore(tFinanceDetail.getScore());
					processCompleted = true;

				} else if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckExceptions)) {

					auditHeader = getFinanceDetailService().doCheckExceptions(auditHeader);

				} else if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doSendNotification)) {

					processCompleted = true;

				} else if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doClearQueues)) {

					aFinanceDetail.getFinScheduleData().getFinanceMain().setNextTaskId("");

				} else if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckProspectCustomer)) {
					// Prospect Customer Checking
					if (StringUtils
							.isBlank(aFinanceDetail.getFinScheduleData().getFinanceMain().getLovDescCustCoreBank())) {
						MessageUtil.showError(Labels.getLabel("label_FinanceMainDialog_Mandatory_Prospect.value"));
						return false;
					}

				} else {
					FinanceDetail tFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
					setNextTaskDetails(taskId, tFinanceDetail.getFinScheduleData().getFinanceMain());
					auditHeader.getAuditDetail().setModelData(tFinanceDetail);
					processCompleted = doSaveProcess(auditHeader, method);

				}

				if (!processCompleted) {
					break;
				}

				finishedTasks += (method + ";");
				FinanceDetail tFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
				serviceTasks = getServiceTasks(taskId, tFinanceDetail.getFinScheduleData().getFinanceMain(),
						finishedTasks);

			}

			FinanceDetail tFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();

			// Check whether to proceed further or not
			String nextTaskId = getNextTaskIds(taskId, tFinanceDetail.getFinScheduleData().getFinanceMain());

			if (processCompleted && nextTaskId.equals(taskId + ";")) {
				processCompleted = false;
			}

			// Proceed further to save the details in WorkFlow
			if (processCompleted) {

				if (!"".equals(nextTaskId) || "Save".equals(userAction.getSelectedItem().getLabel())) {
					setNextTaskDetails(taskId, tFinanceDetail.getFinScheduleData().getFinanceMain());
					auditHeader.getAuditDetail().setModelData(tFinanceDetail);
					processCompleted = doSaveProcess(auditHeader, null);
				}
			}

		} else {

			auditHeader = getAuditHeader(aFinanceDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);

		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws InterfaceException {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		FinanceDetail afinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain afinanceMain = afinanceDetail.getFinScheduleData().getFinanceMain();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getFinanceDetailService().delete(auditHeader, false);
						deleteNotes = true;
					} else {
						auditHeader = getFinanceDetailService().saveOrUpdate(auditHeader, false);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getFinanceDetailService().doApprove(auditHeader, false);

						if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getFinanceDetailService().doReject(auditHeader, false, false);
						if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckLimits)) {
						if (afinanceDetail.getFinScheduleData().getFinanceType().isLimitRequired()) {
							getFinanceDetailService().doCheckLimits(auditHeader);
						} else {
							afinanceDetail.getFinScheduleData().getFinanceMain().setLimitValid(true);
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_QDEFinanceMainDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_QDEFinanceMainDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);

					if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckLimits)) {

						if (overideMap.containsKey("Limit")) {
							FinanceDetail tfinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
							tfinanceDetail.getFinScheduleData().getFinanceMain().setOverrideLimit(true);
							auditHeader.getAuditDetail().setModelData(tfinanceDetail);
						}
					}
				}
			}
			setOverideMap(auditHeader.getOverideMap());
			setNextUserId(((FinanceMain) auditHeader.getAuditDetail().getModelData()).getNextUserId());

		} catch (AppException e) {
			logger.error("Exception: ", e);
		} catch (DataAccessException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// Search Button Events

	// FinanceMain Details Tab ---> 1. Basic Details

	/**
	 * when clicks on button "SearchFinType"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinType(Event event) {
		logger.debug("Entering " + event.toString());

		Object dataObject = ExtendedSearchListBox.show(this.window_QDEFinanceMainDialog, "FinanceType");
		if (dataObject instanceof String) {
			this.finType.setValue(dataObject.toString());
			this.lovDescFinTypeName.setValue("");
		} else {
			FinanceType details = (FinanceType) dataObject;
			if (details != null) {
				this.finType.setValue(details.getFinType());
				this.lovDescFinTypeName.setValue(details.getFinType() + "-" + details.getFinTypeDesc());
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when clicks on button "SearchFinCcy"
	 * 
	 * @param event
	 */
	public void onFulfill$finCcy(Event event) {
		logger.debug("Entering " + event.toString());

		this.finCcy.setConstraint("");
		Object dataObject = finCcy.getObject();
		if (dataObject instanceof String) {
			this.finCcy.setValue(dataObject.toString());
		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {
				this.finCcy.setValue(details.getCcyCode(), details.getCcyDesc());

				// To Format Amount based on the currency
				getFinanceDetail().getFinScheduleData().getFinanceMain().setFinCcy(details.getCcyCode());

				this.finAmount.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				this.finRepaymentAmount.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				this.downPayBank.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				this.downPaySupl.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				this.downPaySupl.setScale(details.getCcyEditField());
				try {
					if (getChildWindowDialogCtrl().getClass().getField("ccyFormatter") != null) {
						getChildWindowDialogCtrl().getClass().getField("ccyFormatter")
								.setInt(getChildWindowDialogCtrl(), details.getCcyEditField());

						if (getChildWindowDialogCtrl().getClass().getMethod("doSetFieldProperties") != null) {
							getChildWindowDialogCtrl().getClass().getMethod("doSetFieldProperties")
									.invoke(getChildWindowDialogCtrl());
						}
					}
				} catch (Exception e) {
				}
				if (StringUtils.isEmpty(moduleDefiner)) {
					getFinanceDetail().getFinScheduleData().getFinanceMain().setFinAmount(
							CurrencyUtil.unFormat(this.finAmount.getActualValue(), details.getCcyEditField()));
				}
			}
		}

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when clicks on button "CommitmentRef"
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InterfaceException
	 */
	public void onFulfill$commitmentRef(Event event) throws InterruptedException, InterfaceException {
		logger.debug("Entering " + event.toString());

		// fetch Limit Details from ACP Interface
		/*
		 * LimitDetail limitDetail =
		 * getLimitCheckDetails().getLimitDetails(this.finLimitRef.getValue(),this.finBranch.getValue());
		 * 
		 * //save the limitDetails if(limitDetail != null) { getLimitCheckDetails().saveOrUpdate(limitDetail); }
		 */

		logger.debug("Leaving " + event.toString());
	}

	// FinanceMain Details Tab ---> 2. Grace Period Details

	/**
	 * when clicks on button "SearchGraceBaseRate"
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$graceRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		ForwardEvent forwardEvent = (ForwardEvent) event;
		String rateType = (String) forwardEvent.getOrigin().getData();
		if (StringUtils.equals(rateType, PennantConstants.RATE_BASE)) {
			this.graceRate.getEffRateComp().setConstraint("");
			Object dataObject = graceRate.getBaseObject();

			if (dataObject instanceof String) {
				this.graceRate.setBaseValue(dataObject.toString());
				this.graceRate.setBaseDescription("");
				this.graceRate.setEffRateValue(BigDecimal.ZERO);
			} else {
				BaseRateCode details = (BaseRateCode) dataObject;
				if (details != null) {
					this.graceRate.setBaseValue(details.getBRType());
					this.graceRate.setBaseDescription(details.getBRTypeDesc());
				}
			}
			calculateRate(this.graceRate.getBaseComp(), this.graceRate.getSpecialComp(), this.graceRate.getBaseComp(),
					this.graceRate.getMarginComp(), this.graceRate.getEffRateComp(), this.finGrcMinRate,
					this.finGrcMaxRate);
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_SPECIAL)) {
			this.graceRate.getEffRateComp().setConstraint("");
			Object dataObject = graceRate.getSpecialObject();

			if (dataObject instanceof String) {
				this.graceRate.setSpecialValue(dataObject.toString());
				this.graceRate.setSpecialDescription("");
				this.graceRate.setEffRateValue(BigDecimal.ZERO);
			} else {
				SplRateCode details = (SplRateCode) dataObject;
				if (details != null) {
					this.graceRate.setSpecialValue(details.getSRType());
					this.graceRate.setSpecialDescription(details.getSRTypeDesc());
				}
			}

			calculateRate(this.graceRate.getBaseComp(), this.graceRate.getSpecialComp(), this.graceRate.getBaseComp(),
					this.graceRate.getMarginComp(), this.graceRate.getEffRateComp(), this.finGrcMinRate,
					this.finGrcMaxRate);
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_MARGIN)) {

		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when user checks the allowGrcRepay checkbox
	 * 
	 * @param event
	 */
	public void onCheck$allowGrcRepay(Event event) {
		logger.debug("Entering" + event.toString());

		if (this.allowGrcRepay.isChecked()) {
			readOnlyComponent(false, this.cbGrcSchdMthd);
			this.space_GrcSchdMthd.setStyle("background-color:red");
			fillComboBox(this.cbGrcSchdMthd, getFinanceDetail().getFinScheduleData().getFinanceMain().getGrcSchdMthd(),
					MandateUtil.getRepayMethods(), ",EQUAL,PRI_PFT,PRI,");
		} else {
			readOnlyComponent(true, this.cbGrcSchdMthd);
			this.cbGrcSchdMthd.setSelectedIndex(0);
			this.space_GrcSchdMthd.setStyle("background-color:white");
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Allow/ Not Grace In Finance
	 * 
	 * @param event
	 */
	public void onCheck$allowGrace(Event event) {
		logger.debug("Entering" + event.toString());
		doAllowGraceperiod(true);
		logger.debug("Leaving" + event.toString());
	}

	// FinanceMain Details Tab ---> 3. Repayment Period Details

	/**
	 * when clicks on button "SearchRepayBaseRate"
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$repayRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		ForwardEvent forwardEvent = (ForwardEvent) event;
		String rateType = (String) forwardEvent.getOrigin().getData();
		if (StringUtils.equals(rateType, PennantConstants.RATE_BASE)) {
			this.graceRate.getEffRateComp().setConstraint("");
			Object dataObject = repayRate.getBaseObject();

			if (dataObject instanceof String) {
				this.repayRate.setBaseValue(dataObject.toString());
				this.repayRate.setBaseDescription("");
				this.graceRate.setEffRateValue(BigDecimal.ZERO);
			} else {
				BaseRateCode details = (BaseRateCode) dataObject;
				if (details != null) {
					this.repayRate.setBaseValue(details.getBRType());
					this.repayRate.setBaseDescription(details.getBRTypeDesc());
				}
			}
			calculateRate(this.repayRate.getBaseComp(), this.repayRate.getSpecialComp(), this.repayRate.getBaseComp(),
					this.repayRate.getMarginComp(), this.graceRate.getEffRateComp(), this.finMinRate, this.finMaxRate);
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_SPECIAL)) {
			this.graceRate.getEffRateComp().setConstraint("");
			Object dataObject = repayRate.getSpecialObject();

			if (dataObject instanceof String) {
				this.repayRate.setSpecialValue(dataObject.toString());
				this.repayRate.setSpecialDescription("");
				this.graceRate.setEffRateValue(BigDecimal.ZERO);
			} else {
				SplRateCode details = (SplRateCode) dataObject;
				if (details != null) {
					this.repayRate.setSpecialValue(details.getSRType());
					this.repayRate.setSpecialDescription(details.getSRTypeDesc());
				}
			}
			calculateRate(this.repayRate.getBaseComp(), this.repayRate.getSpecialComp(), this.repayRate.getBaseComp(),
					this.repayRate.getMarginComp(), this.graceRate.getEffRateComp(), this.finGrcMinRate,
					this.finGrcMaxRate);
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_MARGIN)) {

		}
		logger.debug("Leaving " + event.toString());
	}

	// Overdue Penalty Details

	public void onCheck$applyODPenalty(Event event) {
		logger.debug("Entering" + event.toString());
		onCheckODPenalty(true);
		logger.debug("Leaving" + event.toString());
	}

	private void onCheckODPenalty(boolean checkAction) {
		if (this.applyODPenalty.isChecked()) {

			readOnlyComponent(isReadOnly("FinanceMainDialog_oDIncGrcDays"), this.oDIncGrcDays);
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDChargeType"), this.oDChargeType);
			this.oDGraceDays.setReadonly(isReadOnly("FinanceMainDialog_oDGraceDays"));
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDChargeCalOn"), this.oDChargeCalOn);
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDAllowWaiver"), this.oDAllowWaiver);

			if (checkAction) {
				readOnlyComponent(true, this.oDChargeAmtOrPerc);
				readOnlyComponent(true, this.oDMaxWaiverPerc);
			} else {
				onChangeODChargeType(false);
				onCheckODWaiver(false);
			}

		} else {
			readOnlyComponent(true, this.oDIncGrcDays);
			readOnlyComponent(true, this.oDChargeType);
			this.oDGraceDays.setReadonly(true);
			readOnlyComponent(true, this.oDChargeCalOn);
			readOnlyComponent(true, this.oDChargeAmtOrPerc);
			readOnlyComponent(true, this.oDAllowWaiver);
			readOnlyComponent(true, this.oDMaxWaiverPerc);

			checkAction = true;
		}

		if (checkAction) {
			this.oDIncGrcDays.setChecked(false);
			if (this.applyODPenalty.isChecked()) {
				this.oDChargeType.setSelectedIndex(0);
				this.oDChargeCalOn.setSelectedIndex(0);
			}
			this.oDGraceDays.setValue(0);
			this.oDChargeAmtOrPerc.setValue(BigDecimal.ZERO);
			this.oDAllowWaiver.setChecked(false);
			this.oDMaxWaiverPerc.setValue(BigDecimal.ZERO);
		}
	}

	public void onChange$oDChargeType(Event event) {
		logger.debug("Entering" + event.toString());
		onChangeODChargeType(true);
		logger.debug("Leaving" + event.toString());
	}

	private void onChangeODChargeType(boolean changeAction) {
		if (changeAction) {
			this.oDChargeAmtOrPerc.setValue(BigDecimal.ZERO);
		}
		this.space_oDChargeAmtOrPerc.setSclass("mandatory");
		if (getComboboxValue(this.oDChargeType).equals(PennantConstants.List_Select)) {
			readOnlyComponent(true, this.oDChargeAmtOrPerc);
			this.space_oDChargeAmtOrPerc.setSclass("");
		} else if (getComboboxValue(this.oDChargeType).equals(ChargeType.FLAT)
				|| getComboboxValue(this.oDChargeType).equals(ChargeType.FLAT_ON_PD_MTH)) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDChargeAmtOrPerc"), this.oDChargeAmtOrPerc);
			this.oDChargeAmtOrPerc.setMaxlength(15);
			this.oDChargeAmtOrPerc.setFormat(PennantApplicationUtil.getAmountFormate(
					CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy())));
		} else {
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDChargeAmtOrPerc"), this.oDChargeAmtOrPerc);
			this.oDChargeAmtOrPerc.setMaxlength(6);
			this.oDChargeAmtOrPerc.setFormat(PennantApplicationUtil.getAmountFormate(2));
		}
	}

	public void onCheck$oDAllowWaiver(Event event) {
		logger.debug("Entering" + event.toString());
		onCheckODWaiver(true);
		logger.debug("Leaving" + event.toString());
	}

	private void onCheckODWaiver(boolean checkAction) {
		if (checkAction) {
			this.oDMaxWaiverPerc.setValue(BigDecimal.ZERO);
		}
		if (this.oDAllowWaiver.isChecked()) {
			this.space_oDMaxWaiverPerc.setSclass("mandatory");
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDMaxWaiverPerc"), this.oDMaxWaiverPerc);
		} else {
			readOnlyComponent(true, this.oDMaxWaiverPerc);
			this.space_oDMaxWaiverPerc.setSclass("");
		}
	}

	/**
	 * Method to store the default values if no values are entered in respective fields when validate or build schedule
	 * buttons are clicked
	 * 
	 */
	public void doStoreDefaultValues() {
		// calling method to clear the constraints
		logger.debug("Entering");
		doClearMessage();
		logger.debug("Leaving");
	}

	/**
	 * Change the branch for the Account on changing the finance Branch
	 * 
	 * @param event
	 */
	public void onFulfill$finBranch(Event event) {

	}

	/**
	 * Method for Reset Customer Data
	 */
	private void setCustomerData() {
		logger.debug("Entering");

		this.custID.setValue(customer.getCustID());
		this.custCIF.setValue(customer.getCustCIF());
		this.custShrtName.setValue(customer.getCustShrtName());
		this.finBranch.setValue(customer.getCustDftBranch());
		this.finBranch.setDescription(customer.getLovDescCustDftBranchName());
		custCtgType = customer.getCustCtgCode();

		FinanceDetail financeDetail = getFinanceDetail();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String productCode = financeDetail.getFinScheduleData().getFinanceType().getFinCategory();

		financeMain.setCustID(customer.getCustID());
		setFinanceDetail(getFinanceDetailService().fetchFinCustDetails(financeDetail, custCtgType,
				financeMain.getFinType(), getRole(), FinServiceEvent.ORG));
		financeMain.setLovDescCustFName(StringUtils.trimToEmpty(customer.getCustFName()));
		financeMain.setLovDescCustLName(StringUtils.trimToEmpty(customer.getCustLName()));
		financeMain.setLovDescCustCIF(StringUtils.trimToEmpty(customer.getCustCIF()));

		// Current Finance Monthly Installment Calculation
		BigDecimal totalRepayAmount = financeMain.getTotalRepayAmt();
		int installmentMnts = DateUtil.getMonthsBetween(financeMain.getFinStartDate(), financeMain.getMaturityDate());

		BigDecimal curFinRepayAmt = totalRepayAmount.divide(new BigDecimal(installmentMnts), 0, RoundingMode.HALF_DOWN);
		int months = DateUtil.getMonthsBetween(financeMain.getFinStartDate(), financeMain.getMaturityDate());

		// Get Customer Employee Designation
		String custEmpDesg = "";
		String custEmpSector = "";
		String custEmpAlocType = "";
		String custOtherIncome = "";
		if (financeDetail.getCustomerDetails() != null
				&& financeDetail.getCustomerDetails().getCustEmployeeDetail() != null) {
			custEmpDesg = StringUtils
					.trimToEmpty(financeDetail.getCustomerDetails().getCustEmployeeDetail().getEmpDesg());
			custEmpSector = StringUtils
					.trimToEmpty(financeDetail.getCustomerDetails().getCustEmployeeDetail().getEmpSector());
			custEmpAlocType = StringUtils
					.trimToEmpty(getFinanceDetail().getCustomerDetails().getCustEmployeeDetail().getEmpAlocType());
			custOtherIncome = StringUtils
					.trimToEmpty(financeDetail.getCustomerDetails().getCustEmployeeDetail().getOtherIncome());
		}

		// Set Customer Data to check the eligibility
		financeDetail.setCustomerEligibilityCheck(
				getFinanceDetailService().getCustEligibilityDetail(customer, productCode, financeMain.getFinReference(),
						financeMain.getFinCcy(), curFinRepayAmt, months, financeMain.getCustDSR(), null));

		financeDetail.getCustomerEligibilityCheck().setReqFinAmount(financeMain.getFinAmount());
		financeDetail.getCustomerEligibilityCheck()
				.setDisbursedAmount(financeMain.getFinAmount().subtract(financeMain.getDownPayment()));
		financeDetail.getCustomerEligibilityCheck().setDownpayBank(financeMain.getDownPayBank());
		financeDetail.getCustomerEligibilityCheck().setDownpaySupl(financeMain.getDownPaySupl());
		financeDetail.getCustomerEligibilityCheck().setFinProfitRate(financeMain.getEffectiveRateOfReturn());
		financeDetail.getCustomerEligibilityCheck().setDownpayBank(financeMain.getDownPayBank());
		financeDetail.getCustomerEligibilityCheck().setDownpaySupl(financeMain.getDownPaySupl());
		financeDetail.getCustomerEligibilityCheck().setStepFinance(financeMain.isStepFinance());
		financeDetail.getCustomerEligibilityCheck().setNoOfTerms(financeMain.getNumberOfTerms());
		financeDetail.getCustomerEligibilityCheck().setFinRepayMethod(financeMain.getFinRepayMethod());
		financeDetail.getCustomerEligibilityCheck()
				.setAlwPlannedDefer(financeMain.getPlanDeferCount() > 0 ? true : false);
		financeDetail.getCustomerEligibilityCheck().setSalariedCustomer(customer.isSalariedCustomer());
		financeDetail.getCustomerEligibilityCheck().setCustEmpDesg(custEmpDesg);
		financeDetail.getCustomerEligibilityCheck().setCustEmpSector(custEmpSector);
		financeDetail.getCustomerEligibilityCheck().setCustEmpAloc(custEmpAlocType);
		financeDetail.getCustomerEligibilityCheck().setCustOtherIncome(custOtherIncome);

		// Execute Eligibility Rule and Display Result
		/*
		 * if(getEligibilityDetailDialogCtrl() != null){
		 * getEligibilityDetailDialogCtrl().doFillExecElgList(financeDetail.getFinElgRuleList()); }else{
		 * appendEligibilityDetailTab(false); }
		 * 
		 * // Scoring Detail Tab financeMain.setLovDescCustCtgTypeName(custCtgType); appendFinScoringDetailTab(false);
		 * 
		 * // Agreement Details Tab setAgreementDetailTab(this.window_QDEFinanceMainDialog);
		 */

		// Fill Check List Details based on Rule Execution if Rule Exist
		appendCheckListDetailTab(financeDetail, financeMain.isNewRecord(), false);

		if (getDocumentDetailDialogCtrl() != null) {
			getDocumentDetailDialogCtrl().doFillDocumentDetails(financeDetail.getDocumentDetailsList());
		}

		logger.debug("Leaving");
	}

	// OnBlur Events

	/**
	 * Method for getting Discrepancies based on Finance Amount
	 */
	public void onFulfill$finAmount(Event event) {
		logger.debug("Entering " + event.toString());
		this.finAmount.clearErrorMessage();
		setDownpayAmount();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Set Mandatory On DownPay Account Based on Downpayment Amount
	 * 
	 * @param event
	 */
	public void onFulfill$downPayBank(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		setDownpayAmount();
		logger.debug("Leaving " + event.toString());
	}

	public void onFulfill$downPaySupl(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		this.downPaySupl.clearErrorMessage();
		if ((this.downPaySupl.getActualValue().compareTo(BigDecimal.ZERO) > 0)
				&& (this.finAmount.getActualValue().compareTo(BigDecimal.ZERO) > 0)) {
			if (this.finAmount.getActualValue().compareTo(this.downPaySupl.getActualValue()) <= 0) {
				throw new WrongValueException(this.downPaySupl.getChildren().get(1),
						Labels.getLabel("NUMBER_MAXVALUE",
								new String[] { Labels.getLabel("label_QDEFinanceMainDialog_DownPaySupl.value"),
										Labels.getLabel("label_QDEFinanceMainDialog_FinAmount.value") }));
			}

		}
		setDownpayAmount();
		logger.debug("Leaving " + event.toString());
	}

	private void setDownpayAmount() {
		this.downPayBank.clearErrorMessage();
	}

	/**
	 * Get the Finance Main Details from the Screen
	 */
	public FinanceMain getFinanceMain() {
		FinanceMain financeMain = super.getFinanceMain();
		financeMain.setDownPayment(
				CurrencyUtil.unFormat(this.downPayBank.getActualValue().add(this.downPaySupl.getActualValue()),
						CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy())));
		return financeMain;
	}

	/**
	 * when user clicks on button "Notes"
	 * 
	 * @param event
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug("Entering " + event.toString());

		this.btnNotes.setSclass("");
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving " + event.toString());
	}

	public void onChange$custCIF(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		this.custCIF.clearErrorMessage();
		Customer customer = (Customer) PennantAppUtil.getCustomerObject(this.custCIF.getValue(), null);
		if (customer == null) {
			throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID",
					new String[] { Labels.getLabel("label_EligibilityCheck_CustCIF.value") }));
		} else {
			doSetCustomer(customer, null);
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$viewCustInfo(Event event) {
		try {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("custid", this.custID.longValue());
			map.put("custCIF", this.custCIF.getValue());
			map.put("custShrtName", this.custShrtName.getValue());
			map.put("finFormatter",
					CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy()));
			map.put("finReference", this.finReference.getValue());
			map.put("finance", true);
			if (StringUtils.equals(finDivision, FinanceConstants.FIN_DIVISION_RETAIL)) {
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/FinCustomerDetailsEnq.zul",
						window_QDEFinanceMainDialog, map);
			} else {
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/Enquiry/CustomerSummary.zul",
						window_QDEFinanceMainDialog, map);
			}
		} catch (Exception e) {
			logger.error("Exception: Opening window", e);
		}
	}

	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
		logger.debug("Entering");
		this.custCIF.clearErrorMessage();
		setCustomer((Customer) nCustomer);
		setCustomerData();
		this.custCIFSearchObject = newSearchObject;
		logger.debug("Leaving ");
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
	 * Method for Rendering Finance Schedule Detail For Maintenance purpose
	 */
	public void reRenderScheduleList(FinScheduleData aFinSchData) {
		logger.debug("Entering");
		if (getScheduleDetailDialogCtrl() != null) {
			getScheduleDetailDialogCtrl().doFillScheduleList(aFinSchData);
		}
		logger.debug("Leaving");
	}

	/**
	 * To pass Data For Agreement Child Windows Used in reflection
	 * 
	 * @return
	 * @throws Exception
	 */
	public FinanceDetail getAgrFinanceDetails() throws Exception {
		logger.debug("Entering");

		FinanceDetail aFinanceDetail = new FinanceDetail();
		aFinanceDetail = ObjectUtil.clone(getFinanceDetail());

		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		doWriteComponentsToBean(aFinanceDetail, false);

		if (aFinanceDetail.getFinScheduleData().getFinanceScheduleDetails().size() <= 0) {
			MessageUtil.showError(Labels.getLabel("label_Finance_GenSchedule"));
			return null;
		}

		// Finance Scoring Details Tab --- > Scoring Module Details Check
		// Check if any overrides exits then the overridden score count is same
		// or not
		if (getScoringDetailDialogCtrl() != null) {
			if (getScoringDetailDialogCtrl().isScoreExecuted()) {
				if (!getScoringDetailDialogCtrl().isSufficientScore()) {
					MessageUtil.showError(Labels.getLabel("label_Finance_Insufficient_Score"));
					return null;
				}
			} else {
				MessageUtil.showError(Labels.getLabel("label_Finance_Verify_Score"));
				return null;
			}
		}

		// Finance CheckList Details Tab
		if (checkListChildWindow != null) {
			boolean validationSuccess = doSave_CheckList(aFinanceDetail, true);
			if (!validationSuccess) {
				return null;
			}
		} else {
			aFinanceDetail.setFinanceCheckList(null);
		}

		aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		// Write the additional validations as per below example
		// get the selected branch object from the box
		// Do data level validations here

		// Finance Eligibility Details Tab
		if (getEligibilityDetailDialogCtrl() != null) {
			aFinanceDetail = getEligibilityDetailDialogCtrl().doSave_EligibilityList(aFinanceDetail);
		}

		// Finance Scoring Details Tab
		if (getScoringDetailDialogCtrl() != null) {
			getScoringDetailDialogCtrl().doSave_ScoreDetail(aFinanceDetail);
		} else {
			aFinanceDetail.setFinScoreHeaderList(null);
		}

		// Guaranteer Details Tab ---> Guaranteer Details
		if (getJointAccountDetailDialogCtrl() != null) {
			if (getJointAccountDetailDialogCtrl().getGuarantorDetailList() != null
					&& getJointAccountDetailDialogCtrl().getGuarantorDetailList().size() > 0) {
				getJointAccountDetailDialogCtrl().doSave_GuarantorDetail(aFinanceDetail, false);
			}
			if (getJointAccountDetailDialogCtrl().getJointAccountDetailList() != null
					&& getJointAccountDetailDialogCtrl().getJointAccountDetailList().size() > 0) {
				getJointAccountDetailDialogCtrl().doSave_JointAccountDetail(aFinanceDetail, false);
			}
		} else {
			aFinanceDetail.setJointAccountDetailList(null);
			aFinanceDetail.setGurantorsDetailList(null);
		}
		aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
		logger.debug("Leaving");
		return aFinanceDetail;
	}

	public void updateFinanceMain(FinanceMain financeMain) {
		logger.debug("Entering");
		getFinanceDetail().getFinScheduleData().setFinanceMain(financeMain);
		logger.debug("Leaving");

	}

	public void onSelect$custGenderCode(Event event) {
		logger.debug("Entering");
		if (!StringUtils.trimToEmpty(sCustGender).equals(this.custGenderCode.getSelectedItem().getValue().toString())) {
			this.custSalutationCode.setValue("");
		}
		if (StringUtils.trimToEmpty(this.custGenderCode.getSelectedItem().getValue().toString())
				.equals(PennantConstants.List_Select)) {
			this.custSalutationCode.setDisabled(true);
		} else {
			this.custSalutationCode.setDisabled(false);
		}
		sCustGender = this.custGenderCode.getSelectedItem().getValue().toString();
		fillComboBox(this.custSalutationCode, this.custSalutationCode.getValue(),
				PennantAppUtil.getSalutationCodes(sCustGender), "");
		logger.debug("Leaving");
	}

	/**
	 * This method is to fetch EID Number and calling it from DocumentTypeSelectDialogCtrl when document type is 01.
	 * 
	 */
	public String getCustomerIDNumber(String idType) {
		String idNumber = "";
		try {
			if (PennantConstants.CPRCODE.equalsIgnoreCase(idType)) {
				idNumber = this.eidNumber.getValue();
			} else if (PennantConstants.PASSPORT.equalsIgnoreCase(idType)) {
				idNumber = this.custPassportNo.getValue();
			}
		} catch (Exception e) {
			idNumber = "";
		}
		return idNumber;
	}

	@Override
	public void onSelectCheckListDetailsTab(ForwardEvent event) {
		doWriteComponentsToBean(getFinanceDetail(), false);

		if (getFinanceCheckListReferenceDialogCtrl() != null) {
			getFinanceCheckListReferenceDialogCtrl().doSetLabels(getFinBasicDetails());
			getFinanceCheckListReferenceDialogCtrl().doWriteBeanToComponents(getFinanceDetail().getCheckList(),
					getFinanceDetail().getFinanceCheckList(), false);
		}
	}

	public List<DocumentDetails> getDocumentDetails() {
		if (getDocumentDetailDialogCtrl() != null) {
			return getDocumentDetailDialogCtrl().getDocumentDetailsList();
		}
		return null;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getNextUserId() {
		return nextUserId;
	}

	public void setNextUserId(String nextUserId) {
		this.nextUserId = nextUserId;
	}

}