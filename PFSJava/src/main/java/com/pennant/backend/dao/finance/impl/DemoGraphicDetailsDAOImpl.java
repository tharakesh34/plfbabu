package com.pennant.backend.dao.finance.impl;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.DemoGraphicDetailsDAO;
import com.pennant.backend.model.finance.DemographicDetails;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class DemoGraphicDetailsDAOImpl extends BasicDao<DemographicDetails> implements DemoGraphicDetailsDAO {

	private static Logger logger = Logger.getLogger(DemoGraphicDetailsDAOImpl.class);

	public DemoGraphicDetailsDAOImpl() {
	}

	/**
	 * Method for fetch DemoGraphic Details of corresponding pinCode
	 * 
	 * @param pinCode
	 * 
	 * @return DemographicDetails
	 */

	@Override
	public DemographicDetails getPinCodeDetail(String pinCode) {
		logger.debug("Entering");

		logger.debug(Literal.ENTERING);
		DemographicDetails demographicDetails = new DemographicDetails();
		demographicDetails.setPinCode(pinCode);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("select pin.pincodeid pinCodeId,pin.pincode,city.pccityname City,");
		selectSql.append(" stat.cpprovincename State from PINCODES pin");
		selectSql.append(" left join rmtprovincevscity city on pin.city=city.pccity left join");
		selectSql.append(" RMTCountryVsProvince stat on stat.cpprovince=city.pcprovince where pin.pincode=:pinCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(demographicDetails);
		RowMapper<DemographicDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(DemographicDetails.class);

		try {
			demographicDetails = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			demographicDetails = null;
		}
		logger.debug(Literal.LEAVING);
		return demographicDetails;

	}

}
