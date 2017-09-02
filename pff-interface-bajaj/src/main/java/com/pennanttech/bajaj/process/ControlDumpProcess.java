package com.pennanttech.bajaj.process;

import com.pennanttech.app.util.DateUtility;
import com.pennanttech.bajaj.model.ControlDump;
import com.pennanttech.dataengine.DatabaseDataEngine;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.App;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionStatus;

public class ControlDumpProcess extends DatabaseDataEngine {
	private static final Logger logger = Logger.getLogger(ControlDumpProcess.class);
	public static DataEngineStatus	EXTRACT_STATUS		= new DataEngineStatus("CONTROL_DUMP_REQUEST");

	Date appDate = null;
	private MapSqlParameterSource filterMap;
	private int batchSize = 5000;
	int chunckSize = 1000;

	public ControlDumpProcess(DataSource dataSource, long userId, Date valueDate, Date appDate) {
		super(dataSource, App.DATABASE.name(), userId, true, valueDate, EXTRACT_STATUS);
		this.appDate = appDate;
	}

	@Override
	protected void processData() {
		logger.debug(Literal.ENTERING);
		try {

			filterMap = new MapSqlParameterSource();
			filterMap.addValue("CLOSINGSTATUS", "C");

			// Handling retry on same day.
			deleteData();
			// Moving last run data to log table.
			copyDataFromMainToLogTable(appDate);

			loadcount();

			execute();

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			deleteOldData();
		}

		logger.debug(Literal.LEAVING);
	}


