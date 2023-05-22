package com.pennanttech.interfacebajaj.fileextract;

import javax.sql.DataSource;

import com.pennant.backend.service.financemanagement.PresentmentDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.interfacebajaj.fileextract.service.AbstractFileExtractService;
import com.pennanttech.interfacebajaj.fileextract.service.FileExtractService;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.notifications.service.NotificationService;

public class PresentmentExtractService extends AbstractFileExtractService
		implements FileExtractService<PresentmentDetailExtract> {

	private DataSource dataSource;
	private PresentmentDetailService presentmentDetailService;
	private NotificationService notificationService;

	@Override
	public PresentmentDetailExtract getFileExtract(long userId, String contentType) throws Exception {
		PresentmentDetailExtract extractDetails = new PresentmentDetailExtract(dataSource);
		extractDetails.setDefaultDirectory(getLoacation("PRESENTMENT_RESPONSE_UPLOAD_FILEPATH"));
		extractDetails.setFileExtension(contentType);
		extractDetails.setFileNamePrefix("");
		extractDetails.setBatchType("PRESENTMENT_RESPONSE_IMPORT");
		extractDetails.setUserId(userId);
		extractDetails.setLogStatus(false);
		extractDetails.setUserDetails(new LoggedInUser());
		extractDetails.setStatus(new DataEngineStatus());
		extractDetails.setNotificationService(notificationService);
		extractDetails.setPresentmentDetailService(presentmentDetailService);

		return extractDetails;
	}

	@Override
	public void renderPannel(PresentmentDetailExtract extractDetails) {
		PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.reset();
		PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setFileName(extractDetails.getFile().getName());
		PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setTotalRecords(extractDetails.getTotalRecords());
		PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setValueDate(DateUtil.getSysDate());
		PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setName("Presentment Details");
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setPresentmentDetailService(PresentmentDetailService presentmentDetailService) {
		this.presentmentDetailService = presentmentDetailService;
	}

	/**
	 * @return the notificationService
	 */
	public NotificationService getNotificationService() {
		return notificationService;
	}

	/**
	 * @param notificationService the notificationService to set
	 */
	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

}
