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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.pennant.corebanking.dao.InterfaceDAO;
import com.pennant.coreinterface.exception.CustomerNotFoundException;
import com.pennant.coreinterface.model.AccountPostingTemp;
import com.pennant.coreinterface.model.CoreBankAccountDetail;
import com.pennant.coreinterface.model.CoreBankAccountPosting;
import com.pennant.coreinterface.model.CoreBankingCustomer;
import com.pennant.coreinterface.model.CorePoliceCase;
import com.pennant.coreinterface.model.CustomerInterfaceData;
import com.pennant.coreinterface.model.CustomerInterfaceData.CustomerIdentity;
import com.pennant.coreinterface.model.CustomerInterfaceData.CustomerRating;
import com.pennant.equation.util.DateUtility;

public class InterfaceDAOImpl implements InterfaceDAO {

	private static Logger logger = Logger.getLogger(InterfaceDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	
	@Override
	public List<CoreBankAccountDetail>  fetchAccountDetails(CoreBankAccountDetail coreAcct) {
		List<CoreBankAccountDetail> list = new ArrayList<CoreBankAccountDetail>();
		StringBuffer temAccTypes = new StringBuffer();
		String accTypes = StringUtils.trimToEmpty(coreAcct.getAcType());

		for (int i = 0; i < (accTypes.length()) / 2; i++) {
			if (temAccTypes.length() > 0) {
				temAccTypes.append(",");
			}
			temAccTypes.append("'");
			temAccTypes.append(accTypes.substring(i * 2, i * 2 + 2));
			temAccTypes.append("'");
		}	
		coreAcct.setAcType(temAccTypes.toString());

		StringBuilder selectQuery = new StringBuilder();
		selectQuery.append(" SELECT SCAB+SCAN+SCAS as accountNumber, SCACT as acType, SCSHN as custShrtName, SCCCY as AcCcy, SCBAL AS AcBal, ");
		selectQuery.append(" CASE when SCBAL < 0 THEN '-' ELSE '+' END  as AmountSign ");
		selectQuery.append(" FROM GFPF GFPF");
		selectQuery.append(" INNER JOIN SCPF SCPF ON SCPF.SCAN = GFPF.GFCUS");
		selectQuery.append(" where GFPF.GFCUS =:CustCIF");
		selectQuery.append(" AND SCPF.SCAI14 <> 'Y' AND SCPF.SCAI17 <> 'Y' AND SCPF.SCAI20 <> 'Y' AND SCPF.SCAI30 <> 'Y'");
		selectQuery.append(" AND SCPF.SCACT in(");
		selectQuery.append(temAccTypes.toString());
		selectQuery.append(" )");

		logger.debug("selectSql: " + selectQuery.toString());
		
		try{		
			SqlParameterSource paramSource = new BeanPropertySqlParameterSource(coreAcct);
			RowMapper<CoreBankAccountDetail> rowMapper = ParameterizedBeanPropertyRowMapper .newInstance(CoreBankAccountDetail.class);
			
			list = this.namedParameterJdbcTemplate.query(selectQuery.toString(), paramSource, rowMapper);

		}catch (Exception e) {
			logger.error(e);
			logger.debug("Leaving");
			e.printStackTrace();
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

		selectQuery.append(" SELECT AccountNumber AS Account, ErrorCode, ErrorMessage FROM ACCFINDET_TEMP");
		selectQuery.append(" WHERE  ReqRefId =:ReqRefId and ReqRefSeq =:ReqRefSeq");

		insertQuery.append("INSERT INTO ACCFINDET_temp(ReqRefId, ReqRefSeq, CustCIF, AcCcy, AcSPCode, AcBranch, AccountNumber, CreateNew, CreateIfNF, InternalAc, OpenStatus, ErrorCode, ErrorMessage)");
		insertQuery.append("VALUES(:ReqRefId, :ReqRefSeq, :CustCIF, :AcCcy, :AcSPCode, :AcBranch, :Account, :CreateNew, :CreateIfNF, :InternalAc, :OpenStatus, :ErrorCode, :ErrorMessage)");

		logger.debug("selectSql: " + insertQuery.toString());
		SqlParameterSource[] sqlParmSource = SqlParameterSourceUtils.createBatch(accountPostings.toArray());

		
		try{	
			this.namedParameterJdbcTemplate.getJdbcOperations().execute(deleteQuery.toString());
			prepareAccPostinIds(accountPostings);
			this.namedParameterJdbcTemplate.batchUpdate(insertQuery.toString(), sqlParmSource);
			int reqRefId = accountPostings.get(0).getReqRefId();
			executeAccountForFin(reqRefId, "Y");	

			SqlParameterSource beanParameters = null;
			RowMapper<CoreBankAccountPosting> typeRowMapper = ParameterizedBeanPropertyRowMapper .newInstance(CoreBankAccountPosting.class);

			for(CoreBankAccountPosting item: accountPostings) {				
				beanParameters = new BeanPropertySqlParameterSource(item);
				temp = this.namedParameterJdbcTemplate.queryForObject(selectQuery.toString(), beanParameters, typeRowMapper);

				item.setErrorCode(temp.getErrorCode());
				item.setErrorMessage(temp.getErrorMessage());
				item.setAccount(temp.getAccount());
			}


		}catch (Exception e) {
			logger.debug("Leaving");
			logger.error(e);
		}
		logger.debug("Leaving");
		return accountPostings;

	}
	
	@Override
	public List<CoreBankAccountDetail> fetchAccountBalance(List<String> accountNumberList) {
		logger.debug("Entering");

		Map<String,List<String>> map=new HashMap<String, List<String>>();
		map.put("AccNumberList", accountNumberList);
		
		StringBuilder selectSql = new StringBuilder();		
		selectSql.append(" select SCAB+SCAN+SCAS as AccountNumber, SCBAL as AcBal, SCCCY AS AcCcy,SCSHN as AcShrtName , SCACT AS AcType, ");
		selectSql.append(" case when SCBAL < 0 then '-' else '+' end  as AmountSign"); 
		selectSql.append(" from SCPF ");
		selectSql.append(" where SCAB+SCAN+SCAS IN(:AccNumberList) "); 

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<CoreBankAccountDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper .newInstance(CoreBankAccountDetail.class);
		
		List<CoreBankAccountDetail> list = null;
		try {
			list =  this.namedParameterJdbcTemplate.query(selectSql.toString(), map, typeRowMapper);		
		} catch (EmptyResultDataAccessException e) {
		}
		logger.debug("Leaving");
		return list;
	}
	
	@Override
	public CoreBankAccountDetail fetchAccount(CoreBankAccountDetail accountDetail) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("select AccountId, AcCcy, AcType, AcBranch, AcCustId,InternalAc, AcActive");
		selectSql.append(" from accounts where AcType =:AcType and AcCustId =:CustCIF and AcCcy =:AcCcy and AcBranch =:AcBranch");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountDetail);
		RowMapper<CoreBankAccountDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper .newInstance(CoreBankAccountDetail.class);

		logger.debug("Leaving");

		try{		
			accountDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		}catch (Exception e) {
			accountDetail = null;
		}

		return accountDetail;
	}
	
