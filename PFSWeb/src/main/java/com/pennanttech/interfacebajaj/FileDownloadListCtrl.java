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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.interfacebajaj.model.FileDownlaod;
import com.pennanttech.pff.core.Literal;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/FileDownload/FileDownloadList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class FileDownloadListCtrl extends GFCBaseListCtrl<FileDownlaod> implements Serializable {

	private static final long	serialVersionUID	= 1L;
	private final static Logger	logger				= Logger.getLogger(FileDownloadListCtrl.class);

	protected Window			window_FleDownloadList;
	protected Borderlayout		borderLayout_FileDownloadList;
	protected Paging			pagingFileDownloadList;
	protected Listbox			listBoxFileDownload;
	protected Button			btnRefresh;

	protected Listheader		listheader_FirstHeader;

	private Button				downlaod;

	private enum FileControl {
		MANDATES, DISBURSEMENT, SAPGL;
	}

	String	module	= null;

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

		this.module = getArgument("module");
		FileControl fileContol = FileControl.valueOf(this.module);

		switch (fileContol) {
		case MANDATES:
			super.tableName = "DE_FILE_CONTROL_VIEW";
			super.queueTableName = "DE_FILE_CONTROL_VIEW";
			break;
		case DISBURSEMENT:
			super.tableName = "DE_DISBURSE_FILE_CONTROL_VIEW";
			super.queueTableName = "DE_DISBURSE_FILE_CONTROL_VIEW";
			break;
		case SAPGL:
			super.tableName = "DE_FILE_CONTROL_VIEW";
			super.queueTableName = "DE_FILE_CONTROL_VIEW";
			break;
		default:
			break;
		}

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void onCreate$window_FleDownloadList(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_FleDownloadList, borderLayout_FileDownloadList, listBoxFileDownload,
				pagingFileDownloadList);
		setItemRender(new FileDownloadListModelItemRenderer());

		registerField("Name");
		registerField("FileName");
		registerField("FileLocation");
		registerField("Status");

		doRenderPage();
		search();

		logger.debug(Literal.LEAVING);
	}

	protected void doAddFilters() {
		super.doAddFilters();
		FileControl fileContol = FileControl.valueOf(this.module);
		List<String> list = new ArrayList<>();

		switch (fileContol) {
		case DISBURSEMENT:
				
			searchObject.removeField("PARTNERBANKNAME");
			searchObject.removeField("ALWFILEDOWNLOAD");
			
			this.searchObject.addField("PARTNERBANKNAME");
			this.searchObject.addField("ALWFILEDOWNLOAD");
			
			list.add("DISB_HDFC_EXPORT");
			list.add("DISB_IMPS_EXPORT");
			list.add("DISB_OTHER_CHEQUE_DD_EXPORT");
			list.add("DISB_OTHER_NEFT_RTGS_EXPORT");
			break;
		case MANDATES:
			list.add("MANDATES_EXPORT");
			break;
		case SAPGL:
			list.add("GL_TRAIL_BALANCE_EXPORT");
			list.add("GL_TRANSACTION_EXPORT");
			list.add("GL_TRANSACTION_SUMMARY_EXPORT");
			break;
		default:
			break;
		}

		this.searchObject.addFilterIn("NAME", list);
	}

	/**
	 * Call the FileDownload dialog with a new empty entry. <br>
	 */
	public void onClick$btnRefresh(Event event) throws Exception {
		doReset();
		search();
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
		private static final long	serialVersionUID	= 1L;

		@Override
		public void render(Listitem item, FileDownlaod fileDownlaod, int count) throws Exception {
			Listcell lc;

			if ("DISBURSEMENT".equals(module)) {
				lc = new Listcell(fileDownlaod.getPartnerBankName());
				lc.setParent(item);
			} else {
				lc = new Listcell(fileDownlaod.getName());
				lc.setParent(item);
				listheader_FirstHeader.setLabel("Name");
			}

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

			if (!ExecutionStatus.S.name().equals(fileDownlaod.getStatus())) {
				downlaod.setDisabled(true);
			}

			FileControl fileContol = FileControl.valueOf(module);

			switch (fileContol) {
			case DISBURSEMENT:
				downlaod.setTooltiptext("Disbursement request download.");
				break;
			case MANDATES:
				if (0 == fileDownlaod.getAlwFileDownload()) {
					downlaod.setTooltiptext("Not allow to download disbursement request file.");
				}

				if (StringUtils.containsIgnoreCase(fileDownlaod.getFileName(), "IMPS")) {
					downlaod.setDisabled(true);
					downlaod.setTooltiptext("IMPS Disbursement.");
				}

				break;
			case SAPGL:
				downlaod.setTooltiptext("SAPGL Files download.");
				break;
			default:
				break;
			}
			lc.setParent(item);

		}
	}

}