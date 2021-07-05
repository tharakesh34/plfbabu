package com.pennant.pff.service.paymentmethodupload;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.media.Media;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.PaymentMethodUploadDAO;
import com.pennant.pff.model.paymentmethodupload.PaymentMethodUpload;
import com.pennant.pff.model.paymentmethodupload.PaymentMethodUploadHeader;
import com.pennanttech.dataengine.DataEngineImport;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class PaymentMethodUploadService extends BasicDao<PaymentMethodUpload> {
	private static final Logger logger = LogManager.getLogger(PaymentMethodUploadService.class);

	private DataSource dataSource;
	private PaymentMethodUploadProcess paymentMethodUploadProcess;
	private PaymentMethodUploadDAO paymentMethodUploadDAO;

	public PaymentMethodUploadService() {
		super();
	}

	public void importData(PaymentMethodUploadHeader header) throws Exception {
		logger.debug(Literal.ENTERING);

		long userId = header.getUserId();
		DataEngineStatus des = header.getDeStatus();
		File file = header.getFile();
		Media media = header.getMedia();
		String configName = des.getName();
		String name = "";
		StringBuilder remarks = new StringBuilder();
		Map<String, Object> parametersMap = new HashMap<>();

		if (file != null) {
			name = file.getName();
		} else if (media != null) {
			name = media.getName();
		}

		des.reset();
		des.setFileName(name);

		boolean exists = paymentMethodUploadDAO.isFileExists(name);
		long id;
		if (!exists) {
			id = paymentMethodUploadDAO.saveHeader(name);
			parametersMap.put("BATCHID", id);
			header.setId(id);
		} else {
			remarks.append("Selected file already processed");
			des.setStatus(ExecutionStatus.F.name());
			des.setEndTime(new Timestamp(System.currentTimeMillis()));
			des.setRemarks(remarks.toString());
			des.setName(configName);
			throw new Exception("Selected file already processed");
		}

		des.setRemarks("Initiated Change Payment upload  file [ " + name + " ] processing..");
		Date appDate = SysParamUtil.getAppDate();
		DataEngineImport dataEngine = new DataEngineImport(dataSource, userId, App.DATABASE.name(), true, appDate, des);
		dataEngine.setFile(file);
		dataEngine.setMedia(media);
		dataEngine.setParameterMap(parametersMap);
		dataEngine.setValueDate(appDate);
		dataEngine.importData(configName);

		do {
			if ("S".equals(des.getStatus()) || "F".equals(des.getStatus())) {
				List<PaymentMethodUpload> changePaymentUpload = paymentMethodUploadDAO
						.getChangePaymentUploadDetails(id);
				header.setPaymentmethodUpload(changePaymentUpload);
				paymentMethodUploadProcess.process(header);
				break;
			}
		} while ("S".equals(des.getStatus()) || "F".equals(des.getStatus()));

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setPaymentMethodUploadDAO(PaymentMethodUploadDAO paymentMethodUploadDAO) {
		this.paymentMethodUploadDAO = paymentMethodUploadDAO;
	}

	public void setPaymentMethodUploadProcess(PaymentMethodUploadProcess paymentMethodUploadProcess) {
		this.paymentMethodUploadProcess = paymentMethodUploadProcess;
	}

}
