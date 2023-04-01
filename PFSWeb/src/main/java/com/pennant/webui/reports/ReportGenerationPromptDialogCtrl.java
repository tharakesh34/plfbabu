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
 * * FileName : ReportGenerationPromptDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 23-09-2012
 * * * Modified Date : 23-09-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 23-09-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.reports;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Bandpopup;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timebox;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.InputElement;
import org.zkoss.zul.impl.LabelImageElement;
import org.zkoss.zul.impl.NumberInputElement;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.ReportsUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.collateral.CollateralAssignmentDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceScheduleReportData;
import com.pennant.backend.model.finance.LinkedFinances;
import com.pennant.backend.model.reports.ReportConfiguration;
import com.pennant.backend.model.reports.ReportFilterFields;
import com.pennant.backend.model.reports.ReportSearchTemplate;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.LinkedFinancesService;
import com.pennant.backend.service.reports.ReportConfigurationService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.finance.financemain.model.FinScheduleListItemRenderer;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.ExtendedMultipleSearchListBox;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.SearchResult;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.TableType;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * This is the controller class for the /WEB-INF/pages/reports/ReportGenerationPromptDialog.zul file.
 */
public class ReportGenerationPromptDialogCtrl extends GFCBaseCtrl<ReportConfiguration> {
	private static final long serialVersionUID = 4678287540046204660L;
	private final static Logger logger = LogManager.getLogger(ReportGenerationPromptDialogCtrl.class);

	protected Window window_ReportPromptFilterCtrl;
	protected Borderlayout borderlayout;
	protected Combobox cbSelectTemplate;
	protected Button btnSaveTemplate;
	protected Button btnDeleteTemplate;
	protected Tabbox tabbox;
	protected Tab selectTab;
	protected Grid dymanicFieldsGrid;
	protected Rows dymanicFieldsRows;
	protected Radio pdfFormat;
	protected Radio excelFormat;
	protected Row rows_formatType;
	private String financeReference = "";
	boolean isCustomer360 = false;

	protected String reportMenuCode;
	private ReportConfiguration reportConfiguration;
	private StringBuilder saticValuesWhereCondition = new StringBuilder();
	private String parentFlag;
	// NEEDED for the ReUse in the SearchWindow
	protected Map<String, Object> lovSearchBufferMap = new HashMap<String, Object>(1);// It is For LovSearch selected
																						// Values
	protected Map<String, List<ValueLabel>> listSelectionMaps = new HashMap<String, List<ValueLabel>>(1);
	protected Map<String, ReportFilterFields> rangeFieldsMap = new HashMap<String, ReportFilterFields>(1);// It is For
																											// Range
																											// Fields
																											// storing
	private Map<Object, List<ReportSearchTemplate>> templateLibraryMap;// templates Library
	static final Map<String, String> filterDescMap = PennantStaticListUtil.getFilterDescription();

	private List<ReportSearchTemplate> reportSearchTemplateFieldsList;
	private ReportConfigurationService reportConfigurationService;
	private FinanceDetailService financeDetailService;
	private LinkedFinancesService linkedFinancesService;
	private FinScheduleListItemRenderer renderer;

	private StringBuilder searchCriteriaDesc = new StringBuilder(" ");

	public enum FIELDCLASSTYPE {
		Textbox, Combobox, Datebox, Timebox, Intbox, Decimalbox, Bandbox, Checkbox, Radio
	}

	public enum FIELDTYPE {
		TXT, DATE, TIME, DATETIME, STATICLIST, DYNAMICLIST, LOVSEARCH, DECIMAL, INTRANGE, DECIMALRANGE, NUMBER,
		CHECKBOX, MULTISELANDLIST, MULTISELINLIST, DATERANGE, DATETIMERANGE, TIMERANGE, STATICVALUE
	}

	private boolean isExcel = false;
	private boolean searchClick = false;
	private static final String EXCEL_TYPE = "Excel:";
	private String unitName;
	Window dialogWindow = null;
	private Map<String, Object> valueMap = new HashMap<String, Object>();
	private Map<String, Object> valueLabelMap = new HashMap<String, Object>();
	private Map<String, Object> renderMap = new HashMap<String, Object>();
	private List<String> myLableList = new ArrayList<String>();
	private Map<Long, List<String>> myOrderedLableMap = new HashMap<>();

	private boolean isEntity = false;
	private String entityValue = "";
	private CollateralAssignmentDAO collateralAssignmentDAO;

	public ReportGenerationPromptDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	/**
	 * On creating Window
	 * 
	 * @param event
	 */
	public void onCreate$window_ReportPromptFilterCtrl(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ReportPromptFilterCtrl);

