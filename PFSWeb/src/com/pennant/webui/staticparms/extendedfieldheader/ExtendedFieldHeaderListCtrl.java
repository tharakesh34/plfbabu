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
 * FileName    		:  ExtendedFieldHeaderListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  28-12-2011    														*
 *                                                                  						*
 * Modified Date    :  28-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 28-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.staticparms.extendedfieldheader;


import java.io.Serializable;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
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
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.staticparms.ExtendedFieldHeader;
import com.pennant.backend.service.staticparms.ExtendedFieldHeaderService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.staticparms.extendedfieldheader.model.ExtendedFieldHeaderListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/SystemMasters/ExtendedFieldHeader/ExtendedFieldHeaderList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class ExtendedFieldHeaderListCtrl extends GFCBaseListCtrl<ExtendedFieldHeader> implements Serializable {

	private static final long	serialVersionUID	= -1751614637216289000L;
	private final static Logger logger = Logger.getLogger(ExtendedFieldHeaderListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_ExtendedFieldHeaderList; 		// autowired
	protected Borderlayout	borderLayout_ExtendedFieldHeaderList; 	// autowired
	protected Paging 		pagingExtendedFieldHeaderList; 			// autowired
	protected Listbox 		listBoxExtendedFieldHeader; 			// autowired

	// List headers
	protected Listheader listheader_ModuleName; 		// autowired
	protected Listheader listheader_SubModuleName; 		// autowired
	protected Listheader listheader_TabHeading; 		// autowired
	protected Listheader listheader_NumberOfColumns; 	// autowired
	protected Listheader listheader_RecordStatus; 		// autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 															// autowired
	protected Button button_ExtendedFieldHeaderList_NewExtendedFieldHeader; 			// autowired
	protected Button button_ExtendedFieldHeaderList_ExtendedFieldHeaderSearchDialog; 	// autowired
	protected Button button_ExtendedFieldHeaderList_PrintList; 							// autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<ExtendedFieldHeader> searchObj;
	
	private transient ExtendedFieldHeaderService extendedFieldHeaderService;
	private transient WorkFlowDetails workFlowDetails=null;
	private Tabbox						   tabbox;
	private Tab							   tab;
	
	/**
	 * default constructor.<br>
	 */
	public ExtendedFieldHeaderListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	
	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected ExtendedFeildHeader object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ExtendedFieldHeaderList(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		
		try {
			
			if(event.getTarget() != null && event.getTarget().getParent() != null 
					&& event.getTarget().getParent().getParent() != null
					&& event.getTarget().getParent().getParent().getParent() != null) {
				tabbox = (Tabbox) event.getTarget().getParent().getParent().getParent();
				tab = tabbox.getSelectedTab();
			}
			
			ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("ExtendedFieldHeader");
			boolean wfAvailable=true;

			if (moduleMapping.getWorkflowType()!=null){
				workFlowDetails = WorkFlowUtil.getWorkFlowDetails("ExtendedFieldHeader");

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

			this.borderLayout_ExtendedFieldHeaderList.setHeight(getBorderLayoutHeight());

			// set the paging parameters
			this.pagingExtendedFieldHeaderList.setPageSize(getListRows());
			this.pagingExtendedFieldHeaderList.setDetailed(true);

			this.listheader_ModuleName.setSortAscending(new FieldComparator("moduleName", true));
			this.listheader_ModuleName.setSortDescending(new FieldComparator("moduleName", false));

			this.listheader_SubModuleName.setSortAscending(new FieldComparator("subModuleName", true));
			this.listheader_SubModuleName.setSortDescending(new FieldComparator("subModuleName", false));

			this.listheader_TabHeading.setSortAscending(new FieldComparator("tabHeading", true));
			this.listheader_TabHeading.setSortDescending(new FieldComparator("tabHeading", false));

			this.listheader_NumberOfColumns.setSortAscending(new FieldComparator("numberOfColumns", true));
			this.listheader_NumberOfColumns.setSortDescending(new FieldComparator("numberOfColumns", false));

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
			this.searchObj = new JdbcSearchObject<ExtendedFieldHeader>(ExtendedFieldHeader.class,getListRows());
			this.searchObj.addSort("moduleName", false);
			this.searchObj.addSort("subModuleName", false);

			// Workflow
			if (isWorkFlowEnabled()) {
				this.searchObj.addTabelName("ExtendedFieldHeader_View");
				if (isFirstTask()) {
					button_ExtendedFieldHeaderList_NewExtendedFieldHeader.setVisible(true);
				} else {
					button_ExtendedFieldHeaderList_NewExtendedFieldHeader.setVisible(false);
				}

				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
			}else{
				this.searchObj.addTabelName("ExtendedFieldHeader_AView");
			}

			setSearchObj(this.searchObj);
			if (!isWorkFlowEnabled() && wfAvailable){
				this.button_ExtendedFieldHeaderList_NewExtendedFieldHeader.setVisible(false);
				this.button_ExtendedFieldHeaderList_ExtendedFieldHeaderSearchDialog.setVisible(false);
				this.button_ExtendedFieldHeaderList_PrintList.setVisible(false);
				PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
			}else{
				// Set the ListModel for the articles.
				getPagedListWrapper().init(this.searchObj,this.listBoxExtendedFieldHeader,this.pagingExtendedFieldHeaderList);
				// set the itemRenderer
				this.listBoxExtendedFieldHeader.setItemRenderer(new ExtendedFieldHeaderListModelItemRenderer());
			}
		} catch (Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
			tab.close();
		}
	
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("ExtendedFieldHeaderList");

		this.button_ExtendedFieldHeaderList_NewExtendedFieldHeader.setVisible(getUserWorkspace()
				.isAllowed("button_ExtendedFieldHeaderList_NewExtendedFieldHeader"));
		this.button_ExtendedFieldHeaderList_ExtendedFieldHeaderSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_ExtendedFieldHeaderList_ExtendedFieldHeaderFindDialog"));
		this.button_ExtendedFieldHeaderList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_ExtendedFieldHeaderList_PrintList"));
		
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.staticparms.extendedfieldheader.model.ExtendedFieldHeaderListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onExtendedFieldHeaderItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected ExtendedFieldHeader object
		final Listitem item = this.listBoxExtendedFieldHeader.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final ExtendedFieldHeader aExtendedFieldHeader = (ExtendedFieldHeader) item.getAttribute("data");
			final ExtendedFieldHeader extendedFieldHeader = getExtendedFieldHeaderService().getExtendedFieldHeaderById(
					aExtendedFieldHeader.getId());
			
			if(extendedFieldHeader==null){
				
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=String.valueOf(aExtendedFieldHeader.getId());
				errParm[0]=PennantJavaUtil.getLabel("label_ModuleId")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", 
						errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND ModuleId="+ extendedFieldHeader.getModuleId()+
					" AND version=" + extendedFieldHeader.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "ExtendedFieldHeader",
							whereCond, extendedFieldHeader.getTaskId(), extendedFieldHeader.getNextTaskId());
					if (userAcces){
						showDetailView(extendedFieldHeader);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					if(StringUtils.trimToEmpty(extendedFieldHeader.getNextRoleCode()).equals("")){
						showDetailView(extendedFieldHeader);
					}else{
						PTMessageUtils.showErrorMessage("Record in WorkFlow. Not allowed to Maintain.");
					}
				}
			}	
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the ExtendedFieldHeader dialog with a new empty entry. <br>
	 */
	public void onClick$button_ExtendedFieldHeaderList_NewExtendedFieldHeader(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new ExtendedFieldHeader object, We GET it from the backend.
		final ExtendedFieldHeader aExtendedFieldHeader = getExtendedFieldHeaderService().getNewExtendedFieldHeader();
		showDetailView(aExtendedFieldHeader);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param ExtendedFieldHeader (aExtendedFieldHeader)
	 * @throws Exception
	 */
	private void showDetailView(ExtendedFieldHeader aExtendedFieldHeader) throws Exception {
		logger.debug("Entering");
		
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if(aExtendedFieldHeader.getWorkflowId()==0 && isWorkFlowEnabled()){
			aExtendedFieldHeader.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("extendedFieldHeader", aExtendedFieldHeader);
		
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the ExtendedFieldHeaderListbox from the
		 * dialog when we do a delete, edit or insert a ExtendedFieldHeader.
		 */
		map.put("extendedFieldHeaderListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/StaticParms/ExtendedFieldHeader/ExtendedFieldHeaderDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_ExtendedFieldHeaderList);
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
		this.pagingExtendedFieldHeaderList.setActivePage(0);
		Events.postEvent("onCreate", this.window_ExtendedFieldHeaderList, event);
		this.window_ExtendedFieldHeaderList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Call the ExtendedFieldHeader dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_ExtendedFieldHeaderList_ExtendedFieldHeaderSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		/*
		 * we can call our ExtendedFieldHeaderDialog zul-file with parameters. So we can
		 * call them with a object of the selected ExtendedFieldHeader. For handed over
		 * these parameter only a Map is accepted. So we put the ExtendedFieldHeader object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("extendedFieldHeaderCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/StaticParms/ExtendedFieldHeader/ExtendedFieldHeaderSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the extendedFieldHeader print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_ExtendedFieldHeaderList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		new PTListReportUtils("ExtendedFieldHeader", getSearchObj(),this.pagingExtendedFieldHeaderList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setExtendedFieldHeaderService(ExtendedFieldHeaderService extendedFieldHeaderService) {
		this.extendedFieldHeaderService = extendedFieldHeaderService;
	}
	public ExtendedFieldHeaderService getExtendedFieldHeaderService() {
		return this.extendedFieldHeaderService;
	}

	public JdbcSearchObject<ExtendedFieldHeader> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<ExtendedFieldHeader> searchObj) {
		this.searchObj = searchObj;
	}
	
}