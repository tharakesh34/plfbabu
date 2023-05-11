package com.pennant.pff.upload.list;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.A;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Center;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.North;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.util.PennantConstants;
import com.pennant.component.Uppercasebox;
import com.pennant.pff.upload.model.FieUploadDTO;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;
import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.dataengine.UploadHandler;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.util.ExcelUtil;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.file.UploadContants.Status;
import com.pennanttech.pff.file.UploadStatus;
import com.pennanttech.pff.file.UploadTypes;

public class FileUploadList extends Window implements Serializable {
	private static final long serialVersionUID = 6151629601007479272L;
	private static final Logger logger = LogManager.getLogger(FileUploadList.class);

	private Window window;
	private Paging paging;
	private PagedListWrapper<FileUploadHeader> listWrapper;
	private Textbox uploadFileName;
	private Uppercasebox usrLogin;
	private Datebox fromDate;
	private Datebox toDate;

	private UploadTypes type;
	private List<String> workflowRoles;

	private Listheader listHeader;
	private Checkbox checkBoxComp;

	private FileUploadHeader fileUploadHeader;

	private transient UploadService uploadService;
	private transient DataSource dataSource;
	private String stage;

	private Listbox listbox;

	private ExtendedCombobox entityCode;
	private ExtendedCombobox fileName;

	private List<FileUploadHeader> selectedHeaders = new ArrayList<>();

	private long userId;

	private int listRows = 10;
	private static final int LIST_ROW_HEIGHT = 28;
	private static final int ROW_HEIGHT = 26;

	private static final String ALIGN_CENTER = "center";
	private static final String ALIGN_START = "start";
	private static final String ALIGN_END = "end";
	private static final String ALIGN_RIGHT = "right";

	private static final String FLEX_MIN = "min";

	public FileUploadList() {
		super();
	}

	public FileUploadList(FieUploadDTO object, UploadTypes type) {
		super();

		this.window = object.getWindow();
		this.userId = object.getUserId();
		this.uploadService = object.getService();
		this.dataSource = object.getDataSource();
		this.listWrapper = object.getListWrapper();
		this.fileUploadHeader = object.getHeader();
		this.stage = object.getStage();
		this.type = type;
		this.workflowRoles = object.getRoleCodes();

		createBox();
	}

	private void createBox() {
		logger.debug(Literal.ENTERING);

		window.appendChild(createBorderlayout());

		try {
			doHighlighted();
		} catch (final SuspendNotAllowedException e) {
			logger.error(Literal.EXCEPTION, e);
			this.detach();
		}

		logger.debug(Literal.LEAVING);
	}

	private Borderlayout createBorderlayout() {
		Borderlayout bl = new Borderlayout();
		bl.appendChild(getNorth());
		bl.appendChild(createCenter());

		return bl;
	}

	private Center createCenter() {
		Center center = getCenter();

		Div div = getDiv();
		div.setSclass("z-toolbar;background:white");

		if ("M".equals(this.stage)) {
			div.appendChild(createUploadGroupBox());
		}

		div.appendChild(createDownloadGroupBox());

		this.listbox = createListbox();
		this.listbox.setItemRenderer(new UploadListItemRenderer());

		this.paging = new Paging();
		this.paging.setPageSize(listRows);
		this.paging.setDetailed(true);

		div.appendChild(this.listbox);
		div.appendChild(this.paging);

		center.appendChild(div);
		return center;
	}

	private Listbox createListbox() {
		Listbox box = getListBox();
		box.setHeight(getListBoxHeight(4));

		Listhead listhead = new Listhead();

		if (!"M".equals(this.stage)) {
			this.listHeader = new Listheader();
			this.listHeader.setHflex(FLEX_MIN);
			this.listHeader.setAlign(ALIGN_CENTER);

			this.checkBoxComp = new Checkbox();
			this.checkBoxComp.setDisabled(true);
			this.checkBoxComp.addEventListener(Events.ON_CLICK, event -> onClickListHeaderCheckBox());

			this.listHeader.appendChild(this.checkBoxComp);
			listhead.appendChild(this.listHeader);
		}

		listhead.appendChild(getHFlexListHeader(Labels.getLabel("label_UploadId"), FLEX_MIN));
		listhead.appendChild(getHFlexListHeader(Labels.getLabel("label_FileName"), FLEX_MIN));

		if ("A".equals(this.stage)) {
			listhead.appendChild(getHFlexListHeader(Labels.getLabel("label_UserName"), FLEX_MIN));
		}

		listhead.appendChild(getListHeader(Labels.getLabel("label_TotalRecords"), FLEX_MIN, ALIGN_RIGHT));
		listhead.appendChild(getListHeader(Labels.getLabel("label_ProcessedRecords"), FLEX_MIN, ALIGN_RIGHT));
		listhead.appendChild(getListHeader(Labels.getLabel("label_SuccessRecords"), FLEX_MIN, ALIGN_RIGHT));
		listhead.appendChild(getListHeader(Labels.getLabel("label_FailedRecords"), FLEX_MIN, ALIGN_RIGHT));
		listhead.appendChild(getHFlexListHeader(Labels.getLabel("label_Progress"), FLEX_MIN));
		listhead.appendChild(getHFlexListHeader(Labels.getLabel("label.RecordStatus"), FLEX_MIN));
		listhead.appendChild(getHFlexListHeader(Labels.getLabel("label_Download"), FLEX_MIN));

		box.appendChild(listhead);

		return box;
	}

