package com.pennanttech.pff.external.disbursement;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pennant.backend.util.PennantConstants;
import com.pennanttech.dataengine.Event;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.dataengine.util.DataEngineUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.disbursement.PaymentType;
import com.pennanttech.pff.core.disbursement.model.DisbursementRequest;

public class DisbursementServiceImpl implements DisbursementService {
	private final Logger logger = LogManager.getLogger(DisbursementServiceImpl.class);

	private OfflineDisbursement offlineDisbursement;
	private OfflineDisbursement customOfflineDisbursement;
	private OnlineDisbursement onlineDisbursement;
	@SuppressWarnings("unused")
	private IMPSDisbursement impsDisbursement;

	@Override
	public List<DataEngineStatus> sendReqest(DisbursementRequest request) {
		logger.info(Literal.ENTERING);
		logger.info("Reuested user Id {}", request.getUserId());
		logger.info(Literal.LEAVING);
		if (PennantConstants.ONLINE.equals(request.getDownloadType())) {
			return generateOnlineRequest(request);
		} else {
			return generateRequest(request);
		}
	}

	private List<DataEngineStatus> generateOnlineRequest(DisbursementRequest request) {
		logger.debug(Literal.ENTERING);

		List<DataEngineStatus> statusList = new ArrayList<>();
		DataEngineStatus status = new DataEngineStatus();

		if (onlineDisbursement != null) {
			try {
				status = onlineDisbursement.processRequest(request);
			} catch (Exception e) {
				logger.debug(Literal.EXCEPTION, e);
			}
		}
		statusList.add(status);

		logger.debug(Literal.LEAVING);
		return statusList;
	}

	private List<DataEngineStatus> generateRequest(DisbursementRequest request) {
		List<DataEngineStatus> statusList = new ArrayList<>();
		String configName = request.getDataEngineConfigName();

		if (StringUtils.isBlank(configName)) {
			configName = "DISB_EXPORT_DEFAULT";
		}

		Set<PaymentType> partnerBank = new HashSet<>();
		Set<PaymentType> otherBank = new HashSet<>();

		for (DisbursementRequest disbursment : request.getDisbursementRequests()) {
			partnerBank.add(PaymentType.valueOf(disbursment.getDisbursementType()));
		}

		for (PaymentType paymentType : partnerBank) {
			statusList.add(processDisbursement(request, configName, paymentType));
		}

		for (PaymentType paymentType : otherBank) {
			statusList.add(processDisbursement(request, getOtherBankConfig(paymentType), paymentType));
		}

		return statusList;
	}

	private DataEngineStatus processDisbursement(DisbursementRequest request, String configName,
			PaymentType paymentType) {
		DataEngineStatus dataEngineStatus = null;
		try {

			logger.info("Start processing {} data-engine configuration for the {} payment type...", configName,
					paymentType);
			dataEngineStatus = getOfflineDisbursement().downloadFile(configName, request, paymentType);

			File file = getFile(dataEngineStatus);

			logger.info("Payment instruction with {} file generated successfully and placed in {} location.",
					file.getName(), file.getParent());

			Configuration configuration = dataEngineStatus.getConfiguration();

			EventProperties properties = null;
			Event event = null;
			String protocol = "";

			if (configuration.getPostEvent() != null) {
				String[] postEvents = configuration.getPostEvent().split(",");

				for (String postEvent : postEvents) {
					postEvent = StringUtils.trimToEmpty(postEvent);
					if (Event.SHARE_TO_FTP.name().equals(postEvent)) {
						event = Event.SHARE_TO_FTP;
						protocol = "FTP";
					} else if (Event.SHARE_TO_SFTP.name().equals(postEvent)) {
						event = Event.SHARE_TO_SFTP;
						protocol = "SFTP";
					} else if (Event.SHARE_TO_NETWORK_FOLDER.name().equals(postEvent)) {
						event = Event.SHARE_TO_NETWORK_FOLDER;
						protocol = "Network Folder";
					} else if (Event.MOVE_TO_S3_BUCKET.name().equals(postEvent)) {
						event = Event.MOVE_TO_S3_BUCKET;
						protocol = "Amazon s3";
					}
				}
			}

			if (event != null) {
				properties = getEventProperties(configuration, event);
			}

			if (properties != null) {
				DataEngineUtil.postEvents(event.name(), properties, file);
				logger.info("{} file copied successfully into {}", file.getName(), protocol);
			}

		} catch (Exception e) {
			dataEngineStatus = new DataEngineStatus();
			dataEngineStatus.setStatus("F");
			logger.debug(Literal.EXCEPTION, e);
			throw e;

		}

		dataEngineStatus.getKeyAttributes().put("DISBURSEMENT_TYPE", paymentType.name());
		return dataEngineStatus;
	}

	private EventProperties getEventProperties(Configuration configuration, Event event) {
		Map<String, EventProperties> map = configuration.getEventProperties();

		return map.get(event.name());

	}

	private File getFile(DataEngineStatus status) {
		return new File(status.getConfiguration().getUploadPath().concat(File.separator).concat(status.getFileName()));
	}

	private String getOtherBankConfig(PaymentType disbursementTypes) {
		String configName = null;
		switch (disbursementTypes) {
		case N:
		case R:
		case I:
		case TRANSFER:
			configName = "DISB_OTHER_NEFT_RTGS_EXPORT";
			break;
		case D:
		case C:
			configName = "DISB_OTHER_CHEQUE_DD_EXPORT";
			break;
		default:
			break;
		}

		return configName;
	}

	public void setOfflineDisbursement(OfflineDisbursement offlineDisbursement) {
		this.offlineDisbursement = offlineDisbursement;
	}

	@Autowired(required = false)
	@Qualifier(value = "customOfflineDisbursement")
	public void setCustomOfflineDisbursement(OfflineDisbursement customOfflineDisbursement) {
		this.customOfflineDisbursement = customOfflineDisbursement;
	}

	@Autowired(required = false)
	public void setImpsDisbursement(IMPSDisbursement impsDisbursement) {
		this.impsDisbursement = impsDisbursement;
	}

	private OfflineDisbursement getOfflineDisbursement() {
		return customOfflineDisbursement == null ? offlineDisbursement : customOfflineDisbursement;
	}

	public OnlineDisbursement getOnlineDisbursement() {
		return onlineDisbursement;
	}

	@Autowired(required = false)
	@Qualifier(value = "onlineDisbursement")
	public void setOnlineDisbursement(OnlineDisbursement onlineDisbursement) {
		this.onlineDisbursement = onlineDisbursement;
	}

}
