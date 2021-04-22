package com.pennanttech.pff.external.cibil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.northconcepts.datapipeline.core.Record;
import com.northconcepts.datapipeline.csv.CSVWriter;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.MasterDefUtil;
import com.pennant.app.util.MasterDefUtil.DocType;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.model.finance.FinODDetails;
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

public class CorporateCibilReport extends BasicDao<Object> {
	protected static final Logger logger = LoggerFactory.getLogger(CorporateCibilReport.class);
	public static DataEngineStatus EXTRACT_STATUS = StepUtil.CIBIL_EXTRACT_CORPORATE;
	private static final String DATE_FORMAT = "ddMMyyyy";
	private CibilFileInfo fileInfo;
	private CibilMemberDetail memberDetails;

	private long headerId;
	private long totalRecords;
	private long processedRecords;
	private long successCount;
	private long failedCount;
	private long borrowerCount;
	private long creditfacilityCount;

	@Autowired
	private CIBILService cibilService;

	public CorporateCibilReport() {
		super();
	}

	public void generateReport() throws Exception {
		logger.debug(Literal.ENTERING);

		initlize();

		File cibilFile = createFile();

		final CSVWriter writer = new CSVWriter(new BufferedWriter(new FileWriter(cibilFile)));
		writer.setFieldNamesInFirstRow(false);
		writer.setFieldSeparator('|');
		writer.open();
		try {

			fileInfo = new CibilFileInfo();
			fileInfo.setFileName(cibilFile.getName());
			fileInfo.setCibilMemberDetail(memberDetails);
			fileInfo.setTotalRecords(totalRecords);
			fileInfo.setProcessedRecords(processedRecords);
			fileInfo.setSuccessCount(successCount);
			fileInfo.setFailedCount(failedCount);
			fileInfo.setRemarks(updateRemarks());

			cibilService.logFileInfo(fileInfo);
			headerId = fileInfo.getId();

			/* Clear CIBIL_CUSTOMER_EXTRACT table */
			cibilService.deleteDetails();

			/* Prepare the data and store in CIBIL_CUSTOMER_EXTRACT table */
			cibilService.extractCustomers(PennantConstants.PFF_CUSTCTG_CORP);

			totalRecords = cibilService.getotalRecords(PennantConstants.PFF_CUSTCTG_CORP);
			EXTRACT_STATUS.setTotalRecords(totalRecords);

			// updating the Cibil_File_Info
			cibilService.updateFileStatus(fileInfo);

			if (totalRecords > 0) {
				new HeaderSegment(writer).write();
			}

			String sql = "select distinct custid, finreference from cibil_customer_extract where segment_type = :segment_type";
			MapSqlParameterSource parameterSource = new MapSqlParameterSource();
			parameterSource.addValue("segment_type", PennantConstants.PFF_CUSTCTG_CORP);
			jdbcTemplate.query(sql, parameterSource, new RowCallbackHandler() {
				@Override
				public void processRow(ResultSet rs) throws SQLException {
					EXTRACT_STATUS.setProcessedRecords(processedRecords++);
					long customerId = rs.getLong("custid");
					String finreference = rs.getString("finreference");

					CustomerDetails customer;
					try {
						customer = cibilService.getCustomerDetails(customerId, null, PennantConstants.PFF_CUSTCTG_CORP);

						if (customer == null) {
							failedCount++;
							cibilService.logFileInfoException(headerId, String.valueOf(customerId),
									"Unable to fetch the details.");
							return;
						}

						new BorrowerSegment(writer, customer, finreference).write();
						borrowerCount++;

						EXTRACT_STATUS.setSuccessRecords(successCount++);

					} catch (Exception e) {
						EXTRACT_STATUS.setFailedRecords(failedCount++);
						cibilService.logFileInfoException(headerId, String.valueOf(customerId), e.getMessage());
						logger.error(Literal.EXCEPTION, e);
					}
				}
			});

			new FileClosureSegment(writer).write();

		} catch (Exception e) {
			EXTRACT_STATUS.setStatus("F");
			logger.error(Literal.EXCEPTION, e);
		} finally {
			writer.close();
		}

		if ("F".equals(EXTRACT_STATUS.getStatus())) {
			return;
		}

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
		fileInfo.setStatus(EXTRACT_STATUS.getStatus());
		fileInfo.setTotalRecords(totalRecords);
		fileInfo.setProcessedRecords(processedRecords);
		fileInfo.setFailedCount(failedCount);
		fileInfo.setSuccessCount(successCount);
		fileInfo.setRemarks(remarks);

		cibilService.updateFileStatus(fileInfo);

		logger.debug(Literal.LEAVING);
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
		builder.append("_");
		builder.append(DateUtil.getSysDate("ddMMYYYY"));
		//builder.append("_");
		//builder.append(DateUtil.getSysDate("Hmmss"));
		builder.append(".txt");
		reportName = new File(builder.toString());

		reportName.createNewFile();

		return reportName;
	}

	public class HeaderSegment {
		private CSVWriter writer;

		public HeaderSegment(CSVWriter writer) {
			this.writer = writer;
		}

		public void write() throws Exception {
			Record record = new Record();
			/* Segment Identifier */
			addField(record, "HD");

			/* Member ID */
			addField(record, memberDetails.getMemberId());

			/* Previous Member ID */
			addField(record, "");

			/* Date of Creation & Certification of Input File */
			addField(record, DateUtility.getAppDate(DATE_FORMAT));

			/* Reporting / Cycle Date */
			addField(record, DateUtility.getAppDate(DATE_FORMAT));

			/* Information Type */
			addField(record, "01");

			/* Filler */
			addField(record, "");

			writer.write(record);
		}
	}

