package com.pennanttech.pff.external.cibil;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.FrequencyCodeTypes;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.amtmasters.VehicleDealerDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rmtmasters.PromotionDAO;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.service.cibil.CIBILService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.dataengine.Event;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.dataengine.util.DataEngineUtil;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.eod.step.StepUtil;
import com.pennanttech.pff.model.cibil.CibilFileInfo;
import com.pennanttech.pff.model.cibil.CibilMemberDetail;
import com.pennanttech.pff.overdraft.dao.OverdraftScheduleDetailDAO;

public class RetailCibilReport extends BasicDao<Object> {
	private static final Logger logger = LoggerFactory.getLogger(RetailCibilReport.class);
	public static DataEngineStatus EXTRACT_STATUS = StepUtil.CIBIL_EXTRACT_RETAIL;

	private static final String MEMBER_FIELDS = "Reporting Member ID, Short Name, Cycle Identification, DateReported, Reporting Password, Authentication Method,	Future Use,	Member Data";
	private static final String HEADER_FIELDS = "Consumer Name,Date Of Birth,Gender,Income Tax ID Number,Passport Number,Passport Issue Date,Passport Expiry Date,Voter ID Number,Driving License Number,Driving License Issue Date,Driving License Expiry Date,Ration Card Number,Universal ID Number,Additional ID #1,Additional ID #2,Telephone No.Mobile,Telephone No.Residence,Telephone No.Office,Extension Office,Telephone No.Other ,Extension Other,Email ID 1,Email ID 2,Address Line1,State Code1,PIN Code,Address Category 1,Residence Code 1,Address 2,State Code 2,PIN Code2,Address Category 2,Residence Code 2,Current/New Member Code,Current/New Member Short Name,Current New Account Number,Account Type,Ownership Indicator,Date Opened/Disbursed,Date Of Last Payment,Date Closed,Date Reported,High Credit/Sanctioned Amount,Current Balance,Amount Overdue,Number Of Days Past Due,Old Member Code,Old Member Short Name,Old Account Number,Old Acc Type,Old Ownership Indicator,Suit Filed / Wilful Default,Written-off and Settled Status,Asset Classification,Value of Collateral,Type of Collateral,Credit Limit,Cash Limit,Rate of Interest,RepaymentTenure,EMI Amount,Writtenoff Amount Total ,Writtenoff Principal Amount,Settlement Amt,Payment Frequency,Actual Payment Amt,Occupation Code,Income,Net/Gross Income Indicator,Monthly/AnnualIncome Indicator";

	private static final String DATE_FORMAT = "ddMMyyyy";
	private CibilFileInfo fileInfo;
	private CibilMemberDetail memberDetails;

	private long headerId;
	private long totalRecords;
	private long processedRecords;
	private long successCount;
	private long failedCount;
	private int xlsxRowCount = 3;
	public static boolean executing;

	private CIBILService cibilService;

	private FinanceMainDAO financeMainDAO;
	private OverdraftScheduleDetailDAO overdraftScheduleDetailDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceDisbursementDAO financeDisbursementDAO;
	private RepayInstructionDAO repayInstructionDAO;
	private FinODPenaltyRateDAO finODPenaltyRateDAO;
	private FinanceTypeDAO financeTypeDAO;
	private PromotionDAO promotionDAO;
	private FinanceSuspHeadDAO financeSuspHeadDAO;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private VehicleDealerDAO vehicleDealerDAO;

	public RetailCibilReport() {
		super();
	}

	public void generateReport() throws Exception {
		logger.debug(Literal.ENTERING);

		extractCibilData("");

		logger.debug(Literal.LEAVING);
	}

	public void generateReportBasedOnEntity() throws Exception {
		logger.debug(Literal.ENTERING);

		List<String> entities = cibilService.getEntities();
		if (CollectionUtils.isEmpty(entities)) {
			return;
		}

		for (String entity : entities) {
			extractCibilData(entity);
		}
		logger.debug(Literal.LEAVING);
	}

