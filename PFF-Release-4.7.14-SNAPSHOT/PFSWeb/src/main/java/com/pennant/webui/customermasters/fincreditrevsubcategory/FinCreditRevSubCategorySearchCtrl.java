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
 * FileName    		:  FinCreditRevSubCategorySearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-11-2013    														*
 *                                                                  						*
 * Modified Date    :  13-11-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-11-2013       Pennant	                 0.1                                            * 
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
package com.pennant.webui.customermasters.fincreditrevsubcategory;

import java.util.ArrayList;
import java.util.List;

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
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevSubCategory;
import com.pennant.backend.service.customermasters.FinCreditRevSubCategoryService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

public class FinCreditRevSubCategorySearchCtrl extends GFCBaseCtrl<FinCreditRevSubCategory>  {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(FinCreditRevSubCategorySearchCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinCreditRevSubCategorySearch; 
	
	protected Textbox subCategoryCode; 
	protected Listbox sortOperator_subCategoryCode; 
  	protected Intbox subCategorySeque; 
  	protected Listbox sortOperator_subCategorySeque; 
	protected Textbox categoryId; 
	protected Listbox sortOperator_categoryId; 
	protected Textbox subCategoryDesc; 
	protected Listbox sortOperator_subCategoryDesc; 
	protected Textbox subCategoryItemType; 
	protected Listbox sortOperator_subCategoryItemType; 
	protected Textbox itemsToCal; 
	protected Listbox sortOperator_itemsToCal; 
	protected Textbox itemRule; 
	protected Listbox sortOperator_itemRule; 
	protected Checkbox isCreditCCY; 
	protected Listbox sortOperator_isCreditCCY; 
	protected Textbox mainSubCategoryCode; 
	protected Listbox sortOperator_mainSubCategoryCode; 
  	protected Intbox calcSeque; 
  	protected Listbox sortOperator_calcSeque; 
	protected Checkbox format; 
	protected Listbox sortOperator_format; 
	protected Checkbox percentCategory; 
	protected Listbox sortOperator_percentCategory; 
	protected Checkbox grand; 
	protected Listbox sortOperator_grand; 
	protected Textbox recordStatus; 
	protected Listbox recordType;	
	protected Listbox sortOperator_recordStatus; 
	protected Listbox sortOperator_recordType; 
	
	protected Label label_FinCreditRevSubCategorySearch_RecordStatus; 
	protected Label label_FinCreditRevSubCategorySearch_RecordType; 
	protected Label label_FinCreditRevSubCategorySearchResult; 

	// not auto wired vars
	private transient FinCreditRevSubCategoryListCtrl finCreditRevSubCategoryCtrl; // overhanded per param
	private transient FinCreditRevSubCategoryService finCreditRevSubCategoryService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("FinCreditRevSubCategory");
	private JdbcSearchObject<FinCreditRevSubCategory> searchObj;
	
	/**
	 * constructor
	 */
	public FinCreditRevSubCategorySearchCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onCreate$window_FinCreditRevSubCategorySearch(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinCreditRevSubCategorySearch);

		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	
		if (arguments.containsKey("finCreditRevSubCategoryCtrl")) {
			this.finCreditRevSubCategoryCtrl = (FinCreditRevSubCategoryListCtrl) arguments.get("finCreditRevSubCategoryCtrl");
		} else {
			this.finCreditRevSubCategoryCtrl = null;
		}

		// DropDown ListBox
	
		this.sortOperator_subCategoryCode.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_subCategoryCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_subCategorySeque.setModel(new ListModelList(new SearchOperators().getNumericOperators()));
		this.sortOperator_subCategorySeque.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_categoryId.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_categoryId.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_subCategoryDesc.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_subCategoryDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_subCategoryItemType.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_subCategoryItemType.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_itemsToCal.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_itemsToCal.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_itemRule.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_itemRule.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_isCreditCCY.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_isCreditCCY.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_mainSubCategoryCode.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_mainSubCategoryCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_calcSeque.setModel(new ListModelList(new SearchOperators().getNumericOperators()));
		this.sortOperator_calcSeque.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_format.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_format.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_percentCategory.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_percentCategory.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_grand.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_grand.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=setRecordType(this.recordType);	
		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_FinCreditRevSubCategorySearch_RecordStatus.setVisible(false);
			this.label_FinCreditRevSubCategorySearch_RecordType.setVisible(false);
		}
		
