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
 * FileName    		:  FinanceCheckListReferenceDialogCtrl.java                             * 	  
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
 * 28-05-2018       Sai Krishna              0.2          bugs #387 Don't mandate the       * 
 *                                                        checklist when allows input at a  * 
 *                                                        particular stage.                 * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.webui.lmtmasters.financechecklistreference;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.DeviationConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.webui.collateral.collateralsetup.CollateralBasicDetailsCtrl;
import com.pennant.webui.delegationdeviation.DeviationExecutionCtrl;
import com.pennant.webui.finance.financemain.DocumentDetailDialogCtrl;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.CollectionUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/LMTMasters/FinanceCheckListReference
 * /financeCheckListReferenceDialog.zul file.
 */
public class FinanceCheckListReferenceDialogCtrl extends GFCBaseCtrl<FinanceCheckListReference> {
	private static final long						serialVersionUID		= 4028305737293383251L;
	private static final Logger						logger					= Logger.getLogger(FinanceCheckListReferenceDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	public Window									window_FinanceCheckListReferenceDialog;
	protected Listbox								listBox_CheckList;
	private FinanceDetail							financeDetail;
	private Map<Long, Long>							selectedAnsCountMap;
	private Map<String, Textbox>					commentsTxtBoxMap		= new HashMap<String, Textbox>();
	private Map<String, String>						ansDescMap;
	private Map<String, FinanceCheckListReference>	prevAnswersMap;
	private Map<String, FinanceCheckListReference>	temp_PrevAnswersMap		= new HashMap<String, FinanceCheckListReference>();
	private Map<String, CheckListDetail>			presentAnswersMap;

	/*
	 * Here we remove the checkList Details form list which are not allowed to
	 * show at this stage
	 */
	private Map<Long, FinanceReferenceDetail>		notAllowedToShowMap;
	private Map<Long, FinanceReferenceDetail>		notInputInStageMap;
	private Object									financeMainDialogCtrl	= null;
	private Map<String, List<Listitem>>				checkListDocTypeMap		= null;
	private RuleExecutionUtil						ruleExecutionUtil;
	private CustomerService							customerService;
	private Tabpanel								panel					= null;
	private String									userRole				= "";
	private boolean									dataChanged				= false;

	private FinBasicDetailsCtrl						finBasicDetailsCtrl;
	private CollateralBasicDetailsCtrl  			collateralBasicDetailsCtrl;
	protected Groupbox								finBasicdetails;

	private HashMap<String, Boolean>				screenLevelModification	= new HashMap<String, Boolean>();
	private HashMap<String, String>					screenLevelRemarks		= new HashMap<String, String>();
	private HashMap<Long, String>					deviationCombovalues	= new HashMap<Long, String>();
	private HashMap<Long, Integer>					deviationInboxValues	= new HashMap<Long, Integer>();
	private DeviationExecutionCtrl					deviationExecutionCtrl;

	private boolean											isNotFinanceProcess		= false;
	private String											moduleDefiner			= "";
	private String											moduleName;
	
	// Temporary purpose on Cleanup Finance Detail Object
	private List<FinanceReferenceDetail> checkList = null;
	private List<FinanceCheckListReference> moduleCheckList = null;
	private List<FinanceReferenceDetail> refDetailsList = null;

	/**
	 * default constructor.<br>
	 */
	public FinanceCheckListReferenceDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected
	 * FinanceCheckListReference object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_FinanceCheckListReferenceDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinanceCheckListReferenceDialog);

		if (event.getTarget().getParent() != null) {
			panel = (Tabpanel) event.getTarget().getParent();
		}
		if (arguments.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
			FinanceDetail befImage = new FinanceDetail();
			BeanUtils.copyProperties(this.financeDetail, befImage);
			this.financeDetail.setBefImage(befImage);
			setFinanceDetail(this.financeDetail);
		} else {
			setFinanceDetail(null);
		}
		if (arguments.containsKey("financeMainDialogCtrl")) {
			this.financeMainDialogCtrl = (Object) arguments.get("financeMainDialogCtrl");
		}
		if (arguments.containsKey("roleCode")) {
			userRole = (String) arguments.get("roleCode");
		}
		if (arguments.containsKey("isNotFinanceProcess")) {
			isNotFinanceProcess = (boolean) arguments.get("isNotFinanceProcess");
		}
		if (arguments.containsKey("moduleDefiner")) {
			moduleDefiner = (String) arguments.get("moduleDefiner");
		}
		if (arguments.containsKey("checkList")) {
			checkList = (List<FinanceReferenceDetail>) arguments.get("checkList");
		}
		if (arguments.containsKey("finCheckRefList")) {
			moduleCheckList = (List<FinanceCheckListReference>) arguments.get("finCheckRefList");
		}
		if (!isNotFinanceProcess && StringUtils.equals("", moduleDefiner)) {
			setDeviationExecutionCtrl();
		}
		
		if (arguments.containsKey("moduleName")) {
			this.moduleName = (String) arguments.get("moduleName");
		}
		
