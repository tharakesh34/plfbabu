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
 * FileName    		:  SecurityRoleSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  10-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *  10-08-2011         Pennant	                 0.1                                            * 
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
package com.pennant.webui.administration.securityrole;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.service.administration.SecurityRoleService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Administration/SecurityUsers/SecurityRoleSearchDialog.zul
 * file.
 */
public class SecurityRoleSearchCtrl extends GFCBaseCtrl<SecurityRole>  {
	private static final long serialVersionUID = 2864504890948057555L;
	private static final Logger logger = Logger.getLogger(SecurityRoleSearchCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window     window_SecurityRoleSearch;               // autoWired
	protected Intbox     roleID;                                  // autoWired
	protected Listbox    sortOperator_roleID;                     // autoWired
	protected Combobox   roleApp;                                 // autoWired
	protected Listbox    sortOperator_roleApp;                    // autoWired
	protected Textbox    roleCd;                                  // autoWired
	protected Listbox    sortOperator_roleCd;                     // autoWired
	protected Textbox    roleDesc;                                // autoWired
	protected Listbox    sortOperator_roleDesc;                   // autoWired
	protected Textbox    roleCategory;                            // autoWired
	protected Listbox    sortOperator_roleCategory;               // autoWired
	protected Listbox    recordType;	                          // autoWired
	protected Listbox    sortOperator_recordStatus;               // autoWired
	protected Listbox    sortOperator_recordType;                 // autoWired
	protected Label      label_SecurityRoleSearch_RecordStatus;   // autoWired
	protected Label      label_SecurityRoleSearch_RecordType;     // autoWired
	protected Label      label_SecurityRoleSearchResult;          // autoWired
	
	// not auto wired variables
	private transient SecurityRoleService  securityRoleService;
	private transient WorkFlowDetails      workFlowDetails=WorkFlowUtil.getWorkFlowDetails("SecurityRole");
	private Listbox listBox;
	private Paging  paging;
	private Object  object;
	private List<ValueLabel> listAppCodes = PennantStaticListUtil.getAppCodes();
	
	/**
	 * constructor
	 */
	public SecurityRoleSearchCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events
	
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected SecurityRole object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_SecurityRoleSearch(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SecurityRoleSearch);

		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

		/*Here object is either SecurityRoleListCtrl object or securityRoleGroupsListCtrl object */
		if (arguments.containsKey("securityRoleCtrl")) {
			object  =(Object)arguments.get("securityRoleCtrl");
			listBox = (Listbox)arguments.get("listBoxSecurityRole");
			paging  = (Paging)arguments.get("pagingSecurityRoleList");

		}
		setApplicationCodes();
		
		// DropDown ListBox
		this.sortOperator_roleID.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_roleID.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_roleApp.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_roleApp.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_roleCd.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_roleCd.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_roleDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_roleDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_roleCategory.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_roleCategory.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=setRecordType(this.recordType);	
		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_SecurityRoleSearch_RecordStatus.setVisible(false);
			this.label_SecurityRoleSearch_RecordType.setVisible(false);
		}

