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
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennanttech.pff.core.Literal;
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
		sql.append(" left join CIBIL_DOCUMENT_TYPES dt on DT.CODE = DOC.CUSTDOCCATEGORY");
		sql.append(" left join CIBIL_DOCUMENT_TYPES_MAPPING dm on dm.code = dt.code");
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
	public List<CustomerAddres> getCustomerAddres(long customerId) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();
		sql.append(" select coalesce(cat.code, '04') CustAddrType, CustAddrHNbr, CustFlatNbr, CustAddrStreet,");
		sql.append(
				" CustAddrLine1, CustAddrLine2, coalesce(sm.code, '99') CustAddrProvince, CustAddrZIP, CustAddrType");
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
		sql.append(" select  CustID, FinReference, FinStartDate, LatestRpyDate, Current_Balance, Amount_Overdue,");
		sql.append(" CurODDays, ClosingStatus, collateral_Value, CollateralType, RepayProfitRate, NumberOfTerms,");
		sql.append(" FirstRepay, WrittenOff_Amount, writtenOff_Principal, settelement_Amount, payment_Amount,");
		sql.append(" RepayFrq, ownership, finType");
		sql.append(" from CIBIL_CUSTOMER_LOANS_VIEW cs");
		sql.append(" where cs.FinReference = :FinReference and CUSTID = :CUSTID");

		paramMap.addValue("FinReference", finReference);
		paramMap.addValue("CUSTID", customerId);

		RowMapper<FinanceEnquiry> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceEnquiry.class);
		return this.namedJdbcTemplate.queryForObject(sql.toString(), paramMap, rowMapper);
	}

	@Override
	public long logFileInfo(String fileName, String memberId, String memberName, String memberPwd) {
		
		final KeyHolder keyHolder = new GeneratedKeyHolder();

		logger.trace(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder("Insert Into CIBIL_FILE_INFO");
		sql.append(" (FILE_NAME, MEMBER_ID, MEMBER_NAME, MEMBER_PASSWORD, CREATEDON, STATUS)");
		sql.append(" Values(:FILE_NAME, :MEMBER_ID, :MEMBER_NAME, :MEMBER_PASSWORD, :CREATEDON, :STATUS)");

		paramMap.addValue("MEMBER_ID", memberId);
		paramMap.addValue("FILE_NAME", fileName);

		paramMap.addValue("MEMBER_NAME", memberName);
		paramMap.addValue("MEMBER_PASSWORD", memberPwd);
		paramMap.addValue("CREATEDON", DateUtility.getAppDate());
		paramMap.addValue("STATUS", "I");

		try {
			this.namedJdbcTemplate.update(sql.toString(), paramMap, keyHolder, new String[] { "ID" });
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return keyHolder.getKey().longValue();

	}
	
	@Override
	public void updateFileStatus(long headerid,String status) {
		logger.trace(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder("UPDATE  CIBIL_FILE_INFO");
		sql.append(" SET STATUS = :STATUS WHERE ID = :ID");
		
		
		if("S".equals(status)) {
			paramMap.addValue("STATUS", "C");
		} else {
			paramMap.addValue("STATUS", "F");
		}
		
		paramMap.addValue("ID", headerid);

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

}
