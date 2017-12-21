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
 * FileName    		:  ProvisionSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-05-2012    														*
 *                                                                  						*
 * Modified Date    :  31-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-05-2012       Pennant	                 0.1                                            * 
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
package com.pennant.webui.financemanagement.provision;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

public class ProvisionSearchCtrl extends GFCBaseCtrl<Provision>  {
	private static final long serialVersionUID = 1933806562160029723L;
	private static final Logger logger = Logger.getLogger(ProvisionSearchCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_ProvisionSearch; 			// autowired
	
	protected Textbox 		finReference; 						// autowired
	protected Listbox 		sortOperator_finReference; 			// autowired
	protected Textbox 		finBranch;	 						// autowired
	protected Listbox 		sortOperator_finBranch; 			// autowired
	protected Textbox 		finType; 							// autowired
	protected Listbox 		sortOperator_finType; 				// autowired
	protected Textbox 		custID; 							// autowired
	protected Listbox 		sortOperator_custID; 				// autowired
	protected Datebox 		provisionCalDate; 					// autowired
	protected Listbox 		sortOperator_provisionCalDate; 		// autowired
  	protected Decimalbox 	provisionedAmt; 					// autowired
  	protected Listbox 		sortOperator_provisionedAmt; 		// autowired
  	protected Decimalbox 	nonFormulaProv; 					// autowired
  	protected Listbox 		sortOperator_nonFormulaProv; 		// autowired
	protected Checkbox 		useNFProv; 							// autowired
	protected Listbox 		sortOperator_useNFProv; 			// autowired
	protected Checkbox 		autoReleaseNFP; 					// autowired
	protected Listbox 		sortOperator_autoReleaseNFP; 		// autowired
	
	protected Label label_ProvisionSearch_RecordStatus; 		// autowired
	protected Label label_ProvisionSearch_RecordType; 			// autowired
	protected Label label_ProvisionSearchResult; 				// autowired

	// not auto wired vars
	private transient ProvisionListCtrl provisionCtrl; // overhanded per param
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("Provision");
	private JdbcSearchObject<Provision> searchObj;
	
	/**
	 * constructor
	 */
	public ProvisionSearchCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected Provision object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_ProvisionSearch(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ProvisionSearch);

		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	

		if (arguments.containsKey("provisionCtrl")) {
			this.provisionCtrl = (ProvisionListCtrl) arguments.get("provisionCtrl");
		} else {
			this.provisionCtrl = null;
		}

		// DropDown ListBox
	
		this.sortOperator_finReference.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finReference.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finBranch.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finBranch.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finType.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custID.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_custID.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_provisionCalDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_provisionCalDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_provisionedAmt.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_provisionedAmt.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_nonFormulaProv.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_nonFormulaProv.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_useNFProv.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_useNFProv.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_autoReleaseNFP.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_autoReleaseNFP.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		// Restore the search mask input definition
		// if exists a searchObject than show formerly inputs of filter values
		if (arguments.containsKey("searchObject")) {
			searchObj = (JdbcSearchObject<Provision>) arguments.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();
			final List<Filter> rmvFilter = new ArrayList<Filter>();
			
			for (final Filter filter : ft) {

			// restore founded properties
			rmvFilter.add(filter);
			    if ("finReference".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_finReference, filter);
					this.finReference.setValue(filter.getValue().toString());
					
			    } else if ("finBranch".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_finBranch, filter);
					this.finBranch.setValue(filter.getValue().toString());
					
			    } else if ("finType".equals(filter.getProperty())) {
					SearchOperators.restoreStringOperator(this.sortOperator_finType, filter);
					this.finType.setValue(filter.getValue().toString());
					
			    } else if ("custID".equals(filter.getProperty())) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_custID, filter);
			    	this.custID.setText(filter.getValue().toString());
					
			    } else if ("provisionCalDate".equals(filter.getProperty())) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_provisionCalDate, filter);
			    	this.provisionCalDate.setText(filter.getValue().toString());
					
			    } else if ("provisionedAmt".equals(filter.getProperty())) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_provisionedAmt, filter);
			    	this.provisionedAmt.setText(filter.getValue().toString());
					
			    } else if ("nonFormulaProv".equals(filter.getProperty())) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_nonFormulaProv, filter);
			    	this.nonFormulaProv.setText(filter.getValue().toString());
					
			    } else if ("useNFProv".equals(filter.getProperty())) {
		    		SearchOperators.restoreNumericOperator(this.sortOperator_useNFProv, filter);
			    	if(Integer.parseInt(filter.getValue().toString()) == 1){
			    		this.useNFProv.setChecked(true);
					}else{
						this.useNFProv.setChecked(false);
					}
			    } else if ("autoReleaseNFP".equals(filter.getProperty())) {
		    		SearchOperators.restoreNumericOperator(this.sortOperator_autoReleaseNFP, filter);
			    	if(Integer.parseInt(filter.getValue().toString()) == 1){
			    		this.autoReleaseNFP.setChecked(true);
					}else{
						this.autoReleaseNFP.setChecked(false);
					}
			    }
			}
			for(int i =0 ; i < rmvFilter.size() ; i++){
				searchObj.removeFilter(rmvFilter.get(i));
			}			
		}
		showProvisionSeekDialog();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "search/filter" button is clicked.
	 * 
	 * @param event
	 */
	public void onClick$btnSearch(Event event) {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
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
	private void showProvisionSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_ProvisionSearch.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
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
		
		final JdbcSearchObject<Provision> so = new JdbcSearchObject<Provision>(Provision.class);
		
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
			so.addTabelName("FinProvisions_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("FinProvisions_AView");
		}
		
		
		if (StringUtils.isNotEmpty(this.finReference.getValue())) {

			// get the search operator
			final Listitem item_FinReference = this.sortOperator_finReference.getSelectedItem();

			if (item_FinReference != null) {
				final int searchOpId = ((SearchOperators) item_FinReference.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finReference", "%" + this.finReference.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finReference", this.finReference.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.finBranch.getValue())) {

			// get the search operator
			final Listitem item_FinBranch = this.sortOperator_finBranch.getSelectedItem();

			if (item_FinBranch != null) {
				final int searchOpId = ((SearchOperators) item_FinBranch.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finBranch", "%" + this.finBranch.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finBranch", this.finBranch.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.finType.getValue())) {

			// get the search operator
			final Listitem item_FinType = this.sortOperator_finType.getSelectedItem();

			if (item_FinType != null) {
				final int searchOpId = ((SearchOperators) item_FinType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finType", "%" + this.finType.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finType", this.finType.getValue(), searchOpId));
				}
			}
		}
	  if (this.custID.getValue()!=null) {	  
	    final Listitem item_CustID = this.sortOperator_custID.getSelectedItem();
	  	if (item_CustID != null) {
	 		final int searchOpId = ((SearchOperators) item_CustID.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == Filter.OP_LIKE) {
				so.addFilter(new Filter("lovDescCustCIF", "%" + this.custID.getValue().toUpperCase() + "%", searchOpId));
			} else if (searchOpId == -1) {
				// do nothing
			} else {
				so.addFilter(new Filter("lovDescCustCIF", this.custID.getValue(), searchOpId));
			}
	 	}
	  }	
	  if (this.provisionCalDate.getValue()!=null) {	  
	    final Listitem item_ProvisionCalDate = this.sortOperator_provisionCalDate.getSelectedItem();
	  	if (item_ProvisionCalDate != null) {
	 		final int searchOpId = ((SearchOperators) item_ProvisionCalDate.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.provisionCalDate.getValue()!=null){
	 				so.addFilter(new Filter("provisionCalDate",this.provisionCalDate.getValue(), searchOpId));
	 			}
	 		}
	 	}
	  }	
	  if (this.provisionedAmt.getValue()!=null) {	  
	    final Listitem item_ProvisionedAmt = this.sortOperator_provisionedAmt.getSelectedItem();
	  	if (item_ProvisionedAmt != null) {
	 		final int searchOpId = ((SearchOperators) item_ProvisionedAmt.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.provisionedAmt.getValue()!=null){
	 				so.addFilter(new Filter("provisionedAmt",this.provisionedAmt.getValue(), searchOpId));
	 			}
	 		}
	 	}
	  }	
	  if (this.nonFormulaProv.getValue()!=null) {	  
	    final Listitem item_NonFormulaProv = this.sortOperator_nonFormulaProv.getSelectedItem();
	  	if (item_NonFormulaProv != null) {
	 		final int searchOpId = ((SearchOperators) item_NonFormulaProv.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.nonFormulaProv.getValue()!=null){
	 				so.addFilter(new Filter("nonFormulaProv",this.nonFormulaProv.getValue(), searchOpId));
	 			}
	 		}
	 	}
	  }	
		// get the search operatorxxx
		final Listitem item_UseNFProv = this.sortOperator_useNFProv.getSelectedItem();

		if (item_UseNFProv != null) {
			final int searchOpId = ((SearchOperators) item_UseNFProv.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.useNFProv.isChecked()){
					so.addFilter(new Filter("useNFProv",1, searchOpId));
				}else{
					so.addFilter(new Filter("useNFProv",0, searchOpId));	
				}
			}
		}
		// get the search operatorxxx
		final Listitem item_AutoReleaseNFP = this.sortOperator_autoReleaseNFP.getSelectedItem();

		if (item_AutoReleaseNFP != null) {
			final int searchOpId = ((SearchOperators) item_AutoReleaseNFP.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.autoReleaseNFP.isChecked()){
					so.addFilter(new Filter("autoReleaseNFP",1, searchOpId));
				}else{
					so.addFilter(new Filter("autoReleaseNFP",0, searchOpId));	
				}
			}
		}

		// Defualt Sort on the table
		so.addSort("FinReference", false);

		// store the searchObject for reReading
		this.provisionCtrl.setSearchObj(so);

		final Listbox listBox = this.provisionCtrl.listBoxProvision;
		final Paging paging = this.provisionCtrl.pagingProvisionList;

		// set the model to the listbox with the initial resultset get by the DAO method.
		((PagedListWrapper<Provision>) listBox.getModel()).init(so, listBox, paging);
		this.provisionCtrl.setSearchObj(so);

		this.label_ProvisionSearchResult.setValue(Labels.getLabel("label_ProvisionSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
		paging.setActivePage(0);
		logger.debug("Leaving");
	}

}