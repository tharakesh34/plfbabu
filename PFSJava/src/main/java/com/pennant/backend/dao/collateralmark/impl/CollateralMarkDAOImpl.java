package com.pennant.backend.dao.collateralmark.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.collateralmark.CollateralMarkDAO;
import com.pennant.backend.model.collateral.FinCollateralMark;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

public class CollateralMarkDAOImpl extends SequenceDao<FinCollateralMark> implements CollateralMarkDAO {
   private static Logger logger = Logger.getLogger(CollateralMarkDAOImpl.class);

	
	public CollateralMarkDAOImpl() {
		super();
	}

	@Override
	public int save(FinCollateralMark finCollateralMark) {
		logger.debug("Entering ");
		
		if(finCollateralMark.getId()== 0 ||finCollateralMark.getId()==Long.MIN_VALUE){
			finCollateralMark.setFinCollateralId(getNextId("SeqCollateralMarkLog"));	
		}

		StringBuilder insertSql = new StringBuilder("Insert Into CollateralMarkLog" );
		insertSql.append(" (FinCollateralId, FinReference, ReferenceNum, Status, Reason, BranchCode, DepositID, InsAmount," );
		insertSql.append(" BlockingDate, ReturnCode, ReturnText, Processed)");
		insertSql.append(" Values(:FinCollateralId, :FinReference, :ReferenceNum, :Status, :Reason, :BranchCode, :DepositID, " );
		insertSql.append(" :InsAmount, :BlockingDate, :ReturnCode, :ReturnText, :Processed)" );

		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCollateralMark);
		logger.debug("Leaving ");
		try {
			return this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch(DataAccessException e) {
			logger.error("Exception: ", e);
			return 0;
		}
	}

	/**
	 * Method for Fetch Marked Deposit details
	 * 
	 * @param depositId
	 * @return FinCollateralMark
	 * 
	 */
	@Override
    public FinCollateralMark getCollateralById(String depositId) {
		logger.debug("Entering");

		FinCollateralMark finCollateralMark = new FinCollateralMark();
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("DepositID", depositId);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select FinCollateralId, FinReference, ReferenceNum, Status, Reason, BranchCode, DepositID, InsAmount,");
		selectSql.append(" BlockingDate, ReturnCode, ReturnText, Processed");
		selectSql.append(" From CollateralMarkLog ");
		selectSql.append(" Where DepositID =:DepositID");
		
		logger.debug("selectSql: " + selectSql.toString());
		
		RowMapper<FinCollateralMark> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinCollateralMark.class);

		try {
			finCollateralMark = this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			finCollateralMark = null;
		}
		
		logger.debug("Leaving");
		return finCollateralMark;
    }

	@Override
	public FinCollateralMark getCollatDeMarkStatus(String finReference, String markStatus) {
		logger.debug("Entering");

		FinCollateralMark finCollateralMark = new FinCollateralMark();
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("Status", markStatus);
		source.addValue("Processed", 1);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select FinCollateralId, FinReference, ReferenceNum, Status, Reason, BranchCode, DepositID, InsAmount,");
		selectSql.append(" BlockingDate, ReturnCode, ReturnText, Processed");
		selectSql.append(" From CollateralMarkLog ");
		selectSql.append(" Where FinReference =:FinReference AND Status =:Status AND Processed =:Processed");
		
		logger.debug("selectSql: " + selectSql.toString());
		
		RowMapper<FinCollateralMark> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinCollateralMark.class);

		try {
			finCollateralMark = this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			finCollateralMark = null;
		}
		
		logger.debug("Leaving");
		return finCollateralMark;
    }

	@Override
	public List<FinCollateralMark> getCollateralList(String finReference) {
		logger.debug("Entering");

		List<FinCollateralMark> finCollateralList = new ArrayList<FinCollateralMark>();
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("Status", "MARK");
		source.addValue("Processed", 1);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select FinCollateralId, FinReference, ReferenceNum, Status, Reason, BranchCode, DepositID, InsAmount,");
		selectSql.append(" BlockingDate, ReturnCode, ReturnText, Processed");
		selectSql.append(" From CollateralMarkLog ");
		selectSql.append(" Where FinReference =:FinReference AND Status =:Status AND Processed =:Processed");
		
		logger.debug("selectSql: " + selectSql.toString());

		try {
			finCollateralList = this.jdbcTemplate.queryForList(selectSql.toString(), source, FinCollateralMark.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			finCollateralList = null;
		}
		
		logger.debug("Leaving");
		return finCollateralList;
    }
	

}
