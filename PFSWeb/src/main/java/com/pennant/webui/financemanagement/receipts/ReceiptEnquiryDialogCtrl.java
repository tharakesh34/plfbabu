package com.pennant.webui.financemanagement.receipts;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.aspose.words.SaveFormat;
import com.pennant.AccountSelectionBox;
import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.NumberToEnglishWords;
import com.pennant.app.util.PathUtil;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.reports.CashierReceipt;
import com.pennant.backend.service.applicationmaster.BranchService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.component.Uppercasebox;
import com.pennant.util.AgreementEngine;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.rits.cloning.Cloner;

/**
 * This is the controller class for the WEB-INF/pages/FinanceManagement/Receipts/ReceiptRealizationEnqDialog.zul
 */
public class ReceiptEnquiryDialogCtrl extends GFCBaseCtrl<FinReceiptHeader> {
	private static final long serialVersionUID = 966281186831332116L;
	private static final Logger logger = Logger.getLogger(ReceiptEnquiryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ReceiptEnquiryDialog;
	protected Borderlayout borderlayout_Receipt;

	//Receipt Details
	protected Textbox finType;
	protected Textbox finReference;
	protected Textbox finCcy;
	protected Textbox finBranch;
	protected Textbox custCIF;

	protected Combobox receiptPurpose;
	protected Combobox excessAdjustTo;
	protected Combobox receiptMode;
	protected CurrencyBox receiptAmount;
	protected Combobox allocationMethod;
	protected Combobox effScheduleMethod;
	protected Datebox realizationDate;
	protected Row row_RealizationDate;

	protected Groupbox gb_ReceiptDetails;
	protected Caption caption_receiptDetail;
	protected Label label_ReceiptDialog_favourNo;
	protected Uppercasebox favourNo;
	protected Datebox valueDate;
	protected ExtendedCombobox bankCode;
	protected Textbox favourName;
	protected Datebox depositDate;
	protected Uppercasebox depositNo;
	protected Uppercasebox paymentRef;
	protected Uppercasebox transactionRef;
	protected AccountSelectionBox chequeAcNo;
	protected ExtendedCombobox fundingAccount;
	protected Datebox receivedDate;
	protected Textbox remarks;

	protected Row row_favourNo;
	protected Row row_BankCode;
	protected Row row_DepositDate;
	protected Row row_PaymentRef;
	protected Row row_ChequeAcNo;
	protected Row row_fundingAcNo;
	protected Row row_remarks;
	protected Listbox listBoxReceipts;

	//Allocation Details
	protected Textbox allocation_finType;
	protected Textbox allocation_finReference;
	protected Textbox allocation_finCcy;
	protected Textbox allocation_finBranch;
	protected Textbox allocation_CustCIF;
	protected Decimalbox allocation_paidByCustomer;

	protected Listbox listBoxPastdues;
	protected Listbox listBoxManualAdvises;
	protected Tab allocationDetailsTab;

	protected Groupbox groupbox_Finance;
	protected Groupbox groupbox_Customer;
	protected Groupbox groupbox_Other;
	protected ExtendedCombobox custID;
	protected Textbox reference;
	protected ExtendedCombobox postBranch;
	protected ExtendedCombobox cashierBranch;
	protected ExtendedCombobox finDivision;

	private FinReceiptHeader receiptHeader = null;
	private String module = "";
	private String product = "";
	protected Button btnPrint;
	private transient BranchService branchService;
	Date derivedAppDate = DateUtility.getDerivedAppDate();

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

			if (arguments.containsKey("module")) {
				module = (String) arguments.get("module");
			}
			if (arguments.containsKey("product")) {
				product = (String) arguments.get("product");
			}
			if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_FEE)) {
				this.allocationDetailsTab.setVisible(false);
			}
			this.south.setHeight("0px");

			// set Field Properties
			doSetFieldProperties();
			doReadonly();

			if (StringUtils.equals(FinanceConstants.PRODUCT_GOLD, this.product)) {
				this.btnPrint.setVisible(true);
			} else {
				this.btnPrint.setVisible(false);
			}

			//Reset Finance Repay Header Details
			doWriteBeanToComponents();
			this.borderlayout_Receipt.setHeight(getBorderLayoutHeight());
			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
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
		this.receiptAmount.setProperties(true, formatter);
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
		if (enqiryModule) {
			this.groupboxWf.setVisible(false);
			this.btnCtrl.setWFBtnStatus_Edit(false);
		} else {
			setStatusDetails();
		}

		this.custID.setModuleName("Customer");
		this.custID.setMandatoryStyle(true);
		this.custID.setValueColumn("CustCIF");
		this.custID.setDescColumn("CustShrtName");
		this.custID.setDisplayStyle(2);
		this.custID.setValidateColumns(new String[] { "CustCIF" });

		if (RepayConstants.RECEIPTTO_FINANCE.equals(this.receiptHeader.getRecAgainst())) {
			this.fundingAccount.setModuleName("FinTypePartner");
			this.fundingAccount.setMandatoryStyle(true);
			this.fundingAccount.setValueColumn("PartnerBankID");
			this.fundingAccount.setDescColumn("PartnerBankCode");
			this.fundingAccount.setValueType(DataType.LONG);
			this.fundingAccount.setDisplayStyle(2);
			this.fundingAccount.setValidateColumns(new String[] { "PartnerBankID" });
			this.groupbox_Finance.setVisible(true);
		} else {
			this.fundingAccount.setModuleName("PartnerBank");
			this.fundingAccount.setValueColumn("PartnerBankId");
			this.fundingAccount.setDescColumn("PartnerBankCode");
			this.fundingAccount.setValidateColumns(
					new String[] { "PartnerBankId", "PartnerBankCode", "PartnerBankName", "BankCode" });
			this.fundingAccount.setMandatoryStyle(true);

			if (RepayConstants.RECEIPTTO_CUSTOMER.equals(this.receiptHeader.getRecAgainst())) {
				this.groupbox_Customer.setVisible(true);
			} else if (RepayConstants.RECEIPTTO_OTHER.equals(this.receiptHeader.getRecAgainst())) {
				this.groupbox_Other.setVisible(true);
				this.reference.setMaxlength(20);
			}
		}

		//Post Branch
		this.postBranch.setModuleName("Branch");
		this.postBranch.setValueColumn("BranchCode");
		this.postBranch.setDescColumn("BranchDesc");
		this.postBranch.setValidateColumns(new String[] { "BranchCode" });

		//Cashier Branch
		this.cashierBranch.setModuleName("Branch");
		this.cashierBranch.setValueColumn("BranchCode");
		this.cashierBranch.setDescColumn("BranchDesc");
		this.cashierBranch.setValidateColumns(new String[] { "BranchCode" });

		//Fin Division
		this.finDivision.setModuleName("DivisionDetail");
		this.finDivision.setValueColumn("DivisionCode");
		this.finDivision.setDescColumn("DivisionCodeDesc");
		this.finDivision.setValidateColumns(new String[] { "DivisionCode" });

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

		//Upfront fees changes
		readOnlyComponent(true, this.custID);
		readOnlyComponent(true, this.reference);
		readOnlyComponent(true, this.cashierBranch);
		readOnlyComponent(true, this.postBranch);
		readOnlyComponent(true, this.finDivision);

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
	 * 
	 * @param recMode
	 */
	private void checkByReceiptMode(String recMode, boolean isUserAction) {
		logger.debug("Entering");

		if (StringUtils.isEmpty(recMode) || StringUtils.equals(recMode, PennantConstants.List_Select)
				|| StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_EXCESS)) {
			this.gb_ReceiptDetails.setVisible(false);
			this.receiptAmount.setMandatory(false);
			this.receiptAmount.setReadonly(true);
			this.receiptAmount.setValue(BigDecimal.ZERO);

		} else {

			this.gb_ReceiptDetails.setVisible(true);
			this.caption_receiptDetail.setLabel(this.receiptMode.getSelectedItem().getLabel());
			this.row_fundingAcNo.setVisible(true);
			this.row_remarks.setVisible(true);

			if (StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_CHEQUE)
					|| StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_DD)) {

				this.row_favourNo.setVisible(true);
				this.row_BankCode.setVisible(true);
				this.bankCode.setMandatoryStyle(true);
				this.row_DepositDate.setVisible(true);
				this.row_PaymentRef.setVisible(false);

				if (StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_CHEQUE)) {
					this.row_ChequeAcNo.setVisible(true);
					this.label_ReceiptDialog_favourNo
							.setValue(Labels.getLabel("label_ReceiptDialog_ChequeFavourNo.value"));
				} else {
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
	 * Method for Writing Data into Fields from Bean
	 * 
	 * @throws InterruptedException
	 */
	private void doWriteBeanToComponents() throws InterruptedException {
		logger.debug("Entering");

		// Receipt Header Details
		FinReceiptHeader header = getReceiptHeader();

		this.finType.setValue(header.getFinType() + "-" + header.getFinTypeDesc());
		this.finReference.setValue(header.getReference());
		this.finCcy.setValue(header.getFinCcy() + "-" + header.getFinCcyDesc());
		this.finBranch.setValue(header.getFinBranch() + "-" + header.getFinBranchDesc());
		;
		this.custCIF.setValue(header.getCustCIF() + "-" + header.getCustShrtName());
		int finFormatter = CurrencyUtil.getFormat(header.getFinCcy());
		this.remarks.setValue(header.getRemarks());

		// Allocation Basic Details
		this.allocation_finType.setValue(header.getFinType() + "-" + header.getFinTypeDesc());
		this.allocation_finReference.setValue(header.getReference());
		this.allocation_finCcy.setValue(header.getFinCcy() + "-" + header.getFinCcyDesc());
		this.allocation_finBranch.setValue(header.getFinBranch() + "-" + header.getFinBranchDesc());
		this.allocation_CustCIF.setValue(header.getCustCIF() + "-" + header.getCustShrtName());
		this.allocation_paidByCustomer
				.setValue(PennantApplicationUtil.formateAmount(header.getReceiptAmount(), finFormatter));

		fillComboBox(this.receiptPurpose, header.getReceiptPurpose(), PennantStaticListUtil.getReceiptPurpose(), "");
		fillComboBox(this.excessAdjustTo, header.getExcessAdjustTo(), PennantStaticListUtil.getExcessAdjustmentTypes(),
				"");
		fillComboBox(this.receiptMode, header.getReceiptMode(), PennantStaticListUtil.getReceiptModes(), "");
		this.receiptAmount.setValue(PennantApplicationUtil.formateAmount(header.getReceiptAmount(), finFormatter));
		fillComboBox(this.allocationMethod, header.getAllocationType(), PennantStaticListUtil.getAllocationMethods(),
				"");
		fillComboBox(this.effScheduleMethod, header.getEffectSchdMethod(), PennantStaticListUtil.getEarlyPayEffectOn(),
				",NOEFCT,");
		this.realizationDate.setValue(header.getRealizationDate());
		checkByReceiptMode(header.getReceiptMode(), false);

		if (StringUtils.equals(header.getReceiptModeStatus(), RepayConstants.PAYSTATUS_REALIZED)) {
			this.row_RealizationDate.setVisible(true);
		}

		if ((StringUtils.equals(this.module, RepayConstants.MODULETYPE_FEE)) && header.getRealizationDate() != null) {
			this.row_RealizationDate.setVisible(true);
		}

		// Separating Receipt Amounts based on user entry, if exists
		if (header.getReceiptDetails() != null && !header.getReceiptDetails().isEmpty()) {
			for (int i = 0; i < header.getReceiptDetails().size(); i++) {

				FinReceiptDetail receiptDetail = header.getReceiptDetails().get(i);
				doFillReceipts(receiptDetail, finFormatter);

				if (!StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_EXCESS)
						&& !StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_EMIINADV)
						&& !StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_PAYABLE)) {
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

		// Allocations Adjustment
		if (!StringUtils.equals(this.module, RepayConstants.MODULETYPE_FEE)) {
			doFillAllocationDetail(header.getAllocations(), finFormatter);
		}

		this.postBranch.setValue(header.getPostBranch(), header.getPostBranchDesc());
		this.cashierBranch.setValue(header.getCashierBranch(), header.getCashierBranchDesc());
		this.finDivision.setValue(header.getFinDivision(), header.getFinDivisionDesc());

		if (this.groupbox_Finance.isVisible()) {
			readOnlyComponent(true, this.postBranch);
			readOnlyComponent(true, this.finDivision);
		} else if (this.groupbox_Customer.isVisible()) {
			this.custID.setAttribute("custID", header.getReference());
			this.custID.setValue(header.getCustomerCIF(), header.getCustomerName());
		} else if (this.groupbox_Other.isVisible()) {
			this.reference.setValue(header.getReference());
		}

		this.recordStatus.setValue(header.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Allocation Details based on Allocation Method (Auto/Manual)
	 * 
	 * @param header
	 * @param allocatePaidMap
	 */
	private void doFillAllocationDetail(List<ReceiptAllocationDetail> allocations, int formatter) {
		logger.debug("Entering");

		Listitem item = null;
		Listcell lc = null;

		// Get Receipt Purpose to Make Waiver amount Editable
		this.listBoxManualAdvises.getItems().clear();
		this.listBoxPastdues.getItems().clear();

		BigDecimal totalPaidAmount = BigDecimal.ZERO;
		BigDecimal totalWaivedAmount = BigDecimal.ZERO;
		BigDecimal totalAdvPaidAmount = BigDecimal.ZERO;
		BigDecimal totalAdvWaivedAmount = BigDecimal.ZERO;

		if (allocations != null && !allocations.isEmpty()) {

			for (int i = 0; i < allocations.size(); i++) {

				ReceiptAllocationDetail allocation = allocations.get(i);

				item = new Listitem();
				String label = Labels.getLabel("label_RecceiptDialog_AllocationType_" + allocation.getAllocationType());
				if (StringUtils.isNotEmpty(allocation.getTypeDesc())) {
					label = allocation.getTypeDesc();
				}
				lc = new Listcell(label);
				lc.setStyle("font-weight:bold;color: #191a1c;");
				lc.setParent(item);

				lc = new Listcell(PennantApplicationUtil.amountFormate(allocation.getPaidAmount(), formatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				lc = new Listcell(PennantApplicationUtil.amountFormate(allocation.getWaivedAmount(), formatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				if (StringUtils.equals(allocation.getAllocationType(), RepayConstants.ALLOCATION_MANADV)) {
					this.listBoxManualAdvises.appendChild(item);
					totalAdvPaidAmount = totalAdvPaidAmount.add(allocation.getPaidAmount());
					totalAdvWaivedAmount = totalAdvWaivedAmount.add(allocation.getWaivedAmount());
				} else {
					this.listBoxPastdues.appendChild(item);
					if (StringUtils.equals(allocation.getAllocationType(), RepayConstants.ALLOCATION_TDS)
							|| StringUtils.equals(allocation.getAllocationType(), RepayConstants.ALLOCATION_PFT)) {
						//Nothing TO DO
					} else {
						totalPaidAmount = totalPaidAmount.add(allocation.getPaidAmount());
						totalWaivedAmount = totalWaivedAmount.add(allocation.getWaivedAmount());
					}
				}
			}
		}

		// Fee Amount Collected along Receipt
		if (receiptHeader.getTotFeeAmount() != null && receiptHeader.getTotFeeAmount().compareTo(BigDecimal.ZERO) > 0) {
			item = new Listitem();
			lc = new Listcell(Labels.getLabel("label_RecceiptDialog_AllocationType_EventFee",
					new String[] { PennantAppUtil.getlabelDesc(getReceiptHeader().getReceiptPurpose(),
							PennantStaticListUtil.getReceiptPurpose()) }));
			lc.setStyle("font-weight:bold;color: #191a1c;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(receiptHeader.getTotFeeAmount(), formatter));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(BigDecimal.ZERO, formatter));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			this.listBoxPastdues.appendChild(item);
			totalPaidAmount = totalPaidAmount.add(receiptHeader.getTotFeeAmount());
		}

		// Excess / EMI In Advance Settlement Amount
		if (StringUtils.equals(receiptHeader.getExcessAdjustTo(), RepayConstants.EXCESSADJUSTTO_EXCESS)
				|| StringUtils.equals(receiptHeader.getExcessAdjustTo(), RepayConstants.EXCESSADJUSTTO_EMIINADV)) {
			item = new Listitem();
			lc = new Listcell(PennantAppUtil.getlabelDesc(getReceiptHeader().getExcessAdjustTo(),
					PennantStaticListUtil.getExcessAdjustmentTypes()) + " Adjustment");
			lc.setStyle("font-weight:bold;color: #05b765;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(
					receiptHeader.getReceiptAmount().subtract(totalPaidAmount).subtract(totalAdvPaidAmount),
					formatter));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(BigDecimal.ZERO, formatter));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			this.listBoxPastdues.appendChild(item);
			totalPaidAmount = totalPaidAmount
					.add(receiptHeader.getReceiptAmount().subtract(totalPaidAmount).subtract(totalAdvPaidAmount));
		}

		// Creating Pastdue Totals to verify against calculations & for validation
		if (totalPaidAmount.compareTo(BigDecimal.ZERO) > 0 || totalWaivedAmount.compareTo(BigDecimal.ZERO) > 0) {
			addFooter(totalPaidAmount, totalWaivedAmount, formatter, true);
		}

		// Creating Manual Advise Totals to verify against calculations & for validation
		if (totalAdvPaidAmount.compareTo(BigDecimal.ZERO) > 0 || totalAdvWaivedAmount.compareTo(BigDecimal.ZERO) > 0) {
			addFooter(totalAdvPaidAmount, totalAdvWaivedAmount, formatter, false);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Adding footer to show Totals
	 * 
	 * @param dueAmount
	 * @param paidAmount
	 * @param waivedAmount
	 * @param formatter
	 * @param isPastDue
	 */
	private void addFooter(BigDecimal paidAmount, BigDecimal waivedAmount, int formatter, boolean isPastDue) {

		// Creating Totals to verify against calculations & for validation
		Listitem item = new Listitem();
		item.setStyle("background-color: #C0EBDF;align:bottom;");
		Listcell lc = new Listcell(Labels.getLabel("label_RecceiptDialog_AllocationType_Totals"));
		lc.setStyle("font-weight:bold;");
		lc.setParent(item);

		lc = new Listcell(PennantAppUtil.amountFormate(paidAmount, formatter));
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.setParent(item);

		lc = new Listcell(PennantAppUtil.amountFormate(waivedAmount, formatter));
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.setParent(item);

		if (isPastDue) {
			this.listBoxPastdues.appendChild(item);
		} else {
			this.listBoxManualAdvises.appendChild(item);
		}
	}

	/**
	 * Method for Rendering Receipt Amount Details
	 */
	private void doFillReceipts(FinReceiptDetail receiptDetail, int finFormatter) {
		logger.debug("Entering");

		Listitem item = new Listitem();
		Listcell lc = null;
		String label = "";
		if (StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_EXCESS)
				|| StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_EMIINADV)
				|| StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_EMIINADV)) {

			label = Labels.getLabel("label_RecceiptDialog_ExcessType_" + receiptDetail.getPaymentType());

		} else if (StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_PAYABLE)) {
			label = receiptDetail.getFeeTypeDesc();
		} else {
			label = PennantAppUtil.getlabelDesc(receiptDetail.getPaymentType(),
					PennantStaticListUtil.getReceiptModes());
		}

		lc = new Listcell(label);
		lc.setStyle("font-weight:bold;color: #191a1c;");
		lc.setParent(item);

		lc = new Listcell(PennantApplicationUtil.amountFormate(receiptDetail.getAmount(), finFormatter));
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		lc = new Listcell(Labels.getLabel("label_ReceiptCancellationDialog_Status_" + receiptDetail.getStatus()));
		if (StringUtils.equals(receiptDetail.getStatus(), RepayConstants.PAYSTATUS_APPROVED)
				|| StringUtils.equals(receiptDetail.getStatus(), RepayConstants.PAYSTATUS_FEES)) {
			lc.setStyle("font-weight:bold;color: #0252d3;");
		} else if (StringUtils.equals(receiptDetail.getStatus(), RepayConstants.PAYSTATUS_REALIZED)) {
			lc.setStyle("font-weight:bold;color: #00a83d;");
		} else if (StringUtils.equals(receiptDetail.getStatus(), RepayConstants.PAYSTATUS_BOUNCE)) {
			lc.setStyle("font-weight:bold;color: #f44b42;");
		} else if (StringUtils.equals(receiptDetail.getStatus(), RepayConstants.PAYSTATUS_CANCEL)) {
			lc.setStyle("font-weight:bold;color: #f48341;");
		}
		lc.setParent(item);
		this.listBoxReceipts.appendChild(item);
		logger.debug("Leaving");
	}

	// Printer integration starts

	public void onClick$btnPrint(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		int finFormatter = CurrencyUtil.getFormat(getReceiptHeader().getFinCcy());
		doClearMessage();
		try {

			String reportName = "GOLDLOAN_CashierReceipt";
			String templatePath = PathUtil.getPath(PathUtil.REPORTS_FINANCE) + "/";
			String templateName = reportName + PennantConstants.DOC_TYPE_WORD_EXT;
			AgreementEngine engine = new AgreementEngine(templatePath, templatePath);
			engine.setTemplate(templateName);
			engine.loadTemplate();
			reportName = "CashierReceipt";

			CashierReceipt cashierReceipt = new CashierReceipt();
			cashierReceipt.setUserName(getUserWorkspace().getLoggedInUser().getUserName() + " - "
					+ getUserWorkspace().getLoggedInUser().getFullName());

			String branchDesc = getBranchService().getBranchDesc(getUserWorkspace().getLoggedInUser().getBranchCode());
			String branch = getUserWorkspace().getLoggedInUser().getBranchCode();
			if (StringUtils.isNotEmpty(branchDesc)) {
				branch = branch + " - " + branchDesc;
			}

			cashierReceipt.setUserBranch(branch);
			cashierReceipt.setFinReference(this.finReference.getValue());
			cashierReceipt.setCustName(this.custCIF.getValue());
			cashierReceipt.setReceiptAmount(PennantApplicationUtil.amountFormate(
					PennantApplicationUtil.unFormateAmount(this.receiptAmount.getActualValue(), finFormatter),
					finFormatter));
			cashierReceipt.setReceiptAmountInWords(
					NumberToEnglishWords.getAmountInText(this.receiptAmount.getActualValue(), ""));
			cashierReceipt.setAppDate(DateUtility.formatToLongDate(derivedAppDate));

			Date eventFromDate = this.receivedDate.getValue();
			if (this.realizationDate.getValue() != null
					&& DateUtility.compare(this.realizationDate.getValue(), eventFromDate) > 0) {
				eventFromDate = this.realizationDate.getValue();
			}
			cashierReceipt.setReceiptDate(DateUtility.formatToLongDate(eventFromDate));

			cashierReceipt.setReceiptNo(this.paymentRef.getValue());
			if (StringUtils.equals(getComboboxValue(receiptMode), RepayConstants.RECEIPTMODE_CASH)) {
				cashierReceipt.setFundingAc(getUserWorkspace().getLoggedInUser().getBranchCode() + "CASH");
			} else {
				cashierReceipt
						.setFundingAc(this.fundingAccount.getValue() + " - " + this.fundingAccount.getDescription());
			}
			cashierReceipt.setPaymentMode(this.receiptMode.getSelectedItem().getLabel().toString());

			boolean isDirectPrint = true;
			try {
				if (isDirectPrint) {
					try {
						engine.mergeFields(cashierReceipt);
						byte[] documentByteArray = engine.getDocumentInByteArray(SaveFormat.PDF);
						String encodedString = java.util.Base64.getEncoder().encodeToString(documentByteArray);
						Clients.evalJavaScript("PrinterUtil.print('window_ReceiptEnquiryDialog','onPrintSuccess','"
								+ encodedString + "')");

					} catch (Exception e) {
						MessageUtil.showError(Labels.getLabel("message.error.printerNotImpl"));
					}
				} else {
					engine.mergeFields(cashierReceipt);
					byte[] documentByteArray = engine.getDocumentInByteArray(SaveFormat.PDF);

					// Downloading Document including print
					Filedownload.save(new AMedia(reportName, "pdf", "application/pdf", documentByteArray));
				}
			} catch (Exception e) {
				MessageUtil.showError(Labels.getLabel("message.error.agreementNotFound"));
			}

		} catch (Exception e) {
			MessageUtil.showError(Labels.getLabel("message.error.agreementNotFound"));
		}
		logger.debug(Literal.LEAVING);
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

	public BranchService getBranchService() {
		return branchService;
	}

	public void setBranchService(BranchService branchService) {
		this.branchService = branchService;
	}

}
