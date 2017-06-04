package com.pennanttech.bajaj.process.datamart;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennanttech.dataengine.DatabaseDataEngine;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.Literal;

public class ApplicationDetailsDataMart extends DatabaseDataEngine implements Runnable {
	private static final Logger logger = Logger.getLogger(ApplicationDetailsDataMart.class);

	public ApplicationDetailsDataMart(DataSource dataSource, long userId, Date valueDate, Date appDate) {
		super(dataSource, App.DATABASE.name(), userId, true, valueDate);
	}

	@Override
	public void run() {
		processData();
	}

	@Override
	protected void processData() {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource parmMap;
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT * from DM_APPLICATION_DETAILS_VIEW ");

		parmMap = new MapSqlParameterSource();

		jdbcTemplate.query(sql.toString(), parmMap, new ResultSetExtractor<Long>() {
			MapSqlParameterSource map = null;

			@Override
			public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
				String[] filterFields = new String[1];
				filterFields[0] = "APPLID";
				while (rs.next()) {

					try {
						map = mapData(rs);
						saveOrUpdate(map, "DM_APPLICATION_DETAILS", destinationJdbcTemplate, filterFields);
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						String keyId = rs.getString("APPLID");
						saveBatchLog(keyId, "F", e.getMessage());
					} finally {
						map = null;
					}
				}
				return totalRecords;
			}
		});
		logger.debug(Literal.LEAVING);
	}

	@Override
	protected MapSqlParameterSource mapData(ResultSet rs) throws Exception {
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
		map.addValue("BUSINESSDATE", rs.getObject("BUSINESSDATE"));
		map.addValue("PROCESSED_FLAG", rs.getObject("PROCESSED_FLAG"));
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
}
