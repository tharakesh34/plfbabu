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
 * FileName    		:  ReportGenerationPromptDialogCtrl.java                               * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-09-2012   														*
 *                                                                  						*
 * Modified Date    :  23-09-2012      														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-09-2012         Pennant	                 0.1                                        * 
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
package com.pennant.webui.reports;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.zul.Filedownload;
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

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.reports.ReportConfiguration;
import com.pennant.backend.model.reports.ReportFilterFields;
import com.pennant.backend.model.reports.ReportSearchTemplate;
import com.pennant.backend.service.reports.ReportConfigurationService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.search.Filter;
import com.pennant.search.SearchResult;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.searchdialogs.ExtendedMultipleSearchListBox;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.App.Database;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

	/**
 * This is the controller class for the /WEB-INF/pages/reports/ReportGenerationPromptDialog.zul file.
 */
public class ReportGenerationPromptDialogCtrl extends GFCBaseCtrl<ReportConfiguration> {
	private static final long serialVersionUID = 4678287540046204660L;
	private final static Logger logger = Logger.getLogger(ReportGenerationPromptDialogCtrl.class);

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
	static final HashMap<String, String> filterDescMap = PennantStaticListUtil.getFilterDescription();

	private List<ReportSearchTemplate> reportSearchTemplateFieldsList;
	private ReportConfigurationService reportConfigurationService;
	private StringBuilder searchCriteriaDesc = new StringBuilder(" ");

	public enum FIELDCLASSTYPE {
		Textbox, Combobox, Datebox, Timebox, Intbox, Decimalbox, Bandbox, Checkbox, Radio
	};

	public enum FIELDTYPE {
		TXT, DATE, TIME, DATETIME, STATICLIST, DYNAMICLIST, LOVSEARCH, DECIMAL, INTRANGE, DECIMALRANGE, NUMBER, CHECKBOX, MULTISELANDLIST, MULTISELINLIST, DATERANGE, DATETIMERANGE, TIMERANGE, STATICVALUE
	};

