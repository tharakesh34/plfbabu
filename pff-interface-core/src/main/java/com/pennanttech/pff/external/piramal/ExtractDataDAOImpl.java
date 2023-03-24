package com.pennanttech.pff.external.piramal;

import java.sql.Timestamp;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.util.CollectionUtils;

import com.pennanttech.pennapps.core.resource.Literal;

public class ExtractDataDAOImpl implements ExtractDataDAO {
	private static Logger logger = LogManager.getLogger(ExtractDataDAOImpl.class);
	private static String SELECT_QUERY = "select * from tableName where  LastMntOn >=:LastMntOn";
	private static String DELETE_QUERY = "Delete from tableName";

	private NamedParameterJdbcTemplate mainNamedJdbcTemplate;
	private NamedParameterJdbcTemplate extNamedJdbcTemplate;

	@Override
	public boolean extractDetails(Timestamp date, Class<?> beanType, String tableName) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource source = new MapSqlParameterSource();
		try {
			//delete the data first
			StringBuilder delete = new StringBuilder(DELETE_QUERY.replace("tableName", tableName));
			this.extNamedJdbcTemplate.update(delete.toString(), source);
		} catch (DataAccessException e) {
			logger.error(Literal.EXCEPTION + " Error while deleting the data in ext tables");
		}

		try {
			//get the data from the table
			List<?> list = getDataByEODDate(date, beanType, tableName);
			if (!CollectionUtils.isEmpty(list)) {
				save(list, tableName);
			}
		} catch (DataAccessException e) {
			logger.error(Literal.EXCEPTION + " Error while retriving  the data in ext tables");
		}

		logger.debug(Literal.LEAVING);
		return true;
	}

	/**
	 * This method will save the data into the respective tables as a batch
	 * 
	 * @param externalPostingList
	 * @param module
	 */
	public void save(List<?> externalPostingList, String module) {
		logger.debug(Literal.ENTERING);
		SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(externalPostingList.toArray());
		try {
			int[] count = this.extNamedJdbcTemplate.batchUpdate(getQuery(module), params);
			logger.debug(count.length + " records are  saved in table: " + module);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + " while saving the data in ext table: " + module);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method will return the insert query
	 * 
	 * @param module
	 * @return
	 */
	private String getQuery(String module) {
		if ("Customers".equalsIgnoreCase(module)) {
			return StaticQueryUtil.CUSTOMER_INSERT_SQL;
		}

		if ("customerPhonenumbers".equalsIgnoreCase(module)) {
			return StaticQueryUtil.PHONE_NO_INSERT_SQL;
		}
		if ("customerEmails".equalsIgnoreCase(module)) {
			return StaticQueryUtil.EMAIL_INSERT_SQL;
		}

		if ("customerAddresses".equalsIgnoreCase(module)) {
			return StaticQueryUtil.ADDRESS_INSERT_SQL;
		}

		if ("buildergroup".equalsIgnoreCase(module)) {
			return StaticQueryUtil.BUILDER_GROUP_INSERT_SQL;
		}

		if ("buildercompany".equalsIgnoreCase(module)) {
			return StaticQueryUtil.BUILDER_COMPANY_INSERT_SQL;
		}
		if ("builderprojcet".equalsIgnoreCase(module)) {
			return StaticQueryUtil.BUILDER_PROJECT_INSERT_SQL;
		}

		if ("projectunits".equalsIgnoreCase(module)) {
			return StaticQueryUtil.PROJECT_UNITS_INSERT_SQL;
		}

		return null;
	}

	/**
	 * This method will get the data from the table and map the result set to the respective bean
	 * 
	 * @param date
	 * @param beanType
	 * @param tableName
	 * @return
	 */
	public List<?> getDataByEODDate(Timestamp date, Class<?> beanType, String tableName) {
		logger.debug(Literal.ENTERING);
		StringBuilder selectSQL = new StringBuilder(SELECT_QUERY.replace("tableName", tableName));
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("LastMntOn", date);
		RowMapper<?> typeRowMapper = BeanPropertyRowMapper.newInstance(beanType);
		logger.debug(Literal.LEAVING);
		try {
			return this.mainNamedJdbcTemplate.query(selectSQL.toString(), paramMap, typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + "  while reading the data from " + tableName);
		}
		return null;
	}

	public void setExtDataSource(DataSource extDataSource) {
		this.extNamedJdbcTemplate = new NamedParameterJdbcTemplate(extDataSource);
	}

	public void setMainDataSource(DataSource mainDataSource) {
		this.mainNamedJdbcTemplate = new NamedParameterJdbcTemplate(mainDataSource);
	}

}
