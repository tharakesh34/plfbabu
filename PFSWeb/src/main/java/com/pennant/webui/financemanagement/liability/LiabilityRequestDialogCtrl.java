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
 * FileName    		:  LiabilityRequestDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-12-2015    														*
 *                                                                  						*
 * Modified Date    :  31-12-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-12-2015       Pennant	                 0.1                                            * 
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
package com.pennant.webui.financemanagement.liability;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.liability.LiabilityRequest;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.finance.liability.service.LiabilityRequestService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.financemain.FinanceMainBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.rits.cloning.Cloner;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Finance
 * Management/LiabilityRequest/liabilityRequestDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class LiabilityRequestDialogCtrl extends FinanceMainBaseCtrl {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = Logger.getLogger(LiabilityRequestDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_LiabilityFinanceMainDialog; 

	protected Row row_ClaimReason;
	protected Combobox insClaimReason;
	protected CurrencyBox insClaimAmount;
	protected Row row_InsPaidStatus;
	protected Combobox insPaidStatus;
	
	protected ExtendedCombobox liabilityRef; 

	private LiabilityRequest liabilityRequest;
	private LiabilityRequestListCtrl liabilityRequestListCtrl;

	private LiabilityRequestService liabilityRequestService;
	private FinanceWorkFlowService financeWorkFlowService;

	/**
	 * default constructor.<br>
	 */
	public LiabilityRequestDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.doSetProperties();
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected financeMain object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_LiabilityFinanceMainDialog(Event event) throws Exception {
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
		setProductCode("Murabaha");

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
		if (getLiabilityRequest().isNewRecord()) {
			doShowLiabilityDialog();
		} else {
			doShowDialog(getFinanceDetail());
		}
		
		// Setting tile Name based on Service Action
		if(StringUtils.isNotEmpty(moduleDefiner)){
			this.windowTitle.setValue(Labels.getLabel(moduleDefiner+"_Window.Title"));
		}
		
		this.basicDetailTabDiv.setHeight(this.borderLayoutHeight - 100 + "px");
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws InterruptedException
	 */
	protected void doShowDialog(FinanceDetail afinanceDetail) throws InterruptedException {
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
				this.row_manualSteps.setVisible(false);
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
	 * @throws Exception
	 */
	public void onClose$window_LiabilityFinanceMainDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doClose();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnSave(Event event) throws Exception {
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
	 * @throws Exception
	 */
	public void onClick$btnClose(Event event) throws Exception {
		doClose(this.btnSave.isVisible());
	}

	// CRUD operations

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws Exception
	 */
	public void doSave() throws Exception {
		logger.debug("Entering");

		FinanceDetail aFinanceDetail = new FinanceDetail();
		Cloner cloner = new Cloner();
		aFinanceDetail = cloner.deepClone(getFinanceDetail());

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
		aFinanceDetail.setModuleDefiner(StringUtils.isEmpty(moduleDefiner) ? FinanceConstants.FINSER_EVENT_ORG
				: moduleDefiner);

		// Resetting Service Task ID's from Original State
		aFinanceMain.setRoleCode(this.curRoleCode);
		aFinanceMain.setNextRoleCode(this.curNextRoleCode);
		aFinanceMain.setTaskId(this.curTaskId);
		aFinanceMain.setNextTaskId(this.curNextTaskId);
		aFinanceMain.setNextUserId(this.curNextUserId);
		
		// force validation, if on, than execute by component.getValue()
		// fill the financeMain object with the components data
		this.doWriteComponentsToBean(aFinanceDetail.getFinScheduleData());
		isNew = getLiabilityRequest().isNew();

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
				if (getStageAccountingDetailDialogCtrl().getStageDisbCrSum().compareTo(
						getStageAccountingDetailDialogCtrl().getStageDisbDrSum()) != 0) {
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

				// Mail Alert Notification for Customer/Dealer/Provider...etc
				if (!"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())) {

					List<String> templateTyeList = new ArrayList<String>();
					templateTyeList.add(NotificationConstants.TEMPLATE_FOR_AE);
					templateTyeList.add(NotificationConstants.TEMPLATE_FOR_CN);

					List<ValueLabel> referenceIdList = getFinanceReferenceDetailService().getTemplateIdList(
							aFinanceMain.getFinType(),
							StringUtils.isEmpty(moduleDefiner) ? FinanceConstants.FINSER_EVENT_ORG : moduleDefiner,
							getRole(), templateTyeList);

					templateTyeList = null;
					if (!referenceIdList.isEmpty()) {

						boolean isCustomerNotificationExists = false;
						List<Long> notificationIdlist = new ArrayList<Long>();
						for (ValueLabel valueLabel : referenceIdList) {
							notificationIdlist.add(Long.valueOf(valueLabel.getValue()));
							if (NotificationConstants.TEMPLATE_FOR_CN.equals(valueLabel.getLabel())) {
								isCustomerNotificationExists = true;
							}
						}

						// Mail ID details preparation
						Map<String, List<String>> mailIDMap = new HashMap<String, List<String>>();

						// Customer Email Preparation
						if (isCustomerNotificationExists
								&& aFinanceDetail.getCustomerDetails().getCustomerEMailList() != null
								&& !aFinanceDetail.getCustomerDetails().getCustomerEMailList().isEmpty()) {

							List<CustomerEMail> emailList = aFinanceDetail.getCustomerDetails().getCustomerEMailList();
							List<String> custMailIdList = new ArrayList<String>();
							for (CustomerEMail customerEMail : emailList) {
								custMailIdList.add(customerEMail.getCustEMail());
							}
							if (!custMailIdList.isEmpty()) {
								mailIDMap.put(NotificationConstants.TEMPLATE_FOR_CN, custMailIdList);
							}
						}

						getMailUtil().sendMail(notificationIdlist, aFinanceDetail, mailIDMap, null);
					}

				}

				// User Notifications Message/Alert
				try {
					if (!"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							&& !"Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							&& !this.userAction.getSelectedItem().getLabel().contains("Reject")) {

						if (StringUtils.isNotEmpty(getLiabilityRequest().getNextRoleCode())) {
							if (!PennantConstants.RCD_STATUS_CANCELLED.equals(getLiabilityRequest().getRecordStatus())) {
								String[] to = getLiabilityRequest().getNextRoleCode().split(",");
								String message;

								if (StringUtils.isBlank(getLiabilityRequest().getNextTaskId())) {
									message = Labels.getLabel("REC_FINALIZED_MESSAGE");
								} else {
									message = Labels.getLabel("REC_PENDING_MESSAGE");
								}
								message += " with Reference" + ":" + getLiabilityRequest().getFinReference();

								getEventManager().publish(message, to, finDivision, getLiabilityRequest().getFinBranch());
							}
						}
					}
				} catch (Exception e) {
					logger.error("Exception: ", e);
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
						getLiabilityRequest().getNextRoleCode(), getLiabilityRequest().getFinReference(), " Finance ",
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
	 * @throws Exception
	 */
	private boolean doProcess(FinanceDetail aFinanceDetail, String tranType) throws Exception, InterfaceException {
		logger.debug("Entering");

		boolean processCompleted = true;
		AuditHeader auditHeader = null;
		FinanceMain afinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		int format=CurrencyUtil.getFormat(afinanceMain.getFinCcy());

		afinanceMain.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		afinanceMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		afinanceMain.setUserDetails(getUserWorkspace().getLoggedInUser());
		afinanceMain.setRoleCode(getRole());

		aFinanceDetail.getFinScheduleData().setFinanceMain(afinanceMain);
		aFinanceDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		LiabilityRequest liabilityRequest = getLiabilityRequest();
		liabilityRequest.setCustCIF(aFinanceDetail.getCustomerDetails().getCustomer().getCustCIF());
		liabilityRequest.setFinReference(this.finReference.getValue());

		if (StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_INSCLAIM)) {
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
				liabilityRequest.setInsClaimAmount(PennantApplicationUtil.unFormateAmount(
						this.insClaimAmount.getActualValue(), format));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (!recSave && !this.insPaidStatus.isDisabled()
						&& getComboboxValue(this.insPaidStatus).equals(PennantConstants.List_Select)) {
					throw new WrongValueException(this.insPaidStatus,
							Labels.getLabel("STATIC_INVALID", new String[] { Labels
									.getLabel("label_LiabilityFinanceMainDialog_TakafulPaidStatus.value") }));
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

		if(liabilityRequest.isNewRecord()){
			liabilityRequest.setFinEvent(moduleDefiner);
			liabilityRequest.setInitiatedBy(afinanceMain.getInitiateUser());
		}
		liabilityRequest.setVersion(afinanceMain.getVersion());
		liabilityRequest.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		liabilityRequest.setLastMntOn(new Timestamp(System.currentTimeMillis()));

		liabilityRequest.setFinanceDetail(aFinanceDetail);
		liabilityRequest.setUserDetails(aFinanceDetail.getUserDetails());

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

				if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_DDAMaintenance)) {
					processCompleted = true;
				} else if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckCollaterals)) {
					processCompleted = true;
				} else if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckFurtherWF)) {

					// Fetch Details of Workflow process followed by existing
					// Flow
					String nextFinEvent = getLiabilityRequestService().getProceedingWorkflow(this.finType.getValue(),
							this.moduleDefiner);
					if (StringUtils.isNotBlank(nextFinEvent)) {
						MessageUtil.showMessage(Labels.getLabel("menu_Item_" + nextFinEvent));
					}


				}else if(StringUtils.trimToEmpty(method).contains(FinanceConstants.method_scheduleChange)){
					List<String> finTypeList =getFinanceDetailService().getScheduleEffectModuleList(true);
					boolean isScheduleModify = false;
					for(String fintypeList :finTypeList){
						if(StringUtils.equals(moduleDefiner,fintypeList)){
							isScheduleModify = true;
							break;
						}
					}
					if(isScheduleModify){
						afinanceMain.setScheduleChange(true);
					}else{
						afinanceMain.setScheduleChange(false);
					}
				} else {
					LiabilityRequest tLiabilityRequest = (LiabilityRequest) auditHeader.getAuditDetail().getModelData();
					setNextTaskDetails(taskId, tLiabilityRequest.getFinanceDetail().getFinScheduleData()
							.getFinanceMain());
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
		String action = userAction.getSelectedItem().getLabel();
		String nextTaskId = StringUtils.trimToEmpty(liabilityRequest.getNextTaskId());

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
			nextTaskId = getNextTaskIds(taskId, liabilityRequest);
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

		liabilityRequest.setTaskId(taskId);
		liabilityRequest.setNextTaskId(nextTaskId);
		liabilityRequest.setRoleCode(getRole());
		liabilityRequest.setNextRoleCode(nextRoleCode);

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
	 * @throws InterruptedException
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws InterruptedException {
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
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
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

		} catch (InterruptedException e) {
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
	 * @param aFinanceMain
	 *            financeMain
	 * @throws ParseException
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */
	public void doWriteBeanToComponents(FinanceDetail aFinanceDetail, boolean onLoadProcess) throws ParseException,
			InterruptedException, InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		int format=CurrencyUtil.getFormat(aFinanceMain.getFinCcy());
		FinanceType financeType = aFinanceDetail.getFinScheduleData().getFinanceType();

		this.liabilityRef.setValue(aFinanceMain.getFinReference());
		this.liabilityRef.setObject(aFinanceMain);
		if (!getLiabilityRequest().isNewRecord()) {
			this.liabilityRef.setReadonly(true);
		}
		// Showing Product Details for Promotion Type
		this.finDivisionName.setValue(financeType.getFinDivision() + " - " + financeType.getLovDescFinDivisionName());
		if (StringUtils.isNotEmpty(financeType.getProduct())) {
			this.hbox_PromotionProduct.setVisible(true);
			this.getLabel_FinanceMainDialog_PromoProduct().setVisible(true);
			this.promotionProduct.setValue(financeType.getProduct() + " - " + financeType.getLovDescPromoFinTypeDesc());
			getLabel_FinanceMainDialog_FinType().setValue(
					Labels.getLabel("label_FinanceMainDialog_PromotionCode.value"));
		}

		this.repayAcctId.setMandatoryStyle(!isReadOnly("FinanceMainDialog_ManRepayAcctId"));
		if (getWorkFlow() != null
				&& !"Accounting".equals(getTaskTabs(getTaskId(getRole())))) {
			this.disbAcctId.setMandatoryStyle(!isReadOnly("FinanceMainDialog_MandisbAcctId"));
			this.downPayAccount.setMandatoryStyle(!isReadOnly("FinanceMainDialog_MandownPaymentAcc"));
		} else {
			if (!this.disbAcctId.isReadonly()) {
				this.disbAcctId.setMandatoryStyle(true);
			}
			if (!this.downPayAccount.isReadonly()) {
				this.downPayAccount.setMandatoryStyle(true);
			}
			if (this.downPayBank.isReadonly() && aFinanceMain.getDownPayBank().compareTo(BigDecimal.ZERO) == 0) {
				this.downPayAccount.setMandatoryStyle(false);
			}
		}

		if (isReadOnly("FinanceMainDialog_ManRepayAcctId")) {
			this.repayAcctId.setMandatoryStyle(false);
		} else {
			this.repayAcctId.setMandatoryStyle(true);
		}

		// Finance MainDetails Tab ---> 1. Basic Details

		this.finType.setValue(aFinanceMain.getFinType());
		this.finCcy.setValue(aFinanceMain.getFinCcy());
		fillComboBox(this.cbProfitDaysBasis, aFinanceMain.getProfitDaysBasis(), PennantStaticListUtil.getProfitDaysBasis(), "");
		fillComboBox(this.finRepayMethod, aFinanceMain.getFinRepayMethod(), PennantStaticListUtil.getRepayMethods(), "");
		this.finBranch.setValue(aFinanceMain.getFinBranch());
		this.custCIF.setValue(aFinanceMain.getLovDescCustCIF());
		this.custShrtName.setValue(aFinanceMain.getLovDescCustShrtName());
		this.custID.setValue(aFinanceMain.getCustID());
		this.disbAcctId.setCustCIF(aFinanceMain.getLovDescCustCIF());
		this.repayAcctId.setCustCIF(aFinanceMain.getLovDescCustCIF());
		this.downPayAccount.setCustCIF(aFinanceMain.getLovDescCustCIF());
		this.finPurpose.setValue(aFinanceMain.getFinPurpose());
		this.disbAcctId.setValue(aFinanceMain.getDisbAccountId());
		this.repayAcctId.setValue(aFinanceMain.getRepayAccountId());
		

		String repayMethod = aFinanceMain.getFinRepayMethod();
		if (StringUtils.isEmpty(repayMethod)) {
			if (!getFinanceDetail().getCustomerDetails().getCustomer().isSalariedCustomer()) {
				repayMethod = FinanceConstants.REPAYMTH_AUTODDA;
			} else {
				repayMethod = FinanceConstants.REPAYMTH_AUTO;
			}
		}
		fillComboBox(this.finRepayMethod, repayMethod, PennantStaticListUtil.getRepayMethods(), "");
		fillComboBox(this.accountType, "", PennantStaticListUtil.getAccountTypes(), "");
		doCheckDDA();

		this.commitmentRef.setValue(aFinanceMain.getFinCommitmentRef(),StringUtils.trimToEmpty(aFinanceMain.getFinCommitmentRef()));
		this.finLimitRef.setValue(aFinanceMain.getFinLimitRef(), StringUtils.trimToEmpty(aFinanceMain.getFinLimitRef()));
		
		if(!aFinanceMain.isTDSApplicable()){
			this.tDSApplicable.setVisible(false);
			this.label_FinanceMainDialog_TDSApplicable.setVisible(false);
		}

		if (!financeType.isFinCommitmentReq()) {
			getLabel_FinanceMainDialog_CommitRef().setVisible(false);
			this.commitmentRef.setVisible(false);
			this.commitmentRef.setReadonly(true);
		}

		// if Insurance Claim then claim amount, paidStatus, claim reason fields should be visible
		if (StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_INSCLAIM)) {
			this.row_ClaimReason.setVisible(true);
			this.row_InsPaidStatus.setVisible(true);
			fillComboBox(this.insClaimReason, getLiabilityRequest().getInsClaimReason(),
					PennantStaticListUtil.getInsClaimReasonList(), "");
			fillComboBox(this.insPaidStatus, getLiabilityRequest().getInsPaidStatus(), PennantStaticListUtil.getInsPaidStatusList(),
					"");
			this.insClaimAmount.setValue(PennantAppUtil.formateAmount(
					getLiabilityRequest().getInsClaimAmount(), format));
			doEdit();
		}

		if (!financeType.isFinDepreciationReq()) {
			this.depreciationFrq.setDisabled(true);
			getLabel_FinanceMainDialog_DepriFrq().setVisible(false);
			this.depreciationFrq.setMandatoryStyle(false);
		} else {
			readOnlyComponent(isReadOnly("FinanceMainDialog_depreciationFrq"), this.depreciationFrq);
			getLabel_FinanceMainDialog_DepriFrq().setVisible(true);
			this.depreciationFrq.setVisible(true);
			this.depreciationFrq.setValue(aFinanceMain.getDepreciationFrq());
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
			this.row_downPaySupl.setVisible(true);
			if (getProductCode().equalsIgnoreCase(FinanceConstants.PRODUCT_ISTISNA)
					|| getProductCode().equalsIgnoreCase(FinanceConstants.PRODUCT_SUKUK)) {
				this.downPayPercentage.setVisible(true);
			} else {
				this.row_downPayPercentage.setVisible(true);
			}
			this.downPayAccount.setValue(aFinanceMain.getDownPayAccount());
			this.downPayBank.setValue(PennantAppUtil.formateAmount(aFinanceMain.getDownPayBank(),
					format));
			this.downPaySupl.setValue(PennantAppUtil.formateAmount(aFinanceMain.getDownPaySupl(),
					format));
			if (aFinanceMain.isNewRecord()) {
				this.downPayAccount.setValue("");
			} else {
				this.downPayAccount.setValue(aFinanceMain.getDownPayAccount());
			}

			if (this.downPayBank.isReadonly() && aFinanceMain.getDownPayBank().compareTo(BigDecimal.ZERO) == 0) {
				this.downPayAccount.setMandatoryStyle(false);
				this.downPayAccount.setReadonly(true);
				this.row_downPayBank.setVisible(false);
			}

			if (this.downPayBank.isReadonly() && this.downPaySupl.isReadonly()
					&& aFinanceMain.getDownPayment().compareTo(BigDecimal.ZERO) == 0) {
				if (getProductCode().equalsIgnoreCase(FinanceConstants.PRODUCT_ISTISNA)
						|| getProductCode().equalsIgnoreCase(FinanceConstants.PRODUCT_SUKUK)) {
					this.downPayPercentage.setVisible(false);
				} else {
					this.row_downPayPercentage.setVisible(false);
				}
			}

			if (this.downPaySupl.isReadonly() && aFinanceMain.getDownPaySupl().compareTo(BigDecimal.ZERO) == 0) {
				this.row_downPaySupl.setVisible(false);
			}
		} else {
			this.downPayAccount.setMandatoryStyle(false);
		}

		if (aFinanceMain.getMinDownPayPerc().compareTo(BigDecimal.ZERO) == 0) {
			this.downPayBank.setMandatory(false);
			this.downPaySupl.setMandatory(false);
		}

		if (financeType.isAllowDownpayPgm()) {
			this.row_downPaySupl.setVisible(true);
			this.downPayAccount.setReadonly(true);
			this.downPayBank.setReadonly(true);
			this.downPayAccount.setValue(SysParamUtil.getValueAsString("AHB_DOWNPAY_AC"));
		}
		setDownPayPercentage();
		setNetFinanceAmount(true);
		// Setting DownPayment Supplier to Invisible state to some of the
		// Products
		if (getProductCode().equalsIgnoreCase(FinanceConstants.PRODUCT_MUSHARAKA)
				|| getProductCode().equalsIgnoreCase(FinanceConstants.PRODUCT_SUKUKNRM)
				|| getProductCode().equalsIgnoreCase(FinanceConstants.PRODUCT_ISTISNA)) {
			this.row_downPaySupl.setVisible(false);
			this.downPaySupl.setReadonly(true);
		}
		if (getProductCode().equalsIgnoreCase(FinanceConstants.PRODUCT_ISTISNA)) {
			this.disbAcctId.setReadonly(true);
		}

		this.finPurpose.setValue(aFinanceMain.getFinPurpose());
		if (StringUtils.isNotBlank(aFinanceMain.getFinPurpose())) {
			this.finPurpose.setDescription(aFinanceMain.getLovDescFinPurposeName());
		}
		this.securityDeposit.setValue(PennantAppUtil.formateAmount(aFinanceMain.getSecurityDeposit(),
				format));


		// Step Finance
		if ((aFinanceMain.isNewRecord() || !aFinanceMain.isStepFinance()) && !financeType.isStepFinance()) {
			this.row_stepFinance.setVisible(false);
		}
		this.stepFinance.setChecked(aFinanceMain.isStepFinance());
		this.stepPolicy.setValue(aFinanceMain.getStepPolicy());
		this.stepPolicy.setDescription(aFinanceMain.getLovDescStepPolicyName());
		this.alwManualSteps.setChecked(aFinanceMain.isAlwManualSteps());
		this.noOfSteps.setValue(aFinanceMain.getNoOfSteps());
		fillComboBox(this.stepType, aFinanceMain.getStepType(), PennantStaticListUtil.getStepType(), "");	
		

		if (aFinanceMain.isNewRecord()) {
			if (aFinanceMain.isAlwManualSteps()
					&& getFinanceDetail().getFinScheduleData().getStepPolicyDetails() == null) {
				getFinanceDetail().getFinScheduleData().resetStepPolicyDetails(null);
			}
		}

		if (StringUtils.isNotEmpty(aFinanceMain.getShariaStatus())
				&& !StringUtils.equals(PennantConstants.SHARIA_STATUS_NOTREQUIRED, aFinanceMain.getShariaStatus())) {
			this.shariaApprovalReq.setChecked(true);
		} else {
			this.shariaApprovalReq.setChecked(false);
		}
		if (StringUtils.equals(PennantConstants.SHARIA_STATUS_DECLINED, aFinanceMain.getShariaStatus())) {
			this.shariaApprovalReq.setDisabled(true);
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

			fillComboBox(this.cbGrcSchdMthd, aFinanceMain.getGrcSchdMthd(), PennantStaticListUtil.getScheduleMethods(), ",EQUAL,PRI_PFT,PRI,");
			if (aFinanceMain.isAllowGrcRepay()) {
				this.graceTerms.setVisible(true);
				this.grcRepayRow.setVisible(true);
				this.allowGrcRepay.setChecked(aFinanceMain.isAllowGrcRepay());
			}

			this.graceTerms.setText("");
			this.graceRate.setMarginValue(aFinanceMain.getGrcMargin());
			fillComboBox(this.grcPftDaysBasis, aFinanceMain.getGrcProfitDaysBasis(), PennantStaticListUtil.getProfitDaysBasis(), "");
			if (StringUtils.isNotEmpty(aFinanceMain.getGraceBaseRate())
					&& StringUtils.equals(CalculationConstants.RATE_BASIS_R, this.grcRateBasis.getSelectedItem()
							.getValue().toString())) {
				this.grcBaseRateRow.setVisible(true);
				this.graceRate.setVisible(true);
				this.graceRate.setBaseValue(aFinanceMain.getGraceBaseRate());
				this.graceRate.setSpecialValue(aFinanceMain.getGraceSpecialRate());
				if ((financeType.getFInGrcMinRate() == null || BigDecimal.ZERO.compareTo(financeType.getFInGrcMinRate()) == 0)
						&& (financeType.getFinGrcMaxRate() == null || BigDecimal.ZERO.compareTo(financeType
								.getFinGrcMaxRate()) == 0)) {
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
					this.graceRate.setEffRateText(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan()
							.doubleValue(), 2));
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
				this.graceRate.setEffRateText(PennantApplicationUtil.formatRate(aFinanceMain.getGrcPftRate().doubleValue(), 2));
				this.finGrcMinRate.setValue(BigDecimal.ZERO);
				this.finGrcMaxRate.setValue(BigDecimal.ZERO);

				this.grcAdvRate.setBaseValue("");
				this.grcAdvRate.setBaseDescription("");
				this.grcAdvRate.setMarginText("");
				this.grcAdvPftRate.setText("");
			}

			// Advised profit Rates
			doCheckAdviseRates(aFinanceMain.getGrcAdvBaseRate(), aFinanceMain.getRpyAdvBaseRate(), true,
					financeType.getFinCategory());
			this.grcAdvRate.setBaseValue(aFinanceMain.getGrcAdvBaseRate());
			this.grcAdvRate.setMarginValue(aFinanceMain.getGrcAdvMargin());
			this.grcAdvPftRate.setValue(aFinanceMain.getGrcAdvPftRate());
			calAdvPftRate(this.grcAdvRate.getBaseValue(), this.finCcy.getValue(), this.grcAdvRate.getMarginValue(),
					BigDecimal.ZERO, BigDecimal.ZERO, this.grcAdvRate.getEffRateComp());

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
		this.finRepaymentAmount.setValue(PennantAppUtil.formateAmount(aFinanceMain.getReqRepayAmount(),
				format));
		this.finAmount.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinAmount(),
				format));

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
		fillComboBox(this.cbScheduleMethod, aFinanceMain.getScheduleMethod(), PennantStaticListUtil.getScheduleMethods(), ",NO_PAY,GRCNDPAY,");
		if (StringUtils.isNotEmpty(aFinanceMain.getRepayBaseRate())
				&& StringUtils.equals(CalculationConstants.RATE_BASIS_R, this.repayRateBasis.getSelectedItem()
						.getValue().toString())) {
			this.repayBaseRateRow.setVisible(true);
			this.repayRate.setBaseValue(aFinanceMain.getRepayBaseRate());
			this.repayRate.setSpecialValue(aFinanceMain.getRepaySpecialRate());

			RateDetail rateDetail = RateUtil.rates(this.repayRate.getBaseValue(), this.finCcy.getValue(),
					this.repayRate.getSpecialValue(), this.repayRate.getMarginValue(), this.finMinRate.getValue(),
					this.finMaxRate.getValue());

			if (rateDetail.getErrorDetails() == null) {
				this.repayRate.setEffRateText(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan()
						.doubleValue(), 2));
			}
			readOnlyComponent(true, this.repayProfitRate);

			if (financeType.getFInMinRate().compareTo(BigDecimal.ZERO) == 0
					&& financeType.getFinMaxRate().compareTo(BigDecimal.ZERO) == 0) {
				this.row_FinRepRates.setVisible(false);
			} else {
				this.row_FinRepRates.setVisible(true);
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
			this.repayRate.setEffRateText(PennantApplicationUtil.formatRate(aFinanceMain.getRepayProfitRate().doubleValue(), 2));
			this.finMinRate.setValue(BigDecimal.ZERO);
			this.finMaxRate.setValue(BigDecimal.ZERO);
		}

		// Advised profit Rates
		doCheckAdviseRates(aFinanceMain.getGrcAdvBaseRate(), aFinanceMain.getRpyAdvBaseRate(), false,
				financeType.getFinCategory());
		this.rpyAdvRate.setBaseValue(aFinanceMain.getRpyAdvBaseRate());
		this.rpyAdvRate.setMarginValue(aFinanceMain.getRpyAdvMargin());
		this.rpyAdvPftRate.setValue(aFinanceMain.getRpyAdvPftRate());
		calAdvPftRate(this.rpyAdvRate.getBaseValue(), this.finCcy.getValue(), this.rpyAdvRate.getMarginValue(),
				BigDecimal.ZERO, BigDecimal.ZERO, this.rpyAdvRate.getEffRateComp());

		// External Charges For Ijarah
		this.supplementRent.setValue(PennantAppUtil.formateAmount(aFinanceMain.getSupplementRent(),
				format));
		this.increasedCost.setValue(PennantAppUtil.formateAmount(aFinanceMain.getIncreasedCost(),
				format));

		this.repayFrq.setDisabled(isReadOnly("FinanceMainDialog_repayFrq"));
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

		if (this.rolloverFrqRow.isVisible()
				&& (StringUtils.isNotEmpty(aFinanceMain.getRolloverFrq()) || !aFinanceMain.getRolloverFrq().equals(
						PennantConstants.List_Select))) {
			this.rolloverFrq.setValue(aFinanceMain.getRolloverFrq());
		}

		if (!aFinanceMain.isNew() || !StringUtils.isNotBlank(aFinanceMain.getFinReference())) {
			if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_CHGGRCEND)) {

				this.nextRepayCpzDate.setValue(aFinanceMain.getNextRepayCpzDate());
				this.nextRepayRvwDate.setValue(aFinanceMain.getNextRepayRvwDate());
				this.nextRepayPftDate.setValue(aFinanceMain.getNextRepayPftDate());
			}
		}

		this.nextRepayDate_two.setValue(aFinanceMain.getNextRepayDate());
		this.nextRollOverDate_two.setValue(aFinanceMain.getNextRolloverDate());
		this.nextRepayRvwDate_two.setValue(aFinanceMain.getNextRepayRvwDate());
		this.nextRepayCpzDate_two.setValue(aFinanceMain.getNextRepayCpzDate());
		this.nextRepayPftDate_two.setValue(aFinanceMain.getNextRepayPftDate());

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
				fillComboBox(this.oDChargeType, penaltyRate.getODChargeType(),
						PennantStaticListUtil.getODCChargeType(), "");
				if (FinanceConstants.PENALTYTYPE_FLAT.equals(getComboboxValue(this.oDChargeType))
						|| FinanceConstants.PENALTYTYPE_FLAT_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))) {
					this.oDChargeAmtOrPerc.setValue(PennantAppUtil.formateAmount(penaltyRate.getODChargeAmtOrPerc(),
							format));
				} else if (FinanceConstants.PENALTYTYPE_PERC_ONETIME.equals(getComboboxValue(this.oDChargeType))
						|| FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS.equals(getComboboxValue(this.oDChargeType))
						|| FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))) {
					this.oDChargeAmtOrPerc
							.setValue(PennantAppUtil.formateAmount(penaltyRate.getODChargeAmtOrPerc(), 2));
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

		// FinanceMain Details Tab ---> 5. DDA Registration Details
		if (this.gb_ddaRequest.isVisible()) {
			this.bankName.setValue(aFinanceMain.getBankName());
			this.bankName.setDescription(aFinanceMain.getBankNameDesc());
			this.iban.setValue(aFinanceMain.getIban());
			fillComboBox(this.accountType, aFinanceMain.getAccountType(), PennantStaticListUtil.getAccountTypes(), "");
		}

		this.availCommitAmount = aFinanceMain.getAvailCommitAmount();
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
	 * @throws Exception
	 */
	public FinanceDetail getAgrFinanceDetails() throws Exception {
		logger.debug("Entering");

		FinanceDetail aFinanceDetail = new FinanceDetail();
		Cloner cloner = new Cloner();
		aFinanceDetail = cloner.deepClone(getFinanceDetail());

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

		aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);

		logger.debug("Leaving");
		return aFinanceDetail;
	}

	public void doShowLiabilityDialog() throws FileNotFoundException, XMLStreamException, InterruptedException,
			UnsupportedEncodingException, FactoryConfigurationError {
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
					moduleDefiner, PennantConstants.WORFLOW_MODULE_FINANCE);//TODO: Check Promotion case
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

	private void doLoadWorkFlow(LiabilityRequest liabilityRequest) throws FileNotFoundException, XMLStreamException,
			UnsupportedEncodingException, FactoryConfigurationError {
		logger.debug("Entering");
		String roleCode = null;
		if (!liabilityRequest.isNewRecord() && StringUtils.trimToEmpty(liabilityRequest.getNextTaskId()).contains(";")) {
			roleCode = getFinanceDetailService().getUserRoleCodeByRefernce(
					getUserWorkspace().getUserDetails().getUserId(), liabilityRequest.getFinReference(),
					getUserWorkspace().getUserRoles());
		}

		if (null == roleCode) {
			doLoadWorkFlow(liabilityRequest.isWorkflow(), liabilityRequest.getWorkflowId(),
					liabilityRequest.getNextTaskId());
		} else {
			doLoadWorkFlow(liabilityRequest.isWorkflow(), liabilityRequest.getWorkflowId(), liabilityRequest.getNextTaskId());
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
		int format=CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		
		this.insClaimAmount.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.insClaimAmount.setTextBoxWidth(180);
		this.insClaimAmount.setScale(format);
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

}
