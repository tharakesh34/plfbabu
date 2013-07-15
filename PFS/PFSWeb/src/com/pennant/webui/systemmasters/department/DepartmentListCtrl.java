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
 * FileName    		:  DepartmentListCtrl.java                                                   * 	  
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

package com.pennant.webui.systemmasters.department;

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
import com.pennant.backend.model.systemmasters.Department;
import com.pennant.backend.service.systemmasters.DepartmentService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.systemmasters.department.model.DepartmentListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/Department/DepartmentList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class DepartmentListCtrl extends GFCBaseListCtrl<Department> implements Serializable {

	private static final long serialVersionUID = -3848678436698919359L;
	private final static Logger logger = Logger.getLogger(DepartmentListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_DepartmentList; 			// autoWired
	protected Borderlayout 	borderLayout_DepartmentList; 	// autoWired
	protected Paging 		pagingDepartmentList; 			// autoWired
	protected Listbox 		listBoxDepartment; 				// autoWired

	// List headers
	protected Listheader listheader_DeptCode; 		// autoWired
	protected Listheader listheader_DeptDesc; 		// autoWired
	protected Listheader listheader_DeptIsActive; 	// autoWired
	protected Listheader listheader_RecordStatus; 	// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 										// autoWired
	protected Button button_DepartmentList_NewDepartment; 			// autoWired
	protected Button button_DepartmentList_DepartmentSearchDialog; 	// autoWired
	protected Button button_DepartmentList_PrintList; 				// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Department> searchObj;

	private transient DepartmentService departmentService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public DepartmentListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected Department object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_DepartmentList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("Department");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Department");

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
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_DepartmentList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingDepartmentList.setPageSize(getListRows());
		this.pagingDepartmentList.setDetailed(true);

		this.listheader_DeptCode.setSortAscending(new FieldComparator("deptCode", true));
		this.listheader_DeptCode.setSortDescending(new FieldComparator("deptCode", false));
		this.listheader_DeptDesc.setSortAscending(new FieldComparator("deptDesc", true));
		this.listheader_DeptDesc.setSortDescending(new FieldComparator("deptDesc", false));
		this.listheader_DeptIsActive.setSortAscending(new FieldComparator("deptIsActive", true));
		this.listheader_DeptIsActive.setSortDescending(new FieldComparator("deptIsActive", false));

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
		this.searchObj = new JdbcSearchObject<Department>(Department.class, getListRows());
		this.searchObj.addSort("DeptCode", false);

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTDepartments_View");
			if (isFirstTask()) {
				button_DepartmentList_NewDepartment.setVisible(true);
			} else {
				button_DepartmentList_NewDepartment.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTDepartments_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_DepartmentList_NewDepartment.setVisible(false);
			this.button_DepartmentList_DepartmentSearchDialog.setVisible(false);
			this.button_DepartmentList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj, this.listBoxDepartment, this.pagingDepartmentList);
			// set the itemRenderer
			this.listBoxDepartment.setItemRenderer(new DepartmentListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("DepartmentList");
		this.button_DepartmentList_NewDepartment.setVisible(getUserWorkspace()
				.isAllowed("button_DepartmentList_NewDepartment"));
		this.button_DepartmentList_DepartmentSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_DepartmentList_DepartmentFindDialog"));
		this.button_DepartmentList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_DepartmentList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.department.model.
	 * DepartmentListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onDepartmentItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected Department object
		final Listitem item = this.listBoxDepartment.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Department aDepartment = (Department) item.getAttribute("data");
			final Department department = getDepartmentService().getDepartmentById(aDepartment.getId());

			if (department == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aDepartment.getDeptCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_DeptCode") + ":" + aDepartment.getDeptCode();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND DeptCode='" + department.getDeptCode()
				+ "' AND version=" + department.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"Department", whereCond, department.getTaskId(), department.getNextTaskId());
					if (userAcces) {
						showDetailView(department);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(department);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the Department dialog with a new empty entry. <br>
	 */
	public void onClick$button_DepartmentList_NewDepartment(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// create a new Department object, We GET it from the back end.
		final Department aDepartment = getDepartmentService().getNewDepartment();
		showDetailView(aDepartment);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param Department
	 *            (aDepartment)
	 * @throws Exception
	 */
	private void showDetailView(Department aDepartment) throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aDepartment.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aDepartment.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("department", aDepartment);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the DepartmentListbox from the
		 * dialog when we do a delete, edit or insert a Department.
		 */
		map.put("departmentListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/Department/DepartmentDialog.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_DepartmentList);
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
		this.pagingDepartmentList.setActivePage(0);
		Events.postEvent("onCreate", this.window_DepartmentList, event);
		this.window_DepartmentList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the Department dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_DepartmentList_DepartmentSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our DepartmentDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected Department. For handed over
		 * these parameter only a Map is accepted. So we put the Department
		 * object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("departmentCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/Department/DepartmentSearchDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the department print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_DepartmentList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("Department", getSearchObj(),this.pagingDepartmentList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setDepartmentService(DepartmentService departmentService) {
		this.departmentService = departmentService;
	}
	public DepartmentService getDepartmentService() {
		return this.departmentService;
	}

	public JdbcSearchObject<Department> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<Department> searchObj) {
		this.searchObj = searchObj;
	}
}