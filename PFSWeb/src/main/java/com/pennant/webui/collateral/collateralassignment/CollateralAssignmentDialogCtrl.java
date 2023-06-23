package com.pennant.webui.collateral.collateralassignment;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.pff.extension.CustomerExtension;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.CollateralHeaderDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.ScreenCTL;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class CollateralAssignmentDialogCtrl extends GFCBaseCtrl<CollateralAssignment> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(CollateralAssignmentDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CollateralAssignmentDetailDialog;

	protected Groupbox gb_statusDetails;
	protected ExtendedCombobox collateralRef;
	protected CurrencyBox bankValuation;
	protected Decimalbox assignValuePerc;
	protected Decimalbox availableAssignPerc;
	protected CurrencyBox assignedValue;
	protected CurrencyBox availableAssignValue;
	protected Button collateralInfo;
	protected Button btnNewCollateral;
	protected Button btnEditCollateral;

	private CollateralHeaderDialogCtrl collateralHeaderDialogCtrl;
	private CollateralAssignment collateralAssignment;
	private boolean newRecord;
	private String finType;
	private long customerId;
	private List<CollateralAssignment> collateralAssignments;
	private CollateralSetupService collateralSetupService;
	private FinanceTypeService financeTypeService;
	private Textbox hostReference;

	private List<String> assignCollateralRef;

	private FinanceDetail financeDetail;

	public CollateralAssignmentDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CollateralAssignmentDetailDialog";
	}

	@SuppressWarnings("unchecked")
	public void onCreate$window_CollateralAssignmentDetailDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CollateralAssignmentDetailDialog);

		try {
			// READ OVERHANDED params !
			if (arguments.containsKey("collateralAssignment")) {
				this.collateralAssignment = (CollateralAssignment) arguments.get("collateralAssignment");
				CollateralAssignment befImage = new CollateralAssignment();
				BeanUtils.copyProperties(this.collateralAssignment, befImage);
				this.collateralAssignment.setBefImage(befImage);
				setCollateralAssignment(this.collateralAssignment);
			} else {
				setCollateralAssignment(null);
			}

			// If Finance Related Module
			if (arguments.containsKey("finType")) {
				this.finType = (String) arguments.get("finType");
			}

			if (arguments.containsKey("customerId")) {
				this.customerId = (long) arguments.get("customerId");
			}

			if (arguments.containsKey("assignCollateralRef")) {
				this.assignCollateralRef = (List<String>) arguments.get("assignCollateralRef");
			}

			if (arguments.containsKey("financeDetail")) {
				setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
			}

			// collateralSetupCtrl
			if (arguments.containsKey("collateralHeaderDialogCtrl")) {
				setCollateralHeaderDialogCtrl((CollateralHeaderDialogCtrl) arguments.get("collateralHeaderDialogCtrl"));
				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				this.collateralAssignment.setWorkflowId(0);
				if (arguments.containsKey("roleCode") && !enqiryModule) {
					setRole((String) arguments.get("roleCode"));
					getUserWorkspace().allocateRoleAuthorities(getRole(), "CollateralAssignmentDetailDialog");
				}
			}
			if (!enqiryModule) {
				doLoadWorkFlow(this.collateralAssignment.isWorkflow(), this.collateralAssignment.getWorkflowId(),
						this.collateralAssignment.getNextTaskId());

				doCheckRights();
			}

			doSetFieldProperties();
			doShowDialog(getCollateralAssignment());
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSave(Event event) {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doWriteBeanToComponents(this.collateralAssignment.getBefImage());
		doEdit();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_CollateralAssignmentDetailDialog);
		logger.debug("Leaving" + event.toString());
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
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug("Entering" + event.toString());
		try {
			ScreenCTL.displayNotes(
					getNotes("CollateralAssignment", String.valueOf(getCollateralAssignment().getCollateralRef()),
							getCollateralAssignment().getVersion()),
					this);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for viewing Collateral Information for the selected collateral Reference.
	 * 
	 * @param event
	 */
	public void onClick$collateralInfo(Event event) {
		logger.debug("Entering");

		if (StringUtils.isEmpty(this.collateralRef.getValue())) {
			throw new WrongValueException(this.collateralRef, Labels.getLabel("FIELD_NO_INVALID",
					new String[] { Labels.getLabel("label_CollateralAssignmentDetailDialog_CollateralRef.value") }));
		}
		try {
			// Collateral Details
			Map<String, Object> map = new HashMap<String, Object>();
			CollateralSetup collateralSetup = getCollateralSetup(this.collateralRef.getValue());
			if (collateralSetup == null) {
				collateralSetup = getCollateralSetupService().getCollateralSetupByRef(this.collateralRef.getValue(), "",
						false);
			}
			map.put("collateralSetup", collateralSetup);
			map.put("fromLoan", true);
			map.put("moduleType", PennantConstants.MODULETYPE_ENQ);
			Executions.createComponents("/WEB-INF/pages/Collateral/CollateralSetup/CollateralSetupDialog.zul", null,
					map);
		} catch (Exception e) {
			logger.error("Exception :", e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Getting the collateralsetup from finacedcetils
	 * 
	 * @param reference
	 * @return
	 */
	private CollateralSetup getCollateralSetup(String reference) {
		List<CollateralSetup> collateralSetupList = getFinanceDetail().getCollaterals();
		if (collateralSetupList != null && !collateralSetupList.isEmpty()) {
			for (CollateralSetup detail : collateralSetupList) {
				if (detail.getCollateralRef().equals(reference)) {
					detail.setCustomerDetails(getFinanceDetail().getCustomerDetails());
					return detail;
				}
			}
		}
		return null;
	}

	/**
	 * Collateral NEW and EDIT buttons visibility.
	 * 
	 * @param collateralRef
	 */
	private void setEditCollateralVisibility(String collateralRef) {
		CollateralSetup collateralSetup = getCollateralSetup(collateralRef);
		if (collateralSetup != null) {
			this.btnEditCollateral.setVisible(
					getUserWorkspace().isAllowed("button_CollateralAssignmentDetailDialog_btnEditCollateral"));
			this.collateralInfo.setVisible(!this.btnEditCollateral.isVisible());
			if (getUserWorkspace().isAllowed("button_CollateralAssignmentDetailDialog_btnSave")) {
				this.btnNewCollateral.setVisible(!this.btnEditCollateral.isVisible());
			}
		} else {
			this.btnEditCollateral.setVisible(false);
			this.collateralInfo.setVisible(true);
		}
	}

	/**
	 * Method for Creating a new Collateral.
	 * 
	 * @param event
	 */
	public void onClick$btnNewCollateral(Event event) {
		logger.debug("Entering");
		// Create a new entity.
		CollateralSetup collateralSetup = new CollateralSetup();
		collateralSetup.setNewRecord(true);

		// Display the dialog page.
		final Map<String, Object> arg = new HashMap<String, Object>();
		arg.put("collateralSetup", collateralSetup);
		arg.put("role", getUserWorkspace().getUserRoles());
		arg.put("roleCode", getRole());
		arg.put("fromLoan", true);
		arg.put("dialogCtrl", this);
		arg.put("financeDetail", getFinanceDetail());
		arg.put("window", window_CollateralAssignmentDetailDialog);
		try {
			Executions.createComponents("/WEB-INF/pages/Collateral/CollateralSetup/SelectCollateralTypeDialog.zul",
					null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Creating a new Collateral.
	 * 
	 * @param event
	 */
	public void onClick$btnEditCollateral(Event event) {
		logger.debug("Entering");
		// Create a new entity.
		CollateralSetup collateralSetup = getCollateralSetup(this.collateralRef.getValue());
		// Display the dialog page.
		final Map<String, Object> arg = new HashMap<String, Object>();
		arg.put("collateralSetup", collateralSetup);
		arg.put("role", getUserWorkspace().getUserRoles());
		arg.put("roleCode", getRole());
		arg.put("fromLoan", true);
		arg.put("newRecord", false);
		arg.put("dialogCtrl", this);
		arg.put("financeDetail", getFinanceDetail());
		arg.put("window", window_CollateralAssignmentDetailDialog);
		try {
			Executions.createComponents("/WEB-INF/pages/Collateral/CollateralSetup/CollateralSetupDialog.zul", null,
					arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param collateralAssignment
	 * @throws InterruptedException
	 */
	public void doShowDialog(CollateralAssignment collateralAssignment) throws InterruptedException {
		logger.debug("Entering");

		if (enqiryModule) {
			doReadOnly();
		} else {
			if (isNewRecord()) {
				this.btnCtrl.setInitNew();
				this.collateralRef.focus();
			}
			doEdit();
		}

		try {
			doWriteBeanToComponents(collateralAssignment);
			this.groupboxWf.setVisible(false);
			if (enqiryModule) {
				btnDelete.setVisible(false);
				btnSave.setVisible(false);
				btnEdit.setVisible(false);
				btnEditCollateral.setVisible(false);
				btnNewCollateral.setVisible(false);
				this.assignValuePerc.setReadonly(true);
				this.hostReference.setReadonly(true);
			}
			this.window_CollateralAssignmentDetailDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (isNewRecord()) {
			this.btnCancel.setVisible(false);
			this.collateralRef.setReadonly(isReadOnly("CollateralAssignmentDetailDialog_CollateralRef"));
		} else {
			this.collateralRef.setReadonly(true);
			this.btnCancel.setVisible(true);
			this.btnNewCollateral.setVisible(false);
		}

		this.bankValuation.setDisabled(true);
		this.assignValuePerc.setDisabled(isReadOnly("CollateralAssignmentDetailDialog_AssignValuePerc"));
		this.hostReference.setReadonly(isReadOnly("CollateralAssignmentDetailDialog_HostReference"));
		this.availableAssignPerc.setDisabled(true);
		this.assignedValue.setDisabled(true);
		this.availableAssignValue.setDisabled(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.collateralAssignment.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				this.btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(true);
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for checking Rights for Collateral Fields
	 */
	public boolean isReadOnly(String componentName) {
		return getUserWorkspace().isReadOnly(componentName);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.collateralRef.setReadonly(true);
		this.bankValuation.setDisabled(true);
		this.assignValuePerc.setDisabled(true);
		this.availableAssignPerc.setDisabled(true);
		this.hostReference.setReadonly(true);
		this.assignedValue.setDisabled(true);
		this.availableAssignValue.setDisabled(true);

		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities("CollateralAssignmentDetailDialog", getRole());
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CollateralAssignmentDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CollateralAssignmentDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CollateralAssignmentDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CollateralAssignmentDetailDialog_btnSave"));
		this.btnNewCollateral
				.setVisible(getUserWorkspace().isAllowed("button_CollateralAssignmentDetailDialog_btnNewCollateral"));

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		int formatter = CurrencyUtil.getFormat(getCollateralAssignment().getCollateralCcy());
		this.collateralRef.setProperties("CollateralSetup", "CollateralRef", "", true, 20);
		this.collateralRef.setTextBoxWidth(143);
		this.collateralRef.setWhereClause(getWhereClause().toString());

		this.bankValuation.setProperties(false, formatter);

		this.assignValuePerc.setMaxlength(6);
		this.assignValuePerc.setFormat(PennantApplicationUtil.getAmountFormate(2));
		this.assignValuePerc.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.assignValuePerc.setScale(2);

		this.availableAssignPerc.setMaxlength(6);
		this.availableAssignPerc.setFormat(PennantApplicationUtil.getAmountFormate(2));
		this.availableAssignPerc.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.availableAssignPerc.setScale(2);

		this.hostReference.setMaxlength(50);
		this.assignedValue.setProperties(false, formatter);
		this.availableAssignValue.setProperties(false, formatter);

		setStatusDetails(gb_statusDetails, groupboxWf, south, false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aGuarantorDetail GuarantorDetail
	 */
	public void doWriteBeanToComponents(CollateralAssignment collateralAssignment) {
		logger.debug("Entering");

		int format = CurrencyUtil.getFormat(collateralAssignment.getCollateralCcy());

		this.collateralRef.setValue(collateralAssignment.getCollateralRef());
		setCollateralTypeList(getFinanceDetail().getCollaterals());

		if (StringUtils.isEmpty(collateralAssignment.getCollateralRef())) {
			this.collateralInfo.setVisible(false);
			this.btnEditCollateral.setVisible(false);
		} else {
			setEditCollateralVisibility(collateralAssignment.getCollateralRef());
		}
		this.bankValuation
				.setValue(PennantApplicationUtil.formateAmount(collateralAssignment.getBankValuation(), format));

		BigDecimal curAssignValue = (collateralAssignment.getBankValuation()
				.multiply(collateralAssignment.getAssignPerc())).divide(BigDecimal.valueOf(100), 0,
						RoundingMode.HALF_DOWN);

		this.assignedValue.setValue(PennantApplicationUtil.formateAmount(curAssignValue, format));
		this.assignValuePerc.setValue(collateralAssignment.getAssignPerc());

		BigDecimal totAssignedValue = collateralAssignment.getBankValuation()
				.multiply(collateralAssignment.getTotAssignedPerc())
				.divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);
		BigDecimal availAssignValue = collateralAssignment.getBankValuation().subtract(totAssignedValue)
				.subtract(curAssignValue);
		if (availAssignValue.compareTo(BigDecimal.ZERO) < 0) {
			availAssignValue = BigDecimal.ZERO;
		}
		this.availableAssignValue.setValue(PennantApplicationUtil.formateAmount(availAssignValue, format));

		BigDecimal availAssignPerc = BigDecimal.ZERO;
		if (collateralAssignment.getBankValuation().compareTo(BigDecimal.ZERO) > 0) {
			availAssignPerc = availAssignValue.multiply(new BigDecimal(100))
					.divide(collateralAssignment.getBankValuation(), 2, RoundingMode.HALF_DOWN);
		}
		this.availableAssignPerc.setValue(availAssignPerc);
		this.hostReference.setValue(collateralAssignment.getHostReference());

		logger.debug("Leaving");
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CollateralAssignmentDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param collateralAssignment
	 */
	public void doWriteComponentsToBean(CollateralAssignment collateralAssignment) {
		logger.debug("Entering");
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		int formatter = CurrencyUtil.getFormat(collateralAssignment.getCollateralCcy());

		try {
			collateralAssignment.setCollateralRef(this.collateralRef.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			collateralAssignment.setHostReference(this.hostReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			collateralAssignment.setBankValuation(
					PennantApplicationUtil.unFormateAmount(this.bankValuation.getActualValue(), formatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			BigDecimal totalAssignedPerc = getCollateralAssignment().getTotAssignedPerc();

			if (totalAssignedPerc.compareTo(new BigDecimal(100)) == 0) {
				throw new WrongValueException(this.assignValuePerc,
						Labels.getLabel("label_CollateralAssignment_AssignValuePerc"));
			}

			if ((totalAssignedPerc.add(this.assignValuePerc.getValue())).compareTo(new BigDecimal(100)) > 0) {
				throw new WrongValueException(this.assignValuePerc, Labels.getLabel("FIELD_IS_EQUAL_OR_LESSER",
						new String[] { Labels.getLabel("label_CollateralAssignmentDetailDialog_AssignValuePerc.value"),
								PennantApplicationUtil.formatRate(
										(BigDecimal.valueOf(100).subtract(totalAssignedPerc)).doubleValue(), 2) }));
			}
			collateralAssignment.setAssignPerc(this.assignValuePerc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			collateralAssignment.setAvailableAssignPerc(this.availableAssignPerc.getValue() == null ? BigDecimal.ZERO
					: this.availableAssignPerc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			collateralAssignment.setAssignedValue(
					PennantApplicationUtil.unFormateAmount(this.assignedValue.getActualValue(), formatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			collateralAssignment.setAvailableAssignValue(
					PennantApplicationUtil.unFormateAmount(this.availableAssignValue.getActualValue(), formatter));
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
		setCollateralAssignment(collateralAssignment);
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		doClearMessage();

		if (!this.collateralRef.isReadonly()) {
			this.collateralRef.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CollateralAssignmentDetailDialog_CollateralRef.value"), null, true, true));
		}
		if (!this.assignValuePerc.isDisabled()) {
			this.assignValuePerc.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_CollateralAssignmentDetailDialog_AssignValuePerc.value"), 2, true, false,
					100));
		}
		if (!this.hostReference.isReadonly()) {
			this.hostReference.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CollateralAssignmentDetailDialog_hostReference.value"),
							PennantRegularExpressions.REGEX_ALPHANUM, false));
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");

		this.collateralRef.setConstraint("");
		this.assignValuePerc.setConstraint("");
		this.hostReference.setConstraint("");

		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");

		this.collateralRef.setErrorMessage("");
		this.assignValuePerc.setErrorMessage("");

		logger.debug("Leaving");
	}

	/**
	 * onChanging Facility details
	 * 
	 * @param event
	 */
	public void onFulfill$collateralRef(Event event) {
		logger.debug("Entering");

		Object dataObject = this.collateralRef.getObject();
		CollateralSetup collateralSetup = null;
		this.collateralInfo.setVisible(false);
		if (dataObject != null) {
			if (dataObject instanceof CollateralSetup) {
				collateralSetup = (CollateralSetup) dataObject;
				setAssignment(collateralSetup, true);
			} else {
				// Bank Valuation
				this.bankValuation.setValue(BigDecimal.ZERO);

				// Available Assignment value
				this.availableAssignValue.setValue(BigDecimal.ZERO);

				// Assigned Value
				this.assignedValue.setValue(BigDecimal.ZERO);

				// Available Assign Value Percentage
				this.availableAssignPerc.setValue(BigDecimal.ZERO);
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Filling the collateralk assignments.
	 * 
	 * @param collateralSetup
	 */
	public void doFillAssignment(CollateralSetup collateralSetup) {
		boolean isNewList = false;
		List<CollateralSetup> collateralSetupList = getFinanceDetail().getCollaterals();
		if (collateralSetupList == null) {
			collateralSetupList = new ArrayList<CollateralSetup>();
			getFinanceDetail().setCollaterals(collateralSetupList);
			isNewList = true;
		}

		boolean recAdded = false;
		if (isNewList) {
			collateralSetupList.add(collateralSetup);
		} else {
			for (CollateralSetup detail : collateralSetupList) {
				if (collateralSetup.getCollateralRef().equals(detail.getCollateralRef())) {
					BeanUtils.copyProperties(collateralSetup, detail);
					recAdded = true;
					break;
				}
			}
			if (!recAdded) {
				collateralSetupList.add(collateralSetup);
			}
		}
		getCollateralHeaderDialogCtrl().setCollateralSetups(collateralSetupList);
		setCollateralTypeList(getFinanceDetail().getCollaterals());
		getCollateralHeaderDialogCtrl().setCollateralSetups(collateralSetupList);
		this.collateralRef.setValue(collateralSetup.getCollateralRef());
		setAssignment(collateralSetup, false);
	}

	/**
	 * Setting the collateral assignment from new collateralsetup
	 * 
	 * @param collateralSetup
	 */
	private void setAssignment(CollateralSetup collateralSetup, boolean resetPerc) {
		// Calculate Field from Collateral Setup
		int formatter = CurrencyUtil.getFormat(collateralSetup.getCollateralCcy());

		// Bank Valuation
		this.bankValuation
				.setValue(PennantApplicationUtil.formateAmount(collateralSetup.getBankValuation(), formatter));

		// Available Assignment value : Total Assignments value of the
		// Collateral Excluding Current Reference (Commitment/Finance)
		BigDecimal totAssignedPerc = getCollateralSetupService().getAssignedPerc(collateralSetup.getCollateralRef(),
				"");// TODO:Add

		BigDecimal curAssignValue = collateralSetup.getBankValuation()
				.multiply(this.assignValuePerc.getValue() == null ? BigDecimal.ZERO : this.assignValuePerc.getValue())
				.divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);
		BigDecimal totAssignedValue = collateralSetup.getBankValuation().multiply(totAssignedPerc)
				.divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);
		BigDecimal availAssignValue = collateralSetup.getBankValuation().subtract(totAssignedValue)
				.subtract(curAssignValue);
		if (availAssignValue.compareTo(BigDecimal.ZERO) < 0) {
			availAssignValue = BigDecimal.ZERO;
		}
		this.availableAssignValue.setValue(PennantApplicationUtil.formateAmount(availAssignValue, formatter));

		// Assigned Value
		this.assignedValue.setValue(PennantApplicationUtil.formateAmount(curAssignValue, formatter));

		// Available Assign Value Percentage
		BigDecimal availAssignPerc = BigDecimal.ZERO;
		if (collateralSetup.getBankValuation().compareTo(BigDecimal.ZERO) > 0) {
			availAssignPerc = availAssignValue.multiply(new BigDecimal(100)).divide(collateralSetup.getBankValuation(),
					0, RoundingMode.HALF_DOWN);
		}
		this.availableAssignPerc.setValue(availAssignPerc);

		// Bean Setup for future usage
		getCollateralAssignment().setCollateralCcy(collateralSetup.getCollateralCcy());
		getCollateralAssignment().setBankValuation(collateralSetup.getBankValuation());
		getCollateralAssignment().setAssignedValue(curAssignValue);
		getCollateralAssignment().setAvailableAssignValue(availAssignValue);
		getCollateralAssignment().setAvailableAssignPerc(availAssignPerc);
		getCollateralAssignment().setCollateralValue(collateralSetup.getCollateralValue());
		getCollateralAssignment().setTotAssignedPerc(totAssignedPerc);
		getCollateralAssignment().setCollateralType(collateralSetup.getCollateralType());
		this.collateralInfo.setVisible(true);
		setEditCollateralVisibility(collateralSetup.getCollateralRef());

		// Resetting Collateral Assignment Percentage based on Requirement
		if (resetPerc) {
			resetCollateralAssignPerc(collateralSetup);
		}

	}

	/**
	 * Assigned Value field Percentage based on Loan Value and Collateral availability
	 * 
	 * @param collateralSetup
	 */
	public void resetCollateralAssignPerc(CollateralSetup collateralSetup) {
		logger.debug("Entering");
		// No Collateral Adjustment
		if (StringUtils.equals(ImplementationConstants.COLLATERAL_ADJ, CollateralConstants.COLLATERAL_NO_ADJ)) {
			return;
		}

		// Full Collateral Adjustment Calculation
		if (StringUtils.equals(ImplementationConstants.COLLATERAL_ADJ, CollateralConstants.COLLATERAL_FULL_ADJ)) {
			this.assignValuePerc.setValue(this.availableAssignPerc.getValue());
		}

		// Requested Collateral Adjustment Calculation
		if (StringUtils.equals(ImplementationConstants.COLLATERAL_ADJ, CollateralConstants.COLLATERAL_REQ_ADJ)) {

			BigDecimal utilizedAmt = BigDecimal.ZERO;

			FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();

			if (PennantConstants.COLLATERAL_LTV_CHECK_FINAMT.equals(financeType.getFinLTVCheck())) {
				utilizedAmt = getFinanceDetail().getFinScheduleData().getFinanceMain().getFinAssetValue();

			} else {
				utilizedAmt = getFinanceDetail().getFinScheduleData().getFinanceMain().getFinAmount();
			}

			BigDecimal assignedVal = BigDecimal.ZERO;

			// If already exists any Collateral
			if (collateralAssignments != null) {
				for (CollateralAssignment assignment : collateralAssignments) {
					assignedVal = assignedVal.add((assignment.getAssignPerc().multiply(assignment.getBankValuation()))
							.divide(new BigDecimal(100), 2, RoundingMode.UP));
				}
			}
			utilizedAmt = utilizedAmt.subtract(assignedVal);
			BigDecimal reqAssignPerc = BigDecimal.ZERO;
			// If loan amount is higher than Collateral amount
			if (utilizedAmt.compareTo(collateralSetup.getBankValuation()) > 0) {
				reqAssignPerc = BigDecimal.valueOf(100);
			} else {
				// If loan amount is lesser than Collateral amount
				reqAssignPerc = utilizedAmt.multiply(new BigDecimal(100)).divide(collateralSetup.getBankValuation(), 2,
						RoundingMode.UP);
			}
			if (reqAssignPerc.compareTo(BigDecimal.valueOf(100)) > 0) {
				reqAssignPerc = BigDecimal.valueOf(100);
			}
			this.assignValuePerc.setValue(reqAssignPerc);
		}

		Events.sendEvent("onChange", assignValuePerc, null);
		logger.debug("Leaving");
	}

	/**
	 * Method for calculation of assigned Value based on Percentage entered
	 * 
	 * @param event
	 */
	public void onChange$assignValuePerc(Event event) {
		logger.debug("Entering");

		int formatter = 0;
		if (StringUtils.isNotEmpty(getCollateralAssignment().getCollateralCcy())) {
			formatter = CurrencyUtil.getFormat(getCollateralAssignment().getCollateralCcy());
		}

		// Available Assignment value
		BigDecimal assignValuePerc = this.assignValuePerc.getValue();
		if (assignValuePerc == null) {
			assignValuePerc = BigDecimal.ZERO;
		}
		BigDecimal totAssignedPerc = getCollateralAssignment().getTotAssignedPerc();
		BigDecimal curAssignValue = getCollateralAssignment().getBankValuation().multiply(assignValuePerc)
				.divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);
		BigDecimal totAssignedValue = getCollateralAssignment().getBankValuation().multiply(totAssignedPerc)
				.divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);
		BigDecimal availAssignValue = getCollateralAssignment().getBankValuation().subtract(totAssignedValue)
				.subtract(curAssignValue);
		if (availAssignValue.compareTo(BigDecimal.ZERO) < 0) {
			availAssignValue = BigDecimal.ZERO;
		}
		this.availableAssignValue.setValue(PennantApplicationUtil.formateAmount(availAssignValue, formatter));

		// Assigned Value
		this.assignedValue.setValue(PennantApplicationUtil.formateAmount(curAssignValue, formatter));

		// Available Assign Value Percentage
		BigDecimal availAssignPerc = BigDecimal.ZERO;
		if (getCollateralAssignment().getBankValuation().compareTo(BigDecimal.ZERO) > 0) {
			availAssignPerc = availAssignValue.multiply(new BigDecimal(100))
					.divide(getCollateralAssignment().getBankValuation(), 2, RoundingMode.HALF_DOWN);
		}
		this.availableAssignPerc.setValue(availAssignPerc);

		logger.debug("Leaving");
	}

	protected void onDoDelete(final CollateralAssignment collateralAssignment) {
		String tranType = PennantConstants.TRAN_WF;
		if (StringUtils.isBlank(collateralAssignment.getRecordType())) {
			collateralAssignment.setVersion(collateralAssignment.getVersion() + 1);
			collateralAssignment.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			collateralAssignment.setNewRecord(true);

			if (isWorkFlowEnabled()) {
				tranType = PennantConstants.TRAN_WF;
			} else {
				tranType = PennantConstants.TRAN_DEL;
			}
		}
		try {
			tranType = PennantConstants.TRAN_DEL;
			AuditHeader auditHeader = newAssignmentDetailProcess(collateralAssignment, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_CollateralAssignmentDetailDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				if (getCollateralHeaderDialogCtrl() != null) {
					getCollateralHeaderDialogCtrl().doFillCollateralDetails(this.collateralAssignments, true);
				}
				closeDialog();
			}
		} catch (DataAccessException e) {
			MessageUtil.showError(e);
		}
	}

	private void doDelete() throws InterruptedException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		logger.debug(Literal.ENTERING);

		final CollateralAssignment collateralAssignment = new CollateralAssignment();
		BeanUtils.copyProperties(getCollateralAssignment(), collateralAssignment);

		doDelete(collateralAssignment.getCollateralRef(), collateralAssignment);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		this.collateralRef.setValue("");
		this.assignValuePerc.setValue("");
		logger.debug("Leaving");
	}

	public void doSave() {
		logger.debug("Entering");

		final CollateralAssignment aCollateralAssignment = new CollateralAssignment();
		BeanUtils.copyProperties(getCollateralAssignment(), aCollateralAssignment);

		// force validation, if on, than execute by component.getValue()
		doSetValidation();

		// fill the DocumentDetails object with the components data
		doWriteComponentsToBean(aCollateralAssignment);
		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		String tranType = "";

		if (isNewRecord()) {
			aCollateralAssignment.setVersion(1);
			aCollateralAssignment.setRecordType(PennantConstants.RCD_ADD);
		} else {
			tranType = PennantConstants.TRAN_UPD;
		}

		if (StringUtils.isBlank(aCollateralAssignment.getRecordType())) {
			aCollateralAssignment.setVersion(aCollateralAssignment.getVersion() + 1);
			aCollateralAssignment.setRecordType(PennantConstants.RCD_UPD);
		}

		if (aCollateralAssignment.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
			tranType = PennantConstants.TRAN_ADD;
		} else if (aCollateralAssignment.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
			tranType = PennantConstants.TRAN_UPD;
		}

		// save it to database
		try {
			AuditHeader auditHeader = newAssignmentDetailProcess(aCollateralAssignment, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_CollateralAssignmentDetailDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				if (getCollateralHeaderDialogCtrl() != null) {
					getCollateralHeaderDialogCtrl().doFillCollateralDetails(this.collateralAssignments, true);
				}
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}

		if (assignCollateralRef != null) {
			assignCollateralRef.add(aCollateralAssignment.getCollateralRef());
		}
		logger.debug("Leaving");
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
	private AuditHeader newAssignmentDetailProcess(CollateralAssignment aCollateralAssignment, String tranType) {
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aCollateralAssignment, tranType);
		collateralAssignments = new ArrayList<CollateralAssignment>();

		String[] valueParm = new String[1];
		String[] errParm = new String[1];

		valueParm[0] = aCollateralAssignment.getCollateralRef();
		errParm[0] = PennantJavaUtil.getLabel("label_CollateralRef") + ":" + valueParm[0];

		if (getCollateralHeaderDialogCtrl().getCollateralAssignments() != null
				&& !getCollateralHeaderDialogCtrl().getCollateralAssignments().isEmpty()) {
			for (int i = 0; i < getCollateralHeaderDialogCtrl().getCollateralAssignments().size(); i++) {
				CollateralAssignment collateralAssignment = getCollateralHeaderDialogCtrl().getCollateralAssignments()
						.get(i);

				if (collateralAssignment.getCollateralRef().equals(aCollateralAssignment.getCollateralRef())) { // Both
																												// Current
																												// and
																												// Existing
																												// list
																												// Reference
																												// same

					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (aCollateralAssignment.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aCollateralAssignment.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							collateralAssignments.add(aCollateralAssignment);
						} else if (aCollateralAssignment.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aCollateralAssignment.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aCollateralAssignment.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							collateralAssignments.add(aCollateralAssignment);
						} else if (aCollateralAssignment.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							for (int j = 0; j < getCollateralHeaderDialogCtrl().getCollateralAssignments()
									.size(); j++) {
								CollateralAssignment assignment = getCollateralHeaderDialogCtrl()
										.getCollateralAssignments().get(j);
								if (assignment.getCollateralRef().equals(aCollateralAssignment.getCollateralRef())) {
									collateralAssignments.add(assignment);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							collateralAssignments.add(collateralAssignment);
						}
					}
				} else {
					collateralAssignments.add(collateralAssignment);
				}
			}
		}
		if (!recordAdded) {
			collateralAssignments.add(aCollateralAssignment);
		}
		return auditHeader;
	}

	private void setCollateralTypeList(List<CollateralSetup> collateralSetupList) {
		List<CollateralSetup> csList = collateralSetupService.getCollateralSetupByCustomer(customerId, finType);

		if (CollectionUtils.isEmpty(csList)) {
			csList = new ArrayList<>();
		}

		if (CollectionUtils.isNotEmpty(collateralSetupList)) {
			csList.addAll(collateralSetupList);
		}

		if (CollectionUtils.isEmpty(csList)) {
			csList = null;
		}

		this.collateralRef.setList(csList);
	}

	private StringBuilder getWhereClause() {
		String collateralTypes = financeTypeService.getAllowedCollateralTypes(finType);

		StringBuilder whereClause = new StringBuilder();
		if (StringUtils.isNotEmpty(collateralTypes)) {
			whereClause.append("(CollateralType in ('");
			whereClause.append(collateralTypes.replace(",", "' , '"));
			whereClause.append("')) AND");
		}

		whereClause.append(" ((DepositorId in( ");
		whereClause.append(getInnerQuery()).append(")) ");
		whereClause
				.append(" OR (CollateralRef IN (Select CollateralRef from CollateralThirdParty WHERE CustomerId in(");
		whereClause.append(getInnerQuery()).append("))) ) ");
		// Adding Where Condition to Filter Not Collateral References which are not allowed to Multi Assignment
		// in Loans
		whereClause.append(" AND (((MultiLoanAssignment = 0 and CollateralRef NOT IN (");
		whereClause.append(" Select CollateralRef From CollateralAssignment");
		whereClause.append(" Union All");
		whereClause.append(" Select CollateralRef From CollateralAssignment_Temp)) ");
		whereClause.append(" OR MultiLoanAssignment = 1))  ");
		whereClause.append(" AND (expirydate is Null");
		whereClause.append(" OR expirydate >= '" + JdbcUtil.getDate(SysParamUtil.getAppDate()) + "')");

		return whereClause;
	}

	private String getInnerQuery() {
		StringBuilder sql = new StringBuilder();

		if (CustomerExtension.CUST_CORE_BANK_ID) {
			sql.append(" Select distinct CustID From (");
			sql.append(
					" Select CustID from Customers Where CustCoreBank = (Select CustCoreBank From Customers Where CustID = "
							+ customerId + ")");
			sql.append(" Union All");
			sql.append(
					" Select CustID from Customers_Temp Where CustCoreBank = (Select CustCoreBank From Customers_Temp Where CustID = "
							+ customerId + ")");
			sql.append(") T");
		} else {

			sql.append(" Select distinct CustID From (");
			sql.append(" Select CustID from Customers Where CustID = " + customerId);
			sql.append(" Union All ");
			sql.append(" Select CustID from Customers_Temp Where CustID = " + customerId);
			sql.append(") T");

		}

		return sql.toString();
	}

	// WorkFlow Components
	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(CollateralAssignment detail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, detail.getBefImage(), detail);
		return new AuditHeader(String.valueOf(detail.getCollateralRef()), null, null, null, auditDetail,
				detail.getUserDetails(), getOverideMap());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public CollateralHeaderDialogCtrl getCollateralHeaderDialogCtrl() {
		return collateralHeaderDialogCtrl;
	}

	public void setCollateralHeaderDialogCtrl(CollateralHeaderDialogCtrl collateralHeaderDialogCtrl) {
		this.collateralHeaderDialogCtrl = collateralHeaderDialogCtrl;
	}

	public CollateralAssignment getCollateralAssignment() {
		return collateralAssignment;
	}

	public void setCollateralAssignment(CollateralAssignment collateralAssignment) {
		this.collateralAssignment = collateralAssignment;
	}

	public List<CollateralAssignment> getCollateralAssignmentList() {
		return collateralAssignments;
	}

	public void setCollateralAssignmentList(List<CollateralAssignment> collateralAssignmentList) {
		this.collateralAssignments = collateralAssignmentList;
	}

	public CollateralSetupService getCollateralSetupService() {
		return collateralSetupService;
	}

	public void setCollateralSetupService(CollateralSetupService collateralSetupService) {
		this.collateralSetupService = collateralSetupService;
	}

	public FinanceTypeService getFinanceTypeService() {
		return financeTypeService;
	}

	public void setFinanceTypeService(FinanceTypeService financeTypeService) {
		this.financeTypeService = financeTypeService;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

}
