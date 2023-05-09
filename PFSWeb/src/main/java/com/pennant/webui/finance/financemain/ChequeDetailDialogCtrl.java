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

package com.pennant.webui.finance.financemain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.pennydrop.BankAccountValidation;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.pdc.ChequeHeaderService;
import com.pennant.backend.service.pennydrop.PennyDropService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.mandate.AccountTypes;
import com.pennant.pff.mandate.ChequeSatus;
import com.pennant.pff.mandate.InstrumentType;
import com.pennant.pff.mandate.MandateUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.pdc.chequeheader.ChequeHeaderListCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.external.BankAccountValidationService;

/**
 * This is the controller class for the /WEB-INF/pages/pdc/ChequeDetail/chequeDetailDialog.zul file. <br>
 */
public class ChequeDetailDialogCtrl extends GFCBaseCtrl<ChequeHeader> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ChequeDetailDialogCtrl.class);

	protected Window windowChequeDetailDialog;
	protected Hbox hboxNorth;

	protected Groupbox finBasicdetails;
	protected Intbox totNoOfCheques;
	protected CurrencyBox totAmount;
	protected Checkbox includeCoAppCust;
	protected ExtendedCombobox customer;

	protected Combobox chequeType;
	protected ExtendedCombobox bankBranchID;
	protected Textbox city;
	protected Label cityName;

	protected ExtendedCombobox micr;
	protected Textbox ifsc;
	protected Combobox chequeStatus;

	protected Combobox accountType;
	protected Textbox accHolderName;
	protected Textbox accNumber;
	protected Button btnFetchAccountDetails;

	protected Intbox chequeSerialNo;
	protected CurrencyBox amount;
	protected Intbox noOfCheques;
	protected Button btnGen;

	protected Button btnPennyDropResult;
	protected Textbox pennyDropResult;

	protected Button deleteCheques;

	protected Listbox listBoxChequeDetail;
	protected Listheader listHeaderCheckBox;
	protected Checkbox listHeaderCheckBoxComp;

	protected Listbox listBoxSPDCChequeDetail;
	protected Listheader listSPDCHeaderCheckBox;
	protected Checkbox listSPDCHeaderCheckBoxComp;

	private Tabpanel tabPanel_dialogWindow;

	private Tab parenttab;

	private boolean fromLoan = false;
	private String ccy = SysParamUtil.getAppCurrency();
	private int ccyEditField = PennantConstants.defaultCCYDecPos;
	private boolean pdcReqFlag = false;

	private List<FinanceScheduleDetail> financeSchedules = new ArrayList<>();
	private List<ChequeDetail> chequeDocuments = new ArrayList<>();
	private List<ChequeDetail> chequeDetailList = new ArrayList<>();

	private FinanceDetail financeDetail;
	private BankDetail bankDetail;
	private ChequeHeader chequeHeader;
	private FinanceMainBaseCtrl financeMainDialogCtrl;

	private transient BankDetailService bankDetailService;
	private transient ChequeHeaderService chequeHeaderService;
	private transient PennyDropService pennyDropService;
	private transient BankAccountValidationService bankAccountValidationService;

	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private ChequeHeaderListCtrl chequeHeaderListCtrl;

	private final List<ValueLabel> chequeTypeList = MandateUtil.getChequeTypes();
	private final List<ValueLabel> chequeStatusList = ChequeSatus.getList();
	private final List<ValueLabel> accTypeList = AccountTypes.getList();

	private enum Field {
		CHECK_BOX(0),

		CHEQUE_TYPE(1),

		CHEQUE_SERIAL_NO(1),

		ACCOUNT_TYPE(2),

		ACC_HOLDER_NAME(3),

		ACCOUNT_NO(4),

		BANK_IFSC_CODE(5),

		MICR_CODE(6),

		DUE_DATE(7),

		INSTALLMENT_NO(8),

		AMOUNT(9),

		CHEQUE_STATUS(10),

		BTN_UPLOAD(11),

		BTN_VIEW(12);

		private final int index;

		private Field(int index) {
			this.index = index;
		}

		public int index() {
			return index;
		}
	}

	private static final String ROLE_CODE = "roleCode";
	private static final String FROM_LOAN = "fromLoan";
	private static final String FIN_HEADER = "finHeaderList";
	private static final String CUST_CIF = "CustCIF";
	private static final String WIDTH_100PX = "100px";
	private static final String COMBO_SELECT = "Combo.Select";
	private static final String BANK_BRANCH_ID = "bankBranchDetails";
	private static final String CHQ_SERIAL_EXISTS = Labels.getLabel("ChequeDetailDialog_ChkSerial_Exists");
	private static final String LABEL_CHQ_TYPE = Labels.getLabel("label_ChequeDetailDialog_ChequeType.value");

	public ChequeDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ChequeDetailDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.chequeHeader.getHeaderID());
	}

	@SuppressWarnings("unchecked")
	public void onCreate$windowChequeDetailDialog(Event event) {
		logger.debug(Literal.ENTERING.concat(event.getName()));

		setPageComponents(windowChequeDetailDialog);

		try {
			if (arguments.containsKey("chequeHeader")) {
				this.chequeHeader = (ChequeHeader) arguments.get("chequeHeader");
				setChequeHeader(chequeHeader);
				setChequeDetailList(chequeHeader.getChequeDetailList());
			}

			if (arguments.containsKey(FROM_LOAN)) {
				fromLoan = (Boolean) arguments.get(FROM_LOAN);
			}

			if (arguments.containsKey(ROLE_CODE)) {
				setRole((String) arguments.get(ROLE_CODE));
			}

			if (arguments.containsKey(FIN_HEADER)) {
				appendFinBasicDetails((ArrayList<Object>) arguments.get(FIN_HEADER));
			} else {
				setFinanceDetail(chequeHeaderService.getFinanceDetailById(chequeHeader.getFinID()));
				appendFinBasicDetails(getFinBasicDetails(financeDetail));
			}

			if (arguments.containsKey("parentTabPanel")) {
				tabPanel_dialogWindow = (Tabpanel) arguments.get("parentTabPanel");
			}

			if (fromLoan) {
				if (arguments.containsKey("financeDetail")) {
					setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
					if (financeDetail.getChequeHeader() != null) {
						setChequeHeader(financeDetail.getChequeHeader());
					}
				}
				if (arguments.containsKey("financeMainDialogCtrl")) {
					financeMainDialogCtrl = (FinanceMainBaseCtrl) arguments.get("financeMainDialogCtrl");
				}

				if (arguments.containsKey("tab")) {
					parenttab = (Tab) arguments.get("tab");
				}
				setFinanceSchedules(financeDetail.getFinScheduleData().getFinanceScheduleDetails());
				this.ccy = financeDetail.getFinScheduleData().getFinanceMain().getFinCcy();
				this.ccyEditField = CurrencyUtil.getFormat(ccy);
			} else {
				this.chequeHeaderListCtrl = (ChequeHeaderListCtrl) arguments.get("chequeHeaderListCtrl");
				setFinanceSchedules((List<FinanceScheduleDetail>) arguments.get("financeSchedules"));

				doLoadWorkFlow(this.chequeHeader.isWorkflow(), this.chequeHeader.getWorkflowId(),
						this.chequeHeader.getNextTaskId());

				if (isWorkFlowEnabled()) {
					if (!enqiryModule) {
						this.userAction = setListRecordStatus(this.userAction);
					}
					getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
				} else {
					getUserWorkspace().allocateAuthorities(this.pageRightName, null);
				}

				if (chequeHeader != null) {
					List<ChequeDetail> chequeDetails = chequeHeader.getChequeDetailList();
					if (chequeDetails != null && !chequeDetails.isEmpty()) {
						this.ccy = chequeDetails.get(0).getChequeCcy();
						this.ccyEditField = CurrencyUtil.getFormat(ccy);
					}
				}
			}

			if (arguments.containsKey(ROLE_CODE)) {
				setRole((String) arguments.get(ROLE_CODE));
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(chequeHeader);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING.concat(event.getName()));
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.bankBranchID.setModuleName("CheckBankBranch");
		this.bankBranchID.setValueColumn("BankBranchID");
		this.bankBranchID.setDescColumn("BankName");
		this.bankBranchID.setDisplayStyle(2);
		this.bankBranchID.setValueType(DataType.LONG);
		this.bankBranchID.setValidateColumns(new String[] { "BankBranchID" });
		this.bankBranchID.setTextBoxWidth(100);

		this.micr.setModuleName("CheckBankBranch");
		this.micr.setValueColumn("MICR");
		this.micr.setDisplayStyle(2);
		this.micr.setValidateColumns(new String[] { "MICR" });
		this.micr.setTextBoxWidth(100);

		this.micr.setFilters(new Filter[] { new Filter("MICR", "", Filter.OP_NOT_EQUAL) });

		this.chequeSerialNo.setMaxlength(6);
		this.chequeSerialNo.setFormat("000000");

		this.accNumber.setMaxlength(50);

		this.noOfCheques.setMaxlength(2);

		this.accHolderName.setMaxlength(200);

		this.totAmount.setProperties(false, ccyEditField);

		this.amount.setProperties(false, ccyEditField);
		this.amount.setTextBoxWidth(150);

		this.totNoOfCheques.setReadonly(true);
		this.totAmount.setReadonly(true);

		this.chequeStatus.setDisabled(true);
		this.btnFetchAccountDetails.addEventListener(Events.ON_CLICK, event -> fetchAccounts());

		if (bankAccountValidationService != null) {
			this.btnPennyDropResult.setVisible(!isReadOnly("button_MandateDialog_btnPennyDropResult"));
			this.pennyDropResult.setVisible(!isReadOnly("button_MandateDialog_btnPennyDropResult"));
		}

		Customer customer2 = financeDetail.getCustomerDetails().getCustomer();
		this.customer.setTextBoxWidth(121);
		this.customer.setModuleName("Customer");
		this.customer.setValueColumn(CUST_CIF);
		this.customer.setDescColumn("CustShrtName");
		this.customer.setValidateColumns(new String[] { CUST_CIF, "CustShrtName" });
		this.customer.setValue(customer2.getCustCIF());
		this.customer.setDescription(customer2.getCustShrtName());
		doSetCustomerFilters();

		this.customer.setReadonly(!this.includeCoAppCust.isChecked());

		this.micr.setReadonly(!getUserWorkspace().isAllowed("ChequeDetailDialog_MICR"));

		appendHeaderCheckbox();

		appendSPDCHeaderCheckbox();

		setStatusDetails();

		this.deleteCheques.addForward(Events.ON_CLICK, this.window, "onClickDeleteCheques");
		this.btnGen.addForward(Events.ON_CLICK, this.window, "onClickBtnGenerate");
		this.btnPennyDropResult.addForward(Events.ON_CLICK, this.window, "onClickBtnPennyDropResult");
		this.includeCoAppCust.addForward(Events.ON_CHECK, this.window, "onCheckIncludeCoAppCust");

		logger.debug(Literal.LEAVING);
	}

	private void appendHeaderCheckbox() {
		Listitem listitem = new Listitem();

		Listcell listcell = new Listcell();

		listHeaderCheckBoxComp = new Checkbox();

		listHeaderCheckBoxComp.setDisabled(!isDeleteVisible());
		listHeaderCheckBoxComp.addForward(Events.ON_CLICK, this.window, "onClickListHeaderCheckBox");

		listcell.appendChild(listHeaderCheckBoxComp);
		listitem.appendChild(listcell);

		if (listHeaderCheckBox.getChildren() != null) {
			listHeaderCheckBox.getChildren().clear();
		}

		listHeaderCheckBox.appendChild(listHeaderCheckBoxComp);
	}

	private void appendSPDCHeaderCheckbox() {
		Listitem listitem = new Listitem();

		Listcell listcell = new Listcell();

		listSPDCHeaderCheckBoxComp = new Checkbox();

		listSPDCHeaderCheckBoxComp.setDisabled(!isDeleteVisible());
		listSPDCHeaderCheckBoxComp.addForward(Events.ON_CLICK, this.window, "onClickListSPDCHeaderCheckBox");

		listcell.appendChild(listSPDCHeaderCheckBoxComp);
		listitem.appendChild(listcell);

		if (listSPDCHeaderCheckBox.getChildren() != null) {
			listSPDCHeaderCheckBox.getChildren().clear();
		}

		listSPDCHeaderCheckBox.appendChild(listSPDCHeaderCheckBoxComp);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());

		this.btnNew.setVisible(false);
		this.btnEdit.setVisible(false);
		this.btnDelete.setVisible(false);
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ChequeDetailDialog_btnSave"));
		this.btnGen.setDisabled(!getUserWorkspace().isAllowed("button_ChequeDetailDialog_btnGenerate"));
		boolean alwBtnFetchAcc = getUserWorkspace().isAllowed("button_ChequeDetailDialog_btnFetchAccountDetails");
		this.btnFetchAccountDetails.setDisabled(!alwBtnFetchAcc);
		boolean alwBtnPennyDrp = getUserWorkspace().isAllowed("button_ChequeDetailDialog_btnPennyDropResult");
		this.btnPennyDropResult.setVisible(alwBtnPennyDrp);
		this.btnCancel.setVisible(false);
		this.includeCoAppCust.setDisabled(!getUserWorkspace().isAllowed("button_ChequeDetailDialog_btnGenerate"));
		this.deleteCheques.setVisible(isDeleteVisible());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws ParseException
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.chequeHeader);
		logger.debug(Literal.LEAVING);
	}

	@Override
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		chequeHeaderListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.chequeHeader.getBefImage());
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void doSave() {
		logger.debug(Literal.ENTERING);

		final ChequeHeader ch = new ChequeHeader();
		BeanUtils.copyProperties(this.chequeHeader, ch);

		doSetValidation();
		doWriteComponentsToBean(ch, false);

		ch.setRecordStatus(this.recordStatus.getValue());

		String tranType = loadWrkFlow(ch);

		try {
			if (doProcess(ch, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (DataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() {
		logger.debug(Literal.ENTERING);

		final ChequeHeader aChequeHeader = new ChequeHeader();
		BeanUtils.copyProperties(this.chequeHeader, aChequeHeader);

		doDelete(String.valueOf(aChequeHeader.getHeaderID()), aChequeHeader);
		logger.debug(Literal.LEAVING);
	}

	@Override
	protected boolean doProcess(ChequeHeader ch, String tranType) {
		logger.debug(Literal.ENTERING);

		ch.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		ch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		ch.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			ch.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(ch.getNextTaskId());
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");

				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, ch);
				}

				if (!notesEntered && isNotesMandatory(taskId, ch)) {
					MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
					return false;
				}
			}

			return doServiceOperations(ch, tranType, taskId, nextTaskId);
		} else {
			return doSaveProcess(getAuditHeader(ch, tranType), null);
		}
	}

	private boolean doServiceOperations(ChequeHeader ch, String tranType, String taskId, String nextTaskId) {
		String nextRoleCode = "";

		if (StringUtils.isNotBlank(nextTaskId)) {
			nextRoleCode = getNextRoleCode(nextRoleCode, nextTaskId);
		}

		ch.setTaskId(taskId);
		ch.setNextTaskId(nextTaskId);
		ch.setRoleCode(getRole());
		ch.setNextRoleCode(nextRoleCode);

		String operationRefs = getServiceOperations(taskId, ch);

		if ("".equals(operationRefs)) {
			return doSaveProcess(getAuditHeader(ch, tranType), null);
		}

		String[] list = operationRefs.split(";");

		boolean processCompleted = false;
		for (int i = 0; i < list.length; i++) {
			processCompleted = doSaveProcess(getAuditHeader(ch, PennantConstants.TRAN_WF), list[i]);
			if (!processCompleted) {
				break;
			}
		}

		return processCompleted;
	}

	private String getNextRoleCode(String nextRoleCode, String nextTaskId) {
		String[] nextTasks = nextTaskId.split(";");

		if (nextTasks != null && nextTasks.length > 0) {
			for (int i = 0; i < nextTasks.length; i++) {
				nextRoleCode = getTaskOwner(nextTasks[i]);
			}
		} else {
			nextRoleCode = getTaskOwner(nextTaskId);
		}

		return nextRoleCode;
	}

	private boolean doSaveProcess(AuditHeader ah, String method) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;

		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (ah.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					chequeHeaderService.delete(ah);
					deleteNotes = true;
				} else {
					chequeHeaderService.saveOrUpdate(ah);
				}
			} else {
				deleteNotes = process(ah, method);
			}

			ErrorControl.showErrorDetails(this.windowChequeDetailDialog, ah);
			retValue = ah.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;
				if (deleteNotes) {
					deleteNotes(getNotes(this.chequeHeader), true);
				}
			}
			if (retValue == PennantConstants.porcessOVERIDE) {
				ah.setOveride(true);
				ah.setErrorMessage(null);
				ah.setInfoMessage(null);
				ah.setOverideMessage(null);
			}
		}

		setOverideMap(ah.getOverideMap());

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private boolean process(AuditHeader ah, String method) {
		boolean deleteNotes = false;

		ChequeHeader ch = (ChequeHeader) ah.getAuditDetail().getModelData();

		switch (StringUtils.trimToEmpty(method)) {
		case PennantConstants.method_doApprove:
			chequeHeaderService.doApprove(ah);
			if (ch.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
				deleteNotes = true;
			}
			break;
		case PennantConstants.method_doReject:
			chequeHeaderService.doReject(ah);
			if (ch.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				deleteNotes = true;
			}
			break;
		default:
			String label = Labels.getLabel("InvalidWorkFlowMethod");
			ah.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, label, null));
		}

		return deleteNotes;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param chequeDetail
	 * 
	 */
	public void doWriteBeanToComponents(ChequeHeader ch) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = financeDetail.getFinScheduleData();

		if (pdcReqFlag && fromLoan) {
			fillList(this.chequeType, InstrumentType.PDC.code(), chequeTypeList);
		} else {
			String finRepayMethod = schdData.getFinanceMain().getFinRepayMethod();
			fillList(this.chequeType, finRepayMethod, chequeTypeList);
		}

		fillComboBox(this.chequeStatus, ChequeSatus.NEW, chequeStatusList);
		fillComboBox(this.accountType, "", accTypeList);

		List<ChequeDetail> chequeDetails = ch.getChequeDetailList();

		fillBankBranch(ch, chequeDetails);

		chequeDetails = sortedChequeDetails(chequeDetails);

		doFillCheques(chequeDetails);

		financeSchedules = getFinanceSchedules();
		if (CollectionUtils.isNotEmpty(schdData.getFinanceScheduleDetails())) {
			financeSchedules = schdData.getFinanceScheduleDetails();
		}

		if (fromLoan) {
			setRepayAmount();
		} else {
			for (ChequeDetail cd : chequeDetails) {
				if (!ChequeSatus.REALISED.equals(cd.getChequeStatus())) {
					this.amount.setValue(PennantApplicationUtil.formateAmount(cd.getAmount(), ccyEditField));
					break;
				}
			}
		}

		this.totAmount.setValue(PennantApplicationUtil.formateAmount(ch.getTotalAmount(), ccyEditField));
		this.recordStatus.setValue(ch.getRecordStatus());

		this.pennyDropResult.setValue("");

		if (fromLoan && this.pennyDropResult.isVisible() && CollectionUtils.isNotEmpty(chequeDetails)) {
			ChequeDetail cd = chequeDetails.get(0);
			BankAccountValidation bav = pennyDropService.getPennyDropStatusDataByAcc(cd.getAccountNo(), cd.getIfsc());

			if (bav != null) {
				this.pennyDropResult.setValue(bav.isStatus() ? "Success" : "Fail");
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void doFillCheques(List<ChequeDetail> chequeDetails) {
		List<ChequeDetail> pdcCheques = new ArrayList<>();
		List<ChequeDetail> spdcCheques = new ArrayList<>();

		chequeDetails.stream().forEach(cheque -> {
			if (InstrumentType.isSPDC(cheque.getChequeType())) {
				spdcCheques.add(cheque);
			} else {
				pdcCheques.add(cheque);
			}
		});

		doFillChequeDetails(pdcCheques);
		doFillChequeDetails(spdcCheques);
	}

	public List<WrongValueException> doWriteComponentsToBean(ChequeHeader ch, boolean isGenarate) {
		logger.debug(Literal.ENTERING);

		List<WrongValueException> wve = new ArrayList<>();

		boolean spdc = InstrumentType.isSPDC(chequeType.getSelectedItem().getValue());
		try {
			if (spdc) {
				Integer chequeNo = this.totNoOfCheques.getValue();
				ch.setNoOfCheques(chequeNo == null ? 0 : chequeNo);
			} else {
				ch.setNoOfCheques(this.totNoOfCheques.getValue());
			}
		} catch (WrongValueException we) {
			if (!isGenarate && !spdc) {
				wve.add(we);
			}
		}

		try {
			ch.setTotalAmount(PennantApplicationUtil.unFormateAmount(this.totAmount.getValidateValue(), ccyEditField));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		ch.setActive(true);

		ch.setChequeDetailList(chequeDetailList);

		if (isGenarate) {
			wve.addAll(componentValues());
		} else {
			wve.addAll(doPrepareList(ch));

			if (CollectionUtils.isEmpty(wve)) {
				wve.addAll(validateChequeDetails(ch.getChequeDetailList(), true));
			}

		}

		if (!wve.isEmpty() && parenttab != null) {
			parenttab.setSelected(true);
		}

		showErrorDetails(wve);

		logger.debug(Literal.LEAVING);
		return wve;
	}

	public void doShowDialog(ChequeHeader chequeHeader) {
		logger.debug(Literal.LEAVING);

		doWriteBeanToComponents(chequeHeader);

		try {
			if (fromLoan) {
				chequeTabDisplay(chequeHeader.getChequeDetailList());
			} else if (tabPanel_dialogWindow != null) {
				tabPanel_dialogWindow.appendChild(this.windowChequeDetailDialog);
			} else {
				this.hboxNorth.setVisible(true);
				setDialog(DialogType.EMBEDDED);
			}

			if (enqiryModule) {
				this.btnSave.setVisible(false);
				this.btnNotes.setVisible(false);
				this.btnGen.setVisible(false);
				this.includeCoAppCust.setDisabled(true);

				this.readOnlyComponent(true, this.accountType);
				this.readOnlyComponent(true, this.bankBranchID);
				this.readOnlyComponent(true, this.noOfCheques);
				this.readOnlyComponent(true, this.chequeSerialNo);
				this.readOnlyComponent(true, this.chequeType);
				this.readOnlyComponent(true, this.accHolderName);
				this.readOnlyComponent(true, this.accNumber);
				this.readOnlyComponent(true, this.chequeSerialNo);
				this.readOnlyComponent(true, this.amount);
				this.readOnlyComponent(true, this.btnFetchAccountDetails);
				this.readOnlyComponent(true, customer);
				this.readOnlyComponent(true, this.micr);
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.chequeType.isDisabled()) {
			this.chequeType.setConstraint(new StaticListValidator(chequeTypeList, LABEL_CHQ_TYPE));
		}

		String instrumentType = this.chequeType.getSelectedItem().getValue();
		if (InstrumentType.isPDC(instrumentType) || !fromLoan) {
			this.totNoOfCheques.setConstraint(
					new PTNumberValidator(Labels.getLabel("label_ChequeDetailDialog_NoOfCheques.value"), true, false));
			this.totAmount.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_ChequeDetailDialog_Amount.value"), ccyEditField, true, false));
		}

		if (!this.accNumber.isReadonly() && bankDetail != null) {
			this.accNumber
					.setConstraint(new PTStringValidator(Labels.getLabel("label_ChequeDetailDialog_AccNumber.value"),
							PennantRegularExpressions.REGEX_ACCOUNTNUMBER, true, bankDetail.getMinAccNoLength(),
							bankDetail.getAccNoLength()));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetGenValidation() {
		logger.debug(Literal.LEAVING);

		doSetValidation();

		boolean isPDC = InstrumentType.isPDC(this.chequeType.getSelectedItem().getValue());

		if (!this.accountType.isDisabled()) {
			this.accountType.setConstraint(new PTListValidator<ValueLabel>(
					Labels.getLabel("label_ChequeDetailDialog_AccType.value"), accTypeList, true));
		}

		if (!this.accHolderName.isReadonly()) {
			this.accHolderName.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ChequeDetailDialog_AccHolderName.value"),
							PennantRegularExpressions.REGEX_CHEQUE_NAME, true));
		}

		if (!this.bankBranchID.isReadonly()) {
			this.bankBranchID.setConstraint(new PTStringValidator(
					Labels.getLabel("label_ChequeDetailDialog_BankBranchID.value"), null, true, true));
		}

		if (!this.amount.isReadonly() && isPDC) {
			this.amount.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_ChequeDetailDialog_AmountCD.value"), ccyEditField,
							(isPDC || (!SysParamUtil.isAllowed(SMTParameterConstants.UDC_ALLOW_ZERO_AMT))), false));
		}

		if (!this.chequeSerialNo.isReadonly()) {
			this.chequeSerialNo.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_ChequeDetailDialog_ChequeSerialNo.value"), true, false));
		}

		int numberOfTerms = financeDetail.getFinScheduleData().getFinanceMain().getNumberOfTerms();
		if (!this.noOfCheques.isReadonly()) {
			this.noOfCheques.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_ChequeDetailDialog_NoOfChequesCalc.value"), true, false, 0, numberOfTerms));
		}

		logger.debug(Literal.LEAVING);
	}

	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		Clients.clearWrongValue(this.totNoOfCheques);
		Clients.clearWrongValue(this.noOfCheques);

		this.totNoOfCheques.setConstraint("");
		this.totAmount.setConstraint("");
		this.bankBranchID.setConstraint("");
		this.accNumber.setConstraint("");
		this.chequeSerialNo.setConstraint("");
		this.amount.setConstraint("");
		this.noOfCheques.setConstraint("");

		this.totNoOfCheques.clearErrorMessage();
		this.totAmount.clearErrorMessage();
		this.bankBranchID.clearErrorMessage();
		this.accNumber.clearErrorMessage();
		this.chequeSerialNo.clearErrorMessage();
		this.amount.clearErrorMessage();
		this.noOfCheques.clearErrorMessage();

		logger.debug(Literal.LEAVING);
	}

	private void appendFinBasicDetails(List<Object> finHeaderList) {
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("parentCtrl", this);

			if (finHeaderList != null) {
				map.put(FIN_HEADER, finHeaderList);
			}

			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	private void showErrorDetails(List<WrongValueException> wve) {
		logger.debug(Literal.ENTERING);

		doRemoveValidation();

		if (CollectionUtils.isEmpty(wve)) {
			logger.debug(Literal.LEAVING);
			return;
		}

		logger.debug("Throwing occured Errors By using WrongValueException");

		if (parenttab != null) {
			parenttab.setSelected(true);
		}
		WrongValueException[] wvea = new WrongValueException[wve.size()];
		for (int i = 0; i < wve.size(); i++) {
			wvea[i] = wve.get(i);
		}

		logger.debug(Literal.LEAVING);
		throw new WrongValuesException(wvea);
	}

	public void doClear() {
		logger.debug(Literal.ENTERING);

		this.bankBranchID.setValue("");
		this.totAmount.setValue("");

		logger.debug(Literal.LEAVING);
	}

	public void doSavePDC(FinanceDetail fd, String finReference) {
		logger.debug(Literal.ENTERING);

		ChequeHeader ch = new ChequeHeader();
		BeanUtils.copyProperties(chequeHeader, ch);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		String rcdStatus = fm.getRecordStatus();

		doRemoveValidation();
		doSetValidation();

		List<WrongValueException> wve = doWriteComponentsToBean(ch, false);

		if (!wve.isEmpty() && parenttab != null) {
			parenttab.setSelected(true);
		} else if (!this.btnGen.isDisabled()) {
			WrongValueException exception = validateChequeCount(schdData);
			if (exception != null) {
				wve.add(exception);
			}
		}

		showErrorDetails(wve);

		if (StringUtils.isBlank(ch.getRecordType())) {
			ch.setVersion(ch.getVersion() + 1);
			ch.setRecordStatus(rcdStatus);
			if (ch.isNewRecord()) {
				ch.setNewRecord(true);
				ch.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			} else {
				ch.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			}
		}

		ch.setFinID(fm.getFinID());
		ch.setFinReference(finReference);
		ch.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginLogId());
		ch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		ch.setUserDetails(getUserWorkspace().getLoggedInUser());
		ch.setTaskId(getTaskId());
		ch.setNextTaskId(getNextTaskId());
		ch.setRoleCode(getRole());
		ch.setNextRoleCode(getNextRoleCode());

		if ((CollectionUtils.isEmpty(ch.getChequeDetailList())) && fd.getChequeHeader() == null) {
			fd.setChequeHeader(ch);
			return;
		}

		for (ChequeDetail cd : ch.getChequeDetailList()) {
			cd.setVersion(ch.getVersion() + 1);
			cd.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginLogId());
			cd.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			cd.setUserDetails(getUserWorkspace().getLoggedInUser());
			cd.setTaskId(getTaskId());
			cd.setNextTaskId(getNextTaskId());
			cd.setRoleCode(getRole());
			cd.setNextRoleCode(getNextRoleCode());
			cd.setRecordStatus(rcdStatus);
		}

		fd.setChequeHeader(ch);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$bankBranchID(Event event) {
		logger.debug(Literal.ENTERING.concat(event.getName()));

		Object dataObject = this.bankBranchID.getObject();
		if (dataObject == null || dataObject instanceof String) {
			this.accHolderName.setConstraint("");
			this.city.setValue("");
			this.micr.setValue("");
			this.ifsc.setValue("");
			this.cityName.setValue("");
			this.accHolderName.setValue("");
			this.accNumber.setValue("");
		} else {
			BankBranch details = (BankBranch) dataObject;
			this.bankBranchID.setAttribute(BANK_BRANCH_ID, details);
			this.micr.setValue(details.getMICR());
			this.ifsc.setValue(details.getIFSC());
			this.city.setValue(details.getCity());
			this.cityName.setValue(details.getPCCityName());
			this.bankBranchID.setValue(details.getBranchCode());
			this.bankBranchID.setDescription(details.getBankName());
			if (StringUtils.isNotBlank(details.getBankName())) {
				this.bankDetail = bankDetailService.getAccNoLengthByCode(details.getBankCode());
			}

			if (bankDetail != null) {
				this.accNumber.setMaxlength(this.bankDetail.getAccNoLength());
			}
		}

		logger.debug(Literal.LEAVING.concat(event.getName()));
	}

	public void onClickBtnGenerate(Event event) {
		logger.debug(Literal.ENTERING.concat(event.getName()));

		doRemoveValidation();

		doSetGenValidation();
		doWriteComponentsToBean(chequeHeader, true);

		if (StringUtils.trimToNull(this.bankBranchID.getValue()) == null) {
			logger.debug(Literal.LEAVING);
			return;
		}

		List<ChequeDetail> cheques = new ArrayList<>();

		int chequeSerialNum = this.chequeSerialNo.intValue();
		int numberofCheques = this.noOfCheques.getValue();
		int prvsNoOfCheques = this.totNoOfCheques.getValue() == null ? 0 : this.totNoOfCheques.getValue();
		String typeOfCheque = this.chequeType.getSelectedItem().getValue().toString();

		int emiNum = 0;

		for (int i = 0; i < numberofCheques; i++) {
			ChequeDetail cd = new ChequeDetail();

			cd.setAccountNo(this.accNumber.getValue());
			doFillBankBranch(cd);
			cd.setChequeSerialNumber(StringUtils.leftPad("" + chequeSerialNum, 6, "0"));
			chequeSerialNum = chequeSerialNum + 1;
			cd.setAccountNo(this.accNumber.getValue());

			cd.setAmount(BigDecimal.ZERO);

			if (!InstrumentType.isSPDC(typeOfCheque)) {
				cd.setAmount(PennantApplicationUtil.unFormateAmount(this.amount.getActualValue(), ccyEditField));
			}

			cd.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			cd.setNewRecord(true);
			cd.setActive(true);
			cd.setStatus(PennantConstants.RECORD_TYPE_NEW);
			cd.setChequeType(typeOfCheque);
			cd.setAccHolderName(this.accHolderName.getValue());
			cd.setAccountType(this.accountType.getSelectedItem().getValue().toString());
			cd.setChequeStatus(this.chequeStatus.getSelectedItem().getValue().toString());
			cd.setMicr(this.micr.getValue());

			if (ImplementationConstants.CHEQUE_AMOUNT_ZERO_UDC && !InstrumentType.isPDC(typeOfCheque)) {
				cd.setAmount(PennantApplicationUtil.unFormateAmount(BigDecimal.ZERO, ccyEditField));
			}

			if (InstrumentType.isPDC(cd.getChequeType())) {
				emiNum = getEmiNumber(emiNum);

				if (emiNum == -1) {
					MessageUtil.showMessage("Cheques are generated up to till maturity.");
					break;
				}

				cd.seteMIRefNo(emiNum);
			}

			cheques.add(cd);
			this.deleteCheques.setDisabled(false);
		}

		List<WrongValueException> wve = validateChequeDetails(cheques, false);

		doRemoveValidation();

		if (wve.isEmpty()) {
			int totalCheques = prvsNoOfCheques;
			for (ChequeDetail cheque : cheques) {
				if (!InstrumentType.isSPDC(cheque.getChequeType())) {
					totalCheques = totalCheques + 1;
				}
			}

			this.deleteCheques.setVisible(!cheques.isEmpty());
			listHeaderCheckBoxComp.setChecked(cheques.isEmpty());
			this.totNoOfCheques.setValue(totalCheques);
			chequeDetailList.addAll(cheques);

			cheques = cheques.stream().sorted((cd1, cd2) -> StringUtils.trimToEmpty(cd1.getChequeSerialNumber())
					.compareTo(cd2.getChequeSerialNumber())).collect(Collectors.toList());
			doFillCheques(cheques);
		} else {
			if (parenttab != null) {
				parenttab.setSelected(true);
			}

			MessageUtil.showError(CHQ_SERIAL_EXISTS);
		}

		logger.debug(Literal.LEAVING.concat(event.getName()));
	}

	public void onClickBtnPennyDropResult(Event event) {
		logger.info(Literal.ENTERING.concat(event.getName()));

		if (bankAccountValidationService == null) {
			return;
		}

		List<WrongValueException> wve = new ArrayList<>();

		doSetValidation();

		BankAccountValidation bav = new BankAccountValidation();

		if (fromLoan) {
			bav.setInitiateReference(chequeHeader.getFinReference());
		}

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

		if (pennyDropService.getPennyDropCount(bav.getAcctNum(), bav.getiFSC()) > 0) {
			MessageUtil.showMessage("This Account number with IFSC code already validated.");
			return;
		}

		try {
			this.pennyDropResult.setValue("Fail");
			bav.setStatus(false);
			bav.setInitiateType("C");

			if (bankAccountValidationService != null && bankAccountValidationService.validateBankAccount(bav)) {
				this.pennyDropResult.setValue("Sucess");
				bav.setStatus(true);
			}

			pennyDropService.savePennyDropSts(bav);
		} catch (Exception e) {
			MessageUtil.showMessage(e.getMessage());
		}

		logger.info(Literal.LEAVING.concat(event.getName()));
	}

	private void fillBankBranch(ChequeHeader ch, List<ChequeDetail> chequeDetails) {
		if (CollectionUtils.isEmpty(chequeDetails)) {
			return;
		}

		ChequeDetail cd = chequeDetails.get(chequeDetails.size() - 1);

		BankBranch details = new BankBranch();

		this.bankBranchID.setAttribute(BANK_BRANCH_ID, details);

		details.setBranchCode(cd.getBranchCode());
		details.setBankBranchID(cd.getBankBranchID());
		details.setBankName(cd.getBankName());
		details.setMICR(cd.getMicr());
		details.setIFSC(cd.getIfsc());
		details.setCity(cd.getCity());

		this.accNumber.setValue(cd.getAccountNo());
		this.accHolderName.setValue(cd.getAccHolderName());
		this.chequeSerialNo.setValue(Integer.valueOf(cd.getChequeSerialNumber()));
		this.bankBranchID.setValue(String.valueOf(cd.getBranchCode()));
		this.noOfCheques.setValue(ch.getNoOfCheques());
		this.micr.setValue(cd.getMicr());
		this.ifsc.setValue(cd.getIfsc());
		this.city.setValue(cd.getCity());

		fillComboBox(this.accountType, cd.getAccountType(), accTypeList);
		fillList(this.chequeType, cd.getChequeType(), chequeTypeList);
	}

	private void doFillChequeDetails(List<ChequeDetail> details) {
		if (CollectionUtils.isEmpty(details)) {
			return;
		}

		int index = getListItems().size() + 1;

		for (ChequeDetail cd : details) {
			boolean isReadOnly = isReadOnly(cd);

			Listitem listitem = new Listitem();
			listitem.setId(String.valueOf(index++));
			listitem.setAttribute("data", cd);

			appendSelectBox(listitem, isReadOnly, cd);

			appendChequeSerialNo(listitem, cd);

			appendAccountType(listitem, cd);

			appendAccountHolderName(listitem, cd);

			appendAccountNumber(listitem, cd);

			appendIFSCCode(listitem, cd);

			appendMICRCode(listitem, cd);

			appendEMIReference(listitem, cd, isReadOnly);

			appendInstallementNumber(listitem, cd);

			appendEMIAmount(listitem, cd, isReadOnly);

			appendChequeStatus(listitem, cd);

			appendUploadButton(listitem, cd, isReadOnly);

			appendViewButton(listitem, cd, isReadOnly);

			if (InstrumentType.isSPDC(cd.getChequeType())) {
				listBoxSPDCChequeDetail.appendChild(listitem);
			} else {
				listBoxChequeDetail.appendChild(listitem);
			}

		}

		validateCheckBox();

		this.totNoOfCheques.setValue(getNoOfCheques());
	}

	private boolean isReadOnly(ChequeDetail cd) {
		String chequeSts = cd.getChequeStatus();
		boolean isReadOnly = this.btnGen.isDisabled();

		if (ChequeSatus.CANCELLED.equals(chequeSts) && !cd.isNewRecord()
				&& PennantConstants.RECORD_TYPE_UPD.equals(cd.getRecordType())) {
			isReadOnly = true;
		}

		if (!fromLoan && !((ChequeSatus.NEW.equals(chequeSts)) || (PennantConstants.List_Select.equals(chequeSts)))) {
			isReadOnly = true;
		}

		return isReadOnly;
	}

	public void onChangeEMIReference(ForwardEvent event) {
		List<WrongValueException> wve = new ArrayList<>();

		Listitem selectecListItem = (Listitem) event.getOrigin().getTarget().getParent().getParent();

		List<Listcell> list = selectecListItem.getChildren();

		Combobox combobox = (Combobox) list.get(Field.DUE_DATE.index()).getFirstChild();
		CurrencyBox currencyBox = (CurrencyBox) list.get(Field.AMOUNT.index()).getFirstChild();
		Intbox intbox = (Intbox) list.get(Field.INSTALLMENT_NO.index()).getFirstChild();

		String strChequeDate = combobox.getSelectedItem().getLabel();

		if (PennantConstants.List_Select.equals(combobox.getSelectedItem().getValue())) {
			throw new WrongValueException(combobox,
					Labels.getLabel("DATE_NO_EMPTY", new String[] { Labels.getLabel("listheader_DueDate.label") }));
		}

		Date chequeDate = DateUtil.parseShortDate(strChequeDate);

		for (Listitem listitem : listBoxChequeDetail.getItems()) {
			if (selectecListItem.getId().equals(listitem.getId())) {
				continue;
			}

			List<Listcell> subListCell = listitem.getChildren();
			Combobox subComboBox = (Combobox) subListCell.get(Field.DUE_DATE.index()).getFirstChild();

			String subStrChequeDate = subComboBox.getSelectedItem().getLabel();
			if (Labels.getLabel(COMBO_SELECT).equals(subStrChequeDate)) {
				continue;
			}
			Date subChequeDate = DateUtil.parseShortDate(subStrChequeDate);

			if (chequeDate.compareTo(subChequeDate) == 0) {
				String label = Labels.getLabel("ChequeDetailDialog_EMI_Date_DUB");
				wve.add(new WrongValueException(combobox, label));
			}
		}

		if (wve.isEmpty()) {
			intbox.setValue((int) combobox.getSelectedItem().getValue());
			currencyBox.setValue(PennantApplicationUtil.formateAmount(getEmiAmount(chequeDate), 2));
		}

		showErrorDetails(wve);
	}

	public void onChangeChequeSerialNo(ForwardEvent event) {
		List<WrongValueException> wve = new ArrayList<>();

		Listitem selectecListItem = (Listitem) event.getOrigin().getTarget().getParent().getParent();

		List<Listcell> list = selectecListItem.getChildren();

		Intbox intbox = (Intbox) list.get(Field.CHEQUE_SERIAL_NO.index()).getFirstChild();

		int serialNo = intbox.getValue();

		List<Listitem> items = getListItems();

		for (Listitem listitem : items) {
			if (selectecListItem.getId().equals(listitem.getId())) {
				continue;
			}

			List<Listcell> sublist = listitem.getChildren();

			Intbox subintbox = (Intbox) sublist.get(Field.CHEQUE_SERIAL_NO.index()).getFirstChild();

			int subserialNo = subintbox.getValue();

			if (serialNo == subserialNo) {
				wve.add(new WrongValueException(intbox, CHQ_SERIAL_EXISTS));
			}
		}

		if (wve.isEmpty()) {
			intbox.setValue(serialNo);
		}

		showErrorDetails(wve);
	}

	private BigDecimal getEmiAmount(Date chequeDate) {
		for (FinanceScheduleDetail schedule : getFinSchedules()) {
			if (DateUtil.compare(chequeDate, schedule.getSchDate()) == 0) {
				return schedule.getRepayAmount().subtract(getTDSAmount(schedule));
			}
		}

		return BigDecimal.ZERO;
	}

	public void onFulfillEMIAmount(ForwardEvent event) {
		logger.info(Literal.ENTERING.concat(event.getName()));

		BigDecimal totalChequeAmt = BigDecimal.ZERO;
		int chequeNo = 0;

		for (Listitem listitem : listBoxChequeDetail.getItems()) {
			List<Listcell> subListCell = listitem.getChildren();
			CurrencyBox emiAmount = (CurrencyBox) subListCell.get(Field.AMOUNT.index()).getFirstChild();
			BigDecimal actualValue = emiAmount.getActualValue();

			if (actualValue == null || actualValue.compareTo(BigDecimal.ZERO) < 0) {
				actualValue = BigDecimal.ZERO;
				emiAmount.setValue(BigDecimal.ZERO);
			}

			totalChequeAmt = totalChequeAmt.add(actualValue);
			chequeNo++;
		}

		this.totAmount.setValue(totalChequeAmt);
		this.totNoOfCheques.setValue(chequeNo);

		logger.info(Literal.LEAVING.concat(event.getName()));
	}

	private List<WrongValueException> doPrepareList(ChequeHeader ch) {
		logger.debug(Literal.ENTERING);

		List<WrongValueException> wve = new ArrayList<>();

		List<ChequeDetail> cheques = ch.getChequeDetailList();

		List<Listitem> listItems = getListItems();

		for (Listitem listitem : listItems) {
			ChequeDetail cd = (ChequeDetail) listitem.getAttribute("data");

			List<Listcell> listcell = listitem.getChildren();

			ChequeDetail cheque = getChequeDetail(listcell, cheques);
			cheque.setChequeType(cd.getChequeType());

			Combobox accType = (Combobox) listcell.get(Field.ACCOUNT_TYPE.index()).getFirstChild();
			Combobox chequeSts = (Combobox) listcell.get(Field.CHEQUE_STATUS.index()).getFirstChild();

			String chType = cheque.getChequeType();
			boolean pdc = InstrumentType.isPDC(chType);

			if (!InstrumentType.isSPDC(cheque.getChequeType())) {
				Combobox dueDate = (Combobox) listcell.get(Field.DUE_DATE.index()).getFirstChild();
				CurrencyBox emiAmount = (CurrencyBox) listcell.get(Field.AMOUNT.index()).getFirstChild();

				dueDate.clearErrorMessage();
				emiAmount.clearErrorMessage();

				cheque.setAmount(PennantApplicationUtil.unFormateAmount(emiAmount.getActualValue(), ccyEditField));

				cheque.seteMIRefNo(-1);
				if (!PennantConstants.List_Select.equals(getComboboxValue(dueDate))) {
					setChequeDate(cheque, dueDate);
				} else if (pdc) {
					wve.add(new WrongValueException(dueDate, Labels.getLabel("ChequeDetailDialog_Duedate_Mand")));
				}
			} else {
				cheque.setAmount(BigDecimal.ZERO);
				cheque.seteMIRefNo(-1);
				cheque.setChequeDate(null);
			}

			accType.clearErrorMessage();

			Integer chequeSerialNo = ((Intbox) listcell.get(Field.CHEQUE_SERIAL_NO.index()).getFirstChild()).getValue();
			cheque.setChequeSerialNumber(StringUtils.leftPad("" + chequeSerialNo, 6, "0"));
			cheque.setAccountType(accType.getSelectedItem().getValue().toString());
			cheque.setAccHolderName(listcell.get(Field.ACC_HOLDER_NAME.index()).getLabel());
			cheque.setAccountNo(listcell.get(Field.ACCOUNT_NO.index()).getLabel());

			cheque.setChequeStatus(chequeSts.getSelectedItem().getValue().toString());

			String cbAccType = getComboboxValue(accType);

			if (pdc && PennantConstants.List_Select.equals(cbAccType)) {
				wve.add(new WrongValueException(accType, Labels.getLabel("ChequeDetailDialog_AccountType_Mand")));
			}

			Object bankBranch = this.bankBranchID.getAttribute(BANK_BRANCH_ID);
			if (bankBranch != null) {
				BankBranch branch = (BankBranch) bankBranch;
				cheque.setBankBranchID(branch.getBankBranchID());
				cheque.setBranchCode(branch.getBranchCode());
				cheque.setIfsc(listcell.get(Field.BANK_IFSC_CODE.index()).getLabel());
			}

			cheque.setMicr(listcell.get(Field.MICR_CODE.index()).getLabel());

			if (!cheque.isOldCheque()) {
				setChequeDocuments(cheque);
				cheques.add(cheque);
				cheque.setActive(true);
				cheque.setStatus(PennantConstants.RECORD_TYPE_NEW);
			}
		}

		ch.setChequeDetailList(cheques);
		logger.debug(Literal.LEAVING);
		return wve;
	}

	private List<Listitem> getListItems() {
		List<Listitem> listItems = new ArrayList<>();

		listItems.addAll(listBoxChequeDetail.getItems());
		listItems.addAll(listBoxSPDCChequeDetail.getItems());
		return listItems;
	}

	private ChequeDetail getChequeDetail(List<Listcell> listCells, List<ChequeDetail> cheques) {
		ChequeDetail cheque = new ChequeDetail();

		cheque.setNewRecord(true);
		cheque.setRecordType(PennantConstants.RCD_ADD);
		cheque.setChequeCcy(this.ccy);

		if (CollectionUtils.isEmpty(cheques)) {
			return cheque;
		}

		String ifscCode = StringUtils.trimToEmpty(listCells.get(Field.BANK_IFSC_CODE.index()).getLabel());
		String accountNumber = StringUtils.trimToEmpty(listCells.get(Field.ACCOUNT_NO.index()).getLabel());
		int serialNo = ((Intbox) listCells.get(Field.CHEQUE_SERIAL_NO.index()).getFirstChild()).getValue();

		for (ChequeDetail cd : cheques) {
			int cdSerialNo = Integer.valueOf(cd.getChequeSerialNumber());
			String cdIfsc = cd.getIfsc();
			String cdAccountNo = cd.getAccountNo();

			if (cdSerialNo == serialNo && ifscCode.equals(cdIfsc) && accountNumber.equals(cdAccountNo)) {
				if (cd.isUpload() && !fromLoan) {
					cd.setNewRecord(true);
					cd.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

				cd.setChequeCcy(this.ccy);
				cd.setOldCheque(true);
				return cd;
			}
		}

		return cheque;
	}

	public void onClickDeleteCheques(ForwardEvent event) {
		logger.info(Literal.ENTERING.concat(event.getName()));

		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record");

		MessageUtil.confirm(msg, evnt -> {
			if (Messagebox.ON_YES.equals(evnt.getName())) {
				doDeleteCheques();
			}
		});

		logger.info(Literal.LEAVING.concat(event.getName()));
	}

	private void doDeleteCheques() {
		List<ChequeDetail> cheques = new ArrayList<>();

		boolean deletedCheques = false;
		int count = 0;
		BigDecimal totamt = BigDecimal.ZERO;
		for (Listitem listItem : getListItems()) {
			Checkbox checkbox = (Checkbox) listItem.getFirstChild().getFirstChild();

			ChequeDetail cheque = (ChequeDetail) listItem.getAttribute("data");

			if (InstrumentType.isPDC(cheque.getChequeType())) {
				cheque.setChequeDate(getSchdDate(listItem));
				cheque.setAmount(getEmiAmount(listItem));
			}

			if (ChequeSatus.PRESENT.equals(cheque.getChequeStatus())) {
				cheques.add(cheque);
				continue;
			}

			BigDecimal schdAmt = BigDecimal.ZERO;
			if (!checkbox.isChecked()) {
				int eMIRefNo = cheque.geteMIRefNo() - count;
				if (!fromLoan) {
					if (eMIRefNo != cheque.geteMIRefNo()) {
						cheque.setRecordType(PennantConstants.RCD_UPD);
					}
					schdAmt = getEmiAmount(cheque.getChequeDate());
				}

				cheque.seteMIRefNo(eMIRefNo);
				cheques.add(cheque);

				totamt = totamt.add(schdAmt);
				continue;
			}

			deletedCheques = true;
			count++;

			if (checkbox.isChecked() && ChequeSatus.CANCELLED.equals(cheque.getChequeStatus())) {
				cheques.add(cheque);
				continue;
			}

			cheque.setChequeStatus(ChequeSatus.CANCELLED);

			setWorkflowDetailsOnDelete(cheque);

			String rcdType = cheque.getRecordType();
			if (!cheque.isNewRecord() && !PennantConstants.RECORD_TYPE_UPD.equals(rcdType)) {
				cheques.add(cheque);
			}

		}

		if (!deletedCheques) {
			MessageUtil.showError(Labels.getLabel("Delete_DataList_NoEmpty"));
			return;
		}

		this.deleteCheques.setDisabled(getListItems().size() == count);
		listHeaderCheckBoxComp.setDisabled(listBoxChequeDetail.getItems().size() == count);
		listSPDCHeaderCheckBoxComp.setDisabled(listBoxSPDCChequeDetail.getItems().size() == count);

		listBoxChequeDetail.getItems().clear();
		listBoxSPDCChequeDetail.getItems().clear();
		this.totAmount.setValue(BigDecimal.ZERO);

		setChequeDetailList(cheques);

		cheques = cheques.stream().sorted((cd1, cd2) -> StringUtils.trimToEmpty(cd1.getChequeSerialNumber())
				.compareTo(cd2.getChequeSerialNumber())).collect(Collectors.toList());

		doFillCheques(cheques);

		if (!fromLoan) {
			this.totAmount.setValue(PennantApplicationUtil.formateAmount(totamt, ccyEditField));
		}
	}

	private BigDecimal getEmiAmount(Listitem listitem) {
		CurrencyBox currencybox = (CurrencyBox) listitem.getChildren().get(Field.AMOUNT.index()).getFirstChild();

		BigDecimal emiAmount = currencybox.getValidateValue();

		return PennantApplicationUtil.formateAmount(emiAmount, ccyEditField);
	}

	private Date getSchdDate(Listitem listitem) {
		Combobox combobox = (Combobox) listitem.getChildren().get(Field.DUE_DATE.index()).getFirstChild();
		String combovalue = combobox.getSelectedItem().getLabel();
		if (Labels.getLabel(COMBO_SELECT).equals(combovalue)) {
			return null;
		}
		return DateUtil.parseShortDate(combovalue);
	}

	private void setWorkflowDetailsOnDelete(ChequeDetail cd) {
		String rcdType = cd.getRecordType();

		if (!cd.isNewRecord()) {
			cd.setActive(false);
			cd.setRecordStatus(PennantConstants.RCD_STATUS_CANCELLED);
			cd.setStatus(ChequeSatus.CANCELLED);

			if (fromLoan) {
				cd.setRecordType(PennantConstants.RCD_DEL);
			} else {
				if (PennantConstants.RECORD_TYPE_NEW.equals(rcdType)) {
					cd.setRecordType(PennantConstants.RCD_DEL);
				} else if (!PennantConstants.RECORD_TYPE_UPD.equals(rcdType)) {
					cd.setRecordType(PennantConstants.RCD_UPD);
				}
			}
		}
	}

	public void onClickButtonUpload(ForwardEvent event) {
		ChequeDetail cd = (ChequeDetail) event.getData();

		Map<String, Object> map = new HashMap<>();
		map.put("ChequeDetailDialogCtrl", this);
		map.put("chequeDetail", cd);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/PDC/ChequeDetailDocumentDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	public void onClickButtonView(ForwardEvent event) {
		ChequeDetail cd = (ChequeDetail) event.getData();

		Map<String, Object> map = new HashMap<>();
		map.put("ChequeDetailDialogCtrl", this);
		map.put("chequeDetail", cd);
		map.put("enqModule", true);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/PDC/ChequeDetailDocumentDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	private Combobox getCombobox(String emiNumber) {
		Combobox combobox = new Combobox();
		combobox.setSclass(PennantConstants.mandateSclass);
		combobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue(PennantConstants.List_Select);
		comboitem.setLabel(Labels.getLabel(COMBO_SELECT));
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);
		combobox.setReadonly(true);

		financeSchedules = getFinSchedules();

		for (FinanceScheduleDetail schedule : financeSchedules) {
			if ((schedule.isRepayOnSchDate() || schedule.isPftOnSchDate())
					&& (schedule.getInstNumber() != 0 || FinanceConstants.FLAG_BPI.equals(schedule.getBpiOrHoliday()))
					&& BigDecimal.ZERO.equals(schedule.getPartialPaidAmt())) {
				comboitem = new Comboitem();
				comboitem.setValue(schedule.getInstNumber());
				comboitem.setLabel(DateUtil.formatToShortDate(schedule.getSchDate()));
				comboitem.setAttribute("SchdDate", schedule.getSchDate());
				combobox.appendChild(comboitem);

				if (String.valueOf(schedule.getInstNumber()).equals(String.valueOf(emiNumber))) {
					combobox.setSelectedItem(comboitem);
				}
			}
		}

		return combobox;
	}

	private void getCombobox(Combobox combobox) {
		combobox.setSclass(PennantConstants.mandateSclass);
		combobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue(PennantConstants.List_Select);
		comboitem.setLabel(Labels.getLabel(COMBO_SELECT));
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);
		combobox.setReadonly(true);

		financeSchedules = getFinSchedules();

		for (FinanceScheduleDetail schedule : financeSchedules) {
			if (schedule.isRepayOnSchDate() || schedule.isPftOnSchDate()) {
				comboitem = new Comboitem();
				comboitem.setValue(schedule.getInstNumber());
				comboitem.setLabel(DateUtil.formatToShortDate(schedule.getSchDate()));
				comboitem.setAttribute("SchdDate", schedule.getSchDate());
				combobox.appendChild(comboitem);
			}
		}
	}

	private Combobox getChequeStatusComboBox(String chqStatus) {
		Combobox combobox = new Combobox();
		combobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue(PennantConstants.List_Select);
		comboitem.setLabel(Labels.getLabel(COMBO_SELECT));
		combobox.appendChild(comboitem);
		if (fromLoan) {
			combobox.setDisabled(true);
		}
		fillComboBox(combobox, chqStatus, chequeStatusList, "");
		return combobox;
	}

	private List<Object> getFinBasicDetails(FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		FinanceType finType = schdData.getFinanceType();

		List<Object> list = new ArrayList<>();

		list.add(0, fm.getFinType());
		list.add(1, fm.getFinCcy());
		list.add(2, fm.getScheduleMethod());
		list.add(3, fm.getFinReference());
		list.add(4, fm.getProfitDaysBasis());
		list.add(5, fm.getGrcPeriodEndDate());
		list.add(6, fm.isAllowGrcPeriod());
		list.add(7, StringUtils.isNotEmpty(finType.getProduct()));
		list.add(8, finType.getFinCategory());
		list.add(9, fd.getCustomerDetails().getCustomer().getCustShrtName());
		list.add(10, fm.isNewRecord());
		list.add(11, "");

		logger.debug(Literal.LEAVING);
		return list;
	}

	public void checkTabDisplay(FinanceDetail fd, String mandateType, boolean isContainPrvsCheques) {
		logger.debug(Literal.ENTERING);
		boolean isChqCaptureReq = fd.getFinScheduleData().getFinanceType().isChequeCaptureReq();

		this.parenttab.setVisible(false);

		if (isChqCaptureReq || isContainPrvsCheques) {
			this.parenttab.setVisible(true);

			if (InstrumentType.isPDC(mandateType)) {
				fillList(this.chequeType, InstrumentType.PDC.name(), chequeTypeList);
				this.pdcReqFlag = true;
			} else {
				fillList(this.chequeType, InstrumentType.SPDC.code(), chequeTypeList, ",PDC,");
				this.pdcReqFlag = false;

				List<ChequeDetail> list = new ArrayList<>();
				for (ChequeDetail cd : chequeDetailList) {
					if (!InstrumentType.isPDC(cd.getChequeType())) {
						list.add(cd);
					}
				}

				chequeDetailList.clear();
				listBoxChequeDetail.getItems().clear();
				this.deleteCheques.setDisabled(getListItems().isEmpty());
				listHeaderCheckBoxComp.setDisabled(true);

				setChequeDetailList(list);

				doFillCheques(chequeDetailList);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void setUpdatedSchdules() {
		for (Listitem listitem : this.listBoxChequeDetail.getItems()) {
			List<Listcell> list = listitem.getChildren();
			Listcell chqType = list.get(Field.CHEQUE_TYPE.index());
			Combobox emi = (Combobox) list.get(Field.DUE_DATE.index()).getFirstChild();
			emi.clearErrorMessage();

			if (PennantConstants.List_Select.equals(getComboboxValue(emi))
					&& InstrumentType.isPDC(chqType.getLabel())) {
				String emiRefNum = emi.getSelectedItem().getValue().toString();
				if (!StringUtils.isNumeric(emiRefNum)) {
					getCombobox(emi);
				}
			}
		}
	}

	private AuditHeader getAuditHeader(ChequeHeader aChequeHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aChequeHeader.getBefImage(), aChequeHeader);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aChequeHeader.getUserDetails(),
				getOverideMap());
	}

	public void doSetLabels(List<Object> finHeaderList) {
		finBasicDetailsCtrl.doWriteBeanToComponents(new ArrayList<>(finHeaderList));

		financeSchedules = getFinSchedules();

		if (CollectionUtils.isEmpty(financeSchedules)) {
			return;
		}

		for (FinanceScheduleDetail schedule : financeSchedules) {
			if (schedule.isRepayOnSchDate() || schedule.isPftOnSchDate()) {
				BigDecimal repayAmount = schedule.getRepayAmount().subtract(getTDSAmount(schedule));
				this.amount.setValue(PennantApplicationUtil.formateAmount(repayAmount, ccyEditField));
				break;
			}
		}
	}

	public void fetchAccounts() {
		Long custID = null;

		this.accNumber.setMaxlength(LengthConstants.LEN_ACCOUNT);

		if (financeDetail == null) {
			return;
		} else {
			custID = financeDetail.getCustomerDetails().getCustID();
		}

		Customer details1 = (Customer) this.customer.getObject();

		if (details1 != null) {
			custID = details1.getCustID();
		}

		Filter[] filter = new Filter[2];
		filter[0] = new Filter("CustID", custID, Filter.OP_EQUAL);
		filter[1] = new Filter("RepaymentFrom", "Y", Filter.OP_EQUAL);

		String code = "CustomerBankInfoAccntNumbers";
		doFillCustomerBankInfo(ExtendedSearchListBox.show(this.windowChequeDetailDialog, code, filter, ""));
	}

	private void doFillCustomerBankInfo(Object dataObject) {
		if (dataObject instanceof CustomerBankInfo) {
			CustomerBankInfo details = (CustomerBankInfo) dataObject;
			this.accNumber.setValue(details.getAccountNumber());
			this.accHolderName.setValue(details.getAccountHolderName());
			this.ifsc.setValue(details.getiFSC());

			if (details.getBranchCode() != null) {
				this.bankBranchID.setValue(details.getBranchCode(), details.getLovDescBankName());
			}

			this.city.setValue(details.getCity());
			this.micr.setValue(details.getMicr());

			if (StringUtils.isNotBlank(details.getBankName())) {
				this.bankDetail = bankDetailService.getAccNoLengthByCode(details.getBankCode());
			}

			if (bankDetail != null) {
				this.accNumber.setMaxlength(this.bankDetail.getAccNoLength());
			}

			BankBranch branch = new BankBranch();
			if (details.getiFSC() != null) {
				branch.setBankBranchID(details.getBankBranchID());
				branch.setBranchCode(details.getBranchCode());
				branch.setIFSC(details.getiFSC());
				branch.setBankName(branch.getBankName());
				this.bankBranchID.setAttribute(BANK_BRANCH_ID, branch);
			}
		}
	}

	public void doSetCustomerFilters() {
		Filter[] filters = new Filter[1];
		List<String> custCIFs = new ArrayList<>();

		if (financeMainDialogCtrl != null) {
			JointAccountDetailDialogCtrl jointAcCtrl = financeMainDialogCtrl.getJointAccountDetailDialogCtrl();
			if (jointAcCtrl != null) {
				jointAcCtrl.getJointAccountCustomers().stream().forEach(c1 -> custCIFs.add(c1.getCustCIF()));
			}
		}

		if (financeDetail != null) {
			custCIFs.add(financeDetail.getCustomerDetails().getCustomer().getCustCIF());
			financeDetail.getJointAccountDetailList().stream().forEach(c1 -> custCIFs.add(c1.getCustCIF()));
		}

		if (CollectionUtils.isNotEmpty(custCIFs)) {
			filters[0] = new Filter(CUST_CIF, custCIFs, Filter.OP_IN);
		}
		customer.setFilters(filters);
	}

	private WrongValueException validateChequeCount(FinScheduleData schdData) {
		FinanceMain fm = schdData.getFinanceMain();

		String[] args = new String[2];

		args[0] = Labels.getLabel("label_ChequeDetailDialog_NoOfCheques.value");

		if (InstrumentType.isPDC(fm.getFinRepayMethod())) {
			financeSchedules = getFinSchedules();

			int noOfSchedules = financeSchedules.size() - 1;
			int noOfPDCCheques = SysParamUtil.getValueAsInt(SMTParameterConstants.NUMBEROF_PDC_CHEQUES);

			int number = 0;
			if (noOfSchedules >= noOfPDCCheques) {
				number = noOfPDCCheques;
			} else {
				number = noOfSchedules;
			}

			if (this.totNoOfCheques.intValue() < number) {
				parenttab.setSelected(true);

				args[1] = String.valueOf(number);
				return new WrongValueException(this.totNoOfCheques, Labels.getLabel("NUMBER_MINVALUE_EQ", args));
			}
		} else if (schdData.getFinanceType().isChequeCaptureReq()) {
			int noOfUndateCHeques = SysParamUtil.getValueAsInt(SMTParameterConstants.NUMBEROF_UNDATED_CHEQUES);

			if (this.totNoOfCheques.intValue() < noOfUndateCHeques) {
				parenttab.setSelected(true);

				args[1] = String.valueOf(noOfUndateCHeques);
				return new WrongValueException(this.totNoOfCheques, Labels.getLabel("NUMBER_MINVALUE_EQ", args));
			}

		}

		return null;
	}

	private List<WrongValueException> validateChequeDetails(List<ChequeDetail> cheques, boolean validate) {
		logger.debug(Literal.ENTERING);

		List<WrongValueException> wve = new ArrayList<>();

		List<Listitem> items = getListItems();

		for (Listitem item : items) {
			List<Listcell> listcell = item.getChildren();

			Combobox combobox = (Combobox) listcell.get(Field.DUE_DATE.index()).getFirstChild();

			if (!validate) {
				wve.addAll(validateCheque(cheques, listcell));
			} else if (InstrumentType.isPDC(listcell.get(Field.CHEQUE_TYPE.index()).getLabel())) {

				if (PennantConstants.RCD_STATUS_CANCELLED.equals(chequeStatus.getValue())) {
					wve.addAll(validateEMIReference(cheques, combobox));
				}

				String emiCBValue = getComboboxValue(combobox);
				if (!"0".equals(emiCBValue) && !PennantConstants.List_Select.equals(emiCBValue)) {
					Date emiDate = DateUtil.parseShortDate(getCombobox(emiCBValue).getSelectedItem().getLabel());

					wve.addAll(validateSchedules(listcell, combobox, emiDate));
				}

				int emiRefNumCnt = 0;

				for (ChequeDetail cd : cheques) {
					if (!chequeDuplicate(cd, getComboboxValue(combobox))) {
						continue;
					}

					emiRefNumCnt++;
					if (emiRefNumCnt > 1) {
						if (fromLoan) {
							parenttab.setSelected(true);
						}
						try {
							throw new WrongValueException(combobox,
									Labels.getLabel("ChequeDetailDialog_ChkEMIRef_Exists"));
						} catch (WrongValueException e) {
							wve.add(e);
							break;
						}
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return wve;
	}

	private List<WrongValueException> validateCheque(List<ChequeDetail> cheques, List<Listcell> listcell) {
		List<WrongValueException> wve = new ArrayList<>();

		Listcell serialNum = listcell.get(Field.CHEQUE_SERIAL_NO.index());

		Intbox intbox = (Intbox) serialNum.getFirstChild();
		String serialNo = String.valueOf(intbox.intValue());
		String ifscCode = listcell.get(Field.BANK_IFSC_CODE.index()).getLabel();
		String accountNum = StringUtils.trimToEmpty(listcell.get(Field.ACCOUNT_NO.index()).getLabel());

		for (ChequeDetail cd : cheques) {
			String recordType = cd.getRecordType();
			String cdSerialNo = String.valueOf(cd.getChequeSerialNumber());
			String acc = cd.getAccountNo();
			String cdIfsc = cd.getIfsc();

			if (!PennantConstants.RECORD_TYPE_DEL.equals(recordType) && serialNo.equals(cdSerialNo)
					&& ifscCode.equals(cdIfsc) && accountNum.equals(acc)) {
				wve.add(new WrongValueException(serialNum, CHQ_SERIAL_EXISTS));
				break;
			}
		}

		return wve;
	}

	private List<WrongValueException> validateEMIReference(List<ChequeDetail> cheques, Combobox combobox) {
		List<WrongValueException> wve = new ArrayList<>();

		String emiRefNum = getComboboxValue(combobox);

		if (!StringUtils.isNumeric(emiRefNum)) {
			return wve;
		}

		int emiNum = Integer.parseInt(emiRefNum);

		boolean emiRefExists = false;
		for (ChequeDetail cd : cheques) {
			String rcdStatus = cd.getRecordStatus();
			if (!PennantConstants.RCD_STATUS_CANCELLED.equals(rcdStatus)) {
				String chqType = cd.getChequeType();

				if (emiNum == cd.geteMIRefNo() && InstrumentType.isPDC(chqType)) {
					emiRefExists = true;
					break;
				}
			}
		}

		if (emiRefExists) {
			wve.add(new WrongValueException(combobox, Labels.getLabel("ChequeDetailDialog_ChkEMIRef_Exists")));
		}

		return wve;
	}

	private List<WrongValueException> validateSchedules(List<Listcell> list, Combobox comboBox, Date emiDate) {
		List<WrongValueException> wve = new ArrayList<>();

		if (financeSchedules == null) {
			return wve;
		}

		List<FinanceScheduleDetail> schedules = getFinSchedules();

		wve.addAll(validateChequeAmount(list, comboBox, emiDate, schedules));

		Date schEmiDate = DateUtil.parse(comboBox.getSelectedItem().getLabel(), PennantConstants.dateFormat);
		boolean isEmiDateExit = isEmiDateExists(schedules, schEmiDate);

		if (!isEmiDateExit && !PennantConstants.RCD_STATUS_CANCELLED.equals(chequeStatus.getValue())) {
			wve.add(new WrongValueException(comboBox, Labels.getLabel("ChequeDetailDialog_EMI_Date")));
		}

		return wve;
	}

	private List<WrongValueException> validateChequeAmount(List<Listcell> listcell, Combobox box, Date emiDate,
			List<FinanceScheduleDetail> schedules) {

		List<WrongValueException> wve = new ArrayList<>();

		FinScheduleData schdData = financeDetail.getFinScheduleData();

		FinanceMain fm = schdData.getFinanceMain();

		CurrencyBox currencybox = (CurrencyBox) listcell.get(Field.AMOUNT.index()).getFirstChild();

		BigDecimal emiAmount = currencybox.getActualValue();
		String bpiTreatment = fm.getBpiTreatment();
		Date appDate = SysParamUtil.getAppDate();

		for (FinanceScheduleDetail schedule : schedules) {
			Combobox combobox = (Combobox) listcell.get(Field.CHEQUE_STATUS.index()).getFirstChild();
			combobox.clearErrorMessage();

			if (!(DateUtil.compare(emiDate, schedule.getSchDate()) != 0
					|| !ChequeSatus.NEW.equals(getComboboxValue(combobox)))) {

				if ("B".equals(schedule.getBpiOrHoliday()) && FinanceConstants.BPI_DISBURSMENT.equals(bpiTreatment)) {
					String label = Labels.getLabel("ChequeDetailDialog_ChkEMIRef_BPI_DeductDisb");
					wve.add(new WrongValueException(box, label));
				}

				if (!PennantConstants.RCD_STATUS_CANCELLED.equalsIgnoreCase(chequeStatus.getValue())) {
					WrongValueException exception = validateChequeAmount(schedule, appDate, emiDate, currencybox,
							emiAmount);

					if (exception != null) {
						wve.add(exception);
						break;
					}
				}
			}
		}

		return wve;
	}

	private WrongValueException validateChequeAmount(FinanceScheduleDetail schedule, Date appDate, Date emiDate,
			CurrencyBox emiAmount, BigDecimal emiActualAmt) {

		BigDecimal tdsAmount = getTDSAmount(schedule);
		BigDecimal repayAmount = schedule.getRepayAmount().subtract(tdsAmount);

		int format = CurrencyUtil.getFormat(schedule.getFinCcy());
		BigDecimal emiAmounte = PennantApplicationUtil.unFormateAmount(emiActualAmt, format);

		if (repayAmount.compareTo(emiAmounte) != 0) {
			String label = "";

			if (!emiAmount.isReadonly()) {
				if (tdsAmount.compareTo(BigDecimal.ZERO) > 0) {
					label = Labels.getLabel("ChequeDetailDialog_EMI_TDS_Amount");
				} else if (DateUtil.compare(appDate, emiDate) <= 0) {
					label = Labels.getLabel("ChequeDetailDialog_EMI_Amount");

				}

				return new WrongValueException(emiAmount, label);
			}
		}

		return null;
	}

	private boolean isEmiDateExists(List<FinanceScheduleDetail> schedules, Date schEmiDate) {
		for (FinanceScheduleDetail schedule : schedules) {
			if (DateUtil.compare(schEmiDate, schedule.getSchDate()) == 0) {
				return true;
			}
		}

		return false;
	}

	private BigDecimal getTDSAmount(FinanceScheduleDetail schedule) {
		if (schedule.getTDSAmount() == null) {
			return BigDecimal.ZERO;
		}

		if (schedule.getTDSAmount().compareTo(BigDecimal.ZERO) <= 0) {
			return BigDecimal.ZERO;
		}

		return schedule.getTDSAmount();
	}

	private void setChequeDocuments(ChequeDetail cheque) {
		for (ChequeDetail cd : chequeDocuments) {
			if (cd.getChequeSerialNumber() == cheque.getChequeSerialNumber()) {
				cheque.setDocImage(cd.getDocImage());
				cheque.setDocumentName(cd.getDocumentName());
			}
		}
	}

	private void setChequeDate(ChequeDetail cheque, Combobox emiReference) {
		Comboitem comboitem = emiReference.getSelectedItem();
		String emiRefNum = comboitem.getValue().toString();

		if (StringUtils.isNumeric(emiRefNum)) {
			cheque.seteMIRefNo(Integer.parseInt(emiRefNum));
		}

		cheque.setChequeDate(DateUtil.parseShortDate(comboitem.getLabel()));
	}

	private void setRepayAmount() {
		if (CollectionUtils.isEmpty(financeSchedules)) {
			return;
		}

		for (FinanceScheduleDetail schedule : financeSchedules) {
			if (!(schedule.isRepayOnSchDate() || schedule.isPftOnSchDate())) {
				continue;
			}

			BigDecimal repayAmount = schedule.getRepayAmount();
			BigDecimal tdsAmount = schedule.getTDSAmount();

			if (tdsAmount != null && tdsAmount.compareTo(BigDecimal.ZERO) > 0) {
				repayAmount = repayAmount.subtract(tdsAmount);
			}

			this.amount.setValue(PennantApplicationUtil.formateAmount(repayAmount, ccyEditField));
		}
	}

	private void appendSelectBox(Listitem listitem, boolean isReadOnly, ChequeDetail cd) {
		Checkbox checkBox = new Checkbox();
		checkBox.setChecked(ChequeSatus.CANCELLED.equals(cd.getChequeStatus()));
		checkBox.setDisabled(!isDeleteVisible() || isReadOnly);
		checkBox.addForward(Events.ON_CLICK, this.window, "onClickCheckBox");

		Listcell lc = new Listcell();
		lc.appendChild(checkBox);

		listitem.appendChild(lc);
	}

	private void appendChequeSerialNo(Listitem listitem, ChequeDetail cd) {
		Intbox intbox = new Intbox();
		intbox.setValue(Integer.valueOf(cd.getChequeSerialNumber()));
		intbox.setFormat("000000");
		intbox.setMaxlength(6);
		intbox.addForward(Events.ON_CHANGE, this.window, "onChangeChequeSerialNo");

		if (!cd.isNewRecord()) {
			intbox.setReadonly(true);
		}

		intbox.setWidth(WIDTH_100PX);

		Listcell lc = new Listcell();
		lc.appendChild(intbox);

		listitem.appendChild(lc);
	}

	private void appendAccountType(Listitem listitem, ChequeDetail cd) {
		Combobox combobox = new Combobox();
		combobox.setWidth("130px");

		fillComboBox(combobox, cd.getAccountType(), accTypeList);

		readOnlyComponent(true, combobox);

		Listcell lc = new Listcell();
		lc.appendChild(combobox);

		listitem.appendChild(lc);
	}

	private void appendAccountHolderName(Listitem listitem, ChequeDetail cd) {
		Listcell lc = new Listcell(cd.getAccHolderName());

		listitem.appendChild(lc);
	}

	private void appendAccountNumber(Listitem listitem, ChequeDetail cd) {
		Listcell lc = new Listcell(cd.getAccountNo());

		listitem.appendChild(lc);
	}

	private void appendIFSCCode(Listitem listitem, ChequeDetail cd) {
		Listcell lc = new Listcell(cd.getIfsc());

		listitem.appendChild(lc);
	}

	private void appendMICRCode(Listitem listitem, ChequeDetail cd) {
		Listcell lc = new Listcell(cd.getMicr());

		listitem.appendChild(lc);
	}

	private void appendEMIReference(Listitem listitem, ChequeDetail cd, boolean isReadOnly) {
		Combobox combobox = new Combobox();
		readOnlyComponent(true, combobox);

		if (!InstrumentType.isSPDC(cd.getChequeType())) {
			combobox = getCombobox(String.valueOf(cd.geteMIRefNo()));

			if (!InstrumentType.isPDC(cd.getChequeType()) || enqiryModule) {
				readOnlyComponent(true, combobox);
			} else {
				readOnlyComponent(isReadOnly, combobox);
			}

			combobox.addForward(Events.ON_CHANGE, this.window, "onChangeEMIReference");
		}

		combobox.setWidth(WIDTH_100PX);

		Listcell lc = new Listcell();
		lc.appendChild(combobox);

		listitem.appendChild(lc);
	}

	private void appendInstallementNumber(Listitem listitem, ChequeDetail cd) {
		Intbox intBox = new Intbox();
		intBox.setReadonly(true);
		intBox.setStyle("text-align:right");

		if (!InstrumentType.isSPDC(cd.getChequeType())) {
			intBox.setValue(cd.geteMIRefNo());
		}

		intBox.setWidth(WIDTH_100PX);
		Listcell lc = new Listcell();
		lc.appendChild(intBox);

		listitem.appendChild(lc);
	}

	private void appendEMIAmount(Listitem listitem, ChequeDetail cd, boolean isReadOnly) {
		CurrencyBox emiAmount = new CurrencyBox();
		emiAmount.setProperties(false, ccyEditField);
		readOnlyComponent(true, emiAmount);
		emiAmount.setTextBoxWidth(100);

		if (!InstrumentType.isSPDC(cd.getChequeType())) {
			Date chequeDate = getChequeDate(listitem);

			BigDecimal totalChequeAmt = PennantApplicationUtil.unFormateAmount(this.totAmount.getActualValue(),
					ccyEditField);

			BigDecimal schdAmount = getEmiAmount(chequeDate);

			this.totAmount.setValue(PennantApplicationUtil.formateAmount(totalChequeAmt.add(schdAmount), ccyEditField));
			if (schdAmount.compareTo(BigDecimal.ZERO) > 0) {
				emiAmount.setValue(PennantApplicationUtil.formateAmount(schdAmount, ccyEditField));
			} else {
				emiAmount.setValue(PennantApplicationUtil.formateAmount(cd.getAmount(), ccyEditField));
			}

			readOnlyComponent(isReadOnly || enqiryModule, emiAmount);
			emiAmount.addForward(Events.ON_FULFILL, this.window, "onFulfillEMIAmount");
		}

		Listcell lc = new Listcell();
		lc.appendChild(emiAmount);

		listitem.appendChild(lc);
	}

	private Date getChequeDate(Listitem listitem) {
		Listcell listcell = (Listcell) listitem.getChildren().get(Field.DUE_DATE.index());

		Combobox combobox = (Combobox) listcell.getFirstChild();

		String combovalue = combobox.getSelectedItem().getLabel();
		if (Labels.getLabel(COMBO_SELECT).equals(combovalue)) {
			return null;
		}

		return DateUtil.parseShortDate(combovalue);
	}

	private void appendChequeStatus(Listitem listitem, ChequeDetail cd) {
		Combobox combobox = getChequeStatusComboBox(cd.getChequeStatus());
		combobox.setWidth(WIDTH_100PX);

		readOnlyComponent(true, combobox);

		Listcell lc = new Listcell();
		lc.appendChild(combobox);

		listitem.appendChild(lc);
	}

	private void appendUploadButton(Listitem listitem, ChequeDetail cd, boolean isReadOnly) {
		Button button = new Button(Labels.getLabel("ChequeDetailDialog_Upload"));
		readOnlyComponent(isReadOnly, button);

		button.setVisible(!enqiryModule);

		button.addForward(Events.ON_CLICK, this.window, "onClickButtonUpload", cd);

		Listcell lc = new Listcell();
		lc.appendChild(button);

		listitem.appendChild(lc);
	}

	private void appendViewButton(Listitem listitem, ChequeDetail cd, boolean isReadOnly) {
		Button button = new Button(Labels.getLabel("ChequeDetailDialog_view"));

		readOnlyComponent(isReadOnly, button);

		button.setVisible(cd.getDocumentName() != null);
		button.addForward(Events.ON_CLICK, this.window, "onClickButtonView", cd);

		Listcell lc = new Listcell();
		lc.appendChild(button);

		listitem.appendChild(lc);
	}

	private void chequeTabDisplay(List<ChequeDetail> cheques) {
		try {
			financeMainDialogCtrl.setChequeDetailDialogCtrl(this);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		if (parenttab != null) {
			FinScheduleData schdData = financeDetail.getFinScheduleData();
			FinanceMain fm = schdData.getFinanceMain();

			if (schdData.getFinanceType().isChequeCaptureReq()) {
				checkTabDisplay(financeDetail, fm.getFinRepayMethod(), false);
			} else if (CollectionUtils.isNotEmpty(cheques)) {
				checkTabDisplay(financeDetail, fm.getFinRepayMethod(), true);
			}
		}
	}

	private List<WrongValueException> componentValues() {
		List<WrongValueException> wve = new ArrayList<>();

		try {
			this.chequeType.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.accountType.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.accHolderName.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.bankBranchID.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.accNumber.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.chequeSerialNo.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.noOfCheques.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.amount.getActualValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		return wve;
	}

	private String loadWrkFlow(ChequeHeader ch) {
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(ch.getRecordType())) {
				ch.setVersion(ch.getVersion() + 1);
				if (ch.isNewRecord()) {
					ch.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					ch.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					ch.setNewRecord(true);
				}
			}
		} else {
			ch.setVersion(ch.getVersion() + 1);
			if (ch.isNewRecord()) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		return tranType;
	}

	private int getEmiNumber(int emiNum) {
		while (true) {
			String dueDate = getCombobox(String.valueOf(++emiNum)).getValue();
			if (Labels.getLabel(COMBO_SELECT).equals(dueDate)) {
				return -1;
			}

			Date chequeDate = DateUtil.parseShortDate(dueDate);

			if (!isBPIHoliday(chequeDate) && !isDueDate(chequeDate)) {
				break;
			}
		}

		return emiNum;
	}

	private boolean isDueDate(Date chequeDate) {
		List<Listitem> items = this.listBoxChequeDetail.getItems();

		for (Listitem listitem : items) {
			List<Listcell> list = listitem.getChildren();
			Listcell listcell = list.get(Field.DUE_DATE.index());
			Combobox combobox = (Combobox) listcell.getFirstChild();

			String comboItem = combobox.getSelectedItem().getLabel();

			if (Labels.getLabel(COMBO_SELECT).equals(comboItem)) {
				return false;
			}

			Date chqDate = DateUtil.parseShortDate(comboItem);
			if (chequeDate.compareTo(chqDate) == 0) {
				return true;
			}
		}

		return false;
	}

	private boolean isBPIHoliday(Date chequeDate) {
		for (FinanceScheduleDetail schedule : getFinSchedules()) {
			String bpiHldy = schedule.getBpiOrHoliday();

			if (StringUtils.isNotBlank(bpiHldy) && chequeDate.compareTo(schedule.getSchDate()) == 0) {
				return true;
			}
		}

		return false;
	}

	private List<FinanceScheduleDetail> getFinSchedules() {
		FinScheduleData schdData = financeDetail.getFinScheduleData();

		if (CollectionUtils.isNotEmpty(schdData.getFinanceScheduleDetails())) {
			return schdData.getFinanceScheduleDetails();
		}

		return getFinanceSchedules();
	}

	private void doFillBankBranch(ChequeDetail cd) {
		Object bankBranch = this.bankBranchID.getAttribute(BANK_BRANCH_ID);

		if (bankBranch != null) {
			BankBranch branch = (BankBranch) bankBranch;
			cd.setBankBranchID(branch.getBankBranchID());
			cd.setBranchCode(branch.getBranchCode());
			cd.setIfsc(branch.getIFSC());
			cd.setBankName(branch.getBankName());
		}
	}

	private List<ChequeDetail> sortedChequeDetails(List<ChequeDetail> details) {
		return details.stream().sorted((cd1, cd2) -> Long.compare(cd1.geteMIRefNo(), cd2.geteMIRefNo()))
				.collect(Collectors.toList());
	}

	public void onClickCheckBox(ForwardEvent event) {
		logger.info(Literal.ENTERING.concat(event.getName()));

		for (Listitem listitem : listBoxChequeDetail.getItems()) {
			Checkbox cb = (Checkbox) listitem.getChildren().get(0).getChildren().get(0);
			if (!cb.isChecked()) {
				return;
			}
		}

		listHeaderCheckBoxComp.setChecked(true);
		logger.info(Literal.LEAVING.concat(event.getName()));
	}

	public void onClickListHeaderCheckBox(ForwardEvent event) {
		logger.info(Literal.ENTERING.concat(event.getName()));

		for (Listitem listitem : listBoxChequeDetail.getItems()) {
			Checkbox cb = (Checkbox) listitem.getChildren().get(Field.CHECK_BOX.index()).getChildren().get(0);
			Combobox chequeSts = (Combobox) listitem.getChildren().get(Field.CHEQUE_STATUS.index()).getFirstChild();

			String status = getComboboxValue(chequeSts);
			if (!(ChequeSatus.CANCELLED.equals(status) || ChequeSatus.PRESENT.equals(status))) {
				cb.setChecked(listHeaderCheckBoxComp.isChecked());
			}
		}

		logger.info(Literal.LEAVING.concat(event.getName()));
	}

	public void onClickListSPDCHeaderCheckBox(ForwardEvent event) {
		logger.info(Literal.ENTERING.concat(event.getName()));

		for (Listitem listitem : listBoxSPDCChequeDetail.getItems()) {
			Checkbox cb = (Checkbox) listitem.getChildren().get(Field.CHECK_BOX.index()).getChildren().get(0);
			Combobox chequeSts = (Combobox) listitem.getChildren().get(Field.CHEQUE_STATUS.index()).getFirstChild();

			String status = getComboboxValue(chequeSts);
			if (!(ChequeSatus.CANCELLED.equals(status) || ChequeSatus.PRESENT.equals(status))) {
				cb.setChecked(listSPDCHeaderCheckBoxComp.isChecked());
			}
		}

		logger.info(Literal.LEAVING.concat(event.getName()));
	}

	public void onCheckIncludeCoAppCust(ForwardEvent event) {
		logger.info(Literal.ENTERING.concat(event.getName()));

		this.customer.setReadonly(!this.includeCoAppCust.isChecked());

		logger.info(Literal.LEAVING.concat(event.getName()));
	}

	private int getNoOfCheques() {
		int chequeCount = 0;
		for (Listitem listitem : listBoxChequeDetail.getItems()) {
			ChequeDetail cd = (ChequeDetail) listitem.getAttribute("data");
			if (!ChequeSatus.CANCELLED.equals(cd.getChequeStatus())) {
				chequeCount++;
			}
		}

		return chequeCount;
	}

	private boolean isDeleteVisible() {
		List<ChequeDetail> list = new ArrayList<>();

		for (ChequeDetail cd : chequeDetailList) {
			String status = cd.getChequeStatus();
			if (!(PennantConstants.RCD_STATUS_CANCELLED.equals(status) || ChequeSatus.PRESENT.equals(status))) {
				list.add(cd);
			}
		}

		return !(CollectionUtils.isEmpty(list) || this.btnGen.isDisabled() || enqiryModule
				|| this.deleteCheques.isDisabled());
	}

	private void validateCheckBox() {
		if (!listBoxChequeDetail.getItems().isEmpty()) {
			listHeaderCheckBoxComp.setDisabled(!isDeleteVisible());
		} else {
			listHeaderCheckBoxComp.setDisabled(true);
		}

		if (!listBoxSPDCChequeDetail.getItems().isEmpty()) {
			listSPDCHeaderCheckBoxComp.setDisabled(!isDeleteVisible());
		} else {
			listSPDCHeaderCheckBoxComp.setDisabled(true);
		}
	}

	public void onFulfill$micr(Event event) {

		logger.debug(Literal.ENTERING.concat(event.getName()));

		Object dataObject = this.micr.getObject();
		if (dataObject == null || dataObject instanceof String) {
			this.accHolderName.setConstraint("");
			this.city.setValue("");
			this.micr.setValue("");
			this.ifsc.setValue("");
			this.cityName.setValue("");
			this.accHolderName.setValue("");
			this.accNumber.setValue("");
			this.bankBranchID.setValue("");
			this.bankBranchID.setDescription("");
		} else {
			BankBranch details = (BankBranch) dataObject;

			this.bankBranchID.setAttribute(BANK_BRANCH_ID, details);
			this.micr.setValue(details.getMICR());
			this.ifsc.setValue(details.getIFSC());
			this.city.setValue(details.getCity());
			this.cityName.setValue(details.getPCCityName());
			this.bankBranchID.setValue(details.getBranchCode());
			this.bankBranchID.setDescription(details.getBankName());
			if (StringUtils.isNotBlank(details.getBankName())) {
				this.bankDetail = bankDetailService.getAccNoLengthByCode(details.getBankCode());
			}

			if (bankDetail != null) {
				this.accNumber.setMaxlength(this.bankDetail.getAccNoLength());
			}
		}

		logger.debug(Literal.LEAVING.concat(event.getName()));

	}

	private boolean chequeDuplicate(ChequeDetail cd, String emiRefNum) {
		return (!PennantConstants.RCD_STATUS_CANCELLED.equals(cd.getRecordStatus())) && StringUtils.isNumeric(emiRefNum)
				&& (Integer.parseInt(emiRefNum) == cd.geteMIRefNo()) && (InstrumentType.isPDC(cd.getChequeType())
						&& !PennantConstants.RCD_STATUS_CANCELLED.equals(chequeStatus.getValue()));
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public void setChequeHeader(ChequeHeader chequeHeader) {
		this.chequeHeader = chequeHeader;
	}

	public void setChequeDetailList(List<ChequeDetail> chequeDetailList) {
		this.chequeDetailList = chequeDetailList;
	}

	public List<ChequeDetail> getChequeDocuments() {
		return chequeDocuments;
	}

	public void setChequeDocuments(List<ChequeDetail> chequeDocuments) {
		this.chequeDocuments = chequeDocuments;
	}

	public List<FinanceScheduleDetail> getFinanceSchedules() {
		return financeSchedules;
	}

	public void setFinanceSchedules(List<FinanceScheduleDetail> financeSchedules) {
		this.financeSchedules = financeSchedules;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public void setUpdatedFinanceSchedules(List<FinanceScheduleDetail> financeSchedules) {
		this.financeSchedules = financeSchedules;
		setUpdatedSchdules();
	}

	@Autowired
	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}

	@Autowired
	public void setChequeHeaderService(ChequeHeaderService chequeHeaderService) {
		this.chequeHeaderService = chequeHeaderService;
	}

	@Autowired
	public void setPennyDropService(PennyDropService pennyDropService) {
		this.pennyDropService = pennyDropService;
	}

	@Autowired(required = false)
	@Qualifier(value = "bankAccountValidationService")
	public void setBankAccountValidationService(BankAccountValidationService bankAccountValidationService) {
		this.bankAccountValidationService = bankAccountValidationService;
	}
}
