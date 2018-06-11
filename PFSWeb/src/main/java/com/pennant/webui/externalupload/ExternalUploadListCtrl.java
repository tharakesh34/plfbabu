/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  PaymentHeaderListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2017    														*
 *                                                                  						*
 * Modified Date    :  27-05-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2017       PENNANT	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/

package com.pennant.webui.externalupload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.PathUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.util.PennantConstants;
import com.pennant.batchupload.fileprocessor.BatchUploadProcessor;
import com.pennant.batchupload.fileprocessor.service.BatchUploadConfigService;
import com.pennant.batchupload.model.BatchUploadConfig;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;


/**
 * This is the controller class for the
 * /WEB-INF/pages/com.pennant.payment/PaymentHeader/PaymentHeaderList.zul file.
 * 
 */
public class ExternalUploadListCtrl extends GFCBaseListCtrl<Object> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ExternalUploadListCtrl.class);

	protected Window window_ExternalUploadsList;
	protected Button btnImport;
	protected Row row1;
	protected Textbox fileName;
	protected Button btnFileUpload;
	protected Combobox apiType;

	private final String uploadLoaction =PathUtil.getPath(PathUtil.APP_ROOT_PATH);
	private File file;
	@Value("${api.authkey}")
	private String authorization;
	// ### 10-05-2018---- PSD TCT No :125164 
	@Value("${api.url}")
	private String baseurl;
	@Value("${api.entityId}")
	private String entityId;
	private String extraHeader = null;
	private String apiUrl = null;
	private String sourceFileName = null;
	private Media media = null;

	private BatchUploadConfigService batchUploadConfigService;

	/**
	 * default constructor.<br>
	 */
	public ExternalUploadListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
	}

	/**
	 * The framework calls this event handler when an application requests that
	 * the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_ExternalUploadsList(Event event) {
		setPageComponents(window_ExternalUploadsList);
		//String baseurl = SysParamUtil.getValueAsString("PFFAPI_SERVICE_URL");
		// String baseurl = "http://192.168.1.160:8080/pff-api/services";

		if (StringUtils.isBlank(baseurl)) {
			MessageUtil.showError("Could not find configuration in system parameters.");
			return;
		}

		List<BatchUploadConfig> batchUploadActiveConfig = batchUploadConfigService.getActiveConfiguration();
		fillComboBox(apiType, "", prepairDropDwnList(batchUploadActiveConfig, baseurl), "");
	}

	public void onClick$btnImport(Event event) throws InterruptedException {
		try {
			if (media != null) {
				writeFile(media);
			}
			if (file == null) {
				MessageUtil.showError("Please select the file to upload.");
				return;
			}

			doFileProcess();

			MessageUtil.showError("File is processed successfully.");

			downloadResponseFile();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			if (e.getLocalizedMessage() != null) {
				MessageUtil.showError(e.getLocalizedMessage());
			} else {
				MessageUtil.showError("Something Went Wrong Please Try Again Later.");
			}
			return;
		} finally {
			if (file != null && file.exists()) {
				if (!file.delete()) {
					Thread.sleep(7000);
					file.delete();
				}
			}
			file = null;
			fileName.setText("");
			media = null;
		}
	}

	/**
	 * when the Source type is changed. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onChange$apiType(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		try {
			String[] valueArray = org.apache.commons.lang3.StringUtils.split(this.apiType.getSelectedItem().getValue(),
					",");
			apiUrl = valueArray[0];
			if (valueArray.length > 1 && org.apache.commons.lang3.StringUtils.isNotBlank(valueArray[1])
					&& !"null".equals(valueArray[1])) {
				extraHeader = valueArray[1];
			}
			sourceFileName = this.apiType.getSelectedItem().getLabel();
			logger.info(apiUrl);
			fileName.setValue("");
			file = null;
			media = null;

			if (org.apache.commons.lang3.StringUtils.isNotBlank(apiUrl)) {
				row1.setVisible(true);
			} else {
				this.btnImport.setDisabled(true);
				row1.setVisible(false);
				return;
			}

		} catch (Exception e) {
			row1.setVisible(true);
			apiType.setValue(PennantConstants.List_Select);
			apiType.setSelectedIndex(0);
			this.btnImport.setDisabled(true);
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	public void onUpload$btnFileUpload(UploadEvent event) throws Exception {
		fileName.setText("");
		media = event.getMedia();

		if (!(StringUtils.endsWith(media.getName().toLowerCase(), ".xls")
				|| StringUtils.endsWith(media.getName().toLowerCase(), ".xlsx"))) {
			MessageUtil.showError("The uploaded file could not be recognized. Please upload a valid excel file.");
			this.btnImport.setDisabled(true);
			media = null;
			return;
		}

		this.btnImport.setDisabled(false);
		fileName.setText(media.getName());
	}

	private void writeFile(Media media) throws IOException {
		File parent = new File(uploadLoaction);

		if (!parent.exists()) {
			parent.mkdirs();
		}
		file = new File(parent.getPath().concat(File.separator).concat(media.getName()));
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		FileUtils.writeByteArrayToFile(file, media.getByteData());
	}

	private void doFileProcess() throws Exception {
		logger.debug(Literal.ENTERING);
		if (org.apache.commons.lang3.StringUtils.isNotBlank(apiUrl)) {
			long userId=getUserWorkspace().getUserId();

			BatchUploadProcessor batchProcessor = new BatchUploadProcessor(file, authorization, apiUrl, extraHeader,
					sourceFileName,entityId,userId);
			batchProcessor.process();
			fileName.setValue("");
		}
		logger.debug(Literal.LEAVING);
	}

	private void downloadResponseFile() {
		ByteArrayOutputStream stream = null;
		InputStream inputStream = null;
		try {
			stream = new ByteArrayOutputStream();
			inputStream = new FileInputStream(file.getPath());
			int data;
			while ((data = inputStream.read()) >= 0) {
				stream.write(data);
			}
			inputStream.close();
			inputStream = null;
			Filedownload.save(stream.toByteArray(), "text/plain", FilenameUtils.removeExtension(file.getName())
					+ "_Response." + FilenameUtils.getExtension(file.getName()));
			stream.close();
			stream = null;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError("Something Went Wrong Please Contact To System Administrator.");
		} finally {

			try {
				if (stream != null) {
					stream.close();
					stream = null;
				}
				if (inputStream != null) {
					inputStream.close();
					inputStream = null;
				}
			} catch (IOException e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}
	
	private List<ValueLabel> prepairDropDwnList(List<BatchUploadConfig> batchUploadActiveConfig, String baseurl) {
		List<ValueLabel> list = new ArrayList<ValueLabel>();
		for (BatchUploadConfig batchUploadConfig : batchUploadActiveConfig) {
			list.add(new ValueLabel(baseurl + batchUploadConfig.getUrl() + "," + batchUploadConfig.getExtraHeader(),
					batchUploadConfig.getLabel()));
		}
		return list;
	}

	public BatchUploadConfigService getBatchUploadConfigService() {
		return batchUploadConfigService;
	}

	public void setBatchUploadConfigService(BatchUploadConfigService batchUploadConfigService) {
		this.batchUploadConfigService = batchUploadConfigService;
	}
}