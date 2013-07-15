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
 * FileName    		:  DirectorDetailListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-12-2011    														*
 *                                                                  						*
 * Modified Date    :  01-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.customermasters.directordetail;

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
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.customermasters.DirectorDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.search.SearchResult;
import com.pennant.webui.customermasters.directordetail.model.CustomerDirectorComparator;
import com.pennant.webui.customermasters.directordetail.model.DirectorDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the 
 * /WEB-INF/pages/CustomerMasters/DirectorDetail/DirectorDetailList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class DirectorDetailListCtrl extends GFCBaseListCtrl<DirectorDetail> implements Serializable {

	private static final long serialVersionUID = -5634641691791820344L;
	private final static Logger logger = Logger.getLogger(DirectorDetailListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_DirectorDetailList; 			// autowired
	protected Borderlayout 	borderLayout_DirectorDetailList; 	// autowired
	protected Paging 		pagingDirectorDetailList; 			// autowired
	protected Listbox 		listBoxDirectorDetail; 				// autowired

	// List headers
	protected Listheader listheader_FirstName; 			// autowired
	protected Listheader listheader_ShortName; 			// autowired
	protected Listheader listheader_CustGenderCode; 	// autowired
	protected Listheader listheader_CustSalutationCode; // autowired
	protected Listheader listheader_RecordStatus; 		// autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 												// autowired
	protected Button button_DirectorDetailList_NewDirectorDetail; 			// autowired
	protected Button button_DirectorDetailList_DirectorDetailSearchDialog; 	// autowired
	protected Button button_DirectorDetailList_PrintList; 					// autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<DirectorDetail> searchObj;
	private transient PagedListService pagedListService;
	private transient DirectorDetailService directorDetailService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public DirectorDetailListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected DirectorDetail object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_DirectorDetailList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("DirectorDetail");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("DirectorDetail");
			
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
		
		/* set components visible dependent on the users rights */
		doCheckRights();
		
		/**
		 * Calculate how many rows have been place in the listBox. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_DirectorDetailList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingDirectorDetailList.setPageSize(getListRows());
		this.pagingDirectorDetailList.setDetailed(true);

		this.listheader_FirstName.setSortAscending(new FieldComparator("firstName", true));
		this.listheader_FirstName.setSortDescending(new FieldComparator("firstName", false));
		this.listheader_ShortName.setSortAscending(new FieldComparator("shortName", true));
		this.listheader_ShortName.setSortDescending(new FieldComparator("shortName", false));
		this.listheader_CustGenderCode.setSortAscending(new FieldComparator("custGenderCode", true));
		this.listheader_CustGenderCode.setSortDescending(new FieldComparator("custGenderCode", false));
		this.listheader_CustSalutationCode.setSortAscending(new FieldComparator("custSalutationCode", true));
		this.listheader_CustSalutationCode.setSortDescending(new FieldComparator("custSalutationCode", false));
		
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
		this.searchObj = new JdbcSearchObject<DirectorDetail>(DirectorDetail.class,getListRows());
		this.searchObj.addSort("DirectorId", false);
		this.searchObj.addFilter(new Filter("lovDescCustRecordType", 
				PennantConstants.RECORD_TYPE_NEW, Filter.OP_NOT_EQUAL));
		
		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("CustomerDirectorDetail_View");
			if (isFirstTask()) {
				button_DirectorDetailList_NewDirectorDetail.setVisible(true);
			} else {
				button_DirectorDetailList_NewDirectorDetail.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("CustomerDirectorDetail_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_DirectorDetailList_NewDirectorDetail.setVisible(false);
			this.button_DirectorDetailList_DirectorDetailSearchDialog.setVisible(false);
			this.button_DirectorDetailList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			findSearchObject();
			// set the itemRenderer
			this.listBoxDirectorDetail.setItemRenderer(new DirectorDetailListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Internal Method for Grouping List items
	 */
	public void findSearchObject(){
		logger.debug("Entering");
		final SearchResult<DirectorDetail> searchResult = getPagedListService().getSRBySearchObject(
				this.searchObj);
		listBoxDirectorDetail.setModel(new GroupsModelArray(
				searchResult.getResult().toArray(),new CustomerDirectorComparator()));
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("DirectorDetailList");
		
		this.button_DirectorDetailList_NewDirectorDetail.setVisible(getUserWorkspace().
				isAllowed("button_DirectorDetailList_NewDirectorDetail"));
		this.button_DirectorDetailList_DirectorDetailSearchDialog.setVisible(getUserWorkspace().
				isAllowed("button_DirectorDetailList_DirectorDetailFindDialog"));
		this.button_DirectorDetailList_PrintList.setVisible(getUserWorkspace().
				isAllowed("button_DirectorDetailList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.customermasters.directordetail.model.
	 * DirectorDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onDirectorDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected DirectorDetail object
		final Listitem item = this.listBoxDirectorDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final DirectorDetail aDirectorDetail = (DirectorDetail) item.getAttribute("data");
			final DirectorDetail directorDetail = getDirectorDetailService().getDirectorDetailById(
					aDirectorDetail.getId());
			
			if(directorDetail==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=String.valueOf(aDirectorDetail.getId());
				errParm[0]=PennantJavaUtil.getLabel("label_DirectorId")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD,"41005", 
								errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND DirectorId="+ directorDetail.getDirectorId()+
												" AND version=" + directorDetail.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), 
							"DirectorDetail", whereCond, directorDetail.getTaskId(), 
							directorDetail.getNextTaskId());
					if (userAcces){
						showDetailView(directorDetail);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(directorDetail);
				}
			}	
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the DirectorDetail dialog with a new empty entry. <br>
	 */
	public void onClick$button_DirectorDetailList_NewDirectorDetail(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new DirectorDetail object, We GET it from the backEnd.
		final DirectorDetail aDirectorDetail = getDirectorDetailService().getNewDirectorDetail();
		showDetailView(aDirectorDetail);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param DirectorDetail (aDirectorDetail)
	 * @throws Exception
	 */
	private void showDetailView(DirectorDetail aDirectorDetail) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aDirectorDetail.getWorkflowId()==0 && isWorkFlowEnabled()){
			aDirectorDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("directorDetail", aDirectorDetail);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the DirectorDetailListbox from the
		 * dialog when we do a delete, edit or insert a DirectorDetail.
		 */
		map.put("directorDetailListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/DirectorDetail/DirectorDetailDialog.zul",
							null,map);
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
		PTMessageUtils.showHelpWindow(event, window_DirectorDetailList);
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
		this.pagingDirectorDetailList.setActivePage(0);
		Events.postEvent("onCreate", this.window_DirectorDetailList, event);
		this.window_DirectorDetailList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for calling the DirectorDetail dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_DirectorDetailList_DirectorDetailSearchDialog(Event event) 
							throws Exception {
		logger.debug("Entering" + event.toString());
		
		/*
		 * we can call our DirectorDetailDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected DirectorDetail. For handed over
		 * these parameter only a Map is accepted. So we put the DirectorDetail object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("directorDetailCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/DirectorDetail/DirectorDetailSearchDialog.zul",
								null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the directorDetail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_DirectorDetailList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTReportUtils.getReport("DirectorDetail", getSearchObj());
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setDirectorDetailService(DirectorDetailService directorDetailService) {
		this.directorDetailService = directorDetailService;
	}
	public DirectorDetailService getDirectorDetailService() {
		return this.directorDetailService;
	}

	public JdbcSearchObject<DirectorDetail> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<DirectorDetail> searchObj) {
		this.searchObj = searchObj;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}
}