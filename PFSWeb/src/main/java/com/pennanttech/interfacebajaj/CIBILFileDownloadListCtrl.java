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
 * FileName    		:  FileDownloadListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-06-2013    														*
 *                                                                  						*
 * Modified Date    :  26-06-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-06-2013       Pennant	                 0.1                                            * 
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

package com.pennanttech.interfacebajaj;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.search.Filter;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.dataengine.util.EncryptionUtil;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.interfacebajaj.model.FileDownlaod;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.reports.cibil.CIBILReport;
import com.pennanttech.service.AmazonS3Bucket;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/FileDownload/DisbursementFileDownloadList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CIBILFileDownloadListCtrl extends GFCBaseListCtrl<FileDownlaod> implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(CIBILFileDownloadListCtrl.class);

	protected Window window_CIBILFileDownloadList;
	protected Borderlayout borderLayout_CIBILFileDownloadList;
	protected Paging pagingFileDownloadList;
	protected Listbox listBoxFileDownload;
	protected Button btnRefresh;
	protected Button btnexecute;

	private Button downlaod;

	@Autowired
	protected DataEngineConfig dataEngineConfig;

	protected AmazonS3Bucket bucket;
	@Autowired
	private CIBILReport cibilReport;

	/**
	 * default constructor.<br>
	 */
	public CIBILFileDownloadListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FileDownload";
		super.pageRightName = "FileDownload";
		super.tableName = "CIBIL_FILE_INFO_VIEW";
		super.queueTableName = "CIBIL_FILE_INFO_VIEW";
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();

		Filter[] filter = new Filter[1];
		filter[0] = new Filter("STATUS", "C");
		this.searchObject.addFilters(filter);

	}

	public void onCreate$window_CIBILFileDownloadList(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_CIBILFileDownloadList, borderLayout_CIBILFileDownloadList, listBoxFileDownload,
				pagingFileDownloadList);
		setItemRender(new FileDownloadListModelItemRenderer());

		registerField("ID", SortOrder.DESC);
		registerField("File_Name");
		registerField("CreatedOn EndTime");
		registerField("Status");
		registerField("File_Location FileLocation");
		registerField("ConfigId");
		registerField("PostEvent");

		doRenderPage();
		search();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Call the FileDownload dialog with a new empty entry. <br>
	 */
	public void onClick$btnRefresh(Event event) throws Exception {
		refresh();
	}

	/**
	 * Call the FileDownload dialog with a new empty entry. <br>
	 */
	public void onClick$btnexecute(Event event) throws Exception {
		cibilReport.generateReport();
	}

	public void onClick_Downlaod(ForwardEvent event) throws Exception {
		logger.debug(Literal.ENTERING);
		try {

			Button downloadButt = (Button) event.getOrigin().getTarget();
			FileDownlaod fileDownlaod = (FileDownlaod) downloadButt.getAttribute("object");

			if (com.pennanttech.dataengine.Event.MOVE_TO_S3_BUCKET.name().equals(fileDownlaod.getPostEvent())) {
				String prefix = loadS3Bucket(fileDownlaod.getConfigId());

				downloadFromS3Bucket(prefix, fileDownlaod.getFileName());
			} else {
				downloadFromServer(fileDownlaod);
			}
			dataEngineConfig.saveDowloadHistory(fileDownlaod.getId(), getUserWorkspace().getUserDetails().getUserId());
			refresh();
		} catch (Exception e) {
			MessageUtil.showError(e.getMessage());
		}
		logger.debug(Literal.LEAVING);
	}

	private String loadS3Bucket(long configId) {

		EventProperties eventproperties = dataEngineConfig.getEventProperties(configId, "S3");

		bucket = new AmazonS3Bucket(eventproperties.getRegionName(), eventproperties.getBucketName(),
				EncryptionUtil.decrypt(eventproperties.getAccessKey()),
				EncryptionUtil.decrypt(eventproperties.getSecretKey()));

		return eventproperties.getPrefix();
	}

	private void downloadFromServer(FileDownlaod fileDownlaod) throws FileNotFoundException, IOException {
		String filePath = fileDownlaod.getFileLocation();
		String fileName = fileDownlaod.getFileName();

		if (filePath != null && fileName != null) {
			filePath = filePath.concat("/").concat(fileName);
		}

		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		InputStream inputStream = new FileInputStream(filePath);
		int data;
		while ((data = inputStream.read()) >= 0) {
			stream.write(data);
		}

		inputStream.close();
		inputStream = null;
		Filedownload.save(stream.toByteArray(), "text/plain", fileName);
		stream.close();
	}

	private void downloadFromS3Bucket(String prefix, String fileName) throws Exception {
		String key = prefix.concat("/").concat(fileName);

		byte[] fileData = bucket.getObject(key);
		Filedownload.save(fileData, "text/plain", fileName);
	}

	private void refresh() {
		doReset();
		search();
	}

	/**
	 * Item renderer for listitems in the listbox.
	 * 
	 */
	private class FileDownloadListModelItemRenderer implements ListitemRenderer<FileDownlaod>, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public void render(Listitem item, FileDownlaod fileDownlaod, int count) throws Exception {
			Listcell lc;

			lc = new Listcell(fileDownlaod.getFileName());
			lc.setParent(item);

			lc = new Listcell(DateUtility.formatToLongDate(fileDownlaod.getEndTime()));
			lc.setParent(item);

			lc = new Listcell(fileDownlaod.getStatus());
			lc.setParent(item);

			lc = new Listcell();
			downlaod = new Button();
			downlaod.addForward("onClick", self, "onClick_Downlaod");
			lc.appendChild(downlaod);
			downlaod.setLabel("Download");
			downlaod.setTooltiptext("Download");

			downlaod.setAttribute("object", fileDownlaod);

			StringBuilder builder = new StringBuilder();
			builder.append(fileDownlaod.getFileLocation());
			builder.append(File.separator);
			builder.append(fileDownlaod.getFileName());

			File file = new File(builder.toString());

			 if (!"C".equals(fileDownlaod.getStatus())) {
				downlaod.setDisabled(true);
				downlaod.setTooltiptext("CIBIL request for file generation failed.");
			}

			if (!com.pennanttech.dataengine.Event.MOVE_TO_S3_BUCKET.name().equals(fileDownlaod.getPostEvent())) {
				if (!file.exists()) {
					downlaod.setDisabled(true);
					downlaod.setTooltiptext("File not available.");
				}
			}

			lc.setParent(item);

		}
	}
}