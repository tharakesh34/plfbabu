package com.pennant.pff.noc.webui;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.letter.LoanLetter;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.NOCConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.pff.letter.LetterMode;
import com.pennant.pff.letter.LetterUtil;
import com.pennant.pff.letter.service.LetterService;
import com.pennant.pff.noc.dao.GenerateLetterDAO;
import com.pennant.pff.noc.model.GenerateLetter;
import com.pennant.pff.noc.service.GenerateLetterService;
import com.pennant.util.ErrorControl;
import com.pennant.webui.finance.financemain.FinFeeDetailListCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.receipt.constants.Allocation;

public class GenerateLetterDialogCtrl extends GFCBaseCtrl<GenerateLetter> {
	private static final long serialVersionUID = 3293101778075270047L;
	private static final Logger logger = LogManager.getLogger(GenerateLetterDialogCtrl.class);

	protected Window windowGenerateLetterDialog;
	protected Textbox finReference;
	protected Textbox custCIF;
	protected Textbox custName;
	protected Textbox finType;
	protected Textbox finStatus;
	protected Textbox finStatusReason;
	protected Textbox coreBankID;
	protected Datebox finStartDate;
	protected ExtendedCombobox branch;
	protected CurrencyBox finAmount;
	protected Datebox finClosureDate;
	protected Textbox sourcingOfcr;
	protected Textbox closureType;
	protected Combobox letterType;
	protected Textbox closureReason;
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected Tab letterLogDetailTab;
	protected Button btnDownload;

	protected Label totalPriSchd;
	protected Label priPaid;
	protected Label priWaived;
	protected Label totalProfitSchd;
	protected Label profitPaid;
	protected Label profitWaived;
	protected Label totalLPP;
	protected Label lPPPaid;
	protected Label lPPWaived;
	protected Label totalLPI;
	protected Label lPIPaid;
	protected Label lPIWaived;
	protected Label totalBounces;
	protected Label bouncesPaid;
	protected Label bouncesWaived;
	protected Label totalOtherFee;
	protected Label feePaid;
	protected Label feeWaived;
	protected Label generateLetterDialogClosureDate;
	protected Listbox listBoxPaybles;
	protected String selectMethodName = "onSelectTab";

	private GenerateLetter generateLetter;
	private transient GenerateLetterListCtrl generateLetterListCtrl;
	private transient FinFeeDetailListCtrl finFeeDetailListCtrl;
	private FinanceDetail financeDetail;
	private int ccyFormatter = 0;

	private transient FinReceiptHeaderDAO finReceiptHeaderDAO;
	private transient GenerateLetterService generateLetterService;
	private transient LetterService letterService;
	private transient GenerateLetterDAO generateLetterDAO;