	public void onClickListHeaderCheckBox() {
		selectedHeaders.clear();
		for (Listitem listitem : listbox.getItems()) {
			Checkbox cb = (Checkbox) listitem.getChildren().get(0).getChildren().get(0);
			cb.setChecked(this.checkBoxComp.isChecked());

			if (cb.isChecked()) {
				selectedHeaders.add((FileUploadHeader) listitem.getAttribute("data"));
			}
		}

		checkBoxComp.setChecked(selectedHeaders.size() == listbox.getItems().size());
	}

	private Groupbox createDownloadGroupBox() {
		Groupbox groupbox = getGroupBox();

		if ("M".equals(this.stage)) {
			groupbox.appendChild(getCaption("Download"));
		}

		Div div = getDiv();
		div.appendChild(createDownloadToolBar());
		groupbox.appendChild(div);
		groupbox.appendChild(createDownloadGrid());

		return groupbox;
	}

	private Groupbox createUploadGroupBox() {
		Groupbox groupbox = getGroupBox();

		groupbox.appendChild(getCaption("Upload"));

		Div div = getDiv();
		div.appendChild(createUploadToolBar());
		groupbox.appendChild(div);
		groupbox.appendChild(createUploadGrid());

		return groupbox;
	}

	private Hbox createUploadToolBar() {
		FileUploadHeader fuph = new FileUploadHeader();
		Hbox hbox = getHbox();
		hbox.appendChild(getToolbar(ALIGN_START));

		Toolbar center = getButtonInTB(ALIGN_CENTER, "IMPORT", Events.ON_CLICK, event -> onClickButtonImport());
		center.appendChild(getSpace("2px", false));
		center.appendChild(getTollBarButton("REFRESH", Events.ON_CLICK, event -> uploadRefresh()));
		hbox.appendChild(center);

		hbox.appendChild(
				getHyperLinkInTB(ALIGN_END, "Download Template", Events.ON_CLICK, event -> downloadTemplate(fuph)));

		return hbox;
	}

	private Hbox createDownloadToolBar() {
		Hbox hbox = getHbox();

		if (!"M".equals(this.stage)) {
			Toolbar start = getButtonInTB(ALIGN_START, "APPROVE", Events.ON_CLICK, event -> onClickApprove());
			start.appendChild(getSpace("2px", false));
			start.appendChild(getTollBarButton("REJECT", Events.ON_CLICK, event -> onClickReject()));
			hbox.appendChild(start);
		} else {
			hbox.appendChild(getToolbar(ALIGN_START));
		}

		Toolbar center = getButtonInTB(ALIGN_CENTER, "SEARCH", Events.ON_CLICK, event -> onClickSearch());
		center.appendChild(getSpace("2px", false));
		center.appendChild(getTollBarButton("REFRESH", Events.ON_CLICK, event -> downloadRefresh()));
		hbox.appendChild(center);

		hbox.appendChild(getToolbar(ALIGN_END));

		return hbox;
	}

	private Grid createDownloadGrid() {
		Grid grid = getGrid();

		Columns columns = new Columns();
		columns.appendChild(getColumn("15%"));
		columns.appendChild(getColumn("5%"));
		columns.appendChild(getColumn("30%"));
		columns.appendChild(getColumn("15%"));
		columns.appendChild(getColumn("5%"));
		columns.appendChild(getColumn("30%"));

		Rows rows = new Rows();
		rows.appendChild(appendDateFilters());
		rows.appendChild(appendECBFileName());

		if ("A".equals(this.stage)) {
			rows.appendChild(appendUserName());
		}

		grid.appendChild(columns);
		grid.appendChild(rows);

		return grid;
	}

	private Grid createUploadGrid() {
		Grid grid = getGrid();

		Columns columns = new Columns();
		columns.appendChild(getColumn("15%"));
		columns.appendChild(getColumn("5%"));
		columns.appendChild(getColumn("30%"));
		columns.appendChild(getColumn("15%"));
		columns.appendChild(getColumn("5%"));
		columns.appendChild(getColumn("30%"));

		Rows rows = new Rows();
		rows.appendChild(appendEntityCode());
		rows.appendChild(appendFileName());

		grid.appendChild(columns);
		grid.appendChild(rows);

		return grid;
	}

