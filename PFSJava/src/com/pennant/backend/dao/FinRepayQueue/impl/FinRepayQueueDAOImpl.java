package com.pennant.backend.dao.FinRepayQueue.impl;

import java.util.Date;
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
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.FinRepayQueue.FinRepayQueueDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class FinRepayQueueDAOImpl extends BasisCodeDAO<FinRepayQueue> implements FinRepayQueueDAO {

	private static Logger logger = Logger.getLogger(FinRepayQueueDAOImpl.class);
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	public String save(FinRepayQueue finRepayQueue, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into FinRpyQueue");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (RpyDate, FinPriority, FinType, FinReference, FinRpyFor, Branch, CustomerID,");
		insertSql.append(" SchdPft, SchdPri, SchdPftPaid, SchdPriPaid, SchdPftBal, SchdPriBal, SchdIsPftPaid, SchdIsPriPaid)");
		insertSql.append(" Values(:RpyDate, :FinPriority, :FinType, :FinReference, :FinRpyFor, :Branch, :CustomerID, ");
		insertSql.append(" :SchdPft, :SchdPri, :SchdPftPaid , :SchdPriPaid, :SchdPftBal, :SchdPriBal, :SchdIsPftPaid, :SchdIsPriPaid )");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finRepayQueue);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return finRepayQueue.getFinReference();
	}
	
	/**
	 * Method for Save Finance Repay Queue list
	 * @param finRepayQueueList
	 * @param type
	 */
	public void saveBatch(List<FinRepayQueue> finRepayQueueList, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into FinRpyQueue");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (RpyDate, FinPriority, FinType, FinReference, FinRpyFor, Branch, CustomerID,");
		insertSql.append(" SchdPft, SchdPri, SchdPftPaid, SchdPriPaid, SchdPftBal, SchdPriBal, SchdIsPftPaid, SchdIsPriPaid)");
		insertSql.append(" Values(:RpyDate, :FinPriority, :FinType, :FinReference, :FinRpyFor, :Branch, :CustomerID, ");
		insertSql.append(" :SchdPft, :SchdPri, :SchdPftPaid , :SchdPriPaid, :SchdPftBal, :SchdPriBal, :SchdIsPftPaid, :SchdIsPriPaid )");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(finRepayQueueList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public FinRepayQueue getFinRepayQueueById(FinRepayQueue finRepayQueue, String type) {
		logger.debug("Entering");
		StringBuilder selectSql = new StringBuilder(" Select ");
		selectSql.append(" RpyDate, FinPriority, FinType, FinReference, FinRpyFor, Branch, CustomerID, ");
		selectSql.append(" SchdPft, SchdPri, SchdPftPaid,");
		selectSql.append(" SchdPriPaid, SchdPftBal,SchdPriBal,");
		selectSql.append(" SchdIsPftPaid, SchdIsPriPaid From FinRpyQueue");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" where  RpyDate= :RpyDate ");
		selectSql.append(" AND FinRpyFor=:FinRpyFor and FinReference=:FinReference");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finRepayQueue);
		RowMapper<FinRepayQueue> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinRepayQueue.class);

		try {
			finRepayQueue = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finRepayQueue = null;
		}
		logger.debug("Leaving");
		return finRepayQueue;
	}

	@Override
	public void setFinRepayQueueRecords(List<FinRepayQueue> finRepayQueueList) {
		logger.debug("Entering");

		/*StringBuilder selectSql = new StringBuilder(" Select (FinReference+Convert(nvarchar,Convert(date,RpyDate))+FinRpyFor) From FinRpyQueue");
		selectSql.append(" where (FinReference+Convert(nvarchar,Convert(date,RpyDate))+FinRpyFor) IN(:RpyQueueKey) ");
		selectSql.append(" GROUP BY FinReference , RpyDate , FinRpyFor ");
		logger.debug("selectSql: " + selectSql.toString());

		Map<String , List<String>> params = new HashMap<String, List<String>>(1);
		List<String> valueList = new ArrayList<String>(finRepayQueueList.size());
		for (FinRepayQueue repayQueue : finRepayQueueList) {
			valueList.add(repayQueue.getFinReference()+ String.valueOf(DateUtility.getDBDate(repayQueue.getRpyDate().toString()))+repayQueue.getFinRpyFor());
		}
		params.put("RpyQueueKey", valueList);

		logger.debug("Leaving");
		List<String> resultList = this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), params, String.class);

		List<FinRepayQueue> saveQueueList = new ArrayList<FinRepayQueue>();
		List<FinRepayQueue> updateQueueList = new ArrayList<FinRepayQueue>();

		for (FinRepayQueue que : finRepayQueueList) {
			String key = que.getFinReference()+ String.valueOf(DateUtility.getDBDate(que.getRpyDate().toString()))+que.getFinRpyFor();
			if(resultList.contains(key)){
				updateQueueList.add(que);
			}else{
				saveQueueList.add(que);
			}
		}*/

		if(finRepayQueueList.size() > 0){
			saveBatch(finRepayQueueList, "");
		}

		/*if(updateQueueList.size() > 0){
			updateBatch(updateQueueList, "");
		}*/
		finRepayQueueList = null;
		//updateQueueList = null;
		//resultList = null;
	}

	@Override
	public List<FinRepayQueue> getFinRepayQueues(String finType, Date postDate, String type) {
		logger.debug("Entering");

		FinRepayQueue finRepayQueue = new FinRepayQueue();
		StringBuilder selectSql = new StringBuilder(" Select ");
		selectSql.append(" RpyDate, FinPriority, FinType, FinReference, Branch, CustomerID,");
		selectSql.append(" SchdPft, SchdPri, SchdPftPaid,");
		selectSql.append(" SchdPriPaid, SchdPftBal, SchdPriBal,");
		selectSql.append(" SchdIsPftPaid, SchdIsPriPaid From FinRpyQueue");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" where  RpyDate ='" + postDate + "' ");
		selectSql.append(" AND FinRpFor='" + finType + "'");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finRepayQueue);
		RowMapper<FinRepayQueue> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinRepayQueue.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@SuppressWarnings("serial")
	@Override
	public void update(FinRepayQueue finRepayQueue, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder(" UPDATE FinRpyQueue");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" SET RpyDate =:RpyDate, FinPriority =:FinPriority, ");
		updateSql.append(" SchdPft =:SchdPft, SchdPri =:SchdPri, SchdPftPaid =:SchdPftPaid, SchdPriPaid =:SchdPriPaid, ");
		updateSql.append(" SchdPftBal =:SchdPftBal, SchdPriBal =:SchdPriBal, SchdIsPftPaid =:SchdIsPftPaid, SchdIsPriPaid =:SchdIsPriPaid ");
		updateSql.append(" where  RpyDate= :RpyDate AND FinPriority =:FinPriority ");
		updateSql.append(" AND FinRpyFor=:FinRpyFor AND FinReference=:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finRepayQueue);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41003", finRepayQueue.getFinReference(), "");
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");

	}
	
	/**
	 * Method for Updation of Repay Queue list
	 * @param finRepayQueueList
	 * @param type
	 */
	public void updateBatch(List<FinRepayQueue> finRepayQueueList, String type) {
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder(" UPDATE FinRpyQueue");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" SET RpyDate =:RpyDate, FinPriority =:FinPriority, FinType =:FinType,");
		updateSql.append(" FinReference =:FinReference, FinRpyFor = :FinRpyFor, Branch =:Branch, CustomerID =:CustomerID,");
		updateSql.append(" SchdPft =:SchdPft, SchdPri =:SchdPri, SchdPftPaid =:SchdPftPaid, SchdPriPaid =:SchdPriPaid, ");
		updateSql.append(" SchdPftBal =:SchdPftBal, SchdPriBal =:SchdPriBal, SchdIsPftPaid =:SchdIsPftPaid, SchdIsPriPaid =:SchdIsPriPaid ");
		updateSql.append(" where  RpyDate= :RpyDate AND FinPriority =:FinPriority ");
		updateSql.append(" AND FinRpyFor=:FinRpyFor AND FinReference=:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(finRepayQueueList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public void initialize(FinRepayQueue finRepayQueue) {
		super.initialize(finRepayQueue);

	}
	
	@Override
    public void deleteRepayQueue() {
		logger.debug("Entering");
		StringBuilder deleteSql = new StringBuilder(" DELETE FROM FinRpyQueue");
		logger.debug("updateSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new FinRepayQueue());
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
    }

	/**
	 * This method for getting the error details
	 * 
	 * @param errorId
	 *            (String)
	 * @param Id
	 *            (String)
	 * @param userLanguage
	 *            (String)
	 * @return ErrorDetails
	 */
	private ErrorDetails getError(String errorId, String finref, String userLanguage) {
		String[][] parms = new String[2][2];
		parms[1][0] = finref;

		parms[0][0] = PennantJavaUtil.getLabel("label_CountryCode") + ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0], parms[1]), userLanguage);
	}

}
