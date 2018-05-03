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
 * FileName    		:  FacilityReferenceDetailListCtrl.java                                                   * 	  
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
package com.pennant.webui.lmtmasters.facilityreferencedetail;

import java.util.Map;

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
import com.pennant.backend.model.finance.CAFFacilityType;
import com.pennant.backend.model.lmtmasters.FacilityReference;
import com.pennant.backend.model.lmtmasters.FacilityReferenceDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.lmtmasters.FacilityReferenceDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.webui.lmtmasters.financereferencedetail.model.FacilityReferenceDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/FacilityReferenceDetail/FacilityReferenceDetailList.zul file.
 */
public class FacilityReferenceDetailListCtrl extends GFCBaseListCtrl<CAFFacilityType> {
	private static final long serialVersionUID = 5574042632591594715L;
	private static final Logger logger = Logger.getLogger(FacilityReferenceDetailListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_FacilityReferenceDetailList; 			// auto wired
	protected Borderlayout 	borderLayout_FacilityReferenceDetailList; 	// auto wired
	public Paging 		pagingFacilityReferenceDetailList; 			// auto wired
	public Listbox 		listBoxFacilityReferenceDetail; 				// auto wired

	protected Textbox  facilityType;
	protected Listbox  sortOperator_facilityType;
	protected Textbox  facilityTypeDesc;
	protected Listbox  sortOperator_facilityTypeDesc;
	
	// List headers
	protected Listheader listheader_FacilityType; 		// auto wired
	protected Listheader listheader_FacilityTypeDesc; 	// auto wired

	// checkRights
	protected Button btnHelp; 																// auto wired
	protected Button button_FacilityReferenceDetailList_NewFacilityReferenceDetail; 			// auto wired
	protected Button button_FacilityReferenceDetailList_FacilityReferenceDetailSearchDialog; 	// auto wired
	protected Button button_FacilityReferenceDetailList_PrintList; 							// auto wired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CAFFacilityType> searchObj;
	private transient FacilityReferenceDetailService facilityReferenceDetailService;
	private PagedListService pagedListService;
	
	/**
	 * default constructor.<br>
	 */
	public FacilityReferenceDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		moduleCode = "FacilityReferenceDetail";
	}

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected FinanceCheckList object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FacilityReferenceDetailList(Event event) throws Exception {
		logger.debug("Entering");
		
		// DropDown ListBox
		this.sortOperator_facilityType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_facilityType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_facilityTypeDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_facilityTypeDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());
				
			
		/* set components visible dependent on the users rights */
		doCheckRights();
		
		this.borderLayout_FacilityReferenceDetailList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingFacilityReferenceDetailList.setPageSize(getListRows());
		this.pagingFacilityReferenceDetailList.setDetailed(true);

		this.listheader_FacilityType.setSortAscending(new FieldComparator("FacilityType", true));
		this.listheader_FacilityType.setSortDescending(new FieldComparator("FacilityType", false));
		this.listheader_FacilityTypeDesc.setSortAscending(new FieldComparator("FacilityDesc", true));
		this.listheader_FacilityTypeDesc.setSortDescending(new FieldComparator("FacilityDesc", false));
		
		doSearch();
		// set the itemRenderer
		this.listBoxFacilityReferenceDetail.setItemRenderer(new FacilityReferenceDetailListModelItemRenderer());
		
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("FacilityReferenceDetailList");

		/*this.button_FacilityReferenceDetailList_NewFacilityReferenceDetail.setVisible(
				getUserWorkspace().isAllowed("button_FacilityReferenceDetailList_NewFacilityReferenceDetail"));*/
		this.button_FacilityReferenceDetailList_NewFacilityReferenceDetail.setVisible(false);
		this.button_FacilityReferenceDetailList_FacilityReferenceDetailSearchDialog.setVisible(
				getUserWorkspace().isAllowed("button_FacilityReferenceDetailList_FacilityReferenceDetailFindDialog"));
		this.button_FacilityReferenceDetailList_PrintList.setVisible(false);//getUserWorkspace().isAllowed("button_FacilityReferenceDetailList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.lmtmasters.financereferencedetail.model.
	 * FacilityReferenceDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onFacilityReferenceDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected FacilityReferenceDetail object
		final Listitem item = this.listBoxFacilityReferenceDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			FacilityReference aFacilityReference = new FacilityReference();
			final CAFFacilityType aFacilityType = (CAFFacilityType) item.getAttribute("data");

