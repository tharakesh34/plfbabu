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
 * FileName    		:  FinanceCheckListReferenceDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-12-2011    														*
 *                                                                  						*
 * Modified Date    :  08-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-12-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.lmtmasters.financechecklistreference;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
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
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.finance.financemain.DocumentDetailDialogCtrl;
import com.pennant.webui.finance.financemain.FinanceMainDialogCtrl;
import com.pennant.webui.finance.financemain.FinanceMainQDEDialogCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/LMTMasters/FinanceCheckListReference
 * /financeCheckListReferenceDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FinanceCheckListReferenceDialogCtrl extends GFCBaseListCtrl<FinanceCheckListReference> implements Serializable {
	private static final long serialVersionUID = 4028305737293383251L;
	private final static Logger logger = Logger.getLogger(FinanceCheckListReferenceDialogCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	public Window window_FinanceCheckListReferenceDialog; // autoWired
	// not auto wired variables
	protected Listbox listBox_CheckList; // autoWired
	private FinanceDetail financeDetail; // over handed per parameters
	private Map<Long, Long> selectedAnsCountMap;
	private Map<String, Textbox> commentsTxtBoxMap = new HashMap<String, Textbox>();
	private Map<String, String> ansDescMap;
	private Map<String, FinanceCheckListReference> prevAnswersMap;
	private Map<String, FinanceCheckListReference> temp_PrevAnswersMap = new HashMap<String, FinanceCheckListReference>();
	private Map<String, CheckListDetail> presentAnswersMap;
	
	/*
	 * Here we remove the checkList Details form list which are not allowed to
	 * show at this stage
	 */
	private Map<Long, FinanceReferenceDetail> notAllowedToShowMap;
	private Map<Long, FinanceReferenceDetail> notInputInStageMap;
	private Object financeMainDialogCtrl = null;
	private FinanceMainQDEDialogCtrl financeMainQDEDialogCtrl = null;
	private Map<String, List<Listitem>> checkListDocTypeMap = null;
	private RuleExecutionUtil ruleExecutionUtil;
	private CustomerService customerService;
	private Tabpanel panel = null;
	private String userRole = "";
	private boolean dataChanged = false;

	/**
	 * default constructor.<br>
	 */
	public FinanceCheckListReferenceDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected
	 * FinanceCheckListReference object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinanceCheckListReferenceDialog(Event event) throws Exception {
		logger.debug(event.toString());
		if (event.getTarget().getParent() != null) {
			panel = (Tabpanel) event.getTarget().getParent();
		}
		final Map<String, Object> args = getCreationArgsMap(event);
		if (args.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) args.get("financeDetail");
			FinanceDetail befImage = new FinanceDetail();
			BeanUtils.copyProperties(this.financeDetail, befImage);
			this.financeDetail.setBefImage(befImage);
			setFinanceDetail(this.financeDetail);
		} else {
			setFinanceDetail(null);
		}
		if (args.containsKey("financeMainDialogCtrl")) {
			this.financeMainDialogCtrl = (Object) args.get("financeMainDialogCtrl");
		}
		if (args.containsKey("financeMainQDEDialogCtrl")) {
			this.financeMainQDEDialogCtrl = (FinanceMainQDEDialogCtrl) args.get("financeMainQDEDialogCtrl");
		}
		if (args.containsKey("userRole")) {
			userRole = (String) args.get("userRole");
		}
		doShowDialog();
		logger.debug("Leaving");
	}

	/**
	 * 
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 */
	public void doShowDialog() throws InterruptedException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering ");
		doWriteBeanToComponents(getFinanceDetail());
		if (panel != null) {
			getFinanceMainDialogCtrl().getClass().getMethod("setFinanceCheckListReferenceDialogCtrl", this.getClass()).invoke(getFinanceMainDialogCtrl(), this);
			getBorderLayoutHeight();
			this.listBox_CheckList.setHeight(this.borderLayoutHeight + "px");
			this.window_FinanceCheckListReferenceDialog.setHeight(this.borderLayoutHeight - 70 + "px");
			panel.appendChild(this.window_FinanceCheckListReferenceDialog);
		} else {
			setDialog(this.window_FinanceCheckListReferenceDialog);
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method gets
	 * 
	 * @param aFinanceDetail
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void doWriteBeanToComponents(FinanceDetail aFinanceDetail) {
		logger.debug("Entering ");
		List<FinanceReferenceDetail> checkList = aFinanceDetail.getCheckList();
		notAllowedToShowMap = new HashMap<Long, FinanceReferenceDetail>();
		notInputInStageMap = new HashMap<Long, FinanceReferenceDetail>();
		ansDescMap = new HashMap<String, String>();
		List<FinanceReferenceDetail> tempCheckList = new ArrayList<FinanceReferenceDetail>();
		
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
		HashMap<String, Object> fieldsandvalues = new HashMap<String, Object>();
		
		if (aFinanceDetail.getCustomerEligibilityCheck() != null) {
			fieldsandvalues = aFinanceDetail.getCustomerEligibilityCheck().getDeclaredFieldValues();
			ArrayList<String> keyset = new ArrayList<String>(fieldsandvalues.keySet());
			for (String string : keyset) {
				Object var = fieldsandvalues.get(string);
				if (var instanceof String) {
					var = var.toString().trim();
				}
				engine.put(string, var);
			}
		} else {
			if (aFinanceDetail.getCustomerEligibilityCheck() == null) {
				long custId = 0;
				if (!StringUtils.trimToEmpty(aFinanceDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF()).equals("")) {
					custId = aFinanceDetail.getFinScheduleData().getFinanceMain().getCustID();
					// Data Preparation for Rule Execution
					doSetEngineData(engine, custId);
					engine.put("reqProduct", aFinanceDetail.getFinScheduleData().getFinanceMain().getLovDescProductCodeName());
				}
			} 
		}
		
		for (FinanceReferenceDetail finRefDetail : checkList) {
			if (!finRefDetail.getShowInStage().contains(userRole)) {
				notAllowedToShowMap.put(Long.valueOf(finRefDetail.getFinRefId()), finRefDetail);
			}
			if (!finRefDetail.getAllowInputInStage().contains(userRole)) {
				if (!StringUtils.trimToEmpty(finRefDetail.getLovDescElgRuleValue()).equals("")) {
					Object result = getRuleExecutionUtil().processEngineRule(finRefDetail.getLovDescElgRuleValue(), engine, SystemParameterDetails.getGlobaVariableList(), "");
					if (result != null && Integer.parseInt(result.toString()) == 1) {
						notInputInStageMap.put(Long.valueOf(finRefDetail.getFinRefId()), finRefDetail);
					} else {
						if (!notAllowedToShowMap.containsKey(Long.valueOf(finRefDetail.getFinRefId()))) {
							notAllowedToShowMap.put(Long.valueOf(finRefDetail.getFinRefId()), finRefDetail);
						}
					}
				} else {
					notInputInStageMap.put(Long.valueOf(finRefDetail.getFinRefId()), finRefDetail);
				}
			} else {
				if (!StringUtils.trimToEmpty(finRefDetail.getLovDescElgRuleValue()).equals("")) {
					Object result = getRuleExecutionUtil().processEngineRule(finRefDetail.getLovDescElgRuleValue(), engine, SystemParameterDetails.getGlobaVariableList(), "");
					if (result != null && Integer.parseInt(result.toString()) == 1) {
						tempCheckList.add(finRefDetail);
					} else {
						if (!notAllowedToShowMap.containsKey(Long.valueOf(finRefDetail.getFinRefId()))) {
							notAllowedToShowMap.put(Long.valueOf(finRefDetail.getFinRefId()), finRefDetail);
						}
					}
				} else {
					tempCheckList.add(finRefDetail);
				}
			}
		}
		aFinanceDetail.setFinRefDetailsList(tempCheckList);
		prevAnswersMap = doGetPreviowsAnswers(aFinanceDetail);
		temp_PrevAnswersMap.putAll(prevAnswersMap);
		List<CheckListDetail> checkListDetailsList = new ArrayList<CheckListDetail>();
		checkListDocTypeMap = new HashMap<String, List<Listitem>>();
		String remarks = "";
		for (FinanceReferenceDetail finRefDetail : checkList) {
			if (!notAllowedToShowMap.containsKey(Long.valueOf(finRefDetail.getFinRefId()))) {
				for (CheckListDetail checkListDetail : finRefDetail.getLovDesccheckListDetail()) {
					checkListDetail.setLovDescCheckListDesc(finRefDetail.getLovDescRefDesc());
					checkListDetail.setLovDescCheckMinCount(finRefDetail.getLovDescCheckMinCount());
					checkListDetail.setLovDescCheckMaxCount(finRefDetail.getLovDescCheckMaxCount());
					checkListDetail.setLovDescFinRefDetail(finRefDetail);
					checkListDetail.setLovDescUserRole(userRole);
					String key=checkListDetail.getCheckListId() + ";" + checkListDetail.getAnsSeqNo();
					remarks = prevAnswersMap.get(key) == null ? "" : prevAnswersMap.get(key).getRemarks();
					checkListDetail.setLovDescRemarks(remarks);
					checkListDetail.setLovDescPrevAnsMap(temp_PrevAnswersMap);
					checkListDetailsList.add(checkListDetail);
					ansDescMap.put(key, checkListDetail.getAnsDesc());
				}
			}
		}
		this.listBox_CheckList.setItemRenderer(new FinanceCheckListReferenceListModelItemRenderer());
		this.listBox_CheckList.setModel(new GroupsModelArray(checkListDetailsList.toArray(), new CheckListComparator()));
		logger.debug("Leaving ");
	}

	public class FinanceCheckListReferenceListModelItemRenderer implements ListitemRenderer<CheckListDetail>, Serializable {
		private static final long serialVersionUID = -5988686000244488795L;

		@Override
		public void render(Listitem item, CheckListDetail checkListDetail, int count) throws Exception {
			item.setSelected(false);
			
			if (item instanceof Listgroup) {
				StringBuilder builder=new StringBuilder(checkListDetail.getLovDescCheckListDesc());
				builder.append("  ");//To add Space between sentences
				builder.append(Labels.getLabel("Required_CheckList",new String[]{String.valueOf(checkListDetail.getLovDescCheckMinCount()),String.valueOf(checkListDetail.getLovDescCheckMaxCount())}));
				item.appendChild(new Listcell(builder.toString()));
			} else if (item instanceof Listgroupfoot) {
				Listcell cell = new Listcell("");
				cell.setSpan(2);
				item.appendChild(cell);
			} else {
				((Listbox) item.getParent()).setMultiple(true);
				if (checkListDetail.isDocRequired()) {
					for (DocumentDetails documentDetail : financeDetail.getDocumentDetailsList()) {
						if(documentDetail.getDocCategory().equals(checkListDetail.getDocType())){
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
					if (checkListDetail.getLovDescFinRefDetail().getAllowInputInStage().contains(checkListDetail.getLovDescUserRole())) {
						Button uploadBtn = new Button("Upload");
						uploadBtn.setStyle("background-color:#16a085;color:#ffffff !important;font-size:10px;padding:0px 2px;");
						listCell.appendChild(uploadBtn);
						uploadBtn.addForward("onClick", "", "onUploadRequiredDocument", checkListDetail);
					}
					listCell.appendChild(new Space());
					Button viewBtn = new Button("View");
					viewBtn.setStyle("background-color:#16a085;color:#ffffff !important;font-size:10px;padding:0px 2px;");
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
				if (checkListDetail.getLovDescPrevAnsMap().containsKey(listCell.getId())) {
					item.setSelected(true);
				}
				if (!checkListDetail.getLovDescFinRefDetail().getAllowInputInStage().contains(checkListDetail.getLovDescUserRole())) {
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

	private void doSetEngineData(ScriptEngine engine, long custId) {
		logger.debug("Entering");
		Customer aCustomer = getCustomerService().getApprovedCustomerById(custId);
		
		// Set Customer Data to check the eligibility
		engine.put("custAge", DateUtility.getYearsBetween(aCustomer.getCustDOB(), DateUtility.today()));
		engine.put("custCtgCode", aCustomer.getCustCtgCode());
		engine.put("custTypeCode", aCustomer.getCustTypeCode());
		engine.put("custDftBranch", aCustomer.getCustDftBranch());
		engine.put("custGenderCode", aCustomer.getCustGenderCode());
		engine.put("custDOB", aCustomer.getCustDOB());
		engine.put("custCOB", aCustomer.getCustCOB());
		engine.put("custIsMinor", aCustomer.isCustIsMinor());
		engine.put("custGroupID", aCustomer.getCustGroupID());
		engine.put("custSts", aCustomer.getCustSts());
		engine.put("custGroupSts", aCustomer.getCustGroupSts());
		engine.put("custIsBlocked", aCustomer.isCustIsBlocked());
		engine.put("custIsActive", aCustomer.isCustIsActive());
		engine.put("custIsClosed", aCustomer.isCustIsClosed());
		engine.put("custIsDecease", aCustomer.isCustIsDecease());
		engine.put("custIsDormant", aCustomer.isCustIsDormant());
		engine.put("custIsDelinquent", aCustomer.isCustIsDelinquent());
		engine.put("custIsTradeFinCust", aCustomer.isCustIsTradeFinCust());
		engine.put("custIsStaff", aCustomer.isCustIsStaff());
		engine.put("custIndustry", aCustomer.getCustIndustry());
		engine.put("custSector", aCustomer.getCustSector());
		engine.put("custSubSector", aCustomer.getCustSubSector());
		engine.put("custProfession", aCustomer.getCustProfession());
		engine.put("custTotalIncome", aCustomer.getCustTotalIncome());
		engine.put("custMaritalSts", aCustomer.getCustMaritalSts());
		engine.put("custEmpSts", aCustomer.getCustEmpSts());
		engine.put("custSegment", aCustomer.getCustSegment());
		engine.put("custSubSegment", aCustomer.getCustSubSegment());
		engine.put("custIsBlackListed", aCustomer.isCustIsBlackListed());
		engine.put("custIsRejected", aCustomer.isCustIsRejected());
		engine.put("custParentCountry", aCustomer.getCustParentCountry());
		engine.put("custResdCountry", aCustomer.getCustResdCountry());
		engine.put("custRiskCountry", aCustomer.getCustRiskCountry());
		engine.put("custNationality", aCustomer.getCustNationality());
		engine.put("custParentCountry", aCustomer.getCustParentCountry());
		logger.debug("Leaving");
	}

	class CheckListComparator implements Comparator<CheckListDetail>, Serializable {
		private static final long serialVersionUID = 9112640872865877333L;

		@Override
		public int compare(CheckListDetail data1, CheckListDetail data2) {
			return String.valueOf(data1.getCheckListId()).compareTo(String.valueOf(data2.getCheckListId()));
		}
	}

	/**
	 * This method gets all answers for checkList and prepares the
	 * List<finCheckListReference>
	 */
	@SuppressWarnings("rawtypes")
	private void doWriteComponentsToBean(FinanceDetail aFinanceDetail) {
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
						selectedAnsCountMap.put(Long.valueOf(checkListDetail.getCheckListId()), Long.valueOf(answerCount + 1));
					}
					String key=checkListDetail.getCheckListId() + ";" + checkListDetail.getAnsSeqNo();
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
			String key=finChkListRef.getQuestionId() + ";" + finChkListRef.getAnswer();
			finChkListRef.setLovDescAnswerDesc(ansDescMap.get(key));
			String remarks = commentsTxtBoxMap.get(key) == null ? "" : commentsTxtBoxMap.get(key).getValue();
			finChkListRef.setRemarks(remarks);
			finChkListRef.setFinReference(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinReference());
			if (notAllowedToShowMap.containsKey(prevAnswersMap.get(questionId).getQuestionId())) {
				continue;
			}
			if (!presentAnswersMap.containsKey(questionId)) {
				if (!notInputInStageMap.containsKey(finChkListRef.getQuestionId())) {
					finChkListRef.setRecordType(PennantConstants.RCD_DEL);
					finChkListRef.setBefImage(prevAnswersMap.get(questionId));
					finCheckListRefList.add(finChkListRef);
					dataChanged = true;
				}
			} else {
				FinanceReferenceDetail finRefDetail = presentAnswersMap.get(questionId).getLovDescFinRefDetail();
				finChkListRef.setBefImage(prevAnswersMap.get(questionId));
				finChkListRef.setLovDescQuesDesc(finRefDetail.getLovDescRefDesc());
				if (!StringUtils.equals(finChkListRef.getRemarks().trim(), finChkListRef.getBefImage().getRemarks().trim())) {
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
				FinanceReferenceDetail finRefDetail = presentAnswersMap.get(questionId).getLovDescFinRefDetail();
				finChkListRef.setRecordType(PennantConstants.RCD_ADD);
				finChkListRef.setNewRecord(true);
				finChkListRef.setQuestionId(presentAnswersMap.get(questionId).getCheckListId());
				finChkListRef.setAnswer(presentAnswersMap.get(questionId).getAnsSeqNo());
				finChkListRef.setFinReference(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinReference());
				finChkListRef.setLovDescMaxAnsCount(finRefDetail.getLovDescCheckMaxCount());
				finChkListRef.setLovDescMinAnsCount(finRefDetail.getLovDescCheckMinCount());
				finChkListRef.setLovDescQuesDesc(finRefDetail.getLovDescRefDesc());
				finChkListRef.setLovDescAnswerDesc(presentAnswersMap.get(questionId).getAnsDesc());
				String key=finChkListRef.getQuestionId() + ";" + finChkListRef.getAnswer();
				String remarks = commentsTxtBoxMap.get(key) == null ? "" : commentsTxtBoxMap.get(key).getValue();
				finChkListRef.setRemarks(remarks);
				finChkListRef.setLovDescSelAnsCountMap(selectedAnsCountMap);
				finCheckListRefList.add(finChkListRef);
				dataChanged = true;
			}
		}
		aFinanceDetail.setFinanceCheckList(finCheckListRefList);
		setFinanceDetail(aFinanceDetail);
		logger.debug("Leaving ");
	}

	/**
	 * Method to validate checklist
	 * 
	 * @param auditDetail
	 *            (AuditDetail)
	 * @param usrLanguage
	 *            (String)
	 * @param method
	 *            (String)
	 * @return auditDetail
	 * @throws InterruptedException
	 */
	private boolean validation_CheckList(FinanceDetail financeDetail, String usrLanguage, String method) throws InterruptedException {
		logger.debug("Entering ");
		String[] errParm = new String[3];
		String[] valueParm = new String[1];
		if (financeDetail.getFinRefDetailsList() != null) {
			for (FinanceReferenceDetail aFinRefDetail : financeDetail.getFinRefDetailsList()) {
				if (financeDetail.getLovDescSelAnsCountMap().containsKey(Long.valueOf(aFinRefDetail.getFinRefId())) ) {
					if ((financeDetail.getLovDescSelAnsCountMap().get(Long.valueOf(aFinRefDetail.getFinRefId()))) > (aFinRefDetail.getLovDescCheckMaxCount()) || (financeDetail.getLovDescSelAnsCountMap().get(Long.valueOf(aFinRefDetail.getFinRefId()))) < (aFinRefDetail.getLovDescCheckMinCount())) {
						errParm[0] = "" + aFinRefDetail.getLovDescCheckMinCount();
						errParm[1] = "" + aFinRefDetail.getLovDescCheckMaxCount();
						errParm[2] = aFinRefDetail.getLovDescRefDesc();
						PTMessageUtils.showErrorMessage(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "F0001", errParm, valueParm), usrLanguage).getError());
						return false;
					}
				} else if (aFinRefDetail.getLovDescCheckMinCount() > 0){
					errParm[0] = "" + aFinRefDetail.getLovDescCheckMinCount();
					errParm[1] = "" + aFinRefDetail.getLovDescCheckMaxCount();
					errParm[2] = aFinRefDetail.getLovDescRefDesc();
					PTMessageUtils.showErrorMessage(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "F0001", errParm, valueParm), usrLanguage).getError());
					return false;
				}
			}
		}
		logger.debug("Leaving ");
		return true;
	}

	/**
	 * This method retrieves previously selected answers of checkList for this
	 * finance reference and keeps those in prevAnswersMap where key is question
	 * Id and value is answer like 'A' or 'B'
	 * 
	 * @param financeMain
	 * @param answersRadiogroup
	 */
	public Map<String, FinanceCheckListReference> doGetPreviowsAnswers(FinanceDetail aFinanceDetail) {
		logger.debug("Entering ");
		prevAnswersMap = new HashMap<String, FinanceCheckListReference>();
		List<FinanceCheckListReference> finCheckRefList = aFinanceDetail.getFinanceCheckList();
		if (finCheckRefList != null && !finCheckRefList.isEmpty()) {
			for (FinanceCheckListReference finCheckListRef : finCheckRefList) {
				prevAnswersMap.put(String.valueOf(finCheckListRef.getQuestionId() + ";" + finCheckListRef.getAnswer()), finCheckListRef);
			}
		}
		logger.debug("Leaving ");
		return prevAnswersMap;
	}

	/**
	 * This method validated check list for mandatory Remarks input
	 */
	public void doCheckListValidation(FinanceDetail financeDetail) {
		logger.debug("Entering ");
		doClearErrorMessages();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		/* Check whether answer is selected or not if not selected throw error */
		for (FinanceCheckListReference finChkListRef : financeDetail.getFinanceCheckList()) {
			try {
				String key=finChkListRef.getQuestionId() + ";" + finChkListRef.getAnswer();
				if (commentsTxtBoxMap.containsKey(key)) {
					if (!finChkListRef.getRecordType().equals(PennantConstants.RCD_DEL)) {
						if (commentsTxtBoxMap.get(key).getValue().trim().equals("")) {
							throw new WrongValueException(commentsTxtBoxMap.get(key), Labels.getLabel("label_ChecList_Must_EnterRemarks"));
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
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onChkListValidation(Event event) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String userAction="";
		if (event.getData() != null) {
			map = (Map<String, Object>) event.getData();
		}
		if (map.containsKey("financeMainDialogCtrl")) {
			this.financeMainQDEDialogCtrl = null;
			this.financeMainDialogCtrl = (Object) map.get("financeMainDialogCtrl");
		}
		if (map.containsKey("financeDetail")) {
			setFinanceDetail((FinanceDetail) map.get("financeDetail"));
		}
		if (map.containsKey("financeMainQDEDialogCtrl")) {
			this.financeMainDialogCtrl = null;
			this.financeMainQDEDialogCtrl = (FinanceMainQDEDialogCtrl) map.get("financeMainQDEDialogCtrl");
		}
		if (map.containsKey("userAction")) {
			userAction= (String) map.get("userAction");
		}
		
		
		doClearErrorMessages();
		if (getFinanceMainDialogCtrl() != null) {
			doWriteComponentsToBean(getFinanceDetail());
			doCheckListValidation(getFinanceDetail());
			boolean validationSuccess = true;
			if (!("Save".equalsIgnoreCase(userAction) || "Cancel".equalsIgnoreCase(userAction) || "Resubmit".equalsIgnoreCase(userAction))
					&& !map.containsKey("agreement")) {
				validationSuccess = validation_CheckList(financeDetail, getUserWorkspace().getUserLanguage(), getFinanceDetail().getUserAction());
			}
			if (!validationSuccess) {
				throw new Exception();
			}
			try {
				getFinanceMainDialogCtrl().getClass().getMethod("setFinanceDetail", FinanceDetail.class).invoke(getFinanceMainDialogCtrl(), getFinanceDetail());
			} catch (Exception e) {
				logger.error(e);
			}
		} else {
			doWriteComponentsToBean(this.financeMainQDEDialogCtrl.getFinanceDetail());
			doCheckListValidation(this.financeMainQDEDialogCtrl.getFinanceDetail());
			this.financeMainQDEDialogCtrl.setFinanceDetail(getFinanceDetail());
		}
	}

	/**
	 * Event for checking whethter data has been changed before closing
	 * 
	 * @param event
	 * @return
	 * */
	public void onCheckListClose(Event event) {
		logger.debug("Entering" + event.toString());
		doClearErrorMessages();
		if (getFinanceMainDialogCtrl() != null) {
			try {
				getFinanceMainDialogCtrl().getClass().getMethod("setAssetDataChanged", Boolean.class).invoke(getFinanceMainDialogCtrl(), isDataChanged(getFinanceDetail()));
			} catch (Exception e) {
				logger.error(e);
			}
		}
		if (this.financeMainQDEDialogCtrl != null) {
			this.financeMainQDEDialogCtrl.setCheckListDataChanged(isDataChanged(getFinanceDetail()));
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * This method reads all components data
	 */
	public boolean isDataChanged(FinanceDetail aFinanceDetail) {
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
			// groupBox.set
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			if (panel != null) {
				((Tab) panel.getParent().getParent().getParent().getFellowIfAny("checkListTab")).setSelected(true);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	/**
	 * This event stores the text box value into bean
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onBlurRemarksTextBox(ForwardEvent event) throws Exception {
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
	 * @throws Exception
	 */
	public void onSelectListItem(ForwardEvent event) throws Exception {
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
			final CheckListDetail aCheckListDetail = (CheckListDetail) listitem.getAttribute("data");
			aCheckListDetail.setLovDescPrevAnsMap(temp_PrevAnswersMap);
		}
		logger.debug("Leaving " + event.toString());
	}

	public void onUploadRequiredDocument(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		CheckListDetail checkListDetail = (CheckListDetail) event.getData();
		if (getFinanceMainDialogCtrl() != null) {
			try {
				if (getFinanceMainDialogCtrl().getClass().getMethod("getDocumentDetailDialogCtrl") != null) {
					DocumentDetailDialogCtrl docDialogCtrl = (DocumentDetailDialogCtrl) getFinanceMainDialogCtrl().getClass().getMethod("getDocumentDetailDialogCtrl").invoke(getFinanceMainDialogCtrl());
					if (docDialogCtrl != null) {
						Map<String, DocumentDetails> docDetailMap = docDialogCtrl.getDocDetailMap();
						if (docDetailMap != null && docDetailMap.containsKey(checkListDetail.getDocType())) {
							docDialogCtrl.updateExistingDocument(docDetailMap.get(checkListDetail.getDocType()), true, false);
						} else {
							docDialogCtrl.createNewDocument(checkListDetail, true, updateFinanceMain());
						}
					}
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	public void onViewRequiredDocument(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		CheckListDetail checkListDetail = (CheckListDetail) event.getData();
		String docType = checkListDetail.getDocType();
		if (getFinanceMainDialogCtrl() != null) {
			try {
				if (getFinanceMainDialogCtrl().getClass().getMethod("getDocumentDetailDialogCtrl") != null) {
					DocumentDetailDialogCtrl docDialogCtrl = (DocumentDetailDialogCtrl) getFinanceMainDialogCtrl().getClass().getMethod("getDocumentDetailDialogCtrl").invoke(getFinanceMainDialogCtrl());
					if (docDialogCtrl != null) {
						Map<String, DocumentDetails> docDetailMap = docDialogCtrl.getDocDetailMap();
						if (docDetailMap != null && docDetailMap.containsKey(docType)) {
							DocumentDetails finDocumentDetail = docDetailMap.get(docType);
							finDocumentDetail.setDocIsCustDoc(checkListDetail.isDocIsCustDOC());
							docDialogCtrl.updateExistingDocument(finDocumentDetail, true, true);
						} else {
							PTMessageUtils.showErrorMessage("Document not Yet uploaded.");
						}
					}
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Update Finance Main Details from the Finance Main Ctrl 
	 * @return
	 */
	private FinanceMain updateFinanceMain(){
		FinanceMain main = null;
		try {
			Object object = getFinanceMainDialogCtrl().getClass().getMethod("getFinanceMain").invoke(getFinanceMainDialogCtrl());
			if (object != null) {
				main = (FinanceMain) object;
				return main;
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}

	public RuleExecutionUtil getRuleExecutionUtil() {
		return ruleExecutionUtil;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(FinanceMainDialogCtrl financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public Map<String, List<Listitem>> getCheckListDocTypeMap() {
		return checkListDocTypeMap;
	}

	public void setCheckListDocTypeMap(Map<String, List<Listitem>> checkListDocTypeMap) {
		this.checkListDocTypeMap = checkListDocTypeMap;
	}
}
