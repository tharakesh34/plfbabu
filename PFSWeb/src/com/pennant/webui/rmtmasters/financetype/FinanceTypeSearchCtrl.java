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
 * FileName    		:  FinanceTypeSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-06-2011    														*
 *                                                                  						*
 * Modified Date    :  30-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-06-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.rmtmasters.financetype;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/FinanceType/FinanceTypeSearch.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.search.SearchResult;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.rmtmasters.commodityFinanceType.CommodityFinanceTypeListCtrl;
import com.pennant.webui.rmtmasters.financetype.model.FinanceTypeComparator;
import com.pennant.webui.rmtmasters.financetype.model.FinanceTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

public class FinanceTypeSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -2412267117025447386L;
	private final static Logger logger = Logger.getLogger(FinanceTypeSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_FinanceTypeSearch; 				// autoWired

	protected Textbox 	finType; 							// autoWired
	protected Listbox 	sortOperator_finType; 				// autoWired
	protected Textbox 	finTypeDesc; 						// autoWired
	protected Listbox 	sortOperator_finTypeDesc; 			// autoWired
	protected Textbox 	finCcy; 							// autoWired
	protected Listbox 	sortOperator_finCcy; 				// autoWired
	protected Textbox 	finDaysCalType; 					// autoWired
	protected Listbox 	sortOperator_finDaysCalType; 		// autoWired
	protected Textbox 	finAcType; 							// autoWired
	protected Listbox	sortOperator_finAcType; 			// autoWired
	protected Textbox 	finContingentAcType; 				// autoWired
	protected Listbox 	sortOperator_finContingentAcType; 	// autoWired
	protected Checkbox 	finIsGenRef; 						// autoWired
	protected Listbox 	sortOperator_finIsGenRef; 			// autoWired
	protected Decimalbox finMaxAmount; 						// autoWired
	protected Listbox 	sortOperator_finMaxAmount; 			// autoWired
	protected Decimalbox finMinAmount; 						// autoWired
	protected Listbox 	sortOperator_finMinAmount;	 		// autoWired
	protected Checkbox 	finIsOpenNewFinAc; 					// autoWired
	protected Listbox 	sortOperator_finIsOpenNewFinAc; 	// autoWired
	protected Textbox 	finDftStmtFrq; 						// autoWired
	protected Listbox 	sortOperator_finDftStmtFrq; 		// autoWired
	protected Textbox 	finDftStmtFrqDay; 					// autoWired
	protected Listbox 	sortOperator_finDftStmtFrqDay; 		// autoWired
	protected Checkbox 	finIsAlwMD; 						// autoWired
	protected Listbox 	sortOperator_finIsAlwMD; 			// autoWired
	protected Textbox 	finSchdMthd; 						// autoWired
	protected Listbox 	sortOperator_finSchdMthd; 			// autoWired
	protected Checkbox 	fInIsAlwGrace; 						// autoWired
	protected Listbox 	sortOperator_fInIsAlwGrace; 		// autoWired
	protected Intbox 	finHistRetension; 					// autoWired
	protected Listbox 	sortOperator_finHistRetension; 		// autoWired
	protected Checkbox 	finIsInsureReq; 					// autoWired
	protected Listbox 	sortOperator_finIsInsureReq; 		// autoWired
	protected Checkbox 	finIsCollateralReq; 				// autoWired
	protected Listbox 	sortOperator_finIsCollateralReq; 	// autoWired
	protected Textbox 	finRateType; 						// autoWired
	protected Listbox 	sortOperator_finRateType; 			// autoWired
	protected Textbox 	finBaseRate; 						// autoWired
	protected Listbox 	sortOperator_finBaseRate; 			// autoWired
	protected Textbox 	finSplRate; 						// autoWired
	protected Listbox 	sortOperator_finSplRate; 			// autoWired
	protected Textbox 	finIntRate; 						// autoWired
	protected Listbox 	sortOperator_finIntRate; 			// autoWired
	protected Textbox 	fInMinRate; 						// autoWired
	protected Listbox 	sortOperator_fInMinRate; 			// autoWired
	protected Textbox 	finMaxRate; 						// autoWired
	protected Listbox 	sortOperator_finMaxRate; 			// autoWired
	protected Textbox 	finDftIntFrq; 						// autoWired
	protected Listbox 	sortOperator_finDftIntFrq; 			// autoWired
	protected Textbox 	finDftIntFrqDay; 					// autoWired
	protected Listbox 	sortOperator_finDftIntFrqDay; 		// autoWired
	protected Checkbox 	finIsIntCpz; 						// autoWired
	protected Listbox 	sortOperator_finIsIntCpz; 			// autoWired
	protected Textbox 	finCpzFrq; 							// autoWired
	protected Listbox 	sortOperator_finCpzFrq; 			// autoWired
	protected Textbox 	finCpzFrqDay; 						// autoWired
	protected Listbox 	sortOperator_finCpzFrqDay; 			// autoWired
	protected Checkbox 	finIsRvwAlw; 						// autoWired
	protected Listbox 	sortOperator_finIsRvwAlw; 			// autoWired
	protected Textbox 	finRvwFrq; 							// autoWired
	protected Listbox 	sortOperator_finRvwFrq; 			// autoWired
	protected Textbox 	finRvwFrqDay; 						// autoWired
	protected Listbox 	sortOperator_finRvwFrqDay; 			// autoWired
	protected Textbox 	finGrcRateType; 					// autoWired
	protected Listbox 	sortOperator_finGrcRateType; 		// autoWired
	protected Textbox 	finGrcBaseRate; 					// autoWired
	protected Listbox 	sortOperator_finGrcBaseRate; 		// autoWired
	protected Textbox 	finGrcSplRate; 						// autoWired
	protected Listbox 	sortOperator_finGrcSplRate; 		// autoWired
	protected Textbox 	finGrcIntRate; 						// autoWired
	protected Listbox 	sortOperator_finGrcIntRate; 		// autoWired
	protected Textbox 	fInGrcMinRate; 						// autoWired
	protected Listbox 	sortOperator_fInGrcMinRate; 		// autoWired
	protected Textbox 	finGrcMaxRate; 						// autoWired
	protected Listbox 	sortOperator_finGrcMaxRate; 		// autoWired
	protected Textbox 	finGrcDftIntFrq; 					// autoWired
	protected Listbox 	sortOperator_finGrcDftIntFrq; 		// autoWired
	protected Textbox 	finGrcDftIntFrqDay; 				// autoWired
	protected Listbox 	sortOperator_finGrcDftIntFrqDay; 	// autoWired
	protected Checkbox 	finGrcIsIntCpz; 					// autoWired
	protected Listbox 	sortOperator_finGrcIsIntCpz; 		// autoWired
	protected Textbox 	finGrcCpzFrq; 						// autoWired
	protected Listbox 	sortOperator_finGrcCpzFrq; 			// autoWired
	protected Textbox 	finGrcCpzFrqDay; 					// autoWired
	protected Listbox 	sortOperator_finGrcCpzFrqDay; 		// autoWired
	protected Checkbox 	finGrcIsRvwAlw; 					// autoWired
	protected Listbox 	sortOperator_finGrcIsRvwAlw; 		// autoWired
	protected Textbox 	finGrcRvwFrq; 						// autoWired
	protected Listbox 	sortOperator_finGrcRvwFrq; 			// autoWired
	protected Textbox 	finGrcRvwFrqDay; 					// autoWired
	protected Listbox 	sortOperator_finGrcRvwFrqDay; 		// autoWired
	protected Intbox 	finMinTerm; 						// autoWired
	protected Listbox 	sortOperator_finMinTerm; 			// autoWired
	protected Intbox 	finMaxTerm; 						// autoWired
	protected Listbox 	sortOperator_finMaxTerm; 			// autoWired
	protected Intbox 	finDftTerms; 						// autoWired
	protected Listbox 	sortOperator_finDftTerms; 			// autoWired
	protected Textbox 	finRpyFrq; 							// autoWired
	protected Listbox 	sortOperator_finRpyFrq; 			// autoWired
	protected Textbox 	finRpyFrqDay; 						// autoWired
	protected Listbox 	sortOperator_finRpyFrqDay; 			// autoWired
	protected Textbox 	fInRepayMethod; 					// autoWired
	protected Listbox 	sortOperator_fInRepayMethod; 		// autoWired
	protected Checkbox 	finIsAlwPartialRpy; 				// autoWired
	protected Listbox 	sortOperator_finIsAlwPartialRpy; 	// autoWired
	protected Checkbox 	finIsAlwDifferment; 				// autoWired
	protected Listbox 	sortOperator_finIsAlwDifferment; 	// autoWired
	protected Checkbox 	finIsAlwEarlyRpy; 					// autoWired
	protected Listbox 	sortOperator_finIsAlwEarlyRpy; 		// autoWired
	protected Checkbox 	finIsAlwEarlySettle; 				// autoWired
	protected Listbox 	sortOperator_finIsAlwEarlySettle; 	// autoWired
	protected Textbox 	finODRpyTries; 						// autoWired
	protected Listbox 	sortOperator_finODRpyTries; 		// autoWired
//	protected Textbox 	finLatePayRule; 					// autoWired
//	protected Listbox 	sortOperator_finLatePayRule; 		// autoWired
//	protected Textbox 	finEarlyPayRule; 					// autoWired
//	protected Listbox 	sortOperator_finEarlyPayRule; 		// autoWired
//	protected Textbox 	finEarlySettleRule; 				// autoWired
//	protected Listbox 	sortOperator_finEarlySettleRule; 	// autoWired
	protected Textbox 	finAEAddDsbOD; 						// autoWired
	protected Listbox 	sortOperator_finAEAddDsbOD; 		// autoWired
	protected Textbox 	finAEAddDsbFD; 						// autoWired
	protected Listbox 	sortOperator_finAEAddDsbFD; 		// autoWired
	protected Textbox 	finAEAddDsbFDA; 					// autoWired
	protected Listbox 	sortOperator_finAEAddDsbFDA; 		// autoWired
	protected Textbox 	finAEAmzNorm; 						// autoWired
	protected Listbox 	sortOperator_finAEAmzNorm; 			// autoWired
	protected Textbox 	finAEAmzSusp; 						// autoWired
	protected Listbox 	sortOperator_finAEAmzSusp; 			// autoWired
	protected Textbox 	finAEToNoAmz; 						// autoWired
	protected Listbox 	sortOperator_finAEToNoAmz; 			// autoWired
	protected Textbox 	finToAmz; 							// autoWired
	protected Listbox 	sortOperator_finToAmz; 				// autoWired
	protected Textbox 	finAEIncPft; 						// autoWired
	protected Listbox 	sortOperator_finAEIncPft; 			// autoWired
	protected Textbox 	finAEDecPft; 						// autoWired
	protected Listbox 	sortOperator_finAEDecPft; 			// autoWired
	protected Textbox 	finAERepay; 						// autoWired
	protected Listbox 	sortOperator_finAERepay; 			// autoWired
	protected Textbox 	finAEEarlyPay; 						// autoWired
	protected Listbox 	sortOperator_finAEEarlyPay; 		// autoWired
	protected Textbox 	finAEEarlySettle; 					// autoWired
	protected Listbox 	sortOperator_finAEEarlySettle; 		// autoWired
	protected Textbox 	finAEWriteOff; 						// autoWired
	protected Listbox 	sortOperator_finAEWriteOff; 		// autoWired
	protected Checkbox 	finIsActive; 						// autoWired
	protected Listbox 	sortOperator_finIsActive; 			// autoWired
	protected Textbox 	recordStatus; 						// autoWired
	protected Listbox 	recordType; 						// autoWired
	protected Listbox 	sortOperator_recordStatus; 			// autoWired
	protected Listbox 	sortOperator_recordType; 			// autoWired

	protected Label label_FinanceTypeSearch_RecordStatus; 	// autoWired
	protected Label label_FinanceTypeSearch_RecordType; 	// autoWired
	protected Label label_FinanceTypeSearchResult; 			// autoWired
	
	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<FinanceType> searchObj;

	// not auto wired Var's
	protected Listbox 	listBox;
	protected Paging 	paging ;
	private Object 		object;
	
	private Row row1;
	private Row row2;
	private Row row3;
	private Row row4;
	private Row row5;
	private Row row6;
	private Row row7;
	private Row row8;
	private Row row9;
	private Row row10;
	private Row row11;
	private Row row12;
	private Row row13;
	private Row row14;
	
	private transient FinanceTypeService financeTypeService;
	private transient CommodityFinanceTypeListCtrl commodityFinanceTypeListCtrl;
	private transient FinanceTypeListCtrl financeTypeListCtrl;
	private transient WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("FinanceType");

	/**
	 * constructor
	 */
	public FinanceTypeSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected FinanceType object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onCreate$window_FinanceTypeSearch(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("financeTypeList")) {
			  object=(Object)args.get("financeTypeList");
			  listBox = (Listbox)args.get("listBoxFinanceType");
			  paging = (Paging)args.get("pagingFinanceTypeList");
			  this.financeTypeListCtrl = (FinanceTypeListCtrl) args.get("financeTypeList");
		}

		if(object.getClass().isInstance(commodityFinanceTypeListCtrl)){
			this.window_FinanceTypeSearch.setHeight("250px");
			this.row1.setVisible(false);
			this.row2.setVisible(false);
			this.row3.setVisible(false);
			this.row4.setVisible(false);
			this.row5.setVisible(false);
			this.row6.setVisible(false);
			this.row7.setVisible(false);
			this.row8.setVisible(false);
			this.row9.setVisible(false);
			this.row10.setVisible(false);
			this.row11.setVisible(false);
			this.row12.setVisible(false);
			this.row13.setVisible(false);
			this.row14.setVisible(false);
		}
		
		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_finType.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_finType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finTypeDesc.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_finTypeDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finCcy.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_finCcy.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finDaysCalType.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_finDaysCalType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finAcType.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_finAcType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finContingentAcType.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_finContingentAcType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finIsGenRef.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_finIsGenRef.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finIsOpenNewFinAc.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_finIsOpenNewFinAc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finSchdMthd.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_finSchdMthd.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finHistRetension.setModel(new ListModelList(new SearchOperators().getNumericOperators()));
		this.sortOperator_finHistRetension.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finRateType.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_finRateType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finBaseRate.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_finBaseRate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finSplRate.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_finSplRate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finIntRate.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_finIntRate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_fInMinRate.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_fInMinRate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finGrcRateType.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_finGrcRateType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finGrcBaseRate.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_finGrcBaseRate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finGrcSplRate.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_finGrcSplRate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finGrcIntRate.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_finGrcIntRate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_fInGrcMinRate.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_fInGrcMinRate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finGrcMaxRate.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_finGrcMaxRate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finMinTerm.setModel(new ListModelList(new SearchOperators().getNumericOperators()));
		this.sortOperator_finMinTerm.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finMaxTerm.setModel(new ListModelList(new SearchOperators().getNumericOperators()));
		this.sortOperator_finMaxTerm.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finDftTerms.setModel(new ListModelList(new SearchOperators().getNumericOperators()));
		this.sortOperator_finDftTerms.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_fInRepayMethod.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_fInRepayMethod.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finIsAlwPartialRpy.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_finIsAlwPartialRpy.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finIsAlwDifferment.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_finIsAlwDifferment.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finIsAlwEarlyRpy.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_finIsAlwEarlyRpy.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finIsAlwEarlySettle.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_finIsAlwEarlySettle.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finODRpyTries.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_finODRpyTries.setItemRenderer(new SearchOperatorListModelItemRenderer());

//		this.sortOperator_finLatePayRule.setModel(new ListModelList(new SearchOperators().getStringOperators()));
//		this.sortOperator_finLatePayRule.setItemRenderer(new SearchOperatorListModelItemRenderer());
//
//		this.sortOperator_finEarlyPayRule.setModel(new ListModelList(new SearchOperators().getStringOperators()));
//		this.sortOperator_finEarlyPayRule.setItemRenderer(new SearchOperatorListModelItemRenderer());
//
//		this.sortOperator_finEarlySettleRule.setModel(new ListModelList(new SearchOperators().getStringOperators()));
//		this.sortOperator_finEarlySettleRule.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finIsActive.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_finIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType = PennantAppUtil.setRecordType(this.recordType);
		} else {
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_FinanceTypeSearch_RecordStatus.setVisible(false);
			this.label_FinanceTypeSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<FinanceType> searchObj = (JdbcSearchObject<FinanceType>) args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("finType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finType, filter);
					this.finType.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("finTypeDesc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finTypeDesc, filter);
					this.finTypeDesc.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("finCcy")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finCcy, filter);
					this.finCcy.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("finDaysCalType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finDaysCalType, filter);
					this.finDaysCalType.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("finAcType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finAcType, filter);
					this.finAcType.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("finContingentAcType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finContingentAcType, filter);
					this.finContingentAcType.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("finIsGenRef")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finIsGenRef, filter);
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.finIsGenRef.setChecked(true);
					}else{
						this.finIsGenRef.setChecked(false);
					}
				} else if (filter.getProperty().equals("finIsOpenNewFinAc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finIsOpenNewFinAc, filter);
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.finIsOpenNewFinAc.setChecked(true);
					}else{
						this.finIsOpenNewFinAc.setChecked(false);
					}
				} else if (filter.getProperty().equals("finSchdMthd")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finSchdMthd, filter);
					this.finSchdMthd.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("finHistRetension")) {
					SearchOperators.restoreNumericOperator(this.sortOperator_finHistRetension, filter);
					this.finHistRetension.setValue(Integer.parseInt(filter.getValue().toString()));
				} else if (filter.getProperty().equals("finRateType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finRateType, filter);
					this.finRateType.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("finBaseRate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finBaseRate, filter);
					this.finBaseRate.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("finSplRate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finSplRate, filter);
					this.finSplRate.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("finIntRate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finIntRate, filter);
					this.finIntRate.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("fInMinRate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fInMinRate, filter);
					this.fInMinRate.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("finGrcRateType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finGrcRateType, filter);
					this.finGrcRateType.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("finGrcBaseRate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finGrcBaseRate, filter);
					this.finGrcBaseRate.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("finGrcSplRate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finGrcSplRate, filter);
					this.finGrcSplRate.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("finGrcIntRate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finGrcIntRate, filter);
					this.finGrcIntRate.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("fInGrcMinRate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fInGrcMinRate, filter);
					this.fInGrcMinRate.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("finGrcMaxRate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finGrcMaxRate, filter);
					this.finGrcMaxRate.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("finMinTerm")) {
					SearchOperators.restoreNumericOperator(this.sortOperator_finMinTerm, filter);
					this.finMinTerm.setValue(Integer.parseInt(filter.getValue().toString()));
				} else if (filter.getProperty().equals("finMaxTerm")) {
					SearchOperators.restoreNumericOperator(this.sortOperator_finMaxTerm, filter);
					this.finMaxTerm.setValue(Integer.parseInt(filter.getValue().toString()));
				} else if (filter.getProperty().equals("finDftTerms")) {
					SearchOperators.restoreNumericOperator(this.sortOperator_finDftTerms, filter);
					this.finDftTerms.setValue(Integer.parseInt(filter.getValue().toString()));
				} else if (filter.getProperty().equals("fInRepayMethod")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fInRepayMethod, filter);
					this.fInRepayMethod.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("finIsAlwPartialRpy")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finIsAlwPartialRpy, filter);
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.finIsAlwPartialRpy.setChecked(true);
					}else{
						this.finIsAlwPartialRpy.setChecked(false);
					}
				} else if (filter.getProperty().equals("finIsAlwDifferment")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finIsAlwDifferment, filter);
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.finIsAlwDifferment.setChecked(true);
					}else{
						this.finIsAlwDifferment.setChecked(false);
					}
				} else if (filter.getProperty().equals("finIsAlwEarlyRpy")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finIsAlwEarlyRpy, filter);
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.finIsAlwEarlyRpy.setChecked(true);
					}else{
						this.finIsAlwEarlyRpy.setChecked(false);
					}
				} else if (filter.getProperty().equals("finIsAlwEarlySettle")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finIsAlwEarlySettle, filter);
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.finIsAlwEarlySettle.setChecked(true);
					}else{
						this.finIsAlwEarlySettle.setChecked(false);
					}
				} else if (filter.getProperty().equals("finODRpyTries")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finODRpyTries, filter);
					this.finODRpyTries.setValue(filter.getValue().toString());
				} else 
//					if (filter.getProperty().equals("finLatePayRule")) {
//					SearchOperators.restoreStringOperator(this.sortOperator_finLatePayRule, filter);
//					this.finLatePayRule.setValue(filter.getValue().toString());
//				} else if (filter.getProperty().equals("finEarlyPayRule")) {
//					SearchOperators.restoreStringOperator(this.sortOperator_finEarlyPayRule, filter);
//					this.finEarlyPayRule.setValue(filter.getValue().toString());
//				} else if (filter.getProperty().equals("finEarlySettleRule")) {
//					SearchOperators.restoreStringOperator(this.sortOperator_finEarlySettleRule, filter);
//					this.finEarlySettleRule.setValue(filter.getValue().toString());
//				} else 
					if (filter.getProperty().equals("finIsActive")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finIsActive, filter);
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.finIsActive.setChecked(true);
					}else{
						this.finIsActive.setChecked(false);
					}
				} else if (filter.getProperty().equals("recordStatus")) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordStatus, filter);
					this.recordStatus.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("recordType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordType, filter);
					for (int i = 0; i < this.recordType.getItemCount(); i++) {
						if (this.recordType.getItemAtIndex(i).getValue().equals(filter.getValue().toString())) {
							this.recordType.setSelectedIndex(i);
						}
					}
				}
			}
		}
		showFinanceTypeSeekDialog();
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
	public void onClick$btnSearch(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * closes the dialog window
	 */
	private void doClose() {
		logger.debug("Entering");
		this.window_FinanceTypeSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showFinanceTypeSeekDialog() throws InterruptedException {
		logger.debug("Entering");

		try {
			// open the dialog in modal mode
			this.window_FinanceTypeSearch.doModal();
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
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
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void doSearch() throws Exception {
		logger.debug("Entering");

		final JdbcSearchObject<FinanceType> so = new JdbcSearchObject<FinanceType>(FinanceType.class);
		so.addTabelName("RMTFinanceTypes_View");

		if (isWorkFlowEnabled()) {
			if(object.getClass().isInstance(commodityFinanceTypeListCtrl)) { 
				so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
				so.addFilters(new Filter("finCategory", "CF", Filter.OP_EQUAL));
			} else {
				so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
				so.addFilters(new Filter("finCategory", "CF", Filter.OP_NOT_EQUAL));
			}
		}

		if (StringUtils.isNotEmpty(this.finType.getValue())) {

			// get the search operator
			final Listitem item_FinType = this.sortOperator_finType.getSelectedItem();

			if (item_FinType != null) {
				final int searchOpId = ((SearchOperators) item_FinType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finType", "%"+ this.finType.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finType", this.finType.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.finTypeDesc.getValue())) {

			// get the search operator
			final Listitem item_FinTypeDesc = this.sortOperator_finTypeDesc.getSelectedItem();

			if (item_FinTypeDesc != null) {
				final int searchOpId = ((SearchOperators) item_FinTypeDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finTypeDesc", "%"+ this.finTypeDesc.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finTypeDesc", this.finTypeDesc.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.finCcy.getValue())) {

			// get the search operator
			final Listitem item_FinCcy = this.sortOperator_finCcy.getSelectedItem();

			if (item_FinCcy != null) {
				final int searchOpId = ((SearchOperators) item_FinCcy.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finCcy", "%"+ this.finCcy.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finCcy", this.finCcy.getValue(),searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.finDaysCalType.getValue())) {

			// get the search operator
			final Listitem item_FinDaysCalType = this.sortOperator_finDaysCalType.getSelectedItem();

			if (item_FinDaysCalType != null) {
				final int searchOpId = ((SearchOperators) item_FinDaysCalType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finDaysCalType", "%"+ this.finDaysCalType.getValue().toUpperCase()+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finDaysCalType",this.finDaysCalType.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.finAcType.getValue())) {

			// get the search operator
			final Listitem item_FinAcType = this.sortOperator_finAcType.getSelectedItem();

			if (item_FinAcType != null) {
				final int searchOpId = ((SearchOperators) item_FinAcType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finAcType", "%"+ this.finAcType.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finAcType", this.finAcType.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.finContingentAcType.getValue())) {

			// get the search operator
			final Listitem item_FinContingentAcType = this.sortOperator_finContingentAcType.getSelectedItem();

			if (item_FinContingentAcType != null) {
				final int searchOpId = ((SearchOperators) item_FinContingentAcType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finContingentAcType", "%"+ this.finContingentAcType.getValue().toUpperCase()+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finContingentAcType",this.finContingentAcType.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_FinIsGenRef = this.sortOperator_finIsGenRef.getSelectedItem();

		if (item_FinIsGenRef != null) {
			final int searchOpId = ((SearchOperators) item_FinIsGenRef.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.finIsGenRef.isChecked()) {
					so.addFilter(new Filter("finIsGenRef", 1, searchOpId));
				} else {
					so.addFilter(new Filter("finIsGenRef", 0, searchOpId));
				}
			}
		}

		// get the search operator
		final Listitem item_FinIsOpenNewFinAc = this.sortOperator_finIsOpenNewFinAc.getSelectedItem();

		if (item_FinIsOpenNewFinAc != null) {
			final int searchOpId = ((SearchOperators) item_FinIsOpenNewFinAc.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.finIsOpenNewFinAc.isChecked()) {
					so.addFilter(new Filter("finIsOpenNewFinAc", 1, searchOpId));
				} else {
					so.addFilter(new Filter("finIsOpenNewFinAc", 0, searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.finSchdMthd.getValue())) {

			// get the search operator
			final Listitem item_FinSchdMthd = this.sortOperator_finSchdMthd.getSelectedItem();

			if (item_FinSchdMthd != null) {
				final int searchOpId = ((SearchOperators) item_FinSchdMthd.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finSchdMthd", "%"+ this.finSchdMthd.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finSchdMthd", this.finSchdMthd.getValue(), searchOpId));
				}
			}
		}
		if (this.finHistRetension.getValue()!=null) {

			// get the search operator
			final Listitem item_FinHistRetension = this.sortOperator_finHistRetension.getSelectedItem();

			if (item_FinHistRetension != null) {
				final int searchOpId = ((SearchOperators) item_FinHistRetension.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finHistRetension",this.finHistRetension.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.finRateType.getValue())) {

			// get the search operator
			final Listitem item_FinRateType = this.sortOperator_finRateType.getSelectedItem();

			if (item_FinRateType != null) {
				final int searchOpId = ((SearchOperators) item_FinRateType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finRateType", "%"+ this.finRateType.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finRateType", this.finRateType.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.finBaseRate.getValue())) {

			// get the search operator
			final Listitem item_FinBaseRate = this.sortOperator_finBaseRate.getSelectedItem();

			if (item_FinBaseRate != null) {
				final int searchOpId = ((SearchOperators) item_FinBaseRate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finBaseRate", "%"+ this.finBaseRate.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finBaseRate", this.finBaseRate.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.finSplRate.getValue())) {

			// get the search operator
			final Listitem item_FinSplRate = this.sortOperator_finSplRate.getSelectedItem();

			if (item_FinSplRate != null) {
				final int searchOpId = ((SearchOperators) item_FinSplRate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finSplRate", "%"+ this.finSplRate.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finSplRate", this.finSplRate.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.finIntRate.getValue())) {

			// get the search operator
			final Listitem item_FinIntRate = this.sortOperator_finIntRate.getSelectedItem();

			if (item_FinIntRate != null) {
				final int searchOpId = ((SearchOperators) item_FinIntRate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finIntRate", "%"+ this.finIntRate.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finIntRate", this.finIntRate.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.fInMinRate.getValue())) {

			// get the search operator
			final Listitem item_FInMinRate = this.sortOperator_fInMinRate.getSelectedItem();

			if (item_FInMinRate != null) {
				final int searchOpId = ((SearchOperators) item_FInMinRate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fInMinRate", "%"+ this.fInMinRate.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fInMinRate", this.fInMinRate.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.finMaxRate.getValue())) {

			// get the search operator
			final Listitem item_FinMaxRate = this.sortOperator_finMaxRate.getSelectedItem();

			if (item_FinMaxRate != null) {
				final int searchOpId = ((SearchOperators) item_FinMaxRate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finMaxRate", "%"+ this.finMaxRate.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finMaxRate", this.finMaxRate.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.finGrcRateType.getValue())) {

			// get the search operator
			final Listitem item_FinGrcRateType = this.sortOperator_finGrcRateType.getSelectedItem();

			if (item_FinGrcRateType != null) {
				final int searchOpId = ((SearchOperators) item_FinGrcRateType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finGrcRateType", "%"+ this.finGrcRateType.getValue().toUpperCase()+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finGrcRateType",this.finGrcRateType.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.finGrcBaseRate.getValue())) {

			// get the search operator
			final Listitem item_FinGrcBaseRate = this.sortOperator_finGrcBaseRate.getSelectedItem();

			if (item_FinGrcBaseRate != null) {
				final int searchOpId = ((SearchOperators) item_FinGrcBaseRate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finGrcBaseRate", "%"+ this.finGrcBaseRate.getValue().toUpperCase()+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finGrcBaseRate",this.finGrcBaseRate.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.finGrcSplRate.getValue())) {

			// get the search operator
			final Listitem item_FinGrcSplRate = this.sortOperator_finGrcSplRate.getSelectedItem();

			if (item_FinGrcSplRate != null) {
				final int searchOpId = ((SearchOperators) item_FinGrcSplRate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finGrcSplRate","%" + this.finGrcSplRate.getValue().toUpperCase()+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finGrcSplRate", this.finGrcSplRate.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.finGrcIntRate.getValue())) {

			// get the search operator
			final Listitem item_FinGrcIntRate = this.sortOperator_finGrcIntRate.getSelectedItem();

			if (item_FinGrcIntRate != null) {
				final int searchOpId = ((SearchOperators) item_FinGrcIntRate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finGrcIntRate","%" + this.finGrcIntRate.getValue().toUpperCase()+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finGrcIntRate", this.finGrcIntRate.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.fInGrcMinRate.getValue())) {

			// get the search operator
			final Listitem item_FInGrcMinRate = this.sortOperator_fInGrcMinRate.getSelectedItem();

			if (item_FInGrcMinRate != null) {
				final int searchOpId = ((SearchOperators) item_FInGrcMinRate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fInGrcMinRate","%" + this.fInGrcMinRate.getValue().toUpperCase()+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fInGrcMinRate", this.fInGrcMinRate.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.finGrcMaxRate.getValue())) {

			// get the search operator
			final Listitem item_FinGrcMaxRate = this.sortOperator_finGrcMaxRate.getSelectedItem();

			if (item_FinGrcMaxRate != null) {
				final int searchOpId = ((SearchOperators) item_FinGrcMaxRate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finGrcMaxRate","%" + this.finGrcMaxRate.getValue().toUpperCase()+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finGrcMaxRate", this.finGrcMaxRate.getValue(), searchOpId));
				}
			}
		}
		if (this.finMinTerm.getValue()!=null) {

			// get the search operator
			final Listitem item_FinMinTerm = this.sortOperator_finMinTerm.getSelectedItem();

			if (item_FinMinTerm != null) {
				final int searchOpId = ((SearchOperators) item_FinMinTerm.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finMinTerm", this.finMinTerm.getValue(), searchOpId));
				}
			}
		}
		if (this.finMaxTerm.getValue()!=null) {

			// get the search operator
			final Listitem item_FinMaxTerm = this.sortOperator_finMaxTerm.getSelectedItem();

			if (item_FinMaxTerm != null) {
				final int searchOpId = ((SearchOperators) item_FinMaxTerm.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finMaxTerm", this.finMaxTerm.getValue(), searchOpId));
				}
			}
		}
		if (this.finDftTerms.getValue() != null) {

			// get the search operator
			final Listitem item_FinDftTerms = this.sortOperator_finDftTerms.getSelectedItem();

			if (item_FinDftTerms != null) {
				final int searchOpId = ((SearchOperators) item_FinDftTerms.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finDftTerms", this.finDftTerms.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.fInRepayMethod.getValue())) {

			// get the search operator
			final Listitem item_FInRepayMethod = this.sortOperator_fInRepayMethod.getSelectedItem();

			if (item_FInRepayMethod != null) {
				final int searchOpId = ((SearchOperators) item_FInRepayMethod.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fInRepayMethod", "%"+ this.fInRepayMethod.getValue().toUpperCase()+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fInRepayMethod",this.fInRepayMethod.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_FinIsAlwPartialRpy = this.sortOperator_finIsAlwPartialRpy.getSelectedItem();

		if (item_FinIsAlwPartialRpy != null) {
			final int searchOpId = ((SearchOperators) item_FinIsAlwPartialRpy.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.finIsAlwPartialRpy.isChecked()) {
					so.addFilter(new Filter("finIsAlwPartialRpy", 1, searchOpId));
				} else {
					so.addFilter(new Filter("finIsAlwPartialRpy", 0, searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_FinIsAlwDifferment = this.sortOperator_finIsAlwDifferment.getSelectedItem();

		if (item_FinIsAlwDifferment != null) {
			final int searchOpId = ((SearchOperators) item_FinIsAlwDifferment.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.finIsAlwDifferment.isChecked()) {
					so.addFilter(new Filter("finIsAlwDifferment", 1, searchOpId));
				} else {
					so.addFilter(new Filter("finIsAlwDifferment", 0, searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_FinIsAlwEarlyRpy = this.sortOperator_finIsAlwEarlyRpy.getSelectedItem();

		if (item_FinIsAlwEarlyRpy != null) {
			final int searchOpId = ((SearchOperators) item_FinIsAlwEarlyRpy.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.finIsAlwEarlyRpy.isChecked()) {
					so.addFilter(new Filter("finIsAlwEarlyRpy", 1, searchOpId));
				} else {
					so.addFilter(new Filter("finIsAlwEarlyRpy", 0, searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_FinIsAlwEarlySettle = this.sortOperator_finIsAlwEarlySettle.getSelectedItem();

		if (item_FinIsAlwEarlySettle != null) {
			final int searchOpId = ((SearchOperators) item_FinIsAlwEarlySettle.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.finIsAlwEarlySettle.isChecked()) {
					so.addFilter(new Filter("finIsAlwEarlySettle", 1,searchOpId));
				} else {
					so.addFilter(new Filter("finIsAlwEarlySettle", 0,searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.finODRpyTries.getValue())) {

			// get the search operator
			final Listitem item_FinODRpyTries = this.sortOperator_finODRpyTries.getSelectedItem();

			if (item_FinODRpyTries != null) {
				final int searchOpId = ((SearchOperators) item_FinODRpyTries.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finODRpyTries","%" + this.finODRpyTries.getValue().toUpperCase()+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finODRpyTries", this.finODRpyTries.getValue(), searchOpId));
				}
			}
		}
//		if (StringUtils.isNotEmpty(this.finLatePayRule.getValue())) {
//
//			// get the search operator
//			final Listitem item_FinLatePayRule = this.sortOperator_finLatePayRule.getSelectedItem();
//
//			if (item_FinLatePayRule != null) {
//				final int searchOpId = ((SearchOperators) item_FinLatePayRule.getAttribute("data")).getSearchOperatorId();
//
//				if (searchOpId == Filter.OP_LIKE) {
//					so.addFilter(new Filter("finLatePayRule", "%"+ this.finLatePayRule.getValue().toUpperCase()+ "%", searchOpId));
//				} else if (searchOpId == -1) {
//					// do nothing
//				} else {
//					so.addFilter(new Filter("finLatePayRule",this.finLatePayRule.getValue(), searchOpId));
//				}
//			}
//		}
//		if (StringUtils.isNotEmpty(this.finEarlyPayRule.getValue())) {
//
//			// get the search operator
//			final Listitem item_FinEarlyPayRule = this.sortOperator_finEarlyPayRule.getSelectedItem();
//
//			if (item_FinEarlyPayRule != null) {
//				final int searchOpId = ((SearchOperators) item_FinEarlyPayRule.getAttribute("data")).getSearchOperatorId();
//
//				if (searchOpId == Filter.OP_LIKE) {
//					so.addFilter(new Filter("finEarlyPayRule", "%"+ this.finEarlyPayRule.getValue().toUpperCase()+ "%", searchOpId));
//				} else if (searchOpId == -1) {
//					// do nothing
//				} else {
//					so.addFilter(new Filter("finEarlyPayRule",this.finEarlyPayRule.getValue(), searchOpId));
//				}
//			}
//		}
//		if (StringUtils.isNotEmpty(this.finEarlySettleRule.getValue())) {
//
//			// get the search operator
//			final Listitem item_FinEarlySettleRule = this.sortOperator_finEarlySettleRule.getSelectedItem();
//
//			if (item_FinEarlySettleRule != null) {
//				final int searchOpId = ((SearchOperators) item_FinEarlySettleRule.getAttribute("data")).getSearchOperatorId();
//
//				if (searchOpId == Filter.OP_LIKE) {
//					so.addFilter(new Filter("finEarlySettleRule", "%"+ this.finEarlySettleRule.getValue().toUpperCase()+ "%", searchOpId));
//				} else if (searchOpId == -1) {
//					// do nothing
//				} else {
//					so.addFilter(new Filter("finEarlySettleRule",this.finEarlySettleRule.getValue(), searchOpId));
//				}
//			}
//		}
		// get the search operator
		final Listitem item_FinIsActive = this.sortOperator_finIsActive.getSelectedItem();

		if (item_FinIsActive != null) {
			final int searchOpId = ((SearchOperators) item_FinIsActive.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.finIsActive.isChecked()) {
					so.addFilter(new Filter("finIsActive", 1, searchOpId));
				} else {
					so.addFilter(new Filter("finIsActive", 0, searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem item_RecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (item_RecordStatus != null) {
				final int searchOpId = ((SearchOperators) item_RecordStatus.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%"+ this.recordStatus.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordStatus", this.recordStatus.getValue(), searchOpId));
				}
			}
		}

		String selectedValue = "";
		if (this.recordType.getSelectedItem() != null) {
			selectedValue = this.recordType.getSelectedItem().getValue().toString();
		}

		if (StringUtils.isNotEmpty(selectedValue)) {
			// get the search operator
			final Listitem item_RecordType = this.sortOperator_recordType.getSelectedItem();
			if (item_RecordType != null) {
				final int searchOpId = ((SearchOperators) item_RecordType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordType", "%"+ selectedValue.toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordType", selectedValue,searchOpId));
				}
			}
		}
		// Default Sort on the table
		so.addSort("FinType", false);

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = so.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / " + filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}

		/*Here object is overHanded parameter .object can be instance of FinanceTypeListCtrl
	        *,CommodityFinanceTypeListCtrl*/
			/* store the searchObject for reReading */
		// set the model to the listBox with the initial result set get by the DAO method.
		
		final SearchResult<FinanceType> searchResult = this.financeTypeListCtrl.getPagedListService().getSRBySearchObject(so);
		this.financeTypeListCtrl.listBoxFinanceType.setModel(new GroupsModelArray(searchResult.getResult().toArray(),
				new FinanceTypeComparator()));
		this.financeTypeListCtrl.listBoxFinanceType.setItemRenderer(new FinanceTypeListModelItemRenderer());
		
		object.getClass().getMethod("setSearchObj"
				,Class.forName( "com.pennant.backend.util.JdbcSearchObject" )).invoke(object, so);
		this.label_FinanceTypeSearchResult.setValue(
				Labels.getLabel("label_FinanceTypeSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
		
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setFinanceTypeService(FinanceTypeService financeTypeService) {
		this.financeTypeService = financeTypeService;
	}
	public FinanceTypeService getFinanceTypeService() {
		return this.financeTypeService;
	}

	public void setCommodityFinanceTypeListCtrl(
			CommodityFinanceTypeListCtrl commodityFinanceTypeListCtrl) {
		this.commodityFinanceTypeListCtrl = commodityFinanceTypeListCtrl;
	}
	public CommodityFinanceTypeListCtrl getCommodityFinanceTypeListCtrl() {
		return commodityFinanceTypeListCtrl;
	}
}