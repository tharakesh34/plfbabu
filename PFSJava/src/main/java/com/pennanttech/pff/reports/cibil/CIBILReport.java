package com.pennanttech.pff.reports.cibil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.util.DateUtil;

public class CIBILReport {
	protected static final Logger		logger		= LoggerFactory.getLogger(CIBILReport.class);

	private static final String			DATE_FORMAT	= "ddMMYYYY";

	private String						CBIL_REPORT_PATH;
	private String						CBIL_REPORT_MEMBER_SHORT_NAME;
	private String						CBIL_REPORT_MEMBER_PASSWORD;
	private String						CBIL_REPORT_MEMBER_ID;

	private DataSource					dataSource;
	private NamedParameterJdbcTemplate	namedJdbcTemplate;

	@Autowired
	private CIBILService				cibilService;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.namedJdbcTemplate = new NamedParameterJdbcTemplate(this.dataSource);
	}

	public CIBILReport() {
		super();
	}

	public void generateReport() throws Exception {
		logger.debug(Literal.ENTERING);

		initlize();

		File reportName = createFile();

		final BufferedWriter writer = new BufferedWriter(new FileWriter(reportName));
		try {

			cibilService.logFileInfo(reportName.getName(), CBIL_REPORT_MEMBER_ID, CBIL_REPORT_MEMBER_SHORT_NAME,
					CBIL_REPORT_MEMBER_PASSWORD);

			cibilService.deleteDetails();

			cibilService.extractCustomers();

			new CBILHeader(writer).write();

			StringBuilder sql = new StringBuilder();

			sql.append("select CUSTID, FINREFERENCE, OWNERSHIP  From CIBIL_CUSTOMER_EXTRACT");

			namedJdbcTemplate.query(sql.toString(), new MapSqlParameterSource(), new RowCallbackHandler() {

				@Override
				public void processRow(ResultSet rs) throws SQLException {
					String finreference = rs.getString("FINREFERENCE");
					long customerId = rs.getLong("CUSTID");

					try {
						CustomerDetails customer = cibilService.getCustomerDetails(finreference, customerId);
						new NameSegment(writer, customer.getCustomer()).write();
						new IdentificationSegment(writer, customer.getCustomerDocumentsList()).write();
						new TelephoneSegment(writer, customer.getCustomerPhoneNumList()).write();
						new AddressSegment(writer, customer.getAddressList()).write();
						new AccountSegment(writer, customer.getCustomerFinance()).write();
						List<FinanceEnquiry> list = new ArrayList<FinanceEnquiry>();
						list.add(customer.getCustomerFinance());
						new AccountSegmentHistory(writer, list).write();
						new EndofSubjectSegment(writer).write();
					} catch (IOException e) {
						logger.error(Literal.EXCEPTION, e);
					}
				}
			});

			new TrailerSegment(writer).write();

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private File createFile() throws Exception {
		logger.debug("Creating the ");
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

	public class CBILHeader {
		private BufferedWriter	writer;

		public CBILHeader(BufferedWriter writer) {
			this.writer = writer;
		}

		public void write() throws IOException {
			writer.write("TUDF");
			writer.write("12");
			writer.write(StringUtils.rightPad(CBIL_REPORT_MEMBER_ID, 30, ""));
			writer.write(StringUtils.rightPad(CBIL_REPORT_MEMBER_SHORT_NAME, 16, ""));
			writer.write(StringUtils.rightPad("", 2, ""));
			writer.write(DateUtility.getAppDate(DATE_FORMAT));
			writer.write(StringUtils.rightPad(CBIL_REPORT_MEMBER_PASSWORD, 30, ""));
			writer.write("L");
			writer.write("00000");
			writer.write("");
		}
	}

	public class NameSegment {
		private BufferedWriter	writer;
		private Customer		customer;

		public NameSegment(BufferedWriter writer, Customer customer) {
			this.writer = writer;
			this.customer = customer;
		}

		public void write() throws IOException {
			writeValue(writer, "PN", "N01");
			writeValue(writer, "01", customer.getCustShrtName());
			writeValue(writer, "07", DateUtil.format(customer.getCustDOB(), DATE_FORMAT));
			writeValue(writer, "01", customer.getCustGenderCode());

			if ("M".equals(customer.getCustGenderCode())) {
				writeValue(writer, "08", "2");
			} else if ("F".equals(customer.getCustGenderCode())) {
				writeValue(writer, "08", "1");
			} else {
				writeValue(writer, "08", "3");
			}
		}
	}

	public class IdentificationSegment {
		private BufferedWriter			writer;
		private List<CustomerDocument>	documents;

		public IdentificationSegment(BufferedWriter writer, List<CustomerDocument> documents) {
			this.writer = writer;
			this.documents = documents;
		}

		public void write() throws IOException {
			int i = 0;
			for (CustomerDocument document : documents) {

				String docCode = document.getCustDocCategory();
				if (docCode == null || "07".equals(docCode) || "08".equals(docCode)) {
					continue;
				}

				if (++i > 8) {
					break;
				}

				writeValue(writer, "ID", "I0" + i);
				writeValue(writer, "01", document.getCustDocCategory());
				writeValue(writer, "02", document.getCustDocTitle());

				if (document.getCustDocIssuedOn() != null) {
					writeValue(writer, "03", DateUtil.format(document.getCustDocIssuedOn(), DATE_FORMAT));
				}

				if (document.getCustDocExpDate() != null) {
					writeValue(writer, "04", DateUtil.format(document.getCustDocExpDate(), DATE_FORMAT));
				}

			}

		}
	}

	public class TelephoneSegment {
		private BufferedWriter				writer;
		private List<CustomerPhoneNumber>	phoneNumbers;

		public TelephoneSegment(BufferedWriter writer, List<CustomerPhoneNumber> phoneNumbers) {
			this.writer = writer;
			this.phoneNumbers = phoneNumbers;
		}

		public void write() throws IOException {
			int i = 0;
			for (CustomerPhoneNumber phoneNumber : phoneNumbers) {
				if (++i > 10) {
					break;
				}

				writeValue(writer, "PT", "T" + StringUtils.leftPad(String.valueOf(i), 2, "0"));
				writeValue(writer, "01", phoneNumber.getPhoneNumber());
				writeValue(writer, "03", phoneNumber.getPhoneTypeCode());

			}
		}
	}

	public class EmailContactSegment {
		private BufferedWriter		writer;
		private List<CustomerEMail>	emails;

		public EmailContactSegment(BufferedWriter writer, List<CustomerEMail> emails) {
			this.writer = writer;
			this.emails = emails;
		}

		public void write() throws IOException {
			int i = 0;

			for (CustomerEMail email : emails) {
				if (++i > 10) {
					break;
				}

				if (StringUtils.isNotEmpty(email.getCustEMail())) {
					writeValue(writer, "EC", "C" + StringUtils.leftPad(String.valueOf(i), 2, "0"));
					writeValue(writer, "", email.getCustEMail());
				}

			}
		}
	}

	public class AddressSegment {
		private BufferedWriter			writer;
		private List<CustomerAddres>	addresses;

		public AddressSegment(BufferedWriter writer, List<CustomerAddres> addresses) {
			this.writer = writer;
			this.addresses = addresses;
		}

		public void write() throws IOException {
			int i = 0;

			for (CustomerAddres address : addresses) {
				if (++i > 5) {
					break;
				}

				writeValue(writer, "PA", "A" + StringUtils.leftPad(String.valueOf(i), 2, "0"));
				writeValue(writer, "01", StringUtils.substring(address.getCustAddrHNbr(), 0, 39));
				writeValue(writer, "02", StringUtils.substring(address.getCustFlatNbr(), 0, 39));
				writeValue(writer, "03", StringUtils.substring(address.getCustAddrStreet(), 0, 39));
				writeValue(writer, "04", StringUtils.substring(address.getCustAddrLine1(), 0, 39));
				writeValue(writer, "05", StringUtils.substring(address.getCustAddrLine2(), 0, 39));
				writeValue(writer, "06", address.getCustAddrProvince());
				writeValue(writer, "07", address.getCustAddrZIP());
				writeValue(writer, "08", address.getCustAddrType());
				// Residence Code FIXME
			}
		}
	}

	public class AccountSegment {
		private BufferedWriter	writer;
		private FinanceEnquiry	loan;

		public AccountSegment(BufferedWriter writer, FinanceEnquiry loan) {
			this.writer = writer;
			this.loan = loan;
		}

		public void write() throws IOException {

			writeValue(writer, "TL", "T001");
			writeValue(writer, "01", StringUtils.rightPad(CBIL_REPORT_MEMBER_ID, 30, ""));
			writeValue(writer, "02", StringUtils.rightPad(CBIL_REPORT_MEMBER_SHORT_NAME, 16, ""));
			writeValue(writer, "03", StringUtils.trimToEmpty(loan.getFinReference()));
			writeValue(writer, "04", StringUtils.trimToEmpty(loan.getFinType()));
			writeValue(writer, "05", StringUtils.trimToEmpty(String.valueOf(loan.getOwnership())));
			writeValue(writer, "08", DateUtil.format(loan.getFinStartDate(), DATE_FORMAT));
			writeValue(writer, "09", DateUtil.format(loan.getLatestRpyDate(), DATE_FORMAT));

			BigDecimal CURRENT_BALANCE = loan.getCurrentBalance();

			if (CURRENT_BALANCE == null) {
				CURRENT_BALANCE = BigDecimal.ZERO;
			}

			if (CURRENT_BALANCE.compareTo(BigDecimal.ZERO) <= 0) {
				writeValue(writer, "10", DateUtil.format(loan.getLatestRpyDate(), DATE_FORMAT));
			}

			writeValue(writer, "11", DateUtility.getAppDate(DATE_FORMAT));
			writeValue(writer, "12", loan.getFinAssetValue());
			writeValue(writer, "13", loan.getCurrentBalance());
			writeValue(writer, "14", loan.getAmountOverdue());
			writeValue(writer, "15", getOdDays(loan.getOdDays()));

			String closingstatus = StringUtils.trimToEmpty(loan.getClosingStatus());
			if (closingstatus.equals("M")) {
				//03 = Settled
				closingstatus = "03";

			} else if (closingstatus.equals("W")) {
				// 02 = Written-off
				closingstatus = "02";
			}
			writeValue(writer, "22", closingstatus);

			// writeValue(writer, "26", StringUtils.trimToEmpty(rs.getString(""))); // FIXME

			BigDecimal collateralValue = loan.getCollateralValue();

			if (collateralValue == null || collateralValue == BigDecimal.ZERO) {
				writeValue(writer, "34", "00");
			} else {
				writeValue(writer, "34", collateralValue);
			}

			writeValue(writer, "35", StringUtils.trimToEmpty(loan.getCollateralType()));

			BigDecimal repayProfit = loan.getRepayProfitRate();

			if (repayProfit == null) {
				repayProfit = BigDecimal.ZERO;
			}

			String repayProfitRate = repayProfit.toString();

			if (repayProfitRate.contains(".")) {
				String rate[] = repayProfitRate.split("\\.");
				String integralPart = String.valueOf(rate[0]);
				String decimalPart = rate[1];

				if (decimalPart.length() > 2) {
					decimalPart = (String) decimalPart.subSequence(0, 3);
				}

				writeValue(writer, "38", integralPart + "." + decimalPart);
			} else {
				writeValue(writer, "38", StringUtils.trimToEmpty(repayProfitRate));
			}

			writeValue(writer, "39", loan.getNumberOfTerms());
			writeValue(writer, "40", loan.getFirstRepay());
			writeValue(writer, "41", loan.getWrittenOffAmount());
			writeValue(writer, "42", loan.getWrittenOffPrincipal());
			writeValue(writer, "43", loan.getSettlementAmount());

			String repayFreq = StringUtils.trimToEmpty(loan.getRepayFrq());

			repayFreq = repayFreq.substring(0, 1);

			if (repayFreq.equals("W")) {
				repayFreq = "01";
			} else if (repayFreq.equals("M")) {
				repayFreq = "03";
			} else if (repayFreq.equals("F")) {
				repayFreq = "02";
			} else if (repayFreq.equals("Q")) {
				repayFreq = "04";
			}

			writeValue(writer, "44", repayFreq);
			writeValue(writer, "48", "G");
			writeValue(writer, "49", "M");

		}

	}

	public class AccountSegmentHistory {
		private BufferedWriter			writer;
		private List<FinanceEnquiry>	loans;

		public AccountSegmentHistory(BufferedWriter writer, List<FinanceEnquiry> loans) {
			this.writer = writer;
			this.loans = loans;
		}

		public void write() throws IOException {
			int i = 0;
			for (FinanceEnquiry loan : loans) {
				writeValue(writer, "TH", StringUtils.leftPad(String.valueOf(i++), 2, "0"));
				writeValue(writer, "01", DateUtility.getAppDate(DATE_FORMAT));
				writeValue(writer, "02", getOdDays(loan.getOdDays()));
				writeValue(writer, "03", loan.getAmountOverdue());
				writeValue(writer, "04", loan.getFinAssetValue());
				writeValue(writer, "07", loan.getCurrentBalance());
				writeValue(writer, "08", DateUtil.format(loan.getLatestRpyDate(), DATE_FORMAT));
				writeValue(writer, "09", loan.getPaymentAmount());
			}

		}
	}

	public class EndofSubjectSegment {
		private BufferedWriter	writer;

		public EndofSubjectSegment(BufferedWriter writer) {
			this.writer = writer;
		}

		public void write() throws IOException {
			writer.write("ES02**");
		}
	}

	public class TrailerSegment {
		private BufferedWriter	writer;

		public TrailerSegment(BufferedWriter writer) {
			this.writer = writer;
		}

		public void write() throws IOException {
			writer.write("TRLR");
		}
	}

	private void writeValue(BufferedWriter writer, String fieldTag, String value) throws IOException {
		int length = 0;
		if (StringUtils.isBlank(value)) {
			return;
		}

		length = value.length();

		writer.write(fieldTag + String.valueOf(length) + value);
	}

	private void writeValue(BufferedWriter writer, String fieldTag, BigDecimal value) throws IOException {
		int length = 0;
		if (value == null) {
			return;
		}

		length = value.toString().length();

		writer.write(fieldTag + String.valueOf(length) + value);
	}

	private void writeValue(BufferedWriter writer, String fieldTag, int value) throws IOException {
		int length = 0;

		length = String.valueOf(value).length();

		writer.write(fieldTag + String.valueOf(length) + value);
	}

	private void initlize() {
		this.CBIL_REPORT_PATH = SysParamUtil.getValueAsString("CBIL_REPORT_PATH");
		this.CBIL_REPORT_MEMBER_ID = SysParamUtil.getValueAsString("CBIL_REPORT_MEMBER_ID");
		this.CBIL_REPORT_MEMBER_SHORT_NAME = SysParamUtil.getValueAsString("CBIL_REPORT_MEMBER_SHORT_NAME");
		this.CBIL_REPORT_MEMBER_PASSWORD = SysParamUtil.getValueAsString("CBIL_REPORT_MEMBER_PASSWORD");
	}

	private int getOdDays(int odDays) {
		if (odDays > 900) {
			odDays = 900;
		}
		return odDays;
	}
}
