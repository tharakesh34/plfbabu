package com.pennant.webui.incomeamortization;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.amortization.ProjectedAmortization;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.incomeamortization.IncomeAmortizationService;
import com.pennant.backend.service.incomeamortization.impl.AmortizationProcess;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.eod.constants.EodConstants;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/DataExtraction/IncomeAmortization.zul.zul file.
 * 
 */
public class IncomeAmortizationCtrl extends GFCBaseCtrl<CustEODEvent> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(IncomeAmortizationCtrl.class);

	protected Window window_IncomeAmortization;
	protected Borderlayout borderLayout_IncomeAmortization;

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
	public IncomeAmortizationCtrl() {
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
	public void onCreate$window_IncomeAmortization(Event event) {
		logger.debug(Literal.ENTERING);

		try {
			setPageComponents(this.window_IncomeAmortization);
			this.borderLayout_IncomeAmortization.setHeight(getBorderLayoutHeight());

			if (this.tabbox == null) {
				this.tabbox = (Tabbox) event.getTarget().getParent().getParent().getParent().getParent();
			}

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
	 * Start the process
	 */
	public void onClick$btn_Start(Event event) {
		logger.debug(Literal.ENTERING);

		String amzMonthEnd = doWriteComponentsToBean();

		// Validate EOD is in progress or not
		String phase = SysParamUtil.getValueAsString(PennantConstants.APP_PHASE);
		// long queuingCount = incomeAmortizationService.getCustQueuingCount();

		if (StringUtils.equals(phase, PennantConstants.APP_PHASE_EOD)) {
			MessageUtil.showError(Labels.getLabel("Amortization_EOD_Check"));
			return;
		}

		try {
			// Start amortization process
			doStartAmortizationProcess(amzMonthEnd);

		} catch (AppException e) {

			doReadOnly(false);
			this.label_Status.setValue(Labels.getLabel("label_Failed"));

			logger.error("Exception : ", e);
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * Start the process
	 */
	public void onClick$btnRefresh(Event event) {
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
	public void doStartAmortizationProcess(String amzMonthEnd) {
		logger.debug(Literal.ENTERING);

		// Application Deployment Date and AMZ Month End
		Date startDate = SysParamUtil.getValueAsDate(PennantConstants.APP_DFT_START_DATE);
		Date monthEndDate = DateUtil.getDate(amzMonthEnd, PennantConstants.DBDateFormat);

		if (startDate != null && monthEndDate != null) {

			doReadOnly(true);
			this.label_Status.setValue(Labels.getLabel("label_Started"));

			// Amortization Threads Implementation
			int finListSize = processAmortizationUsingThreads(startDate, monthEndDate);

			if (finListSize == 0) {
				doReadOnly(false);
				this.label_Status.setValue(Labels.getLabel("label_NOFinances"));
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

		// prepare AMZ Queuing and get the finances list
		this.incomeAmortizationService.prepareAMZQueuing(monthEndDate);
		List<FinanceMain> financeList = this.incomeAmortizationService.getFinListForAMZ(monthEndDate);

		if (CollectionUtils.isNotEmpty(financeList)) {

			finListSize = financeList.size();
			AtomicLong finCount = new AtomicLong();
			AmortizationProcess amortizationProcess = null;

			// configured thread count
			// int threadCount = SysParamUtil.getValueAsInt("AMZ_THREAD_COUNT");

			// Log the activity details
			ProjectedAmortization projectedAmortization = new ProjectedAmortization();
			projectedAmortization.setStartDate(startDate);
			projectedAmortization.setMonthEndDate(monthEndDate);
			projectedAmortization.setStatus(EodConstants.PROGRESS_IN_PROCESS); // 1
			projectedAmortization.setStartTime(new Timestamp(System.currentTimeMillis()));
			projectedAmortization.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());

			long amzId = this.incomeAmortizationService.saveAmortizationLog(projectedAmortization);
			projectedAmortization.setAmzLogId(amzId);

			// delete all future Amortizations
			this.incomeAmortizationService.deleteAllProjIncomeAMZByMonth(monthEndDate);

			amortizationProcess = new AmortizationProcess(projectedAmortization, financeList, finCount, finListSize,
					this.incomeAmortizationService);
			amortizationProcess.run();

			// Threads Partitioning

			projectedAmortization = null;
			amortizationProcess = null;
		}
		financeList = null;

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

		// boolean isExist = this.incomeAmortizationService.isAmortizationLogExist();
		ProjectedAmortization amzLog = this.incomeAmortizationService.getAmortizationLog();
		if (amzLog != null) {

			if (amzLog.getStatus() == EodConstants.PROGRESS_FAILED) {
				doReadOnly(false);
				this.label_Status.setValue(Labels.getLabel("label_Failed"));
			} else {
				doReadOnly(true);
				this.label_Status.setValue(Labels.getLabel("label_Inprogress"));
			}

			fillComboBox(this.monthEndDate, DateUtil.format(amzLog.getMonthEndDate(), PennantConstants.DBDateFormat),
					this.datesList, "");
		} else {
			doReadOnly(false);

			if (isTimerEvent) {
				this.label_Status.setValue(Labels.getLabel("label_Completed"));
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
			isValidComboValue(this.monthEndDate, Labels.getLabel("label_IncomeAmortization_Date.value"));
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
	 * @return
	 */
	public static List<ValueLabel> getMonthEndList() {

		List<ValueLabel> monthEndList = new ArrayList<ValueLabel>();

		Date amzMonth = DateUtil.getMonthStart(SysParamUtil.getAppDate());
		amzMonth = DateUtil.addDays(amzMonth, -1);

		monthEndList.add(new ValueLabel(DateUtil.format(amzMonth, PennantConstants.DBDateFormat),
				DateUtil.format(amzMonth, DateFormat.LONG_MONTH.getPattern())));

		return monthEndList;
	}

	/**
	 * Method for Preparing Month End List
	 */
	private void prepareMonthEndList() {

		// Application Deployment Date
		Date startDate = SysParamUtil.getValueAsDate(PennantConstants.APP_DFT_START_DATE);
		Date appDate = SysParamUtil.getAppDate();

		// Allow previous up to one year
		Date prvYrDate = DateUtil.addYears(appDate, -1);
		if (prvYrDate.compareTo(startDate) > 0) {
			startDate = prvYrDate;
		}

		// Prepare 12 Months list From any Deployment Date to Application Date
		this.datesList = PennantStaticListUtil.getMonthEndList(startDate, appDate, PennantConstants.SortOrder_DESC);
	}

	/**
	 * Maximum number of expressions in a list is 1000 </br>
	 * 
	 * @param finRefList
	 * @return
	 */
	@SuppressWarnings("unused")
	private List<FinanceMain> getFinanceMainList(List<String> finRefList) {

		int i = 0;
		int listSize = 1000;
		List<List<String>> smallList = new ArrayList<List<String>>(1);
		List<FinanceMain> finalFinList = new ArrayList<FinanceMain>(1);

		while (i + listSize < finRefList.size()) {
			smallList.add(finRefList.subList(i, i + listSize));
			i = i + listSize;
		}
		smallList.add(finRefList.subList(i, finRefList.size()));

		for (List<String> list : smallList) {
			/*
			 * List<FinanceMain> financeList = this.incomeAmortizationService.getFinMainListByFinRef(list);
			 * finalFinList.addAll(financeList);
			 * 
			 */}
		return finalFinList;
	}

	// getters / setters

	public void setIncomeAmortizationService(IncomeAmortizationService incomeAmortizationService) {
		this.incomeAmortizationService = incomeAmortizationService;
	}
}