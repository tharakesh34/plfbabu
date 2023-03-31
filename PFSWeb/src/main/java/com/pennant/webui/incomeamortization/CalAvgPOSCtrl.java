package com.pennant.webui.incomeamortization;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.pennant.app.core.CustEODEvent;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.amortization.ProjectedAmortization;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.incomeamortization.IncomeAmortizationService;
import com.pennant.backend.service.incomeamortization.impl.CalAvgPOSProcess;
import com.pennant.backend.util.AmortizationConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.eod.constants.EodConstants;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/DataExtraction/DataExtractionList.zul file.
 * 
 */
public class CalAvgPOSCtrl extends GFCBaseCtrl<CustEODEvent> {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(CalAvgPOSCtrl.class);

	protected Window window_CalAvgPOS;
	protected Borderlayout borderLayout_CalAvgPOS;

	protected Tabbox tabbox;
	protected Button btn_Start;
	protected Button btnRefresh;
	protected Combobox monthEndDate;
	protected Label label_Status;

	protected Timer timer;
	private boolean isInitialise = false;

	private List<ValueLabel> datesList = new ArrayList<>();
	private transient IncomeAmortizationService incomeAmortizationService;

	/**
	 * default constructor.<br>
	 */
	public CalAvgPOSCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "IncomeAmortizationDialog";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_CalAvgPOS(Event event) {
		logger.debug(Literal.ENTERING);

		try {

			if (this.tabbox == null) {
				this.tabbox = (Tabbox) event.getTarget().getParent().getParent().getParent().getParent();
			}

			setPageComponents(this.window_CalAvgPOS);
			this.borderLayout_CalAvgPOS.setHeight(getBorderLayoutHeight());

			// Prepare Month End List
			prepareMonthEndList();
			doCheckAmzProcess(false);

		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showError(e);
			closeDialog();
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Preparing Month End List
	 * 
	 * @throws ParseException
	 */
	private void prepareMonthEndList() throws ParseException {

		// Application Deployment Date
		Date startDate = getFormatDate(DateUtility.parseShortDate(AmortizationConstants.AMZ_RECALSTARTDATE));
		Date appDate = SysParamUtil.getAppDate();

		// Prepare 12 Months list From any Deployment Date to Application Date
		this.datesList = PennantStaticListUtil.getMonthEndList(startDate, appDate, PennantConstants.SortOrder_DESC);
	}

	/**
	 * Start the process
	 */
	public void onClick$btn_Start(Event event) {
		logger.debug(Literal.ENTERING);

		String monthEnd = doWriteComponentsToBean();

		// Validate EOD is in progress or not
		// String phase = SysParamUtil.getValueAsString(PennantConstants.APP_PHASE);
		long queuingCount = incomeAmortizationService.getCustQueuingCount();

		if (queuingCount > 0) {
			MessageUtil.showError(Labels.getLabel("CalAvgPOS_EOD_Check"));
			return;
		}

		boolean isAMZRunning = incomeAmortizationService.isAmortizationLogExist();

		if (isAMZRunning) {
			MessageUtil.showError(Labels.getLabel("CalAvgPOS_AMZ_Check"));
			return;
		}

		doReadOnly(true);
		this.label_Status.setValue(Labels.getLabel("label_Started_CalAvgPOS"));

		try {
			// Start amortization process
			doStartCalAvgPOSProcess(monthEnd);

		} catch (AppException e) {

			doReadOnly(false);
			this.label_Status.setValue(Labels.getLabel("label_Failed_CalAvgPOS"));

			logger.error("Exception : ", e);
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * Start the process
	 * 
	 * @throws ParseException
	 */
	public void onClick$btnRefresh(Event event) throws ParseException {
		logger.debug(Literal.ENTERING);

		this.monthEndDate.setSelectedIndex(0);
		this.monthEndDate.setErrorMessage("");
		this.label_Status.setValue("");

		prepareMonthEndList();
		fillComboBox(this.monthEndDate, "", this.datesList, "");

		logger.debug("Leaving");
	}

	/**
	 * timer event
	 * 
	 * @param event
	 */
	public void onTimer$timer(Event event) {
		doCheckAmzProcess(true);
	}

	/**
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) {
		doClose(false);
	}

	protected void doPostClose() {
		if (this.tabbox != null) {
			this.tabbox.getSelectedTab().close();
		}
	}

	/**
	 * 
	 * @param amzMonthEnd
	 */
	public void doStartCalAvgPOSProcess(String amzMonthEnd) {
		logger.debug(Literal.ENTERING);

		// Application Deployment Date and AMZ Month End
		Date startDate = getFormatDate(DateUtility.parseShortDate(AmortizationConstants.AMZ_RECALSTARTDATE));
		Date monthEndDate = DateUtility.getDate(amzMonthEnd, PennantConstants.DBDateFormat);

		if (startDate != null && monthEndDate != null) {

			// Amortization Threads Implementation
			int finListSize = processAmortizationUsingThreads(startDate, monthEndDate);

			if (finListSize == 0) {
				doReadOnly(false);
				this.label_Status.setValue(Labels.getLabel("label_NOFinances_CalAvgPOS"));
			}
		} else {
			throw new AppException(Labels.getLabel("error.unhandled"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Amortization THREAD Implementation
	 * 
	 * @param startDate
	 * @param monthEndDate
	 */
	private int processAmortizationUsingThreads(Date startDate, Date monthEndDate) {
		logger.debug("Entering");

		int finListSize = 0;
		List<FinanceMain> financeList_Thread = null;
		List<FinanceMain> financeList = new ArrayList<FinanceMain>(1);
		CalAvgPOSProcess calAvgPOSProcess = new CalAvgPOSProcess();

		financeList = this.incomeAmortizationService.getFinancesByFinApprovedDate(startDate, monthEndDate);

		if (CollectionUtils.isNotEmpty(financeList)) {

			// configured thread count
			int threadCount = SysParamUtil.getValueAsInt("AMZ_THREAD_COUNT");

			// Log the activity details
			ProjectedAmortization projectedAmortization = new ProjectedAmortization();
			AtomicLong finCount = new AtomicLong();
			finListSize = financeList.size();

			projectedAmortization.setMonthEndDate(monthEndDate);
			projectedAmortization.setStatus(EodConstants.PROGRESS_IN_PROCESS); // 1
			projectedAmortization.setStartTime(new Timestamp(System.currentTimeMillis()));
			projectedAmortization.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());

			long amzId = this.incomeAmortizationService.saveCalAvgPOSLog(projectedAmortization);
			projectedAmortization.setAmzLogId(amzId);

			// Threads Partitioning
			if (finListSize < threadCount) {

				for (int i = 0; i < finListSize; i++) {

					financeList_Thread = new ArrayList<FinanceMain>();
					financeList_Thread.add(financeList.get(i));

					calAvgPOSProcess = new CalAvgPOSProcess(projectedAmortization, financeList_Thread, finCount,
							finListSize, this.incomeAmortizationService, startDate);
					calAvgPOSProcess.start();

					if (!this.timer.isRunning()) {
						this.timer.start();
					}
				}
			} else {
				for (int i = 0; i < threadCount; i++) {

					financeList_Thread = new ArrayList<FinanceMain>();
					if (CollectionUtils.isNotEmpty(financeList)) {

						for (int j = 0; j < finListSize / threadCount; j++) {
							if (!financeList.isEmpty()) {
								financeList_Thread.add(financeList.get(0));
								financeList.remove(financeList.get(0));
							}
						}

						if ((finListSize % threadCount) != 0 && !financeList.isEmpty()) {
							financeList_Thread.add(financeList.get(0));
							financeList.remove(financeList.get(0));
						}

						calAvgPOSProcess = new CalAvgPOSProcess(projectedAmortization, financeList_Thread, finCount,
								finListSize, this.incomeAmortizationService, startDate);
						calAvgPOSProcess.start();

						if (!this.timer.isRunning()) {
							this.timer.start();
						}
					}
				}
			}
		}

		logger.debug("Leaving");
		return finListSize;
	}

	/**
	 * Method for checking amortization status
	 */
	private void doCheckAmzProcess(boolean isTimerEvent) {

		if (!isInitialise) {
			this.timer.setDelay(SysParamUtil.getValueAsInt("EOD_BATCH_REFRESH_TIME"));
			isInitialise = true;
		}

		ProjectedAmortization amzLog = this.incomeAmortizationService.getCalAvgPOSLog();
		if (amzLog != null) {

			if (amzLog.getStatus() == EodConstants.PROGRESS_FAILED) {
				doReadOnly(false);
				this.label_Status.setValue(Labels.getLabel("label_Failed_CalAvgPOS"));
			} else {
				doReadOnly(true);
				this.label_Status.setValue(Labels.getLabel("label_Inprogress_CalAvgPOS"));
			}

			fillComboBox(this.monthEndDate, DateUtility.format(amzLog.getMonthEndDate(), PennantConstants.DBDateFormat),
					this.datesList, "");
		} else {
			doReadOnly(false);

			if (isTimerEvent) {
				this.label_Status.setValue(Labels.getLabel("label_Completed_CalAvgPOS"));
			} else {
				this.label_Status.setValue("");
				fillComboBox(this.monthEndDate, "", this.datesList, "");
			}
		}
	}

	/**
	 * 
	 * @param readOnly
	 */
	private void doReadOnly(boolean readOnly) {

		this.btn_Start.setDisabled(readOnly);
		this.btnRefresh.setDisabled(readOnly);
		this.monthEndDate.setButtonVisible(!readOnly);
		this.monthEndDate.setDisabled(readOnly);

		if (!readOnly && this.timer.isRunning()) {
			this.timer.stop();
		}
	}

	/**
	 * Validate the input parameters and process
	 */
	private String doWriteComponentsToBean() {
		logger.debug(Literal.ENTERING);

		String date = null;

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			isValidComboValue(this.monthEndDate, Labels.getLabel("label_CalAvgPOS_Date.value"));
			date = getComboboxValue(this.monthEndDate);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (wve.size() > 0) {

			WrongValueException[] wvea = new WrongValueException[wve.size()];

			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}

			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
		return date;
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.monthEndDate.setConstraint("");

		logger.debug("Leaving");
	}

	/**
	 * 
	 * @param date
	 * @return
	 */
	private static Date getFormatDate(Date date) {
		return DateUtil.getDatePart(date);
	}

	// getters / setters

	public void setIncomeAmortizationService(IncomeAmortizationService incomeAmortizationService) {
		this.incomeAmortizationService = incomeAmortizationService;
	}
}