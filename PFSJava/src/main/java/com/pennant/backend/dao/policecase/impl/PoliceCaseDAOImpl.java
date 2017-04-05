package com.pennant.backend.dao.policecase.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.policecase.PoliceCaseDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.PoliceCaseDetail;
import com.pennant.backend.model.policecase.PoliceCase;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class PoliceCaseDAOImpl extends BasisCodeDAO<PoliceCaseDetail> implements PoliceCaseDAO {
	private static Logger logger = Logger.getLogger(PoliceCaseDAOImpl.class);
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public PoliceCaseDAOImpl() {
		super();
	}
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	public void saveList(List<PoliceCase> policeCase,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder("Insert Into FinPoliceCaseDetail");
	 	insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append("(FinReference, CustCIF, CustFName, CustLName , CustDOB, CustCRCPR, CustPassportNo ,MobileNumber, ");
		insertSql.append(" CustNationality, CustProduct, PoliceCaseRule, Override, OverrideUser ) ");
		insertSql.append(" Values(:FinReference, :CustCIF , :CustFName , :CustLName , :CustDOB ,  :CustCRCPR , :CustPassportNo ,");
		insertSql.append(" :MobileNumber , :CustNationality , :CustProduct , :PoliceCaseRule, :Override , :OverrideUser)");
		
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils
		        .createBatch(policeCase.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");

	}

	@Override
	public List<PoliceCase> fetchPoliceCase(String finReference, String queryCode) {
		logger.debug("Entering");
	
		PoliceCaseDetail policeCase = new PoliceCaseDetail();
		policeCase.setFinReference(finReference);
		policeCase.setPoliceCaseRule(queryCode);

		StringBuilder selectSql = new StringBuilder(" Select FinReference , CustCIF , CustFName , CustLName , ");
		selectSql.append(" CustDOB , CustCRCPR ,CustPassportNo , MobileNumber , CustNationality , ");
		selectSql.append(" CustProduct , PoliceCaseRule , Override , OverrideUser ");
		selectSql.append(" From FinPoliceCaseDetail"); 
		selectSql.append(" Where FinReference =:FinReference AND PoliceCaseRule LIKE('%"+queryCode+"%')");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(policeCase);
		RowMapper<PoliceCase> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PoliceCase.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		
		

	}
	@Override
    public List<PoliceCase> fetchFinPoliceCase(String finReference) {
		logger.debug("Entering");
		
		PoliceCase policeCase = new PoliceCase();
		policeCase.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(" Select FinReference , CustCIF , CustFName , CustLName , ");
		selectSql.append(" CustDOB , CustCRCPR ,CustPassportNo , MobileNumber , CustNationality , ");
		selectSql.append(" CustProduct , PoliceCaseRule , Override , OverrideUser ");
		selectSql.append(" From FinPoliceCaseDetail");
		selectSql.append(" Where FinReference =:FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(policeCase);
		RowMapper<PoliceCase> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PoliceCase.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
    }
	@Override
	public List<PoliceCaseDetail> fetchCorePolice(PoliceCaseDetail policecase, String sqlQuery) {
		logger.debug("Entering");
		
		List<PoliceCaseDetail> policeCases = null;
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT * FROM PoliceCaseCustomers ");
		selectSql.append(StringUtils.trimToEmpty(sqlQuery));

		logger.debug("selectSql: " +  selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(policecase);
		ParameterizedBeanPropertyRowMapper<PoliceCaseDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(PoliceCaseDetail.class);

		try{
			policeCases = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			policecase = null;
		}
		logger.debug("Leaving");
		return policeCases;
	  }
		@Override
	    public void deleteList(String finReference) {
			logger.debug("Entering");
			
			PoliceCaseDetail policeCase = new PoliceCaseDetail();
			policeCase.setFinReference(finReference);
			
			StringBuilder deleteSql = new StringBuilder("Delete From FinPoliceCaseDetail");
			deleteSql.append(" Where FinReference =:FinReference");
			logger.debug("deleteSql: " + deleteSql.toString());

			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(policeCase);
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			logger.debug("Leaving");
	    }



	@Override
	public void updatePoliceCaseList(List<PoliceCase> policeCase) {
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update FinPoliceCaseDetail");
		updateSql.append(" Set CustFName=:CustFName , CustLName=:CustLName , " );
		updateSql.append(" CustDOB=:CustDOB , CustCRCPR=:CustCRCPR , CustPassportNo=:CustPassportNo , MobileNumber=:MobileNumber , " );
		updateSql.append(" CustNationality=:CustNationality , CustProduct=:CustProduct , PoliceCaseRule=:PoliceCaseRule ,Override=:Override, " );
		updateSql.append(" OverrideUser=:OverrideUser " );
		updateSql.append(" WHERE FinReference=:FinReference AND CustCIF=:CustCIF ");
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils
		        .createBatch(policeCase.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	@Override
	public PoliceCaseDetail getPoliceCaseDetailById(String id, String type) {
		logger.debug("Entering");
		
		PoliceCaseDetail policeCaseDetail= new PoliceCaseDetail();
		policeCaseDetail.setId(id);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select CustCIF , CustFName , CustLName , ");
		selectSql.append(" CustDOB , CustCRCPR ,CustPassportNo , MobileNumber , CustNationality ,CustProduct, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if("_View".equals(type)){
			selectSql.append(", lovDescNationalityDesc ");
		}
		selectSql.append(" From PoliceCaseCustomers" + StringUtils.trimToEmpty(type));
		
		selectSql.append(" Where CustCIF =:CustCIF ");
		logger.debug("selectSql: " + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(policeCaseDetail);
		RowMapper<PoliceCaseDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PoliceCaseDetail.class);

		try {
			policeCaseDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			policeCaseDetail = null;
		}
		logger.debug("Leaving");
		return policeCaseDetail;
	}
	@SuppressWarnings("serial")
	@Override
	public void update(PoliceCaseDetail policeCaseDetail, String type) {
		logger.debug("Entering");
		
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();
		
		updateSql.append("Update PoliceCaseCustomers");
		updateSql.append(StringUtils.trimToEmpty(type));
		
		updateSql.append(" Set CustFName=:CustFName , CustLName=:CustLName , " );
		updateSql.append(" CustDOB=:CustDOB , CustCRCPR=:CustCRCPR , CustPassportNo=:CustPassportNo , MobileNumber=:MobileNumber , CustNationality=:CustNationality, CustProduct=:CustProduct, " );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" WHERE CustCIF=:CustCIF ");
		
		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(policeCaseDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);

			ErrorDetails errorDetails = getError("41003",policeCaseDetail.getCustCIF(), policeCaseDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}
	@SuppressWarnings("serial")
	@Override
	public void delete(PoliceCaseDetail policeCaseDetail, String type) {
		logger.debug("Entering");
		
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append("Delete From PoliceCaseCustomers");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustCIF =:CustCIF");

		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(policeCaseDetail);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41004",policeCaseDetail.getCustCIF(), 
						policeCaseDetail.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
			ErrorDetails errorDetails = getError("41006",policeCaseDetail.getCustCIF(), 
					policeCaseDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");

	}
	@Override
	public String save(PoliceCaseDetail policeCaseDetail, String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder("Insert Into PoliceCaseCustomers");
		insertSql.append(StringUtils.trimToEmpty(type));
		
		insertSql.append("(CustCIF, CustFName, CustLName , CustDOB, CustCRCPR, CustPassportNo ,MobileNumber,CustNationality, CustProduct,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:CustCIF , :CustFName , :CustLName , :CustDOB ,  :CustCRCPR , :CustPassportNo ,:MobileNumber , :CustNationality ,:CustProduct, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(policeCaseDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return policeCaseDetail.getId();
	}
	
	private ErrorDetails  getError(String errorId, String custCIF, String userLanguage){
		String[][] parms= new String[2][1]; 
		parms[1][0] = String.valueOf(custCIF);
		parms[0][0] = PennantJavaUtil.getLabel("label_custCIF")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

	@Override
    public void moveData(String finReference, String suffix) {

		logger.debug(" Entering ");
		try {
	        if (StringUtils.isBlank(suffix)) {
	            return;
	        }
	        
	        MapSqlParameterSource map=new MapSqlParameterSource();
	        map.addValue("FinReference", finReference);
	        
	        StringBuilder selectSql = new StringBuilder();
	        selectSql.append(" SELECT * FROM FinPoliceCaseDetail");
	        selectSql.append(" WHERE FinReference = :FinReference ");
	        
	        RowMapper<PoliceCase> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PoliceCase.class);
	        List<PoliceCase> list = this.namedParameterJdbcTemplate.query(selectSql.toString(), map,typeRowMapper);
	        
	        if (list!=null && !list.isEmpty()) {
	        	saveList(list,suffix);
            }
	        
        } catch (DataAccessException e) {
	     logger.debug(e);
        }
	    logger.debug(" Leaving ");
    
	    
    }
}
