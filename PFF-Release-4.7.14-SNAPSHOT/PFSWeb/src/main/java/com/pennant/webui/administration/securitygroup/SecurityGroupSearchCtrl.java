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
 * FileName    		:  SecurityGroupSearchCtrl.java                                                   * 	  
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
 * 10-08-2011        Pennant	                 0.1                                            * 
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
package com.pennant.webui.administration.securitygroup;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.administration.SecurityGroup;
import com.pennant.backend.service.administration.SecurityGroupService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Administration/SecurityUsers/SecurityGroupSearchDialog.zul
 * file.
 */
public class SecurityGroupSearchCtrl extends GFCBaseCtrl<SecurityGroup>  {

	private static final long serialVersionUID = -1495995793043106184L;
	private static final Logger logger = Logger.getLogger(SecurityGroupSearchCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window   window_SecurityGroupSearch;                              // autoWired
	protected Intbox   grpID;                                                   // autoWired
	protected Listbox  sortOperator_grpID;                                      // autoWired
	protected Textbox  grpCode;                                                 // autoWired
	protected Listbox  sortOperator_grpCode;                                    // autoWired
	protected Textbox  grpDesc;                                                 // autoWired
	protected Listbox  sortOperator_grpDesc;                                    // autoWired
	protected Textbox  recordStatus;                                            // autoWired
	protected Listbox  recordType;                                             	// autoWired
	protected Listbox  sortOperator_recordStatus;                               // autoWired
	protected Listbox  sortOperator_recordType;                                 // autoWired
	protected Label    label_SecurityGroupSearch_RecordStatus;                  // autoWired
	protected Label    label_SecurityGroupSearch_RecordType;                    // autoWired
	protected Label    label_SecurityGroupSearchResult;                         // autoWired
	
	// not auto wired variables
	private transient SecurityGroupService securityGroupService;
	private transient WorkFlowDetails       workFlowDetails=WorkFlowUtil.getWorkFlowDetails("SecurityGroup");
	private transient Object                object;
	private transient Listbox               listBox;
	private transient Paging                paging;
	
	/**
	 * constructor
	 */
	public SecurityGroupSearchCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events
	
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected SecurityGroup object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_SecurityGroupSearch(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SecurityGroupSearch);

		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

		/*Here object is over handed parameter .object can be instance of securityGroupListCtrl
		 * or securityGroupRightsListCtrl*/
		if (arguments.containsKey("securityGroupCtrl")) {
			object =(Object)arguments.get("securityGroupCtrl");
		    listBox=(Listbox)arguments.get("listBoxSecurityGroup");
		    paging=(Paging)arguments.get("pagingSecurityGroupList");
		}

		// DropDown ListBox

		this.sortOperator_grpID.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_grpID.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_grpCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_grpCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_grpDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_grpDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
			this.label_SecurityGroupSearch_RecordStatus.setVisible(false);
			this.label_SecurityGroupSearch_RecordType.setVisible(false);
		}

		// Restore the search mask input definition
		// if exists a searchObject than show formerly inputs of filter values
		if (arguments.containsKey("searchObject")) {
			final JdbcSearchObject<SecurityGroup> searchObj = (JdbcSearchObject<SecurityGroup>) arguments
			.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if ("grpID".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_grpID, filter);
					this.grpID.setValue(Integer.parseInt(filter.getValue().toString()));
				} else if ("grpCode".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_grpCode, filter);
					this.grpCode.setValue(filter.getValue().toString());
				} else if ("grpDesc".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_grpDesc, filter);
					this.grpDesc.setValue(filter.getValue().toString());
				} else if ("recordStatus".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordStatus, filter);
					this.recordStatus.setValue(filter.getValue().toString());
				} else if ("recordType".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordType, filter);
					for (int i = 0; i < this.recordType.getItemCount(); i++) {
						if (this.recordType.getItemAtIndex(i).getValue().equals(filter.getValue().toString())){
							this.recordType.setSelectedIndex(i);
						}
					}
				}
			}
		}
		showSecurityGroupSeekDialog();
		logger.debug("Leaving " + event.toString());
	}

	// Components events

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
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *			  An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(false);
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showSecurityGroupSeekDialog() throws InterruptedException {
		logger.debug("Entering ");
		try {
			// open the dialog in modal mode
			this.window_SecurityGroupSearch.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
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
		final JdbcSearchObject<SecurityGroup> so = new JdbcSearchObject<SecurityGroup>(SecurityGroup.class);
		so.addTabelName("SecGroups_View");

		if (isWorkFlowEnabled()){
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}

		if (this.grpID.getValue()!=null) {

			// get the search operator
			final Listitem itemGrpID = this.sortOperator_grpID.getSelectedItem();
			if (itemGrpID != null) {
				final int searchOpId = ((SearchOperators) itemGrpID.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("grpID", "%" + this.grpID.getValue(), searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("grpID", this.grpID.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.grpCode.getValue())) {

			// get the search operator
			final Listitem itemGrpCode = this.sortOperator_grpCode.getSelectedItem();
			if (itemGrpCode != null) {
				final int searchOpId = ((SearchOperators) itemGrpCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("grpCode", "%" + this.grpCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("grpCode", this.grpCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.grpDesc.getValue())) {

			// get the search operator
			final Listitem itemGrpDesc = this.sortOperator_grpDesc.getSelectedItem();
			if (itemGrpDesc != null) {
				final int searchOpId = ((SearchOperators) itemGrpDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("grpDesc", "%" + this.grpDesc.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("grpDesc", this.grpDesc.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem itemRecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (itemRecordStatus != null) {
				final int searchOpId = ((SearchOperators) itemRecordStatus
						  .getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%" + this.recordStatus.getValue().toUpperCase() + "%", searchOpId));
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
		so.addSort("GrpID", false);

		/*Here object is over handed parameter .object can be instance of securityGroupListCtrl
		 * or securityGroupRightsListCtrl*/
		// store the searchObject for reReading
		object.getClass().getMethod("setSearchObj"
				,Class.forName( "com.pennant.backend.util.JdbcSearchObject" )).invoke(object, so);
		// set the model to the listBox with the initial result set get by the DAO method.
		((PagedListWrapper<SecurityGroup>) listBox.getModel()).init(so, listBox, paging);
		// store the searchObject for reReading
		object.getClass().getMethod("setSearchObj"
				,Class.forName( "com.pennant.backend.util.JdbcSearchObject" )).invoke(object, so);
		this.label_SecurityGroupSearchResult.setValue(
				Labels.getLabel("label_SecurityGroupSearchResult.value") 
				+ " "+ String.valueOf(paging.getTotalSize()));

		logger.debug("Leaving ");
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setSecurityGroupService(SecurityGroupService securityGroupService) {
		this.securityGroupService = securityGroupService;
	}

	public SecurityGroupService getSecurityGroupService() {
		return this.securityGroupService;
	}
}