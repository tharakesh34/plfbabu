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
	public void downloadVocher(long userId, String userName, Date postDate) {
		DataEngineExport dataEngine = null;
		dataEngine = new DataEngineExport(dataSource, userId, App.DATABASE.name(), true,
				SysParamUtil.getAppValueDate());

		DataEngineStatus status = genetare(dataEngine, userName, postDate);

		if ("F".equals(status.getStatus())) {
			if (status.getTotalRecords() == 0) {
				throw new AppException("Postings not avilable for the vocher date ",
						DateUtil.format(postDate, DateFormat.LONG_DATE));
			} else {
				throw new AppException(status.getRemarks());
			}
		}
	}

	protected DataEngineStatus genetare(DataEngineExport dataEngine, String userName, Date postDate) {
		Map<String, Object> parameterMap = new HashMap<>();
		Map<String, Object> filterMap = new HashMap<>();

		parameterMap.put("VOCHER_DATE", DateUtil.format(postDate, "YYYYMMdd"));
		parameterMap.put("ORDER_BY_CLAUSE", "order by PostDate, LinkedTranId, TransOrder");

		filterMap.put("POSTDATE", postDate);
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
