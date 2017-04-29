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

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.service.financemanagement.PresentmentDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantStaticListUtil;
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

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_PresentmentDetailDialog;
	private PresentmentDetail presentmentDetail;

	protected Button btn_AddExlude;
	protected Button btn_AddInclude;
	protected Listbox listBox_Include;
	protected Listbox listBox_Exclude;

	private List<PresentmentDetail> presentmentDetailList = new ArrayList<>();

	private Map<Long, PresentmentDetail> includeMap = new HashMap<Long, PresentmentDetail>();
	private Map<Long, PresentmentDetail> excludeMap = new HashMap<Long, PresentmentDetail>();
	private Map<Long, PresentmentDetail> resultMap = new HashMap<Long, PresentmentDetail>();

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
	@SuppressWarnings("unchecked")
	public void onCreate$window_PresentmentDetailDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_PresentmentDetailDialog);

		try {
			// Get the required arguments.
			this.presentmentDetailList = (List<PresentmentDetail>) arguments.get("presentmentDetailList");
			this.presentmentDetailListCtrl = (PresentmentDetailListCtrl) arguments.get("presentmentDetailListCtrl");
			this.presentmentId = (long) arguments.get("PresentmentId");
			doShowDialog(this.presentmentDetailList);
		} catch (Exception e) {
			logger.error("Exception:", e);
			closeDialog();
			MessageUtil.showError(e.toString());
		}

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
		
		List<Long> list = new ArrayList<Long>();
		for (Long id : resultMap.keySet()) {
			if (!includeMap.containsKey(id)) {
				list.add(id);
			}
		}
		this.presentmentDetailService.updatePresentmentDetails(list, presentmentId);
		
		closeDialog();
		
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
		doShowNotes(this.presentmentDetail);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param presentmentDetail
	 * 
	 */
	public void doWriteBeanToComponents(List<PresentmentDetail> aPresentmentDetailsList) {
		logger.debug(Literal.ENTERING);

		for (PresentmentDetail presentmentDetail : aPresentmentDetailsList) {
			if (presentmentDetail.getExcludeReason() == 0) {
				includeMap.put(presentmentDetail.getId(), presentmentDetail);
			} else {
				excludeMap.put(presentmentDetail.getId(), presentmentDetail);
			}
		}
		resultMap.putAll(includeMap);
		
		doFillList(new ArrayList<PresentmentDetail>(includeMap.values()), listBox_Include);
		doFillList(new ArrayList<PresentmentDetail>(excludeMap.values()), listBox_Exclude);

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btn_AddExlude(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		Clients.clearWrongValue(this.listBox_Include);
		if (listBox_Include.getSelectedItems().isEmpty()) {
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
		doFillList(new ArrayList<PresentmentDetail>(excludeMap.values()), listBox_Exclude);

		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btn_AddInclude(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		Clients.clearWrongValue(this.listBox_Exclude);
		if (listBox_Exclude.getSelectedItems().isEmpty()) {
			return;
		}

		for (Listitem listitem : this.listBox_Exclude.getSelectedItems()) {
			if (this.listBox_Exclude.getSelectedItems().contains(listitem)) {
				PresentmentDetail presentmentDetail = (PresentmentDetail) listitem.getAttribute("data");
				this.includeMap.put(presentmentDetail.getId(), presentmentDetail);
				this.excludeMap.remove(presentmentDetail.getId());
			}
		}

		doFillList(new ArrayList<PresentmentDetail>(includeMap.values()), listBox_Include);
		doFillList(new ArrayList<PresentmentDetail>(excludeMap.values()), listBox_Exclude);

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param presentmentDetailList
	 *            The entity that need to be render.
	 */
	public void doShowDialog(List<PresentmentDetail> presentmentDetailList) {
		logger.debug(Literal.LEAVING);

		this.listBox_Exclude.setHeight(this.borderLayoutHeight - 145 + "px");
		this.listBox_Include.setHeight(this.borderLayoutHeight - 145 + "px");

		doWriteBeanToComponents(presentmentDetailList);

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
			for (PresentmentDetail presentmentDetail : presentmentDetailList) {
				Listitem item = new Listitem();
				lc = new Listcell(presentmentDetail.getCustomerName());
				lc.setParent(item);

				lc = new Listcell(presentmentDetail.getFinReference());
				lc.setParent(item);

				lc = new Listcell(presentmentDetail.getFinTypeDesc());
				lc.setParent(item);

				lc = new Listcell(DateUtility.formatToLongDate(presentmentDetail.getSchDate()));
				lc.setParent(item);

				lc = new Listcell(PennantApplicationUtil.formatRate(
						presentmentDetail.getPresentmentAmt().doubleValue(), 9));
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
