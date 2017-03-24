package com.pennant.backend.dao.returnedCheques.impl;

import java.util.List;

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
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.returnedCheques.ReturnedChequeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.returnedcheques.ReturnedChequeDetails;
import com.pennant.backend.model.returnedcheques.ReturnedCheques;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class ReturnedChequeDAOImpl extends BasisCodeDAO<ReturnedChequeDetails> implements
        ReturnedChequeDAO {

	private static Logger logger = Logger.getLogger(ReturnedChequeDAOImpl.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public ReturnedChequeDAOImpl() {
		super();
	}
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	

	/**
	 * Fetch the Record ReturnedCheque details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return ReturnedCheque
	 */

	@Override
	public ReturnedChequeDetails getReturnedChequeById(String custCIF, String chequeNo, String type) {
		logger.debug("Entering");

		ReturnedChequeDetails returnCheque = new ReturnedChequeDetails();
		returnCheque.setCustCIF(custCIF);
		returnCheque.setChequeNo(chequeNo);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select CustCIF , ChequeNo , Amount ,ReturnDate,ReturnReason,Currency,");
		if (type.contains("View")) {
			selectSql.append("  CustShrtName,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode,  NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From ReturnedCheques");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustCIF =:CustCIF and ChequeNo =:ChequeNo ");
		logger.debug("selectSql:" + selectSql.toString());

		SqlParameterSource beanparameters = new BeanPropertySqlParameterSource(returnCheque);
		RowMapper<ReturnedChequeDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(ReturnedChequeDetails.class);

		try {
			returnCheque = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
			        beanparameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			returnCheque = null;
		}

		logger.debug("Leaving");
		return returnCheque;
	}

	@SuppressWarnings("serial")
	@Override
	public void update(ReturnedChequeDetails returnedChequeDetails, String type) {
		int recordCount = 0;
		logger.debug("Entering ");

		StringBuilder updateSql = new StringBuilder("Update ReturnedCheques");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CustCIF = :CustCIF, ChequeNo = :ChequeNo, Amount = :Amount,");
		updateSql
		        .append(" ReturnDate = :ReturnDate,ReturnReason = :ReturnReason,Currency = :Currency,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		updateSql
		        .append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql
		        .append(" Where CustCIF =:CustCIF and ChequeNo=:ChequeNo");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
		        returnedChequeDetails);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", returnedChequeDetails.getCustCIF(),
			        returnedChequeDetails.getChequeNo(), returnedChequeDetails.getUserDetails()
			                .getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving ");

	}

	/**
	 * This method Deletes the Record from the ReturnedCheque or ReturnedCheque_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete ReturnedCheque by key CustCIF,ChequeNO
	 * 
	 * @param ReturnedCheque
	 *            (returnedCheque)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(ReturnedChequeDetails returnedChequeDetails, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder(" Delete from ReturnedCheques");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustCIF =:CustCIF and ChequeNo =:ChequeNo");
		logger.debug("deleteSql:" + deleteSql.toString());
		SqlParameterSource beanParameterSource = new BeanPropertySqlParameterSource(
		        returnedChequeDetails);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),
			        beanParameterSource);
			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41003", returnedChequeDetails.getCustCIF(),
				        returnedChequeDetails.getChequeNo(), returnedChequeDetails.getUserDetails()
				                .getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);

			ErrorDetails errorDetails = getError("41006", returnedChequeDetails.getCustCIF(),
			        returnedChequeDetails.getChequeNo(), returnedChequeDetails.getUserDetails()
			                .getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into ReturnedCheque or ReturnedCheque_Temp.
	 * 
	 * save ReturnedCheque
	 * 
	 * @param ReturnedCheque
	 *            (returnedCheque)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void save(ReturnedChequeDetails returnedChequeDetails, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert into ReturnedCheques");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append("(CustCIF,ChequeNo,Amount,ReturnDate,ReturnReason,Currency,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CustCIF, :ChequeNo, :Amount, :ReturnDate,:ReturnReason,:Currency,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
		        returnedChequeDetails);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
	}
	
	@Override
	public List<ReturnedCheques> fetchReturnedCheques(ReturnedCheques returnedCheques) {
		logger.debug("Entering");

		ReturnedCheques returnedCheque = new ReturnedCheques();
		returnedCheque.setCustCIF(returnedCheques.getCustCIF());
		
		StringBuilder selectSql = new StringBuilder(" Select CustCIF , ChequeNo , Amount , ");
		selectSql.append(" ReturnDate , ReturnReason ,Currency ");
		selectSql.append(" From ReturnedCheques"); 
		selectSql.append(" Where CustCIF =:CustCIF ");
		logger.debug("selectSql: " + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(returnedCheque);
		RowMapper<ReturnedCheques> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ReturnedCheques.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	private ErrorDetails getError(String errorId, String custCIF, String chequeNo,
	        String userLanguage) {
		String[][] parms = new String[2][2];
		parms[1][0] = String.valueOf(custCIF);
		parms[1][1] = String.valueOf(chequeNo);
		parms[0][0] = PennantJavaUtil.getLabel("label_custCIF") + ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId,
		        parms[0], parms[1]), userLanguage);
	}
	

}
