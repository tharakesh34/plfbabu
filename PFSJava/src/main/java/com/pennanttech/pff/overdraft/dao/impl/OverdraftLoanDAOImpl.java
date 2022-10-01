package com.pennanttech.pff.overdraft.dao.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.FinanceConstants;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.overdraft.dao.OverdraftLoanDAO;
import com.pennanttech.pff.overdraft.model.OverdraftDTO;
import com.pennanttech.pff.overdraft.model.OverdraftLimit;

public class OverdraftLoanDAOImpl extends SequenceDao<OverdraftLimit> implements OverdraftLoanDAO {
	private static Logger logger = LogManager.getLogger(OverdraftLoanDAOImpl.class);

	public OverdraftLoanDAOImpl() {
		super();
	}

	@Override
	public OverdraftDTO getLoanDetails(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FiniD, fm.FinReference, fm.OverdraftTxnChrgReq, fm.OverdraftCalcChrg");
		sql.append(", fm.OverdraftChrgAmtOrPerc, fm.OverdraftChrCalOn, fm.FinType, fm.ProductCategory");
		sql.append(", fm.NextRepayDate, fm.FinCcy, ft.OverdraftTxnChrgFeeType, pd.NSchdDate");
		sql.append(", txnChr.FeeTypeID TxnChrId, txnChr.FeeTypeCode TxnChrCode, txnChr.DueAccReq TxnChrDueAccReq");
		sql.append(", colChr.FeeTypeID ColChrId, colChr.FeeTypeCode ColChrCode, colChr.DueAccReq ColChrDueAccReq");
		sql.append(", pr.ODGraceDays, pr.OverDraftExtGraceDays, pr.OverDraftColAmt");
		sql.append(" From FinanceMain fm");
		sql.append(" Inner Join FinPftDetails pd On pd.FinReference = fm.FinReference");
		sql.append(" Inner Join RMTFinanceTypes ft On ft.FinType = fm.FinType");
		sql.append(" Left Join FeeTypes txnChr On txnChr.FeeTypeID = ft.OverdraftTxnChrgFeeType");
		sql.append(" Left Join FeeTypes colChr On colChr.FeeTypeID = ft.OverDraftColChrgFeeType");
		sql.append(" Left Join FinODPenaltyRates pr On pr.FinReference = fm.FinReference");
		sql.append(" Where fm.FinID = ? and fm.ProductCategory = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				OverdraftDTO fm = new OverdraftDTO();

				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(rs.getString("FinReference"));
				fm.setOverdraftTxnChrgReq(rs.getBoolean("OverdraftTxnChrgReq"));
				fm.setOverdraftCalcChrg(rs.getString("OverdraftCalcChrg"));
				fm.setOverdraftChrgAmtOrPerc(rs.getBigDecimal("OverdraftChrgAmtOrPerc"));
				fm.setOverdraftChrCalOn(rs.getString("OverdraftChrCalOn"));
				fm.setFinType(rs.getString("FinType"));
				fm.setProductCategory(rs.getString("ProductCategory"));
				fm.setNextRepayDate(rs.getDate("NextRepayDate"));
				fm.setFinCcy(rs.getString("FinCcy"));
				fm.setOverdraftTxnChrgFeeType(rs.getLong("OverdraftTxnChrgFeeType"));
				fm.setNextSchdDate(JdbcUtil.getDate(rs.getDate("NSchdDate")));

				FeeType txnChrFeeType = fm.getTxnChrFeeType();
				txnChrFeeType.setFeeTypeID(rs.getLong("TxnChrId"));
				txnChrFeeType.setFeeTypeCode(rs.getString("TxnChrCode"));
				txnChrFeeType.setDueAccReq(rs.getBoolean("TxnChrDueAccReq"));

				FeeType colChrFeeType = fm.getColChrFeeType();
				colChrFeeType.setFeeTypeID(rs.getLong("ColChrId"));
				colChrFeeType.setFeeTypeCode(rs.getString("ColChrCode"));
				colChrFeeType.setDueAccReq(rs.getBoolean("ColChrDueAccReq"));

				FinODPenaltyRate pr = fm.getPenaltyRate();
				pr.setODGraceDays(rs.getInt("ODGraceDays"));
				pr.setOverDraftExtGraceDays(rs.getInt("OverDraftExtGraceDays"));
				pr.setOverDraftColAmt(rs.getBigDecimal("OverDraftColAmt"));

				return fm;
			}, finID, FinanceConstants.PRODUCT_ODFACILITY);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}

	@Override
	public FinanceMain getLoanBasicDetails(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ProductCategory, FinAssetValue, NumberOfTerms, RepayFrq");
		sql.append(", OverdraftTxnChrgReq, OverdraftCalcChrg, OverdraftChrgAmtOrPerc, OverdraftChrCalOn");
		sql.append(" From FinanceMain");
		sql.append(" Where FinID = ? and ProductCategory = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();
				fm.setProductCategory(rs.getString("ProductCategory"));
				fm.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
				fm.setNumberOfTerms(rs.getInt("NumberOfTerms"));
				fm.setRepayFrq(rs.getString("RepayFrq"));
				fm.setOverdraftTxnChrgReq(rs.getBoolean("OverdraftTxnChrgReq"));
				fm.setOverdraftCalcChrg(rs.getString("OverdraftCalcChrg"));
				fm.setOverdraftChrgAmtOrPerc(rs.getBigDecimal("OverdraftChrgAmtOrPerc"));
				fm.setOverdraftChrCalOn(rs.getString("OverdraftChrCalOn"));

				return fm;
			}, finID, FinanceConstants.PRODUCT_ODFACILITY);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}

	@Override
	public OverdraftDTO getChargeConfig(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.ProductCategory, fm.OverdraftTxnChrgReq, rt.OverdraftTxnChrgFeeType, ft.FeeTypeCode");
		sql.append(" From FinanceMain fm");
		sql.append(" Inner Join RmtFinanceTypes rt on rt.FinType = fm.FinType");
		sql.append(" Inner Join Feetypes ft on ft.FeetypeId = rt.OverdraftTxnChrgFeeType");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				OverdraftDTO fm = new OverdraftDTO();
				fm.setProductCategory(rs.getString("ProductCategory"));
				fm.setOverdraftTxnChrgReq(rs.getBoolean("OverdraftTxnChrgReq"));
				fm.setOverdraftTxnChrgFeeType(rs.getLong("OverdraftTxnChrgFeeType"));
				fm.getTxnChrFeeType().setFeeTypeCode(rs.getString("FeeTypeCode"));

				return fm;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}

	@Override
	public long getOverdraftTxnChrgFeeType(String finType) {
		String sql = "Select OverdraftTxnChrgFeeType From RMTFinanceTypes Where FinType = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql, Long.class, finType);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}
		logger.debug(Literal.LEAVING);
		return Long.MIN_VALUE;
	}

}
