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
 * * FileName : SuspenseDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 31-05-2012 * * Modified
 * Date : 31-05-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-05-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.financemanagement.suspense;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.customermasters.impl.CustomerDataService;
import com.pennant.backend.service.financemanagement.SuspenseService;
import com.pennant.backend.service.lmtmasters.FinanceReferenceDetailService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.core.EventManager.Notify;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.FinanceBaseCtrl;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.notifications.service.NotificationService;

/**
 * This is the controller class for the /WEB-INF/pages/FinanceManagement/Suspense/SusoenseDialog.zul file.
 */
public class SuspenseDialogCtrl extends FinanceBaseCtrl<FinanceSuspHead> {
	private static final long serialVersionUID = 7798200490595650451L;
	private static final Logger logger = LogManager.getLogger(SuspenseDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SuspenseDialog; // autowired
	protected ExtendedCombobox finReference; // autowired
	protected Textbox finBranch; // autowired
	protected Textbox finType; // autowired
	protected Longbox custID; // autowired
	protected Textbox lovDescCustCIF; // autowired
	protected Label custShrtName; // autowired
	protected Intbox finSuspSeq; // autowired
	protected Checkbox finIsInSusp; // autowired
	protected Checkbox manualSusp; // autowired
	protected Decimalbox finSuspAmt; // autowired
	protected Decimalbox finCurSuspAmt; // autowired
	protected Datebox finSuspDate; // autowired
	protected Datebox finSuspTrfDate; // autowired

	// not auto wired vars
	private FinanceSuspHead suspHead; // overhanded per param
	private transient SuspenseListCtrl suspenseListCtrl; // overhanded per param

	protected Label recordStatus; // autowired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;

	private transient boolean validationOn;

	private String menuItemRightName = null;

	// ServiceDAOs / Domain Classes
	private Map<String, List<ErrorDetail>> overideMap = new HashMap<String, List<ErrorDetail>>();
	private transient SuspenseService suspenseService;
	private FinanceReferenceDetailService financeReferenceDetailService;
	private CustomerDataService customerDataService;
	private FinanceWorkFlowService financeWorkFlowService;
	private NotificationService notificationService;
	private FinanceMain financeMain;

	/**
	 * default constructor.<br>
	 */
	public SuspenseDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SuspenseDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected Suspense object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_SuspenseDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SuspenseDialog);

