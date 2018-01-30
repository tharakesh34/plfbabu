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
 *                                 FILE HEADER                                               
 ********************************************************************************************
 *																							*
 * FileName    		:  BulkRateChangeDialogCtrl.java  										*                         
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-06-2011    														*
 *                                                                  						*
 * Modified Date    :  03-06-2011    														*
 *                                                                  					    *
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-06-2011       Pennant	                 0.1                                         	* 
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
package com.pennant.webui.financemanagement.bulkratechange;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.QueryBuilder;
import com.pennant.app.bulkratechange.BulkRateChangeProcess;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.applicationmaster.Query;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.BulkRateChangeDetails;
import com.pennant.backend.model.finance.BulkRateChangeHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.finance.BulkRateChangeProcessService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.ReportGenerationUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.enquiry.model.BulkRateChangeDialogModelItemRenderer;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;
import com.rits.cloning.Cloner;

/**
 * This is the controller class for the 
 * WEB-INF/pages/FinanceManagement/SchdlRepayment/SchdlRepaymentDialog.zul
 */
public class BulkRateChangeDialogCtrl extends GFCBaseCtrl<BulkRateChangeDetails> {
	private static final long serialVersionUID = 966281186831332116L;
	private static final Logger logger = Logger.getLogger(BulkRateChangeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_BulkRateChangeDialog; 		
	protected Borderlayout borderlayout_BulkRateChange;
	protected Grid grid_BulkRateChange;
	protected Tab tab_FinancesList; 
	protected Tab tab_RateChangeFinList; 
	protected Tab   tab_Query;							 
	protected Tabpanel tabpanel_Query;              	 
	protected Listbox listBox_FinancesList;	    
	protected Listbox listBox_RateChangeFinList;
	protected ExtendedCombobox finType;
	protected Label  label_BulkRateChangeDialog_SchFromDate;
	protected Datebox schFromDate;
	protected Space space_SchFromDate;
	protected Datebox toDate;
	protected Space space_ToDate;
	protected ExtendedCombobox ruleType;			     
	protected Hbox hbox_ruleType;			     
	protected Label label_BulkRateChangeDialog_RuleType;			     
	protected QueryBuilder rule;
	protected Row  row_ToDate;
	protected Row  row_RuleType;

	protected Paging  paging_FinancesList;
	protected Paging  paging_RateChangeFinList;

	protected Button btnSearch; 
	protected Button addFinList; 
	protected Button removeFinList; 
	protected Button finFilter; 
	protected Button finRefresh; 
	protected Button rateChangeRefresh; 
	protected Button rateChangeFinFilter; 
	protected Button btnApplyRateChange; 	
	
	protected Label 		label_Reference; 
	protected Label 		label_ReCalType; 
	protected Decimalbox 		rateChange; 

	//private FinanceDetailService financeDetailService;
	private BulkRateChangeListCtrl bulkRateChangeListCtrl ;
	private BulkRateChangeProcessService bulkRateChangeProcessService;
	private FinanceDetailService  	financeDetailService;

	private PagedListWrapper<BulkRateChangeDetails> finListWrapper;
	private PagedListWrapper<BulkRateChangeDetails> rateChangeFinListWrapper;
	protected JdbcSearchObject<BulkRateChangeDetails> searchObj;

	private List<BulkRateChangeDetails> oldRateChangeDetails_Fin = new ArrayList<BulkRateChangeDetails>();
	private List<BulkRateChangeDetails> oldRateChangeDetails_RateChangeFin = new ArrayList<BulkRateChangeDetails>();
	private List<BulkRateChangeDetails> oldBulkRateChangeDetails = null;
	List<BulkRateChangeDetails> financesList = new ArrayList<BulkRateChangeDetails>();
	List<BulkRateChangeDetails> rateChangeFinList = new ArrayList<BulkRateChangeDetails>();

	protected boolean 	recSave = false;
	private boolean 	isApplyRateChangeWin = false;
	private boolean 	isNewFinList = false;

	private BulkRateChangeHeader bulkRateChangeHeader;

	Date appStartDate = DateUtility.getAppDate();
	Date endDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");

	/**
	 * default constructor.<br>
	 */
	public BulkRateChangeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "BulkRateChange";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Rule object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_BulkRateChangeDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_BulkRateChangeDialog);

