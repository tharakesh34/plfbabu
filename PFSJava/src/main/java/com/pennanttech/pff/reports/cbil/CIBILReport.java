package com.pennanttech.pff.reports.cbil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.util.DateUtil;

public class CIBILReport {
	protected final static Logger	logger	= LoggerFactory.getLogger(CIBILReport.class);
	private String	CBIL_REPORT_PATH;
	private String	CBIL_REPORT_MEMBER_ID;
	private String	ADDRESS_TYPE_PERMANENT;
	private String	ADDRESS_TYPE_RESIDENCE;
	private String	ADDRESS_TYPE_OFFICE;
	private String	PHONE_TYPE_MOBILE;
	private String	PHONE_TYPE_HOME;
	private String	PHONE_TYPE_OFFICE;

	public CIBILReport() {
		super();
	}

	public void generateReport() throws Exception {
		logger.debug(Literal.ENTERING);
		
		initlize();
		
		
		File reportName = createFile();

		BufferedWriter writer = new BufferedWriter(new FileWriter(reportName));

		try {
			new CBILHeader(writer).write();
			new NameSegment(writer, new Customer()).write();
			new IdentificationSegment(writer, new ArrayList<CustomerDocument>());
			new IdentificationSegment(writer, new ArrayList<CustomerDocument>());
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

	private void initlize() {
		CBIL_REPORT_PATH = SysParamUtil.getValueAsString("CBIL_REPORT_PATH");
		CBIL_REPORT_MEMBER_ID = SysParamUtil.getValueAsString("CBIL_REPORT_MEMBER_ID");
		ADDRESS_TYPE_PERMANENT = SysParamUtil.getValueAsString("ADDRESS_TYPE_PERMANENT");
		ADDRESS_TYPE_RESIDENCE = SysParamUtil.getValueAsString("ADDRESS_TYPE_RESIDENCE");
		ADDRESS_TYPE_OFFICE = SysParamUtil.getValueAsString("ADDRESS_TYPE_OFFICE");
		PHONE_TYPE_MOBILE = SysParamUtil.getValueAsString("PHONE_TYPE_MOBILE");
		PHONE_TYPE_HOME = SysParamUtil.getValueAsString("PHONE_TYPE_HOME");
		PHONE_TYPE_OFFICE = SysParamUtil.getValueAsString("PHONE_TYPE_OFFICE");
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
			String userId = SysParamUtil.getValueAsString("CBIL_PROCESSOR_USER_ID");
			String shortNmae = SysParamUtil.getValueAsString("CBIL_PROCESSOR_USER_SHORT_NAME");
			String password = SysParamUtil.getValueAsString("CBIL_REPORTING_PASSWORD");

			writer.write(StringUtils.rightPad("TUDF", 4, ""));
			writer.write(StringUtils.rightPad("12", 2, ""));
			writer.write(StringUtils.rightPad(userId, 30, ""));
			writer.write(StringUtils.rightPad(shortNmae, 16, ""));
			writer.write(StringUtils.rightPad("", 2, ""));
			writer.write(DateUtil.getSysDate("ddMMYYYY"));
			writer.write(StringUtils.rightPad(password, 20, ""));
			writer.write(StringUtils.rightPad("L", 1, ""));
			writer.write(StringUtils.rightPad("", 48, ""));
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
			writeValue(writer, "07", DateUtil.format(customer.getCustDOB(), "ddMMYYYY"));
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
				writeValue(writer, "02", document.getCustDocTitle());
				writeValue(writer, "03", DateUtil.format(document.getCustDocIssuedOn(), "ddMMYYYY"));
				writeValue(writer, "04", DateUtil.format(document.getCustDocExpDate(), "ddMMYYYY"));
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
		private BufferedWriter			writer;
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

	private void writeValue(BufferedWriter writer, String fieldTag, String value) throws IOException {
		int length = 0;
		if (StringUtils.isBlank(value)) {
			return;
		}

		length = value.length();

		writer.write(fieldTag + String.valueOf(length) + value);
	}

}