	public class BorrowerSegment {
		private CSVWriter writer;
		private CustomerDetails customerDetails;
		private String finReference;

		public BorrowerSegment(CSVWriter writer, CustomerDetails customerDetails, String finReference) {
			this.writer = writer;
			this.customerDetails = customerDetails;
			this.finReference = finReference;
		}

		public void write() throws Exception {
			Customer customer = customerDetails.getCustomer();
			List<CustomerDocument> documents = customerDetails.getCustomerDocumentsList();

			Record record = new Record();

			/* Segment Identifier */
			addField(record, "BS");

			/* Member Branch Code */
			addField(record, customer.getCustDftBranch());

			/* Previous Member Branch Code */
			addField(record, "");

			/* Borrower's Name */
			addField(record, customer.getCustShrtName());

			/* Borrower Short Name */
			addField(record, "");

			/* Company Registration Number */
			addField(record, customer.getCustTradeLicenceNum());

			/* Date of Incorporation */
			addField(record, DateUtil.format(customer.getCustDOB(), DATE_FORMAT));

			String pan = null;
			String corporateIdNum = null;
			String taxIdNumber = null;
			String serviceTax = null;

			String docCategory;
			for (CustomerDocument document : documents) {
				docCategory = document.getCustDocCategory();

				if (StringUtils.equals(docCategory, MasterDefUtil.getDocCode(DocType.PAN))) {
					pan = document.getCustDocTitle();
				} else if (StringUtils.equals(docCategory, MasterDefUtil.getDocCode(DocType.CORPORATE_ID_NUMBER))) {
					corporateIdNum = document.getCustDocTitle();
				} else if (StringUtils.equals(docCategory,
						MasterDefUtil.getDocCode(DocType.TAX_IDENTIFICATION_NUMBER))) {
					taxIdNumber = document.getCustDocTitle();
				} else if (StringUtils.equals(docCategory, MasterDefUtil.getDocCode(DocType.SERVICE_TAX_REG_NO))) {
					serviceTax = document.getCustDocTitle();
				}
			}

			/* pan */
			if (pan == null) {
				pan = customer.getCustCRCPR();
			}
			addField(record, pan);

			/* CIN */
			addField(record, corporateIdNum);

			/* TIN */
			addField(record, taxIdNumber);

			/* Service Tax # */
			addField(record, serviceTax);

			/* Other ID */
			addField(record, "");

			/* Borrower‟s Legal Constitution */
			String value = StringUtils.trimToNull(customer.getLegalconstitution());
			if (value == null) {
				value = "99";
			}
			addField(record, value);

			/* Business Category */
			value = StringUtils.trimToNull(customer.getBusinesscategory());
			if (value == null) {
				value = "07";
			}
			addField(record, value);

			/* Business/ Industry Type */
			value = StringUtils.trimToNull(customer.getCustIndustry());
			if (value == null) {
				value = "11";
			}
			addField(record, value);

			/* Class of Activity 1 */
			addField(record, "");

			/* Class of Activity 2 */
			addField(record, "");

			/* Class of Activity 3 */
			addField(record, "");

			/* SIC_Code */
			addField(record, "");

			/* Sales Figure */
			addField(record, "");

			/* Financial Year */
			addField(record, "");

			/* Number of Employees */
			addField(record, "");

			/* Credit Rating */
			addField(record, "");

			/* Assessment Agency/Authority */
			addField(record, "");

			/* Credit Rating As On */
			addField(record, "");

			/* Credit Rating Expiry Date */
			addField(record, "");

			/* Filler */
			addField(record, "");
			writer.write(record);

			List<CustomerAddres> addres = customerDetails.getAddressList();
			if (addres == null || addres.isEmpty()) {
				throw new Exception("Address details are not available.");
			}

			new AddressSegment(writer, customerDetails).write();
			new RelationshipSegment(writer, customerDetails).write();
			new CreditFacilitySegment(writer, customerDetails, finReference).write();
		}

	}

	public class AddressSegment {
		private CSVWriter writer;
		private CustomerDetails customerDetails;

		public AddressSegment(CSVWriter writer, CustomerDetails customerDetails) {
			this.writer = writer;
			this.customerDetails = customerDetails;
		}

		public void write() throws Exception {
			List<CustomerAddres> addres = customerDetails.getAddressList();
			List<CustomerPhoneNumber> phoneList = customerDetails.getCustomerPhoneNumList();

			if (phoneList == null) {
				phoneList = new ArrayList<>();
			}

			for (CustomerAddres address : addres) {
				Record record = new Record();

				/* Segment Identifier */
				addField(record, "AS");

				/* Borrower Office Location Type */
				String addressType = StringUtils.trimToNull(address.getCustAddrType());

				if (addressType == null) {
					addressType = "05";
				}
				addField(record, addressType);

				/* Borrower Office DUNS Number */
				addField(record, "999999999");

				setAddressDetails(address, phoneList, record, false);

				writer.write(record);
			}

		}
	}

	public class RelationshipSegment {
		private CSVWriter writer;
		CustomerDetails customerDetails;
		Customer customer;
		CustomerDocument custDocument;
		List<CustomerAddres> customerAddr;
		List<CustomerPhoneNumber> customerPhoneNumber;

		public RelationshipSegment(CSVWriter writer, CustomerDetails customerDetails) {
			super();
			this.writer = writer;
			this.customerDetails = customerDetails;
		}

