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
 * FileName    		:  JVPostingListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-06-2013    														*
 *                                                                  						*
 * Modified Date    :  21-06-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-06-2013       Pennant	                 0.1                                            * 
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
package com.pennant.webui.others.jvposting;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.backend.service.others.JVPostingService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.webui.others.jvposting.model.JVPostingListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.PTListReportUtils;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/others/JVPosting/JVPostingList.zul file.
 */
public class JVPostingListCtrl extends GFCBaseListCtrl<JVPosting> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(JVPostingListCtrl.class);

	protected Window window_JVPostingList;
	protected Borderlayout borderLayout_JVPostingList;
	protected Paging pagingJVPostingList;
	protected Listbox listBoxJVPosting;
	protected Timer timer;

	protected Listheader listheader_BatchReference;
	protected Listheader listheader_Batch;
	protected Listheader listheader_DebitCount;
	protected Listheader listheader_CreditsCount;
	protected Listheader listheader_TotDebitsByBatchCcy;
	protected Listheader listheader_TotCreditsByBatchCcy;

	protected Button button_JVPostingList_NewJVPosting;
	protected Button button_JVPostingList_JVPostingSearch;

	private transient JVPostingService jVPostingService;

	protected Textbox reference;
	protected Listbox sortOperator_BatchReference;

	protected Textbox batch;
	protected Listbox sortOperator_Batch;

	protected Textbox totDebitsByBatchCcy;
	protected Listbox sortOperator_TotDebitsByBatchCcy;

	protected Textbox totCreditsByBatchCcy;
	protected Listbox sortOperator_TotCreditsByBatchCcy;
	
	protected Textbox moduleType;


	private transient boolean approvedList = false;
	private transient boolean workFlowList = false;
	private transient boolean isItemDoubleClicked = false;

	/**
	 * default constructor.<br>
	 */
	public JVPostingListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "JVPosting";
		super.pageRightName = "JVPostingList";
		super.tableName = "JVPostings_AView";
		super.queueTableName = "JVPostings_TView";
		super.enquiryTableName = "JVPostings_View";

	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_JVPostingList(Event event) throws Exception {
		logger.debug("Entering");


		// Set the page level components.
		setPageComponents(window_JVPostingList, borderLayout_JVPostingList, listBoxJVPosting, pagingJVPostingList);
		setItemRender(new JVPostingListModelItemRenderer());

		registerButton(button_JVPostingList_JVPostingSearch);
		registerButton(button_JVPostingList_NewJVPosting, "button_JVPostingList_NewJVPosting", true);

		registerField("BatchReference");
		registerField("reference", listheader_BatchReference, SortOrder.ASC, reference,sortOperator_BatchReference, Operators.STRING);
		registerField("batch", listheader_Batch, SortOrder.NONE, batch, sortOperator_Batch, Operators.STRING);
		registerField("debitCount", listheader_DebitCount);
		registerField("creditsCount", listheader_CreditsCount);
		registerField("totDebitsByBatchCcy", listheader_TotDebitsByBatchCcy);
		registerField("totCreditsByBatchCcy", listheader_TotCreditsByBatchCcy);

		// Render the page and display the data.
		doRenderPage();
		search();

		logger.debug("Leaving");
	}

	public void onTimer$timer(Event event) {
		logger.debug("Entering");
		if (this.timer.isRunning()) {
			refreshList();
		}
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_JVPostingList_JVPostingSearch(Event event) {
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
	 * @throws InterruptedException
	 */
	public void onClick$button_JVPostingList_NewJVPosting(Event event) throws InterruptedException {
		logger.debug("Entering");
		this.timer.stop();
		final JVPosting aJVPosting = new JVPosting();
		aJVPosting.setNewRecord(true);
		aJVPosting.setWorkflowId(getWorkFlowId());
		isItemDoubleClicked = false;
		doShowDialogPage(aJVPosting);
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onJVPostingItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());
		this.timer.stop();
		isItemDoubleClicked = true;
		// get the selected JVPosting object
		final Listitem item = this.listBoxJVPosting.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final JVPosting aJVPosting = (JVPosting) item.getAttribute("data");
			// Checking for validation process(Back end thread execution) is
			// completed or not. If completed then show detailed view
			// otherwise shows error message as back end job is running and
			// detailed view is not allowed.
			if (aJVPosting.getValidationStatus() != null
					&& aJVPosting.getValidationStatus().equalsIgnoreCase(PennantConstants.POSTSTS_INPROGRESS)) {
				MessageUtil.showError(Labels.getLabel("label_ACValidationProcessInprogress") + aJVPosting.getId());
				// Method for Refreshing List after Account Validation
				// Process
				refreshList();
			} else {
				JVPosting jVPosting = null;
				if (approvedList) {
					jVPosting = getJVPostingService().getApprovedJVPostingById(aJVPosting.getId());
				} else {
					jVPosting = getJVPostingService().getJVPostingById(aJVPosting.getId());
				}

				if (jVPosting == null) {
					String[] errParm = new String[1];
					String[] valueParm = new String[1];
					valueParm[0] = Long.toString(aJVPosting.getId());
					errParm[0] = PennantJavaUtil.getLabel("label_BatchReference") + ":" + valueParm[0];

					ErrorDetail errorDetails = ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
							"41005", errParm, valueParm), getUserWorkspace().getUserLanguage());
					MessageUtil.showError(errorDetails.getError());
				} else {
					/*
					 * if (isWorkFlowEnabled() && (this.moduleType == null || this.moduleType .getValue()
					 * .equalsIgnoreCase( PennantConstants.MODULETYPE_REPOSTING))) {
					 * 
					 * if (jVPosting.getWorkflowId() == 0) { jVPosting.setWorkflowId(workFlowDetails .getWorkFlowId());
					 * } WorkflowLoad flowLoad = new WorkflowLoad(jVPosting.getWorkflowId(), jVPosting.getNextTaskId(),
					 * getUserWorkspace() .getUserRoleSet()); boolean userAcces = validateUserAccess("JVPosting", new
					 * String[] { "BatchReference" }, flowLoad.getRole(), getUserWorkspace()
					 * .getLoggedInUser().getLoginUsrID(), jVPosting); if (userAcces) { showDetailView(jVPosting); }
					 * else { MessageUtil.showErrorMessage(Labels .getLabel("RECORD_NOTALLOWED")); } } else {
					 */
					doShowDialogPage(jVPosting);
					// }
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param academic
	 *            The entity that need to be passed to the dialog.
	 * @throws InterruptedException
	 */
	private void doShowDialogPage(JVPosting aJVPosting) throws InterruptedException {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them with a object of the selected item. For
		 * handed over these parameter only a Map is accepted. So we put the object in a HashMap.
		 */
		if (this.moduleType != null && StringUtils.equals(PennantConstants.MODULETYPE_ENQ, moduleType.getValue())) {
			moduleCode = PennantConstants.MODULETYPE_ENQ;
		}
		 
		Map<String, Object> arg = getDefaultArguments();
		if (this.moduleCode != null && this.moduleCode.equalsIgnoreCase(PennantConstants.MODULETYPE_ENQ)) {
			arg.put("enqModule", true);
			arg.put("workFlowList", this.workFlowList);
		} else if (this.moduleCode != null
				&& this.moduleCode.equalsIgnoreCase(PennantConstants.MODULETYPE_REPOSTING)) {
			arg.put("rePostingModule", true);
		} else {
			arg.put("enqModule", false);
		}
		/*
		 * we can additionally handed over the listBox or the controller self, so we have in the dialog access to the
		 * listbox Listmodel. This is fine for synchronizing the data in the JVPostingListbox from the dialog when we do
		 * a delete, edit or insert a JVPosting.
		 */
		arg.put("jVPostingListCtrl", this);
		arg.put("isExpRequired", ImplementationConstants.ALLOW_EXPENSE_TRACKING);

		// call the zul-file with the parameters packed in a map
		try {
			String zulPage = null;
			if (isItemDoubleClicked) {
				zulPage = "/WEB-INF/pages/Others/JVPosting/JVPostingDialog.zul";
			} else {
				zulPage = "/WEB-INF/pages/Others/JVPosting/JVPostingDialog.zul";
			}
			arg.put("jVPosting", aJVPosting);
			Executions.createComponents(zulPage, null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */

	public void refreshList() {
		if (!this.timer.isRunning()) {
			this.timer.start();
		}
		final JdbcSearchObject<JVPosting> soJVPosting = searchObject;
		this.pagingJVPostingList.setActivePage(0);
		this.getPagedListWrapper().setSearchObject(soJVPosting);
		if (this.listBoxJVPosting != null) {
			this.listBoxJVPosting.getListModel();
		}
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

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) throws InterruptedException {
		this.timer.stop();
		new PTListReportUtils("JVPosting", searchObject, this.pagingJVPostingList.getTotalSize() + 1);
		logger.debug("Leaving");
	}

	/**
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCheck$fromApproved(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		search();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on "fromWorkflow"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCheck$fromWorkFlow(Event event) throws Exception {
		search();
	}

	public void setJVPostingService(JVPostingService jVPostingService) {
		this.jVPostingService = jVPostingService;
	}

	public JVPostingService getJVPostingService() {
		return this.jVPostingService;
	}

}