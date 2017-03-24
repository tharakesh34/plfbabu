package com.pennant.pff.channelsinterface;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.interfaces.model.AmortizationSchedulePeriod;
import com.pennant.interfaces.model.BondRedeemDetail;
import com.pennant.interfaces.model.Broker;
import com.pennant.interfaces.model.Categories;
import com.pennant.interfaces.model.CustEmployeeDetail;
import com.pennant.interfaces.model.Customer;
import com.pennant.interfaces.model.CustomerAddres;
import com.pennant.interfaces.model.CustomerDocument;
import com.pennant.interfaces.model.CustomerEMail;
import com.pennant.interfaces.model.CustomerPhoneNumber;
import com.pennant.interfaces.model.DDAUpdateStatusRequest;
import com.pennant.interfaces.model.Drawee;
import com.pennant.interfaces.model.FetchFinCustDetailResponse;
import com.pennant.interfaces.model.FetchFinanceDetailsResponse;
import com.pennant.interfaces.model.Finance;
import com.pennant.interfaces.model.FinanceMainExt;
import com.pennant.interfaces.model.Guarantor;
import com.pennant.interfaces.model.JointBorrower;
import com.pennant.interfaces.model.LimitDetails;
import com.pennant.interfaces.model.ProductCodes;
import com.pennant.interfaces.model.Repayment;
import com.pennant.interfaces.model.Transaction;
import com.pennant.pff.interfaces.util.DateUtility;

public class PFFDataAccess {
	private final Logger logger = LoggerFactory.getLogger(PFFDataAccess.class);

	private DataSource dataSource;
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	protected Connection connection;

	public List<Finance> getCustomerFinanceList(String customerNo) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustomerNo", customerNo);
		
		StringBuilder   selectSql = new StringBuilder("SELECT * FROM CUSTOMERFINANCES_IVIEW ");
		selectSql.append(" WHERE CustomerNo=:CustomerNo");
		
		logger.debug("selectSql: " + selectSql.toString());
		