		public void write() throws Exception {
			customer = customerDetails.getCustomer();
			custDocument = customerDetails.getCustomerDocument();
			customerAddr = customerDetails.getAddressList();
			customerPhoneNumber = customerDetails.getCustomerPhoneNumList();

			Record record = new Record();

			/* Segment Identifier */
			addField(record, "RS");

			/* Relationship DUNS Number */
			addField(record, "999999999");

			/* Related Type */
			int relatedType = 0;

			if ("IN".equals(customer.getCustCOB())) {
				relatedType = 1;
			} else {
				relatedType = 3;
			}

			addField(record, String.valueOf(relatedType));

			/* Relationship */
			String relationShip = customer.getCustRelation();
			if (relationShip == null) {
				relationShip = "60";
			}
			addField(record, relationShip);

			/* Business Entity Name */
			if (relatedType == 1 || relatedType == 3) {
				addField(record, customer.getCustShrtName());
			} else {
				addField(record, "");
			}

			/* Business Category */
			if (relatedType == 1 || relatedType == 3) {
				addField(record, customer.getBusinesscategory());
			} else {
				addField(record, "07");
			}

			/* Business / Industry Type */
			if (relatedType == 1 || relatedType == 3) {
				if (customer.getCustIndustry() != null) {
					addField(record, customer.getCustIndustry());
				} else {
					addField(record, "11");
				}

			} else {
				addField(record, "11");
			}

			/* Individual Name prefix */
			if (relatedType == 2 || relatedType == 4) {
				addField(record, customer.getCustSalutationCode());
			} else {
				addField(record, "");
			}

			/* Full Name */
			StringBuffer name = new StringBuffer();

			String firstName = StringUtils.trimToNull(customer.getCustFName());
			String middleName = StringUtils.trimToNull(customer.getCustFName());
			String lastName = StringUtils.trimToNull(customer.getCustFName());

			if (firstName != null) {
				name.append(firstName);
			} else if (middleName != null) {
				name.append(" ");
				name.append(middleName);
			} else if (lastName != null) {
				name.append(" ");
				name.append(lastName);
			}

			if (relatedType == 2 || relatedType == 4) {
				addField(record, name.toString());
			} else {
				addField(record, "");
			}

			/* Gender */

			if (relatedType == 2 || relatedType == 4) {
				if ("M".equals(customer.getCustGenderCode())) {
					addField(record, "01");
				}
				if ("F".equals(customer.getCustGenderCode())) {
					addField(record, "02");
				} else {
					addField(record, "");

				}
			} else {
				addField(record, "");

			}

			/* Company Registration Number */
			if (relatedType == 1 || relatedType == 3) {
				addField(record, customer.getCustTradeLicenceNum());

			} else {
				addField(record, "");

			}

			/* Date of Incorporation */

			if (relatedType == 1 || relatedType == 3) {
				addField(record, DateUtil.format(customer.getCustDOB(), DATE_FORMAT));
			} else {
				addField(record, "");

			}

			/* Date of Birth */
			if (relatedType == 2 || relatedType == 4) {
				addField(record, DateUtil.format(customer.getCustDOB(), DATE_FORMAT));
			} else {
				addField(record, "");

			}

			/* PAN */
			addField(record, customer.getCustCRCPR());

			/* Voter ID */
			addField(record, "");

			/* Passport Number */
			addField(record, "");

			/* Driving Licence ID */
			addField(record, "");
			/* UID */
			addField(record, "");

			/* Ration Card No */
			addField(record, "");

			/* CIN */
			addField(record, "");

			/* DIN */
			addField(record, "");

			/* TIN */
			addField(record, "");

			/* Service Tax */
			addField(record, "");

			/* Other ID */
			addField(record, "");

			/* Percentage of Control */
			addField(record, "");

			if (customerAddr == null || customerAddr.isEmpty()) {
				throw new Exception("Address details are not available.");
			}

			for (CustomerAddres address : customerAddr) {
				/* Address Line 1 */
				addField(record, address.getCustAddrHNbr());

				/* Address Line 2 */
				addField(record, address.getCustFlatNbr());

				/* Address Line 3 */
				addField(record, address.getCustAddrStreet());

				/* City/Town When */
				addField(record, address.getCustAddrCity());

				/* District */
				addField(record, address.getCustDistrict());

				/* State/Union Territory */
				addField(record, address.getCustAddrProvince());

				/* pincode */
				addField(record, address.getCustAddrZIP());

				/* Country */
				addField(record, "079");

				CustomerPhoneNumber phoneNumber = null;
				for (CustomerPhoneNumber custPhNo : customerPhoneNumber) {
					if (address.getCustAddrPriority() == custPhNo.getPhoneTypePriority()) {
						phoneNumber = custPhNo;
						break;
					}
				}

				if (phoneNumber != null) {
					if (PennantConstants.PHONETYPE_MOBILE.equals(phoneNumber.getPhoneTypeCode())) {
						/* Mobile Number(s) */
						addField(record, phoneNumber.getPhoneNumber());

						/* Telephone Area Code */
						addField(record, phoneNumber.getPhoneNumber());
					} else if ("FAX".equals(phoneNumber.getPhoneTypeCode())) {
						/* Fax Area Code Fax Number(s) */
						addField(record, phoneNumber.getPhoneNumber());

						/* Fax Number(s) */
						addField(record, phoneNumber.getPhoneNumber());
					}
				} else {
					addField(record, "");
					addField(record, "");
					addField(record, "");
					addField(record, "");
					addField(record, "");
					addField(record, "");
				}
				writer.write(record);
			}
		}
	}

	public class CreditFacilitySegment {
		private CSVWriter writer;
		private CustomerDetails customerDetails;
		private String finReference;

