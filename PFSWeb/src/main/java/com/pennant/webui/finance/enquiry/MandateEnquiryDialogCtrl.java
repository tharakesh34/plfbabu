/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : MandateDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 18-10-2016 * * Modified
 * Date : 18-10-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 18-10-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.finance.enquiry;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.North;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.FrequencyBox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.NumberToEnglishWords;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.pff.extension.MandateExtension;
import com.pennant.pff.mandate.MandateUtil;
import com.pennant.webui.mandate.mandate.MandateListCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.dms.service.DMSService;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/Mandate/mandateDialog.zul file. <br>
 * ************************************************************<br>
 */
public class MandateEnquiryDialogCtrl extends GFCBaseCtrl<Mandate> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(MandateEnquiryDialogCtrl.class);

	protected Window window_FinMandateEnquiryDialog;
	protected ExtendedCombobox custID;
	protected ExtendedCombobox mandateRef;
	protected Combobox mandateType;
	protected ExtendedCombobox bankBranchID;
	protected Textbox bank;
	protected Textbox city;
	protected Textbox micr;
	protected Textbox ifsc;
	protected Textbox accNumber;
	protected Textbox accHolderName;
	protected Datebox inputDate;
	protected Textbox jointAccHolderName;
	protected Combobox accType;
	protected Checkbox openMandate;
	protected Datebox startDate;
	protected Datebox expiryDate;
	protected CurrencyBox maxLimit;
	protected FrequencyBox periodicity;
	protected Textbox phoneCountryCode;
	protected Textbox phoneAreaCode;
	protected Textbox phoneNumber;
	protected Combobox status;
	protected Textbox approvalID;
	protected Groupbox gb_basicDetails;
	protected Groupbox gb_listBoxFinances;
	protected Checkbox useExisting;
	protected Checkbox active;
	protected Textbox reason;
	protected Textbox umrNumber;
	protected Space space_Reason;
	protected Space space_Expirydate;
	protected Textbox documentName;
	protected Button btnViewMandateDoc;

	private boolean fromLoanEnquiry = false;

	// not auto wired vars
	private Mandate mandate;
	private transient MandateListCtrl mandateListCtrl;

	protected Button btnProcess;
	protected Button btnView;

	// Added BarCode and Reg Status
	protected Uppercasebox barCodeNumber;
	protected Label amountInWords;
	protected Label regStatus;
	protected ExtendedCombobox finReference;
	protected Checkbox swapIsActive;
	protected Label label_RegStatus;
	private ExtendedCombobox entityCode;
	protected Row row_MandateSource;
	protected Textbox eMandateReferenceNo;
	protected ExtendedCombobox eMandateSource;

	// ServiceDAOs / Domain Classes
	private transient MandateService mandateService;

	private final List<ValueLabel> accTypeList = MandateUtil.getAccountTypes();
	private final List<ValueLabel> statusTypeList = MandateUtil.getMandateStatus();

	protected Listbox listBoxMandateFinExposure;
	public transient int ccyFormatter = 0;
	protected North north_mandate;

	/* loan related declrations */

	long mandateID = 0;
	protected Row rowStatus;
	private Tabpanel tabPanel_dialogWindow;
	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	protected ExtendedCombobox partnerBank;
	protected Label label_PartnerBank;
	protected Combobox enquiryCombobox;
	protected boolean disbEnquiry = false;
	protected DMSService dMSService;

	/**
	 * default constructor.<br>
	 */
	public MandateEnquiryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "MandateDialog";
	}

	// ************************************************* //
	// *************** Component Events **************** //
	// ************************************************* //

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected Mandate object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_FinMandateEnquiryDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinMandateEnquiryDialog);

		try {
			// READ OVERHANDED params !

			if (arguments.containsKey("fromLoanEnquiry")) {
				fromLoanEnquiry = (Boolean) arguments.get("fromLoanEnquiry");
			}

			if (arguments.containsKey("tabPaneldialogWindow")) {
				tabPanel_dialogWindow = (Tabpanel) arguments.get("tabPaneldialogWindow");
			}

			if (arguments.containsKey("financeEnquiryHeaderDialogCtrl")) {
				this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) arguments
						.get("financeEnquiryHeaderDialogCtrl");
			}

			// READ OVERHANDED params !
			if (arguments.containsKey("mandate")) {
				this.mandate = (Mandate) arguments.get("mandate");
				Mandate befImage = new Mandate();
				BeanUtils.copyProperties(this.mandate, befImage);
				this.mandate.setBefImage(befImage);
				setMandate(this.mandate);
			} else {
				setMandate(null);
			}

			if (getMandate() != null) {
				ccyFormatter = CurrencyUtil.getFormat(getMandate().getMandateCcy());
			}

			getUserWorkspace().allocateAuthorities(super.pageRightName);

			if (arguments.containsKey("disbEnquiry")) {
				disbEnquiry = (boolean) arguments.get("disbEnquiry");
			}

			enquiryCombobox = (Combobox) arguments.get("enuiryCombobox");

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getMandate());
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.accNumber.setMaxlength(50);
		this.accHolderName.setMaxlength(100);
		this.jointAccHolderName.setMaxlength(50);
		this.startDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.expiryDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.inputDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.maxLimit.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.maxLimit.setScale(ccyFormatter);
		this.maxLimit.setTextBoxWidth(171);
		this.maxLimit.setMandatory(false);

		this.periodicity.setMandatoryStyle(false);
		this.phoneCountryCode.setMaxlength(3);
		this.phoneCountryCode.setWidth("50px");
		this.phoneAreaCode.setMaxlength(3);
		this.phoneAreaCode.setWidth("50px");
		this.phoneNumber.setMaxlength(10);
		this.phoneNumber.setWidth("180px");
		this.approvalID.setMaxlength(50);

		this.custID.setModuleName("Customer");
		this.custID.setMandatoryStyle(false);
		this.custID.setValueColumn("CustCIF");
		this.custID.setDescColumn("CustShrtName");
		this.custID.setDisplayStyle(2);
		this.custID.setValidateColumns(new String[] { "CustID", "CustCIF" });

		this.bankBranchID.setModuleName("BankBranch");
		this.bankBranchID.setMandatoryStyle(false);
		this.bankBranchID.setValueColumn("BranchCode");
		this.bankBranchID.setDescColumn("BranchDesc");
		this.bankBranchID.setDisplayStyle(2);
		this.bankBranchID.setValidateColumns(new String[] { "BranchCode", "BranchDesc" });

		this.mandateRef.setModuleName("Mandate");
		this.mandateRef.setMandatoryStyle(false);
		this.mandateRef.setValueColumn("MandateID");
		this.mandateRef.setDescColumn("CustID");
		this.mandateRef.setDisplayStyle(2);
		this.mandateRef.setValidateColumns(new String[] { "MandateID", "CustID" });

		this.umrNumber.setReadonly(true);

		this.active.setChecked(true);

		this.barCodeNumber.setMaxlength(10);

		this.finReference.setMaxlength(20);
		this.finReference.setTextBoxWidth(120);
		this.finReference.setModuleName("FinanceManagement");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setValidateColumns(new String[] { "FinReference" });

		this.entityCode.setMaxlength(8);
		this.entityCode.setMandatoryStyle(false);
		this.entityCode.setModuleName("Entity");
		this.entityCode.setValueColumn("EntityCode");
		this.entityCode.setDescColumn("EntityDesc");
		this.entityCode.setValidateColumns(new String[] { "EntityCode" });

		if (MandateExtension.PARTNER_BANK_REQ) {
			this.label_PartnerBank.setVisible(true);
			this.partnerBank.setVisible(true);
			this.partnerBank.setMaxlength(8);
			this.partnerBank.setMandatoryStyle(false);
			this.partnerBank.setModuleName("PartnerBank");
			this.partnerBank.setValueColumn("PartnerBankCode");
			this.partnerBank.setDescColumn("PartnerBankName");
			this.partnerBank.setValidateColumns(new String[] { "PartnerBankCode" });
		}
		this.eMandateSource.setModuleName("Mandate_Source");
		this.eMandateSource.setMandatoryStyle(true);
		this.eMandateSource.setDisplayStyle(2);
		this.eMandateSource.setValueColumn("SourceCode");
		this.eMandateSource.setDescColumn("SourceDesc");
		this.eMandateSource.setValidateColumns(new String[] { "SourceCode" });

		this.eMandateReferenceNo.setMaxlength(100);
		this.row_MandateSource.setVisible(true);

		logger.debug("Leaving");
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		if (disbEnquiry && enquiryCombobox != null) {
			this.enquiryCombobox.setSelectedIndex(0);
			this.window_FinMandateEnquiryDialog.onClose();
		} else {
			doClose(false);
		}
	}

	// ****************************************************************+
	// ************************ GUI operations ************************+
	// ****************************************************************+

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aMandate
	 * @throws InterruptedException
	 */
	public void doShowDialog(Mandate aMandate) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aMandate.isNewRecord()) {
			this.inputDate.setValue(SysParamUtil.getAppDate());
			// setFocus
			this.custID.focus();
		} else {
			this.custID.setReadonly(true);
			doReadOnly();
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents(aMandate);
			doDesignByMode();

			if (fromLoanEnquiry) {
				if (tabPanel_dialogWindow != null) {
					int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount()
							* 20;
					this.window_FinMandateEnquiryDialog.setHeight(this.borderLayoutHeight - rowsHeight - 30 + "px");
					tabPanel_dialogWindow.appendChild(this.window_FinMandateEnquiryDialog);
				}
			} else if (disbEnquiry) {
				setDialog(DialogType.MODAL);
			} else {
				setDialog(DialogType.EMBEDDED);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private void doDesignByMode() {
		if (fromLoanEnquiry) {
			this.gb_listBoxFinances.setVisible(false);
			this.north_mandate.setVisible(false);
		}
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering ");
		readOnlyComponent(true, this.custID);
		readOnlyComponent(true, this.mandateRef);
		readOnlyComponent(true, this.inputDate);
		readOnlyComponent(true, this.mandateType);
		readOnlyComponent(true, this.bankBranchID);
		readOnlyComponent(true, this.accNumber);
		readOnlyComponent(true, this.accHolderName);
		readOnlyComponent(true, this.jointAccHolderName);
		readOnlyComponent(true, this.reason);
		readOnlyComponent(true, this.accType);
		readOnlyComponent(true, this.openMandate);
		readOnlyComponent(true, this.startDate);
		readOnlyComponent(true, this.expiryDate);
		readOnlyComponent(true, this.maxLimit);
		readOnlyComponent(true, this.periodicity);
		readOnlyComponent(true, this.phoneCountryCode);
		readOnlyComponent(true, this.phoneAreaCode);
		readOnlyComponent(true, this.phoneNumber);
		readOnlyComponent(true, this.status);
		readOnlyComponent(true, this.approvalID);
		readOnlyComponent(true, this.umrNumber);
		readOnlyComponent(true, this.barCodeNumber);
		readOnlyComponent(true, this.finReference);
		readOnlyComponent(true, this.swapIsActive);
		readOnlyComponent(true, this.entityCode);
		readOnlyComponent(true, this.partnerBank);
		readOnlyComponent(true, this.eMandateReferenceNo);
		readOnlyComponent(true, this.eMandateSource);

		logger.debug("Leaving ");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aMandate Mandate
	 * @param tab
	 */
	public void doWriteBeanToComponents(Mandate aMandate) {
		logger.debug("Entering");
		mandateID = aMandate.getMandateID();

		if (aMandate.getCustID() != Long.MIN_VALUE && aMandate.getCustID() != 0) {
			this.custID.setAttribute("custID", aMandate.getCustID());
			this.custID.setValue(aMandate.getCustCIF(), aMandate.getCustShrtName());
		}
		fillComboBox(this.status, aMandate.getStatus(), statusTypeList, "");
		this.reason.setValue(aMandate.getReason());
		doWriteData(aMandate);
		if (!fromLoanEnquiry) {
			doFillManFinanceExposureDetails(getMandateService().getMandateFinanceDetailById(aMandate.getMandateID()));
		}

		this.approvalID.setValue(aMandate.getApprovalID());
		this.inputDate.setValue(aMandate.getInputDate());

		logger.debug("Leaving");
	}

	private void doWriteData(Mandate aMandate) {

		if (aMandate.getMandateID() != 0 && aMandate.getMandateID() != Long.MIN_VALUE) {
			this.mandateRef.setAttribute("mandateID", aMandate.getMandateID());
			this.mandateRef.setValue(String.valueOf(aMandate.getMandateID()),
					StringUtils.trimToEmpty(aMandate.getMandateRef()));
		}
		fillComboBox(this.mandateType, aMandate.getMandateType(), MandateUtil.getInstrumentTypes(), "");

		if (aMandate.getBankBranchID() != Long.MIN_VALUE && aMandate.getBankBranchID() != 0) {
			this.bankBranchID.setAttribute("bankBranchID", aMandate.getBankBranchID());
			this.bankBranchID.setValue(StringUtils.trimToEmpty(aMandate.getBranchCode()),
					StringUtils.trimToEmpty(aMandate.getBranchDesc()));
		}
		this.city.setValue(StringUtils.trimToEmpty(aMandate.getCity()));
		this.bank.setValue(StringUtils.trimToEmpty(aMandate.getBankName()));
		this.micr.setValue(aMandate.getMICR());
		this.ifsc.setValue(aMandate.getIFSC());
		this.accNumber.setValue(StringUtils.trimToEmpty(aMandate.getAccNumber()));
		this.accHolderName.setValue(aMandate.getAccHolderName());
		this.jointAccHolderName.setValue(aMandate.getJointAccHolderName());
		fillComboBox(this.accType, aMandate.getAccType(), accTypeList, "");
		this.openMandate.setChecked(aMandate.isOpenMandate());
		this.active.setChecked(aMandate.isActive());
		this.startDate.setValue(aMandate.getStartDate());
		this.expiryDate.setValue(aMandate.getExpiryDate());
		this.maxLimit.setValue(CurrencyUtil.parse(aMandate.getMaxLimit(), ccyFormatter));
		this.periodicity.setValue(aMandate.getPeriodicity());
		this.phoneCountryCode.setValue(aMandate.getPhoneCountryCode());
		this.phoneAreaCode.setValue(aMandate.getPhoneAreaCode());
		this.phoneNumber.setValue(aMandate.getPhoneNumber());
		this.umrNumber.setValue(aMandate.getMandateRef());
		this.documentName.setValue(aMandate.getDocumentName());
		this.eMandateSource.setValue(aMandate.geteMandateSource());
		this.eMandateReferenceNo.setValue(aMandate.geteMandateReferenceNo());
		if (aMandate.getDocumentName() == null || aMandate.getDocumentName().equals("")) {
			this.btnViewMandateDoc.setVisible(false);
		}

		long documentRef = aMandate.getDocumentRef();
		if (aMandate.getDocImage() == null && documentRef > 0) {
			mandate.setDocImage(mandateService.getDocumentManImage(documentRef));
		}

		/*
		 * if (mandate.getDocImage() == null) { this.btnViewMandateDoc.setDisabled(true);
		 * this.btnViewMandateDoc.setTooltiptext(Labels.getLabel("label_Mandate_Document")); }
		 */

		this.barCodeNumber.setValue(aMandate.getBarCodeNumber());
		this.finReference.setValue(aMandate.getOrgReference());
		this.swapIsActive.setChecked(aMandate.isSwapIsActive());
		this.amountInWords.setValue(AmtInitialCap());
		this.regStatus
				.setValue(PennantApplicationUtil.getLabelDesc(aMandate.getStatus(), MandateUtil.getMandateStatus()));

		// Entity
		this.entityCode.setValue(aMandate.getEntityCode(), aMandate.getEntityDesc());

		if (this.label_PartnerBank.isVisible() && mandate.getPartnerBankId() != null
				&& aMandate.getPartnerBankId() != Long.MIN_VALUE && aMandate.getPartnerBankId() != 0) {
			this.partnerBank.setAttribute("partnerBankId", aMandate.getPartnerBankId());
			this.partnerBank.setValue(StringUtils.trimToEmpty(aMandate.getPartnerBankCode()),
					StringUtils.trimToEmpty(aMandate.getPartnerBankName()));
		}

	}

	public void doFillManFinanceExposureDetails(List<FinanceEnquiry> manFinanceExposureDetails) {
		this.listBoxMandateFinExposure.getItems().clear();
		if (manFinanceExposureDetails != null) {
			for (FinanceEnquiry finEnquiry : manFinanceExposureDetails) {
				Listitem item = new Listitem();
				Listcell lc = new Listcell(DateUtility.formatToLongDate(finEnquiry.getFinStartDate()));
				lc.setParent(item);
				lc = new Listcell(finEnquiry.getLovDescFinTypeName());
				lc.setParent(item);
				lc = new Listcell(finEnquiry.getFinReference());
				lc.setParent(item);

				BigDecimal totAmt = finEnquiry.getFinCurrAssetValue().add(finEnquiry.getFeeChargeAmt());
				lc = new Listcell(CurrencyUtil.format(totAmt, CurrencyUtil.getFormat(finEnquiry.getFinCcy())));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(finEnquiry.getMaxInstAmount(),
						CurrencyUtil.getFormat(finEnquiry.getFinCcy())));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(CurrencyUtil.format(totAmt.subtract(finEnquiry.getFinRepaymentAmount()),
						CurrencyUtil.getFormat(finEnquiry.getFinCcy())));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(finEnquiry.getFinStatus());
				lc.setParent(item);
				this.listBoxMandateFinExposure.appendChild(item);

			}
		}
	}

	/**
	 * when the "View" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnReason(Event event) {
		logger.debug("Entering");
		Map<String, Object> arg = new HashMap<String, Object>();
		arg.put("mandateId", mandate.getMandateID());

		Executions.createComponents("/WEB-INF/pages/Mandate/MandateStatusList.zul", null, arg);
		logger.debug("Leaving");
	}

	public void onClick$btnViewMandateDoc(Event event) {
		logger.debug("Entering");

		String custCIF = getMandate().getCustCIF();
		String docUri = getMandate().getExternalRef();
		Long docRefId = getMandate().getDocumentRef();
		String docName = getMandate().getDocumentName();
		byte[] docImage = getMandate().getDocImage();
		try {

			if (StringUtils.isNotBlank(docUri)) {
				DocumentDetails dd = dMSService.getExternalDocument(custCIF, docName, docUri);
				mandate.setDocumentName(dd.getDocName());
				mandate.setDocImage(dd.getDocImage());
			} else {
				if (docImage == null) {
					if (docRefId != null && docRefId != Long.MIN_VALUE) {
						mandate.setDocImage(dMSService.getById(docRefId));
					}
				}
			}

			if (mandate.getDocImage() != null) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("mandate", mandate);
				Executions.createComponents("/WEB-INF/pages/util/ImageView.zul", null, map);
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	private String AmtInitialCap() {
		String amtInWords = NumberToEnglishWords.getNumberToWords(this.maxLimit.getActualValue().toBigInteger());

		String[] words = amtInWords.split(" ");
		StringBuilder AmtInWord = new StringBuilder();

		for (int i = 0; i < words.length; i++) {
			if (!words[i].isEmpty()) {

				AmtInWord.append(Character.toUpperCase(words[i].charAt(0))).append(words[i].substring(1)).append(" ");
			}
		}
		return AmtInWord.toString().trim();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public Mandate getMandate() {
		return this.mandate;
	}

	public void setMandate(Mandate mandate) {
		this.mandate = mandate;
	}

	public void setMandateService(MandateService mandateService) {
		this.mandateService = mandateService;
	}

	public MandateService getMandateService() {
		return this.mandateService;
	}

	public void setMandateListCtrl(MandateListCtrl mandateListCtrl) {
		this.mandateListCtrl = mandateListCtrl;
	}

	public MandateListCtrl getMandateListCtrl() {
		return this.mandateListCtrl;
	}

	public void setdMSService(DMSService dMSService) {
		this.dMSService = dMSService;
	}
}
