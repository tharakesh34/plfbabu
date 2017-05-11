package com.pennant.webui.financemanagement.receipts;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.AccountSelectionBox;
import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.component.Uppercasebox;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;
import com.rits.cloning.Cloner;

/**
 * This is the controller class for the WEB-INF/pages/FinanceManagement/Receipts/ReceiptRealizationEnqDialog.zul
 */
public class ReceiptEnquiryDialogCtrl  extends GFCBaseCtrl<FinReceiptHeader> {
	private static final long								serialVersionUID					= 966281186831332116L;
	private final static Logger								logger								= Logger.getLogger(ReceiptEnquiryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window										window_ReceiptEnquiryDialog;
	protected Borderlayout									borderlayout_Receipt;

	//Receipt Details
	protected Textbox										finType;
	protected Textbox										finReference;
	protected Textbox										finCcy;
	protected Textbox										finBranch;
	protected Textbox										custCIF;

	protected Combobox										receiptPurpose;
	protected Combobox										excessAdjustTo;
	protected Combobox										receiptMode;
	protected CurrencyBox									receiptAmount;
	protected Combobox										allocationMethod;
	protected Combobox										effScheduleMethod;
	protected Datebox										realizationDate;
	protected Row											row_RealizationDate;

	protected Groupbox										gb_ReceiptDetails;
	protected Caption										caption_receiptDetail;
	protected Label											label_ReceiptDialog_favourNo;
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
	protected Listbox										listBoxReceipts;

	private FinReceiptHeader								receiptHeader						= null;

	/**
	 * default constructor.<br>
	 */
	public ReceiptEnquiryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Rule object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ReceiptEnquiryDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ReceiptEnquiryDialog);