		public CreditFacilitySegment(CSVWriter writer, CustomerDetails customerDetails, String finReference) {
			super();
			this.writer = writer;
			this.customerDetails = customerDetails;
			this.finReference = finReference;
		}

		public void write() {
			List<FinanceEnquiry> finances = customerDetails.getCustomerFinances();

			if (CollectionUtils.isEmpty(finances)) {
				return;
			}

			for (FinanceEnquiry loan : finances) {
				if (!StringUtils.equals(finReference, loan.getFinReference())) {
					continue;
				}

				Record record = new Record();

				/* Segment Identifier */
				addField(record, "CR");

				/* Account Number */
				addField(record, loan.getFinReference());

				/* Previous Account Number */
				addField(record, "");

				/* Facility / Loan Activation /Sanction Date */
				addField(record, DateUtil.format(loan.getFinStartDate(), DATE_FORMAT));

				/* Sanctioned Amount/ Notional Amount of Contract */
				addField(record, loan.getFinAssetValue());

				/* Currency Code */
				addField(record, "INR");

				/* Credit Type */
				String finType = StringUtils.trimToNull(loan.getFinType());
				if (finType == null) {
					finType = "9999";
				}
				addField(record, finType);

				/* Tenure / Weighted Average maturity period of Contracts */
				addField(record, String.valueOf(loan.getNumberOfTerms()));

				/* Repayment Frequency */
				String rePayfrq = StringUtils.trimToEmpty(loan.getRepayFrq());
				String frequecncy = null;

				if (rePayfrq.length() > 0) {
					frequecncy = rePayfrq.substring(0, 1);
				}

				if ("M".equals(frequecncy)) {
					rePayfrq = "01";
				} else if ("Q".equals(frequecncy)) {
					rePayfrq = "02";
				} else if ("H".equals(frequecncy)) {
					rePayfrq = "03";
				} else if ("Y".equals(frequecncy)) {
					rePayfrq = "04";
				} else if ("D".equals(frequecncy)) {
					rePayfrq = "08";
				} else if ("B".equals(frequecncy)) {
					rePayfrq = "06";
				} else if ("R".equals(frequecncy)) {
					rePayfrq = "07";
				} else if (loan.getNumberOfTerms() == 1) {
					rePayfrq = "06";
				}

				if (StringUtils.isEmpty(rePayfrq)) {
					rePayfrq = "08";
				}
				addField(record, rePayfrq);

				/* Drawing Power */
				addField(record, loan.getFinAssetValue());

				/* Current Balance / Limit Utilized /Mark to Marketr */
				int odDays = Integer.parseInt(getOdDays(loan.getCurODDays()));
				BigDecimal currentBalance = BigDecimal.ZERO;
				String closingstatus = StringUtils.trimToEmpty(loan.getClosingStatus());

				if (odDays > 0) {
					BigDecimal futureSchedulePrincipal = getAmount(loan.getFutureSchedulePrin());
					currentBalance = futureSchedulePrincipal;
				} else {
					currentBalance = getAmount(loan.getFutureSchedulePrin());
				}

				if (currentBalance.compareTo(BigDecimal.ZERO) < 0) {
					currentBalance = BigDecimal.ZERO;
				}

				// and overdue amount should be 0
				if (StringUtils.equals("C", closingstatus)) {
					currentBalance = BigDecimal.ZERO;
				}

				addField(record, currentBalance(loan));

				/* Notional Amount of Out-standing Restructured Contract */
				addField(record, "");

				/* Loan Expiry / Maturity Date */
				addField(record, DateUtil.format(loan.getMaturityDate(), DATE_FORMAT));

				/* Loan Renewal Date */
				addField(record, "");

				/* Asset Classification */
				int pastDues = 0;
				Date odStartDate = null;
				for (FinODDetails od : loan.getFinOdDetails()) {
					if (od.getFinCurODAmt() != null && od.getFinCurODAmt().compareTo(BigDecimal.ZERO) > 0) {
						if (pastDues < od.getFinCurODDays()) {
							pastDues = od.getFinCurODDays();
						}

						if (odStartDate == null && od.getFinODSchdDate() != null) {
							odStartDate = DateUtil.getDatePart(od.getFinODSchdDate());
						}

						if (DateUtility.compare(odStartDate, DateUtil.getDatePart(od.getFinODSchdDate())) > 0) {
							odStartDate = DateUtil.getDatePart(odStartDate);
						}
					}
				}

				String code = "";

				if (pastDues == 0) {
					code = "0001"; // Standard
				} else if (pastDues == 1) {
					code = "1001"; // 1 Day Past Due
				} else if (pastDues == 2) {
					code = "1002"; // 2 Day Past Due
				} else if (pastDues > 2 && pastDues < 999) {
					code = "1" + String.valueOf(pastDues); // n Day Past Due
				} else if (pastDues >= 999) {
					code = "1999"; // 999 or above Days Past Due
				}

				addField(record, code);

				/* Asset Classification Date */
				if (StringUtils.isNotEmpty(code) && !code.equals("0001")) {
					addField(record, DateUtil.format(odStartDate, DATE_FORMAT));
				} else {
					addField(record, "");
				}

				BigDecimal amountOverdue = BigDecimal.ZERO;

				if (odDays > 0) {
					BigDecimal installmentDue = getAmount(loan.getInstalmentDue());
					BigDecimal installmentPaid = getAmount(loan.getInstalmentPaid());
					BigDecimal bounceDue = getAmount(loan.getBounceDue());
					BigDecimal bouncePaid = getAmount(loan.getBouncePaid());
					BigDecimal penaltyDue = getAmount(loan.getLatePaymentPenaltyDue());
					BigDecimal penaltyPaid = getAmount(loan.getLatePaymentPenaltyPaid());
					BigDecimal ExcessAmount = getAmount(loan.getExcessAmount());
					BigDecimal ExcessAmountPaid = getAmount(loan.getExcessAmtPaid());

					amountOverdue = (installmentDue.subtract(installmentPaid)).add(bounceDue.subtract(bouncePaid)
							.add(penaltyDue.subtract(penaltyPaid).subtract(ExcessAmount.subtract(ExcessAmountPaid))));
				} else {
					amountOverdue = BigDecimal.ZERO;
				}

				if (amountOverdue.compareTo(BigDecimal.ZERO) < 0) {
					amountOverdue = BigDecimal.ZERO;
				}

				if (StringUtils.equals("C", closingstatus)) {
					amountOverdue = BigDecimal.ZERO;
				}

				/* Amount Overdue / Limit Overdue */
				if (amountOverdue.compareTo(BigDecimal.ZERO) > 0) {
					addField(record, amountOverdue);
				} else {
					addField(record, "0");
				}

				BigDecimal bucket01 = BigDecimal.ZERO;
				BigDecimal bucket02 = BigDecimal.ZERO;
				BigDecimal bucket03 = BigDecimal.ZERO;
				BigDecimal bucket04 = BigDecimal.ZERO;
				BigDecimal bucket05 = BigDecimal.ZERO;

				int curOdDays = 0;
				for (FinODDetails od : loan.getFinOdDetails()) {
					curOdDays = od.getFinCurODDays();
					if (od.getFinCurODAmt() != null && od.getFinCurODAmt().compareTo(BigDecimal.ZERO) > 0) {
						if (curOdDays <= 30) {
							bucket01 = od.getFinCurODAmt();
						}

						if (curOdDays > 30 && curOdDays <= 60) {
							bucket02 = od.getFinCurODAmt();
						}

						if (curOdDays > 60 && curOdDays <= 90) {
							bucket03 = od.getFinCurODAmt();
						}

						if (curOdDays > 90 && curOdDays <= 180) {
							bucket04 = od.getFinCurODAmt();
						}

						if (curOdDays > 180) {
							bucket05 = od.getFinCurODAmt();
						}
					}
				}

				/* Overdue Bucket 01 */
				if (bucket01 != null && bucket01.compareTo(BigDecimal.ZERO) > 0) {
					addField(record, bucket01);
				} else {
					addField(record, "");
				}

				/* Overdue Bucket 02 */
				if (bucket02 != null && bucket02.compareTo(BigDecimal.ZERO) > 0) {
					addField(record, bucket02);
				} else {
					addField(record, "");
				}

				/* Overdue Bucket 03 */
				if (bucket03 != null && bucket03.compareTo(BigDecimal.ZERO) > 0) {
					addField(record, bucket03);
				} else {
					addField(record, "");
				}

				/* Overdue Bucket 04 */
				if (bucket04 != null && bucket04.compareTo(BigDecimal.ZERO) > 0) {
					addField(record, bucket04);
				} else {
					addField(record, "");
				}

				/* Overdue Bucket 05 */
				if (bucket05 != null && bucket05.compareTo(BigDecimal.ZERO) > 0) {
					addField(record, bucket05);
				} else {
					addField(record, "");
				}

				/* High Credit */
				addField(record, "");

				/* Installment Amount */
				addField(record, "");

				/* Last Repaid Amount */
				addField(record, "");

				/* Account Status */
				if (StringUtils.isEmpty(closingstatus)) {
					addField(record, "01");
				} else if ("M".equals(closingstatus)) {
					addField(record, "02");
				} else if ("C".equals(closingstatus)) {
					addField(record, "03");
				} else if ("E".equals(closingstatus)) {
					addField(record, "04");
				} else if ("W".equals(closingstatus)) {
					addField(record, "05");
				} else {
					addField(record, "05");
				}

				/* Account Status Date */
				addField(record, "");

				/* Written Off Amount */
				if (StringUtils.equals("W", closingstatus)) {
					BigDecimal writtenOffAmount = (loan.getTotalPriSchd().subtract(loan.getTotalPriPaid())
							.add(loan.getTotalPftSchd().subtract(loan.getTotalPftPaid())
									.subtract(loan.getExcessAmount().subtract(loan.getExcessAmtPaid()))));

					addField(record, writtenOffAmount);
				} else {
					addField(record, "");
				}

				/* Settled Amount */
				addField(record, "");

				/* Major reasons for Restructuring */
				addField(record, "");

				/* Amount of Contracts Classified as NPA */
				addField(record, "");

				/* Asset based Security coverage */
				addField(record, "");

				/* Guarantee Coverage */
				addField(record, "");

				/* Bank Remark Code */
				addField(record, "");

				/* Wilful Default Status */
				addField(record, "0");

				/* Date Classified as Wilful Default */
				addField(record, "");

				/* Suit Filed Status */
				addField(record, "");

				/* Suit Reference Number */
				addField(record, "");

				/* Suit Amount in Rupees */
				addField(record, "");

				/* Date of Suit */
				addField(record, "");

				/* Dispute ID No. */
				addField(record, "");

				/* Transaction Type Code */
				addField(record, "01");

				/* Filler */
				addField(record, "");

				writer.write(record);

				creditfacilityCount++;
				try {
					new GuarantorSegment(writer, loan.getFinGuarenters()).write();
					// new SecuritySegment(writer, loan.getCollateralSetupDetails()).write();
					new DishonourOfChequeSegment(writer, loan.getChequeDetail()).write();
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}

			}

		}

	}

