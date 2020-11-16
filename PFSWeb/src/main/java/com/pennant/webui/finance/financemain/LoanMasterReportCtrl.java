package com.pennant.webui.finance.financemain;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.finance.LoanReport;
import com.pennant.backend.service.reports.LoanMasterReportService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.equation.util.DateUtility;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class LoanMasterReportCtrl extends GFCBaseCtrl<LoanReport> {
	private static final long serialVersionUID = 4678287540046204660L;
	private final static Logger logger = Logger.getLogger(LoanMasterReportCtrl.class);

	protected Window window_LoanMasterReport;
	protected Borderlayout borderLayout_loanMasterReport;
	protected ExtendedCombobox finReference;
	protected Tabbox tabbox;
	protected Button btnClear;
	private LoanMasterReportService loanMasterReportService;
	private int formater = CurrencyUtil.getFormat("");
	Date appDate = SysParamUtil.getAppDate();

	public LoanMasterReportCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "LoanMasterReport";
	}

	/**
	 * On creating Window
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_LoanMasterReport(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_LoanMasterReport);
		tabbox = (Tabbox) event.getTarget().getParent().getParent().getParent().getParent();
		doSetFieldProperties();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(false);
		tabbox.getSelectedTab().close();
	}

	/**
	 * When user Clicks on "Search"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnSearch(Event event) throws Exception {
		logger.debug(Literal.ENTERING + event.toString());
		List<LoanReport> loanReports = loanMasterReportService.getLoanReports(this.finReference.getValue());
		downloadLoanMasterReport(loanReports);
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * When user Clicks on "Search"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnClear(Event event) throws Exception {
		logger.debug(Literal.ENTERING + event.toString());
		this.finReference.setValue("");
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * This method is used to download the loanReports
	 * 
	 * @param loanReports
	 */
	public void downloadLoanMasterReport(List<LoanReport> loanReports) {
		try {
			byte[] data = processLoanMasterForXLDownload(loanReports);
			if (data != null) {
				Filedownload.save(data, "application/vnd.ms-excel", "LoanMasterReport");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method will prepare a xl for a given loanReports
	 * 
	 * @param LoanReport
	 * @return
	 */
	private byte[] processLoanMasterForXLDownload(List<LoanReport> loanReports) {
		FileInputStream file = null;
		Workbook workbook = null;
		Sheet sheet = null;
		String path = PathUtil.getPath(PathUtil.LoanReport) + "/LoanMasterReport.xlsx";
		try {
			//Reading the template
			file = new FileInputStream(new File(path));
			workbook = new XSSFWorkbook(file);
			//Getting the Loan Master Report 
			sheet = workbook.getSheetAt(0);
			if (sheet == null) {
				MessageUtil.showError(Labels.getLabel("label_ValidatedUploadFile"));
				return null;
			} else {
				//writing the data to existing template
				createLoanMasterReportSheet(sheet, loanReports);
				file.close();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				workbook.write(baos);
				return baos.toByteArray();
			}
		} catch (FileNotFoundException e) {
			MessageUtil.showError(Labels.getLabel("label_File_Not_Exists_Message").concat(path));
			return null;
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (workbook != null) {
					workbook.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Prepare Loan Report data
	 * 
	 * @param sheet
	 * @param loanReports
	 */
	private void createLoanMasterReportSheet(Sheet sheet, List<LoanReport> loanReports) {
		int rowCount = 0;
		List<String> values = null;
		if (CollectionUtils.isNotEmpty(loanReports)) {
			for (LoanReport loanReport : loanReports) {
				Row row = sheet.createRow(++rowCount);
				//preparing the list of values with format
				values = prepareLoanMasterValuesFromObject(loanReport, loanReports);
				writeData(row, values);
			}
		}
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		this.finReference.setProperties("LoanMasterReport", "FinReference", null, false, 20);
		this.finReference.setTextBoxWidth(130);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Prepare the loanReports list
	 * 
	 * @param loanReport
	 * @param loanReports
	 * @param list
	 */
	private List<String> prepareLoanMasterValuesFromObject(LoanReport loanReport, List<LoanReport> loanReports) {
		logger.debug(Literal.ENTERING);
		ArrayList<String> list = new ArrayList<>();
		//Entity
		list.add(loanReport.getEntity());
		//FinReference
		list.add(String.valueOf(loanReport.getFinReference()));
		//Customer Name
		list.add(String.valueOf(loanReport.getCustName()));
		//Customer CIF
		list.add(String.valueOf(loanReport.getCustCIF()));
		//Customer Category
		list.add(String.valueOf(loanReport.getCustCategory()));
		//Loan Product
		list.add(String.valueOf(loanReport.getFinType()));
		// Transaction Type
		list.add(StringUtils.trimToEmpty(loanReport.getProductDescription()));
		// Property Usage
		if (!"#".equals(loanReport.getPropertyUsage())) {
			list.add(StringUtils.trimToEmpty(loanReport.getPropertyUsage()));
		} else {
			list.add(" ");
		}
		//Scheme
		list.add(String.valueOf(" "));//Scheme
		//First Disb Date
		Date firstDisbDate = loanReport.getFirstDisbDate();
		if (firstDisbDate != null && DateUtility.formatDate(firstDisbDate, "dd-MM-yyyy") != null) {
			list.add(DateUtility.formatDate(firstDisbDate, "dd-MM-yyyy"));
		} else {
			list.add(" ");
		}
		//Last Disb Date
		Date lastDisbDate = loanReport.getLastDisbDate();
		if (lastDisbDate != null && DateUtility.formatDate(lastDisbDate, "dd-MM-yyyy") != null) {
			list.add(DateUtility.formatDate(lastDisbDate, "dd-MM-yyyy"));
		} else {
			list.add(" ");
		}
		//Original ROI
		BigDecimal originalROI = PennantApplicationUtil.unFormateAmount(loanReport.getOriginalROI(), formater);
		list.add(PennantApplicationUtil.amountFormate(originalROI, formater));
		//Revised ROI
		BigDecimal revisedROI = PennantApplicationUtil.unFormateAmount(loanReport.getRevisedROI(), formater);
		if (revisedROI != null && revisedROI.compareTo(BigDecimal.ZERO) > 0) {
			list.add(PennantApplicationUtil.amountFormate(revisedROI, formater));
		} else {
			list.add("0.00");
		}
		// Sanction Amount
		BigDecimal sanctionAmount = loanReport.getSanctioAmount();
		list.add(PennantApplicationUtil.amountFormate(sanctionAmount, formater).replace(",", ""));
		//sanction amount VAS
		BigDecimal sanctionAmountVAS = loanReport.getSanctionAmountVAS();
		list.add(PennantApplicationUtil.amountFormate(sanctionAmountVAS, formater).replace(",", ""));
		//Disbursed Amount
		BigDecimal disbAmt = loanReport.getDisbursementAmount();
		list.add(PennantApplicationUtil.amountFormate(disbAmt, formater).replace(",", ""));
		//UnDisbursed Amount
		BigDecimal unDisbAmt = loanReport.getUnDisbursedAmount();
		list.add(PennantApplicationUtil.amountFormate(unDisbAmt, formater).replace(",", ""));
		//OutStanding Amount loan & Adv
		BigDecimal outstandingLoanAdv = loanReport.getOutstandingAmt_Loan_Adv();
		outstandingLoanAdv = outstandingLoanAdv.setScale(formater, RoundingMode.valueOf(loanReport.getRoundingMode()));
		list.add(String.valueOf(outstandingLoanAdv));
		//OutStanding Amount LI & GI
		BigDecimal outstandingVAS = loanReport.getOustandingAmt_LI_GI();
		outstandingVAS = outstandingVAS.setScale(formater, RoundingMode.valueOf(loanReport.getRoundingMode()));
		list.add(String.valueOf(outstandingVAS));
		//Captalized intrest
		BigDecimal intrstCaptalized = loanReport.getCaptilizedIntrest();
		String interestCaptalized = PennantApplicationUtil.amountFormate(intrstCaptalized, formater).replace(",", "");
		list.add(String.valueOf(interestCaptalized));
		//Loan Debtors Principal
		BigDecimal loanPrncpl = loanReport.getLoanDebtors_Principal();
		String loanPrincipal = PennantApplicationUtil.amountFormate(loanPrncpl, formater).replace(",", "");
		if (loanPrincipal != null) {
			list.add(loanPrincipal);
		} else {
			list.add("0.00");
		}
		//AUM
		BigDecimal aum = BigDecimal.ZERO;
		aum = aum.add(outstandingLoanAdv).add(outstandingVAS).add(new BigDecimal(interestCaptalized))
				.add(new BigDecimal(loanPrincipal));
		aum = aum.setScale(formater, RoundingMode.valueOf(loanReport.getRoundingMode()));
		list.add(String.valueOf(aum));
		//Interest Accrual Amount
		BigDecimal intrstAccrual = loanReport.getIntrestAcrrualAmt();
		String interestAccrual = PennantApplicationUtil.amountFormate(intrstAccrual, formater).replace(",", "");
		if (interestAccrual != null) {
			list.add(interestAccrual);
		} else {
			list.add("0.00");
		}
		//Loan Debtors Interest
		BigDecimal loanPft = loanReport.getLoanDebtors_Interest();
		String loanProfit = PennantApplicationUtil.amountFormate(loanPft, formater).replace(",", "");
		if (loanProfit != null) {
			list.add(loanProfit);
		} else {
			list.add("0.00");
		}
		//Total Outstanding
		BigDecimal totOutstanding = BigDecimal.ZERO;
		totOutstanding = totOutstanding.add(outstandingLoanAdv).add(new BigDecimal(loanPrincipal))
				.add(new BigDecimal(interestAccrual)).add(new BigDecimal(loanProfit));
		list.add(String.valueOf(totOutstanding));
		//ECL provision
		list.add(" ");//ECL provision
		//Property Value
		BigDecimal prpValue = loanReport.getPropertyValue();
		if (prpValue != null) {
			list.add(String.valueOf(prpValue));
		} else {
			list.add(" ");
		}
		// Property ID
		if (loanReport.getPropertyID() != null) {
			list.add(String.valueOf(loanReport.getPropertyID()));
		} else {
			list.add(" ");
		}
		//Property Desc
		if (!"#".equals(loanReport.getPropertyDesc())) {
			list.add(StringUtils.trimToEmpty(loanReport.getPropertyDesc()));
		} else {
			list.add(" ");
		}

		//Weaker Section
		list.add(StringUtils.trimToEmpty(loanReport.getCaste()));
		//States
		list.add(StringUtils.trimToEmpty(loanReport.getBranchState()));

		//Disbursement Tag
		list.add(StringUtils.trimToEmpty(loanReport.getDisbTag()));

		//Tenure in years
		int years = 0;
		int reminder = 0;
		if (loanReport.getRevisedTenure() > 0) {
			years = loanReport.getRevisedTenure() / 12;
			reminder = (loanReport.getRevisedTenure() % 12);

		} else if (loanReport.getOriginalTenure() > 0) {
			years = loanReport.getOriginalTenure() / 12;
			reminder = loanReport.getOriginalTenure() % 12;
		}
		String result = String.valueOf(years);
		if (reminder > 0) {
			result += "." + reminder;
		}
		list.add(result);

		//LTV Ratio
		BigDecimal ltvRatio = BigDecimal.ZERO;
		String format = "0.00";
		try {
			if (totOutstanding != null && totOutstanding.compareTo(BigDecimal.ZERO) > 0 && prpValue != null
					&& prpValue.compareTo(BigDecimal.ZERO) > 0) {
				ltvRatio = totOutstanding.divide(prpValue, MathContext.DECIMAL128);
				ltvRatio = ltvRatio.multiply(new BigDecimal(100));
				DecimalFormat decimalFormat = new DecimalFormat("###.##");
				format = decimalFormat.format(ltvRatio);
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		list.add(String.valueOf(format));
		//Original Tenure
		list.add(String.valueOf(loanReport.getOriginalTenure()));
		//Revised Tenure
		if (loanReport.getRevisedTenure() != 0) {
			list.add(String.valueOf(loanReport.getRevisedTenure()));
		} else {
			list.add("0");
		}
		//NPA Status
		list.add(" ");//NPA Status
		//Loan Status
		list.add(loanReport.isLoanStatus() ? "Live" : "Closed");
		//Employee Loans
		list.add(loanReport.isEmployeeLoans() ? "Yes" : "No");
		list.add(String.valueOf(loanReport.getDpd()));
		logger.debug(Literal.LEAVING);
		return list;
	}

	/**
	 * Write Data into each cell
	 * 
	 * @param row
	 * @param cellValues
	 */
	private static void writeData(Row row, List<String> cellValues) {
		int columnCount = -1;
		for (String value : cellValues) {
			createCell(row, ++columnCount, value);
		}
	}

	/**
	 * Create a Cell for a Row
	 * 
	 * @param row
	 * @param columnCount
	 * @param field
	 */
	private static void createCell(Row row, int columnCount, String field) {
		Cell cell = row.createCell(columnCount);
		cell.setCellType(CellType.STRING);
		cell.setCellValue(field);
	}

	@Override
	public boolean isReadOnly(String componentName) {
		return getUserWorkspace().isReadOnly(componentName);
	}

	public LoanMasterReportService getLoanMasterReportService() {
		return loanMasterReportService;
	}

	public void setLoanMasterReportService(LoanMasterReportService loanMasterReportService) {
		this.loanMasterReportService = loanMasterReportService;
	}

}
