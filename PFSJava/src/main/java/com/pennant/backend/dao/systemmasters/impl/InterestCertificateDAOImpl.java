/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  AddressTypeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.dao.systemmasters.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.systemmasters.InterestCertificateDAO;
import com.pennant.backend.model.agreement.InterestCertificate;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>AddressType model</b> class.<br>
 */
public class InterestCertificateDAOImpl extends BasicDao<InterestCertificate> implements InterestCertificateDAO {
	private static Logger logger = LogManager.getLogger(InterestCertificateDAOImpl.class);

	public InterestCertificateDAOImpl() {
		super();
	}

	@Override
	public InterestCertificate getInterestCertificateDetails(String finReference) throws ParseException {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT distinct FINREFERENCE, CUSTNAME, CUSTADDRHNBR, CUSTADDRSTREET, COUNTRYDESC, CUSTADDRSTATE,");
		sql.append("CUSTADDRCITY, CUSTADDRZIP, CUSTEMAIL, CUSTPHONENUMBER, FINTYPEDESC, FINASSETVALUE,");
		sql.append("EFFECTIVERATE, ENTITYCODE, ENTITYDESC, ENTITYPANNUMBER, ENTITYADDRHNBR,");
		sql.append("ENTITYFLATNBR, ENTITYADDRSTREET, ENTITYSTATE, ENTITYCITY, FINCCY, FinAmount, fintype,");
		sql.append("custflatnbr, EntityZip, FINCURRASSETVALUE, CUSTSALUTATION ");
		sql.append("from INTERESTCERTIFICATE_VIEW ");
		sql.append("Where FinReference =:FinReference");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		RowMapper<InterestCertificate> typeRowMapper = BeanPropertyRowMapper.newInstance(InterestCertificate.class);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public InterestCertificate getSumOfPrinicipalAndProfitAmount(String finReference, Date finStartDate,
			Date finEndDate) throws ParseException {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" select coalesce(sum(coalesce(FINSCHDPFTPAID, 0)),0) FINSCHDPFTPAID");
		sql.append(", coalesce(sum(coalesce(FINSCHDPRIPAID, 0)),0) FINSCHDPRIPAID");
		sql.append(" from FinRepayDetails ");
		sql.append(" Where FinReference =:FinReference");
		sql.append(" and FINPOSTDATE >=:FinstartDate and FINPOSTDATE <=:FinEndDate");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("FinstartDate", finStartDate);
		source.addValue("FinEndDate", finEndDate);

		RowMapper<InterestCertificate> typeRowMapper = BeanPropertyRowMapper.newInstance(InterestCertificate.class);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	public String getCollateralRef(String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("SELECT MIN(COLLATERALREF) ");
		sql.append(" FROM COLLATERALASSIGNMENT");
		sql.append(" Where Reference = :Reference and Active = :Active");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", finReference);
		source.addValue("Active", 1);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public String getCollateralType(String collateralRef) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("SELECT COLLATERALTYPE FROM COLLATERALSETUP");
		sql.append(" Where COLLATERALREF = :COLLATERALREF ");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("COLLATERALREF", collateralRef);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public String getCollateralTypeField(String interfaceType, String table, String field) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("SELECT MappingColumn FROM INTERFACEMAPPING");
		sql.append(" Where INTERFACENAME = :INTERFACENAME and INTERFACEFIELD = :INTERFACEFIELD");
		sql.append(" and MAPPINGTABLE = :MAPPINGTABLE and active = 1");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("INTERFACENAME", interfaceType);
		source.addValue("MAPPINGTABLE", table);
		source.addValue("INTERFACEFIELD", field);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;

	}

	@Override
	public String getCollateralTypeValue(String table, String columnField, String reference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		if (StringUtils.containsIgnoreCase(columnField, "city")) {
			sql.append("select T1.PCCITYNAME from " + table + " T ");
			sql.append("Inner join RMTPROVINCEVSCITY T1 on T1.PCCITY=T." + columnField + " ");
		} else if (StringUtils.containsIgnoreCase(columnField, "state")) {
			sql.append("select T1.CPPROVINCENAME from " + table + " T ");
			sql.append("Inner join RMTCOUNTRYVSPROVINCE T1 on T1.CPPROVINCE=T." + columnField + " ");
		} else {
			sql.append("SELECT " + columnField + " FROM " + table + " ");
		}

		sql.append(" Where REFERENCE =:REFERENCE");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("REFERENCE", reference);

		try {
			List<String> list = this.jdbcTemplate.queryForList(sql.toString(), source, String.class);
			if (list != null && list.size() > 0) {
				//Bugfix:considering single collateral property Value where it is returning multiple records 
				return list.get(0);
			}
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public List<Customer> getCoApplicantNames(String finReference) {
		logger.debug(Literal.ENTERING);

		JointAccountDetail detail = new JointAccountDetail();
		detail.setFinReference(finReference);

		StringBuilder sql = new StringBuilder();
		sql.append("Select custshrtname, CustSalutationCode  FROM FINJOINTACCOUNTDETAILS ja");
		sql.append(" inner join Customers c on c.CustCif = ja.CustCif");
		sql.append(" Where ja.FinReference = :FinReference");
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		RowMapper<Customer> typeRowMapper = BeanPropertyRowMapper.newInstance(Customer.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public InterestCertificate getSumOfPrinicipalAndProfitAmountPaid(String finReference, Date startDate,
			Date finEndDate) throws ParseException {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder(
				"select sum(FINSCHDPFTPAID) as FINSCHDPFTPAID ,sum(FINSCHDPRIPAID) as FINSCHDPRIPAID");
		selectSql.append(" from INTERESTCERTIFICATE_VIEW ");
		selectSql.append(
				" Where FinReference =:FinReference and FINPOSTDATE >=:FinstartDate and FINPOSTDATE <=:FinEndDate");

		logger.debug("selectSql: " + selectSql.toString());

		source.addValue("FinReference", finReference);
		source.addValue("FinstartDate", startDate);
		source.addValue("FinEndDate", finEndDate);

		RowMapper<InterestCertificate> typeRowMapper = BeanPropertyRowMapper.newInstance(InterestCertificate.class);
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}
		logger.debug("Leaving");
		return null;
	}

	public Map<String, Object> getSumOfPriPftEmiAmount(String finReference, Date finStartDate, Date finEndDate) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> amounts = new HashMap<>();

		StringBuilder sql = new StringBuilder();
		sql.append(
				" select sum(profitschd) as profitschd, sum(principalschd) as principalschd , sum(repayamount) as repayamount");
		sql.append(" from finscheduledetails ");
		sql.append(" Where FinReference = :FinReference");
		sql.append(" and schdate >=:FinstartDate and schdate <=:FinEndDate and repayonschdate=1  and InstNumber > 0");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("FinstartDate", finStartDate);
		source.addValue("FinEndDate", finEndDate);

		try {
			amounts = this.jdbcTemplate.queryForMap(sql.toString(), source);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return amounts;
	}

	@Override
	public Map<String, Object> getTotalGrcRepayProfit(String finReference, Date finStartDate, Date finEndDate) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> amounts = new HashMap<>();

		StringBuilder sql = new StringBuilder();
		sql.append(" select sum(profitschd) as grcPft, sum(schdpftpaid) as grcPftPaid ");
		sql.append(" from finscheduledetails ");
		sql.append(" Where FinReference = :FinReference and specifier in('G','E') and bpiOrHoliday=''");
		sql.append(" and schdate >=:FinstartDate and schdate <=:FinEndDate ");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("FinstartDate", finStartDate);
		source.addValue("FinEndDate", finEndDate);

		try {
			amounts = this.jdbcTemplate.queryForMap(sql.toString(), source);
		} catch (EmptyResultDataAccessException e) {
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return amounts;
	}

	@Override
	public InterestCertificate getSchedPrinicipalAndProfit(String finReference, Date startDate, Date endDate)
			throws ParseException {

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" SUM(PRINCIPALSCHD) RepayPriAmt ,SUM(PROFITSCHD) RepayPftAmt");
		sql.append(", SUM(SCHDPFTPAID) SchdProfitPaid ,SUM(SCHDPRIPAID) SchdPrinciplePaid");
		sql.append(" From FINSCHEDULEDETAILS");
		sql.append(" Where FinReference = ? and (schdate >= ? and schdate <= ?)");

		logger.trace(Literal.SQL + sql.toString());

		try {
			Object[] object = new Object[] { finReference, startDate, endDate };
			return this.jdbcOperations.queryForObject(sql.toString(), object, (rs, rowNum) -> {
				InterestCertificate ic = new InterestCertificate();

				ic.setRepayPriAmt(rs.getBigDecimal("RepayPriAmt"));
				ic.setRepayPftAmt(rs.getBigDecimal("RepayPftAmt"));
				ic.setSchdProfitPaid(rs.getBigDecimal("SchdProfitPaid"));
				ic.setSchdPrinciplePaid(rs.getBigDecimal("SchdPrinciplePaid"));

				return ic;
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Records are not found in FINSCHEDULEDETAILS for the  finreference >> {}");
		}

		return null;
	}

	@Override
	public FinanceScheduleDetail getScheduleDetailsByFinReference(String finReference, Date finStartDate,
			Date finEndDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" coalesce(sum(ProfitSchd), 0) ProfitSchd, coalesce(sum(PrincipalSchd), 0) PrincipalSchd");
		sql.append(", coalesce(sum(PartialPaidAmt), 0) PartialPaidAmt, coalesce(sum(SchdPftPaid), 0) SchdPftPaid");
		sql.append(", coalesce(sum(SchdPriPaid), 0) SchdPriPaid");
		sql.append(" From finscheduledetails");
		sql.append(" Where FinReference = ? and schdate >= ? and schdate <= ?");

		logger.trace(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql.toString(),
					new Object[] { finReference, finStartDate, finEndDate }, (rs, i) -> {
						FinanceScheduleDetail fsd = new FinanceScheduleDetail();

						fsd.setProfitSchd(rs.getBigDecimal("ProfitSchd"));
						fsd.setPrincipalSchd(rs.getBigDecimal("PrincipalSchd"));
						fsd.setPartialPaidAmt(rs.getBigDecimal("PartialPaidAmt"));
						fsd.setSchdPftPaid(rs.getBigDecimal("SchdPftPaid"));
						fsd.setSchdPriPaid(rs.getBigDecimal("SchdPriPaid"));

						return fsd;
					});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(
					"Record is not found in FinScheduleDetails table for the specified FinReference = {} and schdate >= {} and schdate <= {}",
					finReference, finStartDate, finEndDate);
		}
		return null;
	}

	@Override
	public InterestCertificate getRepayDetails(String finReference, Date startDate, Date endDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append("  sum(FinSchdPftPaid) FinSchdPftPaid, sum(FinSchdPriPaid) FinSchdPriPaid");
		sql.append(" From FinRepayDetails");
		sql.append(" Where FinReference = ? and FinSchdDate >= ? and FinSchdDate <= ? and FinValueDate > ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(),
					new Object[] { finReference, startDate, endDate, endDate }, (rs, i) -> {

						InterestCertificate ic = new InterestCertificate();

						ic.setFinSchdPftPaid(rs.getBigDecimal("FinSchdPftPaid"));
						ic.setFinSchdPriPaid(rs.getBigDecimal("FinSchdPriPaid"));

						return ic;
					});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(
					"Record is not found in FinRepayDetails for the specified FinReference = {} and FinSchdDate >= {} and FinSchdDate <= {} and FinValueDate > {}",
					finReference, startDate, endDate, endDate);
		}

		return null;
	}

}