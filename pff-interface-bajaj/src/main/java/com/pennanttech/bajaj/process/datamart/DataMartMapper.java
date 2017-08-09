package com.pennanttech.bajaj.process.datamart;

import java.sql.ResultSet;
import java.util.Date;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public class DataMartMapper {

	public static MapSqlParameterSource mapData(DataMartTable table, ResultSet rs, Date appDate) throws Exception {
		switch (table) {
		case DM_APPLICANT_DETAILS:
			return getApplicantMap(rs, appDate);
		case DM_ADDRESS_DETAILS:
			return getAddressMap(rs, appDate);
		case DM_APPLICATION_DETAILS:
			return getApplicationDetails(rs, appDate);
		case DM_BOUNCE_DETAILS:
			return getBounceDetailsMap(rs, appDate);
		case DM_COAPPLICANT_DETAILS:
			return getCoApplicantDetailMap(rs, appDate);
		case DM_DISB_DETAILS_DAILY:
			return getDisbDetailsMap(rs, appDate);
		case FORECLOSURECHARGES:
			return getForeClosureChargesMap(rs, appDate);
		case DM_HTS_UNADJUSTED_AMT:
			return getHTSUnadjustedMap(rs, appDate);
		case DM_INSURANCE_DETAILS:
			return getInsuranceDetailsMap(rs, appDate);
		case DM_IVR_GATEWAY_FLEXI:
			return getIVRDetailsMap(rs, appDate);
		case DM_LEA_DOC_DTL:
			return getLeaDocDtlMap(rs, appDate);
		case DM_LOAN_DETAILS_DAILY:
			return getLoanDetailMap(rs, appDate);
		case DM_LOAN_VOUCHER_DETAILS:
			return getLoanVoucherMap(rs, appDate);
		case DM_LOANWISE_CHARGE_DETAILS:
			return getLoanWiseChargeDetailMap(rs, appDate);
		case DM_LOANWISE_REPAYSCHD_DTLS:
			return getLoanWiseRepayScheduleDetailMap(rs, appDate);
		case DM_NOC_ELIGIBLE_LOANS:
			return getNoceligibleLoans(rs, appDate);
		case DM_OPENECS_DETAILS:
			return getOpenEcsDetailsMap(rs, appDate);
		case DM_PREPAYMENT_DETAILS:
			return getPrePaymentDetailMap(rs, appDate);
		case DM_PRESENTATION_DETAILS:
			return getPresentationDetailsMap(rs, appDate);
		case DM_PROPERTY_DTL:
			return getPropertyDetailMap(rs, appDate);
		case DM_RESCH_DETAILS_DAILY:
			return getReschDetailMap(rs, appDate);
		case DM_SEND_SOA_EMAIL:
			return getSoaEmailDetailMap(rs, appDate);
		case DM_SUBQ_DISB_DETAILS:
			return getSubQDisbDetailMap(rs, appDate);
		case DM_WRITEOFF_DETAILS:
			return getWriteOffDetailsMap(rs, appDate);

		default:
			break;

		}

		return null;
	}

	protected static MapSqlParameterSource getApplicantMap(ResultSet rs, Date appDate) throws Exception {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("CUSTOMERID", rs.getObject("CUSTOMERID"));
		map.addValue("CUST_TYPE", rs.getObject("CUST_TYPE"));
		map.addValue("PANNO", rs.getObject("PANNO"));
		map.addValue("ADDRESS1", rs.getObject("ADDRESS1"));
		map.addValue("ADDRESS2", rs.getObject("ADDRESS2"));
		map.addValue("ADDRESS3", rs.getObject("ADDRESS3"));
		map.addValue("ADDRESS4", rs.getObject("ADDRESS4"));
		map.addValue("CITY", rs.getObject("CITY"));
		map.addValue("STATE", rs.getObject("STATE"));
		map.addValue("COUNTRY", rs.getObject("COUNTRY"));
		map.addValue("ZIPCODE", rs.getObject("ZIPCODE"));
		map.addValue("ADDRESSTYPE", rs.getObject("ADDRESSTYPE"));
		map.addValue("EMAIL", rs.getObject("EMAIL"));
		map.addValue("PHONE1", rs.getObject("PHONE1"));
		map.addValue("PHONE2", rs.getObject("PHONE2"));
		map.addValue("MOBILE", rs.getObject("MOBILE"));
		map.addValue("FAX", rs.getObject("FAX"));
		map.addValue("EXISTING_CUST_FLAG", rs.getObject("EXISTING_CUST_FLAG"));
		map.addValue("INDIV_CORP_FLAG", rs.getObject("INDIV_CORP_FLAG"));
		map.addValue("AGE", rs.getObject("AGE"));
		map.addValue("DOB", rs.getObject("DOB"));
		map.addValue("FNAME", rs.getObject("FNAME"));
		map.addValue("MNAME", rs.getObject("MNAME"));
		map.addValue("LNAME", rs.getObject("LNAME"));
		map.addValue("GENDER", rs.getObject("GENDER"));
		map.addValue("MARITAL_STATUS", rs.getObject("MARITAL_STATUS"));
		map.addValue("NO_OF_DEPENDENT", rs.getObject("NO_OF_DEPENDENT"));
		map.addValue("YEARS_CURRENT_JOB", rs.getObject("YEARS_CURRENT_JOB"));
		map.addValue("YEARS_PREV_JOB", rs.getObject("YEARS_PREV_JOB"));
		map.addValue("QUALIFICATION", rs.getObject("QUALIFICATION"));
		map.addValue("RESIDENCETYPE", rs.getObject("RESIDENCETYPE"));
		map.addValue("YEARS_CURR_RESI", rs.getObject("YEARS_CURR_RESI"));
		map.addValue("EMPLOYER_DESC", rs.getObject("EMPLOYER_DESC"));
		map.addValue("COMPANY_TYPE", rs.getObject("COMPANY_TYPE"));
		map.addValue("INDUSTRYID", rs.getObject("INDUSTRYID"));
		map.addValue("NATURE_OF_BUSINESS", rs.getObject("NATURE_OF_BUSINESS"));
		map.addValue("EMPLOYMENT_TYPE", rs.getObject("EMPLOYMENT_TYPE"));
		map.addValue("EMPDESG", rs.getObject("EMPDESG"));
		map.addValue("OCCUPATION", rs.getObject("OCCUPATION"));
		map.addValue("ANNUAL_INCOME", rs.getObject("ANNUAL_INCOME"));
		map.addValue("GUARDIAN", rs.getObject("GUARDIAN"));
		map.addValue("BUSINESSDATE", appDate);
		map.addValue("PROCESSED_FLAG", "N");
		map.addValue("PROCESS_DATE", rs.getObject("PROCESS_DATE"));
		map.addValue("SEGMENTS", rs.getObject("SEGMENTS"));
		map.addValue("CUSTOMERNAME", rs.getObject("CUSTOMERNAME"));
		map.addValue("CONTACT_PERSON_NAME", rs.getObject("CONTACT_PERSON_NAME"));
		map.addValue("CONSTITUTION", rs.getObject("CONSTITUTION"));
		map.addValue("CUST_BANK_NAME", rs.getObject("CUST_BANK_NAME"));
		map.addValue("CUST_BANK_BRANCH", rs.getObject("CUST_BANK_BRANCH"));
		map.addValue("EMI_CARD_LIMIT", rs.getObject("EMI_CARD_LIMIT"));
		map.addValue("EMI_CARD_ACCEPT_FLAG", rs.getObject("EMI_CARD_ACCEPT_FLAG"));
		map.addValue("EMI_CARD_SWIPE_FLAG", rs.getObject("EMI_CARD_SWIPE_FLAG"));
		map.addValue("EMI_CARD_ELIG", rs.getObject("EMI_CARD_ELIG"));
		map.addValue("EMI_CARD_NO", rs.getObject("EMI_CARD_NO"));
		map.addValue("BANK_ECS_MANDATE", rs.getObject("BANK_ECS_MANDATE"));
		map.addValue("OPEN_ECS_AVLB", rs.getObject("OPEN_ECS_AVLB"));
		map.addValue("OPEN_ECS_DATE", rs.getObject("OPEN_ECS_DATE"));
		map.addValue("BUSINESS_YEAR", rs.getObject("BUSINESS_YEAR"));
		map.addValue("TITLE", rs.getObject("TITLE"));
		map.addValue("COMP_NAME", rs.getObject("COMP_NAME"));
		map.addValue("YEARS_CURR_JOB", rs.getObject("YEARS_CURR_JOB"));
		map.addValue("GRADE", rs.getObject("GRADE"));
		map.addValue("FAMILY_CODE", rs.getObject("FAMILY_CODE"));
		map.addValue("MINOR", rs.getObject("MINOR"));
		map.addValue("GUARDIAN_NEW", rs.getObject("GUARDIAN_NEW"));
		map.addValue("UCIN_NO", rs.getObject("UCIN_NO"));
		map.addValue("PREFERRED_ELIGIBILITY", rs.getObject("PREFERRED_ELIGIBILITY"));
		map.addValue("PREFERRED_CARD_ACCEPTANCE", rs.getObject("PREFERRED_CARD_ACCEPTANCE"));
		map.addValue("PREFERRED_CARD_LIMIT", rs.getObject("PREFERRED_CARD_LIMIT"));
		map.addValue("CUST_BRANCHID", rs.getObject("CUST_BRANCHID"));
		map.addValue("BATCH_ID", rs.getObject("BATCH_ID"));

		return map;
	}

	protected static MapSqlParameterSource getAddressMap(ResultSet rs,  Date appDate) throws Exception {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("ADDRESSID", rs.getObject("ADDRESSID"));
		map.addValue("CUSTOMERID", rs.getObject("CUSTOMERID"));
		map.addValue("STATEID", rs.getObject("STATEID"));
		map.addValue("REGIONID", rs.getObject("REGIONID"));
		map.addValue("CITY", rs.getObject("CITY"));
		map.addValue("STDISD", rs.getObject("STDISD"));
		map.addValue("MAILINGADDRES", rs.getObject("MAILINGADDRESS"));
		map.addValue("ADDRESS1", rs.getObject("ADDRESS1"));
		map.addValue("ADDRESS2", rs.getObject("ADDRESS2"));
		map.addValue("ADDRESS3", rs.getObject("ADDRESS3"));
		map.addValue("ZIPCODE", rs.getObject("ZIPCODE"));
		map.addValue("COUNTRY", rs.getObject("COUNTRY"));
		map.addValue("ADDRESSTYPE", rs.getObject("ADDRESSTYPE"));
		map.addValue("APPLICANT_TYPE", rs.getObject("APPLICANT_TYPE"));
		map.addValue("PHONE1", rs.getObject("PHONE1"));
		map.addValue("PHONE2", rs.getObject("PHONE2"));
		map.addValue("MOBILE", rs.getObject("MOBILE"));
		map.addValue("EMAIL", rs.getObject("EMAIL"));
		map.addValue("BUSINESSDATE", appDate);
		map.addValue("PROCESSFLAG", rs.getObject("PROCESSFLAG"));
		map.addValue("PROCESS_DATE", rs.getObject("PROCESS_DATE"));
		map.addValue("AREA", rs.getObject("AREA"));
		map.addValue("LANDMARK", rs.getObject("LANDMARK"));
		map.addValue("BUSINESS_YEAR", rs.getObject("BUSINESS_YEAR"));
		map.addValue("BATCH_ID", rs.getObject("BATCH_ID"));

		return map;
	}

	protected static MapSqlParameterSource getApplicationDetails(ResultSet rs,  Date appDate) throws Exception {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("APPLID", rs.getObject("APPLID"));
		map.addValue("CRM_DEALID", rs.getObject("CRM_DEALID"));
		map.addValue("CRSCOREDATE", rs.getObject("CRSCOREDATE"));
		map.addValue("CIBILSCORE", rs.getObject("CIBILSCORE"));
		map.addValue("APPSCORE", rs.getObject("APPSCORE"));
		map.addValue("BEHSCORE", rs.getObject("BEHSCORE"));
		map.addValue("DEVIATION_TYPE", rs.getObject("DEVIATION_TYPE"));
		map.addValue("DEVIATION_FLAG", rs.getObject("DEVIATION_FLAG"));
		map.addValue("DEVIATION_CODE", rs.getObject("DEVIATION_CODE"));
		map.addValue("DEVIATION_NAME", rs.getObject("DEVIATION_NAME"));
		map.addValue("REQ_TENURE", rs.getObject("REQ_TENURE"));
		map.addValue("REQ_LOAN_AMT", rs.getObject("REQ_LOAN_AMT"));
		map.addValue("LOAN_LIMIT", rs.getObject("LOAN_LIMIT"));
		map.addValue("APPROVE_LOAN_AMT", rs.getObject("APPROVE_LOAN_AMT"));
		map.addValue("LOAN_PURPOSE", rs.getObject("LOAN_PURPOSE"));
		map.addValue("LOAN_TYPE", rs.getObject("LOAN_TYPE"));
		map.addValue("CANCELLATIONDATE", rs.getObject("CANCELLATIONDATE"));
		map.addValue("CANCEL_REASON", rs.getObject("CANCEL_REASON"));
		map.addValue("FREQUENCY", rs.getObject("FREQUENCY"));
		map.addValue("LOAN_APPROVAL_DATE", rs.getObject("LOAN_APPROVAL_DATE"));
		map.addValue("AGREEMENTDATE", rs.getObject("AGREEMENTDATE"));
		map.addValue("INTRATE", rs.getObject("INTRATE"));
		map.addValue("FLAT_RATE", rs.getObject("FLAT_RATE"));
		map.addValue("IRR", rs.getObject("IRR"));
		map.addValue("GROSS_LTV", rs.getObject("GROSS_LTV"));
		map.addValue("NET_LTV", rs.getObject("NET_LTV"));
		map.addValue("COF", rs.getObject("COF"));
		map.addValue("DEBT_BURDEN_RATIO", rs.getObject("DEBT_BURDEN_RATIO"));
		map.addValue("FOIR_DB", rs.getObject("FOIR_DB"));
		map.addValue("SCHEMEID", rs.getObject("SCHEMEID"));
		map.addValue("SCHEMEDESC", rs.getObject("SCHEMEDESC"));
		map.addValue("SCHEMEGROUPID", rs.getObject("SCHEMEGROUPID"));
		map.addValue("SCHEME_GROUPG_DESC", rs.getObject("SCHEME_GROUPG_DESC"));
		map.addValue("PRODUCT_CATEGORY", rs.getObject("PRODUCT_CATEGORY"));
		map.addValue("PROD_TYPE", rs.getObject("PROD_TYPE"));
		map.addValue("PROMOTIONID", rs.getObject("PROMOTIONID"));
		map.addValue("PROGRAMID", rs.getObject("PROGRAMID"));
		map.addValue("SURROGATE_FLAG", rs.getObject("SURROGATE_FLAG"));
		map.addValue("SOURCING_CHANNEL_TYPE", rs.getObject("SOURCING_CHANNEL_TYPE"));
		map.addValue("SOURCING_CHANNEL_NAME", rs.getObject("SOURCING_CHANNEL_NAME"));
		map.addValue("REFERAL_GROUP", rs.getObject("REFERAL_GROUP"));
		map.addValue("REFERAL_NAME", rs.getObject("REFERAL_NAME"));
		map.addValue("COUNTRYID", rs.getObject("COUNTRYID"));
		map.addValue("COUNTRY", rs.getObject("COUNTRY"));
		map.addValue("REGIONID", rs.getObject("REGIONID"));
		map.addValue("REGION", rs.getObject("REGION"));
		map.addValue("STATEID", rs.getObject("STATEID"));
		map.addValue("STATE", rs.getObject("STATE"));
		map.addValue("CITYID", rs.getObject("CITYID"));
		map.addValue("CITY", rs.getObject("CITY"));
		map.addValue("BRANCHID", rs.getObject("BRANCHID"));
		map.addValue("BRANCHDESC", rs.getObject("BRANCHDESC"));
		map.addValue("BROKER_NAME", rs.getObject("BROKER_NAME"));
		map.addValue("DME_NAME", rs.getObject("DME_NAME"));
		map.addValue("ASM_NAME", rs.getObject("ASM_NAME"));
		map.addValue("RSM_NAME", rs.getObject("RSM_NAME"));
		map.addValue("CRDT_MGR_NAME", rs.getObject("CRDT_MGR_NAME"));
		map.addValue("ROID_NAME", rs.getObject("ROID_NAME"));
		map.addValue("TLID_NAME", rs.getObject("TLID_NAME"));
		map.addValue("BMID_NAME", rs.getObject("BMID_NAME"));
		map.addValue("COID_NAME", rs.getObject("COID_NAME"));
		map.addValue("SUPPLIERID", rs.getObject("SUPPLIERID"));
		map.addValue("DLR_PARTICIPATION_RATE", rs.getObject("DLR_PARTICIPATION_RATE"));
		map.addValue("LOCAL_OUTSTATION_FLG", rs.getObject("LOCAL_OUTSTATION_FLG"));
		map.addValue("CUSTOMERID", rs.getObject("CUSTOMERID"));
		map.addValue("BUSINESSDATE", appDate);
		map.addValue("PROCESSED_FLAG", "N");
		map.addValue("PROCESS_DATE", rs.getObject("PROCESS_DATE"));
		map.addValue("PRODUCT", rs.getObject("PRODUCT"));
		map.addValue("SEGMENTS", rs.getObject("SEGMENTS"));
		map.addValue("CRM_STATUS", rs.getObject("CRM_STATUS"));
		map.addValue("LOGINDATE", rs.getObject("LOGINDATE"));
		map.addValue("APPR_REJ_DATE", rs.getObject("APPR_REJ_DATE"));
		map.addValue("CANCEL_DATE", rs.getObject("CANCEL_DATE"));
		map.addValue("FILE_STATUS", rs.getObject("FILE_STATUS"));
		map.addValue("FILE_HOLD_REASON", rs.getObject("FILE_HOLD_REASON"));
		map.addValue("QDPDONE", rs.getObject("QDPDONE"));
		map.addValue("ISHOLD", rs.getObject("ISHOLD"));
		map.addValue("BRANCH_INW_DATE", rs.getObject("BRANCH_INW_DATE"));
		map.addValue("BRANCH_HOLD_DATE", rs.getObject("BRANCH_HOLD_DATE"));
		map.addValue("CPU_INW_DATE", rs.getObject("CPU_INW_DATE"));
		map.addValue("CPU_HOLD_DATE", rs.getObject("CPU_HOLD_DATE"));
		map.addValue("SYSTEM_HOLD", rs.getObject("SYSTEM_HOLD"));
		map.addValue("PSL_FLAG", rs.getObject("PSL_FLAG"));
		map.addValue("DOC_WAVE_FLAG", rs.getObject("DOC_WAVE_FLAG"));
		map.addValue("CUSTOMER_SWIPE", rs.getObject("CUSTOMER_SWIPE"));
		map.addValue("CUSTOMER_ACCEPTANCE", rs.getObject("CUSTOMER_ACCEPTANCE"));
		map.addValue("KYC_DOC_TYPE", rs.getObject("KYC_DOC_TYPE"));
		map.addValue("KYC_DOC_ID", rs.getObject("KYC_DOC_ID"));
		map.addValue("BUSINESS_IRR", rs.getObject("BUSINESS_IRR"));
		map.addValue("INSPECTORNAME", rs.getObject("INSPECTORNAME"));
		map.addValue("REGNUMBER", rs.getObject("REGNUMBER"));
		map.addValue("BUSINESS_YEAR", rs.getObject("BUSINESS_YEAR"));
		map.addValue("DEALID", rs.getObject("DEALID"));
		map.addValue("PRETAXIRR", rs.getObject("PRETAXIRR"));
		map.addValue("ODM_FLAG", rs.getObject("ODM_FLAG"));
		map.addValue("DI_DATE", rs.getObject("DI_DATE"));
		map.addValue("LAA_QDP_FLAG", rs.getObject("LAA_QDP_FLAG"));
		map.addValue("PREFERRED_CARD_ACCEPTANCE", rs.getObject("PREFERRED_CARD_ACCEPTANCE"));
		map.addValue("ORIG_AMTFIN", rs.getObject("ORIG_AMTFIN"));
		map.addValue("ELC_FLAG", rs.getObject("ELC_FLAG"));
		map.addValue("ELC_LIMIT", rs.getObject("ELC_LIMIT"));
		map.addValue("QDP_DONE_DATE", rs.getObject("QDP_DONE_DATE"));
		map.addValue("LAA_DECENTRALIZED_FLAG", rs.getObject("LAA_DECENTRALIZED_FLAG"));
		map.addValue("FCU_FLAG", rs.getObject("FCU_FLAG"));
		map.addValue("MKTGID", rs.getObject("MKTGID"));
		map.addValue("DM_MPID", rs.getObject("DM_MPID"));
		map.addValue("SWIPE_CARD_CODE", rs.getObject("SWIPE_CARD_CODE"));
		map.addValue("SOURCE_CARD_CODE", rs.getObject("SOURCE_CARD_CODE"));
		map.addValue("DII_USER_ID", rs.getObject("DII_USER_ID"));
		map.addValue("QDP_CHEQUE_ISSUE", rs.getObject("QDP_CHEQUE_ISSUE"));
		map.addValue("INTEREST_TYPE", rs.getObject("INTEREST_TYPE"));
		map.addValue("DII_DONE_DATE", rs.getObject("DII_DONE_DATE"));
		map.addValue("FINISH_DATE", rs.getObject("FINISH_DATE"));
		map.addValue("BATCH_ID", rs.getObject("BATCH_ID"));

		return map;
	}

	protected static MapSqlParameterSource getBounceDetailsMap(ResultSet rs,  Date appDate) throws Exception {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("APPLID", rs.getObject("APPLID"));
		map.addValue("CHEQUEID", rs.getObject("CHEQUEID"));
		map.addValue("AGREEMENTNO", rs.getObject("AGREEMENTNO"));
		map.addValue("BOUNCE_DATE", rs.getObject("BOUNCE_DATE"));
		map.addValue("BOUNCE_REASON", rs.getObject("BOUNCE_REASON"));
		map.addValue("BOUNCE_AMT", rs.getObject("BOUNCE_AMT"));
		map.addValue("BUSINESSDATE", appDate);
		map.addValue("PROCESSED_FLAG", "N");
		map.addValue("PROCESS_DATE", rs.getObject("PROCESS_DATE"));
		map.addValue("SEGMENTS", rs.getObject("SEGMENTS"));
		map.addValue("BUSINESS_YEAR", rs.getObject("BUSINESS_YEAR"));
		map.addValue("CHEQUEDATE", rs.getObject("CHEQUEDATE"));

		return map;

	}

	private static MapSqlParameterSource getCoApplicantDetailMap(ResultSet rs,  Date appDate) throws Exception {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("CUSTOMERID", rs.getObject("CUSTOMERID"));
		map.addValue("APPLICANT_TYPE", rs.getObject("APPLICANT_TYPE"));
		map.addValue("PAN_NO", rs.getObject("PAN_NO"));
		map.addValue("CUST_RELATION", rs.getObject("CUST_RELATION"));
		map.addValue("CUST_TYPE", rs.getObject("CUST_TYPE"));
		map.addValue("AGE", rs.getObject("AGE"));
		map.addValue("DOB", rs.getObject("DOB"));
		map.addValue("FNAME", rs.getObject("FNAME"));
		map.addValue("MNAME", rs.getObject("MNAME"));
		map.addValue("LNAME", rs.getObject("LNAME"));
		map.addValue("GENDER", rs.getObject("GENDER"));
		map.addValue("MARITAL_STATUS", rs.getObject("MARITAL_STATUS"));
		map.addValue("NO_OF_DEPENDENT", rs.getObject("NO_OF_DEPENDENT"));
		map.addValue("YRS_CURRENTJOB", rs.getObject("YRS_CURRENTJOB"));
		map.addValue("PREVIOUS_JOB_YEAR", rs.getObject("PREVIOUS_JOB_YEAR"));
		map.addValue("QUALIFICATION", rs.getObject("QUALIFICATION"));
		map.addValue("EMPLOYER_DESC", rs.getObject("EMPLOYER_DESC"));
		map.addValue("COMPANY_TYPE", rs.getObject("COMPANY_TYPE"));
		map.addValue("INDUSTRYID", rs.getObject("INDUSTRYID"));
		map.addValue("BUSINESS_NATURE", rs.getObject("BUSINESS_NATURE"));
		map.addValue("OCCUPATION_CODE", rs.getObject("OCCUPATION_CODE"));
		map.addValue("GUARDIAN", rs.getObject("GUARDIAN"));
		map.addValue("PROCESS_FLAG", rs.getObject("PROCESS_FLAG"));
		map.addValue("PROCESS_DATE", rs.getObject("PROCESS_DATE"));
		map.addValue("EMP_TYPE", rs.getObject("EMP_TYPE"));
		map.addValue("BUSINESSDATE", appDate);
		map.addValue("INCOME", rs.getObject("INCOME"));
		map.addValue("APPLID", rs.getObject("APPLID"));
		map.addValue("BATCH_ID", rs.getObject("BATCH_ID"));

		return map;
	}

	private static MapSqlParameterSource getDisbDetailsMap(ResultSet rs,  Date appDate) throws Exception {
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("AGREEMENTDATE", rs.getObject("AGREEMENTDATE"));
		map.addValue("APPLID", rs.getObject("APPLID"));
		map.addValue("AGREEMENTNO", rs.getObject("AGREEMENTNO"));
		map.addValue("DISBURSEMENTNO", rs.getObject("DISBURSEMENTNO"));
		map.addValue("DISBURSEMENTDATE", rs.getObject("DISBURSEMENTDATE"));
		map.addValue("PARENT_AGREEMENTNO", rs.getObject("PARENT_AGREEMENTNO"));
		map.addValue("AMTFIN", rs.getObject("AMTFIN"));
		map.addValue("NET_AMTFIN", rs.getObject("NET_AMTFIN"));
		map.addValue("DISBURSEDAMT", rs.getObject("DISBURSEDAMT"));
		map.addValue("DISB_STATUS", rs.getObject("DISB_STATUS"));
		map.addValue("FIRST_DUE_DATE", rs.getObject("FIRST_DUE_DATE"));
		map.addValue("GROSS_TENURE", rs.getObject("GROSS_TENURE"));
		map.addValue("NET_TENURE", rs.getObject("NET_TENURE"));
		map.addValue("MATURITYDATE", rs.getObject("MATURITYDATE"));
		map.addValue("EXPIRYDATE", rs.getObject("EXPIRYDATE"));
		map.addValue("NO_OF_ADV_INSTL", rs.getObject("NO_OF_ADV_INSTL"));
		map.addValue("ADV_EMI_AMT", rs.getObject("ADV_EMI_AMT"));
		map.addValue("EMI", rs.getObject("EMI"));
		map.addValue("REPAYMENT_MODE", rs.getObject("REPAYMENT_MODE"));
		map.addValue("PRODUCTFLAG", rs.getObject("PRODUCTFLAG"));
		map.addValue("PROMOTIONID", rs.getObject("PROMOTIONID"));
		map.addValue("ICICI_LOMBARD", rs.getObject("ICICI_LOMBARD"));
		map.addValue("BAGIC", rs.getObject("BAGIC"));
		map.addValue("BALIC_CHARGES", rs.getObject("BALIC_CHARGES"));
		map.addValue("BUSINESSDATE", appDate);
		map.addValue("PROCESSED_FLAG", "N");
		map.addValue("PROCESS_DATE", rs.getObject("PROCESS_DATE"));
		map.addValue("SEGMENTS", rs.getObject("SEGMENTS"));
		map.addValue("FEE", rs.getObject("FEE"));
		map.addValue("DEALER_SUBV", rs.getObject("DEALER_SUBV"));
		map.addValue("MANU_SUBV_DED", rs.getObject("MANU_SUBV_DED"));
		map.addValue("MANU_SUBV_NDED", rs.getObject("MANU_SUBV_NDED"));
		map.addValue("PREEMI", rs.getObject("PREEMI"));
		map.addValue("EXISTING_LANNO", rs.getObject("EXISTING_LANNO"));
		map.addValue("MORTGAGE_FEE", rs.getObject("MORTGAGE_FEE"));
		map.addValue("COMMITMENT_FEE", rs.getObject("COMMITMENT_FEE"));
		map.addValue("PROCESSING_FEE", rs.getObject("PROCESSING_FEE"));
		map.addValue("PRE_EMI_RECEIVABLE", rs.getObject("PRE_EMI_RECEIVABLE"));
		map.addValue("INSURANCE", rs.getObject("INSURANCE"));
		map.addValue("PAYMENTMODE", rs.getObject("PAYMENTMODE"));
		map.addValue("FREQ", rs.getObject("FREQ"));
		map.addValue("CHEQUENUM", rs.getObject("CHEQUENUM"));
		map.addValue("CUST_ACCT_NO", rs.getObject("CUST_ACCT_NO"));
		map.addValue("BANKNAME", rs.getObject("BANKNAME"));
		map.addValue("MICRCODE", rs.getObject("MICRCODE"));
		map.addValue("BUSINESS_YEAR", rs.getObject("BUSINESS_YEAR"));
		map.addValue("EMI_CHARGE", rs.getObject("EMI_CHARGE"));
		map.addValue("PDC_CHARGE", rs.getObject("PDC_CHARGE"));
		map.addValue("IRR_PER", rs.getObject("IRR_PER"));
		map.addValue("FEE_WL", rs.getObject("FEE_WL"));
		map.addValue("ELC_CHARGE", rs.getObject("ELC_CHARGE"));
		map.addValue("CREDIT_VIDYA_FEES", rs.getObject("CREDIT_VIDYA_FEES"));
		map.addValue("BATCH_ID", rs.getObject("BATCH_ID"));

		return map;
	}

	private static MapSqlParameterSource getForeClosureChargesMap(ResultSet rs,  Date appDate) throws Exception {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("AGREEMENTID", rs.getObject("AGREEMENTID"));
		map.addValue("BAL_PRINCIPAL", rs.getObject("BAL_PRINCIPAL"));
		map.addValue("RESEDUL_VALUE", rs.getObject("RESEDUL_VALUE"));
		map.addValue("INSTALLMENTS", rs.getObject("INSTALLMENTS"));
		map.addValue("ADVICES", rs.getObject("ADVICES"));
		map.addValue("PENALTY", rs.getObject("PENALTY"));
		map.addValue("INTREST_ON_TERMINATION", rs.getObject("INTREST_ON_TERMINATION"));
		map.addValue("FLOATINTREST_ON_TERMINATION", rs.getObject("FLOATINTREST_ON_TERMINATION"));
		map.addValue("OVER_DUES", rs.getObject("OVER_DUES"));
		map.addValue("CURRENT_OVERDUES", rs.getObject("CURRENT_OVERDUES"));
		map.addValue("CURRENT_WAVEOFFAMT", rs.getObject("CURRENT_WAVEOFFAMT"));
		map.addValue("OVER_DISTANCE_CHARGES", rs.getObject("OVER_DISTANCE_CHARGES"));
		map.addValue("INTREST_ON_TERMINATION_PER_DAY", rs.getObject("INTREST_ON_TERMINATION_PER_DAY"));
		map.addValue("INTREST_ACCRUALS", rs.getObject("INTREST_ACCRUALS"));
		map.addValue("EXCESS_AMOUNT", rs.getObject("EXCESS_AMOUNT"));
		map.addValue("EXCESS_REFUND", rs.getObject("EXCESS_REFUND"));
		map.addValue("ADVICE", rs.getObject("ADVICE"));
		map.addValue("REBATE", rs.getObject("REBATE"));
		map.addValue("ADVINSTL", rs.getObject("ADVINSTL"));
		map.addValue("EXCESSPRINPMNT", rs.getObject("EXCESSPRINPMNT"));
		map.addValue("SDAMT", rs.getObject("SDAMT"));
		map.addValue("SDINT", rs.getObject("SDINT"));
		map.addValue("EXCESS_INTREST_RATE", rs.getObject("EXCESS_INTREST_RATE"));
		map.addValue("VAT_ON_FORECLOSURE", rs.getObject("VAT_ON_FORECLOSURE"));
		map.addValue("UNDER_DISTANCE_CHARGES", rs.getObject("UNDER_DISTANCE_CHARGES"));
		map.addValue("NET_PAYBALE", rs.getObject("NET_PAYBALE"));
		map.addValue("WAIVEOFFAMOUNT", rs.getObject("WAIVEOFFAMOUNT"));
		map.addValue("ACTIVITY", rs.getObject("ACTIVITY"));
		map.addValue("AUTHORIZEDON", rs.getObject("AUTHORIZEDON"));
		map.addValue("COMMITMENT_FEE", rs.getObject("COMMITMENT_FEE"));
		map.addValue("ORIGINATION_FEE", rs.getObject("ORIGINATION_FEE"));
		map.addValue("PRE_EMI", rs.getObject("PRE_EMI"));
		map.addValue("BUSINESS_DATE", rs.getObject("BUSINESS_DATE"));
		map.addValue("CHEQUEID", rs.getObject("CHEQUEID"));
		map.addValue("STATUS", rs.getObject("STATUS"));
		map.addValue("INTEREST_WAIVE_OFF", rs.getObject("INTEREST_WAIVE_OFF"));
		map.addValue("BALANCE_PRIN_WAIVE_OFF", rs.getObject("BALANCE_PRIN_WAIVE_OFF"));
		map.addValue("INSTALLMET_INT_WAIVE_OFF", rs.getObject("INSTALLMET_INT_WAIVE_OFF"));
		map.addValue("WOFF_CURRMONTH_INT", rs.getObject("WOFF_CURRMONTH_INT"));
		map.addValue("WOFF_OVERDUE_CHARGE", rs.getObject("WOFF_OVERDUE_CHARGE"));
		map.addValue("WOFF_CHQBOUNCE_CHARGES", rs.getObject("WOFF_CHQBOUNCE_CHARGES"));
		map.addValue("WOFF_OTHERS", rs.getObject("WOFF_OTHERS"));
		map.addValue("INSTALLMENT_PRIN_WAIVE_OFF", rs.getObject("INSTALLMENT_PRIN_WAIVE_OFF"));
		map.addValue("WOFF_PARKING_CHARGES", rs.getObject("WOFF_PARKING_CHARGES"));
		map.addValue("TOT_OTHER_REPO_CHARGES", rs.getObject("TOT_OTHER_REPO_CHARGES"));
		map.addValue("WOFF_OTHER_REPO_CHARGES", rs.getObject("WOFF_OTHER_REPO_CHARGES"));
		map.addValue("TOT_REPOSESSION_CHARGES", rs.getObject("TOT_REPOSESSION_CHARGES"));
		map.addValue("WOFF_REPOSESSION_CHARGES", rs.getObject("WOFF_REPOSESSION_CHARGES"));
		map.addValue("TOT_PARKING_CHARGES", rs.getObject("TOT_PARKING_CHARGES"));
		return map;

	}

	private static MapSqlParameterSource getHTSUnadjustedMap(ResultSet rs,  Date appDate) throws Exception {

		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("APPLID", rs.getObject("APPLID"));
		map.addValue("UNADJUSTED_AMOUNT", rs.getObject("UNADJUSTED_AMOUNT"));
		map.addValue("BUSINESSDATE", appDate);

		return map;

	}

	private static MapSqlParameterSource getInsuranceDetailsMap(ResultSet rs,  Date appDate) throws Exception {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("APPLID", rs.getObject("APPLID"));
		map.addValue("AGREEMENTNO", rs.getObject("AGREEMENTNO"));
		map.addValue("ASSETID", rs.getObject("ASSETID"));
		map.addValue("INSURANCE_TYPE", rs.getObject("INSURANCE_TYPE"));
		map.addValue("INSUR_PREMIUM", rs.getObject("INSUR_PREMIUM"));
		map.addValue("INSURANCE_RENEWAL_DATE", rs.getObject("INSURANCE_RENEWAL_DATE"));
		map.addValue("BUSINESSDATE", appDate);
		map.addValue("PROCESSED_FLAG", "N");
		map.addValue("PROCESS_DATE", rs.getObject("PROCESS_DATE"));
		map.addValue("SEGMENTS", rs.getObject("SEGMENTS"));
		map.addValue("BUSINESS_YEAR", rs.getObject("BUSINESS_YEAR"));
		map.addValue("POLICY_TENURE", rs.getObject("POLICY_TENURE"));
		map.addValue("SUM_ASSURED", rs.getObject("SUM_ASSURED"));
		map.addValue("NOMINEE_NAME", rs.getObject("NOMINEE_NAME"));
		map.addValue("GOOD_HEALTH", rs.getObject("GOOD_HEALTH"));
		map.addValue("CRITICAL_ILLNESS_FLAG", rs.getObject("CRITICAL_ILLNESS_FLAG"));
		map.addValue("RELATIONSHIP", rs.getObject("RELATIONSHIP"));
		map.addValue("NOMINEE_ADDRESS", rs.getObject("NOMINEE_ADDRESS"));
		map.addValue("NOMINEE_RELATION", rs.getObject("NOMINEE_RELATION"));
		map.addValue("NOMINEE_DOB", rs.getObject("NOMINEE_DOB"));
		map.addValue("NOMINEE_AGE", rs.getObject("NOMINEE_AGE"));
		map.addValue("NOMINEE_CONTACTNO", rs.getObject("NOMINEE_CONTACTNO"));
		map.addValue("NOMINEE_NAME2", rs.getObject("NOMINEE_NAME2"));
		map.addValue("NOMINEE_ADDRESS2", rs.getObject("NOMINEE_ADDRESS2"));
		map.addValue("NOMINEE_RELATION2", rs.getObject("NOMINEE_RELATION2"));
		map.addValue("NOMINEE_DOB2", rs.getObject("NOMINEE_DOB2"));
		map.addValue("NOMINEE_AGE2", rs.getObject("NOMINEE_AGE2"));
		map.addValue("NOMINEE_CONTACTNO2", rs.getObject("NOMINEE_CONTACTNO2"));
		map.addValue("DFGH", rs.getObject("DFGH"));
		map.addValue("BATCH_ID", rs.getObject("BATCH_ID"));

		return map;
	}

	private static MapSqlParameterSource getIVRDetailsMap(ResultSet rs,  Date appDate) throws Exception {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("AGREEMENTNO", rs.getObject("AGREEMENTNO"));
		map.addValue("DROP_LINE_LIMIT", rs.getObject("DROP_LINE_LIMIT"));
		map.addValue("AMOUNT_DRAWN_LIMIT", rs.getObject("AMOUNT_DRAWN_LIMIT"));
		map.addValue("UTLIZED_BALANCE_LIMIT", rs.getObject("UTLIZED_BALANCE_LIMIT"));
		map.addValue("PRINCIPLE_AMOUNT_PAID", rs.getObject("PRINCIPLE_AMOUNT_PAID"));
		map.addValue("BALANCE_PRINCIPAL_OUTSTANDING", rs.getObject("BALANCE_PRINCIPAL_OUTSTANDING"));
		map.addValue("PROCESS_DATE", rs.getObject("PROCESS_DATE"));
		map.addValue("BUSINESSDATE", appDate);
		map.addValue("BATCH_ID", rs.getObject("BATCH_ID"));

		return map;

	}

	private static MapSqlParameterSource getLeaDocDtlMap(ResultSet rs,  Date appDate) throws Exception {
		MapSqlParameterSource map = new MapSqlParameterSource();

		
		map.addValue("AGREEMENTID", rs.getObject("AGREEMENTID"));
		map.addValue("APPLICABLE", rs.getObject("APPLICABLE"));
		map.addValue("APPLID", rs.getObject("APPLID"));
		map.addValue("APPROVED_BY", rs.getObject("APPROVED_BY"));
		map.addValue("AUTHDATE", rs.getObject("AUTHDATE"));
		map.addValue("AUTHID", rs.getObject("AUTHID"));
		map.addValue("AWB_NO", rs.getObject("AWB_NO"));
		map.addValue("BRANCHSTATUS", rs.getObject("BRANCHSTATUS"));
		map.addValue("CONSTITUTION", rs.getObject("CONSTITUTION"));
		map.addValue("COURIER_NAME", rs.getObject("COURIER_NAME"));
		map.addValue("CRITICAL", rs.getObject("CRITICAL"));
		map.addValue("CUSTOMER_TYPE", rs.getObject("CUSTOMER_TYPE"));
		map.addValue("CUSTOMERID", rs.getObject("CUSTOMERID"));
		map.addValue("DATE_BRANCH_STATUS", rs.getObject("DATE_BRANCH_STATUS"));
		map.addValue("DATELASTUPDT", rs.getObject("DATELASTUPDT"));
		map.addValue("DOC_CLASSFN", rs.getObject("DOC_CLASSFN"));
		map.addValue("DOC_RETRIEVAL_STATUS", rs.getObject("DOC_RETRIEVAL_STATUS"));
		map.addValue("DOCID", rs.getObject("DOCID"));
		map.addValue("DOCTYPE", rs.getObject("DOCTYPE"));
		map.addValue("FILE_BARCODE", rs.getObject("FILE_BARCODE"));
		map.addValue("MAKERDATE", rs.getObject("MAKERDATE"));
		map.addValue("MAKERID", rs.getObject("MAKERID"));
		map.addValue("MC_STATUS", rs.getObject("MC_STATUS"));
		map.addValue("NEW_FLAG", rs.getObject("NEW_FLAG"));
		map.addValue("ORIGINALS", rs.getObject("ORIGINALS"));
		map.addValue("PACKET_TRACKER_NO", rs.getObject("PACKET_TRACKER_NO"));
		map.addValue("PROPERTY_ADDRESS", rs.getObject("PROPERTY_ADDRESS"));
		map.addValue("PROPERTY_CODE", rs.getObject("PROPERTY_CODE"));
		map.addValue("REASON", rs.getObject("REASON"));
		map.addValue("RECEIVED", rs.getObject("RECEIVED"));
		map.addValue("RECEIVEDDATE", rs.getObject("RECEIVEDDATE"));
		map.addValue("REJECTION_REASONS", rs.getObject("REJECTION_REASONS"));
		map.addValue("RELEASED_DATE", rs.getObject("RELEASED_DATE"));
		map.addValue("RELEASED_REMARKS", rs.getObject("RELEASED_REMARKS"));
		map.addValue("REMARKS", rs.getObject("REMARKS"));
		map.addValue("STAGE", rs.getObject("STAGE"));
		map.addValue("TARGETDATE", rs.getObject("TARGETDATE"));
		map.addValue("TITLE_DOCUMENT", rs.getObject("TITLE_DOCUMENT"));
		map.addValue("TXNDOCID", rs.getObject("TXNDOCID"));
		map.addValue("VALIDTILLDATE", rs.getObject("VALIDTILLDATE"));
		map.addValue("VAP_LOAN_FLAG", rs.getObject("VAP_LOAN_FLAG"));
		map.addValue("BUSINESSDATE", appDate);
		map.addValue("PROCESSFLAG", rs.getObject("PROCESSFLAG"));
		map.addValue("PROCESSDATE", rs.getObject("PROCESSDATE"));
		map.addValue("SEGMENT", rs.getObject("SEGMENT"));
		map.addValue("REVISED_TARGET_DATE", rs.getObject("REVISED_TARGET_DATE"));

		
		
		return map;

	}

	private static MapSqlParameterSource getLoanDetailMap(ResultSet rs,  Date appDate) throws Exception {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("APPLID", rs.getObject("APPLID"));
		map.addValue("AGREEMENTNO", rs.getObject("AGREEMENTNO"));
		map.addValue("CUSTOMERID", rs.getObject("CUSTOMERID"));
		map.addValue("CUSTOMER_YIELD", rs.getObject("CUSTOMER_YIELD"));
		map.addValue("STATUS", rs.getObject("STATUS"));
		map.addValue("NPA_STAGE", rs.getObject("NPA_STAGE"));
		map.addValue("LMS_BUCKET", rs.getObject("LMS_BUCKET"));
		map.addValue("COLL_BUCKET", rs.getObject("COLL_BUCKET"));
		map.addValue("INSURANCE_APPLIED_FLG", rs.getObject("INSURANCE_APPLIED_FLG"));
		map.addValue("BUSINESSDATE", appDate);
		map.addValue("PROCESSED_FLAG", "N");
		map.addValue("PROCESS_DATE", rs.getObject("PROCESS_DATE"));
		map.addValue("SEGMENTS", rs.getObject("SEGMENTS"));
		map.addValue("CLOSUREDATE", rs.getDate("CLOSUREDATE"));
		map.addValue("TOPUP_AMT", rs.getObject("TOPUP_AMT"));
		map.addValue("BUSINESS_YEAR", rs.getObject("BUSINESS_YEAR"));
		map.addValue("PDCID", rs.getObject("PDCID"));
		map.addValue("PDCFLAG", rs.getObject("PCFLAG"));
		map.addValue("OD_FLAG", rs.getObject("OD_FLAG"));
		map.addValue("MARGIN", rs.getObject("MARGIN"));
		map.addValue("SPECIALMARGIN", rs.getObject("SPECIALMARGIN"));
		map.addValue("FIXEDTENOR", rs.getObject("FIXEDTENOR"));
		map.addValue("CEEFFECTIVEDATE", rs.getObject("CEEFFECTIVEDATE"));
		map.addValue("EFF_RATE", rs.getObject("EFF_RATE"));
		map.addValue("PLRRATE", rs.getObject("PLRRATE"));
		map.addValue("PARTY_CODE", rs.getObject("PARTY_CODE"));
		map.addValue("PARTY_NAME", rs.getObject("PARTY_NAME"));
		map.addValue("ZONE", rs.getObject("ZONE"));
		map.addValue("COLLECTION_CENTRE", rs.getObject("COLLECTION_CENTRE"));
		map.addValue("VIRTUAL_ACCOUNT_NUMBER", rs.getObject("VIRTUAL_ACCOUNT_NUMBER"));
		map.addValue("INSTALLMENT_TYPE", rs.getObject("INSTALLMENT_TYPE"));
		map.addValue("COMPANYTYPE", rs.getObject("COMPANYTYPE"));
		map.addValue("FIANANCE_CHARGES", rs.getObject("FIANANCE_CHARGES"));
		map.addValue("FILENO", rs.getObject("FILENO"));
		map.addValue("NO_OF_PDCS", rs.getObject("NO_OF_PDCS"));
		map.addValue("LIFEINSURANCE", rs.getObject("LIFEINSURANCE"));
		map.addValue("SHORTRECEIVED", rs.getObject("SHORTRECEIVED"));
		map.addValue("IN_FAVOUR_OFF", rs.getObject("IN_FAVOUR_OFF"));
		map.addValue("MKTGID", rs.getObject("MKTGID"));
		map.addValue("PRE_EMI_INT_500071", rs.getObject("PRE_EMI_INT_500071"));
		map.addValue("LOAN_PURPOSE_DTL", rs.getObject("LOAN_PURPOSE_DTL"));
		map.addValue("LOAN_PURPOSE_DESC", rs.getObject("LOAN_PURPOSE_DESC"));
		map.addValue("LOGIN_FEES", rs.getObject("LOGIN_FEES"));
		map.addValue("VC_REFERRAL_CD", rs.getObject("VC_REFERRAL_CD"));
		map.addValue("VC_REFERRAL_NAME", rs.getObject("VC_REFERRAL_NAME"));
		map.addValue("PROC_FEES2", rs.getObject("PROC_FEES2"));
		map.addValue("INSTRUMENT_TYPE", rs.getObject("INSTRUMENT_TYPE"));
		map.addValue("LAN_BARCODE", rs.getObject("LAN_BARCODE"));
		map.addValue("INTSTART_DATE_REGULAR", rs.getObject("INTSTART_DATE_REGULAR"));
		map.addValue("BPI_RECEIVABLE", rs.getObject("BPI_RECEIVABLE"));
		map.addValue("BPI_PAYABLE", rs.getObject("BPI_PAYABLE"));
		map.addValue("OPEN_FACILITY_FLAG", rs.getObject("OPEN_FACILITY_FLAG"));
		map.addValue("BATCH_ID", rs.getObject("BATCH_ID"));

		return map;

	}

	private static MapSqlParameterSource getLoanVoucherMap(ResultSet rs,  Date appDate) throws Exception {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("MODULEID", rs.getObject("MODULEID"));
		map.addValue("STAGEID", rs.getObject("STAGEID"));
		map.addValue("LEA_VOUCHERID", rs.getObject("LEA_VOUCHERID"));
		map.addValue("FA_VOUCHERID", rs.getObject("FA_VOUCHERID"));
		map.addValue("VOUCHERTYPE", rs.getObject("VOUCHERTYPE"));
		map.addValue("VOUCHERDATE", rs.getObject("VOUCHERDATE"));
		map.addValue("VALUEDATE", rs.getObject("VALUEDATE"));
		map.addValue("BRANCHID", rs.getObject("BRANCHID"));
		map.addValue("BRANCH_CODE", rs.getObject("BRANCH_CODE"));
		map.addValue("BRANCHDESC", rs.getObject("BRANCHDESC"));
		map.addValue("BUSINESS_AREA", rs.getObject("BUSINESS_AREA"));
		map.addValue("PROFIT_CENTRE", rs.getObject("PROFIT_CENTRE"));
		map.addValue("PRODUCT_FLAG", rs.getObject("PRODUCT_FLAG"));
		map.addValue("SCHEMEID", rs.getObject("SCHEMEID"));
		map.addValue("SCHEMEDESC", rs.getObject("SCHEMEDESC"));
		map.addValue("ASSIGNMENT", rs.getObject("ASSIGNMENT"));
		map.addValue("AGREEMENTID", rs.getObject("AGREEMENTID"));
		map.addValue("AGREEMENTNO", rs.getObject("AGREEMENTNO"));
		map.addValue("AGREEMENTDATE", rs.getObject("AGREEMENTDATE"));
		map.addValue("DISBURSALDATE", rs.getObject("DISBURSALDATE"));
		map.addValue("LOAN_STATUS", rs.getObject("LOAN_STATUS"));
		map.addValue("NPA_STAGEID", rs.getObject("NPA_STAGEID"));
		map.addValue("FINNONE_GLID", rs.getObject("FINNONE_GLID"));
		map.addValue("GROUPGLDESC", rs.getObject("GROUPGLDESC"));
		map.addValue("SAPGL_CODE", rs.getObject("SAPGL_CODE"));
		map.addValue("COST_CENTRE", rs.getObject("COST_CENTRE"));
		map.addValue("DRAMT", rs.getObject("DRAMT"));
		map.addValue("CRAMT", rs.getObject("CRAMT"));
		map.addValue("DRCR_FLAG", rs.getObject("DRCR_FLAG"));
		map.addValue("DRCR_AMT", rs.getObject("DRCR_AMT"));
		map.addValue("NARRATION", rs.getObject("NARRATION"));
		map.addValue("CHEQUEID", rs.getObject("CHEQUEID"));
		map.addValue("BUSINESSDATE", appDate);
		map.addValue("PROCESSDATE", rs.getObject("PROCESSDATE"));
		map.addValue("SEGMENT", rs.getObject("SEGMENT"));
		map.addValue("PROCESSED_FLAG","N");

		return map;

	}

	private static MapSqlParameterSource getLoanWiseChargeDetailMap(ResultSet rs,  Date appDate) throws Exception {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("AGREEMENTID", rs.getObject("AGREEMENTID"));
		map.addValue("TXNADVICEID", rs.getObject("TXNADVICEID"));
		map.addValue("CHARGEID", rs.getObject("CHARGEID"));
		map.addValue("CHARGECODEID", rs.getObject("CHARGECODEID"));
		map.addValue("CHARGEDESC", rs.getObject("CHARGEDESC"));
		map.addValue("CHARGEAMT", rs.getObject("CHARGEAMT"));
		map.addValue("STATUS", rs.getObject("STATUS"));
		map.addValue("AMTINPROCESS", rs.getObject("AMTINPROCESS"));
		map.addValue("TXNADJUSTEDAMT", rs.getObject("TXNADJUSTEDAMT"));
		map.addValue("ADVICEAMT", rs.getObject("ADVICEAMT"));
		map.addValue("ADVICEDATE", rs.getObject("ADVICEDATE"));
		map.addValue("BUSINESSDATE", appDate);
		map.addValue("PROCESSDATE", rs.getObject("PROCESSDATE"));
		map.addValue("PROCESSED_FLAG", "N");
		map.addValue("SEGMENT", rs.getObject("SEGMENT"));
		map.addValue("BATCH_ID", rs.getObject("BATCH_ID"));

		return map;

	}

	private static MapSqlParameterSource getLoanWiseRepayScheduleDetailMap(ResultSet rs,  Date appDate) throws Exception {
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("AGREEMENTNO", rs.getObject("AGREEMENTNO"));
		map.addValue("AGREEMENTID", rs.getObject("AGREEMENTID"));
		map.addValue("PROPINSTLID", rs.getObject("PROPINSTLID"));
		map.addValue("EMI_NO", rs.getObject("EMI_NO"));
		map.addValue("DUEDATE", rs.getObject("DUEDATE"));
		map.addValue("OPENING_PRINCIPAL", rs.getObject("OPENING_PRINCIPAL"));
		map.addValue("INSTALMENT_AMT", rs.getObject("INSTALMENT_AMT"));
		map.addValue("PRINCIPAL_AMT", rs.getObject("PRINCIPAL_AMT"));
		map.addValue("INTEREST_AMT", rs.getObject("INTEREST_AMT"));
		map.addValue("CLOSING_PRINCIPAL", rs.getObject("CLOSING_PRINCIPAL"));
		map.addValue("INSTAL_TYPE", rs.getObject("INSTAL_TYPE"));
		map.addValue("TOTAL_AMOUNT_DUE", rs.getObject("TOTAL_AMOUNT_DUE"));
		map.addValue("DROPLINE_LIMIT", rs.getObject("DROPLINE_LIMIT"));
		map.addValue("ACT_AVAILABLE_LIMIT", rs.getObject("ACT_AVAILABLE_LIMIT"));
		map.addValue("ACT_UTILISATION_LIMIT", rs.getObject("ACT_UTILISATION_LIMIT"));
		map.addValue("EMI_HOLIDAY", rs.getObject("EMI_HOLIDAY"));
		map.addValue("BUSINESSDATE", appDate);

		return map;

	}

	private static MapSqlParameterSource getNoceligibleLoans(ResultSet rs,  Date appDate) throws Exception {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("AGREEMENTNO", rs.getObject("AGREEMENTNO"));
		map.addValue("LOANACCTNUM", rs.getObject("LOANACCTNUM"));
		map.addValue("PRODUCTFLAG", rs.getObject("PRODUCTFLAG"));
		map.addValue("EMAIL", rs.getObject("EMAIL"));
		map.addValue("BUSINESSDATE", appDate);
		map.addValue("PROCESS_DATE", rs.getObject("PROCESS_DATE"));
		map.addValue("PROCESSED_FLAG","N");

		return map;

	}

	private static MapSqlParameterSource getOpenEcsDetailsMap(ResultSet rs,  Date appDate) throws Exception {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("CUSTOMERID", rs.getObject("CUSTOMERID"));
		map.addValue("ECS_ID", rs.getObject("ECS_ID"));
		map.addValue("BANK_ID", rs.getObject("BANK_ID"));
		map.addValue("BANK_NAME", rs.getObject("BANK_NAME"));
		map.addValue("BANKBRANCHID", rs.getObject("BANKBRANCHID"));
		map.addValue("BANKID", rs.getObject("BANKID"));
		map.addValue("BANK_BRANCH_NAME", rs.getObject("BANK_BRANCH_NAME"));
		map.addValue("CITY", rs.getObject("CITY"));
		map.addValue("ACCTYPE", rs.getObject("ACCTYPE"));
		map.addValue("ACCNO", rs.getObject("ACCNO"));
		map.addValue("MAXLIMIT", rs.getObject("MAXLIMIT"));
		map.addValue("BALLIMIT", rs.getObject("BALLIMIT"));
		map.addValue("UTIL_LIMIT", rs.getObject("UTIL_LIMIT"));
		map.addValue("VALID_LIMIT", rs.getObject("VALID_LIMIT"));
		map.addValue("REPAY_MODE", rs.getObject("REPAY_MODE"));
		map.addValue("MICRCODE", rs.getObject("MICRCODE"));
		map.addValue("ACTIVE_FLAG", rs.getObject("ACTIVE_FLAG"));
		map.addValue("CITYID", rs.getObject("CITYID"));
		map.addValue("SEGMENTS", rs.getObject("SEGMENTS"));
		map.addValue("BUSINESSDATE", appDate);
		map.addValue("PROCESS_DATE", rs.getObject("PROCESS_DATE"));
		map.addValue("BATCH_ID", rs.getObject("BATCH_ID"));

		return map;

	}

	private static MapSqlParameterSource getPrePaymentDetailMap(ResultSet rs,  Date appDate) throws Exception {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("APPLID", rs.getObject("APPLID"));
		map.addValue("AGREEMENTNO", rs.getObject("AGREEMENTNO"));
		map.addValue("PREPAYMENTID", rs.getObject("PREPAYMENTID"));
		map.addValue("PREPAYMENT_RATE", rs.getObject("PREPAYMENT_RATE"));
		map.addValue("PREPAYMENT_TYPE", rs.getObject("PREPAYMENT_TYPE"));
		map.addValue("PREPAYMENT_PENALTY_DUE", rs.getObject("PREPAYMENT_PENALTY_DUE"));
		map.addValue("PREPAYMENT_PENALTY_PAID", rs.getObject("PREPAYMENT_PENALTY_PAID"));
		map.addValue("PREPAYMENT_AMT", rs.getObject("PREPAYMENT_AMT"));
		map.addValue("BUSINESSDATE", appDate);
		map.addValue("PROCESSED_FLAG", "N");
		map.addValue("PROCESS_DATE", rs.getObject("PROCESS_DATE"));
		map.addValue("SEGMENTS", rs.getObject("SEGMENTS"));
		map.addValue("PREPAYMENT_DATE", rs.getObject("PREPAYMENT_DATE"));
		map.addValue("BUSINESS_YEAR", rs.getObject("BUSINESS_YEAR"));

		return map;

	}

	private static MapSqlParameterSource getPresentationDetailsMap(ResultSet rs,  Date appDate) throws Exception {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("AGREEMENTNO", rs.getObject("AGREEMENTNO"));
		map.addValue("EMI", rs.getObject("EMI"));
		map.addValue("REPAY_TYPE", rs.getObject("REPAY_TYPE"));
		map.addValue("DEPOSITED_DATE", rs.getObject("DEPOSITED_DATE"));
		map.addValue("CREDIT_STATUS", rs.getObject("CREDIT_STATUS"));
		map.addValue("RETURN_CODE", rs.getObject("RETURN_CODE"));
		map.addValue("RETURN_REASON", rs.getObject("RETURN_REASON"));
		map.addValue("REMARKS", rs.getObject("REMARKS"));
		map.addValue("SEGMENTS", rs.getObject("SEGMENTS"));
		map.addValue("PROCESSDATE", rs.getObject("PROCESSDATE"));
		map.addValue("BUSINESSDATE", appDate);
		map.addValue("EMI_NO", rs.getObject("EMI_NO"));
		map.addValue("CUSTOMER_BANK_NAME", rs.getObject("CUSTOMER_BANK_NAME"));
		map.addValue("BOM_BOUNCE_BUCKET", rs.getObject("BOM_BOUNCE_BUCKET"));
		map.addValue("MICR_CODE", rs.getObject("MICR_CODE"));
		map.addValue("STATUS_UPDT_DATE", rs.getObject("STATUS_UPDT_DATE"));
		map.addValue("CUST_BANK_AC_NO", rs.getObject("CUST_BANK_AC_NO"));
		map.addValue("CUSTOMER_BANK_BRANCH", rs.getObject("CUSTOMER_BANK_BRANCH"));
		map.addValue("CHEQUESNO", rs.getObject("CHEQUESNO"));
		map.addValue("CHEQUEDATE", rs.getObject("CHEQUEDATE"));
		map.addValue("FEMI_FLAG", rs.getObject("FEMI_FLAG"));
		map.addValue("HOLD_IGNORE_CODE", rs.getObject("HOLD_IGNORE_CODE"));
		map.addValue("HOLD_IGNORE_REASON", rs.getObject("HOLD_IGNORE_REASON"));
		map.addValue("DEST_ACC_HOLDER", rs.getObject("DEST_ACC_HOLDER"));
		map.addValue("PDCID", rs.getObject("PDCID"));
		map.addValue("BBRANCHID", rs.getObject("BBRANCHID"));

		return map;

	}

	private static MapSqlParameterSource getPropertyDetailMap(ResultSet rs,  Date appDate) throws Exception {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("PROPERTYID", rs.getObject("PROPERTYID"));
		map.addValue("APPLICATIONID", rs.getObject("APPLICATIONID"));

		map.addValue("ADDRESS1", rs.getObject("ADDRESS1"));
		map.addValue("ADDRESS2", rs.getObject("ADDRESS2"));
		map.addValue("ADDRESS3", rs.getObject("ADDRESS3"));
		map.addValue("CITY", rs.getObject("CITY"));
		map.addValue("STATE", rs.getObject("STATE"));
		map.addValue("PRODUCTFLAG", rs.getObject("PRODUCTFLAG"));
		map.addValue("BUSINESSDATE", appDate);
		map.addValue("PROPERTY_TYPE", rs.getObject("PROPERTY_TYPE"));
		map.addValue("PROPERTY_DESC", rs.getObject("PROPERTY_DESC"));
		map.addValue("PROPERTY_VALUE", rs.getObject("PROPERTY_VALUE"));
		map.addValue("ZIPCODE", rs.getObject("ZIPCODE"));
		map.addValue("PROCESSFLAG", rs.getObject("PROCESSFLAG"));
		map.addValue("PROCESS_DATE", rs.getObject("PROCESS_DATE"));
		map.addValue("BATCH_ID", rs.getObject("BATCH_ID"));

		return map;

	}

	private static MapSqlParameterSource getReschDetailMap(ResultSet rs,  Date appDate) throws Exception {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("APPLID", rs.getObject("APPLID"));
		map.addValue("AGREEMENTNO", rs.getObject("AGREEMENTNO"));
		map.addValue("DISB_STATUS", rs.getObject("DISB_STATUS"));
		map.addValue("GROSS_TENURE", rs.getObject("GROSS_TENURE"));
		map.addValue("NET_TENURE", rs.getObject("NET_TENURE"));
		map.addValue("MATURITYDATE", rs.getObject("MATURITYDATE"));
		map.addValue("EXPIRYDATE", rs.getObject("EXPIRYDATE"));
		map.addValue("EMI", rs.getObject("EMI"));
		map.addValue("REPAYMENT_MODE", rs.getObject("REPAYMENT_MODE"));
		map.addValue("PRODUCTFLAG", rs.getObject("PRODUCTFLAG"));
		map.addValue("BUSINESSDATE", appDate);
		map.addValue("PROCESSED_FLAG", "N");
		map.addValue("PROCESS_DATE", rs.getObject("PROCESS_DATE"));
		map.addValue("SEGMENTS", rs.getObject("SEGMENTS"));
		map.addValue("FREQ", rs.getObject("FREQ"));
		map.addValue("LOAN_STATUS", rs.getObject("LOAN_STATUS"));
		map.addValue("CLOSUREDATE", rs.getObject("CLOSUREDATE"));
		map.addValue("CUST_ACCT_NO", rs.getObject("CUST_ACCT_NO"));
		map.addValue("BANKNAME", rs.getObject("BANKNAME"));
		map.addValue("MICRCODE", rs.getObject("MICRCODE"));
		map.addValue("CUST_BANK_BRANCH", rs.getObject("CUST_BANK_BRANCH"));
		map.addValue("CUST_BANK_CITY", rs.getObject("CUST_BANK_CITY"));
		map.addValue("BUSINESS_YEAR", rs.getObject("BUSINESS_YEAR"));
		map.addValue("PDCID", rs.getObject("PDCID"));
		map.addValue("PCFLAG", rs.getObject("PCFLAG"));
		map.addValue("TIE_UP", rs.getObject("TIE_UP"));
		map.addValue("MARGIN", rs.getObject("MARGIN"));
		map.addValue("SPECIALMARGIN", rs.getObject("SPECIALMARGIN"));
		map.addValue("FIXEDTENOR", rs.getObject("FIXEDTENOR"));
		map.addValue("CEEFFECTIVEDATE", rs.getObject("CEEFFECTIVEDATE"));
		map.addValue("EFF_RATE", rs.getObject("EFF_RATE"));
		map.addValue("PLRRATE", rs.getObject("PLRRATE"));
		map.addValue("TIE_UP_WITH", rs.getObject("TIE_UP_WITH"));
		map.addValue("DATE_OF_CLOSURE", rs.getObject("DATE_OF_CLOSURE"));
		map.addValue("PDCMS_SEQ_GENERATED_DATE", rs.getObject("PDCMS_SEQ_GENERATED_DATE"));
		map.addValue("INSTRUMENT_DATA_ENTRY_DATE", rs.getObject("INSTRUMENT_DATA_ENTRY_DATE"));
		map.addValue("PAYMENT_AUTHORIZATION_DATE", rs.getObject("PAYMENT_AUTHORIZATION_DATE"));

		return map;

	}

	private static MapSqlParameterSource getSoaEmailDetailMap(ResultSet rs,  Date appDate) throws Exception {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("AGREEMENTNO", rs.getObject("AGREEMENTNO"));
		map.addValue("CUSTOMERID", rs.getObject("CUSTOMERID"));
		map.addValue("EMAILID", rs.getObject("EMAILID"));
		map.addValue("AGREEMENTID", rs.getObject("AGREEMENTID"));
		map.addValue("PROCESSID", rs.getObject("PROCESSID"));
		map.addValue("GENERATION_DATE", rs.getObject("GENERATION_DATE"));
		map.addValue("PROCESSED", rs.getObject("PROCESSED"));
		map.addValue("PRODUCTFLAG", rs.getObject("PRODUCTFLAG"));
		map.addValue("GROUPID", rs.getObject("GROUPID"));
		map.addValue("TOTAL_LAN", rs.getObject("TOTAL_LAN"));
		map.addValue("TOTAL_CLOSED_LAN", rs.getObject("TOTAL_CLOSED_LAN"));
		map.addValue("TOTAL_ACTIVE_LAN", rs.getObject("TOTAL_ACTIVE_LAN"));
		map.addValue("SWIPE_FLAG", rs.getObject("SWIPE_FLAG"));
		map.addValue("EMI_CARD_NO", rs.getObject("EMI_CARD_NO"));
		map.addValue("DISBURSEMENT_DATE", rs.getObject("DISBURSEMENT_DATE"));
		map.addValue("SUPPLIERID", rs.getObject("SUPPLIERID"));
		map.addValue("SUPPLIERDESC", rs.getObject("SUPPLIERDESC"));
		map.addValue("AMT_FIN", rs.getObject("AMT_FIN"));
		map.addValue("EMI", rs.getObject("EMI"));
		map.addValue("NEXT_EMI_DUE_DATE", rs.getObject("NEXT_EMI_DUE_DATE"));
		map.addValue("CHEQUE_BOUNCE_CHARGE", rs.getObject("CHEQUE_BOUNCE_CHARGE"));

		return map;

	}

	private static MapSqlParameterSource getSubQDisbDetailMap(ResultSet rs,  Date appDate) throws Exception {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("AGREEMENTDATE", rs.getObject("AGREEMENTDATE"));
		map.addValue("APPLID", rs.getObject("APPLID"));
		map.addValue("AGREEMENTNO", rs.getObject("AGREEMENTNO"));
		map.addValue("DISBURSEMENTNO", rs.getObject("DISBURSEMENTNO"));
		map.addValue("DISBURSEMENTDATE", rs.getObject("DISBURSEMENTDATE"));
		map.addValue("PARENT_AGREEMENTNO", rs.getObject("PARENT_AGREEMENTNO"));
		map.addValue("AMTFIN", rs.getObject("AMTFIN"));
		map.addValue("NET_AMTFIN", rs.getObject("NET_AMTFIN"));
		map.addValue("DISBURSEDAMT", rs.getObject("DISBURSEDAMT"));
		map.addValue("DISB_STATUS", rs.getObject("DISB_STATUS"));
		map.addValue("FIRST_DUE_DATE", rs.getObject("FIRST_DUE_DATE"));
		map.addValue("GROSS_TENURE", rs.getObject("GROSS_TENURE"));
		map.addValue("NET_TENURE", rs.getObject("NET_TENURE"));
		map.addValue("MATURITYDATE", rs.getObject("MATURITYDATE"));
		map.addValue("EXPIRYDATE", rs.getObject("EXPIRYDATE"));
		map.addValue("NO_OF_ADV_INSTL", rs.getObject("NO_OF_ADV_INSTL"));
		map.addValue("ADV_EMI_AMT", rs.getObject("ADV_EMI_AMT"));
		map.addValue("EMI", rs.getObject("EMI"));
		map.addValue("REPAYMENT_MODE", rs.getObject("REPAYMENT_MODE"));
		map.addValue("PRODUCTFLAG", rs.getObject("PRODUCTFLAG"));
		map.addValue("PROMOTIONID", rs.getObject("PROMOTIONID"));
		map.addValue("ICICI_LOMBARD", rs.getObject("ICICI_LOMBARD"));
		map.addValue("BAGIC", rs.getObject("BAGIC"));
		map.addValue("BALIC_CHARGES", rs.getObject("BALIC_CHARGES"));
		map.addValue("BUSINESSDATE", appDate);
		map.addValue("PROCESSED_FLAG", "N");
		map.addValue("PROCESS_DATE", rs.getObject("PROCESS_DATE"));
		map.addValue("SEGMENTS", rs.getObject("SEGMENTS"));
		map.addValue("FEE", rs.getObject("FEE"));
		map.addValue("DEALER_SUBV", rs.getObject("DEALER_SUBV"));
		map.addValue("MANU_SUBV_DED", rs.getObject("MANU_SUBV_DED"));
		map.addValue("MANU_SUBV_NDED", rs.getObject("MANU_SUBV_NDED"));
		map.addValue("PREEMI", rs.getObject("PREEMI"));
		map.addValue("EXISTING_LANNO", rs.getObject("EXISTING_LANNO"));
		map.addValue("MORTGAGE_FEE", rs.getObject("MORTGAGE_FEE"));
		map.addValue("COMMITMENT_FEE", rs.getObject("COMMITMENT_FEE"));
		map.addValue("PROCESSING_FEE", rs.getObject("PROCESSING_FEE"));
		map.addValue("PRE_EMI_RECEIVABLE", rs.getObject("PRE_EMI_RECEIVABLE"));
		map.addValue("INSURANCE", rs.getObject("INSURANCE"));
		map.addValue("PAYMENTMODE", rs.getObject("PAYMENTMODE"));
		map.addValue("FREQ", rs.getObject("FREQ"));
		map.addValue("CHEQUENUM", rs.getObject("CHEQUENUM"));
		map.addValue("BATCH_ID", rs.getObject("BATCH_ID"));

		return map;

	}

	private static MapSqlParameterSource getWriteOffDetailsMap(ResultSet rs,  Date appDate) throws Exception {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("APPLID", rs.getObject("APPLID"));
		map.addValue("AGREEMENTNO", rs.getObject("AGREEMENTNO"));
		map.addValue("CHARGEOFF_DT", rs.getObject("CHARGEOFF_DT"));
		map.addValue("CHARGEOFF_FLG", rs.getObject("CHARGEOFF_FLG"));
		map.addValue("CHARGEOFF_REASON", rs.getObject("CHARGEOFF_REASON"));
		map.addValue("SETTLEMENT_LOSS", rs.getObject("SETTLEMENT_LOSS"));
		map.addValue("GROSS_WRITEOFF_AMT", rs.getObject("GROSS_WRITEOFF_AMT"));
		map.addValue("NET_WRITEOFF_AMT", rs.getObject("NET_WRITEOFF_AMT"));
		map.addValue("BUSINESSDATE", appDate);
		map.addValue("PROCESSED_FLAG", "N");
		map.addValue("PROCESS_DATE", rs.getObject("PROCESS_DATE"));
		map.addValue("SEGMENTS", rs.getObject("SEGMENTS"));
		map.addValue("RECEIPT_ON_CHARGEOFF", rs.getObject("RECEIPT_ON_CHARGEOFF"));
		map.addValue("BUSINESS_YEAR", rs.getObject("BUSINESS_YEAR"));

		return map;

	}
}
