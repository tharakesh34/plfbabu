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
 * FileName    		:  CommitmentListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  25-03-2013    														*
 *                                                                  						*
 * Modified Date    :  25-03-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 25-03-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.commitment.commitment;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
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
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.service.commitment.CommitmentService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.commitment.commitment.model.CommitmentListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Commitment/Commitment/CommitmentList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CommitmentListCtrl extends GFCBaseListCtrl<Commitment> implements Serializable {

	private static final long	           serialVersionUID	= 1L;
	private final static Logger	           logger	        = Logger.getLogger(CommitmentListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window	                   window_CommitmentList;	                                      // autowired
	protected Borderlayout	               borderLayout_CommitmentList;	                              // autowired
	protected Paging	                   pagingCommitmentList;	                                      // autowired
	protected Listbox	                   listBoxCommitment;	                                          // autowired

	// List headers
	protected Listheader	               listheader_CmtReference;	                                  // autowired
	protected Listheader	               listheader_custID;	                                          // autowired
	protected Listheader	               listheader_CmtBranch;	                                      // autowired
	//protected Listheader	               listheader_CmtAccount;	                                      // autowired
	protected Listheader	               listheader_CustName;	                                      // autowired
	protected Listheader	               listheader_CmtCcy;	                                          // autowired
	protected Listheader	               listheader_CmtAmount;	                                      // autowired
	protected Listheader	               listheader_CmtUtilizedAmount;	                              // autowired
	protected Listheader	               listheader_CmtAvailable;	                                  // autowired
	protected Listheader	               listheader_CmtStartDate;	                                  // autowired
	protected Listheader	               listheader_CmtExpDate;	                                      // autowired
	protected Listheader	               listheader_RecordStatus;	                                  // autowired
	protected Listheader	               listheader_RecordType;

	// checkRights
	protected Button	                   btnHelp;	                                                  // autowired
	protected Button	                   button_CommitmentList_NewCommitment;	                      // autowired
	protected Button	                   button_CommitmentList_CommitmentSearch;	                      // autowired
	protected Button	                   button_CommitmentList_PrintList;	                          // autowired
	protected Label	                       label_CommitmentList_RecordStatus;	                          // autoWired
	protected Label	                       label_CommitmentList_RecordType;	                          // autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Commitment>	searchObj;

	private transient CommitmentService	   commitmentService;
	private transient WorkFlowDetails	   workFlowDetails	= null;

	protected Textbox	                   cmtReference;	                                              // autowired
	protected Listbox	                   sortOperator_CmtReference;	                                  // autowired

	protected Textbox	                   custID;	                                                      // autowired
	protected Listbox	                   sortOperator_custID;	                                  	      // autowired

	protected Textbox	                   cmtBranch;	                                                  // autowired
	protected Listbox	                   sortOperator_CmtBranch;	                                      // autowired

/*	protected Textbox	                   cmtAccount;	                                                  // autowired
	protected Listbox	                   sortOperator_CmtAccount;	                                  	  // autowired
*/	protected Textbox	                   custName;	                                                  // autowired
	protected Listbox	                   sortOperator_CustName;	                                 	  // autowired

	protected Textbox	                   cmtCcy;	                                                      // autowired
	protected Listbox	                   sortOperator_CmtCcy;	                                      	  // autowired

	protected Decimalbox	               cmtAmount;	                                                  // autowired
	protected Listbox	                   sortOperator_CmtAmount;	                                      // autowired

	protected Decimalbox	               cmtUtilizedAmount;	                                          // autowired
	protected Listbox	                   sortOperator_CmtUtilizedAmount;	                              // autowired

	protected Decimalbox	               cmtAvailable;	                                              // autowired
	protected Listbox	                   sortOperator_CmtAvailable;	                                  // autowired

	protected Datebox	                   cmtStartDate;	                                              // autowired
	protected Listbox	                   sortOperator_CmtStartDate;	                                  // autowired

	protected Datebox	                   cmtExpDate;	                                                  // autowired
	protected Listbox	                   sortOperator_CmtExpDate;	                                  // autowired

	protected Textbox	                   recordStatus;	                                              // autowired
	protected Listbox	                   recordType;	                                                  // autowired
	protected Listbox	                   sortOperator_RecordStatus;	                                  // autowired
	protected Listbox	                   sortOperator_RecordType;	                                  // autowired
	protected Grid	                       searchGrid;	                                                  // autowired
	protected Textbox	                   moduleType;	                                                  // autowired
	protected Radio	                       fromApproved;
	protected Radio	                       fromWorkFlow;
	protected Row	                       workFlowFrom;

	private transient boolean	           approvedList	    = false;

	/**
	 * default constructor.<br>
	 */
	public CommitmentListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void onCreate$window_CommitmentList(Event event) throws Exception {
		logger.debug("Entering");

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("Commitment");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Commitment");

			if (workFlowDetails == null) {
				setWorkFlowEnabled(false);
			} else {
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}
		} else {
			wfAvailable = false;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_CmtReference.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_CmtReference.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custID.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custID.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_CmtBranch.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_CmtBranch.setItemRenderer(new SearchOperatorListModelItemRenderer());

		/*this.sortOperator_CmtAccount.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_CmtAccount.setItemRenderer(new SearchOperatorListModelItemRenderer());*/
		this.sortOperator_CustName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_CustName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_CmtCcy.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_CmtCcy.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_CmtAmount.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_CmtAmount.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_CmtUtilizedAmount.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_CmtUtilizedAmount.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_CmtAvailable.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_CmtAvailable.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_CmtStartDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_CmtStartDate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_CmtExpDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_CmtExpDate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_RecordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_RecordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			
			this.sortOperator_RecordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_RecordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType = PennantAppUtil.setRecordType(this.recordType);

			this.sortOperator_RecordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);

		} else {
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.label_CommitmentList_RecordStatus.setVisible(false);
			this.label_CommitmentList_RecordType.setVisible(false);
			this.sortOperator_RecordStatus.setVisible(false);
			this.sortOperator_RecordType.setVisible(false);
		}
		/* set components visible dependent on the users rights */
		doCheckRights();

		this.borderLayout_CommitmentList.setHeight(getBorderLayoutHeight());
		this.listBoxCommitment.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

		// set the paging parameters
		this.pagingCommitmentList.setPageSize(getListRows());
		this.pagingCommitmentList.setDetailed(true);

		this.listheader_CmtReference.setSortAscending(new FieldComparator("cmtReference", true));
		this.listheader_CmtReference.setSortDescending(new FieldComparator("cmtReference", false));
		this.listheader_custID.setSortAscending(new FieldComparator("custCIF", true));
		this.listheader_custID.setSortDescending(new FieldComparator("custCIF", false));
		this.listheader_CmtBranch.setSortAscending(new FieldComparator("cmtBranch", true));
		this.listheader_CmtBranch.setSortDescending(new FieldComparator("cmtBranch", false));
/*		this.listheader_CmtAccount.setSortAscending(new FieldComparator("cmtAccount", true));
		this.listheader_CmtAccount.setSortDescending(new FieldComparator("cmtAccount", false));
*/		
		this.listheader_CustName.setSortAscending(new FieldComparator("CustShrtName", true));
		this.listheader_CustName.setSortDescending(new FieldComparator("CustShrtName", false));
		this.listheader_CmtCcy.setSortAscending(new FieldComparator("cmtCcy", true));
		this.listheader_CmtCcy.setSortDescending(new FieldComparator("cmtCcy", false));
		this.listheader_CmtAmount.setSortAscending(new FieldComparator("cmtAmount", true));
		this.listheader_CmtAmount.setSortDescending(new FieldComparator("cmtAmount", false));
		this.listheader_CmtUtilizedAmount.setSortAscending(new FieldComparator("cmtUtilizedAmount", true));
		this.listheader_CmtUtilizedAmount.setSortDescending(new FieldComparator("cmtUtilizedAmount", false));
		this.listheader_CmtAvailable.setSortAscending(new FieldComparator("cmtAvailable", true));
		this.listheader_CmtAvailable.setSortDescending(new FieldComparator("cmtAvailable", false));
		this.listheader_CmtStartDate.setSortAscending(new FieldComparator("cmtStartDate", true));
		this.listheader_CmtStartDate.setSortDescending(new FieldComparator("cmtStartDate", false));
		this.listheader_CmtExpDate.setSortAscending(new FieldComparator("cmtExpDate", true));
		this.listheader_CmtExpDate.setSortDescending(new FieldComparator("cmtExpDate", false));
		// set the itemRenderer
		this.listBoxCommitment.setItemRenderer(new CommitmentListModelItemRenderer());
		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		this.cmtStartDate.setFormat(PennantConstants.dateFormat);
		this.cmtExpDate.setFormat(PennantConstants.dateFormat);
		
		
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_CommitmentList_NewCommitment.setVisible(false);
			this.button_CommitmentList_CommitmentSearch.setVisible(false);
			this.button_CommitmentList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));

		} else {
			doSearch();
			if (this.workFlowFrom != null && !isWorkFlowEnabled()) {
				this.workFlowFrom.setVisible(false);
				this.fromApproved.setSelected(true);
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.commitment.commitment.model.CommitmentListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onCommitmentItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected Commitment object
		final Listitem item = this.listBoxCommitment.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Commitment aCommitment = (Commitment) item.getAttribute("data");
			Commitment commitment = null;
			if (approvedList) {
				commitment = getCommitmentService().getApprovedCommitmentById(aCommitment.getId());
			} else {
				commitment = getCommitmentService().getCommitmentById(aCommitment.getId());
			}

			if (commitment == null) {
				String[] errParm = new String[1];
				String[] valueParm = new String[1];
				valueParm[0] = aCommitment.getId();
				errParm[0] = PennantJavaUtil.getLabel("label_CmtReference") + ":" + valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), getUserWorkspace()
				        .getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				if (isWorkFlowEnabled() && moduleType == null) {

					if (commitment.getWorkflowId() == 0) {
						commitment.setWorkflowId(workFlowDetails.getWorkFlowId());
					}

					//WorkflowLoad flowLoad = new WorkflowLoad(commitment.getWorkflowId(), commitment.getNextTaskId(), getUserWorkspace().getUserRoleSet());

					showDetailView(commitment);
//					boolean userAcces = validateUserAccess("Commitment", new String[] { "CmtReference" }, flowLoad.getRole(), getUserWorkspace().getLoginUserDetails()
//					        .getLoginUsrID(), commitment);
//					if (userAcces) {
//					} else {
//						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
//					}
				} else {
					showDetailView(commitment);
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the Commitment dialog with a new empty entry. <br>
	 */
	public void onClick$button_CommitmentList_NewCommitment(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new Commitment object, We GET it from the backend.
		final Commitment aCommitment = getCommitmentService().getNewCommitment();
		showDetailView(aCommitment);
		logger.debug("Leaving");
	}

	/*
	 * Invoke Search
	 */

	public void onClick$button_CommitmentList_CommitmentSearch(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
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
		/*		this.pagingCommitmentList.setActivePage(0);
				Events.postEvent("onCreate", this.window_CommitmentList, event);
				this.window_CommitmentList.invalidate();
		*/

		this.sortOperator_CmtReference.setSelectedIndex(0);
		this.cmtReference.setValue("");
		this.sortOperator_custID.setSelectedIndex(0);
		this.custID.setValue("");
		this.sortOperator_CmtBranch.setSelectedIndex(0);
		this.cmtBranch.setValue("");
		this.sortOperator_CustName.setSelectedIndex(0);
		this.custName.setValue("");
		this.sortOperator_CmtCcy.setSelectedIndex(0);
		this.cmtCcy.setValue("");
		this.sortOperator_CmtAmount.setSelectedIndex(0);
		this.cmtAmount.setText("");
		this.sortOperator_CmtUtilizedAmount.setSelectedIndex(0);
		this.cmtUtilizedAmount.setText("");
		this.sortOperator_CmtAvailable.setSelectedIndex(0);
		this.cmtAvailable.setText("");
		this.sortOperator_CmtStartDate.setSelectedIndex(0);
		this.cmtStartDate.setValue(null);
		this.sortOperator_CmtExpDate.setSelectedIndex(0);
		this.cmtExpDate.setValue(null);

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
		PTMessageUtils.showHelpWindow(event, window_CommitmentList);
		logger.debug("Leaving");
	}

	/**
	 * When the commitment print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_CommitmentList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		@SuppressWarnings("unused")
		PTListReportUtils reportUtils = new PTListReportUtils("Commitment",getSearchObj(), this.pagingCommitmentList.getTotalSize() + 1);
		logger.debug("Leaving");
	}

	/**
	 * When user clicks on "fromApproved"
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
		getUserWorkspace().alocateAuthorities("CommitmentList",getRole());
		if (moduleType == null) {
			this.button_CommitmentList_NewCommitment.setVisible(getUserWorkspace().isAllowed("button_CommitmentList_NewCommitment"));
		} else {
			this.button_CommitmentList_NewCommitment.setVisible(false);
		}
		this.button_CommitmentList_PrintList.setVisible(getUserWorkspace().isAllowed("button_CommitmentList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param Commitment (aCommitment)
	 * @throws Exception
	 */
	private void showDetailView(Commitment aCommitment) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("commitment", aCommitment);
		if (moduleType != null) {
			map.put("enqModule", true);
		} else {
			map.put("enqModule", false);
		}
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the CommitmentListbox from the
		 * dialog when we do a delete, edit or insert a Commitment.
		 */
		map.put("commitmentListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Commitment/Commitment/CommitmentDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
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
		this.searchObj = new JdbcSearchObject<Commitment>(Commitment.class, getListRows());
		
		this.searchObj.addSort("CmtReference", false);
		this.searchObj.addTabelName("Commitments_View");

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
		/*this.searchObj.addField("cmtReference");
		this.searchObj.addField("custID");
		this.searchObj.addField("cmtBranch");
		this.searchObj.addField("cmtAccount");
		this.searchObj.addField("cmtCcy");
		this.searchObj.addField("cmtAmount");
		this.searchObj.addField("cmtUtilizedAmount");
		this.searchObj.addField("cmtAvailable");
		this.searchObj.addField("cmtStartDate");
		this.searchObj.addField("cmtExpDate");
		this.searchObj.addField("custShrtName");
		this.searchObj.addField("branchDesc");
		this.searchObj.addField("ccyDesc");*/

		// Workflow
		if (isWorkFlowEnabled()) {

			if (isFirstTask() && this.moduleType == null) {
				button_CommitmentList_NewCommitment.setVisible(true);
			} else {
				button_CommitmentList_NewCommitment.setVisible(false);
			}

			if (this.moduleType == null) {
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
				approvedList = false;
			} else {
				if (this.fromApproved.isSelected()) {
					approvedList = true;
				} else {
					this.searchObj.addTabelName("Commitments_TView");
					approvedList = false;
				}
			}
		} else {
			approvedList = true;
		}
		if (approvedList) {
			this.searchObj.addTabelName("Commitments_AView");
		}

		// Cmt Reference
		if (!StringUtils.trimToEmpty(this.cmtReference.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_CmtReference.getSelectedItem(), this.cmtReference.getValue(), "CmtReference");
		}
		// cust I D
		if (!StringUtils.trimToEmpty(this.custID.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_custID.getSelectedItem(), this.custID.getValue(), "custCIF");
		}
		// Cmt Branch
		if (!StringUtils.trimToEmpty(this.cmtBranch.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_CmtBranch.getSelectedItem(), this.cmtBranch.getValue(), "CmtBranch");
		}
		// Cmt Account
		if (!StringUtils.trimToEmpty(this.custName.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_CustName.getSelectedItem(), this.custName.getValue(), "CustShrtName");
		}
		// Cmt Ccy
		if (!StringUtils.trimToEmpty(this.cmtCcy.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_CmtCcy.getSelectedItem(), this.cmtCcy.getValue(), "CmtCcy");
		}
		// Cmt Amount
		if (this.cmtAmount.getValue() != null) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_CmtAmount.getSelectedItem(),
			        PennantApplicationUtil.formateAmount(this.cmtAmount.getValue(), PennantConstants.defaultCCYDecPos), "CmtAmount");
		}
		// Cmt Utilized Amount
		if (this.cmtUtilizedAmount.getValue() != null) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_CmtUtilizedAmount.getSelectedItem(),
			        PennantApplicationUtil.formateAmount(this.cmtUtilizedAmount.getValue(), PennantConstants.defaultCCYDecPos), "CmtUtilizedAmount");
		}
		// Cmt Available
		if (this.cmtAvailable.getValue() != null) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_CmtAvailable.getSelectedItem(),
			        PennantApplicationUtil.formateAmount(this.cmtAvailable.getValue(), PennantConstants.defaultCCYDecPos), "CmtAvailable");
		}
		// Cmt Start Date
		if (this.cmtStartDate.getValue() != null) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_CmtStartDate.getSelectedItem(),
			        DateUtility.formatDate(this.cmtStartDate.getValue(), PennantConstants.DBDateFormat), "CmtStartDate");
		}
		// Cmt Exp Date
		if (this.cmtExpDate.getValue() != null) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_CmtExpDate.getSelectedItem(),
			        DateUtility.formatDate(this.cmtExpDate.getValue(), PennantConstants.DBDateFormat), "CmtExpDate");
		}

		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_RecordStatus.getSelectedItem(), this.recordStatus.getValue(), "RecordStatus");
		}

		// Record Type
		if (this.recordType.getSelectedItem() != null && !StringUtils.trimToEmpty(this.recordType.getSelectedItem().getValue().toString()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_RecordType.getSelectedItem(), this.recordType.getSelectedItem().getValue().toString(), "RecordType");
		}

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = this.searchObj.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / " + filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}

		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxCommitment, this.pagingCommitmentList);

		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setCommitmentService(CommitmentService commitmentService) {
		this.commitmentService = commitmentService;
	}

	public CommitmentService getCommitmentService() {
		return this.commitmentService;
	}

	public JdbcSearchObject<Commitment> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<Commitment> searchObj) {
		this.searchObj = searchObj;
	}


}