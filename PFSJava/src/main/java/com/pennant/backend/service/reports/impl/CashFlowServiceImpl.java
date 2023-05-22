package com.pennant.backend.service.reports.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.reports.CashFlowReportDAO;
import com.pennant.backend.model.finance.CashFlow;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.reports.CashFlowService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

public class CashFlowServiceImpl implements CashFlowService {
	private static final Logger logger = LogManager.getLogger(CashFlowServiceImpl.class);
	private CashFlowReportDAO cashFlowReportDAO;
	private static final String PATH = "CashFlowReport";
	private int formater = CurrencyUtil.getFormat("");

	public CashFlowServiceImpl() {
		super();
	}

	@Override
	public void processCashFlowDetails() {
		logger.debug(Literal.ENTERING);
		List<CashFlow> cashFlows = cashFlowReportDAO.getCashFlowDetails();
		// null check
		if (cashFlows != null) {
			processDetails(cashFlows);
		}
		logger.debug(Literal.LEAVING);
	}

	private void processDetails(List<CashFlow> cashFlows) {
		logger.debug(Literal.ENTERING);
		List<CashFlow> cashFlowList = new ArrayList<>();
		for (CashFlow cashFlow : cashFlows) {
			// process disbursement details
			processDisbursementDetails(cashFlow, cashFlowList);
			// process Schedule repayments details
			processScheduleDetails(cashFlow, cashFlowList);
			// process repayments
			processRepayHeaderDetails(cashFlow, cashFlowList);
		}
		createFile(cashFlowList);
		logger.debug(Literal.LEAVING);
	}

	private void processDisbursementDetails(CashFlow cashFlow, List<CashFlow> cashFlowList) {
		List<CashFlow> cashFlows = cashFlowReportDAO.getFinDisbDetails(cashFlow.getLan(), "");
		if (CollectionUtils.isNotEmpty(cashFlows)) {
			cashFlowList.addAll(cashFlows);
		}
	}

	private void processRepayHeaderDetails(CashFlow cashFlowIn, List<CashFlow> cashFlowList) {
		List<FinRepayHeader> finRepayHeaders = cashFlowReportDAO.getFinRepayHeader(cashFlowIn.getLan());
		BigDecimal forclosureAmt = BigDecimal.ZERO;
		if (CollectionUtils.isNotEmpty(finRepayHeaders)) {
			for (FinRepayHeader finRepayHeader : finRepayHeaders) {
				CashFlow cashFlow = new CashFlow();
				if (FinServiceEvent.FEEPAYMENT.equals(finRepayHeader.getFinEvent())) {
					cashFlow.setDate(finRepayHeader.getValueDate());
					cashFlow.setLan(finRepayHeader.getFinReference());
					cashFlow.setPfReceipt(finRepayHeader.getRepayAmount());
					cashFlow.setType("Unamortized Fee Received");
					cashFlowList.add(cashFlow);
				} else if (FinServiceEvent.SCHDRPY.equals(finRepayHeader.getFinEvent())
						|| FinServiceEvent.EARLYRPY.equals(finRepayHeader.getFinEvent())) {
					cashFlow.setDate(finRepayHeader.getValueDate());
					cashFlow.setLan(finRepayHeader.getFinReference());
					cashFlow.setPrePayment(finRepayHeader.getRepayAmount());
					cashFlow.setType("Repaid");
					cashFlowList.add(cashFlow);
				} else if (FinServiceEvent.EARLYSETTLE.equals(finRepayHeader.getFinEvent())) {
					forclosureAmt = forclosureAmt.add(finRepayHeader.getRepayAmount());
					if (finRepayHeader.getPriAmount().compareTo(BigDecimal.ZERO) >= 1
							|| finRepayHeader.getPftAmount().compareTo(BigDecimal.ZERO) >= 1) {
						cashFlow.setDate(finRepayHeader.getValueDate());
						cashFlow.setLan(finRepayHeader.getFinReference());
						cashFlow.setForClosure(forclosureAmt);
						cashFlow.setType("Foreclosure");
						cashFlowList.add(cashFlow);
					}
				}
			}
		}

	}

