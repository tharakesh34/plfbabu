package com.pennant.webui.finance.enquiry;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.NumberToEnglishWords;
import com.pennant.app.util.ReportsUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.ChequeDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.finance.financemain.model.FinScheduleListItemRenderer;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

import net.sf.jasperreports.engine.JRException;

public class ChequePrintingDialogCtrl extends GFCBaseCtrl<FinanceScheduleDetail> {
	private static final long serialVersionUID = -2919106187676267998L;
	private static final Logger logger = LogManager.getLogger(ChequePrintingDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ChequePrintingDialog;
	protected Window window_ChequePrinting;
	protected Listbox listBoxSchedule;
	protected Borderlayout borderlayoutChequePrinting;
	private Tabpanel tabPanel_dialogWindow;
	protected Tab repayGraphTab;
	protected Div graphDivTabDiv;

	protected Combobox cbPDCPeriod;
	protected Intbox noOfCheques;
	protected Combobox startDate;
	protected Button button_Print;
	protected Iframe chequeImageView;
	public byte[] buf = null;

	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	private FinanceScheduleDetail financeScheduleDetail;
	private FinScheduleData finScheduleData;
	private FinScheduleListItemRenderer finRender;
	private transient boolean validationOn;
	private FinanceScheduleDetail prvSchDetail;
	private List<ValueLabel> listCPPDCPeriod = PennantStaticListUtil.getPDCPeriodList();
	protected Map<Integer, BigDecimal> repayDetailMap = new HashMap<Integer, BigDecimal>();

	@SuppressWarnings("unused")
	private int formatter;
	final String CHEQUE_PRINTING_CHEQUES = "Checks";

	/**
	 * default constructor.<br>
	 */
	public ChequePrintingDialogCtrl() {
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
	public void onCreate$window_ChequePrintingDialog(ForwardEvent event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ChequePrintingDialog);

		try {
			if (event.getTarget().getParent().getParent() != null) {
				tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
			}

			if (arguments.containsKey("finScheduleData")) {
				this.finScheduleData = (FinScheduleData) arguments.get("finScheduleData");
				setFinScheduleData(finScheduleData);
			} else {
				setFinScheduleData(null);
			}

			if (arguments.containsKey("financeEnquiryHeaderDialogCtrl")) {
				this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) arguments
						.get("financeEnquiryHeaderDialogCtrl");
			}

			doShowDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ChequePrintingDialog.onClose();
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 */
	public void doShowDialog() {
		logger.debug("Entering");
		try {

			if (tabPanel_dialogWindow != null) {

				getBorderLayoutHeight();
				int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount() * 20;
				this.listBoxSchedule.setHeight(this.borderLayoutHeight - rowsHeight - 200 + "px");
				this.window_ChequePrintingDialog.setHeight(this.borderLayoutHeight - rowsHeight - 30 + "px");
				tabPanel_dialogWindow.appendChild(this.window_ChequePrintingDialog);

				// fill the components with the data
				doWriteBeanToComponents(finScheduleData);

			}
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_ChequePrintingDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * When user clicks on button "button_Print" button
	 * 
	 * @param event
	 * @throws JRException
	 * @throws FileNotFoundException
	 */
	public void onClick$button_Print(Event event) throws FileNotFoundException, JRException {
		logger.debug("Entering " + event.toString());
		doSetValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			this.cbPDCPeriod.getSelectedItem().getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			isValidComboValue(this.startDate, Labels.getLabel("label_FinanceEnquiryDialog_CPStartDate.value"));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.noOfCheques.intValue();
			if (!this.noOfCheques.isDisabled()) {
				if (this.noOfCheques.intValue() == 0) {
					throw new WrongValueException(this.noOfCheques,
							Labels.getLabel("NUMBER_MINVALUE",
									new String[] { Labels.getLabel("label_FinanceEnquiryDialog_CPNoOfCheques.value"),
											String.valueOf("Zero") }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		int startIndex = 0;
		int endIndex = 0;

		startIndex = this.startDate.getSelectedIndex();
		if (wve.size() == 0 && !this.noOfCheques.isDisabled()) {
			int allowedNoCheques = (repayDetailMap.size() - startIndex + 1)
					/ (Integer.parseInt(this.cbPDCPeriod.getSelectedItem().getValue().toString()));
			if (allowedNoCheques == 0) {
				throw new WrongValueException(this.noOfCheques, Labels.getLabel("EMPTY_CHECK",
						new String[] { Labels.getLabel("label_FinanceEnquiryDialog_CPNoOfCheques.value") }));

			} else if (this.noOfCheques.intValue() != 1 && allowedNoCheques == 1) {
				throw new WrongValueException(this.noOfCheques, Labels.getLabel("ONE_CHECK",
						new String[] { Labels.getLabel("label_FinanceEnquiryDialog_CPNoOfCheques.value") }));
			} else {
				if (this.noOfCheques.intValue() > allowedNoCheques) {
					throw new WrongValueException(this.noOfCheques,
							Labels.getLabel("NUMBER_MAXVALUE_EQ",
									new String[] { Labels.getLabel("label_FinanceEnquiryDialog_CPNoOfCheques.value"),
											String.valueOf(allowedNoCheques) }));
				}
			}
		}

		doRemoveValidation();
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		ChequeDetails chequeDetails = null;
		List<ChequeDetails> chequeDetailsList = new ArrayList<ChequeDetails>();

		endIndex = startIndex + (this.noOfCheques.getValue()
				* (Integer.parseInt(this.cbPDCPeriod.getSelectedItem().getValue().toString()))) - 1;
		if (endIndex > repayDetailMap.size()) {
			endIndex = repayDetailMap.size();
		}

		if (this.cbPDCPeriod.getSelectedItem() != null) {
			if ("0000".equals(this.cbPDCPeriod.getSelectedItem().getValue())) {

				int format = CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy());
				chequeDetailsList = new ArrayList<ChequeDetails>();
				chequeDetailsList.add(null);
				chequeDetails = prepareReportObject(getFinScheduleData());
				BigDecimal repayAmt = getRepayDetails(repayDetailMap, startIndex, repayDetailMap.size());
				chequeDetails.setRepayAmount(CurrencyUtil.format(repayAmt, format));
				chequeDetails.setRepayAmountinWords(
						NumberToEnglishWords.getAmountInText(CurrencyUtil.parse(repayAmt, format),
								getFinScheduleData().getFinanceMain().getFinCcy()).toUpperCase());
				if (!"#".equals(this.startDate.getSelectedItem().getValue().toString())) {
					chequeDetails.setAppDate(DateUtil.formatToLongDate(DateUtil.parse(
							this.startDate.getSelectedItem().getValue().toString(), PennantConstants.DBDateFormat)));
				}
				chequeDetailsList.add(chequeDetails);
			} else {
				chequeDetailsList = getChequeDetailsList(chequeDetails, startIndex, endIndex);
			}
		}

		ArrayList<Object> list = new ArrayList<Object>();
		list.add(chequeDetailsList);
		String userName = getUserWorkspace().getLoggedInUser().getUserName();
		ReportsUtil.print(list, CHEQUE_PRINTING_CHEQUES, userName,
				this.financeEnquiryHeaderDialogCtrl.window_FinEnqHeaderDialog);

		doRemoveValidation();
		logger.debug("Leaving " + event.toString());

	}

	/**
	 * Get the Chequelist Detals based on the selected frequency
	 * 
	 * @param chequeDetails
	 * @param startIndex
	 * @param endIndex
	 * @return
	 */
	public List<ChequeDetails> getChequeDetailsList(ChequeDetails chequeDetails, int startIndex, int endIndex) {
		ArrayList<ChequeDetails> chequeDetailsList = new ArrayList<ChequeDetails>();
		chequeDetailsList.add(null);
		BigDecimal repaymentAmount = null;
		int pDCPeriod = Integer.parseInt(this.cbPDCPeriod.getSelectedItem().getValue().toString());
		for (int j = startIndex; j <= endIndex; j++) {
			if (pDCPeriod == 0 || j % pDCPeriod == 0) {
				chequeDetails = prepareReportObject(getFinScheduleData());
				chequeDetails.setAppDate(DateUtil.formatToLongDate(DateUtil
						.parse(this.startDate.getItemAtIndex(j).getValue().toString(), PennantConstants.DBDateFormat)));
				if (j == endIndex) {
					repaymentAmount = getRepayDetails(repayDetailMap, j - pDCPeriod + 1, repayDetailMap.size());
					if (endIndex != repayDetailMap.size()) {
						chequeDetails.setAppDate("");
					}
				} else {
					repaymentAmount = getRepayDetails(repayDetailMap, j - pDCPeriod + 1, j);
				}
				int format = CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy());
				chequeDetails.setRepayAmount(CurrencyUtil.format(repaymentAmount, format));
				chequeDetails.setRepayAmountinWords(
						NumberToEnglishWords.getAmountInText(CurrencyUtil.parse(repaymentAmount, format),
								getFinScheduleData().getFinanceMain().getFinCcy()).toUpperCase());

				chequeDetailsList.add(chequeDetails);
			}
		}
		// }
		return chequeDetailsList;
	}

	/**
	 * Get the RepayAmount from the schedule based on the selected period
	 * 
	 * @param repayDetailMap
	 * @param startIndex
	 * @param endIndex
	 * @return
	 */
	private BigDecimal getRepayDetails(Map<Integer, BigDecimal> repayDetailMap, int startIndex, int endIndex) {
		BigDecimal repayAmount = BigDecimal.ZERO;
		for (int i = startIndex; startIndex <= endIndex; startIndex++) {
			repayAmount = repayAmount.add((BigDecimal) repayDetailMap.get(i));
			i++;
		}
		return repayAmount;
	}

	/**
	 * Prepare the report object
	 * 
	 * @param finScheduleData
	 * @return
	 */
	private ChequeDetails prepareReportObject(FinScheduleData finScheduleData) {
		ChequeDetails chequeDetails = new ChequeDetails();
		chequeDetails.setFinBranchName(Labels.getLabel("label_ClientName"));// finScheduleData.getFinanceMain().getLovDescFinBranchName()
		chequeDetails.setAppDate(SysParamUtil.getAppValueDate(DateFormat.LONG_DATE));
		chequeDetails.setCustName(finScheduleData.getFinanceMain().getLovDescCustFName() + " "
				+ StringUtils.trimToEmpty(finScheduleData.getFinanceMain().getLovDescCustLName()));
		chequeDetails.setFinReference(finScheduleData.getFinanceMain().getFinType() + "-"
				+ finScheduleData.getFinanceMain().getFinReference());
		return chequeDetails;
	}

	/**
	 * On changing the Period code
	 * 
	 * @param e
	 */
	public void onChange$cbPDCPeriod(Event e) {
		logger.debug("Entering" + e.toString());

		if (StringUtils.equals(this.cbPDCPeriod.getSelectedItem().getValue().toString(),
				PennantConstants.List_Select)) {
			this.startDate.setText("");
			this.startDate.setDisabled(false);
			this.noOfCheques.setValue(0);
			this.noOfCheques.setDisabled(false);
		} else {
			if (Integer.parseInt(this.cbPDCPeriod.getSelectedItem().getValue().toString()) == 0) {
				this.startDate.setDisabled(false);
				this.startDate.setSelectedIndex(1);
				this.noOfCheques.setValue(1);
				this.noOfCheques.setDisabled(true);
				this.noOfCheques.setConstraint("");
			} else {
				this.startDate.setSelectedIndex(1);
				int allowedNoCheques = (repayDetailMap.size() - this.startDate.getSelectedIndex() + 1)
						/ (Integer.parseInt(this.cbPDCPeriod.getSelectedItem().getValue().toString()));
				this.startDate.setText("");
				this.startDate.setDisabled(false);
				this.noOfCheques.setValue(allowedNoCheques);
				this.noOfCheques.setDisabled(false);
			}
		}
		logger.debug("Leaving" + e.toString());
	}

	/**
	 * Set Validation to the components
	 */
	public void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.cbPDCPeriod.isDisabled()) {
			this.cbPDCPeriod.setConstraint(new StaticListValidator(listCPPDCPeriod,
					Labels.getLabel("label_FinanceEnquiryDialog_CPPDCPeriod.value")));
		}
		if (!this.noOfCheques.isReadonly()) {
			this.noOfCheques.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_FinanceEnquiryDialog_CPNoOfCheques.value"), true, false));

		}

		logger.debug("Leaving ");
	}

	/**
	 * Write the values to the components
	 * 
	 * @param aFinSchData
	 */
	public void doWriteBeanToComponents(FinScheduleData aFinSchData) {
		logger.debug("Entering");
		// fill the components with the data
		doFillScheduleList(this.finScheduleData);
		fillComboBox(this.cbPDCPeriod, "", listCPPDCPeriod, "");
		fillSchFromDates(this.startDate, aFinSchData.getFinanceScheduleDetails());
		logger.debug("Leaving ");
	}

	/**
	 * Fill the dates from the schedule details
	 * 
	 * @param dateCombobox
	 * @param financeScheduleDetails
	 */
	public void fillSchFromDates(Combobox dateCombobox, List<FinanceScheduleDetail> financeScheduleDetails) {
		logger.debug("Entering");

		this.startDate.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		if (financeScheduleDetails != null) {
			int count = 1;
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
				BigDecimal schedulePaid = curSchd.getSchdPftPaid().add(curSchd.getSchdPriPaid());
				if ((curSchd.isRepayOnSchDate()
						|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0))
						&& schedulePaid.compareTo(curSchd.getRepayAmount()) != 0
						&& curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0) {

					repayDetailMap.put(count, curSchd.getRepayAmount());
					comboitem = new Comboitem();
					comboitem.setAttribute("index", count);
					count++;
					comboitem.setLabel(
							DateUtil.formatToLongDate(curSchd.getSchDate()) + " " + curSchd.getSpecifier());
					comboitem.setAttribute("fromSpecifier", curSchd.getSpecifier());
					comboitem.setValue(curSchd.getSchDate());
					dateCombobox.appendChild(comboitem);
					if (getFinanceScheduleDetail() != null) {
						dateCombobox.appendChild(comboitem);
						if (curSchd.getSchDate().compareTo(getFinanceScheduleDetail().getSchDate()) == 0) {
							dateCombobox.setSelectedItem(comboitem);
						}
					}
				}
			}
		}
		logger.debug("Leaving");
	}

	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidation(false);
		this.startDate.setConstraint("");
		this.cbPDCPeriod.setConstraint("");
		this.noOfCheques.setConstraint("");

		logger.debug("Leaving");
	}

	public void doClearErrorMessage() {
		logger.debug("Entering");

		this.startDate.setErrorMessage("");
		this.cbPDCPeriod.setErrorMessage("");
		this.noOfCheques.setErrorMessage("");

		logger.debug("Leaving");
	}

	/**
	 * Method to fill the ScheduleList
	 * 
	 * @param FinanceDetail (aFinanceDetail)
	 */
	public void doFillScheduleList(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		finRender = new FinScheduleListItemRenderer();
		if (finScheduleData.getFinanceScheduleDetails() != null) {
			int sdSize = finScheduleData.getFinanceScheduleDetails().size();
			// Clear all the list items in list box
			this.listBoxSchedule.getItems().clear();

			// Find Out Finance Repayment Details on Schedule
			Map<Date, ArrayList<FinanceRepayments>> rpyDetailsMap = null;
			if (finScheduleData.getRepayDetails() != null && finScheduleData.getRepayDetails().size() > 0) {
				rpyDetailsMap = new HashMap<Date, ArrayList<FinanceRepayments>>();

				for (FinanceRepayments rpyDetail : finScheduleData.getRepayDetails()) {
					if (rpyDetailsMap.containsKey(rpyDetail.getFinSchdDate())) {
						ArrayList<FinanceRepayments> rpyDetailList = rpyDetailsMap.get(rpyDetail.getFinSchdDate());
						rpyDetailList.add(rpyDetail);
						rpyDetailsMap.put(rpyDetail.getFinSchdDate(), rpyDetailList);
					} else {
						ArrayList<FinanceRepayments> rpyDetailList = new ArrayList<FinanceRepayments>();
						rpyDetailList.add(rpyDetail);
						rpyDetailsMap.put(rpyDetail.getFinSchdDate(), rpyDetailList);
					}
				}
			}

			finScheduleData.setFinanceScheduleDetails(sortSchdDetails(finScheduleData.getFinanceScheduleDetails()));
			int formatter = CurrencyUtil.getFormat(finScheduleData.getFinanceMain().getFinCcy());
			for (int i = 0; i < sdSize; i++) {
				FinanceScheduleDetail aScheduleDetail = finScheduleData.getFinanceScheduleDetails().get(i);
				boolean showRate = false;
				if (i == 0) {
					prvSchDetail = aScheduleDetail;
					showRate = true;
				} else {
					prvSchDetail = finScheduleData.getFinanceScheduleDetails().get(i - 1);
					if (aScheduleDetail.getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate()) != 0) {
						showRate = true;
					}
				}

				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("finSchdData", finScheduleData);
				map.put("financeScheduleDetail", aScheduleDetail);
				map.put("paymentDetailsMap", rpyDetailsMap);
				map.put("formatter", formatter);
				map.put("window", this.window_ChequePrintingDialog);
				finRender.render(map, prvSchDetail, false, false, true, finScheduleData.getFinFeeDetailList(), showRate,
						false);

				if (i == sdSize - 1) {
					finRender.render(map, prvSchDetail, true, false, true, finScheduleData.getFinFeeDetailList(),
							showRate, false);
					break;
				}
			}
		}
		logger.debug("Leaving");
	}

	private List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> financeScheduleDetail) {

		if (financeScheduleDetail != null && financeScheduleDetail.size() > 0) {
			Collections.sort(financeScheduleDetail, new Comparator<FinanceScheduleDetail>() {

				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					return DateUtil.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return financeScheduleDetail;
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

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}

	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	public FinanceScheduleDetail getFinanceScheduleDetail() {
		return financeScheduleDetail;
	}

	public void setFinanceScheduleDetail(FinanceScheduleDetail financeScheduleDetail) {
		this.financeScheduleDetail = financeScheduleDetail;
	}

}
