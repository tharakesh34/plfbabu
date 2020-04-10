package com.pennanttech.pff.external.disbursement;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.disbursement.PaymentType;
import com.pennanttech.pff.core.disbursement.model.DisbursementRequest;

public class OfflineDisbursementImpl implements OfflineDisbursement {
	private DataSource dataSource;

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

		try {

			export.setValueDate(request.getAppValueDate());
			export.setFilterMap(filterMap);
			export.setParameterMap(parameterMap);

			export.exportData(configName, false);

		} catch (Exception e) {
			throw new AppException(e.getMessage());
		}

		return export.getDataEngineStatus();
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}