	private void loadcount() {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT count(*) FROM FINANCEMAIN FM");
		sql.append(" INNER JOIN CUSTOMERS LC ON LC.CUSTID = FM.CUSTID");
		sql.append(" INNER JOIN RMTBRANCHES LB ON LB.BRANCHCODE = FM.FINBRANCH");
		sql.append(" INNER JOIN RMTCURRENCIES CCY ON CCY.CCYCODE = FM.FINCCY");
		sql.append(" LEFT JOIN MANDATES M ON M.ORGREFERENCE = FM.FINREFERENCE");
		sql.append(" LEFT JOIN PROMOTIONS PM ON PM.PromotionCode  = FM.PromotionCode");
		sql.append(" WHERE COALESCE(FM.CLOSINGSTATUS, 'A') != :CLOSINGSTATUS");

		try {
			totalRecords = parameterJdbcTemplate.queryForObject(sql.toString(), filterMap, Integer.class);
			executionStatus.setTotalRecords(totalRecords);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}


	private void execute() throws Exception {
		List<ControlDump> list = getList();
		
		TransactionStatus txnStatus = null;
		transDef.setTimeout(180);
		int count = 0;
		
		for (ControlDump item : list) {
			try {
				if (count == 0) {
					txnStatus = transManager.getTransaction(transDef);
				}
				
				validateData(item.getAgreementNo(), item);
				saveControlDump(item);
				EXTRACT_STATUS.setSuccessRecords(successCount++);
				
				if (count++ >= chunckSize) {
					transManager.commit(txnStatus);
					count = 0;
				}
			} catch (Exception e) {
				if(!txnStatus.isCompleted()) {
					transManager.commit(txnStatus);
					count = 0;
				}
				
				saveBatchLog(item.getAgreementNo(), "F", e.getMessage());
				EXTRACT_STATUS.setFailedRecords(failedCount++);
			} finally {
				EXTRACT_STATUS.setProcessedRecords(processedCount++);
			}
		}	
		
		if (count > 0 && !txnStatus.isCompleted()) {
			transManager.commit(txnStatus);
		}
	}
	

	private List<ControlDump> getList() throws Exception {
		// Saving the data into main table.

		Map<String, ControlDump> profitDetails = getProfitDetails();
		Map<String, ControlDump> disbursements = getDisbursementDetails();
		Map<String, ControlDump> balanceAmounts = getBalanceAmounts();
		Map<String, ControlDump> repayments = getRepaymentAmounts();
		Map<String, ControlDump> fees = getFeeAmounts();
		Map<String, ControlDump> overDues = getOverDueDetails();
		Map<String, ControlDump> receiptAmounts = getReceiptAmounts();
		Map<String, ControlDump> writeOffAmounts = getWriteOffAmounts();
		Map<String, ControlDump> writeOffPayments = getWriteOffPayments();
		Map<String, ControlDump> excesAmounts = getExcesAmounts();

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT FINREFERENCE,");
		sql.append(" FM.FINTYPE,");
		sql.append(" FINBRANCH,");
		sql.append(" FINCONTRACTDATE,");
		sql.append(" FINAMOUNT,");
		sql.append(" FINASSETVALUE,");
		sql.append(" DUEBUCKET,");
		sql.append(" COALESCE(DPDC.DUEDAYS, 0) DUEDAYS,");
		sql.append(" FINCURRASSETVALUE,");
		sql.append(" EFFECTIVERATEOFRETURN,");
		sql.append(" REPAYPROFITRATE,");
		sql.append(" FM.FINISACTIVE,");
		sql.append(" LASTREPAYDATE,");
		sql.append(" MATURITYDATE,");
		sql.append(" CLOSINGSTATUS,");
		sql.append(" FINSTARTDATE,");
		sql.append(" LC.CUSTCIF,");
		sql.append(" CUSTSHRTNAME,");
		sql.append(" BANKREFNO,");
		sql.append(" BRANCHDESC,");
		sql.append(" CCYEDITFIELD,");
		sql.append(" CCYMINORCCYUNITS,");
		sql.append(" PM.PROMOTIONID,");
		sql.append(" PM.PROMOTIONDESC,");
		sql.append(" M.MANDATETYPE");
		sql.append(" FROM FINANCEMAIN FM");
		sql.append(" INNER JOIN RMTFINANCETYPES FT ON FT.FINTYPE = FM.FINTYPE");
		sql.append(" INNER JOIN CUSTOMERS LC ON LC.CUSTID = FM.CUSTID");
		sql.append(" INNER JOIN RMTBRANCHES LB ON LB.BRANCHCODE = FM.FINBRANCH");
		sql.append(" INNER JOIN RMTCURRENCIES CCY ON CCY.CCYCODE = FM.FINCCY");
		sql.append(" LEFT JOIN MANDATES M ON M.ORGREFERENCE = FM.FINREFERENCE");
		sql.append(" LEFT JOIN PROMOTIONS PM ON PM.PromotionCode  = FM.PromotionCode");
		sql.append(" LEFT JOIN DPDBUCKETS DPD ON DPD.BUCKETCODE = FM.FINSTATUS");
		sql.append(" LEFT JOIN DPDBUCKETSCONFIG DPDC ON DPDC.BUCKETID = DPD.BUCKETID AND FT.FINCATEGORY = DPDC.PRODUCTCODE");
		sql.append(" WHERE COALESCE(FM.CLOSINGSTATUS, 'A') != :CLOSINGSTATUS");

		List<ControlDump> list = new ArrayList<>();

		parameterJdbcTemplate.query(sql.toString(), filterMap, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				ControlDump obj = null;
				String finReference = null;
				ControlDump cd = null;
				String agreementId = null;
				try {
					finReference = rs.getString("FINREFERENCE");
					agreementId = StringUtils.substring(finReference, finReference.length() - 8, finReference.length());
					
					cd = new ControlDump();
					cd.setCreatedOn(appDate);
					cd.setCcyMinorUnits(getAmount(rs, "CCYMINORCCYUNITS"));
					cd.setAmountScale(rs.getInt("CCYEDITFIELD"));
					cd.setAgreementNo(finReference);

					if (StringUtils.isNumeric(agreementId)) {
						cd.setAgreementId(Long.parseLong(agreementId));
					} else {
						cd.setAgreementId(Long.parseLong("0"));
					}

					cd.setAgreementDate(rs.getDate("FINCONTRACTDATE"));
					cd.setProductFlag(rs.getString("FINTYPE"));
					cd.setAmtFin(getAmount(rs, "FINAMOUNT"));
					cd.setAssetCost(getAmount(rs, "FINASSETVALUE"));
					cd.setClosureDate(rs.getDate("MATURITYDATE"));
					cd.setCurrentBucket(rs.getInt("DUEBUCKET"));
					cd.setDerivedBucket(rs.getInt("DUEDAYS"));
					cd.setCustomerId(rs.getString("CUSTCIF"));  
					cd.setCustomerName(rs.getString("CUSTSHRTNAME"));
					cd.setMaturityDate(rs.getDate("MATURITYDATE"));

					int monts = DateUtility.getMonthsBetween(cd.getMaturityDate(), rs.getDate("FINSTARTDATE"), true);
					cd.setSanctionedTenure(monts);

					cd.setSchemeId(rs.getInt("PROMOTIONID"));
					cd.setSchemeName(rs.getString("PROMOTIONDESC"));

					cd.setFlatRate(getAmount(rs, "REPAYPROFITRATE"));
					if (cd.getFlatRate().compareTo(new BigDecimal(999)) > 0) {
						cd.setFlatRate(new BigDecimal(999));
					}
					
					cd.setEffectiveRate(getAmount(rs, "EFFECTIVERATEOFRETURN"));
					if (cd.getEffectiveRate().compareTo(new BigDecimal(999)) > 0) {
						cd.setEffectiveRate(new BigDecimal(999));
					}

					// Loan Branch details
					String branchId = rs.getString("BANKREFNO");
					if (StringUtils.isNumeric(branchId)) {
						cd.setBranchId(Long.parseLong(branchId));
					} else {
						cd.setBranchId(0);
					}

					cd.setBranchName(rs.getString("BRANCHDESC"));

					if ("W".equals(rs.getString("CLOSINGSTATUS"))) {
						cd.setNpaStageId("WRITEOFF");
					} else {
						cd.setNpaStageId("REGULAR");
					}

					if (rs.getBoolean("FINISACTIVE")) {
						cd.setLoanStatus("A");
					} else {
						cd.setLoanStatus("C");
					}
					
					cd.setDisbursedAmount(getAmount(rs, "FINCURRASSETVALUE"));
					if (cd.getAssetCost().compareTo(cd.getDisbursedAmount()) == 0) {
						cd.setDisbStatus("FD");
					} else {
						cd.setDisbStatus("PD");
					}

					// Profit details
					obj = profitDetails.get(finReference);
					if (obj != null) {
						cd.setAccruedAmount(obj.getAccruedAmount());
						cd.setDpd(obj.getDpd());
						cd.setEmiDue(obj.getEmiDue());
						cd.setEmiOs(obj.getEmiOs());
						cd.setEmiReceived(obj.getEmiReceived());
						cd.setFirstDueDate(obj.getFirstDueDate());
						cd.setInterestBalance(obj.getInterestBalance());
						cd.setInterestDue(obj.getInterestDue());
						cd.setInterestOs(obj.getInterestOs());
						cd.setInterestReceived(obj.getInterestReceived());
						cd.setNoOfEmiOs(obj.getNoOfEmiOs());
						cd.setPrincipalDue(obj.getPrincipalDue());
						cd.setPrincipalOs(obj.getPrincipalOs());
						cd.setPrincipalBalance(cd.getPrincipalBalance());
						cd.setPrincipalReceived(obj.getPrincipalReceived());
						cd.setTotalInterest(obj.getTotalInterest());
						cd.setSohBalance(obj.getSohBalance());
						cd.setNoOfUnbilledEmi(obj.getNoOfUnbilledEmi());
						cd.setBalanceUmfc(obj.getBalanceUmfc());
						obj = null;
					}

					obj = balanceAmounts.get(finReference);
					if (obj != null) {
						cd.setAdvanceEmi(obj.getAdvanceEmi());
						cd.setEmiInAdvanceUnbilled(obj.getEmiInAdvanceUnbilled());
						cd.setNetExcessAdjusted(obj.getNetExcessAdjusted());
						cd.setNetExcessReceived(obj.getNetExcessReceived());
						obj = null;
					}

					// Repayments
					obj = repayments.get(finReference);
					if (obj != null) {
						cd.setBulkRefund(obj.getBulkRefund());
						obj = null;
					}

					// Fees
					obj = fees.get(finReference);
					if (obj != null) {
						cd.setForeClosureChargesDue(obj.getForeClosureChargesDue());
						obj = null;
					}

					// Disbursement details
					obj = disbursements.get(finReference);
					if (obj != null) {
						cd.setDisbursalDate(obj.getDisbursalDate());
						obj = null;
					}

					// Receipt amounts
					obj = receiptAmounts.get(finReference);
					if (obj != null) {
						cd.setEmiPrincipalWaived(obj.getEmiPrincipalWaived());
						cd.setPrincipalWaived(obj.getPrincipalWaived());
						cd.setEmiInterestWaived(obj.getEmiInterestWaived());
						obj = null;
					}

					// Disbursement details
					obj = overDues.get(finReference);
					if (obj != null) {
						cd.setFirstRepaydueDate(obj.getFirstRepaydueDate());
						cd.setLppChargesReceivable(obj.getLppChargesReceivable());
						cd.setLppChargesReceived(obj.getLppChargesReceived());
						obj = null;
					}

					// WriteOff details
					obj = writeOffAmounts.get(finReference);
					if (obj != null) {
						cd.setWriteoffDue(obj.getWriteoffDue());
						obj = null;
					}

					// WriteOff details
					obj = writeOffPayments.get(finReference);
					if (obj != null) {
						cd.setWriteoffDue(obj.getWriteoffReceived());
						obj = null;
					}

					// Excess amounts
					obj = excesAmounts.get(finReference);
					if (obj != null) {
						cd.setNoOfAdvanceEmis(obj.getNoOfAdvanceEmis());
						obj = null;
					}

					// Instrument Type
					cd.setInstrument(StringUtils.trimToEmpty(rs.getString("MANDATETYPE")));
					if ("ECS".equals(cd.getInstrument())) {
						cd.setInstrument("E");
					} else if ("DDM".equals(cd.getInstrument())) {
						cd.setInstrument("A");
					} else if ("NACH".equals(cd.getInstrument())) {
						cd.setInstrument("Z");
					} else {
						cd.setInstrument("C");
					}
					
					list.add(cd);
	
				} catch (Exception e) {
					saveBatchLog(cd.getAgreementNo(), "F", e.getMessage());
					EXTRACT_STATUS.setFailedRecords(failedCount++);
					EXTRACT_STATUS.setProcessedRecords(processedCount++);
				} finally {
					profitDetails.remove(finReference);
					disbursements.remove(finReference);
					balanceAmounts.remove(finReference);
					repayments.remove(finReference);
					fees.remove(finReference);
					overDues.remove(finReference);
					receiptAmounts.remove(finReference);
					writeOffAmounts.remove(finReference);
					writeOffPayments.remove(finReference);
					excesAmounts.remove(finReference);
					obj = null;
					cd = null;
				}

			}
			
		});

