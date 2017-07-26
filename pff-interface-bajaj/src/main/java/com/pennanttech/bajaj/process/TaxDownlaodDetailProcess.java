package com.pennanttech.bajaj.process;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennanttech.app.util.DateUtility;
import com.pennanttech.bajaj.model.Branch;
import com.pennanttech.bajaj.model.TaxDetail;
import com.pennanttech.dataengine.DatabaseDataEngine;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.baja.BajajInterfaceConstants;
import com.pennanttech.pff.core.App;

public class TaxDownlaodDetailProcess extends DatabaseDataEngine {
	private static final Logger logger = Logger.getLogger(TaxDownlaodDetailProcess.class);

	private Date appDate;
	private Date fromDate;
	private Date toDate;
	private Map<String, String> taxStateCodesMap = null;
	private Map<String, Branch> branchMap = null;
	private Map<String, TaxDetail> entityDetailMap = null;
	private Map<String, String> provinceMap = null;
	private Map<String, String> cityMap = null;
	private Map<String, String> countryMap = null;
 	private int recordCount = 0;

	//Constant values used in the interface
	private static final String CON_BUSINESS_AREA = "CF";
	private static final String CON_SOURCE_SYSTEM = "PLF";
	private static final String CON_YES = "Y";
	private static final String CON_NO = "N";
	private static final String CON_BLANK = " ";
	private static final String CON_EOD = "EOD"; //FIXME CH To be discussed with Pradeep and Satish and remove this if not Required

	public TaxDownlaodDetailProcess(DataSource dataSource, long userId, Date valueDate, Date fromDate, Date toDate) {
		super(dataSource, App.DATABASE.name(), userId, true, valueDate, BajajInterfaceConstants.GST_TAXDOWNLOAD_STATUS);
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.appDate = valueDate;
	}

