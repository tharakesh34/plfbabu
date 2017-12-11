package com.pennant.backend.service.licencefileupload.dao.impl;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.licencefileupload.LicenceFile;
import com.pennant.backend.service.licencefileupload.dao.LicenceFileDAO;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods declaration for the <b>LicenceFileUpload model</b> class.<br>
 * 
 */
public class LicenceFileDAOImpl extends BasisNextidDaoImpl<LicenceFile> implements LicenceFileDAO{
	private static Logger logger = Logger.getLogger(LicenceFileDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public LicenceFileDAOImpl() {
		super();
	}
	
	@Override
	public boolean save(LicenceFile licenceFileUpload){
		logger.debug(Literal.ENTERING);
		StringBuilder insertSql = new StringBuilder("Insert Into LicenceFile" );
		insertSql.append(" (FileID, Name, Data, DateTime, Active)" );
		insertSql.append(" Values(:FileID, :Name, :Data, :DateTime, :Active)" );
		if (licenceFileUpload.getId() == Long.MIN_VALUE || licenceFileUpload.getId() == 0) {
			licenceFileUpload.setId(getNextidviewDAO().getNextId("SeqLicenceFileUpload"));
		}
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(licenceFileUpload);
		Integer count = namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		boolean insert = false;
		if (count > 0) {
			insert = true;
		}
		logger.debug(Literal.LEAVING);
		return insert;
	}
	
	@Override
	public void update(long fileID, boolean active){
		logger.debug(Literal.ENTERING);
		LicenceFile licenceFileUpload = new LicenceFile();
		licenceFileUpload.setFileID(fileID);
		licenceFileUpload.setActive(active);
		StringBuilder updateSql = new StringBuilder("Update LicenceFile" );
		updateSql.append(" Set Active = :Active ");
		updateSql.append(" Where FileID !=:FileID");
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(licenceFileUpload);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
	}
	
	@Override
	public LicenceFile getActiveLicenceFileUpload(boolean active){
		logger.debug(Literal.ENTERING);
		LicenceFile licenceFileUpload = new LicenceFile();
		licenceFileUpload.setActive(active);
		
		StringBuilder selectSql = new StringBuilder("SELECT fileID, name, data, dateTime, active ");
		selectSql.append(" From LicenceFile");
		selectSql.append(" Where Active =:Active " );
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(licenceFileUpload);
		RowMapper<LicenceFile> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(LicenceFile.class);
		try{
			licenceFileUpload = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			licenceFileUpload = null;
		}
		logger.debug(Literal.LEAVING);
		return licenceFileUpload;
	}

	public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
		return namedParameterJdbcTemplate;
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
}