	private void extractCibilData(String entity) throws Exception {
		logger.debug(Literal.ENTERING);
		initlize();

		if (memberDetails == null) {
			logger.info("Member Details are not configured for the retail customers");
			return;
		}

		File cibilFile = null;
		if ("TUFF".equals(memberDetails.getFileFormate())) {
			cibilFile = createFile();
		} else {
			cibilFile = createXlsFile();
		}

		try {
			fileInfo = new CibilFileInfo();
			fileInfo.setCibilMemberDetail(memberDetails);
			fileInfo.setTotalRecords(totalRecords);
			fileInfo.setProcessedRecords(processedRecords);
			fileInfo.setSuccessCount(successCount);
			fileInfo.setFailedCount(failedCount);
			fileInfo.setRemarks(updateRemarks());
			fileInfo.setFileName(cibilFile.getName());
			cibilService.logFileInfo(fileInfo);

			headerId = fileInfo.getId();
			/* Clear CIBIL_CUSTOMER_EXTRACT table */
			cibilService.deleteDetails();
			/* Prepare the data and store in CIBIL_CUSTOMER_EXTRACT table */
			totalRecords = cibilService.extractCustomers(PennantConstants.PFF_CUSTCTG_INDIV, entity);
			EXTRACT_STATUS.setTotalRecords(totalRecords);

			if ("TUFF".equals(memberDetails.getFileFormate())) {
				tuffFormat(cibilFile, entity);
			} else {
				xlsxFormat(cibilFile, entity);
			}

			if ("F".equals(EXTRACT_STATUS.getStatus())) {
				return;
			}

			updateCiblilData(cibilFile);

		} catch (Exception e) {
			EXTRACT_STATUS.setStatus("F");
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void updateCiblilData(File cibilFile) {
		// Move the File into S3 bucket
		try {
			EventProperties properties = cibilService.getEventProperties("CIBIL_REPORT");
			if (properties != null) {
				if ("MOVE_TO_S3_BUCKET".equalsIgnoreCase(properties.getStorageType())) {
					DataEngineUtil.postEvents(Event.MOVE_TO_S3_BUCKET.name(), properties, cibilFile);
				} else if ("COPY_TO_SFTP".equalsIgnoreCase(properties.getStorageType())) {
					DataEngineUtil.postEvents(Event.COPY_TO_SFTP.name(), properties, cibilFile);
				} else if ("COPY_TO_FTP".equalsIgnoreCase(properties.getStorageType())) {
					DataEngineUtil.postEvents(Event.COPY_TO_FTP.name(), properties, cibilFile);
				}
			}
			EXTRACT_STATUS.setStatus("S");
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			EXTRACT_STATUS.setRemarks(e.getMessage());
			EXTRACT_STATUS.setStatus("F");
		}
		String remarks = updateRemarks();
		fileInfo.setRemarks(remarks);
		fileInfo.setRemarks(remarks);
		fileInfo.setStatus(EXTRACT_STATUS.getStatus());
		fileInfo.setTotalRecords(totalRecords);
		fileInfo.setProcessedRecords(processedRecords);
		fileInfo.setFailedCount(failedCount);
		fileInfo.setSuccessCount(successCount);
		fileInfo.setRemarks(remarks);
		cibilService.updateFileStatus(fileInfo);

	}

	private void tuffFormat(File cibilFile, String entity) throws IOException {
		try (final BufferedWriter writer = new BufferedWriter(new FileWriter(cibilFile))) {
			new HeaderSegment(writer).write();
			MapSqlParameterSource parameterSource = new MapSqlParameterSource();

			String sql = getSqlQuery(entity, parameterSource);

			jdbcTemplate.query(sql, parameterSource, new RowCallbackHandler() {
				@Override
				public void processRow(ResultSet rs) throws SQLException {
					processedRecords++;
					EXTRACT_STATUS.setProcessedRecords(processedRecords);
					long finID = rs.getLong("FinID");
					long customerId = rs.getLong("CUSTID");

					try {
						CustomerDetails customer = cibilService.getCustomerDetails(customerId, finID,
								PennantConstants.PFF_CUSTCTG_INDIV);

						if (customer == null || customer.getCustomerFinance() == null) {
							failedCount++;
							cibilService.logFileInfoException(headerId, finID, String.valueOf(customerId),
									"Unable to fetch the details.");
							return;
						}

						StringBuilder builder = new StringBuilder();
						new NameSegment(builder, customer.getCustomer()).write();
						new IdentificationSegment(builder, customer.getCustomerDocumentsList()).write();
						new TelephoneSegment(builder, customer.getCustomerPhoneNumList()).write();
						new EmailContactSegment(builder, customer.getCustomerEMailList()).write();
						new AddressSegment(builder, customer.getAddressList()).write();
						new AccountSegment(builder, customer.getCustomerFinance()).write();
						List<FinanceEnquiry> list = new ArrayList<>();
						list.add(customer.getCustomerFinance());
						new AccountSegmentHistory(builder, list).write();
						new EndofSubjectSegment(builder).write();

						writer.write(builder.toString());

						EXTRACT_STATUS.setSuccessRecords(successCount++);
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						EXTRACT_STATUS.setFailedRecords(failedCount++);
						cibilService.logFileInfoException(headerId, finID, String.valueOf(customerId), e.getMessage());

					}
				}
			});
			new TrailerSegment(writer).write();
		}
	}

	private void xlsxFormat(File cibilFile, String entity) throws IOException {
		try (FileOutputStream outputStream = new FileOutputStream(cibilFile)) {
			try (BufferedOutputStream bos = new BufferedOutputStream(outputStream)) {
				try (Workbook workbook = new XSSFWorkbook()) {
					int rowIndex = 0;

					Sheet sheet = workbook.createSheet();

					createHeading(sheet, rowIndex++);

					createFields(workbook, sheet, rowIndex++, MEMBER_FIELDS);

					createFields(sheet, rowIndex++, getMemberDetails());

					createFields(sheet, rowIndex++, HEADER_FIELDS);

					MapSqlParameterSource parameterSource = new MapSqlParameterSource();
					String sql = getSqlQuery(entity, parameterSource);

					jdbcTemplate.query(sql, parameterSource, new RowCallbackHandler() {
						@Override
						public void processRow(ResultSet rs) throws SQLException {
							EXTRACT_STATUS.setProcessedRecords(processedRecords++);
							xlsxRowCount++;
							long finID = rs.getLong("FinID");
							long customerId = rs.getLong("CUSTID");
							try {
								CustomerDetails customer = cibilService.getCustomerDetails(customerId, finID,
										PennantConstants.PFF_CUSTCTG_INDIV);
								if (customer == null) {
									failedCount++;
									cibilService.logFileInfoException(headerId, finID, String.valueOf(customerId),
											"Unable to fetch the details.");
									return;
								}
								appendRow(customer, xlsxRowCount, sheet);
								EXTRACT_STATUS.setSuccessRecords(successCount++);
							} catch (Exception e) {
								EXTRACT_STATUS.setFailedRecords(failedCount++);
								cibilService.logFileInfoException(headerId, finID, String.valueOf(customerId),
										e.getMessage());
								logger.error(Literal.EXCEPTION, e);
							}
						}
					});
					workbook.write(bos);
				}
			}
		}
	}

	private String getSqlQuery(String entity, MapSqlParameterSource parameterSource) {
		parameterSource.addValue("segment_type", PennantConstants.PFF_CUSTCTG_INDIV);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT CUSTID, FINID, FINREFERENCE, OWNERSHIP From CIBIL_CUSTOMER_EXTRACT");
		sql.append(" where segment_type = :segment_type");

		if (ImplementationConstants.CIBIL_BASED_ON_ENTITY) {
			sql.append(" and entity = :entity");
			parameterSource.addValue("entity", entity);
		}
		return sql.toString();
	}

	private void appendRow(CustomerDetails customerDetal, int rowIndex, Sheet sheet) {
		Customer customer = customerDetal.getCustomer();

		if (customer == null) {
			return;
		}

		List<CustomerDocument> documents = customerDetal.getCustomerDocumentsList();
		List<CustomerAddres> addressList = customerDetal.getAddressList();
		List<CustomerPhoneNumber> customerPhoneNumbers = customerDetal.getCustomerPhoneNumList();

		Row row = sheet.createRow(rowIndex);
		Cell cell = row.createCell(0);
		cell.setCellValue(getCustomerFullName(customer));

		cell = row.createCell(1);

		cell.setCellValue(getDateOfBirth(customer.getCustDOB()));
		cell = row.createCell(2);

		cell.setCellValue(getCustGenCode(customer));

		cell = row.createCell(3);
		cell.setCellValue(customer.getCustCRCPR());

		String docCategory;
		String docTitle;
		for (CustomerDocument document : documents) {
			docCategory = document.getCustDocCategory();
			docTitle = document.getCustDocTitle();

			/* MasterDefUtil.getDocCode(DocType.PASSPORT) */
			if (StringUtils.equals(docCategory, "02")) {
				cell = row.createCell(4);
				cell.setCellValue(docTitle);

				cell = row.createCell(5);
				cell.setCellValue(DateUtil.format(document.getCustDocIssuedOn(), DATE_FORMAT));

				cell = row.createCell(6);
				cell.setCellValue(DateUtil.format(document.getCustDocExpDate(), DATE_FORMAT));
			}
			/* MasterDefUtil.getDocCode(DocType.VOTER_ID) */
			if (StringUtils.equals(docCategory, "03")) {
				cell = row.createCell(7);
				cell.setCellValue(docTitle);
			}
			/* MasterDefUtil.getDocCode(DocType.DRIVING_LICENCE) */
			if (StringUtils.equals(docCategory, "04")) {
				cell = row.createCell(8);
				cell.setCellValue(docTitle);

				cell = row.createCell(9);
				cell.setCellValue(DateUtil.format(document.getCustDocIssuedOn(), DATE_FORMAT));

				cell = row.createCell(10);
				cell.setCellValue(DateUtil.format(document.getCustDocExpDate(), DATE_FORMAT));
			}
			/* MasterDefUtil.getDocCode(DocType.RATION_CARD) */
			if (StringUtils.equals(docCategory, "05")) {
				cell = row.createCell(11);
				cell.setCellValue(docTitle);
			}
			// MasterDefUtil.getDocCode(DocType.AADHAAR))
			if (StringUtils.equals(docCategory, "06")) {
				cell = row.createCell(12);
				cell.setCellValue(docTitle);
			}
		}

		/* Additional ID #1 */
		cell = row.createCell(13);

		/* Additional ID #2 */
		cell = row.createCell(14);

		for (CustomerPhoneNumber phoneNum : customerPhoneNumbers) {
			String phoneNumber = phoneNum.getPhoneNumber();
			/* Telephone No.Mobile */
			if ("01".equals(phoneNum.getPhoneTypeCode())) {
				cell = row.createCell(15);
				cell.setCellValue(phoneNumber);
			}
			/* Telephone No.Residence */
			if ("02".equals(phoneNum.getPhoneTypeCode())) {
				cell = row.createCell(16);
				cell.setCellValue(phoneNumber);
			}
			/* Telephone No.Office */
			if ("03".equals(phoneNum.getPhoneTypeCode())) {
				cell = row.createCell(17);
				cell.setCellValue(phoneNumber);
			}
		}

		/* Extension Office */
		cell = row.createCell(18);

		/* Telephone No.Other */
		cell = row.createCell(19);

		/* Extension Other */
		cell = row.createCell(20);

		if (customerDetal.getCustomerEMailList() != null) {
			int count = 0;
			List<CustomerEMail> customerEMailListList = customerDetal.getCustomerEMailList();
			for (CustomerEMail customerEMail : customerEMailListList) {
				count++;
				if (count == 1) {
					cell = row.createCell(21);
					cell.setCellValue(customerEMail.getCustEMail());
				}
				if (count == 2) {
					cell = row.createCell(22);
					cell.setCellValue(customerEMail.getCustEMail());
					break;
				}
			}
		}

		int count = 0;
		for (CustomerAddres custAddr : addressList) {
			count++;
			if (count == 1) {
				/* Address1 */
				cell = row.createCell(23);
				cell.setCellValue(writeCustAddress(custAddr));

				/* State Code1 */
				cell = row.createCell(24);
				cell.setCellValue(custAddr.getCustAddrProvince());

				/* PIN Code1 */
				cell = row.createCell(25);
				cell.setCellValue(custAddr.getCustAddrZIP());

				/* Address Category1 */
				cell = row.createCell(26);
				cell.setCellValue(getAddrTypeCode(custAddr.getCustAddrType()));

				/* ResidenceCode1 */
				row.createCell(27);
			}
			if (count == 2) {
				/* Address2 */
				cell = row.createCell(28);
				cell.setCellValue(writeCustAddress(custAddr));

				/* State Code2 */
				cell = row.createCell(29);
				cell.setCellValue(custAddr.getCustAddrProvince());

				/* PIN Code2 */
				cell = row.createCell(30);
				cell.setCellValue(custAddr.getCustAddrZIP());

				/* Address Category2 */
				cell = row.createCell(31);
				cell.setCellValue(getAddrTypeCode(custAddr.getCustAddrType()));

				/* ResidenceCode2 */
				row.createCell(32);
				break;
			}
		}

		/* Current New Member Code */
		cell = row.createCell(33);
		String memberId = memberDetails.getMemberId();

		int lastIndexOf = memberId.lastIndexOf("_");

		if (lastIndexOf <= 0) {
			lastIndexOf = memberId.length();
		}

		cell.setCellValue(memberId.substring(0, lastIndexOf));

		/* Current New Member Name */
		cell = row.createCell(34);
		cell.setCellValue(memberDetails.getMemberShortName());

		/* Ownership Indicator */
		cell = row.createCell(37);

		/* Old Member Code */
		cell = row.createCell(46);

		/* Old Member ShortName */
		cell = row.createCell(48);

		/* Old AccType */
		cell = row.createCell(49);

		/* Old Ownership Indicator */
		cell = row.createCell(50);

		/* Suit Filed Willful Default */
		cell = row.createCell(51);

		/* Written-off and Settled Status */
		cell = row.createCell(52);

		FinanceEnquiry finance = null;
		if (customerDetal.getCustomerFinance() != null) {
			finance = customerDetal.getCustomerFinance();

			/* Current New Account No */
			cell = row.createCell(35);
			cell.setCellValue(finance.getFinReference());

			/* Account Type */
			cell = row.createCell(36);
			cell.setCellValue(finance.getFinType().trim());

			/* DateOpened/Disbursed */
			cell = row.createCell(38);
			cell.setCellValue(DateUtil.format(finance.getFinStartDate(), DATE_FORMAT));

			/* Ownership Indicator */
			cell = row.createCell(37);
			cell.setCellValue(finance.getOwnership());

			/* Date Of LastPayment */
			cell = row.createCell(39);
			cell.setCellValue(DateUtil.format(finance.getLatestRpyDate(), DATE_FORMAT));

			BigDecimal currBal = getCurrentBalance(finance);
			/* Date Closed */
			cell = row.createCell(40);

			if (getCurrentBalance(finance).compareTo(BigDecimal.ZERO) == 0 && !finance.isFinIsActive()) {
				if (DateUtil.compare(finance.getLatestRpyDate(), finance.getMaturityDate()) > 0) {
					cell.setCellValue(DateUtil.format(finance.getLatestRpyDate(), DATE_FORMAT));
				} else {
					cell.setCellValue(DateUtil.format(finance.getMaturityDate(), DATE_FORMAT));
				}
			}

			/* Date Reported */
			Date PrvMonthEnd = DateUtil.addMonths(DateUtil.getMonthEnd(SysParamUtil.getAppDate()), -1);
			cell = row.createCell(41);

			cell.setCellValue(DateUtil.format(PrvMonthEnd, DATE_FORMAT));

			/* High Credit/Sanctioned Amount */
			cell = row.createCell(42);
			String val = PennantApplicationUtil.amountFormate(finance.getFinAssetValue(), 2);
			val = val.replace(",", "");
			val = val.substring(0, val.indexOf("."));
			cell.setCellValue(val);

			/* Current Balance */
			cell = row.createCell(43);
			String curBal = PennantApplicationUtil.amountFormate(currBal, 2);
			curBal = curBal.replace(",", "");
			curBal = curBal.substring(0, curBal.indexOf("."));
			cell.setCellValue(curBal);

			/* Amount Overdue */
			cell = row.createCell(44);
			cell.setCellValue(String.valueOf(getAmountOverDue(finance)));

			/* Number Of Days PastDue */
			cell = row.createCell(45);
			cell.setCellValue(getOdDays(finance.getCurODDays()));

			/* Rate of Interest */
			cell = row.createCell(58);
			cell.setCellValue(String.valueOf(finance.getRepayProfitRate()));

			/* Repayment Tenure */
			cell = row.createCell(59);
			cell.setCellValue(finance.getNumberOfTerms());

			/* EMI Amount */
			cell = row.createCell(60);
			BigDecimal emi = PennantApplicationUtil.formateAmount(getEmiAmount(finance.getFinID()), 2).setScale(0,
					RoundingMode.HALF_DOWN);
			cell.setCellValue(String.valueOf(emi));

			/* Written-off and Settled Status */
			String closingstatus = StringUtils.trimToEmpty(finance.getClosingStatus());

			/* Account Status */
			if ("W".equals(closingstatus)) {
				cell = row.createCell(51);
				cell.setCellValue("02");

				/* Written off AmountTotal */
				cell = row.createCell(61);
				BigDecimal writnAmount = writtenOffAmount(finance);
				if (writnAmount.compareTo(BigDecimal.ZERO) > 0) {
					String wa = PennantApplicationUtil.amountFormate(writnAmount, 2);
					wa = wa.replace(",", "");
					wa = wa.substring(0, wa.indexOf(".") - 1);
					cell.setCellValue(wa);
				} else {
					cell.setCellValue(String.valueOf(writnAmount));
				}

				/* Written off Principal Amount */
				cell = row.createCell(62);
				BigDecimal writnPrincpl = writtenOffPrincipal(finance);
				if (writnPrincpl.compareTo(BigDecimal.ZERO) > 0) {
					String wp = PennantApplicationUtil.amountFormate(writnPrincpl, 2);
					wp = wp.replace(",", "");
					wp = wp.substring(0, wp.indexOf(".") - 1);
					cell.setCellValue(wp);
				} else {
					cell.setCellValue(String.valueOf(writnPrincpl));
				}
			}

			/* Income */
			cell = row.createCell(67);
			BigDecimal incomeAmt = writeCustomerIncomeData(customerDetal);
			if (incomeAmt.compareTo(BigDecimal.ZERO) > 0) {
				String income = PennantApplicationUtil.amountFormate(incomeAmt, 2);
				income = income.replace(",", "");
				income = income.substring(0, income.indexOf("."));
				cell.setCellValue(income);
			}
			/* Monthly/Annual Income Indicator */
			cell = row.createCell(68);
			if (incomeAmt.compareTo(BigDecimal.ZERO) > 0) {
				cell.setCellValue("G");
			}

			/*
			 * Indicates whether the amount specified in the Income field is Annual or Monthly. Valid indicators are: M
			 * = Monthly A = Annual
			 */
			/* Monthly/Annual Income Indicator */
			cell = row.createCell(69);
			if (incomeAmt.compareTo(BigDecimal.ZERO) > 0) {
				cell.setCellValue("A");
			}

			/* Asset Classification */
			int pastDues = 0;
			Date odStartDate = null;
			if (finance.getFinOdDetails() != null) {
				for (FinODDetails od : finance.getFinOdDetails()) {
					if (od.getFinCurODAmt() != null && od.getFinCurODAmt().compareTo(BigDecimal.ZERO) > 0) {

						if (pastDues < od.getFinCurODDays()) {
							pastDues = od.getFinCurODDays();
						}

						if (odStartDate == null && od.getTotPenaltyBal().compareTo(BigDecimal.ZERO) > 0) {
							odStartDate = od.getFinODSchdDate();
							continue;
						}

						if (DateUtil.compare(od.getFinODSchdDate(), odStartDate) < 0
								&& od.getTotPenaltyBal().compareTo(BigDecimal.ZERO) > 0) {
							odStartDate = od.getFinODSchdDate();
						}
					}
				}
			}

			String code = "";

			if (pastDues == 0) {
				code = "01"; // Standard
			} else {
				code = "  ";
			}
			row.createCell(53);
			// cell.setCellValue(code);
		}

		/* Value of Collateral */
		BigDecimal collateralValue = getCollateralValue(customerDetal);
		String collValue = PennantApplicationUtil.amountFormate(collateralValue, 2);
		collValue = collValue.replace(",", "");
		collValue = collValue.substring(0, collValue.indexOf("."));
		cell = row.createCell(54);
		cell.setCellValue(collValue);

		/* Type of Collateral */
		cell = row.createCell(55);
		cell.setCellValue(writeCollateralType(customerDetal));

		/* Payment Frequency */
		cell = row.createCell(64);
		cell.setCellValue(writePaymentFrq(finance.getRepayFrq()));

		/* Credit Limit */
		row.createCell(56);

		/* Cash Limit */
		row.createCell(57);

		/* Settlement Amt */
		row.createCell(63);

		/* Actual Payment Amt */
		BigDecimal instalmentPaid = finance.getInstalmentPaid();
		String actualPaymentAmt = PennantApplicationUtil.amountFormate(instalmentPaid, 2);
		actualPaymentAmt = actualPaymentAmt.replace(",", "");
		actualPaymentAmt = actualPaymentAmt.substring(0, actualPaymentAmt.indexOf("."));
		/* Actual Payment Amt */
		cell = row.createCell(65);
		cell.setCellValue(actualPaymentAmt);

		/* Occupation Code */
		row.createCell(66);

		/* Net/Gross Income Indicator */
		cell = row.createCell(66);
		cell.setCellValue(writeOccupationCode(customer.getSubCategory()));

		logger.info(Literal.LEAVING);
	}

	/* Valid codes are: 01 = Salaried 02 = Self Employed Professional 03 = Self Employed 04 = Others */
	private String writeOccupationCode(String subCategory) {
		if (StringUtils.equals(PennantConstants.EMPLOYMENTTYPE_SALARIED, subCategory)) {
			subCategory = "01";
			return subCategory;
		} else if (StringUtils.equals(PennantConstants.EMPLOYMENTTYPE_SENP, subCategory)) {
			subCategory = "03";
			return subCategory;
		} else if (StringUtils.equals(PennantConstants.EMPLOYMENTTYPE_SEP, subCategory)) {
			subCategory = "02";
			return subCategory;
		} else {
			subCategory = "04";
			return subCategory;
		}
	}

	private BigDecimal writeCustomerIncomeData(CustomerDetails customerDetail) {
		logger.debug(Literal.ENTERING);
		BigDecimal income = BigDecimal.ZERO;
		List<CustomerIncome> customerIncomeList = customerDetail.getCustomerIncomeList();
		if (CollectionUtils.isEmpty(customerIncomeList)) {
			return income;
		}
		for (CustomerIncome customerIncome : customerIncomeList) {
			if (customerIncome.getIncomeExpense().equals("INCOME")) {
				income = income.add(customerIncome.getIncome());
			}
		}
		logger.debug(Literal.LEAVING);
		return income;
	}

	private String writePaymentFrq(String repayFrq) {
		String frequencyCode = FrequencyUtil.getFrequencyCode(repayFrq);
		String code = "";
		if (StringUtils.equals(FrequencyCodeTypes.FRQ_WEEKLY, frequencyCode)) {
			code = "01";
			return code;
		} else if (StringUtils.equals(FrequencyCodeTypes.FRQ_FORTNIGHTLY, frequencyCode)) {
			code = "02";
			return code;
		} else if (StringUtils.equals(FrequencyCodeTypes.FRQ_MONTHLY, frequencyCode)) {
			code = "03";
			return code;
		} else if (StringUtils.equals(FrequencyCodeTypes.FRQ_QUARTERLY, frequencyCode)) {
			code = "04";
			return code;
		}
		return code;
	}

	/**
	 * @param customerDetal
	 * @return
	 */
	private BigDecimal getCollateralValue(CustomerDetails customerDetal) {
		BigDecimal collateralvalue = BigDecimal.ZERO;
		FinanceEnquiry customerFinance = customerDetal.getCustomerFinance();
		if (customerFinance != null) {
			List<CollateralSetup> collateralSetupDetails = customerFinance.getCollateralSetupDetails();
			if (CollectionUtils.isNotEmpty(collateralSetupDetails)) {
				for (CollateralSetup collateralSetup : collateralSetupDetails) {
					collateralvalue = collateralvalue.add(collateralSetup.getBankValuation());
				}
			}
		}

		return collateralvalue;
	}

	private String writeCollateralType(CustomerDetails customerDetal) {
		String collateralType = "";
		FinanceEnquiry customerFinance = customerDetal.getCustomerFinance();
		if (customerFinance != null) {
			List<CollateralSetup> collateralSetupDetails = customerFinance.getCollateralSetupDetails();
			if (CollectionUtils.isEmpty(collateralSetupDetails)) {
				collateralType = "00";
			} else {
				for (CollateralSetup collateralSetup : collateralSetupDetails) {
					if ("PROPERTY".equalsIgnoreCase(collateralSetup.getCollateralType())) {
						collateralType = "01";
					}
				}
			}
		}
		return collateralType;
	}

	private String getAddrTypeCode(String custAddrType) {
		logger.info(Literal.ENTERING);

		String addrsCode = "";
		if (StringUtils.trimToNull(custAddrType) != null) {
			addrsCode = custAddrType;
		} else {
			addrsCode = "04";
		}

		logger.info(Literal.LEAVING);
		return addrsCode;
	}

	private String getCustomerFullName(Customer customer) {
		logger.info(Literal.ENTERING);

		StringBuilder builder = new StringBuilder();

		if (customer.getCustFName() != null) {
			builder.append(StringUtils.trimToEmpty(customer.getCustFName()));
			if (customer.getCustMName() != null || customer.getCustLName() != null) {
				builder.append(" ");
			}
		}

		if (customer.getCustMName() != null) {
			builder.append(StringUtils.trimToEmpty(customer.getCustMName()));
			if (customer.getCustLName() != null) {
				builder.append(" ");
			}
		}

		if (customer.getCustLName() != null) {
			builder.append(StringUtils.trimToEmpty(customer.getCustLName()));
		}

		return builder.toString();
	}

	private String getDateOfBirth(Date custDOB) {
		return DateUtil.format(custDOB, DATE_FORMAT);
	}

	private String getCustGenCode(Customer customer) {
		logger.info(Literal.ENTERING);

		String genderCode = "";
		if ("M".equals(customer.getCustGenderCode()) || "MALE".equals(customer.getCustGenderCode())
				|| "MA".equals(customer.getCustGenderCode())) {
			genderCode = "2";
		} else if ("F".equals(customer.getCustGenderCode()) || "FEMALE".equals(customer.getCustGenderCode())
				|| "FE".equals(customer.getCustGenderCode())) {
			genderCode = "1";
		}

		logger.info(Literal.LEAVING);
		return genderCode;
	}

	private BigDecimal writtenOffAmount(FinanceEnquiry customerFinance) {
		logger.info(Literal.ENTERING);

		BigDecimal writtenOffAmount = (customerFinance.getTotalPriSchd().subtract(customerFinance.getTotalPriPaid())
				.add(customerFinance.getTotalPftSchd().subtract(customerFinance.getTotalPftPaid())
						.subtract(customerFinance.getExcessAmount().subtract(customerFinance.getExcessAmtPaid()))));

		if (writtenOffAmount.compareTo(BigDecimal.ZERO) < 0) {
			writtenOffAmount = BigDecimal.ZERO;
		}

		logger.info(Literal.LEAVING);
		return writtenOffAmount;
	}

	private BigDecimal writtenOffPrincipal(FinanceEnquiry customerFinance) {
		logger.info(Literal.ENTERING);

		BigDecimal writtenOffPrincipal = (customerFinance.getTotalPriSchd().subtract(customerFinance.getTotalPriPaid())
				.subtract(customerFinance.getExcessAmount().subtract(customerFinance.getExcessAmtPaid())));

		if (writtenOffPrincipal.compareTo(BigDecimal.ZERO) < 0) {
			writtenOffPrincipal = BigDecimal.ZERO;
		}
		logger.info(Literal.LEAVING);

		return writtenOffPrincipal;
	}

	private BigDecimal getAmountOverDue(FinanceEnquiry fm) {
		BigDecimal amountOverdue = BigDecimal.ZERO;
		FinanceSummary finSummary = new FinanceSummary();
		FinODDetails finODDetails = finODDetailsDAO.getFinODSummary(fm.getFinID());

		if (finODDetails != null) {
			finSummary.setFinODTotPenaltyAmt(finODDetails.getTotPenaltyAmt());
			finSummary.setFinODTotWaived(finODDetails.getTotWaived());
			finSummary.setFinODTotPenaltyPaid(finODDetails.getTotPenaltyPaid());
			finSummary.setFinODTotPenaltyBal(finODDetails.getTotPenaltyBal());
		}

		FinanceSummary summary = cibilService.getFinanceProfitDetails(fm.getFinID());
		amountOverdue = summary.getTotalOverDue().add(finSummary.getFinODTotPenaltyBal());
		amountOverdue = PennantApplicationUtil.formateAmount(amountOverdue, AccountConstants.CURRENCY_USD_FORMATTER);

		if (amountOverdue.compareTo(BigDecimal.ZERO) < 0) {
			amountOverdue = BigDecimal.ZERO;
		}

		if (StringUtils.equals("C", fm.getClosingStatus())) {
			amountOverdue = BigDecimal.ZERO;
		}
		return amountOverdue;
	}

	private BigDecimal getCurrentBalance(FinanceEnquiry customerFinance) {
		BigDecimal currentBalance = BigDecimal.ZERO;
		String closingstatus = StringUtils.trimToEmpty(customerFinance.getClosingStatus());
		FinanceSummary finSummary = new FinanceSummary();
		FinODDetails finODDetails = finODDetailsDAO.getFinODSummary(customerFinance.getFinID());

		if (finODDetails != null) {
			finSummary.setFinODTotPenaltyAmt(finODDetails.getTotPenaltyAmt());
			finSummary.setFinODTotWaived(finODDetails.getTotWaived());
			finSummary.setFinODTotPenaltyPaid(finODDetails.getTotPenaltyPaid());
			finSummary.setFinODTotPenaltyBal(finODDetails.getTotPenaltyBal());
		}

		FinanceSummary summary = cibilService.getFinanceProfitDetails(customerFinance.getFinID());

		BigDecimal principalOutstanding = summary.getOutStandPrincipal().subtract(summary.getTotalCpz());

		currentBalance = summary.getTotalOverDue().add(finSummary.getFinODTotPenaltyBal());
		currentBalance = currentBalance.add(principalOutstanding);
		currentBalance = PennantApplicationUtil.formateAmount(currentBalance, AccountConstants.CURRENCY_USD_FORMATTER);

		if (currentBalance.compareTo(BigDecimal.ZERO) < 0) {
			currentBalance = BigDecimal.ZERO;
		}

		if ("C".equals(closingstatus)) {
			currentBalance = BigDecimal.ZERO;
		}

		return currentBalance;
	}

	private String writeCustAddress(CustomerAddres custAddr) {
		StringBuilder builder = new StringBuilder();

		if (custAddr.getCustAddrHNbr() != null) {
			builder.append(StringUtils.trimToEmpty(custAddr.getCustAddrHNbr()));
			if (custAddr.getCustFlatNbr() != null || custAddr.getCustAddrStreet() != null
					|| custAddr.getCustAddrLine1() != null || custAddr.getCustAddrLine2() != null) {
				builder.append(", ");
			}
		}

		if (custAddr.getCustAddrStreet() != null) {
			builder.append(StringUtils.trimToEmpty(custAddr.getCustAddrStreet()));
			if (custAddr.getCustFlatNbr() != null || custAddr.getCustAddrLine1() != null
					|| custAddr.getCustAddrLine2() != null) {
				builder.append(", ");
			}
		}

		if (custAddr.getCustFlatNbr() != null) {
			builder.append(StringUtils.trimToEmpty(custAddr.getCustFlatNbr()));
			if (custAddr.getCustAddrLine1() != null || custAddr.getCustAddrLine2() != null) {
				builder.append(", ");
			}
		}

		if (custAddr.getCustAddrLine1() != null) {
			builder.append(StringUtils.trimToEmpty(custAddr.getCustAddrLine1()));
			if (custAddr.getCustAddrLine2() != null) {
				builder.append(", ");
			}
		}
		if (custAddr.getCustAddrCity() != null) {
			builder.append(StringUtils.trimToEmpty(custAddr.getCustAddrCity()));
			if (custAddr.getCustAddrCity() != null) {
				builder.append(", ");
			}
		}

		/*
		 * if (custAddr.getCustAddrProvince() != null) {
		 * builder.append(StringUtils.trimToEmpty(custAddr.getCustAddrProvince())); if (custAddr.getCustAddrProvince()
		 * != null) { builder.append(","); } }
		 */

		if (custAddr.getCustAddrCountry() != null) {
			builder.append(StringUtils.trimToEmpty(custAddr.getCustAddrCountry()));
			if (custAddr.getCustAddrCountry() != null) {
				builder.append("-");
			}
		}

		if (custAddr.getCustAddrZIP() != null) {
			builder.append(StringUtils.trimToEmpty(custAddr.getCustAddrZIP()));
			if (custAddr.getCustAddrZIP() != null) {
				builder.append(" ");
			}
		}
		String custAddress = builder.toString();

		logger.info(Literal.LEAVING);
		return custAddress;
	}

	private String getMemberDetails() {
		StringBuilder detais = new StringBuilder();
		detais.append(memberDetails.getMemberId()).append(",");
		detais.append(memberDetails.getMemberShortName()).append(",");
		detais.append("").append(",");
		detais.append(DateUtil.getSysDate(DATE_FORMAT)).append(",");
		detais.append(memberDetails.getMemberPassword()).append(",");
		detais.append("").append(",");
		detais.append(memberDetails.getMemberCode());
		return detais.toString();
	}

	private void createHeading(Sheet sheet, int rowIndex) {
		Row row = sheet.createRow(rowIndex);
		Cell cella = row.createCell(0);
		cella.setCellValue("CIBIL HEADING");
		sheet.addMergedRegion(CellRangeAddress.valueOf("A1:E1"));
	}

	private void createFields(Workbook workbook, Sheet sheet, int rowIndex, String header) {
		Row row = sheet.createRow(rowIndex);
		int cellIndex = 0;
		final Font font = workbook.createFont();
		final CellStyle style = workbook.createCellStyle();
		font.setBold(true);
		style.setFont(font);
		for (String fieldName : header.split(",")) {
			createCell(row, cellIndex++, fieldName.trim(), style);
		}
	}

	private void createCell(Row row, int index, String cellValue, CellStyle style) {
		Cell cell = row.createCell(index);
		cell.setCellValue(cellValue);
		cell.setCellStyle(style);
	}

	private void createFields(Sheet sheet, int rowIndex, String header) {
		Row row = sheet.createRow(rowIndex);
		int cellIndex = 0;
		for (String fieldName : header.split(",")) {
			createCell(row, cellIndex++, fieldName.trim());
		}
	}

	private void createCell(Row row, int index, String cellValue) {
		Cell cell = row.createCell(index);
		cell.setCellValue(cellValue);
	}

	private File createFile() throws IOException {
		logger.debug("Creating the file");
		File reportName = null;
		String reportLocation = memberDetails.getFilePath();
		String memberId = memberDetails.getMemberId();

		File directory = new File(reportLocation);

		if (!directory.exists()) {
			directory.mkdirs();
		}
		StringBuilder builder = new StringBuilder(reportLocation);
		builder.append(File.separator);
		builder.append(memberId);
		builder.append("_");
		builder.append(DateUtil.getSysDate("ddMMyyyy"));
		// builder.append("_");
		// builder.append(DateUtil.getSysDate("Hms"));
		builder.append(".txt");
		reportName = new File(builder.toString());
		reportName.createNewFile();
		return reportName;
	}

	private File createXlsFile() throws IOException {
		logger.debug("Creating the file");
		File reportName = null;
		String reportLocation = memberDetails.getFilePath();
		String memberId = memberDetails.getMemberId();
		File directory = new File(reportLocation);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		StringBuilder builder = new StringBuilder(reportLocation);
		builder.append(File.separator);
		builder.append(memberId);
		builder.append("_");
		builder.append(DateUtil.getSysDate("ddMMyyyy"));
		// builder.append("_");
		// builder.append(DateUtil.getSysDate("Hms"));
		builder.append(".xlsx");
		reportName = new File(builder.toString());
		reportName.createNewFile();
		return reportName;
	}

	/**
	 * The TUDF Segment marks the beginning of the Data Input File Format, and:
	 * <li>It is a Required segment.</li>
	 * <li>It is of a fixed size of 146 bytes.</li>
	 * <li>It occurs only once per update file.</li>
	 * <li>All the fields must be provided; otherwise, the entire data input file is rejected.</li>
	 *
	 */
	public class HeaderSegment {
		private BufferedWriter writer;

		public HeaderSegment(BufferedWriter writer) {
			this.writer = writer;
		}

		public void write() throws IOException {
			StringBuilder builder = new StringBuilder();
			writeValue(builder, "TUDF");
			writeValue(builder, "12");
			writeValue(builder, rightPad(memberDetails.getMemberId(), 30, ""));
			writeValue(builder, rightPad(memberDetails.getMemberShortName(), 16, ""));
			writeValue(builder, rightPad("", 2, ""));
			writeValue(builder, SysParamUtil.getAppDate(DATE_FORMAT));
			writeValue(builder, rightPad(memberDetails.getMemberPassword(), 30, ""));
			writeValue(builder, "L");
			writeValue(builder, rightPad("0", 5, "0"));
			writeValue(builder, rightPad("", 48, ""));
			writer.write(builder.toString());
		}
	}

	private String rightPad(String value, int size, String paddedString) {
		return StringUtils.rightPad(value, size, paddedString);
	}

	/**
	 * The PN Segment describes personal consumer information, and:
	 * <li>It is a Required segment.</li>
	 * <li>It is variable in length and can be of a maximum size of 174 bytes.</li>
	 * <li>It occurs only once per record.</li>
	 * <li>Tag 06 is reserved for future use.</li>
	 */
	public class NameSegment {
		private StringBuilder builder;
		private Customer customer;

		public NameSegment(StringBuilder builder, Customer customer) {
			this.builder = builder;
			this.customer = customer;
		}

		public void write() throws Exception {
			writeValue(builder, "PN", "N01", "03");
			writeCustomerName(builder, customer);
			writeValue(builder, "07", DateUtil.format(customer.getCustDOB(), DATE_FORMAT), "08");

			if ("M".equals(customer.getCustGenderCode())) {
				writeValue(builder, "08", "2", "01");
			} else if ("F".equals(customer.getCustGenderCode())) {
				writeValue(builder, "08", "1", "01");
			} else {
				writeValue(builder, "08", "3", "01");
			}

		}
	}

	public class IdentificationSegment {
		private StringBuilder builder;
		private List<CustomerDocument> documents;

		public IdentificationSegment(StringBuilder builder, List<CustomerDocument> documents) {
			this.builder = builder;
			this.documents = documents;
		}

		public void write() {
			int i = 0;
			for (CustomerDocument document : documents) {
				String docCode = document.getCustDocCategory();
				if (docCode == null || "07".equals(docCode) || "08".equals(docCode)) {
					continue;
				}

				if (++i > 8) {
					break;
				}

				writeValue(builder, "ID", "I0" + i, "03");
				writeValue(builder, "01", document.getCustDocCategory(), "02");
				writeValue(builder, "02", document.getCustDocTitle(), 30, "ID");

				if (document.getCustDocIssuedOn() != null) {
					writeValue(builder, "03", DateUtil.format(document.getCustDocIssuedOn(), DATE_FORMAT), "08");
				}
				if (document.getCustDocExpDate() != null) {
					writeValue(builder, "04", DateUtil.format(document.getCustDocExpDate(), DATE_FORMAT), "08");

				}
			}
		}
	}

	/**
	 * The PT Segment contains the known phone numbers of the consumer, and:
	 * <li>This is a Required segment if at least one valid ID segment (with ID Type of 01, 02, 03, 04, or 06) is not
	 * present.</li>
	 * <li>It is variable in length and can be of a maximum size of 28 bytes.</li>
	 * <li>This can occur maximum of 10 times per record.</li>
	 * <li>For accounts opened on/after June 1, 2007, at least one valid Telephone (PT) segment or at least one valid
	 * Identification (ID) segment (with ID Type of 01, 02, 03, 04, or 06) is required. If not provided, the record is
	 * rejected.</li>
	 *
	 */
	public class TelephoneSegment {
		private StringBuilder builder;
		private List<CustomerPhoneNumber> phoneNumbers;

		public TelephoneSegment(StringBuilder builder, List<CustomerPhoneNumber> phoneNumbers) {
			this.builder = builder;
			this.phoneNumbers = phoneNumbers;
		}

		public void write() {

			if (phoneNumbers == null || phoneNumbers.isEmpty()) {
				return;
			}

			int i = 0;
			for (CustomerPhoneNumber phoneNumber : phoneNumbers) {
				if (++i > 10) {
					break;
				}

				writeValue(builder, "PT", "T" + StringUtils.leftPad(String.valueOf(i), 2, "0"), "03");
				writeValue(builder, "01", phoneNumber.getPhoneNumber(), 20, "PT");
				if (phoneNumber.getPhoneTypeCode() != null) {
					writeValue(builder, "03", phoneNumber.getPhoneTypeCode(), "02");
				} else {
					writeValue(builder, "03", "00", "02");
				}
			}
		}
	}

	/**
	 * The EC Segment contains the email address of the consumer, and:
	 * <li>This is a When Available segment.</li>
	 * <li>It is variable in length and can be of a maximum size of 81 bytes.</li>
	 * <li>This can occur maximum of 10 times per record.</li>
	 */
	public class EmailContactSegment {
		private StringBuilder builder;
		private List<CustomerEMail> emails;

		public EmailContactSegment(StringBuilder builder, List<CustomerEMail> emails) {
			this.builder = builder;
			this.emails = emails;
		}

		public void write() {

			if (emails == null || emails.isEmpty()) {
				return;
			}

			int i = 0;
			for (CustomerEMail email : emails) {
				if (++i > 10) {
					break;
				}

				writeValue(builder, "EC", "C" + StringUtils.leftPad(String.valueOf(i), 2, "0"), "03");
				writeValue(builder, "01", email.getCustEMail(), 70, "EC");

			}
		}
	}

	/**
	 * The PA Segment contains the known address of the consumer, and:
	 * <li>It is a Required segment.</li>
	 * <li>It is variable in length and can be of a maximum size of 259 bytes.</li>
	 * <li>This can occur maximum of 5 times per record.</li>
	 * <li>Any extra PA Segments after the 5th one will be rejected.</li>
	 * <li>At least one valid PA Segment is required. All invalid PA Segments will be rejected.</li>
	 * <li>It can be provided as free format in Address Line Fields 1-5.</li>
	 *
	 */
	public class AddressSegment {
		private StringBuilder builder;
		private List<CustomerAddres> addresses;

		public AddressSegment(StringBuilder builder, List<CustomerAddres> addresses) {
			this.builder = builder;
			this.addresses = addresses;
		}

		public void write() throws Exception {

			if (addresses == null || addresses.isEmpty()) {
				throw new Exception("Address details are not available.");
			}

			int i = 0;
			for (CustomerAddres address : addresses) {
				if (++i > 5) {
					break;
				}
				writeValue(builder, "PA", "A".concat(StringUtils.leftPad(String.valueOf(i), 2, "0")), "03");
				writeCustomerAddress(builder, address);

				String province = StringUtils.trimToNull(address.getCustAddrProvince());
				if (province == null) {
					province = "99";
				}

				writeValue(builder, "06", province, "02");

				writeValue(builder, "07", address.getCustAddrZIP(), 10, "PA");

				String addrType = StringUtils.trimToNull(address.getCustAddrType());

				if (addrType == null) {
					addrType = "04";
				}

				writeValue(builder, "08", addrType, "02");
			}
		}
	}

	public class AccountSegment {
		private StringBuilder builder;
		private FinanceEnquiry loan;

		public AccountSegment(StringBuilder builder, FinanceEnquiry loan) {
			this.builder = builder;
			this.loan = loan;
		}

		public void write() {
			writeValue(builder, "TL", "T001", "04");
			writeValue(builder, "01", StringUtils.rightPad(memberDetails.getMemberCode(), 10, ""), "10");
			writeValue(builder, "02", memberDetails.getMemberShortName(), 16, "TL");
			writeValue(builder, "03", StringUtils.trimToEmpty(loan.getFinReference()), 25, "TL");

			String finType = StringUtils.trimToNull(loan.getFinType());
			if (finType == null) {
				finType = "00";
			}

			writeValue(builder, "04", finType, "02");
			writeValue(builder, "05", StringUtils.trimToEmpty(String.valueOf(loan.getOwnership())), "01");
			writeValue(builder, "08", DateUtil.format(loan.getFinApprovedDate(), DATE_FORMAT), "08");

			if (loan.getLatestRpyDate() != null) {
				writeValue(builder, "09", DateUtil.format(loan.getLatestRpyDate(), DATE_FORMAT), "08");
			}

			BigDecimal currentBalance = getCurrentBalance(loan);
			String closingstatus = StringUtils.trimToEmpty(loan.getClosingStatus());

			// ### PSD --127340 20-06-2018
			if (currentBalance != null) {
				if (currentBalance.compareTo(BigDecimal.ZERO) == 0 && loan.getLatestRpyDate() != null) {
					writeValue(builder, "10", DateUtil.format(loan.getLatestRpyDate(), DATE_FORMAT), "08");
				}
			}
			writeValue(builder, "11", SysParamUtil.getAppDate(DATE_FORMAT), "08");
			writeValue(builder, "12", loan.getFinAssetValue(), 9, "TL");
			writeValue(builder, "13", currentBalance, 9, "TL");

			BigDecimal amountOverdue = getAmountOverDue(loan);

			writeValue(builder, "14", amountOverdue, 9, "TL");

			if (amountOverdue.compareTo(BigDecimal.ZERO) <= 0) {
				writeValue(builder, "15", "0", 3, "TL");
			} else {
				writeValue(builder, "15", getOdDays(finODDetailsDAO.getFinODDays(loan.getFinID())), 3, "TL");
			}

			if (closingstatus.equals("W")) {
				// 02 = Written-off
				closingstatus = "02";
				writeValue(builder, "22", closingstatus, 2, "TL");
			}

			writeValue(builder, "38", loan.getRepayProfitRate(), 9, "TL");
			writeValue(builder, "39", String.valueOf(loan.getNumberOfTerms()), 9, "TL");
			writeValue(builder, "40", PennantApplicationUtil.formateAmount(loan.getFirstRepay(), 2), 9, "TL");

			if (StringUtils.equals("02", closingstatus) || (StringUtils.equals("03", closingstatus))) {

				BigDecimal writtenOffAmount = (loan.getTotalPriSchd().subtract(loan.getTotalPriPaid())
						.add(loan.getTotalPftSchd().subtract(loan.getTotalPftPaid())
								.subtract(loan.getExcessAmount().subtract(loan.getExcessAmtPaid()))));

				if (writtenOffAmount.compareTo(BigDecimal.ZERO) < 0) {
					writtenOffAmount = BigDecimal.ZERO;
				}

				writeValue(builder, "41", writtenOffAmount, 9, "TL");

				BigDecimal writtenOffPrincipal = (loan.getTotalPriSchd().subtract(loan.getTotalPriPaid())
						.subtract(loan.getExcessAmount().subtract(loan.getExcessAmtPaid())));

				if (writtenOffPrincipal.compareTo(BigDecimal.ZERO) < 0) {
					writtenOffPrincipal = BigDecimal.ZERO;
				}

				writeValue(builder, "42", writtenOffPrincipal, 9, "TL");
			}

			String rePayfrq = StringUtils.trimToEmpty(loan.getRepayFrq());
			String frequecncy = null;

			if (rePayfrq.length() > 0) {
				frequecncy = rePayfrq.substring(0, 1);
			}

			if ("M".equals(frequecncy)) {
				rePayfrq = "03";
			} else if ("Q".equals(frequecncy)) {
				rePayfrq = "04";
			} else if ("D".equals(frequecncy)) {
				rePayfrq = "01";
			} else if ("F".equals(frequecncy)) {
				rePayfrq = "02";
			}

			if (StringUtils.isEmpty(rePayfrq)) {
				rePayfrq = "03";
			}

			writeValue(builder, "44", rePayfrq, 9, "TL");
		}
	}

	public class AccountSegmentHistory {
		private StringBuilder builder;
		private List<FinanceEnquiry> loans;

		public AccountSegmentHistory(StringBuilder builder, List<FinanceEnquiry> loans) {
			this.builder = builder;
			this.loans = loans;
		}

		public void write() {
			int i = 0;
			for (FinanceEnquiry loan : loans) {

				if (++i > 35) {
					break;
				}
				writeValue(builder, "TH", "H".concat(StringUtils.leftPad(String.valueOf(i), 2, "0")), "03");

				writeValue(builder, "01", SysParamUtil.getAppDate(DATE_FORMAT), "08");
				writeValue(builder, "02", getOdDays(finODDetailsDAO.getFinODDays(loan.getFinID())), "03");
				writeValue(builder, "03", getAmountOverDue(loan), 9, "TH");
				writeValue(builder, "04", loan.getFinAssetValue(), 9, "TH");
				writeValue(builder, "07", getCurrentBalance(loan), 9, "TH");
				writeValue(builder, "08", DateUtil.format(loan.getLatestRpyDate(), DATE_FORMAT), "08");
				writeValue(builder, "09", PennantApplicationUtil.formateAmount(loan.getFirstRepay(), 2), 9, "TH");
			}
		}
	}

	public class EndofSubjectSegment {
		private StringBuilder builder;

		public EndofSubjectSegment(StringBuilder writer) {
			this.builder = writer;
		}

		public void write() {
			builder.append("ES02**");
		}
	}

	public class TrailerSegment {
		private BufferedWriter writer;

		public TrailerSegment(BufferedWriter writer) {
			this.writer = writer;
		}

		public void write() throws IOException {
			writer.write("TRLR");
		}
	}

	/**
	 * Append the value to StringBuilder
	 * 
	 * @param builder The StringBuilder to append the value.
	 * @param value   The value to be append.
	 * 
	 */
	private void writeValue(StringBuilder builder, String value) {
		builder.append(value);
	}

	/**
	 * Append the fixed length tags to provided StringBuilder
	 * 
	 * @param builder  The StringBuilder to append the tags.
	 * @param fieldTag Filed Tag
	 * @param value    Filed value
	 * @param size     length of the value
	 */
	private void writeValue(StringBuilder builder, String fieldTag, String value, String size) {
		writeValue(builder, concat(fieldTag, size, value));
	}

	private String concat(String fieldTag, String length, String value) {
		return fieldTag.concat(length).concat(value);
	}

	/**
	 * Append variable length tags to provided StringBuilder
	 * 
	 * @param builder   The StringBuilder tags.
	 * @param fieldTag  Filed Tag
	 * @param value     Filed Value
	 * @param maxLength Max length of the tag
	 * @throws IllegalArgumentException
	 */
	private void writeValue(StringBuilder builder, String fieldTag, String value, int maxLength, String segment) {
		if (StringUtils.isBlank(value)) {
			return;
		}
		int size = value.length();
		String length = null;
		if (maxLength < 99) {
			length = StringUtils.leftPad(String.valueOf(size), 2, "0");
		} else if (maxLength < 999) {
			length = StringUtils.leftPad(String.valueOf(size), 3, "0");
		} else if (maxLength < 9999) {
			length = StringUtils.leftPad(String.valueOf(size), 4, "0");
		}
		if (value.length() > maxLength) {
			throw new IllegalArgumentException(
					String.format("Max length exceeded for the tag %s in segment %s", fieldTag, segment));
		}
		builder.append(concat(fieldTag, length, value));

	}

	private void writeValue(StringBuilder builder, String fieldTag, BigDecimal amount, int maxLength, String segment) {
		if (amount == null) {
			return;
		}

		amount = amount.setScale(0, RoundingMode.HALF_DOWN);
		writeValue(builder, fieldTag, amount.toString(), maxLength, segment);
	}

	// changes to differentiate the CIBIL Member ID during CIBIL generation & enquiry
	private void initlize() {
		memberDetails = cibilService.getMemberDetailsByType(PennantConstants.PFF_CUSTCTG_INDIV,
				PennantConstants.PFF_CIBIL_TYPE_GENERATE);
		totalRecords = 0;
		processedRecords = 0;
		successCount = 0;
		failedCount = 0;
		xlsxRowCount = 3;
	}

	private String getOdDays(int odDays) {
		if (odDays > 900) {
			odDays = 900;
		}
		return String.valueOf(odDays);
	}

	private void writeCustomerName(StringBuilder writer, Customer customer) throws Exception {
		StringBuilder builder = new StringBuilder();

		if (customer.getCustFName() != null) {
			builder.append(StringUtils.trimToEmpty(customer.getCustFName()));
			if (customer.getCustMName() != null || customer.getCustLName() != null) {
				builder.append(" ");
			}
		}

		if (customer.getCustMName() != null) {
			builder.append(StringUtils.trimToEmpty(customer.getCustMName()));
			if (customer.getCustLName() != null) {
				builder.append(" ");
			}
		}

		if (customer.getCustLName() != null) {
			builder.append(StringUtils.trimToEmpty(customer.getCustLName()));
		}

		String customerName = builder.toString();
		try {
			Matcher regexMatcher = null;
			Pattern regex = Pattern.compile("[0-9]");
			Matcher regexMatcherforNum = regex.matcher(customerName);

			if (!regexMatcherforNum.find()) {
				regex = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");
				regexMatcher = regex.matcher(customerName);
			} else {
				throw new Exception("Customer Name should not contain numbers");
			}

			builder = new StringBuilder();
			int field = 0;
			while (regexMatcher.find()) {
				if (field >= 5) {
					break;
				}

				String name = regexMatcher.group();

				if ((builder.length() + name.length()) < 26) {
					if (builder.length() > 0) {
						builder.append(" ");
					}
					builder.append(name);

				} else {
					writeValue(writer, "0".concat(String.valueOf(++field)),
							StringUtils.substring(builder.toString(), 0, 26), 26, "PN");
					builder = new StringBuilder();
					builder.append(name);
				}

			}

			if (builder.length() > 0) {
				if (builder.length() > 26) {
					writeValue(writer, "0".concat(String.valueOf(++field)),
							StringUtils.substring(builder.toString(), 0, 26), 26, "PN");
				} else {
					writeValue(writer, "0".concat(String.valueOf(++field)), builder.toString(), 26, "PN");
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
	}

	private void writeCustomerAddress(StringBuilder writer, CustomerAddres custAddr) {
		StringBuilder builder = new StringBuilder();

		if (custAddr.getCustAddrHNbr() != null) {
			builder.append(StringUtils.trimToEmpty(custAddr.getCustAddrHNbr()));
			if (custAddr.getCustFlatNbr() != null || custAddr.getCustAddrStreet() != null
					|| custAddr.getCustAddrLine1() != null || custAddr.getCustAddrLine2() != null) {
				builder.append(" ");
			}
		}

		if (custAddr.getCustAddrStreet() != null) {
			builder.append(StringUtils.trimToEmpty(custAddr.getCustAddrStreet()));
			if (custAddr.getCustFlatNbr() != null || custAddr.getCustAddrLine1() != null
					|| custAddr.getCustAddrLine2() != null) {
				builder.append(" ");
			}
		}

		if (custAddr.getCustFlatNbr() != null) {
			builder.append(StringUtils.trimToEmpty(custAddr.getCustFlatNbr()));
			if (custAddr.getCustAddrLine1() != null || custAddr.getCustAddrLine2() != null) {
				builder.append(" ");
			}
		}

		if (custAddr.getCustAddrLine1() != null) {
			builder.append(StringUtils.trimToEmpty(custAddr.getCustAddrLine1()));
			if (custAddr.getCustAddrLine2() != null) {
				builder.append(" ");
			}
		}

		if (custAddr.getCustAddrLine2() != null) {
			builder.append(StringUtils.trimToEmpty(custAddr.getCustAddrLine2()));
		}

		String custAddress = builder.toString();
		try {

			Pattern regex = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");
			Matcher regexMatcher = regex.matcher(custAddress);

			builder = new StringBuilder();
			int field = 0;
			while (regexMatcher.find()) {
				if (field >= 5) {
					break;
				}

				String address = regexMatcher.group();

				if ((builder.length() + address.length()) < 40) {
					if (builder.length() > 0) {
						builder.append(" ");
					}
					builder.append(address);

				} else {
					writeValue(writer, "0".concat(String.valueOf(++field)),
							StringUtils.substring(builder.toString(), 0, 40), 40, "PA");
					builder = new StringBuilder();
					builder.append(address);
				}

			}

			if (builder.length() > 0) {
				if (builder.length() > 40) {
					writeValue(writer, "0".concat(String.valueOf(++field)),
							StringUtils.substring(builder.toString(), 0, 40), 40, "PA");
				} else {
					writeValue(writer, "0".concat(String.valueOf(++field)), builder.toString(), 40, "PA");
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	private String updateRemarks() {
		StringBuilder remarks = new StringBuilder();
		if (failedCount > 0) {
			remarks.append("Completed with exceptions, total Records: ");
			remarks.append(totalRecords);
			remarks.append(", Success: ");
			remarks.append(successCount);
			remarks.append(", Failure: ");
			remarks.append(failedCount);

		} else {
			remarks.append("Completed successfully, total Records: ");
			remarks.append(totalRecords);
			remarks.append(", Success: ");
			remarks.append(successCount);
		}
		return remarks.toString();
	}

	public FinScheduleData getFinSchDataById(long finID, String type, boolean summaryRequired) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = new FinScheduleData();
		FinanceMain fm = getFinanceMainDAO().getFinanceMainById(finID, type, false);
		setDasAndDmaData(fm);
		if (fm == null) {
			return schdData;
		}

		schdData.setFinID(fm.getFinID());
		schdData.setFinReference(fm.getFinReference());
		schdData.setFinanceMain(fm);

		// Overdraft Schedule Details
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, fm.getProductCategory())) {
			schdData.setOverdraftScheduleDetails(
					getOverdraftScheduleDetailDAO().getOverdraftScheduleDetails(finID, "_Temp", false));
		}

		// Schedule details
		// finSchData.setFinanceScheduleDetails(
		// getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type, false));

		// Disbursement Details
		// finSchData.setDisbursementDetails(
		// getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, type, false));

		// Repay instructions
		// finSchData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference, type, false));

		// od penality details
		// finSchData.setFinODPenaltyRate(getFinODPenaltyRateDAO().getFinODPenaltyRateByRef(finReference, type));

		if (summaryRequired) {

			// Finance Type
			// finSchData.setFinanceType(getFinanceTypeDAO().getFinanceTypeByFinType(financeMain.getFinType()));
			// Finance Type Details
			FinanceType financeType = null;// getFinanceTypeDAO().getFinanceTypeByFinType(financeMain.getFinType());
			if (financeType != null && StringUtils.isNotBlank(fm.getPromotionCode())) {
				// Fetching Promotion Details
				Promotion promotion = this.promotionDAO.getPromotionByReferenceId(fm.getPromotionSeqId(), "_AView");
				financeType.setFInTypeFromPromotiion(promotion);
			}
			// finSchData.setFinanceType(financeType);

			// Suspense
			schdData.setFinPftSuspended(false);
			FinanceSuspHead financeSuspHead = null;// getFinanceSuspHeadDAO().getFinanceSuspHeadById(finReference, "");
			if (financeSuspHead != null && financeSuspHead.isFinIsInSusp()) {
				schdData.setFinPftSuspended(true);
				schdData.setFinSuspDate(financeSuspHead.getFinSuspDate());
			}

			// Finance Summary Details Preparation
			final Date curBussDate = SysParamUtil.getAppDate();
			FinanceSummary summary = new FinanceSummary();
			summary.setFinReference(fm.getFinReference());
			summary.setSchDate(curBussDate);

			if (fm.isAllowGrcPeriod() && curBussDate.compareTo(fm.getNextGrcPftDate()) <= 0) {
				summary.setNextSchDate(fm.getNextGrcPftDate());
			} else if (fm.getNextRepayDate().compareTo(fm.getNextRepayPftDate()) < 0) {
				summary.setNextSchDate(fm.getNextRepayDate());
			} else {
				summary.setNextSchDate(fm.getNextRepayPftDate());
			}

			// commented because we are fetching total fees from FinfeeDeatail
			// table
			/*
			 * summary = getFinanceScheduleDetailDAO().getFinanceSummaryDetails(summary); summary =
			 * getFinFeeDetailDAO().getTotalFeeCharges(summary);
			 */
			// summary.setFinCurODDays(getProfitDetailsDAO().getCurOddays(finReference, ""));
			schdData.setFinanceSummary(summary);

			FinODDetails finODDetails = getFinODDetailsDAO().getFinODSummary(finID);
			if (finODDetails != null) {
				summary.setFinODTotPenaltyAmt(finODDetails.getTotPenaltyAmt());
				summary.setFinODTotWaived(finODDetails.getTotWaived());
				summary.setFinODTotPenaltyPaid(finODDetails.getTotPenaltyPaid());
				summary.setFinODTotPenaltyBal(finODDetails.getTotPenaltyBal());
			}
		}

		logger.debug(Literal.LEAVING);
		return schdData;
	}

	private BigDecimal getEmiAmount(long finID) {
		List<FinanceScheduleDetail> list = getFinanceScheduleDetailDAO().getFinScheduleDetails(finID, "", 0);
		BigDecimal emiAmount = BigDecimal.ZERO;
		for (FinanceScheduleDetail schd : list) {
			if ((schd.isRepayOnSchDate() || schd.isPftOnSchDate())
					&& schd.getPartialPaidAmt().compareTo(BigDecimal.ZERO) == 0) {
				emiAmount = schd.getRepayAmount();
			}
			if (schd.getSchDate().compareTo(SysParamUtil.getAppDate()) > 0) {
				break;
			}
		}
		return emiAmount;
	}

	private void setDasAndDmaData(FinanceMain financeMain) {
		logger.debug(Literal.ENTERING);
		List<Long> dealerIds = new ArrayList<>();
		long dsaCode = 0;
		long dmaCode = 0;
		long connectorCode = 0;
		if (StringUtils.isNotBlank(financeMain.getDsaCode()) && StringUtils.isNumeric(financeMain.getDsaCode())) {
			dsaCode = Long.parseLong(financeMain.getDsaCode());
			dealerIds.add(dsaCode);
		}
		if (StringUtils.isNotBlank(financeMain.getDmaCode()) && StringUtils.isNumeric(financeMain.getDmaCode())) {
			dmaCode = Long.parseLong(financeMain.getDmaCode());
			dealerIds.add(dmaCode);
		}
		if (financeMain.getConnector() > 0) {
			connectorCode = financeMain.getConnector();
			dealerIds.add(financeMain.getConnector());
		}
		if (dealerIds.size() > 0) {
			List<VehicleDealer> vehicleDealerList = getVehicleDealerDAO().getVehicleDealerById(dealerIds);
			if (vehicleDealerList != null && !vehicleDealerList.isEmpty()) {
				for (VehicleDealer dealer : vehicleDealerList) {
					if (dealer.getDealerId() == dmaCode) {
						financeMain.setDmaName(dealer.getDealerName());
						financeMain.setDmaCodeDesc(dealer.getCode());
					} else if (dealer.getDealerId() == dsaCode) {
						financeMain.setDsaName(dealer.getDealerName());
						financeMain.setDsaCodeDesc(dealer.getCode());
					} else if (dealer.getDealerId() == connectorCode) {
						financeMain.setConnectorCode(dealer.getDealerName());
						financeMain.setConnectorDesc(dealer.getCode());
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	@Autowired
	public void setCibilService(CIBILService cibilService) {
		this.cibilService = cibilService;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public OverdraftScheduleDetailDAO getOverdraftScheduleDetailDAO() {
		return overdraftScheduleDetailDAO;
	}

	@Autowired
	public void setOverdraftScheduleDetailDAO(OverdraftScheduleDetailDAO overdraftScheduleDetailDAO) {
		this.overdraftScheduleDetailDAO = overdraftScheduleDetailDAO;
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	@Autowired
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public FinanceDisbursementDAO getFinanceDisbursementDAO() {
		return financeDisbursementDAO;
	}

	@Autowired
	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	public RepayInstructionDAO getRepayInstructionDAO() {
		return repayInstructionDAO;
	}

	@Autowired
	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	public FinODPenaltyRateDAO getFinODPenaltyRateDAO() {
		return finODPenaltyRateDAO;
	}

	@Autowired
	public void setFinODPenaltyRateDAO(FinODPenaltyRateDAO finODPenaltyRateDAO) {
		this.finODPenaltyRateDAO = finODPenaltyRateDAO;
	}

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}

	@Autowired
	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	@Autowired
	public void setPromotionDAO(PromotionDAO promotionDAO) {
		this.promotionDAO = promotionDAO;
	}

	public FinanceSuspHeadDAO getFinanceSuspHeadDAO() {
		return financeSuspHeadDAO;
	}

	@Autowired
	public void setFinanceSuspHeadDAO(FinanceSuspHeadDAO financeSuspHeadDAO) {
		this.financeSuspHeadDAO = financeSuspHeadDAO;
	}

	public FinanceProfitDetailDAO getProfitDetailsDAO() {
		return profitDetailsDAO;
	}

	public FinODDetailsDAO getFinODDetailsDAO() {
		return finODDetailsDAO;
	}

	@Autowired
	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	@Autowired
	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	public VehicleDealerDAO getVehicleDealerDAO() {
		return vehicleDealerDAO;
	}

	@Autowired
	public void setVehicleDealerDAO(VehicleDealerDAO vehicleDealerDAO) {
		this.vehicleDealerDAO = vehicleDealerDAO;
	}

}
