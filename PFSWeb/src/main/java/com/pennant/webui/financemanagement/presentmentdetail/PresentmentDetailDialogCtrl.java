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
 * FileName    		:  PresentmentDetailDialogCtrl.java                                     * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-04-2017    														*
 *                                                                  						*
 * Modified Date    :  22-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-04-2017       PENNANT	                 0.1                                            * 
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
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.service.financemanagement.PresentmentDetailService;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pff.core.Literal;

/**
 * This is the controller class for the /WEB-INF/pages/financemanagement/PresentmentDetail/presentmentDetailDialog.zul
 * file. <br>
 */
public class PresentmentDetailDialogCtrl extends GFCBaseCtrl<PresentmentDetail> {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(PresentmentDetailDialogCtrl.class);

	protected Window window_PresentmentDetailDialog;
	private PresentmentDetail presentmentDetail;

	protected Button btn_AddExlude;
	protected Button btn_AddInclude;
	protected Listbox listBox_Include;
	protected Listbox listBox_ManualExclude;
	protected Listbox listBox_AutoExclude;
	protected ExtendedCombobox partnerBank;

	protected Tab includeTab;
	protected Tab manualExcludeTab;
	protected Tab autoExcludeTab;

	private Map<Long, PresentmentDetail> includeMap = new HashMap<Long, PresentmentDetail>();
	private Map<Long, PresentmentDetail> excludeMap = new HashMap<Long, PresentmentDetail>();
	List<PresentmentDetail> includeList = new ArrayList<PresentmentDetail>();

	private transient PresentmentDetailListCtrl presentmentDetailListCtrl;
	private transient PresentmentDetailService presentmentDetailService;
	private long presentmentId;

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
		return "";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_PresentmentDetailDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_PresentmentDetailDialog);

		try {
			// Get the required arguments.
			this.presentmentDetailListCtrl = (PresentmentDetailListCtrl) arguments.get("presentmentDetailListCtrl");
			this.presentmentId = (long) arguments.get("PresentmentId");
			
			doShowDialog(this.presentmentId);

		} catch (Exception e) {
			logger.error("Exception:", e);
			closeDialog();
			MessageUtil.showError(e.toString());
		}

		logger.debug(Literal.LEAVING);
	}

	
	private void doSetFieldProperties(){
		logger.debug(Literal.ENTERING);
		this.partnerBank.setMaxlength(LengthConstants.LEN_MASTER_CODE);
		this.partnerBank.setModuleName("PartnerBank");
		this.partnerBank.setValueColumn("PartnerBankId");
		this.partnerBank.setDescColumn("PartnerBankCode");
		this.partnerBank.setValidateColumns(new String[] { "PartnerBankCode" });
		this.partnerBank.setMandatoryStyle(true);
		
		this.listBox_Include.setHeight(getListBoxHeight(3));
		this.listBox_ManualExclude.setHeight(getListBoxHeight(3));
		this.listBox_AutoExclude.setHeight(getListBoxHeight(3));
		
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

		List<Long> excludeList = new ArrayList<Long>(this.excludeMap.keySet());
		List<Long> afterIncludeList = new ArrayList<Long>(this.includeMap.keySet());
		
		if (this.includeList != null && this.includeList.isEmpty()) {
			if (this.includeList.size() <= 0) {
				MessageUtil.showError(" No records are available in include list.");
				return;
			}
		}
		
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
		doSetValidations();
		this.presentmentDetailService.updatePresentmentDetails(excludeList, presentmentId, Long.valueOf(this.partnerBank.getValue()));

		refreshList();
		closeDialog();

		logger.debug(Literal.LEAVING);
	}

	private void doSetValidations() {
		Clients.clearWrongValue(this.partnerBank);
		this.partnerBank.setErrorMessage("");

		if (StringUtils.trimToNull(this.partnerBank.getValue()) == null) {
			throw new WrongValueException(this.partnerBank, " Partner Bank is mandatory.");
		}
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		presentmentDetailListCtrl.search();
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(false);
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
		doShowNotes(this.presentmentDetail);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param presentmentDetail
	 * 
	 */
	public void doWriteBeanToComponents(long presentmentId) {
		logger.debug(Literal.ENTERING);

		this.includeList = this.presentmentDetailService.getPresentmentDetailsList(presentmentId, true, "_AView");
		List<PresentmentDetail> excludeList = this.presentmentDetailService.getPresentmentDetailsList(presentmentId, false, "_AView");
		for (PresentmentDetail presentmentDetail : includeList) {
			this.includeMap.put(presentmentDetail.getId(), presentmentDetail);
		}
		doFillList(includeList, listBox_Include);
		doFillList(excludeList, listBox_AutoExclude);

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btn_AddExlude(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

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

		doFillList(new ArrayList<PresentmentDetail>(includeMap.values()), listBox_Include);
		doFillList(new ArrayList<PresentmentDetail>(excludeMap.values()), listBox_ManualExclude);

		this.autoExcludeTab.setSelected(true);
		this.manualExcludeTab.setSelected(true);
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btn_AddInclude(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

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

		doFillList(new ArrayList<PresentmentDetail>(includeMap.values()), listBox_Include);
		doFillList(new ArrayList<PresentmentDetail>(excludeMap.values()), listBox_ManualExclude);

		this.includeTab.setSelected(true);

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param presentmentId
	 *            The entity that need to be render.
	 */
	public void doShowDialog(long presentmentId) {
		logger.debug(Literal.LEAVING);

		doSetFieldProperties();

		doWriteBeanToComponents(presentmentId);

		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	public void doFillList(List<PresentmentDetail> presentmentDetailList, Listbox listbox) {
		logger.debug("Entering");

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

				lc = new Listcell(presentmentDetail.getFinTypeDesc());
				lc.setParent(item);

				lc = new Listcell(DateUtility.formatToLongDate(presentmentDetail.getSchDate()));
				lc.setParent(item);

				lc = new Listcell(PennantAppUtil.amountFormate(presentmentDetail.getPresentmentAmt(), format));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				lc = new Listcell(PennantStaticListUtil.getlabelDesc(presentmentDetail.getMandateType(),
						PennantStaticListUtil.getMandateTypeList()));
				lc.setParent(item);

				lc = new Listcell(PennantStaticListUtil.getlabelDesc(
						String.valueOf(presentmentDetail.getExcludeReason()),
						PennantStaticListUtil.getPresentmentExclusionList()));
				lc.setParent(item);

				item.setAttribute("data", presentmentDetail);

				listbox.appendChild(item);
			}
		} else {
			listbox.getItems().clear();
		}
		logger.debug("Leaving");
	}

	public void setPresentmentDetailService(PresentmentDetailService presentmentDetailService) {
		this.presentmentDetailService = presentmentDetailService;
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

	public PresentmentDetailListCtrl getPresentmentDetailListCtrl() {
		return presentmentDetailListCtrl;
	}

	public void setPresentmentDetailListCtrl(PresentmentDetailListCtrl presentmentDetailListCtrl) {
		this.presentmentDetailListCtrl = presentmentDetailListCtrl;
	}

	public PresentmentDetailService getPresentmentDetailService() {
		return presentmentDetailService;
	}

}
