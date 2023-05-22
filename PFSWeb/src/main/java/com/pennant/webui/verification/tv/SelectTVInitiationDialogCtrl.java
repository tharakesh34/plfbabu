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
 * * FileName : SelectCollateralTypeDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 11-03-2019 *
 * * Modified Date : 11-03-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 11-03-2019 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.verification.tv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.verification.fieldinvestigation.FIInitiationListCtrl;
import com.pennant.webui.verification.legalverification.LVInitiationListCtrl;
import com.pennant.webui.verification.legalvetting.LegalVettingInitiationListCtrl;
import com.pennant.webui.verification.rcu.RCUInitiationListCtrl;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine.Flow;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.service.LegalVerificationService;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.web.util.ComponentUtil;

public class SelectTVInitiationDialogCtrl extends GFCBaseCtrl<CollateralSetup> {
	private static final Logger logger = LogManager.getLogger(SelectTVInitiationDialogCtrl.class);
	private static final long serialVersionUID = 1L;

	protected Window window_SelectTechnicalVerificationInitiationDialog;
	protected Button btnProceed;
	private ExtendedCombobox finReference;

	private TVInitiationListCtrl tvInitiationListCtrl;
	private FIInitiationListCtrl fiInitiationListCtrl;
	private LVInitiationListCtrl lvInitiationListCtrl;
	private RCUInitiationListCtrl rcuInitiationListCtrl;
	private LegalVettingInitiationListCtrl legalVettingInitiationListCtrl;
	private FinanceDetail financeDetail;

	private transient FinanceDetailService financeDetailService;
	private transient FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private transient LegalVerificationService legalVerificationService;

	private String module = null;

