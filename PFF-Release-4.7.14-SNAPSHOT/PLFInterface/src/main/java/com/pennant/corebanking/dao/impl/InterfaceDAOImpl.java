package com.pennant.corebanking.dao.impl;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.pennant.corebanking.dao.InterfaceDAO;
import com.pennant.coreinterface.model.AccountPostingTemp;
import com.pennant.coreinterface.model.CoreBankAccountDetail;
import com.pennant.coreinterface.model.CoreBankAccountPosting;
import com.pennant.coreinterface.model.CoreCustomerDedup;
import com.pennant.coreinterface.model.CoreDocumentDetails;
import com.pennant.coreinterface.model.customer.InterfaceCustomer;
import com.pennant.coreinterface.model.customer.InterfaceCustomerDetail;
import com.pennant.coreinterface.model.customer.InterfaceCustomerIdentity;
import com.pennant.coreinterface.model.customer.InterfaceCustomerRating;
import com.pennant.coreinterface.model.limit.CustomerLimitDetail;
import com.pennant.equation.util.DateUtility;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennanttech.pennapps.core.InterfaceException;

public class InterfaceDAOImpl implements InterfaceDAO {

	private static Logger logger = Logger.getLogger(InterfaceDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public InterfaceDAOImpl() {
		super();
	}

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public List<CoreBankAccountDetail> fetchAccountDetails(CoreBankAccountDetail coreAcct) {
		logger.debug("Entering");

		List<CoreBankAccountDetail> list = new ArrayList<CoreBankAccountDetail>();
		
		StringBuilder selectQuery = new StringBuilder();
		selectQuery.append(" SELECT  AccountNumber, SCACT AcType, ");
		selectQuery.append(" SCSHN CustShrtName, SCCCY AcCcy, SCBAL AcBal, ");
		selectQuery.append(" CASE when SCBAL < 0 THEN '-' ELSE '+' END  AmountSign ");
		selectQuery.append(" FROM GFPF GFPF");
		selectQuery.append(" INNER JOIN SCPF SCPF ON SCPF.SCAN = GFPF.GFCUS");
		selectQuery.append(" where ");
		if (StringUtils.isNotEmpty(coreAcct.getCustCIF())) {
			selectQuery.append(" GFPF.GFCUS =:CustCIF AND ");
		}
		selectQuery.append(" SCPF.SCAI14 <> 'Y' AND SCPF.SCAI17 <> 'Y' AND SCPF.SCAI20 <> 'Y' AND SCPF.SCAI30 <> 'Y'");
		if (!StringUtils.isBlank(coreAcct.getAcType())) {
			
			StringBuffer temAccTypes = new StringBuffer();
			String accTypes = StringUtils.trimToEmpty(coreAcct.getAcType().replaceAll(",", ""));
			for (int i = 0; i < (accTypes.length()) / 2; i++) {
				if (temAccTypes.length() > 0) {
					temAccTypes.append(",");
				}
				temAccTypes.append("'");
				temAccTypes.append(accTypes.substring(i * 2, i * 2 + 2));
				temAccTypes.append("'");
			}
			coreAcct.setAcType(temAccTypes.toString());
			
			selectQuery.append(" AND SCPF.SCACT in(");
			selectQuery.append(temAccTypes.toString());
			selectQuery.append(" )");
		}

		logger.debug("selectSql: " + selectQuery.toString());

		try {
			SqlParameterSource paramSource = new BeanPropertySqlParameterSource(coreAcct);
			RowMapper<CoreBankAccountDetail> rowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(CoreBankAccountDetail.class);

			list = this.namedParameterJdbcTemplate.query(selectQuery.toString(), paramSource, rowMapper);

		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return list;

	}

	@Override
	public List<CoreBankAccountPosting> validateAccount(List<CoreBankAccountPosting> accountPostings) {
		logger.debug("Entering");

		CoreBankAccountPosting temp = null;
		StringBuilder deleteQuery = new StringBuilder();
		StringBuilder selectQuery = new StringBuilder();
		StringBuilder insertQuery = new StringBuilder();

		deleteQuery.append("DELETE FROM ACCFINDET_temp");

		selectQuery.append(" SELECT AccountNumber Account, ErrorCode, ErrorMessage FROM ACCFINDET_TEMP");
		selectQuery.append(" WHERE  ReqRefId =:ReqRefId and ReqRefSeq =:ReqRefSeq");

		insertQuery
				.append("INSERT INTO ACCFINDET_temp(ReqRefId, ReqRefSeq, CustCIF, AcCcy, AcSPCode, AcBranch, AccountNumber, CreateNew, CreateIfNF, InternalAc, OpenStatus, ErrorCode, ErrorMessage)");
		insertQuery
				.append("VALUES(:ReqRefId, :ReqRefSeq, :CustCIF, :AcCcy, :AcSPCode, :AcBranch, :Account, :CreateNew, :CreateIfNF, :InternalAc, :OpenStatus, :ErrorCode, :ErrorMessage)");

		logger.debug("selectSql: " + insertQuery.toString());
		SqlParameterSource[] sqlParmSource = SqlParameterSourceUtils.createBatch(accountPostings.toArray());

		try {
			this.namedParameterJdbcTemplate.getJdbcOperations().execute(deleteQuery.toString());
			prepareAccPostinIds(accountPostings);
			this.namedParameterJdbcTemplate.batchUpdate(insertQuery.toString(), sqlParmSource);
			int reqRefId = accountPostings.get(0).getReqRefId();
			executeAccountForFin(reqRefId, "Y");

			SqlParameterSource beanParameters = null;
			RowMapper<CoreBankAccountPosting> typeRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(CoreBankAccountPosting.class);

			for (CoreBankAccountPosting item : accountPostings) {
				beanParameters = new BeanPropertySqlParameterSource(item);
				temp = this.namedParameterJdbcTemplate.queryForObject(selectQuery.toString(), beanParameters,
						typeRowMapper);

				item.setErrorCode(temp.getErrorCode());
				item.setErrorMessage(temp.getErrorMessage());
				item.setAccount(temp.getAccount());
			}

		} catch (Exception e) {
			logger.debug("Leaving");
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return accountPostings;

	}

	@Override
	public List<CoreBankAccountDetail> fetchAccountBalance(String accountNumber) {
		logger.debug("Entering");

		Map<String,String> map = new HashMap<String,String>();
		map.put("AccountNumber", accountNumber);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT AccountNumber,  SCBAL AcBal,");
		selectSql.append(" SCCCY AcCcy,SCSHN AcShrtName , SCACT AcType, ");
		selectSql.append(" case when SCBAL < 0 then '-' else '+' end  AmountSign");
		selectSql.append(" from SCPF where AccountNumber = :AccountNumber ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<CoreBankAccountDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CoreBankAccountDetail.class);

		List<CoreBankAccountDetail> list = null;
		try {
			list = this.namedParameterJdbcTemplate.query(selectSql.toString(), map, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}
		logger.debug("Leaving");
		return list;
	}
	@Override
	public List<CoreBankAccountDetail> fetchAccountBalance(List<String> accountNumberList) {
		logger.debug("Entering");
		
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		map.put("AccNumberList", accountNumberList);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT AccountNumber, SCBAL AcBal,  ");
		selectSql.append("SCCCY AcCcy,SCSHN AcShrtName , SCACT AcType, ");
		selectSql.append(" case when SCBAL < 0 then '-' else '+' end  AmountSign");
		selectSql.append(" from SCPF  where AccountNumber IN(:AccNumberList) ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<CoreBankAccountDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CoreBankAccountDetail.class);
		
		List<CoreBankAccountDetail> list = null;
		try {
			list = this.namedParameterJdbcTemplate.query(selectSql.toString(), map, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}
		logger.debug("Leaving");
		return list;
	}

	@Override
	public CoreBankAccountDetail fetchAccount(CoreBankAccountDetail accountDetail) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder(
				"select AccountId, AcCcy, AcType, AcBranch, AcCustId,InternalAc, AcActive");
		selectSql
				.append(" from accounts where AcType =:AcType and AcCustId =:CustCIF and AcCcy =:AcCcy and AcBranch =:AcBranch");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountDetail);
		RowMapper<CoreBankAccountDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CoreBankAccountDetail.class);

		logger.debug("Leaving");

		try {
			accountDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (Exception e) {
			logger.warn("Exception: ", e);
			accountDetail = null;
		}

		return accountDetail;
	}

	@Override
	public void saveAccountDetails(List<CoreBankAccountDetail> accountDetail) {
		logger.debug("Entering");

		StringBuilder deleteQuery = new StringBuilder("DELETE FROM ACCFINDET_temp");

		try {
			this.namedParameterJdbcTemplate.getJdbcOperations().execute(deleteQuery.toString());
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		StringBuilder insertQuery = new StringBuilder(
				"INSERT INTO ACCFINDET_temp(ReqRefId, ReqRefSeq, CustCIF, AcCcy, AcType, AcSPCode, AcBranch, AccountNumber, CreateNew, CreateIfNF, InternalAc, OpenStatus, ErrorCode, ErrorMessage)");
		insertQuery
				.append("VALUES(:ReqRefId, :ReqRefSeq, :CustCIF, :AcCcy, :AcType, :AcSPCode, :AcBranch, :AccountNumber, :CreateNew, :CreateIfNF, :InternalAc, :OpenStatus, :ErrorCode, :ErrorMessage)");

		logger.debug("selectSql: " + insertQuery.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(accountDetail.toArray());

		logger.debug("Leaving");
		try {
			this.namedParameterJdbcTemplate.batchUpdate(insertQuery.toString(), beanParameters);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

	}

	@Override
	public void saveAccountPostings(List<AccountPostingTemp> accountPostings) {
		logger.debug("Entering");

		StringBuilder deleteQuery = new StringBuilder();
		StringBuilder insertQuery = new StringBuilder();

		deleteQuery.append("DELETE FROM AccountPosting_Temp");

		insertQuery
				.append("INSERT INTO AccountPosting_Temp(ReqRefId, ReqRefSeq, ReqShadow,  AccNumber, PostingBranch, PostingCcy, PostingCode, PostingAmount, PostingDate, ValueDate, PostingRef, PostingNar1, PostingNar2, PostingNar3, Error,  ErrDesc)");
		insertQuery
				.append("VALUES(:ReqRefId, :ReqRefSeq, :ReqShadow,  :AccNumber, :PostingBranch, :PostingCcy, :PostingCode, :PostingAmount, :PostingDate, :ValueDate, :PostingRef, :PostingNar1, :PostingNar2, :PostingNar3, :Error,  :ErrDesc)");

		logger.debug("deleteQuery: " + deleteQuery.toString());
		logger.debug("selectSql: " + insertQuery.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(accountPostings.toArray());

		logger.debug("Leaving");
		try {
			this.namedParameterJdbcTemplate.getJdbcOperations().execute(deleteQuery.toString());
			this.namedParameterJdbcTemplate.batchUpdate(insertQuery.toString(), beanParameters);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

	}

	@Override
	public List<AccountPostingTemp> executeAccPosting(List<AccountPostingTemp> accPostingTempList) throws Exception {
		logger.debug("Entering");

		StringBuilder selectQuery = new StringBuilder();
		StringBuilder updateQuery = new StringBuilder();

		selectQuery
				.append("select ReqRefId, ReqRefSeq, ReqShadow,  AccNumber, PostingBranch, PostingCcy, PostingCode, PostingAmount, PostingDate, ValueDate, PostingRef, PostingNar1, PostingNar2, PostingNar3, Error,  ErrDesc from AccountPosting_Temp");

		updateQuery
				.append("UPDATE AccountPosting_Temp SET Error=:Error, ErrDesc=:ErrDesc WHERE ReqRefId=:ReqRefId AND ReqRefSeq=:ReqRefSeq");

		logger.debug("selectQuery: " + selectQuery.toString());

		try {
			final StringBuilder builder = new StringBuilder(
					"{ call SP_POSTING(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }");

			logger.debug("selectSql: " + builder.toString());

			for (final AccountPostingTemp item : accPostingTempList) {

				this.namedParameterJdbcTemplate.getJdbcOperations().execute(new CallableStatementCreator() {
					public CallableStatement createCallableStatement(Connection con) throws SQLException {
						CallableStatement cs = con.prepareCall(builder.toString());
						cs.setString(1, item.getReqShadow());
						cs.setString(2, item.getAccNumber());
						cs.setString(3, item.getPostingBranch());
						cs.setString(4, item.getPostingCcy());
						cs.setString(5, item.getPostingCode());
						cs.setBigDecimal(6, item.getPostingAmount());
						cs.setBigDecimal(7, getDecimalDate(item.getPostingDate()));
						cs.setBigDecimal(8, getDecimalDate(item.getValueDate()));
						cs.setString(9, item.getPostingRef());
						cs.setString(10, item.getPostingNar1());
						cs.setString(11, item.getPostingNar2());
						cs.setString(12, item.getPostingNar3());
						cs.setString(13, item.getPostingNar4());
						cs.registerOutParameter(14, Types.VARCHAR);
						cs.registerOutParameter(15, Types.VARCHAR);

						return cs;
					}
				}, new CallableStatementCallback<Object>() {
					public Object doInCallableStatement(CallableStatement cs) throws SQLException {
						cs.execute();
						item.setError(cs.getString(14));
						item.setErrDesc(cs.getString(15));
						return null;
					}
				});

			}
			SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(accPostingTempList.toArray());
			this.namedParameterJdbcTemplate.batchUpdate(updateQuery.toString(), beanParameters);

		} catch (Exception e) {
			logger.error("Exception: ", e);
			logger.debug("Leaving");
			throw e;
		}
		logger.debug("Leaving");
		return accPostingTempList;
	}

	@Override
	public List<CoreBankAccountPosting> fetchAccountPostingForFin(List<CoreBankAccountPosting> list) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder(
				"SELECT AccNumber Account, PostingRef PostRef, Error errorId, ErrDesc errorMsg FROM AccountPosting_Temp");
		selectSql.append(" WHERE ReqRefId =:ReqRefId and ReqRefSeq =:ReqRefSeq");

		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = null;
		RowMapper<CoreBankAccountPosting> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CoreBankAccountPosting.class);
		try {
			for (CoreBankAccountPosting item : list) {
				beanParameters = new BeanPropertySqlParameterSource(item);
				CoreBankAccountPosting temp = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
						beanParameters, typeRowMapper);

				item.setErrorCode(temp.getErrorCode());
				item.setErrorMessage(temp.getErrorMessage());
				item.setAccount(temp.getAccount());
				item.setPostRef(StringUtils.trimToEmpty(temp.getPostRef()));
				if (StringUtils.equals(item.getInternalAc(), "Y")) {
					item.setAcType(item.getAcSPCode());
				}
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return list;
	}

	@Override
	public List<CoreBankAccountDetail> fetchAccountForFin(List<CoreBankAccountDetail> list) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder(
				"SELECT ReqRefId, ReqRefSeq, CustCIF, AcCcy, AcType, AcSPCode, AcBranch, AccountNumber,");
		selectSql.append(" CreateNew ,CreateIfNF ,InternalAc ,OpenStatus,ErrorCode ,ErrorMessage FROM ACCFINDET_TEMP");
		selectSql.append(" WHERE  ReqRefId =:ReqRefId and ReqRefSeq =:ReqRefSeq");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = null;
		RowMapper<CoreBankAccountDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CoreBankAccountDetail.class);
		try {
			for (CoreBankAccountDetail detail : list) {
				beanParameters = new BeanPropertySqlParameterSource(detail);
				CoreBankAccountDetail item = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
						beanParameters, typeRowMapper);
				detail.setAccountNumber(item.getAccountNumber());
				detail.setErrorCode(item.getErrorCode());
				detail.setErrorMessage(item.getErrorMessage());
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return list;
	}

	@Override
	public String executeAccountForFin(final int reqRefId, final String createNow) {
		logger.debug("Entering");

		/*
		 * final StringBuilder builder = new
		 * StringBuilder("{ call SP_PFFFAN(?, ?, ?, ?) }");
		 * 
		 * logger.debug("selectSql: " + builder.toString());
		 * 
		 * try{
		 * 
		 * this.namedParameterJdbcTemplate.getJdbcOperations().execute( new
		 * CallableStatementCreator() { public CallableStatement
		 * createCallableStatement(Connection con) throws SQLException{
		 * CallableStatement cs = con.prepareCall(builder.toString());
		 * cs.setInt(1, reqRefId); cs.setString(2, createNow);
		 * cs.registerOutParameter(3, Types.VARCHAR); cs.registerOutParameter(4,
		 * Types.VARCHAR);
		 * 
		 * return cs; } }, new CallableStatementCallback<Object>() { public
		 * Object doInCallableStatement(CallableStatement cs) throws
		 * SQLException{ cs.execute(); return cs.getString(4); } } );
		 * 
		 * }catch (Exception e) { logger.info(e); }
		 */
		logger.debug("Leaving");
		return "";
	}

	@Override
	public List<CoreBankAccountDetail> updateAccountDetailsIds(List<CoreBankAccountDetail> list) {
		logger.debug("Entering");

		int reqRefId = 0;
		int reqRefSeq = 0;

		StringBuilder selectSql = new StringBuilder("select COALESCE(MAX(ReqRefId), 0) from ACCFINDET_temp");

		logger.debug("selectSql: " + selectSql.toString());

		try {
			reqRefId = this.namedParameterJdbcTemplate.getJdbcOperations().queryForObject(selectSql.toString(),
					Integer.class);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		reqRefId = reqRefId + 1;
		for (CoreBankAccountDetail item : list) {
			reqRefSeq = reqRefSeq + 1;

			if (item.getInternalAc()) {
				item.setAcSPCode(item.getAcType());
				item.setAcType("");
			}

			item.setReqRefId(reqRefId);
			item.setReqRefSeq(reqRefSeq);
		}

		logger.debug("Leaving");
		return list;
	}

	@Override
	public List<CoreBankAccountPosting> prepareAccPostinIds(List<CoreBankAccountPosting> list) {
		logger.debug("Entering");

		int reqRefId = 0;
		int reqRefSeq = 0;

		StringBuilder selectSql = new StringBuilder("select COALESCE(MAX(ReqRefId), 0) from ACCFINDET_temp");

		logger.debug("selectSql: " + selectSql.toString());

		try {
			reqRefId = this.namedParameterJdbcTemplate.getJdbcOperations().queryForObject(selectSql.toString(),
					Integer.class);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		reqRefId = reqRefId + 1;
		for (CoreBankAccountPosting item : list) {
			reqRefSeq = reqRefSeq + 1;

			if (StringUtils.equals("Y", item.getInternalAc())) {
				item.setAcSPCode(item.getAcType());
				item.setAcType("");
			}

			item.setReqRefId(reqRefId);
			item.setReqRefSeq(reqRefSeq);
		}

		logger.debug("Leaving");
		return list;
	}

	@Override
	public InterfaceCustomerDetail getCustDetails(String custCIF, String custLoc) throws InterfaceException {
		logger.debug("Entering");
		InterfaceCustomerDetail customerInterfaceData = new InterfaceCustomerDetail();
		InterfaceCustomer customer = new InterfaceCustomer();
		customerInterfaceData.setCustCIF(custCIF);
		StringBuilder selectSql = new StringBuilder();

		selectSql
				.append(" SELECT CustCIF, CustCoreBank, CustCtgCode, CustTypeCode, CustSalutationCode, CustFName, CustShrtName, ");
		selectSql
				.append(" CustFNameLclLng,CustShrtNameLclLng,CustDftBranch,CustGenderCode,CustPOB,CustPassportNo,CustIsMinor,CustRO1,");
		selectSql
				.append(" CustIsBlocked,CustIsActive,CustIsClosed,CustIsDecease,CustIsTradeFinCust,CustSector,CustSubSector,CustProfession,CustTotalIncome,");
		selectSql
				.append(" CustMaritalSts,CustEmpSts,CustBaseCcy,CustParentCountry,CustResdCountry,CustRiskCountry,CustNationality, CustDOB,CustAddlVar83,CustAddlVar82");
		selectSql.append(" FROM Customers WHERE CustCIF=:CustCIF");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerInterfaceData);
		RowMapper<InterfaceCustomer> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(InterfaceCustomer.class);

		logger.debug("Leaving");

		try {
			customer = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
			customerInterfaceData.setCustomer(customer);

			setCustDocDetails(customerInterfaceData);
			setCustRatings(customerInterfaceData);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			return null;
		} catch (Exception e) {
			logger.error("Exception: ", e);
			return null;
		}

		return customerInterfaceData;
	}

	private InterfaceCustomerDetail setCustDocDetails(InterfaceCustomerDetail customerInterfaceData) throws Exception {
		logger.debug("Entering");

		CoreBankAccountDetail detail = new CoreBankAccountDetail();
		detail.setCustCIF(customerInterfaceData.getCustCIF());

		StringBuilder selectSql = new StringBuilder();
		selectSql
				.append("SELECT IdCustID, IdType, IdRef, IdIssueCountry, IdIssuedOn, IdExpiresOn FROM CustomerDocuments WHERE CustCIF= :CustCIF");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		RowMapper<InterfaceCustomerIdentity> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(InterfaceCustomerIdentity.class);

		List<InterfaceCustomerIdentity> list = this.namedParameterJdbcTemplate.query(selectSql.toString(),
				beanParameters, typeRowMapper);
		customerInterfaceData.setCustomerIdentityList(list);
		logger.debug("Leaving");
		return customerInterfaceData;
	}

	private InterfaceCustomerDetail setCustRatings(InterfaceCustomerDetail customerInterfaceData) throws Exception {
		logger.debug("Entering");

		CoreBankAccountDetail detail = new CoreBankAccountDetail();
		detail.setCustCIF(customerInterfaceData.getCustCIF());

		StringBuilder selectSql = new StringBuilder();
		selectSql
				.append("SELECT CustRatingType,CustLongRate, CustShortRate FROM CUST_RATINGS_View WHERE CustCIF = :CustCIF");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		RowMapper<InterfaceCustomerRating> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(InterfaceCustomerRating.class);

		List<InterfaceCustomerRating> list = this.namedParameterJdbcTemplate.query(selectSql.toString(),
				beanParameters, typeRowMapper);
		customerInterfaceData.setRatingsList(list);
		logger.debug("Leaving");
		return customerInterfaceData;
	}

	@Override
	public Map<String, String> getCalendarWorkingDays() {
		logger.debug("Entering");
		Map<String, String> map = new HashMap<String, String>();

		StringBuilder selectSql = new StringBuilder(
				" select (1900 + H8YOC) H8YOC, H8DIY from H8PF where H8NUM = 'AAA' AND H8YOC = (select SUBSTRING(convert(CHAR, T4PDAT), 1, 3) from t4pf)");

		logger.debug("selectSql: " + selectSql.toString());

		try {

			SqlRowSet rowSet = this.namedParameterJdbcTemplate.getJdbcOperations().queryForRowSet(selectSql.toString());

			while (rowSet.next()) {
				map.put(rowSet.getString(1), rowSet.getString(2));
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return map;
	}

	private BigDecimal getDecimalDate(Date date) {
		BigDecimal as400Date = null;
		BigDecimal dateInt = null;
		String strDate = null;

		if (date == null) {
			return BigDecimal.ZERO;
		}
		strDate = DateUtility.formatDate(date, InterfaceMasterConfigUtil.DBDateFormat);
		strDate = StringUtils.trimToEmpty(strDate);
		if (StringUtils.isNotEmpty(strDate)) {
			dateInt = new BigDecimal(strDate.substring(0, 4) + strDate.substring(5, 7) + strDate.substring(8, 10));
			as400Date = new BigDecimal(19000000).subtract(dateInt);
			as400Date = new BigDecimal(-1).multiply(as400Date);
		} else {
			as400Date = null;
		}

		return as400Date;
	}

	public void disConnection() {
	}

	/**
	 * Method for fetch customer dedup details from corebank system
	 * 
	 */
	@Override
	public List<CoreCustomerDedup> fetchCustomerDedupDetails(CoreCustomerDedup customerDedup) {
		logger.debug("Entering");

		List<CoreCustomerDedup> custDedupList = new ArrayList<CoreCustomerDedup>();

		logger.debug("Leaving");
		return custDedupList;
	}

	@Override
	public CustomerLimitDetail getLimitDetails(String limitRef, String branchCode) {

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("LimitRef", limitRef);
		source.addValue("branchCode", branchCode);

		StringBuilder selectSql = new StringBuilder("select * from CustomerLimitDetails  Where LimitRef=:LimitRef ");

		RowMapper<CustomerLimitDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CustomerLimitDetail.class);
		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException ex) {
			logger.warn("Exception: ", ex);
			return null;
		}
	}
	
	@Override
	public List<CoreDocumentDetails> getDocumentDetailsByRef(String ref) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder("select DocId, DocModule, DocCategory, DocImage,");
		sql.append(" Doctype, DocName, ReferenceId,FinEvent,");
		sql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId, docRefId ");
		sql.append(" from DocumentDetails");
		sql.append(" where ReferenceId = :ReferenceId ");
		logger.debug("selectSql: " + sql.toString());

		List<CoreDocumentDetails> docList = null;
		CoreDocumentDetails documentDetails = new CoreDocumentDetails();
		documentDetails.setReferenceId(ref);


		logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(documentDetails);
		RowMapper<CoreDocumentDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CoreDocumentDetails.class);

		logger.debug("Leaving");

		try {
			docList = this.namedParameterJdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			docList = new ArrayList<CoreDocumentDetails>();
		}
		return docList;
	}
}