	public class GuarantorSegment {
		private CSVWriter writer;
		private List<CustomerDetails> guarenters;

		public GuarantorSegment(CSVWriter writer, List<CustomerDetails> guarenters) {
			this.writer = writer;
			this.guarenters = guarenters;
		}

		public void write() throws Exception {
			if (CollectionUtils.isEmpty(guarenters)) {
				return;
			}

			for (CustomerDetails customerDetails : guarenters) {
				Customer customer = customerDetails.getCustomer();
				List<CustomerDocument> documents = customerDetails.getCustomerDocumentsList();
				List<CustomerAddres> addressList = customerDetails.getAddressList();
				List<CustomerPhoneNumber> customerPhoneNumbers = customerDetails.getCustomerPhoneNumList();

				Record record = new Record();
				addField(record, "GS");

				/* Guarantor DUNS number */
				addField(record, "999999999");

				/* Guarantor Type */
				int guarantorType = 0;

				if ("I".equals(customer.getLovDescCustCtgType())) {
					guarantorType = 2;
					if (PennantConstants.ADDRESS_TYPE_RESIDENCE.equals(customer.getCustAddlVar1())) {
						if ("IN".equals(customer.getCustAddlVar2())) {
							guarantorType = 2;
						} else {
							guarantorType = 4;
						}
					}
				} else {
					if ("IN".equals(customer.getCustCOB())) {
						guarantorType = 1;
					} else {
						guarantorType = 3;
					}
				}

				addField(record, String.valueOf(guarantorType));

				/* Business Category */
				if (guarantorType == 1 || guarantorType == 3) {
					addField(record, customer.getBusinesscategory());
				} else {
					addField(record, "07");
				}

				/* Business /Industry Type */
				if (guarantorType == 1 || guarantorType == 3) {
					addField(record, customer.getCustIndustry());
				} else {
					addField(record, "11");
				}
				/* Guarantor Name */
				if (guarantorType == 1 || guarantorType == 3) {
					addField(record, customer.getCustShrtName());
				} else {
					addField(record, "");
				}

				/* Individual Name Prefix */
				if (guarantorType == 2 || guarantorType == 4) {
					String title = StringUtils.trimToEmpty(customer.getCustSalutationCode());
					if (title.equalsIgnoreCase("Mr") || title.equalsIgnoreCase("Ms") || title.equalsIgnoreCase("Mrs")
							|| title.equalsIgnoreCase("Dr")) {
						addField(record, title);
					} else {
						addField(record, title);
					}
				} else {
					addField(record, "");
				}

				/* Full Name */
				if (guarantorType == 2 || guarantorType == 4) {
					StringBuffer name = new StringBuffer();

					String firstName = StringUtils.trimToNull(customer.getCustFName());
					String middleName = StringUtils.trimToNull(customer.getCustMName());
					String lastName = StringUtils.trimToNull(customer.getCustLName());

					if (firstName != null) {
						name.append(firstName);
					} else if (middleName != null) {
						name.append(" ");
						name.append(middleName);
					} else if (lastName != null) {
						name.append(" ");
						name.append(lastName);
					}
					addField(record, customer.getCustShrtName());
				} else {
					addField(record, "");
				}
				/* Gender */
				if (guarantorType == 2 || guarantorType == 4) {
					if ("M".equals(customer.getCustGenderCode())) {
						addField(record, "01");
					} else if ("F".equals(customer.getCustGenderCode())) {
						addField(record, "01");
					} else {
						addField(record, "");
					}
				} else {
					addField(record, "");
				}

				/* Company Registration number */
				if (guarantorType == 1 || guarantorType == 3) {
					addField(record, customer.getCustTradeLicenceNum());
				} else {
					addField(record, "");
				}

				/* Date of Incorporation */
				if (guarantorType == 1 || guarantorType == 3) {
					addField(record, DateUtil.format(customer.getCustDOB(), DATE_FORMAT));
				} else {
					addField(record, "");
				}

				/* Date of Birth */
				if (guarantorType == 2 || guarantorType == 4) {
					addField(record, DateUtil.format(customer.getCustDOB(), DATE_FORMAT));
				} else {
					addField(record, "");
				}

				String pan = null;
				String voterId = null;
				String passPortNumber = null;
				String drivingLicenceId = null;
				String uid = null;
				String rationCardNo = null;
				String corporateIdNum = null;
				String directorIdNumber = null;
				String taxIdNumber = null;
				String serviceTax = null;

				String docCategory;
				for (CustomerDocument document : documents) {
					docCategory = document.getCustDocCategory();

					if (StringUtils.equals(docCategory, MasterDefUtil.getDocCode(DocType.PAN))) {
						pan = document.getCustDocTitle();
					} else if (StringUtils.equals(docCategory, MasterDefUtil.getDocCode(DocType.VOTER_ID))) {
						voterId = document.getCustDocTitle();
					} else if (StringUtils.equals(docCategory, MasterDefUtil.getDocCode(DocType.PASSPORT))) {
						passPortNumber = document.getCustDocTitle();
					} else if (StringUtils.equals(docCategory, MasterDefUtil.getDocCode(DocType.DRIVING_LICENCE))) {
						drivingLicenceId = document.getCustDocTitle();
					} else if (StringUtils.equals(docCategory, MasterDefUtil.getDocCode(DocType.AADHAAR))) {
						uid = document.getCustDocTitle();
					} else if (StringUtils.equals(docCategory, MasterDefUtil.getDocCode(DocType.RATION_CARD))) {
						rationCardNo = document.getCustDocTitle();
					} else if (StringUtils.equals(docCategory, MasterDefUtil.getDocCode(DocType.CORPORATE_ID_NUMBER))) {
						corporateIdNum = document.getCustDocTitle();
					} else if (StringUtils.equals(docCategory,
							MasterDefUtil.getDocCode(DocType.DIRECTOR_IDENTIFICATION_NUMBER))) {
						directorIdNumber = document.getCustDocTitle();
					} else if (StringUtils.equals(docCategory,
							MasterDefUtil.getDocCode(DocType.TAX_IDENTIFICATION_NUMBER))) {
						taxIdNumber = document.getCustDocTitle();
					} else if (StringUtils.equals(docCategory, MasterDefUtil.getDocCode(DocType.SERVICE_TAX_REG_NO))) {
						serviceTax = document.getCustDocTitle();
					}
				}

				/* pan */
				if (pan == null) {
					pan = customer.getCustCRCPR();
				}
				addField(record, pan);

				/* VoterId */
				if (guarantorType == 2 || guarantorType == 4) {
					addField(record, voterId);
				} else {
					addField(record, "");
				}

				/* passport Number */
				if (guarantorType == 2 || guarantorType == 4) {
					addField(record, passPortNumber);
				} else {
					addField(record, "");
				}

				/* Driving Licence ID */
				if (guarantorType == 2 || guarantorType == 4) {
					addField(record, drivingLicenceId);
				} else {
					addField(record, "");
				}

				/* UID */
				if (guarantorType == 2 || guarantorType == 4) {
					addField(record, uid);
				} else {
					addField(record, "");
				}

				/* Ration Card No */
				if (guarantorType == 2 || guarantorType == 4) {
					addField(record, rationCardNo);
				} else {
					addField(record, "");
				}

				/* CIN */
				addField(record, corporateIdNum);

				/* DIN */
				if (guarantorType == 2 || guarantorType == 4) {
					addField(record, directorIdNumber);
				} else {
					addField(record, "");
				}

				/* TIN */
				addField(record, taxIdNumber);

				/* Service Tax */
				addField(record, serviceTax);

				/* Other ID */
				addField(record, "");

				/* Address */
				CustomerAddres custAddress = null;

				for (CustomerAddres addres : addressList) {
					if (StringUtils.equals(String.valueOf(addres.getCustAddrPriority()),
							PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
						custAddress = addres;
						break;
					}
				}

				if (custAddress == null) {
					for (CustomerAddres addres : addressList) {
						custAddress = addres;
					}
				}

				if (custAddress == null) {
					custAddress = new CustomerAddres();
				}

				setAddressDetails(custAddress, customerPhoneNumbers, record, true);

				/* Filler */
				addField(record, "");

				writer.write(record);
			}
		}

	}

