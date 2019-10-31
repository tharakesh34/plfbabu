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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.systemmasters.InterestCertificateDAO;
import com.pennant.backend.model.agreement.InterestCertificate;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * DAO methods implementation for the <b>AddressType model</b> class.<br>
 */
public class InterestCertificateDAOImpl extends BasicDao<InterestCertificate> implements InterestCertificateDAO {
	private static Logger logger = Logger.getLogger(InterestCertificateDAOImpl.class);

	public InterestCertificateDAOImpl() {
		super();
	}

	@Override
	public InterestCertificate getInterestCertificateDetails(String finReference) throws ParseException {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();
		sql.append("select distinct c.CustShrtName CustName, ce.CustEmail, cp.PhoneNumber CustPhoneNumber");
		sql.append(", fm.FinReference, FinAssetValue FinAmount, EffectiveRateOfReturn EffectiveRate, fm.FinCCY");
		sql.append(", ft.FinType, FinTypeDesc, e.EntityCode, EntityDesc");
		sql.append(", PanNumber EntityPanNumber, EntityAddrHnbr, EntityFlatNbr, EntityAddrStreet");
		sql.append(", rcp.CpProvinceName CustAddrState, Pincode EntityZip, PccityName EntityCity, ct.CountryDesc");
		sql.append(", ca.CustAddrHnbr, ca.CustAddrStreet, ca.CustAddrZIP, ca.CustFlatNbr CustFlatNr");
		// sql.append(", rd.FinPostDate, rd.FinSchdPftPaid, rd.FinschdPriPaid");
		sql.append(", rcp.CpProvinceName EntityState, rpc.PccityName CustAddrCity");
		sql.append(", (pft.TotalPftBal + pft.TotalPriBal)  TotOustandingAmt, pft.TotalPftBal, pft.TotalPriBal");
		sql.append(" FROM FinanceMain fm");
		sql.append(" inner join RmtFinanceTypes ft on ft.FinType = fm.FinType");
		sql.append(" inner join Customers c on c.custId = fm.CustId");
		sql.append(" inner join CustomerAddresses ca on ca.CustId = fm.CustId and ca.CustAddrPriority = 5");
		sql.append(" inner join SMTDivisionDetail dd on dd.DivisionCode = dd.DivisionCode");
		sql.append(" inner join Entity e on e.EntityCode = dd.Entitycode");
		sql.append(" inner join RmtCountryVsProvince rcp on rcp.CpProVince = ca.CustAddrProvince");
		sql.append(" inner join RmtProvinceVsCity rpc on rpc.PcCity = e.CityCode");
		sql.append(" inner join BmtCountries ct on ct.CountryCode = ca.CustAddrCountry");
		sql.append(" inner join RmtCountryVsProvince rcp on rcp.CpProVince = e.StateCode");
		sql.append(" inner join CustomerEmails ce on ce.CustId = c.CustId and ce.CustEmailPriority = 5");
		sql.append(" inner join CustomerPhoneNumbers cp on cp.PhoneCustId = c.CustId and cp.PhoneTypePriority = 5");
		sql.append(" inner join FinPftDetails pft ON pft.Finreference = fm.Finreference");
		// sql.append(" left join FinRepayDetails rd on rd.FinReference =
		// fm.FinReference");
		sql.append(" Where fm.FinReference = :FinReference");

		logger.trace(Literal.SQL + sql.toString());

		source.addValue("FinReference", finReference);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, new RowMapper<InterestCertificate>() {

				@Override
				public InterestCertificate mapRow(ResultSet rs, int arg1) throws SQLException {
					InterestCertificate ic = new InterestCertificate();

					ic.setCustName(rs.getString("CustName"));
					ic.setCustEmail(rs.getString("CustEmail"));
					ic.setCustPhoneNumber(rs.getString("CustPhoneNumber"));
					ic.setFinReference(rs.getString("FinReference"));
					ic.setFinAmount(rs.getString("FinAmount"));
					ic.setEffectiveRate(rs.getBigDecimal("EffectiveRate"));
					ic.setFinCcy(rs.getString("FinCCY"));
					ic.setFinType(rs.getString("FinType"));
					ic.setFinTypeDesc(rs.getString("FinTypeDesc"));
					ic.setEntityCode(rs.getString("EntityCode"));
					ic.setEntityDesc(rs.getString("EntityDesc"));
					ic.setEntityPanNumber(rs.getString("EntityPanNumber"));
					ic.setEntityAddrHnbr(rs.getString("EntityAddrHnbr"));
					ic.setEntityFlatNbr(rs.getString("EntityFlatNbr"));
					ic.setEntityAddrStreet(rs.getString("EntityAddrStreet"));
					ic.setCustAddrState(rs.getString("CustAddrState"));
					ic.setEntityZip(rs.getString("EntityZip"));
					ic.setEntityCity(rs.getString("EntityCity"));
					ic.setCountryDesc(rs.getString("CountryDesc"));
					ic.setCustAddrHnbr(rs.getString("CustAddrHnbr"));
					ic.setCustAddrStreet(rs.getString("CustAddrStreet"));
					ic.setCustAddrZIP(rs.getString("CustAddrZIP"));
					ic.setCustFlatNbr(rs.getString("CustFlatNr"));
					// ic.setFinPostDate(rs.getDate("FinPostDate"));
					ic.setEntityState(rs.getString("EntityState"));
					ic.setCustAddrCity(rs.getString("CustAddrCity"));
					ic.setTotOustandingamt(rs.getString("TotOustandingAmt"));
					ic.setTotalPftBal(rs.getString("TotalPftBal"));
					ic.setTotalPriBal(rs.getString("TotalPriBal"));
					return ic;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public InterestCertificate getSumOfPrinicipalAndProfitAmount(String finReference, String finStartDate,
			String finEndDate) throws ParseException {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" select sum(FINSCHDPFTPAID) FINSCHDPFTPAID, sum(FINSCHDPRIPAID) FINSCHDPRIPAID");
		sql.append(" from FinRepayDetails ");
		sql.append(" Where FinReference =:FinReference");
		sql.append(" and FINPOSTDATE >=:FinstartDate and FINPOSTDATE <=:FinEndDate");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("FinstartDate", DateUtil.parse(finStartDate, "dd/MM/yyyy"));
		source.addValue("FinEndDate", DateUtil.parse(finEndDate, "dd/MM/yyyy"));

		RowMapper<InterestCertificate> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(InterestCertificate.class);
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
			return this.jdbcTemplate.queryForObject(sql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public List<String> getCoApplicantNames(String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Select custshrtname  FROM FINJOINTACCOUNTDETAILS ja");
		sql.append(" inner join Customers c on c.CustCif = ja.CustCif");
		sql.append(" Where ja.FinReference = :FinReference");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		logger.debug(Literal.LEAVING);
		return jdbcTemplate.queryForList(sql.toString(), source, String.class);

	}
}