package com.pennant.pff.upload.job;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.dataengine.ProcessJobHandler;
import com.pennanttech.dataengine.ProcessRecord;
import com.pennanttech.dataengine.ValidateRecord;

public class UploadProcessJob implements Runnable {
	private static Logger logger = LogManager.getLogger(UploadProcessJob.class);

	private DataSource dataSource;
	private ValidateRecord validateRecord;
	private ProcessRecord processRecord;
	private List<FileUploadHeader> headerList;
	private UploadService uploadService;
	private Date appDate;
	private CountDownLatch latch;
	private String jobName;

	@Override
	public void run() {
		processesThread();
	}

	private void processesThread() {
		if (jobName.equals(headerList.get(0).getType().toUpperCase().concat("_APPROVER_JOB"))) {
			try {
				uploadService.doApprove(headerList);
			} catch (Exception e) {
				logger.warn("Approval Job failed.", e.getMessage());
			}
		} else {
			for (FileUploadHeader header : headerList) {
				logger.info(String.format("Process is initiated for the File %s", header.getFileName()));
				header.setAppDate(appDate);

				ProcessJobHandler handler = new ProcessJobHandler(header.getType().concat("_UPLOAD"), dataSource);
				handler.setValidateRecord(validateRecord);
				handler.setProcessRecord(processRecord);
				handler.processJobFile(header);
			}
		}

		latch.countDown();
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setValidateRecord(ValidateRecord validateRecord) {
		this.validateRecord = validateRecord;
	}

	public void setProcessRecord(ProcessRecord processRecord) {
		this.processRecord = processRecord;
	}

	public void setHeaderList(List<FileUploadHeader> headerList) {
		this.headerList = headerList;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}

	public void setUploadService(UploadService uploadService) {
		this.uploadService = uploadService;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

}
