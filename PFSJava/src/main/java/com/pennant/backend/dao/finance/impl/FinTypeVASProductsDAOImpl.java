package com.pennant.backend.dao.finance.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.FinTypeVASProductsDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.financemanagement.FinTypeVASProducts;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class FinTypeVASProductsDAOImpl extends SequenceDao<FinTypeVASProducts> implements FinTypeVASProductsDAO {
	private static Logger logger = LogManager.getLogger(FinTypeVASProductsDAOImpl.class);

	public FinTypeVASProductsDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new FinanceFlag
	 * 
	 * @return FinanceFlag
	 */
	@Override
	public FinTypeVASProducts getfinTypeVASProducts() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("");

		FinTypeVASProducts finTypeVASProducts = new FinTypeVASProducts();
		if (workFlowDetails != null) {
			finTypeVASProducts.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return finTypeVASProducts;

	}

	/**
	 * This method get the module from method getfinTypeVASProducts() and set the new record flag as true and return
	 * FinTypeVASProducts
	 * 
	 * @return FinTypeVASProducts
	 */
	@Override
	public FinTypeVASProducts getNewfinTypeVASProducts() {
		logger.debug("Entering");
		FinTypeVASProducts finTypeVASProducts = getfinTypeVASProducts();
		finTypeVASProducts.setNewRecord(true);
		logger.debug("Leaving");
		return finTypeVASProducts;
	}

	/**
	 * This method insert new Records into FinTypeVASProducts or FinTypeVASProducts_Temp.
	 * 
	 * save FinTypeVASProducts
	 * 
	 * @param FinTypeVASProducts
	 *            (finTypeVASProducts)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void save(FinTypeVASProducts finTypeVASProducts, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into ");
		insertSql.append(" FinTypeVASProducts");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinType,VasProduct,Mandatory,");
		insertSql.append(
				" Version,LastMntBy,LastMntOn,RecordStatus,RoleCode,NextRoleCode,TaskId,NextTaskId,RecordType,WorkflowId)");
		insertSql.append(" values (:FinType,:VasProduct,:Mandatory, ");
		insertSql.append(" :Version,:LastMntBy,:LastMntOn,:RecordStatus,:RoleCode,:NextRoleCode,:TaskId,");

		insertSql.append(" :NextTaskId,:RecordType,:WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeVASProducts);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");

	}

	public void delete(String finType, String vasProduct, String type) {
		logger.debug("Entering");
		FinTypeVASProducts finTypeVASProducts = new FinTypeVASProducts();
		finTypeVASProducts.setFinType(finType);
		finTypeVASProducts.setVasProduct(vasProduct);
		StringBuilder deleteSql = new StringBuilder("Delete From ");
		deleteSql.append(" FinTypeVASProducts");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinType =:FinType AND VasProduct =:VasProduct");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeVASProducts);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Method for Updating Finance FinTypeVASProducts Details
	 * 
	 * @param finTypeVASProducts
	 * @param type
	 */
	@Override
	public void update(FinTypeVASProducts finTypeVASProducts, String type) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinTypeVASProducts");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set Mandatory = :Mandatory, Version = :Version,");
		updateSql.append(" LastMntBy = :LastMntBy , LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, ");
		updateSql.append(
				" RoleCode= :RoleCode, NextRoleCode = :NextRoleCode,TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinType =:FinType  AND VasProduct =:VasProduct ");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeVASProducts);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");

	}

	@Override
	public List<FinTypeVASProducts> getVASProductsByFinType(String finType, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(
				" t1.FinType, t1.VasProduct, t1.Mandatory, t1.Version, t1.LastMntBy, t1.LastMntOn, t1.RecordStatus, t1.RoleCode");
		sql.append(
				", t1.NextRoleCode, t1.TaskId, t1.NextTaskId, t1.RecordType, t1.WorkflowId, t3.ProductType, t4.ProductCtgDesc");
		sql.append(", t5.DealerName ManufacturerDesc, t2.RecAgainst, t2.VasFee");
		sql.append(" from FinTypeVASProducts");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" t1 left outer join VasStructure  t2 on t1.VasProduct = t2.ProductCode");
		sql.append(" left outer join VasProductType  t3 on t3.ProductType = t2.ProductType");
		sql.append(" left outer join VasProductCategory  t4 on t3.ProductCtg = t4.ProductCtg");
		sql.append(" left outer join AMTVehicleDealer t5 on t2.ManufacturerId = t5.DealerId");
		sql.append(" Where FinType = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, finType);
				}
			}, new RowMapper<FinTypeVASProducts>() {
				@Override
				public FinTypeVASProducts mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinTypeVASProducts vas = new FinTypeVASProducts();

					vas.setFinType(rs.getString("FinType"));
					vas.setVasProduct(rs.getString("VasProduct"));
					vas.setMandatory(rs.getBoolean("Mandatory"));
					vas.setVersion(rs.getInt("Version"));
					vas.setLastMntBy(rs.getLong("LastMntBy"));
					vas.setLastMntOn(rs.getTimestamp("LastMntOn"));
					vas.setRecordStatus(rs.getString("RecordStatus"));
					vas.setRoleCode(rs.getString("RoleCode"));
					vas.setNextRoleCode(rs.getString("NextRoleCode"));
					vas.setTaskId(rs.getString("TaskId"));
					vas.setNextTaskId(rs.getString("NextTaskId"));
					vas.setRecordType(rs.getString("RecordType"));
					vas.setWorkflowId(rs.getLong("WorkflowId"));
					vas.setProductType(rs.getString("ProductType"));
					vas.setProductCtgDesc(rs.getString("ProductCtgDesc"));
					vas.setManufacturerDesc(rs.getString("ManufacturerDesc"));
					vas.setRecAgainst(rs.getString("RecAgainst"));
					vas.setVasFee(rs.getBigDecimal("VasFee"));

					return vas;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	/**
	 * Fetch the Record Finance Flags details by key field
	 * 
	 * @param finRef
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return finFlagsDetail
	 */
	@Override
	public FinTypeVASProducts getFinTypeVASProducts(final String finType, String vasProduct, String type) {
		logger.debug("Entering");
		FinTypeVASProducts finTypeVASProducts = new FinTypeVASProducts();
		finTypeVASProducts.setFinType(finType);
		finTypeVASProducts.setVasProduct(vasProduct);

		StringBuilder selectSql = new StringBuilder(" Select FinType,VasProduct,Mandatory, ");
		selectSql.append(" Version,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From FinTypeVASProducts");

		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType =:FinType AND VasProduct =:VasProduct ");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeVASProducts);
		RowMapper<FinTypeVASProducts> typeRowMapper = BeanPropertyRowMapper.newInstance(FinTypeVASProducts.class);

		try {
			finTypeVASProducts = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finTypeVASProducts = null;
		}
		logger.debug("Leaving");
		return finTypeVASProducts;
	}

	@Override
	public void deleteList(String finType, String type) {
		logger.debug("Entering");
		FinTypeVASProducts finTypeVASProducts = new FinTypeVASProducts();
		finTypeVASProducts.setFinType(finType);
		StringBuilder deleteSql = new StringBuilder("Delete From ");
		deleteSql.append(" FinTypeVASProducts");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinType =:FinType ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeVASProducts);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

}
