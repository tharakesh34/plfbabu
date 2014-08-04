package com.pennant.webui.financemanagement.bulkratechange;

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

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
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
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.QueryBuilder;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.applicationmaster.Query;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.BulkProcessDetails;
import com.pennant.backend.model.finance.BulkProcessHeader;
import com.pennant.backend.model.finance.ScheduleMapDetails;
import com.pennant.backend.service.finance.BulkRateChangeProcessService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.webui.finance.enquiry.model.BulkChangeDialoglItemRenderer;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the 
 * WEB-INF/pages/FinanceManagement/SchdlRepayment/SchdlRepaymentDialog.zul <br/>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class BulkRateChangeDialogCtrl extends GFCBaseListCtrl<BulkProcessDetails> implements Serializable{

	private static final long serialVersionUID = 966281186831332116L;
	private final static Logger logger = Logger.getLogger(BulkRateChangeDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_BulkRateChangeDialog; 		// autowired
	protected Borderlayout borderlayout_BulkRate;
	protected Listbox listBoxBulkrateChangeDialog;	    // autowired
	protected Grid grid_BulkRateChange;					// autowired
	protected Groupbox 		gb_RateDetails;             // autowired
	protected Paging  pagingIjarahaFinancesList;         // autowired

	protected Decimalbox rateChange; 					// autowired
	protected Datebox fromDate;  						// autowired
	protected Datebox toDate;  							// autowired
	protected Datebox tillDate; 	 					// autowired
	protected Combobox cbReCalType; 					// autowired
	protected Label label_IjaraBulkRateChange_TillDate; // autowired
	protected Hbox hbox_TillDate;						// autowired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	protected Label 		recordStatus; 			// autoWired

	// For Bulk Deferment 
	protected Datebox calFromDate;                  	 // autoWired
	protected Datebox calToDate;                    	 // autoWired
	protected Checkbox exDefDate;                   	 // autoWired
	protected Combobox cbAddTermAfter;              	 // autoWired
	protected Tabpanel tabpanel_Query;              	 // autoWired
	protected Tabpanel tabpanel_IjarahaFinances;    	 // autoWired
	protected Tab   tab_Query;							 // autoWired
	protected Tab   tab_IjarahaFinance;					 // autoWired
	protected ExtendedCombobox ruleType;			     // autoWired
	protected Label label_BulkRateChangeDialog_RuleType; // autoWired
	protected Hbox  hbox_ruleType;						 // autoWired
	protected QueryBuilder rule;
	
	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_BulkRateChangeDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 		// autoWire
	protected Button btnEdit; 		// autoWire
	protected Button btnDelete; 	// autoWire
	protected Button btnSave; 		// autoWire
	protected Button btnCancel; 	// autoWire
	protected Button btnClose; 		// autoWire
	protected Button btnHelp; 		// autoWire
	protected Button btnNotes; 		// autoWire
	protected Button btnPreview; 	// autoWire
	protected Button btnProceed; 	// autoWire
	protected Button btnRecal; 	    // autoWire
	
    protected Listheader listheader_reCalStartDate; // autoWire
    protected Listheader listheader_reCalEndDate;   // autoWire
	
	//private FinanceDetailService financeDetailService;
	private List<BulkProcessDetails> rateChangeFinances;
	private BulkProcessHeader bulkProcessHeader;
	private BulkRateChangeListCtrl bulkRateChangeListCtrl ;
	private BulkRateChangeProcessService bulkRateChangeProcessService;
	private List<BulkProcessDetails> oldBulkProcessDetails = null;
	
	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient Date oldVar_fromDate;
	private transient Date oldVar_toDate;
	private transient BigDecimal oldVar_rateChange;
	private transient String oldVar_reCalType;
	private transient String oldVar_recordStatus;
	private boolean notes_Entered = false;
	
	private transient Date oldVar_calFromDate;
	private transient Date oldVar_calToDate;
	private transient boolean oldVar_exDefDate;
	private transient String oldVar_cbAddTermAfter;
	private transient String oldVar_ruleType;
	
	private Row recalFromDateRow;  // autoWire
	private Row excDefDateRow;     // autoWire
	private Row addTermRow;        // autoWire
	private Row rateChangeRow;     // autoWire
	
	/**
	 * default constructor.<br>
	 */
	public BulkRateChangeDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Rule object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_BulkRateChangeDialog(Event event) throws Exception {
		logger.debug("Entering"+event.toString());	
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("bulkProcessHeader")) {
			this.bulkProcessHeader = (BulkProcessHeader) args.get("bulkProcessHeader");
			BulkProcessHeader befImage = new BulkProcessHeader();
			BeanUtils.copyProperties(this.bulkProcessHeader, befImage);
			this.bulkProcessHeader.setBefImage(befImage);
			setBulkProcessHeader(this.bulkProcessHeader);
		} else {
			setBulkProcessHeader(null);
		}

		doLoadWorkFlow(this.bulkProcessHeader.isWorkflow(), this.bulkProcessHeader.getWorkflowId(), this.bulkProcessHeader.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "BulkProcessHeader");
		}

		/* set components visible dependent of the users rights */
		  doCheckRights();
		
		// READ OVERHANDED parameters !
		// we get the bulkProcessHeaderListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete bulkProcessHeader here.
		if (args.containsKey("bulkRateChangeListCtrl")) {
			setBulkRateChangeListCtrl((BulkRateChangeListCtrl) args.get("bulkRateChangeListCtrl"));
		} else {
			setBulkRateChangeListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		if(this.bulkProcessHeader.getBulkProcessFor().equals("D")){
			fillComboBox(this.cbReCalType, "", PennantStaticListUtil.getSchCalCodes(), ",CURPRD,ADDLAST,");
			this.tab_Query.setVisible(true);
		} else {
			fillComboBox(this.cbReCalType, "", PennantStaticListUtil.getSchCalCodes(), ",TILLDATE,ADDTERM,ADDLAST,ADJTERMS,");
			this.tab_Query.setVisible(true);
		}
		fillComboBox(this.cbAddTermAfter, "", PennantStaticListUtil.getAddTermCodes(), "");
		doShowDialog(getBulkProcessHeader());
		getVisibilityOfComponents(bulkProcessHeader);
		this.btnProceed.setVisible(false);
		logger.debug("Leaving" + event.toString());
	}
	
	public void getVisibilityOfComponents(BulkProcessHeader bulkProcessHeader){
		 if(bulkProcessHeader.getBulkProcessFor().equals("D")){
			// this.recalFromDateRow.setVisible(true);
			// this.excDefDateRow.setVisible(true);
			// this.addTermRow.setVisible(true);	
			 this.label_BulkRateChangeDialog_RuleType.setVisible(true);
			 this.hbox_ruleType.setVisible(true);
			 this.ruleType.setVisible(true);
			 
			this.ruleType.setMaxlength(15);
			this.ruleType.setMandatoryStyle(true);
			this.ruleType.setModuleName("Query");
			this.ruleType.setValueColumn("QueryCode");
			this.ruleType.setDescColumn("QueryDesc");
			this.ruleType.setValidateColumns(new String[] { "QueryCode" });
			
		} else if(bulkProcessHeader.getBulkProcessFor().equals("R")) {
			// this.recalFromDateRow.setVisible(false);
			// this.excDefDateRow.setVisible(false);
			 //this.addTermRow.setVisible(false);	
			 this.rateChangeRow.setVisible(true);
		} 
	}
	
	public void doCheckRights(){
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("BulkProcessHeader", getRole());

		this.btnProceed.setVisible(getUserWorkspace().isAllowed("button_BulkProcessHeader_btnProceed"));
		this.btnPreview.setVisible(getUserWorkspace().isAllowed("button_BulkProcessHeader_btnPreview"));
		//this.btnRecal.setVisible(getUserWorkspace().isAllowed("button_BulkProcessHeader_btnRecal"));
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
		}else{
			Query details= (Query) dataObject;
			/*Set FinanceWorkFloe object*/
			if (details != null) {
				this.ruleType.setValue(details.getQueryCode());
				this.ruleType.setDescription(details.getQueryDesc());
				this.rule.setEditable(false);
				this.rule.setSqlQuery(details.getSQLQuery());
			}
		}
		logger.debug("Leaving " + event.toString());
	}
	
	
	public void doShowDialog(BulkProcessHeader aBulkProcessHeader) throws InterruptedException {
		logger.debug("Entering");

		logger.debug("Entering");
		// if aAcademic == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aBulkProcessHeader == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			//aBulkProcessHeader = getAcademicService().getNewAcademic(); TODO

			setBulkProcessHeader(aBulkProcessHeader);
		} else {
			setBulkProcessHeader(aBulkProcessHeader);
		}
		// set ReadOnly mode accordingly if the object is new or not.
		if (aBulkProcessHeader.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.fromDate.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.fromDate.focus();
				if (!StringUtils.trimToEmpty(aBulkProcessHeader.getRecordType()).equals("")) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				//this.btnCtrl.setInitEdit();
				//doReadOnly();
				//btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aBulkProcessHeader);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_BulkRateChangeDialog);
		} catch (final Exception e) {
			logger.error("doShowDialog() " + e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
	
		logger.debug("Leaving");
	}
	
	
	/*
	 *  onSelect Event For Finances Tab
	 */
	public void onSelect$tab_IjarahaFinance(Event event){
		logger.debug("Entering "+event);
		if(!StringUtils.trimToEmpty(getBulkProcessHeader().getRecordStatus()).equals(PennantConstants.RCD_STATUS_SAVED)
			&& !StringUtils.trimToEmpty(getBulkProcessHeader().getRecordStatus()).equals(PennantConstants.RCD_STATUS_SUBMITTED)){
			fillIjarahaFinances();
		}
		logger.debug("Leaving "+event);
	}
	
	
	
	/*
	 *  Writing Bean Values to Components
	 */
	public void doWriteBeanToComponents(BulkProcessHeader aBulkProcessHeader){
		logger.debug("Entering");
		if(aBulkProcessHeader.getFromDate() != null){
	        this.fromDate.setValue(DateUtility.getDBDate(DateUtility.formatDate(aBulkProcessHeader.getFromDate(), PennantConstants.DBDateFormat)));
		} else {
	        this.fromDate.setText("");
		}
		if(aBulkProcessHeader.getToDate() != null){
			this.toDate.setValue(DateUtility.getDBDate(DateUtility.formatDate(aBulkProcessHeader.getToDate(), PennantConstants.DBDateFormat)));
		} else {
			this.toDate.setText("");
		}
        this.rateChange.setValue(aBulkProcessHeader.getNewProcessedRate() == null ? BigDecimal.ZERO : aBulkProcessHeader.getNewProcessedRate());
      
        if(aBulkProcessHeader.getReCalType() != null){
        	if(this.bulkProcessHeader.getBulkProcessFor().equals("D")){
        		fillComboBox(this.cbReCalType, StringUtils.trimToEmpty(aBulkProcessHeader.getReCalType()), PennantStaticListUtil.getSchCalCodes(), ",CURPRD,ADDLAST,");
        	    this.ruleType.setValue(aBulkProcessHeader.getRuleType());
        	    this.rule.setSqlQuery(aBulkProcessHeader.getLovDescSqlQuery());
        	} else {
        		fillComboBox(this.cbReCalType, StringUtils.trimToEmpty(aBulkProcessHeader.getReCalType()), PennantStaticListUtil.getSchCalCodes(), ",TILLDATE,ADDTERM,ADDLAST,ADJTERMS,");
        	}
        } 
        if(aBulkProcessHeader.getBulkProcessDetailsList() != null) {
		getBulkProcessHeader().setBulkProcessDetailsList(aBulkProcessHeader.getBulkProcessDetailsList());
		this.oldBulkProcessDetails = aBulkProcessHeader.getBulkProcessDetailsList();
		this.listBoxBulkrateChangeDialog.setHeight(Integer.parseInt(getListBoxHeight(grid_BulkRateChange.getRows().getVisibleItemCount()).
				substring(0,getListBoxHeight(grid_BulkRateChange.getRows().getVisibleItemCount()).indexOf("px"))) - 50 + "px");
		this.pagingIjarahaFinancesList.setPageSize(Integer.parseInt(getListBoxHeight(grid_BulkRateChange.getRows().getVisibleItemCount()).
				substring(0,getListBoxHeight(grid_BulkRateChange.getRows().getVisibleItemCount()).indexOf("px"))) - 50 );
		
		getPagedListWrapper().initList(getBulkProcessHeader().getBulkProcessDetailsList(), this.listBoxBulkrateChangeDialog, this.pagingIjarahaFinancesList);
		this.listBoxBulkrateChangeDialog.setItemRenderer(new BulkChangeDialoglItemRenderer(!getUserWorkspace().isAllowed("BulkProcessHeader_isDeferedItemDisabled")));
		
        }
    	 
		if (this.recalFromDateRow.isVisible()) {
			if (aBulkProcessHeader.getReCalFromDate() != null) {
				this.calFromDate.setValue(DateUtility.getDBDate(DateUtility.formatDate(aBulkProcessHeader.getReCalFromDate(), PennantConstants.DBDateFormat)));
			} else {
				this.calFromDate.setText("");
			}
			if (aBulkProcessHeader.getReCalType().equals(CalculationConstants.RPYCHG_TILLDATE)) {
				if (aBulkProcessHeader.getReCalToDate() != null) {
					this.calToDate.setValue(DateUtility.getDBDate(DateUtility.formatDate(aBulkProcessHeader.getReCalToDate(), PennantConstants.DBDateFormat)));
				} else {
					this.calToDate.setText("");
				}
			} else if(aBulkProcessHeader.getReCalType().equals(CalculationConstants.RPYCHG_TILLMDT)){
				this.calToDate.setDisabled(true);
			}
		}
        	
    	if(this.excDefDateRow.isVisible()){
    		this.exDefDate.setChecked(bulkProcessHeader.isExcludeDeferement());
    	}
    	if(this.addTermRow.isVisible()){
    		fillComboBox(this.cbAddTermAfter, StringUtils.trimToEmpty(bulkProcessHeader.getAddTermAfter()), PennantStaticListUtil.getAddTermCodes(), "");
    	}
        
    	if (StringUtils.trimToEmpty(getBulkProcessHeader().getReCalType()).equals(CalculationConstants.RPYCHG_TILLDATE) || 
    			StringUtils.trimToEmpty(getBulkProcessHeader().getReCalType()).equals(CalculationConstants.RPYCHG_TILLMDT)) {
			this.listheader_reCalStartDate.setVisible(true);
			this.listheader_reCalEndDate.setVisible(true);
    	} else {
			this.listheader_reCalStartDate.setVisible(true);
			this.listheader_reCalEndDate.setVisible(true);
    	}
    	
		logger.debug("Leaving");
	}
	
	
	/*
	 *  Writing Component Values to Bean
	 */
	public void doWriteComponentsToBean(BulkProcessHeader aBulkProcessHeader){
		logger.debug("Entering");

		// Committed  for test 
		aBulkProcessHeader.setFromDate(DateUtility.getDBDate(DateUtility.formatDate(this.fromDate.getValue(), PennantConstants.DBDateFormat)));
		aBulkProcessHeader.setToDate(DateUtility.getDBDate(DateUtility.formatDate(this.toDate.getValue(), PennantConstants.DBDateFormat)));
		if(this.rateChangeRow.isVisible()){
			aBulkProcessHeader.setNewProcessedRate(this.rateChange.getValue());
		}
		
		aBulkProcessHeader.setReCalType(this.cbReCalType.getSelectedItem().getValue().toString());

		if(this.recalFromDateRow.isVisible()){
			if(this.calFromDate.getValue() != null){
				   aBulkProcessHeader.setReCalFromDate(DateUtility.getDBDate(DateUtility.formatDate(this.calFromDate.getValue(), PennantConstants.DBDateFormat)));
			}
			if(this.calToDate.getValue() != null){
				aBulkProcessHeader.setReCalToDate(DateUtility.getDBDate(DateUtility.formatDate(this.calToDate.getValue(), PennantConstants.DBDateFormat)));
			}
		}
		
		if (this.ruleType.isVisible()) {
			aBulkProcessHeader.setRuleType(this.ruleType.getValue());
		}
		
		if(this.excDefDateRow.isVisible()){
			aBulkProcessHeader.setExcludeDeferement(this.exDefDate.isChecked());
		}
		
		if(aBulkProcessHeader.getReCalType().equals(CalculationConstants.RPYCHG_ADDTERM)){
			aBulkProcessHeader.setAddTermAfter(this.cbAddTermAfter.getSelectedItem().getValue().toString());
		}
		
		if(this.listBoxBulkrateChangeDialog.getItems() != null){
			for(int i=0; i<this.listBoxBulkrateChangeDialog.getItems().size(); i++){
				BulkProcessDetails selectedBulkProcessDetails = null;
				if(this.listBoxBulkrateChangeDialog.getSelectedItems().contains(this.listBoxBulkrateChangeDialog.getItems().get(i))){
					 selectedBulkProcessDetails = (BulkProcessDetails) this.listBoxBulkrateChangeDialog.getItems().get(i).getAttribute("data");
					 for(BulkProcessDetails bulkProcessDetails : aBulkProcessHeader.getBulkProcessDetailsList()){
						 if(selectedBulkProcessDetails != null && 
							selectedBulkProcessDetails.getFinReference().equals(bulkProcessDetails.getFinReference())
							&& selectedBulkProcessDetails.getDeferedSchdDate().compareTo(bulkProcessDetails.getDeferedSchdDate()) == 0){
							 bulkProcessDetails.setAlwProcess(false);
							 break;
						 } 
					 }
				} else {
					selectedBulkProcessDetails = (BulkProcessDetails) this.listBoxBulkrateChangeDialog.getItems().get(i).getAttribute("data");
					for(BulkProcessDetails bulkProcessDetails : aBulkProcessHeader.getBulkProcessDetailsList()){
						 if(selectedBulkProcessDetails != null && 
							selectedBulkProcessDetails.getFinReference().equals(bulkProcessDetails.getFinReference())
							&& selectedBulkProcessDetails.getDeferedSchdDate().compareTo(bulkProcessDetails.getDeferedSchdDate()) == 0){
							 bulkProcessDetails.setAlwProcess(true);
							 break;
						 } 
					 }
				}
			}
		}
		
		if(aBulkProcessHeader.getBulkProcessDetailsList() != null && aBulkProcessHeader.getBulkProcessDetailsList().size() > 0){
			if (aBulkProcessHeader.isNewRecord()) {
				for (BulkProcessDetails bulkProcessDetails : aBulkProcessHeader.getBulkProcessDetailsList()) {
					bulkProcessDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					bulkProcessDetails.setRecordStatus(PennantConstants.RCD_ADD);
					bulkProcessDetails.setWorkflowId(aBulkProcessHeader.getWorkflowId());
					bulkProcessDetails.setNewRecord(true);
				}
			}
		}
		
		logger.debug("Leaving");
	}
	
	
	/*
	 *  Method for Storing Default Values
	 */
	public void doStoreInitValues(){
		logger.debug("Entering");

		this.oldVar_fromDate = this.fromDate.getValue();
		this.oldVar_toDate = this.toDate.getValue();
		if(this.rateChangeRow.isVisible()){
			this.oldVar_rateChange = this.rateChange.getValue();
		}
		this.oldVar_recordStatus = this.recordStatus.getValue();
		if(this.recalFromDateRow.isVisible()){
			this.oldVar_calFromDate = this.calFromDate.getValue();
			this.oldVar_calToDate = this.calToDate.getValue();
		}
		if(this.addTermRow.isVisible()){
			if(this.cbAddTermAfter.getSelectedItem() != null) {
			this.oldVar_cbAddTermAfter = this.cbAddTermAfter.getSelectedItem().getValue().toString();  
			} else {
				this.oldVar_cbAddTermAfter = "";  
			}
		}
		if(this.excDefDateRow.isVisible()){
			this.oldVar_exDefDate = this.exDefDate.isChecked();
		}
		
		if(this.ruleType.isVisible()){
			this.oldVar_ruleType = this.ruleType.getValue();
		} else {
			this.oldVar_ruleType = "";
		}
		
		logger.debug("Leaving");
	}
	
	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		
		this.fromDate.setValue(this.oldVar_fromDate);
		this.toDate.setValue(this.oldVar_toDate);
		if(this.rateChangeRow.isVisible()){
		this.rateChange.setValue(this.oldVar_rateChange);
		}
		this.recordStatus.setValue(this.oldVar_recordStatus);
		if(this.recalFromDateRow.isVisible()){
			this.calFromDate.setValue(this.oldVar_calFromDate);
			this.calToDate.setValue(this.oldVar_calToDate);
		}
		if(this.addTermRow.isVisible()){
		    this.cbAddTermAfter.setValue(this.oldVar_cbAddTermAfter);  
		}
		if(this.excDefDateRow.isVisible()){
			 this.exDefDate.setChecked(this.oldVar_exDefDate);
		}
		
		if(this.ruleType.isVisible()){
			this.ruleType.setValue(this.oldVar_ruleType);
		} 
		
		if (isWorkFlowEnabled()) {
			this.userAction.setSelectedIndex(0);
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
			//this.btnCancel.setVisible(false);
		} else {
			readOnlyComponent(true, this.fromDate);
			readOnlyComponent(true, this.toDate);
			readOnlyComponent(true, this.tillDate);
			readOnlyComponent(true, this.cbReCalType);
			//this.btnCancel.setVisible(true);
		}

		readOnlyComponent(isReadOnly("BulkProcessHeader_fromDate"), this.fromDate);
		readOnlyComponent(isReadOnly("BulkProcessHeader_toDate"), this.toDate);
		readOnlyComponent(isReadOnly("BulkProcessHeader_tillDate"), this.tillDate);
		readOnlyComponent(isReadOnly("BulkProcessHeader_cbReCalType"), this.cbReCalType);
		readOnlyComponent(isReadOnly("BulkProcessHeader_ruleTyp"), this.ruleType);
		
		if(StringUtils.trimToEmpty(getBulkProcessHeader().getReCalType()).equals(CalculationConstants.RPYCHG_TILLDATE) ||
		   StringUtils.trimToEmpty(getBulkProcessHeader().getReCalType()).equals(CalculationConstants.RPYCHG_TILLMDT)){
			this.recalFromDateRow.setVisible(true);
			readOnlyComponent(isReadOnly("BulkProcessHeader_reCalFromDate"), this.calFromDate);
			if(!StringUtils.trimToEmpty(getBulkProcessHeader().getReCalType()).equals(CalculationConstants.RPYCHG_TILLMDT)){
				readOnlyComponent(isReadOnly("BulkProcessHeader_reCalToDate"), this.calToDate);
			}
		} else if(StringUtils.trimToEmpty(getBulkProcessHeader().getReCalType()).equals(CalculationConstants.RPYCHG_ADDTERM)){
			//this.addTermRow.setVisible(true);
			readOnlyComponent(isReadOnly("BulkProcessHeader_cbAddTermAfter"), this.cbAddTermAfter);
		}
		
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.bulkProcessHeader.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				//btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			//btnCancel.setVisible(true);
		}
		logger.debug("Leaving ");
	}
	
	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		if(this.rateChangeRow.isVisible()){
			this.rateChange.setMaxlength(13);
			this.rateChange.setFormat(PennantConstants.rateFormate9);
			this.rateChange.setRoundingMode(BigDecimal.ROUND_DOWN);
			this.rateChange.setScale(9);
		}
			
		this.calFromDate.setFormat(PennantConstants.dateFormat);
		this.calToDate.setFormat(PennantConstants.dateFormat);
		this.tillDate.setFormat(PennantConstants.dateFormat);
		this.fromDate.setFormat(PennantConstants.dateFormat);
		this.toDate.setFormat(PennantConstants.dateFormat);
		
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
		if (this.bulkProcessHeader.getBulkProcessFor().equals("D")) {
			if (recalType.equals(CalculationConstants.RPYCHG_TILLDATE) || recalType.equals(CalculationConstants.RPYCHG_TILLMDT)) {
				// throwValidation();
				if(recalType.equals(CalculationConstants.RPYCHG_TILLDATE)){
					setEventDatesValidation(this.toDate.getValue());
				}
				this.recalFromDateRow.setVisible(true);
				this.excDefDateRow.setVisible(true);
				this.addTermRow.setVisible(false);
				this.cbAddTermAfter.setSelectedIndex(0);
				if (recalType.equals(CalculationConstants.RPYCHG_TILLMDT)) {
					this.calToDate.setDisabled(true);
					this.calToDate.setConstraint("");
				} else {
					this.calToDate.setDisabled(false);
				}
			} else if (recalType.equals(CalculationConstants.RPYCHG_ADDTERM)) {
				this.addTermRow.setVisible(false);
				this.cbAddTermAfter.setSelectedIndex(1);
				this.recalFromDateRow.setVisible(false);
				this.excDefDateRow.setVisible(false);
				this.exDefDate.setChecked(false);
			} else {
				this.recalFromDateRow.setVisible(false);
				this.excDefDateRow.setVisible(false);
				this.exDefDate.setChecked(false);
				this.addTermRow.setVisible(false);
				this.cbAddTermAfter.setSelectedIndex(0);
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/*
	 *  onChange Event For toDate(Date Box)
	 */
	public void onChange$toDate(Event event) {
		logger.debug("Entering" + event.toString());
		setEventDatesValidation(this.toDate.getValue());
		fillIjarahaFinances();
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
			if (!this.calToDate.isDisabled()) {
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
		doResetInitValues();
		doReadOnly(true);
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
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aBulkProcessHeader.getRecordType()).equals("")) {
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
					closeDialog(this.window_BulkRateChangeDialog, "BulkProcessHeader");
				}
			} catch (DataAccessException e) {
				logger.error(e);
				showMessage(e);
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

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if(!userAction.getSelectedItem().getValue().toString().equals(PennantConstants.RECORD_TYPE_CAN)){
			throwValidation();		
		}
		// fill the Academic object with the components data
		doWriteComponentsToBean(aBulkProcessHeader);
		
		if(aBulkProcessHeader.getBulkProcessDetailsList() != null && aBulkProcessHeader.getBulkProcessDetailsList().size() == 0){
			final String msg = "No Records Found For Processing Bulk Deferment";
					try {
						PTMessageUtils.showErrorMessage(msg);
					} catch (InterruptedException e) {
						e.printStackTrace();
						return;
					}
					return;
		}
		
		BulkProcessHeader tempaBulkProcessHeader= getBulkRateChangeProcessService().getBulkProcessHeaderByFromAndToDates(aBulkProcessHeader.getFromDate(), aBulkProcessHeader.getToDate(), "_Temp");
		if (tempaBulkProcessHeader != null) {
			if(!tempaBulkProcessHeader.getRecordStatus().equals(StringUtils.trimToEmpty(aBulkProcessHeader.getRecordStatus()))){
				if (isValidationrequired()) {
					final String msg = "Bulk Deferment with from Date " + aBulkProcessHeader.getFromDate() + " And To date " + aBulkProcessHeader.getToDate() + " Already in process";
					try {
						PTMessageUtils.showErrorMessage(msg);
					} catch (InterruptedException e) {
						e.printStackTrace();
						return;
					}
					return;
				}
			}
		}
		tempaBulkProcessHeader = null;
		
		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aBulkProcessHeader.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aBulkProcessHeader.getRecordType()).equals("")) {
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
				closeDialog(this.window_BulkRateChangeDialog, "BulkProcessHeader");
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
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

		aBulkProcessHeader.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aBulkProcessHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aBulkProcessHeader.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aBulkProcessHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aBulkProcessHeader.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aBulkProcessHeader);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aBulkProcessHeader))) {
					try {
						if (!isNotes_Entered()) {
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						logger.error(e);
						e.printStackTrace();
					}
				}
			}
			if (!StringUtils.trimToEmpty(nextTaskId).equals("")) {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode + ",";
						}
						nextRoleCode = getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aBulkProcessHeader.setTaskId(taskId);
			aBulkProcessHeader.setNextTaskId(nextTaskId);
			aBulkProcessHeader.setRoleCode(getRole());
			aBulkProcessHeader.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aBulkProcessHeader, tranType);
			String operationRefs = getWorkFlow().getOperationRefs(taskId, aBulkProcessHeader);

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
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}
	
	// Check notes Entered or not
		public void setNotes_entered(String notes) {
			logger.debug("Entering");
			if (!isNotes_Entered()) {
				if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
					setNotes_Entered(true);
				} else {
					setNotes_Entered(false);
				}
			}
			logger.debug("Leaving");
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

						if (aBulkProcessHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getBulkRateChangeProcessService().doReject(auditHeader);

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
						deleteNotes(getNotes(), true);
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
			e.printStackTrace();
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
	
	
	// Method for refreshing the list after successful updation
		private void refreshList() {
			logger.debug("Entering");
			final JdbcSearchObject<BulkProcessHeader> soBulkProcessHeader = getBulkRateChangeListCtrl().getSearchObj();
			getBulkRateChangeListCtrl().pagingBulkRateChangeList.setActivePage(0);
			getBulkRateChangeListCtrl().getPagedListWrapper().setSearchObject(soBulkProcessHeader);
			if (getBulkRateChangeListCtrl().listBoxBulkRateChange != null) {
				getBulkRateChangeListCtrl().listBoxBulkRateChange.getListModel();
			}
			logger.debug("Leaving");
		}
	
	
	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_BulkRateChangeDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
		logger.debug("Leaving");
	}
	
	
	
	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {
			doClose();
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}
	
	
	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_BulkRateChangeDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	/**
	 * Get the Reference value
	 */
	private String getReference(){
		return String.valueOf(getBulkProcessHeader().getBulkProcessId());
	}
	
	
	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 */
	private void doClose() throws InterruptedException {
		logger.debug("Entering");
		boolean close = true;

		if (isDataChanged()) {
			logger.debug("doClose isDataChanged : true");
			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
			//	doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("doClose isDataChanged : False");
		}

		if (close) {
			closeDialog(this.window_BulkRateChangeDialog, "BulkProcessHeader");
		}

		logger.debug("Leaving");
	}
	
	
	
	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		// To clear the Error Messages
		doClearMessage();

		if (this.oldVar_fromDate != this.fromDate.getValue()) {
			return true;
		}
		if (this.oldVar_toDate != this.toDate.getValue()) {
			return true;
		}
		if (this.oldVar_rateChange != this.rateChange.getValue()) {
			return true;
		}
		
		if (this.oldVar_reCalType != this.cbReCalType.getSelectedItem().getValue().toString()) {
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
		PTMessageUtils.showHelpWindow(event, window_BulkRateChangeDialog);
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
		throwValidation();
		fillIjarahaFinances();
		this.btnPreview.setVisible(false);
		this.btnProceed.setVisible(false);
		this.btnRecal.setVisible(true);
		logger.debug("Leaving" +event.toString());
	}
	
	/**
	 * Method for recal culating list
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnRecal(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		this.btnPreview.setVisible(true);
		//this.btnProceed.setDisabled(true);
		this.btnRecal.setVisible(false);
		doReadOnly(false);
		logger.debug("Leaving" +event.toString());
	}
	
	public void fillIjarahaFinances() {
		logger.debug("Entering");
	    throwValidation();
		doWriteComponentsToBean(getBulkProcessHeader());
		
		if (getRole().equals("MSTGRP1_MAKER")
				&& !StringUtils.trimToEmpty(this.bulkProcessHeader.getRecordStatus()).equals(PennantConstants.RCD_STATUS_APPROVED)) {

			Date fromDate = DateUtility.getDBDate(DateUtility.formatDate(this.fromDate.getValue(), PennantConstants.DBDateFormat));
			Date toDate = DateUtility.getDBDate(DateUtility.formatDate(this.toDate.getValue(), PennantConstants.DBDateFormat));
			
			if (this.bulkProcessHeader.getBulkProcessFor().equals("R")) {
				setRateChangeFinances(getBulkRateChangeProcessService().getIjaraBulkRateFinList(fromDate, toDate));
				getBulkProcessHeader().setBulkProcessDetailsList(getBulkRateChangeProcessService().getIjaraBulkRateFinList(fromDate, toDate));
			} else if (this.bulkProcessHeader.getBulkProcessFor().equals("D")) {
				String whereClause = StringUtils.trimToEmpty(this.rule.getSqlQuery())+" AND "+getUsrFinAuthenticationQry(false);
				getBulkProcessHeader().setBulkProcessDetailsList(getBulkRateChangeProcessService().getBulkDefermentFinList(fromDate, toDate, whereClause));
			}
			
			if(this.oldBulkProcessDetails != null && this.oldBulkProcessDetails.equals(getBulkProcessHeader().getBulkProcessDetailsList())){
				getBulkProcessHeader().setLovDescIsOlddataChanged(false);
			} else {
				getBulkProcessHeader().setLovDescIsOlddataChanged(true);
			}
			
			
			if (getBulkProcessHeader().getReCalType().equals(CalculationConstants.RPYCHG_TILLDATE) || 
					getBulkProcessHeader().getReCalType().equals(CalculationConstants.RPYCHG_TILLMDT)) {
				List<ScheduleMapDetails> scheduleMapDetailsList = getBulkRateChangeProcessService().getDeferedDates(getBulkProcessHeader().getBulkProcessDetailsList(), 
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

			this.listBoxBulkrateChangeDialog.setHeight(Integer.parseInt(getListBoxHeight(grid_BulkRateChange.getRows().getVisibleItemCount()).substring(0, getListBoxHeight(grid_BulkRateChange.getRows().getVisibleItemCount()).indexOf("px"))) - 50 + "px");
			this.pagingIjarahaFinancesList.setPageSize(Integer.parseInt(getListBoxHeight(grid_BulkRateChange.getRows().getVisibleItemCount()).substring(0, getListBoxHeight(grid_BulkRateChange.getRows().getVisibleItemCount()).indexOf("px"))) - 50);

			getPagedListWrapper().initList(getBulkProcessHeader().getBulkProcessDetailsList(), this.listBoxBulkrateChangeDialog, this.pagingIjarahaFinancesList);
			this.listBoxBulkrateChangeDialog.setItemRenderer(new BulkChangeDialoglItemRenderer(!getUserWorkspace().isAllowed("BulkProcessHeader_isDeferedItemDisabled")));
			doReadOnly(true);
			this.btnPreview.setVisible(false);
			this.btnRecal.setVisible(true);
			
			if(getBulkProcessHeader().getBulkProcessDetailsList() == null || getBulkProcessHeader().getBulkProcessDetailsList().size() == 0){
				try {
					PTMessageUtils.showErrorMessage(" No Finances Founded With Schedule Term In Between "+
							PennantAppUtil.formateDate(this.fromDate.getValue(), PennantConstants.dateFormate)+" AND "+
							PennantAppUtil.formateDate(this.toDate.getValue(), PennantConstants.dateFormate));
				} catch (WrongValueException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}
		}
		logger.debug("Entering");
	}
	
	
	public void throwValidation(){
		logger.debug("Entering");

		doSetValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		Date appDate = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");
		try {
			if(this.fromDate.getValue() != null  && (this.fromDate.getValue().compareTo(appDate) != -1)) {
				
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.toDate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if(this.rateChangeRow.isVisible()){
			try {
				this.rateChange.getValue();
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		
		try{
			isValidComboValue(this.cbReCalType, Labels.getLabel("label_IjaraBulkRateChange_RecalType.value"));
		}catch (WrongValueException we) {
			wve.add(we);
		}

		if(this.hbox_TillDate.isVisible()){
			try {
				if(this.tillDate.getValue().before(this.fromDate.getValue()) || this.tillDate.getValue().after(this.toDate.getValue())){
					throw new WrongValueException(this.tillDate, Labels.getLabel("DATE_RANGE", new String[]{
							Labels.getLabel("label_IjaraBulkRateChange_TillDate.value"),
							PennantAppUtil.formateDate(this.fromDate.getValue(), PennantConstants.dateFormate), 
							PennantAppUtil.formateDate(this.toDate.getValue(), PennantConstants.dateFormate) }));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		if (bulkProcessHeader.getBulkProcessFor().equals("D")) {
			try {
				this.calFromDate.getValue();
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				this.calToDate.getValue();
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		
		try{
			if(this.cbReCalType.getSelectedItem() != null && 
					this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_ADDTERM)){
				isValidComboValue(this.cbAddTermAfter, Labels.getLabel("label_BulkDefferment_AddTermAfter.value"));
			}
		}catch (WrongValueException we) {
			wve.add(we);
		}

		try{
			if(this.tab_Query.isVisible() && StringUtils.trimToEmpty(this.ruleType.getValue()).equals("")){
				throw new WrongValueException(this.ruleType, "NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
						new String[]{Labels.getLabel("label_BulkDefferment_Query.value")}));
			}
		}catch (WrongValueException we) {
			wve.add(we);
		}
		
		doRemoveValidation();
		
		if (wve.size() > 0) {
			doRemoveValidation();
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		//Clear Error Messages
		doClearMessage();

		logger.debug("Leaving");
	}
	
	
	
	/**
	 * Method for List Preview for Condition List
	 * @param event
	 * @throws InterruptedException
	 * @throws AccountNotFoundException 
	 * @throws WrongValueException 
	 */
	public void onClick$btnProceed(Event event) throws InterruptedException, WrongValueException, AccountNotFoundException {
		logger.debug("Entering" +event.toString());
		throwValidation();		
		
		this.btnProceed.setDisabled(true);
		
		//Processing Fetch Finance Details for IJARAH Bulk Rate Changes
		boolean success = getBulkRateChangeProcessService().bulkRateChangeFinances(getRateChangeFinances(),
				this.cbReCalType.getSelectedItem().getValue().toString(), this.rateChange.getValue());
		
		//TODO -- need to check Process failure case
		if(success){
			MultiLineMessageBox.show("Bulk Rate Application Process Succeed for Finance Count " +getRateChangeFinances().size());
		}else{
			
		}
		
		logger.debug("Leaving" +event.toString());
	}
	
	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		
		if (isValidationrequired()) {
			this.fromDate.setConstraint("NO EMPTY,NO PAST:"+ Labels.getLabel(
					"DATE_EMPTY_PAST",new String[] { Labels.getLabel("label_IjaraBulkRateChange_FromDate.value") }));
		} else {
			this.fromDate.setConstraint(new PTDateValidator(Labels.getLabel("label_IjaraBulkRateChange_FromDate.value"), true));
		}

		if(isValidationrequired()){
			this.toDate.setConstraint("NO EMPTY,NO PAST:"+ Labels.getLabel("DATE_EMPTY_PAST",
					new String[]{Labels.getLabel("label_IjaraBulkRateChange_ToDate.value")}));
		} else {
			this.toDate.setConstraint(new PTDateValidator(Labels.getLabel("label_IjaraBulkRateChange_ToDate.value"), true));
		}

		if(!this.rateChange.isDisabled()){
			this.rateChange.setConstraint("NO ZERO, NO NEGATIVE:" + Labels.getLabel("RATE_NO_LESS_ZERO",
					new String[]{Labels.getLabel("label_IjaraBulkRateChange_Rate.value")}));
		}

		if(this.hbox_TillDate.isVisible()){
			if(!this.tillDate.isDisabled()){
				this.tillDate.setConstraint("NO EMPTY,NO PAST:"+ Labels.getLabel("DATE_EMPTY_PAST",
						new String[]{Labels.getLabel("label_IjaraBulkRateChange_TillDate.value")}));
			}
		}
		
		if(this.recalFromDateRow.isVisible()) {

			if(!this.calFromDate.isDisabled()){
				this.calFromDate.setConstraint("NO EMPTY,NO PAST:"+ Labels.getLabel("DATE_EMPTY_PAST",
						new String[]{Labels.getLabel("label_BulkDefferment_CalFromDate.value")}));
			}

			if(!this.calToDate.isDisabled()){
				this.calToDate.setConstraint("NO EMPTY,NO PAST:"+ Labels.getLabel("DATE_EMPTY_PAST",
						new String[]{Labels.getLabel("label_BulkDefferment_CalToDate.value")}));
			}

		}
				
		if(this.ruleType.isVisible() && !this.ruleType.isReadonly()){
			this.ruleType.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
					new String[]{Labels.getLabel("label_BulkRateChangeDialog_RuleType.value")}));
		}
		
		
		logger.debug("Leaving");
	}
	
	
	public boolean isValidationrequired(){
		if(this.userAction.getSelectedItem().getValue().toString().equalsIgnoreCase(PennantConstants.RCD_STATUS_CANCELLED)
			|| this.userAction.getSelectedItem().getValue().toString().equalsIgnoreCase(PennantConstants.RCD_STATUS_REJECTED)
			|| this.userAction.getSelectedItem().getValue().toString().equalsIgnoreCase(PennantConstants.RCD_STATUS_RESUBMITTED)){
			return false;
		} else{
		   return true;
		}
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean isReadOnly) {
		logger.debug("Entering");
		this.rateChange.setDisabled(isReadOnly);
		this.cbReCalType.setDisabled(isReadOnly);
		this.tillDate.setDisabled(isReadOnly);
		this.fromDate.setDisabled(isReadOnly);
		this.toDate.setDisabled(isReadOnly);
		this.ruleType.setReadonly(isReadOnly);
		this.calFromDate.setDisabled(isReadOnly);
	    this.calToDate.setDisabled(isReadOnly);
		if(this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_TILLMDT)){
		    this.calToDate.setDisabled(true);
		    this.calToDate.setConstraint("");
		    
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

		logger.debug("Leaving");
	}
	/**
	 * Method to clear error messages
	 * 
	 * */
	private void doClearMessage() {
		logger.debug("Entering");
		this.rateChange.clearErrorMessage();
		this.tillDate.clearErrorMessage();
		this.fromDate.clearErrorMessage();
		this.toDate.clearErrorMessage();
		this.cbReCalType.clearErrorMessage();
		this.ruleType.clearErrorMessage();
		logger.debug("Leaving");
	}
	
	// Get the notes entered for rejected reason
		private Notes getNotes() {
			logger.debug("Entering");
			Notes notes = new Notes();
			notes.setModuleName("BulkProcessDialog");
			notes.setReference(getReference());
			notes.setVersion(getBulkProcessHeader().getVersion());
			notes.setRoleCode(getRole());
			logger.debug("Leaving");
			return notes;
		}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ Getters & Setters +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
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

	public BulkRateChangeListCtrl getBulkRateChangeListCtrl() {
		return bulkRateChangeListCtrl;
	}

	public void setBulkRateChangeListCtrl(BulkRateChangeListCtrl bulkRateChangeListCtrl) {
		this.bulkRateChangeListCtrl = bulkRateChangeListCtrl;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

	public BulkRateChangeProcessService getBulkRateChangeProcessService() {
		return bulkRateChangeProcessService;
	}

	public void setBulkRateChangeProcessService(BulkRateChangeProcessService bulkRateChangeProcessService) {
		this.bulkRateChangeProcessService = bulkRateChangeProcessService;
	}

}