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
 * FileName    		:  ReportGenerationPromptDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-09-2012   														*
 *                                                                  						*
 * Modified Date    :  23-09-2012      														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-09-2012         Pennant	                 0.1                                        * 
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
package com.pennant.webui.reports;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.reports.ReportSearchTemplate;
import com.pennant.backend.service.reports.ReportConfigurationService;
import com.pennant.webui.util.GFCBaseCtrl;

/**
 * This is the controller class for the
 * /WEB-INF/pages/reports/ReportSearchTemplatePromptDialog.zul file.
 */
public class ReportSearchTemplatePromptDialogCtrl extends GFCBaseCtrl<ReportSearchTemplate> {
	private static final long serialVersionUID = 4678287540046204660L;
	private static final Logger logger = Logger.getLogger(ReportSearchTemplatePromptDialogCtrl.class);

	protected Window     window_ReportSearchTemplateDialog;
	protected Textbox    templateName;
	protected Button     btnSaveTemplate;
	protected ReportGenerationPromptDialogCtrl  reportGenerationPromptDialogCtrl;
	private  ReportConfigurationService reportConfigurationService;
    private  long reportId;
    protected Radiogroup        saveTemplateFor;               // autowired

    public ReportSearchTemplatePromptDialogCtrl() {
    	super();
    }

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}
    
	/**
	 * On creating Window 
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ReportSearchTemplateDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ReportSearchTemplateDialog);

		if (arguments.containsKey("reportGenerationPromptDialogCtrl")) {
			this.reportGenerationPromptDialogCtrl = (ReportGenerationPromptDialogCtrl) arguments.get("reportGenerationPromptDialogCtrl");

		}
		if (arguments.containsKey("reportId")) {
			reportId = (Long) arguments.get("reportId");

		}
		this.window_ReportSearchTemplateDialog.doModal();
		logger.debug("Leaving" + event.toString());
	}
	/**
	 * on Click button "btnSaveTemplate"
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnSaveTemplate(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		dosaveTemplateName();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * This method calls reportGenerationPromptDialogCtrl's doSaveTemplate() method 
	 * @throws InterruptedException 
	 * @throws WrongValueException 
	 */
	private void dosaveTemplateName() throws WrongValueException, InterruptedException{
		logger.debug("Entering");
		if(("").equals(this.templateName.getValue().trim())){
			throw new WrongValueException( this.templateName,Labels.getLabel("FIELD_NO_EMPTY"
					,new String[] {Labels.getLabel("label_Template.label")}));

		}else{
			long templateUser = -1; 
			if(saveTemplateFor.getSelectedIndex() == 0){
				templateUser = getUserWorkspace().getLoggedInUser().getUserId();
			}
			boolean  isSaved = this.reportGenerationPromptDialogCtrl.doSaveTemplate(
					reportId ,templateUser,this.templateName.getValue());
			if(isSaved){
				Clients.showNotification(Labels.getLabel("label_SaveSucess"),  "info", null, null, -1);
				this.window_ReportSearchTemplateDialog.onClose();
			}
		}

		logger.debug("Leaving");	
	}



	/**
	 * on  Closing window 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_ReportSearchTemplateDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		this.window_ReportSearchTemplateDialog.onClose();
		logger.debug("Leaving" + event.toString());
	}
	//Getters and Setters 
	public void setReportConfigurationService(ReportConfigurationService reportConfigurationService) {
		this.reportConfigurationService = reportConfigurationService;
	}


	public ReportConfigurationService getReportConfigurationService() {
		return reportConfigurationService;
	}

}