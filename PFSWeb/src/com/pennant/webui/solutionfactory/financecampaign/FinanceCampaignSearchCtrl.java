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
 * FileName    		:  FinanceCampaignSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-12-2011    														*
 *                                                                  						*
 * Modified Date    :  30-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.solutionfactory.financecampaign;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.solutionfactory.FinanceCampaign;
import com.pennant.backend.service.solutionfactory.FinanceCampaignService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;


public class FinanceCampaignSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(FinanceCampaignSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_FinanceCampaignSearch; // autowired
	
	protected Textbox fCCode; // autowired
	protected Listbox sortOperator_fCCode; // autowired
	protected Textbox fCDesc; // autowired
	protected Listbox sortOperator_fCDesc; // autowired
	protected Textbox fCFinType; // autowired
	protected Listbox sortOperator_fCFinType; // autowired
	protected Checkbox fCIsAlwMD; // autowired
	protected Listbox sortOperator_fCIsAlwMD; // autowired
	protected Checkbox fCIsAlwGrace; // autowired
	protected Listbox sortOperator_fCIsAlwGrace; // autowired
	protected Checkbox fCOrgPrfUnchanged; // autowired
	protected Listbox sortOperator_fCOrgPrfUnchanged; // autowired
	protected Textbox fCRateType; // autowired
	protected Listbox sortOperator_fCRateType; // autowired
	protected Textbox fCBaseRate; // autowired
	protected Listbox sortOperator_fCBaseRate; // autowired
	protected Textbox fCSplRate; // autowired
	protected Listbox sortOperator_fCSplRate; // autowired
  	protected Decimalbox fCIntRate; // autowired
  	protected Listbox sortOperator_fCIntRate; // autowired
	protected Textbox fCDftIntFrq; // autowired
	protected Listbox sortOperator_fCDftIntFrq; // autowired
	protected Checkbox fCIsIntCpz; // autowired
	protected Listbox sortOperator_fCIsIntCpz; // autowired
	protected Textbox fCCpzFrq; // autowired
	protected Listbox sortOperator_fCCpzFrq; // autowired
	protected Checkbox fCIsRvwAlw; // autowired
	protected Listbox sortOperator_fCIsRvwAlw; // autowired
	protected Textbox fCRvwFrq; // autowired
	protected Listbox sortOperator_fCRvwFrq; // autowired
	protected Textbox fCGrcRateType; // autowired
	protected Listbox sortOperator_fCGrcRateType; // autowired
	protected Textbox fCGrcBaseRate; // autowired
	protected Listbox sortOperator_fCGrcBaseRate; // autowired
	protected Textbox fCGrcSplRate; // autowired
	protected Listbox sortOperator_fCGrcSplRate; // autowired
  	protected Decimalbox fCGrcIntRate; // autowired
  	protected Listbox sortOperator_fCGrcIntRate; // autowired
	protected Textbox fCGrcDftIntFrq; // autowired
	protected Listbox sortOperator_fCGrcDftIntFrq; // autowired
	protected Checkbox fCGrcIsIntCpz; // autowired
	protected Listbox sortOperator_fCGrcIsIntCpz; // autowired
	protected Textbox fCGrcCpzFrq; // autowired
	protected Listbox sortOperator_fCGrcCpzFrq; // autowired
	protected Checkbox fCGrcIsRvwAlw; // autowired
	protected Listbox sortOperator_fCGrcIsRvwAlw; // autowired
	protected Textbox fCGrcRvwFrq; // autowired
	protected Listbox sortOperator_fCGrcRvwFrq; // autowired
  	protected Decimalbox fCMinTerm; // autowired
  	protected Listbox sortOperator_fCMinTerm; // autowired
  	protected Decimalbox fCMaxTerm; // autowired
  	protected Listbox sortOperator_fCMaxTerm; // autowired
  	protected Decimalbox fCDftTerms; // autowired
  	protected Listbox sortOperator_fCDftTerms; // autowired
	protected Textbox fCRpyFrq; // autowired
	protected Listbox sortOperator_fCRpyFrq; // autowired
	protected Textbox fCRepayMethod; // autowired
	protected Listbox sortOperator_fCRepayMethod; // autowired
	protected Checkbox fCIsAlwPartialRpy; // autowired
	protected Listbox sortOperator_fCIsAlwPartialRpy; // autowired
	protected Checkbox fCIsAlwDifferment; // autowired
	protected Listbox sortOperator_fCIsAlwDifferment; // autowired
  	protected Decimalbox fCMaxDifferment; // autowired
  	protected Listbox sortOperator_fCMaxDifferment; // autowired
	protected Checkbox fCIsAlwFrqDifferment; // autowired
	protected Listbox sortOperator_fCIsAlwFrqDifferment; // autowired
  	protected Decimalbox fCMaxFrqDifferment; // autowired
  	protected Listbox sortOperator_fCMaxFrqDifferment; // autowired
	protected Checkbox fCIsAlwEarlyRpy; // autowired
	protected Listbox sortOperator_fCIsAlwEarlyRpy; // autowired
	protected Checkbox fCIsAlwEarlySettle; // autowired
	protected Listbox sortOperator_fCIsAlwEarlySettle; // autowired
	protected Checkbox fCIsDwPayRequired; // autowired
	protected Listbox sortOperator_fCIsDwPayRequired; // autowired
	protected Textbox fCRvwRateApplFor; // autowired
	protected Listbox sortOperator_fCRvwRateApplFor; // autowired
	protected Checkbox fCAlwRateChangeAnyDate; // autowired
	protected Listbox sortOperator_fCAlwRateChangeAnyDate; // autowired
	protected Textbox fCGrcRvwRateApplFor; // autowired
	protected Listbox sortOperator_fCGrcRvwRateApplFor; // autowired
	protected Checkbox fCIsIntCpzAtGrcEnd; // autowired
	protected Listbox sortOperator_fCIsIntCpzAtGrcEnd; // autowired
	protected Checkbox fCGrcAlwRateChgAnyDate; // autowired
	protected Listbox sortOperator_fCGrcAlwRateChgAnyDate; // autowired
  	protected Decimalbox fCMinDownPayAmount; // autowired
  	protected Listbox sortOperator_fCMinDownPayAmount; // autowired
	protected Textbox fCSchCalCodeOnRvw; // autowired
	protected Listbox sortOperator_fCSchCalCodeOnRvw; // autowired
	protected Textbox recordStatus; // autowired
	protected Listbox recordType;	// autowired
	protected Listbox sortOperator_recordStatus; // autowired
	protected Listbox sortOperator_recordType; // autowired
	
	protected Label label_FinanceCampaignSearch_RecordStatus; // autowired
	protected Label label_FinanceCampaignSearch_RecordType; // autowired
	protected Label label_FinanceCampaignSearchResult; // autowired

	// not auto wired vars
	private transient FinanceCampaignListCtrl financeCampaignCtrl; // overhanded per param
	private transient FinanceCampaignService financeCampaignService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("FinanceCampaign");
	
	/**
	 * constructor
	 */
	public FinanceCampaignSearchCtrl() {
		super();
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_FinanceCampaignSearch(Event event) throws Exception {

		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("financeCampaignCtrl")) {
			this.financeCampaignCtrl = (FinanceCampaignListCtrl) args.get("financeCampaignCtrl");
		} else {
			this.financeCampaignCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_fCCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fCCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fCDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCFinType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fCFinType.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCIsAlwMD.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_fCIsAlwMD.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCIsAlwGrace.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_fCIsAlwGrace.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCOrgPrfUnchanged.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_fCOrgPrfUnchanged.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCRateType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fCRateType.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCBaseRate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fCBaseRate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCSplRate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fCSplRate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCIntRate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_fCIntRate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCDftIntFrq.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fCDftIntFrq.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCIsIntCpz.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_fCIsIntCpz.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCCpzFrq.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fCCpzFrq.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCIsRvwAlw.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_fCIsRvwAlw.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCRvwFrq.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fCRvwFrq.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCGrcRateType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fCGrcRateType.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCGrcBaseRate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fCGrcBaseRate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCGrcSplRate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fCGrcSplRate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCGrcIntRate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_fCGrcIntRate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCGrcDftIntFrq.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fCGrcDftIntFrq.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCGrcIsIntCpz.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_fCGrcIsIntCpz.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCGrcCpzFrq.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fCGrcCpzFrq.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCGrcIsRvwAlw.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_fCGrcIsRvwAlw.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCGrcRvwFrq.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fCGrcRvwFrq.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCMinTerm.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_fCMinTerm.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCMaxTerm.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_fCMaxTerm.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCDftTerms.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_fCDftTerms.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCRpyFrq.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fCRpyFrq.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCRepayMethod.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fCRepayMethod.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCIsAlwPartialRpy.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_fCIsAlwPartialRpy.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCIsAlwDifferment.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_fCIsAlwDifferment.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCMaxDifferment.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_fCMaxDifferment.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCIsAlwFrqDifferment.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_fCIsAlwFrqDifferment.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCMaxFrqDifferment.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_fCMaxFrqDifferment.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCIsAlwEarlyRpy.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_fCIsAlwEarlyRpy.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCIsAlwEarlySettle.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_fCIsAlwEarlySettle.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCIsDwPayRequired.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_fCIsDwPayRequired.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCRvwRateApplFor.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fCRvwRateApplFor.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCAlwRateChangeAnyDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_fCAlwRateChangeAnyDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCGrcRvwRateApplFor.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fCGrcRvwRateApplFor.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCIsIntCpzAtGrcEnd.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_fCIsIntCpzAtGrcEnd.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCGrcAlwRateChgAnyDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_fCGrcAlwRateChgAnyDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCMinDownPayAmount.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_fCMinDownPayAmount.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fCSchCalCodeOnRvw.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fCSchCalCodeOnRvw.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
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
			this.label_FinanceCampaignSearch_RecordStatus.setVisible(false);
			this.label_FinanceCampaignSearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<FinanceCampaign> searchObj = (JdbcSearchObject<FinanceCampaign>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
			    if (filter.getProperty().equals("fCCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCCode, filter);
					this.fCCode.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCDesc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCDesc, filter);
					this.fCDesc.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCFinType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCFinType, filter);
					this.fCFinType.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCIsAlwMD")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCIsAlwMD, filter);
					this.fCIsAlwMD.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCIsAlwGrace")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCIsAlwGrace, filter);
					this.fCIsAlwGrace.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCOrgPrfUnchanged")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCOrgPrfUnchanged, filter);
					this.fCOrgPrfUnchanged.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCRateType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCRateType, filter);
					this.fCRateType.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCBaseRate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCBaseRate, filter);
					this.fCBaseRate.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCSplRate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCSplRate, filter);
					this.fCSplRate.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCIntRate")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_fCIntRate, filter);
			    	this.fCIntRate.setText(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCDftIntFrq")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCDftIntFrq, filter);
					this.fCDftIntFrq.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCIsIntCpz")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCIsIntCpz, filter);
					this.fCIsIntCpz.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCCpzFrq")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCCpzFrq, filter);
					this.fCCpzFrq.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCIsRvwAlw")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCIsRvwAlw, filter);
					this.fCIsRvwAlw.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCRvwFrq")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCRvwFrq, filter);
					this.fCRvwFrq.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCGrcRateType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCGrcRateType, filter);
					this.fCGrcRateType.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCGrcBaseRate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCGrcBaseRate, filter);
					this.fCGrcBaseRate.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCGrcSplRate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCGrcSplRate, filter);
					this.fCGrcSplRate.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCGrcIntRate")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_fCGrcIntRate, filter);
			    	this.fCGrcIntRate.setText(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCGrcDftIntFrq")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCGrcDftIntFrq, filter);
					this.fCGrcDftIntFrq.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCGrcIsIntCpz")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCGrcIsIntCpz, filter);
					this.fCGrcIsIntCpz.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCGrcCpzFrq")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCGrcCpzFrq, filter);
					this.fCGrcCpzFrq.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCGrcIsRvwAlw")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCGrcIsRvwAlw, filter);
					this.fCGrcIsRvwAlw.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCGrcRvwFrq")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCGrcRvwFrq, filter);
					this.fCGrcRvwFrq.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCMinTerm")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_fCMinTerm, filter);
			    	this.fCMinTerm.setText(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCMaxTerm")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_fCMaxTerm, filter);
			    	this.fCMaxTerm.setText(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCDftTerms")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_fCDftTerms, filter);
			    	this.fCDftTerms.setText(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCRpyFrq")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCRpyFrq, filter);
					this.fCRpyFrq.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCRepayMethod")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCRepayMethod, filter);
					this.fCRepayMethod.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCIsAlwPartialRpy")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCIsAlwPartialRpy, filter);
					this.fCIsAlwPartialRpy.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCIsAlwDifferment")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCIsAlwDifferment, filter);
					this.fCIsAlwDifferment.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCMaxDifferment")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_fCMaxDifferment, filter);
			    	this.fCMaxDifferment.setText(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCIsAlwFrqDifferment")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCIsAlwFrqDifferment, filter);
					this.fCIsAlwFrqDifferment.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCMaxFrqDifferment")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_fCMaxFrqDifferment, filter);
			    	this.fCMaxFrqDifferment.setText(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCIsAlwEarlyRpy")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCIsAlwEarlyRpy, filter);
					this.fCIsAlwEarlyRpy.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCIsAlwEarlySettle")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCIsAlwEarlySettle, filter);
					this.fCIsAlwEarlySettle.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCIsDwPayRequired")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCIsDwPayRequired, filter);
					this.fCIsDwPayRequired.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCRvwRateApplFor")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCRvwRateApplFor, filter);
					this.fCRvwRateApplFor.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCAlwRateChangeAnyDate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCAlwRateChangeAnyDate, filter);
					this.fCAlwRateChangeAnyDate.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCGrcRvwRateApplFor")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCGrcRvwRateApplFor, filter);
					this.fCGrcRvwRateApplFor.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCIsIntCpzAtGrcEnd")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCIsIntCpzAtGrcEnd, filter);
					this.fCIsIntCpzAtGrcEnd.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCGrcAlwRateChgAnyDate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCGrcAlwRateChgAnyDate, filter);
					this.fCGrcAlwRateChgAnyDate.setValue(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCMinDownPayAmount")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_fCMinDownPayAmount, filter);
			    	this.fCMinDownPayAmount.setText(filter.getValue().toString());

					
			    } else if (filter.getProperty().equals("fCSchCalCodeOnRvw")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fCSchCalCodeOnRvw, filter);
					this.fCSchCalCodeOnRvw.setValue(filter.getValue().toString());

					
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
		showFinanceCampaignSeekDialog();
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
		this.window_FinanceCampaignSearch.onClose();
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showFinanceCampaignSeekDialog() throws InterruptedException {

		try {
			// open the dialog in modal mode
			this.window_FinanceCampaignSearch.doModal();
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

		final JdbcSearchObject<FinanceCampaign> so = new JdbcSearchObject<FinanceCampaign>(FinanceCampaign.class);

		if (isWorkFlowEnabled()){
			so.addTabelName("RMTFinCampaign_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("RMTFinCampaign_AView");
		}
		
		
		if (StringUtils.isNotEmpty(this.fCCode.getValue())) {

			// get the search operator
			final Listitem itemFCCode = this.sortOperator_fCCode.getSelectedItem();

			if (itemFCCode != null) {
				final int searchOpId = ((SearchOperators) itemFCCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fCCode", "%" + this.fCCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fCCode", this.fCCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.fCDesc.getValue())) {

			// get the search operator
			final Listitem itemFCDesc = this.sortOperator_fCDesc.getSelectedItem();

			if (itemFCDesc != null) {
				final int searchOpId = ((SearchOperators) itemFCDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fCDesc", "%" + this.fCDesc.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fCDesc", this.fCDesc.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.fCFinType.getValue())) {

			// get the search operator
			final Listitem itemFCFinType = this.sortOperator_fCFinType.getSelectedItem();

			if (itemFCFinType != null) {
				final int searchOpId = ((SearchOperators) itemFCFinType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fCFinType", "%" + this.fCFinType.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fCFinType", this.fCFinType.getValue(), searchOpId));
				}
			}
		}
		// get the search operatorxxx
		final Listitem itemFCIsAlwMD = this.sortOperator_fCIsAlwMD.getSelectedItem();

		if (itemFCIsAlwMD != null) {
			final int searchOpId = ((SearchOperators) itemFCIsAlwMD.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.fCIsAlwMD.isChecked()){
					so.addFilter(new Filter("fCIsAlwMD",1, searchOpId));
				}else{
					so.addFilter(new Filter("fCIsAlwMD",0, searchOpId));	
				}
			}
		}
		// get the search operatorxxx
		final Listitem itemFCIsAlwGrace = this.sortOperator_fCIsAlwGrace.getSelectedItem();

		if (itemFCIsAlwGrace != null) {
			final int searchOpId = ((SearchOperators) itemFCIsAlwGrace.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.fCIsAlwGrace.isChecked()){
					so.addFilter(new Filter("fCIsAlwGrace",1, searchOpId));
				}else{
					so.addFilter(new Filter("fCIsAlwGrace",0, searchOpId));	
				}
			}
		}
		// get the search operatorxxx
		final Listitem itemFCOrgPrfUnchanged = this.sortOperator_fCOrgPrfUnchanged.getSelectedItem();

		if (itemFCOrgPrfUnchanged != null) {
			final int searchOpId = ((SearchOperators) itemFCOrgPrfUnchanged.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.fCOrgPrfUnchanged.isChecked()){
					so.addFilter(new Filter("fCOrgPrfUnchanged",1, searchOpId));
				}else{
					so.addFilter(new Filter("fCOrgPrfUnchanged",0, searchOpId));	
				}
			}
		}
		if (StringUtils.isNotEmpty(this.fCRateType.getValue())) {

			// get the search operator
			final Listitem itemFCRateType = this.sortOperator_fCRateType.getSelectedItem();

			if (itemFCRateType != null) {
				final int searchOpId = ((SearchOperators) itemFCRateType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fCRateType", "%" + this.fCRateType.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fCRateType", this.fCRateType.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.fCBaseRate.getValue())) {

			// get the search operator
			final Listitem itemFCBaseRate = this.sortOperator_fCBaseRate.getSelectedItem();

			if (itemFCBaseRate != null) {
				final int searchOpId = ((SearchOperators) itemFCBaseRate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fCBaseRate", "%" + this.fCBaseRate.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fCBaseRate", this.fCBaseRate.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.fCSplRate.getValue())) {

			// get the search operator
			final Listitem itemFCSplRate = this.sortOperator_fCSplRate.getSelectedItem();

			if (itemFCSplRate != null) {
				final int searchOpId = ((SearchOperators) itemFCSplRate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fCSplRate", "%" + this.fCSplRate.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fCSplRate", this.fCSplRate.getValue(), searchOpId));
				}
			}
		}
	  if (this.fCIntRate.getValue()!=null) {	  
	    final Listitem itemFCIntRate = this.sortOperator_fCIntRate.getSelectedItem();
	  	if (itemFCIntRate != null) {
	 		final int searchOpId = ((SearchOperators) itemFCIntRate.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.fCIntRate.getValue()!=null){
	 				so.addFilter(new Filter("fCIntRate",1, searchOpId));
	 			}else{
	 				so.addFilter(new Filter("fCIntRate",0, searchOpId));	
	 			}
	 		}
	 	}
	  }	
		if (StringUtils.isNotEmpty(this.fCDftIntFrq.getValue())) {

			// get the search operator
			final Listitem itemFCDftIntFrq = this.sortOperator_fCDftIntFrq.getSelectedItem();

			if (itemFCDftIntFrq != null) {
				final int searchOpId = ((SearchOperators) itemFCDftIntFrq.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fCDftIntFrq", "%" + this.fCDftIntFrq.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fCDftIntFrq", this.fCDftIntFrq.getValue(), searchOpId));
				}
			}
		}
		// get the search operatorxxx
		final Listitem itemFCIsIntCpz = this.sortOperator_fCIsIntCpz.getSelectedItem();

		if (itemFCIsIntCpz != null) {
			final int searchOpId = ((SearchOperators) itemFCIsIntCpz.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.fCIsIntCpz.isChecked()){
					so.addFilter(new Filter("fCIsIntCpz",1, searchOpId));
				}else{
					so.addFilter(new Filter("fCIsIntCpz",0, searchOpId));	
				}
			}
		}
		if (StringUtils.isNotEmpty(this.fCCpzFrq.getValue())) {

			// get the search operator
			final Listitem itemFCCpzFrq = this.sortOperator_fCCpzFrq.getSelectedItem();

			if (itemFCCpzFrq != null) {
				final int searchOpId = ((SearchOperators) itemFCCpzFrq.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fCCpzFrq", "%" + this.fCCpzFrq.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fCCpzFrq", this.fCCpzFrq.getValue(), searchOpId));
				}
			}
		}
		// get the search operatorxxx
		final Listitem itemFCIsRvwAlw = this.sortOperator_fCIsRvwAlw.getSelectedItem();

		if (itemFCIsRvwAlw != null) {
			final int searchOpId = ((SearchOperators) itemFCIsRvwAlw.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.fCIsRvwAlw.isChecked()){
					so.addFilter(new Filter("fCIsRvwAlw",1, searchOpId));
				}else{
					so.addFilter(new Filter("fCIsRvwAlw",0, searchOpId));	
				}
			}
		}
		if (StringUtils.isNotEmpty(this.fCRvwFrq.getValue())) {

			// get the search operator
			final Listitem itemFCRvwFrq = this.sortOperator_fCRvwFrq.getSelectedItem();

			if (itemFCRvwFrq != null) {
				final int searchOpId = ((SearchOperators) itemFCRvwFrq.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fCRvwFrq", "%" + this.fCRvwFrq.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fCRvwFrq", this.fCRvwFrq.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.fCGrcRateType.getValue())) {

			// get the search operator
			final Listitem itemFCGrcRateType = this.sortOperator_fCGrcRateType.getSelectedItem();

			if (itemFCGrcRateType != null) {
				final int searchOpId = ((SearchOperators) itemFCGrcRateType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fCGrcRateType", "%" + this.fCGrcRateType.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fCGrcRateType", this.fCGrcRateType.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.fCGrcBaseRate.getValue())) {

			// get the search operator
			final Listitem itemFCGrcBaseRate = this.sortOperator_fCGrcBaseRate.getSelectedItem();

			if (itemFCGrcBaseRate != null) {
				final int searchOpId = ((SearchOperators) itemFCGrcBaseRate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fCGrcBaseRate", "%" + this.fCGrcBaseRate.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fCGrcBaseRate", this.fCGrcBaseRate.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.fCGrcSplRate.getValue())) {

			// get the search operator
			final Listitem itemFCGrcSplRate = this.sortOperator_fCGrcSplRate.getSelectedItem();

			if (itemFCGrcSplRate != null) {
				final int searchOpId = ((SearchOperators) itemFCGrcSplRate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fCGrcSplRate", "%" + this.fCGrcSplRate.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fCGrcSplRate", this.fCGrcSplRate.getValue(), searchOpId));
				}
			}
		}
	  if (this.fCGrcIntRate.getValue()!=null) {	  
	    final Listitem itemFCGrcIntRate = this.sortOperator_fCGrcIntRate.getSelectedItem();
	  	if (itemFCGrcIntRate != null) {
	 		final int searchOpId = ((SearchOperators) itemFCGrcIntRate.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.fCGrcIntRate.getValue()!=null){
	 				so.addFilter(new Filter("fCGrcIntRate",1, searchOpId));
	 			}else{
	 				so.addFilter(new Filter("fCGrcIntRate",0, searchOpId));	
	 			}
	 		}
	 	}
	  }	
		if (StringUtils.isNotEmpty(this.fCGrcDftIntFrq.getValue())) {

			// get the search operator
			final Listitem itemFCGrcDftIntFrq = this.sortOperator_fCGrcDftIntFrq.getSelectedItem();

			if (itemFCGrcDftIntFrq != null) {
				final int searchOpId = ((SearchOperators) itemFCGrcDftIntFrq.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fCGrcDftIntFrq", "%" + this.fCGrcDftIntFrq.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fCGrcDftIntFrq", this.fCGrcDftIntFrq.getValue(), searchOpId));
				}
			}
		}
		// get the search operatorxxx
		final Listitem itemFCGrcIsIntCpz = this.sortOperator_fCGrcIsIntCpz.getSelectedItem();

		if (itemFCGrcIsIntCpz != null) {
			final int searchOpId = ((SearchOperators) itemFCGrcIsIntCpz.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.fCGrcIsIntCpz.isChecked()){
					so.addFilter(new Filter("fCGrcIsIntCpz",1, searchOpId));
				}else{
					so.addFilter(new Filter("fCGrcIsIntCpz",0, searchOpId));	
				}
			}
		}
		if (StringUtils.isNotEmpty(this.fCGrcCpzFrq.getValue())) {

			// get the search operator
			final Listitem itemFCGrcCpzFrq = this.sortOperator_fCGrcCpzFrq.getSelectedItem();

			if (itemFCGrcCpzFrq != null) {
				final int searchOpId = ((SearchOperators) itemFCGrcCpzFrq.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fCGrcCpzFrq", "%" + this.fCGrcCpzFrq.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fCGrcCpzFrq", this.fCGrcCpzFrq.getValue(), searchOpId));
				}
			}
		}
		// get the search operatorxxx
		final Listitem itemFCGrcIsRvwAlw = this.sortOperator_fCGrcIsRvwAlw.getSelectedItem();

		if (itemFCGrcIsRvwAlw != null) {
			final int searchOpId = ((SearchOperators) itemFCGrcIsRvwAlw.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.fCGrcIsRvwAlw.isChecked()){
					so.addFilter(new Filter("fCGrcIsRvwAlw",1, searchOpId));
				}else{
					so.addFilter(new Filter("fCGrcIsRvwAlw",0, searchOpId));	
				}
			}
		}
		if (StringUtils.isNotEmpty(this.fCGrcRvwFrq.getValue())) {

			// get the search operator
			final Listitem itemFCGrcRvwFrq = this.sortOperator_fCGrcRvwFrq.getSelectedItem();

			if (itemFCGrcRvwFrq != null) {
				final int searchOpId = ((SearchOperators) itemFCGrcRvwFrq.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fCGrcRvwFrq", "%" + this.fCGrcRvwFrq.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fCGrcRvwFrq", this.fCGrcRvwFrq.getValue(), searchOpId));
				}
			}
		}
	  if (this.fCMinTerm.getValue()!=null) {	  
	    final Listitem itemFCMinTerm = this.sortOperator_fCMinTerm.getSelectedItem();
	  	if (itemFCMinTerm != null) {
	 		final int searchOpId = ((SearchOperators) itemFCMinTerm.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.fCMinTerm.getValue()!=null){
	 				so.addFilter(new Filter("fCMinTerm",1, searchOpId));
	 			}else{
	 				so.addFilter(new Filter("fCMinTerm",0, searchOpId));	
	 			}
	 		}
	 	}
	  }	
	  if (this.fCMaxTerm.getValue()!=null) {	  
	    final Listitem itemFCMaxTerm = this.sortOperator_fCMaxTerm.getSelectedItem();
	  	if (itemFCMaxTerm != null) {
	 		final int searchOpId = ((SearchOperators) itemFCMaxTerm.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.fCMaxTerm.getValue()!=null){
	 				so.addFilter(new Filter("fCMaxTerm",1, searchOpId));
	 			}else{
	 				so.addFilter(new Filter("fCMaxTerm",0, searchOpId));	
	 			}
	 		}
	 	}
	  }	
	  if (this.fCDftTerms.getValue()!=null) {	  
	    final Listitem itemFCDftTerms = this.sortOperator_fCDftTerms.getSelectedItem();
	  	if (itemFCDftTerms != null) {
	 		final int searchOpId = ((SearchOperators) itemFCDftTerms.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.fCDftTerms.getValue()!=null){
	 				so.addFilter(new Filter("fCDftTerms",1, searchOpId));
	 			}else{
	 				so.addFilter(new Filter("fCDftTerms",0, searchOpId));	
	 			}
	 		}
	 	}
	  }	
		if (StringUtils.isNotEmpty(this.fCRpyFrq.getValue())) {

			// get the search operator
			final Listitem itemFCRpyFrq = this.sortOperator_fCRpyFrq.getSelectedItem();

			if (itemFCRpyFrq != null) {
				final int searchOpId = ((SearchOperators) itemFCRpyFrq.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fCRpyFrq", "%" + this.fCRpyFrq.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fCRpyFrq", this.fCRpyFrq.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.fCRepayMethod.getValue())) {

			// get the search operator
			final Listitem itemFCRepayMethod = this.sortOperator_fCRepayMethod.getSelectedItem();

			if (itemFCRepayMethod != null) {
				final int searchOpId = ((SearchOperators) itemFCRepayMethod.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fCRepayMethod", "%" + this.fCRepayMethod.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fCRepayMethod", this.fCRepayMethod.getValue(), searchOpId));
				}
			}
		}
		// get the search operatorxxx
		final Listitem itemFCIsAlwPartialRpy = this.sortOperator_fCIsAlwPartialRpy.getSelectedItem();

		if (itemFCIsAlwPartialRpy != null) {
			final int searchOpId = ((SearchOperators) itemFCIsAlwPartialRpy.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.fCIsAlwPartialRpy.isChecked()){
					so.addFilter(new Filter("fCIsAlwPartialRpy",1, searchOpId));
				}else{
					so.addFilter(new Filter("fCIsAlwPartialRpy",0, searchOpId));	
				}
			}
		}
		// get the search operatorxxx
		final Listitem itemFCIsAlwDifferment = this.sortOperator_fCIsAlwDifferment.getSelectedItem();

		if (itemFCIsAlwDifferment != null) {
			final int searchOpId = ((SearchOperators) itemFCIsAlwDifferment.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.fCIsAlwDifferment.isChecked()){
					so.addFilter(new Filter("fCIsAlwDifferment",1, searchOpId));
				}else{
					so.addFilter(new Filter("fCIsAlwDifferment",0, searchOpId));	
				}
			}
		}
	  if (this.fCMaxDifferment.getValue()!=null) {	  
	    final Listitem itemFCMaxDifferment = this.sortOperator_fCMaxDifferment.getSelectedItem();
	  	if (itemFCMaxDifferment != null) {
	 		final int searchOpId = ((SearchOperators) itemFCMaxDifferment.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.fCMaxDifferment.getValue()!=null){
	 				so.addFilter(new Filter("fCMaxDifferment",1, searchOpId));
	 			}else{
	 				so.addFilter(new Filter("fCMaxDifferment",0, searchOpId));	
	 			}
	 		}
	 	}
	  }	
		// get the search operatorxxx
		final Listitem itemFCIsAlwFrqDifferment = this.sortOperator_fCIsAlwFrqDifferment.getSelectedItem();

		if (itemFCIsAlwFrqDifferment != null) {
			final int searchOpId = ((SearchOperators) itemFCIsAlwFrqDifferment.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.fCIsAlwFrqDifferment.isChecked()){
					so.addFilter(new Filter("fCIsAlwFrqDifferment",1, searchOpId));
				}else{
					so.addFilter(new Filter("fCIsAlwFrqDifferment",0, searchOpId));	
				}
			}
		}
	  if (this.fCMaxFrqDifferment.getValue()!=null) {	  
	    final Listitem itemFCMaxFrqDifferment = this.sortOperator_fCMaxFrqDifferment.getSelectedItem();
	  	if (itemFCMaxFrqDifferment != null) {
	 		final int searchOpId = ((SearchOperators) itemFCMaxFrqDifferment.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.fCMaxFrqDifferment.getValue()!=null){
	 				so.addFilter(new Filter("fCMaxFrqDifferment",1, searchOpId));
	 			}else{
	 				so.addFilter(new Filter("fCMaxFrqDifferment",0, searchOpId));	
	 			}
	 		}
	 	}
	  }	
		// get the search operatorxxx
		final Listitem itemFCIsAlwEarlyRpy = this.sortOperator_fCIsAlwEarlyRpy.getSelectedItem();

		if (itemFCIsAlwEarlyRpy != null) {
			final int searchOpId = ((SearchOperators) itemFCIsAlwEarlyRpy.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.fCIsAlwEarlyRpy.isChecked()){
					so.addFilter(new Filter("fCIsAlwEarlyRpy",1, searchOpId));
				}else{
					so.addFilter(new Filter("fCIsAlwEarlyRpy",0, searchOpId));	
				}
			}
		}
		// get the search operatorxxx
		final Listitem itemFCIsAlwEarlySettle = this.sortOperator_fCIsAlwEarlySettle.getSelectedItem();

		if (itemFCIsAlwEarlySettle != null) {
			final int searchOpId = ((SearchOperators) itemFCIsAlwEarlySettle.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.fCIsAlwEarlySettle.isChecked()){
					so.addFilter(new Filter("fCIsAlwEarlySettle",1, searchOpId));
				}else{
					so.addFilter(new Filter("fCIsAlwEarlySettle",0, searchOpId));	
				}
			}
		}
		// get the search operatorxxx
		final Listitem itemFCIsDwPayRequired = this.sortOperator_fCIsDwPayRequired.getSelectedItem();

		if (itemFCIsDwPayRequired != null) {
			final int searchOpId = ((SearchOperators) itemFCIsDwPayRequired.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.fCIsDwPayRequired.isChecked()){
					so.addFilter(new Filter("fCIsDwPayRequired",1, searchOpId));
				}else{
					so.addFilter(new Filter("fCIsDwPayRequired",0, searchOpId));	
				}
			}
		}
		if (StringUtils.isNotEmpty(this.fCRvwRateApplFor.getValue())) {

			// get the search operator
			final Listitem itemFCRvwRateApplFor = this.sortOperator_fCRvwRateApplFor.getSelectedItem();

			if (itemFCRvwRateApplFor != null) {
				final int searchOpId = ((SearchOperators) itemFCRvwRateApplFor.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fCRvwRateApplFor", "%" + this.fCRvwRateApplFor.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fCRvwRateApplFor", this.fCRvwRateApplFor.getValue(), searchOpId));
				}
			}
		}
		// get the search operatorxxx
		final Listitem itemFCAlwRateChangeAnyDate = this.sortOperator_fCAlwRateChangeAnyDate.getSelectedItem();

		if (itemFCAlwRateChangeAnyDate != null) {
			final int searchOpId = ((SearchOperators) itemFCAlwRateChangeAnyDate.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.fCAlwRateChangeAnyDate.isChecked()){
					so.addFilter(new Filter("fCAlwRateChangeAnyDate",1, searchOpId));
				}else{
					so.addFilter(new Filter("fCAlwRateChangeAnyDate",0, searchOpId));	
				}
			}
		}
		if (StringUtils.isNotEmpty(this.fCGrcRvwRateApplFor.getValue())) {

			// get the search operator
			final Listitem itemFCGrcRvwRateApplFor = this.sortOperator_fCGrcRvwRateApplFor.getSelectedItem();

			if (itemFCGrcRvwRateApplFor != null) {
				final int searchOpId = ((SearchOperators) itemFCGrcRvwRateApplFor.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fCGrcRvwRateApplFor", "%" + this.fCGrcRvwRateApplFor.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fCGrcRvwRateApplFor", this.fCGrcRvwRateApplFor.getValue(), searchOpId));
				}
			}
		}
		// get the search operatorxxx
		final Listitem itemFCIsIntCpzAtGrcEnd = this.sortOperator_fCIsIntCpzAtGrcEnd.getSelectedItem();

		if (itemFCIsIntCpzAtGrcEnd != null) {
			final int searchOpId = ((SearchOperators) itemFCIsIntCpzAtGrcEnd.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.fCIsIntCpzAtGrcEnd.isChecked()){
					so.addFilter(new Filter("fCIsIntCpzAtGrcEnd",1, searchOpId));
				}else{
					so.addFilter(new Filter("fCIsIntCpzAtGrcEnd",0, searchOpId));	
				}
			}
		}
		// get the search operatorxxx
		final Listitem itemFCGrcAlwRateChgAnyDate = this.sortOperator_fCGrcAlwRateChgAnyDate.getSelectedItem();

		if (itemFCGrcAlwRateChgAnyDate != null) {
			final int searchOpId = ((SearchOperators) itemFCGrcAlwRateChgAnyDate.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.fCGrcAlwRateChgAnyDate.isChecked()){
					so.addFilter(new Filter("fCGrcAlwRateChgAnyDate",1, searchOpId));
				}else{
					so.addFilter(new Filter("fCGrcAlwRateChgAnyDate",0, searchOpId));	
				}
			}
		}
	  if (this.fCMinDownPayAmount.getValue()!=null) {	  
	    final Listitem itemFCMinDownPayAmount = this.sortOperator_fCMinDownPayAmount.getSelectedItem();
	  	if (itemFCMinDownPayAmount != null) {
	 		final int searchOpId = ((SearchOperators) itemFCMinDownPayAmount.getAttribute("data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			
	 			if(this.fCMinDownPayAmount.getValue()!=null){
	 				so.addFilter(new Filter("fCMinDownPayAmount",1, searchOpId));
	 			}else{
	 				so.addFilter(new Filter("fCMinDownPayAmount",0, searchOpId));	
	 			}
	 		}
	 	}
	  }	
		if (StringUtils.isNotEmpty(this.fCSchCalCodeOnRvw.getValue())) {

			// get the search operator
			final Listitem itemFCSchCalCodeOnRvw = this.sortOperator_fCSchCalCodeOnRvw.getSelectedItem();

			if (itemFCSchCalCodeOnRvw != null) {
				final int searchOpId = ((SearchOperators) itemFCSchCalCodeOnRvw.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fCSchCalCodeOnRvw", "%" + this.fCSchCalCodeOnRvw.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fCSchCalCodeOnRvw", this.fCSchCalCodeOnRvw.getValue(), searchOpId));
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
		so.addSort("FCCode", false);

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
		this.financeCampaignCtrl.setSearchObj(so);

		final Listbox listBox = this.financeCampaignCtrl.listBoxFinanceCampaign;
		final Paging paging = this.financeCampaignCtrl.pagingFinanceCampaignList;
		

		// set the model to the listbox with the initial resultset get by the DAO method.
		((PagedListWrapper<FinanceCampaign>) listBox.getModel()).init(so, listBox, paging);
		this.financeCampaignCtrl.setSearchObj(so);

		this.label_FinanceCampaignSearchResult.setValue(Labels.getLabel("label_FinanceCampaignSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setFinanceCampaignService(FinanceCampaignService financeCampaignService) {
		this.financeCampaignService = financeCampaignService;
	}

	public FinanceCampaignService getFinanceCampaignService() {
		return this.financeCampaignService;
	}
}