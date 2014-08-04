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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.backend.service.others.JVPostingService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.others.jvposting.model.JVPostingListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;
	
/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/others/JVPosting/JVPostingList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class JVPostingListCtrl extends GFCBaseListCtrl<JVPosting> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger
			.getLogger(JVPostingListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_JVPostingList; // autowired
	protected Borderlayout borderLayout_JVPostingList; // autowired
	protected Paging pagingJVPostingList; // autowired
	protected Listbox listBoxJVPosting; // autowired
	protected Timer timer;

	// List headers
	protected Listheader listheader_BatchReference; // autowired
	protected Listheader listheader_Batch; // autowired
	protected Listheader listheader_DebitCount; // autowired
	protected Listheader listheader_CreditsCount; // autowired
	protected Listheader listheader_TotDebitsByBatchCcy; // autowired
	protected Listheader listheader_TotCreditsByBatchCcy; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;
	protected Listheader listheader_ValidationStatus;
	protected Listheader listheader_PostingStatus;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_JVPostingList_NewJVPosting; // autowired
	protected Button button_JVPostingList_JVPostingSearch; // autowired
	protected Button button_JVPostingList_PrintList; // autowired
	protected Label label_JVPostingList_RecordStatus; // autoWired
	protected Label label_JVPostingList_RecordType; // autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<JVPosting> searchObj;

	private transient JVPostingService jVPostingService;
	private transient WorkFlowDetails workFlowDetails = null;

	protected Textbox batchReference; // autowired
	protected Listbox sortOperator_BatchReference; // autowired

	protected Textbox batch; // autowired
	protected Listbox sortOperator_Batch; // autowired

	protected Textbox totDebitsByBatchCcy; // autowired
	protected Listbox sortOperator_TotDebitsByBatchCcy; // autowired

	protected Textbox totCreditsByBatchCcy; // autowired
	protected Listbox sortOperator_TotCreditsByBatchCcy; // autowired

	protected Textbox recordStatus; // autowired
	protected Listbox recordType; // autowired
	protected Listbox sortOperator_RecordStatus; // autowired
	protected Listbox sortOperator_RecordType; // autowired
	protected Grid searchGrid; // autowired
	protected Textbox moduleType; // autowired
	protected Radio fromApproved;
	protected Radio fromWorkFlow;
	protected Row workFlowFrom;

	private transient boolean approvedList = false;
	private transient boolean workFlowList = false;
	private transient boolean isItemDoubleClicked = false;

	/**
	 * default constructor.<br>
	 */
	public JVPostingListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void onCreate$window_JVPostingList(Event event) throws Exception {
		logger.debug("Entering");

		if(this.moduleType != null && !this.moduleType.getValue().equals("ENQ")){
			this.timer.setDelay(10000);
			this.timer.start();
		}
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("JVPosting");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("JVPosting");

			if (workFlowDetails == null) {
				setWorkFlowEnabled(false);
			} else {
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(
						workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}
		} else {
			wfAvailable = false;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_BatchReference
				.setModel(new ListModelList<SearchOperators>(
						new SearchOperators().getStringOperators()));
		this.sortOperator_BatchReference
				.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_Batch.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_Batch
				.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_TotDebitsByBatchCcy
				.setModel(new ListModelList<SearchOperators>(
						new SearchOperators().getStringOperators()));
		this.sortOperator_TotDebitsByBatchCcy
				.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_TotCreditsByBatchCcy
				.setModel(new ListModelList<SearchOperators>(
						new SearchOperators().getStringOperators()));
		this.sortOperator_TotCreditsByBatchCcy
				.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_RecordStatus
					.setModel(new ListModelList<SearchOperators>(
							new SearchOperators().getStringOperators()));
			this.sortOperator_RecordStatus
					.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_RecordType
					.setModel(new ListModelList<SearchOperators>(
							new SearchOperators().getStringOperators()));
			this.sortOperator_RecordType
					.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType = setRecordType(this.recordType);

			this.sortOperator_RecordType.setSelectedIndex(1);
			this.recordType.setSelectedIndex(0);

		} else {
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.label_JVPostingList_RecordStatus.setVisible(false);
			this.label_JVPostingList_RecordType.setVisible(false);
			this.sortOperator_RecordStatus.setVisible(false);
			this.sortOperator_RecordType.setVisible(false);
		}
		/* set components visible dependent on the users rights */
		doCheckRights();

		this.borderLayout_JVPostingList.setHeight(getBorderLayoutHeight());
		this.listBoxJVPosting.setHeight(getListBoxHeight(searchGrid.getRows()
				.getChildren().size() - 1));

		// set the paging parameters
		this.pagingJVPostingList.setPageSize(getListRows());
		this.pagingJVPostingList.setDetailed(true);

		this.listheader_BatchReference.setSortAscending(new FieldComparator(
				"batchReference", true));
		this.listheader_BatchReference.setSortDescending(new FieldComparator(
				"batchReference", false));
		this.listheader_Batch.setSortAscending(new FieldComparator("batch",
				true));
		this.listheader_Batch.setSortDescending(new FieldComparator("batch",
				false));
		this.listheader_DebitCount.setSortAscending(new FieldComparator(
				"debitCount", true));
		this.listheader_DebitCount.setSortDescending(new FieldComparator(
				"debitCount", false));
		this.listheader_CreditsCount.setSortAscending(new FieldComparator(
				"creditsCount", true));
		this.listheader_CreditsCount.setSortDescending(new FieldComparator(
				"creditsCount", false));
		this.listheader_TotDebitsByBatchCcy
				.setSortAscending(new FieldComparator("totDebitsByBatchCcy",
						true));
		this.listheader_TotDebitsByBatchCcy
				.setSortDescending(new FieldComparator("totDebitsByBatchCcy",
						false));
		this.listheader_TotCreditsByBatchCcy
				.setSortAscending(new FieldComparator("totCreditsByBatchCcy",
						true));
		this.listheader_TotCreditsByBatchCcy
				.setSortDescending(new FieldComparator("totCreditsByBatchCcy",
						false));
		this.listheader_ValidationStatus.setSortAscending(new FieldComparator(
				"validationStatus", true));
		this.listheader_ValidationStatus.setSortDescending(new FieldComparator(
				"validationStatus", false));
		this.listheader_PostingStatus.setSortAscending(new FieldComparator(
				"BatchPostingStatus", true));
		this.listheader_PostingStatus.setSortDescending(new FieldComparator(
				"BatchPostingStatus", false));
		// set the itemRenderer
		this.listBoxJVPosting
				.setItemRenderer(new JVPostingListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator(
					"recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator(
					"recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator(
					"recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator(
					"recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_JVPostingList_NewJVPosting.setVisible(false);
			this.button_JVPostingList_JVPostingSearch.setVisible(false);
			this.button_JVPostingList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil
					.getLabel("WORKFLOW CONFIG NOT FOUND"));

		} else {
			doSearch();
			if (this.workFlowFrom != null && !isWorkFlowEnabled()) {
				this.workFlowFrom.setVisible(false);
				this.fromApproved.setSelected(true);
			}
		}

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
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see:
	 * com.pennant.webui.others.jvposting.model.JVPostingListModelItemRenderer
	 * .java <br>
	 * 
	 * @param event
	 * @throws Exception
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
					&& aJVPosting.getValidationStatus().equalsIgnoreCase(
							PennantConstants.InProgress)) {
				PTMessageUtils.showErrorMessage(Labels
						.getLabel("label_ACValidationProcessInprogress")
						+ aJVPosting.getId());
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
					valueParm[0] = aJVPosting.getId() + "";
					errParm[0] = PennantJavaUtil
							.getLabel("label_BatchReference")
							+ ":"
							+ valueParm[0];

					ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
									errParm, valueParm), getUserWorkspace()
									.getUserLanguage());
					PTMessageUtils.showErrorMessage(errorDetails
							.getErrorMessage());
				} else {
					/*if (isWorkFlowEnabled()
							&& (this.moduleType == null || this.moduleType
									.getValue()
									.equalsIgnoreCase(
											PennantConstants.MODULETYPE_REPOSTING))) {

						if (jVPosting.getWorkflowId() == 0) {
							jVPosting.setWorkflowId(workFlowDetails
									.getWorkFlowId());
						}
						WorkflowLoad flowLoad = new WorkflowLoad(jVPosting.getWorkflowId(),
								jVPosting.getNextTaskId(), getUserWorkspace()
										.getUserRoleSet());
						boolean userAcces = validateUserAccess("JVPosting",
								new String[] { "BatchReference" },
								flowLoad.getRole(), getUserWorkspace()
										.getLoginUserDetails().getLoginUsrID(), jVPosting);
						if (userAcces) {
							showDetailView(jVPosting);
						} else {
							PTMessageUtils.showErrorMessage(Labels
									.getLabel("RECORD_NOTALLOWED"));
						}
					} else {*/
						showDetailView(jVPosting);
					//}
				}
			}
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
		final JdbcSearchObject<JVPosting> soJVPosting = this.getSearchObj();
		this.pagingJVPostingList.setActivePage(0);
		this.getPagedListWrapper().setSearchObject(soJVPosting);
		if (this.listBoxJVPosting != null) {
			this.listBoxJVPosting.getListModel();
		}
	}

	/**
	 * Call the JVPosting dialog with a new empty entry. <br>
	 */
	public void onClick$button_JVPostingList_NewJVPosting(Event event)
			throws Exception {
		logger.debug(event.toString());
		this.timer.stop();
		// create a new JVPosting object, We GET it from the backend.
		final JVPosting aJVPosting = getJVPostingService().getNewJVPosting();
		isItemDoubleClicked = false;
		showDetailView(aJVPosting);
		logger.debug("Leaving");
	}

	/*
	 * Invoke Search
	 */

	public void onClick$button_JVPostingList_JVPostingSearch(Event event)
			throws Exception {
		logger.debug("Entering" + event.toString());
		this.timer.stop();
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "refresh" button is clicked. <br>
	 * <br>
	 * Refreshes the view by calling the onCreate event manually.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnRefresh(Event event) throws InterruptedException {
		logger.debug(event.toString());
		/*
		 * this.pagingJVPostingList.setActivePage(0);
		 * Events.postEvent("onCreate", this.window_JVPostingList, event);
		 * this.window_JVPostingList.invalidate();
		 */

		this.sortOperator_BatchReference.setSelectedIndex(0);
		this.batchReference.setValue("");
		this.sortOperator_Batch.setSelectedIndex(0);
		this.batch.setValue("");
		this.sortOperator_TotDebitsByBatchCcy.setSelectedIndex(0);
		this.totDebitsByBatchCcy.setValue("");
		this.sortOperator_TotCreditsByBatchCcy.setSelectedIndex(0);
		this.totCreditsByBatchCcy.setValue("");

		if (isWorkFlowEnabled()) {
			this.sortOperator_RecordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");

			this.sortOperator_RecordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}

		doSearch();

		logger.debug("Leaving");
	}

	/*
	 * Invoke Search
	 */

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(event.toString());
		PTMessageUtils.showHelpWindow(event, this.window_JVPostingList);
		logger.debug("Leaving");
	}

	/**
	 * When the jVPosting print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_JVPostingList_PrintList(Event event)
			throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		this.timer.stop();
		PTReportUtils.getReport("JVPosting", getSearchObj());
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
		doSearch();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCheck$fromWorkFlow(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doSearch();
		logger.debug("Leaving " + event.toString());
	}
	


	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("JVPostingList");

		if (this.moduleType == null
				|| this.moduleType.getValue().equalsIgnoreCase(
						PennantConstants.MODULETYPE_REPOSTING)) {
			this.button_JVPostingList_NewJVPosting
					.setVisible(getUserWorkspace().isAllowed(
							"button_JVPostingList_NewJVPosting"));
			this.button_JVPostingList_NewJVPosting.setVisible(true);
		} else {
			this.button_JVPostingList_NewJVPosting.setVisible(false);
		}
		this.button_JVPostingList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_JVPostingList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param JVPosting
	 *            (aJVPosting)
	 * @throws Exception
	 */
	private void showDetailView(JVPosting aJVPosting) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		final HashMap<String, Object> map = new HashMap<String, Object>();
		if (this.moduleType != null
				&& this.moduleType.getValue().equalsIgnoreCase(
						PennantConstants.MODULETYPE_ENQ)) {
			map.put("enqModule", true);
			map.put("workFlowList", this.workFlowList);
		} else if (this.moduleType != null
				&& this.moduleType.getValue().equalsIgnoreCase(
						PennantConstants.MODULETYPE_REPOSTING)) {
			map.put("rePostingModule", true);
		} else {
			map.put("enqModule", false);
		}
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the JVPostingListbox from the
		 * dialog when we do a delete, edit or insert a JVPosting.
		 */
		map.put("jVPostingListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			String zulPage = null;
			if (isItemDoubleClicked) {
				zulPage = "/WEB-INF/pages/Others/JVPosting/JVPostingDialog.zul";
			} else {
				zulPage = "/WEB-INF/pages/Others/JVPosting/JVPostingDialog.zul";
			}
			map.put("jVPosting", aJVPosting);
			Executions.createComponents(zulPage, null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * Search/filter data for the filled out fields<br>
	 * <br>
	 * 1. Checks for each textbox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */

	public void doSearch() {
		logger.debug("Entering");
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<JVPosting>(JVPosting.class,
				getListRows());
		this.searchObj.addSort("BatchReference", false);
		this.searchObj.addTabelName("JVPostings_View");

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
		this.searchObj.addField("batchReference");
		this.searchObj.addField("Currency");
		this.searchObj.addField("CurrencyEditField");
		this.searchObj.addField("batch");
		this.searchObj.addField("debitCount");
		this.searchObj.addField("creditsCount");
		this.searchObj.addField("totDebitsByBatchCcy");
		this.searchObj.addField("totCreditsByBatchCcy");
		this.searchObj.addField("recordStatus");
		this.searchObj.addField("recordType");
		this.searchObj.addField("ValidationStatus");
		this.searchObj.addField("BatchPostingStatus");

		// Workflow
		if (isWorkFlowEnabled()) {
			if (this.moduleType == null) {
				this.searchObj.addFilterNotEqual("nextRoleCode", "");
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace()
						.getUserRoles(), isFirstTask());
				//this.searchObj.addFilterNotEqual("BatchPostingStatus", PennantConstants.Posting_fail);
				approvedList = false;
			} else if (this.moduleType.getValue().equalsIgnoreCase(
					PennantConstants.MODULETYPE_ENQ)) {
				if (this.fromApproved.isSelected()) {
					approvedList = true;
					workFlowList = false;
//					this.searchObj.addFilterEqual("ValidationStatus",
//							PennantConstants.Posting_success);
//					this.searchObj.addFilterEqual("BatchPostingStatus",
//							PennantConstants.Posting_success);
					this.searchObj.addTabelName("JVPostings_AView");
				} else {
					approvedList = false;
					workFlowList = true;
					this.searchObj.addTabelName("JVPostings_TView");
				}
			} else {
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace()
						.getUserRoles(), isFirstTask());
				this.searchObj.addFilterNotEqual("BatchPostingStatus", "");
				this.searchObj.addFilterNotEqual("BatchPostingStatus",
						PennantConstants.Posting_success);
				this.searchObj.addTabelName("JVPostings_View");
			}
		} else {
			approvedList = true;
		}

		// Batch Reference
		if (!StringUtils.trimToEmpty(this.batchReference.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_BatchReference.getSelectedItem(),
					this.batchReference.getValue(), "BatchReference");
		}
		// Batch
		if (!StringUtils.trimToEmpty(this.batch.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_Batch.getSelectedItem(),
					this.batch.getValue(), "Batch");
		}
		// Tot Debits By Batch Ccy
		if (!StringUtils.trimToEmpty(this.totDebitsByBatchCcy.getValue())
				.equals("")) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_TotDebitsByBatchCcy.getSelectedItem(),
					this.totDebitsByBatchCcy.getValue(), "TotDebitsByBatchCcy");
		}
		// Tot Credits By Batch Ccy
		if (!StringUtils.trimToEmpty(this.totCreditsByBatchCcy.getValue())
				.equals("")) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_TotCreditsByBatchCcy.getSelectedItem(),
					this.totCreditsByBatchCcy.getValue(),
					"TotCreditsByBatchCcy");
		}

		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_RecordStatus.getSelectedItem(),
					this.recordStatus.getValue(), "RecordStatus");
		}

		// Record Type
		if (this.recordType.getSelectedItem() != null
				&& !PennantConstants.List_Select.equals(this.recordType
						.getSelectedItem().getValue())) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_RecordType.getSelectedItem(),
					this.recordType.getSelectedItem().getValue().toString(),
					"RecordType");
		}

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = this.searchObj.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / "
						+ filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}

		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxJVPosting,
				this.pagingJVPostingList);

		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setJVPostingService(JVPostingService jVPostingService) {
		this.jVPostingService = jVPostingService;
	}

	public JVPostingService getJVPostingService() {
		return this.jVPostingService;
	}

	public JdbcSearchObject<JVPosting> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<JVPosting> searchObj) {
		this.searchObj = searchObj;
	}
}