		try {

			// READ OVERHANDED params !
			if (arguments.containsKey("suspHead")) {
				this.suspHead = (FinanceSuspHead) arguments.get("suspHead");
				FinanceSuspHead befImage = new FinanceSuspHead();
				BeanUtils.copyProperties(this.suspHead, befImage);
				this.suspHead.setBefImage(befImage);
				setFinanceDetail(this.suspHead.getFinanceDetail());
				setSuspHead(this.suspHead);
			} else {
				setSuspHead(null);
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
			if (arguments.containsKey("financeMain")) {
				financeMain = (FinanceMain) arguments.get("financeMain");
			}
			if (!getSuspHead().isNewRecord()) {
				doLoadWorkFlow(this.suspHead);
			}

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateMenuRoleAuthorities(getRole(), "SuspenseDialog", menuItemRightName);
				for (int i = 0; i < userAction.getItemCount(); i++) {
					userAction.getItemAtIndex(i).setDisabled(false);
				}

				if (getSuspHead().isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
				}
			} else {
				this.btnCtrl.setBtnStatus_New();
				btnCancel.setVisible(true);
			}

			/* set components visible dependent of the users rights */
			isEnquiry = true;// For Schedule in Enquiry mode
			doCheckRights();

			// READ OVERHANDED params !
			// we get the SuspenseListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete Suspense here.
			if (arguments.containsKey("suspenseListCtrl")) {
				setSuspenseListCtrl((SuspenseListCtrl) arguments.get("suspenseListCtrl"));
			} else {
				setSuspenseListCtrl(null);
			}

			if (getSuspHead().isNewRecord()) {
				doSetFieldProperties();
				doShowSuspenseDialog(financeMain);
			} else {
				doShowDialog(getSuspHead());
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_SuspenseDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());

	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	protected void doSetFieldProperties() {
		logger.debug("Entering");

		int format = CurrencyUtil.getFormat(getSuspHead().getFinCcy());

		// Empty sent any required attributes
		this.finReference.setMandatoryStyle(true);
		this.finReference.setModuleName("FinanceMain");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setDescColumn("FinType");
		this.finReference.setValidateColumns(new String[] { "FinReference" });
		this.finBranch.setMaxlength(LengthConstants.LEN_BRANCH);
		this.finType.setMaxlength(8);
		this.custID.setMaxlength(19);
		this.finSuspAmt.setMaxlength(18);
		this.finSuspAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.finCurSuspAmt.setMaxlength(18);
		this.finCurSuspAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.finSuspDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.finSuspTrfDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnSave(Event event) {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_SuspenseDialog);
		logger.debug("Leaving" + event.toString());
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
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aSuspense Suspense
	 */
	public void doWriteBeanToComponents(FinanceSuspHead aSuspHead) {
		logger.debug("Entering");

		int format = CurrencyUtil.getFormat(aSuspHead.getFinCcy());

		this.finReference.setValue(aSuspHead.getFinReference());
		this.finBranch.setValue(aSuspHead.getFinBranch());
		this.finType.setValue(aSuspHead.getFinType());
		this.custID.setValue(aSuspHead.getCustId());
		this.lovDescCustCIF.setValue(aSuspHead.getLovDescCustCIFName());
		this.custShrtName.setValue(aSuspHead.getLovDescCustShrtName());
		this.finSuspSeq.setValue(aSuspHead.getFinSuspSeq());
		this.finIsInSusp.setChecked(aSuspHead.isFinIsInSusp());
		this.manualSusp.setChecked(aSuspHead.isManualSusp());
		this.finSuspAmt.setValue(CurrencyUtil.parse(aSuspHead.getFinSuspAmt(), format));
		this.finCurSuspAmt.setValue(CurrencyUtil.parse(aSuspHead.getFinCurSuspAmt(), format));
		this.finSuspDate.setValue(aSuspHead.getFinSuspDate());
		this.finSuspTrfDate.setValue(aSuspHead.getFinSuspTrfDate());
		if (aSuspHead.getFinSuspDate() == null) {
			Date appDate = SysParamUtil.getAppDate();
			this.finSuspDate.setValue(appDate);
			this.finSuspTrfDate.setValue(appDate);
		}

		this.recordStatus.setValue(aSuspHead.getRecordStatus());

		// Tabs Appending

		getFinanceDetail().setModuleDefiner(moduleDefiner);

		// Customer Details Tab
		appendCustomerDetailTab();

		// Fee Details Tab
		appendFeeDetailTab(true);

		// Schedule Details
		appendScheduleDetailTab(true, false);

		// Agreement Details Tab
		appendAgreementsDetailTab(true);

		// Check List Details Tab
		appendCheckListDetailTab(getFinanceDetail(), false, true);

		// Recommendation Details Tab
		appendRecommendDetailTab(true);

		// Document Details Tab
		appendDocumentDetailTab();

		// Stage Accounting Details
		appendStageAccountingDetailsTab(true);

		// Show Accounting Tab Details Based upon Role Condition using Work flow
		if ("Accounting".equals(getTaskTabs(getTaskId(getRole())))) {
			appendAccountingDetailTab(true);
		}
		logger.debug("Leaving");
	}

	@Override
	public void onSelectCheckListDetailsTab(ForwardEvent event) {
		this.doWriteComponentsToBean(suspHead);

		if (getCustomerDialogCtrl() != null && getCustomerDialogCtrl().getCustomerDetails() != null) {
			getCustomerDialogCtrl().doSetLabels(getFinBasicDetails());
			getCustomerDialogCtrl().doSave_CustomerDetail(getFinanceDetail(), custDetailTab, false);
		}

		if (getFinanceCheckListReferenceDialogCtrl() != null) {
			getFinanceCheckListReferenceDialogCtrl().doSetLabels(getFinBasicDetails());
			getFinanceCheckListReferenceDialogCtrl().doWriteBeanToComponents(getFinanceDetail().getCheckList(),
					getFinanceDetail().getFinanceCheckList(), false);
		}
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSuspHead
	 */
	public void doWriteComponentsToBean(FinanceSuspHead aSuspHead) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		int format = CurrencyUtil.getFormat(aSuspHead.getFinCcy());

		try {
			aSuspHead.setFinReference(this.finReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSuspHead.setFinBranch(this.finBranch.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSuspHead.setFinType(this.finType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSuspHead.setCustId(this.custID.longValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSuspHead.setFinIsInSusp(this.finIsInSusp.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSuspHead.setManualSusp(this.manualSusp.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSuspHead.setFinSuspAmt(CurrencyUtil.unFormat(this.finSuspAmt.getValue(), format));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSuspHead.setFinCurSuspAmt(CurrencyUtil.unFormat(this.finCurSuspAmt.getValue(), format));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aSuspHead.setFinSuspDate(this.finSuspDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSuspHead.setFinSuspTrfDate(this.finSuspTrfDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSuspHead.setFinSuspSeq(this.finSuspSeq.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
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
	 * @param aSuspHead
	 */
	public void doShowDialog(FinanceSuspHead aSuspHead) {
		logger.debug("Entering");

		// set Readonly mode accordingly if the object is new or not.
		if (aSuspHead.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.finReference.focus();
		} else {
			this.finBranch.focus();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				// doReadOnly();
				doEdit();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aSuspHead);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_SuspenseDialog.onClose();
		}

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

		getUserWorkspace().allocateAuthorities("SuspenseDialog", getRole(), menuItemRightName);

		this.btnNew.setVisible(false);
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SuspenseDialog_btnSave"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SuspenseDialog_btnEdit"));
		this.btnNotes.setVisible(false);
		this.btnDelete.setVisible(false);
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	public void doEdit() {
		logger.debug("Entering");

		if (getSuspHead().isNewRecord()) {
			this.finReference.setReadonly(false);
		} else {
			this.finReference.setReadonly(true);
		}

		this.finBranch.setReadonly(true);
		this.finType.setReadonly(true);
		this.custID.setReadonly(true);
		this.manualSusp.setDisabled(isReadOnly("SuspenseDialog_manualSusp"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.suspHead.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(false);
		}

		logger.debug("Leaving");
	}

	public void doShowSuspenseDialog(FinanceMain main) {

		if (main != null) {
			this.finReference.setValue(main.getFinReference());
			getSuspHead().setFinCcy(main.getFinCcy());
			doSetFieldProperties();

			// Workflow Details
			setWorkflowDetails(main.getFinType());
			getSuspHead().setWorkflowId(getWorkFlowId());
			doLoadWorkFlow(getSuspHead());

			// Fetch Total Finance Details Object
			FinanceDetail financeDetail = getFinanceDetailService().getFinSchdDetailById(main.getFinID(), "_View",
					false);
			financeDetail.getFinScheduleData().getFinanceMain().setNewRecord(true);
			financeDetail.setCustomerDetails(customerDataService.getCustomerDetailsbyID(
					financeDetail.getFinScheduleData().getFinanceMain().getCustID(), true, "_View"));
			financeDetail = getFinanceDetailService().getFinanceReferenceDetails(financeDetail, getRole(), "DDE",
					eventCode, moduleDefiner, false);

			getSuspHead().setFinReference(main.getFinReference());
			getSuspHead().setFinBranch(main.getFinBranch());
			getSuspHead().setFinType(main.getFinType());
			getSuspHead().setCustId(main.getCustID());
			getSuspHead().setLovDescCustCIFName(main.getLovDescCustCIF());
			getSuspHead().setLovDescCustShrtName(main.getLovDescCustShrtName());
			getSuspHead().setFinSuspSeq(1);
			getSuspHead().setFinIsInSusp(false);
			getSuspHead().setManualSusp(false);

			// Outstanding Suspense Amount Calculation
			BigDecimal totSuspAmt = BigDecimal.ZERO;
			for (FinanceScheduleDetail curSchd : financeDetail.getFinScheduleData().getFinanceScheduleDetails()) {
				totSuspAmt = totSuspAmt.add(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
			}

			getSuspHead().setFinSuspAmt(totSuspAmt);
			getSuspHead().setFinCurSuspAmt(totSuspAmt);
			Date appDate = SysParamUtil.getAppDate();
			getSuspHead().setFinSuspDate(appDate);
			getSuspHead().setFinSuspTrfDate(appDate);

			setFinanceDetail(financeDetail);
			getSuspHead().setFinanceDetail(financeDetail);
			this.tabpanelsBoxIndexCenter.setVisible(true);

			if (isWorkFlowEnabled()) {
				this.groupboxWf.setVisible(true);
				this.userAction = setListRecordStatus(this.userAction);
				for (int i = 0; i < userAction.getItemCount(); i++) {
					userAction.getItemAtIndex(i).setDisabled(false);
				}
				if (getSuspHead().isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
				}
				this.groupboxWf.setVisible(true);
				getUserWorkspace().allocateMenuRoleAuthorities(getRole(), "SuspenseDialog", menuItemRightName);
				doShowDialog(getSuspHead());
			}
			this.finReference.setReadonly(true);
			setDialog(DialogType.EMBEDDED);
		}
	}

	public void onFulfill$finReference(Event event) {
		Object dataObject = finReference.getObject();
		if (dataObject instanceof String) {
			this.finReference.setDescription("");
			this.finType.setValue("");
			this.finBranch.setValue("");
			this.custID.setText("");
			this.custShrtName.setValue("");
			this.lovDescCustCIF.setValue("");
		} else {
			FinanceMain main = (FinanceMain) dataObject;
			if (main != null) {
				getSuspHead().setFinCcy(main.getFinCcy());

				doSetFieldProperties();

				// Workflow Details
				setWorkflowDetails(main.getFinType());
				getSuspHead().setWorkflowId(getWorkFlowId());
				doLoadWorkFlow(getSuspHead());

				// Fetch Total Finance Details Object
				FinanceDetail financeDetail = getFinanceDetailService().getFinSchdDetailById(main.getFinID(), "_View",
						false);
				financeDetail.getFinScheduleData().getFinanceMain().setNewRecord(true);
				financeDetail.setCustomerDetails(customerDataService.getCustomerDetailsbyID(
						financeDetail.getFinScheduleData().getFinanceMain().getCustID(), true, "_View"));
				financeDetail = getFinanceDetailService().getFinanceReferenceDetails(financeDetail, getRole(), "DDE",
						eventCode, moduleDefiner, false);

				getSuspHead().setFinReference(main.getFinReference());
				getSuspHead().setFinBranch(main.getFinBranch());
				getSuspHead().setFinType(main.getFinType());
				getSuspHead().setCustId(main.getCustID());
				getSuspHead().setLovDescCustCIFName(main.getLovDescCustCIF());
				getSuspHead().setLovDescCustShrtName(main.getLovDescCustShrtName());
				getSuspHead().setFinSuspSeq(1);
				getSuspHead().setFinIsInSusp(false);
				getSuspHead().setManualSusp(false);

				// Outstanding Suspense Amount Calculation
				BigDecimal totSuspAmt = BigDecimal.ZERO;
				for (FinanceScheduleDetail curSchd : financeDetail.getFinScheduleData().getFinanceScheduleDetails()) {
					totSuspAmt = totSuspAmt.add(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
				}

				getSuspHead().setFinSuspAmt(totSuspAmt);
				getSuspHead().setFinCurSuspAmt(totSuspAmt);
				Date appDate = SysParamUtil.getAppDate();
				getSuspHead().setFinSuspDate(appDate);
				getSuspHead().setFinSuspTrfDate(appDate);

				getSuspHead().setFinReference(main.getFinReference());
				getSuspHead().setFinBranch(main.getFinBranch());
				getSuspHead().setFinType(main.getFinType());
				getSuspHead().setCustId(main.getCustID());
				getSuspHead().setLovDescCustCIFName(main.getLovDescCustCIF());
				getSuspHead().setLovDescCustShrtName(main.getLovDescCustShrtName());
				getSuspHead().setFinSuspSeq(1);
				getSuspHead().setFinIsInSusp(false);
				getSuspHead().setManualSusp(false);

				setFinanceDetail(financeDetail);
				getSuspHead().setFinanceDetail(financeDetail);
				this.tabpanelsBoxIndexCenter.setVisible(true);

				if (isWorkFlowEnabled()) {
					this.groupboxWf.setVisible(true);
					this.userAction = setListRecordStatus(this.userAction);
					for (int i = 0; i < userAction.getItemCount(); i++) {
						userAction.getItemAtIndex(i).setDisabled(false);
					}
					if (getSuspHead().isNewRecord()) {
						this.btnCtrl.setBtnStatus_Edit();
						btnCancel.setVisible(false);
					} else {
						this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
					}
					this.groupboxWf.setVisible(true);
					getUserWorkspace().allocateMenuRoleAuthorities(getRole(), "SuspenseDialog", menuItemRightName);
					doShowDialog(getSuspHead());
				}

				this.finReference.setReadonly(true);
				setDialog(DialogType.EMBEDDED);
			}
		}
	}

	private void setWorkflowDetails(String finType) {

		// Finance Maintenance Workflow Check & Assignment
		WorkFlowDetails workFlowDetails = null;
		if (StringUtils.isNotEmpty(moduleDefiner)) {
			FinanceWorkFlow financeWorkflow = getFinanceWorkFlowService().getApprovedFinanceWorkFlowById(finType,
					moduleDefiner, PennantConstants.WORFLOW_MODULE_FINANCE);// TODO: Check Promotion case
			if (financeWorkflow != null && financeWorkflow.getWorkFlowType() != null) {
				workFlowDetails = WorkFlowUtil.getDetailsByType(financeWorkflow.getWorkFlowType());
			}
		}

		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	}

	private void doLoadWorkFlow(FinanceSuspHead suspHead) {
		logger.debug("Entering");
		String roleCode = null;
		if (!suspHead.isNewRecord() && StringUtils.trimToEmpty(suspHead.getNextTaskId()).contains(";")) {
			roleCode = getFinanceDetailService().getUserRoleCodeByRefernce(
					getUserWorkspace().getUserDetails().getUserId(), suspHead.getFinReference(),
					getUserWorkspace().getUserRoles());
		}

		if (null == roleCode) {
			doLoadWorkFlow(suspHead.isWorkflow(), suspHead.getWorkflowId(), suspHead.getNextTaskId());
		} else {
			doLoadWorkFlow(suspHead.isWorkflow(), suspHead.getWorkflowId(), null, roleCode);
		}
		logger.debug("Entering");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.finReference.setConstraint("");
		this.finBranch.setConstraint("");
		this.finType.setConstraint("");
		this.custID.setConstraint("");
		this.lovDescCustCIF.setConstraint("");
		this.finSuspSeq.setConstraint("");
		this.finSuspAmt.setConstraint("");
		this.finCurSuspAmt.setConstraint("");
		this.finSuspDate.setConstraint("");
		this.finSuspTrfDate.setConstraint("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Saves the components to table.
	 */
	public void doSave() {
		logger.debug("Entering");

		final FinanceSuspHead aFinanceSuspHead = new FinanceSuspHead();
		BeanUtils.copyProperties(getSuspHead(), aFinanceSuspHead);

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		doWriteComponentsToBean(aFinanceSuspHead);

		// fill the Suspense object with the components data
		FinanceDetail aFinanceDetail = aFinanceSuspHead.getFinanceDetail();
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		if (this.userAction.getSelectedItem() != null) {
			if ("Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| "Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| this.userAction.getSelectedItem().getLabel().contains("Reject")
					|| this.userAction.getSelectedItem().getLabel().contains("Resubmit")
					|| this.userAction.getSelectedItem().getLabel().contains("Decline")) {
				recSave = true;
				aFinanceDetail.setActionSave(true);
			}
			aFinanceDetail.setUserAction(this.userAction.getSelectedItem().getLabel());
		}

		aFinanceDetail.setAccountingEventCode(eventCode);
		aFinanceDetail.setModuleDefiner(StringUtils.isEmpty(moduleDefiner) ? FinServiceEvent.ORG : moduleDefiner);

		// Document Details Saving
		if (getDocumentDetailDialogCtrl() != null) {
			aFinanceDetail.setDocumentDetailsList(getDocumentDetailDialogCtrl().getDocumentDetailsList());
		} else {
			aFinanceDetail.setDocumentDetailsList(null);
		}

		// Finance Stage Accounting Details Tab
		if (!recSave && getStageAccountingDetailDialogCtrl() != null) {
			// check if accounting rules executed or not
			if (!getStageAccountingDetailDialogCtrl().isStageAccountingsExecuted()) {
				MessageUtil.showError(Labels.getLabel("label_Finance_Calc_StageAccountings"));
				return;
			}
			if (getStageAccountingDetailDialogCtrl().getStageDisbCrSum()
					.compareTo(getStageAccountingDetailDialogCtrl().getStageDisbDrSum()) != 0) {
				MessageUtil.showError(Labels.getLabel("label_Finance_Acc_NotMatching"));
				return;
			}
		} else {
			aFinanceDetail.setStageAccountingList(null);
		}

		// Finance Accounting Details
		if (!recSave && getAccountingDetailDialogCtrl() != null) {
			// check if accounting rules executed or not
			if (!getAccountingDetailDialogCtrl().isAccountingsExecuted()) {
				MessageUtil.showError(Labels.getLabel("label_Finance_Calc_Accountings"));
				return;
			}
			if (getAccountingDetailDialogCtrl().getDisbCrSum()
					.compareTo(getAccountingDetailDialogCtrl().getDisbDrSum()) != 0) {
				MessageUtil.showError(Labels.getLabel("label_Finance_Acc_NotMatching"));
				return;
			}
		}

		// Finance CheckList Details Tab
		if (checkListChildWindow != null) {
			boolean validationSuccess = doSave_CheckList(aFinanceDetail, false);
			if (!validationSuccess) {
				return;
			}
		} else {
			aFinanceDetail.setFinanceCheckList(null);
		}

		// Write the additional validations as per below example
		String tranType = "";
		boolean isNew = aFinanceSuspHead.isNewRecord();
		if (isWorkFlowEnabled()) {

			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinanceSuspHead.getRecordType())) {
				aFinanceSuspHead.setVersion(aFinanceSuspHead.getVersion() + 1);
				if (isNew) {
					aFinanceSuspHead.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinanceSuspHead.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinanceSuspHead.setNewRecord(true);
				}
			}

		} else {
			aFinanceSuspHead.setVersion(aFinanceSuspHead.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			if (doProcess(aFinanceSuspHead, tranType)) {

				refreshList();

				// Customer Notification for Role Identification
				if (StringUtils.isBlank(aFinanceSuspHead.getNextTaskId())) {
					aFinanceSuspHead.setNextRoleCode("");
				}
				String msg = PennantApplicationUtil.getSavingStatus(aFinanceSuspHead.getRoleCode(),
						aFinanceSuspHead.getNextRoleCode(), aFinanceSuspHead.getFinReference(), " Suspense ",
						aFinanceSuspHead.getRecordStatus());
				Clients.showNotification(msg, "info", null, null, -1);

				// Mail Alert Notification for Customer/Dealer/Provider...etc
				if (!"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())) {

					FinanceMain financeMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
					Notification notification = new Notification();
					notification.getTemplates().add(NotificationConstants.TEMPLATE_FOR_AE);
					notification.getTemplates().add(NotificationConstants.TEMPLATE_FOR_CN);
					notification.setModule("SUSPENSE");
					notification.setSubModule(moduleDefiner);
					notification.setKeyReference(financeMain.getFinReference());
					notification.setStage(financeMain.getRoleCode());
					notification.setReceivedBy(getUserWorkspace().getUserId());
					notificationService.sendNotifications(notification, aFinanceDetail, financeMain.getFinType(),
							aFinanceDetail.getDocumentDetailsList());
				}

				// User Notifications Message/Alert
				FinanceMain fm = aFinanceDetail.getFinScheduleData().getFinanceMain();
				if (fm.getNextUserId() != null) {
					publishNotification(Notify.USER, fm.getFinReference(), fm);
				} else {
					if (!SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_DIVISION_BASED_CLUSTER)) {
						publishNotification(Notify.ROLE, fm.getFinReference(), fm);
					} else {
						publishNotification(Notify.ROLE, fm.getFinReference(), fm, finDivision,
								aFinanceMain.getFinBranch());
					}
				}

				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Processing Finance Detail Object for Database Operation
	 * 
	 * @param afinanceMain
	 * @param tranType
	 * @return
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */
	protected boolean doProcess(FinanceSuspHead aFinanceSuspHead, String tranType)
			throws InterruptedException, InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		logger.debug("Entering");
		boolean processCompleted = true;
		AuditHeader auditHeader = null;

		aFinanceSuspHead.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFinanceSuspHead.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinanceSuspHead.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			aFinanceSuspHead.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			// Check for service tasks. If one exists perform the task(s)
			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, aFinanceSuspHead, finishedTasks);

			if (isNotesMandatory(taskId, aFinanceSuspHead)) {
				if (!notesEntered) {
					MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
					return false;
				}
			}

			auditHeader = getAuditHeader(aFinanceSuspHead, PennantConstants.TRAN_WF);

			while (!"".equals(serviceTasks)) {

				String method = serviceTasks.split(";")[0];

				if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckCollaterals)) {
					processCompleted = true;
				} else {
					FinanceSuspHead tFinanceSuspHead = (FinanceSuspHead) auditHeader.getAuditDetail().getModelData();
					setNextTaskDetails(taskId, aFinanceSuspHead);
					auditHeader.getAuditDetail().setModelData(tFinanceSuspHead);
					processCompleted = doSaveProcess(auditHeader, method);

				}

				if (!processCompleted) {
					break;
				}

				finishedTasks += (method + ";");
				FinanceSuspHead tFinanceSuspHead = (FinanceSuspHead) auditHeader.getAuditDetail().getModelData();
				serviceTasks = getServiceTasks(taskId, tFinanceSuspHead, finishedTasks);

			}

			FinanceSuspHead tFinanceSuspHead = (FinanceSuspHead) auditHeader.getAuditDetail().getModelData();

			// Check whether to proceed further or not
			String nextTaskId = getNextTaskIds(taskId, tFinanceSuspHead);

			if (processCompleted && nextTaskId.equals(taskId + ";")) {
				processCompleted = false;
			}

			// Proceed further to save the details in WorkFlow
			if (processCompleted) {

				if (!"".equals(nextTaskId) || "Save".equals(userAction.getSelectedItem().getLabel())) {
					setNextTaskDetails(taskId, aFinanceSuspHead);
					auditHeader.getAuditDetail().setModelData(tFinanceSuspHead);
					processCompleted = doSaveProcess(auditHeader, null);
				}
			}

		} else {
			auditHeader = getAuditHeader(aFinanceSuspHead, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	protected String getServiceTasks(String taskId, FinanceSuspHead suspHead, String finishedTasks) {
		logger.debug("Entering");
		// changes regarding parallel work flow
		String nextRoleCode = StringUtils.trimToEmpty(suspHead.getNextRoleCode());
		String nextRoleCodes[] = nextRoleCode.split(",");

		if (nextRoleCodes.length > 1) {
			return "";
		}

		String serviceTasks = getServiceOperations(taskId, suspHead);
		if (!"".equals(finishedTasks)) {
			String[] list = finishedTasks.split(";");

			for (int i = 0; i < list.length; i++) {
				serviceTasks = serviceTasks.replace(list[i] + ";", "");
			}
		}
		logger.debug("Leaving");
		return serviceTasks;
	}

	protected void setNextTaskDetails(String taskId, FinanceSuspHead suspHead) {
		logger.debug("Entering");

		// Set the next task id
		String action = userAction.getSelectedItem().getLabel();
		String nextTaskId = StringUtils.trimToEmpty(suspHead.getNextTaskId());

		if ("".equals(nextTaskId)) {
			if ("Save".equals(action)) {
				nextTaskId = taskId + ";";
			}
		} else {
			if ("Resubmit".equals(action)) {
				nextTaskId = "";
			} else if (!"Save".equals(action)) {
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
			}
		}

		if ("".equals(nextTaskId)) {
			nextTaskId = getNextTaskIds(taskId, suspHead);
		}

		// Set the role codes for the next tasks
		String nextRoleCode = "";
		String nextRole = "";
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
					nextRole = getTaskOwner(nextTasks[i]);
					nextRoleCode += nextRole;
					String baseRole = "";
					if (!"Resubmit".equals(action)) {
						baseRole = StringUtils.trimToEmpty(getTaskBaseRole(nextTasks[i]));
					}
					baseRoleMap.put(nextRole, baseRole);
				}
			}
		}

		suspHead.setTaskId(taskId);
		suspHead.setNextTaskId(nextTaskId);
		suspHead.setRoleCode(getRole());
		suspHead.setNextRoleCode(nextRoleCode);

		logger.debug("Leaving");
	}

	/**
	 * Method for Processing Workflow Method
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		FinanceSuspHead aFinanceSuspHead = (FinanceSuspHead) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getSuspenseService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getSuspenseService().saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getSuspenseService().doApprove(auditHeader);

					if (aFinanceSuspHead.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getSuspenseService().doReject(auditHeader);

					if (aFinanceSuspHead.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_SuspenseDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_SuspenseDialog, auditHeader);
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
			}
		}

		setOverideMap(auditHeader.getOverideMap());
		logger.debug("Leaving");
		return processCompleted;

	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		if (this.finReference.isVisible()) {
			this.finReference.setConstraint(new PTStringValidator(
					Labels.getLabel("label_SuspenseDialog_FinReference.value"), null, true, true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for return Document detail list object
	 * 
	 * @return
	 */
	public List<DocumentDetails> getDocumentDetails() {
		logger.debug("Entering");

		if (getFinanceDetail() != null) {
			return getFinanceDetail().getDocumentDetailsList();
		}
		logger.debug("Leaving");
		return null;
	}

	/**
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	public ArrayList<Object> getFinBasicDetails() {
		ArrayList<Object> arrayList = new ArrayList<Object>();
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		arrayList.add(0, financeMain.getFinType());
		arrayList.add(1, financeMain.getFinCcy());
		arrayList.add(2, financeMain.getScheduleMethod());
		arrayList.add(3, financeMain.getFinReference());
		arrayList.add(4, financeMain.getProfitDaysBasis());
		arrayList.add(5, financeMain.getGrcPeriodEndDate());
		arrayList.add(6, financeMain.isAllowGrcPeriod());
		FinanceType fianncetype = getFinanceDetail().getFinScheduleData().getFinanceType();
		if (fianncetype != null && !"".equals(fianncetype.getProduct())) {
			arrayList.add(7, true);
		} else {
			arrayList.add(7, false);
		}
		arrayList.add(8, getFinanceDetail().getFinScheduleData().getFinanceType().getFinCategory());
		arrayList.add(9, getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescCustShrtName());
		arrayList.add(10, false);
		arrayList.add(11, moduleDefiner);
		return arrayList;
	}

	public FinanceMain getFinanceMain() {
		return getFinanceDetail().getFinScheduleData().getFinanceMain();
	}

	/**
	 * Method for Executing Eligibility Details
	 */
	public void onExecuteAccountingDetail(Boolean onLoadProcess) {
		logger.debug("Entering");

		getAccountingDetailDialogCtrl().getLabel_AccountingDisbCrVal().setValue("");
		getAccountingDetailDialogCtrl().getLabel_AccountingDisbDrVal().setValue("");

		// Finance Accounting Details Execution
		executeAccounting(onLoadProcess);
		logger.debug("Leaving");
	}

	/**
	 * Method for Executing Eligibility Details
	 * 
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public FinanceDetail onExecuteStageAccDetail()
			throws InterruptedException, IllegalAccessException, InvocationTargetException {
		getFinanceDetail().setModuleDefiner(StringUtils.isEmpty(moduleDefiner) ? FinServiceEvent.ORG : moduleDefiner);
		return getFinanceDetail();
	}

	/**
	 * Method for Executing Accounting tab Rules
	 */
	private void executeAccounting(boolean onLoadProcess) {
		logger.debug("Entering");

		FinanceDetail fd = getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		FinanceProfitDetail profitDetail = financeDetailService.getFinProfitDetailsById(fm.getFinID());
		Date dateValueDate = SysParamUtil.getAppValueDate();

		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();
		aeEvent = AEAmounts.procAEAmounts(fm, schedules, profitDetail, eventCode, dateValueDate, dateValueDate);

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

		Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

		List<ReturnDataSet> returnSetEntries = null;

		aeEvent.setDataMap(dataMap);

		aeEvent.setDataMap(dataMap);
		getEngineExecution().getAccEngineExecResults(aeEvent);

		returnSetEntries = aeEvent.getReturnDataSet();

		if (getAccountingDetailDialogCtrl() != null) {
			getAccountingDetailDialogCtrl().doFillAccounting(returnSetEntries);
			getAccountingDetailDialogCtrl().getFinanceDetail().setReturnDataSetList(returnSetEntries);
		}

		logger.debug("Leaving");
	}

	private AuditHeader getAuditHeader(FinanceSuspHead aFinanceSuspHead, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinanceSuspHead.getBefImage(), aFinanceSuspHead);
		return new AuditHeader(aFinanceSuspHead.getFinReference(), null, null, null, auditDetail,
				aFinanceSuspHead.getUserDetails(), getOverideMap());
	}

	public void onClick$btnNotes(Event event) {
		logger.debug("Entering");
		// logger.debug(event.toString());

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getSuspenseListCtrl().search();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public SuspenseListCtrl getSuspenseListCtrl() {
		return suspenseListCtrl;
	}

	public void setSuspenseListCtrl(SuspenseListCtrl suspenseListCtrl) {
		this.suspenseListCtrl = suspenseListCtrl;
	}

	public SuspenseService getSuspenseService() {
		return suspenseService;
	}

	public void setSuspenseService(SuspenseService suspenseService) {
		this.suspenseService = suspenseService;
	}

	public FinanceSuspHead getSuspHead() {
		return suspHead;
	}

	public void setSuspHead(FinanceSuspHead suspHead) {
		this.suspHead = suspHead;
	}

	public void setOverideMap(Map<String, List<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public Map<String, List<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	public FinanceReferenceDetailService getFinanceReferenceDetailService() {
		return financeReferenceDetailService;
	}

	public void setFinanceReferenceDetailService(FinanceReferenceDetailService financeReferenceDetailService) {
		this.financeReferenceDetailService = financeReferenceDetailService;
	}

	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return financeWorkFlowService;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	@Autowired
	public void setCustomerDataService(CustomerDataService customerDataService) {
		this.customerDataService = customerDataService;
	}

}
