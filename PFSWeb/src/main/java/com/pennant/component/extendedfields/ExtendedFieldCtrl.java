package com.pennant.component.extendedfields;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timebox;
import org.zkoss.zul.Window;

import com.pennant.AccountSelectionBox;
import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.FrequencyBox;
import com.pennant.RateBox;
import com.pennant.UserWorkspace;
import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.model.ScriptError;
import com.pennant.backend.model.ScriptErrors;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.service.collateral.impl.ScriptValidationService;
import com.pennant.backend.service.staticparms.ExtFieldConfigService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.component.Uppercasebox;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtendedFieldCtrl {
	private static final Logger		logger		= Logger.getLogger(ExtendedFieldCtrl.class);

	private int								ccyFormat;
	private int								tabHeight;
	private boolean							isReadOnly	= false;
	private Tab								tab;
	private boolean							isNewRecord	= false;
	private Tabpanel						tabpanel;
	private Window 							window;

	private ExtendedFieldHeader				extendedFieldHeader;
	private ExtendedFieldRender				extendedFieldRender;
	private ExtendedFieldsGenerator			generator;

	private static ScriptValidationService	scriptValidationService;
	private static ExtFieldConfigService	extFieldConfigService;
	private static ExtendedFieldRenderDAO	extendedFieldRenderDAO;
	private static Map<String, Object> tabPanelsMap = new HashMap<>();
	private UserWorkspace					userWorkspace;
	private String							userRole;

	/**
	 * Method for Rendering the Extended field details
	 */
	public void render() throws ScriptException {
		logger.debug(Literal.ENTERING);

		// Extended Field Details auto population / Rendering into Screen
		this.generator = new ExtendedFieldsGenerator();
		this.generator.setTabpanel(tabpanel);
		this.generator.setRowWidth(220);
		this.generator.setCcyFormat(this.ccyFormat);
		this.generator.setReadOnly(this.isReadOnly);
		this.generator.setWindow(window);
		this.generator.setTabHeight(tabHeight);
		this.generator.setUserWorkspace(userWorkspace);
		this.generator.setUserRole(userRole);
		if (tab != null) {
			this.generator.setTopLevelTab(tab);
			this.tab.setLabel(extendedFieldHeader.getTabHeading());
		}
	

		// Pre-Validation Checking & Setting Defaults
		Map<String, Object> fieldValuesMap = null;
		if (this.extendedFieldRender != null && this.extendedFieldRender.getMapValues() != null) {
			fieldValuesMap = this.extendedFieldRender.getMapValues();
		}

		// Initiation of Field Value Map
		if (fieldValuesMap == null) {
			fieldValuesMap = new HashMap<>();
		}

		// setting the pre and post validation scripts
		if (!this.isReadOnly) {
			if (getUserWorkspace() != null) {
				String pageName = PennantApplicationUtil.getExtendedFieldPageName(extendedFieldHeader);
				if (StringUtils.isNotBlank(pageName)) {
					getUserWorkspace().allocateAuthorities(pageName, getUserRole());
				}
			}
			// get pre-validation script if record is new
			if (StringUtils.trimToNull(this.extendedFieldHeader.getPreValidation()) != null) {
				ScriptErrors defaults = scriptValidationService.setPreValidationDefaults(this.extendedFieldHeader.getPreValidation(), fieldValuesMap);

				// Overriding Default values
				List<ScriptError> defaultList = defaults.getAll();
				for (int i = 0; i < defaultList.size(); i++) {
					ScriptError dftKeyValue = defaultList.get(i);
					if (fieldValuesMap.containsKey(dftKeyValue.getProperty())) {
						fieldValuesMap.remove(dftKeyValue.getProperty());
					}
					fieldValuesMap.put(dftKeyValue.getProperty(), dftKeyValue.getValue());
				}
			}
		}
		if (fieldValuesMap != null) {
			generator.setFieldValueMap((HashMap<String, Object>) fieldValuesMap);
		}
		try {
			generator.renderWindow(this.extendedFieldHeader, this.isNewRecord);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for saving the Extended field details
	 * 
	 * @return ExtendedFieldRender extendedFieldRender
	 */
	public ExtendedFieldRender save() throws ParseException {

		if (this.extendedFieldHeader == null) {
			return null;
		}

		Map<String, Object> map = null;
		map = generator.doSave(this.extendedFieldHeader.getExtendedFieldDetails(), this.isReadOnly);
		this.extendedFieldRender.setMapValues(map);
		this.extendedFieldRender.setTypeCode(this.extendedFieldHeader.getSubModuleName());

		// Post Validations for the Extended fields
		if (!isReadOnly) {
			if (StringUtils.trimToNull(extendedFieldHeader.getPostValidation()) != null) {
				ScriptErrors postValidationErrors = scriptValidationService.getPostValidationErrors(extendedFieldHeader.getPostValidation(), map);
				showErrorDetails(postValidationErrors);
			}
		}

		if (isNewRecord) {
			this.extendedFieldRender.setSeqNo(1);
		}
		return extendedFieldRender;
	}


	/**
	 * Method for Showing UI Post validation Errors
	 * 
	 * @param postValidationErrors
	 */
	public void showErrorDetails(ScriptErrors postValidationErrors) {
		List<ScriptError> errorList = postValidationErrors.getAll();
		if (errorList == null || errorList.isEmpty()) {
			return;
		}
		List<ExtendedFieldDetail> notInputElements = new ArrayList<>();
		HashMap<ExtendedFieldDetail, WrongValueException> wveMap = new HashMap<>();
		List<Component> compList = new ArrayList<Component>();

		for (ExtendedFieldDetail detail : extendedFieldHeader.getExtendedFieldDetails()) {
			if (!detail.isInputElement()) {
				notInputElements.add(detail);
			}
		}

		for (int i = 0; i < errorList.size(); i++) {
			ScriptError error = errorList.get(i);
			if (tabpanel != null && tabpanel.getFellowIfAny("ad_" + error.getProperty()) != null) {
				Component component = tabpanel.getFellowIfAny("ad_" + error.getProperty());
				WrongValueException we = new WrongValueException(component, error.getValue());
				ExtendedFieldDetail detail = getExtendedFieldByCompId(component.getId());
				if (detail != null) {
					wveMap.put(detail, we);
					compList.add(component);
				}
			}
		}
		//component visibility based validation thrown in this method, if the component is not visible validation not thrown
		generator.showErrorDetails(wveMap, compList, notInputElements);
	}

	/**
	 * Method to fetch the ExtemdedFieldDetails based on FieldName.
	 * 
	 * @param compId
	 * @return
	 */
	public ExtendedFieldDetail getExtendedFieldByCompId(String compId) {
		if (StringUtils.isNotBlank(compId) || compId.contains("ad_"))
			if (extendedFieldHeader != null && !extendedFieldHeader.getExtendedFieldDetails().isEmpty()) {
				String fieldName = compId.substring(3, compId.length());
				for (ExtendedFieldDetail detail : extendedFieldHeader.getExtendedFieldDetails()) {
					if (StringUtils.equalsIgnoreCase(fieldName, detail.getFieldName())) {
						return detail;
					}
				}
			}
		return null;

	}
	/**
	 * Method to show error details if occurred
	 * 
	 * @param Tab
	 *            tab
	 * @param ArrayList<WrongValueException>
	 *            wve
	 **/
//	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
//		logger.debug(Literal.ENTERING);
//
//		if (wve.size() > 0) {
//			logger.debug("Throwing occured Errors By using WrongValueException");
//			if (this.parentTab != null) {
//				this.parentTab.setSelected(true);
//			}
//			this.tab.setSelected(true);
//
//			WrongValueException[] wvea = new WrongValueException[wve.size()];
//			for (int i = 0; i < wve.size(); i++) {
//				wvea[i] = wve.get(i);
//				if (i == 0) {
//					Component comp = wvea[i].getComponent();
//					if (comp instanceof HtmlBasedComponent) {
//						Clients.scrollIntoView(comp);
//					}
//				}
//				logger.debug(wvea[i]);
//			}
//			throw new WrongValuesException(wvea);
//		}
//		logger.debug(Literal.LEAVING);
//	}

	/**
	 * Method Getting the extended field header details
	 * 
	 * @param String
	 *            module
	 * @param String
	 *            subModule
	 **/
	public ExtendedFieldHeader getExtendedFieldHeader(String module, String subModule) {
		ExtendedFieldHeader extendedFieldHeader = extFieldConfigService.getApprovedExtendedFieldHeaderByModule(module,subModule);
		this.extendedFieldHeader = extendedFieldHeader;
		return extendedFieldHeader;
	}
	
	/**
	 * Method Getting the extended field header details
	 * 
	 * @param String
	 *            module
	 * @param String
	 *            subModule
	 **/
	public ExtendedFieldHeader getExtendedFieldHeader(String module, String subModule, int extendedType) {
		ExtendedFieldHeader extendedFieldHeader = extFieldConfigService.getApprovedExtendedFieldHeaderByModule(module,subModule,extendedType);
		this.extendedFieldHeader = extendedFieldHeader;
		return extendedFieldHeader;
	}

	/**
	 * Method Getting the extended field render details
	 * 
	 * @param String
	 *            reference
	 **/
	public ExtendedFieldRender getExtendedFieldRender(String reference) {

		// Extended Field Details
		StringBuilder tableName = new StringBuilder();
		tableName.append(extendedFieldHeader.getModuleName());
		tableName.append("_");
		tableName.append(extendedFieldHeader.getSubModuleName());
		tableName.append("_ED");

		Map<String, Object> extFieldMap = extendedFieldRenderDAO.getExtendedField(reference, tableName.toString(),
				"_View");
		ExtendedFieldRender extendedFieldRender = new ExtendedFieldRender();
		if (extFieldMap != null) {
			extendedFieldRender.setReference(String.valueOf(extFieldMap.get("Reference")));
			extFieldMap.remove("Reference");
			extendedFieldRender.setSeqNo(Integer.valueOf(String.valueOf(extFieldMap.get("SeqNo"))));
			extFieldMap.remove("SeqNo");
			extendedFieldRender.setVersion(Integer.valueOf(String.valueOf(extFieldMap.get("Version"))));
			extFieldMap.remove("Version");
			extendedFieldRender.setLastMntOn((Timestamp) extFieldMap.get("LastMntOn"));
			extFieldMap.remove("LastMntOn");
			extendedFieldRender.setLastMntBy(Long.valueOf(String.valueOf(extFieldMap.get("LastMntBy"))));
			extFieldMap.remove("LastMntBy");
			extendedFieldRender
					.setRecordStatus(StringUtils.equals(String.valueOf(extFieldMap.get("RecordStatus")), "null") ? ""
							: String.valueOf(extFieldMap.get("RecordStatus")));
			extFieldMap.remove("RecordStatus");
			extendedFieldRender.setRoleCode(StringUtils.equals(String.valueOf(extFieldMap.get("RoleCode")), "null") ? ""
					: String.valueOf(extFieldMap.get("RoleCode")));
			extFieldMap.remove("RoleCode");
			extendedFieldRender
					.setNextRoleCode(StringUtils.equals(String.valueOf(extFieldMap.get("NextRoleCode")), "null") ? ""
							: String.valueOf(extFieldMap.get("NextRoleCode")));
			extFieldMap.remove("NextRoleCode");
			extendedFieldRender.setTaskId(StringUtils.equals(String.valueOf(extFieldMap.get("TaskId")), "null") ? ""
					: String.valueOf(extFieldMap.get("TaskId")));
			extFieldMap.remove("TaskId");
			extendedFieldRender.setNextTaskId(StringUtils.equals(String.valueOf(extFieldMap.get("NextTaskId")), "null")
					? "" : String.valueOf(extFieldMap.get("NextTaskId")));
			extFieldMap.remove("NextTaskId");
			extendedFieldRender.setRecordType(StringUtils.equals(String.valueOf(extFieldMap.get("RecordType")), "null")
					? "" : String.valueOf(extFieldMap.get("RecordType")));
			extFieldMap.remove("RecordType");
			extendedFieldRender.setWorkflowId(Long.valueOf(String.valueOf(extFieldMap.get("WorkflowId"))));
			extFieldMap.remove("WorkflowId");
			extendedFieldRender.setMapValues(extFieldMap);
		}
		this.extendedFieldRender = extendedFieldRender;
		return extendedFieldRender;
	}
	/**
	 * Method Getting the extended field render details
	 * 
	 * @param String
	 *            reference
	 * @param String
	 *            tableName
	 **/
	public ExtendedFieldRender getExtendedFieldRender(String reference, String tableName) {
		
		Map<String, Object> extFieldMap = extendedFieldRenderDAO.getExtendedField(reference, tableName, "_View");
		ExtendedFieldRender extendedFieldRender = new ExtendedFieldRender();
		if (extFieldMap != null) {
			extendedFieldRender.setReference(String.valueOf(extFieldMap.get("Reference")));
			extFieldMap.remove("Reference");
			extendedFieldRender.setSeqNo(Integer.valueOf(String.valueOf(extFieldMap.get("SeqNo"))));
			extFieldMap.remove("SeqNo");
			extendedFieldRender.setVersion(Integer.valueOf(String.valueOf(extFieldMap.get("Version"))));
			extFieldMap.remove("Version");
			extendedFieldRender.setLastMntOn((Timestamp) extFieldMap.get("LastMntOn"));
			extFieldMap.remove("LastMntOn");
			extendedFieldRender.setLastMntBy(Long.valueOf(String.valueOf(extFieldMap.get("LastMntBy"))));
			extFieldMap.remove("LastMntBy");
			extendedFieldRender
			.setRecordStatus(StringUtils.equals(String.valueOf(extFieldMap.get("RecordStatus")), "null") ? ""
					: String.valueOf(extFieldMap.get("RecordStatus")));
			extFieldMap.remove("RecordStatus");
			extendedFieldRender.setRoleCode(StringUtils.equals(String.valueOf(extFieldMap.get("RoleCode")), "null") ? ""
					: String.valueOf(extFieldMap.get("RoleCode")));
			extFieldMap.remove("RoleCode");
			extendedFieldRender
			.setNextRoleCode(StringUtils.equals(String.valueOf(extFieldMap.get("NextRoleCode")), "null") ? ""
					: String.valueOf(extFieldMap.get("NextRoleCode")));
			extFieldMap.remove("NextRoleCode");
			extendedFieldRender.setTaskId(StringUtils.equals(String.valueOf(extFieldMap.get("TaskId")), "null") ? ""
					: String.valueOf(extFieldMap.get("TaskId")));
			extFieldMap.remove("TaskId");
			extendedFieldRender.setNextTaskId(StringUtils.equals(String.valueOf(extFieldMap.get("NextTaskId")), "null")
					? "" : String.valueOf(extFieldMap.get("NextTaskId")));
			extFieldMap.remove("NextTaskId");
			extendedFieldRender.setRecordType(StringUtils.equals(String.valueOf(extFieldMap.get("RecordType")), "null")
					? "" : String.valueOf(extFieldMap.get("RecordType")));
			extFieldMap.remove("RecordType");
			extendedFieldRender.setWorkflowId(Long.valueOf(String.valueOf(extFieldMap.get("WorkflowId"))));
			extFieldMap.remove("WorkflowId");
			extendedFieldRender.setMapValues(extFieldMap);
		}
		this.extendedFieldRender = extendedFieldRender;
		return extendedFieldRender;
	}

	/**
	 * Method Getting the extended field render details
	 * 
	 * @param String
	 *            reference
	 * @param String
	 *            tableName
	 **/
	public ExtendedFieldRender getExtendedFieldRender(String reference, String tableName, String type) {
		
		Map<String, Object> extFieldMap = extendedFieldRenderDAO.getExtendedField(reference, tableName, type);
		ExtendedFieldRender extendedFieldRender = new ExtendedFieldRender();
		if (extFieldMap != null) {
			extendedFieldRender.setReference(String.valueOf(extFieldMap.get("Reference")));
			extFieldMap.remove("Reference");
			extendedFieldRender.setSeqNo(Integer.valueOf(String.valueOf(extFieldMap.get("SeqNo"))));
			extFieldMap.remove("SeqNo");
			extendedFieldRender.setVersion(Integer.valueOf(String.valueOf(extFieldMap.get("Version"))));
			extFieldMap.remove("Version");
			extendedFieldRender.setLastMntOn((Timestamp) extFieldMap.get("LastMntOn"));
			extFieldMap.remove("LastMntOn");
			extendedFieldRender.setLastMntBy(Long.valueOf(String.valueOf(extFieldMap.get("LastMntBy"))));
			extFieldMap.remove("LastMntBy");
			extendedFieldRender
			.setRecordStatus(StringUtils.equals(String.valueOf(extFieldMap.get("RecordStatus")), "null") ? ""
					: String.valueOf(extFieldMap.get("RecordStatus")));
			extFieldMap.remove("RecordStatus");
			extendedFieldRender.setRoleCode(StringUtils.equals(String.valueOf(extFieldMap.get("RoleCode")), "null") ? ""
					: String.valueOf(extFieldMap.get("RoleCode")));
			extFieldMap.remove("RoleCode");
			extendedFieldRender
			.setNextRoleCode(StringUtils.equals(String.valueOf(extFieldMap.get("NextRoleCode")), "null") ? ""
					: String.valueOf(extFieldMap.get("NextRoleCode")));
			extFieldMap.remove("NextRoleCode");
			extendedFieldRender.setTaskId(StringUtils.equals(String.valueOf(extFieldMap.get("TaskId")), "null") ? ""
					: String.valueOf(extFieldMap.get("TaskId")));
			extFieldMap.remove("TaskId");
			extendedFieldRender.setNextTaskId(StringUtils.equals(String.valueOf(extFieldMap.get("NextTaskId")), "null")
					? "" : String.valueOf(extFieldMap.get("NextTaskId")));
			extFieldMap.remove("NextTaskId");
			extendedFieldRender.setRecordType(StringUtils.equals(String.valueOf(extFieldMap.get("RecordType")), "null")
					? "" : String.valueOf(extFieldMap.get("RecordType")));
			extFieldMap.remove("RecordType");
			extendedFieldRender.setWorkflowId(Long.valueOf(String.valueOf(extFieldMap.get("WorkflowId"))));
			extFieldMap.remove("WorkflowId");
			extendedFieldRender.setMapValues(extFieldMap);
		}
		this.extendedFieldRender = extendedFieldRender;
		return extendedFieldRender;
	}
	
	/**
	 * Method Getting the extended field render details
	 * 
	 * @param String
	 *            reference
	 * @param String
	 *            tableName
	 **/
	public List<ExtendedFieldRender> getExtendedFieldRendeList(long id, String tableName, String type) {

		List<ExtendedFieldRender> extendedFieldRenderList = new ArrayList<ExtendedFieldRender>();
		
		List<Map<String, Object>> extFieldList = extendedFieldRenderDAO.getExtendedFieldMap(id, tableName, type);
		if (extFieldList != null && !extFieldList.isEmpty()) {
			for (Map<String, Object> extFieldMap : extFieldList) {
				ExtendedFieldRender extendedFieldRender = new ExtendedFieldRender();
				if (extFieldMap != null) {
					extendedFieldRender.setReference(String.valueOf(extFieldMap.get("Reference")));
					extFieldMap.remove("Reference");
					extendedFieldRender.setSeqNo(Integer.valueOf(String.valueOf(extFieldMap.get("SeqNo"))));
					extFieldMap.remove("SeqNo");
					extendedFieldRender.setVersion(Integer.valueOf(String.valueOf(extFieldMap.get("Version"))));
					extFieldMap.remove("Version");
					extendedFieldRender.setLastMntOn((Timestamp) extFieldMap.get("LastMntOn"));
					extFieldMap.remove("LastMntOn");
					extendedFieldRender.setLastMntBy(Long.valueOf(String.valueOf(extFieldMap.get("LastMntBy"))));
					extFieldMap.remove("LastMntBy");
					extendedFieldRender
							.setRecordStatus(StringUtils.equals(String.valueOf(extFieldMap.get("RecordStatus")), "null")
									? "" : String.valueOf(extFieldMap.get("RecordStatus")));
					extFieldMap.remove("RecordStatus");
					extendedFieldRender
							.setRoleCode(StringUtils.equals(String.valueOf(extFieldMap.get("RoleCode")), "null") ? ""
									: String.valueOf(extFieldMap.get("RoleCode")));
					extFieldMap.remove("RoleCode");
					extendedFieldRender
							.setNextRoleCode(StringUtils.equals(String.valueOf(extFieldMap.get("NextRoleCode")), "null")
									? "" : String.valueOf(extFieldMap.get("NextRoleCode")));
					extFieldMap.remove("NextRoleCode");
					extendedFieldRender.setTaskId(StringUtils.equals(String.valueOf(extFieldMap.get("TaskId")), "null")
							? "" : String.valueOf(extFieldMap.get("TaskId")));
					extFieldMap.remove("TaskId");
					extendedFieldRender
							.setNextTaskId(StringUtils.equals(String.valueOf(extFieldMap.get("NextTaskId")), "null")
									? "" : String.valueOf(extFieldMap.get("NextTaskId")));
					extFieldMap.remove("NextTaskId");
					extendedFieldRender
							.setRecordType(StringUtils.equals(String.valueOf(extFieldMap.get("RecordType")), "null")
									? "" : String.valueOf(extFieldMap.get("RecordType")));
					extFieldMap.remove("RecordType");
					extendedFieldRender.setWorkflowId(Long.valueOf(String.valueOf(extFieldMap.get("WorkflowId"))));
					extFieldMap.remove("WorkflowId");
					extendedFieldRender.setMapValues(extFieldMap);

					extendedFieldRenderList.add(extendedFieldRender);
				}
			}
		}
		return extendedFieldRenderList;
	}

	public void createTab(Tabs tabs, Tabpanels tabPanels) {
		
		if (tabs.getFellowIfAny("Tab" + this.extendedFieldHeader.getModuleName() + this.extendedFieldHeader.getSubModuleName()) != null) {
			Tab tab = (Tab) tabs.getFellow("Tab" + this.extendedFieldHeader.getModuleName() + this.extendedFieldHeader.getSubModuleName());
			tab.close();
		}

		tab = new Tab(this.extendedFieldHeader.getModuleName() + this.extendedFieldHeader.getSubModuleName());
		tab.setId("Tab" + this.extendedFieldHeader.getModuleName() + this.extendedFieldHeader.getSubModuleName());
		tabs.appendChild(tab);

		tabpanel = new Tabpanel();
		tabpanel.setId("TabPanel" + this.extendedFieldHeader.getModuleName() + this.extendedFieldHeader.getSubModuleName());
		tabpanel.setStyle("overflow:auto");
		tabpanel.setHeight("100%");
		tabpanel.setParent(tabPanels);
		// it store all tabpanel id (for pdf extraction)
		tabPanelsMap.put("TabPanel" + this.extendedFieldHeader.getModuleName() + this.extendedFieldHeader.getSubModuleName(), tabpanel);
	}
	
	public void createEnquiryTab(Tabpanel tabPanel) {
		
//		Tabbox tabbox=new Tabbox();
//		tab = new Tab(this.extendedFieldHeader.getModuleName() + this.extendedFieldHeader.getSubModuleName());
//		tab.setId("Tab" + this.extendedFieldHeader.getModuleName() + this.extendedFieldHeader.getSubModuleName());
//		Tabs tabs=new Tabs();
//		tabs.appendChild(tab);
//		tabbox.appendChild(tabs);
//		Tabpanels tabPanels=new Tabpanels();
//		tabbox.appendChild(tabPanels);
		tabpanel = tabPanel;
		tabpanel.setId("TabPanel" + this.extendedFieldHeader.getModuleName() + this.extendedFieldHeader.getSubModuleName());
		tabpanel.setStyle("overflow:auto");
		tabpanel.setHeight("100%");
//		tabpanel.setParent(tabPanels);
		// it store all tabpanel id (for pdf extraction)
//		tabPanel.appendChild(tabbox);
		tabPanelsMap.put("TabPanel" + this.extendedFieldHeader.getModuleName() + this.extendedFieldHeader.getSubModuleName(), tabpanel);
	}
	
	public void fillcomponentData(Map<String, Object> compopnentData, String tabPanelId, boolean isDelete) {
		logger.debug(Literal.ENTERING);
		Tabpanel pdfExtractionPanel = null;

		if (!tabPanelsMap.isEmpty() && tabPanelsMap.size() > 0) {
			for (Map.Entry<String, Object> entry : tabPanelsMap.entrySet()) {
				String key = entry.getKey();
				if (key.equals(tabPanelId)) {
					pdfExtractionPanel = (Tabpanel) tabPanelsMap.get(key);
				}
			}
		}
		if (pdfExtractionPanel != null && compopnentData.size()>0) {
			setcomponentData(compopnentData, pdfExtractionPanel, isDelete);
		}
		logger.debug(Literal.LEAVING);
	}

	private void setcomponentData(Map<String, Object> compopnentData, Tabpanel panel, boolean isDelete) {

		for (Component component : panel.getFellows()) {
			String key = component.getId().replace("ad_", "");
			if (compopnentData.containsKey(key)) {
				Object value = compopnentData.get(key);
				if (component instanceof Textbox) {
					Textbox textbox = (Textbox) component;
					if (isDelete) {
						textbox.setText("");
					} else {
						textbox.setText(String.valueOf(value));
					}
				} else if (component instanceof Uppercasebox) {
					Uppercasebox uppercasebox = (Uppercasebox) component;
					if (isDelete) {
						uppercasebox.setText("");
					} else {
						uppercasebox.setText(String.valueOf(value));
					}
				} else if (component instanceof Datebox) {
					Datebox datebox = (Datebox) component;
					if (isDelete) {
						datebox.setValue(null);
					} else {
						datebox.setValue((Date) value);
					}
				} else if (component instanceof Timebox) {
					Timebox timebox = (Timebox) component;
					if (isDelete) {
						timebox.setText("");
					} else {
						timebox.setText(String.valueOf(value));
					}
				} else if (component instanceof Decimalbox) {
					Decimalbox decimalbox = (Decimalbox) component;
					if (isDelete) {
						decimalbox.setText("");
					} else {
						decimalbox.setText(String.valueOf(value));
					}
				} else if (component instanceof CurrencyBox) {
					CurrencyBox currencyBox = (CurrencyBox) component;
					if (isDelete) {
						currencyBox.setValue("");
					} else {
						currencyBox.setValue(String.valueOf(value));
					}
				} else if (component instanceof Combobox) {
					Combobox combobox = (Combobox) component;
					if (isDelete) {
						combobox.setValue("");
					} else {
						combobox.setValue(String.valueOf(value));
					}
				} else if (component instanceof Bandbox) {
					Bandbox bandbox = (Bandbox) component;
					if (isDelete) {
						bandbox.setValue("");
					} else {
						bandbox.setValue(String.valueOf(value));
					}
				} else if (component instanceof ExtendedCombobox) {
					ExtendedCombobox extendedCombobox = (ExtendedCombobox) component;
					if (isDelete) {
						extendedCombobox.setValue("");
					} else {
						extendedCombobox.setValue(String.valueOf(value));
					}
				} else if (component instanceof RateBox) {
					RateBox rateBox = (RateBox) component;
					if (isDelete) {
						rateBox.setBaseValue("");
					} else {
						rateBox.setBaseValue(String.valueOf(value));
					}
				} else if (component instanceof Checkbox) {
					Checkbox checkbox = (Checkbox) component;
					if (isDelete) {
						checkbox.setValue("");
					} else {
						checkbox.setValue(String.valueOf(value));
					}
				} else if (component instanceof Intbox) {
					Intbox intbox = (Intbox) component;
					if (isDelete) {
						intbox.setValue(0);
					} else {
						intbox.setValue(Integer.valueOf(String.valueOf(value)));
					}
				} else if (component instanceof Longbox) {
					Longbox longbox = (Longbox) component;
					if (isDelete) {
						longbox.setValue(0l);
					} else {
						longbox.setValue(Long.valueOf(String.valueOf(value)));
					}
				} else if (component instanceof Radiogroup) {
				} else if (component instanceof AccountSelectionBox) {
					AccountSelectionBox accountSelectionBox = (AccountSelectionBox) component;
					if (isDelete) {
						accountSelectionBox.setValue("");
					} else {
						accountSelectionBox.setValue(String.valueOf(value));
					}
				} else if (component instanceof FrequencyBox) {
					FrequencyBox frequencyBox = (FrequencyBox) component;
					if (isDelete) {
						frequencyBox.setValue("");
					} else {
						frequencyBox.setValue(String.valueOf(value));
					}
				}
			}
		}
	}

	/**
	 * Method for deAllocating the ExtendedFields Authorities.
	 * 
	 */
	public void deAllocateAuthorities() {
		if (getUserWorkspace() != null) {
			String pageName = PennantApplicationUtil.getExtendedFieldPageName(extendedFieldHeader);
			if (StringUtils.isNotBlank(pageName)) {
				getUserWorkspace().deAllocateAuthorities(pageName);
			}
		}
	}


	/**
	 * @param extendedFieldHeader
	 *            the extendedFieldHeader to set
	 */
	public void setExtendedFieldHeader(ExtendedFieldHeader extendedFieldHeader) {
		this.extendedFieldHeader = extendedFieldHeader;
	}

	/**
	 * @param extendedFieldRender
	 *            the extendedFieldRender to set
	 */
	public void setExtendedFieldRender(ExtendedFieldRender extendedFieldRender) {
		this.extendedFieldRender = extendedFieldRender;
	}

	/**
	 * @param generator
	 *            the generator to set
	 */
	public void setGenerator(ExtendedFieldsGenerator generator) {
		this.generator = generator;
	}

	/**
	 * @param ccyFormat
	 *            the ccyFormat to set
	 */
	public void setCcyFormat(int ccyFormat) {
		this.ccyFormat = ccyFormat;
	}

	/**
	 * @param enqiryModule
	 *            the enqiryModule to set
	 */
	public void setEnqiryModule(boolean enqiryModule) {
		if (enqiryModule) {
			this.isReadOnly = enqiryModule;
		}
	}

	/**
	 * @param isReadOnly
	 *            the isReadOnly to set
	 */
	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	/**
	 * @param tab
	 *            the tab to set
	 */
	public void setTab(Tab tab) {
		this.tab = tab;
	}

	/**
	 * @param tabpanel
	 *            the tabpanel to set
	 */
	public void setTabpanel(Tabpanel tabpanel) {
		this.tabpanel = tabpanel;
	}

	/**
	 * @param isNewRecord
	 *            the isNewRecord to set
	 */
	public void setNewRecord(boolean isNewRecord) {
		this.isNewRecord = isNewRecord;
	}

	/**
	 * @param scriptValidationService the scriptValidationService to set
	 */
	public static void setScriptValidationService(ScriptValidationService scriptValidationService) {
		ExtendedFieldCtrl.scriptValidationService = scriptValidationService;
	}

	/**
	 * @param extFieldConfigService the extFieldConfigService to set
	 */
	public static void setExtFieldConfigService(ExtFieldConfigService extFieldConfigService) {
		ExtendedFieldCtrl.extFieldConfigService = extFieldConfigService;
	}

	/**
	 * @param extendedFieldRenderDAO the extendedFieldRenderDAO to set
	 */
	public static void setExtendedFieldRenderDAO(ExtendedFieldRenderDAO extendedFieldRenderDAO) {
		ExtendedFieldCtrl.extendedFieldRenderDAO = extendedFieldRenderDAO;
	}

	public Window getWindow() {
		return window;
	}

	public void setWindow(Window window) {
		this.window = window;
	}

	public void setTabHeight(int tabHeight) {
		this.tabHeight = tabHeight;
	}

	public ExtendedFieldHeader getExtendedFieldHeader() {
		return extendedFieldHeader;
	}
	
	public UserWorkspace getUserWorkspace() {
		return userWorkspace;
	}

	public void setUserWorkspace(UserWorkspace userWorkspace) {
		this.userWorkspace = userWorkspace;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
}
