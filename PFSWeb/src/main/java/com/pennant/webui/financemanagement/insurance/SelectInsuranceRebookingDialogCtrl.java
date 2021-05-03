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
 * FileName    		:  SelectInsuranceRebookingDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :     																	*
 *                                                                  						*
 *  																						*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *10-01-2017         Pennant	                 0.1                                            * 
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
package com.pennant.webui.financemanagement.insurance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.service.configuration.VASRecordingService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class SelectInsuranceRebookingDialogCtrl extends GFCBaseCtrl<VASRecording> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(SelectInsuranceRebookingDialogCtrl.class);

	public Window window_SelectInsuranceRebooking;

	protected ExtendedCombobox vasRefernce;
	protected ExtendedCombobox primaryLinkRefernce;
	protected Button btnProceed;

	private VASRecording vasRecording;
	private VASRecordingService vASRecordingService;

	private InsuranceRebookingListCtrl insuranceRebookingListCtrl;
	private FinanceWorkFlowService financeWorkFlowService;

	private ArrayList<String> userRoleCodeList;
	private FinanceWorkFlow financeWorkFlow;
	private String module = null;

	public SelectInsuranceRebookingDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	@SuppressWarnings("unchecked")
	public void onCreate$window_SelectInsuranceRebooking(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		try {
			// Get the required arguments.
			this.vasRecording = (VASRecording) arguments.get("vASRecording");
			if (arguments.containsKey("listCtrl")) {
				this.insuranceRebookingListCtrl = (InsuranceRebookingListCtrl) arguments.get("listCtrl");
			}

			if (arguments.containsKey("module")) {
				this.module = (String) arguments.get("module");
			}

			this.userRoleCodeList = (ArrayList<String>) arguments.get("role");

			if (this.vasRecording == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}
			doSetFieldProperties();
		} catch (Exception e) {
			closeDialog();
			logger.debug(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}
		setPageComponents(window_SelectInsuranceRebooking);
		showDialog();
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Opens the showSelectInsuranceRebookingDialog window modal.
	 */
	private void showDialog() throws InterruptedException {
		logger.debug(Literal.ENTERING);
		try {
			this.window_SelectInsuranceRebooking.doModal();
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		StringBuilder whereClause = getWhereClause();

		this.vasRefernce.setMandatoryStyle(true);
		this.vasRefernce.setModuleName("VASRebooking");
		this.vasRefernce.setValueColumn("VasReference");
		this.vasRefernce.setDescColumn("ProductCode");
		this.vasRefernce.setValidateColumns(new String[] { "VASReference" });

		Filter[] filters = new Filter[1];
		filters[0] = new Filter("VASSTATUS", VASConsatnts.STATUS_CANCEL, Filter.OP_EQUAL);
		this.vasRefernce.setFilters(filters);
		this.vasRefernce.setWhereClause(whereClause.toString());

		this.primaryLinkRefernce.setMandatoryStyle(true);
		this.primaryLinkRefernce.setModuleName("FinVASRebooking");
		this.primaryLinkRefernce.setValueColumn("PrimaryLinkRef");
		this.primaryLinkRefernce.setDescColumn("ProductCode");
		this.primaryLinkRefernce.setValidateColumns(new String[] { "PrimaryLinkRef" });

		Filter[] filter = new Filter[1];
		filter[0] = new Filter("VASSTATUS", VASConsatnts.STATUS_CANCEL, Filter.OP_EQUAL);
		this.primaryLinkRefernce.setFilters(filter);
		this.primaryLinkRefernce.setWhereClause(whereClause.toString());

		logger.debug(Literal.LEAVING);
	}

	private StringBuilder getWhereClause() {
		StringBuilder sql = new StringBuilder();
		sql.append(" VASReference not in (Select VASReference from VASRecording_Temp )");
		sql.append(" AND ( STATUS = '");
		sql.append(InsuranceConstants.ACTIVE);
		sql.append("') ");
		sql.append(
				" AND (VASReference not in (Select OldVasReference from VASRecording_Temp where OldVasReference is not null ))");
		sql.append(" AND ( ProductCode");
		sql.append(" in ( Select FINTYPE from LMTFinanceWorkFlowDef_AView  Where MODULENAME = '");
		sql.append(VASConsatnts.MODULE_NAME);
		sql.append("'");
		sql.append(" AND FINEVENT= '");
		sql.append(VASConsatnts.VAS_EVENT_REBOOKING);
		sql.append("' AND (");
		sql.append(getWhereClauseWithFirstTask()).append(")))");
		return sql;
	}

	private String getWhereClauseWithFirstTask() {
		StringBuilder whereClause = new StringBuilder();
		if (userRoleCodeList != null && !userRoleCodeList.isEmpty()) {
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

	public void onFulfill$vasRefernce(Event event) throws InterruptedException {

		Object dataObject = this.vasRefernce.getObject();
		if (dataObject instanceof String) {
			this.vasRefernce.setValue(dataObject.toString());
			this.vasRefernce.setDescColumn("");
		} else {
			if (dataObject instanceof VASRecording) {
				VASRecording details = (VASRecording) dataObject;
				this.primaryLinkRefernce.setValue(details.getPrimaryLinkRef());
				this.primaryLinkRefernce.setDescription(details.getProductCode());
				this.vasRefernce.setValue(details.getVasReference());
				this.vasRefernce.setDescription(details.getProductCode());
			}
		}
		if (dataObject == null) {
			this.primaryLinkRefernce.setValue("");
			this.primaryLinkRefernce.setDescription("");
			this.vasRefernce.setValue("");
			this.vasRefernce.setDescription("");
		}
	}

	public void onFulfill$primaryLinkRefernce(Event event) throws InterruptedException {

		Object dataObject = this.primaryLinkRefernce.getObject();
		if (dataObject instanceof String) {
			this.primaryLinkRefernce.setValue(dataObject.toString());
			this.primaryLinkRefernce.setDescription("");
		} else {

			if (dataObject instanceof VASRecording) {
				VASRecording details = (VASRecording) dataObject;
				this.vasRefernce.setValue(details.getVasReference());
				this.vasRefernce.setDescription(details.getProductCode());
				this.primaryLinkRefernce.setValue(details.getPrimaryLinkRef());
				this.primaryLinkRefernce.setDescription(details.getProductCode());
			}
		}
		if (dataObject == null) {
			this.primaryLinkRefernce.setValue("");
			this.primaryLinkRefernce.setDescColumn("");
			this.vasRefernce.setValue("");
			this.vasRefernce.setDescColumn("");
		}
	}

	/**
	 * When user clicks on button "btnProceed" button
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnProceed(Event event) throws Exception {
		logger.debug(Literal.ENTERING + event.toString());

		if (!doFieldValidation()) {
			return;
		}

		String vasEvent = null;
		if (VASConsatnts.STATUS_REBOOKING.equals(module)) {
			vasEvent = VASConsatnts.VAS_EVENT_REBOOKING;
		} else if (VASConsatnts.STATUS_MAINTAINCE.equals(module)) {
			vasEvent = VASConsatnts.VAS_EVENT_MAINTENANCE;
		}
		//Basic details setting
		vasRecording.setVasReference(this.vasRefernce.getValue());
		vasRecording.setPrimaryLinkRef(this.primaryLinkRefernce.getValue());

		// Setting Workflow Details
		if (getFinanceWorkFlow() == null) {
			FinanceWorkFlow financeWorkFlow = getFinanceWorkFlowService().getApprovedFinanceWorkFlowById(
					this.vasRefernce.setDescription(), vasEvent, PennantConstants.WORFLOW_MODULE_VAS);
			setFinanceWorkFlow(financeWorkFlow);
		}
		// Workflow Details Setup
		WorkFlowDetails workFlowDetails = null;
		if (getFinanceWorkFlow() != null) {
			workFlowDetails = WorkFlowUtil.getDetailsByType(getFinanceWorkFlow().getWorkFlowType());
		}
		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
			vasRecording.setWorkflowId(0);
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
			vasRecording.setWorkflowId(workFlowDetails.getWorkFlowId());
			doLoadWorkFlow(vasRecording.isWorkflow(), vasRecording.getWorkflowId(), vasRecording.getNextTaskId());
		}
		vasRecording = getvASRecordingService().getVASRecordingForInsurance(vasRecording.getVasReference(), getRole(),
				vasEvent, enqiryModule);
		vasRecording.setWorkflowId(workFlowDetails.getWorkFlowId());
		vasRecording.setNewRecord(true);
		showDetailView();
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void showDetailView() {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arguments = new HashMap<String, Object>();
		arguments.put("vASRecording", this.vasRecording);
		arguments.put("listCtrl", this.insuranceRebookingListCtrl);
		if (CollectionUtils.isNotEmpty(userRoleCodeList)) {
			arguments.put("roleCode", userRoleCodeList.get(0));
		}
		arguments.put("module", this.module);
		try {
			Executions.createComponents("/WEB-INF/pages/Insurance/InsuranceRebookingDialog.zul",
					this.insuranceRebookingListCtrl.window_InsuranceRebookingList, arguments);

			this.window_SelectInsuranceRebooking.onClose();
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
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
			if (StringUtils.trimToNull(this.vasRefernce.getValue()) == null) {
				throw new WrongValueException(this.vasRefernce, Labels.getLabel("CHECK_NO_EMPTY",
						new String[] { Labels.getLabel("label_SelectInsuranceRebooking_insuranceRefernce.value") }));
			}

		} catch (WrongValueException e) {
			wve.add(e);
		}
		try {
			if (StringUtils.trimToNull(this.primaryLinkRefernce.getValue()) == null) {
				throw new WrongValueException(this.primaryLinkRefernce, Labels.getLabel("CHECK_NO_EMPTY",
						new String[] { Labels.getLabel("label_SelectInsuranceRebooking_loanRefernce.value") }));
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

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.vasRefernce.setConstraint("");
		this.primaryLinkRefernce.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.vasRefernce.setErrorMessage("");
		this.primaryLinkRefernce.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	public FinanceWorkFlow getFinanceWorkFlow() {
		return financeWorkFlow;
	}

	public void setFinanceWorkFlow(FinanceWorkFlow financeWorkFlow) {
		this.financeWorkFlow = financeWorkFlow;
	}

	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return financeWorkFlowService;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	public VASRecording getvASRecording() {
		return vasRecording;
	}

	public void setvASRecording(VASRecording vASRecording) {
		this.vasRecording = vASRecording;
	}

	public InsuranceRebookingListCtrl getInsuranceRebookingListCtrl() {
		return insuranceRebookingListCtrl;
	}

	public void setInsuranceRebookingListCtrl(InsuranceRebookingListCtrl InsuranceRebookingListCtrl) {
		this.insuranceRebookingListCtrl = InsuranceRebookingListCtrl;
	}

	public VASRecordingService getvASRecordingService() {
		return vASRecordingService;
	}

	public void setvASRecordingService(VASRecordingService vASRecordingService) {
		this.vASRecordingService = vASRecordingService;
	}

}
