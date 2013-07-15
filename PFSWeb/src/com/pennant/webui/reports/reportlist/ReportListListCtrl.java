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
 * FileName    		:  ReportListListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-01-2012    														*
 *                                                                  						*
 * Modified Date    :  23-01-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-01-2012       Pennant	                 0.1                                            * 
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

package com.pennant.webui.reports.reportlist;

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
import com.pennant.backend.model.reports.ReportList;
import com.pennant.backend.service.reports.ReportListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.reports.reportlist.model.ReportListListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Reports/ReportList/ReportListList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class ReportListListCtrl extends GFCBaseListCtrl<ReportList> implements Serializable {

	private static final long serialVersionUID = 2474591726313352697L;
	private final static Logger logger = Logger.getLogger(ReportListListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_ReportListList; 			// autoWired
	protected Borderlayout 	borderLayout_ReportListList; 	// autoWired
	protected Paging 		pagingReportListList; 			// autoWired
	protected Listbox 		listBoxReportList; 				// autoWired

	// List headers
	protected Listheader listheader_Module; 				// autoWired
	protected Listheader listheader_ReportFileName; 		// autoWired
	protected Listheader listheader_ReportHeading; 			// autoWired
	protected Listheader listheader_ModuleType; 			// autoWired
	protected Listheader listheader_RecordStatus; 			// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 										// autoWired
	protected Button button_ReportListList_NewReportList; 			// autoWired
	protected Button button_ReportListList_ReportListSearchDialog; 	// autoWired
	protected Button button_ReportListList_PrintList; 				// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<ReportList> searchObj;
	
	private transient ReportListService reportListService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public ReportListListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected ReportList object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ReportListList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("ReportList");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("ReportList");
			
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
		
		this.borderLayout_ReportListList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingReportListList.setPageSize(getListRows());
		this.pagingReportListList.setDetailed(true);

		this.listheader_Module.setSortAscending(new FieldComparator("module", true));
		this.listheader_Module.setSortDescending(new FieldComparator("module", false));
		this.listheader_ReportFileName.setSortAscending(new FieldComparator("reportFileName", true));
		this.listheader_ReportFileName.setSortDescending(new FieldComparator("reportFileName", false));
		this.listheader_ReportHeading.setSortAscending(new FieldComparator("reportHeading", true));
		this.listheader_ReportHeading.setSortDescending(new FieldComparator("reportHeading", false));
		this.listheader_ModuleType.setSortAscending(new FieldComparator("moduleType", true));
		this.listheader_ModuleType.setSortDescending(new FieldComparator("moduleType", false));
		
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
		this.searchObj = new JdbcSearchObject<ReportList>(ReportList.class,getListRows());
		this.searchObj.addSort("Module", false);
		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("ReportList_View");
			if (isFirstTask()) {
				button_ReportListList_NewReportList.setVisible(true);
			} else {
				button_ReportListList_NewReportList.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("ReportList_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_ReportListList_NewReportList.setVisible(false);
			this.button_ReportListList_ReportListSearchDialog.setVisible(false);
			this.button_ReportListList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxReportList,this.pagingReportListList);
			// set the itemRenderer
			this.listBoxReportList.setItemRenderer(new ReportListListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("ReportListList");
		
		this.button_ReportListList_NewReportList.setVisible(getUserWorkspace()
				.isAllowed("button_ReportListList_NewReportList"));
		this.button_ReportListList_ReportListSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_ReportListList_ReportListFindDialog"));
		this.button_ReportListList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_ReportListList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.reports.reportlist.model.ReportListListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onReportListItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected ReportList object
		final Listitem item = this.listBoxReportList.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final ReportList aReportList = (ReportList) item.getAttribute("data");
			final ReportList reportList = getReportListService().getReportListById(aReportList.getId());
			
			if(reportList==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aReportList.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_Module")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND Module='"+ reportList.getModule()+"' AND version=" + reportList.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(), 
							"ReportList", whereCond, reportList.getTaskId(), reportList.getNextTaskId());
					if (userAcces){
						showDetailView(reportList);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(reportList);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the ReportList dialog with a new empty entry. <br>
	 */
	public void onClick$button_ReportListList_NewReportList(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new ReportList object, We GET it from the backEnd.
		final ReportList aReportList = getReportListService().getNewReportList();
		showDetailView(aReportList);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param ReportList (aReportList)
	 * @throws Exception
	 */
	private void showDetailView(ReportList aReportList) throws Exception {
		logger.debug("Entering");
		
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if(aReportList.getWorkflowId()==0 && isWorkFlowEnabled()){
			aReportList.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("reportList", aReportList);
		
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the ReportListListbox from the
		 * dialog when we do a delete, edit or insert a ReportList.
		 */
		map.put("reportListListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Reports/ReportList/ReportListDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_ReportListList);
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
		this.pagingReportListList.setActivePage(0);
		Events.postEvent("onCreate", this.window_ReportListList, event);
		this.window_ReportListList.invalidate();
		logger.debug("Leaving");
	}

	/*
	 * call the ReportList dialog
	 */
	public void onClick$button_ReportListList_ReportListSearchDialog(Event event) throws Exception {
		logger.debug("Entering");
		logger.debug(event.toString());
		/*
		 * we can call our ReportListDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected ReportList. For handed over
		 * these parameter only a Map is accepted. So we put the ReportList object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("reportListCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Reports/ReportList/ReportListSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * When the reportList print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_ReportListList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		PTReportUtils.getReport("ReportList", getSearchObj());
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setReportListService(ReportListService reportListService) {
		this.reportListService = reportListService;
	}

	public ReportListService getReportListService() {
		return this.reportListService;
	}

	public JdbcSearchObject<ReportList> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<ReportList> searchObj) {
		this.searchObj = searchObj;
	}
}