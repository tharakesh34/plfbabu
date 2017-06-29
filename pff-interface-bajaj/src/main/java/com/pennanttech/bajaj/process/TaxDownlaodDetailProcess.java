package com.pennanttech.bajaj.process;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennanttech.dataengine.DatabaseDataEngine;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.Literal;

public class TaxDownlaodDetailProcess extends DatabaseDataEngine {
	private static final Logger logger = Logger.getLogger(TaxDownlaodDetailProcess.class);

	private Date appDate;
	private Date fromDate;
	private Date toDate;
	private Map<String, String> taxStateCodesMap = null;
	private int recordCount = 0;

	public TaxDownlaodDetailProcess(DataSource dataSource, long userId, Date valueDate, Date fromDate, Date toDate) {
		super(dataSource, App.DATABASE.name(), userId, true, valueDate);
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
			clearTables();
			try {
				preparePosingsData();
				long id = saveHeader();
				processTaxDownloadData(id);
				if (recordCount <= 0) {
					throw new Exception("No records are available for the POSTDATE :" + appDate);
				}
				updateHeader(id, recordCount);
			} catch (Exception e) {
				isError = true;
				logger.error(Literal.EXCEPTION, e);
				throw e;
			} finally {
				clearTables();
				if (isError) {
					clearTaxDownlaodTables();
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	// ProcessTaxDownloadData from source view
	private void processTaxDownloadData(final long id) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource parmMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT * FROM TAXDOWNLOADDETAIL_VIEW WHERE POSTDATE >= :FROMDATE AND  POSTDATE <= :TODATE ");

		parmMap.addValue("FROMDATE", fromDate);
		parmMap.addValue("TODATE", toDate);
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

	// Mapping the data from resultset to table columns.
	private MapSqlParameterSource mapData(ResultSet rs, long id) throws SQLException {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("HEADERID", id);
		map.addValue("TRANSACTION_DATE", rs.getObject("POSTDATE"));

		// hostSystemTransactionID
		String hostSystemTransactionID = rs.getString("LINKEDTRANID").concat("-").concat(rs.getString("TRANSORDER"));
		map.addValue("HOST_SYSTEM_TRANSACTION_ID", hostSystemTransactionID);

		map.addValue("TRANSACTION_TYPE", rs.getObject("FINEVENT"));
		map.addValue("BUSINESS_AREA", "CF");
		map.addValue("SOURCE_SYSTEM", "PLF");
		map.addValue("COMPANY_CODE", rs.getObject("ENTITYCODE"));

		// RegisteredCustomer or not
		String customerGSTIN = rs.getString("CUSTOMERGSTIN");
		if (StringUtils.trimToNull(customerGSTIN) == null) {
			map.addValue("REGISTERED_CUSTOMER", "N");
		} else {
			map.addValue("REGISTERED_CUSTOMER", "Y");
		}

		map.addValue("CUSTOMER_ID", rs.getObject("CUSTID"));
		map.addValue("CUSTOMER_NAME", rs.getObject("CUSTSHRTNAME"));
		map.addValue("CUSTOMER_GSTIN", customerGSTIN);

		// CustomerAddress
		StringBuilder address = null;
		String address1 = rs.getString("ADDRLINE1");
		if (StringUtils.trimToNull(address1) != null) {
			address = new StringBuilder();
			address.append(address1);
			address.append(rs.getObject("ADDRLINE2"));
			address.append(rs.getObject("ADDRLINE3"));
			address.append(rs.getObject("ADDRLINE4"));
			address.append(rs.getObject("COUNTRY"));
			address.append(rs.getObject("PROVINCE"));
			address.append(rs.getObject("CITY"));
			address.append(rs.getObject("PINCODE"));
			String taxStateCode = getTaxStateCode(rs.getString("PROVINCE"));
			map.addValue("CUSTOMER_STATE_CODE", taxStateCode);
			map.addValue("ADDRESS_CHANGE_DATE", rs.getObject("LASTMNTON"));
		} else {
			Map<String, Object> addrMap = getCustomerAddress(rs.getLong("CUSTID"));
			address = new StringBuilder();
			address.append(addrMap.get("CUSTADDRLINE1"));
			address.append(addrMap.get("CUSTADDRLINE2"));
			address.append(addrMap.get("CUSTADDRHNBR"));
			address.append(addrMap.get("CUSTFLATNBR"));
			address.append(addrMap.get("CUSTPOBOX"));
			address.append(addrMap.get("CUSTADDRCITY"));
			address.append(addrMap.get("CUSTADDRCOUNTRY"));
			address.append(addrMap.get("CUSTADDRZIP"));
			address.append(addrMap.get("CUSTADDRPROVINCE"));

			Object custAssrProvince = addrMap.get("CUSTADDRPROVINCE");
			if (custAssrProvince != null) {
				String taxStateCode = getTaxStateCode(custAssrProvince.toString());
				map.addValue("CUSTOMER_STATE_CODE", taxStateCode);
			}
			map.addValue("ADDRESS_CHANGE_DATE", addrMap.get("LASTMNTON"));
		}
		map.addValue("CUSTOMER_ADDRESS", address);

		map.addValue("PAN_NO", rs.getObject("CUSTCRCPR"));
		map.addValue("LEDGER_CODE", rs.getObject("ACCOUNT"));
		map.addValue("HSN_SAC_CODE", rs.getObject("HSNSACCODE"));
		map.addValue("NATURE_OF_SERVICE", rs.getObject("NATUREOFSERVICE"));
		map.addValue("LOAN_ACCOUNT_NO", rs.getObject("FINREFERENCE"));
		map.addValue("PRODUCT_CODE", rs.getObject("FINTYPE"));
		map.addValue("CHARGE_CODE", null);
		map.addValue("LOAN_BRANCH", rs.getObject("FINBRANCH"));
		// LoanBranchState
		String loanBranchState = getTaxStateCode(rs.getString("CPPROVINCE"));
		map.addValue("LOAN_BRANCH_STATE", loanBranchState);

		map.addValue("LOAN_SERVICING_BRANCH", rs.getObject("USERBRANCH"));
		map.addValue("BFL_GSTIN_NO", rs.getObject("TAXCODE"));

		// TxnBranchAddress
		String taxAddressLine1 = rs.getString("ADDRESSLINE1");
		String brnchAddressLine1 = rs.getString("BRANCHADDRLINE1");

		if (StringUtils.trimToNull(taxAddressLine1) != null) {
			address = new StringBuilder();
			address.append(taxAddressLine1);
			address.append(rs.getObject("ADDRESSLINE2"));
			address.append(rs.getObject("ADDRESSLINE3"));
			address.append(rs.getObject("ADDRESSLINE4"));
			address.append(rs.getObject("TAXCITYCODE"));
			address.append(rs.getObject("TAXSTATECODE"));
			address.append(rs.getObject("TAXCOUNTRY"));
			address.append(rs.getObject("TAXPINCODE"));
			String txnBranchStateCode = getTaxStateCode(rs.getString("TAXSTATECODE"));
			map.addValue("TXN_BRANCH_STATE_CODE", txnBranchStateCode);
		} else if (StringUtils.trimToNull(brnchAddressLine1) != null) {
			address = new StringBuilder();
			address.append(brnchAddressLine1);
			address.append(rs.getObject("BRANCHADDRLINE2"));
			address.append(rs.getObject("BRANCHADDRHNBR"));
			address.append(rs.getObject("BRANCHFLATNBR"));
			address.append(rs.getObject("BRANCHADDRSTREET"));
			address.append(rs.getObject("BRANCHPOBOX"));
			address.append(rs.getObject("BRANCHCITY"));
			address.append(rs.getObject("BRANCHPROVINCE"));
			address.append(rs.getObject("BRANCHCOUNTRY"));
			address.append(rs.getObject("PINCODE"));
			String txnBranchStateCode = getTaxStateCode(rs.getString("BRANCHPROVINCE"));
			map.addValue("TXN_BRANCH_STATE_CODE", txnBranchStateCode);
		}
		map.addValue("TXN_BRANCH_ADDRESS", address);

		// PostAmount amount convention using currency minor units..
		BigDecimal postAmount = rs.getBigDecimal("PostAmount");
		int ccyMinorUnits = rs.getInt("CCYMINORCCYUNITS");
		BigDecimal transactionAmt = postAmount.divide(new BigDecimal(ccyMinorUnits));
		map.addValue("TRANSACTION_AMOUNT", transactionAmt);

		map.addValue("REVERSE_CHARGE_APPLICABLE", "Y");

		// InvoiceType
		String status = rs.getString("Status");
		if ("R".equalsIgnoreCase(status)) {
			map.addValue("INVOICE_TYPE", "C");
			map.addValue("ORIGINAL_INVOICE_NO", hostSystemTransactionID);
		} else {
			map.addValue("INVOICE_TYPE", "I");
			map.addValue("ORIGINAL_INVOICE_NO", null);
		}

		logger.debug(Literal.LEAVING);
		return map;
	}

	// Preparing the SQL Query.
	private StringBuilder getSql(String table) {
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO ");
		sql.append(table);
		sql.append(" (HEADERID,  TRANSACTION_DATE,  HOST_SYSTEM_TRANSACTION_ID,  TRANSACTION_TYPE,  BUSINESS_AREA,");
		sql.append(" SOURCE_SYSTEM,  COMPANY_CODE,  REGISTERED_CUSTOMER,  CUSTOMER_ID,  CUSTOMER_NAME,");
		sql.append(" CUSTOMER_GSTIN,  CUSTOMER_ADDRESS,  CUSTOMER_STATE_CODE,  ADDRESS_CHANGE_DATE,  PAN_NO,");
		sql.append(" LEDGER_CODE,  HSN_SAC_CODE,  NATURE_OF_SERVICE,  LOAN_ACCOUNT_NO,  PRODUCT_CODE, CHARGE_CODE,");
		sql.append(" LOAN_BRANCH,  LOAN_BRANCH_STATE,  LOAN_SERVICING_BRANCH,  BFL_GSTIN_NO,  TXN_BRANCH_ADDRESS,");
		sql.append(" TXN_BRANCH_STATE_CODE,  TRANSACTION_AMOUNT,  REVERSE_CHARGE_APPLICABLE,  INVOICE_TYPE, ORIGINAL_INVOICE_NO)");
		sql.append(" values(");
		sql.append(":HEADERID, :TRANSACTION_DATE, :HOST_SYSTEM_TRANSACTION_ID, :TRANSACTION_TYPE, :BUSINESS_AREA,");
		sql.append(":SOURCE_SYSTEM, :COMPANY_CODE, :REGISTERED_CUSTOMER, :CUSTOMER_ID, :CUSTOMER_NAME,");
		sql.append(":CUSTOMER_GSTIN, :CUSTOMER_ADDRESS, :CUSTOMER_STATE_CODE, :ADDRESS_CHANGE_DATE, :PAN_NO,");
		sql.append(":LEDGER_CODE, :HSN_SAC_CODE, :NATURE_OF_SERVICE, :LOAN_ACCOUNT_NO, :PRODUCT_CODE,:CHARGE_CODE,");
		sql.append(":LOAN_BRANCH, :LOAN_BRANCH_STATE, :LOAN_SERVICING_BRANCH, :BFL_GSTIN_NO, :TXN_BRANCH_ADDRESS,");
		sql.append(":TXN_BRANCH_STATE_CODE, :TRANSACTION_AMOUNT, :REVERSE_CHARGE_APPLICABLE, :INVOICE_TYPE, :ORIGINAL_INVOICE_NO)");

		return sql;
	}

	// Loading the default values in to map to reduce the db iterations while
	// processing
	private void loadDefaults() {
		logger.debug(Literal.ENTERING);
		taxStateCodesMap = getTaxStateCodeDetails();
		logger.debug(Literal.LEAVING);
	}

	// Getting the all TAXSTATECODE , CPPROVINCE from RMTCOUNTRYVSPROVINCE
	private Map<String, String> getTaxStateCodeDetails() {
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

	// Get TAXSTATECODE from taxStateCodesMap if available. If not available
	// then fetch from DB
	private String getTaxStateCode(String key) {
		if (StringUtils.trimToNull(key) == null) {
			return null;
		}
		if (taxStateCodesMap.containsKey(key)) {
			return taxStateCodesMap.get(key);
		} else {
			return getTaxStateCodeFromDb(key);
		}
	}

	// Get TAXSTATECODE from DB
	private String getTaxStateCodeFromDb(String key) {
		StringBuilder sql = new StringBuilder();
		MapSqlParameterSource map = new MapSqlParameterSource();

		sql.append("SELECT TAXSTATECODE FROM RMTCOUNTRYVSPROVINCE WHERE CPPROVINCE = :CPPROVINCE");
		map.addValue("CPPROVINCE", key);
		return jdbcTemplate.queryForObject(sql.toString(), map, String.class);
	}

	// Get customer high priority addres from DB
	public Map<String, Object> getCustomerAddress(long custId) {
		final Map<String, Object> map = new HashMap<String, Object>();

		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT CUSTADDRLINE1, CUSTADDRLINE2, CUSTADDRHNBR, CUSTFLATNBR, CUSTADDRSTREET,");
		sql.append(
				" CUSTPOBOX, CUSTADDRCITY, CUSTADDRCOUNTRY, CUSTADDRZIP, CUSTADDRPROVINCE, LASTMNTON FROM CUSTOMERADDRESSES ");
		sql.append(" WHERE CUSTID = :CUSTID AND CUSTADDRPRIORITY= (SELECT MAX(CUSTADDRPRIORITY) ");
		sql.append(" FROM CUSTOMERADDRESSES WHERE CUSTID = :CUSTID) ");

		source.addValue("CUSTID", custId);

		jdbcTemplate.query(sql.toString(), source, new ResultSetExtractor<Map<String, Object>>() {
			public Map<String, Object> extractData(ResultSet rs) throws SQLException {
				while (rs.next()) {
					map.put("CUSTADDRLINE1", rs.getObject("CUSTADDRLINE1"));
					map.put("CUSTADDRLINE2", rs.getObject("CUSTADDRLINE2"));
					map.put("CUSTADDRHNBR", rs.getObject("CUSTADDRHNBR"));
					map.put("CUSTFLATNBR", rs.getObject("CUSTFLATNBR"));
					map.put("CUSTPOBOX", rs.getObject("CUSTPOBOX"));
					map.put("CUSTADDRCITY", rs.getObject("CUSTADDRCITY"));
					map.put("CUSTADDRCOUNTRY", rs.getObject("CUSTADDRCOUNTRY"));
					map.put("CUSTADDRZIP", rs.getObject("CUSTADDRZIP"));
					map.put("CUSTADDRPROVINCE", rs.getObject("CUSTADDRPROVINCE"));
					map.put("LASTMNTON", rs.getObject("LASTMNTON"));
				}
				return map;
			};
		});
		return map;
	}

	// Saving TAXDOWNLOADDETAIL in to DB
	private void saveDetails(MapSqlParameterSource map) {
		saveTaxDetails(map, "TAXDOWNLOADDETAIL");
		saveTaxDetails(map, "LEA_GST_TMP_DTL");
	}

	private void saveTaxDetails(MapSqlParameterSource map, String tableName) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		sql = getSql(tableName);
		destinationJdbcTemplate.update(sql.toString(), map);
		
		logger.debug(Literal.LEAVING);
	}


	// Saving TAXDOWNLOADHAEDER in to DB
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

	// Clearing the TAXDOWNLOADHAEDER table
	private void clearTaxHeader() {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PROCESSDATE_FROM", fromDate);
		source.addValue("PROCESSDATE_TO", toDate);
		jdbcTemplate.update("DELETE FROM TAXDOWNLOADHAEDER WHERE PROCESSDATE >= :PROCESSDATE_FROM AND PROCESSDATE <= PROCESSDATE_TO", source);

		logger.debug(Literal.LEAVING);
	}

	// Clearing the TAXDOWNLOADDETAIL table
	private void clearTaxdetails() {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("TRANSACTION_DATE_FROM", fromDate);
		source.addValue("TRANSACTION_DATE_TO", toDate);
		jdbcTemplate.update("DELETE FROM LEA_GST_TMP_DTL WHERE TRANSACTION_DATE >= :TRANSACTION_DATE_FROM AND TRANSACTION_DATE <= TRANSACTION_DATE_TO", source);
		jdbcTemplate.update("DELETE FROM TAXDOWNLOADDETAIL WHERE TRANSACTION_DATE >= :TRANSACTION_DATE_FROM AND TRANSACTION_DATE <= TRANSACTION_DATE_TO", source);

		logger.debug(Literal.LEAVING);

	}

	// Saving the required postings data into staging table POSTINGS_TAXDOWNLOAD
	private void preparePosingsData() {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FROMDATE", fromDate);
		source.addValue("TODATE", toDate);
		jdbcTemplate.update( "INSERT INTO POSTINGS_TAXDOWNLOAD SELECT * FROM POSTINGS WHERE POSTDATE >= :FROMDATE AND  POSTDATE <= :TODATE", source);

		logger.debug(Literal.LEAVING);
	}

	// Update the TAXDOWNLOADHAEDER record cnt
	private void updateHeader(long id, int recordCnt) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("RECORDCOUNT", recordCnt);
		source.addValue("ID", id);
		jdbcTemplate.update("UPDATE TAXDOWNLOADHAEDER SET RECORDCOUNT = :RECORDCOUNT WHERE ID = :ID", source);

		logger.debug(Literal.LEAVING);
	}

	// Clear the staging table POSTINGS_TAXDOWNLOAD
	private void clearTables() {
		logger.debug(Literal.ENTERING);

		jdbcTemplate.update("TRUNCATE TABLE POSTINGS_TAXDOWNLOAD", new MapSqlParameterSource());

		logger.debug(Literal.LEAVING);
	}

	// Clear the Taxdetails and Taxheader table if error occurred during the
	// process.
	private void clearTaxDownlaodTables() {
		logger.debug(Literal.ENTERING);
		clearTaxdetails();
		clearTaxHeader();
		logger.debug(Literal.LEAVING);
	}

	@Override
	protected MapSqlParameterSource mapData(ResultSet rs) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
