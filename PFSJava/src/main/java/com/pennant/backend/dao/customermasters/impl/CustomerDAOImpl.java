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
 * FileName    		:  CustomerDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  27-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.customermasters.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.Abuser;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceExposure;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.ProspectCustomer;
import com.pennant.backend.model.reports.AvailPastDue;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;
import com.pennanttech.ws.model.customer.SRMCustRequest;

/**
 * DAO methods implementation for the <b>Customer model</b> class.<br>
 * 
 */
public class CustomerDAOImpl extends SequenceDao<Customer> implements CustomerDAO {
	private static Logger logger = LogManager.getLogger(CustomerDAOImpl.class);

	public CustomerDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new Customer
	 * 
	 * @return Customer
	 */
	@Override
	public Customer getCustomer(boolean createNew, Customer customer) {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = null;
		if (!createNew) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Customer");
		} else {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CustomerQDE");
		}
		if (workFlowDetails != null) {
			customer.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return customer;
	}

	/**
	 * This method get the module from method getCustomer() and set the new record flag as true and return Customer()
	 * 
	 * @return Customer
	 */
	@Override
	public Customer getNewCustomer(boolean createNew, Customer customer) {
		logger.debug("Entering");

		Country defaultCountry = PennantApplicationUtil.getDefaultCounty();

		customer = getCustomer(createNew, customer);
		customer.setNewRecord(true);
		customer.setCustCOB(defaultCountry.getCountryCode());

		PFSParameter parameter = SysParamUtil.getSystemParameterObject("APP_DFT_CURR");

		if (customer.getCustBaseCcy() == null) {
			customer.setCustBaseCcy(parameter.getSysParmValue().trim());
		}

		parameter = SysParamUtil.getSystemParameterObject("APP_LNG");
		if (customer.getCustLng() == null) {
			customer.setCustLng(parameter.getSysParmValue().trim());
		}
		if (customer.getLovDescCustLngName() == null) {
			customer.setLovDescCustLngName(parameter.getSysParmDescription());
		}

		customer.setCustParentCountry(defaultCountry.getCountryCode());
		customer.setLovDescCustParentCountryName(parameter.getSysParmDescription());

		customer.setCustRiskCountry(defaultCountry.getCountryCode());
		customer.setLovDescCustRiskCountryName(defaultCountry.getCountryDesc());

		customer.setCustResdCountry(defaultCountry.getCountryCode());
		customer.setLovDescCustResdCountryName(defaultCountry.getCountryDesc());

		customer.setCustNationality(defaultCountry.getCountryCode());
		customer.setLovDescCustNationalityName(defaultCountry.getCountryDesc());

		customer.setCustGroupID(0);

		logger.debug("Leaving");
		return customer;
	}

	/**
	 * Fetch the Record Customers details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Customer
	 */
	@Override
	public Customer getCustomerByID(final long id, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustID, CustCIF, CustCoreBank, CustCtgCode, CustTypeCode, CustSalutationCode");
		sql.append(", CustFName, CustMName, CustLName, CustShrtName, CustFNameLclLng, CustMNameLclLng");
		sql.append(", CustLNameLclLng, CustShrtNameLclLng, CustDftBranch, CustGenderCode, CustDOB");
		sql.append(", CustPOB, CustCOB, CustPassportNo, CustMotherMaiden, CustIsMinor, CustReferedBy");
		sql.append(", CustDSA, CustDSADept, CustRO1, CustRO2, CustGroupID, CustSts, CustStsChgDate");
		sql.append(", CustGroupSts, CustIsBlocked, CustIsActive, CustIsClosed, CustInactiveReason");
		sql.append(", CustIsDecease, CustIsDormant, CustIsDelinquent, CustIsTradeFinCust, CustIsStaff");
		sql.append(", CustTradeLicenceNum, CustTradeLicenceExpiry, CustPassportExpiry, CustVisaNum");
		sql.append(", CustVisaExpiry, CustStaffID, CustIndustry, CustSector, CustSubSector, CustProfession");
		sql.append(", CustTotalIncome, CustMaritalSts, CustEmpSts, CustSegment, CustSubSegment, CustIsBlackListed");
		sql.append(", CustBLRsnCode, CustIsRejected, CustRejectedRsn, CustBaseCcy, CustLng, CustParentCountry");
		sql.append(", CustResdCountry, CustRiskCountry, CustNationality, CustClosedOn, CustStmtFrq");
		sql.append(", CustIsStmtCombined, CustStmtLastDate, CustStmtNextDate, CustStmtDispatchMode");
		sql.append(", CustFirstBusinessDate, CustAddlVar81, CustAddlVar82, CustAddlVar83, CustAddlVar84");
		sql.append(", CustAddlVar85, CustAddlVar86, CustAddlVar87, CustAddlVar88, CustAddlVar89, CustAddlDate1");
		sql.append(", CustAddlDate2, CustAddlDate3, CustAddlDate4, CustAddlDate5, CustAddlVar1, CustAddlVar2");
		sql.append(", CustAddlVar3, CustAddlVar4, CustAddlVar5, CustAddlVar6, CustAddlVar7, CustAddlVar8");
		sql.append(", CustAddlVar9, CustAddlVar10, CustAddlVar11, CustAddlDec1, CustAddlDec2, CustAddlDec3");
		sql.append(", CustAddlDec4, CustAddlDec5, CustSourceID, CustAddlInt1, CustAddlInt2, CustAddlInt3");
		sql.append(", CustAddlInt4, CustAddlInt5, DedupFound, SkipDedup, CustTotalExpense, CustBlackListDate");
		sql.append(", NoOfDependents, CustCRCPR, JointCust, JointCustName, JointCustDob, CustRelation");
		sql.append(", ContactPersonName, EmailID, PhoneNumber, SalariedCustomer, CustSuspSts, CustSuspDate");
		sql.append(", CustSuspTrigger, ApplicationNo, Dnd, Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, CasteId");
		sql.append(", ReligionId, SubCategory, MarginDeviation");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescCustTypeCodeName, LovDescCustMaritalStsName, LovDescCustEmpStsName");
			sql.append(", LovDescCustStsName, LovDescCustIndustryName, LovDescCustSectorName");
			sql.append(", LovDescCustSubSectorName, LovDescCustProfessionName, LovDescCustCOBName");
			sql.append(", LovDescCustSegmentName, LovDescCustNationalityName, LovDescCustGenderCodeName");
			sql.append(", LovDescCustDSADeptName, LovDescCustRO1Name, LovDescCustRO1City");
			sql.append(", LovDescCustGroupStsName, LovDescCustDftBranchName, LovDescCustCtgCodeName");
			sql.append(", LovDescCustCtgType, LovDescCustSalutationCodeName, LovDescCustParentCountryName");
			sql.append(", LovDescCustResdCountryName, LovDescCustRiskCountryName, LovDescCustRO2Name");
			sql.append(", LovDescCustBLRsnCodeName, LovDescCustRejectedRsnName, LovDescCustGroupCode");
			sql.append(", LovDesccustGroupIDName, LovDescCustSubSegmentName, LovDescCustLngName");
			sql.append(", LovDescDispatchModeDescName, LovDescTargetName, CustSwiftBrnCode");
			sql.append(", CasteCode, ReligionCode, CasteDesc, ReligionDesc, BranchProvince");
		}

