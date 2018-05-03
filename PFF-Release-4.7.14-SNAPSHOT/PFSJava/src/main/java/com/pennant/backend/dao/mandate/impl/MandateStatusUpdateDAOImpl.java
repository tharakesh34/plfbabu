package com.pennant.backend.dao.mandate.impl;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.mandate.MandateStatusUpdateDAO;
import com.pennant.backend.model.mandate.MandateStatusUpdate;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

public class MandateStatusUpdateDAOImpl extends BasisNextidDaoImpl<MandateStatusUpdate> implements MandateStatusUpdateDAO {

	private static Logger logger = Logger.getLogger(MandateStatusUpdateDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public MandateStatusUpdateDAOImpl(){
		super();
	}
	
	/**
	 * Fetch the Record  MandateStatus details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return FileUpload
	 */
	@Override
	public MandateStatusUpdate getFileUploadById(final long id, String type ) {
		logger.debug("Entering");
		MandateStatusUpdate mandateStatusUpdate = new MandateStatusUpdate();
		mandateStatusUpdate.setId(id);
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" fileID,fileName,userId,startDate,endDate,totalCount,success,fail,remarks");
		
		if(type.contains("View")){
			sql.append("");
		}	
		sql.append(" From MandateStatusUpdate");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FileID =:FileID");
		
		logger.debug("sql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandateStatusUpdate);
		RowMapper<MandateStatusUpdate> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(MandateStatusUpdate.class);
		
		try{
			mandateStatusUpdate = this.namedParameterJdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			mandateStatusUpdate = null;
		}
		logger.debug("Leaving");
		return mandateStatusUpdate;
	}
	
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the FileUpload or FileUpload_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete FileUpload by key FileHeaderId
	 * 
	 * @param MandateStatusUpdate (mandateStatusUpdate)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(MandateStatusUpdate mandateStatusUpdate,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder sql = new StringBuilder("Delete From MandateStatusUpdate");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FileID =:FileID");
	
		logger.debug("sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandateStatusUpdate);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(sql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into FileUpload or FileUpload_Temp.
	 * it fetches the available Sequence form SeqMandatesStatus by using getNextidviewDAO().getNextId() method.  
	 *
	 * save FileUpload 
	 * 
	 * @param MandateStatusUpdate (fileUpload)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public long save(MandateStatusUpdate mandateStatusUpdate,String type) {
		logger.debug("Entering");
		if (mandateStatusUpdate.getId()==Long.MIN_VALUE){
			mandateStatusUpdate.setId(getNextidviewDAO().getNextId("SeqMandateStatusUpdate"));
			logger.debug("get NextID:"+mandateStatusUpdate.getId());
		}
		
		StringBuilder sql =new StringBuilder("Insert Into MandateStatusUpdate ");
		sql.append(StringUtils.trimToEmpty(type)+" (");
		sql.append(" fileID,filename,userId,startDate,endDate,totalCount,success,fail,remarks)");
		sql.append(" Values(:fileID,:fileName,:userId,:startDate,:endDate,:totalCount,:success,:fail,:remarks)");
		
		logger.debug("sql: " + sql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandateStatusUpdate);
		this.namedParameterJdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug("Leaving");
		return mandateStatusUpdate.getId();
	}
	
	/**
	 * This method updates the Record FileUpload or FileUpload_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update FileUpload by key MandateID and Version
	 * 
	 * @param MandateStatusUpdate (fileUpload)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public void update(MandateStatusUpdate mandateStatusUpdate,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	sql =new StringBuilder("Update MandateStatusUpdate");
		sql.append(StringUtils.trimToEmpty(type)+" Set"); 
		sql.append(" fileName=:fileName,userId=:userId,");
		sql.append(" startDate=:startDate,endDate=:endDate,totalCount=:totalCount,success=:success,fail=:fail,remarks=:remarks");
		sql.append(" Where fileID =:fileID");
		
		/*if (!type.endsWith("_Temp")){
			sql.append("  AND Version= :Version-1");
		}*/
		
		logger.debug("Sql: " + sql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandateStatusUpdate);
		recordCount = this.namedParameterJdbcTemplate.update(sql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}


	@Override
	public int getFileCount(String fileName) {
		logger.debug("Entering");
		MandateStatusUpdate mandateStatusUpdate = new MandateStatusUpdate();
		mandateStatusUpdate.setFileName(fileName);
		StringBuilder sql = new StringBuilder("SELECT COUNT(*)");
		
		sql.append(" From MandateStatusUpdate");
		sql.append(" Where FileName =:FileName");
		
		logger.debug("sql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandateStatusUpdate);
		
		logger.debug("Leaving");
		
			try {
				return this.namedParameterJdbcTemplate.queryForObject(sql.toString(), beanParameters, Integer.class);	
			} catch(EmptyResultDataAccessException dae) {
				logger.debug("Exception: ", dae);
				return 0;
			}
	}
	
	
}