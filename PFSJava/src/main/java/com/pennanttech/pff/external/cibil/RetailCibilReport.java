package com.pennanttech.pff.external.cibil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.service.cibil.CIBILService;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.dataengine.util.EncryptionUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.util.DateUtil;
import com.pennanttech.service.AmazonS3Bucket;

public class RetailCibilReport {
	protected static final Logger logger = LoggerFactory.getLogger(RetailCibilReport.class);
	public static DataEngineStatus EXTRACT_STATUS = new DataEngineStatus("CIBIL_EXPORT_STATUS");

	private static final String DATE_FORMAT = "ddMMyyyy";
	private String CBIL_REPORT_PATH;
	private String CBIL_REPORT_MEMBER_SHORT_NAME;
	private String CBIL_REPORT_MEMBER_PASSWORD;
	private String CBIL_REPORT_MEMBER_ID;
	private String CBIL_REPORT_MEMBER_CODE;

	private long headerId;
	private long totalRecords;
	private long processedRecords;
	private long successCount;
	private long failedCount;

	private DataSource dataSource;
	private NamedParameterJdbcTemplate namedJdbcTemplate;

	@Autowired
	private CIBILService cibilService;

	public RetailCibilReport() {
		super();
	}

	public void generateReport() throws Exception {
		logger.debug(Literal.ENTERING);

		initlize();

		File cibilFile = createFile();

		final BufferedWriter writer = new BufferedWriter(new FileWriter(cibilFile));
		try {

			headerId = cibilService.logFileInfo(cibilFile.getName(), CBIL_REPORT_MEMBER_ID,
					CBIL_REPORT_MEMBER_SHORT_NAME, CBIL_REPORT_MEMBER_PASSWORD, CBIL_REPORT_PATH);

			/* Clear CIBIL_CUSTOMER_EXTRACT table */
			cibilService.deleteDetails();

			/* Prepare the data and store in CIBIL_CUSTOMER_EXTRACT table */
			totalRecords = cibilService.extractCustomers();
			EXTRACT_STATUS.setTotalRecords(totalRecords);

			new HeaderSegment(writer).write();

			StringBuilder sql = new StringBuilder();
			sql.append("select CUSTID, FINREFERENCE, OWNERSHIP From CIBIL_CUSTOMER_EXTRACT");
			namedJdbcTemplate.query(sql.toString(), new MapSqlParameterSource(), new RowCallbackHandler() {
				@Override
				public void processRow(ResultSet rs) throws SQLException {
					EXTRACT_STATUS.setProcessedRecords(processedRecords++);
					String finreference = rs.getString("FINREFERENCE");
					long customerId = rs.getLong("CUSTID");

					try {
						CustomerDetails customer = cibilService.getCustomerDetails(finreference, customerId);

						if (customer == null) {
							failedCount++;
							cibilService.logFileInfoException(headerId, String.valueOf(customerId), "Unable to fetch the details.");
							return;
						}

						StringBuilder builder = new StringBuilder();
						new NameSegment(builder, customer.getCustomer()).write();
						new IdentificationSegment(builder, customer.getCustomerDocumentsList()).write();
						new TelephoneSegment(builder, customer.getCustomerPhoneNumList()).write();
						new EmailContactSegment(builder, customer.getCustomerEMailList()).write();
						new AddressSegment(builder, customer.getAddressList()).write();
						new AccountSegment(builder, customer.getCustomerFinance()).write();
						List<FinanceEnquiry> list = new ArrayList<FinanceEnquiry>();
						list.add(customer.getCustomerFinance());
						// FIX ME BAJAJ ASKED TO REMOVE COMPLETELY
						// new AccountSegmentHistory(builder, list).write();
						new EndofSubjectSegment(builder).write();

						writer.write(builder.toString());

						EXTRACT_STATUS.setSuccessRecords(successCount++);
					} catch (Exception e) {
						EXTRACT_STATUS.setFailedRecords(failedCount++);
						cibilService.logFileInfoException(headerId, String.valueOf(customerId), e.getMessage());
						logger.error(Literal.EXCEPTION, e);
					}
				}
			});

			new TrailerSegment(writer).write();

		} catch (Exception e) {
			EXTRACT_STATUS.setStatus("F");
			logger.error(Literal.EXCEPTION, e);
		} finally {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		}

		if ("F".equals(EXTRACT_STATUS.getStatus())) {
			return;
		}

		// Move the File into S3 bucket
		try {
			EventProperties properties = cibilService.getEventProperties("CIBIL_REPORT", "S3");
			AmazonS3Bucket bucket = null;
			if (properties != null && properties.getStorageType().equals("S3")) {
				String accessKey = EncryptionUtil.decrypt(properties.getAccessKey());
				String secretKey = EncryptionUtil.decrypt(properties.getSecretKey());
				String bucketName = properties.getBucketName();

				bucket = new AmazonS3Bucket(properties.getRegionName(), bucketName, accessKey, secretKey);
				bucket.setSseAlgorithm(properties.getSseAlgorithm());

				bucket.putObject(cibilFile, properties.getPrefix());
			}

			EXTRACT_STATUS.setStatus("S");
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			EXTRACT_STATUS.setRemarks(e.getMessage());
			EXTRACT_STATUS.setStatus("F");
		}

		String remarks = updateRemarks();
		cibilService.updateFileStatus(headerId, EXTRACT_STATUS.getStatus(), totalRecords, processedRecords,
				successCount, failedCount, remarks);

		logger.debug(Literal.LEAVING);
	}

