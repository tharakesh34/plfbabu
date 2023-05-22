/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : NPAProvisionHeaderListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 04-05-2020 * *
 * Modified Date : 04-05-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 04-05-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.npaprovisionheader;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.applicationmaster.NPAProvisionDetail;
import com.pennant.backend.model.applicationmaster.NPAProvisionHeader;
import com.pennant.backend.service.applicationmaster.NPAProvisionHeaderService;
import com.pennant.webui.applicationmaster.npaprovisionheader.model.NPAProvisionHeaderListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.TableType;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/NPAProvisionHeader/NPAProvisionHeaderList.zul
 * file.
 * 
 */
public class NPAProvisionHeaderListCtrl extends GFCBaseListCtrl<NPAProvisionHeader> {
	private static final long serialVersionUID = 1L;

	protected Window window_NPAProvisionHeaderList;
	protected Borderlayout borderLayout_NPAProvisionHeaderList;
	protected Paging pagingNPAProvisionHeaderList;
	protected Listbox listBoxNPAProvisionHeader;

	// List headers
	protected Listheader listheader_Entity;
	protected Listheader listheader_FinType;

	// checkRights
	protected Button button_NPAProvisionHeaderList_NewNPAProvisionHeader;
	protected Button button_NPAProvisionHeaderList_NPAProvisionHeaderSearch;

	// Search Fields
	protected Textbox entity;
	protected Textbox finType;

	protected Listbox sortOperator_Entity;
	protected Listbox sortOperator_FinType;

	private transient NPAProvisionHeaderService nPAProvisionHeaderService;

	/**
	 * default constructor.<br>
	 */
	public NPAProvisionHeaderListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "NPAProvisionHeader";
		super.pageRightName = "NPAProvisionHeaderList";
		super.tableName = "NPA_PROVISION_HEADER_AView";
		super.queueTableName = "NPA_PROVISION_HEADER_View";
		super.enquiryTableName = "NPA_PROVISION_HEADER_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_NPAProvisionHeaderList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_NPAProvisionHeaderList, borderLayout_NPAProvisionHeaderList, listBoxNPAProvisionHeader,
				pagingNPAProvisionHeaderList);
		setItemRender(new NPAProvisionHeaderListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_NPAProvisionHeaderList_NPAProvisionHeaderSearch);
		registerButton(button_NPAProvisionHeaderList_NewNPAProvisionHeader,
				"button_NPAProvisionHeaderList_NewNPAProvisionHeader", true);

		registerField("id");
		registerField("entity", listheader_Entity, SortOrder.NONE, entity, sortOperator_Entity, Operators.STRING);
		registerField("entityName");
		registerField("finType", listheader_FinType, SortOrder.NONE, finType, sortOperator_FinType, Operators.STRING);
		registerField("finTypeName");

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_NPAProvisionHeaderList_NPAProvisionHeaderSearch(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void onClick$button_NPAProvisionHeaderList_NewNPAProvisionHeader(Event event)
			throws IllegalAccessException, InvocationTargetException {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		NPAProvisionHeader nPAProvisionHeader = new NPAProvisionHeader();
		nPAProvisionHeader.setNewRecord(true);
		nPAProvisionHeader.setWorkflowId(getWorkFlowId());

		boolean isCopyProcess = false;
		if (event.getData() != null) {
			NPAProvisionHeader sourceFin = (NPAProvisionHeader) event.getData();
			BeanUtils.copyProperties(nPAProvisionHeader, sourceFin);
			nPAProvisionHeader.setFinType("");
			nPAProvisionHeader.setId(Long.MIN_VALUE);
			nPAProvisionHeader.setFinTypeName("");
			nPAProvisionHeader.setEntity("");
			nPAProvisionHeader.setEntityName("");
			nPAProvisionHeader.setNewRecord(true);
			nPAProvisionHeader.setRecordStatus("");
			nPAProvisionHeader.setWorkflowId(getWorkFlowId());
			isCopyProcess = true;
			List<NPAProvisionDetail> provisionDetailsList = sourceFin.getProvisionDetailsList();
			for (NPAProvisionDetail detail : provisionDetailsList) {
				detail.setId(Long.MIN_VALUE);
			}
			nPAProvisionHeader.setProvisionDetailsList(provisionDetailsList);
		}

		// Display the dialog page.
		Map<String, Object> arg = getDefaultArguments();
		arg.put("nPAProvisionHeader", nPAProvisionHeader);
		arg.put("nPAProvisionHeaderListCtrl", this);
		arg.put("isCopyProcess", isCopyProcess);
		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/NPAProvisionHeader/SelectNPAProvisionHeaderDialog.zul", null,
					arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onNPAProvisionHeaderItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		// Get the selected record.
		Listitem selectedItem = this.listBoxNPAProvisionHeader.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");

		NPAProvisionHeader npaprovisionheader = new NPAProvisionHeader();
		npaprovisionheader.setId(id);
		npaprovisionheader = nPAProvisionHeaderService.getNPAProvisionHeader(npaprovisionheader, TableType.VIEW);

		if (npaprovisionheader == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}
		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  AND  Id = ");
		whereCond.append(npaprovisionheader.getId());
		whereCond.append(" AND  version=");
		whereCond.append(npaprovisionheader.getVersion());

		if (doCheckAuthority(npaprovisionheader, whereCond.toString())) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && npaprovisionheader.getWorkflowId() == 0) {
				npaprovisionheader.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(npaprovisionheader);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param npaprovisionheader The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(NPAProvisionHeader npaprovisionheader) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("nPAProvisionHeader", npaprovisionheader);
		arg.put("nPAProvisionHeaderListCtrl", this);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/NPAProvisionHeader/NPAProvisionHeaderDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	/**
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 */
	public void onCheck$fromApproved(Event event) {
		search();
	}

	/**
	 * When user clicks on "fromWorkFlow"
	 * 
	 * @param event
	 */
	public void onCheck$fromWorkFlow(Event event) {
		search();
	}

	public void setNPAProvisionHeaderService(NPAProvisionHeaderService nPAProvisionHeaderService) {
		this.nPAProvisionHeaderService = nPAProvisionHeaderService;
	}
}