		try {
			if (arguments.containsKey("bulkRateChangeHeader")) {
				this.bulkRateChangeHeader = (BulkRateChangeHeader) arguments.get("bulkRateChangeHeader");
				BulkRateChangeHeader befImage = new BulkRateChangeHeader();
				BeanUtils.copyProperties(this.bulkRateChangeHeader, befImage);
				this.bulkRateChangeHeader.setBefImage(befImage);
				setBulkRateChangeHeader(this.bulkRateChangeHeader);
			} else {
				setBulkRateChangeHeader(null);
			}

			doLoadWorkFlow(this.bulkRateChangeHeader.isWorkflow(),	this.bulkRateChangeHeader.getWorkflowId(),	this.bulkRateChangeHeader.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "BulkRateChange");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("bulkRateChangeListCtrl")) {
				setBulkRateChangeListCtrl((BulkRateChangeListCtrl) arguments.get("bulkRateChangeListCtrl"));
			} else {
				setBulkRateChangeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();

			doShowDialog(getBulkRateChangeHeader());

			this.listBox_RateChangeFinList.setHeight(Integer.parseInt(getListBoxHeight(grid_BulkRateChange.getRows().getVisibleItemCount()).substring(0, 
					getListBoxHeight(grid_BulkRateChange.getRows().getVisibleItemCount()).indexOf("px"))) - 50 + "px");
			this.paging_RateChangeFinList.setPageSize(Integer.parseInt(getListBoxHeight(grid_BulkRateChange.getRows().getVisibleItemCount()).substring(0, 
					getListBoxHeight(grid_BulkRateChange.getRows().getVisibleItemCount()).indexOf("px"))) - 50);

			//createListHeaders(this.listBox_FinancesList);
			//createListHeaders(this.listBox_SelectedFinList);

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_BulkRateChangeDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	public void doCheckRights(){
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities("BulkRateChange", getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_BulkRateChange_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_BulkRateChange_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_BulkRateChange_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_BulkRateChange_btnSave"));
		this.btnApplyRateChange.setVisible(getUserWorkspace().isAllowed("button_BulkRateChange_btnApplyRateChange"));
		this.addFinList.setVisible(getUserWorkspace().isAllowed("button_BulkRateChange_btnAddFinList"));
		this.removeFinList.setVisible(getUserWorkspace().isAllowed("button_BulkRateChange_btnRemoveFinList"));
		this.btnSearch.setVisible(getUserWorkspace().isAllowed("button_BulkRateChange_btnSearch"));
		
		logger.debug("Leaving");
	}

	/**
	 * 
	 * @param event
	 */
	public void onChange$ruleType(Event event){
		logger.debug("Entering "+event);
		event.getData();
		logger.debug("Entering "+event);
	}

	/**
	 * When user clicks on button "SearchRuleType" button
	 * @param event
	 */
	public void onFulfill$ruleType(Event event) {
		logger.debug("Entering " + event.toString());

		this.tab_Query.setSelected(true);
		Object dataObject = this.ruleType.getObject();
		if (dataObject instanceof String) {
			this.ruleType.setValue(dataObject.toString());
			this.ruleType.setDescription("");
			this.rule.setSqlQuery("");
		} else {
			/* Set FinanceWorkFlow object */
			Query details = (Query) dataObject;
			if (details != null) {
				this.ruleType.setValue(details.getQueryCode());
				this.ruleType.setDescription(details.getQueryDesc());
				this.rule.setSqlQuery(details.getSQLQuery());
			}
		}

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aBulkRateChangeHeader
	 * @throws Exception
	 */

	public void doShowDialog(BulkRateChangeHeader aBulkRateChangeHeader) throws Exception {
		logger.debug("Entering");

		// arguments for a given entity, so we get a new Object().
		if (aBulkRateChangeHeader == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.

			aBulkRateChangeHeader = getBulkRateChangeProcessService().getNewBulkRateChangeHeader();

			setBulkRateChangeHeader(aBulkRateChangeHeader);
		} else {
			setBulkRateChangeHeader(aBulkRateChangeHeader);
		}

		// set Read only mode accordingly if the object is new or not.
		if (aBulkRateChangeHeader.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.finType.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.finType.focus();
				if (StringUtils.isNotBlank(aBulkRateChangeHeader.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aBulkRateChangeHeader);

			setDialog(DialogType.EMBEDDED);

		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_BulkRateChangeDialog.onClose();
		} catch (Exception e) {
			throw e;
		}

		logger.debug("Leaving");
	}

	/*
	 *  Writing Bean Values to Components
	 */
	public void doWriteBeanToComponents(BulkRateChangeHeader aBulkRateChangeHeader){
		logger.debug("Entering");

		//Finance Type
		this.finType.setValue(aBulkRateChangeHeader.getFinType());
		this.finType.setDescription(StringUtils.isBlank(aBulkRateChangeHeader.getLovDescFinTypeDesc()) ? "" : aBulkRateChangeHeader.getLovDescFinTypeDesc());

		//Schedule Date From
		if(aBulkRateChangeHeader.getFromDate() != null){
			this.schFromDate.setValue(DateUtility.getDBDate(DateUtility.formatDate(aBulkRateChangeHeader.getFromDate(), PennantConstants.DBDateFormat)));
		} else {
			this.schFromDate.setText("");
		}

		//To Date
		if(aBulkRateChangeHeader.getToDate() != null){
			this.toDate.setValue(DateUtility.getDBDate(DateUtility.formatDate(aBulkRateChangeHeader.getToDate(), PennantConstants.DBDateFormat)));
		} else {
			this.toDate.setText("");
		}

		//Rule
		this.ruleType.setValue(aBulkRateChangeHeader.getRuleType());
		this.ruleType.setDescription(aBulkRateChangeHeader.getLovDescQueryDesc());
		this.rule.setSqlQuery(aBulkRateChangeHeader.getLovDescSqlQuery());

		//Fill Rate Change Details
		fillRateChangeDetails(aBulkRateChangeHeader);

		//Finance List and Rate Change Finances List
		if(aBulkRateChangeHeader.getBulkRateChangeDetailsList() != null) {
			getBulkRateChangeHeader().setBulkRateChangeDetailsList(aBulkRateChangeHeader.getBulkRateChangeDetailsList());

			this.oldBulkRateChangeDetails = aBulkRateChangeHeader.getBulkRateChangeDetailsList(); //TODO

			this.rateChangeFinList.addAll(aBulkRateChangeHeader.getBulkRateChangeDetailsList());
			String whereClause = getWhereClause(); // Build The Where Clause to get the Finances List
			this.financesList.addAll(getBulkRateChangeProcessService().getBulkRateChangeFinList(aBulkRateChangeHeader.getFinType(), aBulkRateChangeHeader.getFromDate(), whereClause));

			this.oldRateChangeDetails_Fin.addAll(this.financesList);
			this.oldRateChangeDetails_RateChangeFin.addAll(this.rateChangeFinList);

			getFinListWrapper().initList(this.financesList, this.listBox_FinancesList, this.paging_FinancesList);
			this.listBox_FinancesList.setItemRenderer(new BulkRateChangeDialogModelItemRenderer());

			getRateChangeFinListWrapper().initList(this.rateChangeFinList, this.listBox_RateChangeFinList, this.paging_RateChangeFinList);
			this.listBox_RateChangeFinList.setItemRenderer(new BulkRateChangeDialogModelItemRenderer());

		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getBulkRateChangeHeader().isNewRecord()) {
			listButtonsVisibility(false);
		} else {
			this.tab_RateChangeFinList.setSelected(true);
		}

		if(!this.row_RuleType.isVisible()) {
			this.tab_FinancesList.setSelected(true);
			this.tabpanel_Query.setVisible(false);
			this.tab_Query.setVisible(false);
		} else {
			this.rule.setEditable(false);
		}

		this.finType.setReadonly(isReadOnly("BulkRateChange_finType"));
		this.finType.setMandatoryStyle(!isReadOnly("BulkRateChange_finType"));
		this.ruleType.setReadonly(isReadOnly("BulkRateChange_ruleType"));
		this.ruleType.setMandatoryStyle(!isReadOnly("BulkRateChange_ruleType"));
		readOnlyComponent(isReadOnly("BulkRateChange_fromDate"), this.schFromDate);
		readOnlyComponent(isReadOnly("BulkRateChange_toDate"), this.toDate);
		this.listBox_FinancesList.setCheckmark(!isReadOnly("BulkRateChange_ListBoxCheckmark"));
		this.listBox_RateChangeFinList.setCheckmark(!isReadOnly("BulkRateChange_ListBoxCheckmark"));

		if (!isReadOnly("BulkRateChange_fromDate")) {
			space_SchFromDate.setSclass("mandatory");
		} else {
			space_SchFromDate.setSclass("");
		}

		if (!isReadOnly("BulkRateChange_toDate")) {
			space_ToDate.setSclass("mandatory");
		} else {
			space_ToDate.setSclass("");
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.bulkRateChangeHeader.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			//btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.finType.setReadonly(true);
		this.ruleType.setReadonly(true);
		readOnlyComponent(true, this.schFromDate);
		readOnlyComponent(true, this.toDate);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}
		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param BulkRateChangeHeader
	 */
	public void doWriteComponentsToBean(BulkRateChangeHeader aBulkRateChangeHeader) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (StringUtils.isBlank(aBulkRateChangeHeader.getBulkRateChangeRef())) {
				aBulkRateChangeHeader.setBulkRateChangeRef(getBulkRateChangeProcessService().getBulkRateChangeReference());
			} 

		} catch (WrongValueException we) {
			wve.add(we);
		}

		//Finance Type
		try {
			aBulkRateChangeHeader.setFinType(this.finType.getValue());
			aBulkRateChangeHeader.setLovDescFinTypeDesc(this.finType.getDescription());
		} catch (WrongValueException we ) {
			wve.add(we);
		}

		//Schedule Date From
		try {
			aBulkRateChangeHeader.setFromDate(DateUtility.getDBDate(DateUtility.formatDate(this.schFromDate.getValue(), PennantConstants.DBDateFormat)));
		} catch (WrongValueException we ) {
			wve.add(we);
		}

		//To Date
		try {
			if(this.row_ToDate.isVisible()){
				aBulkRateChangeHeader.setToDate(DateUtility.getDBDate(DateUtility.formatDate(this.toDate.getValue(), PennantConstants.DBDateFormat)));
			}
		} catch (WrongValueException we ) {
			wve.add(we);
		}

		//Rule Type
		try {
			if(this.row_RuleType.isVisible()) {
				aBulkRateChangeHeader.setRuleType(this.ruleType.getValidatedValue());
			}
		} catch (WrongValueException we ) {
			wve.add(we);
		}

		//Rate Change Finances List
		try {
			aBulkRateChangeHeader.setBulkRateChangeDetailsList(this.rateChangeFinList);
			doSetFinancesForBulkRateChangeProcess(aBulkRateChangeHeader); 

			if(aBulkRateChangeHeader.getBulkRateChangeDetailsList() != null && aBulkRateChangeHeader.getBulkRateChangeDetailsList().size() > 0){
				if (aBulkRateChangeHeader.isNewRecord()) {
					for (BulkRateChangeDetails bulkRateChangeDetail : aBulkRateChangeHeader.getBulkRateChangeDetailsList()) {
						bulkRateChangeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						bulkRateChangeDetail.setRecordStatus(PennantConstants.RCD_ADD);
						bulkRateChangeDetail.setWorkflowId(aBulkRateChangeHeader.getWorkflowId());
						bulkRateChangeDetail.setNewRecord(true);
					}
				}
			}
		} catch (WrongValueException we ) {
			wve.add(we);
		}

		/*try {
			if(this.oldRateChangeDetails_RateChangeFin != null && this.oldRateChangeDetails_RateChangeFin.equals(rateChangeFinList)){
				aBulkRateChangeHeader.setLovDescIsOlddataChanged(false);
			} else {
				aBulkRateChangeHeader.setLovDescIsOlddataChanged(true);
			}
		} catch (WrongValueException we ) {
			wve.add(we);
		}*/

		doRemoveValidation();

		if (!wve.isEmpty()) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	/**
	 * 
	 * @param aBulkRateChangeHeader
	 */
	public void doSetFinancesForBulkRateChangeProcess(BulkRateChangeHeader aBulkRateChangeHeader){
		logger.debug("Entering");

		/*if(this.listBox_FinancesList.getItems() != null){
			for(Listitem listitem : this.listBox_FinancesList.getItems()){
				BulkRateChangeDetails selectedBulkRateChangeDetails = null;
				selectedBulkRateChangeDetails = (BulkRateChangeDetails) listitem.getAttribute("data");
				for(BulkRateChangeDetails rateChangeDetails : aBulkRateChangeHeader.getBulkRateChangeDetailsList()){
					if(selectedBulkRateChangeDetails != null && 
							selectedBulkRateChangeDetails.getFinReference().equals(rateChangeDetails.getFinReference())){
						rateChangeDetails.setAllowRateChange(false);
						break;
					} 
				}
			} 
		}*/

		if(this.listBox_RateChangeFinList.getItems() != null){
			for(Listitem listitem : this.listBox_RateChangeFinList.getItems()){
				BulkRateChangeDetails selectedBulkRateChangeDetails = null;
				selectedBulkRateChangeDetails = (BulkRateChangeDetails) listitem.getAttribute("data");
				for(BulkRateChangeDetails rateChangeDetails : aBulkRateChangeHeader.getBulkRateChangeDetailsList()){
					if(selectedBulkRateChangeDetails != null && 
							selectedBulkRateChangeDetails.getFinReference().equals(rateChangeDetails.getFinReference())){
						rateChangeDetails.setAllowRateChange(true);
						break;
					} 
				}
			} 
		}

		logger.debug("Leaving");
	}


	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes

		this.finType.setMaxlength(8);
		this.finType.setModuleName("FinanceType");
		this.finType.setValueColumn("FinType");
		this.finType.setDescColumn("FinTypeDesc");
		this.finType.setValidateColumns(new String[] { "FinType" });
		this.finType.setMandatoryStyle(true);

		this.schFromDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.toDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.label_BulkRateChangeDialog_RuleType.setVisible(true);
		this.hbox_ruleType.setVisible(true);
		this.ruleType.setVisible(true);
		this.rule.setEditable(false);

		this.ruleType.setMaxlength(15);
		this.ruleType.setTextBoxWidth(120);
		this.ruleType.setMandatoryStyle(true);
		this.ruleType.setModuleName("Query");
		this.ruleType.setValueColumn("QueryCode");
		this.ruleType.setDescColumn("QueryDesc");
		this.ruleType.setValidateColumns(new String[] { "QueryCode" });

		Filter filter[] = new Filter[1];
		filter[0] = new Filter("QueryModule", "BULKRATECHANGE_DETAILS", Filter.OP_EQUAL);
		this.ruleType.setFilters(filter);	

		this.rateChange.setMaxlength(13);
		this.rateChange.setFormat(PennantConstants.rateFormate9);
		this.rateChange.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.rateChange.setScale(9);

		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$btnSave(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		doSave();

		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$btnDelete(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving" + event.toString());
	}


	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.bulkRateChangeHeader.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Deletes a Academic object from database.<br>
	 * @throws Exception 
	 */
	private void doDelete() throws Exception {
		logger.debug("Entering");
		final BulkRateChangeHeader aBulkRateChangeHeader = new BulkRateChangeHeader();
		BeanUtils.copyProperties(getBulkRateChangeHeader(), aBulkRateChangeHeader);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
				"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aBulkRateChangeHeader.getFromDate();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aBulkRateChangeHeader.getRecordType())) {
				aBulkRateChangeHeader.setVersion(aBulkRateChangeHeader.getVersion() + 1);
				aBulkRateChangeHeader.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aBulkRateChangeHeader.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aBulkRateChangeHeader, tranType)) {
					refreshList();
					closeDialog();
				}
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving");
	}


	/**
	 * Saves the components to table. <br>
	 * @throws Exception 
	 */
	public void doSave() throws Exception {
		logger.debug("Entering");

		BulkRateChangeHeader aBulkRateChangeHeader = new BulkRateChangeHeader();
		Cloner cloner = new Cloner();
		aBulkRateChangeHeader = cloner.deepClone(getBulkRateChangeHeader());
		boolean isNew = false;

		recSave = false;
		if (this.userAction.getSelectedItem() != null){
			if (//this.userAction.getSelectedItem().getLabel().equalsIgnoreCase("Save") ||
					this.userAction.getSelectedItem().getLabel().equalsIgnoreCase("Cancel") ||
					this.userAction.getSelectedItem().getLabel().contains("Reject") ||
					this.userAction.getSelectedItem().getLabel().contains("Resubmit") ||
					this.userAction.getSelectedItem().getLabel().contains("Decline")) {
				recSave = true;
			}
		}

		if(isWorkFlowEnabled()){
			aBulkRateChangeHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aBulkRateChangeHeader.getNextTaskId(), aBulkRateChangeHeader);
		}

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if(!PennantConstants.RECORD_TYPE_DEL.equals(aBulkRateChangeHeader.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the ChequePurpose object with the components data
			doWriteComponentsToBean(aBulkRateChangeHeader);
		}

		// Do data level validations here
		if (!recSave && !doValidateData()) {
			return;
		}

		/*BulkRateChangeHeader tempaBulkRateChangeHeader = getBulkRateChangeProcessService().getBulkProcessHeaderByFromAndToDates(aBulkProcessHeader.getFromDate(), aBulkProcessHeader.getToDate(), "_Temp");
		if (tempaBulkRateChangeHeader != null) {
			if(!tempaBulkRateChangeHeader.getRecordStatus().equals(StringUtils.trimToEmpty(aBulkRateChangeHeader.getRecordStatus()))){
				if (isValidationrequired()) {
					final String msg = "Bulk Rate Change with Finance Type " + aBulkRateChangeHeader.getFinType() + " And Schedule Date above" + aBulkRateChangeHeader.getSchDateFrom() + " Already in process";
					try {
						MessageUtil.showErrorMessage(msg);
					} catch (InterruptedException e) {
						logger.error(e);
						return;
					}
					return;
				}
			}
		}
		tempaBulkRateChangeHeader = null;*/

		isNew = aBulkRateChangeHeader.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aBulkRateChangeHeader.getRecordType()).equals("")) {
				aBulkRateChangeHeader.setVersion(aBulkRateChangeHeader.getVersion() + 1);
				if (isNew) {
					aBulkRateChangeHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aBulkRateChangeHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aBulkRateChangeHeader.setNewRecord(true);
				}
			}

		} else {
			aBulkRateChangeHeader.setVersion(aBulkRateChangeHeader.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aBulkRateChangeHeader, tranType)) {
				// For Finance Maintenance
				if (getBulkRateChangeListCtrl() != null) {
					refreshList();
				}
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Validating the Date before Save 
	 * @return
	 * @throws InterruptedException
	 */
	private boolean doValidateData() throws InterruptedException {
		logger.debug("Entering");

		boolean proceed = true;
		try {
			if (this.listBox_FinancesList.getItems().isEmpty() && this.listBox_RateChangeFinList.getItems().isEmpty()) {

				proceed = false;
				this.tab_FinancesList.setSelected(true);
				MessageUtil.showError(Labels.getLabel("label_RateChange_FinanceList_Empty"));
			} else if (this.listBox_RateChangeFinList.getItems().isEmpty()) {

				proceed = false;
				this.tab_RateChangeFinList.setSelected(true);
				MessageUtil.showError(Labels.getLabel("label_RateChange_RateChangeFinList_Empty"));
			} else if (getBulkRateChangeHeader().getRateChange() == null || getBulkRateChangeHeader().getRateChange() == BigDecimal.ZERO) {

				proceed = false;
				this.tab_RateChangeFinList.setSelected(true);
				MessageUtil.showError(Labels.getLabel("label_RateChange_ApplyRateChange"));
			} else {
				/*if (isDataChanged(false)) {
					proceed = false;
					this.tab_FinancesList.setSelected(true);
					MessageUtil.showErrorMessage(Labels.getLabel("label_RateChange_FinanceList_Changed"));
				}*/
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
			proceed = false;
		}

		logger.debug("Leaving");
		return proceed;
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aBulkRateChangeHeader
	 *            (BulkRateChangeHeader)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * @throws Exception 
	 * 
	 */
	private boolean doProcess(BulkRateChangeHeader aBulkRateChangeHeader, String tranType) throws Exception {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aBulkRateChangeHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aBulkRateChangeHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aBulkRateChangeHeader.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aBulkRateChangeHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aBulkRateChangeHeader.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aBulkRateChangeHeader);
				}

				if (isNotesMandatory(taskId, aBulkRateChangeHeader)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}
			

			if (StringUtils.isNotBlank(nextTaskId)) {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}
			aBulkRateChangeHeader.setTaskId(taskId);
			aBulkRateChangeHeader.setNextTaskId(nextTaskId);
			aBulkRateChangeHeader.setRoleCode(getRole());
			aBulkRateChangeHeader.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aBulkRateChangeHeader, tranType);
			String operationRefs = getServiceOperations(taskId, aBulkRateChangeHeader);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aBulkRateChangeHeader, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aBulkRateChangeHeader, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}
			


	/**
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.bulkRateChangeHeader);
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 *            (AuditHeader)
	 * 
	 * @param method
	 *            (String)
	 * 
	 * @return boolean
	 * @throws Exception 
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws Exception {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		BulkRateChangeHeader aBulkRateChangeHeader = (BulkRateChangeHeader) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getBulkRateChangeProcessService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getBulkRateChangeProcessService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {

						auditHeader = getBulkRateChangeProcessService().doApprove(auditHeader);
						if (auditHeader.isNextProcess()) {
							doProcessBulkRateChangeUsingThread(auditHeader);
						}

						if (aBulkRateChangeHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getBulkRateChangeProcessService().doReject(auditHeader);

						if (aBulkRateChangeHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_BulkRateChangeDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_BulkRateChangeDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.bulkRateChangeHeader), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}

			setOverideMap(auditHeader.getOverideMap());
		} catch (InterruptedException e) {
			logger.error(e);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * 
	 * @param auditHeader
	 */
	private void doProcessBulkRateChangeUsingThread(AuditHeader auditHeader) {
		logger.debug("Entering");

		try {
			BulkRateChangeHeader aBulkRateChangeHeader = (BulkRateChangeHeader) auditHeader.getAuditDetail().getModelData();
			int	threadCount = 	SysParamUtil.getValueAsInt(PennantConstants.DFT_THREAD_COUNT);
			BulkRateChangeProcess bulkRateChangeProcess = new BulkRateChangeProcess();
			List<BulkRateChangeDetails> detailsList = new ArrayList<BulkRateChangeDetails>();
			List<BulkRateChangeDetails> detailsList_Thread = null;
			BulkRateChangeHeader bulkRateChangeHeader_Thread = null;

			AtomicLong success = new AtomicLong();
			AtomicLong failure = new AtomicLong();
			Cloner cloner = new Cloner();

			detailsList.addAll(aBulkRateChangeHeader.getBulkRateChangeDetailsList());

			if (aBulkRateChangeHeader.getBulkRateChangeDetailsList().size() < threadCount) {

				for (int i = 0; i < detailsList.size(); i++) {
					bulkRateChangeHeader_Thread = cloner.deepClone(aBulkRateChangeHeader);
					detailsList_Thread = new ArrayList<BulkRateChangeDetails>();

					detailsList_Thread.add(detailsList.get(i));

					bulkRateChangeHeader_Thread.setBulkRateChangeDetailsList(detailsList_Thread);
					bulkRateChangeProcess = new BulkRateChangeProcess(bulkRateChangeHeader_Thread, getBulkRateChangeProcessService(), success, failure, 
							aBulkRateChangeHeader.getBulkRateChangeDetailsList().size());
					bulkRateChangeProcess.start();
				}

			} else {

				int listSize = detailsList.size();
				for (int i = 0; i < threadCount; i++) {
					bulkRateChangeHeader_Thread = cloner.deepClone(aBulkRateChangeHeader);
					detailsList_Thread = new ArrayList<BulkRateChangeDetails>();

					for (int j = 0; j < listSize / threadCount; j++) {
						detailsList_Thread.add(detailsList.get(0));
						detailsList.remove(detailsList.get(0));
					}

					if ((listSize % threadCount) != 0 && !detailsList.isEmpty()) {
						detailsList_Thread.add(detailsList.get(0));
						detailsList.remove(detailsList.get(0));
					}

					bulkRateChangeHeader_Thread.setBulkRateChangeDetailsList(detailsList_Thread);
					bulkRateChangeProcess = new BulkRateChangeProcess(bulkRateChangeHeader_Thread, getBulkRateChangeProcessService(), success, failure, 
							aBulkRateChangeHeader.getBulkRateChangeDetailsList().size());
					bulkRateChangeProcess.start();
				}
			}

		}  catch (Exception e) {
			logger.error(e);
		} 
		logger.debug("Leaving");
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aBulkRateChangeHeader
	 * @param tranType
	 * @return AuditHeader
	 * 
	 */
	private AuditHeader getAuditHeader(BulkRateChangeHeader aBulkRateChangeHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aBulkRateChangeHeader.getBefImage(), aBulkRateChangeHeader);
		return new AuditHeader(getReference(), null, null,
				null, auditDetail, aBulkRateChangeHeader.getUserDetails(), getOverideMap());
	}

	private void refreshList() {
		getBulkRateChangeListCtrl().search();
	}


	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_BulkRateChangeDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
			doClose(this.btnSave.isVisible());
	}

	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getBulkRateChangeHeader().getBulkRateChangeRef();
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isRatechanges(boolean isFinListChanged) {

		if(isFinListChanged) {
			if(this.oldRateChangeDetails_Fin != null && this.financesList != null && this.oldRateChangeDetails_Fin.size() != this.financesList.size()) {
				return true;
			}

			if(this.oldRateChangeDetails_RateChangeFin != null && this.rateChangeFinList != null && this.oldRateChangeDetails_RateChangeFin.size() != this.rateChangeFinList.size()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		MessageUtil.showHelpWindow(event, super.window);
	}


	/**
	 * when the "refresh" button is clicked. <br>
	 * <br>
	 * Refreshes the view by calling the onCreate event manually.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnRefresh(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Events.postEvent("onCreate", this.window_BulkRateChangeDialog, event);
		this.window_BulkRateChangeDialog.invalidate();
		logger.debug("Leaving" + event.toString());
	}


	/*
	 * onClick Event For Print Button
	 */
	public void onClick$print(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		doWriteComponentsToBean(getBulkRateChangeHeader());	

		if(getBulkRateChangeHeader().getReCalType().equals(CalculationConstants.RPYCHG_TILLMDT)){
			getBulkRateChangeHeader().setLovDescReCalType(Labels.getLabel("label_Till_Maturity"));
		} else if(getBulkRateChangeHeader().getReCalType().equals(CalculationConstants.RPYCHG_ADJMDT)){
			getBulkRateChangeHeader().setLovDescReCalType(Labels.getLabel("label_Adj_To_Maturity"));
		} else if(getBulkRateChangeHeader().getReCalType().equals(CalculationConstants.RPYCHG_TILLDATE)){
			getBulkRateChangeHeader().setLovDescReCalType(Labels.getLabel("label_Till_Date"));
		} else if(getBulkRateChangeHeader().getReCalType().equals(CalculationConstants.RPYCHG_ADDTERM)){
			getBulkRateChangeHeader().setLovDescReCalType(Labels.getLabel("label_Add_Terms"));
		}

		List<BulkRateChangeDetails> bulkRateChangeDetailsRptData = new ArrayList<BulkRateChangeDetails>();
		if (getBulkRateChangeHeader().getBulkRateChangeDetailsList() != null && getBulkRateChangeHeader().getBulkRateChangeDetailsList().size() > 0) {
			for (BulkRateChangeDetails bulkRateChangeDetails : getBulkRateChangeHeader().getBulkRateChangeDetailsList()) {
				if (bulkRateChangeDetails.isAllowRateChange()) {
					bulkRateChangeDetailsRptData.add(bulkRateChangeDetails);
				}
			}
		}

		List<Object> list = new ArrayList<Object>();
		list.add(bulkRateChangeDetailsRptData);
		String reportName="FINENQ_BulkDifferemmentDetails";

		ReportGenerationUtil.generateReport(reportName, getBulkRateChangeHeader(), list, true, 1, getUserWorkspace()
				.getLoggedInUser().getFullName(), this.window_BulkRateChangeDialog);

		logger.debug("Leaving" + event.toString());
	}


	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {//TODO
		logger.debug("Entering");

		//FinType
		if (!this.finType.isReadonly() && isValidationrequired()) {
			this.finType.setConstraint(new PTStringValidator(Labels.getLabel("label_BulkRateChangeDialog_FinType.value"), null, true));
		}

		//SchFromDate
		if (isValidationrequired()) {
			this.schFromDate.setConstraint(new PTDateValidator(Labels.getLabel("label_IjaraBulkRateChange_FromDate.value"), true, appStartDate,endDate,true));
		} else {
			this.schFromDate.setConstraint(new PTDateValidator(Labels.getLabel("label_IjaraBulkRateChange_FromDate.value"), true));
		}

		//ToDate
		if(this.row_ToDate.isVisible() && isValidationrequired()) {
			this.toDate.setConstraint(new PTDateValidator(Labels.getLabel("label_IjaraBulkRateChange_ToDate.value"), false, appStartDate,endDate, true));
		} else {
			this.toDate.setConstraint(new PTDateValidator(Labels.getLabel("label_IjaraBulkRateChange_ToDate.value"), false));
		}

		//RuleType
		if(this.row_RuleType.isVisible() && !this.ruleType.isReadonly() && isValidationrequired()) {
			this.ruleType.setConstraint(new PTStringValidator(Labels.getLabel("label_BulkRateChangeDialog_RuleType.value"), null, false));
		}

		logger.debug("Leaving");
	}

	/**
	 * 
	 * @return boolean
	 */
	public boolean isValidationrequired(){
		if(this.userAction.getSelectedItem()!= null){
			if( this.userAction.getSelectedItem().getValue().toString().equalsIgnoreCase(PennantConstants.RCD_STATUS_CANCELLED)
					|| this.userAction.getSelectedItem().getValue().toString().equalsIgnoreCase(PennantConstants.RCD_STATUS_REJECTED)
					|| this.userAction.getSelectedItem().getValue().toString().equalsIgnoreCase(PennantConstants.RCD_STATUS_RESUBMITTED)){
				return false;
			} else{
				return true;
			}
		}
		return true;
	}

	/**
	 * Method to remove constraints
	 * 
	 * */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.finType.setConstraint("");
		this.schFromDate.setConstraint("");
		this.toDate.setConstraint("");
		this.ruleType.setConstraint("");

		logger.debug("Leaving");
	}
	/**
	 * Method to clear error messages
	 * 
	 * */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.finType.clearErrorMessage();
		this.schFromDate.clearErrorMessage();
		this.toDate.clearErrorMessage();
		this.ruleType.clearErrorMessage();
		logger.debug("Leaving");
	}



