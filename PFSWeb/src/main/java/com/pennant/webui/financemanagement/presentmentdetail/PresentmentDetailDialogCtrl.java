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
 * FileName    		:  presentmentHeaderDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-05-2017    														*
 *                                                                  						*
 * Modified Date    :  01-05-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-05-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.financemanagement.presentmentdetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.backend.service.financemanagement.PresentmentDetailService;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/financemanagement/PresentmentHeader/presentmentDetailDialog.zul
 * file. <br>
 */
public class PresentmentDetailDialogCtrl extends GFCBaseCtrl<PresentmentHeader> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(PresentmentDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_PresentmentHeaderDialog;
	private PresentmentHeader presentmentHeader;

	protected Button btn_AddExlude;
	protected Button btn_AddInclude;
	protected Listbox listBox_Include;
	protected Listbox listBox_ManualExclude;
	protected Listbox listBox_AutoExclude;
	protected ExtendedCombobox partnerBank;
	protected Label window_title;
	
	protected Label label_PresentmentReference;
	protected Label label_PresentmentStatus;
	
	// db Status fields
	protected Grid dBStatusGrid;
	protected Label label_TotalPresentments;
	protected Label label_SuccessPresentments;
	protected Label label_FailedPresentments;
	
	protected Listheader listheader_PresentmentDetail_Description;

	protected Tab includeTab;
	protected Tab manualExcludeTab;
	protected Tab autoExcludeTab;

	private Map<Long, PresentmentDetail> includeMap = new HashMap<Long, PresentmentDetail>();
	private Map<Long, PresentmentDetail> excludeMap = new HashMap<Long, PresentmentDetail>();
	List<PresentmentDetail> includeList = new ArrayList<PresentmentDetail>();

	private transient PresentmentDetailListCtrl presentmentDetailListCtrl;
	private transient PresentmentDetailService presentmentDetailService;

	private String moduleType;
	/**
	 * default constructor.<br>
	 */
	public PresentmentDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "PresentmentDetailDialog";
	}

	@Override
	protected String getReference() {
		StringBuffer referenceBuffer = new StringBuffer(String.valueOf(this.presentmentHeader.getId()));
		return referenceBuffer.toString();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_PresentmentHeaderDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_PresentmentHeaderDialog);

		try {
			// Get the required arguments.
			this.presentmentHeader = (PresentmentHeader) arguments.get("presentmentHeader");
			this.moduleType = (String) arguments.get("moduleType");
			this.presentmentDetailListCtrl = (PresentmentDetailListCtrl) arguments.get("presentmentDetailListCtrl");
			if (this.presentmentHeader == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}
			getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			
			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.presentmentHeader);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			closeDialog();
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		if ("E".equalsIgnoreCase(moduleType) || "A".equalsIgnoreCase(moduleType)) {
			this.listheader_PresentmentDetail_Description.setVisible(true);
		} else {
			this.listheader_PresentmentDetail_Description.setVisible(false);
		}
		this.partnerBank.setMaxlength(LengthConstants.LEN_MASTER_CODE);
		this.partnerBank.setModuleName("PartnerBank");
		this.partnerBank.setValueColumn("PartnerBankId");
		this.partnerBank.setValueType(DataType.LONG);
		this.partnerBank.setDescColumn("PartnerBankCode");
		this.partnerBank.setValidateColumns(new String[] { "PartnerBankId" });
		this.partnerBank.setMandatoryStyle(true);
		
		Filter[] filters = null;
		if (StringUtils.isNotEmpty(presentmentHeader.getEntityCode())) {
			filters = new Filter[2];
			filters[0] = new Filter("AlwReceipt", 1, Filter.OP_EQUAL);
			filters[1] = new Filter("Entity", presentmentHeader.getEntityCode(), Filter.OP_EQUAL);
		} else {
			filters = new Filter[1];
			filters[0] = new Filter("AlwReceipt", 1, Filter.OP_EQUAL);
		}
		this.partnerBank.setFilters(filters);	
			
		this.listBox_Include.setHeight(getListBoxHeight(5));
		this.listBox_ManualExclude.setHeight(getListBoxHeight(5));
		this.listBox_AutoExclude.setHeight(getListBoxHeight(4));

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		
		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
		if ("N".equalsIgnoreCase(moduleType)) {
			this.window_title.setValue(Labels.getLabel("lable_window_PresentmentBatchCreation_title"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_PresentmentDetailDialog_btnSave"));
			this.btn_AddExlude.setVisible(getUserWorkspace().isAllowed("button_PresentmentDetailDialog_btnExclude"));
			this.btn_AddInclude.setVisible(getUserWorkspace().isAllowed("button_PresentmentDetailDialog_btnInclude"));
			readOnlyComponent(isReadOnly("PresentmentDetailDialog_partnerBank"), this.partnerBank);
			this.partnerBank.setReadonly(!getUserWorkspace().isAllowed("PresentmentDetailDialog_partnerBank"));
		} else if ("A".equalsIgnoreCase(moduleType)) {
			this.window_title.setValue(Labels.getLabel("lable_window_PresentmentBatchApprove_title"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_PresentmentDetailDialog_btnSave"));
			this.btn_AddExlude.setVisible(false);
			this.btn_AddInclude.setVisible(false);
			readOnlyComponent(true, this.partnerBank);
		} else if ("E".equalsIgnoreCase(moduleType)) {
			this.window_title.setValue(Labels.getLabel("lable_window_PresentmentBatchEnquiry_title"));
			this.btnSave.setVisible(false);
			this.btn_AddExlude.setVisible(false);
			this.btn_AddInclude.setVisible(false);
			readOnlyComponent(true, this.partnerBank);
		}
		this.btnCancel.setVisible(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
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
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.presentmentHeader);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);
		presentmentDetailListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param presentmentHeader
	 * 
	 */
	public void doWriteBeanToComponents(PresentmentHeader aPresentmentHeader) {
		logger.debug(Literal.ENTERING);

		List<PresentmentDetail> excludeList = null;
		if(presentmentHeader.getPartnerBankId() != 0){
			this.partnerBank.setValue(String.valueOf(presentmentHeader.getPartnerBankId()));
			this.partnerBank.setDescription(presentmentHeader.getPartnerBankName());
		}
		
		this.label_PresentmentReference.setValue(presentmentHeader.getReference());
		this.label_PresentmentStatus.setValue(PennantStaticListUtil.getPropertyValue(
				PennantStaticListUtil.getPresentmentBatchStatusList(), presentmentHeader.getStatus()));
		
		Map<Long, PresentmentDetail> totExcludeMap = new HashMap<Long, PresentmentDetail>();
		boolean isApprove = "A".equals(moduleType);
		
		if (MandateConstants.TYPE_PDC.equals(aPresentmentHeader.getMandateType())) {
			this.includeList = this.presentmentDetailService.getPresentmentDetailsList(aPresentmentHeader.getId(), true, isApprove, "_PDCAView");
			excludeList = this.presentmentDetailService.getPresentmentDetailsList(aPresentmentHeader.getId(), false, isApprove, "_PDCAView");
		} else {
			this.includeList = this.presentmentDetailService.getPresentmentDetailsList(aPresentmentHeader.getId(), true, isApprove, "_AView");
			excludeList = this.presentmentDetailService.getPresentmentDetailsList(aPresentmentHeader.getId(), false, isApprove, "_AView");
		}
		for (PresentmentDetail presentmentDetail : includeList) {
			this.includeMap.put(presentmentDetail.getId(), presentmentDetail);
		}

		if (excludeList != null && !excludeList.isEmpty()) {
			for (PresentmentDetail presentmentDetail : excludeList) {
				if (RepayConstants.PEXC_MANUAL_EXCLUDE == presentmentDetail.getExcludeReason()) {
					this.excludeMap.put(presentmentDetail.getId(), presentmentDetail);
				} else {
					totExcludeMap.put(presentmentDetail.getId(), presentmentDetail);
				}
			}
		}
		doFillList(this.includeList, listBox_Include, false);
		doFillList(new ArrayList<PresentmentDetail>(totExcludeMap.values()), listBox_AutoExclude, true);
		doFillList(new ArrayList<PresentmentDetail>(this.excludeMap.values()), listBox_ManualExclude, true);
		
		if ("E".equals(moduleType)) {
			this.dBStatusGrid.setVisible(true);
			this.label_TotalPresentments.setValue(String.valueOf(aPresentmentHeader.getTotalRecords()));
			this.label_SuccessPresentments.setValue(String.valueOf(aPresentmentHeader.getSuccessRecords()));
			this.label_FailedPresentments.setValue(String.valueOf(aPresentmentHeader.getFailedRecords()));
		} else {
			this.dBStatusGrid.setVisible(false);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btn_AddExlude(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());

		Clients.clearWrongValue(this.listBox_Include);
		if (listBox_Include.getSelectedItems().isEmpty()) {
			MessageUtil.showError(" Please select at least one record. ");
			return;
		}

		for (Listitem listitem : this.listBox_Include.getSelectedItems()) {
			if (this.listBox_Include.getSelectedItems().contains(listitem)) {
				PresentmentDetail presentmentDetail = (PresentmentDetail) listitem.getAttribute("data");
				this.excludeMap.put(presentmentDetail.getId(), presentmentDetail);
				this.includeMap.remove(presentmentDetail.getId());
			}
		}

		doFillList(new ArrayList<PresentmentDetail>(includeMap.values()), listBox_Include, false);
		doFillList(new ArrayList<PresentmentDetail>(excludeMap.values()), listBox_ManualExclude, true);

		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onClick$btn_AddInclude(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());

		Clients.clearWrongValue(this.listBox_ManualExclude);
		if (listBox_ManualExclude.getSelectedItems().isEmpty()) {
			MessageUtil.showError(" Please select at least one record. ");
			return;
		}
		for (Listitem listitem : this.listBox_ManualExclude.getSelectedItems()) {
			if (this.listBox_ManualExclude.getSelectedItems().contains(listitem)) {
				PresentmentDetail presentmentDetail = (PresentmentDetail) listitem.getAttribute("data");
				this.includeMap.put(presentmentDetail.getId(), presentmentDetail);
				this.excludeMap.remove(presentmentDetail.getId());
			}
		}
		doFillList(new ArrayList<PresentmentDetail>(includeMap.values()), listBox_Include, false);
		doFillList(new ArrayList<PresentmentDetail>(excludeMap.values()), listBox_ManualExclude, true);

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aPresentmentHeader
	 */
	public void doWriteComponentsToBean(PresentmentHeader aPresentmentHeader) {
		logger.debug(Literal.LEAVING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if (StringUtils.trimToNull(this.partnerBank.getValue()) == null) {
				throw new WrongValueException(this.partnerBank, Labels.getLabel("FIELD_IS_MAND", new String[] { Labels.getLabel("label_PresentmentDetailList_Bank.value")}));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param presentmentHeader
	 *            The entity that need to be render.
	 */
	public void doShowDialog(PresentmentHeader presentmentHeader) {
		logger.debug(Literal.LEAVING);
		
		doEdit();
		doWriteBeanToComponents(presentmentHeader);
		try {
			setDialog(DialogType.EMBEDDED);
		} catch (UiException e){
			logger.error(Literal.EXCEPTION, e);
			this.window_PresentmentHeaderDialog.onClose();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);
		this.partnerBank.setConstraint("");
		this.partnerBank.setErrorMessage("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);
		this.partnerBank.setErrorMessage("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);
		
		if ("N".equalsIgnoreCase(moduleType)) {
			this.userAction.appendItem("Save", "Saved");
			this.userAction.appendItem("Submit", "Submit");
			this.userAction.appendItem("Cancel", "Cancel");
			this.userAction.setSelectedIndex(0);
		} else if ("A".equalsIgnoreCase(moduleType)) {
			this.userAction.appendItem("Approve", "Approved");
			this.userAction.appendItem("Resubmit", "Resubmited");
			this.listBox_Include.setMultiple(false);
			this.listBox_Include.setCheckmark(false);
			this.listBox_ManualExclude.setMultiple(false);
			this.listBox_ManualExclude.setCheckmark(false);
			this.userAction.setSelectedIndex(0);
		} else if ("E".equalsIgnoreCase(moduleType)) {
			this.userAction.setVisible(false);
			this.listBox_Include.setMultiple(false);
			this.listBox_Include.setMultiple(false);
			this.listBox_Include.setCheckmark(false);
			this.listBox_ManualExclude.setMultiple(false);
			this.listBox_ManualExclude.setCheckmark(false);
			this.groupboxWf.setVisible(false);
		}
		
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug(Literal.ENTERING);
		
		this.partnerBank.setValue("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);

		final PresentmentHeader aPresentmentHeader = new PresentmentHeader();
		BeanUtils.copyProperties(this.presentmentHeader, aPresentmentHeader);

		List<Long> excludeList = new ArrayList<Long>(this.excludeMap.keySet());
		List<Long> afterIncludeList = new ArrayList<Long>(this.includeMap.keySet());
		
		long partnerBankId = 0;
		String userAction = this.userAction.getSelectedItem().getLabel();

		if (!"Cancel".equals(userAction)) {
			if (this.includeList != null && this.includeList.isEmpty()) {
				if (this.includeList.size() <= 0) {
					MessageUtil.showError(" No records are available in include list.");
					return;
				}
			}

			if (afterIncludeList != null && afterIncludeList.isEmpty()) {
				if (afterIncludeList.size() <= 0) {
					MessageUtil.showError(" No records are available in include list. All records are moved to manual exlude.");
					return;
				}
			}
			doWriteComponentsToBean(aPresentmentHeader);
			partnerBankId = Long.valueOf(this.partnerBank.getValue());
		}
		try {
			boolean isPDC = MandateConstants.TYPE_PDC.equals(aPresentmentHeader.getMandateType());
			this.presentmentDetailService.updatePresentmentDetails(excludeList, afterIncludeList, userAction, aPresentmentHeader.getId(), partnerBankId, getUserWorkspace().getLoggedInUser(), isPDC);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}

		refreshList();
		closeDialog();
		
		logger.debug(Literal.LEAVING);
	}

	public void doFillList(List<PresentmentDetail> presentmentDetailList, Listbox listbox, boolean isExclude) {
		logger.debug(Literal.ENTERING);

		ArrayList<ValueLabel> excludeList = PennantStaticListUtil.getPresentmentExclusionList();
		ArrayList<ValueLabel> statusList = PennantStaticListUtil.getPresentmentsStatusList();
		
		if (presentmentDetailList != null && !presentmentDetailList.isEmpty()) {
			listbox.getItems().clear();
			Listcell lc;
			int format = 0;
			for (PresentmentDetail presentmentDetail : presentmentDetailList) {
				format = CurrencyUtil.getFormat(presentmentDetail.getFinCcy());
				Listitem item = new Listitem();
				lc = new Listcell(presentmentDetail.getCustomerName());
				lc.setParent(item);

				lc = new Listcell(presentmentDetail.getFinReference());
				lc.setParent(item);

				lc = new Listcell(presentmentDetail.getFinType());
				lc.setParent(item);

				lc = new Listcell(DateUtility.formatToLongDate(presentmentDetail.getSchDate()));
				lc.setParent(item);

				lc = new Listcell(PennantAppUtil.amountFormate(presentmentDetail.getAdvanceAmt(), format));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				
				lc = new Listcell(PennantAppUtil.amountFormate(presentmentDetail.getPresentmentAmt(), format));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				lc = new Listcell(presentmentDetail.getPresentmentRef());
				lc.setParent(item);
				
				if(MandateConstants.TYPE_PDC.equals(presentmentDetail.getMandateType())){
					lc = new Listcell(Labels.getLabel("label_Mandate_PDC"));
				} else {
					lc = new Listcell(presentmentDetail.getMandateType());
				}
				lc.setParent(item);
				if (isExclude) {
					lc = new Listcell(PennantStaticListUtil.getlabelDesc(String.valueOf(presentmentDetail.getExcludeReason()), excludeList));
					lc.setParent(item);
				} else {
					lc = new Listcell(PennantStaticListUtil.getlabelDesc(presentmentDetail.getStatus(), statusList));
					lc.setParent(item);
					if ("E".equalsIgnoreCase(moduleType) || "A".equalsIgnoreCase(moduleType)) {
						lc = new Listcell(presentmentDetail.getErrorDesc());
						lc.setParent(item);
					}
				}
				item.setAttribute("data", presentmentDetail);
				listbox.appendChild(item);
			}
		} else {
			listbox.getItems().clear();
		}
		logger.debug(Literal.LEAVING);
	}

	public Map<Long, PresentmentDetail> getIncludeMap() {
		return includeMap;
	}

	public void setIncludeMap(Map<Long, PresentmentDetail> includeMap) {
		this.includeMap = includeMap;
	}

	public Map<Long, PresentmentDetail> getExcludeMap() {
		return excludeMap;
	}

	public void setExcludeMap(Map<Long, PresentmentDetail> excludeMap) {
		this.excludeMap = excludeMap;
	}

	public PresentmentDetailService getPresentmentDetailService() {
		return presentmentDetailService;
	}

	public void setPresentmentDetailService(PresentmentDetailService presentmentDetailService) {
		this.presentmentDetailService = presentmentDetailService;
	}
}
