package com.pennant.webui.finance.financemain.stepfinance;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class FinStepPolicyDetailDialogCtrl extends GFCBaseCtrl<FinanceStepPolicyDetail> {
	private static final long serialVersionUID = -4626382073313654611L;
	private static final Logger logger = LogManager.getLogger(FinStepPolicyDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinStepPolicyDialog; // autoWired

	protected Space space_FinStepPolicyDialog_FinAmount; // autoWired
	protected Intbox stepFinTenor; // autoWired

	protected Space space_FinStepPolicyDialog_StepNumber; // autoWired
	protected Intbox stepNumber; // autoWired

	protected Space space_FinStepPolicyDialog_TenorSplitPerc; // autoWired
	protected Decimalbox tenorSplitPerc; // autoWired

	protected Space space_FinStepPolicyDialog_InstallMents; // autoWired
	protected Intbox installments; // autoWired

	protected Space space_FinStepPolicyDialog_RateMargin; // autoWired
	protected Decimalbox rateMargin; // autoWired

	protected Space space_FinStepPolicyDialog_EMIStepPerc; // autoWired
	protected Decimalbox eMIStepPerc; // autoWired

	protected CurrencyBox steppedEMI; // autoWired

	protected Row row_rateMargin;
	protected Row row_steppedEMI;
	protected Label label_FinStepPolicyDialog_AutoCal;
	protected Hbox hbox_autoCal;
	protected Checkbox autoCal;

	// not auto wired variables
	private FinanceStepPolicyDetail financeStepPolicyDetail; // overHanded per
																// parameter

	private transient boolean validationOn;

	private boolean newRecord = false;
	private boolean newFinStep = false;
	private StepDetailDialogCtrl stepDetailDialogCtrl;
	private List<FinanceStepPolicyDetail> finStepPolicyDetails;
	private String moduleType = "";
	private String userRole = "";
	private int ccyFormatter = 0;
	private double totTenorPerc = 0.00;
	private boolean alwDeletion = true;
	private boolean alwManualStep = false;
	private int rpySteps = 0;
	private int grcSteps = 0;

	private FinanceDetail financeDetail = null;
	private FinScheduleData finScheduleData = null;
	private FinanceMain financeMain = null;
	private boolean isEnquiry = false;

	/**
	 * default constructor.<br>
	 */
	public FinStepPolicyDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinStepPolicyDetailDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected CustomerEmploymentDetail object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_FinStepPolicyDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinStepPolicyDialog);

		// READ OVERHANDED parameters !
		if (arguments.containsKey("financeStepPolicyDetail")) {
			this.financeStepPolicyDetail = (FinanceStepPolicyDetail) arguments.get("financeStepPolicyDetail");
			FinanceStepPolicyDetail befImage = new FinanceStepPolicyDetail();
			BeanUtils.copyProperties(this.financeStepPolicyDetail, befImage);
			this.financeStepPolicyDetail.setBefImage(befImage);
			setFinanceStepPolicyDetail(this.financeStepPolicyDetail);
		} else {
			setFinanceStepPolicyDetail(null);
		}

		if (arguments.containsKey("moduleType")) {
			this.moduleType = (String) arguments.get("moduleType");
		}
		if (arguments.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
		}

		if (arguments.containsKey("enquiryModule")) {
			isEnquiry = (Boolean) arguments.get("enquiryModule");
		}

		if (arguments.containsKey("ccyFormatter")) {
			this.ccyFormatter = (Integer) arguments.get("ccyFormatter");
		}

		if (arguments.containsKey("totTenorPerc")) {
			this.totTenorPerc = (Double) arguments.get("totTenorPerc");
		}

		if (arguments.containsKey("alwDeletion")) {
			this.alwDeletion = (Boolean) arguments.get("alwDeletion");
		}

		if (arguments.containsKey("alwManualStep")) {
			this.alwManualStep = (Boolean) arguments.get("alwManualStep");
		}

		if (arguments.containsKey("rpySteps")) {
			this.rpySteps = (int) arguments.get("rpySteps");
		}

		if (arguments.containsKey("grcSteps")) {
			this.grcSteps = (int) arguments.get("grcSteps");
		}

		if (arguments.containsKey("stepDetailDialogCtrl")) {

			setStepDetailDialogCtrl((StepDetailDialogCtrl) arguments.get("stepDetailDialogCtrl"));
			setNewFinStep(true);

			if (arguments.containsKey("newRecord")) {
				setNewRecord((Boolean) arg.get("newRecord"));
			} else {
				setNewRecord(getFinanceStepPolicyDetail().isNewRecord());
			}
			this.financeStepPolicyDetail.setWorkflowId(0);
			if (arguments.containsKey("roleCode") && !isEnquiry) {
				userRole = arguments.get("roleCode").toString();
				getUserWorkspace().allocateRoleAuthorities(userRole, "FinStepPolicyDetailDialog");
			}
		}

		doLoadWorkFlow(this.financeStepPolicyDetail.isWorkflow(), this.financeStepPolicyDetail.getWorkflowId(),
				this.financeStepPolicyDetail.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "FinStepPolicyDetailDialog");
		}
		/* set components visible dependent of the users rights */
		doCheckRights();

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getFinanceStepPolicyDetail());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.stepNumber.setMaxlength(2);
		this.stepNumber.setStyle("text-align:right;");
		this.stepFinTenor.setStyle("text-align:right;");
		this.installments.setStyle("text-align:right;");
		this.tenorSplitPerc.setMaxlength(6);
		this.tenorSplitPerc.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.installments.setMaxlength(3);
		this.eMIStepPerc.setMaxlength(6);
		this.eMIStepPerc.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.rateMargin.setMaxlength(14);
		this.rateMargin.setFormat(PennantApplicationUtil.getRateFormate(9));
		this.steppedEMI.setProperties(true, ccyFormatter);
		this.steppedEMI.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.stepFinTenor.setMaxlength(4);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
			this.south.setHeight("0px");
		}
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
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(false);
		this.btnEdit.setVisible(false);

		if (alwDeletion) {
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinStepPolicyDetailDialog_btnDelete"));
		} else {
			this.btnDelete.setVisible(false);
		}
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinStepPolicyDetailDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
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
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_FinStepPolicyDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
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
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.financeStepPolicyDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinStepPolicy CustomerEmploymentDetail
	 */
	public void doWriteBeanToComponents(FinanceStepPolicyDetail aFinStepPolicy) {
		logger.debug("Entering");
		if (StringUtils.equals(aFinStepPolicy.getStepSpecifier(), PennantConstants.STEP_SPECIFIER_REG_EMI)) {
			this.stepFinTenor
					.setValue(getStepDetailDialogCtrl().getFinScheduleData().getFinanceMain().getNumberOfTerms());
		} else {
			this.stepFinTenor.setValue(getStepDetailDialogCtrl().getFinScheduleData().getFinanceMain().getGraceTerms());
		}
		this.stepNumber.setValue(aFinStepPolicy.getStepNo());
		this.tenorSplitPerc.setValue(aFinStepPolicy.getTenorSplitPerc());
		this.installments.setValue(aFinStepPolicy.getInstallments());
		this.eMIStepPerc.setValue(aFinStepPolicy.getEmiSplitPerc());
		this.steppedEMI.setValue(PennantApplicationUtil.formateAmount(aFinStepPolicy.getSteppedEMI(), ccyFormatter));
		this.rateMargin.setValue(aFinStepPolicy.getRateMargin());
		this.autoCal.setChecked(aFinStepPolicy.isAutoCal());
		this.recordStatus.setValue(aFinStepPolicy.getRecordStatus());

		disableComponent(aFinStepPolicy.getStepNo());

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinStepPolicy
	 */
	public void doWriteComponentsToBean(FinanceStepPolicyDetail aFinStepPolicy) {
		logger.debug("Entering");
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aFinStepPolicy.setStepNo(this.stepNumber.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinStepPolicy.setTenorSplitPerc(this.tenorSplitPerc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinStepPolicy.setInstallments(this.installments.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinStepPolicy.setEmiSplitPerc(this.eMIStepPerc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinStepPolicy.setSteppedEMI(
					PennantApplicationUtil.unFormateAmount(this.steppedEMI.getActualValue(), ccyFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinStepPolicy
					.setRateMargin(this.rateMargin.getValue() == null ? BigDecimal.ZERO : this.rateMargin.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinStepPolicy.setAutoCal(this.autoCal.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aFinStepPolicy.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aFinStepPolicy
	 */
	public void doShowDialog(FinanceStepPolicyDetail aFinStepPolicy) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
		} else {
			this.stepNumber.focus();
			if (isNewFinStep()) {
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
			doWriteBeanToComponents(aFinStepPolicy);

			doCheckEnquiry();

			FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();
			FinanceMain aFinanceMain = financeDetail.getFinScheduleData().getFinanceMain();
			String calcOfSteps = StringUtils.isNotBlank(aFinanceMain.getCalcOfSteps()) ? aFinanceMain.getCalcOfSteps()
					: financeType.getCalcOfSteps();
			if (StringUtils.equals(calcOfSteps, PennantConstants.STEPPING_CALC_AMT)) {
				row_rateMargin.setVisible(false);
				row_steppedEMI.setVisible(true);
			} else if (StringUtils.equals(calcOfSteps, PennantConstants.STEPPING_CALC_PERC)) {
				row_steppedEMI.setVisible(false);
				row_rateMargin.setVisible(true);
			}

			this.window_FinStepPolicyDialog.setHeight("35%");
			this.window_FinStepPolicyDialog.setWidth("80%");
			this.groupboxWf.setVisible(false);
			this.window_FinStepPolicyDialog.doModal();

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	private void doCheckEnquiry() {
		if ("ENQ".equals(this.moduleType) || isEnquiry) {
			this.tenorSplitPerc.setDisabled(true);
			this.installments.setReadonly(true);
			this.eMIStepPerc.setDisabled(true);
			this.steppedEMI.setReadonly(true);
			this.rateMargin.setDisabled(true);
			this.autoCal.setDisabled(true);
		}
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		setValidationOn(true);
		FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();
		FinanceMain aFinanceMain = financeDetail.getFinScheduleData().getFinanceMain();
		String calcOfSteps = StringUtils.isNotBlank(aFinanceMain.getCalcOfSteps()) ? aFinanceMain.getCalcOfSteps()
				: financeType.getCalcOfSteps();
		if (!this.stepNumber.isReadonly()) {
			this.stepNumber.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_FinStepPolicyDialog_StepNumber.value"), true, false, 1, 100));
		}
		if (!this.tenorSplitPerc.isDisabled()) {
			this.tenorSplitPerc.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_FinStepPolicyDialog_EMIStepPerc.value"), 2, true, false, this.totTenorPerc));
		}
		if (!this.installments.isReadonly()) {
			this.installments.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_FinStepPolicyDialog_Installments.value"), true, false));
		}
		if (!this.rateMargin.isDisabled()) {
			this.rateMargin.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_FinStepPolicyDialog_RateMargin.value"), 9, false, true, -9999, 9999));
		}
		if (!this.eMIStepPerc.isDisabled()) {
			if (StringUtils.equals(calcOfSteps, PennantConstants.STEPPING_CALC_PERC)) {
				this.eMIStepPerc.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinStepPolicyDialog_EMIStepPerc.value"), 2, true, false, 999));
			} else {
				this.eMIStepPerc.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinStepPolicyDialog_EMIStepPerc.value"), 2, false, false, 999));
			}
		}
		if (this.row_steppedEMI.isVisible() && !this.steppedEMI.isDisabled() && !this.steppedEMI.isReadonly()
				&& financeStepPolicyDetail != null) {
			if (StringUtils.equals(financeStepPolicyDetail.getStepSpecifier(),
					PennantConstants.STEP_SPECIFIER_REG_EMI)) {
				this.steppedEMI.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinStepPolicyDialog_SteppedEMI.value"), 2, true, false));
			} else if (StringUtils.equals(financeStepPolicyDetail.getStepSpecifier(),
					PennantConstants.STEP_SPECIFIER_GRACE)) {
				this.steppedEMI.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinStepPolicyDialog_SteppedEMI.value"), 2, false, false));
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.stepNumber.setConstraint("");
		this.tenorSplitPerc.setConstraint("");
		this.installments.setConstraint("");
		this.rateMargin.setConstraint("");
		this.eMIStepPerc.setConstraint("");
		this.steppedEMI.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.stepNumber.setErrorMessage("");
		this.tenorSplitPerc.setErrorMessage("");
		this.installments.setErrorMessage("");
		this.rateMargin.setErrorMessage("");
		this.eMIStepPerc.setErrorMessage("");
		logger.debug("Leaving");
	}

	public void onChange$stepNumber(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		this.stepNumber.setErrorMessage("");
		int stpNo = this.stepNumber.intValue();
		disableComponent(stpNo);
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void disableComponent(int stpNo) {
		if (getFinanceStepPolicyDetail() != null && StringUtils.equals(getFinanceStepPolicyDetail().getStepSpecifier(),
				PennantConstants.STEP_SPECIFIER_REG_EMI)) {
			if (stpNo == rpySteps) {
				this.steppedEMI.setReadonly(true);
				this.steppedEMI.setValue(BigDecimal.ZERO);
			} else {
				this.steppedEMI.setReadonly(isReadOnly("FinStepPolicyDetailDialog_SteppedEMI"));
			}
		} else if (getFinanceStepPolicyDetail() != null && StringUtils
				.equals(getFinanceStepPolicyDetail().getStepSpecifier(), PennantConstants.STEP_SPECIFIER_GRACE)) {
			if (stpNo == grcSteps) {
				this.label_FinStepPolicyDialog_AutoCal.setVisible(true);
				this.hbox_autoCal.setVisible(true);
				if (this.autoCal.isChecked()) {
					this.steppedEMI.setReadonly(true);
					this.steppedEMI.setValue(BigDecimal.ZERO);
				} else {
					this.steppedEMI.setReadonly(isReadOnly("FinStepPolicyDetailDialog_SteppedEMI"));
				}
			} else {
				this.label_FinStepPolicyDialog_AutoCal.setVisible(false);
				this.hbox_autoCal.setVisible(false);
				this.steppedEMI.setReadonly(isReadOnly("FinStepPolicyDetailDialog_SteppedEMI"));
				this.autoCal.setChecked(false);
			}
		}
	}

	public void onCheck$autoCal(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		int stpNo = this.stepNumber.intValue();
		disableComponent(stpNo);
		logger.debug(Literal.LEAVING + event.toString());
	}

	protected boolean doCustomDelete(final FinanceStepPolicyDetail aFinStepPolicy, String tranType) {
		if (isNewFinStep()) {
			tranType = PennantConstants.TRAN_DEL;
			AuditHeader auditHeader = newFinStepPolicyProcess(aFinStepPolicy, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_FinStepPolicyDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				if (aFinStepPolicy.getStepSpecifier().equals(PennantConstants.STEP_SPECIFIER_GRACE)) {
					stepDetailDialogCtrl.doFillStepDetaisForGrace(this.finStepPolicyDetails);
				} else {
					stepDetailDialogCtrl.doFillStepDetais(this.finStepPolicyDetails);
				}
				stepDetailDialogCtrl.setDataChanged(true);
				return true;
			}
		}

		return false;
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final FinanceStepPolicyDetail aFinStepPolicy = new FinanceStepPolicyDetail();
		BeanUtils.copyProperties(getFinanceStepPolicyDetail(), aFinStepPolicy);

		doDelete(String.valueOf(aFinStepPolicy.getStepNo()), aFinStepPolicy);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering ");

		this.stepFinTenor.setDisabled(true);
		this.installments.setReadonly(isReadOnly("FinStepPolicyDetailDialog_Installmensts"));
		this.rateMargin.setDisabled(isReadOnly("FinStepPolicyDetailDialog_RateMargin"));
		this.autoCal.setDisabled(isReadOnly("FinStepPolicyDetailDialog_AutoCal"));

		this.space_FinStepPolicyDialog_TenorSplitPerc.setSclass("");

		if (alwManualStep) {
			this.stepNumber.setReadonly(false);
			this.tenorSplitPerc.setDisabled(true);
		} else {
			this.space_FinStepPolicyDialog_StepNumber.setSclass("");
			this.space_FinStepPolicyDialog_StepNumber.setSclass("");
			this.space_FinStepPolicyDialog_InstallMents.setSclass("");
			this.space_FinStepPolicyDialog_EMIStepPerc.setSclass("");
			this.stepNumber.setReadonly(true);
			this.tenorSplitPerc.setDisabled(true);
			this.installments.setReadonly(true);
			this.rateMargin.setDisabled(true);
			this.eMIStepPerc.setDisabled(true);
			this.btnSave.setVisible(false);
		}

		if (!isNewRecord() && alwManualStep) {
			this.space_FinStepPolicyDialog_StepNumber.setSclass("");
			this.stepNumber.setReadonly(true);
			this.tenorSplitPerc.setDisabled(true);
		}

		logger.debug("Leaving ");
	}

	public boolean isReadOnly(String componentName) {
		boolean isFinStepWorkflow = false;
		if (getStepDetailDialogCtrl() != null) {
			isFinStepWorkflow = getStepDetailDialogCtrl().getFinanceDetail().getFinScheduleData().getFinanceMain()
					.isWorkflow();
		}
		if (isWorkFlowEnabled() || isFinStepWorkflow) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.stepFinTenor.setReadonly(true);
		this.stepNumber.setReadonly(true);
		this.tenorSplitPerc.setDisabled(true);
		this.installments.setReadonly(true);
		this.rateMargin.setDisabled(true);
		this.eMIStepPerc.setReadonly(true);
		this.steppedEMI.setReadonly(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		this.tenorSplitPerc.setText("");
		this.installments.setText("");
		this.eMIStepPerc.setText("");
		this.eMIStepPerc.setText("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final FinanceStepPolicyDetail aFinanceStepPolicyDetail = new FinanceStepPolicyDetail();
		BeanUtils.copyProperties(getFinanceStepPolicyDetail(), aFinanceStepPolicyDetail);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the CustomerEmploymentDetail object with the components data
		doWriteComponentsToBean(aFinanceStepPolicyDetail);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aFinanceStepPolicyDetail.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinanceStepPolicyDetail.getRecordType())) {
				aFinanceStepPolicyDetail.setVersion(aFinanceStepPolicyDetail.getVersion() + 1);
				if (isNew) {
					aFinanceStepPolicyDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinanceStepPolicyDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinanceStepPolicyDetail.setNewRecord(true);
				}
			}
		} else {

			if (isNewFinStep()) {
				if (isNewRecord()) {
					aFinanceStepPolicyDetail.setVersion(1);
					aFinanceStepPolicyDetail.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isBlank(aFinanceStepPolicyDetail.getRecordType())) {
					aFinanceStepPolicyDetail.setVersion(aFinanceStepPolicyDetail.getVersion() + 1);
					aFinanceStepPolicyDetail.setRecordType(PennantConstants.RCD_UPD);
				}

				if (aFinanceStepPolicyDetail.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aFinanceStepPolicyDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}

			} else {
				aFinanceStepPolicyDetail.setVersion(aFinanceStepPolicyDetail.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {
			if (isNewFinStep()) {
				AuditHeader auditHeader = newFinStepPolicyProcess(aFinanceStepPolicyDetail, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_FinStepPolicyDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					if (aFinanceStepPolicyDetail.getStepSpecifier().equals(PennantConstants.STEP_SPECIFIER_GRACE)) {
						stepDetailDialogCtrl.doFillStepDetaisForGrace(this.finStepPolicyDetails);
					} else {
						stepDetailDialogCtrl.doFillStepDetais(this.finStepPolicyDetails);
					}
					stepDetailDialogCtrl.setDataChanged(true);
					closeDialog();
				}
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private AuditHeader newFinStepPolicyProcess(FinanceStepPolicyDetail aFinanceStepPolicyDetail, String tranType) {
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aFinanceStepPolicyDetail, tranType);
		finStepPolicyDetails = new ArrayList<FinanceStepPolicyDetail>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		int maxStepNo = 0;
		if (StringUtils.equals(aFinanceStepPolicyDetail.getStepSpecifier(), PennantConstants.STEP_SPECIFIER_REG_EMI)) {
			maxStepNo = rpySteps;
			valueParm[1] = PennantJavaUtil.getLabel("label_FinanceMainDialog_RepaySteps.value") + " "
					+ String.valueOf(maxStepNo);
		} else {
			maxStepNo = grcSteps;
			valueParm[1] = PennantJavaUtil.getLabel("label_FinanceMainDialog_GrcSteps.value") + " "
					+ String.valueOf(maxStepNo);
		}

		if (!(PennantConstants.TRAN_DEL.equals(tranType))
				&& !(StringUtils.equals(aFinanceStepPolicyDetail.getRecordType(), PennantConstants.RECORD_TYPE_DEL)
						|| StringUtils.equals(aFinanceStepPolicyDetail.getRecordType(),
								PennantConstants.RECORD_TYPE_CAN))
				&& (aFinanceStepPolicyDetail.getStepNo() > maxStepNo)) {
			valueParm[0] = String.valueOf(aFinanceStepPolicyDetail.getStepNo());
			errParm[0] = PennantJavaUtil.getLabel("label_StepNumber") + " " + valueParm[0];
			errParm[1] = valueParm[1];
			auditHeader.setErrorDetails(
					ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "30568", errParm, valueParm),
							getUserWorkspace().getUserLanguage()));
			return auditHeader;
		}

		valueParm[0] = String.valueOf(aFinanceStepPolicyDetail.getStepNo());
		errParm[0] = PennantJavaUtil.getLabel("label_StepNumber") + ":" + valueParm[0];

		if (getStepDetailDialogCtrl().getFinStepPoliciesList() != null
				&& !getStepDetailDialogCtrl().getFinStepPoliciesList().isEmpty()) {
			for (int i = 0; i < getStepDetailDialogCtrl().getFinStepPoliciesList().size(); i++) {
				FinanceStepPolicyDetail financeStepPolicyDetail = getStepDetailDialogCtrl().getFinStepPoliciesList()
						.get(i);

				if (financeStepPolicyDetail.getStepNo() == aFinanceStepPolicyDetail.getStepNo() && StringUtils.equals(
						financeStepPolicyDetail.getStepSpecifier(), aFinanceStepPolicyDetail.getStepSpecifier())) { // Both
																													// Current
																													// and
																													// Existing
																													// list
																													// Steps
					// same

					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (tranType == PennantConstants.TRAN_DEL) {
						recordAdded = true;
						/*
						 * if(aFinanceStepPolicyDetail.getRecordType().equals( PennantConstants.RECORD_TYPE_UPD)){
						 * aFinanceStepPolicyDetail .setRecordType(PennantConstants.RECORD_TYPE_DEL); recordAdded=true;
						 * finStepPolicyDetails.add(aFinanceStepPolicyDetail); }else
						 * if(aFinanceStepPolicyDetail.getRecordType().equals (PennantConstants.RCD_ADD)){
						 * recordAdded=true; }else if(aFinanceStepPolicyDetail.getRecordType().equals(
						 * PennantConstants.RECORD_TYPE_NEW)){ aFinanceStepPolicyDetail
						 * .setRecordType(PennantConstants.RECORD_TYPE_CAN); recordAdded=true;
						 * finStepPolicyDetails.add(aFinanceStepPolicyDetail); }else
						 * if(aFinanceStepPolicyDetail.getRecordType().equals (PennantConstants.RECORD_TYPE_CAN)){
						 * recordAdded=true; for (int j = 0; j < getStepDetailDialogCtrl
						 * ().getFinScheduleData().getStepPolicyDetails ().size(); j++) { FinanceStepPolicyDetail
						 * policyDetail = getStepDetailDialogCtrl().getFinScheduleData ().getStepPolicyDetails().get(j);
						 * if(policyDetail.getFinReference ().equals(aFinanceStepPolicyDetail.getFinReference()) &&
						 * policyDetail.getStepNo() == aFinanceStepPolicyDetail.getStepNo()){
						 * finStepPolicyDetails.add(policyDetail); } } }
						 */
					} else {
						if (tranType != PennantConstants.TRAN_UPD) {
							finStepPolicyDetails.add(financeStepPolicyDetail);
						}
					}
				} else {
					finStepPolicyDetails.add(financeStepPolicyDetail);
				}
			}
		}
		if (!recordAdded) {
			finStepPolicyDetails.add(aFinanceStepPolicyDetail);
		}
		return auditHeader;
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(FinanceStepPolicyDetail aFinanceStepPolicyDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinanceStepPolicyDetail.getBefImage(),
				aFinanceStepPolicyDetail);

		return new AuditHeader(String.valueOf(aFinanceStepPolicyDetail.getFinReference()),
				String.valueOf(aFinanceStepPolicyDetail.getStepNo()), null, null, auditDetail,
				aFinanceStepPolicyDetail.getUserDetails(), getOverideMap());
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
			ErrorControl.showErrorControl(this.window_FinStepPolicyDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.financeStepPolicyDetail);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.financeStepPolicyDetail.getFinReference());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setStepDetailDialogCtrl(StepDetailDialogCtrl stepDetailDialogCtrl) {
		this.stepDetailDialogCtrl = stepDetailDialogCtrl;
	}

	public StepDetailDialogCtrl getStepDetailDialogCtrl() {
		return stepDetailDialogCtrl;
	}

	public void setNewFinStep(boolean newFinStep) {
		this.newFinStep = newFinStep;
	}

	public boolean isNewFinStep() {
		return newFinStep;
	}

	public FinanceStepPolicyDetail getFinanceStepPolicyDetail() {
		return financeStepPolicyDetail;
	}

	public void setFinanceStepPolicyDetail(FinanceStepPolicyDetail financeStepPolicyDetail) {
		this.financeStepPolicyDetail = financeStepPolicyDetail;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}

	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
		setFinScheduleData(financeDetail.getFinScheduleData());
		setFinanceMain(this.finScheduleData.getFinanceMain());
	}
}
