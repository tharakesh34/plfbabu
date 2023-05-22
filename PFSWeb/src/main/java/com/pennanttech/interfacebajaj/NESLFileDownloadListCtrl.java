package com.pennanttech.interfacebajaj;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.interfacebajaj.model.FileDownlaod;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.external.NESLPrepareLoansExcelReportService;
import com.pennanttech.pff.external.NESLPrepareLoansJsonReportService;
import com.pennanttech.service.AmazonS3Bucket;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/FileDownload/DisbursementFileDownloadList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class NESLFileDownloadListCtrl extends GFCBaseListCtrl<FileDownlaod> {
	private static final long serialVersionUID = 1L;

	protected Window window_NESLFileDownloadList;
	protected Borderlayout borderLayout_NESLFileDownloadList;
	protected Paging pagingFileDownloadList;
	protected Listbox listBoxFileDownload;
	protected Button btnRefresh;
	protected Button btnexecute;

	private Button downlaod;

	protected transient ExtendedCombobox entity;
	protected Combobox monthAndYear;
	protected Combobox reportFormat;
	protected Combobox customerCategory;

	protected transient DataEngineConfig dataEngineConfig;

	protected AmazonS3Bucket bucket;

	private transient NESLPrepareLoansExcelReportService nESLPrepareLoansExcelReportService;

	private transient NESLPrepareLoansJsonReportService nESLPrepareLoansJsonReportService;
	private List<ValueLabel> reportFormatList = PennantStaticListUtil.getReportFormatList();
	private List<ValueLabel> categoryCodes = PennantStaticListUtil.getCustCtgList();

	/*
	 * default constructor.<br>
	 */
	public NESLFileDownloadListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FileDownload";
		super.pageRightName = "FileDownload";
		super.tableName = "NESL_REPORTS_INFO_VIEW";
		super.queueTableName = "NESL_REPORTS_INFO_VIEW";
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();

		Filter[] filter = null;
		if (!entity.getValue().isEmpty() && !monthAndYear.getValue().isEmpty()
				&& !monthAndYear.getSelectedItem().getValue().equals("#") && !customerCategory.getValue().isEmpty()
				&& !customerCategory.getSelectedItem().getValue().equals("#") && !reportFormat.getValue().isEmpty()
				&& !reportFormat.getSelectedItem().getValue().equals("#")) {
			filter = new Filter[4];
			filter[0] = new Filter("ENTITY", entity.getValue());
			filter[1] = new Filter("MONTHANDYEAR", monthAndYear.getValue());
			filter[2] = new Filter("CustomerCategory", customerCategory.getValue());
			filter[3] = new Filter("ReportFormat", reportFormat.getValue());
		}

		if (filter != null) {
			this.searchObject.addFilters(filter);
		}

	}

	public void onCreate$window_NESLFileDownloadList(Event event) {
		logger.debug(Literal.ENTERING);
		List<ValueLabel> monthsList = getPastTenMonths();
		fillComboBox(this.monthAndYear, "", monthsList, "");
		// Set the page level components.
		setPageComponents(window_NESLFileDownloadList, borderLayout_NESLFileDownloadList, listBoxFileDownload,
				pagingFileDownloadList);
		setItemRender(new FileDownloadListModelItemRenderer());

		registerField("ID", SortOrder.DESC);
		registerField("FILENAME ");
		registerField("PROCESSEDON EndTime");
		registerField("Status");
		registerField("FILELOCATION");
		registerField("ENTITYDESC entityCode");
		fillComboBox(this.reportFormat, "", reportFormatList, "");
		registerField("REPORTFORMAT");
		fillComboBox(this.customerCategory, "", categoryCodes, "");
		registerField("CUSTOMERCATEGORY");

		doSetFieldProperties();
		doRenderPage();
		this.listBoxFileDownload.setHeight((getContentAreaHeight() - 100) + "px");
		this.paging.setPageSize(11);
		search();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Call the FileDownload dialog with a new empty entry. <br>
	 */
	public void onClick$btnRefresh(Event event) {
		refresh();
	}

	/**
	 * Call the FileDownload dialog with a new empty entry. <br>
	 */
	public void onClick$btnexecute(Event event) {

		if (!entity.getValue().isEmpty() && !monthAndYear.getValue().isEmpty()
				&& !monthAndYear.getSelectedItem().getValue().equals("#") && !reportFormat.getValue().isEmpty()
				&& !reportFormat.getSelectedItem().getValue().equals("#")
				&& !customerCategory.getSelectedItem().getValue().equals("#")
				&& !customerCategory.getValue().isEmpty()) {
			String date = monthAndYear.getValue();
			int generatedStaus = nESLPrepareLoansJsonReportService.getFileGenerationStatusBasedOnMonth(date,
					entity.getValue(), reportFormat.getValue(), getComboboxValue(customerCategory));
			if (generatedStaus <= 0) {
				if (reportFormat.getSelectedItem().getValue().equals("JSON")) {
					nESLPrepareLoansJsonReportService.prepareJsonFileForLoansNESLReport(monthAndYear.getValue(),
							entity.getValue(), reportFormat.getValue(), getComboboxValue(customerCategory));
				} else {
					nESLPrepareLoansExcelReportService.prepareExcelFileForLoansNESLReport(monthAndYear.getValue(),
							entity.getValue(), reportFormat.getValue(), getComboboxValue(customerCategory));
				}
			} else {
				MessageUtil.showError("Report already generated for " + date);
			}
			search();
		} else {
			MessageUtil.showError("Please Select All Mandatory Fields To Generate Report");
		}

	}

	public void onClick_Downlaod(ForwardEvent event) throws IOException {
		logger.debug(Literal.ENTERING);
		try {

			Button downloadButt = (Button) event.getOrigin().getTarget();
			FileDownlaod fileDownlaod = (FileDownlaod) downloadButt.getAttribute("object");
			downloadFromServer(fileDownlaod);
			dataEngineConfig.saveDowloadHistory(fileDownlaod.getId(), getUserWorkspace().getUserDetails().getUserId());
			nESLPrepareLoansJsonReportService.updateFileDownloadCount(fileDownlaod.getId());
			search();
		} catch (IOException e) {

			MessageUtil.showError("File Not Found");
		}
		logger.debug(Literal.LEAVING);
	}

	private void downloadFromServer(FileDownlaod fileDownlaod) throws IOException {
		String filePath = fileDownlaod.getFileLocation();
		String fileName = fileDownlaod.getFileName();
		if (filePath != null && fileName != null) {
			filePath = filePath.concat("/").concat(fileName);
		}

		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		InputStream inputStream = new FileInputStream(filePath);
		int data;
		try {
			while ((data = inputStream.read()) >= 0) {
				stream.write(data);
			}
			inputStream.close();
			Filedownload.save(stream.toByteArray(), "text/plain", fileName);
		} catch (IOException e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			inputStream.close();
			stream.close();
		}
	}

	private void refresh() {
		fillComboBox(monthAndYear, PennantConstants.List_Select, getPastTenMonths(), "");
		this.entity.setValue("");
		fillComboBox(customerCategory, PennantConstants.List_Select, categoryCodes, "");
		fillComboBox(reportFormat, PennantConstants.List_Select, reportFormatList, "");
		search();
	}

	/**
	 * Item renderer for listitems in the listbox.
	 * 
	 */
	private class FileDownloadListModelItemRenderer implements ListitemRenderer<FileDownlaod>, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public void render(Listitem item, FileDownlaod fileDownlaod, int count) throws Exception {
			Listcell lc;

			lc = new Listcell(fileDownlaod.getFileName());
			lc.setParent(item);

			lc = new Listcell(fileDownlaod.getEntityCode());
			lc.setParent(item);

			lc = new Listcell(fileDownlaod.getCustomerCategory());
			lc.setParent(item);

			lc = new Listcell(DateUtil.formatToLongDate(fileDownlaod.getEndTime()));
			lc.setParent(item);

			lc = new Listcell(fileDownlaod.getStatus());
			lc.setParent(item);

			lc = new Listcell();
			downlaod = new Button();
			downlaod.addForward("onClick", self, "onClick_Downlaod");
			lc.appendChild(downlaod);
			downlaod.setLabel("Download");
			downlaod.setTooltiptext("Download");

			downlaod.setAttribute("object", fileDownlaod);

			StringBuilder builder = new StringBuilder();
			builder.append(fileDownlaod.getFileLocation());
			builder.append(File.separator);
			builder.append(fileDownlaod.getFileName());

			if ("Fail".equals(fileDownlaod.getStatus())) {
				downlaod.setDisabled(true);
				downlaod.setTooltiptext("File not available.");
			}

			lc.setParent(item);

		}
	}

	private void doSetFieldProperties() {
		this.entity.setProperties("Entity", "EntityCode", "EntityDesc", false, LengthConstants.LEN_MASTER_CODE);
		this.entity.setMandatoryStyle(true);
		this.entity.setSclass(PennantConstants.mandateSclass);
	}

	public void onFulfill$entity(Event event) {
		logger.debug(Literal.ENTERING);
		search();
		logger.debug(Literal.LEAVING);

	}

	public void onChange$reportFormat(Event event) {
		logger.debug(Literal.ENTERING);
		search();
		logger.debug(Literal.LEAVING);
	}

	public void onChange$customerCategory(Event event) {
		logger.debug(Literal.ENTERING);
		search();
		logger.debug(Literal.LEAVING);
	}

	public void onChange$monthAndYear(Event event) {
		logger.debug(Literal.ENTERING);
		search();
		logger.debug(Literal.LEAVING);
	}

	private List<ValueLabel> getPastTenMonths() {
		logger.debug(Literal.ENTERING);
		List<ValueLabel> monthsList = new ArrayList<ValueLabel>();
		DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMM-yyyy");
		SimpleDateFormat month = new SimpleDateFormat("MM");
		SimpleDateFormat year = new SimpleDateFormat("yyyy");

		Date appDate = SysParamUtil.getAppDate();
		for (int i = 0; i < 10; i++) {
			YearMonth thisMonth = YearMonth.of(Integer.parseInt(year.format(appDate)),
					Integer.parseInt(month.format(appDate)));
			monthsList.add(new ValueLabel((i) + "", thisMonth.minusMonths(i + 1).format(monthYearFormatter)));
		}
		logger.debug(Literal.LEAVING);
		return monthsList;
	}

	public DataEngineConfig getDataEngineConfig() {
		return dataEngineConfig;
	}

	public void setDataEngineConfig(DataEngineConfig dataEngineConfig) {
		this.dataEngineConfig = dataEngineConfig;
	}

	public NESLPrepareLoansJsonReportService getnESLPrepareLoansJsonReportService() {
		return nESLPrepareLoansJsonReportService;
	}

	public void setnESLPrepareLoansJsonReportService(
			NESLPrepareLoansJsonReportService nESLPrepareLoansJsonReportService) {
		this.nESLPrepareLoansJsonReportService = nESLPrepareLoansJsonReportService;
	}

	public NESLPrepareLoansExcelReportService getnESLPrepareLoansExcelReportService() {
		return nESLPrepareLoansExcelReportService;
	}

	public void setnESLPrepareLoansExcelReportService(
			NESLPrepareLoansExcelReportService nESLPrepareLoansExcelReportService) {
		this.nESLPrepareLoansExcelReportService = nESLPrepareLoansExcelReportService;
	}

}