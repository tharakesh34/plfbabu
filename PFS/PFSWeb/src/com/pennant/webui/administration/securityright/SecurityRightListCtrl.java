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
 * FileName    		:  SecurityRightListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-07-2011    														*
 *                                                                  						*
 * Modified Date    :  10-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.administration.securityright;

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
import org.zkoss.zul.Panel;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.administration.SecurityRight;
import com.pennant.backend.service.administration.SecurityRightService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.administration.securityright.model.SecurityRightListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/administration/SecurityRight/SecurityRightList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class SecurityRightListCtrl extends GFCBaseListCtrl<SecurityRight> implements Serializable {

	private static final long serialVersionUID = 7695068400635984692L;
	private final static Logger logger = Logger.getLogger(SecurityRightListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window       window_SecurityRightList;                    // autoWired
	protected Borderlayout borderLayout_SecurityRightList;              // autoWired
	protected Paging       pagingSecurityRightList;                     // autoWired
	protected Listbox      listBoxSecurityRight;                        // autoWired
	
	// List headers
	protected Listheader   listheader_RightType;                    	// autoWired
	protected Listheader   listheader_RightName;                    	// autoWired
	protected Listheader   listheader_RecordStatus;                 	// autoWired
	protected Listheader   listheader_RecordType;                   	// autoWired
	protected Panel        securityRightSeekPanel;                      // autoWired
	protected Panel        securityRightListPanel;                      // autoWired
	
	// checkRights
	protected Button       btnHelp;                                  			// autoWired
	protected Button       button_SecurityRightList_NewSecurityRight;          	// autoWired
	protected Button       button_SecurityRightList_SecurityRightSearchDialog; 	// autoWired
	protected Button       button_SecurityRightList_PrintList;            		// autoWired
	
	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<SecurityRight> searchObj;
	private transient SecurityRightService    securityRightService;
	private transient WorkFlowDetails    	  workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public SecurityRightListCtrl() {
		super();
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected SecurityRight object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SecurityRightList(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("SecurityRight");
		boolean wfAvailable=true;

		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("SecurityRight");

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
		this.borderLayout_SecurityRightList.setHeight(String.valueOf(getBorderLayoutHeight()));

		// set the paging parameters
		this.pagingSecurityRightList.setPageSize(getListRows());
		this.pagingSecurityRightList.setDetailed(true);

		this.listheader_RightType.setSortAscending(new FieldComparator("rightType", true));
		this.listheader_RightType.setSortDescending(new FieldComparator("rightType", false));
		this.listheader_RightName.setSortAscending(new FieldComparator("rightName", true));
		this.listheader_RightName.setSortDescending(new FieldComparator("rightName", false));

		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and initial sorting ++//
		this.searchObj = new JdbcSearchObject<SecurityRight>(SecurityRight.class,getListRows());
		this.searchObj.addSort("rightName", false);
		this.searchObj.addTabelName("SecRights_View");

		// WorkFlow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_SecurityRightList_NewSecurityRight.setVisible(true);
			} else {
				button_SecurityRightList_NewSecurityRight.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}
		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_SecurityRightList_NewSecurityRight.setVisible(false);
			this.button_SecurityRightList_SecurityRightSearchDialog.setVisible(false);
			this.button_SecurityRightList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxSecurityRight,this.pagingSecurityRightList);
			// set the itemRenderer
			this.listBoxSecurityRight.setItemRenderer(new SecurityRightListModelItemRenderer());
		}	
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering ");
		
		getUserWorkspace().alocateAuthorities("SecurityRightList");
		this.button_SecurityRightList_NewSecurityRight.setVisible(getUserWorkspace()
				.isAllowed("button_SecurityRightList_NewSecurityRight"));
		this.button_SecurityRightList_SecurityRightSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_SecurityRightList_SecurityRightFindDialog"));
		this.button_SecurityRightList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_SecurityRightList_PrintList"));
		logger.debug("Leaving ");
	}
	
	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.administration.secright.model.SecurityRightListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onSecurityRightItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		// get the selected SecurityRight object
		final Listitem item = this.listBoxSecurityRight.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final SecurityRight aSecurityRight = (SecurityRight) item.getAttribute("data");
			final SecurityRight securityRight = getSecurityRightService().getSecurityRightById(aSecurityRight.getId());

			if(securityRight==null){
				
				String[] errorParm= new String[3];
				errorParm[0]=PennantJavaUtil.getLabel("label_RightID") + ":"+ aSecurityRight.getRightID();
				errorParm[1] = PennantJavaUtil.getLabel("label_RightName") + ":"+ aSecurityRight.getRightName();

				String[] valueParm= new String[3];
				valueParm[0]	=	String.valueOf(aSecurityRight.getRightID());
				valueParm[1] 	= 	aSecurityRight.getRightName();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005"
						        , errorParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getError());
			}else{

				String whereCond =  " AND RightID="+ securityRight.getRightID()+" AND version=" + securityRight.getVersion()+" ";
				if(isWorkFlowEnabled()){
					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace()
							.getLoginUserDetails().getLoginUsrID(), "SecurityRight", whereCond, securityRight.getTaskId()
							, securityRight.getNextTaskId());
					if (userAcces){
						showDetailView(securityRight);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(securityRight);
				}
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Call the SecurityRight dialog with a new empty entry. <br>
	 */
	public void onClick$button_SecurityRightList_NewSecurityRight(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		
		// create a new SecurityRight object, We GET it from the back end.
		final SecurityRight aSecurityRight = getSecurityRightService().getNewSecurityRight();	
		showDetailView(aSecurityRight);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param SecurityRight (aSecurityRight)
	 * @throws Exception
	 */
	private void showDetailView(SecurityRight aSecurityRight) throws Exception {
		logger.debug("Entering ");
		
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if(aSecurityRight.getWorkflowId()==0 && isWorkFlowEnabled()){
			aSecurityRight.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("securityRight", aSecurityRight);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox List model. This is
		 * fine for synchronizing the data in the SecurityRightListbox from the
		 * dialog when we do a delete, edit or insert a SecurityRight.
		 */
		map.put("securityRightListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Administration/SecurityRight/SecurityRightDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving ");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		PTMessageUtils.showHelpWindow(event, window_SecurityRightList);
		logger.debug("Leaving " + event.toString());
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
		logger.debug("Entering " + event.toString());
		this.pagingSecurityRightList.setActivePage(0);
		Events.postEvent("onCreate", this.window_SecurityRightList, event);
		this.window_SecurityRightList.invalidate();
		logger.debug("Leaving " + event.toString());
	}

	/*
	 * call the SecurityRight dialog
	 */
	public void onClick$button_SecurityRightList_SecurityRightSearchDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		
		/*
		 * we can call our SecurityRightDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected SecurityRight. For handed over
		 * these parameter only a Map is accepted. So we put the SecurityRight object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("securityRightCtrl", this);
		map.put("searchObject", this.searchObj);
		map.put("listBoxSecurityRight", this.listBoxSecurityRight);
		map.put("pagingSecurityRightList", this.pagingSecurityRightList);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Administration/SecurityRight/SecurityRightSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When the securityRight print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_SecurityRightList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		PTReportUtils.getReport("SecurityRight", getSearchObj());
		logger.debug("Leaving " + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setSecurityRightService(SecurityRightService securityRightService) {
		this.securityRightService = securityRightService;
	}
	public SecurityRightService getSecurityRightService() {
		return this.securityRightService;
	}

	public JdbcSearchObject<SecurityRight> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<SecurityRight> searchObj) {
		this.searchObj = searchObj;
	}
}