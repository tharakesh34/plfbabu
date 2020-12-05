package com.pennant.backend.dao.finance.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.CashBackDetailDAO;
import com.pennant.backend.model.finance.CashBackDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class CashBackDetailDAOImpl extends BasicDao<CashBackDetail> implements CashBackDetailDAO {

	/**
	 * This method insert new Records into CashBackDetail.
	 * 
	 * save CashBackDetail
	 * 
	 * @param CashBackDetail
	 *            (cashBackDetail)
	 * 
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void save(List<CashBackDetail> cashBackDetail) {

		logger.debug(Literal.ENTERING);

		StringBuilder insertSql = new StringBuilder("Insert Into CashBackDetails");
		insertSql.append(" (FinReference,Type, AdviseId, Amount, Refunded)");

		insertSql.append(" Values(:FinReference, :Type, :AdviseId, :Amount, :Refunded)");

		logger.trace(Literal.SQL + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(cashBackDetail.toArray());
		try {
			this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error("Exception", e);
			throw e;
		}
		logger.debug(Literal.LEAVING);

	}

	@Override
	public List<CashBackDetail> getCashBackDetails() {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Refunded", 0);

		StringBuilder sql = new StringBuilder();
		sql.append(
				" Select FM.PromotionSeqID , CB.FinReference , CB.Type,FM.FinStartDate, FM.MandateID,CB.AdviseID, FT.FeeTypeCode, CB.Amount, FE.HOSTREFERENCE");
		sql.append(" From CASHBACKDETAILS CB ");
		sql.append(" INNER JOIN FinanceMain FM ON CB.FinReference=FM.FinReference ");
		sql.append(" INNER JOIN ManualAdvise MA ON MA.AdviseID= CB.AdviseID ");
		sql.append(" INNER JOIN FeeTypes FT ON FT.FeeTypeID= MA.FeeTypeID ");
		sql.append(" INNER JOIN FINANCEMAIN_EXTENSION FE ON FM.FinReference = FE.FinReference ");
		sql.append(" where Refunded=:Refunded ");

		logger.debug("selectSql: " + sql.toString());
		List<CashBackDetail> cashBackDetailList = new ArrayList<CashBackDetail>();

		RowMapper<CashBackDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CashBackDetail.class);
		try {
			cashBackDetailList = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.debug(e);
		}

		logger.debug("Leaving");
		return cashBackDetailList;
	}

	@Override
	public CashBackDetail getManualAdviseIdByFinReference(String finReference, String type) {
		logger.debug("Entering");

		CashBackDetail cashBackDetail = new CashBackDetail();
		cashBackDetail.setFinReference(finReference);
		cashBackDetail.setType(type);
		cashBackDetail.setRefunded(false);
		StringBuilder selectSql = new StringBuilder("Select AdviseId,FinReference,Amount ");
		selectSql.append(" From CashBackDetails");
		selectSql.append(" Where FinReference =:FinReference AND Type =:Type AND Refunded =:Refunded");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(cashBackDetail);
		logger.debug("Leaving");
		RowMapper<CashBackDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CashBackDetail.class);
		try {
			cashBackDetail = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			cashBackDetail = null;
		}
		logger.debug("Leaving");
		return cashBackDetail;

	}

	@Override
	public int updateCashBackDetail(long adviseId) {
		int recordCount = 0;
		logger.debug("Entering");
		CashBackDetail cashBackDetail = new CashBackDetail();
		cashBackDetail.setAdviseId(adviseId);
		cashBackDetail.setRefunded(true);
		StringBuilder updateSql = new StringBuilder("Update ");

		updateSql.append(" CashBackDetails set Refunded =:Refunded ");
		updateSql.append(" Where AdviseId = :AdviseId");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(cashBackDetail);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
		return recordCount;
	}

}