	private Row appendEntityCode() {
		Row row = new Row();

		this.entityCode = new ExtendedCombobox();
		// this.entityCode.setRemoveSpace(true);
		this.entityCode.setModuleName("Entity");
		this.entityCode.setMandatoryStyle(true);
		this.entityCode.setDisplayStyle(2);
		this.entityCode.setValueColumn("EntityCode");
		this.entityCode.setDescColumn("EntityDesc");
		this.entityCode.setValidateColumns(new String[] { "EntityCode" });
		this.entityCode.addForward(ExtendedCombobox.ON_FUL_FILL, this, "onChangeEntityCode", null);

		Cell cell = new Cell();
		cell.appendChild(new Label(Labels.getLabel("label_EntityCode")));
		row.appendChild(cell);

		cell = new Cell();
		cell.appendChild(getOperators(Filter.OP_EQUAL));
		row.appendChild(cell);

		cell = new Cell();
		cell.appendChild(this.entityCode);

		row.appendChild(cell);

		List<Entity> entity = uploadService.getEntities();

		if (entity.size() == 1) {
			this.entityCode.setValue(entity.get(0).getEntityCode());
			this.entityCode.setDescColumn(entity.get(0).getEntityDesc());
			this.entityCode.setReadonly(true);
		}

		return row;
	}

	private Row appendUserName() {
		Row row = new Row();

		Cell cell = new Cell();
		cell.appendChild(new Label(Labels.getLabel("label_UserName")));
		row.appendChild(cell);

		cell = new Cell();
		cell.appendChild(getOperators(Filter.OP_EQUAL));
		row.appendChild(cell);

		cell = new Cell();

		cell.setColspan(2);

		Hbox hbox = new Hbox();

		this.usrLogin = new Uppercasebox();
		this.usrLogin.setWidth("240px");
		this.usrLogin.setReadonly(false);
		hbox.appendChild(getSpace("2px", false));
		hbox.appendChild(this.usrLogin);

		cell.appendChild(hbox);
		row.appendChild(cell);

		return row;
	}

	private Row appendFileName() {
		Row row = new Row();

		Cell cell = new Cell();
		cell.appendChild(new Label(Labels.getLabel("label_FileName")));
		row.appendChild(cell);

		cell = new Cell();
		cell.appendChild(getOperators(Filter.OP_EQUAL));
		row.appendChild(cell);

		cell = new Cell();

		cell.setColspan(2);

		Hbox hbox = new Hbox();
		hbox.appendChild(getSpace("2px", true));

		this.uploadFileName = new Textbox();
		uploadFileName.setWidth("320px");
		uploadFileName.setReadonly(true);

		hbox.appendChild(uploadFileName);

		Button button = getButton(Labels.getLabel("btnBrowse.label"));
		button.setUpload("true");
		button.addEventListener(Events.ON_UPLOAD, event -> {
			downloadRefresh();

			Media media = ((UploadEvent) event).getMedia();

			this.uploadFileName.setText("");

			String name = media.getName();

			this.fileUploadHeader.setMedia(media);
			this.fileUploadHeader.setFile(new File(media.getName()));
			this.uploadFileName.setText(name);
			this.fileUploadHeader.setFileName(name);
		});

		hbox.appendChild(button);

		cell.appendChild(hbox);
		row.appendChild(cell);

		return row;
	}

	private Row appendDateFilters() {
		Row row = new Row();

		Cell cell = new Cell();
		cell.appendChild(new Label("From Date"));
		row.appendChild(cell);

		cell = new Cell();
		cell.appendChild(getOperators(Filter.OP_EQUAL));
		row.appendChild(cell);

		cell = new Cell();
		Hbox hbox = new Hbox();
		this.fromDate = new Datebox();
		this.fromDate.setWidth("150px");
		this.fromDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.fromDate.addForward(Events.ON_CHANGE, this, "onChangeDateFilters", null);
		hbox.appendChild(getSpace("2px", "M".equals(this.stage)));
		hbox.appendChild(this.fromDate);

		cell.appendChild(hbox);
		row.appendChild(cell);

		cell = new Cell();
		cell.appendChild(new Label("To Date"));
		row.appendChild(cell);

		cell = new Cell();
		cell.appendChild(getOperators(Filter.OP_EQUAL));
		row.appendChild(cell);

		cell = new Cell();
		hbox = new Hbox();
		this.toDate = new Datebox();
		this.toDate.setWidth("150px");
		this.toDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.toDate.addForward(Events.ON_CHANGE, this, "onChangeDateFilters", null);
		hbox.appendChild(getSpace("2px", "M".equals(this.stage)));
		hbox.appendChild(this.toDate);

		cell.appendChild(hbox);
		row.appendChild(cell);

		return row;
	}

