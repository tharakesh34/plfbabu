package com.pennanttech.webui.verification;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.verification.VerificationCategory;
import com.pennanttech.pennapps.pff.verification.model.Verification;

public class FinalValuationDialogCtrl extends GFCBaseCtrl<Verification> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(FinalValuationDialogCtrl.class);

	protected Window window_FinalValuationDialog;
	protected Combobox finalValuationCollateral;
	protected CurrencyBox valuationAmountAsPerPE;
	protected CurrencyBox valuationAsPerCOP;
	protected CurrencyBox finalValuationAmount;
	protected Grid agenciesGrid;
	protected Rows finalValuationRows;
	protected Combobox decisionOnVal;
	protected Textbox finalValRemarks;

	private List<String> collateralRef = new ArrayList<>();
	private List<ValueLabel> collateralRefList = new ArrayList<>();
	private List<Verification> verificationList = new ArrayList<>();
	private TVerificationDialogCtrl tVerificationDialogCtrl;
	private List<Verification> valuationList = new ArrayList<>();
	List<ValueLabel> decisionOnValList = new ArrayList<>();
	private FinanceDetail financeDetail;

	public FinalValuationDialogCtrl() {
		super();
	}

	@SuppressWarnings("unchecked")
	public void onCreate$window_FinalValuationDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_FinalValuationDialog);

		if (arguments.containsKey("CollateralRef")) {
			collateralRef = (List<String>) arguments.get("CollateralRef");
		}
		if (arguments.containsKey("verifications")) {
			verificationList = (List<Verification>) arguments.get("verifications");
		}
		if (arguments.containsKey("tVerificationDialogCtrl")) {
			settVerificationDialogCtrl((TVerificationDialogCtrl) arguments.get("tVerificationDialogCtrl"));
		}
		if (arguments.get("enqiryModule") != null) {
			enqiryModule = (Boolean) arguments.get("enqiryModule");
		}

		if (arguments.get("financeDetail") != null) {
			this.setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
		}

		doSetFieldProperties();
		doShowDialog();

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		this.valuationAmountAsPerPE.setReadonly(true);
		this.valuationAmountAsPerPE.setProperties(false, PennantConstants.defaultCCYDecPos);
		this.finalValuationAmount.setProperties(false, PennantConstants.defaultCCYDecPos);
		this.valuationAsPerCOP.setProperties(false, PennantConstants.defaultCCYDecPos);
		for (String collRef : collateralRef) {
			collateralRefList.add(new ValueLabel(collRef, collRef));
		}
		fillComboBox(finalValuationCollateral, "", collateralRefList, "");
		decisionOnValList.add(new ValueLabel(PennantConstants.OK, Labels.getLabel("label_FinalValuationDialog_OK")));
		decisionOnValList
				.add(new ValueLabel(PennantConstants.NOTOK, Labels.getLabel("label_FinalValuationDialog_NOTOK")));
		fillComboBox(decisionOnVal, "", decisionOnValList, "");
		logger.debug(Literal.LEAVING);
	}

	private void doShowDialog() {
		logger.debug(Literal.ENTERING);
		if (enqiryModule) {
			this.btnSave.setDisabled(true);
			this.finalValuationAmount.setReadonly(true);
			this.decisionOnVal.setDisabled(true);
			this.finalValRemarks.setReadonly(true);
		}
		this.finalValuationAmount.setReadonly(true);
		this.window_FinalValuationDialog.setHeight("70%");
		this.window_FinalValuationDialog.setWidth("80%");
		this.window_FinalValuationDialog.doModal();
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnSave(Event event) throws ParseException {
		logger.debug(Literal.ENTERING);
		doSave();
		renderFinalValuationInExtendedDetails();
		logger.debug(Literal.LEAVING);
	}

	public void doSave() {
		logger.debug(Literal.ENTERING);
		doSetValidation();
		Verification verification = null;

		for (Verification verifi : verificationList) {
			if (verifi.getReferenceFor().equals(getComboboxValue(this.finalValuationCollateral))) {
				verification = verifi;
			}

		}

		doWriteComponentsToBean(verification);

		for (Verification verifi : verificationList) {
			if (verifi.getReferenceFor().equals(getComboboxValue(this.finalValuationCollateral))) {
				if (verifi.getReferenceFor().equals(verification.getReferenceFor())) {
					verifi.setFinalValAmt(verification.getFinalValAmt());
					verifi.setFinalValAsPerPE(verification.getFinalValAsPerPE());
					verifi.setFinalValDecision(verification.getFinalValDecision());
					verifi.setFinalValRemarks(verification.getFinalValRemarks());
				}
			}
		}
		this.valuationList.add(verification);
		closeDialog();
		logger.debug(Literal.ENTERING);
	}

	private void validateRemarks() {
		this.finalValRemarks.setConstraint("");
		this.finalValRemarks.setErrorMessage("");
		BigDecimal minValAmount = (BigDecimal) finalValuationAmount.getAttribute("minValue");
		if (minValAmount.compareTo(BigDecimal.ZERO) > 0) {
			minValAmount = PennantApplicationUtil.formateAmount(minValAmount, PennantConstants.defaultCCYDecPos);
		}
		BigDecimal finValAmount = this.finalValuationAmount.getActualValue();
		if (!this.finalValRemarks.isReadonly() && !this.finalValuationAmount.isReadonly()
				&& minValAmount.compareTo(finValAmount) != 0 && StringUtils.isBlank(finalValRemarks.getValue())) {
			throw new WrongValueException(finalValRemarks, Labels.getLabel("FIELD_NO_EMPTY",
					new Object[] { Labels.getLabel("label_FieldInvestigationDialog_FinalValRemarks.value") }));
		}

	}

	@SuppressWarnings("deprecation")
	private void renderFinalValuationInExtendedDetails() {
		String collaRef = this.finalValuationCollateral.getSelectedItem().getValue();
		for (CollateralSetup collateralSetup : financeDetail.getCollaterals()) {
			if (CollectionUtils.isNotEmpty(collateralSetup.getExtendedFieldRenderList())) {
				ExtendedFieldRender extendedFieldRender = collateralSetup.getExtendedFieldRenderList().get(0);
				if (extendedFieldRender != null && extendedFieldRender.getMapValues() != null) {
					Map<String, Object> map = extendedFieldRender.getMapValues();
					List<Verification> verifications = getVerificationsByReference(collaRef);
					if (CollectionUtils.isNotEmpty(verifications)) {
						for (int i = 0; i < verifications.size(); i++) {
							if (i == 0) {
								map.put("valamt1", PennantAppUtil.amountFormate(
										verifications.get(i).getValuationAmount(), PennantConstants.defaultCCYDecPos));
							} else if (i == 1) {
								map.put("valamt2", PennantAppUtil.amountFormate(
										verifications.get(i).getValuationAmount(), PennantConstants.defaultCCYDecPos));
							}
							map.put("finvalamt", PennantAppUtil.amountFormate(verifications.get(i).getFinalValAmt(),
									PennantConstants.defaultCCYDecPos));
						}
					}
				}
			}
		}
	}

	private void doSetValidation() {
		logger.debug(Literal.ENTERING);
		if (!this.finalValuationCollateral.isDisabled()) {
			this.finalValuationCollateral.setConstraint(
					new PTListValidator(Labels.getLabel("label_FinalValuationDialog_FinalValuationCollaterals.value"),
							collateralRefList, true));
		}
		if (this.finalValuationAmount.isVisible() && !this.finalValuationAmount.isReadonly()) {
			this.finalValuationAmount.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_FinalValuationDialog_FinalValuationAmount.value"), 2, true, false));
		}
		if (!this.decisionOnVal.isDisabled()) {
			this.decisionOnVal.setConstraint(new PTListValidator(
					Labels.getLabel("label_FinalValuationDialog_DecisiononValuation.value"), decisionOnValList, true));
		}
		logger.debug(Literal.LEAVING);
	}

	public void doWriteComponentsToBean(Verification verification) {
		logger.debug(Literal.LEAVING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			verification.setReferenceFor(getComboboxValue(finalValuationCollateral));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			verification.setFinalValAmt(PennantApplicationUtil
					.unFormateAmount(this.finalValuationAmount.getActualValue(), PennantConstants.defaultCCYDecPos));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			verification.setFinalValDecision(getComboboxValue(this.decisionOnVal));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			//PSD#155631
			if (ImplementationConstants.TV_FINALVAL_AMOUNT_VALD) {
				validateRemarks();
			}
			verification.setFinalValRemarks(this.finalValRemarks.getValue());
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
		logger.debug(Literal.LEAVING);
	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.finalValuationCollateral.setConstraint("");
		this.valuationAmountAsPerPE.setConstraint("");
		this.finalValuationAmount.setConstraint("");
		this.decisionOnVal.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	public void onChange$finalValuationCollateral(Event event) {
		logger.debug(Literal.ENTERING);
		this.finalValuationCollateral.setErrorMessage("");
		String collaRef = this.finalValuationCollateral.getSelectedItem().getValue();
		renderAgencies(collaRef);
		logger.debug(Literal.LEAVING);
	}

	protected void renderAgencies(String collaRef) {
		logger.debug(Literal.ENTERING);
		boolean isApproved = false;
		List<BigDecimal> verificationAmountsList = new ArrayList<>();
		this.finalValuationRows.getChildren().clear();

		this.finalValuationAmount.setValue(BigDecimal.ZERO);
		if (PennantConstants.List_Select.equalsIgnoreCase(collaRef)) {
			this.valuationAmountAsPerPE.setValue(BigDecimal.ZERO);
			this.valuationAsPerCOP.setValue(BigDecimal.ZERO);
			fillComboBox(decisionOnVal, "", decisionOnValList, "");
			this.finalValRemarks.setValue("");
		} else {
			List<Verification> verifications = getVerificationsByReference(collaRef);

			for (int i = 0; i < verifications.size(); i++) {
				Verification verification = verifications.get(i);
				if (verification.getAgencyName() != null) {
					Row row = new Row();
					row.setHeight("30px");
					Label label = new Label();
					label.setValue("Valuation Amount ");
					if (verification.getAgencyName() != null) {
						label.setValue("Valuation Amount ".concat(verification.getAgencyName()));
					}

					if ((verification.getVerificationCategory() == VerificationCategory.INTERNAL.getKey()
							|| verification.getVerificationCategory() == VerificationCategory.EXTERNAL.getKey())
							&& verification.getValuationAmount().compareTo(BigDecimal.ZERO) > 0) {
						verificationAmountsList.add(verification.getValuationAmount());
					}

					CurrencyBox currencyBox = new CurrencyBox();
					currencyBox.setId("ValAmt".concat(String.valueOf(i)));
					currencyBox.setProperties(false, PennantConstants.defaultCCYDecPos);
					currencyBox.setValue(PennantApplicationUtil.formateAmount(verification.getValuationAmount(),
							PennantConstants.defaultCCYDecPos));
					currencyBox.setReadonly(true);
					if (enqiryModule) {
						currencyBox.setReadonly(true);
					}
					row.appendChild(label);
					row.appendChild(currencyBox);
					this.finalValuationRows.appendChild(row);
				}

				this.valuationAsPerCOP.setValue(verification.getValueForCOP());
				this.valuationAmountAsPerPE.setValue(verification.getFinalValAsPerPE());
				this.finalValRemarks.setValue(StringUtils.trimToEmpty(verification.getFinalValRemarks()));
				if (!isApproved
						&& StringUtils.equals(verification.getTvRecordStatus(), PennantConstants.RCD_STATUS_APPROVED)) {
					isApproved = true;

					this.finalValuationAmount.setValue(PennantApplicationUtil
							.formateAmount(verification.getFinalValAmt(), PennantConstants.defaultCCYDecPos));

					fillComboBox(this.decisionOnVal, verification.getFinalValDecision(), decisionOnValList, "");

				}
			}

			if (CollectionUtils.isNotEmpty(verificationAmountsList)) {
				if (StringUtils.isBlank(this.finalValRemarks.getValue())) {
					this.finalValuationAmount.setValue(PennantApplicationUtil.formateAmount(
							Collections.min(verificationAmountsList), PennantConstants.defaultCCYDecPos));
				}
			}
		}
	}

	private List<Verification> getVerificationsByReference(String collaRef) {
		logger.debug(Literal.ENTERING);
		List<Verification> verifications = new ArrayList<>();
		for (int i = 0; i < this.verificationList.size(); i++) {
			Verification verification = this.verificationList.get(i);
			if (verification.getReferenceFor().equals(collaRef) && verification.getReinitid() == null) {
				verifications.add(verification);
			}
		}
		logger.debug(Literal.LEAVING);
		return verifications;
	}

	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	public TVerificationDialogCtrl gettVerificationDialogCtrl() {
		return tVerificationDialogCtrl;
	}

	public void settVerificationDialogCtrl(TVerificationDialogCtrl tVerificationDialogCtrl) {
		this.tVerificationDialogCtrl = tVerificationDialogCtrl;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}
}
