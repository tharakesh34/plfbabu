package com.pennant.webui.finance.financemain;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jaxen.JaxenException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.reason.details.ReasonDetails;
import com.pennant.backend.model.reason.details.ReasonHeader;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.finance.FinanceCancellationService;
import com.pennant.backend.service.lmtmasters.FinanceReferenceDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.component.Uppercasebox;
import com.pennant.core.EventManager.Notify;
import com.pennant.pff.extension.FeeExtension;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.searchdialogs.ExtendedMultipleSearchListBox;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.overdue.constants.ChargeType;
import com.pennapps.core.util.ObjectUtil;

public class FinanceCancellationDialogCtrl extends FinanceBaseCtrl<FinanceMain> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = LogManager.getLogger(FinanceCancellationDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinanceCancellationDialog; // autoWired

	// Finance Main Details Tab---> 1. Key Details
	protected CurrencyBox downPaySupl; // autoWired
	protected Button btnFlagDetails;
	protected Uppercasebox flagDetails;

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	protected transient BigDecimal oldVar_downPaySupl;

	private FinanceCancellationService financeCancellationService;
	private FinanceReferenceDetailService financeReferenceDetailService;
	private PostingsPreparationUtil postingsPreparationUtil;
	private ManualAdviseDAO manualAdviseDAO;

	protected Listbox listBoxCancelFinancePosting;

	protected Label label_FinanceMainDialog_FinAssetValue;
	protected Label label_FinanceMainDialog_FinCurrentAssetValue;
	protected Label label_FinanceMainDialog_FinAmount;
	protected CurrencyBox finCurrentAssetValue;
	protected Row row_FinAssetValue;
	protected CurrencyBox finAssetValue;
	protected Label netFinAmount;
	protected Checkbox manualSchedule;
	protected Row row_ManualSchedule;
	protected Textbox finDivisionName;
	protected Hbox hbox_PromotionProduct;
	private Label label_FinanceMainDialog_PromotionProduct;
	private Label label_FinanceMainDialog_FinType;
	protected Textbox cancelRemarks;
	protected Uppercasebox reasons;
	protected Button btnReasons;

	/**
	 * default constructor.<br>
	 */
	public FinanceCancellationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinanceMainDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_FinanceCancellationDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinanceCancellationDialog);

		try {

			// READ OVERHANDED parameters !
			if (arguments.containsKey("financeDetail")) {
				setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
				FinanceMain befImage = new FinanceMain();
				BeanUtils.copyProperties(getFinanceDetail().getFinScheduleData().getFinanceMain(), befImage);
				getFinanceDetail().getFinScheduleData().getFinanceMain().setBefImage(befImage);
				setFinanceDetail(getFinanceDetail());
			}

			// READ OVERHANDED params !
			// we get the financeMainListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete financeMain here.

			if (arguments.containsKey("financeSelectCtrl")) {
				setFinanceSelectCtrl((FinanceSelectCtrl) arguments.get("financeSelectCtrl"));
			}

			if (arguments.containsKey("tabbox")) {
				listWindowTab = (Tab) arguments.get("tabbox");
			}

			if (arguments.containsKey("moduleDefiner")) {
				moduleDefiner = (String) arguments.get("moduleDefiner");
			}

			if (arguments.containsKey("eventCode")) {
				eventCode = (String) arguments.get("eventCode");
			}

			if (arguments.containsKey("menuItemRightName")) {
				menuItemRightName = (String) arguments.get("menuItemRightName");
			}

			FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
			doLoadWorkFlow(financeMain.isWorkflow(), financeMain.getWorkflowId(), financeMain.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateMenuRoleAuthorities(getRole(), "FinanceMainDialog", menuItemRightName);
			} else {
				this.south.setHeight("0px");
			}

			setMainWindow(window_FinanceCancellationDialog);

			/* set components visible dependent of the users rights */
			isEnquiry = true;
			doCheckRights();

			this.basicDetailTabDiv.setHeight(this.borderLayoutHeight - 100 - 52 + "px");

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFinanceDetail());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_FinanceCancellationDialog.onClose();
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	public void doSetFieldProperties() {
		logger.debug("Entering");
		FinanceType fintype = getFinanceDetail().getFinScheduleData().getFinanceType();
		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		super.doSetFieldProperties();
		this.downPaySupl.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.downPaySupl.setScale(formatter);

		this.finAssetValue.setProperties(false, formatter);
		this.finCurrentAssetValue.setProperties(false, formatter);

		// Field visibility & Naming for FinAsset value and finCurrent asset value by OD/NONOD.
		setFinAssetFieldVisibility(fintype);
		logger.debug("Leaving");
	}

	private void setFinAssetFieldVisibility(FinanceType financeType) {

		boolean isOverdraft = false;
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				getFinanceDetail().getFinScheduleData().getFinanceMain().getProductCategory())) {
			isOverdraft = true;
		}

		if (financeType.isAlwMaxDisbCheckReq()) {

			if (isOverdraft) {

				this.label_FinanceMainDialog_FinAssetValue
						.setValue(Labels.getLabel("label_FinanceMainDialog_FinOverDftLimit.value"));
				this.label_FinanceMainDialog_FinCurrentAssetValue.setValue("");
				this.finCurrentAssetValue.setVisible(false);
			} else {
				if (!isOverdraft && financeType.isFinIsAlwMD()) {
					readOnlyComponent(isReadOnly("FinanceMainDialog_finAssetValue"), this.finAssetValue);
					this.row_FinAssetValue.setVisible(true);
					this.finAssetValue.setMandatory(true);
					this.finCurrentAssetValue.setReadonly(true);
					this.label_FinanceMainDialog_FinAssetValue
							.setValue(Labels.getLabel("label_FinanceMainDialog_FinMaxDisbAmt.value"));
					this.label_FinanceMainDialog_FinCurrentAssetValue
							.setValue(Labels.getLabel("label_FinanceMainDialog_TotalDisbAmt.value"));
				} else {
					this.label_FinanceMainDialog_FinAssetValue.setVisible(false);
					this.finAssetValue.setVisible(false);
					this.label_FinanceMainDialog_FinCurrentAssetValue
							.setValue(Labels.getLabel("label_FinanceMainDialog_TotalDisbAmt.value"));
					this.label_FinanceMainDialog_FinCurrentAssetValue.setVisible(true);
					this.finCurrentAssetValue.setVisible(true);
				}
			}
		} else {
			this.row_FinAssetValue.setVisible(false);
			if (this.label_FinanceMainDialog_FinAmount != null) {
				this.label_FinanceMainDialog_FinAmount
						.setValue(Labels.getLabel("label_FinanceMainDialog_FinMaxDisbAmt.value"));
			}
		}
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

		getUserWorkspace().allocateAuthorities("FinanceMainDialog", getRole(), menuItemRightName);

		this.btnNew.setVisible(false);
		this.btnEdit.setVisible(false);
		this.btnDelete.setVisible(false);// getUserWorkspace().isAllowed("button_FinanceMainDialog_btnDelete")
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnSave"));
		this.btnCancel.setVisible(false);
		this.btnFlagDetails.setVisible(false);

		// Schedule related buttons
		this.btnValidate.setVisible(false);
		this.btnBuildSchedule.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 */
	public void onClose$window_FinanceCancellationDialog(Event event) {
		logger.debug("Entering " + event.toString());
		closeDialog();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnSave(Event event) {
		logger.debug("Entering " + event.toString());
		doSave();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		MessageUtil.showHelpWindow(event, window_FinanceCancellationDialog);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	// GUI operations

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain financeMain
	 */
	public void doWriteBeanToComponents(FinanceDetail aFinanceDetail, boolean onLoadProcess) {
		logger.debug("Entering");

		super.doWriteBeanToComponents(aFinanceDetail, onLoadProcess);
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		if (aFinanceDetail.getFinScheduleData().getFinanceType().isFinIsDwPayRequired() && aFinanceDetail
				.getFinScheduleData().getFinanceMain().getMinDownPayPerc().compareTo(BigDecimal.ZERO) >= 0) {
			this.downPaySupl.setValue(CurrencyUtil.parse(aFinanceMain.getDownPaySupl(),
					CurrencyUtil.getFormat(getFinanceMain().getFinCcy())));
		}

		aFinanceDetail.setModuleDefiner(StringUtils.isEmpty(moduleDefiner) ? FinServiceEvent.ORG : moduleDefiner);

		// Fill Remaining Tab Details
		// ============================================

		// Customer Details
		appendCustomerDetailTab();

		// Fee Details
		appendFeeDetailTab(true);

		// Schedule Details
		appendScheduleDetailTab(true, true);

		// Agreement Details
		appendAgreementsDetailTab(true);

		// Check List Details
		appendCheckListDetailTab(getFinanceDetail(), true, true);

		// Recommendation Details
		appendRecommendDetailTab(true);

		// Document Details
		appendDocumentDetailTab();

		// Stage Accounting
		appendStageAccountingDetailsTab(true);

		// Final Accounting
		if ("Accounting".equals(getTaskTabs(getTaskId(getRole())))) {
			appendAccountingDetailTab(true);
		}
		// Showing Product Details for Promotion Type
		this.finDivisionName.setValue(aFinanceDetail.getFinScheduleData().getFinanceType().getFinDivision() + " - "
				+ aFinanceDetail.getFinScheduleData().getFinanceType().getLovDescFinDivisionName());
		if (StringUtils.isNotEmpty(aFinanceDetail.getFinScheduleData().getFinanceType().getProduct())) {
			this.hbox_PromotionProduct.setVisible(true);
			this.getLabel_FinanceMainDialog_PromotionProduct().setVisible(true);
			this.promotionProduct.setValue(aFinanceDetail.getFinScheduleData().getFinanceType().getProduct() + " - "
					+ aFinanceDetail.getFinScheduleData().getFinanceType().getLovDescPromoFinTypeDesc());
			this.label_FinanceMainDialog_FinType
					.setValue(Labels.getLabel("label_FinanceMainDialog_PromotionCode.value"));
		}

		List<ReasonHeader> details = financeCancellationService.getCancelReasonDetails(aFinanceMain.getFinReference());
		String data = "";
		if (details.size() > 0) {
			for (ReasonHeader header : details) {
				if (data.length() == 0) {
					data += header.getReasonId();
				} else {
					data += "," + header.getReasonId();
				}

			}
		}
		this.reasons.setText(data);
		if (details.size() > 0) {
			this.cancelRemarks.setText(details.get(0).getRemarks());
		}
		if (aFinanceDetail.getFinScheduleData().getFinanceType().isManualSchedule()) {
			this.row_ManualSchedule.setVisible(true);
			this.manualSchedule.setChecked(aFinanceMain.isManualSchedule());
		} else {
			this.row_ManualSchedule.setVisible(false);
		}
		this.finAssetValue.setValue(CurrencyUtil.parse(aFinanceMain.getFinAssetValue(),
				CurrencyUtil.getFormat(getFinanceMain().getFinCcy())));
		this.finCurrentAssetValue.setValue(CurrencyUtil.parse(aFinanceMain.getFinCurrAssetValue(),
				CurrencyUtil.getFormat(getFinanceMain().getFinCcy())));
		setNetFinanceAmount(true);

		logger.debug("Leaving");
	}

	public void setNetFinanceAmount(boolean isDataRender) {
		logger.debug("Entering");

		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		BigDecimal feeChargeAmount = BigDecimal.ZERO;
		BigDecimal finAmount = this.finAmount.getActualValue() == null ? BigDecimal.ZERO
				: this.finAmount.getActualValue();

		// Fee calculation for Add to Disbursement
		List<FinFeeDetail> finFeeDetails = getFinanceDetail().getFinScheduleData().getFinFeeDetailList();
		if (finFeeDetails != null && !finFeeDetails.isEmpty()) {
			for (FinFeeDetail feeDetail : finFeeDetails) {
				if (StringUtils.equals(feeDetail.getFeeScheduleMethod(),
						CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
					feeChargeAmount = feeChargeAmount.add(feeDetail.getActualAmount()
							.subtract(feeDetail.getWaivedAmount()).subtract(feeDetail.getPaidAmount()));
				}
			}
		}

		feeChargeAmount = PennantApplicationUtil.formateAmount(feeChargeAmount, formatter);
		BigDecimal netFinanceVal = finAmount
				.subtract(this.downPayBank.getActualValue().add(this.downPaySupl.getActualValue()))
				.add(feeChargeAmount);
		if (netFinanceVal.compareTo(BigDecimal.ZERO) < 0) {
			netFinanceVal = BigDecimal.ZERO;
		}

		String netFinAmt = PennantApplicationUtil
				.amountFormate(PennantApplicationUtil.unFormateAmount(netFinanceVal, formatter), formatter);
		if (finAmount != null && finAmount.compareTo(BigDecimal.ZERO) > 0) {
			if (FeeExtension.ADD_FEEINFTV_ONCALC) {
				this.netFinAmount.setValue(netFinAmt + " ("
						+ ((netFinanceVal.multiply(new BigDecimal(100))).divide(finAmount, 2, RoundingMode.HALF_DOWN))
						+ "%)");
			} else {
				this.netFinAmount.setValue(
						netFinAmt + " (" + (((netFinanceVal.subtract(feeChargeAmount)).multiply(new BigDecimal(100)))
								.divide(finAmount, 2, RoundingMode.HALF_DOWN)) + "%)");
			}
		} else {
			this.netFinAmount.setValue("");
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceSchData (FinScheduleData)
	 */
	public void doWriteComponentsToBean(FinScheduleData aFinanceSchData) {
		logger.debug("Entering");

		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		super.doWriteComponentsToBean(aFinanceSchData, wve);
		FinanceMain aFinanceMain = aFinanceSchData.getFinanceMain();
		boolean isOverDraft = false;

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, aFinanceMain.getProductCategory())) {
			isOverDraft = true;
		}
		wve = new ArrayList<WrongValueException>();

		try {

			if (recSave) {

				aFinanceMain.setDownPayBank(CurrencyUtil.unFormat(this.downPayBank.getActualValue(), formatter));
				aFinanceMain.setDownPaySupl(CurrencyUtil.unFormat(this.downPaySupl.getActualValue(), formatter));
				aFinanceMain.setDownPayment(CurrencyUtil.unFormat(
						(this.downPayBank.getActualValue()).add(this.downPaySupl.getActualValue()), formatter));

			} else if (!this.downPayBank.isReadonly() || !this.downPaySupl.isReadonly()) {

				this.downPayBank.clearErrorMessage();
				this.downPaySupl.clearErrorMessage();
				BigDecimal reqDwnPay = PennantApplicationUtil.getPercentageValue(this.finAmount.getActualValue(),
						aFinanceMain.getMinDownPayPerc());

				BigDecimal downPayment = this.downPayBank.getActualValue().add(this.downPaySupl.getActualValue());

				if (downPayment.compareTo(this.finAmount.getValidateValue()) > 0) {
					throw new WrongValueException(this.downPayBank,
							Labels.getLabel("MAND_FIELD_MIN",
									new String[] { Labels.getLabel("label_FinanceMainDialog_DownPayment.value"),
											reqDwnPay.toString(),
											CurrencyUtil.format(this.finAmount.getActualValue(), formatter) }));
				}

				if (downPayment.compareTo(reqDwnPay) == -1) {
					throw new WrongValueException(this.downPayBank, Labels.getLabel("PERC_MIN",
							new String[] {
									Labels.getLabel("label_" + getProductCode() + "FinanceMainDialog_DownPayBS.value"),
									CurrencyUtil.format(reqDwnPay, formatter) }));
				}
			}
			aFinanceMain.setDownPayBank(CurrencyUtil.unFormat(this.downPayBank.getActualValue(), formatter));
			aFinanceMain.setDownPaySupl(CurrencyUtil.unFormat(this.downPaySupl.getActualValue(), formatter));
			aFinanceMain.setDownPayment(CurrencyUtil
					.unFormat(this.downPayBank.getActualValue().add(this.downPaySupl.getActualValue()), formatter));

		} catch (WrongValueException we) {
			logger.error("Exception", we);
			// wve.add(we);
		}

		try {
			if (isOverDraft) {
				// validate Overdraft Limit with configured finmin and fin max amounts
				this.label_FinanceMainDialog_FinAssetValue
						.setValue(Labels.getLabel("label_FinanceMainDialog_ODFinAssetValue.value"));

				if (StringUtils.equals(FinServiceEvent.OVERDRAFTSCHD, this.moduleDefiner)) {
					if (this.finAssetValue.getValidateValue()
							.compareTo(CurrencyUtil.parse(aFinanceMain.getFinAssetValue(), formatter)) < 0) {
						throw new WrongValueException(this.finAssetValue.getCcyTextBox(),
								Labels.getLabel("NUMBER_MINVALUE_EQ",
										new String[] { Labels.getLabel("label_FinanceMainDialog_ODFinAssetValue.value"),
												String.valueOf(aFinanceMain.getFinAssetValue()) }));
					}

				}
			}
			if (this.row_FinAssetValue.isVisible()) {
				// Validate if the total disbursement amount exceeds maximum disbursement Amount
				if (((StringUtils.isEmpty(moduleDefiner)
						|| StringUtils.equals(FinServiceEvent.ADDDISB, moduleDefiner)))) {
					if (this.finCurrentAssetValue.getActualValue() != null
							&& finAssetValue.getActualValue().compareTo(BigDecimal.ZERO) > 0
							&& finCurrentAssetValue.getActualValue().compareTo(finAssetValue.getActualValue()) > 0) {
						throw new WrongValueException(finCurrentAssetValue.getCcyTextBox(),
								Labels.getLabel("NUMBER_MAXVALUE_EQ",
										new String[] { this.label_FinanceMainDialog_FinCurrentAssetValue.getValue(),
												String.valueOf(label_FinanceMainDialog_FinAssetValue.getValue()) }));
					}
				}
				aFinanceMain.setFinAssetValue(
						CurrencyUtil.unFormat(this.finAssetValue.isReadonly() ? this.finAssetValue.getActualValue()
								: this.finAssetValue.getValidateValue(), formatter));
			}
			// Validation on finAsset And fin Current Asset value based on field visibility

			if (!isOverDraft) {
				if (financeType.isFinIsAlwMD()) {
					if (this.row_FinAssetValue.isVisible() && StringUtils.isEmpty(moduleDefiner)) {

						// If max disbursement amount less than prinicpal amount validate the amount
						aFinanceMain.setFinAssetValue(
								CurrencyUtil.unFormat(this.finAssetValue.getActualValue(), formatter));
						aFinanceMain.setFinCurrAssetValue(
								CurrencyUtil.unFormat(this.finCurrentAssetValue.getActualValue(), formatter));

						if (this.row_FinAssetValue.isVisible() && finAssetValue.getActualValue() != null
								&& this.finAmount.getActualValue().compareTo(BigDecimal.ZERO) > 0
								&& finAssetValue.getActualValue().compareTo(this.finAmount.getActualValue()) < 0) {

							throw new WrongValueException(finAssetValue.getCcyTextBox(), Labels.getLabel(
									"NUMBER_MINVALUE_EQ",
									new String[] { this.label_FinanceMainDialog_FinAssetValue.getValue(), String
											.valueOf(Labels.getLabel("label_FinanceMainDialog_FinAmount.value")) }));
						}
					}
				}
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setFinAssetValue(CurrencyUtil.unFormat(this.finAssetValue.getActualValue(), formatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			ArrayList<ReasonDetails> reasonList = new ArrayList<>();
			String reasonCode = this.reasons.getValue();

			if (reasonCode == null || "".equals(reasonCode) || reasonCode.isEmpty()) {
				throw new WrongValueException(this.reasons, Labels.getLabel("FIELD_NO_INVALID",
						new String[] { Labels.getLabel("label_FinanceMainDialog_CancelReason.value") }));
			}

			for (String reasonId : reasonCode.split(",")) {
				ReasonDetails reasonDetails = new ReasonDetails();
				reasonDetails.setReasonId(Long.valueOf(reasonId));
				reasonList.add(reasonDetails);
			}
			aFinanceMain.setDetailsList(reasonList);
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceMain.setCancelRemarks(this.cancelRemarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		aFinanceMain.setManualSchedule(this.manualSchedule.isChecked());
		doRemoveValidation();
		// FinanceMain Details Tab Validation Error Throwing
		showErrorDetails(wve, financeTypeDetailsTab);

		logger.debug("Leaving");
	}

	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.reasons.isReadonly()) {
			this.reasons.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_CancelReason.value"), null, true));
		}
		logger.debug(Literal.LEAVING);
	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		this.reasons.setConstraint("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method to show error details if occurred
	 * 
	 **/
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug("Entering");

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			tab.setSelected(true);
			buildEvent = false;

			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 */
	public void doShowDialog(FinanceDetail afinanceDetail) {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		doReadOnly();
		if (StringUtils.isNotBlank(afinanceDetail.getFinScheduleData().getFinanceMain().getRecordType())) {
			this.btnNotes.setVisible(true);
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(afinanceDetail, true);
			onCheckODPenalty(false);
			if (afinanceDetail.getFinScheduleData().getFinanceMain().isNewRecord()
					&& !afinanceDetail.getFinScheduleData().getFinanceMain().isLovDescIsSchdGenerated()) {
				changeFrequencies();
				this.finReference.focus();
			}

			FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
			if (financeType.getFinMinTerm() == 1 && financeType.getFinMaxTerm() == 1) {

				this.rpyFrqRow.setVisible(false);
				this.hbox_ScheduleMethod.setVisible(false);
				this.noOfTermsRow.setVisible(false);
			}

			readOnlyComponent(true, this.reasons);
			if (this.recordStatus.getValue().equals("Submitted")) {
				readOnlyComponent(true, this.btnReasons);
				readOnlyComponent(true, this.cancelRemarks);
			} else {
				readOnlyComponent(false, this.btnReasons);
				readOnlyComponent(false, this.cancelRemarks);
			}

			doStoreServiceIds(afinanceDetail.getFinScheduleData().getFinanceMain());
			setDialog(DialogType.EMBEDDED);

		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_FinanceCancellationDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	public boolean isDataChanged(boolean close) {
		logger.debug("Entering");

		// To clear the Error Messages
		doClearMessage();
		if (super.isDataChanged(close)) {
			return true;
		}
		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceType().getFinCcy());

		BigDecimal oldDwnPaySupl = CurrencyUtil.unFormat(this.oldVar_downPaySupl, formatter);
		BigDecimal newDwnPaySupl = CurrencyUtil.unFormat(this.downPaySupl.getActualValue(), formatter);
		if (oldDwnPaySupl.compareTo(newDwnPaySupl) != 0) {
			return true;
		}
		logger.debug("Leaving");
		return false;
	}

	// CRUD operations

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		super.doReadOnly();
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
		}
		this.downPaySupl.setReadonly(true);
		this.flagDetails.setReadonly(true);
		this.applicationNo.setReadonly(true);
		this.manualSchedule.setDisabled(true);

		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		super.doClear();
		this.downPaySupl.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table.
	 */
	public void doSave() {
		logger.debug("Entering");

		FinanceDetail aFinanceDetail = new FinanceDetail();
		aFinanceDetail = ObjectUtil.clone(getFinanceDetail());

		if (this.userAction.getSelectedItem() != null) {
			if ("Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| "Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| this.userAction.getSelectedItem().getLabel().contains("Reject")
					|| this.userAction.getSelectedItem().getLabel().contains("Resubmit")
					|| this.userAction.getSelectedItem().getLabel().contains("Decline")) {
				recSave = true;
				aFinanceDetail.setActionSave(true);
			}
			aFinanceDetail.setUserAction(this.userAction.getSelectedItem().getLabel());
		}

		aFinanceDetail.setAccountingEventCode(eventCode);
		aFinanceDetail.setModuleDefiner(moduleDefiner);

		boolean isNew = false;
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		// Resetting Service Task ID's from Original State
		aFinanceMain.setRoleCode(this.curRoleCode);
		aFinanceMain.setNextRoleCode(this.curNextRoleCode);
		aFinanceMain.setTaskId(this.curTaskId);
		aFinanceMain.setNextTaskId(this.curNextTaskId);
		aFinanceMain.setNextUserId(this.curNextUserId);

		// force validation, if on, than execute by component.getValue()
		// fill the financeMain object with the components data
		doSetValidation();
		doWriteComponentsToBean(aFinanceDetail.getFinScheduleData());
		aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		// Document Details Saving
		if (getDocumentDetailDialogCtrl() != null) {
			aFinanceDetail.setDocumentDetailsList(getDocumentDetailDialogCtrl().getDocumentDetailsList());
		} else {
			aFinanceDetail.setDocumentDetailsList(null);
		}

		// Finance Cancellation Account balance Validation
		if (!isReadOnly("FinanceMainDialog_ManFinCanCelAc")) {

			BigDecimal finamt = aFinanceMain.getFinAmount().subtract(aFinanceMain.getDownPayment());
		}

		// Finance Stage Accounting Details Tab
		if (!recSave && getStageAccountingDetailDialogCtrl() != null) {
			// check if accounting rules executed or not
			if (!getStageAccountingDetailDialogCtrl().isStageAccountingsExecuted()) {
				MessageUtil.showError(Labels.getLabel("label_Finance_Calc_StageAccountings"));
				return;
			}
			if (getStageAccountingDetailDialogCtrl().getStageDisbCrSum()
					.compareTo(getStageAccountingDetailDialogCtrl().getStageDisbDrSum()) != 0) {
				MessageUtil.showError(Labels.getLabel("label_Finance_Acc_NotMatching"));
				return;
			}
		} else {
			aFinanceDetail.setStageAccountingList(null);
		}

		// Finance Accounting Details
		if (!recSave && getAccountingDetailDialogCtrl() != null) {
			// check if accounting rules executed or not
			if (!getAccountingDetailDialogCtrl().isAccountingsExecuted()) {
				MessageUtil.showError(Labels.getLabel("label_Finance_Calc_Accountings"));
				return;
			}
			if (getAccountingDetailDialogCtrl().getDisbCrSum()
					.compareTo(getAccountingDetailDialogCtrl().getDisbDrSum()) != 0) {
				MessageUtil.showError(Labels.getLabel("label_Finance_Acc_NotMatching"));
				return;
			}
		}

		// Finance CheckList Details Tab
		if (checkListChildWindow != null) {
			boolean validationSuccess = doSave_CheckList(aFinanceDetail, false);
			if (!validationSuccess) {
				return;
			}
		} else {
			aFinanceDetail.setFinanceCheckList(null);
		}

		// Write the additional validations as per below example
		// get the selected branch object from the box
		// Do data level validations here

		String tranType = "";
		if (isWorkFlowEnabled()) {

			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinanceMain.getRecordType())) {
				aFinanceMain.setVersion(aFinanceMain.getVersion() + 1);
				if (isNew) {
					aFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinanceMain.setNewRecord(true);
				}
			}

		} else {
			aFinanceMain.setVersion(aFinanceMain.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
			if (doProcess(aFinanceDetail, tranType)) {

				if (getFinanceSelectCtrl() != null) {
					refreshMaintainList();
				}

				// Customer Notification for Role Identification
				if (StringUtils.isBlank(aFinanceMain.getNextTaskId())) {
					aFinanceMain.setNextRoleCode("");
				}
				String msg = PennantApplicationUtil.getSavingStatus(aFinanceMain.getRoleCode(),
						aFinanceMain.getNextRoleCode(), aFinanceMain.getFinReference(), " Loan ",
						aFinanceMain.getRecordStatus());
				Clients.showNotification(msg, "info", null, null, -1);

				// Mail Alert Notification for Customer/Dealer/Provider...etc
				if (!"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())) {

					FinanceMain financeMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
					Notification notification = new Notification();
					notification.getTemplates().add(NotificationConstants.TEMPLATE_FOR_AE);
					notification.getTemplates().add(NotificationConstants.TEMPLATE_FOR_CN);

					notification.setModule("LOAN_CANCELLATION");
					notification.setSubModule(FinServiceEvent.CANCELFIN);
					notification.setKeyReference(financeMain.getFinReference());
					notification.setStage(financeMain.getRoleCode());
					notification.setReceivedBy(getUserWorkspace().getUserId());
					notificationService.sendNotifications(notification, aFinanceDetail, financeMain.getFinType(),
							aFinanceDetail.getDocumentDetailsList());
				}

				// User Notifications Message/Alert
				FinanceMain fm = aFinanceDetail.getFinScheduleData().getFinanceMain();
				if (fm.getNextUserId() != null) {
					publishNotification(Notify.USER, fm.getFinReference(), fm);
				} else {
					publishNotification(Notify.ROLE, fm.getFinReference(), fm);
				}

				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	// WorkFlow Creations

	private String getServiceTasks(String taskId, FinanceMain financeMain, String finishedTasks) {
		logger.debug("Entering");

		String serviceTasks = getServiceOperations(taskId, financeMain);

		if (!"".equals(finishedTasks)) {
			String[] list = finishedTasks.split(";");

			for (int i = 0; i < list.length; i++) {
				serviceTasks = serviceTasks.replace(list[i] + ";", "");
			}
		}
		logger.debug("Leaving");
		return serviceTasks;
	}

	private void setNextTaskDetails(String taskId, FinanceMain financeMain) {
		logger.debug("Entering");

		// Set the next task id
		String action = userAction.getSelectedItem().getLabel();
		String nextTaskId = StringUtils.trimToEmpty(financeMain.getNextTaskId());

		if ("".equals(nextTaskId)) {
			if ("Save".equals(action)) {
				nextTaskId = taskId + ";";
			}
		} else {
			if (!"Save".equals(action)) {
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
			}
		}

		if ("".equals(nextTaskId)) {
			nextTaskId = getNextTaskIds(taskId, financeMain);
		}

		// Set the role codes for the next tasks
		String nextRoleCode = "";

		if ("".equals(nextTaskId)) {
			nextRoleCode = getFirstTaskOwner();
		} else {
			String[] nextTasks = nextTaskId.split(";");

			if (nextTasks.length > 0) {
				for (int i = 0; i < nextTasks.length; i++) {
					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode.concat(",");
					}
					nextRoleCode += getTaskOwner(nextTasks[i]);
				}
			}
		}

		financeMain.setTaskId(taskId);
		financeMain.setNextTaskId(nextTaskId);
		financeMain.setRoleCode(getRole());
		financeMain.setNextRoleCode(nextRoleCode);

		logger.debug("Leaving");
	}

	/**
	 * Method for Processing Finance Detail Object for Database Operation
	 * 
	 * @param afinanceMain
	 * @param tranType
	 * @return
	 * @throws InterruptedException
	 * @throws JaxenException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	protected boolean doProcess(FinanceDetail aFinanceDetail, String tranType)
			throws InterruptedException, JaxenException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		boolean processCompleted = true;
		AuditHeader auditHeader = null;
		FinanceMain afinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		afinanceMain.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		afinanceMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		afinanceMain.setUserDetails(getUserWorkspace().getLoggedInUser());

		aFinanceDetail.getFinScheduleData().setFinanceMain(afinanceMain);
		aFinanceDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			afinanceMain.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			// Check for service tasks. If one exists perform the task(s)
			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, afinanceMain, finishedTasks);

			if (isNotesMandatory(taskId, afinanceMain)) {
				if (!notesEntered) {
					MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
					return false;
				}
			}

			auditHeader = getAuditHeader(aFinanceDetail, PennantConstants.TRAN_WF);

			while (!"".equals(serviceTasks)) {

				String method = serviceTasks.split(";")[0];

				if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckCollaterals)) {
					processCompleted = true;
				} else {
					FinanceDetail tFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
					setNextTaskDetails(taskId, tFinanceDetail.getFinScheduleData().getFinanceMain());
					auditHeader.getAuditDetail().setModelData(tFinanceDetail);
					processCompleted = doSaveProcess(auditHeader, method);

				}

				if (!processCompleted) {
					break;
				}

				finishedTasks += (method + ";");
				FinanceDetail tFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
				serviceTasks = getServiceTasks(taskId, tFinanceDetail.getFinScheduleData().getFinanceMain(),
						finishedTasks);

			}

			FinanceDetail tFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();

			// Check whether to proceed further or not
			String nextTaskId = getNextTaskIds(taskId, tFinanceDetail.getFinScheduleData().getFinanceMain());

			if (processCompleted && nextTaskId.equals(taskId + ";")) {
				processCompleted = false;
			}

			// Proceed further to save the details in WorkFlow
			if (processCompleted) {

				if (!"".equals(nextTaskId) || "Save".equals(userAction.getSelectedItem().getLabel())) {
					setNextTaskDetails(taskId, tFinanceDetail.getFinScheduleData().getFinanceMain());
					auditHeader.getAuditDetail().setModelData(tFinanceDetail);
					processCompleted = doSaveProcess(auditHeader, null);
				}
			}

		} else {

			auditHeader = getAuditHeader(aFinanceDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);

		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 * @throws InterruptedException
	 * @throws JaxenException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws InterruptedException, JaxenException,
			IllegalAccessException, InvocationTargetException, InterfaceException {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		FinanceDetail afinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain afinanceMain = afinanceDetail.getFinScheduleData().getFinanceMain();

		Date appDate = SysParamUtil.getAppDate();

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				auditHeader = getFinanceCancellationService().saveOrUpdate(auditHeader);

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					long finID = afinanceMain.getFinID();
					String errormsg = "";

					List<ManualAdvise> manualAdvise = manualAdviseDAO.getAdviseStatus(finID);

					List<ManualAdvise> updateList = new ArrayList<>();

					if (CollectionUtils.isNotEmpty(manualAdvise)) {
						for (ManualAdvise md : manualAdvise) {
							Date valueDate = md.getValueDate();

							BigDecimal amount = md.getAdviseAmount()
									.subtract(md.getPaidAmount().add(md.getWaivedAmount()));

							if (amount.compareTo(BigDecimal.ZERO) > 0) {
								if (DateUtil.compare(valueDate, appDate) > 0) {
									md.setStatus(PennantConstants.MANUALADVISE_CANCEL);
									md.setAdviseID(md.getAdviseID());
									updateList.add(md);
								}
							}
						}

						if (CollectionUtils.isNotEmpty(updateList)) {
							errormsg = "Manual Advise will be cancelled along with loan, Do you want to proceed yes or No";
							if (MessageUtil.confirm(errormsg) == MessageUtil.YES) {
								manualAdviseDAO.updateStatus(updateList, "");
							} else {
								return false;
							}
						}
					}

					auditHeader = getFinanceCancellationService().doApprove(auditHeader, true);

					if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getFinanceCancellationService().doReject(auditHeader);
					if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).contains(FinanceConstants.method_scheduleChange)) {
					List<String> finTypeList = getFinanceDetailService().getScheduleEffectModuleList(true);
					boolean isScheduleModify = false;
					for (String fintypeList : finTypeList) {
						if (StringUtils.equals(moduleDefiner, fintypeList)) {
							isScheduleModify = true;
							break;
						}
					}
					if (isScheduleModify) {
						afinanceMain.setScheduleChange(true);
					} else {
						afinanceMain.setScheduleChange(false);
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_FinanceCancellationDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_FinanceCancellationDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(), true);
				}
			}

			if (retValue == PennantConstants.porcessOVERIDE) {
				auditHeader.setOveride(true);
				auditHeader.setErrorMessage(null);
				auditHeader.setInfoMessage(null);
				auditHeader.setOverideMessage(null);

				if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckLimits)) {

					if (overideMap.containsKey("Limit")) {
						FinanceDetail tfinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
						tfinanceDetail.getFinScheduleData().getFinanceMain().setOverrideLimit(true);
						auditHeader.getAuditDetail().setModelData(tfinanceDetail);
					}
				}
			}
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// ******************************************************//
	// ************** Overdue Penalty Details ***************//
	// ******************************************************//

	private void onCheckODPenalty(boolean checkAction) {
		if (this.applyODPenalty.isChecked()) {

			readOnlyComponent(isReadOnly("FinanceMainDialog_oDIncGrcDays"), this.oDIncGrcDays);
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDChargeType"), this.oDChargeType);
			this.oDGraceDays.setReadonly(isReadOnly("FinanceMainDialog_oDGraceDays"));
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDChargeCalOn"), this.oDChargeCalOn);
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDAllowWaiver"), this.oDAllowWaiver);

			if (checkAction) {
				readOnlyComponent(true, this.oDChargeAmtOrPerc);
				readOnlyComponent(true, this.oDMaxWaiverPerc);
			} else {
				onChangeODChargeType(false);
				onCheckODWaiver(false);
			}

		} else {
			readOnlyComponent(true, this.oDIncGrcDays);
			readOnlyComponent(true, this.oDChargeType);
			this.oDGraceDays.setReadonly(true);
			readOnlyComponent(true, this.oDChargeCalOn);
			readOnlyComponent(true, this.oDChargeAmtOrPerc);
			readOnlyComponent(true, this.oDAllowWaiver);
			readOnlyComponent(true, this.oDMaxWaiverPerc);

			checkAction = true;
		}

		if (checkAction) {
			this.oDIncGrcDays.setChecked(false);
			if (this.applyODPenalty.isChecked()) {
				this.oDChargeType.setSelectedIndex(0);
				this.oDChargeCalOn.setSelectedIndex(0);
			}
			this.oDGraceDays.setValue(0);
			this.oDChargeAmtOrPerc.setValue(BigDecimal.ZERO);
			this.oDAllowWaiver.setChecked(false);
			this.oDMaxWaiverPerc.setValue(BigDecimal.ZERO);
		}
	}

	private void onChangeODChargeType(boolean changeAction) {
		if (changeAction) {
			this.oDChargeAmtOrPerc.setValue(BigDecimal.ZERO);
		}
		this.space_oDChargeAmtOrPerc.setSclass("mandatory");
		if (getComboboxValue(this.oDChargeType).equals(PennantConstants.List_Select)) {
			readOnlyComponent(true, this.oDChargeAmtOrPerc);
			this.space_oDChargeAmtOrPerc.setSclass("");
		} else if (getComboboxValue(this.oDChargeType).equals(ChargeType.FLAT)
				|| getComboboxValue(this.oDChargeType).equals(ChargeType.FLAT_ON_PD_MTH)) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDChargeAmtOrPerc"), this.oDChargeAmtOrPerc);
			this.oDChargeAmtOrPerc.setMaxlength(15);
			this.oDChargeAmtOrPerc.setFormat(PennantApplicationUtil.getAmountFormate(
					CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy())));
		} else {
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDChargeAmtOrPerc"), this.oDChargeAmtOrPerc);
			this.oDChargeAmtOrPerc.setMaxlength(6);
			this.oDChargeAmtOrPerc.setFormat(PennantApplicationUtil.getAmountFormate(2));
		}
	}

	private void onCheckODWaiver(boolean checkAction) {
		if (checkAction) {
			this.oDMaxWaiverPerc.setValue(BigDecimal.ZERO);
		}
		if (this.oDAllowWaiver.isChecked()) {
			this.space_oDMaxWaiverPerc.setSclass("mandatory");
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDMaxWaiverPerc"), this.oDMaxWaiverPerc);
		} else {
			readOnlyComponent(true, this.oDMaxWaiverPerc);
			this.space_oDMaxWaiverPerc.setSclass("");
		}
	}

	/**
	 * Method to store the default values if no values are entered in respective fields when validate or build schedule
	 * buttons are clicked
	 * 
	 */
	public void doStoreDefaultValues() {
		// calling method to clear the constraints
		logger.debug("Entering");
		doClearMessage();
		super.doStoreDefaultValues();
		logger.debug("Leaving");
	}

	// OnBlur Events

	/**
	 * Get the Finance Main Details from the Screen
	 */
	public FinanceMain getFinanceMain() {
		FinanceMain financeMain = super.getFinanceMain();
		financeMain.setDownPayment(CurrencyUtil.unFormat(
				this.downPayBank.getActualValue() == null ? BigDecimal.ZERO
						: this.downPayBank.getActualValue()
								.add(this.downPaySupl.getActualValue() == null ? BigDecimal.ZERO
										: this.downPaySupl.getActualValue()),
				CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy())));
		financeMain.setFinAssetValue(CurrencyUtil.unFormat(this.finAssetValue.getActualValue(),
				CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy())));
		financeMain.setFinCurrAssetValue(CurrencyUtil.unFormat(this.finCurrentAssetValue.getActualValue(),
				CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy())));
		return financeMain;
	}

	/**
	 * when user clicks on button "Notes"
	 * 
	 * @param event
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug("Entering " + event.toString());

		this.btnNotes.setSclass("");
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving " + event.toString());
	}

	public void onClick$viewCustInfo(Event event) {
		try {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("custid", this.custID.longValue());
			map.put("custCIF", this.custCIF.getValue());
			map.put("custShrtName", this.custShrtName.getValue());
			map.put("finFormatter",
					CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy()));
			map.put("finReference", this.finReference.getValue());
			map.put("finance", true);
			if (StringUtils.equals(finDivision, FinanceConstants.FIN_DIVISION_RETAIL)) {
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/FinCustomerDetailsEnq.zul",
						window_FinanceCancellationDialog, map);
			} else {
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/Enquiry/CustomerSummary.zul",
						window_FinanceCancellationDialog, map);
			}
		} catch (Exception e) {
			logger.error("Exception: Opening window", e);
		}
	}

	public void updateFinanceMain(FinanceMain financeMain) {
		logger.debug("Entering");
		getFinanceDetail().getFinScheduleData().setFinanceMain(financeMain);
		logger.debug("Leaving");

	}

	public List<DocumentDetails> getDocumentDetails() {
		if (getDocumentDetailDialogCtrl() != null) {
			return getDocumentDetailDialogCtrl().getDocumentDetailsList();
		}
		return null;
	}

	/**
	 * Method for Executing Eligibility Details
	 * 
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public FinanceDetail onExecuteStageAccDetail()
			throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		buildEvent = false;
		doWriteComponentsToBean(getFinanceDetail().getFinScheduleData());
		getFinanceDetail().setModuleDefiner(StringUtils.isEmpty(moduleDefiner) ? FinServiceEvent.ORG : moduleDefiner);
		logger.debug("Leaving");
		return getFinanceDetail();
	}

	public void onSelectCheckListDetailsTab(ForwardEvent event)
			throws ParseException, InterruptedException, IllegalAccessException, InvocationTargetException {

		this.doWriteComponentsToBean(getFinanceDetail().getFinScheduleData());

		if (getCustomerDialogCtrl() != null && getCustomerDialogCtrl().getCustomerDetails() != null) {
			getCustomerDialogCtrl().doSetLabels(getFinBasicDetails());
			getCustomerDialogCtrl().doSave_CustomerDetail(getFinanceDetail(), custDetailTab, false);
		}

		if (getFinanceCheckListReferenceDialogCtrl() != null) {
			getFinanceCheckListReferenceDialogCtrl().doSetLabels(getFinBasicDetails());
			getFinanceCheckListReferenceDialogCtrl().doWriteBeanToComponents(getFinanceDetail().getCheckList(),
					getFinanceDetail().getFinanceCheckList(), false);
		}

	}

	/**
	 * Method for Executing Eligibility Details
	 */
	public void onExecuteAccountingDetail(Boolean onLoadProcess) {
		logger.debug("Entering");

		buildEvent = false;
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		boolean isOverdraft = false;

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				getFinanceDetail().getFinScheduleData().getFinanceMain().getProductCategory())) {
			isOverdraft = true;
		}
		// Finance Accounting Details Execution
		executeAccounting(onLoadProcess);
		try {
			if (!isOverdraft) {
				this.finAmount.getValidateValue();
			} else {
				this.finAssetValue.getValidateValue();
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Executing Accounting tab Rules
	 */
	private void executeAccounting(boolean onLoadProcess) {
		logger.debug("Entering");

		if (StringUtils.isNotBlank(this.custCIF.getValue())) {

			if (onLoadProcess) {
				doWriteComponentsToBean(getFinanceDetail().getFinScheduleData());
			}
			List<ReturnDataSet> returnDataSets = new ArrayList<>();
			FinanceMain finMain = getFinanceDetail().getFinScheduleData().getFinanceMain();

			List<ReturnDataSet> accountingSetEntries = postingsPreparationUtil
					.getReveralsByFinreference(finMain.getFinReference());
			for (ReturnDataSet returnDataSet : accountingSetEntries) {
				if (!AccountingEvent.FEEPAY.equalsIgnoreCase(returnDataSet.getFinEvent())) {
					returnDataSets.add(returnDataSet);
				}
			}
			getFinanceDetail().setReturnDataSetList(returnDataSets);
			if (getAccountingDetailDialogCtrl() != null) {
				getAccountingDetailDialogCtrl().doFillAccounting(returnDataSets);
			}
		}

		logger.debug("Leaving");
	}

	public FinanceCancellationService getFinanceCancellationService() {
		return financeCancellationService;
	}

	public void setFinanceCancellationService(FinanceCancellationService financeCancellationService) {
		this.financeCancellationService = financeCancellationService;
	}

	public FinanceReferenceDetailService getFinanceReferenceDetailService() {
		return financeReferenceDetailService;
	}

	public void setFinanceReferenceDetailService(FinanceReferenceDetailService financeReferenceDetailService) {
		this.financeReferenceDetailService = financeReferenceDetailService;
	}

	public Label getLabel_FinanceMainDialog_PromotionProduct() {
		return label_FinanceMainDialog_PromotionProduct;
	}

	public void setLabel_FinanceMainDialog_PromotionProduct(Label label_FinanceMainDialog_PromotionProduct) {
		this.label_FinanceMainDialog_PromotionProduct = label_FinanceMainDialog_PromotionProduct;
	}

	public FinFeeDetailListCtrl getFinFeeDetailListCtrl() {
		return super.getFinFeeDetailListCtrl();
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	@SuppressWarnings("unchecked")
	public void onClick$btnReasons(Event event) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> resonTypesMap = new HashMap<String, Object>();
		Object dataObject = null;

		String[] resonTypes = this.reasons.getValue().split(",");
		for (int i = 0; i < resonTypes.length; i++) {
			resonTypesMap.put(resonTypes[i], null);
		}

		dataObject = ExtendedMultipleSearchListBox.show(this.window_FinanceCancellationDialog, "LoanCancelReasons",
				resonTypesMap);

		if (dataObject instanceof String) {
			this.reasons.setValue(dataObject.toString());
			this.reasons.setTooltiptext("");
		} else {
			Map<String, Object> details = (Map<String, Object>) dataObject;
			if (details != null) {
				String tempReasons = details.keySet().toString();
				tempReasons = tempReasons.replace("[", " ").replace("]", "").replace(" ", "");
				if (tempReasons.startsWith(",")) {
					tempReasons = tempReasons.substring(1);
				}
				if (tempReasons.endsWith(",")) {
					tempReasons = tempReasons.substring(0, tempReasons.length() - 1);
				}
				this.reasons.setValue(tempReasons);
			}

			// Setting tooltip with Descriptions
			String toolTipDesc = "";
			for (String key : details.keySet()) {
				Object obj = (Object) details.get(key);
				if (obj instanceof String) {
					// Do Nothing
				} else {
					ReasonCode type = (ReasonCode) obj;
					if (type != null) {
						toolTipDesc = toolTipDesc.concat(type.getCode().concat(" , "));
					}
				}
			}
			if (StringUtils.isNotBlank(toolTipDesc) && toolTipDesc.endsWith(", ")) {
				toolTipDesc = toolTipDesc.substring(0, toolTipDesc.length() - 2);
			}
			this.reasons.setTooltiptext(toolTipDesc);
		}

		logger.debug(Literal.LEAVING);
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}
}
