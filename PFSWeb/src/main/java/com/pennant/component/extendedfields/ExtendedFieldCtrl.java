package com.pennant.component.extendedfields;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;

import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.model.ScriptError;
import com.pennant.backend.model.ScriptErrors;
import com.pennant.backend.model.staticparms.ExtendedFieldHeader;
import com.pennant.backend.model.staticparms.ExtendedFieldRender;
import com.pennant.backend.service.collateral.impl.ScriptValidationService;
import com.pennant.backend.service.staticparms.ExtFieldConfigService;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtendedFieldCtrl {
	private static final Logger		logger		= Logger.getLogger(ExtendedFieldCtrl.class);

	private int								ccyFormat;
	private boolean							isReadOnly	= false;
	private Tab								parentTab;
	private Tab								tab;
	private boolean							isNewRecord	= false;
	private Tabpanel						tabpanel;

	private ExtendedFieldHeader				extendedFieldHeader;
	private ExtendedFieldRender				extendedFieldRender;
	private ExtendedFieldsGenerator			generator;

	private static ScriptValidationService	scriptValidationService;
	private static ExtFieldConfigService	extFieldConfigService;
	private static ExtendedFieldRenderDAO	extendedFieldRenderDAO;
	 

	/**
	 * Method for Rendering the Extended field details
	 */
	public void render() throws ScriptException {
		logger.debug(Literal.ENTERING);

		// Extended Field Details auto population / Rendering into Screen
		this.generator = new ExtendedFieldsGenerator();
		this.generator.setTabpanel(tabpanel);
		this.generator.setCcyFormat(this.ccyFormat);
		this.generator.setReadOnly(this.isReadOnly);
		this.tab.setLabel(extendedFieldHeader.getTabHeading());

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
			// get pre-validation script if record is new
			if (StringUtils.trimToNull(this.extendedFieldHeader.getPreValidation()) != null) {
				ScriptErrors defaults = scriptValidationService.setPreValidationDefaults(this.extendedFieldHeader.getPostValidation(), fieldValuesMap);

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
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			map = generator.doSave(this.extendedFieldHeader.getExtendedFieldDetails(), this.isReadOnly);
			this.extendedFieldRender.setMapValues(map);
			this.extendedFieldRender.setTypeCode(this.extendedFieldHeader.getSubModuleName());
		} catch (WrongValuesException wves) {
			WrongValueException[] wvea = wves.getWrongValueExceptions();
			for (int i = 0; i < wvea.length; i++) {
				wve.add(wvea[i]);
			}
		}
		// Error Detail
		showErrorDetails(wve, this.tab);

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
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		for (int i = 0; i < errorList.size(); i++) {
			ScriptError error = errorList.get(i);
//FIXME:Ganesh with the help of Satisk
//			if (rows.getFellowIfAny("ad_" + error.getProperty()) != null) {
//				Component component = rows.getFellowIfAny("ad_" + error.getProperty());
//				WrongValueException we = new WrongValueException(component, error.getValue());
//				wve.add(we);
//			}
		}

		if (wve.size() > 0) {
			if (this.parentTab != null) {
				this.parentTab.setSelected(true);
			}
			this.tab.setSelected(true);

			logger.debug("Throwing occured Errors By using WrongValueException");
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
				if (i == 0) {
					Component comp = wvea[i].getComponent();
					if (comp instanceof HtmlBasedComponent) {
						Clients.scrollIntoView(comp);
					}
				}
				logger.debug(wvea[i]);
			}
			throw new WrongValuesException(wvea);
		}
	}

	/**
	 * Method to show error details if occurred
	 * 
	 * @param Tab
	 *            tab
	 * @param ArrayList<WrongValueException>
	 *            wve
	 **/
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug(Literal.ENTERING);

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			if (this.parentTab != null) {
				this.parentTab.setSelected(true);
			}
			this.tab.setSelected(true);

			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
				if (i == 0) {
					Component comp = wvea[i].getComponent();
					if (comp instanceof HtmlBasedComponent) {
						Clients.scrollIntoView(comp);
					}
				}
				logger.debug(wvea[i]);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug(Literal.LEAVING);
	}

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

	public void createTab(Tabs tabs, Tabpanels tabPanels) {

		if (tabs.getFellowIfAny("Tab" + this.extendedFieldHeader.getTabHeading()) != null) {
			Tab tab = (Tab) tabs.getFellow("Tab" + this.extendedFieldHeader.getTabHeading());
			tab.close();
		}

		tab = new Tab(this.extendedFieldHeader.getTabHeading());
		tab.setId("Tab" + this.extendedFieldHeader.getTabHeading());
		tabs.appendChild(tab);

		tabpanel = new Tabpanel();
		tabpanel.setId("TabPanel" + this.extendedFieldHeader.getTabHeading());
		tabpanel.setStyle("overflow:auto");
		tabpanel.setHeight("100%");
		tabpanel.setParent(tabPanels);

	}

	/**
	 * @param parentTab
	 *            the parentTab to set
	 */
	public void setParentTab(Tab parentTab) {
		this.parentTab = parentTab;
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

}
