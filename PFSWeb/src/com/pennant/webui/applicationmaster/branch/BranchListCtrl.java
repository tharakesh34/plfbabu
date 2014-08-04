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
 * FileName    		:  BranchListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.branch;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
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

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.service.applicationmaster.BranchService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.applicationmaster.branch.model.BranchListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/Branch/BranchList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class BranchListCtrl extends GFCBaseListCtrl<Branch> implements Serializable {

	private static final long serialVersionUID = 1237735044265585362L;
	private final static Logger logger = Logger.getLogger(BranchListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_BranchList; 			// auto wired
	protected Borderlayout 	borderLayout_BranchList; 	// auto wired
	protected Paging 		pagingBranchList; 			// auto wired
	protected Listbox 		listBoxBranch; 				// auto wired

	// List headers
	protected Listheader listheader_BranchCode; 		// auto wired
	protected Listheader listheader_BranchDesc; 		// auto wired
	protected Listheader listheader_BranchCity; 		// auto wired
	protected Listheader listheader_BranchProvince; 	// auto wired
	protected Listheader listheader_BranchCountry; 		// auto wired
	protected Listheader listheader_BranchSwiftBankCde; // auto wired
	protected Listheader listheader_BranchIsActive; 	// auto wired
	protected Listheader listheader_RecordStatus; 		// auto wired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 								// auto wired
	protected Button button_BranchList_NewBranch; 			// auto wired
	protected Button button_BranchList_BranchSearchDialog; 	// auto wired
	protected Button button_BranchList_PrintList; 			// auto wired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Branch> searchObj;
	
	private transient BranchService branchService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public BranchListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected Branch object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_BranchList(Event event) throws Exception {
		logger.debug("Entering"+event.toString());
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("Branch");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Branch");
			
			if (workFlowDetails==null){
				setWorkFlowEnabled(false);
			}else{
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}	
		}else{
			wfAvailable=false;
		}
		
		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the list box. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_BranchList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingBranchList.setPageSize(getListRows());
		this.pagingBranchList.setDetailed(true);

		this.listheader_BranchCode.setSortAscending(new FieldComparator("branchCode", true));
		this.listheader_BranchCode.setSortDescending(new FieldComparator("branchCode", false));
		this.listheader_BranchDesc.setSortAscending(new FieldComparator("branchDesc", true));
		this.listheader_BranchDesc.setSortDescending(new FieldComparator("branchDesc", false));
		this.listheader_BranchCity.setSortAscending(new FieldComparator("branchCity", true));
		this.listheader_BranchCity.setSortDescending(new FieldComparator("branchCity", false));
		this.listheader_BranchProvince.setSortAscending(new FieldComparator("branchProvince", true));
		this.listheader_BranchProvince.setSortDescending(new FieldComparator("branchProvince", false));
		this.listheader_BranchCountry.setSortAscending(new FieldComparator("branchCountry", true));
		this.listheader_BranchCountry.setSortDescending(new FieldComparator("branchCountry", false));
		this.listheader_BranchSwiftBankCde.setSortAscending(new FieldComparator("branchSwiftBankCde", true));
		this.listheader_BranchSwiftBankCde.setSortDescending(new FieldComparator("branchSwiftBankCde", false));
		this.listheader_BranchIsActive.setSortAscending(new FieldComparator("branchIsActive", true));
		this.listheader_BranchIsActive.setSortDescending(new FieldComparator("branchIsActive", false));
		
		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}
		
		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<Branch>(Branch.class,getListRows());
		this.searchObj.addSort("BranchCode", false);
		
		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("RMTBranches_View");
			if (isFirstTask()) {
				button_BranchList_NewBranch.setVisible(true);
			} else {
				button_BranchList_NewBranch.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("RMTBranches_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_BranchList_NewBranch.setVisible(false);
			this.button_BranchList_BranchSearchDialog.setVisible(false);
			this.button_BranchList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxBranch,this.pagingBranchList);
			// set the itemRenderer
			this.listBoxBranch.setItemRenderer(
					new BranchListModelItemRenderer());
		}	
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("BranchList");
		
		this.button_BranchList_NewBranch.setVisible(getUserWorkspace()
				.isAllowed("button_BranchList_NewBranch"));
		this.button_BranchList_BranchSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_BranchList_BranchFindDialog"));
		this.button_BranchList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_BranchList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see:
	 * com.pennant.webui.rmtmasters.branch.model.BranchListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onBranchItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering"+event.toString());

		// get the selected Branch object
		final Listitem item = this.listBoxBranch.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Branch aBranch = (Branch) item.getAttribute("data");
			final Branch branch = getBranchService().getBranchById(aBranch.getId());
			if(branch==null){

				String[] valueParm = new String[1];
				String[] errParm= new String[1];

				valueParm[0] = aBranch.getBranchCode();
				errParm[0] = PennantJavaUtil.getLabel("label_BranchCode") + ":"+ valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				String whereCond =  " AND BranchCode='"+ branch.getBranchCode()+
				"' AND version=" + branch.getVersion()+" ";

				if(isWorkFlowEnabled()){
					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "Branch",
							whereCond, branch.getTaskId(), branch.getNextTaskId());
					if (userAcces){
						showDetailView(branch);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(branch);
				}
			}
		}
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * Call the Branch dialog with a new empty entry. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_BranchList_NewBranch(Event event) throws Exception {
		logger.debug("Entering"+event.toString());
		// create a new Branch object, We GET it from the back end.
		final Branch aBranch = getBranchService().getNewBranch();
		showDetailView(aBranch);
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param Branch (aBranch)
	 * @throws Exception
	 */
	private void showDetailView(Branch aBranch) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aBranch.getWorkflowId()==0 && isWorkFlowEnabled()){
			aBranch.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("branch", aBranch);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the BranchListbox from the
		 * dialog when we do a delete, edit or insert a Branch.
		 */
		map.put("branchListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/Branch/BranchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering"+event.toString());
		PTMessageUtils.showHelpWindow(event, window_BranchList);
		logger.debug("Leaving"+event.toString());
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
		logger.debug("Entering"+event.toString());
		this.pagingBranchList.setActivePage(0);
		Events.postEvent("onCreate", this.window_BranchList, event);
		this.window_BranchList.invalidate();
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * call the Branch dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_BranchList_BranchSearchDialog(Event event) throws Exception {
		logger.debug("Entering"+event.toString());
		/*
		 * we can call our BranchDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected Branch. For handed over
		 * these parameter only a Map is accepted. So we put the Branch object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("branchCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/Branch/BranchSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * When the branch print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_BranchList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering"+event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("Branch", getSearchObj(),this.pagingBranchList.getTotalSize()+1);
		logger.debug("Leaving"+event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setBranchService(BranchService branchService) {
		this.branchService = branchService;
	}
	public BranchService getBranchService() {
		return this.branchService;
	}

	public JdbcSearchObject<Branch> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<Branch> searchObj) {
		this.searchObj = searchObj;
	}
}