		sql.append(" from Customers");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustID = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { id }, new RowMapper<Customer>() {
				@Override
				public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
					Customer c = new Customer();

					c.setCustID(rs.getLong("CustID"));
					c.setCustCIF(rs.getString("CustCIF"));
					c.setCustCoreBank(rs.getString("CustCoreBank"));
					c.setCustCtgCode(rs.getString("CustCtgCode"));
					c.setCustTypeCode(rs.getString("CustTypeCode"));
					c.setCustSalutationCode(rs.getString("CustSalutationCode"));
					c.setCustFName(rs.getString("CustFName"));
					c.setCustMName(rs.getString("CustMName"));
					c.setCustLName(rs.getString("CustLName"));
					c.setCustShrtName(rs.getString("CustShrtName"));
					c.setCustFNameLclLng(rs.getString("CustFNameLclLng"));
					c.setCustMNameLclLng(rs.getString("CustMNameLclLng"));
					c.setCustLNameLclLng(rs.getString("CustLNameLclLng"));
					c.setCustShrtNameLclLng(rs.getString("CustShrtNameLclLng"));
					c.setCustDftBranch(rs.getString("CustDftBranch"));
					c.setCustGenderCode(rs.getString("CustGenderCode"));
					c.setCustDOB(rs.getTimestamp("CustDOB"));
					c.setCustPOB(rs.getString("CustPOB"));
					c.setCustCOB(rs.getString("CustCOB"));
					c.setCustPassportNo(rs.getString("CustPassportNo"));
					c.setCustMotherMaiden(rs.getString("CustMotherMaiden"));
					c.setCustIsMinor(rs.getBoolean("CustIsMinor"));
					c.setCustReferedBy(rs.getString("CustReferedBy"));
					c.setCustDSA(rs.getString("CustDSA"));
					c.setCustDSADept(rs.getString("CustDSADept"));
					c.setCustRO1(rs.getLong("CustRO1"));
					c.setCustRO2(rs.getString("CustRO2"));
					c.setCustGroupID(rs.getLong("CustGroupID"));
					c.setCustSts(rs.getString("CustSts"));
					c.setCustStsChgDate(rs.getTimestamp("CustStsChgDate"));
					c.setCustGroupSts(rs.getString("CustGroupSts"));
					c.setCustIsBlocked(rs.getBoolean("CustIsBlocked"));
					c.setCustIsActive(rs.getBoolean("CustIsActive"));
					c.setCustIsClosed(rs.getBoolean("CustIsClosed"));
					c.setCustInactiveReason(rs.getString("CustInactiveReason"));
					c.setCustIsDecease(rs.getBoolean("CustIsDecease"));
					c.setCustIsDormant(rs.getBoolean("CustIsDormant"));
					c.setCustIsDelinquent(rs.getBoolean("CustIsDelinquent"));
					c.setCustIsTradeFinCust(rs.getBoolean("CustIsTradeFinCust"));
					c.setCustIsStaff(rs.getBoolean("CustIsStaff"));
					c.setCustTradeLicenceNum(rs.getString("CustTradeLicenceNum"));
					c.setCustTradeLicenceExpiry(rs.getTimestamp("CustTradeLicenceExpiry"));
					c.setCustPassportExpiry(rs.getTimestamp("CustPassportExpiry"));
					c.setCustVisaNum(rs.getString("CustVisaNum"));
					c.setCustVisaExpiry(rs.getTimestamp("CustVisaExpiry"));
					c.setCustStaffID(rs.getString("CustStaffID"));
					c.setCustIndustry(rs.getString("CustIndustry"));
					c.setCustSector(rs.getString("CustSector"));
					c.setCustSubSector(rs.getString("CustSubSector"));
					c.setCustProfession(rs.getString("CustProfession"));
					c.setCustTotalIncome(rs.getBigDecimal("CustTotalIncome"));
					c.setCustMaritalSts(rs.getString("CustMaritalSts"));
					c.setCustEmpSts(rs.getString("CustEmpSts"));
					c.setCustSegment(rs.getString("CustSegment"));
					c.setCustSubSegment(rs.getString("CustSubSegment"));
					c.setCustIsBlackListed(rs.getBoolean("CustIsBlackListed"));
					c.setCustBLRsnCode(rs.getString("CustBLRsnCode"));
					c.setCustIsRejected(rs.getBoolean("CustIsRejected"));
					c.setCustRejectedRsn(rs.getString("CustRejectedRsn"));
					c.setCustBaseCcy(rs.getString("CustBaseCcy"));
					c.setCustLng(rs.getString("CustLng"));
					c.setCustParentCountry(rs.getString("CustParentCountry"));
					c.setCustResdCountry(rs.getString("CustResdCountry"));
					c.setCustRiskCountry(rs.getString("CustRiskCountry"));
					c.setCustNationality(rs.getString("CustNationality"));
					c.setCustClosedOn(rs.getTimestamp("CustClosedOn"));
					c.setCustStmtFrq(rs.getString("CustStmtFrq"));
					c.setCustIsStmtCombined(rs.getBoolean("CustIsStmtCombined"));
					c.setCustStmtLastDate(rs.getTimestamp("CustStmtLastDate"));
					c.setCustStmtNextDate(rs.getTimestamp("CustStmtNextDate"));
					c.setCustStmtDispatchMode(rs.getString("CustStmtDispatchMode"));
					c.setCustFirstBusinessDate(rs.getTimestamp("CustFirstBusinessDate"));
					c.setCustAddlVar81(rs.getString("CustAddlVar81"));
					c.setCustAddlVar82(rs.getString("CustAddlVar82"));
					c.setCustAddlVar83(rs.getString("CustAddlVar83"));
					c.setCustAddlVar84(rs.getString("CustAddlVar84"));
					c.setCustAddlVar85(rs.getString("CustAddlVar85"));
					c.setCustAddlVar86(rs.getString("CustAddlVar86"));
					c.setCustAddlVar87(rs.getString("CustAddlVar87"));
					c.setCustAddlVar88(rs.getString("CustAddlVar88"));
					c.setCustAddlVar89(rs.getString("CustAddlVar89"));
					c.setCustAddlDate1(rs.getTimestamp("CustAddlDate1"));
					c.setCustAddlDate2(rs.getTimestamp("CustAddlDate2"));
					c.setCustAddlDate3(rs.getTimestamp("CustAddlDate3"));
					c.setCustAddlDate4(rs.getTimestamp("CustAddlDate4"));
					c.setCustAddlDate5(rs.getTimestamp("CustAddlDate5"));
					c.setCustAddlVar1(rs.getString("CustAddlVar1"));
					c.setCustAddlVar2(rs.getString("CustAddlVar2"));
					c.setCustAddlVar3(rs.getString("CustAddlVar3"));
					c.setCustAddlVar4(rs.getString("CustAddlVar4"));
					c.setCustAddlVar5(rs.getString("CustAddlVar5"));
					c.setCustAddlVar6(rs.getString("CustAddlVar6"));
					c.setCustAddlVar7(rs.getString("CustAddlVar7"));
					c.setCustAddlVar8(rs.getString("CustAddlVar8"));
					c.setCustAddlVar9(rs.getString("CustAddlVar9"));
					c.setCustAddlVar10(rs.getString("CustAddlVar10"));
					c.setCustAddlVar11(rs.getString("CustAddlVar11"));
					c.setCustAddlDec1(rs.getBigDecimal("CustAddlDec1"));
					c.setCustAddlDec2(rs.getDouble("CustAddlDec2"));
					c.setCustAddlDec3(rs.getDouble("CustAddlDec3"));
					c.setCustAddlDec4(rs.getDouble("CustAddlDec4"));
					c.setCustAddlDec5(rs.getDouble("CustAddlDec5"));
					c.setCustSourceID(rs.getString("CustSourceID"));
					c.setCustAddlInt1(rs.getInt("CustAddlInt1"));
					c.setCustAddlInt2(rs.getInt("CustAddlInt2"));
					c.setCustAddlInt3(rs.getInt("CustAddlInt3"));
					c.setCustAddlInt4(rs.getInt("CustAddlInt4"));
					c.setCustAddlInt5(rs.getInt("CustAddlInt5"));
					c.setDedupFound(rs.getBoolean("DedupFound"));
					c.setSkipDedup(rs.getBoolean("SkipDedup"));
					c.setCustTotalExpense(rs.getBigDecimal("CustTotalExpense"));
					c.setCustBlackListDate(rs.getTimestamp("CustBlackListDate"));
					c.setNoOfDependents(rs.getInt("NoOfDependents"));
					c.setCustCRCPR(rs.getString("CustCRCPR"));
					c.setJointCust(rs.getBoolean("JointCust"));
					c.setJointCustName(rs.getString("JointCustName"));
					c.setJointCustDob(rs.getTimestamp("JointCustDob"));
					c.setCustRelation(rs.getString("CustRelation"));
					c.setContactPersonName(rs.getString("ContactPersonName"));
					c.setEmailID(rs.getString("EmailID"));
					c.setPhoneNumber(rs.getString("PhoneNumber"));
					c.setSalariedCustomer(rs.getBoolean("SalariedCustomer"));
					c.setCustSuspSts(rs.getBoolean("CustSuspSts"));
					c.setCustSuspDate(rs.getTimestamp("CustSuspDate"));
					c.setCustSuspTrigger(rs.getString("CustSuspTrigger"));
					c.setApplicationNo(rs.getString("ApplicationNo"));
					c.setDnd(rs.getBoolean("Dnd"));
					c.setVersion(rs.getInt("Version"));
					c.setLastMntBy(rs.getLong("LastMntBy"));
					c.setLastMntOn(rs.getTimestamp("LastMntOn"));
					c.setRecordStatus(rs.getString("RecordStatus"));
					c.setRoleCode(rs.getString("RoleCode"));
					c.setNextRoleCode(rs.getString("NextRoleCode"));
					c.setTaskId(rs.getString("TaskId"));
					c.setNextTaskId(rs.getString("NextTaskId"));
					c.setRecordType(rs.getString("RecordType"));
					c.setWorkflowId(rs.getLong("WorkflowId"));
					c.setCasteId(rs.getLong("CasteId"));
					c.setReligionId(rs.getLong("ReligionId"));
					c.setSubCategory(rs.getString("SubCategory"));
					c.setMarginDeviation(rs.getBoolean("MarginDeviation"));

					if (StringUtils.trimToEmpty(type).contains("View")) {
						c.setLovDescCustTypeCodeName(rs.getString("LovDescCustTypeCodeName"));
						c.setLovDescCustMaritalStsName(rs.getString("LovDescCustMaritalStsName"));
						c.setLovDescCustEmpStsName(rs.getString("LovDescCustEmpStsName"));
						c.setLovDescCustStsName(rs.getString("LovDescCustStsName"));
						c.setLovDescCustIndustryName(rs.getString("LovDescCustIndustryName"));
						c.setLovDescCustSectorName(rs.getString("LovDescCustSectorName"));
						c.setLovDescCustSubSectorName(rs.getString("LovDescCustSubSectorName"));
						c.setLovDescCustProfessionName(rs.getString("LovDescCustProfessionName"));
						c.setLovDescCustCOBName(rs.getString("LovDescCustCOBName"));
						c.setLovDescCustSegmentName(rs.getString("LovDescCustSegmentName"));
						c.setLovDescCustNationalityName(rs.getString("LovDescCustNationalityName"));
						c.setLovDescCustGenderCodeName(rs.getString("LovDescCustGenderCodeName"));
						c.setLovDescCustDSADeptName(rs.getString("LovDescCustDSADeptName"));
						c.setLovDescCustRO1Name(rs.getString("LovDescCustRO1Name"));
						c.setLovDescCustRO1City(rs.getString("LovDescCustRO1City"));
						c.setLovDescCustGroupStsName(rs.getString("LovDescCustGroupStsName"));
						c.setLovDescCustDftBranchName(rs.getString("LovDescCustDftBranchName"));
						c.setLovDescCustCtgCodeName(rs.getString("LovDescCustCtgCodeName"));
						c.setLovDescCustCtgType(rs.getString("LovDescCustCtgType"));
						c.setLovDescCustSalutationCodeName(rs.getString("LovDescCustSalutationCodeName"));
						c.setLovDescCustParentCountryName(rs.getString("LovDescCustParentCountryName"));
						c.setLovDescCustResdCountryName(rs.getString("LovDescCustResdCountryName"));
						c.setLovDescCustRiskCountryName(rs.getString("LovDescCustRiskCountryName"));
						c.setLovDescCustRO2Name(rs.getString("LovDescCustRO2Name"));
						c.setLovDescCustBLRsnCodeName(rs.getString("LovDescCustBLRsnCodeName"));
						c.setLovDescCustRejectedRsnName(rs.getString("LovDescCustRejectedRsnName"));
						c.setLovDescCustGroupCode(rs.getString("LovDescCustGroupCode"));
						c.setLovDesccustGroupIDName(rs.getString("LovDesccustGroupIDName"));
						c.setLovDescCustSubSegmentName(rs.getString("LovDescCustSubSegmentName"));
						c.setLovDescCustLngName(rs.getString("LovDescCustLngName"));
						c.setLovDescDispatchModeDescName(rs.getString("LovDescDispatchModeDescName"));
						c.setLovDescTargetName(rs.getString("LovDescTargetName"));
						c.setCustSwiftBrnCode(rs.getString("CustSwiftBrnCode"));
						c.setCasteCode(rs.getString("CasteCode"));
						c.setReligionCode(rs.getString("ReligionCode"));
						c.setCasteDesc(rs.getString("CasteDesc"));
						c.setReligionDesc(rs.getString("ReligionDesc"));
						c.setBranchProvince(rs.getString("BranchProvince"));
					}

					return c;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	/**
	 * Fetch the Record Customers details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Customer
	 */
	@Override
	public Customer getCustomerForPostings(final long custId) {
		logger.debug("Entering");
		Customer customer = new Customer();
		customer.setId(custId);

		StringBuilder selectSql = new StringBuilder("SELECT CustCIF, CustCOB, CustCtgCode, CustIndustry,");
		selectSql.append(" CustIsStaff, CustNationality, CustParentCountry, CustResdCountry,");
		selectSql.append(" CustRiskCountry, CustSector, CustSubSector, CustTypeCode");
		selectSql.append(" , CasteId, ReligionId, SubCategory");

		selectSql.append(" FROM  Customers");
		selectSql.append(" Where CustID =:CustID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);

		try {
			customer = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			customer = null;
		}
		logger.debug("Leaving");
		return customer;
	}

	/**
	 * This method Deletes the Record from the Customers or Customers_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Customers by key CustID
	 * 
	 * @param Customers
	 *            (customer)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(Customer customer, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From Customers");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustID =:CustID");
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);

		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into Customers or Customers_Temp.
	 *
	 * save Customers
	 * 
	 * @param Customers
	 *            (customer)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(Customer customer, String type) {
		logger.debug("Entering");

		if (customer.getCustID() == 0 || customer.getCustID() == Long.MIN_VALUE) {
			customer.setCustID(getNextValue("SeqCustomers"));
		}
		// FIXME : To be discussed

		StringBuilder insertSql = new StringBuilder("Insert Into Customers");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append("(CustID, CustCIF, CustCoreBank, CustCtgCode, CustTypeCode, CustSalutationCode, CustFName,");
		insertSql.append(" CustMName, CustLName, CustShrtName, CustFNameLclLng, CustMNameLclLng, CustLNameLclLng,");
		insertSql.append(
				" CustShrtNameLclLng, CustDftBranch, CustGenderCode, CustDOB, CustPOB, CustCOB, CustPassportNo,");
		insertSql.append(
				" CustMotherMaiden, CustIsMinor, CustReferedBy, CustDSA, CustDSADept, CustRO1, CustRO2, CustGroupID,");
		insertSql.append(
				" CustSts, CustStsChgDate, CustGroupSts, CustIsBlocked, CustIsActive, CustIsClosed, CustInactiveReason,");
		insertSql.append(" CustIsDecease, CustIsDormant, CustIsDelinquent, CustIsTradeFinCust,CustTradeLicenceNum ,");
		insertSql.append(
				" CustTradeLicenceExpiry,CustPassportExpiry,CustVisaNum ,CustVisaExpiry, CustIsStaff, CustStaffID,");
		insertSql.append(
				" CustIndustry, CustSector, CustSubSector, CustProfession, CustTotalIncome, CustMaritalSts, CustEmpSts,");
		insertSql.append(
				" CustSegment, CustSubSegment, CustIsBlackListed, CustBLRsnCode, CustIsRejected, CustRejectedRsn,");
		insertSql
				.append(" CustBaseCcy, CustLng, CustParentCountry, CustResdCountry, CustRiskCountry, CustNationality,");
		insertSql.append(
				" CustClosedOn, CustStmtFrq, CustIsStmtCombined, CustStmtLastDate, CustStmtNextDate, CustStmtDispatchMode,");
		insertSql.append(
				" CustFirstBusinessDate, CustAddlVar81, CustAddlVar82, CustAddlVar83, CustAddlVar84, CustAddlVar85,");
		insertSql.append(
				" CustAddlVar86, CustAddlVar87, CustAddlVar88, CustAddlVar89, CustAddlDate1, CustAddlDate2, CustAddlDate3,");
		insertSql.append(
				" CustAddlDate4, CustAddlDate5, CustAddlVar1, CustAddlVar2, CustAddlVar3, CustAddlVar4, CustAddlVar5,");
		insertSql.append(
				" CustAddlVar6, CustAddlVar7, CustAddlVar8, CustAddlVar9, CustAddlVar10, CustAddlVar11, CustAddlDec1,");
		insertSql.append(
				" CustAddlDec2, CustAddlDec3, CustAddlDec4, CustAddlDec5, CustAddlInt1, CustAddlInt2, CustAddlInt3, CustAddlInt4,CustAddlInt5,");
		insertSql.append(
				" DedupFound,SkipDedup,CustTotalExpense,CustBlackListDate,NoOfDependents,CustCRCPR,CustSourceID,");
		insertSql.append(
				" JointCust, JointCustName, JointCustDob, custRelation, ContactPersonName, EmailID, PhoneNumber, SalariedCustomer,ApplicationNo, Dnd,");

		insertSql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		insertSql.append(" ,CasteId, ReligionId, SubCategory,MarginDeviation )");
		insertSql.append(
				" Values(:CustID, :CustCIF, :CustCoreBank, :CustCtgCode, :CustTypeCode, :CustSalutationCode, :CustFName, :CustMName,");
		insertSql.append(
				" :CustLName, :CustShrtName, :CustFNameLclLng, :CustMNameLclLng, :CustLNameLclLng, :CustShrtNameLclLng, :CustDftBranch,");
		insertSql.append(
				" :CustGenderCode, :CustDOB, :CustPOB, :CustCOB, :CustPassportNo, :CustMotherMaiden, :CustIsMinor, :CustReferedBy,");
		insertSql.append(
				" :CustDSA, :CustDSADept, :CustRO1, :CustRO2, :CustGroupID, :CustSts, :CustStsChgDate, :CustGroupSts, :CustIsBlocked,");
		insertSql.append(
				" :CustIsActive, :CustIsClosed, :CustInactiveReason, :CustIsDecease, :CustIsDormant, :CustIsDelinquent,");
		insertSql.append(
				" :CustIsTradeFinCust, :CustTradeLicenceNum ,:CustTradeLicenceExpiry, :CustPassportExpiry, :CustVisaNum , :CustVisaExpiry,");
		insertSql.append(
				" :CustIsStaff, :CustStaffID, :CustIndustry, :CustSector, :CustSubSector, :CustProfession, :CustTotalIncome,");
		insertSql.append(
				" :CustMaritalSts, :CustEmpSts, :CustSegment, :CustSubSegment, :CustIsBlackListed, :CustBLRsnCode, :CustIsRejected,");
		insertSql.append(
				" :CustRejectedRsn, :CustBaseCcy, :CustLng, :CustParentCountry, :CustResdCountry, :CustRiskCountry, :CustNationality,");
		insertSql.append(
				" :CustClosedOn, :CustStmtFrq, :CustIsStmtCombined, :CustStmtLastDate, :CustStmtNextDate, :CustStmtDispatchMode,");
		insertSql.append(
				" :CustFirstBusinessDate, :CustAddlVar81, :CustAddlVar82, :CustAddlVar83, :CustAddlVar84, :CustAddlVar85, :CustAddlVar86,");
		insertSql.append(
				" :CustAddlVar87, :CustAddlVar88, :CustAddlVar89, :CustAddlDate1, :CustAddlDate2, :CustAddlDate3, :CustAddlDate4,");
		insertSql.append(
				" :CustAddlDate5, :CustAddlVar1, :CustAddlVar2, :CustAddlVar3, :CustAddlVar4, :CustAddlVar5, :CustAddlVar6, :CustAddlVar7,");
		insertSql.append(
				" :CustAddlVar8, :CustAddlVar9, :CustAddlVar10, :CustAddlVar11, :CustAddlDec1, :CustAddlDec2, :CustAddlDec3, :CustAddlDec4,");
		insertSql.append(" :CustAddlDec5, :CustAddlInt1, :CustAddlInt2, :CustAddlInt3, :CustAddlInt4, :CustAddlInt5,");
		insertSql.append(
				" :DedupFound,:SkipDedup,:CustTotalExpense,:CustBlackListDate,:NoOfDependents,:CustCRCPR,:CustSourceID,");
		insertSql.append(
				" :JointCust, :JointCustName, :JointCustDob, :custRelation, :ContactPersonName, :EmailID, :PhoneNumber, :SalariedCustomer, :ApplicationNo, :Dnd,");
		insertSql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId");
		insertSql.append(" ,:CasteId, :ReligionId, :SubCategory, :MarginDeviation )");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return customer.getId();
	}

	/**
	 * This method updates the Record Customers or Customers_Temp. if Record not updated then throws DataAccessException
	 * with error 41004. update Customers by key CustID and Version
	 * 
	 * @param Customers
	 *            (customer)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(Customer customer, String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update Customers");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(
				" Set CustCtgCode = :CustCtgCode,CustCoreBank =:CustCoreBank, CustTypeCode = :CustTypeCode, CustSalutationCode = :CustSalutationCode, CustFName = :CustFName,");
		updateSql.append(
				" CustMName = :CustMName, CustLName = :CustLName, CustShrtName = :CustShrtName, CustFNameLclLng = :CustFNameLclLng,");
		updateSql.append(
				" CustMNameLclLng = :CustMNameLclLng, CustLNameLclLng = :CustLNameLclLng, CustShrtNameLclLng = :CustShrtNameLclLng,");
		updateSql.append(
				" CustDftBranch = :CustDftBranch, CustGenderCode = :CustGenderCode, CustDOB = :CustDOB, CustPOB = :CustPOB,");
		updateSql.append(
				" CustCOB = :CustCOB, CustPassportNo = :CustPassportNo, CustMotherMaiden = :CustMotherMaiden, CustIsMinor = :CustIsMinor,");
		updateSql.append(
				" CustReferedBy = :CustReferedBy, CustDSA = :CustDSA, CustDSADept = :CustDSADept, CustRO1 = :CustRO1,");
		updateSql.append(
				" CustRO2 = :CustRO2, custRelation = :custRelation, CustGroupID = :CustGroupID, CustSts = :CustSts, CustStsChgDate = :CustStsChgDate,");
		updateSql.append(
				" CustGroupSts = :CustGroupSts, CustIsBlocked = :CustIsBlocked, CustIsActive = :CustIsActive, CustIsClosed = :CustIsClosed,");
		updateSql.append(
				" CustInactiveReason = :CustInactiveReason, CustIsDecease = :CustIsDecease, CustIsDormant = :CustIsDormant,");
		updateSql.append(
				" CustIsDelinquent = :CustIsDelinquent, CustIsTradeFinCust = :CustIsTradeFinCust, CustTradeLicenceNum = :CustTradeLicenceNum,");
		updateSql.append(
				" CustTradeLicenceExpiry= :CustTradeLicenceExpiry,CustPassportExpiry = :CustPassportExpiry,CustVisaNum = :CustVisaNum, ");
		updateSql.append(
				" CustVisaExpiry = :CustVisaExpiry, CustIsStaff = :CustIsStaff, CustStaffID = :CustStaffID, CustIndustry = :CustIndustry,");
		updateSql
				.append(" CustSector = :CustSector, CustSubSector = :CustSubSector, CustProfession = :CustProfession,");
		updateSql.append(
				" CustTotalIncome = :CustTotalIncome, CustMaritalSts = :CustMaritalSts, CustEmpSts = :CustEmpSts,");
		updateSql.append(
				" CustSegment = :CustSegment, CustSubSegment = :CustSubSegment, CustIsBlackListed = :CustIsBlackListed,");
		updateSql.append(
				" CustBLRsnCode = :CustBLRsnCode, CustIsRejected = :CustIsRejected, CustRejectedRsn = :CustRejectedRsn,");
		updateSql.append(
				" CustBaseCcy = :CustBaseCcy, CustLng = :CustLng, CustParentCountry = :CustParentCountry, CustResdCountry = :CustResdCountry,");
		updateSql.append(
				" CustRiskCountry = :CustRiskCountry, CustNationality = :CustNationality, CustClosedOn = :CustClosedOn,");
		updateSql.append(
				" CustStmtFrq = :CustStmtFrq, CustIsStmtCombined = :CustIsStmtCombined, CustStmtLastDate = :CustStmtLastDate,MarginDeviation = :MarginDeviation,");
		updateSql.append(" CustStmtNextDate = :CustStmtNextDate, CustStmtDispatchMode = :CustStmtDispatchMode,");
		updateSql.append(
				" CustFirstBusinessDate = :CustFirstBusinessDate, CustAddlVar81 = :CustAddlVar81, CustAddlVar82 = :CustAddlVar82,");
		updateSql.append(
				" CustAddlVar83 = :CustAddlVar83, CustAddlVar84 = :CustAddlVar84, CustAddlVar85 = :CustAddlVar85,");
		updateSql.append(
				" CustAddlVar86 = :CustAddlVar86, CustAddlVar87 = :CustAddlVar87, CustAddlVar88 = :CustAddlVar88,");
		updateSql.append(
				" CustAddlVar89 = :CustAddlVar89, CustAddlDate1 = :CustAddlDate1, CustAddlDate2 = :CustAddlDate2,");
		updateSql.append(
				" CustAddlDate3 = :CustAddlDate3, CustAddlDate4 = :CustAddlDate4, CustAddlDate5 = :CustAddlDate5,");
		updateSql.append(
				" CustAddlVar1 = :CustAddlVar1, CustAddlVar2 = :CustAddlVar2, CustAddlVar3 = :CustAddlVar3, CustAddlVar4 = :CustAddlVar4,");
		updateSql.append(
				" CustAddlVar5 = :CustAddlVar5, CustAddlVar6 = :CustAddlVar6, CustAddlVar7 = :CustAddlVar7, CustAddlVar8 = :CustAddlVar8,");
		updateSql.append(
				" CustAddlVar9 = :CustAddlVar9, CustAddlVar10 = :CustAddlVar10, CustAddlVar11 = :CustAddlVar11, CustAddlDec1 = :CustAddlDec1,");
		updateSql.append(
				" CustAddlDec2 = :CustAddlDec2, CustAddlDec3 = :CustAddlDec3, CustAddlDec4 = :CustAddlDec4, CustAddlDec5 = :CustAddlDec5,");
		updateSql.append(
				" CustAddlInt1 = :CustAddlInt1, CustAddlInt2 = :CustAddlInt2, CustAddlInt3 = :CustAddlInt3, CustAddlInt4 = :CustAddlInt4,");
		updateSql.append(
				" CustAddlInt5 = :CustAddlInt5,DedupFound=:DedupFound,SkipDedup=:SkipDedup,CustTotalExpense=:CustTotalExpense,");
		updateSql.append(
				" CustBlackListDate = :CustBlackListDate, NoOfDependents=:NoOfDependents,CustCRCPR=:CustCRCPR,CustSourceID=:CustSourceID,");
		updateSql.append(" JointCust = :JointCust, JointCustName = :JointCustName, JointCustDob = :JointCustDob,");
		updateSql.append(
				" ContactPersonName = :ContactPersonName, EmailID = :EmailID, PhoneNumber = :PhoneNumber, SalariedCustomer = :SalariedCustomer,");
		updateSql.append(
				" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		updateSql.append(
				" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(
				" ,CasteId = :CasteId, ReligionId = :ReligionId, SubCategory = :SubCategory, ApplicationNo = :ApplicationNo, Dnd = :Dnd ");
		updateSql.append(" Where CustID =:CustID");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public boolean isDuplicateCif(long custId, String cif) {
		logger.debug(Literal.ENTERING);

		boolean exists = false;

		// Prepare the parameter source.
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("CustID", custId);
		paramSource.addValue("CustCIF", cif);

		// Check whether the document id exists for another customer.
		String sql = QueryUtil.getCountQuery(new String[] { "Customers_Temp", "Customers" },
				"CustID != :CustID and CustCIF = :CustCIF");

		logger.trace(Literal.SQL + sql);
		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	public Customer getCustomerByCIF(String cifId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = selectCustomerBasicInfo(type);
		sql.append(" Where CustCIF = ?");

		logger.trace(Literal.SQL + sql.toString());

		CustomerRowMapper rowMapper = new CustomerRowMapper(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { cifId }, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	private StringBuilder selectCustomerBasicInfo(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustID, CustCIF, CustFName, CustMName, CustLName, CustDOB, CustShrtName, CustCRCPR");
		sql.append(", CustPassportNo, CustCtgCode, CustNationality, CustDftBranch, Version, CustBaseCcy");
		sql.append(", PhoneNumber, EmailID, CustRO1, CasteId, ReligionId, SubCategory");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescCustStsName, CasteCode, CasteDesc, ReligionCode, ReligionDesc");
		}

		sql.append(" from Customers");
		sql.append(StringUtils.trimToEmpty(type));
		return sql;
	}

	public Customer checkCustomerByCIF(String cif, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustID, CustCIF");
		sql.append(" from Customers");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustCIF = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { cif }, new RowMapper<Customer>() {
				@Override
				public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
					Customer c = new Customer();

					c.setCustID(rs.getLong("CustID"));
					c.setCustCIF(rs.getString("CustCIF"));

					return c;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	/**
	 * fetch customer and Employee details
	 * 
	 * @return WIFCustomer
	 */
	public WIFCustomer getWIFCustomerByCIF(long cifId, String type) {
		logger.debug("Entering");
		WIFCustomer wifCustomer = new WIFCustomer();
		wifCustomer.setCustID(cifId);

		StringBuilder selectSql = new StringBuilder(
				"SELECT T1.CustCRCPR, T1.CustFName, T1.CustShrtName, T1.CustTypeCode, T1.CustCtgCode, T1.CustDOB, T1.custNationality,");
		selectSql.append(
				" T1.custGenderCode, T1.custSalutationCode, T1.custMaritalSts, T1.CustEmpSts,T1.CustTotalIncome as TotalIncome,T1.custTotalExpense as TotalExpense,T1.CustBaseCcy,T1.CustSubSector,");
		selectSql.append(
				" T1.NoOfDependents,T1.SalariedCustomer,T1.LovDescCustMaritalStsName,T1.LovDescCustCtgCodeName,T1.LovDescCustTypeCodeName,T1.LovDescCustNationalityName,T1.LovDescCustEmpStsName ");
		selectSql.append(" FROM  Customers_View  T1  ");
		selectSql.append(" Where T1.CustID=:CustID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(wifCustomer);
		RowMapper<WIFCustomer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(WIFCustomer.class);

		try {
			wifCustomer = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			wifCustomer = null;
		}
		logger.debug("Leaving");
		return wifCustomer;
	}

	@Override
	public String getNewProspectCustomerCIF() {
		return String.valueOf(getNextValue("SeqProspectCustomer"));
	}

	@Override
	public List<FinanceProfitDetail> getCustFinAmtDetails(long custId, CustomerEligibilityCheck eligibilityCheck) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinCcy, TotalPriBal, TotalPftBal, ODProfit, ODPrincipal");
		sql.append(" from FinPftDetails");
		sql.append(" where CustID = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setLong(index++, custId);
				}
			}, new RowMapper<FinanceProfitDetail>() {
				@Override
				public FinanceProfitDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinanceProfitDetail fpd = new FinanceProfitDetail();

					fpd.setFinCcy(rs.getString("FinCcy"));
					fpd.setTotalPriBal(rs.getBigDecimal("TotalPriBal"));
					fpd.setTotalPftBal(rs.getBigDecimal("TotalPftBal"));
					fpd.setODProfit(rs.getBigDecimal("ODProfit"));
					fpd.setODPrincipal(rs.getBigDecimal("ODPrincipal"));

					return fpd;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public String getCustEmpDesg(long custID) {
		logger.debug("Entering");

		String custEmpDesg = "";
		CustomerEmploymentDetail detail = new CustomerEmploymentDetail();
		detail.setCustID(custID);

		StringBuilder selectSql = new StringBuilder(" SELECT CustEmpDesg ");
		selectSql.append(" FROM  CustomerEmpDetails ");
		selectSql.append(" WHERE CustID=:CustID AND CurrentEmployer = 1 ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);

		try {
			custEmpDesg = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			custEmpDesg = "";
		}
		detail = null;
		logger.debug("Leaving");
		return custEmpDesg == null ? "" : custEmpDesg;
	}

	@Override
	public String getCustCurEmpAlocType(long custID) {
		logger.debug("Entering");

		String custCurEmpAloctype = "";
		CustomerEmploymentDetail detail = new CustomerEmploymentDetail();
		detail.setCustID(custID);

		StringBuilder selectSql = new StringBuilder(" SELECT EmpAlocationType ");
		selectSql.append(" FROM  CustomerEmpDetails_AView ");
		selectSql.append(" WHERE CustID=:CustID AND CurrentEmployer = 1 ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);

		try {
			custCurEmpAloctype = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			custCurEmpAloctype = "";
		}
		detail = null;
		logger.debug("Leaving");
		return custCurEmpAloctype == null ? "" : custCurEmpAloctype;
	}

	@Override
	public BigDecimal getCustRepayOtherTotal(long custID) {
		logger.debug("Entering");

		BigDecimal custRepayOther = BigDecimal.ZERO;
		CustomerIncome detail = new CustomerIncome();
		detail.setCustId(custID);

		StringBuilder selectSql = new StringBuilder(" SELECT CustRepayOther ");
		selectSql.append(" FROM  CustOthExpense_View ");
		selectSql.append(" WHERE CustID=:CustID ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);

		try {
			custRepayOther = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			custRepayOther = BigDecimal.ZERO;
		}
		detail = null;
		logger.debug("Leaving");
		return custRepayOther;
	}

	@Override
	public BigDecimal getCustRepayBankTotal(long custID) {
		logger.debug("Entering");
		List<CustomerIncome> CustomerIncomeDetailsList = new ArrayList<CustomerIncome>();

		BigDecimal custRepayBank = BigDecimal.ZERO;
		CustomerIncome detail = new CustomerIncome();
		detail.setCustId(custID);

		StringBuilder selectSql = new StringBuilder("Select CustId,TotalRepayAmt,MaturityDate,FinStartDate,FinCcy,");
		selectSql.append("'" + SysParamUtil.getAppCurrency() + "'" + " toCcy");
		selectSql.append(" FROM FinanceMain WHERE FinIsActive = 1 and CustID=:CustId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		RowMapper<CustomerIncome> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerIncome.class);

		try {
			CustomerIncomeDetailsList = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			CustomerIncomeDetailsList = null;
		}
		if (CustomerIncomeDetailsList != null && !CustomerIncomeDetailsList.isEmpty()) {
			for (CustomerIncome customerIncome : CustomerIncomeDetailsList) {
				int months = DateUtility.getMonthsBetween(customerIncome.getFinStartDate(),
						customerIncome.getMaturityDate());
				if (months == 0) {
					custRepayBank = custRepayBank.add(CalculationUtil.getConvertedAmount(customerIncome.getFinCcy(),
							customerIncome.getToCcy(), customerIncome.getTotalRepayAmt()));
				} else {
					custRepayBank = custRepayBank.add(CalculationUtil.getConvertedAmount(customerIncome.getFinCcy(),
							customerIncome.getToCcy(),
							customerIncome.getTotalRepayAmt().divide(new BigDecimal(months), RoundingMode.HALF_UP)));
				}

			}
			custRepayBank = PennantApplicationUtil.formateAmount(custRepayBank,
					CurrencyUtil.getFormat(SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY)));
		}

		detail = null;
		logger.debug("Leaving");

		return custRepayBank;

	}

	@Override
	public BigDecimal getCustRepayProcBank(long custID, String curFinReference) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("select CustId CustCif, TotalRepayAmt, MaturityDate, FinStartDate,");
		sql.append(" FinCcy");
		sql.append(" from FinanceMain_Temp where CustID = :CustID and RcdMaintainSts is null");
		sql.append(" and FinReference <> :FinReference");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("CustID", custID);
		paramSource.addValue("FinReference", curFinReference);

		RowMapper<FinanceExposure> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceExposure.class);

		List<FinanceExposure> financeExposures = new ArrayList<>();
		try {
			financeExposures = this.jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		String toCcy = SysParamUtil.getAppCurrency();
		BigDecimal totalRepayAmt = BigDecimal.ZERO;
		BigDecimal repayAmt;
		int months;

		for (FinanceExposure finExposure : financeExposures) {
			months = DateUtility.getMonthsBetween(finExposure.getFinStartDate(), finExposure.getMaturityDate(), true);
			repayAmt = finExposure.getTotalRepayAmt();

			if (months > 0) {
				repayAmt = repayAmt.divide(new BigDecimal(months), RoundingMode.HALF_UP);
			}

			totalRepayAmt = totalRepayAmt
					.add(CalculationUtil.getConvertedAmount(finExposure.getFinCCY(), toCcy, repayAmt));
		}

		totalRepayAmt = PennantApplicationUtil.formateAmount(totalRepayAmt,
				CurrencyUtil.getFormat(SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY)));

		logger.debug(Literal.LEAVING);
		return totalRepayAmt;
	}

	@Override
	public FinanceExposure getCoAppRepayBankTotal(String custCIF) {
		logger.debug("Entering");

		FinanceExposure exposure = null;
		Customer detail = new Customer();
		detail.setCustCIF(custCIF);

		StringBuilder selectSql = new StringBuilder(" SELECT CustBaseCcy FinCCY, CustTotalIncome FinanceAmt, ");
		selectSql.append(
				" CustTotalExpense OverdueAmt, CustRepayBank CurrentExpoSure, CustProcBank CurrentExpoSureinBaseCCY  ");
		selectSql.append(" FROM  CoAppBankExpense_View WHERE CustCIF=:CustCIF ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		RowMapper<FinanceExposure> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceExposure.class);

		try {
			exposure = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			exposure = null;
		}
		detail = null;
		logger.debug("Leaving");
		return exposure;
	}

	@Override
	public String getCustWorstSts(long custID) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" t1.CustStsCode");
		sql.append(" from BMTCustStatusCodes t1");
		sql.append(" inner join (");
		sql.append(" Select max(DueDays) MaxDays from FinanceMain f");

		if (App.DATABASE == Database.SQL_SERVER) {
			sql.append(" with(nolock)");
		}

		sql.append(", BMTCustStatusCodes s");
		sql.append(" Where f.finStatus = s.CustStsCode and f.CustID = ?");
		sql.append(") t2 on t1.DueDays = t2.MaxDays");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { custID }, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public String getCustWorstStsbyCurFinSts(long custID, String finReference, String curFinSts) {
		logger.debug("Entering");

		String custWorstSts = "";
		FinanceMain main = new FinanceMain();
		main.setCustID(custID);
		main.setFinReference(finReference);
		main.setFinStatus(curFinSts);

		StringBuilder selectSql = new StringBuilder(" Select CustStsCode from BMTCustStatusCodes ");
		selectSql.append(
				" WHERE DueDays= (Select MAX(MaxODDays)MaxDays from (select MAX(DueDays)MaxODDays from FinanceMain F ");
		if (App.DATABASE == Database.SQL_SERVER) {
			selectSql.append(" WITH(NOLOCK) ");
		}
		selectSql.append(", BMTCustStatusCodes S  ");
		selectSql.append(
				" WHERE F.FinStatus = S.CustStsCode and F.CustID = :CustID and F.FinReference <> :FinReference	UNION ");
		selectSql.append(" Select DueDays from BMTCustStatusCodes where CustStsCode=:FinStatus ) T )  ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(main);

		try {
			custWorstSts = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			custWorstSts = "";
		}
		main = null;
		logger.debug("Leaving");
		return custWorstSts == null ? "" : custWorstSts;
	}

	@Override
	public String getCustWorstStsDesc(long custID) {
		logger.debug("Entering");

		String custWorstSts = "";
		CustomerEmploymentDetail detail = new CustomerEmploymentDetail();
		detail.setCustID(custID);

		StringBuilder selectSql = new StringBuilder(" SELECT T1.CustStsDescription FROM BMTCustStatusCodes T1 ");
		selectSql.append(" INNER JOIN (SELECT MAX(DueDays) MaxDays from FinanceMain F ");
		if (App.DATABASE == Database.SQL_SERVER) {
			selectSql.append(" WITH(NOLOCK) ");
		}
		selectSql.append(
				" , BMTCustStatusCodes S  WHERE F.FinStatus = S.CustStsCode and F.CustID = :CustID)T2 ON T1.DueDays=T2.MaxDays  ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);

		try {
			custWorstSts = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			custWorstSts = "";
		}
		logger.debug("Leaving");
		return custWorstSts == null ? "" : custWorstSts;
	}

	/**
	 * Method for Checking JOint Customer Existence in Customer Details Data
	 */
	@Override
	public boolean isJointCustExist(long custID) {
		logger.debug("Entering");

		boolean jointCustExist = false;
		Customer detail = new Customer();
		detail.setCustID(custID);

		StringBuilder selectSql = new StringBuilder(" SELECT JointCust ");
		selectSql.append(" FROM  Customers_AView ");
		selectSql.append(" WHERE CustID=:CustID ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);

		try {
			jointCustExist = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Boolean.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			jointCustExist = false;
		}

		detail = null;
		logger.debug("Leaving");
		return jointCustExist;
	}

	@Override
	public long saveWIFCustomer(WIFCustomer customer) {

		logger.debug("Entering");

		if (customer.getCustID() == 0 || customer.getCustID() == Long.MIN_VALUE) {
			customer.setCustID(getNextValue("SeqWIFCustomer"));
		}

		StringBuilder insertSql = new StringBuilder("Insert Into WIFCustomers");
		insertSql.append(
				" (CustID , CustCRCPR , CustCtgCode , CustTypeCode , CustShrtName , CustGenderCode , CustDOB , ");
		insertSql.append(
				" CustSector , CustSubSector , CustMaritalSts , CustEmpSts , CustIsBlackListed , CustBlackListDate , ");
		insertSql.append(" NoOfDependents , CustBaseCcy , CustNationality , JointCust, ExistCustID, ElgRequired, ");
		insertSql.append(
				" SalariedCustomer,EmpName,EmpDept,EmpDesg,TotalIncome,TotalExpense,CustSalutationCode,CustSegment)");
		insertSql.append(
				" VALUES (:CustID , :CustCRCPR , :CustCtgCode , :CustTypeCode , :CustShrtName , :CustGenderCode , :CustDOB , ");
		insertSql.append(
				" :CustSector , :CustSubSector , :CustMaritalSts , :CustEmpSts , :CustIsBlackListed , :CustBlackListDate , ");
		insertSql
				.append(" :NoOfDependents , :CustBaseCcy , :CustNationality ,:JointCust, :ExistCustID, :ElgRequired ,");
		insertSql.append(
				" :SalariedCustomer,:EmpName,:EmpDept,:EmpDesg,:TotalIncome,:TotalExpense,:CustSalutationCode,:CustSegment)");
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return customer.getCustID();

	}

	@Override
	public void updateWIFCustomer(WIFCustomer customer) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder(" Update WIFCustomers");
		updateSql.append(" Set CustCRCPR=:CustCRCPR , CustCtgCode=:CustCtgCode , CustTypeCode=:CustTypeCode , ");
		updateSql.append(
				" CustShrtName=:CustShrtName , CustGenderCode=:CustGenderCode , CustDOB=:CustDOB , CustSector=:CustSector , ");
		updateSql.append(" CustSubSector=:CustSubSector , CustMaritalSts=:CustMaritalSts , CustEmpSts=:CustEmpSts , ");
		updateSql.append(
				" CustIsBlackListed=:CustIsBlackListed , CustBlackListDate=:CustBlackListDate , NoOfDependents=:NoOfDependents , ");
		updateSql.append(
				" CustBaseCcy=:CustBaseCcy , CustNationality=:CustNationality , JointCust=:JointCust , ExistCustID=:ExistCustID, ElgRequired=:ElgRequired ,");
		updateSql.append(
				" SalariedCustomer=:SalariedCustomer,EmpName=:EmpName,EmpDept=:EmpDept,EmpDesg=:EmpDesg,TotalIncome=:TotalIncome,");
		updateSql.append("TotalExpense=:TotalExpense,CustSalutationCode=:CustSalutationCode,CustSegment=:CustSegment");
		updateSql.append(" WHERE CustID=:CustID ");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");

	}

	/**
	 * Fetch the Record Customers details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Customer
	 */
	@Override
	public WIFCustomer getWIFCustomerByID(final long custId, String custCRCPR, String type) {
		logger.debug("Entering");
		WIFCustomer customer = new WIFCustomer();
		customer.setCustID(custId);
		customer.setCustCRCPR(custCRCPR);

		StringBuilder selectSql = new StringBuilder(
				" SELECT CustID , CustCtgCode , CustTypeCode , CustShrtName , CustGenderCode , ");
		selectSql.append(
				" CustDOB , CustSector , CustSubSector , CustMaritalSts , CustEmpSts , CustIsBlackListed , CustBlackListDate ,");
		selectSql.append(
				" NoOfDependents , CustBaseCcy , CustNationality , CustCRCPR , JointCust, ExistCustID, ElgRequired , ");
		selectSql.append(
				" SalariedCustomer,EmpName,EmpDept,EmpDesg,TotalIncome,TotalExpense,CustSalutationCode,CustSegment ");
		if (type.contains("View")) {
			selectSql.append(
					" ,lovDescCustTypeCodeName, lovDescCustMaritalStsName, lovDescCustEmpStsName, lovDescCustNationalityName, ");
			selectSql.append(
					" lovDescCustSectorName, lovDescCustSubSectorName, lovDescCustGenderCodeName, lovDescCustCtgCodeName, ");
			selectSql.append(" lovDescEmpName,lovDescEmpDept,lovDescEmpDesg,lovDescCustSegmentName ");
		}
		selectSql.append(" FROM WIFCustomers");
		selectSql.append(StringUtils.trimToEmpty(type));

		if (custCRCPR == null) {
			selectSql.append(" Where CustID =:CustID");
		} else {
			selectSql.append(" Where CustCRCPR =:CustCRCPR");
		}

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<WIFCustomer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(WIFCustomer.class);

		try {
			customer = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			customer = null;
		}
		logger.debug("Leaving");
		return customer;
	}

	/**
	 * Fetch the Record Customers details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Customer
	 */
	@Override
	public String getCustomerByCRCPR(final String custCRCPR, String type) {
		logger.debug("Entering");

		String custCIF = null;
		WIFCustomer customer = new WIFCustomer();
		customer.setCustCRCPR(custCRCPR);

		StringBuilder selectSql = new StringBuilder(" SELECT CustCIF ");
		selectSql.append(" FROM Customers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustCRCPR =:CustCRCPR");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);

		try {
			custCIF = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			custCIF = null;
		}
		logger.debug("Leaving");
		return custCIF;
	}

	/**
	 * Fetch the Record Customers details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Customer
	 */
	@Override
	public Date getCustBlackListedDate(final String custCRCPR, String type) {
		logger.debug("Entering");

		Date blackListedDate = null;
		Abuser abuser = new Abuser();
		abuser.setAbuserIDNumber(custCRCPR);

		StringBuilder selectSql = new StringBuilder(" SELECT AbuserExpDate ");
		selectSql.append(" FROM EQNAbuserList");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where AbuserIDNumber =:AbuserIDNumber");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(abuser);

		try {
			blackListedDate = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Date.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			blackListedDate = null;
		}
		logger.debug("Leaving");
		return blackListedDate;
	}

	public void updateProspectCustomer(Customer customer) {
		logger.debug("Entering");
		long custID = customer.getCustID();

		if (custID != 0) {
			StringBuilder updateSql = new StringBuilder(" Update Customers set CustCoreBank = :CustCoreBank ");
			updateSql.append(" where CustID = :custID ");
			updateCustID(updateSql.toString(), customer);
		}
		logger.debug("Leaving");
	}

	public void updateCustID(String updateSql, Customer customer) {
		logger.debug("Entering");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		this.jdbcTemplate.update(updateSql, beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public ProspectCustomer getProspectCustomer(String finReference, String type) {
		logger.debug("Entering");

		ProspectCustomer prospectCustomer = new ProspectCustomer();
		prospectCustomer.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(
				" SELECT CustID , CustCIF , CustShrtName, CustCtgCode, CustDftBranch  ");
		selectSql.append(" FROM WIFProspectCustomer");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(prospectCustomer);
		RowMapper<ProspectCustomer> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ProspectCustomer.class);

		try {
			prospectCustomer = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			prospectCustomer = null;
		}
		logger.debug("Leaving");
		return prospectCustomer;
	}

	public boolean isAvailableCustomer(final long id, String type) {
		logger.debug("Entering");
		Customer customer = new Customer();
		customer.setId(id);

		StringBuilder selectSql = new StringBuilder("SELECT CustID ");
		selectSql.append(" FROM  Customers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID =:CustID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);

		try {
			customer = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			customer = null;
		}
		logger.debug("Leaving");
		return customer == null ? false : true;
	}

	/**
	 * Method for Fetch Customer CR/CPR number For Checking Black listed data
	 */
	@Override
	public String getCustCRCPRById(long custId, String type) {
		logger.debug("Entering");

		String custCRCPR = "";
		Customer customer = new Customer();
		customer.setCustID(custId);

		StringBuilder selectSql = new StringBuilder("SELECT CustCRCPR ");
		selectSql.append(" FROM  Customers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID =:CustID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);

		try {
			custCRCPR = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			custCRCPR = "";
		}
		logger.debug("Leaving");
		return custCRCPR;
	}

	@Override
	public void updateFromFacility(Customer customer, String type) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update Customers");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(
				" Set CustCOB = :CustCOB, CustRiskCountry = :CustRiskCountry, CustDOB = :CustDOB, CustSector = :CustSector");
		updateSql.append(" Where CustID =:CustID");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public AvailPastDue getCustPastDueDetailByCustId(AvailPastDue pastDue, String limitCcy) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder(" select T1.CustID,  ");
		selectSql.append(" SUM([dbo].[UDF_ConvertCurrency](FinCurODAmt, T2.FinCcy, '");
		selectSql.append(limitCcy.trim());
		selectSql.append("')) PastDueAmount, MAX(FinCurODDays) DueDays ,MIN(FinODSchdDate) PastDueFrom ");
		selectSql.append(" FROM FinODDetails T1 INNER JOIN FinanceMain T2 ON T1.FinReference = T2.FinReference ");
		selectSql.append(" where FinCurODAmt>0 AND T1.CustID =:CustID Group By T1.CustID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(pastDue);
		RowMapper<AvailPastDue> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(AvailPastDue.class);

		try {
			pastDue = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			pastDue = null;
		}
		logger.debug("Leaving");
		return pastDue;
	}

	@Override
	public Customer getCustomerByID(final long id) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustCIF, CustID, CustGroupID, CustCtgCode, CustStsChgDate, CustShrtName, CustCRCPR");
		sql.append(", CustDftBranch, CasteId, ReligionId, SubCategory");
		sql.append(" from Customers");
		sql.append(" Where CustID = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { id }, new RowMapper<Customer>() {
				@Override
				public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
					Customer c = new Customer();

					c.setCustCIF(rs.getString("CustCIF"));
					c.setCustID(rs.getLong("CustID"));
					c.setCustGroupID(rs.getLong("CustGroupID"));
					c.setCustCtgCode(rs.getString("CustCtgCode"));
					c.setCustStsChgDate(rs.getTimestamp("CustStsChgDate"));
					c.setCustShrtName(rs.getString("CustShrtName"));
					c.setCustCRCPR(rs.getString("CustCRCPR"));
					c.setCustDftBranch(rs.getString("CustDftBranch"));
					c.setCasteId(rs.getLong("CasteId"));
					c.setReligionId(rs.getLong("ReligionId"));
					c.setSubCategory(rs.getString("SubCategory"));

					return c;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	/**
	 * Fetch the Customer Finance Details
	 * 
	 * @param curBD
	 * @param nextBD
	 * @return
	 */
	@Override
	public List<FinanceEnquiry> getCustomerFinanceDetailById(long custId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinReference, fm.FinType, fm.FinStatus, fm.FinStartDate, fm.FinCcy, fm.FinAmount");
		sql.append(", fm.DownPayment, fm.FeeChargeAmt, fm.FinCurrAssetValue, fm.InsuranceAmt");
		sql.append(", fm.FinRepaymentAmount, fm.NumberOfTerms, ft.FintypeDesc as LovDescFinTypeName");
		sql.append(", coalesce(t6.MaxinstAmount, 0) MaxInstAmount");
		sql.append(" from FinanceMain fm");
		sql.append(" inner join RMTfinanceTypes ft on ft.Fintype = fm.FinType");
		sql.append(" left join (select FinReference, (NSchdPri+NSchdPft) MaxInstAmount");
		sql.append(" from FinPftdetails) t6 on t6.FinReference = fm.Finreference");
		sql.append(" where CustID = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setLong(index++, custId);
				}
			}, new RowMapper<FinanceEnquiry>() {
				@Override
				public FinanceEnquiry mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinanceEnquiry fm = new FinanceEnquiry();

					fm.setFinReference(rs.getString("FinReference"));
					fm.setFinType(rs.getString("FinType"));
					fm.setFinStatus(rs.getString("FinStatus"));
					fm.setFinStartDate(rs.getTimestamp("FinStartDate"));
					fm.setFinCcy(rs.getString("FinCcy"));
					fm.setFinAmount(rs.getBigDecimal("FinAmount"));
					fm.setDownPayment(rs.getBigDecimal("DownPayment"));
					fm.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
					fm.setFinCurrAssetValue(rs.getBigDecimal("FinCurrAssetValue"));
					fm.setInsuranceAmt(rs.getBigDecimal("InsuranceAmt"));
					fm.setFinRepaymentAmount(rs.getBigDecimal("FinRepaymentAmount"));
					fm.setNumberOfTerms(rs.getInt("NumberOfTerms"));
					fm.setLovDescFinTypeName(rs.getString("LovDescFinTypeName"));
					fm.setMaxInstAmount(rs.getBigDecimal("MaxInstAmount"));

					return fm;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public boolean financeExistForCustomer(final long id, String type) {
		logger.debug("Entering");
		int count = 0;
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("CustID", id);

		StringBuilder selectSql = new StringBuilder("SELECT  COUNT(CustID)  FROM  FinanceMain");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :CustID");

		logger.debug("selectSql: " + selectSql.toString());
		try {
			count = this.jdbcTemplate.queryForObject(selectSql.toString(), mapSqlParameterSource, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			count = 0;
		}
		logger.debug("Leaving");
		return count > 0 ? true : false;
	}

	@Override
	public long getCustCRCPRByCustId(String custCRCPR, String type) {
		logger.debug("Entering");

		Long custID = null;
		WIFCustomer customer = new WIFCustomer();
		customer.setCustCRCPR(custCRCPR);

		StringBuilder selectSql = new StringBuilder(" SELECT CustId ");
		selectSql.append(" FROM Customers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustCRCPR =:CustCRCPR");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);

		try {
			custID = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			custID = Long.valueOf(0);
		}
		logger.debug("Leaving");
		return custID;
	}

	@Override
	public WIFCustomer getWIFByCustCRCPR(String custCRCPR, String type) {
		logger.debug("Entering");

		WIFCustomer customer = new WIFCustomer();
		customer.setCustCRCPR(custCRCPR);

		StringBuilder selectSql = new StringBuilder(" SELECT * ");
		selectSql.append(" FROM WIFCustomers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustCRCPR =:CustCRCPR");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<WIFCustomer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(WIFCustomer.class);
		try {
			customer = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			customer = null;
		}
		logger.debug("Leaving");
		return customer;
	}

	@Override
	public boolean isDuplicateCrcpr(long custId, String custCRCPR) {
		logger.debug(Literal.ENTERING);

		boolean exists = false;

		// Prepare the parameter source.
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("CustID", custId);
		paramSource.addValue("CustCRCPR", custCRCPR);

		// Check whether the document id exists for another customer.
		String sql = QueryUtil.getCountQuery(new String[] { "Customers_Temp", "Customers" },
				"CustID != :CustID and CustCRCPR = :CustCRCPR");

		logger.trace(Literal.SQL + sql);
		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	/**
	 * Method for check OldProspectCIF exists in other tables and update with newCIF
	 * 
	 * @param oldCustCIF
	 * @param newCustCIF
	 */
	@Override
	public void updateProspectCustCIF(String oldCustCIF, String newCustCIF) {
		logger.debug("Entering");

		if (!StringUtils.isBlank(oldCustCIF) && !StringUtils.isBlank(newCustCIF)) {

			MapSqlParameterSource source = new MapSqlParameterSource();
			source.addValue("OldCustCIF", oldCustCIF);
			source.addValue("NewCustCIF", newCustCIF);

			StringBuilder updateSql = new StringBuilder(
					" Update Customers set CustCIF =:NewCustCIF, CustCoreBank =:NewCustCIF");
			if (isExistsProspectCIF("Customers", oldCustCIF)) {
				updateSql.append(" where CustCIF =:OldCustCIF ");
				updateCustCIF(updateSql.toString(), source);
			}
			updateSql.delete(0, updateSql.length());

			updateSql = new StringBuilder(" Update FinBlackListDetail set CustCIF =:NewCustCIF ");
			if (isExistsProspectCIF("FinBlackListDetail", oldCustCIF)) {
				updateSql.append(" where CustCIF = :OldCustCIF ");
				updateCustCIF(updateSql.toString(), source);
			}
			updateSql.delete(0, updateSql.length());

			if (isExistsProspectCIF("CustomerDedupDetail", oldCustCIF)) {
				updateSql.append(" Update CustomerDedupDetail set CustCIF =:NewCustCIF ");
				updateSql.append(" where CustCIF = :OldCustCIF ");
				updateCustCIF(updateSql.toString(), source);
			}
			updateSql.delete(0, updateSql.length());

			if (isExistsProspectCIF("FinDedupDetail", oldCustCIF)) {
				updateSql.append(" Update FinDedupDetail set CustCIF =:NewCustCIF ");
				updateSql.append(" where CustCIF = :OldCustCIF ");
				updateCustCIF(updateSql.toString(), source);
			}
			updateSql.delete(0, updateSql.length());

			logger.debug("Leaving");
		}

	}

	/**
	 * Method for Check Is ProspectCustCIF Exists or not with old CustCIF
	 * 
	 * @param tableName
	 * @param oldCustCIF
	 * @return
	 */
	private boolean isExistsProspectCIF(String tableName, String oldCustCIF) {
		logger.debug("Entering");

		List<String> objList = null;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustCIF", oldCustCIF);

		StringBuilder selectSql = new StringBuilder("SELECT CustCIF FROM ");
		selectSql.append(tableName);
		selectSql.append(" WHERE CustCIF=:CustCIF");

		logger.debug("selectSql: " + selectSql.toString());

		logger.debug("Leaving");

		try {
			objList = this.jdbcTemplate.queryForList(selectSql.toString(), source, String.class);
			if (objList != null && !objList.isEmpty()) {
				return true;
			}
		} catch (EmptyResultDataAccessException ex) {
			logger.warn("Exception: ", ex);
			return false;
		}
		return false;
	}

	/**
	 * Method for Update Corebank CustCIF for Prospect Customer
	 * 
	 * @param updateSql
	 * @param source
	 */
	private void updateCustCIF(String updateSql, MapSqlParameterSource source) {
		logger.debug("Entering");
		this.jdbcTemplate.update(updateSql, source);
		logger.debug("Leaving");
	}

	/**
	 * Method for fetch Customer core bank id
	 * 
	 * @param CustCIF
	 * @return String
	 */
	@Override
	public String getCustCoreBankIdByCIF(String custCIF) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustCIF", custCIF);

		StringBuilder selectSql = new StringBuilder("SELECT CustCoreBank ");
		selectSql.append(" FROM  Customers");
		selectSql.append(" Where CustCIF=:CustCIF");

		logger.debug("selectSql: " + selectSql.toString());

		logger.debug("Leaving");
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			return null;
		}
	}

	/**
	 * Method for generate new CustCIF for core bank
	 */
	@Override
	public String getNewCoreCustomerCIF() {
		logger.debug("Entering");
		// FIXME murthy
		// String coreCustCIF =
		// String.valueOf(getNextidviewDAO().getNextId("SeqCorebankCustomer"));

		logger.debug("Leaving");
		// return StringUtils.leftPad(coreCustCIF, 7, "0");
		return "";
	}

	@Override
	public void updateCorebankCustCIF(String coreCustCIF) {
		logger.debug("Entering");
		// FIXME murthy
		// getNextidviewDAO().setSeqNumber("SeqCorebankCustomer",
		// (Long.parseLong(coreCustCIF)) - 1);

		logger.debug("Leaving");
	}

	@Override
	public void updateCustSuspenseDetails(Customer aCustomer, String tableType) {
		logger.debug("Entering");

		StringBuffer updateSql = new StringBuffer();
		updateSql.append("UPDATE Customers");
		updateSql.append(tableType);
		updateSql.append(
				" SET CustSuspSts =:CustSuspSts, CustSuspDate =:CustSuspDate, CustSuspTrigger =:CustSuspTrigger,");
		updateSql.append(
				" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		updateSql.append(
				" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" WHERE CustID =:CustID");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(aCustomer);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public void saveCustSuspMovements(Customer aCustomer) {
		logger.debug("Entering");

		StringBuffer insertSql = new StringBuffer();
		insertSql.append("INSERT INTO CustSuspMovements ");
		insertSql.append("(CustID, CustSuspEffDate, CustSuspAprDate, CustSuspMvtType, CustSuspRemarks) ");
		insertSql.append(" VALUES(:CustID, :CustSuspEffDate, :CustSuspAprDate, :CustSuspMvtType, :CustSuspRemarks) ");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(aCustomer);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public String getCustSuspRemarks(long custID) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustID", custID);

		StringBuffer selectSql = new StringBuffer();
		selectSql.append(" Select T1.CustSuspRemarks FROM CustSuspMovements T1 INNER JOIN ");
		selectSql.append(
				" (Select CustID,MAX(CustSuspEffDate) MaxSuspEffDate FROM CustSuspMovements Group by CustID) T2 ");
		selectSql.append(" ON T1.CustID =T2.CustID and T1.CustSuspEffDate =T2.MaxSuspEffDate where T1.CustID=:CustID");

		logger.debug("insertSql: " + selectSql.toString());

		logger.debug("Leaving");
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return null;
		}

	}

	@Override
	public Customer getSuspendCustomer(Long custID) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustID", custID);

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT CustID, CustCIF, CustShrtName, CustDftBranch, CustSts, CustStsChgDate, custSuspSts,");
		selectSql.append(" CasteId, ReligionId, SubCategory,");
		selectSql.append(" custSuspDate, custSuspTrigger From Customers ");
		selectSql.append(" Where CustID = :CustID AND custSuspTrigger = 'M'");

		logger.debug("insertSql: " + selectSql.toString());

		logger.debug("Leaving");
		try {
			RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return null;
		}

	}

	@Override
	public ArrayList<Customer> getCustomerByLimitRule(String queryCode, String sqlQuery) {
		ArrayList<Customer> custList = new ArrayList<Customer>();

		logger.debug("insertSql: " + queryCode);

		logger.debug("Leaving");
		try {
			RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);
			custList = (ArrayList<Customer>) this.jdbcTemplate.query(queryCode, typeRowMapper);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return null;
		}

		return custList;
	}

	/**
	 * Method for get total number of records from specified master table.<br>
	 * 
	 * @param tableName
	 * @param columnName
	 * @param value
	 * 
	 * @return int
	 */
	@Override
	public int getLookupCount(String tableName, String columnName, Object value) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ColumnName", columnName);
		source.addValue("Value", value);

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT COUNT(*) FROM ");
		selectSql.append(tableName);
		selectSql.append(" WHERE ");
		selectSql.append(columnName);
		selectSql.append("= :Value");

		logger.debug("insertSql: " + selectSql.toString());
		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			recordCount = 0;
		}
		logger.debug("Leaving");

		return recordCount;
	}

	/**
	 * Get customer count by cust CIF.
	 * 
	 * @param custCIF
	 * @param type
	 * @return Integer
	 */
	@Override
	public int getCustomerCountByCIF(String custCIF, String type) {
		logger.debug("Entering");
		Customer customer = new Customer();
		customer.setCustCIF(custCIF);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT COUNT(*) FROM Customers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE CustCIF = :CustCIF");

		logger.debug("SelectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);

		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception :", e);
			recordCount = 0;
		}

		logger.debug("Leaving");
		return recordCount;
	}

	/**
	 * Get Customer Core Bank Id
	 * 
	 * @param custCoreBank
	 */
	public boolean getCustomerByCoreBankId(String custCoreBank) {
		logger.debug("Entering");
		Customer customer = new Customer();
		customer.setCustCoreBank(custCoreBank);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT COUNT(*) FROM Customers ");
		selectSql.append(" WHERE CustCoreBank = :CustCoreBank");

		logger.debug("SelectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);

		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception :", e);
			recordCount = 0;
		}

		logger.debug("Leaving");
		return recordCount > 0 ? true : false;
	}

	@Override
	public void updateCustStatus(String custStatus, Date statusChgdate, long custId) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustSts", custStatus);
		source.addValue("CustStsChgDate", statusChgdate);
		source.addValue("CustId", custId);
		StringBuilder selectSql = new StringBuilder("Update Customers  ");
		selectSql.append(" Set CustSts = :CustSts, CustStsChgDate= :CustStsChgDate WHERE CustId=:CustId ");
		logger.debug("selectSql: " + selectSql.toString());

		logger.debug("Leaving");
		this.jdbcTemplate.update(selectSql.toString(), source);
	}

	@Override
	public Customer getCustomerStatus(long custId) {

		logger.debug("Entering");
		Customer customer = new Customer();
		customer.setId(custId);

		StringBuilder selectSql = new StringBuilder("SELECT  CustSts");
		selectSql.append(" FROM  Customers");
		selectSql.append(" Where CustID =:CustID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);

		try {
			customer = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			customer = null;
		}
		logger.debug("Leaving");
		return customer;

	}

	public Customer getCustomerEOD(final long id) {
		Customer customer = new Customer();
		customer.setId(id);

		StringBuilder selectSql = new StringBuilder("SELECT CustID, CustCIF, CustCoreBank, CustCtgCode, ");
		selectSql.append(" CustTypeCode, CustDftBranch, CustPOB, CustCOB, CustGroupID,  ");
		selectSql.append(" CustSts, CustStsChgDate, CustIsStaff, CustIndustry, CustSector, CustSubSector, ");
		selectSql.append(" CustEmpSts, CustSegment, CustSubSegment, CustAppDate, ");
		selectSql.append(" CustParentCountry, CustResdCountry, CustRiskCountry, CustNationality, ");
		selectSql.append(" SalariedCustomer, custSuspSts,custSuspDate, custSuspTrigger ");
		selectSql.append(" , CasteId, ReligionId, SubCategory, CustShrtName");

		selectSql.append(" FROM  Customers");
		selectSql.append(" Where CustID =:CustID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);

		try {
			customer = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			customer = null;
		}
		return customer;
	}

	@Override
	public void updateCustAppDate(long custId, Date custAppDate, String newCustStatus) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustAppDate", custAppDate);
		source.addValue("CustId", custId);

		StringBuilder selectSql = new StringBuilder("Update Customers  ");
		selectSql.append(" Set CustAppDate = :CustAppDate ");

		if (newCustStatus != null) {
			source.addValue("NewCustStatus", newCustStatus);
			selectSql.append(", CustSts = :NewCustStatus ");
		}

		selectSql.append(" WHERE CustId=:CustId ");
		logger.debug("selectSql: " + selectSql.toString());

		logger.debug("Leaving");
		this.jdbcTemplate.update(selectSql.toString(), source);
	}

	@Override
	public Date getCustAppDate(long custId) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustId", custId);
		StringBuilder selectSql = new StringBuilder("select CustAppDate from Customers");
		selectSql.append(" WHERE CustId=:CustId ");
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Date.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			return null;
		}
	}

	/**
	 * Fetch the Record Customers details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Customer
	 */
	@Override
	public List<Customer> getCustomerByGroupID(final long custGroupID) {
		logger.debug("Entering");
		Customer customer = new Customer();
		customer.setCustGroupID(custGroupID);

		StringBuilder selectSql = new StringBuilder("SELECT CustID, CustCIF, CustCoreBank, CustCtgCode, CustTypeCode,");
		selectSql.append(
				" CustSalutationCode, CustFName, CustMName, CustLName, CustShrtName, CustFNameLclLng, CustMNameLclLng,");
		selectSql.append(
				" CustLNameLclLng, CustShrtNameLclLng, CustDftBranch, CustGenderCode, CustDOB, CustPOB, CustCOB,");
		selectSql.append(
				" CustPassportNo, CustMotherMaiden, CustIsMinor, CustReferedBy, CustDSA, CustDSADept, CustRO1, CustRO2,");
		selectSql.append(
				" CustGroupID, CustSts, CustStsChgDate, CustGroupSts, CustIsBlocked, CustIsActive, CustIsClosed,");
		selectSql.append(
				" CustInactiveReason, CustIsDecease, CustIsDormant, CustIsDelinquent, CustIsTradeFinCust, CustIsStaff,");
		selectSql.append(
				" CustTradeLicenceNum , CustTradeLicenceExpiry, CustPassportExpiry, CustVisaNum , CustVisaExpiry,");
		selectSql.append(
				" CustStaffID, CustIndustry, CustSector, CustSubSector, CustProfession, CustTotalIncome, CustMaritalSts,");
		selectSql.append(
				" CustEmpSts, CustSegment, CustSubSegment, CustIsBlackListed, CustBLRsnCode, CustIsRejected, CustRejectedRsn,");
		selectSql.append(
				" CustBaseCcy, CustLng, CustParentCountry, CustResdCountry, CustRiskCountry, CustNationality, CustClosedOn, ");
		selectSql.append(
				"CustStmtFrq, CustIsStmtCombined, CustStmtLastDate, CustStmtNextDate, CustStmtDispatchMode, CustFirstBusinessDate,");
		selectSql.append(
				" CustAddlVar81, CustAddlVar82, CustAddlVar83, CustAddlVar84, CustAddlVar85, CustAddlVar86, CustAddlVar87,");
		selectSql.append(
				" CustAddlVar88, CustAddlVar89, CustAddlDate1, CustAddlDate2, CustAddlDate3, CustAddlDate4, CustAddlDate5,");
		selectSql.append(
				" CustAddlVar1, CustAddlVar2, CustAddlVar3, CustAddlVar4, CustAddlVar5, CustAddlVar6, CustAddlVar7, CustAddlVar8, ");
		selectSql.append(
				" CustAddlVar9, CustAddlVar10, CustAddlVar11, CustAddlDec1, CustAddlDec2, CustAddlDec3, CustAddlDec4, CustAddlDec5,");
		selectSql.append(
				" CustAddlInt1, CustAddlInt2, CustAddlInt3, CustAddlInt4, CustAddlInt5,DedupFound,SkipDedup,CustTotalExpense,");
		selectSql.append(" CustBlackListDate,NoOfDependents,CustCRCPR,CustSourceID,");
		selectSql.append(
				" JointCust, JointCustName, JointCustDob, custRelation, ContactPersonName, EmailID, PhoneNumber,");
		selectSql.append(" SalariedCustomer, custSuspSts,custSuspDate, custSuspTrigger ");
		selectSql.append(" , CasteId, ReligionId, SubCategory, ApplicationNo, Dnd ");
		selectSql.append(" FROM  Customers");
		selectSql.append(" Where CustGroupID =:CustGroupID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);
		List<Customer> list = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		logger.debug("Leaving");
		return list;
	}

	@Override
	public int updateCustCRCPR(String custDocTitle, long custID) {
		int recordCount = 0;
		logger.debug("Entering");
		Customer customer = new Customer();
		customer.setCustCRCPR(custDocTitle);
		customer.setCustID(custID);
		StringBuilder updateSql = new StringBuilder("Update Customers");
		updateSql.append(" Set CustCRCPR=:CustCRCPR");
		updateSql.append(" Where CustID =:CustID");
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
		return recordCount;

	}

	/**
	 * Method for validating customers in Customer Group
	 * 
	 */
	@Override
	public boolean customerExistingCustGrp(long custGrpID, String type) {
		logger.debug("Entering");

		int count = 0;
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("CustGroupID", custGrpID);

		StringBuilder selectSql = new StringBuilder("SELECT  COUNT(CustGroupID)  FROM  Customers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustGroupID = :CustGroupID");

		logger.debug("selectSql: " + selectSql.toString());
		try {
			count = this.jdbcTemplate.queryForObject(selectSql.toString(), mapSqlParameterSource, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			count = 0;
		}

		logger.debug("Leaving");
		return count > 0 ? true : false;
	}

	public int getCustCountByDealerId(long dealerId) {
		logger.debug("Entering");
		Customer customer = new Customer();
		customer.setCustRO1(dealerId);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From Customers");
		selectSql.append(" Where CustRO1 =:CustRO1");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return 0;
		}
	}

	/**
	 * Method for validating customers in Caste
	 * 
	 */
	@Override
	public boolean isCasteExist(long casteId, String type) {
		logger.debug("Entering");

		int count = 0;
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("CasteId", casteId);

		StringBuilder selectSql = new StringBuilder("SELECT  COUNT(CasteId)  FROM  Customers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CasteId = :CasteId");

		logger.debug("selectSql: " + selectSql.toString());
		try {
			count = this.jdbcTemplate.queryForObject(selectSql.toString(), mapSqlParameterSource, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			count = 0;
		}

		logger.debug("Leaving");

		return count > 0 ? true : false;
	}

	/**
	 * Method for validating customers in Religion
	 * 
	 */
	@Override
	public boolean isReligionExist(long religionId, String type) {
		logger.debug("Entering");

		int count = 0;
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("ReligionId", religionId);

		StringBuilder selectSql = new StringBuilder("SELECT  COUNT(ReligionId)  FROM  Customers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ReligionId = :ReligionId");

		logger.debug("selectSql: " + selectSql.toString());
		try {
			count = this.jdbcTemplate.queryForObject(selectSql.toString(), mapSqlParameterSource, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			count = 0;
		}

		logger.debug("Leaving");

		return count > 0 ? true : false;
	}

	@Override
	public int getCustomerCountByCustID(long custID, String type) {
		// TODO Auto-generated method stub
		logger.debug("Entering");
		Customer customer = new Customer();
		customer.setCustID(custID);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT COUNT(*) FROM Customers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE CustID = :CustID");

		logger.debug("SelectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);

		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception :", e);
			recordCount = 0;
		}

		logger.debug("Leaving");
		return recordCount;
	}

	@Override
	public Customer checkCustomerByID(long custID, String type) {
		logger.debug("Entering");
		Customer customer = new Customer();
		customer.setCustID(custID);

		StringBuilder selectSql = new StringBuilder("SELECT CustID, CustCIF, CustShrtname");
		selectSql.append(" FROM  Customers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID=:CustID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);

		try {
			customer = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			if (!type.equals(TableType.TEMP_TAB.getSuffix())) {
				logger.debug("Leaving - Customer ID with " + String.valueOf(custID) + " Not Found");
			}
			customer = null;
			return customer;
		}

		logger.debug("Leaving");
		return customer;
	}

	@Override
	public List<Customer> getCustomerDetailsByCRCPR(String custCRCPR, String custCtgCode, String type) {
		logger.debug("Entering");

		List<Customer> customers = null;
		Customer customer = new Customer();
		customer.setCustCRCPR(custCRCPR);
		customer.setCustCtgCode(custCtgCode);

		StringBuilder selectSql = new StringBuilder("SELECT CustID, CustCIF, CustCoreBank, CustCtgCode, CustTypeCode,");
		selectSql.append(
				" CustSalutationCode, CustFName, CustMName, CustLName, CustShrtName, CustFNameLclLng, CustMNameLclLng,");
		selectSql.append(
				" CustLNameLclLng, CustShrtNameLclLng, CustDftBranch, CustGenderCode, CustDOB, CustPOB, CustCOB,");
		selectSql.append(
				" CustPassportNo, CustMotherMaiden, CustIsMinor, CustReferedBy, CustDSA, CustDSADept, CustRO1, CustRO2,");
		selectSql.append(
				" CustGroupID, CustSts, CustStsChgDate, CustGroupSts, CustIsBlocked, CustIsActive, CustIsClosed,");
		selectSql.append(
				" CustInactiveReason, CustIsDecease, CustIsDormant, CustIsDelinquent, CustIsTradeFinCust, CustIsStaff,");
		selectSql.append(
				" CustTradeLicenceNum , CustTradeLicenceExpiry, CustPassportExpiry, CustVisaNum , CustVisaExpiry,");
		selectSql.append(
				" CustStaffID, CustIndustry, CustSector, CustSubSector, CustProfession, CustTotalIncome, CustMaritalSts,");
		selectSql.append(
				" CustEmpSts, CustSegment, CustSubSegment, CustIsBlackListed, CustBLRsnCode, CustIsRejected, CustRejectedRsn,");
		selectSql.append(
				" CustBaseCcy, CustLng, CustParentCountry, CustResdCountry, CustRiskCountry, CustNationality, CustClosedOn, ");
		selectSql.append(
				"CustStmtFrq, CustIsStmtCombined, CustStmtLastDate, CustStmtNextDate, CustStmtDispatchMode, CustFirstBusinessDate,");
		selectSql.append(
				" CustAddlVar81, CustAddlVar82, CustAddlVar83, CustAddlVar84, CustAddlVar85, CustAddlVar86, CustAddlVar87,");
		selectSql.append(
				" CustAddlVar88, CustAddlVar89, CustAddlDate1, CustAddlDate2, CustAddlDate3, CustAddlDate4, CustAddlDate5,");
		selectSql.append(
				" CustAddlVar1, CustAddlVar2, CustAddlVar3, CustAddlVar4, CustAddlVar5, CustAddlVar6, CustAddlVar7, CustAddlVar8, ");
		selectSql.append(
				" CustAddlVar9, CustAddlVar10, CustAddlVar11, CustAddlDec1, CustAddlDec2, CustAddlDec3, CustAddlDec4, CustAddlDec5,CustSourceID,");
		selectSql.append(
				" CustAddlInt1, CustAddlInt2, CustAddlInt3, CustAddlInt4, CustAddlInt5,DedupFound,SkipDedup,CustTotalExpense,CustBlackListDate,NoOfDependents,CustCRCPR,");
		selectSql.append(
				" JointCust, JointCustName, JointCustDob, custRelation, ContactPersonName, EmailID, PhoneNumber, SalariedCustomer, custSuspSts,custSuspDate, custSuspTrigger, ");

		if (type.contains("View")) {
			selectSql.append(
					" lovDescCustTypeCodeName, lovDescCustMaritalStsName, lovDescCustEmpStsName,  lovDescCustStsName,");
			selectSql.append(
					" lovDescCustIndustryName, lovDescCustSectorName, lovDescCustSubSectorName, lovDescCustProfessionName, lovDescCustCOBName ,");
			selectSql.append(
					" lovDescCustSegmentName, lovDescCustNationalityName, lovDescCustGenderCodeName, lovDescCustDSADeptName, lovDescCustRO1Name, lovDescCustRO1City, ");
			selectSql.append(
					" lovDescCustGroupStsName, lovDescCustDftBranchName, lovDescCustCtgCodeName,lovDescCustCtgType, lovDescCustSalutationCodeName ,");
			selectSql.append(
					" lovDescCustParentCountryName, lovDescCustResdCountryName , lovDescCustRiskCountryName , lovDescCustRO2Name , lovDescCustBLRsnCodeName,");
			selectSql.append(
					" lovDescCustRejectedRsnName, lovDescCustGroupCode, lovDesccustGroupIDName , lovDescCustSubSegmentName, lovDescCustLngName , lovDescDispatchModeDescName");
			selectSql.append(" ,lovDescTargetName,CustSwiftBrnCode,");
			selectSql.append(" CasteCode, ReligionCode, CasteDesc, ReligionDesc,branchProvince,");
		}

		selectSql.append(
				" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" , CasteId, ReligionId, SubCategory,MarginDeviation ");
		selectSql.append(" FROM  Customers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustCRCPR =:CustCRCPR and CustCtgCode=:CustCtgCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);

		try {
			customers = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			customer = null;
		}
		logger.debug("Leaving");
		return customers;
	}

	@Override
	public Customer getCustomerByCoreBankId(String externalCif, String type) {
		logger.debug("Entering");
		Customer customer = new Customer();
		customer.setCustCoreBank(externalCif);
		StringBuilder selectSql = selectCustomerBasicInfo(type);
		selectSql.append(" Where CustCoreBank = :CustCoreBank");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);

		try {
			customer = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			customer = null;
		}
		logger.debug("Leaving");
		return customer;
	}

	/**
	 * Fetch the Record Customers details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Customer
	 */
	@Override
	public String getCustomerByCRCPR(final String custCRCPR, final String custCtgCode, String type) {
		logger.debug("Entering");

		String custCIF = null;
		WIFCustomer customer = new WIFCustomer();
		customer.setCustCRCPR(custCRCPR);
		customer.setCustCtgCode(custCtgCode);

		StringBuilder selectSql = new StringBuilder(" SELECT CustCIF ");
		selectSql.append(" FROM Customers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustCRCPR =:CustCRCPR");
		selectSql.append(" And   custCtgCode =:custCtgCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);

		try {
			custCIF = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			custCIF = null;
		}
		logger.debug("Leaving");
		return custCIF;
	}

	@Override
	public boolean isDuplicateCif(long custId, String cif, String custCtgCode) {
		logger.debug(Literal.ENTERING);

		boolean exists = false;

		// Prepare the parameter source.
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("CustID", custId);
		paramSource.addValue("CustCIF", cif);
		paramSource.addValue("custCtgCode", custCtgCode);

		// Check whether the document id exists for another customer.
		String sql = QueryUtil.getCountQuery(new String[] { "Customers_Temp", "Customers" },
				"CustID != :CustID and CustCIF = :CustCIF and custCtgCode = :custCtgCode");

		logger.trace(Literal.SQL + sql);
		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public boolean isDuplicateCrcpr(long custId, String custCRCPR, String custCtgCode) {
		logger.debug(Literal.ENTERING);

		boolean exists = false;

		// Prepare the parameter source.
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("CustID", custId);
		paramSource.addValue("CustCRCPR", custCRCPR);
		paramSource.addValue("custCtgCode", custCtgCode);

		// Check whether the document id exists for another customer.
		String sql = QueryUtil.getCountQuery(new String[] { "Customers_Temp", "Customers" },
				"CustID != :CustID and CustCRCPR = :CustCRCPR and custCtgCode = :custCtgCode");

		logger.trace(Literal.SQL + sql);
		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public boolean isDuplicateCoreBankId(long custId, String custCoreBank) {
		logger.debug(Literal.ENTERING);

		boolean exists = false;

		// Prepare the parameter source.
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("CustID", custId);
		paramSource.addValue("CustCoreBank", custCoreBank);

		// Check whether the document id exists for another customer.
		String sql = QueryUtil.getCountQuery(new String[] { "Customers_Temp", "Customers" },
				"CustID != :CustID and CustCoreBank  = :CustCoreBank");

		logger.trace(Literal.SQL + sql);
		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public Customer getCustomerDetailForFinancials(String custCIF, String tableType) {
		logger.debug(Literal.ENTERING);
		Customer customer = new Customer();
		customer.setCustCIF(custCIF);

		StringBuilder selectSql = new StringBuilder("SELECT CustFName, CustMName, CustLName, CustShrtName, CustDOB,");
		selectSql.append("lovDescCustTypeCodeName, custCtgCode");

		selectSql.append(" FROM  Customers");
		selectSql.append(StringUtils.trimToEmpty(tableType));
		selectSql.append(" Where CustCIF = :CustCIF");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);

		try {
			customer = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			customer = null;
		}
		logger.debug(Literal.LEAVING);
		return customer;
	}

	public int getCrifScoreValue(String tablename, String reference) {
		StringBuilder sql = new StringBuilder("SELECT CRIFSCORE ");
		sql.append(" FROM  ");
		sql.append(StringUtils.trimToEmpty(tablename));
		sql.append(" Where RecordStatus = :RecordStatus And Reference = :Reference");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("RecordStatus", "Approved");
		paramSource.addValue("Reference", reference);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, Integer.class);
		} catch (Exception e) {
			//
		}

		return 0;
	}

	@Override
	public List<Long> getCustomerDetailsBySRM(SRMCustRequest request) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("select distinct c.CustId from Customers c");
		sql.append(" left join CustomerPhoneNumbers  cp on cp.Phonecustid = c.CustId and");
		sql.append(" PhoneTypecode = :PhoneTypecode left join CustomerEMails ce on");
		sql.append(" ce.CustId = c.CustId left join Financemain fm on fm.CustId = c.CustId");

		StringBuilder whereClause = new StringBuilder();

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("PhoneTypecode", PennantConstants.PHONETYPE_MOBILE);
		if (StringUtils.isNotBlank(request.getCustCif())) {
			appenFilter(whereClause, request.getCustCif(), "c.CustCIF", "CustCIF", params);
		}
		if (StringUtils.isNotBlank(request.getFinReference())) {
			appenFilter(whereClause, request.getFinReference(), "fm.FinReference", "FinReference", params);
		}
		if (StringUtils.isNotBlank(request.getPhoneNumber())) {
			appenFilter(whereClause, request.getPhoneNumber(), "cp.PhoneNumber", "PhoneNumber", params);
		}
		if (StringUtils.isNotBlank(request.getCustCRCPR())) {
			appenFilter(whereClause, request.getCustCRCPR(), "c.CustCRCPR", "CustCRCPR", params);
		}
		if (request.getCustDOB() != null) {
			appenFilter(whereClause, request.getCustDOB(), "c.CustDOB", "CustDOB", params);
		}
		if (StringUtils.isNotBlank(request.getCustShrtName())) {
			appenFilter(whereClause, request.getCustShrtName(), "c.CustShrtName", "CustShrtName", params);
		}
		sql.append(whereClause.toString());

		try {
			return this.jdbcTemplate.queryForList(sql.toString(), params, Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Customer Detail not avilable for specified Request");
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	private void appenWhere(StringBuilder whereClause, Object fieldValue, String column, String parameterName,
			MapSqlParameterSource parameterSource) {
		if (whereClause.length() > 0) {
			whereClause.append(" and");
		} else {
			whereClause.append(" where");
		}

		whereClause.append(" ").append(column).append(" = :").append(parameterName);
		parameterSource.addValue(parameterName, fieldValue);
	}

	private void appenFilter(StringBuilder whereClause, Object fieldValue, String column, String parameterName,
			MapSqlParameterSource parameterSource) {
		if (fieldValue != null) {
			appenWhere(whereClause, fieldValue, column, parameterName, parameterSource);
		}

		parameterSource.addValue(parameterName, fieldValue);
	}

	@Override
	public boolean isCrifDeroge(String tablename, String reference) {
		StringBuilder sql = new StringBuilder("SELECT CRIFDEROGE ");
		sql.append(" FROM  ");
		sql.append(StringUtils.trimToEmpty(tablename));
		sql.append(" Where RecordStatus = :RecordStatus And Reference = :Reference");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("RecordStatus", "Approved");
		paramSource.addValue("Reference", reference);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, Boolean.class);
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);

		}
		return false;
	}

	@Override
	public Long getCustomerIdByCIF(String custCIF) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select distinct CustId");
		sql.append(" from (select custId from Customers_Temp");
		sql.append(" where CustCIF = ?");
		sql.append(" union all");
		sql.append(" Select CustId from Customers");
		sql.append(" where custCIF = ?) T");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { custCIF, custCIF },
					new RowMapper<Long>() {

						@Override
						public Long mapRow(ResultSet rs, int arg1) throws SQLException {
							return rs.getLong("CustId");
						}
					});

		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e.getCause());
		}
		return null;
	}

	private class CustomerRowMapper implements RowMapper<Customer> {
		private String type;

		private CustomerRowMapper(String type) {
			this.type = type;
		}

		@Override
		public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
			Customer c = new Customer();

			c.setCustID(rs.getLong("CustID"));
			c.setCustCIF(rs.getString("CustCIF"));
			c.setCustFName(rs.getString("CustFName"));
			c.setCustMName(rs.getString("CustMName"));
			c.setCustLName(rs.getString("CustLName"));
			c.setCustDOB(rs.getTimestamp("CustDOB"));
			c.setCustShrtName(rs.getString("CustShrtName"));
			c.setCustCRCPR(rs.getString("CustCRCPR"));
			c.setCustPassportNo(rs.getString("CustPassportNo"));
			c.setCustCtgCode(rs.getString("CustCtgCode"));
			c.setCustNationality(rs.getString("CustNationality"));
			c.setCustDftBranch(rs.getString("CustDftBranch"));
			c.setVersion(rs.getInt("Version"));
			c.setCustBaseCcy(rs.getString("CustBaseCcy"));
			c.setPhoneNumber(rs.getString("PhoneNumber"));
			c.setEmailID(rs.getString("EmailID"));
			c.setCustRO1(rs.getLong("CustRO1"));
			c.setCasteId(rs.getLong("CasteId"));
			c.setReligionId(rs.getLong("ReligionId"));
			c.setSubCategory(rs.getString("SubCategory"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				c.setLovDescCustStsName(rs.getString("LovDescCustStsName"));
				c.setCasteCode(rs.getString("CasteCode"));
				c.setCasteDesc(rs.getString("CasteDesc"));
				c.setReligionCode(rs.getString("ReligionCode"));
				c.setReligionDesc(rs.getString("ReligionDesc"));
			}

			return c;
		}

	}
}