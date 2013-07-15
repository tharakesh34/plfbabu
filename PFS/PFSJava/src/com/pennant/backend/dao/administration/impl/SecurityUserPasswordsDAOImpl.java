package com.pennant.backend.dao.administration.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.administration.SecurityUserPasswordsDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class SecurityUserPasswordsDAOImpl extends BasisNextidDaoImpl<SecurityUser> implements  SecurityUserPasswordsDAO{
	
	private static Logger logger = Logger.getLogger(SecurityUserPasswordsDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
 
	/**
	 * This method inserts record into SecUserPasswords
	 */
	public long save(SecurityUser securityUser) {
		logger.debug("Entering ");

		StringBuilder   insertSql  = new StringBuilder  ("Insert Into SecUserPasswords");
		insertSql .append(" (UsrID, UsrPwd,UsrToken,LastMntOn) Values(:UsrID,:UsrPwd,:UsrToken,:LastMntOn)");
		logger.debug("saveRecentPasswordSql:"+insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityUser);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving ");
		return securityUser.getId();
	}
	/**
	 * This method selects the List of records from SecUserPasswords as SecurityUser Objects 
	 */

	public List<SecurityUser> getUserPreviousPasswords(SecurityUser secUser){
		logger.debug("Entering ");
		
		StringBuilder  selectSql = new StringBuilder ();
		selectSql.append(" SELECT  UsrID, UsrPwd,UsrToken,LastMntOn " );
		selectSql.append(" FROM SecUserPasswords " );
		selectSql.append(" where UsrID=:UsrID order by LastMntOn DESC ");
		
		logger.debug("selectUserRecentPasswordsSql : " + selectSql.toString());      
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secUser);
		RowMapper< SecurityUser> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				SecurityUser.class);
		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
	}
	
	/**
	 * This method  deletes the record from SecUserPasswords with specific condition
	 */
	@SuppressWarnings("serial")
	public void delete(SecurityUser securityUser) {
		logger.debug("Entering ");

		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder ("Delete From SecUserPasswords " );
		deleteSql.append(" where LastMntOn = (select min(LastMntOn) from SecUserPasswords " );
		deleteSql.append(" where UsrID =:UsrID) and UsrID =:UsrID ");	
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityUser);
		logger.debug("deleteOldestPasswordSql:"+deleteSql.toString());

		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError ("41003",securityUser.getUsrID() , 
						securityUser.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.debug("Error deleteOldestPassword() Method");
			ErrorDetails errorDetails= getError ("41006",securityUser.getUsrID() , 
					securityUser.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving ");
	}

	private ErrorDetails  getError(String errorId, long userId, String userLanguage){
		String[][] parms= new String[2][1]; 
		parms[1][0] = String.valueOf(userId);
		parms[0][0] = PennantJavaUtil.getLabel("label_UsrID")+":" +parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
				errorId, parms[0],parms[1]), userLanguage);
	}

}
