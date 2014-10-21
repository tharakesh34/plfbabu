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
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  DocumentUploadDialogCtrl.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  28-05-2012    
 *                                                                  
 * Modified Date    :  28-05-2012    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 28-05-2012       Pennant	                 0.1                                         * 
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

package com.pennant.webui.finance.externalfinance;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.externalinput.ExtFinanceData;
import com.pennant.externalinput.service.ExtFinanceUploadService;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages//DocumentUpload/documentUploadDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class ExtFinanceUploadDialogCtrl extends GFCBaseListCtrl<ExtFinanceData> implements Serializable {

	private static final long serialVersionUID = -7966467989874119940L;
	private final static Logger logger = Logger.getLogger(ExtFinanceUploadDialogCtrl.class);
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window  		window_ExtFinanceUploadDialog;// autowired
	protected Textbox 		fileName; 					// autowired
	protected Tabbox 		tabbox;

	// Button controller for the CRUD buttons
	protected Button btnValidate; 			// autowire
	protected Button btnProcess; 			// autowire
	protected Button btnHelp; 			// autowire]
	
	public String fileNameWithExt="";
	public String  outFileAbsolultePath="";
	private ExtFinanceUploadService extFinanceUploadService;
	
	/**
	 * default constructor.<br>
	 */
	public ExtFinanceUploadDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected External Finance Upload object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ExtFinanceUploadDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("ExtFinanceUploadDialog");
		this.btnValidate.setVisible(true);
		this.btnProcess.setVisible(true);
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * when the "Validate" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws IOException 
	 */
	public void onClick$btnValidate(Event event) {
		logger.debug("Entering" + event.toString());
		logger.debug("Leaving" + event.toString());
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
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_ExtFinanceUploadDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "Upload" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onUpload$btnUpload(UploadEvent event) throws Exception{	
		logger.debug("Entering" + event.toString());
		
		Media media = event.getMedia();
		this.fileNameWithExt = media.getName();		
		this.fileName.setValue("");		
		String status = "";
		if (media == null || (!media.getContentType().equalsIgnoreCase("application/vnd.ms-excel"))) {
			status = Labels.getLabel("fileformat_invalid") +" : "+ fileNameWithExt;
		}else{
			this.fileName.setValue(fileNameWithExt);
			status = getExtFinanceUploadService().procExternalFinance(media.getStreamData(), getUserWorkspace().getLoginUserDetails());
		}
		PTMessageUtils.showErrorMessage(status);
		logger.debug("Leaving" + event.toString());
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public ExtFinanceUploadService getExtFinanceUploadService() {
		return extFinanceUploadService;
	}

	public void setExtFinanceUploadService(
			ExtFinanceUploadService extFinanceUploadService) {
		this.extFinanceUploadService = extFinanceUploadService;
	}
}
