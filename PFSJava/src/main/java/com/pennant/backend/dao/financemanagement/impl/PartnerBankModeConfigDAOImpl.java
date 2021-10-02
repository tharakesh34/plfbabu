package com.pennant.backend.dao.financemanagement.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.financemanagement.PartnerBankModeConfigDAO;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class PartnerBankModeConfigDAOImpl extends SequenceDao<PartnerBank> implements PartnerBankModeConfigDAO {
	private static Logger logger = LogManager.getLogger(PartnerBankModeConfigDAOImpl.class);

	public String getConfigName(String mode, long partnerBankID, String type, String reqType) {
		StringBuilder sql = new StringBuilder("Select Config_Name From PARTNERBANKS_DATA_ENGINE");
		sql.append(" Where PayMode = ? and PartnerBankId = ? and Type = ? and RequestType = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			Object[] obj = new Object[] { mode, partnerBankID, type, reqType };
			return jdbcOperations.queryForObject(sql.toString(), String.class, obj);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	public String getConfigNameByMode(String mode, String type, String reqType) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select Config_Name from Partnerbanks_Data_Engine");
		sql.append(" Where PayMode = ? and Type = ? and RequestType = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), String.class, mode, type, reqType);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}
}
