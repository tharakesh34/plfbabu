package com.pennant.backend.dao.applicationmaster.impl;

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

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.TargetDetailDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.TargetDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class TargetDetailDAOImpl extends BasisCodeDAO<TargetDetail> implements TargetDetailDAO {
	private static Logger logger = Logger.getLogger(TargetDetailDAOImpl.class);
	
	// Spring Named JDBC Template
		private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public TargetDetailDAOImpl() {
		super();
	}
	

	@Override
    public TargetDetail getTargetDetailById(String id, String type) {
		logger.debug("Entering");
		TargetDetail targetDetail = new TargetDetail();
		targetDetail.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("Select TargetCode, TargetDesc, Active,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From TargetDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where TargetCode =:TargetCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(targetDetail);
		RowMapper<TargetDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(TargetDetail.class);

		try {
			targetDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			targetDetail = null;
		}
		logger.debug("Leaving");
		return targetDetail;
    }

	@SuppressWarnings("serial")
	@Override
    public void update(TargetDetail targetDetail, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update TargetDetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set TargetCode = :TargetCode, TargetDesc = :TargetDesc, Active = :Active,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where TargetCode =:TargetCode");
		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(targetDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);

			ErrorDetails errorDetails = getError("41003",targetDetail.getTargetCode(), targetDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	    
    }

	@SuppressWarnings("serial")
	@Override
    public void delete(TargetDetail targetDetail, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();
		
		deleteSql.append("Delete From TargetDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where TargetCode =:TargetCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(targetDetail);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41004",targetDetail.getTargetCode(), 
						targetDetail.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
			ErrorDetails errorDetails = getError("41006",targetDetail.getTargetCode(), 
					targetDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
    }

	@Override
    public String save(TargetDetail targetDetail, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into TargetDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (TargetCode, TargetDesc, Active,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values (:TargetCode, :TargetDesc, :Active,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(targetDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return targetDetail.getId();
    }

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	private ErrorDetails  getError(String errorId, String targetCode, String userLanguage){
		String[][] parms= new String[2][1]; 
		parms[1][0] = targetCode;
		parms[0][0] = PennantJavaUtil.getLabel("label_TargetCode")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}
}
