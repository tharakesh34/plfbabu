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
 * FileName    		:  LegalECDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-06-2018    														*
 *                                                                  						*
 * Modified Date    :  19-06-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-06-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.legal.legalecdetail;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.legal.LegalECDetail;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Legal/LegalECDetail/legalECDetailDialog.zul file. <br>
 */
public class LegalDecisionDialogCtrl extends GFCBaseCtrl<LegalECDetail> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(LegalDecisionDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_LegalDecisionDialog;
	protected Longbox legalReference;
	protected Textbox remarks;
	private LegalECDetail legalECDetail; // overhanded per param

	/*
	 * private transient LegalECDetailListCtrl legalECDetailListCtrl; //
	 * overhanded per param private transient LegalECDetailService
	 * legalECDetailService;
	 */

	/**
	 * default constructor.<br>
	 */
	public LegalDecisionDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "LegalECDetailDialog";
	}

	@Override
	protected String getReference() {
		StringBuffer referenceBuffer = new StringBuffer(String.valueOf(this.legalECDetail.getLegalECId()));
		return referenceBuffer.toString();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that
	 * the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_LegalDecisionDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_LegalDecisionDialog);

		try {
			// Get the required arguments.
			this.legalECDetail = (LegalECDetail) arguments.get("legalECDetail");
			// this.legalECDetailListCtrl = (LegalECDetailListCtrl)
			// arguments.get("legalECDetailListCtrl");

			if (this.legalECDetail == null) {
				legalECDetail = new LegalECDetail();
				//throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			LegalECDetail legalECDetail = new LegalECDetail();
			BeanUtils.copyProperties(this.legalECDetail, legalECDetail);
			this.legalECDetail.setBefImage(legalECDetail);

			// Render the page and display the data.
			doLoadWorkFlow(this.legalECDetail.isWorkflow(), this.legalECDetail.getWorkflowId(),
					this.legalECDetail.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}
			doCheckRights();
			doSetFieldProperties();
			doShowDialog(this.legalECDetail);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param legalPropertyTitle
	 *            The entity that need to be render.
	 */
	public void doShowDialog(LegalECDetail legalECDetail) {
		logger.debug(Literal.LEAVING);

		if (legalECDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			//this.title.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(legalECDetail.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				//this.title.focus();
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				//doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		//doWriteBeanToComponents(legalPropertyTitle);
		
		this.btnSave.setVisible(true);
		setDialog(DialogType.MODAL);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_LegalPropertyTitleDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_LegalPropertyTitleDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_LegalPropertyTitleDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_LegalPropertyTitleDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}
	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.legalECDetail.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);

		}

		/*readOnlyComponent(isReadOnly("LegalPropertyTitleDialog_LegalReference"), this.legalReference);
		readOnlyComponent(isReadOnly("LegalPropertyTitleDialog_Title"), this.title);
*/
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.legalECDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.legalReference.setMaxlength(19);
		this.remarks.setMaxlength(1000);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

}