	public GenerateLetterDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "GenerateLetterDialog";
	}

	public void onCreate$windowGenerateLetterDialog(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(windowGenerateLetterDialog);

		try {

			this.generateLetter = (GenerateLetter) arguments.get("generateLetter");
			this.moduleCode = (String) arguments.get("moduleCode");
			if (this.generateLetter == null) {
				throw new AppException(Labels.getLabel("error.unhandled"));
			}

			this.generateLetterListCtrl = (GenerateLetterListCtrl) arguments.get("generateLetterListCtrl");

			GenerateLetter custSerBranch = new GenerateLetter();
			BeanUtils.copyProperties(this.generateLetter, custSerBranch);

			this.generateLetter.setBefImage(custSerBranch);

			doLoadWorkFlow(this.generateLetter.isWorkflow(), this.generateLetter.getWorkflowId(),
					this.generateLetter.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				String roleCode = getRole();

				if (PennantConstants.RCD_STATUS_APPROVED.equals(generateLetter.getRecordStatus())
						&& StringUtils.isBlank(generateLetter.getNextRoleCode())) {
					roleCode = "GENERATE_LETTER_DOWNLOAD";
					setRole(roleCode);
				}
				getUserWorkspace().allocateRoleAuthorities(roleCode, this.pageRightName);
			}

			doSetFieldProperties();
			doShowDialog(this.generateLetter);

		} catch (Exception e) {
			closeDialog();
			deAllocateAuthorities("FinFeeDetailListCtrl");
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);

		GenerateLetter gl = new GenerateLetter();
		BeanUtils.copyProperties(this.generateLetter, gl);

		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(gl);

		isNew = gl.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;

			if (StringUtils.isBlank(gl.getRecordType())) {
				gl.setVersion(gl.getVersion() + 1);

				if (isNew) {
					gl.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					gl.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					gl.setNewRecord(true);
				}
			}
		} else {
			gl.setVersion(gl.getVersion() + 1);

			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(gl, tranType)) {
				refreshList();
				closeDialog();
				deAllocateAuthorities("FinFeeDetailListCtrl");
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void doSetValidation() {
	}

	private void doWriteComponentsToBean(GenerateLetter geneLtr) {
		List<FinFeeDetail> fees = geneLtr.getFinanceDetail().getFinScheduleData().getFinFeeDetailList();
		Date appDate = SysParamUtil.getAppDate();

		if (CollectionUtils.isNotEmpty(fees)) {
			FinFeeDetail fee = fees.get(0);
			geneLtr.setWaiverAmt(fee.getWaivedAmount());
			geneLtr.setActualAmt(fee.getActualAmount());
		}

		geneLtr.setRequestType(LetterMode.OTC.name());
		geneLtr.setCreatedOn(DateUtil.getSysDate());
		geneLtr.setCreatedDate(appDate);
		geneLtr.setGeneratedBy(getUserWorkspace().getUserId());
		geneLtr.setFinanceDetail(getFinanceDetail());
	}

	protected boolean doProcess(GenerateLetter gl, String tranType) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		gl.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		gl.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		gl.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			gl.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {

				nextTaskId = StringUtils.trimToEmpty(gl.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, gl);
				}

				if (isNotesMandatory(taskId, gl) && !notesEntered) {
					MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
					return false;
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

			gl.setTaskId(taskId);
			gl.setNextTaskId(nextTaskId);
			gl.setRoleCode(getRole());
			gl.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(gl, tranType);
			String operationRefs = getServiceOperations(taskId, gl);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(gl, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(gl, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader ah, String method) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		GenerateLetter aBounceCode = (GenerateLetter) ah.getAuditDetail().getModelData();
		boolean deleteNotes = false;
		String recordType = aBounceCode.getRecordType();

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (PennantConstants.TRAN_DEL.equals(ah.getAuditTranType())) {
					ah = generateLetterService.delete(ah);
					deleteNotes = true;
				} else {
					if ("GENERATE_LETTER_DOWNLOAD".equals(aBounceCode.getNextRoleCode())) {
						ah = generateLetterService.doApprove(ah);
					} else {
						ah = generateLetterService.saveOrUpdate(ah);
					}
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					ah = generateLetterService.doApprove(ah);

					if (PennantConstants.RECORD_TYPE_DEL.equals(recordType)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					ah = generateLetterService.doReject(ah);

					if (PennantConstants.RECORD_TYPE_NEW.equals(recordType)) {
						deleteNotes = true;
					}
				} else {
					ah.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					return processCompleted;
				}
			}

			ah = ErrorControl.showErrorDetails(this.windowGenerateLetterDialog, ah);
			retValue = ah.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.generateLetter), true);
				}
			}

			if (retValue == PennantConstants.porcessOVERIDE) {
				ah.setOveride(true);
				ah.setErrorMessage(null);
				ah.setInfoMessage(null);
				ah.setOverideMessage(null);
			}

		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);

		doClose(this.btnSave.isVisible());

		deAllocateAuthorities("FinFeeDetailListCtrl");

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);

		doShowNotes(this.generateLetter);

		logger.debug(Literal.LEAVING);
	}

	protected void refreshList() {
		logger.debug(Literal.ENTERING);

		generateLetterListCtrl.fillListData();

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);

		MessageUtil.showHelpWindow(event, windowGenerateLetterDialog);

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);

		doEdit();

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialog(GenerateLetter csb) {
		logger.debug(Literal.LEAVING);

		if (PennantConstants.RCD_STATUS_APPROVED.equals(csb.getRecordStatus())) {
			this.btnDownload.setVisible(true);
			this.btnSave.setVisible(false);
		}

		if (csb.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(csb.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				btnCancel.setVisible(false);
			}
		}

		doWriteBeanToComponents(csb);

		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	private void doWriteBeanToComponents(GenerateLetter gl) {
		logger.debug(Literal.ENTERING);
		this.ccyFormatter = CurrencyUtil
				.getFormat(gl.getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		FinanceMain fm = gl.getFinanceDetail().getFinScheduleData().getFinanceMain();
		Customer customer = gl.getFinanceDetail().getCustomerDetails().getCustomer();

		dofillDetails(gl.getFinanceDetail());

		appendFeeDetailTab(true);

		appendGenerateLetterEnquiryTab(true);

		this.finReference.setValue(fm.getFinReference());
		this.custCIF.setValue(customer.getCustCIF());
		this.custName.setValue(customer.getCustShrtName());
		this.finType.setValue(fm.getFinType());

		String loanClosureType = finReceiptHeaderDAO.getClosureTypeValue(fm.getFinID());

		if (FinanceConstants.CLOSE_STATUS_CANCELLED.equals(fm.getClosingStatus())) {
			this.finStatus.setValue(Labels.getLabel("label_Status_Cancelled"));
			this.finStatusReason.setValue(Labels.getLabel("label_Status_Cancelled"));
			this.closureType.setValue(Labels.getLabel("label_ClosureType_Cancel"));
			this.closureReason.setValue(gl.getReasonCode());
			this.generateLetterDialogClosureDate
					.setValue(Labels.getLabel("label_GenerateLetterDialog_CancellationDate"));
		} else if (FinanceConstants.CLOSE_STATUS_EARLYSETTLE.equals(fm.getClosingStatus())) {
			this.finStatus.setValue(Labels.getLabel("label_Closed"));

			if (loanClosureType != null) {
				this.finStatusReason.setValue(loanClosureType);
				this.closureType.setValue(loanClosureType);
				this.closureReason.setValue(gl.getReasonCode());
			}

		} else if (FinanceConstants.CLOSE_STATUS_MATURED.equals(fm.getClosingStatus())) {
			this.finStatus.setValue(Labels.getLabel("label_Matured"));
			this.finStatusReason.setValue(Labels.getLabel("label_normal"));
			this.closureType.setValue(Labels.getLabel("label_ClosureType_Closure"));
			this.closureReason.setValue(gl.getReasonCode());
		}

		this.coreBankID.setValue(customer.getCustCoreBank());
		this.finStartDate.setValue(fm.getFinStartDate());
		this.branch.setValue(fm.getFinBranch());
		this.finAmount.setValue(CurrencyUtil.parse(fm.getFinAmount(), this.ccyFormatter));
		this.finClosureDate.setValue(fm.getClosedDate());
		this.sourcingOfcr.setValue(String.valueOf(fm.getAccountsOfficer()));
		this.letterType.setValue(gl.getLetterType());
		fillComboBox(this.letterType, gl.getLetterType(), LetterUtil.getLetterTypes(), "");
		this.recordStatus.setValue(gl.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	private void appendFeeDetailTab(boolean isLoadProcess) {
		logger.debug(Literal.ENTERING);

		try {

			if (tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_FEE)) == null) {
				createTab(AssetConstants.UNIQUE_ID_FEE, isLoadProcess);
			} else {
				if (!isLoadProcess) {
					Tab tab = (Tab) tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_FEE));
					tab.setVisible(false);
				}
			}

			if (isLoadProcess) {
				Tabpanel tabPanel = getTabpanel(AssetConstants.UNIQUE_ID_FEE);
				if (tabPanel != null) {
					tabPanel.getChildren().clear();
				}
				Tab tab = (Tab) tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_FEE));
				tab.setVisible(true);

				Map<String, Object> map = getDefaultArguments();
				map.put("parentTab", getTab(AssetConstants.UNIQUE_ID_FEE));
				map.put("moduleDefiner", this.moduleCode);
				map.put("eventCode", NOCConstants.getLetterType(this.generateLetter.getLetterType()));
				map.put("isReceiptsProcess", false);
				map.put("numberOfTermsLabel", Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value"));
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinFeeDetailList.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_FEE), map);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void appendGenerateLetterEnquiryTab(boolean isLoadProcess) {
		logger.debug(Literal.ENTERING);

		try {

			if (tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_LETTERLOG)) == null) {
				createTab(AssetConstants.UNIQUE_ID_LETTERLOG, isLoadProcess);
			} else {
				if (!isLoadProcess) {
					Tab tab = (Tab) tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_LETTERLOG));
					tab.setVisible(false);
				}
			}

			if (isLoadProcess) {

				Tabpanel tabPanel = getTabpanel(AssetConstants.UNIQUE_ID_LETTERLOG);
				if (tabPanel != null) {
					tabPanel.getChildren().clear();
				}
				Tab tab = (Tab) tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_LETTERLOG));
				tab.setVisible(true);

				Map<String, Object> map = getDefaultArguments();
				map.put("parentTab", getTab(AssetConstants.UNIQUE_ID_LETTERLOG));
				map.put("moduleDefiner", this.moduleCode);
				map.put("eventCode", "GENERLTR");
				map.put("numberOfTermsLabel", Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value"));
				Executions.createComponents(getLetterLogPageName(), getTabpanel(AssetConstants.UNIQUE_ID_LETTERLOG),
						map);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public static String getLetterLogPageName() {
		StringBuilder builder = new StringBuilder("/WEB-INF/pages/NOC/LetterLogEnquiryDialog");
		builder.append(".zul");
		return builder.toString();
	}

	public void onClick$btnDownload(Event event) {
		logger.debug(Literal.ENTERING);

		doWriteComponentsToBean(this.generateLetter);

		try {
			LoanLetter letter = letterService.generate(this.generateLetter.getLetterID(), SysParamUtil.getAppDate());

			if ("S".equals(letter.getStatus())) {
				Filedownload.save(new AMedia(letter.getLetterName(), "pdf", "application/pdf", letter.getContent()));
				MessageUtil.showMessage("Letter Downloaded successfully.");
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
			return;
		}

		if ("Submit".equals(userAction.getSelectedItem().getLabel())) {
			generateLetterDAO.delete(this.generateLetter, TableType.MAIN_TAB);
		}

		refreshList();

		closeDialog();

		deAllocateAuthorities("FinFeeDetailListCtrl");

		logger.debug(Literal.LEAVING);
	}

	private void dofillDetails(FinanceDetail findetail) {
		FinanceSummary fmSummary = findetail.getFinScheduleData().getFinanceSummary();

		if (fmSummary == null) {
			return;
		}

		BigDecimal priWaived = BigDecimal.ZERO;
		BigDecimal pftWaived = BigDecimal.ZERO;

		List<ReceiptAllocationDetail> waiver = generateLetterService.getPrinAndPftWaiver(fmSummary.getFinReference());

		for (ReceiptAllocationDetail al : waiver) {
			if (al.getAllocationType().equals(Allocation.PRI) || al.getAllocationType().equals(Allocation.FUT_PRI)) {
				priWaived = priWaived.add(al.getWaivedAmount());
			}
			if (al.getAllocationType().equals(Allocation.PFT) || al.getAllocationType().equals(Allocation.FUT_PFT)) {
				pftWaived = pftWaived.add(al.getWaivedAmount());
			}
		}

		this.totalPriSchd.setValue(
				CurrencyUtil.format(fmSummary.getTotalPriSchd().subtract(fmSummary.getTotalCpz()), ccyFormatter));
		this.priPaid.setValue(CurrencyUtil.format(fmSummary.getSchdPriPaid(), ccyFormatter));
		this.priWaived.setValue(CurrencyUtil.format(priWaived, ccyFormatter));

		this.totalProfitSchd.setValue(CurrencyUtil.format(fmSummary.getTotalProfit(), ccyFormatter));
		this.profitPaid.setValue(CurrencyUtil.format(fmSummary.getSchdPftPaid(), ccyFormatter));
		this.profitWaived.setValue(CurrencyUtil.format(pftWaived, ccyFormatter));

		this.totalLPP.setValue(CurrencyUtil.format(fmSummary.getFinODTotPenaltyAmt(), ccyFormatter));
		this.lPPWaived.setValue(CurrencyUtil.format(fmSummary.getFinODTotWaived(), ccyFormatter));
		this.lPPPaid.setValue(CurrencyUtil.format(fmSummary.getFinODTotPenaltyPaid(), ccyFormatter));

		this.totalLPI.setValue(CurrencyUtil.format(fmSummary.getTotalLPI(), ccyFormatter));
		this.lPIPaid.setValue(CurrencyUtil.format(fmSummary.getLpiPaid(), ccyFormatter));
		this.lPIWaived.setValue(CurrencyUtil.format(fmSummary.getLpiWaived(), ccyFormatter));

		dofillPaybleDetails(fmSummary.getFinID());

		fillManualAdvises(fmSummary.getFinID(), fmSummary);
	}

	private void fillManualAdvises(long finID, FinanceSummary financeSummary) {
		List<ManualAdvise> maList = generateLetterService.getManualAdvises(finID);

		BigDecimal paidAmount = BigDecimal.ZERO;
		BigDecimal adviseAmt = BigDecimal.ZERO;
		BigDecimal waivedAmt = BigDecimal.ZERO;

		BigDecimal bouncPaid = BigDecimal.ZERO;
		BigDecimal bounces = BigDecimal.ZERO;
		BigDecimal bouncWaived = BigDecimal.ZERO;

		for (ManualAdvise ma : maList) {
			if (Allocation.BOUNCE.equals(ma.getFeeTypeCode())) {
				bouncPaid = bouncPaid.add(ma.getPaidAmount());
				bounces = bounces.add(ma.getAdviseAmount());
				bouncWaived = bouncWaived.add(ma.getWaivedAmount());
			} else {
				paidAmount = paidAmount.add(ma.getPaidAmount());
				adviseAmt = adviseAmt.add(ma.getAdviseAmount());
				waivedAmt = waivedAmt.add(ma.getWaivedAmount());
			}
		}

		adviseAmt = adviseAmt.add(financeSummary.getTotalFees());
		paidAmount = paidAmount.add(financeSummary.getTotalPaidFee());
		waivedAmt = waivedAmt.add(financeSummary.getTotalWaiverFee());

		this.totalBounces.setValue(CurrencyUtil.format(bounces, ccyFormatter));
		this.bouncesPaid.setValue(CurrencyUtil.format(bouncPaid, ccyFormatter));
		this.bouncesWaived.setValue(CurrencyUtil.format(bouncWaived, ccyFormatter));

		this.totalOtherFee.setValue(CurrencyUtil.format(adviseAmt, ccyFormatter));
		this.feePaid.setValue(CurrencyUtil.format(paidAmount, ccyFormatter));
		this.feeWaived.setValue(CurrencyUtil.format(waivedAmt, ccyFormatter));
	}

	private void doFillPaybles(BigDecimal amount, BigDecimal inProgressAmt, BigDecimal adjustAmt, BigDecimal balanceAmt,
			String desc) {
		Listitem item = new Listitem();

		Listcell lc;

		lc = new Listcell(desc);
		lc.setSpan(2);
		lc.setStyle("text-align:left");
		lc.setParent(item);

		lc = new Listcell(CurrencyUtil.format(amount, ccyFormatter));
		lc.setStyle("text-align:right");
		lc.setParent(item);

		lc = new Listcell(CurrencyUtil.format(inProgressAmt, ccyFormatter));
		lc.setStyle("text-align:right");
		lc.setParent(item);

		lc = new Listcell(CurrencyUtil.format(adjustAmt, ccyFormatter));
		lc.setStyle("text-align:right");
		lc.setParent(item);

		lc = new Listcell(CurrencyUtil.format(balanceAmt, ccyFormatter));
		lc.setStyle("text-align:right");
		lc.setParent(item);

		this.listBoxPaybles.appendChild(item);
	}

	private void dofillPaybleDetails(long finID) {
		List<FinExcessAmount> excessAvailable = generateLetterService.getExcessAvailable(finID);

		List<ManualAdvise> payables = generateLetterService.getpayableAdvises(finID);

		if (CollectionUtils.isEmpty(excessAvailable) && CollectionUtils.isEmpty(payables)) {
			return;
		}

		BigDecimal excessAmount = BigDecimal.ZERO;
		BigDecimal excessProgressAmt = BigDecimal.ZERO;
		BigDecimal excessAdjustAmt = BigDecimal.ZERO;
		BigDecimal excessBalanceAmt = BigDecimal.ZERO;

		BigDecimal payableAmount = BigDecimal.ZERO;
		BigDecimal payableProgressAmt = BigDecimal.ZERO;
		BigDecimal payableAdjustAmt = BigDecimal.ZERO;
		BigDecimal payableBalanceAmt = BigDecimal.ZERO;

		for (FinExcessAmount e : excessAvailable) {
			excessAmount = excessAmount.add(e.getAmount());
			excessProgressAmt = excessProgressAmt.add(e.getReservedAmt());
			excessAdjustAmt = excessAdjustAmt.add(e.getUtilisedAmt());
			excessBalanceAmt = excessBalanceAmt.add(e.getBalanceAmt());
		}

		for (ManualAdvise fee : payables) {
			payableAmount = payableAmount.add(fee.getAdviseAmount());
			payableProgressAmt = payableProgressAmt.add(fee.getReservedAmt());
			BigDecimal utilizedAmt = fee.getPaidAmount().add(fee.getWaivedAmount());
			payableAdjustAmt = payableAdjustAmt.add(utilizedAmt);
			payableBalanceAmt = payableBalanceAmt
					.add(fee.getAdviseAmount().subtract(utilizedAmt).subtract(fee.getReservedAmt()));
		}

		if (CollectionUtils.isNotEmpty(excessAvailable)) {
			doFillPaybles(excessAmount, excessProgressAmt, excessAdjustAmt, excessBalanceAmt, "Excess");
		}

		if (CollectionUtils.isNotEmpty(payables)) {
			doFillPaybles(payableAmount, payableProgressAmt, payableAdjustAmt, payableBalanceAmt, "Payable");
		}

		Listitem totitem = new Listitem();
		totitem.setStyle("background-color: #C0EBDF;align:bottom;");

		Listcell lcell;

		lcell = new Listcell("TOTALS");
		lcell.setSpan(2);
		lcell.setStyle("text-align:left");
		lcell.setParent(totitem);

		lcell = new Listcell(CurrencyUtil.format(excessAmount.add(payableAmount), ccyFormatter));
		lcell.setStyle("text-align:right");
		lcell.setParent(totitem);

		lcell = new Listcell(CurrencyUtil.format(excessProgressAmt.add(payableProgressAmt), ccyFormatter));
		lcell.setStyle("text-align:right");
		lcell.setParent(totitem);

		lcell = new Listcell(CurrencyUtil.format(excessAdjustAmt.add(payableAdjustAmt), ccyFormatter));
		lcell.setStyle("text-align:right");
		lcell.setParent(totitem);

		lcell = new Listcell(CurrencyUtil.format(excessBalanceAmt.add(payableBalanceAmt), ccyFormatter));
		lcell.setStyle("text-align:right");
		lcell.setParent(totitem);

		this.listBoxPaybles.appendChild(totitem);

	}

	private Map<String, Object> getDefaultArguments() {
		this.financeDetail = this.generateLetter.getFinanceDetail();

		final Map<String, Object> map = new HashMap<>();
		map.put("roleCode", getRole());
		map.put("generateLetter", this.generateLetter);
		map.put("financeMainDialogCtrl", this);
		map.put("finHeaderList", getFinBasicDetails());
		map.put("financeDetail", getFinanceDetail());
		map.put("ccyFormatter", CurrencyUtil
				.getFormat(this.generateLetter.getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy()));

		return map;
	}

	private Object getFinBasicDetails() {
		ArrayList<Object> arrayList = new ArrayList<>();
		FinanceMain financeMain = this.generateLetter.getFinanceDetail().getFinScheduleData().getFinanceMain();

		arrayList.add(0, financeMain.getFinType());
		arrayList.add(1, financeMain.getFinCcy());
		arrayList.add(2, financeMain.getScheduleMethod());
		arrayList.add(3, financeMain.getFinReference());
		arrayList.add(4, financeMain.getProfitDaysBasis());
		arrayList.add(5, financeMain.getGrcPeriodEndDate());
		arrayList.add(6, financeMain.isAllowGrcPeriod());
		FinanceType fianncetype = getFinanceDetail().getFinScheduleData().getFinanceType();

		arrayList.add(7, fianncetype != null && StringUtils.isNotEmpty(fianncetype.getProduct()));

		arrayList.add(8, getFinanceDetail().getFinScheduleData().getFinanceMain().getProductCategory());

		if (getFinanceDetail().getCustomerDetails() != null
				&& getFinanceDetail().getCustomerDetails().getCustomer() != null) {
			arrayList.add(9, getFinanceDetail().getCustomerDetails().getCustomer().getCustShrtName());
		} else {
			arrayList.add(9, "");
		}

		arrayList.add(10, false);
		arrayList.add(11, this.moduleCode);
		return arrayList;
	}

	public void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		int finCcy = CurrencyUtil
				.getFormat(this.generateLetter.getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		this.finStartDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.finClosureDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.finAmount.setProperties(false, finCcy);
		readOnlyComponent(true, this.finStartDate);
		readOnlyComponent(true, this.finClosureDate);
		readOnlyComponent(true, this.letterType);
		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	public void doEdit() {
		logger.debug(Literal.ENTERING);

		this.btnCancel.setVisible(!this.generateLetter.isNewRecord());

		if (isWorkFlowEnabled()) {

			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.generateLetter.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}

		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug(Literal.LEAVING);
	}

	private String getTabID(String id) {
		return "TAB" + StringUtils.trimToEmpty(id);
	}

	public void createTab(String moduleID, boolean tabVisible) {
		logger.debug("Entering");
		String tabName = "";
		if (StringUtils.equals(AssetConstants.UNIQUE_ID_JOINTGUARANTOR, moduleID)) {
			tabName = Labels.getLabel("tab_Co-borrower&Gurantors");
		} else if (StringUtils.equals(AssetConstants.UNIQUE_ID_ADDITIONALFIELDS, moduleID)) {
			tabName = "Fees";
		} else {
			tabName = Labels.getLabel("tab_label_" + moduleID);
		}
		Tab tab = new Tab(tabName);
		tab.setId(getTabID(moduleID));
		tab.setVisible(tabVisible);
		tabsIndexCenter.appendChild(tab);
		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId(getTabpanelID(moduleID));
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setParent(tabpanelsBoxIndexCenter);
		tabpanel.setHeight("100%");
		ComponentsCtrl.applyForward(tab, ("onSelect=" + selectMethodName));
		logger.debug("Leaving");
	}

	private String getTabpanelID(String id) {
		return "TABPANEL" + StringUtils.trimToEmpty(id);
	}

	private Tabpanel getTabpanel(String id) {
		return (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny(getTabpanelID(id));
	}

	private Tab getTab(String id) {
		return (Tab) tabsIndexCenter.getFellowIfAny(getTabID(id));
	}

	private AuditHeader getAuditHeader(GenerateLetter csb, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, csb.getBefImage(), csb);
		return new AuditHeader(String.valueOf(csb.getId()), null, null, null, auditDetail, csb.getUserDetails(),
				getOverideMap());
	}

	@Autowired
	public void setGenerateLetterService(GenerateLetterService generateLetterService) {
		this.generateLetterService = generateLetterService;
	}

	public void setGenerateLetterListCtrl(GenerateLetterListCtrl generateLetterListCtrl) {
		this.generateLetterListCtrl = generateLetterListCtrl;
	}

	public FinFeeDetailListCtrl getFinFeeDetailListCtrl() {
		return finFeeDetailListCtrl;
	}

	public void setFinFeeDetailListCtrl(FinFeeDetailListCtrl finFeeDetailListCtrl) {
		this.finFeeDetailListCtrl = finFeeDetailListCtrl;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	@Autowired
	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	@Autowired
	public void setLetterService(LetterService letterService) {
		this.letterService = letterService;
	}

	@Autowired
	public void setGenerateLetterDAO(GenerateLetterDAO generateLetterDAO) {
		this.generateLetterDAO = generateLetterDAO;
	}

}
