package com.pennant.webui.finance.financemain.isradetails;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.isradetail.ISRADetail;
import com.pennant.backend.model.isradetail.ISRALiquidDetail;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.finance.financemain.FinanceMainBaseCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceRuleCode;
import com.pennapps.core.util.ObjectUtil;

public class ISRADetailDialogCtrl extends GFCBaseCtrl<ISRADetail> {
	private static final long serialVersionUID = -6945930303723518608L;
	private static final Logger logger = LogManager.getLogger(ISRADetailDialogCtrl.class);

	protected Window window_ISRADetailDialog;
	protected CurrencyBox minISRAAmt;
	protected CurrencyBox minDSRAAmt;
	protected CurrencyBox totalAmt;
	protected CurrencyBox undisbursedLimit;
	protected CurrencyBox fundsAmt;
	protected CurrencyBox shortfallAmt;
	protected CurrencyBox excessCashCltAmt;
	protected Button btnNewLiquidDetail;
	protected Groupbox finBasicdetails;
	private Listbox listBoxISRALiquidDetails;

	private FinanceDetail financeDetail = null;
	private Object financeMainDialogCtrl = null;
	private FinBasicDetailsCtrl finBasicDetailsCtrl;

	private List<ISRALiquidDetail> israLiquidDetailList = new ArrayList<ISRALiquidDetail>();
	private String roleCode = "";
	private String moduleDefiner = "";
	private ArrayList<Object> finHeaderList = new ArrayList<>();
	private int ccyEditField = PennantConstants.defaultCCYDecPos;
	private BigDecimal totalLiquidValue = BigDecimal.ZERO;
	private Date loanStartDate = null;

