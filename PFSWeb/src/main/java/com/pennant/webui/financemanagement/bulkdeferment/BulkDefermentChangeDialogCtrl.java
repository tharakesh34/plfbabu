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
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  IjarahBulkRateChangeCtrl.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  03-06-2011    
 *                                                                  
 * Modified Date    :  03-06-2011    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-06-2011       Pennant	                 0.1                                         * 
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
package com.pennant.webui.financemanagement.bulkdeferment;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.QueryBuilder;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.Query;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.BulkProcessDetails;
import com.pennant.backend.model.finance.BulkProcessHeader;
import com.pennant.backend.model.finance.ScheduleMapDetails;
import com.pennant.backend.service.finance.BulkDefermentChangeProcessService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.ReportGenerationUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.enquiry.model.BulkChangeDialoglItemRenderer;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the 
 * WEB-INF/pages/FinanceManagement/SchdlRepayment/SchdlRepaymentDialog.zul
 */
public class BulkDefermentChangeDialogCtrl extends GFCBaseCtrl<BulkProcessDetails> {
	private static final long serialVersionUID = 966281186831332116L;
	private static final Logger logger = Logger.getLogger(BulkDefermentChangeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_BulkRateChangeDialog; 		
	protected Borderlayout borderlayout_BulkRate;
	protected Listbox listBoxBulkrateChangeDialog;	    
	protected Grid grid_BulkRateChange;					
	protected Groupbox 		gb_RateDetails;             
	protected Paging  pagingIjarahaFinancesList;         

	protected Decimalbox rateChange; 					
	protected Datebox fromDate;  						
	protected Datebox toDate;  							
	protected Datebox tillDate; 	 					
	protected Combobox cbReCalType; 					
	protected Label label_IjaraBulkRateChange_TillDate; 
	protected Hbox hbox_TillDate;						


	// For Bulk Deferment 
	protected Datebox calFromDate;                  	 
	protected Label label_BulkDefferment_CalToDate;
	protected Space space_CalToDate;
	protected Datebox calToDate;                    	 
	protected Checkbox exDefDate;                   	 
	protected Combobox cbAddTermAfter;              	 
	protected Tabpanel tabpanel_Query;              	 
	protected Tabpanel tabpanel_IjarahaFinances;    	 
	protected Tab   tab_Query;							 
	protected Tab   tab_IjarahaFinance;					 
	protected ExtendedCombobox ruleType;			     
	protected Label label_BulkRateChangeDialog_RuleType; 
	protected Hbox  hbox_ruleType;						 
	protected QueryBuilder rule;
	
	protected Button btnPreview; 	
	protected Button btnProceed; 	
	protected Button btnRecal; 	    
	protected Button btnPrint; 	    

	protected Listheader listheader_reCalStartDate; 
	protected Listheader listheader_reCalEndDate;   

	//private FinanceDetailService financeDetailService;
	private List<BulkProcessDetails> rateChangeFinances;
	private BulkProcessHeader bulkProcessHeader;
	private BulkDefermentChangeListCtrl bulkDefermentChangeListCtrl ;
	private BulkDefermentChangeProcessService bulkDefermentChangeProcessService;
	private List<BulkProcessDetails> oldBulkProcessDetails = null;

	
	private Row recalFromDateRow;  
	private Row excDefDateRow;     
	private Row addTermRow;        
	private Row rateChangeRow;     
	/**
	 * default constructor.<br>
	 */
	public BulkDefermentChangeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "BulkProcessHeader";
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
			if (arguments.containsKey("bulkProcessHeader")) {
				this.bulkProcessHeader = (BulkProcessHeader) arguments
						.get("bulkProcessHeader");
				BulkProcessHeader befImage = new BulkProcessHeader();
				BeanUtils.copyProperties(this.bulkProcessHeader, befImage);
				this.bulkProcessHeader.setBefImage(befImage);
				setBulkProcessHeader(this.bulkProcessHeader);
			} else {
				setBulkProcessHeader(null);
			}

			doLoadWorkFlow(this.bulkProcessHeader.isWorkflow(),
					this.bulkProcessHeader.getWorkflowId(),
					this.bulkProcessHeader.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"BulkProcessHeader");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED parameters !
			// we get the bulkProcessHeaderListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete bulkProcessHeader here.
			if (arguments.containsKey("bulkDefermentChangeListCtrl")) {
				setBulkDefermentChangeListCtrl((BulkDefermentChangeListCtrl) arguments
						.get("bulkDefermentChangeListCtrl"));
			} else {
				setBulkDefermentChangeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			getVisibilityOfComponents(getBulkProcessHeader());
			doShowDialog(getBulkProcessHeader());
			
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_BulkRateChangeDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	public void getVisibilityOfComponents(BulkProcessHeader bulkProcessHeader){
		
		if("D".equals(bulkProcessHeader.getBulkProcessFor())){
			this.label_BulkRateChangeDialog_RuleType.setVisible(true);
			this.hbox_ruleType.setVisible(true);
			this.ruleType.setVisible(true);

			this.ruleType.setMaxlength(15);
			this.ruleType.setTextBoxWidth(120);
			this.ruleType.setModuleName("Query");
			this.ruleType.setValueColumn("QueryCode");
			this.ruleType.setDescColumn("QueryDesc");
			this.ruleType.setValidateColumns(new String[] { "QueryCode" });

			Filter filter[] = new Filter[1];
			filter[0] = new Filter("QueryModule", "BULKDEFEREMENT_DETAILS", Filter.OP_EQUAL);
			this.ruleType.setFilters(filter);	

		} else if("R".equals(bulkProcessHeader.getBulkProcessFor())) {
			this.rateChangeRow.setVisible(true);
		} 
		
		if(StringUtils.isEmpty(bulkProcessHeader.getRuleType())){
			this.tab_Query.setVisible(false);
			this.tab_IjarahaFinance.setSelected(true);
		}
		
		this.btnProceed.setVisible(false);
		this.btnPrint.setVisible(false);
		if(!this.getBulkProcessHeader().isNew()){
			this.btnPreview.setVisible(false);
			this.btnRecal.setVisible(getUserWorkspace().isAllowed("button_BulkProcessHeader_btnRecal"));
		}else{
			this.btnRecal.setVisible(false);
		}
	}

	public void doCheckRights(){
		logger.debug("Entering");
		
		getUserWorkspace().allocateAuthorities("BulkProcessHeader",getRole());
		this.btnProceed.setVisible(getUserWorkspace().isAllowed("button_BulkProcessHeader_btnProceed"));
		this.btnPreview.setVisible(getUserWorkspace().isAllowed("button_BulkProcessHeader_btnPreview"));
		this.btnRecal.setVisible(getUserWorkspace().isAllowed("button_BulkProcessHeader_btnRecal"));
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_BulkProcessHeader_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_BulkProcessHeader_btnEdit"));
		//this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_BulkProcessHeader_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_BulkProcessHeader_btnSave"));
		this.btnCancel.setVisible(false);	
		
		logger.debug("Leaving");
	}

	public void onChange$ruleType(Event event){
		logger.debug("Entering "+event);
		event.getData();
		logger.debug("Entering "+event);
	}

	/**
	 * When user clicks on button "SearchFinType" button
	 * @param event
	 */
	public void onFulfill$ruleType(Event event){
		logger.debug("Entering " + event.toString());
		Object dataObject = this.ruleType.getObject();
		if (dataObject instanceof String){
			this.ruleType.setValue(dataObject.toString());
			this.ruleType.setDescription("");
			this.tab_Query.setVisible(false);
		}else{
			Query details= (Query) dataObject;
			if (details != null) {
				this.ruleType.setValue(details.getQueryCode());
				this.ruleType.setDescription(details.getQueryDesc());
				this.rule.setEditable(false);
				this.rule.setSqlQuery(details.getSQLQuery());
			}
			this.tab_Query.setVisible(true);
		}
		logger.debug("Leaving " + event.toString());
	}
	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aBulkProcessHeader
	 * @throws Exception
	 */

	public void doShowDialog(BulkProcessHeader aBulkProcessHeader) throws Exception {
		logger.debug("Entering");
		// set ReadOnly mode accordingly if the object is new or not.
		if (aBulkProcessHeader.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.fromDate.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.fromDate.focus();
				if (StringUtils.isNotBlank(aBulkProcessHeader.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doReadOnly(true,true);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aBulkProcessHeader);
			
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
	 *  onSelect Event For Finances Tab
	public void onSelect$tab_IjarahaFinance(Event event){
		logger.debug("Entering "+event);
		if(!StringUtils.trimToEmpty(getBulkProcessHeader().getRecordStatus()).equals(PennantConstants.RCD_STATUS_SAVED)
			&& !StringUtils.trimToEmpty(getBulkProcessHeader().getRecordStatus()).equals(PennantConstants.RCD_STATUS_SUBMITTED)){
			fillIjarahaFinances();
		}
		logger.debug("Leaving "+event);
	}*/

	/*
	 *  Writing Bean Values to Components
	 */
	public void doWriteBeanToComponents(BulkProcessHeader aBulkProcessHeader){
		logger.debug("Entering");

		this.fromDate.setValue(aBulkProcessHeader.getFromDate());
		this.toDate.setValue(aBulkProcessHeader.getToDate());
		this.rateChange.setValue(aBulkProcessHeader.getNewProcessedRate() == null ? BigDecimal.ZERO : aBulkProcessHeader.getNewProcessedRate());
		this.exDefDate.setChecked(bulkProcessHeader.isExcludeDeferement());
		this.ruleType.setValue(aBulkProcessHeader.getRuleType());
		this.rule.setSqlQuery(aBulkProcessHeader.getLovDescSqlQuery());

		String excldMthds = "";
		if("D".equals(this.bulkProcessHeader.getBulkProcessFor())){
			excldMthds = ",ADDTERM,CURPRD,ADDLAST,STEPPOS,";
		} else {
			excldMthds = ",TILLDATE,ADDTERM,ADDLAST,ADJTERMS,STEPPOS,";
		}
		fillComboBox(this.cbReCalType, StringUtils.trimToEmpty(aBulkProcessHeader.getReCalType()), PennantStaticListUtil.getSchCalCodes(), excldMthds);
		this.calFromDate.setValue(aBulkProcessHeader.getReCalFromDate());
		this.calToDate.setValue(aBulkProcessHeader.getReCalToDate());

		if(StringUtils.equals(aBulkProcessHeader.getReCalType(),CalculationConstants.RPYCHG_TILLMDT)){
			this.calToDate.setVisible(false);
			this.space_CalToDate.setSclass("");
			this.label_BulkDefferment_CalToDate.setVisible(false);
		}

		if(!aBulkProcessHeader.isNew() && !StringUtils.isEmpty(aBulkProcessHeader.getRuleType()) ){
			this.tab_Query.setVisible(true);
		}

		if (StringUtils.trimToEmpty(getBulkProcessHeader().getReCalType()).equals(CalculationConstants.RPYCHG_TILLDATE) || 
				StringUtils.trimToEmpty(getBulkProcessHeader().getReCalType()).equals(CalculationConstants.RPYCHG_TILLMDT)) {
			this.listheader_reCalStartDate.setVisible(true);
			this.listheader_reCalEndDate.setVisible(true);
			this.recalFromDateRow.setVisible(true);
		} else {
			this.listheader_reCalStartDate.setVisible(false);
			this.listheader_reCalEndDate.setVisible(false);
			this.recalFromDateRow.setVisible(false);
		}

		// List Process Details for Rendering
		if(aBulkProcessHeader.getBulkProcessDetailsList() != null) {
			getBulkProcessHeader().setBulkProcessDetailsList(aBulkProcessHeader.getBulkProcessDetailsList());
			this.oldBulkProcessDetails = aBulkProcessHeader.getBulkProcessDetailsList();
			this.listBoxBulkrateChangeDialog.setHeight(Integer.parseInt(getListBoxHeight(grid_BulkRateChange.getRows().getVisibleItemCount()).
					substring(0,getListBoxHeight(grid_BulkRateChange.getRows().getVisibleItemCount()).indexOf("px"))) - 50 + "px");
			this.pagingIjarahaFinancesList.setPageSize(Integer.parseInt(getListBoxHeight(grid_BulkRateChange.getRows().getVisibleItemCount()).
					substring(0,getListBoxHeight(grid_BulkRateChange.getRows().getVisibleItemCount()).indexOf("px"))) - 50 );

			getPagedListWrapper().initList(getBulkProcessHeader().getBulkProcessDetailsList(), this.listBoxBulkrateChangeDialog, this.pagingIjarahaFinancesList);
			this.listBoxBulkrateChangeDialog.setItemRenderer(new BulkChangeDialoglItemRenderer(!getUserWorkspace().isAllowed("BulkProcessHeader_isDeferedItemDisabled"),
					this.window_BulkRateChangeDialog));

		}

		this.recordStatus.setValue(getBulkProcessHeader().getRecordStatus());
		logger.debug("Leaving");
	}

	/*
	 *  Writing Component Values to Bean
	 */
	public void doWriteComponentsToBean(BulkProcessHeader aBulkProcessHeader){
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aBulkProcessHeader.setFromDate(DateUtility.getDBDate(DateUtility.formatDate(this.fromDate.getValue(), PennantConstants.DBDateFormat)));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBulkProcessHeader.setToDate(DateUtility.getDBDate(DateUtility.formatDate(this.toDate.getValue(), PennantConstants.DBDateFormat)));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(this.toDate.getValue() != null && this.fromDate.getValue() != null && this.toDate.getValue().before(this.fromDate.getValue())){
				throw new WrongValueException(this.toDate,Labels.getLabel("DATE_ALLOWED_AFTER",
						new String[]{Labels.getLabel("label_BulkRateChangeDialog_ToDate.value"),
						Labels.getLabel("label_BulkRateChangeDialog_FromDate.value")}));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(this.rateChangeRow.isVisible()){
				aBulkProcessHeader.setNewProcessedRate(this.rateChange.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (isValidComboValue(this.cbReCalType, Labels.getLabel("label_BulkRateChangeDialog_RecalType.value")) 
					&& this.cbReCalType.getSelectedIndex() != 0) {
			aBulkProcessHeader.setReCalType(this.cbReCalType.getSelectedItem().getValue().toString());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		if(this.recalFromDateRow.isVisible()){
			try {
				if(this.calFromDate.getValue() != null){
					aBulkProcessHeader.setReCalFromDate(DateUtility.getDBDate(DateUtility.formatDate(this.calFromDate.getValue(), PennantConstants.DBDateFormat)));
				}

			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if(this.calToDate.getValue() != null){
					aBulkProcessHeader.setReCalToDate(DateUtility.getDBDate(DateUtility.formatDate(this.calToDate.getValue(), PennantConstants.DBDateFormat)));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		try {
			if(this.calToDate.getValue() != null){
				if(this.calToDate.getValue().before(this.calFromDate.getValue())){
					throw new WrongValueException(this.calToDate,Labels.getLabel("DATE_ALLOWED_AFTER",
							new String[]{Labels.getLabel("label_BulkDefferment_CalToDate.value"),
							Labels.getLabel("label_BulkDefferment_CalFromDate.value")}));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.ruleType.isVisible()) {
				aBulkProcessHeader.setRuleType(this.ruleType.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(this.excDefDateRow.isVisible()){
				aBulkProcessHeader.setExcludeDeferement(this.exDefDate.isChecked());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(StringUtils.equals(aBulkProcessHeader.getReCalType(),CalculationConstants.RPYCHG_ADDTERM)){
				aBulkProcessHeader.setAddTermAfter(this.cbAddTermAfter.getSelectedItem().getValue().toString());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		doRemoveValidation();
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		if(aBulkProcessHeader.getBulkProcessDetailsList() != null && !aBulkProcessHeader.getBulkProcessDetailsList().isEmpty()){
			if (aBulkProcessHeader.isNewRecord()) {
				for (BulkProcessDetails bulkProcessDetails : aBulkProcessHeader.getBulkProcessDetailsList()) {
					bulkProcessDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					bulkProcessDetails.setRecordStatus(PennantConstants.RCD_ADD);
					bulkProcessDetails.setWorkflowId(aBulkProcessHeader.getWorkflowId());
					bulkProcessDetails.setNewRecord(true);
				}
			}else{
				aBulkProcessHeader.setRecordStatus(this.recordStatus.getValue());
			}
		}

		logger.debug("Leaving");
	}

	public void doEdit() {

		logger.debug("Entering");
		if (getBulkProcessHeader().isNewRecord()) {
			this.fromDate.setReadonly(false);
			this.toDate.setReadonly(false);
			this.tillDate.setReadonly(false);
			readOnlyComponent(false, this.cbReCalType);
		} else {
			readOnlyComponent(true, this.fromDate);
			readOnlyComponent(true, this.toDate);
			readOnlyComponent(true, this.tillDate);
			readOnlyComponent(true, this.cbReCalType);
		}

		readOnlyComponent(isReadOnly("BulkProcessHeader_fromDate"), this.fromDate);
		readOnlyComponent(isReadOnly("BulkProcessHeader_toDate"), this.toDate);
		readOnlyComponent(isReadOnly("BulkProcessHeader_tillDate"), this.tillDate);
		readOnlyComponent(isReadOnly("BulkProcessHeader_cbReCalType"), this.cbReCalType);
		readOnlyComponent(isReadOnly("BulkProcessHeader_ruleType"), this.ruleType);

		if(StringUtils.trimToEmpty(getBulkProcessHeader().getReCalType()).equals(CalculationConstants.RPYCHG_TILLDATE) ||
				StringUtils.trimToEmpty(getBulkProcessHeader().getReCalType()).equals(CalculationConstants.RPYCHG_TILLMDT)){
			this.recalFromDateRow.setVisible(true);
			readOnlyComponent(isReadOnly("BulkProcessHeader_reCalFromDate"), this.calFromDate);
			if(!StringUtils.trimToEmpty(getBulkProcessHeader().getReCalType()).equals(CalculationConstants.RPYCHG_TILLMDT)){
				readOnlyComponent(isReadOnly("BulkProcessHeader_reCalToDate"), this.calToDate);
			}
		} else if(StringUtils.trimToEmpty(getBulkProcessHeader().getReCalType()).equals(CalculationConstants.RPYCHG_ADDTERM)){
			readOnlyComponent(isReadOnly("BulkProcessHeader_cbAddTermAfter"), this.cbAddTermAfter);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.bulkProcessHeader.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.rateChange.setMaxlength(13);
		this.rateChange.setFormat(PennantConstants.rateFormate9);
		this.rateChange.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.rateChange.setScale(9);

		this.calFromDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.calToDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.tillDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.fromDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.toDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.rule.setEditable(false);
		
		logger.debug("Leaving");
	}

	/**
	 * when user changes recalculation type 
	 * @param event
	 * 
	 */
	public void onChange$cbReCalType(Event event) {
		logger.debug("Entering" + event.toString());
		String recalType = this.cbReCalType.getSelectedItem().getValue().toString();
		if ("D".equals(this.bulkProcessHeader.getBulkProcessFor())) {
			if (recalType.equals(CalculationConstants.RPYCHG_TILLDATE) || recalType.equals(CalculationConstants.RPYCHG_TILLMDT)) {
				if(recalType.equals(CalculationConstants.RPYCHG_TILLDATE)){
					setEventDatesValidation(this.toDate.getValue());
					this.calToDate.setVisible(true);
					this.label_BulkDefferment_CalToDate.setVisible(true);
					this.space_CalToDate.setSclass(PennantConstants.mandateSclass);
					this.calFromDate.setConstraint("");
					this.calToDate.setConstraint("");
					this.calFromDate.setText("");
					this.calToDate.setText("");
				}
				this.recalFromDateRow.setVisible(true);
				this.addTermRow.setVisible(false);
				this.cbAddTermAfter.setSelectedIndex(0);
				if (recalType.equals(CalculationConstants.RPYCHG_TILLMDT)) {
					this.calToDate.setVisible(false);
					this.label_BulkDefferment_CalToDate.setVisible(false);
					this.space_CalToDate.setSclass("");
					this.calFromDate.setConstraint("");
					this.calToDate.setConstraint("");
					this.calFromDate.setValue(null);
					this.calFromDate.setText("");
					this.calToDate.setText("");
				} else {
					this.calToDate.setDisabled(false);
					this.calToDate.setVisible(true);
					this.label_BulkDefferment_CalToDate.setVisible(true);
					this.space_CalToDate.setSclass(PennantConstants.mandateSclass);
				}
			} else if (recalType.equals(CalculationConstants.RPYCHG_ADDTERM)) {
				this.addTermRow.setVisible(false);
				this.cbAddTermAfter.setSelectedIndex(1);
				this.recalFromDateRow.setVisible(false);
				this.excDefDateRow.setVisible(false);
				this.exDefDate.setChecked(false);
				this.calFromDate.setText("");
				this.calToDate.setText("");
			} else {
				this.recalFromDateRow.setVisible(false);
				this.excDefDateRow.setVisible(false);
				this.exDefDate.setChecked(false);
				this.addTermRow.setVisible(false);
				this.cbAddTermAfter.setSelectedIndex(0);
				this.calFromDate.setText("");
				this.calToDate.setText("");
				this.calToDate.setVisible(true);
				this.label_BulkDefferment_CalToDate.setVisible(true);
				this.space_CalToDate.setSclass(PennantConstants.mandateSclass);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/*
	 *  Method for setting validations for event dates
	 */
	public void setEventDatesValidation(Date date){
		logger.debug("Entering");
		if (this.recalFromDateRow.isVisible()) {
			if (!this.calFromDate.isDisabled()) {
				this.calFromDate.setConstraint(new PTDateValidator(Labels.getLabel("label_BulkDefferment_CalFromDate.value"), true, this.toDate.getValue(), null, false));
			}
			if (!this.calToDate.isDisabled()&& this.calToDate.isVisible()) {
				this.calToDate.setConstraint(new PTDateValidator(Labels.getLabel("label_BulkDefferment_CalToDate.value"), true, this.toDate.getValue(), null, false));
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$btnSave(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
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
		doWriteBeanToComponents(this.bulkProcessHeader.getBefImage());
		doReadOnly(true,false);
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}


	/**
	 * Deletes a Academic object from database.<br>
	 * @throws Exception 
	 */
	private void doDelete() throws Exception {
		logger.debug("Entering");
		final BulkProcessHeader aBulkProcessHeader = new BulkProcessHeader();
		BeanUtils.copyProperties(getBulkProcessHeader(), aBulkProcessHeader);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
				"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aBulkProcessHeader.getFromDate();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aBulkProcessHeader.getRecordType())) {
				aBulkProcessHeader.setVersion(aBulkProcessHeader.getVersion() + 1);
				aBulkProcessHeader.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aBulkProcessHeader.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aBulkProcessHeader, tranType)) {
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

		final BulkProcessHeader aBulkProcessHeader = new BulkProcessHeader();
		BeanUtils.copyProperties(getBulkProcessHeader(), aBulkProcessHeader);
		boolean isNew = false;

		// fill the Academic object with the components data
		if(isValidationrequired()){
			doSetValidation();
			doWriteComponentsToBean(aBulkProcessHeader);
		}

		if((aBulkProcessHeader.getBulkProcessDetailsList() == null || aBulkProcessHeader.getBulkProcessDetailsList().size() == 0 )|| isBulkProcessDetailsChanges()){
			String msg ;
			msg= Labels.getLabel("label_BulkMsg");
			if(aBulkProcessHeader.getBulkProcessDetailsList() != null && aBulkProcessHeader.getBulkProcessDetailsList().size()>0){
				msg =Labels.getLabel("label_BulkDataMsg");
			}
			MessageUtil.showError(msg);
			return;
		}
		
		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aBulkProcessHeader.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aBulkProcessHeader.getRecordType())) {
				aBulkProcessHeader.setVersion(aBulkProcessHeader.getVersion() + 1);
				if (isNew) {
					aBulkProcessHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aBulkProcessHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aBulkProcessHeader.setNewRecord(true);
				}
			}

		} else {
			aBulkProcessHeader.setVersion(aBulkProcessHeader.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aBulkProcessHeader, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}



	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aBulkProcessHeader
	 *            (BulkProcessHeader)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * @throws Exception 
	 * 
	 */
	private boolean doProcess(BulkProcessHeader aBulkProcessHeader, String tranType) throws Exception {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aBulkProcessHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aBulkProcessHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aBulkProcessHeader.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aBulkProcessHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aBulkProcessHeader.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aBulkProcessHeader);
				}

				if (isNotesMandatory(taskId, aBulkProcessHeader)) {
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

			aBulkProcessHeader.setTaskId(taskId);
			aBulkProcessHeader.setNextTaskId(nextTaskId);
			aBulkProcessHeader.setRoleCode(getRole());
			aBulkProcessHeader.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aBulkProcessHeader, tranType);
			String operationRefs = getServiceOperations(taskId, aBulkProcessHeader);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aBulkProcessHeader, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aBulkProcessHeader, tranType);
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
		logger.debug("Entering" + event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes(this.bulkProcessHeader));
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
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
		BulkProcessHeader aBulkProcessHeader = (BulkProcessHeader) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getBulkDefermentChangeProcessService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getBulkDefermentChangeProcessService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getBulkDefermentChangeProcessService().doApprove(auditHeader);

						if (aBulkProcessHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getBulkDefermentChangeProcessService().doReject(auditHeader);

						if (aBulkProcessHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
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
						deleteNotes(getNotes(this.bulkProcessHeader), true);
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
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return processCompleted;
	}


	/**
	 * Get Audit Header Details
	 * 
	 * @param aBulkProcessHeader
	 * @param tranType
	 * @return AuditHeader
	 * 
	 */
	private AuditHeader getAuditHeader(BulkProcessHeader aBulkProcessHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aBulkProcessHeader.getBefImage(), aBulkProcessHeader);
		return new AuditHeader(getReference(), null, null,
				null, auditDetail, aBulkProcessHeader.getUserDetails(), getOverideMap());
	}


	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getBulkDefermentChangeListCtrl().search();
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
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
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
		return String.valueOf(getBulkProcessHeader().getBulkProcessId());
	}
	
	private boolean isBulkProcessDetailsChanges() {
		if (this.oldBulkProcessDetails != getBulkProcessHeader().getBulkProcessDetailsList()) {
			return true;
		}
		return false;
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		MessageUtil.showHelpWindow(event, window_BulkRateChangeDialog);
		logger.debug("Leaving" +event.toString());
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

	/**
	 * Method for List Preview for Condition List
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnPreview(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		fillProcessFinances();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Method for recal culating list
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnRecal(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		this.btnPreview.setVisible(getUserWorkspace().isAllowed("button_BulkProcessHeader_btnPreview"));
		this.btnRecal.setVisible(false);
		doReadOnly(false,false);
		logger.debug("Leaving" +event.toString());
	}
	
	/**
	 * Method for Processing Check Action Events and saving data.
	 * @param forwardEvent
	 */
	public void onFinanceItemSelected(ForwardEvent forwardEvent){
		
		@SuppressWarnings("unchecked")
		List<Object> list = (List<Object>) forwardEvent.getData();
		
		Checkbox checkbox =  (Checkbox) list.get(0);
		BulkProcessDetails processDetail = (BulkProcessDetails) list.get(1);
		if(processDetail == null){
			return;
		}
		List<BulkProcessDetails> bulkProcDetailList = getBulkProcessHeader().getBulkProcessDetailsList();
		if(bulkProcDetailList != null && !bulkProcDetailList.isEmpty()){
			for (BulkProcessDetails detail : bulkProcDetailList) {
				if(StringUtils.equals(detail.getFinReference(), processDetail.getFinReference()) && 
						DateUtility.compare(detail.getDeferedSchdDate(), processDetail.getDeferedSchdDate()) == 0){
					detail.setAlwProcess(checkbox.isChecked());
				}
			}
		}
	}

	/**
	 * Method for Rendering Lists of Bulk Process Details on Finance Objects
	 */
	public void fillProcessFinances() {
		logger.debug("Entering");
		
		doSetValidation();
		doWriteComponentsToBean(getBulkProcessHeader());
		
		String whereClause;
		if (getUserWorkspace().isAllowed("button_BulkProcessHeader_btnProceed")
				&& !StringUtils.trimToEmpty(this.bulkProcessHeader.getRecordStatus()).equals(PennantConstants.RCD_STATUS_APPROVED)) {

			Date fromDate = DateUtility.getDBDate(DateUtility.formatDate(this.fromDate.getValue(), PennantConstants.DBDateFormat));
			Date toDate = DateUtility.getDBDate(DateUtility.formatDate(this.toDate.getValue(), PennantConstants.DBDateFormat));

			if ("R".equals(this.bulkProcessHeader.getBulkProcessFor())) {
				setRateChangeFinances(getBulkDefermentChangeProcessService().getIjaraBulkRateFinList(fromDate, toDate));
				getBulkProcessHeader().setBulkProcessDetailsList(getBulkDefermentChangeProcessService().getIjaraBulkRateFinList(fromDate, toDate));
			} else if ("D".equals(this.bulkProcessHeader.getBulkProcessFor())) {
				if(StringUtils.isNotBlank(this.rule.getSqlQuery())){
					whereClause = StringUtils.trimToEmpty(this.rule.getSqlQuery())+" AND "+getUsrFinAuthenticationQry(false) +" AND (DeferedSchdDate Between '"+fromDate+"' AND '"+toDate+"') "  ;
				}else{
					whereClause = getUsrFinAuthenticationQry(false)+" AND (DeferedSchdDate Between '"+fromDate+"' AND '"+toDate+"') "  ;
				}
				getBulkProcessHeader().setBulkProcessDetailsList(getBulkDefermentChangeProcessService().getBulkDefermentFinList(fromDate, toDate, whereClause));
			}

			if(this.oldBulkProcessDetails != null && this.oldBulkProcessDetails.equals(getBulkProcessHeader().getBulkProcessDetailsList())){
				getBulkProcessHeader().setLovDescIsOlddataChanged(false);
			} else {
				getBulkProcessHeader().setLovDescIsOlddataChanged(true);
			}

			if (getBulkProcessHeader().getReCalType().equals(CalculationConstants.RPYCHG_TILLDATE) || 
					getBulkProcessHeader().getReCalType().equals(CalculationConstants.RPYCHG_TILLMDT)) {
				List<ScheduleMapDetails> scheduleMapDetailsList = getBulkDefermentChangeProcessService().getDeferedDates(getBulkProcessHeader().getBulkProcessDetailsList(), 
						getBulkProcessHeader().getReCalType(), getBulkProcessHeader().getReCalFromDate(), getBulkProcessHeader().getReCalToDate());

				List<BulkProcessDetails> schdBulkProcessDetails = new ArrayList<BulkProcessDetails>(); 
				for (BulkProcessDetails aBulkProcessDetails : getBulkProcessHeader().getBulkProcessDetailsList()) {
					for (ScheduleMapDetails scheduleMapDetail : scheduleMapDetailsList) {
						if (aBulkProcessDetails.getFinReference().equals(scheduleMapDetail.getFinReference())) {
							aBulkProcessDetails.setReCalStartDate(scheduleMapDetail.getSchdFromDate());
							aBulkProcessDetails.setReCalEndDate(scheduleMapDetail.getSchdToDate());
							schdBulkProcessDetails.add(aBulkProcessDetails);
						}
					}
				}

				getBulkProcessHeader().setBulkProcessDetailsList(schdBulkProcessDetails);
				schdBulkProcessDetails = null;
				this.listheader_reCalStartDate.setVisible(true);
				this.listheader_reCalEndDate.setVisible(true);
			} else {
				this.listheader_reCalStartDate.setVisible(false);
				this.listheader_reCalEndDate.setVisible(false);
			}

			if(getBulkProcessHeader().getBulkProcessDetailsList() == null || getBulkProcessHeader().getBulkProcessDetailsList().size() == 0){
				try {
					MessageUtil.showError("No records found with schedule term in between "
							+ DateUtility.formatToLongDate(this.fromDate.getValue()) + " and "
							+ DateUtility.formatToLongDate(this.toDate.getValue()) + ".");
				} catch (WrongValueException e) {
					logger.error("Exception: ", e);
				}
				return;
			}
			doReadOnly(true, false);
		}

		this.btnPreview.setVisible(false);
		this.btnRecal.setVisible(getUserWorkspace().isAllowed("button_BulkProcessHeader_btnRecal"));
		if(listBoxBulkrateChangeDialog != null && listBoxBulkrateChangeDialog.getItems().size() >0){
			this.btnPrint.setVisible(false);
		} else {
			this.btnPrint.setVisible(false);
		}
		this.tab_IjarahaFinance.setSelected(true);
		
		logger.debug("Entering");
	}

	/**
	 * Method for List Preview for Condition List
	 * @param event
	 * @throws InterruptedException
	 * @throws AccountNotFoundException 
	 * @throws WrongValueException 
	 */
	public void onClick$btnProceed(Event event) throws InterruptedException, WrongValueException, InterfaceException {
		logger.debug("Entering" +event.toString());
		this.btnProceed.setDisabled(true);

		//Processing Fetch Finance Details for IJARAH Bulk Rate Changes
		boolean success = getBulkDefermentChangeProcessService().bulkRateChangeFinances(getRateChangeFinances(),
				this.cbReCalType.getSelectedItem().getValue().toString(), this.rateChange.getValue());

		//Need to check Process failure case
		if(success){
			MessageUtil.showMessage(
					"Bulk Rate Application Process Succeed for Finance Count " + getRateChangeFinances().size());
		}

		logger.debug("Leaving" +event.toString());
	}

	/*
	 * onClick Event For Print Button
	 */
	public void onClick$btnPrint(Event event) throws Exception{
		logger.debug("Entering" + event.toString());

		doWriteComponentsToBean(getBulkProcessHeader());	

		if(getBulkProcessHeader().getReCalType().equals(CalculationConstants.RPYCHG_TILLMDT)){
			getBulkProcessHeader().setLovDescReCalType(Labels.getLabel("label_Till_Maturity"));
		} else if(getBulkProcessHeader().getReCalType().equals(CalculationConstants.RPYCHG_ADJMDT)){
			getBulkProcessHeader().setLovDescReCalType(Labels.getLabel("label_Adj_To_Maturity"));
		} else if(getBulkProcessHeader().getReCalType().equals(CalculationConstants.RPYCHG_TILLDATE)){
			getBulkProcessHeader().setLovDescReCalType(Labels.getLabel("label_Till_Date"));
		} else if(getBulkProcessHeader().getReCalType().equals(CalculationConstants.RPYCHG_ADDTERM)){
			getBulkProcessHeader().setLovDescReCalType(Labels.getLabel("label_Add_Terms"));
		}

		List<BulkProcessDetails> bulkProcessDetailsRptData = new ArrayList<BulkProcessDetails>();
		if (getBulkProcessHeader().getBulkProcessDetailsList() != null
				&& getBulkProcessHeader().getBulkProcessDetailsList().size() > 0) {
			for (BulkProcessDetails bulkProcessDetail : getBulkProcessHeader()
					.getBulkProcessDetailsList()) {
				if (!bulkProcessDetail.isAlwProcess()) {
					bulkProcessDetailsRptData.add(bulkProcessDetail);
				}
			}
		}

		List<Object> list = new ArrayList<Object>();
		list.add(bulkProcessDetailsRptData);
		String reportName="FINENQ_BulkDifferemmentDetails";

		ReportGenerationUtil.generateReport(reportName, getBulkProcessHeader(), list, true, 1, getUserWorkspace()
				.getLoggedInUser().getFullName(), this.window_BulkRateChangeDialog);

		logger.debug("Leaving" + event.toString());
	}


	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		Date endDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");
		Date appStartDate = DateUtility.getAppDate();

		if(!isValidationrequired()){
			logger.debug("Leaving");
			return;
		}

		this.fromDate.setConstraint(new PTDateValidator(Labels.getLabel("label_IjaraBulkRateChange_FromDate.value"),true,appStartDate,endDate,true));

		this.toDate.setConstraint(new PTDateValidator(Labels.getLabel("label_IjaraBulkRateChange_ToDate.value"),true,null,endDate,true));

		if(!this.rateChange.isDisabled()){
			this.rateChange.setConstraint(new PTStringValidator(Labels.getLabel("label_IjaraBulkRateChange_Rate.value"),null,true));
		}

		if(this.hbox_TillDate.isVisible()){
			if(!this.tillDate.isDisabled()){
				this.tillDate.setConstraint(new PTDateValidator(Labels.getLabel("label_IjaraBulkRateChange_TillDate.value"),true,appStartDate,endDate,true));
			}
		}

		if(this.recalFromDateRow.isVisible()) {

			if(!this.calFromDate.isDisabled()){
				this.calFromDate.setConstraint(new PTDateValidator(Labels.getLabel("label_BulkDefferment_CalFromDate.value"),true,appStartDate,endDate,true));
			}

			if(this.calToDate.isVisible()){
				this.calToDate.setConstraint(new PTDateValidator(Labels.getLabel("label_BulkDefferment_CalToDate.value"),true,appStartDate,endDate,true));
			}

		}

		if(this.ruleType.isVisible() && !this.ruleType.isReadonly()){
			this.ruleType.setConstraint(new PTStringValidator(Labels.getLabel("label_BulkRateChangeDialog_RuleType.value"),null,false,false));
		}

		if (this.recalFromDateRow.isVisible()){
			setEventDatesValidation(this.toDate.getValue());
		}

		logger.debug("Leaving");
	}

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
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean isReadOnly, boolean isBeforeRender) {
		logger.debug("Entering");
		this.rateChange.setDisabled(isReadOnly);
		this.cbReCalType.setDisabled(isReadOnly);
		this.tillDate.setDisabled(isReadOnly);
		this.fromDate.setDisabled(isReadOnly);
		this.toDate.setDisabled(isReadOnly);
		this.ruleType.setReadonly(isReadOnly);
		this.calFromDate.setDisabled(isReadOnly);
		this.calToDate.setDisabled(isReadOnly);
		this.calToDate.setDisabled(isReadOnly);
		
		if(!isBeforeRender){
			if(isReadOnly){
				isReadOnly = !getUserWorkspace().isAllowed("BulkProcessHeader_isDeferedItemDisabled");
			}else{
				isReadOnly = true;
			}

			this.listBoxBulkrateChangeDialog.setHeight(Integer.parseInt(getListBoxHeight(grid_BulkRateChange.getRows().getVisibleItemCount()).substring(0, getListBoxHeight(grid_BulkRateChange.getRows().getVisibleItemCount()).indexOf("px"))) - 50 + "px");
			this.pagingIjarahaFinancesList.setPageSize(Integer.parseInt(getListBoxHeight(grid_BulkRateChange.getRows().getVisibleItemCount()).substring(0, getListBoxHeight(grid_BulkRateChange.getRows().getVisibleItemCount()).indexOf("px"))) - 50);

			getPagedListWrapper().initList(getBulkProcessHeader().getBulkProcessDetailsList(), this.listBoxBulkrateChangeDialog, this.pagingIjarahaFinancesList);
			this.listBoxBulkrateChangeDialog.setItemRenderer(new BulkChangeDialoglItemRenderer(isReadOnly, this.window_BulkRateChangeDialog));
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to remove constraints
	 * 
	 * */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.rateChange.setConstraint("");
		this.tillDate.setConstraint("");
		this.fromDate.setConstraint("");
		this.toDate.setConstraint("");
		this.cbReCalType.setConstraint("");
		this.ruleType.setConstraint("");
		this.calFromDate.setConstraint("");
		this.calToDate.setConstraint("");

		logger.debug("Leaving");
	}
	/**
	 * Method to clear error messages
	 * 
	 * */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.rateChange.clearErrorMessage();
		this.tillDate.clearErrorMessage();
		this.fromDate.clearErrorMessage();
		this.toDate.clearErrorMessage();
		this.cbReCalType.clearErrorMessage();
		this.ruleType.clearErrorMessage();
		this.calFromDate.clearErrorMessage();
		this.calToDate.clearErrorMessage();
		logger.debug("Leaving");
	}

	// Getters & Setters
	public void setRateChangeFinances(List<BulkProcessDetails> rateChangeFinances) {
		this.rateChangeFinances = rateChangeFinances;
	}
	public List<BulkProcessDetails> getRateChangeFinances() {
		return rateChangeFinances;
	}

	public BulkProcessHeader getBulkProcessHeader() {
		return bulkProcessHeader;
	}

	public void setBulkProcessHeader(BulkProcessHeader bulkProcessHeader) {
		this.bulkProcessHeader = bulkProcessHeader;
	}

	public BulkDefermentChangeListCtrl getBulkDefermentChangeListCtrl() {
		return bulkDefermentChangeListCtrl;
	}

	public void setBulkDefermentChangeListCtrl(BulkDefermentChangeListCtrl bulkDefermentChangeListCtrl) {
		this.bulkDefermentChangeListCtrl = bulkDefermentChangeListCtrl;
	}

	public BulkDefermentChangeProcessService getBulkDefermentChangeProcessService() {
		return bulkDefermentChangeProcessService;
	}

	public void setBulkDefermentChangeProcessService(BulkDefermentChangeProcessService bulkDefermentChangeProcessService) {
		this.bulkDefermentChangeProcessService = bulkDefermentChangeProcessService;
	}

}