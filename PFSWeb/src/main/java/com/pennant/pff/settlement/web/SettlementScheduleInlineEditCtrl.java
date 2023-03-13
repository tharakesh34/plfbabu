package com.pennant.pff.settlement.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;

import com.pennant.CurrencyBox;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.pff.settlement.model.SettlementSchedule;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class SettlementScheduleInlineEditCtrl extends GFCBaseCtrl<SettlementSchedule> {
	private static final Logger logger = LogManager.getLogger(SettlementScheduleInlineEditCtrl.class);

	private static final long serialVersionUID = -1289772081447044673L;

	public SettlementScheduleInlineEditCtrl() {
		super();
	}

	public void doRenderSettlementSchList(List<SettlementSchedule> schedules, Listbox listBox) {
		listBox.getItems().clear();

		int listCount = 0;
		for (SettlementSchedule schedule : schedules) {
			listCount = listCount + 1;
			doFillSettlementSchedule(schedule, listBox, listCount);
		}
	}

	public void doFillSettlementSchedule(SettlementSchedule schedule, Listbox listbox, int listCount) {
		logger.debug(Literal.ENTERING);

		Listitem item = new Listitem();

		appendSerialNumber(listCount, item);
		appendInstDate(schedule, listCount, item);
		appendTransactionAmount(schedule, listCount, item);
		appenedRecordType(schedule, item);
		appendDeleteAction(listbox, item);

		item.setAttribute("data", schedule);
		if (PennantConstants.RECORD_TYPE_DEL.equals(schedule.getRecordType())) {
			doReadOnly(item);
		}

		logger.debug(Literal.LEAVING);
	}

	public Map<String, List> preparesettlementSchdData(Listbox listbox, List<SettlementSchedule> sSchedules,
			long headerID) {

		logger.debug(Literal.ENTERING);

		List<Listitem> listItems = listbox.getItems();
		List<SettlementSchedule> settlementSchedule = new ArrayList<>();
		List<WrongValueException> wve = new ArrayList<>();

		Map<String, List> settlementScheduleData = new HashMap<>();

		if (CollectionUtils.isEmpty(listItems)) {
			return settlementScheduleData;
		}

		for (Listitem listItem : listItems) {
			SettlementSchedule schedule = (SettlementSchedule) listItem.getAttribute("data");

			if (schedule == null) {
				continue;
			}

			schedule.setSettlementHeaderID(headerID);

			List<Component> listCells = listItem.getChildren();
			try {
				schedule.setSettlementInstalDate(
						((Datebox) listCells.get(1).getChildren().get(0).getLastChild()).getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				schedule.setSettlementAmount(
						((CurrencyBox) listCells.get(2).getChildren().get(0).getLastChild()).getActualValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			SettlementSchedule oldData = isINexsistingList(schedule, sSchedules);
			if (oldData == null) {
				schedule.setVersion(schedule.getVersion() + 1);
				schedule.setRecordType(PennantConstants.RCD_ADD);
			} else {
				isRecordUpdated(schedule, sSchedules);
			}

			settlementSchedule.add(schedule);
		}

		settlementScheduleData.put("errorList", wve);
		settlementScheduleData.put("settlementSchedule", settlementSchedule);

		logger.debug(Literal.LEAVING);
		return settlementScheduleData;
	}

	public void doDelete(Listbox listbox, Listitem listitem) {
		if (listitem == null || listitem.getAttribute("data") == null) {
			return;
		}

		SettlementSchedule schedule = (SettlementSchedule) listitem.getAttribute("data");
		Listcell listCell = (Listcell) listitem.getChildren().get(3);

		String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_SrNo") + " : " + (listitem.getIndex() + 1);

		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			doReadOnly(listitem);
			if (schedule.isNewRecord()) {
				listbox.removeChild(listitem);
				return;
			} else if (StringUtils.isBlank(schedule.getRecordType())) {
				schedule.setVersion(schedule.getVersion() + 1);
				schedule.setRecordType(PennantConstants.RECORD_TYPE_DEL);

			} else if (PennantConstants.RECORD_TYPE_UPD.equals(schedule.getRecordType())) {
				schedule.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			} else if (PennantConstants.RECORD_TYPE_NEW.equals(schedule.getRecordType())) {
				schedule.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			listCell.setLabel(PennantJavaUtil.getLabel(schedule.getRecordType()));
		}
	}

	private SettlementSchedule isINexsistingList(SettlementSchedule schedule, List<SettlementSchedule> schedules) {
		for (SettlementSchedule exSchedule : schedules) {
			if (schedule.getSettlementDetailID() == exSchedule.getSettlementDetailID()) {
				return exSchedule;
			}
		}

		return null;
	}

	private void isRecordUpdated(SettlementSchedule schedule, List<SettlementSchedule> list) {
		String sRecordType = schedule.getRecordType();
		long detailID = schedule.getSettlementDetailID();

		for (SettlementSchedule cp : list) {
			String recordType = cp.getRecordType();
			if (PennantConstants.RECORD_TYPE_DEL.equals(recordType)
					|| PennantConstants.RECORD_TYPE_CAN.equals(recordType) || schedule.isNewRecord()) {
				continue;
			}

			if (detailID == cp.getSettlementDetailID() && StringUtils.isBlank(sRecordType)) {
				schedule.setNewRecord(true);
				schedule.setVersion(schedule.getVersion() + 1);
				schedule.setRecordType(PennantConstants.RCD_UPD);
			}
		}
	}

	private void doReadOnly(Listitem listitem) {
		List<Component> listCells = listitem.getChildren();
		Button delete = (Button) listCells.get(4).getChildren().get(0).getLastChild();
		Datebox datebox = (Datebox) listCells.get(1).getChildren().get(0).getLastChild();
		CurrencyBox currencyBox = (CurrencyBox) listCells.get(2).getChildren().get(0).getLastChild();
		delete.setDisabled(true);
		datebox.setReadonly(true);
		currencyBox.setDisabled(true);
	}

	private void appendSerialNumber(int listCount, Listitem item) {
		Listcell listCell = new Listcell();

		Textbox srNo = new Textbox();
		srNo.setId("SrNo" + listCount);

		Hbox hbox = new Hbox();

		Space space = new Space();
		space.setSpacing("2px");
		srNo.setReadonly(getUserWorkspace().isReadOnly("EscrowMaintenanceDialog_TranReference"));
		srNo.setValue(String.valueOf(listCount));

		hbox.appendChild(space);
		hbox.appendChild(srNo);
		listCell.appendChild(hbox);

		item.appendChild(listCell);
	}

	private void appendInstDate(SettlementSchedule schedule, int listCount, Listitem item) {
		Listcell listCell = new Listcell();

		Datebox dateBox = new Datebox();
		dateBox.setId("SettlementInstalDate" + listCount);
		dateBox.setFormat(DateFormat.SHORT_DATE.getPattern());
		dateBox.setDisabled(false);
		dateBox.setValue(schedule.getSettlementInstalDate());

		if ((PennantConstants.RECORD_TYPE_NEW.equals(schedule.getRecordType())
				&& PennantConstants.RCD_STATUS_SUBMITTED.equals(schedule.getRecordStatus()))
				|| PennantConstants.RCD_STATUS_APPROVED.equals(schedule.getRecordStatus())
				|| FinanceConstants.SETTLEMENT_CANCEL.equals(schedule.getModule())) {
			dateBox.setDisabled(true);
		}

		Hbox hbox = new Hbox();
		Space space = new Space();
		space.setSpacing("2px");
		space.setSclass(PennantConstants.mandateSclass);

		hbox.appendChild(space);
		hbox.appendChild(dateBox);
		listCell.appendChild(hbox);

		dateBox.setAttribute("data", schedule);
		dateBox.addForward("onChange", self, "onChangesettlementInstalDate");

		item.appendChild(listCell);

		if (!dateBox.isReadonly()) {
			dateBox.setConstraint(new PTDateValidator(Labels.getLabel("label_SettlementDialog_StartDate.value"), true,
					true, null, false));
		}
	}

	private void appendTransactionAmount(SettlementSchedule schedule, int listCount, Listitem item) {
		Listcell listCell = new Listcell();

		CurrencyBox amount = new CurrencyBox();
		amount.setId("SettlementAmount" + listCount);
		amount.setProperties(false, PennantConstants.defaultCCYDecPos);
		amount.setReadonly(false);
		amount.setValue(schedule.getSettlementAmount());
		amount.setAttribute("data", schedule);
		amount.addForward("onFulfill", self, "onFulfillsettlementAmount");

		if ((PennantConstants.RECORD_TYPE_NEW.equals(schedule.getRecordType())
				&& PennantConstants.RCD_STATUS_SUBMITTED.equals(schedule.getRecordStatus()))
				|| PennantConstants.RCD_STATUS_APPROVED.equals(schedule.getRecordStatus())
				|| FinanceConstants.SETTLEMENT_CANCEL.equals(schedule.getModule())) {
			amount.setReadonly(true);
		}

		Hbox hbox = new Hbox();
		hbox.appendChild(amount);
		listCell.appendChild(hbox);

		item.appendChild(listCell);

		if (!amount.isReadonly()) {
			amount.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_SettlementDialog_SettlementAmount.value"),
							PennantConstants.defaultCCYDecPos, true, false));

		}

	}

	private void appenedRecordType(SettlementSchedule schedule, Listitem item) {
		Listcell listCell = new Listcell();
		listCell.setLabel(PennantJavaUtil.getLabel(schedule.getRecordType()));
		item.appendChild(listCell);
	}

	private void appendDeleteAction(Listbox listbox, Listitem item) {
		Listcell listCell = new Listcell();

		Space space = new Space();
		space.setSpacing("2px");

		Button button = new Button();
		button.setSclass("z-toolbarbutton");
		button.setLabel(Labels.getLabel("btnDelete.label"));
		button.setDisabled(false);
		button.addForward("onClick", self, "onClickSettlementScheduleButtonDelete", item);

		Hbox hbox = new Hbox();
		hbox.appendChild(space);
		hbox.appendChild(button);
		listCell.appendChild(hbox);

		item.appendChild(listCell);

		listbox.appendChild(item);
	}
}
