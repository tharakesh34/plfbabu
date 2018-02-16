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
 * FileName    		:  VASRecordingListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-12-2016    														*
 *                                                                  						*
 * Modified Date    :  02-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-12-2016       PENNANT	                 0.1                                            * 
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

package com.pennant.webui.configuration.vasrecording;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
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
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.service.configuration.VASRecordingService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.configuration.vasrecording.model.VASRecordingListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Configuration/VASConfiguration/VASConfigurationList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class VASRecordingListCtrl extends GFCBaseListCtrl<VASRecording> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(VASRecordingListCtrl.class);

	protected Window window_VASRecordingList;
	protected Borderlayout borderLayout_VASRecordingList;
	protected Paging pagingVASRecordingList;
	protected Listbox listBoxVASRecording;

	// List headers
	protected Listheader listheader_ProductCode;
	protected Listheader listheader_PostingAgainst;
	protected Listheader listheader_PrimaryLinkRef;
	protected Listheader listheader_VasReference;
	protected Listheader listheader_Fee;
	protected Listheader listheader_FeePaymentMode;
	protected Listheader listheader_ValueDate;
	protected Listheader listheader_AccrualTillDate;
	protected Listheader listheader_RecurringDate;
	protected Listheader listheader_DsaId;
	protected Listheader listheader_DmaId;
	protected Listheader listheader_FulfilOfficerId;
	protected Listheader listheader_ReferralId;
	protected Listheader listheader_VasStatus;
	
	// checkRights
	protected Button 	btnHelp;
	protected Button    button_VASRecordingList_NewVASRecording;
	protected Button 	button_VASRecordingList_VASRecordingSearch;
	protected Button 	button_VASRecordingList_PrintList;
	protected Button	btnRefresh;
	protected Label 	label_VASRecordingList_RecordStatus;
	protected Label 	label_VASRecordingList_RecordType;

 	protected Textbox 	productCode;
	protected Listbox 	sortOperator_ProductCode;

	protected Combobox 	postingAgainst;
	protected Listbox 	sortOperator_PostingAgainst;

	protected Textbox 	primaryLinkRef;
	protected Listbox 	sortOperator_PrimaryLinkRef;

	protected Textbox 	vasReference;
	protected Listbox 	sortOperator_VasReference;

	protected Decimalbox 	fee;
	protected Listbox 		sortOperator_Fee;

	protected Combobox 	feePaymentMode;
	protected Listbox 	sortOperator_FeePaymentMode;

	protected Datebox 	valueDate;
	protected Listbox 	sortOperator_ValueDate;

	protected Datebox accrualTillDate;
	protected Listbox sortOperator_AccrualTillDate;

	protected Datebox recurringDate;
	protected Listbox sortOperator_RecurringDate;

	protected Textbox dsaId;
	protected Listbox sortOperator_DsaId;

	protected Textbox dmaId;
	protected Listbox sortOperator_DmaId;

	protected Textbox fulfilOfficerId;
	protected Listbox sortOperator_FulfilOfficerId;

	protected Textbox referralId;
	protected Listbox sortOperator_ReferralId;

	protected Textbox moduleType;
	protected JdbcSearchObject<Customer> custCIFSearchObject;
	
	private transient VASRecordingService 	vASRecordingService;
	private transient FinanceWorkFlowService financeWorkFlowService;
	private transient WorkFlowDetails workFlowDetails  =  null;
	
	private String module = null;
	/**
	 * default constructor.<br>
	 */
	public VASRecordingListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "VASRecording";
		super.pageRightName = "VASRecordingList";
		super.tableName = "VasRecording_AView";
		super.queueTableName = "VasRecording_View";
	}

	protected void doAddFilters() {
		super.doAddFilters();
		if ("C".equals(module)) {
			this.searchObject.addWhereClause("(RecordType IS NULL AND VasStatus != 'C') OR (VasStatus = 'C' AND RecordType IS NOT NULL)");
		} else if ("N".equals(module)) {
			Filter[] filters = new Filter[2];
			filters[0] = new Filter("RecordType", PennantConstants.RECORD_TYPE_NEW, Filter.OP_EQUAL);
			filters[1] = new Filter("FinanceProcess", 0, Filter.OP_EQUAL);
			this.searchObject.addFilterAnd(filters);
		}
	}
	
	public void onCreate$window_VASRecordingList(Event event) throws Exception {
		logger.debug("Entering");

		//Getting moduleName from mainmenu.xml
		module = getArgument("module");
		
		// Set the page level components.
		setPageComponents(window_VASRecordingList, borderLayout_VASRecordingList, listBoxVASRecording,
				pagingVASRecordingList);
		setItemRender(new VASRecordingListModelItemRenderer());

		fillComboBox(this.postingAgainst, "", PennantStaticListUtil.getRecAgainstTypes(), "");
		fillComboBox(this.feePaymentMode, "", PennantStaticListUtil.getFeeTypes(), "");

		// Register buttons and fields.
		registerButton(button_VASRecordingList_NewVASRecording, "button_VASRecordingList_NewVASRecording", true);
		registerButton(button_VASRecordingList_VASRecordingSearch);

		registerField("productCode", listheader_ProductCode, SortOrder.ASC, productCode, sortOperator_ProductCode,
				Operators.STRING);
		registerField("postingAgainst", listheader_PostingAgainst, SortOrder.NONE, postingAgainst,
				sortOperator_PostingAgainst, Operators.SIMPLE_NUMARIC);
		registerField("primaryLinkRef", listheader_PrimaryLinkRef, SortOrder.NONE, primaryLinkRef,
				sortOperator_PrimaryLinkRef, Operators.STRING);
		registerField("vasReference", listheader_VasReference, SortOrder.NONE, vasReference, sortOperator_VasReference,
				Operators.STRING);
		registerField("feePaymentMode", listheader_FeePaymentMode, SortOrder.NONE, feePaymentMode,
				sortOperator_FeePaymentMode, Operators.SIMPLE_NUMARIC);
		registerField("nextRoleCode");
		registerField("VasStatus");
		// Render the page and display the data.
		doRenderPage();
		search();

		if ("C".equals(module) || "E".equals(module)) {
			this.button_VASRecordingList_NewVASRecording.setVisible(false);
		}
		if ("E".equals(module)) {
			this.listheader_VasStatus.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_VASRecordingList_VASRecordingSearch(Event event) {
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
	public void onClick$button_VASRecordingList_NewVASRecording(Event event) {

		logger.debug("Entering");
		// Create a new entity.
		VASRecording vASRecording = new VASRecording();
		vASRecording.setNewRecord(true);
		vASRecording.setWorkflowId(getWorkFlowId());

		Map<String, Object> arg = getDefaultArguments();
		arg.put("vASRecording", vASRecording);
		arg.put("vASRecordingListCtrl", this);
		arg.put("role", getUserWorkspace().getUserRoles());

		try {
			Executions.createComponents("/WEB-INF/pages/VASRecording/SelectVASConfigurationDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */

	public void onVASRecordingItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxVASRecording.getSelectedItem();

		// Get the selected entity.
		VASRecording vasRecording = (VASRecording) selectedItem.getAttribute("vasRecording");

		// Set Workflow Details
		String userRole = vasRecording.getNextRoleCode();
		if (StringUtils.isEmpty(vasRecording.getRecordType()) && !enqiryModule) {
			setWorkflowDetails(vasRecording.getProductCode());
			if (workFlowDetails == null) {
				MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
				return;
			}
			if (StringUtils.isEmpty(userRole)) {
				userRole = workFlowDetails.getFirstTaskOwner();
			}
		}
		
		VASRecording aVASRecording = getVASRecordingService().getVASRecordingByRef(vasRecording.getVasReference(), userRole, enqiryModule);
		if (aVASRecording == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		//Role Code State Checking
		String nextroleCode = aVASRecording.getNextRoleCode();
		if (StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)) {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = aVASRecording.getProductCode();
			errParm[0] = PennantJavaUtil.getLabel("label_ProductCode") + ":" + valueParm[0];

			ErrorDetail errorDetails = ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005",
					errParm, valueParm), getUserWorkspace().getUserLanguage());
			MessageUtil.showError(errorDetails.getError());

			Events.sendEvent(Events.ON_CLICK, this.btnRefresh, null);
			logger.debug("Leaving");
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND ProductCode='" + aVASRecording.getProductCode() + "' AND version="
				+ aVASRecording.getVersion() + " ";

		if (doCheckAuthority(aVASRecording, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && aVASRecording.getWorkflowId() == 0) {
				aVASRecording.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(aVASRecording);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}
		logger.debug("Leaving");
	}

	
	/**
	 * Method for Fetching Dynamic Workflow Details
	 * @param productCode
	 */
	private void setWorkflowDetails(String productCode){

		// Setting Workflow Details
		FinanceWorkFlow financeWorkFlow = getFinanceWorkFlowService().getApprovedFinanceWorkFlowById(
				productCode, FinanceConstants.FINSER_EVENT_ORG, VASConsatnts.MODULE_NAME);

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
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("vASRecording", vASRecording);
		arg.put("vASRecordingListCtrl", this);
		arg.put("module", module);

		try {
			Executions.createComponents("/WEB-INF/pages/VASRecording/VASRecordingDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
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