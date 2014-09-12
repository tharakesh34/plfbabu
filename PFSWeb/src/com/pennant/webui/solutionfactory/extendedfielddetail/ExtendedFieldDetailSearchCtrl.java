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
 * FileName    		:  ExtendedFieldDetailSearchCtrl.java                                                   * 	  
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

package com.pennant.webui.solutionfactory.extendedfielddetail;

import java.io.Serializable;
import java.util.ArrayList;
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
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.model.staticparms.ExtendedFieldHeader;
import com.pennant.backend.service.solutionfactory.ExtendedFieldDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;


public class ExtendedFieldDetailSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(ExtendedFieldDetailSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_ExtendedFieldDetailSearch; // autowired
	
	protected Textbox moduleId; // autowired
	protected Listbox sortOperator_moduleId; // autowired
	protected Textbox fieldName; // autowired
	protected Listbox sortOperator_fieldName; // autowired
	protected Textbox fieldType; // autowired
	protected Listbox sortOperator_fieldType; // autowired
  	protected Intbox fieldLength; // autowired
  	protected Listbox sortOperator_fieldLength; // autowired
  	protected Intbox fieldPrec; // autowired
  	protected Listbox sortOperator_fieldPrec; // autowired
	protected Textbox fieldLabel; // autowired
	protected Listbox sortOperator_fieldLabel; // autowired
	protected Checkbox fieldMandatory; // autowired
	protected Listbox sortOperator_fieldMandatory; // autowired
	protected Textbox fieldConstraint; // autowired
	protected Listbox sortOperator_fieldConstraint; // autowired
  	protected Intbox fieldSeqOrder; // autowired
  	protected Listbox sortOperator_fieldSeqOrder; // autowired
  	protected Intbox fieldColumn; // autowired
  	protected Listbox sortOperator_fieldColumn; // autowired
	protected Textbox fieldList; // autowired
	protected Listbox sortOperator_fieldList; // autowired
	protected Textbox fieldDefaultValue; // autowired
	protected Listbox sortOperator_fieldDefaultValue; // autowired
	protected Longbox fieldMinValue; // autowired
	protected Listbox sortOperator_fieldMinValue; // autowired
	protected Longbox fieldMaxValue; // autowired
	protected Listbox sortOperator_fieldMaxValue; // autowired
	protected Checkbox fieldUnique; // autowired
	protected Listbox sortOperator_fieldUnique; // autowired
	protected Textbox fieldExternalScript; // autowired
	protected Listbox sortOperator_fieldExternalScript; // autowired
	protected Textbox recordStatus; // autowired
	protected Listbox recordType;	// autowired
	protected Listbox sortOperator_recordStatus; // autowired
	protected Listbox sortOperator_recordType; // autowired
	
	protected Label label_ExtendedFieldDetailSearch_RecordStatus; // autowired
	protected Label label_ExtendedFieldDetailSearch_RecordType; // autowired
	protected Label label_ExtendedFieldDetailSearchResult; // autowired

	// not auto wired vars
	private transient ExtendedFieldDetailListCtrl extendedFieldDetailCtrl; // overhanded per param
	private transient ExtendedFieldDetailService extendedFieldDetailService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("ExtendedFieldDetail");
	private JdbcSearchObject<ExtendedFieldDetail> searchObj; 
	
	/**
	 * constructor
	 */
	public ExtendedFieldDetailSearchCtrl() {
		super();
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_ExtendedFieldDetailSearch(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		
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

		if (args.containsKey("extendedFieldDetailCtrl")) {
			this.extendedFieldDetailCtrl = (ExtendedFieldDetailListCtrl) args.get("extendedFieldDetailCtrl");
		} else {
			this.extendedFieldDetailCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_moduleId.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_moduleId.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fieldName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fieldName.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fieldType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fieldType.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fieldLength.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_fieldLength.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fieldPrec.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_fieldPrec.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fieldLabel.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fieldLabel.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fieldMandatory.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_fieldMandatory.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fieldConstraint.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fieldConstraint.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fieldSeqOrder.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_fieldSeqOrder.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fieldColumn.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_fieldColumn.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fieldList.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fieldList.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fieldDefaultValue.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fieldDefaultValue.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fieldMinValue.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_fieldMinValue.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fieldMaxValue.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_fieldMaxValue.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fieldUnique.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_fieldUnique.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fieldExternalScript.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fieldExternalScript.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
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
			this.label_ExtendedFieldDetailSearch_RecordStatus.setVisible(false);
			this.label_ExtendedFieldDetailSearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			
			searchObj = (JdbcSearchObject<ExtendedFieldDetail>) args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();
			final List <Filter> rmvFilter = new ArrayList<Filter>();

			for (final Filter filter : ft) {

				rmvFilter.add(filter);
				// restore founded properties
			    if (filter.getProperty().equals("moduleId")) {
					SearchOperators.restoreStringOperator(this.sortOperator_moduleId, filter);
					this.moduleId.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fieldName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fieldName, filter);
					this.fieldName.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fieldType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fieldType, filter);
					this.fieldType.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fieldLength")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_fieldLength, filter);
			    	this.fieldLength.setText(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fieldPrec")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_fieldPrec, filter);
			    	this.fieldPrec.setText(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fieldLabel")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fieldLabel, filter);
					this.fieldLabel.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fieldMandatory")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fieldMandatory, filter);
					this.fieldMandatory.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fieldConstraint")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fieldConstraint, filter);
					this.fieldConstraint.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fieldSeqOrder")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_fieldSeqOrder, filter);
			    	this.fieldSeqOrder.setText(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fieldColumn")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_fieldColumn, filter);
			    	this.fieldColumn.setText(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fieldList")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fieldList, filter);
					this.fieldList.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fieldDefaultValue")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fieldDefaultValue, filter);
					this.fieldDefaultValue.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fieldMinValue")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_fieldMinValue, filter);
			    	this.fieldMinValue.setText(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fieldMaxValue")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_fieldMaxValue, filter);
			    	this.fieldMaxValue.setText(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fieldUnique")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fieldUnique, filter);
					this.fieldUnique.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fieldExternalScript")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fieldExternalScript, filter);
					this.fieldExternalScript.setValue(filter.getValue().toString());

					
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
		showExtendedFieldDetailSeekDialog();
		} catch (Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
			e.printStackTrace();
			window_ExtendedFieldDetailSearch.onClose();
		}
	
		logger.debug("Leaving" + event.toString());
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
		this.window_ExtendedFieldDetailSearch.onClose();
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showExtendedFieldDetailSeekDialog() throws InterruptedException {

		try {
			// open the dialog in modal mode
			this.window_ExtendedFieldDetailSearch.doModal();
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
	
	@SuppressWarnings("unused")
    public void doSearch() {
		logger.debug("Entering");
		
		final JdbcSearchObject<ExtendedFieldHeader> so = new JdbcSearchObject<ExtendedFieldHeader>(ExtendedFieldHeader.class);
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
			so.addTabelName("ExtendedFieldDetail_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("ExtendedFieldDetail_AView");
		}
		
		
		if (StringUtils.isNotEmpty(this.moduleId.getValue())) {

			// get the search operator
			final Listitem item_ModuleId = this.sortOperator_moduleId.getSelectedItem();

			if (item_ModuleId != null) {
				final int searchOpId = ((SearchOperators) item_ModuleId.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("moduleId", "%" + this.moduleId.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("moduleId", this.moduleId.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.fieldName.getValue())) {

			// get the search operator
			final Listitem itemFieldName = this.sortOperator_fieldName.getSelectedItem();

			if (itemFieldName != null) {
				final int searchOpId = ((SearchOperators) itemFieldName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fieldName", "%" + this.fieldName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fieldName", this.fieldName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.fieldType.getValue())) {

			// get the search operator
			final Listitem item_FieldType = this.sortOperator_fieldType.getSelectedItem();

			if (item_FieldType != null) {
				final int searchOpId = ((SearchOperators) item_FieldType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fieldType", "%" + this.fieldType.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fieldType", this.fieldType.getValue(), searchOpId));
				}
			}
		}
	  if (this.fieldLength.getValue()!=null) {	  
	    final Listitem itemFieldLength = this.sortOperator_fieldLength.getSelectedItem();
	  	if (itemFieldLength != null) {
	 		final int searchOpId = ((SearchOperators) itemFieldLength.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.fieldLength.getValue()!=null){
	 				so.addFilter(new Filter("fieldLength",1, searchOpId));
	 			}else{
	 				so.addFilter(new Filter("fieldLength",0, searchOpId));	
	 			}
	 		}
	 	}
	  }	
	  if (this.fieldPrec.getValue()!=null) {	  
	    final Listitem item_FieldPrec = this.sortOperator_fieldPrec.getSelectedItem();
	  	if (item_FieldPrec != null) {
	 		final int searchOpId = ((SearchOperators) item_FieldPrec.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.fieldPrec.getValue()!=null){
	 				so.addFilter(new Filter("fieldPrec",1, searchOpId));
	 			}else{
	 				so.addFilter(new Filter("fieldPrec",0, searchOpId));	
	 			}
	 		}
	 	}
	  }	
		if (StringUtils.isNotEmpty(this.fieldLabel.getValue())) {

			// get the search operator
			final Listitem item_FieldLabel = this.sortOperator_fieldLabel.getSelectedItem();

			if (item_FieldLabel != null) {
				final int searchOpId = ((SearchOperators) item_FieldLabel.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fieldLabel", "%" + this.fieldLabel.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fieldLabel", this.fieldLabel.getValue(), searchOpId));
				}
			}
		}
		// get the search operatorxxx
		final Listitem item_FieldMandatory = this.sortOperator_fieldMandatory.getSelectedItem();

		if (item_FieldMandatory != null) {
			final int searchOpId = ((SearchOperators) item_FieldMandatory.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.fieldMandatory.isChecked()){
					so.addFilter(new Filter("fieldMandatory",1, searchOpId));
				}else{
					so.addFilter(new Filter("fieldMandatory",0, searchOpId));	
				}
			}
		}
		if (StringUtils.isNotEmpty(this.fieldConstraint.getValue())) {

			// get the search operator
			final Listitem itemFieldConstraint = this.sortOperator_fieldConstraint.getSelectedItem();

			if (itemFieldConstraint != null) {
				final int searchOpId = ((SearchOperators) itemFieldConstraint.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fieldConstraint", "%" + this.fieldConstraint.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fieldConstraint", this.fieldConstraint.getValue(), searchOpId));
				}
			}
		}
	  if (this.fieldSeqOrder.getValue()!=null) {	  
	    final Listitem itemFieldSeqOrder = this.sortOperator_fieldSeqOrder.getSelectedItem();
	  	if (itemFieldSeqOrder != null) {
	 		final int searchOpId = ((SearchOperators) itemFieldSeqOrder.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.fieldSeqOrder.getValue()!=null){
	 				so.addFilter(new Filter("fieldSeqOrder",1, searchOpId));
	 			}else{
	 				so.addFilter(new Filter("fieldSeqOrder",0, searchOpId));	
	 			}
	 		}
	 	}
	  }	
	  if (this.fieldColumn.getValue()!=null) {	  
	    final Listitem itemFieldColumn = this.sortOperator_fieldColumn.getSelectedItem();
	  	if (itemFieldColumn != null) {
	 		final int searchOpId = ((SearchOperators) itemFieldColumn.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.fieldColumn.getValue()!=null){
	 				so.addFilter(new Filter("fieldColumn",1, searchOpId));
	 			}else{
	 				so.addFilter(new Filter("fieldColumn",0, searchOpId));	
	 			}
	 		}
	 	}
	  }	
		if (StringUtils.isNotEmpty(this.fieldList.getValue())) {

			// get the search operator
			final Listitem itemFieldList = this.sortOperator_fieldList.getSelectedItem();

			if (itemFieldList != null) {
				final int searchOpId = ((SearchOperators) itemFieldList.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fieldList", "%" + this.fieldList.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fieldList", this.fieldList.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.fieldDefaultValue.getValue())) {

			// get the search operator
			final Listitem itemFieldDefaultValue = this.sortOperator_fieldDefaultValue.getSelectedItem();

			if (itemFieldDefaultValue != null) {
				final int searchOpId = ((SearchOperators) itemFieldDefaultValue.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fieldDefaultValue", "%" + this.fieldDefaultValue.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fieldDefaultValue", this.fieldDefaultValue.getValue(), searchOpId));
				}
			}
		}
	  if (this.fieldMinValue.getValue()!=null) {	  
	    final Listitem item_FieldMinValue = this.sortOperator_fieldMinValue.getSelectedItem();
	  	if (item_FieldMinValue != null) {
	 		final int searchOpId = ((SearchOperators) item_FieldMinValue.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.fieldMinValue.getValue()!=null){
	 				so.addFilter(new Filter("fieldMinValue",1, searchOpId));
	 			}else{
	 				so.addFilter(new Filter("fieldMinValue",0, searchOpId));	
	 			}
	 		}
	 	}
	  }	
	  if (this.fieldMaxValue.getValue()!=null) {	  
	    final Listitem itemFieldMaxValue = this.sortOperator_fieldMaxValue.getSelectedItem();
	  	if (itemFieldMaxValue != null) {
	 		final int searchOpId = ((SearchOperators) itemFieldMaxValue.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.fieldMaxValue.getValue()!=null){
	 				so.addFilter(new Filter("fieldMaxValue",1, searchOpId));
	 			}else{
	 				so.addFilter(new Filter("fieldMaxValue",0, searchOpId));	
	 			}
	 		}
	 	}
	  }	
		// get the search operatorxxx
		final Listitem item_FieldUnique = this.sortOperator_fieldUnique.getSelectedItem();

		if (item_FieldUnique != null) {
			final int searchOpId = ((SearchOperators) item_FieldUnique.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.fieldUnique.isChecked()){
					so.addFilter(new Filter("fieldUnique",1, searchOpId));
				}else{
					so.addFilter(new Filter("fieldUnique",0, searchOpId));	
				}
			}
		}
		if (StringUtils.isNotEmpty(this.fieldExternalScript.getValue())) {

			// get the search operator
			final Listitem itemFieldExternalScript = this.sortOperator_fieldExternalScript.getSelectedItem();

			if (itemFieldExternalScript != null) {
				final int searchOpId = ((SearchOperators) itemFieldExternalScript.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fieldExternalScript", "%" + this.fieldExternalScript.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fieldExternalScript", this.fieldExternalScript.getValue(), searchOpId));
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
		this.extendedFieldDetailCtrl.setSearchObj(so);

		final Listbox listBox = this.extendedFieldDetailCtrl.listBoxExtendedFieldDetail;
		final Paging paging = this.extendedFieldDetailCtrl.pagingExtendedFieldDetailList;
		

		// set the model to the listbox with the initial resultset get by the DAO method.
		/*((PagedListWrapper<ExtendedFieldDetail>) listBox.getModel()).init(so, listBox, paging);
		this.extendedFieldDetailCtrl.setSearchObj(so);*/

		this.label_ExtendedFieldDetailSearchResult.setValue(Labels.getLabel("label_ExtendedFieldDetailSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
		paging.setActivePage(0);
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setExtendedFieldDetailService(ExtendedFieldDetailService extendedFieldDetailService) {
		this.extendedFieldDetailService = extendedFieldDetailService;
	}

	public ExtendedFieldDetailService getExtendedFieldDetailService() {
		return this.extendedFieldDetailService;
	}
}