package com.pennant.pff.presentment.web;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.app.util.SysParamUtil;
import com.pennant.pff.presentment.model.DueExtractionConfig;
import com.pennant.pff.presentment.model.DueExtractionHeader;
import com.pennant.pff.presentment.model.InstrumentTypes;
import com.pennant.pff.presentment.service.DueExtractionConfigService;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class DueExtractionConfigDialogCtrl extends GFCBaseCtrl<InstrumentTypes> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(DueExtractionConfigDialogCtrl.class);

	protected Window windowDueExtractionConfigDialog;
	protected Datebox appDate;
	protected Listbox listBox;

	private DueExtractionHeader dueExtractionHeader;

	private transient DueExtractionConfigService dueExtractionConfigService;

	/**
	 * default constructor.<br>
	 */
	public DueExtractionConfigDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "DueExtractionConfigDialog";
	}

	@SuppressWarnings("unchecked")
	public void onCreate$windowDueExtractionConfigDialog(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(windowDueExtractionConfigDialog);

		try {

			doCheckRights();

			dueExtractionHeader = (DueExtractionHeader) arguments.get("DueExtractionHeader");
			// dueExtractionConfigListCtrl = (DueExtractionConfigListCtrl) arguments.get("DueExtractionConfigListCtrl");

			doLoadWorkFlow(dueExtractionHeader.isWorkflow(), dueExtractionHeader.getWorkflowId(),
					dueExtractionHeader.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}

			doShowDialog(this.dueExtractionHeader);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.windowDueExtractionConfigDialog.onClose();
		}
		logger.debug(Literal.LEAVING);
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnSave.setVisible(true);

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialog(DueExtractionHeader dueExtractionHeader) {
		logger.debug(Literal.ENTERING);

		if (isWorkFlowEnabled()) {
			if (StringUtils.isNotBlank(dueExtractionHeader.getRecordType())) {
				this.btnNotes.setVisible(true);
			}
		} else {
			this.btnCtrl.setInitEdit();
			btnCancel.setVisible(false);
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		dueExtractionHeader.setInstruments(dueExtractionConfigService.getInstrumentTypesMap());
		dueExtractionHeader.setConfig(dueExtractionConfigService.getDueExtractionConfig(dueExtractionHeader.getID()));

		doWriteBeanToComponents(dueExtractionHeader);

		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	private void doWriteBeanToComponents(DueExtractionHeader dueExtractionHeader) {
		logger.debug(Literal.ENTERING);

		this.appDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.appDate.setValue(SysParamUtil.getAppDate());
		this.appDate.setDisabled(true);

		doFillInstrumentDetails(listBox, dueExtractionHeader);

		logger.debug(Literal.LEAVING);
	}

	private void doFillInstrumentDetails(Listbox listbox, DueExtractionHeader dueExtractionHeader) {
		Listhead head = new Listhead();

		Listheader header = new Listheader("Due Date");
		header.setParent(head);

		Map<Long, InstrumentTypes> instruments = dueExtractionHeader.getInstruments();

		Set<Long> keySet = instruments.keySet();

		for (Long key : keySet) {
			header = new Listheader(instruments.get(key).getCode());
			header.setParent(head);
		}

		listbox.appendChild(head);

		List<DueExtractionConfig> dueExtractionConfig = dueExtractionHeader.getConfig();

		int index = 0;
		for (int i = 0; i < dueExtractionConfig.size(); i++) {
			DueExtractionConfig dueConfig = dueExtractionConfig.get(i);
			Listitem item = new Listitem();

			Listcell cell = new Listcell();

			Datebox datebox = new Datebox();
			if (i == 0) {
				cell = new Listcell("Configured Days");
				cell.setParent(item);
			} else {
				Date dueDate = dueConfig.getDueDate();

				datebox.setWidth("100px");
				datebox.setDisabled(true);
				datebox.setValue(dueDate);
				datebox.setFormat(DateFormat.SHORT_DATE.getPattern());

				cell.appendChild(datebox);
				cell.setParent(item);
			}

			for (Long key : keySet) {
				if (index == 0) {
					cell = new Listcell();
					Intbox intbox = new Intbox();
					intbox.setDisabled(true);
					intbox.setValue(instruments.get(key).getExtractionDays());
					cell.appendChild(intbox);
					cell.setParent(item);
					continue;
				}

				cell = new Listcell();

				datebox = new Datebox();
				datebox.setWidth("120px");
				datebox.setDisabled(instruments.get(key).getExtractionDays() == 0);
				datebox.setFormat(DateFormat.SHORT_DATE.getPattern());
				datebox.setValue(dueConfig.getExtractionDate());

				datebox.addForward(Events.ON_CHANGE, this.window, "onChangeExtractionDate");

				cell.appendChild(datebox);
				cell.setParent(item);
			}
			index++;

			listbox.appendChild(item);
		}
	}

	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	@Autowired
	public void setDueExtractionConfigService(DueExtractionConfigService dueExtractionConfigService) {
		this.dueExtractionConfigService = dueExtractionConfigService;
	}

}
