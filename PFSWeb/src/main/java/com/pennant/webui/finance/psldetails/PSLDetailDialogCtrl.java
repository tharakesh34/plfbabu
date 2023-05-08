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
 * * FileName : PSLDetailDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 20-06-2018 * * Modified
 * Date : 20-06-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 20-06-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.psldetails;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.psl.PSLDetail;
import com.pennant.backend.model.systemmasters.LoanPurpose;
import com.pennant.backend.service.finance.PSLDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.ExtendedMultipleSearchListBox;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/AMTMasters/PSLDetail/pSLDetailDialog.zul file. <br>
 */
public class PSLDetailDialogCtrl extends GFCBaseCtrl<PSLDetail> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(PSLDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_PSLDetailDialog;
	protected Space space_FinReference;
	protected Textbox finReference;
	protected Combobox categoryCode;
	protected ExtendedCombobox weakerSection;
	protected Combobox landHolding;
	protected Combobox landArea;
	protected Combobox sector;
	protected CurrencyBox amount;
	protected Combobox subCategory;
	protected ExtendedCombobox purpose;
	protected ExtendedCombobox endUse;
	protected Uppercasebox loanPurpose;
	protected CurrencyBox eligibiltyAmount;
	protected Button btnLoanPurpose;
	protected Groupbox finBasicdetails;
	protected Groupbox gb_EndUseDetails;
	protected Space space_LandArea;
	private PSLDetail pSLDetail; // overhanded per param
	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private boolean fromLoan = false;
	private Object financeMainDialogCtrl = null;
	Tab parenttab = null;
	private boolean enquirymode = false;
	protected Row row_Category;
	protected Row row_LandHolding;
	protected Row row_Sector;
	protected Row row_Subcategory;
	protected Row row_Purpose;
	protected Row row_EndUse;
	protected Label label_SubCategory;
	protected Label label_WeakerSection;
	protected Space space_SubCategory;
	protected Button btnNotes;

	private transient PSLDetailListCtrl pSLDetailListCtrl; // overhanded per param
	private transient PSLDetailService pSLDetailService;

	private List<ValueLabel> listLandHolding = PennantStaticListUtil.getYesNo();
	// private List<ValueLabel> listLandArea=PennantStaticListUtil.getYesNo();
	// private List<ValueLabel> listSector=PennantStaticListUtil.getYesNo();
	private List<ValueLabel> categoryList = PennantAppUtil.getPslCategoryList();
	private List<ValueLabel> landAreaList = PennantStaticListUtil.getLandAreaList();
	private List<ValueLabel> subCategoryList = PennantStaticListUtil.getSubCategoryList();
	private List<ValueLabel> subCategoryListGeneral = PennantStaticListUtil.getSubCategoryGeneralList();
	private List<ValueLabel> sectorList = PennantStaticListUtil.getPSLSectorList();
	private List<ValueLabel> subSectorList = PennantStaticListUtil.getSubSectorList();
	private Map<String, Object> rules = new HashMap<>();

	/**
	 * default constructor.<br>
	 */
	public PSLDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "PSLDetailDialog";
	}

	@Override
	protected String getReference() {
		return this.pSLDetail.getFinReference();
	}

	public Map<String, Object> getRules() {
		return rules;
	}

	public void setRules(Map<String, Object> rules) {
		this.rules = rules;
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_PSLDetailDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_PSLDetailDialog);

		try {
			// Get the required arguments.
			this.pSLDetail = (PSLDetail) arguments.get("pSLDetail");

			if (arguments.containsKey("fromLoan")) {
				fromLoan = (Boolean) arguments.get("fromLoan");
			} else {
				this.pSLDetailListCtrl = (PSLDetailListCtrl) arguments.get("pSLDetailListCtrl");
			}

			if (arguments.containsKey("enquirymode")) {
				enquirymode = (boolean) arguments.containsKey("enquirymode");
			}

			if (arguments.containsKey("finHeaderList")) {
				appendFinBasicDetails((ArrayList<Object>) arguments.get("finHeaderList"));
			} else {
				appendFinBasicDetails(null);
			}

			if (arguments.containsKey("financeMainDialogCtrl")) {
				setFinanceMainDialogCtrl(arguments.get("financeMainDialogCtrl"));
			}

			if (arguments.containsKey("tab")) {
				parenttab = (Tab) arguments.get("tab");
			}

			if (this.pSLDetail == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			PSLDetail pSLDetail = new PSLDetail();
			BeanUtils.copyProperties(this.pSLDetail, pSLDetail);
			this.pSLDetail.setBefImage(pSLDetail);

			// Render the page and display the data.
			doLoadWorkFlow(this.pSLDetail.isWorkflow(), this.pSLDetail.getWorkflowId(), this.pSLDetail.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}

			fillComboBox(this.categoryCode, PennantConstants.List_Select, categoryList, "");
			fillComboBox(this.landArea, PennantConstants.List_Select, landAreaList, "");
			fillComboBox(this.sector, PennantConstants.List_Select, sectorList, "");
			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.pSLDetail);
			this.groupboxWf.setVisible(false);
			ArrayList<Object> finHeaderList = (ArrayList<Object>) arguments.get("finHeaderList");

			/*
			 * if ("205".equals(String.valueOf(finHeaderList.get(0)))) { this.gb_EndUseDetails.setVisible(false); } else
			 * { this.gb_EndUseDetails.setVisible(false); }
			 */
			this.btnCancel.setVisible(false);
			this.btnNotes.setVisible(false);
			this.eligibiltyAmount.setReadonly(true);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onChange$categoryCode(Event event) {
		doRemoveValidation();
		doClearMessage();
		setWeakerSection();

		setValuesForCategory(categoryCode.getSelectedItem().getValue());
	}

	public void onChange$subCategory(Event event) {
		setPurposeForGeneral();
	}

	private void setPurposeForGeneral() {
		if ("GNL".equals(categoryCode.getSelectedItem().getValue())) {
			if ("EC".equals(subCategory.getSelectedItem().getValue())) {
				setpurpose(subCategory.getSelectedItem().getValue());
			} else if ("SI".equals(subCategory.getSelectedItem().getValue())) {
				setpurpose(subCategory.getSelectedItem().getValue());
			} else if ("RE".equals(subCategory.getSelectedItem().getValue())) {
				setpurpose(subCategory.getSelectedItem().getValue());
			} else if ("ED".equals(subCategory.getSelectedItem().getValue())) {
				setpurpose(subCategory.getSelectedItem().getValue());
			} else if ("OT".equals(subCategory.getSelectedItem().getValue())) {
				setpurpose(subCategory.getSelectedItem().getValue());
			}
		}
	}

	private void setpurpose(String value) {
		this.purpose.setModuleName("PSLPurpose");
		this.purpose.setValueColumn("Code");
		this.purpose.setDescColumn("Description");
		this.purpose.setMandatoryStyle(true);
		Filter purposeFilter[] = new Filter[2];
		purposeFilter[0] = new Filter("CategoryCode", categoryCode.getSelectedItem().getValue(), Filter.OP_EQUAL);
		purposeFilter[1] = new Filter("SubCategoryCode", value, Filter.OP_EQUAL);
		this.purpose.setFilters(purposeFilter);
		this.purpose.setValidateColumns(new String[] { "Code", "Description" });

	}

	private void setWeakerSection() {
		if (!"NPSL".equals(categoryCode.getValue())) {
			this.weakerSection.setVisible(true);
		} else {
			this.weakerSection.setVisible(false);
		}
	}

	private void setValuesForCategory(String value) {
		if ("AGRI".equals(value)) {
			this.row_LandHolding.setVisible(true);
			this.row_Sector.setVisible(false);
			this.row_Subcategory.setVisible(true);
			this.row_Purpose.setVisible(true);
			this.row_EndUse.setVisible(true);
			this.subCategory.setVisible(true);
			this.subCategory.setDisabled(false);
			String excludeValues = ",MF,SF,OF,";
			fillComboBox(this.landHolding, "", listLandHolding, "");
			fillComboBox(this.subCategory, "", subCategoryList, excludeValues);
			fillComboBox(this.landArea, "", landAreaList, "");
			this.label_SubCategory.setVisible(true);
			this.weakerSection.setVisible(true);
			this.label_WeakerSection.setVisible(true);
			this.amount.setValue(BigDecimal.ZERO);
			this.purpose.setValue("");
			this.space_SubCategory.setSclass("mandatory");
			this.sector.setValue("");
			this.subCategory.setValue("");
			setPurpose();
		} else if ("MSME".equals(value)) {
			this.row_Sector.setVisible(true);
			this.row_Subcategory.setVisible(true);
			this.row_LandHolding.setVisible(false);
			this.row_Purpose.setVisible(true);
			this.row_EndUse.setVisible(false);
			this.subCategory.setVisible(true);
			this.label_SubCategory.setVisible(true);
			fillComboBox(this.sector, "", sectorList, "");
			fillComboBox(this.subCategory, "", subSectorList, "");
			this.subCategory.setDisabled(false);
			this.weakerSection.setVisible(true);
			this.label_WeakerSection.setVisible(true);
			this.space_SubCategory.setSclass("mandatory");
			this.landHolding.setValue("");
			this.landArea.setValue("");
			this.endUse.setValue("");
			this.purpose.setValue("");
			setPurpose();
		} else if ("HF".equals(value)) {
			this.row_LandHolding.setVisible(false);
			this.row_Sector.setVisible(false);
			this.row_Subcategory.setVisible(true);
			this.row_Purpose.setVisible(true);
			this.row_EndUse.setVisible(false);
			this.subCategory.setVisible(true);
			this.subCategory.setDisabled(true);
			this.endUse.setValue("");
			this.purpose.setValue("");
			String excludeValues = ",MF,SF,OF,LL,TF,OL,SE,";
			fillComboBox(subCategory, "HF", subCategoryList, excludeValues);
			setPurpose();
			this.label_SubCategory.setVisible(true);
			this.weakerSection.setVisible(true);
			this.label_WeakerSection.setVisible(true);
			this.landHolding.setValue("");
			this.landArea.setValue("");
			this.space_SubCategory.setSclass("mandatory");
		} else if ("GNL".equals(value)) {
			this.row_LandHolding.setVisible(false);
			this.row_Sector.setVisible(false);
			this.row_Subcategory.setVisible(true);
			this.row_Purpose.setVisible(true);
			this.row_EndUse.setVisible(false);
			this.subCategory.setVisible(true);
			this.subCategory.setDisabled(false);
			this.label_SubCategory.setVisible(true);
			this.weakerSection.setVisible(true);
			this.label_WeakerSection.setVisible(true);
			this.space_SubCategory.setSclass("mandatory");
			this.landHolding.setValue("");
			this.landArea.setValue("");
			this.endUse.setValue("");
			this.purpose.setValue("");
			fillComboBox(subCategory, "", subCategoryListGeneral, "");
			setPurpose();
		} else if ("NPSL".equals(value)) {
			this.row_LandHolding.setVisible(false);
			this.row_Sector.setVisible(false);
			this.row_Subcategory.setVisible(true);
			this.row_Purpose.setVisible(true);
			this.subCategory.setDisabled(false);
			this.row_EndUse.setVisible(false);
			this.subCategory.setVisible(false);
			this.label_SubCategory.setVisible(false);
			this.weakerSection.setVisible(false);
			this.label_WeakerSection.setVisible(false);
			this.landHolding.setValue("");
			this.landArea.setValue("");
			this.endUse.setValue("");
			this.subCategory.setValue("");
			this.space_SubCategory.setSclass("");
			this.purpose.setValue("");
			this.purpose.setValue("");
			setPurpose();
		} else {
			this.row_LandHolding.setVisible(false);
			this.row_Sector.setVisible(false);
			this.row_Subcategory.setVisible(false);
			this.row_Purpose.setVisible(false);
			this.subCategory.setVisible(false);
			this.subCategory.setDisabled(false);
			this.label_SubCategory.setVisible(false);
			this.weakerSection.setVisible(true);
			this.label_WeakerSection.setVisible(true);
			this.space_SubCategory.setSclass("");
			this.landHolding.setValue("");
			this.landArea.setValue("");
			this.row_EndUse.setVisible(false);
		}
	}

	private void setPurpose() {
		this.purpose.setModuleName("PSLPurpose");
		this.purpose.setValueColumn("Code");
		this.purpose.setDescColumn("Description");
		Filter purposeFilter[] = new Filter[1];
		purposeFilter[0] = new Filter("CategoryCode", categoryCode.getSelectedItem().getValue(), Filter.OP_EQUAL);
		this.purpose.setFilters(purposeFilter);
		this.purpose.setValidateColumns(new String[] { "Code", "Description" });
		this.purpose.setMandatoryStyle(true);

	}

	public void onChange$landArea(Event event) {
		setSubCateogoryValueForAgri();
	}

	public void onChange$landHolding(Event event) {
		setSubCateogoryValueForAgri();
	}

	public void setSubCateogoryValueForAgri() {
		if ("Agriculture".equals(categoryCode.getValue()) && "Y".equals(landHolding.getSelectedItem().getValue())) {
			this.space_LandArea.setSclass("mandatory");
			if (landArea.getSelectedItem() != null && "1".equals(landArea.getSelectedItem().getValue())) {
				fillComboBox(this.subCategory, "MF", subCategoryList, "");
				this.subCategory.setDisabled(true);
			} else if (landArea.getSelectedItem() != null && "2".equals(landArea.getSelectedItem().getValue())) {
				fillComboBox(this.subCategory, "SF", subCategoryList, "");
				this.subCategory.setDisabled(true);
			} else if (landArea.getSelectedItem() != null && "3".equals(landArea.getSelectedItem().getValue())) {
				fillComboBox(this.subCategory, "OF", subCategoryList, "");
				this.subCategory.setDisabled(true);
			} else {
				String excludeValues = ",MF,SF,OF,";
				fillComboBox(this.subCategory, "", subCategoryList, excludeValues);
				this.subCategory.setDisabled(false);

			}
		} else if ("Agriculture".equals(categoryCode.getValue())
				&& "N".equals(landHolding.getSelectedItem().getValue())) {
			this.space_LandArea.setSclass("");
			this.landArea.setValue("");
			String excludeValues = ",MF,SF,OF,";
			fillComboBox(this.subCategory, "", subCategoryList, excludeValues);
			this.subCategory.setDisabled(false);

		}

		if ("Agriculture".equals(categoryCode.getValue())) {
			this.purpose.setModuleName("PSLPurpose");
			this.purpose.setValueColumn("Code");
			this.purpose.setDescColumn("Description");
			Filter purposeFilter[] = new Filter[1];
			purposeFilter[0] = new Filter("CategoryCode", categoryCode.getSelectedItem().getValue(), Filter.OP_EQUAL);
			this.purpose.setFilters(purposeFilter);
			this.purpose.setValidateColumns(new String[] { "CategoryCode", "SubCategoryCode", "Code", "Description" });
		}
	}

	public void onFulfill$amount(Event event) {
		setSubCateogoryValueForMSMI();
	}

	public void setSubCateogoryValueForMSMI() {
		BigDecimal amount = this.amount.getActualValue();
		if ("MSME".equals(categoryCode.getValue())) {
			String excludeValues = ",HF,";
			fillComboBox(this.subCategory, "", subSectorList, excludeValues);
			this.subCategory.setDisabled(false);
			if ("MNF".equals(sector.getSelectedItem().getValue())) {
				if (amount.compareTo(BigDecimal.valueOf(2500000)) <= 0) {
					fillComboBox(this.subCategory, "MI", subSectorList, "");
					this.subCategory.setDisabled(true);
				} else if (amount.compareTo(BigDecimal.valueOf(2500000)) >= 0
						&& amount.compareTo(BigDecimal.valueOf(50000000)) <= 0) {
					fillComboBox(this.subCategory, "SI", subSectorList, "");
					this.subCategory.setDisabled(true);
				} else if (amount.compareTo(BigDecimal.valueOf(50000000)) >= 0
						&& amount.compareTo(BigDecimal.valueOf(100000000)) <= 0) {
					fillComboBox(this.subCategory, "ME", subSectorList, "");
					this.subCategory.setDisabled(true);

				}

			} else if ("SVS".equals(sector.getSelectedItem().getValue())) {

				if (amount.compareTo(BigDecimal.valueOf(1000000)) <= 0) {
					fillComboBox(this.subCategory, "MI", subSectorList, "");
					this.subCategory.setDisabled(true);

				} else if (amount.compareTo(BigDecimal.valueOf(1000000)) >= 0
						&& amount.compareTo(BigDecimal.valueOf(20000000)) <= 0) {
					fillComboBox(this.subCategory, "SI", subSectorList, "");
					this.subCategory.setDisabled(true);

				} else if (amount.compareTo(BigDecimal.valueOf(20000000)) >= 0
						&& amount.compareTo(BigDecimal.valueOf(50000000)) <= 0) {
					fillComboBox(this.subCategory, "ME", subSectorList, "");
					this.subCategory.setDisabled(true);

				}
			} else {
				// PSD 127735
				fillComboBox(this.subCategory, "MI", subSectorList, "HF");
				this.subCategory.setDisabled(false);
			}
		}

		if ("MSME".equals(categoryCode.getValue())) {
			setPurpose();
		}
	}

	public void onFulfill$purpose(Event event) {
		logger.debug("Entering");
		onFullfillAggriculture();
		logger.debug("Leaving");
	}

	public void onFullfillAggriculture() {
		if ("Agriculture".equals(categoryCode.getValue())) {
			this.endUse.setDisplayStyle(2);
			this.endUse.setModuleName("PSLEndUse");
			this.endUse.setValueColumn("Code");
			this.endUse.setDescColumn("Description");
			Filter endUseFilter[] = new Filter[1];
			endUseFilter[0] = new Filter("PurposeCode", this.purpose.getValue(), Filter.OP_EQUAL);
			this.endUse.setFilters(endUseFilter);
			this.endUse.setValidateColumns(new String[] { "purposeCode", "Code", "Description" });
			this.endUse.setMandatoryStyle(true);
		}
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails(ArrayList<Object> finHeaderList) {
		logger.debug(Literal.ENTERING);

		try {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			if (finHeaderList != null) {
				map.put("finHeaderList", finHeaderList);
			}
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.error(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Adding Flags into Multi Selection Extended box
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onClick$btnLoanPurpose(Event event) {
		logger.debug("Entering  " + event.toString());

		Map<String, Object> loanPurposeMap = new HashMap<String, Object>();
		Object dataObject = null;

		String[] courtType = this.loanPurpose.getValue().split(",");
		for (int i = 0; i < courtType.length; i++) {
			loanPurposeMap.put(courtType[i], courtType[i]);
		}

		if (StringUtils.trimToNull(this.loanPurpose.getValue()) != null) {
			dataObject = ExtendedMultipleSearchListBox.show(this.window, "LoanPurpose", loanPurposeMap);
		} else {
			dataObject = ExtendedMultipleSearchListBox.show(this.window, "LoanPurpose", loanPurposeMap);
		}

		BigDecimal amount = BigDecimal.ZERO;

		if (dataObject instanceof String) {
			this.loanPurpose.setValue(dataObject.toString());
			this.loanPurpose.setTooltiptext("");
		} else {
			Map<String, Object> details = (Map<String, Object>) dataObject;
			if (details != null) {
				String purposeTypes = details.keySet().toString();
				purposeTypes = purposeTypes.replace("[", " ").replace("]", "").replace(" ", "");
				if (purposeTypes.startsWith(",")) {
					purposeTypes = purposeTypes.substring(1);
				}
				if (purposeTypes.endsWith(",")) {
					purposeTypes = purposeTypes.substring(0, purposeTypes.length() - 1);
				}
				this.loanPurpose.setValue(purposeTypes);
				Object[] loanPurposes = details.values().toArray();

				for (Object object : loanPurposes) {
					if (object instanceof LoanPurpose) {
						LoanPurpose purpose = (LoanPurpose) object;
						amount = amount.add(purpose.getEligibleAmount());
					}
				}
				this.eligibiltyAmount
						.setValue(PennantApplicationUtil.formateAmount(amount, CurrencyUtil.getFormat("")));
				rules.put("ASL_ELIGABLE_AMOUNT", amount);
			}
		}

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.finReference.setMaxlength(20);
		this.weakerSection.setModuleName("PSLWeakerSection");
		this.weakerSection.setValueColumn("Code");
		this.weakerSection.setDescColumn("Description");
		this.weakerSection.setValidateColumns(new String[] { "Code", "Description" });
		this.weakerSection.setMandatoryStyle(true);
		this.amount.setProperties(false, PennantConstants.defaultCCYDecPos);
		this.eligibiltyAmount.setProperties(false, PennantConstants.defaultCCYDecPos);

		this.amount.setMandatory(true);
		/*
		 * this.purpose.setModuleName("PSLPurpose"); this.purpose.setValueColumn("Code");
		 * this.purpose.setDescColumn("Description"); this.purpose.setValidateColumns(new String[] {"CategoryCode",
		 * "SubCategoryCode","Code","Description"});
		 */

		// this.subCategory.setModuleName("PSLPurpose");
		// this.subCategory.setValueColumn("Code");
		// this.subCategory.setDescColumn("Description");
		// this.subCategory.setValidateColumns(new String[] {"purposeCode", "Code","Description"});

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_PSLDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_PSLDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_PSLDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_PSLDetailDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		// doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.pSLDetail);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		pSLDetailListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.pSLDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param pSLDetail
	 * 
	 */
	public void doWriteBeanToComponents(PSLDetail aPSLDetail) {
		logger.debug(Literal.ENTERING);

		// this.finReference.setValue(aPSLDetail.getFinReference());
		this.categoryCode.setValue(aPSLDetail.getCategoryCode());
		fillComboBox(this.categoryCode, aPSLDetail.getCategoryCode(), categoryList, "");
		setValuesForCategory(aPSLDetail.getCategoryCode());
		this.weakerSection.setValue(aPSLDetail.getWeakerSection());
		this.weakerSection.setDescription(aPSLDetail.getWeakerSectionName());
		fillComboBox(this.landHolding, aPSLDetail.getLandHolding(), listLandHolding, "");
		fillComboBox(this.landArea, aPSLDetail.getLandArea(), landAreaList, "");
		fillComboBox(this.sector, aPSLDetail.getSector(), sectorList, "");
		this.amount.setValue(PennantApplicationUtil.formateAmount(new BigDecimal(aPSLDetail.getAmount()),
				PennantConstants.defaultCCYDecPos));
		if ("AGRI".equals(aPSLDetail.getCategoryCode())) {
			fillComboBox(this.subCategory, aPSLDetail.getSubCategory(), subCategoryList, "");
			if ("MF".equals(aPSLDetail.getSubCategory()) || "OF".equals(aPSLDetail.getSubCategory())
					|| "SF".equals(aPSLDetail.getSubCategory())) {
				subCategory.setDisabled(true);
			}
		} else if ("MSME".equals(aPSLDetail.getCategoryCode())) {
			fillComboBox(this.subCategory, aPSLDetail.getSubCategory(), subSectorList, "");
			if ("MI".equals(aPSLDetail.getSubCategory()) || "SI".equals(aPSLDetail.getSubCategory())
					|| "ME".equals(aPSLDetail.getSubCategory())) {
				subCategory.setDisabled(true);
			}
		} else if ("HF".equals(aPSLDetail.getCategoryCode())) {
			String excludeValues = ",MF,SF,OF,LL,TF,OL,SC,";
			fillComboBox(subCategory, aPSLDetail.getSubCategory(), subCategoryList, excludeValues);
			if ("HF".equals(aPSLDetail.getSubCategory())) {
				subCategory.setDisabled(true);
			}
		} else if ("GNL".equals(aPSLDetail.getCategoryCode())) {
			fillComboBox(subCategory, aPSLDetail.getSubCategory(), subCategoryListGeneral, "");
		}

		this.purpose.setValue(aPSLDetail.getPurpose());
		this.purpose.setDescription(aPSLDetail.getPurposeName());
		onFullfillAggriculture();
		this.endUse.setValue(aPSLDetail.getEndUse());
		this.endUse.setDescription(aPSLDetail.getEndUseName());

		if (aPSLDetail.isNewRecord()) {
			// this.categoryCode.setDescription("");
			this.weakerSection.setDescription("");
			// this.subCategory.setDescription("");
			this.purpose.setDescription("");
			this.endUse.setDescription("");
		} else {
			// this.categoryCode.setDescription(aPSLDetail.getCategoryCodeName());
			this.weakerSection.setDescription(aPSLDetail.getWeakerSectionName());
			// this.subCategory.setDescription(aPSLDetail.getSubCategoryName());
			this.purpose.setDescription(aPSLDetail.getPurposeName());
			this.endUse.setDescription(aPSLDetail.getEndUseName());
		}

		this.loanPurpose.setValue(aPSLDetail.getLoanPurpose());
		this.eligibiltyAmount.setValue(aPSLDetail.getEligibleAmount());

		this.recordStatus.setValue(aPSLDetail.getRecordStatus());
		rules.put("ASL_ELIGABLE_AMOUNT", aPSLDetail.getEligibleAmount());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aPSLDetail
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<WrongValueException> doWriteComponentsToBean(PSLDetail aPSLDetail) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Fin Reference
		try {
			ArrayList<Object> finHeaderList = (ArrayList<Object>) arguments.get("finHeaderList");// FIXME FINID
			aPSLDetail.setFinReference(String.valueOf(finHeaderList.get(3)));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Category Code
		try {
			aPSLDetail.setCategoryCode(((this.categoryCode.getSelectedItem().getValue().toString())));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Weaker Section
		try {
			aPSLDetail.setWeakerSection(StringUtils.trimToNull(this.weakerSection.getValidatedValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Land Holding
		try {
			String strLandHolding = null;
			if (this.landHolding.getSelectedItem() != null) {
				strLandHolding = this.landHolding.getSelectedItem().getValue().toString();
			}
			if (strLandHolding != null && !PennantConstants.List_Select.equals(strLandHolding)) {
				aPSLDetail.setLandHolding(strLandHolding);

			} else {
				aPSLDetail.setLandHolding(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Land Area
		try {

			if (this.row_LandHolding.isVisible() && this.landArea.isVisible()
					&& "Y".equals(landHolding.getSelectedItem().getValue())) {
				this.landArea.setConstraint(
						new StaticListValidator(landAreaList, Labels.getLabel("label_PSLDetailDialog_LandArea.value")));
			}

			String strLandArea = null;
			if (this.landArea.getSelectedItem() != null) {
				strLandArea = this.landArea.getSelectedItem().getValue().toString();
			}
			if (strLandArea != null && !PennantConstants.List_Select.equals(strLandArea)) {
				aPSLDetail.setLandArea(strLandArea);

			} else {
				aPSLDetail.setLandArea(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Sector
		try {

			if (this.row_Sector.isVisible() && ("SVS".equals(this.sector.getSelectedItem().getValue())
					|| "MNF".equals(this.sector.getSelectedItem().getValue())) && this.amount.isVisible()) {
				this.amount.setConstraint(new PTDecimalValidator(Labels.getLabel("label_PSLDetailDialog_Amount.value"),
						2, true, false, 0));
			}

			String strSector = null;
			if (this.sector.getSelectedItem() != null) {
				strSector = this.sector.getSelectedItem().getValue().toString();
			}
			if (strSector != null && !PennantConstants.List_Select.equals(strSector)) {
				aPSLDetail.setSector(strSector);

			} else {
				aPSLDetail.setSector(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Investment in Plant & Machinery / Equipment
		try {
			if (this.amount.getActualValue().compareTo(BigDecimal.ZERO) > 0) {
				aPSLDetail.setAmount(PennantApplicationUtil
						.unFormateAmount(this.amount.getValidateValue(), PennantConstants.defaultCCYDecPos)
						.doubleValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Sub Category
		try {
			String strSubCategory = null;
			if (this.subCategory.getSelectedItem() != null) {
				strSubCategory = this.subCategory.getSelectedItem().getValue().toString();
			}
			if (strSubCategory != null && !PennantConstants.List_Select.equals(strSubCategory)) {
				aPSLDetail.setSubCategory(strSubCategory);

			} else {
				aPSLDetail.setSubCategory(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Purpose
		try {
			aPSLDetail.setPurpose(StringUtils.trimToNull(this.purpose.getValidatedValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// End Use
		try {
			aPSLDetail.setEndUse(StringUtils.trimToNull(this.endUse.getValidatedValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aPSLDetail.setLoanPurpose(StringUtils.trimToNull(this.loanPurpose.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aPSLDetail.setEligibleAmount(PennantApplicationUtil
					.unFormateAmount(this.eligibiltyAmount.getValidateValue(), PennantConstants.defaultCCYDecPos));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		rules.put("ASL_ELIGABLE_AMOUNT", aPSLDetail.getEligibleAmount());

		doRemoveValidation();
		doRemoveLOVValidation();
		if (!fromLoan) {
			if (!wve.isEmpty()) {
				WrongValueException[] wvea = new WrongValueException[wve.size()];
				for (int i = 0; i < wve.size(); i++) {
					wvea[i] = (WrongValueException) wve.get(i);
				}
				throw new WrongValuesException(wvea);
			}
		}
		logger.debug(Literal.LEAVING);
		return wve;
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param pSLDetail The entity that need to be render.
	 */
	public void doShowDialog(PSLDetail pSLDetail) {
		logger.debug(Literal.LEAVING);

		if (pSLDetail.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.finReference.focus();
		} else {
			this.finReference.setReadonly(true);

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(pSLDetail.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.categoryCode.focus();
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
				if (fromLoan && !enqiryModule) {
					doEdit();
				}
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		doWriteBeanToComponents(pSLDetail);

		if (fromLoan && !enqiryModule) {
			try {
				getFinanceMainDialogCtrl().getClass().getMethod("setpSLDetailDialogCtrl", this.getClass())
						.invoke(getFinanceMainDialogCtrl(), this);
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}
			if (parenttab != null) {
				this.parenttab.setVisible(true);
			}
		} else {
			if (enquirymode) {
				this.window_PSLDetailDialog.setHeight("80%");
				this.window_PSLDetailDialog.setWidth("80%");
				this.groupboxWf.setVisible(false);
				this.btnEdit.setVisible(false);
				this.btnDelete.setVisible(false);
				this.btnNotes.setVisible(false);
				doReadOnly();
				this.window_PSLDetailDialog.doModal();
			} else {
				setDialog(DialogType.EMBEDDED);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (this.categoryCode.isVisible()) {
			this.categoryCode.setConstraint(
					new StaticListValidator(categoryList, Labels.getLabel("label_PSLDetailDialog_CategoryCode.value")));
		}
		if (this.weakerSection.isVisible()) {
			this.weakerSection.setConstraint(
					new PTStringValidator(Labels.getLabel("label_PSLDetailDialog_WeakerSection.value"), null, true));
		}

		if (this.row_LandHolding.isVisible() && this.landArea.isVisible()
				&& landHolding.getSelectedItem().getValue() != null
				&& PennantConstants.YES.equals(landHolding.getSelectedItem().getValue())) {
			this.landArea.setConstraint(
					new StaticListValidator(landAreaList, Labels.getLabel("label_PSLDetailDialog_LandArea.value")));
		}

		if (this.row_Sector.isVisible() && this.sector.getSelectedItem().getValue() != null
				&& ("SVS".equals(this.sector.getSelectedItem().getValue())
						|| "MNF".equals(this.sector.getSelectedItem().getValue()))
				&& this.amount.isVisible()) {
			this.amount.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_PSLDetailDialog_Amount.value"), 2, true, false, 0));
		}

		if (this.row_Subcategory.isVisible() && this.subCategory.isVisible()) {
			this.subCategory.setConstraint(
					new PTStringValidator(Labels.getLabel("label_PSLDetailDialog_SubCategory.value"), null, true));
		}
		if (this.row_Purpose.isVisible() && this.purpose.isVisible()) {
			this.purpose.setConstraint(
					new PTStringValidator(Labels.getLabel("label_PSLDetailDialog_Purpose.value"), null, true));
		}

		if (this.row_EndUse.isVisible() && this.endUse.isVisible()) {
			this.endUse.setConstraint(
					new PTStringValidator(Labels.getLabel("label_PSLDetailDialog_EndUse.value"), null, true));
		}

		if (this.row_LandHolding.isVisible() && this.landHolding.isVisible()) {
			this.landHolding.setConstraint(new StaticListValidator(listLandHolding,
					Labels.getLabel("label_PSLDetailDialog_LandHolding.value")));
		}

		if (this.row_Sector.isVisible() && this.sector.isVisible()) {
			this.sector.setConstraint(
					new StaticListValidator(sectorList, Labels.getLabel("label_PSLDetailDialog_Sector.value")));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.categoryCode.setConstraint("");
		this.weakerSection.setConstraint("");
		this.landHolding.setConstraint("");
		this.landArea.setConstraint("");
		this.sector.setConstraint("");
		this.amount.setConstraint("");
		this.subCategory.setConstraint("");
		this.purpose.setConstraint("");
		this.endUse.setConstraint("");
		this.loanPurpose.setConstraint("");
		this.eligibiltyAmount.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);

		this.categoryCode.setErrorMessage("");
		this.weakerSection.setErrorMessage("");
		this.landHolding.setErrorMessage("");
		this.landArea.setErrorMessage("");
		this.sector.setErrorMessage("");
		this.amount.setErrorMessage("");
		this.subCategory.setErrorMessage("");
		this.purpose.setErrorMessage("");
		this.endUse.setErrorMessage("");
		this.loanPurpose.setErrorMessage("");
		this.eligibiltyAmount.setErrorMessage("");
		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final PSLDetail aPSLDetail = new PSLDetail();
		BeanUtils.copyProperties(this.pSLDetail, aPSLDetail);

		doDelete(aPSLDetail.getFinReference(), aPSLDetail);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.pSLDetail.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}

		readOnlyComponent(isReadOnly("PSLDetailDialog_CategoryCode"), this.categoryCode);
		readOnlyComponent(isReadOnly("PSLDetailDialog_WeakerSection"), this.weakerSection);
		readOnlyComponent(isReadOnly("PSLDetailDialog_LandHolding"), this.landHolding);
		readOnlyComponent(isReadOnly("PSLDetailDialog_LandArea"), this.landArea);
		readOnlyComponent(isReadOnly("PSLDetailDialog_Sector"), this.sector);
		readOnlyComponent(isReadOnly("PSLDetailDialog_Amount"), this.amount);
		readOnlyComponent(isReadOnly("PSLDetailDialog_SubCategory"), this.subCategory);
		readOnlyComponent(isReadOnly("PSLDetailDialog_Purpose"), this.purpose);
		readOnlyComponent(isReadOnly("PSLDetailDialog_EndUse"), this.endUse);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.pSLDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.LEAVING);

		// readOnlyComponent(true, this.finReference);
		readOnlyComponent(true, this.categoryCode);
		readOnlyComponent(true, this.weakerSection);
		readOnlyComponent(true, this.landHolding);
		readOnlyComponent(true, this.landArea);
		readOnlyComponent(true, this.sector);
		readOnlyComponent(true, this.amount);
		readOnlyComponent(true, this.subCategory);
		readOnlyComponent(true, this.purpose);
		readOnlyComponent(true, this.endUse);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);

		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		this.finReference.setValue("");
		this.categoryCode.setValue("");
		// this.categoryCode.setDescription("");
		this.weakerSection.setValue("");
		this.weakerSection.setDescription("");
		this.landHolding.setSelectedIndex(0);
		this.landArea.setSelectedIndex(0);
		this.sector.setSelectedIndex(0);
		this.amount.setValue("");
		this.subCategory.setValue("");
		// this.subCategory.setDescription("");
		this.purpose.setValue("");
		this.purpose.setDescription("");
		this.endUse.setValue("");
		this.endUse.setDescription("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final PSLDetail aPSLDetail = new PSLDetail();
		BeanUtils.copyProperties(this.pSLDetail, aPSLDetail);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aPSLDetail);

		isNew = aPSLDetail.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aPSLDetail.getRecordType())) {
				aPSLDetail.setVersion(aPSLDetail.getVersion() + 1);
				if (isNew) {
					aPSLDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aPSLDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aPSLDetail.setNewRecord(true);
				}
			}
		} else {
			aPSLDetail.setVersion(aPSLDetail.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aPSLDetail, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(PSLDetail aPSLDetail, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aPSLDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aPSLDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aPSLDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aPSLDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aPSLDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aPSLDetail);
				}

				if (isNotesMandatory(taskId, aPSLDetail)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}

				}
			}
			if (!StringUtils.isBlank(nextTaskId)) {
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

			aPSLDetail.setTaskId(taskId);
			aPSLDetail.setNextTaskId(nextTaskId);
			aPSLDetail.setRoleCode(getRole());
			aPSLDetail.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aPSLDetail, tranType);
			String operationRefs = getServiceOperations(taskId, aPSLDetail);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aPSLDetail, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aPSLDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader auditHeader
	 * @param method      (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		PSLDetail aPSLDetail = (PSLDetail) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = pSLDetailService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = pSLDetailService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = pSLDetailService.doApprove(auditHeader);

					if (aPSLDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = pSLDetailService.doReject(auditHeader);
					if (aPSLDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_PSLDetailDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_PSLDetailDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.pSLDetail), true);
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

		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(PSLDetail aPSLDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aPSLDetail.getBefImage(), aPSLDetail);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aPSLDetail.getUserDetails(),
				getOverideMap());
	}

	public void setPSLDetailService(PSLDetailService pSLDetailService) {
		this.pSLDetailService = pSLDetailService;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public void doSave(FinanceDetail aFd, Tab pslDetailsTab, boolean recSave) {

		logger.debug("Entering");

		doClearMessage();
		if (!recSave) {
			doSetValidation();
		}

		ArrayList<WrongValueException> wve = doWriteComponentsToBean(this.pSLDetail);
		FinScheduleData aSchdData = aFd.getFinScheduleData();
		FinanceMain aFm = aSchdData.getFinanceMain();
		this.pSLDetail.setFinID(aFm.getFinID());
		this.pSLDetail.setFinReference(aFm.getFinReference());

		if (!wve.isEmpty() && parenttab != null) {
			parenttab.setSelected(true);
		}

		showErrorDetails(wve);

		if (StringUtils.isBlank(this.pSLDetail.getRecordType())) {
			this.pSLDetail.setVersion(this.pSLDetail.getVersion() + 1);
			this.pSLDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			this.pSLDetail.setNewRecord(true);
		}

		this.pSLDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		this.pSLDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		this.pSLDetail.setUserDetails(getUserWorkspace().getLoggedInUser());
		aFd.setPslDetail(this.pSLDetail);

		logger.debug("Leaving");

	}

	private void showErrorDetails(ArrayList<WrongValueException> wve) {
		logger.debug("Entering");

		doRemoveValidation();

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			if (parenttab != null) {
				parenttab.setSelected(true);
			}
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}
}