	/*
	 * *************************************************
	 * **************** Bulk Rate Change Changes *********
	 * *************************************************
	 *
	 */

	/**
	 *  
	 */
	public void onClick$btnSearch(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		isNewFinList = true;
		doReSetValues();
		doFillFinancesList();

		logger.debug("Leaving" + event.toString());
	}

	private void doReSetValues() {
		logger.debug("Entering");

		this.tab_FinancesList.setSelected(true);
		this.listBox_RateChangeFinList.getItems().clear();
		this.financesList.clear();
		this.rateChangeFinList.clear();
		getBulkRateChangeHeader().setRateChange(BigDecimal.ZERO);
		this.rateChange.setValue(BigDecimal.ZERO);

		logger.debug("Leaving");
	}

	/**
	 * Method for fetching Details of Finances Based on Finance Types and Schedule Date
	 */
	public void onClick$addFinList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		if(!this.listBox_FinancesList.getSelectedItems().isEmpty()) {
			this.tab_RateChangeFinList.setSelected(true);

			// For Newly added finances
			for (Listitem listitem : this.listBox_FinancesList.getItems()) {
				if (this.listBox_FinancesList.getSelectedItems().contains(listitem)) {
					this.rateChangeFinList.add((BulkRateChangeDetails) listitem.getAttribute("data"));
					this.financesList.remove(((BulkRateChangeDetails) listitem.getAttribute("data")));
				}
			}

			//Finances Tab - Finances List
			listboxItemRenderer(this.financesList, listBox_FinancesList, paging_FinancesList);

			//Selected Finances Tab - Selected Finances List For Rate Change
			listboxItemRenderer(this.rateChangeFinList, listBox_RateChangeFinList, paging_RateChangeFinList);

		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for fetching Details of Finances Based on Finance Types and Schedule Date
	 */
	public void onClick$removeFinList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		if(!this.listBox_RateChangeFinList.getSelectedItems().isEmpty())  {
			//this.tab_FinancesList.setSelected(true);

			// For Newly added finances
			for (Listitem listitem : this.listBox_RateChangeFinList.getItems()) {
				if (this.listBox_RateChangeFinList.getSelectedItems().contains(listitem)) {
					this.rateChangeFinList.remove((BulkRateChangeDetails) listitem.getAttribute("data"));
					this.financesList.add(((BulkRateChangeDetails) listitem.getAttribute("data")));
				}
			}

			//Finances Tab - Finances List
			listboxItemRenderer(this.financesList, listBox_FinancesList, paging_FinancesList);

			//Selected Finances Tab - Selected Finances List For Rate Change
			listboxItemRenderer(this.rateChangeFinList, listBox_RateChangeFinList, paging_RateChangeFinList);

		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$finFilter(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		searchBulkRateFinances();

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$rateChangeFinFilter(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		searchBulkRateFinances();

		logger.debug("Leaving" + event.toString());

	}

	/**
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$finRefresh(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		//Finances details List Validation
		if (isRatechanges(false)) {
			MessageUtil.showError(Labels.getLabel("label_RateChange_FinanceList_Changed"));
			return;
		}

		listboxItemRenderer(this.financesList, listBox_FinancesList, paging_FinancesList); //TODO

		logger.debug("Leaving" + event.toString());

	}

	/**
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$rateChangeRefresh(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		//Finances details List Validation
		if (isRatechanges(false)) {
			MessageUtil.showError(Labels.getLabel("label_RateChange_FinanceList_Changed"));
			return;
		}

		listboxItemRenderer(this.rateChangeFinList, listBox_RateChangeFinList, paging_RateChangeFinList); //TODO

		logger.debug("Leaving" + event.toString());

	}

	/**
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnApplyRateChange(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		if(this.listBox_FinancesList.getItems().isEmpty() && this.listBox_RateChangeFinList.getItems().isEmpty()) {

			this.tab_FinancesList.setSelected(true);
			MessageUtil.showError(Labels.getLabel("label_RateChange_FinanceList_Empty"));
			return;
		} 

		this.isApplyRateChangeWin = true;
		openRateChangeDialog();

		logger.debug("Leaving" + event.toString());

	}
	
	/**
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnCalculateNewRates(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		if ((getBulkRateChangeHeader().getRateChange() != BigDecimal.ZERO) && !this.rateChangeFinList.isEmpty()) {

			List<BulkRateChangeDetails> changedFinList = new ArrayList<BulkRateChangeDetails>();
			List<BulkRateChangeDetails> unChangedFinList = new ArrayList<BulkRateChangeDetails>();
			this.tab_RateChangeFinList.setSelected(true);

			for (BulkRateChangeDetails bulkRateChangeDetail : this.rateChangeFinList) {
				boolean newFinance = true;
				for (BulkRateChangeDetails rateChangeDetail : this.oldRateChangeDetails_RateChangeFin) {
					if (bulkRateChangeDetail.getFinReference().equals(rateChangeDetail.getFinReference())) {
						newFinance = false;
						break;
					}
				} 
				if (newFinance) {
					changedFinList.add(bulkRateChangeDetail);
				} else {
					unChangedFinList.add(bulkRateChangeDetail);
				}
			}
			if (!changedFinList.isEmpty()) {
				calculateNewRateAndNewProfit(getBulkRateChangeHeader(),	changedFinList, unChangedFinList);
			}
		} 

		logger.debug("Leaving" + event.toString());

	}

	/**
	 * 
	 * @throws InterruptedException
	 */
	private void openRateChangeDialog() throws InterruptedException {
		logger.debug("Entering");

		this.tab_RateChangeFinList.setSelected(true);
		doSetValidation();
		doWriteComponentsToBean(getBulkRateChangeHeader());

		//Finances details List Validation
		if (isRatechanges(false)) {
			MessageUtil.showError(Labels.getLabel("label_RateChange_FinanceList_Changed"));
			return;
		}

		/*if(!recSave && (this.rateChangeFinList == null || this.rateChangeFinList.isEmpty())) { //TODO
			try {
				this.tab_RateChangeFinList.setSelected(true);
				MessageUtil.showErrorMessage(Labels.getLabel("label_RateChange_EmptyFinanceList"));
			} catch (InterruptedException e) {
				logger.error(e);
				return;
			}
			return;
		}*/

		final HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("isApplyRateChangeWin", isApplyRateChangeWin);
		map.put("bulkRateChangeDialogCtrl", this);
		map.put("bulkRateChangeHeader", getBulkRateChangeHeader());

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/BulkRateChange/BulkRateChange.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * 
	 * @throws Exception
	 */
	private void searchBulkRateFinances() throws Exception {
		logger.debug("Entering");

		//Finances details List Validation
		if (isRatechanges(false)) { //TODO to many times check once
			MessageUtil.showError(Labels.getLabel("label_RateChange_FinanceList_Changed"));
			return;
		}
		final HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("isNewFinList", isNewFinList);
		map.put("bulkRateChangeDialogCtrl", this);
		map.put("bulkRateChangeHeader", getBulkRateChangeHeader());

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/BulkRateChange/BulkRateChangeSearchDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");

	}

	/**
	 * 
	 * @param list
	 * @param listbox
	 * @param paging
	 */
	@SuppressWarnings("unchecked")
	private void listboxItemRenderer(List<BulkRateChangeDetails> list, Listbox listbox, Paging paging) {
		logger.debug("Entering");

		// Set the ListModel for the articles.
		((PagedListWrapper<BulkRateChangeDetails>) listbox.getModel()).initList(list, listbox, paging);
		listbox.setItemRenderer(new BulkRateChangeDialogModelItemRenderer());

		logger.debug("Leaving");
	}

	/**
	 * Method for fetching Details of Finances Based on Finance Types and Schedule Date
	 */
	private void doFillFinancesList() {
		logger.debug("Entering");

		throwValidation();

		/*if (getUserWorkspace().isAllowed("button_BulkProcessHeader_btnProceed")
				&& !StringUtils.trimToEmpty(this.bulkRateChangeHeader.getRecordStatus()).equals(PennantConstants.RCD_STATUS_APPROVED)) {*/

		String whereClause = getWhereClause(); // Build The Where Clause to get the Finances List
		String finType = this.finType.getValue();
		Date fromDate = new Timestamp(this.schFromDate.getValue().getTime());

		getBulkRateChangeHeader().setBulkRateChangeDetailsList(getBulkRateChangeProcessService().getBulkRateChangeFinList(finType, fromDate, whereClause));

		this.financesList.addAll(getBulkRateChangeHeader().getBulkRateChangeDetailsList());

		if(this.oldBulkRateChangeDetails != null && this.oldBulkRateChangeDetails.equals(getBulkRateChangeHeader().getBulkRateChangeDetailsList())){
			getBulkRateChangeHeader().setLovDescIsOlddataChanged(false);
		} else {
			getBulkRateChangeHeader().setLovDescIsOlddataChanged(true);
		}


		this.listBox_FinancesList.setHeight(Integer.parseInt(getListBoxHeight(grid_BulkRateChange.getRows().getVisibleItemCount()).substring(0, 
				getListBoxHeight(grid_BulkRateChange.getRows().getVisibleItemCount()).indexOf("px"))) - 50 + "px");
		this.paging_FinancesList.setPageSize(Integer.parseInt(getListBoxHeight(grid_BulkRateChange.getRows().getVisibleItemCount()).substring(0, 
				getListBoxHeight(grid_BulkRateChange.getRows().getVisibleItemCount()).indexOf("px"))) - 50);

		getFinListWrapper().initList(this.financesList, this.listBox_FinancesList, this.paging_FinancesList);
		this.listBox_FinancesList.setItemRenderer(new BulkRateChangeDialogModelItemRenderer());

		getRateChangeFinListWrapper().initList(this.rateChangeFinList, this.listBox_RateChangeFinList, this.paging_RateChangeFinList);
		this.listBox_RateChangeFinList.setItemRenderer(new BulkRateChangeDialogModelItemRenderer());

		//doReadOnly(true);

		if(getBulkRateChangeHeader().getBulkRateChangeDetailsList() == null || getBulkRateChangeHeader().getBulkRateChangeDetailsList().size() == 0) {
			try {
				final String msg = " No Finances Founded With Finance Type " + this.finType.getValue() + " And Schedule Date Greater than or equal to " +
						DateUtility.formatUtilDate(this.schFromDate.getValue(), PennantConstants.dateFormat);

				MessageUtil.showMessage(msg);

			} catch (WrongValueException e) {
				logger.error(e);
			}
			return;
		} else {
			listButtonsVisibility(true);
		}
		//}
	/*	if(listBox_FinancesList != null && listBox_FinancesList.getItems().size() > 0){
			this.btnPrint.setVisible(false);//TODO
		} else {
			this.btnPrint.setVisible(false);
		}*/
		logger.debug("Leaving");
	}

	/**
	 * 
	 */
	private void throwValidation() {
		logger.debug("Entering");

		doSetValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Finance Type
		try {
			this.finType.getValidatedValue();
		} catch (WrongValueException we ) {
			wve.add(we);
		}

		//Schedule Date From
		try {
			this.schFromDate.getValue();
		} catch (WrongValueException we ) {
			wve.add(we);
		}

		//To Date
		try {
			if (this.row_ToDate.isVisible() && this.toDate.getValue() != null) {
				this.toDate.getValue();
			}
		} catch (WrongValueException we ) {
			wve.add(we);
		}

		//Rule Type
		try {
			if(this.row_RuleType.isVisible()) {
				this.ruleType.getValidatedValue();
			}
		} catch (WrongValueException we ) {
			wve.add(we);
		}

		doRemoveValidation();

		if (!wve.isEmpty()) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	/**
	 * 
	 * @return whereClause
	 */
	private String getWhereClause() {
		logger.debug("Entering");

		StringBuilder whereClause = new StringBuilder();

		whereClause.append(" ( FinIsActive = 1 AND RcdMaintainSts = '' AND RecordType != '" + PennantConstants.RECORD_TYPE_NEW + "' " );
		whereClause.append(" AND ((AllowGrcPftRvw = 1 OR AllowRepayRvw = 1) OR (FinStartDate = LastRepayDate and FinStartDate = LastRepayPftDate))" );

		if (this.row_RuleType.isVisible() && !StringUtils.trimToEmpty(this.rule.getSqlQuery()).equals("")) {
			whereClause.append(" AND (" + this.rule.getSqlQuery() + ")" ); 
		} else {
			Date fromDate = new Timestamp(this.schFromDate.getValue().getTime());
			whereClause.append(" AND (FinType = '" + this.finType.getValue() + "' AND " + "FromDate >= '" + fromDate + "')"); 
		}

		// Filtering added based on user branch and division
		whereClause.append(") AND " + getUsrFinAuthenticationQry(false)); 

		logger.debug("Leaving");
		return whereClause.toString();
	}

	/**
	 * 
	 * @param bulkRateChangeHeader
	 */
	public void fillRateChangeDetails(BulkRateChangeHeader bulkRateChangeHeader) {
		logger.debug("Entering");

		this.label_Reference.setValue(bulkRateChangeHeader.getBulkRateChangeRef());
		this.label_ReCalType.setValue(PennantStaticListUtil.getlabelDesc(bulkRateChangeHeader.getReCalType(), PennantStaticListUtil.getSchCalCodes()));
		this.rateChange.setValue(bulkRateChangeHeader.getRateChange());

		logger.debug("Leaving");
	}

	/**
	 * Method for Rate changes for IJARAH Finances by Applying Actual rates
	 * 
	 * @param bulkRateChangeFinances
	 */
	public void calculateNewRateAndNewProfit(BulkRateChangeHeader bulkRateChangeHeader, List<BulkRateChangeDetails> financeList, List<BulkRateChangeDetails> unChangedFinList) {
		logger.debug("Entering");

		List<BulkRateChangeDetails> detailsList = new ArrayList<BulkRateChangeDetails>();

		//Bulk Rate Changes applied for fetched list
		for (BulkRateChangeDetails rateChangeFinance : financeList) {

			//Get Total Finance Details to particular Finance
			//FinanceDetail financeDetail = getFinanceDetailService().getFinanceDetailById(rateChangeFinance.getFinReference(), false, "SCDCHG", false);
			FinanceDetail financeDetail = getFinanceDetailService().getFinSchdDetailById(rateChangeFinance.getFinReference(), "_AView", false);

			FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

			financeMain.setRecalType(bulkRateChangeHeader.getReCalType());
			financeMain.setEventFromDate(bulkRateChangeHeader.getFromDate());
			financeMain.setRecalFromDate(bulkRateChangeHeader.getFromDate());

			if (bulkRateChangeHeader.getToDate() == null) {
				financeMain.setEventToDate(financeMain.getMaturityDate());
			} else {
				financeMain.setEventToDate(bulkRateChangeHeader.getToDate());
			}

			BigDecimal newProfitRate = financeMain.getEffectiveRateOfReturn().add(bulkRateChangeHeader.getRateChange());

			//Schedule Re-Calculation based on Applied parameters
			financeDetail.setFinScheduleData(ScheduleCalculator.changeRate(financeDetail.getFinScheduleData(), "", "", BigDecimal.ZERO,
					newProfitRate == null ? BigDecimal.ZERO : newProfitRate, true));

			rateChangeFinance.setNewProfitRate(financeMain.getEffectiveRateOfReturn());
			rateChangeFinance.setNewProfit(financeMain.getTotalProfit());

			detailsList.add(rateChangeFinance);
			financeDetail = null;

		}
		if (unChangedFinList != null) {
			detailsList.addAll(unChangedFinList);
		}
		getRateChangeFinListWrapper().initList(detailsList, this.listBox_RateChangeFinList, this.paging_RateChangeFinList);
		this.listBox_RateChangeFinList.setItemRenderer(new BulkRateChangeDialogModelItemRenderer());

		logger.debug("Leaving");
	}

	/**
	 * 
	 * @param btnAccess
	 */
	private void listButtonsVisibility(boolean btnAccess) {
		logger.debug("Entering");

		this.finFilter.setVisible(btnAccess);
		this.finRefresh.setVisible(btnAccess);
		this.addFinList.setVisible(btnAccess);
		this.rateChangeFinFilter.setVisible(btnAccess);
		this.rateChangeRefresh.setVisible(btnAccess);
		this.removeFinList.setVisible(btnAccess);

		logger.debug("Leaving");
	}

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private void addAndRemoveListitems( ){
		logger.debug("Entering");

		List<Listitem> selectedItems = new ArrayList<Listitem>();
		selectedItems.addAll(this.listBox_FinancesList.getSelectedItems());

		for (Listitem listitem : selectedItems) {
			listitem.setParent(listBox_RateChangeFinList);
			listitem.setSelected(false);
		}

		logger.debug("Entering");
	}


	/**
	 * 
	 * @param listbox
	 */
	@SuppressWarnings("unused")
	private void createListHeaders(Listbox listbox){
		logger.debug("Entering");

		Listhead listhead = new Listhead();
		Listheader lh;

		lh = new Listheader();
		lh.setHflex("min");
		lh.setLabel(Labels.getLabel("listheader_FinReference.label"));
		lh.setParent(listhead);

		lh = new Listheader();
		lh.setHflex("min");
		lh.setLabel(Labels.getLabel("listheader_CustCIF.label"));
		lh.setParent(listhead);

		lh = new Listheader();
		lh.setHflex("min");
		lh.setLabel(Labels.getLabel("listheader_FinBranch.label"));
		lh.setParent(listhead);

		lh = new Listheader();
		lh.setHflex("min");
		lh.setLabel(Labels.getLabel("listheader_Ccy.label"));
		lh.setParent(listhead);

		lh = new Listheader();
		lh.setHflex("min");
		lh.setLabel(Labels.getLabel("listheader_Rate.label"));
		lh.setParent(listhead);

		lh = new Listheader();
		lh.setHflex("min");
		lh.setLabel(Labels.getLabel("listheader_PrincipalAmt.label"));
		lh.setParent(listhead);

		lh = new Listheader();
		lh.setHflex("min");
		lh.setLabel(Labels.getLabel("listheader_Profit.label"));
		lh.setParent(listhead);

		lh = new Listheader();
		lh.setHflex("min");
		lh.setLabel(Labels.getLabel("listheader_RateChange.label"));
		lh.setParent(listhead);

		lh = new Listheader();
		lh.setHflex("min");
		lh.setLabel(Labels.getLabel("listheader_NewProfit.label"));
		lh.setParent(listhead);

		listhead.setParent(listbox);

		logger.debug("Leaving");
	}

	@SuppressWarnings("unchecked")
	public PagedListWrapper<BulkRateChangeDetails> getFinListWrapper() {
		if (this.finListWrapper == null) {
			this.finListWrapper = (PagedListWrapper<BulkRateChangeDetails>) SpringUtil.getBean("pagedListWrapper");
		}
		return finListWrapper;
	}
	@SuppressWarnings("unchecked")
	public PagedListWrapper<BulkRateChangeDetails> getRateChangeFinListWrapper() {
		if (this.rateChangeFinListWrapper == null) {
			this.rateChangeFinListWrapper = (PagedListWrapper<BulkRateChangeDetails>) SpringUtil.getBean("pagedListWrapper");
		}
		return rateChangeFinListWrapper;
	}
	public JdbcSearchObject<BulkRateChangeDetails> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<BulkRateChangeDetails> searchObj) {
		this.searchObj = searchObj;
	}

	public BulkRateChangeHeader getBulkRateChangeHeader() {
		return bulkRateChangeHeader;
	}

	public void setBulkRateChangeHeader(BulkRateChangeHeader bulkRateChangeHeader) {
		this.bulkRateChangeHeader = bulkRateChangeHeader;
	}
	public BulkRateChangeListCtrl getBulkRateChangeListCtrl() {
		return bulkRateChangeListCtrl;
	}

	public void setBulkRateChangeListCtrl(BulkRateChangeListCtrl bulkRateChangeListCtrl) {
		this.bulkRateChangeListCtrl = bulkRateChangeListCtrl;
	}

	public BulkRateChangeProcessService getBulkRateChangeProcessService() {
		return bulkRateChangeProcessService;
	}

	public void setBulkRateChangeProcessService(BulkRateChangeProcessService bulkRateChangeProcessService) {
		this.bulkRateChangeProcessService = bulkRateChangeProcessService;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

}