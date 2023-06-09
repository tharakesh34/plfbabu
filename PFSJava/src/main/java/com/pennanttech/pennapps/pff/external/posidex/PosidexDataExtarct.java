package com.pennanttech.pennapps.pff.external.posidex;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.TransactionStatus;

import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennanttech.dataengine.DatabaseDataEngine;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.external.PosidexProcess;
import com.pennanttech.pff.model.external.posidex.PosidexCustomer;
import com.pennanttech.pff.model.external.posidex.PosidexCustomerAddress;
import com.pennanttech.pff.model.external.posidex.PosidexCustomerLoan;

public class PosidexDataExtarct extends DatabaseDataEngine implements PosidexProcess {
	private static final Logger logger = LogManager.getLogger(PosidexDataExtarct.class);
	public static DataEngineStatus EXTRACT_STATUS = new DataEngineStatus("POSIDEX_CUSTOMER_UPDATE_REQUEST");

	private Date lastRunDate;
	private long batchId;
	private Date appDate;
	private String SOURCE_SYSTEM_ID;
	private String SOURCE_SYSTEM;

	private static final String CUSTOMER_DETAILS = "PSX_DEDUP_EOD_CUST_DEMO_DTL";
	private static final String CUSTOMER_ADDR_DETAILS = "PSX_DEDUP_EOD_CUST_ADDR_DTL";
	private static final String CUSTOMER_LOAN_DETAILS = "PSX_DEDUP_EOD_CUST_LOAN_DTL";
	private static final String CUSTOMER_REPORT_DETAILS = "DEDUP_EOD_CUST_REP_DTL";

	private String summary = null;
	private Map<String, String> parameterCodes = new HashMap<>();

	int chunckSize = 100;
	private MapSqlParameterSource paramMa = null;
	private boolean localUpdate = true;

	public PosidexDataExtarct(DataSource dataSource, long userId, Date valueDate, Date appDate) {
		super(dataSource, App.DATABASE.name(), userId, true, valueDate, EXTRACT_STATUS);
		this.appDate = appDate;
	}

	@Override
	public void process(Object... objects) {
		try {
			process("POSIDEX_CUSTOMER_UPDATE_REQUEST");
		} catch (Exception e) {
			throw new InterfaceException("POSIDEX_CUSTOMER_UPDATE_REQUEST", e.getMessage());
		}
	}

	@Override
	protected void processData() {
		logger.debug(Literal.ENTERING);

		paramMa = new MapSqlParameterSource();

		lastRunDate = getLatestRunDate();

		loadParameters();

		prepareCustomers();

		batchId = logHeader();

		loadCount();

		loadDescrepancyCount();

		loaddefaults();

		try {
			do {
				extractData();
				if (totalRecords <= processedCount) {
					return;
				}
			} while (totalRecords > 0);
			logger.debug("Processed Count " + processedCount);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			updateRemarks(new StringBuilder());
			updateHeader();
		}
	}

	public void extractData() {
		Map<String, PosidexCustomer> customers = getCustomers();
		setAddresses(customers);
		setLoans(customers);
		int count = 0;

		TransactionStatus txnStatus = null;
		transDef.setTimeout(180);
		for (PosidexCustomer customer : customers.values()) {
			if (count == 0) {
				txnStatus = transManager.getTransaction(transDef);
			}
			try {
				saveOrUpdate(customer);
				EXTRACT_STATUS.setSuccessRecords(successCount++);
				if (count++ >= chunckSize) {
					transManager.commit(txnStatus);
					count = 0;
				}
			} catch (Exception e) {
				count = 0;
				logger.error(Literal.EXCEPTION, e);
				transManager.rollback(txnStatus);
				saveBatchLog(String.valueOf(customer.getCustomerNo()), "F", e.getMessage());
				EXTRACT_STATUS.setFailedRecords(failedCount++);
			} finally {
				EXTRACT_STATUS.setProcessedRecords(processedCount++);
			}
		}

		if (count > 0 && !txnStatus.isCompleted()) {
			transManager.commit(txnStatus);
		}
	}

	private void saveOrUpdate(PosidexCustomer customer) {
		if (customer.getProcessType().equals("I")) {
			save(customer, true);
		} else {
			update(customer);
		}

		// Save to posidex tables
		save(customer, false);

		// Save customer addresses
		for (PosidexCustomerAddress address : customer.getPosidexCustomerAddress()) {
			if (address.getProcessType().equals("I")) {
				save(address, true);
			} else {
				update(address);
			}

			// Save to posidex tables
			save(address, false);
		}

		// Save customer loans
		for (PosidexCustomerLoan loan : customer.getPosidexCustomerLoans()) {
			if (loan.getProcessType().equals("I")) {
				save(loan, true);
			} else {
				update(loan);
			}

			// Save to posidex tables
			save(loan, false);
		}

		delete(customer);
	}

