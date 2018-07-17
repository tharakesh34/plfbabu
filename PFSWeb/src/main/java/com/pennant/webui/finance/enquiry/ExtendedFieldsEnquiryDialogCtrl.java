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
 * FileName    		:  ScheduleEnquiryDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.enquiry;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.extendedfields.ExtendedFieldCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class ExtendedFieldsEnquiryDialogCtrl extends GFCBaseCtrl<Object> {

	private static final long serialVersionUID = 1050856461018302842L;
	private static final Logger logger = Logger.getLogger(ExtendedFieldsEnquiryDialogCtrl.class);

	protected Window window_ExtendedFieldsEnquiryDialog;

	protected Listbox listBox_ExtendedFields;
	protected Combobox eventName;
	protected Tab detailsTab;

	private Tabpanel tabPanel_dialogWindow;

	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	private int formatter = SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT);
	private FinanceEnquiry financeEnquiry;

	public ExtendedFieldsEnquiryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected financeMain object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ExtendedFieldsEnquiryDialog(ForwardEvent event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_ExtendedFieldsEnquiryDialog);

		if (event.getTarget().getParent().getParent() != null) {
			tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
		}

		if (arguments.containsKey("ccyFormatter")) {
			this.formatter = Integer.parseInt(arguments.get("ccyFormatter").toString());
		}

		if (arguments.containsKey("financeEnquiryHeaderDialogCtrl")) {
			this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) arguments
					.get("financeEnquiryHeaderDialogCtrl");
		}

		if (arguments.containsKey("finaceEnquiry")) {
			setFinanceEnquiry((FinanceEnquiry) arguments.get("finaceEnquiry"));
		}
		doShowDialog();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws InterruptedException
	 */
	public void doShowDialog() throws InterruptedException {
		logger.debug(Literal.ENTERING);
		try {
			if (tabPanel_dialogWindow != null) {
				getBorderLayoutHeight();
				int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount() * 20;
				this.listBox_ExtendedFields.setHeight(this.borderLayoutHeight - rowsHeight - 200 + "px");
				this.window_ExtendedFieldsEnquiryDialog.setHeight(this.borderLayoutHeight - rowsHeight - 30 + "px");
				tabPanel_dialogWindow.appendChild(this.window_ExtendedFieldsEnquiryDialog);
			}
			doFillEvents();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/*
	 * Filling the finance events
	 */
	private void doFillEvents() {
		fillComboBox(eventName, FinanceConstants.FINSER_EVENT_ORG,
				PennantStaticListUtil.getValueLabels(PennantStaticListUtil.getFinEvents(true)));
		onChangeEvent(this.eventName.getSelectedItem().getValue().toString());
	}

	public void onChange$eventName(Event event) {
		logger.debug(Literal.ENTERING);

		String eventName = this.eventName.getSelectedItem().getValue().toString();
		onChangeEvent(eventName);
		
		logger.debug(Literal.LEAVING);
	}

	private void onChangeEvent(String eventName) {

		this.listBox_ExtendedFields.getItems().clear();
		
		this.detailsTab.setLabel(this.eventName.getSelectedItem().getLabel());
		ExtendedFieldCtrl extendedFieldCtrl = new ExtendedFieldCtrl();
		String subModule = getFinanceEnquiry().getLovDescProductCodeName();
		ExtendedFieldHeader extendedFieldHeader = extendedFieldCtrl
				.getExtendedFieldHeader(ExtendedFieldConstants.MODULE_LOAN, subModule, eventName);

		if (extendedFieldHeader == null) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(ExtendedFieldConstants.MODULE_LOAN);
		sb.append("_");
		sb.append(subModule);
		sb.append("_");
		sb.append(PennantStaticListUtil.getFinEventCode(eventName));
		sb.append("_ED");
		try {
			List<ExtendedFieldRender> extendedFieldRenderList = extendedFieldCtrl
					.getExtendedFieldRenderList(getFinanceEnquiry().getFinReference(), sb.toString(), "_View");
			
			doFillExtendedDetails(extendedFieldRenderList, extendedFieldHeader);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	/**
	 * Method to fill the extendedField Render List Details
	 * 
	 * @param extendedFieldRenderList
	 * @param extendedFieldHeader
	 */
	private void doFillExtendedDetails(List<ExtendedFieldRender> extendedFieldRenderList,
			ExtendedFieldHeader extendedFieldHeader) {
		logger.debug(Literal.ENTERING);

		this.listBox_ExtendedFields.getItems().clear();
		if (CollectionUtils.isNotEmpty(extendedFieldRenderList)) {
			for (ExtendedFieldRender detail : extendedFieldRenderList) {
				Listitem item = new Listitem();
				Listcell lc;

				lc = new Listcell(detail.getReference());
				lc.setParent(item);

				lc = new Listcell(String.valueOf(detail.getSeqNo()));
				lc.setParent(item);

				lc = new Listcell(detail.getRecordStatus());
				lc.setParent(item);

				lc = new Listcell(PennantJavaUtil.getLabel(detail.getRecordType()));
				lc.setParent(item);

				item.setAttribute("renderObject", detail);
				item.setAttribute("headerObject", extendedFieldHeader);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onExtendedFieldDetailItemDoubleClicked");
				this.listBox_ExtendedFields.appendChild(item);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Double click the item
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onExtendedFieldDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug(Literal.ENTERING + event.toString());

		Listitem item = this.listBox_ExtendedFields.getSelectedItem();

		ExtendedFieldRender fieldRender = (ExtendedFieldRender) item.getAttribute("renderObject");
		ExtendedFieldHeader extendedFieldHeader = (ExtendedFieldHeader) item.getAttribute("headerObject");
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("extendedFieldHeader", extendedFieldHeader);
		map.put("extendedFieldRender", fieldRender);
		map.put("ccyFormat", formatter);
		map.put("isReadOnly", true);
		map.put("moduleType", PennantConstants.MODULETYPE_ENQ);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/SolutionFactory/ExtendedFieldDetail/ExtendedFieldCaptureDialog.zul",
					window_ExtendedFieldsEnquiryDialog, map);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING + event.toString());
	}
	
	 
	private void fillComboBox(Combobox combobox, String value, List<ValueLabel> list) {
		combobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		combobox.setReadonly(true);
		for (ValueLabel valueLabel : list) {
			comboitem = new Comboitem();
			comboitem.setValue(valueLabel.getValue());
			comboitem.setLabel(valueLabel.getLabel());
			combobox.appendChild(comboitem);
			if (StringUtils.trimToEmpty(value).equals(StringUtils.trim(valueLabel.getValue()))) {
				combobox.setSelectedItem(comboitem);
			}
		}
	}

	public FinanceEnquiry getFinanceEnquiry() {
		return financeEnquiry;
	}

	public void setFinanceEnquiry(FinanceEnquiry financeEnquiry) {
		this.financeEnquiry = financeEnquiry;
	}

}
