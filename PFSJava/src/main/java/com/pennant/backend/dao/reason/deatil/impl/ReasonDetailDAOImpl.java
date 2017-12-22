package com.pennant.backend.dao.reason.deatil.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.reason.deatil.ReasonDetailDAO;
import com.pennant.backend.model.reason.details.ReasonDetails;
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
	public List<Map<String, Object>> getReasonDetailsLog(String reference) {
		logger.debug(Literal.ENTERING);
		
		final List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		
		source.addValue("Reference", reference);
		
		StringBuilder sql = new StringBuilder();
		sql.append(" Select RH.Module, RH.Reference, RH.Remarks, RH.Rolecode, RH.Activity, RC.Code, RC.Description,");
		sql.append(" RH.Touser, RH.Logtime from ReasonHeader RH inner join ReasonDetails RD ON RH.ID = RD.HeaderId");
		sql.append(" inner join Reasons RS ON RS.ID = RD.ReasonID");
		sql.append(" inner join ReasonCategory RC ON RC.ID = RS.ReasonCategoryId");
		sql.append(" inner join ReasonTypes RT ON RT.ID = RS.ReasonTypeID Where RH.Reference = :Reference");


		namedParameterJdbcTemplate.query(sql.toString(), source, new ResultSetExtractor<List<Map<String, Object>>>() {
					public List<Map<String, Object>> extractData(ResultSet rs) throws SQLException {
						while (rs.next()) {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("Module", rs.getString("Module"));
							map.put("Reference", rs.getString("Reference"));
							map.put("Remarks", rs.getString("Remarks"));
							map.put("Rolecode", rs.getString("Rolecode"));
							map.put("Activity", rs.getString("Activity"));
							map.put("Code", rs.getString("Description"));
							map.put("Touser", rs.getLong("Touser"));
							map.put("Logtime", rs.getDate("Logtime"));
							map.put("Code", rs.getString("Code"));
							list.add(map);
						}
						return list;
					};
				});
		logger.debug(Literal.LEAVING);
		return list;
	}
}
