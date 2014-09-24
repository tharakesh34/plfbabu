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
 * FileName    		:  ExtendedFieldHeaderSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  28-12-2011    														*
 *                                                                  						*
 * Modified Date    :  28-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 28-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.staticparms.extendedfieldheader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.pennant.backend.model.staticparms.ExtendedFieldHeader;
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

public class ExtendedFieldHeaderSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long	serialVersionUID	= -1108191185099038825L;
	private final static Logger logger = Logger.getLogger(ExtendedFieldHeaderSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_ExtendedFieldHeaderSearch; 	// autowired
	
	protected Combobox 	moduleName; 						// autowired
	protected Listbox 	sortOperator_moduleName; 			// autowired
	protected Combobox 	subModuleName; 						// autowired
	protected Listbox 	sortOperator_subModuleName; 		// autowired
	protected Textbox 	tabHeading; 						// autowired
	protected Listbox 	sortOperator_tabHeading; 			// autowired
  	protected Intbox 	numberOfColumns; 					// autowired
  	protected Listbox 	sortOperator_numberOfColumns; 		// autowired
	protected Textbox 	recordStatus; 						// autowired
	protected Listbox 	recordType;							// autowired
	protected Listbox 	sortOperator_recordStatus; 			// autowired
	protected Listbox 	sortOperator_recordType; 			// autowired
	
	protected Label label_ExtendedFieldHeaderSearch_RecordStatus; 	// autowired
	protected Label label_ExtendedFieldHeaderSearch_RecordType; 	// autowired
	protected Label label_ExtendedFieldHeaderSearchResult; 			// autowired

	// not auto wired vars
	private transient ExtendedFieldHeaderListCtrl extendedFieldHeaderCtrl; // overhanded per param
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("ExtendedFieldHeader");
	private JdbcSearchObject<ExtendedFieldHeader> searchObj;
	private final HashMap<String, HashMap<String, String>> moduleMap = PennantStaticListUtil.getModuleName();
	private List<ValueLabel> modulesList = null;
	/**
	 * constructor
	 */
	public ExtendedFieldHeaderSearchCtrl() {
		super();
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_ExtendedFieldHeaderSearch(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		
		if (modulesList == null) {
			ValueLabel valuLable = null;
			modulesList = new ArrayList<ValueLabel>(moduleMap.size());
			Set<String> moduleKeys = moduleMap.keySet();
			for (String key : moduleKeys) {
				valuLable = new ValueLabel(key,Labels.getLabel("label_ExtendedField_" + key));
				modulesList.add(valuLable);
			}
		}
		
		
		try{
		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("extendedFieldHeaderCtrl")) {
			this.extendedFieldHeaderCtrl = (ExtendedFieldHeaderListCtrl) args.get("extendedFieldHeaderCtrl");
		} else {
			this.extendedFieldHeaderCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		
	
		this.sortOperator_moduleName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_moduleName.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_subModuleName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_subModuleName.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_tabHeading.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_tabHeading.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_numberOfColumns.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_numberOfColumns.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
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
			this.label_ExtendedFieldHeaderSearch_RecordStatus.setVisible(false);
			this.label_ExtendedFieldHeaderSearch_RecordType.setVisible(false);
		}
		
		fillComboBox(moduleName, null, modulesList, "");
		fillsubModule(subModuleName, "", "");
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			this.searchObj = (JdbcSearchObject<ExtendedFieldHeader>) args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
			    if (filter.getProperty().equals("moduleName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_moduleName, filter);
					fillComboBox(moduleName, filter.getValue().toString(), modulesList, "");
			    } else if (filter.getProperty().equals("subModuleName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_subModuleName, filter);
					this.subModuleName.setValue(filter.getValue().toString());
					String moduleName = this.moduleName.getSelectedItem().getValue().toString().equals("#") ? "" : 
						                        this.moduleName.getSelectedItem().getValue().toString();
					fillsubModule(subModuleName, moduleName, filter.getValue().toString());
			    } else if (filter.getProperty().equals("tabHeading")) {
					SearchOperators.restoreStringOperator(this.sortOperator_tabHeading, filter);
					this.tabHeading.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("numberOfColumns")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_numberOfColumns, filter);
			    	this.numberOfColumns.setText(filter.getValue().toString());

					
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
		showExtendedFieldHeaderSeekDialog();
		} catch (Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
			e.printStackTrace();
			window_ExtendedFieldHeaderSearch.onClose();
		}
	
		logger.debug("Leaving" + event.toString());
	}

	
	
	public void onChange$moduleName(Event event){
		logger.debug("Entering  :" + event.toString());
        if(!this.moduleName.getSelectedItem().getValue().toString().equals("#")){
               fillsubModule(subModuleName, this.moduleName.getSelectedItem().getValue().toString(), "");
        } else {
            fillsubModule(subModuleName, "", "");
        }
		logger.debug("Leaving  :" + event.toString());
	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * when the "search/filter" button is clicked.
	 * 
	 * @param event
	 */
	public void onClick$btnSearch(Event event) {
		logger.debug(event.toString());
		doSearch();
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doClose();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * closes the dialog window
	 */
	private void doClose() {
		this.window_ExtendedFieldHeaderSearch.onClose();
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showExtendedFieldHeaderSeekDialog() throws InterruptedException {

		try {
			// open the dialog in modal mode
			this.window_ExtendedFieldHeaderSearch.doModal();
		} catch (final Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
		}
	}

	
	private void fillsubModule(Combobox combobox, String moduleName, String value) {
		if (this.moduleName.getSelectedItem() != null) {
			HashMap<String, String> hashMap = PennantStaticListUtil.getModuleName().get(moduleName) == null ? new HashMap<String, String>()
					: PennantStaticListUtil.getModuleName().get(moduleName);
			ArrayList<String> arrayList = new ArrayList<String>(hashMap.keySet());
			subModuleName.getItems().clear();
			Comboitem comboitem = new Comboitem();
			comboitem.setLabel("----Select-----");
			comboitem.setValue("");
			subModuleName.appendChild(comboitem);
			subModuleName.setSelectedItem(comboitem);
			if (arrayList != null) {
				for (int i = 0; i < arrayList.size(); i++) {
					comboitem = new Comboitem();
					comboitem.setLabel(Labels.getLabel("label_ExtendedField_"+arrayList.get(i)));
					comboitem.setValue(arrayList.get(i));
					subModuleName.appendChild(comboitem);
					if (StringUtils.trimToEmpty(value).equals(arrayList.get(i))) {
						subModuleName.setSelectedItem(comboitem);
					}
				}
			}
		} else {
			subModuleName.getItems().clear();
		}
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Search/filter data for the filled out fields<br>
	 * <br>
	 * 1. Checks for each textbox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */ 
	@SuppressWarnings("unchecked")
	public void doSearch() {
		logger.debug("Entering");
		
		final JdbcSearchObject<ExtendedFieldHeader> so = new JdbcSearchObject<ExtendedFieldHeader>(ExtendedFieldHeader.class);
		List<Filter> filters =this.searchObj.getFilters();
		 for (int i = 0; i < filters.size(); i++) {
		 Filter filter= filters.get(i);
		 so.addFilter  (new   Filter(filter.getProperty(),filter.getValue(),filter.getOperator()));
		}

		if(!StringUtils.trimToEmpty(this.searchObj.getWhereClause()).equals("")){
		 so.addWhereClause(this.searchObj.getWhereClause());
		}

		 so.setSorts(this.searchObj.getSorts());
		 so.addTabelName(this.searchObj.getTabelName());

		if (isWorkFlowEnabled()){
			so.addTabelName("ExtendedFieldHeader_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("ExtendedFieldHeader_AView");
		}
		
		
		
		if (!this.moduleName.getSelectedItem().getValue().toString().equals("#")) {

			// get the search operator
			final Listitem itemModuleName = this.sortOperator_moduleName.getSelectedItem();
			if (itemModuleName != null) {
				final int searchOpId = ((SearchOperators) itemModuleName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("moduleName", "%" + this.moduleName.getSelectedItem().getValue().toString().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("moduleName", this.moduleName.getSelectedItem().getValue().toString(), searchOpId));
				}
			}
		}
		if (!this.subModuleName.getSelectedItem().getValue().toString().equals("#")
				&& !this.subModuleName.getSelectedItem().getValue().toString().equals("")) {

			// get the search operator
			final Listitem itemSubModuleName = this.sortOperator_subModuleName.getSelectedItem();
			if (itemSubModuleName != null) {
				final int searchOpId = ((SearchOperators) itemSubModuleName.getAttribute("data")).getSearchOperatorId();
				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("subModuleName", "%" + this.subModuleName.getSelectedItem().getValue().toString().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("subModuleName", this.subModuleName.getSelectedItem().getValue().toString(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.tabHeading.getValue())) {

			// get the search operator
			final Listitem itemTabHeading = this.sortOperator_tabHeading.getSelectedItem();
			if (itemTabHeading != null) {
				final int searchOpId = ((SearchOperators) itemTabHeading.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("tabHeading", "%" + this.tabHeading.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("tabHeading", this.tabHeading.getValue(), searchOpId));
				}
			}
		}
	  if (this.numberOfColumns.getValue()!=null) {	  
	    final Listitem itemNumberOfColumns = this.sortOperator_numberOfColumns.getSelectedItem();
	  	if (itemNumberOfColumns != null) {
	 		final int searchOpId = ((SearchOperators) itemNumberOfColumns.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.numberOfColumns.getValue()!=null){
	 				so.addFilter(new Filter("numberOfColumns",1, searchOpId));
	 			}else{
	 				so.addFilter(new Filter("numberOfColumns",0, searchOpId));	
	 			}
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
		// Defualt Sort on the table
		so.addSort("ModuleId", false);

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = so.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / " + filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}

		// store the searchObject for reReading
		this.extendedFieldHeaderCtrl.setSearchObj(so);

		final Listbox listBox = this.extendedFieldHeaderCtrl.listBoxExtendedFieldHeader;
		final Paging paging = this.extendedFieldHeaderCtrl.pagingExtendedFieldHeaderList;
		

		// set the model to the listbox with the initial resultset get by the DAO method.
		((PagedListWrapper<ExtendedFieldHeader>) listBox.getModel()).init(so, listBox, paging);
		this.extendedFieldHeaderCtrl.setSearchObj(so);

		this.label_ExtendedFieldHeaderSearchResult.setValue(Labels.getLabel("label_ExtendedFieldHeaderSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
		paging.setActivePage(0);
		logger.debug("Leaving");
	}

}