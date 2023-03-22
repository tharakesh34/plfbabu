package com.pennant.webui.finance.financemain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.tds.receivables.TdsReceivablesTxn;
import com.pennant.backend.service.tandetails.TanAssignmentService;
import com.pennant.backend.service.tds.receivables.TdsReceivablesTxnService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.finance.tds.cerificate.model.TanAssignment;
import com.pennanttech.finance.tds.cerificate.model.TanDetail;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.TableType;

public class TanDetailListCtrl extends GFCBaseCtrl<TanAssignment> {
	private static final long serialVersionUID = 4157448822555239535L;
	private static final Logger logger = LogManager.getLogger(TanDetailListCtrl.class);
	protected Window window_TanDetailList;

	protected Button btnAdd_AddTanDetail;
	protected Listbox listBoxTanDetail;
	protected Groupbox finBasicdetails;
	private Tab parentTab = null;

	private TanAssignment tanAssignment;
	private FinanceDetail financeDetail;
	private String finReference = "";
	private long custId = 0;
	private long listCount = 0;

	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private Object financeMainDialogCtrl;
	private TanAssignmentService tanAssignmentService;

	private List<TanAssignment> tanAssiginmentList = new ArrayList<>();
	private List<TanAssignment> deleteTanAssiginmentList = new ArrayList<>();
	private List<TanAssignment> tanNumberList = new ArrayList<>();
	private TdsReceivablesTxnService tdsReceivablesTxnService;

