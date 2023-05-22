package com.pennant.webui.finance.financemain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Window;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinChangeCustomer;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.service.finance.FinChangeCustomerService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.webui.finance.finchangecustomer.FinChangeCustomerListCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class ChangeCustomerDialogCtrl extends GFCBaseCtrl<FinChangeCustomer> {
	private static final long serialVersionUID = -4578996988245614938L;

	private static final Logger logger = LogManager.getLogger(ChangeCustomerDialogCtrl.class);

	protected Window window_ChangeCustomerDialog;
	protected Listheader listheader_CustomerCIF;
	protected Listheader listheader_CustomerName;
	protected Listheader listheader_PANNumber;
	protected Listheader listheader_CustomerBranch;
	protected Listheader listheader_CustomerType;
	protected Listheader listheader_EmploymentType;
	protected Listbox listBoxChangeCustomer;
	protected Borderlayout borderlayoutChangeCustomer;
	protected Listheader listheader_CustomerRadio;
	protected Label finReference;
	protected Label oldCustomerId;
	protected Label coApplicantId;
	protected Label custCategory;

	protected Paging pagingChangeCustomerDialogCtrl;
	protected Button btnExistingCustomer;
	protected Radiogroup radioButtonGroupBtn = new Radiogroup();
	protected List<JointAccountDetail> jointAccountDetailList = null;

	protected FinChangeCustomerListCtrl finChangeCustomerListCtrl;
	protected FinChangeCustomer finChangeCustomer;
	protected FinChangeCustomerService finChangeCustomerService;

	private long coaplicantCustId = 0;
	private JointAccountDetail jointAccountDetail;

	public ChangeCustomerDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinChangeCustomerDialog";
	}

	@Override
	protected String getReference() {
		return this.finChangeCustomer.getFinReference();
	}

	@SuppressWarnings("unchecked")
	public void onCreate$window_ChangeCustomerDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_ChangeCustomerDialog);

		try {
			// Get the required arguments.
			this.finChangeCustomer = (FinChangeCustomer) arguments.get("finChangeCustomer");
			this.finChangeCustomerListCtrl = (FinChangeCustomerListCtrl) arguments.get("finChangeCustomerListCtrl");
			this.jointAccountDetailList = (List<JointAccountDetail>) arguments.get("jointAccountDetails");

			if (this.finChangeCustomer == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			FinChangeCustomer finChangeCustomer = new FinChangeCustomer();
			BeanUtils.copyProperties(this.finChangeCustomer, finChangeCustomer);
			this.finChangeCustomer.setBefImage(finChangeCustomer);

			// Render the page and display the data.
			doLoadWorkFlow(this.finChangeCustomer.isWorkflow(), this.finChangeCustomer.getWorkflowId(),
					this.finChangeCustomer.getNextTaskId());

			coaplicantCustId = finChangeCustomer.getCoApplicantId();

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.finChangeCustomer);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinChangeCustomerDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinChangeCustomerDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinChangeCustomerDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinChangeCustomerDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.setJointAccountDetail
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
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
	public void onClick$btnDelete(Event event) throws InterruptedException {
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
		doShowNotes(this.finChangeCustomer);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		finChangeCustomerListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.finChangeCustomer.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	private void doWriteBeanToComponents(FinChangeCustomer finChangeCustomer) {
		logger.debug(Literal.ENTERING);

		doFilllistbox(getJointAccountDetail());
		this.finReference.setValue(finChangeCustomer.getFinReference());
		this.oldCustomerId.setValue(String.valueOf(finChangeCustomer.getCustCif()));
		this.custCategory.setValue(finChangeCustomer.getCustCategory());
		this.coApplicantId.setValue(finChangeCustomer.getJcustCif());

		logger.debug(Literal.LEAVING);

	}

	public void doFilllistbox(List<JointAccountDetail> jointAccountDetail) {
		logger.debug(Literal.ENTERING);

		if (jointAccountDetail != null) {
			ListModelList<JointAccountDetail> listModelList = new ListModelList<JointAccountDetail>(jointAccountDetail);
			this.listBoxChangeCustomer.setModel(listModelList);
			this.listBoxChangeCustomer.setItemRenderer(new ChangeCustomerListModelItemRenderer());

			logger.debug(Literal.LEAVING);

		}

		logger.debug(Literal.LEAVING);
	}

	private class ChangeCustomerListModelItemRenderer implements ListitemRenderer<JointAccountDetail>, Serializable {
		private static final long serialVersionUID = 1L;

		public ChangeCustomerListModelItemRenderer() {
			super();
		}

		@Override
		public void render(Listitem item, JointAccountDetail changeCustomer, int index) {
			Listcell lc = new Listcell();
			Radio radio = new Radio();
			radio.setValue(changeCustomer.getCustID());
			if (coaplicantCustId != 0 && changeCustomer.getCustID() == coaplicantCustId) {
				radio.setChecked(true);
				jointAccountDetail = changeCustomer;
			}
			radio.addForward("onCheck", self, "onCheck_radioButtonGroupBtn");
			radio.setParent(radioButtonGroupBtn);
			lc.appendChild(radio);
			radio.setAttribute("JointAcctDetail", changeCustomer);
			radio.setAttribute("CustCategory", changeCustomer.getCustomerDetails().getCustomer().getCustCtgCode());
			lc.setParent(item);

			lc = new Listcell(changeCustomer.getCustCIF());
			lc.setParent(item);

			lc = new Listcell(changeCustomer.getLovDescCIFName());
			lc.setParent(item);

			lc = new Listcell(changeCustomer.getCustomerDetails().getCustomer().getCustCRCPR());
			lc.setParent(item);

			lc = new Listcell(changeCustomer.getCustomerDetails().getCustomer().getCustDftBranch());
			lc.setParent(item);

			lc = new Listcell(changeCustomer.getCustomerDetails().getCustomer().getCustTypeCode());
			lc.setParent(item);

			lc = new Listcell(changeCustomer.getCustomerDetails().getCustomer().getCustEmpSts());
			lc.setParent(item);

			item.setAttribute("id", changeCustomer.getCustCIF());

		}
	}

	public void onCheck_radioButtonGroupBtn(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		Radio checkBox = (Radio) event.getOrigin().getTarget();

		for (int i = 1; i < this.listBoxChangeCustomer.getChildren().size(); i++) {

			Listitem item = (Listitem) this.listBoxChangeCustomer.getChildren().get(i);
			Listcell lc = (Listcell) item.getChildren().get(0);

			if (lc.getChildren().size() == 0) {
				continue;
			}
			Radio radio = (Radio) lc.getChildren().get(0);
			if ((!(radio.getUuid().trim().equals(checkBox.getUuid().trim()))) && radio.isChecked()) {
				radio.setChecked(false);
			}
		}
		coaplicantCustId = checkBox.getValue();
		jointAccountDetail = (JointAccountDetail) checkBox.getAttribute("JointAcctDetail");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinChangeCustomer
	 */
	public void doWriteComponentsToBean(FinChangeCustomer aFinChangeCustomer) {
		logger.debug(Literal.ENTERING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Fin Reference
		try {
			if (coaplicantCustId == 0) {
				MessageUtil.showError("Please Select one Customer to Proceed");
				throw new WrongValueException();
			} else {
				aFinChangeCustomer.setCoApplicantId(coaplicantCustId);
				aFinChangeCustomer.setJointAccountDetail(jointAccountDetail);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param finChangeCustomer The entity that need to be render.
	 */
	public void doShowDialog(FinChangeCustomer finChangeCustomer) {
		logger.debug(Literal.ENTERING);

		if (finChangeCustomer.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			// this.finReference.focus();
		} else {
			// this.finReference.setReadonly(true);

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(finChangeCustomer.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				// this.hold.focus();
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		doWriteBeanToComponents(finChangeCustomer);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final FinChangeCustomer aFinChangeCustomer = new FinChangeCustomer();
		BeanUtils.copyProperties(this.finChangeCustomer, aFinChangeCustomer);

		doDelete(aFinChangeCustomer.getFinReference(), aFinChangeCustomer);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.finChangeCustomer.isNewRecord()) {
			this.btnCancel.setVisible(false);
			// readOnlyComponent(true, this.finReference);
		} else {
			this.btnCancel.setVisible(true);
			// readOnlyComponent(true, this.finReference);
			// readOnlyComponent(true, this.finReference);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.finChangeCustomer.isNewRecord()) {
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

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		// readOnlyComponent(true, this.finReference);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);

		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug(Literal.ENTERING);
		// this.finReference.setValue("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final FinChangeCustomer aFinChangeCustomer = new FinChangeCustomer();
		BeanUtils.copyProperties(this.finChangeCustomer, aFinChangeCustomer);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aFinChangeCustomer);
		aFinChangeCustomer.setJointAccountDetail(jointAccountDetail);
		isNew = aFinChangeCustomer.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinChangeCustomer.getRecordType())) {
				aFinChangeCustomer.setVersion(aFinChangeCustomer.getVersion() + 1);
				if (isNew) {
					aFinChangeCustomer.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinChangeCustomer.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinChangeCustomer.setNewRecord(true);
				}
			}
		} else {
			aFinChangeCustomer.setVersion(aFinChangeCustomer.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aFinChangeCustomer, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(FinChangeCustomer aFinChangeCustomer, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aFinChangeCustomer.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFinChangeCustomer.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinChangeCustomer.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aFinChangeCustomer.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFinChangeCustomer.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aFinChangeCustomer);
				}

				if (isNotesMandatory(taskId, aFinChangeCustomer)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}

				}
			}
			if (!StringUtils.isBlank(nextTaskId)) {
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

			aFinChangeCustomer.setTaskId(taskId);
			aFinChangeCustomer.setNextTaskId(nextTaskId);
			aFinChangeCustomer.setRoleCode(getRole());
			aFinChangeCustomer.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aFinChangeCustomer, tranType);
			String operationRefs = getServiceOperations(taskId, aFinChangeCustomer);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aFinChangeCustomer, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aFinChangeCustomer, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader auditHeader
	 * @param method      (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		FinChangeCustomer aFinChangeCustomer = (FinChangeCustomer) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = finChangeCustomerService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = finChangeCustomerService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = finChangeCustomerService.doApprove(auditHeader);

					if (aFinChangeCustomer.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = finChangeCustomerService.doReject(auditHeader);
					if (aFinChangeCustomer.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_ChangeCustomerDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_ChangeCustomerDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.finChangeCustomer), true);
				}
			}

			if (retValue == PennantConstants.porcessOVERIDE) {
				auditHeader.setOveride(true);
				auditHeader.setErrorMessage(null);
				auditHeader.setInfoMessage(null);
				auditHeader.setOverideMessage(null);
				aFinChangeCustomer.setCollateralDelinkStatus(true);

			}
			if (retValue == 2) {
				auditHeader.setOveride(true);
				auditHeader.setErrorMessage(null);
				auditHeader.setInfoMessage(null);
				auditHeader.setOverideMessage(null);
				aFinChangeCustomer.setCollateralDelinkStatus(false);
				List<ErrorDetail> overideMessage = auditHeader.getOverideMessage();

				Iterator<ErrorDetail> itr = overideMessage.iterator();
				while (itr.hasNext()) {
					ErrorDetail errorDetail = itr.next();
					if (StringUtils.equals(errorDetail.getCode(), "60218")) {
						itr.remove();
					}
				}
				retValue = 0;
			}
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(FinChangeCustomer aFinChangeCustomer, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinChangeCustomer.getBefImage(), aFinChangeCustomer);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aFinChangeCustomer.getUserDetails(),
				getOverideMap());
	}

	public List<JointAccountDetail> getJointAccountDetail() {
		return jointAccountDetailList;
	}

	public void setJointAccountDetail(List<JointAccountDetail> jointAccountDetail) {
		this.jointAccountDetailList = jointAccountDetail;
	}

	public FinChangeCustomerService getFinChangeCustomerService() {
		return finChangeCustomerService;
	}

	public void setFinChangeCustomerService(FinChangeCustomerService finChangeCustomerService) {
		this.finChangeCustomerService = finChangeCustomerService;
	}

}
