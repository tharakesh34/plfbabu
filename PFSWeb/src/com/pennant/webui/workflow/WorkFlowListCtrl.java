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
 *
 * FileName    		:  WorkFlowListCtl.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.workflow;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.UserWorkspace;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.service.WorkFlowDetailsService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.search.Filter;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.workflow.model.WorkFlowListModelItemRenderer;


/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the 
 * /PFSWeb/WebContent/WEB-INF/pages/SolutionFactory/workFlow/workFlowList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class WorkFlowListCtrl extends GFCBaseListCtrl<WorkFlowDetails> implements Serializable {

	private static final long serialVersionUID = -1635165456608902454L;
	
	private final static Logger logger = Logger.getLogger(WorkFlowListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_workFlowList; 				// autowired
	protected Borderlayout borderLayout_workFlowList; 	// autowired
	protected Paging pagingWorkFlowList; 				// autowired
	
	protected Listbox listBoxWorkFlow; 					// autowired
	protected Listheader listheader_workFlowType; 		// autowired
	protected Listheader listheader_workFlowSubType;
	protected Listheader listheader_workFlowDesc;
	protected Listheader listheader_workFlowStatus;

	private transient WorkFlowDetailsService workFlowDetailsService;

	// checkRights
	protected Button btnHelp; 								// autowired
	protected Button button_workFlowList_NewworkFlow; 		// autowired
	protected Button button_workFlowList_workFlowFindDialog;// autowired
	protected Button button_workFlowList_PrintList; 		// autowired
	

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<WorkFlowDetails> searchObj;
	// row count for listbox
	/**
	 * default constructor.<br>
	 */
	public WorkFlowListCtrl() {
		super();
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected workFlow object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_workFlowList(Event event) throws Exception {
		
		logger.debug("Entering" + event.toString());
		
		/* set components visible dependent of the users rights */
		//doCheckRights();

		/**
		 * Calculate how many rows have been place in the list box. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_workFlowList.setHeight(getBorderLayoutHeight());

		// set the paging params
		this.pagingWorkFlowList.setPageSize(getListRows());
		this.pagingWorkFlowList.setDetailed(true);
		
		// not used listheaders must be declared like ->
		this.listheader_workFlowType.setSortAscending(
				new FieldComparator("workFlowType", true));
		this.listheader_workFlowType.setSortDescending(
				new FieldComparator("workFlowType", false));
		
		this.listheader_workFlowSubType.setSortAscending(
				new FieldComparator("workFlowSubType", true));
		this.listheader_workFlowSubType.setSortDescending(
				new FieldComparator("workFlowSubType", false));
		
		this.listheader_workFlowDesc.setSortAscending(
				new FieldComparator("workFlowDesc", true));
		this.listheader_workFlowDesc.setSortDescending(
				new FieldComparator("workFlowDesc", false));
		
		this.listheader_workFlowStatus.setSortAscending(
				new FieldComparator("workFlowActive", true));
		this.listheader_workFlowStatus.setSortDescending(
				new FieldComparator("workFlowActive", false));
		
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<WorkFlowDetails>(WorkFlowDetails.class);
		this.searchObj.addSort("workFlowType", false);
		this.searchObj.addFilter(new Filter("WorkFlowActive",1,Filter.OP_EQUAL));
		setSearchObj(this.searchObj);
		
		findSearchObject();
		logger.debug("Leaving" + event.toString());
		
	}
	public void findSearchObject(){
		getPagedListWrapper().init(this.searchObj, this.listBoxWorkFlow,
				this.pagingWorkFlowList);
		// set the itemRenderer
		this.listBoxWorkFlow.setItemRenderer(new WorkFlowListModelItemRenderer());
	}
	
	

	/**
	 * SetVisible for components by checking if there's a right for it.	
	 */
	@SuppressWarnings("unused")
	private void doCheckRights() {
		logger.debug("Entering ");
		final UserWorkspace workspace = getUserWorkspace();
		workspace.alocateAuthorities("WorkFlowList");
		logger.debug("Leaving ");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.invoiceHeader.model.
	 * InvoiceHeaderListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onWorkFlowItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected invoiceHeader object
		final Listitem item = this.listBoxWorkFlow.getSelectedItem();

		if (item != null) {
			final WorkFlowDetails aWorkFlowDetails = (WorkFlowDetails) item
					.getAttribute("data");
			
			//Get the latest details by passing the primary key to the servers
			showDetailView(getWorkFlowDetailsService().getWorkFlowDetailsByID(
					aWorkFlowDetails.getId()));
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Call the InvoiceHeader dialog with a new empty entry. <br>
	 */
	public void onClick$button_workFlowList_NewworkFlow(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		final WorkFlowDetails aWorkFlowDetails = getWorkFlowDetailsService()
				.getNewWorkFlowDetails();
		showDetailView(aWorkFlowDetails);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param branche
	 * @throws Exception
	 */
	private void showDetailView(WorkFlowDetails aWorkFlowDetails) throws Exception {
		logger.debug("Entering ");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("workFlowDetails", aWorkFlowDetails);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the invoiceHeaderListbox from the
		 * dialog when we do a delete, edit or insert a invoiceHeader.
		 */
		map.put("workFlowListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SolutionFactory/workFlow/WorkflowDesign.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving ");
	}

	/**
	 * Call the Help Window
	 * 
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_workFlowList);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * TO Refresh the Current Window
	 * 
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnRefresh(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Events.postEvent("onCreate", this.window_workFlowList, event);
		this.window_workFlowList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public JdbcSearchObject<WorkFlowDetails> getSearchObj() {
		return searchObj;
	}
	public void setSearchObj(JdbcSearchObject<WorkFlowDetails> searchObj) {
		this.searchObj = searchObj;
	}

	public WorkFlowDetailsService getWorkFlowDetailsService() {
		return workFlowDetailsService;
	}
	public void setWorkFlowDetailsService(
			WorkFlowDetailsService workFlowDetailsService) {
		this.workFlowDetailsService = workFlowDetailsService;
	}

	
}