		try {
			if (arguments.containsKey("MonthEndReportEvent")) {
				event = (Event) arguments.get("MonthEndReportEvent");
				tabbox = (Tabbox) event.getTarget().getParent().getParent().getParent();
			} else if (!arguments.containsKey("ReportConfiguration")) {
				tabbox = (Tabbox) event.getTarget().getParent().getParent().getParent().getParent();
			}
			if (arguments.containsKey("dialogWindow")) {
				dialogWindow = (Window) arguments.get("dialogWindow");

			}

			if (arguments.containsKey("entity")) {
				if ("Y".equalsIgnoreCase(getArgument("entity"))) {
					isEntity = true;
				}
			}

			if (arguments.containsKey("customer360")) {
				isCustomer360 = (boolean) arguments.get("customer360");
				financeReference = (String) arguments.get("financeReference");
			}

			if (arguments.containsKey("ReportConfiguration")) {
				reportConfiguration = (ReportConfiguration) arguments.get("ReportConfiguration");
				reportMenuCode = reportConfiguration.getMenuItemCode();
			} else {
				// get the parameters map that are overHanded by creation.
				reportMenuCode = tabbox.getSelectedTab().getId().trim().replace("tab_", "menu_Item_");
				selectTab = tabbox.getSelectedTab();
				reportConfiguration = getReportConfiguration(reportMenuCode);
			}

			if (reportConfiguration == null || (reportConfiguration.isPromptRequired()
					&& reportConfiguration.getListReportFieldsDetails().size() == 0)) {
				MessageUtil.showError(Labels.getLabel("label_ReportNotConfigured.error"));

				closeDialog();
			} else {

				if (reportConfiguration.getReportName().startsWith(EXCEL_TYPE)) {
					isExcel = true;
				} else {
					if (reportConfiguration.isAlwMultiFormat()) {
						this.rows_formatType.setVisible(true);
					}
					isExcel = false;
				}
				// if prompt Required Render components else direct report
				if (reportConfiguration.isPromptRequired()) {
					doRenderComponents();
					doFillcbSelectTemplate();// Fill Template Library
					this.window_ReportPromptFilterCtrl.doModal();
				} else {
					doShowReport(null, null, null, null, null);
				}
				logger.debug("Leaving" + event.toString());

			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showError(Labels.getLabel("label_ReportConfiguredError.error"));
			closeDialog();
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * This method retries the Report Detail Configuration and Filter Components
	 * 
	 * @param reportMenuCode
	 * @return aReportConfiguration(ReportConfiguration)
	 */
	private ReportConfiguration getReportConfiguration(String reportMenuCode) {
		ReportConfiguration aReportConfiguration = null;
		logger.debug("Entering");
		JdbcSearchObject<ReportConfiguration> searchObj = null;
		List<ReportConfiguration> listReportConfiguration = null;
		try {
			// ++ create the searchObject and initialize sorting ++//
			searchObj = new JdbcSearchObject<ReportConfiguration>(ReportConfiguration.class);
			searchObj.addTabelName("REPORTCONFIGURATION");
			searchObj.addFilter(new Filter("MENUITEMCODE", reportMenuCode, Filter.OP_EQUAL));

			listReportConfiguration = getPagedListWrapper().getPagedListService().getBySearchObject(searchObj);

			if (!listReportConfiguration.isEmpty()) {
				aReportConfiguration = listReportConfiguration.get(0);
				if (aReportConfiguration != null) {
					this.window_ReportPromptFilterCtrl.setTitle(aReportConfiguration.getReportHeading());
					JdbcSearchObject<ReportFilterFields> filtersSearchObj = new JdbcSearchObject<ReportFilterFields>(
							ReportFilterFields.class);
					filtersSearchObj.addTabelName("REPORTFILTERFIELDS");
					filtersSearchObj
							.addFilter(new Filter("reportID", aReportConfiguration.getReportID(), Filter.OP_EQUAL));
					filtersSearchObj.addSort("SEQORDER", false);
					List<ReportFilterFields> listReportFilterFields = getPagedListWrapper().getPagedListService()
							.getBySearchObject(filtersSearchObj);
					aReportConfiguration.setListReportFieldsDetails(listReportFilterFields);
				}
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {
			searchObj = null;
			listReportConfiguration = null;
		}
		logger.debug("Leaving");
		return aReportConfiguration;
	}

	/**
	 * This Method Renders Components by Type
	 * 
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private void doRenderComponents()
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		logger.debug("Entering");
		int j = 0;

		for (ReportFilterFields filter : reportConfiguration.getListReportFieldsDetails()) {
			FIELDTYPE fieldValueType = FIELDTYPE.valueOf(filter.getFieldType());

			switch (fieldValueType) {
			case TXT:
				renderSimpleInputElement(filter, FIELDTYPE.TXT.toString());
				break;
			case NUMBER:
				renderSimpleInputElement(filter, FIELDTYPE.NUMBER.toString());
				break;
			case DECIMAL:
				renderSimpleInputElement(filter, FIELDTYPE.DECIMAL.toString());
				break;
			case INTRANGE:
				renderNumberRangeBox(filter);
				break;
			case DECIMALRANGE:
				renderNumberRangeBox(filter);
				break;

			case DATE:
				renderDateBox(filter);
				break;
			case DATETIME:
				renderDateBox(filter);
				break;
			case TIME:
				renderDateBox(filter);
				break;
			case DATERANGE:
				renderDateRangeBox(filter);
				break;
			case DATETIMERANGE:
				renderDateRangeBox(filter);
				break;
			case TIMERANGE:
				renderDateRangeBox(filter);
				break;

			case STATICLIST:
				renderComboBox(filter, true);
				break;

			case DYNAMICLIST:
				renderComboBox(filter, false);
				break;

			case LOVSEARCH:
				renderLovSearchField(filter, j++);
				break;

			case MULTISELANDLIST:
				renderMultiSelctionList(filter);
				break;

			case MULTISELINLIST:
				renderMultiSelctionList(filter);
				break;
			case CHECKBOX:
				renderSimpleInputElement(filter, FIELDTYPE.CHECKBOX.toString());
				break;
			case STATICVALUE:
				saticValuesWhereCondition = addAndCondition(saticValuesWhereCondition);
				saticValuesWhereCondition.append(filter.getStaticValue());
				break;

			}
		}

		if (myLableList.size() > 0 && !myLableList.isEmpty()) {
			myOrderedLableMap.put((long) j, myLableList);
			myLableList = new ArrayList<String>();
		}
		/* Calculate and compare height of all rows and set height of window against components height */
		int dialogHeight = dymanicFieldsGrid.getRows().getVisibleItemCount() * 25 + 150;
		if (borderLayoutHeight > dialogHeight) {
			this.window_ReportPromptFilterCtrl.setHeight(dialogHeight + "px");
		} else {
			this.window_ReportPromptFilterCtrl.setHeight(getBorderLayoutHeight());
		}

		/* Hide Template Library */
		if (!reportConfiguration.isShowTempLibrary()) {
			borderlayout.getNorth().setVisible(false);
		}
		logger.debug("Leaving");

	}

	// COMPONENT RENDERERS

	/**
	 * Render simple Elements like Text box Date box
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void renderSimpleInputElement(ReportFilterFields aReportFieldsDetails, String componentType) {
		logger.debug("Entering");
		// TextBox
		Row textBoxRow = new Row();
		textBoxRow.appendChild(new Label(aReportFieldsDetails.getFieldLabel()));

		Listbox sortOperator = new Listbox();
		sortOperator.setId("sortOperator_" + aReportFieldsDetails.getFieldID());
		sortOperator.setItemRenderer(new SearchOperatorListModelItemRenderer());
		sortOperator.setWidth("43px");
		sortOperator.setMold("select");
		sortOperator.setVisible(aReportFieldsDetails.isFilterRequired());
		textBoxRow.appendChild(sortOperator);

		Space space = new Space();
		setSpaceStyle(space, aReportFieldsDetails.isMandatory());
		Component simpleComponent = null;

		if (componentType.equals(FIELDTYPE.TXT.toString())) {
			sortOperator.setModel(new ListModelList(new SearchOperators().getStringOperators()));
			simpleComponent = new Textbox();
			((Textbox) simpleComponent).setWidth(aReportFieldsDetails.getFieldWidth() + "px");
			((Textbox) simpleComponent).setMaxlength(aReportFieldsDetails.getFieldLength());
		} else if (componentType.equals(FIELDTYPE.NUMBER.toString())) {
			sortOperator.setModel(new ListModelList(new SearchOperators().getNumericOperators()));
			simpleComponent = new Intbox();
			((Intbox) simpleComponent).setMaxlength(aReportFieldsDetails.getFieldLength());
		} else if (componentType.equals(FIELDTYPE.DECIMAL.toString())) {
			sortOperator.setModel(new ListModelList(new SearchOperators().getNumericOperators()));
			simpleComponent = new Decimalbox();
			((Decimalbox) simpleComponent).setMaxlength(aReportFieldsDetails.getFieldLength());
		} else if (componentType.equals(FIELDTYPE.CHECKBOX.toString())) {
			sortOperator.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
			simpleComponent = new Checkbox();
		}

		simpleComponent.setId(Long.toString(aReportFieldsDetails.getFieldID()));
		Hbox hbox = new Hbox();
		hbox.appendChild(space);
		hbox.appendChild(simpleComponent);
		textBoxRow.appendChild(hbox);
		dymanicFieldsRows.appendChild(textBoxRow);
		logger.debug("Leaving");
	}

	/**
	 * Render LovSearch Field
	 * 
	 * @param j
	 */
	private void renderLovSearchField(ReportFilterFields aReportFieldsDetails, int j) {
		logger.debug("Entering");

		if (aReportFieldsDetails.getFilterFileds() == null) {
			if (j > 0) {
				myOrderedLableMap.put(aReportFieldsDetails.getFieldID() - 1, myLableList);
			}
			myLableList = new ArrayList<String>();
		}
		myLableList.add(aReportFieldsDetails.getFilterFileds());
		Textbox textbox = new Textbox();
		Textbox textboxhidden = new Textbox();
		Hbox hbox;
		// LOV
		Row lovSearchFieldRow = new Row();
		lovSearchFieldRow.appendChild(new Label(aReportFieldsDetails.getFieldLabel()));
		Space space = new Space();
		lovSearchFieldRow.appendChild(space);

		space = new Space();
		setSpaceStyle(space, aReportFieldsDetails.isMandatory());
		hbox = new Hbox();

		textboxhidden.setId(Long.toString(aReportFieldsDetails.getFieldID()));
		textboxhidden.setVisible(false);

		textbox.setId("txtLovFiled" + aReportFieldsDetails.getFieldID());
		textbox.setReadonly(true);
		textbox.setWidth(aReportFieldsDetails.getFieldWidth() + "px");

		hbox.appendChild(space);
		hbox.appendChild(textboxhidden);
		hbox.appendChild(textbox);

		Button btn = new Button();
		btn.setImage("/images/icons/LOVSearch.png");
		btn.setId(aReportFieldsDetails.getModuleName());
		CustomArgument aCustomArgument = new CustomArgument(hbox, aReportFieldsDetails);
		btn.addForward("onClick", window_ReportPromptFilterCtrl, "onLovButtonClicked", aCustomArgument);
		hbox.appendChild(btn);
		lovSearchFieldRow.appendChild(hbox);
		dymanicFieldsRows.appendChild(lovSearchFieldRow);

		if (StringUtils.equals(reportMenuCode, "menu_Item_GST_InvoiceReport")
				|| StringUtils.equals(reportMenuCode, "menu_Item_ProcFees_InvoiceReport")) {
			lovSearchFieldRow.setId("row_GSTInv_" + aReportFieldsDetails.getFieldID());
			if (aReportFieldsDetails.getFieldID() == 3 || aReportFieldsDetails.getFieldID() == 4) {
				lovSearchFieldRow.setVisible(false);
			}
		}

		if (isCustomer360 && aReportFieldsDetails.getFieldName().equals("FinReference")
				&& StringUtils.isNotEmpty(financeReference)) {
			textbox.setValue(String.valueOf(financeReference));
			textboxhidden.setValue(String.valueOf(financeReference));
			// textboxhidden.setValue(String.valueOf(custId));
		}
		logger.debug("Leaving");
	}

	/**
	 * Render DateBox with filter
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void renderDateBox(ReportFilterFields aReportFieldsDetails) {
		logger.debug("Entering");
		// TextBox
		Row dateboxRow = new Row();
		dateboxRow.appendChild(new Label(aReportFieldsDetails.getFieldLabel()));
		Listbox sortOperator = new Listbox();
		sortOperator.setId("sortOperator_" + aReportFieldsDetails.getFieldID());
		sortOperator.setItemRenderer(new SearchOperatorListModelItemRenderer());
		sortOperator.setWidth("43px");
		sortOperator.setMold("select");
		sortOperator.setVisible(aReportFieldsDetails.isFilterRequired());
		sortOperator.setModel(new ListModelList(new SearchOperators().getNumericOperators()));
		dateboxRow.appendChild(sortOperator);
		Hbox hbox = new Hbox();
		Space space = new Space();
		setSpaceStyle(space, aReportFieldsDetails.isMandatory());
		hbox.appendChild(space);

		if (aReportFieldsDetails.getFieldType().equals(FIELDTYPE.TIME.toString())) {
			Timebox timeBox = new Timebox();
			timeBox.setFormat(PennantConstants.timeFormat);
			timeBox.setId(Long.toString(aReportFieldsDetails.getFieldID()));
			timeBox.setWidth("120px");
			hbox.appendChild(timeBox);
		} else {
			Datebox datebox = new Datebox();
			datebox.setId(Long.toString(aReportFieldsDetails.getFieldID()));
			if (aReportFieldsDetails.getFieldType().equals(FIELDTYPE.DATETIME.toString())) {
				datebox.setFormat(PennantConstants.dateTimeAMPMFormat);
				datebox.setWidth("200px");
			} else if (aReportFieldsDetails.getFieldType().equals(FIELDTYPE.DATE.toString())) {
				datebox.setFormat(DateFormat.SHORT_DATE.getPattern());

			}
			hbox.appendChild(datebox);
		}

		dateboxRow.appendChild(hbox);
		dymanicFieldsRows.appendChild(dateboxRow);
		logger.debug("Leaving");
	}

	/**
	 * Render DateType Range Box
	 */
	private void renderDateRangeBox(ReportFilterFields aReportFieldsDetails) {
		logger.debug("Entering");
		// TextBox
		Row dateboxRow = new Row();
		dateboxRow.appendChild(new Label(aReportFieldsDetails.getFieldLabel()));
		Space space = new Space();
		dateboxRow.appendChild(space);
		Hbox hbox = new Hbox();
		space = new Space();
		setSpaceStyle(space, aReportFieldsDetails.isMandatory());
		hbox.appendChild(space);
		// Time Range
		if (aReportFieldsDetails.getFieldType().equals(FIELDTYPE.TIMERANGE.toString())) {
			Timebox timeBox = new Timebox();
			timeBox.setFormat(PennantConstants.timeFormat);
			timeBox.setId("From_" + aReportFieldsDetails.getFieldID());
			timeBox.setWidth("120px");
			hbox.appendChild(timeBox);

			Label label = new Label(" To ");
			label.setStyle("margin:0px 10px;");
			hbox.appendChild(label);
			timeBox = new Timebox();
			timeBox.setFormat(PennantConstants.timeFormat);
			timeBox.setId("To_" + aReportFieldsDetails.getFieldID());
			timeBox.setWidth("120px");
			hbox.appendChild(timeBox);

		} else {
			// Date Range
			Datebox datebox = new Datebox();
			datebox.setId("From_" + aReportFieldsDetails.getFieldID());
			if (aReportFieldsDetails.getFieldType().equals(FIELDTYPE.DATETIMERANGE.toString())) {
				datebox.setFormat(PennantConstants.dateTimeAMPMFormat);
				datebox.setWidth("180px");
				hbox.appendChild(datebox);
			} else if (aReportFieldsDetails.getFieldType().equals(FIELDTYPE.DATERANGE.toString())) {

				datebox.setFormat(DateFormat.SHORT_DATE.getPattern());
				hbox.appendChild(datebox);
			}
			Label label = new Label(" To ");
			label.setStyle("margin:2px 10px;");
			hbox.appendChild(label);
			datebox = new Datebox();
			datebox.setId("To_" + aReportFieldsDetails.getFieldID());
			// Date Time Range
			if (aReportFieldsDetails.getFieldType().equals(FIELDTYPE.DATETIMERANGE.toString())) {
				datebox.setFormat(PennantConstants.dateTimeAMPMFormat);
				datebox.setWidth("180px");
			} else if (aReportFieldsDetails.getFieldType().equals(FIELDTYPE.DATERANGE.toString())) {
				datebox.setFormat(DateFormat.SHORT_DATE.getPattern());

			}
			hbox.appendChild(datebox);
		}

		rangeFieldsMap.put(String.valueOf(aReportFieldsDetails.getFieldID()), aReportFieldsDetails);
		dateboxRow.appendChild(hbox);
		dymanicFieldsRows.appendChild(dateboxRow);
		logger.debug("Leaving");
	}

	/**
	 * Render DateType Range Box
	 */
	private void renderNumberRangeBox(ReportFilterFields aReportFieldsDetails) {
		logger.debug("Entering");
		// TextBox
		Row numberRangeBoxRow = new Row();
		numberRangeBoxRow.appendChild(new Label(aReportFieldsDetails.getFieldLabel()));
		Space space = new Space();
		numberRangeBoxRow.appendChild(space);
		Hbox hbox = new Hbox();
		space = new Space();
		setSpaceStyle(space, aReportFieldsDetails.isMandatory());
		hbox.appendChild(space);
		NumberInputElement numberBox = null;
		// Time Range
		if (aReportFieldsDetails.getFieldType().equals(FIELDTYPE.INTRANGE.toString())) {
			numberBox = new Intbox();
		} else {
			numberBox = new Decimalbox();
		}
		/* numberBox.setWidth(aReportFieldsDetails.getFieldWidth()+"px"); */
		numberBox.setId("From_" + aReportFieldsDetails.getFieldID());
		hbox.appendChild(numberBox);
		Label label = new Label(" To ");
		label.setStyle("margin:2px 10px;");
		hbox.appendChild(label);
		if (aReportFieldsDetails.getFieldType().equals(FIELDTYPE.INTRANGE.toString())) {
			numberBox = new Intbox();
		} else {
			numberBox = new Decimalbox();
		}
		numberBox.setId("To_" + aReportFieldsDetails.getFieldID());
		hbox.appendChild(numberBox);
		rangeFieldsMap.put(String.valueOf(aReportFieldsDetails.getFieldID()), aReportFieldsDetails);
		numberRangeBoxRow.appendChild(hbox);
		dymanicFieldsRows.appendChild(numberRangeBoxRow);
		logger.debug("Leaving");
	}

	/**
	 * Render ComboBox
	 */
	@SuppressWarnings("unchecked")
	private void renderComboBox(ReportFilterFields aReportFieldsDetails, boolean isStatic) {
		logger.debug("Entering");

		Row staticListRow = new Row();
		staticListRow.appendChild(new Label(aReportFieldsDetails.getFieldLabel()));
		Space space = new Space();
		staticListRow.appendChild(space);

		space = new Space();
		setSpaceStyle(space, aReportFieldsDetails.isMandatory());
		Combobox comboBox = new Combobox();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue(PennantConstants.List_Select);
		comboBox.setReadonly(true);
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		comboBox.appendChild(comboitem);
		comboBox.setId(Long.toString(aReportFieldsDetails.getFieldID()));
		comboBox.setWidth(aReportFieldsDetails.getFieldWidth() + "px");

		try {
			// If Static List get From PennantAppUtil Methods else from Search Object by module name and where condition
			if (isStatic) {
				List<ValueLabel> staticValuesList = (List<ValueLabel>) Class
						.forName("com.pennant.backend.util.PennantStaticListUtil")
						.getMethod(aReportFieldsDetails.getAppUtilMethodName())
						.invoke(Class.forName("com.pennant.backend.util.PennantStaticListUtil"));
				listSelectionMaps.put(comboBox.getId(), staticValuesList);

				for (int i = 0; i < staticValuesList.size(); i++) {
					if (!staticValuesList.get(i).getValue().equals(Labels.getLabel("value_Select"))
							&& (StringUtils.isNotEmpty(staticValuesList.get(i).getValue()))) {
						comboitem = new Comboitem();
						// comboitem.setValue(staticValuesList.get(i).getId());
						comboitem.setLabel(staticValuesList.get(i).getLabel());
						comboitem.setValue(staticValuesList.get(i).getValue());
						comboBox.appendChild(comboitem);
						comboBox.addForward("onSelect", window_ReportPromptFilterCtrl, "onComboFieldSelected",
								comboBox);
					}
				}
				comboBox.setSelectedIndex(0);
			} else {
				if (aReportFieldsDetails.getModuleName().equals("LoanProvisions")) {
					List<ValueLabel> staticValuesList = PennantAppUtil.getNpaProvisionDates();
					listSelectionMaps.put(comboBox.getId(), staticValuesList);

					for (int i = 0; i < staticValuesList.size(); i++) {
						if (!staticValuesList.get(i).getObject().equals(Labels.getLabel("value_Select"))
								&& (staticValuesList.get(i).getObject() != null)) {
							comboitem = new Comboitem();
							comboitem.setLabel(staticValuesList.get(i).getLabel());
							comboitem.setValue(staticValuesList.get(i).getObject());
							comboBox.appendChild(comboitem);
							comboBox.addForward("onSelect", window_ReportPromptFilterCtrl, "onComboFieldSelected",
									comboBox);
						}
					}
					comboBox.setSelectedIndex(0);
				} else {
					List<ValueLabel> staticValuesList = new ArrayList<ValueLabel>();
					JdbcSearchObject<Object> dynsearchObject = new JdbcSearchObject<Object>(
							(Class<Object>) ModuleUtil.getModuleClass(aReportFieldsDetails.getModuleName()));
					// Add where condition for LOV Search Filter
					if (aReportFieldsDetails.getWhereCondition() != null
							&& !("").equals(aReportFieldsDetails.getWhereCondition().trim())) {
						dynsearchObject.addWhereClause(aReportFieldsDetails.getWhereCondition());
					}
					List<Object> dynamicListResult = getPagedListWrapper().getPagedListService()
							.getBySearchObject(dynsearchObject);
					ValueLabel valueLabel = null;
					for (int i = 0; i < dynamicListResult.size(); i++) {
						valueLabel = new ValueLabel();
						comboitem = new Comboitem();

						Object object = dynamicListResult.get(i).getClass()
								.getMethod(aReportFieldsDetails.getLovHiddenFieldMethod())
								.invoke(dynamicListResult.get(i));

						comboitem.setValue(object.toString());
						valueLabel.setValue(object.toString());

						object = (String) dynamicListResult.get(i).getClass()
								.getMethod(aReportFieldsDetails.getLovTextFieldMethod())
								.invoke(dynamicListResult.get(i));

						comboitem.setLabel(object.toString());
						valueLabel.setLabel(object.toString());
						staticValuesList.add(valueLabel);
						comboBox.appendChild(comboitem);

					}
					comboBox.setSelectedIndex(0);
					listSelectionMaps.put(comboBox.getId(), staticValuesList);
				}

			}

		} catch (Exception e) {
			logger.warn("Error While rendering combobox Filed Name : " + aReportFieldsDetails.getFieldLabel());
			logger.error("Exception: ", e);
		}
		Hbox hbox = new Hbox();
		hbox.appendChild(space);
		hbox.appendChild(comboBox);
		staticListRow.appendChild(hbox);
		dymanicFieldsRows.appendChild(staticListRow);

		if (StringUtils.equals(reportMenuCode, "menu_Item_GST_InvoiceReport")
				|| StringUtils.equals(reportMenuCode, "menu_Item_ProcFees_InvoiceReport")) {
			comboBox.setSelectedIndex(1);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method fills Band box with multiple selected list box for multiple selection
	 * 
	 * @param listBox
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public void renderMultiSelctionList(ReportFilterFields aReportFieldsDetails)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		logger.debug("Entering");

		List<ValueLabel> multiSelectionListValues = (List<ValueLabel>) Class
				.forName("com.pennant.backend.util.PennantStaticListUtil")
				.getMethod(aReportFieldsDetails.getAppUtilMethodName())
				.invoke(Class.forName("com.pennant.backend.util.PennantStaticListUtil"));

		Row textBoxRow = new Row();
		textBoxRow.appendChild(new Label(aReportFieldsDetails.getFieldLabel()));
		Space space = new Space();
		textBoxRow.appendChild(space);

		space = new Space();
		setSpaceStyle(space, aReportFieldsDetails.isMandatory());
		Bandbox bandBox = new Bandbox();
		bandBox.setId(Long.toString(aReportFieldsDetails.getFieldID()));
		bandBox.setReadonly(true);
		bandBox.setWidth(aReportFieldsDetails.getFieldWidth() + "px");
		bandBox.setTabindex(-1);

		Bandpopup bandpopup = new Bandpopup();
		Listbox listBox = new Listbox();
		listBox.setMultiple(true);
		listBox.setDisabled(true);
		listBox.setWidth("350px");
		bandpopup.appendChild(listBox);
		bandBox.appendChild(bandpopup);

		for (int i = 0; i < multiSelectionListValues.size(); i++) {
			Listitem listItem = new Listitem();
			if (!multiSelectionListValues.get(i).getValue().equals(Labels.getLabel("value_Select"))
					&& (StringUtils.isNotEmpty(multiSelectionListValues.get(i).getValue()))) {

				Listcell listCell = new Listcell();
				Checkbox checkBox = new Checkbox();
				checkBox.addEventListener("onCheck", new onMultiSelectionItemSelected());
				checkBox.setValue(multiSelectionListValues.get(i).getValue());

				Label label = new Label(multiSelectionListValues.get(i).getLabel());
				label.setStyle("padding-left:5px");
				listCell.setValue(multiSelectionListValues.get(i).getValue());
				listCell.appendChild(checkBox);
				listCell.appendChild(label);
				listItem.appendChild(listCell);
				listBox.appendChild(listItem);
			}
		}

		Hbox hbox = new Hbox();
		hbox.appendChild(space);
		hbox.appendChild(bandBox);
		textBoxRow.appendChild(hbox);
		dymanicFieldsRows.appendChild(textBoxRow);
		logger.debug("Leaving");
	}

	// Preparing Where Condition
	/**
	 * This method prepares a where condition from the All fields or Add Filter and search data All fields in list for
	 * saving filter Template by using flag 'isWhereCondition'
	 * 
	 * @return if isWhereCondition=true ?whereCondition(String):reportSearchTemplateList(List<ReportSearchTemplate>)
	 */

	public Object doPrepareWhereConditionOrTemplate(boolean isWhereCondition, boolean excludeDates) {
		logger.debug("Entering");

		String filter = "=";
		StringBuilder whereCondition = null;
		if (reportConfiguration.isWhereCondition()) {
			whereCondition = new StringBuilder("where ");
		} else {
			whereCondition = new StringBuilder("");
		}
		searchCriteriaDesc = new StringBuilder();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		List<ReportSearchTemplate> reportSearchTemplateList = null;
		if (isWhereCondition) {
			doSetValidation();
		} else {
			reportSearchTemplateList = new ArrayList<ReportSearchTemplate>();
		}

		long reqRowId = 0;
		if (StringUtils.equals(reportMenuCode, "menu_Item_GST_InvoiceReport")
				|| StringUtils.equals(reportMenuCode, "menu_Item_ProcFees_InvoiceReport")) {
			Combobox invoiceFor = (Combobox) dymanicFieldsRows.getFellow("1");
			String invForVal = StringUtils.trimToEmpty(invoiceFor.getSelectedItem().getValue().toString());

			if ("D".equals(invForVal)) {
				reqRowId = 4;
			} else if ("M".equals(invForVal)) {
				reqRowId = 3;
			} else {
				reqRowId = 2;
			}
		}

		for (int i = 0; i < reportConfiguration.getListReportFieldsDetails().size(); i++) {
			ReportFilterFields aReportFieldsDetails = reportConfiguration.getListReportFieldsDetails().get(i);
			String filedId = Long.toString(aReportFieldsDetails.getFieldID());
			if (reqRowId > 0) {
				if (aReportFieldsDetails.getFieldID() >= 2 && aReportFieldsDetails.getFieldID() <= 4) {
					if (reqRowId != aReportFieldsDetails.getFieldID()) {
						continue;
					}
				}
			}
			if (dymanicFieldsRows.hasFellow(filedId)) {
				filter = " = ";
				// COMPONENT
				Component component = dymanicFieldsRows.getFellow(filedId);
				FIELDCLASSTYPE fieldValueType = FIELDCLASSTYPE.valueOf(component.getClass().getSimpleName());

				// FILTER
				switch (fieldValueType) {
				case Textbox:
					Textbox textbox = (Textbox) component;
					String txtLabels = null;
					boolean isLOVSearch = aReportFieldsDetails.getFieldType().equals(FIELDTYPE.LOVSEARCH.toString());
					if (!isLOVSearch) {
						filter = getFilter(aReportFieldsDetails);
					}
					try {
						if (StringUtils.isNotEmpty(filter) && !("").equals(textbox.getValue().trim())
								&& !StringUtils.equals(textbox.getValue().trim(), "SELECTALL")) {
							// Prepare Where Condition
							if (isWhereCondition) {
								whereCondition = addAndCondition(whereCondition);
								if (!aReportFieldsDetails.isMultiSelectSearch()) {

									if (!"%".equals(filter)) {
										whereCondition.append(aReportFieldsDetails.getFieldDBName() + " " + filter
												+ " '" + textbox.getValue() + "'");
									} else {
										whereCondition.append(aReportFieldsDetails.getFieldDBName() + " like  '%"
												+ textbox.getValue() + "%'");
									}
									searchCriteriaDesc.append(aReportFieldsDetails.getFieldLabel() + " "
											+ filterDescMap.get(filter.trim()) + " ");

									if (this.isEntity
											&& StringUtils.equalsIgnoreCase("EntityCode",
													aReportFieldsDetails.getFieldDBName())
											&& StringUtils.equalsIgnoreCase("Entity",
													aReportFieldsDetails.getModuleName())) {
										this.entityValue = textbox.getValue();
									}

								} else {
									String inCondition = getINCondition(textbox.getValue());
									whereCondition.append(aReportFieldsDetails.getFieldDBName() + " in " + inCondition);
									String[] size = inCondition.split(",");
									if (size.length > 1) {
										searchCriteriaDesc.append(aReportFieldsDetails.getFieldLabel() + " is in ");
									} else {
										searchCriteriaDesc.append(aReportFieldsDetails.getFieldLabel() + " is ");
									}
								}
								if (isLOVSearch) {
									txtLabels = ((Textbox) textbox.getNextSibling()).getValue();
									searchCriteriaDesc.append(txtLabels + "\n");
								} else {
									searchCriteriaDesc.append(textbox.getValue() + "\n");
								}

							} else {// Saving Filter Template

								ReportSearchTemplate aReportSearchTemplate = getSearchTemplate(filter,
										aReportFieldsDetails);
								aReportSearchTemplate.setFieldValue(textbox.getValue());
								reportSearchTemplateList.add(aReportSearchTemplate);

							}
						}
					} catch (WrongValueException we) {
						// If LovSearch show error message on text box
						if (aReportFieldsDetails.getFieldType().trim().equals(FIELDTYPE.LOVSEARCH.toString())) {
							WrongValueException wee = new WrongValueException(
									we.getComponent().getParent().getChildren().get(2), we.getMessage());
							wve.add(wee);
						} else {
							wve.add(we);
						}
					}
					break;

				case Intbox:
					Intbox intbox = (Intbox) component;
					filter = getFilter(aReportFieldsDetails);
					// Check For Minimum and Max Ranges
					if (isWhereCondition && StringUtils.isNotEmpty(filter)
							&& !(aReportFieldsDetails.getFieldMaxValue() == 0
									&& aReportFieldsDetails.getFieldMinValue() == 0)) {
						if (intbox.getValue() > aReportFieldsDetails.getFieldMaxValue()
								|| intbox.getValue() < aReportFieldsDetails.getFieldMinValue()) {

							throw new WrongValueException(intbox,
									Labels.getLabel("FIELD_RANGE",
											new String[] { aReportFieldsDetails.getFieldLabel(),
													String.valueOf(aReportFieldsDetails.getFieldMinValue()),
													String.valueOf(aReportFieldsDetails.getFieldMaxValue()) }));

						}
					}

					try {
						if (StringUtils.isNotEmpty(filter)) {
							if (isWhereCondition) {// Prepare Where Condition
								whereCondition = addAndCondition(whereCondition);
								whereCondition.append(aReportFieldsDetails.getFieldDBName() + " " + filter + " '"
										+ intbox.getValue() + "'");
								searchCriteriaDesc.append(aReportFieldsDetails.getFieldLabel() + " "
										+ filterDescMap.get(filter.trim()) + " " + intbox.getValue() + "\n");
							} else {// Saving Filter Template
								ReportSearchTemplate aReportSearchTemplate = getSearchTemplate(filter,
										aReportFieldsDetails);
								aReportSearchTemplate.setFieldValue(String.valueOf(intbox.getValue()));
								reportSearchTemplateList.add(aReportSearchTemplate);

							}
						}
					} catch (WrongValueException we) {
						wve.add(we);
					}
					break;

				case Decimalbox:
					Decimalbox decimalbox = (Decimalbox) component;
					// Check For Minimum and Max Ranges
					if (isWhereCondition && StringUtils.isNotEmpty(filter)
							&& !(aReportFieldsDetails.getFieldMaxValue() == 0)
							&& (aReportFieldsDetails.getFieldMinValue() == 0)) {
						if (decimalbox.getValue().floatValue() > aReportFieldsDetails.getFieldMaxValue()
								|| decimalbox.getValue().floatValue() < aReportFieldsDetails.getFieldMinValue()) {

							throw new WrongValueException(decimalbox,
									Labels.getLabel("FIELD_RANGE",
											new String[] { aReportFieldsDetails.getFieldLabel(),
													String.valueOf(aReportFieldsDetails.getFieldMinValue()),
													String.valueOf(aReportFieldsDetails.getFieldMaxValue()) }));

						}
					}
					filter = getFilter(aReportFieldsDetails);
					try {
						if (StringUtils.isNotEmpty(filter)) {
							if (isWhereCondition) { // Prepare Where Condition
								whereCondition = addAndCondition(whereCondition);
								whereCondition.append(aReportFieldsDetails.getFieldDBName() + " " + filter + " '"
										+ decimalbox.getValue() + "'");
								searchCriteriaDesc.append(aReportFieldsDetails.getFieldLabel() + " "
										+ filterDescMap.get(filter.trim()) + " " + decimalbox.getValue() + "\n");
							} else { // Saving Filter Template
								ReportSearchTemplate aReportSearchTemplate = getSearchTemplate(filter,
										aReportFieldsDetails);
								aReportSearchTemplate.setFieldValue(String.valueOf(decimalbox.getValue()));
								reportSearchTemplateList.add(aReportSearchTemplate);

							}
						}
					} catch (WrongValueException we) {
						wve.add(we);
					}
					break;

				case Checkbox:
					Checkbox checkbox = (Checkbox) component;
					filter = getFilter(aReportFieldsDetails);
					try {
						if (StringUtils.isNotEmpty(filter)) { // Prepare Where Condition
							if (isWhereCondition) {
								whereCondition = addAndCondition(whereCondition);
								whereCondition.append(aReportFieldsDetails.getFieldDBName() + " " + filter + " '"
										+ (checkbox.isChecked() ? 1 : 0) + "'");
								searchCriteriaDesc.append(aReportFieldsDetails.getFieldLabel() + " "
										+ filterDescMap.get(filter.trim()) + " " + checkbox.getValue() + "\n");
							} else { // Saving Filter Template
								ReportSearchTemplate aReportSearchTemplate = getSearchTemplate(filter,
										aReportFieldsDetails);
								aReportSearchTemplate.setFieldValue(String.valueOf(checkbox.getValue()));
								reportSearchTemplateList.add(aReportSearchTemplate);

							}
						}
					} catch (WrongValueException we) {
						wve.add(we);
					}
					break;

				// DATE TYPE
				case Datebox:
					try {

						filter = getFilter(aReportFieldsDetails);
						if (!excludeDates) {
							if (StringUtils.isNotEmpty(filter)) {
								if (isWhereCondition) {// Prepare Where Condition
									Datebox datebox = (Datebox) component;
									if (datebox.getValue() == null && aReportFieldsDetails.isMandatory()) {
										throw new WrongValueException(datebox, Labels.getLabel("FIELD_NO_EMPTY",
												new String[] { aReportFieldsDetails.getFieldLabel() }));
									}
									whereCondition = getWhereConditionFromDateTimeAndRangeTypes(whereCondition,
											aReportFieldsDetails, component, filter + " ");

								} else { // Saving Filter Template
									Datebox datebox = (Datebox) component;
									if (datebox.getValue() == null && aReportFieldsDetails.isMandatory()) {
										throw new WrongValueException(datebox, Labels.getLabel("FIELD_NO_EMPTY",
												new String[] { aReportFieldsDetails.getFieldLabel() }));
									} else if (datebox.getValue() != null) {
										ReportSearchTemplate aReportSearchTemplate = getSearchTemplate(filter,
												aReportFieldsDetails);
										aReportSearchTemplate.setFieldValue(
												DateUtil.format(datebox.getValue(), PennantConstants.DBDateFormat));
										reportSearchTemplateList.add(aReportSearchTemplate);
									}

								}
							}
						}

					} catch (WrongValueException we) {
						wve.add(we);
					}
					break;

				// Only Time any date
				case Timebox:
					try {
						if (StringUtils.isNotEmpty(filter)) {// Prepare Where Condition
							if (isWhereCondition) {
								whereCondition = getWhereConditionFromDateTimeAndRangeTypes(whereCondition,
										aReportFieldsDetails, component, filter);

							} else { // Saving Filter Template
								Timebox timebox = (Timebox) component;
								if (timebox.getValue() != null) {
									ReportSearchTemplate aReportSearchTemplate = getSearchTemplate(filter,
											aReportFieldsDetails);
									aReportSearchTemplate.setFieldValue(String.valueOf(timebox.getValue()));
									reportSearchTemplateList.add(aReportSearchTemplate);
								}
							}
						}
					} catch (WrongValueException we) {
						wve.add(we);
					}
					break;

				// SELECTION TYPE
				case Combobox:
					Combobox combobox = (Combobox) component;
					try {
						// ComboBox validation for select from the list
						if (combobox.getSelectedItem() == null || (aReportFieldsDetails.isMandatory()
								&& combobox.getSelectedItem() != null
								&& combobox.getSelectedItem().getValue().equals(PennantConstants.List_Select))) {
							throw new WrongValueException(combobox, Labels.getLabel("STATIC_INVALID",
									new String[] { aReportFieldsDetails.getFieldLabel() }));
						} else if (combobox.getSelectedItem() != null
								&& !combobox.getSelectedItem().getValue().equals(PennantConstants.List_Select)) {
							if (isWhereCondition) {// Prepare Where Condition
								whereCondition = addAndCondition(whereCondition);
								whereCondition.append(aReportFieldsDetails.getFieldDBName() + " = '"
										+ combobox.getSelectedItem().getValue() + "'");
								searchCriteriaDesc.append(aReportFieldsDetails.getFieldLabel() + " "
										+ filterDescMap.get("=") + " " + combobox.getSelectedItem().getLabel() + "\n");
							} else { // Saving Filter Template
								ReportSearchTemplate aReportSearchTemplate = getSearchTemplate(filter,
										aReportFieldsDetails);
								aReportSearchTemplate.setFieldValue(combobox.getSelectedItem().getLabel());
								reportSearchTemplateList.add(aReportSearchTemplate);
							}

						}
					} catch (WrongValueException we) {
						wve.add(we);
					}
					break;

				case Bandbox:
					Bandbox bandbox = (Bandbox) component;
					try {
						if (!("").equals(bandbox.getValue().trim())) {// Prepare Where Condition
							if (isWhereCondition) {
								whereCondition = getWhereCondFromMSelectListBox(aReportFieldsDetails, bandbox,
										whereCondition);
							} else { // Saving Filter Template
								ReportSearchTemplate aReportSearchTemplate = getSearchTemplate(filter,
										aReportFieldsDetails);
								aReportSearchTemplate.setFieldValue(bandbox.getValue());
								reportSearchTemplateList.add(aReportSearchTemplate);
							}
						}
						break;
					} catch (WrongValueException we) {
						wve.add(we);
					}
				default:
					break;

				}
				filedId = null;
			}

		}

		Date appDate = SysParamUtil.getAppDate();
		// Prepare Where Condition for Date time type Range Components
		for (String filedId : rangeFieldsMap.keySet()) {

			Component fromDateBox = dymanicFieldsRows.getFellow("From_" + filedId);
			Component toDateBox = dymanicFieldsRows.getFellow("To_" + filedId);
			if (fromDateBox instanceof Datebox) {
				((Datebox) fromDateBox).setErrorMessage("");
			}
			if (toDateBox instanceof Datebox) {
				((Datebox) toDateBox).setErrorMessage("");
			}
			ReportFilterFields aReportFilterFields = rangeFieldsMap.get(filedId);
			try {
				if (isWhereCondition) { // Prepare Where Condition when
					/*
					 * if (toDateBox instanceof Datebox && ((Datebox) toDateBox).getValue() != null && (toDateBox
					 * instanceof Datebox && ((Datebox) toDateBox).getValue().after(appDate))) { throw new
					 * WrongValueException(toDateBox, Labels.getLabel("label_Error_ToDateMustbeBfrAppDate.vlaue")); }
					 */
					// Enter only From Value Selected
					if ((fromDateBox instanceof Datebox && ((Datebox) fromDateBox).getValue() != null)
							|| (fromDateBox instanceof Timebox && ((Timebox) fromDateBox).getValue() != null)
							|| (fromDateBox instanceof Intbox && ((Intbox) fromDateBox).getValue() != null)
							|| (fromDateBox instanceof Decimalbox && ((Decimalbox) fromDateBox).getValue() != null)) {
						// Checking To value is selected or not
						if (fromDateBox instanceof Datebox && ((Datebox) fromDateBox).getValue() != null
								&& (toDateBox instanceof Datebox && ((Datebox) toDateBox).getValue() == null)) {
							throw new WrongValueException(toDateBox,
									Labels.getLabel("label_Error_ToDateMandatory.vlaue"));
						}
						if ((toDateBox instanceof Datebox && ((Datebox) toDateBox).getValue() != null)
								|| (toDateBox instanceof Timebox && ((Timebox) toDateBox).getValue() != null)
								|| (toDateBox instanceof Intbox && ((Intbox) toDateBox).getValue() != null)
								|| (toDateBox instanceof Decimalbox && ((Decimalbox) toDateBox).getValue() != null)) {
							// Check From date is before To date
							if (fromDateBox instanceof Datebox
									&& ((Datebox) toDateBox).getValue().before(((Datebox) fromDateBox).getValue())) {
								throw new WrongValueException(fromDateBox,
										Labels.getLabel("label_Error_FromDateMustBfrTo.vlaue"));
							} else if (StringUtils.equals("menu_Item_GLcodewise_Report", reportMenuCode)) {// FIXME
								int diffDays = 31;
								if (DateUtil.getDaysBetween(((Datebox) fromDateBox).getValue(),
										((Datebox) toDateBox).getValue()) > diffDays && filedId.equals("11")) {
									throw new WrongValueException(toDateBox,
											Labels.getLabel("label_Difference_between_days") + " " + diffDays);
								}
							} else if (toDateBox instanceof Datebox
									&& ((Datebox) toDateBox).getValue().after(appDate)) {
								/*
								 * throw new WrongValueException(toDateBox,
								 * Labels.getLabel("label_Error_ToDateMustbeBfrAppDate.vlaue"));
								 */
							} else if (fromDateBox instanceof Datebox
									&& ((Datebox) fromDateBox).getValue().after(appDate)) {
								throw new WrongValueException(fromDateBox,
										Labels.getLabel("label_Error_FromDateMustbeBfrAppDate.vlaue"));
							} else if (fromDateBox instanceof Timebox // Check From time is before time
									&& DateUtil.compareTime(((Timebox) toDateBox).getValue(),
											((Timebox) fromDateBox).getValue(), true) == -1) {
								throw new WrongValueException(fromDateBox,
										Labels.getLabel("label_Error_FromTimeMustBfrTo.vlaue"));
							} else if (fromDateBox instanceof Decimalbox) {
								Number fromValue = fromDateBox instanceof Intbox ? ((Intbox) fromDateBox).getValue()
										: ((Decimalbox) fromDateBox).getValue();
								Number toValue = toDateBox instanceof Intbox ? ((Intbox) toDateBox).getValue()
										: ((Decimalbox) toDateBox).getValue();
								if (fromValue.doubleValue() > toValue.doubleValue()) {
									throw new WrongValueException(fromDateBox,
											Labels.getLabel("label_Error_FromValueMustGretaerTo.vlaue"));
								}
							} else if (StringUtils.equals("menu_Item_PresentmentStatusReport", reportMenuCode)) {// FIXME
								int diffentDays = SysParamUtil.getValueAsInt("PRESENTMENT_DAYS_DEF");
								if (DateUtil.getDaysBetween(((Datebox) fromDateBox).getValue(),
										((Datebox) toDateBox).getValue()) > diffentDays) {
									throw new WrongValueException(toDateBox,
											Labels.getLabel("label_Difference_between_days") + " " + diffentDays);
								}

							}
							if (!excludeDates) {
								whereCondition = getWhereConditionFromDateTimeAndRangeTypes(whereCondition,
										aReportFilterFields, fromDateBox, ">=");

								Datebox toDatebox = new Datebox();

								if (whereCondition.toString().contains("AuditDateTime")) {
									Datebox datebox = (Datebox) toDateBox;
									Date toDate = datebox.getValue();

									Calendar cal = Calendar.getInstance();
									cal.setTime(toDate);
									cal.set(Calendar.HOUR_OF_DAY, 23);
									cal.set(Calendar.MINUTE, 59);
									cal.set(Calendar.SECOND, 59);
									cal.set(Calendar.MILLISECOND, 999);

									toDatebox.setValue(cal.getTime());
									toDatebox.setId(datebox.getId());
								} else {
									toDatebox = (Datebox) toDateBox;
								}
								whereCondition = getWhereConditionFromDateTimeAndRangeTypes(whereCondition,
										aReportFilterFields, toDatebox, "<=");
							}
						}
					}
					if (aReportFilterFields.isMandatory()) {
						// If To Value Selected From Value not Selected show error
						// Checking To value is selected or not
						if ((fromDateBox instanceof Datebox && ((Datebox) fromDateBox).getValue() == null)
								|| (fromDateBox instanceof Timebox && ((Timebox) fromDateBox).getValue() == null)
								|| (fromDateBox instanceof Intbox && ((Intbox) fromDateBox).getValue() == null)
								|| (fromDateBox instanceof Decimalbox
										&& ((Decimalbox) fromDateBox).getValue() == null)) {
							if ((toDateBox instanceof Datebox && ((Datebox) toDateBox).getValue() == null)
									|| (toDateBox instanceof Timebox && ((Timebox) toDateBox).getValue() == null)
									|| (toDateBox instanceof Intbox && ((Intbox) toDateBox).getValue() == null)
									|| (toDateBox instanceof Decimalbox
											&& ((Decimalbox) toDateBox).getValue() == null)) {
								throw new WrongValueException(toDateBox,
										Labels.getLabel("label_Error_EitherValueMustSelect.value"));
							}
							throw new WrongValueException(fromDateBox,
									Labels.getLabel("label_Error_FromValueMustSelect.vlaue"));
						} else if ((toDateBox instanceof Datebox && ((Datebox) toDateBox).getValue() == null)
								|| (toDateBox instanceof Timebox && ((Timebox) toDateBox).getValue() == null)
								|| (toDateBox instanceof Intbox && ((Intbox) toDateBox).getValue() == null)
								|| (toDateBox instanceof Decimalbox && ((Decimalbox) toDateBox).getValue() == null)) {
							throw new WrongValueException(toDateBox,
									Labels.getLabel("label_Error_ToValueMustSelect.vlaue"));
						}
					}
					if (fromDateBox instanceof Datebox && ((Datebox) fromDateBox).getValue() != null
							&& (toDateBox instanceof Datebox && ((Datebox) toDateBox).getValue() == null)) {
						whereCondition = getWhereConditionFromDateTimeAndRangeTypes(whereCondition, aReportFilterFields,
								fromDateBox, ">=");
					}
					if (toDateBox instanceof Datebox && ((Datebox) toDateBox).getValue() != null
							&& (fromDateBox instanceof Datebox && ((Datebox) fromDateBox).getValue() == null)) {
						whereCondition = getWhereConditionFromDateTimeAndRangeTypes(whereCondition, aReportFilterFields,
								toDateBox, "<=");
					}
				} else {// Saving Filter Template only when from and to is selected
					String fromValue = null;
					String toValue = null;
					if (fromDateBox instanceof Datebox) {
						if (!excludeDates) {
							Datebox fromDate = (Datebox) fromDateBox;
							Datebox todate = (Datebox) toDateBox;
							if (fromDate.getValue() != null && todate.getValue() != null) {
								fromValue = DateUtil.formatToShortDate(fromDate.getValue());
								toValue = DateUtil.formatToShortDate(todate.getValue());
							}
						}
					} else if (fromDateBox instanceof Timebox) {
						Timebox fromDate = (Timebox) fromDateBox;
						Timebox todate = (Timebox) toDateBox;
						if (fromDate.getValue() != null && todate.getValue() != null) {
							fromValue = fromDate.getValue().toString();
							toValue = todate.getValue().toString();
						}
					} else if (fromDateBox instanceof Intbox) {
						Intbox fromDate = (Intbox) fromDateBox;
						Intbox todate = (Intbox) toDateBox;
						if (fromDate.getValue() != null && todate.getValue() != null) {
							fromValue = fromDate.getValue().toString();
							toValue = todate.getValue().toString();
						}
					} else if (fromDateBox instanceof Decimalbox) {
						Decimalbox fromDate = (Decimalbox) fromDateBox;
						Decimalbox todate = (Decimalbox) toDateBox;
						if (fromDate.getValue() != null && todate.getValue() != null) {
							fromValue = fromDate.getValue().toString();
							toValue = todate.getValue().toString();
						}
					}

					if (fromValue != null && toValue != null) {
						ReportSearchTemplate aReportSearchTemplate = getSearchTemplate(filter, aReportFilterFields);
						aReportSearchTemplate.setFieldValue(fromValue + "&" + toValue);
						reportSearchTemplateList.add(aReportSearchTemplate);
					}

				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

		}

		if (!isWhereCondition && wve.size() == 0) {
			return reportSearchTemplateList;
		}

		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];

			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		// Add Static variables
		if ("where".equals(whereCondition.toString().trim()) && !(saticValuesWhereCondition.length() == 0)) {
			whereCondition.append(" " + saticValuesWhereCondition.toString());

		} else if (!"where".equals(whereCondition.toString().trim()) && !(saticValuesWhereCondition.length() == 0)) {
			whereCondition.append(" and " + saticValuesWhereCondition.toString());
		}

		logger.debug("where Condition :" + whereCondition.toString());

		return whereCondition;
	}

	/**
	 * This method returns new ReportSearchTemplate object by setting default values
	 * 
	 * @param filter
	 * @param aReportFieldsDetails
	 * @return
	 */
	private ReportSearchTemplate getSearchTemplate(String filter, ReportFilterFields aReportFieldsDetails) {
		logger.debug("Entering");
		ReportSearchTemplate aReportSearchTemplate = new ReportSearchTemplate();
		aReportSearchTemplate.setReportID(aReportFieldsDetails.getReportID());
		aReportSearchTemplate.setFieldID(aReportFieldsDetails.getFieldID());
		aReportSearchTemplate
				.setFilter(aReportFieldsDetails.isFilterRequired() ? filter : aReportFieldsDetails.getDefaultFilter());
		aReportSearchTemplate.setFieldType(aReportFieldsDetails.getFieldType());
		aReportSearchTemplate.setVersion(0);
		aReportSearchTemplate.setRoleCode(getRole());
		logger.debug("Leaving");
		return aReportSearchTemplate;
	}

	/**
	 * This method returns the filter
	 * 
	 * @param aReportFieldsDetails
	 * @return
	 */
	private String getFilter(ReportFilterFields aReportFieldsDetails) {
		logger.debug("Entering");
		String filter;
		if (aReportFieldsDetails.isFilterRequired()) {
			Listbox sortOperatorList;
			sortOperatorList = (Listbox) dymanicFieldsRows
					.getFellowIfAny("sortOperator_" + aReportFieldsDetails.getFieldID());
			if (sortOperatorList.getSelectedItem() != null) {
				filter = sortOperatorList.getSelectedItem().getLabel();
			} else {
				filter = "";
			}
		} else {
			filter = aReportFieldsDetails.getDefaultFilter();
		}
		logger.debug("Entering");
		return filter;
	}

	/**
	 * Prepare where condition for Date type Components
	 * 
	 * @param whereCondition
	 * @param aReportFieldsDetails
	 * @param component
	 */
	@SuppressWarnings("unused")
	private StringBuilder getWhereConditionFromDateTimeAndRangeTypes(StringBuilder whereCondition,
			ReportFilterFields aReportFieldsDetails, Component component, String filter) {
		logger.debug("Leaving");

		String filedID = null;
		Object filedValue = null;
		String dateFormat = null;

		FIELDCLASSTYPE fieldDataType = FIELDCLASSTYPE.valueOf(component.getClass().getSimpleName());
		FIELDTYPE fieldValueType = FIELDTYPE.valueOf(aReportFieldsDetails.getFieldType());

		// FILTER
		switch (fieldDataType) {
		case Datebox:
			Datebox datebox = (Datebox) component;
			dateFormat = DateFormat.SHORT_DATE.getPattern();
			if (datebox.getValue() != null) {
				filedID = datebox.getId();
				filedValue = datebox.getValue();
				// Prepare query For Only Date Selection
				if (fieldValueType.toString().equals(FIELDTYPE.DATE.toString())
						|| fieldValueType.toString().equals(FIELDTYPE.DATERANGE.toString())) {
					whereCondition = addAndCondition(whereCondition);
					String exactDate = "";
					if (App.DATABASE == Database.DB2) {
						exactDate = "DATE(" + aReportFieldsDetails.getFieldDBName() + ") " + filter + "'"
								+ DateUtil.format(datebox.getValue(), PennantConstants.DBDateFormat) + "'";
					}

					if (whereCondition.toString().contains("AuditDateTime")) {
						if (App.DATABASE == Database.SQL_SERVER) {
							exactDate = "CONVERT(DATETIME, FLOOR(CONVERT(FLOAT," + aReportFieldsDetails.getFieldDBName()
									+ "))) " + filter + "'"
									+ DateUtil.format(datebox.getValue(), PennantConstants.DBDateTimeFormat) + "'";
						}

						if (App.DATABASE == Database.ORACLE || App.DATABASE == Database.POSTGRES) {
							exactDate = aReportFieldsDetails.getFieldDBName() + " " + filter + "'"
									+ DateUtil.format(datebox.getValue(), PennantConstants.DBDateTimeFormat) + "'";
						}
					} else {
						if (App.DATABASE == Database.SQL_SERVER) {
							exactDate = "CONVERT(DATETIME, FLOOR(CONVERT(FLOAT," + aReportFieldsDetails.getFieldDBName()
									+ "))) " + filter + "'"
									+ DateUtil.format(datebox.getValue(), PennantConstants.DBDateFormat) + "'";
						}

						if (App.DATABASE == Database.ORACLE || App.DATABASE == Database.POSTGRES) {
							exactDate = aReportFieldsDetails.getFieldDBName() + " " + filter + "'"
									+ DateUtil.format(datebox.getValue(), PennantConstants.DBDateFormat) + "'";
						}
					}
					whereCondition.append(exactDate);

				}
				// Prepare query for Exact Date and Time
				if (fieldValueType.toString().equals(FIELDTYPE.DATETIME.toString())
						|| fieldValueType.toString().equals(FIELDTYPE.DATETIMERANGE.toString())) {
					String dateTime = DateUtil.format(datebox.getValue(), PennantConstants.DBDateTimeFormat);
					whereCondition = addAndCondition(whereCondition);
					String exactDateTime = "";
					dateFormat = PennantConstants.dateTimeAMPMFormat;
					if (App.DATABASE == Database.DB2) {
						if ("=".equals(filter.trim()) || "<>".equals(filter.trim())) {
							exactDateTime = "DATE(" + aReportFieldsDetails.getFieldDBName() + ") = '" + dateTime
									+ "' and " + "TIME(" + aReportFieldsDetails.getFieldDBName() + ") " + filter + "'"
									+ dateTime + "'";
						} else {
							exactDateTime = aReportFieldsDetails.getFieldDBName() + " " + filter + "'" + dateTime + "'";
						}
					}

					exactDateTime = aReportFieldsDetails.getFieldDBName() + " " + filter + "'" + dateTime + "'";
					whereCondition.append(exactDateTime);
				}
			}
			break;

		case Timebox:
			// Prepare query For Only Time
			Timebox timeBox = (Timebox) component;
			filedID = timeBox.getId();
			dateFormat = PennantConstants.timeFormat;
			filedValue = timeBox.getValue().toString();
			if (timeBox.getValue() != null) {
				whereCondition = addAndCondition(whereCondition);
				String timeFunction = "";
				if (App.DATABASE == Database.DB2) {
					timeFunction = "TIME(" + aReportFieldsDetails.getFieldDBName() + ")" + filter + "'"
							+ DateUtil.format(timeBox.getValue(), PennantConstants.DBDateTimeFormat) + "'";
				}
				if (App.DATABASE == Database.SQL_SERVER) {
					timeFunction = "CONVERT(VARCHAR(8)," + aReportFieldsDetails.getFieldDBName() + ",108)" + filter
							+ "'" + DateUtil.format(timeBox.getValue(), PennantConstants.DBTimeFormat) + "'";
				}
				whereCondition.append(timeFunction);
			}
			break;
		// Only IntBox and Decimal comes only when Range Purpose
		case Intbox:
			// Prepare query For Only Time
			Intbox intBox = (Intbox) component;
			filedID = intBox.getId();
			filedValue = intBox.getValue().toString();
			if (intBox.getValue() != null) {
				whereCondition = addAndCondition(whereCondition);
				whereCondition.append(aReportFieldsDetails.getFieldDBName() + " " + filter + " '" + filedValue + "'");
			}
			break;
		case Decimalbox:
			Decimalbox decimalbox = (Decimalbox) component;
			filedID = decimalbox.getId();
			filedValue = decimalbox.getValue().toString();
			if (decimalbox.getValue() != null) {
				whereCondition = addAndCondition(whereCondition);
				whereCondition.append(aReportFieldsDetails.getFieldDBName() + " " + filter + " '" + filedValue + "'");
			}
			break;
		default:
			break;
		}
		if (component instanceof Datebox && ((Datebox) component).getValue() != null) {
			filedValue = DateUtil.format((Date) filedValue, dateFormat);
		}
		if (rangeFieldsMap.containsKey(String.valueOf(aReportFieldsDetails.getFieldID()))) {
			boolean isDateType = component instanceof Datebox;

			if (filedID.equals("From_" + aReportFieldsDetails.getFieldID())) {
				searchCriteriaDesc
						.append(aReportFieldsDetails.getFieldLabel() + " is between  " + filedValue + " and ");
			} else {

				searchCriteriaDesc.append(filedValue + "\n");

			}
		} else {
			if (component instanceof Datebox && ((Datebox) component).getValue() == null) {
				// NOthing to do
			} else {
				searchCriteriaDesc.append(aReportFieldsDetails.getFieldLabel() + " " + filterDescMap.get(filter.trim())
						+ " " + filedValue + "\n");
			}
		}
		logger.debug("Leaving");
		return whereCondition;
	}

	/**
	 * This method appends where condition
	 * 
	 * @param whereCondition
	 */
	private StringBuilder addAndCondition(StringBuilder whereCondition) {
		if (("").equals(whereCondition.toString().trim())) {
			whereCondition.append("and ");
		} else if (!"where".equals(whereCondition.toString().trim())
				&& !("").equals(whereCondition.toString().trim())) {
			whereCondition.append("and ");
		}
		return whereCondition;
	}

	/**
	 * Setting Validations for Components
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		for (int i = 0; i < reportConfiguration.getListReportFieldsDetails().size(); i++) {

			boolean isRangeField = false;
			ReportFilterFields aReportFilterFields = reportConfiguration.getListReportFieldsDetails().get(i);
			Object tempComponent = dymanicFieldsRows.getFellowIfAny(Long.toString(aReportFilterFields.getFieldID()));
			aReportFilterFields.getFilterFileds();

			// If not CheckBox type
			if (!(tempComponent instanceof LabelImageElement)) {
				InputElement component = (InputElement) dymanicFieldsRows
						.getFellowIfAny(Long.toString(aReportFilterFields.getFieldID()));
				if (rangeFieldsMap.containsKey(String.valueOf(aReportFilterFields.getFieldID()))) {
					isRangeField = true;
					component = (InputElement) dymanicFieldsRows
							.getFellowIfAny("From_" + aReportFilterFields.getFieldID());
				}
				if (component != null) {
					/*
					 * We set Constraint only if mandatory for all types and for text box we keep Constraint even non
					 * mandatory Because To prevent Injections
					 */

					if (aReportFilterFields.isMandatory() && (aReportFilterFields.getFieldType()
							.equals(FIELDTYPE.LOVSEARCH.toString())
							|| aReportFilterFields.getFieldType().contains(FIELDTYPE.DATE.toString())
							|| aReportFilterFields.getFieldType().contains(FIELDTYPE.TIME.toString())
							|| aReportFilterFields.getFieldType().equals(FIELDTYPE.MULTISELANDLIST.toString())
							|| aReportFilterFields.getFieldType().equals(FIELDTYPE.MULTISELINLIST.toString()))) {
						if (!(component instanceof Datebox)) {
							if (isRangeField) {
								component.setConstraint("NO EMPTY:" + Labels.getLabel("RANGE_MUST_SELECT",
										new String[] { aReportFilterFields.getFieldLabel() }));
								isRangeField = false;

							} else {
								component.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
										new String[] { aReportFilterFields.getFieldLabel() }));

							}
						}
					} else if (aReportFilterFields.isMandatory() && component instanceof Combobox) {
						component.setConstraint(new StaticListValidator(listSelectionMaps.get(component.getId()),
								aReportFilterFields.getFieldLabel()));

					} else {// For Text box
						if (aReportFilterFields.getFieldConstraint() != null
								&& !("").equals(aReportFilterFields.getFieldConstraint().trim())) {
							aReportFilterFields.setFieldConstraint(
									aReportFilterFields.getFieldConstraint().replaceAll("^\"|\"$", ""));
							component.setConstraint(new PTStringValidator(aReportFilterFields.getFieldErrorMessage(),
									aReportFilterFields.getFieldConstraint(), aReportFilterFields.isMandatory()));
						}
					}
				}
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Removing validation for Components
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		for (int i = 0; i < reportConfiguration.getListReportFieldsDetails().size(); i++) {
			ReportFilterFields aReportFilterFields = reportConfiguration.getListReportFieldsDetails().get(i);

			Object tempComponent = dymanicFieldsRows.getFellowIfAny(Long.toString(aReportFilterFields.getFieldID()));
			if (!(tempComponent instanceof LabelImageElement)) {
				InputElement component = (InputElement) dymanicFieldsRows
						.getFellowIfAny(Long.toString(aReportFilterFields.getFieldID()));
				InputElement rangecomponent = null;
				if (rangeFieldsMap.containsKey(Long.toString(aReportFilterFields.getFieldID()))) {
					component = (InputElement) dymanicFieldsRows
							.getFellowIfAny("From_" + aReportFilterFields.getFieldID());
					rangecomponent = (InputElement) dymanicFieldsRows
							.getFellowIfAny("To_" + aReportFilterFields.getFieldID());
				}
				if (component != null) {
					component.setConstraint("");
					component.setErrorMessage("");
				}
				if (rangecomponent != null) {
					rangecomponent.setConstraint("");
					rangecomponent.setErrorMessage("");
				}
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * This method call the report control to generate the report
	 */
	private void doShowReport(String whereCond, String whereCond2, String fromDate, String toDate, String whereCond1) {
		logger.info(Literal.ENTERING);

		Map<String, Object> argsMap = new HashMap<String, Object>(10);
		String userName = getUserWorkspace().getLoggedInUser().getFullName();
		argsMap.put("userName", userName);
		argsMap.put("reportHeading", reportConfiguration.getReportHeading());
		argsMap.put("reportGeneratedBy", Labels.getLabel("Reports_footer_ReportGeneratedBy.lable"));
		argsMap.put("appDate", SysParamUtil.getAppDate());
		argsMap.put("appCcy", SysParamUtil.getAppCurrency());
		argsMap.put("appccyEditField", SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT));
		argsMap.put("unitParam", unitName);

		if (whereCond != null) {
			argsMap.put("whereCondition", whereCond);
		}

		if (whereCond2 != null) {
			argsMap.put("whereCondition1", whereCond2);
		}

		if (whereCond1 != null) {
			if (StringUtils.contains(whereCond1, "appPercentage")
					&& (StringUtils.equals(reportMenuCode, "menu_Item_ForeclosureTerminationReport"))) {
				String[] args = StringUtils.split(whereCond1, "=");
				argsMap.put("appPercentage", "" + StringUtils.remove(args[1], "}"));
			}
			argsMap.put("whereCondition2", whereCond1);
		}

		Date befDate = null;
		Date aftDate = null;

		if (!(StringUtils.equals(reportMenuCode, "menu_Item_FeeReport")
				|| StringUtils.equals(reportMenuCode, "menu_Item_LoanDisbursementBasicListing")
				|| StringUtils.equals(reportMenuCode, "menu_Item_LoanClosureReport"))) {
			befDate = DateUtil.addDays(DateUtil.parseFullDate(fromDate), -1);
			aftDate = DateUtil.addDays(DateUtil.parseFullDate(toDate), 1);
		}

		if (fromDate != null) {
			if (StringUtils.equals(reportMenuCode, "menu_Item_FeeReport")
					|| StringUtils.equals(reportMenuCode, "menu_Item_LoanDisbursementBasicListing")
					|| StringUtils.equals(reportMenuCode, "menu_Item_LoanClosureReport")) {
				argsMap.put("DateOne", fromDate.toString());
			} else {
				argsMap.put("fromDate", "'" + DateUtil.parseFullDate(fromDate).toString() + "'");
			}
		}

		if (fromDate != null && befDate != null) {
			argsMap.put("befDate", "'" + befDate.toString() + "'");
		}

		if (toDate != null) {
			if (StringUtils.equals(reportMenuCode, "menu_Item_FeeReport")
					|| StringUtils.equals(reportMenuCode, "menu_Item_LoanDisbursementBasicListing")
					|| StringUtils.equals(reportMenuCode, "menu_Item_LoanClosureReport")) {
				argsMap.put("DateTwo", toDate.toString());
			} else {
				argsMap.put("toDate", "'" + DateUtil.parseFullDate(toDate).toString() + "'");
			}
		}

		if (toDate != null && aftDate != null) {
			argsMap.put("aftDate", "'" + aftDate.toString() + "'");
		}

		if (!reportConfiguration.isPromptRequired()) {
			argsMap.put("whereCondition", "");
		}

		if (this.isEntity) {
			String path = PathUtil.REPORTS_IMAGE_CLIENT_PATH + PathUtil.REPORTS_IMAGE_CLIENT_IMAGE + this.entityValue
					+ PathUtil.REPORTS_IMAGE_PNG_FORMAT;
			argsMap.put("organizationLogo", PathUtil.getPath(path));
		} else {
			if (StringUtils.equals(reportConfiguration.getMenuItemCode(), "menu_Item_PaymentSchedule")) {
				File file = new File(PathUtil.getPath(PathUtil.PAYMENT_SCHEDULE_IMAGE_CLIENT));
				if (file.exists()) {
					argsMap.put("organizationLogo", PathUtil.getPath(PathUtil.PAYMENT_SCHEDULE_IMAGE_CLIENT));
				} else {
					argsMap.put("organizationLogo", PathUtil.getPath(PathUtil.REPORTS_IMAGE_CLIENT));
				}
			} else {
				argsMap.put("organizationLogo", PathUtil.getPath(PathUtil.REPORTS_IMAGE_CLIENT));
			}
			argsMap.put("waterMark", PathUtil.getPath(PathUtil.REPORTS_IMAGE_CLIENT_WATERMARK));
		}

		argsMap.put("signimage", PathUtil.getPath(PathUtil.REPORTS_IMAGE_SIGN));
		argsMap.put("productLogo", PathUtil.getPath(PathUtil.REPORTS_IMAGE_PRODUCT));
		argsMap.put("bankName", Labels.getLabel("label_ClientName"));

		argsMap.put("searchCriteria", searchCriteriaDesc.toString());
		String reportName = reportConfiguration.getReportJasperName();// This will come dynamically

		if (reportConfiguration.isScheduleReq()) {
			String finReference = "";
			reportSearchTemplateFieldsList = (List<ReportSearchTemplate>) doPrepareWhereConditionOrTemplate(false,
					true);
			for (int i = 0; i < reportSearchTemplateFieldsList.size(); i++) {

				List<ReportFilterFields> filters = reportConfiguration.getListReportFieldsDetails();
				for (int j = 0; j < filters.size(); j++) {
					if (reportSearchTemplateFieldsList.get(i).getFieldID() != filters.get(j).getFieldID()) {
						continue;
					}
					if (!StringUtils.equalsIgnoreCase(filters.get(j).getFieldName(), "Loan Reference")) {
						continue;
					}
					finReference = reportSearchTemplateFieldsList.get(i).getFieldValue();
				}
			}

			if (StringUtils.isNotBlank(finReference)) {
				Long finID = financeDetailService.getFinID(finReference, TableType.MAIN_TAB);
				FinScheduleData scheduleData = financeDetailService.getFinSchDataByFinRef(finID, "", 0);
				renderer = new FinScheduleListItemRenderer();
				List<FinanceScheduleReportData> schdList = renderer.getPrintScheduleData(scheduleData, null, null,
						false, true, true);

				List<Object> subList = new ArrayList<Object>();
				subList.add(schdList);
				JRBeanCollectionDataSource subListDS = new JRBeanCollectionDataSource(schdList);
				argsMap.put("subDataSource2", subListDS);
			}
		}

		try {

			String dataSourceName = reportConfiguration.getDataSourceName();
			if ((!isExcel && !this.rows_formatType.isVisible())
					|| (this.rows_formatType.isVisible() && this.pdfFormat.isChecked())) {
				Map<String, Object> auditMap = new HashMap<String, Object>(4);
				auditMap.put("parentWindow", this.window_ReportPromptFilterCtrl);
				auditMap.put("reportName", reportConfiguration.getReportName().replace(EXCEL_TYPE, ""));
				auditMap.put("tabbox", tabbox);
				auditMap.put("searchClick", searchClick);
				auditMap.put("selectTab", selectTab);
				if (dialogWindow != null) {
					auditMap.put("dialogWindow", dialogWindow);
				}

				argsMap.putAll(auditMap);

				ReportsUtil.showPDF(PathUtil.REPORTS_ORGANIZATION, reportName, argsMap, dataSourceName);
			} else {
				ReportsUtil.downloadExcel(PathUtil.REPORTS_ORGANIZATION, reportName, argsMap, dataSourceName);

				if (selectTab != null) {
					selectTab.onClose();
				}
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
			closeDialog();
		}
		logger.debug(Literal.LEAVING);
	}

	// COMPONENT EVENTS

	/**
	 * When user Clicks on "Add Search Template"
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onClick$btnSaveTemplate(Event event) {
		logger.debug("Entering" + event.toString());

		reportSearchTemplateFieldsList = (List<ReportSearchTemplate>) doPrepareWhereConditionOrTemplate(false, false);
		if (reportSearchTemplateFieldsList.size() > 0) {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("reportGenerationPromptDialogCtrl", this);
			map.put("reportId", reportConfiguration.getReportID());
			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/Reports/ReportSearchTemplatePromptDialog.zul", null, map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}

		} else {

			MessageUtil.showError(Labels.getLabel("label_Empty_Filter.error"));

		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When user Clicks on "Delete Search Template"
	 * 
	 * @param event
	 */
	public void onClick$btnDeleteTemplate(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		final String msg = Labels.getLabel("label_ReportGenerationDialgCtrl_Delete_Template") + "\n\n --> "
				+ this.cbSelectTemplate.getSelectedItem().getLabel();

		MessageUtil.confirm(msg, evnt -> {
			if (Messagebox.ON_YES.equals(evnt.getName())) {
				try {
					boolean isRcdDeleted = getReportConfigurationService().deleteSearchTemplate(
							reportConfiguration.getReportID(), getUserWorkspace().getLoggedInUser().getUserId(),
							this.cbSelectTemplate.getSelectedItem().getLabel());

					if (isRcdDeleted) {
						this.cbSelectTemplate.getSelectedItem().detach();
						this.cbSelectTemplate.setValue(Labels.getLabel("Combo.Select"));
						this.btnDeleteTemplate.setDisabled(true);
						doClearComponents();
						Clients.showNotification(Labels.getLabel("label_DeleteSuccess"), "info", null, null, -1);
					} else {
						Clients.showNotification(Labels.getLabel("label_DeleteFail"), "warning", null, null, -1);
					}
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
					showMessage(e);
				}

			}
		});

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * This method calls from ReportSearchTemplatePromptDialogCtrl on click save button .it sets template name and save
	 * the template
	 * 
	 * @param reportSearchTemplateList
	 * @return
	 */
	protected boolean doSaveTemplate(long reportId, long usrId, String templateName) {
		logger.debug("Entering");
		int recordCount = getReportConfigurationService().getRecordCountByTemplateName(reportId, usrId, templateName);
		if (recordCount > 0) {
			MessageUtil.showError(Labels.getLabel("label_TemplateName_AlreadyExist.error"));
			return false;

		} else {
			for (ReportSearchTemplate aReportSearchTemplate : reportSearchTemplateFieldsList) {
				aReportSearchTemplate.setTemplateName(templateName.trim());
				aReportSearchTemplate.setUsrID(usrId);
			}
			if (reportSearchTemplateFieldsList.size() > 0) {
				getReportConfigurationService().saveOrUpdateSearchTemplate(reportSearchTemplateFieldsList, true);
			}
			reportSearchTemplateFieldsList = null;
			doFillcbSelectTemplate();
			return true;

		}
	}

	/**
	 * When select Premise code ComboBox
	 * 
	 * @param event
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void onSelect$cbSelectTemplate(Event event)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		logger.debug("Entering" + event.toString());
		List<ReportSearchTemplate> aReportSearchTemplateList = null;
		doClearComponents();
		if (this.cbSelectTemplate.getSelectedItem() != null
				&& !this.cbSelectTemplate.getSelectedItem().getValue().equals(PennantConstants.List_Select)) {
			aReportSearchTemplateList = templateLibraryMap.get(this.cbSelectTemplate.getSelectedItem().getValue());
			this.btnDeleteTemplate.setDisabled(false);
		} else {
			this.btnDeleteTemplate.setDisabled(true);
		}

		if (aReportSearchTemplateList != null) {
			Map<Long, ReportSearchTemplate> fieldsMap = new HashMap<Long, ReportSearchTemplate>();
			for (ReportSearchTemplate aReportSearchTemplate : aReportSearchTemplateList) {
				fieldsMap.put(aReportSearchTemplate.getFieldID(), aReportSearchTemplate);
			}
			doSetSearchTemplate(fieldsMap);
		}
		renderMap.clear();
		valueMap.clear();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When we select template from ComBo Box all components will be filled against that template .This method fills all
	 * components and filters against Template
	 * 
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private void doSetSearchTemplate(Map<Long, ReportSearchTemplate> reportSearchTemplateMap)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		logger.debug("Entering");
		for (int i = 0; i < reportConfiguration.getListReportFieldsDetails().size(); i++) {
			ReportFilterFields aReportFieldsDetails = reportConfiguration.getListReportFieldsDetails().get(i);

			if (reportSearchTemplateMap.containsKey(aReportFieldsDetails.getFieldID())) {
				ReportSearchTemplate reportSearchTemplate = reportSearchTemplateMap
						.get(aReportFieldsDetails.getFieldID());
				// Here We will check if Field type changed after template saved for avoiding problems in values
				// displaying
				if (reportSearchTemplate.getFieldType().equals(aReportFieldsDetails.getFieldType())) {
					doSetValueOrClearOpertionOnFields(aReportFieldsDetails, reportSearchTemplate, false);
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * This Method Set Template Values or Clear and set all default values by flag isClearComponents
	 * 
	 * @param aReportFilterField
	 * @param reportSearchTemplate
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private void doSetValueOrClearOpertionOnFields(ReportFilterFields aReportFilterField,
			ReportSearchTemplate reportSearchTemplate, boolean isClearComponents)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String filedId;
		if (aReportFilterField.getFieldType().contains("RANGE")) {
			filedId = "From_" + aReportFilterField.getFieldID();
		} else {
			filedId = Long.toString(aReportFilterField.getFieldID());
		}
		if (dymanicFieldsRows.hasFellow(filedId)) {

			// Set the Filter
			if (aReportFilterField.isFilterRequired()) {
				setFilterValue(aReportFilterField.getFieldID(),
						isClearComponents ? "" : reportSearchTemplate.getFilter());
			}
			// COMPONENT
			Component component;
			component = dymanicFieldsRows.getFellow(filedId);

			FIELDCLASSTYPE fieldValueType = FIELDCLASSTYPE.valueOf(component.getClass().getSimpleName());
			// FILTER
			switch (fieldValueType) {
			case Textbox:
				Textbox textbox = (Textbox) component;
				if (isClearComponents) {
					textbox.setValue("");
					if (aReportFilterField.getFieldType().equals(FIELDTYPE.LOVSEARCH.toString())) {
						Textbox lovDisplayText = (Textbox) textbox.getNextSibling();
						lovDisplayText.setValue("");
						lovSearchBufferMap.remove(filedId);
					}
				} else {
					textbox.setValue(reportSearchTemplate.getFieldValue());
					if (aReportFilterField.getFieldType().equals(FIELDTYPE.LOVSEARCH.toString())) {
						setLovSearchValue(aReportFilterField, reportSearchTemplate.getFieldValue(),
								(Textbox) textbox.getNextSibling());
					}
				}
				break;
			case Intbox:
				Intbox intbox = (Intbox) component;
				String labels[] = null;
				if (isClearComponents) {
					intbox.setText("");
				} else {
					labels = reportSearchTemplate.getFieldValue().split("-");
					intbox.setText(labels[0]);
				}
				if (aReportFilterField.getFieldType().contains("RANGE")) {
					Intbox toIntbox = (Intbox) intbox.getNextSibling().getNextSibling();
					if (isClearComponents) {
						toIntbox.setValue(null);
						toIntbox.setText("");

					} else {
						labels = reportSearchTemplate.getFieldValue().split("-");
						toIntbox.setText(labels[1]);
					}
				}
				labels = null;
				break;

			case Decimalbox:
				Decimalbox decimalbox = (Decimalbox) component;

				if (isClearComponents) {
					decimalbox.setText("");
				} else {
					labels = reportSearchTemplate.getFieldValue().split("-");
					decimalbox.setText(labels[0]);
				}
				if (aReportFilterField.getFieldType().contains("RANGE")) {
					Decimalbox toDecimalbox = (Decimalbox) decimalbox.getNextSibling().getNextSibling();
					if (isClearComponents) {
						toDecimalbox.setValue(BigDecimal.ZERO);
						toDecimalbox.setText("");

					} else {
						labels = reportSearchTemplate.getFieldValue().split("-");
						toDecimalbox.setText(labels[1]);
					}
				}
				labels = null;
				break;

			case Checkbox:
				Checkbox checkbox = (Checkbox) component;
				if (isClearComponents) {
					checkbox.setChecked(false);
				} else {
					checkbox.setChecked(Boolean.parseBoolean(reportSearchTemplate.getFieldValue().trim()));
				}

				break;
			// DATE TYPE
			case Datebox:
				String[] dateFields = null;
				if (reportSearchTemplate != null && reportSearchTemplate.getFieldValue() != null) {
					if (reportSearchTemplate.getFieldValue().contains("&")) {
						dateFields = reportSearchTemplate.getFieldValue().split("&");
					} else {
						dateFields = new String[1];
						dateFields[0] = reportSearchTemplate.getFieldValue();
					}
				}
				Datebox datebox = (Datebox) component;
				if (isClearComponents) {
					datebox.setValue(null);
					datebox.setText("");
				} else {
					if (dateFields != null && dateFields[0] != null) {
						try {
							if ("DATE".equalsIgnoreCase(aReportFilterField.getFieldType())) {
								datebox.setValue(DateUtil.getDate(dateFields[0], PennantConstants.DBDateFormat));
							} else {
								datebox.setValue(DateUtil.getDate(dateFields[0], PennantConstants.dateFormat));
							}
						} catch (Exception e) {
							logger.error("Error in Formating The Date", e);
							datebox.setValue(DateUtil.getSysDate());
						}
					} else {
						datebox.setValue(DateUtil.getSysDate());
					}
				}
				if (aReportFilterField.getFieldType().contains("RANGE")) {
					Datebox todatebox = (Datebox) datebox.getNextSibling().getNextSibling();
					if (isClearComponents) {
						todatebox.setValue(null);
						todatebox.setText("");
					} else {
						if (dateFields != null && dateFields.length == 2 && dateFields[1] != null) {
							try {
								todatebox.setValue(DateUtil.getDate(dateFields[1], PennantConstants.dateFormat));
							} catch (Exception e) {
								logger.error("Error in Formating The Date", e);
								todatebox.setValue(DateUtil.getSysDate());
							}
						} else {
							todatebox.setValue(DateUtil.getSysDate());
						}
					}
				}
				break;

			case Timebox:
				Timebox timebox = (Timebox) component;
				if (isClearComponents) {
					timebox.setValue(null);
					timebox.setText("");
				} else {
					timebox.setValue(DateUtil.getSysDate());
				}
				if (aReportFilterField.getFieldType().contains("RANGE")) {
					Timebox toTimebox = (Timebox) timebox.getNextSibling().getNextSibling();
					if (isClearComponents) {
						toTimebox.setValue(null);
						toTimebox.setText("");

					} else {
						toTimebox.setValue(DateUtil.getSysDate());
					}
				}
				break;
			// SELECTION TYPE
			case Combobox:
				Combobox combobox = (Combobox) component;
				if (isClearComponents) {
					combobox.setValue(Labels.getLabel("Combo.Select"));
				} else {
					combobox.setValue(reportSearchTemplate.getFieldValue());
				}

				break;
			case Bandbox:
				Bandbox bandbox = (Bandbox) component;
				if (isClearComponents) {
					bandbox.setValue("");
					setBandBoxValue(bandbox, "");
				} else {
					bandbox.setValue(reportSearchTemplate.getFieldValue());
					setBandBoxValue(bandbox, reportSearchTemplate.getFieldValue());
				}

				break;
			default:
				break;
			}
			filedId = null;
		}
	}

	/**
	 * This method sets Filter value
	 * 
	 * @param aReportFieldsDetails
	 * @param reportSearchTemplate
	 */
	private void setFilterValue(long fieldId, String filter) {
		Listbox sortOperatorList;
		sortOperatorList = (Listbox) dymanicFieldsRows.getFellowIfAny("sortOperator_" + fieldId);
		for (int j = 0; j < sortOperatorList.getChildren().size(); j++) {
			Listitem listItem = (Listitem) sortOperatorList.getChildren().get(j);
			Listcell lc = (Listcell) listItem.getChildren().get(0);
			if (lc.getLabel().equals(filter)) {
				sortOperatorList.setSelectedItem(listItem);
			}
		}
	}

	/**
	 * This method sets Multiple selection Band box values
	 * 
	 * @param bandBox
	 */
	private void setBandBoxValue(Bandbox bandBox, String filedValue) {
		logger.debug("Entering");
		String values[] = filedValue.split(",");
		Bandpopup bandPopUp = (Bandpopup) bandBox.getChildren().get(0);
		Listbox listBox = (Listbox) bandPopUp.getChildren().get(0);
		// set Selected listCells
		for (int i = 0; i < listBox.getChildren().size(); i++) {
			Listitem listItem = (Listitem) listBox.getChildren().get(i);
			Listcell listCell = (Listcell) listItem.getChildren().get(0);
			Checkbox checkBox = (Checkbox) listCell.getChildren().get(0);
			for (int j = 0; j < values.length; j++) {
				boolean isChecked = StringUtils.isEmpty(filedValue) ? false
						: checkBox.getValue().toString().trim().equals(values[j].trim());
				checkBox.setChecked(isChecked);
				if (isChecked) {
					break;
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * set LovSearch Values against Template
	 * 
	 * @param bandBox
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setLovSearchValue(ReportFilterFields aReportFilterField, String filedValue, Textbox displaytextBox)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		logger.debug("Entering");
		String values[] = filedValue.split(",");
		String fieldDbName = aReportFilterField.getLovHiddenFieldMethod().replace("get", "");
		// Get hidden filed id by removing "get"
		Map<String, Object> lovSearchMap = new HashMap<String, Object>(1);
		// Create jdbc search object and prepare where clause as fieldDbName in('values[0]','values[1]')
		JdbcSearchObject<Object> jdbcSearchObject = new JdbcSearchObject(
				ModuleUtil.getModuleClass(aReportFilterField.getModuleName()));
		jdbcSearchObject.addWhereClause(" " + fieldDbName + " in " + getINCondition(filedValue));
		final SearchResult searchResult = getPagedListWrapper().getPagedListService()
				.getSRBySearchObject(jdbcSearchObject);

		StringBuilder lovDisplayValue = new StringBuilder();
		if (searchResult != null) {
			for (int i = 0; i < searchResult.getResult().size(); i++) {
				Object resultantObject = searchResult.getResult().get(i);
				lovDisplayValue.append(resultantObject.getClass().getMethod(aReportFilterField.getLovTextFieldMethod())
						.invoke(resultantObject).toString());
				if (i != searchResult.getResult().size() - 1) {
					lovDisplayValue.append(",");
				}
				for (int j = 0; j < values.length; j++) {
					if (values[j]
							.equals(resultantObject.getClass().getMethod(aReportFilterField.getLovHiddenFieldMethod())
									.invoke(resultantObject).toString())) {
						lovSearchMap.put(values[j], resultantObject);
					}
				}
				logger.debug("Leaving");
			}
		}
		displaytextBox.setValue(lovDisplayValue.toString());
		lovSearchBufferMap.put(String.valueOf(aReportFilterField.getFieldID()), lovSearchMap);
	}

	/**
	 * When user Clicks on "Search"
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onClick$btnSearch(Event event) {
		logger.info(Literal.ENTERING);
		searchClick = true;

		// ++ create the searchObject and initialize sorting ++//
		Map<String, Object> argMap = null;
		if (StringUtils.equals(reportMenuCode, "menu_Item_AccountStmt")) {
			String fromDate = null;
			String toDate = null;
			List<ReportSearchTemplate> filters = (List<ReportSearchTemplate>) doPrepareWhereConditionOrTemplate(false,
					false);
			if (filters != null && filters.size() >= 2) {
				String[] fromDateArray = filters.get(1).getFieldValue().split("&");
				fromDate = DateUtil.format(DateUtil.getDate(fromDateArray[0]), PennantConstants.DBDateFormat);
				toDate = DateUtil.format(DateUtil.getDate(fromDateArray[1]), PennantConstants.DBDateFormat);
			}

			StringBuilder whereCond1 = (StringBuilder) doPrepareWhereConditionOrTemplate(true, true);
			StringBuilder whereCond2 = (StringBuilder) doPrepareWhereConditionOrTemplate(true, false);
			/*
			 * StringBuilder whereCond3 = (StringBuilder) doPrepareWhereConditionOrTemplate(false, true); StringBuilder
			 * whereCond4 = (StringBuilder) doPrepareWhereConditionOrTemplate(true, false);
			 */

			doShowReport("where".equals(whereCond1.toString().trim()) ? "" : whereCond1.toString(),
					"where".equals(whereCond2.toString().trim()) ? "" : whereCond2.toString(), fromDate, toDate, null);

		} else if (StringUtils.equals(reportMenuCode, "menu_Item_DelinquencyVariance")) {
			String fromDate = null;
			String toDate = null;
			List<ReportSearchTemplate> filters = (List<ReportSearchTemplate>) doPrepareWhereConditionOrTemplate(false,
					false);
			if (filters != null && filters.size() >= 2) {
				fromDate = filters.get(0).getFieldValue();
				toDate = filters.get(1).getFieldValue();
			}

			StringBuilder whereCondition = (StringBuilder) doPrepareWhereConditionOrTemplate(true, true);
			doShowReport("where".equals(whereCondition.toString().trim()) ? "" : whereCondition.toString(), null,
					fromDate, toDate, null);
		} else if (StringUtils.equals(reportMenuCode, "menu_Item_LimitReports")) {
			String limitType = null;
			// SimpleDateFormat format=new SimpleDateFormat()""
			// Date currentDate= new Date(System.currentTimeMillis());
			StringBuilder whereCondition = new StringBuilder();
			List<ReportSearchTemplate> filters = (List<ReportSearchTemplate>) doPrepareWhereConditionOrTemplate(false,
					false);
			if (filters != null && filters.size() > 0) {
				limitType = filters.get(0).getFieldValue();
				if ("Expired Limits".equals(limitType)) {
					whereCondition.append(" Where T2.ExpiryDate < GetDate()  ");
				} else if ("Excess Limits".equals(limitType)) {
					whereCondition.append(" Where ( CalculatedLimit-UtilisedLimit ) < 0 ");
				}

			}

			// StringBuilder whereCondition = (StringBuilder) doPrepareWhereConditionOrTemplate(true, true);
			doShowReport("where".equals(whereCondition.toString().trim()) ? "" : whereCondition.toString(), null, null,
					null, null);
		} else if (StringUtils.equals(reportMenuCode, "menu_Item_FeeAmzReferenceReport")
				|| StringUtils.equals(reportMenuCode, "menu_Item_FeeAmzLoanTypeReport")) {

			StringBuilder whereCond1 = (StringBuilder) doPrepareWhereConditionOrTemplate(true, false);
			StringBuilder whereCond2 = (StringBuilder) doPrepareWhereConditionOrTemplate(true, false);
			StringBuilder whereCond = (StringBuilder) doPrepareWhereConditionOrTemplate(true, false);

			ReportFilterFields rff = reportConfiguration.getListReportFieldsDetails().stream()
					.filter(e -> e.getFieldType().equals("DATERANGE")).findAny().orElse(null);

			if ((rff != null && rff.getFieldType().equals(FIELDTYPE.DATERANGE.toString()))) {
				Component component = dymanicFieldsRows.getFellow(Long.toString(rff.getFieldID()));
				Date value = ((Datebox) component).getValue();

				whereCond = getWhereClauseForAMZ(whereCond, DateUtil.addMonths(value, -2), value);
				whereCond1 = getWhereClauseForAMZ(whereCond1, DateUtil.addMonths(value, -1), value);
				whereCond2 = getWhereClauseForAMZ(whereCond2, value, DateUtil.addMonths(value, 15));

			}
			doShowReport("where".equals(whereCond.toString().trim()) ? "" : whereCond.toString(),
					"where".equals(whereCond2.toString().trim()) ? "" : whereCond2.toString(), null, null,
					"where".equals(whereCond1.toString().trim()) ? "" : whereCond1.toString());

		} else if (StringUtils.equals(reportMenuCode, "menu_Item_ForeclosureTerminationReport")) {
			String finReference = null;
			String appPercentage = null;
			List<ReportSearchTemplate> filters = (List<ReportSearchTemplate>) doPrepareWhereConditionOrTemplate(false,
					false);
			if (filters != null && filters.size() >= 2) {
				finReference = filters.get(0).getFieldValue();
				appPercentage = filters.get(1).getFieldValue();
			}

			StringBuilder whereCondition = (StringBuilder) doPrepareWhereConditionOrTemplate(true, true);
			String whereCond = whereCondition.toString();

			// Removing appPercentage in where condition
			if ((finReference != null && !finReference.isEmpty()) && whereCond.contains("and")) {
				whereCond = whereCond.substring(0, whereCond.lastIndexOf("and"));
			} else {
				whereCond = "";
			}

			argMap = new HashMap<String, Object>(1);
			if (appPercentage != null) {
				argMap.put("appPercentage", "" + appPercentage);
			}

			doShowReport("where".equals(whereCond.trim()) ? "" : whereCond, null, null, null, argMap.toString());

		} else if (StringUtils.equals(reportMenuCode, "menu_Item_GST_InvoiceReport")
				|| StringUtils.equals(reportMenuCode, "menu_Item_ProcFees_InvoiceReport")) {
			String custCif = null;
			String finReference = null;
			String fromDate = null;
			String toDate = null;
			String invoiceType = null;
			List<ReportSearchTemplate> filters = (List<ReportSearchTemplate>) doPrepareWhereConditionOrTemplate(false,
					false);
			boolean invoiceExist = false;
			if (filters != null && filters.size() >= 2) {
				for (ReportSearchTemplate template : filters) {
					if (template.getFieldID() == 2 || template.getFieldID() == 3 || template.getFieldID() == 4) { // CustCif
						custCif = template.getFieldValue();
					} else if (template.getFieldID() == 5) { // Fin Reference
						finReference = template.getFieldValue();
					} else if (template.getFieldID() == 6) { // Invoice Number
						invoiceExist = true;
						// break;
					} else if (template.getFieldID() == 7) { // Dates
						String[] fromDateArray = template.getFieldValue().split("&");
						fromDate = DateUtil.format(DateUtil.getDate(fromDateArray[0]),
								PennantConstants.DBDateFormat);
						toDate = DateUtil.format(DateUtil.getDate(fromDateArray[1]),
								PennantConstants.DBDateFormat);
						if (fromDate != null && toDate != null) {
							boolean validateInput = validateGstInvoiceReportInputs(fromDate, toDate);
							if (!validateInput) {
								return;
							}
						}

					} else if (template.getFieldID() == 8) {
						// Invoice Type
						if (StringUtils.isNotBlank(template.getFieldValue())) {
							if (StringUtils.equals(Labels.getLabel("Invoice_Type_Credit"), template.getFieldValue())) {
								invoiceType = PennantConstants.GST_INVOICE_TRANSACTION_TYPE_CREDIT;
							} else if (StringUtils.equals(Labels.getLabel("Invoice_Type_Debit"),
									template.getFieldValue())) {
								invoiceType = PennantConstants.GST_INVOICE_TRANSACTION_TYPE_DEBIT;
							} else if (StringUtils.equals(Labels.getLabel("Invoice_Type_Exempted"),
									template.getFieldValue())) {
								invoiceType = PennantConstants.GST_INVOICE_TRANSACTION_TYPE_EXEMPTED;
							} else if (StringUtils.equals(Labels.getLabel("Invoice_Type_Exempted_Credit"),
									template.getFieldValue())) {
								invoiceType = PennantConstants.GST_INVOICE_TRANSACTION_TYPE_EXEMPTED_TAX_CREDIT;
							}
						}

					}
				}
			}

			StringBuilder whereCondition = (StringBuilder) doPrepareWhereConditionOrTemplate(true, false);

			// check if invoice number is existed or not
			boolean invoiceNoExist = getReportConfigurationService().isGstInvoiceExist(custCif, finReference,
					invoiceType, DateUtil.parseFullDate(fromDate), DateUtil.parseFullDate(toDate));

			if (invoiceNoExist) {
				doShowReport("where".equals(whereCondition.toString().trim()) ? "" : whereCondition.toString(), null,
						null, null, null);
			} else {
				if (invoiceExist) {
					MessageUtil.showMessage(Labels.getLabel("info.invoice_cust_not_invoice")); // TODO validate message
					return;
				} else {
					MessageUtil.showMessage(Labels.getLabel("info.invoice_not_generate"));
					return;
				}
			}
		} else if (StringUtils.equals(reportMenuCode, "menu_Item_BillingReport")
				|| StringUtils.equals(reportMenuCode, "menu_Item_ACHPResentations")
				|| StringUtils.equals(reportMenuCode, "menu_Item_PDCPResentation")
				|| StringUtils.equals(reportMenuCode, "menu_Item_ODReport")
				|| StringUtils.equals(reportMenuCode, "menu_Item_LoanRegisterReport")
				|| StringUtils.equals(reportMenuCode, "menu_Item_CashflowReport")) {
			String userDate = null;

			List<ReportSearchTemplate> filters = (List<ReportSearchTemplate>) doPrepareWhereConditionOrTemplate(false,
					false);
			if (!StringUtils.equals(reportMenuCode, "menu_Item_CashflowReport")
					&& !StringUtils.equals(reportMenuCode, "menu_Item_AdvanceReport") && filters != null
					&& filters.size() == 2) {
				userDate = filters.get(1).getFieldValue();
			} else if (filters != null && filters.size() == 1) {
				userDate = filters.get(0).getFieldValue();
			}

			StringBuilder whereCondition = (StringBuilder) doPrepareWhereConditionOrTemplate(true, false);
			doShowReport("where".equals(whereCondition.toString().trim()) ? "" : whereCondition.toString(), null,
					userDate, null, null);
		} else if (StringUtils.equals(reportMenuCode, "menu_Item_NoObjectionCertificate")) {
			StringBuilder whereCondition = (StringBuilder) doPrepareWhereConditionOrTemplate(true, false);

			if (ImplementationConstants.NOC_LINKED_LOANS_CHECK_REQ && whereCondition != null) {
				String finReference = whereCondition.substring(24, whereCondition.length() - 1);
				if (collateralAssignmentDAO.getAssignedCollateralCountByRef(finReference) > 0) {
					String msg = Labels.getLabel("label_collateralAssignment_Error");
					if (MessageUtil.confirm(msg) == MessageUtil.NO) {
						return;
					}
				}
			}

			List<ReportSearchTemplate> filters = (List<ReportSearchTemplate>) doPrepareWhereConditionOrTemplate(false,
					false);
			if (!filters.isEmpty()) {
				String finref = ((ReportSearchTemplate) filters.get(0)).getFieldValue();
				processLinkedLoans(finref);
			}

			doShowReport("where".equals(whereCondition.toString().trim()) ? "" : whereCondition.toString(), null, null,
					null, null);
		} else if (StringUtils.equals(reportMenuCode, "menu_Item_StatutoryAudit_Principal")
				|| StringUtils.equals(reportMenuCode, "menu_Item_Future_CashFlowReport")
				|| StringUtils.equals(reportMenuCode, "menu_Item_PartywiseInterestDetails_GSTreport")
				|| StringUtils.equals(reportMenuCode, "menu_Item_StatAuditInterest_Accrual")
				|| StringUtils.equals(reportMenuCode, "menu_Item_StatutoryAudit_Interest")
				|| StringUtils.equals(reportMenuCode, "menu_Item_InterestRateModification")
				|| StringUtils.equals(reportMenuCode, "menu_Item_CorporateStatAuditReport")) {

			String fromDate = null;
			String toDate = null;

			List<ReportSearchTemplate> filters = (List<ReportSearchTemplate>) doPrepareWhereConditionOrTemplate(false,
					false);
			fromDate = ((ReportSearchTemplate) filters.get(0)).getFieldValue();
			if (!StringUtils.equals(reportMenuCode, "menu_Item_Future_CashFlowReport")) {
				toDate = ((ReportSearchTemplate) filters.get(1)).getFieldValue();
			}
			doShowReport("", null, fromDate, toDate, null);
		} else if (StringUtils.equals(reportMenuCode, "menu_Item_StatutoryAudit_Principal")
				|| StringUtils.equals(reportMenuCode, "menu_Item_Future_CashFlowReport")
				|| StringUtils.equals(reportMenuCode, "menu_Item_PartywiseInterestDetails_GSTreport")
				|| StringUtils.equals(reportMenuCode, "menu_Item_StatAuditInterest_Accrual")
				|| StringUtils.equals(reportMenuCode, "menu_Item_StatutoryAudit_Interest")
				|| StringUtils.equals(reportMenuCode, "menu_Item_InterestRateModification")
				|| StringUtils.equals(reportMenuCode, "menu_Item_CorporateStatAuditReport")) {
			String fromDate = null;
			String toDate = null;

			List<ReportSearchTemplate> filters = (List<ReportSearchTemplate>) doPrepareWhereConditionOrTemplate(false,
					false);
			fromDate = ((ReportSearchTemplate) filters.get(0)).getFieldValue();
			if (!StringUtils.equals(reportMenuCode, "menu_Item_Future_CashFlowReport")) {
				toDate = ((ReportSearchTemplate) filters.get(1)).getFieldValue();
			}
			doShowReport("", null, fromDate, toDate, null);
		} else if (StringUtils.equals(reportMenuCode, "menu_Item_LoanDetailLMSReport")
				|| StringUtils.equals(reportMenuCode, "menu_Item_OverdueAgeingReport")) {
			String toDate = null;
			List<ReportSearchTemplate> filters = (List<ReportSearchTemplate>) doPrepareWhereConditionOrTemplate(false,
					false);
			toDate = ((ReportSearchTemplate) filters.get(0)).getFieldValue();
			doShowReport("", null, null, toDate, null);
		} else if (StringUtils.equals(reportMenuCode, "menu_Item_LoanDetailsMISReport")
				|| StringUtils.equals(reportMenuCode, "menu_Item_CME_ExposureReport")
				|| StringUtils.equals(reportMenuCode, "menu_Item_Revised_LimitReport")) {
			List<ReportSearchTemplate> filters = (List<ReportSearchTemplate>) doPrepareWhereConditionOrTemplate(false,
					false);
			StringBuilder whereCondition = (StringBuilder) doPrepareWhereConditionOrTemplate(true, true);
			String toDate = "";
			if (StringUtils.equals(reportMenuCode, "menu_Item_Revised_LimitReport")) {
				for (ReportSearchTemplate filter : filters) {
					if (filter.getFieldID() == 2) {
						toDate = ((ReportSearchTemplate) filter).getFieldValue();
					}
				}
			} else {
				toDate = ((ReportSearchTemplate) filters.get(1)).getFieldValue();
			}
			doShowReport("where".equals(whereCondition.toString().trim()) ? "" : whereCondition.toString(), null, null,
					toDate, null);
		} else if (StringUtils.equals(reportMenuCode, "menu_Item_DailyBalanceReport")) {
			List<ReportSearchTemplate> filters = (List<ReportSearchTemplate>) doPrepareWhereConditionOrTemplate(false,
					false);
			StringBuilder whereCondition = (StringBuilder) doPrepareWhereConditionOrTemplate(true, true);
			String fromDate = "";
			String toDate = "";
			fromDate = ((ReportSearchTemplate) filters.get(0)).getFieldValue();
			toDate = ((ReportSearchTemplate) filters.get(1)).getFieldValue();
			doShowReport("where".equals(whereCondition.toString().trim()) ? "" : whereCondition.toString(), null,
					fromDate, toDate, null);
		} else if (StringUtils.equals(reportMenuCode, "menu_Item_WriteoffReport")) {
			StringBuilder whereCondition = (StringBuilder) doPrepareWhereConditionOrTemplate(true, false);
			if ("where".equals(whereCondition.toString().trim())) {
				whereCondition.append(" FM.WRITEOFFLOAN = 1");
			} else {
				whereCondition.append(" and FM.WRITEOFFLOAN = 1");
			}
			doShowReport("where".equals(whereCondition.toString().trim()) ? "" : whereCondition.toString(), null, null,
					null, null);
		} else if (StringUtils.equals(reportMenuCode, "menu_Item_PresentmentExcludeReport")) {
			StringBuilder whereCondition = (StringBuilder) doPrepareWhereConditionOrTemplate(true, false);

			whereCondition.append(" and T.excludereason != 0");

			doShowReport("where".equals(whereCondition.toString().trim()) ? "" : whereCondition.toString(), null, null,
					null, null);
		}
		// #PSD:152141 UAT2: Users:Report: Indaas accounting report not available -START
		else if (StringUtils.equals(reportMenuCode, "menu_Item_AmortizationReport")) {
			String fromDate = null;

			List<ReportSearchTemplate> filters = (List<ReportSearchTemplate>) doPrepareWhereConditionOrTemplate(false,
					false);
			if (filters != null) {
				fromDate = ((ReportSearchTemplate) filters.get(0)).getFieldValue();
			}
			StringBuilder whereCondition = (StringBuilder) doPrepareWhereConditionOrTemplate(true, false);
			doShowReport("where".equals(whereCondition.toString().trim()) ? "" : whereCondition.toString(), null,
					fromDate, null, null);
		} else if ("menu_Item_FeeReport".equals(reportMenuCode)
				|| "menu_Item_LoanClosureReport".equals(reportMenuCode)) {
			StringBuilder whereCondition = (StringBuilder) doPrepareWhereConditionOrTemplate(true, false);
			List<ReportSearchTemplate> filters = (List<ReportSearchTemplate>) doPrepareWhereConditionOrTemplate(false,
					false);
			String[] dateslist = ((ReportSearchTemplate) filters.get(0)).getFieldValue().split("&");
			String fromDate = dateslist[0];
			String toDate = dateslist[1];
			doShowReport("where".equals(whereCondition.toString().trim()) ? "" : whereCondition.toString(), null,
					fromDate, toDate, null);
		} else if ("menu_Item_LoanDisbursementBasicListing".equals(reportMenuCode)) {
			StringBuilder whereCondition = (StringBuilder) doPrepareWhereConditionOrTemplate(true, false);
			List<ReportSearchTemplate> filters = (List<ReportSearchTemplate>) doPrepareWhereConditionOrTemplate(false,
					false);
			String fromDate = "";
			String toDate = "";
			for (ReportSearchTemplate filter : filters) {
				if (filter.getFieldID() == 4) {
					String[] dateslist = ((ReportSearchTemplate) filter).getFieldValue().split("&");
					fromDate = dateslist[0];
					toDate = dateslist[1];
				}
			}
			doShowReport("where".equals(whereCondition.toString().trim()) ? "" : whereCondition.toString(), null,
					fromDate, toDate, null);
		} else {
			StringBuilder whereCondition = (StringBuilder) doPrepareWhereConditionOrTemplate(true, false);
			doShowReport("where".equals(whereCondition.toString().trim()) ? "" : whereCondition.toString(), null, null,
					null, null);
		}

		logger.info(Literal.LEAVING);
	}

	private void processLinkedLoans(String finReference) {
		List<LinkedFinances> linkedFinances = linkedFinancesService.getLinkedFinancesByFinRef(finReference, "_AView");
		List<LinkedFinances> linkedFinances2 = linkedFinancesService.getLinkedFinancesByRef(finReference, "_AView");

		if (CollectionUtils.isNotEmpty(linkedFinances) || CollectionUtils.isNotEmpty(linkedFinances2)) {
			String[] parameters = new String[2];
			parameters[0] = finReference;
			StringBuilder ref = new StringBuilder("");
			for (LinkedFinances LinkedFinance : linkedFinances) {

				if (ref.length() > 0) {
					ref.append(", ");
				}

				ref.append(LinkedFinance.getFinReference());

			}
			for (LinkedFinances LinkedFinance : linkedFinances2) {

				if (ref.length() > 0) {
					ref.append(", ");
				}

				ref.append(LinkedFinance.getLinkedReference());

			}
			parameters[1] = ref.toString();
			if (MessageUtil.confirm(
					parameters[0] + " is Linked with " + parameters[1]
							+ " . Please Delink the loan first then Proceed ",
					MessageUtil.CANCEL | MessageUtil.OVERIDE) == MessageUtil.CANCEL) {
				ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
				wve.add(new WrongValueException("Please Delink the loan first then Proceed "));
				showErrorDetails(wve);
			}
		}
	}

	private StringBuilder getWhereClauseForAMZ(StringBuilder whereCond, Date fromDate, Date toDate) {
		String where = whereCond.toString();

		if (!where.contains("MONTHENDDATE")) {
			return whereCond;
		}

		where = where.replace(where.split("and")[2], "");

		StringBuilder whereCondition = new StringBuilder(where);
		whereCondition.append(" MONTHENDDATE >= '");
		whereCondition.append(DateUtil.format(fromDate, "yyyy-MM-dd")).append("'");
		whereCondition.append(" and MONTHENDDATE <= '");
		whereCondition.append(DateUtil.format(toDate, "yyyy-MM-dd")).append("'");

		return whereCondition;
	}

	/**
	 * When user Clicks on "Search"
	 * 
	 * @param event
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void onClick$btnClear(Event event)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		logger.debug("Entering" + event.toString());
		doClearComponents();
		renderMap.clear();
		valueMap.clear();
		valueLabelMap.clear();
		this.cbSelectTemplate.setValue(Labels.getLabel("Combo.Select"));
		this.btnDeleteTemplate.setDisabled(true);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(false);
	}

	protected void doPostClose() {
		if (tabbox != null) {
			tabbox.getSelectedTab().close();
		}
	}

	/**
	 * 
	 * On multiple Selection List box item selected
	 * 
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public final class onMultiSelectionItemSelected implements EventListener {

		public onMultiSelectionItemSelected() {

		}

		@Override
		public void onEvent(Event event) {
			Checkbox checkbox = (Checkbox) event.getTarget();
			Listitem listItem = (Listitem) checkbox.getParent().getParent();
			Bandbox bandBox = (Bandbox) listItem.getParent().getParent().getParent();
			bandBox.setErrorMessage("");
			String displayString = "";
			Map<String, String> valuesMap = new LinkedHashMap<String, String>();
			String[] bandBoxValues = bandBox.getValue().split(",");
			for (int i = 0; i < bandBoxValues.length; i++) {
				valuesMap.put(bandBoxValues[i], bandBoxValues[i]);
			}
			if (checkbox.isChecked()) {
				valuesMap.put(checkbox.getValue().toString(), checkbox.getValue().toString());
			} else {
				valuesMap.remove(checkbox.getValue().toString());
			}
			for (String values : valuesMap.keySet()) {
				displayString = displayString.concat(("").equals(bandBox.getValue().trim()) ? values : values + ",");
			}

			// Excluding Last added Comma
			if (StringUtils.trimToEmpty(displayString).endsWith(",")) {
				displayString = displayString.substring(0, displayString.length() - 1);
			}

			bandBox.setValue(displayString);
			bandBox.setTooltiptext(displayString);
			valuesMap = null;
		}
	}

	public void onComboFieldSelected(Event event) {
		logger.debug("Entering" + event.toString());

		Component component = (Component) event.getData();
		String value = ((Combobox) component).getSelectedItem().getValue().toString();
		ReportFilterFields aReportFieldsDetails = reportConfiguration.getListReportFieldsDetails().get(0);
		valueLabelMap.put(aReportFieldsDetails.getFieldDBName(), value);

		if (StringUtils.equals(reportMenuCode, "menu_Item_GST_InvoiceReport")
				|| StringUtils.equals(reportMenuCode, "menu_Item_ProcFees_InvoiceReport")) {

			if (aReportFieldsDetails.getFieldID() != Long.valueOf(component.getId())) {
				return;
			}

			Row customerRow = (Row) dymanicFieldsRows.getFellow("row_GSTInv_2");
			Row maniufacturerRow = (Row) dymanicFieldsRows.getFellow("row_GSTInv_3");
			Row dealerRow = (Row) dymanicFieldsRows.getFellow("row_GSTInv_4");
			if (StringUtils.equals(value, "D")) {
				customerRow.setVisible(false);
				maniufacturerRow.setVisible(false);
				dealerRow.setVisible(true);
			} else if (StringUtils.equals(value, "M")) {
				customerRow.setVisible(false);
				maniufacturerRow.setVisible(true);
				dealerRow.setVisible(false);
			} else {
				customerRow.setVisible(true);
				maniufacturerRow.setVisible(false);
				dealerRow.setVisible(false);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On LovSearch Button Clicked
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onLovButtonClicked(Event event) {
		logger.debug("Entering" + event.toString());

		CustomArgument customArgument = (CustomArgument) event.getData();
		ReportFilterFields aReportFieldsDetails = customArgument.getaReportFieldsDetails();

		Hbox hbox = customArgument.hbox;
		Textbox valuestextBox = (Textbox) hbox.getChildren().get(1);
		Textbox labelstextBox = (Textbox) hbox.getChildren().get(2);
		Button button = (Button) hbox.getChildren().get(3);

		Filter[] filters = null;
		try {
			filters = doLovFilter(aReportFieldsDetails);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showError(e.getMessage());
			return;
		}

		try {
			// If multiple search
			if (aReportFieldsDetails.isMultiSelectSearch()) {
				Map<String, Object> lovSearchMap = new HashMap<String, Object>(1);

				Map<String, Object> filterMap = (Map<String, Object>) lovSearchBufferMap.get(valuestextBox.getId());
				if (reportMenuCode.equals("menu_Item_SubventionAmortReport")
						|| reportMenuCode.equals("menu_Item_SubventionMISReport")) {
					lovSearchMap = (Map<String, Object>) ExtendedMultipleSearchListBox.show(
							this.window_ReportPromptFilterCtrl, button.getId(),
							filterMap == null ? new HashMap<String, Object>() : filterMap);
				} else {
					lovSearchMap = (Map<String, Object>) ExtendedMultipleSearchListBox.show(
							this.window_ReportPromptFilterCtrl, button.getId(),
							filterMap == null ? new HashMap<String, Object>() : filterMap);
				}

				// Put in map for select next time
				lovSearchBufferMap.put(valuestextBox.getId(), lovSearchMap);

				if (lovSearchMap != null) {
					String codes = "";
					String descs = "";
					Set<String> suCodes = lovSearchMap.keySet();
					Iterator<String> itr = suCodes.iterator();

					while (itr.hasNext()) {
						String str = itr.next();

						if (StringUtils.equals(str, "SELECTALL")) {
							codes = "SELECTALL" + ",";
							descs = "Select All" + ",";
						} else {
							if (lovSearchMap.get(str) != null) {
								// get Label and Value by reflection methods
								codes = codes + lovSearchMap.get(str).getClass()
										.getMethod(aReportFieldsDetails.getLovHiddenFieldMethod())
										.invoke(lovSearchMap.get(str)) + ",";
								descs = descs + lovSearchMap.get(str).getClass()
										.getMethod(aReportFieldsDetails.getLovTextFieldMethod())
										.invoke(lovSearchMap.get(str)) + ",";
							}
						}
					}
					valuestextBox
							.setValue(StringUtils.isNotEmpty(codes) ? codes.substring(0, codes.length() - 1) : codes);
					labelstextBox
							.setValue(StringUtils.isNotEmpty(descs) ? descs.substring(0, descs.length() - 1) : descs);
					labelstextBox.setTooltiptext(
							StringUtils.isNotEmpty(descs) ? descs.substring(0, descs.length() - 1) : descs);
				} else {
					valuestextBox.setValue("");
					labelstextBox.setValue("");
				}
				lovSearchBufferMap.put(String.valueOf(aReportFieldsDetails.getFieldID()), lovSearchMap);
			} else {
				String searchValue = null;
				if ("FinanceMain".equals(aReportFieldsDetails.getModuleName())) {
					searchValue = getUsrFinAuthenticationQry(false);
				} else if (getParentFlag() != null && aReportFieldsDetails.getFilterFileds() != null) {
					valuestextBox.setValue("");
				}
				Object dataObject = ExtendedSearchListBox.show(this.window_ReportPromptFilterCtrl, button.getId(),
						valuestextBox.getValue(), filters, searchValue);

				if (dataObject instanceof String) {
					valuestextBox.setValue(dataObject.toString());
					labelstextBox.setValue("");
					doClearFields(aReportFieldsDetails);
				} else {
					Object details = dataObject;

					if (details != null) {
						String tempValuestextBox = valuestextBox.getValue();
						valuestextBox.setValue(details.getClass()
								.getMethod(aReportFieldsDetails.getLovHiddenFieldMethod()).invoke(details).toString());

						if (!tempValuestextBox.equals("")) {
							setParentFlag(aReportFieldsDetails.getFieldDBName());
						}
						String label = details.getClass().getMethod(aReportFieldsDetails.getLovTextFieldMethod())
								.invoke(details).toString();
						labelstextBox.setValue(label);
						labelstextBox.setTooltiptext(label);
					}
				}

				if (this.isEntity && StringUtils.equalsIgnoreCase("EntityCode", aReportFieldsDetails.getFieldDBName())
						&& StringUtils.equalsIgnoreCase("Entity", aReportFieldsDetails.getModuleName())) {
					this.entityValue = valuestextBox.getValue();
				}

				valueMap.put(aReportFieldsDetails.getFieldDBName(), valuestextBox.getValue());
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showError(Labels.getLabel("label_ReportConfiguredError.error"));
		}

		logger.debug("Leaving" + event.toString());
	}

	private Filter[] doLovFilter(ReportFilterFields aReportFieldsDetails) {
		logger.debug("Entering");

		Filter[] filters = null;
		StringBuilder message = new StringBuilder();
		renderMap.putAll(valueMap);
		renderMap.putAll(valueLabelMap);
		if (StringUtils.trimToNull(aReportFieldsDetails.getFilterFileds()) != null) {
			String[] filterFields = StringUtils.split(aReportFieldsDetails.getFilterFileds(), "|");
			filters = new Filter[filterFields.length];
			for (int i = 0; i < filterFields.length; i++) {
				String[] fieldStr = StringUtils.split(filterFields[i], "@");
				Object valueObject = renderMap.get(fieldStr[0]);

				if (valueObject == null || valueObject.equals("") || valueObject.equals("#")) {
					message.append("\n- ");
					message.append(fieldStr[0]);
				}
				if (valueObject != null) {
					if (valueObject instanceof String) {
						filters[i] = new Filter(fieldStr[1], valueObject.toString());
					}
				}
			}
		}

		if (StringUtils.trimToNull(message.toString()) != null) {
			throw new AppException(message.insert(0, "Please select the below fields:").toString());
		}

		if (StringUtils.equals(reportMenuCode, "menu_Item_NoObjectionCertificate")) {
			Filter[] nocFilter = new Filter[1];
			nocFilter[0] = Filter.in("CLOSINGSTATUS", FinanceConstants.CLOSE_STATUS_MATURED,
					FinanceConstants.CLOSE_STATUS_EARLYSETTLE);
			filters = nocFilter;
		} else if (StringUtils.equals(reportMenuCode, "menu_Item_WriteoffReport")) {
			Filter[] writeOffFilter = new Filter[1];
			writeOffFilter[0] = Filter.equalTo("WRITEOFFLOAN", 1);
			filters = writeOffFilter;
		} else if (StringUtils.equals(reportMenuCode, "menu_Item_SubventionAmortReport")
				|| StringUtils.equals(reportMenuCode, "menu_Item_SubventionMISReport")) {
			Filter[] subFilter = new Filter[1];
			subFilter[0] = Filter.greaterThan("manufacturerdealerid", 0);
			filters = subFilter;
		} else if ("menu_Item_FATDSReport".equals(reportMenuCode)) {
			if ("AccountMapping".equals(aReportFieldsDetails.getModuleName())) {
				Filter[] subFilter = new Filter[1];
				subFilter[0] = Filter.like("AccountType", "%TDS%");
				filters = subFilter;
			}
			if ("AccountType".equals(aReportFieldsDetails.getModuleName())) {
				Filter[] subFilter = new Filter[1];
				subFilter[0] = Filter.like("AcType", "%TDS%");
				filters = subFilter;
			}
		}
		logger.debug("Leaving");
		return filters;
	}

	// HELPERS

	/**
	 * This method prepares Where condition For Selected items of list Box
	 * 
	 * @param multiSelectionListBox
	 * @param whereCondition
	 * @return
	 */
	private StringBuilder getWhereCondFromMSelectListBox(ReportFilterFields aReportFieldsDetails, Bandbox banBox,
			StringBuilder whereCondition) {
		logger.debug("Entering");
		// Forming and Condition like ' userEnable='1' and usrStaus='2''
		StringBuilder csvValues = new StringBuilder();
		Bandpopup banPopUp = (Bandpopup) banBox.getChildren().get(0);
		Listbox multiSelectionListBox = (Listbox) banPopUp.getChildren().get(0);
		Listitem li = new Listitem(); // To read List Item
		StringBuilder tempWhereCondition = new StringBuilder();
		for (int i = 0; i < multiSelectionListBox.getItems().size(); i++) {
			li = multiSelectionListBox.getItems().get(i);
			Listcell lc = (Listcell) li.getFirstChild();
			Checkbox checkBox = (Checkbox) lc.getFirstChild();
			if (checkBox.isChecked()) {
				csvValues.append(lc.getValue() + ",");
				if (aReportFieldsDetails.getFieldType().equals(FIELDTYPE.MULTISELANDLIST.toString())) {
					addAndCondition(whereCondition);
					addAndCondition(tempWhereCondition);
					whereCondition.append(lc.getId() + "='" + lc.getValue() + "'");
					tempWhereCondition.append(lc.getId() + "='" + lc.getLabel() + "'");
				}
			}

		}
		if (aReportFieldsDetails.getFieldType().equals(FIELDTYPE.MULTISELANDLIST.toString())) {
			searchCriteriaDesc
					.append(aReportFieldsDetails.getFieldLabel() + " where  " + tempWhereCondition.toString() + "\n");
		}

		// Forming In Condition RightType in ('0','1','2')
		if (aReportFieldsDetails.getFieldType().equals(FIELDTYPE.MULTISELINLIST.toString())
				&& !(csvValues.length() == 0)) {
			addAndCondition(whereCondition);
			String inCondition = getINCondition(csvValues.toString());
			String[] inCondSize = inCondition.split(",");
			whereCondition.append(aReportFieldsDetails.getFieldDBName() + " in " + inCondition);
			if (inCondSize.length > 1) {
				searchCriteriaDesc.append(aReportFieldsDetails.getFieldLabel() + " is in "
						+ StringUtils.substring(banBox.getValue(), 0, banBox.getValue().length() - 1) + "\n");
			} else {
				if (banBox.getValue().contains(",")) {
					searchCriteriaDesc.append(aReportFieldsDetails.getFieldLabel() + " is "
							+ StringUtils.substring(banBox.getValue(), 0, banBox.getValue().length() - 1) + "\n");
				} else {
					searchCriteriaDesc.append(aReportFieldsDetails.getFieldLabel() + " is "
							+ StringUtils.substring(banBox.getValue(), 0, banBox.getValue().length()) + "\n");
				}

			}
		}
		logger.debug("Leaving");
		return whereCondition;
	}

	/**
	 * This method prepares Where condition For Selected items of List Box
	 * 
	 * @param multiSelectionListBox
	 * @param WhereCondition
	 * @return
	 */
	private String getINCondition(String csvString) {
		logger.debug("Entering");
		String strTokens[] = csvString.split(",");
		StringBuilder inCondition = new StringBuilder("(");
		for (int i = 0; i < strTokens.length; i++) {
			inCondition.append("'" + strTokens[i] + "',");

		}
		inCondition.replace(inCondition.length() - 1, inCondition.length(), "");
		inCondition.append(")");
		logger.debug("Leaving");
		return inCondition.toString();
	}

	// Inner Class

	private class CustomArgument {
		private Hbox hbox;
		private ReportFilterFields aReportFieldsDetails;

		public ReportFilterFields getaReportFieldsDetails() {
			return aReportFieldsDetails;
		}

		public CustomArgument(Hbox hbox, ReportFilterFields aReportFieldsDetails) {
			this.hbox = hbox;
			this.aReportFieldsDetails = aReportFieldsDetails;
		}
	}

	/**
	 * Sets Red mark if filed is mandatory
	 * 
	 * @param space
	 * @param isManditory
	 */
	private void setSpaceStyle(Space space, boolean isManditory) {
		space.setWidth("2px");
		if (isManditory) {
			space.setSclass("mandatory");
		}
	}

	/**
	 * This method Fills the Search Template into comBo box
	 */
	private void doFillcbSelectTemplate() {
		this.cbSelectTemplate.getChildren().clear();

		templateLibraryMap = getReportConfigurationService().getTemplatesByReportID(reportConfiguration.getReportID(),
				getUserWorkspace().getLoggedInUser().getUserId());

		Comboitem comboitem = new Comboitem();
		comboitem.setValue(PennantConstants.List_Select);
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		this.cbSelectTemplate.appendChild(comboitem);
		for (Object templateName : templateLibraryMap.keySet()) {
			comboitem = new Comboitem();
			// comboitem.setId(templateName.toString());
			comboitem.setValue(templateName.toString());
			comboitem.setLabel(templateName.toString());
			this.cbSelectTemplate.appendChild(comboitem);
		}
		this.cbSelectTemplate.setValue(Labels.getLabel("Combo.Select"));
	}

	/**
	 * Clear all the components
	 * 
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private void doClearComponents() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		for (int i = 0; i < reportConfiguration.getListReportFieldsDetails().size(); i++) {
			ReportFilterFields aReportFieldsDetails = reportConfiguration.getListReportFieldsDetails().get(i);
			doSetValueOrClearOpertionOnFields(aReportFieldsDetails, null, true);
		}
	}

	/**
	 * Clear components
	 */
	private void doClearFields(ReportFilterFields aReportFieldsDetails) {
		logger.debug("Entering");
		long fieldID = aReportFieldsDetails.getFieldID();

		if (myOrderedLableMap.containsKey(fieldID) && myOrderedLableMap.get(fieldID).size() == 1
				&& myOrderedLableMap.get(fieldID).get(0) == null) {
			return;
		}

		mapRenderer: for (Map.Entry<Long, List<String>> entry : myOrderedLableMap.entrySet()) {
			Long key = entry.getKey();
			List<String> value = entry.getValue();
			if (fieldID < key) {
				// finding group for the clicked(clear button) one.
				long index = 0;

				if (aReportFieldsDetails.getFilterFileds() != null) { // checking whether clicked one is parent or child
					index = key - fieldID;
				}

				for (long i = index; i < value.size(); i++) { // clearing corresponding child in particular group.
					if (dymanicFieldsRows.getFellowIfAny(Long.toString(fieldID)) != null) {
						Component component = dymanicFieldsRows.getFellow(Long.toString(fieldID));
						if (component instanceof Textbox) {
							Textbox textbox = (Textbox) component;
							textbox.setValue("");
							if (component.getNextSibling() != null && component.getNextSibling() instanceof Textbox) {
								Textbox lovDisplayText = (Textbox) component.getNextSibling();
								lovDisplayText.setValue("");
							}
						}
					}
					fieldID++;
				}
				break mapRenderer;
			}
		}
		renderMap.clear();
		logger.debug("Entering");
	}

	private void showErrorDetails(ArrayList<WrongValueException> wve) {
		logger.debug("Entering");

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");

			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	/**
	 * This method shows Message box with error message
	 * 
	 * @param e
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering ");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_ReportPromptFilterCtrl, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving ");
	}

	private boolean validateGstInvoiceReportInputs(String fromDate, String toDate) {
		boolean isValidInput = true;
		Date fromDateValue = DateUtil.parse(fromDate, "yyyy-MM-dd");
		Date toDateValue = DateUtil.parse(toDate, "yyyy-MM-dd");

		long diffDays = DateUtil.getDaysBetween(fromDateValue, toDateValue);

		boolean toDateExceedFlag = false;

		if (DateUtil.compare(toDateValue, SysParamUtil.getAppDate()) > 0) {
			toDateExceedFlag = true;
		}
		if (diffDays > 31) {
			MessageUtil.showMessage(Labels.getLabel("info.invoice_toDate_fromDate_diff_days")); // TODO validate message
			isValidInput = false;
		} else if (toDateExceedFlag) {
			MessageUtil.showMessage(Labels.getLabel("info.invoice_toDate_exceed_businessDate") + " "
					+ SysParamUtil.getAppDate("dd/MM/yyy"));
			isValidInput = false;
		}
		return isValidInput;
	}

	// GETTERS AND SETTERS

	public void setReportConfigurationService(ReportConfigurationService reportConfigurationService) {
		this.reportConfigurationService = reportConfigurationService;
	}

	public ReportConfigurationService getReportConfigurationService() {
		return reportConfigurationService;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getParentFlag() {
		return parentFlag;
	}

	public void setParentFlag(String parentFlag) {
		this.parentFlag = parentFlag;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public CollateralAssignmentDAO getCollateralAssignmentDAO() {
		return collateralAssignmentDAO;
	}

	public void setCollateralAssignmentDAO(CollateralAssignmentDAO collateralAssignmentDAO) {
		this.collateralAssignmentDAO = collateralAssignmentDAO;
	}

	public void setLinkedFinancesService(LinkedFinancesService linkedFinancesService) {
		this.linkedFinancesService = linkedFinancesService;
	}
}