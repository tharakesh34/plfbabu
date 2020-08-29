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

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.service.cibil.CIBILService;
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

	private CIBILService cibilService;

	public RetailCibilReport() {
		super();
	}

	public void generateReport() throws Exception {
		logger.debug(Literal.ENTERING);

		initlize();

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
			totalRecords = cibilService.extractCustomers(PennantConstants.PFF_CUSTCTG_INDIV);
			EXTRACT_STATUS.setTotalRecords(totalRecords);

			if ("TUFF".equals(memberDetails.getFileFormate())) {
				tuffFormat(cibilFile);
			} else {
				xlsxFormat(cibilFile);
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
			if (properties.getStorageType().equalsIgnoreCase("MOVE_TO_S3_BUCKET")) {
				DataEngineUtil.postEvents(Event.MOVE_TO_S3_BUCKET.name(), properties, cibilFile);
			} else if (properties.getStorageType().equalsIgnoreCase("COPY_TO_SFTP")) {
				DataEngineUtil.postEvents(Event.COPY_TO_SFTP.name(), properties, cibilFile);
			} else if (properties.getStorageType().equalsIgnoreCase("COPY_TO_FTP")) {
				DataEngineUtil.postEvents(Event.COPY_TO_FTP.name(), properties, cibilFile);
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

	private void tuffFormat(File cibilFile) throws Exception, IOException {
		try (final BufferedWriter writer = new BufferedWriter(new FileWriter(cibilFile))) {
			new HeaderSegment(writer).write();
			StringBuilder sql = new StringBuilder();
			sql.append("select CUSTID, FINREFERENCE, OWNERSHIP From CIBIL_CUSTOMER_EXTRACT");
			sql.append(" where segment_type = :segment_type ");
			MapSqlParameterSource parameterSource = new MapSqlParameterSource();
			parameterSource.addValue("segment_type", PennantConstants.PFF_CUSTCTG_INDIV);
			jdbcTemplate.query(sql.toString(), parameterSource, new RowCallbackHandler() {
				@Override
				public void processRow(ResultSet rs) throws SQLException {
					processedRecords++;
					EXTRACT_STATUS.setProcessedRecords(processedRecords);
					String finreference = rs.getString("FINREFERENCE");
					long customerId = rs.getLong("CUSTID");

					try {
						CustomerDetails customer = cibilService.getCustomerDetails(customerId, finreference,
								PennantConstants.PFF_CUSTCTG_INDIV);

						if (customer == null || customer.getCustomerFinance() == null) {
							failedCount++;
							cibilService.logFileInfoException(headerId, String.valueOf(customerId),
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

						new EndofSubjectSegment(builder).write();

						writer.write(builder.toString());

						EXTRACT_STATUS.setSuccessRecords(successCount++);
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						EXTRACT_STATUS.setFailedRecords(failedCount++);
						cibilService.logFileInfoException(headerId, String.valueOf(customerId), e.getMessage());

					}
				}
			});
			new TrailerSegment(writer).write();
		}
	}

	private void xlsxFormat(File cibilFile) throws Exception, IOException {
		try (FileOutputStream outputStream = new FileOutputStream(cibilFile)) {
			try (BufferedOutputStream bos = new BufferedOutputStream(outputStream)) {
				try (Workbook workbook = new XSSFWorkbook()) {
					int rowIndex = 0;

					Sheet sheet = workbook.createSheet();

					createHeading(sheet, rowIndex++);

					createFields(workbook, sheet, rowIndex++, MEMBER_FIELDS);

					createFields(sheet, rowIndex++, getMemberDetails());

					createFields(sheet, rowIndex++, HEADER_FIELDS);

					StringBuilder sql = new StringBuilder();
					sql.append("select CUSTID, FINREFERENCE, OWNERSHIP From CIBIL_CUSTOMER_EXTRACT");
					sql.append(" where segment_type = :segment_type ");
					MapSqlParameterSource parameterSource = new MapSqlParameterSource();
					parameterSource.addValue("segment_type", PennantConstants.PFF_CUSTCTG_INDIV);
					jdbcTemplate.query(sql.toString(), parameterSource, new RowCallbackHandler() {
						@Override
						public void processRow(ResultSet rs) throws SQLException {
							EXTRACT_STATUS.setProcessedRecords(processedRecords++);
							xlsxRowCount++;
							String finreference = rs.getString("FINREFERENCE");
							long customerId = rs.getLong("CUSTID");
							try {
								CustomerDetails customer = cibilService.getCustomerDetails(customerId, finreference,
										PennantConstants.PFF_CUSTCTG_INDIV);
								if (customer == null) {
									failedCount++;
									cibilService.logFileInfoException(headerId, String.valueOf(customerId),
											"Unable to fetch the details.");
									return;
								}
								appendRow(customer, xlsxRowCount, sheet);
								EXTRACT_STATUS.setSuccessRecords(successCount++);
							} catch (Exception e) {
								EXTRACT_STATUS.setFailedRecords(failedCount++);
								cibilService.logFileInfoException(headerId, String.valueOf(customerId), e.getMessage());
								logger.error(Literal.EXCEPTION, e);
							}
						}
					});
					workbook.write(bos);
				}
			}
		}
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

		cell.setCellValue(getCustGenCode(customer.getCustGenderCode()));

		String docCategory;
		String docTitle;
		for (CustomerDocument document : documents) {

			docCategory = document.getCustDocCategory();
			docTitle = document.getCustDocTitle();

			if (StringUtils.equals(docCategory, "01")) {
				cell = row.createCell(3);
				cell.setCellValue(docTitle);
			}
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
		row.createCell(13);

		/* Additional ID #2 */
		row.createCell(14);

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
		row.createCell(18);

		/* Telephone No.Other */
		row.createCell(19);

		/* Extension Other */
		row.createCell(20);

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
		cell.setCellValue(memberDetails.getMemberCode());

		/* Current New Member Name */
		cell = row.createCell(34);
		cell.setCellValue(memberDetails.getMemberName());

		/* Ownership Indicator */
		row.createCell(37);

		/* Old Member Code */
		row.createCell(46);

		/* Old Member ShortName */
		row.createCell(48);

		/* Old AccType */
		row.createCell(49);

		/* Old Ownership Indicator */
		row.createCell(50);

		/* Suit Filed Willful Default */
		row.createCell(51);

		/* Written-off and Settled Status */
		row.createCell(52);

		if (customerDetal.getCustomerFinance() != null) {
			FinanceEnquiry finance = customerDetal.getCustomerFinance();
			/* Current New Account No */
			cell = row.createCell(35);
			cell.setCellValue(finance.getFinReference());

			/* Account Type */
			cell = row.createCell(36);
			cell.setCellValue(finance.getFinType());

			/* DateOpened/Disbursed */
			cell = row.createCell(38);
			cell.setCellValue(DateUtil.format(finance.getFinApprovedDate(), DATE_FORMAT));

			/* Ownership Indicator */
			cell = row.createCell(37);
			cell.setCellValue(finance.getOwnership());

			/* Date Of LastPayment */
			cell = row.createCell(39);
			cell.setCellValue(DateUtil.format(finance.getLatestRpyDate(), DATE_FORMAT));

			/* Date Closed */
			row.createCell(40);
			cell.setCellValue(DateUtil.format(finance.getMaturityDate(), DATE_FORMAT));

			/* Date Reported */
			cell = row.createCell(41);
			cell.setCellValue(SysParamUtil.getAppDate(DATE_FORMAT));

			/* High Credit/Sanctioned Amount */
			cell = row.createCell(42);
			cell.setCellValue(String.valueOf(finance.getFinAssetValue()));

			/* Current Balance */
			cell = row.createCell(43);
			cell.setCellValue(String.valueOf(currentBalance(finance)));

			/* Amount Overdue */
			cell = row.createCell(44);
			cell.setCellValue(String.valueOf(amountOverDue(finance)));

			/* Number Of Days PastDue */
			cell = row.createCell(45);
			cell.setCellValue(finance.getCurODDays());

			/* Rate of Interest */
			cell = row.createCell(58);
			cell.setCellValue(String.valueOf(finance.getRepayProfitRate()));

			/* Repayment Tenure */
			cell = row.createCell(59);
			cell.setCellValue(finance.getNumberOfTerms());

			/* EMI Amount */
			cell = row.createCell(60);
			cell.setCellValue(String.valueOf(finance.getInstalmentDue()));

			/* Written off AmountTotal */
			cell = row.createCell(61);
			BigDecimal writnAmount = writtenOffAmount(finance);
			cell.setCellValue(String.valueOf(writnAmount));

			/* Written off Principal Amount */
			cell = row.createCell(62);
			BigDecimal writnPrincpl = writtenOffPrincipal(finance);
			cell.setCellValue(String.valueOf(writnPrincpl));

			/* Income */
			cell = row.createCell(67);
			cell.setCellValue("");
		}

		/* Asset Classification */
		row.createCell(53);

		/* Value of Collateral */
		row.createCell(54);

		/* Type of Collateral */
		row.createCell(55);

		/* Rate of Interest */
		row.createCell(58);

		/* Payment Frequency */
		row.createCell(64);

		/* Credit Limit */
		row.createCell(56);

		/* Cash Limit */
		row.createCell(57);

		/* Settlement Amt */
		row.createCell(63);

		/* Actual Payment Amt */
		row.createCell(65);

		/* Occupation Code */
		row.createCell(66);

		/* Net/Gross Income Indicator */
		row.createCell(68);

		/* Monthly/Annual Income Indicator */
		row.createCell(69);
	}

	private String getAddrTypeCode(String custAddrType) {
		String addrsCode = "";
		if (custAddrType != null) {
			if ("01".equals(custAddrType)) {
				addrsCode = "1";
			} else if ("02".equals(custAddrType)) {
				addrsCode = "2";
			} else if ("03".equals(custAddrType)) {
				addrsCode = "3";
			}
		}
		return addrsCode;
	}

	private String getCustomerFullName(Customer customer) {
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

	private String getCustGenCode(String custGenCode) {
		String genderCode = "";
		if ("M".equals(custGenCode) || "MALE".equals(custGenCode)) {
			genderCode = "2";
		} else if ("F".equals(custGenCode) || "FEMALE".equals(custGenCode)) {
			genderCode = "1";
		} else {
			genderCode = "3";
		}
		return genderCode;
	}

	private BigDecimal writtenOffAmount(FinanceEnquiry fm) {
		BigDecimal writtenOffAmount = BigDecimal.ZERO;
		if (PennantConstants.FIN_CLOSE_STATUS_WRITEOFF.equals(fm.getClosingStatus())) {
			BigDecimal excessBal = (fm.getExcessAmount().subtract(fm.getExcessAmtPaid()));
			BigDecimal unpaidEmi = (fm.getTotalPriSchd().subtract(fm.getTotalPriPaid())
					.add(fm.getTotalPftSchd().subtract(fm.getTotalPftPaid())));
			writtenOffAmount = unpaidEmi.subtract(excessBal);
		}

		if (writtenOffAmount.compareTo(BigDecimal.ZERO) < 0) {
			writtenOffAmount = BigDecimal.ZERO;
		}

		return writtenOffAmount;
	}

	private BigDecimal writtenOffPrincipal(FinanceEnquiry fm) {
		BigDecimal writtenOffPrincipal = BigDecimal.ZERO;
		if (PennantConstants.FIN_CLOSE_STATUS_WRITEOFF.equals(fm.getClosingStatus())) {
			BigDecimal excessBal = (fm.getExcessAmount().subtract(fm.getExcessAmtPaid()));
			BigDecimal unpaidPrincipal = (fm.getTotalPriSchd().subtract(fm.getTotalPriPaid()));
			writtenOffPrincipal = unpaidPrincipal.subtract(excessBal);
		}

		if (writtenOffPrincipal.compareTo(BigDecimal.ZERO) < 0) {
			writtenOffPrincipal = BigDecimal.ZERO;
		}

		return writtenOffPrincipal;
	}

	private BigDecimal amountOverDue(FinanceEnquiry fm) {
		BigDecimal amountOverdue;
		int odDays = Integer.parseInt(getOdDays(fm.getCurODDays()));
		if (odDays > 0) {
			BigDecimal installmentDue = getAmount(fm.getInstalmentDue());
			BigDecimal installmentPaid = getAmount(fm.getInstalmentPaid());
			BigDecimal bounceDue = getAmount(fm.getBounceDue());
			BigDecimal bouncePaid = getAmount(fm.getBouncePaid());
			BigDecimal penaltyDue = getAmount(fm.getLatePaymentPenaltyDue());
			BigDecimal penaltyPaid = getAmount(fm.getLatePaymentPenaltyPaid());
			BigDecimal excessAmount = getAmount(fm.getExcessAmount());
			BigDecimal excessAmountPaid = getAmount(fm.getExcessAmtPaid());
			amountOverdue = (installmentDue.subtract(installmentPaid)).add(bounceDue.subtract(bouncePaid)
					.add(penaltyDue.subtract(penaltyPaid).subtract(excessAmount.subtract(excessAmountPaid))));
		} else {
			amountOverdue = BigDecimal.ZERO;
		}
		if (amountOverdue.compareTo(BigDecimal.ZERO) < 0) {
			amountOverdue = BigDecimal.ZERO;
		}
		return amountOverdue;
	}

	private BigDecimal currentBalance(FinanceEnquiry fm) {
		int odDays = Integer.parseInt(getOdDays(fm.getCurODDays()));
		BigDecimal currentBalance;
		String closingstatus = StringUtils.trimToEmpty(fm.getClosingStatus());
		if (odDays > 0) {
			BigDecimal futureSchedulePrincipal = getAmount(fm.getFutureSchedulePrin());
			BigDecimal installmentDue = getAmount(fm.getInstalmentDue());
			BigDecimal installmentPaid = getAmount(fm.getInstalmentPaid());
			BigDecimal bounceDue = getAmount(fm.getBounceDue());
			BigDecimal bouncePaid = getAmount(fm.getBouncePaid());
			BigDecimal penaltyDue = getAmount(fm.getLatePaymentPenaltyDue());
			BigDecimal penaltyPaid = getAmount(fm.getLatePaymentPenaltyPaid());
			BigDecimal excessAmount = getAmount(fm.getExcessAmount());
			BigDecimal excessAmountPaid = getAmount(fm.getExcessAmtPaid());
			currentBalance = futureSchedulePrincipal
					.add(installmentDue.subtract(installmentPaid).add(bounceDue.subtract(bouncePaid)
							.add(penaltyDue.subtract(penaltyPaid).subtract(excessAmount.subtract(excessAmountPaid)))));

		} else {
			currentBalance = getAmount(fm.getFutureSchedulePrin());
		}
		if (currentBalance.compareTo(BigDecimal.ZERO) < 0) {
			currentBalance = BigDecimal.ZERO;
		}
		// and overdue amount should be 0
		if (StringUtils.equals("C", closingstatus)) {
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
				builder.append(",");
			}
		}

		if (custAddr.getCustAddrStreet() != null) {
			builder.append(StringUtils.trimToEmpty(custAddr.getCustAddrStreet()));
			if (custAddr.getCustFlatNbr() != null || custAddr.getCustAddrLine1() != null
					|| custAddr.getCustAddrLine2() != null) {
				builder.append(",");
			}
		}

		if (custAddr.getCustFlatNbr() != null) {
			builder.append(StringUtils.trimToEmpty(custAddr.getCustFlatNbr()));
			if (custAddr.getCustAddrLine1() != null || custAddr.getCustAddrLine2() != null) {
				builder.append(",");
			}
		}

		if (custAddr.getCustAddrLine1() != null) {
			builder.append(StringUtils.trimToEmpty(custAddr.getCustAddrLine1()));
			if (custAddr.getCustAddrLine2() != null) {
				builder.append(",");
			}
		}
		if (custAddr.getCustAddrCity() != null) {
			builder.append(StringUtils.trimToEmpty(custAddr.getCustAddrCity()));
			if (custAddr.getCustAddrCity() != null) {
				builder.append(",");
			}
		}

		if (custAddr.getCustAddrProvince() != null) {
			builder.append(StringUtils.trimToEmpty(custAddr.getCustAddrProvince()));
			if (custAddr.getCustAddrProvince() != null) {
				builder.append(",");
			}
		}

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
		return builder.toString();
	}

	private String getMemberDetails() {
		StringBuilder detais = new StringBuilder();
		detais.append(memberDetails.getMemberId()).append(",");
		detais.append(memberDetails.getMemberShortName()).append(",");
		detais.append("").append(",");
		detais.append(DateUtil.format(SysParamUtil.getAppDate(), DATE_FORMAT)).append(",");
		detais.append(memberDetails.getMemberPassword()).append(",");
		detais.append("").append(",");
		detais.append(memberDetails.getMemberCode());
		return detais.toString();
	}

	private void createHeading(Sheet sheet, int rowIndex) {
		Row row = sheet.createRow((int) rowIndex);
		Cell cella = row.createCell(0);
		cella.setCellValue("CIBIL HEADING");
		sheet.addMergedRegion(CellRangeAddress.valueOf("A1:E1"));
	}

	private void createFields(Workbook workbook, Sheet sheet, int rowIndex, String header) {
		Row row = sheet.createRow((int) rowIndex);
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
		Row row = sheet.createRow((int) rowIndex);
		int cellIndex = 0;
		for (String fieldName : header.split(",")) {
			createCell(row, cellIndex++, fieldName.trim());
		}
	}

	private void createCell(Row row, int index, String cellValue) {
		Cell cell = row.createCell(index);
		cell.setCellValue(cellValue);
	}

	private File createFile() throws Exception {
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
		builder.append("-");
		builder.append(DateUtil.getSysDate("ddMMYYYY"));
		builder.append("-");
		builder.append(DateUtil.getSysDate("Hms"));
		builder.append(".txt");
		reportName = new File(builder.toString());
		reportName.createNewFile();
		return reportName;
	}

	private File createXlsFile() throws Exception {
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
		builder.append("-");
		builder.append(DateUtil.getSysDate("ddMMYYYY"));
		builder.append("-");
		builder.append(DateUtil.getSysDate("Hms"));
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

		public void write() throws Exception {
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

		public void write() throws Exception {
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

		public void write() throws Exception {

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

		public void write() throws Exception {

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

		public void write() throws Exception {
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

			int odDays = Integer.parseInt(getOdDays(loan.getCurODDays()));
			BigDecimal currentBalance = BigDecimal.ZERO;
			String closingstatus = StringUtils.trimToEmpty(loan.getClosingStatus());

			if (odDays > 0) {
				BigDecimal futureSchedulePrincipal = getAmount(loan.getFutureSchedulePrin());
				BigDecimal installmentDue = getAmount(loan.getInstalmentDue());
				BigDecimal installmentPaid = getAmount(loan.getInstalmentPaid());
				BigDecimal bounceDue = getAmount(loan.getBounceDue());
				BigDecimal bouncePaid = getAmount(loan.getBouncePaid());
				BigDecimal penaltyDue = getAmount(loan.getLatePaymentPenaltyDue());
				BigDecimal penaltyPaid = getAmount(loan.getLatePaymentPenaltyPaid());
				BigDecimal excessAmount = getAmount(loan.getExcessAmount());
				BigDecimal excessAmountPaid = getAmount(loan.getExcessAmtPaid());

				currentBalance = futureSchedulePrincipal
						.add(installmentDue.subtract(installmentPaid).add(bounceDue.subtract(bouncePaid).add(
								penaltyDue.subtract(penaltyPaid).subtract(excessAmount.subtract(excessAmountPaid)))));
			} else {
				currentBalance = loan.getFutureSchedulePrin();
			}

			if (currentBalance != null) {
				if (currentBalance.compareTo(BigDecimal.ZERO) < 0) {
					currentBalance = BigDecimal.ZERO;
				}
			}

			// ### PSD --127340 20-06-2018 FOr cancelled loans current balnce
			// and overdue amount should be 0
			if (StringUtils.equals("C", closingstatus)) {
				currentBalance = BigDecimal.ZERO;
			}
			// ### PSD --127340 20-06-2018
			if (currentBalance != null) {
				if (currentBalance.compareTo(BigDecimal.ZERO) == 0 && loan.getLatestRpyDate() != null) {
					writeValue(builder, "10", DateUtil.format(loan.getLatestRpyDate(), DATE_FORMAT), "08");
				}
			}
			writeValue(builder, "11", SysParamUtil.getAppDate(DATE_FORMAT), "08");
			writeValue(builder, "12", loan.getFinAssetValue(), 9, "TL");
			writeValue(builder, "13", currentBalance, 9, "TL");

			BigDecimal amountOverdue = BigDecimal.ZERO;

			if (odDays > 0) {
				BigDecimal installmentDue = getAmount(loan.getInstalmentDue());
				BigDecimal installmentPaid = getAmount(loan.getInstalmentPaid());
				BigDecimal bounceDue = getAmount(loan.getBounceDue());
				BigDecimal bouncePaid = getAmount(loan.getBouncePaid());
				BigDecimal penaltyDue = getAmount(loan.getLatePaymentPenaltyDue());
				BigDecimal penaltyPaid = getAmount(loan.getLatePaymentPenaltyPaid());
				BigDecimal excessAmount = getAmount(loan.getExcessAmount());
				BigDecimal excessAmountPaid = getAmount(loan.getExcessAmtPaid());

				amountOverdue = (installmentDue.subtract(installmentPaid)).add(bounceDue.subtract(bouncePaid)
						.add(penaltyDue.subtract(penaltyPaid).subtract(excessAmount.subtract(excessAmountPaid))));

			} else {
				amountOverdue = BigDecimal.ZERO;
			}

			if (amountOverdue.compareTo(BigDecimal.ZERO) < 0) {
				amountOverdue = BigDecimal.ZERO;
			}

			// ### PSD --127340 20-06-2018 FOr cancelled loans current balnce
			// and overdue amount should be 0
			if (StringUtils.equals("C", closingstatus)) {
				amountOverdue = BigDecimal.ZERO;
			}
			// ### PSD --127340 20-06-2018

			writeValue(builder, "14", amountOverdue, 9, "TL");

			if (amountOverdue.compareTo(BigDecimal.ZERO) <= 0) {
				writeValue(builder, "15", "0", 3, "TL");
			} else {
				writeValue(builder, "15", getOdDays(loan.getCurODDays()), 3, "TL");
			}

			if (closingstatus.equals("W")) {
				// 02 = Written-off
				closingstatus = "02";
				writeValue(builder, "22", closingstatus, 2, "TL");
			}

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
		}
	}

	public class AccountSegmentHistory {
		private StringBuilder builder;
		private List<FinanceEnquiry> loans;

		public AccountSegmentHistory(StringBuilder builder, List<FinanceEnquiry> loans) {
			this.builder = builder;
			this.loans = loans;
		}

		public void write() throws Exception {
			int i = 0;
			for (FinanceEnquiry loan : loans) {

				if (++i > 35) {
					break;
				}
				writeValue(builder, "TH", "H".concat(StringUtils.leftPad(String.valueOf(i), 2, "0")), "03");

				writeValue(builder, "01", SysParamUtil.getAppDate(DATE_FORMAT), "08");
				writeValue(builder, "02", getOdDays(loan.getCurODDays()), "03");
				writeValue(builder, "03", loan.getAmountOverdue(), 9, "TH");
				writeValue(builder, "04", loan.getFinAssetValue(), 9, "TH");
				writeValue(builder, "07", loan.getCurrentBalance(), 9, "TH");
				writeValue(builder, "08", DateUtil.format(loan.getLatestRpyDate(), DATE_FORMAT), "08");
			}
		}
	}

	public class EndofSubjectSegment {
		private StringBuilder builder;

		public EndofSubjectSegment(StringBuilder writer) {
			this.builder = writer;
		}

		public void write() throws IOException {
			builder.append("ES02**");
		}
	}

	public class TrailerSegment {
		private BufferedWriter writer;

		public TrailerSegment(BufferedWriter writer) {
			this.writer = writer;
		}

		public void write() throws Exception {
			writer.write("TRLR");
		}
	}

	/**
	 * Append the value to StringBuilder
	 * 
	 * @param builder
	 *            The StringBuilder to append the value.
	 * @param value
	 *            The value to be append.
	 * 
	 */
	private void writeValue(StringBuilder builder, String value) {
		builder.append(value);
	}

	/**
	 * Append the fixed length tags to provided StringBuilder
	 * 
	 * @param builder
	 *            The StringBuilder to append the tags.
	 * @param fieldTag
	 *            Filed Tag
	 * @param value
	 *            Filed value
	 * @param size
	 *            length of the value
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
	 * @param builder
	 *            The StringBuilder tags.
	 * @param fieldTag
	 *            Filed Tag
	 * @param value
	 *            Filed Value
	 * @param maxLength
	 *            Max length of the tag
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

	private void writeValue(StringBuilder builder, String fieldTag, BigDecimal amount, int maxLength, String segment)
			throws IOException {
		if (amount == null) {
			return;
		}

		amount = amount.setScale(0, RoundingMode.HALF_DOWN);
		writeValue(builder, fieldTag, amount.toString(), maxLength, segment);
	}

	private void initlize() {
		memberDetails = cibilService.getMemberDetails(PennantConstants.PFF_CUSTCTG_INDIV);
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

	private BigDecimal getAmount(BigDecimal amount) {
		return amount == null ? BigDecimal.ZERO : amount;
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

	private void writeCustomerAddress(StringBuilder writer, CustomerAddres custAddr) throws IOException {
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
			remarks.append(", Sucess: ");
			remarks.append(successCount);
			remarks.append(", Failure: ");
			remarks.append(failedCount);

		} else {
			remarks.append("Completed successfully, total Records: ");
			remarks.append(totalRecords);
			remarks.append(", Sucess: ");
			remarks.append(successCount);
		}
		return remarks.toString();
	}

	@Autowired
	public void setCibilService(CIBILService cibilService) {
		this.cibilService = cibilService;
	}

}
