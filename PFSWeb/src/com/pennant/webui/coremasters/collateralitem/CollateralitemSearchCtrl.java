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
 * FileName    		:  CollateralitemSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-02-2013    														*
 *                                                                  						*
 * Modified Date    :  20-02-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-02-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.coremasters.collateralitem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.coremasters.Collateralitem;
import com.pennant.backend.service.coremasters.CollateralitemService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;


public class CollateralitemSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(CollateralitemSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CollateralitemSearch; // autowired
	
	protected Textbox hYCUS; // autowired
	protected Listbox sortOperator_hYCUS; // autowired
	protected Textbox hYCLC; // autowired
	protected Listbox sortOperator_hYCLC; // autowired
	protected Textbox hYDLP; // autowired
	protected Listbox sortOperator_hYDLP; // autowired
	protected Textbox hYDLR; // autowired
	protected Listbox sortOperator_hYDLR; // autowired
	protected Textbox hYDBNM; // autowired
	protected Listbox sortOperator_hYDBNM; // autowired
	protected Textbox hYAB; // autowired
	protected Listbox sortOperator_hYAB; // autowired
	protected Textbox hYAN; // autowired
	protected Listbox sortOperator_hYAN; // autowired
	protected Textbox hYAS; // autowired
	protected Listbox sortOperator_hYAS; // autowired
	protected Textbox hYCLP; // autowired
	protected Listbox sortOperator_hYCLP; // autowired
	protected Textbox hYCLR; // autowired
	protected Listbox sortOperator_hYCLR; // autowired
	protected Textbox hYCLO; // autowired
	protected Listbox sortOperator_hYCLO; // autowired
	protected Textbox recordStatus; // autowired
	protected Listbox recordType;	// autowired
	protected Listbox sortOperator_recordStatus; // autowired
	protected Listbox sortOperator_recordType; // autowired
	
	protected Label label_CollateralitemSearch_RecordStatus; // autowired
	protected Label label_CollateralitemSearch_RecordType; // autowired
	protected Label label_CollateralitemSearchResult; // autowired

	// not auto wired vars
	private transient CollateralitemListCtrl collateralitemCtrl; // overhanded per param
	private transient CollateralitemService collateralitemService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("Collateralitem");
	private JdbcSearchObject<Collateralitem> searchObj;
	
	/**
	 * constructor
	 */
	public CollateralitemSearchCtrl() {
		super();
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_CollateralitemSearch(Event event) throws Exception {

		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("collateralitemCtrl")) {
			this.collateralitemCtrl = (CollateralitemListCtrl) args.get("collateralitemCtrl");
		} else {
			this.collateralitemCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_hYCUS.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_hYCUS.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_hYCLC.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_hYCLC.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_hYDLP.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_hYDLP.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_hYDLR.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_hYDLR.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_hYDBNM.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_hYDBNM.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_hYAB.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_hYAB.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_hYAN.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_hYAN.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_hYAS.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_hYAS.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_hYCLP.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_hYCLP.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_hYCLR.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_hYCLR.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_hYCLO.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_hYCLO.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
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
			this.label_CollateralitemSearch_RecordStatus.setVisible(false);
			this.label_CollateralitemSearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			searchObj = (JdbcSearchObject<Collateralitem>) args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();
			final List <Filter> rmvFilter = new ArrayList<Filter>();
			
			for (final Filter filter : ft) {

			// restore founded properties
			rmvFilter.add(filter);
			    if (filter.getProperty().equals("hYCUS")) {
					SearchOperators.restoreStringOperator(this.sortOperator_hYCUS, filter);
					this.hYCUS.setValue(filter.getValue().toString());
					
			    } else if (filter.getProperty().equals("hYCLC")) {
					SearchOperators.restoreStringOperator(this.sortOperator_hYCLC, filter);
					this.hYCLC.setValue(filter.getValue().toString());
					
			    } else if (filter.getProperty().equals("hYDLP")) {
					SearchOperators.restoreStringOperator(this.sortOperator_hYDLP, filter);
					this.hYDLP.setValue(filter.getValue().toString());
					
			    } else if (filter.getProperty().equals("hYDLR")) {
					SearchOperators.restoreStringOperator(this.sortOperator_hYDLR, filter);
					this.hYDLR.setValue(filter.getValue().toString());
					
			    } else if (filter.getProperty().equals("hYDBNM")) {
					SearchOperators.restoreStringOperator(this.sortOperator_hYDBNM, filter);
					this.hYDBNM.setValue(filter.getValue().toString());
					
			    } else if (filter.getProperty().equals("hYAB")) {
					SearchOperators.restoreStringOperator(this.sortOperator_hYAB, filter);
					this.hYAB.setValue(filter.getValue().toString());
					
			    } else if (filter.getProperty().equals("hYAN")) {
					SearchOperators.restoreStringOperator(this.sortOperator_hYAN, filter);
					this.hYAN.setValue(filter.getValue().toString());
					
			    } else if (filter.getProperty().equals("hYAS")) {
					SearchOperators.restoreStringOperator(this.sortOperator_hYAS, filter);
					this.hYAS.setValue(filter.getValue().toString());
					
			    } else if (filter.getProperty().equals("hYCLP")) {
					SearchOperators.restoreStringOperator(this.sortOperator_hYCLP, filter);
					this.hYCLP.setValue(filter.getValue().toString());
					
			    } else if (filter.getProperty().equals("hYCLR")) {
					SearchOperators.restoreStringOperator(this.sortOperator_hYCLR, filter);
					this.hYCLR.setValue(filter.getValue().toString());
					
			    } else if (filter.getProperty().equals("hYCLO")) {
					SearchOperators.restoreStringOperator(this.sortOperator_hYCLO, filter);
					this.hYCLO.setValue(filter.getValue().toString());
					
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
			for(int i =0 ; i < rmvFilter.size() ; i++){
				searchObj.removeFilter(rmvFilter.get(i));
			}			
		}
		showCollateralitemSeekDialog();
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
		this.window_CollateralitemSearch.onClose();
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showCollateralitemSeekDialog() throws InterruptedException {

		try {
			// open the dialog in modal mode
			this.window_CollateralitemSearch.doModal();
		} catch (final Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
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
		final JdbcSearchObject<Collateralitem> so = new JdbcSearchObject<Collateralitem>(Collateralitem.class);
		
		List<Filter> filters =this.searchObj.getFilters();
		 for (int i = 0; i < filters.size(); i++) {
		 Filter filter= filters.get(i);
		 so.addFilter  (new   Filter(filter.getProperty(),filter.getValue(),filter.getOperator()));
		}
		 
		 if(!StringUtils.trimToEmpty(this.searchObj.getWhereClause()).equals("")){
			 so.addWhereClause(new String(this.searchObj.getWhereClause()));
			}

			 so.setSorts(this.searchObj.getSorts());
			 so.addTabelName(this.searchObj.getTabelName());
		 
		if (isWorkFlowEnabled()){
			so.addTabelName("HYPF_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("HYPF_AView");
		}
		
		
		if (StringUtils.isNotEmpty(this.hYCUS.getValue())) {

			// get the search operator
			final Listitem item_HYCUS = this.sortOperator_hYCUS.getSelectedItem();

			if (item_HYCUS != null) {
				final int searchOpId = ((SearchOperators) item_HYCUS.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("hYCUS", "%" + this.hYCUS.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("hYCUS", this.hYCUS.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.hYCLC.getValue())) {

			// get the search operator
			final Listitem item_HYCLC = this.sortOperator_hYCLC.getSelectedItem();

			if (item_HYCLC != null) {
				final int searchOpId = ((SearchOperators) item_HYCLC.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("hYCLC", "%" + this.hYCLC.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("hYCLC", this.hYCLC.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.hYDLP.getValue())) {

			// get the search operator
			final Listitem item_HYDLP = this.sortOperator_hYDLP.getSelectedItem();

			if (item_HYDLP != null) {
				final int searchOpId = ((SearchOperators) item_HYDLP.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("hYDLP", "%" + this.hYDLP.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("hYDLP", this.hYDLP.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.hYDLR.getValue())) {

			// get the search operator
			final Listitem item_HYDLR = this.sortOperator_hYDLR.getSelectedItem();

			if (item_HYDLR != null) {
				final int searchOpId = ((SearchOperators) item_HYDLR.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("hYDLR", "%" + this.hYDLR.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("hYDLR", this.hYDLR.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.hYDBNM.getValue())) {

			// get the search operator
			final Listitem item_HYDBNM = this.sortOperator_hYDBNM.getSelectedItem();

			if (item_HYDBNM != null) {
				final int searchOpId = ((SearchOperators) item_HYDBNM.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("hYDBNM", "%" + this.hYDBNM.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("hYDBNM", this.hYDBNM.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.hYAB.getValue())) {

			// get the search operator
			final Listitem item_HYAB = this.sortOperator_hYAB.getSelectedItem();

			if (item_HYAB != null) {
				final int searchOpId = ((SearchOperators) item_HYAB.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("hYAB", "%" + this.hYAB.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("hYAB", this.hYAB.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.hYAN.getValue())) {

			// get the search operator
			final Listitem item_HYAN = this.sortOperator_hYAN.getSelectedItem();

			if (item_HYAN != null) {
				final int searchOpId = ((SearchOperators) item_HYAN.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("hYAN", "%" + this.hYAN.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("hYAN", this.hYAN.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.hYAS.getValue())) {

			// get the search operator
			final Listitem item_HYAS = this.sortOperator_hYAS.getSelectedItem();

			if (item_HYAS != null) {
				final int searchOpId = ((SearchOperators) item_HYAS.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("hYAS", "%" + this.hYAS.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("hYAS", this.hYAS.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.hYCLP.getValue())) {

			// get the search operator
			final Listitem item_HYCLP = this.sortOperator_hYCLP.getSelectedItem();

			if (item_HYCLP != null) {
				final int searchOpId = ((SearchOperators) item_HYCLP.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("hYCLP", "%" + this.hYCLP.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("hYCLP", this.hYCLP.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.hYCLR.getValue())) {

			// get the search operator
			final Listitem item_HYCLR = this.sortOperator_hYCLR.getSelectedItem();

			if (item_HYCLR != null) {
				final int searchOpId = ((SearchOperators) item_HYCLR.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("hYCLR", "%" + this.hYCLR.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("hYCLR", this.hYCLR.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.hYCLO.getValue())) {

			// get the search operator
			final Listitem item_HYCLO = this.sortOperator_hYCLO.getSelectedItem();

			if (item_HYCLO != null) {
				final int searchOpId = ((SearchOperators) item_HYCLO.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("hYCLO", "%" + this.hYCLO.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("hYCLO", this.hYCLO.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem item_RecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (item_RecordStatus != null) {
				final int searchOpId = ((SearchOperators) item_RecordStatus.getAttribute("data")).getSearchOperatorId();
	
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
		// Defualt Sort on the table
		so.addSort("HYCUS", false);

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
		this.collateralitemCtrl.setSearchObj(so);

		final Listbox listBox = this.collateralitemCtrl.listBoxCollateralitem;
		final Paging paging = this.collateralitemCtrl.pagingCollateralitemList;
		

		// set the model to the listbox with the initial resultset get by the DAO method.
		((PagedListWrapper<Collateralitem>) listBox.getModel()).init(so, listBox, paging);
		this.collateralitemCtrl.setSearchObj(so);

		this.label_CollateralitemSearchResult.setValue(Labels.getLabel("label_CollateralitemSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
		paging.setActivePage(0);
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setCollateralitemService(CollateralitemService collateralitemService) {
		this.collateralitemService = collateralitemService;
	}

	public CollateralitemService getCollateralitemService() {
		return this.collateralitemService;
	}
}