package com.pennanttech.bajaj.process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
import com.pennanttech.bajaj.model.posidex.PosidexCustomer;
import com.pennanttech.bajaj.model.posidex.PosidexCustomerAddress;
import com.pennanttech.bajaj.model.posidex.PosidexCustomerLoan;
import com.pennanttech.dataengine.DatabaseDataEngine;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.baja.BajajInterfaceConstants;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.util.DateUtil;

public class PosidexRequestProcess extends DatabaseDataEngine {
	private static final Logger logger = Logger.getLogger(PosidexRequestProcess.class);

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

	private int batchSize = 50000;
	private MapSqlParameterSource paramMa = null;
	private boolean localUpdate = false;

	public PosidexRequestProcess(DataSource dataSource, long userId, Date valueDate, Date appDate) {
		super(dataSource, App.DATABASE.name(), userId, true, valueDate, BajajInterfaceConstants.POSIDEX_REQUEST_STATUS);
		this.appDate = appDate;
	}

	@Override
	protected void processData() {
		logger.debug(Literal.ENTERING);

		paramMa = new MapSqlParameterSource();
		paramMa.addValue("ROWNUM", batchSize);

		lastRunDate = getLatestRunDate();

		loadParameters();

		prepareCustomers();

		batchId = logHeader();

		loadCount();

		loaddefaults();

		try {
			do {
				extractData();
			} while (totalRecords > 0 && totalRecords != processedCount);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			updateRemarks(new StringBuilder());
			updateHeader();
		}
	}

	public void extractData() throws SQLException {
		Map<Long, PosidexCustomer> customers = getCustomers();
		setAddresses(customers);
		setLoans(customers);
		
		for (PosidexCustomer customer : customers.values()) {
			TransactionStatus txnStatus = transManager.getTransaction(transDef);
			try {
				saveOrUpdate(customer);
				successCount++;
				transManager.commit(txnStatus);
			} catch (Exception e) {
				transManager.rollback(txnStatus);
				saveBatchLog(String.valueOf(customer.getCustomerNo()), "F", e.getMessage());
				failedCount++;
				logger.error(Literal.EXCEPTION, e);
			} finally {
				processedCount++;
				txnStatus.flush();
				txnStatus = null;
			}
		}
	}

