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
 * FileName    		:  FeeReceiptDialogCtrl.java                           
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
package com.pennant.webui.financemanagement.receipts;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.AccountSelectionBox;
import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.AccountingSet;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.finance.FeeReceiptService;
import com.pennant.backend.service.rmtmasters.AccountingSetService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.component.Uppercasebox;
import com.pennant.core.EventManager;
import com.pennant.core.EventManager.Notify;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.finance.financemain.AccountingDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;
import com.rits.cloning.Cloner;

/**
 * This is the controller class for the WEB-INF/pages/FinanceManagement/Receipts/FeeReceiptDialog.zul
 */
public class FeeReceiptDialogCtrl extends GFCBaseCtrl<FinReceiptHeader> {
	private static final long								serialVersionUID					= 966281186831332116L;
	private static final Logger								logger								= Logger.getLogger(FeeReceiptDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window										window_FeeReceiptDialog;
	protected Borderlayout									borderlayout_FeeReceipt;

	//Receipt Details
	protected ExtendedCombobox								finType;
	protected ExtendedCombobox								finReference;
	protected ExtendedCombobox								finCcy;
	protected ExtendedCombobox								finBranch;
	protected Textbox										custCIF;
	protected Textbox										custName;
	protected Groupbox										gb_FeeDetail;
	protected Listbox										listBoxFeeDetail;

	protected Combobox										receiptPurpose;
	protected Combobox										excessAdjustTo;
	protected Combobox										receiptMode;
	protected CurrencyBox									receiptAmount;
	protected Combobox										allocationMethod;
	protected Row											row_RealizationDate;
	protected Datebox										realizationDate;
	
	protected Groupbox										gb_ReceiptDetails;
	protected Caption										caption_receiptDetail;
	protected Label											label_FeeReceiptDialog_favourNo;
	protected Uppercasebox									favourNo;
	protected Datebox										valueDate;
	protected ExtendedCombobox								bankCode;
	protected Textbox										favourName;
	protected Datebox										depositDate;
	protected Uppercasebox									depositNo;
	protected Uppercasebox									paymentRef;
	protected Uppercasebox									transactionRef;
	protected AccountSelectionBox							chequeAcNo;
	protected ExtendedCombobox								fundingAccount;
	protected Datebox										receivedDate;
	protected Textbox										remarks;
	
	protected Row											row_favourNo;	
	protected Row											row_BankCode;	
	protected Row											row_DepositDate;	
	protected Row											row_PaymentRef;	
	protected Row											row_ChequeAcNo;	
	protected Row											row_fundingAcNo;	
	protected Row											row_remarks;
	
	protected Tabbox										tabBoxIndexCenter;
	protected Tabs											tabsIndexCenter;
	protected Tabpanels										tabpanelsBoxIndexCenter;

	//Buttons
	protected Button										btnReceipt;

	protected transient FeeReceiptListCtrl					feeReceiptListCtrl					= null;		
	private transient AccountingDetailDialogCtrl			accountingDetailDialogCtrl;
	private FinReceiptHeader								receiptHeader						= null;
	private transient FeeReceiptService						feeReceiptService;
	private AccountingSetService							accountingSetService;
	private AccountEngineExecution							engineExecution;
	private EventManager									eventManager;
	private boolean											feesExists = false;

	/**
	 * default constructor.<br>
	 */
	public FeeReceiptDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FeeReceiptDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Rule object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FeeReceiptDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FeeReceiptDialog);

