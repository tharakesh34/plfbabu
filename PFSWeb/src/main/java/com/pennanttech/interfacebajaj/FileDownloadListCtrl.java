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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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

import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.interfacebajaj.model.FileDownlaod;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/FileDownload/FileDownloadList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class FileDownloadListCtrl extends GFCBaseListCtrl<FileDownlaod> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(FileDownloadListCtrl.class);

	protected Window window_FleDownloadList;
	protected Borderlayout borderLayout_FileDownloadList;
	protected Paging pagingFileDownloadList;
	protected Listbox listBoxFileDownload;
	protected Button btnRefresh;

	private Button downlaod;

	/**
	 * default constructor.<br>
	 */
	public FileDownloadListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FileDownload";
		super.pageRightName = "FileDownload";
		super.tableName = "FILE_DOWNLOAD_VIEW";
		super.queueTableName = "FILE_DOWNLOAD_VIEW";
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void onCreate$window_FleDownloadList(Event event) throws Exception {
		logger.debug("Entering");
		
		// Set the page level components.
		setPageComponents(window_FleDownloadList, borderLayout_FileDownloadList, listBoxFileDownload, pagingFileDownloadList);
		setItemRender(new FileDownloadListModelItemRenderer());
		
		registerField("Name");
		registerField("FileName");
		registerField("FileLocation");
		registerField("PartnerBankName");
		registerField("AlwFileDownload");
		registerField("Status");
		
		doRenderPage();
		search();
		
		logger.debug("Leaving");
	}

	/**
	 * Call the FileDownload dialog with a new empty entry. <br>
	 */
	public void onClick$btnRefresh(Event event) throws Exception {
		logger.debug(event.toString());
		doReset();
		search();
		logger.debug("Leaving");
	}

	public void onClick_Downlaod(ForwardEvent event) throws Exception {
		logger.debug("Entering");
		try {

			Button downloadButt = (Button) event.getOrigin().getTarget();

			FileDownlaod fileDownlaod = (FileDownlaod) downloadButt.getAttribute("object");

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
			stream = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug("Leaving");
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

			lc = new Listcell(fileDownlaod.getPartnerBankName());
			lc.setParent(item);

			lc = new Listcell(fileDownlaod.getFileName());
			lc.setParent(item);

			lc = new Listcell(fileDownlaod.getFileLocation());
			lc.setParent(item);
			
			lc = new Listcell(ExecutionStatus.getStatus(fileDownlaod.getStatus()).getValue());
			lc.setParent(item);

			lc = new Listcell();
			downlaod = new Button();
			downlaod.addForward("onClick", self, "onClick_Downlaod");
			lc.appendChild(downlaod);
			downlaod.setLabel("Download");
			downlaod.setAttribute("object", fileDownlaod);
			
			if (0 == fileDownlaod.getAlwFileDownload() || !ExecutionStatus.S.name().equals(fileDownlaod.getStatus())) {
				downlaod.setDisabled(true);
			}

			if (0 == fileDownlaod.getAlwFileDownload()) {
				downlaod.setTooltiptext("Not allowed to downlaod.");
			} 
			
			if(StringUtils.containsIgnoreCase(fileDownlaod.getFileName(), "IMPS")){
				downlaod.setDisabled(true);
				downlaod.setTooltiptext("IMPS Disbursement.");
			}
			
			lc.setParent(item);
			
		}
	}

}