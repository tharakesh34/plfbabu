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
 * FileName    		:  FinanceReferenceDetailListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-11-2011    														*
 *                                                                  						*
 * Modified Date    :  26-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.lmtmasters.financereferencedetail;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.lmtmasters.FinanceReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.lmtmasters.FinanceReferenceDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.lmtmasters.financereferencedetail.model.FinanceReferenceDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/FinanceReferenceDetail/FinanceReferenceDetailList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class FinanceReferenceDetailListCtrl extends GFCBaseListCtrl<FinanceType> implements Serializable {

	private static final long serialVersionUID = 5574042632591594715L;
	private final static Logger logger = Logger.getLogger(FinanceReferenceDetailListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_FinanceReferenceDetailList; 			// auto wired
	protected Borderlayout 	borderLayout_FinanceReferenceDetailList; 	// auto wired
	protected Paging 		pagingFinanceReferenceDetailList; 			// auto wired
	protected Listbox 		listBoxFinanceReferenceDetail; 				// auto wired

	protected Textbox  finType;
	protected Listbox  sortOperator_finType;
	protected Textbox  finTypeDesc;
	protected Listbox  sortOperator_finTypeDesc;
	
	// List headers
	protected Listheader listheader_FinanceType; 		// auto wired
	protected Listheader listheader_FinanceTypeDesc; 	// auto wired

	// checkRights
	protected Button btnHelp; 																// auto wired
	protected Button button_FinanceReferenceDetailList_NewFinanceReferenceDetail; 			// auto wired
	protected Button button_FinanceReferenceDetailList_FinanceReferenceDetailSearchDialog; 	// auto wired
	protected Button button_FinanceReferenceDetailList_PrintList; 							// auto wired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<FinanceType> searchObj;
	private transient FinanceReferenceDetailService financeReferenceDetailService;
	private transient WorkFlowDetails workFlowDetails=null;
	private PagedListService pagedListService;
	
	/**
	 * default constructor.<br>
	 */
	public FinanceReferenceDetailListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected FinanceCheckList object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinanceReferenceDetailList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("FinanceReferenceDetail");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("FinanceReferenceDetail");
			
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
		
		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
		this.sortOperator_finType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finType.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finTypeDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finTypeDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		/* set components visible dependent on the users rights */
		doCheckRights();
		
		this.borderLayout_FinanceReferenceDetailList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingFinanceReferenceDetailList.setPageSize(getListRows());
		this.pagingFinanceReferenceDetailList.setDetailed(true);

		this.listheader_FinanceType.setSortAscending(new FieldComparator("FinType", true));
		this.listheader_FinanceType.setSortDescending(new FieldComparator("FinType", false));
		this.listheader_FinanceTypeDesc.setSortAscending(new FieldComparator("FinTypeDesc", true));
		this.listheader_FinanceTypeDesc.setSortDescending(new FieldComparator("FinTypeDesc", false));

		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<FinanceType>(FinanceType.class,getListRows());
		this.searchObj.addSort("FinType", false);
		this.searchObj.addFilter(new Filter("finIsActive", 1, Filter.OP_EQUAL));
		
		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("RMTFinanceTypes_View");
			if (isFirstTask()) {
				button_FinanceReferenceDetailList_NewFinanceReferenceDetail.setVisible(true);
			} else {
				button_FinanceReferenceDetailList_NewFinanceReferenceDetail.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("RMTFinanceTypes_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_FinanceReferenceDetailList_NewFinanceReferenceDetail.setVisible(false);
			//this.button_FinanceReferenceDetailList_FinanceReferenceDetailSearchDialog.setVisible(false);
			this.button_FinanceReferenceDetailList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxFinanceReferenceDetail,this.pagingFinanceReferenceDetailList);
			// set the itemRenderer
			this.listBoxFinanceReferenceDetail.setItemRenderer(new FinanceReferenceDetailListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("FinanceReferenceDetailList");

		/*this.button_FinanceReferenceDetailList_NewFinanceReferenceDetail.setVisible(
				getUserWorkspace().isAllowed("button_FinanceReferenceDetailList_NewFinanceReferenceDetail"));*/
		this.button_FinanceReferenceDetailList_NewFinanceReferenceDetail.setVisible(false);
		/*this.button_FinanceReferenceDetailList_FinanceReferenceDetailSearchDialog.setVisible(
				getUserWorkspace().isAllowed("button_FinanceReferenceDetailList_FinanceReferenceDetailFindDialog"));*/
		this.button_FinanceReferenceDetailList_PrintList.setVisible(
				getUserWorkspace().isAllowed("button_FinanceReferenceDetailList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.lmtmasters.financereferencedetail.model.
	 * FinanceReferenceDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onFinanceReferenceDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected FinanceReferenceDetail object
		final Listitem item = this.listBoxFinanceReferenceDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			FinanceReference aFinanceReference = new FinanceReference();
			final FinanceType aFinanceType = (FinanceType) item.getAttribute("data");

			final FinanceReference financeReference = getFinanceReferenceDetailService().getFinanceReference(
					aFinanceType.getFinType());
			if(financeReference ==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=String.valueOf(aFinanceReference.getFinType());
				errParm[0]=PennantJavaUtil.getLabel("label_FinType")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}
			if(financeReference.getLovDescWorkFlowRolesName()==null || financeReference.getLovDescWorkFlowRolesName().equals("")){
				PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
			}else{
				financeReference.setLovDescFinTypeDescName(aFinanceType.getFinTypeDesc());
			showDetailView(financeReference);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the FinanceReferenceDetail dialog with a new empty entry. <br>
	 */
	public void onClick$button_FinanceReferenceDetailList_NewFinanceReferenceDetail(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new FinanceReferenceDetail object, We GET it from the backEnd.
		FinanceReference aFinanceReference = new FinanceReference();
		showDetailView(aFinanceReference);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param FinanceReferenceDetail (aFinanceReferenceDetail)
	 * @throws Exception
	 */
	private void showDetailView(FinanceReference aFinanceReference) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		FinanceReferenceDetail financeReferenceDetail = getFinanceReferenceDetailService().getNewFinanceReferenceDetail();
		financeReferenceDetail.setWorkflowId(0);
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeReference", aFinanceReference);
		map.put("financeReferenceDetail", financeReferenceDetail);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the FinanceReferenceDetailListbox from the
		 * dialog when we do a delete, edit or insert a FinanceReferenceDetail.
		 */
		map.put("financeReferenceDetailListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SolutionFactory/FinanceReferenceDetail/FinanceReferenceDetailDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_FinanceReferenceDetailList);
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
		this.sortOperator_finType.setSelectedIndex(0);
		this.finType.setValue("");
		this.sortOperator_finTypeDesc.setSelectedIndex(0);
		this.finTypeDesc.setValue("");
		this.pagingFinanceReferenceDetailList.setActivePage(0);
		Events.postEvent("onCreate", this.window_FinanceReferenceDetailList, event);
		this.window_FinanceReferenceDetailList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Call the FinanceReferenceDetail dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_FinanceReferenceDetailList_FinanceReferenceDetailSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving");
	}
	
	public void doSearch(){
		logger.debug("Entering");
		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<FinanceType>(FinanceType.class,getListRows());
		this.searchObj.addSort("FinType", false);
		this.searchObj.addSort("FinTypeDesc", false);
		this.searchObj.addTabelName("RMTFinanceTypes_AView");
	
			
		//Finance Type
		if (!StringUtils.trimToEmpty(this.finType.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_finType.getSelectedItem(), this.finType.getValue(), "finType");
		}
		
		// Finance Type Desc
		if (!StringUtils.trimToEmpty(this.finTypeDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_finTypeDesc.getSelectedItem(), this.finTypeDesc.getValue(), "finTypeDesc");
		}
		
		if (logger.isDebugEnabled()) {
			final List<Filter> lf = this.searchObj.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / " + filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj,this.listBoxFinanceReferenceDetail,this.pagingFinanceReferenceDetailList);
		logger.debug("Leaving" );
	}
}
	
	
	
	
	
	
	
	/**
	 * When the financeReferenceDetail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_FinanceReferenceDetailList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTReportUtils.getReport("FinanceReferenceDetail", getSearchObj());
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setFinanceReferenceDetailService(FinanceReferenceDetailService financeReferenceDetailService) {
		this.financeReferenceDetailService = financeReferenceDetailService;
	}
	public FinanceReferenceDetailService getFinanceReferenceDetailService() {
		return this.financeReferenceDetailService;
	}

	public JdbcSearchObject<FinanceType> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<FinanceType> searchObj) {
		this.searchObj = searchObj;
	}
	
	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}
}