	private Row appendECBFileName() {
		Row row = new Row();

		this.fileName = new ExtendedCombobox();
		this.fileName.setModuleName("FileUploadHeader");
		this.fileName.setDisplayStyle(2);
		this.fileName.setValueColumn("Id");
		this.fileName.setDescColumn("FileName");
		this.fileName.setValueType(DataType.LONG);
		this.fileName.setValidateColumns(new String[] { "Id" });

		if ("M".equals(this.stage)) {
			Filter[] filters = new Filter[1];
			filters[0] = new Filter("Type", this.fileUploadHeader.getType(), Filter.OP_EQUAL);
			this.fileName.setFilters(filters);
		} else {

			Filter[] filters = new Filter[2];
			filters[0] = new Filter("Type", this.fileUploadHeader.getType(), Filter.OP_EQUAL);
			filters[1] = new Filter("NextRoleCode", this.workflowRoles, Filter.OP_IN);
			this.fileName.setFilters(filters);

		}

		if (!"M".equals(this.stage)) {
			row = appendEntityCode();
		}

		Cell cell = new Cell();
		cell.appendChild(new Label(Labels.getLabel("label_FileName")));
		row.appendChild(cell);

		cell = new Cell();
		cell.appendChild(getOperators(Filter.OP_EQUAL));
		row.appendChild(cell);

		cell = new Cell();
		cell.setColspan(4);
		cell.appendChild(this.fileName);

		row.appendChild(cell);

		return row;
	}

	public void onChangeDateFilters(ForwardEvent event) {

		if (this.fromDate.getValue() != null && this.toDate.getValue() != null) {
			String frmDate = PennantAppUtil.formateDate(this.fromDate.getValue(), PennantConstants.DBDateFormat);
			String toDte = PennantAppUtil.formateDate(DateUtil.addDays(this.toDate.getValue(), 1),
					PennantConstants.DBDateFormat);

			Filter[] filters = new Filter[4];

			if ("A".equals(this.stage) && StringUtils.isNotEmpty(this.entityCode.getValue())) {
				filters = new Filter[5];
			}

			filters[0] = new Filter("Type", this.fileUploadHeader.getType(), Filter.OP_EQUAL);
			filters[1] = new Filter("CreatedOn", frmDate, Filter.OP_GREATER_OR_EQUAL);
			filters[2] = new Filter("CreatedOn", toDte, Filter.OP_LESS_THAN);
			filters[3] = new Filter("Progress", Status.IN_PROCESS.getValue(), Filter.OP_NOT_EQUAL);

			if ("A".equals(this.stage) && StringUtils.isNotEmpty(this.entityCode.getValue())) {
				filters[4] = new Filter("EntityCode", this.entityCode.getValue(), Filter.OP_EQUAL);
			}

			this.fileName.setFilters(filters);

			this.fileName.setWhereClause("NextRoleCode is null or NextRoleCode in (".concat(getRoles()).concat(")"));
		}
	}

	private String getRoles() {
		StringBuilder whereClause = new StringBuilder("");

		for (String roleCode : this.workflowRoles) {
			if (whereClause.length() > 1) {
				whereClause.append(", ");
			}

			whereClause.append("'").append(roleCode).append("'");
		}

		return whereClause.toString();
	}

