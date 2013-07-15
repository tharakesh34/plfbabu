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
 * FileName    		:  OverdueChargeRecoveryListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-05-2012    														*
 *                                                                  						*
 * Modified Date    :  11-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-05-2012       Pennant	                 0.1                                            * 
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

package com.pennant.webui.financemanagement.overduechargerecovery;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.CreateEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Grid;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.financemanagement.OverdueChargeRecoveryService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.search.SearchResult;
import com.pennant.webui.finance.enquiry.FinanceEnquiryHeaderDialogCtrl;
import com.pennant.webui.financemanagement.overduechargerecovery.model.OverdueChargeRecoveryComparator;
import com.pennant.webui.financemanagement.overduechargerecovery.model.OverdueChargeRecoveryListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/FinanceManagement/OverdueChargeRecovery/OverdueChargeRecoveryList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class OverdueChargeRecoveryListCtrl extends GFCBaseListCtrl<OverdueChargeRecovery> implements Serializable {

	private static final long serialVersionUID = -4562972510077651582L;
	private final static Logger logger = Logger.getLogger(OverdueChargeRecoveryListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_OverdueChargeRecoveryList; 		// autowired
	protected Borderlayout 	borderLayout_OverdueChargeRecoveryList; // autowired
	protected Paging 		pagingOverdueChargeRecoveryList; 		// autowired
	protected Listbox 		listBoxOverdueChargeRecovery; 			// autowired
	protected Div 			div_OverdueChargeRecoveryList; 			// autowired
	private Tabpanel 		tabPanel_dialogWindow;

	// List headers
	protected Listheader listheader_FinSchdDate; 		// autowired
	protected Listheader listheader_FinODDate; 			// autowired
	protected Listheader listheader_FinODPri; 			// autowired
	protected Listheader listheader_FinODPft; 			// autowired
	protected Listheader listheader_FinODTot; 			// autowired
	protected Listheader listheader_FinODCPenalty; 		// autowired
	protected Listheader listheader_FinODCWaived; 		// autowired
	protected Listheader listheader_FinODCPLPenalty; 	// autowired
	protected Listheader listheader_FinODCCPenalty; 	// autowired
	protected Listheader listheader_FinODCRecoverySts; 	// autowired

	// checkRights
	protected Button button_OverdueChargeRecoveryList_OverdueChargeRecoverySearchDialog; // autowired
	protected Button button_OverdueChargeRecoveryList_PrintList; // autowired
	protected Button btnHelp; // autowired
	protected Button btnRefresh; // autowired

	// NEEDED for the ReUse in the SearchWindow
	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	protected JdbcSearchObject<OverdueChargeRecovery> searchObj;
	private transient PagedListService pagedListService;
	private transient OverdueChargeRecoveryService overdueChargeRecoveryService;
	private transient WorkFlowDetails workFlowDetails=null;

	private Textbox recoveryCode;
	private String finReference = "";

	/**
	 * default constructor.<br>
	 */
	public OverdueChargeRecoveryListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected OverdueChargeRecovery object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_OverdueChargeRecoveryList(ForwardEvent event) throws Exception {
		logger.debug("Entering" + event.toString());

		if(event != null && event.getTarget().getParent().getParent() != null){
			tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
		}

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("OverdueChargeRecovery");
		boolean wfAvailable=true;

		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("OverdueChargeRecovery");

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
		
		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("finReference")) {
			this.finReference = (String) args.get("finReference");
		} 
		if (args.containsKey("financeEnquiryHeaderDialogCtrl")) {
			this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) args.get("financeEnquiryHeaderDialogCtrl");
		} 

		/* set components visible dependent on the users rights */
		doCheckRights();

		// set the paging parameters
		if(this.recoveryCode.getValue().equals("N")) {
			this.borderLayout_OverdueChargeRecoveryList.setHeight(getBorderLayoutHeight());
			this.pagingOverdueChargeRecoveryList.setPageSize(getListRows());
			this.pagingOverdueChargeRecoveryList.setDetailed(true);
		}

		this.listheader_FinSchdDate.setSortAscending(new FieldComparator("finSchdDate", true));
		this.listheader_FinSchdDate.setSortDescending(new FieldComparator("finSchdDate", false));
		this.listheader_FinODDate.setSortAscending(new FieldComparator("finODDate", true));
		this.listheader_FinODDate.setSortDescending(new FieldComparator("finODDate", false));
		this.listheader_FinODPri.setSortAscending(new FieldComparator("finODPri", true));
		this.listheader_FinODPri.setSortDescending(new FieldComparator("finODPri", false));
		this.listheader_FinODPft.setSortAscending(new FieldComparator("finODPft", true));
		this.listheader_FinODPft.setSortDescending(new FieldComparator("finODPft", false));
		this.listheader_FinODTot.setSortAscending(new FieldComparator("finODTot", true));
		this.listheader_FinODTot.setSortDescending(new FieldComparator("finODTot", false));
		this.listheader_FinODCPenalty.setSortAscending(new FieldComparator("finODCPenalty", true));
		this.listheader_FinODCPenalty.setSortDescending(new FieldComparator("finODCPenalty", false));
		this.listheader_FinODCWaived.setSortAscending(new FieldComparator("finODCWaived", true));
		this.listheader_FinODCWaived.setSortDescending(new FieldComparator("finODCWaived", false));
		this.listheader_FinODCPLPenalty.setSortAscending(new FieldComparator("finODCPLPenalty", true));
		this.listheader_FinODCPLPenalty.setSortDescending(new FieldComparator("finODCPLPenalty", false));
		this.listheader_FinODCCPenalty.setSortAscending(new FieldComparator("finODCCPenalty", true));
		this.listheader_FinODCCPenalty.setSortDescending(new FieldComparator("finODCCPenalty", false));
		this.listheader_FinODCRecoverySts.setSortAscending(new FieldComparator("finODCRecoverySts", true));
		this.listheader_FinODCRecoverySts.setSortDescending(new FieldComparator("finODCRecoverySts", false));

		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<OverdueChargeRecovery>(OverdueChargeRecovery.class,getListRows());
		this.searchObj.addSort("FinReference", false);
		if(!"".equals(finReference)){
			this.searchObj.addFilter(new Filter("FinReference", this.finReference,Filter.OP_EQUAL));
		}
		// Workflow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("FinODCRecovery_View");
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("FinODCRecovery_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_OverdueChargeRecoveryList_OverdueChargeRecoverySearchDialog.setVisible(false);
			this.button_OverdueChargeRecoveryList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			findSearchObject();
			// set the itemRenderer
			this.listBoxOverdueChargeRecovery.setItemRenderer(new OverdueChargeRecoveryListModelItemRenderer());
		}

		if(this.recoveryCode.getValue().equals("Y")) {
			
			this.div_OverdueChargeRecoveryList.setVisible(false);
			if(tabPanel_dialogWindow != null){
				getBorderLayoutHeight();
				int rowsHeight = this.financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount()*20;
				listBoxOverdueChargeRecovery.setHeight(borderLayoutHeight-rowsHeight-95+"px");
				this.window_OverdueChargeRecoveryList.setHeight(this.borderLayoutHeight-rowsHeight-55+"px");
				tabPanel_dialogWindow.appendChild(this.window_OverdueChargeRecoveryList);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Internal Method for Grouping List items
	 */
	public void findSearchObject() {
		logger.debug("Entering");
		if(this.recoveryCode.getValue().equals("N")) {
			this.searchObj.addFilter(new Filter("finODCRecoverySts", "R"));
		}
		final SearchResult<OverdueChargeRecovery> searchResult = getPagedListService().getSRBySearchObject(this.searchObj);
		listBoxOverdueChargeRecovery.setModel(new GroupsModelArray(
				searchResult.getResult().toArray(),new OverdueChargeRecoveryComparator()));
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("OverdueChargeRecoveryList");
		this.button_OverdueChargeRecoveryList_OverdueChargeRecoverySearchDialog.setVisible(
				getUserWorkspace().isAllowed("button_OverdueChargeRecoveryList_OverdueChargeRecoveryFindDialog"));
		this.button_OverdueChargeRecoveryList_PrintList.setVisible(
				getUserWorkspace().isAllowed("button_OverdueChargeRecoveryList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.financemanagement.overduechargerecovery.model.OverdueChargeRecoveryListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onOverdueChargeRecoveryItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected OverdueChargeRecovery object
		final Listitem item = this.listBoxOverdueChargeRecovery.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final OverdueChargeRecovery aOverdueChargeRecovery = (OverdueChargeRecovery) item.getAttribute("data");
			final OverdueChargeRecovery overdueChargeRecovery = getOverdueChargeRecoveryService().
			getOverdueChargeRecoveryById(aOverdueChargeRecovery.getId(),aOverdueChargeRecovery.getFinSchdDate(),
					aOverdueChargeRecovery.getFinODFor());

			if(overdueChargeRecovery==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aOverdueChargeRecovery.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND FinReference='"+ overdueChargeRecovery.getFinReference()+"' AND version=" + overdueChargeRecovery.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "OverdueChargeRecovery", whereCond, overdueChargeRecovery.getTaskId(), overdueChargeRecovery.getNextTaskId());
					if (userAcces){
						showDetailView(overdueChargeRecovery);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(overdueChargeRecovery);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the OverdueChargeRecovery dialog with a new empty entry. <br>
	 */
	public void onClick$button_OverdueChargeRecoveryList_NewOverdueChargeRecovery(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new OverdueChargeRecovery object, We GET it from the backend.
		final OverdueChargeRecovery aOverdueChargeRecovery = getOverdueChargeRecoveryService().getNewOverdueChargeRecovery();
		showDetailView(aOverdueChargeRecovery);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param OverdueChargeRecovery (aOverdueChargeRecovery)
	 * @throws Exception
	 */
	private void showDetailView(OverdueChargeRecovery aOverdueChargeRecovery) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if(aOverdueChargeRecovery.getWorkflowId()==0 && isWorkFlowEnabled()){
			aOverdueChargeRecovery.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("overdueChargeRecovery", aOverdueChargeRecovery);
		if(this.recoveryCode.getValue().equals("Y")) {
			map.put("inquiry", true);
		}

		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the OverdueChargeRecoveryListbox from the
		 * dialog when we do a delete, edit or insert a OverdueChargeRecovery.
		 */
		map.put("overdueChargeRecoveryListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/OverdueChargeRecovery/OverdueChargeRecoveryDialog.zul",null,map);
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
		logger.debug(event.toString());
		PTMessageUtils.showHelpWindow(event, window_OverdueChargeRecoveryList);
		logger.debug("Leaving");
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
		this.pagingOverdueChargeRecoveryList.setActivePage(0);
		Events.postEvent("onCreate", this.window_OverdueChargeRecoveryList, event);
		this.window_OverdueChargeRecoveryList.invalidate();
		logger.debug("Leaving");
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if(this.recoveryCode.getValue().equals("N")){
			closeDialog(this.window_OverdueChargeRecoveryList, "OverdueChargeRecovery");
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for call the OverdueChargeRecovery dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_OverdueChargeRecoveryList_OverdueChargeRecoverySearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our OverdueChargeRecoveryDialog zul-file with parameters. So we can
		 * call them with a object of the selected OverdueChargeRecovery. For handed over
		 * these parameter only a Map is accepted. So we put the OverdueChargeRecovery object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("overdueChargeRecoveryCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/OverdueChargeRecovery/OverdueChargeRecoverySearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the overdueChargeRecovery print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_OverdueChargeRecoveryList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		PTReportUtils.getReport("OverdueChargeRecovery", getSearchObj());
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setOverdueChargeRecoveryService(OverdueChargeRecoveryService overdueChargeRecoveryService) {
		this.overdueChargeRecoveryService = overdueChargeRecoveryService;
	}
	public OverdueChargeRecoveryService getOverdueChargeRecoveryService() {
		return this.overdueChargeRecoveryService;
	}

	public JdbcSearchObject<OverdueChargeRecovery> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<OverdueChargeRecovery> searchObj) {
		this.searchObj = searchObj;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}
}