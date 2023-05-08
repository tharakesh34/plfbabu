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
 * * FileName : FinanceCheckListReferenceDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 08-12-2011 * * Modified Date : 08-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 08-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.facility.facility;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.facility.Facility;
import com.pennant.backend.model.lmtmasters.FacilityReferenceDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/LMTMasters/FinanceCheckListReference
 * /financeCheckListReferenceDialog.zul file.
 */
public class FacilityCheckListReferenceDialogCtrl extends GFCBaseCtrl<FinanceCheckListReference> {
	private static final long serialVersionUID = 4028305737293383251L;
	private static final Logger logger = LogManager.getLogger(FacilityCheckListReferenceDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	public Window window_FinanceCheckListReferenceDialog;
	// not auto wired variables
	protected Listbox listBox_CheckList;
	private Facility facility;

	private Map<Long, Long> selectedAnsCountMap;
	private Map<String, Textbox> commentsTxtBoxMap = new HashMap<String, Textbox>();
	private Map<String, String> ansDescMap;
	private Map<String, FinanceCheckListReference> prevAnswersMap;
	private Map<String, FinanceCheckListReference> temp_PrevAnswersMap = new HashMap<String, FinanceCheckListReference>();
	private Map<String, CheckListDetail> presentAnswersMap;

	/*
	 * Here we remove the checkList Details form list which are not allowed to show at this stage
	 */
	private Map<Long, FacilityReferenceDetail> notAllowedToShowMap;
	private Map<Long, FacilityReferenceDetail> notInputInStageMap;
	private Object ctrlObject = null;
	private Map<String, List<Listitem>> checkListDocTypeMap = null;
	private CustomerService customerService;
	private String userRole = "";
	private boolean dataChanged = false;
	private boolean enqModule = false;

	/**
	 * default constructor.<br>
	 */
	public FacilityCheckListReferenceDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected FinanceCheckListReference object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_FinanceCheckListReferenceDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinanceCheckListReferenceDialog);

