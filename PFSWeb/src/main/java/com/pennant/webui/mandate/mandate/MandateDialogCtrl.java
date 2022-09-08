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
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.North;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.FrequencyBox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.NumberToEnglishWords;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.dao.pennydrop.PennyDropDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.pennydrop.BankAccountValidation;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.systemmasters.EmployerDetail;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.backend.service.pennydrop.PennyDropService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.component.Uppercasebox;
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
	protected Groupbox gb_basicDetails;

	protected Checkbox useExisting;
	protected ExtendedCombobox mandateRef;
	protected Textbox custID;
	protected Button btnSearchCustCIF;
	private ExtendedCombobox entityCode;
	protected ExtendedCombobox finReference;
	protected Combobox mandateType;
	protected ExtendedCombobox bankBranchID;

	protected Row emandateRow;
	protected Textbox eMandateReferenceNo;
	protected ExtendedCombobox eMandateSource;

	protected Textbox bank;
	protected Textbox city;
	protected Label cityName;
	protected Textbox micr;
	protected Textbox ifsc;
	protected Textbox accNumber;
	protected Button btnFetchAccountDetails;
	protected Textbox accHolderName;
	protected Textbox jointAccHolderName;
	protected Combobox accType;
	protected CurrencyBox maxLimit;
	protected Label amountInWords;
	protected Datebox startDate;
	protected Checkbox openMandate;
	protected Datebox expiryDate;
	protected FrequencyBox periodicity;
	protected Textbox phoneNumber;
	protected Checkbox defaultMandate;
	protected Combobox mandateStatus;
	protected Button btnReason;
	protected Space space_Reason;
	protected Textbox reason;
	protected Uppercasebox barCodeNumber;

	protected Checkbox externalMandate;
	protected Textbox umrNumber;
	private Checkbox securityMandate;

	protected Checkbox swapMandate;
	private Datebox swapEffectiveDate;

	private Checkbox hold;
	private ExtendedCombobox holdReasons;

	protected Row dasRow;
	private ExtendedCombobox employeeID;
	private Textbox employerName;

	protected Datebox inputDate;
	protected ExtendedCombobox partnerBank;
	protected Checkbox active;
	protected Textbox pennyDropResult;
	protected Button btnPennyDropResult;
	protected Textbox txnDetails;
	protected Button btnUploadDoc;
	protected Textbox documentName;
	protected Groupbox mandateDocGroupBox;
	protected Iframe mandatedoc;
	private byte[] imagebyte;
	protected Textbox approvalID;
	protected Label regStatus;

	protected Button btnHelp;
	protected Button btnProcess;
	protected Button btnView;

	protected North north_mandate;
	protected Groupbox finBasicdetails;
	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private FinanceMainBaseCtrl financeMainDialogCtrl;

	Tab parenttab = null;
	// long mandateID = 0;
	private transient MandateRegistrationListCtrl mandateRegistrationListCtrl;
	private transient MandateListCtrl mandateListCtrl;

	private CustomerDetailsService customerDetailsService;
	private transient MandateService mandateService;
	private transient BankDetailService bankDetailService;
	private ExternalDocumentManager externalDocumentManager = null;

	private transient BankAccountValidationService bankAccountValidationService;
	private transient MandateDAO mandateDAO;
	private transient PennyDropService pennyDropService;
	private transient PennyDropDAO pennyDropDAO;
	private transient FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private transient BankAccountValidation bankAccountValidations;

	String finType = null;

	private Mandate mandate;

	private boolean flag = false;
	private boolean enqModule = false;
	private boolean fromLoan = false;
	private boolean registration = false;
	private boolean maintain = false;
	private int ccyFormatter = 0;
	protected int maxAccNoLength;
	protected int minAccNoLength;
	List<FinanceMain> validFinreferences = new ArrayList<FinanceMain>();

	private final List<ValueLabel> mandateTypeList = MandateUtil.getInstrumentTypes();
	private final List<ValueLabel> accTypeList = PennantStaticListUtil.getAccTypeList();
	private transient final String btnCtroller_ClassPrefix = "button_MandateDialog_";

	private boolean notes_Entered = false;

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
			this.mandate.setMandateType(fm.getFinRepayMethod());

			FinanceType ft = fd.getFinScheduleData().getFinanceType();
			this.mandate.setEntityCode(ft.getLovDescEntityCode());
			this.mandate.setEntityDesc(ft.getLovDescEntityDesc());
			this.mandate.setOrgReference(fm.getFinReference());
		}

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
	}

	private void onCreateFromMandate() {
		setMandateRegistrationListCtrl((MandateRegistrationListCtrl) arguments.get("mandateRegistrationListCtrl"));

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
			setMandate(this.mandate);
		} else {
			setMandate(null);
		}

		if (arguments.containsKey("mandateListCtrl")) {
			setMandateListCtrl((MandateListCtrl) arguments.get("mandateListCtrl"));
		}

		if (arguments.containsKey("validReferences")) {
			validFinreferences = (List<FinanceMain>) arguments.get("validReferences");
		}

		this.useExisting.setDisabled(!fromLoan);

		if (fromLoan) {
			onCreateFromLoanOrgination();
		} else {
			onCreateFromMandate();
		}

		this.custID.setValue(this.mandate.getCustCIF());
		this.entityCode.setDescColumn(this.mandate.getEntityDesc());
		this.entityCode.setValue(this.mandate.getEntityCode());

		try {

			doLoadWorkFlow(this.mandate.isWorkflow(), this.mandate.getWorkflowId(), this.mandate.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
			}

			getUserWorkspace().allocateAuthorities(super.pageRightName, getRole());

			doCheckRights();

			ccyFormatter = CurrencyUtil.getFormat(this.mandate.getMandateCcy());

			doSetFieldProperties();

			doShowDialog(getMandate());

		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.finReference.setList(validFinreferences);

		this.finReference.setMaxlength(20);
		this.finReference.setTextBoxWidth(130);
		this.finReference.setModuleName("FinanceManagement");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setValidateColumns(new String[] { "FinReference" });

		if (!fromLoan) {
			getFinReferences();
		}

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

		this.periodicity.setMandatoryStyle(true);
		this.phoneNumber.setMaxlength(10);
		this.phoneNumber.setWidth("200px");
		this.approvalID.setMaxlength(50);

		this.bankBranchID.setModuleName("BankBranch");
		this.bankBranchID.setMandatoryStyle(true);
		this.bankBranchID.setValueColumn("BankBranchID");
		this.bankBranchID.setDescColumn("BranchDesc");
		this.bankBranchID.setDisplayStyle(2);

		if (App.DATABASE == Database.POSTGRES) {
			this.bankBranchID.setValueType(DataType.LONG);
		}

		this.bankBranchID.setValidateColumns(new String[] { "BankBranchID" });

		this.mandateRef.setModuleName("Mandate");
		this.mandateRef.setMandatoryStyle(true);
		this.mandateRef.setValueColumn("MandateID");
		this.mandateRef.setDescColumn("MandateRef");
		this.mandateRef.setDisplayStyle(2);
		this.mandateRef.setInputAllowed(false);
		this.mandateRef.setValueType(DataType.LONG);
		this.mandateRef.setValidateColumns(new String[] { "MandateID" });

		// addMandateFiletrs(null);

		this.active.setChecked(true);
		this.reason.setMaxlength(60);
		this.umrNumber.setReadonly(true);
		this.documentName.setMaxlength(150);

		this.btnFetchAccountDetails.addEventListener(Events.ON_CLICK, event -> fetchAccounts());
		this.btnFetchAccountDetails.setDisabled(true);

		if (StringUtils.isNotBlank(this.mandate.getBankCode())) {
			BankDetail bankDetail = getBankDetailService().getAccNoLengthByCode(this.mandate.getBankCode());
			maxAccNoLength = bankDetail.getAccNoLength();
			minAccNoLength = bankDetail.getMinAccNoLength();
		}

		this.barCodeNumber.setMaxlength(10);

		this.entityCode.setMaxlength(8);
		this.entityCode.setDisplayStyle(2);
		this.entityCode.setTextBoxWidth(150);

		this.partnerBank.setVisible(true);
		this.partnerBank.setMaxlength(8);
		this.partnerBank.setDisplayStyle(2);
		this.partnerBank.setMandatoryStyle(true);
		this.partnerBank.setWidth("200px");
		this.partnerBank.setModuleName("FinTypePartnerBank_Mandates");
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
		this.holdReasons.setModuleName("BounceReason");
		this.holdReasons.setDisplayStyle(2);
		this.holdReasons.setValueColumn("BounceID");
		this.holdReasons.setTextBoxWidth(200);
		this.holdReasons.setValueType(DataType.LONG);
		this.holdReasons.setDescColumn("Reason");
		this.holdReasons.setValidateColumns(new String[] { "BounceID", "BounceCode", "Lovdesccategory", "Reason" });

		this.employeeID.setInputAllowed(true);
		this.employeeID.setMandatoryStyle(true);
		this.employeeID.setModuleName("EmployerDetails");
		this.employeeID.setTextBoxWidth(200);
		this.employeeID.setValueColumn("EmployerId");
		this.employeeID.setValueType(DataType.LONG);
		this.employeeID.setFilters(new Filter[] { new Filter("AllowDAS", 1, Filter.OP_EQUAL) });
		this.employeeID.setValidateColumns(new String[] { "EmployerId" });
		this.employerName.setValue("EmpName");

		this.barCodeNumber.setVisible(true);

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
		MessageUtil.showHelpWindow(event, window_MandateDialog);
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

		List<WrongValueException> wve = new ArrayList<>();

		doSetValidation(true);

		BankAccountValidation bav = new BankAccountValidation();

		if (this.mandate.getOrgReference() != null) {
			bav.setInitiateReference(this.mandate.getOrgReference());
		}

		bav.setInitiateReference(String.valueOf(this.custID.getValue()));
		bav.setUserDetails(getUserWorkspace().getLoggedInUser());

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

		int count = getPennyDropService().getPennyDropCount(bav.getAcctNum(), bav.getiFSC());
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

	public void onCheck$openMandate(Event event) {
		checkOpenMandate();
	}

	public void onCheck$swapIsActive(Event event) {
		logger.debug(Literal.ENTERING);

		if (this.swapMandate.isChecked()) {
			this.swapEffectiveDate.setReadonly(false);
			this.swapEffectiveDate.setDisabled(false);
		} else {
			this.swapEffectiveDate.setReadonly(true);
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
		Object dataObject = null;
		long custID = this.mandate.getCustID();
		Filter filter[] = new Filter[2];
		filter[0] = new Filter("CustID", custID, Filter.OP_EQUAL);
		filter[1] = new Filter("RepaymentFrom", "Y", Filter.OP_EQUAL);

		dataObject = ExtendedSearchListBox.show(this.window_MandateDialog, "CustomerBankInfoAccntNumbers", filter, "");

		if (dataObject == null || !(dataObject instanceof CustomerBankInfo)) {
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
			readOnlyComponent(true, this.approvalID);
			readOnlyComponent(true, this.btnUploadDoc);
			readOnlyComponent(true, this.barCodeNumber);
			readOnlyComponent(true, swapMandate);
			readOnlyComponent(true, this.pennyDropResult);
			readOnlyComponent(true, this.txnDetails);
			readOnlyComponent(true, this.defaultMandate);
			readOnlyComponent(true, this.umrNumber);
			readOnlyComponent(true, this.txnDetails);
			readOnlyComponent(true, this.partnerBank);

		} else {
			readOnlyComponent(true, this.mandateRef);
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
			readOnlyComponent(true, this.approvalID);
			this.maxLimit.setMandatory(true);
			readOnlyComponent(isReadOnly("MandateDialog_BarCodeNumber"), this.barCodeNumber);
			readOnlyComponent(isReadOnly("MandateDialog_SwapIsActive"), swapMandate);
			readOnlyComponent(isReadOnly("MasterDialog_PennyDropResult"), pennyDropResult);
			readOnlyComponent(isReadOnly("MasterDialog_TxnDetails"), txnDetails);
			readOnlyComponent(isReadOnly("MandateDialog_DefaultMandate"), defaultMandate);
			readOnlyComponent(isReadOnly("MandateDialog_PartnerBankId"), this.partnerBank);
			readOnlyComponent(isReadOnly("MandateDialog_umrNumber"), this.umrNumber);

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
		this.approvalID.setValue("");
		this.documentName.setValue("");
		this.mandatedoc.setContent(null);
		this.barCodeNumber.setValue("");
		this.finReference.setValue("");
		this.regStatus.setValue("");
		this.amountInWords.setValue("");
		this.swapMandate.setChecked(false);
		this.pennyDropResult.setValue("");
		this.txnDetails.setValue("");
		this.defaultMandate.setChecked(false);
		this.partnerBank.setValue("");
		this.eMandateSource.setValue("");
		this.eMandateReferenceNo.setValue("");
	}

	public void onChange$mandateType(Event event) {
		String str = this.mandateType.getSelectedItem().getValue().toString();
		this.bankBranchID.setValue("");
		this.bankBranchID.setDescription("");
		this.bank.setValue("");
		this.micr.setValue("");
		this.ifsc.setValue("");
		this.city.setValue("");
		this.cityName.setValue("");
		this.accNumber.setValue("");

		onChangeMandateType(str);

	}

	private void onChangeMandateType(String str) {
		InstrumentType instrumentType = InstrumentType.valueOf(str);

		if (instrumentType == null) {
			return;
		}

		doEditFieldByInstrument(instrumentType);
	}

	private boolean isApproved() {
		return StringUtils.isEmpty(this.mandate.getRecordType()) && !this.mandate.isNewRecord();
	}

	private void doEditFieldByInstrument(InstrumentType instrumentType) {
		if (instrumentType == InstrumentType.SI || instrumentType == InstrumentType.DAS) {
			doSetReadOnly();
		}

		if (instrumentType == InstrumentType.EMANDATE) {
			this.emandateRow.setVisible(true);
			readOnlyComponent(false, this.eMandateReferenceNo);
			readOnlyComponent(false, this.eMandateSource);
		}

		if (instrumentType == InstrumentType.DAS) {
			this.dasRow.setVisible(true);
			readOnlyComponent(isReadOnly("MandateDialog_EmployeeID"), this.employeeID);
			readOnlyComponent(isReadOnly("MandateDialog_EmployerName"), this.employerName);
		}

		if (instrumentType == InstrumentType.SI) {
			readOnlyComponent(false, this.finReference);
			readOnlyComponent(isReadOnly("MandateDialog_BankBranchID"), this.bankBranchID);
			readOnlyComponent(isReadOnly("MandateDialog_AccNumber"), this.accNumber);
		}

		switch (instrumentType) {
		case ECS:
		case DD:
		case NACH:
		case EMANDATE:
			readOnlyComponent(false, this.finReference);
			this.bankBranchID.setFilters(new Filter[] { new Filter(instrumentType.name(), 1, Filter.OP_EQUAL) });
			break;
		default:
			break;
		}

		if (isApproved()) {
			readOnlyComponent(true, this.finReference);
		} else {
			readOnlyComponent(isReadOnly("MandateDialog_EmployeeID"), this.finReference);
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
				bankAccountValidations = getPennyDropService().getPennyDropStatusDataByAcc(acctNumber, ifscCode);
			}

			doWriteBeanToComponents(aMandate);

			doDesignByStatus(aMandate);

			doDesignByMode();

			if (fromLoan) {
				financeMainDialogCtrl.setMandateDialogCtrl(this);

				if (parenttab != null) {
					checkTabDisplay(aMandate.getMandateType(), false);
				}

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
			this.mandateStatus.setVisible(false);
			this.btnReason.setVisible(false);
			this.reason.setVisible(false);
		} else {
			this.mandateStatus.setVisible(true);
			this.btnReason.setVisible(true);
			this.reason.setVisible(true);
		}

		if (MandateStatus.isRejected(status)) {
			readOnlyComponent(true, mandateStatus);
			readOnlyComponent(true, reason);
		}

		if (MandateStatus.isNew(status) || MandateStatus.isInprocess(status)) {
			readOnlyComponent(true, this.mandateStatus);
			readOnlyComponent(true, reason);

			this.reason.setValue("");
			// this.mandateStatus.setVisible(false);
			this.btnReason.setVisible(false);

			this.reason.setVisible(false);
		}

		if (MandateStatus.isApproved(status) || MandateStatus.isHold(status) || MandateStatus.isRelease(status)) {
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
			readOnlyComponent(true, this.approvalID);
			readOnlyComponent(true, this.barCodeNumber);
			readOnlyComponent(true, this.swapMandate);
			readOnlyComponent(true, this.swapEffectiveDate);
			readOnlyComponent(true, this.pennyDropResult);
			readOnlyComponent(true, this.txnDetails);
			readOnlyComponent(true, this.defaultMandate);
			readOnlyComponent(true, this.umrNumber);
			readOnlyComponent(true, this.eMandateReferenceNo);
			readOnlyComponent(true, this.eMandateSource);
			readOnlyComponent(true, this.hold);
			readOnlyComponent(true, this.holdReasons);
			readOnlyComponent(true, this.partnerBank);
		}

		if (MandateStatus.isApproved(status) || MandateStatus.isRelease(status)) {
			readOnlyComponent(false, this.hold);
			readOnlyComponent(false, this.holdReasons);
		}

	}

	private void doDesignByMode() {
		if (enqModule) {
			this.btnCancel.setVisible(false);
			this.btnSave.setVisible(false);
			this.btnDelete.setVisible(false);
			this.btnNotes.setVisible(false);
			return;
		}

		if (registration) {
			this.mandateStatus.setVisible(true);
			this.btnReason.setVisible(true);
			this.reason.setVisible(true);
			readOnlyComponent(false, this.mandateStatus);
			readOnlyComponent(false, this.reason);
			this.btnCancel.setVisible(false);
			this.btnEdit.setVisible(false);
			this.btnSave.setVisible(false);
			this.btnNotes.setVisible(false);
			this.btnDelete.setVisible(false);
			this.btnProcess.setVisible(true);
		}

		if (fromLoan) {
			this.north_mandate.setVisible(false);
			readOnlyComponent(!ImplementationConstants.MANDATE_ALLOW_CO_APP, this.custID);
			readOnlyComponent(true, this.mandateType);

			this.mandateStatus.setVisible(false);
			this.btnReason.setVisible(false);

			this.reason.setVisible(false);

			readOnlyComponent(true, this.finReference);
		}

		if (StringUtils.isNotEmpty(getMandate().getOrgReference())
				&& !MandateStatus.isFinance(getMandate().getStatus())) {
			readOnlyComponent(true, this.openMandate);
		}

		if (StringUtils.isEmpty(getMandate().getRecordType())
				&& StringUtils.isNotEmpty(getMandate().getRecordStatus())) {
			readOnlyComponent(true, this.mandateType);
		}

		if (this.hold.isChecked()) {
			this.holdReasons.setReadonly(false);
		}

	}

	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (getMandate().isNewRecord()) {
			this.btnCancel.setVisible(false);
			this.custID.setReadonly(false);
		} else {
			this.btnCancel.setVisible(true);
			this.custID.setReadonly(true);
		}

		readOnlyComponent(true, this.finReference);
		readOnlyComponent(true, this.mandateType);
		readOnlyComponent(true, this.custID);

		if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_MANDATE_ACCT_DET_READONLY)) {
			readOnlyComponent(true, accNumber);
			readOnlyComponent(true, bankBranchID);
		} else {
			readOnlyComponent(isReadOnly("MandateDialog_AccNumber"), this.accNumber);
			readOnlyComponent(isReadOnly("MandateDialog_BankBranchID"), this.bankBranchID);
		}

		readOnlyComponent(isReadOnly("MandateDialog_MandateRef"), this.useExisting);
		readOnlyComponent(isReadOnly("MandateDialog_MandateRef"), this.mandateRef);
		readOnlyComponent(isReadOnly("MandateDialog_AccHolderName"), this.accHolderName);
		readOnlyComponent(isReadOnly("MandateDialog_JointAccHolderName"), this.jointAccHolderName);
		readOnlyComponent(isReadOnly("MandateDialog_AccType"), this.accType);
		readOnlyComponent(isReadOnly("MandateDialog_OpenMandate"), this.openMandate);
		readOnlyComponent(isReadOnly("MandateDialog_StartDate"), this.startDate);
		readOnlyComponent(isReadOnly("MandateDialog_ExpiryDate"), this.expiryDate);
		readOnlyComponent(isReadOnly("MandateDialog_MaxLimit"), this.maxLimit);
		readOnlyComponent(isReadOnly("MandateDialog_Periodicity"), this.periodicity);
		readOnlyComponent(isReadOnly("MandateDialog_PhoneNumber"), this.phoneNumber);
		readOnlyComponent(isReadOnly("MandateDialog_ApprovalID"), this.approvalID);
		readOnlyComponent(isReadOnly("MandateDialog_Status"), this.mandateStatus);
		readOnlyComponent(isReadOnly("MandateDialog_Status"), this.reason);
		readOnlyComponent(isReadOnly("MandateDialog_InputDate"), this.inputDate);
		readOnlyComponent(true, this.umrNumber);
		readOnlyComponent(isReadOnly("MandateDialog_BarCodeNumber"), this.barCodeNumber);
		readOnlyComponent(isReadOnly("MandateDialog_SwapIsActive"), this.swapMandate);
		readOnlyComponent(isReadOnly("MandateDialog_PennyDropResult"), this.pennyDropResult);
		readOnlyComponent(isReadOnly("MandateDialog_TxnDetails"), this.txnDetails);
		readOnlyComponent(isReadOnly("MandateDialog_DefaultMandate"), this.defaultMandate);
		readOnlyComponent(isReadOnly("MandateDialog_Active"), this.active);
		readOnlyComponent(isReadOnly("MandateDialog_SwapEffectiveDate"), this.swapEffectiveDate);
		readOnlyComponent(isReadOnly("MandateDialog_Hold"), this.hold);
		readOnlyComponent(isReadOnly("MandateDialog_HoldReasons"), this.holdReasons);
		readOnlyComponent(isReadOnly("MandateDialog_SecurityMandate"), this.securityMandate);
		readOnlyComponent(isReadOnly("MandateDialog_EmployeeID"), this.employeeID);
		readOnlyComponent(isReadOnly("MandateDialog_EmployerName"), this.employerName);

		if (SysParamUtil.isAllowed(SMTParameterConstants.MANDATE_ALW_PARTNER_BANK)) {
			readOnlyComponent(isReadOnly("MandateDialog_PartnerBankId"), this.partnerBank);
		} else {
			readOnlyComponent(true, partnerBank);
		}

		readOnlyComponent(isReadOnly("MandateDialog_eMandateSource"), this.eMandateSource);
		readOnlyComponent(isReadOnly("MandateDialog_eMandateReferenceNo"), this.eMandateReferenceNo);

		boolean isVisible = true;

		this.btnPennyDropResult.setVisible(isVisible);
		this.pennyDropResult.setVisible(isVisible);

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

			if (StringUtils.equals(getMandate().getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED)) {
				this.btnNotes.setVisible(true);
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		if (bankAccountValidationService != null && !enqiryModule) {
			btnPennyDropResult.setVisible(true);
		} else {
			btnPennyDropResult.setVisible(false);
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
		readOnlyComponent(true, this.btnPennyDropResult);
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
		readOnlyComponent(true, this.reason);
		readOnlyComponent(true, this.barCodeNumber);

		readOnlyComponent(true, this.externalMandate);
		readOnlyComponent(true, this.umrNumber);

		readOnlyComponent(true, this.securityMandate);
		readOnlyComponent(true, this.pennyDropResult);
		readOnlyComponent(true, this.approvalID);
		readOnlyComponent(true, this.txnDetails);
		readOnlyComponent(true, this.documentName);
		readOnlyComponent(true, this.btnUploadDoc);
		readOnlyComponent(true, this.hold);
		readOnlyComponent(true, this.holdReasons);
		readOnlyComponent(true, this.swapMandate);
		readOnlyComponent(true, this.swapEffectiveDate);
		readOnlyComponent(true, this.employeeID);
		readOnlyComponent(true, this.employerName);
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

		fillComboBox(this.mandateType, aMandate.getMandateType(), mandateTypeList, "");

		onChangeMandateType(StringUtils.trimToEmpty(aMandate.getMandateType()));

		if (fromLoan) {
			this.useExisting.setChecked(aMandate.isUseExisting());
			useExisting();
		} else {
			readOnlyComponent(true, this.mandateRef);
		}

		this.custID.setValue(aMandate.getCustCIF());
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
		}

		fillComboBox(this.mandateStatus, aMandate.getStatus(), MandateUtil.getMandateStatus(), excludeList);

		this.reason.setValue(aMandate.getReason());

		doWriteData(aMandate);

		this.approvalID.setValue(aMandate.getApprovalID());
		this.recordStatus.setValue(aMandate.getRecordStatus());

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
			this.hold.setChecked(false);
		} else {
			this.inputDate.setValue(aMandate.getInputDate());
			if (maintain) {
				this.hold.setChecked(aMandate.isHold());
				this.holdReasons.setValue(String.valueOf(aMandate.getHoldReasons()));
			}

			this.swapMandate.setChecked(aMandate.isSwapIsActive());
			this.swapEffectiveDate.setValue(aMandate.getSwapEffectiveDate());
		}

		if (aMandate.getEmployeeID() != null) {
			this.employeeID.setValue(String.valueOf(aMandate.getEmployeeID()));
			this.employerName.setValue(aMandate.getEmployerName());
		} else {
			this.employeeID.setValue("");
			this.employerName.setValue("");
		}

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
			if (StringUtils.isEmpty(aMandate.getPeriodicity())) {
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
		this.reason.setValue(aMandate.getReason());
		this.umrNumber.setValue(aMandate.getMandateRef());
		this.documentName.setValue(aMandate.getDocumentName());
		this.defaultMandate.setChecked(aMandate.isDefaultMandate());
		this.eMandateSource.setValue(aMandate.geteMandateSource());
		this.eMandateReferenceNo.setValue(aMandate.geteMandateReferenceNo());

		setMandateDocument(aMandate);

		this.barCodeNumber.setValue(aMandate.getBarCodeNumber());
		this.finReference.setValue(aMandate.getOrgReference());

		Filter[] flt = new Filter[4];
		if (aMandate.getFinType() != null) {
			finType = aMandate.getFinType();

		}

		flt[0] = new Filter("Active", 1, Filter.OP_EQUAL);
		flt[1] = new Filter("Purpose", "R", Filter.OP_EQUAL);
		flt[2] = new Filter("FinType", finType, Filter.OP_EQUAL);
		flt[3] = new Filter("PaymentMode", DisbursementConstants.PAYMENT_TYPE_NEFT, Filter.OP_EQUAL);

		this.partnerBank.setFilters(flt);

		if (!StringUtils.equals(aMandate.getStatus(), PennantConstants.List_Select)) {
			this.regStatus.setValue(PennantAppUtil.getlabelDesc(aMandate.getStatus(), MandateUtil.getMandateStatus()));
		}

		this.amountInWords.setValue(AmtInitialCap());
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
				bankAccountValidations = getPennyDropService().getPennyDropStatusDataByAcc(aMandate.getAccNumber(),
						aMandate.getiFSC());
				if (bankAccountValidations != null) {
					this.pennyDropResult.setValue(bankAccountValidations.isStatus() ? "Success" : "Fail");
				}
			}
		} else {
			this.pennyDropResult.setValue("");
		}

		if (aMandate.getPartnerBankId() != 0 && aMandate.getPartnerBankId() != Long.MIN_VALUE) {
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
			if (this.periodicity.isValidComboValue()) {
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
			aMandate.setStatus(this.mandateStatus.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMandate.setInputDate(
					DateUtility.getDate(DateUtil.format(this.inputDate.getValue(), PennantConstants.dateFormat)));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMandate.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMandate.setReason(this.reason.getValue());
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
			aMandate.setBarCodeNumber(this.barCodeNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

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
			if (this.hold.isChecked() && !this.holdReasons.isReadonly()) {
				aMandate.setHoldReasons(Long.valueOf(this.holdReasons.getValue()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			String empId = StringUtils.trimToNull(employeeID.getValue());
			aMandate.setEmployeeID(empId == null ? null : Long.valueOf(empId));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMandate.setEmployerName(this.employerName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMandate.setHold(this.hold.isChecked());
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

	private void doSetValidation(boolean validate) {
		logger.debug(Literal.ENTERING);

		if (this.useExisting.isChecked()) {
			this.mandateType.setConstraint(
					new StaticListValidator(mandateTypeList, Labels.getLabel("label_MandateDialog_MandateType.value")));
		} else {
			if (!this.mandateType.isDisabled()) {
				this.mandateType.setConstraint(new StaticListValidator(mandateTypeList,
						Labels.getLabel("label_MandateDialog_MandateType.value")));
			}
		}

		if (!this.bankBranchID.isReadonly()) {
			this.bankBranchID.setConstraint(
					new PTStringValidator(Labels.getLabel("label_MandateDialog_BankBranchID.value"), null, validate));
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

		Date mandbackDate = DateUtil.addDays(SysParamUtil.getAppDate(),
				-SysParamUtil.getValueAsInt("MANDATE_STARTDATE"));
		Date appExpiryDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");

		if (!this.startDate.isDisabled() && this.startDate.isButtonVisible()) {
			this.startDate.setConstraint(new PTDateValidator(Labels.getLabel("label_MandateDialog_StartDate.value"),
					validate, mandbackDate, appExpiryDate, true));
		}

		if (!this.expiryDate.isDisabled() && this.expiryDate.isButtonVisible()) {
			try {
				this.expiryDate
						.setConstraint(new PTDateValidator(Labels.getLabel("label_MandateDialog_ExpiryDate.value"),
								validate, this.startDate.getValue(), appExpiryDate, this.openMandate.isChecked()));
			} catch (WrongValueException we) {
				this.expiryDate.setConstraint(new PTDateValidator(
						Labels.getLabel("label_MandateDialog_ExpiryDate.value"), validate, true, null, false));
			}
		}

		if (!this.maxLimit.isReadonly()) {
			this.maxLimit.setConstraint(new PTDecimalValidator(Labels.getLabel("label_MandateDialog_MaxLimit.value"),
					ccyFormatter, validate, false));
		}

		if (!this.phoneNumber.isReadonly()) {
			this.phoneNumber.setConstraint(
					new PTMobileNumberValidator(Labels.getLabel("label_MandateDialog_PhoneNumber.value"), false));
		}

		if (!this.reason.isReadonly()) {
			this.reason.setConstraint(new PTStringValidator(Labels.getLabel("label_MandateStatusDialog_Reason.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, flag));
		}

		if (!this.barCodeNumber.isReadonly()) {

			this.barCodeNumber
					.setConstraint(new PTStringValidator(Labels.getLabel("label_MandateDialog_BarCodeNumber.value"),
							PennantRegularExpressions.REGEX_BARCODE_NUMBER, false));
		}

		if (!this.finReference.isReadonly()) {
			this.finReference
					.setConstraint(new PTStringValidator(Labels.getLabel("label_MandateDialog_FinReference.value"),
							null, ImplementationConstants.CLIENT_NFL ? false : validate));
		}

		if (!this.partnerBank.isReadonly()) {
			this.partnerBank.setConstraint(
					new PTStringValidator(Labels.getLabel("label_MandateDialog_PartnerBank.value"), null, true, false));
		}

		if (!this.eMandateSource.isReadonly()) {
			this.eMandateSource.setConstraint(new PTStringValidator(
					Labels.getLabel("label_MandateDialog_E-Mandate_Source.value"), null, validate));
		}

		if (!this.eMandateReferenceNo.isReadonly()) {
			this.eMandateReferenceNo.setConstraint(new PTStringValidator(
					Labels.getLabel("label_MandateDialog_E-Mandate_Reference_No.value"), null, validate));

		}

		if (!this.employeeID.isReadonly()) {
			this.employeeID.setConstraint(
					new PTStringValidator(Labels.getLabel("label_MandateDialog_EmployeeID.value"), null, true, false));

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
		this.approvalID.setConstraint("");
		this.reason.setConstraint("");
		this.barCodeNumber.setConstraint("");
		this.finReference.setConstraint("");
		this.eMandateReferenceNo.setConstraint("");
		this.eMandateSource.setConstraint("");
		this.partnerBank.setConstraint("");
		this.holdReasons.setConstraint("");
		this.employeeID.setConstraint("");
		this.employerName.setConstraint("");
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
		this.approvalID.setErrorMessage("");
		this.reason.setErrorMessage("");
		this.documentName.setErrorMessage("");
		this.barCodeNumber.setErrorMessage("");
		this.finReference.setErrorMessage("");
		this.partnerBank.setErrorMessage("");

		this.holdReasons.setErrorMessage("");
		this.employeeID.setErrorMessage("");
		this.employerName.setErrorMessage("");
	}

	protected void refreshList() {
		if (registration) {
			getMandateRegistrationListCtrl().search();
		} else {
			getMandateListCtrl().search();
		}
	}

	public void onSelect$status(Event event) {
		this.space_Reason.setSclass("mandatory");
		flag = true;
	}

	public void onFulfill$bankBranchID(Event event) {
		Object dataObject = this.bankBranchID.getObject();

		if (dataObject == null || dataObject instanceof String) {
			this.bank.setValue("");
			this.city.setValue("");
			this.micr.setValue("");
			this.ifsc.setValue("");
			this.cityName.setValue("");
		} else {
			BankBranch details = (BankBranch) dataObject;
			if (details != null) {
				this.bankBranchID.setAttribute("bankBranchID", details.getBankBranchID());
				this.bank.setValue(details.getBankName());
				this.micr.setValue(details.getMICR());
				this.ifsc.setValue(details.getIFSC());
				this.city.setValue(details.getCity());
				this.cityName.setValue(details.getPCCityName());
				if (StringUtils.isNotBlank(details.getBankCode())) {
					BankDetail bankDetail = getBankDetailService().getAccNoLengthByCode(details.getBankCode());
					maxAccNoLength = bankDetail.getAccNoLength();
					minAccNoLength = bankDetail.getMinAccNoLength();
				}
				this.accNumber.setMaxlength(maxAccNoLength);

			}
		}
	}

	public void onFulfill$mandateRef(Event event) {
		logger.debug(Literal.ENTERING);

		Object dataObject = mandateRef.getObject();

		if (dataObject instanceof String) {
			this.mandateRef.setValue(dataObject.toString());
			this.mandateRef.setAttribute("mandateID", Long.MIN_VALUE);
			clearMandatedata();
		} else {
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
		}

		logger.debug(Literal.LEAVING);
	}

	private void addPartenetBankFilter() {
		Filter[] filter = new Filter[4];
		filter[0] = new Filter("Active", 1, Filter.OP_EQUAL);
		filter[1] = new Filter("Purpose", "R", Filter.OP_EQUAL);
		filter[2] = new Filter("FinType", finType, Filter.OP_EQUAL);
		filter[3] = new Filter("PaymentMode", DisbursementConstants.PAYMENT_TYPE_NEFT, Filter.OP_EQUAL);

		this.partnerBank.setFilters(filter);
	}

	public void onFulfill$finReference(Event event) {
		logger.debug(Literal.ENTERING);

		Object dataObject = finReference.getObject();

		FinanceMain fm = (FinanceMain) dataObject;

		this.finReference.setValue(fm.getFinReference());

		this.finType = fm.getFinType();

		addPartenetBankFilter();

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$employeeID(Event event) {
		Object dataObject = employeeID.getObject();

		if (dataObject instanceof String) {
			this.employeeID.setValue(dataObject.toString());
		} else {
			EmployerDetail details = (EmployerDetail) dataObject;
			if (details != null) {
				this.employeeID.setValue(String.valueOf(details.getEmployerId()));
				this.employerName.setValue(details.getEmpName());

			} else {
				this.employeeID.setValue("");
				this.employerName.setValue("");
			}
		}
	}

	private String AmtInitialCap() {
		String amtInWords = NumberToEnglishWords.getNumberToWords(this.maxLimit.getActualValue().toBigInteger());

		String[] words = amtInWords.split(" ");
		StringBuffer AmtInWord = new StringBuffer();

		for (int i = 0; i < words.length; i++) {
			if (!words[i].isEmpty()) {
				AmtInWord.append(Character.toUpperCase(words[i].charAt(0))).append(words[i].substring(1)).append(" ");

			}

		}
		return AmtInWord.toString().trim();
	}

	private void doDelete() {
		logger.debug(Literal.ENTERING);
		final Mandate aMandate = new Mandate();
		BeanUtils.copyProperties(getMandate(), aMandate);

		// in delete case if approver approves needs notes
		if (this.btnNotes.isVisible() && !notesEntered) {
			MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
			return;
		}

		doDelete(String.valueOf(aMandate.getMandateID()), aMandate);

		logger.debug(Literal.LEAVING);
	}

	public void doClear() {
		this.custID.setValue("");
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
		this.approvalID.setValue("");
		this.bank.setValue("");
		this.micr.setValue("");
		this.ifsc.setValue("");
		this.city.setValue("");
		this.reason.setValue("");
		this.documentName.setValue("");
		this.defaultMandate.setChecked(false);
		this.partnerBank.setValue("");

		this.eMandateSource.setValue("");
		this.eMandateReferenceNo.setValue("");

		this.hold.setChecked(false);
		this.holdReasons.setValue("");
		this.employeeID.setValue("");
		this.employerName.setValue("");

	}

	public void doSave() {
		logger.debug(Literal.ENTERING);

		final Mandate aMandate = new Mandate();
		BeanUtils.copyProperties(getMandate(), aMandate);
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

		// save it to database
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
					retValue = ErrorControl.showErrorControl(this.window_MandateDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_MandateDialog, auditHeader);
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
		this.parenttab.setVisible(false);
		String val = StringUtils.trimToEmpty(mandateType);

		if (!InstrumentType.isPDC(val)) {
			for (ValueLabel valueLabel : mandateTypeList) {
				if (val.equals(valueLabel.getValue())) {
					this.parenttab.setVisible(true);
					fillComboBox(this.mandateType, mandateType, mandateTypeList, "");
					break;
				}
			}
		}

		// addMandateFiletrs(mandateType);

		if (this.useExisting.isChecked() && onchange) {
			clearMandatedata();
		}
	}

	private void addMandateFiletrs(String repaymethod) {
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

		if (!wve.isEmpty() && parenttab != null) {
			parenttab.setSelected(true);
		}

		showErrorDetails(wve);

		if (StringUtils.isBlank(getMandate().getRecordType())) {
			this.mandate.setVersion(getMandate().getVersion() + 1);
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

	public void onCheck$hold(Event event) {
		logger.debug(Literal.ENTERING);
		if (this.hold.isChecked()) {
			this.holdReasons.setValue(this.holdReasons.getValue());
			this.holdReasons.setReadonly(false);
			this.holdReasons.setMandatoryStyle(true);
		} else {
			this.holdReasons.setReadonly(true);
			this.holdReasons.setValue("");
			this.holdReasons.setMandatoryStyle(false);
		}
		logger.debug(Literal.LEAVING);
	}

	private void getFinReferences() {
		this.finReference.setObject("");
		this.finReference.setValue("");
		String entity = this.entityCode.getValue();

		if (SysParamUtil.isAllowed(SMTParameterConstants.MANDATE_ALW_PARTNER_BANK)) {
			this.partnerBank.setObject("");
			this.partnerBank.setValue("");
		}

		StringBuilder sql = new StringBuilder();
		sql.append(" FinType in(Select FinType from RMTFinanceTypes where FinDivision IN ");

		sql.append(" (Select DivisionCode from SMTDivisionDetail where EntityCode = '" + entity + "'))");

		this.finReference.setMandatoryStyle(ImplementationConstants.CLIENT_NFL ? false : true);
		this.finReference.setWhereClause(sql.toString());

		Filter[] filter = new Filter[2];
		filter[0] = new Filter("FinIsActive", 1, Filter.OP_EQUAL);
		filter[1] = new Filter("CustID", this.mandate.getCustID(), Filter.OP_EQUAL);
		this.finReference.setFilters(filter);
	}

	public void doSetCustomerFilters() {
		List<String> custCIFs = new ArrayList<>(2);

		if (getMandate() != null) {
			custCIFs.add(getMandate().getCustCIF());
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

		Executions.createComponents(pageName, null, map);

		logger.debug(Literal.LEAVING);
	}

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

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

	public void setMandateRegistrationListCtrl(MandateRegistrationListCtrl mandateRegistrationListCtrl) {
		this.mandateRegistrationListCtrl = mandateRegistrationListCtrl;
	}

	public MandateRegistrationListCtrl getMandateRegistrationListCtrl() {
		return this.mandateRegistrationListCtrl;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public BankDetailService getBankDetailService() {
		return bankDetailService;
	}

	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}

	public void setExternalDocumentManager(ExternalDocumentManager externalDocumentManager) {
		this.externalDocumentManager = externalDocumentManager;
	}

	@Autowired(required = false)
	@Qualifier(value = "bankAccountValidationService")
	public void setBankAccountValidationService(BankAccountValidationService bankAccountValidationService) {
		this.bankAccountValidationService = bankAccountValidationService;
	}

	public MandateDAO getMandateDAO() {
		return mandateDAO;
	}

	public void setMandateDAO(MandateDAO mandateDAO) {
		this.mandateDAO = mandateDAO;
	}

	public PennyDropService getPennyDropService() {
		return pennyDropService;
	}

	public void setPennyDropService(PennyDropService pennyDropService) {
		this.pennyDropService = pennyDropService;
	}

	public PennyDropDAO getPennyDropDAO() {
		return pennyDropDAO;
	}

	public void setPennyDropDAO(PennyDropDAO pennyDropDAO) {
		this.pennyDropDAO = pennyDropDAO;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

}
