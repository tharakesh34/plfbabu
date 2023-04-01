package com.pennant.webui.dataextraction;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.sql.DataSource;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkmax.zul.Filedownload;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.Property;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.external.gst.TaxDownlaodExtract;
import com.pennanttech.pff.model.external.gst.TaxDownload;
import com.pennanttech.pff.staticlist.AppStaticList;

/**
 * This is the controller class for the /WEB-INF/pages/DataExtraction/DataExtractionList.zul file.
 * 
 */
public class DataExtractionListCtrl extends GFCBaseListCtrl<Object> {
	private static final long serialVersionUID = 1L;

	protected Window window_DataExtractionList;

	protected Borderlayout borderLayout_DataExtractionList;
	protected Paging pagingDataExtractionList;
	protected Button btn_Import;
	protected Button btn_Download;

	protected Combobox processMonth;
	protected Combobox configName;

	private List<Property> months = AppStaticList.getMonths();
	private List<ValueLabel> configNamesList = PennantStaticListUtil.getConfigNames();

	/**
	 * default constructor.<br>
	 */
	public DataExtractionListCtrl() {
		super();
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
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
		fillList(this.processMonth, months, "");
		fillComboBox(this.configName, "", configNamesList, "");
	}

	/**
	 * Import the Data from Pennant data source into Interface data source(Bajaj)
	 */
	public void onClick$btn_Import(Event event) {
		validateAndProcess(false);
	}

	/**
	 * Download the data from Interface data source into browser as excel file format
	 */
	public void onClick$btn_Download(Event event) {
		validateAndProcess(true);
	}

	/**
	 * Validate the input parameters and process
	 */
	private void validateAndProcess(boolean isDownloadProcess) {
		String processMonth = null;
		String configName = null;

		doSetValidations();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			processMonth = getComboboxValue(this.processMonth);
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			configName = getComboboxValue(this.configName);
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
		process(processMonth, configName, isDownloadProcess);
	}

	/**
	 * Set the component validations.
	 */
	private void doSetValidations() {
		this.processMonth.setConstraint(
				new PTListValidator(Labels.getLabel("label_DataExtractionList_Month.value"), months, true));
		this.configName.setConstraint(new PTListValidator(Labels.getLabel("label_DataExtractionList_ProcessName.value"),
				configNamesList, true));
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		this.processMonth.setConstraint("");
		this.configName.setConstraint("");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		fillList(this.processMonth, months, "");
		fillComboBox(this.configName, "", configNamesList, "");
	}

	/**
	 * Call the FileDownload dialog with a new empty entry. <br>
	 */
	public void onClick$btnRefresh(Event event) {
		doClear();
	}

