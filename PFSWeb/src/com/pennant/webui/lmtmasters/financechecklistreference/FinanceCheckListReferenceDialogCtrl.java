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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
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
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.finance.financemain.FinanceMainDialogCtrl;
import com.pennant.webui.finance.financemain.FinanceMainQDEDialogCtrl;
import com.pennant.webui.lmtmasters.financechecklistreference.model.FinanceCheckListReferenceListModelItemRenderer;
import com.pennant.webui.util.GFCBaseCtrl;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/LMTMasters/FinanceCheckListReference/financeCheckListReferenceDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FinanceCheckListReferenceDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 4028305737293383251L;
	private final static Logger logger = Logger.getLogger(FinanceCheckListReferenceDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_FinanceCheckListReferenceDialog; // autoWired

	// not auto wired variables

	protected Listbox                               listBox_CheckList;       // autoWired
	protected Tab                                   tabCheckList;

	private FinanceDetail                           financeDetail; // over handed per parameters
	private transient FinanceDetailService          financeDetailService;
	private Map<Long, Long>                         selectedAnsCountMap ;
	private Map<String, Textbox>                    commentsTxtBoxMap  =new HashMap<String, Textbox>();
	private Map<String, String>                     ansDescMap;
	private Map<String, FinanceCheckListReference>  prevAnswersMap;
	private Map<String, FinanceCheckListReference>  temp_PrevAnswersMap=new HashMap<String, FinanceCheckListReference>();
	private Map<String, CheckListDetail>            presentAnswersMap;
	/*Here we remove the checkList Details form list which are not  allowed to show at this stage */
	private Map<Long, FinanceReferenceDetail>   notAllowedToShowMap;  
	private Map<Long, FinanceReferenceDetail>   notInputInStageMap ; 
	private FinanceMainDialogCtrl       financeMainDialogCtrl = null;
	private FinanceMainQDEDialogCtrl    financeMainQDEDialogCtrl = null;
	private RuleExecutionUtil ruleExecutionUtil;
	private CustomerService customerService; 
	private Tabpanel panel = null;
	private String   userRole="";
	private boolean dataChanged=false;
	private int setWindowHeight = 0;
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
	 * ZUL-file is called with a parameter for a selected FinanceCheckListReference object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinanceCheckListReferenceDialog(Event event) throws Exception {
		logger.debug(event.toString());

		if(event.getTarget().getParent() != null){
			panel = (Tabpanel) event.getTarget().getParent();
		}

		final Map<String, Object> args = getCreationArgsMap(event);
		if (args.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) args.get("financeDetail");
			FinanceDetail befImage =new FinanceDetail();
			BeanUtils.copyProperties(this.financeDetail, befImage);
			this.financeDetail.setBefImage(befImage);
			setFinanceDetail(this.financeDetail);
		} else {
			setFinanceDetail(null);
		}
		
		if(args.containsKey("financeMainDialogCtrl")){
			this.financeMainDialogCtrl = (FinanceMainDialogCtrl) args.get("financeMainDialogCtrl");
		}
		
		if(args.containsKey("financeMainQDEDialogCtrl")){
			this.financeMainQDEDialogCtrl = (FinanceMainQDEDialogCtrl) args.get("financeMainQDEDialogCtrl");
		}
		
		if (args.containsKey("userRole")) {
			userRole=(String)args.get("userRole");
		}
		
		if (args.containsKey("height")) {
			setWindowHeight=(Integer)args.get("height");
		}
		
		doShowDialog();
		logger.debug("Leaving");
	}
	/**
	 * 
	 * @throws InterruptedException
	 */
	public void doShowDialog() throws InterruptedException {
		logger.debug("Entering ");

		doWriteBeanToComponents(getFinanceDetail());

		if(panel != null){
			this.listBox_CheckList.setHeight(setWindowHeight+"px");
			this.window_FinanceCheckListReferenceDialog.setHeight(setWindowHeight+"px");
			panel.appendChild(this.window_FinanceCheckListReferenceDialog);
		}else{
			setDialog(this.window_FinanceCheckListReferenceDialog);
		}
		logger.debug("Leaving ");
	}
	/**
	 * This method gets 
	 * @param aFinanceDetail
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void doWriteBeanToComponents(FinanceDetail aFinanceDetail){
		logger.debug("Entering ");
		List<FinanceReferenceDetail> checkList =aFinanceDetail.getCheckList();

		notAllowedToShowMap  =new HashMap<Long, FinanceReferenceDetail>();
		notInputInStageMap  =new HashMap<Long, FinanceReferenceDetail>();
		ansDescMap =  new HashMap<String, String>();
		List<FinanceReferenceDetail> tempCheckList=new ArrayList<FinanceReferenceDetail>();
		
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");;
		HashMap<String, Object> fieldsandvalues = new HashMap<String, Object>();
		
		if(aFinanceDetail.getCustomerScoringCheck() != null){
			fieldsandvalues = aFinanceDetail.getCustomerScoringCheck().getDeclaredFieldValues();
			
			ArrayList<String> keyset = new ArrayList<String>(fieldsandvalues.keySet());
			for (int i = 0; i < keyset.size(); i++) {
				Object var=fieldsandvalues.get(keyset.get(i));
				if (var instanceof String) {
					var=var.toString().trim();
				}
				engine.put(keyset.get(i),var );
			}
			
		}else{
			if(aFinanceDetail.getCustomerEligibilityCheck() == null){
				long custId = 0;
				if(!StringUtils.trimToEmpty(aFinanceDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF()).equals("")){
					custId = aFinanceDetail.getFinScheduleData().getFinanceMain().getCustID();
					
					//Data Preparation for Rule Execution
					doSetEngineData(engine, custId);
				}
			}else{
				ArrayList<String> keyset = new ArrayList<String>(
						aFinanceDetail.getCustomerEligibilityCheck().getDeclaredFieldValues().keySet());
				for (int i = 0; i < keyset.size(); i++) {
					Object var=fieldsandvalues.get(keyset.get(i));
					if (var instanceof String) {
						var=var.toString().trim();
					}
					engine.put(keyset.get(i),var );
				}
			}
		}

		for(int i=0;i<checkList.size();i++){
			
			if(!checkList.get(i).getShowInStage().contains(userRole)){
				notAllowedToShowMap.put(Long.valueOf(checkList.get(i).getFinRefId()), checkList.get(i));
			}
			if(!checkList.get(i).getAllowInputInStage().contains(userRole)){
				notInputInStageMap.put(Long.valueOf(checkList.get(i).getFinRefId()), checkList.get(i));		
			}else{
				
				if(!StringUtils.trimToEmpty(checkList.get(i).getLovDescElgRuleValue()).equals("")){
					Object result = getRuleExecutionUtil().processEngineRule(checkList.get(i).getLovDescElgRuleValue(),
							engine, SystemParameterDetails.getGlobaVariableList(), "");
					
					if(result != null && Integer.parseInt(result.toString()) == 1){
						tempCheckList.add(checkList.get(i));
					}else{
						if(!notAllowedToShowMap.containsKey(Long.valueOf(checkList.get(i).getFinRefId()))){
							notAllowedToShowMap.put(Long.valueOf(checkList.get(i).getFinRefId()), checkList.get(i));
						}
					}
				}else{
					tempCheckList.add(checkList.get(i));
				}
			}
		}
		
		aFinanceDetail.setFinRefDetailsList(tempCheckList);
		prevAnswersMap=doGetPreviowsAnswers(aFinanceDetail);
		temp_PrevAnswersMap.putAll(prevAnswersMap);
		List<CheckListDetail> checkListDetailsList=new ArrayList<CheckListDetail>();

		String remarks="";
		for(FinanceReferenceDetail finRefDetail:checkList){
			if(!notAllowedToShowMap.containsKey(Long.valueOf(finRefDetail.getFinRefId()))){
				for(CheckListDetail checkListDetail:finRefDetail.getLovDesccheckListDetail()){
					checkListDetail.setLovDescCheckListDesc(finRefDetail.getLovDescRefDesc());
					checkListDetail.setLovDescFinRefDetail(finRefDetail);
					checkListDetail.setLovDescUserRole(userRole);

					remarks=  prevAnswersMap.get(String.valueOf(checkListDetail.getCheckListId()
							+";"+checkListDetail.getAnsSeqNo()))== null? ""
									: prevAnswersMap.get(String.valueOf(checkListDetail.getCheckListId()
											+";"+checkListDetail.getAnsSeqNo())).getRemarks();

					checkListDetail.setLovDescRemarks(remarks);
					checkListDetail.setLovDescPrevAnsMap(temp_PrevAnswersMap);
					checkListDetailsList.add(checkListDetail);
					ansDescMap.put(String.valueOf(checkListDetail.getCheckListId()
							+";"+checkListDetail.getAnsSeqNo()),checkListDetail.getAnsDesc());
				}
			}
		}
		this.listBox_CheckList.setItemRenderer(new FinanceCheckListReferenceListModelItemRenderer());
		this.listBox_CheckList.setModel(new GroupsModelArray(
				checkListDetailsList.toArray(),new CheckListComparator()));
		logger.debug("Leaving ");		
	}

	
	private void doSetEngineData(ScriptEngine engine, long custId){
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
	/**
	 * 
	 * @author S059
	 *
	 */
	class CheckListComparator implements Comparator<CheckListDetail>,Serializable{
		private static final long serialVersionUID = 9112640872865877333L;

		@Override
		public int compare(CheckListDetail data1, CheckListDetail data2) { 
			return String.valueOf(data1.getCheckListId()).compareTo(String.valueOf(data2.getCheckListId())); 
		}
	}


	/**
	 * This method gets all answers for checkList and prepares the List<finCheckListReference>
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void  doWriteComponentsToBean(FinanceDetail aFinanceDetail){

		logger.debug("Entering ");
		List<FinanceCheckListReference> finCheckListRefList=new ArrayList<FinanceCheckListReference>();
		List<CheckListDetail> checkListDetailsList=new ArrayList<CheckListDetail>();
		presentAnswersMap  =new HashMap<String , CheckListDetail>();
		selectedAnsCountMap =new HashMap<Long, Long>();
		Set SeletedSet= new HashSet();                    //To get Selected Items
		SeletedSet=this.listBox_CheckList.getSelectedItems();
		List list=new ArrayList(SeletedSet);

		for(int i=0;i<list.size();i++){
			if(list.get(i) instanceof Listitem  && !( list.get(i) instanceof Listgroup)){
				Listitem listitem=(Listitem)list.get(i);
				Listcell listCell=(Listcell)listitem.getChildren().get(1);
				Textbox txtboxRemarks=(Textbox) listCell.getChildren().get(0);
				final CheckListDetail aCheckListDetail = (CheckListDetail)listitem.getAttribute("data");
				aCheckListDetail.setLovDescRemarks(txtboxRemarks.getValue());
				if(aCheckListDetail.isRemarksMand()){
					commentsTxtBoxMap.put(listCell.getId(), txtboxRemarks);
				}
				checkListDetailsList.add(aCheckListDetail);
			}
		}
		//taking count of selected answers
		for( CheckListDetail checkListDetail:checkListDetailsList){
			if(!notAllowedToShowMap.containsKey(Long.valueOf(checkListDetail.getCheckListId()))){
				if(!notInputInStageMap.containsKey(Long.valueOf(checkListDetail.getCheckListId()))){

					if(!selectedAnsCountMap.containsKey(Long.valueOf(checkListDetail.getCheckListId()))){
						selectedAnsCountMap.put(Long.valueOf(checkListDetail.getCheckListId()),Long.valueOf(1));
					}else if(selectedAnsCountMap.containsKey(Long.valueOf(checkListDetail.getCheckListId()))){
						long answerCount = selectedAnsCountMap.get(Long.valueOf(checkListDetail.getCheckListId()));
						selectedAnsCountMap.remove(Long.valueOf(checkListDetail.getCheckListId()));
						selectedAnsCountMap.put(Long.valueOf(checkListDetail.getCheckListId()),Long.valueOf(answerCount+1));
					}
					presentAnswersMap.put(String.valueOf(
							checkListDetail.getCheckListId()+";"+checkListDetail.getAnsSeqNo()), checkListDetail);	
				}	
			}	
		}

		aFinanceDetail.setLovDescSelAnsCountMap(selectedAnsCountMap);
		aFinanceDetail.getLovDescSelAnsCountMap().putAll(selectedAnsCountMap);
		//For unselected check list details
		for(String questionId:prevAnswersMap.keySet()){	

			FinanceCheckListReference finChkListRef=new FinanceCheckListReference();
			finChkListRef.setQuestionId(prevAnswersMap.get(questionId).getQuestionId());
			finChkListRef.setLovDescQuesDesc(prevAnswersMap.get(questionId).getLovDescQuesDesc());
			finChkListRef.setAnswer(prevAnswersMap.get(questionId).getAnswer());
			finChkListRef.setLovDescAnswerDesc(ansDescMap.get(finChkListRef.getQuestionId()+";"
					+finChkListRef.getAnswer()));
			String remarks=commentsTxtBoxMap.get(String.valueOf(finChkListRef.getQuestionId()+";"
					+finChkListRef.getAnswer()))==null?""
							:commentsTxtBoxMap.get(String.valueOf(finChkListRef.getQuestionId()+";"
									+finChkListRef.getAnswer())).getValue();
			finChkListRef.setRemarks(remarks);
			finChkListRef.setFinReference(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinReference());

			if(!presentAnswersMap.containsKey(questionId)){
				if(!notInputInStageMap.containsKey(finChkListRef.getQuestionId())){
					finChkListRef.setRecordType(PennantConstants.RCD_DEL);
					finChkListRef.setBefImage(prevAnswersMap.get(questionId));
					finCheckListRefList.add(finChkListRef);
					dataChanged=true;
				}

			}else{
				FinanceReferenceDetail finRefDetail=presentAnswersMap.get(questionId).getLovDescFinRefDetail();
				finChkListRef.setBefImage(prevAnswersMap.get(questionId));
				finChkListRef.setLovDescQuesDesc(finRefDetail.getLovDescRefDesc());

				if(!StringUtils.equals(finChkListRef.getRemarks().trim()
						,finChkListRef.getBefImage().getRemarks().trim())){
					finChkListRef.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					dataChanged=true;
				}else{
					finChkListRef.setRecordType("");
				}
				finCheckListRefList.add(finChkListRef);
			}		
		}
		//For Selected check list Details
		for(String questionId:presentAnswersMap.keySet()){	

			FinanceCheckListReference finChkListRef=new FinanceCheckListReference();
			if(!prevAnswersMap.containsKey(questionId)){
				FinanceReferenceDetail finRefDetail=presentAnswersMap.get(questionId).getLovDescFinRefDetail();
				finChkListRef.setRecordType(PennantConstants.RCD_ADD);
				finChkListRef.setNewRecord(true);
				finChkListRef.setQuestionId(presentAnswersMap.get(questionId).getCheckListId());
				finChkListRef.setAnswer(presentAnswersMap.get(questionId).getAnsSeqNo());
				finChkListRef.setFinReference(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinReference());
				finChkListRef.setLovDescMaxAnsCount(finRefDetail.getLovDescCheckMaxCount());
				finChkListRef.setLovDescMinAnsCount(finRefDetail.getLovDescCheckMinCount());
				finChkListRef.setLovDescQuesDesc(finRefDetail.getLovDescRefDesc());
				finChkListRef.setLovDescAnswerDesc(presentAnswersMap.get(questionId).getAnsDesc());
				String remarks=commentsTxtBoxMap.get(String.valueOf(finChkListRef.getQuestionId()+";"
						+finChkListRef.getAnswer()))==null?""
								:commentsTxtBoxMap.get(String.valueOf(finChkListRef.getQuestionId()+";"
										+finChkListRef.getAnswer())).getValue();
				finChkListRef.setRemarks(remarks);
				finChkListRef.setLovDescSelAnsCountMap(selectedAnsCountMap);
				finCheckListRefList.add(finChkListRef);
				dataChanged=true;
			}	
		}
		aFinanceDetail.setFinanceCheckList(finCheckListRefList);
		setFinanceDetail(aFinanceDetail);
		logger.debug("Leaving ");
	}

	/**
	 * This method retrieves previously selected answers of checkList for this finance reference 
	 * and keeps those in prevAnswersMap where key is question Id and value is answer like 'A' or 'B'
	 * @param financeMain
	 * @param answersRadiogroup
	 */
	public Map<String , FinanceCheckListReference> doGetPreviowsAnswers(FinanceDetail aFinanceDetail){
		logger.debug("Entering ");
		prevAnswersMap  =new HashMap<String , FinanceCheckListReference>();

		List<FinanceCheckListReference> finCheckRefList=aFinanceDetail.getFinanceCheckList();
		if(finCheckRefList.size()>0){
			for(FinanceCheckListReference finCheckListRef:finCheckRefList){
				prevAnswersMap.put(String.valueOf(finCheckListRef.getQuestionId()+";"+finCheckListRef.getAnswer())
						, finCheckListRef);
			}	
		}
		logger.debug("Leaving ");
		return prevAnswersMap;
	}

	/**
	 * This method validated check list for mandatory Remarks input 
	 */
	public void doCheckListValidation(FinanceDetail financeDetail){
		logger.debug("Entering ");
		doClearErrorMessages();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		/*Check whether answer is selected or not if not selected throw error*/

		for(FinanceCheckListReference finChkListRef:financeDetail.getFinanceCheckList()){
			try{
				if(commentsTxtBoxMap.containsKey(String.valueOf
						(finChkListRef.getQuestionId()+";"+finChkListRef.getAnswer()))){
					if(!finChkListRef.getRecordType().equals(PennantConstants.RCD_DEL)){
						if(commentsTxtBoxMap.get(String.valueOf
								(finChkListRef.getQuestionId()+";"+finChkListRef.getAnswer())).getValue().trim().equals("")){
							throw new WrongValueException(commentsTxtBoxMap
									.get(String.valueOf
											(finChkListRef.getQuestionId()+";"+finChkListRef.getAnswer()))
											,Labels.getLabel("label_ChecList_Must_EnterRemarks"));
						}
					}
				}
			}catch(WrongValueException we){
				wve.add(we);
			}
		}
		showErrorDetails(wve);
		logger.debug("Leaving ");
	}
	/**
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onChkListValidation(Event event){

		Map<String,Object> map = new HashMap<String,Object>();
		if(event.getData() != null){
			map = (Map<String, Object>) event.getData();
		}
		if(map.containsKey("financeMainDialogCtrl")){
			this.financeMainQDEDialogCtrl = null;
			this.financeMainDialogCtrl = (FinanceMainDialogCtrl) map.get("financeMainDialogCtrl");
		}
		if(map.containsKey("financeMainQDEDialogCtrl")){
			this.financeMainDialogCtrl = null;
			this.financeMainQDEDialogCtrl = (FinanceMainQDEDialogCtrl) map.get("financeMainQDEDialogCtrl");
		}
		doClearErrorMessages();
		
		if(this.financeMainDialogCtrl !=null){
			doWriteComponentsToBean(this.financeMainDialogCtrl.getFinanceDetail());
			doCheckListValidation(this.financeMainDialogCtrl.getFinanceDetail());
			this.financeMainDialogCtrl.setFinanceDetail(getFinanceDetail());
		}else {
			doWriteComponentsToBean(this.financeMainQDEDialogCtrl.getFinanceDetail());
			doCheckListValidation(this.financeMainQDEDialogCtrl.getFinanceDetail());
			this.financeMainQDEDialogCtrl.setFinanceDetail(getFinanceDetail());
		}
	}

	/**
	 * Event for checking whethter data has been changed before closing
	 * @param event
	 * @return 
	 * */
	public void onCheckListClose(Event event){
		logger.debug("Entering" + event.toString());
		doClearErrorMessages();
		if(this.financeMainDialogCtrl!=null){
			this.financeMainDialogCtrl.setAssetDataChanged(isDataChanged(getFinanceDetail()));
		}
		if(this.financeMainQDEDialogCtrl!=null){
			this.financeMainQDEDialogCtrl.setCheckListDataChanged(isDataChanged(getFinanceDetail()));
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * This method reads all components data
	 */
	public boolean isDataChanged(FinanceDetail aFinanceDetail){
		logger.debug("Entering ");
		doWriteComponentsToBean(aFinanceDetail);
		return dataChanged;

	}

	/**
	 * This method clears error messages on check list
	 */
	public void doClearErrorMessages(){
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
			//groupBox.set
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			if(panel != null){
				((Tab)panel.getParent().getParent().getFellowIfAny("checkListTab")).setSelected(true);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}
	/**
	 * This event stores the text box value into bean 
	 * @param event
	 * @throws Exception
	 */
	public void onBlurRemarksTextBox(ForwardEvent event) throws Exception {
		logger.debug("Entering ");
		//(Quick fix will be changed later)
		Textbox textbox=(Textbox)event.getOrigin().getTarget();
		@SuppressWarnings("rawtypes")
		List list= this.listBox_CheckList.getItems();
		for(int i=0;i<list.size();i++){
			if(list.get(i) instanceof Listitem  && !( list.get(i) instanceof Listgroup)){
				Listitem listitem=(Listitem)list.get(i);
				Listcell listCell=(Listcell)listitem.getChildren().get(1);
				if(StringUtils.equals(textbox.getParent().getId(), listCell.getId())){
					final CheckListDetail aCheckList = (CheckListDetail)listitem.getAttribute("data");
					aCheckList.setLovDescRemarks(textbox.getValue());
					break;
				}
			}
		}
		logger.debug("Leaving ");
	}
	/**
	 *   onSelect event for listitem
	 * @param event
	 * @throws Exception
	 */
	public void onSelectListItem(ForwardEvent event) throws Exception {
		logger.debug("Entering " + event.toString());
		Listitem listitem=(Listitem)event.getOrigin().getTarget();
		if(listitem instanceof Listitem  && !( listitem instanceof Listgroup)){
			Listcell listCell=(Listcell)listitem.getChildren().get(1);
			if(listitem.isSelected()){
				if(!temp_PrevAnswersMap.containsKey(listCell.getId())){
					temp_PrevAnswersMap.put(listCell.getId(), new FinanceCheckListReference());
				}
			}else{
				if(temp_PrevAnswersMap.containsKey(listCell.getId())){
					temp_PrevAnswersMap.remove(listCell.getId());
				}
			}
			final CheckListDetail aCheckListDetail = (CheckListDetail)listitem.getAttribute("data");
			aCheckListDetail.setLovDescPrevAnsMap(temp_PrevAnswersMap);
			logger.debug("Leaving " + event.toString());
		}
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}
	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

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
}
