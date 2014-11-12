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
 * FileName    		:  SecurityUserListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  27-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.administration.securityuser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.service.administration.SecurityUserService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.administration.securityuser.model.SecurityUserListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Administration/SecurityUser/SecurityUserList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class SecurityUserListCtrl extends GFCBaseListCtrl<SecurityUser> implements Serializable {

	private static final long serialVersionUID = 3104549665882133520L;
	private final static Logger logger = Logger.getLogger(SecurityUserListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window       window_SecurityUserList;                           
	protected Borderlayout borderLayout_SecurityUserList;                     
	protected Paging       pagingSecurityUserList;                            
	protected Listbox      listBoxSecurityUser;                               	// List headers
	protected Listheader   listheader_UsrLogin;                                
	protected Listheader   listheader_UsrFName;                                
	protected Listheader   listheader_UsrMName;                                
	protected Listheader   listheader_UsrLName;                                
	protected Listheader   listheader_UsrCanOverrideLimits;                    
	protected Listheader   listheader_UsrAcExp;                                
	protected Listheader   listheader_UsrCredentialsExp;                       
	protected Listheader   listheader_UsrAcLocked;                             
	protected Listheader   listheader_UsrDftAppCode;                           
	protected Listheader   listheader_UsrBranchCode;                           
	protected Listheader   listheader_UsrDeptCode;                             
	protected Listheader   listheader_RecordStatus;                            
	protected Listheader   listheader_RecordType;                              
	protected Panel        securityUserSeekPanel;                              
	protected Panel        securityUserListPanel;                              

	protected Textbox  usrLogin;                                  
	protected Listbox  sortOperator_UsrLogin;                     
	protected Textbox  usrFName;                                  
	protected Listbox  sortOperator_UsrFName;                
	protected Textbox  usrMName;                                  
	protected Listbox  sortOperator_UsrMName;                     
	protected Textbox  usrLName;                                  
	protected Listbox  sortOperator_UsrLName;                     
	protected Textbox  usrMobile;                                 
	protected Listbox  sortOperator_UsrMobile;                    
	protected Textbox  usrEmail;                                  
	protected Listbox  sortOperator_UsrEmail;                     
	protected Checkbox usrEnabled;                                
	protected Listbox  sortOperator_UsrEnabled;                   
	protected Checkbox usrAcExp;                                  
	protected Listbox  sortOperator_UsrAcExp;                     
	protected Checkbox usrCredentialsExp;                         
	protected Listbox  sortOperator_UsrCredentialsExp;            
	protected Checkbox usrAcLocked;                               
	protected Listbox  sortOperator_UsrAcLocked;                  
	protected Textbox recordStatus; 
	protected Listbox recordType;	
	protected Listbox sortOperator_RecordStatus; 
	protected Listbox sortOperator_RecordType; 
	protected Textbox usrDeptCode;
	protected Listbox sortOperator_UsrDeptCode;
	
	// checkRights
	protected Button    btnHelp;                                            
	protected Button    button_SecurityUserList_NewSecurityUser;            
	protected Button    button_SecurityUserList_SecurityUserSearch;   
	protected Button    button_SecurityUserList_PrintList;
	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<SecurityUser> searchObj;

	private transient SecurityUserService securityUserService;
	private transient WorkFlowDetails workFlowDetails=null;

	protected Grid 			searchGrid;							
	protected Textbox 		moduleType; 						
	protected Radio			fromApproved;
	protected Radio			fromWorkFlow;
	protected Row			workFlowFrom;
	protected Row row_AlwWorkflow;
	private transient boolean  approvedList=false;
	private transient boolean  userModule=false;
	private transient boolean  enqModule=false;
	
	
	
	/**
	 * default constructor.<br>
	 */
	public SecurityUserListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 *  Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected SecurityUser object in a
	 * Map.
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SecurityUserList(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		try{
			String moduleName ="SecurityUser";
			if(moduleType==null){
				enqModule=true;
				userModule=true;
			} else if("USER".equals(this.moduleType.getValue())){
				enqModule=false;
				userModule=true;
			} else if("USERENQ".equals(this.moduleType.getValue())){
				enqModule=true;
				userModule=true;
			} else if("USERROLE".equals(this.moduleType.getValue())){
				enqModule=false;
				userModule=false;
				moduleName="SecurityUserRoles";
			} else if("USERROLEENQ".equals(this.moduleType.getValue())){
				enqModule=true;
				userModule=false;
				moduleName="SecurityUserRoles";
			}else{
				enqModule=true;
				userModule=true;
			}

			ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap(moduleName);
			boolean wfAvailable=true;

			if (moduleMapping.getWorkflowType()!=null){
				workFlowDetails = WorkFlowUtil.getWorkFlowDetails(moduleName);

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

			// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
			
			this.sortOperator_UsrLogin.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_UsrLogin.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_UsrFName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_UsrFName.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_UsrMName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_UsrMName.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_UsrLName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_UsrLName.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_UsrAcExp.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));                     
			this.sortOperator_UsrAcExp.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_UsrCredentialsExp.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
			this.sortOperator_UsrCredentialsExp.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_UsrAcLocked.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
			this.sortOperator_UsrAcLocked.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_UsrDeptCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_UsrDeptCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
			
			
			if (isWorkFlowEnabled()){
				this.sortOperator_RecordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
				this.sortOperator_RecordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
				this.sortOperator_RecordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
				this.sortOperator_RecordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
				this.recordType=PennantAppUtil.setRecordType(this.recordType);

				this.sortOperator_RecordType.setSelectedIndex(0);
				this.recordType.setSelectedIndex(0);

			}else{
				this.row_AlwWorkflow.setVisible(false);
			}
			
			/* set components visible dependent of the users rights */
			doCheckRights();

			/**
			 * Calculate how many rows have been place in the listBox. Get the
			 * currentDesktopHeight from a hidden IntBox from the index.zul that are
			 * filled by onClientInfo() in the indexCtroller
			 */

			this.borderLayout_SecurityUserList.setHeight(getBorderLayoutHeight());
			this.listBoxSecurityUser.setHeight(getListBoxHeight(searchGrid.getRows().getChildren().size()));
			
			// set the paging parameters
			this.pagingSecurityUserList.setPageSize(getListRows());
			this.pagingSecurityUserList.setDetailed(true);
			
			this.listheader_UsrLogin.setSortAscending(new FieldComparator("usrLogin", true));
			this.listheader_UsrLogin.setSortDescending(new FieldComparator("usrLogin", false));
			this.listheader_UsrFName.setSortAscending(new FieldComparator("usrFName", true));
			this.listheader_UsrFName.setSortDescending(new FieldComparator("usrFName", false));
			this.listheader_UsrMName.setSortAscending(new FieldComparator("usrMName", true));
			this.listheader_UsrMName.setSortDescending(new FieldComparator("usrMName", false));
			this.listheader_UsrLName.setSortAscending(new FieldComparator("usrLName", true));
			this.listheader_UsrLName.setSortDescending(new FieldComparator("usrLName", false));
			this.listheader_UsrCanOverrideLimits.setSortAscending(new FieldComparator("usrCanOverrideLimits", true));
			this.listheader_UsrCanOverrideLimits.setSortDescending(new FieldComparator("usrCanOverrideLimits", false));
			this.listheader_UsrAcExp.setSortAscending(new FieldComparator("usrAcExp", true));
			this.listheader_UsrAcExp.setSortDescending(new FieldComparator("usrAcExp", false));
			this.listheader_UsrCredentialsExp.setSortAscending(new FieldComparator("usrCredentialsExp", true));
			this.listheader_UsrCredentialsExp.setSortDescending(new FieldComparator("usrCredentialsExp", false));
			this.listheader_UsrAcLocked.setSortAscending(new FieldComparator("usrAcLocked", true));
			this.listheader_UsrAcLocked.setSortDescending(new FieldComparator("usrAcLocked", false));
			this.listheader_UsrDftAppCode.setSortAscending(new FieldComparator("usrDftAppCode", true));
			this.listheader_UsrDftAppCode.setSortDescending(new FieldComparator("usrDftAppCode", false));
			this.listheader_UsrBranchCode.setSortAscending(new FieldComparator("usrBranchCode", true));
			this.listheader_UsrBranchCode.setSortDescending(new FieldComparator("usrBranchCode", false));
			this.listheader_UsrDeptCode.setSortAscending(new FieldComparator("usrDeptCode", true));
			this.listheader_UsrDeptCode.setSortDescending(new FieldComparator("usrDeptCode", false));
			
			this.listBoxSecurityUser.setItemRenderer(new SecurityUserListModelItemRenderer());
			
			if (isWorkFlowEnabled()){
				this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
				this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
				this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
				this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
			}else{
				this.listheader_RecordStatus.setVisible(false);
				this.listheader_RecordType.setVisible(false);
			}

			if (!isWorkFlowEnabled() && wfAvailable){
				this.button_SecurityUserList_NewSecurityUser.setVisible(false);
				this.button_SecurityUserList_SecurityUserSearch.setVisible(false);
				this.button_SecurityUserList_PrintList.setVisible(false);
				PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));

			}else{
				doSearch();
				if(this.workFlowFrom!=null && !isWorkFlowEnabled()){
					this.workFlowFrom.setVisible(false);
					this.fromApproved.setSelected(true);
				}
			}
			
		} catch (Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
			e.printStackTrace();
			window_SecurityUserList.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	
	/**
	 * call the SecurityUser dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_SecurityUserList_SecurityUserSearch(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doSearch();
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * when the "refresh" button is clicked. <br>
	 * <br>
	 * Refreshes the view by calling the onCreate event manually.
	 * 
	 * @param event
	 * @throws InterruptedExceptiono
	 */
	public void onClick$btnRefresh(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
		usrLogin.setText("");                                  
		sortOperator_UsrLogin.setSelectedIndex(0);                     
		usrFName.setText("");                                  
		sortOperator_UsrFName.setSelectedIndex(0);                     
		usrMName.setText("");                                  
		sortOperator_UsrMName.setSelectedIndex(0);
		usrLName.setText("");                                  
		sortOperator_UsrLName.setSelectedIndex(0);                     
		usrAcExp.setChecked(false);       
		sortOperator_UsrAcExp.setSelectedIndex(0);                     
		usrCredentialsExp.setChecked(false);                         
		sortOperator_UsrCredentialsExp.setSelectedIndex(0);            
		usrAcLocked.setChecked(false);                               
		sortOperator_UsrAcLocked.setSelectedIndex(0);  
		usrDeptCode.setText("");                                 
		sortOperator_UsrDeptCode.setSelectedIndex(0);
		
		if (isWorkFlowEnabled()){
			this.sortOperator_RecordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");

			this.sortOperator_RecordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		
		doSearch();
		logger.debug("Leaving " + event.toString());
	}
	/**
	 * When user clicks on "fromApproved"
	 * @param event
	 * @throws Exception
	 */
	public void onCheck$fromApproved(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doSearch();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on "fromApproved"
	 * @param event
	 * @throws Exception
	 */
	public void onCheck$fromWorkFlow(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doSearch();
		logger.debug("Leaving " + event.toString());
	}
	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		PTMessageUtils.showHelpWindow(event, window_SecurityUserList);
		logger.debug("Leaving " + event.toString());
	}


	/**
	 * When the securityUser print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_SecurityUserList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		new PTListReportUtils("SecUsers", getSearchObj(),this.pagingSecurityUserList.getTotalSize()+1);
		logger.debug("Leaving " + event.toString());
	}
	/**
	 * Call the SecurityUser dialog with a new empty entry. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_SecurityUserList_NewSecurityUser(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		// create a new SecurityUser object, We GET it from the back end.
		final SecurityUser aSecurityUser = getSecurityUserService().getNewSecurityUser();
		showDetailView(aSecurityUser);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.administration.securityusers.model.SecurityUserListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onSecurityUserItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		// get the selected SecurityUser object
		final Listitem item = this.listBoxSecurityUser.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final SecurityUser aSecurityUser = (SecurityUser) item.getAttribute("data");
			SecurityUser securityUser = null;
			if("USER".equals(this.moduleType.getValue())){
				securityUser  = getSecurityUserService().getSecurityUserById(aSecurityUser.getId());
			}else if("USERENQ".equals(this.moduleType.getValue())){
				securityUser  = getSecurityUserService().getApprovedSecurityUserById(aSecurityUser.getId());
			}else if("USERROLE".equals(this.moduleType.getValue())){
				securityUser  = getSecurityUserService().getSecurityUserRolesById(aSecurityUser.getId());
			}else if("USERROLEENQ".equals(this.moduleType.getValue())){
				securityUser  = getSecurityUserService().getApprovedSecurityUserRolesById(aSecurityUser.getId());
			}

			if(securityUser==null){
				String[] errorParm= new String[3];
				errorParm[0]=PennantJavaUtil.getLabel("label_UsrID") + ":"+ aSecurityUser.getUsrID();
				errorParm[1] = PennantJavaUtil.getLabel("label_UsrLogin") + ":"+ aSecurityUser.getUsrLogin();

				String[] valueParm= new String[3];
				valueParm[0]	=	String.valueOf(aSecurityUser.getUsrID());
				valueParm[1] 	= 	aSecurityUser.getUsrLogin();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005"
						, errorParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{

				if(isWorkFlowEnabled() && !enqModule){
					if(securityUser.getWorkflowId()==0){
						securityUser.setWorkflowId(workFlowDetails.getWorkFlowId());
					}

					doLoadWorkFlow(isWorkFlowEnabled(), securityUser.getWorkflowId(), securityUser.getNextTaskId());
		
					boolean userAcces =  false;
					String whereCond =  " AND UsrID="+ securityUser.getUsrID()+" AND version=" + securityUser.getVersion()+" ";
					if("USER".equals(this.moduleType.getValue())){
						userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace()
								.getLoginUserDetails().getLoginUsrID(), "SecurityUser", whereCond, securityUser.getTaskId()
								, securityUser.getNextTaskId());
					}else{
						userAcces=true; 
					}
					
					if (userAcces){
						showDetailView(securityUser);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(securityUser);
				}
			}
		}
		logger.debug("Leaving " + event.toString());
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * SetVisible for components by checking if there's a right for it.
	 * @return void
	 */
	private void doCheckRights() {
		logger.debug("Entering ");

		if(!userModule){
			getUserWorkspace().alocateAuthorities("SecurityUserRolesList");
		}else{
			getUserWorkspace().alocateAuthorities("SecurityUserList");
		}
		
		if(userModule){
			this.button_SecurityUserList_NewSecurityUser.setVisible(getUserWorkspace().isAllowed("button_SecurityUserList_NewSecurityUser"));	
		}else{
			this.button_SecurityUserList_NewSecurityUser.setVisible(false);
		}
		
		this.button_SecurityUserList_PrintList.setVisible(getUserWorkspace().isAllowed("button_SecurityUserList_PrintList"));
		logger.debug("Leaving ");
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param aSecurityUser (SecurityUser)
	 * @throws Exception
	 */
	private void showDetailView(SecurityUser aSecurityUser) throws Exception {
		logger.debug("Entering ");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if(aSecurityUser.getWorkflowId()==0 && isWorkFlowEnabled()){
			aSecurityUser.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("securityUser", aSecurityUser);
		map.put("enqModule", enqModule);

		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox List model. This is
		 * fine for synchronizing the data in the SecurityUserListbox from the
		 * dialog when we do a delete, edit or insert a SecurityUser.
		 */
		map.put("securityUserListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			if("USERROLE".equals(this.moduleType.getValue()) || "USERROLEENQ".equals(this.moduleType.getValue())){
				Executions.createComponents("/WEB-INF/pages/Administration/SecurityUserRoles/SecurityUserRolesDailog.zul",null,map);
			}else{
				Executions.createComponents("/WEB-INF/pages/Administration/SecurityUser/SecurityUserDialog.zul",null,map);
			}
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving ");
	}



	/**
	 * Search/filter data for the filled out fields<br>
	 * <br>
	 * 1. Checks for each text box if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */ 
	
	public void doSearch() {
		
		this.searchObj = new JdbcSearchObject<SecurityUser>(SecurityUser.class,getListRows());
		this.searchObj.addSort("UsrLogin", false);
		
		this.searchObj.addField("UsrID");
		this.searchObj.addField("UsrLogin");
		this.searchObj.addField("UsrFName");
		this.searchObj.addField("UsrMName");
		this.searchObj.addField("UsrLName");
		this.searchObj.addField("usrCanOverrideLimits");
		this.searchObj.addField("usrAcExp");
		this.searchObj.addField("usrCredentialsExp");
		this.searchObj.addField("usrAcLocked");
		this.searchObj.addField("usrDeptCode");
		this.searchObj.addField("lovDescUsrDeptCodeName");
		this.searchObj.addField("RecordType");
		this.searchObj.addField("RecordStatus");
		
		if(userModule){
			this.searchObj.addTabelName("SecUsers_View");	
		}else{
			this.searchObj.addTabelName("SecUsers_RView"); 
		}
		
		// WorkFlow
		if (isWorkFlowEnabled()) {
			
			if (isFirstTask() && userModule && !enqModule) {
				button_SecurityUserList_NewSecurityUser.setVisible(true);
			} else {
				if("USERROLE".equals(this.moduleType.getValue())) {
					button_SecurityUserList_NewSecurityUser.setVisible(false);
				} else {
					button_SecurityUserList_NewSecurityUser.setVisible(false);
				}
			}
			
			//Required Since Same Dialog Controller Used For two Screens
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			
			if(!enqModule){
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
				approvedList=false;
			}else{
				if(this.fromApproved.isSelected()){
					approvedList=true;
				}else{
					if(userModule){
						this.searchObj.addTabelName("SecUsers_TView");	
					}else{
						this.searchObj.addTabelName("SecUsers_RTView");
					}
					approvedList=false;
				}
			}
		}else{
			approvedList=true;
		}
		if(approvedList){
			this.searchObj.addTabelName("SecUsers_AView"); 
		}
		
		// Login User
		if (!StringUtils.trimToEmpty(this.usrLogin.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_UsrLogin.getSelectedItem(), this.usrLogin.getValue(), "UsrLogin");
		}

		
		// First Name
		if (!StringUtils.trimToEmpty(this.usrFName.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_UsrFName.getSelectedItem(), this.usrFName.getValue(), "UsrFName");
		}


		// Middle Name
		if (!StringUtils.trimToEmpty(this.usrMName.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_UsrMName.getSelectedItem(), this.usrMName.getValue(), "UsrMName");
		}

		// Last Name
		if (!StringUtils.trimToEmpty(this.usrLName.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_UsrLName.getSelectedItem(), this.usrLName.getValue(), "UsrLName");
		}

		if (!StringUtils.trimToEmpty(this.usrDeptCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_UsrDeptCode.getSelectedItem(), this.usrDeptCode.getValue(), "usrDeptCode");
		}

		// User Account Expired
		int intUsrAcExp=0;
		if(this.usrAcExp.isChecked()){
			intUsrAcExp=1;
		}
	 	searchObj = getSearchFilter(searchObj, this.sortOperator_UsrAcExp.getSelectedItem(),intUsrAcExp, "UsrAcExp");
	

		// User Credentials Expire
		int intCredentialsExp=0;
		if(this.usrCredentialsExp.isChecked()){
			intCredentialsExp=1;
		}
	 	searchObj = getSearchFilter(searchObj, this.sortOperator_UsrCredentialsExp.getSelectedItem(),intCredentialsExp, "UsrCredentialsExp");
	
	 // User Account Locked
 		int intUsrAcLocked=0;
 		if(this.usrAcLocked.isChecked()){
 			intUsrAcLocked=1;
 		}
 	 	searchObj = getSearchFilter(searchObj, this.sortOperator_UsrAcLocked.getSelectedItem(),intUsrAcLocked, "UsrAcLocked");
 	
		
 	 	
		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_RecordStatus.getSelectedItem(), this.recordStatus.getValue(), "RecordStatus");
		}
		
		// Record Type
		if (this.recordType.getSelectedItem()!=null  && !PennantConstants.List_Select.equals(this.recordType.getSelectedItem().getValue())
				&& !StringUtils.trimToEmpty((String) this.recordType.getSelectedItem().getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_RecordType.getSelectedItem(), this.recordType.getSelectedItem().getValue().toString(), "RecordType");
		}

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = this.searchObj.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / " + filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}
		
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj,this.listBoxSecurityUser,this.pagingSecurityUserList);
		
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setSecurityUserService(SecurityUserService securityUserService) {
		this.securityUserService = securityUserService;
	}
	public SecurityUserService getSecurityUserService() {
		return this.securityUserService;
	}

	public JdbcSearchObject<SecurityUser> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<SecurityUser> searchObj) {
		this.searchObj = searchObj;
	}

	public Paging getPagingSecurityUserList() {
		return pagingSecurityUserList;
	}

	public Listbox getListBoxSecurityUser() {
		return listBoxSecurityUser;
	}
	
}