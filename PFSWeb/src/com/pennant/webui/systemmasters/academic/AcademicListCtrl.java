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
 * FileName    		:  AcademicListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-05-2011    														*
 *                                                                  						*
 * Modified Date    :  23-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.academic;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SessionUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.systemmasters.Academic;
import com.pennant.backend.service.systemmasters.AcademicService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.policy.model.UserImpl;
import com.pennant.webui.systemmasters.academic.model.AcademicListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/Academic/AcademicList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class AcademicListCtrl extends GFCBaseListCtrl<Academic> implements Serializable {

	private static final long serialVersionUID = 5327118548986437717L;
	private final static Logger logger = Logger.getLogger(AcademicListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 			window_AcademicList; 			// autoWired
	protected Borderlayout 		borderLayout_AcademicList; 		// autoWired
	protected Paging 			pagingAcademicList; 			// autoWired
	protected Listbox 			listBoxAcademic; 				// autoWired

	// List headers
	protected Listheader listheader_AcademicLevel; 				// autoWired
	protected Listheader listheader_AcademicDecipline; 			// autoWired
	protected Listheader listheader_AcademicDesc; 				// autoWired
	protected Listheader listheader_RecordStatus; 				// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 									// autoWired
	protected Button button_AcademicList_NewAcademic; 			// autoWired
	protected Button button_AcademicList_AcademicSearchDialog;  // autoWired
	protected Button button_AcademicList_PrintList; 			// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Academic> searchObj;

	private transient AcademicService academicService;
	private transient WorkFlowDetails workFlowDetails=null;
	Iframe report;

	/**
	 * default constructor.<br>
	 */
	public AcademicListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	
	private String getLoggedInUsers() {
		StringBuilder builder = new StringBuilder();
		List<UserImpl> users = SessionUtil.getLoggedInUsers();
		SecurityUser secUser = null;
		if(!users.isEmpty()) {			
			for (UserImpl user : users) {
				if(user.getUserId() != getUserWorkspace().getLoginUserDetails().getLoginUsrID()){
					if(builder.length() > 0){
						builder.append("</br>");
					}
					secUser = user.getSecurityUser(); 
					builder.append("&bull;").append("&nbsp;").append(user.getUserId()).append("&ndash;").append(secUser.getUsrFName() + " " + StringUtils.trimToEmpty(secUser.getUsrMName()) + " " + secUser.getUsrLName()  );
				}
			}
		}
		return builder.toString();
	}
	
	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected AcademicCode object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_AcademicList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		String loggedInUsers = getLoggedInUsers();		
		if(!loggedInUsers.equals("")) {
			loggedInUsers = "\n"+loggedInUsers;
			//PTMessageUtils.showErrorMessage(Labels.getLabel("label_current_logged_users", new String[]{loggedInUsers}));
			Clients.showNotification(Labels.getLabel("label_current_logged_users", new String[]{loggedInUsers}),  "info", null, null, -1);

			//return;
		}

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("Academic");
		boolean wfAvailable = true;
	    
		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Academic");

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

		/* set components visible dependent on the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the listBox. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_AcademicList.setHeight(getBorderLayoutHeight());
		// set the paging parameters
		this.pagingAcademicList.setPageSize(getListRows());
		this.pagingAcademicList.setDetailed(true);

		// Apply sorting for getting List in the ListBox
		this.listheader_AcademicLevel.setSortAscending(new FieldComparator("academicLevel", true));
		this.listheader_AcademicLevel.setSortDescending(new FieldComparator("academicLevel", false));
		this.listheader_AcademicDecipline.setSortAscending(new FieldComparator("academicDecipline", true));
		this.listheader_AcademicDecipline.setSortDescending(new FieldComparator("academicDecipline", false));
		this.listheader_AcademicDesc.setSortAscending(new FieldComparator("academicDesc", true));
		this.listheader_AcademicDesc.setSortDescending(new FieldComparator("academicDesc", false));

		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and initial sorting ++//
		this.searchObj = new JdbcSearchObject<Academic>(Academic.class, getListRows());
		this.searchObj.addSort("AcademicLevel", false);
		this.searchObj.addSort("AcademicDecipline", false);

		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTAcademics_View");
			if (isFirstTask()) {
				button_AcademicList_NewAcademic.setVisible(true);
			} else {
				button_AcademicList_NewAcademic.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTAcademics_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_AcademicList_NewAcademic.setVisible(false);
			this.button_AcademicList_AcademicSearchDialog.setVisible(false);
			this.button_AcademicList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj, this.listBoxAcademic, this.pagingAcademicList);
			// set the itemRenderer
			this.listBoxAcademic.setItemRenderer(new AcademicListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("AcademicList");

		this.button_AcademicList_NewAcademic.setVisible(getUserWorkspace()
				.isAllowed("button_AcademicList_NewAcademic"));
		this.button_AcademicList_AcademicSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_AcademicList_AcademicFindDialog"));
		this.button_AcademicList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_AcademicList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.academic.model.
	 * AcademicListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onAcademicItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// get the selected Academic object
		final Listitem item = this.listBoxAcademic.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Academic aAcademic = (Academic) item.getAttribute("data");
			final Academic academic = getAcademicService().getAcademicById(aAcademic.getAcademicID());

			if (academic == null) {

				String[] valueParm = new String[2];
				String[] errParm = new String[2];

				valueParm[0] = aAcademic.getAcademicLevel();
				valueParm[1] = aAcademic.getAcademicDecipline();

				errParm[0] = PennantJavaUtil.getLabel("label_AcademicLevel") + ":" + valueParm[0];
				errParm[1] = PennantJavaUtil.getLabel("label_AcademicDecipline") + ":" + valueParm[1];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND AcademicID='" + academic.getAcademicID() 
				+ "' AND version=" + academic.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"Academic", whereCond, academic.getTaskId(), academic.getNextTaskId());

					if (userAcces) {
						showDetailView(academic);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(academic);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the Academic dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_AcademicList_NewAcademic(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new Academic object, We GET it from the backEnd.
		final Academic aAcademic = getAcademicService().getNewAcademic();
		showDetailView(aAcademic);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param Academic
	 *            (aAcademic)
	 * @throws Exception
	 */
	private void showDetailView(Academic aAcademic) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aAcademic.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aAcademic.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("academic", aAcademic);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the AcademicListbox from the
		 * dialog when we do a delete, edit or insert a Academic.
		 */
		map.put("academicListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/Academic/AcademicDialog.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_AcademicList);
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
		this.pagingAcademicList.setActivePage(0);
		Events.postEvent("onCreate", this.window_AcademicList, event);
		this.window_AcademicList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the Academic dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_AcademicList_AcademicSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our AcademicDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected Academic. For handed over
		 * these parameter only a Map is accepted. So we put the Academic object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("academicCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/Academic/AcademicSearchDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the academic print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_AcademicList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());	
		PTListReportUtils reportUtils = new PTListReportUtils("Academic", getSearchObj(),this.pagingAcademicList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setAcademicService(AcademicService academicService) {
		this.academicService = academicService;
	}
	public AcademicService getAcademicService() {
		return this.academicService;
	}

	public JdbcSearchObject<Academic> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<Academic> searchObj) {
		this.searchObj = searchObj;
	}

}