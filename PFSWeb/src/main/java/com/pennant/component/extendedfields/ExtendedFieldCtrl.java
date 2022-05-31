package com.pennant.component.extendedfields;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldExtension;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.service.collateral.impl.ScriptValidationService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.staticparms.ExtFieldConfigService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class ExtendedFieldCtrl {
	private static final Logger logger = LogManager.getLogger(ExtendedFieldCtrl.class);

	private int ccyFormat;
	private int tabHeight;
	private boolean isReadOnly = false;
	private Tab parentTab;
	private Tab tab;
	private boolean isNewRecord = false;
	private Tabpanel tabpanel;
	private Window window;
	private boolean dataLoadReq = false;

	private ExtendedFieldHeader extendedFieldHeader;
	private ExtendedFieldRender extendedFieldRender;
	private ExtendedFieldsGenerator generator;

	private static ScriptValidationService scriptValidationService;
	private static ExtFieldConfigService extFieldConfigService;
	private static ExtendedFieldRenderDAO extendedFieldRenderDAO;
	private static Map<String, Object> tabPanelsMap = new HashMap<>();
	private static String REFERENCE = "Reference";
	private static String SEQNO = "SeqNo";
	private static String VERSION = "Version";
	private static String LASTMNTON = "LastMntOn";
	private static String LASTMNTBY = "LastMntBy";
	private static String RECORDSTATUS = "RecordStatus";
	private static String ROLECODE = "RoleCode";
	private static String NEXTROLECODE = "NextRoleCode";
	private static String TASKID = "TaskId";
	private static String NEXTTASKID = "NextTaskId";
	private static String RECORDTYPE = "RecordType";
	private static String WORKFLOWID = "WorkflowId";
	private static String INSTRUCTIONUID = "InstructionUID";
	private UserWorkspace userWorkspace;
	private String userRole;
	private boolean overflow;
	private static ExtendedFieldDetailsService extendedFieldDetailsService;

	private boolean appendActivityLog = false;
	private boolean extendedFieldExtnt = false;

	private List<Object> finBasicDetails = new ArrayList<>();
	private ExtendedFieldExtension extendedFieldExtension;

	/**
	 * Method for Rendering the Extended field details
	 */
	public void render() throws ScriptException {
		logger.debug(Literal.ENTERING);
		renderData();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Setting the multiple objects into bindings for setting the default values.
	 * 
	 * @param objectList
	 * @throws ScriptException
	 */
	public void render(List<Object> objectList) throws ScriptException {
		logger.debug(Literal.ENTERING);
		scriptValidationService.setObjectList(objectList);
		renderData();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Rendering data
	 * 
	 * @throws ScriptException
	 */
	private void renderData() throws ScriptException {
		logger.debug(Literal.ENTERING);
		// Extended Field Details auto population / Rendering into Screen
		this.generator = new ExtendedFieldsGenerator();
		this.generator.setTabpanel(tabpanel);
		this.generator.setRowWidth(220);
		this.generator.setCcyFormat(this.ccyFormat);
		this.generator.setReadOnly(this.isReadOnly);
		this.generator.setWindow(window);
		this.generator.setTabHeight(tabHeight);
		this.generator.setOverflow(overflow);
		this.generator.setUserWorkspace(userWorkspace);
		this.generator.setUserRole(userRole);
		this.generator.setExtendedFieldDetailsService(getExtendedFieldDetailsService());
		this.generator.setAppendActivityLog(isAppendActivityLog());
		this.generator.setFinHeaderList(finBasicDetails);
		this.generator.setSeqNo(extendedFieldRender.getSeqNo());
		this.generator.setInstructionUID(extendedFieldRender.getInstructionUID());

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
		if (getUserWorkspace() != null) {
			String pageName = PennantApplicationUtil.getExtendedFieldPageName(extendedFieldHeader);
			if (StringUtils.isNotBlank(pageName)) {
				getUserWorkspace().allocateAuthorities(pageName, getUserRole());
			}
		}
		// get pre-validation script if record is new
		if (StringUtils.trimToNull(this.extendedFieldHeader.getPreValidation()) != null) {
			ScriptErrors defaults = scriptValidationService
					.setPreValidationDefaults(this.extendedFieldHeader.getPreValidation(), fieldValuesMap);

			// Overriding Default values
			List<ScriptError> defaultList = defaults.getAll();
			for (int i = 0; i < defaultList.size(); i++) {
				ScriptError dftKeyValue = defaultList.get(i);
				try {
					// setting the default values to the extended field for pre-scripting
					ExtendedFieldDetail detail = getFieldDetail(dftKeyValue.getProperty(),
							extendedFieldHeader.getExtendedFieldDetails());
					if (detail != null && detail.isValFromScript()) {
						detail.setFieldList(dftKeyValue.getValue());
						if (fieldValuesMap.containsKey(detail.getFieldName())) {
							// defaults should be populated when a new record is open other wise DB value need to be
							// displayed
							String value = fieldValuesMap.get(detail.getFieldName()).toString();
							if ("#".equals(value) || "0".equals(value) || StringUtils.isBlank(value)) {// combobox,numeric,text
								fieldValuesMap.put(detail.getFieldName(), dftKeyValue.getValue());
							}
						}
					} else if (fieldValuesMap.containsKey(dftKeyValue.getProperty())) {
						fieldValuesMap.put(dftKeyValue.getProperty(), dftKeyValue.getValue());
					}
				} catch (Exception e) {
					logger.debug(Literal.EXCEPTION + "Error while setting default values");
				}
			}
		}
		if (fieldValuesMap != null) {
			generator.setFieldValueMap((Map<String, Object>) fieldValuesMap);
		}
		try {
			generator.renderWindow(this.extendedFieldHeader, this.isNewRecord);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	public ExtendedFieldRender save(boolean validationReq) throws ParseException {
		logger.debug(Literal.ENTERING);
		return save(validationReq, null);
	}

	/**
	 * Method for saving the Extended field details
	 * 
	 * @return ExtendedFieldRender extendedFieldRender
	 */
	public ExtendedFieldRender save(boolean validationReq, CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);

		if (this.extendedFieldHeader == null) {
			return null;
		}

		if (this.parentTab != null) {
			generator.setParentTab(parentTab);
		}

		// If Validation Required is false, then No need to fetch data with applying constraints
		boolean readOnly = this.isReadOnly;
		if (!validationReq) {
			readOnly = true;
		}

		Map<String, Object> mapValues = generator.doSave(this.extendedFieldHeader.getExtendedFieldDetails(), readOnly);
		this.extendedFieldRender.setMapValues(mapValues);
		this.extendedFieldRender.setTypeCode(this.extendedFieldHeader.getSubModuleName());
		mapValues.put("cd", customerDetails);

		// Post Validations for the Extended fields
		if (!readOnly) {
			if (StringUtils.trimToNull(extendedFieldHeader.getPostValidation()) != null) {
				ScriptErrors postValidationErrors = scriptValidationService
						.getPostValidationErrors(extendedFieldHeader.getPostValidation(), mapValues);
				showErrorDetails(postValidationErrors);
			}
		}
		mapValues.remove("cd");

		if (isNewRecord) {
			this.extendedFieldRender.setSeqNo(1);
		}

		logger.debug(Literal.LEAVING);
		return extendedFieldRender;
	}

	/**
	 * Method for Showing UI Post validation Errors
	 * 
	 * @param postValidationErrors
	 */
	public void showErrorDetails(ScriptErrors postValidationErrors) {
		logger.debug(Literal.ENTERING);

		List<ScriptError> errorList = postValidationErrors.getAll();
		if (errorList == null || errorList.isEmpty()) {
			return;
		}
		List<ExtendedFieldDetail> notInputElements = new ArrayList<>();
		Map<ExtendedFieldDetail, WrongValueException> wveMap = new HashMap<>();
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

		generator.showErrorDetails(wveMap, compList, notInputElements);
		logger.debug(Literal.LEAVING);
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
	 * Method Getting the extended field header details
	 * 
	 * @param String module
	 * @param String subModule
	 **/
	public ExtendedFieldHeader getExtendedFieldHeader(String module, String subModule, String event) {
		ExtendedFieldHeader extendedFieldHeader = extFieldConfigService.getApprovedExtendedFieldHeaderByModule(module,
				subModule, event);
		this.extendedFieldHeader = extendedFieldHeader;
		return extendedFieldHeader;
	}

	/**
	 * Method Getting the extended field header details
	 * 
	 * @param String module
	 * @param String subModule
	 **/
	public ExtendedFieldHeader getExtendedFieldHeader(String module, String subModule) {
		ExtendedFieldHeader extendedFieldHeader = extFieldConfigService.getApprovedExtendedFieldHeaderByModule(module,
				subModule, null);
		this.extendedFieldHeader = extendedFieldHeader;
		return extendedFieldHeader;
	}

	/**
	 * Method Getting the extended field header details
	 * 
	 * @param String module
	 * @param String subModule
	 **/
	public ExtendedFieldHeader getExtendedFieldHeader(String module, String subModule, int extendedType) {
		ExtendedFieldHeader extendedFieldHeader = extFieldConfigService.getApprovedExtendedFieldHeaderByModule(module,
				subModule, extendedType);
		this.extendedFieldHeader = extendedFieldHeader;
		return extendedFieldHeader;
	}

	/**
	 * Method Getting the extended field render details
	 * 
	 * @param String reference
	 **/
	public ExtendedFieldRender getExtendedFieldRender(String reference) {
		String tableName = getTableName();

		ExtendedFieldRender extendedFieldRender = getExtRenderData(reference, tableName, TableType.VIEW.getSuffix());

		this.extendedFieldRender = extendedFieldRender;

		return extendedFieldRender;
	}

	/**
	 * Method Getting the extended field render details
	 * 
	 * @param String reference
	 * @param String tableName
	 **/
	public ExtendedFieldRender getExtendedFieldRender(String reference, String tableName) {
		ExtendedFieldRender extendedFieldRender = getExtRenderData(reference, tableName, TableType.VIEW.getSuffix());

		this.extendedFieldRender = extendedFieldRender;

		return extendedFieldRender;
	}

	public ExtendedFieldRender getExtendedFieldRender(String reference, long instructionUID) {

		String tableName = getTableName();

		ExtendedFieldRender extendedFieldRender = getExtRenderData(reference, instructionUID, tableName,
				TableType.VIEW.getSuffix());

		this.extendedFieldRender = extendedFieldRender;

		return extendedFieldRender;
	}

	/**
	 * Method Getting the extended field render details
	 * 
	 * @param String reference
	 * @param String tableName
	 **/
	public ExtendedFieldRender getExtendedFieldRender(String reference, String tableName, String type) {

		ExtendedFieldRender extendedFieldRender = getExtRenderData(reference, tableName, type);

		this.extendedFieldRender = extendedFieldRender;

		return extendedFieldRender;
	}

	/**
	 * Method Getting the extended field render details
	 * 
	 * @param String reference
	 * @param String tableName
	 **/
	public List<ExtendedFieldRender> getExtendedFieldRenderList(String reference, String tableName, String type) {

		List<ExtendedFieldRender> renderList = new ArrayList<>();
		List<Map<String, Object>> extendedMapValues = extendedFieldRenderDAO.getExtendedFieldMap(reference, tableName,
				type);

		if (CollectionUtils.isEmpty(extendedMapValues)) {
			return renderList;
		}

		for (Map<String, Object> extFieldMap : extendedMapValues) {
			ExtendedFieldRender extendedFieldRender = new ExtendedFieldRender();
			if (extFieldMap != null) {
				modifyMapData(extFieldMap, extendedFieldRender);
				extendedFieldRender.setMapValues(extFieldMap);
				renderList.add(extendedFieldRender);
			}
		}
		return renderList;
	}

	/**
	 * Method Getting the extended field render details
	 * 
	 * @param String reference
	 * @param String tableName
	 **/
	public List<ExtendedFieldRender> getVerificationExtendedFieldsList(long id, String tableName, String type) {

		List<ExtendedFieldRender> renderList = new ArrayList<ExtendedFieldRender>();

		List<Map<String, Object>> extendedMapValues = extendedFieldRenderDAO.getExtendedFieldMap(id, tableName, type);

		if (CollectionUtils.isEmpty(extendedMapValues)) {
			return renderList;
		}

		for (Map<String, Object> extFieldMap : extendedMapValues) {
			ExtendedFieldRender extendedFieldRender = new ExtendedFieldRender();
			if (extFieldMap != null) {
				modifyMapData(extFieldMap, extendedFieldRender);
				extendedFieldRender.setMapValues(extFieldMap);
				renderList.add(extendedFieldRender);
			}
		}
		return renderList;
	}

	public void createTab(Tabs tabs, Tabpanels tabPanels, String height) {

		String module = this.extendedFieldHeader.getModuleName() + this.extendedFieldHeader.getSubModuleName();
		String tabId = "Tab" + module;

		if (tabs.getFellowIfAny(tabId) != null) {
			Tab tab = (Tab) tabs.getFellow(tabId);
			tab.close();
		}

		tab = new Tab(module);
		tab.setId(tabId);
		tabs.appendChild(tab);

		tabpanel = new Tabpanel();
		tabpanel.setId("TabPanel" + module);
		tabpanel.setStyle("overflow:auto");
		tabpanel.setHeight(height);
		tabpanel.setParent(tabPanels);
		tabPanelsMap.put("TabPanel" + module, tabpanel);
	}

	public void createTab(Tabs tabs, Tabpanels tabPanels, String height, boolean overflow) {
		this.overflow = overflow;

		createTab(tabs, tabPanels, height);
	}

	public void createTab(Tabs tabs, Tabpanels tabPanels) {
		createTab(tabs, tabPanels, "100%");
	}

	public void createEnquiryTab(Tabpanel tabPanel) {
		String module = this.extendedFieldHeader.getModuleName() + this.extendedFieldHeader.getSubModuleName();
		tabpanel = tabPanel;
		tabpanel.setId("TabPanel" + module);
		tabpanel.setStyle("overflow:auto");
		tabpanel.setHeight("100%");
		tabPanelsMap.put("TabPanel" + module, tabpanel);
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
		if (pdfExtractionPanel != null && compopnentData.size() > 0) {
			setcomponentData(compopnentData, pdfExtractionPanel, isDelete);
		}
		logger.debug(Literal.LEAVING);
	}

	public void removeTab(Tabs tabs) {
		logger.debug(Literal.ENTERING);
		if (this.extendedFieldHeader == null) {
			return;
		}
		String module = this.extendedFieldHeader.getModuleName() + this.extendedFieldHeader.getSubModuleName();
		String tabId = "Tab" + module;

		if (tabs.getFellowIfAny(tabId) != null) {
			Tab tab = (Tab) tabs.getFellow(tabId);
			tab.close();
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
				} else if (component instanceof AccountSelectionBox || component instanceof Radiogroup) {
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
	 * Getting the Extendedfield details
	 * 
	 * @param fileldName
	 * @return
	 */
	private ExtendedFieldDetail getFieldDetail(String fileldName, List<ExtendedFieldDetail> extendedFieldDetails) {
		for (ExtendedFieldDetail detail : extendedFieldDetails) {
			if (detail.getFieldName().equals(fileldName)) {
				return detail;
			}
		}
		return null;
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

	private ExtendedFieldRender getExtRenderData(String reference, String tableName, String tableType) {
		ExtendedFieldRender extendedFieldRender = new ExtendedFieldRender();

		if (this.dataLoadReq) {
			extendedFieldRender
					.setSeqNo(extendedFieldRenderDAO.getMaxSeq(reference, tableName, TableType.VIEW.getSuffix()) + 1);
			return extendedFieldRender;
		}

		Map<String, Object> extFieldMap = null;
		if (isExtendedFieldExtnt()) {
			extFieldMap = extendedFieldRenderDAO.getExtendedField(reference, extendedFieldExtension.getInstructionUID(),
					tableName, tableType);
		} else {
			extFieldMap = extendedFieldRenderDAO.getExtendedField(reference, tableName, tableType);
		}

		if (extFieldMap != null) {
			modifyMapData(extFieldMap, extendedFieldRender);
			extendedFieldRender.setMapValues(extFieldMap);
		}

		return extendedFieldRender;
	}

	private ExtendedFieldRender getExtRenderData(String reference, long instructionUid, String tableName,
			String tableType) {
		ExtendedFieldRender extendedFieldRender = new ExtendedFieldRender();

		if (this.dataLoadReq) {
			extendedFieldRender
					.setSeqNo(extendedFieldRenderDAO.getMaxSeq(reference, tableName, TableType.VIEW.getSuffix()) + 1);

			return extendedFieldRender;
		}

		Map<String, Object> extFieldMap = extendedFieldRenderDAO.getExtendedField(reference, instructionUid, tableName,
				tableType);

		if (extFieldMap != null) {
			modifyMapData(extFieldMap, extendedFieldRender);
			extendedFieldRender.setMapValues(extFieldMap);
		}
		return extendedFieldRender;
	}

	private void modifyMapData(Map<String, Object> extFieldMap, ExtendedFieldRender extendedFieldRender) {

		extendedFieldRender.setReference(getStringVal(extFieldMap, REFERENCE));

		extendedFieldRender.setSeqNo(getIntVal(extFieldMap, SEQNO));

		extendedFieldRender.setVersion(getIntVal(extFieldMap, VERSION));

		if (extFieldMap.containsKey(INSTRUCTIONUID)) {
			extendedFieldRender.setInstructionUID(getLongVal(extFieldMap, INSTRUCTIONUID));
		}

		extendedFieldRender.setLastMntOn(getTimeStampVal(extFieldMap, LASTMNTON));

		extendedFieldRender.setLastMntBy(getLongVal(extFieldMap, LASTMNTBY));

		String rcdStatus = getStringVal(extFieldMap, RECORDSTATUS);
		extendedFieldRender.setRecordStatus(StringUtils.equals(rcdStatus, "null") ? "" : rcdStatus);

		String roleCode = getStringVal(extFieldMap, ROLECODE);
		extendedFieldRender.setRoleCode(StringUtils.equals(roleCode, "null") ? "" : roleCode);

		String nextRoleCode = getStringVal(extFieldMap, NEXTROLECODE);
		extendedFieldRender.setNextRoleCode(StringUtils.equals(nextRoleCode, "null") ? "" : nextRoleCode);

		String taskId = getStringVal(extFieldMap, TASKID);
		extendedFieldRender.setTaskId(StringUtils.equals(taskId, "null") ? "" : taskId);

		String nextTaskId = getStringVal(extFieldMap, NEXTTASKID);
		extendedFieldRender.setNextTaskId(StringUtils.equals(nextTaskId, "null") ? "" : nextTaskId);

		String rcdType = getStringVal(extFieldMap, RECORDTYPE);
		extendedFieldRender.setRecordType(StringUtils.equals(rcdType, "null") ? "" : rcdType);

		extendedFieldRender.setWorkflowId(getLongVal(extFieldMap, WORKFLOWID));
	}

	public ExtendedFieldExtension getExtendedFieldExtension(String externalRef, String modeStatus, String finEvent) {
		if (isExtendedFieldExtnt()) {
			this.extendedFieldExtension = extendedFieldDetailsService.getExtendedFieldExtension(externalRef, modeStatus,
					finEvent);
		} else {
			this.extendedFieldExtension = null;
		}
		return this.extendedFieldExtension;
	}

	private String getStringVal(Map<String, Object> extFieldMap, String key) {
		String val = String.valueOf(extFieldMap.get(key));
		extFieldMap.remove(key);
		return val;
	}

	private int getIntVal(Map<String, Object> extFieldMap, String key) {
		int val = Integer.valueOf(String.valueOf(extFieldMap.get(key)));
		extFieldMap.remove(key);
		return val;
	}

	private Timestamp getTimeStampVal(Map<String, Object> extFieldMap, String key) {
		Timestamp val = (Timestamp) extFieldMap.get(key);
		extFieldMap.remove(key);
		return val;
	}

	private long getLongVal(Map<String, Object> extFieldMap, String key) {
		long val = Long.valueOf(String.valueOf(extFieldMap.get(key)));
		extFieldMap.remove(key);
		return val;
	}

	private String getTableName() {
		StringBuilder tableName = new StringBuilder();
		tableName.append(extendedFieldHeader.getModuleName());
		tableName.append("_");
		tableName.append(extendedFieldHeader.getSubModuleName());
		if (extendedFieldHeader.getEvent() != null) {
			tableName.append("_");
			tableName.append(
					StringUtils.trimToEmpty(PennantStaticListUtil.getFinEventCode(extendedFieldHeader.getEvent())));
		}
		tableName.append("_ED");
		return tableName.toString();
	}

	/**
	 * Getting the component value.
	 * 
	 * @param componentId
	 * @return
	 */
	public Component getComponent(String componentId) {
		return generator.getWindow().getFellowIfAny(componentId);
	}

	/**
	 * @param parentTab the parentTab to set
	 */
	public void setParentTab(Tab parentTab) {
		this.parentTab = parentTab;
	}

	/**
	 * @param extendedFieldHeader the extendedFieldHeader to set
	 */
	public void setExtendedFieldHeader(ExtendedFieldHeader extendedFieldHeader) {
		this.extendedFieldHeader = extendedFieldHeader;
	}

	/**
	 * @param extendedFieldRender the extendedFieldRender to set
	 */
	public void setExtendedFieldRender(ExtendedFieldRender extendedFieldRender) {
		this.extendedFieldRender = extendedFieldRender;
	}

	/**
	 * @param generator the generator to set
	 */
	public void setGenerator(ExtendedFieldsGenerator generator) {
		this.generator = generator;
	}

	/**
	 * @param ccyFormat the ccyFormat to set
	 */
	public void setCcyFormat(int ccyFormat) {
		this.ccyFormat = ccyFormat;
	}

	/**
	 * @param enqiryModule the enqiryModule to set
	 */
	public void setEnqiryModule(boolean enqiryModule) {
		if (enqiryModule) {
			this.isReadOnly = enqiryModule;
		}
	}

	/**
	 * @param isReadOnly the isReadOnly to set
	 */
	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	/**
	 * @param tab the tab to set
	 */
	public void setTab(Tab tab) {
		this.tab = tab;
	}

	/**
	 * @param tabpanel the tabpanel to set
	 */
	public void setTabpanel(Tabpanel tabpanel) {
		this.tabpanel = tabpanel;
	}

	/**
	 * @param isNewRecord the isNewRecord to set
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

	public boolean isOverflow() {
		return overflow;
	}

	public void setOverflow(boolean overflow) {
		this.overflow = overflow;
	}

	public void setValues(Map<String, Object> fieldValueMap) {
		if (extendedFieldHeader != null) {
			this.generator.setValues(extendedFieldHeader.getExtendedFieldDetails(), fieldValueMap);
		}
	}

	public ExtendedFieldsGenerator getGenerator() {
		return generator;
	}

	public ExtendedFieldDetailsService getExtendedFieldDetailsService() {
		return extendedFieldDetailsService;
	}

	public static void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		ExtendedFieldCtrl.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	public boolean isDataLoadReq() {
		return dataLoadReq;
	}

	public void setDataLoadReq(boolean dataLoadReq) {
		this.dataLoadReq = dataLoadReq;
	}

	public void setFinBasicDetails(List<Object> finBasicDetails) {
		this.finBasicDetails = finBasicDetails;
	}

	public boolean isAppendActivityLog() {
		return appendActivityLog;
	}

	public void setAppendActivityLog(boolean appendActivityLog) {
		this.appendActivityLog = appendActivityLog;
	}

	public boolean isExtendedFieldExtnt() {
		return extendedFieldExtnt;
	}

	public void setExtendedFieldExtnt(boolean extendedFieldExtnt) {
		this.extendedFieldExtnt = extendedFieldExtnt;
	}

	public ExtendedFieldExtension getExtendedFieldExtension() {
		return this.extendedFieldExtension;
	}

	public void setExtendedFieldExtension(ExtendedFieldExtension extendedFieldExtension) {
		this.extendedFieldExtension = extendedFieldExtension;
	}

}
