package com.pennant.pff.noc.webui;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.pff.noc.model.ServiceBranch;
import com.pennant.pff.noc.model.ServiceBranchesLoanType;
import com.pennant.pff.noc.service.ServiceBranchService;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.dataengine.util.ExcelUtil;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class ServiceBranchDialogCtrl extends GFCBaseCtrl<ServiceBranch> {
	private static final long serialVersionUID = 3293101778075270047L;
	private static final Logger logger = LogManager.getLogger(ServiceBranchDialogCtrl.class);

	protected Window windowServiceBranchDialog;
	protected Uppercasebox code;
	protected Textbox description;
	protected Textbox ofcOrHouseNum;
	protected Textbox flatNum;
	protected Textbox street;
	protected Textbox addrLine1;
	protected Textbox addrLine2;
	protected Textbox pOBox;
	protected ExtendedCombobox country;
	protected ExtendedCombobox cpProvince;
	protected ExtendedCombobox city;
	protected ExtendedCombobox pinCode;
	protected Textbox folderPath;
	protected Checkbox active;
	protected Listbox listBoxLoanTypes;
	protected Button btnNewLoanTypeBranch;
	protected Textbox file;
	protected Button btnImportLoanTypeBranch;
	protected Button btnUploadLoanTypeBranch;
	protected Media media;

	private ServiceBranch serviceBranch;

	private transient ServiceBranchListCtrl serviceBranchListCtrl;
	private transient ServiceBranchService serviceBranchService;
	private List<ServiceBranchesLoanType> loanTypeList = new ArrayList<>();

	public ServiceBranchDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ServiceBranchDialog";
	}

	public void onCreate$windowServiceBranchDialog(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		setPageComponents(windowServiceBranchDialog);

		try {

			this.serviceBranch = (ServiceBranch) arguments.get("serviceBranch");

			if (this.serviceBranch == null) {
				throw new AppException(Labels.getLabel("error.unhandled"));
			}

			this.serviceBranchListCtrl = (ServiceBranchListCtrl) arguments.get("serviceBranchListCtrl");

			ServiceBranch custSerBranch = new ServiceBranch();
			BeanUtils.copyProperties(this.serviceBranch, custSerBranch);

			this.serviceBranch.setBefImage(custSerBranch);

			doLoadWorkFlow(this.serviceBranch.isWorkflow(), this.serviceBranch.getWorkflowId(),
					this.serviceBranch.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doSetFieldProperties();
			this.listBoxLoanTypes.setHeight(getListBoxHeight(4));
			doCheckRights();
			doShowDialog(this.serviceBranch);

		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		ServiceBranch custServBranch = new ServiceBranch();
		BeanUtils.copyProperties(this.serviceBranch, custServBranch);

		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(custServBranch);

		if (!savelist(custServBranch)) {
			return;
		}

		isNew = custServBranch.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;

			if (StringUtils.isBlank(custServBranch.getRecordType())) {
				custServBranch.setVersion(custServBranch.getVersion() + 1);

				if (isNew) {
					custServBranch.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					custServBranch.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					custServBranch.setNewRecord(true);
				}
			} else {
				custServBranch.setVersion(custServBranch.getVersion() + 1);

				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}

			try {
				if (doProcess(custServBranch, tranType)) {
					refreshList();
					closeDialog();
				}
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	protected boolean doProcess(ServiceBranch csb, String tranType) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		csb.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		csb.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		csb.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			csb.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {

				nextTaskId = StringUtils.trimToEmpty(csb.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, csb);
				}

				if (isNotesMandatory(taskId, csb) && !notesEntered) {
					MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
					return false;
				}
			}

			if (StringUtils.isNotBlank(nextTaskId)) {
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

			csb.setTaskId(taskId);
			csb.setNextTaskId(nextTaskId);
			csb.setRoleCode(getRole());
			csb.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(csb, tranType);
			String operationRefs = getServiceOperations(taskId, csb);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(csb, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(csb, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader ah, String method) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		ServiceBranch aBounceCode = (ServiceBranch) ah.getAuditDetail().getModelData();
		boolean deleteNotes = false;
		String recordType = aBounceCode.getRecordType();

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (PennantConstants.TRAN_DEL.equals(ah.getAuditTranType())) {
					ah = serviceBranchService.delete(ah);
					deleteNotes = true;
				} else {
					ah = serviceBranchService.saveOrUpdate(ah);
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					ah = serviceBranchService.doApprove(ah);

					if (PennantConstants.RECORD_TYPE_DEL.equals(recordType)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					ah = serviceBranchService.doReject(ah);

					if (PennantConstants.RECORD_TYPE_NEW.equals(recordType)) {
						deleteNotes = true;
					}
				} else {
					ah.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					return processCompleted;
				}
			}

			ah = ErrorControl.showErrorDetails(this.windowServiceBranchDialog, ah);
			retValue = ah.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.serviceBranch), true);
				}
			}

			if (retValue == PennantConstants.porcessOVERIDE) {
				ah.setOveride(true);
				ah.setErrorMessage(null);
				ah.setInfoMessage(null);
				ah.setOverideMessage(null);
			}

		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		final ServiceBranch custSerBranch = new ServiceBranch();
		BeanUtils.copyProperties(this.serviceBranch, custSerBranch);
		doDelete(String.valueOf(custSerBranch.getId()), custSerBranch);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		doClose(this.btnSave.isVisible());

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);

		doShowNotes(this.serviceBranch);

		logger.debug(Literal.LEAVING);
	}

	protected void refreshList() {
		logger.debug(Literal.ENTERING);

		serviceBranchListCtrl.fillListData();

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		MessageUtil.showHelpWindow(event, windowServiceBranchDialog);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		doEdit();

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnNewLoanTypeBranch(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		ServiceBranchesLoanType serBranLoanType = new ServiceBranchesLoanType();

		serBranLoanType.setNewRecord(true);
		int keyValue = listBoxLoanTypes.getItems().size();
		List<Listitem> list = listBoxLoanTypes.getItems();

		if (list != null && !list.isEmpty()) {
			for (Listitem detail : list) {
				ServiceBranchesLoanType serviceBranhInfo = (ServiceBranchesLoanType) detail.getAttribute("data");
				if (serviceBranhInfo != null && serviceBranhInfo.getKeyValue() > keyValue) {
					keyValue = serviceBranhInfo.getKeyValue();
				}
			}
		}

		serBranLoanType.setKeyValue(keyValue + 1);
		renderItem(serBranLoanType);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onUpload$btnUploadLoanTypeBranch(UploadEvent event) throws IOException {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		media = ((UploadEvent) event).getMedia();
		this.file.setText(media.getName());

		List<WrongValueException> wve = new ArrayList<>();

		try {
			ExcelUtil.isValidFile(this.file.getValue(), 100, "^[a-zA-Z0-9 ._]*$");
		} catch (AppException e) {
			wve.add(new WrongValueException(this.file, e.getMessage()));
		}

		if (!media.getName().toLowerCase().startsWith("loan")) {
			MessageUtil.showError(Labels.getLabel("lable.invalidFileName"));
			return;
		}

		doRemoveValidation();
		showErrorDetails(wve);
		readFromExcel();

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	private void readFromExcel() throws IOException {
		Workbook workBook = null;

		try {
			if (media.getName().toLowerCase().endsWith(".xls")) {
				try {
					workBook = new HSSFWorkbook(media.getStreamData());
				} catch (OfficeXmlFileException e) {
					workBook = new XSSFWorkbook(media.getStreamData());
				}
			} else if (media.getName().toLowerCase().endsWith(".xlsx")) {
				workBook = new XSSFWorkbook(media.getStreamData());
			}
		} catch (Exception e) {
			MessageUtil.showError(Labels.getLabel("label_ValidatedUploadFile"));
			return;
		}

		if (workBook == null) {
			MessageUtil.showError(Labels.getLabel("label_ValidatedUploadFile"));
			return;
		}

		Sheet myExcelSheet = workBook.getSheetAt(0);
		if (myExcelSheet == null) {
			MessageUtil.showError(Labels.getLabel("label_ValidatedUploadFile"));
			return;
		}

		if (!myExcelSheet.getSheetName().contains(Labels.getLabel("lable_UploadFileName.value"))) {
			MessageUtil.showError(Labels.getLabel("label_ValidatedUploadFile"));
			return;
		}

		int rowCount = myExcelSheet.getPhysicalNumberOfRows();
		if (rowCount <= 1) {
			MessageUtil.showError(Labels.getLabel("label_ValidatedUploadFile"));
			return;
		}

		Iterator<Row> rows = myExcelSheet.iterator();

		int keyValue = listBoxLoanTypes.getItems().size();

		while (rows.hasNext()) {
			Row row = rows.next();

			if (row.getRowNum() == 0) {
				if (row.getLastCellNum() != 2) {
					MessageUtil.showError(Labels.getLabel("label_ValidatedUploadFile"));
					return;
				}
				continue;
			}

			keyValue++;
			ServiceBranchesLoanType csbt = new ServiceBranchesLoanType();

			for (Cell cell : row) {
				try {

					switch (cell.getColumnIndex()) {
					case 0:
						csbt.setFinType(StringUtils.trimToNull(cell.toString()));
						break;
					case 1:
						cell.setCellType(CellType.STRING);
						csbt.setBranch(StringUtils.trimToNull(cell.toString()));
						break;
					default:
						break;
					}
				} catch (WrongValueException e) {
					MessageUtil.showError(e.getMessage());
					return;
				}
			}

			LoggedInUser loggedInUser = getUserWorkspace().getLoggedInUser();
			csbt.setKeyValue(keyValue);
			csbt.setLastMntBy(loggedInUser.getUserId());
			csbt.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			csbt.setUserDetails(loggedInUser);
			csbt.setVersion(1);
			csbt.setRecordStatus("");
			csbt.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			csbt.setNewRecord(true);
			loanTypeList.add(csbt);
		}

		workBook.close();
	}

	public void onClick$btnImportLoanTypeBranch(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		for (ServiceBranchesLoanType serBranch : loanTypeList) {
			renderItem(serBranch);
		}

		loanTypeList.clear();
		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	private void doShowDialog(ServiceBranch csb) {
		logger.debug(Literal.LEAVING);

		if (csb.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(csb.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				btnCancel.setVisible(false);
			}
		}

		doWriteBeanToComponents(csb);
		doFillCheckListDetailsList(csb.getServiceBranchLoanTypeList());

		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	private void doWriteBeanToComponents(ServiceBranch csb) {
		logger.debug(Literal.ENTERING);

		if (!csb.isNewRecord()) {
			this.code.setDisabled(true);
		}
		this.code.setValue(csb.getCode());
		this.description.setValue(csb.getDescription());
		this.country.setValue(csb.getCountry());
		this.city.setValue(csb.getCity());
		this.cpProvince.setValue(csb.getCpProvince());

		if (csb.getPinCodeId() != null) {
			this.pinCode.setAttribute("pinCodeId", csb.getPinCodeId());
		} else {
			this.pinCode.setAttribute("pinCodeId", null);
		}
		this.pinCode.setValue(csb.getPinCode());
		this.active.setChecked(csb.isActive());
		this.addrLine1.setValue(csb.getAddrLine1());
		this.addrLine2.setValue(csb.getAddrLine2());
		this.ofcOrHouseNum.setValue(csb.getOfcOrHouseNum());
		this.flatNum.setValue(csb.getFlatNum());
		this.street.setValue(csb.getStreet());
		this.pOBox.setValue(csb.getPoBox());
		this.folderPath.setValue(csb.getFolderPath());
		if (csb.isNewRecord()) {
			this.country.setDescription("");
			this.city.setDescription("");
			this.pinCode.setDescription("");
		} else {
			this.country.setDescription(csb.getCountry());
			this.city.setDescription(csb.getCity());
			this.pinCode.setDescription(csb.getPinCode());
		}

		this.recordStatus.setValue(csb.getRecordStatus());

		if (!csb.isNewRecord()) {
			ArrayList<Filter> filters = new ArrayList<Filter>();

			if (this.country.getValue() != null && !this.country.getValue().isEmpty()) {
				Filter filterPin0 = new Filter("PCCountry", country.getValue(), Filter.OP_EQUAL);
				filters.add(filterPin0);
			}

			if (this.cpProvince.getValue() != null && !this.cpProvince.getValue().isEmpty()) {
				Filter filterPin1 = new Filter("PCProvince", cpProvince.getValue(), Filter.OP_EQUAL);
				filters.add(filterPin1);
			}

			if (this.city.getValue() != null && !this.city.getValue().isEmpty()) {
				Filter filterPin2 = new Filter("City", this.city.getValue(), Filter.OP_EQUAL);
				filters.add(filterPin2);
			}

			Filter[] filterPin = new Filter[filters.size()];
			for (int i = 0; i < filters.size(); i++) {
				filterPin[i] = filters.get(i);
			}
			this.pinCode.setFilters(filterPin);

		}

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$pinCode(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		Object dataObj = pinCode.getObject();
		if (dataObj instanceof String) {

		} else {
			PinCode pc = (PinCode) dataObj;

			if (pc != null) {
				this.country.setValue(pc.getpCCountry());
				this.country.setDescription(pc.getLovDescPCCountryName());
				this.city.setValue(pc.getCity());
				this.city.setDescription(pc.getPCCityName());
				this.cpProvince.setValue(pc.getPCProvince());
				this.cpProvince.setDescription(pc.getLovDescPCProvinceName());
				this.city.setErrorMessage("");
				this.cpProvince.setErrorMessage("");
				this.country.setErrorMessage("");
				this.pinCode.setAttribute("pinCodeId", pc.getPinCodeId());
				this.pinCode.setValue(pc.getPinCode());
			}

		}
		Filter[] fltr = new Filter[1];
		if (this.city.getValue() != null && !this.city.getValue().isEmpty()) {
			fltr[0] = new Filter("City", this.city.getValue(), Filter.OP_EQUAL);
		} else {
			fltr[0] = new Filter("City", null, Filter.OP_NOT_EQUAL);
		}

		this.pinCode.setFilters(fltr);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onFulfill$city(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		doRemoveValidation();
		doClearMessage();

		Object dataObj = city.getObject();
		String cityValue = null;
		if (!(dataObj instanceof String)) {
			City city = (City) dataObj;
			if (city == null) {
				fillPindetails(null, null);
			}
			if (city != null) {
				this.cpProvince.setValue(city.getPCProvince());
				this.cpProvince.setDescription(city.getLovDescPCProvinceName());
				this.country.setValue(city.getPCCountry());
				this.country.setDescription(city.getLovDescPCCountryName());
				this.pinCode.setValue("");
				this.pinCode.setDescription("");
				cityValue = city.getPCCity();

				fillPindetails(cityValue, this.cpProvince.getValue());
			} else {
				this.city.setObject("");
				this.pinCode.setObject("");
				this.pinCode.setValue("");
				this.pinCode.setDescription("");
				this.cpProvince.setErrorMessage("");
				this.country.setErrorMessage("");

				fillPindetails(null, this.cpProvince.getValue());
			}
		} else if ("".equals(dataObj)) {
			this.pinCode.setValue("");
			this.pinCode.setDescription("");
			this.cpProvince.setObject("");
		}
		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onFulfill$cpProvince(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		Object dataObj = cpProvince.getObject();
		String pcProvince = this.cpProvince.getValue();
		if (dataObj instanceof String) {
			this.city.setValue("");
			this.city.setDescription("");
			this.pinCode.setValue("");
			this.pinCode.setDescription("");

			fillPindetails(null, null);
		} else if (!(dataObj instanceof String)) {
			Province province = (Province) dataObj;
			if (province == null) {
				fillPindetails(null, null);
			}
			if (province != null) {
				this.cpProvince.setErrorMessage("");
				pcProvince = this.cpProvince.getValue();
				this.country.setValue(province.getCPCountry());
				this.country.setDescription(province.getLovDescCPCountryName());
				this.city.setValue("");
				this.city.setDescription("");
				this.pinCode.setValue("");
				this.pinCode.setDescription("");

				fillPindetails(null, pcProvince);
			} else {
				this.city.setObject("");
				this.pinCode.setObject("");
				this.city.setValue("");
				this.city.setDescription("");
				this.pinCode.setValue("");
				this.pinCode.setDescription("");
			}
		}

		fillCitydetails(pcProvince);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onFulfill$country(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		Object dataObj = country.getObject();
		String pcProvince = null;
		if (dataObj instanceof String) {
			this.cpProvince.setValue("");
			this.cpProvince.setDescription("");
			this.city.setValue("");
			this.city.setDescription("");
			this.pinCode.setValue("");
			this.pinCode.setDescription("");

			fillPindetails(null, null);

		} else if (!(dataObj instanceof String)) {
			Country country = (Country) dataObj;

			if (country == null) {
				fillProvinceDetails(null);
			}

			if (country != null) {
				this.cpProvince.setErrorMessage("");
				pcProvince = country.getCountryCode();

				fillProvinceDetails(pcProvince);
			} else {
				this.cpProvince.setObject("");
				this.cpProvince.setValue("");
				this.cpProvince.setDescription("");
				this.city.setObject("");
				this.city.setValue("");
				this.city.setDescription("");
				this.pinCode.setObject("");
				this.pinCode.setValue("");
				this.pinCode.setDescription("");
			}

			fillPindetails(null, null);

		}
		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	private void fillPindetails(String id, String province) {
		this.pinCode.setModuleName("PinCode");
		this.pinCode.setValueColumn("PinCodeId");
		this.pinCode.setDescColumn("AreaName");
		this.pinCode.setValidateColumns(new String[] { "PinCodeId" });
		Filter[] filters1 = new Filter[1];

		if (id != null) {
			filters1[0] = new Filter("City", id, Filter.OP_EQUAL);
		} else if (province != null && !province.isEmpty()) {
			filters1[0] = new Filter("PCProvince", province, Filter.OP_EQUAL);
		} else {
			filters1[0] = new Filter("City", null, Filter.OP_NOT_EQUAL);
		}

		this.pinCode.setFilters(filters1);
	}

	private void fillCitydetails(String state) {
		logger.debug("Entering");

		this.city.setModuleName("City");
		this.city.setValueColumn("PCCity");
		this.city.setDescColumn("PCCityName");
		this.city.setValidateColumns(new String[] { "PCCity" });
		Filter[] filters1 = new Filter[1];

		if (state == null || state.isEmpty()) {
			filters1[0] = new Filter("PCProvince", null, Filter.OP_NOT_EQUAL);
		} else {
			filters1[0] = new Filter("PCProvince", state, Filter.OP_EQUAL);
		}

		this.city.setFilters(filters1);
	}

	private void fillProvinceDetails(String country) {
		this.cpProvince.setMandatoryStyle(true);
		this.cpProvince.setModuleName("Province");
		this.cpProvince.setValueColumn("CPProvince");
		this.cpProvince.setDescColumn("CPProvinceName");
		this.cpProvince.setValidateColumns(new String[] { "CPProvince" });

		Filter[] filters1 = new Filter[1];

		if (country == null || country.equals("")) {
			filters1[0] = new Filter("CPCountry", null, Filter.OP_NOT_EQUAL);
		} else {
			filters1[0] = new Filter("CPCountry", country, Filter.OP_EQUAL);
		}

		this.cpProvince.setFilters(filters1);
	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.code.setConstraint("");
		this.description.setConstraint("");
		this.ofcOrHouseNum.setConstraint("");
		this.flatNum.setConstraint("");
		this.street.setConstraint("");
		this.addrLine1.setConstraint("");
		this.pOBox.setConstraint("");
		this.folderPath.setConstraint("");
		this.city.setConstraint("");
		this.country.setConstraint("");
		this.pinCode.setConstraint("");
		this.cpProvince.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerServiceBranch_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerServiceBranchDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerServiceBranchDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerServiceBranchDialog_btnSave"));

		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.country.setMandatoryStyle(true);
		this.country.setModuleName("Country");
		this.country.setValueColumn("CountryCode");
		this.country.setDescColumn("CountryDesc");
		this.country.setValidateColumns(new String[] { "CountryCode" });
		this.country.setTextBoxWidth(180);

		this.city.setMandatoryStyle(true);
		this.city.setModuleName("City");
		this.city.setValueColumn("PCCity");
		this.city.setDescColumn("PCCityName");
		this.city.setValidateColumns(new String[] { "PCCity" });
		this.city.setTextBoxWidth(180);

		this.pinCode.setMandatoryStyle(true);
		this.pinCode.setModuleName("PinCode");
		this.pinCode.setValueColumn("PinCodeId");
		this.pinCode.setDescColumn("AreaName");
		this.pinCode.setValueType(DataType.LONG);
		this.pinCode.setInputAllowed(false);
		this.pinCode.setValidateColumns(new String[] { "PinCodeId" });
		this.pinCode.setTextBoxWidth(180);
		this.pinCode.setTextBoxWidth(180);

		this.cpProvince.setMaxlength(8);
		this.cpProvince.setMandatoryStyle(true);
		this.cpProvince.setModuleName("Province");
		this.cpProvince.setValueColumn("CPProvince");
		this.cpProvince.setDescColumn("CPProvinceName");
		this.cpProvince.setValidateColumns(new String[] { "CPProvince" });

		this.code.setMaxlength(6);
		this.description.setMaxlength(50);
		this.ofcOrHouseNum.setMaxlength(50);
		this.flatNum.setMaxlength(50);
		this.street.setMaxlength(50);
		this.addrLine1.setMaxlength(50);
		this.addrLine2.setMaxlength(50);
		this.pOBox.setMaxlength(50);
		this.pinCode.setMaxlength(50);
		this.folderPath.setMaxlength(50);
		this.file.setText("");
		this.file.setMaxlength(500);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	public void doEdit() {
		logger.debug(Literal.ENTERING);

		this.btnCancel.setVisible(!this.serviceBranch.isNewRecord());
		this.code.setReadonly(isReadOnly("CustomerServiceBranchDialog_Code"));
		this.description.setReadonly(isReadOnly("CustomerServiceBranchDialog_Description"));
		this.ofcOrHouseNum.setReadonly(isReadOnly("CustomerServiceBranchDialog_OfcOrHouseNum"));
		this.flatNum.setReadonly(isReadOnly("CustomerServiceBranchDialog_FlatNum"));
		this.street.setReadonly(isReadOnly("CustomerServiceBranchDialog_Street"));
		this.addrLine1.setReadonly(isReadOnly("CustomerServiceBranchDialog_AddrLine1"));
		this.addrLine2.setReadonly(isReadOnly("CustomerServiceBranchDialog_AddrLine2"));
		this.city.setReadonly(isReadOnly("CustomerServiceBranchDialog_City"));
		this.country.setReadonly(isReadOnly("CustomerServiceBranchDialog_Country"));
		this.pinCode.setReadonly(isReadOnly("CustomerServiceBranchDialog_PinCode"));
		this.pOBox.setReadonly(isReadOnly("CustomerServiceBranchDialog_PoBox"));
		this.cpProvince.setReadonly(isReadOnly("CustomerServiceBranchDialog_CpProvince"));
		this.folderPath.setReadonly(isReadOnly("CustomerServiceBranchDialog_FolderPath"));
		this.active.setDisabled(isReadOnly("CustomerServiceBranchDialog_Active"));
		this.btnImportLoanTypeBranch.setDisabled(isReadOnly("CustomerServiceBranchDialog_Code"));
		this.btnNewLoanTypeBranch.setDisabled(isReadOnly("CustomerServiceBranchDialog_Code"));
		this.btnUploadLoanTypeBranch.setDisabled(isReadOnly("CustomerServiceBranchDialog_Code"));

		if (isWorkFlowEnabled()) {

			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.serviceBranch.isNewRecord()) {
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

	public void doWriteComponentsToBean(ServiceBranch csb) {
		logger.debug(Literal.ENTERING);

		List<WrongValueException> wve = new ArrayList<>();

		try {
			String code = this.code.getValue();
			if (csb.isNewRecord()) {
				if (StringUtils.isNotBlank(code) && code.length() < 2) {
					throw new WrongValueException(this.code, Labels.getLabel("label_ServiceBranchDialog_Code.value")
							+ " lenth should be greater than 1.");
				}
			}
			csb.setCode(code);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			csb.setDescription(this.description.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			csb.setCountry(this.country.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			csb.setCpProvince(this.cpProvince.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			csb.setCity(this.city.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			Object obj = this.pinCode.getAttribute("pinCodeId");
			if (obj != null) {
				if (!StringUtils.isEmpty(obj.toString())) {
					csb.setPinCodeId(Long.valueOf((obj.toString())));
					csb.setPinCode(this.pinCode.getValue());
				}
			} else {
				csb.setPinCodeId(null);
			}
			csb.setPinCode(this.pinCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			csb.setAddrLine1(this.addrLine1.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			csb.setAddrLine2(this.addrLine2.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			csb.setFlatNum(this.flatNum.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			csb.setStreet(this.street.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			csb.setOfcOrHouseNum(this.ofcOrHouseNum.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			csb.setPoBox(this.pOBox.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			csb.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			csb.setFolderPath(this.folderPath.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.code.isReadonly()) {
			this.code.setConstraint(new PTStringValidator(Labels.getLabel("label_ServiceBranchDialog_Code.value"),
					PennantRegularExpressions.REGEX_ALPHANUM, true));
		}

		if (!this.description.isReadonly()) {
			this.description
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BounceCodeDialog_BounceCodeDesc.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (!this.country.isReadonly()) {
			this.country.setConstraint(new PTStringValidator(Labels.getLabel("label_ServiceBranchDialog_Country.value"),
					null, true, true));
		}

		if (!this.cpProvince.isReadonly()) {
			this.cpProvince.setConstraint(new PTStringValidator(
					Labels.getLabel("label_ServiceBranchDialog_Province/State.value"), null, true, true));
		}

		if (!this.city.isReadonly()) {
			this.city.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ServiceBranchDialog_City.value"), null, true, true));
		}

		if (this.pinCode.isButtonVisible()) {
			this.pinCode.setConstraint(new PTStringValidator(Labels.getLabel("label_ServiceBranchDialog_Pincode.value"),
					null, true, true));
		}

		if (!this.pOBox.isReadonly()) {
			this.pOBox.setConstraint(new PTStringValidator(Labels.getLabel("label_ServiceBranchDialog_PoBox.value"),
					PennantRegularExpressions.REGEX_NUMERIC, false));
		}

		if (!this.ofcOrHouseNum.isReadonly()) {
			this.ofcOrHouseNum
					.setConstraint(new PTStringValidator(Labels.getLabel("label_ServiceBranchDialog_HouseNum.value"),
							PennantRegularExpressions.REGEX_ADDRESS, true));
		}

		if (!this.addrLine1.isReadonly()) {
			this.addrLine1
					.setConstraint(new PTStringValidator(Labels.getLabel("label_ServiceBranchDialog_AddrLine1.value"),
							PennantRegularExpressions.REGEX_ADDRESS, true));
		}

		if (!this.folderPath.isReadonly()) {
			this.folderPath.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ServiceBranchDialog_FolderPath.value"), null, true));
		}

		logger.debug(Literal.LEAVING);
	}

	private void renderItem(ServiceBranchesLoanType loanTypeMapping) {
		Listitem listItem = new Listitem();
		Listcell listCell;
		Hbox hbox;
		Space space;
		boolean isReadOnly = isReadOnly("CustomerServiceBranchLoanTypeDialog_btnNew");

		String recordType = loanTypeMapping.getRecordType();
		String recordStatus = loanTypeMapping.getRecordStatus();

		if (!(loanTypeMapping.isNewRecord()) || (recordStatus != null || "".equals(recordStatus))) {
			isReadOnly = true;
		}

		// FinType
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		space.setSclass("mandatory");
		ExtendedCombobox fintypes = new ExtendedCombobox();

		if (StringUtils.isNotEmpty(loanTypeMapping.getFinType())) {
			Search search = new Search(FinanceType.class);
			FinanceType finType = null;

			search.addFilterEqual("FinType", loanTypeMapping.getFinType());
			SearchProcessor searchProcessor = (SearchProcessor) SpringBeanUtil.getBean("searchProcessor");
			List<Object> results = searchProcessor.getResults(search);
			if (CollectionUtils.isNotEmpty(results)) {
				finType = (FinanceType) results.get(0);

				fintypes.setValue(loanTypeMapping.getFinType());
				fintypes.setDescription(finType.getFinTypeDesc());
				fintypes.setObject(finType);
			} else {
				MessageUtil.showError(
						Labels.getLabel("lable.invalid_LoanType") + " :".concat(loanTypeMapping.getFinType()));
				return;
			}
		}

		readOnlyComponent(isReadOnly, fintypes);

		fintypes.setWidth("80px");
		fintypes.setModuleName("CSDFinanceType");
		fintypes.setValueColumn("FinType");
		fintypes.setDescColumn("FinTypeDesc");
		fintypes.setValidateColumns(new String[] { "FinType" });
		listCell.setId("fintypes".concat(String.valueOf(loanTypeMapping.getKeyValue())));
		hbox.appendChild(space);
		hbox.appendChild(fintypes);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// Branch
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		space.setSclass("mandatory");
		ExtendedCombobox barnches = new ExtendedCombobox();

		if (StringUtils.isNotEmpty(loanTypeMapping.getBranch())) {
			Search search = new Search(Branch.class);
			Branch branch = null;

			search.addFilterEqual("BranchCode", loanTypeMapping.getBranch());
			SearchProcessor searchProcessor = (SearchProcessor) SpringBeanUtil.getBean("searchProcessor");
			List<Object> results = searchProcessor.getResults(search);

			if (CollectionUtils.isNotEmpty(results)) {
				branch = (Branch) searchProcessor.getResults(search).get(0);

				barnches.setValue(branch.getBranchCode());
				barnches.setDescription(branch.getBranchDesc());
				barnches.setObject(branch);
			} else {
				MessageUtil
						.showError(Labels.getLabel("lable.invalid_Branch") + " :".concat(loanTypeMapping.getBranch()));
				return;
			}
		}

		readOnlyComponent(isReadOnly, barnches);

		barnches.setWidth("80px");
		barnches.setModuleName("CSDBranch");
		barnches.setValueColumn("BranchCode");
		barnches.setDescColumn("BranchDesc");
		barnches.setValidateColumns(new String[] { "BranchCode" });
		listCell.setId("barnches".concat(String.valueOf(loanTypeMapping.getKeyValue())));
		hbox.appendChild(space);
		hbox.appendChild(barnches);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		listCell = new Listcell(recordStatus);
		listCell.setParent(listItem);

		listCell = new Listcell(recordType);
		listCell.setParent(listItem);

		// Delete action
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
			button.setDisabled(isReadOnly("CustomerServiceBranchDialog_Code"));
		}

		listCell.appendChild(button);
		listCell.setParent(listItem);
		button.addForward("onClick", self, "onClickLoanTypeButtonDelete", listItem);

		listItem.setAttribute("data", loanTypeMapping);
		this.listBoxLoanTypes.appendChild(listItem);
		this.listBoxLoanTypes.setHeight(getListBoxHeight(4));
	}

	private boolean savelist(ServiceBranch custServBranch) {
		List<WrongValueException> wve = new ArrayList<>();
		if (this.listBoxLoanTypes.getItemCount() == 0) {
			throw new WrongValueException(this.btnNewLoanTypeBranch, Labels.getLabel("FIELD_IS_MAND",
					new String[] { Labels.getLabel("label_ServiceBranchDialogLoanType.value") }));
		} else {
			custServBranch.getServiceBranchLoanTypeList().clear();
			for (Listitem listitem : listBoxLoanTypes.getItems()) {
				ServiceBranchesLoanType mapping = (ServiceBranchesLoanType) listitem.getAttribute("data");

				List<Listcell> listcels = listitem.getChildren();
				for (Listcell listcell : listcels) {
					try {
						getCompValuetoBean(listcell, mapping);
					} catch (WrongValueException we) {
						wve.add(we);
					}
				}
				boolean isNew = false;

				isNew = mapping.isNewRecord();
				String tranType = "";

				if (isWorkFlowEnabled()) {
					tranType = PennantConstants.TRAN_WF;
					if (StringUtils.isBlank(mapping.getRecordType())) {
						mapping.setVersion(mapping.getVersion() + 1);
						if (isNew) {
							mapping.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						} else {
							mapping.setRecordType(PennantConstants.RECORD_TYPE_UPD);
							mapping.setNewRecord(true);
						}
					}
				} else {
					if (isNew) {
						tranType = PennantConstants.TRAN_ADD;
						mapping.setVersion(1);
						mapping.setRecordType(PennantConstants.RCD_ADD);
					} else {
						tranType = PennantConstants.TRAN_UPD;
					}

					if (StringUtils.isBlank(mapping.getRecordType())) {
						tranType = PennantConstants.TRAN_UPD;
						mapping.setRecordType(PennantConstants.RCD_UPD);
					}
					if (mapping.getRecordType().equals(PennantConstants.RCD_ADD) && isNew) {
						tranType = PennantConstants.TRAN_ADD;
					} else if (mapping.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						tranType = PennantConstants.TRAN_UPD;
					}
				}
				showErrorDetails(wve);
				custServBranch.getServiceBranchLoanTypeList().add(mapping);
			}
		}

		return true;
	}

	private void getCompValuetoBean(Listcell listcell, ServiceBranchesLoanType mapping) {
		String id = StringUtils.trimToNull(listcell.getId());
		if (id == null) {
			return;
		}
		id = id.replaceAll("\\d", "");

		switch (id) {
		case "fintypes":
			Hbox hbox1 = (Hbox) listcell.getFirstChild();
			ExtendedCombobox fintype = (ExtendedCombobox) hbox1.getLastChild();
			Clients.clearWrongValue(fintype);
			String finType = fintype.getValue();

			if (StringUtils.isEmpty(finType)) {
				throw new WrongValueException(fintype, Labels.getLabel("FIELD_IS_MAND", new String[] { "Fin Type " }));
			}
			mapping.setFinType(finType);
			break;
		case "barnches":
			Hbox hbox2 = (Hbox) listcell.getFirstChild();
			ExtendedCombobox branch = (ExtendedCombobox) hbox2.getLastChild();
			Clients.clearWrongValue(branch);
			String code = branch.getValue();

			if (StringUtils.isEmpty(code)) {
				throw new WrongValueException(branch, Labels.getLabel("FIELD_IS_MAND", new String[] { "Branch" }));
			}
			mapping.setBranch(code);
			break;
		default:
			break;
		}
	}

	public void onClickLoanTypeButtonDelete(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		Listitem item = (Listitem) event.getData();
		ServiceBranchesLoanType branchMapping = (ServiceBranchesLoanType) item.getAttribute("data");

		if (branchMapping.isNewRecord()) {
			listBoxLoanTypes.removeItemAt(item.getIndex());
		} else {
			if ("Approved".equals(branchMapping.getRecordStatus())) {
				branchMapping.setNewRecord(true);
			}

			branchMapping.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			if ((item.getIndex() == 0) && (item.getNextSibling() == null)) {
				MessageUtil.showError("At Least Loan Type details required.");
			} else {
				listBoxLoanTypes.removeItemAt(item.getIndex());
				renderItem(branchMapping);
			}
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
				logger.debug(wvea[i]);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug(Literal.LEAVING);
	}

	public void doFillCheckListDetailsList(List<ServiceBranchesLoanType> list) {
		logger.debug("Entering");

		this.listBoxLoanTypes.getItems().clear();
		int keyValue = 0;
		for (ServiceBranchesLoanType serLoanType : list) {
			serLoanType.setKeyValue(keyValue + 1);
			renderItem(serLoanType);
			keyValue = serLoanType.getKeyValue();
		}

		logger.debug("Leaving ");
	}

	private AuditHeader getAuditHeader(ServiceBranch csb, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, csb.getBefImage(), csb);
		return new AuditHeader(String.valueOf(csb.getId()), null, null, null, auditDetail, csb.getUserDetails(),
				getOverideMap());
	}

	@Autowired
	public void setServiceBranchService(ServiceBranchService serviceBranchService) {
		this.serviceBranchService = serviceBranchService;
	}

	public void setServiceBranchListCtrl(ServiceBranchListCtrl serviceBranchListCtrl) {
		this.serviceBranchListCtrl = serviceBranchListCtrl;
	}
}