	private void saveOrUpdate(PosidexCustomer customer) {
		try {
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
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	private void delete(PosidexCustomer cusotemr) throws Exception {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("CUST_ID", cusotemr.getCustomerNo());
		jdbcTemplate.update("DELETE FROM POSIDEX_CUSTOMERS WHERE CUST_ID=:CUST_ID", paramMap);
	}

	private void save(PosidexCustomer cusotemr, boolean stage) {
		StringBuilder sql = new StringBuilder();
		sql.append("Insert into ").append(CUSTOMER_DETAILS);
		sql.append(" values (");
		sql.append(" :BatchID,");
		sql.append(" :CustomerNo,");
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
			jdbcTemplate.update(sql.toString(), beanParameters);
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
		sql.append(" :CustomerNo,");
		sql.append(" :CustCoreBank");
		sql.append(")");

		if (stage) {
			jdbcTemplate.update(sql.toString(), beanParameters);
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
		sql.append(" WHERE CustomerNo = :CustomerNo");

		sql.append(")");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(cusotemr);
		jdbcTemplate.update(sql.toString(), beanParameters);

	}

	private void save(PosidexCustomerAddress address, boolean stage) {
		StringBuilder sql = new StringBuilder();
		sql.append("Insert into ").append(CUSTOMER_ADDR_DETAILS);
		sql.append(" values (");
		sql.append(" :BatchID,");
		sql.append(" :CustomerNo,");
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
		sql.append(" :EMail,");
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
			jdbcTemplate.update(sql.toString(), beanParameters);
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
		sql.append(" EMAIL=:EMail,");
		sql.append(" PROCESS_FLAG=:ProcessFlag,");
		sql.append(" ERROR_CODE=:ErrorCode,");
		sql.append(" ERROR_DESC=:ErrorDesc,");
		sql.append(" CUSTOMER_ID=:CustomerId,");
		sql.append(" SOURCE_SYSTEM=:SourceSystem,");
		sql.append(" PSX_BATCH_ID=:PsxBatchID,");
		sql.append(" EOD_BATCH_ID=:EodBatchID");
		sql.append(" WHERE CUSTOMER_NO = :CustomerNo AND ADDRESS_TYPE=:Addresstype");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(addresses);
		jdbcTemplate.update(sql.toString(), beanParameters);

	}

	private void save(PosidexCustomerLoan loan, boolean stage) {
		StringBuilder sql = new StringBuilder();
		sql.append("Insert into ").append(CUSTOMER_LOAN_DETAILS);
		sql.append(" values (");
		sql.append(" :BatchID,");
		sql.append(" :CustomerNo,");
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
				jdbcTemplate.update(sql.toString(), beanParameters);
			} else {
				destinationJdbcTemplate.update(sql.toString(), beanParameters);
			}
		} catch (Exception e) {
			e.printStackTrace();
			
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
		jdbcTemplate.update(sql.toString(), beanParameters);

	}

	private Map<Long, PosidexCustomer> getCustomers() throws SQLException {
		Map<Long, PosidexCustomer> customers = new HashMap<>(batchSize);
		StringBuilder sql = new StringBuilder();
		sql.append(" select C.CUSTID, CUSTCIF, CUSTCOREBANK, CUSTFNAME, CUSTMNAME, CUSTLNAME, CUSTSHRTNAME, CUSTDOB,");
		sql.append(" CUSTGENDERCODE, CUSTMOTHERMAIDEN, C.CUSTCTGCODE, CUSTDOCTITLE, CUSTDOCCATEGORY,");
		sql.append(" EMPNAME, ACCOUNTNUMBER, CUSTCOREBANK,");
		sql.append(" PROCESS_TYPE");
		sql.append(" FROM CUSTOMERS C");
		sql.append(" LEFT JOIN CUSTOMERDOCUMENTS CD ON CD.CUSTID = C.CUSTID");
		sql.append(" LEFT JOIN CUSTEMPLOYEEDETAIL CE ON CE.CUSTID = C.CUSTID");
		sql.append(" LEFT JOIN (select CUSTID, ACCOUNTNUMBER from CUSTOMERBANKINFO");
		sql.append(" WHERE ROWNUM  =1 order by BANKID) CBA ON CBA.CUSTID = C.CUSTID");
		sql.append(" LEFT JOIN PSX_DEDUP_EOD_CUST_DEMO_DTL PC ON PC.CUSTOMER_NO = C.CUSTID");
		sql.append(" WHERE C.CUSTID IN (select CUST_ID FROM POSIDEX_CUSTOMERS WHERE ROWNUM <= :ROWNUM)");

		return extractCustomers(customers, sql);
	}

	private Map<Long, PosidexCustomer> extractCustomers(Map<Long, PosidexCustomer> customers, StringBuilder sql) {
		return jdbcTemplate.query(sql.toString(), paramMa, new ResultSetExtractor<Map<Long, PosidexCustomer>>() {
			@Override
			public Map<Long, PosidexCustomer> extractData(ResultSet rs) throws SQLException, DataAccessException {
				String docType = null;
				while (rs.next()) {
					PosidexCustomer customer = new PosidexCustomer();
					customer.setCustomerNo(rs.getLong("CUSTID"));
					customer.setCustomerId(rs.getString("CUSTCIF"));
					customer.setFirstName(rs.getString("CUSTFNAME"));
					customer.setMiddleName(rs.getString("CUSTMNAME"));
					customer.setLastName(rs.getString("CUSTLNAME"));
					customer.setDob(rs.getDate("CUSTDOB"));
					customer.setGender(rs.getString("CUSTGENDERCODE"));
					customer.setFatherName(rs.getString("CUSTMOTHERMAIDEN"));
					customer.setProcessType(rs.getString("PROCESS_TYPE"));
					customer.setApplicantType(rs.getString("CUSTCTGCODE"));
					customer.setCustCoreBank(rs.getString("CUSTCOREBANK"));

					if (customer.getProcessType() == null) {
						customer.setProcessType("I");
					}

					if ("RETAIL".equals(customer.getApplicantType())) {
						customer.setApplicantType("I");
					} else {
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

					customers.put(customer.getCustomerNo(), customer);

				}
				return customers;

			}
		});
	}

	private void setAddresses(Map<Long, PosidexCustomer> customers) throws SQLException {
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
		sql.append(" WHERE CA.CUSTID IN (select CUST_ID FROM POSIDEX_CUSTOMERS WHERE ROWNUM <= :ROWNUM)");
		sql.append("ORDER BY CUSTADDRPRIORITY DESC");

		extractAddresses(customers, sql);
	}

	private void extractAddresses(Map<Long, PosidexCustomer> customers, StringBuilder sql)
			throws DataAccessException, SQLException {
		jdbcTemplate.query(sql.toString(), paramMa, new RowCallbackHandler() {
			Map<Long, List<CustomerPhoneNumber>> phoneNumbers = getPhoneNumbers();
			Map<Long, List<CustomerEMail>> email = getEmail();
			PosidexCustomerAddress address = null;
			PosidexCustomer customer = null;
			
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				customer = customers.get(rs.getLong("CUSTID"));

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
						if(phoneNumber == null) {
							continue;
						}
						
						if (phoneNumber.getPhoneNumber().length() > 10) {
							if (address.getLandline1() == null) {
								address.setLandline1(phoneNumber.getPhoneNumber());
								phoneNumber = null;
							} else if (address.getLandline2() == null) {
								address.setLandline2(phoneNumber.getPhoneNumber());
								phoneNumber = null;
							}
						} else {
							if (address.getMobile() == null) {
								address.setMobile((phoneNumber.getPhoneNumber()));
								phoneNumber = null;
							}
						}
					}
				}

				List<CustomerEMail> eMaillist = email.get(address.getCustomerNo());

				if (eMaillist != null) {
					for (CustomerEMail eMail : eMaillist) {
						if(eMail == null) {
							continue;
						}
						if (address.geteMail() == null) {
							address.seteMail(eMail.getCustEMail());
							eMail = null;
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

	private Map<Long, List<CustomerPhoneNumber>> getPhoneNumbers() throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT PHONECUSTID, PHONETYPECODE, PHONENUMBER, PHONETYPEPRIORITY");
		sql.append(" from CUSTOMERPHONENUMBERS");
		sql.append(" WHERE PHONECUSTID IN (select CUST_ID FROM POSIDEX_CUSTOMERS WHERE ROWNUM <= :ROWNUM)");
		sql.append(" ORDER BY PHONETYPEPRIORITY DESC ");

		return extractPhoneNumbers(sql);
	}

	private Map<Long, List<CustomerPhoneNumber>> extractPhoneNumbers(StringBuilder sql) {
		return jdbcTemplate.query(sql.toString(), paramMa,
				new ResultSetExtractor<Map<Long, List<CustomerPhoneNumber>>>() {

					@Override
					public Map<Long, List<CustomerPhoneNumber>> extractData(ResultSet rs)
							throws SQLException, DataAccessException {

						CustomerPhoneNumber phoneNumber = null;
						Map<Long, List<CustomerPhoneNumber>> phoneTypes = new ConcurrentHashMap<>();

						List<CustomerPhoneNumber> list = new CopyOnWriteArrayList<>();

						while (rs.next()) {
							phoneNumber = new CustomerPhoneNumber();

							phoneNumber.setPhoneCustID(rs.getLong("PHONECUSTID"));
							phoneNumber.setPhoneTypeCode(rs.getString("PHONETYPECODE"));
							phoneNumber.setPhoneNumber(rs.getString("PHONENUMBER"));
							phoneNumber.setPhoneTypePriority(rs.getInt("PHONETYPEPRIORITY"));
							
							list = phoneTypes.get(phoneNumber.getPhoneCustID());
							if (list == null) {
								list = new ArrayList<>();
								phoneTypes.put(phoneNumber.getPhoneCustID(), list);
							} 
							
							list.add(phoneNumber);
						}
						return phoneTypes;
					}

				});
	}

	private Map<Long, List<CustomerEMail>> getEmail() throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT CUSTID, CUSTEMAILTYPECODE, CUSTEMAIL, CUSTEMAILPRIORITY");
		sql.append(" from CUSTOMEREMAILS");
		sql.append(" WHERE CUSTID IN (select CUST_ID FROM POSIDEX_CUSTOMERS WHERE ROWNUM <= :ROWNUM)");
		sql.append(" ORDER BY CUSTEMAILPRIORITY DESC ");

		return extractEMails(sql);
	}

	private Map<Long, List<CustomerEMail>> extractEMails(StringBuilder sql) {
		return jdbcTemplate.query(sql.toString(), paramMa, new ResultSetExtractor<Map<Long, List<CustomerEMail>>>() {

			@Override
			public Map<Long, List<CustomerEMail>> extractData(ResultSet rs) throws SQLException, DataAccessException {

				CustomerEMail eMail = null;
				Map<Long, List<CustomerEMail>> emailTypes = new HashMap<>();

				List<CustomerEMail> list = null;

				while (rs.next()) {
					eMail = new CustomerEMail();

					eMail.setCustID(rs.getLong("CUSTID"));
					eMail.setCustEMailTypeCode(rs.getString("CUSTEMAILTYPECODE"));
					eMail.setCustEMail(rs.getString("CUSTEMAIL"));
					eMail.setCustEMailPriority(rs.getInt("CUSTEMAILPRIORITY"));

					list = emailTypes.get(eMail.getCustID());

					if (list == null) {
						list = new ArrayList<>();
						emailTypes.put(eMail.getCustID(), list);
					}
					list.add(eMail);
				}
				return emailTypes;
			}

		});

	}
	
	private void setLoans(Map<Long, PosidexCustomer> customers) throws SQLException {
		StringBuilder sql = new StringBuilder();

		sql.append(" select FM.CUSTID, FM.FINREFERENCE, FM.CUSTOMER_TYPE, FM.FINTYPE, PROCESS_TYPE");
		sql.append(" from ");
		sql.append(" (SELECT CUSTID, FINREFERENCE, FM.FINTYPE, 'P' CUSTOMER_TYPE from FINANCEMAIN FM");
		sql.append(" UNION ALL");
		sql.append(" SELECT GUARANTORID, FM.FINREFERENCE, FM.FINTYPE, 'G' CUSTOMER_TYPE from FINGUARANTORSDETAILS G");
		sql.append(" INNER JOIN FINANCEMAIN FM ON FM.FINREFERENCE = G.FINREFERENCE");
		sql.append(" UNION ALL");
		sql.append(
				" SELECT JOINTACCOUNTID, FM.FINREFERENCE, FM.FINTYPE, 'C' CUSTOMER_TYPE  from  FINJOINTACCOUNTDETAILS C");
		sql.append(" INNER JOIN FINANCEMAIN FM ON FM.FINREFERENCE = C.FINREFERENCE");
		sql.append(" )FM ");

		sql.append(
				" LEFT JOIN PSX_DEDUP_EOD_CUST_LOAN_DTL PCL ON PCL.CUSTOMER_NO = FM.CUSTID AND PCL.LAN_NO = FM.FINREFERENCE AND PCL.CUSTOMER_TYPE = FM.CUSTOMER_TYPE ");
		sql.append(" WHERE FM.CUSTID IN (select CUST_ID FROM POSIDEX_CUSTOMERS WHERE ROWNUM <= :ROWNUM)");

		extractLoans(customers, sql.toString());

	}

	private void extractLoans(Map<Long, PosidexCustomer> customers, String sql) {
		jdbcTemplate.query(sql.toString(), paramMa, new RowCallbackHandler() {
			PosidexCustomerLoan loan = null;
			PosidexCustomer customer = null;

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				customer = customers.get(rs.getLong("CUSTID"));

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
					loan.setApplnNo(StringUtils.substring(loan.getLanNo(), loan.getLanNo().length() - 8,
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

		MapSqlParameterSource parmMap = new MapSqlParameterSource();

		sql.append("SELECT count(*) from POSIDEX_CUSTOMERS");

		try {
			totalRecords = jdbcTemplate.queryForObject(sql.toString(), parmMap, Integer.class);
			BajajInterfaceConstants.POSIDEX_REQUEST_STATUS.setTotalRecords(totalRecords);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

	}

	private Date getLatestRunDate() {
		StringBuilder sql = new StringBuilder();
		sql.append("select Max(INSERT_TIMESTAMP) from PUSH_PULL_CONTROL_T");
		try {
			return jdbcTemplate.queryForObject(sql.toString(), new MapSqlParameterSource(), Date.class);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return null;
	}

	private void prepareCustomers() {
		MapSqlParameterSource parmMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();

		sql.append(" insert into POSIDEX_CUSTOMERS");
		sql.append(" select CUSTID, CUSTCIF, CUSTCTGCODE, :EXTRACTED_ON from CUSTOMERS C");
		sql.append(" WHERE C.CUSTCOREBANK IS NOT NULL AND CUSTID not in ( select cust_id  from Posidex_customers)");

		if (lastRunDate != null) {
			sql.append(
					"AND (C.LASTMNTON > :LASTMNTON OR (SELECT MAX(LASTMNTON) FROM FINANCEMAIN WHERE CUSTID= C.CUSTID) > :LASTMNTON)");
		}

		parmMap.addValue("LASTMNTON", lastRunDate);
		parmMap.addValue("EXTRACTED_ON", appDate);

		jdbcTemplate.update(sql.toString(), parmMap);

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

			jdbcTemplate.update(sql.toString(), paramMap, keyHolder, new String[] { "BATCHID" });

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
			jdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	/*private String getPhoneNumber(ResultSet rs) throws SQLException {
		return StringUtils.trimToEmpty(rs.getString("PHONECOUNTRYCODE"))
				.concat(StringUtils.trimToEmpty(rs.getString("PHONEAREACODE")))
				.concat(StringUtils.trimToEmpty(rs.getString("PHONENUMBER")));
	}*/

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

		jdbcTemplate.query(sql.toString(), paramMap, new RowCallbackHandler() {
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