		// Restore the search mask input definition
		// if exists a searchObject than show formerly inputs of filter values
		if (arguments.containsKey("searchObject")) {
			searchObj = (JdbcSearchObject<FinCreditRevSubCategory>) arguments.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();
			final List<Filter> rmvFilter = new ArrayList<Filter>();
			
			for (final Filter filter : ft) {

			// restore founded properties
			rmvFilter.add(filter);
			    if ("subCategoryCode".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_subCategoryCode, filter);
					this.subCategoryCode.setValue(filter.getValue().toString());
					
			    } else if ("subCategorySeque".equals(filter.getProperty())) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_subCategorySeque, filter);
			    	this.subCategorySeque.setText(filter.getValue().toString());
					
			    } else if ("categoryId".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_categoryId, filter);
					this.categoryId.setValue(filter.getValue().toString());
					
			    } else if ("subCategoryDesc".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_subCategoryDesc, filter);
					this.subCategoryDesc.setValue(filter.getValue().toString());
					
			    } else if ("subCategoryItemType".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_subCategoryItemType, filter);
					this.subCategoryItemType.setValue(filter.getValue().toString());
					
			    } else if ("itemsToCal".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_itemsToCal, filter);
					this.itemsToCal.setValue(filter.getValue().toString());
					
			    } else if ("itemRule".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_itemRule, filter);
					this.itemRule.setValue(filter.getValue().toString());
					
			    } else if ("isCreditCCY".equals(filter.getProperty())) {
		    		SearchOperators.restoreNumericOperator(this.sortOperator_isCreditCCY, filter);
			    	if(Integer.parseInt(filter.getValue().toString()) == 1){
			    		this.isCreditCCY.setChecked(true);
					}else{
						this.isCreditCCY.setChecked(false);
					}
					
			    } else if ("mainSubCategoryCode".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_mainSubCategoryCode, filter);
					this.mainSubCategoryCode.setValue(filter.getValue().toString());
					
			    } else if ("calcSeque".equals(filter.getProperty())) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_calcSeque, filter);
			    	this.calcSeque.setText(filter.getValue().toString());
					
			    } else if ("format".equals(filter.getProperty())) {
		    		SearchOperators.restoreNumericOperator(this.sortOperator_format, filter);
			    	if(Integer.parseInt(filter.getValue().toString()) == 1){
			    		this.format.setChecked(true);
					}else{
						this.format.setChecked(false);
					}
					
			    } else if ("percentCategory".equals(filter.getProperty())) {
		    		SearchOperators.restoreNumericOperator(this.sortOperator_percentCategory, filter);
			    	if(Integer.parseInt(filter.getValue().toString()) == 1){
			    		this.percentCategory.setChecked(true);
					}else{
						this.percentCategory.setChecked(false);
					}
					
			    } else if ("grand".equals(filter.getProperty())) {
		    		SearchOperators.restoreNumericOperator(this.sortOperator_grand, filter);
			    	if(Integer.parseInt(filter.getValue().toString()) == 1){
			    		this.grand.setChecked(true);
					}else{
						this.grand.setChecked(false);
					}
					
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
			for(int i =0 ; i < rmvFilter.size() ; i++){
				searchObj.removeFilter(rmvFilter.get(i));
			}			
		}
		showFinCreditRevSubCategorySeekDialog();
	}

	// Components events

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
	private void showFinCreditRevSubCategorySeekDialog() throws InterruptedException {

		try {
			// open the dialog in modal mode
			this.window_FinCreditRevSubCategorySearch.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

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
		final JdbcSearchObject<FinCreditRevSubCategory> so = new JdbcSearchObject<FinCreditRevSubCategory>(FinCreditRevSubCategory.class);
		
		List<Filter> filters =this.searchObj.getFilters();
		 for (int i = 0; i < filters.size(); i++) {
		 Filter filter= filters.get(i);
		 so.addFilter  (new   Filter(filter.getProperty(),filter.getValue(),filter.getOperator()));
		}
		 
		 if(StringUtils.isNotBlank(this.searchObj.getWhereClause())){
			 so.addWhereClause(this.searchObj.getWhereClause());
			}

			 so.setSorts(this.searchObj.getSorts());
			 so.addTabelName(this.searchObj.getTabelName());
		 
		if (isWorkFlowEnabled()){
			so.addTabelName("FinCreditRevSubCategory_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("FinCreditRevSubCategory_AView");
		}
		
		
		if (StringUtils.isNotEmpty(this.subCategoryCode.getValue())) {

			// get the search operator
			final Listitem itemSubCategoryCode = this.sortOperator_subCategoryCode.getSelectedItem();
			if (itemSubCategoryCode != null) {
				final int searchOpId = ((SearchOperators) itemSubCategoryCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("subCategoryCode", "%" + this.subCategoryCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("subCategoryCode", this.subCategoryCode.getValue(), searchOpId));
				}
			}
		}
	  if (this.subCategorySeque.getValue()!=null) {	  
	    final Listitem itemSubCategorySeque = this.sortOperator_subCategorySeque.getSelectedItem();
	  	if (itemSubCategorySeque != null) {
	 		final int searchOpId = ((SearchOperators) itemSubCategorySeque.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.subCategorySeque.getValue()!=null){
	 				so.addFilter(new Filter("subCategorySeque",this.subCategorySeque.getValue(), searchOpId));
	 			}
	 		}
	 	}
	  }	
		if (StringUtils.isNotEmpty(this.categoryId.getValue())) {

			// get the search operator
			final Listitem itemCategoryId = this.sortOperator_categoryId.getSelectedItem();
			if (itemCategoryId != null) {
				final int searchOpId = ((SearchOperators) itemCategoryId.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("categoryId", "%" + this.categoryId.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("categoryId", this.categoryId.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.subCategoryDesc.getValue())) {

			// get the search operator
			final Listitem itemSubCategoryDesc = this.sortOperator_subCategoryDesc.getSelectedItem();
			if (itemSubCategoryDesc != null) {
				final int searchOpId = ((SearchOperators) itemSubCategoryDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("subCategoryDesc", "%" + this.subCategoryDesc.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("subCategoryDesc", this.subCategoryDesc.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.subCategoryItemType.getValue())) {

			// get the search operator
			final Listitem itemSubCategoryItemType = this.sortOperator_subCategoryItemType.getSelectedItem();
			if (itemSubCategoryItemType != null) {
				final int searchOpId = ((SearchOperators) itemSubCategoryItemType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("subCategoryItemType", "%" + this.subCategoryItemType.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("subCategoryItemType", this.subCategoryItemType.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.itemsToCal.getValue())) {

			// get the search operator
			final Listitem itemItemsToCal = this.sortOperator_itemsToCal.getSelectedItem();
			if (itemItemsToCal != null) {
				final int searchOpId = ((SearchOperators) itemItemsToCal.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("itemsToCal", "%" + this.itemsToCal.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("itemsToCal", this.itemsToCal.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.itemRule.getValue())) {

			// get the search operator
			final Listitem itemItemRule = this.sortOperator_itemRule.getSelectedItem();
			if (itemItemRule != null) {
				final int searchOpId = ((SearchOperators) itemItemRule.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("itemRule", "%" + this.itemRule.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("itemRule", this.itemRule.getValue(), searchOpId));
				}
			}
		}
		// get the search operatorxxx
		final Listitem itemIsCreditCCY = this.sortOperator_isCreditCCY.getSelectedItem();
		if (itemIsCreditCCY != null) {
			final int searchOpId = ((SearchOperators) itemIsCreditCCY.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.isCreditCCY.isChecked()){
					so.addFilter(new Filter("isCreditCCY",1, searchOpId));
				}else{
					so.addFilter(new Filter("isCreditCCY",0, searchOpId));	
				}
			}
		}
		if (StringUtils.isNotEmpty(this.mainSubCategoryCode.getValue())) {

			// get the search operator
			final Listitem itemMainSubCategoryCode = this.sortOperator_mainSubCategoryCode.getSelectedItem();
			if (itemMainSubCategoryCode != null) {
				final int searchOpId = ((SearchOperators) itemMainSubCategoryCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("mainSubCategoryCode", "%" + this.mainSubCategoryCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("mainSubCategoryCode", this.mainSubCategoryCode.getValue(), searchOpId));
				}
			}
		}
	  if (this.calcSeque.getValue()!=null) {	  
	    final Listitem itemCalcSeque = this.sortOperator_calcSeque.getSelectedItem();
	  	if (itemCalcSeque != null) {
	 		final int searchOpId = ((SearchOperators) itemCalcSeque.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.calcSeque.getValue()!=null){
	 				so.addFilter(new Filter("calcSeque",this.calcSeque.getValue(), searchOpId));
	 			}
	 		}
	 	}
	  }	
		// get the search operatorxxx
		final Listitem itemFormat = this.sortOperator_format.getSelectedItem();
		if (itemFormat != null) {
			final int searchOpId = ((SearchOperators) itemFormat.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.format.isChecked()){
					so.addFilter(new Filter("format",1, searchOpId));
				}else{
					so.addFilter(new Filter("format",0, searchOpId));	
				}
			}
		}
		// get the search operatorxxx
		final Listitem itemPercentCategory = this.sortOperator_percentCategory.getSelectedItem();
		if (itemPercentCategory != null) {
			final int searchOpId = ((SearchOperators) itemPercentCategory.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.percentCategory.isChecked()){
					so.addFilter(new Filter("percentCategory",1, searchOpId));
				}else{
					so.addFilter(new Filter("percentCategory",0, searchOpId));	
				}
			}
		}
		// get the search operatorxxx
		final Listitem itemGrand = this.sortOperator_grand.getSelectedItem();
		if (itemGrand != null) {
			final int searchOpId = ((SearchOperators) itemGrand.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.grand.isChecked()){
					so.addFilter(new Filter("grand",1, searchOpId));
				}else{
					so.addFilter(new Filter("grand",0, searchOpId));	
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
		so.addSort("SubCategoryCode", false);

		// store the searchObject for reReading
		this.finCreditRevSubCategoryCtrl.setSearchObj(so);

		final Listbox listBox = this.finCreditRevSubCategoryCtrl.listBoxFinCreditRevSubCategory;
		final Paging paging = this.finCreditRevSubCategoryCtrl.pagingFinCreditRevSubCategoryList;
		

		// set the model to the listbox with the initial resultset get by the DAO method.
		((PagedListWrapper<FinCreditRevSubCategory>) listBox.getModel()).init(so, listBox, paging);
		this.finCreditRevSubCategoryCtrl.setSearchObj(so);

		this.label_FinCreditRevSubCategorySearchResult.setValue(Labels.getLabel("label_FinCreditRevSubCategorySearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
		paging.setActivePage(0);
		logger.debug("Leaving");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinCreditRevSubCategoryService(FinCreditRevSubCategoryService finCreditRevSubCategoryService) {
		this.finCreditRevSubCategoryService = finCreditRevSubCategoryService;
	}

	public FinCreditRevSubCategoryService getFinCreditRevSubCategoryService() {
		return this.finCreditRevSubCategoryService;
	}
}