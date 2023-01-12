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

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
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
import org.zkoss.zul.Timer;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.SysParamUtil;
import com.pennant.pff.upload.model.FieUploadDTO;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;
import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.dataengine.DataEngineImport;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.util.ExcelUtil;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.file.UploadContants.Status;
import com.pennanttech.pff.file.UploadTypes;

public class FileUploadList extends Window implements Serializable {
	private static final long serialVersionUID = 6151629601007479272L;
	private static final Logger logger = LogManager.getLogger(FileUploadList.class);

	private Window window;
	private Paging paging;
	private PagedListWrapper<FileUploadHeader> listWrapper;
	private Textbox uploadFileName;
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

		window.appendChild(createToolBar());
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
		listhead.appendChild(getListHeader(Labels.getLabel("label_TotalRecords"), FLEX_MIN, ALIGN_RIGHT));
		listhead.appendChild(getListHeader(Labels.getLabel("label_ProcessedRecords"), FLEX_MIN, ALIGN_RIGHT));
		listhead.appendChild(getListHeader(Labels.getLabel("label_SuccessRecords"), FLEX_MIN, ALIGN_RIGHT));
		listhead.appendChild(getListHeader(Labels.getLabel("label_FailedRecords"), FLEX_MIN, ALIGN_RIGHT));
		listhead.appendChild(getHFlexListHeader(Labels.getLabel("label_Progress"), FLEX_MIN));
		listhead.appendChild(getHFlexListHeader(Labels.getLabel("label.RecordStatus"), FLEX_MIN));
		listhead.appendChild(getHFlexListHeader(Labels.getLabel("label_Download"), FLEX_MIN));
		listhead.appendChild(getHFlexListHeader(Labels.getLabel("label_View"), FLEX_MIN));

		box.appendChild(listhead);