	public void onChangeEntityCode(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		if (StringUtils.isBlank(this.entityCode.getValue())) {
			this.entityCode.setValue("", "");
		}

		if (StringUtils.isNotBlank(this.entityCode.getValue())) {
			Clients.clearWrongValue(entityCode);
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClickButtonImport() {
		logger.debug(Literal.ENTERING);

		this.entityCode.clearErrorMessage();

		this.fileUploadHeader.setEntityCode(this.entityCode.getValue());

		doWriteComponentsToBean(this.fileUploadHeader);

		UploadHandler dataEngine = new UploadHandler(type.name().concat("_UPLOAD"), this.dataSource);

		try {
			dataEngine.importFile(this.fileUploadHeader);
		} catch (Exception e) {
			MessageUtil.showError(e.getMessage());
		}

		doSearch(true);

		logger.debug(Literal.LEAVING);
	}

	private void search(boolean isApprove) {
		List<FileUploadHeader> list = getUploadHeaders();

		selectedHeaders.clear();
		if (!"M".equals(this.stage) && !list.isEmpty()) {
			this.checkBoxComp.setDisabled(false);
		}

		listbox.clearSelection();

		list.forEach(h1 -> {
			h1.setUserDetails(this.fileUploadHeader.getUserDetails());
			h1.setStage(this.stage);
		});

		listWrapper.initList(list, listbox, paging);
	}

	private void doSearch(boolean upload) {
		if (upload) {
			this.fileName.setValue(String.valueOf(this.fileUploadHeader.getId()));
			this.fileName.setDescription(this.fileUploadHeader.getFileName());

			this.fromDate.setValue(DateUtil.getSysDate());
			this.toDate.setValue(DateUtil.getSysDate());

			List<FileUploadHeader> list = new ArrayList<>();

			this.fileUploadHeader.setStage(this.stage);

			list.add(this.fileUploadHeader);

			listbox.clearSelection();

			if (!"M".equals(this.stage) && !list.isEmpty()) {
				this.checkBoxComp.setDisabled(false);
			}

			listWrapper.initList(list, listbox, paging);
		} else {
			search(false);
		}

		selectedHeaders = new ArrayList<>();
	}

	private List<FileUploadHeader> getUploadHeaders() {
		Date dataFrom = null;
		Date dataTo = null;
		Long fileID = null;
		String eCode = null;
		String usrLogin = null;

		List<WrongValueException> wve = new ArrayList<>();

		setConstraints();

		try {
			eCode = this.entityCode.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			dataFrom = this.fromDate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			dataTo = this.toDate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (StringUtils.isNotEmpty(this.fileName.getValue())) {
				fileID = Long.valueOf(this.fileName.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if ("A".equals(this.stage)) {
			try {
				usrLogin = this.usrLogin.getValue();
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		doRemoveValidation();

		showErrorMessage(wve);

		return uploadService.getUploadHeaderById(this.workflowRoles, eCode, fileID, dataFrom, dataTo, type.name(),
				this.stage, usrLogin);
	}

	private void setConstraints() {
		if ("M".equals(this.stage)) {
			this.fromDate.setConstraint(new PTDateValidator("From Date", true));
			this.toDate.setConstraint(new PTDateValidator("To Date", true));
		} else {
			if (!this.entityCode.isReadonly()) {
				this.entityCode
						.setConstraint(new PTStringValidator(Labels.getLabel("label_EntityCode"), null, true, true));
			}
		}

	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.fromDate.setConstraint("");
		this.fromDate.setErrorMessage("");

		this.toDate.setConstraint("");
		this.toDate.setErrorMessage("");

		this.fileName.setConstraint("");
		this.fileName.setErrorMessage("");

		this.entityCode.setConstraint("");
		this.entityCode.setErrorMessage("");

		if ("A".equals(this.stage)) {
			this.usrLogin.setConstraint("");
			this.usrLogin.setErrorMessage("");
		}

		logger.debug(Literal.LEAVING);
	}

	private void showErrorMessage(List<WrongValueException> wve) {
		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}

			throw new WrongValuesException(wvea);
		}
	}

	private void doWriteComponentsToBean(FileUploadHeader header) {
		String name = this.uploadFileName.getValue();

		List<WrongValueException> wve = new ArrayList<>();

		setConstraints();

		try {
			ExcelUtil.isValidFile(name, 100, "^[a-zA-Z0-9 ._]*$");
		} catch (AppException e) {
			wve.add(new WrongValueException(this.uploadFileName, e.getMessage()));
		}

		try {
			if (!this.entityCode.isReadonly()) {
				this.entityCode
						.setConstraint(new PTStringValidator(Labels.getLabel("label_EntityCode"), null, true, true));
				header.setEntityCode(this.entityCode.getValue());
			}
		} catch (WrongValueException e) {
			wve.add(new WrongValueException(this.entityCode, e.getMessage()));
		}

		doRemoveValidation();

		showErrorMessage(wve);

		header.setFileName(name);

		header.setProgress(Status.DEFAULT.getValue());
	}

	private void downloadTemplate(FileUploadHeader fuph) {
		String name = this.type.name().concat("_TEMPLATE");
		fileDownload(name, fuph);
	}

	private void fileDownload(String name, FileUploadHeader fuph) {
		DataEngineExport export = null;

		if (name.contains("_DOWNLOAD")) {
			export = new DataEngineExport(dataSource, userId, App.DATABASE.name(), true,
					this.fileUploadHeader.getAppDate());

			Map<String, Object> parameterMap = new HashMap<>();
			parameterMap.put("QUERY", uploadService.getSqlQuery());

			Map<String, Object> filterMap = new HashMap<>();
			filterMap.put("HEADER_ID", fuph.getId());
			export.setParameterMap(parameterMap);
			export.setFilterMap(filterMap);
		} else {
			export = new DataEngineExport(dataSource, App.DATABASE.name());
		}

		try {
			export.exportData(name, false);
		} catch (Exception e) {
			throw new AppException(e.getMessage());
		}

		DataEngineStatus deStatus = export.getDataEngineStatus();

		if ("F".equals(deStatus.getStatus())) {
			MessageUtil.showError("Unable to download the file, Please contact system administrator.");
			return;
		}

		Configuration configuration = deStatus.getConfiguration();

		File file = new File(configuration.getUploadPath().concat(File.separator).concat(deStatus.getFileName()));

		try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
			fileDownload(deStatus, file, stream);
		} catch (Exception e) {
			logger.warn("Unable to download the selected file, Please contact system administrator.");
			return;
		}

		if ("A".equals(this.stage)) {
			uploadService.updateDownloadStatus(fuph.getId(), Status.DOWNLOADED.getValue());
		}
	}

	private void fileDownload(DataEngineStatus deStatus, File file, ByteArrayOutputStream stream) {
		try (InputStream inputStream = new FileInputStream(file)) {
			int data;
			while ((data = inputStream.read()) >= 0) {
				stream.write(data);
			}

			Filedownload.save(stream.toByteArray(), "application/octet-stream", deStatus.getFileName());
		} catch (Exception e) {
			logger.warn("Unable to download the selected file, Please contact system administrator.");
		}
	}

	private void onClickSearch() {
		if (this.fromDate.getValue() != null) {
			this.toDate.setConstraint(new PTDateValidator(Labels.getLabel("label_ToDate.value"), false,
					this.fromDate.getValue(), null, true));
			try {
				this.toDate.getValue();
			} catch (WrongValueException e) {
				throw new WrongValueException(this.toDate, e.getMessage());
			}
		}

		search(false);
	}

	private void uploadRefresh() {
		headerRefresh();

		if (!this.entityCode.isReadonly()) {
			this.entityCode.clearErrorMessage();
			this.entityCode.setConstraint("");
			this.entityCode.setErrorMessage("");
			this.entityCode.setValue(null);

			Clients.clearWrongValue(this.entityCode);
		}

		if ("M".equals(this.stage)) {
			this.uploadFileName.setValue("");
		}

		if (this.fromDate.getValue() != null && this.toDate.getValue() != null) {
			search(false);
		}
	}

	private void downloadRefresh() {
		headerRefresh();

		this.fromDate.setRawValue("");
		this.fromDate.setConstraint("");
		this.fromDate.setErrorMessage("");

		this.toDate.setRawValue("");
		this.fromDate.setConstraint("");
		this.fromDate.setErrorMessage("");

		this.fileName.setValue("", "");
		this.fileName.setConstraint("");
		this.fileName.setErrorMessage("");
		this.fromDate.setValue(null);
		this.toDate.setValue(null);

		if (!"M".equals(this.stage)) {

			if (!this.entityCode.isReadonly()) {
				this.entityCode.clearErrorMessage();
				this.entityCode.setConstraint("");
				this.entityCode.setErrorMessage("");
				this.entityCode.setValue(null);
			}

			this.fromDate.setConstraint("");
			this.fromDate.setErrorMessage("");
			this.fromDate.setValue(null);

			this.toDate.setConstraint("");
			this.toDate.setErrorMessage("");
			this.toDate.setValue(null);

			if ("A".equals(this.stage)) {
				this.usrLogin.setValue("");
				this.usrLogin.setConstraint("");
				this.usrLogin.setErrorMessage("");
			}

			selectedHeaders.clear();
			checkBoxComp.setChecked(false);
			listbox.getItems().clear();
			listbox.clearSelection();
		}

	}

	private void headerRefresh() {
		this.fileUploadHeader.setId(Long.MIN_VALUE);
		this.fileUploadHeader.setFileName(null);
		this.fileUploadHeader.setFile(null);
		this.fileUploadHeader.setMedia(null);
		this.fileUploadHeader.setTotalRecords(0);
		this.fileUploadHeader.setSuccessRecords(0);
		this.fileUploadHeader.setFailureRecords(0);
		this.fileUploadHeader.setProgress(0);
		this.fileUploadHeader.setRemarks(null);
		this.fileUploadHeader.setStage(this.stage);

		listbox.getItems().clear();
		listbox.clearSelection();
	}

	private void onClickReject() {

		if (selectedHeaders.isEmpty()) {
			MessageUtil.showError(Labels.getLabel("DataList_NoEmpty"));
			return;
		}

		String uploadIDs = uploadService.isValidateApprove(selectedHeaders);
		if (StringUtils.isNotEmpty(uploadIDs)) {
			MessageUtil.showError(
					Labels.getLabel("DOWNLOAD_MANDATORY", new Object[] { "Upload ID : (" + uploadIDs + ")" }));
			return;
		}

		for (FileUploadHeader header : selectedHeaders) {
			header.setLastMntBy(this.userId);
			header.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			header.setProgress(UploadStatus.REJECTED.status());
		}

		uploadService.doReject(selectedHeaders);

		doSearch(false);
	}

	private void onClickApprove() {
		logger.debug(Literal.ENTERING);

		if (CollectionUtils.isEmpty(selectedHeaders)) {
			MessageUtil.showError(Labels.getLabel("DataList_NoEmpty"));
			return;
		}

		String uploadIDs = uploadService.isValidateApprove(selectedHeaders);
		if (StringUtils.isNotEmpty(uploadIDs)) {
			MessageUtil.showError(
					Labels.getLabel("DOWNLOAD_MANDATORY", new Object[] { "Upload ID : (" + uploadIDs + ")" }));
			return;
		}

		List<FileUploadHeader> headers = new ArrayList<>();
		for (FileUploadHeader header : selectedHeaders) {
			header.setLastMntBy(this.userId);
			header.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			header.setApprovedBy(this.userId);
			header.setApprovedOn(new Timestamp(System.currentTimeMillis()));
			header.setProgress(UploadStatus.APPROVE.status());
			uploadService.update(header);

			headers.add(header);
		}

		search(true);
		logger.debug(Literal.LEAVING);
	}

	private Space getSpace(String width, boolean mandatory) {
		Space space = new Space();
		space.setWidth(width);

		if (mandatory) {
			space.addSclass("mandatory");
		}

		return space;
	}

	private Div getDiv() {
		Div div = new Div();

		div.setSclass("z-toolbar");
		div.setStyle("padding:0px;height:28px;");
		div.setWidth("100%");

		return div;
	}

	private Hbox getHbox() {
		Hbox hbox = new Hbox();

		hbox.setPack("stretch");
		hbox.setSclass("hboxRemoveWhiteStrips");
		hbox.setWidth("100%");
		hbox.setWidths("30%,40%,30%");

		return hbox;
	}

	private Groupbox getGroupBox() {
		Groupbox groupbox = new Groupbox();
		groupbox.setWidth("100%");

		return groupbox;
	}

	private Caption getCaption(String label) {
		Caption caption = new Caption();
		caption.setLabel(label);
		caption.setStyle("padding:0px;height:40px;font-size:15px;");

		return caption;
	}

	private Grid getGrid() {
		Grid grid = new Grid();

		grid.setSclass("GridLayoutNoBorder");
		grid.setSizedByContent(false);
		grid.setStyle("border:0px; padding-left:5px; padding-right:5px;");

		return grid;
	}

	private Listbox getListBox() {
		Listbox box = new Listbox();

		box.setEmptyMessage(Labels.getLabel("listbox.emptyMessage"));
		box.setSizedByContent(true);
		box.setSpan(true);
		box.setTooltip(Labels.getLabel("listbox.tooltiptext"));
		box.setWidth("100%");
		box.setMultiple(false);
		box.setStyle("white-space: nowrap;");

		return box;
	}

	private Listheader getListHeader(String label) {
		return new Listheader(label);
	}

	private Listheader getHFlexListHeader(String label, String hflex) {
		Listheader header = getListHeader(label);
		header.setHflex(hflex);

		return header;
	}

	private Listheader getListHeader(String label, String hflex, String align) {
		Listheader header = getListHeader(label);
		header.setHflex(hflex);
		header.setAlign(align);

		return header;
	}

	private Button getButton(String label) {
		Button button = new Button();

		button.setLabel(label);
		button.setId(label);
		button.setTooltiptext(label);
		button.setAutodisable(getScreenButtons());

		return button;
	}

	private North getNorth() {
		North north = new North();
		north.setBorder("none");
		north.setHeight("0px");

		return north;
	}

	private Center getCenter() {
		Center center = new Center();
		center.setBorder("none");

		return center;
	}

	private Column getColumn(String width) {
		Column column = new Column();
		column.setWidth(width);

		return column;
	}

	private Toolbar getToolbar(String align) {
		Toolbar toolBar = new Toolbar();

		switch (align) {
		case ALIGN_START:
			toolBar.setAlign(ALIGN_START);
			toolBar.setSclass("toolbar-start");
			break;
		case ALIGN_CENTER:
			toolBar.setAlign(ALIGN_CENTER);
			toolBar.setSclass("toolbar-center");
			break;
		case ALIGN_END:
			toolBar.setAlign(ALIGN_END);
			toolBar.setSclass("toolbar-end");
			break;

		default:
			break;
		}

		return toolBar;
	}

	private Button getTollBarButton(String label, String evtnm, EventListener<Event> eventListener) {
		Button btn = new Button();
		btn.setLabel(label);
		btn.setId(label);
		btn.setSclass("z-toolbarbutton");
		btn.addEventListener(evtnm, eventListener);
		btn.setAutodisable(getScreenButtons());

		return btn;
	}

	private Toolbar getButtonInTB(String align, String btnLabel, String evtnm, EventListener<Event> eventListener) {
		Toolbar toolBar = getToolbar(align);

		toolBar.appendChild(getTollBarButton(btnLabel, evtnm, eventListener));

		return toolBar;
	}

	private Toolbar getHyperLinkInTB(String align, String label, String evtnm, EventListener<Event> eventListener) {
		Toolbar toolBar = getToolbar(align);

		A a = new A();
		a.setLabel(label);

		a.addEventListener(evtnm, eventListener);

		toolBar.appendChild(a);
		return toolBar;
	}

	private Listbox getOperators(Integer... operaotors) {
		Listbox box = new Listbox();
		box.setModel(SearchOperators.getOperators(operaotors));
		box.setItemRenderer(new SearchOperatorListModelItemRenderer());
		box.setMold("select");
		box.setRows(1);

		return box;
	}

	private String getListBoxHeight(int gridRowCount) {
		int listBoxHeight = getContentAreaHeight();
		listBoxHeight = listBoxHeight - (gridRowCount * ROW_HEIGHT) - (ROW_HEIGHT);
		this.listRows = Math.round(listBoxHeight / LIST_ROW_HEIGHT) - 1;

		if ("M".equals(this.stage)) {
			return listBoxHeight - 125 + "px";
		}

		return listBoxHeight + 30 + "px";
	}

	private int getContentAreaHeight() {
		return getDesktopHeight() - 58;
	}

	private int getDesktopHeight() {
		Intbox desktopHeight = (Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight");

		return desktopHeight.getValue() == null ? 0 : desktopHeight.getValue().intValue();
	}

	private String getScreenButtons() {
		List<String> buttons = new ArrayList<>();

		buttons.add("IMPORT");
		buttons.add("SEARCH");
		buttons.add("REFRESH");
		buttons.add(Labels.getLabel("label_Download"));
		buttons.add(Labels.getLabel("btnBrowse.label"));

		return JdbcUtil.getInCondition(buttons);
	}

	private class UploadListItemRenderer implements ListitemRenderer<FileUploadHeader>, Serializable {
		private static final long serialVersionUID = -2313478930487980639L;

		@Override
		public void render(Listitem item, FileUploadHeader uph, int count) throws Exception {
			long id = uph.getId();

			if (id <= 0) {
				return;
			}

			item.setAttribute("id", id);
			item.setAttribute("data", uph);

			if (!"M".equals(uph.getStage())) {
				Listcell lc = new Listcell();
				Checkbox cb = appendSelectBox();

				if (checkBoxComp.isChecked()) {
					cb.setChecked(true);
					selectedHeaders.add(uph);
				}

				lc.appendChild(cb);
				lc.setParent(item);
			}

			Listcell lc = new Listcell(String.valueOf(id));
			lc.setParent(item);

			lc = new Listcell(uph.getFileName());
			lc.setParent(item);

			if ("A".equals(uph.getStage())) {
				lc = new Listcell(uph.getUserDetails().getUserName());
				lc.setParent(item);
			}

			lc = new Listcell(String.valueOf(uph.getTotalRecords()));
			lc.setParent(item);

			lc = new Listcell(String.valueOf(uph.getSuccessRecords() + uph.getFailureRecords()));
			lc.setParent(item);

			lc = new Listcell(String.valueOf(uph.getSuccessRecords()));
			lc.setParent(item);

			lc = new Listcell(String.valueOf(uph.getFailureRecords()));
			lc.setParent(item);

			lc = new Listcell(UploadStatus.valueOf(uph.getProgress()).name());
			lc.setParent(item);

			lc = new Listcell(uph.getRecordStatus());
			lc.setParent(item);

			Button dowButton = getButton(Labels.getLabel("label_Download"), String.valueOf(id));
			dowButton.addEventListener(Events.ON_CLICK, event -> onClickDownload(uph));
			dowButton.setDisabled(false);

			lc = new Listcell();
			lc.appendChild(dowButton);
			lc.setParent(item);

			uph.setDownloadReq(!dowButton.isDisabled());

			item.setAttribute("id", id);
			item.setAttribute("data", uph);

			ComponentsCtrl.applyForward(item, "onDoubleClick=onItemDoubleClicked");
		}

		private Button getButton(String label, String id) {
			Button button = new Button();

			button.setLabel(label);
			button.setId(label.concat(id));
			button.setTooltiptext(label);
			button.setAutodisable(getScreenButtons());

			return button;
		}

		private Checkbox appendSelectBox() {
			Checkbox checkBox = new Checkbox();
			checkBox.setDisabled(false);
			checkBox.addEventListener(Events.ON_CLICK, event -> {
				selectedHeaders.clear();
				for (Listitem listitem : listbox.getItems()) {
					Checkbox cb = (Checkbox) listitem.getChildren().get(0).getChildren().get(0);
					if (cb.isChecked()) {
						selectedHeaders.add((FileUploadHeader) listitem.getAttribute("data"));
					}
				}

				checkBoxComp.setChecked(selectedHeaders.size() == listbox.getItems().size());
			});

			return checkBox;
		}

		private void onClickDownload(FileUploadHeader fuph) {
			try {
				new UploadHandler().downloadFile(fuph);
			} catch (AppException e) {
				MessageUtil.showError(e.getMessage());
			}

			if ("A".equals(fuph.getStage())) {
				uploadService.updateDownloadStatus(fuph.getId(), UploadStatus.DOWNLOADED.status());
			}
		}

	}
}