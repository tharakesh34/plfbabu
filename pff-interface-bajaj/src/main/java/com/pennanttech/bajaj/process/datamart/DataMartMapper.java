package com.pennanttech.bajaj.process.datamart;

import java.sql.ResultSet;
import java.util.Date;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public class DataMartMapper {

	public static MapSqlParameterSource mapData(DataMartTable table, ResultSet rs, Date appDate, Date valueDate)
			throws Exception {
		return null;
	}

	public static void saveApplicantDetails(ResultSet rs, Date appDate, Date valueDate, long batchId,
			JdbcTemplate jdbcTemplate) throws Exception {

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO DM_APPLICANT_DETAILS (");
		sql.append("CUSTOMERID");
		sql.append(", CUST_TYPE");
		sql.append(", PANNO");
		sql.append(", ADDRESS1");
		sql.append(", ADDRESS2");
		sql.append(", ADDRESS3");
		sql.append(", ADDRESS4");
		sql.append(", CITY");
		sql.append(", STATE");
		sql.append(", COUNTRY");
		sql.append(", ZIPCODE");
		sql.append(", ADDRESSTYPE");
		sql.append(", EMAIL");
		sql.append(", PHONE1");
		sql.append(", PHONE2");
		sql.append(", MOBILE");
		sql.append(", FAX");
		sql.append(", EXISTING_CUST_FLAG");
		sql.append(", INDIV_CORP_FLAG");
		sql.append(", AGE");
		sql.append(", DOB");
		sql.append(", FNAME");
		sql.append(", MNAME");
		sql.append(", LNAME");
		sql.append(", GENDER");
		sql.append(", MARITAL_STATUS");
		sql.append(", NO_OF_DEPENDENT");
		sql.append(", YEARS_CURRENT_JOB");
		sql.append(", YEARS_PREV_JOB");
		sql.append(", QUALIFICATION");
		sql.append(", RESIDENCETYPE");
		sql.append(", YEARS_CURR_RESI");
		sql.append(", EMPLOYER_DESC");
		sql.append(", COMPANY_TYPE");
		sql.append(", INDUSTRYID");
		sql.append(", NATURE_OF_BUSINESS");
		sql.append(", EMPLOYMENT_TYPE");
		sql.append(", EMPDESG");
		sql.append(", OCCUPATION");
		sql.append(", ANNUAL_INCOME");
		sql.append(", GUARDIAN");
		sql.append(", BUSINESSDATE");
		sql.append(", PROCESSED_FLAG");
		sql.append(", PROCESS_DATE");
		sql.append(", SEGMENTS");
		sql.append(", CUSTOMERNAME");
		sql.append(", CONTACT_PERSON_NAME");
		sql.append(", CONSTITUTION");
		sql.append(", CUST_BANK_NAME");
		sql.append(", CUST_BANK_BRANCH");
		sql.append(", EMI_CARD_LIMIT");
		sql.append(", EMI_CARD_ACCEPT_FLAG");
		sql.append(", EMI_CARD_SWIPE_FLAG");
		sql.append(", EMI_CARD_ELIG");
		sql.append(", EMI_CARD_NO");
		sql.append(", BANK_ECS_MANDATE");
		sql.append(", OPEN_ECS_AVLB");
		sql.append(", OPEN_ECS_DATE");
		sql.append(", BUSINESS_YEAR");
		sql.append(", TITLE");
		sql.append(", COMP_NAME");
		sql.append(", YEARS_CURR_JOB");
		sql.append(", GRADE");
		sql.append(", FAMILY_CODE");
		sql.append(", MINOR");
		sql.append(", GUARDIAN_NEW");
		sql.append(", UCIN_NO");
		sql.append(", PREFERRED_ELIGIBILITY");
		sql.append(", PREFERRED_CARD_ACCEPTANCE");
		sql.append(", PREFERRED_CARD_LIMIT");
		sql.append(", CUST_BRANCHID");
		sql.append(", BATCH_ID");
		sql.append(") VALUES(");
		sql.append("?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(")");

		jdbcTemplate.update(sql.toString(), rs.getObject("CUSTOMERID"), rs.getObject("CUST_TYPE"),
				rs.getObject("PANNO"), rs.getObject("ADDRESS1"), rs.getObject("ADDRESS2"), rs.getObject("ADDRESS3"),
				rs.getObject("ADDRESS4"), rs.getObject("CITY"), rs.getObject("STATE"), rs.getObject("COUNTRY"),
				rs.getObject("ZIPCODE"), rs.getObject("ADDRESSTYPE"), rs.getObject("EMAIL"), rs.getObject("PHONE1"),
				rs.getObject("PHONE2"), rs.getObject("MOBILE"), rs.getObject("FAX"), rs.getObject("EXISTING_CUST_FLAG"),
				rs.getObject("INDIV_CORP_FLAG"), rs.getObject("AGE"), rs.getObject("DOB"), rs.getObject("FNAME"),
				rs.getObject("MNAME"), rs.getObject("LNAME"), rs.getObject("GENDER"), rs.getObject("MARITAL_STATUS"),
				rs.getObject("NO_OF_DEPENDENT"), rs.getObject("YEARS_CURRENT_JOB"), rs.getObject("YEARS_PREV_JOB"),
				rs.getObject("QUALIFICATION"), rs.getObject("RESIDENCETYPE"), rs.getObject("YEARS_CURR_RESI"),
				rs.getObject("EMPLOYER_DESC"), rs.getObject("COMPANY_TYPE"), rs.getObject("INDUSTRYID"),
				rs.getObject("NATURE_OF_BUSINESS"), rs.getObject("EMPLOYMENT_TYPE"), rs.getObject("EMPDESG"),
				rs.getObject("OCCUPATION"), rs.getObject("ANNUAL_INCOME"), rs.getObject("GUARDIAN"), appDate,
				rs.getObject("PROCESSED_FLAG"), valueDate, rs.getObject("SEGMENTS"), rs.getObject("CUSTOMERNAME"),
				rs.getObject("CONTACT_PERSON_NAME"), rs.getObject("CONSTITUTION"), rs.getObject("CUST_BANK_NAME"),
				rs.getObject("CUST_BANK_BRANCH"), rs.getObject("EMI_CARD_LIMIT"), rs.getObject("EMI_CARD_ACCEPT_FLAG"),
				rs.getObject("EMI_CARD_SWIPE_FLAG"), rs.getObject("EMI_CARD_ELIG"), rs.getObject("EMI_CARD_NO"),
				rs.getObject("BANK_ECS_MANDATE"), rs.getObject("OPEN_ECS_AVLB"), rs.getObject("OPEN_ECS_DATE"),
				rs.getObject("BUSINESS_YEAR"), rs.getObject("TITLE"), rs.getObject("COMP_NAME"),
				rs.getObject("YEARS_CURR_JOB"), rs.getObject("GRADE"), rs.getObject("FAMILY_CODE"),
				rs.getObject("MINOR"), rs.getObject("GUARDIAN_NEW"), rs.getObject("UCIN_NO"),
				rs.getObject("PREFERRED_ELIGIBILITY"), rs.getObject("PREFERRED_CARD_ACCEPTANCE"),
				rs.getObject("PREFERRED_CARD_LIMIT"), rs.getObject("CUST_BRANCHID"), batchId);

	}

	public static void saveAddressDetails(ResultSet rs, Date appDate, Date valueDate, long batchId,
			JdbcTemplate jdbcTemplate) throws Exception {

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO DM_ADDRESS_DETAILS (");
		sql.append(" ADDRESSID");
		sql.append(",CUSTOMERID");
		sql.append(",STATEID");
		sql.append(",REGIONID");
		sql.append(",CITY");
		sql.append(",STDISD");
		sql.append(",MAILINGADDRES");
		sql.append(",ADDRESS1");
		sql.append(",ADDRESS2");
		sql.append(",ADDRESS3");
		sql.append(",ZIPCODE");
		sql.append(",COUNTRY");
		sql.append(",ADDRESSTYPE");
		sql.append(",APPLICANT_TYPE");
		sql.append(",PHONE1");
		sql.append(",PHONE2");
		sql.append(",MOBILE");
		sql.append(",EMAIL");
		sql.append(",BUSINESSDATE");
		sql.append(",PROCESSFLAG");
		sql.append(",PROCESS_DATE");
		sql.append(",AREA");
		sql.append(",LANDMARK");
		sql.append(",BUSINESS_YEAR");
		sql.append(",BATCH_ID");
		sql.append(") VALUES(");
		sql.append("?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(")");

		jdbcTemplate.update(sql.toString(), rs.getObject("ADDRESSID"), rs.getObject("CUSTOMERID"),
				rs.getObject("STATEID"), rs.getObject("REGIONID"), rs.getObject("CITY"), rs.getObject("STDISD"),
				rs.getObject("MAILINGADDRESS"), rs.getObject("ADDRESS1"), rs.getObject("ADDRESS2"),
				rs.getObject("ADDRESS3"), rs.getObject("ZIPCODE"), rs.getObject("COUNTRY"), rs.getObject("ADDRESSTYPE"),
				rs.getObject("APPLICANT_TYPE"), rs.getObject("PHONE1"), rs.getObject("PHONE2"), rs.getObject("MOBILE"),
				rs.getObject("EMAIL"), appDate, rs.getObject("PROCESSFLAG"), valueDate, rs.getObject("AREA"),
				rs.getObject("LANDMARK"), rs.getObject("BUSINESS_YEAR"), batchId);
	}

	public static void saveApplicationDetails(ResultSet rs, Date appDate, Date valueDate, long batchId,
			JdbcTemplate jdbcTemplate) throws Exception {

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO DM_APPLICATION_DETAILS (");

		sql.append("APPLID");
		sql.append(",CRM_DEALID");
		sql.append(",CRSCOREDATE");
		sql.append(",CIBILSCORE");
		sql.append(",APPSCORE");
		sql.append(",BEHSCORE");
		sql.append(",DEVIATION_TYPE");
		sql.append(",DEVIATION_FLAG");
		sql.append(",DEVIATION_CODE");
		sql.append(",DEVIATION_NAME");
		sql.append(",REQ_TENURE");
		sql.append(",REQ_LOAN_AMT");
		sql.append(",LOAN_LIMIT");
		sql.append(",APPROVE_LOAN_AMT");
		sql.append(",LOAN_PURPOSE");
		sql.append(",LOAN_TYPE");
		sql.append(",CANCELLATIONDATE");
		sql.append(",CANCEL_REASON");
		sql.append(",FREQUENCY");
		sql.append(",LOAN_APPROVAL_DATE");
		sql.append(",AGREEMENTDATE");
		sql.append(",INTRATE");
		sql.append(",FLAT_RATE");
		sql.append(",IRR");
		sql.append(",GROSS_LTV");
		sql.append(",NET_LTV");
		sql.append(",COF");
		sql.append(",DEBT_BURDEN_RATIO");
		sql.append(",FOIR_DB");
		sql.append(",SCHEMEID");
		sql.append(",SCHEMEDESC");
		sql.append(",SCHEMEGROUPID");
		sql.append(",SCHEME_GROUPG_DESC");
		sql.append(",PRODUCT_CATEGORY");
		sql.append(",PROD_TYPE");
		sql.append(",PROMOTIONID");
		sql.append(",PROGRAMID");
		sql.append(",SURROGATE_FLAG");
		sql.append(",SOURCING_CHANNEL_TYPE");
		sql.append(",SOURCING_CHANNEL_NAME");
		sql.append(",REFERAL_GROUP");
		sql.append(",REFERAL_NAME");
		sql.append(",COUNTRYID");
		sql.append(",COUNTRY");
		sql.append(",REGIONID");
		sql.append(",REGION");
		sql.append(",STATEID");
		sql.append(",STATE");
		sql.append(",CITYID");
		sql.append(",CITY");
		sql.append(",BRANCHID");
		sql.append(",BRANCHDESC");
		sql.append(",BROKER_NAME");
		sql.append(",DME_NAME");
		sql.append(",ASM_NAME");
		sql.append(",RSM_NAME");
		sql.append(",CRDT_MGR_NAME");
		sql.append(",ROID_NAME");
		sql.append(",TLID_NAME");
		sql.append(",BMID_NAME");
		sql.append(",COID_NAME");
		sql.append(",SUPPLIERID");
		sql.append(",DLR_PARTICIPATION_RATE");
		sql.append(",LOCAL_OUTSTATION_FLG");
		sql.append(",CUSTOMERID");
		sql.append(",BUSINESSDATE");
		sql.append(",PROCESSED_FLAG");
		sql.append(",PROCESS_DATE");
		sql.append(",PRODUCT");
		sql.append(",SEGMENTS");
		sql.append(",CRM_STATUS");
		sql.append(",LOGINDATE");
		sql.append(",APPR_REJ_DATE");
		sql.append(",CANCEL_DATE");
		sql.append(",FILE_STATUS");
		sql.append(",FILE_HOLD_REASON");
		sql.append(",QDPDONE");
		sql.append(",ISHOLD");
		sql.append(",BRANCH_INW_DATE");
		sql.append(",BRANCH_HOLD_DATE");
		sql.append(",CPU_INW_DATE");
		sql.append(",CPU_HOLD_DATE");
		sql.append(",SYSTEM_HOLD");
		sql.append(",PSL_FLAG");
		sql.append(",DOC_WAVE_FLAG");
		sql.append(",CUSTOMER_SWIPE");
		sql.append(",CUSTOMER_ACCEPTANCE");
		sql.append(",KYC_DOC_TYPE");
		sql.append(",KYC_DOC_ID");
		sql.append(",BUSINESS_IRR");
		sql.append(",INSPECTORNAME");
		sql.append(",REGNUMBER");
		sql.append(",BUSINESS_YEAR");
		sql.append(",DEALID");
		sql.append(",PRETAXIRR");
		sql.append(",ODM_FLAG");
		sql.append(",DI_DATE");
		sql.append(",LAA_QDP_FLAG");
		sql.append(",PREFERRED_CARD_ACCEPTANCE");
		sql.append(",ORIG_AMTFIN");
		sql.append(",ELC_FLAG");
		sql.append(",ELC_LIMIT");
		sql.append(",QDP_DONE_DATE");
		sql.append(",LAA_DECENTRALIZED_FLAG");
		sql.append(",FCU_FLAG");
		sql.append(",MKTGID");
		sql.append(",DM_MPID");
		sql.append(",SWIPE_CARD_CODE");
		sql.append(",SOURCE_CARD_CODE");
		sql.append(",DII_USER_ID");
		sql.append(",QDP_CHEQUE_ISSUE");
		sql.append(",INTEREST_TYPE");
		sql.append(",DII_DONE_DATE");
		sql.append(",FINISH_DATE");
		sql.append(",BATCH_ID");

		sql.append(") VALUES(");

		sql.append("?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(")");

		jdbcTemplate.update(sql.toString(), rs.getObject("APPLID"), rs.getObject("CRM_DEALID"),
				rs.getObject("CRSCOREDATE"), rs.getObject("CIBILSCORE"), rs.getObject("APPSCORE"),
				rs.getObject("BEHSCORE"), rs.getObject("DEVIATION_TYPE"), rs.getObject("DEVIATION_FLAG"),
				rs.getObject("DEVIATION_CODE"), rs.getObject("DEVIATION_NAME"), rs.getObject("REQ_TENURE"),
				rs.getObject("REQ_LOAN_AMT"), rs.getObject("LOAN_LIMIT"), rs.getObject("APPROVE_LOAN_AMT"),
				rs.getObject("LOAN_PURPOSE"), rs.getObject("LOAN_TYPE"), rs.getObject("CANCELLATIONDATE"),
				rs.getObject("CANCEL_REASON"), rs.getObject("FREQUENCY"), rs.getObject("LOAN_APPROVAL_DATE"),
				rs.getObject("AGREEMENTDATE"), rs.getObject("INTRATE"), rs.getObject("FLAT_RATE"), rs.getObject("IRR"),
				rs.getObject("GROSS_LTV"), rs.getObject("NET_LTV"), rs.getObject("COF"),
				rs.getObject("DEBT_BURDEN_RATIO"), rs.getObject("FOIR_DB"), rs.getObject("SCHEMEID"),
				rs.getObject("SCHEMEDESC"), rs.getObject("SCHEMEGROUPID"), rs.getObject("SCHEME_GROUPG_DESC"),
				rs.getObject("PRODUCT_CATEGORY"), rs.getObject("PROD_TYPE"), rs.getObject("PROMOTIONID"),
				rs.getObject("PROGRAMID"), rs.getObject("SURROGATE_FLAG"), rs.getObject("SOURCING_CHANNEL_TYPE"),
				rs.getObject("SOURCING_CHANNEL_NAME"), rs.getObject("REFERAL_GROUP"), rs.getObject("REFERAL_NAME"),
				rs.getObject("COUNTRYID"), rs.getObject("COUNTRY"), rs.getObject("REGIONID"), rs.getObject("REGION"),
				rs.getObject("STATEID"), rs.getObject("STATE"), rs.getObject("CITYID"), rs.getObject("CITY"),
				rs.getObject("BRANCHID"), rs.getObject("BRANCHDESC"), rs.getObject("BROKER_NAME"),
				rs.getObject("DME_NAME"), rs.getObject("ASM_NAME"), rs.getObject("RSM_NAME"),
				rs.getObject("CRDT_MGR_NAME"), rs.getObject("ROID_NAME"), rs.getObject("TLID_NAME"),
				rs.getObject("BMID_NAME"), rs.getObject("COID_NAME"), rs.getObject("SUPPLIERID"),
				rs.getObject("DLR_PARTICIPATION_RATE"), rs.getObject("LOCAL_OUTSTATION_FLG"),
				rs.getObject("CUSTOMERID"), appDate, rs.getObject("PROCESSED_FLAG"), valueDate, rs.getObject("PRODUCT"),
				rs.getObject("SEGMENTS"), rs.getObject("CRM_STATUS"), rs.getObject("LOGINDATE"),
				rs.getObject("APPR_REJ_DATE"), rs.getObject("CANCEL_DATE"), rs.getObject("FILE_STATUS"),
				rs.getObject("FILE_HOLD_REASON"), rs.getObject("QDPDONE"), rs.getObject("ISHOLD"),
				rs.getObject("BRANCH_INW_DATE"), rs.getObject("BRANCH_HOLD_DATE"), rs.getObject("CPU_INW_DATE"),
				rs.getObject("CPU_HOLD_DATE"), rs.getObject("SYSTEM_HOLD"), rs.getObject("PSL_FLAG"),
				rs.getObject("DOC_WAVE_FLAG"), rs.getObject("CUSTOMER_SWIPE"), rs.getObject("CUSTOMER_ACCEPTANCE"),
				rs.getObject("KYC_DOC_TYPE"), rs.getObject("KYC_DOC_ID"), rs.getObject("BUSINESS_IRR"),
				rs.getObject("INSPECTORNAME"), rs.getObject("REGNUMBER"), rs.getObject("BUSINESS_YEAR"),
				rs.getObject("DEALID"), rs.getObject("PRETAXIRR"), rs.getObject("ODM_FLAG"), rs.getObject("DI_DATE"),
				rs.getObject("LAA_QDP_FLAG"), rs.getObject("PREFERRED_CARD_ACCEPTANCE"), rs.getObject("ORIG_AMTFIN"),
				rs.getObject("ELC_FLAG"), rs.getObject("ELC_LIMIT"), rs.getObject("QDP_DONE_DATE"),
				rs.getObject("LAA_DECENTRALIZED_FLAG"), rs.getObject("FCU_FLAG"), rs.getObject("MKTGID"),
				rs.getObject("DM_MPID"), rs.getObject("SWIPE_CARD_CODE"), rs.getObject("SOURCE_CARD_CODE"),
				rs.getObject("DII_USER_ID"), rs.getObject("QDP_CHEQUE_ISSUE"), rs.getObject("INTEREST_TYPE"),
				rs.getObject("DII_DONE_DATE"), rs.getObject("FINISH_DATE"), batchId);

	}

	public static void saveBounceDetailsMap(ResultSet rs, Date appDate, Date valueDate, long batchId,
			JdbcTemplate jdbcTemplate) throws Exception {

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO DM_BOUNCE_DETAILS (");

		sql.append("APPLID");
		sql.append(",CHEQUEID");
		sql.append(",AGREEMENTNO");
		sql.append(",BOUNCE_DATE");
		sql.append(",BOUNCE_REASON");
		sql.append(",BOUNCE_AMT");
		sql.append(",BUSINESSDATE");
		sql.append(",PROCESSED_FLAG");
		sql.append(",PROCESS_DATE");
		sql.append(",SEGMENTS");
		sql.append(",BUSINESS_YEAR");
		sql.append(",CHEQUEDATE");
		sql.append(",BATCH_ID");
		sql.append(") VALUES(");

		sql.append("?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(")");

		jdbcTemplate.update(sql.toString(), rs.getObject("APPLID"), rs.getObject("CHEQUEID"),
				rs.getObject("AGREEMENTNO"), rs.getObject("BOUNCE_DATE"), rs.getObject("BOUNCE_REASON"),
				rs.getObject("BOUNCE_AMT"), appDate, rs.getObject("PROCESSED_FLAG"), valueDate,
				rs.getObject("SEGMENTS"), rs.getObject("BUSINESS_YEAR"), rs.getObject("CHEQUEDATE"), batchId);

	}

	public static void saveCoApplicantDetails(ResultSet rs, Date appDate, Date valueDate, long batchId,
			JdbcTemplate jdbcTemplate) throws Exception {

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO DM_COAPPLICANT_DETAILS (");
		sql.append("CUSTOMERID");
		sql.append(",APPLICANT_TYPE");
		sql.append(",PAN_NO");
		sql.append(",CUST_RELATION");
		sql.append(",CUST_TYPE");
		sql.append(",AGE");
		sql.append(",DOB");
		sql.append(",FNAME");
		sql.append(",MNAME");
		sql.append(",LNAME");
		sql.append(",GENDER");
		sql.append(",MARITAL_STATUS");
		sql.append(",NO_OF_DEPENDENT");
		sql.append(",YRS_CURRENTJOB");
		sql.append(",PREVIOUS_JOB_YEAR");
		sql.append(",QUALIFICATION");
		sql.append(",EMPLOYER_DESC");
		sql.append(",COMPANY_TYPE");
		sql.append(",INDUSTRYID");
		sql.append(",BUSINESS_NATURE");
		sql.append(",OCCUPATION_CODE");
		sql.append(",GUARDIAN");
		sql.append(",PROCESS_FLAG");
		sql.append(",PROCESS_DATE");
		sql.append(",EMP_TYPE");
		sql.append(",BUSINESSDATE");
		sql.append(",INCOME");
		sql.append(",APPLID");
		sql.append(",BATCH_ID");
		sql.append(") VALUES(");

		sql.append("?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(")");

		jdbcTemplate.update(sql.toString(), rs.getObject("CUSTOMERID"), rs.getObject("APPLICANT_TYPE"),
				rs.getObject("PAN_NO"), rs.getObject("CUST_RELATION"), rs.getObject("CUST_TYPE"), rs.getObject("AGE"),
				rs.getObject("DOB"), rs.getObject("FNAME"), rs.getObject("MNAME"), rs.getObject("LNAME"),
				rs.getObject("GENDER"), rs.getObject("MARITAL_STATUS"), rs.getObject("NO_OF_DEPENDENT"),
				rs.getObject("YRS_CURRENTJOB"), rs.getObject("PREVIOUS_JOB_YEAR"), rs.getObject("QUALIFICATION"),
				rs.getObject("EMPLOYER_DESC"), rs.getObject("COMPANY_TYPE"), rs.getObject("INDUSTRYID"),
				rs.getObject("BUSINESS_NATURE"), rs.getObject("OCCUPATION_CODE"), rs.getObject("GUARDIAN"),
				rs.getObject("PROCESS_FLAG"), valueDate, rs.getObject("EMP_TYPE"), appDate, rs.getObject("INCOME"),
				rs.getObject("APPLID"), batchId);
	}

	public static void saveDisbDetails(ResultSet rs, Date appDate, Date valueDate, long batchId,
			JdbcTemplate jdbcTemplate) throws Exception {

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO DM_DISB_DETAILS_DAILY (");
		sql.append("AGREEMENTDATE");
		sql.append(",APPLID");
		sql.append(",AGREEMENTNO");
		sql.append(",DISBURSEMENTNO");
		sql.append(",DISBURSEMENTDATE");
		sql.append(",PARENT_AGREEMENTNO");
		sql.append(",AMTFIN");
		sql.append(",NET_AMTFIN");
		sql.append(",DISBURSEDAMT");
		sql.append(",DISB_STATUS");
		sql.append(",FIRST_DUE_DATE");
		sql.append(",GROSS_TENURE");
		sql.append(",NET_TENURE");
		sql.append(",MATURITYDATE");
		sql.append(",EXPIRYDATE");
		sql.append(",NO_OF_ADV_INSTL");
		sql.append(",ADV_EMI_AMT");
		sql.append(",EMI");
		sql.append(",REPAYMENT_MODE");
		sql.append(",PRODUCTFLAG");
		sql.append(",PROMOTIONID");
		sql.append(",ICICI_LOMBARD");
		sql.append(",BAGIC");
		sql.append(",BALIC_CHARGES");
		sql.append(",BUSINESSDATE");
		sql.append(",PROCESSED_FLAG");
		sql.append(",PROCESS_DATE");
		sql.append(",SEGMENTS");
		sql.append(",FEE");
		sql.append(",DEALER_SUBV");
		sql.append(",MANU_SUBV_DED");
		sql.append(",MANU_SUBV_NDED");
		sql.append(",PREEMI");
		sql.append(",EXISTING_LANNO");
		sql.append(",MORTGAGE_FEE");
		sql.append(",COMMITMENT_FEE");
		sql.append(",PROCESSING_FEE");
		sql.append(",PRE_EMI_RECEIVABLE");
		sql.append(",INSURANCE");
		sql.append(",PAYMENTMODE");
		sql.append(",FREQ");
		sql.append(",CHEQUENUM");
		sql.append(",CUST_ACCT_NO");
		sql.append(",BANKNAME");
		sql.append(",MICRCODE");
		sql.append(",BUSINESS_YEAR");
		sql.append(",EMI_CHARGE");
		sql.append(",PDC_CHARGE");
		sql.append(",IRR_PER");
		sql.append(",FEE_WL");
		sql.append(",ELC_CHARGE");
		sql.append(",CREDIT_VIDYA_FEES");
		sql.append(",BATCH_ID");

		sql.append(") VALUES(");

		sql.append("?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(")");

		jdbcTemplate.update(sql.toString(), rs.getObject("AGREEMENTDATE"), rs.getObject("APPLID"),
				rs.getObject("AGREEMENTNO"), rs.getObject("DISBURSEMENTNO"), rs.getObject("DISBURSEMENTDATE"),
				rs.getObject("PARENT_AGREEMENTNO"), rs.getObject("AMTFIN"), rs.getObject("NET_AMTFIN"),
				rs.getObject("DISBURSEDAMT"), rs.getObject("DISB_STATUS"), rs.getObject("FIRST_DUE_DATE"),
				rs.getObject("GROSS_TENURE"), rs.getObject("NET_TENURE"), rs.getObject("MATURITYDATE"),
				rs.getObject("EXPIRYDATE"), rs.getObject("NO_OF_ADV_INSTL"), rs.getObject("ADV_EMI_AMT"),
				rs.getObject("EMI"), rs.getObject("REPAYMENT_MODE"), rs.getObject("PRODUCTFLAG"),
				rs.getObject("PROMOTIONID"), rs.getObject("ICICI_LOMBARD"), rs.getObject("BAGIC"),
				rs.getObject("BALIC_CHARGES"), appDate, rs.getObject("PROCESSED_FLAG"), valueDate,
				rs.getObject("SEGMENTS"), rs.getObject("FEE"), rs.getObject("DEALER_SUBV"),
				rs.getObject("MANU_SUBV_DED"), rs.getObject("MANU_SUBV_NDED"), rs.getObject("PREEMI"),
				rs.getObject("EXISTING_LANNO"), rs.getObject("MORTGAGE_FEE"), rs.getObject("COMMITMENT_FEE"),
				rs.getObject("PROCESSING_FEE"), rs.getObject("PRE_EMI_RECEIVABLE"), rs.getObject("INSURANCE"),
				rs.getObject("PAYMENTMODE"), rs.getObject("FREQ"), rs.getObject("CHEQUENUM"),
				rs.getObject("CUST_ACCT_NO"), rs.getObject("BANKNAME"), rs.getObject("MICRCODE"),
				rs.getObject("BUSINESS_YEAR"), rs.getObject("EMI_CHARGE"), rs.getObject("PDC_CHARGE"),
				rs.getObject("IRR_PER"), rs.getObject("FEE_WL"), rs.getObject("ELC_CHARGE"),
				rs.getObject("CREDIT_VIDYA_FEES"), batchId);
	}

	public static void saveForeClosureChargesMap(ResultSet rs, Date appDate, long batchId, JdbcTemplate jdbcTemplate)
			throws Exception {

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO FORECLOSURECHARGES (");

		sql.append("AGREEMENTID");
		sql.append(",BAL_PRINCIPAL");
		sql.append(",RESEDUL_VALUE");
		sql.append(",INSTALLMENTS");
		sql.append(",ADVICES");
		sql.append(",PENALTY");
		sql.append(",INTREST_ON_TERMINATION");
		sql.append(",FLOATINTREST_ON_TERMINATION");
		sql.append(",OVER_DUES");
		sql.append(",CURRENT_OVERDUES");
		sql.append(",CURRENT_WAVEOFFAMT");
		sql.append(",OVER_DISTANCE_CHARGES");
		sql.append(",INTREST_ON_TERMINATION_PER_DAY");
		sql.append(",INTREST_ACCRUALS");
		sql.append(",EXCESS_AMOUNT");
		sql.append(",EXCESS_REFUND");
		sql.append(",ADVICE");
		sql.append(",REBATE");
		sql.append(",ADVINSTL");
		sql.append(",EXCESSPRINPMNT");
		sql.append(",SDAMT");
		sql.append(",SDINT");
		sql.append(",EXCESS_INTREST_RATE");
		sql.append(",VAT_ON_FORECLOSURE");
		sql.append(",UNDER_DISTANCE_CHARGES");
		sql.append(",NET_PAYBALE");
		sql.append(",WAIVEOFFAMOUNT");
		sql.append(",ACTIVITY");
		sql.append(",AUTHORIZEDON");
		sql.append(",COMMITMENT_FEE");
		sql.append(",ORIGINATION_FEE");
		sql.append(",PRE_EMI");
		sql.append(",BUSINESS_DATE");
		sql.append(",CHEQUEID");
		sql.append(",STATUS");
		sql.append(",INTEREST_WAIVE_OFF");
		sql.append(",BALANCE_PRIN_WAIVE_OFF");
		sql.append(",INSTALLMET_INT_WAIVE_OFF");
		sql.append(",WOFF_CURRMONTH_INT");
		sql.append(",WOFF_OVERDUE_CHARGE");
		sql.append(",WOFF_CHQBOUNCE_CHARGES");
		sql.append(",WOFF_OTHERS");
		sql.append(",INSTALLMENT_PRIN_WAIVE_OFF");
		sql.append(",WOFF_PARKING_CHARGES");
		sql.append(",TOT_OTHER_REPO_CHARGES");
		sql.append(",WOFF_OTHER_REPO_CHARGES");
		sql.append(",TOT_REPOSESSION_CHARGES");
		sql.append(",WOFF_REPOSESSION_CHARGES");
		sql.append(",TOT_PARKING_CHARGES");
		sql.append(",BATCH_ID");

		sql.append(") VALUES(");
		sql.append("?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(")");

		jdbcTemplate.update(sql.toString(), rs.getObject("AGREEMENTID"), rs.getObject("BAL_PRINCIPAL"),
				rs.getObject("RESEDUL_VALUE"), rs.getObject("INSTALLMENTS"), rs.getObject("ADVICES"),
				rs.getObject("PENALTY"), rs.getObject("INTREST_ON_TERMINATION"),
				rs.getObject("FLOATINTREST_ON_TERMINATION"), rs.getObject("OVER_DUES"),
				rs.getObject("CURRENT_OVERDUES"), rs.getObject("CURRENT_WAVEOFFAMT"),
				rs.getObject("OVER_DISTANCE_CHARGES"), rs.getObject("INTREST_ON_TERMINATION_PER_DAY"),
				rs.getObject("INTREST_ACCRUALS"), rs.getObject("EXCESS_AMOUNT"), rs.getObject("EXCESS_REFUND"),
				rs.getObject("ADVICE"), rs.getObject("REBATE"), rs.getObject("ADVINSTL"),
				rs.getObject("EXCESSPRINPMNT"), rs.getObject("SDAMT"), rs.getObject("SDINT"),
				rs.getObject("EXCESS_INTREST_RATE"), rs.getObject("VAT_ON_FORECLOSURE"),
				rs.getObject("UNDER_DISTANCE_CHARGES"), rs.getObject("NET_PAYBALE"), rs.getObject("WAIVEOFFAMOUNT"),
				rs.getObject("ACTIVITY"), rs.getObject("AUTHORIZEDON"), rs.getObject("COMMITMENT_FEE"),
				rs.getObject("ORIGINATION_FEE"), rs.getObject("PRE_EMI"), appDate, rs.getObject("CHEQUEID"),
				rs.getObject("STATUS"), rs.getObject("INTEREST_WAIVE_OFF"), rs.getObject("BALANCE_PRIN_WAIVE_OFF"),
				rs.getObject("INSTALLMET_INT_WAIVE_OFF"), rs.getObject("WOFF_CURRMONTH_INT"),
				rs.getObject("WOFF_OVERDUE_CHARGE"), rs.getObject("WOFF_CHQBOUNCE_CHARGES"),
				rs.getObject("WOFF_OTHERS"), rs.getObject("INSTALLMENT_PRIN_WAIVE_OFF"),
				rs.getObject("WOFF_PARKING_CHARGES"), rs.getObject("TOT_OTHER_REPO_CHARGES"),
				rs.getObject("WOFF_OTHER_REPO_CHARGES"), rs.getObject("TOT_REPOSESSION_CHARGES"),
				rs.getObject("WOFF_REPOSESSION_CHARGES"), rs.getObject("TOT_PARKING_CHARGES"), batchId);

	}

	public static void saveHTSUnadjustedMap(ResultSet rs, Date appDate, long batchId, JdbcTemplate jdbcTemplate)
			throws Exception {

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO DM_HTS_UNADJUSTED_AMT (");
		sql.append("APPLID");
		sql.append(",UNADJUSTED_AMOUNT");
		sql.append(",BUSINESSDATE");
		sql.append(",BATCH_ID");

		sql.append(") VALUES(");

		sql.append("?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(")");

		jdbcTemplate.update(sql.toString(), rs.getObject("APPLID"), rs.getObject("UNADJUSTED_AMOUNT"), appDate,
				batchId);

	}

	public static void saveInsuranceDetailsMap(ResultSet rs, Date appDate, Date valueDate, long batchId,
			JdbcTemplate jdbcTemplate) throws Exception {

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO DM_INSURANCE_DETAILS (");

		sql.append("APPLID");
		sql.append(",AGREEMENTNO");
		sql.append(",ASSETID");
		sql.append(",INSURANCE_TYPE");
		sql.append(",INSUR_PREMIUM");
		sql.append(",INSURANCE_RENEWAL_DATE");
		sql.append(",BUSINESSDATE");
		sql.append(",PROCESSED_FLAG");
		sql.append(",PROCESS_DATE");
		sql.append(",SEGMENTS");
		sql.append(",BUSINESS_YEAR");
		sql.append(",POLICY_TENURE");
		sql.append(",SUM_ASSURED");
		sql.append(",NOMINEE_NAME");
		sql.append(",GOOD_HEALTH");
		sql.append(",CRITICAL_ILLNESS_FLAG");
		sql.append(",RELATIONSHIP");
		sql.append(",NOMINEE_ADDRESS");
		sql.append(",NOMINEE_RELATION");
		sql.append(",NOMINEE_DOB");
		sql.append(",NOMINEE_AGE");
		sql.append(",NOMINEE_CONTACTNO");
		sql.append(",NOMINEE_NAME2");
		sql.append(",NOMINEE_ADDRESS2");
		sql.append(",NOMINEE_RELATION2");
		sql.append(",NOMINEE_DOB2");
		sql.append(",NOMINEE_AGE2");
		sql.append(",NOMINEE_CONTACTNO2");
		sql.append(",DFGH");
		sql.append(",BATCH_ID");
		sql.append(") VALUES(");

		sql.append("?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(")");

		jdbcTemplate.update(sql.toString(), rs.getObject("APPLID"), rs.getObject("AGREEMENTNO"),
				rs.getObject("ASSETID"), rs.getObject("INSURANCE_TYPE"), rs.getObject("INSUR_PREMIUM"),
				rs.getObject("INSURANCE_RENEWAL_DATE"), appDate, rs.getObject("PROCESSED_FLAG"), valueDate,
				rs.getObject("SEGMENTS"), rs.getObject("BUSINESS_YEAR"), rs.getObject("POLICY_TENURE"),
				rs.getObject("SUM_ASSURED"), rs.getObject("NOMINEE_NAME"), rs.getObject("GOOD_HEALTH"),
				rs.getObject("CRITICAL_ILLNESS_FLAG"), rs.getObject("RELATIONSHIP"), rs.getObject("NOMINEE_ADDRESS"),
				rs.getObject("NOMINEE_RELATION"), rs.getObject("NOMINEE_DOB"), rs.getObject("NOMINEE_AGE"),
				rs.getObject("NOMINEE_CONTACTNO"), rs.getObject("NOMINEE_NAME2"), rs.getObject("NOMINEE_ADDRESS2"),
				rs.getObject("NOMINEE_RELATION2"), rs.getObject("NOMINEE_DOB2"), rs.getObject("NOMINEE_AGE2"),
				rs.getObject("NOMINEE_CONTACTNO2"), rs.getObject("DFGH"), batchId);

	}

	public static void saveIVRDetails(ResultSet rs, Date appDate, Date valueDate, long batchId,
			JdbcTemplate jdbcTemplate) throws Exception {

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO DM_IVR_GATEWAY_FLEXI (");
		sql.append("AGREEMENTNO");
		sql.append(",DROP_LINE_LIMIT");
		sql.append(",AMOUNT_DRAWN_LIMIT");
		sql.append(",UTLIZED_BALANCE_LIMIT");
		sql.append(",PRINCIPLE_AMOUNT_PAID");
		sql.append(",BALANCE_PRINCIPAL_OUTSTANDING");
		sql.append(",PROCESS_DATE");
		sql.append(",BUSINESSDATE");
		sql.append(",BATCH_ID");
		sql.append(") VALUES(");
		sql.append("?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(")");

		jdbcTemplate.update(sql.toString(), rs.getObject("AGREEMENTNO"), rs.getObject("DROP_LINE_LIMIT"),
				rs.getObject("AMOUNT_DRAWN_LIMIT"), rs.getObject("UTLIZED_BALANCE_LIMIT"),
				rs.getObject("PRINCIPLE_AMOUNT_PAID"), rs.getObject("BALANCE_PRINCIPAL_OUTSTANDING"), valueDate,
				appDate, batchId);

	}

	public static void saveLeaDocDtl(ResultSet rs, Date appDate, Date valueDate, long batchId,
			JdbcTemplate jdbcTemplate) throws Exception {

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO DM_LEA_DOC_DTL (");
		sql.append("AGREEMENTID");
		sql.append(",APPLICABLE");
		sql.append(",APPLID");
		sql.append(",APPROVED_BY");
		sql.append(",AUTHDATE");
		sql.append(",AUTHID");
		sql.append(",AWB_NO");
		sql.append(",BRANCHSTATUS");
		sql.append(",CONSTITUTION");
		sql.append(",COURIER_NAME");
		sql.append(",CRITICAL");
		sql.append(",CUSTOMER_TYPE");
		sql.append(",CUSTOMERID");
		sql.append(",DATE_BRANCH_STATUS");
		sql.append(",DATELASTUPDT");
		sql.append(",DOC_CLASSFN");
		sql.append(",DOC_RETRIEVAL_STATUS");
		sql.append(",DOCID");
		sql.append(",DOCTYPE");
		sql.append(",FILE_BARCODE");
		sql.append(",MAKERDATE");
		sql.append(",MAKERID");
		sql.append(",MC_STATUS");
		sql.append(",NEW_FLAG");
		sql.append(",ORIGINALS");
		sql.append(",PACKET_TRACKER_NO");
		sql.append(",PROPERTY_ADDRESS");
		sql.append(",PROPERTY_CODE");
		sql.append(",REASON");
		sql.append(",RECEIVED");
		sql.append(",RECEIVEDDATE");
		sql.append(",REJECTION_REASONS");
		sql.append(",RELEASED_DATE");
		sql.append(",RELEASED_REMARKS");
		sql.append(",REMARKS");
		sql.append(",STAGE");
		sql.append(",TARGETDATE");
		sql.append(",TITLE_DOCUMENT");
		sql.append(",TXNDOCID");
		sql.append(",VALIDTILLDATE");
		sql.append(",VAP_LOAN_FLAG");
		sql.append(",BUSINESSDATE");
		sql.append(",PROCESSFLAG");
		sql.append(",PROCESSDATE");
		sql.append(",SEGMENT");
		sql.append(",REVISED_TARGET_DATE");
		sql.append(",BATCH_ID");
		sql.append(") VALUES(");

		sql.append("?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(")");

		jdbcTemplate.update(sql.toString(), rs.getObject("AGREEMENTID"), rs.getObject("APPLICABLE"),
				rs.getObject("APPLID"), rs.getObject("APPROVED_BY"), rs.getObject("AUTHDATE"), rs.getObject("AUTHID"),
				rs.getObject("AWB_NO"), rs.getObject("BRANCHSTATUS"), rs.getObject("CONSTITUTION"),
				rs.getObject("COURIER_NAME"), rs.getObject("CRITICAL"), rs.getObject("CUSTOMER_TYPE"),
				rs.getObject("CUSTOMERID"), rs.getObject("DATE_BRANCH_STATUS"), rs.getObject("DATELASTUPDT"),
				rs.getObject("DOC_CLASSFN"), rs.getObject("DOC_RETRIEVAL_STATUS"), rs.getObject("DOCID"),
				rs.getObject("DOCTYPE"), rs.getObject("FILE_BARCODE"), rs.getObject("MAKERDATE"),
				rs.getObject("MAKERID"), rs.getObject("MC_STATUS"), rs.getObject("NEW_FLAG"), rs.getObject("ORIGINALS"),
				rs.getObject("PACKET_TRACKER_NO"), rs.getObject("PROPERTY_ADDRESS"), rs.getObject("PROPERTY_CODE"),
				rs.getObject("REASON"), rs.getObject("RECEIVED"), rs.getObject("RECEIVEDDATE"),
				rs.getObject("REJECTION_REASONS"), rs.getObject("RELEASED_DATE"), rs.getObject("RELEASED_REMARKS"),
				rs.getObject("REMARKS"), rs.getObject("STAGE"), rs.getObject("TARGETDATE"),
				rs.getObject("TITLE_DOCUMENT"), rs.getObject("TXNDOCID"), rs.getObject("VALIDTILLDATE"),
				rs.getObject("VAP_LOAN_FLAG"), appDate, rs.getObject("PROCESSFLAG"), valueDate, rs.getObject("SEGMENT"),
				rs.getObject("REVISED_TARGET_DATE"), batchId);

	}

	public static void saveLoanDetail(ResultSet rs, Date appDate, Date valueDate, long batchId,
			JdbcTemplate jdbcTemplate) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO DM_LOAN_DETAILS_DAILY (");

		sql.append("APPLID");
		sql.append(",AGREEMENTNO");
		sql.append(",CUSTOMERID");
		sql.append(",CUSTOMER_YIELD");
		sql.append(",STATUS");
		sql.append(",NPA_STAGE");
		sql.append(",LMS_BUCKET");
		sql.append(",COLL_BUCKET");
		sql.append(",INSURANCE_APPLIED_FLG");
		sql.append(",BUSINESSDATE");
		sql.append(",PROCESSED_FLAG");
		sql.append(",PROCESS_DATE");
		sql.append(",SEGMENTS");
		sql.append(",CLOSUREDATE");
		sql.append(",TOPUP_AMT");
		sql.append(",BUSINESS_YEAR");
		sql.append(",PDCID");
		sql.append(",PDCFLAG");
		sql.append(",OD_FLAG");
		sql.append(",MARGIN");
		sql.append(",SPECIALMARGIN");
		sql.append(",FIXEDTENOR");
		sql.append(",CEEFFECTIVEDATE");
		sql.append(",EFF_RATE");
		sql.append(",PLRRATE");
		sql.append(",PARTY_CODE");
		sql.append(",PARTY_NAME");
		sql.append(",ZONE");
		sql.append(",COLLECTION_CENTRE");
		sql.append(",VIRTUAL_ACCOUNT_NUMBER");
		sql.append(",INSTALLMENT_TYPE");
		sql.append(",COMPANYTYPE");
		sql.append(",FIANANCE_CHARGES");
		sql.append(",FILENO");
		sql.append(",NO_OF_PDCS");
		sql.append(",LIFEINSURANCE");
		sql.append(",SHORTRECEIVED");
		sql.append(",IN_FAVOUR_OFF");
		sql.append(",MKTGID");
		sql.append(",PRE_EMI_INT_500071");
		sql.append(",LOAN_PURPOSE_DTL");
		sql.append(",LOAN_PURPOSE_DESC");
		sql.append(",LOGIN_FEES");
		sql.append(",VC_REFERRAL_CD");
		sql.append(",VC_REFERRAL_NAME");
		sql.append(",PROC_FEES2");
		sql.append(",INSTRUMENT_TYPE");
		sql.append(",LAN_BARCODE");
		sql.append(",INTSTART_DATE_REGULAR");
		sql.append(",BPI_RECEIVABLE");
		sql.append(",BPI_PAYABLE");
		sql.append(",OPEN_FACILITY_FLAG");
		sql.append(",BATCH_ID");

		sql.append(") VALUES(");

		sql.append("?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(")");

		jdbcTemplate.update(sql.toString(), rs.getObject("APPLID"), rs.getObject("AGREEMENTNO"),
				rs.getObject("CUSTOMERID"), rs.getObject("CUSTOMER_YIELD"), rs.getObject("STATUS"),
				rs.getObject("NPA_STAGE"), rs.getObject("LMS_BUCKET"), rs.getObject("COLL_BUCKET"),
				rs.getObject("INSURANCE_APPLIED_FLG"), appDate, rs.getObject("PROCESSED_FLAG"), valueDate,
				rs.getObject("SEGMENTS"), rs.getDate("CLOSUREDATE"), rs.getObject("TOPUP_AMT"),
				rs.getObject("BUSINESS_YEAR"), rs.getObject("PDCID"), rs.getObject("PCFLAG"), rs.getObject("OD_FLAG"),
				rs.getObject("MARGIN"), rs.getObject("SPECIALMARGIN"), rs.getObject("FIXEDTENOR"),
				rs.getObject("CEEFFECTIVEDATE"), rs.getObject("EFF_RATE"), rs.getObject("PLRRATE"),
				rs.getObject("PARTY_CODE"), rs.getObject("PARTY_NAME"), rs.getObject("ZONE"),
				rs.getObject("COLLECTION_CENTRE"), rs.getObject("VIRTUAL_ACCOUNT_NUMBER"),
				rs.getObject("INSTALLMENT_TYPE"), rs.getObject("COMPANYTYPE"), rs.getObject("FIANANCE_CHARGES"),
				rs.getObject("FILENO"), rs.getObject("NO_OF_PDCS"), rs.getObject("LIFEINSURANCE"),
				rs.getObject("SHORTRECEIVED"), rs.getObject("IN_FAVOUR_OFF"), rs.getObject("MKTGID"),
				rs.getObject("PRE_EMI_INT_500071"), rs.getObject("LOAN_PURPOSE_DTL"), rs.getObject("LOAN_PURPOSE_DESC"),
				rs.getObject("LOGIN_FEES"), rs.getObject("VC_REFERRAL_CD"), rs.getObject("VC_REFERRAL_NAME"),
				rs.getObject("PROC_FEES2"), rs.getObject("INSTRUMENT_TYPE"), rs.getObject("LAN_BARCODE"),
				rs.getObject("INTSTART_DATE_REGULAR"), rs.getObject("BPI_RECEIVABLE"), rs.getObject("BPI_PAYABLE"),
				rs.getObject("OPEN_FACILITY_FLAG"), batchId);

	}

	public static void saveLoanVoucherDetails(ResultSet rs, Date appDate, Date valueDate, long batchId,
			JdbcTemplate jdbcTemplate) throws Exception {

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO DM_LOAN_VOUCHER_DETAILS (");

		sql.append("MODULEID");
		sql.append(",STAGEID");
		sql.append(",LEA_VOUCHERID");
		sql.append(",FA_VOUCHERID");
		sql.append(",VOUCHERTYPE");
		sql.append(",VOUCHERDATE");
		sql.append(",VALUEDATE");
		sql.append(",BRANCHID");
		sql.append(",BRANCH_CODE");
		sql.append(",BRANCHDESC");
		sql.append(",BUSINESS_AREA");
		sql.append(",PROFIT_CENTRE");
		sql.append(",PRODUCT_FLAG");
		sql.append(",SCHEMEID");
		sql.append(",SCHEMEDESC");
		sql.append(",ASSIGNMENT");
		sql.append(",AGREEMENTID");
		sql.append(",AGREEMENTNO");
		sql.append(",AGREEMENTDATE");
		sql.append(",DISBURSALDATE");
		sql.append(",LOAN_STATUS");
		sql.append(",NPA_STAGEID");
		sql.append(",FINNONE_GLID");
		sql.append(",GROUPGLDESC");
		sql.append(",SAPGL_CODE");
		sql.append(",COST_CENTRE");
		sql.append(",DRAMT");
		sql.append(",CRAMT");
		sql.append(",DRCR_FLAG");
		sql.append(",DRCR_AMT");
		sql.append(",NARRATION");
		sql.append(",CHEQUEID");
		sql.append(",BUSINESSDATE");
		sql.append(",PROCESSDATE");
		sql.append(",SEGMENT");
		sql.append(",PROCESSED_FLAG");
		sql.append(",BATCH_ID");
		sql.append(") VALUES(");

		sql.append("?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(")");

		jdbcTemplate.update(sql.toString(), rs.getObject("MODULEID"), rs.getObject("STAGEID"),
				rs.getObject("LEA_VOUCHERID"), rs.getObject("FA_VOUCHERID"), rs.getObject("VOUCHERTYPE"),
				rs.getObject("VOUCHERDATE"), rs.getObject("VALUEDATE"), rs.getObject("BRANCHID"),
				rs.getObject("BRANCH_CODE"), rs.getObject("BRANCHDESC"), rs.getObject("BUSINESS_AREA"),
				rs.getObject("PROFIT_CENTRE"), rs.getObject("PRODUCT_FLAG"), rs.getObject("SCHEMEID"),
				rs.getObject("SCHEMEDESC"), rs.getObject("ASSIGNMENT"), rs.getObject("AGREEMENTID"),
				rs.getObject("AGREEMENTNO"), rs.getObject("AGREEMENTDATE"), rs.getObject("DISBURSALDATE"),
				rs.getObject("LOAN_STATUS"), rs.getObject("NPA_STAGEID"), rs.getObject("FINNONE_GLID"),
				rs.getObject("GROUPGLDESC"), rs.getObject("SAPGL_CODE"), rs.getObject("COST_CENTRE"),
				rs.getObject("DRAMT"), rs.getObject("CRAMT"), rs.getObject("DRCR_FLAG"), rs.getObject("DRCR_AMT"),
				rs.getObject("NARRATION"), rs.getObject("CHEQUEID"), appDate, valueDate, rs.getObject("SEGMENT"),
				rs.getObject("PROCESSED_FLAG"), batchId);

	}

	public static void saveLoanWiseChargeDetail(ResultSet rs, Date appDate, Date valueDate, long batchId,
			JdbcTemplate jdbcTemplate) throws Exception {

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO DM_LOANWISE_CHARGE_DETAILS (");

		sql.append("AGREEMENTID");
		sql.append(",TXNADVICEID");
		sql.append(",CHARGEID");
		sql.append(",CHARGECODEID");
		sql.append(",CHARGEDESC");
		sql.append(",CHARGEAMT");
		sql.append(",STATUS");
		sql.append(",AMTINPROCESS");
		sql.append(",TXNADJUSTEDAMT");
		sql.append(",ADVICEAMT");
		sql.append(",ADVICEDATE");
		sql.append(",BUSINESSDATE");
		sql.append(",PROCESSDATE");
		sql.append(",PROCESSED_FLAG");
		sql.append(",SEGMENT");
		sql.append(",BATCH_ID");

		sql.append(") VALUES(");

		sql.append("?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(")");

		jdbcTemplate.update(sql.toString(), rs.getObject("AGREEMENTID"), rs.getObject("TXNADVICEID"),
				rs.getObject("CHARGEID"), rs.getObject("CHARGECODEID"), rs.getObject("CHARGEDESC"),
				rs.getObject("CHARGEAMT"), rs.getObject("STATUS"), rs.getObject("AMTINPROCESS"),
				rs.getObject("TXNADJUSTEDAMT"), rs.getObject("ADVICEAMT"), rs.getObject("ADVICEDATE"), appDate,
				rs.getObject("PROCESSDATE"), rs.getObject("PROCESSED_FLAG"), rs.getObject("SEGMENT"), batchId);

	}

	public static void saveLoanWiseRepayScheduleDetailMap(ResultSet rs, Date appDate, long batchId,
			JdbcTemplate jdbcTemplate) throws Exception {

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO DM_LOANWISE_REPAYSCHD_DTLS (");
		sql.append("AGREEMENTNO");
		sql.append(",AGREEMENTID");
		sql.append(",PROPINSTLID");
		sql.append(",EMI_NO");
		sql.append(",DUEDATE");
		sql.append(",OPENING_PRINCIPAL");
		sql.append(",INSTALMENT_AMT");
		sql.append(",PRINCIPAL_AMT");
		sql.append(",INTEREST_AMT");
		sql.append(",CLOSING_PRINCIPAL");
		sql.append(",INSTAL_TYPE");
		sql.append(",TOTAL_AMOUNT_DUE");
		sql.append(",DROPLINE_LIMIT");
		sql.append(",ACT_AVAILABLE_LIMIT");
		sql.append(",ACT_UTILISATION_LIMIT");
		sql.append(",EMI_HOLIDAY");
		sql.append(",BATCH_ID");
		sql.append(",BUSINESSDATE");
		sql.append(") VALUES(");

		sql.append("?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(")");

		jdbcTemplate.update(sql.toString(), rs.getObject("AGREEMENTNO"), rs.getObject("AGREEMENTID"),
				rs.getObject("PROPINSTLID"), rs.getObject("EMI_NO"), rs.getObject("DUEDATE"),
				rs.getObject("OPENING_PRINCIPAL"), rs.getObject("INSTALMENT_AMT"), rs.getObject("PRINCIPAL_AMT"),
				rs.getObject("INTEREST_AMT"), rs.getObject("CLOSING_PRINCIPAL"), rs.getObject("INSTAL_TYPE"),
				rs.getObject("TOTAL_AMOUNT_DUE"), rs.getObject("DROPLINE_LIMIT"), rs.getObject("ACT_AVAILABLE_LIMIT"),
				rs.getObject("ACT_UTILISATION_LIMIT"), rs.getObject("EMI_HOLIDAY"), batchId, appDate);

	}

	public static void saveNoceligibleLoans(ResultSet rs, Date appDate, Date valueDate, long batchId,
			JdbcTemplate jdbcTemplate) throws Exception {

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO DM_NOC_ELIGIBLE_LOANS (");
		sql.append("AGREEMENTNO");
		sql.append(",LOANACCTNUM");
		sql.append(",PRODUCTFLAG");
		sql.append(",EMAIL");
		sql.append(",BUSINESSDATE");
		sql.append(",PROCESS_DATE");
		sql.append(",PROCESSED_FLAG");
		sql.append(",BATCH_ID");
		sql.append(") VALUES(");
		sql.append("?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(")");

		jdbcTemplate.update(sql.toString(), rs.getObject("AGREEMENTNO"), rs.getObject("LOANACCTNUM"),
				rs.getObject("PRODUCTFLAG"), rs.getObject("EMAIL"), appDate, valueDate, rs.getObject("PROCESSED_FLAG"),
				batchId);

	}

	public static void saveOpenEcsDetails(ResultSet rs, Date appDate, Date valueDate, long batchId,
			JdbcTemplate jdbcTemplate) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO DM_OPENECS_DETAILS (");
		sql.append("CUSTOMERID");
		sql.append(", ECS_ID");
		sql.append(", BANK_ID");
		sql.append(", BANK_NAME");
		sql.append(", BANKBRANCHID");
		sql.append(", BANKID");
		sql.append(", BANK_BRANCH_NAME");
		sql.append(", CITY");
		sql.append(", ACCTYPE");
		sql.append(", ACCNO");
		sql.append(", MAXLIMIT");
		sql.append(", BALLIMIT");
		sql.append(", UTIL_LIMIT");
		sql.append(", VALID_LIMIT");
		sql.append(", REPAY_MODE");
		sql.append(", MICRCODE");
		sql.append(", ACTIVE_FLAG");
		sql.append(", CITYID");
		sql.append(", SEGMENTS");
		sql.append(", BUSINESSDATE");
		sql.append(", PROCESS_DATE");
		sql.append(", BATCH_ID");
		sql.append(") VALUES(");
		sql.append("?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(")");

		jdbcTemplate.update(sql.toString(), rs.getObject("CUSTOMERID"), rs.getObject("ECS_ID"), rs.getObject("BANK_ID"),
				rs.getObject("BANK_NAME"), rs.getObject("BANKBRANCHID"), rs.getObject("BANKID"),
				rs.getObject("BANK_BRANCH_NAME"), rs.getObject("CITY"), rs.getObject("ACCTYPE"), rs.getObject("ACCNO"),
				rs.getObject("MAXLIMIT"), rs.getObject("BALLIMIT"), rs.getObject("UTIL_LIMIT"),
				rs.getObject("VALID_LIMIT"), rs.getObject("REPAY_MODE"), rs.getObject("MICRCODE"),
				rs.getObject("ACTIVE_FLAG"), rs.getObject("CITYID"), rs.getObject("SEGMENTS"), appDate, valueDate,
				batchId);
	}

	public static void savePrePaymentDetail(ResultSet rs, Date appDate, Date valueDate, long batchId,
			JdbcTemplate jdbcTemplate) throws Exception {

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO DM_PREPAYMENT_DETAILS (");
		sql.append("APPLID");
		sql.append(",AGREEMENTNO");
		sql.append(",PREPAYMENTID");
		sql.append(",PREPAYMENT_RATE");
		sql.append(",PREPAYMENT_TYPE");
		sql.append(",PREPAYMENT_PENALTY_DUE");
		sql.append(",PREPAYMENT_PENALTY_PAID");
		sql.append(",PREPAYMENT_AMT");
		sql.append(",BUSINESSDATE");
		sql.append(",PROCESSED_FLAG");
		sql.append(",PROCESS_DATE");
		sql.append(",SEGMENTS");
		sql.append(",PREPAYMENT_DATE");
		sql.append(",BUSINESS_YEAR");
		sql.append(",BATCH_ID");
		sql.append(") VALUES(");

		sql.append("?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(")");

		jdbcTemplate.update(sql.toString(), rs.getObject("APPLID"), rs.getObject("AGREEMENTNO"),
				rs.getObject("PREPAYMENTID"), rs.getObject("PREPAYMENT_RATE"), rs.getObject("PREPAYMENT_TYPE"),
				rs.getObject("PREPAYMENT_PENALTY_DUE"), rs.getObject("PREPAYMENT_PENALTY_PAID"),
				rs.getObject("PREPAYMENT_AMT"), appDate, rs.getObject("PROCESSED_FLAG"), valueDate,
				rs.getObject("SEGMENTS"), rs.getObject("PREPAYMENT_DATE"), rs.getObject("BUSINESS_YEAR"), batchId);
	}

	public static void savePresentationDetails(ResultSet rs, Date appDate, Date valueDate, long batchId,
			JdbcTemplate jdbcTemplate) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO DM_PRESENTATION_DETAILS (");

		sql.append("AGREEMENTNO");
		sql.append(",EMI");
		sql.append(",REPAY_TYPE");
		sql.append(",DEPOSITED_DATE");
		sql.append(",CREDIT_STATUS");
		sql.append(",RETURN_CODE");
		sql.append(",RETURN_REASON");
		sql.append(",REMARKS");
		sql.append(",SEGMENTS");
		sql.append(",PROCESSDATE");
		sql.append(",BUSINESSDATE");
		sql.append(",EMI_NO");
		sql.append(",CUSTOMER_BANK_NAME");
		sql.append(",BOM_BOUNCE_BUCKET");
		sql.append(",MICR_CODE");
		sql.append(",STATUS_UPDT_DATE");
		sql.append(",CUST_BANK_AC_NO");
		sql.append(",CUSTOMER_BANK_BRANCH");
		sql.append(",CHEQUESNO");
		sql.append(",CHEQUEDATE");
		sql.append(",FEMI_FLAG");
		sql.append(",HOLD_IGNORE_CODE");
		sql.append(",HOLD_IGNORE_REASON");
		sql.append(",DEST_ACC_HOLDER");
		sql.append(",PDCID");
		sql.append(",BBRANCHID");
		sql.append(",BATCH_ID");
		sql.append(") VALUES(");

		sql.append("?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(")");

		jdbcTemplate.update(sql.toString(), rs.getObject("AGREEMENTNO"), rs.getObject("EMI"),
				rs.getObject("REPAY_TYPE"), rs.getObject("DEPOSITED_DATE"), rs.getObject("CREDIT_STATUS"),
				rs.getObject("RETURN_CODE"), rs.getObject("RETURN_REASON"), rs.getObject("REMARKS"),
				rs.getObject("SEGMENTS"), valueDate, appDate, rs.getObject("EMI_NO"),
				rs.getObject("CUSTOMER_BANK_NAME"), rs.getObject("BOM_BOUNCE_BUCKET"), rs.getObject("MICR_CODE"),
				rs.getObject("STATUS_UPDT_DATE"), rs.getObject("CUST_BANK_AC_NO"), rs.getObject("CUSTOMER_BANK_BRANCH"),
				rs.getObject("CHEQUESNO"), rs.getObject("CHEQUEDATE"), rs.getObject("FEMI_FLAG"),
				rs.getObject("HOLD_IGNORE_CODE"), rs.getObject("HOLD_IGNORE_REASON"), rs.getObject("DEST_ACC_HOLDER"),
				rs.getObject("PDCID"), rs.getObject("BBRANCHID"), batchId);

	}

	public static void savePropertyDetail(ResultSet rs, Date appDate, Date valueDate, long batchId,
			JdbcTemplate jdbcTemplate) throws Exception {

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO DM_PROPERTY_DTL (");

		sql.append("PROPERTYID");
		sql.append(",APPLICATIONID");
		sql.append(",ADDRESS1");
		sql.append(",ADDRESS2");
		sql.append(",ADDRESS3");
		sql.append(",CITY");
		sql.append(",STATE");
		sql.append(",PRODUCTFLAG");
		sql.append(",BUSINESSDATE");
		sql.append(",PROPERTY_TYPE");
		sql.append(",PROPERTY_DESC");
		sql.append(",PROPERTY_VALUE");
		sql.append(",ZIPCODE");
		sql.append(",PROCESSFLAG");
		sql.append(",PROCESS_DATE");
		sql.append(",BATCH_ID");

		sql.append(") VALUES(");

		sql.append("?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(")");

		jdbcTemplate.update(sql.toString(), rs.getObject("PROPERTYID"), rs.getObject("APPLICATIONID"),
				rs.getObject("ADDRESS1"), rs.getObject("ADDRESS2"), rs.getObject("ADDRESS3"), rs.getObject("CITY"),
				rs.getObject("STATE"), rs.getObject("PRODUCTFLAG"), appDate, rs.getObject("PROPERTY_TYPE"),
				rs.getObject("PROPERTY_DESC"), rs.getObject("PROPERTY_VALUE"), rs.getObject("ZIPCODE"),
				rs.getObject("PROCESSFLAG"), valueDate, batchId);
	}

	public static void saveReschDetails(ResultSet rs, Date appDate, Date valueDate, long batchId,
			JdbcTemplate jdbcTemplate) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO DM_RESCH_DETAILS_DAILY (");

		sql.append("APPLID");
		sql.append(",AGREEMENTNO");
		sql.append(",DISB_STATUS");
		sql.append(",GROSS_TENURE");
		sql.append(",NET_TENURE");
		sql.append(",MATURITYDATE");
		sql.append(",EXPIRYDATE");
		sql.append(",EMI");
		sql.append(",REPAYMENT_MODE");
		sql.append(",PRODUCTFLAG");
		sql.append(",BUSINESSDATE");
		sql.append(",PROCESSED_FLAG");
		sql.append(",PROCESS_DATE");
		sql.append(",SEGMENTS");
		sql.append(",FREQ");
		sql.append(",LOAN_STATUS");
		sql.append(",CLOSUREDATE");
		sql.append(",CUST_ACCT_NO");
		sql.append(",BANKNAME");
		sql.append(",MICRCODE");
		sql.append(",CUST_BANK_BRANCH");
		sql.append(",CUST_BANK_CITY");
		sql.append(",BUSINESS_YEAR");
		sql.append(",PDCID");
		sql.append(",PCFLAG");
		sql.append(",TIE_UP");
		sql.append(",MARGIN");
		sql.append(",SPECIALMARGIN");
		sql.append(",FIXEDTENOR");
		sql.append(",CEEFFECTIVEDATE");
		sql.append(",EFF_RATE");
		sql.append(",PLRRATE");
		sql.append(",TIE_UP_WITH");
		sql.append(",DATE_OF_CLOSURE");
		sql.append(",PDCMS_SEQ_GENERATED_DATE");
		sql.append(",INSTRUMENT_DATA_ENTRY_DATE");
		sql.append(",PAYMENT_AUTHORIZATION_DATE");
		sql.append(",BATCH_ID");

		sql.append(") VALUES(");

		sql.append("?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(")");

		jdbcTemplate.update(sql.toString(), rs.getObject("APPLID"), rs.getObject("AGREEMENTNO"),
				rs.getObject("DISB_STATUS"), rs.getObject("GROSS_TENURE"), rs.getObject("NET_TENURE"),
				rs.getObject("MATURITYDATE"), rs.getObject("EXPIRYDATE"), rs.getObject("EMI"),
				rs.getObject("REPAYMENT_MODE"), rs.getObject("PRODUCTFLAG"), appDate, rs.getObject("PROCESSED_FLAG"),
				valueDate, rs.getObject("SEGMENTS"), rs.getObject("FREQ"), rs.getObject("LOAN_STATUS"),
				rs.getObject("CLOSUREDATE"), rs.getObject("CUST_ACCT_NO"), rs.getObject("BANKNAME"),
				rs.getObject("MICRCODE"), rs.getObject("CUST_BANK_BRANCH"), rs.getObject("CUST_BANK_CITY"),
				rs.getObject("BUSINESS_YEAR"), rs.getObject("PDCID"), rs.getObject("PCFLAG"), rs.getObject("TIE_UP"),
				rs.getObject("MARGIN"), rs.getObject("SPECIALMARGIN"), rs.getObject("FIXEDTENOR"),
				rs.getObject("CEEFFECTIVEDATE"), rs.getObject("EFF_RATE"), rs.getObject("PLRRATE"),
				rs.getObject("TIE_UP_WITH"), rs.getObject("DATE_OF_CLOSURE"), rs.getObject("PDCMS_SEQ_GENERATED_DATE"),
				rs.getObject("INSTRUMENT_DATA_ENTRY_DATE"), rs.getObject("PAYMENT_AUTHORIZATION_DATE"), batchId);

	}

	public static void saveSoaEmailDetails(ResultSet rs, long batchId, JdbcTemplate jdbcTemplate) throws Exception {

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO DM_SEND_SOA_EMAIL (");
		sql.append("AGREEMENTNO");
		sql.append(",CUSTOMERID");
		sql.append(",EMAILID");
		sql.append(",AGREEMENTID");
		sql.append(",PROCESSID");
		sql.append(",GENERATION_DATE");
		sql.append(",PROCESSED");
		sql.append(",PRODUCTFLAG");
		sql.append(",GROUPID");
		sql.append(",TOTAL_LAN");
		sql.append(",TOTAL_CLOSED_LAN");
		sql.append(",TOTAL_ACTIVE_LAN");
		sql.append(",SWIPE_FLAG");
		sql.append(",EMI_CARD_NO");
		sql.append(",DISBURSEMENT_DATE");
		sql.append(",SUPPLIERID");
		sql.append(",SUPPLIERDESC");
		sql.append(",AMT_FIN");
		sql.append(",EMI");
		sql.append(",NEXT_EMI_DUE_DATE");
		sql.append(",CHEQUE_BOUNCE_CHARGE");
		sql.append(",BATCH_ID");

		sql.append(") VALUES(");

		sql.append("?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(")");

		jdbcTemplate.update(sql.toString(), rs.getObject("AGREEMENTNO"), rs.getObject("CUSTOMERID"),
				rs.getObject("EMAILID"), rs.getObject("AGREEMENTID"), rs.getObject("PROCESSID"),
				rs.getObject("GENERATION_DATE"), rs.getObject("PROCESSED"), rs.getObject("PRODUCTFLAG"),
				rs.getObject("GROUPID"), rs.getObject("TOTAL_LAN"), rs.getObject("TOTAL_CLOSED_LAN"),
				rs.getObject("TOTAL_ACTIVE_LAN"), rs.getObject("SWIPE_FLAG"), rs.getObject("EMI_CARD_NO"),
				rs.getObject("DISBURSEMENT_DATE"), rs.getObject("SUPPLIERID"), rs.getObject("SUPPLIERDESC"),
				rs.getObject("AMT_FIN"), rs.getObject("EMI"), rs.getObject("NEXT_EMI_DUE_DATE"),
				rs.getObject("CHEQUE_BOUNCE_CHARGE"), batchId);

	}

	public static void saveSubQDisbDetails(ResultSet rs, Date appDate, Date valueDate, long batchId,
			JdbcTemplate jdbcTemplate) throws Exception {

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO DM_SUBQ_DISB_DETAILS (");

		sql.append("AGREEMENTDATE");
		sql.append(",APPLID");
		sql.append(",AGREEMENTNO");
		sql.append(",DISBURSEMENTNO");
		sql.append(",DISBURSEMENTDATE");
		sql.append(",PARENT_AGREEMENTNO");
		sql.append(",AMTFIN");
		sql.append(",NET_AMTFIN");
		sql.append(",DISBURSEDAMT");
		sql.append(",DISB_STATUS");
		sql.append(",FIRST_DUE_DATE");
		sql.append(",GROSS_TENURE");
		sql.append(",NET_TENURE");
		sql.append(",MATURITYDATE");
		sql.append(",EXPIRYDATE");
		sql.append(",NO_OF_ADV_INSTL");
		sql.append(",ADV_EMI_AMT");
		sql.append(",EMI");
		sql.append(",REPAYMENT_MODE");
		sql.append(",PRODUCTFLAG");
		sql.append(",PROMOTIONID");
		sql.append(",ICICI_LOMBARD");
		sql.append(",BAGIC");
		sql.append(",BALIC_CHARGES");
		sql.append(",BUSINESSDATE");
		sql.append(",PROCESSED_FLAG");
		sql.append(",PROCESS_DATE");
		sql.append(",SEGMENTS");
		sql.append(",FEE");
		sql.append(",DEALER_SUBV");
		sql.append(",MANU_SUBV_DED");
		sql.append(",MANU_SUBV_NDED");
		sql.append(",PREEMI");
		sql.append(",EXISTING_LANNO");
		sql.append(",MORTGAGE_FEE");
		sql.append(",COMMITMENT_FEE");
		sql.append(",PROCESSING_FEE");
		sql.append(",PRE_EMI_RECEIVABLE");
		sql.append(",INSURANCE");
		sql.append(",PAYMENTMODE");
		sql.append(",FREQ");
		sql.append(",CHEQUENUM");
		sql.append(",BATCH_ID");

		sql.append(") VALUES(");
		sql.append("?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(")");

		jdbcTemplate.update(sql.toString(), rs.getObject("AGREEMENTDATE"), rs.getObject("APPLID"),
				rs.getObject("AGREEMENTNO"), rs.getObject("DISBURSEMENTNO"), rs.getObject("DISBURSEMENTDATE"),
				rs.getObject("PARENT_AGREEMENTNO"), rs.getObject("AMTFIN"), rs.getObject("NET_AMTFIN"),
				rs.getObject("DISBURSEDAMT"), rs.getObject("DISB_STATUS"), rs.getObject("FIRST_DUE_DATE"),
				rs.getObject("GROSS_TENURE"), rs.getObject("NET_TENURE"), rs.getObject("MATURITYDATE"),
				rs.getObject("EXPIRYDATE"), rs.getObject("NO_OF_ADV_INSTL"), rs.getObject("ADV_EMI_AMT"),
				rs.getObject("EMI"), rs.getObject("REPAYMENT_MODE"), rs.getObject("PRODUCTFLAG"),
				rs.getObject("PROMOTIONID"), rs.getObject("ICICI_LOMBARD"), rs.getObject("BAGIC"),
				rs.getObject("BALIC_CHARGES"), appDate, rs.getObject("PROCESSED_FLAG"), valueDate,
				rs.getObject("SEGMENTS"), rs.getObject("FEE"), rs.getObject("DEALER_SUBV"),
				rs.getObject("MANU_SUBV_DED"), rs.getObject("MANU_SUBV_NDED"), rs.getObject("PREEMI"),
				rs.getObject("EXISTING_LANNO"), rs.getObject("MORTGAGE_FEE"), rs.getObject("COMMITMENT_FEE"),
				rs.getObject("PROCESSING_FEE"), rs.getObject("PRE_EMI_RECEIVABLE"), rs.getObject("INSURANCE"),
				rs.getObject("PAYMENTMODE"), rs.getObject("FREQ"), rs.getObject("CHEQUENUM"), batchId);

	}



	public static void saveWriteOffDetails(ResultSet rs, Date appDate, Date valueDate, long batchId,
			JdbcTemplate jdbcTemplate) throws Exception {

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO DM_WRITEOFF_DETAILS (");

		sql.append("APPLID");
		sql.append(",AGREEMENTNO");
		sql.append(",CHARGEOFF_DT");
		sql.append(",CHARGEOFF_FLG");
		sql.append(",CHARGEOFF_REASON");
		sql.append(",SETTLEMENT_LOSS");
		sql.append(",GROSS_WRITEOFF_AMT");
		sql.append(",NET_WRITEOFF_AMT");
		sql.append(",BUSINESSDATE");
		sql.append(",PROCESSED_FLAG");
		sql.append(",PROCESS_DATE");
		sql.append(",SEGMENTS");
		sql.append(",RECEIPT_ON_CHARGEOFF");
		sql.append(",BUSINESS_YEAR");
		sql.append(",BATCH_ID");

		sql.append(") VALUES(");

		sql.append("?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(",?");
		sql.append(")");

		jdbcTemplate.update(sql.toString(), rs.getObject("APPLID"), rs.getObject("AGREEMENTNO"),
				rs.getObject("CHARGEOFF_DT"), rs.getObject("CHARGEOFF_FLG"), rs.getObject("CHARGEOFF_REASON"),
				rs.getObject("SETTLEMENT_LOSS"), rs.getObject("GROSS_WRITEOFF_AMT"), rs.getObject("NET_WRITEOFF_AMT"),
				appDate, rs.getObject("PROCESSED_FLAG"), valueDate, rs.getObject("SEGMENTS"),
				rs.getObject("RECEIPT_ON_CHARGEOFF"), rs.getObject("BUSINESS_YEAR"), batchId);

	}
}
