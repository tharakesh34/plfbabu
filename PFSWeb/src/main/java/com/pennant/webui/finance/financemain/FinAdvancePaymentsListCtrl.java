/**
 * o * Copyright 2011 - Pennant Technologies
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
 * * FileName : FinAdvancePaymentsListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-08-2013 * *
 * Modified Date : 14-08-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-08-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.financemain;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.finance.FinAdvancePaymentsService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.webui.finance.payorderissue.DisbursementInstCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennapps.core.util.ObjectUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/FinAdvancePaymentsList.zul file.
 */
public class FinAdvancePaymentsListCtrl extends GFCBaseCtrl<FinAdvancePayments> {
	private static final long serialVersionUID = 4157448822555239535L;
	private static final Logger logger = LogManager.getLogger(FinAdvancePaymentsListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinAdvancePaymentsList;

	protected Button btnNew_NewFinAdvancePayments;
	protected Label label_AdvancePayments_Title;

	protected Listbox listBoxAdvancePayments;

	// For Dynamically calling of this Controller
	private FinanceDetail financedetail;
	private Object financeMainDialogCtrl;
	private Component parent = null;
	private Tab parentTab = null;
	private transient boolean recSave = false;
	private String roleCode = "";
	private boolean isEnquiry = false;
	private transient boolean newFinance;
	protected Groupbox finBasicdetails;
	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private String ModuleType_Loan = "LOAN";
	private List<FinAdvancePayments> finAdvancePaymentsList = new ArrayList<FinAdvancePayments>();
	private transient FinAdvancePaymentsService finAdvancePaymentsService;
	private DisbursementInstCtrl disbursementInstCtrl;
	private List<FinanceDisbursement> financeDisbursement;
	private List<FinanceDisbursement> approvedDisbursments;
	String moduleDefiner = "";
	private List<VASRecording> vasRecordingList = new ArrayList<VASRecording>();

	/**
	 * default constructor.<br>
	 */
	public FinAdvancePaymentsListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinAdvancePaymentsList";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected FinAdvancePayment object in a Map.
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_FinAdvancePaymentsList(ForwardEvent event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinAdvancePaymentsList);

		try {
			if (event.getTarget().getParent() != null) {
				parent = event.getTarget().getParent();
				// parent.setStyle("overflow:auto;");
			}

			if (arguments.containsKey("financeMainDialogCtrl")) {
				setFinanceMainDialogCtrl((Object) arguments.get("financeMainDialogCtrl"));
				this.window_FinAdvancePaymentsList.setTitle("");
				setNewFinance(true);
			}

			if (arguments.containsKey("roleCode")) {
				roleCode = (String) arguments.get("roleCode");
				getUserWorkspace().allocateRoleAuthorities(roleCode, "FinAdvancePaymentsList");
			}

			if (arguments.containsKey("parentTab")) {
				parentTab = (Tab) arguments.get("parentTab");
			}

			if (arguments.containsKey("isEnquiry")) {
				isEnquiry = (Boolean) arguments.get("isEnquiry");
			}

			if (arguments.containsKey("approvedDisbursments")) {
				approvedDisbursments = (List<FinanceDisbursement>) arguments.get("approvedDisbursments");
			}

			if (arguments.containsKey("moduleDefiner")) {
				moduleDefiner = (String) arguments.get("moduleDefiner");
			}

			if (arguments.containsKey("financeDetail")) {
				setFinancedetail((FinanceDetail) arguments.get("financeDetail"));
				if (getFinancedetail() != null) {
					if (getFinancedetail().getAdvancePaymentsList() != null) {
						setFinAdvancePaymentsList(getFinancedetail().getAdvancePaymentsList());
					}
				}
			}

			if (arguments.containsKey("financeMainDialogCtrl")) {
				this.financeMainDialogCtrl = (Object) arguments.get("financeMainDialogCtrl");
				try {
					financeMainDialogCtrl.getClass().getMethod("setFinAdvancePaymentsListCtrl", this.getClass())
							.invoke(financeMainDialogCtrl, this);
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
				this.window_FinAdvancePaymentsList.setTitle("");
			}

			doEdit();
			doCheckRights();
			doShowDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	private FinanceDetail getFinancedetailsFromBase() {
		try {
			if (financeMainDialogCtrl != null) {
				return (FinanceDetail) financeMainDialogCtrl.getClass().getMethod("getFinanceDetail")
						.invoke(financeMainDialogCtrl);
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		return null;

	}

	/**
	 * Remove Error Messages for Fields
	 */

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		logger.debug("Leaving");
	}

	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("FinAdvancePaymentsList", roleCode);
		if (!StringUtils.equals(moduleDefiner, FinServiceEvent.CANCELDISB)) {
			this.btnNew_NewFinAdvancePayments
					.setVisible(getUserWorkspace().isAllowed("FinAdvancePaymentsList_NewFinAdvancePaymentsDetail"));
		}
		logger.debug("leaving");
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || isNewFinance()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @throws InterruptedException
	 */
	public void doShowDialog() throws InterruptedException {
		logger.debug("Entering");

		try {
			appendFinBasicDetails();
			doCheckEnquiry();
			FinanceMain finMain = getFinancedetail().getFinScheduleData().getFinanceMain();
			FinanceType financeType = getFinancedetail().getFinScheduleData().getFinanceType();
			disbursementInstCtrl.init(this.listBoxAdvancePayments, finMain.getFinCcy(),
					financeType.isAlwMultiPartyDisb(), roleCode);
			disbursementInstCtrl
					.setFinanceDisbursement(getFinancedetail().getFinScheduleData().getDisbursementDetails());
			disbursementInstCtrl.setFinanceMain(finMain);
			disbursementInstCtrl.setApprovedDisbursments(approvedDisbursments);

			doWriteBeanToComponents();

			this.listBoxAdvancePayments.setHeight(borderLayoutHeight - 226 + "px");
			if (parent != null) {
				this.window_FinAdvancePaymentsList.setHeight(borderLayoutHeight - 75 + "px");
				parent.appendChild(this.window_FinAdvancePaymentsList);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * 
	 */
	public void doWriteBeanToComponents() {
		doFillFinAdvancePaymentsDetails(getFinAdvancePaymentsList(),
				getFinancedetail().getFinScheduleData().getVasRecordingList());
	}

	private void doCheckEnquiry() {
		if (isEnquiry) {
			this.btnNew_NewFinAdvancePayments.setVisible(false);
		}
	}

	public boolean onAdvancePaymentValidation(Map<String, Object> map) {
		logger.debug("Entering");

		boolean proceed = true;
		boolean isFinalStage = false;
		String userAction = "";
		FinanceDetail finDetail = null;

		if (map.containsKey("userAction")) {
			userAction = (String) map.get("userAction");
		}

		if (map.containsKey("financeDetail")) {
			finDetail = (FinanceDetail) map.get("financeDetail");
		}

		if (map.containsKey("isFinalStage")) {
			isFinalStage = (boolean) map.get("isFinalStage");
		}

		if (map.containsKey("moduleDefiner")) {
			moduleDefiner = (String) map.get("moduleDefiner");
		}

		recSave = false;
		if ("Save".equalsIgnoreCase(userAction) || "Cancel".equalsIgnoreCase(userAction)
				|| "Reject".equalsIgnoreCase(userAction) || "Resubmit".equalsIgnoreCase(userAction)) {
			recSave = true;
		}

		// QDP Change: Once realized the disbursement it should not allow to reject or resubmit the loan
		FinanceMain fm = finDetail.getFinScheduleData().getFinanceMain();
		List<FinAdvancePayments> advPayments = finDetail.getAdvancePaymentsList();
		String moduleDef = finDetail.getModuleDefiner();

		if (fm.isQuickDisb() && CollectionUtils.isNotEmpty(advPayments)
				&& (StringUtils.isBlank(moduleDef) || FinServiceEvent.ORG.equals(moduleDef))) {
			boolean realized = false;
			boolean disbDownload = false;
			for (FinAdvancePayments advancePayments : advPayments) {
				String disbStatus = advancePayments.getStatus();
				String paymentType = advancePayments.getPaymentType();

				if (ImplementationConstants.CHEQUENO_MANDATORY_DISB_INS
						&& (DisbursementConstants.STATUS_PAID.equals(disbStatus)
								|| DisbursementConstants.STATUS_PRINT.equals(disbStatus))
						&& (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(paymentType)
								|| DisbursementConstants.PAYMENT_TYPE_DD.equals(paymentType))) {
					realized = true;
				} else if (DisbursementConstants.STATUS_REALIZED.equals(disbStatus)
						&& (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(paymentType)
								|| DisbursementConstants.PAYMENT_TYPE_DD.equals(paymentType))) {
					realized = true;
				} else if (DisbursementConstants.STATUS_PAID.equals(disbStatus)
						&& (DisbursementConstants.PAYMENT_TYPE_NEFT.equals(paymentType)
								|| DisbursementConstants.PAYMENT_TYPE_RTGS.equals(paymentType))) {
					realized = true;
				} else if (DisbursementConstants.STATUS_AWAITCON.equals(disbStatus)) {
					disbDownload = true;
				}
			}

			// Once realized the disbursement it should not allow to reject or resubmit the loan
			if ("Reject".equalsIgnoreCase(userAction)) {
				if (realized) {
					MessageUtil.showError("Quick Disbursement loan should not Reject or Resubmit.");
					return false;
				}
			}

			// with out realized the disbursement it should not allow to approve the loan
			if ("Approve".equalsIgnoreCase(userAction)) {
				if (!realized) {
					MessageUtil.showError("Disbursement instructions should be realized before approve the loan.");
					return false;
				}
			}

			// if disbursement status as AC then it should not allow to reject the loan
			if ("Reject".equalsIgnoreCase(userAction) && disbDownload) {
				MessageUtil.showError(
						"Quick Disbursement loan should not Reject in disbursement instructions status as Awaiting Conformation.");
				return false;
			}

			// if Disbursement Status as Approved and Record status as Resubmitted then we are not allowing to resubmit
			FinanceMain financeMain = fm;
			int count = finAdvancePaymentsService.getFinAdvCountByRef(financeMain.getFinID(), "");
			if (count > 0 && userAction.contains("Resubmit")) {
				MessageUtil.showError(
						"Quick Disbursement loan should not Resubmit in disbursement instructions status as Approved.");
				return false;
			}
		}

		doClearMessage();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if (!recSave) {
				FinanceMain main = null;
				FinScheduleData schdData = null;
				if (finDetail != null) {
					schdData = finDetail.getFinScheduleData();
					main = schdData.getFinanceMain();
				}

				if (main != null && main.getFinAmount() != null) {
					disbursementInstCtrl.setFinanceDisbursement(schdData.getDisbursementDetails());
					disbursementInstCtrl.setFinanceMain(main);
					disbursementInstCtrl.markCancelIfNoDisbursmnetFound(getFinAdvancePaymentsList());
					if (CollectionUtils.isNotEmpty(schdData.getFinFeeDetailList())) {
						disbursementInstCtrl.setFinFeeDetailList(schdData.getFinFeeDetailList());
					}
					boolean validate = getUserWorkspace()
							.isAllowed("FinAdvancePaymentsList_NewFinAdvancePaymentsDetail") || isFinalStage;

					if (StringUtils.equals(moduleDefiner, FinServiceEvent.CANCELDISB)
							|| userAction.contains("Resubmit")) {
						validate = false;
					}

					List<ErrorDetail> valid = disbursementInstCtrl
							.validateOrgFinAdvancePayment(getFinAdvancePaymentsList(), validate);
					// VAS FrontEndFunctionality Validations
					if (ImplementationConstants.VAS_INST_ON_DISB
							&& !StringUtils.equalsIgnoreCase(moduleDefiner, FinServiceEvent.ADDDISB)) {
						List<ErrorDetail> vasErrList = null;
						disbursementInstCtrl.setVasRecordingList(vasRecordingList);
						vasErrList = disbursementInstCtrl.validateVasInstructions(getFinAdvancePaymentsList(),
								validate);
						if (vasErrList != null) {
							valid.addAll(vasErrList);
						}
					}

					valid = ErrorUtil.getErrorDetails(valid, getUserWorkspace().getUserLanguage());

					if (valid != null && !valid.isEmpty()) {
						proceed = false;
						if (parentTab != null) {
							parentTab.setSelected(true);
						}
						for (ErrorDetail errorDetails : valid) {
							MessageUtil.showError(errorDetails.getError());
						}

					}
				}

			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		showErrorDetails(wve);

		if (finDetail != null && proceed) {
			List<FinAdvancePayments> finadvpaymentsList = ObjectUtil.clone(getFinAdvancePaymentsList());
			finDetail.setAdvancePaymentsList(finadvpaymentsList);
		}
		logger.debug("Leaving");
		return proceed;
	}

	private void showErrorDetails(ArrayList<WrongValueException> wve) {
		logger.debug("Entering");

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	public void onClick$btnNew_NewFinAdvancePayments(Event event)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		logger.debug("Entering" + event.toString());
		Clients.clearWrongValue(this.btnNew_NewFinAdvancePayments);
		Clients.clearWrongValue(this.label_AdvancePayments_Title);
		FinanceDetail findetails = getFinancedetailsFromBase();
		financeDisbursement = findetails.getFinScheduleData().getDisbursementDetails();
		disbursementInstCtrl.setFinanceDisbursement(financeDisbursement);
		disbursementInstCtrl.setFinanceMain(findetails.getFinScheduleData().getFinanceMain());
		disbursementInstCtrl.setDocumentDetails(getDisbursmentDoc());
		if (CollectionUtils.isNotEmpty(findetails.getFinScheduleData().getFinFeeDetailList())) {
			disbursementInstCtrl.setFinFeeDetailList(findetails.getFinScheduleData().getFinFeeDetailList());
		}
		if (CollectionUtils.isNotEmpty(getVasRecordingList())) {
			disbursementInstCtrl.setVasRecordingList(getVasRecordingList());
		}
		disbursementInstCtrl.setModuleDefiner(moduleDefiner);
		disbursementInstCtrl.onClickNew(this, this.financeMainDialogCtrl, ModuleType_Loan, getFinAdvancePaymentsList(),
				null, moduleDefiner);

		logger.debug("Leaving" + event.toString());
	}

	public void onFinAdvancePaymentsItemDoubleClicked(Event event)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		logger.debug("Entering" + event.toString());
		Clients.clearWrongValue(this.btnNew_NewFinAdvancePayments);
		FinanceDetail findetails = getFinancedetailsFromBase();
		financeDisbursement = findetails.getFinScheduleData().getDisbursementDetails();
		disbursementInstCtrl.setFinanceDisbursement(financeDisbursement);
		disbursementInstCtrl.setFinanceMain(findetails.getFinScheduleData().getFinanceMain());
		disbursementInstCtrl.setDocumentDetails(getDisbursmentDoc());
		if (CollectionUtils.isNotEmpty(findetails.getFinScheduleData().getFinFeeDetailList())) {
			disbursementInstCtrl.setFinFeeDetailList(findetails.getFinScheduleData().getFinFeeDetailList());
		}
		if (CollectionUtils.isNotEmpty(getVasRecordingList())) {
			disbursementInstCtrl.setVasRecordingList(getVasRecordingList());
		}
		disbursementInstCtrl.setModuleDefiner(moduleDefiner);
		disbursementInstCtrl.onDoubleClick(this, this.financeMainDialogCtrl, ModuleType_Loan, isEnquiry, null);

		logger.debug("Leaving" + event.toString());
	}

	public void doFillFinAdvancePaymentsDetails(List<FinAdvancePayments> finAdvancePayDetails,
			List<VASRecording> vasRecordingList) {
		logger.debug("Entering");
		setFinAdvancePaymentsList(finAdvancePayDetails);
		setVasRecordingList(vasRecordingList);
		/* added empty check skip the below VAS process in LMS */
		if (ImplementationConstants.VAS_INST_ON_DISB && StringUtils.isEmpty(moduleDefiner)) {
			String entityCode = getFinancedetail().getFinScheduleData().getFinanceMain().getEntityCode();
			finAdvancePaymentsService.processVasInstructions(vasRecordingList, getFinAdvancePaymentsList(), entityCode);
		}
		disbursementInstCtrl.doFillFinAdvancePaymentsDetails(getFinAdvancePaymentsList(),
				getUserWorkspace().isAllowed("FinAdvancePaymentsList_NewFinAdvancePaymentsDetail"), vasRecordingList);
		logger.debug("Leaving");
	}

	/**
	 * This method set the Advence Payment Detail to aFinanceDetail
	 * 
	 * @param aFinanceDetail
	 */
	public void doSave_AdvencePaymentDetail(FinanceDetail aFinanceDetail) {
		logger.debug("Entering ");
		if (getFinAdvancePaymentsList() != null && !this.getFinAdvancePaymentsList().isEmpty()) {
			aFinanceDetail.setAdvancePaymentsList(getFinAdvancePaymentsList());
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails() {
		try {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(e);
		}

	}

	private DocumentDetails getDisbursmentDoc()
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		logger.debug("Entering");

		if (financeMainDialogCtrl != null) {
			DocumentDetailDialogCtrl documentDetailDialogCtrl = (DocumentDetailDialogCtrl) financeMainDialogCtrl
					.getClass().getMethod("getDocumentDetailDialogCtrl").invoke(financeMainDialogCtrl);

			String document = SysParamUtil.getValueAsString("DISB_DOC");
			if (documentDetailDialogCtrl != null) {
				for (DocumentDetails details : documentDetailDialogCtrl.getDocumentDetailsList()) {
					if (StringUtils.equalsIgnoreCase(details.getDocCategory(), document)) {
						return details;
					}
				}
			}
		}
		logger.debug("Leaving");
		return null;

	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public List<FinAdvancePayments> getFinAdvancePaymentsList() {
		return finAdvancePaymentsList;
	}

	public void setFinAdvancePaymentsList(List<FinAdvancePayments> finAdvancePaymentsList) {
		this.finAdvancePaymentsList = finAdvancePaymentsList;
	}

	public List<VASRecording> getVasRecordingList() {
		return vasRecordingList;
	}

	public void setVasRecordingList(List<VASRecording> vasRecordingList) {
		this.vasRecordingList = vasRecordingList;
	}

	public boolean isNewFinance() {
		return newFinance;
	}

	public void setNewFinance(boolean newFinance) {
		this.newFinance = newFinance;
	}

	public void setFinancedetail(FinanceDetail financedetail) {
		this.financedetail = financedetail;
	}

	public FinanceDetail getFinancedetail() {
		return financedetail;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public void setDisbursementInstCtrl(DisbursementInstCtrl disbursementInstCtrl) {
		this.disbursementInstCtrl = disbursementInstCtrl;
	}

	public FinAdvancePaymentsService getFinAdvancePaymentsService() {
		return finAdvancePaymentsService;
	}

	@Autowired
	public void setFinAdvancePaymentsService(FinAdvancePaymentsService finAdvancePaymentsService) {
		this.finAdvancePaymentsService = finAdvancePaymentsService;
	}

}