	/**
	 * Import the Data from Pennant data source into Interface data source(Bajaj) Or Download the data from Interface
	 * data source into browser as excel file format Based process Invocation
	 * 
	 * @param processMonth
	 * @param configName
	 * @param processType(Download/SaveData)
	 */
	private void process(String processMonth, String configName, boolean isDownloadProcess) {
		logger.debug(Literal.ENTERING);

		try {
			int month = Integer.parseInt(processMonth);
			Date processDate = DateUtil.getSysDateByMonth(month);
			Date appDate = SysParamUtil.getAppDate();
			String msg = null;

			if (DateUtil.compare(processDate, appDate) > 0) {
				processDate = DateUtil.addYears(processDate, -1);
			}

			switch (configName) {
			case "GST_TAXDOWNLOAD_DETAILS_TRANASCTION":
				TaxDownlaodExtract trnPocess = new TaxDownlaodExtract((DataSource) SpringUtil.getBean("dataSource"),
						getUserWorkspace().getUserDetails().getUserId(), SysParamUtil.getAppValueDate(),
						SysParamUtil.getAppDate(), DateUtil.getMonthStart(processDate),
						DateUtil.getMonthEnd(processDate));
				if (isDownloadProcess) {
					msg = downloadGstTransactionData(configName, trnPocess);
				} else {
					msg = saveGstTransactionData(configName, trnPocess);
				}
				MessageUtil.showMessage(msg);
				trnPocess = null;
				break;
			case "GST_TAXDOWNLOAD_DETAILS_SUMMARY":
				TaxDownlaodExtract summProcess = new TaxDownlaodExtract((DataSource) SpringUtil.getBean("dataSource"),
						getUserWorkspace().getUserDetails().getUserId(), SysParamUtil.getAppValueDate(),
						SysParamUtil.getAppDate(), DateUtil.getMonthStart(processDate),
						DateUtil.getMonthEnd(processDate));
				if (isDownloadProcess) {
					msg = downloadGstSummaryData(configName, summProcess);
				} else {
					msg = saveGstSummaryData(configName, summProcess);
				}
				MessageUtil.showMessage(msg);
				summProcess = null;
				break;

			default:
				MessageUtil.showMessage(Labels.getLabel("label_DataExtraction_Confignotavailable.value"));
				break;
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Save GstTransaction Data from Pennant datasource into Interface datasource(Bajaj)
	 */
	private String saveGstTransactionData(String configName, TaxDownlaodExtract process) {

		process.clearTables();
		process.preparePosingsData();
		long count = process.setGstTranasactioRecords();

		if (count <= 0) {
			return Labels.getLabel("label_DataExtraction_NoRecordsavailable.value");
		}
		process.processGstTransactionData();
		DataEngineStatus status = TaxDownlaodExtract.EXTRACT_STATUS;
		return status.getRemarks();
	}

	/**
	 * Download the GstTransaction data from Interface datasource into browser as excel file format
	 */
	private String downloadGstTransactionData(String configName, TaxDownlaodExtract process) throws Exception {

		long count = process.getGstTrnansactionRecordCount();
		if (count <= 0) {
			return Labels.getLabel("label_DataExtraction_NoRecordsavailable.value");
		}

		List<TaxDownload> list = process.getGstTrnansactionDeatils();

		if (list != null && !list.isEmpty()) {
			downloadGstTrnDetails(list);
		} else {
			return Labels.getLabel("label_DataExtraction_NoRecordsavailable.value");
		}
		return Labels.getLabel("label_DataExtractionList_DownloadedSuccess.value");
	}

	/**
	 * Save Data from Pennant datasource into Interface datasource(Bajaj)
	 */
	private String saveGstSummaryData(String configName, TaxDownlaodExtract process) {

		process.clearTables();
		process.preparePosingsData();
		long count = process.setGstSummaryRecords();

		if (count <= 0) {
			return Labels.getLabel("label_DataExtraction_NoRecordsavailable.value");
		}
		process.processGstSummaryData();
		DataEngineStatus status = TaxDownlaodExtract.EXTRACT_STATUS;
		return status.getRemarks();
	}

	/**
	 * Download the data into browser as excel file format
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	private void downloadGstTrnDetails(List<TaxDownload> list) throws Exception {
		logger.debug(Literal.ENTERING);

		ByteArrayOutputStream bos = null;
		HSSFWorkbook workbook = null;
		Sheet sheet = null;
		try {
			workbook = new HSSFWorkbook();
			sheet = workbook.createSheet("GST Tranasction Details");

			CellStyle dateFormat = workbook.createCellStyle();
			CreationHelper createHelper = workbook.getCreationHelper();
			dateFormat.setDataFormat(createHelper.createDataFormat().getFormat(DateFormat.LONG_DATE.getPattern()));

			Map<Integer, Object[]> data = new TreeMap<Integer, Object[]>();
			data.put(1, getGstTransactionHeaderNames());
			int i = 2;
			for (TaxDownload item : list) {
				data.put(i, getGstTranasctionColumnValues(item));
				i++;
			}

			Set<Integer> keyset = data.keySet();
			int rownum = 0;
			for (int key : keyset) {
				Row row = sheet.createRow(rownum++);
				Object[] objArray = data.get(key);
				int cellnum = 0;
				for (Object obj : objArray) {
					Cell cell = row.createCell(cellnum++);
					if (obj instanceof String) {
						cell.setCellValue((String) obj);
					} else if (obj instanceof Integer) {
						cell.setCellValue((Integer) obj);
					} else if (obj instanceof Long) {
						cell.setCellValue((Long) obj);
					} else if (obj instanceof Date) {
						cell.setCellStyle(dateFormat);
						cell.setCellValue((Date) obj);
					} else if (obj instanceof BigDecimal) {
						BigDecimal val = (BigDecimal) obj;
						cell.setCellValue((Double) val.doubleValue());
					} else if (obj instanceof Boolean) {
						cell.setCellValue((Boolean) obj);
					}
				}
			}

			bos = new ByteArrayOutputStream();
			workbook.write(bos);
			Filedownload.save(new AMedia("GSTDetails", "xls", "application/vnd.ms-excel", bos.toByteArray()));
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		} finally {
			if (bos != null) {
				bos.close();
				bos.flush();
				bos = null;
			}
			if (workbook != null) {
				workbook = null;
			}
			sheet = null;
		}
		logger.debug(Literal.LEAVING);
	}

	private Object[] getGstTransactionHeaderNames() {
		Object[] columnNames = new Object[] { "TRANSACTION_DATE", "HOST_SYSTEM_TRANSACTION_ID", "TRANSACTION_TYPE",
				"BUSINESS_AREA", "SOURCE_SYSTEM", "COMPANY_CODE", "REGISTERED_CUSTOMER", "CUSTOMER_ID", "CUSTOMER_NAME",
				"CUSTOMER_GSTIN", "CUSTOMER_ADDRESS", "CUSTOMER_STATE_CODE", "ADDRESS_CHANGE_DATE", "PAN_NO",
				"LEDGER_CODE", "HSN_SAC_CODE", "NATURE_OF_SERVICE", "LOAN_ACCOUNT_NO", "PRODUCT_CODE", "CHARGE_CODE",
				"LOAN_BRANCH", "LOAN_BRANCH_STATE", "LOAN_SERVICING_BRANCH", "BFL_GSTIN_NO", "TXN_BRANCH_ADDRESS",
				"TXN_BRANCH_STATE_CODE", "TRANSACTION_AMOUNT", "REVERSE_CHARGE_APPLICABLE", "INVOICE_TYPE",
				"ORIGINAL_INVOICE_NO", "LOAN_BRANCH_ADDRESS", "TO_STATE", "FROM_STATE", "BUSINESSDATETIME",
				"PROCESSDATETIME", "PROCESSED_FLAG", "AGREEMENTID", "CONSIDER_FOR_GST", "EXEMPTED_STATE",
				"EXEMPTED_CUSTOMER" };
		return columnNames;
	}

	private Object[] getGstTranasctionColumnValues(TaxDownload item) {
		Object[] columnValues = new Object[] { item.getTransactionDate(), item.getHostSystemTransactionId(),
				item.getTransactionType(), item.getBusinessArea(), item.getSourceSystem(), item.getCompanyCode(),
				item.getRegisteredCustomer(), item.getCustomerId(), item.getCustomerName(), item.getCustomerGstin(),
				item.getCustomerAddress(), item.getCustomerStateCode(), item.getAddressChangeDate(), item.getPanNo(),
				item.getLedgerCode(), item.getHsnSacCode(), item.getNatureOfService(), item.getLoanAccountNo(),
				item.getProductCode(), item.getChargeCode(), item.getLoanBranch(), item.getLoanBranchState(),
				item.getLoanServicingBranch(), item.getBflGstinNo(), item.getTxnBranchAddress(),
				item.getTxnBranchStateCode(), item.getTransactionAmount(), item.getReverseChargeApplicable(),
				item.getInvoiceType(), item.getOriginalInvoiceNo(), item.getLoanBranchAddress(), item.getToState(),
				item.getFromState(), item.getBusinessDatetime(), item.getProcessDatetime(), item.getProcessedFlag(),
				item.getAgreementId(), item.getConsiderForGst(), item.getExemptedState(), item.getExemptedCustomer() };
		return columnValues;
	}

	/**
	 * Download the data from Interface datasource into browser as excel file format
	 * 
	 * @throws Exception
	 */
	private String downloadGstSummaryData(String configName, TaxDownlaodExtract process) throws Exception {

		long count = process.getGSTSummaryRecordCount();
		if (count <= 0) {
			return Labels.getLabel("label_DataExtraction_NoRecordsavailable.value");
		}

		List<TaxDownload> list = process.getGstSummaryDeatils();

		if (list != null && !list.isEmpty()) {
			downloadGstSummaryData(list);
		} else {
			return Labels.getLabel("label_DataExtraction_NoRecordsavailable.value");
		}
		return Labels.getLabel("label_DataExtractionList_DownloadedSuccess.value");
	}

	/**
	 * Download the data into browser as excel file format
	 * 
	 * @throws Exception
	 */
	private void downloadGstSummaryData(List<TaxDownload> list) throws Exception {
		logger.debug(Literal.ENTERING);

		ByteArrayOutputStream bos = null;
		HSSFWorkbook workbook = null;
		Sheet sheet = null;
		try {
			workbook = new HSSFWorkbook();
			sheet = workbook.createSheet("GST Summary Details");

			CellStyle dateFormat = workbook.createCellStyle();
			CreationHelper createHelper = workbook.getCreationHelper();
			dateFormat.setDataFormat(createHelper.createDataFormat().getFormat(DateFormat.LONG_DATE.getPattern()));

			Map<Integer, Object[]> data = new TreeMap<Integer, Object[]>();
			data.put(1, getGstSummaryHeaderNames());
			int i = 2;
			for (TaxDownload item : list) {
				data.put(i, getGstSummaryColumnValues(item));
				i++;
			}

			Set<Integer> keyset = data.keySet();
			int rownum = 0;
			for (int key : keyset) {
				Row row = sheet.createRow(rownum++);
				Object[] objArray = data.get(key);
				int cellnum = 0;
				for (Object obj : objArray) {
					Cell cell = row.createCell(cellnum++);
					if (obj instanceof String) {
						cell.setCellValue((String) obj);
					} else if (obj instanceof Integer) {
						cell.setCellValue((Integer) obj);
					} else if (obj instanceof Long) {
						cell.setCellValue((Long) obj);
					} else if (obj instanceof Date) {
						cell.setCellStyle(dateFormat);
						cell.setCellValue((Date) obj);
					} else if (obj instanceof BigDecimal) {
						BigDecimal val = (BigDecimal) obj;
						cell.setCellValue((Double) val.doubleValue());
					} else if (obj instanceof Boolean) {
						cell.setCellValue((Boolean) obj);
					}
				}
			}

			bos = new ByteArrayOutputStream();
			workbook.write(bos);
			Filedownload.save(new AMedia("GSTDetails", "xls", "application/vnd.ms-excel", bos.toByteArray()));
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		} finally {
			if (bos != null) {
				bos.close();
				bos.flush();
				bos = null;
			}
			if (workbook != null) {
				workbook = null;
			}
			sheet = null;
		}
		logger.debug(Literal.LEAVING);
	}

	private Object[] getGstSummaryHeaderNames() {
		Object[] columnNames = new Object[] { "TRANSACTION_DATE", "ENTITYNAME", "ENTITYGSTIN", "LEDGERCODE",
				"FINNONEBRANCHID", "REGISTEREDUNREGISTERED", "INTERINTRASTATE", "AMOUNT" };
		return columnNames;
	}

	private Object[] getGstSummaryColumnValues(TaxDownload item) {
		Object[] columnValues = new Object[] { item.getTransactionDate(), item.getEntityName(), item.getEntityGSTIN(),
				item.getLedgerCode(), item.getFinBranchId(), item.getRegisteredCustomer(), item.getInterIntraState(),
				item.getAmount() };
		return columnValues;
	}

}