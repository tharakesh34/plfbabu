package com.pennant.webui.finance.financemain;

import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.overdraft.model.OverdraftScheduleDetail;

public class OverdraftScheduleDetailDialogCtrl extends GFCBaseListCtrl<FinanceScheduleDetail> {
	private static final long serialVersionUID = 6004939933729664895L;

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_OverdraftScheduleDetailDialog; // autoWired

	protected Label schdl_finType;
	protected Label schdl_finReference;
	protected Label schdl_customer;
	protected Label schdl_odBranch;
	protected Label schdl_startDate;
	protected Decimalbox schdl_odLimit;
	protected Label schdl_odYears;
	protected Label schdl_odMonths;
	protected Label schdl_dropLineFrequency;

	private Object financeMainDialogCtrl = null;
	private FinScheduleData finScheduleData = null;
	private FinanceDetail financeDetail = null;

	private Listbox listBoxSchedule;

	/**
	 * default constructor.<br>
	 */
	public OverdraftScheduleDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_OverdraftScheduleDetailDialog(ForwardEvent event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_OverdraftScheduleDetailDialog);

		// READ OVERHANDED parameters !
		if (arguments.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
			setFinanceDetail(financeDetail);
			setFinScheduleData(financeDetail.getFinScheduleData());
		}

		if (arguments.containsKey("financeMainDialogCtrl")) {
			this.financeMainDialogCtrl = (Object) arguments.get("financeMainDialogCtrl");
		}
		doShowDialog();
		logger.debug("Leaving " + event.toString());
	}

	@SuppressWarnings("rawtypes")
	public void doShowDialog() {
		logger.debug("Entering");
		try {

			// fill the components with the data
			doFillScheduleList(this.finScheduleData);

			// Set Manual Schedule Dialog Controller instance in base Controller
			if (getFinanceMainDialogCtrl() != null) {
				try {
					Class[] paramType = { this.getClass() };
					Object[] stringParameter = { this };
					if (financeMainDialogCtrl.getClass().getMethod("setOverdraftScheduleDetailDialogCtrl",
							paramType) != null) {
						financeMainDialogCtrl.getClass().getMethod("setOverdraftScheduleDetailDialogCtrl", paramType)
								.invoke(financeMainDialogCtrl, stringParameter);
					}

				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
			}

			getBorderLayoutHeight();
			this.listBoxSchedule.setHeight(this.borderLayoutHeight - 200 + "px");
			this.window_OverdraftScheduleDetailDialog.setHeight(this.borderLayoutHeight - 80 + "px");
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to fill the Schedule Listbox with provided generated schedule.
	 * 
	 * @param FinScheduleData (aFinSchData)
	 */
	public void doFillScheduleList(FinScheduleData aFinSchdData) {
		logger.debug("Entering");

		FinanceMain financeMain = aFinSchdData.getFinanceMain();

		this.schdl_finType.setValue(financeMain.getFinType());
		this.schdl_finReference.setValue(financeMain.getFinReference());
		this.schdl_odYears.setValue(String.valueOf(financeMain.getNumberOfTerms() / 12));
		this.schdl_odMonths.setValue(String.valueOf(financeMain.getNumberOfTerms() % 12));
		this.schdl_odBranch.setValue(financeMain.getFinBranch());
		this.schdl_startDate.setValue(DateUtil.formatToLongDate(financeMain.getFinStartDate()));
		this.schdl_odLimit.setValue(
				CurrencyUtil.parse(financeMain.getFinAssetValue(), CurrencyUtil.getFormat(financeMain.getFinCcy())));
		this.schdl_dropLineFrequency
				.setValue(FrequencyUtil.getFrequencyDetail(financeMain.getDroplineFrq()).getFrequencyDescription());
		this.schdl_customer.setValue(getFinanceDetail().getCustomerDetails().getCustomer().getCustCIF());

		listBoxSchedule.getItems().clear();
		Listitem item;
		int formatter = CurrencyUtil.getFormat(aFinSchdData.getFinanceMain().getFinCcy());
		for (int i = 0; i < aFinSchdData.getOverdraftScheduleDetails().size(); i++) {

			OverdraftScheduleDetail curSchd = null;
			curSchd = aFinSchdData.getOverdraftScheduleDetails().get(i);

			item = new Listitem();
			Listcell lc = new Listcell(
					DateUtil.format(curSchd.getDroplineDate(), DateFormat.SHORT_DATE.getPattern()));
			lc.setStyle("font-weight:bold;");
			item.appendChild(lc);

			String eventName = "";
			if (i == 0) {
				eventName = "Overdraft Created";
			} else if (i == (aFinSchdData.getOverdraftScheduleDetails().size() - 1)) {
				eventName = "Limit Drop / Expiry";
			} else {
				eventName = "Limit Drop";
			}
			lc = new Listcell(eventName);
			lc.setStyle("font-weight:bold;");
			item.appendChild(lc);

			lc = new Listcell(
					PennantApplicationUtil.formatRate(curSchd.getDroplineRate().doubleValue(), formatter) + "%");
			item.appendChild(lc);

			lc = new Listcell(CurrencyUtil.format(curSchd.getLimitDrop(), formatter));
			lc.setStyle("text-align:right;font-weight: bold;color:#F87217;");
			item.appendChild(lc);

			lc = new Listcell(CurrencyUtil.format(curSchd.getODLimit(), formatter));
			item.appendChild(lc);
			listBoxSchedule.appendChild(item);
		}

		logger.debug("Leaving");
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
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

}