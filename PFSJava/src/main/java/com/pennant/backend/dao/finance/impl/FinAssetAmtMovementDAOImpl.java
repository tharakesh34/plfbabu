package com.pennant.backend.dao.finance.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.FinAssetAmtMovementDAO;
import com.pennant.backend.model.finance.FinAssetAmtMovement;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

public class FinAssetAmtMovementDAOImpl extends SequenceDao<FinAssetAmtMovement> implements FinAssetAmtMovementDAO {

	private static Logger logger = Logger.getLogger(FinAssetAmtMovementDAOImpl.class);

	public FinAssetAmtMovementDAOImpl() {
		super();
	}

	/**
	 * Method for retrieving the Sanction Amount Movements
	 */
	@Override
	public List<FinAssetAmtMovement> getFinAssetAmtMovements(String finReference, String movementType) {

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select FinServiceInstID, FinReference, MovementDate, MovementOrder, ");
		selectSql.append(" MovementType, MovementAmount, AvailableAmt, SanctionedAmt, RevisedSanctionedAmt, ");
		selectSql.append(" DisbursedAmt, LastMntBy, LastMntOn ");
		selectSql.append(" From FinAssetAmtMovements ");
		selectSql.append(" Where FinReference = :FinReference And MovementType = :MovementType ");
		selectSql.append(" Order by FinServiceInstID Desc ");

		logger.debug("selectSql: " + selectSql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("MovementType", movementType);

		RowMapper<FinAssetAmtMovement> typeRowMapper = BeanPropertyRowMapper.newInstance(FinAssetAmtMovement.class);

		return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	/**
	 * Method for saving Fee schedule Details list
	 */
	@Override
	public void saveFinAssetAmtMovement(FinAssetAmtMovement assetAmtMovt) {

		StringBuilder insertSql = new StringBuilder(" Insert Into FinAssetAmtMovements");
		insertSql.append(" (FinServiceInstID, FinReference, MovementDate, MovementOrder, ");
		insertSql.append(" MovementType, MovementAmount, AvailableAmt, SanctionedAmt, RevisedSanctionedAmt, ");
		insertSql.append(" DisbursedAmt, LastMntBy, LastMntOn) ");
		insertSql.append(" Values (:FinServiceInstID, :FinReference, :MovementDate, :MovementOrder, ");
		insertSql.append(" :MovementType, :MovementAmount, :AvailableAmt, :SanctionedAmt, :RevisedSanctionedAmt, ");
		insertSql.append(" :DisbursedAmt, :LastMntBy, :LastMntOn) ");

		logger.debug("insertSql : " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(assetAmtMovt);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
	}
}
