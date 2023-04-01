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
 * * FileName : CarLoanDetailDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 08-10-2011 * *
 * Modified Date : 08-10-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 08-10-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.wiffinancemain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.aspose.words.SaveFormat;
import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FacilityType;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.IndicativeTermDetail;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.util.FacilityConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.component.PTCKeditor;
import com.pennant.util.AgreementEngine;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.feature.model.ModuleMapping;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/LMTMasters/CarLoanDetail/carLoanDetailDialog.zul file.
 */
public class IndicativeTermDetailDialogCtrl extends GFCBaseCtrl<IndicativeTermDetail> {
	private static final long serialVersionUID = 5058430665774376406L;
	private static final Logger logger = LogManager.getLogger(IndicativeTermDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_IndTermDetailDialog; // autowired

	protected Textbox rpsnName; // autowired
	protected ExtendedCombobox rpsnDesg; // autowired
	protected Textbox custName; // autowired
	protected Label custShrtName; // autowired
	protected Longbox custId; // autowired
	protected ExtendedCombobox facilityType; // autowired
	protected Textbox pricing; // autowired
	protected Textbox repayments; // autowired
	protected Textbox lCPeriod; // autowired
	protected Textbox usancePeriod; // autowired
	protected Checkbox securityClean; // autowired
	protected PTCKeditor securityName; // autowired
	protected Textbox utilization; // autowired
	protected PTCKeditor commission; // autowired
	protected PTCKeditor purpose; // autowired
	protected PTCKeditor guarantee; // autowired
	protected PTCKeditor covenants; // autowired
	protected PTCKeditor documentsRequired; // autowired
	protected Intbox tenorYear; // autowired
	protected Intbox tenorMonth; // autowired
	protected Textbox tenorDesc; // autowired
	protected Combobox transactionType; // autowired
	protected Textbox agentBank; // autowired
	protected Space space_AgentBank; // autowired
	protected CurrencyBox totalFacility; // autowired
	protected ExtendedCombobox totalFacilityCCY; // autowired
	protected CurrencyBox underWriting; // autowired
	protected ExtendedCombobox underWritingCCY; // autowired
	protected CurrencyBox propFinalTake; // autowired
	protected ExtendedCombobox propFinalTakeCCY; // autowired
	protected Textbox otherDetails; // autowired
	protected Space space_OtherDetails; // autowired
	protected Row row_totalFacility; // autowired
	protected Row row_underWriting; // autowired
	protected Row row_propFinalTake; // autowired
	protected Button btnGenerateTermSheet;

	protected Row row_LCPeriod;
	protected Row row_UsancePeriod;

	// For Dynamically calling of this Controller
	private Div toolbar;
	private Object financeMainDialogCtrl;
	private Tabpanel panel = null;

	private transient boolean newFinance;

	// ServiceDAOs / Domain Classes
	private Map<String, List<ErrorDetail>> overideMap = new HashMap<String, List<ErrorDetail>>();
	private transient boolean recSave = false;
	private IndicativeTermDetail indicativeTermDetail;
	private CustomerDetailsService customerDetailsService;
	private CustomerDetails customerDetails = null;
	private String userRole = "";
	private List<ValueLabel> transactionTypesList = PennantStaticListUtil.getTransactionTypesList();
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public IndicativeTermDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "IndicativeTermDetailDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected CarLoanDetail object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onCreate$window_IndTermDetailDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_IndTermDetailDialog);

		try {
			/* set components visible dependent of the users rights */

			if (event.getTarget().getParent() != null) {
				panel = (Tabpanel) event.getTarget().getParent();
			}

			if (arguments.containsKey("indicativeTermDetail")) {
				indicativeTermDetail = (IndicativeTermDetail) arguments.get("indicativeTermDetail");
			} else {
				indicativeTermDetail = new IndicativeTermDetail();
			}
			if (arguments.containsKey("financeMainDialogCtrl")) {
				this.financeMainDialogCtrl = (Object) arguments.get("financeMainDialogCtrl");

				try {
					financeMainDialogCtrl.getClass().getMethod("setIndicativeTermDetailDialogCtrl", this.getClass())
							.invoke(financeMainDialogCtrl, this);
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}

				setNewFinance(true);

				this.window_IndTermDetailDialog.setTitle("");

			}

			if (arguments.containsKey("roleCode")) {
				userRole = arguments.get("roleCode").toString();
				getUserWorkspace().allocateRoleAuthorities(userRole, "IndicativeTermDetailDialog");
			}

			ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("IndicativeTermDetail");
			if (moduleMapping.getWorkflowType() != null) {
				workFlowDetails = WorkFlowUtil.getWorkFlowDetails("IndicativeTermDetail");
				if (workFlowDetails == null) {
					setWorkFlowEnabled(false);
					this.indicativeTermDetail.setWorkflowId(0);
				} else {
					setWorkFlowEnabled(true);
					setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
					long workflowid = workFlowDetails.getId();
					indicativeTermDetail.setWorkflowId(workflowid);
				}
			}
			doLoadWorkFlow(this.indicativeTermDetail.isWorkflow(), this.indicativeTermDetail.getWorkflowId(),
					this.indicativeTermDetail.getNextTaskId());
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (isWorkFlowEnabled() && !isNewFinance()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "IndicativeTermDetailDialog");
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getIndicativeTermDetail());
		} catch (Exception e) {
			logger.debug("Exception: ", e);
			this.window_IndTermDetailDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.rpsnName.setMaxlength(50);
		this.rpsnDesg.setMaxlength(8);
		this.rpsnDesg.setMandatoryStyle(true);
		this.rpsnDesg.setModuleName("GeneralDesignation");
		this.rpsnDesg.setValueColumn("GenDesignation");
		this.rpsnDesg.setDescColumn("GenDesgDesc");
		this.rpsnDesg.setValidateColumns(new String[] { "GenDesignation" });
		this.custName.setMaxlength(100);
		this.facilityType.setMaxlength(8);
		this.facilityType.setMandatoryStyle(true);
		this.facilityType.setModuleName("FacilityType");
		this.facilityType.setValueColumn("FacilityType");
		this.facilityType.setDescColumn("FacilityDesc");
		this.facilityType.setValidateColumns(new String[] { "FacilityType" });
		this.pricing.setMaxlength(200);
		this.repayments.setMaxlength(200);
		this.lCPeriod.setMaxlength(200);
		this.usancePeriod.setMaxlength(200);
		this.utilization.setMaxlength(200);
		this.tenorYear.setMaxlength(4);
		this.tenorMonth.setMaxlength(2);
		this.tenorDesc.setMaxlength(200);
		this.agentBank.setMaxlength(200);
		this.otherDetails.setMaxlength(200);
		this.totalFacility.setMandatory(true);
		this.totalFacility.setFormat(PennantApplicationUtil
				.getAmountFormate(CurrencyUtil.getFormat(getIndicativeTermDetail().getTotalFacilityCCY())));
		this.totalFacility.setScale(CurrencyUtil.getFormat(getIndicativeTermDetail().getTotalFacilityCCY()));
		this.totalFacilityCCY.setMaxlength(3);
		this.totalFacilityCCY.setMandatoryStyle(true);
		this.totalFacilityCCY.setModuleName("Currency");
		this.totalFacilityCCY.setValueColumn("CcyCode");
		this.totalFacilityCCY.setDescColumn("CcyDesc");
		this.totalFacilityCCY.setValidateColumns(new String[] { "CcyCode" });
		this.underWriting.setMandatory(true);
		this.underWriting.setFormat(PennantApplicationUtil
				.getAmountFormate(CurrencyUtil.getFormat(getIndicativeTermDetail().getUnderWritingCCY())));
		this.underWriting.setScale(CurrencyUtil.getFormat(getIndicativeTermDetail().getUnderWritingCCY()));
		this.underWritingCCY.setMaxlength(3);
		this.underWritingCCY.setMandatoryStyle(true);
		this.underWritingCCY.setModuleName("Currency");
		this.underWritingCCY.setValueColumn("CcyCode");
		this.underWritingCCY.setDescColumn("CcyDesc");
		this.underWritingCCY.setValidateColumns(new String[] { "CcyCode" });
		this.propFinalTake.setMandatory(true);
		this.propFinalTake.setFormat(PennantApplicationUtil
				.getAmountFormate(CurrencyUtil.getFormat(getIndicativeTermDetail().getPropFinalTakeCCY())));
		this.propFinalTake.setScale(CurrencyUtil.getFormat(getIndicativeTermDetail().getPropFinalTakeCCY()));
		this.propFinalTakeCCY.setMaxlength(3);
		this.propFinalTakeCCY.setMandatoryStyle(true);
		this.propFinalTakeCCY.setModuleName("Currency");
		this.propFinalTakeCCY.setValueColumn("CcyCode");
		this.propFinalTakeCCY.setDescColumn("CcyDesc");
		this.propFinalTakeCCY.setValidateColumns(new String[] { "CcyCode" });

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving");
	}

