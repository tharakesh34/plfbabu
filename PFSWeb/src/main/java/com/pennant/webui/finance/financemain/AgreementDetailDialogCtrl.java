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
 * FileName    		:  FinanceMainDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.aspose.words.SaveFormat;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.model.amtmasters.Authorization;
import com.pennant.backend.model.finance.FinAgreementDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.util.AgreementEngine;
import com.pennant.util.AgreementGeneration;
import com.pennant.webui.collateral.collateralsetup.CollateralBasicDetailsCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/FinanceMainDialog.zul file.
 */
public class AgreementDetailDialogCtrl extends GFCBaseCtrl<FinAgreementDetail> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = Logger.getLogger(AgreementDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_AgreementDetailDialog; // autoWired

	// Agreements Details Tab
	protected Longbox authorization1;
	protected Textbox lovDescAuthorization1Name;
	protected Button btnSearchAuthorization1;
	protected Longbox authorization2;
	protected Textbox lovDescAuthorization2Name;
	protected Button btnSearchAuthorization2;
	protected Listbox listBox_Agreements; // autoWired

	// protected List box listBox_FinAgreementDetail; // autoWired
	// Main Tab Details
	private RuleService ruleService;
	private RuleExecutionUtil ruleExecutionUtil;
	private AgreementGeneration agreementGeneration;
	private FinanceDetail financeDetail = null;
	private Object financeMainDialogCtrl = null;
	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private CollateralBasicDetailsCtrl collateralBasicDetailsCtrl;
	protected Groupbox finBasicdetails;

	private List<FinanceReferenceDetail> agreementList = null;
	boolean isFinanceProcess = false;
	private String moduleName;
	@Autowired
	private ConvFinanceMainDialogCtrl convFinanceMainDialogCtrl;

	/**
	 * default constructor.<br>
	 */
	public AgreementDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_AgreementDetailDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_AgreementDetailDialog);

		// READ OVERHANDED parameters !
		if (arguments.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
			this.isFinanceProcess = true;
		}
		if (arguments.containsKey("financeMainDialogCtrl")) {
			setFinanceMainDialogCtrl(arguments.get("financeMainDialogCtrl"));
		}
		if (arguments.containsKey("agreementList")) {
			agreementList = (List<FinanceReferenceDetail>) arguments.get("agreementList");
		}

		if (arguments.containsKey("moduleName")) {
			this.moduleName = (String) arguments.get("moduleName");
		}
		// append finance basic details
		if (arguments.containsKey("finHeaderList")) {
			appendFinBasicDetails((ArrayList<Object>) arguments.get("finHeaderList"));
		}

		if (arguments.containsKey("enqModule")) {
			enqiryModule = (boolean) arguments.get("enqModule");
		} else {
			enqiryModule = false;
		}

		doShowDialog(true);
		logger.debug(Literal.LEAVING + event.toString());
	}

	@SuppressWarnings("rawtypes")
	public void doShowDialog(boolean isLoadProcess) {
		logger.debug(Literal.ENTERING);

		List<FinanceReferenceDetail> agreementsList = null;
		if (isFinanceProcess) {

			// Prepare Data for Rule Executions
			try {
				Object object = getFinanceMainDialogCtrl().getClass().getMethod("prepareCustElgDetail", Boolean.class)
						.invoke(getFinanceMainDialogCtrl(), isLoadProcess);
				if (object != null) {
					setFinanceDetail((FinanceDetail) object);
				}
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}

			agreementsList = sortAgreementListbyGroup(financeDetail.getAggrementList());
		} else {
			agreementsList = sortAgreementListbyGroup(this.agreementList);
		}

		// clear the Agreements list box
		this.listBox_Agreements.getItems().clear();
		Listgroup group;
		String aggGrpNameTemp = "";
		if (agreementsList != null) {
			for (FinanceReferenceDetail financeReferenceDetail : agreementsList) {

				boolean isAgrRender = true;
				// Check Each Agreement is attached with Rule or Not, If Rule
				// Exists based on Rule Result Agreement will display
				if (StringUtils.isNotBlank(financeReferenceDetail.getLovDescAggRuleName())) {
					Rule rule = getRuleService().getApprovedRuleById(financeReferenceDetail.getLovDescAggRuleName(),
							RuleConstants.MODULE_AGRRULE, RuleConstants.EVENT_AGRRULE);
					if (rule != null) {
						if (isFinanceProcess) {
							HashMap<String, Object> fieldsAndValues = getFinanceDetail().getCustomerEligibilityCheck()
									.getDeclaredFieldValues();
							isAgrRender = (boolean) getRuleExecutionUtil().executeRule(rule.getSQLRule(),
									fieldsAndValues,
									getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy(),
									RuleReturnType.BOOLEAN);
						}
					}
				}
				if (isAgrRender) {
					if (isFinanceProcess && StringUtils.trimToEmpty(financeReferenceDetail.getLovDescAggReportName())
							.contains("/")) {
						String aggName = financeReferenceDetail.getLovDescAggReportName();
						String aggGrpName = aggName.substring(0, aggName.lastIndexOf("/"));
						if (!StringUtils.equals(aggGrpName, aggGrpNameTemp)) {
							aggGrpNameTemp = aggGrpName;
							group = new Listgroup();
							group.setOpen(false);
							Listcell cell = new Listcell(aggGrpName);
							cell.setParent(group);
							this.listBox_Agreements.appendChild(group);
						}
					}
					addAgreementtoList(financeReferenceDetail);
				}
			}
		}
		try {
			Class[] paramType = { this.getClass() };
			Object[] stringParameter = { this };
			getFinanceMainDialogCtrl().getClass().getMethod("setAgreementDetailDialogCtrl", paramType)
					.invoke(getFinanceMainDialogCtrl(), stringParameter);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		// set Read only mode accordingly if the object is new or not.
		doEdit();

		getBorderLayoutHeight();
		this.listBox_Agreements.setHeight(this.borderLayoutHeight - 140 - 90 + "px");// 210px
		this.window_AgreementDetailDialog.setHeight(this.borderLayoutHeight - 80 + "px");
		logger.debug(Literal.LEAVING);
	}

	private List<FinanceReferenceDetail> sortAgreementListbyGroup(List<FinanceReferenceDetail> aggList) {
		logger.debug(Literal.ENTERING);
		HashMap<String, List<FinanceReferenceDetail>> aggDetailMap = new HashMap<String, List<FinanceReferenceDetail>>();
		for (FinanceReferenceDetail financeReferenceDetail : aggList) {
			String aggName = financeReferenceDetail.getLovDescAggReportName();
			String aggGrpName = "NOGROUP";
			if (StringUtils.trimToEmpty(financeReferenceDetail.getLovDescAggReportName()).contains("/")) {
				aggGrpName = aggName.substring(0, aggName.lastIndexOf("/"));
			}
			if (aggDetailMap.get(aggGrpName) == null) {
				List<FinanceReferenceDetail> finRefList = new ArrayList<FinanceReferenceDetail>();
				finRefList.add(financeReferenceDetail);
				aggDetailMap.put(aggGrpName, finRefList);
			} else {
				aggDetailMap.get(aggGrpName).add(financeReferenceDetail);
			}
		}
		List<FinanceReferenceDetail> agreementsList = new ArrayList<FinanceReferenceDetail>();
		for (String grpName : aggDetailMap.keySet()) {
			agreementsList.addAll(aggDetailMap.get(grpName));
		}
		logger.debug(Literal.LEAVING);
		return agreementsList;
	}

	public void onClick$btnSearchAuthorization1(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		String authtypes[] = null;
		if (getFinanceDetail() != null
				&& StringUtils.isNotBlank(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinPurpose())) {
			authtypes = new String[2];
			authtypes[0] = AssetConstants.AUTH_DEFAULT;
			authtypes[1] = getFinanceDetail().getFinScheduleData().getFinanceMain().getFinPurpose();
		} else {
			authtypes = new String[1];
			authtypes[0] = AssetConstants.AUTH_DEFAULT;
		}
		Filter filter[] = new Filter[1];
		filter[0] = new Filter("AuthType", authtypes, Filter.OP_IN);
		Object dataObject = ExtendedSearchListBox.show(this.window_AgreementDetailDialog, "Authorization", filter);
		if (dataObject instanceof String) {
			this.authorization1.setValue(null);
			this.lovDescAuthorization1Name.setValue("");
		} else {
			Authorization details = (Authorization) dataObject;
			if (details != null) {
				this.authorization1.setValue(details.getAuthUserId());
				this.lovDescAuthorization1Name.setValue(details.getAuthName() + "-" + details.getAuthDesig());
			}
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onClick$btnSearchAuthorization2(Event event) {

		logger.debug(Literal.ENTERING);
		String authtypes[] = null;
		if (getFinanceDetail() != null
				&& StringUtils.isNotBlank(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinPurpose())) {
			authtypes = new String[2];
			authtypes[0] = AssetConstants.AUTH_DEFAULT;
			authtypes[1] = getFinanceDetail().getFinScheduleData().getFinanceMain().getFinPurpose();
		} else {
			authtypes = new String[1];
			authtypes[0] = AssetConstants.AUTH_DEFAULT;
		}
		Filter filter[] = new Filter[1];
		filter[0] = new Filter("AuthType", authtypes, Filter.OP_IN);
		Object dataObject = ExtendedSearchListBox.show(this.window_AgreementDetailDialog, "Authorization", filter);
		if (dataObject instanceof String) {
			this.authorization2.setValue(null);
			this.lovDescAuthorization2Name.setValue("");
		} else {
			Authorization details = (Authorization) dataObject;
			if (details != null) {
				this.authorization2.setValue(details.getAuthUserId());
				this.lovDescAuthorization2Name.setValue(details.getAuthName() + "-" + details.getAuthDesig());
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method to fill Agreements tab.
	 * 
	 * @param listbox
	 * @param financeReferenceDetail
	 */
	private void addAgreementtoList(FinanceReferenceDetail financeReferenceDetail) {
		logger.debug(Literal.ENTERING);
		Listitem item = new Listitem(); // To Create List item
		Listcell listCell;
		listCell = new Listcell();
		listCell.setLabel(financeReferenceDetail.getLovDescRefDesc());
		listCell.setParent(item);
		listCell = new Listcell();
		Html ageementLink = new Html();
		ageementLink.setContent("<a href='javascript:;' style = 'font-weight:bold'>"
				+ financeReferenceDetail.getLovDescNamelov() + "</a> ");
		listCell.appendChild(ageementLink);
		listCell.setParent(item);
		this.listBox_Agreements.appendChild(item);
		/*
		 * ageementLink.addForward("onClick", window_AgreementDetailDialog, "onGenerateReportClicked",
		 * financeReferenceDetail);
		 */

		ageementLink.addEventListener(Events.ON_CLICK,
				event -> onGenerateReportClicked(financeReferenceDetail, getFinanceDetails()));

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Generating Template replaced to Finance Details
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onGenerateReportClicked(FinanceReferenceDetail data, FinanceDetail detail) throws Exception {
		logger.debug(Literal.ENTERING);

		if (isFinanceProcess) {
			if (null != convFinanceMainDialogCtrl && null != detail) {
				try {
					convFinanceMainDialogCtrl.setCreditRevDetails(detail);
				} catch (Exception e) {
					if (e.getCause().getClass().equals(WrongValuesException.class)) {
						throw e;
					}
				}
			}
		}

		if (isFinanceProcess) {
			// Calling Credit Review Details

			FinScheduleData finScheduleData = detail.getFinScheduleData();
			FinanceMain fm = finScheduleData.getFinanceMain();
			if (detail == null || finScheduleData == null || fm == null) {
				return;
			}

			try {

				String finReference = fm.getFinReference();
				String aggName = StringUtils.trimToEmpty(data.getLovDescNamelov());
				String reportName = "";

				/**
				 * Disabling the aggPath functionality as aggPath is no longer considered in loan process. As discussed
				 * with Raju. This functionality is moved to collateral and associated at customer side.
				 * 
				 */
				String aggPath = "", templateName = "";
				if (StringUtils.trimToEmpty(data.getLovDescAggReportName()).contains("/")) {
					String aggRptName = StringUtils.trimToEmpty(data.getLovDescAggReportName());
					// aggPath =
					// main.getFinPurpose()+"/"+aggRptName.substring(0,aggRptName.lastIndexOf("/"));
					templateName = aggRptName.substring(aggRptName.lastIndexOf("/") + 1, aggRptName.length());
				} else {
					// aggPath = main.getFinPurpose();
					templateName = data.getLovDescAggReportName();
				}

				AgreementEngine engine = new AgreementEngine(aggPath);
				engine.setTemplate(templateName);
				engine.loadTemplate();

				engine.mergeFields(getAgreementGeneration().getAggrementData(detail, data.getLovDescAggImage(),
						getUserWorkspace().getUserDetails()));

				getAgreementGeneration().setExtendedMasterDescription(detail, engine);
				getAgreementGeneration().setCustExtFieldDesc(detail.getCustomerDetails(), engine);
				getAgreementGeneration().setFeeDetails(detail, engine);

				byte[] docData = null;
				int format = 0;

				if (PennantConstants.DOC_TYPE_PDF.equals(data.getAggType())) {
					reportName = finReference + "_" + aggName + PennantConstants.DOC_TYPE_PDF_EXT;
					format = SaveFormat.PDF;
					docData = engine.getDocumentInByteArray(format);
				} else {
					reportName = finReference + "_" + aggName + PennantConstants.DOC_TYPE_WORD_EXT;
					format = SaveFormat.DOCX;
					docData = engine.getDocumentInByteArray(format);
				}

				showDocument(docData, window_AgreementDetailDialog, reportName, format);

				engine.close();
				engine = null;
			} catch (Exception e) {
				if (e instanceof IllegalArgumentException && (e.getMessage().equals("Document site does not exist.")
						|| e.getMessage().equals("Template site does not exist.")
						|| e.getMessage().equals("Template does not exist."))) {
					// throw new Exception("Template does not exists.Please
					// configure Template.");
					AppException exception = new AppException("Template does not exists.Please configure Template.");
					MessageUtil.showError(exception);
				} else {
					MessageUtil.showError(e);
				}
			}
		} else {

			// Other Than Finance Modules : TODO Need to Modify
			String finReference = "";
			String aggName = StringUtils.trimToEmpty(data.getLovDescNamelov());
			String reportName = "";

			try {

				String aggPath = "", templateName = "";
				if (StringUtils.trimToEmpty(data.getLovDescAggReportName()).contains("/")) {
					String aggRptName = StringUtils.trimToEmpty(data.getLovDescAggReportName());
					aggPath = aggRptName.substring(0, aggRptName.lastIndexOf("/"));
					templateName = aggRptName.substring(aggRptName.lastIndexOf("/") + 1, aggRptName.length());
				} else {
					aggPath = "";
					templateName = data.getLovDescAggReportName();
				}
				AgreementEngine engine = new AgreementEngine(aggPath);
				engine.setTemplate(templateName);
				engine.loadTemplate();

				int format = SaveFormat.PDF;
				if (StringUtils.equals(data.getAggType(), PennantConstants.DOC_TYPE_PDF)) {
					reportName = finReference + "_" + aggName + PennantConstants.DOC_TYPE_PDF_EXT;
					format = SaveFormat.PDF;
				} else {
					reportName = finReference + "_" + aggName + PennantConstants.DOC_TYPE_WORD_EXT;
					format = SaveFormat.DOCX;
				}

				byte[] docData = engine.getDocumentInByteArray(format);

				showDocument(docData, window_AgreementDetailDialog, reportName, format);

				engine.close();
				engine = null;

			} catch (Exception e) {
				final String msg = e.getMessage() + "\n" + Labels.getLabel("message.error.agreementNotFound");

				MessageUtil.showError(msg);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private FinanceDetail getFinanceDetails() {

		if (!isFinanceProcess) {
			return null;
		}

		try {
			Object object = getFinanceMainDialogCtrl().getClass().getMethod("getAgrFinanceDetails")
					.invoke(financeMainDialogCtrl);
			if (object != null) {
				return (FinanceDetail) object;
			}
		} catch (Exception e) {
			if (e.getCause().getClass().equals(WrongValuesException.class)) {
				throw new AppException(e.getMessage());
			}
		}
		return null;
	}

	public void doEdit() {
		readOnlyComponent(true, this.lovDescAuthorization1Name);
		readOnlyComponent(true, this.lovDescAuthorization2Name);

		this.btnSearchAuthorization1.setDisabled(isReadOnly("FinanceMainDialog_Authorization1"));
		this.btnSearchAuthorization2.setDisabled(isReadOnly("FinanceMainDialog_Authorization2"));
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails(ArrayList<Object> finHeaderList) {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			map.put("finHeaderList", finHeaderList);
			map.put("moduleName", moduleName);
			if (isFinanceProcess) {
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul",
						this.finBasicdetails, map);
			} else {
				Executions.createComponents("/WEB-INF/pages/Collateral/CollateralSetup/CollateralBasicDetails.zul",
						this.finBasicdetails, map);
			}
		} catch (Exception e) {
			logger.debug(e);
		}
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		if (isFinanceProcess) {
			getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
		} else {
			getCollateralBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
		}
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public RuleService getRuleService() {
		return ruleService;
	}

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}

	public RuleExecutionUtil getRuleExecutionUtil() {
		return ruleExecutionUtil;
	}

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public AgreementGeneration getAgreementGeneration() {
		return agreementGeneration;
	}

	public void setAgreementGeneration(AgreementGeneration agreementGeneration) {
		this.agreementGeneration = agreementGeneration;
	}

	public CollateralBasicDetailsCtrl getCollateralBasicDetailsCtrl() {
		return collateralBasicDetailsCtrl;
	}

	public void setCollateralBasicDetailsCtrl(CollateralBasicDetailsCtrl collateralBasicDetailsCtrl) {
		this.collateralBasicDetailsCtrl = collateralBasicDetailsCtrl;
	}

}