package com.pennanttech.pff.provision.web;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.North;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.enquiry.FinanceEnquiryHeaderDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.provision.dao.ProvisionDAO;
import com.pennanttech.pff.provision.model.Provision;
import com.pennanttech.pff.provision.model.ProvisionRuleData;
import com.pennanttech.pff.provision.service.ProvisionService;

public class ManualProvisioningDialogCtrl extends GFCBaseCtrl<Provision> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ManualProvisioningDialogCtrl.class);

	protected Window window_ManualProvisioningDialog;

	protected Groupbox finBasicdetails;
	protected North north;
	protected Textbox finReference;
	protected Textbox customer;
	protected Textbox finType;
	protected CurrencyBox finAmount;
	protected Datebox finStartDate;
	protected Datebox maturityDate;
	protected CurrencyBox principalOutstanding;
	protected CurrencyBox totalOutstanding;
	protected CurrencyBox pricipalOverDue;
	protected CurrencyBox interestOverDue;
	protected Intbox actualDpd;
	protected Intbox effectiveDpd;
	protected Textbox loanActualClassification;
	protected Textbox effectiveClassification;
	protected Decimalbox regProvisionPercentage;
	protected CurrencyBox regProvisionAmount;
	protected Decimalbox intProvisionPercentage;
	protected CurrencyBox intProvisionAmount;
	protected Checkbox manualProvision;
	protected Decimalbox manProvisionPercentage;
	protected CurrencyBox manProvisionAmount;
	protected Label amanProvisionAmount;
	protected Button btnSearchCustomer;

	private Provision provision;
	private transient ManualProvisioningListCtrl manualProvisioningListCtrl;
	private transient ProvisionService provisionService;
	private CustomerDetailsService customerDetailsService;
	private Combobox effManualAssetClassification;
	private Combobox effManualAssetSubClassification;
	protected Decimalbox newProvisionRegPercentage;
	protected CurrencyBox newProvisionRegAmount;
	protected Decimalbox newProvisionIntPercentage;
	protected CurrencyBox newProvisionIntAmount;
	protected Checkbox overrideProvision;
	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	private ProvisionDAO provisionDao;

	/**
	 * default constructor.<br>
	 */
	public ManualProvisioningDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ManualProvisioningDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.provision.getId());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_ManualProvisioningDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_ManualProvisioningDialog);

		try {
			if (arguments.containsKey("enquiry")) {
				enqiryModule = (boolean) arguments.get("enquiry");
			} else {
				enqiryModule = false;
				if (finBasicdetails != null) {
					finBasicdetails.setVisible(false);
				}

			}

			if (arguments.containsKey("financeEnquiryHeaderDialogCtrl")) {
				this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) arguments
						.get("financeEnquiryHeaderDialogCtrl");
				getBorderLayoutHeight();
				int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount() * 20;
				this.window_ManualProvisioningDialog.setHeight(this.borderLayoutHeight - rowsHeight + "px");
				this.north.setVisible(false);
			}

			// Get the required arguments.
			this.provision = (Provision) arguments.get("provision");
			this.manualProvisioningListCtrl = (ManualProvisioningListCtrl) arguments.get("manualProvisioningListCtrl");

			if (this.provision == null) {
				// throw new Exception(Labels.getLabel("error.unhandled"));
				this.provision = new Provision();
				this.btnSearchCustomer.setDisabled(true);
			}
			// Store the before image.
			Provision provision = new Provision();
			BeanUtils.copyProperties(this.provision, provision);
			this.provision.setBefImage(provision);

			// Render the page and display the data.
			doLoadWorkFlow(this.provision.isWorkflow(), this.provision.getWorkflowId(), this.provision.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.provision);
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

		int finFormatter = CurrencyUtil.getFormat(provision.getFinCcy());

		this.finAmount.setProperties(false, finFormatter);
		this.principalOutstanding.setProperties(false, finFormatter);
		this.totalOutstanding.setProperties(false, finFormatter);
		this.pricipalOverDue.setProperties(false, finFormatter);
		this.interestOverDue.setProperties(false, finFormatter);
		this.regProvisionAmount.setProperties(false, finFormatter);
		this.intProvisionAmount.setProperties(false, finFormatter);
		this.manProvisionAmount.setProperties(false, finFormatter);
		this.effManualAssetClassification.setMaxlength(20);
		this.effManualAssetSubClassification.setMaxlength(20);
		this.newProvisionRegPercentage.setMaxlength(8);
		this.newProvisionRegAmount.setProperties(false, finFormatter);
		this.newProvisionIntPercentage.setMaxlength(8);
		this.newProvisionIntAmount.setProperties(false, finFormatter);
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ManualProvisioningDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ManualProvisioningDialog_btnEdit"));
		this.btnDelete.setVisible(false);
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ManualProvisioningDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		doSave();
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		MessageUtil.showHelpWindow(event, super.window);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		doCancel();
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.provision.getBefImage());
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param academic
	 * 
	 */
	public void doWriteBeanToComponents(Provision provision) {
		logger.debug(Literal.ENTERING);
		int format = CurrencyUtil.getFormat(provision.getFinCcy());

		this.finReference.setValue(provision.getFinReference());
		this.customer.setValue(provision.getCustCIF() + "-" + provision.getCustShrtName());
		this.finType.setValue(provision.getFinType());
		this.finAmount.setValue(PennantApplicationUtil.formateAmount(provision.getFinAssetValue(), format));
		this.finStartDate.setValue(provision.getFinStartDate());
		this.maturityDate.setValue(provision.getMaturityDate());
		this.principalOutstanding.setValue(PennantApplicationUtil.formateAmount(provision.getOsPrincipal(), format));
		this.totalOutstanding.setValue(
				PennantApplicationUtil.formateAmount(provision.getOsProfit().add(provision.getOsPrincipal()), format));

		this.pricipalOverDue.setValue(PennantApplicationUtil.formateAmount(provision.getOdPrincipal(), format));
		this.interestOverDue.setValue(PennantApplicationUtil.formateAmount(provision.getOdProfit(), format));

		this.actualDpd.setValue(provision.getPastDueDays());
		this.effectiveDpd.setValue(provision.getEffNpaPastDueDays());
		this.loanActualClassification.setValue(provision.getLoanClassification());
		this.effectiveClassification.setValue(provision.getEffectiveClassification());
		this.regProvisionPercentage.setValue(provision.getRegProvsnPer());
		this.regProvisionAmount.setValue(PennantApplicationUtil.formateAmount(provision.getRegProvsnAmt(), format));
		this.intProvisionPercentage.setValue(provision.getIntProvsnPer());
		this.intProvisionAmount.setValue(PennantApplicationUtil.formateAmount(provision.getIntProvsnAmt(), format));

		boolean manProv = provision.isManualProvision();
		BigDecimal manProvPer = provision.getManProvsnPer();
		BigDecimal manProvAmt = provision.getManProvsnAmt();

		this.manualProvision.setChecked(manProv);
		this.overrideProvision.setChecked(provision.isOverrideProvision());
		this.manProvisionPercentage.setValue(manProvPer);
		this.manProvisionAmount.setValue(PennantApplicationUtil.formateAmount(manProvAmt, format));

		if (manProv && manProvPer.compareTo(BigDecimal.ZERO) > 0 && manProvAmt.compareTo(BigDecimal.ZERO) == 0) {
			BigDecimal osPrincipal = provision.getOsPrincipal();
			manProvAmt = osPrincipal.multiply(manProvPer.divide(new BigDecimal(100)));
		}

		this.amanProvisionAmount.setVisible(true);
		this.amanProvisionAmount.setValue(String.valueOf(PennantApplicationUtil.formateAmount(manProvAmt, 2)));
		this.recordStatus.setValue(provision.getRecordStatus());

		this.effManualAssetClassification.setValue(provision.getEffManualAssetClass());
		fillComboBox(this.effManualAssetClassification, provision.getEffManualAssetClass(),
				getAssetClassList(provision), "");
		this.effManualAssetSubClassification.setValue(provision.getEffManualAssetSubClass());
		this.newProvisionRegPercentage.setValue(provision.getNewRegProvisionPer());
		this.newProvisionRegAmount
				.setValue(PennantApplicationUtil.formateAmount(provision.getNewRegProvisionAmt(), format));
		this.newProvisionIntPercentage.setValue(provision.getNewIntProvisionPer());
		this.newProvisionIntAmount
				.setValue(PennantApplicationUtil.formateAmount(provision.getNewIntProvisionAmt(), format));

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aAcademic
	 */
	public void doWriteComponentsToBean(Provision provision) {
		logger.debug(Literal.ENTERING);

		List<WrongValueException> wve = new ArrayList<>();
		try {
			provision.setManualProvision(this.manualProvision.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			provision.setOverrideProvision(this.overrideProvision.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.effManualAssetClassification.getSelectedIndex() <= 0) {
				provision.setEffManualAssetClass("");
			} else {
				provision.setEffManualAssetClass(this.effManualAssetClassification.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.effManualAssetSubClassification.getSelectedIndex() <= 0) {
				provision.setEffManualAssetSubClass("");
			} else {
				provision.setEffManualAssetSubClass(this.effManualAssetSubClassification.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			provision.setNewRegProvisionPer(this.newProvisionRegPercentage.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			provision.setNewRegProvisionAmt(this.newProvisionRegAmount.getActualValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			provision.setNewIntProvisionPer(this.newProvisionIntPercentage.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			provision.setNewIntProvisionAmt(this.newProvisionIntAmount.getActualValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (this.overrideProvision.isChecked()) {
			try {
				provision.setManProvsnPer(this.manProvisionPercentage.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				provision.setManProvsnAmt(this.manProvisionAmount.getActualValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		} else {
			provision.setOverrideProvision(false);
			provision.setManProvsnPer(BigDecimal.ZERO);
			provision.setManProvsnAmt(BigDecimal.ZERO);
		}

		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doRemoveValidation() {
		this.effManualAssetClassification.setConstraint("");
		this.effManualAssetSubClassification.setConstraint("");

	}

	private void doClearmessage() {
		this.effManualAssetClassification.setErrorMessage("");
		this.effManualAssetSubClassification.setErrorMessage("");

	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param aAcademic The entity that need to be render.
	 */
	public void doShowDialog(Provision provision) {
		logger.debug(Literal.ENTERING);

		if (isWorkFlowEnabled()) {
			if (StringUtils.isNotBlank(provision.getRecordType())) {
				this.btnNotes.setVisible(true);
			} else {
				this.btnNotes.setVisible(false);
			}
			doEdit();
		} else {
			this.btnCtrl.setInitEdit();
			btnCancel.setVisible(false);
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
			this.manualProvision.setDisabled(true);
		}

		doWriteBeanToComponents(provision);

		if (!enqiryModule) {
			setDialog(DialogType.EMBEDDED);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doSetValidation() {
		logger.debug(Literal.ENTERING);
		if (this.manualProvision.isChecked() && this.effManualAssetClassification.getSelectedIndex() <= 0) {
			throw new WrongValueException(this.effManualAssetClassification,
					Labels.getLabel("STATIC_INVALID", new String[] {
							Labels.getLabel("label_ManualProvisioningDialog_EffManualAssetClassification.value") }));
		}
		if (this.effManualAssetSubClassification.getSelectedIndex() <= 0
				&& this.effManualAssetClassification.getSelectedIndex() > 0) {
			throw new WrongValueException(this.effManualAssetSubClassification,
					Labels.getLabel("STATIC_INVALID", new String[] {
							Labels.getLabel("label_ManualProvisioningDialog_EffManualAssetSubClassification.value") }));
		}

		logger.debug(Literal.LEAVING);
	}

	private void doEdit() {
		logger.debug(Literal.ENTERING);

		this.btnCancel.setVisible(true);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		this.btnDelete.setVisible(false);

		this.manualProvision.setDisabled(isReadOnly("ManualProvisioningDialog_ManProvsnPer"));

		logger.debug("Leaving ");
	}

	public void doSave() {
		logger.debug(Literal.ENTERING);

		final Provision aProvision = new Provision();
		BeanUtils.copyProperties(this.provision, aProvision);
		doSetValidation();

		doWriteComponentsToBean(aProvision);

		boolean isNew = aProvision.isNew();
		String tranType = null;

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aProvision.getRecordType())) {
				aProvision.setVersion(aProvision.getVersion() + 1);
				if (isNew) {
					aProvision.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aProvision.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aProvision.setNewRecord(true);
				}
			}
		} else {
			aProvision.setVersion(aProvision.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aProvision, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	protected boolean doProcess(Provision aProvision, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader;
		String nextRoleCode = "";

		aProvision.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aProvision.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aProvision.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId;
			aProvision.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aProvision.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aProvision);
				}

				if (isNotesMandatory(taskId, aProvision)) {
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

			aProvision.setTaskId(taskId);
			aProvision.setNextTaskId(nextTaskId);
			aProvision.setRoleCode(getRole());
			aProvision.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aProvision, tranType);
			String operationRefs = getServiceOperations(taskId, aProvision);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aProvision, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aProvision, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private AuditHeader getAuditHeader(Provision aProvision, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aProvision.getBefImage(), aProvision);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aProvision.getUserDetails(),
				getOverideMap());
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		AuditHeader aAuditHeader = auditHeader;
		Provision aProvision = (Provision) aAuditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (aAuditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					aAuditHeader = this.provisionService.delete(aAuditHeader);
					deleteNotes = true;
				} else {
					aAuditHeader = this.provisionService.saveOrUpdate(aAuditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					aAuditHeader = this.provisionService.doApprove(aAuditHeader);

					if (aProvision.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					aAuditHeader = this.provisionService.doReject(aAuditHeader);

					if (aProvision.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					aAuditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_ManualProvisioningDialog, aAuditHeader);
					return processCompleted;
				}
			}

			aAuditHeader = ErrorControl.showErrorDetails(this.window_ManualProvisioningDialog, aAuditHeader);
			retValue = aAuditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.provision), true);
				}
			}

			if (retValue == PennantConstants.porcessOVERIDE) {
				aAuditHeader.setOveride(true);
				aAuditHeader.setErrorMessage(null);
				aAuditHeader.setInfoMessage(null);
				aAuditHeader.setOverideMessage(null);
			}
		}

		setOverideMap(aAuditHeader.getOverideMap());
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.provision);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		manualProvisioningListCtrl.search();
	}

	public void onClick$btnSearchCustomer(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());

		final Map<String, Object> map = new HashMap<>();

		map.put("customerDetails", customerDetailsService.getCustomerById(provision.getCustID()));
		map.put("enqiryModule", true);
		map.put("dialogCtrl", this);
		map.put("newRecord", false);
		map.put("CustomerEnq", "CustomerEnq");

		Executions.createComponents(PennantAppUtil.getCustomerPageName(), null, map);

		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onCheck$manualProvision(Event event) throws Exception {
		if (this.manualProvision.isChecked()) {
			this.newProvisionIntPercentage.setDisabled(false);
			this.newProvisionIntPercentage.setReadonly(false);
			this.newProvisionRegPercentage.setDisabled(false);
			this.newProvisionRegPercentage.setReadonly(false);
			this.newProvisionRegAmount.setDisabled(false);
			this.newProvisionRegAmount.setReadonly(false);
			this.newProvisionIntAmount.setDisabled(false);
			this.newProvisionIntAmount.setReadonly(false);
			this.effManualAssetClassification.setDisabled(false);
			this.effManualAssetSubClassification.setDisabled(false);
			fillComboBox(this.effManualAssetSubClassification, this.effManualAssetClassification.getValue(),
					getAssetSubClassCodesList(provision, provision.getEffManualAssetClass()), "");
		} else {
			this.manProvisionPercentage.setValue(BigDecimal.ZERO);
			this.manProvisionAmount.setValue(BigDecimal.ZERO);
			this.amanProvisionAmount.setValue(String.valueOf(BigDecimal.ZERO));
			this.amanProvisionAmount.setVisible(false);
			this.newProvisionIntPercentage.setDisabled(true);
			this.newProvisionIntPercentage.setReadonly(true);
			this.newProvisionRegPercentage.setDisabled(true);
			this.newProvisionRegPercentage.setReadonly(true);
			this.newProvisionRegAmount.setDisabled(true);
			this.newProvisionIntAmount.setDisabled(true);
			this.effManualAssetClassification.setDisabled(true);
			this.effManualAssetSubClassification.setDisabled(true);
			doClearmessage();
		}

	}

	public void onCheck$overrideProvision(Event event) throws Exception {
		if (this.overrideProvision.isChecked()) {
			this.manProvisionPercentage.setDisabled(false);
			this.manProvisionPercentage.setReadonly(false);
			this.manProvisionAmount.setDisabled(false);
			this.manProvisionAmount.setReadonly(false);
		} else {
			this.manProvisionPercentage.setDisabled(true);
			this.manProvisionPercentage.setReadonly(true);
			this.manProvisionAmount.setDisabled(true);
			this.manProvisionAmount.setReadonly(true);
		}
	}

	public void onChange$manProvisionPercentage(Event event) throws Exception {
		BigDecimal manPer = this.manProvisionPercentage.getValue();

		this.amanProvisionAmount.setValue("0");
		this.manProvisionPercentage.setErrorMessage("");

		if (manPer.compareTo(BigDecimal.ZERO) < 0 || manPer.compareTo(new BigDecimal("100")) > 0) {
			throw new WrongValueException(this.manProvisionPercentage, "Percentage should in between 0 to 100.");
		}

		int format = CurrencyUtil.getFormat(provision.getFinCcy());

		BigDecimal osPrincipal = PennantApplicationUtil.unFormateAmount(this.principalOutstanding.getValidateValue(),
				format);

		BigDecimal amount = osPrincipal.multiply(manPer.divide(new BigDecimal(100)));
		this.manProvisionAmount.setValue(BigDecimal.ZERO);
		this.amanProvisionAmount.setVisible(true);
		this.amanProvisionAmount.setValue(String.valueOf(PennantApplicationUtil.formateAmount(amount, format)));
	}

	public void onSelect$effManualAssetClassification(Event event) throws Exception {
		String stage = effManualAssetClassification.getSelectedItem().getLabel();
		ProvisionRuleData prd = provisionDao.getProvisionData(provision.getFinReference());
		prd.setEffNpaClassCode(stage);
		prd.setNpaClassCode(stage);
		prd.setNpaSubClassCode(stage);
		prd.setEffNpaSubClassCode(stage);
		prd.setSecured(true);
		List<ValueLabel> assetSubClassCodesList = getAssetSubClassCodesList(provision, stage);
		if (CollectionUtils.isNotEmpty(assetSubClassCodesList)) {
			this.effManualAssetSubClassification.setValue(stage);
			fillComboBox(this.effManualAssetSubClassification, stage, assetSubClassCodesList, "");
			provisionService.executeProvisionRule(prd, provision);

			BigDecimal regperc = provision.getRegProvsnPer();
			BigDecimal intperc = provision.getIntProvsnPer();

			this.newProvisionIntAmount.setValue(provision.getIntProvsnAmt());
			this.newProvisionIntAmount.setVisible(true);
			this.newProvisionIntPercentage.setValue(intperc);
			this.newProvisionRegPercentage.setValue(regperc);
			this.newProvisionRegAmount.setValue(provision.getRegProvsnAmt());
			this.newProvisionRegAmount.setVisible(true);
		} else {
			this.effManualAssetSubClassification.setValue("");
			this.newProvisionIntPercentage.setValue("0");
			this.newProvisionIntAmount.setValue("0");
			this.newProvisionRegPercentage.setValue("0");
			this.newProvisionRegAmount.setValue("0");
			fillComboBox(this.effManualAssetSubClassification, stage, assetSubClassCodesList, "");

		}

	}

	private List<ValueLabel> getAssetSubClassCodesList(Provision provision, String stage) {

		List<String> assetSubClassCodes = provisionDao.getAssetSubClassCodes(stage);
		this.provision.setAssetSubClassCodes(assetSubClassCodes);
		List<ValueLabel> scc = new ArrayList<>();
		for (String asc : assetSubClassCodes) {
			ValueLabel subclassCodes = new ValueLabel();
			subclassCodes.setLabel(asc);
			subclassCodes.setValue(asc);
			scc.add(subclassCodes);
		}
		return scc;
	}

	private List<ValueLabel> getAssetClassList(Provision provision) {

		List<ValueLabel> acc = new ArrayList<>();

		for (String assetClassCodes : provision.getAssetClassCodes()) {
			ValueLabel codes = new ValueLabel();
			codes.setLabel(assetClassCodes);
			codes.setValue(assetClassCodes);
			acc.add(codes);
		}

		return acc;
	}

	@Autowired
	public void setProvisionService(ProvisionService provisionService) {
		this.provisionService = provisionService;
	}

	@Autowired
	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	@Autowired
	public void setProvisionDao(ProvisionDAO provisionDao) {
		this.provisionDao = provisionDao;
	}

}