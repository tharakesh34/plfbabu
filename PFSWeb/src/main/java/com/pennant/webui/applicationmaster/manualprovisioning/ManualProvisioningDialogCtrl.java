package com.pennant.webui.applicationmaster.manualprovisioning;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.collateral.CollateralAssignmentDAO;
import com.pennant.backend.dao.configuration.VASRecordingDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.NPAProvisionDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.financemanagement.ProvisionAmount;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.rulefactory.RuleResult;
import com.pennant.backend.service.financemanagement.ProvisionService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.ProvisionConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.TableType;

public class ManualProvisioningDialogCtrl extends GFCBaseCtrl<Provision> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ManualProvisioningDialogCtrl.class);

	protected Window window_ManualProvisioningDialog;

	// Loan Summary
	protected Textbox finReference;
	protected Textbox customer;
	protected Textbox finType;
	protected CurrencyBox finAmount;
	protected Label finStartDate;
	protected Label maturityDate;
	protected CurrencyBox pricipalOutstanding;
	protected CurrencyBox totalOverdue;
	protected Intbox dPD;

	// Current NPA Summary
	protected Textbox regStage;
	protected Textbox internalStage;
	protected Decimalbox regProvisionPercentage;
	protected Decimalbox internalProvisionPercentage;
	protected CurrencyBox regProvisionAmount;
	protected CurrencyBox internalProvisionAmount;
	protected Checkbox currManualProvision;

	// New NPA Summary
	protected Combobox newRegStage;
	protected Combobox newInternalStage;
	protected Decimalbox newRegProvisionPercentage;
	protected Decimalbox newInternalProvisionPercentage;
	protected CurrencyBox newRegProvisionAmount;
	protected CurrencyBox newInternalProvisionAmount;
	protected Checkbox manualProvision;

	protected String enquiry;
	private List<ValueLabel> assetRegStageList;
	private List<ValueLabel> assetIntStageList;
	private Provision provision;
	private transient ManualProvisioningListCtrl manualProvisioningListCtrl;
	private transient ProvisionService provisionService;
	private boolean isInsuranceComponent = false;
	private boolean provisionInternal = false;
	private int format = 2;
	private transient boolean validationOn;
	private List<NPAProvisionDetail> provisionDetails = null;
	private boolean isSecured = false;
	private RuleDAO ruleDAO;
	private CollateralAssignmentDAO collateralAssignmentDAO;
	private VASRecordingDAO vASRecordingDAO;
	BigDecimal insuranceAmount = BigDecimal.ZERO;
	Map<String, Object> dataMap = new HashMap<>();

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
			// Get the required arguments.
			this.provision = (Provision) arguments.get("provision");
			this.manualProvisioningListCtrl = (ManualProvisioningListCtrl) arguments.get("manualProvisioningListCtrl");
			if (arguments.containsKey("enquiry")) {
				this.enquiry = PennantConstants.MODULETYPE_ENQ;
			}

			if (this.provision == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			Provision provision = new Provision();
			BeanUtils.copyProperties(this.provision, provision);
			this.provision.setBefImage(provision);

			// Render the page and display the data.
			doLoadWorkFlow(this.provision.isWorkflow(), this.provision.getWorkflowId(), this.provision.getNextTaskId());

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

		this.finReference.setMaxlength(20);
		this.customer.setMaxlength(50);
		this.finType.setMaxlength(8);
		this.dPD.setMaxlength(4);
		this.regStage.setMaxlength(8);
		this.internalStage.setMaxlength(8);
		this.newRegStage.setMaxlength(20);
		this.newInternalStage.setMaxlength(20);
		if (provision.getFinanceDetail() != null) {
			String finCcy = provision.getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy();
			format = CurrencyUtil.getFormat(finCcy);
		}
		this.finAmount.setProperties(false, format);
		this.pricipalOutstanding.setProperties(false, format);
		this.totalOverdue.setProperties(false, format);
		this.regProvisionAmount.setProperties(false, format);
		this.internalProvisionAmount.setProperties(false, format);
		this.newRegProvisionAmount.setProperties(false, format);
		this.newInternalProvisionAmount.setProperties(false, format);

		if (StringUtils.equals(ProvisionConstants.PROVISION_BOOKS_REG,
				SysParamUtil.getValueAsString(SMTParameterConstants.PROVISION_BOOKS))) {
			provisionInternal = false;
		} else if (StringUtils.equals(ProvisionConstants.PROVISION_BOOKS_INT,
				SysParamUtil.getValueAsString(SMTParameterConstants.PROVISION_BOOKS))) {
			provisionInternal = true;
		}

		List<VASRecording> vASRecordingsList = vASRecordingDAO.getVASRecordingsByLinkRef(provision.getFinReference(),
				"");

		if (CollectionUtils.isNotEmpty(vASRecordingsList)) {
			for (VASRecording vasRecording : vASRecordingsList) {
				insuranceAmount = insuranceAmount.add(vasRecording.getFee());
			}
			isInsuranceComponent = true;
		}

		isSecured = this.collateralAssignmentDAO.isSecuredLoan(provision.getFinReference(), TableType.MAIN_TAB);
		provision.setSecured(isSecured);

		FinScheduleData finScheduleData = provision.getFinanceDetail().getFinScheduleData();
		FinanceProfitDetail finProfitDetail = finScheduleData.getFinPftDeatil();
		FinanceMain financeMain = finScheduleData.getFinanceMain();

		dataMap.put("custCtgCode", provision.getCustCtgCode());
		dataMap.put("DueBucket", provision.getCurrBucket());
		dataMap.put("ODDays", provision.getDueDays());
		dataMap.put("Product", finProfitDetail.getFinCategory());
		dataMap.put("reqFinType", provision.getFinType());

		dataMap.put("SecuredLoan", isSecured);
		dataMap.put("AssetStage", provision.getAssetStageOrder());
		dataMap.put("InsuranceAmount", insuranceAmount);
		dataMap.put("PropertyType", provision.getPropertyType()); // FIX ME

		dataMap.put("OutstandingAmount", provision.getClosingBalance());
		dataMap.put("FuturePrincipal", finProfitDetail.getFutureRpyPri());
		dataMap.put("OverduePrincipal", finProfitDetail.getODPrincipal());
		dataMap.put("OverdueInterest", finProfitDetail.getODProfit());
		dataMap.put("UndisbursedAmount", financeMain.getFinAssetValue().subtract(financeMain.getFinCurrAssetValue()));

		this.assetRegStageList = getRegNPAStageList(provision);
		this.assetIntStageList = getIntNPAStageList(provision);
		setStatusDetails();

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		if (this.provision.getNpaIntHeader() == null) {
			this.newInternalStage.setReadonly(true);
		}

		if (this.provision.getNpaRegHeader() == null) {
			this.newRegStage.setReadonly(true);
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
		doReadOnly();
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

		FinanceMain aFinanceMain = provision.getFinanceDetail().getFinScheduleData().getFinanceMain();
		FinanceProfitDetail finProfitDetail = provision.getFinanceDetail().getFinScheduleData().getFinPftDeatil();
		format = CurrencyUtil.getFormat(aFinanceMain.getFinCcy());

		if (provision.getCollateralValue() != null) {
			if (provision.getCollateralValue().compareTo(BigDecimal.ZERO) > 0) {
				setSecured(true);
			}
		}
		// Loan Summary
		this.finReference.setValue(provision.getFinReference());
		this.customer.setValue(provision.getCustCIF() + "-" + provision.getCustShrtName());
		this.finType.setValue(provision.getFinType());
		this.finAmount.setValue(PennantApplicationUtil.formateAmount(aFinanceMain.getFinAmount(), format));
		this.finStartDate.setValue(DateUtility.formatToLongDate(aFinanceMain.getFinStartDate()));
		this.maturityDate.setValue(DateUtility.formatToLongDate(aFinanceMain.getMaturityDate()));
		this.pricipalOutstanding
				.setValue(PennantApplicationUtil.formateAmount(finProfitDetail.getTotalPriBal(), format));
		this.totalOverdue.setValue(PennantApplicationUtil.formateAmount(finProfitDetail.getODProfit()
				.add(finProfitDetail.getODPrincipal().add(finProfitDetail.getPenaltyDue())), format));
		this.dPD.setValue(finProfitDetail.getCurODDays());

		// Current NPA Provision Summary
		BigDecimal regProvAmount = BigDecimal.ZERO;
		BigDecimal intProvAmount = BigDecimal.ZERO;
		BigDecimal regInsProvAmount = BigDecimal.ZERO;
		BigDecimal intInsProvAmount = BigDecimal.ZERO;

		// Current NPA Provision Summary
		if (provision.getOldProvision() != null) {
			List<ProvisionAmount> provisionAmounts = provision.getOldProvision().getProvisionAmounts();
			for (ProvisionAmount provisionAmount : provisionAmounts) {
				switch (provisionAmount.getProvisionType()) {
				case ProvisionConstants.PROVISION_BOOKS_INT:
					this.internalStage.setValue(provisionAmount.getAssetCode());
					this.internalProvisionPercentage.setValue(provisionAmount.getProvisionPer());
					intProvAmount = provisionAmount.getProvisionAmtCal();
					break;

				case ProvisionConstants.PROVISION_BOOKS_REG:
					this.regStage.setValue(provisionAmount.getAssetCode());
					this.regProvisionPercentage.setValue(provisionAmount.getProvisionPer());
					regProvAmount = provisionAmount.getProvisionAmtCal();
					break;

				case ProvisionConstants.PROVISION_BOOKS_INT_INS:
					regInsProvAmount = provisionAmount.getProvisionAmtCal();
					break;

				case ProvisionConstants.PROVISION_BOOKS_REG_INS:
					intInsProvAmount = provisionAmount.getProvisionAmtCal();
					break;

				}
				this.internalProvisionAmount
						.setValue(PennantApplicationUtil.formateAmount(intProvAmount.add(intInsProvAmount), format));

				this.regProvisionAmount
						.setValue(PennantApplicationUtil.formateAmount(regProvAmount.add(regInsProvAmount), format));

			}
			this.currManualProvision.setChecked(provision.getOldProvision().isManualProvision());
		}

		// New Provision
		List<ProvisionAmount> provisionAmounts = provision.getProvisionAmounts();
		// fillComboBox(this.newRegStage, null, this.assetRegStageList, "");
		// fillComboBox(this.newInternalStage, null, this.assetIntStageList, "");
		regProvAmount = BigDecimal.ZERO;
		intProvAmount = BigDecimal.ZERO;
		regInsProvAmount = BigDecimal.ZERO;
		intInsProvAmount = BigDecimal.ZERO;
		for (ProvisionAmount provisionAmount : provisionAmounts) {
			switch (provisionAmount.getProvisionType()) {

			case ProvisionConstants.PROVISION_BOOKS_INT:
				this.newInternalStage.setAttribute("Object", provisionAmount);
				fillComboBox(this.newInternalStage, provisionAmount.getAssetCode(), this.assetIntStageList, "");
				this.newInternalProvisionPercentage.setValue(provisionAmount.getProvisionPer());
				intProvAmount = provisionAmount.getProvisionAmtCal();
				this.newInternalProvisionAmount.setAttribute("provAmount", intProvAmount);
				break;

			case ProvisionConstants.PROVISION_BOOKS_REG:
				this.newRegStage.setAttribute("Object", provisionAmount);
				fillComboBox(this.newRegStage, provisionAmount.getAssetCode(), this.assetRegStageList, "");
				this.newRegProvisionPercentage.setValue(provisionAmount.getProvisionPer());
				regProvAmount = provisionAmount.getProvisionAmtCal();
				this.newRegProvisionAmount.setAttribute("provAmount", regProvAmount);
				break;

			case ProvisionConstants.PROVISION_BOOKS_INT_INS:
				regInsProvAmount = provisionAmount.getProvisionAmtCal();
				this.newInternalProvisionAmount.setAttribute("provInsAmount", intInsProvAmount);
				break;

			case ProvisionConstants.PROVISION_BOOKS_REG_INS:
				intInsProvAmount = provisionAmount.getProvisionAmtCal();
				this.newRegProvisionAmount.setAttribute("provInsAmount", intInsProvAmount);
				break;
			}

			this.newInternalProvisionAmount
					.setValue(PennantApplicationUtil.formateAmount(intProvAmount.add(intInsProvAmount), format));
			this.newRegProvisionAmount
					.setValue(PennantApplicationUtil.formateAmount(regProvAmount.add(regInsProvAmount), format));
		}

		this.manualProvision.setChecked(provision.isManualProvision());
		this.recordStatus.setValue(provision.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aAcademic
	 */
	public void doWriteComponentsToBean(Provision provision) {
		logger.debug(Literal.ENTERING);
		BigDecimal regProvAmount = (BigDecimal) this.newRegProvisionAmount.getAttribute("provAmount");
		BigDecimal intProvAmount = (BigDecimal) this.newInternalProvisionAmount.getAttribute("provAmount");
		ArrayList<WrongValueException> wve = new ArrayList<>();
		if (provisionInternal) {
			try {
				provision.setAssetCode(this.newInternalStage.getSelectedItem().getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				provision.setProvisionedAmt(intProvAmount);
			} catch (WrongValueException we) {
				wve.add(we);
			}

			provision.setAssetStageOrder(getAssetStageOrder(this.newInternalStage.getSelectedItem().getValue()));
			provision.setNpaTemplateId(Long.valueOf(2));
		} else {
			try {
				provision.setAssetCode(this.newRegStage.getSelectedItem().getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				provision.setProvisionedAmt(regProvAmount);
			} catch (WrongValueException we) {
				wve.add(we);
			}

			provision.setAssetStageOrder(getAssetStageOrder(this.newRegStage.getSelectedItem().getValue()));
			provision.setNpaTemplateId(Long.valueOf(1));
		}

		try {
			provision.setManualProvision(this.manualProvision.isChecked());
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

		List<ProvisionAmount> provisionAmountList = provision.getProvisionAmounts();

		BigDecimal regInsProvAmount = (BigDecimal) this.newRegProvisionAmount.getAttribute("provInsAmount");
		BigDecimal intInsProvAmount = (BigDecimal) this.newInternalProvisionAmount.getAttribute("provInsAmount");

		String internalAssetCode = getComboboxValue(this.newInternalStage);
		String regAssetCode = getComboboxValue(this.newRegStage);
		BigDecimal regProvPer = this.newRegProvisionPercentage.getValue();
		BigDecimal intProcvPer = this.newInternalProvisionPercentage.getValue();
		for (ProvisionAmount provAmt : provisionAmountList) {
			switch (provAmt.getProvisionType()) {

			case ProvisionConstants.PROVISION_BOOKS_INT:
				provAmt.setAssetCode(internalAssetCode);
				provAmt.setProvisionPer(intProcvPer);
				provAmt.setProvisionAmtCal(intProvAmount);
				break;

			case ProvisionConstants.PROVISION_BOOKS_REG:
				provAmt.setAssetCode(regAssetCode);
				provAmt.setProvisionPer(regProvPer);
				provAmt.setProvisionAmtCal(regProvAmount);
				break;

			case ProvisionConstants.PROVISION_BOOKS_INT_INS:
				provAmt.setAssetCode(internalAssetCode);
				provAmt.setProvisionPer(intProcvPer);
				provAmt.setProvisionAmtCal(intInsProvAmount);
				break;

			case ProvisionConstants.PROVISION_BOOKS_REG_INS:
				provAmt.setAssetCode(regAssetCode);
				provAmt.setProvisionPer(regProvPer);
				provAmt.setProvisionAmtCal(regInsProvAmount);
				break;

			}
		}
		provision.setRecordStatus(this.recordStatus.getValue());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Setting Asset Stage Order
	 * 
	 * @param value
	 * @return
	 */
	private int getAssetStageOrder(String assetCode) {
		List<NPAProvisionDetail> provisionDetails = getProvisionDetails();

		if (CollectionUtils.isNotEmpty(provisionDetails)) {
			for (NPAProvisionDetail npaProvisionDetail : provisionDetails) {
				if (StringUtils.equals(assetCode, npaProvisionDetail.getAssetCode())) {
					return npaProvisionDetail.getAssetStageOrder();
				}
			}
		}
		return 0;
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param aAcademic The entity that need to be render.
	 */
	public void doShowDialog(Provision provision) {
		logger.debug(Literal.ENTERING);

		if (provision.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(provision.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				if (enqiryModule) {
					this.btnNotes.setVisible(false);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
		}

		doWriteBeanToComponents(provision);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);
		setValidationOn(true);
		if (provisionInternal) {
			this.newInternalStage.setConstraint(new PTListValidator<ValueLabel>(
					Labels.getLabel("label_ManualProvisioningDialog_NewInternalStage.value"), assetIntStageList, true));
		} else {
			this.newRegStage.setConstraint(new PTListValidator<ValueLabel>(
					Labels.getLabel("label_ManualProvisioningDialog_NewRegStage.value"), assetRegStageList, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		setValidationOn(false);
		this.newRegStage.setConstraint("");
		this.newInternalStage.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);
		this.newRegStage.setErrorMessage("");
		this.newInternalStage.setErrorMessage("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (PennantConstants.MODULETYPE_ENQ.equals(enquiry)) {
			doReadOnly();
		} else {

			if (this.provision.isNewRecord()) {
				this.newRegStage.setDisabled(false);
				this.newInternalStage.setDisabled(false);
				this.btnCancel.setVisible(false);
			} else {
				doReadOnly();
				this.newRegStage.setDisabled(isReadOnly("ManualProvisioningDialog_NewRBIStage"));
				this.newInternalStage.setDisabled(isReadOnly("ManualProvisioningDialog_NewInternalStage"));
				this.btnCancel.setVisible(true);
			}
			this.manualProvision.setDisabled(isReadOnly("ManualProvisioningDialog_NewInternalStage"));
			this.newRegProvisionPercentage.setReadonly(isReadOnly("ManualProvisioning_NewRegProvisionPercentage"));
			this.newInternalProvisionPercentage
					.setReadonly(isReadOnly("ManualProvisioning_NewInternalProvisionPercentage"));
			this.newRegProvisionAmount.setReadonly(isReadOnly("ManualProvisioning_NewRegProvisionAmount"));
			this.newInternalProvisionAmount.setReadonly(isReadOnly("ManualProvisioning_NewInternalProvisionAmount"));

		}
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.provision.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
		this.btnDelete.setVisible(false);

		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		this.finReference.setReadonly(true);
		this.finAmount.setReadonly(true);
		this.customer.setReadonly(true);
		this.finType.setReadonly(true);
		this.pricipalOutstanding.setReadonly(true);
		this.totalOverdue.setReadonly(true);
		this.dPD.setReadonly(true);
		this.regStage.setReadonly(true);
		this.regProvisionPercentage.setReadonly(true);
		this.regProvisionAmount.setReadonly(true);
		this.internalStage.setReadonly(true);
		this.internalProvisionPercentage.setReadonly(true);
		this.internalProvisionAmount.setReadonly(true);
		this.manualProvision.setDisabled(true);
		this.newRegStage.setDisabled(true);
		this.newRegProvisionPercentage.setReadonly(true);
		this.newRegProvisionAmount.setReadonly(true);
		this.newInternalStage.setDisabled(true);
		this.newInternalProvisionPercentage.setReadonly(true);
		this.newInternalProvisionAmount.setReadonly(true);
		if (!enqiryModule) {
			if (isWorkFlowEnabled()) {
				for (int i = 0; i < userAction.getItemCount(); i++) {
					userAction.getItemAtIndex(i).setDisabled(true);
				}
			}

			if (isWorkFlowEnabled()) {
				this.recordStatus.setValue("");
				this.userAction.setSelectedIndex(0);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug(Literal.ENTERING);

		this.finReference.setValue("");
		this.finType.setValue("");
		this.finAmount.setValue("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);

		final Provision aProvision = new Provision();
		BeanUtils.copyProperties(this.provision, aProvision);
		boolean isNew;

		// ************************************************************
		// force validation, if on, than execute by component.getValue()
		// ************************************************************
		doSetValidation();
		// fill the Academic object with the components data
		doWriteComponentsToBean(aProvision);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aProvision.isNewRecord();
		String tranType;

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

	protected boolean doProcess(Provision aProvision, String tranType) throws Exception {
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

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method      (String)
	 * 
	 * @return boolean
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InterfaceException
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
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
	 * NPA Stage List
	 * 
	 * @param provision2
	 * @return
	 */
	private List<ValueLabel> getIntNPAStageList(Provision provision) {

		List<NPAProvisionDetail> details = provision.getNpaIntHeader().getProvisionDetailsList();

		setProvisionDetails(details);
		List<ValueLabel> stageList = new ArrayList<ValueLabel>(details.size());

		if (CollectionUtils.isNotEmpty(details)) {
			for (NPAProvisionDetail provisionDetail : details) {
				stageList.add(
						new ValueLabel(provisionDetail.getAssetCode(), String.valueOf(provisionDetail.getAssetCode())));
			}
		}

		return stageList;
	}

	private List<ValueLabel> getRegNPAStageList(Provision provision) {

		if (provision.getNpaRegHeader() == null) {
			return null;
		}

		List<NPAProvisionDetail> details = provision.getNpaRegHeader().getProvisionDetailsList();

		setProvisionDetails(details);
		List<ValueLabel> stageList = new ArrayList<ValueLabel>(details.size());

		if (CollectionUtils.isNotEmpty(details)) {
			for (NPAProvisionDetail provisionDetail : details) {
				stageList.add(
						new ValueLabel(provisionDetail.getAssetCode(), String.valueOf(provisionDetail.getAssetCode())));
			}
		}
		return stageList;
	}

	/**
	 * NPA Stage selection
	 * 
	 * @param event
	 */
	public void onSelect$newRegStage(Event event) {
		logger.debug(Literal.ENTERING);

		String stage = newRegStage.getSelectedItem().getLabel();

		this.newRegProvisionPercentage.setValue(BigDecimal.ZERO);
		this.newRegProvisionAmount.setValue(BigDecimal.ZERO);

		for (NPAProvisionDetail detail : provision.getNpaRegHeader().getProvisionDetailsList()) {
			if (StringUtils.equals(detail.getAssetCode(), stage)) {
				Rule provRule = ruleDAO.getRuleByID(detail.getRuleId(), "");
				RuleResult ruleResult = executeProvisionRule(provRule);
				BigDecimal provisionAmount = new BigDecimal(ruleResult.getProvAmount().toString());
				BigDecimal provpercentage = new BigDecimal(ruleResult.getProvPercentage().toString());
				BigDecimal vasProvAmount = BigDecimal.ZERO;
				if (isInsuranceComponent) {
					vasProvAmount = new BigDecimal(ruleResult.getVasProvAmount().toString());
				}

				this.newRegProvisionPercentage.setValue(provpercentage);
				this.newRegProvisionAmount.setAttribute("provAmount", provisionAmount);
				this.newRegProvisionAmount.setAttribute("provInsAmount", vasProvAmount);
				this.newRegProvisionAmount
						.setValue(PennantApplicationUtil.formateAmount(provisionAmount.add(vasProvAmount), format));
				break;
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * NPA Stage selection
	 * 
	 * @param event
	 */
	public void onSelect$newInternalStage(Event event) {
		logger.debug(Literal.ENTERING);

		String stage = newInternalStage.getSelectedItem().getLabel();

		this.newInternalProvisionPercentage.setValue(BigDecimal.ZERO);
		this.newInternalProvisionAmount.setValue(BigDecimal.ZERO);

		for (NPAProvisionDetail detail : provision.getNpaIntHeader().getProvisionDetailsList()) {
			if (StringUtils.equals(detail.getAssetCode(), stage)) {
				Rule provRule = ruleDAO.getRuleByID(detail.getRuleId(), "");
				RuleResult ruleResult = executeProvisionRule(provRule);
				BigDecimal provisionAmount = new BigDecimal(ruleResult.getProvAmount().toString());
				BigDecimal provpercentage = new BigDecimal(ruleResult.getProvPercentage().toString());
				BigDecimal vasProvAmount = BigDecimal.ZERO;
				if (isInsuranceComponent) {
					vasProvAmount = new BigDecimal(ruleResult.getVasProvAmount().toString());
				}
				this.newInternalProvisionPercentage.setValue(provpercentage);

				this.newInternalProvisionAmount.setAttribute("provAmount", provisionAmount);
				this.newInternalProvisionAmount.setAttribute("provInsAmount", vasProvAmount);
				this.newInternalProvisionAmount
						.setValue(PennantApplicationUtil.formateAmount(provisionAmount.add(vasProvAmount), format));
				break;
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private RuleResult executeProvisionRule(Rule provRule) {
		Object result = RuleExecutionUtil.executeRule(provRule.getSQLRule(), dataMap, provision.getFinCcy(),
				RuleReturnType.OBJECT);
		RuleResult ruleResult = (RuleResult) result;
		return ruleResult;
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(Provision aProvision, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aProvision.getBefImage(), aProvision);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aProvision.getUserDetails(),
				getOverideMap());
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

	public void setProvisionService(ProvisionService provisionService) {
		this.provisionService = provisionService;
	}

	public ProvisionService getProvisionService() {
		return provisionService;
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public List<NPAProvisionDetail> getProvisionDetails() {
		return provisionDetails;
	}

	public void setProvisionDetails(List<NPAProvisionDetail> provisionDetails) {
		this.provisionDetails = provisionDetails;
	}

	public boolean isSecured() {
		return isSecured;
	}

	public void setSecured(boolean isSecured) {
		this.isSecured = isSecured;
	}

	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	public void setCollateralAssignmentDAO(CollateralAssignmentDAO collateralAssignmentDAO) {
		this.collateralAssignmentDAO = collateralAssignmentDAO;
	}

	public void setvASRecordingDAO(VASRecordingDAO vASRecordingDAO) {
		this.vASRecordingDAO = vASRecordingDAO;
	}

}