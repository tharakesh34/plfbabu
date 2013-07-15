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
 * FileName    		:  EmploymentTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.employmenttype;

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
import com.pennant.backend.model.systemmasters.EmploymentType;
import com.pennant.backend.service.systemmasters.EmploymentTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.systemmasters.employmenttype.model.EmploymentTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/EmploymentType/EmploymentTypeList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class EmploymentTypeListCtrl extends GFCBaseListCtrl<EmploymentType>
		implements Serializable {

	private static final long serialVersionUID = -7932825649812138524L;
	private final static Logger logger = Logger.getLogger(EmploymentTypeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_EmploymentTypeList; 			// autoWired
	protected Borderlayout 	borderLayout_EmploymentTypeList; 	// autoWired
	protected Paging 		pagingEmploymentTypeList; 			// autoWired
	protected Listbox 		listBoxEmploymentType; 				// autoWired

	// List headers
	protected Listheader listheader_EmpType; 		// autoWired
	protected Listheader listheader_EmpTypeDesc; 	// autoWired
	protected Listheader listheader_RecordStatus; 	// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 												// autoWired
	protected Button button_EmploymentTypeList_NewEmploymentType; 			// autoWired
	protected Button button_EmploymentTypeList_EmploymentTypeSearchDialog; 	// autoWired
	protected Button button_EmploymentTypeList_PrintList; 					// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<EmploymentType> searchObj;

	private transient EmploymentTypeService employmentTypeService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public EmploymentTypeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected EmploymentType object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_EmploymentTypeList(Event event)	throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("EmploymentType");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("EmploymentType");

			if (workFlowDetails == null) {
				setWorkFlowEnabled(false);
			} else {
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}
		} else {
			wfAvailable = false;
		}

		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the list box. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that
		 * are filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_EmploymentTypeList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingEmploymentTypeList.setPageSize(getListRows());
		this.pagingEmploymentTypeList.setDetailed(true);

		this.listheader_EmpType.setSortAscending(new FieldComparator("empType",true));
		this.listheader_EmpType.setSortDescending(new FieldComparator("empType", false));
		this.listheader_EmpTypeDesc.setSortAscending(new FieldComparator("empTypeDesc", true));
		this.listheader_EmpTypeDesc.setSortDescending(new FieldComparator("empTypeDesc", false));

		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<EmploymentType>(EmploymentType.class, getListRows());
		this.searchObj.addSort("EmpType", false);

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("RMTEmpTypes_View");
			if (isFirstTask()) {
				button_EmploymentTypeList_NewEmploymentType.setVisible(true);
			} else {
				button_EmploymentTypeList_NewEmploymentType.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("RMTEmpTypes_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_EmploymentTypeList_NewEmploymentType.setVisible(false);
			this.button_EmploymentTypeList_EmploymentTypeSearchDialog.setVisible(false);
			this.button_EmploymentTypeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj, this.listBoxEmploymentType, this.pagingEmploymentTypeList);
			// set the itemRenderer
			this.listBoxEmploymentType.setItemRenderer(new EmploymentTypeListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("EmploymentTypeList");
		this.button_EmploymentTypeList_NewEmploymentType.setVisible(getUserWorkspace()
				.isAllowed("button_EmploymentTypeList_NewEmploymentType"));
		this.button_EmploymentTypeList_EmploymentTypeSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_EmploymentTypeList_EmploymentTypeFindDialog"));
		this.button_EmploymentTypeList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_EmploymentTypeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.rmtmasters.employmenttype.model.
	 * EmploymentTypeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onEmploymentTypeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected EmploymentType object
		final Listitem item = this.listBoxEmploymentType.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final EmploymentType aEmploymentType = (EmploymentType) item.getAttribute("data");
			final EmploymentType employmentType = getEmploymentTypeService().getEmploymentTypeById(aEmploymentType.getId());
			if(employmentType==null){

				String[] valueParm = new String[1];
				String[] errParm= new String[1];

				valueParm[0] = aEmploymentType.getEmpType();
				errParm[0] = PennantJavaUtil.getLabel("label_EmpType") + ":"+ valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				String whereCond = " AND EmpType='" + employmentType.getEmpType()
				+ "' AND version=" + employmentType.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "EmploymentType",
							whereCond,employmentType.getTaskId(),employmentType.getNextTaskId());
					if (userAcces) {
						showDetailView(employmentType);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(employmentType);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the EmploymentType dialog with a new empty entry. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_EmploymentTypeList_NewEmploymentType(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new EmploymentType object, We GET it from the back end.
		final EmploymentType aEmploymentType = getEmploymentTypeService().getNewEmploymentType();
		showDetailView(aEmploymentType);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param EmploymentType
	 *            (aEmploymentType)
	 * @throws Exception
	 */
	private void showDetailView(EmploymentType aEmploymentType)	throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aEmploymentType.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aEmploymentType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("employmentType", aEmploymentType);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the EmploymentTypeListbox from the
		 * dialog when we do a delete, edit or insert a EmploymentType.
		 */
		map.put("employmentTypeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/EmploymentType/EmploymentTypeDialog.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_EmploymentTypeList);
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
		this.pagingEmploymentTypeList.setActivePage(0);
		Events.postEvent("onCreate", this.window_EmploymentTypeList, event);
		this.window_EmploymentTypeList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the EmploymentType dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_EmploymentTypeList_EmploymentTypeSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our EmploymentTypeDialog ZUL-file with parameters. So we
		 * can call them with a object of the selected EmploymentType. For
		 * handed over these parameter only a Map is accepted. So we put the
		 * EmploymentType object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("employmentTypeCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/EmploymentType/EmploymentTypeSearchDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the employmentType print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_EmploymentTypeList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("EmploymentType", getSearchObj(),this.pagingEmploymentTypeList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setEmploymentTypeService(EmploymentTypeService employmentTypeService) {
		this.employmentTypeService = employmentTypeService;
	}
	public EmploymentTypeService getEmploymentTypeService() {
		return this.employmentTypeService;
	}

	public JdbcSearchObject<EmploymentType> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<EmploymentType> searchObj) {
		this.searchObj = searchObj;
	}

}