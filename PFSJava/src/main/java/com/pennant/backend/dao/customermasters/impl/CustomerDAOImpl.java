/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : CustomerDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2011 * * Modified Date
 * : 27-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.customermasters.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.pennant.app.core.CustEODEvent;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerCoreBank;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceExposure;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.ProspectCustomer;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.pff.extension.CustomerExtension;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;
import com.pennanttech.ws.model.customer.SRMCustRequest;

/**
 * DAO methods implementation for the <b>Customer model</b> class.<br>
 * 
 */
public class CustomerDAOImpl extends SequenceDao<Customer> implements CustomerDAO {

	public CustomerDAOImpl() {
		super();
	}

	@Override
	public Customer getCustomer(boolean createNew, Customer customer) {

		WorkFlowDetails workFlowDetails = null;
		if (!createNew) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Customer");
		} else {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CustomerQDE");
		}

		if (workFlowDetails != null) {
			customer.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		return customer;
	}

	@Override
	public Customer getNewCustomer(boolean createNew, Customer customer) {

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

		return customer;
	}

	@Override
	public Customer getCustomerByID(final long id, String type) {
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
		sql.append(", ReligionId, SubCategory, MarginDeviation, ResidentialStatus");
		sql.append(", OtherCaste, OtherReligion, NatureOfBusiness, EntityType, CustResidentialSts, Qualification, Vip");

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

		sql.append(" From Customers");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				Customer c = new Customer();
				c.setNatureOfBusiness(rs.getString("NatureOfBusiness"));
				c.setQualification(rs.getString("Qualification"));
				c.setOtherReligion(rs.getString("OtherReligion"));
				c.setVip(rs.getBoolean("Vip"));
				c.setCustResidentialSts(rs.getString("CustResidentialSts"));
				c.setOtherCaste(rs.getString("OtherCaste"));
				c.setEntityType(rs.getString("EntityType"));
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
				c.setResidentialStatus(rs.getString("ResidentialStatus"));

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
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public Customer getCustomerForPostings(final long custId) {
		StringBuilder sql = new StringBuilder("Select CustCIF, CustCOB, CustCtgCode, CustIndustry");
		sql.append(", CustIsStaff, CustNationality, CustParentCountry, CustResdCountry");
		sql.append(", CustRiskCountry, CustSector, CustSubSector, CustTypeCode");
		sql.append(", CasteId, ReligionId, SubCategory");
		sql.append(" From Customers");
		sql.append(" Where CustID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				Customer c = new Customer();

				c.setCustCIF(rs.getString("CustCIF"));
				c.setCustDOB(JdbcUtil.getDate(rs.getDate("CustCOB")));
				c.setCustCtgCode(rs.getString("CustCtgCode"));
				c.setCustIndustry(rs.getString("CustIndustry"));
				c.setCustIsStaff(rs.getBoolean("CustIsStaff"));
				c.setCustNationality(rs.getString("CustNationality"));
				c.setCustParentCountry(rs.getString("CustParentCountry"));
				c.setCustResdCountry(rs.getString("CustResdCountry"));
				c.setCustRiskCountry(rs.getString("CustRiskCountry"));
				c.setCustSector(rs.getString("CustSector"));
				c.setCustSubSector(rs.getString("CustSubSector"));
				c.setCustTypeCode(rs.getString("CustTypeCode"));
				c.setCasteId(rs.getLong("CasteId"));
				c.setReligionId(rs.getLong("ReligionId"));
				c.setSubCategory(rs.getString("SubCategory"));

				return c;
			}, custId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void delete(Customer customer, String type) {
		StringBuilder sql = new StringBuilder("Delete From Customers");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			if (this.jdbcOperations.update(sql.toString(), customer.getCustID()) <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

	}

	@Override
	public long save(Customer c, String type) {
		if (c.getCustID() == 0 || c.getCustID() == Long.MIN_VALUE) {
			c.setCustID(getNextValue("SeqCustomers"));
		}

		if (StringUtils.trimToNull(c.getCustCoreBank()) == null) {
			c.setCustCoreBank(String.valueOf(-1 * c.getCustID()));
		}

		StringBuilder sql = new StringBuilder("Insert Into Customers");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(CustID, CustCIF, CustCoreBank, CustCtgCode, CustTypeCode, CustSalutationCode, CustFName");
		sql.append(", CustMName, CustLName, CustShrtName, CustFNameLclLng, CustMNameLclLng, CustLNameLclLng");
		sql.append(", CustShrtNameLclLng, CustDftBranch, CustGenderCode, CustDOB, CustPOB, CustCOB, CustPassportNo");
		sql.append(", CustMotherMaiden, CustIsMinor, CustReferedBy, CustDSA, CustDSADept, CustRO1, CustRO2");
		sql.append(", CustGroupID, CustSts, CustStsChgDate, CustGroupSts, CustIsBlocked, CustIsActive, CustIsClosed");
		sql.append(", CustInactiveReason, CustIsDecease, CustIsDormant, CustIsDelinquent, CustIsTradeFinCust");
		sql.append(", CustTradeLicenceNum, CustTradeLicenceExpiry, CustPassportExpiry, CustVisaNum");
		sql.append(", CustVisaExpiry, CustIsStaff, CustStaffID, CustIndustry, CustSector");
		sql.append(", CustSubSector, CustProfession, CustTotalIncome, CustMaritalSts, CustEmpSts");
		sql.append(", CustSegment, CustSubSegment, CustIsBlackListed, CustBLRsnCode, CustIsRejected, CustRejectedRsn");
		sql.append(", CustBaseCcy, CustLng, CustParentCountry, CustResdCountry, CustRiskCountry, CustNationality");
		sql.append(", CustClosedOn, CustStmtFrq, CustIsStmtCombined, CustStmtLastDate, CustStmtNextDate");
		sql.append(", CustStmtDispatchMode, CustFirstBusinessDate, CustAddlVar81, CustAddlVar82, CustAddlVar83");
		sql.append(", CustAddlVar84, CustAddlVar85, CustAddlVar86, CustAddlVar87, CustAddlVar88, CustAddlVar89");
		sql.append(", CustAddlDate1, CustAddlDate2, CustAddlDate3, CustAddlDate4, CustAddlDate5, CustAddlVar1");
		sql.append(", CustAddlVar2, CustAddlVar3, CustAddlVar4, CustAddlVar5, CustAddlVar6, CustAddlVar7");
		sql.append(", CustAddlVar8, CustAddlVar9, CustAddlVar10, CustAddlVar11, CustAddlDec1, CustAddlDec2");
		sql.append(", CustAddlDec3, CustAddlDec4, CustAddlDec5, CustAddlInt1, CustAddlInt2, CustAddlInt3");
		sql.append(", CustAddlInt4, CustAddlInt5, DedupFound, SkipDedup, CustTotalExpense, CustBlackListDate");
		sql.append(", NoOfDependents, CustCRCPR, CustSourceID, JointCust, JointCustName, JointCustDob");
		sql.append(", CustRelation, ContactPersonName, EmailID, PhoneNumber, SalariedCustomer,ApplicationNo, Dnd");
		sql.append(", OtherCaste, OtherReligion, NatureOfBusiness, EntityType, CustResidentialSts, Qualification");
		sql.append(", Vip, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId, CasteId, ReligionId, SubCategory, MarginDeviation, ResidentialStatus)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, c.getCustID());
			ps.setString(index++, c.getCustCIF());
			ps.setString(index++, c.getCustCoreBank());
			ps.setString(index++, c.getCustCtgCode());
			ps.setString(index++, c.getCustTypeCode());
			ps.setString(index++, c.getCustSalutationCode());
			ps.setString(index++, c.getCustFName());
			ps.setString(index++, c.getCustMName());
			ps.setString(index++, c.getCustLName());
			ps.setString(index++, c.getCustShrtName());
			ps.setString(index++, c.getCustFNameLclLng());
			ps.setString(index++, c.getCustMNameLclLng());
			ps.setString(index++, c.getCustLNameLclLng());
			ps.setString(index++, c.getCustShrtNameLclLng());
			ps.setString(index++, c.getCustDftBranch());
			ps.setString(index++, c.getCustGenderCode());
			ps.setDate(index++, JdbcUtil.getDate(c.getCustDOB()));
			ps.setString(index++, c.getCustPOB());
			ps.setString(index++, c.getCustCOB());
			ps.setString(index++, c.getCustPassportNo());
			ps.setString(index++, c.getCustMotherMaiden());
			ps.setBoolean(index++, c.isCustIsMinor());
			ps.setString(index++, c.getCustReferedBy());
			ps.setString(index++, c.getCustDSA());
			ps.setString(index++, c.getCustDSADept());
			ps.setLong(index++, c.getCustRO1());
			ps.setString(index++, c.getCustRO2());
			ps.setLong(index++, c.getCustGroupID());
			ps.setString(index++, c.getCustSts());
			ps.setDate(index++, JdbcUtil.getDate(c.getCustStsChgDate()));
			ps.setString(index++, c.getCustGroupSts());
			ps.setBoolean(index++, c.isCustIsBlocked());
			ps.setBoolean(index++, c.isCustIsActive());
			ps.setBoolean(index++, c.isCustIsClosed());
			ps.setString(index++, c.getCustInactiveReason());
			ps.setBoolean(index++, c.isCustIsDecease());
			ps.setBoolean(index++, c.isCustIsDormant());
			ps.setBoolean(index++, c.isCustIsDelinquent());
			ps.setBoolean(index++, c.isCustIsTradeFinCust());
			ps.setString(index++, c.getCustTradeLicenceNum());
			ps.setDate(index++, JdbcUtil.getDate(c.getCustTradeLicenceExpiry()));
			ps.setDate(index++, JdbcUtil.getDate(c.getCustPassportExpiry()));
			ps.setString(index++, c.getCustVisaNum());
			ps.setDate(index++, JdbcUtil.getDate(c.getCustVisaExpiry()));
			ps.setBoolean(index++, c.isCustIsStaff());
			ps.setString(index++, c.getCustStaffID());
			ps.setString(index++, c.getCustIndustry());
			ps.setString(index++, c.getCustSector());
			ps.setString(index++, c.getCustSubSector());
			ps.setString(index++, c.getCustProfession());
			ps.setBigDecimal(index++, c.getCustTotalIncome());
			ps.setString(index++, c.getCustMaritalSts());
			ps.setString(index++, c.getCustEmpSts());
			ps.setString(index++, c.getCustSegment());
			ps.setString(index++, c.getCustSubSegment());
			ps.setBoolean(index++, c.isCustIsBlackListed());
			ps.setString(index++, c.getCustBLRsnCode());
			ps.setBoolean(index++, c.isCustIsRejected());
			ps.setString(index++, c.getCustRejectedRsn());
			ps.setString(index++, c.getCustBaseCcy());
			ps.setString(index++, c.getCustLng());
			ps.setString(index++, c.getCustParentCountry());
			ps.setString(index++, c.getCustResdCountry());
			ps.setString(index++, c.getCustRiskCountry());
			ps.setString(index++, c.getCustNationality());
			ps.setDate(index++, JdbcUtil.getDate(c.getCustClosedOn()));
			ps.setString(index++, c.getCustStmtFrq());
			ps.setBoolean(index++, c.isCustIsStmtCombined());
			ps.setTimestamp(index++, c.getCustStmtLastDate());
			ps.setTimestamp(index++, c.getCustStmtNextDate());
			ps.setString(index++, c.getCustStmtDispatchMode());
			ps.setDate(index++, JdbcUtil.getDate(c.getCustFirstBusinessDate()));
			ps.setString(index++, c.getCustAddlVar81());
			ps.setString(index++, c.getCustAddlVar82());
			ps.setString(index++, c.getCustAddlVar83());
			ps.setString(index++, c.getCustAddlVar84());
			ps.setString(index++, c.getCustAddlVar85());
			ps.setString(index++, c.getCustAddlVar86());
			ps.setString(index++, c.getCustAddlVar87());
			ps.setString(index++, c.getCustAddlVar88());
			ps.setString(index++, c.getCustAddlVar89());
			ps.setDate(index++, JdbcUtil.getDate(c.getCustAddlDate1()));
			ps.setDate(index++, JdbcUtil.getDate(c.getCustAddlDate2()));
			ps.setDate(index++, JdbcUtil.getDate(c.getCustAddlDate3()));
			ps.setDate(index++, JdbcUtil.getDate(c.getCustAddlDate4()));
			ps.setDate(index++, JdbcUtil.getDate(c.getCustAddlDate5()));
			ps.setString(index++, c.getCustAddlVar1());
			ps.setString(index++, c.getCustAddlVar2());
			ps.setString(index++, c.getCustAddlVar3());
			ps.setString(index++, c.getCustAddlVar4());
			ps.setString(index++, c.getCustAddlVar5());
			ps.setString(index++, c.getCustAddlVar6());
			ps.setString(index++, c.getCustAddlVar7());
			ps.setString(index++, c.getCustAddlVar8());
			ps.setString(index++, c.getCustAddlVar9());
			ps.setString(index++, c.getCustAddlVar10());
			ps.setString(index++, c.getCustAddlVar11());
			ps.setBigDecimal(index++, c.getCustAddlDec1());
			ps.setDouble(index++, c.getCustAddlDec2());
			ps.setDouble(index++, c.getCustAddlDec3());
			ps.setDouble(index++, c.getCustAddlDec4());
			ps.setDouble(index++, c.getCustAddlDec5());
			ps.setInt(index++, c.getCustAddlInt1());
			ps.setInt(index++, c.getCustAddlInt2());
			ps.setInt(index++, c.getCustAddlInt3());
			ps.setInt(index++, c.getCustAddlInt4());
			ps.setInt(index++, c.getCustAddlInt5());
			ps.setBoolean(index++, c.isDedupFound());
			ps.setBoolean(index++, c.isSkipDedup());
			ps.setBigDecimal(index++, c.getCustTotalExpense());
			ps.setDate(index++, JdbcUtil.getDate(c.getCustBlackListDate()));
			ps.setInt(index++, c.getNoOfDependents());
			ps.setString(index++, c.getCustCRCPR());
			ps.setString(index++, c.getCustSourceID());
			ps.setBoolean(index++, c.isJointCust());
			ps.setString(index++, c.getJointCustName());
			ps.setDate(index++, JdbcUtil.getDate(c.getJointCustDob()));
			ps.setString(index++, c.getCustRelation());
			ps.setString(index++, c.getContactPersonName());
			ps.setString(index++, c.getEmailID());
			ps.setString(index++, c.getPhoneNumber());
			ps.setBoolean(index++, c.isSalariedCustomer());
			ps.setString(index++, c.getApplicationNo());
			ps.setBoolean(index++, c.isDnd());
			ps.setString(index++, c.getOtherCaste());
			ps.setString(index++, c.getOtherReligion());
			ps.setString(index++, c.getNatureOfBusiness());
			ps.setString(index++, c.getEntityType());
			ps.setString(index++, c.getCustResidentialSts());
			ps.setString(index++, c.getQualification());
			ps.setBoolean(index++, c.isVip());
			ps.setInt(index++, c.getVersion());
			ps.setLong(index++, c.getLastMntBy());
			ps.setTimestamp(index++, c.getLastMntOn());
			ps.setString(index++, c.getRecordStatus());
			ps.setString(index++, c.getRoleCode());
			ps.setString(index++, c.getNextRoleCode());
			ps.setString(index++, c.getTaskId());
			ps.setString(index++, c.getNextTaskId());
			ps.setString(index++, c.getRecordType());
			ps.setLong(index++, c.getWorkflowId());
			ps.setLong(index++, c.getCasteId());
			ps.setLong(index++, c.getReligionId());
			ps.setString(index++, c.getSubCategory());
			ps.setBoolean(index++, c.isMarginDeviation());
			ps.setString(index, c.getResidentialStatus());
		});

		return c.getId();
	}

	@Override
	public void update(Customer c, String type) {
		int recordCount = 0;

		StringBuilder sql = new StringBuilder("Update Customers").append(StringUtils.trimToEmpty(type));
		sql.append(" Set CustCtgCode = ?, CustCoreBank = ?, CustTypeCode = ?, CustSalutationCode = ?");
		sql.append(", CustFName = ?, CustMName = ?, CustLName = ?, CustShrtName = ?, CustFNameLclLng = ?");
		sql.append(", CustMNameLclLng = ?, CustLNameLclLng = ?, CustShrtNameLclLng = ?, CustDftBranch = ?");
		sql.append(", CustGenderCode = ?, CustDOB = ?, CustPOB = ?, CustCOB = ?, CustPassportNo = ?");
		sql.append(", CustMotherMaiden = ?, CustIsMinor = ?, CustReferedBy = ?, CustDSA = ?");
		sql.append(", CustDSADept = ?, CustRO1 = ?, CustRO2 = ?, custRelation = ?, CustGroupID = ?");
		sql.append(", CustSts = ?, CustStsChgDate = ?, CustGroupSts = ?, CustIsBlocked = ?, CustIsActive = ?");
		sql.append(", CustIsClosed = ?, CustInactiveReason = ?, CustIsDecease = ?, CustIsDormant = ?");
		sql.append(", CustIsDelinquent = ?, CustIsTradeFinCust = ?, CustTradeLicenceNum = ?");
		sql.append(", CustTradeLicenceExpiry= ?, CustPassportExpiry = ?, CustVisaNum = ?, CustVisaExpiry = ?");
		sql.append(", CustIsStaff = ?, CustStaffID = ?, CustIndustry = ?, CustSector = ?, CustSubSector = ?");
		sql.append(", CustProfession = ?, CustTotalIncome = ?, CustMaritalSts = ?, CustEmpSts = ?,CustSegment = ?");
		sql.append(", CustSubSegment = ?, CustIsBlackListed = ?, CustBLRsnCode = ?, CustIsRejected = ?");
		sql.append(", CustRejectedRsn = ?, CustBaseCcy = ?, CustLng = ?, CustParentCountry = ?, CustResdCountry = ?");
		sql.append(", CustRiskCountry = ?, CustNationality = ?, CustClosedOn = ?, CustStmtFrq = ?");
		sql.append(", CustIsStmtCombined = ?, CustStmtLastDate = ?, MarginDeviation = ?, CustStmtNextDate = ?");
		sql.append(", CustStmtDispatchMode = ?, CustFirstBusinessDate = ?, CustAddlVar81 = ?, CustAddlVar82 = ?");
		sql.append(", CustAddlVar83 = ?, CustAddlVar84 = ?, CustAddlVar85 = ?, CustAddlVar86 = ?, CustAddlVar87 = ?");
		sql.append(", CustAddlVar88 = ?, CustAddlVar89 = ?, CustAddlDate1 = ?, CustAddlDate2 = ?");
		sql.append(", CustAddlDate3 = ?, CustAddlDate4 = ?, CustAddlDate5 = ?, CustAddlVar1 = ?, CustAddlVar2 = ?");
		sql.append(", CustAddlVar3 = ?, CustAddlVar4 = ?, CustAddlVar5 = ?, CustAddlVar6 = ?, CustAddlVar7 = ?");
		sql.append(", CustAddlVar8 = ?, CustAddlVar9 = ?, CustAddlVar10 = ?, CustAddlVar11 = ?, CustAddlDec1 = ?");
		sql.append(", CustAddlDec2 = ?, CustAddlDec3 = ?, CustAddlDec4 = ?, CustAddlDec5 = ?, CustAddlInt1 = ?");
		sql.append(", CustAddlInt2 = ?, CustAddlInt3 = ?, CustAddlInt4 = ?, CustAddlInt5 = ?, DedupFound = ?");
		sql.append(", SkipDedup = ?, CustTotalExpense = ?, CustBlackListDate = ?, NoOfDependents = ?, CustCRCPR = ?");
		sql.append(", CustSourceID = ?, JointCust = ?, JointCustName = ?, JointCustDob = ?, ContactPersonName = ?");
		sql.append(", EmailID = ?, PhoneNumber = ?, SalariedCustomer = ?, Version = ?, LastMntBy = ?, LastMntOn = ?");
		sql.append(", RecordStatus= ?, RoleCode = ?, NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?");
		sql.append(", WorkflowId = ?, CasteId = ?, ReligionId = ?, SubCategory = ?, ApplicationNo = ?");
		sql.append(", Dnd = ?, ResidentialStatus = ?, OtherCaste= ?, OtherReligion= ?, NatureOfBusiness= ?");
		sql.append(", EntityType= ?, CustResidentialSts= ?, Qualification= ?, Vip = ?");
		sql.append(" Where CustID = ?");

		if (!type.endsWith("_Temp")) {
			sql.append(" and Version = ?");
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, c.getCustCtgCode());
			ps.setString(index++, c.getCustCoreBank());
			ps.setString(index++, c.getCustTypeCode());
			ps.setString(index++, c.getCustSalutationCode());
			ps.setString(index++, c.getCustFName());
			ps.setString(index++, c.getCustMName());
			ps.setString(index++, c.getCustLName());
			ps.setString(index++, c.getCustShrtName());
			ps.setString(index++, c.getCustFNameLclLng());
			ps.setString(index++, c.getCustMNameLclLng());
			ps.setString(index++, c.getCustLNameLclLng());
			ps.setString(index++, c.getCustShrtNameLclLng());
			ps.setString(index++, c.getCustDftBranch());
			ps.setString(index++, c.getCustGenderCode());
			ps.setDate(index++, JdbcUtil.getDate(c.getCustDOB()));
			ps.setString(index++, c.getCustPOB());
			ps.setString(index++, c.getCustCOB());
			ps.setString(index++, c.getCustPassportNo());
			ps.setString(index++, c.getCustMotherMaiden());
			ps.setBoolean(index++, c.isCustIsMinor());
			ps.setString(index++, c.getCustReferedBy());
			ps.setString(index++, c.getCustDSA());
			ps.setString(index++, c.getCustDSADept());
			ps.setLong(index++, c.getCustRO1());
			ps.setString(index++, c.getCustRO2());
			ps.setString(index++, c.getCustRelation());
			ps.setLong(index++, c.getCustGroupID());
			ps.setString(index++, c.getCustSts());
			ps.setDate(index++, JdbcUtil.getDate(c.getCustStsChgDate()));
			ps.setString(index++, c.getCustGroupSts());
			ps.setBoolean(index++, c.isCustIsBlocked());
			ps.setBoolean(index++, c.isCustIsActive());
			ps.setBoolean(index++, c.isCustIsClosed());
			ps.setString(index++, c.getCustInactiveReason());
			ps.setBoolean(index++, c.isCustIsDecease());
			ps.setBoolean(index++, c.isCustIsDormant());
			ps.setBoolean(index++, c.isCustIsDelinquent());
			ps.setBoolean(index++, c.isCustIsTradeFinCust());
			ps.setString(index++, c.getCustTradeLicenceNum());
			ps.setDate(index++, JdbcUtil.getDate(c.getCustTradeLicenceExpiry()));
			ps.setDate(index++, JdbcUtil.getDate(c.getCustPassportExpiry()));
			ps.setString(index++, c.getCustVisaNum());
			ps.setDate(index++, JdbcUtil.getDate(c.getCustVisaExpiry()));
			ps.setBoolean(index++, c.isCustIsStaff());
			ps.setString(index++, c.getCustStaffID());
			ps.setString(index++, c.getCustIndustry());
			ps.setString(index++, c.getCustSector());
			ps.setString(index++, c.getCustSubSector());
			ps.setString(index++, c.getCustProfession());
			ps.setBigDecimal(index++, c.getCustTotalIncome());
			ps.setString(index++, c.getCustMaritalSts());
			ps.setString(index++, c.getCustEmpSts());
			ps.setString(index++, c.getCustSegment());
			ps.setString(index++, c.getCustSubSegment());
			ps.setBoolean(index++, c.isCustIsBlackListed());
			ps.setString(index++, c.getCustBLRsnCode());
			ps.setBoolean(index++, c.isCustIsRejected());
			ps.setString(index++, c.getCustRejectedRsn());
			ps.setString(index++, c.getCustBaseCcy());
			ps.setString(index++, c.getCustLng());
			ps.setString(index++, c.getCustParentCountry());
			ps.setString(index++, c.getCustResdCountry());
			ps.setString(index++, c.getCustRiskCountry());
			ps.setString(index++, c.getCustNationality());
			ps.setDate(index++, JdbcUtil.getDate(c.getCustClosedOn()));
			ps.setString(index++, c.getCustStmtFrq());
			ps.setBoolean(index++, c.isCustIsStmtCombined());
			ps.setTimestamp(index++, c.getCustStmtLastDate());
			ps.setBoolean(index++, c.isMarginDeviation());
			ps.setTimestamp(index++, c.getCustStmtNextDate());
			ps.setString(index++, c.getCustStmtDispatchMode());
			ps.setDate(index++, JdbcUtil.getDate(c.getCustFirstBusinessDate()));
			ps.setString(index++, c.getCustAddlVar81());
			ps.setString(index++, c.getCustAddlVar82());
			ps.setString(index++, c.getCustAddlVar83());
			ps.setString(index++, c.getCustAddlVar84());
			ps.setString(index++, c.getCustAddlVar85());
			ps.setString(index++, c.getCustAddlVar86());
			ps.setString(index++, c.getCustAddlVar87());
			ps.setString(index++, c.getCustAddlVar88());
			ps.setString(index++, c.getCustAddlVar89());
			ps.setDate(index++, JdbcUtil.getDate(c.getCustAddlDate1()));
			ps.setDate(index++, JdbcUtil.getDate(c.getCustAddlDate2()));
			ps.setDate(index++, JdbcUtil.getDate(c.getCustAddlDate3()));
			ps.setDate(index++, JdbcUtil.getDate(c.getCustAddlDate4()));
			ps.setDate(index++, JdbcUtil.getDate(c.getCustAddlDate5()));
			ps.setString(index++, c.getCustAddlVar1());
			ps.setString(index++, c.getCustAddlVar2());
			ps.setString(index++, c.getCustAddlVar3());
			ps.setString(index++, c.getCustAddlVar4());
			ps.setString(index++, c.getCustAddlVar5());
			ps.setString(index++, c.getCustAddlVar6());
			ps.setString(index++, c.getCustAddlVar7());
			ps.setString(index++, c.getCustAddlVar8());
			ps.setString(index++, c.getCustAddlVar9());
			ps.setString(index++, c.getCustAddlVar10());
			ps.setString(index++, c.getCustAddlVar11());
			ps.setBigDecimal(index++, c.getCustAddlDec1());
			ps.setDouble(index++, c.getCustAddlDec2());
			ps.setDouble(index++, c.getCustAddlDec3());
			ps.setDouble(index++, c.getCustAddlDec4());
			ps.setDouble(index++, c.getCustAddlDec5());
			ps.setInt(index++, c.getCustAddlInt1());
			ps.setInt(index++, c.getCustAddlInt2());
			ps.setInt(index++, c.getCustAddlInt3());
			ps.setInt(index++, c.getCustAddlInt4());
			ps.setInt(index++, c.getCustAddlInt5());
			ps.setBoolean(index++, c.isDedupFound());
			ps.setBoolean(index++, c.isSkipDedup());
			ps.setBigDecimal(index++, c.getCustTotalExpense());
			ps.setDate(index++, JdbcUtil.getDate(c.getCustBlackListDate()));
			ps.setInt(index++, c.getNoOfDependents());
			ps.setString(index++, c.getCustCRCPR());
			ps.setString(index++, c.getCustSourceID());
			ps.setBoolean(index++, c.isJointCust());
			ps.setString(index++, c.getJointCustName());
			ps.setDate(index++, JdbcUtil.getDate(c.getJointCustDob()));
			ps.setString(index++, c.getContactPersonName());
			ps.setString(index++, c.getEmailID());
			ps.setString(index++, c.getPhoneNumber());
			ps.setBoolean(index++, c.isSalariedCustomer());
			ps.setInt(index++, c.getVersion());
			ps.setLong(index++, c.getLastMntBy());
			ps.setTimestamp(index++, c.getLastMntOn());
			ps.setString(index++, c.getRecordStatus());
			ps.setString(index++, c.getRoleCode());
			ps.setString(index++, c.getNextRoleCode());
			ps.setString(index++, c.getTaskId());
			ps.setString(index++, c.getNextTaskId());
			ps.setString(index++, c.getRecordType());
			ps.setLong(index++, c.getWorkflowId());
			ps.setLong(index++, c.getCasteId());
			ps.setLong(index++, c.getReligionId());
			ps.setString(index++, c.getSubCategory());
			ps.setString(index++, c.getApplicationNo());
			ps.setBoolean(index++, c.isDnd());
			ps.setString(index++, c.getResidentialStatus());
			ps.setString(index++, c.getOtherCaste());
			ps.setString(index++, c.getOtherReligion());
			ps.setString(index++, c.getNatureOfBusiness());
			ps.setString(index++, c.getEntityType());
			ps.setString(index++, c.getCustResidentialSts());
			ps.setString(index++, c.getQualification());
			ps.setBoolean(index++, c.isVip());

			ps.setLong(index++, c.getCustID());

			if (!type.endsWith("_Temp")) {
				ps.setInt(index, c.getVersion() - 1);
			}

		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

	}

	@Override
	public boolean isDuplicateCif(long custId, String cif) {
		String sql = QueryUtil.getCountQuery(new String[] { "Customers_Temp", "Customers" },
				"CustID != ? and CustCIF = ?");

		Object[] obj = new Object[] { custId, cif, custId, cif };

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, Integer.class, obj) > 0;
	}

	@Override
	public Customer getCustomerByCIF(String cifId, String type) {
		StringBuilder sql = selectCustomerBasicInfo(type);
		sql.append(" Where CustCIF = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new CustomerRowMapper(type), cifId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	private StringBuilder selectCustomerBasicInfo(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustID, CustCoreBank, CustCIF, CustFName, CustMName, CustLName, CustDOB, CustShrtName, CustCRCPR");
		sql.append(", CustPassportNo, CustCtgCode, CustNationality, CustDftBranch, Version, CustBaseCcy");
		sql.append(", PhoneNumber, EmailID, CustRO1, CasteId, ReligionId, SubCategory, NatureOfBusiness, CustTypeCode");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescCustStsName, CasteCode, CasteDesc, ReligionCode, ReligionDesc");
		}

		sql.append(" From Customers");
		sql.append(StringUtils.trimToEmpty(type));
		return sql;
	}

	@Override
	public Customer checkCustomerByCIF(String cif, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustID, CustCIF");
		sql.append(" From Customers");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustCIF = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, i) -> {
				Customer c = new Customer();

				c.setCustID(rs.getLong("CustID"));
				c.setCustCIF(rs.getString("CustCIF"));

				return c;
			}, cif);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public Customer getCustomer(String cif) {
		String sql = "Select CustID, CustCIF, CustCoreBank, CustShrtName From Customers Where CustCIF = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, i) -> {
				Customer c = new Customer();

				c.setCustID(rs.getLong("CustID"));
				c.setCustCIF(rs.getString("CustCIF"));
				c.setCustCoreBank(rs.getString("CustCoreBank"));
				c.setCustShrtName(rs.getString("CustShrtName"));

				return c;
			}, cif);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public Customer getCustomer(long custID) {
		String sql = "Select CustID, CustCIF, CustCoreBank, CustShrtname FROM  Customers Where CustID = ?";

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				Customer c = new Customer();

				c.setCustID(rs.getLong("CustID"));
				c.setCustCIF(rs.getString("CustCIF"));
				c.setCustCoreBank(rs.getString("CustCoreBank"));
				c.setCustShrtName(rs.getString("CustShrtname"));

				return c;
			}, custID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public Customer getCustomerCoreBankID(String custCoreBank) {
		String sql = "Select CustID, CustCIF, CustCoreBank, CustShrtName From Customers Where CustCoreBank = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, i) -> {
				Customer c = new Customer();

				c.setCustID(rs.getLong("CustID"));
				c.setCustCIF(rs.getString("CustCIF"));
				c.setCustCoreBank(rs.getString("CustCoreBank"));
				c.setCustShrtName(rs.getString("CustShrtName"));

				return c;
			}, custCoreBank);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public WIFCustomer getWIFCustomerByCIF(long cifId, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustCRCPR, CustShrtName, CustTypeCode, CustCtgCode");
		sql.append(", CustDOB, CustNationality, CustGenderCode, CustSalutationCode");
		sql.append(", CustMaritalSts, CustEmpSts, CustTotalIncome");
		sql.append(", CustTotalExpense, CustBaseCcy, CustSubSector");
		sql.append(", NoOfDependents, SalariedCustomer, LovDescCustMaritalStsName, LovDescCustCtgCodeName");
		sql.append(", LovDescCustTypeCodeName, LovDescCustNationalityName, LovDescCustEmpStsName");
		sql.append(" From Customers_View");
		sql.append(" Where CustID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {

				WIFCustomer wif = new WIFCustomer();

				wif.setCustCRCPR(rs.getString("CustCRCPR"));
				wif.setCustShrtName(rs.getString("CustShrtName"));
				wif.setCustTypeCode(rs.getString("CustTypeCode"));
				wif.setCustCtgCode(rs.getString("CustCtgCode"));
				wif.setCustDOB(JdbcUtil.getDate(rs.getDate("CustDOB")));
				wif.setCustNationality(rs.getString("CustNationality"));
				wif.setCustGenderCode(rs.getString("CustGenderCode"));
				wif.setCustSalutationCode(rs.getString("CustSalutationCode"));
				wif.setCustMaritalSts(rs.getString("CustMaritalSts"));
				wif.setCustEmpSts(rs.getString("CustEmpSts"));
				wif.setTotalIncome(rs.getBigDecimal("CustTotalIncome"));
				wif.setTotalExpense(rs.getBigDecimal("CustTotalExpense"));
				wif.setCustBaseCcy(rs.getString("CustBaseCcy"));
				wif.setCustSubSector(rs.getString("CustSubSector"));
				wif.setNoOfDependents(rs.getInt("NoOfDependents"));
				wif.setSalariedCustomer(rs.getBoolean("SalariedCustomer"));
				wif.setLovDescCustMaritalStsName(rs.getString("LovDescCustMaritalStsName"));
				wif.setLovDescCustCtgCodeName(rs.getString("LovDescCustCtgCodeName"));
				wif.setLovDescCustTypeCodeName(rs.getString("LovDescCustTypeCodeName"));
				wif.setLovDescCustNationalityName(rs.getString("LovDescCustNationalityName"));
				wif.setLovDescCustEmpStsName(rs.getString("LovDescCustEmpStsName"));

				return wif;
			}, cifId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public String getNewProspectCustomerCIF() {
		return String.valueOf(getNextValue("SeqProspectCustomer"));
	}

	@Override
	public List<FinanceProfitDetail> getCustFinAmtDetails(long custId, CustomerEligibilityCheck eligibilityCheck) {
		String sql = "Select FinCcy, TotalPriBal, TotalPftBal, ODProfit, ODPrincipal From FinPftDetails Where CustID = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.query(sql, (rs, rowNum) -> {
			FinanceProfitDetail fpd = new FinanceProfitDetail();

			fpd.setFinCcy(rs.getString("FinCcy"));
			fpd.setTotalPriBal(rs.getBigDecimal("TotalPriBal"));
			fpd.setTotalPftBal(rs.getBigDecimal("TotalPftBal"));
			fpd.setODProfit(rs.getBigDecimal("ODProfit"));
			fpd.setODPrincipal(rs.getBigDecimal("ODPrincipal"));

			return fpd;
		}, custId);
	}

	@Override
	public String getCustEmpDesg(long custID) {
		String sql = "Select CustEmpDesg From CustomerEmpDetails Where CustID = ? and CurrentEmployer = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, String.class, custID, 1);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return "";
		}
	}

	@Override
	public String getCustCurEmpAlocType(long custID) {
		String sql = "Select EmpAlocationType From CustomerEmpDetails_AView Where CustID = ? and CurrentEmployer = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, String.class, custID, 1);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return "";
		}
	}

	@Override
	public BigDecimal getCustRepayOtherTotal(long custID) {
		String sql = "Select CustRepayOther From CustOthExpense_View Where CustID = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, BigDecimal.class, custID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return BigDecimal.ZERO;
		}
	}

	@Override
	public BigDecimal getCustRepayBankTotal(long custID) {
		String sql = "Select CustId, TotalRepayAmt, MaturityDate, FinStartDate, FinCcy From FinanceMain Where FinIsActive = ? and CustID = ?";

		logger.debug(Literal.SQL.concat(sql));

		List<CustomerIncome> incDtls = this.jdbcOperations.query(sql, (rs, rowNum) -> {
			CustomerIncome custInc = new CustomerIncome();

			custInc.setFinCcy(rs.getString("FinCcy"));
			custInc.setTotalRepayAmt(rs.getBigDecimal("TotalRepayAmt"));
			custInc.setCustId(rs.getLong("CustId"));
			custInc.setFinStartDate(rs.getDate("FinStartDate"));
			custInc.setMaturityDate(rs.getDate("MaturityDate"));

			return custInc;
		}, 1, custID);

		if (CollectionUtils.isEmpty(incDtls)) {
			return BigDecimal.ZERO;
		}

		BigDecimal crb = BigDecimal.ZERO;
		String appCurrency = SysParamUtil.getAppCurrency();

		for (CustomerIncome ci : incDtls) {
			ci.setToCcy(appCurrency);

			int months = DateUtil.getMonthsBetween(ci.getFinStartDate(), ci.getMaturityDate());
			BigDecimal totalRepayAmt = ci.getTotalRepayAmt();
			if (months > 0) {
				totalRepayAmt = ci.getTotalRepayAmt().divide(new BigDecimal(months), RoundingMode.HALF_UP);
			}

			crb = crb.add(CalculationUtil.getConvertedAmount(ci.getFinCcy(), ci.getToCcy(), totalRepayAmt));
		}

		return PennantApplicationUtil.formateAmount(crb, CurrencyUtil.getFormat((PennantConstants.LOCAL_CCY)));

	}

	@Override
	public BigDecimal getCustRepayProcBank(long custID, String curFinReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustId, TotalRepayAmt, MaturityDate, FinStartDate, FinCcy");
		sql.append(" From FinanceMain_Temp Where CustID = ? and RcdMaintainSts is null and FinReference <> ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		List<FinanceExposure> financeExposures = this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			FinanceExposure fe = new FinanceExposure();

			fe.setCustCif(rs.getString("CustId"));
			fe.setTotalRepayAmt(rs.getBigDecimal("TotalRepayAmt"));
			fe.setMaturityDate(JdbcUtil.getDate(rs.getDate("MaturityDate")));
			fe.setFinStartDate(JdbcUtil.getDate(rs.getDate("FinStartDate")));
			fe.setFinCCY(rs.getString("FinCcy"));

			return fe;
		}, custID, curFinReference);

		String toCcy = SysParamUtil.getAppCurrency();
		BigDecimal totalRepayAmt = BigDecimal.ZERO;

		for (FinanceExposure finExposure : financeExposures) {
			int months = DateUtil.getMonthsBetween(finExposure.getFinStartDate(), finExposure.getMaturityDate());
			BigDecimal repayAmt = finExposure.getTotalRepayAmt();

			if (months > 0) {
				repayAmt = repayAmt.divide(new BigDecimal(months), RoundingMode.HALF_UP);
			}

			totalRepayAmt = totalRepayAmt
					.add(CalculationUtil.getConvertedAmount(finExposure.getFinCCY(), toCcy, repayAmt));
		}

		totalRepayAmt = PennantApplicationUtil.formateAmount(totalRepayAmt,
				CurrencyUtil.getFormat(SysParamUtil.getAppCurrency()));

		return totalRepayAmt;
	}

	@Override
	public FinanceExposure getCoAppRepayBankTotal(String custCIF) {
		String sql = "Select CustID, CustBaseCcy, CustTotalIncome, CustTotalExpense From Customers Where CustCIF = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				FinanceExposure fe = new FinanceExposure();

				fe.setCustID(rs.getLong("CustID"));
				fe.setFinCCY(rs.getString("CustBaseCcy"));
				fe.setFinanceAmt(rs.getBigDecimal("CustTotalIncome"));
				fe.setOverdueAmt(rs.getBigDecimal("CustTotalExpense"));

				return fe;
			}, custCIF);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public String getCustWorstSts(long custID) {
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

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), String.class, custID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public String getCustWorstStsbyCurFinSts(long custID, String finReference, String curFinSts) {
		String custWorstSts = "";
		FinanceMain main = new FinanceMain();
		main.setCustID(custID);
		main.setFinReference(finReference);
		main.setFinStatus(curFinSts);

		StringBuilder selectSql = new StringBuilder("Select CustStsCode from BMTCustStatusCodes  Where DueDays = ");
		selectSql.append("(Select MAX(MaxODDays)MaxDays from (select MAX(DueDays) MaxODDays from FinanceMain F ");

		if (App.DATABASE == Database.SQL_SERVER) {
			selectSql.append(" WITH(NOLOCK) ");
		}

		selectSql.append(", BMTCustStatusCodes S");
		selectSql.append(
				" Where F.FinStatus = S.CustStsCode and F.CustID = :CustID and F.FinReference <> :FinReference	UNION ");
		selectSql.append(" Select DueDays from BMTCustStatusCodes where CustStsCode=:FinStatus ) T )  ");

		logger.debug(Literal.SQL.concat(selectSql.toString()));
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(main);

		try {
			custWorstSts = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			custWorstSts = "";
		}

		return custWorstSts == null ? "" : custWorstSts;
	}

	@Override
	public String getCustWorstStsDesc(long custID) {
		String custWorstSts = "";
		CustomerEmploymentDetail detail = new CustomerEmploymentDetail();
		detail.setCustID(custID);

		StringBuilder selectSql = new StringBuilder("Select T1.CustStsDescription From BMTCustStatusCodes T1");
		selectSql.append(" Inner Join (Select MAX(DueDays) MaxDays from FinanceMain F ");
		if (App.DATABASE == Database.SQL_SERVER) {
			selectSql.append(" WITH(NOLOCK) ");
		}
		selectSql.append(
				" , BMTCustStatusCodes S  WHERE F.FinStatus = S.CustStsCode and F.CustID = :CustID)T2 ON T1.DueDays=T2.MaxDays  ");

		logger.debug(Literal.SQL.concat(selectSql.toString()));
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);

		try {
			custWorstSts = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			custWorstSts = "";
		}

		return custWorstSts == null ? "" : custWorstSts;
	}

	@Override
	public boolean isJointCustExist(long custID) {
		String sql = "Select JointCust From Customers_AView Where CustID = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, Boolean.class, custID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}
	}

	@Override
	public long saveWIFCustomer(WIFCustomer c) {
		if (c.getCustID() == 0 || c.getCustID() == Long.MIN_VALUE) {
			c.setCustID(getNextValue("SeqWIFCustomer"));
		}

		StringBuilder sql = new StringBuilder("Insert Into WIFCustomers");
		sql.append(" (CustID, CustCRCPR, CustCtgCode, CustTypeCode, CustShrtName, CustGenderCode, CustDOB");
		sql.append(", CustSector, CustSubSector, CustMaritalSts, CustEmpSts, CustIsBlackListed");
		sql.append(", CustBlackListDate, NoOfDependents, CustBaseCcy, CustNationality, JointCust");
		sql.append(", ExistCustID, ElgRequired, SalariedCustomer, EmpName, EmpDept");
		sql.append(", EmpDesg, TotalIncome, TotalExpense, CustSalutationCode, CustSegment)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, c.getCustID());
			ps.setString(index++, c.getCustCRCPR());
			ps.setString(index++, c.getCustCtgCode());
			ps.setString(index++, c.getCustTypeCode());
			ps.setString(index++, c.getCustShrtName());
			ps.setString(index++, c.getCustGenderCode());
			ps.setDate(index++, JdbcUtil.getDate(c.getCustDOB()));
			ps.setString(index++, c.getCustSector());
			ps.setString(index++, c.getCustSubSector());
			ps.setString(index++, c.getCustMaritalSts());
			ps.setString(index++, c.getCustEmpSts());
			ps.setBoolean(index++, c.isCustIsBlackListed());
			ps.setDate(index++, JdbcUtil.getDate(c.getCustBlackListDate()));
			ps.setInt(index++, c.getNoOfDependents());
			ps.setString(index++, c.getCustBaseCcy());
			ps.setString(index++, c.getCustNationality());
			ps.setBoolean(index++, c.isJointCust());
			ps.setLong(index++, c.getExistCustID());
			ps.setBoolean(index++, c.isElgRequired());
			ps.setBoolean(index++, c.isSalariedCustomer());
			ps.setLong(index++, c.getEmpName());
			ps.setString(index++, c.getEmpDept());
			ps.setString(index++, c.getEmpDesg());
			ps.setBigDecimal(index++, c.getTotalIncome());
			ps.setBigDecimal(index++, c.getTotalExpense());
			ps.setString(index++, c.getCustSalutationCode());
			ps.setString(index, c.getCustSegment());
		});

		return c.getCustID();
	}

	@Override
	public void updateWIFCustomer(WIFCustomer c) {
		int recordCount = 0;

		StringBuilder sql = new StringBuilder(" Update WIFCustomers");
		sql.append(" Set CustCRCPR= ?, CustCtgCode = ?, CustTypeCode = ?");
		sql.append(", CustShrtName = ?, CustGenderCode = ?, CustDOB = ?, CustSector = ?");
		sql.append(", CustSubSector = ?, CustMaritalSts = ?, CustEmpSts = ?");
		sql.append(", CustIsBlackListed = ?, CustBlackListDate = ?, NoOfDependents = ?");
		sql.append(", CustBaseCcy = ?, CustNationality = ?, JointCust = ?, ExistCustID = ?, ElgRequired = ?");
		sql.append(", SalariedCustomer = ?, EmpName = ?, EmpDept = ?, EmpDesg = ?, TotalIncome = ?");
		sql.append(", TotalExpense = ?, CustSalutationCode = ?, CustSegment = ?");
		sql.append(" Where CustID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, c.getCustCRCPR());
			ps.setString(index++, c.getCustCtgCode());
			ps.setString(index++, c.getCustTypeCode());
			ps.setString(index++, c.getCustShrtName());
			ps.setString(index++, c.getCustGenderCode());
			ps.setDate(index++, JdbcUtil.getDate(c.getCustDOB()));
			ps.setString(index++, c.getCustSector());
			ps.setString(index++, c.getCustSubSector());
			ps.setString(index++, c.getCustMaritalSts());
			ps.setString(index++, c.getCustEmpSts());
			ps.setBoolean(index++, c.isCustIsBlackListed());
			ps.setDate(index++, JdbcUtil.getDate(c.getCustBlackListDate()));
			ps.setInt(index++, c.getNoOfDependents());
			ps.setString(index++, c.getCustBaseCcy());
			ps.setString(index++, c.getCustNationality());
			ps.setBoolean(index++, c.isJointCust());
			ps.setLong(index++, c.getExistCustID());
			ps.setBoolean(index++, c.isElgRequired());
			ps.setBoolean(index++, c.isSalariedCustomer());
			ps.setLong(index++, c.getEmpName());
			ps.setString(index++, c.getEmpDept());
			ps.setString(index++, c.getEmpDesg());
			ps.setBigDecimal(index++, c.getTotalIncome());
			ps.setBigDecimal(index++, c.getTotalExpense());
			ps.setString(index++, c.getCustSalutationCode());
			ps.setString(index++, c.getCustSegment());

			ps.setLong(index, c.getCustID());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

	}

	@Override
	public WIFCustomer getWIFCustomerByID(final long custId, String custCRCPR, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustID, CustCtgCode, CustTypeCode, CustShrtName, CustGenderCode, CustDOB, CustSector");
		sql.append(", CustSubSector, CustMaritalSts, CustEmpSts, CustIsBlackListed, CustBlackListDate, CustCRCPR");
		sql.append(", NoOfDependents, CustBaseCcy, CustNationality, JointCust, ExistCustID, ElgRequired, CustSegment");
		sql.append(", SalariedCustomer, EmpName, EmpDept, EmpDesg, TotalIncome, TotalExpense, CustSalutationCode");

		if (type.contains("View")) {
			sql.append(", LovDescCustTypeCodeName, LovDescCustMaritalStsName, LovDescCustEmpStsName");
			sql.append(", LovDescCustNationalityName, LovDescCustSectorName, LovDescCustSubSectorName");
			sql.append(", LovDescCustGenderCodeName, LovDescCustCtgCodeName, LovDescEmpName");
			sql.append(", LovDescEmpDept, LovDescEmpDesg, LovDescCustSegmentName");
		}

		sql.append(" From WIFCustomers");
		sql.append(StringUtils.trimToEmpty(type));

		Object[] obj = new Object[] { custId };
		if (custCRCPR == null) {
			sql.append(" Where CustID = ?");
		} else {
			sql.append(" Where CustCRCPR = ?");
			obj = new Object[] { custCRCPR };
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				WIFCustomer wif = new WIFCustomer();

				wif.setCustID(rs.getLong("CustID"));
				wif.setCustCtgCode(rs.getString("CustCtgCode"));
				wif.setCustTypeCode(rs.getString("CustTypeCode"));
				wif.setCustShrtName(rs.getString("CustShrtName"));
				wif.setCustGenderCode(rs.getString("CustGenderCode"));
				wif.setCustDOB(JdbcUtil.getDate(rs.getDate("CustDOB")));
				wif.setCustSector(rs.getString("CustSector"));
				wif.setCustSubSector(rs.getString("CustSubSector"));
				wif.setCustMaritalSts(rs.getString("CustMaritalSts"));
				wif.setCustEmpSts(rs.getString("CustEmpSts"));
				wif.setCustIsBlackListed(rs.getBoolean("CustIsBlackListed"));
				wif.setCustBlackListDate(JdbcUtil.getDate(rs.getDate("CustBlackListDate")));
				wif.setCustCRCPR(rs.getString("CustCRCPR"));
				wif.setNoOfDependents(rs.getInt("NoOfDependents"));
				wif.setCustBaseCcy(rs.getString("CustBaseCcy"));
				wif.setCustNationality(rs.getString("CustNationality"));
				wif.setJointCust(rs.getBoolean("JointCust"));
				wif.setExistCustID(rs.getLong("ExistCustID"));
				wif.setElgRequired(rs.getBoolean("ElgRequired"));
				wif.setCustSegment(rs.getString("CustSegment"));
				wif.setSalariedCustomer(rs.getBoolean("SalariedCustomer"));
				wif.setEmpName(rs.getLong("EmpName"));
				wif.setEmpDept(rs.getString("EmpDept"));
				wif.setEmpDesg(rs.getString("EmpDesg"));
				wif.setCustSegment(rs.getString("CustSegment"));
				wif.setCustSegment(rs.getString("CustSegment"));
				wif.setTotalIncome(rs.getBigDecimal("TotalIncome"));
				wif.setTotalExpense(rs.getBigDecimal("TotalExpense"));
				wif.setCustSalutationCode(rs.getString("CustSalutationCode"));

				if (type.contains("View")) {
					wif.setLovDescCustTypeCodeName(rs.getString("LovDescCustTypeCodeName"));
					wif.setLovDescCustMaritalStsName(rs.getString("LovDescCustMaritalStsName"));
					wif.setLovDescCustEmpStsName(rs.getString("LovDescCustEmpStsName"));
					wif.setLovDescCustNationalityName(rs.getString("LovDescCustNationalityName"));
					wif.setLovDescCustSectorName(rs.getString("LovDescCustSectorName"));
					wif.setLovDescCustSubSectorName(rs.getString("LovDescCustSubSectorName"));
					wif.setLovDescCustGenderCodeName(rs.getString("LovDescCustGenderCodeName"));
					wif.setLovDescCustCtgCodeName(rs.getString("LovDescCustCtgCodeName"));
					wif.setLovDescEmpName(rs.getString("LovDescEmpName"));
					wif.setLovDescEmpDept(rs.getString("LovDescEmpDept"));
					wif.setLovDescEmpDesg(rs.getString("LovDescEmpDesg"));
					wif.setLovDescCustSegmentName(rs.getString("LovDescCustSegmentName"));
				}

				return wif;
			}, obj);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public String getCustomerByCRCPR(String custCRCPR, String type) {
		StringBuilder sql = new StringBuilder("Select CustCIF From Customers");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustCRCPR = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), String.class, custCRCPR);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public Date getCustBlackListedDate(final String custCRCPR, String type) {
		StringBuilder sql = new StringBuilder("Select AbuserExpDate  From EQNAbuserList");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where AbuserIDNumber = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Date.class, custCRCPR);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public void updateProspectCustomer(Customer customer) {
		long custID = customer.getCustID();

		if (custID == 0) {
			return;
		}
		String sql = "Update Customers set CustCoreBank = ? where CustID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, customer.getCustCoreBank(), custID);

	}

	@Override
	public ProspectCustomer getProspectCustomer(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustID, CustCIF, CustShrtName, CustCtgCode, CustDftBranch");
		sql.append(" From WIFProspectCustomer");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNUm) -> {
				ProspectCustomer pc = new ProspectCustomer();

				pc.setCustId(rs.getLong("CustID"));
				pc.setCustCIF(rs.getString("CustCIF"));
				pc.setCustShrtName(rs.getString("CustShrtName"));
				pc.setCustCtgCode(rs.getString("CustCtgCode"));
				pc.setCustDftBranch(rs.getString("CustDftBranch"));

				return pc;

			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public boolean isAvailableCustomer(final long id, String type) {
		StringBuilder sql = new StringBuilder("Select count(CustID) From Customers");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, id) > 0;
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}
	}

	@Override
	public String getCustCRCPRById(long custId, String type) {
		StringBuilder sql = new StringBuilder("Select CustCRCPR From Customers");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), String.class, custId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return "";
		}
	}

	@Override
	public void updateFromFacility(Customer customer, String type) {
		StringBuilder sql = new StringBuilder("Update Customers");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set CustCOB = ?, CustRiskCountry = ?, CustDOB = ?, CustSector = ? Where CustID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, customer.getCustCOB());
			ps.setString(index++, customer.getCustRiskCountry());
			ps.setDate(index++, JdbcUtil.getDate(customer.getCustDOB()));
			ps.setString(index++, customer.getCustSector());

			ps.setLong(index, customer.getCustID());
		});
	}

	@Override
	public Customer getCustomerByID(final long id) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustCIF, CustID, CustGroupID, CustCtgCode, CustStsChgDate, CustShrtName, CustCRCPR");
		sql.append(", CustDftBranch, CasteId, ReligionId, SubCategory");
		sql.append(" from Customers");
		sql.append(" Where CustID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
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
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<FinanceEnquiry> getCustomerFinanceDetailById(Customer customer) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinReference, fm.FinType, fm.FinStatus, fm.FinStartDate, fm.FinCcy, fm.FinAmount");
		sql.append(", fm.DownPayment, fm.FeeChargeAmt, fm.FinCurrAssetValue ");
		sql.append(", fm.FinRepaymentAmount, fm.NumberOfTerms, ft.FintypeDesc as LovDescFinTypeName");
		sql.append(", coalesce(t6.MaxinstAmount, 0) MaxInstAmount");
		sql.append(" from FinanceMain fm");
		sql.append(" inner join Customers c on c.CustID = fm.CustID");
		sql.append(" inner join RMTfinanceTypes ft on ft.Fintype = fm.FinType");
		sql.append(" left join (select FinReference, (NSchdPri+NSchdPft) MaxInstAmount");
		sql.append(" from FinPftdetails) t6 on t6.FinReference = fm.Finreference");
		if (CustomerExtension.CUST_CORE_BANK_ID) {
			sql.append(" where c.CustCoreBank = ?");
		} else {
			sql.append(" where c.CustID = ?");
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
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
			fm.setFinRepaymentAmount(rs.getBigDecimal("FinRepaymentAmount"));
			fm.setNumberOfTerms(rs.getInt("NumberOfTerms"));
			fm.setLovDescFinTypeName(rs.getString("LovDescFinTypeName"));
			fm.setMaxInstAmount(rs.getBigDecimal("MaxInstAmount"));

			return fm;
		}, CustomerExtension.CUST_CORE_BANK_ID ? customer.getCustCoreBank() : customer.getCustID());
	}

	@Override
	public boolean financeExistForCustomer(long custID) {
		StringBuilder sql = new StringBuilder("Select count(CustID) From (");
		sql.append(" Select CustID From FinanceMain_Temp Where CustID = ?");
		sql.append(" union all");
		sql.append(" Select CustID From FinanceMain Where CustID = ?");
		sql.append(" and not exists (Select 1 from FinanceMain_Temp where FinID = FinanceMain.FinID))");

		logger.debug(Literal.SQL.concat(sql.toString()));
		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, custID, custID) > 0;
	}

	@Override
	public long getCustCRCPRByCustId(String custCRCPR) {
		StringBuilder sql = new StringBuilder("Select CustId From Customers_Temp Where CustCRCPR = ?");
		sql.append(" union all");
		sql.append(" Select CustId From Customers Where CustCRCPR = ?");
		sql.append(" Where not exists (Select 1 from Customers_Temp where CustId = Customers.CustId)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Long.class, custCRCPR, custCRCPR);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return Long.valueOf(0);
		}
	}

	@Override
	public WIFCustomer getWIFByCustCRCPR(String custCRCPR, String type) {
		WIFCustomer customer = new WIFCustomer();
		customer.setCustCRCPR(custCRCPR);

		StringBuilder selectSql = new StringBuilder("Select * ");
		selectSql.append(" FROM WIFCustomers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustCRCPR =:CustCRCPR");

		logger.debug(Literal.SQL.concat(selectSql.toString()));
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<WIFCustomer> typeRowMapper = BeanPropertyRowMapper.newInstance(WIFCustomer.class);
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isDuplicateCrcpr(long custId, String custCRCPR) {
		String sql = QueryUtil.getCountQuery(new String[] { "Customers_Temp", "Customers" },
				"CustID != ? and CustCRCPR = ?");

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, Integer.class, custId, custCRCPR, custId, custCRCPR) > 0;
	}

	@Override
	public String getCustCoreBankIdByCIF(String custCIF) {

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustCIF", custCIF);

		StringBuilder selectSql = new StringBuilder("SELECT CustCoreBank ");
		selectSql.append(" FROM  Customers");
		selectSql.append(" Where CustCIF=:CustCIF");

		logger.debug(Literal.SQL.concat(selectSql.toString()));

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void updateCustSuspenseDetails(Customer aCustomer, String tableType) {

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

	}

	@Override
	public void saveCustSuspMovements(Customer aCustomer) {

		StringBuffer insertSql = new StringBuffer();
		insertSql.append("INSERT INTO CustSuspMovements ");
		insertSql.append("(CustID, CustSuspEffDate, CustSuspAprDate, CustSuspMvtType, CustSuspRemarks) ");
		insertSql.append(" VALUES(:CustID, :CustSuspEffDate, :CustSuspAprDate, :CustSuspMvtType, :CustSuspRemarks) ");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(aCustomer);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

	}

	@Override
	public String getCustSuspRemarks(long custID) {

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustID", custID);

		StringBuffer selectSql = new StringBuffer();
		selectSql.append(" Select T1.CustSuspRemarks FROM CustSuspMovements T1 INNER JOIN ");
		selectSql.append(
				" (Select CustID,MAX(CustSuspEffDate) MaxSuspEffDate FROM CustSuspMovements Group by CustID) T2 ");
		selectSql.append(" ON T1.CustID =T2.CustID and T1.CustSuspEffDate =T2.MaxSuspEffDate where T1.CustID=:CustID");

		logger.debug("insertSql: " + selectSql.toString());

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public Customer getSuspendCustomer(Long custID) {

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustID", custID);

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT CustID, CustCIF, CustShrtName, CustDftBranch, CustSts, CustStsChgDate, custSuspSts,");
		selectSql.append(" CasteId, ReligionId, SubCategory,");
		selectSql.append(" custSuspDate, custSuspTrigger From Customers ");
		selectSql.append(" Where CustID = :CustID AND custSuspTrigger = 'M'");

		logger.debug("insertSql: " + selectSql.toString());

		try {
			RowMapper<Customer> typeRowMapper = BeanPropertyRowMapper.newInstance(Customer.class);
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
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
		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
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

		Customer customer = new Customer();
		customer.setCustCIF(custCIF);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT COUNT(*) FROM Customers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE CustCIF = :CustCIF");

		logger.debug(Literal.SQL.concat(selectSql.toString()));

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);

		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	/**
	 * Get Customer Core Bank Id
	 * 
	 * @param custCoreBank
	 */
	public boolean getCustomerByCoreBankId(String custCoreBank) {

		Customer customer = new Customer();
		customer.setCustCoreBank(custCoreBank);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT COUNT(*) FROM Customers ");
		selectSql.append(" WHERE CustCoreBank = :CustCoreBank");

		logger.debug(Literal.SQL.concat(selectSql.toString()));

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);

		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class) > 0;
	}

	@Override
	public void updateCustStatus(String custStatus, Date statusChgdate, long custId) {

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustSts", custStatus);
		source.addValue("CustStsChgDate", statusChgdate);
		source.addValue("CustId", custId);
		StringBuilder selectSql = new StringBuilder("Update Customers  ");
		selectSql.append(" Set CustSts = :CustSts, CustStsChgDate= :CustStsChgDate WHERE CustId=:CustId ");
		logger.debug(Literal.SQL.concat(selectSql.toString()));

		this.jdbcTemplate.update(selectSql.toString(), source);
	}

	@Override
	public String getCustomerStatus(long custId) {
		String sql = "Select CustSts From Customers where CustID = ?";

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, String.class, custId);
	}

	public Customer getCustomerEOD(final long custID) {
		StringBuilder sql = new StringBuilder(getCustomerEODQuery());
		sql.append(" Where CustID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		List<Customer> list = this.jdbcOperations.query(sql.toString(), getCustomerEODRowMapper(), custID);

		if (list.isEmpty()) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}

		Collections.sort(list, new Comparator<Customer>() {
			@Override
			public int compare(Customer c1, Customer c2) {
				return Long.valueOf(c2.getCustID()).compareTo(Long.valueOf(c1.getCustID()));
			}
		});

		return list.get(0);
	}

	public Customer getCustomerEOD(final String coreBankId) {
		StringBuilder sql = new StringBuilder(getCustomerEODQuery());
		sql.append(" Where CustCoreBank = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		List<Customer> list = this.jdbcOperations.query(sql.toString(), getCustomerEODRowMapper(), coreBankId);

		if (list.isEmpty()) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}

		Collections.sort(list, new Comparator<Customer>() {
			@Override
			public int compare(Customer c1, Customer c2) {
				return Long.valueOf(c2.getCustID()).compareTo(Long.valueOf(c1.getCustID()));
			}
		});

		return list.get(0);
	}

	@Override
	public void updateCustAppDate(CustEODEvent custEODEvent) {
		Customer customer = custEODEvent.getCustomer();
		long custID = customer.getCustID();
		String corBankID = customer.getCustCoreBank();

		String newCustStatus = null;

		if (custEODEvent.isUpdCustomer()) {
			newCustStatus = customer.getCustSts();
		}

		StringBuilder sql = new StringBuilder("Update Customers");
		sql.append(" set CustAppDate = ?");

		if (newCustStatus != null) {
			sql.append(", CustSts = ?");
		}

		if (CustomerExtension.CUST_CORE_BANK_ID) {
			sql.append(" Where CustCoreBank = ?");
		} else {
			sql.append(" Where CustId = ?");
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;
			ps.setDate(++index, JdbcUtil.getDate(custEODEvent.getEventProperties().getNextDate()));

			String updateCustStatus = null;
			if (custEODEvent.isUpdCustomer()) {
				updateCustStatus = customer.getCustSts();
			}

			if (updateCustStatus != null) {
				ps.setString(++index, updateCustStatus);
			}

			if (CustomerExtension.CUST_CORE_BANK_ID) {
				ps.setString(++index, corBankID);
			} else {
				ps.setLong(++index, custID);
			}
		});
	}

	@Override
	public Date getCustAppDate(long custId) {
		String sql = "select CustAppDate from Customers where CustId = ?";
		try {
			return this.jdbcOperations.queryForObject(sql, Date.class, custId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * Fetch the Record Customers details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return Customer
	 */
	@Override
	public List<Customer> getCustomerByGroupID(final long custGroupID) {

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
		selectSql.append(" , CasteId, ReligionId, SubCategory, ApplicationNo, Dnd, Vip ");
		selectSql.append(" FROM  Customers");
		selectSql.append(" Where CustGroupID =:CustGroupID");

		logger.debug(Literal.SQL.concat(selectSql.toString()));
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<Customer> typeRowMapper = BeanPropertyRowMapper.newInstance(Customer.class);
		List<Customer> list = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);

		return list;
	}

	@Override
	public int updateCustCRCPR(String custDocTitle, long custID) {
		int recordCount = 0;

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

		return recordCount;

	}

	/**
	 * Method for validating customers in Customer Group
	 * 
	 */
	@Override
	public boolean customerExistingCustGrp(long custGrpID, String type) {

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("CustGroupID", custGrpID);

		StringBuilder selectSql = new StringBuilder("SELECT  COUNT(CustGroupID)  FROM  Customers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustGroupID = :CustGroupID");

		logger.debug(Literal.SQL.concat(selectSql.toString()));
		return this.jdbcTemplate.queryForObject(selectSql.toString(), mapSqlParameterSource, Integer.class) > 0;
	}

	public int getCustCountByDealerId(long dealerId) {

		Customer customer = new Customer();
		customer.setCustRO1(dealerId);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From Customers");
		selectSql.append(" Where CustRO1 =:CustRO1");

		logger.debug(Literal.SQL.concat(selectSql.toString()));
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);

		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	/**
	 * Method for validating customers in Caste
	 * 
	 */
	@Override
	public boolean isCasteExist(long casteId, String type) {

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("CasteId", casteId);

		StringBuilder selectSql = new StringBuilder("SELECT  COUNT(CasteId)  FROM  Customers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CasteId = :CasteId");

		logger.debug(Literal.SQL.concat(selectSql.toString()));
		return this.jdbcTemplate.queryForObject(selectSql.toString(), mapSqlParameterSource, Integer.class) > 0;
	}

	/**
	 * Method for validating customers in Religion
	 * 
	 */
	@Override
	public boolean isReligionExist(long religionId, String type) {

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("ReligionId", religionId);

		StringBuilder selectSql = new StringBuilder("SELECT  COUNT(ReligionId)  FROM  Customers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ReligionId = :ReligionId");

		logger.debug(Literal.SQL.concat(selectSql.toString()));
		return this.jdbcTemplate.queryForObject(selectSql.toString(), mapSqlParameterSource, Integer.class) > 0;
	}

	@Override
	public int getCustomerCountByCustID(long custID, String type) {
		// TODO Auto-generated method stub

		Customer customer = new Customer();
		customer.setCustID(custID);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT COUNT(*) FROM Customers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE CustID = :CustID");

		logger.debug(Literal.SQL.concat(selectSql.toString()));

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);

		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public List<Customer> getCustomerDetailsByCRCPR(String custCRCPR, String custCtgCode, String type) {

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

		logger.debug(Literal.SQL.concat(selectSql.toString()));
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<Customer> typeRowMapper = BeanPropertyRowMapper.newInstance(Customer.class);

		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public Customer getCustomerByCoreBankId(String externalCif, String type) {

		Customer customer = new Customer();
		customer.setCustCoreBank(externalCif);
		StringBuilder selectSql = selectCustomerBasicInfo(type);
		selectSql.append(" Where CustCoreBank = :CustCoreBank");
		logger.debug(Literal.SQL.concat(selectSql.toString()));
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<Customer> typeRowMapper = BeanPropertyRowMapper.newInstance(Customer.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * Fetch the Record Customers details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return Customer
	 */
	@Override
	public String getCustomerByCRCPR(final String custCRCPR, final String custCtgCode, String type) {
		WIFCustomer customer = new WIFCustomer();
		customer.setCustCRCPR(custCRCPR);
		customer.setCustCtgCode(custCtgCode);

		StringBuilder selectSql = new StringBuilder(" SELECT CustCIF ");
		selectSql.append(" FROM Customers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustCRCPR =:CustCRCPR");
		selectSql.append(" And   custCtgCode =:custCtgCode");

		logger.debug(Literal.SQL.concat(selectSql.toString()));
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
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
		RowMapper<Customer> typeRowMapper = BeanPropertyRowMapper.newInstance(Customer.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
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
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
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
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}
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

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { custCIF, custCIF },
					new RowMapper<Long>() {

						@Override
						public Long mapRow(ResultSet rs, int arg1) throws SQLException {
							return rs.getLong("CustId");
						}
					});

		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
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
			c.setCustCoreBank(rs.getString("CustCoreBank"));
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

	@Override
	public String getCustomerIdCIF(Long custId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select CustCIF");
		sql.append(" from Customers");
		sql.append(" where custId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { custId }, new RowMapper<String>() {

				@Override
				public String mapRow(ResultSet rs, int arg1) throws SQLException {
					return rs.getString("CustCIF");
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public Customer getCustomerForPresentment(long id) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustID, CustGroupID");
		sql.append(" From Customers");
		sql.append(" Where CustId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return jdbcOperations.queryForObject(sql.toString(), new Object[] { id }, new RowMapper<Customer>() {

				@Override
				public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
					Customer c = new Customer();
					c.setCustID(rs.getLong("CustID"));
					c.setCustGroupID(rs.getLong("CustGroupID"));

					return c;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isCustTypeExists(String custType, String type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("CustTypeCode", custType);

		StringBuilder sql = new StringBuilder("SELECT  COUNT(CustTypeCode)  FROM  Customers");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustTypeCode = :CustTypeCode");

		logger.debug(Literal.SQL.concat(sql.toString()));
		return this.jdbcTemplate.queryForObject(sql.toString(), mapSqlParameterSource, Integer.class) > 0;
	}

	@Override
	public String getExternalCibilResponse(String cif, String tableName) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("Select JsonResponse from ");
		sql.append(tableName);
		sql.append(" where  Reference = :Reference ");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", cif);
		logger.debug(Literal.SQL.concat(sql.toString()));
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<String> isDuplicateCRCPR(long custId, String custCRCPR, String custCtgCode) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("select custCIF");
		sql.append(" FROM  Customers");
		sql.append("_View");
		sql.append(" Where CustCRCPR =:CustCRCPR");
		if (custCtgCode != null) {
			sql.append(" and CustCtgCode=:CustCtgCode");
		}

		logger.trace("Sql: " + sql.toString());
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("CustCRCPR", custCRCPR);
		if (custCtgCode != null) {
			parameterSource.addValue("CustCtgCode", custCtgCode);
		}

		return this.jdbcTemplate.queryForList(sql.toString(), parameterSource, String.class);
	}

	@Override
	public List<FinanceEnquiry> getCustomerFinances(long custId, long finID, String segmentType) {
		StringBuilder sql = new StringBuilder("Select fm.FinType, fm.FinID, fm.FinReference");
		sql.append(", fm.FinStartDate, fm.FinApprovedDate, fm.FirstRepay, fm.RepayFrq");
		sql.append(", fm.FinAssetValue, fm.NumberOfTerms, fm.MaturityDate, fm.ClosingStatus, fm.ClosedDate");
		sql.append(", pd.totalpribal Future_Schedule_Prin, (pd.odprincipal + pd.odprofit) Instalment_Due");
		sql.append(", pd.LatestRpyDate, (pd.totalpftpaid + pd.totalpripaid) Instalment_Paid, pd.CurReducingRate");
		sql.append(", pd.totalprischd Total_Pri_Schd, pd.totalpripaid Total_Pri_Paid, pd.totalpftschd Total_Pft_Schd");
		sql.append(", pd.totalpftpaid Total_Pft_Paid, pd.CurOdDays, ci.CustIncome, '' OwnerShip");
		sql.append(", ma.Bounce_Due, ma.Bounce_Paid, fo.Late_Payment_Penalty_Due");
		sql.append(", fo.Late_Payment_Penalty_Paid, fe.Excess_Amount, fe.Excess_Amt_Paid");
		sql.append(" FROM FinanceMain fm");
		sql.append(" INNER JOIN FinPftDetails pd ON pd.FinID = fm.FinID");
		sql.append(" INNER JOIN CUSTOMERS c ON c.CustId = fm.CustId");
		sql.append(" LEFT JOIN (SELECT fo1.finid, sum(fo1.totpenaltybal) late_payment_penalty_due");
		sql.append(", sum(fo1.totpenaltypaid) late_payment_penalty_paid");
		sql.append(" FROM finoddetails fo1 GROUP BY fo1.finid) fo ON fo.finid = fm.finid");
		sql.append(" LEFT JOIN (SELECT ma_1.finid, sum(COALESCE(ma_1.adviseamount, 0) - COALESCE(ma_1.paidamount, 0) ");
		sql.append(" - COALESCE(ma_1.waivedamount, 0)) bounce_due,");
		sql.append(" sum(ma_1.paidamount) bounce_paid FROM manualadvise ma_1 WHERE ma_1.bounceid > 0");
		sql.append(" GROUP BY ma_1.finid) ma ON ma.finid = fm.finid");
		sql.append(" LEFT JOIN (SELECT fea.finid, sum(fea.amount)  excess_amount, sum(COALESCE(fea.utilisedamt, 0)");
		sql.append(" + COALESCE(fea.reservedamt, 0)) excess_amt_paid ");
		sql.append(" FROM finexcessamount fea GROUP BY fea.FinID) fe ON fe.FinID = fm.FinID");
		sql.append(" LEFT JOIN (SELECT ci1.custid, COALESCE(sum(ci1.custincome), 0) custincome");
		sql.append(" FROM customerincomes ci1 GROUP BY ci1.custid) ci ON ci.custid = fm.custid");
		sql.append(" Where c.CustID = ? and c.CustCtgCode = ?");

		if (PennantConstants.PFF_CUSTCTG_INDIV.equals(segmentType)) {
			sql.append(" and fm.FinID = ?");
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, custId);
			ps.setString(index++, segmentType);

			if (PennantConstants.PFF_CUSTCTG_INDIV.equals(segmentType)) {
				ps.setLong(index, finID);
			}
		}, (rs, rowNum) -> {
			FinanceEnquiry finEnqy = new FinanceEnquiry();

			finEnqy.setFinType(rs.getString("FinType"));
			finEnqy.setFinID(rs.getLong("FinID"));
			finEnqy.setFinReference(rs.getString("FinReference"));
			finEnqy.setFinStartDate(rs.getDate("FinStartDate"));
			finEnqy.setFinApprovedDate(rs.getDate("FinApprovedDate"));
			finEnqy.setFirstRepay(rs.getBigDecimal("FirstRepay"));
			finEnqy.setRepayFrq(rs.getString("RepayFrq"));
			finEnqy.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
			finEnqy.setNumberOfTerms(rs.getInt("NumberOfTerms"));
			finEnqy.setMaturityDate(rs.getDate("MaturityDate"));
			finEnqy.setClosingStatus(rs.getString("ClosingStatus"));
			finEnqy.setClosedDate(rs.getDate("ClosedDate"));
			finEnqy.setFutureSchedulePrin(rs.getBigDecimal("Future_Schedule_Prin"));
			finEnqy.setInstalmentDue(rs.getBigDecimal("Instalment_Due"));
			finEnqy.setLatestRpyDate(rs.getDate("LatestRpyDate"));
			finEnqy.setInstalmentPaid(rs.getBigDecimal("Instalment_Paid"));
			finEnqy.setRepayProfitRate(rs.getBigDecimal("CurReducingRate"));
			finEnqy.setTotalPriSchd(rs.getBigDecimal("Total_Pri_Schd"));
			finEnqy.setTotalPriPaid(rs.getBigDecimal("Total_Pri_Paid"));
			finEnqy.setTotalPftSchd(rs.getBigDecimal("Total_Pft_Schd"));
			finEnqy.setTotalPftPaid(rs.getBigDecimal("Total_Pft_Paid"));
			finEnqy.setCurODDays(rs.getInt("CurOdDays"));
			finEnqy.setSvAmount(rs.getBigDecimal("CustIncome"));
			finEnqy.setOwnership(rs.getString("OwnerShip"));
			finEnqy.setBounceDue(rs.getBigDecimal("Bounce_Due"));
			finEnqy.setBouncePaid(rs.getBigDecimal("Bounce_Paid"));
			finEnqy.setLatePaymentPenaltyDue(rs.getBigDecimal("Late_Payment_Penalty_Due"));
			finEnqy.setLatePaymentPenaltyPaid(rs.getBigDecimal("Late_Payment_Penalty_Paid"));
			finEnqy.setExcessAmount(rs.getBigDecimal("Excess_Amount"));
			finEnqy.setExcessAmtPaid(rs.getBigDecimal("Excess_Amt_Paid"));

			return finEnqy;
		});
	}

	@Override
	public boolean isPanFoundByCustIds(List<Long> coAppCustIds, String panNumber) {
		StringBuilder sql = new StringBuilder("Select count(CustCrCpr) From Customers");
		sql.append(" Where CustCrCpr = ? and CustID in (");

		Object[] obj = new Object[coAppCustIds.size() + 1];
		obj[0] = panNumber;
		int i = 1;

		for (Long custID : coAppCustIds) {
			sql.append(" ?,");
			obj[i++] = custID;
		}

		sql.deleteCharAt(sql.length() - 1);
		sql.append(")");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, obj) > 0;
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}
	}

	@Override
	public String getCustDefaulBranchByCIF(String custCIF) {
		String sql = "Select CustDftBranch  from Customers  Where CustCIF = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return jdbcOperations.queryForObject(sql, String.class, custCIF);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public long getCustIDByCIF(String custCIF) {
		String sql = "Select CustID From Customers Where CustCIF = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return jdbcOperations.queryForObject(sql, Long.class, custCIF);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	@Override
	public Customer getCustomerForAutoRefund(long custID) {
		String sql = "Select CustCtgCode, custTypeCode From Customers Where CustId = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				Customer c = new Customer();
				c.setCustCtgCode(rs.getString("CustCtgCode"));
				c.setCustTypeCode(rs.getString("custTypeCode"));

				return c;
			}, custID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public String getCustShrtNameByFinID(long finID) {
		String sql = "Select CustShrtName From Customers c Inner Join FinanceMain fm on fm.CustID = c.CustID Where FinID = ?";

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, String.class, finID);
	}

	private String getCustomerEODQuery() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustID, CustCIF, CustCoreBank, CustCtgCode, CustTypeCode, CustDftBranch, CustPOB");
		sql.append(", CustCOB, CustGroupID, CustSts, CustStsChgDate, CustIsStaff, CustIndustry, CustSector");
		sql.append(", CustSubSector, CustEmpSts, CustSegment, CustSubSegment, CustAppDate, CustParentCountry");
		sql.append(", CustResdCountry, CustRiskCountry, CustNationality, SalariedCustomer, CustSuspSts");
		sql.append(", CustSuspDate, CustSuspTrigger, CasteId, ReligionId, SubCategory, CustShrtName, CustCRCPR");
		sql.append(" From Customers");

		return sql.toString();
	}

	private RowMapper<Customer> getCustomerEODRowMapper() {
		return new RowMapper<Customer>() {
			@Override
			public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
				Customer c = new Customer();

				c.setCustID(rs.getLong("CustID"));
				c.setCustCIF(rs.getString("CustCIF"));
				c.setCustCoreBank(rs.getString("CustCoreBank"));
				c.setCustCtgCode(rs.getString("CustCtgCode"));
				c.setCustTypeCode(rs.getString("CustTypeCode"));
				c.setCustDftBranch(rs.getString("CustDftBranch"));
				c.setCustPOB(rs.getString("CustPOB"));
				c.setCustCOB(rs.getString("CustCOB"));
				c.setCustGroupID(rs.getLong("CustGroupID"));
				c.setCustSts(rs.getString("CustSts"));
				c.setCustStsChgDate(rs.getTimestamp("CustStsChgDate"));
				c.setCustIsStaff(rs.getBoolean("CustIsStaff"));
				c.setCustIndustry(rs.getString("CustIndustry"));
				c.setCustSector(rs.getString("CustSector"));
				c.setCustSubSector(rs.getString("CustSubSector"));
				c.setCustEmpSts(rs.getString("CustEmpSts"));
				c.setCustSegment(rs.getString("CustSegment"));
				c.setCustSubSegment(rs.getString("CustSubSegment"));
				c.setCustAppDate(rs.getTimestamp("CustAppDate"));
				c.setCustParentCountry(rs.getString("CustParentCountry"));
				c.setCustResdCountry(rs.getString("CustResdCountry"));
				c.setCustRiskCountry(rs.getString("CustRiskCountry"));
				c.setCustNationality(rs.getString("CustNationality"));
				c.setSalariedCustomer(rs.getBoolean("SalariedCustomer"));
				c.setCustSuspSts(rs.getBoolean("CustSuspSts"));
				c.setCustSuspDate(rs.getTimestamp("CustSuspDate"));
				c.setCustSuspTrigger(rs.getString("CustSuspTrigger"));
				c.setCasteId(rs.getLong("CasteId"));
				c.setReligionId(rs.getLong("ReligionId"));
				c.setSubCategory(rs.getString("SubCategory"));
				c.setCustShrtName(rs.getString("CustShrtName"));
				c.setCustCRCPR(rs.getString("CustCRCPR"));

				return c;
			}
		};

	}

	@Override
	public CustomerCoreBank getCoreBankByFinID(long finID) {
		String sql = "Select c.CustId, c.CustCoreBank From Customers c Inner Join FinanceMain fm on fm.CustID = c.CustID Where FinID = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, i) -> {
				CustomerCoreBank c = new CustomerCoreBank();

				c.setCustID(rs.getLong("CustID"));
				c.setCustCoreBank(rs.getString("CustCoreBank"));

				return c;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public CustomerCoreBank getCoreBankByCustID(long custID) {
		String sql = "Select c.CustId,c.CustCoreBank From Customers c where CustID = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, i) -> {
				CustomerCoreBank c = new CustomerCoreBank();

				c.setCustID(rs.getLong("CustID"));
				c.setCustCoreBank(rs.getString("CustCoreBank"));

				return c;
			}, custID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}

	}

	@Override
	public Date getCustomerDOBByCustID(long custID) {
		String sql = "Select CustDOB From Customers Where CustID = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, Date.class, custID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<Long> getByCustShrtName(String CustShrtName, TableType tableType) {
		Object[] object = new Object[] { CustShrtName + "%" };

		StringBuilder sql = new StringBuilder();
		switch (tableType) {
		case MAIN_TAB:
			sql.append(" Select CustID From Customers Where CustShrtName like ?");
			break;
		case TEMP_TAB:
			object = new Object[] { CustShrtName + "%" };
			sql.append(" Select CustID From Customers_Temp  Where CustShrtName like ?");
			break;
		case BOTH_TAB:
			object = new Object[] { CustShrtName + "%", CustShrtName + "%" };
			sql.append(" Select CustID From Customers_Temp  Where CustShrtName like ?");
			sql.append(" Union All");
			sql.append(" Select CustID From Customers c Where CustShrtName like ?");
			sql.append(" and not exists (Select 1 From Customers_Temp Where CustID = c.CustID)");
		default:
			break;
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		List<Long> list = new ArrayList<>();

		SqlRowSet rowSet = this.jdbcOperations.queryForRowSet(sql.toString(), object);

		while (rowSet.next()) {

			if (list.size() == 50) {
				break;
			}

			list.add(rowSet.getLong("CustID"));
		}

		return list;
	}

	@Override
	public List<Long> getByCustCRCPR(String custCRCPR, TableType tableType) {
		Object[] object = new Object[] { custCRCPR };

		StringBuilder sql = new StringBuilder();
		switch (tableType) {
		case MAIN_TAB:
			sql.append(" Select CustID From Customers Where CustCRCPR = ?");
			break;
		case TEMP_TAB:
			object = new Object[] { custCRCPR };
			sql.append(" Select CustID From Customers_Temp  Where CustCRCPR = ?");
			break;
		case BOTH_TAB:
			object = new Object[] { custCRCPR, custCRCPR };
			sql.append(" Select CustID From Customers_Temp  Where CustCRCPR = ?");
			sql.append(" Union All");
			sql.append(" Select CustID From Customers c Where CustCRCPR = ?");
			sql.append(" and not exists (Select 1 From Customers_Temp Where CustID = c.CustID)");
		default:
			break;
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForList(sql.toString(), Long.class, object);
	}

	@Override
	public List<Long> getByAccNumber(String accNumber, TableType tableType) {
		Object[] object = new Object[] { accNumber };

		StringBuilder sql = new StringBuilder();
		switch (tableType) {
		case MAIN_TAB:
			sql.append(" Select c.CustID From Customers c");
			sql.append(" Inner Join Mandates m on m.CustID = c.CustID where m.AccNumber = ?");
			break;
		case TEMP_TAB:
			object = new Object[] { accNumber };
			sql.append(" Select c.CustID From Customers_Temp c");
			sql.append(" Inner Join Mandates m on m.CustID = c.CustID where m.AccNumber = ?");
			break;
		case BOTH_TAB:
			object = new Object[] { accNumber, accNumber };
			sql.append(" Select c.CustID From Customers_Temp c");
			sql.append(" Inner Join Mandates m on m.CustID = c.CustID where m.AccNumber = ?");
			sql.append(" Union All");
			sql.append(" Select c.CustID From Customers c");
			sql.append(" Inner Join Mandates m on m.CustID = c.CustID where m.AccNumber = ?");
			sql.append(" and not exists (Select 1 From Customers_Temp Where CustID = c.CustID)");
		default:
			break;
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForList(sql.toString(), Long.class, object);
	}

	@Override
	public List<Long> getByAccNumber(String accNumber) {

		StringBuilder sql = new StringBuilder(" Select c.CustID");
		sql.append(" From Customers c Inner Join Mandates m on m.CustID = c.CustID where m.AccNumber = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForList(sql.toString(), Long.class, accNumber);
	}

	@Override
	public List<Long> getByPhoneNumber(String phoneNumber, TableType tableType) {
		Object[] object = new Object[] { phoneNumber, 5 };

		StringBuilder sql = new StringBuilder();
		switch (tableType) {
		case MAIN_TAB:
			sql.append(" Select CustID From Customers c");
			sql.append(
					" Inner Join CustomerPhoneNumbers cp on cp.PhoneCustID = c.CustID and cp.PhoneNumber = ? and PhoneTypePriority = ?");
			break;
		case TEMP_TAB:
			object = new Object[] { phoneNumber, 5 };
			sql.append(" Select CustID From Customers_Temp c");
			sql.append(
					" Inner Join CustomerPhoneNumbers_Temp cp on cp.PhoneCustID = c.CustID and cp.PhoneNumber = ? and PhoneTypePriority = ?");
			break;
		case BOTH_TAB:
			object = new Object[] { phoneNumber, 5, phoneNumber, 5 };
			sql.append(" Select CustID From Customers_Temp c");
			sql.append(" Inner Join CustomerPhoneNumbers_Temp cp");
			sql.append(" on cp.PhoneCustID = c.CustID and cp.PhoneNumber = ? and PhoneTypePriority = ?");
			sql.append(" Union All");
			sql.append(" Select CustID From Customers c");
			sql.append(" Inner Join CustomerPhoneNumbers cp");
			sql.append(" on cp.PhoneCustID = c.CustID and cp.PhoneNumber = ? and PhoneTypePriority = ?");
			sql.append(" and not exists (Select 1 From Customers_Temp Where CustID = c.CustID)");
		default:
			break;
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForList(sql.toString(), Long.class, object);
	}

	@Override
	public List<Long> getByCustShrtNameAndPhoneNumber(String CustShrtName, String phoneNumber, TableType tableType) {
		Object[] object = new Object[] { phoneNumber, CustShrtName };

		StringBuilder sql = new StringBuilder();
		switch (tableType) {
		case MAIN_TAB:
			sql.append(" Select CustID From Customers c");
			sql.append(" Inner Join CustomerPhoneNumbers cp on cp.PhoneCustID = c.CustID and cp.PhoneNumber = ?");
			sql.append(" Where c.CustShrtName = ?");
			break;
		case TEMP_TAB:
			object = new Object[] { phoneNumber, CustShrtName };
			sql.append(" Select CustID From Customers_Temp c");
			sql.append(" Inner Join CustomerPhoneNumbers_Temp cp on cp.PhoneCustID = c.CustID and cp.PhoneNumber = ?");
			sql.append(" Where c.CustShrtName = ?");
			break;
		case BOTH_TAB:
			object = new Object[] { phoneNumber, CustShrtName, phoneNumber, CustShrtName };
			sql.append(" Select CustID From Customers_Temp c");
			sql.append(" Inner Join CustomerPhoneNumbers_Temp cp on cp.PhoneCustID = c.CustID and cp.PhoneNumber = ?");
			sql.append(" Where c.CustShrtName = ?");
			sql.append(" Union All");
			sql.append(" Select CustID From Customers c");
			sql.append(" Inner Join CustomerPhoneNumbers cp on cp.PhoneCustID = c.CustID and cp.PhoneNumber = ?");
			sql.append(" Where c.CustShrtName = ?");
			sql.append(" and not exists (Select 1 From Customers_Temp Where CustID = c.CustID)");
		default:
			break;
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForList(sql.toString(), Long.class, object);
	}

	@Override
	public List<Long> getByCustShrtNameAndDOB(String custShrtName, Date custDOB, TableType tableType) {
		Object[] object = new Object[] { custDOB, custShrtName };

		StringBuilder sql = new StringBuilder();
		switch (tableType) {
		case MAIN_TAB:
			sql.append(" Select CustID From Customers Where CustDOB = ? and CustShrtName = ?");
			break;
		case TEMP_TAB:
			object = new Object[] { custDOB, custShrtName };
			sql.append(" Select CustID From Customers_Temp  Where CustDOB = ? and CustShrtName = ?");
			break;
		case BOTH_TAB:
			object = new Object[] { custDOB, custShrtName, custDOB, custShrtName };
			sql.append(" Select CustID From Customers_Temp  Where CustDOB = ? and CustShrtName = ?");
			sql.append(" Union All");
			sql.append(" Select CustID From Customers c Where CustDOB = ? and CustShrtName = ?");
			sql.append(" and not exists (Select 1 From Customers_Temp Where CustID = c.CustID)");
		default:
			break;
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForList(sql.toString(), Long.class, object);
	}

	@Override
	public List<Long> getByCustShrtNameAndEMIAmount(String customerName, BigDecimal repayAmount) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Select fm.CustID");
		sql.append(" From FinScheduleDetails fs");
		sql.append(" Inner Join FinanceMain fm On fm.finID = fs.finID");
		sql.append(" Inner Join Customers cu on cu.CustID = fm.CustID");
		sql.append(" Where fm.FinIsActive = ? and fs.RepayAmount = ? and cu.CustShrtName = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForList(sql.toString(), Long.class, 1, repayAmount, customerName);
	}

	@Override
	public List<Long> getByCustShrtNameAndPANNumber(String custShrtName, String custCRCPR, TableType tableType) {
		Object[] object = new Object[] { custCRCPR, custShrtName };

		StringBuilder sql = new StringBuilder();
		switch (tableType) {
		case MAIN_TAB:
			sql.append(" Select CustID From Customers Where CustCRCPR = ? and CustShrtName = ?");
			break;
		case TEMP_TAB:
			object = new Object[] { custCRCPR, custShrtName };
			sql.append(" Select CustID From Customers_Temp  Where CustCRCPR = ? and CustShrtName = ?");
			break;
		case BOTH_TAB:
			object = new Object[] { custCRCPR, custShrtName, custCRCPR, custShrtName };
			sql.append(" Select CustID From Customers_Temp  Where CustCRCPR = ? and CustShrtName = ?");
			sql.append(" Union All");
			sql.append(" Select CustID From Customers c Where CustCRCPR = ? and CustShrtName = ?");
			sql.append(" and not exists (Select 1 From Customers_Temp Where CustID = c.CustID)");
		default:
			break;
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForList(sql.toString(), Long.class, object);
	}

	@Override
	public Customer getBasicDetails(long custID, TableType tableType) {
		Object[] object = new Object[] { custID };

		StringBuilder sql = new StringBuilder();
		switch (tableType) {
		case MAIN_TAB:
			sql.append(" Select CustID");
			sql.append(", CustCIF, CustSalutationCode, CustFName, CustMName, CustLName");
			sql.append(", CustCoreBank, CustShrtName, CustMotherMaiden, CustShrtNameLclLng");
			sql.append(", CustGenderCode, CustCRCPR, CustDOB");
			sql.append(" From Customers Where CustID = ?");
			break;
		case TEMP_TAB:
			sql.append(" Select CustSalutationCode, CustFName, CustMName, CustLName");
			sql.append(", CustCoreBank, CustShrtName, CustMotherMaiden, CustShrtNameLclLng");
			sql.append(", CustGenderCode, CustCRCPR, CustDOB");
			sql.append(" From Customers_Temp Where CustID = ?");
			break;
		case BOTH_TAB:
			object = new Object[] { custID, custID };

			sql.append(" Select CustSalutationCode, CustFName, CustMName, CustLName");
			sql.append(", CustCoreBank, CustShrtName, CustMotherMaiden, CustShrtNameLclLng");
			sql.append(", CustGenderCode, CustCRCPR, CustDOB");
			sql.append(" From Customers_Temp Where CustID = ?");
			sql.append(" Union All");
			sql.append(" Select CustSalutationCode, CustFName, CustMName, CustLName");
			sql.append(", CustCoreBank, CustShrtName, CustMotherMaiden, CustShrtNameLclLng");
			sql.append(", CustGenderCode, CustCRCPR, CustDOB");
			sql.append(" From Customers c Where CustID = ?");
			sql.append(" and not exists (Select 1 From Customers_Temp Where CustID = c.CustID)");
			break;
		default:
			break;
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				Customer c = new Customer();
				c.setCustSalutationCode(rs.getString("CustSalutationCode"));
				c.setCustCIF(rs.getString("CustCIF"));
				c.setCustFName(rs.getString("CustFName"));
				c.setCustMName(rs.getString("CustMName"));
				c.setCustLName(rs.getString("CustLName"));
				c.setCustCoreBank(rs.getString("CustCoreBank"));
				c.setCustShrtName(rs.getString("CustShrtName"));
				c.setCustMotherMaiden(rs.getString("CustMotherMaiden"));
				c.setCustShrtNameLclLng(rs.getString("CustGenderCode"));
				c.setCustCRCPR(rs.getString("CustCRCPR"));
				c.setCustDOB(rs.getDate("CustDOB"));

				return c;
			}, object);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<Customer> getBasicDetailsForJointCustomers(long finID, TableType tableType) {
		Object[] object = new Object[] { 1, finID };

		StringBuilder sql = new StringBuilder();
		switch (tableType) {
		case MAIN_TAB:
			sql.append(" Select c.CustCIF, c.CustID, c.CustSalutationCode, c.CustFName");
			sql.append(", c.CustMName, c.CustLName, ja.CatOfcoApplicant From Customers c");
			sql.append(" Inner Join FinanceMain fm on fm.FinIsActive = ?");
			sql.append(" Inner Join FinJointAccountDetails ja on ja.FinID = fm.FinID and ja.CustCIF = C.CustCIF");
			sql.append(" Where fm.finID = ?");
			break;
		case TEMP_TAB:
			sql.append(" Select c.CustCIF, c.CustID, c.CustSalutationCode, c.CustFName");
			sql.append(", c.CustMName, c.CustLName, ja.CatOfcoApplicant From Customers_Temp c");
			sql.append(" Inner Join FinanceMain fm on fm.FinIsActive = ?");
			sql.append(" Inner Join FinJointAccountDetails_Temp ja on ja.FinID = fm.FinID and ja.CustCIF = C.CustCIF");
			sql.append(" Where fm.finID = ?");
			break;
		case BOTH_TAB:
			object = new Object[] { 1, finID, 1, finID };

			sql.append(" Select c.CustCIF, c.CustID, c.CustSalutationCode, c.CustFName");
			sql.append(", c.CustMName, c.CustLName, ja.CatOfcoApplicant From Customers_Temp c");
			sql.append(" Inner Join FinanceMain fm on fm.FinIsActive = ?");
			sql.append(" Inner Join FinJointAccountDetails_Temp ja on ja.FinID = fm.FinID and ja.CustCIF = C.CustCIF");
			sql.append(" Where fm.finID = ?");
			sql.append(" Union All");
			sql.append(" Select c.CustID, c.CustSalutationCode, c.CustFName");
			sql.append(", c.CustMName, c.CustLName, ja.CatOfcoApplicant From Customers c");
			sql.append(" Inner Join FinanceMain fm on fm.FinIsActive = ?");
			sql.append(" Inner Join FinJointAccountDetails ja on ja.FinID = fm.FinID and ja.CustCIF = C.CustCIF");
			sql.append(" Where fm.finID = ?");
			sql.append(" and not exists (Select 1 From Customers_Temp Where CustID = c.CustID)");
			break;
		default:
			break;
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			Customer c = new Customer();
			c.setCustCIF(rs.getString("CustCIF"));
			c.setCustID(rs.getLong("CustID"));
			c.setCustSalutationCode(rs.getString("CustSalutationCode"));
			c.setCustFName(rs.getString("CustFName"));
			c.setCustMName(rs.getString("CustMName"));
			c.setCustLName(rs.getString("CustLName"));
			c.setRelationWithCust(rs.getString("CatOfcoApplicant"));

			return c;
		}, object);

	}

	@Override
	public List<Long> getByCustShrtNameDOBAndFinType(String custShrtName, Date custDOB, String finType,
			TableType tableType) {
		Object[] object = new Object[] { custDOB, custShrtName, finType };

		StringBuilder sql = new StringBuilder();
		switch (tableType) {
		case MAIN_TAB:
			sql.append(" Select c.CustID From Customers c");
			sql.append(" Inner Join FinanceMain fm on fm.CustID = c.CustID");
			sql.append(" Where c.CustDOB = ? and c.CustShrtName = ? And fm.FinType = ?");
			break;
		case TEMP_TAB:
			object = new Object[] { custDOB, custShrtName, finType };
			sql.append(" Select c.CustID From Customers_Temp c");
			sql.append(" Inner Join FinanceMain_Temp fm on fm.CustID = c.CustID");
			sql.append(" Where c.CustDOB = ? and c.CustShrtName = ? And fm.FinType = ?");
			break;
		case BOTH_TAB:
			object = new Object[] { custDOB, custShrtName, finType, custDOB, custShrtName, finType };
			sql.append(" Select c.CustID From Customers_Temp c");
			sql.append(" Inner Join FinanceMain_Temp fm on fm.CustID = c.CustID");
			sql.append(" Where c.CustDOB = ? and c.CustShrtName = ? And fm.FinType = ?");
			sql.append(" Union All");
			sql.append(" Select c.CustID From Customers c");
			sql.append(" Inner Join FinanceMain fm on fm.CustID = c.CustID");
			sql.append(" Where c.CustDOB = ? and c.CustShrtName = ? And fm.FinType = ?");
			sql.append(" and not exists (Select 1 From Customers_Temp Where CustID = c.CustID)");
		default:
			break;
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForList(sql.toString(), Long.class, object);
	}

	@Override
	public List<Long> getByCustName(String custShrtName, TableType tableType) {
		Object[] object = new Object[] { custShrtName };

		StringBuilder sql = new StringBuilder();
		switch (tableType) {
		case MAIN_TAB:
			sql.append(" Select CustID From Customers Where CustShrtName = ?");
			break;
		case TEMP_TAB:
			object = new Object[] { custShrtName };
			sql.append(" Select CustID From Customers_Temp  Where CustShrtName = ?");
			break;
		case BOTH_TAB:
			object = new Object[] { custShrtName, custShrtName };
			sql.append(" Select CustID From Customers_Temp  Where CustShrtName = ?");
			sql.append(" Union All");
			sql.append(" Select CustID From Customers c Where CustShrtName = ?");
			sql.append(" and not exists (Select 1 From Customers_Temp Where CustID = c.CustID)");
		default:
			break;
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForList(sql.toString(), Long.class, object);
	}
}