		try {
			if (arguments.containsKey("facility")) {
				this.facility = (Facility) arguments.get("facility");
				Facility befImage = new Facility();
				BeanUtils.copyProperties(this.facility, befImage);
				this.facility.setBefImage(befImage);
				setFacility(this.facility);
			} else {
				setFacility(null);
			}
			if (arguments.containsKey("control")) {
				this.ctrlObject = (Object) arguments.get("control");
			}
			if (arguments.containsKey("userRole")) {
				userRole = (String) arguments.get("userRole");
			}

			if (arguments.containsKey("enqModule")) {
				enqModule = true;
			} else {
				enqModule = false;
			}
			doShowDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_FinanceCheckListReferenceDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Display the dialog.
	 * 
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void doShowDialog() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		logger.debug("Entering ");
		try {
			doWriteBeanToComponents(getFacility());
			getCtrlObject().getClass().getMethod("setFacilityCheckListReferenceDialogCtrl", this.getClass())
					.invoke(getCtrlObject(), this);
			getBorderLayoutHeight();
			this.listBox_CheckList.setHeight(this.borderLayoutHeight + "px");
			this.window_FinanceCheckListReferenceDialog.setHeight(this.borderLayoutHeight - 80 + "px");
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_FinanceCheckListReferenceDialog.onClose();
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method gets
	 * 
	 * @param aFacility
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void doWriteBeanToComponents(Facility aFacility) {
		logger.debug("Entering ");
		notAllowedToShowMap = new HashMap<Long, FacilityReferenceDetail>();
		notInputInStageMap = new HashMap<Long, FacilityReferenceDetail>();
		ansDescMap = new HashMap<String, String>();
		// Check List from Facility process editor
		List<FacilityReferenceDetail> checkList = aFacility.getCheckList();

		// Check List To display
		List<FacilityReferenceDetail> checkListToDisplay = new ArrayList<FacilityReferenceDetail>();

		Map<String, Object> fieldsandvalues = null;
		if (aFacility.getCustomerEligibilityCheck() != null) {
			fieldsandvalues = aFacility.getCustomerEligibilityCheck().getDeclaredFieldValues();
		} else {
			// Data Preparation for Rule Execution
			fieldsandvalues = doSetEngineData(aFacility.getCustID());
		}

		for (FacilityReferenceDetail finRefDetail : checkList) {
			if (isAllowedToShow(finRefDetail, userRole)) {
				if (StringUtils.isNotBlank(finRefDetail.getLovDescElgRuleValue())) {
					boolean isValid = (boolean) RuleExecutionUtil.executeRule(finRefDetail.getLovDescElgRuleValue(),
							fieldsandvalues, null, RuleReturnType.BOOLEAN);
					if (isValid) {
						notAllowedToShowMap.put(Long.valueOf(finRefDetail.getFinRefId()), finRefDetail);
						continue;
					}
				}
				checkListToDisplay.add(finRefDetail);
				if (!isAllowedInputInStage(finRefDetail, userRole)) {
					notInputInStageMap.put(Long.valueOf(finRefDetail.getFinRefId()), finRefDetail);
				}
			} else {
				notAllowedToShowMap.put(Long.valueOf(finRefDetail.getFinRefId()), finRefDetail);
			}
		}

		aFacility.setFinRefDetailsList(checkListToDisplay);

		prevAnswersMap = doGetPreviowsAnswers(aFacility);
		temp_PrevAnswersMap.putAll(prevAnswersMap);
		checkListDocTypeMap = new HashMap<String, List<Listitem>>();
		String remarks = "";

		// Create Items to Render in List box
		List<CheckListDetail> checkListToRender = new ArrayList<CheckListDetail>();
		for (FacilityReferenceDetail finRefDetail : checkListToDisplay) {
			List<CheckListDetail> checkListAnsDetails = finRefDetail.getLovDescCheckListAnsDetails();
			if (checkListAnsDetails != null && !checkListAnsDetails.isEmpty()) {
				for (CheckListDetail checkListAnsDetail : checkListAnsDetails) {
					checkListAnsDetail.setLovDescCheckListDesc(finRefDetail.getLovDescRefDesc());
					checkListAnsDetail.setLovDescCheckMinCount(finRefDetail.getLovDescCheckMinCount());
					checkListAnsDetail.setLovDescCheckMaxCount(finRefDetail.getLovDescCheckMaxCount());
					checkListAnsDetail.setLovDescFinRefId(finRefDetail.getFinRefId());

					checkListAnsDetail.setLovDescUserRole(userRole);
					String key = checkListAnsDetail.getCheckListId() + ";" + checkListAnsDetail.getAnsSeqNo();
					remarks = prevAnswersMap.get(key) == null ? "" : prevAnswersMap.get(key).getRemarks();
					checkListAnsDetail.setLovDescRemarks(remarks);
					ansDescMap.put(key, checkListAnsDetail.getAnsDesc());
					checkListToRender.add(checkListAnsDetail);
				}
			}
		}
		this.listBox_CheckList.setItemRenderer(new FinanceCheckListReferenceListModelItemRenderer());
		this.listBox_CheckList.setModel(new GroupsModelArray(checkListToRender.toArray(), new CheckListComparator()));
		logger.debug("Leaving ");
	}

	public boolean isAllowedToShow(FacilityReferenceDetail financeReferenceDetail, String userRole) {
		logger.debug("Entering");
		String showinStage = StringUtils.trimToEmpty(financeReferenceDetail.getShowInStage());
		if (showinStage.contains(",")) {
			String[] roles = showinStage.split(",");
			for (String string : roles) {
				if (userRole.equals(string)) {
					logger.debug("Leaving");
					return true;
				}
			}
		}
		logger.debug("Leaving");
		return false;
	}

	public boolean isAllowedInputInStage(FacilityReferenceDetail financeReferenceDetail, String userRole) {
		logger.debug("Entering");
		String showinStage = StringUtils.trimToEmpty(financeReferenceDetail.getAllowInputInStage());
		if (showinStage.contains(",")) {
			String[] roles = showinStage.split(",");
			for (String string : roles) {
				if (userRole.equals(string)) {
					logger.debug("Leaving");
					return true;
				}
			}
		}
		logger.debug("Leaving");
		return false;
	}

	public class FinanceCheckListReferenceListModelItemRenderer
			implements ListitemRenderer<CheckListDetail>, Serializable {
		private static final long serialVersionUID = -5988686000244488795L;

		public FinanceCheckListReferenceListModelItemRenderer() {
		    super();
		}

		@Override
		public void render(Listitem item, CheckListDetail checkListDetail, int count) {
			item.setSelected(false);

			if (item instanceof Listgroup) {
				StringBuilder builder = new StringBuilder(checkListDetail.getLovDescCheckListDesc());
				builder.append("  ");// To add Space between sentences
				builder.append(Labels.getLabel("Required_CheckList",
						new String[] { String.valueOf(checkListDetail.getLovDescCheckMinCount()),
								String.valueOf(checkListDetail.getLovDescCheckMaxCount()) }));
				item.appendChild(new Listcell(builder.toString()));
				item.setId(checkListDetail.getCheckListId() + "_LG");
			} else if (item instanceof Listgroupfoot) {
				Listcell cell = new Listcell("");
				cell.setSpan(2);
				item.appendChild(cell);
			} else {
				((Listbox) item.getParent()).setMultiple(true);
				if (checkListDetail.isDocRequired()) {
					for (DocumentDetails documentDetail : getFacility().getDocumentDetailsList()) {
						if (documentDetail.getDocCategory().equals(checkListDetail.getDocType())) {
							item.setSelected(true);
						}
					}
					List<Listitem> list = new ArrayList<Listitem>();
					if (checkListDocTypeMap.containsKey(checkListDetail.getDocType())) {
						list = checkListDocTypeMap.get(checkListDetail.getDocType());
						checkListDocTypeMap.remove(checkListDetail.getDocType());
					}
					list.add(item);
					checkListDocTypeMap.put(checkListDetail.getDocType(), list);
				}
				Listcell listCell = new Listcell(checkListDetail.getAnsDesc());
				listCell.setParent(item);
				listCell = new Listcell();
				if (checkListDetail.isDocRequired()) {
					item.setDisabled(true);
					if (!enqModule) {
						if (!notInputInStageMap.containsKey(checkListDetail.getLovDescFinRefId())) {
							Button uploadBtn = new Button("Upload");
							uploadBtn.setStyle(
									"background-color:#16a085;color:#ffffff !important;font-size:10px;padding:0px 2px;");
							listCell.appendChild(uploadBtn);
							uploadBtn.addForward("onClick", "", "onUploadRequiredDocument", checkListDetail);
						}
					}
					listCell.appendChild(new Space());
					Button viewBtn = new Button("View");
					viewBtn.setStyle(
							"background-color:#16a085;color:#ffffff !important;font-size:10px;padding:0px 2px;");
					listCell.appendChild(viewBtn);
					viewBtn.addForward("onClick", "", "onViewRequiredDocument", checkListDetail);
				}
				listCell.setParent(item);
				listCell = new Listcell();
				listCell.setId(checkListDetail.getCheckListId() + ";" + checkListDetail.getAnsSeqNo());
				Textbox txtBoxRemarks = new Textbox();
				txtBoxRemarks.setMaxlength(50);
				txtBoxRemarks.setWidth("400px");
				txtBoxRemarks.setVisible(false);
				ComponentsCtrl.applyForward(txtBoxRemarks, "onBlur=onBlurRemarksTextBox");
				txtBoxRemarks.setValue(checkListDetail.getLovDescRemarks());
				if (checkListDetail.isRemarksAllow()) {
					txtBoxRemarks.setVisible(true);
				}
				if (temp_PrevAnswersMap.containsKey(listCell.getId())) {
					item.setSelected(true);
				}
				if (notInputInStageMap.containsKey(checkListDetail.getLovDescFinRefId())) {
					item.setDisabled(true);
					txtBoxRemarks.setReadonly(true);
				} else {
					txtBoxRemarks.setReadonly(false);
				}
				listCell.appendChild(txtBoxRemarks);
				listCell.setParent(item);
			}
			item.setAttribute("data", checkListDetail);
			ComponentsCtrl.applyForward(item, "onClick=onSelectListItem");
		}
	}

	private Map<String, Object> doSetEngineData(long custId) {
		logger.debug("Entering");
		Map<String, Object> fieldsAndValues = new HashMap<String, Object>();
		Customer aCustomer = getCustomerService().getApprovedCustomerById(custId);
		// Set Customer Data to check the eligibility
		fieldsAndValues.put("custAge", DateUtil.getYearsBetween(aCustomer.getCustDOB(), DateUtil.getSysDate()));
		fieldsAndValues.put("custCtgCode", aCustomer.getCustCtgCode());
		fieldsAndValues.put("custTypeCode", aCustomer.getCustTypeCode());
		fieldsAndValues.put("custDftBranch", aCustomer.getCustDftBranch());
		fieldsAndValues.put("custGenderCode", aCustomer.getCustGenderCode());
		fieldsAndValues.put("custDOB", aCustomer.getCustDOB());
		fieldsAndValues.put("custCOB", aCustomer.getCustCOB());
		fieldsAndValues.put("custIsMinor", aCustomer.isCustIsMinor());
		fieldsAndValues.put("custGroupID", aCustomer.getCustGroupID());
		fieldsAndValues.put("custSts", aCustomer.getCustSts());
		fieldsAndValues.put("custGroupSts", aCustomer.getCustGroupSts());
		fieldsAndValues.put("custIsBlocked", aCustomer.isCustIsBlocked());
		fieldsAndValues.put("custIsActive", aCustomer.isCustIsActive());
		fieldsAndValues.put("custIsClosed", aCustomer.isCustIsClosed());
		fieldsAndValues.put("custIsDecease", aCustomer.isCustIsDecease());
		fieldsAndValues.put("custIsDormant", aCustomer.isCustIsDormant());
		fieldsAndValues.put("custIsDelinquent", aCustomer.isCustIsDelinquent());
		fieldsAndValues.put("custIsTradeFinCust", aCustomer.isCustIsTradeFinCust());
		fieldsAndValues.put("custIsStaff", aCustomer.isCustIsStaff());
		fieldsAndValues.put("custIndustry", aCustomer.getCustIndustry());
		fieldsAndValues.put("custSector", aCustomer.getCustSector());
		fieldsAndValues.put("custSubSector", aCustomer.getCustSubSector());
		fieldsAndValues.put("custProfession", aCustomer.getCustProfession());
		fieldsAndValues.put("custTotalIncome", aCustomer.getCustTotalIncome());
		fieldsAndValues.put("custMaritalSts", aCustomer.getCustMaritalSts());
		fieldsAndValues.put("custEmpSts", aCustomer.getCustEmpSts());
		fieldsAndValues.put("custSegment", aCustomer.getCustSegment());
		fieldsAndValues.put("custSubSegment", aCustomer.getCustSubSegment());
		fieldsAndValues.put("custIsBlackListed", aCustomer.isCustIsBlackListed());
		fieldsAndValues.put("custIsRejected", aCustomer.isCustIsRejected());
		fieldsAndValues.put("custParentCountry", aCustomer.getCustParentCountry());
		fieldsAndValues.put("custResdCountry", aCustomer.getCustResdCountry());
		fieldsAndValues.put("custRiskCountry", aCustomer.getCustRiskCountry());
		fieldsAndValues.put("custNationality", aCustomer.getCustNationality());
		fieldsAndValues.put("custParentCountry", aCustomer.getCustParentCountry());
		logger.debug("Leaving");

		return fieldsAndValues;
	}

	class CheckListComparator implements Comparator<CheckListDetail>, Serializable {
		private static final long serialVersionUID = 9112640872865877333L;

		public CheckListComparator() {
		    super();
		}

		@Override
		public int compare(CheckListDetail data1, CheckListDetail data2) {
			return String.valueOf(data1.getCheckListId()).compareTo(String.valueOf(data2.getCheckListId()));
		}
	}

	/**
	 * This method gets all answers for checkList and prepares the List<finCheckListReference>
	 */
	@SuppressWarnings("rawtypes")
	private void doWriteComponentsToBean(Facility aFinanceDetail) {
		logger.debug("Entering ");
		dataChanged = false;
		List<FinanceCheckListReference> finCheckListRefList = new ArrayList<FinanceCheckListReference>();

		List<CheckListDetail> checkListDetailsList = new ArrayList<CheckListDetail>();
		presentAnswersMap = new HashMap<String, CheckListDetail>();
		selectedAnsCountMap = new HashMap<Long, Long>();

		// To get Selected Items
		Set seletedSet = this.listBox_CheckList.getSelectedItems();
		if (seletedSet != null) {
			for (Object object : seletedSet) {
				if (object instanceof Listitem && !(object instanceof Listgroup)) {
					Listitem listitem = (Listitem) object;
					Listcell listCell = (Listcell) listitem.getChildren().get(2);
					Textbox txtboxRemarks = (Textbox) listCell.getChildren().get(0);
					final CheckListDetail aCheckListDetail = (CheckListDetail) listitem.getAttribute("data");
					aCheckListDetail.setLovDescRemarks(txtboxRemarks.getValue());
					if (aCheckListDetail.isRemarksMand()) {
						commentsTxtBoxMap.put(listCell.getId(), txtboxRemarks);
					}
					checkListDetailsList.add(aCheckListDetail);
				}
			}
		}
		// taking count of selected answers
		for (CheckListDetail checkListDetail : checkListDetailsList) {
			if (!notAllowedToShowMap.containsKey(Long.valueOf(checkListDetail.getCheckListId()))) {
				if (!notInputInStageMap.containsKey(Long.valueOf(checkListDetail.getCheckListId()))) {
					if (!selectedAnsCountMap.containsKey(Long.valueOf(checkListDetail.getCheckListId()))) {
						selectedAnsCountMap.put(Long.valueOf(checkListDetail.getCheckListId()), Long.valueOf(1));
					} else if (selectedAnsCountMap.containsKey(Long.valueOf(checkListDetail.getCheckListId()))) {
						long answerCount = selectedAnsCountMap.get(Long.valueOf(checkListDetail.getCheckListId()));
						selectedAnsCountMap.remove(Long.valueOf(checkListDetail.getCheckListId()));
						selectedAnsCountMap.put(Long.valueOf(checkListDetail.getCheckListId()),
								Long.valueOf(answerCount + 1));
					}
					String key = checkListDetail.getCheckListId() + ";" + checkListDetail.getAnsSeqNo();
					presentAnswersMap.put(key, checkListDetail);
				}
			}
		}

		aFinanceDetail.setLovDescSelAnsCountMap(selectedAnsCountMap);
		aFinanceDetail.getLovDescSelAnsCountMap().putAll(selectedAnsCountMap);
		// For unselected check list details
		for (String questionId : prevAnswersMap.keySet()) {
			FinanceCheckListReference finChkListRef = new FinanceCheckListReference();
			finChkListRef.setQuestionId(prevAnswersMap.get(questionId).getQuestionId());
			finChkListRef.setLovDescQuesDesc(prevAnswersMap.get(questionId).getLovDescQuesDesc());
			finChkListRef.setAnswer(prevAnswersMap.get(questionId).getAnswer());
			String key = finChkListRef.getQuestionId() + ";" + finChkListRef.getAnswer();
			finChkListRef.setLovDescAnswerDesc(ansDescMap.get(key));
			String remarks = commentsTxtBoxMap.get(key) == null ? "" : commentsTxtBoxMap.get(key).getValue();
			finChkListRef.setRemarks(remarks);
			finChkListRef.setFinReference(getFacility().getCAFReference());
			if (notAllowedToShowMap.containsKey(prevAnswersMap.get(questionId).getQuestionId())) {
				continue;
			}
			if (!presentAnswersMap.containsKey(questionId)) {
				if (!notInputInStageMap.containsKey(finChkListRef.getQuestionId())) {
					finChkListRef.setRecordType(PennantConstants.RECORD_TYPE_CAN);
					finChkListRef.setBefImage(prevAnswersMap.get(questionId));
					finCheckListRefList.add(finChkListRef);
					dataChanged = true;
				}
			} else {
				finChkListRef.setBefImage(prevAnswersMap.get(questionId));
				if (!StringUtils.equals(finChkListRef.getRemarks().trim(),
						finChkListRef.getBefImage().getRemarks().trim())) {
					finChkListRef.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					dataChanged = true;
				} else {
					finChkListRef.setRecordType("");
				}
				finCheckListRefList.add(finChkListRef);
			}
		}
		// For Selected check list Details
		for (String questionId : presentAnswersMap.keySet()) {
			FinanceCheckListReference finChkListRef = new FinanceCheckListReference();
			if (!prevAnswersMap.containsKey(questionId)) {
				finChkListRef.setRecordType(PennantConstants.RCD_ADD);
				finChkListRef.setNewRecord(true);
				finChkListRef.setQuestionId(presentAnswersMap.get(questionId).getCheckListId());
				finChkListRef.setAnswer(presentAnswersMap.get(questionId).getAnsSeqNo());
				finChkListRef.setFinReference(getFacility().getCAFReference());
				finChkListRef.setLovDescAnswerDesc(presentAnswersMap.get(questionId).getAnsDesc());
				String key = finChkListRef.getQuestionId() + ";" + finChkListRef.getAnswer();
				String remarks = commentsTxtBoxMap.get(key) == null ? "" : commentsTxtBoxMap.get(key).getValue();
				finChkListRef.setRemarks(remarks);
				finChkListRef.setLovDescSelAnsCountMap(selectedAnsCountMap);
				finCheckListRefList.add(finChkListRef);
				dataChanged = true;
			}
		}
		if (aFinanceDetail.getFinanceCheckList() != null) {
			for (FinanceCheckListReference reference : aFinanceDetail.getFinanceCheckList()) {
				boolean contains = false;
				for (FinanceCheckListReference newListReference : finCheckListRefList) {
					if (newListReference.getFinReference().equals(reference.getFinReference())
							&& newListReference.getQuestionId() == reference.getQuestionId()
							&& newListReference.getAnswer() == reference.getAnswer()) {
						contains = true;
					}
				}
				if (!contains) {
					finCheckListRefList.add(reference);
				}
			}
		}

		aFinanceDetail.setFinanceCheckList(finCheckListRefList);
		setFacility(aFinanceDetail);
		logger.debug("Leaving ");
	}

	/**
	 * Method to validate checklist
	 * 
	 * @param auditDetail (AuditDetail)
	 * @param usrLanguage (String)
	 * @param method      (String)
	 * @return auditDetail
	 */
	private ArrayList<WrongValueException> validation_CheckList(Facility facility, String usrLanguage, String method) {
		logger.debug("Entering ");

		Map<Long, Long> selAnsCountMap = new HashMap<Long, Long>();
		Set<Listitem> selSet = this.listBox_CheckList.getSelectedItems();
		for (Listitem listitem : selSet) {
			final CheckListDetail aCheckListDetail = (CheckListDetail) listitem.getAttribute("data");
			if (selAnsCountMap.containsKey(aCheckListDetail.getCheckListId())) {
				selAnsCountMap.put(aCheckListDetail.getCheckListId(),
						selAnsCountMap.get(aCheckListDetail.getCheckListId()) + Long.valueOf(1));
			} else {
				selAnsCountMap.put(aCheckListDetail.getCheckListId(), Long.valueOf(1));
			}
		}
		if (facility.getFinRefDetailsList() != null) {
			ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
			for (FacilityReferenceDetail aFinRefDetail : facility.getFinRefDetailsList()) {
				boolean valid = true;
				if (!isAllowedInputInStage(aFinRefDetail, userRole)) {
					continue;
				}

				if (selAnsCountMap.containsKey(Long.valueOf(aFinRefDetail.getFinRefId()))) {
					long selCount = selAnsCountMap.get(Long.valueOf(aFinRefDetail.getFinRefId()));
					if ((selCount) > (aFinRefDetail.getLovDescCheckMaxCount())
							|| (selCount) < (aFinRefDetail.getLovDescCheckMinCount())) {
						valid = false;
					}
				} else {
					valid = false;
				}
				if (!valid) {
					String[] errParm = new String[3];
					String[] valueParm = new String[1];
					errParm[0] = Long.toString(aFinRefDetail.getLovDescCheckMinCount());
					errParm[1] = Long.toString(aFinRefDetail.getLovDescCheckMaxCount());
					errParm[2] = aFinRefDetail.getLovDescRefDesc();
					wve.add(new WrongValueException(
							this.listBox_CheckList.getFellowIfAny(aFinRefDetail.getFinRefId() + "_LG"),
							ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "30701", errParm, valueParm),
									usrLanguage).getError()));
				}
			}
			return wve;
		}
		logger.debug("Leaving ");
		return null;
	}

