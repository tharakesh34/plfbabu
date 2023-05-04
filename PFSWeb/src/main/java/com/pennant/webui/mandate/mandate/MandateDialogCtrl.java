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
package com.pennant.webui.mandate.mandate;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.zkoss.text.MessageFormats;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.North;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.FrequencyBox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.NumberToEnglishWords;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.pennydrop.BankAccountValidation;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.systemmasters.EmployerDetail;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.applicationmaster.ClusterService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.backend.service.pennydrop.PennyDropService;
import com.pennant.backend.service.rmtmasters.FinTypePartnerBankService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.pff.extension.MandateExtension;
import com.pennant.pff.extension.PartnerBankExtension;
import com.pennant.pff.mandate.InstrumentType;
import com.pennant.pff.mandate.MandateStatus;
import com.pennant.pff.mandate.MandateUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTMobileNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.finance.enquiry.FinanceEnquiryHeaderDialogCtrl;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.finance.financemain.FinanceMainBaseCtrl;
import com.pennant.webui.finance.financemain.JointAccountDetailDialogCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.ScreenCTL;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.interfacebajaj.MandateRegistrationListCtrl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.DocType;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.core.util.MediaUtil;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.document.external.ExternalDocumentManager;
import com.pennanttech.pff.external.BankAccountValidationService;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/Mandate/mandateDialog.zul file. <br>
 * ************************************************************<br>
 */