	public SelectTVInitiationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	public void onCreate$window_SelectTechnicalVerificationInitiationDialog(Event event) {
		logger.debug(Literal.ENTERING);

		try {
			setPageComponents(window_SelectTechnicalVerificationInitiationDialog);

			// Get the required arguments.
			if (arguments.containsKey("tvInitiationListCtrl")) {
				this.tvInitiationListCtrl = (TVInitiationListCtrl) arguments.get("tvInitiationListCtrl");
			} else if (arguments.containsKey("fiInitiationListCtrl")) {
				this.fiInitiationListCtrl = (FIInitiationListCtrl) arguments.get("fiInitiationListCtrl");
			} else if (arguments.containsKey("lvInitiationListCtrl")) {
				this.lvInitiationListCtrl = (LVInitiationListCtrl) arguments.get("lvInitiationListCtrl");
			} else if (arguments.containsKey("rcuInitiationListCtrl")) {
				this.rcuInitiationListCtrl = (RCUInitiationListCtrl) arguments.get("rcuInitiationListCtrl");
			} else if (arguments.containsKey("legalVettingInitiationListCtrl")) {
				this.legalVettingInitiationListCtrl = (LegalVettingInitiationListCtrl) arguments
						.get("legalVettingInitiationListCtrl");
			}

			if (arguments.containsKey("roleCode")) {
				setRole((String) arguments.get("roleCode"));
			}

			if (arguments.containsKey("window")) {
				this.window = (Window) arguments.get("window");
			}

			if (arguments.containsKey("module")) {
				this.module = (String) arguments.get("module");
			}

			doSetFieldProperties();
			showDialog();

		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Opens the SelectFinanceTypeDialog window modal.
	 */
	private void showDialog() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		try {
			this.window_SelectTechnicalVerificationInitiationDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.finReference.getTextbox().setMaxlength(20);
		this.finReference.setDisplayStyle(2);
		this.finReference.setMandatoryStyle(true);
		this.finReference.setModuleName("FinanceMain_Temp");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setDescColumn("FinType");
		this.finReference.setValidateColumns(new String[] { "FinReference" });

		Filter[] filter = new Filter[1];
		filter[0] = new Filter("FINISACTIVE", 1, Filter.OP_EQUAL);
		this.finReference.setFilters(filter);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * When user clicks on button "btnProceed" button
	 * 
	 * @param event
	 */
	public void onClick$btnProceed(Event event) {
		logger.debug("Entering " + event.toString());

		if (!doFieldValidation()) {
			return;
		}
		String initTab = null;
		String apprTab = null;
		if (StringUtils.equals(VerificationType.FI.getValue(), module)) {
			initTab = FinanceConstants.PROCEDT_VERIFICATION_FI_INIT;
			apprTab = FinanceConstants.PROCEDT_VERIFICATION_FI_APPR;
		} else if (StringUtils.equals(VerificationType.TV.getValue(), module)) {
			initTab = FinanceConstants.PROCEDT_VERIFICATION_TV_INIT;
			apprTab = FinanceConstants.PROCEDT_VERIFICATION_TV_APPR;
		} else if (StringUtils.equals(VerificationType.LV.getValue(), module)) {
			initTab = FinanceConstants.PROCEDT_VERIFICATION_LV_INIT;
			apprTab = FinanceConstants.PROCEDT_VERIFICATION_LV_APPR;
		} else if (StringUtils.equals(VerificationType.RCU.getValue(), module)) {
			initTab = FinanceConstants.PROCEDT_VERIFICATION_RCU_INIT;
			apprTab = FinanceConstants.PROCEDT_VERIFICATION_RCU_APPR;
		} else if (StringUtils.equals(VerificationType.VETTING.getValue(), module)) {
			initTab = FinanceConstants.PROCEDT_VERIFICATION_LVETTING_INIT;
			apprTab = FinanceConstants.PROCEDT_VERIFICATION_LVETTING_APPR;
		}

		Object dataObject = this.finReference.getObject();
		FinanceMain financeMain = (FinanceMain) dataObject;

		if (initTab == FinanceConstants.PROCEDT_VERIFICATION_LVETTING_INIT) {
			if (!legalVerificationService.isLVVerificationExists(financeMain.getFinReference())) {
				MessageUtil.showMessage(Labels.getLabel("label_legalverification_errmsg"));
				return;
			}

		}

		List<FinanceReferenceDetail> financeReferenceDetails = getFinanceReferenceDetailDAO()
				.getFinanceProcessEditorDetails(financeMain.getFinType(), FinServiceEvent.ORG,
						FinanceConstants.PROCEDT_LIMIT, "_FINVIEW");

		boolean iniitiation = false;
		boolean approval = false;
		boolean predessor = false;
		StringBuilder message = new StringBuilder();
		String currentStage = "";
		if (StringUtils.containsIgnoreCase(financeMain.getRecordStatus(), "Resubmit")
				|| StringUtils.containsIgnoreCase(financeMain.getRecordStatus(), "Submit")) {
			currentStage = financeMain.getNextRoleCode();

		} else {
			currentStage = financeMain.getRoleCode();
		}

		String approvalStage = "";
		String initiationStage = "";

		if (CollectionUtils.isNotEmpty(financeReferenceDetails)) {
			for (FinanceReferenceDetail referenceDetail : financeReferenceDetails) {

				String reference = referenceDetail.getLovDescRefDesc();

				if ((!referenceDetail.isIsActive()) || StringUtils.isEmpty(reference)) {
					continue;
				}

				if (StringUtils.equals(initTab, reference)) {
					iniitiation = true;
					initiationStage = StringUtils.trimToEmpty(referenceDetail.getMandInputInStage());
					initiationStage = StringUtils.remove(initiationStage, ",");
				}

				if (StringUtils.equals(apprTab, reference)) {
					approval = true;

					approvalStage = StringUtils.trimToEmpty(referenceDetail.getMandInputInStage());
					approvalStage = StringUtils.remove(approvalStage, ",");
					if (financeMain.getWorkflowId() != 0) {

						WorkFlowDetails flowDetails = WorkFlowUtil.getWorkflow(financeMain.getWorkflowId());
						WorkflowEngine engine = new WorkflowEngine(flowDetails.getWorkFlowXml());

						if (engine.compareRoles(currentStage, approvalStage) == Flow.PREDECESSOR) {
							predessor = true;
						}
					}
				}
			}

		}

		if (!iniitiation) {
			message.append(
					module.concat(" Initiation tab not configured for the loan type :") + financeMain.getFinType());
		} else if (!approval) {
			message.append(
					module.concat(" Approval tab not configured for the loan type :") + financeMain.getFinType());
		} else if (predessor) {
			message.append("The loan already crosseed the ".concat(module).concat(" Approval stage.").concat(module)
					.concat(" Initiation stage: ") + initiationStage + ",".concat(module).concat(" Approval stage: ")
					+ approvalStage + ", Current loan stage: " + currentStage);
		}

		if (message.length() > 0) {
			MessageUtil.showError(message.toString());
			return;
		}

		showDetailView();
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void showDetailView() {
		logger.debug(Literal.ENTERING);

		Map<String, Object> map = new HashMap<>();

		map.put("InitType", true);
		map.put("userRole", getRole());
		map.put("moduleDefiner", "");

		Long finID = ComponentUtil.getFinID(finReference);

		if (StringUtils.equals(VerificationType.FI.getValue(), module)) {
			setFinanceDetail(
					financeDetailService.getVerificationInitiationDetails(finID, VerificationType.FI, "_View"));
			map = getDefaultArguments(map);
			map.put("verification", getFinanceDetail().getFiVerification());
			map.put("financeDetail", getFinanceDetail());
			map.put("finHeaderList", getFinBasicDetails());
			map.put("fiInitiationListCtrl", fiInitiationListCtrl);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/FIInitiation.zul", window,
					map);
		} else if (StringUtils.equals(VerificationType.TV.getValue(), module)) {
			setFinanceDetail(
					financeDetailService.getVerificationInitiationDetails(finID, VerificationType.TV, "_View"));
			map = getDefaultArguments(map);
			map.put("verification", getFinanceDetail().getTvVerification());
			map.put("financeDetail", getFinanceDetail());
			map.put("finHeaderList", getFinBasicDetails());
			map.put("tvInitiationListCtrl", tvInitiationListCtrl);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/TVInitiation.zul", window,
					map);
		} else if (StringUtils.equals(VerificationType.LV.getValue(), module)) {
			setFinanceDetail(
					financeDetailService.getVerificationInitiationDetails(finID, VerificationType.LV, "_View"));
			map = getDefaultArguments(map);
			map.put("verification", getFinanceDetail().getLvVerification());
			map.put("financeDetail", getFinanceDetail());
			map.put("finHeaderList", getFinBasicDetails());
			map.put("lvInitiationListCtrl", lvInitiationListCtrl);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/LVInitiation.zul", window,
					map);
		} else if (StringUtils.equals(VerificationType.RCU.getValue(), module)) {
			setFinanceDetail(
					financeDetailService.getVerificationInitiationDetails(finID, VerificationType.RCU, "_View"));
			map = getDefaultArguments(map);
			map.put("verification", getFinanceDetail().getRcuVerification());
			map.put("financeDetail", getFinanceDetail());
			map.put("rcuInitiationListCtrl", rcuInitiationListCtrl);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/RCUInitiation.zul", window,
					map);
		} else if (StringUtils.equals(VerificationType.VETTING.getValue(), module)) {
			setFinanceDetail(
					financeDetailService.getVerificationInitiationDetails(finID, VerificationType.VETTING, "_View"));
			map = getDefaultArguments(map);
			map.put("verification", getFinanceDetail().getLegalVetting());
			map.put("financeDetail", getFinanceDetail());
			map.put("finHeaderList", getFinBasicDetails());
			map.put("legalVettingInitiationListCtrl", legalVettingInitiationListCtrl);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/LegalVettingInitiation.zul",
					window, map);
		}
		this.window_SelectTechnicalVerificationInitiationDialog.onClose();

