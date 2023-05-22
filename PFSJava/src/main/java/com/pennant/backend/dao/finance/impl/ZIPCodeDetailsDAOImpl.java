package com.pennant.backend.dao.finance.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.ZIPCodeDetailsDAO;
import com.pennant.backend.model.finance.ZIPCodeDetails;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class ZIPCodeDetailsDAOImpl extends BasicDao<ZIPCodeDetails> implements ZIPCodeDetailsDAO {

	private static Logger logger = LogManager.getLogger(ZIPCodeDetailsDAOImpl.class);

	public ZIPCodeDetailsDAOImpl() {
	    super();
	}

	/**
	 * Method for fetch ZIPCode Details of corresponding pinCode
	 * 
	 * @param pinCode
	 * 
	 * @return ZIPCodeDetails
	 */

	@Override
	public ZIPCodeDetails getPinCodeDetail(String pinCode) {
		logger.debug("Entering");

		logger.debug(Literal.ENTERING);
		ZIPCodeDetails zIPCodeDetails = new ZIPCodeDetails();
		zIPCodeDetails.setPinCode(pinCode);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("select pin.pincodeid pinCodeId,pin.pincode,city.pccityname City,");
		selectSql.append(" stat.cpprovincename State from PINCODES pin");
		selectSql.append(" left join rmtprovincevscity city on pin.city=city.pccity left join");
		selectSql.append(" RMTCountryVsProvince stat on stat.cpprovince=city.pcprovince where pin.pincode=:pinCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(zIPCodeDetails);
		RowMapper<ZIPCodeDetails> typeRowMapper = BeanPropertyRowMapper.newInstance(ZIPCodeDetails.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
}
