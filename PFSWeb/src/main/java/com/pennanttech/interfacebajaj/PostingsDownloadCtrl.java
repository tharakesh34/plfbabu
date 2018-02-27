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

package com.pennanttech.interfacebajaj;

import java.io.Serializable;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.mandate.Mandate;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.external.PostingDownloadService;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/Mandate/MandateRegistration.zul file.<br>
 * ************************************************************<br>
 * 
 */
public class PostingsDownloadCtrl extends GFCBaseListCtrl<Mandate> implements Serializable {

	private static final long		serialVersionUID	= 1L;
	private static final Logger		logger				= Logger.getLogger(PostingsDownloadCtrl.class);

	protected Window				window_PostingsDownloadCtrl;
	protected Datebox				postingDate;
	protected Button				btnFileUpload;

	@Autowired(required = true)
	private PostingDownloadService	postingDownloadService;

	/**
	 * default constructor.<br>
	 */
	public PostingsDownloadCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {

	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_PostingsDownloadCtrl(Event event) {
		// Set the page level components.
		setPageComponents(window_PostingsDownloadCtrl);

		// Register buttons and fields.
		registerButton(btnFileUpload);
	}

	/**
	 * when the "Upload" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnFileUpload(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		if (postingDate.getValue() == null) {
			MessageUtil.showError(App.getLabel("PostingDownload_PostDate_Mand"));
			return;
		}
		try {
			PostingDownloadProcess process = new PostingDownloadProcess(postingDate.getValue());
			Thread thread = new Thread(process);
			thread.start();
		} catch (Exception e) {
			logger.error("Exception :", e);
			MessageUtil.showMessage(e.getMessage());
		}
		MessageUtil.showMessage(App.getLabel("PostingDownload_Request_Sent"));
	}

	public class PostingDownloadProcess extends Thread {

		private Date postingDate;

		public PostingDownloadProcess(Date postingDate) {
			this.postingDate = postingDate;
		}

		@Override
		public void run() {
			long userId = getUserWorkspace().getLoggedInUser().getUserId();
			try {
				postingDownloadService.sendPostings(postingDate, userId);
			} catch (Exception e) {
				logger.error("Exception", e);
			}
		}
	}
}