	private File createFile() throws Exception {
		logger.debug("Creating the file");
		File reportName = null;
		String reportLocation = CBIL_REPORT_PATH;
		String memberId = CBIL_REPORT_MEMBER_ID;

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

	/**
	 * The TUDF Segment marks the beginning of the Data Input File Format, and:
	 * <li>It is a Required segment.</li>
	 * <li>It is of a fixed size of 146 bytes.</li>
	 * <li>It occurs only once per update file.</li>
	 * <li>All the fields must be provided; otherwise, the entire data input
	 * file is rejected.</li>
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
			writeValue(builder, rightPad(CBIL_REPORT_MEMBER_ID, 30, ""));
			writeValue(builder, rightPad(CBIL_REPORT_MEMBER_SHORT_NAME, 16, ""));
			writeValue(builder, rightPad("", 2, ""));
			writeValue(builder, DateUtility.getAppDate(DATE_FORMAT));
			writeValue(builder, rightPad(CBIL_REPORT_MEMBER_PASSWORD, 30, ""));
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
	 * <li>It is variable in length and can be of a maximum size of 174
	 * bytes.</li>
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
	 * <li>This is a Required segment if at least one valid ID segment (with ID
	 * Type of 01, 02, 03, 04, or 06) is not present.</li>
	 * <li>It is variable in length and can be of a maximum size of 28
	 * bytes.</li>
	 * <li>This can occur maximum of 10 times per record.</li>
	 * <li>For accounts opened on/after June 1, 2007, at least one valid
	 * Telephone (PT) segment or at least one valid Identification (ID) segment
	 * (with ID Type of 01, 02, 03, 04, or 06) is required. If not provided, the
	 * record is rejected.</li>
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
	 * <li>It is variable in length and can be of a maximum size of 81
	 * bytes.</li>
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
	 * <li>It is variable in length and can be of a maximum size of 259
	 * bytes.</li>
	 * <li>This can occur maximum of 5 times per record.</li>
	 * <li>Any extra PA Segments after the 5th one will be rejected.</li>
	 * <li>At least one valid PA Segment is required. All invalid PA Segments
	 * will be rejected.</li>
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

				writeValue(builder, "06", address.getCustAddrProvince(), "02");
				writeValue(builder, "07", address.getCustAddrZIP(), 10, "PA");

				if (address.getCustAddrType() != null) {
					writeValue(builder, "08", address.getCustAddrType(), "02");
				} else {
					writeValue(builder, "08", "04", "02");
				}
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
			writeValue(builder, "01", StringUtils.rightPad(CBIL_REPORT_MEMBER_CODE, 10, ""), "10");
			writeValue(builder, "02", CBIL_REPORT_MEMBER_SHORT_NAME, 16, "TL");
			writeValue(builder, "03", StringUtils.trimToEmpty(loan.getFinReference()), 25, "TL");
			writeValue(builder, "04", StringUtils.trimToEmpty(loan.getFinType()), "02");
			writeValue(builder, "05", StringUtils.trimToEmpty(String.valueOf(loan.getOwnership())), "01");
			writeValue(builder, "08", DateUtil.format(loan.getFinApprovedDate(), DATE_FORMAT), "08");

			if (loan.getLatestRpyDate() != null) {
				writeValue(builder, "09", DateUtil.format(loan.getLatestRpyDate(), DATE_FORMAT), "08");
			}
			
			int odDays = Integer.parseInt(getOdDays(loan.getOdDays()));
			BigDecimal currentBalance = BigDecimal.ZERO;
			String closingstatus = StringUtils.trimToEmpty(loan.getClosingStatus());
			
			if (odDays != 0) {
				currentBalance = loan.getFutureSchedulePrin()
						.add(loan.getInstalmentDue().subtract(loan.getInstalmentPaid())
								.add(loan.getBounceDue().subtract(loan.getBouncePaid())
										.add(loan.getLatePaymentPenaltyDue().subtract(loan.getLatePaymentPenaltyPaid())
												.subtract(loan.getExcessAmount().subtract(loan.getExcessAmtPaid())))));
			} else {
				currentBalance = loan.getFutureSchedulePrin();
			}
			
			if (currentBalance.compareTo(BigDecimal.ZERO) < 0) {
				currentBalance = BigDecimal.ZERO;
			}
			
			//### PSD --127340 20-06-2018 FOr cancelled loans current balnce and overdue amount should be 0
			if (StringUtils.equals("C", closingstatus)) {
				currentBalance = BigDecimal.ZERO;
			}
			//### PSD --127340 20-06-2018
			
			
			if (currentBalance.compareTo(BigDecimal.ZERO) == 0 && loan.getLatestRpyDate() != null) {
				writeValue(builder, "10", DateUtil.format(loan.getLatestRpyDate(), DATE_FORMAT), "08");
			}
			
			writeValue(builder, "11", DateUtility.getAppDate(DATE_FORMAT), "08");
			writeValue(builder, "12", loan.getFinAssetValue(), 9, "TL");
			writeValue(builder, "13", currentBalance, 9, "TL");
			
			
			BigDecimal amountOverdue = BigDecimal.ZERO;
			
			if (odDays != 0) {
				amountOverdue = (loan.getInstalmentDue().subtract(loan.getInstalmentPaid()))
						.add(loan.getBounceDue().subtract(loan.getBouncePaid())
								.add(loan.getLatePaymentPenaltyDue().subtract(loan.getLatePaymentPenaltyPaid())
										.subtract(loan.getExcessAmount().subtract(loan.getExcessAmtPaid()))));
			} else {
				amountOverdue = BigDecimal.ZERO;
			}
			
			
			if (amountOverdue.compareTo(BigDecimal.ZERO) < 0) {
				amountOverdue = BigDecimal.ZERO;
			}
			
			//### PSD --127340 20-06-2018 FOr cancelled loans current balnce and overdue amount should be 0
			if (StringUtils.equals("C", closingstatus)) {
				amountOverdue = BigDecimal.ZERO;
			}
			//### PSD --127340 20-06-2018

			writeValue(builder, "14", amountOverdue, 9, "TL");
			
			if (amountOverdue.compareTo(BigDecimal.ZERO) <= 0) {
				writeValue(builder, "15", "0", 3, "TL");
			} else {
				writeValue(builder, "15", getOdDays(loan.getOdDays()), 3, "TL");
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

				writeValue(builder, "01", DateUtility.getAppDate(DATE_FORMAT), "08");
				writeValue(builder, "02", getOdDays(loan.getOdDays()), "03");
				writeValue(builder, "03", loan.getAmountOverdue(), 9, "TH");
				writeValue(builder, "04", loan.getFinAssetValue(), 9, "TH");
				writeValue(builder, "07", loan.getCurrentBalance(), 9, "TH");
				writeValue(builder, "08", DateUtil.format(loan.getLatestRpyDate(), DATE_FORMAT), "08");
				// writeValue(writer, "09", loan.getPaymentAmount(), 0);
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
		this.CBIL_REPORT_PATH = SysParamUtil.getValueAsString("CBIL_REPORT_PATH");
		this.CBIL_REPORT_MEMBER_ID = SysParamUtil.getValueAsString("CBIL_REPORT_MEMBER_ID");
		this.CBIL_REPORT_MEMBER_CODE = SysParamUtil.getValueAsString("CBIL_REPORT_MEMBER_CODE");
		this.CBIL_REPORT_MEMBER_SHORT_NAME = SysParamUtil.getValueAsString("CBIL_REPORT_MEMBER_SHORT_NAME");
		this.CBIL_REPORT_MEMBER_PASSWORD = SysParamUtil.getValueAsString("CBIL_REPORT_MEMBER_PASSWORD");

		totalRecords = 0;
		processedRecords = 0;
		successCount = 0;
		failedCount = 0;

		EXTRACT_STATUS.reset();
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

	
	
	private  void writeCustomerAddress(StringBuilder writer, CustomerAddres custAddr) throws IOException {
		StringBuilder builder = new StringBuilder();

		if (custAddr.getCustAddrHNbr() != null) {
			builder.append(StringUtils.trimToEmpty(custAddr.getCustAddrHNbr()));
			if (custAddr.getCustFlatNbr() != null || custAddr.getCustAddrStreet() != null
					|| custAddr.getCustAddrLine1() != null || custAddr.getCustAddrLine2() != null) {
				builder.append(" ");
			}
		}

		if (custAddr. getCustAddrStreet() != null) {
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

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.namedJdbcTemplate = new NamedParameterJdbcTemplate(this.dataSource);
	}
}