		// Restore the search mask input definition
		// if exists a searchObject than show formerly inputs of filter values
		if (arguments.containsKey("searchObject")) {
			final JdbcSearchObject<SecurityRole> searchObj = (JdbcSearchObject<SecurityRole>) arguments.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if ("roleID".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_roleID, filter);
					this.roleID.setValue(Integer.parseInt(filter.getValue().toString()));
				} else if ("roleApp".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_roleApp, filter);
					List<Comboitem> items=this.roleApp.getItems();
					for(Comboitem comboItem:items){
						if(StringUtils.equals(comboItem.getValue().toString(),filter.getValue().toString())){
							this.roleApp.setSelectedItem(comboItem);
						}
					}
				} else if ("roleCd".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_roleCd, filter);
					this.roleCd.setValue(filter.getValue().toString());
				} else if ("roleDesc".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_roleDesc, filter);
					this.roleDesc.setValue(filter.getValue().toString());
				} else if ("roleCategory".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_roleCategory, filter);
					this.roleCategory.setValue(filter.getValue().toString());
				} else if ("recordStatus".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordStatus, filter);
					this.recordStatus.setValue(filter.getValue().toString());
				} else if ("recordType".equals(filter.getProperty())) {
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
		showSecurityRoleSeekDialog();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "search/filter" button is clicked.
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$btnSearch(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doSearch();
		logger.debug("Leaving ");
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(false);
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showSecurityRoleSeekDialog() throws InterruptedException {
		logger.debug("Entering ");
		try {
			// open the dialog in modal mode
			this.window_SecurityRoleSearch.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving ");
	}
	
	/**
	 * This method fills ComboBox roleApp with Application Names 
	 */
	private void setApplicationCodes() {
		logger.debug("Entering ");
		for (int i = 0; i < listAppCodes.size(); i++) {

			Comboitem comboitem = new Comboitem();
			comboitem = new Comboitem();
			comboitem.setLabel(listAppCodes.get(i).getLabel());
			comboitem.setValue(listAppCodes.get(i).getValue());
			this.roleApp.appendChild(comboitem);
			this.roleApp.setSelectedIndex(0);
		}
		logger.debug("Leaving ");
	}
	
	/**
	 * Search/filter data for the filled out fields<br>
	 * <br>
	 * 1. Checks for each textBox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 * @throws Exception 
	 */ 
	@SuppressWarnings("unchecked")
	public void doSearch() throws Exception {
		logger.debug("Entering ");
		final JdbcSearchObject<SecurityRole> so = new JdbcSearchObject<SecurityRole>(SecurityRole.class);
		so.addTabelName("SecRoles_View");

		if (isWorkFlowEnabled()){
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}

		if (this.roleID.getValue()!= null) {

			// get the search operator
			final Listitem itemRoleID = this.sortOperator_roleID.getSelectedItem();
			if (itemRoleID != null) {
				final int searchOpId = ((SearchOperators) itemRoleID.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("roleID", "%" + this.roleID.getValue() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("roleID", this.roleID.getValue(), searchOpId));
				}
			}
		}
		if ((this.roleApp.getValue()!=null) 
				&& (!StringUtils.equals(this.roleApp.getSelectedItem().getLabel(), Labels.getLabel("common.Select")))) {

			// get the search operator
			final Listitem itemRoleApp = this.sortOperator_roleApp.getSelectedItem();
			if (itemRoleApp != null) {
				final int searchOpId = ((SearchOperators) itemRoleApp.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("roleApp", "%" + this.roleApp.getSelectedItem().getValue(),searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("roleApp", this.roleApp.getSelectedItem().getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.roleCd.getValue())) {

			// get the search operator
			final Listitem itemRoleCd = this.sortOperator_roleCd.getSelectedItem();
			if (itemRoleCd != null) {
				final int searchOpId = ((SearchOperators) itemRoleCd.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("roleCd", "%" + this.roleCd.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("roleCd", this.roleCd.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.roleDesc.getValue())) {

			// get the search operator
			final Listitem itemRoleDesc = this.sortOperator_roleDesc.getSelectedItem();
			if (itemRoleDesc != null) {
				final int searchOpId = ((SearchOperators) itemRoleDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("roleDesc", "%"	+ this.roleDesc.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("roleDesc", this.roleDesc.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.roleCategory.getValue())) {

			// get the search operator
			final Listitem itemRoleCategory = this.sortOperator_roleCategory.getSelectedItem();
			if (itemRoleCategory != null) {
				final int searchOpId = ((SearchOperators) itemRoleCategory.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("roleCategory", "%" + this.roleCategory.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("roleCategory", this.roleCategory.getValue(),searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem itemRecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (itemRecordStatus != null) {
				final int searchOpId = ((SearchOperators) itemRecordStatus.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%"	+ this.recordStatus.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordStatus", this.recordStatus.getValue(),searchOpId));
				}
			}
		}

		String selectedValue="";
		if (this.recordType.getSelectedItem()!=null){
			selectedValue =this.recordType.getSelectedItem().getValue().toString();
		}

		if (StringUtils.isNotEmpty(selectedValue)) {
			// get the search operator
			final Listitem itemRecordType = this.sortOperator_recordType.getSelectedItem();
			if (itemRecordType!= null) {
				final int searchOpId = ((SearchOperators) itemRecordType.getAttribute("data")).getSearchOperatorId();

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
		so.addSort("RoleID", false);

		/*Here object is either SecurityRoleListCtrl object or securityRoleGroupsListCtrl object  */
		// store the searchObject for reReading
		object.getClass().getMethod("setSearchObj"
				,Class.forName( "com.pennant.backend.util.JdbcSearchObject" )).invoke(object, so);

		// set the model to the listBox with the initial result set get by the DAO method.
		((PagedListWrapper<SecurityRole>) listBox.getModel()).init(so, listBox, paging);
		object.getClass().getMethod("setSearchObj"
				,Class.forName( "com.pennant.backend.util.JdbcSearchObject" )).invoke(object, so);
		this.label_SecurityRoleSearchResult.setValue(
				Labels.getLabel("label_SecurityRoleSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));

		logger.debug("Leaving ");

	}
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setSecurityRoleService(SecurityRoleService securityRoleService) {
		this.securityRoleService = securityRoleService;
	}
	public SecurityRoleService getSecurityRoleService() {
		return this.securityRoleService;
	}
}