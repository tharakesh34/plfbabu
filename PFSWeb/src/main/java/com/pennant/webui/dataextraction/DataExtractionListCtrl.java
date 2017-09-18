package com.pennant.webui.dataextraction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennanttech.bajaj.process.TaxDownlaodProcess;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * This is the controller class for the /WEB-INF/pages/DataExtraction/DataExtractionList.zul
 * file.
 * 
 */
public class DataExtractionListCtrl extends GFCBaseListCtrl<Object> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(DataExtractionListCtrl.class);

	protected Window window_DataExtractionList;

	protected Borderlayout borderLayout_DataExtractionList;
	protected Paging pagingDataExtractionList;
	protected Button btn_Process;

	protected Combobox processMonth;
	protected Combobox processName;

	private List<ValueLabel> monthList = PennantStaticListUtil.getMonthList();
	private List<ValueLabel> configNamesList = PennantStaticListUtil.getConfigNames();

	/**
	 * default constructor.<br>
	 */
	public DataExtractionListCtrl() {
		super();
	}

	/**
	 * The framework calls this event handler when an application requests that
	 * the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_DataExtractionList(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_DataExtractionList, borderLayout_DataExtractionList, null, pagingDataExtractionList);
		doRenderPage();
		doSetFieldProperties();

		logger.debug(Literal.LEAVING);
	}


	/**
	 * Set the component level properties.
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		fillComboBox(this.processMonth, "", monthList, "");
		fillComboBox(this.processName, "", configNamesList, "");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the search
	 * button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btn_Process(Event event) {

		String processMonth = null;
		String processName = null;

		doSetValidations();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {

			processMonth = getComboboxValue(this.processMonth);
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {

			processName = getComboboxValue(this.processName);
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

		process(processMonth, processName);

	}

	/**
	 * Set the component validations.
	 */
	private void doSetValidations() {
		logger.debug(Literal.ENTERING);

		this.processMonth.setConstraint(
				new PTListValidator(Labels.getLabel("label_DataExtractionList_Month.value"), monthList, true));
		this.processName.setConstraint(
				new PTListValidator(Labels.getLabel("label_DataExtractionList_ProcessName.value"), configNamesList, true));

		logger.debug(Literal.LEAVING);
	}


	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");

		this.processMonth.setConstraint("");
		this.processName.setConstraint("");

		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering ");

		fillComboBox(this.processMonth, "", monthList, "");
		fillComboBox(this.processName, "", configNamesList, "");

		logger.debug("Leaving ");
	}

	/**
	 * Call the FileDownload dialog with a new empty entry. <br>
	 */
	public void onClick$btnRefresh(Event event) throws Exception {
		doClear();
	}

	private void process(String processMonth, String processName) {
		logger.debug("Entering ");

		try {
			int month = Integer.parseInt(processMonth);

			Date processDate = DateUtility.getDate(month);
			Date appDate = DateUtility.getAppDate();

			if (DateUtility.compare(processDate, appDate) > 0) {
				processDate = DateUtility.getPreviousYearDate(processDate);
			}

			switch (processName) {
			case "GST_TAXDOWNLOAD_DETAILS":
				TaxDownlaodProcess process = new TaxDownlaodProcess((DataSource) SpringUtil.getBean("pfsDatasource"),
						getUserWorkspace().getUserDetails().getUserId(), DateUtility.getAppValueDate(),
						DateUtility.getAppDate(), DateUtility.getMonthStart(processDate), DateUtility.getMonthEnd(processDate));
				
				process.preparePosingsData();
				long count = process.setTotalRecords();
				
				if (count <= 0) {
					MessageUtil.showMessage("No records are available for the selected configuration.");
					return;
				}
				
				process.clearTaxdetails();
				process.process(processName);
				break;

			default:
				MessageUtil.showMessage("The selected configuration not available.");
				break;
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving ");
	}

}