
package com.pennant.webui.reports;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.ReportsUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.RescheduleLog;
import com.pennant.backend.service.reports.RescheduleReportGenerationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

import net.sf.jasperreports.engine.JRException;

/**
 * This is the controller class for the /WEB-INF/pages/Reports/RescheduleGenerationDialog.zul file.
 */
public class RescheduleReportGenerationDialogCtrl extends GFCBaseCtrl<RescheduleLog> {
	private static final long serialVersionUID = 4678287540046204660L;
	private final static Logger logger = LogManager.getLogger(RescheduleReportGenerationDialogCtrl.class);

	protected Window window_RescheduleMentReportGenerationDialogCtrl;
	protected ExtendedCombobox finReference;
	protected Datebox fromDate;
	protected Datebox toDate;

	private List<RescheduleLog> reschedulementList = new ArrayList<RescheduleLog>();
	private transient RescheduleReportGenerationService rescheduleReportGenerationService;

	public RescheduleReportGenerationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	/**
	 * On creating Window
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_RescheduleMentReportGenerationDialogCtrl(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_RescheduleMentReportGenerationDialogCtrl);

		doSetFieldProperties();
		this.window_RescheduleMentReportGenerationDialogCtrl.doModal();

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		// Finance Reference
		this.finReference.setModuleName("FinanceMain");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setDescColumn("FinType");
		this.finReference.setDisplayStyle(2);
		this.finReference.setValidateColumns(new String[] { "FinReference" });
		this.finReference.setMandatoryStyle(true);
		this.finReference.setMaxlength(LengthConstants.LEN_REF);
		this.finReference.setTextBoxWidth(140);

		this.fromDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.toDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);

		// Close the current window
		this.window_RescheduleMentReportGenerationDialogCtrl.onClose();

		// Close the current menu item
		final Borderlayout borderlayout = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
		final Tabbox tabbox = (Tabbox) borderlayout.getFellow("center").getFellow("divCenter")
				.getFellow("tabBoxIndexCenter");
		tabbox.getSelectedTab().close();

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnGenereate(Event event) throws IllegalAccessException, InvocationTargetException {
		logger.debug(Literal.ENTERING);

		doSetValidation();

		doWriteComponentsToBean();

		RescheduleLog reschedulementLog = new RescheduleLog();
		List<Object> list = new ArrayList<>();
		list.add(this.reschedulementList);

		String reportName = "RescheduleMentReport";

		if (CollectionUtils.isEmpty(reschedulementList)
				&& MessageUtil.confirm("Reschedule service event is not occured between these dates for Loan Reference:"
						+ this.finReference.getValue(), MessageUtil.OK) == MessageUtil.OK) {
			return;
		}

		String userName = getUserWorkspace().getLoggedInUser().getFullName();
		try {
			ReportsUtil.showPDF(PathUtil.REPORTS_ORGANIZATION, reportName, reschedulementLog, list, userName);
		} catch (Exception e) {
			MessageUtil.showError(e);
			closeDialog();
		}
		this.window_RescheduleMentReportGenerationDialogCtrl.setVisible(true);
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$finReference(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		Object dataObject = finReference.getObject();

		if (dataObject instanceof String) {
			this.finReference.setValue(dataObject.toString());
		} else {
			FinanceMain details = (FinanceMain) dataObject;
			if (details != null) {
				this.finReference.setValue(details.getFinReference());
				this.fromDate.setValue(details.getFinStartDate());
				this.toDate.setValue(SysParamUtil.getAppDate());
			} else {
				this.finReference.setValue("");
				this.fromDate.setValue(null);
				this.toDate.setValue(null);
			}
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void doWriteComponentsToBean() throws IllegalAccessException, InvocationTargetException {
		logger.debug(Literal.ENTERING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		String finReference = "";
		Date startDate = null;
		Date endDate = null;
		// FinReference
		try {
			finReference = this.finReference.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

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

		doRemoveValidation();
		if (wve.isEmpty()) {

			java.sql.Date fromDate = new java.sql.Date(startDate.getTime());
			java.sql.Date toDate = new java.sql.Date(endDate.getTime());
			setReschedulementList(
					this.rescheduleReportGenerationService.getReschedulementList(finReference, fromDate, toDate));

		} else {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.finReference.setConstraint("");
		this.fromDate.setConstraint("");
		this.toDate.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.finReference.setErrorMessage("");
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

		// Finance Type
		this.finReference.setConstraint(new PTStringValidator(
				Labels.getLabel("label_RescheduleMentReportDialog_FinReference.value"), null, true, true));

		// Date appStartDate = DateUtility.getAppDate();
		Date appEndDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");

		if (!this.fromDate.isDisabled()) {

			this.fromDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_RescheduleMentReportDialog_FromDate.value"), true));
		}
		if (!this.toDate.isDisabled()) {
			try {
				this.fromDate.getValue();
				this.toDate.setConstraint(
						new PTDateValidator(Labels.getLabel("label_RescheduleMentReportDialog_ToDate.value"), true,
								this.fromDate.getValue(), appEndDate, false));
			} catch (WrongValueException we) {
				this.toDate.setConstraint(new PTDateValidator(
						Labels.getLabel("label_RescheduleMentReportDialog_ToDate.value"), true, true, null, false));
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void createReport(String reportName, Object object, List<Object> listData, String reportSrc,
			String userName, Window dialogWindow, boolean createExcel) throws JRException, InterruptedException {
		logger.debug(Literal.ENTERING);
		try {
			byte[] buf = ReportsUtil.generatePDF(reportName, object, listData, userName);

			final HashMap<String, Object> auditMap = new HashMap<String, Object>();
			auditMap.put("reportBuffer", buf);
			String genReportName = Labels.getLabel(reportName);
			auditMap.put("reportName", StringUtils.isBlank(genReportName) ? reportName : genReportName);
			if (dialogWindow != null) {
				auditMap.put("dialogWindow", dialogWindow);
			}
			Executions.createComponents("/WEB-INF/pages/Reports/ReportView.zul", null, auditMap);

		} catch (AppException e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError("Template does not exist.");
			ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", null, null), "EN");
		}
		logger.debug(Literal.LEAVING);
	}

	public RescheduleReportGenerationService getRescheduleReportGenerationService() {
		return rescheduleReportGenerationService;
	}

	public void setRescheduleReportGenerationService(
			RescheduleReportGenerationService rescheduleReportGenerationService) {
		this.rescheduleReportGenerationService = rescheduleReportGenerationService;
	}

	public List<RescheduleLog> getReschedulementList() {
		return reschedulementList;
	}

	public void setReschedulementList(List<RescheduleLog> reschedulementList) {
		this.reschedulementList = reschedulementList;
	}

}