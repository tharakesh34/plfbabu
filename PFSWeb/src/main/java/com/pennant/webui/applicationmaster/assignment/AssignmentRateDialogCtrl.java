package com.pennant.webui.applicationmaster.assignment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.applicationmaster.AssignmentRate;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class AssignmentRateDialogCtrl extends GFCBaseCtrl<AssignmentRate> {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(AssignmentRateDialogCtrl.class);

	protected Window window_AssignmentRateDialog;
	protected Textbox assignmentId;
	protected Textbox description;
	protected Textbox dealCode;
	protected Textbox loanType;
	protected Datebox effectiveDate;
	protected Decimalbox mclrRate;
	protected Decimalbox bankSpreadRate;
	protected Space space_opexRate;
	protected Decimalbox opexRate;
	protected Textbox resetFrequency;

	private AssignmentRate assignmentRate;
	private boolean newRecord = false;
	private boolean newAssignmentRate = false;
	private String userRole;
	private boolean newAssignment = false;
	private List<AssignmentRate> assignmentRateDetailList;
	private AssignmentDialogCtrl assignmentDialogCtrl;
	private ArrayList<Object> assignmentHeaderList = new ArrayList<>();
	private boolean isOpexRateMandatory;

	public AssignmentRateDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		logger.debug(Literal.ENTERING);
		super.pageRightName = "AssignmentRateDialog";
		logger.debug(Literal.LEAVING);
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	@SuppressWarnings("unchecked")
	public void onCreate$window_AssignmentRateDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_AssignmentRateDialog);

		try {

			if (arguments.containsKey("enqiryModule")) {
				enqiryModule = (Boolean) arguments.get("enqiryModule");
			} else {
				enqiryModule = false;
			}

			if (arguments.containsKey("assignmentRate")) {
				this.assignmentRate = (AssignmentRate) arguments.get("assignmentRate");
				AssignmentRate befImage = new AssignmentRate();
				BeanUtils.copyProperties(this.assignmentRate, befImage);
				this.assignmentRate.setBefImage(befImage);
				setAssignmentRate(this.assignmentRate);
			} else {
				setAssignmentRate(null);
			}
			if (getAssignmentRate().isNewRecord()) {
				setNewRecord(true);
			}

			if (arguments.containsKey("assignmentHeaderList")) {
				assignmentHeaderList = (ArrayList<Object>) arguments.get("assignmentHeaderList");
			}

			if (arguments.containsKey("assignmentDialogCtrl")) {
				setAssignmentDialogCtrl((AssignmentDialogCtrl) arguments.get("assignmentDialogCtrl"));
				setNewAssignment(true);

				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				this.assignmentRate.setWorkflowId(0);

				if (arguments.containsKey("roleCode") && !enqiryModule) {
					userRole = arguments.get("roleCode").toString();
					getUserWorkspace().allocateRoleAuthorities(userRole, this.pageRightName);
				}
			}

			if (arguments.containsKey("isOpexRateMandatory")) {
				this.isOpexRateMandatory = (boolean) arguments.get("isOpexRateMandatory");
			}

			doLoadWorkFlow(this.assignmentRate.isWorkflow(), this.assignmentRate.getWorkflowId(),
					this.assignmentRate.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			// set Field Properties
			doSetFieldProperties();
			doCheckRights();
			doShowDialog(getAssignmentRate());

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void doShowDialog(AssignmentRate assignmentRate) {

		logger.debug(Literal.ENTERING);

		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.effectiveDate.focus();
		} else {
			this.mclrRate.focus();
			if (isNewAssignment()) {
				doEdit();
			} else if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(assignmentRate);

			if (isNewAssignment()) {
				this.window_AssignmentRateDialog.setHeight("80%");
				this.window_AssignmentRateDialog.setWidth("70%");
				this.groupboxWf.setVisible(false);
				this.window_AssignmentRateDialog.doModal();
			} else {
				this.window_AssignmentRateDialog.setWidth("100%");
				this.window_AssignmentRateDialog.setHeight("100%");
				this.groupboxWf.setVisible(false);
				setDialog(DialogType.EMBEDDED);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);

	}

	private void doWriteBeanToComponents(AssignmentRate assignmentRate) {
		logger.debug(Literal.ENTERING);

		if (!assignmentHeaderList.isEmpty()) {
			this.assignmentId.setValue(String.valueOf(assignmentHeaderList.get(0)));
			this.description.setValue(String.valueOf(assignmentHeaderList.get(1)));
			this.dealCode.setValue(String.valueOf(assignmentHeaderList.get(2)));
			this.loanType.setValue(String.valueOf(assignmentHeaderList.get(3)));
		}
		this.effectiveDate.setValue(assignmentRate.getEffectiveDate());
		this.mclrRate.setValue(assignmentRate.getMclrRate());
		this.bankSpreadRate.setValue(assignmentRate.getBankSpreadRate());
		this.opexRate.setValue(assignmentRate.getOpexRate());
		this.resetFrequency.setValue(assignmentRate.getResetFrequency());

		this.recordStatus.setValue(assignmentRate.getRecordStatus());
		visibleComponents();
		logger.debug(Literal.LEAVING);
	}

	private void visibleComponents() {
		logger.debug(Literal.ENTERING);
		if (isOpexRateMandatory) {
			this.space_opexRate.setVisible(true);
		}
		logger.debug(Literal.LEAVING);
	}

	private void doReadOnly() {

		logger.debug(Literal.ENTERING);

		this.effectiveDate.setReadonly(true);
		this.mclrRate.setReadonly(true);
		this.bankSpreadRate.setReadonly(true);
		this.opexRate.setReadonly(true);
		this.resetFrequency.setReadonly(true);
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

	private void doEdit() {
		logger.debug(Literal.ENTERING);
		if (isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.effectiveDate.setDisabled(true);
			this.btnCancel.setVisible(true);
		}
		this.mclrRate.setReadonly(getUserWorkspace().isReadOnly("AssignmentRateDialog_MCLRRate"));
		this.bankSpreadRate.setReadonly(getUserWorkspace().isReadOnly("AssignmentRateDialog_BankSpreadRate"));
		this.opexRate.setReadonly(getUserWorkspace().isReadOnly("AssignmentRateDialog_OpexRate"));
		this.resetFrequency.setReadonly(getUserWorkspace().isReadOnly("AssignmentRateDialog_ResetFrequency"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.assignmentRate.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (newAssignment) {
				if (enqiryModule) {
					this.btnCtrl.setBtnStatus_New();
					this.btnSave.setVisible(false);
					btnCancel.setVisible(false);
				} else if (isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(newAssignment);
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AssignmentRateDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AssignmentRateDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_AssignmentRateDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AssignmentRateDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		this.effectiveDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.mclrRate.setMaxlength(13);
		this.mclrRate.setFormat(PennantConstants.rateFormate9);
		this.mclrRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.mclrRate.setScale(9);
		this.bankSpreadRate.setMaxlength(13);
		this.bankSpreadRate.setFormat(PennantConstants.rateFormate9);
		this.bankSpreadRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.bankSpreadRate.setScale(9);
		this.opexRate.setMaxlength(13);
		this.opexRate.setFormat(PennantConstants.rateFormate9);
		this.opexRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.opexRate.setScale(9);
		this.resetFrequency.setMaxlength(50);
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);
	}

	public void doSave() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final AssignmentRate aAssignmentRate = new AssignmentRate();
		BeanUtils.copyProperties(getAssignmentRate(), aAssignmentRate);
		boolean isNew = false;

		if (!PennantConstants.RECORD_TYPE_DEL.equals(aAssignmentRate.getRecordType()) && isValidation()) {
			doSetValidation();
			doWriteComponentsToBean(aAssignmentRate);
		}

		isNew = aAssignmentRate.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aAssignmentRate.getRecordType()).equals("")) {
				aAssignmentRate.setVersion(aAssignmentRate.getVersion() + 1);
				if (isNew) {
					aAssignmentRate.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aAssignmentRate.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aAssignmentRate.setNewRecord(true);
				}
			}
		} else {

			if (isNewAssignment()) {
				if (isNewRecord()) {
					aAssignmentRate.setVersion(1);
					aAssignmentRate.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isBlank(aAssignmentRate.getRecordType())) {
					aAssignmentRate.setVersion(aAssignmentRate.getVersion() + 1);
					aAssignmentRate.setRecordType(PennantConstants.RCD_UPD);
				}

				if (aAssignmentRate.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aAssignmentRate.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}

			} else {
				aAssignmentRate.setVersion(aAssignmentRate.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {
			if (isNewAssignment()) {
				AuditHeader auditHeader = newAssignmentRateProcess(aAssignmentRate, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_AssignmentRateDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getAssignmentDialogCtrl().doFillAssignmentRateDetailsList(this.assignmentRateDetailList);
					closeDialog();
				}
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	protected boolean doCustomDelete(final AssignmentRate aAssignmentRate, String tranType) {
		if (isNewAssignment()) {
			tranType = PennantConstants.TRAN_DEL;
			AuditHeader auditHeader = newAssignmentRateProcess(aAssignmentRate, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_AssignmentRateDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				if (getAssignmentDialogCtrl() != null) {
					getAssignmentDialogCtrl().doFillAssignmentRateDetailsList(this.assignmentRateDetailList);
				}
				return true;
			}
		}

		return false;
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final AssignmentRate aAssignmentRate = new AssignmentRate();
		BeanUtils.copyProperties(getAssignmentRate(), aAssignmentRate);

		String keyReference = Labels.getLabel("label_AssignmentRateDialog_EffectiveDate.value") + " : "
				+ aAssignmentRate.getEffectiveDate();

		doDelete(keyReference, aAssignmentRate);

		logger.debug(Literal.LEAVING);
	}

	private void doWriteComponentsToBean(AssignmentRate aAssignmentRate) {
		logger.debug(Literal.ENTERING);
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			aAssignmentRate.setEffectiveDate(this.effectiveDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// MCLR Rate
		try {
			aAssignmentRate.setMclrRate(this.mclrRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Bank Spread Rate
		try {
			aAssignmentRate.setBankSpreadRate(this.bankSpreadRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Opex Rate
		try {
			aAssignmentRate.setOpexRate(BigDecimal.valueOf(this.opexRate.doubleValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAssignmentRate.setResetFrequency(this.resetFrequency.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doRemoveLOVValidation() {
		// TODO Auto-generated method stub

	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		this.effectiveDate.setConstraint("");
		this.mclrRate.setConstraint("");
		this.bankSpreadRate.setConstraint("");
		this.opexRate.setConstraint("");
		this.resetFrequency.setConstraint("");
		logger.debug(Literal.LEAVING);
	}

	private void doSetValidation() {

		if (!this.effectiveDate.isReadonly()) {
			this.effectiveDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_AssignmentRateDialog_EffectiveDate.value"), true));
		}

		if (!this.mclrRate.isReadonly()) {
			this.mclrRate.setConstraint(new PTDecimalValidator(Labels.getLabel("label_AssignmentDialog_MCLRRate.value"),
					9, true, false, 0, 999));
		}

		if (!this.bankSpreadRate.isReadonly()) {
			this.bankSpreadRate.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_AssignmentRateDialog_BankSpreadRate.value"), 9, true, false, 0, 999));
		}

		if (!this.opexRate.isReadonly()) {
			this.opexRate
					.setConstraint(new PTDecimalValidator(Labels.getLabel("label_AssignmentRateDialog_OpexRate.value"),
							9, isOpexRateMandatory, false, 0, 999));
		}

		if (!this.resetFrequency.isReadonly()) {
			this.resetFrequency
					.setConstraint(new PTStringValidator(Labels.getLabel("label_AssignmentDialog_ResetFrequency.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}

	}

	private AuditHeader newAssignmentRateProcess(AssignmentRate aAssignmentRate, String tranType) {
		logger.debug(Literal.ENTERING);

		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aAssignmentRate, tranType);
		assignmentRateDetailList = new ArrayList<AssignmentRate>();

		String[] valueParm = new String[3];
		String[] errParm = new String[3];

		valueParm[0] = DateUtil.formatToLongDate(aAssignmentRate.getEffectiveDate()).toString();

		errParm[0] = PennantJavaUtil.getLabel("label_AssignmentRateDialog_EffectiveDate.value") + " : " + valueParm[0];
		List<AssignmentRate> assignmentRateList = getAssignmentDialogCtrl().getAssignmentRateDetailList();

		if (assignmentRateList != null && !assignmentRateList.isEmpty()) {
			for (AssignmentRate assignmentrate : assignmentRateList) {
				if (aAssignmentRate.getEffectiveDate().compareTo(assignmentrate.getEffectiveDate()) <= 0) {

					if (isNewRecord()) {
						errParm[1] = "/equal to "
								.concat(DateUtil.formatToLongDate(assignmentrate.getEffectiveDate()).toString());
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "30507", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {

						if (aAssignmentRate.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aAssignmentRate.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							assignmentRateDetailList.add(aAssignmentRate);

						} else if (aAssignmentRate.getRecordType().equals(PennantConstants.RCD_ADD)) {

							recordAdded = true;

						} else if (aAssignmentRate.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {

							aAssignmentRate.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							assignmentRateDetailList.add(aAssignmentRate);

						} else if (aAssignmentRate.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;

						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							assignmentRateDetailList.add(assignmentrate);
						}
					}
				} else {
					assignmentRateDetailList.add(assignmentrate);
				}
			}
		}

		if (!recordAdded) {
			assignmentRateDetailList.add(aAssignmentRate);
		}

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader getAuditHeader(AssignmentRate aAssignmentRate, String tranType) {
		logger.debug(Literal.ENTERING);
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aAssignmentRate.getBefImage(), aAssignmentRate);
		logger.debug(Literal.LEAVING);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aAssignmentRate.getUserDetails(),
				getOverideMap());

	}

	@Override
	protected String getReference() {
		return String.valueOf(getAssignmentRate().getId());

	}

	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	public AssignmentRate getAssignmentRate() {
		return assignmentRate;
	}

	public void setAssignmentRate(AssignmentRate assignmentRate) {
		this.assignmentRate = assignmentRate;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewAssignmentRate() {
		return newAssignmentRate;
	}

	public void setNewAssignmentRate(boolean newAssignmentRate) {
		this.newAssignmentRate = newAssignmentRate;
	}

	public AssignmentDialogCtrl getAssignmentDialogCtrl() {
		return assignmentDialogCtrl;
	}

	public void setAssignmentDialogCtrl(AssignmentDialogCtrl assignmentDialogCtrl) {
		this.assignmentDialogCtrl = assignmentDialogCtrl;
	}

	public boolean isNewAssignment() {
		return newAssignment;
	}

	public void setNewAssignment(boolean newAssignment) {
		this.newAssignment = newAssignment;
	}

}