		// append finance basic details 
		if (arguments.containsKey("finHeaderList")) {
			appendFinBasicDetails((ArrayList<Object> )arguments.get("finHeaderList"));
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

		try {
			
			if(!isNotFinanceProcess){
				checkList = getFinanceDetail().getCheckList();
				moduleCheckList = getFinanceDetail().getFinanceCheckList();
			}
			
			doWriteBeanToComponents(checkList, moduleCheckList, true);
			if (!isNotFinanceProcess && StringUtils.equals("", moduleDefiner)) {
				loadDeviationDetails(checkList);
			}
			if (panel != null) {
				getFinanceDialogCtrl().getClass().getMethod("setFinanceCheckListReferenceDialogCtrl", this.getClass()).invoke(getFinanceDialogCtrl(), this);
				getBorderLayoutHeight();
				this.listBox_CheckList.setHeight(getListBoxHeight(7));
				this.window_FinanceCheckListReferenceDialog.setHeight(this.borderLayoutHeight - 70 + "px");
				panel.appendChild(this.window_FinanceCheckListReferenceDialog);
			} else {
				setDialog(DialogType.EMBEDDED);
			}
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_FinanceCheckListReferenceDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving ");
	}

	/**
	 * Method for writing data into Tab panel from existing list.
	 * @param checkList
	 * @param finCheckRefList
	 * @param isLoadProcess
	 */
	public void doWriteBeanToComponents(List<FinanceReferenceDetail> checkList,List<FinanceCheckListReference> finCheckRefList, boolean isLoadProcess) {
		logger.debug("Entering ");
		
		notAllowedToShowMap = new HashMap<Long, FinanceReferenceDetail>();
		notInputInStageMap = new HashMap<Long, FinanceReferenceDetail>();
		ansDescMap = new HashMap<String, String>();
		List<FinanceReferenceDetail> tempCheckList = new ArrayList<FinanceReferenceDetail>();

		// Prepare Data for Rule Executions
		if(!isNotFinanceProcess){
			try {
				Object object = getFinanceDialogCtrl().getClass().getMethod("prepareCustElgDetail", Boolean.class).invoke(getFinanceDialogCtrl(), isLoadProcess);
				if (object != null) {
					setFinanceDetail((FinanceDetail) object);
				}
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}
		}

		if(checkList != null && !checkList.isEmpty()){
			for (FinanceReferenceDetail finRefDetail : checkList) {

				String rule = finRefDetail.getLovDescElgRuleValue();

				if (!finRefDetail.getShowInStage().contains(userRole)) {
					notAllowedToShowMap.put(Long.valueOf(finRefDetail.getFinRefId()), finRefDetail);
				}
				
				HashMap<String, Object> fieldsAndValues = null;
				String finCcy = null;
				
				if(!isNotFinanceProcess){
					fieldsAndValues = getFinanceDetail().getCustomerEligibilityCheck().getDeclaredFieldValues();
					finCcy = getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy();
				}
				
				if (!finRefDetail.getAllowInputInStage().contains(userRole)) {
					if (StringUtils.isNotBlank(rule)) {
						boolean ruleResult = (boolean) ruleExecutionUtil.executeRule(rule, fieldsAndValues, finCcy, RuleReturnType.BOOLEAN);
						if (ruleResult) {
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
					if (StringUtils.isNotBlank(rule)) {
						boolean ruleResult = (boolean) ruleExecutionUtil.executeRule(rule, fieldsAndValues, finCcy, RuleReturnType.BOOLEAN);
						if (ruleResult) {
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
		}
		
		// Need to be look at this for other than Finance Module
		if(!isNotFinanceProcess){
			getFinanceDetail().setFinRefDetailsList(tempCheckList);
		}else{
			setRefDetailsList(tempCheckList);
		}
		
		prevAnswersMap = doGetPreviowsAnswers(finCheckRefList);
		temp_PrevAnswersMap.putAll(prevAnswersMap);
		List<CheckListDetail> checkListDetailsList = new ArrayList<CheckListDetail>();
		checkListDocTypeMap = new HashMap<String, List<Listitem>>();
		String remarks = "";
		for (FinanceReferenceDetail finRefDetail : checkList) {
			if (!notAllowedToShowMap.containsKey(Long.valueOf(finRefDetail.getFinRefId()))) {
				if (finRefDetail.getLovDesccheckListDetail() != null) {
					for (CheckListDetail checkListDetail : finRefDetail.getLovDesccheckListDetail()) {
						checkListDetail.setLovDescCheckListDesc(finRefDetail.getLovDescRefDesc());
						checkListDetail.setLovDescCheckMinCount(finRefDetail.getLovDescCheckMinCount());
						checkListDetail.setLovDescCheckMaxCount(finRefDetail.getLovDescCheckMaxCount());
						checkListDetail.setLovDescFinRefDetail(finRefDetail);
						checkListDetail.setLovDescUserRole(userRole);
						String key = checkListDetail.getCheckListId() + ";" + checkListDetail.getAnsSeqNo();
						remarks = prevAnswersMap.get(key) == null ? "" : prevAnswersMap.get(key).getRemarks();
						checkListDetail.setLovDescRemarks(remarks);
						checkListDetailsList.add(checkListDetail);
						ansDescMap.put(key, checkListDetail.getAnsDesc());
						if (isLoadProcess) {
							screenLevelRemarks.put(key, checkListDetail.getLovDescRemarks());
						}
					}
				}
			}
		}

		List<DocumentDetails> list = getUpdateDocumentDetails();
		if (list == null && !isNotFinanceProcess) {
			list = financeDetail.getDocumentDetailsList();
		}

		doFillListbox(checkListDetailsList, list);
		logger.debug("Leaving ");
	}

	/**
	 * @param detailsList
	 */
	public void doFillListbox(List<CheckListDetail> detailsList, List<DocumentDetails> documentDetails) {
		logger.debug(" Entering ");

		this.listBox_CheckList.getItems().clear();
		if (detailsList == null || detailsList.isEmpty()) {
			return;
		}

		SortedMap<Long, List<CheckListDetail>> sortedMap = new TreeMap<Long, List<CheckListDetail>>();

		// group using map and Check list id
		for (CheckListDetail chekDetails : detailsList) {
			long key = chekDetails.getCheckListId();
			if (sortedMap.containsKey(key)) {
				sortedMap.get(key).add(chekDetails);
			} else {
				List<CheckListDetail> sortedList = new ArrayList<CheckListDetail>();
				sortedList.add(chekDetails);
				sortedMap.put(key, sortedList);
			}
		}

		for (Long checkListId : sortedMap.keySet()) {

			List<CheckListDetail> listtorender = sortedMap.get(checkListId);
			// prepare Group
			CheckListDetail header = listtorender.get(0);
			// add list Group
			Listgroup listgroup = new Listgroup();

			StringBuilder builder = new StringBuilder(header.getLovDescCheckListDesc());
			builder.append("  ");// To add Space between sentences
			builder.append(Labels.getLabel("Required_CheckList", new String[] { String.valueOf(header.getLovDescCheckMinCount()), String.valueOf(header.getLovDescCheckMaxCount()) }));
			Listcell grplistCell = new Listcell();
			grplistCell.appendChild(new Label(builder.toString()));
			grplistCell.appendChild(new Space());
			// Deviation Combo box
			FinanceReferenceDetail refDet = header.getLovDescFinRefDetail();
			
			Combobox combobox = new Combobox();
			combobox.setWidth("100px");
			String comboVal = deviationCombovalues.get(checkListId);
			fillComboBox(combobox, StringUtils.trimToEmpty(comboVal), PennantStaticListUtil.getCheckListDeviationType(), getExcludedList(refDet));
			grplistCell.appendChild(combobox);
			grplistCell.appendChild(new Space());
			Intbox intbox = new Intbox();
			intbox.setValue(deviationInboxValues.get(checkListId));
			intbox.setWidth("50px");
			intbox.setVisible(false);
			grplistCell.appendChild(intbox);
			Object[] object = new Object[3];
			object[0] = combobox;
			object[1] = intbox;
			object[2] = checkListId;
			intbox.addForward("onChange", "", "onChangeDeviationIntbox", object);
			combobox.addForward("onChange", "", "onChangeDeviationCombo", object);
			listgroup.appendChild(grplistCell);
			
			if (header.getLovDescFinRefDetail().getAllowInputInStage().contains(header.getLovDescUserRole())) {
				intbox.setDisabled(false);
				combobox.setDisabled(false);
			} else {
				intbox.setDisabled(true);
				combobox.setDisabled(true);
			}

			if (refDet.isAllowDeviation()) {
				combobox.setVisible(true);
				processOnChangeDevCombobox(object);
			} else {
				combobox.setVisible(false);
			}
			listgroup.appendChild(grplistCell);
			listgroup.appendChild(new Listcell(""));

			listgroup.setAttribute("data", header);
			this.listBox_CheckList.appendChild(listgroup);

			for (CheckListDetail checkListDetail : listtorender) {
				// add list item

				Listitem item = new Listitem();
				if (checkListDetail.isDocRequired()) {
					if (documentDetails != null && !documentDetails.isEmpty()) {
						for (DocumentDetails documentDetail : documentDetails) {
							if (documentDetail.getDocCategory().equals(checkListDetail.getDocType())) {
								item.setSelected(true);
							}
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
					if (StringUtils.isNotEmpty(checkListDetail.getLovDescUserRole()) && 
							checkListDetail.getLovDescFinRefDetail().getAllowInputInStage().contains(checkListDetail.getLovDescUserRole())) {
						Button uploadBtn = new Button("Upload");
						// added for label disabled>>> bugfix
						uploadBtn.setAutodisable("Upload");
						String btnID = "btn_" + checkListDetail.getCheckListId() + "_" + checkListDetail.getAnsSeqNo();
						uploadBtn.setId(btnID);
						uploadBtn.setStyle("background-color:#16a085;color:#ffffff !important;font-size:10px;padding:0px 2px;");
						uploadBtn.setAutodisable(btnID);
						listCell.appendChild(uploadBtn);
						uploadBtn.addForward("onClick", "", "onUploadRequiredDocument", checkListDetail);
					}

					listCell.appendChild(new Space());
					Button viewBtn = new Button("View");
					String vbtnID = "viewbtn_" + checkListDetail.getCheckListId() + "_" + checkListDetail.getAnsSeqNo();
					viewBtn.setStyle("background-color:#16a085;color:#ffffff !important;font-size:10px;padding:0px 2px;");
					viewBtn.setId(vbtnID);
					viewBtn.setAutodisable(vbtnID);
					listCell.appendChild(viewBtn);
					viewBtn.addForward("onClick", "", "onViewRequiredDocument", checkListDetail);
				}
				String key = checkListDetail.getCheckListId() + ";" + checkListDetail.getAnsSeqNo();

				listCell.setParent(item);
				listCell = new Listcell();
				listCell.setId(key);
				Textbox txtBoxRemarks = new Textbox();
				txtBoxRemarks.setMaxlength(100);
				txtBoxRemarks.setWidth("400px");
				txtBoxRemarks.setVisible(false);
				txtBoxRemarks.setValue(StringUtils.trimToEmpty(screenLevelRemarks.get(key)));
				txtBoxRemarks.addForward("onChange", "", "onChangeRemarks", checkListDetail);
				if (checkListDetail.isRemarksAllow()) {
					txtBoxRemarks.setVisible(true);
				}
				if (temp_PrevAnswersMap.containsKey(listCell.getId())) {
					item.setSelected(true);
				}
				// to get the Previous status if any on tab navigations

				if (screenLevelModification.containsKey(key)) {
					item.setSelected(screenLevelModification.get(key));
				}

				if (!checkListDetail.getLovDescFinRefDetail().getAllowInputInStage().contains(checkListDetail.getLovDescUserRole())) {
					item.setDisabled(true);
					txtBoxRemarks.setReadonly(true);
				} else {
					txtBoxRemarks.setReadonly(false);
				}
				listCell.appendChild(txtBoxRemarks);
				listCell.setParent(item);
				item.setAttribute("data", checkListDetail);
				ComponentsCtrl.applyForward(item, "onClick=onSelectListItem");
				this.listBox_CheckList.appendChild(item);
			}

		}

		logger.debug(" Leaving ");
	}

	public List<String> getExcludedList(FinanceReferenceDetail refDet) {
		List<String> exculdedList = new ArrayList<String>();
		if (!refDet.isAllowExpire()) {
			exculdedList.add(DeviationConstants.CL_EXPIRED);
		}
		if (!refDet.isAllowPostpone()) {
			exculdedList.add(DeviationConstants.CL_POSTPONED);
		}
		if (!refDet.isAllowWaiver()) {
			exculdedList.add(DeviationConstants.CL_WAIVED);
		}
		return exculdedList;
	}

	/**
	 * This method gets all answers for checkList and prepares the
	 * List<finCheckListReference>
	 * @return 
	 */
	@SuppressWarnings("rawtypes")
	private List<Object> doWriteComponentsToBean() {
		logger.debug("Entering ");
		
		List<Object> returnList = new ArrayList<>();
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
					commentsTxtBoxMap.put(listCell.getId(), txtboxRemarks);
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
					String key = checkListDetail.getCheckListId() + ";" + checkListDetail.getAnsSeqNo();
					presentAnswersMap.put(key, checkListDetail);
				}
			}
		}
		
		// Adding Selected Map details to return List
		returnList.add(selectedAnsCountMap);
		
		// For unselected check list details
		if (prevAnswersMap != null) {
			for (String questionId : prevAnswersMap.keySet()) {
				FinanceCheckListReference finChkListRef = new FinanceCheckListReference();
				finChkListRef.setQuestionId(prevAnswersMap.get(questionId).getQuestionId());
				finChkListRef.setLovDescQuesDesc(prevAnswersMap.get(questionId).getLovDescQuesDesc());
				finChkListRef.setAnswer(prevAnswersMap.get(questionId).getAnswer());
				String key = finChkListRef.getQuestionId() + ";" + finChkListRef.getAnswer();
				finChkListRef.setLovDescAnswerDesc(ansDescMap.get(key));
				String remarks = commentsTxtBoxMap.get(key) == null ? "" : commentsTxtBoxMap.get(key).getValue();
				finChkListRef.setRemarks(remarks);
				if (getFinanceDetail() != null) {
					finChkListRef.setFinReference(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinReference());
				}
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
					if (!StringUtils.equals(StringUtils.trimToEmpty(finChkListRef.getRemarks()), StringUtils.trimToEmpty(finChkListRef.getBefImage().getRemarks()))) {
						finChkListRef.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						dataChanged = true;
					} else {
						finChkListRef.setRecordType("");
					}
					finCheckListRefList.add(finChkListRef);
				}
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
				if (getFinanceDetail() != null) {
					finChkListRef.setFinReference(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinReference());
				}
				finChkListRef.setLovDescMaxAnsCount(finRefDetail.getLovDescCheckMaxCount());
				finChkListRef.setLovDescMinAnsCount(finRefDetail.getLovDescCheckMinCount());
				finChkListRef.setLovDescQuesDesc(finRefDetail.getLovDescRefDesc());
				finChkListRef.setLovDescAnswerDesc(presentAnswersMap.get(questionId).getAnsDesc());
				String key = finChkListRef.getQuestionId() + ";" + finChkListRef.getAnswer();
				String remarks = commentsTxtBoxMap.get(key) == null ? "" : commentsTxtBoxMap.get(key).getValue();
				finChkListRef.setRemarks(remarks);
				finChkListRef.setLovDescSelAnsCountMap(selectedAnsCountMap);
				finCheckListRefList.add(finChkListRef);
				dataChanged = true;
			}
		}
		
		// Adding Selected Map details to return List
		returnList.add(finCheckListRefList);
		logger.debug("Leaving ");
		return returnList;
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
	private boolean validation_CheckList(FinanceDetail financeDetail, String usrLanguage) throws InterruptedException {
		logger.debug("Entering ");

		List<FinanceDeviations> chkDeviations = new ArrayList<FinanceDeviations>();
		List<FinanceReferenceDetail> refList = null;
		if (isNotFinanceProcess) {
			refList = getRefDetailsList();
		} else {
			refList = getFinanceDetail().getFinRefDetailsList();
		}
		if (refList != null) {
			for (FinanceReferenceDetail aFinRefDetail : refList) {

				boolean valid = true;
				long min = aFinRefDetail.getLovDescCheckMinCount();
				long max = aFinRefDetail.getLovDescCheckMaxCount();
				long finref = aFinRefDetail.getFinRefId();

				if (selectedAnsCountMap.containsKey(Long.valueOf(finref))) {
					long selCount = selectedAnsCountMap.get(Long.valueOf(finref));
					if ((selCount) > (max) || (selCount) < (min)) {
						valid = false;
					}
				} else if (min > 0) {
					valid = false;
				}

				if (deviationExecutionCtrl != null && !isNotFinanceProcess) {
					if (!valid) {
						valid = validateDeviation(aFinRefDetail, chkDeviations, financeDetail, valid);
					}
				}

				// bugs #387 Don't mandate the checklist when allows input at a particular stage.
				if (!valid && CollectionUtil.exists(aFinRefDetail.getMandInputInStage(), ",", userRole)) {
					String[] errParm = new String[3];
					String[] valueParm = new String[1];
					errParm[0] = Long.toString(min);
					errParm[1] = Long.toString(max);
					errParm[2] = aFinRefDetail.getLovDescRefDesc();
					if (panel != null) {
						((Tab) panel.getParent().getParent().getParent().getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_CHECKLIST))).setSelected(true);
					}
					MessageUtil.showError(ErrorUtil
							.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "30701", errParm, valueParm),
									usrLanguage)
							.getError());
					return false;
				}

			}
		}

		if (deviationExecutionCtrl != null && !isNotFinanceProcess) {
			deviationExecutionCtrl.fillDeviationListbox(chkDeviations, getUserRole(), DeviationConstants.TY_CHECKLIST);
		}

		logger.debug("Leaving ");
		return true;
	}

	public String getTabID(String id){
		return "TAB"+StringUtils.trimToEmpty(id);
	}
	
	private boolean validateDeviation(FinanceReferenceDetail aFinRefDetail, List<FinanceDeviations> chkDeviations, FinanceDetail financeDetail, boolean checkCount) {
		boolean valid = false;
		long finref = aFinRefDetail.getFinRefId();
//		long min = aFinRefDetail.getLovDescCheckMinCount();

		if (!checkCount) {
			//Check for Deviation and reset validation if deviation is allowed
			String comboVal = StringUtils.trimToEmpty(deviationCombovalues.get(finref));
			if (!"".equals(comboVal) && !comboVal.equals(PennantConstants.List_Select)) {
				int val = deviationInboxValues.get(finref);
				FinanceDeviations deviation = deviationExecutionCtrl.checkCheckListDeviations(finref, financeDetail, comboVal, val);

				if (deviation != null && !"".equals(deviation.getDelegationRole())) {
					if (deviationExecutionCtrl.isAlreadyExsists(deviation)) {
						valid = true;
					} else {
						chkDeviations.add(deviation);
						valid = true;
					}
				}
			}
		} 
//		else {
//			List<CheckListDetail> list = aFinRefDetail.getLovDesccheckListDetail();
//			int validCount = 0;
//			for (CheckListDetail checkListDetail : list) {
//				String docType = checkListDetail.getDocType();
//				if (checkListDetail.isDocRequired() && checkListDetail.isDocIsCustDOC()) {
//					List<CustomerDocument> doclist = financeDetail.getCustomerDetails().getCustomerDocumentsList();
//					for (CustomerDocument documentDetails : doclist) {
//						if (docType.equals(documentDetails.getCustDocType())) {
//							Date expDate = documentDetails.getCustDocExpDate();
//							if (expDate.compareTo(DateUtility.getAppDate()) <= 0) {
//								validCount--;
//							}
//						}
//					}
//
//				} else {
//					validCount++;
//				}
//			}
//
//			if (validCount < min) {
//				valid = false;
//			} else {
//				valid = true;
//			}
//			
//			if (!valid) {
//				//Check Deviation specified for expired
//				String comboVal = StringUtils.trimToEmpty(deviationCombovalues.get(finref));
//				if (!"".equals(comboVal) && comboVal.equals(PennantConstants.List_Select) ) {
//					int val = deviationInboxValues.get(finref);
//					FinanceDeviations deviation = getFinDelegationDeviationCtrl().checkCheckListDeviations(finref, financeDetail, comboVal, val);
//
//					if (deviation != null && !"".equals(deviation.getDelegationRole())) {
//						if (getFinDelegationDeviationCtrl().isAlreadyExsists(deviation)) {
//							valid = true;
//						} else {
//							chkDeviations.add(deviation);
//							valid = true;
//						}
//					}
//				}
//			}
			
//		}

		return valid;
	}

	/**
	 * This method retrieves previously selected answers of checkList for this
	 * finance reference and keeps those in prevAnswersMap where key is question
	 * Id and value is answer like 'A' or 'B'
	 * 
	 * @param financeMain
	 * @param answersRadiogroup
	 */
	private Map<String, FinanceCheckListReference> doGetPreviowsAnswers(List<FinanceCheckListReference> finCheckRefList) {
		logger.debug("Entering ");
		prevAnswersMap = new HashMap<String, FinanceCheckListReference>();
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
	private void doCheckListValidation(List<FinanceCheckListReference> list) {
		logger.debug("Entering ");
		doClearErrorMessages();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		/* Check whether answer is selected or not if not selected throw error */
		for (FinanceCheckListReference finChkListRef : list) {
			try {
				String key = finChkListRef.getQuestionId() + ";" + finChkListRef.getAnswer();
				if (commentsTxtBoxMap.containsKey(key)) {
					if (!StringUtils.trimToEmpty(finChkListRef.getRecordType()).equals(PennantConstants.RCD_DEL)) {
						if (("").equals(commentsTxtBoxMap.get(key).getValue().trim())) {
							/*
							 * Mandatory validation remarks based on the
							 * configuration
							 */
							if (isRemarksMandatory(key)) {
								throw new WrongValueException(commentsTxtBoxMap.get(key), Labels.getLabel("label_ChecList_Must_EnterRemarks"));
							}
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
	 * @param refere
	 * @return
	 */
	public boolean isRemarksMandatory(String refere) {
		logger.debug(" Entering ");

		Set<Listitem> items = this.listBox_CheckList.getSelectedItems();
		for (Listitem listitem : items) {
			Object data = listitem.getAttribute("data");
			if (data != null) {
				CheckListDetail checkListDetail = (CheckListDetail) data;
				String key = checkListDetail.getCheckListId() + ";" + checkListDetail.getAnsSeqNo();
				if (StringUtils.equals(refere, key)) {

					logger.debug(" Leaving ");
					return checkListDetail.isRemarksMand();
				}
			}
		}

		logger.debug(" Leaving ");
		return false;
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
		String userAction = "";
		if (event.getData() != null) {
			map = (Map<String, Object>) event.getData();
		}
		if (map.containsKey("financeMainDialogCtrl")) {
			this.financeMainDialogCtrl = (Object) map.get("financeMainDialogCtrl");
		}
		if (map.containsKey("financeDetail")) {
			setFinanceDetail((FinanceDetail) map.get("financeDetail"));
		}
		if (map.containsKey("userAction")) {
			userAction = (String) map.get("userAction");
		}

		doClearErrorMessages();
		if (getFinanceDialogCtrl() != null) {
			List<Object> returnedList = doWriteComponentsToBean();
			doCheckListValidation((List<FinanceCheckListReference>)returnedList.get(1));
			boolean validationSuccess = true;

			if (!("Save".equalsIgnoreCase(userAction) || "Cancel".equalsIgnoreCase(userAction) || userAction.contains("Reject") || 
					userAction.contains("Resubmit") || userAction.contains("Hold")) && !map.containsKey("agreement")) {
				validationSuccess = validation_CheckList(this.financeDetail, getUserWorkspace().getUserLanguage());
			}

			if (!validationSuccess) {
				throw new Exception();
			}
			
			
			// Set Details in Controller rather than Bean or set in this class and fetch from main bean
			if (isNotFinanceProcess) {
				try {
					if (StringUtils.equals(moduleName, CollateralConstants.MODULE_NAME)) {
						getFinanceDialogCtrl().getClass().getMethod("setCollateralChecklists", List.class).invoke(getFinanceDialogCtrl(), (List<FinanceCheckListReference>) returnedList.get(1));
					} else if (StringUtils.equals(moduleName, VASConsatnts.MODULE_NAME)) {
						getFinanceDialogCtrl().getClass().getMethod("setVasChecklists", List.class).invoke(getFinanceDialogCtrl(), (List<FinanceCheckListReference>) returnedList.get(1));
					}
					getFinanceDialogCtrl().getClass().getMethod("setSelectedAnsCountMap", HashMap.class).invoke(getFinanceDialogCtrl(), (HashMap<Long, Long>) returnedList.get(0));
				} catch (Exception e) {
					logger.error("Exception: ", e);
					throw e;
				}
			} else {
				getFinanceDetail().setLovDescSelAnsCountMap((HashMap<Long, Long>) returnedList.get(0));
				getFinanceDetail().setFinanceCheckList((List<FinanceCheckListReference>) returnedList.get(1));
				//Setting must be done for Beans
				try {
					getFinanceDialogCtrl().getClass().getMethod("setFinanceDetail", FinanceDetail.class).invoke(getFinanceDialogCtrl(), getFinanceDetail());
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
			}
		}
	}

	/**
	 * This method reads all components data
	 */
	public boolean isDataChanged(FinanceDetail aFinanceDetail) {
		logger.debug("Entering ");
		doWriteComponentsToBean();
		logger.debug("Leaving ");
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
			if (panel != null) {
				((Tab) panel.getParent().getParent().getParent().getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_CHECKLIST))).setSelected(true);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
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
			doMaintainScreenLevelModification(listitem);
		}
		logger.debug("Leaving " + event.toString());
	}

	public void onUploadRequiredDocument(ForwardEvent event) throws InterruptedException {
 		logger.debug("Entering " + event.toString());
		CheckListDetail checkListDetail = (CheckListDetail) event.getData();
		if (getFinanceDialogCtrl() != null) {
			try {
				if (getFinanceDialogCtrl().getClass().getMethod("getDocumentDetailDialogCtrl") != null) {
					DocumentDetailDialogCtrl docDialogCtrl = (DocumentDetailDialogCtrl) getFinanceDialogCtrl().getClass().getMethod("getDocumentDetailDialogCtrl").invoke(getFinanceDialogCtrl());
					if (docDialogCtrl != null) {
						Map<String, DocumentDetails> docDetailMap = docDialogCtrl.getDocDetailMap();
						if (docDetailMap != null && docDetailMap.containsKey(checkListDetail.getDocType())) {
							DocumentDetails detail = docDetailMap.get(checkListDetail.getDocType());
							if (StringUtils.equals(detail.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
								MessageUtil.showError(Labels.getLabel("label_DocumentDeleteStatus"));
							} else {
								docDialogCtrl.updateExistingDocument(docDetailMap.get(checkListDetail.getDocType()), checkListDetail.getCheckListId(), false);
							}
						} else {
							docDialogCtrl.createNewDocument(checkListDetail, true);
						}
					}
				}
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	public String getLPORef(long uniqueId, String docType) {
		return docType + "_" + uniqueId;
	}

	public void onChangeRemarks(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		CheckListDetail checkListDetail = (CheckListDetail) event.getData();
		String key = checkListDetail.getCheckListId() + ";" + checkListDetail.getAnsSeqNo();
		Component com = event.getOrigin().getTarget();
		if (com instanceof Textbox) {
			screenLevelRemarks.put(key, ((Textbox) com).getValue());
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onViewRequiredDocument(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		CheckListDetail checkListDetail = (CheckListDetail) event.getData();
		String docType = checkListDetail.getDocType();
		if (getFinanceDialogCtrl() != null) {
			try {
				if (getFinanceDialogCtrl().getClass().getMethod("getDocumentDetailDialogCtrl") != null) {
					DocumentDetailDialogCtrl docDialogCtrl = (DocumentDetailDialogCtrl) getFinanceDialogCtrl().getClass().getMethod("getDocumentDetailDialogCtrl").invoke(getFinanceDialogCtrl());
					if (docDialogCtrl != null) {
						Map<String, DocumentDetails> docDetailMap = docDialogCtrl.getDocDetailMap();
						if (docDetailMap != null && docDetailMap.containsKey(docType)) {
							DocumentDetails finDocumentDetail = docDetailMap.get(docType);

							if (StringUtils.equals(finDocumentDetail.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
								MessageUtil.showError(Labels.getLabel("label_DocumentDeleteStatus"));
							} else {
								if (DocumentCategories.CUSTOMER.getKey().equals(checkListDetail.getCategoryCode())) {
									finDocumentDetail.setCategoryCode(DocumentCategories.CUSTOMER.getKey());
								}
								docDialogCtrl.updateExistingDocument(finDocumentDetail, checkListDetail.getCheckListId(), true);
							}
						} else {
							MessageUtil.showError(Labels.getLabel("label_NoDocumentFound"));
						}
					}
				}
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void doMaintainScreenLevelModification(Listitem listitem) {

		final CheckListDetail aCheckListDetail = (CheckListDetail) listitem.getAttribute("data");
		String key = aCheckListDetail.getCheckListId() + ";" + aCheckListDetail.getAnsSeqNo();
		if (!listitem.isDisabled()) {
			screenLevelModification.put(key, listitem.isSelected());
		}
	}

	/**
	 * Update Finance Main Details from the Finance Main Ctrl
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<DocumentDetails> getUpdateDocumentDetails() {
		try {

			return (List<DocumentDetails>) getFinanceDialogCtrl().getClass().getMethod("getDocumentDetails").invoke(getFinanceDialogCtrl());

		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		return null;
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails(ArrayList<Object> finHeaderList) {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this );
			map.put("finHeaderList", finHeaderList );
			map.put("moduleName", moduleName);
			if(isNotFinanceProcess){
				Executions.createComponents("/WEB-INF/pages/Collateral/CollateralSetup/CollateralBasicDetails.zul",this.finBasicdetails, map);
			}else {
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul",this.finBasicdetails, map);
			}
		} catch (Exception e) {
			logger.debug(e);
		}
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		if(isNotFinanceProcess){
			getCollateralBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
		}else{
			getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
		}
	}

	public void onChangeDeviationCombo(ForwardEvent event) throws Exception {
		logger.debug("Entering ");
		Object[] objects = (Object[]) event.getData();
		processOnChangeDevCombobox(objects);
		logger.debug("Leaving ");
	}

	public void processOnChangeDevCombobox(Object[] objects) {
		Combobox combobox = (Combobox) objects[0];
		Intbox intbox = (Intbox) objects[1];
		Long key = (Long) objects[2];
		String val = combobox.getSelectedItem().getValue().toString();
		if (!val.equals(PennantConstants.List_Select)) {
			if (DeviationConstants.CL_WAIVED.equals(val)) {
				intbox.setVisible(false);
				intbox.setValue(0);
			} else {
				intbox.setVisible(true);
			}
		} else {
			intbox.setVisible(false);
			intbox.setValue(0);
		}

		deviationCombovalues.put(key, val);
		deviationInboxValues.put(key, intbox.intValue());
	}

	public void onChangeDeviationIntbox(ForwardEvent event) throws Exception {
		logger.debug("Entering ");
		Object[] objects = (Object[]) event.getData();
		//Combobox combobox = (Combobox) objects[0];
		Intbox intbox = (Intbox) objects[1];
		Long key = (Long) objects[2];
		deviationInboxValues.put(key, intbox.intValue());
		logger.debug("Leaving ");
	}

	private void loadDeviationDetails(List<FinanceReferenceDetail> checkListDetailsList) {
		logger.debug(" Entering ");
		if(deviationExecutionCtrl != null){
			for (FinanceReferenceDetail checkListDetail : checkListDetailsList) {
				List<FinanceDeviations> list = deviationExecutionCtrl.getFinanceDeviations();
				List<FinanceDeviations> approvedList = getFinanceDetail().getApprovedFinanceDeviations();
				StoreDevaitions(checkListDetail,list);
				StoreDevaitions(checkListDetail,approvedList);
			}
		}
		logger.debug(" Leaving ");

	}

	private void StoreDevaitions(FinanceReferenceDetail checkListDetail, List<FinanceDeviations> list) {
		if (list == null || list.isEmpty()) {
			return;
		}

		for (FinanceDeviations financeDeviations : list) {
			if (!financeDeviations.getModule().equals(DeviationConstants.TY_CHECKLIST)) {
				continue;
			}

			String devCode = financeDeviations.getDeviationCode();
			int index = devCode.indexOf("_");
			long devRef = Long.parseLong(devCode.substring(0, index));
			String devVal = devCode.substring(index);
			if (devRef == checkListDetail.getFinRefId()) {
				deviationCombovalues.put(devRef, devVal);
				if (DeviationConstants.CL_WAIVED.equals(devVal)) {
					deviationInboxValues.put(devRef, 0);
				} else {
					deviationInboxValues.put(devRef, Integer.parseInt(financeDeviations.getDeviationValue()));
				}
				break;
			}

		}
	}

	private String getUserRole() {
		try {
			return (String) getFinanceDialogCtrl().getClass().getMethod("getUserRole").invoke(getFinanceDialogCtrl());
		} catch (Exception e) {
			logger.debug(e);
		}
		return "";
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
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

	public Object getFinanceDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public Map<String, List<Listitem>> getCheckListDocTypeMap() {
		return checkListDocTypeMap;
	}

	public void setCheckListDocTypeMap(Map<String, List<Listitem>> checkListDocTypeMap) {
		this.checkListDocTypeMap = checkListDocTypeMap;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}


	public void setDeviationExecutionCtrl() throws Exception {
		try {
			if(getFinanceDialogCtrl().getClass().getMethod("getDeviationExecutionCtrl") != null){
				deviationExecutionCtrl = (DeviationExecutionCtrl) getFinanceDialogCtrl().getClass().getMethod(
						"getDeviationExecutionCtrl").invoke(getFinanceDialogCtrl());
			}
		} catch (NoSuchMethodException e) {
			logger.error(e);
		}
		
	}

	public CollateralBasicDetailsCtrl getCollateralBasicDetailsCtrl() {
		return collateralBasicDetailsCtrl;
	}
	public void setCollateralBasicDetailsCtrl(CollateralBasicDetailsCtrl collateralBasicDetailsCtrl) {
		this.collateralBasicDetailsCtrl = collateralBasicDetailsCtrl;
	}
	
	public List<FinanceReferenceDetail> getRefDetailsList() {
		return refDetailsList;
	}
	public void setRefDetailsList(List<FinanceReferenceDetail> refDetailsList) {
		this.refDetailsList = refDetailsList;
	}
}
