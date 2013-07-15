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
 * FileName    		:  GeneralDepartmentListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.generaldepartment;

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
import com.pennant.backend.model.systemmasters.GeneralDepartment;
import com.pennant.backend.service.systemmasters.GeneralDepartmentService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.systemmasters.generaldepartment.model.GeneralDepartmentListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/GeneralDepartment/GeneralDepartmentList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class GeneralDepartmentListCtrl extends
		GFCBaseListCtrl<GeneralDepartment> implements Serializable {

	private static final long serialVersionUID = -8782007567428187225L;
	private final static Logger logger = Logger.getLogger(GeneralDepartmentListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_GeneralDepartmentList; 		// autoWired
	protected Borderlayout 	borderLayout_GeneralDepartmentList; // autoWired
	protected Paging 		pagingGeneralDepartmentList; 		// autoWired
	protected Listbox 		listBoxGeneralDepartment; 			// autoWired

	// List headers
	protected Listheader listheader_GenDepartment; 	// autoWired
	protected Listheader listheader_GenDeptDesc; 	// autoWired
	protected Listheader listheader_RecordStatus; 	// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 													// autoWired
	protected Button button_GeneralDepartmentList_NewGeneralDepartment; 		// autoWired
	protected Button button_GeneralDepartmentList_GeneralDepartmentSearchDialog;// autoWired
	protected Button button_GeneralDepartmentList_PrintList; 					// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<GeneralDepartment> searchObj;
	
	private transient GeneralDepartmentService generalDepartmentService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public GeneralDepartmentListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected GeneralDepartment object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_GeneralDepartmentList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("GeneralDepartment");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("GeneralDepartment");
			
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
		
		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the listBox. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_GeneralDepartmentList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingGeneralDepartmentList.setPageSize(getListRows());
		this.pagingGeneralDepartmentList.setDetailed(true);

		this.listheader_GenDepartment.setSortAscending(
				new FieldComparator("genDepartment", true));
		this.listheader_GenDepartment.setSortDescending(
				new FieldComparator("genDepartment", false));
		this.listheader_GenDeptDesc.setSortAscending(
				new FieldComparator("genDeptDesc", true));
		this.listheader_GenDeptDesc.setSortDescending(
				new FieldComparator("genDeptDesc", false));
		
		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(
					new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(
					new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(
					new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(
					new FieldComparator("recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}
		
		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<GeneralDepartment>(
				GeneralDepartment.class,getListRows());
		this.searchObj.addSort("GenDepartment", false);
		
		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("RMTGenDepartments_View");
			if (isFirstTask()) {
				button_GeneralDepartmentList_NewGeneralDepartment.setVisible(true);
			} else {
				button_GeneralDepartmentList_NewGeneralDepartment.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),
					isFirstTask());
		}else{
			this.searchObj.addTabelName("RMTGenDepartments_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_GeneralDepartmentList_NewGeneralDepartment.setVisible(false);
			this.button_GeneralDepartmentList_GeneralDepartmentSearchDialog.setVisible(false);
			this.button_GeneralDepartmentList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(
					PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,
					this.listBoxGeneralDepartment,this.pagingGeneralDepartmentList);
			// set the itemRenderer
			this.listBoxGeneralDepartment.setItemRenderer(
					new GeneralDepartmentListModelItemRenderer());
		}	
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("GeneralDepartmentList");
		
		this.button_GeneralDepartmentList_NewGeneralDepartment.setVisible(getUserWorkspace()
				.isAllowed("button_GeneralDepartmentList_NewGeneralDepartment"));
		this.button_GeneralDepartmentList_GeneralDepartmentSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_GeneralDepartmentList_GeneralDepartmentFindDialog"));
		this.button_GeneralDepartmentList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_GeneralDepartmentList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.rmtmasters.generaldepartment.model.
	 * GeneralDepartmentListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onGeneralDepartmentItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected GeneralDepartment object
		final Listitem item = this.listBoxGeneralDepartment.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final GeneralDepartment aGeneralDepartment = (GeneralDepartment) item
					.getAttribute("data");
			final GeneralDepartment generalDepartment = getGeneralDepartmentService()
			.getGeneralDepartmentById(aGeneralDepartment.getId());
			if(generalDepartment==null){

				String[] valueParm = new String[1];
				String[] errParm= new String[1];

				valueParm[0] = aGeneralDepartment.getGenDepartment();
				errParm[0] = PennantJavaUtil.getLabel("label_GenDepartment") + ":"+ valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				String whereCond = " AND GenDepartment='"+ generalDepartment.getGenDepartment() +
				"' AND version="+ generalDepartment.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "GeneralDepartment",
							whereCond, generalDepartment.getTaskId(),generalDepartment.getNextTaskId());
					if (userAcces){
						showDetailView(generalDepartment);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(generalDepartment);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the GeneralDepartment dialog with a new empty entry. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_GeneralDepartmentList_NewGeneralDepartment(
			Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new GeneralDepartment object, We GET it from the backEnd.
		final GeneralDepartment aGeneralDepartment = getGeneralDepartmentService()
				.getNewGeneralDepartment();
		showDetailView(aGeneralDepartment);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some params in a map if needed. <br>
	 * 
	 * @param GeneralDepartment (aGeneralDepartment)
	 * @throws Exception
	 */
	private void showDetailView(GeneralDepartment aGeneralDepartment) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aGeneralDepartment.getWorkflowId()==0 && isWorkFlowEnabled()){
			aGeneralDepartment.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("generalDepartment", aGeneralDepartment);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the GeneralDepartmentListbox from the
		 * dialog when we do a delete, edit or insert a GeneralDepartment.
		 */
		map.put("generalDepartmentListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/GeneralDepartment/GeneralDepartmentDialog.zul",
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
		logger.debug("Entering"+event.toString());
		PTMessageUtils.showHelpWindow(event, window_GeneralDepartmentList);
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
		this.pagingGeneralDepartmentList.setActivePage(0);
		Events.postEvent("onCreate", this.window_GeneralDepartmentList, event);
		this.window_GeneralDepartmentList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the GeneralDepartment dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_GeneralDepartmentList_GeneralDepartmentSearchDialog(
			Event event) throws Exception {
		logger.debug("Entering"+event.toString());
		/*
		 * we can call our GeneralDepartmentDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected GeneralDepartment. For handed over
		 * these parameter only a Map is accepted. So we put the GeneralDepartment object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("generalDepartmentCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/GeneralDepartment/GeneralDepartmentSearchDialog.zul",
							null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * When the generalDepartment print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_GeneralDepartmentList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering"+event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("GeneralDepartment", getSearchObj(),
				this.pagingGeneralDepartmentList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setGeneralDepartmentService(GeneralDepartmentService generalDepartmentService) {
		this.generalDepartmentService = generalDepartmentService;
	}
	public GeneralDepartmentService getGeneralDepartmentService() {
		return this.generalDepartmentService;
	}

	public JdbcSearchObject<GeneralDepartment> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<GeneralDepartment> searchObj) {
		this.searchObj = searchObj;
	}
}