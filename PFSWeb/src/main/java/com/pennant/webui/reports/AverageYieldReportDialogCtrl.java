package com.pennant.webui.reports;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.ReportsUtil;
import com.pennant.backend.model.systemmasters.AverageYieldReport;
import com.pennant.backend.service.reports.AverageYieldReportService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

import net.sf.jasperreports.engine.JRException;

public class AverageYieldReportDialogCtrl extends GFCBaseCtrl<AverageYieldReport> {
	private static final long serialVersionUID = 4678287540046204660L;
	private static final Logger logger = LogManager.getLogger(AverageYieldReportDialogCtrl.class);

	protected Window window_AverageYieldReportDialog;
	protected Datebox fromDate;
	protected Datebox toDate;

	protected Window dialogWindow;
	protected Tabbox tabbox;
	protected Label WindowTitle;
	protected Window parentWindow;

	private List<AverageYieldReport> averageYieldLoanReportList = new ArrayList<AverageYieldReport>();
	private List<AverageYieldReport> averageYieldProductReportList = new ArrayList<AverageYieldReport>();

	private transient AverageYieldReportService averageYieldReportService;
	private String module = "";

	public AverageYieldReportDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	public void onCreate$window_AverageYieldReportDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_AverageYieldReportDialog);

		if (arguments.containsKey("module")) {
			if ("Loan".equalsIgnoreCase(getArgument("module"))) {
				this.WindowTitle.setValue(Labels.getLabel("label_Item_AverageYieldLoanReport.value"));
				this.module = PennantConstants.AVERAGE_YIELD_LOAN_REPORT;
			}
			if ("Product".equalsIgnoreCase(getArgument("module"))) {
				this.WindowTitle.setValue(Labels.getLabel("label_Item_AverageYieldProductReport.value"));
				this.module = PennantConstants.AVERAGE_YIELD_PRODUCT_REPORT;
			}
		}

		doSetFieldProperties();

		this.window_AverageYieldReportDialog.doModal();

		logger.debug("Leaving");
	}

	private void doSetFieldProperties() {
		logger.debug("Entering");

		this.fromDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.toDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		logger.debug("Leaving");
	}

	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);

		// Close the current window
		this.window_AverageYieldReportDialog.onClose();

		// Close the current menu item
		final Borderlayout borderlayout = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
		final Tabbox tab = (Tabbox) borderlayout.getFellow("center").getFellow("divCenter")
				.getFellow("tabBoxIndexCenter");
		tab.getSelectedTab().close();

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnGenereate(Event event) throws IllegalAccessException, InvocationTargetException {
		logger.debug(Literal.ENTERING);

		doSetValidation();

		doWriteComponentsToBean();

		AverageYieldReport avgYieldReport = new AverageYieldReport();

		List<Object> list = new ArrayList<Object>();

		if (StringUtils.equals(module, PennantConstants.AVERAGE_YIELD_LOAN_REPORT)) {
			if (CollectionUtils.isEmpty(averageYieldLoanReportList)) {
				if (MessageUtil.confirm("Average yield Loan report is not occured between these dates",
						MessageUtil.OK) != MessageUtil.OK) {
					try {
						String reportSrc = PathUtil.getPath(PathUtil.REPORTS_ORGANIZATION) + "/"
								+ "AverageYieldReport_Loanwise" + ".jasper";
						createReport("AverageYieldReport_Loanwise", avgYieldReport, list, reportSrc,
								getUserWorkspace().getLoggedInUser().getFullName(), window, true);
					} catch (InterruptedException | JRException e) {
						e.printStackTrace();
					}
				}
			} else {
				list.add(this.averageYieldLoanReportList);
				try {
					String reportSrc = PathUtil.getPath(PathUtil.REPORTS_ORGANIZATION) + "/"
							+ "AverageYieldReport_Loanwise" + ".jasper";
					createReport("AverageYieldReport_Loanwise", avgYieldReport, list, reportSrc,
							getUserWorkspace().getLoggedInUser().getFullName(), window, true);
				} catch (InterruptedException | JRException e) {
					e.printStackTrace();
				}
			}
		} else {
			if (CollectionUtils.isEmpty(averageYieldProductReportList)) {
				if (MessageUtil.confirm("Average yield Product report is not occured between these dates",
						MessageUtil.OK) != MessageUtil.OK) {
					try {
						String reportSrc = PathUtil.getPath(PathUtil.REPORTS_ORGANIZATION) + "/"
								+ "AverageYieldReport_Productwise" + ".jasper";
						createReport("AverageYieldReport_Productwise", avgYieldReport, list, reportSrc,
								getUserWorkspace().getLoggedInUser().getFullName(), window, true);
					} catch (InterruptedException | JRException e) {
						e.printStackTrace();
					}
				}
			} else {
				list.add(this.averageYieldProductReportList);
				try {
					String reportSrc = PathUtil.getPath(PathUtil.REPORTS_ORGANIZATION) + "/"
							+ "AverageYieldReport_Productwise" + ".jasper";
					createReport("AverageYieldReport_Productwise", avgYieldReport, list, reportSrc,
							getUserWorkspace().getLoggedInUser().getFullName(), window, true);
				} catch (InterruptedException | JRException e) {
					e.printStackTrace();
				}
			}
		}

		this.window_AverageYieldReportDialog.setVisible(true);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.fromDate.setConstraint("");
		this.toDate.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.fromDate.setErrorMessage("");
		this.toDate.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		doClearMessage();
		doRemoveValidation();

		if (!this.fromDate.isDisabled()) {

			this.fromDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_RescheduleMentReportDialog_FromDate.value"), true));
		}

		logger.debug(Literal.LEAVING);
	}

	public void doWriteComponentsToBean() throws IllegalAccessException, InvocationTargetException {
		logger.debug(Literal.ENTERING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		Date startDate = null;
		Date endDate = null;

		// Start Date
		try {
			startDate = this.fromDate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// End Date
		try {
			endDate = this.toDate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (wve.isEmpty()) {

			if (StringUtils.equals(module, PennantConstants.AVERAGE_YIELD_LOAN_REPORT)) {
				setAverageYieldLoanReportList(
						this.averageYieldReportService.getAverageYieldLoanReportList(startDate, endDate));
			} else {
				setAverageYieldProductReportList(
						this.averageYieldReportService.getAverageYieldProductReportList(startDate, endDate));
			}
		} else {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	private void createReport(String reportName, Object object, List<Object> listData, String reportSrc,
			String userName, Window dialogWindow, boolean createExcel) throws JRException, InterruptedException {
		logger.debug(Literal.ENTERING);
		try {
			ReportsUtil.downloadExcel(reportName, object, listData, userName);
		} catch (AppException e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError("Template does not exist.");
			ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", null, null), "EN");
		}
		logger.debug(Literal.LEAVING);
	}

	public void setAverageYieldReportService(AverageYieldReportService averageYieldReportService) {
		this.averageYieldReportService = averageYieldReportService;
	}

	public void setAverageYieldLoanReportList(List<AverageYieldReport> averageYieldLoanReportList) {
		this.averageYieldLoanReportList = averageYieldLoanReportList;
	}

	public void setAverageYieldProductReportList(List<AverageYieldReport> averageYieldProductReportList) {
		this.averageYieldProductReportList = averageYieldProductReportList;
	}

}
