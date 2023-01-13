package com.pennant.webui.settlement;

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
import com.pennant.backend.model.settlement.SettlementSchedule;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
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

	public void doRenderSettlementSchList(List<SettlementSchedule> settlementScheduleList,
			Listbox listBoxSettlementScheduleInlineEdit) {
		// render start
		listBoxSettlementScheduleInlineEdit.getItems().clear();
		if (CollectionUtils.isNotEmpty(settlementScheduleList)) {
			int listCount = 0;
			for (SettlementSchedule settlementSchedule : settlementScheduleList) {
				listCount = listCount + 1;
				doFillSettlementSchedule(settlementSchedule, listBoxSettlementScheduleInlineEdit, listCount);
			}
		}
	}

	public void doFillSettlementSchedule(SettlementSchedule settlementSchedule, Listbox listbox, int listCount) {
		logger.debug(Literal.ENTERING);
		Space space = null;
		Hbox hbox = null;
		Listitem item = new Listitem();
		Listcell cellSrNo = new Listcell();
		Listcell cellSettlementInstalDate = new Listcell();
		Listcell cellSettAmount = new Listcell();
		Listcell cellRecordType = new Listcell();
		Listcell cellDelete = new Listcell();

		// ********************* Serial Number
		Textbox srNo = new Textbox();
		srNo.setId("SrNo" + listCount);
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		srNo.setReadonly(getUserWorkspace().isReadOnly("EscrowMaintenanceDialog_TranReference"));
		srNo.setValue(String.valueOf(listCount));
		hbox.appendChild(space);
		hbox.appendChild(srNo);
		cellSrNo.appendChild(hbox);

		// **************** Settlement Installment Date
		Datebox settlementInstalDate = new Datebox();
		settlementInstalDate.setId("SettlementInstalDate" + listCount);
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		space.setSclass(PennantConstants.mandateSclass);
		// tranDate.setFormat(PennantConstants.dateFormat);
		settlementInstalDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		settlementInstalDate.setDisabled(false); // getUserWorkspace().isReadOnly("EscrowMaintenanceDialog_TranDate"));
		settlementInstalDate.setValue(settlementSchedule.getSettlementInstalDate());
		hbox.appendChild(space);
		hbox.appendChild(settlementInstalDate);
		cellSettlementInstalDate.appendChild(hbox);

		// ********************* Transaction Amount
		CurrencyBox settlementAmount = new CurrencyBox();
		settlementAmount.setId("SettlementAmount" + listCount);
		hbox = new Hbox();
		settlementAmount.setProperties(false, PennantConstants.defaultCCYDecPos);
		settlementAmount.setReadonly(false); // getUserWorkspace().isReadOnly("EscrowMaintenanceDialog_TranAmount"));
		settlementAmount.setValue(settlementSchedule.getSettlementAmount());
		hbox.appendChild(settlementAmount);
		cellSettAmount.appendChild(hbox);
		Object[] tranData = new Object[1];
		tranData[0] = cellSettAmount;

		// ******************

		cellRecordType.setLabel(PennantJavaUtil.getLabel(settlementSchedule.getRecordType()));
		// Delete action
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		Button button = new Button();
		button.setSclass("z-toolbarbutton");
		button.setLabel(Labels.getLabel("btnDelete.label"));
		button.setDisabled(false); // getUserWorkspace().isReadOnly("button_EscrowMaintenanceDialog_EscrowBtnDelete"));
		button.addForward("onClick", self, "onClickSettlementScheduleButtonDelete", item);
		hbox.appendChild(space);
		hbox.appendChild(button);
		cellDelete.appendChild(hbox);
		// set parent
		cellSrNo.setParent(item);
		cellSettlementInstalDate.setParent(item);
		cellSettAmount.setParent(item);
		cellRecordType.setParent(item);
		cellDelete.setParent(item);

		item.setAttribute("data", settlementSchedule);
		listbox.appendChild(item);

		if (PennantConstants.RECORD_TYPE_DEL.equals(settlementSchedule.getRecordType())) {
			doReadOnly(item);
		}

		List<Object> list = new ArrayList<Object>(3);
		list.add(settlementInstalDate);
		list.add(settlementAmount);

		settlementInstalDate.addForward("onChange", self, "onChangesettlementInstalDate", list);
		settlementAmount.addForward("onFulfill", self, "onFulfillsettlementAmount", list);
		settlementInstalDate.setAttribute("data", settlementSchedule);
		settlementAmount.setAttribute("data", settlementSchedule);
		if (!settlementSchedule.isNewRecord()) {
			settlementInstalDate.setAttribute("cellRecordType", cellRecordType);
			settlementAmount.setAttribute("cellRecordType", cellRecordType);
		}
		logger.debug(Literal.LEAVING);
	}

	public Map<String, List> preparesettlementScheduleData(Listbox listbox,
			List<SettlementSchedule> settlementScheduleList, long settlementHeaderID) {
		logger.debug(Literal.ENTERING);
		List<Listitem> listItems = listbox.getItems();
		List<SettlementSchedule> settlementSchedule = new ArrayList<>();
		ArrayList<WrongValueException> wve = new ArrayList<>();
		Map<String, List> settlementScheduleData = new HashMap<>();

		if (CollectionUtils.isEmpty(listItems)) {
			return settlementScheduleData;
		}

		for (Listitem listItem : listItems) {
			SettlementSchedule aSettlementSchedule = (SettlementSchedule) listItem.getAttribute("data");

			if (settlementSchedule == null) {
				continue;
			}

			String regEx = "";
			aSettlementSchedule.setSettlementHeaderID(settlementHeaderID);

			List<Component> listCells = listItem.getChildren();
			// Settlement Installment Date
			try {
				Datebox installmentDate = (Datebox) listCells.get(1).getChildren().get(0).getLastChild();
				aSettlementSchedule.setSettlementInstalDate(installmentDate.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				// Settlement Amount
				CurrencyBox settlementAmount = (CurrencyBox) listCells.get(2).getChildren().get(0).getLastChild();
				aSettlementSchedule.setSettlementAmount(settlementAmount.getActualValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			SettlementSchedule oldData = isINexsistingList(aSettlementSchedule, settlementScheduleList);
			if (oldData == null) {
				aSettlementSchedule.setVersion(aSettlementSchedule.getVersion() + 1);
				aSettlementSchedule.setRecordType(PennantConstants.RCD_ADD);
			} else {
				isRecordUpdated(aSettlementSchedule, settlementScheduleList);
			}
			settlementSchedule.add(aSettlementSchedule);
		}

		settlementScheduleData.put("errorList", wve);
		settlementScheduleData.put("settlementSchedule", settlementSchedule);

		logger.debug(Literal.LEAVING);
		return settlementScheduleData;
	}

	/**
	 * This method will perform the delete operation
	 * 
	 * @param listbox
	 * @param listitem
	 * @param isFinanceProcess
	 */
	public void doDelete(Listbox listbox, Listitem listitem) {

		if (listitem != null && listitem.getAttribute("data") != null) {
			SettlementSchedule settlementSchedule = (SettlementSchedule) listitem.getAttribute("data");
			Listcell cellRecordType = (Listcell) listitem.getChildren().get(3);

			String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record");
			msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
					+ Labels.getLabel("label_SrNo") + " : " + (listitem.getIndex() + 1);
			// Show a confirm box
			if (MessageUtil.confirm(msg) == MessageUtil.YES) {
				doReadOnly(listitem);
				if (settlementSchedule.isNewRecord()) {
					listbox.removeChild(listitem);
					return;
				} else if (StringUtils.isBlank(settlementSchedule.getRecordType())) {
					settlementSchedule.setVersion(settlementSchedule.getVersion() + 1);
					settlementSchedule.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				} else if (PennantConstants.RECORD_TYPE_UPD.equals(settlementSchedule.getRecordType())) {
					settlementSchedule.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (PennantConstants.RECORD_TYPE_NEW.equals(settlementSchedule.getRecordType())) {
					settlementSchedule.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				}
				cellRecordType.setLabel(PennantJavaUtil.getLabel(settlementSchedule.getRecordType()));
			}
		}

	}

	private SettlementSchedule isINexsistingList(SettlementSchedule schedule,
			List<SettlementSchedule> settlementSchedules) {
		if (CollectionUtils.isNotEmpty(settlementSchedules)) {
			for (SettlementSchedule settlementSchedule : settlementSchedules) {
				if (schedule.getSettlementDetailID() == settlementSchedule.getSettlementDetailID()) {
					return settlementSchedule;
				}
			}
		}
		return null;
	}

	private void isRecordUpdated(SettlementSchedule schedule, List<SettlementSchedule> list) {
		if (CollectionUtils.isEmpty(list)) {
			return;
		}

		for (SettlementSchedule cp : list) {
			if (PennantConstants.RECORD_TYPE_DEL.equals(cp.getRecordType())
					|| PennantConstants.RECORD_TYPE_CAN.equals(cp.getRecordType()) || schedule.isNewRecord()) {
				continue;
			}
			long settlementID = schedule.getSettlementDetailID();
			long aSettlementID = cp.getSettlementDetailID();
			if (settlementID == aSettlementID) {
				if (StringUtils.isBlank(schedule.getRecordType())) {
					schedule.setNewRecord(true);

					schedule.setVersion(schedule.getVersion() + 1);
					schedule.setRecordType(PennantConstants.RCD_UPD);
				}
			}
		}
	}

	/**
	 * Setting the components to read only
	 * 
	 * @param listitem
	 */
	private void doReadOnly(Listitem listitem) {
		List<Component> listCells = listitem.getChildren();
		Button delete = (Button) listCells.get(4).getChildren().get(0).getLastChild();
		Datebox datebox = (Datebox) listCells.get(1).getChildren().get(0).getLastChild();
		CurrencyBox currencyBox = (CurrencyBox) listCells.get(2).getChildren().get(0).getLastChild();
		delete.setDisabled(true);
		datebox.setReadonly(true);
		currencyBox.setDisabled(true);
	}

}
