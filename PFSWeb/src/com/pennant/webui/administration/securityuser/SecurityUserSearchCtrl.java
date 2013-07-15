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
 * FileName    		:  SecurityUserSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  2-8-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *  2-8-2011       Pennant	                 0.1                                            * 
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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.service.administration.SecurityUserService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Administration/SecurityUser/SecurityUserSearchDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class SecurityUserSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 8855431291719268507L;
	private final static Logger logger = Logger.getLogger(SecurityUserSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window   window_SecurityUserSearch;                 // autoWired
	protected Intbox   usrID;                                     // autoWired
	protected Listbox  sortOperator_usrID;                        // autoWired
	protected Textbox  usrLogin;                                  // autoWired
	protected Listbox  sortOperator_usrLogin;                     // autoWired
	protected Textbox  usrPwd;                                    // autoWired
	protected Listbox  sortOperator_usrPwd;                       // autoWired
	protected Textbox  userStaffID;                               // autoWired
	protected Listbox  sortOperator_userStaffID;                  // autoWired
	protected Textbox  usrFName;                                  // autoWired
	protected Listbox  sortOperator_usrFName;                     // autoWired
	protected Textbox  usrMName;                                  // autoWired
	protected Listbox  sortOperator_usrMName;                     // autoWired
	protected Textbox  usrLName;                                  // autoWired
	protected Listbox  sortOperator_usrLName;                     // autoWired
	protected Textbox  usrMobile;                                 // autoWired
	protected Listbox  sortOperator_usrMobile;                    // autoWired
	protected Textbox  usrEmail;                                  // autoWired
	protected Listbox  sortOperator_usrEmail;                     // autoWired
	protected Checkbox usrEnabled;                                // autoWired
	protected Listbox  sortOperator_usrEnabled;                   // autoWired
	protected Textbox  usrCanSignonFrom;                          // autoWired
	protected Listbox  sortOperator_usrCanSignonFrom;             // autoWired
	protected Textbox  usrCanSignonTo;                            // autoWired
	protected Listbox  sortOperator_usrCanSignonTo;               // autoWired
	protected Checkbox usrCanOverrideLimits;                      // autoWired
	protected Listbox  sortOperator_usrCanOverrideLimits;         // autoWired
	protected Checkbox usrAcExp;                                  // autoWired
	protected Listbox  sortOperator_usrAcExp;                     // autoWired
	protected Checkbox usrCredentialsExp;                         // autoWired
	protected Listbox  sortOperator_usrCredentialsExp;            // autoWired
	protected Checkbox usrAcLocked;                               // autoWired
	protected Listbox  sortOperator_usrAcLocked;                  // autoWired
	protected Textbox  usrLanguage;                               // autoWired
	protected Listbox  sortOperator_usrLanguage;                  // autoWired
	protected Textbox  usrBranchCode;                             // autoWired
	protected Listbox  sortOperator_usrBranchCode;                // autoWired
	protected Textbox  usrDeptCode;                               // autoWired
	protected Listbox  sortOperator_usrDeptCode;                  // autoWired
	protected Textbox  recordStatus;                              // autoWired
	protected Listbox  recordType;	                              // autoWired
	protected Listbox  sortOperator_recordStatus;                 // autoWired
	protected Listbox  sortOperator_recordType;                   // autoWired
	protected Label    label_SecurityUserSearch_RecordStatus;     // autoWired
	protected Label    label_SecurityUserSearch_RecordType;       // autoWired
	protected Label    label_SecurityUserSearchResult;            // autoWired
	protected Listbox  listBox;
	protected Paging   paging ;
	private transient SecurityUserService securityUserService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("SecurityUser");
	private Object object;
	Object obj;
	String secClass;
	
	/**
	 * constructor
	 */
	public SecurityUserSearchCtrl() {
		super();
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected AccountType object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_SecurityUserSearch(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		
		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
        /*Here object is overHanded parameter .object can be instance of by SeurityusersListCtrl
         *,SecurityUserRolesListCtrl,SecurityUserChangePasswordListCtrl*/
   
		if (args.containsKey("securityUserList")) {
			  object=(Object)args.get("securityUserList");
			  listBox = (Listbox)args.get("listBoxSecurityUser");
			  paging = (Paging)args.get("pagingSecurityUserList");
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_usrID.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_usrID.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_usrLogin.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_usrLogin.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_usrPwd.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_usrPwd.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_userStaffID.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_userStaffID.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_usrFName.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_usrFName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_usrMName.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_usrMName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_usrLName.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_usrLName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_usrMobile.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_usrMobile.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_usrEmail.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_usrEmail.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_usrEnabled.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_usrEnabled.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_usrCanSignonFrom.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_usrCanSignonFrom.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_usrCanSignonTo.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_usrCanSignonTo.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_usrCanOverrideLimits.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_usrCanOverrideLimits.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_usrAcExp.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_usrAcExp.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_usrCredentialsExp.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_usrCredentialsExp.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_usrAcLocked.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_usrAcLocked.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_usrLanguage.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_usrLanguage.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_usrBranchCode.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_usrBranchCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_usrDeptCode.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_usrDeptCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=PennantAppUtil.setRecordType(this.recordType);	
		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_SecurityUserSearch_RecordStatus.setVisible(false);
			this.label_SecurityUserSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<SecurityUser> searchObj = (JdbcSearchObject<SecurityUser>) args
			.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("usrID")) {
					SearchOperators.restoreStringOperator(this.sortOperator_usrID, filter);
					this.usrID.setValue(Integer.parseInt(filter.getValue().toString()));
				} else if (filter.getProperty().equals("usrLogin")) {
					SearchOperators.restoreStringOperator(this.sortOperator_usrLogin, filter);
					this.usrLogin.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("usrPwd")) {
					SearchOperators.restoreStringOperator(this.sortOperator_usrPwd, filter);
					this.usrPwd.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("userStaffID")) {
					SearchOperators.restoreStringOperator(this.sortOperator_userStaffID, filter);
					this.userStaffID.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("usrFName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_usrFName, filter);
					this.usrFName.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("usrMName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_usrMName, filter);
					this.usrMName.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("usrLName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_usrLName, filter);
					this.usrLName.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("usrMobile")) {
					SearchOperators.restoreStringOperator(this.sortOperator_usrMobile, filter);
					this.usrMobile.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("usrEmail")) {
					SearchOperators.restoreStringOperator(this.sortOperator_usrEmail, filter);
					this.usrEmail.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("usrEnabled")) {
					SearchOperators.restoreStringOperator(this.sortOperator_usrEnabled, filter);
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.usrEnabled.setChecked(true);
					}else{
						this.usrEnabled.setChecked(false);
					}
				} else if (filter.getProperty().equals("usrCanSignonFrom")) {
					SearchOperators.restoreStringOperator(this.sortOperator_usrCanSignonFrom, filter);
					this.usrCanSignonFrom.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("usrCanSignonTo")) {
					SearchOperators.restoreStringOperator(this.sortOperator_usrCanSignonTo, filter);
					this.usrCanSignonTo.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("usrCanOverrideLimits")) {
					SearchOperators.restoreStringOperator(this.sortOperator_usrCanOverrideLimits, filter);
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.usrCanOverrideLimits.setChecked(true);
					}else{
						this.usrCanOverrideLimits.setChecked(false);
					}
				} else if (filter.getProperty().equals("usrAcExp")) {
					SearchOperators.restoreStringOperator(this.sortOperator_usrAcExp, filter);
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.usrAcExp.setChecked(true);
					}else{
						this.usrAcExp.setChecked(false);
					}
				} else if (filter.getProperty().equals("usrCredentialsExp")) {
					SearchOperators.restoreStringOperator(this.sortOperator_usrCredentialsExp, filter);
					this.usrCredentialsExp.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("usrAcLocked")) {
					SearchOperators.restoreStringOperator(this.sortOperator_usrAcLocked, filter);
					this.usrAcLocked.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("usrLanguage")) {
					SearchOperators.restoreStringOperator(this.sortOperator_usrLanguage, filter);
					this.usrLanguage.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("usrBranchCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_usrBranchCode, filter);
					this.usrBranchCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("usrDeptCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_usrDeptCode, filter);
					this.usrDeptCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("recordStatus")) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordStatus, filter);
					this.recordStatus.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("recordType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordType, filter);
					for (int i = 0; i < this.recordType.getItemCount(); i++) {
						if (this.recordType.getItemAtIndex(i).getValue().equals(
								filter.getValue().toString())){
							this.recordType.setSelectedIndex(i);
						}
					}
				}
			}
		}
		showSecurityUserSeekDialog();
		logger.debug("Leaving " + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * when the "search/filter" button is clicked.
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$btnSearch(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doSearch();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		doClose();
		logger.debug("Leaving " + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * closes the dialog window
	 */
	private void doClose() {
		logger.debug("Entering ");
		this.window_SecurityUserSearch.onClose();
		logger.debug("Leaving ");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showSecurityUserSeekDialog() throws InterruptedException {
		logger.debug("Entering ");

		try {
			// open the dialog in modal mode
			this.window_SecurityUserSearch.setHeight("500px");
			this.window_SecurityUserSearch.doModal();
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving ");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Search/filter data for the filled out fields<br>
	 * <br>
	 * 1. Checks for each textBox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 * @throws Exception 
	 * 
	 */ 
	@SuppressWarnings("unchecked")
	public void doSearch() throws Exception {
		logger.debug("Entering ");
		final JdbcSearchObject<SecurityUser> so = new JdbcSearchObject<SecurityUser>(SecurityUser.class);
		so.addTabelName("SecUsers_View");

		if (isWorkFlowEnabled()){
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}

		if (this.usrID.getValue()!=null) {

			// get the search operator
			final Listitem item_UsrID = this.sortOperator_usrID.getSelectedItem();

			if (item_UsrID != null) {
				final int searchOpId = ((SearchOperators) item_UsrID.getAttribute("data"))
				                                .getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("usrID", "%" + this.usrID.getValue(), searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("usrID", this.usrID.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.usrLogin.getValue())) {

			// get the search operator
			final Listitem item_UsrLogin = this.sortOperator_usrLogin.getSelectedItem();

			if (item_UsrLogin != null) {
				final int searchOpId = ((SearchOperators) item_UsrLogin.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("usrLogin", "%" + this.usrLogin.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("usrLogin", this.usrLogin.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.usrPwd.getValue())) {

			// get the search operator
			final Listitem item_UsrPwd = this.sortOperator_usrPwd.getSelectedItem();

			if (item_UsrPwd != null) {
				final int searchOpId = ((SearchOperators) item_UsrPwd.getAttribute("data")).getSearchOperatorId();
				                  

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("usrPwd", "%" + this.usrPwd.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("usrPwd", this.usrPwd.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.userStaffID.getValue())) {

			// get the search operator
			final Listitem item_UserStaffID = this.sortOperator_userStaffID.getSelectedItem();

			if (item_UserStaffID != null) {
				final int searchOpId = ((SearchOperators) item_UserStaffID.getAttribute("data")).getSearchOperatorId();                       

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("userStaffID", "%" + this.userStaffID.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("userStaffID", this.userStaffID.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.usrFName.getValue())) {

			// get the search operator
			final Listitem item_UsrFName = this.sortOperator_usrFName.getSelectedItem();

			if (item_UsrFName != null) {
				final int searchOpId = ((SearchOperators) item_UsrFName.getAttribute("data")).getSearchOperatorId();      

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("usrFName", "%" + this.usrFName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("usrFName", this.usrFName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.usrMName.getValue())) {

			// get the search operator
			final Listitem item_UsrMName = this.sortOperator_usrMName.getSelectedItem();

			if (item_UsrMName != null) {
				final int searchOpId = ((SearchOperators) item_UsrMName.getAttribute("data")).getSearchOperatorId();       

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("usrMName", "%" + this.usrMName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("usrMName", this.usrMName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.usrLName.getValue())) {

			// get the search operator
			final Listitem item_UsrLName = this.sortOperator_usrLName.getSelectedItem();

			if (item_UsrLName != null) {
				final int searchOpId = ((SearchOperators) item_UsrLName.getAttribute("data")).getSearchOperatorId();          

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("usrLName", "%" + this.usrLName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("usrLName", this.usrLName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.usrMobile.getValue())) {

			// get the search operator
			final Listitem item_UsrMobile = this.sortOperator_usrMobile.getSelectedItem();

			if (item_UsrMobile != null) {
				final int searchOpId = ((SearchOperators) item_UsrMobile.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("usrMobile", "%" + this.usrMobile.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("usrMobile", this.usrMobile.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.usrEmail.getValue())) {

			// get the search operator
			final Listitem item_UsrEmail = this.sortOperator_usrEmail.getSelectedItem();

			if (item_UsrEmail != null) {
				final int searchOpId = ((SearchOperators) item_UsrEmail.getAttribute("data")).getSearchOperatorId();    

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("usrEmail", "%"  + this.usrEmail.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("usrEmail", this.usrEmail.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_UsrEnabled = this.sortOperator_usrEnabled.getSelectedItem();

		if (item_UsrEnabled != null) {
			final int searchOpId = ((SearchOperators) item_UsrEnabled.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if(this.usrEnabled.isChecked()){
					so.addFilter(new Filter("usrEnabled",1, searchOpId));
				}else{
					so.addFilter(new Filter("usrEnabled",0, searchOpId));	
				}
			}
		}
		if (StringUtils.isNotEmpty(this.usrCanSignonFrom.getValue())) {

			// get the search operator
			final Listitem item_UsrCanSignonFrom = this.sortOperator_usrCanSignonFrom.getSelectedItem();

			if (item_UsrCanSignonFrom != null) {
				final int searchOpId = ((SearchOperators) item_UsrCanSignonFrom.getAttribute("data")).getSearchOperatorId();               

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("usrCanSignonFrom", "%" + this.usrCanSignonFrom.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("usrCanSignonFrom", this.usrCanSignonFrom.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.usrCanSignonTo.getValue())) {

			// get the search operator
			final Listitem item_UsrCanSignonTo = this.sortOperator_usrCanSignonTo.getSelectedItem();

			if (item_UsrCanSignonTo != null) {
				final int searchOpId = ((SearchOperators) item_UsrCanSignonTo.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("usrCanSignonTo", "%" + this.usrCanSignonTo.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("usrCanSignonTo", this.usrCanSignonTo.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_UsrCanOverrideLimits = this.sortOperator_usrCanOverrideLimits.getSelectedItem();

		if (item_UsrCanOverrideLimits != null) {
			final int searchOpId = ((SearchOperators) item_UsrCanOverrideLimits.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if(this.usrCanOverrideLimits.isChecked()){
					so.addFilter(new Filter("usrCanOverrideLimits",1, searchOpId));
				}else{
					so.addFilter(new Filter("usrCanOverrideLimits",0, searchOpId));	
				}
			}
		}
		// get the search operator
		final Listitem item_UsrAcExp = this.sortOperator_usrAcExp.getSelectedItem();

		if (item_UsrAcExp != null) {
			final int searchOpId = ((SearchOperators) item_UsrAcExp.getAttribute("data")).getSearchOperatorId();  

			if (searchOpId == -1) {
				// do nothing
			} else {

				if(this.usrAcExp.isChecked()){
					so.addFilter(new Filter("usrAcExp",1, searchOpId));
				}else{
					so.addFilter(new Filter("usrAcExp",0, searchOpId));	
				}
			}
		}
		// get the search operator
		final Listitem item_UsrCredentialsExp = this.sortOperator_usrCredentialsExp.getSelectedItem();

		if (item_UsrCredentialsExp != null) {
			final int searchOpId = ((SearchOperators) item_UsrCredentialsExp.getAttribute("data")).getSearchOperatorId();  

			if (searchOpId == -1) {
				// do nothing
			} else {

				if(this.usrCredentialsExp.isChecked()){
					so.addFilter(new Filter("usrCredentialsExp",1, searchOpId));
				}else{
					so.addFilter(new Filter("usrCredentialsExp",0, searchOpId));	
				}
			}
		}
		// get the search operator
		final Listitem item_UsrAcLocked = this.sortOperator_usrAcLocked.getSelectedItem();

		if (item_UsrAcLocked != null) {
			final int searchOpId = ((SearchOperators) item_UsrAcLocked.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if(this.usrAcLocked.isChecked()){
					so.addFilter(new Filter("usrAcLocked",1, searchOpId));
				}else{
					so.addFilter(new Filter("usrAcLocked",0, searchOpId));	
				}
			}
		}
		if (StringUtils.isNotEmpty(this.usrLanguage.getValue())) {

			// get the search operator
			final Listitem item_UsrLanguage = this.sortOperator_usrLanguage.getSelectedItem();

			if (item_UsrLanguage != null) {
				final int searchOpId = ((SearchOperators) item_UsrLanguage.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
				   so.addFilter(new Filter("usrLanguage", "%" +this.usrLanguage.getValue().toUpperCase()+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("usrLanguage", this.usrLanguage.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.usrBranchCode.getValue())) {

			// get the search operator
			final Listitem item_UsrBranchCode = this.sortOperator_usrBranchCode.getSelectedItem();

			if (item_UsrBranchCode != null) {
				final int searchOpId = ((SearchOperators) item_UsrBranchCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("usrBranchCode", "%"  + this.usrBranchCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("usrBranchCode", this.usrBranchCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.usrDeptCode.getValue())) {

			// get the search operator
			final Listitem item_UsrDeptCode = this.sortOperator_usrDeptCode.getSelectedItem();

			if (item_UsrDeptCode != null) {
				final int searchOpId = ((SearchOperators) item_UsrDeptCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("usrDeptCode", "%"  + this.usrDeptCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("usrDeptCode", this.usrDeptCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem item_RecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (item_RecordStatus != null) {
				final int searchOpId = ((SearchOperators) item_RecordStatus.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%"  + this.recordStatus.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordStatus", this.recordStatus.getValue(), searchOpId));
				}
			}
		}

		String selectedValue="";
		if (this.recordType.getSelectedItem()!=null){
			selectedValue =this.recordType.getSelectedItem().getValue().toString();
		}

		if (StringUtils.isNotEmpty(selectedValue)) {
			// get the search operator
			final Listitem item_RecordType = this.sortOperator_recordType.getSelectedItem();
			if (item_RecordType!= null) {
				final int searchOpId = ((SearchOperators) item_RecordType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordType", "%" + selectedValue.toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordType", selectedValue, searchOpId));
				}
			}
		}
		// Default Sort on the table
		so.addSort("UsrID", false);

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = so.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / " + filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}
	   /*Here object is overHanded parameter .object can be instance of SeurityusersListCtrl
        *,SecurityUserRolesListCtrl,SecurityUserChangePasswordListCtrl*/
		/* store the searchObject for reReading */
		    object.getClass().getMethod("setSearchObj"
		    		,Class.forName( "com.pennant.backend.util.JdbcSearchObject" )).invoke(object, so);
			
			// set the model to the listBox with the initial result set get by the DAO method.
			((PagedListWrapper<SecurityUser>) listBox.getModel()).init(so, listBox, paging);
		    object.getClass().getMethod("setSearchObj"
		    		,Class.forName( "com.pennant.backend.util.JdbcSearchObject" )).invoke(object, so);
			this.label_SecurityUserSearchResult.setValue(
					 Labels.getLabel("label_SecurityUserSearchResult.value") + " "
					 + String.valueOf(paging.getTotalSize()));
		
		logger.debug("Leaving ");
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
}