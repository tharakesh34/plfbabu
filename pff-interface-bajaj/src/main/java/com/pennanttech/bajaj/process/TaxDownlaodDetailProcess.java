package com.pennanttech.bajaj.process;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.model.finance.TaxDownload;
import com.pennanttech.app.util.DateUtility;
import com.pennanttech.bajaj.model.Branch;
import com.pennanttech.bajaj.model.Province;
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
	private Map<String, Province> provinceMap = null;
	private Map<String, Branch> branchMap = null;
	private Map<String, TaxDetail> entityDetailMap = null;
 	private Map<String, String> cityMap = null;
	private Map<String, String> countryMap = null;
	private int recordCount = 0;
	private int batchSize = 500;

	// Constant values used in the interface
	private static final String CON_BUSINESS_AREA = "CF";
	private static final String CON_SOURCE_SYSTEM = "PLF";
	private static final String CON_YES = "Y";
	private static final String CON_NO = "N";
	private static final String CON_C = "C";
	private static final String CON_I = "I";
	private static final String CON_BLANK = " ";
	private static final String EXTRACTION_TYPE_SUMMARY = "SUM";
	private static final String CON_REGISTERED = "REGISTERED";
	private static final String CON_UNREGISTERED = "UNREGISTERED";
	private static final String CON_INTER = "INTER";
	private static final String CON_INTRA = "INTRA";
	private static final String REG_INTRA = "1";
	private static final String REG_INTER = "2";
	private static final String UNREG_INTRA = "3";	
	private static final String UNREG_INTER = "4";
	private static final String ADDR_DELIMITER = " ";
	private static final String CON_EOD = "EOD"; // FIXME CH To be discussed  with Pradeep and Satish and remove this if not 	Required

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
		provinceMap = null;
		try {
			loadDefaults();

			clearTables();
			try {
				preparePosingsData();
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
	 * 
	 * @param id
	 * @throws SQLException
	 */
	private void processTaxDownloadData(final long id) throws SQLException {
		processTrnExtractionTypeData(id);
		processSumExtractionTypeData(id);
	}

	private void processTrnExtractionTypeData(final long id) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource parmMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT * FROM TAXDOWNLOADDETAIL_VIEW WHERE TAXAPPLICABLE = :TAXAPPLICABLE");
		sql.append(" AND POSTAMOUNT != 0 AND POSTDATE >= :FROMDATE AND  POSTDATE <= :TODATE ");

		parmMap.addValue("FROMDATE", fromDate);
		parmMap.addValue("TODATE", toDate);
		parmMap.addValue("TAXAPPLICABLE", true);

		List<TaxDownload> list = new ArrayList<TaxDownload>();

		jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				TaxDownload taxDownload = null;
				
				recordCount++;
				taxDownload = mapTrnExtractionTypeData(rs, id);
				list.add(taxDownload);

				if (list.size() >= batchSize) {
					saveTrnExtractDetails(list);
					list.clear();
				}
			
			}
		});

		if (!list.isEmpty()) {
			saveTrnExtractDetails(list);
			list.clear();
		}
		logger.debug(Literal.LEAVING);
	}

	private void processSumExtractionTypeData(long id) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource parmMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT  T1.POSTDATE, T1.ACCOUNT, T1.FINREFERENCE, T1.USERBRANCH, ");
		sql.append(" T1.POSTAMOUNT, T1.ACCOUNTTYPE, T2.FINTYPE, T2.FINBRANCH, T4.ENTITYCODE, T5.ENTITYDESC, ");
		sql.append(" T6.CUSTADDRPROVINCE, T7.PROVINCE TAXPROVINCE, T9.EXTRACTIONTYPE, T10.BRANCHPROVINCE");
		sql.append(" FROM POSTINGS_TAXDOWNLOAD T1 ");
		sql.append(" INNER JOIN FINANCEMAIN T2 ON T1.FINREFERENCE = T2.FINREFERENCE  ");
		sql.append(" INNER JOIN RMTFINANCETYPES T3 ON T2.FINTYPE = T3.FINTYPE ");
		sql.append(" INNER JOIN SMTDIVISIONDETAIL T4 ON T3.FINDIVISION = T4.DIVISIONCODE ");
		sql.append(" INNER JOIN ENTITIES T5 ON T4.ENTITYCODE = T5.ENTITYCODE ");
		sql.append(" INNER JOIN CUSTOMERAddresses T6 ON T6.CUSTID = T2.CUSTID And CustAddrPriority = 5 ");
		sql.append(" LEFT JOIN FINTAXDETAIL T7 ON T7.FINREFERENCE = T1.FINREFERENCE  ");
		sql.append(" INNER JOIN RMTACCOUNTTYPES T9 ON T9.ACTYPE = T1.ACCOUNTTYPE  ");
		sql.append(" INNER JOIN RMTBranches T10 ON T10.BranchCode = T2.FinBranch  ");
		sql.append(" WHERE T9.ExtractionType = :EXTRACTIONTYPE AND POSTAMOUNT != 0 AND POSTDATE >= :FROMDATE AND  POSTDATE <= :TODATE ");
		sql.append(" ORDER BY T1.FINREFERENCE, T4.EntityCode, T1.Account, T10.BRANCHPROVINCE ");

		parmMap.addValue("FROMDATE", fromDate);
		parmMap.addValue("TODATE", toDate);
		parmMap.addValue("EXTRACTIONTYPE", EXTRACTION_TYPE_SUMMARY);	
		
		List<TaxDownload> list = new ArrayList<TaxDownload>();
		jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				TaxDownload taxDownload = null;
				Map<String, TaxDownload> map = new HashMap<String, TaxDownload>();
				
				String finBranch = null;
				String entityCode = null;
				String loanProvince = null;
				String customerProvince = null;
			    TaxDetail entityDetail = null;
			    String key = null;
			    String finReference = null;
			    String branchProvince = null;
				
				String entityName = null;
				String bflGSTIN = null;
				String ledgerCode = null;
				BigDecimal postAmount = BigDecimal.ZERO;
				
				while (rs.next()) {
					recordCount++;
					
					entityName = rs.getString("ENTITYDESC");//1
					
					entityCode = rs.getString("ENTITYCODE");
					Branch loanBranch = branchMap.get(rs.getString("FINBRANCH"));
					loanProvince = loanBranch.getBranchProvince();
					entityDetail = entityDetailMap.get(loanProvince + "_" + entityCode);
					if (entityDetail != null) {
						bflGSTIN = entityDetail.getTaxCode();//2
					}  
					ledgerCode = rs.getString("ACCOUNT");//3
					finBranch = rs.getString("FINBRANCH");//4
					postAmount = rs.getBigDecimal("POSTAMOUNT");//6
					finReference = rs.getString("FINREFERENCE");
					branchProvince = rs.getString("BRANCHPROVINCE");
					
					customerProvince = rs.getString("TAXPROVINCE");
					boolean registered = false;
					boolean inter = false;
				
					if (StringUtils.trimToNull(customerProvince) != null) {
						registered = true;
					} else {
						customerProvince = rs.getString("CUSTADDRPROVINCE");
					}

					if (StringUtils.equals(customerProvince, loanProvince)) {
						inter = true;
					}
 
					 if (registered) {
						if (inter) {
							key = REG_INTER;
						} else {
							key = REG_INTRA;
						}
					} else {
						if (inter) {
							key = UNREG_INTER;
						} else {
							key = UNREG_INTRA;
						}
					}  
					
					if (taxDownload != null && (taxDownload.getFinReference().equals(finReference) 
							&& taxDownload.getBranchProvince().equals(branchProvince) && taxDownload.getEntityCode().equals(entityCode)
							&& taxDownload.getLedgerCode().equals(ledgerCode))) {
						if (map.containsKey(key)) {
							TaxDownload download = map.get(key);
							download.setAmount(download.getAmount().add(taxDownload.getAmount()));
						} else {
							taxDownload = getTaxDetails(id, rs, finBranch, entityCode, finReference, branchProvince, entityName, bflGSTIN, ledgerCode, postAmount, registered, inter);
							map.put(key, taxDownload);
						}
					} else {
						for (TaxDownload item : map.values()) {
							list.add(item);
						}
						map.clear();
						
						taxDownload = getTaxDetails(id, rs, finBranch, entityCode, finReference, branchProvince, entityName, bflGSTIN, ledgerCode, postAmount, registered, inter);
						map.put(key, taxDownload);
					}  
					 
					if (list.size() >= batchSize) {
						saveSumExtractDetails(list);
						list.clear();
					}
				}
			}
		});

		if (!list.isEmpty()) {
			saveSumExtractDetails(list);
			list.clear();
		}
		logger.debug(Literal.LEAVING);
	}

	
	private TaxDownload getTaxDetails(long id, ResultSet rs, String finBranch, String entityCode, String finReference, String branchProvince, String entityName, String bflGSTIN, String ledgerCode,
			BigDecimal postAmount, boolean registered, boolean inter) throws SQLException {
		TaxDownload taxDownload;
		taxDownload = new TaxDownload();
		taxDownload.setHeaderId(id);
		taxDownload.setEntityName(entityName);
		taxDownload.setEntityGSTIN(bflGSTIN);
		taxDownload.setLedgerCode(ledgerCode);
		taxDownload.setFinBranchId(finBranch);
		taxDownload.setAmount(postAmount);
		taxDownload.setEntityCode(entityCode);
		taxDownload.setTransactionDate(rs.getDate("POSTDATE"));
		taxDownload.setFinReference(finReference);
		taxDownload.setBranchProvince(branchProvince);
		if(registered){
			taxDownload.setRegisteredCustomer(CON_REGISTERED);
		} else {
			taxDownload.setRegisteredCustomer(CON_UNREGISTERED);
		}
		
		if(inter){
			taxDownload.setInterIntraState(CON_INTER);
		} else {
			taxDownload.setInterIntraState(CON_INTRA);
		}
		return taxDownload;
	}
	/**
	 * TaxDownload
	 * 
	 * @param ResultSet
	 * @param Id
	 * @return
	 * @throws SQLException
	 */
	private TaxDownload mapTrnExtractionTypeData(ResultSet rs, long id) throws SQLException {
		logger.debug(Literal.ENTERING);

		TaxDownload taxDownload = new TaxDownload();

		String entityCode;
		String hostSystemTransactionID;
		StringBuilder customerAddress;
		Date lastMntOn;
		String province;
		String customerGSTIN;
		String taxStateCode;
		Date finApprovalDate;
		Branch loanBranch;
		Branch userBranch;
		String loanBranchState;
		String userBranchCode;
		TaxDetail entityDetail;
		String txnBranchAddress;
		String txnBranchStateCode;

		taxDownload.setHeaderId(id);
		taxDownload.setTransactionDate(rs.getDate("POSTDATE"));
		taxDownload.setBusinessDatetime(taxDownload.getTransactionDate());
		taxDownload.setProcessDatetime(new Timestamp(System.currentTimeMillis()));
		taxDownload.setProcessedFlag(CON_NO);
		
		hostSystemTransactionID = rs.getString("LINKEDTRANID").concat("-").concat(rs.getString("TRANSORDER"));
		taxDownload.setHostSystemTransactionId(hostSystemTransactionID);
		taxDownload.setTransactionType(rs.getString("FINEVENT"));
		taxDownload.setBusinessArea(CON_BUSINESS_AREA);
		taxDownload.setSourceSystem(CON_SOURCE_SYSTEM);
		entityCode = rs.getString("ENTITYCODE");
		taxDownload.setCompanyCode(entityCode);

		customerGSTIN = rs.getString("CUSTOMERGSTIN");
		if (StringUtils.trimToNull(customerGSTIN) != null) {
			taxDownload.setRegisteredCustomer(CON_YES);
			taxDownload.setCustomerId(rs.getLong("TAXCUSTCIF"));
			taxDownload.setCustomerName(rs.getString("TAXCUSTSHRTNAME"));
			taxDownload.setCustomerGstin(customerGSTIN);
			boolean taxExempted = rs.getBoolean("TAXEXEMPTED");
			taxDownload.setExemptedCustomer(taxExempted? CON_YES : CON_NO);
			taxDownload.setPanNo(rs.getString("TAXCUSTCRCPR"));
			// Address Details
			customerAddress = new StringBuilder();
			customerAddress.append(StringUtils.trimToEmpty(rs.getString("TAXADDRLINE1")));
			customerAddress.append(ADDR_DELIMITER);
			customerAddress.append(StringUtils.trimToEmpty(rs.getString("TAXADDRLINE2")));
			customerAddress.append(ADDR_DELIMITER);
			customerAddress.append(StringUtils.trimToEmpty(rs.getString("TAXADDRLINE3")));
			customerAddress.append(ADDR_DELIMITER);
			customerAddress.append(StringUtils.trimToEmpty(rs.getString("TAXADDRLINE4")));
			customerAddress.append(ADDR_DELIMITER);
			customerAddress.append(StringUtils.trimToEmpty(rs.getString("TAXPINCODE")));
			customerAddress.append(ADDR_DELIMITER);
			customerAddress.append(cityMap.get(StringUtils.trimToEmpty(rs.getString("TAXCITY"))));
			province = rs.getString("TAXPROVINCE");
			customerAddress.append(ADDR_DELIMITER);
			customerAddress.append(provinceMap.get(province).getCPProvinceName());
			customerAddress.append(ADDR_DELIMITER);
			customerAddress.append(countryMap.get(StringUtils.trimToEmpty(rs.getString("TAXCOUNTRY"))));
			lastMntOn = rs.getDate("TAXLASTMNTON");

		} else {
			taxDownload.setRegisteredCustomer(CON_NO);
			taxDownload.setExemptedCustomer(CON_NO);
			try {
				taxDownload.setCustomerId(rs.getLong("CUSTCIF"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			taxDownload.setCustomerName(rs.getString("CUSTSHRTNAME"));
			taxDownload.setCustomerGstin(CON_BLANK);
			taxDownload.setPanNo(rs.getString("CUSTCRCPR"));

			// Address Details
			customerAddress = new StringBuilder();
			customerAddress.append(StringUtils.trimToEmpty(rs.getString("CUSTADDRHNBR")));
			customerAddress.append(ADDR_DELIMITER);
			customerAddress.append(StringUtils.trimToEmpty(rs.getString("CUSTFLATNBR")));
			customerAddress.append(ADDR_DELIMITER);
			customerAddress.append(StringUtils.trimToEmpty(rs.getString("CUSTADDRSTREET")));
			customerAddress.append(ADDR_DELIMITER);
			customerAddress.append(StringUtils.trimToEmpty(rs.getString("CUSTADDRLINE1")));
			customerAddress.append(ADDR_DELIMITER);
			customerAddress.append(StringUtils.trimToEmpty(rs.getString("CUSTADDRLINE2")));
			customerAddress.append(ADDR_DELIMITER);
			customerAddress.append(StringUtils.trimToEmpty(rs.getString("CUSTPOBOX")));
			customerAddress.append(ADDR_DELIMITER);
			customerAddress.append(StringUtils.trimToEmpty(rs.getString("CUSTADDRZIP")));
			customerAddress.append(ADDR_DELIMITER);
			customerAddress.append(cityMap.get(StringUtils.trimToEmpty(rs.getString("CUSTADDRCITY"))));
			province = rs.getString("CUSTADDRPROVINCE");
			customerAddress.append(ADDR_DELIMITER);
			customerAddress.append(provinceMap.get(province).getCPProvinceName());
			customerAddress.append(ADDR_DELIMITER);
			customerAddress.append(countryMap.get(StringUtils.trimToEmpty(rs.getString("CUSTADDRCOUNTRY"))));
			lastMntOn = rs.getDate("CUSTADDRLASTMNTON");
		}

		taxDownload.setCustomerAddress(customerAddress.toString());
		taxStateCode = getProvince(province).getTaxStateCode();
		taxDownload.setCustomerStateCode(taxStateCode);
		finApprovalDate = rs.getDate("FINAPPROVEDDATE");

		taxDownload.setToState(taxStateCode);

		// Only changes after the loan approval should be shown in the Address Change Date
		if (DateUtility.compare(lastMntOn, finApprovalDate) > 0) {
			lastMntOn = DateUtility.getDBDate(DateUtility.formatDate(lastMntOn, "yyyy-MM-dd"));
			taxDownload.setAddressChangeDate(lastMntOn);
		} else {
			taxDownload.setAddressChangeDate(null);
		}

		taxDownload.setLedgerCode(rs.getString("ACCOUNT"));
		taxDownload.setHsnSacCode(rs.getString("HSNSACCODE"));
		taxDownload.setNatureOfService(rs.getString("NATUREOFSERVICE"));
		taxDownload.setLoanAccountNo(rs.getString("FINREFERENCE"));
		taxDownload.setAgreementId(Long.parseLong(StringUtils.substring(taxDownload.getLoanAccountNo(), 
				taxDownload.getLoanAccountNo().length()-8)));
		taxDownload.setConsiderForGst(CON_YES);
		
		taxDownload.setProductCode(rs.getString("FINTYPE"));
		taxDownload.setChargeCode(0);

		loanBranch = branchMap.get(rs.getObject("FINBRANCH"));
		taxDownload.setLoanBranch(Long.valueOf(loanBranch.getBankRefNo()));
		taxDownload.setLoanBranchAddress(getBranchAddress(loanBranch));
		
		Province loanProvince =  getProvince(loanBranch.getBranchProvince());
		taxDownload.setExemptedState(loanProvince.isTaxExempted()? CON_YES : CON_NO);
		
		loanBranchState =loanProvince.getTaxStateCode();
		taxDownload.setLoanBranchState(loanBranchState);
		taxDownload.setFromState(loanBranchState);
		userBranchCode = rs.getString("USERBRANCH");
		if (userBranchCode == null || userBranchCode.equals(CON_EOD)) {
			userBranch = loanBranch;
		} else {
			userBranch = branchMap.get(userBranchCode);
		}
		taxDownload.setLoanServicingBranch(userBranch.getBranchCode());
		
		entityDetail = entityDetailMap.get(loanBranch.getBranchProvince() + "_" + entityCode);
		if (entityDetail != null) {
			taxDownload.setBflGstinNo(entityDetail.getTaxCode());

			StringBuilder gstAddress = new StringBuilder();
			gstAddress.append(StringUtils.trimToEmpty(entityDetail.getAddressLine1()));
			gstAddress.append(ADDR_DELIMITER);
			gstAddress.append(StringUtils.trimToEmpty(entityDetail.getAddressLine2()));
			gstAddress.append(ADDR_DELIMITER);
			gstAddress.append(StringUtils.trimToEmpty(entityDetail.getAddressLine3()));
			gstAddress.append(ADDR_DELIMITER);
			gstAddress.append(StringUtils.trimToEmpty(entityDetail.getAddressLine4()));
			gstAddress.append(ADDR_DELIMITER);
			gstAddress.append(StringUtils.trimToEmpty(entityDetail.getPinCode()));
			gstAddress.append(ADDR_DELIMITER);
			gstAddress.append(cityMap.get(StringUtils.trimToEmpty(entityDetail.getCityCode())));
			gstAddress.append(ADDR_DELIMITER);
			gstAddress.append(provinceMap.get(StringUtils.trimToEmpty(entityDetail.getStateCode())).getCPProvinceName());
			gstAddress.append(ADDR_DELIMITER);
			gstAddress.append(countryMap.get(StringUtils.trimToEmpty(entityDetail.getCountry())));
			txnBranchAddress = gstAddress.toString();
			txnBranchStateCode = entityDetail.getStateCode();
		} else {
			taxDownload.setBflGstinNo(CON_BLANK);
			txnBranchAddress = getBranchAddress(userBranch);
			txnBranchStateCode = userBranch.getBranchProvince();
		}
		taxDownload.setTxnBranchAddress(txnBranchAddress);
		taxDownload.setTxnBranchStateCode(getProvince(txnBranchStateCode).getTaxStateCode());

		// PostAmount amount convention using currency minor units..
		BigDecimal postAmount = rs.getBigDecimal("PostAmount");
		int ccyMinorUnits = rs.getInt("CCYMINORCCYUNITS");
		BigDecimal transactionAmt = postAmount.divide(new BigDecimal(ccyMinorUnits));
		taxDownload.setTransactionAmount(transactionAmt);
		boolean revChargeApplicable = rs.getBoolean("REVERSECHARGEAPPLICABLE");
		
		taxDownload.setReverseChargeApplicable(revChargeApplicable? CON_YES : CON_NO);
		// InvoiceType
		long oldTransactionID = rs.getLong("OLDLINKEDTRANID");
		if (oldTransactionID != 0) {
			taxDownload.setInvoiceType(CON_C);
			taxDownload.setOriginalInvoiceNo(String.valueOf(oldTransactionID).concat("-").concat(rs.getString("TRANSORDER")));
		} else {
			taxDownload.setInvoiceType(CON_I);
			taxDownload.setOriginalInvoiceNo(null);
		}

		logger.debug(Literal.LEAVING);
		return taxDownload;
	}

	/**
	 * Loading the default values in to map to reduce the db iterations while
	 * processing
	 */
	private void loadDefaults() {
		logger.debug(Literal.ENTERING);
		provinceMap = getProvinceDetails();
		branchMap = getBranchDetails();
		entityDetailMap = setEntityDetails();
 		cityMap = setCityMap();
		countryMap = setCountryMap();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Get Branch Address
	 * 
	 * @param branch
	 * @return
	 */
	private String getBranchAddress(Branch branch) {
		StringBuilder branchAddress = new StringBuilder();// 1
		branchAddress.append(StringUtils.trimToEmpty(branch.getBranchAddrHNbr()));
		branchAddress.append(ADDR_DELIMITER);
		branchAddress.append(StringUtils.trimToEmpty(branch.getBranchFlatNbr()));
		branchAddress.append(ADDR_DELIMITER);
		branchAddress.append(StringUtils.trimToEmpty(branch.getBranchAddrStreet()));
		branchAddress.append(ADDR_DELIMITER);
		branchAddress.append(StringUtils.trimToEmpty(branch.getBranchAddrLine1()));
		branchAddress.append(ADDR_DELIMITER);
		branchAddress.append(StringUtils.trimToEmpty(branch.getBranchAddrLine2()));
		branchAddress.append(ADDR_DELIMITER);
		branchAddress.append(StringUtils.trimToEmpty(branch.getBranchPOBox()));
		branchAddress.append(ADDR_DELIMITER);
		branchAddress.append(cityMap.get(StringUtils.trimToEmpty(branch.getBranchCity())));
		branchAddress.append(ADDR_DELIMITER);
		branchAddress.append(provinceMap.get(StringUtils.trimToEmpty(branch.getBranchProvince())).getCPProvinceName());
		branchAddress.append(ADDR_DELIMITER);
		branchAddress.append(countryMap.get(StringUtils.trimToEmpty(branch.getBranchCountry())));
		return branchAddress.toString();
	}

	/**
	 * Get Tax State Code details from the existing Map
	 * 
	 * @param key
	 * @return
	 */
	private Province getProvince(String key) {
		if (StringUtils.trimToNull(key) == null) {
			return null;
		}
		return provinceMap.get(key);
	}

	/**
	 * Get Branch Address, Branch State and Host Branch code
	 * 
	 * @return
	 */
	private Map<String, Branch> getBranchDetails() {
		logger.debug(Literal.ENTERING);
		if (branchMap != null) {
			branchMap.clear();
		}
		final Map<String, Branch> map = new HashMap<String, Branch>();
		StringBuilder sql = new StringBuilder("SELECT  BranchCode, BranchDesc, BranchAddrLine1,");
		sql.append(" BranchAddrLine2, BranchPOBox, BranchCity, BranchProvince, BranchCountry,");
		sql.append(" BranchFax, BranchTel, BranchSwiftBrnCde, BranchIsActive,");
		sql.append(" BankRefNo, BranchAddrHNbr, BranchFlatNbr, BranchAddrStreet, PinCode ");
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
	 * Get Branch Address, Branch State and Host Branch code
	 * 
	 * @return
	 */
	private Map<String, Province> getProvinceDetails() {
		logger.debug(Literal.ENTERING);
		if (provinceMap != null) {
			provinceMap.clear();
		}
		final Map<String, Province> map = new HashMap<String, Province>();
		StringBuilder selectSql = new StringBuilder("SELECT CPCountry, CPProvince, CPProvinceName,SystemDefault,");
		selectSql.append(" BankRefNo,CPIsActive," );
		selectSql.append(" TaxExempted, UnionTerritory, TaxStateCode, TaxAvailable, BusinessArea " );		
		selectSql.append(" FROM  RMTCountryVsProvince");

		logger.debug("selectSql: " + selectSql.toString());
		
		RowMapper<Province> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Province.class);
		
		List<Province> provinces = this.jdbcTemplate.getJdbcOperations().query(selectSql.toString(), typeRowMapper);
		for (Province province : provinces) {
			map.put(province.getCPProvince(), province);
		}
		logger.debug(Literal.LEAVING);
		return map;
	}

	/**
	 * Get Entity Address and Entity GSTIN Number
	 * 
	 * @return
	 */
	private Map<String, TaxDetail> setEntityDetails() {
		logger.debug(Literal.ENTERING);
		if (entityDetailMap != null) {
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
			map.put(taxDetail.getStateCode() + "_" + taxDetail.getEntityCode(), taxDetail);
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
	 * Save Tax Download Details to PFF Table and then Bajaj Table
	 * 
	 * @param list
	 */
	private void saveTrnExtractDetails(List<TaxDownload> list) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;

		try {
			sql = getTaxDownLoadDetailSql();
			SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(list.toArray());
			this.jdbcTemplate.batchUpdate(sql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error(Literal.ENTERING, e);
			throw e;
		} finally {
			sql = null;
		}

		try {
			sql = getGSTSql();
			SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(list.toArray());
			this.jdbcTemplate.batchUpdate(sql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error(Literal.ENTERING, e);
			throw e;
		} finally {
			sql = null;
			logger.debug(Literal.LEAVING);
		}

	}

	private void saveSumExtractDetails(List<TaxDownload> list) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		try {
			sql = new StringBuilder();
			sql.append(" INSERT INTO TAXDOWNLOADSUMMARYDETAILS");
			sql.append(" (HEADERID,  TRANSACTION_DATE, ENTITYNAME,  ENTITYGSTIN,  LEDGERCODE,  FINNONEBRANCHID,");
			sql.append(" REGISTEREDUNREGISTERED,  INTERINTRASTATE,  AMOUNT)");
			sql.append(" values(");
			sql.append(" :HeaderId, :TransactionDate, :EntityName, :EntityGSTIN, :LedgerCode, :FinBranchId,");
			sql.append(" :RegisteredCustomer, :InterIntraState, :Amount)");

			SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(list.toArray());
			this.jdbcTemplate.batchUpdate(sql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error(Literal.ENTERING, e);
			throw e;
		} finally {
			sql = null;
		}

		try {
			sql = new StringBuilder();
			sql.append(" INSERT INTO GSTSUMMARYDETAILS	");
			sql.append(" (TRANSACTION_DATE, ENTITYNAME,  ENTITYGSTIN,  LEDGERCODE,  FINNONEBRANCHID,");
			sql.append(" REGISTEREDUNREGISTERED,  INTERINTRASTATE,  AMOUNT)");
			sql.append(" values(");
			sql.append(" :TransactionDate, :EntityName, :EntityGSTIN, :LedgerCode, :FinBranchId,");
			sql.append(" :RegisteredCustomer, :InterIntraState, :Amount)");
			SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(list.toArray());
			this.jdbcTemplate.batchUpdate(sql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error(Literal.ENTERING, e);
			throw e;
		} finally {
			sql = null;
			logger.debug(Literal.LEAVING);
		}
	}

	/**
	 * Save Header Details for Tax Details Download
	 * 
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
	 * Clear the Taxdetails and Taxheader table if error occurred during the
	 */
	private void clearTaxDownlaodTables() {
		logger.debug(Literal.ENTERING);
		clearTaxdetails();
		clearTaxHeader();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clear the existing records from tax header if already available for the
	 * same dates
	 */
	private void clearTaxHeader() {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PROCESSDATE_FROM", fromDate);
		source.addValue("PROCESSDATE_TO", toDate);
		jdbcTemplate.update(
				"DELETE FROM TAXDOWNLOADHAEDER WHERE PROCESSDATE >= :PROCESSDATE_FROM AND PROCESSDATE <= :PROCESSDATE_TO", source);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clear the existing records from tax details if already available for the
	 * same dates
	 */
	private void clearTaxdetails() {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("TRANSACTION_DATE_FROM", fromDate);
		source.addValue("TRANSACTION_DATE_TO", toDate);
		jdbcTemplate.update(
				"DELETE FROM LEA_GST_TMP_DTL WHERE TRANSACTION_DATE >= :TRANSACTION_DATE_FROM AND TRANSACTION_DATE <= :TRANSACTION_DATE_TO",
				source);
		jdbcTemplate.update(
				"DELETE FROM TAXDOWNLOADDETAIL WHERE TRANSACTION_DATE >= :TRANSACTION_DATE_FROM AND TRANSACTION_DATE <= :TRANSACTION_DATE_TO",
				source);
		jdbcTemplate.update(
				"DELETE FROM TAXDOWNLOADSUMMARYDETAILS WHERE TRANSACTION_DATE >= :TRANSACTION_DATE_FROM AND TRANSACTION_DATE <= :TRANSACTION_DATE_TO",
				source);
		jdbcTemplate.update(
				"DELETE FROM GSTSUMMARYDETAILS WHERE TRANSACTION_DATE >= :TRANSACTION_DATE_FROM AND TRANSACTION_DATE <= :TRANSACTION_DATE_TO",
				source);

		logger.debug(Literal.LEAVING);

	}

	/**
	 * Duplicate the postings data to a new Table, to avoid any dependencies
	 */
	private void preparePosingsData() {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FROMDATE", fromDate);
		source.addValue("TODATE", toDate);
		jdbcTemplate.update( "INSERT INTO POSTINGS_TAXDOWNLOAD SELECT * FROM  POSTINGS WHERE POSTDATE >= :FROMDATE AND POSTDATE <= :TODATE",
		source);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Update the Total Count in Tax Downloader Header
	 * 
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
	 * Prepare SQL Query for Tax Download Details
	 * 
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
		sql.append(" ORIGINAL_INVOICE_NO, LOAN_BRANCH_ADDRESS, TO_STATE, FROM_STATE, BUSINESSDATETIME, PROCESSDATETIME,");
		sql.append(" PROCESSED_FLAG, AGREEMENTID, CONSIDER_FOR_GST, EXEMPTED_STATE, EXEMPTED_CUSTOMER )");
		sql.append(" values(");
		sql.append(" :HeaderId, :TransactionDate, :HostSystemTransactionId, :TransactionType, :BusinessArea,");
		sql.append(" :SourceSystem, :CompanyCode, :RegisteredCustomer, :CustomerId, :CustomerName,");
		sql.append(" :CustomerGstin, :CustomerAddress, :CustomerStateCode, :AddressChangeDate, :PanNo,");
		sql.append(" :LedgerCode, :HsnSacCode, :NatureOfService, :LoanAccountNo, :ProductCode, :ChargeCode,");
		sql.append(" :LoanBranch, :LoanBranchState, :LoanServicingBranch, :BflGstinNo, :TxnBranchAddress,");
		sql.append(" :TxnBranchStateCode, :TransactionAmount, :ReverseChargeApplicable, :InvoiceType,");
		sql.append(" :OriginalInvoiceNo, :LoanBranchAddress, :ToState, :FromState, :BusinessDatetime, :ProcessDatetime,");
		sql.append(" :ProcessedFlag, :AgreementId, :ConsiderForGst, :ExemptedState, :ExemptedCustomer )");
		return sql;
	}

	/**
	 * Prepare the query for GST Download details
	 * 
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
		sql.append(" ORIGINAL_INVOICE_NO, LOAN_BRANCH_ADDRESS, TO_STATE, FROM_STATE, BUSINESSDATETIME, PROCESSDATETIME,");
		sql.append(" PROCESSED_FLAG, AGREEMENTID, CONSIDER_FOR_GST, EXEMPTED_STATE, EXEMPTED_CUSTOMER )");
		sql.append(" values(");
		sql.append(" :TransactionDate, :HostSystemTransactionId, :TransactionType, :BusinessArea,");
		sql.append(" :SourceSystem, :CompanyCode, :RegisteredCustomer, :CustomerId, :CustomerName,");
		sql.append(" :CustomerGstin, :CustomerAddress, :CustomerStateCode, :AddressChangeDate, :PanNo,");
		sql.append(" :LedgerCode, :HsnSacCode, :NatureOfService, :LoanAccountNo, :ProductCode, :ChargeCode,");
		sql.append(" :LoanBranch, :LoanBranchState, :LoanServicingBranch, :BflGstinNo, :TxnBranchAddress,");
		sql.append(" :TxnBranchStateCode, :TransactionAmount, :ReverseChargeApplicable, :InvoiceType,");
		sql.append(" :OriginalInvoiceNo, :LoanBranchAddress, :ToState, :FromState, :BusinessDatetime, :ProcessDatetime,");
		sql.append(" :ProcessedFlag, :AgreementId, :ConsiderForGst, :ExemptedState, :ExemptedCustomer )");
		return sql;
	}

	/**
	 * Clear the stageing table
	 */
	private void clearTables() {
		logger.debug(Literal.ENTERING);

		jdbcTemplate.update("TRUNCATE TABLE POSTINGS_TAXDOWNLOAD", new MapSqlParameterSource());

		logger.debug(Literal.LEAVING);
	}

	@Override
	protected MapSqlParameterSource mapData(ResultSet rs) throws Exception {
		return null;
	}

}