	@Override
	public void saveAccountDetails(List<CoreBankAccountDetail> accountDetail) {
		logger.debug("Entering");

		StringBuilder deleteQuery = new StringBuilder("DELETE FROM ACCFINDET_temp");

		try{	
			this.namedParameterJdbcTemplate.getJdbcOperations().execute(deleteQuery.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}


		StringBuilder insertQuery = new StringBuilder("INSERT INTO ACCFINDET_temp(ReqRefId, ReqRefSeq, CustCIF, AcCcy, AcType, AcSPCode, AcBranch, AccountNumber, CreateNew, CreateIfNF, InternalAc, OpenStatus, ErrorCode, ErrorMessage)");
		insertQuery.append("VALUES(:ReqRefId, :ReqRefSeq, :CustCIF, :AcCcy, :AcType, :AcSPCode, :AcBranch, :AccountNumber, :CreateNew, :CreateIfNF, :InternalAc, :OpenStatus, :ErrorCode, :ErrorMessage)");

		logger.debug("selectSql: " + insertQuery.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(accountDetail.toArray());

		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertQuery.toString(), beanParameters);
		}catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public void saveAccountPostings(List<AccountPostingTemp> accountPostings) {
		logger.debug("Entering");

		StringBuilder deleteQuery = new StringBuilder();
		StringBuilder insertQuery = new StringBuilder();
		
		deleteQuery.append("DELETE FROM AccountPosting_Temp");
		
		insertQuery.append("INSERT INTO AccountPosting_Temp(ReqRefId, ReqRefSeq, ReqShadow,  AccNumber, PostingBranch, PostingCcy, PostingCode, PostingAmount, PostingDate, ValueDate, PostingRef, PostingNar1, PostingNar2, PostingNar3, Error,  ErrDesc)");
		insertQuery.append("VALUES(:ReqRefId, :ReqRefSeq, :ReqShadow,  :AccNumber, :PostingBranch, :PostingCcy, :PostingCode, :PostingAmount, :PostingDate, :ValueDate, :PostingRef, :PostingNar1, :PostingNar2, :PostingNar3, :Error,  :ErrDesc)");

		logger.debug("deleteQuery: " + deleteQuery.toString());
		logger.debug("selectSql: " + insertQuery.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(accountPostings.toArray());

		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.getJdbcOperations().execute(deleteQuery.toString());
			this.namedParameterJdbcTemplate.batchUpdate(insertQuery.toString(), beanParameters);
		}catch (Exception e) {
			logger.error(e);
			logger.debug("Leaving");
			e.printStackTrace();
		}

	}
	
	@Override
	public List<AccountPostingTemp> executeAccPosting(List<AccountPostingTemp> accPostingTempList) throws Exception{
		logger.debug("Entering");
				
		StringBuilder selectQuery = new StringBuilder();
		StringBuilder updateQuery = new StringBuilder();
		
		selectQuery.append("select ReqRefId, ReqRefSeq, ReqShadow,  AccNumber, PostingBranch, PostingCcy, PostingCode, PostingAmount, PostingDate, ValueDate, PostingRef, PostingNar1, PostingNar2, PostingNar3, Error,  ErrDesc from AccountPosting_Temp");
		
		updateQuery.append("UPDATE AccountPosting_Temp SET Error=:Error, ErrDesc=:ErrDesc WHERE ReqRefId=:ReqRefId AND ReqRefSeq=:ReqRefSeq");

		logger.debug("selectQuery: " + selectQuery.toString());
		
		try{	
			//accPostingTempList = this.namedParameterJdbcTemplate.getJdbcOperations().queryForList(selectQuery.toString(), AccountPostingTemp.class);
			final StringBuilder builder = new StringBuilder("{ call SP_POSTING(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }");

			logger.debug("selectSql: " + builder.toString());
		
			for(final AccountPostingTemp item :accPostingTempList){
				
				this.namedParameterJdbcTemplate.getJdbcOperations().execute(
					    new CallableStatementCreator() {
					        public CallableStatement createCallableStatement(Connection con) throws SQLException{
					            CallableStatement cs = con.prepareCall(builder.toString());
					            cs.setString(1, item.getReqShadow()); 
					            cs.setString(2, item.getAccNumber()); 
					            cs.setString(3, item.getPostingBranch()); 
					            cs.setString(4, item.getPostingCcy()); 
					            cs.setString(5, item.getPostingCode()); 
					            cs.setBigDecimal(6,item.getPostingAmount()); 
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
					    },
					    new CallableStatementCallback<Object>() {
					        public Object doInCallableStatement(CallableStatement cs) throws SQLException{
					            cs.execute();
					            item.setError(cs.getString(14));
					            item.setErrDesc(cs.getString(15));
					           return null;
					        }
					    }
					);
				
			}
			SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(accPostingTempList.toArray());
			this.namedParameterJdbcTemplate.batchUpdate(updateQuery.toString(), beanParameters);
			
		}catch (Exception e) {
			logger.error(e);
			logger.debug("Leaving");
			throw e;
		}
		logger.debug("Leaving");
		return accPostingTempList;
	}
	
	@Override
	public List<CoreBankAccountPosting> fetchAccountPostingForFin(List<CoreBankAccountPosting> list) {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder("SELECT AccNumber as Account, PostingRef as PostRef, Error as errorId, ErrDesc as errorMsg FROM AccountPosting_Temp");
		selectSql.append(" WHERE ReqRefId =:ReqRefId and ReqRefSeq =:ReqRefSeq");

		logger.debug("selectSql: " + selectSql.toString());
		
		SqlParameterSource beanParameters = null;
		RowMapper<CoreBankAccountPosting> typeRowMapper = ParameterizedBeanPropertyRowMapper .newInstance(CoreBankAccountPosting.class);
		try{		
			for(CoreBankAccountPosting item: list) {
				beanParameters = new BeanPropertySqlParameterSource(item);
				CoreBankAccountPosting temp = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
				
				item.setErrorCode(temp.getErrorCode());
				item.setErrorMessage(temp.getErrorMessage());
				item.setAccount(temp.getAccount());
				item.setPostRef(StringUtils.trimToEmpty(temp.getPostRef()));
				if(StringUtils.trimToEmpty(item.getInternalAc()).equals("Y")){
					item.setAcType(item.getAcSPCode());
				}
			}
			
		}catch (Exception e) {
			logger.error(e);
			logger.debug("Leaving");
			e.printStackTrace();
		}
		logger.debug("Leaving");
		return list;
	}
		
	@Override
	public List<CoreBankAccountDetail> fetchAccountForFin(List<CoreBankAccountDetail> list) {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder("SELECT ReqRefId, ReqRefSeq, CustCIF, AcCcy, AcType, AcSPCode, AcBranch, AccountNumber,");
		selectSql.append(" CreateNew ,CreateIfNF ,InternalAc ,OpenStatus,ErrorCode ,ErrorMessage FROM ACCFINDET_TEMP");
		selectSql.append(" WHERE  ReqRefId =:ReqRefId and ReqRefSeq =:ReqRefSeq");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = null;
		RowMapper<CoreBankAccountDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper .newInstance(CoreBankAccountDetail.class);
		try{		
			for(CoreBankAccountDetail detail: list) {
				beanParameters = new BeanPropertySqlParameterSource(detail);
				CoreBankAccountDetail item = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
				detail.setAccountNumber(item.getAccountNumber());
				detail.setErrorCode(item.getErrorCode());
				detail.setErrorMessage(item.getErrorMessage());
			}
			
		}catch (Exception e) {
			logger.error(e);
			logger.debug("Leaving");
			e.printStackTrace();
		}
		logger.debug("Leaving");
		return list;
	}
	
	@Override
	public String executeAccountForFin(final int reqRefId, final String createNow) {
		logger.debug("Entering");

		final StringBuilder builder = new StringBuilder("{ call SP_PFFFAN(?, ?, ?, ?) }");

		logger.debug("selectSql: " + builder.toString());
		
		try{		
			
			this.namedParameterJdbcTemplate.getJdbcOperations().execute(
				    new CallableStatementCreator() {
				        public CallableStatement createCallableStatement(Connection con) throws SQLException{
				            CallableStatement cs = con.prepareCall(builder.toString());
				            cs.setInt(1, reqRefId); 
				            cs.setString(2, createNow); 
				            cs.registerOutParameter(3, Types.VARCHAR); 
				            cs.registerOutParameter(4, Types.VARCHAR); 
				            
				            return cs;
				        }
				    },
				    new CallableStatementCallback<Object>() {
				        public Object doInCallableStatement(CallableStatement cs) throws SQLException{
				            cs.execute();
				           return cs.getString(4);
				        }
				    }
				);
			
		}catch (Exception e) {
			logger.error(e);
			logger.debug("Leaving");
			e.printStackTrace();
		}
		logger.debug("Leaving");
		return "";
	}
	
	@Override
	public List<CoreBankAccountDetail> updateAccountDetailsIds(List<CoreBankAccountDetail> list) {
		logger.debug("Entering");

		int reqRefId = 0;
		int reqRefSeq = 0;

		StringBuilder selectSql = new StringBuilder("select IsNull(max(ReqRefId),0) from ACCFINDET_temp");

		logger.debug("selectSql: " + selectSql.toString());

		try{		
			reqRefId = this.namedParameterJdbcTemplate.getJdbcOperations().queryForInt(selectSql.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		reqRefId = reqRefId + 1 ;
		for(CoreBankAccountDetail item:list) {
			reqRefSeq = reqRefSeq + 1;
			
			
			if("Y".equals(item.getInternalAc())) {
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

		StringBuilder selectSql = new StringBuilder("select IsNull(max(ReqRefId),0) from ACCFINDET_temp");

		logger.debug("selectSql: " + selectSql.toString());

		try{		
			reqRefId = this.namedParameterJdbcTemplate.getJdbcOperations().queryForInt(selectSql.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}

		reqRefId = reqRefId + 1 ;
		for(CoreBankAccountPosting item:list) {
			reqRefSeq = reqRefSeq + 1;


			if("Y".equals(item.getInternalAc())) {
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
	public CoreBankingCustomer fetchCustomerDetails(CoreBankingCustomer customer) throws CustomerNotFoundException {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder(" CustomerMnemonic, CustomerFullName,  DefaultAccountShortName, CustomerType,");
		selectSql.append(" CustomerClosed, CustomerInactive, ParentCountry, RiskCountry,");
		selectSql.append(" ResidentCountry, CustomerBranchMnemonic, GroupName, Salutation,");
		selectSql.append(" CustDOB, GenderCode, CustPOB, CustPassportNum,");
		selectSql.append(" CustPassportExpiry, Minor, TradeLicNumber, TradeLicExpiry,");
		selectSql.append(" VisaNumber, VisaExpiry, Nationality");
		selectSql.append(" FROM V_CUSTOMERDETAILS");
		selectSql.append(" WHERE  CustomerMnemonic =:CustomerMnemonic");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<CoreBankingCustomer> typeRowMapper = ParameterizedBeanPropertyRowMapper .newInstance(CoreBankingCustomer.class);

		logger.debug("Leaving");

		try{		
			customer = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		}catch (Exception e) {
			logger.error(e);
			logger.debug("Leaving");
			e.printStackTrace();
			throw new CustomerNotFoundException();
		}

		return customer;
	}
	
	@Override
	public CustomerInterfaceData getCustDetails(String custCIF, String custLoc) {
		logger.debug("Entering");
		CustomerInterfaceData customerInterfaceData = new CustomerInterfaceData();
		customerInterfaceData.setCustCIF(custCIF);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" SELECT * FROM CUST_DETAILS_VIEW WHERE CustCIF=:CustCIF");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerInterfaceData);
		RowMapper<CustomerInterfaceData> typeRowMapper = ParameterizedBeanPropertyRowMapper .newInstance(CustomerInterfaceData.class);

		logger.debug("Leaving");

		try{		
			customerInterfaceData = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);			
			setCustDocDetails(customerInterfaceData);
			setCustRatings(customerInterfaceData);
		}catch (Exception e) {
			logger.error(e);
		}

		return customerInterfaceData;
	}
	
	private  CustomerInterfaceData setCustDocDetails(CustomerInterfaceData customerInterfaceData) throws Exception{
		logger.debug("Entering");
		
		CoreBankAccountDetail detail = new CoreBankAccountDetail();
		detail.setCustCIF(customerInterfaceData.getCustCIF());

		StringBuilder selectSql = new StringBuilder();		
		selectSql.append("SELECT CustIDType, CustIDNumber, CustIDCountry, CustIDIssueDate, CustIDExpDate FROM CUST_DOCDETAILS_View WHERE CustCIF= :CustCIF");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		RowMapper<CustomerIdentity> typeRowMapper = ParameterizedBeanPropertyRowMapper .newInstance(CustomerIdentity.class);
		
		List<CustomerIdentity> list = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);		
		customerInterfaceData.setCustomerIdentitylist(list);
		logger.debug("Leaving");
		return customerInterfaceData;
	}
	
	private  CustomerInterfaceData setCustRatings(CustomerInterfaceData customerInterfaceData) throws Exception{
		logger.debug("Entering");
		
		CoreBankAccountDetail detail = new CoreBankAccountDetail();
		detail.setCustCIF(customerInterfaceData.getCustCIF());

		StringBuilder selectSql = new StringBuilder();		
		selectSql.append("SELECT CustRatingType,CustLongRate, CustShortRate FROM CUST_RATINGS_View WHERE CustCIF = :CustCIF");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		RowMapper<CustomerRating> typeRowMapper = ParameterizedBeanPropertyRowMapper .newInstance(CustomerRating.class);
		
		List<CustomerRating> list = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);		
		customerInterfaceData.setCustomerRatinglist(list);
		logger.debug("Leaving");
		return customerInterfaceData;
	}
	
	@Override
	public Map<String, String> getCalendarWorkingDays()  {
		logger.debug("Entering");
		Map<String, String > map = new HashMap<String, String>();
		
		StringBuilder selectSql = new StringBuilder(" select (1900 + H8YOC) as H8YOC, H8DIY from H8PF where H8NUM = 'AAA' AND H8YOC = (select SUBSTRING(convert(CHAR, T4PDAT), 1, 3) from t4pf)");

		logger.debug("selectSql: " + selectSql.toString());

		try{		
			 
			SqlRowSet rowSet = this.namedParameterJdbcTemplate.getJdbcOperations().queryForRowSet(selectSql.toString());
	
			while(rowSet.next()) {
				map.put(rowSet.getString(1), rowSet.getString(2));
			}

		}catch (Exception e) {
			logger.error(e);
			logger.debug("Leaving");
			e.printStackTrace();

		}
		logger.debug("Leaving");
		return map;
	}
	
	
	private BigDecimal getDecimalDate(Date date){
		BigDecimal as400Date= null;
		BigDecimal dateInt = null;
		String strDate = null;

		if(date==null) {
			return BigDecimal.ZERO;
		}
		strDate = DateUtility.formatDate(date, "yyyy-MM-dd");
		strDate = StringUtils.trimToEmpty(strDate);
		if (!strDate.equals("")) {
			dateInt = 	new BigDecimal(strDate.substring(0,4) + strDate.substring(5,7) + strDate.substring(8,10));
			as400Date = new BigDecimal(19000000).subtract(dateInt);
			as400Date = new BigDecimal(-1).multiply(as400Date);
		} else {
			as400Date = null;
		}

		return as400Date;
	}
	
	public void disConnection() {
		// TODO
	}


	@Override
	public List<CorePoliceCase> fetchPoliceCustInformation(CorePoliceCase corePoliceCase, String sqlQuery) {
		List<CorePoliceCase> corePolice = new ArrayList<CorePoliceCase>();
		corePolice.add(setStaticBlackListData(new CorePoliceCase(), 1001));
		corePolice.add(setStaticBlackListData(new CorePoliceCase(), 1002));
		corePolice.add(setStaticBlackListData(new CorePoliceCase(), 1003));
		corePolice.add(setStaticBlackListData(new CorePoliceCase(), 1004));
		corePolice.add(setStaticBlackListData(new CorePoliceCase(), 1005));
		corePolice.add(setStaticBlackListData(new CorePoliceCase(), 1006));
		corePolice.add(setStaticBlackListData(new CorePoliceCase(), 1007));
		
		/*StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT * FROM PoliceCase ");
		selectSql.append(StringUtils.trimToEmpty(sqlQuery));
		selectSql.append(" AND CustCIF=:CustCIF ");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = null;
		RowMapper<CorePoliceCase> typeRowMapper = ParameterizedBeanPropertyRowMapper .newInstance(CorePoliceCase.class);
		try{		
			for(CorePoliceCase detail: corePolice) {
				beanParameters = new BeanPropertySqlParameterSource(detail);
				CorePoliceCase item = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
				detail.setCustCIF(item.getCustCIF());
				detail.setCustDateofBirth(item.getCustDateofBirth());
				detail.setCustEIDNumber(item.getCustEIDNumber());
				detail.setCustFirstName(item.getCustFirstName());
				detail.setCustLastName(item.getCustLastName());
				detail.setCustMobileNumber(item.getCustMobileNumber());
				detail.setCustNationality(item.getCustNationality());
				detail.setCustPassPort(item.getCustPassPort());
			}
			
		}catch (Exception e) {
			logger.error(e);
			logger.debug("Leaving");
			e.printStackTrace();
		}*/
		logger.debug("Leaving");
		return corePolice;
		
	}

	private CorePoliceCase setStaticBlackListData(CorePoliceCase staticList, int count) {
		staticList.setCustCIF("PC"+count);
		staticList.setCustDOB(DateUtility.getUtilDate("01/01/1950", "dd/MM/yyyy"));
		staticList.setCustFName(staticList.getCustCIF()+" First Name");
		staticList.setCustLName(staticList.getCustCIF()+" Last Name");
		staticList.setCustCRCPR("EID"+count);
		staticList.setCustPassPort("PPT"+count);
		staticList.setCustMobileNumber("9711234"+count);
		staticList.setCustNationality("AE");
		return staticList;
	}
}


