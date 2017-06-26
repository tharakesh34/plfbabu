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
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.interfacebajaj.model.FileDownlaod;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.reports.cibil.CIBILReport;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/FileDownload/DisbursementFileDownloadList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class GlFileDownloadListctrl extends GFCBaseListCtrl<FileDownlaod> implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(GlFileDownloadListctrl.class);

	protected Window window_GlFileDownloadList;
	protected Borderlayout borderLayout_GlFileDownloadList;
	protected Paging pagingFileDownloadList;
	protected Listbox listBoxFileDownload;
	protected Button btnRefresh;
	protected Button btnexecute;
	
	@Autowired
	protected DataEngineConfig dataEngineConfig;

	private Button downlaod;
	
	@Autowired
	private CIBILReport cibilReport;

	/**
	 * default constructor.<br>
	 */
	public GlFileDownloadListctrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FileDownload";
		super.pageRightName = "FileDownload";
		super.tableName = "DE_FILE_CONTROL_VIEW";
		super.queueTableName = "DE_FILE_CONTROL_VIEW";

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void onCreate$window_GlFileDownloadList(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_GlFileDownloadList, borderLayout_GlFileDownloadList,
				listBoxFileDownload, pagingFileDownloadList);
		setItemRender(new FileDownloadListModelItemRenderer());
		setComparator(new FileDownloadComparator());


		registerField("Id");
		registerField("Name");
		registerField("FileName");
		registerField("FileLocation");
		registerField("Status");
		registerField("EndTime");
		
		doRenderPage();
		search();

		logger.debug(Literal.LEAVING);
	}

	
	
	public class FileDownloadComparator implements Comparator<Object>, Serializable {
		
		private static final long serialVersionUID = -8606975433219761922L;

		public FileDownloadComparator() {
			
		}
		
		@SuppressWarnings("deprecation")
		@Override
	    public int compare(Object o1, Object o2) { 
			FileDownlaod data = (FileDownlaod) o1; 
			FileDownlaod data2 = (FileDownlaod) o2; 
	        return String.valueOf(data.getEndTime().getDay()).compareTo(
	        		String.valueOf(data2.getEndTime().getDay())); 
	        
	    }
	}

	protected void doAddFilters() {
		super.doAddFilters();
		List<String> list = new ArrayList<>();

		list.add("GL_TRAIL_BALANCE_EXPORT");
		list.add("GL_TRANSACTION_EXPORT");
		list.add("GL_TRANSACTION_SUMMARY_EXPORT");

		this.searchObject.addFilterIn("NAME", list);
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
			dataEngineConfig.saveDowloadHistory(fileDownlaod.getId(), getUserWorkspace().getUserDetails().getUserId());
			stream = null;
			refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug(Literal.LEAVING);
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

			
			if (item instanceof Listgroup) {	
				item.appendChild(new Listcell((DateUtility.formatDate(fileDownlaod.getEndTime(),PennantConstants.dateTimeFormat)))); 

			} else if (item instanceof Listgroupfoot) { 
				Listcell cell = new Listcell("");
				cell.setSpan(4);
				item.appendChild(cell); 

			} else {
			
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
			downlaod.setTooltiptext("Download");

			downlaod.setAttribute("object", fileDownlaod);

			StringBuilder builder = new StringBuilder();
			builder.append(fileDownlaod.getFileLocation());
			builder.append(File.separator);
			builder.append(fileDownlaod.getFileName());

			File file = new File(builder.toString());

			if (!file.exists()) {
				downlaod.setDisabled(true);
				downlaod.setTooltiptext("File not available.");
			} else if (!ExecutionStatus.S.name().equals(fileDownlaod.getStatus())) {
				downlaod.setDisabled(true);
				downlaod.setTooltiptext("SAPGL request for file generation failed.");
			}


			lc.setParent(item);
			}

		}
	}
}