package com.pennanttech.pff.overdraft.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.overdraft.model.VariableOverdraftSchdDetail;

public class VariableOverdraftScheduleDetailDialogCtrl extends GFCBaseCtrl<VariableOverdraftSchdDetail> {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(VariableOverdraftScheduleDetailDialogCtrl.class);

	protected Window window_VariableOverdraftScheduleDetailDialog;
	protected Datebox date;
	protected CurrencyBox droplineAmount;

	private boolean newRecord;
	private int ccyEditField = PennantConstants.defaultCCYDecPos;
	private FinScheduleData finScheduleData;
	private boolean isEditable = false;
	private Date maturityDate = null;
	private BigDecimal odLimit = BigDecimal.ZERO;

	private VariableOverdraftSchdDetail variableOverdraftSchdDetail;
	private VariableOverdraftScheduleDialogCtrl variableOverdraftScheduleDialogCtrl;

	public VariableOverdraftScheduleDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "VariableOverdraftScheduleDetailDialog";
	}

	public void onCreate$window_VariableOverdraftScheduleDetailDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_VariableOverdraftScheduleDetailDialog);

		if (arguments.containsKey("VariableOverdraftSchdDetail")) {
			this.variableOverdraftSchdDetail = (VariableOverdraftSchdDetail) arguments
					.get("VariableOverdraftSchdDetail");
			setVariableOverdraftSchdDetail(variableOverdraftSchdDetail);
		} else {
			setVariableOverdraftSchdDetail(null);
		}

		if (arguments.containsKey("ccyEditField")) {
			this.ccyEditField = (int) arguments.get("ccyEditField");
		}

		if (arguments.containsKey("finScheduleData")) {
			this.finScheduleData = (FinScheduleData) arguments.get("finScheduleData");
		}

		if (arguments.containsKey("maturityDate")) {
			this.maturityDate = (Date) arguments.get("maturityDate");
		}

		if (arguments.containsKey("odLimit")) {
			this.odLimit = (BigDecimal) arguments.get("odLimit");
		}

		if (arguments.containsKey("VariableOverdraftScheduleDialogCtrl")) {
			this.variableOverdraftScheduleDialogCtrl = (VariableOverdraftScheduleDialogCtrl) arguments
					.get("VariableOverdraftScheduleDialogCtrl");
			setVariableOverdraftScheduleDialogCtrl(this.variableOverdraftScheduleDialogCtrl);

			if (arguments.containsKey("newRecord")) {
				this.newRecord = (boolean) arguments.get("newRecord");
			}

			if (arguments.containsKey("isEditable")) {
				this.isEditable = (boolean) arguments.get("isEditable");
			}
		}

		doCheckRights();
		doSetFieldProperties();
		doShowDialog(variableOverdraftSchdDetail);

		logger.debug(Literal.LEAVING);
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		this.btnSave.setVisible(this.isEditable);
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		this.date.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.droplineAmount.setProperties(true, ccyEditField);
	}

	private void doShowDialog(VariableOverdraftSchdDetail variableODSchdDetail) {
		logger.debug(Literal.ENTERING);

		if (enqiryModule) {
			doReadOnly();
		} else {
			if (isNewRecord()) {
				this.btnCtrl.setInitNew();
				this.date.focus();
			}
			doEdit();
		}

		try {
			doWriteBeanToComponents(variableODSchdDetail);
			this.window_VariableOverdraftScheduleDetailDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doWriteBeanToComponents(VariableOverdraftSchdDetail detail) {
		logger.debug(Literal.ENTERING);

		this.date.setValue(detail.getSchDate());
		this.droplineAmount.setValue(PennantApplicationUtil.formateAmount(detail.getDroplineAmount(), ccyEditField));

		logger.debug(Literal.LEAVING);
	}

	private void doWriteComponentsToBean(VariableOverdraftSchdDetail detail) {
		logger.debug(Literal.ENTERING);

		doSetValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			detail.setSchDate(this.date.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			detail.setDroplineAmount(
					PennantApplicationUtil.unFormateAmount(this.droplineAmount.getActualValue(), ccyEditField));
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

		setVariableOverdraftSchdDetail(detail);

		logger.debug(Literal.LEAVING);
	}

	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = this.finScheduleData.getFinanceMain();

		Date startDate = DateUtil.addDays(financeMain.getFinStartDate(), 1);

		this.date.setConstraint(
				new PTDateValidator(Labels.getLabel("label_VariableOverdraftScheduleDetailDialog_Date.value"), true,
						startDate, this.maturityDate, true));

		this.droplineAmount.setConstraint(new PTDecimalValidator(
				Labels.getLabel("label_VariableOverdraftScheduleDetailDialog_DropLineAmount.value"), this.ccyEditField,
				true, false));

		logger.debug(Literal.LEAVING);
	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.droplineAmount.setConstraint("");
		this.date.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (!isNewRecord()) {
			readOnlyComponent(true, this.date);
		} else {
			readOnlyComponent(!this.isEditable, this.date);
		}

		readOnlyComponent(!this.isEditable, this.droplineAmount);

		logger.debug(Literal.LEAVING);
	}

	private void doReadOnly() {
		readOnlyComponent(true, this.date);
		readOnlyComponent(true, this.droplineAmount);
	}

	protected void doSave() {
		logger.debug(Literal.ENTERING);

		final VariableOverdraftSchdDetail newDetail = new VariableOverdraftSchdDetail();
		BeanUtils.copyProperties(variableOverdraftSchdDetail, newDetail);

		doWriteComponentsToBean(newDetail);

		boolean isChanged = false;

		if (variableOverdraftScheduleDialogCtrl != null) {
			List<VariableOverdraftSchdDetail> variableODSchdDetailList = variableOverdraftScheduleDialogCtrl
					.getOverdraftVariableSchdDetails();

			BigDecimal droplineAmount = BigDecimal.ZERO;

			if (CollectionUtils.isNotEmpty(variableODSchdDetailList)) {
				for (VariableOverdraftSchdDetail detail : variableODSchdDetailList) {
					if (detail.getSchDate().compareTo(newDetail.getSchDate()) == 0) {
						if (isNewRecord()) {
							MessageUtil.showError(Labels.getLabel("VARIABLE_OD_DROPLINE_DATE_MATCH"));
							return;
						}

						droplineAmount = droplineAmount.add(newDetail.getDroplineAmount());
					} else {
						droplineAmount = droplineAmount.add(detail.getDroplineAmount());
					}
				}
			}

			if (droplineAmount.compareTo(this.odLimit) > 0) {
				MessageUtil.showError(Labels.getLabel("VARIABLE_OD_PRIAMT_FINAMT_NOTMATCH"));
				return;
			}

			if (isNewRecord()) {
				variableODSchdDetailList.add(newDetail);
				isChanged = true;
			} else {
				if (CollectionUtils.isNotEmpty(variableODSchdDetailList)) {
					for (VariableOverdraftSchdDetail detail : variableODSchdDetailList) {
						if (detail.getSchDate().compareTo(newDetail.getSchDate()) == 0) {
							if (newDetail.getDroplineAmount().compareTo(detail.getDroplineAmount()) != 0) {
								detail.setDroplineAmount(newDetail.getDroplineAmount());
								isChanged = true;
							}

							break;
						}
					}
				}
			}

			sortSchedules(variableODSchdDetailList);

			variableOverdraftScheduleDialogCtrl.doFillScheduleDetails(variableODSchdDetailList, isChanged);
		}

		closeDialog();

		logger.debug(Literal.LEAVING);
	}

	private List<VariableOverdraftSchdDetail> sortSchedules(List<VariableOverdraftSchdDetail> variableODSchdDetails) {
		if (CollectionUtils.isNotEmpty(variableODSchdDetails)) {
			Collections.sort(variableODSchdDetails, new Comparator<VariableOverdraftSchdDetail>() {
				@Override
				public int compare(VariableOverdraftSchdDetail odSchd1, VariableOverdraftSchdDetail odSchd2) {
					return DateUtil.compare(odSchd1.getSchDate(), odSchd2.getSchDate());
				}
			});
		}

		return variableODSchdDetails;
	}

	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public void setVariableOverdraftSchdDetail(VariableOverdraftSchdDetail variableOverdraftSchdDetail) {
		this.variableOverdraftSchdDetail = variableOverdraftSchdDetail;
	}

	public void setVariableOverdraftScheduleDialogCtrl(
			VariableOverdraftScheduleDialogCtrl variableOverdraftScheduleDialogCtrl) {
		this.variableOverdraftScheduleDialogCtrl = variableOverdraftScheduleDialogCtrl;
	}

}