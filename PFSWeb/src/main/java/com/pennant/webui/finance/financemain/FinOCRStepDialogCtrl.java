package com.pennant.webui.finance.financemain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinOCRDetail;
import com.pennant.backend.model.finance.FinOCRHeader;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class FinOCRStepDialogCtrl extends GFCBaseCtrl<FinOCRDetail> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(FinOCRStepDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window windowFinOCRStepDialog;
	protected Intbox stepSequence;
	protected Combobox contributor;
	protected Intbox customerContribution;
	protected Intbox financerContribution;
	protected Space spaceCustomerContribution;
	protected Space spaceFinancerContribution;
	private FinOCRDetail finOCRDetail;
	private FinOCRHeader finOCRHeader;
	private List<ValueLabel> contributorList = PennantStaticListUtil.getOCRContributorList();
	private FinOCRDialogCtrl finOCRDialogCtrl;
	private transient boolean fromParent;
	private List<FinOCRDetail> finOCRDetailsList = new ArrayList<FinOCRDetail>();
	private String roleCode;

	/**
	 * default constructor.<br>
	 */
	public FinOCRStepDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinOCRStepDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$windowFinOCRStepDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(windowFinOCRStepDialog);

		try {
			// Get the required arguments.
			this.finOCRDetail = (FinOCRDetail) arguments.get("finOCRDetail");

			if (this.finOCRDetail == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			if (arguments.containsKey("finOCRDialogCtrl")) {
				this.finOCRDialogCtrl = (FinOCRDialogCtrl) arguments.get("finOCRDialogCtrl");
				setFromParent(true);
			}

			if (arguments.containsKey("finOCRHeader")) {
				this.finOCRHeader = (FinOCRHeader) arguments.get("finOCRHeader");
				setFinOCRHeader(this.finOCRHeader);
			}

			if (arguments.containsKey("roleCode")) {
				this.roleCode = (String) arguments.get("roleCode");
			}

			if (arguments.containsKey("enqiryModule")) {
				this.enqiryModule = (Boolean) arguments.get("enqiryModule");
			}
			// Store the before image.
			FinOCRDetail finOCRDetail = new FinOCRDetail();
			BeanUtils.copyProperties(this.finOCRDetail, finOCRDetail);
			this.finOCRDetail.setBefImage(finOCRDetail);

			// Render the page and display the data.
			doLoadWorkFlow(this.finOCRDetail.isWorkflow(), this.finOCRDetail.getWorkflowId(),
					this.finOCRDetail.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				getUserWorkspace().allocateRoleAuthorities(this.roleCode, this.pageRightName);
			} else if (!enqiryModule) {
				getUserWorkspace().allocateRoleAuthorities(this.roleCode, this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.finOCRDetail);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		this.spaceCustomerContribution.setVisible(false);
		this.spaceFinancerContribution.setVisible(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		if (!enqiryModule) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinOCRStepDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinOCRStepDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinOCRStepDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinOCRStepDialog_btnSave"));
		} else {
			this.btnNew.setVisible(false);
			this.btnEdit.setVisible(false);
			this.btnDelete.setVisible(false);
			this.btnSave.setVisible(false);
		}
		this.btnCancel.setVisible(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.finOCRDetail);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.finOCRDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinOCRDetail
	 * 
	 */
	public void doWriteBeanToComponents(FinOCRDetail aFinOCRDetail) {
		logger.debug(Literal.ENTERING);
		this.stepSequence.setValue(getNextStepSequence(aFinOCRDetail));
		fillComboBox(this.contributor, aFinOCRDetail.getContributor(), contributorList, "");
		this.customerContribution.setValue(aFinOCRDetail.getCustomerContribution());
		this.financerContribution.setValue(aFinOCRDetail.getFinancerContribution());
		doSetContributorProperties(aFinOCRDetail.getContributor());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinOCRDetail
	 */
	public void doWriteComponentsToBean(FinOCRDetail aFinOCRDetail) {
		logger.debug(Literal.ENTERING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Step Seq
		try {
			aFinOCRDetail.setStepSequence(this.stepSequence.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Customer Contribution
		try {
			aFinOCRDetail.setCustomerContribution(this.customerContribution.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//FinancerContribution
		try {
			aFinOCRDetail.setFinancerContribution(this.financerContribution.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if ("#".equals(getComboboxValue(this.contributor))) {
				if (!this.contributor.isDisabled()) {
					throw new WrongValueException(this.contributor, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_FinOCRStepDialog_Contributor.value") }));
				}
			} else {
				aFinOCRDetail.setContributor(getComboboxValue(this.contributor));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		doRemoveValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param aFinOCRDetail
	 *            The entity that need to be render.
	 */
	public void doShowDialog(FinOCRDetail aFinOCRDetail) {
		logger.debug(Literal.ENTERING);

		// set ReadOnly mode accordingly if the object is new or not.
		if (aFinOCRDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isFromParent()) {
				if (enqiryModule) {
					doReadOnly();
					this.btnCtrl.setBtnStatus_Enquiry();
					this.btnNotes.setVisible(false);
				} else {
					doEdit();
				}
			} else if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		doWriteBeanToComponents(aFinOCRDetail);
		this.windowFinOCRStepDialog.setHeight("40%");
		this.windowFinOCRStepDialog.setWidth("80%");
		setDialog(DialogType.MODAL);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Setting the validation constraints for fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);
		if (!this.customerContribution.isReadonly() && this.spaceCustomerContribution.isVisible()) {
			this.customerContribution.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_FinOCRStepDialog_CustomerContribution.value"), true, false, 1, 99));
		}

		if (!this.financerContribution.isReadonly() && this.spaceFinancerContribution.isVisible()) {
			this.financerContribution.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_FinOCRStepDialog_FinancerContribution.value"), true, false, 1, 99));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		this.customerContribution.setConstraint("");
		this.financerContribution.setConstraint("");
		this.contributor.setConstraint("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);
		this.customerContribution.setErrorMessage("");
		this.financerContribution.setErrorMessage("");
		this.contributor.setErrorMessage("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Deletes a FinOCRDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final FinOCRDetail aFinOCRDetail = new FinOCRDetail();
		BeanUtils.copyProperties(this.finOCRDetail, aFinOCRDetail);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aFinOCRDetail.getStepSequence();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aFinOCRDetail.getRecordType())) {
				aFinOCRDetail.setVersion(aFinOCRDetail.getVersion() + 1);
				aFinOCRDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				aFinOCRDetail.setNewRecord(true);
				if (isWorkFlowEnabled()) {
					aFinOCRDetail.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				tranType = PennantConstants.TRAN_DEL;
				AuditHeader auditHeader = processFinOCRStepDetails(aFinOCRDetail, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.windowFinOCRStepDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					finOCRDialogCtrl.doFillFinOCRStepDetails(this.finOCRDetailsList);
					closeDialog();
				}
			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);
		readOnlyComponent(isReadOnly("FinOCRStepDialog_contributor"), this.contributor);
		readOnlyComponent(isReadOnly("FinOCRStepDialog_customerContribution"), this.customerContribution);
		readOnlyComponent(isReadOnly("FinOCRStepDialog_financerContribution"), this.financerContribution);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		readOnlyComponent(true, this.contributor);
		readOnlyComponent(true, this.customerContribution);
		readOnlyComponent(true, this.financerContribution);

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
		logger.debug("Entering");
		this.contributor.setValue("");
		this.customerContribution.setValue(0);
		this.financerContribution.setValue(0);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final FinOCRDetail aFinOCRDetail = new FinOCRDetail();
		BeanUtils.copyProperties(this.finOCRDetail, aFinOCRDetail);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aFinOCRDetail);

		isNew = aFinOCRDetail.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinOCRDetail.getRecordType())) {
				aFinOCRDetail.setVersion(aFinOCRDetail.getVersion() + 1);
				if (isNew) {
					aFinOCRDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinOCRDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinOCRDetail.setNewRecord(true);
				}
			}
		} else {
			aFinOCRDetail.setVersion(aFinOCRDetail.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
				aFinOCRDetail.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}

			if (StringUtils.isBlank(aFinOCRDetail.getRecordType())) {
				aFinOCRDetail.setVersion(aFinOCRDetail.getVersion() + 1);
				aFinOCRDetail.setRecordType(PennantConstants.RCD_UPD);
			}

			if (PennantConstants.RCD_ADD.equals(aFinOCRDetail.getRecordType()) && isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (PennantConstants.RECORD_TYPE_NEW.equals(aFinOCRDetail.getRecordType())) {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		try {
			AuditHeader auditHeader = processFinOCRStepDetails(aFinOCRDetail, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.windowFinOCRStepDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				finOCRDialogCtrl.doFillFinOCRStepDetails(this.finOCRDetailsList);
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private AuditHeader processFinOCRStepDetails(FinOCRDetail aFinOCRDetail, String tranType) {

		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aFinOCRDetail, tranType);
		finOCRDetailsList = new ArrayList<FinOCRDetail>();

		if (CollectionUtils.isNotEmpty(getFinOCRDialogCtrl().getFinOCRDetailList())) {
			if (!PennantConstants.TRAN_DEL.equals(tranType)) {
				auditHeader = validateOCRSteps(getFinOCRDialogCtrl().getFinOCRDetailList(), aFinOCRDetail, auditHeader);
			}
			if (!CollectionUtils.isEmpty(auditHeader.getErrorMessage())) {
				return auditHeader;
			}
			for (FinOCRDetail finOCRDetail : getFinOCRDialogCtrl().getFinOCRDetailList()) {

				// Both Current and Existing list Sequence is same
				if (finOCRDetail.getStepSequence() == aFinOCRDetail.getStepSequence()) {

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (PennantConstants.RECORD_TYPE_UPD.equals(aFinOCRDetail.getRecordType())) {
							aFinOCRDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							finOCRDetailsList.add(aFinOCRDetail);
						} else if (PennantConstants.RCD_ADD.equals(aFinOCRDetail.getRecordType())) {
							recordAdded = true;
						} else if (PennantConstants.RECORD_TYPE_NEW.equals(aFinOCRDetail.getRecordType())) {
							aFinOCRDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							finOCRDetailsList.add(aFinOCRDetail);
						} else if (PennantConstants.RECORD_TYPE_CAN.equals(aFinOCRDetail.getRecordType())) {
							recordAdded = true;
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							finOCRDetailsList.add(finOCRDetail);
						}
					}
				} else {
					finOCRDetailsList.add(finOCRDetail);
				}
			}
		} else if (!PennantConstants.TRAN_DEL.equals(tranType)) {
			auditHeader = validateOCRSteps(null, aFinOCRDetail, auditHeader);
		}
		if (!recordAdded) {
			finOCRDetailsList.add(aFinOCRDetail);
		}
		return auditHeader;

	}

	/**
	 * Validating OCR Step Details
	 * 
	 * @param finOCRDetailList
	 * @param aFinOCRDetail
	 * @param auditHeader
	 * @return
	 */
	private AuditHeader validateOCRSteps(List<FinOCRDetail> finOCRDetailList, FinOCRDetail aFinOCRDetail,
			AuditHeader auditHeader) {
		int customerPortion = 0;
		int financerPortion = 0;
		int totalExtCustomer = 0;
		int totalExtFinancer = 0;
		String message = "Total ";
		String[] valueParm = new String[2];

		// Header contribution splitting 
		if (getFinOCRHeader() != null) {
			//100 – value entered at header section against field "Customer Portion (%)".
			financerPortion = 100 - getFinOCRHeader().getCustomerPortion();
			customerPortion = getFinOCRHeader().getCustomerPortion();
		}
		if (!CollectionUtils.isEmpty(finOCRDetailList)) {
			for (FinOCRDetail finOCRDetail : finOCRDetailList) {
				if (PennantConstants.RCD_DEL.equalsIgnoreCase(finOCRDetail.getRecordType())
						|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(finOCRDetail.getRecordType())) {
					continue;
				}
				if (aFinOCRDetail.getStepSequence() != finOCRDetail.getStepSequence()) {
					totalExtCustomer += finOCRDetail.getCustomerContribution();
					totalExtFinancer += finOCRDetail.getFinancerContribution();
				}
			}
		}

		totalExtCustomer += aFinOCRDetail.getCustomerContribution();
		totalExtFinancer += aFinOCRDetail.getFinancerContribution();

		if (totalExtCustomer > customerPortion
				&& PennantConstants.CUSTOMER_CONTRIBUTION.equals(aFinOCRDetail.getContributor())) {
			valueParm[0] = message.concat(Labels.getLabel("label_FinOCRStepDialog_CustomerContribution.value"));
			valueParm[1] = String.valueOf(customerPortion);
			auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));
			return auditHeader;
		} else if (totalExtFinancer > financerPortion
				&& PennantConstants.FINANCER_CONTRIBUTION.equals(aFinOCRDetail.getContributor())) {
			valueParm[0] = message.concat(Labels.getLabel("label_FinOCRStepDialog_FinancerContribution.value"));
			valueParm[1] = String.valueOf(financerPortion);
			auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));
			return auditHeader;
		}
		return auditHeader;
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aFinOCRDetail
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(FinOCRDetail aFinOCRDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinOCRDetail.getBefImage(), aFinOCRDetail);

		return new AuditHeader(getReference(), String.valueOf(aFinOCRDetail.getDetailID()), null, null, auditDetail,
				aFinOCRDetail.getUserDetails(), getOverideMap());
	}

	/**
	 * onChange Event For contributor
	 */
	public void onChange$contributor(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		String contributor = getComboboxValue(this.contributor);
		doSetContributorProperties(contributor);
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Setting Contributor properties
	 * 
	 * @param contributor
	 */
	private void doSetContributorProperties(String contributor) {
		if (PennantConstants.CUSTOMER_CONTRIBUTION.equals(contributor)) {
			this.customerContribution.setReadonly(isReadOnly("FinOCRStepDialog_customerContribution"));
			this.spaceCustomerContribution.setVisible(true);
			this.spaceCustomerContribution.setSclass(PennantConstants.mandateSclass);
			this.spaceFinancerContribution.setVisible(false);
			this.spaceFinancerContribution.setSclass("");
			this.financerContribution.setErrorMessage("");
			this.financerContribution.setReadonly(true);
			this.financerContribution.setValue(0);
		} else if (PennantConstants.FINANCER_CONTRIBUTION.equals(contributor)) {
			this.financerContribution.setReadonly(isReadOnly("FinOCRStepDialog_financerContribution"));
			this.spaceFinancerContribution.setVisible(true);
			this.spaceFinancerContribution.setSclass(PennantConstants.mandateSclass);
			this.spaceCustomerContribution.setVisible(false);
			this.spaceCustomerContribution.setSclass("");
			this.customerContribution.setErrorMessage("");
			this.customerContribution.setValue(0);
			this.customerContribution.setReadonly(true);
		}
	}

	/**
	 * Generating Step Sequence
	 * 
	 * @param finOCRDetail
	 * @return
	 */
	private int getNextStepSequence(FinOCRDetail finOCRDetail) {
		int sequence = 0;
		if (finOCRDetail.getStepSequence() > 0) {
			return finOCRDetail.getStepSequence();
		}
		List<FinOCRDetail> list = getFinOCRDialogCtrl().getFinOCRDetailList();
		if (!CollectionUtils.isEmpty(list)) {
			for (FinOCRDetail finOCRDetail2 : list) {
				if (finOCRDetail2.getStepSequence() > 0) {
					sequence = finOCRDetail2.getStepSequence();
				}
			}
		}

		return sequence + 1;
	}

	/**
	 * checking user has access for the components or not
	 */
	public boolean isReadOnly(String componentName) {
		if (enqiryModule) {
			return true;
		} else if (isWorkFlowEnabled()) {
			return getUserWorkspace().isReadOnly(componentName);
		} else {
			return getUserWorkspace().isReadOnly(componentName);
		}
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	public FinOCRDialogCtrl getFinOCRDialogCtrl() {
		return finOCRDialogCtrl;
	}

	public void setFinOCRDialogCtrl(FinOCRDialogCtrl finOCRDialogCtrl) {
		this.finOCRDialogCtrl = finOCRDialogCtrl;
	}

	public List<FinOCRDetail> getFinOCRDetailsList() {
		return finOCRDetailsList;
	}

	public void setFinOCRDetailsList(List<FinOCRDetail> finOCRDetailsList) {
		this.finOCRDetailsList = finOCRDetailsList;
	}

	public boolean isFromParent() {
		return fromParent;
	}

	public void setFromParent(boolean newFinance) {
		this.fromParent = newFinance;
	}

	public FinOCRHeader getFinOCRHeader() {
		return finOCRHeader;
	}

	public void setFinOCRHeader(FinOCRHeader finOCRHeader) {
		this.finOCRHeader = finOCRHeader;
	}

}
