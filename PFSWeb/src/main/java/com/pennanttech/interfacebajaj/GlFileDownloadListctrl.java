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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
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
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.bajaj.process.SAPGLProcess;
import com.pennanttech.bajaj.process.TrailBalanceEngine;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.dataengine.util.EncryptionUtil;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.interfacebajaj.model.FileDownlaod;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;
import com.pennanttech.service.AmazonS3Bucket;

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
	protected Combobox dimention;
	protected Combobox months;
	private List<ValueLabel> dimentionsList = new ArrayList<>();
	private List<ValueLabel> monthsList = PennantStaticListUtil.getMontEnds(DateUtility.getAppDate());

	
	@Autowired
	protected DataEngineConfig dataEngineConfig;
	private Button downlaod;
 	
	protected AmazonS3Bucket bucket;
	
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
		
		dimentionsList.add(new ValueLabel(TrailBalanceEngine.Dimention.STATE.name(), TrailBalanceEngine.Dimention.STATE.name()));
		dimentionsList.add(new ValueLabel(TrailBalanceEngine.Dimention.CONSOLIDATE.name(), TrailBalanceEngine.Dimention.CONSOLIDATE.name()));
		
		fillComboBox(dimention, "", dimentionsList, "");
		fillComboBox(months, "", monthsList, "");
		
		registerField("Id", SortOrder.DESC);
		registerField("Name");
		registerField("Status");
		registerField("CONFIGID");
		registerField("POSTEVENT");
		registerField("FileName");
		registerField("FileLocation");
		registerField("ValueDate", SortOrder.DESC);
		
		doRenderPage();
		search();
		
		logger.debug(Literal.LEAVING);
	}
	
	public class FileDownloadComparator implements Comparator<Object>, Serializable {
		private static final long serialVersionUID = -8606975433219761922L;

		public FileDownloadComparator() {
			
		}
		
		@Override
		public int compare(Object o1, Object o2) {
			FileDownlaod data = (FileDownlaod) o1;
			FileDownlaod data2 = (FileDownlaod) o2;
			return data2.getValueDate().compareTo(data.getValueDate());
		}
	}

	protected void doAddFilters() {
		super.doAddFilters();
		List<String> list = new ArrayList<>();

		list.add("TRIAL_BALANCE_EXPORT_STATE");
		list.add("TRIAL_BALANCE_EXPORT_CONSOLIDATE");
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
		doSetValidations();
		ArrayList<WrongValueException> wve = new ArrayList<>();

		try {
			this.dimention.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.months.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		try {
			String selectedDimention = dimention.getSelectedItem().getValue();
			String selectedMonth = months.getSelectedItem().getValue();

			Date valueDate = null;
			Date appDate = null;

			appDate = DateUtil.parse(selectedMonth, PennantConstants.DBDateFormat);
			valueDate = appDate;

			TrailBalanceEngine trialbal = new TrailBalanceEngine((DataSource) SpringUtil.getBean("dataSource"),
					getUserWorkspace().getUserDetails().getUserId(), valueDate, appDate);

			if (trialbal.isBatchExists(selectedDimention)) {
				int conf = MessageUtil
						.confirm("Trial balance already generated for the selected month.\n Do you want to continue?");

				if (conf == MessageUtil.NO) {
					return;
				} 
			}
			DataEngineStatus status = TrailBalanceEngine.EXTRACT_STATUS;
			status.setStatus("I");
			if (selectedDimention.equals(TrailBalanceEngine.Dimention.STATE.name())) {
				trialbal.extractReport(TrailBalanceEngine.Dimention.STATE);

				if ("S".equals(status.getStatus())) {
					new SAPGLProcess((DataSource) SpringUtil.getBean("dataSource"),
							getUserWorkspace().getUserDetails().getUserId(), valueDate, appDate).extractReport();
				}
			} else if (selectedDimention.equals(TrailBalanceEngine.Dimention.CONSOLIDATE.name())) {
				trialbal.extractReport(TrailBalanceEngine.Dimention.CONSOLIDATE);
			}

			refresh();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

	}

	
	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidations() throws Exception {

		if (!this.dimention.isDisabled()) {
			this.dimention.setConstraint(
					new StaticListValidator(dimentionsList, Labels.getLabel("label_GLFileList_Dimension.value")));
		}

		if (!this.months.isDisabled() && PennantConstants.List_Select.equals(this.months.getSelectedItem().getValue()))
			this.months.setConstraint(
					new StaticListValidator(monthsList, Labels.getLabel("label_GLFileList_Months.value")));

	}
	
	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering ");
		this.dimention.setConstraint("");
		this.months.setConstraint("");

		logger.debug("Leaving ");
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

			
			if (item instanceof Listgroup) {	
				item.appendChild(new Listcell((DateUtility.formatDate(fileDownlaod.getValueDate(), DateFormat.LONG_MONTH.getPattern())))); 
			} else if (item instanceof Listgroupfoot) { 
				Listcell cell = new Listcell("");
				cell.setSpan(4);
				item.appendChild(cell); 

			} else {
			
			lc = new Listcell(fileDownlaod.getName());
			lc.setParent(item);

			lc = new Listcell(fileDownlaod.getFileName());
			lc.setParent(item);
			
			lc = new Listcell(DateUtility.formatDate(fileDownlaod.getValueDate(), PennantConstants.dateFormat));
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
			
				if (!ExecutionStatus.S.name().equals(fileDownlaod.getStatus())) {
					downlaod.setDisabled(true);
					downlaod.setTooltiptext("SAPGL request for file generation failed.");
				}

				if (!com.pennanttech.dataengine.Event.MOVE_TO_S3_BUCKET.name().equals(fileDownlaod.getPostEvent())) {
					File file = new File(builder.toString());
					if (!file.exists()) {
						downlaod.setDisabled(true);
						downlaod.setTooltiptext("File not available.");
					}
				}
	
			lc.setParent(item);
			}

		}
	}
}