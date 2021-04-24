package com.pennanttech.pff.external.gl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.pennant.app.util.SysParamUtil;
import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;

public class VocherDownloadServiceImpl implements VocherDownloadService {

	private DataSource dataSource;

	@Override
	public void downloadVocher(long userId, String userName, Date postDate, Date toDate) {
		DataEngineExport dataEngine = null;
		dataEngine = new DataEngineExport(dataSource, userId, App.DATABASE.name(), true,
				SysParamUtil.getAppValueDate());

		DataEngineStatus status = genetare(dataEngine, userName, postDate, toDate);

		if ("F".equals(status.getStatus())) {
			if (status.getTotalRecords() == 0) {
				throw new AppException(String.format("Postings not avilable for the vocher dates between %s and %s",
						DateUtil.format(postDate, DateFormat.SHORT_DATE),
						DateUtil.format(toDate, DateFormat.SHORT_DATE)));
			} else {
				throw new AppException(status.getRemarks());
			}
		}
	}

	protected DataEngineStatus genetare(DataEngineExport dataEngine, String userName, Date postDate, Date toDate) {
		Map<String, Object> parameterMap = new HashMap<>();
		Map<String, Object> filterMap = new HashMap<>();

		parameterMap.put("FROM_DATE", DateUtil.format(postDate, "yyyyMMdd"));
		parameterMap.put("TO_DATE", DateUtil.format(toDate, "yyyyMMdd"));
		parameterMap.put("VOCHER_DATE",
				DateUtil.format(postDate, "yyyyMMdd") + "_" + DateUtil.format(toDate, "yyyyMMdd"));
		parameterMap.put("ORDER_BY_CLAUSE", "order by PostDate, LinkedTranId, TransOrder");

		filterMap.put("POSTDATE", postDate);
		filterMap.put("FROM_DATE", postDate);
		filterMap.put("TO_DATE", toDate);
		dataEngine.setParameterMap(parameterMap);
		dataEngine.setFilterMap(filterMap);
		dataEngine.setUserName(userName);
		dataEngine.setValueDate(SysParamUtil.getAppValueDate());

		try {
			return dataEngine.exportData("GL_VOCHER_DOWNLOAD_TALLY");
		} catch (Exception e) {
			throw new InterfaceException("9999", "Vocher Download", e);
		}
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