	private void processScheduleDetails(CashFlow cashFlowIn, List<CashFlow> cashFlowList) {
		// Schedule Details
		List<FinanceScheduleDetail> fsds = cashFlowReportDAO.getFinScheduleDetails(cashFlowIn.getLan(), "");
		List<FinODDetails> fods = cashFlowReportDAO.getFinODDetailsByFinRef(cashFlowIn.getLan(), "");
		BigDecimal schdPriPaid = BigDecimal.ZERO;
		BigDecimal schdPftPaid = BigDecimal.ZERO;
		BigDecimal schdPftPaidPreEmi = BigDecimal.ZERO;
		if (CollectionUtils.isNotEmpty(fsds)) {
			for (FinanceScheduleDetail fsd : fsds) {
				CashFlow cashFlow = new CashFlow();
				// Pre EMI
				if ((CalculationConstants.SCH_SPECIFIER_GRACE.equals(fsd.getSpecifier())
						|| CalculationConstants.SCH_SPECIFIER_GRACE_END.equals(fsd.getSpecifier())
								&& fsd.getPartialPaidAmt().compareTo(BigDecimal.ZERO) == 0)) {
					if (fsd.getProfitSchd().compareTo(BigDecimal.ZERO) >= 1) {
						cashFlow.setDate(fsd.getSchDate());
						cashFlow.setLan(fsd.getFinReference());
						cashFlow.setType("IntRecdNet");
						BigDecimal pftPaid = fsd.getProfitSchd().subtract(fsd.getSchdPftPaid());
						if (CollectionUtils.isNotEmpty(fods)) {
							for (FinODDetails fod : fods) {
								if (fsd.getSchDate().compareTo(fod.getFinODSchdDate()) == 0) {
									if (fsd.isSchPftPaid() || (pftPaid.compareTo(BigDecimal.ZERO) >= 1)) {
										schdPftPaidPreEmi = schdPftPaidPreEmi.add(pftPaid);
										schdPftPaid = schdPftPaid.add(pftPaid);
									} else {
										schdPftPaidPreEmi = BigDecimal.ZERO;
										schdPftPaid = BigDecimal.ZERO;
									}
									if (!fsd.isSchPftPaid()) {
										cashFlow.setInterestCollection(BigDecimal.ZERO);
									} else {
										cashFlow.setInterestCollection(schdPftPaidPreEmi);
									}
								}
							}
						} else {
							cashFlow.setInterestCollection(schdPftPaidPreEmi.add(fsd.getProfitSchd()));
							schdPftPaidPreEmi = BigDecimal.ZERO;
						}
						cashFlowList.add(cashFlow);
					}
					// EMI Collection
				} else if ((CalculationConstants.SCH_SPECIFIER_REPAY.equals(fsd.getSpecifier())
						|| CalculationConstants.SCH_SPECIFIER_MATURITY.equals(fsd.getSpecifier()))
						&& fsd.getPartialPaidAmt().compareTo(BigDecimal.ZERO) == 0) {
					cashFlow.setDate(fsd.getSchDate());
					cashFlow.setLan(fsd.getFinReference());
					cashFlow.setType("EMI collection");
					BigDecimal pftPaid = fsd.getProfitSchd().subtract(fsd.getSchdPftPaid());
					BigDecimal priPaid = fsd.getPrincipalSchd().subtract(fsd.getSchdPriPaid());
					if (CollectionUtils.isNotEmpty(fods)) {
						for (FinODDetails fod : fods) {
							if (fsd.getSchDate().compareTo(fod.getFinODSchdDate()) == 0) {
								if (fsd.isSchPftPaid() || (pftPaid.compareTo(BigDecimal.ZERO) >= 1
										|| priPaid.compareTo(BigDecimal.ZERO) >= 1)) {
									schdPftPaid = schdPftPaid.add(pftPaid);
									schdPriPaid = schdPriPaid.add(priPaid);
								} else {
									schdPftPaid = BigDecimal.ZERO;
									schdPriPaid = BigDecimal.ZERO;
								}
								if (!fsd.isSchPftPaid()) {
									cashFlow.setInterestCollection(BigDecimal.ZERO);
									cashFlow.setPrincipalCollection(BigDecimal.ZERO);
								} else {
									cashFlow.setInterestCollection(schdPftPaid);
									cashFlow.setPrincipalCollection(schdPriPaid);
								}
							}
						}
					} else {
						cashFlow.setInterestCollection(schdPftPaid.add(fsd.getProfitSchd()));
						cashFlow.setPrincipalCollection(schdPriPaid.add(fsd.getPrincipalSchd()));
						schdPftPaid = BigDecimal.ZERO;
						schdPriPaid = BigDecimal.ZERO;
					}
					cashFlowList.add(cashFlow);
				}
			}
		}
	}