	/**
	 * This method retrieves previously selected answers of checkList for this finance reference and keeps those in
	 * prevAnswersMap where key is question Id and value is answer like 'A' or 'B'
	 * 
	 * @param financeMain
	 * @param answersRadiogroup
	 */
	public Map<String, FinanceCheckListReference> doGetPreviowsAnswers(Facility aFinanceDetail) {
		logger.debug("Entering ");
		prevAnswersMap = new HashMap<String, FinanceCheckListReference>();
		List<FinanceCheckListReference> finCheckRefList = aFinanceDetail.getFinanceCheckList();
		if (finCheckRefList != null && !finCheckRefList.isEmpty()) {
			for (FinanceCheckListReference finCheckListRef : finCheckRefList) {
				prevAnswersMap.put(String.valueOf(finCheckListRef.getQuestionId() + ";" + finCheckListRef.getAnswer()),
						finCheckListRef);
			}
		}
		logger.debug("Leaving ");
		return prevAnswersMap;
	}

	/**
	 * This method validated check list for mandatory Remarks input
	 */
	public void doCheckListValidation(Facility financeDetail) {
		logger.debug("Entering ");
		doClearErrorMessages();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		/* Check whether answer is selected or not if not selected throw error */
		for (FinanceCheckListReference finChkListRef : financeDetail.getFinanceCheckList()) {
			try {
				String key = finChkListRef.getQuestionId() + ";" + finChkListRef.getAnswer();
				if (commentsTxtBoxMap.containsKey(key)) {
					if (!finChkListRef.getRecordType().equals(PennantConstants.RCD_DEL)) {
						if (StringUtils.isEmpty(commentsTxtBoxMap.get(key).getValue().trim())) {
							throw new WrongValueException(commentsTxtBoxMap.get(key),
									Labels.getLabel("label_ChecList_Must_EnterRemarks"));
						}
					}
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		showErrorDetails(wve);
		logger.debug("Leaving ");
	}

	/**
	 * Method for Validating Check List Details
	 * 
	 * @param event
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void onChkListValidation(Event event) {
		logger.debug("Entering");
		Map<String, Object> map = new HashMap<String, Object>();
		String userAction = "";
		Facility facility = null;
		if (event.getData() != null) {
			map = (Map<String, Object>) event.getData();
		}
		if (map.containsKey("control")) {
			this.ctrlObject = (Object) map.get("control");
		}
		if (map.containsKey("facility")) {
			facility = (Facility) map.get("facility");
		}
		if (map.containsKey("userAction")) {
			userAction = (String) map.get("userAction");
		}

		doClearErrorMessages();
		if (getCtrlObject() != null) {
			doWriteComponentsToBean(facility);
			doCheckListValidation(facility);
			if (map.containsKey("agreement")) {
				map.put("Error",
						validation_CheckList(facility, getUserWorkspace().getUserLanguage(), facility.getUserAction()));
			} else {
				if (!("Save".equalsIgnoreCase(userAction) || "Cancel".equalsIgnoreCase(userAction))) {
					map.put("Error", validation_CheckList(facility, getUserWorkspace().getUserLanguage(),
							facility.getUserAction()));
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Event for checking whethter data has been changed before closing
	 * 
	 * @param event
	 * @return
	 */
	public void onCheckListClose(Event event) {
		logger.debug("Entering" + event.toString());
		doClearErrorMessages();
		if (getCtrlObject() != null) {
			try {
				getCtrlObject().getClass().getMethod("setAssetDataChanged", Boolean.class).invoke(getCtrlObject(),
						isDataChanged(getFacility()));
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * This method reads all components data
	 */
	public boolean isDataChanged(Facility aFinanceDetail) {
		logger.debug("Entering ");
		doWriteComponentsToBean(aFinanceDetail);
		return dataChanged;
	}

	/**
	 * This method clears error messages on check list
	 */
	public void doClearErrorMessages() {
		logger.debug("Entering ");
		for (String questionId : commentsTxtBoxMap.keySet()) {
			commentsTxtBoxMap.get(questionId).setErrorMessage("");
		}
		logger.debug("Leaving ");
	}

	/**
	 * Writes the showErrorDetails method for .<br>
	 * displaying exceptions if occurred
	 */
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
	 * This event stores the text box value into bean
	 * 
	 * @param event
	 */
	public void onBlurRemarksTextBox(ForwardEvent event) {
		logger.debug("Entering ");
		// (Quick fix will be changed later)
		Textbox textbox = (Textbox) event.getOrigin().getTarget();
		@SuppressWarnings("rawtypes")
		List list = this.listBox_CheckList.getItems();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) instanceof Listitem && !(list.get(i) instanceof Listgroup)) {
				Listitem listitem = (Listitem) list.get(i);
				Listcell listCell = (Listcell) listitem.getChildren().get(1);
				if (StringUtils.equals(textbox.getParent().getId(), listCell.getId())) {
					final CheckListDetail aCheckList = (CheckListDetail) listitem.getAttribute("data");
					aCheckList.setLovDescRemarks(textbox.getValue());
					break;
				}
			}
		}
		logger.debug("Leaving ");
	}

	/**
	 * onSelect event for listitem
	 * 
	 * @param event
	 */
	public void onSelectListItem(ForwardEvent event) {
		logger.debug("Entering " + event.toString());
		Listitem listitem = (Listitem) event.getOrigin().getTarget();
		if (listitem instanceof Listitem && !(listitem instanceof Listgroup)) {
			Listcell listCell = (Listcell) listitem.getChildren().get(1);
			if (listitem.isSelected()) {
				if (!temp_PrevAnswersMap.containsKey(listCell.getId())) {
					temp_PrevAnswersMap.put(listCell.getId(), new FinanceCheckListReference());
				}
			} else {
				if (temp_PrevAnswersMap.containsKey(listCell.getId())) {
					temp_PrevAnswersMap.remove(listCell.getId());
				}
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	public void onUploadRequiredDocument(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		CheckListDetail checkListDetail = (CheckListDetail) event.getData();
		if (getCtrlObject() != null) {
			try {
				if (getCtrlObject().getClass().getMethod("getFacilityDocumentDetailDialogCtrl") != null) {
					FacilityDocumentDetailDialogCtrl docDialogCtrl = (FacilityDocumentDetailDialogCtrl) getCtrlObject()
							.getClass().getMethod("getFacilityDocumentDetailDialogCtrl").invoke(getCtrlObject());
					if (docDialogCtrl != null) {
						Map<String, DocumentDetails> docDetailMap = docDialogCtrl.getDocDetailMap();
						if (docDetailMap != null && docDetailMap.containsKey(checkListDetail.getDocType())) {
							docDialogCtrl.updateExistingDocument(docDetailMap.get(checkListDetail.getDocType()), true,
									false);
						} else {
							docDialogCtrl.createNewDocument(checkListDetail, true, updateFinanceMain());
						}
					}
				}
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	public void onViewRequiredDocument(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		CheckListDetail checkListDetail = (CheckListDetail) event.getData();
		String docType = checkListDetail.getDocType();
		if (getCtrlObject() != null) {
			try {
				if (getCtrlObject().getClass().getMethod("getFacilityDocumentDetailDialogCtrl") != null) {
					FacilityDocumentDetailDialogCtrl docDialogCtrl = (FacilityDocumentDetailDialogCtrl) getCtrlObject()
							.getClass().getMethod("getFacilityDocumentDetailDialogCtrl").invoke(getCtrlObject());
					if (docDialogCtrl != null) {
						Map<String, DocumentDetails> docDetailMap = docDialogCtrl.getDocDetailMap();
						if (docDetailMap != null && docDetailMap.containsKey(docType)) {
							DocumentDetails finDocumentDetail = docDetailMap.get(docType);
							if (DocumentCategories.CUSTOMER.getKey().equals(checkListDetail.getCategoryCode())) {
								finDocumentDetail.setCategoryCode(DocumentCategories.CUSTOMER.getKey());
							}
							docDialogCtrl.updateExistingDocument(finDocumentDetail, true, true);
						} else {
							MessageUtil.showError("Document not Yet uploaded.");
						}
					}
				}
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Update Finance Main Details from the Finance Main Ctrl
	 * 
	 * @return
	 */
	private Facility updateFinanceMain() {
		Facility main = null;
		try {
			Object object = getCtrlObject().getClass().getMethod("getFacility").invoke(getCtrlObject());
			if (object != null) {
				main = (Facility) object;
				return main;
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		return null;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public Facility getFacility() {
		return facility;
	}

	public void setFacility(Facility facility) {
		this.facility = facility;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public Object getCtrlObject() {
		return ctrlObject;
	}

	public void setCtrlObject(Object object) {
		this.ctrlObject = object;
	}

	public Map<String, List<Listitem>> getCheckListDocTypeMap() {
		return checkListDocTypeMap;
	}

	public void setCheckListDocTypeMap(Map<String, List<Listitem>> checkListDocTypeMap) {
		this.checkListDocTypeMap = checkListDocTypeMap;
	}
}
