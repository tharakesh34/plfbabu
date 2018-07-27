package com.pennant.backend.dao.cibil;

import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennanttech.dataengine.model.DataEngineLog;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.util.DateUtil;

public class CIBILDAOImpl implements CIBILDAO {
	private static Logger logger = Logger.getLogger(CIBILDAOImpl.class);

	private DataSource dataSource;
	private NamedParameterJdbcTemplate namedJdbcTemplate;

	@Override
	public CustomerDetails getCustomerDetails(long customerId) {
		CustomerDetails customerDetails = new CustomerDetails();

		try {
			customerDetails.setCustomer(getCustomer(customerId));
			customerDetails.setCustomerDocumentsList(getCustomerDocuments(customerId));
			customerDetails.setCustomerPhoneNumList(getCustomerPhoneNumbers(customerId));
			customerDetails.setCustomerEMailList(getCustomerEmails(customerId));
			customerDetails.setAddressList(getCustomerAddres(customerId));
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			customerDetails = null;
		}

		return customerDetails;
	}

	@Override
	public Customer getCustomer(long customerId) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();
		sql.append(
				" select custShrtName, CustSalutationCode, CustFName, CustMName, CustLName, custDOB, custGenderCode from customers");
		sql.append(" where CUSTID = :CUSTID");

		paramMap.addValue("CUSTID", customerId);

