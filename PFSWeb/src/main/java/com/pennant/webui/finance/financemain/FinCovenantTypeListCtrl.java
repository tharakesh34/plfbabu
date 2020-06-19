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
 * FileName    		:  FinCovenantTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.financemain;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.legal.LegalDetail;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.service.finance.FinCovenantTypeService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.FacilityConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.collateral.collateralsetup.CollateralBasicDetailsCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineLog;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class FinCovenantTypeListCtrl extends GFCBaseCtrl<FinanceDetail> {
	private static final long serialVersionUID = 4157448822555239535L;
	private static final Logger logger = Logger.getLogger(FinCovenantTypeListCtrl.class);

	protected Window window_FinCovenantTypeList;

	protected Button btnNew_NewFinCovenantType;

	protected Listbox listBoxFinCovenantType;

	// For Dynamically calling of this Controller
	private FinanceDetail financedetail;
	private Object financeMainDialogCtrl;
	private Component parent = null;
	private Tab parentTab = null;
	private List<FinCovenantType> finCovenantTypesDetailList = new ArrayList<FinCovenantType>();
	private int ccyFormat = 0;
	private transient boolean recSave = false;
	private String roleCode = "";
	private boolean isEnquiry = false;
	private transient boolean newFinance;
	protected Groupbox finBasicdetails;
	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private String allowedRoles;

	private boolean enquiry = false;
	private boolean isNotFinanceProcess = false;
	private ArrayList<Object> headerList;
	private String moduleName;
	private LegalDetail legalDetail;
	private CollateralBasicDetailsCtrl collateralBasicDetailsCtrl;
	private Label window_FinCovenantTypeList_title;

	//File Upload functionality in Covenants
	protected Textbox fileName;
	protected Button btnFileUpload;
	protected Button btnImport;

	private transient Media media = null;
	private File file = null;

	protected transient DataEngineConfig dataEngineConfig;

	private long userId;
	private Configuration config;

	private DataEngineStatus dataEngineStatus = new DataEngineStatus(PennantConstants.COVENANTS_UPLOADBY_REFERENCE);

	private static final String COVENANTS_UPLOADBY_REFERENCE = "COVENANTS_UPLOADBY_REFERENCE";

	@Autowired(required = false)
	private transient FinCovenantFileUploadResponce finCovenantFileUploadResponce;

	private List<FinCovenantType> finCovenantTypeData;

	FinCovenantTypeService finCovenantTypeService;

	/**
	 * default constructor.<br>
	 */
	public FinCovenantTypeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinCovenantTypeList";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected CovenantType object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_FinCovenantTypeList(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinCovenantTypeList);

		try {
			if (event.getTarget().getParent() != null) {
				parent = event.getTarget().getParent();
			}

			if (arguments.containsKey("financeMainDialogCtrl")) {
				setFinanceMainDialogCtrl((Object) arguments.get("financeMainDialogCtrl"));
				this.window_FinCovenantTypeList.setTitle("");
				setNewFinance(true);
			}

			if (arguments.containsKey("enquiry")) {
				setEnquiry((boolean) arguments.get("enquiry"));
			}

			if (arguments.containsKey("enqModule")) {
				setEnquiry((boolean) arguments.get("enqModule"));
			}

			if (arguments.containsKey("roleCode")) {
				roleCode = (String) arguments.get("roleCode");
				getUserWorkspace().allocateRoleAuthorities((String) arguments.get("roleCode"), "FinCovenantTypeList");
			}

			if (arguments.containsKey("ccyFormatter")) {
				ccyFormat = Integer.parseInt(arguments.get("ccyFormatter").toString());
			}

			if (arguments.containsKey("parentTab")) {
				parentTab = (Tab) arguments.get("parentTab");
			}

			if (arguments.containsKey("isEnquiry")) {
				isEnquiry = (Boolean) arguments.get("isEnquiry");
			}

			if (arguments.containsKey("financeDetail")) {
				setFinancedetail((FinanceDetail) arguments.get("financeDetail"));
				if (getFinancedetail() != null) {
					if (getFinancedetail().getCovenantTypeList() != null) {
						setFinCovenantTypeDetailList(getFinancedetail().getCovenantTypeList());
					}
				}
			}

			if (arguments.containsKey("allowedRoles")) {
				allowedRoles = (String) arguments.get("allowedRoles");
			}

			if (arguments.containsKey("financeMainDialogCtrl")) {
				this.financeMainDialogCtrl = (Object) arguments.get("financeMainDialogCtrl");
				try {
					financeMainDialogCtrl.getClass().getMethod("setFinCovenantTypeListCtrl", this.getClass())
							.invoke(getFinanceMainDialogCtrl(), this);
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
				this.window_FinCovenantTypeList.setTitle("");
			}

			//moduleName
			if (arguments.containsKey("moduleName")) {
				this.moduleName = (String) arguments.get("moduleName");
			}
			if (arguments.containsKey("legalDetail")) {
				setLegalDetail((LegalDetail) arguments.get("legalDetail"));
				if (getLegalDetail() != null) {
					if (getLegalDetail().getCovenantTypeList() != null) {
						setFinCovenantTypeDetailList(getLegalDetail().getCovenantTypeList());
					}
				}
			}
			if (arguments.containsKey("isNotFinanceProcess")) {
				isNotFinanceProcess = (boolean) arguments.get("isNotFinanceProcess");
			}
			if (arguments.containsKey("finHeaderList")) {
				headerList = (ArrayList<Object>) arguments.get("finHeaderList");
			}

			doEdit();
			doCheckRights();
			doSetFieldProperties();
			loadConfig();
			doShowDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		if (isNotFinanceProcess) {
			window_FinCovenantTypeList_title.setVisible(false);
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		logger.debug("Leaving");
	}

	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("FinCovenantTypeList", roleCode);
		this.btnNew_NewFinCovenantType
				.setVisible(getUserWorkspace().isAllowed("FinCovenantTypeList_NewFinCovenantTypeDetail"));
		this.btnFileUpload.setVisible(getUserWorkspace().isAllowed("FinCovenantTypeList_NewFinCovenantTypeDetail"));
		this.btnImport.setVisible(getUserWorkspace().isAllowed("FinCovenantTypeList_NewFinCovenantTypeDetail"));
		if (isEnquiry()) {
			this.btnNew_NewFinCovenantType.setVisible(false);
			this.btnFileUpload.setVisible(false);
			this.btnImport.setVisible(false);
		}
		logger.debug("leaving");
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || isNewFinance()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @throws InterruptedException
	 */
	public void doShowDialog() throws InterruptedException {
		logger.debug("Entering");

		try {
			appendFinBasicDetails();
			doCheckEnquiry();
			doWriteBeanToComponents();

			try {
				doCheckEnquiry();
				doWriteBeanToComponents();

				if (arguments.containsKey("financeMainDialogCtrl")) {
					this.financeMainDialogCtrl = (Object) arguments.get("financeMainDialogCtrl");
					try {
						financeMainDialogCtrl.getClass().getMethod("setFinCovenantTypeListCtrl", this.getClass())
								.invoke(getFinanceMainDialogCtrl(), this);
					} catch (Exception e) {
						logger.error("Exception: ", e);
					}
					this.window_FinCovenantTypeList.setTitle("");
				}
				this.listBoxFinCovenantType.setHeight(borderLayoutHeight - 226 + "px");
				if (parent != null) {
					this.window_FinCovenantTypeList.setHeight(borderLayoutHeight - 75 + "px");
					parent.appendChild(this.window_FinCovenantTypeList);
				}
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param commodityHeader
	 * 
	 */
	public void doWriteBeanToComponents() {
		logger.debug("Entering ");

		doFillFinCovenantTypeDetails(getFinCovenantTypeDetailList());
		// Bug Fix for covenant at delete operation. so befImage is placed here
		// instead of at fincovenantDialogctrl
		if (CollectionUtils.isNotEmpty(getFinCovenantTypeDetailList())) {
			for (FinCovenantType covenantType : getFinCovenantTypeDetailList()) {
				FinCovenantType befImage = new FinCovenantType();
				BeanUtils.copyProperties(covenantType, befImage);
				covenantType.setBefImage(befImage);
			}
		}

		logger.debug("Leaving ");
	}

	private void doCheckEnquiry() {
		if (isEnquiry) {
			this.btnNew_NewFinCovenantType.setVisible(false);
		}
	}

	@SuppressWarnings("unchecked")
	public void onCovenantTypeValidation(Event event) {
		logger.debug("Entering" + event.toString());

		String userAction = "";
		FinanceDetail finDetail = null;
		Map<String, Object> map = new HashMap<String, Object>();
		if (event.getData() != null) {
			map = (Map<String, Object>) event.getData();
		}

		if (map.containsKey("userAction")) {
			userAction = (String) map.get("userAction");
		}

		if (map.containsKey("financeDetail")) {
			finDetail = (FinanceDetail) map.get("financeDetail");
		}

		recSave = false;
		if ("Save".equalsIgnoreCase(userAction) || "Cancel".equalsIgnoreCase(userAction)
				|| "Reject".equalsIgnoreCase(userAction) || "Resubmit".equalsIgnoreCase(userAction)) {
			recSave = true;
		}
		doClearMessage();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if (!recSave) {
				FinanceMain main = null;
				if (getFinanceMainDialogCtrl() != null) {
					try {
						if (financeMainDialogCtrl.getClass().getMethod("getFinanceMain") != null) {
							Object object = financeMainDialogCtrl.getClass().getMethod("getFinanceMain")
									.invoke(financeMainDialogCtrl);
							if (object != null) {
								main = (FinanceMain) object;
							}
						}
					} catch (Exception e) {
						logger.error("Exception: ", e);
					}
				}
				if (this.listBoxFinCovenantType.getItems() != null
						&& !this.listBoxFinCovenantType.getItems().isEmpty()) {
					if (main != null && main.getFinAmount() != null) {

					}
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		showErrorDetails(wve);

		if (finDetail != null) {
			finDetail.setCovenantTypeList(finCovenantTypesDetailList);
		}
		logger.debug("Leaving" + event.toString());
	}

	private void showErrorDetails(ArrayList<WrongValueException> wve) {
		logger.debug("Entering");

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");

			if (parentTab != null) {
				parentTab.setSelected(true);
			}

			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	public void onClick$btnNew_NewFinCovenantType(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Clients.clearWrongValue(this.btnNew_NewFinCovenantType);

		final FinCovenantType aFinCovenantType = new FinCovenantType();
		if (isNotFinanceProcess) {
			aFinCovenantType.setFinReference(getLegalDetail().getLoanReference());
			aFinCovenantType.setModule(CollateralConstants.LEGAL_MODULE);
		} else {
			aFinCovenantType.setFinReference(financedetail.getFinScheduleData().getFinReference());
		}
		aFinCovenantType.setNewRecord(true);
		aFinCovenantType.setWorkflowId(0);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finCovenantTypes", aFinCovenantType);
		map.put("ccyFormatter", ccyFormat);
		map.put("finCovenantTypesListCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", roleCode);
		map.put("module", DocumentCategories.FINANCE.getKey());
		map.put("financeMainDialogCtrl", this.financeMainDialogCtrl);
		map.put("allowedRoles", allowedRoles);
		if (isNotFinanceProcess) {
			map.put("legalDetail", getLegalDetail());
		} else {
			map.put("financeDetail", getFinancedetail());
		}
		map.put("isNotFinanceProcess", isNotFinanceProcess);
		map.put("moduleName", moduleName);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinCovenantTypeDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onFinCovenantTypeItemDoubleClicked(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Clients.clearWrongValue(this.btnNew_NewFinCovenantType);

		Listitem listitem = this.listBoxFinCovenantType.getSelectedItem();
		if (listitem != null && listitem.getAttribute("data") != null) {
			final FinCovenantType aFinCovenantType = (FinCovenantType) listitem.getAttribute("data");
			if (isDeleteRecord(aFinCovenantType)) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				aFinCovenantType.setNewRecord(false);

				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("finCovenantTypes", aFinCovenantType);
				map.put("ccyFormatter", ccyFormat);
				map.put("finCovenantTypesListCtrl", this);
				map.put("financeMainDialogCtrl", this.financeMainDialogCtrl);
				map.put("roleCode", roleCode);
				map.put("enqModule", isEnquiry());
				map.put("allowedRoles", allowedRoles);
				if (isNotFinanceProcess) {
					map.put("legalDetail", getLegalDetail());
				} else {
					map.put("financeDetail", getFinancedetail());
				}
				map.put("isNotFinanceProcess", isNotFinanceProcess);
				map.put("moduleName", moduleName);

				// call the ZUL-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinCovenantTypeDialog.zul", null,
							map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	public void doFillFinCovenantTypeDetails(List<FinCovenantType> finCovenantType) {
		logger.debug("Entering");
		this.listBoxFinCovenantType.getItems().clear();
		setFinCovenantTypeDetailList(finCovenantType);
		if (finCovenantType != null && !finCovenantType.isEmpty()) {
			for (FinCovenantType detail : finCovenantType) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(detail.getCovenantTypeDesc());
				lc.setParent(item);
				lc = new Listcell(detail.getMandRoleDesc());
				lc.setParent(item);

				Checkbox cb = new Checkbox();
				cb.setDisabled(true);
				cb.setChecked(detail.isAlwWaiver());
				lc = new Listcell();
				cb.setParent(lc);
				lc.setParent(item);

				cb = new Checkbox();
				cb.setDisabled(true);
				cb.setChecked(detail.isAlwPostpone());
				lc = new Listcell();
				cb.setParent(lc);
				lc.setParent(item);

				cb = new Checkbox();
				cb.setDisabled(true);
				cb.setChecked(detail.isAlwOtc());
				lc = new Listcell();
				cb.setParent(lc);
				lc.setParent(item);

				lc = new Listcell(detail.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(detail.getRecordType()));
				lc.setParent(item);
				item.setAttribute("data", detail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onFinCovenantTypeItemDoubleClicked");
				this.listBoxFinCovenantType.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}

	private boolean isDeleteRecord(FinCovenantType aFinCovenantType) {
		if (StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, aFinCovenantType.getRecordType())
				|| StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, aFinCovenantType.getRecordType())) {
			return true;
		}

		if (isNotFinanceProcess && !CollateralConstants.LEGAL_MODULE.equals(aFinCovenantType.getModule())
				&& !FacilityConstants.MODULE_NAME.equals(this.moduleName)) {
			return true;
		}
		return false;
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails() {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			map.put("finHeaderList", headerList);
			map.put("moduleName", moduleName);
			if (isNotFinanceProcess) {
				Executions.createComponents("/WEB-INF/pages/Collateral/CollateralSetup/CollateralBasicDetails.zul",
						this.finBasicdetails, map);
			} else {
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul",
						this.finBasicdetails, map);
			}
		} catch (Exception e) {
			logger.debug(e);
		}
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		if (isNotFinanceProcess) {
			getCollateralBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
		} else {
			getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
		}
	}

	private void loadConfig() throws Exception {
		if (config == null) {
			List<ValueLabel> menuList = new ArrayList<>();
			this.config = dataEngineConfig.getConfigurationByName(COVENANTS_UPLOADBY_REFERENCE);
			dataEngineStatus = dataEngineConfig.getLatestExecution(COVENANTS_UPLOADBY_REFERENCE);
			ValueLabel valueLabel = new ValueLabel(COVENANTS_UPLOADBY_REFERENCE, "Covenant Upload By Reference");
			menuList.add(valueLabel);
		}
	}

	public void onClick$btnImport(Event event) throws InterruptedException {
		this.btnImport.setDisabled(true);
		if (media == null) {
			MessageUtil.showError("Please upload file.");
			return;
		}

		try {
			try {
				List<DocumentType> documentData = finCovenantTypeService.getPddOtcList();
				finCovenantTypeData = finCovenantFileUploadResponce.finCovenantFileUploadResponceData(this.userId,
						dataEngineStatus, file, media, false, allowedRoles.split(";"), documentData);

				StringBuilder exceptions = new StringBuilder();
				if ("S".equals(dataEngineStatus.getStatus()) && dataEngineStatus.getDataEngineLogList() != null) {
					dataEngineStatus.getDataEngineLogList();

					for (DataEngineLog dsLog : dataEngineStatus.getDataEngineLogList()) {

						if (exceptions.length() > 0) {
							exceptions.append("\n");
						}

						exceptions.append(dsLog.getKeyId() + " " + dsLog.getReason());
					}
				}

				if (StringUtils.isNotBlank(exceptions.toString())) {
					MessageUtil.showError(exceptions.toString());
					return;
				}

				if (finCovenantTypeData != null) {
					for (int i = 0; i < finCovenantTypesDetailList.size(); i++) {
						for (int j = 0; j < finCovenantTypeData.size(); j++) {
							if (finCovenantTypesDetailList.get(i).getCovenantType()
									.equals(finCovenantTypeData.get(j).getCovenantType())) {
								throw new AppException("Covenant Type Already Exists :"
										+ finCovenantTypeData.get(j).getCovenantType());
							}
						}
					}
					finCovenantTypeData.addAll(finCovenantTypesDetailList);
					doFillFinCovenantTypeDetails(finCovenantTypeData);
				}
			} catch (Exception e) {
				MessageUtil.showError(e.getMessage());
				return;
			}
		} catch (Exception e) {
			MessageUtil.showError(e.getMessage());
			return;
		}

	}

	public void onUpload$btnFileUpload(UploadEvent event) throws Exception {
		// Clear the file name.
		this.fileName.setText("");

		// Get the media of the selected file.
		media = event.getMedia();

		if (!PennantAppUtil.uploadDocFormatValidation(media)) {
			return;
		}
		String mediaName = media.getName();

		// Get the selected configuration details.
		String prefix = config.getFilePrefixName();
		String extension = config.getFileExtension();

		// Validate the file extension.
		if (!(StringUtils.endsWithIgnoreCase(mediaName, extension))) {
			MessageUtil.showError(Labels.getLabel("invalid_file_ext", new String[] { extension }));

			media = null;
			return;
		}

		// Validate the file prefix.
		if (prefix != null && !(StringUtils.startsWith(mediaName, prefix))) {
			MessageUtil.showError(Labels.getLabel("invalid_file_prefix", new String[] { prefix }));

			media = null;
			return;
		}

		this.fileName.setText(mediaName);
		this.btnImport.setDisabled(false);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public List<FinCovenantType> getFinCovenantTypeDetailList() {
		return finCovenantTypesDetailList;
	}

	public void setFinCovenantTypeDetailList(List<FinCovenantType> finCovenantTypesDetailList) {
		this.finCovenantTypesDetailList = finCovenantTypesDetailList;
	}

	public boolean isNewFinance() {
		return newFinance;
	}

	public void setNewFinance(boolean newFinance) {
		this.newFinance = newFinance;
	}

	public void setFinancedetail(FinanceDetail financedetail) {
		this.financedetail = financedetail;
	}

	public FinanceDetail getFinancedetail() {
		return financedetail;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public CollateralBasicDetailsCtrl getCollateralBasicDetailsCtrl() {
		return collateralBasicDetailsCtrl;
	}

	public void setCollateralBasicDetailsCtrl(CollateralBasicDetailsCtrl collateralBasicDetailsCtrl) {
		this.collateralBasicDetailsCtrl = collateralBasicDetailsCtrl;
	}

	public LegalDetail getLegalDetail() {
		return legalDetail;
	}

	public void setLegalDetail(LegalDetail legalDetail) {
		this.legalDetail = legalDetail;
	}

	public boolean isEnquiry() {
		return enquiry;
	}

	public void setEnquiry(boolean enquiry) {
		this.enquiry = enquiry;
	}

	public void setDataEngineConfig(DataEngineConfig dataEngineConfig) {
		this.dataEngineConfig = dataEngineConfig;
	}

	public void setFinCovenantFileUploadResponce(FinCovenantFileUploadResponce finCovenantFileUploadResponce) {
		this.finCovenantFileUploadResponce = finCovenantFileUploadResponce;
	}

	public void setFinCovenantTypeService(FinCovenantTypeService finCovenantTypeService) {
		this.finCovenantTypeService = finCovenantTypeService;
	}

}