	public ISRADetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ISRADetailDialog";
	}

	@SuppressWarnings("unchecked")
	public void onCreate$window_ISRADetailDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_ISRADetailDialog);

		try {

			if (arguments.containsKey("financeDetail")) {
				setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
			}

			if (arguments.containsKey("moduleDefiner")) {
				this.moduleDefiner = (String) arguments.get("moduleDefiner");
			}

			if (arguments.containsKey("financeMainDialogCtrl")) {
				this.financeMainDialogCtrl = arguments.get("financeMainDialogCtrl");

				if (financeMainDialogCtrl instanceof FinanceMainBaseCtrl) {
					((FinanceMainBaseCtrl) financeMainDialogCtrl).setIsraDetailDialogCtrl(this);
				}
			}

			if (arguments.containsKey("finHeaderList")) {
				this.finHeaderList = (ArrayList<Object>) arguments.get("finHeaderList");
			}

			if (arguments.containsKey("roleCode")) {
				this.roleCode = (String) arguments.get("roleCode");
			}

			getUserWorkspace().allocateRoleAuthorities(this.roleCode, this.pageRightName);

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.financeDetail);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		getUserWorkspace().allocateAuthorities(this.pageRightName, this.roleCode);
		this.btnNewLiquidDetail.setVisible(getUserWorkspace().isAllowed("button_ISRADetailDialog_btnNewLiquidDetail"));

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.finBasicdetails.setVisible(true);

		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		this.ccyEditField = CurrencyUtil.getFormat(financeMain.getFinCcy());
		this.loanStartDate = financeMain.getFinStartDate();

		this.minISRAAmt.setProperties(false, ccyEditField);
		this.minDSRAAmt.setProperties(false, ccyEditField);
		this.totalAmt.setProperties(false, ccyEditField);
		this.undisbursedLimit.setProperties(false, ccyEditField);
		this.fundsAmt.setProperties(false, ccyEditField);
		this.shortfallAmt.setProperties(false, ccyEditField);
		this.excessCashCltAmt.setProperties(false, ccyEditField);

		this.listBoxISRALiquidDetails.setHeight(this.borderLayoutHeight - 380 + "px");
		this.window_ISRADetailDialog.setHeight(this.borderLayoutHeight - 75 + "px");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param aISRADetails The entity that need to be render.
	 */
	public void doShowDialog(FinanceDetail aFinanceDetail) throws Exception {
		logger.debug(Literal.ENTERING);

		// set ReadOnly mode accordingly if the object is new or not.
		if (aFinanceDetail.isNewRecord()) {
			doEdit();
			this.minISRAAmt.focus();
		} else {
			if (isWorkFlowEnabled() || StringUtils.isEmpty(moduleDefiner)) {
				doEdit();
			} else {
				doEdit();
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinanceDetail);

			if (financeMainDialogCtrl != null) {
				financeMainDialogCtrl.getClass().getMethod("setIsraDetailDialogCtrl", this.getClass())
						.invoke(financeMainDialogCtrl, this);
			}
		} catch (UiException e) {
			logger.error(Literal.EXCEPTION, e);
			this.window_ISRADetailDialog.onClose();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param ISRA Details
	 * 
	 */
	public void doWriteBeanToComponents(FinanceDetail aFinanceDetail) {
		logger.debug(Literal.ENTERING);

		appendFinBasicDetails();

		ISRADetail israetails = aFinanceDetail.getIsraDetail();

		if (israetails != null) {
			israetails.setNewRecord(false);
		} else {
			israetails = new ISRADetail();
			israetails.setNewRecord(true);
			aFinanceDetail.setIsraDetail(israetails);
		}

		this.minISRAAmt.setValue(PennantApplicationUtil.formateAmount(israetails.getMinISRAAmt(), ccyEditField));
		this.minDSRAAmt.setValue(PennantApplicationUtil.formateAmount(israetails.getMinDSRAAmt(), ccyEditField));
		this.totalAmt.setValue(PennantApplicationUtil.formateAmount(israetails.getTotalAmt(), ccyEditField));
		this.undisbursedLimit
				.setValue(PennantApplicationUtil.formateAmount(israetails.getUndisbursedLimit(), ccyEditField));
		this.shortfallAmt.setValue(PennantApplicationUtil.formateAmount(israetails.getShortfallAmt(), ccyEditField));
		this.excessCashCltAmt
				.setValue(PennantApplicationUtil.formateAmount(israetails.getExcessCashCltAmt(), ccyEditField));
		this.fundsAmt.setValue(PennantApplicationUtil.formateAmount(israetails.getFundsAmt(), ccyEditField));

		doFillISRALiquidDetails(israetails.getIsraLiquidDetails());

		setFundsInDsraVal(aFinanceDetail);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aISRADetails
	 */
	public void doWriteComponentsToBean(FinanceDetail aFinanceDetail) {
		logger.debug(Literal.ENTERING);

		ArrayList<WrongValueException> wve = new ArrayList<>();
		FinanceMain fm = aFinanceDetail.getFinScheduleData().getFinanceMain();
		int formatter = CurrencyUtil.getFormat(fm.getFinCcy());
		ISRADetail iSRADetails = aFinanceDetail.getIsraDetail();

		try {
			iSRADetails
					.setMinISRAAmt(PennantApplicationUtil.unFormateAmount(this.minISRAAmt.getActualValue(), formatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			iSRADetails
					.setMinDSRAAmt(PennantApplicationUtil.unFormateAmount(this.minDSRAAmt.getActualValue(), formatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			iSRADetails.setTotalAmt(PennantApplicationUtil.unFormateAmount(this.totalAmt.getActualValue(), formatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			iSRADetails.setUndisbursedLimit(
					PennantApplicationUtil.unFormateAmount(this.undisbursedLimit.getActualValue(), formatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			iSRADetails.setFundsAmt(PennantApplicationUtil.unFormateAmount(this.fundsAmt.getActualValue(), formatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			iSRADetails.setShortfallAmt(
					PennantApplicationUtil.unFormateAmount(this.shortfallAmt.getActualValue(), formatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			iSRADetails.setExcessCashCltAmt(
					PennantApplicationUtil.unFormateAmount(this.excessCashCltAmt.getActualValue(), formatter));
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

		iSRADetails.setIsraLiquidDetails(ObjectUtil.clone(this.israLiquidDetailList));
		aFinanceDetail.setIsraDetail(iSRADetails);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.minISRAAmt.isReadonly()) {
			this.minISRAAmt.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_ISRADetailsDialog_minISRAAmt.value"), ccyEditField, false, false));
		}
		if (!this.minDSRAAmt.isReadonly()) {
			this.minDSRAAmt.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_ISRADetailsDialog_MinDSRAReq.value"), ccyEditField, false, false));
		}
		if (!this.totalAmt.isReadonly()) {
			this.totalAmt.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_ISRADetailsDialog_TotalAmt.value"), ccyEditField, false, false));
		}
		if (!this.undisbursedLimit.isReadonly()) {
			this.undisbursedLimit.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_ISRADetailsDialog_Undisbursed_Limit.value"), ccyEditField, false, false));
		}
		if (!this.fundsAmt.isReadonly()) {
			this.fundsAmt.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_ISRADetailsDialog_FundsAmt.value"), ccyEditField, false, false));
		}
		if (!this.shortfallAmt.isReadonly()) {
			this.shortfallAmt.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_ISRADetailsDialog_ShortfallAmt.value"), ccyEditField, false, false));
		}
		if (!this.excessCashCltAmt.isReadonly()) {
			this.excessCashCltAmt.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_ISRADetailsDialog_ExcessCashCltAmt.value"), ccyEditField, false, false));
		}

		logger.debug(Literal.LEAVING);
	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.minISRAAmt.setConstraint("");
		this.minDSRAAmt.setConstraint("");
		this.totalAmt.setConstraint("");
		this.undisbursedLimit.setConstraint("");
		this.fundsAmt.setConstraint("");
		this.shortfallAmt.setConstraint("");
		this.excessCashCltAmt.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.minISRAAmt.setErrorMessage("");
		this.minDSRAAmt.setErrorMessage("");
		this.totalAmt.setErrorMessage("");
		this.undisbursedLimit.setErrorMessage("");
		this.fundsAmt.setErrorMessage("");
		this.shortfallAmt.setErrorMessage("");
		this.excessCashCltAmt.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	private void doEdit() {
		logger.debug(Literal.ENTERING);

		readOnlyComponent(getUserWorkspace().isReadOnly("ISRADetailDialog_minISRAAmt"), this.minISRAAmt);
		readOnlyComponent(getUserWorkspace().isReadOnly("ISRADetailDialog_minDSRAAmt"), this.minDSRAAmt);
		readOnlyComponent(getUserWorkspace().isReadOnly("ISRADetailDialog_Undisbursed_Limit"), this.undisbursedLimit);
		readOnlyComponent(true, this.fundsAmt);
		readOnlyComponent(true, this.shortfallAmt);
		readOnlyComponent(true, this.excessCashCltAmt);

		logger.debug(Literal.LEAVING);
	}

	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		readOnlyComponent(true, this.minISRAAmt);
		readOnlyComponent(true, this.minDSRAAmt);
		readOnlyComponent(true, this.totalAmt);
		readOnlyComponent(true, this.undisbursedLimit);
		readOnlyComponent(true, this.fundsAmt);
		readOnlyComponent(true, this.shortfallAmt);
		readOnlyComponent(true, this.excessCashCltAmt);

		logger.debug(Literal.LEAVING);
	}

	public void doSaveISRADetails(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		doSetValidation();
		doWriteComponentsToBean(financeDetail);

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnNewLiquidDetail(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		ISRALiquidDetail liquidDetails = new ISRALiquidDetail();
		liquidDetails.setNewRecord(true);
		liquidDetails.setWorkflowId(0);
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("ISRALiquidDetails", liquidDetails);
		map.put("ISRADetailDialogCtrl", this);
		map.put("newRecord", true);
		map.put("roleCode", this.roleCode);
		map.put("ccyEditField", ccyEditField);
		map.put("newButtonVisible", this.btnNewLiquidDetail.isVisible());
		map.put("loanStartDate", this.loanStartDate);
		try {
			Executions.createComponents("/WEB-INF/pages/ISRADetails/ISRALiquidDetailDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void doFillISRALiquidDetails(List<ISRALiquidDetail> liquidDetails) {
		logger.debug(Literal.ENTERING);

		this.listBoxISRALiquidDetails.getItems().clear();
		totalLiquidValue = BigDecimal.ZERO;

		if (CollectionUtils.isNotEmpty(liquidDetails)) {

			for (ISRALiquidDetail israLiquidDetail : liquidDetails) {

				if (!StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, israLiquidDetail.getRecordType())
						&& !StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, israLiquidDetail.getRecordType())) {
					totalLiquidValue = totalLiquidValue.add(israLiquidDetail.getAmount());
				}
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(israLiquidDetail.getName());
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(israLiquidDetail.getAmount(), ccyEditField));
				lc.setParent(item);
				lc = new Listcell(
						DateUtil.format(israLiquidDetail.getExpiryDate(), DateFormat.SHORT_DATE.getPattern()));
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(israLiquidDetail.getRecordType()));
				lc.setParent(item);
				item.setAttribute("data", israLiquidDetail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onLiquidDetailItemDoubleClicked");
				this.listBoxISRALiquidDetails.appendChild(item);
			}
		}

		Listitem item = new Listitem();
		Listcell lc;
		lc = new Listcell("Total of Liquid Instruments");
		lc.setStyle("font-weight:bold;");
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.amountFormate(totalLiquidValue, ccyEditField));
		lc.setSpan(1);
		lc.setStyle("font-weight:bold; text-align:left;");
		lc.setParent(item);
		lc = new Listcell();
		lc.setSpan(2);
		lc.setStyle("cursor:default");
		lc.setParent(item);
		this.listBoxISRALiquidDetails.appendChild(item);

		setTotalIsraDsraValue();

		setIsraLiquidDetailList(liquidDetails);
		logger.debug(Literal.LEAVING);
	}

	public void onLiquidDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug(Literal.ENTERING + event.toString());
		final Listitem item = this.listBoxISRALiquidDetails.getSelectedItem();
		if (item != null) {
			int index = item.getIndex();
			ISRALiquidDetail liquidDetail = (ISRALiquidDetail) item.getAttribute("data");

			if (isDeleteRecord(liquidDetail.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final Map<String, Object> map = new HashMap<String, Object>();

				map.put("ISRALiquidDetails", liquidDetail);
				map.put("ISRADetailDialogCtrl", this);
				map.put("roleCode", this.roleCode);
				map.put("index", index);
				map.put("newButtonVisible", this.btnNewLiquidDetail.isVisible());
				map.put("loanStartDate", this.loanStartDate);

				try {
					Executions.createComponents("/WEB-INF/pages/ISRADetails/ISRALiquidDetailDialog.zul", null, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private boolean isDeleteRecord(String rcdType) {
		if (StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, rcdType)
				|| StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, rcdType)) {
			return true;
		}
		return false;
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails() {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			map.put("finHeaderList", this.finHeaderList);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}
	}

	// +++++++++++++++++ Calculations Started +++++++++++++++++//

	public void onFulfill$minISRAAmt(Event event) {
		setTotalIsraDsraValue();
	}

	public void onFulfill$minDSRAAmt(Event event) {
		setTotalIsraDsraValue();
	}

	public void onFulfill$undisbursedLimit(Event event) {
		setTotalIsraDsraValue();
	}

	private void setTotalIsraDsraValue() {

		BigDecimal israAmt = PennantApplicationUtil.unFormateAmount(this.minISRAAmt.getActualValue(), ccyEditField);
		BigDecimal dsraAmt = PennantApplicationUtil.unFormateAmount(this.minDSRAAmt.getActualValue(), ccyEditField);

		this.totalAmt.setValue(PennantApplicationUtil.formateAmount(israAmt.add(dsraAmt), ccyEditField));

		setShortfallOrExcessValue();
	}

	private void setShortfallOrExcessValue() {

		BigDecimal totIsraDsraVal = PennantApplicationUtil.unFormateAmount(this.totalAmt.getActualValue(),
				ccyEditField);
		BigDecimal totLiquidIntrmnts = totalLiquidValue
				.add(PennantApplicationUtil.unFormateAmount(this.undisbursedLimit.getActualValue(), ccyEditField))
				.add(PennantApplicationUtil.unFormateAmount(this.fundsAmt.getActualValue(), ccyEditField));

		if (totIsraDsraVal.compareTo(totLiquidIntrmnts) > 0) {
			this.shortfallAmt.setValue(
					PennantApplicationUtil.formateAmount(totIsraDsraVal.subtract(totLiquidIntrmnts), ccyEditField));
			this.excessCashCltAmt.setValue(BigDecimal.ZERO);
		} else {
			this.excessCashCltAmt.setValue(
					PennantApplicationUtil.formateAmount(totLiquidIntrmnts.subtract(totIsraDsraVal), ccyEditField));
			this.shortfallAmt.setValue(BigDecimal.ZERO);
		}
	}

	public void setFundsInDsraVal(FinanceDetail fd) {

		if (fd.getFinScheduleData().getFinanceType().isDsfReq()) {
			List<FinFeeDetail> finFeeDetailList = fd.getFinScheduleData().getFinFeeDetailList();
			FinFeeDetail dsfFeeDetail = null;

			if (CollectionUtils.isNotEmpty(finFeeDetailList)) {
				for (FinFeeDetail finFeeDetail : finFeeDetailList) {
					if (StringUtils.equals(AdvanceRuleCode.DSF.name(), finFeeDetail.getFeeTypeCode())) {
						dsfFeeDetail = finFeeDetail;
						break;
					}
				}
			}

			if (dsfFeeDetail != null) {
				this.fundsAmt.setValue(PennantApplicationUtil.formateAmount(dsfFeeDetail.getNetAmount(), ccyEditField));
			} else {
				this.fundsAmt.setValue(BigDecimal.ZERO);
			}
		}

		setTotalIsraDsraValue();
	}

	// +++++++++++++++++ Calculations Ended +++++++++++++++++//

	public void closeDialog() {
		deAllocateAuthorities("ISRALiquidDetailDialog");
		super.closeDialog();
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		finBasicDetailsCtrl.doWriteBeanToComponents(finHeaderList);
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public List<ISRALiquidDetail> getIsraLiquidDetailList() {
		return israLiquidDetailList;
	}

	public void setIsraLiquidDetailList(List<ISRALiquidDetail> israLiquidDetailList) {
		this.israLiquidDetailList = israLiquidDetailList;
	}

	public Date getLoanStartDate() {
		return loanStartDate;
	}

	public void setLoanStartDate(Date loanStartDate) {
		this.loanStartDate = loanStartDate;
	}

}
