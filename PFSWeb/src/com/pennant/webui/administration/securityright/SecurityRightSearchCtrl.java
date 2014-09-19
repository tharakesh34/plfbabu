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
 * FileName    		:  SecurityRightSearchCtrl.java                                                   * 	  
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
import java.util.List;
import java.util.Map;

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
import com.pennant.backend.model.administration.SecurityRight;
import com.pennant.backend.service.administration.SecurityRightService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantStaticListUtil;
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
 * /WEB-INF/pages/Administration/SecurityUsers/SecurityRightSearchDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class SecurityRightSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 8273724192810448161L;
	private final static Logger logger = Logger.getLogger(SecurityRightSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window   window_SecurityRightSearch;              // autoWired
	protected Intbox   rightID;                                 // autoWired
	protected Listbox  sortOperator_rightID;                    // autoWired
	protected Combobox rightType;                               // autoWired
	protected Listbox  sortOperator_rightType;                  // autoWired
	protected Textbox  rightName;                               // autoWired
	protected Listbox  sortOperator_rightName;                  // autoWired
	protected Textbox  recordStatus;                            // autoWired
	protected Listbox  recordType;	                            // autoWired
	protected Listbox  sortOperator_recordStatus;               // autoWired
	protected Listbox  sortOperator_recordType;                 // autoWired
	protected Label    label_SecurityRightSearch_RecordStatus;  // autoWired
	protected Label    label_SecurityRightSearch_RecordType;    // autoWired
	protected Label    label_SecurityRightSearchResult;         // autoWired
	
	// not auto wired variables
	private transient SecurityRightService  securityRightService;
	private transient WorkFlowDetails  workFlowDetails=WorkFlowUtil.getWorkFlowDetails("SecurityRight");
	private List<ValueLabel>           listRightType = PennantStaticListUtil.getRightType();
    private Listbox listBox;
    private Paging  paging;
	private Object  object;
	
	/**
	 * constructor
	 */
	public SecurityRightSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected SecurityRight object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_SecurityRightSearch(Event event) throws Exception {
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

		if (args.containsKey("securityRightCtrl")) {
			object  =(Object)args.get("securityRightCtrl");
			listBox = (Listbox)args.get("listBoxSecurityRight");
			paging  = (Paging)args.get("pagingSecurityRightList");
		}
		else{
			object=null;
		}
		setListRightType();
		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_rightID.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_rightID.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_rightType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_rightType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_rightName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_rightName.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
			this.label_SecurityRightSearch_RecordStatus.setVisible(false);
			this.label_SecurityRightSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<SecurityRight> searchObj = (JdbcSearchObject<SecurityRight>) args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("rightID")) {
					SearchOperators.restoreStringOperator(this.sortOperator_rightID, filter);
					this.rightID.setValue(Integer.parseInt(filter.getValue().toString()));
				} else if (filter.getProperty().equals("rightType")) {
						SearchOperators.restoreStringOperator(this.sortOperator_rightType, filter);
						//Upgraded to ZK-6.5.1.1 Changed from get children to get items 	
						List<Comboitem> items=this.rightType.getItems();
						for(Comboitem comboItem:items){
							if(StringUtils.equals(comboItem.getValue().toString(),filter.getValue().toString())){
								this.rightType.setSelectedItem(comboItem);
						}
					}
				} else if (filter.getProperty().equals("rightName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_rightName, filter);
					this.rightName.setValue(filter.getValue().toString());
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
		showSecurityRightSeekDialog();
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * This method sets all rightsTypes as ComboItems for ComboBox
	 */
	private void setListRightType() {
		logger.debug("Entering ");
		for (int i = 0; i < listRightType.size(); i++) {

			Comboitem comboitem = new Comboitem();
			comboitem = new Comboitem();
			comboitem.setLabel(listRightType.get(i).getLabel());
			comboitem.setValue(listRightType.get(i).getValue());
			this.rightType.appendChild(comboitem);
		}
		this.rightType.setSelectedIndex(0);
		logger.debug("Leaving ");
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
		this.window_SecurityRightSearch.onClose();
		logger.debug("Leaving ");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showSecurityRightSeekDialog() throws InterruptedException {
		logger.debug("Entering ");
		try {
			// open the dialog in modal mode
			this.window_SecurityRightSearch.doModal();
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
	 **/
	@SuppressWarnings("unchecked")
	public void doSearch() throws Exception {
		logger.debug("Entering ");

		final JdbcSearchObject<SecurityRight> so = new JdbcSearchObject<SecurityRight>(SecurityRight.class);
		so.addTabelName("SecRights_View");

		if (isWorkFlowEnabled()){
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}

		if (this.rightID.getValue()!=null) {

			// get the search operator
			final Listitem itemRightID = this.sortOperator_rightID.getSelectedItem();
			if (itemRightID != null) {
				final int searchOpId = ((SearchOperators) itemRightID.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("rightID", "%" + this.rightID.getValue(), searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("rightID", this.rightID.getValue(), searchOpId));
				}
			}
		}
		if ((this.rightType.getSelectedItem().getValue()!=null )
				&& (!StringUtils.equals(this.rightType.getSelectedItem().getLabel() ,Labels.getLabel("common.Select")))) {

			// get the search operator
			final Listitem itemRightType = this.sortOperator_rightType.getSelectedItem();
			if (itemRightType != null) {
				final int searchOpId = ((SearchOperators) itemRightType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("rightType", "%" + this.rightType.getSelectedItem().getValue(), searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("rightType", this.rightType.getSelectedItem().getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.rightName.getValue())) {

			// get the search operator
			final Listitem itemRightName = this.sortOperator_rightName.getSelectedItem();
			if (itemRightName != null) {
				final int searchOpId = ((SearchOperators) itemRightName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("rightName", "%" + this.rightName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("rightName", this.rightName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem itemRecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (itemRecordStatus != null) {
				final int searchOpId = ((SearchOperators) itemRecordStatus.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%" + this.recordStatus.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordStatus" , this.recordStatus.getValue(), searchOpId));
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
		so.addSort("RightID", false);

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = so.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / " + filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}
		object.getClass().getMethod("setSearchObj"
				,Class.forName( "com.pennant.backend.util.JdbcSearchObject" )).invoke(object, so);

		// set the model to the listBox with the initial result set get by the DAO method.
		((PagedListWrapper<SecurityRight>) listBox.getModel()).init(so, listBox, paging);
		object.getClass().getMethod("setSearchObj"
				,Class.forName( "com.pennant.backend.util.JdbcSearchObject" )).invoke(object, so);
		this.label_SecurityRightSearchResult.setValue(
				Labels.getLabel("label_SecurityRightSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving ");
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
}