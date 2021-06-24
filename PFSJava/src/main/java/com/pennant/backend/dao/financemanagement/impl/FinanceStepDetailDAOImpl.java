package com.pennant.backend.dao.financemanagement.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.financemanagement.FinanceStepDetailDAO;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class FinanceStepDetailDAOImpl extends BasicDao<StepPolicyDetail> implements FinanceStepDetailDAO {
	private static Logger logger = LogManager.getLogger(FinanceStepDetailDAOImpl.class);

	public FinanceStepDetailDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new StepPolicyDetail
	 * 
	 * @return StepPolicyDetail
	 */
	@Override
	public FinanceStepPolicyDetail getFinStepPolicy() {
		logger.debug("Entering");
		FinanceStepPolicyDetail finStepDetail = new FinanceStepPolicyDetail();
		logger.debug("Leaving");
		return finStepDetail;
	}

	/**
	 * This method get the module from method getStepPolicyDetail() and set the new record flag as true and return
	 * StepPolicyDetail()
	 * 
	 * @return StepPolicyDetail
	 */
	@Override
	public FinanceStepPolicyDetail getNewFinStepPolicy() {
		logger.debug("Entering");
		FinanceStepPolicyDetail stepPolicyDetail = getFinStepPolicy();
		stepPolicyDetail.setNewRecord(true);
		logger.debug("Leaving");
		return stepPolicyDetail;
	}

	/**
	 * Fetch the Record Finance Types details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return StepPolicyDetail
	 */
	@Override
	public List<FinanceStepPolicyDetail> getFinStepDetailListByFinRef(final String finReference, String type,
			boolean isWIF) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, StepNo, TenorSplitPerc, Installments, RateMargin, EmiSplitPerc");
		sql.append(", SteppedEMI, StepSpecifier, StepStart, StepEnd, AutoCal");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");

		if (isWIF) {
			sql.append(" from WIFFinStepPolicyDetail");
		} else {
			sql.append(" from FinStepPolicyDetail");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql);

		try {
			return this.jdbcOperations.query(sql.toString(), ps -> {
				ps.setString(1, finReference);
			}, (rs, i) -> {
				FinanceStepPolicyDetail spd = new FinanceStepPolicyDetail();

				spd.setFinReference(rs.getString("FinReference"));
				spd.setStepNo(rs.getInt("StepNo"));
				spd.setTenorSplitPerc(rs.getBigDecimal("TenorSplitPerc"));
				spd.setInstallments(rs.getInt("Installments"));
				spd.setRateMargin(rs.getBigDecimal("RateMargin"));
				spd.setEmiSplitPerc(rs.getBigDecimal("EmiSplitPerc"));
				spd.setSteppedEMI(rs.getBigDecimal("SteppedEMI"));
				spd.setStepSpecifier(rs.getString("StepSpecifier"));
				spd.setStepStart(rs.getDate("StepStart"));
				spd.setStepEnd(rs.getDate("StepEnd"));
				spd.setAutoCal(rs.getBoolean("AutoCal"));
				spd.setVersion(rs.getInt("Version"));
				spd.setLastMntBy(rs.getLong("LastMntBy"));
				spd.setLastMntOn(rs.getTimestamp("LastMntOn"));
				spd.setRecordStatus(rs.getString("RecordStatus"));
				spd.setRoleCode(rs.getString("RoleCode"));
				spd.setNextRoleCode(rs.getString("NextRoleCode"));
				spd.setTaskId(rs.getString("TaskId"));
				spd.setNextTaskId(rs.getString("NextTaskId"));
				spd.setRecordType(rs.getString("RecordType"));
				spd.setWorkflowId(rs.getLong("WorkflowId"));

				return spd;
			});
		} catch (EmptyResultDataAccessException e) {
			String name = isWIF == true ? "WIF" : "";
			logger.warn("Record is not found in {}FinStepPolicyDetail{} for the specified FinReference >> {}", name,
					type, finReference);
		}

		return new ArrayList<>();
	}

	/**
	 * This method insert new Records into StepPolicyDetails or StepPolicyDetails_Temp.
	 * 
	 * save StepPolicyDetails
	 * 
	 * @param FinanceStepPolicyDetail
	 *            (StepPolicyDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void saveList(List<FinanceStepPolicyDetail> finStepDetailList, boolean isWIF, String type) {
		logger.debug("Entering ");

		StringBuilder insertSql = new StringBuilder("Insert Into ");
		if (isWIF) {
			insertSql.append(" WIFFinStepPolicyDetail");
		} else {
			insertSql.append(" FinStepPolicyDetail");
		}
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" (FinReference, StepNo, TenorSplitPerc, Installments,  RateMargin, EmiSplitPerc, SteppedEMI, ");
		insertSql.append(" StepSpecifier, StepStart, StepEnd, AutoCal, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(
				" Values(:FinReference, :StepNo, :TenorSplitPerc, :Installments, :RateMargin, :EmiSplitPerc, :SteppedEMI, ");
		insertSql.append(" :StepSpecifier, :StepStart, :StepEnd, :AutoCal, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(finStepDetailList.toArray());
		this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving ");
	}

	/**
	 * This method Deletes the Record from the StepPolicyDetails or StepPolicyDetails_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete Finance Types by key FinType
	 * 
	 * @param Finance
	 *            Types (StepPolicyDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void deleteList(String finReference, boolean isWIF, String type) {
		logger.debug("Entering");

		FinanceStepPolicyDetail finStepPolicy = new FinanceStepPolicyDetail();
		finStepPolicy.setFinReference(finReference);

		StringBuilder deleteSql = new StringBuilder("Delete From ");
		if (isWIF) {
			deleteSql.append(" WIFFinStepPolicyDetail");
		} else {
			deleteSql.append(" FinStepPolicyDetail");
		}
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append("  Where  FinReference = :FinReference ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finStepPolicy);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

}