public class MandateDialogCtrl extends GFCBaseCtrl<Mandate> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(MandateDialogCtrl.class);

	protected Window window_MandateDialog;
	private Tabpanel tabPanel_dialogWindow;
	protected Groupbox basicDetailsGroupbox;
	protected Label labelUseExisting;
	protected Checkbox useExisting;
	protected ExtendedCombobox mandateRef;
	protected ExtendedCombobox custID;
	protected Button btnSearchCustCIF;
	protected Label custNameLabel;
	private ExtendedCombobox entityCode;
	protected ExtendedCombobox finReference;
	protected Combobox mandateType;

	protected Groupbox mandateDetailsGroupbox;
	protected Checkbox externalMandate;
	protected Textbox umrNumber;
	protected Row emandateRow;
	protected Row finreferenceRow;
	protected Textbox eMandateReferenceNo;
	protected ExtendedCombobox eMandateSource;
	private Checkbox securityMandate;
	protected Checkbox openMandate;
	protected Checkbox defaultMandate;
	protected Datebox startDate;
	protected Datebox expiryDate;
	protected CurrencyBox maxLimit;
	protected FrequencyBox periodicity;
	protected Row holdRow;
	protected Combobox holdReason;
	protected Label regStatus;
	protected Row mandateStatusRow;
	protected Combobox mandateStatus;
	protected Button btnReason;
	protected Row remarksRow;
	protected Textbox remarks;
	protected Textbox documentName;

	protected Groupbox mandateSwapGroupbox;
	protected Checkbox swapMandate;
	private Datebox swapEffectiveDate;

	protected Groupbox accDetailsGroupbox;
	protected ExtendedCombobox bankBranchID;
	protected Textbox bank;
	protected Textbox city;
	protected Label cityName;
	protected ExtendedCombobox micr;
	protected Textbox ifsc;
	protected Textbox accNumber;
	protected Button btnFetchAccountDetails;
	protected Textbox accHolderName;
	protected Textbox jointAccHolderName;
	protected Combobox accType;
	protected Label amountInWords;
	protected Textbox phoneNumber;

	protected Groupbox dasGroupbox;
	protected Row dasRow;
	private ExtendedCombobox employerID;
	private Textbox employeeNo;

	protected Groupbox otherDetailsGroupbox;
	protected Datebox inputDate;

	protected Row partnerBankRow;
	protected ExtendedCombobox partnerBank;
	protected Checkbox active;

	protected Button btnPennyDropResult;

	protected Row pennyDropRow;
	protected Textbox pennyDropResult;
	protected Textbox txnDetails;

	protected Button btnUploadDoc;
	protected Groupbox mandateDocGroupBox;
	protected Iframe mandatedoc;
	private byte[] imagebyte;
	protected Button btnHelp;
	protected Button btnProcess;
	protected Button btnView;
	protected North north_mandate;
	protected Groupbox finBasicdetails;
	protected Groupbox listBoxFinancesGroupbox;
	protected Listbox listBoxMandateFinExposure;

	private transient static String btnCtroller_ClassPrefix = "button_MandateDialog_";
	private boolean notes_Entered = false;

	private transient MandateRegistrationListCtrl mandateRegistrationListCtrl;
	private transient MandateListCtrl mandateListCtrl;
	private transient FinBasicDetailsCtrl finBasicDetailsCtrl;
	private transient FinanceMainBaseCtrl financeMainDialogCtrl;
	private transient FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;

	private String finType = null;
	private Mandate mandate;

	private Tab parenttab = null;
	private boolean enqModule = false;
	private boolean fromLoan = false;
	private boolean registration = false;
	private boolean maintain = false;
	private boolean fromLoanEnquiry = false;
	private boolean disbEnquiry = false;
	private int ccyFormatter = 0;
	private int maxAccNoLength;
	private int minAccNoLength;
	private boolean issecurityMandate = false;
	private transient BankAccountValidation bankAccountValidations;

	private List<ValueLabel> mandateTypeList = MandateUtil.getInstrumentTypes();
	private List<ValueLabel> securityMandateTypeList = MandateUtil.getSecurityInstrumentTypes();
	private final List<ValueLabel> accTypeList = MandateUtil.getAccountTypes();
	private List<ValueLabel> mandateHoldList = PennantAppUtil.getMandateHoldReasons();

	private transient CustomerDetailsService customerDetailsService;
	private transient MandateService mandateService;
	private transient BankDetailService bankDetailService;
	private transient PennyDropService pennyDropService;
	private transient ExternalDocumentManager externalDocumentManager = null;
	private transient BankAccountValidationService bankAccountValidationService;
	private transient FinTypePartnerBankService finTypePartnerBankService;
	private transient ClusterService clusterService;

	/**
	 * default constructor.<br>
	 */
	public MandateDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "MandateDialog";
	}

	private void onCreateFromLoanOrgination() {
		FinanceDetail fd = (FinanceDetail) arguments.get("financeDetail");
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		this.finType = fm.getFinType();

		if (fd.getMandate() != null) {
			this.mandate = fd.getMandate();
			if (!StringUtils.equals(fm.getFinReference(), mandate.getOrgReference())) {
				this.mandate.setUseExisting(true);
			}
		} else {
			this.mandate = new Mandate();
			this.mandate.setNewRecord(true);
			this.mandate.setCustID(fm.getCustID());
			this.mandate.setCustCIF(getCIFForCustomer(fd));
			String custShrtName = fd.getCustomerDetails().getCustomer().getCustShrtName();
			this.mandate.setCustShrtName(custShrtName);

			if (!issecurityMandate) {
				this.mandate.setMandateType(fm.getFinRepayMethod());
			}

			FinanceType ft = fd.getFinScheduleData().getFinanceType();
			this.mandate.setEntityCode(ft.getLovDescEntityCode());
			this.mandate.setEntityDesc(ft.getLovDescEntityDesc());
			this.mandate.setOrgReference(fm.getFinReference());
		}

		addPartnerBankFilter(fm.getFinBranch());

		this.mandate.setWorkflowId(0);

		if (arguments.containsKey("roleCode")) {
			setRole(arguments.get("roleCode").toString());
		}

		if (arguments.containsKey("tab")) {
			parenttab = (Tab) arguments.get("tab");
		}

		if (arguments.containsKey("finHeaderList")) {
			appendFinBasicDetails((ArrayList<Object>) arguments.get("finHeaderList"));
		}

		if (arguments.containsKey("financeMainDialogCtrl")) {
			financeMainDialogCtrl = (FinanceMainBaseCtrl) arguments.get("financeMainDialogCtrl");
		}

		if (arguments.containsKey("securityMandate")) {
			issecurityMandate = (Boolean) arguments.get("securityMandate");
		}

		if (issecurityMandate) {
			fillComboBox(this.mandateType, mandate.getMandateType(), securityMandateTypeList, "");
		} else {
			fillComboBox(this.mandateType, mandate.getMandateType(), mandateTypeList, "");
		}
	}

	private void onCreateFromMandate() {
		this.mandateRegistrationListCtrl = ((MandateRegistrationListCtrl) arguments.get("mandateRegistrationListCtrl"));

		this.finReference.setValue(this.mandate.getFinReference());

		this.mandate.setOrgReference(this.mandate.getFinReference());
		readOnlyComponent(true, this.finReference);

		this.finType = this.mandate.getFinType();

		addPartnerBankFilter(this.mandate.getFinBranch());

		fillComboBox(this.mandateType, mandate.getMandateType(), mandateTypeList, "");

		this.mandateType.setDisabled(true);

		this.btnFetchAccountDetails.setDisabled(false);
		Filter[] filtersProvince = new Filter[1];
		filtersProvince[0] = new Filter("CustId", this.mandate.getCustCIF(), Filter.OP_EQUAL);
		this.mandateRef.setFilters(filtersProvince);
		this.mandateRef.setReadonly(false);
	}

	public void onCreate$window_MandateDialog(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_MandateDialog);

		if (arguments.containsKey("enqModule")) {
			enqModule = (Boolean) arguments.get("enqModule");
		}

		if (arguments.containsKey("fromLoanEnquiry")) {
			fromLoanEnquiry = (Boolean) arguments.get("fromLoanEnquiry");
		}

		if (arguments.containsKey("financeEnquiryHeaderDialogCtrl")) {
			this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) arguments
					.get("financeEnquiryHeaderDialogCtrl");
		}

		if (arguments.containsKey("tabPaneldialogWindow")) {
			tabPanel_dialogWindow = (Tabpanel) arguments.get("tabPaneldialogWindow");
		}

		if (arguments.containsKey("fromLoan")) {
			fromLoan = (Boolean) arguments.get("fromLoan");
		}

		if (arguments.containsKey("registration")) {
			registration = (Boolean) arguments.get("registration");
		}

		if (arguments.containsKey("maintain")) {
			maintain = (Boolean) arguments.get("maintain");
		}

		if (arguments.containsKey("mandate")) {
			this.mandate = (Mandate) arguments.get("mandate");
			Mandate befImage = new Mandate();
			BeanUtils.copyProperties(this.mandate, befImage);
			this.mandate.setBefImage(befImage);
		}

		if (arguments.containsKey("mandateListCtrl")) {
			this.mandateListCtrl = (MandateListCtrl) arguments.get("mandateListCtrl");
		}

		if (fromLoan) {
			onCreateFromLoanOrgination();
		} else {
			onCreateFromMandate();
		}

		this.custID.setValue(this.mandate.getCustCIF());
		this.entityCode.setDescColumn(this.mandate.getEntityDesc());
		this.entityCode.setValue(this.mandate.getEntityCode());

		try {

			if (!enqModule && !fromLoanEnquiry) {
				doLoadWorkFlow(this.mandate.isWorkflow(), this.mandate.getWorkflowId(), this.mandate.getNextTaskId());

				if (isWorkFlowEnabled() && !enqModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}

				getUserWorkspace().allocateAuthorities(super.pageRightName, getRole());

				doCheckRights();

				ccyFormatter = CurrencyUtil.getFormat(this.mandate.getMandateCcy());

			}

			doSetFieldProperties();

			doShowDialog(this.mandate);

		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.custID.setModuleName("Customer");
		this.custID.setMandatoryStyle(true);
		this.custID.setValueColumn("CustCIF");
		this.custID.setDescColumn("CustShrtName");
		this.custID.setDisplayStyle(2);
		this.custID.setValidateColumns(new String[] { "CustCIF" });

		this.finReference.setMaxlength(20);
		this.finReference.setTextBoxWidth(130);
		this.finReference.setMandatoryStyle(true);
		this.finReference.setModuleName("FinanceManagement");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setValidateColumns(new String[] { "FinReference" });

		this.accHolderName.setMaxlength(100);
		this.jointAccHolderName.setMaxlength(50);
		this.startDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.expiryDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.inputDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.swapEffectiveDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.maxLimit.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.maxLimit.setScale(ccyFormatter);
		this.maxLimit.setTextBoxWidth(200);
		this.maxLimit.setMandatory(true);
		this.maxLimit.addForward(Events.ON_CLICK, this.window, "onClickMaxLimit");

		this.periodicity.setMandatoryStyle(true);
		this.phoneNumber.setMaxlength(10);
		this.phoneNumber.setWidth("200px");

		this.bankBranchID.setModuleName("BankBranch");
		this.bankBranchID.setMandatoryStyle(true);
		this.bankBranchID.setValueColumn("BranchCode");
		this.bankBranchID.setDescColumn("BranchDesc");
		this.bankBranchID.setDisplayStyle(2);

		this.micr.setFilters(new Filter[] { new Filter("MICR", "", Filter.OP_NOT_EQUAL) });

		if (App.DATABASE == Database.POSTGRES) {
			this.bankBranchID.setValueType(DataType.LONG);
		}

		this.bankBranchID.setValidateColumns(new String[] { "BranchCode" });

		this.micr.setModuleName("BankBranch");
		this.micr.setValueColumn("MICR");
		this.micr.setDisplayStyle(2);
		this.micr.setValidateColumns(new String[] { "MICR" });

		if (App.DATABASE == Database.POSTGRES) {
			this.bankBranchID.setValueType(DataType.LONG);
		}

		this.bankBranchID.setValidateColumns(new String[] { "BranchCode" });

		this.mandateRef.setModuleName("Mandate");
		this.mandateRef.setMandatoryStyle(true);
		this.mandateRef.setValueColumn("MandateID");
		this.mandateRef.setDescColumn("MandateRef");
		this.mandateRef.setDisplayStyle(2);
		this.mandateRef.setInputAllowed(false);
		this.mandateRef.setValueType(DataType.LONG);
		this.mandateRef.setValidateColumns(new String[] { "MandateID" });

		if (fromLoan) {
			addMandateFilters(null);
		}

		this.active.setChecked(true);
		this.remarks.setMaxlength(60);
		this.umrNumber.setReadonly(true);
		this.documentName.setMaxlength(150);

		this.btnFetchAccountDetails.addEventListener(Events.ON_CLICK, event -> fetchAccounts());
		this.btnFetchAccountDetails.setDisabled(true);

		if (StringUtils.isNotBlank(this.mandate.getBankCode())) {
			BankDetail bankDetail = bankDetailService.getAccNoLengthByCode(this.mandate.getBankCode());
			maxAccNoLength = bankDetail.getAccNoLength();
			minAccNoLength = bankDetail.getMinAccNoLength();
		}

		this.entityCode.setMaxlength(8);
		this.entityCode.setDisplayStyle(2);
		this.entityCode.setTextBoxWidth(150);

		this.partnerBankRow.setVisible(MandateExtension.PARTNER_BANK_REQ);
		this.partnerBank.setMaxlength(14);
		this.partnerBank.setDisplayStyle(2);
		this.partnerBank.setMandatoryStyle(true);
		this.partnerBank.setWidth("200px");

		if (PartnerBankExtension.BRANCH_WISE_MAPPING) {
			this.partnerBank.setModuleName("FinTypePartner");
		} else {
			this.partnerBank.setModuleName("FinTypePartnerBank_Mandates");
		}

		this.partnerBank.setValueColumn("PartnerBankCode");
		this.partnerBank.setDescColumn("PartnerBankName");
		this.partnerBank.setValidateColumns(new String[] { "PartnerBankCode" });

		this.eMandateSource.setModuleName("Mandate_Sources");
		this.eMandateSource.setMandatoryStyle(true);
		this.eMandateSource.setDisplayStyle(2);
		this.eMandateSource.setValueColumn("Code");
		this.eMandateSource.setDescColumn("Description");
		this.eMandateSource.setValidateColumns(new String[] { "Code" });

		this.eMandateReferenceNo.setMaxlength(50);

		this.employerID.setInputAllowed(true);
		this.employerID.setMandatoryStyle(true);
		this.employerID.setModuleName("EmployerDetails");
		this.employerID.setTextBoxWidth(200);
		this.employerID.setValueColumn("EmployerId");
		this.employerID.setDescColumn("EmpName");
		this.employerID.setValueType(DataType.LONG);
		this.employerID.setFilters(new Filter[] { new Filter("AllowDAS", 1, Filter.OP_EQUAL) });
		this.employerID.setValidateColumns(new String[] { "EmployerId" });

		if (fromLoan) {
			this.remarksRow.setVisible(false);
			this.mandateStatusRow.setVisible(false);
		}

		this.employeeNo.setMaxlength(200);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	private void doCheckRights() {
		if (!enqModule) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_MandateDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_MandateDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_MandateDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_MandateDialog_btnSave"));
		}

		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);
	}

	public void onClick$btnEdit(Event event) {
		doEdit();
	}

	public void onClick$btnDelete(Event event) {
		doDelete();
	}

	public void onClick$btnSave(Event event) {
		doSave();
	}

	public void onClick$btnCancel(Event event) {
		doCancel();
	}

	public void onClick$btnHelp(Event event) {
		MessageUtil.showHelpWindow(event, window);
	}

	public void onClick$btnProcess(Event event) {
		doSave();
	}

	public void onClick$btnReason(Event event) {
		Map<String, Object> arg = new HashMap<String, Object>();
		arg.put("mandateId", mandate.getMandateID());

		Executions.createComponents("/WEB-INF/pages/Mandate/MandateStatusList.zul", null, arg);
	}

	public void onClick$btnClose(Event event) throws InterruptedException {
		doClose(this.btnSave.isVisible());
	}

	public void onClick$btnNotes(Event event) {
		try {

			ScreenCTL.displayNotes(
					getNotes("Mandate", String.valueOf(this.mandate.getMandateID()), this.mandate.getVersion()), this);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	public void onClick$btnPennyDropResult(Event event) {
		if (bankAccountValidationService == null) {
			return;
		}

		doSetValidation(true);

		BankAccountValidation bav = new BankAccountValidation();

		if (this.mandate.getOrgReference() != null) {
			bav.setInitiateReference(this.mandate.getOrgReference());
		}

		bav.setInitiateReference(String.valueOf(this.custID.getValue()));
		bav.setUserDetails(getUserWorkspace().getLoggedInUser());

		List<WrongValueException> wve = new ArrayList<>();

		try {
			if (this.accNumber.getValue() != null) {
				bav.setAcctNum(PennantApplicationUtil.unFormatAccountNumber(this.accNumber.getValue()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.bankBranchID.getValue() != null) {
				bav.setiFSC(this.ifsc.getValue());
			}
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

		int count = pennyDropService.getPennyDropCount(bav.getAcctNum(), bav.getiFSC());
		if (count > 0) {
			MessageUtil.showMessage("This Account number with IFSC code already validated.");
			return;
		}

		try {
			boolean status = false;
			if (bankAccountValidationService != null) {
				status = bankAccountValidationService.validateBankAccount(bav);
			}

			if (status) {
				this.pennyDropResult.setValue("Sucess");
			} else {
				this.pennyDropResult.setValue("Fail");
			}

			bav.setStatus(status);
			bav.setInitiateType("M");

			pennyDropService.savePennyDropSts(bav);
		} catch (Exception e) {
			MessageUtil.showMessage(e.getMessage());
		}
	}

	public void onCheck$useExisting(Event event) {
		doClearMessage();
		useExisting();

		if (!this.useExisting.isChecked() && !this.mandate.isUseExisting()) {
			doWriteData(this.mandate);
		} else {
			this.mandateRef.setAttribute("mandateID", Long.MIN_VALUE);
			this.periodicity.setValue(MandateConstants.MANDATE_DEFAULT_FRQ);
			this.startDate.setValue(SysParamUtil.getAppDate());
		}
	}

	public void onCheck$externalMandate(Event event) {
		if (this.externalMandate.isChecked()) {
			readOnlyComponent(isReadOnly("MandateDialog_UmrNumber"), this.umrNumber);
			this.umrNumber.clearErrorMessage();
		} else {
			this.umrNumber.setErrorMessage("");
			this.umrNumber.setConstraint("");
			this.umrNumber.setValue("");
			readOnlyComponent(true, this.umrNumber);
		}

	}

	public void onCheck$openMandate(Event event) {
		checkOpenMandate();
	}

	public void onCheck$swapMandate(Event event) {
		logger.debug(Literal.ENTERING);

		if (this.swapMandate.isChecked()) {
			this.swapEffectiveDate.setReadonly(false);
			this.swapEffectiveDate.setDisabled(false);

			if (MandateExtension.SWAP_EFFECTIVE_DATE_DEFAULT) {
				this.swapEffectiveDate.setValue(SysParamUtil.getAppDate());
			}
		} else {
			this.swapEffectiveDate.setReadonly(true);
			this.swapEffectiveDate.setConstraint("");
			this.swapEffectiveDate.setValue(null);
			this.swapEffectiveDate.setDisabled(true);
		}
	}

	private void checkOpenMandate() {
		if (this.openMandate.isChecked()) {
			readOnlyComponent(true, this.expiryDate);
			this.expiryDate.setValue(null);
		} else {
			if (this.useExisting.isChecked()) {
				readOnlyComponent(true, this.expiryDate);
			} else {
				readOnlyComponent(isReadOnly("MandateDialog_ExpiryDate"), this.expiryDate);
			}
		}
	}

	public void fetchAccounts() {

		Object obj = this.custID.getAttribute("custID");

		if (obj == null) {
			return;
		}

		Object dataObject = null;
		long custId = Long.parseLong(this.custID.getAttribute("custID").toString());
		Filter[] filter = new Filter[2];
		filter[0] = new Filter("CustID", custId, Filter.OP_EQUAL);
		filter[1] = new Filter("RepaymentFrom", "Y", Filter.OP_EQUAL);

		dataObject = ExtendedSearchListBox.show(this.window, "CustomerBankInfoAccntNumbers", filter, "");

		if (!(dataObject instanceof CustomerBankInfo)) {
			return;
		}

		this.ifsc.setValue("");
		this.bankBranchID.setValue("");
		this.city.setValue("");

		CustomerBankInfo acDetail = (CustomerBankInfo) dataObject;

		this.accNumber.setValue(acDetail.getAccountNumber());
		this.accHolderName.setValue(acDetail.getAccountHolderName());

		if (acDetail.getiFSC() != null) {
			this.ifsc.setValue(acDetail.getiFSC());
		} else {
			this.ifsc.setValue("");
		}

		Long branchId = acDetail.getBankBranchID();

		if (branchId != null && branchId > 0) {
			this.bankBranchID.setValue(String.valueOf(branchId));
		} else {
			this.bankBranchID.setValue("");
		}

		if (acDetail.getCity() != null) {
			this.city.setValue(acDetail.getCity());
		} else {
			this.city.setValue("");
		}

		this.bankBranchID.setAttribute("bankBranchID", branchId);
		this.bankBranchID.setValue(acDetail.getBranchCode(), acDetail.getBankBranch());
		this.bank.setValue(acDetail.getBankName());
		this.micr.setValue(acDetail.getMicr());
		this.ifsc.setValue(acDetail.getiFSC());
		this.city.setValue(acDetail.getCity());

		if (maxAccNoLength != 0) {
			this.accNumber.setMaxlength(maxAccNoLength);
		} else {
			this.accNumber.setMaxlength(LengthConstants.LEN_ACCOUNT);
		}

	}

	private void useExisting() {
		boolean checked = this.useExisting.isChecked();
		if (checked) {
			readOnlyComponent(isReadOnly("MandateDialog_MandateRef"), this.mandateRef);
			this.mandateRef.setButtonDisabled(isReadOnly("MandateDialog_MandateRef"));
			readOnlyComponent(true, this.bankBranchID);
			readOnlyComponent(true, this.accNumber);
			readOnlyComponent(true, this.accHolderName);
			readOnlyComponent(true, this.jointAccHolderName);
			readOnlyComponent(true, this.accType);
			readOnlyComponent(true, this.maxLimit);
			readOnlyComponent(true, this.periodicity);
			readOnlyComponent(true, this.phoneNumber);
			readOnlyComponent(true, this.startDate);
			readOnlyComponent(true, this.expiryDate);
			readOnlyComponent(true, this.openMandate);
			readOnlyComponent(true, this.btnUploadDoc);
			readOnlyComponent(true, swapMandate);
			readOnlyComponent(true, this.txnDetails);
			readOnlyComponent(true, this.defaultMandate);
			readOnlyComponent(true, this.umrNumber);
			readOnlyComponent(true, this.txnDetails);
			readOnlyComponent(true, this.partnerBank);

		} else {
			readOnlyComponent(true, this.mandateRef);
			this.mandateRef.setButtonDisabled(true);
			readOnlyComponent(isReadOnly("MandateDialog_BankBranchID"), this.bankBranchID);
			readOnlyComponent(isReadOnly("MandateDialog_AccNumber"), this.accNumber);
			readOnlyComponent(isReadOnly("MandateDialog_AccHolderName"), this.accHolderName);
			readOnlyComponent(isReadOnly("MandateDialog_JointAccHolderName"), this.jointAccHolderName);
			readOnlyComponent(isReadOnly("MandateDialog_AccType"), this.accType);
			readOnlyComponent(isReadOnly("MandateDialog_MaxLimit"), this.maxLimit);
			readOnlyComponent(isReadOnly("MandateDialog_Periodicity"), this.periodicity);
			readOnlyComponent(isReadOnly("MandateDialog_PhoneNumber"), this.phoneNumber);
			readOnlyComponent(isReadOnly("MandateDialog_StartDate"), this.startDate);
			readOnlyComponent(isReadOnly("MandateDialog_ExpiryDate"), this.expiryDate);
			readOnlyComponent(isReadOnly("MandateDialog_OpenMandate"), this.openMandate);
			readOnlyComponent(isReadOnly("MandateDialog_BtnUploadDoc"), this.btnUploadDoc);
			this.maxLimit.setMandatory(true);
			readOnlyComponent(isReadOnly("MandateDialog_SwapIsActive"), swapMandate);
			readOnlyComponent(isReadOnly("MasterDialog_TxnDetails"), txnDetails);
			readOnlyComponent(isReadOnly("MandateDialog_DefaultMandate"), defaultMandate);
			readOnlyComponent(isReadOnly("MandateDialog_PartnerBankId"), this.partnerBank);
			readOnlyComponent(isReadOnly("MandateDialog_umrNumber"), this.umrNumber);
			readOnlyComponent(isReadOnly("MandateDialog_MICR"), this.micr);

		}
		readOnlyComponent(isReadOnly("MandateDialog_eMandateSource"), eMandateSource);
		readOnlyComponent(isReadOnly("MandateDialog_eMandateReferenceNo"), eMandateReferenceNo);
		clearMandatedata();
	}

	private void clearMandatedata() {
		this.mandateRef.setValue("", "");
		this.bankBranchID.setValue("", "");
		this.accNumber.setValue("");
		this.accHolderName.setValue("");
		this.city.setValue("");
		this.cityName.setValue("");
		this.bank.setValue("");
		this.micr.setValue("");
		this.ifsc.setValue("");
		this.jointAccHolderName.setValue("");
		this.startDate.setText("");
		this.expiryDate.setText("");
		this.accType.setValue("");
		this.maxLimit.setValue(BigDecimal.ZERO);
		this.periodicity.setValue("");
		this.phoneNumber.setValue("");
		this.umrNumber.setValue("");
		this.openMandate.setChecked(false);
		this.documentName.setValue("");
		this.mandatedoc.setContent(null);
		this.finReference.setValue("");
		this.regStatus.setValue("");
		this.amountInWords.setValue("");
		this.swapMandate.setChecked(false);
		this.txnDetails.setValue("");
		this.defaultMandate.setChecked(false);
		this.partnerBank.setValue("");
		this.eMandateSource.setValue("");
		this.eMandateReferenceNo.setValue("");
	}

	public void onChange$mandateType(Event event) {
		String mandateType = this.mandateType.getSelectedItem().getValue().toString();
		this.bankBranchID.setValue("");
		this.bankBranchID.setDescription("");
		this.bank.setValue("");
		this.micr.setValue("");
		this.ifsc.setValue("");
		this.city.setValue("");
		this.cityName.setValue("");
		this.accNumber.setValue("");

		onChangeMandateType(mandateType);

	}

	private void onChangeMandateType(String mandateType) {
		InstrumentType instrumentType = InstrumentType.getType(mandateType);

		if (instrumentType == null) {
			return;
		}

		doEditFieldByInstrument(instrumentType);
	}

	private void doEditFieldByInstrument(InstrumentType instrumentType) {
		if (InstrumentType.SI == instrumentType || InstrumentType.DAS == instrumentType) {
			doSetReadOnly();
			this.emandateRow.setVisible(false);
			this.dasGroupbox.setVisible(false);
			this.accDetailsGroupbox.setVisible(false);
			this.mandateSwapGroupbox.setVisible(false);
			this.mandateDetailsGroupbox.setVisible(false);
			this.otherDetailsGroupbox.setVisible(false);
			this.openMandate.setChecked(false);
			this.useExisting.setChecked(false);
		} else if (!enqModule || !fromLoanEnquiry) {
			doEdit();
		}

		if (InstrumentType.EMANDATE == instrumentType) {
			this.emandateRow.setVisible(true);
			readOnlyComponent(isReadOnly("MandateDialog_eMandateReferenceNo"), this.eMandateReferenceNo);
			readOnlyComponent(isReadOnly("MandateDialog_eMandateSource"), this.eMandateSource);
		}

		if (instrumentType == InstrumentType.DAS) {
			this.dasGroupbox.setVisible(true);
			this.dasRow.setVisible(true);

			if (fromLoan) {
				this.mandateSwapGroupbox.setVisible(false);
			} else {
				this.mandateSwapGroupbox.setVisible(true);

				readOnlyComponent(isReadOnly("MandateDialog_SwapIsActive"), this.swapMandate);
				readOnlyComponent(isReadOnly("MandateDialog_SwapEffectiveDate"), this.swapEffectiveDate);
			}
			readOnlyComponent(isReadOnly("MandateDialog_EmployerID"), this.employerID);
			readOnlyComponent(isReadOnly("MandateDialog_EmployeeNo"), this.employeeNo);

		}

		if (InstrumentType.SI == instrumentType) {
			this.mandateSwapGroupbox.setVisible(!fromLoan);
			this.accDetailsGroupbox.setVisible(true);

			readOnlyComponent(isReadOnly("MandateDialog_SwapIsActive"), this.swapMandate);
			readOnlyComponent(isReadOnly("MandateDialog_SwapEffectiveDate"), this.swapEffectiveDate);
			readOnlyComponent(isReadOnly("MandateDialog_BankBranchID"), this.bankBranchID);
			readOnlyComponent(isReadOnly("MandateDialog_AccNumber"), this.accNumber);
			readOnlyComponent(isReadOnly("MandateDialog_AccType"), this.accType);
			readOnlyComponent(isReadOnly("MandateDialog_AccHolderName"), this.accHolderName);
			readOnlyComponent(isReadOnly("MandateDialog_JointAccHolderName"), this.jointAccHolderName);
			readOnlyComponent(isReadOnly("MandateDialog_MICR"), this.micr);

			String bankcode = SysParamUtil.getValueAsString("BANK_CODE");
			Filter[] filters = new Filter[1];
			filters[0] = new Filter("BankCode", bankcode, Filter.OP_EQUAL);
			this.bankBranchID.setFilters(filters);
			this.micr.setFilters(filters);
		}

		switch (instrumentType) {
		case ECS:
		case DD:
		case NACH:
		case EMANDATE:
			this.mandateDetailsGroupbox.setVisible(true);
			this.accDetailsGroupbox.setVisible(true);
			this.otherDetailsGroupbox.setVisible(true);
			this.mandateSwapGroupbox.setVisible(true);
			this.dasGroupbox.setVisible(false);

			if (fromLoan) {
				this.mandateSwapGroupbox.setVisible(false);
				readOnlyComponent(isReadOnly("MandateDialog_MandateRef"), this.mandateRef);
			}

			this.bankBranchID.setFilters(new Filter[] { new Filter(instrumentType.name(), 1, Filter.OP_EQUAL) });
			this.micr.setFilters(new Filter[] { new Filter(instrumentType.name(), 1, Filter.OP_EQUAL) });
			break;
		default:
			break;
		}

		if (issecurityMandate) {
			this.accDetailsGroupbox.setVisible(true);
			this.mandateDetailsGroupbox.setVisible(true);
			this.otherDetailsGroupbox.setVisible(true);
			this.useExisting.setVisible(true);
			this.mandateSwapGroupbox.setVisible(false);
			this.dasGroupbox.setVisible(false);
			doEdit();
		}

		if (fromLoanEnquiry || enqModule) {
			readOnlyComponent(true, this.active);
		}
	}

	public void doShowDialog(Mandate aMandate) {
		logger.debug(Literal.ENTERING);

		if (aMandate.isNewRecord()) {
			this.btnCtrl.setInitNew();

			Date appDate = SysParamUtil.getAppDate();
			Date sysDate = DateUtil.getSysDate();

			if (DateUtil.compare(appDate, sysDate) == 0) {
				this.inputDate.setValue(sysDate);
			} else {
				this.inputDate.setValue(appDate);
			}

			doEdit();

			this.custID.focus();
		} else {
			this.custID.setReadonly(true);
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aMandate.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
				if (fromLoan && !enqModule) {
					doEdit();
				}
			}
		}

		try {
			if (aMandate != null) {
				String acctNumber = aMandate.getAccNumber();
				String ifscCode = aMandate.getIFSC();
				bankAccountValidations = pennyDropService.getPennyDropStatusDataByAcc(acctNumber, ifscCode);
			}

			doWriteBeanToComponents(aMandate);

			doDesignByStatus(aMandate);

			doDesignByMode();

			if (fromLoan) {
				financeMainDialogCtrl.setMandateDialogCtrl(this);

				if (parenttab != null && !issecurityMandate) {
					checkTabDisplay(aMandate.getMandateType(), false);
				}

				if (issecurityMandate) {
					checkTabDisplaySecurityTab(aMandate.getMandateType());
				}

			} else if (fromLoanEnquiry) {
				if (tabPanel_dialogWindow != null) {
					int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount()
							* 20;
					this.window.setHeight(this.borderLayoutHeight - rowsHeight - 30 + "px");
					tabPanel_dialogWindow.appendChild(this.window);
				}
			} else if (disbEnquiry) {
				setDialog(DialogType.MODAL);
			} else {
				setDialog(DialogType.EMBEDDED);
			}

			this.mandatedoc.setHeight((borderLayoutHeight - 50) + "px");

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void doDesignByStatus(Mandate aMandate) {
		String status = StringUtils.trimToEmpty(aMandate.getStatus());

		if (status.equals("") || status.equals(PennantConstants.List_Select)) {
			mandateStatusRow.setVisible(false);
			remarksRow.setVisible(false);
		} else {
			mandateStatusRow.setVisible(true);
			remarksRow.setVisible(true);
		}

		if (MandateStatus.isRejected(status)) {
			readOnlyComponent(true, mandateStatus);
			readOnlyComponent(true, remarks);
		}

		if (MandateStatus.isNew(status) || MandateStatus.isInprocess(status) || enqModule || fromLoanEnquiry) {
			readOnlyComponent(true, this.mandateStatus);
			readOnlyComponent(true, remarks);

			mandateStatusRow.setVisible(true);
			remarksRow.setVisible(true);
			this.remarks.setValue("");
		}

		if (MandateStatus.isApproved(status) || MandateStatus.isHold(status) || MandateStatus.isRelease(status)
				|| enqModule || fromLoanEnquiry) {
			readOnlyComponent(true, this.mandateRef);
			readOnlyComponent(true, this.mandateType);
			readOnlyComponent(true, this.bankBranchID);
			readOnlyComponent(true, this.accNumber);
			readOnlyComponent(true, this.accHolderName);
			readOnlyComponent(true, this.jointAccHolderName);
			readOnlyComponent(true, this.accType);
			readOnlyComponent(true, this.maxLimit);
			readOnlyComponent(true, this.periodicity);
			readOnlyComponent(true, this.phoneNumber);
			readOnlyComponent(true, this.startDate);
			readOnlyComponent(true, this.expiryDate);
			readOnlyComponent(true, this.openMandate);
			readOnlyComponent(true, this.swapMandate);
			readOnlyComponent(true, this.swapEffectiveDate);
			readOnlyComponent(true, this.txnDetails);
			readOnlyComponent(true, this.defaultMandate);
			readOnlyComponent(true, this.umrNumber);
			readOnlyComponent(true, this.eMandateReferenceNo);
			readOnlyComponent(true, this.eMandateSource);
			readOnlyComponent(true, this.holdReason);
			readOnlyComponent(true, this.partnerBank);
			readOnlyComponent(true, this.externalMandate);
			readOnlyComponent(true, this.employerID);
			readOnlyComponent(true, this.employeeNo);
			readOnlyComponent(true, this.micr);
			readOnlyComponent(true, this.inputDate);
		}

		if (MandateStatus.isApproved(status) || MandateStatus.isRelease(status) || MandateStatus.isHold(status)) {
			holdRow.setVisible(true);
			if (fromLoanEnquiry || enqModule) {
				readOnlyComponent(true, this.holdReason);
			} else {
				readOnlyComponent(isReadOnly("MandateDialog_HoldReason"), this.holdReason);
			}
		}
	}

	private void doDesignByMode() {
		if (fromLoanEnquiry) {
			this.north_mandate.setVisible(false);
			this.finreferenceRow.setVisible(false);
		}

		if (enqModule) {
			this.btnCancel.setVisible(false);
			this.btnSave.setVisible(false);
			this.btnDelete.setVisible(false);
			this.btnNotes.setVisible(false);
			this.listBoxFinancesGroupbox.setVisible(true);
			return;
		}

		if (registration) {
			this.remarksRow.setVisible(true);
			this.mandateStatusRow.setVisible(true);

			readOnlyComponent(false, this.mandateStatus);
			readOnlyComponent(false, this.remarks);

			this.btnCancel.setVisible(false);
			this.btnEdit.setVisible(false);
			this.btnSave.setVisible(false);
			this.btnNotes.setVisible(false);
			this.btnDelete.setVisible(false);
			this.btnProcess.setVisible(true);
		}

		if (fromLoan) {
			this.north_mandate.setVisible(false);

			if (MandateExtension.ALLOW_CO_APP) {
				readOnlyComponent(MandateExtension.ALLOW_CO_APP, this.custID);
			} else {
				readOnlyComponent(true, this.custID);
			}

			this.remarksRow.setVisible(true);

			readOnlyComponent(true, this.finReference);
		}

		if (StringUtils.isEmpty(this.mandate.getRecordType())
				&& StringUtils.isNotEmpty(this.mandate.getRecordStatus())) {
			readOnlyComponent(true, this.mandateType);
		}
	}

	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.mandate.isNewRecord()) {
			this.btnCancel.setVisible(false);

			if (fromLoan && MandateExtension.ALLOW_CO_APP) {
				this.custID.setReadonly(false);
			}

		} else {
			this.btnCancel.setVisible(true);
			this.custID.setReadonly(true);
		}

		if (!fromLoan) {
			this.useExisting.setVisible(false);
			this.labelUseExisting.setVisible(false);
		}

		if (fromLoan) {
			readOnlyComponent(true, this.mandateType);
		}

		if (StringUtils.isNotEmpty(this.mandate.getOrgReference())) {
			readOnlyComponent(true, this.finReference);
		}

		if (MandateExtension.ACCOUNT_DETAILS_READONLY) {
			readOnlyComponent(true, accNumber);
			readOnlyComponent(true, bankBranchID);
			readOnlyComponent(true, micr);
		} else {
			readOnlyComponent(isReadOnly("MandateDialog_AccNumber"), this.accNumber);
			readOnlyComponent(isReadOnly("MandateDialog_BankBranchID"), this.bankBranchID);
			readOnlyComponent(isReadOnly("MandateDialog_MICR"), this.micr);
		}

		if (fromLoan || issecurityMandate) {
			readOnlyComponent(true, this.mandateRef);
			this.mandateRef.setButtonDisabled(true);
		} else {
			readOnlyComponent(isReadOnly("MandateDialog_MandateRef"), this.mandateRef);
			this.mandateRef.setButtonDisabled(isReadOnly("MandateDialog_MandateRef"));
		}

		readOnlyComponent(isReadOnly("MandateDialog_MandateRef"), this.useExisting);
		readOnlyComponent(isReadOnly("MandateDialog_AccHolderName"), this.accHolderName);
		readOnlyComponent(isReadOnly("MandateDialog_JointAccHolderName"), this.jointAccHolderName);
		readOnlyComponent(isReadOnly("MandateDialog_AccType"), this.accType);
		readOnlyComponent(isReadOnly("MandateDialog_OpenMandate"), this.openMandate);
		readOnlyComponent(isReadOnly("MandateDialog_StartDate"), this.startDate);
		if (!openMandate.isChecked()) {
			readOnlyComponent(isReadOnly("MandateDialog_ExpiryDate"), this.expiryDate);
		}
		readOnlyComponent(isReadOnly("MandateDialog_MaxLimit"), this.maxLimit);
		readOnlyComponent(isReadOnly("MandateDialog_Periodicity"), this.periodicity);
		readOnlyComponent(isReadOnly("MandateDialog_PhoneNumber"), this.phoneNumber);
		readOnlyComponent(isReadOnly("MandateDialog_Status"), this.mandateStatus);
		readOnlyComponent(isReadOnly("MandateDialog_Status"), this.remarks);
		readOnlyComponent(isReadOnly("MandateDialog_InputDate"), this.inputDate);
		readOnlyComponent(isReadOnly("MandateDialog_ExternalMandate"), this.externalMandate);
		readOnlyComponent(true, this.umrNumber);
		readOnlyComponent(isReadOnly("MandateDialog_SwapIsActive"), this.swapMandate);
		readOnlyComponent(isReadOnly("MandateDialog_TxnDetails"), this.txnDetails);
		readOnlyComponent(isReadOnly("MandateDialog_DefaultMandate"), this.defaultMandate);
		readOnlyComponent(isReadOnly("MandateDialog_Active"), this.active);
		readOnlyComponent(isReadOnly("MandateDialog_SwapEffectiveDate"), this.swapEffectiveDate);
		readOnlyComponent(isReadOnly("MandateDialog_HoldReason"), this.holdReason);

		if (issecurityMandate) {
			readOnlyComponent(isReadOnly("MandateDialog_SecurityMandate"), this.securityMandate);
		} else {
			readOnlyComponent(true, this.securityMandate);
		}

		readOnlyComponent(isReadOnly("MandateDialog_EmployerID"), this.employerID);
		readOnlyComponent(isReadOnly("MandateDialog_EmployeeNo"), this.employeeNo);
		readOnlyComponent(isReadOnly("button_MandateDialog_btnPennyDropResult"), this.btnPennyDropResult);

		if (MandateExtension.PARTNER_BANK_REQ) {
			readOnlyComponent(isReadOnly("MandateDialog_PartnerBankId"), this.partnerBank);
		} else {
			readOnlyComponent(true, partnerBank);
		}

		readOnlyComponent(isReadOnly("MandateDialog_eMandateSource"), this.eMandateSource);
		readOnlyComponent(isReadOnly("MandateDialog_eMandateReferenceNo"), this.eMandateReferenceNo);

		if (bankAccountValidationService != null && !enqiryModule) {
			btnPennyDropResult.setVisible(true);
		} else {
			btnPennyDropResult.setVisible(false);
		}

		if (this.finReference.getValue() == null) {
			readOnlyComponent(true, partnerBank);
			this.partnerBank.setMandatoryStyle(false);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.mandate.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}

			if (StringUtils.equals(this.mandate.getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED)) {
				this.btnNotes.setVisible(true);
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || fromLoan) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.mandate.getBefImage());

		doReadOnly();

		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	private void doSetReadOnly() {
		readOnlyComponent(true, this.useExisting);
		readOnlyComponent(true, this.mandateRef);
		readOnlyComponent(isReadOnly("button_MandateDialog_btnPennyDropResult"), this.btnPennyDropResult);
		readOnlyComponent(true, this.custID);
		readOnlyComponent(true, this.entityCode);
		readOnlyComponent(true, this.finReference);
		readOnlyComponent(true, this.mandateType);
		readOnlyComponent(true, this.bankBranchID);
		readOnlyComponent(true, this.eMandateSource);
		readOnlyComponent(true, this.eMandateReferenceNo);
		readOnlyComponent(true, this.bank);
		readOnlyComponent(true, this.city);
		readOnlyComponent(true, this.micr);
		readOnlyComponent(true, this.ifsc);
		readOnlyComponent(true, this.accNumber);
		readOnlyComponent(true, this.btnFetchAccountDetails);
		readOnlyComponent(true, this.accHolderName);
		readOnlyComponent(true, this.jointAccHolderName);
		readOnlyComponent(true, this.accType);
		readOnlyComponent(true, this.maxLimit);
		readOnlyComponent(true, this.openMandate);
		readOnlyComponent(true, this.startDate);
		readOnlyComponent(true, this.expiryDate);
		readOnlyComponent(true, this.periodicity);
		readOnlyComponent(true, this.phoneNumber);
		readOnlyComponent(true, this.inputDate);
		readOnlyComponent(true, this.partnerBank);
		readOnlyComponent(true, this.active);
		readOnlyComponent(true, this.defaultMandate);
		readOnlyComponent(true, this.mandateStatus);
		readOnlyComponent(true, this.btnReason);
		readOnlyComponent(true, this.remarks);

		readOnlyComponent(true, this.externalMandate);
		readOnlyComponent(true, this.umrNumber);

		readOnlyComponent(true, this.securityMandate);
		readOnlyComponent(true, this.pennyDropResult);
		readOnlyComponent(true, this.txnDetails);
		readOnlyComponent(true, this.documentName);
		readOnlyComponent(true, this.btnUploadDoc);
		readOnlyComponent(true, this.holdReason);
		readOnlyComponent(true, this.swapMandate);
		readOnlyComponent(true, this.swapEffectiveDate);
		readOnlyComponent(true, this.employerID);
		readOnlyComponent(true, this.employeeNo);
	}

	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		doSetReadOnly();

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}

		logger.debug(Literal.LEAVING);
	}

	public void doWriteBeanToComponents(Mandate aMandate) {
		logger.debug(Literal.ENTERING);

		if (!fromLoanEnquiry) {
			doFillManFinanceExposureDetails(mandateService.getMandateFinanceDetailById(aMandate.getMandateID()));
		}

		this.securityMandate.setChecked(aMandate.isSecurityMandate());

		if (issecurityMandate) {
			fillComboBox(this.mandateType, aMandate.getMandateType(), securityMandateTypeList, "");
		} else {
			fillComboBox(this.mandateType, aMandate.getMandateType(), mandateTypeList, "");
		}

		onChangeMandateType(StringUtils.trimToEmpty(aMandate.getMandateType()));

		if (fromLoan) {
			this.useExisting.setChecked(aMandate.isUseExisting());
			useExisting();
		} else {
			readOnlyComponent(true, this.mandateRef);
		}

		if (aMandate.getCustID() != Long.MIN_VALUE && aMandate.getCustID() != 0) {
			this.custID.setAttribute("custID", aMandate.getCustID());
			this.custID.setValue(aMandate.getCustCIF());
			this.custNameLabel.setValue(aMandate.getCustShrtName());
			this.btnFetchAccountDetails.setDisabled(false);
		}
		this.btnFetchAccountDetails.setDisabled(false);

		List<String> excludeList = new ArrayList<String>();

		if (registration) {
			excludeList.add(MandateStatus.FIN);
			excludeList.add(MandateStatus.NEW);
			excludeList.add(MandateStatus.APPROVED);
			excludeList.add(MandateStatus.AWAITCON);
			excludeList.add(MandateStatus.HOLD);
			excludeList.add(MandateStatus.RELEASE);
			excludeList.add(MandateStatus.CANCEL);
		} else if (maintain) {
			excludeList.add(MandateStatus.FIN);
			excludeList.add(MandateStatus.NEW);
			excludeList.add(MandateStatus.AWAITCON);
			excludeList.add(MandateStatus.REJECTED);
			excludeList.add(MandateStatus.CANCEL);
			excludeList.add(MandateStatus.INPROCESS);

			Mandate oldMnadate = mandateService.getApprovedMandateById(aMandate.getMandateID());

			if (MandateStatus.isHold(oldMnadate.getStatus())) {
				excludeList.add(MandateStatus.HOLD);
			} else {
				excludeList.add(MandateStatus.RELEASE);
			}
		} else if (MandateStatus.isApproved(aMandate.getStatus())) {
			excludeList.add(MandateStatus.FIN);
			excludeList.add(MandateStatus.NEW);
			excludeList.add(MandateStatus.AWAITCON);
			excludeList.add(MandateStatus.REJECTED);
			excludeList.add(MandateStatus.CANCEL);
			excludeList.add(MandateStatus.INPROCESS);
		}

		fillComboBox(this.mandateStatus, aMandate.getStatus(), MandateUtil.getMandateStatus(), excludeList);

		this.remarks.setValue(aMandate.getReason());

		doWriteData(aMandate);

		this.recordStatus.setValue(aMandate.getRecordStatus());

		fillComboBox(this.holdReason, aMandate.getHoldReason(), mandateHoldList, "");

		if (aMandate.isNewRecord()) {
			Date appDate = SysParamUtil.getAppDate();
			Date sysDate = DateUtil.getSysDate();

			if (DateUtil.compare(appDate, sysDate) == 0) {
				this.inputDate.setValue(sysDate);
				this.swapEffectiveDate.setValue(sysDate);
			} else {
				this.inputDate.setValue(appDate);
				if (this.swapMandate.isChecked()) {
					this.swapEffectiveDate.setValue(appDate);
				}
			}

			this.active.setChecked(true);
		} else {
			this.inputDate.setValue(aMandate.getInputDate());
			if (aMandate.getHoldReason() != null && MandateStatus.isHold(aMandate.getStatus())) {
				this.holdReason.setValue(aMandate.getHoldReason());
			}

			this.swapMandate.setChecked(aMandate.isSwapIsActive());

			if (this.swapMandate.isChecked()) {
				this.swapEffectiveDate.setValue(aMandate.getSwapEffectiveDate());
			}
		}

		if (aMandate.getEmployerID() != null) {
			this.employerID.setValue(String.valueOf(aMandate.getEmployerID()));
			this.employerID.setDescription(aMandate.getEmployerName());
		} else {
			this.employerID.setValue("");
			this.employerID.setDescription("");
		}

		this.employeeNo.setValue(aMandate.getEmployeeNo());
		this.externalMandate.setChecked(aMandate.isExternalMandate());

		logger.debug(Literal.LEAVING);
	}

	private void visibleDocFrame(byte[] data) {
		if (data == null) {
			this.mandateDocGroupBox.setVisible(false);
		} else {
			this.mandateDocGroupBox.setVisible(true);
		}
	}

	private void doWriteData(Mandate aMandate) {
		ccyFormatter = CurrencyUtil.getFormat(aMandate.getMandateCcy());

		if (aMandate.isNewRecord()) {
			if (!issecurityMandate && StringUtils.isEmpty(aMandate.getPeriodicity())) {
				aMandate.setPeriodicity(MandateConstants.MANDATE_DEFAULT_FRQ);
			}
			if (aMandate.getStartDate() == null) {
				aMandate.setStartDate(SysParamUtil.getAppDate());
			}
		}

		if (aMandate.getMandateID() != 0 && aMandate.getMandateID() != Long.MIN_VALUE) {
			this.mandateRef.setAttribute("mandateID", aMandate.getMandateID());
			this.mandateRef.setValue(String.valueOf(aMandate.getMandateID()),
					StringUtils.trimToEmpty(aMandate.getMandateRef()));
		}

		Long bankBranchId = aMandate.getBankBranchID();
		if (bankBranchId != null && bankBranchId != Long.MIN_VALUE && bankBranchId != 0) {
			this.bankBranchID.setAttribute("bankBranchID", bankBranchId);
			String branchCode = aMandate.getBranchCode();
			this.bankBranchID.setValue(String.valueOf(branchCode), StringUtils.trimToEmpty(aMandate.getBranchDesc()));
		}

		this.city.setValue(StringUtils.trimToEmpty(aMandate.getCity()));
		this.cityName.setValue(StringUtils.trimToEmpty(aMandate.getPccityName()));
		this.bank.setValue(StringUtils.trimToEmpty(aMandate.getBankName()));
		this.micr.setValue(aMandate.getMICR());
		this.ifsc.setValue(aMandate.getIFSC());
		this.accNumber.setValue(aMandate.getAccNumber());
		this.accHolderName.setValue(aMandate.getAccHolderName());
		this.jointAccHolderName.setValue(aMandate.getJointAccHolderName());

		fillComboBox(this.accType, aMandate.getAccType(), accTypeList, "");

		this.openMandate.setChecked(aMandate.isOpenMandate());
		this.startDate.setValue(aMandate.getStartDate());
		this.expiryDate.setValue(aMandate.getExpiryDate());
		this.maxLimit.setValue(PennantApplicationUtil.formateAmount(aMandate.getMaxLimit(), ccyFormatter));
		this.periodicity.setValue(aMandate.getPeriodicity());
		this.phoneNumber.setValue(aMandate.getPhoneNumber());
		this.remarks.setValue(aMandate.getReason());
		this.umrNumber.setValue(aMandate.getMandateRef());
		this.documentName.setValue(aMandate.getDocumentName());
		this.defaultMandate.setChecked(aMandate.isDefaultMandate());
		this.eMandateSource.setValue(aMandate.geteMandateSource());
		this.eMandateReferenceNo.setValue(aMandate.geteMandateReferenceNo());

		setMandateDocument(aMandate);

		this.finReference.setValue(aMandate.getOrgReference());

		if (!StringUtils.equals(aMandate.getStatus(), PennantConstants.List_Select)) {
			this.regStatus.setValue(
					PennantApplicationUtil.getLabelDesc(aMandate.getStatus(), MandateUtil.getMandateStatus()));
		}

		this.amountInWords.setValue(amountInWords());
		this.swapMandate.setChecked(aMandate.isSwapIsActive());

		if (!enqModule && !registration) {
			checkOpenMandate();
		}

		if (StringUtils.isNotBlank(aMandate.getEntityCode())) {
			this.entityCode.setValue(StringUtils.trimToEmpty(aMandate.getEntityCode()),
					StringUtils.trimToEmpty(aMandate.getEntityDesc()));
		}

		if (bankAccountValidations != null) {
			this.pennyDropResult.setValue(bankAccountValidations.isStatus() ? "Success" : "Fail");
		} else if (fromLoan) {
			if (this.pennyDropResult.isVisible()) {
				BankAccountValidation bankAccountValidations = new BankAccountValidation();
				bankAccountValidations = pennyDropService.getPennyDropStatusDataByAcc(aMandate.getAccNumber(),
						aMandate.getIFSC());
				if (bankAccountValidations != null) {
					this.pennyDropResult.setValue(bankAccountValidations.isStatus() ? "Success" : "Fail");
				}
			}
		} else {
			this.pennyDropResult.setValue("");
		}

		if (mandate.getPartnerBankId() != null && aMandate.getPartnerBankId() != 0
				&& aMandate.getPartnerBankId() != Long.MIN_VALUE) {
			this.partnerBank.setValue(aMandate.getPartnerBankCode());
			this.partnerBank.setDescription(aMandate.getPartnerBankName());
			this.partnerBank.setObject(new FinTypePartnerBank(aMandate.getPartnerBankId()));
		}
	}

	private void setMandateDocument(Mandate aMandate) {
		if (aMandate.getDocImage() == null && StringUtils.isNotBlank(aMandate.getExternalRef())) {
			// Fetch document from interface
			String custCif = aMandate.getCustCIF();
			AMedia media = externalDocumentManager.getDocumentMedia(aMandate.getDocumentName(),
					aMandate.getExternalRef(), custCif);
			if (media != null) {
				mandatedoc.setContent(media);
			} else {
				logger.info(
						"Document is not found in External DMS for the specified Docref:" + aMandate.getExternalRef());
			}
		}
		AMedia amedia = null;
		if (aMandate.getDocImage() != null) {
			amedia = new AMedia(aMandate.getDocumentName(), null, null, aMandate.getDocImage());
			imagebyte = aMandate.getDocImage();
			mandatedoc.setContent(amedia);
		}
	}

	private List<WrongValueException> doWriteComponentsToBean(Mandate aMandate, Tab tab) {
		logger.debug(Literal.ENTERING);

		List<WrongValueException> wve = new ArrayList<>();

		Object mandateID = this.mandateRef.getAttribute("mandateID");

		if (mandateID != null) {
			aMandate.setMandateID((long) mandateID);
		} else {
			aMandate.setMandateID(Long.MIN_VALUE);
		}

		aMandate.setUseExisting(this.useExisting.isChecked());

		// Customer ID
		try {
			this.custID.getValidatedValue();
			Object obj = this.custID.getAttribute("custID");
			if (obj != null) {
				if (!StringUtils.isEmpty(obj.toString())) {
					aMandate.setCustID(Long.valueOf((obj.toString())));
				}
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			String ref = this.mandateRef.getValue();
			if (fromLoan && this.useExisting.isChecked() && StringUtils.isEmpty(ref)) {
				throw new WrongValueException(this.mandateRef.getTextbox(), Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_MandateDialog_MandateRef.value") }));
			}

			Object obj = this.mandateRef.getAttribute("mandateRef");
			if (obj != null) {
				if (!StringUtils.isEmpty(obj.toString())) {
					aMandate.setMandateRef(obj.toString());
				}
			} else {
				aMandate.setMandateRef(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMandate.setMandateType(this.mandateType.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			Object obj = this.bankBranchID.getAttribute("bankBranchID");
			if (obj != null) {
				aMandate.setBankBranchID(Long.valueOf(String.valueOf(obj)));
			} else {
				if (StringUtils.isNotEmpty(this.bankBranchID.getValue())) {
					aMandate.setBankBranchID(Long.valueOf(this.bankBranchID.getValue()));
				} else {
					aMandate.setBankBranchID(null);
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMandate.setAccNumber(PennantApplicationUtil.unFormatAccountNumber(this.accNumber.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMandate.setAccHolderName(this.accHolderName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMandate.setJointAccHolderName(this.jointAccHolderName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.accType.getValue();
			if (this.accType.getSelectedItem() != null && this.accType.getSelectedItem().getValue() != null) {
				aMandate.setAccType(this.accType.getSelectedItem().getValue().toString());
			} else {
				aMandate.setAccType("");
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMandate.setOpenMandate(this.openMandate.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMandate.setStartDate(this.startDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMandate.setExpiryDate(this.expiryDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMandate.setMaxLimit(PennantApplicationUtil.unFormateAmount(this.maxLimit.getActualValue(), ccyFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (issecurityMandate) {
				//
			} else if (this.periodicity.isValidComboValue()) {
				aMandate.setPeriodicity(this.periodicity.getValue() == null ? "" : this.periodicity.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMandate.setPhoneNumber(this.phoneNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {

			if (registration) {
				this.mandateStatus.setConstraint(new StaticListValidator(MandateUtil.getMandateStatus(),
						Labels.getLabel("label_MandateDialog_Status.value")));
			}
			aMandate.setStatus(this.mandateStatus.getSelectedItem() == null ? "#"
					: this.mandateStatus.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMandate.setInputDate(
					DateUtil.getDate(DateUtil.format(this.inputDate.getValue(), PennantConstants.dateFormat)));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMandate.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMandate.setReason(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMandate.setMandateCcy(SysParamUtil.getAppCurrency());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		aMandate.setApprovalID(String.valueOf((getUserWorkspace().getLoggedInUser().getUserId())));

		try {
			aMandate.setDocumentName(this.documentName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMandate.setDocImage(this.imagebyte);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMandate.setOrgReference(StringUtils.trimToNull(this.finReference.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMandate.setSwapIsActive(this.swapMandate.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (aMandate.isSwapIsActive()) {
				aMandate.setSwapEffectiveDate(DateUtil
						.getDate(DateUtil.format(this.swapEffectiveDate.getValue(), PennantConstants.dateFormat)));
			} else {
				aMandate.setSwapEffectiveDate(null);
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMandate.setExternalMandate(this.externalMandate.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		List<ErrorDetail> errors = mandateService.doValidations(aMandate);

		for (ErrorDetail error : errors) {
			String label = null;
			String errorCode = error.getCode();

			if (errorCode.equalsIgnoreCase("90404")) {
				label = Labels.getLabel("label_MandateDialog_BarCodeNumber.value");
			}

			if (errorCode.equalsIgnoreCase("90502")) {
				label = Labels.getLabel("label_MandateDialog_AccHolderName.value");
			}

			if (errorCode.equalsIgnoreCase("90237")) {
				label = Labels.getLabel("label_MandateDialog_JointAccHolderName.value");
			}

			Component component = Path.getComponent("/outerIndexWindow/window_MandateDialog/" + error.getField());
			String msg = MessageFormats.format(error.getMessage(), new String[] { label });

			WrongValueException wa = new WrongValueException(component, msg);
			wve.add(wa);
		}

		try {
			aMandate.setMandateRef(this.umrNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			Object parnerBankObj = this.partnerBank.getObject();
			if (parnerBankObj instanceof FinTypePartnerBank) {
				FinTypePartnerBank partBank = (FinTypePartnerBank) parnerBankObj;
				if (partBank != null && partBank.getPartnerBankID() != 0) {
					aMandate.setPartnerBankId(partBank.getPartnerBankID());
				} else if (partBank != null && partBank.getId() != 0) {
					aMandate.setPartnerBankId(partBank.getId());
				}
			} else {
				Object attribute = this.partnerBank.getAttribute("partnerBankId");

				if (attribute != null) {
					aMandate.setPartnerBankId(Long.valueOf(attribute.toString()));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMandate.setDefaultMandate(this.defaultMandate.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (InstrumentType.isEMandate(aMandate.getMandateType())) {
			try {
				aMandate.seteMandateReferenceNo(this.eMandateReferenceNo.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				// E mandate Source
				Object obj = this.eMandateSource.getAttribute("eMandateSource");
				if (obj != null) {
					aMandate.seteMandateSource(String.valueOf(obj));
				} else {
					if (StringUtils.isNotEmpty(this.eMandateSource.getValue())) {
						aMandate.seteMandateSource((this.eMandateSource.getValue()));
					}
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		try {
			if (MandateStatus.isHold(getComboboxValue(this.mandateStatus)) && !this.holdReason.isDisabled()) {
				if (CollectionUtils.isNotEmpty(mandateHoldList)) {
					Comboitem holdRsn = this.holdReason.getSelectedItem();
					if (holdRsn != null && !PennantConstants.List_Select.equals(holdRsn.getValue().toString())) {
						aMandate.setHoldReason(holdRsn.getValue().toString());
					} else {
						aMandate.setHoldReason(PennantConstants.List_Select);
					}

					if ("#".equals(getComboboxValue(this.holdReason))) {
						throw new WrongValueException(this.holdReason, Labels.getLabel("STATIC_INVALID",
								new String[] { Labels.getLabel("label_MandateDialog_HoldReason.value") }));
					} else {
						aMandate.setHoldReason(holdReason.getValue().toString());
					}
				} else {
					aMandate.setHoldReason(PennantConstants.List_Select);
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			String empId = StringUtils.trimToNull(employerID.getValue());
			aMandate.setEmployerID(empId == null ? null : Long.valueOf(empId));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMandate.setEmployeeNo(this.employeeNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMandate.setSecurityMandate(this.securityMandate.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMandate.setInputDate(this.inputDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (this.swapEffectiveDate.getValue() != null) {
			if (this.swapEffectiveDate.getValue().compareTo(SysParamUtil.getAppDate()) <= 0) {
				throw new WrongValueException(this.swapEffectiveDate,
						Labels.getLabel("DATE_ALLOWED_AFTER",
								new String[] { Labels.getLabel("label_MandateDialog_SwapEffectiveDate.value"),
										DateUtil.formatToShortDate(SysParamUtil.getAppDate()) }));
			}
		}

		logger.debug(Literal.LEAVING);
		return wve;
	}

	private void showErrorDetails(List<WrongValueException> wve) {
		doRemoveValidation();

		if (CollectionUtils.isEmpty(wve)) {
			return;
		}

		logger.debug("Throwing occured Errors By using WrongValueException");
		if (parenttab != null) {
			parenttab.setSelected(true);
		}

		WrongValueException[] wvea = new WrongValueException[wve.size()];

		for (int i = 0; i < wve.size(); i++) {
			wvea[i] = wve.get(i);
			if (i == 0) {
				Component comp = wvea[i].getComponent();
				if (comp instanceof HtmlBasedComponent) {
					Clients.scrollIntoView(comp);
				}
			}
			logger.error(wvea[i]);
		}

		throw new WrongValuesException(wvea);
	}

	private void doSetBasicDetailValidation(boolean validate) {

		if (this.useExisting.isChecked()) {
			this.mandateType.setConstraint(
					new StaticListValidator(mandateTypeList, Labels.getLabel("label_MandateDialog_MandateType.value")));
		} else {
			if (!this.mandateType.isDisabled() && !issecurityMandate) {
				this.mandateType.setConstraint(new StaticListValidator(mandateTypeList,
						Labels.getLabel("label_MandateDialog_MandateType.value")));
			}
		}

		// Customer ID
		if (!this.custID.isReadonly()) {
			this.custID.setConstraint(
					new PTStringValidator(Labels.getLabel("label_MandateDialog_CustID.value"), null, validate, true));
		}

		if (!this.finReference.isReadonly()) {
			this.finReference
					.setConstraint(new PTStringValidator(Labels.getLabel("label_MandateDialog_FinReference.value"),
							null, !ImplementationConstants.CLIENT_NFL));
		}

		if (!this.eMandateSource.isReadonly()) {
			this.eMandateSource.setConstraint(new PTStringValidator(
					Labels.getLabel("label_MandateDialog_E-Mandate_Source.value"), null, validate));
		}

		if (!this.eMandateReferenceNo.isReadonly()) {
			this.eMandateReferenceNo.setConstraint(new PTStringValidator(
					Labels.getLabel("label_MandateDialog_E-Mandate_Reference_No.value"), null, validate));

		}

	}

	private void doSetMandateDetValidation(boolean validate) {
		if (this.externalMandate.isChecked() && !this.umrNumber.isReadonly()) {
			this.umrNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_MandateDialog_UmrNumber.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		Date mandbackDate = DateUtil.addDays(SysParamUtil.getAppDate(),
				-SysParamUtil.getValueAsInt("MANDATE_STARTDATE"));
		Date appExpiryDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");

		if (!this.startDate.isDisabled() && this.startDate.isButtonVisible()) {
			this.startDate.setConstraint(new PTDateValidator(Labels.getLabel("label_MandateDialog_StartDate.value"),
					validate, mandbackDate, appExpiryDate, true));
		}

		if (!this.expiryDate.isDisabled() && this.expiryDate.isButtonVisible()
				&& MandateExtension.EXPIRY_DATE_MANDATORY) {
			try {
				this.expiryDate
						.setConstraint(new PTDateValidator(Labels.getLabel("label_MandateDialog_ExpiryDate.value"),
								validate, this.startDate.getValue(), appExpiryDate, this.openMandate.isChecked()));
			} catch (WrongValueException we) {
				this.expiryDate
						.setConstraint(new PTDateValidator(Labels.getLabel("label_MandateDialog_ExpiryDate.value"),
								MandateExtension.EXPIRY_DATE_MANDATORY, true, null, false));
			}
		}

		if (this.expiryDate.getValue() != null && (this.expiryDate.getValue().compareTo(this.startDate.getValue()) <= 0
				|| this.expiryDate.getValue().after(appExpiryDate))) {
			this.expiryDate.setConstraint(new PTDateValidator(Labels.getLabel("label_MandateDialog_ExpiryDate.value"),
					MandateExtension.EXPIRY_DATE_MANDATORY, this.startDate.getValue(), appExpiryDate, true));
		}

		Date lanMatDate = this.mandate.getLoanMaturityDate();
		Date expDate = this.expiryDate.getValue();

		if (!fromLoan) {
			if (expDate != null && lanMatDate != null && expDate.before(lanMatDate)) {
				this.expiryDate
						.setConstraint(new PTDateValidator(Labels.getLabel("label_MandateDialog_ExpiryDate.value"),
								MandateExtension.EXPIRY_DATE_MANDATORY, lanMatDate, appExpiryDate, true));
			}
		}

		if (!this.maxLimit.isReadonly()) {
			this.maxLimit.setConstraint(new PTDecimalValidator(Labels.getLabel("label_MandateDialog_MaxLimit.value"),
					ccyFormatter, validate, false));
		}

		if (!this.remarks.isReadonly() && (PennantConstants.List_Select.equals(this.mandateStatus.getValue()))) {

			if (this.mandateStatus.getValue().equals(this.mandate.getStatus())) {
				this.remarks
						.setConstraint(new PTStringValidator(Labels.getLabel("label_MandateStatusDialog_Remarks.value"),
								PennantRegularExpressions.REGEX_DESCRIPTION, true));
			}
		}

	}

	private void doSetAccountDetValidation(boolean validate) {

		if (!this.bankBranchID.isReadonly()) {
			this.bankBranchID.setConstraint(
					new PTStringValidator(Labels.getLabel("label_MandateDialog_BankBranchID.value"), null, validate));
		}

		if (!this.phoneNumber.isReadonly()) {
			this.phoneNumber.setConstraint(
					new PTMobileNumberValidator(Labels.getLabel("label_MandateDialog_PhoneNumber.value"), false));
		}

		if (!this.accNumber.isReadonly()) {
			this.accNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_MandateDialog_AccNumber.value"),
					PennantRegularExpressions.REGEX_ACCOUNTNUMBER, validate, minAccNoLength, maxAccNoLength));
		}

		if (!this.accHolderName.isReadonly()) {
			this.accHolderName
					.setConstraint(new PTStringValidator(Labels.getLabel("label_MandateDialog_AccHolderName.value"),
							PennantRegularExpressions.REGEX_ACCOUNT_HOLDER_NAME, validate));
		}

		if (!this.jointAccHolderName.isReadonly()) {
			this.jointAccHolderName.setConstraint(
					new PTStringValidator(Labels.getLabel("label_MandateDialog_JointAccHolderName.value"),
							PennantRegularExpressions.REGEX_ACCOUNT_HOLDER_NAME, false));
		}

		if (!this.accType.isDisabled() && validate) {
			this.accType.setConstraint(
					new StaticListValidator(accTypeList, Labels.getLabel("label_MandateDialog_AccType.value")));
		}

	}

	private void dosetOtherDetValidaion(boolean validate) {
		if (!this.partnerBank.isReadonly() && MandateExtension.PARTNER_BANK_REQ) {
			this.partnerBank.setConstraint(
					new PTStringValidator(Labels.getLabel("label_MandateDialog_PartnerBank.value"), null, true, false));
		}
	}

	private void doSetDasValidation(boolean validate) {
		if (this.dasRow.isVisible() && !this.employerID.isReadonly()) {
			this.employerID.setConstraint(
					new PTStringValidator(Labels.getLabel("label_MandateDialog_EmployerID.value"), null, true, true));
			this.employeeNo.setConstraint(new PTStringValidator(Labels.getLabel("label_MandateDialog_EmployeeNo.value"),
					PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
	}

	private void doSetValidation(boolean validate) {
		logger.debug(Literal.ENTERING);

		if (issecurityMandate && PennantConstants.List_Select.equals(getComboboxValue(this.mandateType))) {
			return;
		}

		if (this.basicDetailsGroupbox.isVisible()) {
			doSetBasicDetailValidation(validate);
		}

		if (this.mandateDetailsGroupbox.isVisible()) {
			doSetMandateDetValidation(validate);
		}

		if (this.accDetailsGroupbox.isVisible()) {
			doSetAccountDetValidation(validate);
		}

		if (this.otherDetailsGroupbox.isVisible()) {
			dosetOtherDetValidaion(validate);
		}

		if (this.dasGroupbox.isVisible()) {
			doSetDasValidation(validate);
		}

		if (this.mandateSwapGroupbox.isVisible()) {
			doSetSwapValidation(validate);
		}
	}

	private void doRemoveValidation() {
		this.custID.setConstraint("");
		this.mandateRef.setConstraint("");
		this.inputDate.setConstraint("");
		this.mandateType.setConstraint("");
		this.bankBranchID.setConstraint("");
		this.accNumber.setConstraint("");
		this.accHolderName.setConstraint("");
		this.jointAccHolderName.setConstraint("");
		this.accType.setConstraint("");
		this.startDate.setConstraint("");
		this.expiryDate.setConstraint("");
		this.maxLimit.setConstraint("");
		this.phoneNumber.setConstraint("");
		this.mandateStatus.setConstraint("");
		this.remarks.setConstraint("");
		this.finReference.setConstraint("");
		this.eMandateReferenceNo.setConstraint("");
		this.eMandateSource.setConstraint("");
		this.partnerBank.setConstraint("");
		this.holdReason.setConstraint("");
		this.employerID.setConstraint("");
		this.employeeNo.setConstraint("");
	}

	@Override
	protected void doClearMessage() {
		this.custID.setErrorMessage("");
		this.mandateRef.setErrorMessage("");
		this.mandateRef.clearErrorMessage();
		this.mandateType.setErrorMessage("");
		this.bankBranchID.setErrorMessage("");
		this.accNumber.setErrorMessage("");
		this.accHolderName.setErrorMessage("");
		this.jointAccHolderName.setErrorMessage("");
		this.accType.setErrorMessage("");
		this.startDate.setErrorMessage("");
		this.expiryDate.setErrorMessage("");
		this.maxLimit.setErrorMessage("");
		this.phoneNumber.setErrorMessage("");
		this.mandateStatus.setErrorMessage("");
		this.remarks.setErrorMessage("");
		this.documentName.setErrorMessage("");
		this.finReference.setErrorMessage("");
		this.partnerBank.setErrorMessage("");

		this.holdReason.setErrorMessage("");
		this.employerID.setErrorMessage("");
		this.employeeNo.setErrorMessage("");
	}

	protected void refreshList() {
		if (registration) {
			mandateRegistrationListCtrl.search();
		} else {
			mandateListCtrl.search();
		}
	}

	public void onFulfill$bankBranchID(Event event) {
		Object dataObject = this.bankBranchID.getObject();

		if (dataObject == null || dataObject instanceof String) {
			this.bank.setValue("");
			this.city.setValue("");
			this.micr.setValue("");
			this.ifsc.setValue("");
			this.cityName.setValue("");

			return;
		}

		BankBranch details = (BankBranch) dataObject;

		this.bankBranchID.setAttribute("bankBranchID", details.getBankBranchID());
		this.bank.setValue(details.getBankName());
		this.micr.setValue(details.getMICR());
		this.ifsc.setValue(details.getIFSC());
		this.city.setValue(details.getCity());
		this.cityName.setValue(details.getPCCityName());

		if (StringUtils.isNotBlank(details.getBankCode())) {
			BankDetail bankDetail = bankDetailService.getAccNoLengthByCode(details.getBankCode());
			maxAccNoLength = bankDetail.getAccNoLength();
			minAccNoLength = bankDetail.getMinAccNoLength();
		}

		this.accNumber.setMaxlength(maxAccNoLength);
	}

	public void onFulfill$micr(Event event) {
		Object dataObject = this.micr.getObject();

		if (StringUtils.isEmpty(this.micr.getValue()) || dataObject == null || dataObject instanceof String) {
			this.bank.setValue("");
			this.city.setValue("");
			this.micr.setValue("");
			this.ifsc.setValue("");
			this.cityName.setValue("");
			this.bankBranchID.setValue("", "");

			return;
		}

		BankBranch details = (BankBranch) dataObject;

		this.bankBranchID.setAttribute("bankBranchID", details.getBankBranchID());
		this.bankBranchID.setValue(details.getBranchCode(), details.getBranchDesc());
		this.bank.setValue(details.getBankName());
		this.micr.setValue(details.getMICR());
		this.ifsc.setValue(details.getIFSC());
		this.city.setValue(details.getCity());
		this.cityName.setValue(details.getPCCityName());

		if (StringUtils.isNotBlank(details.getBankCode())) {
			BankDetail bankDetail = bankDetailService.getAccNoLengthByCode(details.getBankCode());
			maxAccNoLength = bankDetail.getAccNoLength();
			minAccNoLength = bankDetail.getMinAccNoLength();
		}

		this.accNumber.setMaxlength(maxAccNoLength);
	}

	public void onFulfill$mandateRef(Event event) {
		logger.debug(Literal.ENTERING);

		Object dataObject = mandateRef.getObject();

		if (dataObject instanceof String) {
			this.mandateRef.setValue(dataObject.toString());
			this.mandateRef.setAttribute("mandateID", Long.MIN_VALUE);
			clearMandatedata();

			logger.debug(Literal.LEAVING);
			return;
		}

		Mandate details = (Mandate) dataObject;
		if (details != null) {
			this.mandateRef.setAttribute("mandateID", details.getMandateID());
			mandateService.getDocumentImage(details);
			doWriteData(details);
		} else {
			this.mandateRef.setValue("");
			this.mandateRef.setAttribute("mandateID", Long.MIN_VALUE);
			clearMandatedata();
		}

		logger.debug(Literal.LEAVING);
	}

	private void addPartnerBankFilter(String finBranch) {
		if (finType == null) {
			return;
		}

		Filter[] filters = new Filter[4];
		filters[0] = new Filter("Purpose", "R", Filter.OP_EQUAL);
		filters[1] = new Filter("FinType", finType, Filter.OP_EQUAL);
		filters[2] = new Filter("PaymentMode", DisbursementConstants.PAYMENT_TYPE_NEFT, Filter.OP_EQUAL);

		if (!PartnerBankExtension.BRANCH_WISE_MAPPING) {
			filters[3] = new Filter("Active", 1, Filter.OP_EQUAL);

			this.partnerBank.setFilters(filters);

			return;
		}

		Long clusterId = null;
		if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("B")) {
			filters[3] = new Filter("BranchCode", finBranch, Filter.OP_EQUAL);
		} else if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("C")) {
			clusterId = clusterService.getClustersFilter(finBranch);
			filters[3] = new Filter("ClusterId", clusterId, Filter.OP_EQUAL);
		}

		FinTypePartnerBank fpb = new FinTypePartnerBank();

		fpb.setFinType(finType);
		fpb.setPurpose("R");
		fpb.setPaymentMode(DisbursementConstants.PAYMENT_TYPE_NEFT);
		fpb.setBranchCode(finBranch);
		fpb.setClusterId(clusterId);

		List<FinTypePartnerBank> fintypePartnerbank = finTypePartnerBankService.getFinTypePartnerBanks(fpb);

		if (fintypePartnerbank.size() == 1) {
			this.partnerBank.setAttribute("partnerBankId", fintypePartnerbank.get(0).getPartnerBankID());
			this.partnerBank.setValue(fintypePartnerbank.get(0).getPartnerBankCode());
			this.partnerBank.setDescription(fintypePartnerbank.get(0).getPartnerBankName());
		}

		this.partnerBank.setFilters(filters);
	}

	public void onFulfill$finReference(Event event) {
		logger.debug(Literal.ENTERING);

		Object dataObject = finReference.getObject();

		FinanceMain fm = (FinanceMain) dataObject;

		this.finType = null;
		this.finReference.setValue("");
		if (MandateExtension.PARTNER_BANK_REQ) {
			this.partnerBank.setObject("");
			this.partnerBank.setValue("");
		}

		if (fm != null) {
			this.finReference.setValue(fm.getFinReference());
			this.finType = fm.getFinType();

			addPartnerBankFilter(fm.getFinBranch());
		}

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$employerID(Event event) {
		Object dataObject = employerID.getObject();

		if (dataObject instanceof String) {
			this.employerID.setValue(dataObject.toString());
			return;
		}

		EmployerDetail details = (EmployerDetail) dataObject;
		if (details != null) {
			this.employerID.setValue(String.valueOf(details.getEmployerId()));
			this.employerID.setDescription(details.getEmpName());
		} else {
			this.employerID.setValue("");
			this.employerID.setDescription("");
		}
	}

	private String amountInWords() {
		String amtInWords = NumberToEnglishWords.getNumberToWords(this.maxLimit.getActualValue().toBigInteger());

		String[] words = amtInWords.split(" ");
		StringBuilder amount = new StringBuilder();
		for (int i = 0; i < words.length; i++) {
			if (!words[i].isEmpty()) {
				amount.append(Character.toUpperCase(words[i].charAt(0))).append(words[i].substring(1)).append(" ");
			}
		}

		return amount.toString().trim();
	}

	private void doDelete() {
		logger.debug(Literal.ENTERING);
		final Mandate aMandate = new Mandate();
		BeanUtils.copyProperties(this.mandate, aMandate);

		if (this.btnNotes.isVisible() && !notesEntered) {
			MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
			return;
		}

		doDelete(String.valueOf(aMandate.getMandateID()), aMandate);

		logger.debug(Literal.LEAVING);
	}

	public void doClear() {
		this.mandateRef.setValue("");
		this.inputDate.setValue(null);
		this.mandateType.setValue("");
		this.bankBranchID.setValue("");
		this.accNumber.setValue("");
		this.accHolderName.setValue("");
		this.jointAccHolderName.setValue("");
		this.accType.setValue("");
		this.openMandate.setChecked(false);
		this.startDate.setValue(null);
		this.expiryDate.setValue(null);
		this.maxLimit.setValue(BigDecimal.ZERO);
		this.periodicity.setValue("");
		this.phoneNumber.setValue("");
		this.mandateStatus.setValue("");
		this.bank.setValue("");
		this.micr.setValue("");
		this.ifsc.setValue("");
		this.city.setValue("");
		this.remarks.setValue("");
		this.documentName.setValue("");
		this.defaultMandate.setChecked(false);
		this.partnerBank.setValue("");

		this.eMandateSource.setValue("");
		this.eMandateReferenceNo.setValue("");

		this.holdReason.setValue("");
		this.employerID.setValue("");
		this.employeeNo.setValue("");

	}

	public void doSave() {
		logger.debug(Literal.ENTERING);

		final Mandate aMandate = new Mandate();
		BeanUtils.copyProperties(this.mandate, aMandate);
		boolean isNew = false;

		if (isWorkFlowEnabled()) {
			aMandate.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aMandate.getNextTaskId(), aMandate);
		}

		if (!PennantConstants.RECORD_TYPE_DEL.equals(aMandate.getRecordType()) && isValidation()) {
			doSetValidation(true);
			List<WrongValueException> wve = doWriteComponentsToBean(aMandate, null);
			showErrorDetails(wve);
		}

		isNew = aMandate.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aMandate.getRecordType()).equals("")) {
				aMandate.setVersion(aMandate.getVersion() + 1);
				if (isNew) {
					aMandate.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aMandate.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aMandate.setNewRecord(true);
				}
			}
		} else {
			aMandate.setVersion(aMandate.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (registration) {
				aMandate.setModule(MandateConstants.MODULE_REGISTRATION);
			}

			if (doProcess(aMandate, tranType)) {
				if (StringUtils.isBlank(aMandate.getNextTaskId())) {
					aMandate.setNextRoleCode("");
				}

				String msg = PennantApplicationUtil.getSavingStatus(aMandate.getRoleCode(), aMandate.getNextRoleCode(),
						String.valueOf(aMandate.getMandateID()), " Mandate ", aMandate.getRecordStatus());
				Clients.showNotification(msg, "info", null, null, -1);
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	protected boolean doProcess(Mandate aMandate, String tranType) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		aMandate.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aMandate.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aMandate.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {

			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (auditingReq) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			aMandate.setTaskId(getTaskId());
			aMandate.setNextTaskId(getNextTaskId());
			aMandate.setRoleCode(getRole());
			aMandate.setNextRoleCode(getNextRoleCode());

			if (StringUtils.isBlank(getOperationRefs())) {
				processCompleted = doSaveProcess(getAuditHeader(aMandate, tranType), null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader = getAuditHeader(aMandate, PennantConstants.TRAN_WF);

				for (int i = 0; i < list.length; i++) {
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			processCompleted = doSaveProcess(getAuditHeader(aMandate, tranType), null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		Mandate aMandate = (Mandate) auditHeader.getAuditDetail().getModelData();

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
				if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())) {
					auditHeader = mandateService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = mandateService.saveOrUpdate(auditHeader);
				}

			} else {
				if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
					auditHeader = mandateService.doApprove(auditHeader);

					if (PennantConstants.RECORD_TYPE_DEL.equals(aMandate.getRecordType())) {
						deleteNotes = true;
					}

				} else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
					auditHeader = mandateService.doReject(auditHeader);
					if (PennantConstants.RECORD_TYPE_NEW.equals(aMandate.getRecordType())) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes("Mandate", String.valueOf(aMandate.getMandateID()), aMandate.getVersion()),
							true);
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

		logger.debug("return Value:" + processCompleted);
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private AuditHeader getAuditHeader(Mandate aMandate, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aMandate.getBefImage(), aMandate);
		return new AuditHeader(String.valueOf(aMandate.getMandateID()), null, null, null, auditDetail,
				aMandate.getUserDetails(), getOverideMap());
	}

	private void appendFinBasicDetails(List<Object> finHeaderList) {
		final Map<String, Object> map = new HashMap<>();
		map.put("parentCtrl", this);

		if (finHeaderList != null) {
			map.put("finHeaderList", finHeaderList);
		}

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
	}

	public void doSetLabels(List<Object> finHeaderList) {
		finBasicDetailsCtrl.doWriteBeanToComponents((ArrayList<Object>) finHeaderList);
	}

	public void checkTabDisplay(String mandateType, boolean onchange) {
		mandateType = StringUtils.trimToEmpty(mandateType);

		InstrumentType instrumentType = InstrumentType.getType(mandateType);

		if (instrumentType == null) {
			this.parenttab.setVisible(false);
			return;
		}

		if (fromLoan) {
			addMandateFilters(mandateType);
		}

		this.inputDate.setValue(SysParamUtil.getAppDate());
		doEditFieldByInstrument(instrumentType);

		this.parenttab.setVisible(!(instrumentType == InstrumentType.PDC || instrumentType == InstrumentType.MANUAL));

		fillComboBox(this.mandateType, mandateType, mandateTypeList, "");

		if (this.useExisting.isChecked() && onchange) {
			clearMandatedata();
		}
	}

	public void checkTabDisplaySecurityTab(String mandateType) {
		InstrumentType instrumentType = InstrumentType.getType(mandateType);

		if (instrumentType != null) {
			doEditFieldByInstrument(instrumentType);
		} else {
			clearMandatedata();
		}

		if (issecurityMandate) {
			fillComboBox(this.mandateType, mandateType, securityMandateTypeList, "");
		} else {
			fillComboBox(this.mandateType, mandateType, mandateTypeList, "");
		}

		clearMandatedata();
	}

	private void addMandateFilters(String repaymethod) {
		FinanceDetail fd = (FinanceDetail) arguments.get("financeDetail");
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		if (StringUtils.isEmpty(repaymethod)) {
			repaymethod = fm.getFinRepayMethod();
		}

		Filter[] filters = new Filter[5];
		filters[0] = new Filter("CustID", fm.getCustID(), Filter.OP_EQUAL);
		filters[1] = new Filter("MandateType", repaymethod, Filter.OP_EQUAL);
		filters[2] = new Filter("Active", 1, Filter.OP_EQUAL);
		filters[3] = new Filter("STATUS", MandateStatus.REJECTED, Filter.OP_NOT_EQUAL);
		filters[4] = new Filter("EntityCode", mandate.getEntityCode(), Filter.OP_EQUAL);

		this.mandateRef.setFilters(filters);
		this.mandateRef.setWhereClause("(OpenMandate = 1 or OrgReference is null)");
	}

	public void doSave_Mandate(FinanceDetail fd, Tab tab, boolean recSave) {
		logger.debug(Literal.ENTERING);

		doClearMessage();

		boolean validate = false;
		if (!recSave && !isReadOnly("MandateDialog_validate")) {
			validate = true;
		}

		doSetValidation(validate);

		List<WrongValueException> wve = doWriteComponentsToBean(this.mandate, tab);

		String instrumentType = getComboboxValue(this.mandateType);

		if (issecurityMandate && PennantConstants.List_Select.equals(instrumentType)) {
			fd.setSecurityMandate(null);
			return;
		}

		if (!wve.isEmpty() && parenttab != null) {
			parenttab.setSelected(true);
		}

		showErrorDetails(wve);

		if (StringUtils.isBlank(this.mandate.getRecordType())) {
			this.mandate.setVersion(this.mandate.getVersion() + 1);
			this.mandate.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			this.mandate.setNewRecord(true);
		}
		this.mandate.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		this.mandate.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		this.mandate.setUserDetails(getUserWorkspace().getLoggedInUser());

		fd.setMandate(this.mandate);

		logger.debug(Literal.LEAVING);
	}

	private String getCIFForCustomer(FinanceDetail financeDetail) {
		if (financeDetail != null && financeDetail.getCustomerDetails() != null
				&& financeDetail.getCustomerDetails().getCustomer() != null) {
			return StringUtils.trimToEmpty(financeDetail.getCustomerDetails().getCustomer().getCustCIF());
		}
		return "";
	}

	public void onUpload$btnUploadDoc(UploadEvent event) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		Media media = event.getMedia();

		List<DocType> documents = new ArrayList<>();
		documents.add(DocType.PDF);
		documents.add(DocType.GIF);
		documents.add(DocType.JPEG);
		documents.add(DocType.JPG);
		documents.add(DocType.PNG);

		if (!MediaUtil.isValid(media, documents)) {
			MessageUtil.showError(
					Labels.getLabel("upload_document_invalid", new String[] { "pdf or image(gif/.jpeg/jpg/png)" }));
			return;
		}

		browseDoc(media, this.documentName);

		logger.debug(Literal.LEAVING);
	}

	private void browseDoc(Media media, Textbox textbox) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		try {
			String docType = "";
			if (MediaUtil.isPdf(media)) {
				docType = PennantConstants.DOC_TYPE_PDF;
			} else if (MediaUtil.isImage(media)) {
				docType = PennantConstants.DOC_TYPE_IMAGE;
			}

			// Process for Correct Format Document uploading
			String fileName = media.getName();
			byte[] ddaImageData = IOUtils.toByteArray(media.getStreamData());
			// Data Fill by QR Bar Code Reader
			if (docType.equals(PennantConstants.DOC_TYPE_PDF)) {
				this.mandatedoc.setContent(
						new AMedia("document.pdf", "pdf", "application/pdf", new ByteArrayInputStream(ddaImageData)));

			} else if (docType.equals(PennantConstants.DOC_TYPE_IMAGE)) {
				this.mandatedoc.setContent(media);
			}
			this.mandatedoc.setVisible(true);
			textbox.setValue(fileName);
			imagebyte = media.getByteData();

			visibleDocFrame(imagebyte);

		} catch (Exception ex) {
			logger.error("Exception: ", ex);
		}
		logger.debug(Literal.LEAVING);
	}

	public void onChange$mandateStatus(Event event) {
		String mandateStatus = getComboboxValue(this.mandateStatus);

		if (MandateStatus.isHold(mandateStatus)) {
			this.holdReason.setDisabled(false);
		} else {
			this.holdReason.setValue("");
			this.holdReason.setDisabled(true);
		}
	}

	public void onFulfill$maxLimit(Event event) {
		this.amountInWords.setValue(amountInWords());
	}

	public void doSetCustomerFilters() {
		List<String> custCIFs = new ArrayList<>(2);

		if (this.mandate != null) {
			custCIFs.add(this.mandate.getCustCIF());
		}

		if (financeMainDialogCtrl != null) {
			JointAccountDetailDialogCtrl jaddc = financeMainDialogCtrl.getJointAccountDetailDialogCtrl();

			if (jaddc != null) {
				List<Customer> jointAccountCustomers = jaddc.getJointAccountCustomers();
				for (Customer customer : jointAccountCustomers) {
					custCIFs.add(customer.getCustCIF());
				}
			}
		}

		Filter[] filter = new Filter[1];
		filter[0] = new Filter("CustCIF", custCIFs, Filter.OP_IN);
	}

	public void onClick$btnSearchCustCIF(Event event) {
		logger.debug(Literal.ENTERING);

		final Map<String, Object> map = new HashMap<>();

		CustomerDetails customerDetails = customerDetailsService.getCustomerById(this.mandate.getCustID());

		String pageName = PennantAppUtil.getCustomerPageName();

		map.put("customerDetails", customerDetails);
		map.put("enqiryModule", true);
		map.put("dialogCtrl", this);
		map.put("newRecord", false);
		map.put("CustomerEnq", "CustomerEnq");
		map.put("isEnqProcess", true);

		Executions.createComponents(pageName, null, map);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$custID(Event event) {
		Object dataObject = custID.getObject();
		if (dataObject instanceof String) {
			this.custID.setValue(dataObject.toString());
			this.btnFetchAccountDetails.setDisabled(false);
		} else {
			Customer details = (Customer) dataObject;
			if (details != null) {
				this.custID.setAttribute("custID", details.getCustID());
				this.btnFetchAccountDetails.setDisabled(false);
			} else {
				this.accNumber.setValue("");
				this.accHolderName.setValue("");
				this.btnFetchAccountDetails.setDisabled(true);
			}
		}
	}

	public void doFillManFinanceExposureDetails(List<FinanceEnquiry> manFinanceExposureDetails) {
		this.listBoxMandateFinExposure.getItems().clear();
		if (manFinanceExposureDetails != null) {
			for (FinanceEnquiry finEnquiry : manFinanceExposureDetails) {
				Listitem item = new Listitem();
				Listcell lc = new Listcell(DateUtil.formatToLongDate(finEnquiry.getFinStartDate()));
				lc.setParent(item);
				lc = new Listcell(finEnquiry.getLovDescFinTypeName());
				lc.setParent(item);
				lc = new Listcell(finEnquiry.getFinReference());
				lc.setParent(item);

				BigDecimal totAmt = finEnquiry.getFinCurrAssetValue().add(finEnquiry.getFeeChargeAmt());
				lc = new Listcell(
						PennantApplicationUtil.amountFormate(totAmt, CurrencyUtil.getFormat(finEnquiry.getFinCcy())));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(finEnquiry.getMaxInstAmount(),
						CurrencyUtil.getFormat(finEnquiry.getFinCcy())));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(
						PennantApplicationUtil.amountFormate(totAmt.subtract(finEnquiry.getFinRepaymentAmount()),
								CurrencyUtil.getFormat(finEnquiry.getFinCcy())));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(finEnquiry.getFinStatus());
				lc.setParent(item);
				this.listBoxMandateFinExposure.appendChild(item);

			}
		}
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

	@Autowired
	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	@Autowired
	public void setMandateService(MandateService mandateService) {
		this.mandateService = mandateService;
	}

	@Autowired
	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}

	@Autowired
	public void setPennyDropService(PennyDropService pennyDropService) {
		this.pennyDropService = pennyDropService;
	}

	@Autowired
	public void setExternalDocumentManager(ExternalDocumentManager externalDocumentManager) {
		this.externalDocumentManager = externalDocumentManager;
	}

	@Autowired(required = false)
	@Qualifier(value = "bankAccountValidationService")
	public void setBankAccountValidationService(BankAccountValidationService bankAccountValidationService) {
		this.bankAccountValidationService = bankAccountValidationService;
	}
	
	private void doSetSwapValidation(boolean validate) {
		if (this.swapMandate.isChecked() && this.swapEffectiveDate.getValue() == null) {
			this.swapEffectiveDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_MandateDialog_SwapEffectiveDate.value"), true));
		}
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public void setFinTypePartnerBankService(FinTypePartnerBankService finTypePartnerBankService) {
		this.finTypePartnerBankService = finTypePartnerBankService;
	}

	public void setClusterService(ClusterService clusterService) {
		this.clusterService = clusterService;
	}

}
