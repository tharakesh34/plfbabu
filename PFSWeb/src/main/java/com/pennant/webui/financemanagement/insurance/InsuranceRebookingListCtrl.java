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
 * FileName    		:  InsuranceRebookingListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :    														*
 *                                                                  						*
 * Modified Date    :     														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-05-2012       Pennant	                 0.1                                            * 
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

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.service.configuration.VASRecordingService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.financemanagement.insurance.model.InsuranceRebookingListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class InsuranceRebookingListCtrl extends GFCBaseListCtrl<VASRecording> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(InsuranceRebookingListCtrl.class);

	public Window window_InsuranceRebookingList;
	protected Borderlayout borderLayout_InsuranceRebookingList;
	protected Paging pagingInsuranceRebookingList;
	protected Listbox listBoxInsuranceRebookingList;

	// List headers
	protected Listheader listheader_ProductCode;
	protected Listheader listheader_PostingAgainst;
	protected Listheader listheader_PrimaryLinkRef;
	protected Listheader listheader_VasReference;

	protected Button button_InsuranceRebookingList_NewInsuranceRebooking;
	protected Button button_InsuranceRebookingList_InsuranceRebookingSearch;
	protected Button btnRefresh;

	protected Label label_InsuranceRebookingList_RecordStatus;
	protected Label label_InsuranceRebookingList_RecordType;

	protected Textbox productCode;
	protected Listbox sortOperator_ProductCode;

	protected Combobox postingAgainst;
	protected Listbox sortOperator_PostingAgainst;

	protected Textbox primaryLinkRef;
	protected Listbox sortOperator_PrimaryLinkRef;

	protected Textbox vasReference;
	protected Listbox sortOperator_VasReference;

	private transient VASRecordingService vASRecordingService;
	private transient FinanceWorkFlowService financeWorkFlowService;
	private transient WorkFlowDetails workFlowDetails = null;

	private String module = null;
	private boolean rebooking;
	private boolean maintainance;
	private boolean cancellation;

	@Override
	protected void doSetProperties() {
		super.moduleCode = "VASRecording";
		super.pageRightName = "InsuranceRebookingList";
		super.tableName = "VasRecording_View";
		super.queueTableName = "VasRecording_View";
	}

	protected void doAddFilters() {
		super.doAddFilters();
		if (VASConsatnts.STATUS_REBOOKING.equals(this.module)) {
			rebooking = true;
			this.searchObject.addFilterEqual("VasStatus", VASConsatnts.STATUS_REBOOKING);
			Filter[] filters = new Filter[1];
			filters[0] = new Filter("ProductCtg", VASConsatnts.VAS_CATEGORY_VASI, Filter.OP_EQUAL);
			this.searchObject.addFilterAnd(filters);
		} else if (VASConsatnts.STATUS_MAINTAINCE.equals(this.module)) {
			maintainance = true;
			StringBuilder sql = new StringBuilder();
			sql.append("VasStatus = '");
			sql.append(VASConsatnts.STATUS_MAINTAINCE).append("' OR VasStatus = '");
			sql.append(VASConsatnts.STATUS_NORMAL).append("')");
			sql.append(" AND VASReference not in (Select VASReference from VASRecording_Temp Where VasStatus = '");
			sql.append(VASConsatnts.STATUS_NORMAL).append("'");
			Filter[] filters = new Filter[1];
			filters[0] = new Filter("ProductCtg", VASConsatnts.VAS_CATEGORY_VASI, Filter.OP_EQUAL);
			this.searchObject.addFilterAnd(filters);
			this.searchObject.addWhereClause(sql.toString());
		} else if (VASConsatnts.STATUS_CANCEL.equals(this.module)) {
			cancellation = true;
			StringBuilder sql = new StringBuilder();
			sql.append(
					"(( VasStatus = 'C' OR VasStatus = 'S' OR VasStatus = 'N') AND (VASReference not in (Select VASReference from VASRecording_Temp Where VasStatus = 'N')) AND (VASReference not in (Select VASReference from VASRecording  Where VasStatus = 'S' OR  VasStatus = 'C')) AND ( ProductCtg = '");
			sql.append(VASConsatnts.VAS_CATEGORY_VASI).append("'))");
			this.searchObject.addWhereClause(sql.toString());
		}
	}

	public void onCreate$window_InsuranceRebookingList(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Getting moduleName from mainmenu.xml
		this.module = getArgument("module");

		// Set the page level components.
		setPageComponents(window_InsuranceRebookingList, borderLayout_InsuranceRebookingList,
				listBoxInsuranceRebookingList, pagingInsuranceRebookingList);
		setItemRender(new InsuranceRebookingListModelItemRenderer());

		fillComboBox(this.postingAgainst, "", PennantStaticListUtil.getRecAgainstTypes(), "");

		// Register buttons and fields.
		registerButton(button_InsuranceRebookingList_NewInsuranceRebooking,
				"button_InsuranceMaintananceRebookingDialog_btnSave", true);
		registerButton(button_InsuranceRebookingList_InsuranceRebookingSearch);

		registerField("productCode", listheader_ProductCode, SortOrder.ASC, productCode, sortOperator_ProductCode,
				Operators.STRING);
		registerField("postingAgainst", listheader_PostingAgainst, SortOrder.NONE, postingAgainst,
				sortOperator_PostingAgainst, Operators.SIMPLE_NUMARIC);
		registerField("vasReference", listheader_VasReference, SortOrder.NONE, vasReference, sortOperator_VasReference,
				Operators.STRING);
		registerField("primaryLinkRef", listheader_PrimaryLinkRef, SortOrder.NONE, primaryLinkRef,
				sortOperator_PrimaryLinkRef, Operators.STRING);
		registerField("nextRoleCode");
		// Render the page and display the data.
		doRenderPage();
		search();
		if (maintainance || cancellation) {
			this.button_InsuranceRebookingList_NewInsuranceRebooking.setVisible(false);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_InsuranceRebookingList_InsuranceRebookingSearch(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_InsuranceRebookingList_NewInsuranceRebooking(Event event) {
		logger.debug(Literal.ENTERING);
		// Create a new entity.
		VASRecording vASRecording = new VASRecording();
		vASRecording.setNewRecord(true);
		vASRecording.setWorkflowId(getWorkFlowId());
		Map<String, Object> arg = getDefaultArguments();
		arg.put("vASRecording", vASRecording);
		arg.put("listCtrl", this);
		arg.put("role", getUserWorkspace().getUserRoles());
		arg.put("module", module);
		try {
			Executions.createComponents("/WEB-INF/pages/Insurance/SelectInsuranceRebookingDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */

	public void onInsuranceRebookingItemDoubleClicked(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		String servicingEvent = "";
		if (rebooking) {
			servicingEvent = VASConsatnts.VAS_EVENT_REBOOKING;
		} else if (maintainance) {
			servicingEvent = VASConsatnts.VAS_EVENT_MAINTENANCE;
		} else if (cancellation) {
			servicingEvent = VASConsatnts.VAS_EVENT_CANCELLATION;
		}

		// Get the selected record.
		Listitem selectedItem = this.listBoxInsuranceRebookingList.getSelectedItem();

		// Get the selected entity.
		VASRecording vasRecording = (VASRecording) selectedItem.getAttribute("vASRecording");

		// Set Workflow Details
		String userRole = vasRecording.getNextRoleCode();
		if (StringUtils.isEmpty(vasRecording.getRecordType()) && !enqiryModule) {
			setWorkflowDetails(vasRecording.getProductCode(), servicingEvent);
			if (workFlowDetails == null) {
				MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
				return;
			}
			if (StringUtils.isEmpty(userRole)) {
				userRole = workFlowDetails.getFirstTaskOwner();
			}
		}
		VASRecording aVASRecording = getVASRecordingService()
				.getVASRecordingForInsurance(vasRecording.getVasReference(), userRole, servicingEvent, enqiryModule);
		if (aVASRecording == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Role Code State Checking
		String nextroleCode = aVASRecording.getNextRoleCode();
		if (StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)) {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = aVASRecording.getProductCode();
			errParm[0] = PennantJavaUtil.getLabel("label_ProductCode") + ":" + valueParm[0];

			ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
					getUserWorkspace().getUserLanguage());
			MessageUtil.showError(errorDetails.getError());

			Events.sendEvent(Events.ON_CLICK, this.btnRefresh, null);
			logger.debug(Literal.LEAVING);
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " where ProductCode=?";

		if (doCheckAuthority(aVASRecording, whereCond, new Object[] { aVASRecording.getProductCode() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && aVASRecording.getWorkflowId() == 0) {
				aVASRecording.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(aVASRecording);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Fetching Dynamic Workflow Details
	 * 
	 * @param productCode
	 */
	private void setWorkflowDetails(String productCode, String servicingEvent) {

		// Setting Workflow Details
		FinanceWorkFlow financeWorkFlow = getFinanceWorkFlowService().getApprovedFinanceWorkFlowById(productCode,
				servicingEvent, PennantConstants.WORFLOW_MODULE_VAS);

		// Workflow Details Setup
		if (financeWorkFlow != null) {
			workFlowDetails = WorkFlowUtil.getDetailsByType(financeWorkFlow.getWorkFlowType());
		}
		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
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
		}
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param country
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(VASRecording vASRecording) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("vASRecording", vASRecording);
		arg.put("listCtrl", this);
		arg.put("module", module);
		try {
			if (VASConsatnts.STATUS_CANCEL.equals(this.module)) {
				Executions.createComponents("/WEB-INF/pages/Insurance/InsuranceSurrenderDialog.zul", null, arg);
			} else {
				Executions.createComponents("/WEB-INF/pages/Insurance/InsuranceRebookingDialog.zul", null, arg);
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	public void setVASRecordingService(VASRecordingService vASRecordingService) {
		this.vASRecordingService = vASRecordingService;
	}

	public VASRecordingService getVASRecordingService() {
		return this.vASRecordingService;
	}

	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return financeWorkFlowService;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}
}