			final FacilityReference facilityReference = getFacilityReferenceDetailService().getFacilityReference(
					aFacilityType.getFacilityType());
			if(facilityReference ==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=String.valueOf(aFacilityReference.getFinType());
				errParm[0]=PennantJavaUtil.getLabel("label_FinType")+":"+valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
			}
			if(facilityReference.getLovDescWorkFlowRolesName()==null || StringUtils.isEmpty(facilityReference.getLovDescWorkFlowRolesName())){
				MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
			}else{
				facilityReference.setLovDescFinTypeDescName(aFacilityType.getFacilityDesc());
			showDetailView(facilityReference);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the FacilityReferenceDetail dialog with a new empty entry. <br>
	 */
	public void onClick$button_FacilityReferenceDetailList_NewFacilityReferenceDetail(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new FacilityReferenceDetail object, We GET it from the backEnd.
		FacilityReference aFacilityReference = new FacilityReference();
		showDetailView(aFacilityReference);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param FacilityReferenceDetail (aFacilityReferenceDetail)
	 * @throws Exception
	 */
	private void showDetailView(FacilityReference aFacilityReference) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		FacilityReferenceDetail facilityReferenceDetail = getFacilityReferenceDetailService().getNewFacilityReferenceDetail();
		facilityReferenceDetail.setWorkflowId(0);
		Map<String, Object> map = getDefaultArguments();
		map.put("facilityReference", aFacilityReference);
		map.put("facilityReferenceDetail", facilityReferenceDetail);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the FacilityReferenceDetailListbox from the
		 * dialog when we do a delete, edit or insert a FacilityReferenceDetail.
		 */
		map.put("facilityReferenceDetailListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SolutionFactory/FacilityReferenceDetail/FacilityReferenceDetailDialog.zul",null,map);
		} catch (Exception e) {
			MessageUtil.showError(e);
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
		MessageUtil.showHelpWindow(event, window_FacilityReferenceDetailList);
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
		this.pagingFacilityReferenceDetailList.setActivePage(0);
		this.sortOperator_facilityType.setSelectedIndex(0);
		this.facilityType.setValue("");
		this.sortOperator_facilityTypeDesc.setSelectedIndex(0);
		this.facilityTypeDesc.setValue("");
		this.pagingFacilityReferenceDetailList.setActivePage(0);
		Events.postEvent("onCreate", this.window_FacilityReferenceDetailList, event);
		this.window_FacilityReferenceDetailList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Call the FacilityReferenceDetail dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_FacilityReferenceDetailList_FacilityReferenceDetailSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving");
	}

	
	public void doSearch(){
		logger.debug("Entering");
		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<CAFFacilityType>(CAFFacilityType.class,getListRows());
		this.searchObj.addSort("FacilityType", false);
		this.searchObj.addSort("FacilityDesc", false);
		
		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("CAFFacilityTypes");//TODO RMTFacilityTypes_View
			if (isFirstTask()) {
				button_FacilityReferenceDetailList_NewFacilityReferenceDetail.setVisible(true);
			} else {
				button_FacilityReferenceDetailList_NewFacilityReferenceDetail.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("CAFFacilityTypes");//TODO RMTFacilityTypes_AView
		}

		// Facility Type  
		if (StringUtils.isNotBlank(this.facilityType.getValue())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_facilityType.getSelectedItem(), this.facilityType.getValue(), "facilityType");
		}
		
		// Facility Type Desc
		if (StringUtils.isNotBlank(this.facilityTypeDesc.getValue())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_facilityTypeDesc.getSelectedItem(), this.facilityTypeDesc.getValue(), "facilityDesc");
		}

		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj,this.listBoxFacilityReferenceDetail,this.pagingFacilityReferenceDetailList);
		logger.debug("Leaving" );
}
	
	
	/**
	 * When the facilityReferenceDetail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_FacilityReferenceDetailList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		new PTListReportUtils("FacilityReferenceDetail", getSearchObj(),this.pagingFacilityReferenceDetailList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public void setFacilityReferenceDetailService(FacilityReferenceDetailService facilityReferenceDetailService) {
		this.facilityReferenceDetailService = facilityReferenceDetailService;
	}
	public FacilityReferenceDetailService getFacilityReferenceDetailService() {
		return this.facilityReferenceDetailService;
	}

	public JdbcSearchObject<CAFFacilityType> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<CAFFacilityType> searchObj) {
		this.searchObj = searchObj;
	}
	
	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}
}