		RowMapper<Finance> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Finance.class);
		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	public FetchFinanceDetailsResponse getFinanceDetails(String financeRef) {
		logger.debug("Entering");
		FetchFinanceDetailsResponse response = null;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinanceRef", financeRef);
		
		StringBuilder   selectSql = new StringBuilder("SELECT * FROM FINDETAILSBYREFERENCE_IVIEW ");
		selectSql.append(" WHERE FinanceRef=:FinanceRef");
		
		logger.debug("selectSql: " + selectSql.toString());
		
		RowMapper<FetchFinanceDetailsResponse> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				FetchFinanceDetailsResponse.class);
		logger.debug("Leaving ");
		try {
			response = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		}catch(EmptyResultDataAccessException e){
			return response;
		}
		return response;
	}

	public List<Guarantor> getGuarantorDetails(String financeRef) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", financeRef);
		
		StringBuilder   selectSql = new StringBuilder("SELECT GuarantorCIF as CustomerNo, GuarantorCIFName as CustomerName");
		selectSql.append(" FROM FinGuarantorsDetails ");
		selectSql.append(" WHERE FinReference=:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		
		RowMapper<Guarantor> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Guarantor.class);
		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	public List<JointBorrower> getJointBorrowerDetails(String financeRef) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", financeRef);
		
		StringBuilder   selectSql = new StringBuilder("SELECT CustomerNo, CustomerName FROM FINJOINTACDETAILS_IVIEW ");
		selectSql.append(" WHERE FinReference=:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		
		RowMapper<JointBorrower> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(JointBorrower.class);
		logger.debug("Leaving ");
		return  this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	public List<AmortizationSchedulePeriod> getFinanceScheduleDetails(String financeRef) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReferenceNum", financeRef);
		
		StringBuilder   selectSql = new StringBuilder("SELECT * FROM FinScheduleDetails_IView ");
		selectSql.append(" WHERE ReferenceNum=:ReferenceNum");
		
		logger.debug("selectSql: " + selectSql.toString());
		
		RowMapper<AmortizationSchedulePeriod> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				AmortizationSchedulePeriod.class);
		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	public List<Transaction> getFinTransactionDetails(String financeRef, Date txnFromDate, Date txnToDate, BigDecimal txnFromAmt,BigDecimal txnToAmt ) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinanceRef", financeRef);
		source.addValue("TxnFromDate", txnFromDate);
		source.addValue("TxnToDate", txnToDate);
		source.addValue("TxnFromAmt", txnFromAmt);
		source.addValue("TxnToAmt", txnToAmt);
		
		StringBuilder   selectSql = new StringBuilder("SELECT * FROM FinTransactionHistory_IView ");
		selectSql.append(" WHERE FinanceRef=:FinanceRef");

		if(null != txnFromDate){
			selectSql.append(" AND TransactionDate >= :TxnFromDate");
		}
		if(null != txnToDate){
			selectSql.append(" AND TransactionDate <= :TxnToDate");
		}
		if(null != txnFromAmt){
			selectSql.append(" AND ConvertedAmount >= :TxnFromAmt");
		}
		if(null != txnToAmt){
			selectSql.append(" AND ConvertedAmount <= :TxnToAmt");
		}

		logger.debug("selectSql: " + selectSql.toString());
		
		RowMapper<Transaction> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				Transaction.class);
		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	public List<Repayment> getFinRepayDetails(String acctNum) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AcctNum", acctNum);
		
		StringBuilder   selectSql = new StringBuilder("SELECT * FROM FinUnpaidRepayments_IView");
		selectSql.append(" WHERE RepayAccountId=:AcctNum");
		
		logger.debug("selectSql: " + selectSql.toString());
		
		RowMapper<Repayment> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				Repayment.class);
		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}
	
	public FetchFinCustDetailResponse getFinCustInstDetails(String finReference) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		
		StringBuilder   selectSql = new StringBuilder("SELECT * FROM FinCustInstDetails_IView");
		selectSql.append(" WHERE FinReference=:FinReference ");
		
		logger.debug("selectSql: " + selectSql.toString());
		
		RowMapper<FetchFinCustDetailResponse> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				FetchFinCustDetailResponse.class);
		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
	}
	
	public boolean updateDDAReference(String ddaRefNum, String finReference) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("DdaReferenceNo", ddaRefNum);
		source.addValue("FinReference", finReference);
		
		StringBuilder updateSql = new StringBuilder("UPDATE FinanceMain_Temp SET DdaReferenceNo =:DdaReferenceNo ");
		updateSql.append(" WHERE FinReference=:FinReference ");
		
		logger.debug("selectSql: " + updateSql.toString());
		int cnt = this.namedParameterJdbcTemplate.update(updateSql.toString(), source);
		
		boolean ddaUpdated = false;
		if(cnt > 0){
			ddaUpdated = true;
		}
		logger.debug("Leaving ");
		return ddaUpdated;
	}

	/**
	 * Method for Update the DDA UPDATE STATUS REQUEST
	 * 
	 * @param ddaUpdateStatus
	 * @return
	 */
	public boolean updateDDAStatus(DDAUpdateStatusRequest ddaUpdateStatus) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("DdaAckStatus", ddaUpdateStatus.getResponseType());
		source.addValue("DdaAckDate", DateUtility.getCurrentDtTm());
		source.addValue("FinRefence", ddaUpdateStatus.getFinReference());
		source.addValue("Purpose", "REGISTRATION");
		
		StringBuilder updateSql = new StringBuilder("UPDATE DDAReferenceLog SET ");
		updateSql.append(" DdaAckStatus =:DdaAckStatus, DdaAckDate=:DdaAckDate ");
		updateSql.append(" WHERE FinRefence=:FinRefence AND Purpose=:Purpose");
		
		logger.debug("selectSql: " + updateSql.toString());
		
		int cnt = this.namedParameterJdbcTemplate.update(updateSql.toString(), source);
		
		boolean ddaStatusUpdated = false;
		if(cnt > 0){
			ddaStatusUpdated = true;
		}
		logger.debug("Leaving ");
		return ddaStatusUpdated;
	}

	/**
	 * Method for update the DDAReference number in DDAReferenceLog
	 * 
	 * @param ddaReferenceNo
	 * @param finReference
	 */
	public void updateDDARefLog(String ddaReferenceNo, String finReference) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("DdaReference", ddaReferenceNo);
		source.addValue("DdaRefReceiveDate", DateUtility.getCurrentDtTm());
		source.addValue("FinRefence", finReference);
		source.addValue("Purpose", "REGISTRATION");
		
		StringBuilder updateSql = new StringBuilder("UPDATE DDAReferenceLog SET ");
		updateSql.append(" DdaReference =:DdaReference, DdaRefReceiveDate=:DdaRefReceiveDate ");
		updateSql.append(" WHERE FinRefence=:FinRefence AND Purpose=:Purpose");
		
		logger.debug("selectSql: " + updateSql.toString());
		
		this.namedParameterJdbcTemplate.update(updateSql.toString(), source);
	
		logger.debug("Leaving ");
	}
	
	public void conclude() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (Exception e) {
			logger.error("Exception: {}", e);
		} finally {
			connection = null;
		}
	}

	/**
	 * Method for save the customer limit details
	 * 
	 * @param limitDetails
	 * @return
	 */
	public boolean saveLimitDetails(LimitDetails limitDetails) {

		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into limitdetails");
		insertSql.append(" (CustomerReference, LimitRef, SecurityMstId, PortfolioRef," );
		insertSql.append(" LevelValue, ParentLimitRef, Rev_Nrev, LimitDesc, BranchCode)" );
		insertSql.append(" Values(:CustomerReference, :LimitRef, :SecurityMstId, :PortfolioRef," );
		insertSql.append(" :LevelValue, :ParentLimitRef, :Rev_Nrev, :LimitDesc, :BranchCode)");

		logger.debug("insertSql: "+ insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitDetails);
		int number = this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		if(number > 0) {
			return true;
		} else {
			return false;
		}
	
	}

	/**
	 * Method for save limit category details
	 * 
	 * @param categories
	 * @return
	 */
	public boolean saveLimitCategories(Categories categories) {
		logger.debug("Entering");
		
        StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into limitcategories");
		insertSql.append(" (LimitRef, Category)" );
		insertSql.append(" Values(:LimitRef, :Category)");

		logger.debug("insertSql: "+ insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(categories);
		int number = this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		if(number > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean saveProductCodes(ProductCodes productCodes) {
		logger.debug("Entering");
		
        StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into limitproductcodes");
		insertSql.append(" (LimitRef, ProductCode)" );
		insertSql.append(" Values(:LimitRef, :ProductCode)");

		logger.debug("insertSql: "+ insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(productCodes);
		int number = this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		if(number > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean saveDraweeDetails(Drawee drawee) {
		logger.debug("Entering");
		
        StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into limitdrawees");
		insertSql.append(" (LimitRef, DraweeID, DraweeName)" );
		insertSql.append(" Values(:LimitRef, :DraweeID, :DraweeName)");

		logger.debug("insertSql: "+ insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(drawee);
		int number = this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		if(number > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean saveBrokerDetails(Broker broker) {
		logger.debug("Entering");
		
        StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into limitbrokers");
		insertSql.append(" (LimitRef, BrokerID, BrokerName)" );
		insertSql.append(" Values(:LimitRef, :BrokerID, :BrokerName)");

		logger.debug("insertSql: "+ insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(broker);
		int number = this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		if(number > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Method for update customer personal information
	 * 
	 * @param customer
	 * @param type
	 * @return
	 */
	public boolean updateCustomer(Customer customer, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder updateSql = new StringBuilder("Update Customers");
		updateSql.append(StringUtils.trimToEmpty(type) ); 
		updateSql.append(" Set CustID = :CustID, CustCIF = :CustCIF, CustSts = :CustSts, CustSalutationCode = :CustSalutationCode, " );
		updateSql.append(" CustFName = :CustFName, CustMName = :CustMName, CustLName = :CustLName, CustShrtName = :CustShrtName, " );
		updateSql.append(" CustMotherMaiden = :CustMotherMaiden, CustDOB = :CustDOB, CustPOB = :CustPOB, CustLng = :CustLng," );
		updateSql.append(" CustSector = :CustSector, CustIndustry = :CustIndustry, CustSegment = :CustSegment, CustNationality = :CustNationality," );
		updateSql.append(" CustDftBranch = :CustDftBranch, CustGenderCode = :CustGenderCode, CustMaritalSts = :CustMaritalSts, NoOfDependents=:NoOfDependents," );
		updateSql.append(" CustCtgCode = :CustCtgCode, CustRO1 = :CustRO1 " );
		updateSql.append(" Where CustID =:CustID");
		
		logger.debug("updateSql: "+ updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		logger.debug("Leaving");
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count : "+recordCount);
			return false;
		} else {
			return true;
		}
	}

	public boolean updateCustDocuments(CustomerDocument customerDocument, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update CustomerDocuments");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CustID = :CustID, CustDocTitle = :CustDocTitle," );
		updateSql.append(" CustDocExpDate = :CustDocExpDate, CustDocIssuedOn = :CustDocIssuedOn " );
		updateSql.append(" Where CustID =:CustID AND CustDocCategory =:CustDocCategory");
		
		logger.debug("updateSql: "+ updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerDocument);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count : "+recordCount);
			return false;
		} else {
			return true;
		}
	}
	
	public boolean getCustomerDocuments(CustomerDocument customerDocument, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT COUNT(*) " );
		selectSql.append(" FROM  CustomerDocuments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :custID AND CustDocCategory = :CustDocCategory");

		logger.debug("selectSql: " + selectSql.toString());		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerDocument);

		int recordCount = 0;
		try {
			recordCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			recordCount = 0;
		}
		logger.debug("Leaving");
		if(recordCount > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method insert new Records into CustomerDocuments
	 * 
	 * save Customer Documents
	 * 
	 * @param Customer
	 *            Documents (customerDocument)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return boolean
	 * 
	 */
	public boolean saveCustomerDocuments(CustomerDocument customerDocument, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into CustomerDocuments");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CustID, CustDocTitle, CustDocCategory, CustDocExpDate, CustDocIssuedOn, " );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CustID,  :CustDocTitle, :CustDocExpDate, :CustDocIssuedOn, :CustDocCategory, " );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerDocument);
		int recordCount = 0;
		
		recordCount = this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		if(recordCount > 0) {
			return true;
		} else {
			return false;
		}
	}


	public boolean updateCustEmployee(CustEmployeeDetail custEmployeeDetail, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" Update CustEmployeeDetail");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CustID = :CustID, EmpStatus = :EmpStatus, EmpSector = :EmpSector," );
		updateSql.append(" EmpName = :EmpName,  EmpDesg = :EmpDesg, EmpDept = :EmpDept,");
		updateSql.append(" EmpFrom = :EmpFrom, MonthlyIncome = :MonthlyIncome ");
		updateSql.append(" Where CustID =:CustID ");

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(custEmployeeDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count : "+recordCount);
			return false;
		} else {
			return true;
		}
	}

	public boolean getCustEmployee(CustEmployeeDetail custEmployeeDetail, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder();	
		selectSql.append(" SELECT COUNT(*)  FROM  CustEmployeeDetail ");
		selectSql.append(" Where CustID = :CustID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(custEmployeeDetail);

		int recordCount = 0;
		try{
			recordCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);	
		}catch (EmptyResultDataAccessException e) {
			recordCount = 0;
		}
		logger.debug("Leaving");
		if(recordCount > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean saveCustEmployee(CustEmployeeDetail custEmployeeDetail, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into CustEmployeeDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CustID, EmpStatus, EmpSector, EmpName, EmpDesg," );
		insertSql.append(" EmpDept , EmpFrom, MonthlyIncome," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CustID, :EmpStatus, :EmpSector, :EmpName, :EmpDesg,");
		insertSql.append(" :EmpDept, :EmpFrom, :MonthlyIncome," );
		insertSql.append(" :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(custEmployeeDetail);
		int recordCount = 0;

		recordCount = this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		if(recordCount > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean updateCustAddress(CustomerAddres custAddress, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update CustomerAddresses");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CustID = :CustID," );
		updateSql.append(" CustAddrHNbr = :CustAddrHNbr, CustFlatNbr = :CustFlatNbr," );
		updateSql.append(" CustAddrStreet = :CustAddrStreet, CustAddrLine1 = :CustAddrLine1," );
		updateSql.append(" CustAddrCountry = :CustAddrCountry, CustAddrCity = :CustAddrCity " );
		updateSql.append(" Where CustID =:CustID AND CustAddrType =:custAddrType");

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(custAddress);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);

		logger.debug("Leaving");

		if (recordCount >= 0) {
			logger.debug("Error Update Method Count : "+recordCount);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean getCustAddress(CustomerAddres custAddress, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT COUNT(*) FROM CustomerAddresses " );
		selectSql.append(" Where CustID = :custID AND CustAddrType = :custAddrType");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(custAddress);

		int recordCount = 0;
		try {
			recordCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			recordCount = 0;
		}
		logger.debug("Leaving");
		if(recordCount > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean saveCustAddress(CustomerAddres custAddress, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into CustomerAddresses");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CustID, CustAddrType, CustAddrHNbr, CustFlatNbr, CustAddrStreet," );
		insertSql.append(" CustAddrLine1, CustAddrCountry, CustAddrCity," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId," );
		insertSql.append(" NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CustID, :CustAddrType, :CustAddrHNbr, :CustFlatNbr, :CustAddrStreet,");
		insertSql.append(" :CustAddrLine1, :CustAddrCountry, :CustAddrCity,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(custAddress);

		int recordCount = 0;
		recordCount = this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		if(recordCount > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean updateCustPhoneNumber(CustomerPhoneNumber custPhoneNumber, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" Update CustomerPhoneNumbers");
		updateSql.append( StringUtils.trimToEmpty(type) ); 
		updateSql.append(" Set PhoneCustID = :PhoneCustID," );
		updateSql.append(" PhoneCountryCode = :PhoneCountryCode, PhoneAreaCode = :PhoneAreaCode," );
		updateSql.append(" PhoneNumber = :PhoneNumber " );
		updateSql.append(" Where PhoneCustID =:PhoneCustID AND PhoneTypeCode=:PhoneTypeCode");
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(custPhoneNumber);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		logger.debug("Leaving");

		if (recordCount >= 0) {
			logger.debug("Error Update Method Count : "+recordCount);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean getCustPhoneNumber(CustomerPhoneNumber custPhoneNumber, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT  COUNT(*) " );
		selectSql.append(" FROM  CustomerPhoneNumbers");
		selectSql.append(" Where PhoneCustID =:PhoneCustID AND PhoneTypeCode=:PhoneTypeCode ") ; 

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(custPhoneNumber);

		int recordCount = 0;
		try{
			recordCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);	
		}catch (EmptyResultDataAccessException e) {
			recordCount = 0;
		}
		logger.debug("Leaving");
		if(recordCount > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean saveCustPhoneNumber(CustomerPhoneNumber custPhoneNumber, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into CustomerPhoneNumbers");
		insertSql.append(StringUtils.trimToEmpty(type) );
		insertSql.append(" (PhoneCustID, PhoneTypeCode, PhoneCountryCode, PhoneAreaCode, PhoneNumber," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:PhoneCustID, :PhoneTypeCode, :PhoneCountryCode,:PhoneAreaCode,:PhoneNumber,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(custPhoneNumber);
		int recordCount = 0;
		recordCount = this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		if(recordCount > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean updateCustEmail(CustomerEMail custEmail, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" Update CustomerEMails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CustID = :CustID, " );
		updateSql.append("  CustEMail = :CustEMail ");
		updateSql.append(" Where CustID =:CustID AND CustEMailTypeCode =:custEMailTypeCode ");
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(custEmail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count : "+recordCount);
			return false;
		} else {
			return true;
		}
	}
	
	public boolean getCustEmail(CustomerEMail custEmail, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder();	
		selectSql.append(" SELECT COUNT(*)  FROM  CustomerEMails");
		selectSql.append(" Where CustID = :custID AND CustEMailTypeCode = :custEMailTypeCode");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(custEmail);
		
		int recordCount = 0;
		try{
			recordCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);	
		}catch (EmptyResultDataAccessException e) {
			recordCount = 0;
		}
		logger.debug("Leaving");
		if(recordCount > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean saveCustEmail(CustomerEMail custEmail, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into CustomerEMails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CustID, CustEMailTypeCode, CustEMail," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CustID, :CustEMailTypeCode, :CustEMail,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(custEmail);
		int recordCount = this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		if(recordCount > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Mapping MDM code with PFF code
	 * 
	 */
    public String getMDMCode(String code,String tableName) {
		logger.debug("Entering");
		String value =new String();
		StringBuilder selectSql = new StringBuilder("Select value From "+tableName);
		selectSql.append(" where code =:Code");
		
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("Code", code);
		
		value = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),parameterSource,String.class); 
			
		logger.debug("Leaving");
		return value;
	}
	
	/**
	 * Mapping PFF code with MDM code
	 * 
	 */
    public String grtPFFCode(String value,String tableName) {
		logger.debug("Entering");
		String mdmCode =new String();
		StringBuilder selectSql = new StringBuilder("Select code From "+tableName);
		selectSql.append(" where value =:Value");
		
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("Value", value);
		
		
		mdmCode = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),parameterSource,String.class); 
				
		logger.debug("Leaving");
		
		return mdmCode;
	}

	public boolean getCustomer(long id, String type) {
		logger.debug("Entering");
		Customer customer = new Customer();
		customer.setCustID(id);	

		StringBuilder selectSql = new StringBuilder("SELECT  COUNT(*) ");
		selectSql.append(" FROM  Customers");
		selectSql.append(" Where CustID =:CustID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);

		int recordCount = 0;
		try{
			recordCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);	
		}catch (EmptyResultDataAccessException e) {
			recordCount = 0;
		}
		logger.debug("Leaving");
		if(recordCount > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Method for save S&D Fee charges details
	 * 
	 * @param lmtActReqBean
	 * @return
	 */
	public boolean saveStudyFeeCharges(LimitDetails limitDetails) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into StudyFeeCharges");
		insertSql.append(" (CustomerReference, LimitRef, StudyFee, StudyFeeCcy, StudyFeeStartDate, StudyFeeExpiryDate)" );
		insertSql.append(" Values");
		insertSql.append(" (:CustomerReference, :LimitRef, :StudyFee, :StudyFeeCcy, :StudyFeeStartDate, :StudyFeeExpiryDate)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitDetails);
		int recordCount = this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		if(recordCount > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Method for get customer limit details based on LimitRef
	 * 
	 * @param customerReference
	 * @param limitRef
	 * @return
	 */
	public LimitDetails getStudyFeeDetails(String customerReference, String limitRef) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustomerReference", customerReference);
		source.addValue("LimitRef", limitRef);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT  CustomerReference, LimitRef, StudyFee, StudyFeeCcy, StudyFeeStartDate, StudyFeeExpiryDate" );
		selectSql.append(" FROM  StudyFeeCharges");
		selectSql.append(" Where CustomerReference =:CustomerReference AND LimitRef =:LimitRef");

		logger.debug("selectSql: " + selectSql.toString());		

		LimitDetails limitDetails = null;
		try {
			RowMapper<LimitDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LimitDetails.class);
			limitDetails = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			limitDetails = null;
		}
		logger.debug("Leaving");
		return limitDetails;
	}

	public void updateStudyFeeCharges(LimitDetails limitDetails) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" Update StudyFeeCharges");
		updateSql.append(" Set CustomerReference =:CustomerReference, LimitRef =:LimitRef, StudyFee =:StudyFee,");
		updateSql.append(" StudyFeeCcy =:StudyFeeCcy, StudyFeeStartDate =:StudyFeeStartDate, StudyFeeExpiryDate =:StudyFeeExpiryDate");
		updateSql.append(" Where CustomerReference =:CustomerReference AND LimitRef =:LimitRef ");
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitDetails);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count : "+recordCount);
		} 
	}

	/**
	 * Method for fetch customer limit details based on limitRef
	 * 
	 * @param customerReference
	 * @param limitRef
	 * @return
	 */
	public LimitDetails getLimitDetails(String customerReference, String limitRef) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustomerReference", customerReference);
		source.addValue("LimitRef", limitRef);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT  CustomerReference, LimitRef, SecurityMstId, PortfolioRef, ParentLimitRef," );
		selectSql.append(" Rev_Nrev, LevelValue, LimitDesc" );
		selectSql.append(" FROM  LimitDetails");
		selectSql.append(" Where CustomerReference =:CustomerReference AND LimitRef =:LimitRef");

		logger.debug("selectSql: " + selectSql.toString());		

		LimitDetails limitDetails = null;
		try {
			RowMapper<LimitDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LimitDetails.class);
			limitDetails = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			limitDetails = null;
		}
		logger.debug("Leaving");
		return limitDetails;
	}

	/**
	 * Method for update the limit details received from ACP interface
	 * 
	 * @param limitDetails
	 */
	public void updateLimitDetails(LimitDetails limitDetails) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" Update LimitDetails");
		updateSql.append(" Set CustomerReference =:CustomerReference, LimitRef =:LimitRef, SecurityMstId =:SecurityMstId,");
		updateSql.append(" PortfolioRef =:PortfolioRef, ParentLimitRef =:ParentLimitRef, Rev_Nrev =:Rev_Nrev,");
		updateSql.append(" LimitDesc =:LimitDesc, LevelValue =:LevelValue, BranchCode =:BranchCode");
		updateSql.append(" Where CustomerReference =:CustomerReference AND LimitRef =:LimitRef ");

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitDetails);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count : "+recordCount);
		}
	}

	/**
	 * Method for delete limit category details
	 * 
	 * @param limitRef
	 * @return
	 */
	public void deleteLimitCategories(String limitRef) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("LimitRef", limitRef);
		
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" DELETE FROM LimitCategories");
		deleteSql.append(" Where LimitRef =:LimitRef");

		logger.debug("deleteSql: "+ deleteSql.toString());
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);

		logger.debug("Leaving");
	}
	
	/**
	 * Method for Delete limit product details
	 * 
	 * @param productCodes
	 */
	public void deleteLimitProductCodes(String limitRef) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("LimitRef", limitRef);
		
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" DELETE FROM LimitProductCodes");
		deleteSql.append(" Where LimitRef =:LimitRef");

		logger.debug("deleteSql: "+ deleteSql.toString());
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);

		logger.debug("Leaving");
	}

	/**
	 * Method for Delete limit drawee details
	 * 
	 * @param drawee
	 */
	public void deleteDraweeDetails(String limitRef) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("LimitRef", limitRef);
		
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" DELETE FROM LimitDrawees");
		deleteSql.append(" Where LimitRef =:LimitRef");

		logger.debug("deleteSql: "+ deleteSql.toString());
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);

		logger.debug("Leaving");
	}

	/**
	 * Method for Delete Limit Broker details
	 * 
	 * @param limitRef
	 */
	public void deleteBrokerDetails(String limitRef) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("LimitRef", limitRef);
		
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" DELETE FROM LimitBrokers");
		deleteSql.append(" Where LimitRef =:LimitRef");

		logger.debug("deleteSql: "+ deleteSql.toString());
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);

		logger.debug("Leaving");
	}

	/**
	 * Method for fetch the National bond Redeemtion details
	 * 
	 * @param purchaseRef
	 * @param hostRef
	 * @return BondRedeemDetail
	 */
	public BondRedeemDetail getBondDetails(String purchaseRef, String hostRef) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PurchaseRef", purchaseRef);
		source.addValue("HostRef", hostRef);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT  PurchaseRef, HostRef, ProductName, SukukAmount, RedeemStatus" );
		selectSql.append(" FROM  BondRedeemDetails");
		selectSql.append(" Where PurchaseRef =:PurchaseRef AND HostRef =:HostRef");

		logger.debug("selectSql: " + selectSql.toString());		

		BondRedeemDetail bondRedeemDetail = null;
		try {
			RowMapper<BondRedeemDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BondRedeemDetail.class);
			bondRedeemDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			bondRedeemDetail = null;
		}
		logger.debug("Leaving");
		return bondRedeemDetail;
	}

	/**
	 * Method for save National bond redeem details
	 * 
	 * @param detail
	 * @return boolean
	 */
	public boolean saveBondRedeemDetails(BondRedeemDetail detail) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into BondRedeemDetails");
		insertSql.append(" (ReferenceNum, PurchaseRef, HostRef, ProductName, SukukAmount, TimeStamp, RedeemStatus)" );
		insertSql.append(" Values(:ReferenceNum, :PurchaseRef, :HostRef, :ProductName," );
		insertSql.append(" :SukukAmount, :TimeStamp,  :RedeemStatus)");

		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		int number = this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		if(number > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Method for update the National bond redeem details
	 * 
	 * @param detail
	 * @return
	 */
	public boolean updateBondRedeemDetails(BondRedeemDetail detail) {
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("UPDATE BondRedeemDetails ");
		updateSql.append(" SET ReferenceNum =:ReferenceNum, PurchaseRef =:PurchaseRef, HostRef =:HostRef, ");
		updateSql.append(" ProductName =:ProductName, SukukAmount =:SukukAmount, TimeStamp =:TimeStamp, ");
		updateSql.append(" RedeemStatus =:RedeemStatus");
		updateSql.append(" WHERE PurchaseRef=:PurchaseRef ");
		logger.debug("selectSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		int recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		boolean bondRedeemSts = false;
		if(recordCount > 0){
			bondRedeemSts = true;
		}
		logger.debug("Leaving ");
		return bondRedeemSts;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++++ getter / setter +++++++++++++++++++ //
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	public FinanceMainExt getFinanceBondDetails(String purchaseRef) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PurchaseRef", purchaseRef);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT  PurchaseRef, HostRef, SukukAmount" );
		selectSql.append(" FROM  FinanceMainExt");
		selectSql.append(" Where PurchaseRef =:PurchaseRef");

		logger.debug("selectSql: " + selectSql.toString());		

		FinanceMainExt financeMainExt = null;
		try {
			RowMapper<FinanceMainExt> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMainExt.class);
			financeMainExt = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			financeMainExt = null;
		}
		logger.debug("Leaving");
		return financeMainExt;
	}

}