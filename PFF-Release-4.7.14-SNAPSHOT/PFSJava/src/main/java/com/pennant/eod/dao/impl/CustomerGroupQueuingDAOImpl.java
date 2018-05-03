package com.pennant.eod.dao.impl;

import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.customerqueuing.CustomerGroupQueuing;
import com.pennant.backend.model.customerqueuing.CustomerQueuing;
import com.pennant.eod.constants.EodConstants;
import com.pennant.eod.dao.CustomerGroupQueuingDAO;

public class CustomerGroupQueuingDAOImpl implements CustomerGroupQueuingDAO {

	private static Logger				logger			= Logger.getLogger(CustomerQueuingDAOImpl.class);
	
	private static final String			START_GRPID_RC	= "UPDATE CustomerGroupQueuing set Progress=:Progress ,StartTime = :StartTime "
			+ "Where GroupId = :GroupId AND Progress= :ProgressWait";

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public CustomerGroupQueuingDAOImpl() {
		super();
	}

	@Override
	public void delete() {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Delete from CustomerGroupQueuing");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new CustomerQueuing());
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public void logCustomerGroupQueuing() {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder insertSql = new StringBuilder("INSERT INTO CustomerGroupQueuing_Log ");
		insertSql.append(" SELECT * FROM CustomerGroupQueuing ");

		logger.debug("updateSql: " + insertSql.toString());

		this.namedParameterJdbcTemplate.update(insertSql.toString(), source);

		logger.debug("Leaving");
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public int prepareCustomerGroupQueue() {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Progress", EodConstants.PROGRESS_WAIT);
		source.addValue("EodDate", DateUtility.getAppValueDate());
		source.addValue("StartTime", DateUtility.getAppValueDate());
		source.addValue("EodProcess", true);

		StringBuilder insertSql = new StringBuilder("INSERT INTO CustomerGroupQueuing (GroupId, EodDate, StartTime, Progress, EodProcess)");
		insertSql.append(" Select DISTINCT CustomerGroup, :EodDate, :StartTime, :Progress, :EodProcess From LimitHeader T1");
		insertSql.append(" Inner Join LIMITSTRUCTURE T2 on T1.LimitStructureCode = T2.StructureCode");
		insertSql.append(" Where T1.CustomerGroup <> 0 And T2.Rebuild = '1'");

		logger.debug("insertSql: " + insertSql.toString());

		int count = this.namedParameterJdbcTemplate.update(insertSql.toString(), source);

		logger.debug("Leaving");

		return count;
	}
	
	@Override
	public void updateProgress(CustomerGroupQueuing customerGroupQueuing) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update CustomerGroupQueuing");
		updateSql.append(" Set Progress = :Progress");
		updateSql.append(" Where GroupId = :GroupId");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerGroupQueuing);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}
	
	@Override
	public void updateStatus(long groupID, int progress) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("GroupId", groupID);
		source.addValue("Progress", progress);
		source.addValue("EndTime", DateUtility.getSysDate());

		StringBuilder updateSql = new StringBuilder("Update CustomerGroupQueuing set");
		updateSql.append(" EndTime = :EndTime, Progress = :Progress");
		updateSql.append(" Where GroupId = :GroupId ");
		logger.debug("updateSql: " + updateSql.toString());

		this.namedParameterJdbcTemplate.update(updateSql.toString(), source);
		logger.debug("Leaving");
	}
	
	@Override
	public void updateFailed(CustomerGroupQueuing customerGroupQueuing) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update CustomerGroupQueuing set");
		updateSql.append(" EndTime = :EndTime,");
		updateSql.append(" Progress = :Progress Where GroupId = :GroupId");
		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerGroupQueuing);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public int startEODForGroupId(long groupID) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("GroupId", groupID);
		source.addValue("StartTime", DateUtility.getSysDate());
		source.addValue("ProgressWait", EodConstants.PROGRESS_WAIT);
		source.addValue("Progress", EodConstants.PROGRESS_IN_PROCESS);

		try {
			logger.debug("selectSql: " + START_GRPID_RC);
			return this.namedParameterJdbcTemplate.update(START_GRPID_RC, source);
		} catch (EmptyResultDataAccessException dae) {
			logger.error("Exception: ", dae);
		}

		logger.debug("Leaving");
		return 0;

	}

	@Override
	public List<CustomerGroupQueuing> getCustomerGroupsList() {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuilder selectSql = new StringBuilder();
		
		source.addValue("Progress", EodConstants.PROGRESS_WAIT);

		selectSql.append(" Select GroupId, EodDate, StartTime, EndTime, Progress, ErrorLog, Status, EodProcess from CustomerGroupQueuing");
		selectSql.append(" Where Progress = :Progress ");
		
		RowMapper<CustomerGroupQueuing> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerGroupQueuing.class);

		logger.debug("selectSql: " + selectSql.toString());

		List<CustomerGroupQueuing> customerGroupQueueingList = null;
		try {
			customerGroupQueueingList =  this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (Exception dae) {
			logger.error("Exception: ", dae);
		}

		logger.debug("Leaving");

		return customerGroupQueueingList;
	}

	@Override
	public int getCustomerGroupsCount(Date eodDate) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("EodDate", eodDate);
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append(" Select Count(EodDate) from CustomerGroupQueuing");
		selectSql.append(" where EodDate = :EodDate");
		
		logger.debug("selectSql: " + selectSql.toString());
		
		int count =  0;
		try {
			count =  this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (Exception dae) {
			count =  0;
		}
		
		logger.debug("Leaving");
		
		return count;
	}

	/**
	 * Insert into CustomerGroupQueuing for Customer Group Rebuild
	 */
	@Override
	public void insertCustGrpQueueForRebuild(CustomerGroupQueuing custGrpQueuing) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("INSERT INTO CustomerGroupQueuing (GroupId, EodDate, StartTime, Progress, EodProcess)");
		insertSql.append(" values (:GroupId, :EodDate, :StartTime, :Progress, :EodProcess)");

		logger.debug("updateSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(custGrpQueuing);

		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Count by Group ID
	 */
	@Override
	public int getCountByGrpId(long groupId) {
		logger.debug("Entering");

		CustomerGroupQueuing custGrpQueuing = new CustomerGroupQueuing();
		custGrpQueuing.setGroupId(groupId);

		StringBuilder selectSql = new StringBuilder("SELECT COALESCE(Count(GroupId), 0) from CustomerGroupQueuing ");
		selectSql.append(" Where GroupId = :GroupId");
		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(custGrpQueuing);
		int count = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);

		logger.debug("Leaving");
		return count;
	}

	/**
	 * insert into CustomerGroupQueuing_Log after Customer Group Rebuild
	 */
	@Override
	public void logCustomerGroupQueuingByGrpId(long groupId) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("GroupId", groupId);

		StringBuilder insertSql = new StringBuilder("INSERT INTO CustomerGroupQueuing_Log ");
		insertSql.append(" SELECT * FROM CustomerGroupQueuing Where GroupId = :GroupId");
		logger.debug("updateSql: " + insertSql.toString());

		this.namedParameterJdbcTemplate.update(insertSql.toString(), source);
		logger.debug("Leaving");
	}

	/**
	 * delete from CustomerGroupQueuingafter Customer GRoup Rebuild
	 */
	@Override
	public void deleteByGrpId(long groupId) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("GroupId", groupId);

		StringBuilder deleteSql = new StringBuilder("Delete From CustomerGroupQueuing Where GroupId = :GroupId");
		logger.debug("deleteSql: " + deleteSql.toString());

		this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);
		logger.debug("Leaving");
	}
}