		try {
			if (arguments.containsKey("receiptHeader")) {

				setReceiptHeader((FinReceiptHeader) arguments.get("receiptHeader"));
				FinReceiptHeader befImage = new FinReceiptHeader();

				Cloner cloner = new Cloner();
				befImage = cloner.deepClone(getReceiptHeader());
				getReceiptHeader().setBefImage(befImage);

			}
			this.south.setHeight("0px");

			// set Field Properties
			doSetFieldProperties();
			doReadonly();

			//Reset Finance Repay Header Details
			doWriteBeanToComponents();
			this.borderlayout_Receipt.setHeight(getBorderLayoutHeight());
			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showErrorMessage(e);
			this.window_ReceiptEnquiryDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	protected void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		int formatter = CurrencyUtil.getFormat(getReceiptHeader().getFinCcy());

		//Receipts Details
		this.finType.setMaxlength(8);
		this.finReference.setMaxlength(20);
		this.finCcy.setMaxlength(LengthConstants.LEN_CURRENCY);
		this.finBranch.setMaxlength(LengthConstants.LEN_BRANCH);
		this.custCIF.setMaxlength(LengthConstants.LEN_CIF);
		this.receiptAmount.setProperties(true , formatter);
		this.realizationDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.fundingAccount.setModuleName("FinTypePartner");
		this.fundingAccount.setMandatoryStyle(true);
		this.fundingAccount.setValueColumn("PartnerBankID");
		this.fundingAccount.setDescColumn("PartnerBankCode");
		this.fundingAccount.setDisplayStyle(2);
		this.fundingAccount.setValidateColumns(new String[] { "PartnerBankID" });
		
		this.chequeAcNo.setButtonVisible(false);
		this.chequeAcNo.setMandatory(true);
		this.chequeAcNo.setAcountDetails("", "", true);
		this.chequeAcNo.setTextBoxWidth(180);

		this.receivedDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.remarks.setMaxlength(100);
		this.favourName.setMaxlength(50);
		this.valueDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.favourNo.setMaxlength(50);
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
		if (enqiryModule) {
			this.groupboxWf.setVisible(false);
			this.btnCtrl.setWFBtnStatus_Edit(false);
		} else {
			setStatusDetails();
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	public void doReadonly() {
		logger.debug("Entering");

		// Receipt Details
		readOnlyComponent(true, this.receiptPurpose);
		readOnlyComponent(true, this.excessAdjustTo);
		readOnlyComponent(true, this.receiptMode);
		readOnlyComponent(true, this.receiptAmount);
		readOnlyComponent(true, this.allocationMethod);
		readOnlyComponent(true, this.effScheduleMethod);
		readOnlyComponent(true, this.realizationDate);

		//Receipt Details
		readOnlyComponent(true, this.favourNo);
		readOnlyComponent(true, this.valueDate);
		readOnlyComponent(true, this.bankCode);
		readOnlyComponent(true, this.favourName);
		readOnlyComponent(true, this.depositDate);
		readOnlyComponent(true, this.depositNo);
		readOnlyComponent(true, this.chequeAcNo);
		readOnlyComponent(true, this.fundingAccount);
		readOnlyComponent(true, this.paymentRef);
		readOnlyComponent(true, this.transactionRef);
		readOnlyComponent(true, this.receivedDate);
		readOnlyComponent(true, this.remarks);

		logger.debug("Leaving");
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
	 * Method for Setting Fields based on Receipt Mode selected
	 * @param recMode
	 */
	private void checkByReceiptMode(String recMode, boolean isUserAction) {
		logger.debug("Entering");

		if (StringUtils.isEmpty(recMode) || StringUtils.equals(recMode, PennantConstants.List_Select) ||
				StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_EXCESS)) {
			this.gb_ReceiptDetails.setVisible(false);
			this.receiptAmount.setMandatory(false);
			this.receiptAmount.setReadonly(true);
			this.receiptAmount.setValue(BigDecimal.ZERO);

		} else{

			this.gb_ReceiptDetails.setVisible(true);
			this.caption_receiptDetail.setLabel(this.receiptMode.getSelectedItem().getLabel());

			if (StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_CHEQUE)
					|| StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_DD)) {

				this.row_favourNo.setVisible(true);
				this.row_BankCode.setVisible(true);
				this.bankCode.setMandatoryStyle(true);
				this.row_DepositDate.setVisible(true);
				this.row_PaymentRef.setVisible(false);
				this.row_remarks.setVisible(false);

				if(StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_CHEQUE)){
					this.row_fundingAcNo.setVisible(true);
					this.row_ChequeAcNo.setVisible(true);
					this.label_ReceiptDialog_favourNo.setValue(Labels.getLabel("label_ReceiptDialog_ChequeFavourNo.value"));
				}else{
					this.row_fundingAcNo.setVisible(false);
					this.row_ChequeAcNo.setVisible(false);
					this.label_ReceiptDialog_favourNo.setValue(Labels.getLabel("label_ReceiptDialog_DDFavourNo.value"));
				}

			} else if (StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_CASH)) {

				this.row_favourNo.setVisible(false);
				this.row_BankCode.setVisible(false);
				this.bankCode.setMandatoryStyle(false);
				this.row_DepositDate.setVisible(false);
				this.row_ChequeAcNo.setVisible(false);
				this.row_PaymentRef.setVisible(false);
				this.row_fundingAcNo.setVisible(true);
				this.row_remarks.setVisible(true);

			} else {
				this.row_favourNo.setVisible(false);
				this.row_BankCode.setVisible(false);
				this.bankCode.setMandatoryStyle(false);
				this.row_DepositDate.setVisible(false);
				this.row_ChequeAcNo.setVisible(false);
				this.row_fundingAcNo.setVisible(false);
				this.row_PaymentRef.setVisible(true);
				this.row_remarks.setVisible(true);
			}
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
		