		return list;
	}
	
	private void validateData(String finReference, ControlDump cd) throws Exception{
		if (cd.getFirstDueDate() == null) {
			throw new Exception("First Due date is null");
		}
		
		if (StringUtils.isNumeric(cd.getCustomerId())) {
			throw new Exception("Customer CIF not numeric.");
		}
	}
	

	private BigDecimal getAmount(ResultSet rs, String columnName) throws SQLException {
		BigDecimal amount;

		amount = rs.getBigDecimal(columnName);

		if (amount == null) {
			amount = BigDecimal.ZERO;
		}
		return amount;
	}

	@Override
	protected MapSqlParameterSource mapData(ResultSet rs) throws Exception {
		return null;

	}

	private void copyDataFromMainToLogTable(final Date currentDate) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO CF_CONTROL_DUMP_LOG SELECT");
		sql.append(" AGREEMENTNO, AGREEMENTID, PRODUCTFLAG, SCHEMEID, BRANCHID,");
		sql.append(" NPA_STAGEID, LOAN_STATUS, DISB_STATUS, FIRST_DUE_DATE,");
		sql.append(" MATURITY_DATE, AMTFIN, DISBURSED_AMOUNT, EMI_DUE,");
		sql.append(" PRINCIPAL_DUE, INTEREST_DUE, EMI_RECEIVED, PRINCIPAL_RECEIVED,");
		sql.append(" INTEREST_RECEIVED, EMI_OS, PRINCIPAL_OS, INTEREST_OS,");
		sql.append(" BULK_REFUND, PRINCIPAL_WAIVED, EMI_PRINCIPAL_WAIVED,");
		sql.append(" EMI_INTEREST_WAIVED, PRINCIPAL_AT_TERM, ADVANCE_EMI, ADVANCE_EMI_BILLED,");
		sql.append(" MIGRATED_ADVANCE_EMI, MIGRATED_ADVANCE_EMI_BILLED, MIGRATED_ADVANCE_EMI_UNBILLED,");
		sql.append(" CLOSED_CAN_ADV_EMI, PRINCIPAL_BALANCE, INTEREST_BALANCE, SOH_BALANCE, NO_OF_UNBILLED_EMI,");
		sql.append(" TOTAL_INTEREST, ACCRUED_AMOUNT, BALANCE_UMFC, EMI_IN_ADVANCE_RECEIVED_MAKER,");
		sql.append(" EMI_IN_ADVANCE_BILLED, EMI_IN_ADVANCE_UNBILLED, MIG_ADV_EMI_BILLED_PRINCOMP,");
		sql.append(" MIG_ADV_EMI_BILLED_INTCOMP, MIG_ADV_EMI_UNBILLED_PRINCOMP, MIG_ADV_EMI_UNBILLED_INTCOMP,");
		sql.append(" EMI_IN_ADV_BILLED_PRINCOMP, EMI_IN_ADV_BILLED_INTCOMP, EMI_IN_ADV_UNBILLED_PRINCOMP,");
		sql.append(" EMI_IN_ADV_UNBILLED_INTCOMP, CLOS_CAN_ADV_EMI_PRINCOMP, CLOS_CAN_ADV_EMI_INTCOMP,");
		sql.append(" SECURITY_DEPOSIT, SECURITY_DEPOSIT_ADJUSTED, ROUNDING_DIFF_RECEIVABLE, ROUNDING_DIFF_RECEIVED,");
		sql.append(" MIG_DIFFERENCE_RECEIVABLE, MIG_DIFFERENCE_RECEIVED, MIG_DIFFERENCE_PAYABLE, MIG_DIFFERENCE_PAID,");
		sql.append(" WRITEOFF_DUE, WRITEOFF_RECEIVED, SOLD_SEIZE_RECEIVABLE, SOLD_SEIZE_RECEIVED, SOLD_SEIZE_PAYABLE,");
		sql.append(" SOLD_SEIZE_PAID, NET_EXCESS_RECEIVED, NET_EXCESS_ADJUSTED,LPP_CHARGES_RECEIVABLE,");
		sql.append(" LPP_CHARGES_RECEIVED, PDC_SWAP_CHARGES_RECEIVABLE, PDC_SWAP_CHARGES_RECEIVED,");
		sql.append(" REPO_CHARGES_RECEIVABLE, REPO_CHARGES_RECEIVED, FORECLOSURE_CHARGES_DUE,");
		sql.append(" FORECLOSURE_CHARGES_RECEIVED, BOUNCE_CHARGES_DUE, BOUNCE_CHARGES_RECEIVED, INSUR_RENEW_CHARGE,");
		sql.append(" INSUR_RENEW_CHARGE_RECD, INSUR_RECEIVABLE, INSUR_RECEIVED, INSUR_PAYABLE, INSUR_PAID,");
		sql.append(" CUSTOMERID, CUSTOMERNAME, SANCTIONED_TENURE, LOAN_EMI, FLAT_RATE, EFFECTIVE_RATE,");
		sql.append(" AGREEMENTDATE, DISBURSALDATE, CLOSUREDATE, NO_OF_ADVANCE_EMIS, ASSETCOST, NO_OF_EMI_OS, DPD,");
		sql.append(" CURRENT_BUCKET, BRANCH_NAME, SCHEME_NAME, DERIVED_BUCKET,");
		sql.append(" ASSETDESC, MAKE, CHASISNUM, REGDNUM,");
		sql.append(" ENGINENUM, INVOICEAMT, SUPPLIERDESC, INSTRUMENT,");
		sql.append(" REPO_DATE, LOCAL_OUTSTATION_FLAG, FIRST_REPAYDUE_DATE, CREATED_ON, :LOGGED_ON");
		sql.append(" from CF_CONTROL_DUMP");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("LOGGED_ON", currentDate);

		destinationJdbcTemplate.update(sql.toString(), paramSource);

		logger.debug(Literal.LEAVING);
	}

	private Map<String, ControlDump> getRepaymentAmounts() {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT FM.FINREFERENCE, FINEVENT, SUM(REPAYAMOUNT) FROM FINREPAYHEADER RH");
		sql.append(" INNER JOIN FINANCEMAIN FM ON FM.FINREFERENCE = RH.FINREFERENCE");
		sql.append(" WHERE FINEVENT = :PS");
		sql.append(" AND COALESCE(FM.CLOSINGSTATUS, 'A') != :CLOSINGSTATUS");
		sql.append(" GROUP BY FM.FINREFERENCE, FINEVENT");

		try {
			return extractRepaymentAmounts(sql);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return new HashMap<>();
	}

	private Map<String, ControlDump> extractRepaymentAmounts(StringBuilder sql) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource(filterMap.getValues());
		paramMap.addValue("PS", "PartialSettlement");
		return parameterJdbcTemplate.query(sql.toString(), paramMap, new ResultSetExtractor<Map<String, ControlDump>>() {
			@Override
			public Map<String, ControlDump> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<String, ControlDump> map = new HashMap<>();
				ControlDump cd = null;
				String event = null;
				String key = null;
				while (rs.next()) {
					key = rs.getString("FINREFERENCE");
					
					cd = map.get(key);
					if (cd == null) {
						cd = new ControlDump();
					}
					
					event = StringUtils.trimToEmpty(rs.getString("FINEVENT"));

					if ("PartialSettlement".equals(event)) {
						cd.setBulkRefund(getAmount(rs, "REPAYAMOUNT"));
					}

					map.put(rs.getString("FINREFERENCE"), cd);
				}
				return map;
			}
		});
	}

	private Map<String, ControlDump> getOverDueDetails() {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT FM.FINREFERENCE, MIN(FINODSCHDDATE) FINODSCHDDATE,");
		sql.append(" SUM(TOTPENALTYBAL) TOTPENALTYBAL, SUM(TOTPENALTYPAID) TOTPENALTYPAID");
		sql.append(" FROM FINODDETAILS OD");
		sql.append(" INNER JOIN FINANCEMAIN FM ON FM.FINREFERENCE = OD.FINREFERENCE");
		sql.append(" WHERE COALESCE(FM.CLOSINGSTATUS, 'A') != :CLOSINGSTATUS");
		sql.append(" GROUP BY FM.FINREFERENCE");

		try {
			return extractOverDueDetails(sql);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return new HashMap<>();
	}

	private Map<String, ControlDump> extractOverDueDetails(StringBuilder sql) {
		return parameterJdbcTemplate.query(sql.toString(), filterMap, new ResultSetExtractor<Map<String, ControlDump>>() {
			@Override
			public Map<String, ControlDump> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<String, ControlDump> map = new HashMap<>();
				ControlDump cd = null;
				while (rs.next()) {
					cd = new ControlDump();
					cd.setFirstRepaydueDate(rs.getDate("FINODSCHDDATE"));
					cd.setLppChargesReceivable(getAmount(rs, "TOTPENALTYBAL"));
					cd.setLppChargesReceived(getAmount(rs, "TOTPENALTYPAID"));
					map.put(rs.getString("FINREFERENCE"), cd);
				}
				return map;
			}
		});
	}

	private Map<String, ControlDump> getWriteOffAmounts() {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT FM.FINREFERENCE, WRITTENOFFPRI, WRITTENOFFPFT FROM FINWRITEOFFDETAIL WO");
		sql.append(" INNER JOIN FINANCEMAIN FM ON FM.FINREFERENCE = WO.FINREFERENCE");
		sql.append(" WHERE COALESCE(FM.CLOSINGSTATUS, 'A') != :CLOSINGSTATUS");
		try {
			return extractWriteOffAmounts(sql);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return new HashMap<>();
	}

	private Map<String, ControlDump> extractWriteOffAmounts(StringBuilder sql) {
		return parameterJdbcTemplate.query(sql.toString(), filterMap, new ResultSetExtractor<Map<String, ControlDump>>() {
			@Override
			public Map<String, ControlDump> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<String, ControlDump> map = new HashMap<>();
				ControlDump cd = null;
				while (rs.next()) {
					cd = new ControlDump();
					cd.setWriteoffDue(getAmount(rs, "WRITTENOFFPRI"));
					cd.setWriteoffDue(cd.getWriteoffDue().add(getAmount(rs, "WRITTENOFFPFT")));
					map.put(rs.getString("FINREFERENCE"), cd);
				}
				return map;
			}
		});
	}

	private Map<String, ControlDump> getWriteOffPayments() {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT FM.FINREFERENCE, SUM(WRITEOFFPAYAMOUNT) WRITEOFFPAYAMOUNT FROM FINWRITEOFFPAYMENT WP");
		sql.append(" INNER JOIN FINANCEMAIN FM ON FM.FINREFERENCE = WP.FINREFERENCE");
		sql.append(" WHERE COALESCE(FM.CLOSINGSTATUS, 'A') != :CLOSINGSTATUS");
		sql.append(" GROUP BY FM.FINREFERENCE");

		try {
			return extractWriteOffPayments(sql);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return new HashMap<>();
	}

	private Map<String, ControlDump> extractWriteOffPayments(StringBuilder sql) {
		return parameterJdbcTemplate.query(sql.toString(), filterMap, new ResultSetExtractor<Map<String, ControlDump>>() {
			@Override
			public Map<String, ControlDump> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<String, ControlDump> map = new HashMap<>();
				ControlDump cd = null;
				while (rs.next()) {
					cd = new ControlDump();
					cd.setWriteoffReceived(getAmount(rs, "WRITEOFFPAYAMOUNT"));
					map.put(rs.getString("FINREFERENCE"), cd);
				}
				return map;
			}
		});
	}

	private Map<String, ControlDump> getExcesAmounts() {
		StringBuilder sql = new StringBuilder();
		sql.append(" select FM.FINREFERENCE, count(*) AdvanceEmi from FINEXCESSMOVEMENT EM");
		sql.append(" INNER JOIN FINEXCESSAMOUNT EA ON EA.EXCESSID = EA.EXCESSID");
		sql.append(" INNER JOIN FINANCEMAIN FM ON FM.FINREFERENCE = EA.FINREFERENCE");
		sql.append(" WHERE TRANTYPE = :TRANTYPE AND AMOUNTTYPE = :AMOUNTTYPE");
		sql.append(" AND COALESCE(FM.CLOSINGSTATUS, 'A') != :CLOSINGSTATUS");
		sql.append(" GROUP BY FM.FINREFERENCE");

		try {
			return extractExcesAmounts(sql);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return new HashMap<>();
	}

	private Map<String, ControlDump> extractExcesAmounts(StringBuilder sql) {
		MapSqlParameterSource parm = new MapSqlParameterSource(filterMap.getValues());
		parm.addValue("TRANTYPE", "C");
		parm.addValue("AMOUNTTYPE", "A");
		return parameterJdbcTemplate.query(sql.toString(), parm, new ResultSetExtractor<Map<String, ControlDump>>() {
			@Override
			public Map<String, ControlDump> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<String, ControlDump> map = new HashMap<>();
				ControlDump cd = null;
				while (rs.next()) {
					cd = new ControlDump();
					cd.setNoOfAdvanceEmis(rs.getLong("AdvanceEmi"));
					map.put(rs.getString("FINREFERENCE"), cd);
				}
				return map;
			}
		});
	}

	private Map<String, ControlDump> getFeeAmounts() {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT FM.FINREFERENCE, FINEVENT, SUM(PAIDAMOUNT) FROM FINFEEDETAIL FEE");
		sql.append(" INNER JOIN FINANCEMAIN FM ON FM.FINREFERENCE = FEE.FINREFERENCE");
		sql.append(" WHERE FINEVENT=:ES");
		sql.append(" AND COALESCE(FM.CLOSINGSTATUS, 'A') != :CLOSINGSTATUS");
		sql.append(" GROUP BY FM.FINREFERENCE, FINEVENT");

		try {
			return extractFeeAmounts(sql);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return new HashMap<>();
	}

	private Map<String, ControlDump> extractFeeAmounts(StringBuilder sql) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource(filterMap.getValues());
		paramMap.addValue("ES", "EarlySettlement");
		return parameterJdbcTemplate.query(sql.toString(), paramMap, new ResultSetExtractor<Map<String, ControlDump>>() {
			@Override
			public Map<String, ControlDump> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<String, ControlDump> map = new HashMap<>();
				ControlDump cd = null;
				String finEvent = null;
				String key = null;
				while (rs.next()) {
					key = rs.getString("FINREFERENCE");
					
					cd = map.get(key);
					if (cd == null) {
						cd = new ControlDump();
					}
					
					finEvent = StringUtils.trimToEmpty(rs.getString("FINEVENT"));

					if (StringUtils.equals("EarlySettlement", finEvent)) {
						cd.setForeClosureChargesDue(getAmount(rs, "PAIDAMOUNT"));
					}
					
					map.put(key, cd);

				}
				return map;
			}
		});
	}

	private Map<String, ControlDump> getReceiptAmounts() {
		StringBuilder sql = new StringBuilder();
		sql.append(" select FM.FINREFERENCE, SUM(WAIVEDAMOUNT) WAIVEDAMOUNT, RD.ALLOCATIONTYPE");
		sql.append(" from FINRECEIPTHEADER RH");
		sql.append(" INNER JOIN RECEIPTALLOCATIONDETAIL RD ON RD.RECEIPTID = RH.RECEIPTID");
		sql.append(" INNER JOIN FINANCEMAIN FM ON FM.FINREFERENCE = RH.REFERENCE");
		sql.append(" WHERE (RD.ALLOCATIONTYPE = :PRI OR RD.ALLOCATIONTYPE = :PFT)");
		sql.append(" AND COALESCE(FM.CLOSINGSTATUS, 'A') != :CLOSINGSTATUS");
		sql.append(" GROUP BY FM.FINREFERENCE, RD.ALLOCATIONTYPE");

		try {
			return extractReceiptAmounts(sql);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return new HashMap<>();
	}

	private Map<String, ControlDump> extractReceiptAmounts(StringBuilder sql) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource(filterMap.getValues());
		paramMap.addValue("PRI", "PRI");
		paramMap.addValue("PFT", "PFT");
		return parameterJdbcTemplate.query(sql.toString(), paramMap, new ResultSetExtractor<Map<String, ControlDump>>() {
			@Override
			public Map<String, ControlDump> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<String, ControlDump> map = new HashMap<>();
				ControlDump cd = null;
				String allocationType = null;
				String key = null;
				while (rs.next()) {
					key = rs.getString("FINREFERENCE");
					
					cd = map.get(key);
					if (cd == null) {
						cd = new ControlDump();
					}
					
					allocationType = StringUtils.trimToEmpty(rs.getString("ALLOCATIONTYPE"));

					if (allocationType.equals("PRI")) {
						cd.setEmiPrincipalWaived(getAmount(rs, "WAIVEDAMOUNT"));
						cd.setPrincipalWaived(getAmount(rs, "WAIVEDAMOUNT"));
					} else if(allocationType.equals("PFT")){
						cd.setEmiInterestWaived(getAmount(rs, "WAIVEDAMOUNT"));
					}

					map.put(key, cd);
				}
				return map;
			}
		});
	}

	private Map<String, ControlDump> getProfitDetails() {
		StringBuilder sql = new StringBuilder();

		sql.append(" select");
		sql.append(" FM.FINREFERENCE,");
		sql.append(" FIRSTREPAYDATE,");
		sql.append(" FIRSTREPAYAMT,");
		sql.append(" PFTACCRUED,");
		sql.append(" CURODDAYS,");
		sql.append(" ODPRINCIPAL,");
		sql.append(" ODPROFIT,");
		sql.append(" TOTALPRIBAL,");
		sql.append(" TOTALPFTBAL,");
		sql.append(" TOTALPRIPAID,");
		sql.append(" TOTALPFTPAID,");
		sql.append(" TOTALPFTSCHD,");
		sql.append(" ODPROFIT,");
		sql.append(" NOINST,");
		sql.append(" NOPAIDINST,");
		sql.append(" NOODINST,");
		sql.append(" ODPRINCIPAL,");
		sql.append(" FUTUREINST,");
		sql.append(" ACRTILLLBD");
		sql.append(" from FINPFTDETAILS PD");
		sql.append(" INNER JOIN FINANCEMAIN FM ON FM.FINREFERENCE = PD.FINREFERENCE");
		sql.append(" WHERE COALESCE(FM.CLOSINGSTATUS, 'A') != :CLOSINGSTATUS");

		try {
			return extractProfitDetails(sql);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return new HashMap<>();
	}

	private Map<String, ControlDump> extractProfitDetails(StringBuilder sql) {
		return parameterJdbcTemplate.query(sql.toString(), filterMap, new ResultSetExtractor<Map<String, ControlDump>>() {
			@Override
			public Map<String, ControlDump> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<String, ControlDump> map = new HashMap<>();
				ControlDump cd = null;
				while (rs.next()) {
					cd = new ControlDump();

					cd.setAccruedAmount(getAmount(rs, "PFTACCRUED"));
					cd.setDpd(rs.getLong("CURODDAYS"));
					cd.setEmiDue(getAmount(rs, "ODPRINCIPAL"));
					cd.setEmiDue(cd.getEmiDue().add(getAmount(rs, "ODPROFIT")));
					cd.setEmiOs(getAmount(rs, "TOTALPRIBAL"));
					cd.setPrincipalOs(getAmount(rs, "TOTALPRIBAL"));
					cd.setPrincipalBalance(getAmount(rs, "TOTALPRIBAL"));
					cd.setEmiOs(cd.getEmiOs().add(getAmount(rs, "TOTALPFTBAL")));
					cd.setEmiReceived(getAmount(rs, "TOTALPFTPAID"));
					cd.setEmiReceived(cd.getEmiReceived().add(getAmount(rs, "TOTALPRIPAID")));
					cd.setFirstDueDate(rs.getDate("FIRSTREPAYDATE"));
					cd.setInterestBalance(getAmount(rs, "TOTALPFTBAL"));
					cd.setInterestDue(getAmount(rs, "ODPROFIT"));
					cd.setInterestOs(getAmount(rs, "TOTALPFTBAL"));
					cd.setInterestReceived(getAmount(rs, "TOTALPFTPAID"));
					cd.setPrincipalDue(getAmount(rs, "ODPRINCIPAL"));
					cd.setPrincipalReceived(getAmount(rs, "TOTALPRIPAID"));
					cd.setTotalInterest(getAmount(rs, "TOTALPFTSCHD"));
					cd.setSohBalance(getAmount(rs, "TOTALPRIBAL").add(getAmount(rs, "TOTALPFTBAL")));
					cd.setNoOfEmiOs(rs.getInt("NOINST") - rs.getInt("NOPAIDINST"));
					cd.setNoOfUnbilledEmi(rs.getInt("FUTUREINST"));
					cd.setBalanceUmfc(getAmount(rs, "TOTALPFTSCHD"));
					cd.setBalanceUmfc(cd.getBalanceUmfc().subtract(getAmount(rs, "TOTALPFTPAID")));
					//cd.setBalanceUmfc(cd.getBalanceUmfc().subtract(getAmount(rs, "ACRTILLLBD")));
					cd.setLoanEmi(getAmount(rs, "FIRSTREPAYAMT"));

					map.put(rs.getString("FINREFERENCE"), cd);
				}
				return map;
			}

		});
	}

	private Map<String, ControlDump> getBalanceAmounts() {
		StringBuilder sql = new StringBuilder();

		sql.append(" select FM.FINREFERENCE, AMOUNT, BALANCEAMT, UTILISEDAMT, AMOUNTTYPE from FINEXCESSAMOUNT BA");
		sql.append(" INNER JOIN FINANCEMAIN FM ON FM.FINREFERENCE = BA.FINREFERENCE");
		sql.append(" WHERE COALESCE(FM.CLOSINGSTATUS, 'A') != :CLOSINGSTATUS");

		try {
			return extractBalanceAmounts(sql);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return null;
	}

	private Map<String, ControlDump> extractBalanceAmounts(StringBuilder sql) {
		return parameterJdbcTemplate.query(sql.toString(), filterMap, new ResultSetExtractor<Map<String, ControlDump>>() {
			@Override
			public Map<String, ControlDump> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<String, ControlDump> map = new HashMap<>();
				ControlDump cd = null;
				String amountType = null;
				while (rs.next()) {
					cd = new ControlDump();
					amountType = StringUtils.trimToEmpty(rs.getString("AMOUNTTYPE"));

					if ("A".equals(amountType)) {
						cd.setAdvanceEmi(getAmount(rs, "AMOUNT"));
						cd.setEmiInAdvanceUnbilled(getAmount(rs, "BALANCEAMT"));
					} else if ("E".equals(amountType)) {
						cd.setNetExcessAdjusted(getAmount(rs, "AMOUNT"));
						cd.setNetExcessReceived(getAmount(rs, "UTILISEDAMT"));
					}

					map.put(rs.getString("FINREFERENCE"), cd);
				}
				return map;
			}

		});
	}

	private Map<String, ControlDump> getDisbursementDetails() {
		StringBuilder sql = new StringBuilder();

		sql.append(" select FM.FINREFERENCE, MAX(DISBDATE) DISBDATE from FINDISBURSEMENTDETAILS DD");
		sql.append(" INNER JOIN FINANCEMAIN FM ON FM.FINREFERENCE = DD.FINREFERENCE");
		sql.append(" WHERE COALESCE(FM.CLOSINGSTATUS, 'A') != :CLOSINGSTATUS");
		sql.append(" GROUP BY FM.FINREFERENCE");
		try {
			return extractgDisbursementDetails(sql);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return new HashMap<>();
	}

	private Map<String, ControlDump> extractgDisbursementDetails(StringBuilder sql) {
		return parameterJdbcTemplate.query(sql.toString(), filterMap, new ResultSetExtractor<Map<String, ControlDump>>() {
			@Override
			public Map<String, ControlDump> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<String, ControlDump> map = new HashMap<>();
				ControlDump cd = null;
				while (rs.next()) {
					cd = new ControlDump();
					cd.setDisbursalDate(rs.getDate("DISBDATE"));
					map.put(rs.getString("FINREFERENCE"), cd);
				}
				return map;
			}
		});
	}

	private void saveControlDump(ControlDump item) throws SQLException {
		StringBuilder sql = new StringBuilder();

		sql.append("insert into CF_CONTROL_DUMP values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?,");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?,");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?,");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?,");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?,");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?,");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?,");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?,");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?,");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?,");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?,");
		sql.append("?,?,?)");

		List<Object[]> inputList = new ArrayList<Object[]>();

		formatAmounts(item);

		Object[] object = { item.getAgreementNo(), item.getAgreementId(), item.getProductFlag(), item.getSchemeId(),
				item.getBranchId(), item.getNpaStageId(), item.getLoanStatus(), item.getDisbStatus(),
				item.getFirstDueDate(), item.getMaturityDate(), item.getAmtFin(), item.getDisbursedAmount(),
				item.getEmiDue(), item.getPrincipalDue(), item.getInterestDue(), item.getEmiReceived(),
				item.getPrincipalReceived(), item.getInterestReceived(), item.getEmiOs(), item.getPrincipalOs(),
				item.getInterestOs(), item.getBulkRefund(), item.getPrincipalWaived(), item.getEmiPrincipalWaived(),
				item.getEmiInterestWaived(), item.getPrincipalAtTerm(), item.getAdvanceEmi(),
				item.getAdvanceEmiBilled(), item.getMigratedAdvanceEmi(), item.getMigratedAdvanceEmiBilled(),
				item.getMigratedAdvanceEmiUnbilled(), item.getClosedCanAdvEmi(), item.getPrincipalBalance(),
				item.getInterestBalance(), item.getSohBalance(), item.getNoOfUnbilledEmi(), item.getTotalInterest(),
				item.getAccruedAmount(), item.getBalanceUmfc(), item.getEmiInAdvanceReceivedMaker(),
				item.getEmiInAdvanceBilled(), item.getEmiInAdvanceUnbilled(), item.getMigAdvEmiBilledPrincomp(),
				item.getMigAdvEmiBilledIntcomp(), item.getMigAdvEmiUnbilledPrincomp(),
				item.getMigAdvEmiUnbilledIntcomp(), item.getEmiInAdvBilledPrincomp(),
				item.getEmiInAdvBilledIntcomp(), item.getEmiInAdvUnbilledPrincomp(),
				item.getEmiInAdvUnbilledIntcomp(), item.getClosCanAdvEmiPrincomp(), item.getClosCanAdvEmiIntcomp(),
				item.getSecurityDeposit(), item.getSecurityDepositAdjusted(), item.getRoundingDiffReceivable(),
				item.getRoundingDiffReceived(), item.getMigDifferenceReceivable(), item.getMigDifferenceReceived(),
				item.getMigDifferencePayable(), item.getMigDifferencePaid(), item.getWriteoffDue(),
				item.getWriteoffReceived(), item.getSoldSeizeReceivable(), item.getSoldSeizeReceived(),
				item.getSoldSeizePayable(), item.getSoldSeizePaid(), item.getNetExcessReceived(),
				item.getNetExcessAdjusted(), item.getLppChargesReceivable(), item.getLppChargesReceived(),
				item.getPdcSwapChargesReceivable(), item.getPdcSwapChargesReceived(),
				item.getRepoChargesReceivable(), item.getRepoChargesReceived(), item.getForeClosureChargesDue(),
				item.getForeClosureChargesReceived(), item.getBounceChargesDue(), item.getBounceChargesReceived(),
				item.getInsurRenewCharge(), item.getInsurRenewChargeRecd(), item.getInsurReceivable(),
				item.getInsurReceived(), item.getInsurPayable(), item.getInsurPaid(), item.getCustomerId(),
				item.getCustomerName(), item.getSanctionedTenure(), item.getLoanEmi(), item.getFlatRate(),
				item.getEffectiveRate(), item.getAgreementDate(), item.getDisbursalDate(), item.getClosureDate(),
				item.getNoOfAdvanceEmis(), item.getAssetCost(), item.getNoOfEmiOs(), item.getDpd(),
				item.getCurrentBucket(), item.getBranchName(), item.getSchemeName(), item.getDerivedBucket(),
				item.getAssetDesc(), item.getMake(), item.getChasisNum(), item.getRegdNum(), item.getEngineNum(),
				item.getInvoiceAmt(), item.getSupplierDesc(), item.getInstrument(), item.getRepoDate(),
				item.getLocalOutStationFlag(), item.getFirstRepaydueDate(), item.getCreatedOn() };
		inputList.add(object);
	

		destinationJdbcTemplate.getJdbcOperations().batchUpdate(sql.toString(), inputList);

		inputList = null;
	}

	private void formatAmounts(ControlDump item) {
		RoundingMode HALF_DOWN = RoundingMode.HALF_DOWN;
		BigDecimal units = item.getCcyMinorUnits();
		int scale = item.getAmountScale();

		item.setAmtFin(item.getAmtFin().divide(units, scale, HALF_DOWN));
		item.setDisbursedAmount(item.getDisbursedAmount().divide(units, scale, HALF_DOWN));
		item.setEmiDue(item.getEmiDue().divide(units, scale, HALF_DOWN));
		item.setPrincipalDue(item.getPrincipalDue().divide(units, scale, HALF_DOWN));
		item.setInterestDue(item.getInterestDue().divide(units, scale, HALF_DOWN));
		item.setEmiReceived(item.getEmiReceived().divide(units, scale, HALF_DOWN));
		item.setPrincipalReceived(item.getPrincipalReceived().divide(units, scale, HALF_DOWN));
		item.setInterestReceived(item.getInterestReceived().divide(units, scale, HALF_DOWN));
		item.setEmiOs(item.getEmiOs().divide(units, scale, HALF_DOWN));
		item.setPrincipalOs(item.getPrincipalOs().divide(units, scale, HALF_DOWN));
		item.setInterestOs(item.getInterestOs().divide(units, scale, HALF_DOWN));
		item.setBulkRefund(item.getBulkRefund().divide(units, scale, HALF_DOWN));
		item.setPrincipalWaived(item.getPrincipalWaived().divide(units, scale, HALF_DOWN));
		item.setEmiPrincipalWaived(item.getEmiPrincipalWaived().divide(units, scale, HALF_DOWN));
		item.setEmiInterestWaived(item.getEmiInterestWaived().divide(units, scale, HALF_DOWN));
		item.setPrincipalAtTerm(item.getPrincipalAtTerm().divide(units, scale, HALF_DOWN));
		item.setAdvanceEmi(item.getAdvanceEmi().divide(units, scale, HALF_DOWN));
		item.setAdvanceEmiBilled(item.getAdvanceEmiBilled().divide(units, scale, HALF_DOWN));
		item.setMigratedAdvanceEmi(item.getMigratedAdvanceEmi().divide(units, scale, HALF_DOWN));
		item.setMigratedAdvanceEmiBilled(item.getMigratedAdvanceEmiBilled().divide(units, scale, HALF_DOWN));
		item.setMigratedAdvanceEmiUnbilled(item.getMigratedAdvanceEmiUnbilled().divide(units, scale, HALF_DOWN));
		item.setClosedCanAdvEmi(item.getClosedCanAdvEmi().divide(units, scale, HALF_DOWN));
		item.setPrincipalBalance(item.getPrincipalBalance().divide(units, scale, HALF_DOWN));
		item.setInterestBalance(item.getInterestBalance().divide(units, scale, HALF_DOWN));
		item.setSohBalance(item.getSohBalance().divide(units, scale, HALF_DOWN));
		item.setTotalInterest(item.getTotalInterest().divide(units, scale, HALF_DOWN));
		item.setAccruedAmount(item.getAccruedAmount().divide(units, scale, HALF_DOWN));
		item.setBalanceUmfc(item.getBalanceUmfc().divide(units, scale, HALF_DOWN));
		item.setEmiInAdvanceReceivedMaker(item.getEmiInAdvanceReceivedMaker().divide(units, scale, HALF_DOWN));
		item.setEmiInAdvanceBilled(item.getEmiInAdvanceBilled().divide(units, scale, HALF_DOWN));
		item.setEmiInAdvanceUnbilled(item.getEmiInAdvanceUnbilled().divide(units, scale, HALF_DOWN));
		item.setMigAdvEmiBilledPrincomp(item.getMigAdvEmiBilledPrincomp().divide(units, scale, HALF_DOWN));
		item.setMigAdvEmiBilledIntcomp(item.getMigAdvEmiBilledIntcomp().divide(units, scale, HALF_DOWN));
		item.setMigAdvEmiUnbilledPrincomp(item.getMigAdvEmiUnbilledPrincomp().divide(units, scale, HALF_DOWN));
		item.setMigAdvEmiUnbilledIntcomp(item.getMigAdvEmiUnbilledIntcomp().divide(units, scale, HALF_DOWN));
		item.setEmiInAdvBilledPrincomp(item.getEmiInAdvBilledPrincomp().divide(units, scale, HALF_DOWN));
		item.setEmiInAdvBilledIntcomp(item.getEmiInAdvBilledIntcomp().divide(units, scale, HALF_DOWN));
		item.setEmiInAdvUnbilledPrincomp(item.getEmiInAdvUnbilledPrincomp().divide(units, scale, HALF_DOWN));
		item.setEmiInAdvUnbilledIntcomp(item.getEmiInAdvUnbilledIntcomp().divide(units, scale, HALF_DOWN));
		item.setClosCanAdvEmiPrincomp(item.getClosCanAdvEmiPrincomp().divide(units, scale, HALF_DOWN));
		item.setClosCanAdvEmiIntcomp(item.getClosCanAdvEmiIntcomp().divide(units, scale, HALF_DOWN));
		item.setSecurityDeposit(item.getSecurityDeposit().divide(units, scale, HALF_DOWN));
		item.setSecurityDepositAdjusted(item.getSecurityDepositAdjusted().divide(units, scale, HALF_DOWN));
		item.setRoundingDiffReceivable(item.getRoundingDiffReceivable().divide(units, scale, HALF_DOWN));
		item.setRoundingDiffReceived(item.getRoundingDiffReceived().divide(units, scale, HALF_DOWN));
		item.setMigDifferenceReceivable(item.getMigDifferenceReceivable().divide(units, scale, HALF_DOWN));
		item.setMigDifferenceReceived(item.getMigDifferenceReceived().divide(units, scale, HALF_DOWN));
		item.setMigDifferencePayable(item.getMigDifferencePayable().divide(units, scale, HALF_DOWN));
		item.setMigDifferencePaid(item.getMigDifferencePaid().divide(units, scale, HALF_DOWN));
		item.setWriteoffDue(item.getWriteoffDue().divide(units, scale, HALF_DOWN));
		item.setWriteoffReceived(item.getWriteoffReceived().divide(units, scale, HALF_DOWN));
		item.setSoldSeizeReceivable(item.getSoldSeizeReceivable().divide(units, scale, HALF_DOWN));
		item.setSoldSeizeReceived(item.getSoldSeizeReceived().divide(units, scale, HALF_DOWN));
		item.setSoldSeizePayable(item.getSoldSeizePayable().divide(units, scale, HALF_DOWN));
		item.setSoldSeizePaid(item.getSoldSeizePaid().divide(units, scale, HALF_DOWN));
		item.setNetExcessReceived(item.getNetExcessReceived().divide(units, scale, HALF_DOWN));
		item.setNetExcessAdjusted(item.getNetExcessAdjusted().divide(units, scale, HALF_DOWN));
		item.setLppChargesReceivable(item.getLppChargesReceivable().divide(units, scale, HALF_DOWN));
		item.setLppChargesReceived(item.getLppChargesReceived().divide(units, scale, HALF_DOWN));
		item.setPdcSwapChargesReceivable(item.getPdcSwapChargesReceivable().divide(units, scale, HALF_DOWN));
		item.setPdcSwapChargesReceived(item.getPdcSwapChargesReceived().divide(units, scale, HALF_DOWN));
		item.setRepoChargesReceivable(item.getRepoChargesReceivable().divide(units, scale, HALF_DOWN));
		item.setRepoChargesReceived(item.getRepoChargesReceived().divide(units, scale, HALF_DOWN));
		item.setForeClosureChargesDue(item.getForeClosureChargesDue().divide(units, scale, HALF_DOWN));
		item.setForeClosureChargesReceived(item.getForeClosureChargesReceived().divide(units, scale, HALF_DOWN));
		item.setBounceChargesDue(item.getBounceChargesDue().divide(units, scale, HALF_DOWN));
		item.setBounceChargesReceived(item.getBounceChargesReceived().divide(units, scale, HALF_DOWN));
		item.setInsurRenewCharge(item.getInsurRenewCharge().divide(units, scale, HALF_DOWN));
		item.setInsurRenewChargeRecd(item.getInsurRenewChargeRecd().divide(units, scale, HALF_DOWN));
		item.setInsurReceivable(item.getInsurReceivable().divide(units, scale, HALF_DOWN));
		item.setInsurReceived(item.getInsurReceived().divide(units, scale, HALF_DOWN));
		item.setInsurPayable(item.getInsurPayable().divide(units, scale, HALF_DOWN));
		item.setInsurPaid(item.getInsurPaid().divide(units, scale, HALF_DOWN));
		item.setLoanEmi(item.getLoanEmi().divide(units, scale, HALF_DOWN));		
		item.setAssetCost(item.getAssetCost().divide(units, scale, HALF_DOWN));
		item.setInvoiceAmt(item.getInvoiceAmt().divide(units, scale, HALF_DOWN));
	}

	private void deleteData() {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource parameterMap = new MapSqlParameterSource();
		parameterMap.addValue("CREATED_ON", appDate);

		destinationJdbcTemplate.update("DELETE FROM CF_CONTROL_DUMP_LOG WHERE CREATED_ON = :CREATED_ON", parameterMap);
		destinationJdbcTemplate.update("DELETE FROM CF_CONTROL_DUMP WHERE CREATED_ON = :CREATED_ON", parameterMap);

		logger.debug(Literal.LEAVING);
	}

	private void deleteOldData() {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource parameterMap = new MapSqlParameterSource();
		parameterMap.addValue("CREATED_ON", appDate);

		destinationJdbcTemplate.update("DELETE FROM CF_CONTROL_DUMP WHERE CREATED_ON != :CREATED_ON", parameterMap);

		logger.debug(Literal.LEAVING);
	}
}
