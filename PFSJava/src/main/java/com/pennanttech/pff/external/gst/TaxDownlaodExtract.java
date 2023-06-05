package com.pennanttech.pff.external.gst;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.TaxDetail;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.Province;
import com.pennanttech.dataengine.DatabaseDataEngine;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.external.TaxDownloadProcess;
import com.pennanttech.pff.model.external.gst.TaxDownload;

public class TaxDownlaodExtract extends DatabaseDataEngine implements TaxDownloadProcess {
	public static DataEngineStatus EXTRACT_STATUS = new DataEngineStatus("GST_TAXDOWNLOAD_DETAILS");

	private Date appDate;
	private Date fromDate;
	private Date toDate;
	private Map<String, Province> provinceMap = null;
	private Map<String, Branch> branchMap = null;
	private Map<String, TaxDetail> entityDetailMap = null;
	private Map<String, String> cityMap = null;
	private Map<String, String> countryMap = null;
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
	private static final String ADDR_DELIMITER = " ";
	private static final String CON_EOD = "EOD"; // FIXME CH To be discussed with Pradeep and Satish and remove this if
													// not Required
	private static final String CON_DEBIT = "D"; //

	public TaxDownlaodExtract(DataSource dataSource, long userId, Date valueDate, Date appDate, Date fromDate,
			Date toDate) {
		super(dataSource, App.DATABASE.name(), userId, true, valueDate, EXTRACT_STATUS);
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.appDate = appDate;
	}

	@Override
	public void process(Object... objects) {
		try {
			process("GST_TAXDOWNLOAD_DETAILS");
		} catch (Exception e) {
			throw new InterfaceException("GST_TAXDOWNLOAD_DETAILS", e.getMessage());
		}
	}

	@Override
	protected void processData() {
		provinceMap = null;
		boolean isError = false;
		try {
			loadDefaults();
			clearTables();

			try {
				preparePosingsData();

				long id = saveHeader();

				processTaxDownloadData(id);

				if (processedCount <= 0) {
					throwPostException();
				}

				updateHeader(id, processedCount);
			} catch (Exception e) {
				isError = true;
				throw e;
			} finally {
				if (isError || failedCount > 0) {
					clearTaxDownlaodTables();
					EXTRACT_STATUS.setStatus("F");
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	private void processTaxDownloadData(final long id) {
		setGstTranasactioRecords();
		processTrnExtractionTypeData(id);
		processSumExtractionTypeData(id);
	}

	private void processTrnExtractionTypeData(final long id) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" * From TaxDownloadDetail_View");
		sql.append(" Where TaxApplicable = ? and PostAmount != 0 and PostDate >= ? and PostDate <= ?");

		List<TaxDownload> list = new ArrayList<>();

		jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setBoolean(index++, true);
			ps.setBigDecimal(index++, BigDecimal.ZERO);
			ps.setDate(index++, JdbcUtil.getDate(fromDate));
			ps.setDate(index, JdbcUtil.getDate(toDate));
		}, rs -> {
			TaxDownload taxDownload = null;

			EXTRACT_STATUS.setProcessedRecords(processedCount++);

			try {
				taxDownload = mapTrnExtractionTypeData(rs, id);

				list.add(taxDownload);

				EXTRACT_STATUS.setSuccessRecords(successCount++);

				if (list.size() >= batchSize) {
					saveTrnExtractDetails(list);
					list.clear();
				}
			} catch (Exception e) {
				saveBatchLog(rs.getString("FINREFERENCE"), "F", e.getMessage());
				EXTRACT_STATUS.setFailedRecords(failedCount++);
				throw e;
			}
		});

		try {
			if (!list.isEmpty()) {
				saveTrnExtractDetails(list);

				list.clear();
			}
		} catch (Exception e) {
			EXTRACT_STATUS.setFailedRecords(failedCount++);
			throw e;
		}
	}

	private void processSumExtractionTypeData(long id) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Select pt.PostDate, pt.Account, fm.FinId, fm.FinReference, pt.UserBranch");
		sql.append(", pt.PostAmount, pt.AccountType, fm.FinType, fm.FinBranch, sd.EntityCode, e.EntityDesc");
		sql.append(", ca.CustAddrProvince CProv, td.Province, at.ExtractionType, b.BranchProvince");
		sql.append(" from Postings_TaxDownload pt");
		sql.append(" Inner Join FinanceMain fm on pt.FinID = fm.FinID");
		sql.append(" Inner Join RMTFinanceTypes ft on fm.FinType = ft.FinType");
		sql.append(" Inner Join SMTDivisiondetail sd on ft.FinDivision = sd.DivisionCode");
		sql.append(" Inner Join Entities e on sd.EntityCode = e.EntityCode");
		sql.append(" Inner Join CustomerAddresses ca on ca.CustID = fm.CustID and CustAddrPriority = ?");
		sql.append(" Left Join FinTaxDetail td on td.FinID = pt.FinID");
		sql.append(" Inner Join RMTAccountTypes at on at.AcType = pt.AccountType");
		sql.append(" Inner Join RMTBranches b ON b.BranchCode = fm.FinBranch");
		sql.append(" Where at.ExtractionType = ? and PostAmount != ? and PostDate >= ? and  PostDate <= ?");

		List<TaxDownload> list = new ArrayList<>();

		jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setInt(index++, 5);
			ps.setString(index++, EXTRACTION_TYPE_SUMMARY);
			ps.setBigDecimal(index++, BigDecimal.ZERO);
			ps.setDate(index++, JdbcUtil.getDate(fromDate));
			ps.setDate(index, JdbcUtil.getDate(toDate));

		}, rs -> {
			TaxDownload td = new TaxDownload();

			td.setHeaderId(id);
			td.setTransactionDate(JdbcUtil.getDate(rs.getDate("PostDate")));
			td.setEntityCode(rs.getString("EntityCode"));
			td.setFinReference(rs.getString("FinReference"));
			td.setFinID(rs.getLong("FinID"));
			td.setEntityName(rs.getString("EntityDesc"));
			td.setEntityGSTIN(null);
			td.setLedgerCode(rs.getString("Account"));
			td.setFinBranchId(rs.getString("FinBranch"));
			td.setAmount(rs.getBigDecimal("PostAmount"));
			td.setRegisteredCustomer(CON_UNREGISTERED);
			td.setInterIntraState(CON_INTRA);

			if (StringUtils.trimToNull(rs.getString("Province")) != null) {
				td.setRegisteredCustomer(CON_REGISTERED);
			}

			String loanProvince = StringUtils.trimToEmpty(rs.getString("BranchProvince"));
			if (loanProvince.equals(rs.getString("Province")) || loanProvince.equals(rs.getString("CProv"))) {
				td.setInterIntraState(CON_INTER);
			}

			TaxDetail taxDtls = entityDetailMap.get(loanProvince + "_" + rs.getString("EntityCode"));
			if (taxDtls != null) {
				td.setEntityGSTIN(taxDtls.getTaxCode());
			}

			TaxDownload taxDwl = isExists(list, td);

			if (taxDwl != null) {
				taxDwl.getAmount().add(taxDwl.getAmount().add(td.getAmount()));
			} else {
				list.add(td);
			}
		});

		if (CollectionUtils.isNotEmpty(list)) {
			saveSumExtractDetails(list);

			list.clear();
		}
	}

	private TaxDownload isExists(List<TaxDownload> list, TaxDownload taxDownload) {
		for (TaxDownload td : list) {
			if (StringUtils.equals(taxDownload.getEntityCode(), td.getEntityCode())
					&& StringUtils.equals(taxDownload.getEntityGSTIN(), td.getEntityGSTIN())
					&& StringUtils.equals(taxDownload.getLedgerCode(), td.getLedgerCode())
					&& StringUtils.equals(taxDownload.getFinBranchId(), td.getFinBranchId())
					&& StringUtils.equals(taxDownload.getRegisteredCustomer(), td.getRegisteredCustomer())
					&& StringUtils.equals(taxDownload.getInterIntraState(), td.getInterIntraState())) {

				return td;
			}
		}

		return null;
	}

	private TaxDownload mapTrnExtractionTypeData(ResultSet rs, long id) throws SQLException {
		TaxDownload td = new TaxDownload();

		StringBuilder customerAddress;
		Date lastMntOn;
		String province;
		String taxStateCode;
		Date finApprovalDate;
		Branch loanBranch;
		Branch userBranch;
		String loanBranchState;
		String userBranchCode;
		TaxDetail entityDetail;
		String txnBranchAddress;
		String txnBranchStateCode;

		td.setHeaderId(id);
		td.setTransactionDate(rs.getDate("POSTDATE"));
		td.setBusinessDatetime(td.getTransactionDate());
		td.setProcessDatetime(new Timestamp(System.currentTimeMillis()));
		td.setProcessedFlag(CON_NO);
		td.setHostSystemTransactionId(rs.getString("LINKEDTRANID").concat("-").concat(rs.getString("TRANSORDER")));
		td.setTransactionType(rs.getString("FINEVENT"));
		td.setBusinessArea(CON_BUSINESS_AREA);
		td.setSourceSystem(CON_SOURCE_SYSTEM);
		td.setCompanyCode(rs.getString("ENTITYCODE"));
		td.setEntityCode(rs.getString("ENTITYCODE"));

		String customerGSTIN = rs.getString("CUSTOMERGSTIN");
		if (StringUtils.trimToNull(customerGSTIN) != null) {
			td.setRegisteredCustomer(CON_YES);
			td.setCustomerId(rs.getLong("TAXCUSTCIF"));
			td.setCustomerName(rs.getString("TAXCUSTSHRTNAME"));
			td.setCustomerGstin(customerGSTIN);
			boolean taxExempted = rs.getBoolean("TAXEXEMPTED");
			td.setExemptedCustomer(taxExempted ? CON_YES : CON_NO);
			td.setPanNo(rs.getString("TAXCUSTCRCPR"));
			customerAddress = new StringBuilder();
			String addrLine1 = StringUtils.trimToEmpty(rs.getString("TAXADDRLINE1"));
			if (!addrLine1.isEmpty()) {
				customerAddress.append(addrLine1);
				customerAddress.append(ADDR_DELIMITER);
			}

			String addrLine2 = StringUtils.trimToEmpty(rs.getString("TAXADDRLINE2"));
			if (!addrLine2.isEmpty()) {
				customerAddress.append(addrLine2);
				customerAddress.append(ADDR_DELIMITER);
			}

			String addrLine3 = StringUtils.trimToEmpty(rs.getString("TAXADDRLINE3"));
			if (!addrLine3.isEmpty()) {
				customerAddress.append(addrLine3);
				customerAddress.append(ADDR_DELIMITER);
			}

			String addrLine4 = StringUtils.trimToEmpty(rs.getString("TAXADDRLINE4"));
			if (!addrLine4.isEmpty()) {
				customerAddress.append(addrLine4);
				customerAddress.append(ADDR_DELIMITER);
			}

			String pincode = StringUtils.trimToEmpty(rs.getString("TAXPINCODE"));
			if (!pincode.isEmpty()) {
				customerAddress.append(pincode);
				customerAddress.append(ADDR_DELIMITER);
			}

			String city = StringUtils.trimToEmpty(rs.getString("TAXCITY"));
			if (!city.isEmpty()) {
				customerAddress.append(cityMap.get(city));
				customerAddress.append(ADDR_DELIMITER);
			}

			province = StringUtils.trimToEmpty(rs.getString("TAXPROVINCE"));
			if (!province.isEmpty()) {
				customerAddress.append(provinceMap.get(province).getCPProvinceName());
				customerAddress.append(ADDR_DELIMITER);
			}

			String country = StringUtils.trimToEmpty(rs.getString("TAXCOUNTRY"));
			if (!country.isEmpty()) {
				customerAddress.append(countryMap.get(country));
			}
			lastMntOn = rs.getDate("TAXLASTMNTON");

		} else {
			td.setRegisteredCustomer(CON_NO);
			td.setExemptedCustomer(CON_NO);
			try {
				td.setCustomerId(rs.getLong("CUSTCIF"));
			} catch (Exception e) {
				//
			}
			td.setCustomerName(rs.getString("CUSTSHRTNAME"));
			td.setCustomerGstin(CON_BLANK);
			td.setPanNo(rs.getString("CUSTCRCPR"));

			// Address Details
			customerAddress = new StringBuilder();

			String custAddRhnbr = StringUtils.trimToEmpty(rs.getString("CUSTADDRHNBR"));
			if (!custAddRhnbr.isEmpty()) {
				customerAddress.append(custAddRhnbr);
				customerAddress.append(ADDR_DELIMITER);
			}

			String custFlatNbr = StringUtils.trimToEmpty(rs.getString("CUSTFLATNBR"));
			if (!custFlatNbr.isEmpty()) {
				customerAddress.append(custFlatNbr);
				customerAddress.append(ADDR_DELIMITER);
			}

			String custAddrStreet = StringUtils.trimToEmpty(rs.getString("CUSTADDRSTREET"));
			if (!custAddrStreet.isEmpty()) {
				customerAddress.append(custAddrStreet);
				customerAddress.append(ADDR_DELIMITER);
			}

			String custAddrLine1 = StringUtils.trimToEmpty(rs.getString("CUSTADDRLINE1"));
			if (!custAddrLine1.isEmpty()) {
				customerAddress.append(custAddrLine1);
				customerAddress.append(ADDR_DELIMITER);
			}

			String custAddrLine2 = StringUtils.trimToEmpty(rs.getString("CUSTADDRLINE2"));
			if (!custAddrLine2.isEmpty()) {
				customerAddress.append(custAddrLine2);
				customerAddress.append(ADDR_DELIMITER);
			}

			String custPoBox = StringUtils.trimToEmpty(rs.getString("CUSTPOBOX"));
			if (!custPoBox.isEmpty()) {
				customerAddress.append(custPoBox);
				customerAddress.append(ADDR_DELIMITER);
			}

			String custAddrZip = StringUtils.trimToEmpty(rs.getString("CUSTADDRZIP"));
			if (!custAddrZip.isEmpty()) {
				customerAddress.append(custAddrZip);
				customerAddress.append(ADDR_DELIMITER);
			}

			String custAddrCity = StringUtils.trimToEmpty(rs.getString("CUSTADDRCITY"));
			if (!custAddrCity.isEmpty()) {
				customerAddress.append(cityMap.get(custAddrCity));
				customerAddress.append(ADDR_DELIMITER);
			}

			province = rs.getString("CUSTADDRPROVINCE");
			if (!StringUtils.trimToEmpty(province).isEmpty()) {
				customerAddress.append(provinceMap.get(province).getCPProvinceName());
				customerAddress.append(ADDR_DELIMITER);
			}

			String custAddrCtry = StringUtils.trimToEmpty(rs.getString("CUSTADDRCOUNTRY"));
			if (!custAddrCtry.isEmpty()) {
				customerAddress.append(countryMap.get(custAddrCtry));
			}
			lastMntOn = rs.getDate("CUSTADDRLASTMNTON");
		}

		td.setCustomerAddress(customerAddress.toString());
		taxStateCode = getProvince(province).getTaxStateCode();
		td.setCustomerStateCode(taxStateCode);
		finApprovalDate = rs.getDate("FINAPPROVEDDATE");

		td.setToState(taxStateCode);

		// Only changes after the loan approval should be shown in the Address
		// Change Date
		if (DateUtil.compare(lastMntOn, finApprovalDate) > 0) {
			td.setAddressChangeDate(DateUtil.getDatePart(lastMntOn));
		} else {
			td.setAddressChangeDate(null);
		}

		td.setLedgerCode(rs.getString("ACCOUNT"));
		td.setHsnSacCode(rs.getString("HSNSACCODE"));
		td.setNatureOfService(rs.getString("NATUREOFSERVICE"));
		td.setLoanAccountNo(rs.getString("FINREFERENCE"));
		td.setAgreementId(Long.parseLong(StringUtils.substring(td.getLoanAccountNo(),
				td.getLoanAccountNo().length() - 7, td.getLoanAccountNo().length())));
		td.setConsiderForGst(CON_YES);

		td.setProductCode(rs.getString("FINTYPE"));
		td.setChargeCode(0);

		loanBranch = branchMap.get(rs.getObject("FINBRANCH"));
		td.setLoanBranch(Long.valueOf(loanBranch.getBankRefNo()));
		td.setLoanBranchAddress(getBranchAddress(loanBranch));

		Province loanProvince = getProvince(loanBranch.getBranchProvince());
		td.setExemptedState(loanProvince.isTaxExempted() ? CON_YES : CON_NO);

		loanBranchState = loanProvince.getTaxStateCode();
		td.setLoanBranchState(loanBranchState);
		td.setFromState(loanBranchState);
		userBranchCode = rs.getString("USERBRANCH");
		if (userBranchCode == null || userBranchCode.equals(CON_EOD)) {
			userBranch = loanBranch;
		} else {
			userBranch = branchMap.get(userBranchCode);
		}
		td.setLoanServicingBranch(userBranch.getBranchCode());

		entityDetail = entityDetailMap.get(loanBranch.getBranchProvince() + "_" + td.getEntityCode());
		if (entityDetail != null) {
			td.setBflGstinNo(entityDetail.getTaxCode());

			StringBuilder gstAddress = new StringBuilder();

			String addLine1 = StringUtils.trimToEmpty(entityDetail.getAddressLine1());
			if (!addLine1.isEmpty()) {
				gstAddress.append(addLine1);
				gstAddress.append(ADDR_DELIMITER);
			}

			String addLine2 = StringUtils.trimToEmpty(entityDetail.getAddressLine2());
			if (!addLine2.isEmpty()) {
				gstAddress.append(addLine2);
				gstAddress.append(ADDR_DELIMITER);
			}

			String addLine3 = StringUtils.trimToEmpty(entityDetail.getAddressLine3());
			if (!addLine3.isEmpty()) {
				gstAddress.append(addLine3);
				gstAddress.append(ADDR_DELIMITER);
			}

			String addLine4 = StringUtils.trimToEmpty(entityDetail.getAddressLine4());
			if (!addLine4.isEmpty()) {
				gstAddress.append(addLine4);
				gstAddress.append(ADDR_DELIMITER);
			}

			String pinCode = StringUtils.trimToEmpty(entityDetail.getPinCode());
			if (!pinCode.isEmpty()) {
				gstAddress.append(pinCode);
				gstAddress.append(ADDR_DELIMITER);
			}

			String cityCode = StringUtils.trimToEmpty(entityDetail.getCityCode());
			if (!cityCode.isEmpty()) {
				gstAddress.append(cityMap.get(cityCode));
				gstAddress.append(ADDR_DELIMITER);
			}

			String stateCode = StringUtils.trimToEmpty(entityDetail.getStateCode());
			if (!stateCode.isEmpty()) {
				gstAddress.append(provinceMap.get(stateCode).getCPProvinceName());
				gstAddress.append(ADDR_DELIMITER);
			}

			String country = StringUtils.trimToEmpty(entityDetail.getCountry());
			if (!country.isEmpty()) {
				gstAddress.append(countryMap.get(country));
			}

			txnBranchAddress = gstAddress.toString();
			txnBranchStateCode = entityDetail.getStateCode();
		} else {
			td.setBflGstinNo(CON_BLANK);
			txnBranchAddress = getBranchAddress(userBranch);
			txnBranchStateCode = userBranch.getBranchProvince();
		}
		td.setTxnBranchAddress(txnBranchAddress);
		td.setTxnBranchStateCode(getProvince(txnBranchStateCode).getTaxStateCode());

		// PostAmount amount convention using currency minor units..
		BigDecimal postAmount = rs.getBigDecimal("PostAmount");
		int ccyMinorUnits = rs.getInt("CCYMINORCCYUNITS");
		BigDecimal transactionAmt = postAmount.divide(new BigDecimal(ccyMinorUnits));
		td.setTransactionAmount(transactionAmt);
		boolean revChargeApplicable = rs.getBoolean("REVERSECHARGEAPPLICABLE");

		td.setReverseChargeApplicable(revChargeApplicable ? CON_YES : CON_NO);
		// InvoiceType
		long oldTransactionID = rs.getLong("OLDLINKEDTRANID");

		// if (oldTransactionID != 0) {
		if (rs.getString("DRORCR").equals(CON_DEBIT)) {
			td.setInvoiceType(CON_C);

			if (oldTransactionID != 0) {
				td.setOriginalInvoiceNo(
						String.valueOf(oldTransactionID).concat("-").concat(rs.getString("TRANORDERID")));
			}
		} else {
			td.setInvoiceType(CON_I);
			td.setOriginalInvoiceNo(null);
		}

		return td;
	}

	private void loadDefaults() {
		provinceMap = getProvinceDetails();
		branchMap = getBranchDetails();
		entityDetailMap = setEntityDetails();
		cityMap = setCityMap();
		countryMap = setCountryMap();
	}

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

	private Province getProvince(String key) {
		if (StringUtils.trimToNull(key) == null) {
			return null;
		}

		return provinceMap.get(key);
	}

	private Map<String, Branch> getBranchDetails() {
		if (branchMap != null) {
			branchMap.clear();
		}

		final Map<String, Branch> map = new HashMap<>();
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" BranchCode, BranchDesc, BranchAddrLine1, BranchAddrLine2, BranchPOBox");
		sql.append(", BranchCity, BranchProvince, BranchCountry,BranchFax, BranchTel");
		sql.append(", BranchSwiftBrnCde, BranchIsActive, BankRefNo, BranchAddrHNbr");
		sql.append(", BranchFlatNbr, BranchAddrStreet, PinCode");
		sql.append(" From RMTBranches");

		logger.debug(Literal.SQL + sql.toString());

		List<Branch> branches = this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			Branch b = new Branch();

			b.setBranchCode(rs.getString("BranchCode"));
			b.setBranchDesc(rs.getString("BranchDesc"));
			b.setBranchAddrLine1(rs.getString("BranchAddrLine1"));
			b.setBranchAddrLine2(rs.getString("BranchAddrLine2"));
			b.setBranchPOBox(rs.getString("BranchPOBox"));
			b.setBranchCity(rs.getString("BranchCity"));
			b.setBranchProvince(rs.getString("BranchProvince"));
			b.setBranchCountry(rs.getString("BranchCountry"));
			b.setBranchFax(rs.getString("BranchFax"));
			b.setBranchTel(rs.getString("BranchTel"));
			b.setBranchSwiftBankCde(rs.getString("BranchSwiftBankCde"));
			b.setBranchIsActive(rs.getBoolean("BranchIsActive"));
			b.setBankRefNo(rs.getString("BankRefNo"));
			b.setBranchAddrHNbr(rs.getString("BranchAddrHNbr"));
			b.setBranchFlatNbr(rs.getString("BranchFlatNbr"));
			b.setBranchAddrStreet(rs.getString("BranchAddrStreet"));
			b.setPinCode(rs.getString("PinCode"));

			return b;
		});

		branches.forEach(b -> map.put(b.getBranchCode(), b));

		return map;
	}

	private Map<String, Province> getProvinceDetails() {
		if (provinceMap != null) {
			provinceMap.clear();
		}

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CPCountry, CPProvince, CPProvinceName,SystemDefault, BankRefNo, CPIsActive");
		sql.append(", TaxExempted, UnionTerritory, TaxStateCode, TaxAvailable, BusinessArea");
		sql.append(" From RMTCountryVsProvince");

		logger.debug(Literal.SQL + sql.toString());

		List<Province> provinces = this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			Province p = new Province();
			p.setCPCountry(rs.getString("CPCountry"));
			p.setCPProvince(rs.getString("CPProvince"));
			p.setSystemDefault(rs.getBoolean("SystemDefault"));
			p.setBankRefNo(rs.getString("BankRefNo"));
			p.setcPIsActive(rs.getBoolean("CPIsActive"));
			p.setTaxExempted(rs.getBoolean("TaxExempted"));
			p.setUnionTerritory(rs.getBoolean("UnionTerritory"));
			p.setTaxStateCode(rs.getString("TaxStateCode"));
			p.setTaxAvailable(rs.getBoolean("TaxAvailable"));
			p.setBusinessArea(rs.getString("BusinessArea"));

			return p;
		});

		final Map<String, Province> map = new HashMap<>();

		provinces.forEach(p -> map.put(p.getCPProvince(), p));

		return map;
	}

	private Map<String, TaxDetail> setEntityDetails() {
		if (entityDetailMap != null) {
			entityDetailMap.clear();
		}

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, Country, StateCode, EntityCode, TaxCode, AddressLine1, AddressLine2");
		sql.append(", AddressLine3, AddressLine4, PinCode, CityCode");
		sql.append(" From TaxDetail");

		logger.debug(Literal.SQL + sql.toString());

		List<TaxDetail> taxDetails = this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			TaxDetail td = new TaxDetail();

			td.setId(rs.getLong("Id"));
			td.setCountry(rs.getString("Country"));
			td.setStateCode(rs.getString("StateCode"));
			td.setEntityCode(rs.getString("EntityCode"));
			td.setTaxCode(rs.getString("TaxCode"));
			td.setAddressLine1(rs.getString("AddressLine1"));
			td.setAddressLine2(rs.getString("AddressLine2"));
			td.setAddressLine3(rs.getString("AddressLine3"));
			td.setAddressLine4(rs.getString("AddressLine4"));
			td.setPinCode(rs.getString("PinCode"));
			td.setCityCode(rs.getString("CityCode"));

			return td;
		});

		final Map<String, TaxDetail> map = new HashMap<>();

		taxDetails.forEach(td -> map.put((td.getStateCode() + "_" + td.getEntityCode()), td));

		return map;
	}

	private Map<String, String> setCountryMap() {
		String sql = "Select CountryCode, CountryDesc From BMTCountries";

		logger.debug(Literal.SQL + sql);

		List<Country> cities = jdbcOperations.query(sql, (rs, rowNum) -> {
			Country c = new Country();

			c.setCountryCode(rs.getString("CountryCode"));
			c.setCountryDesc(rs.getString("CountryDesc"));

			return c;
		});

		final Map<String, String> map = new HashMap<>();
		cities.forEach(c -> map.put(c.getCountryCode(), c.getCountryDesc()));

		return map;
	}

	private Map<String, String> setCityMap() {
		String sql = "Select PCCity, PCCityName From RMTProvinceVsCity";

		logger.debug(Literal.SQL + sql);

		List<City> cities = jdbcOperations.query(sql, (rs, rowNum) -> {
			City c = new City();

			c.setPCCity(rs.getString("PCCity"));
			c.setPCCityName(rs.getString("PCCityName"));

			return c;
		});

		final Map<String, String> map = new HashMap<>();
		cities.forEach(c -> map.put(c.getPCCity(), c.getPCCityName()));

		return map;
	}

	private void saveTrnExtractDetails(List<TaxDownload> list) {
		StringBuilder sql = getTaxDownLoadDetailSql();

		try {
			this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					TaxDownload td = list.get(i);
					setPreparedStatementForTD(ps, td);
				}

				@Override
				public int getBatchSize() {
					return list.size();
				}
			});
		} catch (Exception e) {
			throw e;
		}

		sql = getGSTSql();

		try {
			this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					TaxDownload td = list.get(i);

					setPreparedStatementForGst(ps, td, 1);
				}

				@Override
				public int getBatchSize() {
					return list.size();
				}
			});
		} catch (Exception e) {
			throw e;
		}
	}

	private void saveSumExtractDetails(List<TaxDownload> list) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Insert Into TaxDownloadSummaryDetails");
		sql.append(" (HeaderId, Transaction_Date, EntityName, EntityGSTIN, LedgerCode, FinnOneBranchId");
		sql.append(", RegisteredUnregistered, InterIntraState, Amount)");
		sql.append(" Values( ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					TaxDownload td = list.get(i);
					int index = 1;

					ps.setLong(index++, td.getHeaderId());
					ps.setDate(index++, JdbcUtil.getDate(td.getTransactionDate()));
					ps.setString(index++, td.getEntityName());
					ps.setString(index++, td.getEntityGSTIN());
					ps.setString(index++, td.getLedgerCode());
					ps.setString(index++, td.getFinBranchId());
					ps.setString(index++, td.getRegisteredCustomer());
					ps.setString(index++, td.getInterIntraState());
					ps.setBigDecimal(index++, td.getAmount());
				}

				@Override
				public int getBatchSize() {
					return list.size();
				}
			});
		} catch (Exception e) {
			throw e;
		}

		sql = new StringBuilder();
		sql.append("Insert Into GSTSummaryDetails");
		sql.append(" (Transaction_Date, EntityName, EntityGSTIN, LedgerCode, FinnOneBranchId");
		sql.append(", RegisteredUnregistered, InterIntraState, Amount)");
		sql.append(" Values( ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					TaxDownload td = list.get(i);
					int index = 1;

					ps.setDate(index++, JdbcUtil.getDate(td.getTransactionDate()));
					ps.setString(index++, td.getEntityName());
					ps.setString(index++, td.getEntityGSTIN());
					ps.setString(index++, td.getLedgerCode());
					ps.setString(index++, td.getFinBranchId());
					ps.setString(index++, td.getRegisteredCustomer());
					ps.setString(index++, td.getInterIntraState());
					ps.setBigDecimal(index++, td.getAmount());
				}

				@Override
				public int getBatchSize() {
					return list.size();
				}
			});
		} catch (

		Exception e) {
			throw e;
		}
	}

	private long saveHeader() {
		long id = getNextId("SEQTAXDOWNLOADHAEDER", false);

		String sql = "Insert Into TaxDownloadHaeder(Id, ProcessDate, StartDate, EndDate, RecordCount, Export) Values(?, ?, ?, ?, ?, ?)";

		logger.debug(Literal.SQL + sql);

		try {
			jdbcOperations.update(sql, ps -> {
				int index = 1;

				ps.setLong(index++, id);
				ps.setDate(index++, JdbcUtil.getDate(appDate));
				ps.setDate(index++, JdbcUtil.getDate(appDate));
				ps.setDate(index++, JdbcUtil.getDate(appDate));
				ps.setInt(index++, 0);
				ps.setInt(index, 0);
			});
		} catch (Exception e) {
			//
		}

		return id;
	}

	private void clearTaxDownlaodTables() {
		clearTranasction("Delete From LEA_GST_TMP_DTL Where TRANSACTION_DATE >= ? and TRANSACTION_DATE <= ?");
		clearTranasction("Delete From TAXDOWNLOADDETAIL Where TRANSACTION_DATE >= ? and TRANSACTION_DATE <= ?");
		clearTranasction("Delete From TAXDOWNLOADSUMMARYDETAILS Where TRANSACTION_DATE >= ? and TRANSACTION_DATE <= ?");
		clearTranasction("Delete From GSTSUMMARYDETAILS Where TRANSACTION_DATE >= ? and TRANSACTION_DATE <= ?");
		clearTranasction("Delete From TAXDOWNLOADHAEDER WHERE PROCESSDATE >= ? and PROCESSDATE <= ?");
	}

	private void clearTranasction(String sql) {
		logger.debug(Literal.SQL + sql);

		jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setDate(index++, JdbcUtil.getDate(appDate));
			ps.setDate(index, JdbcUtil.getDate(toDate));
		});
	}

	public void preparePosingsData() {
		String sql = "Insert Into Postings_TaxDownload Select * From Postings Where PostDate >= ? and PostDate <= ?";

		logger.debug(Literal.SQL + sql);

		jdbcOperations.update(sql, ps -> {
			int index = 1;
			ps.setDate(index++, JdbcUtil.getDate(fromDate));
			ps.setDate(index, JdbcUtil.getDate(toDate));
		});
	}

	private void updateHeader(long id, long recordCnt) {
		String sql = "Update TaxDownloadHaeder Set RecordCount = ? Where Id = ?";

		logger.debug(Literal.SQL + sql);

		jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setLong(index++, recordCnt);
			ps.setLong(index, id);

		});
	}

	private StringBuilder getTaxDownLoadDetailSql() {
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO TAXDOWNLOADDETAIL");
		sql.append(" (HEADERID, TRANSACTION_DATE, HOST_SYSTEM_TRANSACTION_ID, TRANSACTION_TYPE, BUSINESS_AREA");
		sql.append(", SOURCE_SYSTEM, COMPANY_CODE, REGISTERED_CUSTOMER, CUSTOMER_ID, CUSTOMER_NAME");
		sql.append(", CUSTOMER_GSTIN, CUSTOMER_ADDRESS, CUSTOMER_STATE_CODE, ADDRESS_CHANGE_DATE, PAN_NO");
		sql.append(", LEDGER_CODE, HSN_SAC_CODE, NATURE_OF_SERVICE, LOAN_ACCOUNT_NO, PRODUCT_CODE, CHARGE_CODE");
		sql.append(", LOAN_BRANCH, LOAN_BRANCH_STATE, LOAN_SERVICING_BRANCH, BFL_GSTIN_NO, TXN_BRANCH_ADDRESS");
		sql.append(", TXN_BRANCH_STATE_CODE, TRANSACTION_AMOUNT, REVERSE_CHARGE_APPLICABLE, INVOICE_TYPE");
		sql.append(", ORIGINAL_INVOICE_NO, LOAN_BRANCH_ADDRESS, TO_STATE, FROM_STATE, BUSINESSDATETIME");
		sql.append(", PROCESSDATETIME, PROCESSED_FLAG, AGREEMENTID, CONSIDER_FOR_GST, EXEMPTED_STATE");
		sql.append(", EXEMPTED_CUSTOMER)");
		sql.append(" Values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?)");

		return sql;
	}

	private StringBuilder getGSTSql() {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO LEA_GST_TMP_DTL");
		sql.append(" (TRANSACTION_DATE, HOST_SYSTEM_TRANSACTION_ID, TRANSACTION_TYPE, BUSINESS_AREA");
		sql.append(", SOURCE_SYSTEM, COMPANY_CODE, REGISTERED_CUSTOMER, CUSTOMER_ID, CUSTOMER_NAME");
		sql.append(", CUSTOMER_GSTIN, CUSTOMER_ADDRESS, CUSTOMER_STATE_CODE, ADDRESS_CHANGE_DATE, PAN_NO");
		sql.append(", LEDGER_CODE, HSN_SAC_CODE, NATURE_OF_SERVICE, LOAN_ACCOUNT_NO, PRODUCT_CODE, CHARGE_CODE");
		sql.append(", LOAN_BRANCH, LOAN_BRANCH_STATE, LOAN_SERVICING_BRANCH, BFL_GSTIN_NO, TXN_BRANCH_ADDRESS");
		sql.append(", TXN_BRANCH_STATE_CODE, TRANSACTION_AMOUNT, REVERSE_CHARGE_APPLICABLE, INVOICE_TYPE");
		sql.append(", ORIGINAL_INVOICE_NO, LOAN_BRANCH_ADDRESS, TO_STATE, FROM_STATE, BUSINESSDATETIME");
		sql.append(", PROCESSDATETIME, PROCESSED_FLAG, AGREEMENTID, CONSIDER_FOR_GST, EXEMPTED_STATE");
		sql.append(", EXEMPTED_CUSTOMER)");
		sql.append(" Values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?)");

		return sql;
	}

	private void setPreparedStatementForTD(PreparedStatement ps, TaxDownload td) throws SQLException {
		int index = 1;

		ps.setLong(index++, td.getHeaderId());
		setPreparedStatementForGst(ps, td, index);
	}

	private void setPreparedStatementForGst(PreparedStatement ps, TaxDownload td, int index) throws SQLException {

		ps.setDate(index++, JdbcUtil.getDate(td.getTransactionDate()));
		ps.setString(index++, td.getHostSystemTransactionId());
		ps.setString(index++, td.getTransactionType());
		ps.setString(index++, td.getBusinessArea());
		ps.setString(index++, td.getSourceSystem());
		ps.setString(index++, td.getCompanyCode());
		ps.setString(index++, td.getRegisteredCustomer());
		ps.setLong(index++, td.getCustomerId());
		ps.setString(index++, td.getCustomerName());
		ps.setString(index++, td.getCustomerGstin());
		ps.setString(index++, td.getCustomerAddress());
		ps.setString(index++, td.getCustomerStateCode());
		ps.setDate(index++, JdbcUtil.getDate(td.getAddressChangeDate()));
		ps.setString(index++, td.getPanNo());
		ps.setString(index++, td.getLedgerCode());
		ps.setString(index++, td.getHsnSacCode());
		ps.setString(index++, td.getNatureOfService());
		ps.setString(index++, td.getLoanAccountNo());
		ps.setString(index++, td.getProductCode());
		ps.setLong(index++, td.getChargeCode());
		ps.setLong(index++, td.getLoanBranch());
		ps.setString(index++, td.getLoanBranchState());
		ps.setString(index++, td.getLoanServicingBranch());
		ps.setString(index++, td.getBflGstinNo());
		ps.setString(index++, td.getTxnBranchAddress());
		ps.setString(index++, td.getTxnBranchStateCode());
		ps.setBigDecimal(index++, td.getTransactionAmount());
		ps.setString(index++, td.getReverseChargeApplicable());
		ps.setString(index++, td.getInvoiceType());
		ps.setString(index++, td.getOriginalInvoiceNo());
		ps.setString(index++, td.getLoanBranchAddress());
		ps.setString(index++, td.getToState());
		ps.setString(index++, td.getFromState());
		ps.setDate(index++, JdbcUtil.getDate(td.getBusinessDatetime()));
		ps.setDate(index++, JdbcUtil.getDate(td.getProcessDatetime()));
		ps.setString(index++, td.getProcessedFlag());
		ps.setLong(index++, td.getAgreementId());
		ps.setString(index++, td.getConsiderForGst());
		ps.setString(index++, td.getExemptedState());
		ps.setString(index++, td.getExemptedCustomer());
	}

	public void clearTables() {
		jdbcOperations.update("TRUNCATE TABLE POSTINGS_TAXDOWNLOAD");
	}

	public long setGstTranasactioRecords() {
		StringBuilder sql = new StringBuilder();
		sql.append(" Select count(1) From TaxDownloadDetail_View");
		sql.append(" Where TaxApplicable = ? and PostAmount != ? and PostDate >= ? and PostDate <= ?");

		logger.debug(Literal.SQL + sql.toString());

		java.sql.Date frmDt = JdbcUtil.getDate(fromDate);
		java.sql.Date toDt = JdbcUtil.getDate(toDate);

		totalRecords = jdbcOperations.queryForObject(sql.toString(), Long.class, 1, 0, frmDt, toDt);

		EXTRACT_STATUS.setTotalRecords(totalRecords);

		return totalRecords;
	}

	public void processGstTransactionData() {
		long id = saveHeader();
		processTrnExtractionTypeData(id);
		if (processedCount <= 0) {
			throwPostException();
		}

		updateHeader(id, processedCount);
	}

	public void processGstSummaryData() {
		long id = saveHeader();
		processSumExtractionTypeData(id);

		if (processedCount <= 0) {
			throwPostException();
		}

		updateHeader(id, processedCount);
	}

	private void throwPostException() {
		StringBuilder msg = new StringBuilder("No records are available for the PostDates");
		msg.append(", From Date : ").append(fromDate);
		msg.append(", To Date : ").append(toDate);
		msg.append(", Application Date : ").append(appDate);

		throw new AppException(msg.toString());
	}

	public long setGstSummaryRecords() {
		totalRecords = setGstTranasactioRecords();

		return totalRecords;
	}

	public long getGstTrnansactionRecordCount() {
		java.sql.Date frmDt = JdbcUtil.getDate(fromDate);
		java.sql.Date toDt = JdbcUtil.getDate(toDate);

		String sql = "SELECT count(*)  FROM LEA_GST_TMP_DTL WHERE TRANSACTION_DATE >= ? AND TRANSACTION_DATE <= ?";

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Long.class, frmDt, toDt);
	}

	public List<TaxDownload> getGstTrnansactionDeatils() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" TRANSACTION_DATE, HOST_SYSTEM_TRANSACTION_ID, TRANSACTION_TYPE, BUSINESS_AREA");
		sql.append(", SOURCE_SYSTEM, COMPANY_CODE, REGISTERED_CUSTOMER, CUSTOMER_ID, CUSTOMER_NAME");
		sql.append(", CUSTOMER_GSTIN, CUSTOMER_ADDRESS, CUSTOMER_STATE_CODE, ADDRESS_CHANGE_DATE, PAN_NO");
		sql.append(", LEDGER_CODE, HSN_SAC_CODE, NATURE_OF_SERVICE, LOAN_ACCOUNT_NO, PRODUCT_CODE, CHARGE_CODE");
		sql.append(", LOAN_BRANCH, LOAN_BRANCH_STATE, LOAN_SERVICING_BRANCH, BFL_GSTIN_NO, TXN_BRANCH_ADDRESS");
		sql.append(", TXN_BRANCH_STATE_CODE, TRANSACTION_AMOUNT, REVERSE_CHARGE_APPLICABLE, INVOICE_TYPE");
		sql.append(", ORIGINAL_INVOICE_NO, LOAN_BRANCH_ADDRESS, TO_STATE, FROM_STATE, BUSINESSDATETIME");
		sql.append(", PROCESSDATETIME, PROCESSED_FLAG, AGREEMENTID, CONSIDER_FOR_GST");
		sql.append(", EXEMPTED_STATE, EXEMPTED_CUSTOMER");
		sql.append(" From LEA_GST_TMP_DTL");
		sql.append(" Where TRANSACTION_DATE >= ? and TRANSACTION_DATE <= ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setDate(index++, JdbcUtil.getDate(fromDate));
			ps.setDate(index, JdbcUtil.getDate(toDate));

		}, (rs, rowNum) -> {
			TaxDownload td = new TaxDownload();

			td.setTransactionDate(rs.getDate("TRANSACTION_DATE"));
			td.setHostSystemTransactionId(rs.getString("HOST_SYSTEM_TRANSACTION_ID"));
			td.setTransactionType(rs.getString("TRANSACTION_TYPE"));
			td.setBusinessArea(rs.getString("BUSINESS_AREA"));
			td.setSourceSystem(rs.getString("SOURCE_SYSTEM"));
			td.setCompanyCode(rs.getString("COMPANY_CODE"));
			td.setRegisteredCustomer(rs.getString("REGISTERED_CUSTOMER"));
			td.setCustomerId(rs.getLong("CUSTOMER_ID"));
			td.setCustomerName(rs.getString("CUSTOMER_NAME"));
			td.setCustomerGstin(rs.getString("CUSTOMER_GSTIN"));
			td.setCustomerAddress(rs.getString("CUSTOMER_ADDRESS"));
			td.setCustomerStateCode(rs.getString("CUSTOMER_STATE_CODE"));
			td.setAddressChangeDate(rs.getDate("ADDRESS_CHANGE_DATE"));
			td.setPanNo(rs.getString("PAN_NO"));
			td.setLedgerCode(rs.getString("LEDGER_CODE"));
			td.setHsnSacCode(rs.getString("HSN_SAC_CODE"));
			td.setNatureOfService(rs.getString("NATURE_OF_SERVICE"));
			td.setLoanAccountNo(rs.getString("LOAN_ACCOUNT_NO"));
			td.setProductCode(rs.getString("PRODUCT_CODE"));
			td.setChargeCode(rs.getLong("CHARGE_CODE"));
			td.setLoanBranch(rs.getLong("LOAN_BRANCH"));
			td.setLoanBranchState(rs.getString("LOAN_BRANCH_STATE"));
			td.setLoanServicingBranch(rs.getString("LOAN_SERVICING_BRANCH"));
			td.setBflGstinNo(rs.getString("BFL_GSTIN_NO"));
			td.setTxnBranchAddress(rs.getString("TXN_BRANCH_ADDRESS"));
			td.setTxnBranchStateCode(rs.getString("TXN_BRANCH_STATE_CODE"));
			td.setTransactionAmount(rs.getBigDecimal("TRANSACTION_AMOUNT"));
			td.setReverseChargeApplicable(rs.getString("REVERSE_CHARGE_APPLICABLE"));
			td.setInvoiceType(rs.getString("INVOICE_TYPE"));
			td.setOriginalInvoiceNo(rs.getString("ORIGINAL_INVOICE_NO"));
			td.setLoanBranchAddress(rs.getString("LOAN_BRANCH_ADDRESS"));
			td.setToState(rs.getString("TO_STATE"));
			td.setFromState(rs.getString("FROM_STATE"));
			td.setBusinessDatetime(rs.getDate("BUSINESSDATETIME"));
			td.setProcessDatetime(rs.getDate("PROCESSDATETIME"));
			td.setProcessedFlag(rs.getString("PROCESSED_FLAG"));
			td.setAgreementId(rs.getLong("AGREEMENTID"));
			td.setConsiderForGst(rs.getString("CONSIDER_FOR_GST"));
			td.setExemptedState(rs.getString("EXEMPTED_STATE"));
			td.setExemptedCustomer(rs.getString("EXEMPTED_CUSTOMER"));

			return td;
		});

	}

	public long getGSTSummaryRecordCount() {
		String sql = "SELECT count(*) FROM GSTSUMMARYDETAILS WHERE TRANSACTION_DATE >= ? and TRANSACTION_DATE <= ?";

		logger.debug(Literal.SQL + sql);

		java.sql.Date frmDt = JdbcUtil.getDate(fromDate);
		java.sql.Date toDt = JdbcUtil.getDate(toDate);

		return jdbcOperations.queryForObject(sql, Long.class, frmDt, toDt);
	}

	public List<TaxDownload> getGstSummaryDeatils() {

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Transaction_Date, EntityName, EntityGSTIN, LedgerCode, FinnOneBranchId");
		sql.append(", RegisteredUnregistered, InterIntraState, Amount");
		sql.append(" From GSTSummaryDetails");
		sql.append(" Where Transaction_Date >= ? and Transaction_Date <= ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setDate(index++, JdbcUtil.getDate(fromDate));
			ps.setDate(index, JdbcUtil.getDate(toDate));

		}, (rs, rowNum) -> {
			TaxDownload td = new TaxDownload();

			td.setTransactionDate(rs.getDate("Transaction_Date"));
			td.setEntityName(rs.getString("EntityName"));
			td.setEntityGSTIN(rs.getString("EntityGSTIN"));
			td.setLedgerCode(rs.getString("LedgerCode"));
			td.setFinBranchId(rs.getString("FinnOneBranchId"));
			td.setRegisteredCustomer(rs.getString("RegisteredUnregistered"));
			td.setInterIntraState(rs.getString("InterIntraState"));
			td.setAmount(rs.getBigDecimal("Amount"));

			return td;
		});
	}

	@Override
	protected MapSqlParameterSource mapData(ResultSet rs) {
		return null;
	}
}