	private void createFile(List<CashFlow> cashFlows) {
		logger.debug(Literal.ENTERING);
		if (cashFlows != null) {
			prepareCashFlowReportSheet(cashFlows);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method will prepare a xl for a given CashFlow
	 * 
	 * @param CashFlow
	 * @return
	 */
	private void prepareCashFlowReportSheet(List<CashFlow> list) {
		logger.debug(Literal.ENTERING);
		FileInputStream fileInputStream = null;
		Workbook workbook = null;
		Sheet sheet = null;
		String path = App.getResourcePath(PATH) + "/CashFlowReport.xlsx";
		try {
			// Reading the template
			File file = new File(path);
			fileInputStream = new FileInputStream(file);
			workbook = new XSSFWorkbook(fileInputStream);
			// Getting the CashFlowReportSheet sheet at index 0
			sheet = workbook.getSheetAt(0);
			if (sheet == null) {
				logger.error("File not exist");
			} else {
				// writing the data to existing template
				createCashFlowReportSheet(sheet, list);
				fileInputStream.close();
				String appDate = SysParamUtil.getAppDate("dd.MM.yyyy");
				String DATE_FORMAT = "hh.mm.ss";
				SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
				String time = sdf.format(Calendar.getInstance().getTime());
				String newPath = App.getResourcePath(PATH) + "//" + appDate + "//" + "CashFlowReport_" + appDate + "_"
						+ time + ".xlsx";
				File outFile = new File(newPath);
				if (!outFile.exists()) {
					outFile.getParentFile().mkdirs(); // Will create parent directories if not exists
					outFile.createNewFile();
				}
				FileOutputStream fileOutputStream = new FileOutputStream(outFile);
				workbook.write(fileOutputStream);
				fileOutputStream.close();
			}
		} catch (FileNotFoundException e) {
			logger.error("File not exist");
		} catch (IOException e1) {
			logger.error(Literal.EXCEPTION, e1);
		} catch (Exception e1) {
			logger.error(Literal.EXCEPTION, e1);
		} finally {
			try {
				if (workbook != null) {
					workbook.close();
				}
			} catch (IOException e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Prepare CashFlow data
	 * 
	 * @param sheet
	 * @param cashFlows
	 */
	private void createCashFlowReportSheet(Sheet sheet, List<CashFlow> cashFlows) {
		int rowCount = 0;
		List<String> values = null;
		if (CollectionUtils.isNotEmpty(cashFlows)) {
			for (int i = 0; i < cashFlows.size(); i++) {
				Row row = sheet.createRow(++rowCount);
				List<CashFlow> cashFlowCompareByDate = cashFlowCompareByDate(cashFlows);
				if (cashFlowCompareByDate != null && !cashFlowCompareByDate.isEmpty()) {
					Comparator<CashFlow> comp = new BeanComparator<CashFlow>("lan");
					Collections.sort(cashFlowCompareByDate, comp);
				}
				// preparing the list of values with format
				values = prepareCashFlowValuesFromObject(cashFlowCompareByDate.get(i));

				writeData(row, values);
			}

		}
	}

	private List<String> prepareCashFlowValuesFromObject(CashFlow cashFlow) {
		ArrayList<String> list = new ArrayList<>();
		Date date = cashFlow.getDate();
		if (date != null && DateUtil.format(date, "dd-MM-yyyy") != null) {
			list.add(DateUtil.format(date, "dd-MM-yyyy"));
		} else {
			list.add(" ");
		}
		list.add(cashFlow.getLan());
		BigDecimal disbAmt = cashFlow.getDisb();
		list.add(PennantApplicationUtil.amountFormate(disbAmt, formater).replace(",", ""));
		BigDecimal subAmt = cashFlow.getSubventionAmount();
		list.add(PennantApplicationUtil.amountFormate(subAmt, formater).replace(",", ""));
		BigDecimal pfReceipt = cashFlow.getPfReceipt();
		list.add(PennantApplicationUtil.amountFormate(pfReceipt, formater).replace(",", ""));
		BigDecimal prncplCollection = cashFlow.getPrincipalCollection();
		list.add(PennantApplicationUtil.amountFormate(prncplCollection, formater).replace(",", ""));
		BigDecimal intrstCollection = cashFlow.getInterestCollection();
		list.add(PennantApplicationUtil.amountFormate(intrstCollection, formater).replace(",", ""));
		BigDecimal prePayment = cashFlow.getPrePayment();
		list.add(PennantApplicationUtil.amountFormate(prePayment, formater).replace(",", ""));
		BigDecimal foreClosure = cashFlow.getForClosure();
		list.add(PennantApplicationUtil.amountFormate(foreClosure, formater).replace(",", ""));
		list.add(String.valueOf(cashFlow.getType()));

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
		cell.setCellValue(field);
	}

	private List<CashFlow> cashFlowCompareByDate(List<CashFlow> cashFlows) {
		if (cashFlows != null && cashFlows.size() > 0) {
			Collections.sort(cashFlows, new Comparator<CashFlow>() {
				@Override
				public int compare(CashFlow detail1, CashFlow detail2) {
					return com.pennanttech.pennapps.core.util.DateUtil.compare(detail1.getDate(), detail2.getDate());
				}
			});
		}
		return cashFlows;
	}

	public CashFlowReportDAO getCashFlowReportDAO() {
		return cashFlowReportDAO;
	}

	public void setCashFlowReportDAO(CashFlowReportDAO cashFlowReportDAO) {
		this.cashFlowReportDAO = cashFlowReportDAO;
	}
}