	public TanDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "TanDetailList";
		super.moduleCode = "TanAssignment";
	}

	@SuppressWarnings("unchecked")
	public void onCreate$window_TanDetailList(ForwardEvent event) throws Exception {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_TanDetailList);

		try {

			if (event.getTarget().getParent() != null) {
				event.getTarget().getParent();
			}

			if (arguments.containsKey("roleCode")) {
				setRole((String) arguments.get("roleCode"));
				getUserWorkspace().allocateRoleAuthorities(getRole(), "TanDetailList");
			}

			if (arguments.containsKey("parentTab")) {
				parentTab = (Tab) arguments.get("parentTab");
			}

			if (arguments.containsKey("financeDetail")) {
				setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
				this.custId = financeDetail.getFinScheduleData().getFinanceMain().getCustID();
				this.finReference = financeDetail.getFinScheduleData().getFinanceMain().getFinReference();
				setTanAssiginmentList(financeDetail.getTanAssignments());
			}

			if (arguments.containsKey("financeMainDialogCtrl")) {
				setFinanceMainDialogCtrl((Object) arguments.get("financeMainDialogCtrl"));
				try {
					financeMainDialogCtrl.getClass().getMethod("setTanDetailListCtrl", this.getClass())
							.invoke(financeMainDialogCtrl, this);
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
				this.window_TanDetailList.setTitle("");
			}

			if (arguments.containsKey("finHeaderList")) {
				appendFinBasicDetails((ArrayList<Object>) arguments.get("finHeaderList"));
			} else {
				this.finBasicdetails.setZclass("null");
			}

			this.tanAssignment = (TanAssignment) arguments.get("tanAssignment");

			doSetProperties();

			doLoadWorkFlow(this.tanAssignment.isWorkflow(), this.tanAssignment.getWorkflowId(),
					this.tanAssignment.getNextTaskId(), getRole());

			if (isWorkFlowEnabled()) {
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());

			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);

			}
			doCheckRights();

			doShowDialog();
		} catch (Exception e) {
			closeDialog();
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void doShowDialog() {
		logger.debug(Literal.ENTERING);

		if (CollectionUtils.isNotEmpty(tanAssiginmentList)) {
			listCount = 0;
			for (TanAssignment tanAss : tanAssiginmentList) {
				if (!tanAss.getFinReference().equals(this.finReference)) {
					tanNumberList.add(tanAss);
					listCount++;
				}
			}
			doFillCheckListDetailsList(tanAssiginmentList);
		}

		if ((custId != 0 && StringUtils.isEmpty(finReference)) || CollectionUtils.isEmpty(tanAssiginmentList)) {
			List<TanAssignment> list = tanAssignmentService.getTanDetails(custId, "");
			listCount = list.size();
			doFillCheckListDetailsList(list);
		}

		logger.debug(Literal.LEAVING);
	}

	private void appendFinBasicDetails(ArrayList<Object> finHeaderList) {
		logger.debug(Literal.ENTERING);

		try {
			final Map<String, Object> map = new HashMap<>();
			map.put("parentCtrl", this);
			map.put("finHeaderList", finHeaderList);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnAdd_AddTanDetail(Event event) {
		logger.debug(Literal.ENTERING);

		TanAssignment aTanAssignment = new TanAssignment();
		aTanAssignment.setNewRecord(true);
		TanDetail aTanDetail = new TanDetail();
		aTanDetail.setNewRecord(true);
		int keyValue = 0;
		List<Listitem> mappingList = listBoxTanDetail.getItems();
		if (CollectionUtils.isNotEmpty(mappingList)) {
			for (Listitem detail : mappingList) {
				TanAssignment tanAssignment = (TanAssignment) detail.getAttribute("data");
				if (tanAssignment != null && tanAssignment.getTanDetail().getKeyValue() > keyValue) {
					keyValue = tanAssignment.getTanDetail().getKeyValue();
				}
			}
		}
		aTanDetail.setKeyValue(keyValue + 1);
		aTanDetail.setFinReference(this.finReference);
		aTanAssignment.setTanDetail(aTanDetail);
		renderItem(aTanAssignment);

		logger.debug(Literal.LEAVING);
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		if (finReference != null) {
			this.btnAdd_AddTanDetail
					.setVisible(getUserWorkspace().isAllowed("button_TanDetailList_btnAdd_AddTanDetail"));
		}

		logger.debug(Literal.LEAVING);
	}

	public void doSave(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		tanNumberList = tanAssignmentService.getTanNumberList(this.custId);

		if (!saveTanDetailList()) {
			financeDetail.setTanAssignments(null);
			return;
		}

		for (TanAssignment aTanAssignment : this.tanAssiginmentList) {
			boolean isNew = false;

			isNew = aTanAssignment.getTanDetail().isNewRecord();

			if (isWorkFlowEnabled()) {
				if (StringUtils.isBlank(aTanAssignment.getRecordType())) {
					aTanAssignment.setVersion(aTanAssignment.getVersion() + 1);
					if (isNew) {
						aTanAssignment.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else {
						aTanAssignment.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						aTanAssignment.setNewRecord(true);
					}
				}
			} else {
				aTanAssignment.setVersion(aTanAssignment.getVersion() + 1);
				if (isNew) {
					aTanAssignment.setVersion(1);
					aTanAssignment.setRecordType(PennantConstants.RCD_ADD);
				}

				if (StringUtils.isBlank(aTanAssignment.getRecordType())) {
					aTanAssignment.setRecordType(PennantConstants.RCD_UPD);
				}
			}

			try {
				setWorkFlowDetails(aTanAssignment);

			} catch (final DataAccessException e) {
				logger.error(Literal.EXCEPTION, e);
				MessageUtil.showError(e);
			}

		}
		financeDetail.setTanAssignments(tanAssiginmentList);
		logger.debug(Literal.LEAVING);
	}

	private boolean setWorkFlowDetails(TanAssignment aTanAssignment) {
		logger.debug(Literal.ENTERING);

		TanDetail tanDetail = aTanAssignment.getTanDetail();
		String nextRoleCode = "";
		String nextTaskId = "";
		String taskId = getTaskId(getRole());

		aTanAssignment.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aTanAssignment.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aTanAssignment.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			aTanAssignment.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aTanAssignment.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aTanAssignment);
				}

				if (isNotesMandatory(taskId, aTanAssignment)) {
					if (!notesEntered) {
						logger.debug(Literal.LEAVING);
						return false;
					}
				}
			}

			if (!StringUtils.isBlank(nextTaskId)) {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}
		}

		aTanAssignment.setTaskId(taskId);
		aTanAssignment.setNextTaskId(nextTaskId);
		aTanAssignment.setRoleCode(getRole());
		aTanAssignment.setNextRoleCode(nextRoleCode);

		// For TAN Detail
		tanDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		tanDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		tanDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		tanDetail.setTaskId(taskId);
		tanDetail.setNextTaskId(nextTaskId);
		tanDetail.setRoleCode(getRole());
		tanDetail.setNextRoleCode(nextRoleCode);

		logger.debug(Literal.LEAVING);
		return true;
	}

	private boolean saveTanDetailList() {
		logger.debug(Literal.ENTERING);

		List<WrongValueException> wve = new ArrayList<>();
		if (this.listBoxTanDetail.getItemCount() == listCount) {
			MessageUtil.showError(new ErrorDetail(
					Labels.getLabel("FIELD_IS_MAND", new String[] { Labels.getLabel("label_TanDetail.title") })));
			return false;
		} else {
			if (CollectionUtils.isEmpty(tanAssiginmentList)) {
				tanAssiginmentList = new ArrayList<>();
			}

			tanAssiginmentList.clear();
			for (Listitem listitem : listBoxTanDetail.getItems()) {
				TanAssignment aTanAssignment = (TanAssignment) listitem.getAttribute("data");
				if (listitem.getIndex() < listCount) {
					continue;
				}
				TanDetail aTanDetail = aTanAssignment.getTanDetail();
				List<Listcell> listcels = listitem.getChildren();
				for (Listcell listcell : listcels) {
					try {
						getCompValuetoBean(listcell, aTanDetail);
					} catch (WrongValueException we) {
						wve.add(we);
					}
				}
				boolean isNew = false;

				isNew = aTanDetail.isNewRecord();
				if (isWorkFlowEnabled()) {
					if (StringUtils.isBlank(aTanDetail.getRecordType())) {
						aTanDetail.setVersion(aTanDetail.getVersion() + 1);
						if (isNew) {
							aTanDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						} else {
							aTanDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
							aTanDetail.setNewRecord(true);
						}
					}
				} else {
					if (isNew) {
						aTanDetail.setVersion(1);
						aTanDetail.setRecordType(PennantConstants.RCD_ADD);
					}

					if (StringUtils.isBlank(aTanDetail.getRecordType())) {
						aTanDetail.setRecordType(PennantConstants.RCD_UPD);
					}
				}

				showErrorDetails(wve);
				aTanAssignment.setCustID(this.custId);
				aTanAssignment.setFinReference(aTanDetail.getFinReference());
				aTanAssignment.setTanDetail(aTanDetail);
				aTanAssignment.setNewRecord(aTanDetail.isNewRecord());
				aTanAssignment.setRecordType(aTanDetail.getRecordType());
				tanAssiginmentList.add(aTanAssignment);
			}
			if (CollectionUtils.isNotEmpty(deleteTanAssiginmentList)) {
				tanAssiginmentList.addAll(deleteTanAssiginmentList);
			}

			if (CollectionUtils.isNotEmpty(tanAssiginmentList)) {
				Set<String> tNum = new HashSet<>();
				List<TanAssignment> tanNumberList = tanAssiginmentList.stream()
						.filter(e -> tNum.add(e.getTanDetail().getTanNumber())).collect(Collectors.toList());
				if (tanAssiginmentList.size() != tanNumberList.size()) {
					MessageUtil.showError("TAN Numbers Already Exist");
					return false;
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return true;
	}

	private void getCompValuetoBean(Listcell listcell, TanDetail tanDetail) {
		logger.debug(Literal.ENTERING);

		String id = StringUtils.trimToNull(listcell.getId());
		if (id == null) {
			return;
		}
		id = id.replaceAll("\\d", "");

		switch (id) {
		case "tanNumber":
			Hbox hbox1 = (Hbox) listcell.getFirstChild();
			Uppercasebox tanNumber = (Uppercasebox) hbox1.getLastChild();
			Clients.clearWrongValue(tanNumber);
			String tanNumberId = tanNumber.getText().toUpperCase();
			Matcher matcher = ValidateTanNumber(tanNumberId);
			if (StringUtils.isEmpty(tanNumberId)) {
				throw new WrongValueException(tanNumber,
						Labels.getLabel("FIELD_IS_MAND", new String[] { "TAN Number " }));
			}
			if (!matcher.matches()) {
				throw new WrongValueException(tanNumber, Labels.getLabel("label_TanDetail_tanInvalidErrorMsg.value"));
			}
			if (tanDetail.isNewRecord()
					&& !StringUtils.equals(tanAssignment.getRecordType(), PennantConstants.RECORD_TYPE_UPD)
					&& !StringUtils.equals(tanAssignment.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {

				StringBuilder message = new StringBuilder("");
				for (TanAssignment aTanAssignment : tanNumberList) {
					if (StringUtils.equals(aTanAssignment.getTanDetail().getTanNumber(), tanNumberId)) {
						message.append("CIF - " + aTanAssignment.getCustCIF());
					}
				}
				if (StringUtils.isNotBlank(message.toString())) {
					String msg = "Duplicate TAN number, TAN number is present for " + message.toString()
							+ " , Do you want to continue ?";
					if (MessageUtil.NO == MessageUtil.confirm(msg)) {
						throw new WrongValueException(tanNumber,
								Labels.getLabel("label_TanDetail_tanNumberDuplicateErrorMsg.value"));
					}
				}
			}
			tanDetail.setTanNumber(tanNumberId);
			break;
		case "finReference":
			Hbox hbox2 = (Hbox) listcell.getFirstChild();
			Label finReference = (Label) hbox2.getLastChild();
			Clients.clearWrongValue(finReference);
			String finReferenceId = finReference.getValue();
			if (StringUtils.isEmpty(finReferenceId) || (StringUtils.equals(finReferenceId, "0"))) {
				throw new WrongValueException(finReference,
						Labels.getLabel("FIELD_IS_MAND", new String[] { "FinReference " }));
			}
			tanDetail.setFinReference(finReferenceId);
			break;
		case "tanHolderName":
			Hbox hbox3 = (Hbox) listcell.getFirstChild();
			Textbox tanHolderName = (Textbox) hbox3.getLastChild();
			Clients.clearWrongValue(tanHolderName);
			String tanHolderNameId = tanHolderName.getText();
			if (StringUtils.isEmpty(tanHolderNameId) || (StringUtils.equals(tanHolderNameId, "0"))) {
				throw new WrongValueException(tanHolderName,
						Labels.getLabel("FIELD_IS_MAND", new String[] { "TAN Holder Name " }));
			}
			tanDetail.setTanHolderName(tanHolderNameId);
			break;
		default:
			break;
		}

		logger.debug(Literal.LEAVING);
	}

	private void showErrorDetails(List<WrongValueException> wve) {
		logger.debug(Literal.ENTERING);

		boolean focus = false;
		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
				if (i == 0) {
					Component comp = wvea[i].getComponent();
					if (!focus) {
						focus = setComponentFocus(comp);
					}
				}
				logger.error(Literal.EXCEPTION, wvea[i]);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	public void doFillCheckListDetailsList(List<TanAssignment> tanAssignment) {
		logger.debug(Literal.ENTERING);

		this.listBoxTanDetail.getItems().clear();
		int keyValue = 0;
		for (TanAssignment aTanAssignment : tanAssignment) {
			TanDetail tanDetail = aTanAssignment.getTanDetail();
			aTanAssignment.setKeyValue(keyValue + 1);
			tanDetail.setFinReference(
					(aTanAssignment.getFinReference() != null) ? aTanAssignment.getFinReference() : this.finReference);
			tanDetail.setKeyValue(keyValue + 1);
			renderItem(aTanAssignment);
			keyValue = aTanAssignment.getKeyValue();
		}

		logger.debug(Literal.LEAVING);
	}

	private void renderItem(TanAssignment tanAssignment) {
		logger.debug(Literal.ENTERING);

		Listitem listItem = new Listitem();
		Listcell listCell;
		Hbox hbox;
		Space space;

		boolean isReadOnly = isReadOnly("button_TanDetailList_btnDelete_DeleteTanDetail");

		String recordType = "";
		String recordStatus = "";

		TanDetail tanDetail = tanAssignment.getTanDetail();

		if (StringUtils.isNotBlank(this.finReference)) {
			recordType = tanAssignment.getRecordType();
			recordStatus = tanAssignment.getRecordStatus();
		}

		if ("Approved".equals(recordStatus)
				&& !StringUtils.equals(this.finReference, tanAssignment.getFinReference())) {
			isReadOnly = true;
		}
		if ((recordStatus != null && "".equals(recordStatus)) || "Submitted".equals(recordStatus)) {
			isReadOnly = true;
			if (StringUtils.equals(this.finReference, tanAssignment.getFinReference())) {
				readOnlyComponent(true, this.btnAdd_AddTanDetail);
			}
		}

		// TanNumber Uppercasebox
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		space.setSclass("mandatory");
		Uppercasebox tanNumber = new Uppercasebox();
		tanNumber.setStyle("text-align:left");
		tanNumber.setMaxlength(10);
		tanNumber.setValue(tanDetail.getTanNumber());
		tanNumber.addForward("onChange", self, "onChangeTanNumber");
		readOnlyComponent(isReadOnly, tanNumber);
		tanNumber.setWidth("120px");
		listCell.setId("tanNumber".concat(String.valueOf(tanDetail.getKeyValue())));
		hbox.appendChild(space);
		hbox.appendChild(tanNumber);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// FinReference Label
		listCell = new Listcell();
		hbox = new Hbox();
		Label finReference = new Label();
		finReference.setStyle("text-align:left");
		finReference.setValue(tanDetail.getFinReference());
		readOnlyComponent(isReadOnly, finReference);
		finReference.setWidth("80px");
		listCell.setId("finReference".concat(String.valueOf(tanDetail.getKeyValue())));
		hbox.appendChild(finReference);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// TanHolderName Textbox
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		space.setSclass("mandatory");
		Textbox tanHolderName = new Textbox();
		tanHolderName.setStyle("text-align:left");
		tanHolderName.setMaxlength(150);
		tanHolderName.setValue(tanDetail.getTanHolderName());
		tanHolderName.addForward("onChange", self, "onChangeTanHolderName");
		readOnlyComponent(isReadOnly, tanHolderName);
		tanHolderName.setWidth("150px");
		listCell.setId("tanHolderName".concat(String.valueOf(tanDetail.getKeyValue())));
		hbox.appendChild(space);
		hbox.appendChild(tanHolderName);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		listCell = new Listcell(recordStatus);
		listCell.setParent(listItem);

		listCell = new Listcell(recordType);
		listCell.setParent(listItem);

		// Delete action
		if (StringUtils.isNotBlank(this.finReference)) {
			listCell = new Listcell();
			hbox = new Hbox();
			space = new Space();
			space.setSpacing("2px");
			Button button = new Button();
			button.setSclass("z-toolbarbutton");
			button.setLabel("Delete");
			if ("DELETE".equalsIgnoreCase(recordType)) {
				button.setDisabled(true);
			} else {
				button.setDisabled(isReadOnly);
			}
			listCell.appendChild(button);
			listCell.setParent(listItem);
			button.addForward("onClick", self, "onClickTanDetailButtonDelete", listItem);
		}

		if (tanDetail.getRecordStatus() != null && tanDetail.getRecordStatus().equals("Approved")) {
			tanDetail.setRecordType(tanAssignment.getRecordType());
			tanDetail.setRecordStatus(tanAssignment.getRecordStatus());
		}

		listItem.setAttribute("data", tanAssignment);
		this.listBoxTanDetail.appendChild(listItem);
		logger.debug(Literal.LEAVING);
	}

	public void onClickTanDetailButtonDelete(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		Listitem item = (Listitem) event.getData();
		TanAssignment aTanAssignment = (TanAssignment) item.getAttribute("data");

		TanDetail tanDetail = aTanAssignment.getTanDetail();

		List<TdsReceivablesTxn> tdsreceivable = tdsReceivablesTxnService
				.getTdsReceivablesTxnsByFinRef(aTanAssignment.getFinReference(), TableType.TEMP_TAB);

		for (TdsReceivablesTxn detail : tdsreceivable) {

			if ("Saved".equals(detail.getRecordStatus()) || "Submitted".equals(detail.getRecordStatus())
					|| "Resubmitted".equals(detail.getRecordStatus())) {

				MessageUtil.showError("Adjustment transaction is pending for this TanNumber at certificate adjustment");
				return;
			}
		}

		if (tanDetail.isNewRecord()) {
			listBoxTanDetail.removeItemAt(item.getIndex());
		} else {

			if ("Approved".equals(tanDetail.getRecordStatus())) {
				tanDetail.setNewRecord(true);
			}
			if ("Approved".equals(tanDetail.getRecordStatus())) {
				tanDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				aTanAssignment.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			} else {
				tanDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				aTanAssignment.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}

			aTanAssignment.setCustID(this.custId);
			aTanAssignment.setFinReference(tanDetail.getFinReference());
			aTanAssignment.setTanID(tanDetail.getId());
			if (isWorkFlowEnabled()) {
				aTanAssignment.setNewRecord(tanDetail.isNewRecord());
			}
			aTanAssignment.setTanDetail(tanDetail);
			setWorkFlowDetails(aTanAssignment);
			aTanAssignment.setTanDetail(tanDetail);
			deleteTanAssiginmentList.add(aTanAssignment);
			listBoxTanDetail.removeItemAt(item.getIndex());
		}

		logger.debug(Literal.LEAVING);
	}

	public void onChangeTanNumber(ForwardEvent event) throws Exception {
		logger.debug(Literal.ENTERING);

		Uppercasebox tanNumber = (Uppercasebox) event.getOrigin().getTarget();
		Clients.clearWrongValue(tanNumber);

		String aTanNumber = tanNumber.getValue();
		Matcher matcher = ValidateTanNumber(aTanNumber);
		if (!matcher.matches()) {
			throw new WrongValueException(tanNumber, Labels.getLabel("label_TanDetail_tanInvalidErrorMsg.value"));
		}
		if (aTanNumber.length() < 10 || aTanNumber.length() > 10) {
			throw new WrongValueException(tanNumber, Labels.getLabel("label_TanDetail_tanNumberLengthErrorMsg.value"));
		}

		logger.debug(Literal.LEAVING);

	}

	private Matcher ValidateTanNumber(String aTanNumber) {
		Pattern pattern = Pattern
				.compile(PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_TAN_NUMBER));
		Matcher matcher = pattern.matcher(aTanNumber);
		return matcher;
	}

	public void onChangeTanHolderName(ForwardEvent event) throws Exception {
		logger.debug(Literal.ENTERING);

		Textbox tanHolderName = (Textbox) event.getOrigin().getTarget();
		Clients.clearWrongValue(tanHolderName);

		String aTanHolderName = tanHolderName.getValue();
		Pattern pattern = Pattern
				.compile(PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_NAME));
		Matcher matcher = pattern.matcher(aTanHolderName);
		if (!matcher.matches()) {
			throw new WrongValueException(tanHolderName,
					Labels.getLabel("label_TanDetail_tanHolderNameErrorMsg.value"));
		}

		logger.debug(Literal.LEAVING);

	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		logger.debug(Literal.ENTERING);

		finBasicDetailsCtrl.doWriteBeanToComponents(finHeaderList);
		if (finHeaderList != null && !StringUtils.isEmpty((String) finHeaderList.get(3))) {
			finReference = (String) finHeaderList.get(3);
			doCheckRights();
		}

		logger.debug(Literal.LEAVING);
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	private void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
		if (financeMainDialogCtrl instanceof FinanceMainBaseCtrl) {
			((FinanceMainBaseCtrl) financeMainDialogCtrl).setTanDetailListCtrl(this);
		}
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public void setTanAssignment(TanAssignment tanAssignment) {
		this.tanAssignment = tanAssignment;
	}

	public List<TanAssignment> getTanAssiginmentList() {
		return tanAssiginmentList;
	}

	public void setTanAssiginmentList(List<TanAssignment> tanAssiginmentList) {
		this.tanAssiginmentList = tanAssiginmentList;
	}

	public void setTanAssignmentService(TanAssignmentService tanAssignmentService) {
		this.tanAssignmentService = tanAssignmentService;
	}

	public TdsReceivablesTxnService getTdsReceivablesTxnService() {
		return tdsReceivablesTxnService;
	}

	public void setTdsReceivablesTxnService(TdsReceivablesTxnService tdsReceivablesTxnService) {
		this.tdsReceivablesTxnService = tdsReceivablesTxnService;
	}

}
