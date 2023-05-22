package com.pennanttech.pff.external.disbursement;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennant.app.constants.DataEngineConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.disbursement.PaymentType;
import com.pennanttech.pff.core.disbursement.model.DisbursementRequest;

public class OfflineDisbursementImpl implements OfflineDisbursement {
	private static Logger logger = LogManager.getLogger(OfflineDisbursementImpl.class);

	private DataSource dataSource;
	private JdbcOperations jdbcOperations;

	@Override
	public DataEngineStatus downloadFile(String configName, DisbursementRequest request, PaymentType disbursementType) {
		DataEngineExport export = new DataEngineExport(dataSource, request.getUserId(), App.DATABASE.name(), true,
				request.getAppValueDate());

		LoggedInUser userDetails = request.getLoggedInUser();

		Map<String, Object> filterMap = new HashMap<>();
		filterMap.put("HEADER_ID", request.getHeaderId());
		filterMap.put("DISBURSEMENT_TYPE", disbursementType.name());

		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("PRODUCT_CODE", request.getFinType());
		parameterMap.put("PAYMENT_TYPE", disbursementType.name());
		parameterMap.put("PARTNER_BANK_CODE", request.getPartnerBankCode());
		parameterMap.put("HEADER_ID", request.getHeaderId());

		parameterMap.put("USER_ID", request.getUserId());

		if (userDetails != null) {
			parameterMap.put("USER_NAME", userDetails.getUserName());
			parameterMap.put("USER_DEPT_CODE", userDetails.getDepartmentCode());
			parameterMap.put("USER_BRANCH_CODE", userDetails.getBranchCode());
			parameterMap.put("USER_BRANCH_NAME", userDetails.getBranchName());
		} else {
			parameterMap.put("USER_NAME", "");
			parameterMap.put("USER_DEPT_CODE", "");
			parameterMap.put("USER_BRANCH_CODE", "");
			parameterMap.put("USER_BRANCH_NAME", "");
		}

		parameterMap.put("CLIENT_CODE", request.getFileNamePrefix());
		parameterMap.put("SEQ_LPAD_SIZE", 3);
		parameterMap.put("SEQ_LPAD_VALUE", "0");

		parameterMap.put("STOP_POST_EVENTS", "Y");
		parameterMap.put("dd_MM_yyyy", DateUtil.format(SysParamUtil.getAppDate(), "dd_MM_yyyy"));

		try {

			String configByPartnerBnak = getConfigByPartnerBnak(disbursementType.name(), request.getPartnerBankId());
			if (configByPartnerBnak != null) {
				configName = configByPartnerBnak;
			}

			export.setValueDate(request.getAppValueDate());
			export.setFilterMap(filterMap);
			export.setParameterMap(parameterMap);

			export.exportData(configName, false);

		} catch (Exception e) {
			throw new AppException(e.getMessage());
		}

		return export.getDataEngineStatus();
	}

	public String getConfigByPartnerBnak(String paymentType, long partnerBankId) {
		String sql = "Select Config_Name from Partnerbanks_Data_Engine Where PayMode = ? and PartnerBankId= ? and Type = ? and RequestType = ?";

		try {
			return jdbcOperations.queryForObject(sql, String.class, paymentType, partnerBankId,
					DataEngineConstants.DISBURSEMENT, DataEngineConstants.EXPORT);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}
		return null;
	}

	public void setDataSource(DataSource dataSource) {
		jdbcOperations = new NamedParameterJdbcTemplate(dataSource).getJdbcOperations();
		this.dataSource = dataSource;
	}

}
