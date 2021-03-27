package com.pennant.pff.service.RateChangeupload;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.zkoss.util.media.Media;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.EntityDAO;
import com.pennant.backend.dao.finance.RateChangeUploadDAO;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.pff.model.ratechangeupload.RateChangeUpload;
import com.pennant.pff.model.ratechangeupload.RateChangeUploadHeader;
import com.pennanttech.dataengine.DataEngineImport;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.jdbc.BasicDao;

public class RateChangeUploadService extends BasicDao<RateChangeUpload> {

	private DataSource dataSource;
	private RateChangeUploadProcess rateChangeUploadProcess;
	private EntityDAO entityDAO;
	private RateChangeUploadDAO rateChangeUploadDAO;

	public RateChangeUploadService() {
		super();
	}

	public void importData(RateChangeUploadHeader rateChangeUploadHeader) throws Exception {

		long userId = rateChangeUploadHeader.getUserId();
		DataEngineStatus des = rateChangeUploadHeader.getDeStatus();
		File file = rateChangeUploadHeader.getFile();
		Media media = rateChangeUploadHeader.getMedia();
		String configName = des.getName();
		String name = "";
		StringBuilder remarks = new StringBuilder();
		Map<String, Object> parametersMap = new HashMap<>();
		String entityCode = rateChangeUploadHeader.getEntityCode();

		if (file != null) {
			name = file.getName();
		} else if (media != null) {
			name = media.getName();
		}

		des.reset();
		des.setFileName(name);

		boolean exists = rateChangeUploadDAO.isFileExists(name);
		long id;
		if (!exists) {
			id = rateChangeUploadDAO.saveHeader(name, entityCode);
			parametersMap.put("BATCHID", id);
			rateChangeUploadHeader.setId(id);
		} else {
			remarks.append("Selected file already processed");
			des.setStatus(ExecutionStatus.F.name());
			des.setEndTime(new Timestamp(System.currentTimeMillis()));
			des.setRemarks(remarks.toString());
			des.setName(configName);
			throw new Exception("Selected file already processed");
		}

		des.setRemarks("initiated RateChange upload  file [ " + name + " ] processing..");
		Date appDate = SysParamUtil.getAppDate();
		DataEngineImport dataEngine = new DataEngineImport(dataSource, userId, App.DATABASE.name(), true, appDate, des);
		dataEngine.setFile(file);
		dataEngine.setMedia(media);
		dataEngine.setParameterMap(parametersMap);
		dataEngine.setValueDate(appDate);
		dataEngine.importData(configName);

		do {
			if ("S".equals(des.getStatus()) || "F".equals(des.getStatus())) {
				List<RateChangeUpload> rateChangeUpload = rateChangeUploadDAO.getRateChangeUploadDetails(id);
				rateChangeUploadHeader.setRateChangeUpload(rateChangeUpload);
				rateChangeUploadProcess.process(rateChangeUploadHeader);
				break;
			}
		} while ("S".equals(des.getStatus()) || "F".equals(des.getStatus()));

	}

	public List<Entity> getEntites() {
		return entityDAO.getEntites();
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setRateChangeUploadProcess(RateChangeUploadProcess rateChangeUploadProcess) {
		this.rateChangeUploadProcess = rateChangeUploadProcess;
	}

	public EntityDAO getEntityDAO() {
		return entityDAO;
	}

	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}

	public RateChangeUploadDAO getRateChangeUploadDAO() {
		return rateChangeUploadDAO;
	}

	public void setRateChangeUploadDAO(RateChangeUploadDAO rateChangeUploadDAO) {
		this.rateChangeUploadDAO = rateChangeUploadDAO;
	}

	public RateChangeUploadProcess getRateChangeUploadProcess() {
		return rateChangeUploadProcess;
	}

}
