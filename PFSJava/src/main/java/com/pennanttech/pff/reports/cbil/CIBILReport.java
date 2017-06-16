package com.pennanttech.pff.reports.cbil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.util.DateUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

public class CIBILReport {
	protected final static Logger		logger	= LoggerFactory.getLogger(CIBILReport.class);
	private String						CBIL_REPORT_PATH;
	private String						CBIL_REPORT_MEMBER_SHORT_NAME;
	private String						CBIL_REPORT_MEMBER_PASSWORD;
	private String						CBIL_REPORT_MEMBER_ID;
	private String						ADDRESS_TYPE_PERMANENT;
	private String						ADDRESS_TYPE_RESIDENCE;
	private String						ADDRESS_TYPE_OFFICE;
	private String						PHONE_TYPE_MOBILE;
	private String						PHONE_TYPE_HOME;
	private String						PHONE_TYPE_OFFICE;

	private DataSource					dataSource;
	private NamedParameterJdbcTemplate	namedJdbcTemplate;

	@Autowired
	private CustomerDetailsService		customerDetailsService;

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
			new CBILHeader(writer).write();

			//MapSqlParameterSource paramMap = new MapSqlParameterSource();

			StringBuilder sql = new StringBuilder();

			sql.append("select custid from customers where custid in (select distinct custid  from financemain)");

			namedJdbcTemplate.query(sql.toString(), new RowCallbackHandler() {

				@Override
				public void processRow(ResultSet rs) throws SQLException {
					CustomerDetails customer = customerDetailsService.getApprovedCustomerById(rs.getLong("custid"));
					try {
						new NameSegment(writer, customer.getCustomer()).write();
						new IdentificationSegment(writer, customer.getCustomerDocumentsList()).write();
						new TelephoneSegment(writer, customer.getCustomerPhoneNumList()).write();
						new AddressSegment(writer, customer.getAddressList()).write();
						//new AccountSegment(writer, new ArrayList<FinanceMain>());
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
			writer.write(StringUtils.rightPad("", 2, "")); //FIXME
			writer.write(DateUtility.getAppDate(DateFormat.ddMMYYYY));
			writer.write(StringUtils.rightPad(CBIL_REPORT_MEMBER_PASSWORD, 30, ""));
			writer.write("L");
			writer.write("00000");
			writer.write("C");
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
			writeValue(writer, "07", DateUtil.format(customer.getCustDOB(), DateFormat.ddMMYYYY));
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
				if (++i > 8) {
					break;
				}

				writeValue(writer, "ID", "I0" + i);

				if (PennantConstants.PANNUMBER.equals(document.getCustDocCategory())) {
					writeValue(writer, "01", "01");
				} else if (PennantConstants.PASSPORT.equals(document.getCustDocCategory())) {
					writeValue(writer, "02", "01");
				} else if (PennantConstants.CPRCODE.equals(document.getCustDocCategory())) {
					writeValue(writer, "06", "01");
				} else {
					continue;
				}

				writeValue(writer, "02", document.getCustDocTitle());
				writeValue(writer, "03", DateUtil.format(document.getCustDocIssuedOn(), DateFormat.ddMMYYYY));
				writeValue(writer, "04", DateUtil.format(document.getCustDocExpDate(), DateFormat.ddMMYYYY));
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

				if (PHONE_TYPE_MOBILE.equals(phoneNumber.getPhoneTypeCode())) {
					writeValue(writer, "01", phoneNumber.getPhoneNumber());
					writeValue(writer, "03", "01");
				} else if (PHONE_TYPE_HOME.equals(phoneNumber.getPhoneTypeCode())) {
					writeValue(writer, "01", phoneNumber.getPhoneNumber());
					writeValue(writer, "03", "02");
				} else if (PHONE_TYPE_OFFICE.equals(phoneNumber.getPhoneTypeCode())) {
					writeValue(writer, "01", phoneNumber.getPhoneNumber());
					writeValue(writer, "03", "03");
				} else {
					writeValue(writer, "01", phoneNumber.getPhoneNumber());
					writeValue(writer, "03", "00");
				}
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

				if (ADDRESS_TYPE_PERMANENT.equals(address.getCustAddrType())) {
					writeValue(writer, "08", "01");
				} else if (ADDRESS_TYPE_RESIDENCE.equals(address.getCustAddrType())) {
					writeValue(writer, "08", "02");
				} else if (ADDRESS_TYPE_OFFICE.equals(address.getCustAddrType())) {
					writeValue(writer, "08", "03");
				} else {
					writeValue(writer, "08", "04");
				}

				//Residence Code FIXME
			}
		}
	}

	public class AccountSegment {
		private BufferedWriter		writer;
		private List<FinanceMain>	loans;

		public AccountSegment(BufferedWriter writer, List<FinanceMain> loans) {
			this.writer = writer;
			this.loans = loans;
		}

		public void write() throws IOException {
			int i = 0;

			for (FinanceMain loan : loans) {
				writeValue(writer, "PA", "T" + StringUtils.leftPad(String.valueOf(i++), 2, "0"));
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

	private void initlize() {
		this.CBIL_REPORT_PATH = SysParamUtil.getValueAsString("CBIL_REPORT_PATH");
		this.CBIL_REPORT_MEMBER_ID = SysParamUtil.getValueAsString("CBIL_REPORT_MEMBER_ID");
		this.CBIL_REPORT_MEMBER_SHORT_NAME = SysParamUtil.getValueAsString("CBIL_REPORT_MEMBER_SHORT_NAME");
		this.CBIL_REPORT_MEMBER_PASSWORD = SysParamUtil.getValueAsString("CBIL_REPORT_MEMBER_PASSWORD");
		this.ADDRESS_TYPE_PERMANENT = SysParamUtil.getValueAsString("ADDRESS_TYPE_PERMANENT");
		this.ADDRESS_TYPE_RESIDENCE = SysParamUtil.getValueAsString("ADDRESS_TYPE_RESIDENCE");
		this.ADDRESS_TYPE_OFFICE = SysParamUtil.getValueAsString("ADDRESS_TYPE_OFFICE");
		this.PHONE_TYPE_MOBILE = SysParamUtil.getValueAsString("PHONE_TYPE_MOBILE");
		this.PHONE_TYPE_HOME = SysParamUtil.getValueAsString("PHONE_TYPE_HOME");
		this.PHONE_TYPE_OFFICE = SysParamUtil.getValueAsString("PHONE_TYPE_OFFICE");
	}

}
