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
 * FileName    		:  ExtendedFieldDetailListCtrl.java                                                   * 	  
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

package com.pennant.webui.solutionfactory.extendedfielddetail;


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
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.staticparms.ExtendedFieldHeader;
import com.pennant.backend.service.solutionfactory.ExtendedFieldDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.solutionfactory.extendedfielddetail.model.ExtendedFieldDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMasters/ExtendedFieldDetail/ExtendedFieldDetailList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class ExtendedFieldDetailListCtrl extends GFCBaseListCtrl<ExtendedFieldHeader> implements Serializable {

	private static final long serialVersionUID = 7866684540841299572L;
	private final static Logger logger = Logger.getLogger(ExtendedFieldDetailListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_ExtendedFieldDetailList; 		// autowired
	protected Borderlayout 	borderLayout_ExtendedFieldDetailList; 	// autowired
	protected Paging 		pagingExtendedFieldDetailList; 			// autowired
	protected Listbox 		listBoxExtendedFieldDetail; 			// autowired

	// List headers
	protected Listheader listheader_FieldName; 		// autowired
	protected Listheader listheader_FieldType; 		// autowired
	protected Listheader listheader_RecordStatus; 	// autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 														// autowired
	protected Button button_ExtendedFieldDetailList_NewExtendedFieldDetail; 		// autowired
	protected Button button_ExtendedFieldDetailList_ExtendedFieldDetailSearchDialog;// autowired
	protected Button button_ExtendedFieldDetailList_PrintList; 						// autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<ExtendedFieldHeader> searchObj;
	private transient ExtendedFieldDetailService extendedFieldDetailService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	private Tabbox						   tabbox;
	private Tab							   tab;
	
	/**
	 * default constructor.<br>
	 */
	public ExtendedFieldDetailListCtrl() {
		super();
	}


	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected ExtendedFieldDetail object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ExtendedFieldDetailList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		try{
			if(event.getTarget() != null && event.getTarget().getParent() != null 
					&& event.getTarget().getParent().getParent() != null
					&& event.getTarget().getParent().getParent().getParent() != null) {
				tabbox = (Tabbox) event.getTarget().getParent().getParent().getParent();
				tab = tabbox.getSelectedTab();
			}
			
			ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("ExtendedFieldDetail");
			boolean wfAvailable=true;

			if (moduleMapping.getWorkflowType()!=null){
				workFlowDetails = WorkFlowUtil.getWorkFlowDetails("ExtendedFieldDetail");

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

			this.borderLayout_ExtendedFieldDetailList.setHeight(getBorderLayoutHeight());

			// set the paging parameters
			this.pagingExtendedFieldDetailList.setPageSize(getListRows());
			this.pagingExtendedFieldDetailList.setDetailed(true);

			this.listheader_FieldName.setSortAscending(new FieldComparator("moduleName", true));
			this.listheader_FieldName.setSortDescending(new FieldComparator("moduleName", false));
			this.listheader_FieldType.setSortAscending(new FieldComparator("subModuleName", true));
			this.listheader_FieldType.setSortDescending(new FieldComparator("subModuleName", false));


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
			this.searchObj = new JdbcSearchObject<ExtendedFieldHeader>(ExtendedFieldHeader.class,getListRows());
			this.searchObj.addSort("moduleName", false);
			this.searchObj.addSort("subModuleName", false);

			// WorkFlow
			if (isWorkFlowEnabled()) {
				this.searchObj.addTabelName("ExtendedFieldHeader_View");
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
			}else{
				this.searchObj.addTabelName("ExtendedFieldHeader_AView");
			}

			setSearchObj(this.searchObj);
			if (!isWorkFlowEnabled() && wfAvailable){
				//this.button_ExtendedFieldDetailList_NewExtendedFieldDetail.setVisible(false);
				this.button_ExtendedFieldDetailList_ExtendedFieldDetailSearchDialog.setVisible(false);
				this.button_ExtendedFieldDetailList_PrintList.setVisible(false);
				PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
			}else{
				// Set the ListModel for the articles.
				getPagedListWrapper().init(this.searchObj,this.listBoxExtendedFieldDetail,this.pagingExtendedFieldDetailList);
				// set the itemRenderer
				this.listBoxExtendedFieldDetail.setItemRenderer(new ExtendedFieldDetailListModelItemRenderer());
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
		getUserWorkspace().alocateAuthorities("ExtendedFieldDetailList");
		this.button_ExtendedFieldDetailList_ExtendedFieldDetailSearchDialog.setVisible(
				getUserWorkspace().isAllowed("button_ExtendedFieldDetailList_ExtendedFieldDetailFindDialog"));
		this.button_ExtendedFieldDetailList_PrintList.setVisible(
				getUserWorkspace().isAllowed("button_ExtendedFieldDetailList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.solutionfactory.extendedfielddetail.model.ExtendedFieldDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onExtendedFieldDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected ExtendedFieldDetail object
		final Listitem item = this.listBoxExtendedFieldDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			ExtendedFieldHeader aExtendedFieldHeader  = (ExtendedFieldHeader) item.getAttribute("data");
			aExtendedFieldHeader = getExtendedFieldDetailService().getExtendedFieldHeaderById(
					aExtendedFieldHeader);
			showDetailView(aExtendedFieldHeader);

		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param ExtendedFieldDetail (aExtendedFieldDetail)
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
		 * fine for synchronizing the data in the ExtendedFieldDetailListbox from the
		 * dialog when we do a delete, edit or insert a ExtendedFieldDetail.
		 */
		map.put("extendedFieldDetailListCtrl", this);
		map.put("moduleid", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/ExtendedFieldDetail/ExtendedFieldDialog.zul",
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
		PTMessageUtils.showHelpWindow(event, window_ExtendedFieldDetailList);
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
		this.pagingExtendedFieldDetailList.setActivePage(0);
		Events.postEvent("onCreate", this.window_ExtendedFieldDetailList, event);
		this.window_ExtendedFieldDetailList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Call the ExtendedFieldDetail dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_ExtendedFieldDetailList_ExtendedFieldDetailSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		/*
		 * we can call our ExtendedFieldDetailDialog zul-file with parameters. So we can
		 * call them with a object of the selected ExtendedFieldDetail. For handed over
		 * these parameter only a Map is accepted. So we put the ExtendedFieldDetail object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("extendedFieldDetailCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/ExtendedFieldDetail/ExtendedFieldDetailSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the extendedFieldDetail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_ExtendedFieldDetailList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTReportUtils.getReport("ExtendedFieldDetail", getSearchObj());
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setExtendedFieldDetailService(ExtendedFieldDetailService extendedFieldDetailService) {
		this.extendedFieldDetailService = extendedFieldDetailService;
	}
	public ExtendedFieldDetailService getExtendedFieldDetailService() {
		return this.extendedFieldDetailService;
	}

	public JdbcSearchObject<ExtendedFieldHeader> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<ExtendedFieldHeader> searchObj) {
		this.searchObj = searchObj;
	}
}