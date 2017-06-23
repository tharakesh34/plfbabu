package com.pennant.backend.dao.cibil;

import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

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
	private static Logger				logger	= Logger.getLogger(CIBILDAOImpl.class);

	private DataSource					dataSource;
	private NamedParameterJdbcTemplate	namedJdbcTemplate;

	@Override
	public Customer getCustomer(long customerId) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();
		sql.append(" select custShrtName, custDOB, custGenderCode from customers");
		sql.append(" where CUSTID = :CUSTID");

		paramMap.addValue("CUSTID", customerId);

		return this.namedJdbcTemplate.queryForObject(sql.toString(), paramMap,
				ParameterizedBeanPropertyRowMapper.newInstance(Customer.class));
	}

	@Override
	public List<CustomerDocument> getCustomerDocuments(long customerId) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();
		sql.append(" select CustDocCategory, CustDocIssuedOn, CustDocExpDate from CustomerDocuments");
		sql.append(" where CUSTID = :CUSTID");

		paramMap.addValue("CUSTID", customerId);

		return this.namedJdbcTemplate.query(sql.toString(), paramMap,
				ParameterizedBeanPropertyRowMapper.newInstance(CustomerDocument.class));
	}

	@Override
	public List<CustomerPhoneNumber> getCustomerPhoneNumbers(long customerId) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();
		sql.append(" select PhoneTypeCode, PhoneNumber from CustomerPhoneNumbers");
		sql.append(" where CUSTID = :CUSTID");

		paramMap.addValue("CUSTID", customerId);

		return this.namedJdbcTemplate.query(sql.toString(), paramMap,
				ParameterizedBeanPropertyRowMapper.newInstance(CustomerPhoneNumber.class));
	}

	@Override
	public List<CustomerAddres> getCustomerAddres(long customerId) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();
		sql.append(" select CustAddrHNbr, CustFlatNbr, CustAddrStreet, CustAddrLine1, CustAddrLine2, CustAddrProvince, ");
		sql.append(" CustAddrZIP, CustAddrType from  CustomerAddresses");
		sql.append(" where CUSTID = :CUSTID");

		paramMap.addValue("CUSTID", customerId);

		return this.namedJdbcTemplate.query(sql.toString(), paramMap,
				ParameterizedBeanPropertyRowMapper.newInstance(CustomerAddres.class));
	}

	@Override
	public FinanceEnquiry getFinanceSummary(String finReference) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();
		sql.append(" select  CustID, FinReference, FinStartDate, LatestRpyDate, CurrentBalance, AmountOverdue, ODDays, ClosingStatus, collateralValue ");
		sql.append(" CollateralType, RepayProfitRate, NumberOfTerms, FirstRepay, WrittenOffAmount, writtenOffPrincipal, settlementAmount, RepayFrq from  CUSTOMER_LOANS_VIEW");
		sql.append(" where FinReference = :FinReference");

		paramMap.addValue("FinReference", finReference);

		return this.namedJdbcTemplate.queryForObject(sql.toString(), paramMap,
				ParameterizedBeanPropertyRowMapper.newInstance(FinanceEnquiry.class));
	}

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

		}

		return customerDetails;
	}

	@Override
	public void logFileInfo(String fileName, String memberId, String memberName, String memberPwd) {
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
	public void extractCustomers() throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO CIBIL_CUSTOMER_EXTRACT ");
		sql.append(" SELECT CUSTID, FINREFERENCE, OWNERSHIP, LATESTRPYDATE FROM CIBIL_CUSTOMER_EXTARCT_VIEW");
		sql.append(" WHERE LATESTRPYDATE >= :LATESTRPYDATE ");

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("LATESTRPYDATE", DateUtil.addMonths(DateUtility.getAppDate(), -36));

		try {
			namedJdbcTemplate.update(sql.toString(), paramMap);
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