	public class SecuritySegment {
		private CSVWriter writer;
		private List<CollateralSetup> securities;

		public SecuritySegment(CSVWriter writer, List<CollateralSetup> securities) {
			this.writer = writer;
			this.securities = securities;
		}

		public void write() throws Exception {
			Record record = new Record();

			if (CollectionUtils.isEmpty(securities)) {
				return;
			}

			for (CollateralSetup security : securities) {
				addField(record, "SS");

				/* Value of Security */
				addField(record, security.getBankValuation());

				/* Currency Type */
				addField(record, security.getCollateralCcy());

				/* Type of Security */

				String securityType = StringUtils.trimToEmpty(security.getCollateralType());

				if (StringUtils.isEmpty(securityType)) {
					securityType = "009";
				}
				addField(record, securityType);

				/* Security Classification */
				addField(record, "01");

				/* Date of Valuation */
				addField(record, "");

				/* Filler */
				addField(record, "");

				writer.write(record);
			}

		}

	}

	public class DishonourOfChequeSegment {
		private CSVWriter writer;
		private List<ChequeDetail> chequeDetails;

		public DishonourOfChequeSegment(CSVWriter writer, List<ChequeDetail> chequeDetails) {
			this.writer = writer;
			this.chequeDetails = chequeDetails;
		}

