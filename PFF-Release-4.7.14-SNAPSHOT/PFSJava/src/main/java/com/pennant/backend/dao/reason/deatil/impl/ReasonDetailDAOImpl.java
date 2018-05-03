package com.pennant.backend.dao.reason.deatil.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.reason.deatil.ReasonDetailDAO;
import com.pennant.backend.model.reason.details.ReasonDetails;
import com.pennant.backend.model.reason.details.ReasonDetailsLog;
import com.pennant.backend.model.reason.details.ReasonHeader;
import com.pennanttech.pennapps.core.resource.Literal;

public class ReasonDetailDAOImpl extends BasisNextidDaoImpl<ReasonHeader> implements ReasonDetailDAO {
	private static Logger logger = Logger.getLogger(ReasonDetailDAOImpl.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public ReasonDetailDAOImpl() {
		super();
	}

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public long save(ReasonHeader reasonHeader) {
		logger.debug(Literal.ENTERING);
		try {
			long id = saveHeader(reasonHeader);
			List<ReasonDetails> details = reasonHeader.getDetailsList();
			
			if (details != null && !details.isEmpty()) {
				for (ReasonDetails reasonDetails : details) {
					reasonDetails.setHeaderId(id);
				}
				saveDetails(details);
			}
			
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);

		return reasonHeader.getId();
	}

	private long saveHeader(ReasonHeader reasonHeader) throws Exception {
		StringBuilder sql = null;
		SqlParameterSource beanParameters = null;

		try {
			if (reasonHeader.getId() == Long.MIN_VALUE) {
				reasonHeader.setId(getNextidviewDAO().getNextId("SeqReasonHeader"));
			}
			sql = new StringBuilder("Insert Into  ReasonHeader ");
			sql.append(" (Id, Module, Reference, Remarks, Rolecode, Activity, ToUser, LogTime)");
			sql.append(" Values(:Id, :Module, :Reference, :Remarks, :RoleCode, :Activity, :ToUser, :LogTime)");
			logger.debug("insertSql: " + sql.toString());

			beanParameters = new BeanPropertySqlParameterSource(reasonHeader);
			this.namedParameterJdbcTemplate.update(sql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		} finally {
			beanParameters = null;
			sql = null;
		}
		return reasonHeader.getId();
	}

	private void saveDetails(List<ReasonDetails> detailsList) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		SqlParameterSource[] beanParameters = null;
		try {
			sql = new StringBuilder("Insert Into  ReasonDetails ");
			sql.append(" (HeaderId, ReasonId)");
			sql.append(" Values( :HeaderId, :ReasonId)");
			logger.debug("insertSql: " + sql.toString());

			beanParameters = SqlParameterSourceUtils.createBatch(detailsList.toArray());
			this.namedParameterJdbcTemplate.batchUpdate(sql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		} finally {
			beanParameters = null;
			sql = null;
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<ReasonDetailsLog> getReasonDetailsLog(String reference) {
		logger.debug(Literal.ENTERING);

		RowMapper<ReasonDetailsLog> mapper = null;
		MapSqlParameterSource source = null;

		StringBuilder sql = new StringBuilder();
		sql.append(" Select RH.Module, RH.Reference, RH.Remarks, RH.Rolecode,SR.RoleDesc, RH.Activity, RC.Code, RC.Description,");
		sql.append(" RH.Touser,SU.UsrLogin,SU.Usrfname ,SU.Usrmname ,SU.Usrlname , RH.Logtime ");
		sql.append(" from ReasonHeader RH inner join ReasonDetails RD ON RH.ID = RD.HeaderId");
		sql.append(" inner join Reasons RS ON RS.ID = RD.ReasonID");
		sql.append(" inner join ReasonCategory RC ON RC.ID = RS.ReasonCategoryId");
		sql.append(" inner join ReasonTypes RT ON RT.ID = RS.ReasonTypeID ");
		sql.append(" inner join SecUsers SU ON SU.UsrId = RH.Touser ");
		sql.append(" inner join SecRoles SR ON SR.RoleCd = RH.Rolecode Where RH.Reference = :Reference");
		logger.debug(Literal.SQL + sql.toString());

		mapper = ParameterizedBeanPropertyRowMapper.newInstance(ReasonDetailsLog.class);
		source = new MapSqlParameterSource();
		source.addValue("Reference", reference);
		try {
			return this.namedParameterJdbcTemplate.query(sql.toString(),source, mapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			source = null;
			mapper = null;
			logger.debug(Literal.LEAVING);
		}
		return null;
	}
	
	@Override
	public boolean isreasonCodeExists(long reasonCode) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("reasonid", reasonCode);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(reasonid)");
		selectSql.append(" From ReasonDetails ");
		selectSql.append(" Where reasonid=:reasonid");

		logger.debug("selectSql: " + selectSql.toString());
		int rcdCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);

		logger.debug("Leaving");
		return rcdCount > 0 ? true : false;
	}
}
