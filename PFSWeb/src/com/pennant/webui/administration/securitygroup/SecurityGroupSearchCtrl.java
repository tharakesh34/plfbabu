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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
 * /WEB-INF/pages/Administration/SecurityUsers/SecurityGroupSearchDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class SecurityGroupSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -1495995793043106184L;
	private final static Logger logger = Logger.getLogger(SecurityGroupSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	
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
		
		/*Here object is over handed parameter .object can be instance of securityGroupListCtrl
		 * or securityGroupRightsListCtrl*/
		if (args.containsKey("securityGroupCtrl")) {
			object =(Object)args.get("securityGroupCtrl");
		    listBox=(Listbox)args.get("listBoxSecurityGroup");
		    paging=(Paging)args.get("pagingSecurityGroupList");
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

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
			this.recordType=PennantAppUtil.setRecordType(this.recordType);	
		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_SecurityGroupSearch_RecordStatus.setVisible(false);
			this.label_SecurityGroupSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<SecurityGroup> searchObj = (JdbcSearchObject<SecurityGroup>) args
			.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("grpID")) {
					SearchOperators.restoreStringOperator(this.sortOperator_grpID, filter);
					this.grpID.setValue(Integer.parseInt(filter.getValue().toString()));
				} else if (filter.getProperty().equals("grpCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_grpCode, filter);
					this.grpCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("grpDesc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_grpDesc, filter);
					this.grpDesc.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("recordStatus")) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordStatus, filter);
					this.recordStatus.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("recordType")) {
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
		this.window_SecurityGroupSearch.onClose();
		logger.debug("Leaving ");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showSecurityGroupSeekDialog() throws InterruptedException {
		logger.debug("Entering ");
		try {
			// open the dialog in modal mode
			this.window_SecurityGroupSearch.doModal();
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
			final Listitem item_GrpID = this.sortOperator_grpID.getSelectedItem();

			if (item_GrpID != null) {
				final int searchOpId = ((SearchOperators) item_GrpID.getAttribute("data")).getSearchOperatorId();

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
			final Listitem item_GrpCode = this.sortOperator_grpCode.getSelectedItem();

			if (item_GrpCode != null) {
				final int searchOpId = ((SearchOperators) item_GrpCode.getAttribute("data")).getSearchOperatorId();

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
			final Listitem item_GrpDesc = this.sortOperator_grpDesc.getSelectedItem();

			if (item_GrpDesc != null) {
				final int searchOpId = ((SearchOperators) item_GrpDesc.getAttribute("data")).getSearchOperatorId();

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
			final Listitem item_RecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (item_RecordStatus != null) {
				final int searchOpId = ((SearchOperators) item_RecordStatus
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
		so.addSort("GrpID", false);

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = so.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / " + filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}
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
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setSecurityGroupService(SecurityGroupService securityGroupService) {
		this.securityGroupService = securityGroupService;
	}

	public SecurityGroupService getSecurityGroupService() {
		return this.securityGroupService;
	}
}