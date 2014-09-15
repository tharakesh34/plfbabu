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
 * FileName    		:  WIFFinanceScheduleDetailSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.wiffinancescheduledetail;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.finance.WIFFinanceScheduleDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;


public class WIFFinanceScheduleDetailSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(WIFFinanceScheduleDetailSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_WIFFinanceScheduleDetailSearch; // autowired
	
	protected Textbox finReference; // autowired
	protected Listbox sortOperator_finReference; // autowired
	protected Textbox schDate; // autowired
	protected Listbox sortOperator_schDate; // autowired
	protected Textbox schSeq; // autowired
	protected Listbox sortOperator_schSeq; // autowired
	protected Checkbox pftOnSchDate; // autowired
	protected Listbox sortOperator_pftOnSchDate; // autowired
	protected Checkbox cpzOnSchDate; // autowired
	protected Listbox sortOperator_cpzOnSchDate; // autowired
	protected Checkbox repayOnSchDate; // autowired
	protected Listbox sortOperator_repayOnSchDate; // autowired
	protected Checkbox rvwOnSchDate; // autowired
	protected Listbox sortOperator_rvwOnSchDate; // autowired
	protected Checkbox disbOnSchDate; // autowired
	protected Listbox sortOperator_disbOnSchDate; // autowired
	protected Checkbox downpaymentOnSchDate; // autowired
	protected Listbox sortOperator_downpaymentOnSchDate; // autowired
	protected Textbox balanceForPftCal; // autowired
	protected Listbox sortOperator_balanceForPftCal; // autowired
	protected Textbox baseRate; // autowired
	protected Listbox sortOperator_baseRate; // autowired
	protected Textbox splRate; // autowired
	protected Listbox sortOperator_splRate; // autowired
	protected Textbox actRate; // autowired
	protected Listbox sortOperator_actRate; // autowired
	protected Textbox adjRate; // autowired
	protected Listbox sortOperator_adjRate; // autowired
	protected Textbox noOfDays; // autowired
	protected Listbox sortOperator_noOfDays; // autowired
	protected Textbox dayFactor; // autowired
	protected Listbox sortOperator_dayFactor; // autowired
	protected Textbox profitCalc; // autowired
	protected Listbox sortOperator_profitCalc; // autowired
	protected Textbox profitSchd; // autowired
	protected Listbox sortOperator_profitSchd; // autowired
	protected Textbox principalSchd; // autowired
	protected Listbox sortOperator_principalSchd; // autowired
	protected Textbox repayAmount; // autowired
	protected Listbox sortOperator_repayAmount; // autowired
	protected Textbox profitBalance; // autowired
	protected Listbox sortOperator_profitBalance; // autowired
	protected Textbox disbAmount; // autowired
	protected Listbox sortOperator_disbAmount; // autowired
	protected Textbox downPaymentAmount; // autowired
	protected Listbox sortOperator_downPaymentAmount; // autowired
	protected Textbox cpzAmount; // autowired
	protected Listbox sortOperator_cpzAmount; // autowired
	protected Textbox diffProfitSchd; // autowired
	protected Listbox sortOperator_diffProfitSchd; // autowired
	protected Textbox dIffPrincipalSchd; // autowired
	protected Listbox sortOperator_dIffPrincipalSchd; // autowired
	protected Textbox closingBalance; // autowired
	protected Listbox sortOperator_closingBalance; // autowired
	protected Textbox profitFraction; // autowired
	protected Listbox sortOperator_profitFraction; // autowired
	protected Textbox prvRepayAmount; // autowired
	protected Listbox sortOperator_prvRepayAmount; // autowired
	protected Textbox deffProfitBal; // autowired
	protected Listbox sortOperator_deffProfitBal; // autowired
	protected Textbox diffPrincipalBal; // autowired
	protected Listbox sortOperator_diffPrincipalBal; // autowired
	protected Textbox schdPriPaid; // autowired
	protected Listbox sortOperator_schdPriPaid; // autowired
	protected Checkbox isSchdPftPaid; // autowired
	protected Listbox sortOperator_isSchdPftPaid; // autowired
	protected Textbox recordStatus; // autowired
	protected Listbox recordType;	// autowired
	protected Listbox sortOperator_recordStatus; // autowired
	protected Listbox sortOperator_recordType; // autowired
	
	protected Label label_WIFFinanceScheduleDetailSearch_RecordStatus; // autowired
	protected Label label_WIFFinanceScheduleDetailSearch_RecordType; // autowired
	protected Label label_WIFFinanceScheduleDetailSearchResult; // autowired

	// not auto wired vars
	private transient WIFFinanceScheduleDetailListCtrl wIFFinanceScheduleDetailCtrl; // overhanded per param
	private transient WIFFinanceScheduleDetailService wIFFinanceScheduleDetailService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("WIFFinanceScheduleDetail");
	
	/**
	 * constructor
	 */
	public WIFFinanceScheduleDetailSearchCtrl() {
		super();
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_WIFFinanceScheduleDetailSearch(Event event) throws Exception {

		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("wIFFinanceScheduleDetailCtrl")) {
			this.wIFFinanceScheduleDetailCtrl = (WIFFinanceScheduleDetailListCtrl) args.get("wIFFinanceScheduleDetailCtrl");
		} else {
			this.wIFFinanceScheduleDetailCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_finReference.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finReference.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_schDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_schDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_schSeq.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_schSeq.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_pftOnSchDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_pftOnSchDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_cpzOnSchDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_cpzOnSchDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_repayOnSchDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_repayOnSchDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_rvwOnSchDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_rvwOnSchDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_disbOnSchDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_disbOnSchDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_downpaymentOnSchDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_downpaymentOnSchDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_balanceForPftCal.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_balanceForPftCal.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_baseRate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_baseRate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_splRate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_splRate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_actRate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_actRate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_adjRate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_adjRate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_noOfDays.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_noOfDays.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_dayFactor.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_dayFactor.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_profitCalc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_profitCalc.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_profitSchd.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_profitSchd.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_principalSchd.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_principalSchd.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_repayAmount.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_repayAmount.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_profitBalance.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_profitBalance.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_disbAmount.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_disbAmount.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_downPaymentAmount.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_downPaymentAmount.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_cpzAmount.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_cpzAmount.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_diffProfitSchd.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_diffProfitSchd.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_dIffPrincipalSchd.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_dIffPrincipalSchd.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_closingBalance.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_closingBalance.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_profitFraction.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_profitFraction.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_prvRepayAmount.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_prvRepayAmount.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_deffProfitBal.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_deffProfitBal.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_diffPrincipalBal.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_diffPrincipalBal.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_schdPriPaid.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_schdPriPaid.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_isSchdPftPaid.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_isSchdPftPaid.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
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
			this.label_WIFFinanceScheduleDetailSearch_RecordStatus.setVisible(false);
			this.label_WIFFinanceScheduleDetailSearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<FinanceScheduleDetail> searchObj = (JdbcSearchObject<FinanceScheduleDetail>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
			    if (filter.getProperty().equals("finReference")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finReference, filter);
					this.finReference.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("schDate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_schDate, filter);
					this.schDate.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("schSeq")) {
					SearchOperators.restoreStringOperator(this.sortOperator_schSeq, filter);
					this.schSeq.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("pftOnSchDate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_pftOnSchDate, filter);
					this.pftOnSchDate.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("cpzOnSchDate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_cpzOnSchDate, filter);
					this.cpzOnSchDate.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("repayOnSchDate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_repayOnSchDate, filter);
					this.repayOnSchDate.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("rvwOnSchDate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_rvwOnSchDate, filter);
					this.rvwOnSchDate.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("disbOnSchDate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_disbOnSchDate, filter);
					this.disbOnSchDate.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("downpaymentOnSchDate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_downpaymentOnSchDate, filter);
					this.downpaymentOnSchDate.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("balanceForPftCal")) {
					SearchOperators.restoreStringOperator(this.sortOperator_balanceForPftCal, filter);
					this.balanceForPftCal.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("baseRate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_baseRate, filter);
					this.baseRate.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("splRate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_splRate, filter);
					this.splRate.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("actRate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_actRate, filter);
					this.actRate.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("adjRate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_adjRate, filter);
					this.adjRate.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("noOfDays")) {
					SearchOperators.restoreStringOperator(this.sortOperator_noOfDays, filter);
					this.noOfDays.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("dayFactor")) {
					SearchOperators.restoreStringOperator(this.sortOperator_dayFactor, filter);
					this.dayFactor.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("profitCalc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_profitCalc, filter);
					this.profitCalc.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("profitSchd")) {
					SearchOperators.restoreStringOperator(this.sortOperator_profitSchd, filter);
					this.profitSchd.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("principalSchd")) {
					SearchOperators.restoreStringOperator(this.sortOperator_principalSchd, filter);
					this.principalSchd.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("repayAmount")) {
					SearchOperators.restoreStringOperator(this.sortOperator_repayAmount, filter);
					this.repayAmount.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("profitBalance")) {
					SearchOperators.restoreStringOperator(this.sortOperator_profitBalance, filter);
					this.profitBalance.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("disbAmount")) {
					SearchOperators.restoreStringOperator(this.sortOperator_disbAmount, filter);
					this.disbAmount.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("downPaymentAmount")) {
					SearchOperators.restoreStringOperator(this.sortOperator_downPaymentAmount, filter);
					this.downPaymentAmount.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("cpzAmount")) {
					SearchOperators.restoreStringOperator(this.sortOperator_cpzAmount, filter);
					this.cpzAmount.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("diffProfitSchd")) {
					SearchOperators.restoreStringOperator(this.sortOperator_diffProfitSchd, filter);
					this.diffProfitSchd.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("dIffPrincipalSchd")) {
					SearchOperators.restoreStringOperator(this.sortOperator_dIffPrincipalSchd, filter);
					this.dIffPrincipalSchd.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("closingBalance")) {
					SearchOperators.restoreStringOperator(this.sortOperator_closingBalance, filter);
					this.closingBalance.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("profitFraction")) {
					SearchOperators.restoreStringOperator(this.sortOperator_profitFraction, filter);
					this.profitFraction.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("prvRepayAmount")) {
					SearchOperators.restoreStringOperator(this.sortOperator_prvRepayAmount, filter);
					this.prvRepayAmount.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("deffProfitBal")) {
					SearchOperators.restoreStringOperator(this.sortOperator_deffProfitBal, filter);
					this.deffProfitBal.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("diffPrincipalBal")) {
					SearchOperators.restoreStringOperator(this.sortOperator_diffPrincipalBal, filter);
					this.diffPrincipalBal.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("schdPriPaid")) {
					SearchOperators.restoreStringOperator(this.sortOperator_schdPriPaid, filter);
					this.schdPriPaid.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("isSchdPftPaid")) {
					SearchOperators.restoreStringOperator(this.sortOperator_isSchdPftPaid, filter);
					this.isSchdPftPaid.setValue(filter.getValue().toString());
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
		showWIFFinanceScheduleDetailSeekDialog();
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
		this.window_WIFFinanceScheduleDetailSearch.onClose();
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showWIFFinanceScheduleDetailSeekDialog() throws InterruptedException {

		try {
			// open the dialog in modal mode
			this.window_WIFFinanceScheduleDetailSearch.doModal();
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

		final JdbcSearchObject<FinanceScheduleDetail> so = new JdbcSearchObject<FinanceScheduleDetail>(FinanceScheduleDetail.class);

		if (isWorkFlowEnabled()){
			so.addTabelName("WIFFinScheduleDetails_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("WIFFinScheduleDetails_AView");
		}
		
		
		if (StringUtils.isNotEmpty(this.finReference.getValue())) {

			// get the search operator
			final Listitem itemFinReference = this.sortOperator_finReference.getSelectedItem();

			if (itemFinReference != null) {
				final int searchOpId = ((SearchOperators) itemFinReference.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finReference", "%" + this.finReference.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finReference", this.finReference.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.schDate.getValue())) {

			// get the search operator
			final Listitem itemSchDate = this.sortOperator_schDate.getSelectedItem();

			if (itemSchDate != null) {
				final int searchOpId = ((SearchOperators) itemSchDate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("schDate", "%" + this.schDate.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("schDate", this.schDate.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.schSeq.getValue())) {

			// get the search operator
			final Listitem itemSchSeq = this.sortOperator_schSeq.getSelectedItem();

			if (itemSchSeq != null) {
				final int searchOpId = ((SearchOperators) itemSchSeq.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("schSeq", "%" + this.schSeq.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("schSeq", this.schSeq.getValue(), searchOpId));
				}
			}
		}
		// get the search operatorxxx
		final Listitem itemPftOnSchDate = this.sortOperator_pftOnSchDate.getSelectedItem();

		if (itemPftOnSchDate != null) {
			final int searchOpId = ((SearchOperators) itemPftOnSchDate.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.pftOnSchDate.isChecked()){
					so.addFilter(new Filter("pftOnSchDate",1, searchOpId));
				}else{
					so.addFilter(new Filter("pftOnSchDate",0, searchOpId));	
				}
			}
		}
		// get the search operatorxxx
		final Listitem itemCpzOnSchDate = this.sortOperator_cpzOnSchDate.getSelectedItem();

		if (itemCpzOnSchDate != null) {
			final int searchOpId = ((SearchOperators) itemCpzOnSchDate.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.cpzOnSchDate.isChecked()){
					so.addFilter(new Filter("cpzOnSchDate",1, searchOpId));
				}else{
					so.addFilter(new Filter("cpzOnSchDate",0, searchOpId));	
				}
			}
		}
		// get the search operatorxxx
		final Listitem itemRepayOnSchDate = this.sortOperator_repayOnSchDate.getSelectedItem();

		if (itemRepayOnSchDate != null) {
			final int searchOpId = ((SearchOperators) itemRepayOnSchDate.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.repayOnSchDate.isChecked()){
					so.addFilter(new Filter("repayOnSchDate",1, searchOpId));
				}else{
					so.addFilter(new Filter("repayOnSchDate",0, searchOpId));	
				}
			}
		}
		// get the search operatorxxx
		final Listitem itemRvwOnSchDate = this.sortOperator_rvwOnSchDate.getSelectedItem();

		if (itemRvwOnSchDate != null) {
			final int searchOpId = ((SearchOperators) itemRvwOnSchDate.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.rvwOnSchDate.isChecked()){
					so.addFilter(new Filter("rvwOnSchDate",1, searchOpId));
				}else{
					so.addFilter(new Filter("rvwOnSchDate",0, searchOpId));	
				}
			}
		}
		// get the search operatorxxx
		final Listitem itemDisbOnSchDate = this.sortOperator_disbOnSchDate.getSelectedItem();

		if (itemDisbOnSchDate != null) {
			final int searchOpId = ((SearchOperators) itemDisbOnSchDate.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.disbOnSchDate.isChecked()){
					so.addFilter(new Filter("disbOnSchDate",1, searchOpId));
				}else{
					so.addFilter(new Filter("disbOnSchDate",0, searchOpId));	
				}
			}
		}
		// get the search operatorxxx
		final Listitem itemDownpaymentOnSchDate = this.sortOperator_downpaymentOnSchDate.getSelectedItem();

		if (itemDownpaymentOnSchDate != null) {
			final int searchOpId = ((SearchOperators) itemDownpaymentOnSchDate.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.downpaymentOnSchDate.isChecked()){
					so.addFilter(new Filter("downpaymentOnSchDate",1, searchOpId));
				}else{
					so.addFilter(new Filter("downpaymentOnSchDate",0, searchOpId));	
				}
			}
		}
		if (StringUtils.isNotEmpty(this.balanceForPftCal.getValue())) {

			// get the search operator
			final Listitem itemBalanceForPftCal = this.sortOperator_balanceForPftCal.getSelectedItem();

			if (itemBalanceForPftCal != null) {
				final int searchOpId = ((SearchOperators) itemBalanceForPftCal.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("balanceForPftCal", "%" + this.balanceForPftCal.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("balanceForPftCal", this.balanceForPftCal.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.baseRate.getValue())) {

			// get the search operator
			final Listitem itemBaseRate = this.sortOperator_baseRate.getSelectedItem();

			if (itemBaseRate != null) {
				final int searchOpId = ((SearchOperators) itemBaseRate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("baseRate", "%" + this.baseRate.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("baseRate", this.baseRate.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.splRate.getValue())) {

			// get the search operator
			final Listitem itemSplRate = this.sortOperator_splRate.getSelectedItem();

			if (itemSplRate != null) {
				final int searchOpId = ((SearchOperators) itemSplRate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("splRate", "%" + this.splRate.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("splRate", this.splRate.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.actRate.getValue())) {

			// get the search operator
			final Listitem itemActRate = this.sortOperator_actRate.getSelectedItem();

			if (itemActRate != null) {
				final int searchOpId = ((SearchOperators) itemActRate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("actRate", "%" + this.actRate.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("actRate", this.actRate.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.adjRate.getValue())) {

			// get the search operator
			final Listitem itemAdjRate = this.sortOperator_adjRate.getSelectedItem();

			if (itemAdjRate != null) {
				final int searchOpId = ((SearchOperators) itemAdjRate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("adjRate", "%" + this.adjRate.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("adjRate", this.adjRate.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.noOfDays.getValue())) {

			// get the search operator
			final Listitem itemNoOfDays = this.sortOperator_noOfDays.getSelectedItem();

			if (itemNoOfDays != null) {
				final int searchOpId = ((SearchOperators) itemNoOfDays.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("noOfDays", "%" + this.noOfDays.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("noOfDays", this.noOfDays.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.dayFactor.getValue())) {

			// get the search operator
			final Listitem itemDayFactor = this.sortOperator_dayFactor.getSelectedItem();

			if (itemDayFactor != null) {
				final int searchOpId = ((SearchOperators) itemDayFactor.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("dayFactor", "%" + this.dayFactor.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("dayFactor", this.dayFactor.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.profitCalc.getValue())) {

			// get the search operator
			final Listitem itemProfitCalc = this.sortOperator_profitCalc.getSelectedItem();

			if (itemProfitCalc != null) {
				final int searchOpId = ((SearchOperators) itemProfitCalc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("profitCalc", "%" + this.profitCalc.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("profitCalc", this.profitCalc.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.profitSchd.getValue())) {

			// get the search operator
			final Listitem itemProfitSchd = this.sortOperator_profitSchd.getSelectedItem();

			if (itemProfitSchd != null) {
				final int searchOpId = ((SearchOperators) itemProfitSchd.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("profitSchd", "%" + this.profitSchd.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("profitSchd", this.profitSchd.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.principalSchd.getValue())) {

			// get the search operator
			final Listitem itemPrincipalSchd = this.sortOperator_principalSchd.getSelectedItem();

			if (itemPrincipalSchd != null) {
				final int searchOpId = ((SearchOperators) itemPrincipalSchd.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("principalSchd", "%" + this.principalSchd.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("principalSchd", this.principalSchd.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.repayAmount.getValue())) {

			// get the search operator
			final Listitem itemRepayAmount = this.sortOperator_repayAmount.getSelectedItem();

			if (itemRepayAmount != null) {
				final int searchOpId = ((SearchOperators) itemRepayAmount.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("repayAmount", "%" + this.repayAmount.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("repayAmount", this.repayAmount.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.profitBalance.getValue())) {

			// get the search operator
			final Listitem itemProfitBalance = this.sortOperator_profitBalance.getSelectedItem();

			if (itemProfitBalance != null) {
				final int searchOpId = ((SearchOperators) itemProfitBalance.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("profitBalance", "%" + this.profitBalance.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("profitBalance", this.profitBalance.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.disbAmount.getValue())) {

			// get the search operator
			final Listitem itemDisbAmount = this.sortOperator_disbAmount.getSelectedItem();

			if (itemDisbAmount != null) {
				final int searchOpId = ((SearchOperators) itemDisbAmount.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("disbAmount", "%" + this.disbAmount.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("disbAmount", this.disbAmount.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.downPaymentAmount.getValue())) {

			// get the search operator
			final Listitem itemDownPaymentAmount = this.sortOperator_downPaymentAmount.getSelectedItem();

			if (itemDownPaymentAmount != null) {
				final int searchOpId = ((SearchOperators) itemDownPaymentAmount.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("downPaymentAmount", "%" + this.downPaymentAmount.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("downPaymentAmount", this.downPaymentAmount.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.cpzAmount.getValue())) {

			// get the search operator
			final Listitem itemCpzAmount = this.sortOperator_cpzAmount.getSelectedItem();

			if (itemCpzAmount != null) {
				final int searchOpId = ((SearchOperators) itemCpzAmount.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("cpzAmount", "%" + this.cpzAmount.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("cpzAmount", this.cpzAmount.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.diffProfitSchd.getValue())) {

			// get the search operator
			final Listitem itemDiffProfitSchd = this.sortOperator_diffProfitSchd.getSelectedItem();

			if (itemDiffProfitSchd != null) {
				final int searchOpId = ((SearchOperators) itemDiffProfitSchd.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("diffProfitSchd", "%" + this.diffProfitSchd.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("diffProfitSchd", this.diffProfitSchd.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.dIffPrincipalSchd.getValue())) {

			// get the search operator
			final Listitem itemDIffPrincipalSchd = this.sortOperator_dIffPrincipalSchd.getSelectedItem();

			if (itemDIffPrincipalSchd != null) {
				final int searchOpId = ((SearchOperators) itemDIffPrincipalSchd.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("dIffPrincipalSchd", "%" + this.dIffPrincipalSchd.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("dIffPrincipalSchd", this.dIffPrincipalSchd.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.closingBalance.getValue())) {

			// get the search operator
			final Listitem itemClosingBalance = this.sortOperator_closingBalance.getSelectedItem();

			if (itemClosingBalance != null) {
				final int searchOpId = ((SearchOperators) itemClosingBalance.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("closingBalance", "%" + this.closingBalance.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("closingBalance", this.closingBalance.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.profitFraction.getValue())) {

			// get the search operator
			final Listitem itemProfitFraction = this.sortOperator_profitFraction.getSelectedItem();

			if (itemProfitFraction != null) {
				final int searchOpId = ((SearchOperators) itemProfitFraction.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("profitFraction", "%" + this.profitFraction.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("profitFraction", this.profitFraction.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.prvRepayAmount.getValue())) {

			// get the search operator
			final Listitem itemPrvRepayAmount = this.sortOperator_prvRepayAmount.getSelectedItem();

			if (itemPrvRepayAmount != null) {
				final int searchOpId = ((SearchOperators) itemPrvRepayAmount.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("prvRepayAmount", "%" + this.prvRepayAmount.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("prvRepayAmount", this.prvRepayAmount.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.deffProfitBal.getValue())) {

			// get the search operator
			final Listitem itemDeffProfitBal = this.sortOperator_deffProfitBal.getSelectedItem();

			if (itemDeffProfitBal != null) {
				final int searchOpId = ((SearchOperators) itemDeffProfitBal.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("deffProfitBal", "%" + this.deffProfitBal.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("deffProfitBal", this.deffProfitBal.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.diffPrincipalBal.getValue())) {

			// get the search operator
			final Listitem itemDiffPrincipalBal = this.sortOperator_diffPrincipalBal.getSelectedItem();

			if (itemDiffPrincipalBal != null) {
				final int searchOpId = ((SearchOperators) itemDiffPrincipalBal.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("diffPrincipalBal", "%" + this.diffPrincipalBal.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("diffPrincipalBal", this.diffPrincipalBal.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.schdPriPaid.getValue())) {

			// get the search operator
			final Listitem itemSchdPriPaid = this.sortOperator_schdPriPaid.getSelectedItem();

			if (itemSchdPriPaid != null) {
				final int searchOpId = ((SearchOperators) itemSchdPriPaid.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("schdPriPaid", "%" + this.schdPriPaid.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("schdPriPaid", this.schdPriPaid.getValue(), searchOpId));
				}
			}
		}
		// get the search operatorxxx
		final Listitem itemIsSchdPftPaid = this.sortOperator_isSchdPftPaid.getSelectedItem();

		if (itemIsSchdPftPaid != null) {
			final int searchOpId = ((SearchOperators) itemIsSchdPftPaid.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.isSchdPftPaid.isChecked()){
					so.addFilter(new Filter("isSchdPftPaid",1, searchOpId));
				}else{
					so.addFilter(new Filter("isSchdPftPaid",0, searchOpId));	
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
		so.addSort("FinReference", false);

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
		this.wIFFinanceScheduleDetailCtrl.setSearchObj(so);

		final Listbox listBox = this.wIFFinanceScheduleDetailCtrl.listBoxWIFFinanceScheduleDetail;
		final Paging paging = this.wIFFinanceScheduleDetailCtrl.pagingWIFFinanceScheduleDetailList;
		

		// set the model to the listbox with the initial resultset get by the DAO method.
		((PagedListWrapper<FinanceScheduleDetail>) listBox.getModel()).init(so, listBox, paging);
		this.wIFFinanceScheduleDetailCtrl.setSearchObj(so);

		this.label_WIFFinanceScheduleDetailSearchResult.setValue(Labels.getLabel("label_WIFFinanceScheduleDetailSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setWIFFinanceScheduleDetailService(WIFFinanceScheduleDetailService wIFFinanceScheduleDetailService) {
		this.wIFFinanceScheduleDetailService = wIFFinanceScheduleDetailService;
	}

	public WIFFinanceScheduleDetailService getWIFFinanceScheduleDetailService() {
		return this.wIFFinanceScheduleDetailService;
	}
}