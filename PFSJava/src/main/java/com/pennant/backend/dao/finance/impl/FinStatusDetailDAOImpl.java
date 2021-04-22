package com.pennant.backend.dao.finance.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.finance.FinStatusDetailDAO;
import com.pennant.backend.model.finance.FinStatusDetail;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;

public class FinStatusDetailDAOImpl extends BasicDao<FinStatusDetail> implements FinStatusDetailDAO {
	private static Logger logger = LogManager.getLogger(FinStatusDetailDAOImpl.class);

	public FinStatusDetailDAOImpl() {
		super();
	}

	@Override
	public void save(FinStatusDetail finStatusDetail) {
		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" FinStatusDetail");
		sql.append("(FinReference, ValueDate, CustId, FinStatus, ODDays");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?");
		sql.append(")");

		logger.trace(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setString(index++, finStatusDetail.getFinReference());
				ps.setDate(index++, JdbcUtil.getDate(finStatusDetail.getValueDate()));
				ps.setLong(index++, finStatusDetail.getCustId());
				ps.setString(index++, finStatusDetail.getFinStatus());
				ps.setInt(index++, finStatusDetail.getODDays());
			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
	}

	@Override
	public void saveOrUpdateFinStatus(FinStatusDetail finStatusDetail) {
		StringBuilder sql = new StringBuilder("DELETE FROM  FinStatusDetail");
		sql.append(" Where FinReference = ? AND ValueDate = ?");

		logger.trace(Literal.SQL + sql.toString());
		this.jdbcOperations.update(sql.toString(), ps -> {
			ps.setString(1, finStatusDetail.getFinReference());
			ps.setDate(2, JdbcUtil.getDate(finStatusDetail.getValueDate()));

		});

		save(finStatusDetail);
	}

	public int update(FinStatusDetail finStatusDetail) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinStatusDetail ");
		updateSql.append(" Set FinStatus=:FinStatus  ");
		updateSql.append(" Where FinReference =:FinReference AND ValueDate=:ValueDate");

		logger.debug("insertSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finStatusDetail);
		return this.jdbcTemplate.update(updateSql.toString(), beanParameters);
	}

	@Override
	public List<FinStatusDetail> getFinStatusDetailList(Date valueDate) {
		logger.debug("Entering");

		FinStatusDetail finStatusDetail = new FinStatusDetail();
		finStatusDetail.setValueDate(valueDate);

		StringBuilder selectSql = new StringBuilder(" Select CustId , FinStatus ");
		selectSql.append(" From FinStatusDetail_View");
		selectSql.append(" Where ValueDate =:ValueDate ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finStatusDetail);
		RowMapper<FinStatusDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(FinStatusDetail.class);
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public void updateCustStatuses(List<FinStatusDetail> custStatuses) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("Update Customers  ");
		selectSql.append(" Set CustSts = :FinStatus, CustStsChgDate= :ValueDate WHERE CustId=:CustId ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(custStatuses.toArray());
		logger.debug("Leaving");
		this.jdbcTemplate.batchUpdate(selectSql.toString(), beanParameters);
	}

	public List<FinStatusDetail> getFinStatusDetailByRefId(String finReference) {
		logger.debug(Literal.LEAVING);

		FinStatusDetail finStatusDetail = new FinStatusDetail();
		finStatusDetail.setFinReference(finReference);

		StringBuilder sql = new StringBuilder(" Select CustId, FinStatus, ValueDate, OdDays, FinReference");
		sql.append(" From FinStatusDetail");
		sql.append(" Where FinReference =:FinReference");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finStatusDetail);

		RowMapper<FinStatusDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(FinStatusDetail.class);
		logger.debug(Literal.LEAVING);

		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);

	}
}
