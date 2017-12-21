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
 * FileName    		:  MandateListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-10-2016    														*
 *                                                                  						*
 * Modified Date    :  18-10-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-10-2016       Pennant	                 0.1                                            * 
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

package com.pennant.webui.mandate.mandate;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.mandate.MandateStatusUpdate;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.backend.util.MandateConstants;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * ************************************************************<br>
 * This is the controller class for the
 * /WEB-INF/pages/Mandate/MandateFileUpload.zul file.<br>
 * ************************************************************<br>
 * 
 */
public class MandateFileUploadListCtrl extends GFCBaseListCtrl<Mandate> implements Serializable {

	private static final long			serialVersionUID	= 1L;
	private static final Logger			logger				= Logger.getLogger(MandateFileUploadListCtrl.class);

	protected Window					window_MandateFileUploadList;
	protected Borderlayout				borderLayout_MandateList;
	protected Paging					pagingMandateList;
	protected Listbox					listBoxMandate;

	protected Label						totalCount;
	protected Label						procCustomers;
	protected Label						status;
	protected Label						remarks;
	protected Textbox					fileName;
	protected Datebox					startDate;
	protected Datebox					endDate;

	private transient MandateService	mandateService;

	/**
	 * default constructor.<br>
	 */
	public MandateFileUploadListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Mandate";
		super.pageRightName = "MandateList";
		super.tableName = "Mandates_AView";
		super.queueTableName = "Mandates_View";
	}

	@Override
	protected void doAddFilters() {

		super.doAddFilters();
		searchObject.addFilterEqual("active", 1);
	}

	/**
	 * The framework calls this event handler when an application requests that
	 * the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_MandateFileUploadList(Event event) {

		// Set the page level components.
		setPageComponents(window_MandateFileUploadList, borderLayout_MandateList, listBoxMandate, pagingMandateList);

		// Render the page and display the data.
		doRenderPage();
	}

	/**
	 * when the "Process" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnProcess(Event event) {
		logger.debug("Entering" + event.toString());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "Upload" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onUpload$btnUpload(UploadEvent event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		boolean isSupported = false;
		Media media = event.getMedia();
		if ("txt".equals(media.getFormat()) || "xlsx".equals(media.getFormat())) {
			isSupported=true;
		}
		
		if (isSupported) {
			AMedia amedia =(AMedia) media;
			fileName.setValue(amedia.getName());
			String[] data = amedia.getStringData().split("\n");
			int total = 0;
			int sucessCount = 0;
			int failedCount = 0;
			String status = null;
			String reason = null;
			MandateStatusUpdate mandateStatusUpdate = new MandateStatusUpdate();
			if(mandateService.getFileCount(amedia.getName()) == 0){
			mandateStatusUpdate.setStartDate(DateUtility.getAppDate());
			mandateStatusUpdate.setUserDetails(getUserWorkspace().getLoggedInUser());
			mandateStatusUpdate.setUserId(getUserWorkspace().getLoggedInUser().getUserId());
			mandateStatusUpdate.setFileName(amedia.getName());
			long fileid = mandateService.processStatusSave(mandateStatusUpdate);
			for (String line : data) {
				try {
					total++;
					String[] record = line.split("[|]");
					// get mandate whose status is awaiting confirmation
					Mandate mandate = mandateService.getMandateStatusUpdateById(Long.parseLong(StringUtils.trimToEmpty(record[0])), MandateConstants.STATUS_AWAITCON);
					mandate.setMandateRef(record[10]);
					mandate.setApprovalID(record[12]);
					mandate.setUserDetails(getUserWorkspace().getLoggedInUser());
					status = record[9];
					reason = record[11];
					mandateService.processFileUpload(mandate, status, reason, fileid);
					sucessCount++;
				} catch (Exception e) {
					logger.error("Exception", e);
					failedCount++;
				}
			}
			totalCount.setValue(String.valueOf(total));
			procCustomers.setValue(String.valueOf(sucessCount));
			this.status.setValue(status);
			remarks.setValue(Labels.getLabel("MandateDataList_Remarks", new String[] { String.valueOf(total),String.valueOf(sucessCount),String.valueOf(failedCount) }));
			mandateStatusUpdate.setEndDate(DateUtility.getAppDate());
			mandateStatusUpdate.setRemarks(reason);
			mandateStatusUpdate.setTotalCount(total);
			mandateStatusUpdate.setSuccess(sucessCount);
			mandateStatusUpdate.setFail(failedCount);
			mandateService.processStatusUpdate(mandateStatusUpdate);
		} else {
				MessageUtil.showError(fileName.getValue() + " already updated");
			fileName.setValue("");
		}
		}else{
			MessageUtil.showError(Labels.getLabel("Mandate_Supported_Document"));
		}

		logger.debug("Leaving" + event.toString());
	}

	public void setMandateService(MandateService mandateService) {
		this.mandateService = mandateService;
	}
}