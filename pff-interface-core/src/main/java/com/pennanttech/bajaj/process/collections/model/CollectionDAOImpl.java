package com.pennanttech.bajaj.process.collections.model;

import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennanttech.dataengine.util.DateUtil;

public class CollectionDAOImpl implements CollectionDAO {

	private static final Logger logger = Logger.getLogger(CollectionDAOImpl.class);
	
	protected DataSource dataSource;
	protected NamedParameterJdbcTemplate namedJdbcTemplate;

	@Override
	public Date getAppDate() {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		
		StringBuilder selectSql = new StringBuilder("Select SYSPARMVALUE from SMTPARAMETERS where SYSPARMCODE = 'APP_DATE'");
		
		logger.debug("selectSql: " + selectSql.toString());
	
		logger.debug("Leaving");

		return this.namedJdbcTemplate.queryForObject(selectSql.toString(), source, Date.class);
	}
	
	/**
	 * Method for Over Due Finances
	 */
	@Override
	public List<CollectionFinances> getCollectionODFinList(int curOdDays, List<String> divisions, Date appDate) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CurODDays", curOdDays);
		source.addValue("FinDivisions", divisions);
		source.addValue("AppDate", appDate);
		
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append(" SELECT FPD.FinReference, FPD.CustID, FPD.ODPrincipal, FPD.ODProfit, FPD.CurOdDays, FPD.FinType,");
		selectSql.append(" SDD.EntityCode, C.CustCoreBank, RB.BranchSwiftBrnCde, RB.BankRefNo, RFT.ProductCategory, RFT.FinDivision, ");
		selectSql.append(" FPD.TOTALPRIBAL, FM.FinAssetValue, FM.FinAmount, FM.DownPayment");
		selectSql.append(" FROM FinPftDetails FPD");
		selectSql.append(" Inner Join FINANCEMAIN FM ON FM.FinReference = FPD.FinReference");
		selectSql.append(" Inner Join Customers C On C.CustId = FPD.CustId");
		selectSql.append(" Inner Join RmtFinancetypes RFT On FPD.FinType = RFT.FinType and RFT.ProductCategory <> 'ODFCLITY'");
		selectSql.append(" Inner Join SMTDivisionDetail SDD On SDD.DivisionCode = RFT.FinDivision");
		selectSql.append(" Inner Join RMTBRANCHES RB On RB.BranchCode = FPD.FinBranch");
		selectSql.append(" WHERE (FPD.CurODDays > :CurODDays OR (FPD.FinIsActive = 0 AND FPD.LatestRpyDate = :AppDate))");
		selectSql.append(" And RFT.FinDivision IN (:FinDivisions)");
		
		logger.debug("selectSql: " + selectSql.toString());
		