		return box;
	}

	public void onClickCheckBox(ForwardEvent event) {
		logger.info(Literal.ENTERING.concat(event.getName()));

		selectedHeaders = new ArrayList<>();

		for (Listitem listitem : listbox.getItems()) {
			Checkbox cb = (Checkbox) listitem.getChildren().get(0).getChildren().get(0);
			if (cb.isChecked()) {
				selectedHeaders.add((FileUploadHeader) listitem.getAttribute("data"));
			}
		}

		checkBoxComp.setChecked(selectedHeaders.size() == listbox.getItems().size());
		logger.info(Literal.LEAVING.concat(event.getName()));
	}

	public void onClickListHeaderCheckBox() {
		for (Listitem listitem : listbox.getItems()) {
			Checkbox cb = (Checkbox) listitem.getChildren().get(0).getChildren().get(0);

			if (cb.isChecked()) {
				selectedHeaders.add((FileUploadHeader) listitem.getAttribute("data"));
			}
		}
	}

	private Groupbox createDownloadGroupBox() {
		Groupbox groupbox = getGroupBox();

		if ("M".equals(this.stage)) {
			groupbox.appendChild(getCapion("Download"));
		}

		Div div = getDiv();
		div.appendChild(createDownloadToolBar());
		groupbox.appendChild(div);
		groupbox.appendChild(createDownloadGrid());

		return groupbox;
	}

	private Groupbox createUploadGroupBox() {
		Groupbox groupbox = getGroupBox();

		groupbox.appendChild(getCapion("Upload"));

		Div div = getDiv();
		div.appendChild(createUploadToolBar());
		groupbox.appendChild(div);
		groupbox.appendChild(createUploadGrid());

		return groupbox;
	}

	private Div createToolBar() {
		Hbox hbox = getHbox();
		hbox.appendChild(getToolbar(ALIGN_START));
		hbox.appendChild(getToolbar(ALIGN_CENTER, this.type.description()));
		hbox.appendChild(getButtonInTB(ALIGN_END, "Close", "onClick", event -> onClose()));

		Div div = getDiv();
		hbox.setParent(div);

		return div;
	}

	private Hbox createUploadToolBar() {
		FileUploadHeader fuph = new FileUploadHeader();
		Hbox hbox = getHbox();
		hbox.appendChild(
				getHyperLinkInTB(ALIGN_START, "Download Template", Events.ON_CLICK, event -> downloadTemplate(fuph)));
		hbox.appendChild(getButtonInTB(ALIGN_CENTER, "IMPORT", Events.ON_CLICK, event -> onClickButtonImport()));
		hbox.appendChild(getToolbar(ALIGN_END));

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
		center.appendChild(getTollBarButton("REFRESH", Events.ON_CLICK, event -> onClickRefresh()));
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
		this.entityCode.setModuleName("Entity");
		this.entityCode.setMandatoryStyle(!"M".equals(this.stage));
		this.entityCode.setDisplayStyle(2);
		this.entityCode.setValueColumn("EntityCode");
		this.entityCode.setDescColumn("EntityDesc");
		this.entityCode.setValidateColumns(new String[] { "EntityCode" });
		this.entityCode.addForward(ExtendedCombobox.ON_FUL_FILL, this, "onChangeEntityCode", null);

		removeSpace(this.entityCode);

		Cell cell = new Cell();
		cell.appendChild(new Label(Labels.getLabel("label_EntityCode")));
		row.appendChild(cell);

		cell = new Cell();
		cell.appendChild(getOperators(Filter.OP_EQUAL));
		row.appendChild(cell);

		cell = new Cell();
		cell.appendChild(this.entityCode);

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
			onClickRefresh();

			Media media = ((UploadEvent) event).getMedia();

			this.uploadFileName.setText("");

			String name = media.getName();

			this.fileUploadHeader.setMedia(media);
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
		this.fileName.setMandatoryStyle(true);
		this.fileName.setDisplayStyle(2);
		this.fileName.setValueColumn("Id");
		this.fileName.setDescColumn("FileName");
		this.fileName.setValueType(DataType.LONG);
		this.fileName.setValidateColumns(new String[] { "Id" });

		Filter[] filters = new Filter[3];
		filters[0] = new Filter("Type", this.fileUploadHeader.getType(), Filter.OP_EQUAL);
		filters[1] = new Filter("CreatedOn", this.fromDate.getValue(), Filter.OP_GREATER_OR_EQUAL);
		filters[2] = new Filter("CreatedOn", this.toDate.getValue(), Filter.OP_LESS_OR_EQUAL);
		this.fileName.setFilters(filters);

		removeSpace(this.fileName);

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
		cell.appendChild(this.fileName);

		row.appendChild(cell);

		return row;
	}

	public void onChangeEntityCode(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		this.entityCode.clearErrorMessage();

		if (StringUtils.isBlank(this.entityCode.getValue())) {
			this.entityCode.setValue("", "");
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClickButtonImport() {
		logger.debug(Literal.ENTERING);

		this.entityCode.clearErrorMessage();

		if (StringUtils.isEmpty(this.entityCode.getValue())) {
			throw new WrongValueException(this.entityCode, "Entity Code cannot be blank");
		}

		DataEngineStatus status = new DataEngineStatus();
		status.setName(this.type.name().concat("_UPLOAD"));
		status.setFileName(this.fileUploadHeader.getFileName());

		this.fileUploadHeader.setDeStatus(status);
		this.fileUploadHeader.setEntityCode(this.entityCode.getValue());

		ProcessDTO processDTO = new ProcessDTO();

		processDTO.setHeader(this.fileUploadHeader);
		processDTO.setDataSource(this.dataSource);
		processDTO.setService(this.uploadService);
		processDTO.setUserID(this.userId);

		doWriteComponentsToBean(this.fileUploadHeader);

		new Process(processDTO).run();

		do {
			logger.info("Waiting for Data Engine Response...");
		} while (this.fileUploadHeader.getId() <= 0);

		doSearch(true);

		logger.debug(Literal.LEAVING);
	}

	private void doSearch(boolean upload) {
		if (upload) {
			this.fileName.setValue(String.valueOf(this.fileUploadHeader.getId()));
			this.fileName.setDescription(this.fileUploadHeader.getFileName());

			this.fromDate.setValue(this.fileUploadHeader.getAppDate());
			this.toDate.setValue(this.fileUploadHeader.getAppDate());

			Timer timer = new Timer();
			timer.setRepeats(true);
			timer.setDelay(2000);

			this.window.appendChild(timer);

			timer.addEventListener(Events.ON_TIMER, evnt -> {
				List<FileUploadHeader> list = new ArrayList<>();

				this.fileUploadHeader.setStage(this.stage);

				list.add(this.fileUploadHeader);

				listbox.clearSelection();

				if (!"M".equals(this.stage) && !list.isEmpty()) {
					this.checkBoxComp.setDisabled(false);
				}

				listWrapper.initList(list, listbox, paging);
			});
		} else {
			List<FileUploadHeader> list = getUploadHeaders();

			listbox.clearSelection();

			if (!"M".equals(this.stage) && !list.isEmpty()) {
				this.checkBoxComp.setDisabled(false);
			}

			list.forEach(h1 -> {
				h1.setUserDetails(this.fileUploadHeader.getUserDetails());
				h1.setStage(this.stage);
			});

			listWrapper.initList(list, listbox, paging);
		}

		selectedHeaders = new ArrayList<>();
	}

	private List<FileUploadHeader> getUploadHeaders() {
		Date dataFrom = null;
		Date dataTo = null;
		Long fileID = null;
		String eCode = null;

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

		doRemoveValidation();

		showErrorMessage(wve);

		return uploadService.getUploadHeaderById(this.workflowRoles, eCode, fileID, dataFrom, dataTo, type.name());
	}

	private void setConstraints() {
		if ("M".equals(this.stage)) {
			this.fromDate.setConstraint(new PTDateValidator("From Date", true));
			this.toDate.setConstraint(new PTDateValidator("To Date", true));
		} else {
			this.entityCode.setConstraint(new PTStringValidator(Labels.getLabel("label_EntityCode"), null, true, true));
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

		try {
			ExcelUtil.isValidFile(name, 200, "^[a-zA-Z0-9 ._]*$");
		} catch (AppException e) {
			throw new WrongValueException(this.uploadFileName, e.getMessage());
		}

		try {
			if (!this.entityCode.isReadonly()) {
				this.entityCode
						.setConstraint(new PTStringValidator(Labels.getLabel("label_EntityCode"), null, true, true));
				header.setEntityCode(this.entityCode.getValue());
			}
		} catch (WrongValueException we) {
			throw new WrongValueException(this.entityCode, we.getMessage());
		}

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
		doSearch(false);
	}

	private void onClickRefresh() {
		this.fileUploadHeader.setId(Long.MIN_VALUE);
		this.fileUploadHeader.setFileName(null);
		this.fileUploadHeader.setFile(null);
		this.fileUploadHeader.setMedia(null);
		this.fileUploadHeader.setDeStatus(new DataEngineStatus());
		this.fileUploadHeader.setExecutionID(Long.MIN_VALUE);
		this.fileUploadHeader.setTotalRecords(0);
		this.fileUploadHeader.setSuccessRecords(0);
		this.fileUploadHeader.setFailureRecords(0);
		this.fileUploadHeader.setProgress(0);
		this.fileUploadHeader.setRemarks(null);
		this.fileUploadHeader.setStage(this.stage);

		this.fromDate.setValue(null);
		this.toDate.setValue(null);
		this.fileName.setValue("", "");
		listbox.clearSelection();

		if (!"M".equals(this.stage)) {
			this.entityCode.setValue(null);
			this.checkBoxComp.setDisabled(true);
		}

		List<FileUploadHeader> list = new ArrayList<>();
		list.add(this.fileUploadHeader);
		listWrapper.initList(list, listbox, paging);
	}

	private void onClickReject() {
		uploadService.doReject(selectedHeaders);

		doSearch(false);
	}

	private void onClickApprove() {
		for (FileUploadHeader header : selectedHeaders) {
			header.setLastMntBy(this.userId);
			header.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		}
		uploadService.doApprove(selectedHeaders);

		doSearch(false);
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

	private Caption getCapion(String label) {
		Caption caption = new Caption();
		caption.setLabel(label);

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

	private Toolbar getToolbar(String align, String label) {
		Toolbar toolBar = getToolbar(align);
		toolBar.appendChild(new Label(label));
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

	private void removeSpace(ExtendedCombobox extendedCombobox) {
		extendedCombobox.removeChild(extendedCombobox.getFirstChild());
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
			return listBoxHeight - 145 + "px";
		}

		return listBoxHeight + 5 + "px";
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
		buttons.add(Labels.getLabel("label_View"));

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

			if (!"M".equals(uph.getStage())) {
				Listcell lc = new Listcell();
				lc.appendChild(appendSelectBox());
				lc.setParent(item);
			}

			Listcell lc = new Listcell(String.valueOf(id));
			lc.setParent(item);

			lc = new Listcell(uph.getFileName());
			lc.setParent(item);

			lc = new Listcell(String.valueOf(uph.getTotalRecords()));
			lc.setParent(item);

			lc = new Listcell(String.valueOf(uph.getSuccessRecords() + uph.getFailureRecords()));
			lc.setParent(item);

			lc = new Listcell(String.valueOf(uph.getSuccessRecords()));
			lc.setParent(item);

			lc = new Listcell(String.valueOf(uph.getFailureRecords()));
			lc.setParent(item);

			switch (Status.value(uph.getProgress())) {
			case IMPORTED:
				lc = new Listcell("Import");
				break;
			case IMPORT_IN_PROCESS:
				lc = new Listcell("In Progress..");
				break;
			case IMPORT_FAILED:
				lc = new Listcell("Failed");
				break;
			default:
				lc = new Listcell("");
				break;
			}

			lc.setParent(item);

			lc = new Listcell(uph.getRecordStatus());
			lc.setParent(item);

			if (uph.getSuccessRecords() > 0) {
				Button dowButton = getButton(Labels.getLabel("label_Download"), String.valueOf(id));
				dowButton.addEventListener(Events.ON_CLICK, event -> onClickDownload(uph));

				lc = new Listcell();
				lc.appendChild(dowButton);
				lc.setParent(item);
			} else {
				Button viewButton = getButton(Labels.getLabel("label_View"), String.valueOf(id));
				viewButton.addEventListener(Events.ON_CLICK, event -> onClickView(uph));

				lc = new Listcell();
				lc.appendChild(viewButton);
				lc.setParent(item);
			}

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
			String name = fuph.getType().concat("_DOWNLOAD");
			fileDownload(name, fuph);
		}

		private void onClickView(FileUploadHeader uph) {
			Map<String, Object> map = new HashMap<>();

			map.put("List", uph.getDataEngineLog());
			map.put("preview", false);
			map.put("BatchId", uph.getExecutionID());
			try {
				Executions.createComponents("~./data-engine/pages/ExceptionLog.zul", null, map);
			} catch (final Exception e) {
				e.getMessage();
			}
		}
	}

	private class ProcessDTO implements Serializable {
		private static final long serialVersionUID = -341504661579686922L;

		private FileUploadHeader header;
		private transient UploadService service;
		private transient DataSource dataSource;
		private long userID;

		public ProcessDTO() {
			super();
		}

		public FileUploadHeader getHeader() {
			return header;
		}

		public void setHeader(FileUploadHeader header) {
			this.header = header;
		}

		public UploadService getService() {
			return service;
		}

		public void setService(UploadService service) {
			this.service = service;
		}

		public DataSource getDataSource() {
			return dataSource;
		}

		public void setDataSource(DataSource dataSource) {
			this.dataSource = dataSource;
		}

		public long getUserID() {
			return userID;
		}

		public void setUserID(long userID) {
			this.userID = userID;
		}
	}

	private class Process implements Runnable {
		private ProcessDTO processDTO;

		public Process(ProcessDTO processDTO) {
			this.processDTO = processDTO;
		}

		@Override
		public void run() {
			FileUploadHeader uploadHeader = this.processDTO.getHeader();

			Date appDate = uploadHeader.getAppDate();

			uploadHeader.setId(this.processDTO.getService().saveHeader(uploadHeader, TableType.MAIN_TAB));
			uploadHeader.setAppDate(appDate == null ? SysParamUtil.getAppDate() : appDate);

			DataEngineStatus status = uploadHeader.getDeStatus();

			DataEngineImport engine = new DataEngineImport(this.processDTO.getDataSource(), this.processDTO.getUserID(),
					App.DATABASE.name(), true, appDate);

			engine.setExecutionStatus(status);
			engine.setFile(uploadHeader.getFile());
			engine.setMedia(uploadHeader.getMedia());
			engine.setValueDate(appDate);

			Map<String, Object> parameterMap = new HashMap<>();
			parameterMap.put("HEADER_ID", uploadHeader.getId());

			engine.setParameterMap(parameterMap);

			try {
				engine.importData(status.getName());
			} catch (Exception e) {
				status.setRemarks(e.getMessage());
				update(uploadHeader, status);
				throw new AppException(e.getMessage());
			}

			String deStatus = status.getStatus();

			do {
				update(uploadHeader, status);
				if ("S".equals(deStatus) || "F".equals(deStatus)) {
					break;
				}
			} while ("S".equals(deStatus) || "F".equals(deStatus));
		}

		private void update(FileUploadHeader uploadHeader, DataEngineStatus status) {
			int totalRecords = (int) status.getTotalRecords();
			int successRecords = (int) status.getSuccessRecords();
			int failedRecords = (int) status.getFailedRecords();

			uploadHeader.setTotalRecords(totalRecords);
			uploadHeader.setSuccessRecords(successRecords);
			uploadHeader.setFailureRecords(failedRecords);

			if ("S".equals(status.getStatus())) {
				uploadHeader.setProgress(Status.IMPORTED.getValue());
			} else {
				uploadHeader.setProgress(Status.IMPORT_FAILED.getValue());
			}

			uploadHeader.setRemarks(status.getRemarks());
			uploadHeader.setExecutionID(status.getId());
			uploadHeader.setDataEngineLog(status.getDataEngineLogList());

			this.processDTO.getService().update(uploadHeader);
		}
	}
}