		this.finType.setValue(header.getFinType());
		this.finReference.setValue(header.getReference());
		this.finCcy.setValue(header.getFinCcy());
		this.finBranch.setValue(header.getFinBranch());;
		this.custCIF.setValue(header.getCustCIF());
		int finFormatter = CurrencyUtil.getFormat(this.finCcy.getValue());
		
		fillComboBox(this.receiptPurpose, header.getReceiptPurpose(), PennantStaticListUtil.getReceiptPurpose(), "");
		fillComboBox(this.excessAdjustTo, header.getExcessAdjustTo(), PennantStaticListUtil.getExcessAdjustmentTypes(), "");
		fillComboBox(this.receiptMode, header.getReceiptMode(), PennantStaticListUtil.getReceiptModes(), "");
		this.receiptAmount.setValue(PennantApplicationUtil.formateAmount(header.getReceiptAmount(), finFormatter));
		fillComboBox(this.allocationMethod, header.getAllocationType(), PennantStaticListUtil.getAllocationMethods(), "");
		fillComboBox(this.effScheduleMethod, header.getEffectSchdMethod(), PennantStaticListUtil.getEarlyPayEffectOn(), ",NOEFCT,");
		this.realizationDate.setValue(header.getRealizationDate());
		checkByReceiptMode(header.getReceiptMode(), false);
		
		if(StringUtils.equals(header.getReceiptModeStatus(), RepayConstants.PAYSTATUS_REALIZED)){
			this.row_RealizationDate.setVisible(true);
		}

		// Separating Receipt Amounts based on user entry, if exists
		if(header.getReceiptDetails() != null && !header.getReceiptDetails().isEmpty()){
			for (int i = 0; i < header.getReceiptDetails().size(); i++) {
				
				FinReceiptDetail receiptDetail = header.getReceiptDetails().get(i);
				doFillReceipts(receiptDetail, finFormatter);
				
				if(!StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EXCESS) && 
						!StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EMIINADV) &&
						!StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_PAYABLE)){
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
					this.remarks.setValue(receiptDetail.getRemarks());
				}
			}
		}

		this.recordStatus.setValue(header.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Receipt Amount Details
	 */
	private void doFillReceipts(FinReceiptDetail receiptDetail, int finFormatter){
		logger.debug("Entering");

		Listitem item = new Listitem();
		Listcell lc = null;
		String label = "";
		if(StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EXCESS) || 
				StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EMIINADV) ||
				StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_PAYABLE)){

			label = Labels.getLabel("label_RecceiptDialog_ExcessType_"+receiptDetail.getPaymentType());

		}else{
			label = PennantAppUtil.getlabelDesc(receiptDetail.getPaymentType(), PennantStaticListUtil.getReceiptModes());
		}

		lc = new Listcell(label);
		lc.setStyle("font-weight:bold;color: #191a1c;");
		lc.setParent(item);

		lc = new Listcell(PennantApplicationUtil.amountFormate(receiptDetail.getAmount(), finFormatter));
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		lc = new Listcell(Labels.getLabel("label_ReceiptCancellationDialog_Status_"+receiptDetail.getStatus()));
		if(StringUtils.equals(receiptDetail.getStatus(), RepayConstants.PAYSTATUS_APPROVED)){
			lc.setStyle("font-weight:bold;color: #0252d3;");
		}else if(StringUtils.equals(receiptDetail.getStatus(), RepayConstants.PAYSTATUS_REALIZED)){
			lc.setStyle("font-weight:bold;color: #00a83d;");
		}else if(StringUtils.equals(receiptDetail.getStatus(), RepayConstants.PAYSTATUS_BOUNCE)){
			lc.setStyle("font-weight:bold;color: #f44b42;");
		}else if(StringUtils.equals(receiptDetail.getStatus(), RepayConstants.PAYSTATUS_CANCEL)){
			lc.setStyle("font-weight:bold;color: #f48341;");
		}
		lc.setParent(item);
		this.listBoxReceipts.appendChild(item);
		logger.debug("Leaving");
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

}