		logger.debug(Literal.LEAVING);
	}

	public Map<String, Object> getDefaultArguments(Map<String, Object> map) {

		map.put("roleCode", getRole());
		map.put("financeMainDialogCtrl", this);
		map.put("finHeaderList", getFinBasicDetails());
		map.put("financeDetail", getFinanceDetail());
		map.put("isFinanceProcess", false);
		map.put("ccyFormatter",
				CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy()));
		return map;
	}

	/**
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	private ArrayList<Object> getFinBasicDetails() {
		ArrayList<Object> arrayList = new ArrayList<Object>();
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		arrayList.add(0, financeMain.getFinType());
		arrayList.add(1, financeMain.getFinCcy());
		arrayList.add(2, "");
		arrayList.add(3, this.finReference.getValue());
		arrayList.add(4, "");
		arrayList.add(5, financeMain.getGrcPeriodEndDate());
		arrayList.add(6, financeMain.isAllowGrcCpz());
		if (StringUtils.isNotEmpty(getFinanceDetail().getFinScheduleData().getFinanceType().getProduct())) {
			arrayList.add(7, true);
		} else {
			arrayList.add(7, false);
		}
		arrayList.add(8, getFinanceDetail().getFinScheduleData().getFinanceType().getFinCategory());
		String custShrtName = "";
		if (getFinanceDetail().getCustomerDetails() != null
				&& getFinanceDetail().getCustomerDetails().getCustomer() != null) {
			custShrtName = getFinanceDetail().getCustomerDetails().getCustomer().getCustShrtName();
		}

		arrayList.add(9, custShrtName);
		arrayList.add(10, financeMain.isNewRecord());
		arrayList.add(11, "");
		/* arrayList.add(12, financeMain.getFlexiType()); */
		return arrayList;
	}

	/**
	 * Method for checking /validating fields before proceed.
	 * 
	 * @return
	 */
	private boolean doFieldValidation() {
		doClearMessage();
		doRemoveValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (StringUtils.trimToNull(this.finReference.getValue()) == null) {
				throw new WrongValueException(this.finReference, Labels.getLabel("CHECK_NO_EMPTY", new String[] {
						Labels.getLabel("label_SelectTechnicalVerificationInitiationDialog_FinReference.value") }));
			}
		} catch (WrongValueException e) {
			wve.add(e);
		}

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		return true;
	}

	/**
	 * When user clicks on button "finReference" button
	 * 
	 * @param event
	 */
	public void onFulfill$finReference(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		this.finReference.setConstraint("");
		this.finReference.clearErrorMessage();
		Clients.clearWrongValue(finReference);
		Object dataObject = this.finReference.getObject();
		if (dataObject instanceof String) {
			this.finReference.setValue(dataObject.toString());
			this.finReference.setDescription("");
		} else {

		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		this.finReference.setConstraint("");
		logger.debug(Literal.LEAVING);
	}

	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);
		this.finReference.setErrorMessage("");
		logger.debug(Literal.LEAVING);
	}

	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return financeWorkFlowService;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public FinanceReferenceDetailDAO getFinanceReferenceDetailDAO() {
		return financeReferenceDetailDAO;
	}

	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}

	public LegalVerificationService getLegalVerificationService() {
		return legalVerificationService;
	}

	public void setLegalVerificationService(LegalVerificationService legalVerificationService) {
		this.legalVerificationService = legalVerificationService;
	}
}