		public void write() throws Exception {
			if (CollectionUtils.isEmpty(chequeDetails)) {
				return;
			}

			for (ChequeDetail bounce : chequeDetails) {
				Record record = new Record();
				/* Segment Identifier */
				addField(record, "CD");

				/* Date of dishonour */
				addField(record, DateUtil.format(bounce.getChequeBounceDate(), DATE_FORMAT));

				/* Amount */
				addField(record, bounce.getAmount());

				/* Instrument / Cheque Number */
				addField(record, bounce.getChequeNumber());

				/* Number of times dishonoured */
				addField(record, "1");

				/* Cheque Issue Date */
				addField(record, DateUtil.format(bounce.getChequeDate(), DATE_FORMAT));

				/* Reason for Dishonour */
				addField(record, bounce.getChequeBounceReason());

				/* filler */
				addField(record, "");

				writer.write(record);
			}

		}
	}

	public class FileClosureSegment {
		private CSVWriter writer;

		public FileClosureSegment(CSVWriter writer) {
			this.writer = writer;
		}

		public void write() throws Exception {
			Record record = new Record();
			/* Segment Identifier */
			addField(record, "TS");
			/* Number of Borrower Segments */
			addField(record, String.valueOf(borrowerCount));

			/* Number of Credit Facility Segments */
			addField(record, String.valueOf(creditfacilityCount));

			/* filler */
			addField(record, "");

			writer.write(record);
		}

	}
	//changes to differentiate the CIBIL Member ID during CIBIL generation & enquiry
	private void initlize() {
		memberDetails = cibilService.getMemberDetailsByType(PennantConstants.PFF_CUSTCTG_CORP,
				PennantConstants.PFF_CIBIL_TYPE_GENERATE);
		totalRecords = 0;
		processedRecords = 0;
		successCount = 0;
		failedCount = 0;

		//EXTRACT_STATUS.reset();
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

	public void setCibilService(CIBILService cibilService) {
		this.cibilService = cibilService;
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

	private void setAddressDetails(CustomerAddres address, List<CustomerPhoneNumber> phoneList, Record record,
			boolean isGuarantor) {

		/* Address Line 1 */
		addField(record, StringUtils.trimToNull(address.getCustAddrHNbr()));

		/* Address Line 2 */
		addField(record, StringUtils.trimToNull(address.getCustFlatNbr()));

		/* Address Line 3 */
		addField(record, StringUtils.trimToNull(address.getCustAddrStreet()));

		/* City/Town */
		addField(record, address.getCustAddrCity());

		/* District */
		addField(record, address.getCustDistrict());

		/* State/Union Territory */

		addField(record, address.getCustAddrProvince());

		/* Pin Code */
		addField(record, address.getCustAddrZIP());

		/* Country */

		addField(record, "079");

		CustomerPhoneNumber customerPhoneNumber = null;
		for (CustomerPhoneNumber custPhNo : phoneList) {
			if (address.getCustAddrPriority() == custPhNo.getPhoneTypePriority()) {
				customerPhoneNumber = custPhNo;
				break;
			}
		}

		if (customerPhoneNumber == null) {
			customerPhoneNumber = new CustomerPhoneNumber();
		}

		StringBuilder mob = new StringBuilder();
		StringBuilder telephone = new StringBuilder();
		String telephoneAreaCode = null;
		StringBuilder fax = new StringBuilder();
		String faxAreaCode = null;

		String phoneType = customerPhoneNumber.getPhoneTypeCode();
		if ("OFFICE".equals(phoneType) || "HOME".equals(phoneType)) {
			telephoneAreaCode = customerPhoneNumber.getPhoneAreaCode();
		}

		if ("FAX".equals(phoneType) || "OFFFAX".equals(phoneType)) {
			faxAreaCode = customerPhoneNumber.getPhoneAreaCode();
		}

		if (phoneList != null) {
			for (CustomerPhoneNumber phone : phoneList) {
				phoneType = StringUtils.trimToEmpty(phone.getPhoneTypeCode());
				if (phoneType.startsWith("MOB") || phoneType.endsWith("MOB")) {
					if (mob.length() > 0) {
						mob.append(",");
					} else {
						mob.append(phone.getPhoneNumber());
					}
				} else if ("FAX".equals(phoneType) || "OFFFAX".equals(phoneType)) {
					if (fax.length() > 0) {
						fax.append(",");
					} else {
						fax.append(phone.getPhoneNumber());
					}
				} else if ("OFFICE".equals(phoneType) || "HOME".equals(phoneType)) {
					if (telephone.length() > 0) {
						telephone.append(",");
					} else {
						telephone.append(phone.getPhoneNumber());
					}
				}
			}

		}

		/* Mobile Number(s) */
		addField(record, mob.toString());

		/* Telephone Area Code */
		addField(record, telephoneAreaCode);

		/* Telephone Number(s) */
		addField(record, telephone.toString());

		/* Fax Area Code */
		addField(record, faxAreaCode);

		/* Fax Number(s) */
		addField(record, fax.toString());

		/* Filters */
		addField(record, "");
	}

	private void addField(Record record, String value) {
		if (StringUtils.isNotEmpty(value)) {
			record.addField().setValue(value);
		} else {
			record.addField();
		}
	}

	private void addField(Record record, BigDecimal value) {
		if (value == null) {
			value = BigDecimal.ZERO;
		}

		value = value.setScale(0, BigDecimal.ROUND_DOWN);

		addField(record, value.toString());
	}

	private BigDecimal currentBalance(FinanceEnquiry customerFinance) {
		int odDays = Integer.parseInt(getOdDays(customerFinance.getCurODDays()));
		BigDecimal currentBalance = BigDecimal.ZERO;
		String closingstatus = StringUtils.trimToEmpty(customerFinance.getClosingStatus());
		if (odDays > 0) {
			BigDecimal futureSchedulePrincipal = getAmount(customerFinance.getFutureSchedulePrin());
			BigDecimal installmentDue = getAmount(customerFinance.getInstalmentDue());
			BigDecimal installmentPaid = getAmount(customerFinance.getInstalmentPaid());
			BigDecimal bounceDue = getAmount(customerFinance.getBounceDue());
			BigDecimal bouncePaid = getAmount(customerFinance.getBouncePaid());
			BigDecimal penaltyDue = getAmount(customerFinance.getLatePaymentPenaltyDue());
			BigDecimal penaltyPaid = getAmount(customerFinance.getLatePaymentPenaltyPaid());
			BigDecimal ExcessAmount = getAmount(customerFinance.getExcessAmount());
			BigDecimal ExcessAmountPaid = getAmount(customerFinance.getExcessAmtPaid());
			currentBalance = futureSchedulePrincipal
					.add(installmentDue.subtract(installmentPaid).add(bounceDue.subtract(bouncePaid)
							.add(penaltyDue.subtract(penaltyPaid).subtract(ExcessAmount.subtract(ExcessAmountPaid)))));

		} else {
			currentBalance = getAmount(customerFinance.getFutureSchedulePrin());
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
}
