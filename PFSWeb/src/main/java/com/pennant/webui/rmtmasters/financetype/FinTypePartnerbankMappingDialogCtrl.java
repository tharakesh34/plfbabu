package com.pennant.webui.rmtmasters.financetype;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Cluster;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.partnerbank.PartnerBankModes;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.rmtmasters.FinTypePartnerBankService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.extension.PartnerBankExtension;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class FinTypePartnerbankMappingDialogCtrl extends GFCBaseCtrl<FinTypePartnerBank> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(FinTypePartnerbankMappingDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinTypePartnerBankMappingDialog;
	protected ExtendedCombobox finType;
	protected Combobox purpose;
	protected Combobox paymentMode;
	protected ExtendedCombobox partnerBankID;
	private FinTypePartnerBank finTypePartnerBank; // overhanded per param
	protected Row row_Van;
	protected Label label_VanApplicable;
	protected Checkbox vanApplicable;
	protected Row row_Branch;
	protected Label label_Branch;
	protected ExtendedCombobox branch;
	protected Row row_Cluster;
	protected Label label_Cluster;
	protected ExtendedCombobox cluster;
	private String finDivision = null;

	List<ValueLabel> purposeList = PennantStaticListUtil.getPurposeList();

	private transient FinTypePartnerbankMappingListCtrl finTypeParterbankMappingListCtrl;
	FinTypePartnerBankService finTypePartnerBankService;

	private Cluster clusterData;

	/**
	 * default constructor.<br>
	 */
	public FinTypePartnerbankMappingDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinTypePartnerBankMappingDialog";
	}

	@Override
	protected String getReference() {
		StringBuffer referenceBuffer = new StringBuffer(String.valueOf(this.finTypePartnerBank.getID()));
		return referenceBuffer.toString();
	}

	public void onCreate$window_FinTypePartnerBankMappingDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_FinTypePartnerBankMappingDialog);

		try {
			// Get the required arguments.
			this.finTypePartnerBank = (FinTypePartnerBank) arguments.get("fintypepartnerbank");
			this.finTypeParterbankMappingListCtrl = (FinTypePartnerbankMappingListCtrl) arguments
					.get("fintypepartnerbankMappingListCtrl");

			if (this.finTypePartnerBank == null) {
				throw new AppException(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			FinTypePartnerBank aFintypePartnerbank = new FinTypePartnerBank();
			BeanUtils.copyProperties(this.finTypePartnerBank, aFintypePartnerbank);
			this.finTypePartnerBank.setBefImage(aFintypePartnerbank);

			// Render the page and display the data.
			doLoadWorkFlow(this.finTypePartnerBank.isWorkflow(), this.finTypePartnerBank.getWorkflowId(),
					this.finTypePartnerBank.getNextTaskId());

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
			doShowDialog(this.finTypePartnerBank);
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

		this.partnerBankID.setModuleName("PartnerBankModes");
		this.partnerBankID.setValueColumn("PartnerBankCode");
		this.partnerBankID.setDescColumn("PartnerBankName");
		this.partnerBankID.setValidateColumns(new String[] { "PartnerBankCode" });
		this.partnerBankID.setMandatoryStyle(true);

		if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("B")) {
			this.row_Branch.setVisible(true);
			this.label_Branch.setVisible(true);
			this.branch.setVisible(true);
			this.row_Cluster.setVisible(false);
			this.label_Cluster.setVisible(false);
			this.cluster.setVisible(false);
			this.branch.setModuleName("Branch");
			this.branch.setValueColumn("BranchCode");
			this.branch.setDescColumn("BranchDesc");
			this.branch.setMandatoryStyle(true);
			this.branch.setValidateColumns(new String[] { "BranchCode" });
		} else if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("C")) {
			this.row_Branch.setVisible(false);
			this.label_Branch.setVisible(false);
			this.branch.setVisible(false);
			this.row_Cluster.setVisible(true);
			this.label_Cluster.setVisible(true);
			this.cluster.setVisible(true);
			this.cluster.setModuleName("Cluster");
			this.cluster.setValueColumn("Code");
			this.cluster.setDescColumn("Name");
			this.cluster.setValidateColumns(new String[] { "Code", "Name" });
			this.cluster.setMandatoryStyle(true);
			this.cluster.setFilters(
					new Filter[] { new Filter("CLUSTERTYPE", PartnerBankExtension.CLUSTER_TYPE, Filter.OP_EQUAL) });
		} else {
			this.row_Branch.setVisible(false);
			this.label_Branch.setVisible(false);
			this.branch.setVisible(false);
			this.row_Cluster.setVisible(false);
			this.label_Cluster.setVisible(false);
			this.cluster.setVisible(false);
		}

		this.finType.setMaxlength(8);
		this.finType.setMandatoryStyle(true);
		this.finType.setModuleName("FinanceType");
		this.finType.setValueColumn("FinType");
		this.finType.setDescColumn("FinTypeDesc");
		this.finType.setValidateColumns(new String[] { "FinType" });

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinTypePartnerBankMappingDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinTypePartnerBankMappingDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinTypePartnerBankMappingDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinTypePartnerBankMappingDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
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
		doShowNotes(this.finTypePartnerBank);
		logger.debug(Literal.LEAVING);
	}

	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		finTypeParterbankMappingListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.finTypePartnerBank.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$partnerBankID(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		Object dataObject = partnerBankID.getObject();

		if (dataObject instanceof String) {
			this.partnerBankID.setValue(dataObject.toString());
			this.partnerBankID.setDescription("");
		} else {
			PartnerBankModes partnerBankModes = (PartnerBankModes) dataObject;
			if (partnerBankModes != null) {
				this.partnerBankID.setAttribute("PartnerBankId", partnerBankModes.getPartnerBankId());
				this.partnerBankID.setValue(partnerBankModes.getPartnerBankCode(),
						partnerBankModes.getPartnerBankName());
			}
		}

		logger.debug("Leaving");
	}

	public void onFulfill$finType(Event event) {
		String finType = this.finType.getValue();

		if (finType == null) {
			return;
		}

		FinanceType ft = (FinanceType) this.finType.getObject();

		if (ft != null) {
			finDivision = ft.getFinDivision();
		}

		setPartnerBankProperties();
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param finTypePartnerBank
	 * 
	 */
	public void doWriteBeanToComponents(FinTypePartnerBank aFinTypePartnerBank) {
		logger.debug(Literal.ENTERING);

		this.finType.setValue(aFinTypePartnerBank.getFinType());
		this.finType.setDescription(aFinTypePartnerBank.getFinTypeDesc());

		fillComboBox(this.purpose, aFinTypePartnerBank.getPurpose(), purposeList, "");

		String purposeValue = this.purpose.getSelectedItem().getValue();

		List<ValueLabel> paymentModesList = new ArrayList<>();

		if (StringUtils.equals(purposeValue, AccountConstants.PARTNERSBANK_DISB)
				|| StringUtils.equals(purposeValue, AccountConstants.PARTNERSBANK_PAYMENT)) {
			paymentModesList = PennantStaticListUtil.getPaymentTypesWithIST();
		} else {
			paymentModesList = PennantStaticListUtil.getAllPaymentTypes();
		}

		fillComboBox(this.paymentMode, aFinTypePartnerBank.getPaymentMode(), paymentModesList, "");

		setPartnerBankProperties();

		if (!aFinTypePartnerBank.isNewRecord()) {
			this.partnerBankID.setValue(StringUtils.trimToEmpty(aFinTypePartnerBank.getPartnerBankCode()),
					StringUtils.trimToEmpty(aFinTypePartnerBank.getPartnerBankName()));
			this.partnerBankID.setAttribute("PartnerBankId", aFinTypePartnerBank.getPartnerBankID());
		}
		this.vanApplicable.setChecked(aFinTypePartnerBank.isVanApplicable());
		if (this.branch.isVisible()) {
			this.branch.setValue(aFinTypePartnerBank.getBranchCode());
			this.branch.setDescription(aFinTypePartnerBank.getBranchDesc());
		} else if (this.cluster.isVisible()) {
			this.cluster.setId(String.valueOf(aFinTypePartnerBank.getClusterId()));
			this.cluster.setValue(aFinTypePartnerBank.getClusterCode());
			this.cluster.setDescription(aFinTypePartnerBank.getName());
		}
		this.recordStatus.setValue(aFinTypePartnerBank.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$cluster(Event event) {
		logger.debug(Literal.ENTERING);

		Cluster cluster = (Cluster) this.cluster.getObject();

		if (cluster == null) {
			return;
		}
		Search search = new Search(Cluster.class);
		search.addFilterEqual("Id", cluster.getId());

		SearchProcessor searchProcessor = (SearchProcessor) SpringBeanUtil.getBean("searchProcessor");
		clusterData = (Cluster) searchProcessor.getResults(search).get(0);

		this.cluster.setId(String.valueOf(clusterData.getId()));
		this.cluster.setDescription(clusterData.getName());
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinTypePartnerBank
	 */
	public void doWriteComponentsToBean(FinTypePartnerBank aFinTypePartnerBank) {
		logger.debug(Literal.LEAVING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		doSetLOVValidation();

		// Finance Type
		try {
			String finTypeValue = StringUtils.trimToEmpty(this.finType.getValue());
			aFinTypePartnerBank.setFinType(finTypeValue);
			aFinTypePartnerBank.setFinTypeDesc(this.finType.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Purpose
		try {
			String purposeValue = getComboboxValue(this.purpose);
			if (PennantConstants.List_Select.equals(purposeValue)) {
				throw new WrongValueException(this.purpose, Labels.getLabel("Label_RuleDialog_select_list"));
			}
			aFinTypePartnerBank.setPurpose(purposeValue);
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Payment Mode
		try {
			String paymentModeValue = getComboboxValue(this.paymentMode);
			if (PennantConstants.List_Select.equals(paymentModeValue)) {
				throw new WrongValueException(this.paymentMode, Labels.getLabel("Label_RuleDialog_select_list"));
			}
			aFinTypePartnerBank.setPaymentMode(getComboboxValue(this.paymentMode));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Partner Bank ID
		try {

			aFinTypePartnerBank.setPartnerBankName((this.partnerBankID.getDescription()));
			aFinTypePartnerBank.setPartnerBankCode(this.partnerBankID.getValue());
			this.partnerBankID.getValidatedValue();
			Object object = this.partnerBankID.getAttribute("PartnerBankId");

			if (object != null) {
				aFinTypePartnerBank.setPartnerBankID(Long.parseLong(object.toString()));

			} else {
				aFinTypePartnerBank.setPartnerBankID(Long.MIN_VALUE);
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinTypePartnerBank.setVanApplicable(this.vanApplicable.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (this.branch.isVisible()) {
			try {
				this.branch.getValidatedValue();
				aFinTypePartnerBank.setBranchCode(this.branch.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

		} else if (this.cluster.isVisible()) {
			try {
				aFinTypePartnerBank.setName(this.cluster.getDescription());
				aFinTypePartnerBank.setClusterCode(this.cluster.getValue());
				this.cluster.getValidatedValue();
				aFinTypePartnerBank.setClusterId(Long.parseLong(this.cluster.getId()));

			} catch (WrongValueException we) {
				wve.add(we);
			}

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

	/**
	 * Displays the dialog page.
	 * 
	 * @param finTypePartnerBank The entity that need to be render.
	 */
	public void doShowDialog(FinTypePartnerBank finTypePartnerBank) {
		logger.debug(Literal.ENTERING);

		if (finTypePartnerBank.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.finType.focus();
		} else {
			this.finType.focus();
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(finTypePartnerBank.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_FinTypePartnerBankMappingDialog.onClose();
		} catch (Exception e) {
			throw e;
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);
		boolean isMandValidate = true;

		this.partnerBankID.setConstraint(
				new PTStringValidator(Labels.getLabel("label_FinTypePartnerBankDialog_PartnerBankID.value"), null,
						isMandValidate ? this.partnerBankID.isMandatory() : false, true));

		if (!this.finType.isReadonly()) {
			this.finType.setConstraint(
					new PTStringValidator(Labels.getLabel("label_LoanTypePartnerbankMappingDialogue_FinType.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("B")) {
			if (!this.branch.isReadonly())
				this.branch.setConstraint(new PTStringValidator(
						Labels.getLabel("label_LoanTypePartnerbankMappingDialogue_BranchOrCluster.value"),
						PennantRegularExpressions.REGEX_DESCRIPTION, true));
		} else if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("C")) {
			if (!this.cluster.isReadonly())
				this.cluster.setConstraint(new PTStringValidator(
						Labels.getLabel("label_LoanTypePartnerbankMappingDialogue_BranchOrCluster.value"),
						PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		logger.debug(Literal.LEAVING);

	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.finType.setConstraint("");
		this.purpose.setConstraint("");
		this.paymentMode.setConstraint("");
		this.partnerBankID.setConstraint("");
		this.cluster.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final FinTypePartnerBank aFinTypePartnerBank = new FinTypePartnerBank();
		BeanUtils.copyProperties(this.finTypePartnerBank, aFinTypePartnerBank);

		doDelete(aFinTypePartnerBank.getPartnerBankCode(), aFinTypePartnerBank);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);
		if (getFinTypePartnerBank().isNewRecord()) {
			this.finType.setReadonly(false);
		} else {
			this.finType.setReadonly(true);
		}
		doWriteBeanToComponents(finTypePartnerBank);

		readOnlyComponent(isReadOnly("FinTypePartnerBankMappingDialog_Purpose"), this.purpose);
		readOnlyComponent(isReadOnly("FinTypePartnerBankMappingDialog_PaymentMode"), this.paymentMode);
		readOnlyComponent(isReadOnly("FinTypePartnerBankMappingDialog_PartnerBankID"), this.partnerBankID);

		if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("B")) {
			readOnlyComponent(isReadOnly("FinTypePartnerBankMappingDialog_branch"), this.branch);
		} else {
			readOnlyComponent(isReadOnly("FinTypePartnerBankMappingDialog_branch"), this.cluster);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.finTypePartnerBank.isNewRecord()) {
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

	public boolean isReadOnly(String componentName) {
		return getUserWorkspace().isReadOnly(componentName);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.LEAVING);

		readOnlyComponent(true, this.finType);
		readOnlyComponent(true, this.purpose);
		readOnlyComponent(true, this.paymentMode);
		readOnlyComponent(true, this.partnerBankID);
		if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("B")) {
			readOnlyComponent(true, this.branch);
		} else {
			readOnlyComponent(true, this.cluster);
		}

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
	 * Set the Rule Return Type
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onChange$purpose(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		String purposeValue = this.purpose.getSelectedItem().getValue();

		List<ValueLabel> paymentModesList = new ArrayList<>();

		if (StringUtils.equals(purposeValue, AccountConstants.PARTNERSBANK_DISB)
				|| StringUtils.equals(purposeValue, AccountConstants.PARTNERSBANK_PAYMENT)) {
			paymentModesList = PennantStaticListUtil.getPaymentTypesWithIST();
		} else {
			paymentModesList = PennantStaticListUtil.getAllPaymentTypes();
		}

		fillComboBox(this.paymentMode, "", paymentModesList, "");
		setPartnerBankProperties();

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the Rule Return Type
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onChange$paymentMode(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		setPartnerBankProperties();

		logger.debug("Leaving" + event.toString());
	}

	private void setPartnerBankProperties() {
		logger.debug("Entering");

		String purposeValue = getComboboxValue(this.purpose);
		String paymentModeValue = getComboboxValue(this.paymentMode);

		Filter[] filters = null;

		if (StringUtils.isNotEmpty(finDivision)) {
			filters = new Filter[3];
			filters[0] = new Filter("Purpose", purposeValue, Filter.OP_EQUAL);
			filters[1] = new Filter("PaymentMode", paymentModeValue, Filter.OP_EQUAL);
			filters[2] = new Filter("DIVISIONCODE", finDivision, Filter.OP_EQUAL);
		} else {
			filters = new Filter[2];
			filters[0] = new Filter("Purpose", purposeValue, Filter.OP_EQUAL);
			filters[1] = new Filter("PaymentMode", paymentModeValue, Filter.OP_EQUAL);
		}

		this.partnerBankID.setValue("");
		this.partnerBankID.setDescription("");
		this.partnerBankID.setFilters(filters);

		if (StringUtils.equals(AccountConstants.PARTNERSBANK_RECEIPTS, this.purpose.getSelectedItem().getValue())) {
			this.row_Van.setVisible(SysParamUtil.isAllowed(SMTParameterConstants.VAN_REQUIRED));
		} else {
			this.vanApplicable.setChecked(false);
			this.row_Van.setVisible(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");

		this.finType.setValue("");
		this.purpose.setValue("");
		this.paymentMode.setValue("");
		this.partnerBankID.setValue("");
		this.partnerBankID.setDescription("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */

	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final FinTypePartnerBank aFinTypePartnerBank = new FinTypePartnerBank();
		BeanUtils.copyProperties(this.finTypePartnerBank, aFinTypePartnerBank);
		boolean isNew = false;

		doClearMessage();
		doSetValidation();
		doWriteComponentsToBean(aFinTypePartnerBank);

		isNew = aFinTypePartnerBank.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinTypePartnerBank.getRecordType())) {
				aFinTypePartnerBank.setVersion(aFinTypePartnerBank.getVersion() + 1);
				if (isNew) {
					aFinTypePartnerBank.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinTypePartnerBank.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinTypePartnerBank.setNewRecord(true);
				}
			}
		} else {
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
				aFinTypePartnerBank.setVersion(1);
				aFinTypePartnerBank.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}

			if (StringUtils.isBlank(aFinTypePartnerBank.getRecordType())) {
				aFinTypePartnerBank.setVersion(aFinTypePartnerBank.getVersion() + 1);
				aFinTypePartnerBank.setRecordType(PennantConstants.RCD_UPD);
			}

			if (aFinTypePartnerBank.getRecordType().equals(PennantConstants.RCD_ADD) && isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (aFinTypePartnerBank.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {

			if (doProcess(aFinTypePartnerBank, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}

		logger.debug("Leaving");
	}

	protected boolean doProcess(FinTypePartnerBank aFinTypePartnerBank, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aFinTypePartnerBank.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFinTypePartnerBank.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinTypePartnerBank.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aFinTypePartnerBank.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFinTypePartnerBank.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aFinTypePartnerBank);
				}

				if (isNotesMandatory(taskId, aFinTypePartnerBank)) {
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

			aFinTypePartnerBank.setTaskId(taskId);
			aFinTypePartnerBank.setNextTaskId(nextTaskId);
			aFinTypePartnerBank.setRoleCode(getRole());
			aFinTypePartnerBank.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aFinTypePartnerBank, tranType);
			String operationRefs = getServiceOperations(taskId, aFinTypePartnerBank);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aFinTypePartnerBank, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aFinTypePartnerBank, tranType);
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
		FinTypePartnerBank aFintypeParrtnerbank = (FinTypePartnerBank) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = finTypePartnerBankService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = finTypePartnerBankService.saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = finTypePartnerBankService.doApprove(auditHeader);

						if (aFintypeParrtnerbank.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = finTypePartnerBankService.doReject(auditHeader);
						if (aFintypeParrtnerbank.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_FinTypePartnerBankMappingDialog,
								auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_FinTypePartnerBankMappingDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.finTypePartnerBank), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (AppException e) {
			logger.error("Exception: ", e);
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_FinTypePartnerBankMappingDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
	}

	private AuditHeader getAuditHeader(FinTypePartnerBank aFinTypePartnerBank, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinTypePartnerBank.getBefImage(), aFinTypePartnerBank);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aFinTypePartnerBank.getUserDetails(),
				getOverideMap());
	}

	public FinTypePartnerBank getFinTypePartnerBank() {
		return finTypePartnerBank;
	}

	public void setFinTypePartnerBank(FinTypePartnerBank finTypePartnerBank) {
		this.finTypePartnerBank = finTypePartnerBank;
	}

	public void setFinTypeParterbankMappingListCtrl(
			FinTypePartnerbankMappingListCtrl finTypeParterbankMappingListCtrl) {
	}

	public void setFinTypePartnerBankService(FinTypePartnerBankService finTypePartnerBankService) {
		this.finTypePartnerBankService = finTypePartnerBankService;
	}
}