	@Override
	protected void processData() {
		this.recordCount = 0;
		boolean isError = false;
		taxStateCodesMap = null;
		try {
			loadDefaults();
			try {
				long id = saveHeader();
				processTaxDownloadData(id);
				if (recordCount <= 0) {
					throw new Exception("No records are available for the PostDates, FromDate: " + fromDate + ", ToDate: " + toDate + ", ApplicationDate: " + appDate);
				}
				updateHeader(id, recordCount);
			} catch (Exception e) {
				isError = true;
				logger.error(Literal.EXCEPTION, e);
				throw e;
			} finally {
				if (isError) {
					clearTaxDownlaodTables();
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	/**
	 * Process Tax Download Data
	 * @param id
	 */
	private void processTaxDownloadData(final long id) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource parmMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT * FROM TAXDOWNLOADDETAIL_VIEW WHERE TAXAPPLICABLE = :TAXAPPLICABLE AND ");
		sql.append("POSTAMOUNT != 0 AND POSTDATE >= :FROMDATE AND  POSTDATE <= :TODATE ");

		parmMap.addValue("FROMDATE", fromDate);
		parmMap.addValue("TODATE", toDate);	
		parmMap.addValue("TAXAPPLICABLE", true);	
		jdbcTemplate.query(sql.toString(), parmMap, new ResultSetExtractor<Long>() {
			public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
				logger.debug("Entering");
				MapSqlParameterSource map = null;
				while (rs.next()) {
					recordCount++;
					map = mapData(rs, id);
					saveDetails(map);
					map = null;
				}
				return (long) 0;
			}
		});

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Map parameter source 
	 * @param rs
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	private MapSqlParameterSource mapData(ResultSet rs, long id) throws SQLException {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource map = new MapSqlParameterSource();
		String entityCode;
		
		map.addValue("HEADERID", id);
		map.addValue("TRANSACTION_DATE", rs.getObject("POSTDATE"));

		// hostSystemTransactionID
		String hostSystemTransactionID = rs.getString("LINKEDTRANID").concat("-").concat(rs.getString("TRANSORDER"));
		map.addValue("HOST_SYSTEM_TRANSACTION_ID", hostSystemTransactionID);

		map.addValue("TRANSACTION_TYPE", rs.getObject("FINEVENT"));
		map.addValue("BUSINESS_AREA", CON_BUSINESS_AREA);
		map.addValue("SOURCE_SYSTEM", CON_SOURCE_SYSTEM);
		
		entityCode = rs.getString("ENTITYCODE");
		map.addValue("COMPANY_CODE", entityCode);

		StringBuilder customerAddress = null;
		Date lastMntOn ;
		String province; 
		// RegisteredCustomer or not
		String customerGSTIN = rs.getString("CUSTOMERGSTIN");
		if (StringUtils.trimToNull(customerGSTIN) != null) {
			map.addValue("REGISTERED_CUSTOMER", CON_YES);
			map.addValue("CUSTOMER_ID", rs.getObject("TAXCUSTCIF"));
			map.addValue("CUSTOMER_NAME", rs.getObject("TAXCUSTSHRTNAME"));
			map.addValue("CUSTOMER_GSTIN", customerGSTIN);
			map.addValue("PAN_NO", rs.getObject("TAXCUSTCRCPR"));

			//Address Details 
			customerAddress = new StringBuilder();
			customerAddress.append(StringUtils.trimToEmpty(rs.getString("TAXADDRLINE1")));
			customerAddress.append(StringUtils.trimToEmpty(rs.getString("TAXADDRLINE2")));
			customerAddress.append(StringUtils.trimToEmpty(rs.getString("TAXADDRLINE3")));
			customerAddress.append(StringUtils.trimToEmpty(rs.getString("TAXADDRLINE4")));
			customerAddress.append(StringUtils.trimToEmpty(rs.getString("TAXPINCODE")));
			
			customerAddress.append(cityMap.get(StringUtils.trimToEmpty(rs.getString("TAXCITY"))));
			province = rs.getString("TAXPROVINCE");
			customerAddress.append(provinceMap.get(province));
			customerAddress.append(countryMap.get(StringUtils.trimToEmpty(rs.getString("TAXCOUNTRY"))));

			lastMntOn = (Date) rs.getObject("TAXLASTMNTON");
		} else {
			map.addValue("REGISTERED_CUSTOMER", CON_NO);
			map.addValue("CUSTOMER_ID", rs.getObject("CUSTCIF"));
			map.addValue("CUSTOMER_NAME", rs.getObject("CUSTSHRTNAME"));
			map.addValue("CUSTOMER_GSTIN", CON_BLANK);
			map.addValue("PAN_NO", rs.getObject("CUSTCRCPR"));

			//Address Details 
			customerAddress = new StringBuilder();
			customerAddress.append(StringUtils.trimToEmpty(rs.getString("CUSTADDRHNBR")));
			customerAddress.append(StringUtils.trimToEmpty(rs.getString("CUSTFLATNBR")));
			customerAddress.append(StringUtils.trimToEmpty(rs.getString("CUSTADDRSTREET")));
			customerAddress.append(StringUtils.trimToEmpty(rs.getString("CUSTADDRLINE1")));
			customerAddress.append(StringUtils.trimToEmpty(rs.getString("CUSTADDRLINE2")));
			customerAddress.append(StringUtils.trimToEmpty(rs.getString("CUSTPOBOX")));
			customerAddress.append(StringUtils.trimToEmpty(rs.getString("CUSTADDRZIP")));
			 
			customerAddress.append(cityMap.get(StringUtils.trimToEmpty(rs.getString("CUSTADDRCITY"))));
			province = rs.getString("CUSTADDRPROVINCE");
			customerAddress.append(provinceMap.get(province));
			customerAddress.append(countryMap.get(StringUtils.trimToEmpty(rs.getString("CUSTADDRCOUNTRY"))));

			lastMntOn = (Date) rs.getObject("CUSTADDRLASTMNTON");
		}

		map.addValue("CUSTOMER_ADDRESS", customerAddress);

		String taxStateCode = getTaxStateCode(province);
		map.addValue("CUSTOMER_STATE_CODE", taxStateCode);

		Date finApprovalDate = (Date) rs.getObject("FINAPPROVEDDATE");

		//Only changes after the loan approval should be shown in the Address Change Date
		if(DateUtility.compare(lastMntOn, finApprovalDate) > 0){
			lastMntOn = DateUtility.getDBDate(DateUtility.formatDate(lastMntOn, "yyyy-MM-dd"));
			map.addValue("ADDRESS_CHANGE_DATE", lastMntOn);
		}else{
			map.addValue("ADDRESS_CHANGE_DATE", null);
		}

		map.addValue("LEDGER_CODE", rs.getObject("ACCOUNT"));
		map.addValue("HSN_SAC_CODE", rs.getObject("HSNSACCODE"));
		map.addValue("NATURE_OF_SERVICE", rs.getObject("NATUREOFSERVICE"));
		map.addValue("LOAN_ACCOUNT_NO", rs.getObject("FINREFERENCE"));
		map.addValue("PRODUCT_CODE", rs.getObject("FINTYPE"));
		map.addValue("CHARGE_CODE", 0);
		
		Branch loanBranch = branchMap.get(rs.getObject("FINBRANCH"));
	//	map.addValue("LOAN_BRANCH", loanBranch.getBranchSwiftBrnCde());
		map.addValue("LOAN_BRANCH", loanBranch.getBranchCode());
		map.addValue("LOAN_BRANCH_ADDRESS", getBranchAddress(loanBranch));
		
		// LoanBranchState
		String loanBranchState = getTaxStateCode(loanBranch.getBranchProvince());
		map.addValue("LOAN_BRANCH_STATE", loanBranchState);
		
		String userBranchCode = rs.getString("USERBRANCH");
		Branch userBranch;
		if(userBranchCode == null || userBranchCode.equals(CON_EOD)){
			userBranch = loanBranch;
		} else{
			 userBranch = branchMap.get(userBranchCode);
		}
		
		//map.addValue("LOAN_SERVICING_BRANCH", userBranch.getBranchSwiftBrnCde());
		map.addValue("LOAN_SERVICING_BRANCH", userBranch.getBranchCode());
		TaxDetail entityDetail = entityDetailMap.get(loanBranch.getBranchProvince()+"_"+entityCode);
		
		String txnBranchAddress ;
		String txnBranchStateCode ;
		if(entityDetail != null){
			map.addValue("BFL_GSTIN_NO", entityDetail.getTaxCode());
			StringBuilder gstAddress = new StringBuilder();
			gstAddress.append(StringUtils.trimToEmpty(entityDetail.getAddressLine1()));//2
			gstAddress.append(StringUtils.trimToEmpty(entityDetail.getAddressLine2()));
			gstAddress.append(StringUtils.trimToEmpty(entityDetail.getAddressLine3()));
			gstAddress.append(StringUtils.trimToEmpty(entityDetail.getAddressLine4()));
			gstAddress.append(StringUtils.trimToEmpty(entityDetail.getPinCode()));
			 
			gstAddress.append(cityMap.get(StringUtils.trimToEmpty(entityDetail.getCityCode())));
			gstAddress.append(provinceMap.get(StringUtils.trimToEmpty(entityDetail.getStateCode())));
			gstAddress.append(countryMap.get(StringUtils.trimToEmpty(entityDetail.getCountry())));

			txnBranchAddress = gstAddress.toString();
			txnBranchStateCode = entityDetail.getStateCode();
		}else{
			map.addValue("BFL_GSTIN_NO", CON_BLANK);
			txnBranchAddress = getBranchAddress(userBranch);
			txnBranchStateCode = userBranch.getBranchProvince();
		}
		
		map.addValue("TXN_BRANCH_ADDRESS", txnBranchAddress);
		map.addValue("TXN_BRANCH_STATE_CODE", getTaxStateCode(txnBranchStateCode));

		// PostAmount amount convention using currency minor units..
		BigDecimal postAmount = rs.getBigDecimal("PostAmount");
		int ccyMinorUnits = rs.getInt("CCYMINORCCYUNITS");
		BigDecimal transactionAmt = postAmount.divide(new BigDecimal(ccyMinorUnits));
		map.addValue("TRANSACTION_AMOUNT", transactionAmt);

		map.addValue("REVERSE_CHARGE_APPLICABLE", "Y");

		// InvoiceType
		String oldTransactionID = rs.getString("OLDLINKEDTRANID");
		if (StringUtils.trimToNull(oldTransactionID) != null) {
			map.addValue("INVOICE_TYPE", "C");
			map.addValue("ORIGINAL_INVOICE_NO",  oldTransactionID.concat("-").concat(rs.getString("TRANSORDER")));
		} else {
			map.addValue("INVOICE_TYPE", "I");
			map.addValue("ORIGINAL_INVOICE_NO", null);
		}
		
		logger.debug(Literal.LEAVING);
		return map;
	}
	
	/**
	 * Get Branch Address
	 * @param branch
	 * @return
	 */
	private String getBranchAddress(Branch branch){
		StringBuilder branchAddress = new StringBuilder();//1
		branchAddress.append(StringUtils.trimToEmpty(branch.getBranchAddrHNbr()));
		branchAddress.append(StringUtils.trimToEmpty(branch.getBranchFlatNbr()));
		branchAddress.append(StringUtils.trimToEmpty(branch.getBranchAddrStreet()));
		branchAddress.append(StringUtils.trimToEmpty(branch.getBranchAddrLine1()));
		branchAddress.append(StringUtils.trimToEmpty(branch.getBranchAddrLine2()));
		branchAddress.append(StringUtils.trimToEmpty(branch.getBranchPOBox()));
		 
		branchAddress.append(cityMap.get(StringUtils.trimToEmpty(branch.getBranchCity())));
		branchAddress.append(provinceMap.get(StringUtils.trimToEmpty(branch.getBranchProvince())));
		branchAddress.append(countryMap.get(StringUtils.trimToEmpty(branch.getBranchCountry())));
		return branchAddress.toString();
	}
	
	
	
	
	
	/**
	 * Prepare SQL Query for Tax Download Details
	 * @return
	 */
	private StringBuilder getTaxDownLoadDetailSql() {
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO TAXDOWNLOADDETAIL");
		sql.append(" (HEADERID,  TRANSACTION_DATE,  HOST_SYSTEM_TRANSACTION_ID,  TRANSACTION_TYPE,  BUSINESS_AREA,");
		sql.append(" SOURCE_SYSTEM,  COMPANY_CODE,  REGISTERED_CUSTOMER,  CUSTOMER_ID,  CUSTOMER_NAME,");
		sql.append(" CUSTOMER_GSTIN,  CUSTOMER_ADDRESS,  CUSTOMER_STATE_CODE,  ADDRESS_CHANGE_DATE,  PAN_NO,");
		sql.append(" LEDGER_CODE,  HSN_SAC_CODE,  NATURE_OF_SERVICE,  LOAN_ACCOUNT_NO,  PRODUCT_CODE, CHARGE_CODE,");
		sql.append(" LOAN_BRANCH,  LOAN_BRANCH_STATE,  LOAN_SERVICING_BRANCH,  BFL_GSTIN_NO,  TXN_BRANCH_ADDRESS,");
		sql.append(" TXN_BRANCH_STATE_CODE,  TRANSACTION_AMOUNT,  REVERSE_CHARGE_APPLICABLE,  INVOICE_TYPE,");
		sql.append(" ORIGINAL_INVOICE_NO, LOAN_BRANCH_ADDRESS)");
		sql.append(" values(");
		sql.append(" :HEADERID, :TRANSACTION_DATE, :HOST_SYSTEM_TRANSACTION_ID, :TRANSACTION_TYPE, :BUSINESS_AREA,");
		sql.append(" :SOURCE_SYSTEM, :COMPANY_CODE, :REGISTERED_CUSTOMER, :CUSTOMER_ID, :CUSTOMER_NAME,");
		sql.append(" :CUSTOMER_GSTIN, :CUSTOMER_ADDRESS, :CUSTOMER_STATE_CODE, :ADDRESS_CHANGE_DATE, :PAN_NO,");
		sql.append(" :LEDGER_CODE, :HSN_SAC_CODE, :NATURE_OF_SERVICE, :LOAN_ACCOUNT_NO, :PRODUCT_CODE,:CHARGE_CODE,");
		sql.append(" :LOAN_BRANCH, :LOAN_BRANCH_STATE, :LOAN_SERVICING_BRANCH, :BFL_GSTIN_NO, :TXN_BRANCH_ADDRESS,");
		sql.append(" :TXN_BRANCH_STATE_CODE, :TRANSACTION_AMOUNT, :REVERSE_CHARGE_APPLICABLE, :INVOICE_TYPE,");
		sql.append(" :ORIGINAL_INVOICE_NO, :LOAN_BRANCH_ADDRESS)");

		return sql;
	}

	/**
	 * Prepare the query for GST Download File
	 * @return
	 */
	private StringBuilder getGSTSql() {
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO LEA_GST_TMP_DTL");
		sql.append(" (TRANSACTION_DATE,  HOST_SYSTEM_TRANSACTION_ID,  TRANSACTION_TYPE,  BUSINESS_AREA,");
		sql.append(" SOURCE_SYSTEM,  COMPANY_CODE,  REGISTERED_CUSTOMER,  CUSTOMER_ID,  CUSTOMER_NAME,");
		sql.append(" CUSTOMER_GSTIN,  CUSTOMER_ADDRESS,  CUSTOMER_STATE_CODE,  ADDRESS_CHANGE_DATE,  PAN_NO,");
		sql.append(" LEDGER_CODE,  HSN_SAC_CODE,  NATURE_OF_SERVICE,  LOAN_ACCOUNT_NO,  PRODUCT_CODE, CHARGE_CODE,");
		sql.append(" LOAN_BRANCH,  LOAN_BRANCH_STATE,  LOAN_SERVICING_BRANCH,  BFL_GSTIN_NO,  TXN_BRANCH_ADDRESS,");
		sql.append(" TXN_BRANCH_STATE_CODE,  TRANSACTION_AMOUNT,  REVERSE_CHARGE_APPLICABLE,  INVOICE_TYPE,");
		sql.append(" ORIGINAL_INVOICE_NO, LOAN_BRANCH_ADDRESS)");
		sql.append(" values(");
		sql.append(" :TRANSACTION_DATE, :HOST_SYSTEM_TRANSACTION_ID, :TRANSACTION_TYPE, :BUSINESS_AREA,");
		sql.append(" :SOURCE_SYSTEM, :COMPANY_CODE, :REGISTERED_CUSTOMER, :CUSTOMER_ID, :CUSTOMER_NAME,");
		sql.append(" :CUSTOMER_GSTIN, :CUSTOMER_ADDRESS, :CUSTOMER_STATE_CODE, :ADDRESS_CHANGE_DATE, :PAN_NO,");
		sql.append(" :LEDGER_CODE, :HSN_SAC_CODE, :NATURE_OF_SERVICE, :LOAN_ACCOUNT_NO, :PRODUCT_CODE,:CHARGE_CODE,");
		sql.append(" :LOAN_BRANCH, :LOAN_BRANCH_STATE, :LOAN_SERVICING_BRANCH, :BFL_GSTIN_NO, :TXN_BRANCH_ADDRESS,");
		sql.append(" :TXN_BRANCH_STATE_CODE, :TRANSACTION_AMOUNT, :REVERSE_CHARGE_APPLICABLE, :INVOICE_TYPE,");
		sql.append(" :ORIGINAL_INVOICE_NO, :LOAN_BRANCH_ADDRESS)");

		return sql;
	}

	/**
	 * Loading the default values in to map to reduce the db iterations while processing
	 */
	private void loadDefaults() {
		logger.debug(Literal.ENTERING);
		taxStateCodesMap = setTaxStateCodeDetails();
		branchMap = setBranchDetails();
		entityDetailMap = setEntityDetails();
		provinceMap = setProvinceMap();
		cityMap = setCityMap();
		countryMap = setCountryMap();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Get the TAX State Code for a State 
	 * @return
	 */
	private Map<String, String> setTaxStateCodeDetails() {
		logger.debug(Literal.ENTERING);

		final Map<String, String> map = new HashMap<String, String>();
		String sql = "SELECT CPPROVINCE,TAXSTATECODE FROM RMTCOUNTRYVSPROVINCE";

		jdbcTemplate.query(sql, new ResultSetExtractor<Map<String, String>>() {
			public Map<String, String> extractData(ResultSet rs) throws SQLException {
				while (rs.next()) {
					map.put(rs.getString("CPPROVINCE"), rs.getString("TAXSTATECODE"));
				}
				return map;
			};
		});
		logger.debug(Literal.LEAVING);
		return map;
	}

	/**
	 * Get Tax State Code details from the existing Map
	 * @param key
	 * @return
	 */
	private String getTaxStateCode(String key) {
		if (StringUtils.trimToNull(key) == null) {
			return null;
		}
		return taxStateCodesMap.get(key);
	}

	/**
	 * Get Branch Address, Branch State and Host Branch code
	 * @return
	 */
	private Map<String, Branch> setBranchDetails() {
		logger.debug(Literal.ENTERING);
		if(branchMap != null){
			branchMap.clear();
		}
		final Map<String, Branch> map = new HashMap<String, Branch>();
		StringBuilder sql = new StringBuilder("SELECT  BranchCode, BranchDesc, BranchAddrLine1," );
		sql.append(" BranchAddrLine2, BranchPOBox, BranchCity, BranchProvince, BranchCountry,");
		sql.append(" BranchFax, BranchTel, BranchSwiftBrnCde, BranchIsActive,");
		sql.append(" BranchAddrHNbr,BranchFlatNbr,BranchAddrStreet, PinCode ");
		sql.append(" From RMTBranches ");

		RowMapper<Branch> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Branch.class);

		List<Branch> branches = this.jdbcTemplate.getJdbcOperations().query(sql.toString(), typeRowMapper);
		for (Branch branch : branches) {
			map.put(branch.getBranchCode(), branch);
		}
		
		logger.debug(Literal.LEAVING);
		return map;
	}
	
	/**
	 * Get Entity Address and Entity GSTIN Number 
	 * @return
	 */
	private Map<String, TaxDetail> setEntityDetails() {
		logger.debug(Literal.ENTERING);
		if(entityDetailMap != null){
			entityDetailMap.clear();
		}
 		final Map<String, TaxDetail> map = new HashMap<String, TaxDetail>();
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" id, country, stateCode, entityCode, taxCode, addressLine1, ");
		sql.append(" addressLine2, addressLine3, addressLine4, pinCode, cityCode ");	
		sql.append(" From TaxDetail ");

		RowMapper<TaxDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(TaxDetail.class);
		
		List<TaxDetail> taxDetails = this.jdbcTemplate.getJdbcOperations().query(sql.toString(), typeRowMapper);
		for (TaxDetail taxDetail : taxDetails) {
			map.put(taxDetail.getStateCode()+"_"+taxDetail.getEntityCode(),taxDetail);
		}
		
		logger.debug(Literal.LEAVING);
		return map;
	}
 
	/**
	 * Get the Countrycode and CountryDescription
	 * 
	 * @return
	 */
	private Map<String, String> setCountryMap() {
		logger.debug(Literal.ENTERING);

		final Map<String, String> map = new HashMap<String, String>();
		String sql = "SELECT COUNTRYCODE, COUNTRYDESC FROM BMTCOUNTRIES";

		jdbcTemplate.query(sql, new ResultSetExtractor<Map<String, String>>() {
			public Map<String, String> extractData(ResultSet rs) throws SQLException {
				while (rs.next()) {
					map.put(rs.getString("COUNTRYCODE"), rs.getString("COUNTRYDESC"));
				}
				return map;
			};
		});
		logger.debug(Literal.LEAVING);
		return map;
	}

	/**
	 * Get the Citycode and CityDescription
	 * 
	 * @return
	 */
	private Map<String, String> setCityMap() {
		logger.debug(Literal.ENTERING);

		final Map<String, String> map = new HashMap<String, String>();
		String sql = "SELECT PCCITY, PCCITYNAME FROM RMTPROVINCEVSCITY";

		jdbcTemplate.query(sql, new ResultSetExtractor<Map<String, String>>() {
			public Map<String, String> extractData(ResultSet rs) throws SQLException {
				while (rs.next()) {
					map.put(rs.getString("PCCITY"), rs.getString("PCCITYNAME"));
				}
				return map;
			};
		});
		logger.debug(Literal.LEAVING);
		return map;
	}

	/**
	 * Get the Provincecode and ProvinceDescription
	 * 
	 * @return
	 */
	private Map<String, String> setProvinceMap() {
		logger.debug(Literal.ENTERING);

		final Map<String, String> map = new HashMap<String, String>();
		String sql = "SELECT CPPROVINCE,CPPROVINCENAME FROM RMTCOUNTRYVSPROVINCE";

		jdbcTemplate.query(sql, new ResultSetExtractor<Map<String, String>>() {
			public Map<String, String> extractData(ResultSet rs) throws SQLException {
				while (rs.next()) {
					map.put(rs.getString("CPPROVINCE"), rs.getString("CPPROVINCENAME"));
				}
				return map;
			};
		});
		logger.debug(Literal.LEAVING);
		return map;
	}

	/**
	 * Save Tax Download Details to PFF Table and then Bajaj Table
	 * @param map
	 */
	private void saveDetails(MapSqlParameterSource map) {
		StringBuilder sql = null;
		sql = getTaxDownLoadDetailSql();
		destinationJdbcTemplate.update(sql.toString(), map);

		sql = getGSTSql();
		destinationJdbcTemplate.update(sql.toString(), map);
	}

	/**
	 * Save Header Details for Tax Details Download
	 * @return
	 */
	private long saveHeader() {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource map = new MapSqlParameterSource();
		long id = getNextId("SEQTAXDOWNLOADHAEDER", false);
		map.addValue("Id", id);
		map.addValue("ProcessDate", appDate);
		map.addValue("StartDate", appDate);
		map.addValue("EndDate", appDate);
		map.addValue("RecordCount", 0);
		map.addValue("Export", 0);

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO TAXDOWNLOADHAEDER (Id, ProcessDate, StartDate, EndDate, RecordCount, Export)");
		sql.append(" VALUES (:Id, :ProcessDate, :StartDate, :EndDate, :RecordCount, :Export)");

		try {
			jdbcTemplate.update(sql.toString(), map);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return id;
	}

	/**
	 * Clear the existing records from tax header if already available for the same dates
	 */
	private void clearTaxHeader() {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PROCESSDATE_FROM", fromDate);
		source.addValue("PROCESSDATE_TO", toDate);
		jdbcTemplate.update("DELETE FROM TAXDOWNLOADHAEDER WHERE PROCESSDATE >= :PROCESSDATE_FROM AND PROCESSDATE <= :PROCESSDATE_TO", source);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clear the existing records from tax details if already available for the same dates
	 */
	private void clearTaxdetails() {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("TRANSACTION_DATE_FROM", fromDate);
		source.addValue("TRANSACTION_DATE_TO", toDate);
		jdbcTemplate.update("DELETE FROM LEA_GST_TMP_DTL WHERE TRANSACTION_DATE >= :TRANSACTION_DATE_FROM AND TRANSACTION_DATE <= :TRANSACTION_DATE_TO", source);
		jdbcTemplate.update("DELETE FROM TAXDOWNLOADDETAIL WHERE TRANSACTION_DATE >= :TRANSACTION_DATE_FROM AND TRANSACTION_DATE <= :TRANSACTION_DATE_TO", source);

		logger.debug(Literal.LEAVING);

	}

	/**
	 * Update the Total Count in Tax Downloader Header 
	 * @param id
	 * @param recordCnt
	 */
	private void updateHeader(long id, int recordCnt) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("RECORDCOUNT", recordCnt);
		source.addValue("ID", id);
		jdbcTemplate.update("UPDATE TAXDOWNLOADHAEDER SET RECORDCOUNT = :RECORDCOUNT WHERE ID = :ID", source);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clear the Taxdetails and Taxheader table if error occurred during the
	 */
	private void clearTaxDownlaodTables() {
		logger.debug(Literal.ENTERING);
		clearTaxdetails();
		clearTaxHeader();
		logger.debug(Literal.LEAVING);
	}

	@Override
	protected MapSqlParameterSource mapData(ResultSet rs) throws Exception {
		return null;
	}
	
}