		try {
			if (arguments.containsKey("receiptHeader")) {
				receiptHeader = (FinReceiptHeader) arguments.get("receiptHeader");

				Cloner cloner = new Cloner();
				FinReceiptHeader befImage = cloner.deepClone(receiptHeader);
				receiptHeader.setBefImage(befImage);

			}

			if (arguments.containsKey("feeReceiptListCtrl")) {
				feeReceiptListCtrl =  (FeeReceiptListCtrl) arguments.get("feeReceiptListCtrl");
			}

			doLoadWorkFlow(receiptHeader.isWorkflow(), receiptHeader.getWorkflowId(), receiptHeader.getNextTaskId());

			if (isWorkFlowEnabled()) {
				String recStatus = StringUtils.trimToEmpty(receiptHeader.getRecordStatus());
				if (recStatus.equals(PennantConstants.RCD_STATUS_REJECTED)) {
					this.userAction = setRejectRecordStatus(this.userAction);
				} else {
					this.userAction = setListRecordStatus(this.userAction);
				}
			} else {
				this.south.setHeight("0px");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// set Field Properties
			doSetFieldProperties();

			// set Read only mode accordingly if the object is new or not.
			doEdit();
			if (StringUtils.isNotBlank(receiptHeader.getRecordType())) {
				this.btnNotes.setVisible(true);
			}

			//Reset Finance Repay Header Details
			doWriteBeanToComponents();
			this.borderlayout_FeeReceipt.setHeight(getBorderLayoutHeight());

			// Setting tile Name based on Service Action
			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_FeeReceiptDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities(super.pageRightName, getRole());
		this.btnReceipt.setVisible(getUserWorkspace().isAllowed("button_FeeReceiptDialog_btnReceipt"));
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	protected void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		int formatter = CurrencyUtil.getFormat(receiptHeader.getFinCcy());

		this.finReference.setModuleName("FinanceMainTemp");
		this.finReference.setMandatoryStyle(true);
		this.finReference.setValueColumn("FinReference");
		this.finReference.setDescColumn("FinType");
		this.finReference.setDisplayStyle(2);
		this.finReference.setValidateColumns(new String[] { "FinReference" });
		
		// Remove Receipt Records , because fees in Receipts collected automatically
		Filter referenceFilter[] = new Filter[2];
		referenceFilter[0] = new Filter("RcdMaintainSts", FinanceConstants.FINSER_EVENT_RECEIPT, Filter.OP_NOT_EQUAL);
		referenceFilter[1] = Filter.isNull("RcdMaintainSts");
		
		Filter filter[] = new Filter[1];
		filter[0] = Filter.or(referenceFilter);
		this.finReference.setFilters(filter);

		//Receipts Details
		this.receiptAmount.setProperties(true , formatter);
		this.realizationDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		
		this.fundingAccount.setModuleName("FinTypePartner");
		this.fundingAccount.setMandatoryStyle(true);
		this.fundingAccount.setValueColumn("PartnerBankID");
		this.fundingAccount.setDescColumn("PartnerBankCode");
		this.fundingAccount.setDisplayStyle(2);
		this.fundingAccount.setValidateColumns(new String[] { "PartnerBankID" });
		
		this.chequeAcNo.setButtonVisible(false);
		this.chequeAcNo.setMandatory(false);
		this.chequeAcNo.setAcountDetails("", "", true);
		this.chequeAcNo.setTextBoxWidth(180);
		
		this.receivedDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.remarks.setMaxlength(100);
		this.favourName.setMaxlength(50);
		this.valueDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.favourNo.setMaxlength(6);
		this.depositDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.depositNo.setMaxlength(50);
		this.paymentRef.setMaxlength(50);
		this.transactionRef.setMaxlength(50);
		
		this.bankCode.setModuleName("BankDetail");
		this.bankCode.setMandatoryStyle(true);
		this.bankCode.setValueColumn("BankCode");
		this.bankCode.setDescColumn("BankName");
		this.bankCode.setDisplayStyle(2);
		this.bankCode.setValidateColumns(new String[] { "BankCode" });

		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	public void doEdit() {
		logger.debug("Entering");
		
		if(this.receiptHeader.isNewRecord()){
			this.finReference.setReadonly(false);
		}else{
			this.finReference.setReadonly(true);
		}
		
		this.finType.setReadonly(true);
		this.finCcy.setReadonly(true);
		this.finBranch.setReadonly(true);
		this.custCIF.setReadonly(true);
		this.custName.setReadonly(true);

		// Receipt Details
		readOnlyComponent(true, this.receiptPurpose);
		readOnlyComponent(true, this.allocationMethod);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_excessAdjustTo"), this.excessAdjustTo);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_receiptMode"), this.receiptMode);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_receiptAmount"), this.receiptAmount);
		this.excessAdjustTo.setDisabled(true);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_realizationDate"), this.realizationDate);
		
		//Receipt Details
		readOnlyComponent(isReadOnly("FeeReceiptDialog_favourNo"), this.favourNo);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_valueDate"), this.valueDate);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_bankCode"), this.bankCode);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_favourName"), this.favourName);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_depositDate"), this.depositDate);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_depositNo"), this.depositNo);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_chequeAcNo"), this.chequeAcNo);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_paymentRef"), this.paymentRef);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_transactionRef"), this.transactionRef);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_fundingAccount"), this.fundingAccount);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_cashReceivedDate"), this.receivedDate);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_remarks"), this.remarks);

		logger.debug("Leaving");
	}
	
	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnReceipt.isVisible());
	}

	public void onFulfill$bankCode(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = bankCode.getObject();

		if (dataObject instanceof String) {
			this.bankCode.setValue(dataObject.toString());
		} else {
			BankDetail details = (BankDetail) dataObject;
			if (details != null) {
				this.bankCode.setAttribute("bankCode", details.getBankCode());
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onFulfill$finReference(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finReference.getObject();
		this.gb_FeeDetail.setVisible(false);
		boolean clearFields = false;
		if (dataObject instanceof String) {
			this.finReference.setValue(dataObject.toString());
			clearFields = true;
		} else {
			FinanceMain main = (FinanceMain) dataObject;
			if (main != null) {
				this.finReference.setValue(main.getFinReference(),"");
				this.finType.setValue(main.getFinType(), main.getLovDescFinTypeName());
				this.finCcy.setValue(main.getFinCcy(), CurrencyUtil.getCcyDesc(main.getFinCcy()));
				this.finBranch.setValue(main.getFinBranch(), main.getLovDescFinBranchName());
				this.custCIF.setValue(main.getLovDescCustCIF());
				this.custName.setValue(main.getLovDescCustShrtName());
				
				// Fee Details Fetching only for display
				List<FinFeeDetail> feelist = getFeeReceiptService().getPaidFinFeeDetails(main.getFinReference());
				doFillFeeDetails(feelist);
			}else{
				clearFields = true;
			}
		}
		
		if(clearFields){
			this.finReference.setValue("","");
			this.finType.setValue("","");
			this.finCcy.setValue("","");
			this.finBranch.setValue("","");
			this.custCIF.setValue("");
			this.custName.setValue("");
			
			this.gb_FeeDetail.setVisible(false);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Filling Fee details which are going to be paid on Origination Process
	 */
	private void doFillFeeDetails(List<FinFeeDetail> feeDetails) {
		logger.debug("Entering");

		Listcell lc;
		Listitem item;

		this.listBoxFeeDetail.getItems().clear();
		feesExists = false;
		if (feeDetails != null) {
			int finFormatter = CurrencyUtil.getFormat(this.finCcy.getValue());
			BigDecimal totalPaid = BigDecimal.ZERO;
			this.gb_FeeDetail.setVisible(true);
			for (int i = 0; i < feeDetails.size(); i++) {
				FinFeeDetail fee = feeDetails.get(i);
				item = new Listitem();

				lc = new Listcell(StringUtils.isNotEmpty(fee.getVasReference()) ? fee.getVasReference() :  fee.getFeeTypeDesc());
				lc.setStyle("font-weight:bold;color: #FF6600;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(fee.getCalculatedAmount(), finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(fee.getActualAmount(), finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(fee.getWaivedAmount(), finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(fee.getPaidAmount(), finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(fee.getRemainingFee(), finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				
				this.listBoxFeeDetail.appendChild(item);
				totalPaid = totalPaid.add(fee.getPaidAmount());
			}

			if(totalPaid.compareTo(BigDecimal.ZERO) > 0){
				feesExists = true;
				doFillSummaryDetails(totalPaid, finFormatter);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Filling Summary Details for Repay Schedule Terms
	 * 
	 * @param totalrefund
	 * @param totalWaiver
	 * @param totalPft
	 * @param totalPri
	 */
	private void doFillSummaryDetails(BigDecimal totalPaid, int finFormatter) {

		Listcell lc;
		Listitem item;
		
		//Summary Details
		item = new Listitem();
		lc = new Listcell(Labels.getLabel("listcell_Total.label"));
		item.setStyle("font-weight:bold;background-color: #C0EBDF;");
		lc.setSpan(4);
		lc.setParent(item);
		
		lc = new Listcell(PennantApplicationUtil.amountFormate(totalPaid, finFormatter));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell("");
		lc.setParent(item);
		this.listBoxFeeDetail.appendChild(item);
	}

	/**
	 * Method for Processing Captured details based on Receipt Mode
	 * @param event
	 */
	public void onChange$receiptMode(Event event) {
		String dType = this.receiptMode.getSelectedItem().getValue().toString();
		checkByReceiptMode(dType, true);
	}

	/**
	 * Method for Setting Fields based on Receipt Mode selected
	 * @param recMode
	 */
	private void checkByReceiptMode(String recMode, boolean isUserAction) {
		logger.debug("Entering");
		
		if(isUserAction){
			this.receiptAmount.setValue(BigDecimal.ZERO);
			this.favourNo.setValue("");
			this.valueDate.setValue(DateUtility.getAppDate());
			this.bankCode.setValue("");
			this.bankCode.setDescription("");
			this.bankCode.setObject(null);
			this.favourName.setValue("");
			this.depositDate.setValue(null);
			this.depositNo.setValue("");
			this.paymentRef.setValue("");
			this.transactionRef.setValue("");
			this.chequeAcNo.setValue("");
			this.fundingAccount.setValue("");
			this.fundingAccount.setDescription("");
			this.fundingAccount.setObject(null);
			this.receivedDate.setValue(DateUtility.getAppDate());
			this.remarks.setValue("");
		}
		
		if (StringUtils.isEmpty(recMode) || StringUtils.equals(recMode, PennantConstants.List_Select) ||
				StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_EXCESS)) {
			this.gb_ReceiptDetails.setVisible(false);
			this.receiptAmount.setMandatory(false);
			this.receiptAmount.setReadonly(true);
			this.receiptAmount.setValue(BigDecimal.ZERO);
			
		} else{

			this.gb_ReceiptDetails.setVisible(true);
			this.caption_receiptDetail.setLabel(this.receiptMode.getSelectedItem().getLabel());
			this.receiptAmount.setMandatory(true);
			readOnlyComponent(isReadOnly("FeeReceiptDialog_receiptAmount"), this.receiptAmount);
			
			Filter fundingAcFilters[] = new Filter[3];
			fundingAcFilters[0] = new Filter("Purpose", RepayConstants.RECEIPTTYPE_RECIPT, Filter.OP_EQUAL);
			fundingAcFilters[1] = new Filter("FinType", this.finType.getValue(), Filter.OP_EQUAL);
			fundingAcFilters[2] = new Filter("PaymentMode", recMode, Filter.OP_EQUAL);
			Filter.and(fundingAcFilters);
			this.fundingAccount.setFilters(fundingAcFilters);
			this.row_fundingAcNo.setVisible(true);
			this.row_remarks.setVisible(true);
			
			if (StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_CHEQUE)
					|| StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_DD)) {
				
				this.row_favourNo.setVisible(true);
				this.row_BankCode.setVisible(true);
				this.bankCode.setMandatoryStyle(true);
				this.row_DepositDate.setVisible(true);
				this.row_PaymentRef.setVisible(false);
				
				if(StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_CHEQUE)){
					this.row_ChequeAcNo.setVisible(true);
					this.label_FeeReceiptDialog_favourNo.setValue(Labels.getLabel("label_FeeReceiptDialog_ChequeFavourNo.value"));
					
					if(isUserAction){
						this.depositDate.setValue(DateUtility.getAppDate());
						this.receivedDate.setValue(DateUtility.getAppDate());
						this.valueDate.setValue(DateUtility.getAppDate());
					}
					
				}else{
					this.row_ChequeAcNo.setVisible(false);
					this.label_FeeReceiptDialog_favourNo.setValue(Labels.getLabel("label_FeeReceiptDialog_DDFavourNo.value"));
					
					if(isUserAction){
						this.depositDate.setValue(DateUtility.getAppDate());
						this.valueDate.setValue(DateUtility.getAppDate());
					}
				}
				
				if(isUserAction){
					this.favourName.setValue(Labels.getLabel("label_ClientName"));
				}
				
			} else if (StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_CASH)) {
				
				this.row_favourNo.setVisible(false);
				this.row_BankCode.setVisible(false);
				this.bankCode.setMandatoryStyle(false);
				this.row_DepositDate.setVisible(false);
				this.row_ChequeAcNo.setVisible(false);
				this.row_PaymentRef.setVisible(false);
				
				if(isUserAction){
					this.receivedDate.setValue(DateUtility.getAppDate());
				}
				
			} else {
				this.row_favourNo.setVisible(false);
				this.row_BankCode.setVisible(false);
				this.bankCode.setMandatoryStyle(false);
				this.row_DepositDate.setVisible(false);
				this.row_ChequeAcNo.setVisible(false);
				this.row_PaymentRef.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for event of Changing Repayment Amount
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnReceipt(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	public void doSave() throws WrongValueException, InterruptedException {
		logger.debug("Entering");

		try {
			boolean recReject = false;
			if (this.userAction.getSelectedItem() != null
					&& ("Resubmit".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							|| "Reject".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel()) || "Cancel"
							.equalsIgnoreCase(this.userAction.getSelectedItem().getLabel()))) {
				recReject = true;
			}
			
			// If Fee Details not exists against Reference , not allowed to proceed further
			if(!feesExists){
				/*MessageUtil.showError(Labels.getLabel("label_FeeReceiptDialog_NoFees.value"));
				return;*/
			}

			if(!recReject){
				doClearMessage();
				doSetValidation();
				doWriteComponentsToBean();
				
				FinReceiptHeader header = getReceiptHeader();
				if(header.getReceiptDetails().isEmpty()){
					FinReceiptDetail receiptDetail = new FinReceiptDetail();
					receiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
					receiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
					header.getReceiptDetails().add(receiptDetail);
				}
				header.setRemarks(this.remarks.getValue());
				for (FinReceiptDetail receiptDetail : header.getReceiptDetails()) {
					receiptDetail.setAmount(header.getReceiptAmount());
					receiptDetail.setPaymentType(header.getReceiptMode());
					receiptDetail.setFavourNumber(this.favourNo.getValue());
					receiptDetail.setValueDate(this.valueDate.getValue());
					receiptDetail.setBankCode(this.bankCode.getValue());
					receiptDetail.setFavourName(this.favourName.getValue());
					receiptDetail.setDepositDate(this.depositDate.getValue());
					receiptDetail.setDepositNo(this.depositNo.getValue());
					receiptDetail.setPaymentRef(this.paymentRef.getValue());
					receiptDetail.setTransactionRef(this.transactionRef.getValue());
					receiptDetail.setChequeAcNo(this.chequeAcNo.getValue());
					receiptDetail.setFundingAc(Long.valueOf(this.fundingAccount.getValue()));
					receiptDetail.setReceivedDate(this.receivedDate.getValue());
					
					
					if(receiptDetail.getRepayHeaders().isEmpty()){
						FinRepayHeader repayHeader = new FinRepayHeader();
						repayHeader.setFinReference(this.finReference.getValue());
						repayHeader.setValueDate(this.receivedDate.getValue());
						repayHeader.setFinEvent(FinanceConstants.FINSER_EVENT_FEEPAYMENT);
						repayHeader.setRepayAmount(header.getReceiptAmount());
						
						receiptDetail.getRepayHeaders().add(repayHeader);
					}else{
						receiptDetail.getRepayHeaders().get(0).setValueDate(this.receivedDate.getValue());
						receiptDetail.getRepayHeaders().get(0).setRepayAmount(header.getReceiptAmount());
					}
				}
			}

			//If Schedule Re-modified Save into DB or else only add Repayments Details
			doProcessReceipt();

		} catch (InterfaceException pfe) {
			MessageUtil.showError(pfe);
			return;
		} catch (WrongValuesException we) {
			throw we;
		} catch (Exception e) {
			logger.error("Exception: ", e);
			return;
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Process Repayment Details
	 * 
	 * @throws Exception
	 */
	private void doProcessReceipt() throws Exception {
		logger.debug("Entering");

		// Receipt Header Details workflow fields
		FinReceiptHeader receiptHeader = getReceiptHeader();
		receiptHeader.setReference(this.finReference.getValue());
		receiptHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		receiptHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		receiptHeader.setUserDetails(getUserWorkspace().getLoggedInUser());

		//Duplicate Creation of Object
		Cloner cloner = new Cloner();
		FinReceiptHeader aReceiptHeader = cloner.deepClone(receiptHeader);

		String tranType = "";
		if (isWorkFlowEnabled()) {

			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aReceiptHeader.getRecordType())) {
				aReceiptHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				aReceiptHeader.setVersion(1);
				aReceiptHeader.setNewRecord(true);
			}

		} else {
			aReceiptHeader.setVersion(aReceiptHeader.getVersion() + 1);
			tranType = PennantConstants.TRAN_UPD;
		}

		// save it to database
		try {
			
			if (doProcess(aReceiptHeader, tranType)) {

				if (feeReceiptListCtrl != null) {
					refreshMaintainList();
				}

				//Customer Notification for Role Identification
				if (StringUtils.isBlank(aReceiptHeader.getNextTaskId())) {
					aReceiptHeader.setNextRoleCode("");
				}
				String msg = PennantApplicationUtil.getSavingStatus(aReceiptHeader.getRoleCode(),
						aReceiptHeader.getNextRoleCode(), this.finReference.getValue(), " Fee Receipt ",
						aReceiptHeader.getRecordStatus());
				Clients.showNotification(msg, "info", null, null, -1);
				

				// User Notifications Message/Alert
				try {
					if (!"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							&& !"Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							&& !this.userAction.getSelectedItem().getLabel().contains("Reject")) {

						// Send message Notification to Users
						String nextRoleCodes = aReceiptHeader.getNextRoleCode();
						if (StringUtils.isNotEmpty(nextRoleCodes)) {
							Notify notify = Notify.valueOf("ROLE");
							String[] to = nextRoleCodes.split(",");
							if (StringUtils.isNotEmpty(aReceiptHeader.getReference())) {

								String reference = aReceiptHeader.getReference();
								if (!PennantConstants.RCD_STATUS_CANCELLED.equalsIgnoreCase(aReceiptHeader.getRecordStatus())) {
									getEventManager().publish(
											Labels.getLabel("REC_PENDING_MESSAGE") + " with Reference" + ":"
													+ reference, notify, to);
								}
							} else {
								getEventManager().publish(Labels.getLabel("REC_PENDING_MESSAGE"), notify, to);
							}
						}
					}
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}

				closeDialog();
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Writing Data into Fields from Bean
	 * @throws InterruptedException
	 */
	private void doWriteBeanToComponents() throws InterruptedException {
		logger.debug("Entering");

		// Receipt Header Details
		FinReceiptHeader header = getReceiptHeader();
		int finFormatter = CurrencyUtil.getFormat(header.getFinCcy());
		fillComboBox(this.receiptPurpose, FinanceConstants.FINSER_EVENT_FEEPAYMENT, PennantStaticListUtil.getReceiptPurpose(), "");
		fillComboBox(this.excessAdjustTo, header.getExcessAdjustTo(), PennantStaticListUtil.getExcessAdjustmentTypes(), "");
		fillComboBox(this.receiptMode, header.getReceiptMode(), PennantStaticListUtil.getReceiptModes(), ",EXCESS,");
		this.receiptAmount.setValue(PennantApplicationUtil.formateAmount(BigDecimal.ZERO, finFormatter));
		this.realizationDate.setValue(header.getRealizationDate());
		if(!isReadOnly("FeeReceiptDialog_realizationDate") || header.getRealizationDate() != null){
			this.row_RealizationDate.setVisible(true);
		}else{
			this.row_RealizationDate.setVisible(false);
		}
		this.remarks.setValue(header.getRemarks());
		
		if(!header.isNewRecord()){
			this.finReference.setValue(header.getReference(),"");
			this.finType.setValue(header.getFinType(), header.getFinTypeDesc());
			this.finCcy.setValue(header.getFinCcy(), CurrencyUtil.getCcyDesc(header.getFinCcy()));
			this.finBranch.setValue(header.getFinBranch(), header.getFinBranchDesc());
			this.custCIF.setValue(header.getCustCIF());
			this.custName.setValue(header.getCustShrtName());
		}
		
		String allocateMthd = header.getAllocationType();
		if(StringUtils.isEmpty(allocateMthd)){
			allocateMthd = RepayConstants.ALLOCATIONTYPE_AUTO;
		}
		fillComboBox(this.allocationMethod, allocateMthd, PennantStaticListUtil.getAllocationMethods(), "");
		checkByReceiptMode(header.getReceiptMode(), false);
		
		// Separating Receipt Amounts based on user entry, if exists
		Map<String, BigDecimal> receiptAmountsMap = new HashMap<>();
		if(header.getReceiptDetails() != null && !header.getReceiptDetails().isEmpty()){
			for (int i = 0; i < header.getReceiptDetails().size(); i++) {
				FinReceiptDetail receiptDetail = header.getReceiptDetails().get(i);
				receiptAmountsMap.put(receiptDetail.getPaymentType(), receiptDetail.getAmount());
				if(!StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EXCESS) && 
						!StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EMIINADV) &&
						!StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_PAYABLE)){
					this.receiptAmount.setValue(PennantApplicationUtil.formateAmount(receiptDetail.getAmount(), finFormatter));
					this.favourNo.setValue(receiptDetail.getFavourNumber());
					this.valueDate.setValue(receiptDetail.getValueDate());
					this.bankCode.setValue(receiptDetail.getBankCode());
					this.bankCode.setDescription(receiptDetail.getBankCodeDesc());
					this.favourName.setValue(receiptDetail.getFavourName());
					this.depositDate.setValue(receiptDetail.getDepositDate());
					this.depositNo.setValue(receiptDetail.getDepositNo());
					this.paymentRef.setValue(receiptDetail.getPaymentRef());
					this.transactionRef.setValue(receiptDetail.getTransactionRef());
					this.chequeAcNo.setValue(receiptDetail.getChequeAcNo());
					this.fundingAccount.setValue(String.valueOf(receiptDetail.getFundingAc()));
					this.fundingAccount.setDescription(receiptDetail.getFundingAcDesc());
					this.receivedDate.setValue(receiptDetail.getReceivedDate());
				}
			}
		}
		
		// Fee Details
		doFillFeeDetails(header.getPaidFeeList());
		
		//Show Accounting Tab Details Based upon Role Condition using Work flow
		if ("Accounting".equals(getTaskTabs(getTaskId(getRole())))) {
			//Accounting Details Tab Addition
			appendAccountingDetailTab(true);
		}

		this.recordStatus.setValue(header.getRecordStatus());
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	protected void appendAccountingDetailTab(boolean onLoadProcess) {
		logger.debug("Entering");
		boolean createTab = false;
		if (getTab(AssetConstants.UNIQUE_ID_ACCOUNTING) == null) {
			createTab = true;
		}
		
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_ACCOUNTING, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_ACCOUNTING);
		}
		if (!onLoadProcess) {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("roleCode", getRole());
			map.put("dialogCtrl", this);
			map.put("finHeaderList", getFinBasicDetails());
			
			// Fetch Accounting Set ID
			AccountingSet accountingSet = accountingSetService.getAccSetSysDflByEvent(AccountEventConstants.ACCEVENT_FEEPAY,
					AccountEventConstants.ACCEVENT_FEEPAY, "");
			
			long acSetID = 0;
			if(accountingSet != null){
				acSetID = accountingSet.getAccountSetid();
			}
			
			map.put("acSetID", acSetID);
			map.put("postAccReq", false);
			
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AccountingDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_ACCOUNTING), map);
			Tab tab = getTab(AssetConstants.UNIQUE_ID_ACCOUNTING);
			if (tab != null) {
				tab.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}
	
	/**
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	private ArrayList<Object> getFinBasicDetails() {
		ArrayList<Object> arrayList = new ArrayList<Object>();
		arrayList.add(0, this.finType.getValue());
		arrayList.add(1, this.finCcy.getValue());
		arrayList.add(2, PennantAppUtil.getlabelDesc(getReceiptHeader().getScheduleMethod(), PennantStaticListUtil.getScheduleMethods()));
		arrayList.add(3, this.finReference.getValue());
		arrayList.add(4, PennantAppUtil.getlabelDesc(getReceiptHeader().getPftDaysBasis(), PennantStaticListUtil.getProfitDaysBasis()));
		arrayList.add(5, null);
		arrayList.add(6, false);
		arrayList.add(7, false);
		arrayList.add(8, null);
		arrayList.add(9, this.custName.getValue());
		arrayList.add(10, true);
		arrayList.add(11, null);
		return arrayList;
	}
	
	/**
	 * This method will create tab and will assign corresponding tab selection method and makes tab visibility based on
	 * parameter
	 * 
	 * @param moduleID
	 * @param tabVisible
	 */
	public void createTab(String moduleID, boolean tabVisible) {
		logger.debug("Entering");
		String tabName = Labels.getLabel("tab_label_" + moduleID);
		Tab tab = new Tab(tabName);
		tab.setId(getTabID(moduleID));
		tab.setVisible(tabVisible);
		tabsIndexCenter.appendChild(tab);
		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId(getTabpanelID(moduleID));
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setParent(tabpanelsBoxIndexCenter);
		tabpanel.setHeight("100%");
		ComponentsCtrl.applyForward(tab, ("onSelect=onSelectAccountTab"));
		logger.debug("Leaving");
	}
	
	public void onSelectAccountTab(ForwardEvent event){
		
		Tab tab = (Tab) event.getOrigin().getTarget();
		tab.removeForward(Events.ON_SELECT, tab, "onSelectAccountTab");
		appendAccountingDetailTab(false);
		if (accountingDetailDialogCtrl != null) {
			accountingDetailDialogCtrl.doSetLabels(getFinBasicDetails());
		}
	}
	
	private String getTabID(String id) {
		return "TAB" + StringUtils.trimToEmpty(id);
	}

	private String getTabpanelID(String id) {
		return "TABPANEL" + StringUtils.trimToEmpty(id);
	}

	private void clearTabpanelChildren(String id) {
		Tabpanel tabpanel = getTabpanel(id);
		if (tabpanel != null) {
			tabpanel.setStyle("overflow:auto;");
			tabpanel.getChildren().clear();
		}
	}

	private Tab getTab(String id) {
		return (Tab) tabsIndexCenter.getFellowIfAny(getTabID(id));
	}

	private Tabpanel getTabpanel(String id) {
		return (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny(getTabpanelID(id));
	}
	
	/**
	 * Method for Executing Eligibility Details
	 * 
	 * @throws Exception
	 */
	public void executeAccounting() throws Exception{
		logger.debug("Entering");

		List<ReturnDataSet> accountingSetEntries = new ArrayList<ReturnDataSet>();
		AEEvent aeEvent = new AEEvent();
		aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_FEEPAY);
		aeEvent.setFinReference(getReceiptHeader().getReference());
		aeEvent.setCustCIF(getReceiptHeader().getCustCIF());
		aeEvent.setBranch(getReceiptHeader().getFinBranch());
		aeEvent.setCcy(getReceiptHeader().getFinCcy());
		aeEvent.setCustID(getReceiptHeader().getCustID());
		
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if(amountCodes == null){
			amountCodes = new AEAmountCodes();
		}
		
		FinReceiptDetail receiptDetail = getReceiptHeader().getReceiptDetails().get(0);
		amountCodes.setPartnerBankAc(receiptDetail.getPartnerBankAc());
		amountCodes.setPartnerBankAcType(receiptDetail.getPartnerBankAcType());
		amountCodes.setPaidFee(receiptDetail.getAmount());
		amountCodes.setFinType(getReceiptHeader().getFinType());
		
		// Fetch Accounting Set ID
		long accountingSetID = AccountingConfigCache.getAccountSetID(getReceiptHeader().getFinType(),
				AccountEventConstants.ACCEVENT_FEEPAY, FinanceConstants.MODULEID_FINTYPE);
		if(accountingSetID != 0 && accountingSetID != Long.MIN_VALUE){
			aeEvent.getAcSetIDList().add(accountingSetID);
			aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());
			accountingSetEntries.addAll(engineExecution.getAccEngineExecResults(aeEvent).getReturnDataSet());
		}else{
			Clients.showNotification(Labels.getLabel("label_FeeReceiptDialog_NoAccounting.value"), "warning", null, null, -1);
		}
		if(accountingDetailDialogCtrl != null){
			accountingDetailDialogCtrl.doFillAccounting(accountingSetEntries);
		}

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		if (!this.receiptPurpose.isDisabled()) {
			this.receiptPurpose.setConstraint(new StaticListValidator(PennantStaticListUtil.getReceiptPurpose(), Labels
					.getLabel("label_FeeReceiptDialog_ReceiptPurpose.value")));
		}
		
		String recptMode = getComboboxValue(receiptMode);
		if (!this.receiptMode.isDisabled()) {
			this.receiptMode.setConstraint(new StaticListValidator(PennantStaticListUtil.getReceiptModes(), Labels
					.getLabel("label_FeeReceiptDialog_ReceiptMode.value")));
		}
		if (!this.excessAdjustTo.isDisabled()) {
			this.excessAdjustTo.setConstraint(new StaticListValidator(PennantStaticListUtil.getExcessAdjustmentTypes(), Labels
					.getLabel("label_FeeReceiptDialog_ExcessAdjustTo.value")));
		}
		if (!this.allocationMethod.isDisabled()) {
			this.allocationMethod.setConstraint(new StaticListValidator(PennantStaticListUtil.getAllocationMethods(), Labels
					.getLabel("label_FeeReceiptDialog_AllocationMethod.value")));
		}
		
		if(this.row_RealizationDate.isVisible() && !this.realizationDate.isDisabled()){
			this.realizationDate.setConstraint(new PTDateValidator(Labels.getLabel("label_FeeReceiptDialog_RealizationDate.value"), 
					true, this.valueDate.getValue(), DateUtility.getAppDate(), true));
		}
		
		if (StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_CHEQUE)){
			
			if(!this.chequeAcNo.isReadonly()){
				this.chequeAcNo.setConstraint(new PTStringValidator(Labels.getLabel("label_FeeReceiptDialog_ChequeAccountNo.value"), null, false));
			}
		}
		
		if(!StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_EXCESS)){
			if(!this.fundingAccount.isReadonly()){
				this.fundingAccount.setConstraint(new PTStringValidator(Labels.getLabel("label_FeeReceiptDialog_FundingAccount.value"), null, true));
			}

			if(!this.receivedDate.isDisabled()){
				this.receivedDate.setConstraint(new PTDateValidator(Labels.getLabel("label_FeeReceiptDialog_ReceivedDate.value"), true, 
						SysParamUtil.getValueAsDate(""), DateUtility.getAppDate(), true));
			}
		}
		
		if(StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_DD) ||
				StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_CHEQUE)){
			
			if(!this.favourNo.isReadonly()){
				String label = Labels.getLabel("label_FeeReceiptDialog_ChequeFavourNo.value");
				if(StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_DD)){
					label = Labels.getLabel("label_FeeReceiptDialog_DDFavourNo.value");
				}
				this.favourNo.setConstraint(new PTStringValidator(label, PennantRegularExpressions.REGEX_NUMERIC, true, 1, 6));
			}
			
			if(!this.valueDate.isDisabled()){
				this.valueDate.setConstraint(new PTDateValidator(Labels.getLabel("label_FeeReceiptDialog_ValueDate.value"), true, 
						SysParamUtil.getValueAsDate(""), DateUtility.getAppDate(), true));
			}
			
			if(!this.bankCode.isReadonly()){
				this.bankCode.setConstraint(new PTStringValidator(Labels.getLabel("label_FeeReceiptDialog_BankCode.value"), null, true, true));
			}
			
			if(!this.favourName.isReadonly()){
				this.favourName.setConstraint(new PTStringValidator(Labels.getLabel("label_FeeReceiptDialog_favourName.value"),
						PennantRegularExpressions.REGEX_NAME, true));
			}
			
			if(!this.depositDate.isDisabled()){
				this.depositDate.setConstraint(new PTDateValidator(Labels.getLabel("label_FeeReceiptDialog_DepositDate.value"), true, 
						SysParamUtil.getValueAsDate(""), DateUtility.getAppDate(), true));
			}
			
			if(!this.depositNo.isReadonly()){
				this.depositNo.setConstraint(new PTStringValidator(Labels.getLabel("label_FeeReceiptDialog_depositNo.value"),
						PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, false));
			}
		}
		
		if(StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_NEFT) || 
				StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_RTGS) || 
				StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_IMPS)){
			
			if(!this.transactionRef.isReadonly()){
				this.transactionRef.setConstraint(new PTStringValidator(Labels.getLabel("label_FeeReceiptDialog_tranReference.value"),
						PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
			}
		}
		
		if(!StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_EXCESS)){
			if(!this.paymentRef.isReadonly()){
				this.paymentRef.setConstraint(new PTStringValidator(Labels.getLabel("label_FeeReceiptDialog_paymentReference.value"),
						PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, false));
			}

			if(!this.remarks.isReadonly()){
				this.remarks.setConstraint(new PTStringValidator(Labels.getLabel("label_FeeReceiptDialog_Remarks.value"),
						PennantRegularExpressions.REGEX_DESCRIPTION, true));
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		
		this.receiptPurpose.setConstraint("");
		this.receiptMode.setConstraint("");
		this.excessAdjustTo.setConstraint("");
		this.allocationMethod.setConstraint("");
		this.realizationDate.setConstraint("");
		
		this.favourNo.setConstraint("");
		this.valueDate.setConstraint("");
		this.bankCode.setConstraint("");
		this.favourName.setConstraint("");
		this.depositDate.setConstraint("");
		this.depositNo.setConstraint("");
		this.paymentRef.setConstraint("");
		this.transactionRef.setConstraint("");
		this.chequeAcNo.setConstraint("");
		this.fundingAccount.setConstraint("");
		this.receivedDate.setConstraint("");
		this.remarks.setConstraint("");

		logger.debug("Leaving");
	}
	
	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");

		this.receiptPurpose.setErrorMessage("");
		this.receiptMode.setErrorMessage("");
		this.excessAdjustTo.setErrorMessage("");
		this.allocationMethod.setErrorMessage("");
		this.realizationDate.setErrorMessage("");
		
		this.favourNo.setErrorMessage("");
		this.valueDate.setErrorMessage("");
		this.bankCode.setErrorMessage("");
		this.favourName.setErrorMessage("");
		this.depositDate.setErrorMessage("");
		this.depositNo.setErrorMessage("");
		this.paymentRef.setErrorMessage("");
		this.transactionRef.setErrorMessage("");
		this.chequeAcNo.setErrorMessage("");
		this.fundingAccount.setErrorMessage("");
		this.receivedDate.setErrorMessage("");
		this.remarks.setErrorMessage("");

		logger.debug("Leaving");
	}

	/**
	 * Method for capturing Fields data from components to bean
	 * @return
	 */
	private FinReceiptHeader doWriteComponentsToBean() {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<>();
		int finFormatter = CurrencyUtil.getFormat(this.finCcy.getValue());

		FinReceiptHeader header = getReceiptHeader();
		header.setReceiptDate(DateUtility.getAppDate());
		header.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		header.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		header.setReference(this.finReference.getValue());
		header.setReceiptPurpose(FinanceConstants.FINSER_EVENT_FEEPAYMENT);
		try {
			header.setReceiptMode(getComboboxValue(receiptMode));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			header.setExcessAdjustTo(getComboboxValue(excessAdjustTo));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			header.setAllocationType(getComboboxValue(allocationMethod));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			header.setReceiptAmount(PennantApplicationUtil.unFormateAmount(receiptAmount.getValidateValue(), finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			header.setRealizationDate(this.realizationDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		// Receipt Mode Details
		try {
			this.favourNo.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.valueDate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.bankCode.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.favourName.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.depositDate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.depositNo.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.paymentRef.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.transactionRef.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.chequeAcNo.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.fundingAccount.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.receivedDate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.remarks.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		doRemoveValidation();
		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		
		logger.debug("Leaving");
		return header;
	}

	/**
	 * Method for Processing Finance Detail Object for Database Operation
	 * 
	 * @param afinanceMain
	 * @param tranType
	 * @return
	 * @throws InterruptedException
	 */
	private boolean doProcess(FinReceiptHeader aReceiptHeader, String tranType) throws InterruptedException {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader;
		String nextRoleCode = "";

		aReceiptHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aReceiptHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aReceiptHeader.setUserDetails(getUserWorkspace().getLoggedInUser());
		
		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId;
			aReceiptHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			
			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aReceiptHeader.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aReceiptHeader);
				}

				if (isNotesMandatory(taskId, aReceiptHeader)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}

				}
			}
			if (!StringUtils.isBlank(nextTaskId)) {
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

			aReceiptHeader.setTaskId(taskId);
			aReceiptHeader.setNextTaskId(nextTaskId);
			aReceiptHeader.setRoleCode(getRole());
			aReceiptHeader.setNextRoleCode(nextRoleCode);
			
			auditHeader = getAuditHeader(aReceiptHeader, tranType);
			String operationRefs = getServiceOperations(taskId, aReceiptHeader);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aReceiptHeader, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aReceiptHeader, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 * @throws InterruptedException
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws InterruptedException {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		FinReceiptHeader aReceiptHeader = (FinReceiptHeader) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					auditHeader = getFeeReceiptService().saveOrUpdate(auditHeader);

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getFeeReceiptService().doApprove(auditHeader);

						if (aReceiptHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getFeeReceiptService().doReject(auditHeader);
						if (aReceiptHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_FeeReceiptDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_FeeReceiptDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						//deleteNotes(getNotes(), true);
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

		} catch (InterfaceException e) {
			MessageUtil.showError(e);
		} catch (IllegalAccessException | InvocationTargetException e) {
			logger.error("Exception: ", e);
		}

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
	
	/**
	 * when user clicks on button "Notes"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(getReceiptHeader());
	}

	/**
	 * Get Audit Header Details
	 */
	private AuditHeader getAuditHeader(FinReceiptHeader receiptHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, receiptHeader);
		return new AuditHeader(String.valueOf(receiptHeader.getReceiptID()), null, null, null, auditDetail, 
				receiptHeader.getUserDetails(), getOverideMap());
	}

	protected void refreshMaintainList() {
		final JdbcSearchObject<FinReceiptHeader> soReceipt = feeReceiptListCtrl.getSearchObject();
		feeReceiptListCtrl.pagingFeeReceiptList.setActivePage(0);
		feeReceiptListCtrl.getPagedListWrapper().setSearchObject(soReceipt);
		if (feeReceiptListCtrl.listBoxFeeReceipt != null) {
			feeReceiptListCtrl.listBoxFeeReceipt.getListModel();
		}
	}

	@Override
	protected String getReference() {
		return this.finReference.getValue();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinReceiptHeader getReceiptHeader() {
		return receiptHeader;
	}
	public void setReceiptHeader(FinReceiptHeader receiptHeader) {
		this.receiptHeader = receiptHeader;
	}

	public FeeReceiptService getFeeReceiptService() {
		return feeReceiptService;
	}
	public void setFeeReceiptService(FeeReceiptService feeReceiptService) {
		this.feeReceiptService = feeReceiptService;
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	public void setEventManager(EventManager eventManager) {
		this.eventManager = eventManager;
	}

	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

	public void setAccountingSetService(AccountingSetService accountingSetService) {
		this.accountingSetService = accountingSetService;
	}

	public AccountingDetailDialogCtrl getAccountingDetailDialogCtrl() {
		return accountingDetailDialogCtrl;
	}

	public void setAccountingDetailDialogCtrl(AccountingDetailDialogCtrl accountingDetailDialogCtrl) {
		this.accountingDetailDialogCtrl = accountingDetailDialogCtrl;
	}

}