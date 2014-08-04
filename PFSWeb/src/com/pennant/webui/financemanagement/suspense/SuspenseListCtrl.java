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
 * FileName    		:  SuspenseListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-05-2012    														*
 *                                                                  						*
 * Modified Date    :  31-05-2012    														*
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

package com.pennant.webui.financemanagement.suspense;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
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
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.service.financemanagement.SuspenseService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.financemanagement.suspense.model.SuspenseListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/FinanceManagement/Suspense/SuspenseList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class SuspenseListCtrl extends GFCBaseListCtrl<FinanceSuspHead> implements Serializable {

	private static final long serialVersionUID = 4481377123949925578L;
	private final static Logger logger = Logger.getLogger(SuspenseListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_SuspenseList; 		// autowired
	protected Borderlayout 	borderLayout_SuspenseList; // autowired
	protected Paging 		pagingSuspenseList; 		// autowired
	protected Listbox 		listBoxSuspense; 			// autowired

	// List headers
	protected Listheader listheader_FinReference; 	// autowired
	protected Listheader listheader_CustID; 		// autowired
	protected Listheader listheader_FinIsInSusp; 	// autowired
	protected Listheader listheader_ManualSusp; 	// autowired
	protected Listheader listheader_FinSuspAmt;		// autowired
	protected Listheader listheader_FinCurSuspAmt;	// autowired
	protected Listheader listheader_RecordStatus; 				// autoWired
	protected Listheader listheader_RecordType;

	// Filtering Felds

	protected Textbox 		finReference; 						// autowired
	protected Listbox 		sortOperator_finReference; 			// autowired
	protected Textbox 		custID; 							// autowired
	protected Listbox 		sortOperator_custID; 				// autowired
	protected Datebox 		finSuspDate; 					    // autowired
	protected Listbox 		sortOperator_finSuspDate; 		    // autowired
	protected Decimalbox 	finSuspAmt; 					    // autowired
	protected Listbox 		sortOperator_finSuspAmt; 		    // autowired
	protected Decimalbox 	finCurSuspAmt; 					    // autowired
	protected Listbox 		sortOperator_finCurSuspAmt; 		// autowired
	protected Checkbox 		manualSusp; 					    // autowired
	protected Listbox 		sortOperator_manualSusp; 		    // autowired

	protected Label label_SuspenseSearch_RecordStatus; 		    // autowired
	protected Label label_SuspenseSearch_RecordType; 			// autowired
	protected Label label_SuspenseSearchResult; 				// autowired

	private Grid 			searchGrid;							// autowired
	protected Textbox 		moduleType; 						// autowired

	private transient boolean  approvedList=false; 
	private transient WorkFlowDetails workFlowDetails=null;

	// checkRights
	protected Button btnHelp; 										// autowired
	protected Button button_SuspenseList_SuspenseSearchDialog;    	// autowired
	protected Button button_SuspenseList_NewSuspense; 	            // autowired
	protected Button button_SuspenseList_PrintList; 				// autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<FinanceSuspHead> searchObj;
	private transient SuspenseService suspenseService;
	protected Textbox moduleName;
	private String menuItemRightName = null;
	private String module = "";

	/**
	 * default constructor.<br>
	 */
	public SuspenseListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected Suspense object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SuspenseList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		//Getting Menu Item Right Name
		if (event.getTarget() != null && event.getTarget().getParent() != null
				&& event.getTarget().getParent().getParent()!=null && 
				event.getTarget().getParent().getParent().getParent() != null && 
				event.getTarget().getParent().getParent().getParent().getParent() != null) {

			String menuItemName = ((Tabbox)event.getTarget().getParent().getParent().getParent().getParent()).getSelectedTab().getId();
			menuItemName = menuItemName.trim().replace("tab_", "menu_Item_");

			if(getUserWorkspace().getHasMenuRights().containsKey(menuItemName)){
				menuItemRightName = getUserWorkspace().getHasMenuRights().get(menuItemName);
			}
		}

		boolean wfAvailable=true;
		if(moduleName.getValue().equals("SUSPHEAD")){
			this.module="Suspense";

			ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("FinanceSuspHead");

			if (moduleMapping.getWorkflowType()!=null){
				workFlowDetails = WorkFlowUtil.getWorkFlowDetails("FinanceSuspHead");

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
		}else if(moduleName.getValue().equals("SUSPENQ")){
			this.module="SuspenseEnquiry";
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_finReference.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finReference.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custID.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custID.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finSuspDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_finSuspDate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finSuspAmt.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_finSuspAmt.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finCurSuspAmt.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_finCurSuspAmt.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_manualSusp.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_manualSusp.setItemRenderer(new SearchOperatorListModelItemRenderer());

		/* set components visible dependent on the users rights */
		doCheckRights();

		this.borderLayout_SuspenseList.setHeight(getBorderLayoutHeight());
		this.listBoxSuspense.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

		// set the paging parameters
		this.pagingSuspenseList.setPageSize(getListRows());
		this.pagingSuspenseList.setDetailed(true);

		this.listheader_FinReference.setSortAscending(new FieldComparator("finReference", true));
		this.listheader_FinReference.setSortDescending(new FieldComparator("finReference", false));
		this.listheader_CustID.setSortAscending(new FieldComparator("custID", true));
		this.listheader_CustID.setSortDescending(new FieldComparator("custID", false));
		this.listheader_FinIsInSusp.setSortAscending(new FieldComparator("finIsInSusp", true));
		this.listheader_FinIsInSusp.setSortDescending(new FieldComparator("finIsInSusp", false));
		this.listheader_ManualSusp.setSortAscending(new FieldComparator("manualSusp", true));
		this.listheader_ManualSusp.setSortDescending(new FieldComparator("manualSusp", false));
		this.listheader_FinSuspAmt.setSortAscending(new FieldComparator("finSuspAmt", true));
		this.listheader_FinSuspAmt.setSortDescending(new FieldComparator("finSuspAmt", false));
		this.listheader_FinCurSuspAmt.setSortAscending(new FieldComparator("finCurSuspAmt", true));
		this.listheader_FinCurSuspAmt.setSortDescending(new FieldComparator("finCurSuspAmt", false));
		this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
		this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
		this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
		this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));

		if("Suspense".equals(this.module)){
			// ++ create the searchObject and init sorting ++//
			this.searchObj = new JdbcSearchObject<FinanceSuspHead>(FinanceSuspHead.class,getListRows());
			this.searchObj.addSort("FinReference", false);

			// Workflow
			if (isWorkFlowEnabled()) {
				if (isFirstTask()) {
					button_SuspenseList_NewSuspense.setVisible(true);
				} else {
					button_SuspenseList_NewSuspense.setVisible(false);
				}
			}

			if (!isWorkFlowEnabled() && wfAvailable){
				this.button_SuspenseList_NewSuspense.setVisible(false);
				this.button_SuspenseList_SuspenseSearchDialog.setVisible(false);
				this.button_SuspenseList_PrintList.setVisible(false);
				PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
			}else{
				doSearch();
			}
		}else{
			doSearch();
		}

		// set the itemRenderer
		this.listBoxSuspense.setItemRenderer(new SuspenseListModelItemRenderer());

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("SuspenseList");

		if(this.module.equals("Suspense")){
			this.button_SuspenseList_NewSuspense.setVisible(getUserWorkspace().isAllowed("button_SuspenseList_NewSuspense"));
		}else{
			this.button_SuspenseList_NewSuspense.setVisible(false);
		}
		this.button_SuspenseList_SuspenseSearchDialog.setVisible(true);
		this.button_SuspenseList_PrintList.setVisible(getUserWorkspace().isAllowed("button_SuspenseList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.provision.provision.model.SuspenseListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onSuspenseItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected Suspense object
		final Listitem item = this.listBoxSuspense.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceSuspHead aSuspHead = (FinanceSuspHead) item.getAttribute("data");

			boolean isEnquiry = true;
			if(this.module.equals("Suspense")){
				isEnquiry = false;
			}
			final FinanceSuspHead suspHead = getSuspenseService().getFinanceSuspHeadById(aSuspHead.getFinReference(),isEnquiry);

			if(suspHead==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aSuspHead.getFinReference();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(
						PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND FinReference='"+ suspHead.getFinReference()+"' AND Version=" + suspHead.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "FinanceSuspHead", 
							whereCond, suspHead.getTaskId(), suspHead.getNextTaskId());
					if (userAcces){
						showDetailView(suspHead);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(suspHead);
				}
			}	
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param Suspense (aSuspense)
	 * @throws Exception
	 */
	private void showDetailView(FinanceSuspHead aSuspHead) throws Exception {
		logger.debug("Entering");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("suspHead", aSuspHead);
		
		if(aSuspHead.getWorkflowId()==0 && isWorkFlowEnabled()){
			aSuspHead.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the SuspenseListbox from the
		 * dialog when we do a delete, edit or insert a Suspense.
		 */
		map.put("suspenseListCtrl", this);
		map.put("menuItemRightName", menuItemRightName);

		// call the zul-file with the parameters packed in a map
		try {
			if(this.module.equals("Suspense")){
				Executions.createComponents("/WEB-INF/pages/FinanceManagement/Suspense/SuspenseDialog.zul",
						null,map);
			}else{
				Executions.createComponents("/WEB-INF/pages/FinanceManagement/SuspenseDetail/SuspenseDetailEnquiryDialog.zul",
						null,map);
			}
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
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_SuspenseList);
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
		logger.debug("Entering" + event.toString());
		this.sortOperator_custID.setSelectedIndex(0);
		this.custID.setValue("");
		this.sortOperator_finCurSuspAmt.setSelectedIndex(0);
		this.finCurSuspAmt.setText("");
		this.sortOperator_finReference.setSelectedIndex(0);
		this.finReference.setValue("");
		this.sortOperator_finSuspAmt.setSelectedIndex(0);
		this.finSuspAmt.setText("");
		this.sortOperator_finSuspDate.setSelectedIndex(0);
		this.finSuspDate.setValue(null);
		this.sortOperator_manualSusp.setSelectedIndex(0);
		this.manualSusp.setChecked(false);
		
		this.pagingSuspenseList.setActivePage(0);
		Events.postEvent("onCreate", this.window_SuspenseList, event);
		this.window_SuspenseList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the Suspense dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_SuspenseList_SuspenseSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the New Suspense  button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_SuspenseList_NewSuspense(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		final FinanceSuspHead aSuspense = getSuspenseService().getNewFinanceSuspHead();
		aSuspense.setNewRecord(true);
		showDetailView(aSuspense);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the provision print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_SuspenseList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("FinanceSuspHead", getSearchObj(),this.pagingSuspenseList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	public void doSearch(){
		logger.debug("Entering");
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<FinanceSuspHead>(FinanceSuspHead.class,getListRows());

		// Defualt Sort on the table
		this.searchObj.addSort("FinReference", false);

		// Workflow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("FinSuspHead");
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
			approvedList=false;
		}else{
			approvedList=true;
		}
		if(approvedList){
			this.searchObj.addTabelName("FinSuspHead_AView");
		}else{
			this.searchObj.addTabelName("FinSuspHead_View");
		}

		//Finance Reference
		if (!StringUtils.trimToEmpty(this.finReference.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_finReference.getSelectedItem(), this.finReference.getValue() , "finReference");
		}

		//Finance Suspense Amount
		if (this.finSuspAmt.getValue()!=null) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_finSuspAmt.getSelectedItem(), this.finSuspAmt.getValue() , "finSuspAmt");
		}

		// Current Suspense Amount
		if (this.finCurSuspAmt.getValue()!=null) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_finCurSuspAmt.getSelectedItem(), this.finCurSuspAmt.getValue() , "finCurSuspAmt");
		}
		//Finance Customer Id
		if (!StringUtils.trimToEmpty(this.custID.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_custID.getSelectedItem(), this.custID.getValue() , "custID");
		}

		// Manual Suspense 
		if (this.manualSusp.isChecked()) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_manualSusp.getSelectedItem(), 1 , "manualSusp");
		}else{
			searchObj = getSearchFilter(searchObj,this.sortOperator_manualSusp.getSelectedItem(), 0 , "manualSusp");
		}

		// Finance Suspense Date
		if (this.finSuspDate.getValue()!=null) {
			searchObj.addFilter(new Filter("finSuspDate",DateUtility.formatUtilDate(
					this.finSuspDate.getValue(),PennantConstants.DBDateFormat), Filter.OP_EQUAL));
		}

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = this.searchObj.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / "+ filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}

		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxSuspense,this.pagingSuspenseList);
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setSuspenseService(SuspenseService suspenseService) {
		this.suspenseService = suspenseService;
	}
	public SuspenseService getSuspenseService() {
		return this.suspenseService;
	}

	public JdbcSearchObject<FinanceSuspHead> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<FinanceSuspHead> searchObj) {
		this.searchObj = searchObj;
	}
}