	public void onFulfill$totalFacilityCCY(Event event) {
		logger.debug("Entering " + event.toString());
		Object dataObject = totalFacilityCCY.getObject();
		if (dataObject instanceof String) {
			this.totalFacilityCCY.setValue(dataObject.toString());
			this.totalFacilityCCY.setDescription("");
		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {
				// To Format Amount based on the currency
				this.totalFacility.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	public void onFulfill$underWritingCCY(Event event) {
		logger.debug("Entering " + event.toString());
		Object dataObject = underWritingCCY.getObject();
		if (dataObject instanceof String) {
			this.underWritingCCY.setValue(dataObject.toString());
			this.underWritingCCY.setDescription("");
		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {
				// To Format Amount based on the currency
				this.underWriting.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	public void onFulfill$propFinalTakeCCY(Event event) {
		logger.debug("Entering " + event.toString());
		Object dataObject = propFinalTakeCCY.getObject();
		if (dataObject instanceof String) {
			this.propFinalTakeCCY.setValue(dataObject.toString());
			this.propFinalTakeCCY.setDescription("");
		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {
				// To Format Amount based on the currency
				this.propFinalTake.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("IndicativeTermDetailDialog", userRole);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_IndicativeTermDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_IndicativeTermDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_IndicativeTermDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_IndicativeTermDetailDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_IndTermDetailDialog);
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnGenerateTermSheet(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		FinanceDetail detail = null;
		String finDivision = "";
		Date date = SysParamUtil.getAppDate();

		try {
			Object object = getFinanceMainDialogCtrl().getClass().getMethod("getFinanceDetail")
					.invoke(financeMainDialogCtrl);
			if (object != null) {
				detail = (FinanceDetail) object;
			}
			if (detail != null && detail.getFinScheduleData() != null
					&& detail.getFinScheduleData().getFinanceMain() != null) {
				FinanceMain main = detail.getFinScheduleData().getFinanceMain();
				if (main != null) {
					int format = CurrencyUtil.getFormat(main.getFinCcy());

					finDivision = detail.getFinScheduleData().getFinanceType().getFinDivision();
					doWriteComponentsToBean(indicativeTermDetail);

					indicativeTermDetail
							.setFinAmount(PennantApplicationUtil.amountFormate(main.getFinAmount(), format));
					indicativeTermDetail.setFinPurpose(main.getFinPurpose());
					indicativeTermDetail.setTenor(indicativeTermDetail.getTenorYear() + " Years "
							+ indicativeTermDetail.getTenorMonth() + " Months ");
					indicativeTermDetail.setFinCcy(main.getFinCcy());
					indicativeTermDetail.setAppDate(DateUtil.formatToLongDate(date));
					int tempYear = Integer.parseInt(date.toString().substring(0, 4));

					indicativeTermDetail.setAppLastYear(String.valueOf(tempYear - 1));
					indicativeTermDetail.setAppPastYear(String.valueOf(tempYear - 2));

					customerDetails = getCustomerDetailsService().getCustomerDetailsbyIdandPhoneType(main.getCustID(),
							"FAX");

					if (customerDetails.getCustomerPhoneNumList() != null
							&& customerDetails.getCustomerPhoneNumList().size() > 0) {
						indicativeTermDetail
								.setFax(customerDetails.getCustomerPhoneNumList().get(0).getPhoneNumber() == null ? ""
										: customerDetails.getCustomerPhoneNumList().get(0).getPhoneNumber());
					} else {
						indicativeTermDetail.setFax("");
					}
					if (customerDetails.getAddressList() != null && customerDetails.getAddressList().size() > 0) {
						indicativeTermDetail
								.setCity(customerDetails.getAddressList().get(0).getCustAddrCity() == null ? ""
										: customerDetails.getAddressList().get(0).getCustAddrCity());
					} else {
						indicativeTermDetail.setCity("");
					}

					if (customerDetails.getAddressList() != null && customerDetails.getAddressList().size() > 0) {
						indicativeTermDetail
								.setCountry(customerDetails.getAddressList().get(0).getCustAddrCountry() == null ? ""
										: customerDetails.getAddressList().get(0).getCustAddrCountry());
					} else {
						indicativeTermDetail.setCountry("");
					}

					if (customerDetails.getAddressList() != null && customerDetails.getAddressList().size() > 0) {
						indicativeTermDetail
								.setPoBox(customerDetails.getAddressList().get(0).getCustAddrZIP() == null ? ""
										: customerDetails.getAddressList().get(0).getCustAddrZIP());
					} else {
						indicativeTermDetail.setPoBox("");
					}
				}
			}
		} catch (Exception e) {
			logger.debug(e);
			if (e instanceof WrongValuesException) {
				throw e;
			}
		}

		try {
			String sheetName = "";
			if (StringUtils.isNotBlank(indicativeTermDetail.getUsancePeriod())
					&& StringUtils.isNotBlank(indicativeTermDetail.getLCPeriod())) {
				sheetName = "IndicativeTermSheet_LC_Usance.docx";
			} else if (StringUtils.isNotBlank(indicativeTermDetail.getUsancePeriod())) {
				sheetName = "IndicativeTermSheet_Usance.docx";
			} else if (StringUtils.isNotBlank(indicativeTermDetail.getLCPeriod())) {
				sheetName = "IndicativeTermSheet_LC.docx";
			} else {
				sheetName = "IndicativeTermSheet.docx";
			}

			if (StringUtils.isNotBlank(finDivision)) {
				sheetName = finDivision + "_" + sheetName;
			}

			AgreementEngine engine = new AgreementEngine();
			String refNo = detail.getFinScheduleData().getFinanceMain().getFinReference();
			String reportName = refNo + "_TermSheet.docx";
			engine.setTemplate(sheetName);
			// engine.loadTemplateWithFontSize(11);
			engine.mergeFields(indicativeTermDetail);
			byte[] docData = engine.getDocumentInByteArray(SaveFormat.PDF);
			showDocument(docData, this.window_IndTermDetailDialog, reportName, SaveFormat.DOCX);
			engine.close();
			engine = null;

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Event for checking validation for dynamically calling condition
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onAssetValidation(Event event) {
		logger.debug("Entering" + event.toString());
		String userAction = "";
		Map<String, Object> map = new HashMap<String, Object>();
		if (event.getData() != null) {
			map = (Map<String, Object>) event.getData();
		}
		if (map.containsKey("userAction")) {
			userAction = (String) map.get("userAction");
		}
		doClearMessage();
		recSave = false;
		if ("Save".equalsIgnoreCase(userAction) && !map.containsKey("agreement")) {
			recSave = true;
		} else {
			doSetValidation();
			doSetLOVValidation();
		}
		doWriteComponentsToBean(getIndicativeTermDetail());
		if (StringUtils.isBlank(getIndicativeTermDetail().getRecordType())) {
			getIndicativeTermDetail().setVersion(getIndicativeTermDetail().getVersion() + 1);
			getIndicativeTermDetail().setRecordType(PennantConstants.RECORD_TYPE_NEW);
			getIndicativeTermDetail().setNewRecord(true);
		}

		try {
			getFinanceMainDialogCtrl().getClass().getMethod("setIndicativeTermDetail", IndicativeTermDetail.class)
					.invoke(getFinanceMainDialogCtrl(), this.getIndicativeTermDetail());
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Event for checking whethter data has been changed before closing
	 * 
	 * @param event
	 * @return
	 */

	public void onAssetClose(Event event) {
		logger.debug("Entering" + event.toString());
		try {
			financeMainDialogCtrl.getClass().getMethod("setAssetDataChanged", Boolean.class)
					.invoke(financeMainDialogCtrl, this.isDataChanged());
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");

		doWriteBeanToComponents(this.indicativeTermDetail.getBefImage());
		doReadOnly();

		this.btnCtrl.setInitEdit();

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param indicativeTermDetail CarLoanDetail
	 */
	public void doWriteBeanToComponents(IndicativeTermDetail indicativeTermDetail) {
		logger.debug("Entering");

		this.rpsnName.setValue(indicativeTermDetail.getRpsnName());
		this.rpsnDesg.setValue(indicativeTermDetail.getRpsnDesg());
		this.rpsnDesg.setDescription(indicativeTermDetail.getLovDescRpsnDesgName());
		this.custName.setValue(indicativeTermDetail.getLovDescCustCIF());
		this.custShrtName.setValue(indicativeTermDetail.getLovDescCustShrtName());
		this.custId.setValue(indicativeTermDetail.getCustId());
		this.facilityType.setValue(indicativeTermDetail.getFacilityType());
		this.pricing.setValue(indicativeTermDetail.getPricing());
		this.repayments.setValue(indicativeTermDetail.getRepayments());
		this.lCPeriod.setValue(indicativeTermDetail.getLCPeriod());
		this.usancePeriod.setValue(indicativeTermDetail.getUsancePeriod());
		this.securityClean.setChecked(indicativeTermDetail.isSecurityClean());
		this.securityName.setValue(indicativeTermDetail.getSecurityName());
		this.utilization.setValue(indicativeTermDetail.getUtilization());
		this.commission.setValue(indicativeTermDetail.getCommission());
		this.purpose.setValue(indicativeTermDetail.getPurpose());
		this.guarantee.setValue(indicativeTermDetail.getGuarantee());
		this.covenants.setValue(indicativeTermDetail.getCovenants());
		this.documentsRequired.setValue(indicativeTermDetail.getDocumentsRequired());
		this.tenorYear.setValue(indicativeTermDetail.getTenorYear());
		this.tenorMonth.setValue(indicativeTermDetail.getTenorMonth());
		this.tenorDesc.setValue(indicativeTermDetail.getTenorDesc());
		onCheckSecurity();
		fillComboBox(this.transactionType, indicativeTermDetail.getTransactionType(), transactionTypesList, "");
		this.agentBank.setValue(indicativeTermDetail.getAgentBank());
		this.otherDetails.setValue(indicativeTermDetail.getOtherDetails());
		this.totalFacility.setValue(CurrencyUtil.parse(indicativeTermDetail.getTotalFacility(),
				CurrencyUtil.getFormat(getIndicativeTermDetail().getTotalFacilityCCY())));
		this.totalFacilityCCY.setValue(indicativeTermDetail.getTotalFacilityCCY());
		this.underWriting.setValue(CurrencyUtil.parse(indicativeTermDetail.getUnderWriting(),
				CurrencyUtil.getFormat(getIndicativeTermDetail().getUnderWritingCCY())));
		this.underWritingCCY.setValue(indicativeTermDetail.getUnderWritingCCY());
		this.propFinalTake.setValue(CurrencyUtil.parse(indicativeTermDetail.getPropFinalTake(),
				CurrencyUtil.getFormat(getIndicativeTermDetail().getPropFinalTakeCCY())));
		this.propFinalTakeCCY.setValue(indicativeTermDetail.getPropFinalTakeCCY());

		if (indicativeTermDetail.isNewRecord()) {
			this.facilityType.setDescription("");
			this.totalFacilityCCY.setDescription("");
			this.underWritingCCY.setDescription("");
			this.propFinalTakeCCY.setDescription("");
		} else {
			this.facilityType.setDescription(indicativeTermDetail.getLovDescFacilityType());
			this.totalFacilityCCY.setDescription(CurrencyUtil.getCcyDesc(indicativeTermDetail.getTotalFacilityCCY()));
			this.underWritingCCY.setDescription(CurrencyUtil.getCcyDesc(indicativeTermDetail.getTotalFacilityCCY()));
			this.propFinalTakeCCY.setDescription(CurrencyUtil.getCcyDesc(indicativeTermDetail.getTotalFacilityCCY()));
		}
		doCheckTransactionType();
		this.recordStatus.setValue(indicativeTermDetail.getRecordStatus());
		logger.debug("Leaving");
	}

	public void doFillScheduleData(FinanceDetail detail) {
		logger.debug("Entering");

		FinanceMain main = detail.getFinScheduleData().getFinanceMain();

		String rate = PennantApplicationUtil.formatRate(main.getRepayProfitRate().doubleValue(), 2);
		String[] rateFields = new String[] { rate, PennantApplicationUtil.getLabelDesc(main.getRepayRateBasis(),
				PennantStaticListUtil.getInterestRateType(true)) };
		this.pricing.setValue(Labels.getLabel("label_IndTermDetailDialog_Pricing", rateFields));

		String[] descFields = new String[] { String.valueOf(main.getNumberOfTerms()), main.getScheduleMethod(),
				FrequencyUtil.getFrequencyDetail(main.getRepayFrq()).getFrequencyDescription(),
				DateUtil.formatToLongDate(main.getMaturityDate()) };
		this.repayments.setValue(Labels.getLabel("label_IndTermDetailDialog_Repayments", descFields));

		if (detail.getFinScheduleData().getFinanceType().isFinIsAlwMD()) {
			this.utilization.setValue(Labels.getLabel("label_IndTermDetailDialog_Utilization_MultiDisbursement"));
		} else {
			this.utilization.setValue(Labels.getLabel("label_IndTermDetailDialog_Utilization_SingleDisbursement"));
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aIndicativeTermDetail
	 */
	public void doWriteComponentsToBean(IndicativeTermDetail aIndicativeTermDetail) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			aIndicativeTermDetail.setRpsnName(this.rpsnName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndicativeTermDetail.setLovDescRpsnDesgName(this.rpsnDesg.getDescription());
			aIndicativeTermDetail.setRpsnDesg(this.rpsnDesg.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndicativeTermDetail.setLovDescCustShrtName(this.custName.getValue());
			aIndicativeTermDetail.setCustId(this.custId.longValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndicativeTermDetail.setLovDescFacilityType(this.facilityType.getDescription());
			aIndicativeTermDetail.setFacilityType(this.facilityType.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndicativeTermDetail.setPricing(this.pricing.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndicativeTermDetail.setRepayments(this.repayments.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndicativeTermDetail.setLCPeriod(this.lCPeriod.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndicativeTermDetail.setUsancePeriod(this.usancePeriod.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndicativeTermDetail.setSecurityClean(this.securityClean.isChecked());
			aIndicativeTermDetail.setSecurityName(this.securityName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndicativeTermDetail.setUtilization(this.utilization.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndicativeTermDetail.setCommission(this.commission.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndicativeTermDetail.setPurpose(this.purpose.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndicativeTermDetail.setCovenants(this.covenants.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndicativeTermDetail.setDocumentsRequired(this.documentsRequired.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndicativeTermDetail.setGuarantee(this.guarantee.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.tenorYear.isReadonly() && this.tenorYear.intValue() == 0 && this.tenorMonth.intValue() == 0) {
				this.tenorYear.setConstraint(new PTNumberValidator(
						Labels.getLabel("label_IndTermDetailDialog_tenorYear.value"), true, false));
			}
			aIndicativeTermDetail.setTenorYear(this.tenorYear.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aIndicativeTermDetail.setTenorMonth(this.tenorMonth.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (StringUtils.isNotEmpty(this.tenorDesc.getValue())) {
				aIndicativeTermDetail.setTenorDesc(this.tenorDesc.getValue());
			} else if (!this.tenorDesc.isReadonly()) {
				throw new WrongValueException(this.tenorDesc, Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_IndTermDetailDialog_tenorDesc.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if ("#".equals(getComboboxValue(this.transactionType))) {
				throw new WrongValueException(this.transactionType, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_IndTermDetailDialog_transactionType.value") }));
			}

			aIndicativeTermDetail.setTransactionType(getComboboxValue(this.transactionType));

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aIndicativeTermDetail.setAgentBank(this.agentBank.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aIndicativeTermDetail.setOtherDetails(this.otherDetails.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.row_totalFacility.isVisible()) {
				aIndicativeTermDetail.setTotalFacilityCCY(this.totalFacilityCCY.getValidatedValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.row_totalFacility.isVisible()) {
				aIndicativeTermDetail.setTotalFacility(CurrencyUtil.unFormat(this.totalFacility.getValidateValue(),
						CurrencyUtil.getFormat(getIndicativeTermDetail().getTotalFacilityCCY())));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.row_underWriting.isVisible()) {
				aIndicativeTermDetail.setUnderWritingCCY(this.underWritingCCY.getValidatedValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.row_underWriting.isVisible()) {
				aIndicativeTermDetail.setUnderWriting(CurrencyUtil.unFormat(this.underWriting.getValidateValue(),
						CurrencyUtil.getFormat(getIndicativeTermDetail().getUnderWritingCCY())));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.row_propFinalTake.isVisible()) {
				aIndicativeTermDetail.setPropFinalTakeCCY(this.propFinalTakeCCY.getValidatedValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.row_propFinalTake.isVisible()) {
				aIndicativeTermDetail.setPropFinalTake(CurrencyUtil.unFormat(this.propFinalTake.getValidateValue(),
						CurrencyUtil.getFormat(getIndicativeTermDetail().getPropFinalTakeCCY())));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();
		if (!recSave) {
			if (wve.size() > 0) {
				WrongValueException[] wvea = new WrongValueException[wve.size()];
				for (int i = 0; i < wve.size(); i++) {
					wvea[i] = (WrongValueException) wve.get(i);
				}
				if (panel != null) {
					((Tab) panel.getParent().getParent().getFellowIfAny("indicativeTermTab")).setSelected(true);
				}
				throw new WrongValuesException(wvea);
			}
		}
		aIndicativeTermDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param indicativeTermDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(IndicativeTermDetail indicativeTermDetail) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (indicativeTermDetail.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {

			if (isNewFinance()) {
				doEdit();
			} else if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents(indicativeTermDetail);

			if (panel != null) {
				this.toolbar.setVisible(false);
				this.groupboxWf.setVisible(true);
				this.window_IndTermDetailDialog.setHeight((borderLayoutHeight - 50) + "px");
				panel.appendChild(this.window_IndTermDetailDialog);
			} else {
				setDialog(DialogType.EMBEDDED);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_IndTermDetailDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		boolean isTranTypeSyndication = this.transactionType.getSelectedItem().getValue()
				.equals(FacilityConstants.FACILITY_TRAN_SYNDIACTION);

		if (!this.rpsnName.isReadonly()) {
			this.rpsnName
					.setConstraint(new PTStringValidator(Labels.getLabel("label_IndTermDetailDialog_RpsnName.value"),
							PennantRegularExpressions.REGEX_NAME, true));
		}
		if (!this.pricing.isReadonly()) {
			this.pricing.setConstraint(new PTStringValidator(Labels.getLabel("label_IndTermDetailDialog_Pricing.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL_COMMAHIPHEN, false));
		}
		if (!this.repayments.isReadonly()) {
			this.repayments
					.setConstraint(new PTStringValidator(Labels.getLabel("label_IndTermDetailDialog_Repayments.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL_COMMAHIPHEN, false));
		}
		if (!this.lCPeriod.isReadonly()) {
			this.lCPeriod
					.setConstraint(new PTStringValidator(Labels.getLabel("label_IndTermDetailDialog_LCPeriod.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL_COMMAHIPHEN, false));
		}
		if (!this.usancePeriod.isReadonly()) {
			this.usancePeriod.setConstraint(
					new PTStringValidator(Labels.getLabel("label_IndTermDetailDialog_UsancePeriod.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL_COMMAHIPHEN, false));
		}
		if (!this.utilization.isReadonly()) {
			this.utilization
					.setConstraint(new PTStringValidator(Labels.getLabel("label_IndTermDetailDialog_Utilization.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL_COMMAHIPHEN, false));
		}
		if (!this.tenorYear.isReadonly()) {
			this.tenorYear.setConstraint(
					new PTNumberValidator(Labels.getLabel("label_IndTermDetailDialog_tenorYear.value"), false, false));
		}
		if (!this.tenorMonth.isReadonly()) {
			this.tenorMonth.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_IndTermDetailDialog_tenorMonth.value"), false, false, 0, 11));
		}
		if (!this.tenorDesc.isReadonly()) {
			this.tenorDesc
					.setConstraint(new PTStringValidator(Labels.getLabel("label_IndTermDetailDialog_tenorDesc.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL_COMMAHIPHEN, false));
		}
		if (isTranTypeSyndication) {
			if (!this.totalFacilityCCY.isReadonly()) {
				this.totalFacilityCCY.setConstraint(new PTStringValidator(
						Labels.getLabel("label_IndTermDetailDialog_totalFacilityCCY.value"), null, true, true));
			}
			if (!this.underWritingCCY.isReadonly()) {
				this.underWritingCCY.setConstraint(new PTStringValidator(
						Labels.getLabel("label_IndTermDetailDialog_underWritingCCY.value"), null, true, true));
			}
			if (!this.propFinalTakeCCY.isReadonly()) {
				this.propFinalTakeCCY.setConstraint(new PTStringValidator(
						Labels.getLabel("label_IndTermDetailDialog_propFinalTakeCCY.value"), null, true, true));
			}
			if (!this.totalFacility.isReadonly()) {
				this.totalFacility.setConstraint(
						new PTDecimalValidator(Labels.getLabel("label_IndTermDetailDialog_totalFacility.value"),
								CurrencyUtil.getFormat(getIndicativeTermDetail().getTotalFacilityCCY()), true, false));
			}
			if (!this.underWriting.isReadonly()) {
				this.underWriting.setConstraint(
						new PTDecimalValidator(Labels.getLabel("label_IndTermDetailDialog_underWriting.value"),
								CurrencyUtil.getFormat(getIndicativeTermDetail().getUnderWritingCCY()), true, false));
			}
			if (!this.propFinalTake.isReadonly()) {
				this.propFinalTake.setConstraint(
						new PTDecimalValidator(Labels.getLabel("label_IndTermDetailDialog_propFinalTake.value"),
								CurrencyUtil.getFormat(getIndicativeTermDetail().getPropFinalTakeCCY()), true, false));
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.rpsnName.setConstraint("");
		this.pricing.setConstraint("");
		this.repayments.setConstraint("");
		this.lCPeriod.setConstraint("");
		this.usancePeriod.setConstraint("");
		this.utilization.setConstraint("");
		this.tenorYear.setConstraint("");
		this.tenorMonth.setConstraint("");
		this.tenorDesc.setConstraint("");
		this.agentBank.setConstraint("");
		this.otherDetails.setConstraint("");
		this.totalFacility.setConstraint("");
		this.totalFacilityCCY.setConstraint("");
		this.underWriting.setConstraint("");
		this.underWritingCCY.setConstraint("");
		this.propFinalTake.setConstraint("");
		this.propFinalTakeCCY.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Method for set constraints of LOV fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.rpsnDesg.setConstraint(
				new PTStringValidator(Labels.getLabel("label_IndTermDetailDialog_RpsnDesg.value"), null, true, true));
		this.facilityType.setConstraint(new PTStringValidator(
				Labels.getLabel("label_IndTermDetailDialog_FacilityType.value"), null, true, true));
		logger.debug("Leaving");
	}

	/**
	 * Method for remove constraints of LOV fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.rpsnDesg.setConstraint("");
		this.facilityType.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Method for clear Error messages to Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.rpsnName.setErrorMessage("");
		this.pricing.setErrorMessage("");
		this.repayments.setErrorMessage("");
		this.lCPeriod.setErrorMessage("");
		this.usancePeriod.setErrorMessage("");
		this.utilization.setErrorMessage("");
		this.rpsnDesg.setErrorMessage("");
		this.facilityType.setErrorMessage("");
		this.tenorYear.setErrorMessage("");
		this.tenorMonth.setErrorMessage("");
		this.tenorDesc.setErrorMessage("");
		this.transactionType.setErrorMessage("");
		this.agentBank.setErrorMessage("");
		this.otherDetails.setErrorMessage("");
		this.totalFacility.setErrorMessage("");
		this.totalFacilityCCY.setErrorMessage("");
		this.underWriting.setErrorMessage("");
		this.underWritingCCY.setErrorMessage("");
		this.propFinalTake.setErrorMessage("");
		this.propFinalTakeCCY.setErrorMessage("");
		logger.debug("Leaving");
	}

	// Method for refreshing the list after successful update
	protected void refreshList() {
		logger.debug("Entering");
		logger.debug("Leaving");
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final IndicativeTermDetail aIndicativeTermDetail = new IndicativeTermDetail();
		BeanUtils.copyProperties(getIndicativeTermDetail(), aIndicativeTermDetail);

		doDelete(aIndicativeTermDetail.getFinReference(), aIndicativeTermDetail);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getIndicativeTermDetail().isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}

		this.rpsnName.setReadonly(isReadOnly("IndicativeTermDetailDialog_rpsnName"));
		this.rpsnDesg.setReadonly(isReadOnly("IndicativeTermDetailDialog_rpsnDesg"));
		this.custName.setReadonly(true);
		this.custId.setReadonly(true);
		this.facilityType.setReadonly(isReadOnly("IndicativeTermDetailDialog_facilityType"));
		this.pricing.setReadonly(isReadOnly("IndicativeTermDetailDialog_pricing"));
		this.repayments.setReadonly(isReadOnly("IndicativeTermDetailDialog_repayments"));
		this.lCPeriod.setReadonly(isReadOnly("IndicativeTermDetailDialog_lCPeriod"));
		this.usancePeriod.setReadonly(isReadOnly("IndicativeTermDetailDialog_usancePeriod"));
		this.securityClean.setDisabled(isReadOnly("IndicativeTermDetailDialog_securityClean"));
		this.securityName.setReadonly(isReadOnly("IndicativeTermDetailDialog_securityName"));
		this.utilization.setReadonly(isReadOnly("IndicativeTermDetailDialog_utilization"));
		this.commission.setReadonly(isReadOnly("IndicativeTermDetailDialog_commission"));
		this.purpose.setReadonly(isReadOnly("IndicativeTermDetailDialog_purpose"));
		this.guarantee.setReadonly(isReadOnly("IndicativeTermDetailDialog_guarantee"));
		this.covenants.setReadonly(isReadOnly("IndicativeTermDetailDialog_covenants"));
		this.documentsRequired.setReadonly(isReadOnly("IndicativeTermDetailDialog_documentsRequired"));
		this.tenorYear.setReadonly(isReadOnly("IndicativeTermDetailDialog_tenorYear"));
		this.tenorMonth.setReadonly(isReadOnly("IndicativeTermDetailDialog_tenorMonth"));
		this.tenorDesc.setReadonly(isReadOnly("IndicativeTermDetailDialog_tenorDesc"));
		readOnlyComponent(isReadOnly("IndicativeTermDetailDialog_transactionType"), this.transactionType);
		readOnlyComponent(isReadOnly("IndicativeTermDetailDialog_agentBank"), this.agentBank);
		readOnlyComponent(isReadOnly("IndicativeTermDetailDialog_otherDetails"), this.otherDetails);
		readOnlyComponent(isReadOnly("IndicativeTermDetailDialog_totalFacility"), this.totalFacility);
		readOnlyComponent(isReadOnly("IndicativeTermDetailDialog_totalFacilityCCY"), this.totalFacilityCCY);
		readOnlyComponent(isReadOnly("IndicativeTermDetailDialog_underWriting"), this.underWriting);
		readOnlyComponent(isReadOnly("IndicativeTermDetailDialog_underWritingCCY"), this.underWritingCCY);
		readOnlyComponent(isReadOnly("IndicativeTermDetailDialog_propFinalTake"), this.propFinalTake);
		readOnlyComponent(isReadOnly("IndicativeTermDetailDialog_propFinalTakeCCY"), this.propFinalTakeCCY);

		// this.btnGenerateTermSheet.setDisabled(isReadOnly("IndicativeTermDetailDialog_btnGenerateTermSheet"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.indicativeTermDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || isNewFinance()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.rpsnName.setReadonly(true);
		this.rpsnDesg.setReadonly(true);
		this.custName.setReadonly(true);
		this.custId.setReadonly(true);
		this.facilityType.setReadonly(true);
		this.pricing.setReadonly(true);
		this.repayments.setReadonly(true);
		this.lCPeriod.setReadonly(true);
		this.usancePeriod.setReadonly(true);
		this.securityClean.setDisabled(true);
		this.securityName.setReadonly(true);
		this.utilization.setReadonly(true);
		this.commission.setReadonly(true);
		this.purpose.setReadonly(true);
		this.guarantee.setReadonly(true);
		this.covenants.setReadonly(true);
		this.documentsRequired.setReadonly(true);
		this.tenorYear.setReadonly(true);
		this.tenorMonth.setReadonly(true);
		this.tenorDesc.setReadonly(true);
		this.transactionType.setDisabled(true);
		this.agentBank.setReadonly(true);
		this.otherDetails.setReadonly(true);
		this.totalFacility.setDisabled(true);
		this.totalFacilityCCY.setReadonly(true);
		this.underWriting.setDisabled(true);
		this.underWritingCCY.setReadonly(true);
		this.propFinalTake.setDisabled(true);
		this.propFinalTakeCCY.setReadonly(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}
		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		this.rpsnName.setValue("");
		this.rpsnDesg.setValue("");
		this.custName.setValue("");
		this.custId.setValue(Long.valueOf(0));
		this.facilityType.setValue("");
		this.facilityType.setDescription("");
		this.pricing.setValue("");
		this.repayments.setValue("");
		this.lCPeriod.setValue("");
		this.usancePeriod.setValue("");
		this.securityClean.setChecked(false);
		this.securityName.setValue("");
		this.utilization.setValue("");
		this.commission.setValue("");
		this.purpose.setValue("");
		this.guarantee.setValue("");
		this.covenants.setValue("");
		this.documentsRequired.setValue("");
		this.tenorYear.setText("");
		this.tenorMonth.setText("");
		this.tenorDesc.setText("");
		this.agentBank.setValue("");
		this.otherDetails.setValue("");
		this.totalFacility.setValue("");
		this.totalFacilityCCY.setValue("");
		this.underWriting.setValue("");
		this.underWritingCCY.setValue("");
		this.propFinalTake.setValue("");
		this.propFinalTakeCCY.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final IndicativeTermDetail aIndicativeTermDetail = new IndicativeTermDetail();
		BeanUtils.copyProperties(getIndicativeTermDetail(), aIndicativeTermDetail);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		doSetLOVValidation();
		// fill the CarLoanDetail object with the components data
		doWriteComponentsToBean(aIndicativeTermDetail);
		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here
		isNew = aIndicativeTermDetail.isNewRecord();
		String tranType = "";
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aIndicativeTermDetail.getRecordType())) {
				aIndicativeTermDetail.setVersion(aIndicativeTermDetail.getVersion() + 1);
				if (isNew) {
					aIndicativeTermDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aIndicativeTermDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aIndicativeTermDetail.setNewRecord(true);
				}
			}
		} else {
			aIndicativeTermDetail.setVersion(aIndicativeTermDetail.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			if (doProcess(aIndicativeTermDetail, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aIndicativeTermDetail (CarLoanDetail)
	 * 
	 * @param tranType              (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(IndicativeTermDetail aIndicativeTermDetail, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";
		aIndicativeTermDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aIndicativeTermDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aIndicativeTermDetail.setUserDetails(getUserWorkspace().getLoggedInUser());
		if (isWorkFlowEnabled()) {

			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aIndicativeTermDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aIndicativeTermDetail.getNextTaskId());
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aIndicativeTermDetail);
				}
				if (isNotesMandatory(taskId, aIndicativeTermDetail)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}
			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();
			} else {
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
			aIndicativeTermDetail.setTaskId(taskId);
			aIndicativeTermDetail.setNextTaskId(nextTaskId);
			aIndicativeTermDetail.setRoleCode(getRole());
			aIndicativeTermDetail.setNextRoleCode(nextRoleCode);
			auditHeader = getAuditHeader(aIndicativeTermDetail, tranType);
			String operationRefs = getServiceOperations(taskId, aIndicativeTermDetail);
			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");
				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aIndicativeTermDetail, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aIndicativeTermDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * @param method      (String)
	 * @return boolean
	 */
	@SuppressWarnings("unused")
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;
		IndicativeTermDetail aIndicativeTermDetail = (IndicativeTermDetail) auditHeader.getAuditDetail().getModelData();

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
			} else {
			}
			auditHeader = ErrorControl.showErrorDetails(this.window_IndTermDetailDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;
				if (deleteNotes) {
					deleteNotes(getNotes(this.indicativeTermDetail), true);
				}
			}
			if (retValue == PennantConstants.porcessOVERIDE) {
				auditHeader.setOveride(true);
				auditHeader.setErrorMessage(null);
				auditHeader.setInfoMessage(null);
				auditHeader.setOverideMessage(null);
			}
		}
		setOverideMap(auditHeader.getOverideMap());
		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// Search Button Component Events

	public void onFulfill$facilityType(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = facilityType.getObject();
		if (dataObject instanceof String) {
			this.facilityType.setValue("");
			this.facilityType.setDescription("");
		} else {
			FacilityType detail = (FacilityType) dataObject;
			if (detail != null) {
				this.facilityType.setValue(detail.getFacilityType());
				this.facilityType.setDescription(detail.getFacilityDesc());

				if ("L".equals(detail.getFacilityFor())) {
					this.row_LCPeriod.setVisible(true);
					this.row_UsancePeriod.setVisible(true);
				} else {
					this.row_LCPeriod.setVisible(false);
					this.row_UsancePeriod.setVisible(false);
					this.lCPeriod.setValue("");
					this.usancePeriod.setValue("");
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onCheck$securityClean(Event event) {
		logger.debug("Entering" + event.toString());
		onCheckSecurity();
		logger.debug("Leaving" + event.toString());
	}

	private void onCheckSecurity() {
		if (this.securityClean.isChecked()) {
			this.securityName.setReadonly(true);
			this.securityName.setValue("");
		} else {
			this.securityName.setReadonly(isReadOnly("IndicativeTermDetailDialog_securityName"));
		}
	}

	public void doCheckTransactionType() {
		logger.debug("Entering");
		doClearMessage();
		boolean isTranTypeSyndiation = this.transactionType.getSelectedItem().getValue()
				.equals(FacilityConstants.FACILITY_TRAN_SYNDIACTION);
		if (isTranTypeSyndiation) {
			this.row_totalFacility.setVisible(true);
			this.row_underWriting.setVisible(true);
			this.row_propFinalTake.setVisible(true);
		} else {
			this.row_totalFacility.setVisible(false);
			this.row_underWriting.setVisible(false);
			this.row_propFinalTake.setVisible(false);
			this.totalFacility.setValue(BigDecimal.ZERO);
			this.underWriting.setValue(BigDecimal.ZERO);
			this.propFinalTake.setValue(BigDecimal.ZERO);
			this.totalFacilityCCY.setValue("");
			this.totalFacilityCCY.setDescription("");
			this.underWritingCCY.setValue("");
			this.underWritingCCY.setDescription("");
			this.propFinalTakeCCY.setValue("");
			this.propFinalTakeCCY.setDescription("");
		}
		logger.debug("Leaving");
	}

	public void onChange$transactionType(Event event) {
		logger.debug("Entering" + event.toString());
		doCheckTransactionType();
		logger.debug("Leaving" + event.toString());
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aIndicativeTermDetail
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(IndicativeTermDetail aIndicativeTermDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aIndicativeTermDetail.getBefImage(),
				aIndicativeTermDetail);
		return new AuditHeader(String.valueOf(aIndicativeTermDetail.getFinReference()), null, null, null, auditDetail,
				aIndicativeTermDetail.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e (Exception)
	 */
	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_IndTermDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.indicativeTermDetail);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.indicativeTermDetail.getFinReference());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public boolean isNotes_Entered() {
		return notesEntered;
	}

	public void setOverideMap(Map<String, List<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public Map<String, List<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

	public boolean isNewFinance() {
		return newFinance;
	}

	public void setNewFinance(boolean newFinance) {
		this.newFinance = newFinance;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public IndicativeTermDetail getIndicativeTermDetail() {
		return indicativeTermDetail;
	}

	public void setIndicativeTermDetail(IndicativeTermDetail indicativeTermDetail) {
		this.indicativeTermDetail = indicativeTermDetail;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

}
