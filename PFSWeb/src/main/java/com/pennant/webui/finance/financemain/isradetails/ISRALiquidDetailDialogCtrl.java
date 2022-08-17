package com.pennant.webui.finance.financemain.isradetails;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.isradetail.ISRALiquidDetail;
import com.pennant.backend.util.PennantApplicationUtil;
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
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class ISRALiquidDetailDialogCtrl extends GFCBaseCtrl<ISRALiquidDetail> {

	private static final long serialVersionUID = 8246101777023830259L;
	private static final Logger logger = LogManager.getLogger(ISRALiquidDetailDialogCtrl.class);

	protected Window window_LiquidDetailDialog;
	protected Textbox name;
	protected CurrencyBox amount;
	protected Datebox expiryDate;

	private ISRALiquidDetail israLiquidDetail;
	private List<ISRALiquidDetail> israLiquidDetails;
	private String moduleType = "";
	private ISRADetailDialogCtrl israDetailDialogCtrl;
	private boolean newRecord = false;
	private String roleCode = "";
	private boolean newIsra = false;
	private int ccyEditField = PennantConstants.defaultCCYDecPos;
	private int index = 0;
	private boolean newButtonVisible = false;
	private Date loanStartDate = null;

	public ISRALiquidDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ISRALiquidDetailDialog";
	}

	public void onCreate$window_LiquidDetailDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_LiquidDetailDialog);

		if (arguments.containsKey("ISRALiquidDetails")) {
			this.israLiquidDetail = (ISRALiquidDetail) arguments.get("ISRALiquidDetails");
			ISRALiquidDetail befImage = new ISRALiquidDetail();
			BeanUtils.copyProperties(this.israLiquidDetail, befImage);
			this.israLiquidDetail.setBefImage(befImage);
			setIsraLiquidDetail(this.israLiquidDetail);
		} else {
			setIsraLiquidDetail(null);
		}

		if (arguments.containsKey("moduleType")) {
			this.moduleType = (String) arguments.get("moduleType");
		}

		if (arguments.containsKey("ccyEditField")) {
			this.ccyEditField = (int) arguments.get("ccyEditField");
		}
		if (arguments.containsKey("index")) {
			this.index = (Integer) arguments.get("index");
		}
		if (arguments.containsKey("newButtonVisible")) {
			this.newButtonVisible = (boolean) arguments.get("newButtonVisible");
		}
		if (arguments.containsKey("loanStartDate")) {
			this.loanStartDate = (Date) arguments.get("loanStartDate");
		}

		if (getIsraLiquidDetail().isNewRecord()) {
			setNewRecord(true);
		}

		if (arguments.containsKey("ISRADetailDialogCtrl")) {

			setIsraDetailDialogCtrl((ISRADetailDialogCtrl) arguments.get("ISRADetailDialogCtrl"));
			setNewIsra(true);

			if (arguments.containsKey("newRecord")) {
				setNewRecord(true);
			} else {
				setNewRecord(false);
			}
			this.israLiquidDetail.setWorkflowId(0);
			if (arguments.containsKey("roleCode") && !enqiryModule) {
				roleCode = arguments.get("roleCode").toString();
				getUserWorkspace().allocateRoleAuthorities(this.roleCode, this.pageRightName);
			}
		}

		doLoadWorkFlow(this.israLiquidDetail.isWorkflow(), this.israLiquidDetail.getWorkflowId(),
				this.israLiquidDetail.getNextTaskId());

		doCheckRights();

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "CustomerAddresDialog");
		}

		doSetFieldProperties();
		doShowDialog(getIsraLiquidDetail());

		logger.debug(Literal.LEAVING + event.toString());
	}

	private void doShowDialog(ISRALiquidDetail israLiquidDetail) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			readOnlyComponent(isReadOnly("ISRALiquidDetailDialog_name"), this.name);
			this.name.focus();
		} else {
			this.amount.focus();
			readOnlyComponent(true, this.name);
			if (isNewIsra()) {
				doEdit();
			}
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(israLiquidDetail.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				doEdit();
				btnCancel.setVisible(false);
			}
		}
		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
		}
		try {
			doWriteBeanToComponents(israLiquidDetail);

			doCheckEnquiry();

			if (!newButtonVisible) {
				this.btnDelete.setVisible(false);
				this.btnSave.setVisible(false);
			}

			this.window_LiquidDetailDialog.setHeight("50%");
			this.window_LiquidDetailDialog.setWidth("70%");
			this.window_LiquidDetailDialog.doModal();

		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.name.setMaxlength(100);
		this.amount.setProperties(true, ccyEditField);
		this.expiryDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		logger.debug(Literal.LEAVING);
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		getUserWorkspace().allocateAuthorities(this.pageRightName, this.roleCode);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_israLiquidDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_israLiquidDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_israLiquidDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_israLiquidDetailDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		doDelete();
	}

	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());

		doSave();

		logger.debug(Literal.LEAVING + event.toString());
	}

	protected void doSave() {
		logger.debug(Literal.ENTERING);

		final ISRALiquidDetail aISRALiquidDetail = new ISRALiquidDetail();
		BeanUtils.copyProperties(getIsraLiquidDetail(), aISRALiquidDetail);
		boolean isNew = false;

		doWriteComponentsToBean(aISRALiquidDetail);

		isNew = aISRALiquidDetail.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aISRALiquidDetail.getRecordType())) {
				aISRALiquidDetail.setVersion(aISRALiquidDetail.getVersion() + 1);
				if (isNew) {
					aISRALiquidDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aISRALiquidDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aISRALiquidDetail.setNewRecord(true);
				}
			}
		} else {

			if (isNewIsra()) {
				if (isNewRecord()) {
					aISRALiquidDetail.setVersion(1);
					aISRALiquidDetail.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isBlank(aISRALiquidDetail.getRecordType())) {
					aISRALiquidDetail.setVersion(aISRALiquidDetail.getVersion() + 1);
					aISRALiquidDetail.setRecordType(PennantConstants.RCD_UPD);
				}

				if (aISRALiquidDetail.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aISRALiquidDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}
			} else {
				aISRALiquidDetail.setVersion(aISRALiquidDetail.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}
		try {
			if (isNewIsra()) {
				AuditHeader auditHeader = newIsraLiquidDetailProcess(aISRALiquidDetail, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_LiquidDetailDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getIsraDetailDialogCtrl().doFillISRALiquidDetails(this.israLiquidDetails);
					closeDialog();
				}
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private AuditHeader newIsraLiquidDetailProcess(ISRALiquidDetail iSRALiquidDetails, String tranType) {

		boolean recordAdded = false;
		boolean duplicateRecord = false;

		AuditHeader auditHeader = getAuditHeader(iSRALiquidDetails, tranType);
		this.israLiquidDetails = new ArrayList<ISRALiquidDetail>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = iSRALiquidDetails.getName();
		valueParm[1] = String.valueOf(iSRALiquidDetails.getAmount());

		errParm[0] = PennantJavaUtil.getLabel("label_ISRALiquidDetailDialog_Name.value") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_ISRALiquidDetailDialog_Amount.value") + ":" + valueParm[1];

		if (getIsraDetailDialogCtrl() != null) {

			List<ISRALiquidDetail> israLiquidDetailList = getIsraDetailDialogCtrl().getIsraLiquidDetailList();

			if (CollectionUtils.isNotEmpty(israLiquidDetailList)) {
				for (int i = 0; i < israLiquidDetailList.size(); i++) {
					ISRALiquidDetail details = israLiquidDetailList.get(i);

					if ((details.getName().equals(iSRALiquidDetails.getName()))) {
						duplicateRecord = true;
					}

					if (duplicateRecord) {
						if (isNewRecord()) {
							auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41008", errParm, valueParm),
									getUserWorkspace().getUserLanguage()));
							return auditHeader;
						}
						if (PennantConstants.TRAN_DEL.equals(tranType)) {
							if (PennantConstants.RECORD_TYPE_UPD.equals(details.getRecordType())) {
								details.setRecordType(PennantConstants.RECORD_TYPE_DEL);
								recordAdded = true;
								this.israLiquidDetails.add(details);
							} else if (PennantConstants.RCD_ADD.equals(details.getRecordType())) {
								recordAdded = true;
							} else if (PennantConstants.RECORD_TYPE_NEW.equals(details.getRecordType())) {
								details.setRecordType(PennantConstants.RECORD_TYPE_CAN);
								recordAdded = true;
								this.israLiquidDetails.add(details);
							} else if (PennantConstants.RECORD_TYPE_CAN.equals(details.getRecordType())) {
								recordAdded = true;
							}
						} else {
							this.israLiquidDetails.add(details);
						}
					} else {
						this.israLiquidDetails.add(details);
					}
					duplicateRecord = false;
				}
			}
		}
		if (PennantConstants.TRAN_UPD.equals(tranType)) {
			this.israLiquidDetails.remove(index);
			this.israLiquidDetails.add(iSRALiquidDetails);
			recordAdded = true;
		}
		if (!recordAdded) {
			this.israLiquidDetails.add(iSRALiquidDetails);
		}
		return auditHeader;
	}

	private void doWriteComponentsToBean(ISRALiquidDetail aISRALiquidDetails) {
		logger.debug(Literal.ENTERING);

		doSetValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aISRALiquidDetails.setName(this.name.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aISRALiquidDetails
					.setAmount(PennantApplicationUtil.unFormateAmount(this.amount.getActualValue(), ccyEditField));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aISRALiquidDetails.setExpiryDate(this.expiryDate.getValue());
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

		setIsraLiquidDetail(aISRALiquidDetails);

		logger.debug(Literal.LEAVING);
	}

	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		this.name.setConstraint(new PTStringValidator(Labels.getLabel("label_ISRALiquidDetailDialog_Name.value"),
				PennantRegularExpressions.REGEX_ALPHANUM_SPACE, true));

		this.amount.setConstraint(new PTDecimalValidator(Labels.getLabel("label_ISRALiquidDetailDialog_Amount.value"),
				ccyEditField, true, true));

		this.expiryDate
				.setConstraint(new PTDateValidator(Labels.getLabel("label_ISRALiquidDetailDialog_ExpiryDate.value"),
						true, this.loanStartDate, null, false));

		logger.debug(Literal.LEAVING);
	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.name.setConstraint("");
		this.amount.setConstraint("");
		this.expiryDate.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	private void doWriteBeanToComponents(ISRALiquidDetail detail) {
		logger.debug(Literal.ENTERING);

		this.name.setValue(detail.getName());
		this.amount.setValue(PennantApplicationUtil.formateAmount(detail.getAmount(), ccyEditField));
		this.expiryDate.setValue(detail.getExpiryDate());

		logger.debug("Leaving ");
	}

	private void doReadOnly() {
		logger.debug(Literal.ENTERING);

		/*
		 * this.name.setReadonly(true); this.amount.setReadonly(true); this.expiryDate.setReadonly(true);
		 */

		logger.debug("Leaving ");
	}

	private void doEdit() {
		logger.debug(Literal.ENTERING);

		readOnlyComponent(getUserWorkspace().isReadOnly("ISRALiquidDetailDialog_amount"), this.amount);
		readOnlyComponent(getUserWorkspace().isReadOnly("ISRALiquidDetailDialog_expiryDate"), this.expiryDate);

		logger.debug(Literal.LEAVING);

	}

	private void doCheckEnquiry() {
		if (PennantConstants.MODULETYPE_ENQ.equals(this.moduleType)) {
			this.btnDelete.setVisible(false);
			this.btnSave.setVisible(false);
			doReadOnly();
		}
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	private AuditHeader getAuditHeader(ISRALiquidDetail aISRALiquidDetails, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aISRALiquidDetails.getBefImage(), aISRALiquidDetails);

		return new AuditHeader(getReference(), String.valueOf(aISRALiquidDetails.getId()), null, null, auditDetail,
				aISRALiquidDetails.getUserDetails(), getOverideMap());
	}

	/**
	 * Deletes a CustomerIncome object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final ISRALiquidDetail israLiquidDetail = new ISRALiquidDetail();
		BeanUtils.copyProperties(getIsraLiquidDetail(), israLiquidDetail);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_ISRALiquidDetailDialog_Name.value") + " : " + israLiquidDetail.getName();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(israLiquidDetail.getRecordType())) {
				israLiquidDetail.setVersion(israLiquidDetail.getVersion() + 1);
				israLiquidDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (getIsraDetailDialogCtrl() != null) {
					// && getIsraDetailDialogCtrl().) {
					israLiquidDetail.setNewRecord(true);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (isNewIsra()) {
					tranType = PennantConstants.TRAN_DEL;
					AuditHeader auditHeader = newIsraLiquidDetailProcess(israLiquidDetail, tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_LiquidDetailDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
						if (getIsraDetailDialogCtrl() != null) {
							getIsraDetailDialogCtrl().doFillISRALiquidDetails(this.israLiquidDetails);
						}
						closeDialog();
					}
				}
			} catch (DataAccessException e) {
				logger.error("Exception: ", e);
				showMessage(e);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void showMessage(Exception e) {
		logger.debug(Literal.ENTERING);
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_LiquidDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug(Literal.LEAVING);
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public boolean isNewIsra() {
		return newIsra;
	}

	public void setNewIsra(boolean newIsra) {
		this.newIsra = newIsra;
	}

	public ISRALiquidDetail getIsraLiquidDetail() {
		return israLiquidDetail;
	}

	public void setIsraLiquidDetail(ISRALiquidDetail israLiquidDetail) {
		this.israLiquidDetail = israLiquidDetail;
	}

	public List<ISRALiquidDetail> getIsraLiquidDetails() {
		return israLiquidDetails;
	}

	public void setIsraLiquidDetails(List<ISRALiquidDetail> israLiquidDetails) {
		this.israLiquidDetails = israLiquidDetails;
	}

	public ISRADetailDialogCtrl getIsraDetailDialogCtrl() {
		return israDetailDialogCtrl;
	}

	public void setIsraDetailDialogCtrl(ISRADetailDialogCtrl israDetailDialogCtrl) {
		this.israDetailDialogCtrl = israDetailDialogCtrl;
	}

}