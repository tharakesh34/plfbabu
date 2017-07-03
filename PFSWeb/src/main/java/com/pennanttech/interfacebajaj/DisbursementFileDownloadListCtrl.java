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
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.interfacebajaj.model.FileDownlaod;
import com.pennanttech.pff.core.Literal;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/FileDownload/DisbursementFileDownloadList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class DisbursementFileDownloadListCtrl extends GFCBaseListCtrl<FileDownlaod>  {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(DisbursementFileDownloadListCtrl.class);

	private Window window_DisbursementFileDownloadList;
	private Borderlayout borderLayout_DisbursementFileDownloadList;
	private Paging pagingFileDownloadList;
	private Listbox listBoxFileDownload;
	protected Button btnRefresh;
	protected Timer timer;
	private Button downlaod;

	String module = null;
	@Autowired
	protected DataEngineConfig dataEngineConfig;
	/**
	 * default constructor.<br>
	 */
	public DisbursementFileDownloadListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FileDownload";
		super.pageRightName = "FileDownload";

		this.module = getArgument("module");

		super.tableName = "DE_DISBURSE_FILE_CONTROL_VIEW";
		super.queueTableName = "DE_DISBURSE_FILE_CONTROL_VIEW";

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void onCreate$window_DisbursementFileDownloadList(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_DisbursementFileDownloadList, borderLayout_DisbursementFileDownloadList,
				listBoxFileDownload, pagingFileDownloadList);
		setItemRender(new FileDownloadListModelItemRenderer());

		registerField("ID");
		registerField("PartnerBankCode");
		registerField("FileLocation");
		registerField("FileName");
		registerField("Status");
		registerField("Name");
		registerField("EndTime");

		doRenderPage();
		search();
		this.borderLayout_DisbursementFileDownloadList.setHeight(borderLayoutHeight+ "px");
		logger.debug(Literal.LEAVING);
	}

	protected void doAddFilters() {
		super.doAddFilters();
		List<String> list = new ArrayList<>();

		searchObject.removeField("PARTNERBANKNAME");
		searchObject.removeField("ALWFILEDOWNLOAD");

		this.searchObject.addField("PARTNERBANKNAME");
		this.searchObject.addField("ALWFILEDOWNLOAD");

		list.add("DISB_HDFC_EXPORT");
		list.add("DISB_IMPS_EXPORT");
		list.add("DISB_OTHER_CHEQUE_DD_EXPORT");
		list.add("DISB_OTHER_NEFT_RTGS_EXPORT");

		this.searchObject.addFilterIn("NAME", list);
	}

	/**
	 * Call the FileDownload dialog with a new empty entry. <br>
	 */
	public void onClick$btnRefresh(Event event) throws Exception {
		refresh();
	}

	private void refresh() {
		doReset();
		search();
	}

	public void onClick_Downlaod(ForwardEvent event) throws Exception {
		logger.debug(Literal.ENTERING);
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
			
			dataEngineConfig.saveDowloadHistory(fileDownlaod.getId(),getUserWorkspace().getUserDetails().getUserId());
			refresh();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
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

			lc = new Listcell(fileDownlaod.getName());
			lc.setParent(item);

			lc = new Listcell(fileDownlaod.getFileName());
			lc.setParent(item);
			
			lc = new Listcell(DateUtility.formatDate(fileDownlaod.getEndTime(), PennantConstants.dateTimeFormat));
			lc.setParent(item);


			lc = new Listcell(ExecutionStatus.getStatus(fileDownlaod.getStatus()).getValue());
			lc.setParent(item);

			lc = new Listcell();
			downlaod = new Button();
			downlaod.addForward("onClick", self, "onClick_Downlaod");
			lc.appendChild(downlaod);
			downlaod.setLabel("Download");
			downlaod.setAttribute("object", fileDownlaod);

			StringBuilder builder = new StringBuilder();
			builder.append(fileDownlaod.getFileLocation());
			builder.append(File.separator);
			builder.append(fileDownlaod.getFileName());

			File file = new File(builder.toString());
			
			if(!fileDownlaod.isAlwFileDownload()) {
				downlaod.setDisabled(true);
				downlaod.setTooltiptext("Not allowed to download.");
			} else  if (!file.exists()) {
				downlaod.setDisabled(true);
				downlaod.setTooltiptext("File not available.");
			} else if (!ExecutionStatus.S.name().equals(fileDownlaod.getStatus())) {
				downlaod.setDisabled(true);
				downlaod.setTooltiptext("File generation failed.");
			}

			lc.setParent(item);
		}
	}
	
	public void onTimer$timer(Event event) {
		if (pagingFileDownloadList.getActivePage() == 0) {
			Events.postEvent("onCreate", this.window_DisbursementFileDownloadList, event);
			searchObject.clearFields();
		}
	}
}