		RowMapper<CollectionFinances> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CollectionFinances.class);
		List<CollectionFinances> finReferencesList = this.namedJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		
		logger.debug("Leaving");
		
		return finReferencesList;
	}
	
	/**
	 * Method for Finances having OD cleared completely and these are part of Collections same day </br>
	 */
	@Override
	public List<CollectionFinances> getCollectionFinList(List<String> divisions, Date appDate) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinDivisions", divisions);
		source.addValue("AppDate", appDate);

		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" SELECT FPD.FinReference, FPD.CustID, FPD.ODPrincipal, FPD.ODProfit, FPD.CurOdDays, FPD.FinType,");
		selectSql.append(" SDD.EntityCode, C.CustCoreBank, RB.BranchSwiftBrnCde, RB.BankRefNo, RFT.ProductCategory, RFT.FinDivision,");
		selectSql.append(" FPD.TOTALPRIBAL, FM.FinAssetValue, FM.FinAmount, FM.DownPayment");
		selectSql.append(" FROM FinPftDetails FPD");
		selectSql.append(" Inner Join FINANCEMAIN FM ON FM.FinReference = FPD.FinReference");		
		selectSql.append(" INNER JOIN (Select FInReference, MAX(FinLMdfDate) FinLMdfDate from FinODDetails");
		selectSql.append(" Group By FinReference Having SUM(FInCurODAmt) = 0) T ON T.FInReference = FPD.FInReference");
		selectSql.append(" Inner Join Customers C On C.CustId = FPD.CustId");
		selectSql.append(" Inner Join RmtFinancetypes RFT On FPD.FinType = RFT.FinType and RFT.ProductCategory <> 'ODFCLITY'");
		selectSql.append(" Inner Join SMTDivisionDetail SDD On SDD.DivisionCode = RFT.FinDivision");
		selectSql.append(" Inner Join RMTBRANCHES RB On RB.BranchCode = FPD.FinBranch");
		selectSql.append(" WHERE FPD.FinIsActive = 1 AND RFT.FinDivision IN (:FinDivisions) AND T.FinLMdfDate = :AppDate");

		logger.debug("selectSql: " + selectSql.toString());

		RowMapper<CollectionFinances> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CollectionFinances.class);
		List<CollectionFinances> finReferencesList = this.namedJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);

		logger.debug("Leaving");

		return finReferencesList;
	}

	@Override
	public long saveDataExtraction(DataExtractions dataExtractions) {
		logger.debug("Entering");
					
		StringBuilder insertSql = new StringBuilder();
		
		if (dataExtractions.getExtractionId() == Long.MIN_VALUE) {
			dataExtractions.setExtractionId(getOracleSeq("SeqDataExtractions"));	//FIXME works only for oracle database only
			logger.debug("get NextID:" + dataExtractions.getExtractionId());
		}	
		
		insertSql.append("Insert Into DataExtractions");
		insertSql.append(" (ExtractionId, ExtractionDate, InterfaceName, Progress, StartTime, EndTime, LastMntBy)" );
		insertSql.append(" Values(:ExtractionId, :ExtractionDate, :InterfaceName, :Progress, :StartTime, :EndTime, :LastMntBy)" );

		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dataExtractions);
		this.namedJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
		
		return dataExtractions.getExtractionId();
	}
	
	@Override
	public void saveCollectionFinancesBatch(List<CollectionFinances> collectionFinancesList) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" INSERT INTO CollectionFinances");
		insertSql.append(" (ExtractionId, FinReference, CustID, ODPrincipal, ODProfit, CurODDays, FinType, EntityCode, CustCoreBank,");
		insertSql.append(" BranchSwiftBrnCde, BankRefNo, ProductCategory, FinDivision, ForeClosureCharges)");
		insertSql.append(" VALUES (:ExtractionId, :FinReference, :CustID, :ODPrincipal, :ODProfit, :CurODDays, :FinType, :EntityCode,");
		insertSql.append(" :CustCoreBank, :BranchSwiftBrnCde, :BankRefNo, :ProductCategory, :FinDivision, :ForeClosureCharges) ");

		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(collectionFinancesList.toArray());
		this.namedJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
	}
	
	@Override
	public void truncateCollectionTables(boolean isCollectionFinance) {
		logger.debug("Entering");
		
		//PRODUCT_MASTER
		truncateTable(CollectionConstants.TN_PRODUCT_MASTER_TMP);
		
		//CUST_ADDRESS_INFO_V_TMP
		truncateTable(CollectionConstants.TN_CUST_ADDRESS_INFO_V_TMP);
		
		//NON_DELINQ_BOM_POSITION_ACCT
		truncateTable(CollectionConstants.TN_NON_DELINQ_BOM_POSITION_ACCT);

		//REPAYMENT_SCH_V
		truncateTable(CollectionConstants.TN_REPAYMENT_SCH_V_TMP);
		
		//GUARANTOR_DETAILS_V_TMP
		truncateTable(CollectionConstants.TN_GUARANTOR_DETAILS_V_TMP);
		
		//FORECLOSURE_DETAILS_V_TMP
		truncateTable(CollectionConstants.TN_FORECLOSURE_DETAILS_V_TMP);
		
		//CUST_PERS_INFO_V_TMP
		truncateTable(CollectionConstants.TN_CUST_PERS_INFO_V_TMP);

		//DISBURSAL_INFO_TMP
		truncateTable(CollectionConstants.TN_DISBURSAL_INFO_TMP);
		
		//BOUNCE_HISTORY_V
		truncateTable(CollectionConstants.TN_BOUNCE_HISTORY_V_TEMP);

		//CASE_DETAILS_V
		truncateTable(CollectionConstants.TN_CASE_DETAILS_V_TMP);

		//PAYMENT_DETAILS_V
		truncateTable(CollectionConstants.TN_PAYMENT_DETAILS_V);
		
		if (isCollectionFinance) {
			//logTable(CollectionConstants.TN_DATAEXTRACTIONS);
			logTable(CollectionConstants.TN_COLLECTIONFINANCES);
			//COLLECTIONFINANCES
			truncateTable(CollectionConstants.TN_COLLECTIONFINANCES);	
			//truncateTable(CollectionConstants.TN_DATAEXTRACTIONS);	
		}
		
		logger.debug("Leaving");
	}
	
	
	public void logTable(String tableName) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder insertSql = new StringBuilder("INSERT INTO " + tableName + "_LOG");
		insertSql.append(" SELECT * FROM " + tableName);

		logger.debug("insertSql: " + insertSql.toString());

		this.namedJdbcTemplate.update(insertSql.toString(), source);

		logger.debug("Leaving");
	}
	
	private void truncateTable(String tableName) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		
		StringBuilder deleteSql = new StringBuilder("TRUNCATE TABLE ");
		deleteSql.append(tableName);
		
		logger.debug("deleteSql: " + deleteSql.toString());
		
		this.namedJdbcTemplate.update(deleteSql.toString(), source);
		
		logger.debug("Leaving");
	}
	
	@Override
	public void delete(long extractionId) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ExtractionId", extractionId);
		
		StringBuilder deleteSql = new StringBuilder("Delete from CollectionFinances where ExtractionId = :ExtractionId");
		
		logger.debug("deleteSql: " + deleteSql.toString());
	
		this.namedJdbcTemplate.update(deleteSql.toString(), source);
		
		deleteSql = new StringBuilder("Delete from DataExtractions where ExtractionId = :ExtractionId");
		
		logger.debug("deleteSql: " + deleteSql.toString());
	
		this.namedJdbcTemplate.update(deleteSql.toString(), source);
		
		logger.debug("Leaving");
	}
	
	//FIXME only for Oracle only it will works
	private long getOracleSeq(String seqName) throws DataAccessException {
		
		StringBuilder selectSql = new StringBuilder("select ").append(seqName).append(".NEXTVAL from DUAL");

		return this.namedJdbcTemplate.getJdbcOperations().queryForObject(selectSql.toString(), Long.class);
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	@Override
	public void updateDataExtractionStatus(long extractionId, int progress) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Progress", progress);
		source.addValue("ExtractionId", extractionId);
		source.addValue("EndTime", DateUtil.getSysDate());
		
		StringBuilder updateSql =new StringBuilder("Update DataExtractions Set Progress = :Progress, EndTime = :EndTime");
		updateSql.append(" Where ExtractionId = :ExtractionId");
		
		logger.debug("updateSql: " + updateSql.toString());
		
		this.namedJdbcTemplate.update(updateSql.toString(), source);
		
		logger.debug("Leaving");
	}

	@Override
	public void updateCollection(String tableName, String status) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Status", status);
		
		StringBuilder updateSql = new StringBuilder("Update COLLECTIONS_TABLES Set Status = :Status");
		
		if (StringUtils.isBlank(tableName)) {
			updateSql.append(", ERROR_DESC = '', EFFECTED_COUNT = 0");
		} else {
			updateSql.append(" Where TABLE_NAME = :TABLE_NAME");
			source.addValue("TABLE_NAME", tableName);
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		this.namedJdbcTemplate.update(updateSql.toString(), source);
		
		logger.debug("Leaving");
	}
	
	// ****************************************************************** //
	// ****************** Receipts Extraction Process ******************* //
	// ****************************************************************** //
	/**
	 * Receipts Extraction Process
	 */

	@Override
	public void startReceiptProcess(long extractionId) {
		logger.debug("Entering");

		Date startTime = DateUtil.getSysDate();

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ExtractionId", extractionId);
		source.addValue("StartTime", startTime);
		source.addValue("STATUS", "INPROGRESS");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into EXTRACTION_HEADER");
		insertSql.append(" (ExtractionId, STARTTIME, STATUS)");
		insertSql.append(" Values(:ExtractionId, :StartTime, :STATUS)");
		logger.debug("insertSql: " + insertSql.toString());

		this.namedJdbcTemplate.update(insertSql.toString(), source);
		logger.debug("Leaving");
	}

	@Override
	public void saveAllocationHeader(long extractionID) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("extractionID", extractionID);

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert into COL_ALLOCATION_HEADER (RECEIPTID, RECEIPTSEQID, EXTRACTIONID) ");
		insertSql.append(" Select T2.ReceiptID, T2.RECEIPTSEQID, :extractionID From FinReceiptHeader T1 ");
		insertSql.append(" Inner Join FinReceiptDetail T2 On T1.ReceiptID = T2.ReceiptID ");
		insertSql.append(" Where T2.ReceiptID Not IN (Select Distinct ReceiptID from COL_ALLOCATION_HEADER)");

		logger.debug("selectSql: " + insertSql.toString());

		logger.debug("Leaving");
		this.namedJdbcTemplate.update(insertSql.toString(), source);;
	}

	@Override
	public List<Long> getReceiptIdList(long extractionID) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ExtractionID", extractionID);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select Distinct RECEIPTSEQID From COL_ALLOCATION_HEADER" );
		selectSql.append(" Where ExtractionID = :ExtractionID ");

		logger.debug("selectSql: " + selectSql.toString());

		logger.debug("Leaving");
		return this.namedJdbcTemplate.queryForList(selectSql.toString(), source, Long.class);
	}

	@Override
	public List<CollectionReceiptExtraction> getReceiptDetailList(long receiptSeqID) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptSeqID", receiptSeqID);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select T3.FinReference, T1.ReceiptID, T1.ReceiptSeqID, T3.SchDate, T3.ProfitSchdPayNow profit, T3.PrincipalSchdPayNow principal, "); 
		selectSql.append(" T3.SchdFeePayNow schdFee, T3.PenaltyPayNow penalty, T3.TDSSchdPayNow tDSSchd, T4.InstNumber");
		selectSql.append(" from FinReceiptDetail T1 ");
		selectSql.append(" inner join FinRepayHeader t2 on T1.ReceiptSeqId = T2.ReceiptSeqId "); 
		selectSql.append(" inner join FinRepayScheduleDetail T3 on T2.Repayid = T3.Repayid ");
		selectSql.append(" inner join FinScheduleDetails T4 on T3.SchDate = T4.SchDate AND T3.FinReference = T4.FinReference");
		selectSql.append(" Where T1.ReceiptSeqID = :ReceiptSeqID and (T3.ProfitSchdPayNow > 0 Or T3.PrincipalSchdPayNow > 0 Or  "); 
		selectSql.append(" T3.SchdFeePayNow > 0 Or T3.PenaltyPayNow  > 0 Or T3.TDSSchdPayNow > 0 )");
		logger.debug("selectSql: " + selectSql.toString());

		RowMapper<CollectionReceiptExtraction> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CollectionReceiptExtraction.class);
		List<CollectionReceiptExtraction> finReferencesList = this.namedJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);

		logger.debug("Leaving");
		return finReferencesList;

	}

	/**
	 * Receipts Extraction Process
	 */

	@Override
	public void updateAllocationHeader(CollectionReceiptExtraction receiptExtraction) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("UPDATE COL_ALLOCATION_HEADER SET");
		updateSql.append(" PRINCIPAL = :principal, PROFIT = :profit, PENALTY = :penalty,");
		updateSql.append(" EXCESS = :excessAmt, BOUNCE = :bounceAmt, MANUALADVISE = :adviseAmt,");
		updateSql.append(" DueDate = :DueDate, InstNumber = :InstNumber");
		updateSql.append(" where ReceiptSeqID = :ReceiptSeqID");

		logger.debug("selectSql: " + updateSql.toString());

		logger.debug("Leaving");
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(receiptExtraction);
		this.namedJdbcTemplate.update(updateSql.toString(), paramSource);
	}

	@Override
	public List<CollectionReceiptExtraction> getFinExcessMovements(long receiptSeqID) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptSeqID", receiptSeqID);

		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" select T1.EXCESSID PrimaryId, T1.RECEIPTID, T2.ReceiptSEQID, T1.AMOUNT, T2.RECEIVEDDATE SchDate, T3.AmountType from FinExcessMovement T1");
		selectSql.append(" Inner Join FinReceiptDetail T2 on T1.ReceiptID = T2.ReceiptID" );
		selectSql.append(" Inner Join FinExcessAmount T3 on T1.EXCESSID = T3.EXCESSID" );
		selectSql.append(" Where T1.ReceiptID = :ReceiptSeqID ");

		logger.debug("selectSql: " + selectSql.toString());

		RowMapper<CollectionReceiptExtraction> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CollectionReceiptExtraction.class);
		List<CollectionReceiptExtraction> finReferencesList = this.namedJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);

		logger.debug("Leaving");
		return finReferencesList;

	}

	@Override
	public List<CollectionReceiptExtraction> getManualAdvises(long receiptSeqID) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("receiptSeqID", receiptSeqID);

		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" select T1.ADVISEID PrimaryId, T2.BounceId, T1.RECEIPTID,T1.RECEIPTSEQID,T2.FINREFERENCE,T3.FEETYPEDESC feeDesc,");
		selectSql.append(" T1.PAIDAMOUNT Amount, T2.VALUEDATE SchDate from MANUALADVISEMOVEMENTS T1 ");
		selectSql.append(" INNER JOIN MANUALADVISE T2 ON T2.ADVISEID = T1.ADVISEID ");
		selectSql.append(" LEFT JOIN FEETYPES T3 ON T3.feeTYpeID = T2.FeeTypeID");
		selectSql.append(" Where RECEIPTSEQID = :receiptSeqID");

		logger.debug("selectSql: " + selectSql.toString());

		RowMapper<CollectionReceiptExtraction> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CollectionReceiptExtraction.class);
		List<CollectionReceiptExtraction> finReferencesList = this.namedJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);

		logger.debug("Leaving");
		return finReferencesList;
	}

	@Override
	public void saveAllocationDetailsBatch(List<CollectionReceiptExtraction> extractionReceiptList) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" INSERT INTO COL_ALLOCATION_DETAILS");
		insertSql.append(" (EXTRACTIONID, FINREFERENCE, ALLOCATIONTYPE, FEEDESC, PRIMARYID, SCHDATE, AMOUNT, PROFIT, PRINCIPAL, RECEIPTID, RECEIPTSEQID)");
		insertSql.append(" VALUES (:extractionId, :finReference, :allocationType, :feeDesc, :primaryId, :schDate, :amount, :profit, :principal, :receiptID, :receiptSeqID) ");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(extractionReceiptList.toArray());
		this.namedJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
	}


	@Override
	public void updateReceiptProcess(long extractionId) {
		logger.debug("Entering");

		Date endTime = DateUtil.getSysDate();
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ExtractionId", extractionId);
		source.addValue("ENDTIME", endTime);
		source.addValue("STATUS", "COMPLETED"); 


		StringBuilder updateSql = new StringBuilder("Update EXTRACTION_HEADER Set ENDTIME = :ENDTIME, STATUS = :STATUS");
		updateSql.append(" Where ExtractionId = :ExtractionId");
		logger.debug("updateSql: " + updateSql.toString());

		this.namedJdbcTemplate.update(updateSql.toString(), source);
		logger.debug("Leaving");
	}
	
	@Override
	public String getSystemParameterValue(String sysParmCode) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("SysParmCode", sysParmCode);

		StringBuilder selectSql = new StringBuilder("Select SysParmValue from SMTPARAMETERS");
		selectSql.append(" Where SysParmCode = :SysParmCode");

		logger.debug("selectSql: " + selectSql.toString());

		String sysParamValue = this.namedJdbcTemplate.queryForObject(selectSql.toString(), source, String.class);

		logger.debug("Leaving");
		return sysParamValue;
	}
	
	/**
	 * 
	 */
	@Override
	public List<CollectionFinTypeFees> getFinTypeFeesList() {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ModuleId", CollectionConstants.MODULEID_FINTYPE);
		source.addValue("FinEvent", CollectionConstants.ACCEVENT_EARLYSTL);

		StringBuilder selectSql = new StringBuilder("SELECT FinType, CalculationType, RuleCode, Amount, Percentage, CalculateOn");
		selectSql.append(" FROM FinTypeFees");
		selectSql.append(" Where ModuleId = :ModuleId AND FinEvent = :FinEvent");

		logger.debug("selectSql: " + selectSql.toString());

		RowMapper<CollectionFinTypeFees> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CollectionFinTypeFees.class);
		List<CollectionFinTypeFees> finTypeFeesList = this.namedJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);

		logger.debug("Leaving");
		return finTypeFeesList;
	}
	
}
