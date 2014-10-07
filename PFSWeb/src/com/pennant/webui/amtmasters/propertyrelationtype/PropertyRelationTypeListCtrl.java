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
 * FileName    		:  PropertyRelationTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-09-2011    														*
 *                                                                  						*
 * Modified Date    :  29-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-09-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.amtmasters.propertyrelationtype;


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
import com.pennant.backend.model.amtmasters.PropertyRelationType;
import com.pennant.backend.service.amtmasters.PropertyRelationTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.amtmasters.propertyrelationtype.model.PropertyRelationTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/AMTMasters/PropertyRelationType/PropertyRelationTypeList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class PropertyRelationTypeListCtrl extends GFCBaseListCtrl<PropertyRelationType> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(PropertyRelationTypeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_PropertyRelationTypeList; // autowired
	protected Borderlayout borderLayout_PropertyRelationTypeList; // autowired
	protected Paging pagingPropertyRelationTypeList; // autowired
	protected Listbox listBoxPropertyRelationType; // autowired

	// List headers
	protected Listheader listheader_PropertyRelationTypeName; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_PropertyRelationTypeList_NewPropertyRelationType; // autowired
	protected Button button_PropertyRelationTypeList_PropertyRelationTypeSearchDialog; // autowired
	protected Button button_PropertyRelationTypeList_PrintList; // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<PropertyRelationType> searchObj;
	
	private transient PropertyRelationTypeService propertyRelationTypeService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public PropertyRelationTypeListCtrl() {
		super();
	}

	public void onCreate$window_PropertyRelationTypeList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("PropertyRelationType");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("PropertyRelationType");
			
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
		
		this.borderLayout_PropertyRelationTypeList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingPropertyRelationTypeList.setPageSize(getListRows());
		this.pagingPropertyRelationTypeList.setDetailed(true);

		this.listheader_PropertyRelationTypeName.setSortAscending(new FieldComparator("propertyRelationTypeName", true));
		this.listheader_PropertyRelationTypeName.setSortDescending(new FieldComparator("propertyRelationTypeName", false));
		
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
		this.searchObj = new JdbcSearchObject<PropertyRelationType>(PropertyRelationType.class,getListRows());
		this.searchObj.addSort("PropertyRelationTypeId", false);

		this.searchObj.addTabelName("AMTPropertyRelationType_View");
		
		// Workflow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_PropertyRelationTypeList_NewPropertyRelationType.setVisible(true);
			} else {
				button_PropertyRelationTypeList_NewPropertyRelationType.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_PropertyRelationTypeList_NewPropertyRelationType.setVisible(false);
			this.button_PropertyRelationTypeList_PropertyRelationTypeSearchDialog.setVisible(false);
			this.button_PropertyRelationTypeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxPropertyRelationType,this.pagingPropertyRelationTypeList);
			// set the itemRenderer
			this.listBoxPropertyRelationType.setItemRenderer(new PropertyRelationTypeListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("PropertyRelationTypeList");
		
		this.button_PropertyRelationTypeList_NewPropertyRelationType.setVisible(getUserWorkspace().isAllowed("button_PropertyRelationTypeList_NewPropertyRelationType"));
		this.button_PropertyRelationTypeList_PropertyRelationTypeSearchDialog.setVisible(getUserWorkspace().isAllowed("button_PropertyRelationTypeList_PropertyRelationTypeFindDialog"));
		this.button_PropertyRelationTypeList_PrintList.setVisible(getUserWorkspace().isAllowed("button_PropertyRelationTypeList_PrintList"));
	logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.amtmaster.propertyrelationtype.model.PropertyRelationTypeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onPropertyRelationTypeItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected PropertyRelationType object
		final Listitem item = this.listBoxPropertyRelationType.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final PropertyRelationType aPropertyRelationType = (PropertyRelationType) item.getAttribute("data");
			final PropertyRelationType propertyRelationType = getPropertyRelationTypeService().getPropertyRelationTypeById(aPropertyRelationType.getId());
			
			if(propertyRelationType==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=String.valueOf(aPropertyRelationType.getId());
				errParm[0]=PennantJavaUtil.getLabel("label_PropertyRelationTypeId")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND PropertyRelationTypeId="+ propertyRelationType.getPropertyRelationTypeId()+" AND version=" + propertyRelationType.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "PropertyRelationType", whereCond, propertyRelationType.getTaskId(), propertyRelationType.getNextTaskId());
					if (userAcces){
						showDetailView(propertyRelationType);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(propertyRelationType);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the PropertyRelationType dialog with a new empty entry. <br>
	 */
	public void onClick$button_PropertyRelationTypeList_NewPropertyRelationType(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new PropertyRelationType object, We GET it from the backend.
		final PropertyRelationType aPropertyRelationType = getPropertyRelationTypeService().getNewPropertyRelationType();
		showDetailView(aPropertyRelationType);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param PropertyRelationType (aPropertyRelationType)
	 * @throws Exception
	 */
	private void showDetailView(PropertyRelationType aPropertyRelationType) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aPropertyRelationType.getWorkflowId()==0 && isWorkFlowEnabled()){
			aPropertyRelationType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("propertyRelationType", aPropertyRelationType);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the PropertyRelationTypeListbox from the
		 * dialog when we do a delete, edit or insert a PropertyRelationType.
		 */
		map.put("propertyRelationTypeListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/AMTMasters/PropertyRelationType/PropertyRelationTypeDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_PropertyRelationTypeList);
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
		this.pagingPropertyRelationTypeList.setActivePage(0);
		Events.postEvent("onCreate", this.window_PropertyRelationTypeList, event);
		this.window_PropertyRelationTypeList.invalidate();
		logger.debug("Leaving");
	}

	/*
	 * call the PropertyRelationType dialog
	 */
	
	public void onClick$button_PropertyRelationTypeList_PropertyRelationTypeSearchDialog(Event event) throws Exception {
		logger.debug("Entering");
		logger.debug(event.toString());
		/*
		 * we can call our PropertyRelationTypeDialog zul-file with parameters. So we can
		 * call them with a object of the selected PropertyRelationType. For handed over
		 * these parameter only a Map is accepted. So we put the PropertyRelationType object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("propertyRelationTypeCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/AMTMasters/PropertyRelationType/PropertyRelationTypeSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * When the propertyRelationType print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_PropertyRelationTypeList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		new PTListReportUtils("PropertyRelationType", getSearchObj(),this.pagingPropertyRelationTypeList.getTotalSize()+1);
		logger.debug("Leaving");
	}

	public void setPropertyRelationTypeService(PropertyRelationTypeService propertyRelationTypeService) {
		this.propertyRelationTypeService = propertyRelationTypeService;
	}

	public PropertyRelationTypeService getPropertyRelationTypeService() {
		return this.propertyRelationTypeService;
	}

	public JdbcSearchObject<PropertyRelationType> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<PropertyRelationType> searchObj) {
		this.searchObj = searchObj;
	}
}