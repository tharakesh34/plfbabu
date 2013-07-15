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
 * FileName    		:  PropertyDetailListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-09-2011    														*
 *                                                                  						*
 * Modified Date    :  30-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-09-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.amtmasters.propertydetail;


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
import com.pennant.backend.model.amtmasters.PropertyDetail;
import com.pennant.backend.service.amtmasters.PropertyDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.amtmasters.propertydetail.model.PropertyDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/AMTMasters/PropertyDetail/PropertyDetailList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class PropertyDetailListCtrl extends GFCBaseListCtrl<PropertyDetail> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(PropertyDetailListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_PropertyDetailList; // autowired
	protected Borderlayout borderLayout_PropertyDetailList; // autowired
	protected Paging pagingPropertyDetailList; // autowired
	protected Listbox listBoxPropertyDetail; // autowired

	// List headers
	protected Listheader listheader_PropertyDetailDesc; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_PropertyDetailList_NewPropertyDetail; // autowired
	protected Button button_PropertyDetailList_PropertyDetailSearchDialog; // autowired
	protected Button button_PropertyDetailList_PrintList; // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<PropertyDetail> searchObj;
	
	private transient PropertyDetailService propertyDetailService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public PropertyDetailListCtrl() {
		super();
	}

	public void onCreate$window_PropertyDetailList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("PropertyDetail");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("PropertyDetail");
			
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
		
		this.borderLayout_PropertyDetailList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingPropertyDetailList.setPageSize(getListRows());
		this.pagingPropertyDetailList.setDetailed(true);

		this.listheader_PropertyDetailDesc.setSortAscending(new FieldComparator("propertyDetailDesc", true));
		this.listheader_PropertyDetailDesc.setSortDescending(new FieldComparator("propertyDetailDesc", false));
		
		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}
		
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<PropertyDetail>(PropertyDetail.class,getListRows());
		this.searchObj.addSort("PropertyDetailId", false);

		this.searchObj.addTabelName("AMTPropertyDetail_View");
		
		// Workflow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_PropertyDetailList_NewPropertyDetail.setVisible(true);
			} else {
				button_PropertyDetailList_NewPropertyDetail.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_PropertyDetailList_NewPropertyDetail.setVisible(false);
			this.button_PropertyDetailList_PropertyDetailSearchDialog.setVisible(false);
			this.button_PropertyDetailList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxPropertyDetail,this.pagingPropertyDetailList);
			// set the itemRenderer
			this.listBoxPropertyDetail.setItemRenderer(new PropertyDetailListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("PropertyDetailList");
		
		this.button_PropertyDetailList_NewPropertyDetail.setVisible(getUserWorkspace().isAllowed("button_PropertyDetailList_NewPropertyDetail"));
		this.button_PropertyDetailList_PropertyDetailSearchDialog.setVisible(getUserWorkspace().isAllowed("button_PropertyDetailList_PropertyDetailFindDialog"));
		this.button_PropertyDetailList_PrintList.setVisible(getUserWorkspace().isAllowed("button_PropertyDetailList_PrintList"));
	logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.amtmaster.propertydetail.model.PropertyDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onPropertyDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected PropertyDetail object
		final Listitem item = this.listBoxPropertyDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final PropertyDetail aPropertyDetail = (PropertyDetail) item.getAttribute("data");
			final PropertyDetail propertyDetail = getPropertyDetailService().getPropertyDetailById(aPropertyDetail.getId());
			
			if(propertyDetail==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=String.valueOf(aPropertyDetail.getId());
				errParm[0]=PennantJavaUtil.getLabel("label_PropertyDetailId")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND PropertyDetailId="+ propertyDetail.getPropertyDetailId()+" AND version=" + propertyDetail.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "PropertyDetail", whereCond, propertyDetail.getTaskId(), propertyDetail.getNextTaskId());
					if (userAcces){
						showDetailView(propertyDetail);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(propertyDetail);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the PropertyDetail dialog with a new empty entry. <br>
	 */
	public void onClick$button_PropertyDetailList_NewPropertyDetail(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new PropertyDetail object, We GET it from the backend.
		final PropertyDetail aPropertyDetail = getPropertyDetailService().getNewPropertyDetail();
		showDetailView(aPropertyDetail);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param PropertyDetail (aPropertyDetail)
	 * @throws Exception
	 */
	private void showDetailView(PropertyDetail aPropertyDetail) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aPropertyDetail.getWorkflowId()==0 && isWorkFlowEnabled()){
			aPropertyDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("propertyDetail", aPropertyDetail);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the PropertyDetailListbox from the
		 * dialog when we do a delete, edit or insert a PropertyDetail.
		 */
		map.put("propertyDetailListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/AMTMasters/PropertyDetail/PropertyDetailDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_PropertyDetailList);
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
		this.pagingPropertyDetailList.setActivePage(0);
		Events.postEvent("onCreate", this.window_PropertyDetailList, event);
		this.window_PropertyDetailList.invalidate();
		logger.debug("Leaving");
	}

	/*
	 * call the PropertyDetail dialog
	 */
	
	public void onClick$button_PropertyDetailList_PropertyDetailSearchDialog(Event event) throws Exception {
		logger.debug("Entering");
		logger.debug(event.toString());
		/*
		 * we can call our PropertyDetailDialog zul-file with parameters. So we can
		 * call them with a object of the selected PropertyDetail. For handed over
		 * these parameter only a Map is accepted. So we put the PropertyDetail object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("propertyDetailCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/AMTMasters/PropertyDetail/PropertyDetailSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * When the propertyDetail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_PropertyDetailList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		PTReportUtils.getReport("PropertyDetail", getSearchObj());
		logger.debug("Leaving");
	}

	public void setPropertyDetailService(PropertyDetailService propertyDetailService) {
		this.propertyDetailService = propertyDetailService;
	}

	public PropertyDetailService getPropertyDetailService() {
		return this.propertyDetailService;
	}

	public JdbcSearchObject<PropertyDetail> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<PropertyDetail> searchObj) {
		this.searchObj = searchObj;
	}
}