	private boolean isExcel = false;
	private boolean searchClick = false;
	private static final String EXCEL_TYPE = "Excel:";
	private String unitName;
	Window dialogWindow = null;
	private Map<String, Object> valueMap = new HashMap<String, Object>();
	private Map<String, Object> valueLabelMap = new HashMap<String, Object>();
	private Map<String, Object> renderMap= new HashMap<String, Object>();
	private List<String> myLableList = new ArrayList<String>();
	private Map<Long, List<String>> myOrderedLableMap = new HashMap<>();
	
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
	 * @throws Exception
	 */
	public void onCreate$window_ReportPromptFilterCtrl(Event event) throws Exception {
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

			if (arguments.containsKey("ReportConfiguration")) {
				reportConfiguration = (ReportConfiguration) arguments.get("ReportConfiguration");
				
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
					doShowReport(null, null, null, null);
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
	private ReportConfiguration getReportConfiguration(String reportMenuCode) throws Exception {
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
	 * @throws Exception
	 */
	private void doRenderComponents() throws Exception {
		logger.debug("Entering");
		int j =0;
		for (int i = 0; i < reportConfiguration.getListReportFieldsDetails().size(); i++) {

			FIELDTYPE fieldValueType = FIELDTYPE
					.valueOf(reportConfiguration.getListReportFieldsDetails().get(i).getFieldType());

			switch (fieldValueType) {
			case TXT:
				renderSimpleInputElement(reportConfiguration.getListReportFieldsDetails().get(i),
						FIELDTYPE.TXT.toString());
				break;
			case NUMBER:
				renderSimpleInputElement(reportConfiguration.getListReportFieldsDetails().get(i),
						FIELDTYPE.NUMBER.toString());
				break;
			case DECIMAL:
				renderSimpleInputElement(reportConfiguration.getListReportFieldsDetails().get(i),
						FIELDTYPE.DECIMAL.toString());
				break;
			case INTRANGE:
				renderNumberRangeBox(reportConfiguration.getListReportFieldsDetails().get(i));
				break;
			case DECIMALRANGE:
				renderNumberRangeBox(reportConfiguration.getListReportFieldsDetails().get(i));
				break;

			case DATE:
				renderDateBox(reportConfiguration.getListReportFieldsDetails().get(i));
				break;
			case DATETIME:
				renderDateBox(reportConfiguration.getListReportFieldsDetails().get(i));
				break;
			case TIME:
				renderDateBox(reportConfiguration.getListReportFieldsDetails().get(i));
				break;
			case DATERANGE:
				renderDateRangeBox(reportConfiguration.getListReportFieldsDetails().get(i));
				break;
			case DATETIMERANGE:
				renderDateRangeBox(reportConfiguration.getListReportFieldsDetails().get(i));
				break;
			case TIMERANGE:
				renderDateRangeBox(reportConfiguration.getListReportFieldsDetails().get(i));
				break;

			case STATICLIST:
				renderComboBox(reportConfiguration.getListReportFieldsDetails().get(i), true);
				break;

			case DYNAMICLIST:
				renderComboBox(reportConfiguration.getListReportFieldsDetails().get(i), false);
				break;

			case LOVSEARCH:
				renderLovSearchField(reportConfiguration.getListReportFieldsDetails().get(i), j);
				j++;
				break;

			case MULTISELANDLIST:
				renderMultiSelctionList(reportConfiguration.getListReportFieldsDetails().get(i));
				break;

			case MULTISELINLIST:
				renderMultiSelctionList(reportConfiguration.getListReportFieldsDetails().get(i));
				break;
			case CHECKBOX:
				renderSimpleInputElement(reportConfiguration.getListReportFieldsDetails().get(i),
						FIELDTYPE.CHECKBOX.toString());
				break;
			case STATICVALUE:
				saticValuesWhereCondition = addAndCondition(saticValuesWhereCondition);
				saticValuesWhereCondition
						.append(reportConfiguration.getListReportFieldsDetails().get(i).getStaticValue());
				break;

			}
		}
		
		if (myLableList.size()>0 && !myLableList.isEmpty()) {
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
		sortOperator.setId("sortOperator_"+aReportFieldsDetails.getFieldID());
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
	private void renderComboBox(ReportFilterFields aReportFieldsDetails, boolean isStatic) throws Exception {
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
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		comboBox.appendChild(comboitem);
		comboBox.setId(Long.toString(aReportFieldsDetails.getFieldID()));
		comboBox.setWidth(aReportFieldsDetails.getFieldWidth() + "px");

		try {
			// If Static List get From PennantAppUtil Methods else from Search Object by module name and where condition
			if (isStatic) {
				List<ValueLabel> staticValuesList = (List<ValueLabel>) Class
						.forName("com.pennant.backend.util.PennantStaticListUtil").getMethod(aReportFieldsDetails
						.getAppUtilMethodName()).invoke(Class.forName("com.pennant.backend.util.PennantStaticListUtil"));
				listSelectionMaps.put(comboBox.getId(), staticValuesList);

				for (int i = 0; i < staticValuesList.size(); i++) {
					if (!staticValuesList.get(i).getValue().equals(Labels.getLabel("value_Select")) && (StringUtils.isNotEmpty(staticValuesList.get(i).getValue()))) {
						comboitem = new Comboitem();
						// comboitem.setValue(staticValuesList.get(i).getId());
						comboitem.setLabel(staticValuesList.get(i).getLabel());
						comboitem.setValue(staticValuesList.get(i).getValue());
						comboBox.appendChild(comboitem);
						comboBox.addForward("onSelect", window_ReportPromptFilterCtrl, "onComboFieldSelected", comboBox);
					}
				}
				comboBox.setSelectedIndex(0);
			} else {
				List<ValueLabel> staticValuesList = new ArrayList<ValueLabel>();
				JdbcSearchObject<Object> dynsearchObject = new JdbcSearchObject<Object>((Class<Object>) ModuleUtil.getModuleClass(aReportFieldsDetails.getModuleName()));
				// Add where condition for LOV Search Filter
				if (aReportFieldsDetails.getWhereCondition() != null && !("").equals(aReportFieldsDetails.getWhereCondition().trim())) {
					dynsearchObject.addWhereClause(aReportFieldsDetails.getWhereCondition());
				}
				List<Object> dynamicListResult = getPagedListWrapper().getPagedListService().getBySearchObject(dynsearchObject);
				ValueLabel valueLabel = null;
				for (int i = 0; i < dynamicListResult.size(); i++) {
					valueLabel = new ValueLabel();
					comboitem = new Comboitem();

					Object object = dynamicListResult.get(i).getClass().getMethod(aReportFieldsDetails.getLovHiddenFieldMethod()).invoke(dynamicListResult.get(i));

					comboitem.setValue(object.toString());
					valueLabel.setValue(object.toString());

					object = (String) dynamicListResult.get(i).getClass().getMethod(aReportFieldsDetails.getLovTextFieldMethod()).invoke(dynamicListResult.get(i));

					comboitem.setLabel(object.toString());
					valueLabel.setLabel(object.toString());
					staticValuesList.add(valueLabel);
					comboBox.appendChild(comboitem);

				}
				comboBox.setSelectedIndex(0);
				listSelectionMaps.put(comboBox.getId(), staticValuesList);
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

		logger.debug("Leaving");
	}

	/**
	 * This method fills Band box with multiple selected list box for multiple selection
	 * 
	 * @param listBox
	 */
	@SuppressWarnings("unchecked")
	public void renderMultiSelctionList(ReportFilterFields aReportFieldsDetails) throws Exception {
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
		StringBuilder whereCondition=null;
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

		for (int i = 0; i < reportConfiguration.getListReportFieldsDetails().size(); i++) {
			ReportFilterFields aReportFieldsDetails = reportConfiguration.getListReportFieldsDetails().get(i);
			String filedId = Long.toString(aReportFieldsDetails.getFieldID());
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
						if (StringUtils.isNotEmpty(filter) && !("").equals(textbox.getValue().trim())) {
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
									(Textbox) we.getComponent().getParent().getChildren().get(2), we.getMessage());
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
										aReportSearchTemplate.setFieldValue(DateUtility
												.formatUtilDate(datebox.getValue(), PennantConstants.DBDateFormat));
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
					// Enter only From Value Selected
					if ((fromDateBox instanceof Datebox && ((Datebox) fromDateBox).getValue() != null)
							|| (fromDateBox instanceof Timebox && ((Timebox) fromDateBox).getValue() != null)
							|| (fromDateBox instanceof Intbox && ((Intbox) fromDateBox).getValue() != null)
							|| (fromDateBox instanceof Decimalbox && ((Decimalbox) fromDateBox).getValue() != null)) {
						// Checking To value is selected or not
						if ((toDateBox instanceof Datebox && ((Datebox) toDateBox).getValue() != null)
								|| (toDateBox instanceof Timebox && ((Timebox) toDateBox).getValue() != null)
								|| (toDateBox instanceof Intbox && ((Intbox) toDateBox).getValue() != null)
								|| (toDateBox instanceof Decimalbox && ((Decimalbox) toDateBox).getValue() != null)) {
							// Check From date is before To date
							if (fromDateBox instanceof Datebox
									&& ((Datebox) toDateBox).getValue().before(((Datebox) fromDateBox).getValue())) {
								throw new WrongValueException(fromDateBox,
										Labels.getLabel("label_Error_FromDateMustBfrTo.vlaue"));
							} else if (fromDateBox instanceof Timebox // Check From time is before To time
									&& DateUtility.compareTime(((Timebox) toDateBox).getValue(),
											((Timebox) fromDateBox).getValue(), true) == -1) {
								throw new WrongValueException(fromDateBox,
										Labels.getLabel("label_Error_FromTimeMustBfrTo.vlaue"));
							} else if (fromDateBox instanceof Decimalbox) {
								Number fromValue = (Number) (fromDateBox instanceof Intbox
										? ((Intbox) fromDateBox).getValue() : ((Decimalbox) fromDateBox).getValue());
								Number toValue = (Number) (toDateBox instanceof Intbox ? ((Intbox) toDateBox).getValue()
										: ((Decimalbox) toDateBox).getValue());
								if (fromValue.doubleValue() > toValue.doubleValue()) {
									throw new WrongValueException(fromDateBox,
											Labels.getLabel("label_Error_FromValueMustGretaerTo.vlaue"));
								}
							}
							if (!excludeDates) {
								whereCondition = getWhereConditionFromDateTimeAndRangeTypes(whereCondition,
										aReportFilterFields, fromDateBox, ">=");
								whereCondition = getWhereConditionFromDateTimeAndRangeTypes(whereCondition,
										aReportFilterFields, toDateBox, "<=");
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
								fromValue = DateUtility.formatToShortDate(fromDate.getValue());
								toValue = DateUtility.formatToShortDate(todate.getValue());
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
			/* if any Exception Occurs make password and new password Fields empty */
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
								+ DateUtility.formatUtilDate(datebox.getValue(), PennantConstants.DBDateFormat) + "'";
					}

					if (App.DATABASE == Database.SQL_SERVER) {
						exactDate = "CONVERT(DATETIME, FLOOR(CONVERT(FLOAT," + aReportFieldsDetails.getFieldDBName()
								+ "))) " + filter + "'"
								+ DateUtility.formatUtilDate(datebox.getValue(), PennantConstants.DBDateFormat) + "'";
					}
					if (App.DATABASE == Database.ORACLE) {
						exactDate = aReportFieldsDetails.getFieldDBName() + " " + filter + "'"
								+ DateUtility.formatUtilDate(datebox.getValue(), PennantConstants.DBDateFormat) + "'";
					}
					whereCondition.append(exactDate);

				}
				// Prepare query for Exact Date and Time
				if (fieldValueType.toString().equals(FIELDTYPE.DATETIME.toString())
						|| fieldValueType.toString().equals(FIELDTYPE.DATETIMERANGE.toString())) {
					String dateTime = DateUtility.formatUtilDate(datebox.getValue(), PennantConstants.DBDateTimeFormat);
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
							+ DateUtility.formatUtilDate(timeBox.getValue(), PennantConstants.DBDateTimeFormat) + "'";
				}
				if (App.DATABASE == Database.SQL_SERVER) {
					timeFunction = "CONVERT(VARCHAR(8)," + aReportFieldsDetails.getFieldDBName() + ",108)" + filter
							+ "'" + DateUtility.formatUtilDate(timeBox.getValue(), PennantConstants.DBTimeFormat) + "'";
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
			filedValue = DateUtility.formatUtilDate((Date) filedValue, dateFormat);
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
			Object tempComponent = (Object) dymanicFieldsRows
					.getFellowIfAny(Long.toString(aReportFilterFields.getFieldID()));
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
									aReportFilterFields.getFieldConstraint()));
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

			Object tempComponent = (Object) dymanicFieldsRows
					.getFellowIfAny(Long.toString(aReportFilterFields.getFieldID()));
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
	 * 
	 * @throws Exception
	 */
	public void doShowReport(String whereCond, String whereCond2, String fromDate, String toDate) throws Exception {
		logger.debug("Entering");

		HashMap<String, Object> reportArgumentsMap = new HashMap<String, Object>(10);

		reportArgumentsMap.put("userName", getUserWorkspace().getLoggedInUser().getFullName());
		reportArgumentsMap.put("reportHeading", reportConfiguration.getReportHeading());
		reportArgumentsMap.put("reportGeneratedBy", Labels.getLabel("Reports_footer_ReportGeneratedBy.lable"));
		reportArgumentsMap.put("appDate", DateUtility.getAppDate());
		reportArgumentsMap.put("appCcy", SysParamUtil.getValueAsString("APP_DFT_CURR"));
		reportArgumentsMap.put("appccyEditField", SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT));
		reportArgumentsMap.put("unitParam", unitName);

		if (whereCond != null) {
			reportArgumentsMap.put("whereCondition", whereCond);
		}
		if (whereCond2 != null) {
			reportArgumentsMap.put("whereCondition1", whereCond2);
		}
		if (fromDate != null) {
			reportArgumentsMap.put("fromDate", "'" + DateUtility.getDBDate(fromDate).toString() + "'");
		}
		if (toDate != null) {
			reportArgumentsMap.put("toDate", "'" + DateUtility.getDBDate(toDate).toString() + "'");
		}

		if (!reportConfiguration.isPromptRequired()) {
			reportArgumentsMap.put("whereCondition", "");
		}

		reportArgumentsMap.put("organizationLogo", PathUtil.getPath(PathUtil.REPORTS_IMAGE_CLIENT));
		reportArgumentsMap.put("productLogo", PathUtil.getPath(PathUtil.REPORTS_IMAGE_PRODUCT));
		reportArgumentsMap.put("bankName", Labels.getLabel("label_ClientName"));

		reportArgumentsMap.put("searchCriteria", searchCriteriaDesc.toString());
		String reportName = reportConfiguration.getReportJasperName();// This will come dynamically
		String reportSrc = PathUtil.getPath(PathUtil.REPORTS_ORGANIZATION) + "/" + reportName + ".jasper";

		byte[] buf = null;
		Connection con = null;
		DataSource reportDataSourceObj = null;

		try {
			File file = new File(reportSrc);
			if (file.exists()) {

				logger.debug("Buffer started");

				reportDataSourceObj = (DataSource) SpringUtil.getBean(reportConfiguration.getDataSourceName());// This
																												// will
																												// come
																												// dynamically
				con = reportDataSourceObj.getConnection();

				if ((!isExcel && !this.rows_formatType.isVisible())
						|| (this.rows_formatType.isVisible() && this.pdfFormat.isChecked())) {

					buf = JasperRunManager.runReportToPdf(reportSrc, reportArgumentsMap, con);
					HashMap<String, Object> auditMap = new HashMap<String, Object>(4);
					auditMap.put("reportBuffer", buf);
					auditMap.put("parentWindow", this.window_ReportPromptFilterCtrl);
					auditMap.put("reportName", reportConfiguration.getReportName().replace(EXCEL_TYPE, ""));
					auditMap.put("tabbox", tabbox);
					auditMap.put("searchClick", searchClick);
					auditMap.put("selectTab", selectTab);
					if (dialogWindow != null) {
						auditMap.put("dialogWindow", dialogWindow);
					}

					// call the ZUL-file with the parameters packed in a map
					Executions.createComponents("/WEB-INF/pages/Reports/ReportView.zul", null, auditMap);
				} else {

					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					String printfileName = JasperFillManager.fillReportToFile(reportSrc, reportArgumentsMap, con);
					JRXlsExporter excelExporter = new JRXlsExporter();
					reportName = reportConfiguration.getReportHeading();
					excelExporter.setParameter(JRExporterParameter.INPUT_FILE_NAME, printfileName);
					// excelExporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);
					excelExporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
					excelExporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
					excelExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
					excelExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS,
							Boolean.TRUE);
					excelExporter.setParameter(JRXlsExporterParameter.IS_IGNORE_GRAPHICS, Boolean.FALSE);
					excelExporter.setParameter(JRXlsExporterParameter.IS_IGNORE_CELL_BORDER, Boolean.FALSE);
					excelExporter.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN, Boolean.TRUE);
					excelExporter.setParameter(JRXlsExporterParameter.IS_IMAGE_BORDER_FIX_ENABLED, Boolean.FALSE);
					excelExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
					excelExporter.exportReport();
					Filedownload.save(
							new AMedia(reportName, "xls", "application/vnd.ms-excel", outputStream.toByteArray()));

					if (selectTab != null) {
						// selectTab.setSelected(true);
						// if(!reportConfiguration.isPromptRequired()){//ReOpen the Comment after Auto Refresh Fix
						selectTab.onClose();
						// }
					}
				}

			} else {
				MessageUtil.showError(Labels.getLabel("label_Error_ReportNotImplementedYet.vlaue"));
				closeDialog();
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showError("Error in Configuring the " + reportName + " report");
			closeDialog();
		} finally {
			if (con != null) {
				con.close();
			}
			con = null;
			reportDataSourceObj = null;
			buf = null;
		}
		logger.debug("Leaving");
	}

	// COMPONENT EVENTS

	/**
	 * When user Clicks on "Add Search Template"
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onClick$btnSaveTemplate(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		reportSearchTemplateFieldsList = (List<ReportSearchTemplate>) doPrepareWhereConditionOrTemplate(false, false);
		if (reportSearchTemplateFieldsList.size() > 0) {
			final HashMap<String, Object> map = new HashMap<String, Object>();
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
	 * @throws Exception
	 */

	public void onClick$btnDeleteTemplate(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// Show a confirm box
		final String msg = Labels.getLabel("label_ReportGenerationDialgCtrl_Delete_Template") + "\n\n --> "
				+ this.cbSelectTemplate.getSelectedItem().getLabel();

		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			try {
				boolean isRcdDeleted = getReportConfigurationService().deleteSearchTemplate(
						reportConfiguration.getReportID(), getUserWorkspace().getLoggedInUser().getLoginUsrID(),
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
				logger.error("Exception: ", e);
				showMessage(e);
			}

		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * This method calls from ReportSearchTemplatePromptDialogCtrl on click save button .it sets template name and save
	 * the template
	 * 
	 * @param reportSearchTemplateList
	 * @return
	 * @throws InterruptedException
	 */
	protected boolean doSaveTemplate(long reportId, long usrId, String templateName) throws InterruptedException {
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
	 * @throws Exception
	 */
	public void onSelect$cbSelectTemplate(Event event) throws Exception {
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
	 * @throws Exception
	 * 
	 */

	private void doSetSearchTemplate(Map<Long, ReportSearchTemplate> reportSearchTemplateMap) throws Exception {
		logger.debug("Entering");
		for (int i = 0; i < reportConfiguration.getListReportFieldsDetails().size(); i++) {
			ReportFilterFields aReportFieldsDetails = reportConfiguration.getListReportFieldsDetails().get(i);

			if (reportSearchTemplateMap.containsKey(aReportFieldsDetails.getFieldID())) {
				ReportSearchTemplate reportSearchTemplate = (ReportSearchTemplate) reportSearchTemplateMap
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
	 * @throws Exception
	 */
	private void doSetValueOrClearOpertionOnFields(ReportFilterFields aReportFilterField,
			ReportSearchTemplate reportSearchTemplate, boolean isClearComponents) throws Exception {
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
								datebox.setValue(DateUtility.getDate(dateFields[0], PennantConstants.DBDateFormat));
							} else {
								datebox.setValue(DateUtility.getDate(dateFields[0], PennantConstants.dateFormat));
							}
						} catch (Exception e) {
							logger.error("Error in Formating The Date", e);
							datebox.setValue(DateUtility.getSysDate());
						}
					} else {
						datebox.setValue(DateUtility.getSysDate());
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
								todatebox.setValue(DateUtility.getDate(dateFields[1], PennantConstants.dateFormat));
							} catch (Exception e) {
								logger.error("Error in Formating The Date", e);
								todatebox.setValue(DateUtility.getSysDate());
							}
						} else {
							todatebox.setValue(DateUtility.getSysDate());
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
					timebox.setValue(DateUtility.getSysDate());
				}
				if (aReportFilterField.getFieldType().contains("RANGE")) {
					Timebox toTimebox = (Timebox) timebox.getNextSibling().getNextSibling();
					if (isClearComponents) {
						toTimebox.setValue(null);
						toTimebox.setText("");

					} else {
						toTimebox.setValue(DateUtility.getSysDate());
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
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setLovSearchValue(ReportFilterFields aReportFilterField, String filedValue, Textbox displaytextBox)
			throws Exception {
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
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onClick$btnSearch(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		searchClick = true;

		// ++ create the searchObject and initialize sorting ++//
		if (StringUtils.equals(reportMenuCode, "menu_Item_AccountStmt")) {
			String fromDate = null;
			String toDate = null;
			List<ReportSearchTemplate> filters = (List<ReportSearchTemplate>) doPrepareWhereConditionOrTemplate(false,
					false);
			if (filters != null && filters.size() >= 2) {
				String[] fromDateArray = ((ReportSearchTemplate) filters.get(1)).getFieldValue().split("&");
				fromDate = DateUtility.formatUtilDate(DateUtility.getDate(fromDateArray[0]),
						PennantConstants.DBDateFormat);
				toDate = DateUtility.formatUtilDate(DateUtility.getDate(fromDateArray[1]),
						PennantConstants.DBDateFormat);
			}

			StringBuilder whereCond1 = (StringBuilder) doPrepareWhereConditionOrTemplate(true, true);
			StringBuilder whereCond2 = (StringBuilder) doPrepareWhereConditionOrTemplate(true, false);
			/*
			 * StringBuilder whereCond3 = (StringBuilder) doPrepareWhereConditionOrTemplate(false, true); StringBuilder
			 * whereCond4 = (StringBuilder) doPrepareWhereConditionOrTemplate(true, false);
			 */

			doShowReport("where".equals(whereCond1.toString().trim()) ? "" : whereCond1.toString(),
					"where".equals(whereCond2.toString().trim()) ? "" : whereCond2.toString(), fromDate, toDate);
		} else if (StringUtils.equals(reportMenuCode, "menu_Item_DelinquencyVariance")) {
			String fromDate = null;
			String toDate = null;
			List<ReportSearchTemplate> filters = (List<ReportSearchTemplate>) doPrepareWhereConditionOrTemplate(false,
					false);
			if (filters != null && filters.size() >= 2) {
				fromDate = ((ReportSearchTemplate) filters.get(0)).getFieldValue();
				toDate = ((ReportSearchTemplate) filters.get(1)).getFieldValue();
			}

			StringBuilder whereCondition = (StringBuilder) doPrepareWhereConditionOrTemplate(true, true);
			doShowReport("where".equals(whereCondition.toString().trim()) ? "" : whereCondition.toString(), null,
					fromDate, toDate);
		} else if (StringUtils.equals(reportMenuCode, "menu_Item_LimitReports")) {
			String limitType = null;
			// SimpleDateFormat format=new SimpleDateFormat()""
			// Date currentDate= new Date(System.currentTimeMillis());
			StringBuilder whereCondition = new StringBuilder();
			List<ReportSearchTemplate> filters = (List<ReportSearchTemplate>) doPrepareWhereConditionOrTemplate(false,
					false);
			if (filters != null && filters.size() > 0) {
				limitType = ((ReportSearchTemplate) filters.get(0)).getFieldValue();
				if ("Expired Limits".equals(limitType)) {
					whereCondition.append(" Where T2.ExpiryDate < GetDate()  ");
				} else if ("Excess Limits".equals(limitType)) {
					whereCondition.append(" Where ( CalculatedLimit-UtilisedLimit ) < 0 ");
				}

			}

			// StringBuilder whereCondition = (StringBuilder) doPrepareWhereConditionOrTemplate(true, true);
			doShowReport("where".equals(whereCondition.toString().trim()) ? "" : whereCondition.toString(), null, null,
					null);
		} else {
			StringBuilder whereCondition = (StringBuilder) doPrepareWhereConditionOrTemplate(true, false);
			doShowReport("where".equals(whereCondition.toString().trim()) ? "" : whereCondition.toString(), null, null,
					null);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When user Clicks on "Search"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnClear(Event event) throws Exception {
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
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		if (doClose(false)) {
			if (tabbox != null) {
				tabbox.getSelectedTab().close();
			}
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

		public void onEvent(Event event) throws Exception {
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
		
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * On LovSearch Button Clicked
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onLovButtonClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		CustomArgument customArgument = (CustomArgument) event.getData();
		ReportFilterFields aReportFieldsDetails = customArgument.getaReportFieldsDetails();

		Hbox hbox = (Hbox) customArgument.hbox;
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
				lovSearchMap = (Map<String, Object>) ExtendedMultipleSearchListBox.show(
						this.window_ReportPromptFilterCtrl, button.getId(),
						filterMap == null ? new HashMap<String, Object>() : filterMap);

				// Put in map for select next time
				lovSearchBufferMap.put(valuestextBox.getId(), lovSearchMap);

				if (lovSearchMap != null) {
					String codes = "";
					String descs = "";
					Set<String> suCodes = lovSearchMap.keySet();
					Iterator<String> itr = suCodes.iterator();

					while (itr.hasNext()) {
						String str = itr.next();

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
				  Object dataObject = ExtendedSearchListBox.show(this.window_ReportPromptFilterCtrl,  button.getId(),  valuestextBox.getValue(), filters, searchValue);
				  
				  if (dataObject instanceof String) {
					valuestextBox.setValue(dataObject.toString());
					labelstextBox.setValue("");
					doClearFields(aReportFieldsDetails);
				} else {
					Object details = (Object) dataObject;

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
				valueMap.put(aReportFieldsDetails.getFieldDBName(), valuestextBox.getValue());
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showError(Labels.getLabel("label_ReportConfiguredError.error"));
		}

		logger.debug("Leaving" + event.toString());
	}

	
	private Filter[] doLovFilter(ReportFilterFields aReportFieldsDetails) throws Exception {
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
			throw new Exception(message.insert(0, "Please select the below fields:").toString());
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
			li = (Listitem) multiSelectionListBox.getItems().get(i);
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
				getUserWorkspace().getLoggedInUser().getLoginUsrID());

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
	 * @throws Exception
	 */
	private void doClearComponents() throws Exception {
		for (int i = 0; i < reportConfiguration.getListReportFieldsDetails().size(); i++) {
			ReportFilterFields aReportFieldsDetails = reportConfiguration.getListReportFieldsDetails().get(i);
			doSetValueOrClearOpertionOnFields(aReportFieldsDetails, null, true);
		}
	}
	
	/**
	 * Clear components
	 * 
	 * @throws Exception
	 */
	private void doClearFields(ReportFilterFields aReportFieldsDetails) throws Exception {
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
					Component component = dymanicFieldsRows.getFellow(Long.toString(fieldID));
					Textbox textbox = (Textbox) component;
					textbox.setValue("");
					Textbox lovDisplayText = (Textbox) component.getNextSibling();
					lovDisplayText.setValue("");
					fieldID++;
				}
				break mapRenderer;
			}
		}
		renderMap.clear();
		logger.debug("Entering");
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
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_ReportPromptFilterCtrl, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving ");
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
}