	private void delete(PosidexCustomer cusotemr) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("CUST_ID", cusotemr.getCustomerNo());
		parameterJdbcTemplate.update("DELETE FROM POSIDEX_CUSTOMERS WHERE CUST_ID=:CUST_ID", paramMap);
	}

	private void save(PosidexCustomer cusotemr, boolean stage) {
		StringBuilder sql = new StringBuilder();
		sql.append("Insert into ").append(CUSTOMER_DETAILS);
		sql.append(" values (");
		sql.append(" :BatchID,");

		if (stage) {
			sql.append(" :CustomerNo,");
		} else {
			sql.append(" :CustomerId,");
		}

		sql.append(" :SourceSysId,");
		sql.append(" :FirstName,");
		sql.append(" :MiddleName,");
		sql.append(" :LastName,");
		sql.append(" :Dob,");
		sql.append(" :Pan,");
		sql.append(" :DrivingLicenseNumber,");
		sql.append(" :VoterId,");
		sql.append(" :DateOfIncorporation,");
		sql.append(" :TanNo,");
		sql.append(" :ProcessType,");
		sql.append(" :ApplicantType,");
		sql.append(" :EmpoyerName,");
		sql.append(" :FatherName,");
		sql.append(" :PassportNo,");
		sql.append(" :AccountNumber,");
		sql.append(" :CreditCardNumber,");
		sql.append(" :ProcessFlag,");
		sql.append(" :ErrorCode,");
		sql.append(" :ErrorDesc,");
		sql.append(" :CustomerId,");
		sql.append(" :SourceSystem,");
		sql.append(" :PsxBatchID,");
		sql.append(" :UcinFlag,");
		sql.append(" :EodBatchID,");
		sql.append(" :InsertTs,");
		sql.append(" :Gender,");
		sql.append(" :AadharNo,");
		sql.append(" :Cin,");
		sql.append(" :Din,");
		sql.append(" :RegistrationNo,");
		sql.append(" :CaNumber,");
		sql.append(" :Segment");
		sql.append(")");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(cusotemr);

		if (stage) {
			parameterJdbcTemplate.update(sql.toString(), beanParameters);
		} else {
			destinationJdbcTemplate.update(sql.toString(), beanParameters);
		}

		saveReport(stage, beanParameters);
	}

	private void saveReport(boolean stage, SqlParameterSource beanParameters) {
		StringBuilder sql = new StringBuilder();
		sql.append("Insert into ").append(CUSTOMER_REPORT_DETAILS);
		sql.append(" (BATCHID, SOURCE_SYS_ID, CUSTOMER_ID, FILLER_STRING_1)");
		sql.append(" values (");
		sql.append(" :BatchID,");
		sql.append(" :SourceSysId,");
		sql.append(" :CustomerId,");
		sql.append(" :CustCoreBank");
		sql.append(")");

		if (stage) {
			parameterJdbcTemplate.update(sql.toString(), beanParameters);
		} else {
			destinationJdbcTemplate.update(sql.toString(), beanParameters);
		}
	}

	private void update(PosidexCustomer cusotemr) {

		if (!localUpdate) {
			return;
		}

		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE ").append(CUSTOMER_DETAILS);
		sql.append(" Set BATCHID=:BatchID,");
		sql.append(" SOURCE_SYS_ID=:SourceSysId,");
		sql.append(" FIRST_NAME=:FirstName,");
		sql.append(" MIDDLE_NAME=:MiddleName,");
		sql.append(" LAST_NAME=:LastName,");
		sql.append(" DOB=:Dob,");
		sql.append(" PAN=:Pan,");
		sql.append(" DRIVING_LICENSE_NUMBER=:DrivingLicenseNumber,");
		sql.append(" VOTER_ID=:VoterId,");
		sql.append(" DATE_OF_INCORPORATION=:DateOfIncorporation,");
		sql.append(" TAN_NO=:TanNo,");
		sql.append(" PROCESS_TYPE=:ProcessType,");
		sql.append(" APPLICANT_TYPE=:ApplicantType,");
		sql.append(" EMPOYER_NAME=:EmpoyerName,");
		sql.append(" FATHER_NAME=:FatherName,");
		sql.append(" PASSPORT_NO=:PassportNo,");
		sql.append(" ACCOUNT_NUMBER=:AccountNumber,");
		sql.append(" CREDIT_CARD_NUMBER=:CreditCardNumber,");
		sql.append(" PROCESS_FLAG=:ProcessFlag,");
		sql.append(" ERROR_CODE=:ErrorCode,");
		sql.append(" ERROR_DESC=:ErrorDesc,");
		sql.append(" CUSTOMER_ID=:CustomerId,");
		sql.append(" SOURCE_SYSTEM=:SourceSystem,");
		sql.append(" PSX_BATCH_ID=:PsxBatchID,");
		sql.append(" UCIN_FLAG=:UcinFlag,");
		sql.append(" EOD_BATCH_ID=:EodBatchID,");
		sql.append(" INSERT_TS=:InsertTs,");
		sql.append(" GENDER=:Gender,");
		sql.append(" AADHAR_NO=:AadharNo,");
		sql.append(" CIN=:Cin,");
		sql.append(" DIN=:Din,");
		sql.append(" REGISTRATION_NO=:RegistrationNo,");
		sql.append(" CA_NUMBER=:CaNumber,");
		sql.append(" SEGMENT=:Segment");
		sql.append(" WHERE CUSTOMER_NO = :CustomerNo");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(cusotemr);
		parameterJdbcTemplate.update(sql.toString(), beanParameters);

	}

	private void save(PosidexCustomerAddress address, boolean stage) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ");
		sql.append(CUSTOMER_ADDR_DETAILS).append("(");
		sql.append(" BatchID,");

		sql.append(" CUSTOMER_NO,");
		sql.append(" SOURCE_SYS_ID,");
		sql.append(" SEGMENT,");
		sql.append(" ADDRESS_TYPE,");
		sql.append(" ADDRESS_1,");
		sql.append(" ADDRESS_2,");
		sql.append(" ADDRESS_3,");
		sql.append(" STATE,");
		sql.append(" CITY,");
		sql.append(" PIN,");
		sql.append(" LANDLINE_1,");
		sql.append(" LANDLINE_2,");
		sql.append(" MOBILE,");
		sql.append(" AREA,");
		sql.append(" LANDMARK,");
		sql.append(" STD,");
		sql.append(" PROCESS_TYPE,");
		sql.append(" EMAIL,");
		sql.append(" PROCESS_FLAG,");
		sql.append(" ERROR_CODE,");
		sql.append(" ERROR_DESC,");
		sql.append(" CUSTOMER_ID,");
		sql.append(" SOURCE_SYSTEM,");
		sql.append(" PSX_BATCH_ID,");
		sql.append(" EOD_BATCH_ID ");
		sql.append(" ) VALUES (");
		sql.append(" :BatchID,");

		if (stage) {
			sql.append(" :CustomerNo,");
		} else {
			sql.append(" :CustomerId,");
		}

		sql.append(" :SourceSysId,");
		sql.append(" :Segment,");
		sql.append(" :Addresstype,");
		sql.append(" :Address1,");
		sql.append(" :Address2,");
		sql.append(" :Address3,");
		sql.append(" :State,");
		sql.append(" :City,");
		sql.append(" :Pin,");
		sql.append(" :Landline1,");
		sql.append(" :Landline2,");
		sql.append(" :Mobile,");
		sql.append(" :Area,");
		sql.append(" :Landmark,");
		sql.append(" :Std,");
		sql.append(" :ProcessType,");
		sql.append(" :Email,");
		sql.append(" :ProcessFlag,");
		sql.append(" :ErrorCode,");
		sql.append(" :ErrorDesc,");
		sql.append(" :CustomerId,");
		sql.append(" :SourceSystem,");
		sql.append(" :PsxBatchID,");
		sql.append(" :EodBatchID");
		sql.append(")");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(address);

		if (stage) {
			parameterJdbcTemplate.update(sql.toString(), beanParameters);
		} else {
			destinationJdbcTemplate.update(sql.toString(), beanParameters);
		}
	}

	private void update(PosidexCustomerAddress addresses) {
		if (!localUpdate) {
			return;
		}

		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE ").append(CUSTOMER_ADDR_DETAILS);
		sql.append(" Set BatchID=:BatchID,");
		sql.append(" SOURCE_SYS_ID=:SourceSysId,");
		sql.append(" SEGMENT=:Segment,");
		sql.append(" ADDRESS_1=:Address1,");
		sql.append(" ADDRESS_2=:Address2,");
		sql.append(" ADDRESS_3=:Address3,");
		sql.append(" STATE=:State,");
		sql.append(" CITY=:City,");
		sql.append(" PIN=:Pin,");
		sql.append(" LANDLINE_1=:Landline1,");
		sql.append(" LANDLINE_2=:Landline2,");
		sql.append(" MOBILE=:Mobile,");
		sql.append(" AREA=:Area,");
		sql.append(" LANDMARK=:Landmark,");
		sql.append(" STD=:Std,");
		sql.append(" PROCESS_TYPE=:ProcessType,");
		sql.append(" EMAIL=:Email,");
		sql.append(" PROCESS_FLAG=:ProcessFlag,");
		sql.append(" ERROR_CODE=:ErrorCode,");
		sql.append(" ERROR_DESC=:ErrorDesc,");
		sql.append(" CUSTOMER_ID=:CustomerId,");
		sql.append(" SOURCE_SYSTEM=:SourceSystem,");
		sql.append(" PSX_BATCH_ID=:PsxBatchID,");
		sql.append(" EOD_BATCH_ID=:EodBatchID");
		sql.append(" WHERE CUSTOMER_NO = :CustomerNo AND ADDRESS_TYPE=:Addresstype");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(addresses);

		parameterJdbcTemplate.update(sql.toString(), beanParameters);

	}

	private void save(PosidexCustomerLoan loan, boolean stage) {
		StringBuilder sql = new StringBuilder();
		sql.append("Insert into ").append(CUSTOMER_LOAN_DETAILS);
		sql.append(" values (");
		sql.append(" :BatchID,");
		if (stage) {
			sql.append(" :CustomerNo,");
		} else {
			sql.append(" :CustomerId,");
		}
		sql.append(" :SourceSysId,");
		sql.append(" :Segment,");
		sql.append(" :DealID,");
		sql.append(" :LanNo,");
		sql.append(" :CustomerType,");
		sql.append(" :ApplnNo,");
		sql.append(" :ProductCode,");
		sql.append(" :ProcessType,");
		sql.append(" :ProcessFlag,");
		sql.append(" :ErrorCode,");
		sql.append(" :ErrorDesc,");
		sql.append(" :PsxBatchID,");
		sql.append(" :PsxID,");
		sql.append(" :CustomerId,");
		sql.append(" :SourceSystem,");
		sql.append(" :EodBatchID");
		sql.append(")");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(loan);

		try {
			if (stage) {
				parameterJdbcTemplate.update(sql.toString(), beanParameters);
			} else {
				destinationJdbcTemplate.update(sql.toString(), beanParameters);
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);

		}

	}

	private void update(PosidexCustomerLoan loan) {
		if (!localUpdate) {
			return;
		}

		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE ").append(CUSTOMER_LOAN_DETAILS);
		sql.append(" SET BATCHID =:BatchID,");
		sql.append(" SOURCE_SYS_ID =:SourceSysId,");
		sql.append(" SEGMENT =:Segment,");
		sql.append(" DEAL_ID =:DealID,");
		sql.append(" APPLN_NO =:ApplnNo,");
		sql.append(" PRODUCT_CODE =:ProductCode,");
		sql.append(" PROCESS_TYPE =:ProcessType,");
		sql.append(" PROCESS_FLAG =:ProcessFlag,");
		sql.append(" ERROR_CODE =:ErrorCode,");
		sql.append(" ERROR_DESC =:ErrorDesc,");
		sql.append(" PSX_BATCH_ID =:PsxBatchID,");
		sql.append(" PSX_ID =:PsxID,");
		sql.append(" CUSTOMER_ID =:CustomerId,");
		sql.append(" SOURCE_SYSTEM =:SourceSystem,");
		sql.append(" EOD_BATCH_ID =:EodBatchID");
		sql.append(" WHERE CUSTOMER_NO = :CustomerNo AND LAN_NO=:LanNo AND CUSTOMER_TYPE =:CustomerType");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(loan);
		parameterJdbcTemplate.update(sql.toString(), beanParameters);

	}

	private Map<String, PosidexCustomer> getCustomers() {
		Map<String, PosidexCustomer> customers = new HashMap<>();
		StringBuilder sql = new StringBuilder();
		sql.append(" select C.CUSTID, CUSTCIF, CUSTCOREBANK, CUSTFNAME, CUSTMNAME, CUSTLNAME, CUSTSHRTNAME, CUSTDOB,");
		sql.append(" CUSTGENDERCODE, CUSTMOTHERMAIDEN, C.CUSTCTGCODE, CUSTDOCTITLE, CUSTDOCCATEGORY,");
		sql.append(" EMPNAME, ACCOUNTNUMBER, CUSTCOREBANK,");
		sql.append(" PROCESS_TYPE");
		sql.append(" FROM CUSTOMERS C");
		sql.append(" LEFT JOIN CUSTOMERDOCUMENTS CD ON CD.CUSTID = C.CUSTID");
		sql.append(" LEFT JOIN CUSTEMPLOYEEDETAIL CE ON CE.CUSTID = C.CUSTID");
		sql.append(" LEFT JOIN (select CUSTID, ACCOUNTNUMBER from CUSTOMERBANKINFO");
		sql.append(" order by BANKID) CBA ON CBA.CUSTID = C.CUSTID AND ROWNUM  =1");
		sql.append(" LEFT JOIN PSX_DEDUP_EOD_CUST_DEMO_DTL PC ON PC.CUSTOMER_NO = C.CUSTID");
		sql.append(" WHERE C.CUSTID IN (select CUST_ID FROM POSIDEX_CUSTOMERS)");

		return extractCustomers(customers, sql);
	}

	private Map<String, PosidexCustomer> extractCustomers(Map<String, PosidexCustomer> customers, StringBuilder sql) {
		return parameterJdbcTemplate.query(sql.toString(), paramMa,
				new ResultSetExtractor<Map<String, PosidexCustomer>>() {
					PosidexCustomer customer = null;

					@Override
					public Map<String, PosidexCustomer> extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						String docType = null;
						while (rs.next()) {

							customer = customers.get(rs.getString("CUSTID"));
							if (customer == null) {
								customer = new PosidexCustomer();
								customers.put(rs.getString("CUSTID"), customer);
							}

							customer.setCustomerNo(rs.getString("CUSTID"));
							customer.setCustomerId(rs.getString("CUSTCIF"));
							customer.setFirstName(rs.getString("CUSTFNAME"));
							customer.setMiddleName(rs.getString("CUSTMNAME"));
							customer.setLastName(rs.getString("CUSTLNAME"));

							customer.setProcessType(rs.getString("PROCESS_TYPE"));
							customer.setApplicantType(rs.getString("CUSTCTGCODE"));
							customer.setCustCoreBank(rs.getString("CUSTCOREBANK"));

							if (customer.getProcessType() == null) {
								customer.setProcessType("I");
							} else {
								customer.setProcessType("U");
							}

							if ("RETAIL".equals(customer.getApplicantType())) {
								customer.setApplicantType("I");
								customer.setDob(rs.getDate("CUSTDOB"));
								customer.setGender(rs.getString("CUSTGENDERCODE"));
							} else {
								customer.setDateOfIncorporation(rs.getDate("CUSTDOB"));
								customer.setApplicantType("C");
								customer.setFirstName(rs.getString("CUSTSHRTNAME"));
							}

							// Set Document Details
							docType = StringUtils.trimToEmpty(rs.getString("CUSTDOCCATEGORY"));

							if ("03".equals(docType)) {
								customer.setPan(rs.getString("CUSTDOCTITLE"));
							} else if ("04".equals(docType)) {
								customer.setDrivingLicenseNumber(rs.getString("CUSTDOCTITLE"));
							} else if ("05".equals(docType)) {
								customer.setVoterId(rs.getString("CUSTDOCTITLE"));
							} else if ("15".equals(docType)) {
								customer.setTanNo(rs.getString("CUSTDOCTITLE"));
							} else if ("02".equals(docType)) {
								customer.setPassportNo(rs.getString("CUSTDOCTITLE"));
							} else if ("01".equals(docType)) {
								customer.setAadharNo(rs.getString("CUSTDOCTITLE"));
							} else if ("16".equals(docType)) {
								customer.setCin(rs.getString("CUSTDOCTITLE"));
							} else if ("17".equals(docType)) {
								customer.setDin(rs.getString("CUSTDOCTITLE"));
							} else if ("18".equals(docType)) {
								customer.setRegistrationNo(rs.getString("CUSTDOCTITLE"));
							} else if ("19".equals(docType)) {
								customer.setCaNumber(rs.getString("CUSTDOCTITLE"));
							}

							customer.setEmpoyerName(rs.getString("EMPNAME"));
							customer.setAccountNumber(rs.getString("ACCOUNTNUMBER"));
							customer.setBatchID(batchId);
							customer.setSourceSysId(SOURCE_SYSTEM_ID);
							customer.setSourceSystem(SOURCE_SYSTEM);
							customer.setInsertTs(appDate);
							customer.setSegment("CF");
						}
						return customers;

					}
				});
	}

	private void setAddresses(Map<String, PosidexCustomer> customers) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select");
		sql.append(" CA.CUSTID,");
		sql.append(" CUSTADDRTYPE,");
		sql.append(" ADDRESS_TYPE,");
		sql.append(" CUSTADDRHNBR,");
		sql.append(" CUSTFLATNBR,");
		sql.append(" CUSTADDRLINE1,");
		sql.append(" CUSTADDRPROVINCE,");
		sql.append(" CUSTADDRCITY,");
		sql.append(" CUSTADDRZIP,");
		sql.append(" CUSTADDRSTREET,");
		sql.append(" CUSTADDRLINE1,");
		sql.append(" PROCESS_TYPE");
		sql.append(" from CUSTOMERADDRESSES CA");
		sql.append(" LEFT JOIN PSX_DEDUP_EOD_CUST_ADDR_DTL PC ON PC.CUSTOMER_NO = CA.CUSTID");
		sql.append(" AND PC.ADDRESS_TYPE = CA.CUSTADDRTYPE");
		sql.append(" WHERE CA.CUSTID IN (select CUST_ID FROM POSIDEX_CUSTOMERS)");
		sql.append("ORDER BY CUSTADDRPRIORITY DESC");

		extractAddresses(customers, sql);
	}

	private void extractAddresses(Map<String, PosidexCustomer> customers, StringBuilder sql)
			throws DataAccessException {
		parameterJdbcTemplate.query(sql.toString(), paramMa, new RowCallbackHandler() {
			Map<String, List<CustomerPhoneNumber>> phoneNumbers = getPhoneNumbers();
			Map<String, List<CustomerEMail>> email = getEmail();
			PosidexCustomerAddress address = null;
			PosidexCustomer customer = null;

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				customer = customers.get(rs.getString("CUSTID"));

				address = new PosidexCustomerAddress();
				address.setCustomerId(customer.getCustomerId());
				address.setBatchID(customer.getBatchID());
				address.setSourceSysId(customer.getSourceSysId());
				address.setSourceSystem(customer.getSourceSystem());
				address.setSegment(customer.getSegment());
				address.setProcessFlag(customer.getProcessFlag());
				address.setCustomerNo(customer.getCustomerNo());

				address.setAddresstype(rs.getString("CUSTADDRTYPE"));
				address.setAddress1(rs.getString("CUSTADDRHNBR"));
				address.setAddress2(rs.getString("CUSTFLATNBR"));
				address.setAddress3(rs.getString("CUSTADDRLINE1"));
				address.setState(rs.getString("CUSTADDRPROVINCE"));
				address.setCity(rs.getString("CUSTADDRCITY"));
				address.setPin(rs.getString("CUSTADDRZIP"));

				if (address.getPin() == null) {
					address.setPin("0");
				}

				List<CustomerPhoneNumber> list = phoneNumbers.get(address.getCustomerNo());

				if (list != null) {
					for (CustomerPhoneNumber phoneNumber : list) {
						if (phoneNumber == null) {
							continue;
						}

						if (phoneNumber.getPhoneNumber().length() > 10) {
							if (address.getLandline1() == null) {
								address.setLandline1(phoneNumber.getPhoneNumber());
							} else if (address.getLandline2() == null) {
								address.setLandline2(phoneNumber.getPhoneNumber());
							}
						} else {
							if (address.getMobile() == null) {
								address.setMobile((phoneNumber.getPhoneNumber()));
							}
						}
					}
				}

				List<CustomerEMail> eMaillist = email.get(address.getCustomerNo());

				if (eMaillist != null) {
					for (CustomerEMail eMail : eMaillist) {
						if (eMail == null) {
							continue;
						}
						if (address.getEmail() == null) {
							address.setEmail(eMail.getCustEMail());
						}
					}
				}

				address.setArea(rs.getString("CUSTADDRSTREET"));
				address.setLandmark(rs.getString("CUSTADDRLINE1"));

				if (rs.getString("PROCESS_TYPE") == null) {
					address.setProcessType("I");
				} else {
					address.setProcessType("U");
				}

				customer.getPosidexCustomerAddress().add(address);

			}
		});
	}

	private Map<String, List<CustomerPhoneNumber>> getPhoneNumbers() {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT PHONECUSTID, PHONETYPECODE, PHONENUMBER, PHONETYPEPRIORITY");
		sql.append(" from CUSTOMERPHONENUMBERS");
		sql.append(" WHERE PHONECUSTID IN (select CUST_ID FROM POSIDEX_CUSTOMERS)");
		sql.append(" ORDER BY PHONETYPEPRIORITY DESC ");

		return extractPhoneNumbers(sql);
	}

	private Map<String, List<CustomerPhoneNumber>> extractPhoneNumbers(StringBuilder sql) {
		return parameterJdbcTemplate.query(sql.toString(), paramMa,
				new ResultSetExtractor<Map<String, List<CustomerPhoneNumber>>>() {

					@Override
					public Map<String, List<CustomerPhoneNumber>> extractData(ResultSet rs)
							throws SQLException, DataAccessException {

						CustomerPhoneNumber phoneNumber = null;
						Map<String, List<CustomerPhoneNumber>> phoneTypes = new ConcurrentHashMap<>();

						List<CustomerPhoneNumber> list;

						while (rs.next()) {
							phoneNumber = new CustomerPhoneNumber();

							phoneNumber.setPhoneCustID(rs.getLong("PHONECUSTID"));
							phoneNumber.setPhoneTypeCode(rs.getString("PHONETYPECODE"));
							phoneNumber.setPhoneNumber(rs.getString("PHONENUMBER"));
							phoneNumber.setPhoneTypePriority(rs.getInt("PHONETYPEPRIORITY"));

							list = phoneTypes.get(String.valueOf(phoneNumber.getPhoneCustID()));
							if (list == null) {
								list = new ArrayList<>();
								phoneTypes.put(String.valueOf(phoneNumber.getPhoneCustID()), list);
							}

							list.add(phoneNumber);
						}
						return phoneTypes;
					}

				});
	}

	private Map<String, List<CustomerEMail>> getEmail() {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT CUSTID, CUSTEMAILTYPECODE, CUSTEMAIL, CUSTEMAILPRIORITY");
		sql.append(" from CUSTOMEREMAILS");
		sql.append(" WHERE CUSTID IN (select CUST_ID FROM POSIDEX_CUSTOMERS)");
		sql.append(" ORDER BY CUSTEMAILPRIORITY DESC ");

		return extractEMails(sql);
	}

	private Map<String, List<CustomerEMail>> extractEMails(StringBuilder sql) {
		return parameterJdbcTemplate.query(sql.toString(), paramMa,
				new ResultSetExtractor<Map<String, List<CustomerEMail>>>() {

					@Override
					public Map<String, List<CustomerEMail>> extractData(ResultSet rs)
							throws SQLException, DataAccessException {

						CustomerEMail eMail = null;
						Map<String, List<CustomerEMail>> emailTypes = new HashMap<>();

						List<CustomerEMail> list = null;

						while (rs.next()) {
							eMail = new CustomerEMail();

							eMail.setCustID(rs.getLong("CUSTID"));
							eMail.setCustEMailTypeCode(rs.getString("CUSTEMAILTYPECODE"));
							eMail.setCustEMail(rs.getString("CUSTEMAIL"));
							eMail.setCustEMailPriority(rs.getInt("CUSTEMAILPRIORITY"));

							list = emailTypes.get(String.valueOf(eMail.getCustID()));

							if (list == null) {
								list = new ArrayList<>();
								emailTypes.put(String.valueOf(eMail.getCustID()), list);
							}
							list.add(eMail);
						}
						return emailTypes;
					}

				});

	}

	private void setLoans(Map<String, PosidexCustomer> customers) {
		StringBuilder sql = new StringBuilder();

		sql.append(" select FM.CUSTID, FM.FINREFERENCE, FM.CUSTOMER_TYPE, FM.FINTYPE, PROCESS_TYPE");
		sql.append(" from ");
		sql.append(" (SELECT CUSTID, FM.FINREFERENCE, FM.FINTYPE, 'P' CUSTOMER_TYPE from FINANCEMAIN FM");
		sql.append(" UNION ALL");
		sql.append(" SELECT C.CUSTID, FM.FINREFERENCE, FM.FINTYPE, 'G' CUSTOMER_TYPE from FINGUARANTORSDETAILS G");
		sql.append(" INNER JOIN FINANCEMAIN FM ON FM.FINREFERENCE = G.FINREFERENCE");
		sql.append(" INNER JOIN CUSTOMERS C ON C.CUSTCIF = G.GUARANTORCIF");
		sql.append(" UNION ALL");
		sql.append(" SELECT C.CUSTID, FM.FINREFERENCE, FM.FINTYPE,'C' CUSTOMER_TYPE from FINJOINTACCOUNTDETAILS J");
		sql.append(" INNER JOIN FINANCEMAIN FM ON FM.FINREFERENCE = J.FINREFERENCE");
		sql.append(" INNER JOIN CUSTOMERS C ON  C.CUSTCIF = J.CUSTCIF");
		sql.append(" )FM ");
		sql.append(
				" LEFT JOIN PSX_DEDUP_EOD_CUST_LOAN_DTL PCL ON PCL.CUSTOMER_NO = FM.CUSTID AND PCL.LAN_NO = FM.FINREFERENCE");
		sql.append(" AND PCL.CUSTOMER_TYPE = FM.CUSTOMER_TYPE ");
		sql.append(" WHERE FM.CUSTID IN (select CUST_ID FROM POSIDEX_CUSTOMERS)");

		extractLoans(customers, sql.toString());

	}

	private void extractLoans(Map<String, PosidexCustomer> customers, String sql) {
		parameterJdbcTemplate.query(sql, paramMa, new RowCallbackHandler() {
			PosidexCustomerLoan loan = null;
			PosidexCustomer customer = null;

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				customer = customers.get(rs.getString("CUSTID"));

				loan = new PosidexCustomerLoan();
				loan.setCustomerId(customer.getCustomerId());
				loan.setBatchID(customer.getBatchID());
				loan.setSourceSysId(customer.getSourceSysId());
				loan.setSourceSystem(customer.getSourceSystem());
				loan.setSegment(customer.getSegment());
				loan.setProcessFlag(customer.getProcessFlag());
				loan.setCustomerNo(customer.getCustomerNo());

				loan.setLanNo(rs.getString("FINREFERENCE"));
				loan.setProductCode(rs.getString("FINTYPE"));
				loan.setCustomerType(rs.getString("CUSTOMER_TYPE"));

				try {
					loan.setApplnNo(StringUtils.substring(loan.getLanNo(), loan.getLanNo().length() - 7,
							loan.getLanNo().length()));
				} catch (Exception e) {
					// TODO: handle exception
				}

				if (rs.getString("PROCESS_TYPE") == null) {
					loan.setProcessType("I");
				} else {
					loan.setProcessType("U");
				}
				customer.getPosidexCustomerLoans().add(loan);

			}
		});
	}

	private void loadCount() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT count(*) from POSIDEX_CUSTOMERS");

		try {
			totalRecords = jdbcTemplate.queryForObject(sql.toString(), Integer.class);
			EXTRACT_STATUS.setTotalRecords(totalRecords);
			logger.debug("Total Records " + totalRecords);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	private void loadDescrepancyCount() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT count(*) from POSIDEX_CUSTOMERS where CUST_ID not in (select CUSTID from customers) ");

		try {
			int descrepancyCount = jdbcTemplate.queryForObject(sql.toString(), Integer.class);
			processedCount = processedCount + descrepancyCount;
			failedCount = failedCount + descrepancyCount;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

	}

	private Date getLatestRunDate() {
		StringBuilder sql = new StringBuilder();
		sql.append("select Max(INSERT_TIMESTAMP) from PUSH_PULL_CONTROL_T");
		try {
			return jdbcTemplate.queryForObject(sql.toString(), Date.class);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return null;
	}

	private void prepareCustomers() {
		MapSqlParameterSource parmMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();

		sql.append(" INSERT INTO POSIDEX_CUSTOMERS");
		sql.append(" SELECT DISTINCT CUSTID, CUSTCIF, CUSTCTGCODE, :EXTRACTED_ON");
		sql.append(" FROM (select C.CUSTID, C.CUSTCIF, C.CUSTCTGCODE  from (");
		sql.append(" select C.CUSTID from CUSTOMERS C");
		sql.append(" LEFT JOIN FINANCEMAIN FM ON FM.CUSTID = C.CUSTID");
		sql.append(" LEFT JOIN FINGUARANTORSDETAILS G ON G.GUARANTORCIF = C.CUSTCIF");
		sql.append(" LEFT JOIN FINJOINTACCOUNTDETAILS J ON J.CUSTCIF = C.CUSTCIF");

		if (lastRunDate != null) {
			sql.append(" WHERE (C.LASTMNTON > (select Max(INSERT_TIMESTAMP) from PUSH_PULL_CONTROL_T)");
			sql.append(" OR (FM.LASTMNTON > (select Max(INSERT_TIMESTAMP) from PUSH_PULL_CONTROL_T))");
			sql.append(" OR (G.LASTMNTON > (select Max(INSERT_TIMESTAMP) from PUSH_PULL_CONTROL_T))");
			sql.append(" OR (J.LASTMNTON > (select Max(INSERT_TIMESTAMP) from PUSH_PULL_CONTROL_T))");
			sql.append(" )");
		}

		sql.append(" UNION ALL");
		sql.append(" select DISTINCT C.CUSTID from CUSTOMERS C");
		sql.append(" WHERE C.CUSTID NOT IN (");
		sql.append(" SELECT CUSTOMER_NO FROM PSX_DEDUP_EOD_CUST_DEMO_DTL");
		sql.append(" UNION ALL");
		sql.append(" SELECT CUSTOMER_NO FROM PSX_DEDUP_EOD_CUST_LOAN_DTL");
		sql.append(")");
		sql.append(" ) T");
		sql.append(" INNER JOIN CUSTOMERS C ON C.CUSTID = T.CUSTID");
		sql.append(" WHERE C.CUSTCOREBANK IS NOT NULL) T");
		sql.append(" WHERE CUSTID NOT IN (SELECT CUST_ID FROM POSIDEX_CUSTOMERS)");

		parmMap.addValue("LASTMNTON", lastRunDate);
		parmMap.addValue("EXTRACTED_ON", appDate);

		parameterJdbcTemplate.update(sql.toString(), parmMap);

	}

	private long logHeader() {
		final KeyHolder keyHolder = new GeneratedKeyHolder();

		try {
			MapSqlParameterSource paramMap;
			StringBuilder sql = new StringBuilder();
			sql.append(" INSERT INTO PUSH_PULL_CONTROL_T (STATUS, INSERT_TIMESTAMP) VALUES(");
			sql.append(":STATUS, :INSERT_TIMESTAMP)");

			paramMap = new MapSqlParameterSource();
			paramMap.addValue("STATUS", "S");
			paramMap.addValue("INSERT_TIMESTAMP", DateUtil.getSysDate());

			parameterJdbcTemplate.update(sql.toString(), paramMap, keyHolder, new String[] { "BATCHID" });

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return keyHolder.getKey().longValue();
	}

	private void updateHeader() {
		MapSqlParameterSource paramMap;
		StringBuilder sql = new StringBuilder();

		sql.append(" UPDATE PUSH_PULL_CONTROL_T  SET STATUS = :STATUS, COMPLETION_TIMESTAMP = :COMPLETION_TIMESTAMP,");
		sql.append(" ERR_DESCRIPTION = :ERR_DESCRIPTION");
		sql.append(" WHERE BATCHID = :BATCHID");

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("STATUS", "I");
		paramMap.addValue("COMPLETION_TIMESTAMP", DateUtil.getSysDate());
		paramMap.addValue("BATCHID", batchId);
		paramMap.addValue("ERR_DESCRIPTION", summary);

		try {
			parameterJdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	private void loaddefaults() {
		SOURCE_SYSTEM_ID = parameterCodes.get("POSIDEX_SOURCE_SYSTEM_ID");
		SOURCE_SYSTEM = parameterCodes.get("POSIDEX_SOURCE_SYSTEM");
	}

	private void loadParameters() {
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		paramMap = new MapSqlParameterSource();

		sql.append("SELECT SYSPARMCODE, SYSPARMVALUE FROM SMTPARAMETERS where SYSPARMCODE like :SYSPARMCODE");
		paramMap.addValue("SYSPARMCODE", "POSIDEX_%");

		parameterJdbcTemplate.query(sql.toString(), paramMap, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				parameterCodes.put(rs.getString("SYSPARMCODE"), rs.getString("SYSPARMVALUE"));
			}
		});
	}

	@Override
	protected MapSqlParameterSource mapData(ResultSet rs) throws Exception {
		return null;
	}

}
