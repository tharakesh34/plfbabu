package com.pennant.pff.generate.letter.webui;

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.PennantConstants;
import com.pennant.pff.generate.letter.model.GenerateLetter;
import com.pennant.pff.generate.letter.service.GenerateLetterService;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

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
	protected Textbox letterType;
	protected Textbox closureReason;

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

	private GenerateLetter generateLetter;
	private transient GenerateLetterService generateLetterService;
	private transient GenerateLetterListCtrl generateLetterListCtrl;

	public GenerateLetterDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "GenerateLetterDialog";
	}

	public void onCreate$windowGenerateLetterDialog(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		setPageComponents(windowGenerateLetterDialog);

		try {

			this.generateLetter = (GenerateLetter) arguments.get("generateLetter");

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
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.generateLetter);

		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

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
				}
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	private void doSetValidation() {
	}

	private void doWriteComponentsToBean(GenerateLetter gl) {

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
					ah = generateLetterService.saveOrUpdate(ah);
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

	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		final GenerateLetter gl = new GenerateLetter();
		BeanUtils.copyProperties(this.generateLetter, gl);
		doDelete(String.valueOf(gl.getId()), gl);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		doClose(this.btnSave.isVisible());

		logger.debug(Literal.LEAVING.concat(event.toString()));
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
		logger.debug(Literal.ENTERING.concat(event.toString()));

		MessageUtil.showHelpWindow(event, windowGenerateLetterDialog);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		doEdit();

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	private void doShowDialog(GenerateLetter csb) {
		logger.debug(Literal.LEAVING);

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

		FinanceMain fm = gl.getFinanceMain();

		this.finReference.setValue(fm.getFinReference());
		this.custCIF.setValue(fm.getCustCIF());
		this.custName.setValue(fm.getCustAcctHolderName());
		this.finType.setValue(fm.getFinType());
		this.finStatus.setValue(fm.getFinStatus());
		this.finStatusReason.setValue(fm.getFinStsReason());

		this.coreBankID.setValue(fm.getCoreBankId());
		this.finStartDate.setValue(fm.getFinStartDate());
		this.branch.setValue(fm.getFinBranch());
		this.finAmount.setValue(fm.getFinAmount());
		this.finClosureDate.setValue(fm.getClosedDate());
		this.sourcingOfcr.setValue(fm.getSourcingBranch());
		this.letterType.setValue(gl.getLetterType());
		this.closureType.setValue(fm.getClosingStatus());
		this.closureReason.setValue(fm.getClosingStatus());

		this.recordStatus.setValue(gl.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.finReference.setConstraint("");
		this.custCIF.setConstraint("");
		this.custName.setConstraint("");
		this.finType.setConstraint("");
		this.finStatus.setConstraint("");
		this.finStatusReason.setConstraint("");
		this.coreBankID.setConstraint("");
		this.finStartDate.setConstraint("");
		this.branch.setConstraint("");
		this.finAmount.setConstraint("");
		this.finClosureDate.setConstraint("");
		this.sourcingOfcr.setConstraint("");
		this.letterType.setConstraint("");
		this.closureType.setConstraint("");
		this.closureReason.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerServiceBranch_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerServiceBranchDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerServiceBranchDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerServiceBranchDialog_btnSave"));

		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.finReference.setMaxlength(6);
		this.finStartDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.finClosureDate.setFormat(DateFormat.SHORT_DATE.getPattern());

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
}