		RowMapper<Customer> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);
		return this.namedJdbcTemplate.queryForObject(sql.toString(), paramMap, rowMapper);
	}

	@Override
	public List<CustomerDocument> getCustomerDocuments(long customerId) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();

		sql.append(" select dt.code CustDocCategory, CustDocTitle, CustDocIssuedOn, CustDocExpDate");
		sql.append(" from CustomerDocuments doc");
		sql.append(" INNER JOIN CIBIL_DOCUMENT_TYPES_MAPPING DM ON DM.DOCTYPECODE = DOC.CUSTDOCCATEGORY");
		sql.append(" INNER JOIN CIBIL_DOCUMENT_TYPES DT ON DT.CODE = DM.CODE");
		sql.append(" where CUSTID = :CUSTID");

		paramMap.addValue("CUSTID", customerId);

		RowMapper<CustomerDocument> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerDocument.class);

		return this.namedJdbcTemplate.query(sql.toString(), paramMap, rowMapper);
	}

	@Override
	public List<CustomerPhoneNumber> getCustomerPhoneNumbers(long customerId) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();

		sql.append(" select coalesce(cpt.code, '00') PhoneTypeCode, cp.PhoneNumber");
		sql.append(" from CustomerPhoneNumbers cp");
		sql.append(" left join CIBIL_PHONE_TYPES_MAPPING pm on pm.PHONETYPECODE=cp.PHONETYPECODE");
		sql.append(" left join CIBIL_PHONE_TYPES cpt on CPT.CODE = pm.code");
		sql.append(" where PHONECUSTID = :PHONECUSTID");

		paramMap.addValue("PHONECUSTID", customerId);

		RowMapper<CustomerPhoneNumber> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CustomerPhoneNumber.class);

		return this.namedJdbcTemplate.query(sql.toString(), paramMap, rowMapper);
	}

	@Override
	public List<CustomerEMail> getCustomerEmails(long customerId) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();

		sql.append(" select CustEMail from CUSTOMEREMAILS");
		sql.append(" where CUSTID = :CUSTID");

		paramMap.addValue("CUSTID", customerId);

		RowMapper<CustomerEMail> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerEMail.class);

		return this.namedJdbcTemplate.query(sql.toString(), paramMap, rowMapper);
	}

	@Override
	public List<CustomerAddres> getCustomerAddres(long customerId) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();
		sql.append(" select coalesce(cat.code, '04') CustAddrType, CustAddrHNbr, CustFlatNbr, CustAddrStreet,");
		sql.append(" CustAddrLine1, CustAddrLine2, coalesce(sm.code, '99') CustAddrProvince, CustAddrZIP");
		sql.append(" from CustomerAddresses ca");
		sql.append(" left join CIBIL_ADDRESS_TYPES_MAPPING am on am.ADDRTYPECODE=ca.CUSTADDRTYPE");
		sql.append(" left join CIBIL_ADDRESS_TYPES cat on CAT.CODE=am.code");
		sql.append(" left join CIBIL_STATES_MAPPING sm on sm.CPPROVINCE = ca.CUSTADDRPROVINCE ");
		sql.append(" where CUSTID = :CUSTID");

		paramMap.addValue("CUSTID", customerId);

		RowMapper<CustomerAddres> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerAddres.class);
		return this.namedJdbcTemplate.query(sql.toString(), paramMap, rowMapper);
	}

	@Override
	public FinanceEnquiry getFinanceSummary(String finReference, long customerId) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();
		sql.append(" select  CustID, FinReference, FinStartDate, FinApprovedDate, LatestRpyDate,");
		sql.append("  CurODDays as ODDays, ClosingStatus, ");
		sql.append("  Future_schedule_prin, Instalment_Due, Instalment_Paid, Bounce_Due, Bounce_Paid, ");
		sql.append(
				"  Late_Payment_Penalty_Due, Late_Payment_Penalty_Paid, Total_Pri_Schd, Total_Pri_Paid, Total_Pft_Schd, ");
		sql.append("  Total_Pft_Paid, Excess_Amount, Excess_Amt_Paid, ");
		sql.append("  Ownership, FinType, Finassetvalue, CustIncome");
		sql.append("  from CIBIL_CUSTOMER_LOANS_VIEW cs");
		sql.append("  where cs.FinReference = :FinReference and CUSTID = :CUSTID");

		paramMap.addValue("FinReference", finReference);
		paramMap.addValue("CUSTID", customerId);

		RowMapper<FinanceEnquiry> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceEnquiry.class);
		return this.namedJdbcTemplate.queryForObject(sql.toString(), paramMap, rowMapper);
	}

	@Override
	public long logFileInfo(String fileName, String memberId, String memberName, String memberPwd, String reportPath) {

		final KeyHolder keyHolder = new GeneratedKeyHolder();

		logger.trace(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder("Insert Into CIBIL_FILE_INFO");
		sql.append(" (FILE_NAME, MEMBER_ID, MEMBER_NAME, MEMBER_PASSWORD, CREATEDON, STATUS, FILE_LOCATION, START_TIME)");
		sql.append(" Values(:FILE_NAME, :MEMBER_ID, :MEMBER_NAME, :MEMBER_PASSWORD, :CREATEDON, :STATUS,");
		sql.append(":FILE_LOCATION, :START_TIME)");

		paramMap.addValue("MEMBER_ID", memberId);
		paramMap.addValue("FILE_NAME", fileName);

		paramMap.addValue("MEMBER_NAME", memberName);
		paramMap.addValue("MEMBER_PASSWORD", memberPwd);
		paramMap.addValue("CREATEDON", DateUtility.getAppDate());
		paramMap.addValue("STATUS", "I");
		paramMap.addValue("FILE_LOCATION", reportPath);
		paramMap.addValue("START_TIME",  DateUtil.getSysDate());

		try {
			this.namedJdbcTemplate.update(sql.toString(), paramMap, keyHolder, new String[] { "id" });
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return keyHolder.getKey().longValue();

	}
	
	@Override
	public void logFileInfoException(long id, String finReference, String reason) {

		logger.trace(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder("Insert Into CIBIL_FILE_INFO_LOG");
		sql.append(" (ID, FINREFERENCE, REASON, STATUS)");
		sql.append(" Values(:ID, :FINREFERENCE, :REASON, :STATUS)");

		paramMap.addValue("ID", id);
		paramMap.addValue("FINREFERENCE", finReference);
		paramMap.addValue("REASON", reason);
		paramMap.addValue("STATUS", "F");

		try {
			this.namedJdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.trace(Literal.LEAVING);

	}
	
	@Override
	public DataEngineStatus getLatestExecution() {
		DataEngineStatus dataStatus = null;
		RowMapper<DataEngineStatus> rowMapper = null;
		StringBuilder sql = null;

		sql = new StringBuilder("Select ID, TOTAL_RECORDS, PROCESSED_RECORDS, SUCCESS_RECORDS, FAILED_RECORDS,");
		sql.append(" REMARKS, START_TIME, END_TIME from CIBIL_FILE_INFO");
		sql.append(" where Id = (Select MAX(Id) from CIBIL_FILE_INFO)");

		rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DataEngineStatus.class);

		try {
			dataStatus = namedJdbcTemplate.queryForObject(sql.toString(), new MapSqlParameterSource(), rowMapper);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (dataStatus != null) {
			List<DataEngineLog> list = getExceptions(dataStatus.getId());
			if (list != null && !list.isEmpty()) {
				dataStatus.setDataEngineLogList(list);
			}
		} else {
			dataStatus = new DataEngineStatus();
		}

		return dataStatus;
	}

	public List<DataEngineLog> getExceptions(long Id) {
		RowMapper<DataEngineLog> rowMapper = null;
		MapSqlParameterSource parameterMap = null;
		StringBuilder sql = null;

		try {
			sql = new StringBuilder("Select * from CIBIL_FILE_INFO_LOG where ID = :ID");

			parameterMap = new MapSqlParameterSource();
			parameterMap.addValue("ID", Id);

			rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DataEngineLog.class);

			return namedJdbcTemplate.query(sql.toString(), parameterMap, rowMapper);

		} catch (Exception e) {
		} finally {
			rowMapper = null;
			sql = null;
		}
		return null;
	}
	
	@Override
	public void updateFileStatus(long headerid, String status, long totalRecords, long processedRecords,
			long successCount, long failedCount, String remarks) {
		logger.trace(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder("UPDATE  CIBIL_FILE_INFO");
		sql.append(" SET STATUS = :STATUS , TOTAL_RECORDS = :TOTALRECORDS, PROCESSED_RECORDS = :PROCESSEDRECORDS,");
		sql.append(" SUCCESS_RECORDS = :SUCCESSCOUNT, FAILED_RECORDS = :FAILEDCOUNT, REMARKS = :REMARKS,");
		sql.append("END_TIME = :END_TIME WHERE ID = :ID");

		if ("S".equals(status)) {
			paramMap.addValue("STATUS", "C");
		} else {
			paramMap.addValue("STATUS", "F");
		}

		paramMap.addValue("TOTALRECORDS", totalRecords);
		paramMap.addValue("PROCESSEDRECORDS", processedRecords);
		paramMap.addValue("SUCCESSCOUNT", successCount);
		paramMap.addValue("FAILEDCOUNT", failedCount);
		paramMap.addValue("REMARKS", remarks);
		paramMap.addValue("ID", headerid);
		paramMap.addValue("END_TIME", DateUtil.getSysDate());

		try {
			this.namedJdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.trace(Literal.LEAVING);

	}
	
	@Override
	public void deleteDetails() {
		logger.debug(Literal.ENTERING);
		try {
			namedJdbcTemplate.update("TRUNCATE TABLE CIBIL_CUSTOMER_EXTRACT", new MapSqlParameterSource());
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public int extractCustomers() throws Exception {
		StringBuilder sql = new StringBuilder();

		sql.append(" INSERT INTO CIBIL_CUSTOMER_EXTRACT ");
		sql.append(" SELECT CUSTID, FINREFERENCE, OWNERSHIP, LATESTRPYDATE FROM CIBIL_CUSTOMER_EXTARCT_VIEW");
		sql.append(" WHERE LATESTRPYDATE >= :LATESTRPYDATE ");

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("LATESTRPYDATE", DateUtil.addMonths(DateUtility.getAppDate(), -36));

		try {
			return namedJdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception("Unable to insert CIBIL Details");
		}
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.namedJdbcTemplate = new NamedParameterJdbcTemplate(this.dataSource);
	}

	@Override
	public EventProperties getEventProperties(String configName, String eventType) {
		RowMapper<EventProperties> rowMapper = null;
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		StringBuilder sql = null;
		try {
			sql = new StringBuilder("SELECT DEP.* FROM DATA_ENGINE_EVENT_PROPERTIES DEP");
			sql.append(" INNER JOIN DATA_ENGINE_CONFIG DC ON DC.ID = DEP.CONFIG_ID");
			sql.append("  Where DC.NAME = :NAME AND DEP.STORAGE_TYPE = :STORAGE_TYPE");

			rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(EventProperties.class);
			parameterSource.addValue("NAME", configName);
			parameterSource.addValue("STORAGE_TYPE", eventType);
			return namedJdbcTemplate.queryForObject(sql.toString(), parameterSource, rowMapper);

		} catch (Exception e) {
			logger.warn("Configuration details not available for " + configName);
		} 

		return null;
	}

}