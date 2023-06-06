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
 * * FileName : LiabilityRequestDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 31-12-2015 * *
 * Modified Date : 31-12-2015 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-12-2015 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.financemanagement.liability;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.app.util.TDSCalculator;
import com.pennant.backend.dao.collateral.CollateralAssignmentDAO;
import com.pennant.backend.dao.finance.JointAccountDetailDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.AgreementDefinition;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.AgreementDetail;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.liability.LiabilityRequest;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.finance.liability.service.LiabilityRequestService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.core.EventManager.Notify;
import com.pennant.pff.mandate.MandateUtil;
import com.pennant.util.ErrorControl;
import com.pennant.webui.finance.financemain.FinanceMainBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceType;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.overdue.constants.ChargeType;
import com.pennapps.core.util.ObjectUtil;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Finance Management/LiabilityRequest/liabilityRequestDialog.zul
 * file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class LiabilityRequestDialogCtrl extends FinanceMainBaseCtrl {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = LogManager.getLogger(LiabilityRequestDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_LiabilityFinanceMainDialog;

	protected Row row_ClaimReason;
	protected Combobox insClaimReason;
	protected CurrencyBox insClaimAmount;
	protected Row row_InsPaidStatus;
	protected Combobox insPaidStatus;

	// Escrow row
	protected Row row_Escrow;
	protected Checkbox escrow;
	protected ExtendedCombobox customerBankAcct;

	protected ExtendedCombobox liabilityRef;

	private LiabilityRequest liabilityRequest;
	private LiabilityRequestListCtrl liabilityRequestListCtrl;

	private LiabilityRequestService liabilityRequestService;
	private FinanceWorkFlowService financeWorkFlowService;
	protected CollateralAssignmentDAO collateralAssignmentDAO;
	private JointAccountDetailDAO jointAccountDetailDAO;
	private Map<String, List<DocumentDetails>> autoDownloadMap = null;

	/**
	 * default constructor.<br>
	 */
	public LiabilityRequestDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.doSetProperties();
		super.moduleCode = "LiabilityRequest";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_LiabilityFinanceMainDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_LiabilityFinanceMainDialog);

		if (arguments.containsKey("financeDetail")) {
			setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
			FinanceMain befImage = new FinanceMain();
			BeanUtils.copyProperties(getFinanceDetail().getFinScheduleData().getFinanceMain(), befImage);
			getFinanceDetail().getFinScheduleData().getFinanceMain().setBefImage(befImage);
			setFinanceDetail(getFinanceDetail());
			old_NextRoleCode = getFinanceDetail().getFinScheduleData().getFinanceMain().getNextRoleCode();
		}

		// LiabilityRequest object
		if (arguments.containsKey("liabilityRequest")) {
			setLiabilityRequest((LiabilityRequest) arguments.get("liabilityRequest"));
		}

		// LiabilityRequest List controller object
		if (arguments.containsKey("liabilityRequestListCtrl")) {
			setLiabilityRequestListCtrl((LiabilityRequestListCtrl) arguments.get("liabilityRequestListCtrl"));
		}

		// READ OVERHANDED params !
		// we get the financeMainListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete financeMain here.

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

		isEnquiry = true;
		if (!getLiabilityRequest().isNewRecord()) {
			doLoadWorkFlow(getLiabilityRequest());
		}

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (getLiabilityRequest().isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_New();
			btnCancel.setVisible(true);
		}

		setMainWindow(window_LiabilityFinanceMainDialog);

		this.basicDetailTabDiv.setHeight(this.borderLayoutHeight - 100 - 52 + "px");

		// set Field Properties
		if (!getLiabilityRequest().isNewRecord()) {
			super.doSetFieldProperties();
			this.tabBoxIndexCenter.setVisible(true);

		}

		this.btnValidate.setDisabled(true);
		this.btnBuildSchedule.setDisabled(true);
		this.btnValidate.setVisible(false);
		this.btnBuildSchedule.setVisible(false);
		this.btnCancel.setVisible(false);
		this.btnDelete.setVisible(false);
		this.btnFlagDetails.setVisible(false);
		this.subVentionFrom.setDisabled(true);
		if (getLiabilityRequest().isNewRecord()) {
			doShowLiabilityDialog();
		} else {
			doShowDialog(getFinanceDetail());
		}

		// Setting tile Name based on Service Action
		if (StringUtils.isNotEmpty(moduleDefiner)) {
			this.windowTitle.setValue(Labels.getLabel(moduleDefiner + "_Window.Title"));
		}

		this.basicDetailTabDiv.setHeight(this.borderLayoutHeight - 100 + "px");
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 */
	protected void doShowDialog(FinanceDetail afinanceDetail) {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		doReadOnly();
		this.flagDetails.setReadonly(true);
		if (getLiabilityRequest().isNewRecord()) {
			this.btnCtrl.setInitNew();
			this.finReference.setReadonly(false);
		} else {
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
			} else {
				this.btnCtrl.setInitNew();
				btnCancel.setVisible(false);
			}
		}

		// setFocus
		this.finReference.focus();

		try {
			doSetFieldProperties();
			doWriteBeanToComponents(afinanceDetail, true);
			onCheckODPenalty(false);

			FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
			if (financeType.getFinMinTerm() == 1 && financeType.getFinMaxTerm() == 1) {
				if (!financeType.isFinRepayPftOnFrq()) {
					getLabel_FinanceMainDialog_FinRepayPftOnFrq().setVisible(false);
					this.rpyPftFrqRow.setVisible(false);
					this.hbox_finRepayPftOnFrq.setVisible(false);
				} else {
					this.rpyPftFrqRow.setVisible(true);

					// As of Bank Request below two fields visibility overridden
					// from TRUE to FALSE by default
					getLabel_FinanceMainDialog_FinRepayPftOnFrq().setVisible(false);
					this.hbox_finRepayPftOnFrq.setVisible(false);
				}

				this.rpyFrqRow.setVisible(false);
				this.hbox_ScheduleMethod.setVisible(false);
				getLabel_FinanceMainDialog_ScheduleMethod().setVisible(false);
				this.noOfTermsRow.setVisible(false);
			} else {
				if (!financeType.isFinRepayPftOnFrq()) {
					getLabel_FinanceMainDialog_FinRepayPftOnFrq().setVisible(false);
					this.hbox_finRepayPftOnFrq.setVisible(false);
				}
			}

			if (!financeType.isFinRepayPftOnFrq() && !financeType.isFinIsIntCpz()) {
				getLabel_FinanceMainDialog_FinRepayPftOnFrq().setVisible(false);
				this.rpyPftFrqRow.setVisible(false);
				this.hbox_finRepayPftOnFrq.setVisible(false);
			}

			if (!isReadOnly("FinanceMainDialog_NoScheduleGeneration")) {
				this.gb_gracePeriodDetails.setVisible(false);
				// this.gb_repaymentDetails.setVisible(false);
				this.gb_OverDuePenalty.setVisible(false);
				if (this.numberOfTerms_two.intValue() == 0) {
					this.numberOfTerms_two.setValue(1);
				}
				this.row_stepFinance.setVisible(false);
				// this.row_manualSteps.setVisible(false);
			}

			setDialog(DialogType.EMBEDDED);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 */
	public void onClose$window_LiabilityFinanceMainDialog(Event event) {
		logger.debug("Entering " + event.toString());
		doClose();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnSave(Event event) {
		logger.debug("Entering " + event.toString());
		doSave();
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
		MessageUtil.showHelpWindow(event, window_LiabilityFinanceMainDialog);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	// CRUD operations

	/**
	 * Saves the components to table.
	 */
	public void doSave() {
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
					|| this.userAction.getSelectedItem().getLabel().contains("Resubmit")
					|| this.userAction.getSelectedItem().getLabel().contains("Decline")) {
				recSave = true;
				aFinanceDetail.setActionSave(true);
			}
			aFinanceDetail.setUserAction(this.userAction.getSelectedItem().getLabel());
		}

		aFinanceDetail.setAccountingEventCode(eventCode);
		aFinanceDetail.setModuleDefiner(StringUtils.isEmpty(moduleDefiner) ? FinServiceEvent.ORG : moduleDefiner);

		// Resetting Service Task ID's from Original State
		aFinanceMain.setRoleCode(this.curRoleCode);
		aFinanceMain.setNextRoleCode(this.curNextRoleCode);
		aFinanceMain.setTaskId(this.curTaskId);
		aFinanceMain.setNextTaskId(this.curNextTaskId);
		aFinanceMain.setNextUserId(this.curNextUserId);

		// force validation, if on, than execute by component.getValue()
		// fill the financeMain object with the components data
		this.doWriteComponentsToBean(aFinanceDetail.getFinScheduleData());
		isNew = getLiabilityRequest().isNewRecord();

		// Document Details Saving
		if (getDocumentDetailDialogCtrl() != null) {
			aFinanceDetail.setDocumentDetailsList(getDocumentDetailDialogCtrl().getDocumentDetailsList());
		} else {
			aFinanceDetail.setDocumentDetailsList(null);
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

		if (StringUtils.isBlank(this.custCIF.getValue())) {
			aFinanceDetail.setStageAccountingList(null);
		} else {

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
		}

		// Validation For Mandatory Recommendation
		if (!doValidateRecommendation()) {
			return;
		}

		aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		// Write the additional validations as per below example
		// get the selected branch object from the box
		// Do data level validations here

		String tranType = "";
		if (isWorkFlowEnabled()) {

			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isEmpty(getLiabilityRequest().getRecordType())) {
				aFinanceMain.setVersion(getLiabilityRequest().getVersion() + 1);
				if (isNew) {
					getLiabilityRequest().setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					getLiabilityRequest().setRecordType(PennantConstants.RECORD_TYPE_UPD);
					getLiabilityRequest().setNewRecord(true);
				}
			}

		} else {
			getLiabilityRequest().setVersion(aFinanceMain.getVersion() + 1);
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

				generateAgreement();

				// Mail Alert Notification for Customer/Dealer/Provider...etc
				if (!"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())) {

					FinanceMain financeMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
					Notification notification = new Notification();
					notification.getTemplates().add(NotificationConstants.TEMPLATE_FOR_AE);
					notification.getTemplates().add(NotificationConstants.TEMPLATE_FOR_CN);
					notification.setModule("LIABILTIY");
					notification.setSubModule(moduleDefiner);
					notification.setKeyReference(financeMain.getFinReference());
					notification.setStage(financeMain.getRoleCode());
					notification.setReceivedBy(getUserWorkspace().getUserId());
					notificationService.sendNotifications(notification, aFinanceDetail, financeMain.getFinType(),
							aFinanceDetail.getDocumentDetailsList());
				}

				// User Notifications Message/Alert
				if (!SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_DIVISION_BASED_CLUSTER)) {
					publishNotification(Notify.ROLE, getLiabilityRequest().getFinReference(), getLiabilityRequest());
				} else {
					publishNotification(Notify.ROLE, getLiabilityRequest().getFinReference(), getLiabilityRequest(),
							finDivision, aFinanceMain.getFinBranch());
				}

				// For Finance Maintenance
				if (getLiabilityRequestListCtrl() != null) {
					refreshMaintainList();
				}

				// Customer Notification for Role Identification
				if (StringUtils.isBlank(getLiabilityRequest().getNextTaskId())) {
					getLiabilityRequest().setNextRoleCode("");
				}

				String msg = PennantApplicationUtil.getSavingStatus(getLiabilityRequest().getRoleCode(),
						getLiabilityRequest().getNextRoleCode(), getLiabilityRequest().getFinReference(), " Loan ",
						getLiabilityRequest().getRecordStatus(), getNextUserId());
				Clients.showNotification(msg, "info", null, null, -1);

				closeDialog();
				if (listWindowTab != null) {
					listWindowTab.setSelected(true);
				}
			}

		} catch (DataAccessException | InterfaceException e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * Auto Generation of Loan Agreements while submitting
	 */
	private void generateAgreement() {
		if (autoDownloadMap == null || autoDownloadMap.isEmpty()) {
			return;
		}
		List<DocumentDetails> downLoaddocLst = autoDownloadMap.get("autoDownLoadDocs");

		if (CollectionUtils.isEmpty(downLoaddocLst)) {
			return;
		}

		for (DocumentDetails ldocDetails : downLoaddocLst) {
			String docName = ldocDetails.getDocName();
			byte[] docImage = ldocDetails.getDocImage();
			if (PennantConstants.DOC_TYPE_PDF.equals(ldocDetails.getDoctype())) {
				Filedownload.save(new AMedia(docName, "pdf", "application/pdf", docImage));
			} else {
				Filedownload.save(new AMedia(docName, "msword", "application/msword", docImage));
			}
		}

		autoDownloadMap = null;
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshMaintainList() {
		getLiabilityRequestListCtrl().search();
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
		int format = CurrencyUtil.getFormat(afinanceMain.getFinCcy());

		afinanceMain.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		afinanceMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		afinanceMain.setUserDetails(getUserWorkspace().getLoggedInUser());
		afinanceMain.setRoleCode(getRole());

		aFinanceDetail.getFinScheduleData().setFinanceMain(afinanceMain);
		aFinanceDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		LiabilityRequest liabilityRequest = getLiabilityRequest();
		liabilityRequest.setCustCIF(aFinanceDetail.getCustomerDetails().getCustomer().getCustCIF());
		liabilityRequest.setFinID(this.finId.getValue());
		liabilityRequest.setFinReference(this.finReference.getValue());

		if (StringUtils.equals(moduleDefiner, FinServiceEvent.INSCLAIM)) {
			ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

			try {
				if (!recSave && !this.insClaimReason.isDisabled()
						&& getComboboxValue(this.insClaimReason).equals(PennantConstants.List_Select)) {
					throw new WrongValueException(this.insClaimReason, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_LiabilityFinanceMainDialog_TakafulReason.value") }));
				}
				liabilityRequest.setInsClaimReason(getComboboxValue(this.insClaimReason));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (!recSave && !this.insClaimAmount.isReadonly()
						&& insClaimAmount.getActualValue().compareTo(BigDecimal.ZERO) == 0) {
					throw new WrongValueException(this.insClaimAmount, Labels.getLabel("NUMBER_MINVALUE",
							new String[] { Labels.getLabel("label_LiabilityFinanceMainDialog_TakafulClaimAmount.value"),
									String.valueOf(BigDecimal.ZERO) }));
				}
				liabilityRequest.setInsClaimAmount(
						PennantApplicationUtil.unFormateAmount(this.insClaimAmount.getActualValue(), format));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (!recSave && !this.insPaidStatus.isDisabled()
						&& getComboboxValue(this.insPaidStatus).equals(PennantConstants.List_Select)) {
					throw new WrongValueException(this.insPaidStatus, Labels.getLabel("STATIC_INVALID", new String[] {
							Labels.getLabel("label_LiabilityFinanceMainDialog_TakafulPaidStatus.value") }));
				}
				liabilityRequest.setInsPaidStatus(getComboboxValue(this.insPaidStatus));
			} catch (WrongValueException we) {
				wve.add(we);
			}

			if (wve.size() > 0) {
				logger.debug("Throwing occured Errors By using WrongValueException");
				financeTypeDetailsTab.setSelected(true);
				WrongValueException[] wvea = new WrongValueException[wve.size()];
				for (int i = 0; i < wve.size(); i++) {
					wvea[i] = wve.get(i);
					if (i == 0) {
						Component comp = wvea[i].getComponent();
						if (comp instanceof HtmlBasedComponent) {
							((HtmlBasedComponent) comp).focus();
						}
					}
					logger.debug(wvea[i]);
				}
				throw new WrongValuesException(wvea);
			}
		}

		if (liabilityRequest.isNewRecord()) {
			liabilityRequest.setFinEvent(moduleDefiner);
			liabilityRequest.setInitiatedBy(afinanceMain.getInitiateUser());
		}
		liabilityRequest.setVersion(afinanceMain.getVersion());
		liabilityRequest.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		liabilityRequest.setLastMntOn(new Timestamp(System.currentTimeMillis()));

		liabilityRequest.setFinanceDetail(aFinanceDetail);
		liabilityRequest.setUserDetails(aFinanceDetail.getUserDetails());

		// Auto Generation of Agreements while submitting
		generateAggrement(aFinanceDetail);

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			liabilityRequest.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			// Check for service tasks. If one exists perform the task(s)
			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, liabilityRequest, finishedTasks);

			if (isNotesMandatory(taskId, liabilityRequest)) {
				if (!notesEntered) {
					MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
					return false;
				}
			}

			auditHeader = getAuditHeader(liabilityRequest, PennantConstants.TRAN_WF);

			while (!"".equals(serviceTasks)) {

				String method = serviceTasks.split(";")[0];

				if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckCollaterals)) {
					processCompleted = true;
				} else if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckFurtherWF)) {

					// Fetch Details of Workflow process followed by existing
					// Flow
					String nextFinEvent = getLiabilityRequestService().getProceedingWorkflow(this.finType.getValue(),
							this.moduleDefiner);
					if (StringUtils.isNotBlank(nextFinEvent)) {
						MessageUtil.showMessage(Labels.getLabel("menu_Item_" + nextFinEvent));
					}

				} else if (StringUtils.trimToEmpty(method).contains(FinanceConstants.method_scheduleChange)) {
					List<String> finTypeList = getFinanceDetailService().getScheduleEffectModuleList(true);
					boolean isScheduleModify = false;
					for (String fintypeList : finTypeList) {
						if (StringUtils.equals(moduleDefiner, fintypeList)) {
							isScheduleModify = true;
							break;
						}
					}
					if (isScheduleModify) {
						afinanceMain.setScheduleChange(true);
					} else {
						afinanceMain.setScheduleChange(false);
					}
				} else {
					LiabilityRequest tLiabilityRequest = (LiabilityRequest) auditHeader.getAuditDetail().getModelData();
					setNextTaskDetails(taskId,
							tLiabilityRequest.getFinanceDetail().getFinScheduleData().getFinanceMain());
					auditHeader.getAuditDetail().setModelData(tLiabilityRequest);
					processCompleted = doSaveProcess(auditHeader, method);

				}

				if (!processCompleted) {
					break;
				}

				finishedTasks += (method + ";");
				LiabilityRequest tLiabilityRequest = (LiabilityRequest) auditHeader.getAuditDetail().getModelData();
				serviceTasks = getServiceTasks(taskId, tLiabilityRequest, finishedTasks);

			}

			LiabilityRequest tLiabilityRequest = (LiabilityRequest) auditHeader.getAuditDetail().getModelData();
			// Check whether to proceed further or not
			String nextTask = getNextTaskIds(taskId, tLiabilityRequest);

			if (processCompleted && nextTask.equals(taskId + ";")) {
				processCompleted = false;
			}

			// Proceed further to save the details in WorkFlow
			if (processCompleted) {

				if (!"".equals(nextTask) || "Save".equals(userAction.getSelectedItem().getLabel())) {
					setNextTaskDetails(taskId, tLiabilityRequest);
					// doProcess_Assets(tFinanceDetail);
					auditHeader.getAuditDetail().setModelData(tLiabilityRequest);
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

	private void generateAggrement(FinanceDetail aFinanceDetail) {
		if (recSave) {
			return;
		}

		List<DocumentDetails> agenDocList = new ArrayList<DocumentDetails>();

		autoDownloadMap = new HashMap<>();
		AgreementDefinition agreementDefinition = null;
		List<DocumentDetails> autoDownloadLst = new ArrayList<DocumentDetails>();
		String templateValidateMsg = "";
		String accMsg = "";
		boolean isTemplateError = false;
		Set<String> allagrDataset = new HashSet<>();
		Map<String, AgreementDefinition> agrdefMap = new HashMap<>();
		Map<String, FinanceReferenceDetail> finRefMap = new HashMap<>();
		List<DocumentDetails> documents = aFinanceDetail.getDocumentDetailsList();
		List<DocumentDetails> existingUploadDocList = documents;
		for (FinanceReferenceDetail financeReferenceDetail : aFinanceDetail.getAggrementList()) {
			long id = financeReferenceDetail.getFinRefId();
			agreementDefinition = getAgreementDefinitionService().getAgreementDefinitionById(id);
			// For Agreement Rules
			boolean isAgrRender = true;
			// Check Each Agreement is attached with Rule or Not, If Rule
			// Exists based on Rule Result Agreement will display
			if (StringUtils.isNotBlank(financeReferenceDetail.getLovDescAggRuleName())) {
				Rule rule = getRuleService().getApprovedRuleById(financeReferenceDetail.getLovDescAggRuleName(),
						RuleConstants.MODULE_AGRRULE, RuleConstants.EVENT_AGRRULE);
				if (rule != null) {
					Map<String, Object> fieldsAndValues = getFinanceDetail().getCustomerEligibilityCheck()
							.getDeclaredFieldValues();
					isAgrRender = (boolean) RuleExecutionUtil.executeRule(rule.getSQLRule(), fieldsAndValues,
							getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy(),
							RuleReturnType.BOOLEAN);
				}
			}

			if (isAgrRender) {
				if (agreementDefinition.isAutoGeneration()) {
					try {
						templateValidateMsg = validateTemplate(financeReferenceDetail); // If

						if ("Y".equals(templateValidateMsg)) {
							if (!isTemplateError) {

								allagrDataset.add(agreementDefinition.getAggImage());
								agrdefMap.put(agreementDefinition.getAggReportName(), agreementDefinition);
								finRefMap.put(agreementDefinition.getAggReportName(), financeReferenceDetail);

							}
						} else {

							accMsg = accMsg + "  " + templateValidateMsg;
							isTemplateError = true;
							continue;
						}

					} catch (Exception e) {
						MessageUtil.showError(e.getMessage());
					}
				}
			}
		} // for close
		if (isTemplateError) {
			MessageUtil.showError(accMsg + " Templates Does not Exists Please configure.");
			return;
		}

		if (agrdefMap.isEmpty()) {
			return;
		}

		DocumentDetails documentDetails = null;
		AgreementDetail agrData = getAgreementGeneration().getAggrementData(aFinanceDetail, allagrDataset.toString(),
				getUserWorkspace().getUserDetails());
		for (String tempName : agrdefMap.keySet()) {
			AgreementDefinition aggdef = agrdefMap.get(tempName);
			documentDetails = autoGenerateAgreement(finRefMap.get(tempName), aFinanceDetail, aggdef,
					existingUploadDocList, agrData);
			agenDocList.add(documentDetails);
			if (aggdef.isAutoDownload()) {
				autoDownloadLst.add(documentDetails);
			}
		}
		if (documents == null) {
			aFinanceDetail.setDocumentDetailsList(new ArrayList<DocumentDetails>());
		}

		// aFinanceDetail.getDocumentDetailsList().addAll(agenDocList);

		for (int i = 0; i < agenDocList.size(); i++) {
			boolean rcdFound = false;
			for (int j = 0; j < documents.size(); j++) {
				if (!StringUtils.equals(documents.get(j).getDocCategory(), agenDocList.get(i).getDocCategory())) {
					continue;
				}
				rcdFound = true;
				documents.get(j).setDocImage(agenDocList.get(i).getDocImage());
				documents.get(j).setDocRefId(agenDocList.get(i).getDocRefId());
				break;
			}

			if (!rcdFound) {
				documents.add(agenDocList.get(i));
			}
		}
		autoDownloadMap.put("autoDownLoadDocs", autoDownloadLst);
		agrdefMap = null;
		finRefMap = null;
		allagrDataset = null;

	}

	private String getServiceTasks(String taskId, LiabilityRequest liabilityRequest, String finishedTasks) {
		logger.debug("Entering");
		// changes regarding parallel work flow
		String nextRoleCode = StringUtils.trimToEmpty(liabilityRequest.getNextRoleCode());
		String nextRoleCodes[] = nextRoleCode.split(",");

		if (nextRoleCodes.length > 1) {
			return "";
		}

		String serviceTasks = getServiceOperations(taskId, liabilityRequest);
		if (!"".equals(finishedTasks)) {
			String[] list = finishedTasks.split(";");

			for (int i = 0; i < list.length; i++) {
				serviceTasks = serviceTasks.replace(list[i] + ";", "");
			}
		}
		logger.debug("Leaving");
		return serviceTasks;
	}

	protected void setNextTaskDetails(String taskId, LiabilityRequest liabilityRequest) {
		logger.debug("Entering");

		// Set the next task id
		String nextTaskId = "";

		if ("Save".equals(userAction.getSelectedItem().getLabel())) {
			nextTaskId = taskId + ";";
		} else {
			nextTaskId = StringUtils.trimToEmpty(liabilityRequest.getNextTaskId());

			nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
			if ("".equals(nextTaskId)) {
				nextTaskId = getNextTaskIds(taskId, liabilityRequest);
			}
		}

		// Set the role codes for the next tasks
		String nextRoleCode = "";

		if (StringUtils.isNotBlank(nextTaskId)) {
			String[] nextTasks = nextTaskId.split(";");

			if (nextTasks != null && nextTasks.length > 0) {
				for (int i = 0; i < nextTasks.length; i++) {

					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode.concat(",");
					}
					nextRoleCode = getTaskOwner(nextTasks[i]);
				}
			} else {
				nextRoleCode = getTaskOwner(nextTaskId);
			}
		}

		liabilityRequest.setTaskId(taskId);
		liabilityRequest.setNextTaskId(nextTaskId);
		liabilityRequest.setRoleCode(getRole());
		liabilityRequest.setNextRoleCode(nextRoleCode);

		getLiabilityRequest().setTaskId(taskId);
		getLiabilityRequest().setNextTaskId(nextTaskId);
		getLiabilityRequest().setRoleCode(getRole());
		getLiabilityRequest().setNextRoleCode(nextRoleCode);

		logger.debug("Leaving");
	}

	/**
	 * Get Audit Header Details
	 */
	private AuditHeader getAuditHeader(LiabilityRequest liabilityRequest, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, liabilityRequest.getBefImage(), liabilityRequest);
		return new AuditHeader(liabilityRequest.getFinReference(), null, null, null, auditDetail,
				liabilityRequest.getUserDetails(), getOverideMap());
	}

	/**
	 * Method for Saving Details Record
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		LiabilityRequest aLiabilityRequest = (LiabilityRequest) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getLiabilityRequestService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getLiabilityRequestService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getLiabilityRequestService().doApprove(auditHeader);

						if (aLiabilityRequest.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getLiabilityRequestService().doReject(auditHeader);
						if (aLiabilityRequest.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(getMainWindow(), auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(getMainWindow(), auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(getFinanceDetail().getFinScheduleData().getFinanceMain()), true);
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
			LiabilityRequest liabilityRequest = (LiabilityRequest) auditHeader.getAuditDetail().getModelData();
			setNextUserId(liabilityRequest.getFinanceDetail().getFinScheduleData().getFinanceMain().getNextUserId());

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

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain financeMain
	 * @throws ParseException
	 * @throws InterruptedException
	 * @throws InterfaceException
	 */
	@Override
	public void doWriteBeanToComponents(FinanceDetail aFinanceDetail, boolean onLoadProcess)
			throws ParseException, InterruptedException, InterfaceException {
		logger.debug("Entering");

		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		int format = CurrencyUtil.getFormat(aFinanceMain.getFinCcy());
		FinanceType financeType = aFinanceDetail.getFinScheduleData().getFinanceType();

		this.reqLoanAmt.setValue(PennantApplicationUtil.formateAmount(aFinanceMain.getReqLoanAmt(), format));
		this.appliedLoanAmt.setValue(PennantApplicationUtil.formateAmount(aFinanceMain.getAppliedLoanAmt(), format));
		this.reqLoanTenor.setValue(aFinanceMain.getReqLoanTenor());
		fillList(this.advType, AdvanceType.getRepayList(), aFinanceMain.getAdvType());
		this.advTerms.setValue(aFinanceMain.getAdvTerms());
		this.finOCRRequired.setChecked(aFinanceMain.isFinOcrRequired());
		this.liabilityRef.setValue(aFinanceMain.getFinReference());
		this.liabilityRef.setObject(aFinanceMain);
		this.tDSStartDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.tDSEndDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.tDSPercentage.setFormat(PennantApplicationUtil.getAmountFormate(2));
		this.tDSLimitAmt.setValue(PennantApplicationUtil.formateAmount(aFinanceMain.getTdsLimitAmt(), format));
		if (!getLiabilityRequest().isNewRecord()) {
			this.liabilityRef.setReadonly(true);
		}
		// Showing Product Details for Promotion Type
		this.finDivisionName.setValue(financeType.getFinDivision() + " - " + financeType.getLovDescFinDivisionName());
		if (StringUtils.isNotEmpty(financeType.getProduct())) {
			this.hbox_PromotionProduct.setVisible(true);
			this.getLabel_FinanceMainDialog_PromoProduct().setVisible(true);
			this.promotionProduct.setValue(financeType.getProduct() + " - " + financeType.getLovDescPromoFinTypeDesc());
			getLabel_FinanceMainDialog_FinType()
					.setValue(Labels.getLabel("label_FinanceMainDialog_PromotionCode.value"));
		}

		// Finance MainDetails Tab ---> 1. Basic Details
		this.vanCode.setValue(aFinanceMain.getVanCode());
		this.finType.setValue(aFinanceMain.getFinType());
		this.finCcy.setValue(aFinanceMain.getFinCcy());
		fillComboBox(this.cbProfitDaysBasis, aFinanceMain.getProfitDaysBasis(),
				PennantStaticListUtil.getProfitDaysBasis(), "");
		fillComboBox(this.finRepayMethod, aFinanceMain.getFinRepayMethod(), MandateUtil.getRepayMethods(), "");
		fillComboBox(this.advStage, aFinanceMain.getAdvStage(), MandateUtil.getRepayMethods(), "");
		this.offerId.setValue(aFinanceMain.getOfferId());
		this.finIsRateRvwAtGrcEnd.setChecked(aFinanceMain.isFinIsRateRvwAtGrcEnd());
		this.allowDrawingPower.setChecked(aFinanceMain.isAllowDrawingPower());
		this.allowRevolving.setChecked(aFinanceMain.isAllowRevolving());
		this.finBranch.setValue(aFinanceMain.getFinBranch());
		this.custCIF.setValue(aFinanceMain.getLovDescCustCIF());
		this.custShrtName.setValue(aFinanceMain.getLovDescCustShrtName());
		this.custID.setValue(aFinanceMain.getCustID());
		this.finPurpose.setValue(aFinanceMain.getFinPurpose());
		this.accountsOfficer.setValue(String.valueOf(aFinanceMain.getAccountsOfficer()));
		this.accountsOfficer.setDescription(aFinanceMain.getLovDescAccountsOfficer());

		this.dsaCode.setValue(aFinanceMain.getDsaName());
		this.dsaCode.setDescription(aFinanceMain.getDsaCodeDesc());

		if (aFinanceMain.getOfferId() != null) {
			this.offerId.setValue(aFinanceMain.getOfferId());
		}
		fillComboBox(this.sourChannelCategory, aFinanceMain.getSourChannelCategory(),
				PennantStaticListUtil.getSourcingChannelCategory(), "");

		this.sourcingBranch.setValue(aFinanceMain.getSourcingBranch());
		this.sourcingBranch.setDescription(aFinanceMain.getLovDescSourcingBranch());
		if (aFinanceMain.getAsmName() != null) {
			this.asmName.setValue(String.valueOf(aFinanceMain.getAsmName()));
		}
		this.connector.setValue(String.valueOf(aFinanceMain.getConnector()));

		if (aFinanceMain.getReferralId() != null) {
			this.referralId.setValue(aFinanceMain.getReferralId());
			this.referralId.setDescription(aFinanceMain.getReferralIdDesc());
		}
		fillComboBox(this.subVentionFrom, aFinanceMain.getSubVentionFrom(), PennantStaticListUtil.getSubVentionFrom(),
				"");

		if (aFinanceMain.getDmaCode() != null) {
			this.dmaCode.setValue(aFinanceMain.getDmaCode());
			this.dmaCode.setDescription(aFinanceMain.getDmaName());
		}

		if (aFinanceMain.getSalesDepartment() != null) {
			this.salesDepartment.setValue(aFinanceMain.getSalesDepartment());
			this.salesDepartment.setDescription(aFinanceMain.getSalesDepartmentDesc());
		}

		// Start : Offer Details
		if (StringUtils.isNotBlank(aFinanceMain.getOfferProduct())) {
			this.gb_offerDetails.setVisible(true);
			this.offerProduct.setValue(aFinanceMain.getOfferProduct());
			this.offerAmount.setValue((BigDecimal) aFinanceMain.getOfferAmount());
			this.custSegmentation.setValue(StringUtils.trimToEmpty(aFinanceMain.getCustSegmentation()));
			this.baseProduct.setValue(aFinanceMain.getBaseProduct());
			this.processType.setValue(aFinanceMain.getProcessType());
			this.bureauTimeSeries.setValue(aFinanceMain.getBureauTimeSeries());
			this.campaignName.setValue(aFinanceMain.getCampaignName());
			this.existingLanRefNo.setValue(aFinanceMain.getExistingLanRefNo());
			this.verification.setValue(aFinanceMain.getVerification());
			this.leadSource.setValue(aFinanceMain.getLeadSource());
			this.poSource.setValue(aFinanceMain.getPoSource());
		} else {
			this.gb_offerDetails.setVisible(false);
		}
		// End : Offer Details

		fillList(this.grcAdvType, AdvanceType.getGrcList(), aFinanceMain.getGrcAdvType());
		this.grcAdvTerms.setValue(aFinanceMain.getGrcAdvTerms());

		String repayMethod = StringUtils.trimToEmpty(aFinanceMain.getFinRepayMethod());

		fillComboBox(this.finRepayMethod, repayMethod, MandateUtil.getRepayMethods(), "");

		this.commitmentRef.setValue(aFinanceMain.getFinCommitmentRef(),
				StringUtils.trimToEmpty(aFinanceMain.getFinCommitmentRef()));
		this.finLimitRef.setValue(aFinanceMain.getFinLimitRef(),
				StringUtils.trimToEmpty(aFinanceMain.getFinLimitRef()));

		if (!TDSCalculator.isTDSApplicable(aFinanceMain)) {
			this.tDSApplicable.setVisible(false);
			this.label_FinanceMainDialog_TDSApplicable.setVisible(false);
			this.label_FinanceMainDialog_TDSType.setVisible(false);
			this.cbTdsType.setVisible(false);
			this.cbTdsType.setDisabled(true);
		}

		if (!financeType.isFinCommitmentReq()) {
			getLabel_FinanceMainDialog_CommitRef().setVisible(false);
			this.commitmentRef.setVisible(false);
			this.commitmentRef.setReadonly(true);
		}

		// if Insurance Claim then claim amount, paidStatus, claim reason fields should be visible
		if (StringUtils.equals(moduleDefiner, FinServiceEvent.INSCLAIM)) {
			this.row_ClaimReason.setVisible(true);
			this.row_InsPaidStatus.setVisible(true);
			fillComboBox(this.insClaimReason, getLiabilityRequest().getInsClaimReason(),
					PennantStaticListUtil.getInsClaimReasonList(), "");
			fillComboBox(this.insPaidStatus, getLiabilityRequest().getInsPaidStatus(),
					PennantStaticListUtil.getInsPaidStatusList(), "");
			this.insClaimAmount
					.setValue(PennantApplicationUtil.formateAmount(getLiabilityRequest().getInsClaimAmount(), format));
			doEdit();
		}

		this.finIsActive.setChecked(aFinanceMain.isFinIsActive());
		this.lovDescFinTypeName.setValue(aFinanceMain.getFinType() + "-" + aFinanceMain.getLovDescFinTypeName());
		this.finCcy.setValue(aFinanceMain.getFinCcy(), CurrencyUtil.getCcyDesc(aFinanceMain.getFinCcy()));
		if (StringUtils.isNotBlank(aFinanceMain.getFinBranch())) {
			this.finBranch.setDescription(aFinanceMain.getLovDescFinBranchName());
		}

		if (aFinanceMain.getFinStartDate() != null) {
			this.finStartDate.setValue(aFinanceMain.getFinStartDate());
		}

		if (aFinanceMain.getFinContractDate() != null) {
			this.finContractDate.setValue(aFinanceMain.getFinContractDate());
		} else {
			this.finContractDate.setValue(aFinanceMain.getFinStartDate());
		}

		setDownpaymentRulePercentage(true);

		if (financeType.isFinIsDwPayRequired() && aFinanceMain.getMinDownPayPerc().compareTo(BigDecimal.ZERO) >= 0) {

			this.row_downPayBank.setVisible(true);
			this.row_downPayPercentage.setVisible(true);
			this.downPayBank.setValue(PennantApplicationUtil.formateAmount(aFinanceMain.getDownPayBank(), format));
			this.downPaySupl.setValue(PennantApplicationUtil.formateAmount(aFinanceMain.getDownPaySupl(), format));

			if (this.downPayBank.isReadonly() && aFinanceMain.getDownPayBank().compareTo(BigDecimal.ZERO) == 0) {
				this.row_downPayBank.setVisible(false);
			}

			if (this.downPayBank.isReadonly() && this.downPaySupl.isReadonly()
					&& aFinanceMain.getDownPayment().compareTo(BigDecimal.ZERO) == 0) {
				this.row_downPayPercentage.setVisible(false);
			}
		}

		if (aFinanceMain.getMinDownPayPerc().compareTo(BigDecimal.ZERO) == 0) {
			this.downPayBank.setMandatory(false);
			this.downPaySupl.setMandatory(false);
		}

		setDownPayPercentage();
		setNetFinanceAmount(true);

		this.finPurpose.setValue(aFinanceMain.getFinPurpose());
		if (StringUtils.isNotBlank(aFinanceMain.getFinPurpose())) {
			this.finPurpose.setValue(StringUtils.trimToEmpty(aFinanceMain.getFinPurpose()),
					StringUtils.trimToEmpty(aFinanceMain.getLovDescFinPurposeName()));
		}

		// Step Finance
		if ((aFinanceMain.isNewRecord() || !aFinanceMain.isStepFinance()) && !financeType.isStepFinance()) {
			this.row_stepFinance.setVisible(false);
		}
		this.stepFinance.setChecked(aFinanceMain.isStepFinance());
		if (aFinanceMain.isNewRecord()) {
			if (aFinanceMain.isAlwManualSteps()
					&& getFinanceDetail().getFinScheduleData().getStepPolicyDetails() == null) {
				getFinanceDetail().getFinScheduleData().resetStepPolicyDetails(null);
			}
		}

		// Finance MainDetails Tab ---> 2. Grace Period Details
		if (financeType.isFInIsAlwGrace()) {

			if (aFinanceMain.getGrcPeriodEndDate() == null) {
				aFinanceMain.setGrcPeriodEndDate(aFinanceMain.getFinStartDate());
			}

			this.allowGrace.setChecked(aFinanceMain.isAllowGrcPeriod());
			this.gb_gracePeriodDetails.setVisible(true);
			this.gracePeriodEndDate.setText("");
			this.gracePeriodEndDate_two.setValue(aFinanceMain.getGrcPeriodEndDate());
			fillComboBox(this.grcRateBasis, aFinanceMain.getGrcRateBasis(),
					PennantStaticListUtil.getInterestRateType(!aFinanceMain.isMigratedFinance()), ",C,D,");

			fillComboBox(this.cbGrcSchdMthd, aFinanceMain.getGrcSchdMthd(), PennantStaticListUtil.getScheduleMethods(),
					",EQUAL,PRI_PFT,PRI,");
			if (aFinanceMain.isAllowGrcRepay()) {
				this.graceTerms.setVisible(true);
				this.grcRepayRow.setVisible(true);
				this.allowGrcRepay.setChecked(aFinanceMain.isAllowGrcRepay());
			}

			this.graceTerms.setText("");
			this.graceRate.setMarginValue(aFinanceMain.getGrcMargin());
			fillComboBox(this.grcPftDaysBasis, aFinanceMain.getGrcProfitDaysBasis(),
					PennantStaticListUtil.getProfitDaysBasis(), "");
			if (StringUtils.isNotEmpty(aFinanceMain.getGraceBaseRate()) && StringUtils.equals(
					CalculationConstants.RATE_BASIS_R, this.grcRateBasis.getSelectedItem().getValue().toString())) {
				this.grcBaseRateRow.setVisible(true);
				this.graceRate.setVisible(true);
				this.graceRate.setBaseValue(aFinanceMain.getGraceBaseRate());
				this.graceRate.setSpecialValue(aFinanceMain.getGraceSpecialRate());
				if ((financeType.getFInGrcMinRate() == null
						|| BigDecimal.ZERO.compareTo(financeType.getFInGrcMinRate()) == 0)
						&& (financeType.getFinGrcMaxRate() == null
								|| BigDecimal.ZERO.compareTo(financeType.getFinGrcMaxRate()) == 0)) {
					this.row_FinGrcRates.setVisible(false);
					this.finGrcMinRate.setValue(BigDecimal.ZERO);
					this.finGrcMaxRate.setValue(BigDecimal.ZERO);
				} else {
					this.row_FinGrcRates.setVisible(true);
					if (aFinanceMain.isNewRecord()) {
						this.finGrcMinRate.setValue(financeType.getFInGrcMinRate());
						this.finGrcMaxRate.setValue(financeType.getFinGrcMaxRate());
					} else {
						this.finGrcMinRate.setValue(aFinanceMain.getGrcMinRate());
						this.finGrcMaxRate.setValue(aFinanceMain.getGrcMaxRate());
					}
				}

				RateDetail rateDetail = RateUtil.rates(aFinanceMain.getGraceBaseRate(), aFinanceMain.getFinCcy(),
						aFinanceMain.getGraceSpecialRate(), aFinanceMain.getGrcMargin(), aFinanceMain.getGrcMinRate(),
						aFinanceMain.getGrcMaxRate());

				if (rateDetail.getErrorDetails() == null) {
					this.graceRate.setEffRateText(
							PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
				}
				readOnlyComponent(true, this.gracePftRate);

			} else {
				this.row_FinGrcRates.setVisible(false);
				this.grcBaseRateRow.setVisible(false);
				this.graceRate.setVisible(false);
				this.graceRate.setBaseValue("");
				this.graceRate.setBaseDescription("");
				this.graceRate.setBaseReadonly(true);
				this.graceRate.setSpecialValue("");
				this.graceRate.setSpecialDescription("");
				this.graceRate.setSpecialReadonly(true);
				readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);
				this.gracePftRate.setValue(aFinanceMain.getGrcPftRate());
				this.graceRate.setEffRateValue(aFinanceMain.getGrcPftRate());
				this.graceRate.setEffRateText(
						PennantApplicationUtil.formatRate(aFinanceMain.getGrcPftRate().doubleValue(), 2));
				this.finGrcMinRate.setValue(BigDecimal.ZERO);
				this.finGrcMaxRate.setValue(BigDecimal.ZERO);
			}

			this.grcPftFrqRow.setVisible(true);
			this.gracePftFrq.setDisabled(isReadOnly("FinanceMainDialog_gracePftFrq"));
			this.gracePftFrq.setValue(aFinanceMain.getGrcPftFrq());
			readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftDate"), this.nextGrcPftDate);
			if (aFinanceMain.isAllowGrcPftRvw()) {
				this.gracePftRvwFrq.setDisabled(isReadOnly("FinanceMainDialog_gracePftRvwFrq"));
				if (StringUtils.isNotBlank(aFinanceMain.getGrcPftRvwFrq())
						&& !StringUtils.equals(aFinanceMain.getGrcPftRvwFrq(), PennantConstants.List_Select)) {
					this.grcPftRvwFrqRow.setVisible(true);
					this.gracePftRvwFrq.setValue(aFinanceMain.getGrcPftRvwFrq());
				}
				readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftRvwDate"), this.nextGrcPftRvwDate);
			} else {
				this.gracePftRvwFrq.setDisabled(true);
				this.nextGrcPftRvwDate.setValue(SysParamUtil.getValueAsDate("APP_DFT_ENDDATE"));
				readOnlyComponent(true, this.nextGrcPftRvwDate);

			}
			if (aFinanceMain.isAllowGrcCpz()) {
				this.graceCpzFrq.setDisabled(isReadOnly("FinanceMainDialog_graceCpzFrq"));
				if (StringUtils.isNotBlank(aFinanceMain.getGrcCpzFrq())
						|| !StringUtils.trimToEmpty(aFinanceMain.getGrcCpzFrq()).equals(PennantConstants.List_Select)) {
					this.grcCpzFrqRow.setVisible(true);
					this.graceCpzFrq.setValue(aFinanceMain.getGrcCpzFrq());
				}
				readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcCpzDate"), this.nextGrcCpzDate);
			} else {
				this.graceCpzFrq.setDisabled(true);
				this.nextGrcCpzDate.setValue(SysParamUtil.getValueAsDate("APP_DFT_ENDDATE"));
				readOnlyComponent(true, this.nextGrcCpzDate);
			}

			if (!this.allowGrace.isChecked()) {
				doAllowGraceperiod(false);
			}

		} else {
			this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
			this.gb_gracePeriodDetails.setVisible(false);
			this.allowGrace.setVisible(false);
			getLabel_FinanceMainDialog_AlwGrace().setVisible(false);
		}
		if (this.allowGrace.isDisabled() && !this.allowGrace.isChecked()) {
			getLabel_FinanceMainDialog_AlwGrace().setVisible(false);
			this.allowGrace.setVisible(false);
		}

		// Show default date values beside the date components
		this.graceTerms_Two.setValue(0);
		if (aFinanceMain.isAllowGrcPeriod()) {
			this.graceTerms_Two.setValue(aFinanceMain.getGraceTerms());
			this.nextGrcPftDate_two.setValue(aFinanceMain.getNextGrcPftDate());
			this.nextGrcPftRvwDate_two.setValue(aFinanceMain.getNextGrcPftRvwDate());
			this.nextGrcCpzDate_two.setValue(aFinanceMain.getNextGrcCpzDate());
		}

		// Finance MainDetails Tab ---> 3. Repayment Period Details

		fillComboBox(this.repayRateBasis, aFinanceMain.getRepayRateBasis(),
				PennantStaticListUtil.getInterestRateType(!aFinanceMain.isMigratedFinance()), "");
		this.finRepaymentAmount
				.setValue(PennantApplicationUtil.formateAmount(aFinanceMain.getReqRepayAmount(), format));
		this.finAmount.setValue(PennantApplicationUtil.formateAmount(aFinanceMain.getFinAmount(), format));

		if ("PFT".equals(aFinanceMain.getScheduleMethod())) {
			this.finRepaymentAmount.setReadonly(true);
		}
		this.numberOfTerms_two.setValue(aFinanceMain.getNumberOfTerms());
		this.numberOfTerms.setText("");

		if (this.numberOfTerms_two.intValue() == 1) {
			this.repayFrq.setMandatoryStyle(false);
		} else {
			this.repayFrq.setMandatoryStyle(true);
		}

		this.finRepayPftOnFrq.setChecked(aFinanceMain.isFinRepayPftOnFrq());
		if (!financeType.isFinRepayPftOnFrq()) {
			this.finRepayPftOnFrq.setDisabled(true);
		}
		this.maturityDate_two.setValue(aFinanceMain.getMaturityDate());
		this.repayRate.setMarginValue(aFinanceMain.getRepayMargin());
		fillComboBox(this.cbScheduleMethod, aFinanceMain.getScheduleMethod(),
				PennantStaticListUtil.getScheduleMethods(), ",NO_PAY,GRCNDPAY,PFTCAP,");
		if (StringUtils.isNotEmpty(aFinanceMain.getRepayBaseRate()) && StringUtils.equals(
				CalculationConstants.RATE_BASIS_R, this.repayRateBasis.getSelectedItem().getValue().toString())) {
			this.repayBaseRateRow.setVisible(true);
			this.repayRate.setBaseValue(aFinanceMain.getRepayBaseRate());
			this.repayRate.setSpecialValue(aFinanceMain.getRepaySpecialRate());

			RateDetail rateDetail = RateUtil.rates(this.repayRate.getBaseValue(), this.finCcy.getValue(),
					this.repayRate.getSpecialValue(), this.repayRate.getMarginValue(), this.finMinRate.getValue(),
					this.finMaxRate.getValue());

			if (rateDetail.getErrorDetails() == null) {
				this.repayRate.setEffRateText(
						PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
			}
			readOnlyComponent(true, this.repayProfitRate);

			if (financeType.getFInMinRate().compareTo(BigDecimal.ZERO) == 0
					&& financeType.getFinMaxRate().compareTo(BigDecimal.ZERO) == 0) {
				this.row_FinRepRates.setVisible(false);
			} else {
				this.row_FinRepRates.setVisible(true);
				readOnlyComponent(true, this.finMinRate);
				readOnlyComponent(true, this.finMaxRate);
				if (aFinanceMain.isNewRecord()) {
					this.finMinRate.setValue(financeType.getFInMinRate());
					this.finMaxRate.setValue(financeType.getFinMaxRate());
				} else {
					this.finMinRate.setValue(aFinanceMain.getRpyMinRate());
					this.finMaxRate.setValue(aFinanceMain.getRpyMaxRate());
				}
			}

		} else {
			this.row_FinRepRates.setVisible(false);
			this.repayBaseRateRow.setVisible(false);
			this.repayRate.setMarginReadonly(true);
			this.repayRate.setBaseValue("");
			this.repayRate.setBaseDescription("");
			this.repayRate.setBaseReadonly(true);
			this.repayRate.setSpecialValue("");
			this.repayRate.setSpecialDescription("");
			this.repayRate.setSpecialReadonly(true);
			readOnlyComponent(isReadOnly("FinanceMainDialog_profitRate"), this.repayProfitRate);
			this.repayProfitRate.setValue(aFinanceMain.getRepayProfitRate());
			this.repayRate.setEffRateValue(aFinanceMain.getRepayProfitRate());
			this.repayRate.setEffRateText(
					PennantApplicationUtil.formatRate(aFinanceMain.getRepayProfitRate().doubleValue(), 2));
			this.finMinRate.setValue(BigDecimal.ZERO);
			this.finMaxRate.setValue(BigDecimal.ZERO);
		}

		if (StringUtils.isNotEmpty(aFinanceMain.getRepayFrq())
				|| !aFinanceMain.getRepayFrq().equals(PennantConstants.List_Select)) {
			this.rpyFrqRow.setVisible(true);
			this.repayFrq.setValue(aFinanceMain.getRepayFrq());
		}

		this.repayPftFrq.setDisabled(isReadOnly("FinanceMainDialog_repayPftFrq"));

		if (StringUtils.isNotEmpty(aFinanceMain.getRepayPftFrq())
				|| !aFinanceMain.getRepayPftFrq().equals(PennantConstants.List_Select)) {
			this.rpyPftFrqRow.setVisible(true);
			this.repayPftFrq.setValue(aFinanceMain.getRepayPftFrq());
		}

		if (aFinanceMain.isAllowRepayRvw()) {
			this.repayRvwFrq.setDisabled(isReadOnly("FinanceMainDialog_repayRvwFrq"));
			if (StringUtils.isNotEmpty(aFinanceMain.getRepayRvwFrq())
					|| !aFinanceMain.getRepayRvwFrq().equals(PennantConstants.List_Select)) {
				this.rpyRvwFrqRow.setVisible(true);
				this.repayRvwFrq.setValue(aFinanceMain.getRepayRvwFrq());
			}
			readOnlyComponent(isReadOnly("FinanceMainDialog_nextRepayRvwDate"), this.nextRepayRvwDate);
		} else {
			this.repayRvwFrq.setDisabled(true);
			readOnlyComponent(true, this.nextRepayRvwDate);
		}

		if (aFinanceMain.isAllowRepayCpz()) {
			if (isReadOnly("FinanceMainDialog_repayCpzFrq")) {
				this.repayCpzFrq.setDisabled(true);
			} else {
				this.repayCpzFrq.setDisabled(false);
				readOnlyComponent(true, this.nextRepayCpzDate);
			}
			if (StringUtils.isNotEmpty(aFinanceMain.getRepayCpzFrq())
					|| !aFinanceMain.getRepayCpzFrq().equals(PennantConstants.List_Select)) {
				this.rpyCpzFrqRow.setVisible(true);
				this.repayCpzFrq.setValue(aFinanceMain.getRepayCpzFrq());
			}
			readOnlyComponent(isReadOnly("FinanceMainDialog_nextRepayCpzDate"), this.nextRepayCpzDate);
		} else {
			this.repayCpzFrq.setDisabled(true);
			readOnlyComponent(true, this.nextRepayCpzDate);
		}

		if (!aFinanceMain.isNewRecord() || !StringUtils.isNotBlank(aFinanceMain.getFinReference())) {
			if (moduleDefiner.equals(FinServiceEvent.CHGGRCEND)) {

				this.nextRepayCpzDate.setValue(aFinanceMain.getNextRepayCpzDate());
				this.nextRepayRvwDate.setValue(aFinanceMain.getNextRepayRvwDate());
				this.nextRepayPftDate.setValue(aFinanceMain.getNextRepayPftDate());
			}
		}

		this.nextRepayDate_two.setValue(aFinanceMain.getNextRepayDate());
		this.nextRepayRvwDate_two.setValue(aFinanceMain.getNextRepayRvwDate());
		this.nextRepayCpzDate_two.setValue(aFinanceMain.getNextRepayCpzDate());
		this.nextRepayPftDate_two.setValue(aFinanceMain.getNextRepayPftDate());

		this.finId.setValue(aFinanceMain.getFinID());
		this.finReference.setValue(aFinanceMain.getFinReference());
		if (financeType.isFinIsAlwDifferment() && aFinanceMain.getPlanDeferCount() == 0) {
			this.defferments.setReadonly(isReadOnly("FinanceMainDialog_defferments"));
		} else {
			this.defferments.setReadonly(true);
		}

		this.defferments.setValue(aFinanceMain.getDefferments());
		if (financeType.isAlwPlanDeferment() && StringUtils.isEmpty(moduleDefiner)) {
			this.planDeferCount.setReadonly(isReadOnly("FinanceMainDialog_planDeferCount"));
		} else {
			this.planDeferCount.setReadonly(true);
			this.hbox_PlanDeferCount.setVisible(false);
			getLabel_FinanceMainDialog_PlanDeferCount().setVisible(false);
		}

		if (!financeType.isFinIsAlwDifferment() && !financeType.isAlwPlanDeferment()) {
			this.defermentsRow.setVisible(false);
		}

		this.planDeferCount.setValue(aFinanceMain.getPlanDeferCount());

		// FinanceMain Details Tab ---> 4. Overdue Penalty Details
		if (financeType.isApplyODPenalty()) {

			FinODPenaltyRate penaltyRate = aFinanceDetail.getFinScheduleData().getFinODPenaltyRate();

			if (penaltyRate != null) {
				this.gb_OverDuePenalty.setVisible(true);
				this.applyODPenalty.setChecked(penaltyRate.isApplyODPenalty());
				this.oDIncGrcDays.setChecked(penaltyRate.isODIncGrcDays());
				fillComboBox(this.oDChargeCalOn, penaltyRate.getODChargeCalOn(),
						PennantStaticListUtil.getODCCalculatedOn(), "");
				this.oDGraceDays.setValue(penaltyRate.getODGraceDays());
				fillComboBox(this.oDChargeType, penaltyRate.getODChargeType(), PennantStaticListUtil.getODCChargeType(),
						"");
				if (ChargeType.FLAT.equals(getComboboxValue(this.oDChargeType))
						|| ChargeType.FLAT_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))) {
					this.oDChargeAmtOrPerc
							.setValue(PennantApplicationUtil.formateAmount(penaltyRate.getODChargeAmtOrPerc(), format));
				} else if (ChargeType.PERC_ONE_TIME.equals(getComboboxValue(this.oDChargeType))
						|| ChargeType.PERC_ON_DUE_DAYS.equals(getComboboxValue(this.oDChargeType))
						|| ChargeType.PERC_ON_EFF_DUE_DAYS.equals(getComboboxValue(this.oDChargeType))
						|| ChargeType.PERC_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))) {
					this.oDChargeAmtOrPerc
							.setValue(PennantApplicationUtil.formateAmount(penaltyRate.getODChargeAmtOrPerc(), 2));
				}
				this.oDAllowWaiver.setChecked(penaltyRate.isODAllowWaiver());
				this.oDMaxWaiverPerc.setValue(penaltyRate.getODMaxWaiverPerc());
			} else {
				this.applyODPenalty.setChecked(false);
				this.gb_OverDuePenalty.setVisible(false);
				fillComboBox(this.oDChargeCalOn, "", PennantStaticListUtil.getODCCalculatedOn(), "");
				fillComboBox(this.oDChargeType, "", PennantStaticListUtil.getODCChargeType(), "");
			}
		} else {
			this.applyODPenalty.setChecked(false);
			this.gb_OverDuePenalty.setVisible(false);
			fillComboBox(this.oDChargeCalOn, "", PennantStaticListUtil.getODCCalculatedOn(), "");
			fillComboBox(this.oDChargeType, "", PennantStaticListUtil.getODCChargeType(), "");
		}

		this.recordStatus.setValue(getLiabilityRequest().getRecordStatus());

		if (aFinanceDetail.getFinScheduleData().getFinanceScheduleDetails().size() > 0) {
			aFinanceDetail.getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(true);
		}
		long intiateUser = aFinanceDetail.getFinScheduleData().getFinanceMain().getInitiateUser();

		if (intiateUser == 0) {
			if (isFirstTask() && getUserWorkspace().getUserRoles().contains(getWorkFlow().firstTaskOwner())) {
				aFinanceDetail.getFinScheduleData().getFinanceMain()
						.setInitiateUser(getUserWorkspace().getLoggedInUser().getUserId());
			}
		}

		if (this.alwLoanSplit.isChecked()) {
			this.parentLoanReference.setVisible(true);
			label_FinanceMainDialog_ParentLoanReference.setVisible(true);
		} else {
			this.parentLoanReference.setVisible(false);
			label_FinanceMainDialog_ParentLoanReference.setVisible(false);
		}

		setReadOnlyForCombobox();

		// Customer Details
		appendCustomerDetailTab(true);

		// Schedule Details Tab Adding
		appendScheduleDetailTab(true, true);

		// Agreements Detail Tab Addition
		appendAgreementsDetailTab(true);

		// CheckList Details Tab Addition
		appendCheckListDetailTab(getFinanceDetail(), true);

		// Recommend & Comments Details Tab Addition
		appendRecommendDetailTab(true);

		// Document Detail Tab Addition
		appendDocumentDetailTab(true);

		// Stage Accounting details Tab Addition
		appendStageAccountingDetailsTab(true);

		appendCustomerDetailTab(false);
		appendRecommendDetailTab(false);
		appendDocumentDetailTab(false);
		logger.debug("Leaving");
	}

	/**
	 * To pass Data For Agreement Child Windows Used in reflection
	 * 
	 * @return
	 */
	public FinanceDetail getAgrFinanceDetails() {
		logger.debug("Entering");

		FinanceDetail aFinanceDetail = new FinanceDetail();
		aFinanceDetail = ObjectUtil.clone(getFinanceDetail());

		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		doWriteComponentsToBean(aFinanceDetail.getFinScheduleData());

		// Customer Details Tab ---> Customer Details
		if (getCustomerDialogCtrl() != null) {
			processCustomerDetails(aFinanceDetail, false);
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
		aFinanceDetail.setCollateralAssignmentList(collateralAssignmentDAO
				.getCollateralAssignmentByFinRef(finReference.getValue(), FinanceConstants.MODULE_NAME, "_View"));

		aFinanceDetail.setJointAccountDetailList(
				jointAccountDetailDAO.getJointAccountDetailByFinRef(finReference.getValue(), "_AView"));

		aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);

		logger.debug("Leaving");
		return aFinanceDetail;
	}

	public void doShowLiabilityDialog() {
		logger.debug("Entering ");
		// Workflow Details
		setWorkflowDetails(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinType());
		getLiabilityRequest().setWorkflowId(getWorkFlowId());
		doLoadWorkFlow(getLiabilityRequest());
		getLiabilityRequest().setFinanceDetail(getFinanceDetail());
		this.tabBoxIndexCenter.setVisible(true);
		this.liabilityRef.setReadonly(true);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
			this.userAction = setListRecordStatus(this.userAction);
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (getLiabilityRequest().isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
			this.groupboxWf.setVisible(true);
			doShowDialog(getFinanceDetail());
		}

		logger.debug("Leaving ");
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

	private void doLoadWorkFlow(LiabilityRequest liabilityRequest) {
		logger.debug("Entering");
		String roleCode = null;
		if (!liabilityRequest.isNewRecord()
				&& StringUtils.trimToEmpty(liabilityRequest.getNextTaskId()).contains(";")) {
			roleCode = getFinanceDetailService().getUserRoleCodeByRefernce(
					getUserWorkspace().getUserDetails().getUserId(), liabilityRequest.getFinReference(),
					getUserWorkspace().getUserRoles());
		}

		if (null == roleCode) {
			doLoadWorkFlow(liabilityRequest.isWorkflow(), liabilityRequest.getWorkflowId(),
					liabilityRequest.getNextTaskId());
		} else {
			doLoadWorkFlow(liabilityRequest.isWorkflow(), liabilityRequest.getWorkflowId(),
					liabilityRequest.getNextTaskId());
		}
		logger.debug("Entering");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	protected void doEdit() {
		logger.debug("Entering");
		getUserWorkspace().allocateRoleAuthorities(getRole(), super.pageRightName);
		readOnlyComponent(isReadOnly("LiabilityRequestDialog_TakafulPaidStatus"), this.insPaidStatus);
		readOnlyComponent(isReadOnly("LiabilityRequestDialog_TakafulClaimReason"), this.insClaimReason);
		this.insClaimAmount.setReadonly(isReadOnly("LiabilityRequestDialog_TakafulClaimAmount"));
		logger.debug("Leaving");
	}

	protected void doSetFieldProperties() {
		logger.debug("Entering");
		this.insClaimAmount.setMandatory(true);
		int format = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		this.insClaimAmount.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.insClaimAmount.setTextBoxWidth(180);
		this.insClaimAmount.setScale(format);
		this.finAmount.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.finAmount.setScale(format);
		this.finRepaymentAmount.setScale(format);
		logger.debug("Leaving");
	}

	public LiabilityRequestService getLiabilityRequestService() {
		return liabilityRequestService;
	}

	public void setLiabilityRequestService(LiabilityRequestService liabilityRequestService) {
		this.liabilityRequestService = liabilityRequestService;
	}

	public LiabilityRequest getLiabilityRequest() {
		return liabilityRequest;
	}

	public void setLiabilityRequest(LiabilityRequest liabilityRequest) {
		this.liabilityRequest = liabilityRequest;
	}

	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return financeWorkFlowService;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	public LiabilityRequestListCtrl getLiabilityRequestListCtrl() {
		return liabilityRequestListCtrl;
	}

	public void setLiabilityRequestListCtrl(LiabilityRequestListCtrl liabilityRequestListCtrl) {
		this.liabilityRequestListCtrl = liabilityRequestListCtrl;
	}

	@Autowired
	public void setCollateralAssignmentDAO(CollateralAssignmentDAO collateralAssignmentDAO) {
		this.collateralAssignmentDAO = collateralAssignmentDAO;
	}

	@Autowired
	public void setJointAccountDetailDAO(JointAccountDetailDAO jointAccountDetailDAO) {
		this.jointAccountDetailDAO = jointAccountDetailDAO;
	}

}
