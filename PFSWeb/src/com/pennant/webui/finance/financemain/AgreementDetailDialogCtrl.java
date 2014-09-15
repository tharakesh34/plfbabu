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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.aspose.words.SaveFormat;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.NumberToEnglishWords;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.amtmasters.Authorization;
import com.pennant.backend.model.applicationmaster.AgreementDefinition;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.finance.AgreementDetail;
import com.pennant.backend.model.finance.AgreementDetail.CheckListAnsDetails;
import com.pennant.backend.model.finance.AgreementDetail.CheckListDetails;
import com.pennant.backend.model.finance.AgreementDetail.CommidityLoanDetails;
import com.pennant.backend.model.finance.AgreementDetail.CustomerCreditReview;
import com.pennant.backend.model.finance.AgreementDetail.CustomerCreditReviewDetails;
import com.pennant.backend.model.finance.AgreementDetail.CustomerFinance;
import com.pennant.backend.model.finance.AgreementDetail.CustomerIncomeCategory;
import com.pennant.backend.model.finance.AgreementDetail.CustomerIncomeDetails;
import com.pennant.backend.model.finance.AgreementDetail.ExceptionList;
import com.pennant.backend.model.finance.AgreementDetail.GoodLoanDetails;
import com.pennant.backend.model.finance.AgreementDetail.GroupRecommendation;
import com.pennant.backend.model.finance.AgreementDetail.Recommendation;
import com.pennant.backend.model.finance.AgreementDetail.ScoringDetails;
import com.pennant.backend.model.finance.AgreementDetail.ScoringHeader;
import com.pennant.backend.model.finance.FinAgreementDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleReportData;
import com.pennant.backend.model.finance.FinanceScoreDetail;
import com.pennant.backend.model.finance.FinanceScoreHeader;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewSummary;
import com.pennant.backend.model.lmtmasters.CarLoanDetail;
import com.pennant.backend.model.lmtmasters.CommidityLoanDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.lmtmasters.GenGoodsLoanDetail;
import com.pennant.backend.model.lmtmasters.GoodsLoanDetail;
import com.pennant.backend.model.lmtmasters.MortgageLoanDetail;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.service.NotesService;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.CreditApplicationReviewService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.TemplateEngine;
import com.pennant.webui.finance.financemain.model.FinScheduleListItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance/financeMain/FinanceMainDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class AgreementDetailDialogCtrl extends GFCBaseListCtrl<FinAgreementDetail> implements Serializable {
	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(AgreementDetailDialogCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_AgreementDetailDialog; // autoWired
	// Finance Schedule Details Tab
	protected Label agr_finType; // autoWired
	protected Label agr_finCcy; // autoWired
	protected Label agr_scheduleMethod; // autoWired
	protected Label agr_profitDaysBasis; // autoWired
	protected Label agr_finReference; // autoWired
	protected Label agr_grcEndDate; // autoWired
	protected Label labe_agr_grcEndDate; // autoWired
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
	private FinanceDetail financeDetail = null;
	private PagedListService pagedListService;
	private CreditApplicationReviewService creditApplicationReviewService;
	private NotesService notesService;
	long custid = 0;
	String jointcustCif = "";
	private Object financeMainDialogCtrl = null;
	private List<ValueLabel> profitDaysBasisList = new ArrayList<ValueLabel>();
	private List<ValueLabel> schMethodList = new ArrayList<ValueLabel>();

	/**
	 * default constructor.<br>
	 */
	public AgreementDetailDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected financeMain object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_AgreementDetailDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
		// READ OVERHANDED parameters !
		if (args.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) args.get("financeDetail");
		}
		if (args.containsKey("financeMainDialogCtrl")) {
			setFinanceMainDialogCtrl(args.get("financeMainDialogCtrl"));
		}
		if (args.containsKey("profitDaysBasisList")) {
			profitDaysBasisList = (List<ValueLabel>) args.get("profitDaysBasisList");
		}
		if (args.containsKey("schMethodList")) {
			schMethodList = (List<ValueLabel>) args.get("schMethodList");
		}
		doShowDialog();
		logger.debug("Leaving " + event.toString());
	}

	@SuppressWarnings("rawtypes")
	private void doShowDialog() {
		logger.debug("Entering");
		FinanceMain aFinanceMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		custid = aFinanceMain.getCustID();
		this.agr_finType.setValue(StringUtils.trimToEmpty(aFinanceMain.getLovDescFinTypeName()));
		this.agr_finCcy.setValue(StringUtils.trimToEmpty(aFinanceMain.getLovDescFinCcyName()));
		this.agr_scheduleMethod.setValue(PennantAppUtil.getlabelDesc(aFinanceMain.getScheduleMethod(), schMethodList));
		this.agr_profitDaysBasis.setValue(PennantAppUtil.getlabelDesc(aFinanceMain.getProfitDaysBasis(), profitDaysBasisList));
		this.agr_finReference.setValue(StringUtils.trimToEmpty(aFinanceMain.getFinReference()));
		this.agr_grcEndDate.setValue(DateUtility.formatDate(aFinanceMain.getGrcPeriodEndDate(), PennantConstants.dateFormate));
		this.authorization1.setValue(aFinanceMain.getAuthorization1());
		if (!StringUtils.trimToEmpty(aFinanceMain.getLovDescAuthorization1Name()).equals("")) {
			this.lovDescAuthorization1Name.setValue(aFinanceMain.getLovDescAuthorization1Name());
		}
		this.authorization2.setValue(aFinanceMain.getAuthorization2());
		if (!StringUtils.trimToEmpty(aFinanceMain.getLovDescAuthorization2Name()).equals("")) {
			this.lovDescAuthorization2Name.setValue(aFinanceMain.getLovDescAuthorization2Name());
		}
		
		
		if(getFinanceDetail().getFinScheduleData().getFinanceType() == null || 
				!getFinanceDetail().getFinScheduleData().getFinanceType().isFInIsAlwGrace()) {
			this.agr_grcEndDate.setVisible(false);
			this.labe_agr_grcEndDate.setVisible(false);
		}
		
		List<FinanceReferenceDetail> agreementsList = financeDetail.getAggrementList();
		for (FinanceReferenceDetail financeReferenceDetail : agreementsList) {
			doFillAgreementsList(this.listBox_Agreements, financeReferenceDetail, getRole());
		}
		try {
			Class[] paramType = { this.getClass() };
			Object[] stringParameter = { this };
			getFinanceMainDialogCtrl().getClass().getMethod("setAgreementDetailDialogCtrl", paramType).invoke(getFinanceMainDialogCtrl(), stringParameter);
		} catch (Exception e) {
			logger.error(e);
		}
		
		// set Read only mode accordingly if the object is new or not.
		if (aFinanceMain.isNewRecord()) {
			doEdit();
		} else {
			if (isWorkFlowEnabled()) {
				doEdit();
			} 
		}
		getBorderLayoutHeight();
		this.listBox_Agreements.setHeight(this.borderLayoutHeight - 80 - 135 + "px");// 210px
		this.window_AgreementDetailDialog.setHeight(this.borderLayoutHeight - 80 + "px");
		logger.debug("Leaving");
	}

	public void onClick$btnSearchAuthorization1(Event event) {
		logger.debug("Entering " + event.toString());
		String authtypes[] = null;
		if (getFinanceDetail() != null && !StringUtils.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescAssetCodeName()).equals("")) {
			authtypes = new String[2];
			authtypes[0] = PennantConstants.AUTH_DEFAULT;
			authtypes[1] = getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescAssetCodeName();
		} else {
			authtypes = new String[1];
			authtypes[0] = PennantConstants.AUTH_DEFAULT;
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
				this.authorization1.setValue(details.getAuthorizedId());
				this.lovDescAuthorization1Name.setValue(details.getAuthName() + "-" + details.getAuthDesig());
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	public void onClick$btnSearchAuthorization2(Event event) {
		logger.debug("Entering " + event.toString());
		String authtypes[] = null;
		if (getFinanceDetail() != null && !StringUtils.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescAssetCodeName()).equals("")) {
			authtypes = new String[2];
			authtypes[0] = PennantConstants.AUTH_DEFAULT;
			authtypes[1] = getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescAssetCodeName();
		} else {
			authtypes = new String[1];
			authtypes[0] = PennantConstants.AUTH_DEFAULT;
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
				this.authorization2.setValue(details.getAuthorizedId());
				this.lovDescAuthorization2Name.setValue(details.getAuthName() + "-" + details.getAuthDesig());
			}
		}
		
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method to fill Agreements tab.
	 * 
	 * @param listbox
	 * @param financeReferenceDetail
	 * @param userRole
	 */
	private void doFillAgreementsList(Listbox listbox, FinanceReferenceDetail financeReferenceDetail, String userRole) {
		logger.debug("Entering ");
		Listitem item = new Listitem(); // To Create List item
		Listcell listCell;
		listCell = new Listcell();
		listCell.setLabel(financeReferenceDetail.getLovDescNamelov());
		listCell.setParent(item);
		listCell = new Listcell();
		Html ageementLink = new Html();
		ageementLink.setContent("<a href='' style = 'font-weight:bold'>" + financeReferenceDetail.getLovDescAggReportName() + "</a> ");
		listCell.appendChild(ageementLink);
		listCell.setParent(item);
		listbox.appendChild(item);
		ageementLink.addForward("onClick", window_AgreementDetailDialog, "onGenerateReportClicked", financeReferenceDetail);
		logger.debug("Leaving ");
	}

	/**
	 * Method for Generating Template replaced to Finance Details
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onGenerateReportClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		FinanceReferenceDetail data = (FinanceReferenceDetail) event.getData();
		FinanceDetail detail = null;
		
		try {
			Object object = getFinanceMainDialogCtrl().getClass().getMethod("getAgrFinanceDetails").invoke(financeMainDialogCtrl);
			if (object != null) {
				detail = (FinanceDetail) object;
			}
		} catch (Exception e) {
			if (e.getCause().getClass().equals(WrongValuesException.class)) {
				throw e;
			}
		}
		try {
			if (detail!=null && detail.getFinScheduleData() != null && detail.getFinScheduleData().getFinanceMain() != null) {
				FinanceMain main = detail.getFinScheduleData().getFinanceMain();
				custid = main.getCustID();
				
				TemplateEngine engine = new TemplateEngine(main.getLovDescAssetCodeName());
				String reportName = main.getFinReference() + "_" + data.getLovDescAggReportName();
				engine.setTemplate(data.getLovDescAggReportName());
				engine.loadTemplateWithFontSize(11);
				engine.mergeFields(getAggrementData(detail,data.getLovDescAggImage()));
				engine.showDocument(this.window_AgreementDetailDialog, reportName, SaveFormat.DOCX);
				engine.close();
				engine = null;
			}
		} catch (Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.getMessage());
		}
		logger.debug("Leaving" + event.toString());
	}
	
	
	public byte[] doGenerateAgreement(FinanceDetail detail, AgreementDefinition agreementDef) throws InterruptedException{
		try {
			byte[] docByte = null;
			if (detail!=null && detail.getFinScheduleData() != null && detail.getFinScheduleData().getFinanceMain() != null) {
				FinanceMain main = detail.getFinScheduleData().getFinanceMain();
				custid = main.getCustID();
				
				TemplateEngine engine = new TemplateEngine(main.getLovDescAssetCodeName());
				String reportName = detail.getFinScheduleData().getFinReference() + "_" + agreementDef.getAggName();
				engine.setTemplate(agreementDef.getAggReportName());
				engine.loadTemplateWithFontSize(11);
				engine.mergeFields(getAggrementData(detail,agreementDef.getAggImage()));
				docByte = engine.getDocumentInByteArray(reportName, SaveFormat.DOCX );
 				engine.close();
				engine = null;
			}
			return docByte;
		} catch (Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.getMessage());
			return null;
		}
	}

	public void doEdit(){
		  readOnlyComponent(true, this.lovDescAuthorization1Name);
		  readOnlyComponent(true, this.lovDescAuthorization2Name);
		
		  this.btnSearchAuthorization1.setDisabled(isReadOnly("FinanceMainDialog_Authorization1"));
		  this.btnSearchAuthorization2.setDisabled(isReadOnly("FinanceMainDialog_Authorization2"));
	}
	
	/**
	 * This method set the check list details to aFinanceDetail
	 * 
	 * @param aFinanceDetail
	 */
	public void doSave_Agreements(FinanceDetail aFinanceDetail) {
		logger.debug("Entering ");
		aFinanceDetail.getFinScheduleData().getFinanceMain().setAuthorization1(this.authorization1.longValue());
		aFinanceDetail.getFinScheduleData().getFinanceMain().setAuthorization2(this.authorization2.longValue());
		logger.debug("Leaving ");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public CreditApplicationReviewService getCreditApplicationReviewService() {
		return creditApplicationReviewService;
	}

	public void setCreditApplicationReviewService(CreditApplicationReviewService creditApplicationReviewService) {
		this.creditApplicationReviewService = creditApplicationReviewService;
	}

	public void setNotesService(NotesService notesService) {
		this.notesService = notesService;
	}

	public NotesService getNotesService() {
		return notesService;
	}

	// =============== Agreement Generation============
	/**
	 * To prepare Agreement Data
	 * @param detail
	 * @return
	 */
	private AgreementDetail getAggrementData(FinanceDetail detail,String aggModuleDetails) {
		logger.debug("Entering");
		
		
		// Create New Object For The Agreement Detail
		AgreementDetail agreement = new AgreementDetail();
		
		agreement.setUserId(String.valueOf(getUserWorkspace().getUserDetails().getUserId()));
		agreement.setUserName(getUserWorkspace().getUserDetails().getUsername());
		try {

			if (custid != 0) {
				Date appldate = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");
				String appDate = DateUtility.formatUtilDate(appldate, PennantConstants.dateFormate);

				// ------------------ Customer Data Setting
				if(aggModuleDetails.contains(PennantConstants.AGG_BASICDE)){
				AgreementDetail custAgreementData = getCustomerAggrementData(custid, null);
				try {
					
					if (custAgreementData != null) {
						BeanUtils.copyProperties(custAgreementData, agreement);
						if (!StringUtils.trimToEmpty(agreement.getCustDOB()).equals("")) {
							agreement.setCustDOB(DateUtility.formatUtilDate(DateUtility.getDBDate(agreement.getCustDOB()), PennantConstants.dateFormate));
							agreement.setCustAge(String.valueOf(DateUtility.getYearsBetween(DateUtility.getDate(agreement.getCustDOB(), PennantConstants.dateFormate), appldate)));
						}
						if (detail.getJountAccountDetailList()!=null && !detail.getJountAccountDetailList().isEmpty()) {
							JointAccountDetail jointAccountDetail = detail.getJountAccountDetailList().get(0);
							AgreementDetail jointCustomer = getCustomerAggrementData(0, jointAccountDetail.getCustCIF());
							if (jointCustomer !=null && !StringUtils.trimToEmpty(jointCustomer.getCustDOB()).equals("")) {
								agreement.setCustJointDOB(DateUtility.formatUtilDate(DateUtility.getDBDate(jointCustomer.getCustDOB()), PennantConstants.dateFormate));
								agreement.setCustJointAge(String.valueOf(DateUtility.getYearsBetween(DateUtility.getDate(jointCustomer.getCustDOB(), PennantConstants.dateFormate), appldate)));
							}
						}
						
						agreement.setCustName(custAgreementData.getCustName());
						agreement.setCustTotIncome(PennantApplicationUtil.amountFormate(custAgreementData.getCustTotalIncome(), custAgreementData.getLovDescCcyFormatter()));
						agreement.setCustTotExpense(PennantApplicationUtil.amountFormate(custAgreementData.getCustTotalExpense(), custAgreementData.getLovDescCcyFormatter()));
					}
					
				} catch (Exception e) {
					logger.debug(e);
				}}
				FinanceMain main = null;
				if (detail != null && detail.getFinScheduleData().getFinanceMain() != null) {
					main = detail.getFinScheduleData().getFinanceMain();
				}
				// Application Date
				agreement.setApplicationDate(appDate);
				// -----------------Customer Employment
				if(aggModuleDetails.contains(PennantConstants.AGG_EMPMNTD)){
					agreement = getCustEmpDetails(agreement);
				}
				
				// -----------------Customer Income Details
				if(aggModuleDetails.contains(PennantConstants.AGG_INCOMDE)){
					agreement = getCustomerIncomeDetails(agreement);
				}
				
				// -----------------Customer Finance Details
				if(aggModuleDetails.contains(PennantConstants.AGG_EXSTFIN)){
					agreement = getCustomerFinanceDetails(agreement);
				}
				
				// -------------------- Car loan Details
				if(aggModuleDetails.contains(PennantConstants.AGG_CARLOAN)){
					agreement = getCarLoanDetails(agreement, detail.getCarLoanDetail());
					
					if (detail.getGurantorsDetailList()!=null && !detail.getGurantorsDetailList().isEmpty()) {
						GuarantorDetail guarantorDetail = detail.getGurantorsDetailList().get(0);
						agreement.setGuarantorName(guarantorDetail.getGuarantorCIFName());
					}else{
						agreement.setGuarantorName(agreement.getCustName());
					}
					
				}
				
				// ---------------------- Mortgage Loan Detail
				if(aggModuleDetails.contains(PennantConstants.AGG_MORTGLD)){
					agreement = getMortgageLoanDetails(agreement, detail.getMortgageLoanDetail());
				}
				
				// -------------- Goods Loan Detail
				if(aggModuleDetails.contains(PennantConstants.AGG_GOODSLD)){
					agreement = getGoodsLoanDetail(agreement, detail.getGoodsLoanDetails());
				}
				
				// --------------General Goods Loan Detail
				if(aggModuleDetails.contains(PennantConstants.AGG_GENGOOD)){
					agreement = getGenGoodsLoanDetail(agreement, detail.getGenGoodsLoanDetails());
				}
				
				if (agreement.getGoodsLoanDetails()==null || agreement.getGoodsLoanDetails().isEmpty()) {
					agreement.setGoodsLoanDetails(new ArrayList<AgreementDetail.GoodLoanDetails>(1));
					agreement.getGoodsLoanDetails().add(agreement.new GoodLoanDetails());
				}
				
				
				// -------------- Commodity Loan Detail
				if(aggModuleDetails.contains(PennantConstants.AGG_COMMODT)){
					agreement = getCommodityLoanDetail(agreement, detail.getCommidityLoanDetails(), detail);
				}
				
				// -----------------Customer Credit Review Details
				if(aggModuleDetails.contains(PennantConstants.AGG_CRDTRVW)){
					if (main!=null) {
						agreement = getCustomerCreditReviewDetails(agreement,main.getLovDescCustCtgTypeName());

					}}

				// -----------------Scoring Detail
				if(aggModuleDetails.contains(PennantConstants.AGG_SCOREDE)){
					if (main!=null && main.getLovDescCustCtgTypeName().equals("C") || main.getLovDescCustCtgTypeName().equals("B")) {
						agreement = getScoringDetailList(agreement,detail);
					}
				}
				
				
				// ----------------Finance Details
				if(aggModuleDetails.contains(PennantConstants.AGG_FNBASIC)){
					if (detail != null) {
						agreement = getFinanceDetails(agreement,detail);

					}
				}
				if (detail != null && detail.getFinScheduleData().getFinODPenaltyRate() != null) {
					agreement.setODchargeamtPage(PennantAppUtil.amountFormate(detail.getFinScheduleData().getFinODPenaltyRate().getODChargeAmtOrPerc(), 2));
				}

				// -----------------Check List Details
				if(aggModuleDetails.contains(PennantConstants.AGG_CHKLSTD)){
					if (detail != null) {
						agreement = getCheckListDetails(agreement, detail);
					}
				}
				
				// -------------------Schedule Details
				if(aggModuleDetails.contains(PennantConstants.AGG_SCHEDLD)){
					agreement = getSheduleDetails(agreement, detail);
				}
				
				// -------------------Recommendations
				if(aggModuleDetails.contains(PennantConstants.AGG_RECOMMD)){
					if (main != null) {
						agreement = getRecommendations(agreement, main.getFinReference());
					}
				}
				
				// -------------------Exception List
				if(aggModuleDetails.contains(PennantConstants.AGG_EXCEPTN)){
					agreement = getExceptionList(agreement, detail);
				}
				
				agreement = setGroupRecommendations(agreement, main.getFinReference());
				logger.debug("Leaving");
				
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
		return agreement;
	}

	/**
	 * To Prepare Schedule details data
	 * @param agreement
	 * @param detail
	 * @return
	 */
	private AgreementDetail getSheduleDetails(AgreementDetail agreement, FinanceDetail detail) {
		logger.debug("Entering");
		try {
			if (detail != null && detail.getFinScheduleData() != null) {
				FinScheduleListItemRenderer itemRenderer = new FinScheduleListItemRenderer();
				// Find Out Fee charge Details on Schedule
				/*Map<Date, ArrayList<FeeRule>> feeChargesMap = null;
				List<FeeRule> feeRules = detail.getFinScheduleData().getFeeRules();
				if (feeRules != null && !feeRules.isEmpty()) {
					feeChargesMap = new HashMap<Date, ArrayList<FeeRule>>();
					for (FeeRule fee : detail.getFinScheduleData().getFeeRules()) {
						if (feeChargesMap.containsKey(fee.getSchDate())) {
							ArrayList<FeeRule> feeChargeList = feeChargesMap.get(fee.getSchDate());
							feeChargeList.add(fee);
							feeChargesMap.put(fee.getSchDate(), feeChargeList);
						} else {
							ArrayList<FeeRule> feeChargeList = new ArrayList<FeeRule>();
							feeChargeList.add(fee);
							feeChargesMap.put(fee.getSchDate(), feeChargeList);
						}
					}
				}*/
				List<FinanceScheduleReportData> subList = itemRenderer.getAgreementSchedule(detail.getFinScheduleData());
				agreement.setScheduleData(subList);
			}else{
				agreement.setScheduleData(new ArrayList<FinanceScheduleReportData>());
				agreement.getScheduleData().add(new FinanceScheduleReportData());
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
		return agreement;
	}

	private String formatdDate(Date date) {
		return DateUtility.formatUtilDate(date, PennantConstants.dateFormate);
	}

	private String formatdAmount(BigDecimal amount) {
		return (PennantApplicationUtil.amountFormate(amount, getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
	}

	/**
	 * To get Customer agreement data from the view based on cif or id
	 * @param custid
	 * @param custcif
	 * @return
	 */
	private AgreementDetail getCustomerAggrementData(long custid, String custcif) {
		logger.debug("Entering");
		// AgreementDetail Data
		JdbcSearchObject<AgreementDetail> jdbcSearchObject = new JdbcSearchObject<AgreementDetail>(AgreementDetail.class);
		jdbcSearchObject.addTabelName("CustomerAgreementDetail_View");
		if (!StringUtils.trimToEmpty(custcif).equals("")) {
			jdbcSearchObject.addFilterEqual("CustCIF", custcif);
		} else {
			jdbcSearchObject.addFilterEqual("CustID", custid);
		}
		List<AgreementDetail> agreementDetails = getPagedListService().getBySearchObject(jdbcSearchObject);
		if (agreementDetails != null && !agreementDetails.isEmpty()) {
			return agreementDetails.get(0);
		}
		return null;
	}

	/**
	 * To  get CustIncome And Expense
	 * @param custid
	 * @return
	 */
	private List<CustomerIncome> getCustIncomeAndExpense(long custid) {
		logger.debug("Entering");
		// Customer Income and Expense Data
		JdbcSearchObject<CustomerIncome> jdbcSearchObject = new JdbcSearchObject<CustomerIncome>(CustomerIncome.class);
		jdbcSearchObject.addTabelName("CustomerIncomes_AView");
		jdbcSearchObject.addFilterEqual("CustID", custid);
		return getPagedListService().getBySearchObject(jdbcSearchObject);
	}

	/**
	 * To  get Customer Employment  Details
	 * @param custid
	 * @return
	 */
	private List<CustomerEmploymentDetail> getCustomerEmpDetails(long custid) {
		logger.debug("Entering");
		// Customer Employment Data
		JdbcSearchObject<CustomerEmploymentDetail> jdbcSearchObject = new JdbcSearchObject<CustomerEmploymentDetail>(CustomerEmploymentDetail.class);
		jdbcSearchObject.addTabelName("CustomerEmpDetails_AView");
		jdbcSearchObject.addFilterEqual("CustID", custid);
		logger.debug("Leaving");
		return getPagedListService().getBySearchObject(jdbcSearchObject);
	}

	/**
	 * To  get Finance Details By Customer
	 * @param custId
	 * @return
	 */
	private List<FinanceSummary> getFinanceDetailsByCustomer(long custId) {
		logger.debug("Entering");
		// Customer FinanceDetails
		JdbcSearchObject<FinanceSummary> jdbcSearchObject = new JdbcSearchObject<FinanceSummary>(FinanceSummary.class);
		jdbcSearchObject.addTabelName("CustFinanceExposure_View");
		jdbcSearchObject.addFilterEqual("CustID", custId);
		logger.debug("Leaving");
		return getPagedListService().getBySearchObject(jdbcSearchObject);
	}

	/**
	 * To  get Scoring Detail List
	 * @param agreementDetail
	 * @return
	 */
	private AgreementDetail getScoringDetailList(AgreementDetail agreementDetail,FinanceDetail detail) {
		logger.debug("Entering");
		try {
			List<ScoringHeader> finScoringHeaderList = new ArrayList<AgreementDetail.ScoringHeader>();
			List<ScoringHeader> nonFinScoringHeaderList = new ArrayList<AgreementDetail.ScoringHeader>();
			List<FinanceScoreHeader> finScoreHeaderList = detail.getFinScoreHeaderList();
			if (finScoreHeaderList != null && !finScoreHeaderList.isEmpty()) {
				ScoringHeader header = null;
				long prvGrpId = 0;
				if (detail.getScoreDetailListMap().containsKey(detail.getFinScoreHeaderList().get(0).getHeaderId())) {
					List<FinanceScoreDetail> scoreDetailList = detail.getScoreDetailListMap().get(detail.getFinScoreHeaderList().get(0).getHeaderId());
					for (FinanceScoreDetail curScoreDetail : scoreDetailList) {
						// Adding List Group
						if ((prvGrpId == 0) || (prvGrpId != curScoreDetail.getSubGroupId())) {
							header = agreementDetail.new ScoringHeader();
							header.setScoringGroup(curScoreDetail.getSubGrpCodeDesc());
							header.setScoringDetails(new ArrayList<AgreementDetail.ScoringDetails>());
							if ("F".equals(curScoreDetail.getCategoryType())) {
								finScoringHeaderList.add(header);
							} else {
								nonFinScoringHeaderList.add(header);
							}
						}
						ScoringDetails details = agreementDetail.new ScoringDetails();
						details.setScoringMetric(curScoreDetail.getRuleCode());
						details.setScoringDesc(curScoreDetail.getRuleCodeDesc());
						details.setMetricMaxScore(String.valueOf(curScoreDetail.getMaxScore()));
						details.setCalcScore(String.valueOf(curScoreDetail.getExecScore()));
						header.getScoringDetails().add(details);
						prvGrpId = curScoreDetail.getSubGroupId();
					}
				}
			}
			agreementDetail.setFinScoringHeaderDetails(finScoringHeaderList);
			agreementDetail.setNonFinScoringHeaderDetails(nonFinScoringHeaderList);
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
		return agreementDetail;
	}

	/**
	 * To  get Customer Income and Expense Details
	 * @param agreement
	 * @return
	 */
	private AgreementDetail getCustomerIncomeDetails(AgreementDetail agreement) {
		logger.debug("Entering");
		try{
		List<CustomerIncome> customerIncomes = getCustIncomeAndExpense(custid);
		if (customerIncomes != null && !customerIncomes.isEmpty()) {
			agreement.setCustincomeCategories(new ArrayList<AgreementDetail.CustomerIncomeCategory>());
			Map<String, List<CustomerIncome>> incomeMap = new HashMap<String, List<CustomerIncome>>();
			Map<String, List<CustomerIncome>> expenseMap = new HashMap<String, List<CustomerIncome>>();
			for (CustomerIncome customerIncome : customerIncomes) {
				String category=StringUtils.trimToEmpty(customerIncome.getCategory());
				if (customerIncome.getIncomeExpense().equals(PennantConstants.INCOME)) {
					if (incomeMap.containsKey(category)) {
						incomeMap.get(category).add(customerIncome);
					} else {
						ArrayList<CustomerIncome> list = new ArrayList<CustomerIncome>();
						list.add(customerIncome);
						incomeMap.put(category, list);
					}
				} else {
					if (expenseMap.containsKey(category)) {
						expenseMap.get(category).add(customerIncome);
					} else {
						ArrayList<CustomerIncome> list = new ArrayList<CustomerIncome>();
						list.add(customerIncome);
						expenseMap.put(category, list);
					}
				}
			}
			if (incomeMap != null) {
				for (String category : incomeMap.keySet()) {
					List<CustomerIncome> list = incomeMap.get(category);
					if (list != null && !list.isEmpty()) {
						CustomerIncomeCategory incomeCategory = agreement.new CustomerIncomeCategory();
						incomeCategory.setIncomeCategory("Income" + "-" + list.get(0).getLovDescCategoryName());
						incomeCategory.setCustomerIncomeDetails(new ArrayList<AgreementDetail.CustomerIncomeDetails>());
						for (CustomerIncome customerIncome : list) {
							CustomerIncomeDetails customerIncomeDetails = agreement.new CustomerIncomeDetails();
							customerIncomeDetails.setIncomeType(customerIncome.getLovDescCustIncomeTypeName());
							customerIncomeDetails.setJointCust(customerIncome.isJointCust() ?  "Yes" : "");
							customerIncomeDetails.setIncome(PennantApplicationUtil.amountFormate(customerIncome.getCustIncome(), customerIncome.getLovDescCcyEditField()));
							incomeCategory.getCustomerIncomeDetails().add(customerIncomeDetails);
						}
						agreement.getCustincomeCategories().add(incomeCategory);
					}
				}
			}
			if (expenseMap != null) {
				for (String category : expenseMap.keySet()) {
					List<CustomerIncome> list = expenseMap.get(category);
					if (list != null && !list.isEmpty()) {
						CustomerIncomeCategory incomeCategory = agreement.new CustomerIncomeCategory();
						incomeCategory.setIncomeCategory("Expense" + "-" + list.get(0).getLovDescCategoryName());
						incomeCategory.setCustomerIncomeDetails(new ArrayList<AgreementDetail.CustomerIncomeDetails>());
						for (CustomerIncome customerIncome : list) {
							CustomerIncomeDetails customerIncomeDetails = agreement.new CustomerIncomeDetails();
							customerIncomeDetails.setIncomeType(customerIncome.getLovDescCustIncomeTypeName());
							customerIncomeDetails.setJointCust(customerIncome.isJointCust() ?  "Yes" : "");
							customerIncomeDetails.setIncome(PennantApplicationUtil.amountFormate(customerIncome.getCustIncome(), customerIncome.getLovDescCcyEditField()));
							incomeCategory.getCustomerIncomeDetails().add(customerIncomeDetails);
						}
						agreement.getCustincomeCategories().add(incomeCategory);
					}
				}
			}
		}
		
			if (agreement.getCustincomeCategories() == null || agreement.getCustincomeCategories().isEmpty()) {
				agreement.setCustincomeCategories(new ArrayList<AgreementDetail.CustomerIncomeCategory>());
				CustomerIncomeCategory incomeCategory = agreement.new CustomerIncomeCategory();
				incomeCategory.setCustomerIncomeDetails(new ArrayList<AgreementDetail.CustomerIncomeDetails>());
				incomeCategory.getCustomerIncomeDetails().add(agreement.new CustomerIncomeDetails());
				agreement.getCustincomeCategories().add(incomeCategory);
			}
		
		}catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
		return agreement;
	}

	/**
	 * To  get Customer Finance Details
	 * @param agreement
	 * @return
	 */
	private AgreementDetail getCustomerFinanceDetails(AgreementDetail agreement) {
		logger.debug("Entering");
		try {
			List<FinanceSummary> financeMains = getFinanceDetailsByCustomer(custid);
			if (financeMains != null && !financeMains.isEmpty()) {
				agreement.setCustomerFinances(new ArrayList<AgreementDetail.CustomerFinance>());
				BigDecimal tot = new BigDecimal(0);
				for (FinanceSummary financeMain : financeMains) {
					CustomerFinance customerFinance = agreement.new CustomerFinance();
					customerFinance.setDealDate(formatdDate(financeMain.getFinStartDate()));
					customerFinance.setDealType(financeMain.getFinType() + "-" + financeMain.getFinReference());
					customerFinance.setOriginalAmount(formatdAmount(financeMain.getTotalOriginal()));
					int installmentMnts = DateUtility.getMonthsBetween(financeMain.getFinStartDate(), financeMain.getMaturityDate(), true);
					customerFinance.setMonthlyInstalment(formatdAmount(financeMain.getTotalRepayAmt().divide(new BigDecimal(installmentMnts), RoundingMode.HALF_DOWN)));
					customerFinance.setOutstandingBalance(formatdAmount(financeMain.getTotalOutStanding()));
					tot = tot.add(financeMain.getTotalOutStanding());
					agreement.getCustomerFinances().add(customerFinance);
				}
				agreement.setTotCustFin(formatdAmount(tot));
			} else {
				agreement.setCustomerFinances(new ArrayList<AgreementDetail.CustomerFinance>());
				agreement.getCustomerFinances().add(agreement.new CustomerFinance());
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
		return agreement;
	}

	/**
	 * To get Customer  Employment Details
	 * @param agreement
	 * @return
	 */
	private AgreementDetail getCustEmpDetails(AgreementDetail agreement) {
		logger.debug("Entering");
		try {
			String employer[] = new String[] { "", "", "", "" };
			List<CustomerEmploymentDetail> customerEmploymentDetails = getCustomerEmpDetails(custid);
			if (customerEmploymentDetails != null && !customerEmploymentDetails.isEmpty()) {
				for (CustomerEmploymentDetail customerEmploymentDetail : customerEmploymentDetails) {
					if (customerEmploymentDetail.isCurrentEmployer()) {
						employer[0] = customerEmploymentDetail.getLovDesccustEmpName();
						employer[1] = String.valueOf(DateUtility.getYearsBetween(customerEmploymentDetail.getCustEmpFrom(), DateUtility.getSystemDate()));
					} else {
						employer[2] = customerEmploymentDetail.getLovDesccustEmpName();
						employer[3] = String.valueOf(DateUtility.getYearsBetween(customerEmploymentDetail.getCustEmpFrom(), customerEmploymentDetail.getCustEmpTo()));
					}
				}
			}
			if (employer != null && employer.length == 4) {
				agreement.setCustCompanyName(employer[0]);
				agreement.setCustYearsExp(employer[1]);
				agreement.setCustPrevCompanyName(employer[2]);
				agreement.setCustYearsService(employer[3]);
			}
			logger.debug("Leaving");
		} catch (Exception e) {
			logger.debug(e);
		}
		return agreement;
	}


	/**
	 * To get Car Loan Details
	 * @param agreement
	 * @param carLoanDetail
	 * @return
	 */
	private AgreementDetail getCarLoanDetails(AgreementDetail agreement, CarLoanDetail carLoanDetail) {
		logger.debug("Entering");
		try {
			if (carLoanDetail != null) {
				agreement.setVehicleType(carLoanDetail.getLovDescManufacturerName());
				agreement.setModelYear(String.valueOf(carLoanDetail.getCarMakeYear()));
				agreement.setModel(carLoanDetail.getLovDescModelDesc());
				agreement.setCarChasisNo(carLoanDetail.getCarChasisNo());
				agreement.setCarColor(carLoanDetail.getCarColor());
				agreement.setCarCapacity(String.valueOf(carLoanDetail.getCarCapacity()));
				agreement.setInsuranceType(carLoanDetail.getInsuranceType());
				agreement.setInsuranceDesc(carLoanDetail.getInsuranceDesc());
				agreement.setEngineNo(carLoanDetail.getEngineNumber());
				agreement.setPurchaseOrder(carLoanDetail.getPurchageOdrNumber());
				agreement.setPurchaseOrderDate(DateUtility.formatUtilDate(carLoanDetail.getPurchaseDate(), PennantConstants.dateFormate));
				agreement.setMerchantName(carLoanDetail.getLovDescCarDealerName());
				agreement.setMerchantPhone(carLoanDetail.getLovDescCarDealerPhone());
				agreement.setMerchantFax(carLoanDetail.getLovDescCarDealerFax());
				agreement.setQuotationNo(carLoanDetail.getQuoationNbr());
				agreement.setCarRegistrationNo(carLoanDetail.getCarRegNo());
				agreement.setQuotationDate(DateUtility.formatUtilDate(carLoanDetail.getQuoationDate(), PennantConstants.dateFormate));
				agreement.setVehicleStatus(carLoanDetail.getLovDescCarLoanForName());
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		return agreement;
	}

	/**
	 * To get Mortgage Loan Details
	 * @param agreement
	 * @param mortgageLoanDetail
	 * @return
	 */

	private AgreementDetail getMortgageLoanDetails(AgreementDetail agreement, MortgageLoanDetail mortgageLoanDetail) {
		logger.debug("Entering");
		try {
			if (mortgageLoanDetail != null) {
				agreement.setAssetType(mortgageLoanDetail.getLovDescMortgPropertyName());
				agreement.setAssetarea(mortgageLoanDetail.getMortgAddrPOBox());
				agreement.setAssetRegistration(mortgageLoanDetail.getMortRegistrationNo());
				agreement.setDeedno(mortgageLoanDetail.getMortDeedNo());
				agreement.setAssetStatus(mortgageLoanDetail.getMortStatus());
				agreement.setAssetareainSF(mortgageLoanDetail.getMortAreaSF()== null?"":String.valueOf(mortgageLoanDetail.getMortAreaSF()));
				agreement.setAssetage(mortgageLoanDetail.getMortAreaSM() == null?"":String.valueOf(mortgageLoanDetail.getMortAge()));
				agreement.setAssetareainSM(mortgageLoanDetail.getMortAreaSM()==null?"":String.valueOf(mortgageLoanDetail.getMortAreaSM()));
				agreement.setAssetMarketvle(formatdAmount(mortgageLoanDetail.getMortgCurrentValue()));
				agreement.setAssetPricePF(formatdAmount(mortgageLoanDetail.getMortPricePF()));
				agreement.setAssetFinRatio(formatdAmount(mortgageLoanDetail.getMortFinRatio()));
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
		return agreement;
	}

	/**
	 * To get Goods Loan Detail
	 * @param agreement
	 * @param goodsLoanDetail
	 * @return
	 */
	private AgreementDetail getGoodsLoanDetail(AgreementDetail agreement, List<GoodsLoanDetail> goodsLoanDetail) {
		logger.debug("Entering");
		try {
			if (goodsLoanDetail != null && !goodsLoanDetail.isEmpty()) {
				agreement.setGoodsLoanDetails(new ArrayList<AgreementDetail.GoodLoanDetails>());
				for (GoodsLoanDetail goodsLoan : goodsLoanDetail) {
					GoodLoanDetails goodLoanDetails = agreement.new GoodLoanDetails();
					goodLoanDetails.setItemType(goodsLoan.getItemDescription());
					goodLoanDetails.setItemNumber(goodsLoan.getItemNumber());
					goodLoanDetails.setItemDescription(goodsLoan.getItemDescription());
					goodLoanDetails.setQuantity(String.valueOf(goodsLoan.getQuantity()));
					goodLoanDetails.setUnitPrice(formatdAmount(goodsLoan.getUnitPrice().multiply(new BigDecimal(goodsLoan.getQuantity()))));
					agreement.setMerchantName(goodsLoan.getLovDescSellerID());
					agreement.setMerchantPhone(goodsLoan.getLovDescSellerPhone());
					agreement.setMerchantFax(goodsLoan.getLovDescSellerFax());
					agreement.setQuotationDate(DateUtility.formatUtilDate(goodsLoan.getAddtional6(), PennantConstants.dateFormate));
					agreement.getGoodsLoanDetails().add(goodLoanDetails);
				}
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
		return agreement;
	}

	/** To get General Goods Loan Detail
	 * @param agreement
	 * @param goodsLoanDetail
	 * @return
	 */
	private AgreementDetail getFinanceDetails(AgreementDetail agreement,FinanceDetail detail) {
		logger.debug("Entering");
		try {
			FinanceMain	main=detail.getFinScheduleData().getFinanceMain();
			agreement.setFinCcy(main.getFinCcy());
			agreement.setFinRef(main.getFinReference());
			agreement.setFinTypeDesc(main.getFinType());
			agreement.setReferenceNo(main.getFinReference());
			agreement.setNoOfPayments(String.valueOf(main.getCalTerms()+main.getGraceTerms()));
			agreement.setStartDate(formatdDate(main.getFinStartDate()));
			agreement.setEndDate(formatdDate(main.getCalMaturity()));
			agreement.setContractDate(formatdDate(main.getFinStartDate()));
			agreement.setPrice(formatdAmount(main.getFinAmount()));
			agreement.setDownPayment(formatdAmount(main.getDownPayment()));
			agreement.setAuthorization1(main.getLovDescAuthorization1Name());
			agreement.setAuthorization2(main.getLovDescAuthorization2Name());
			// Finance Amount + Agreement fee			
			List<FeeRule> feeRules = detail.getFinScheduleData().getFeeRules();
			
			BigDecimal feeCharge=BigDecimal.ZERO;
			BigDecimal aggreementFee=BigDecimal.ZERO;
			BigDecimal takafulFee=BigDecimal.ZERO;
			for (FeeRule feeRule : feeRules) {
				feeCharge=feeCharge.add(feeRule.getFeeAmount().subtract(feeRule.getWaiverAmount()).subtract(feeRule.getPaidAmount()));
				if (feeRule.getFeeCode().equals("ADMIN")) {
					aggreementFee = feeRule.getFeeAmount().subtract(feeRule.getWaiverAmount()).subtract(feeRule.getPaidAmount());
					continue;
				}
				if (feeRule.getFeeCode().equals("TAKAFUL")) {
					takafulFee = feeRule.getFeeAmount().subtract(feeRule.getWaiverAmount()).subtract(feeRule.getPaidAmount());
					continue;
				}
			}
			agreement.setCostOfGoods(formatdAmount(main.getFinAmount().add(aggreementFee)));
			// Only TakafulInsurance
			agreement.setTakafulInsurance(formatdAmount(takafulFee));
			// Down Payment
			agreement.setAdvacePayment(formatdAmount(main.getDownPayment()));
			//LPO Price Vehicle
			agreement.setLpoPrice(formatdAmount(main.getFinAmount().subtract(main.getDownPaySupl())));
			// Gross Profit
			agreement.setProfit(formatdAmount(main.getTotalGrossPft()));
			// Finance Amount-Down payment+Gross Profit
			agreement.setRemainingBal(formatdAmount(main.getFinAmount().add(main.getTotalGrossPft()).subtract(main.getDownPayment()).add(feeCharge)));
			
			agreement.setFinAmount(formatdAmount(main.getFinAmount()));
			
			String word=NumberToEnglishWords.getAmountInText(PennantApplicationUtil.formateAmount(
					main.getFinAmount().add(main.getTotalGrossPft()).subtract(main.getDownPayment()).add(feeCharge),main.getLovDescFinFormatter()), main.getFinCcy());
			agreement.setFinAmountInWords(word.toUpperCase());
			
			agreement.setTotalAmount(formatdAmount(main.getFinAmount().subtract(main.getDownPayment()).add(feeCharge)));
			
			agreement.setTenureMonths(String.valueOf(DateUtility.getMonthsBetween(detail.getFinScheduleData().getFinanceMain().getFinStartDate(),
					detail.getFinScheduleData().getFinanceMain().getMaturityDate(), true)));
			agreement.setInstRate(PennantApplicationUtil.formatRate(main.getEffectiveRateOfReturn().doubleValue(), 9));
			agreement.setCustAccountNo(PennantApplicationUtil.formatAccountNumber(main.getDisbAccountId()));
			agreement.setRepayAccount(PennantApplicationUtil.formatAccountNumber(main.getRepayAccountId()));
			agreement.setCustDSR(String.valueOf(main.getCustDSR() == null ? "0.00" : main.getCustDSR()));
			agreement.setRepayFrq(FrequencyUtil.getFrequencyDetail(main.getRepayFrq()).getFrequencyDescription());
			agreement.setNextRepayDate(formatdDate(main.getNextRepayDate()));
			agreement.setLastRepayDate(formatdDate(main.getMaturityDate()));
			agreement.setTotRepayPrdAmount(formatdAmount(main.getTotalRepayAmt().subtract(main.getTotalGrossGrcPft())));
			agreement.setNextInstAmount(formatdAmount(main.getFirstRepay()));//TODO-check condition if grace exists
			agreement.setFinPurpose(main.getLovDescFinPurposeName());
			
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
		return agreement;
	}
	
	
	
	/** To get General Goods Loan Detail
	 * @param agreement
	 * @param goodsLoanDetail
	 * @return
	 */
	private AgreementDetail getGenGoodsLoanDetail(AgreementDetail agreement, List<GenGoodsLoanDetail> goodsLoanDetail) {
		logger.debug("Entering");
		try {
			if (goodsLoanDetail != null && !goodsLoanDetail.isEmpty()) {
				agreement.setGoodsLoanDetails(new ArrayList<AgreementDetail.GoodLoanDetails>());
				for (GenGoodsLoanDetail goodsLoan : goodsLoanDetail) {
					GoodLoanDetails goodLoanDetails = agreement.new GoodLoanDetails();
					goodLoanDetails.setSupplierName(goodsLoan.getLovDescSellerID());
					goodLoanDetails.setItemType(goodsLoan.getItemDescription());
					goodLoanDetails.setItemNumber(goodsLoan.getItemNumber());
					goodLoanDetails.setItemDescription(goodsLoan.getItemDescription());
					goodLoanDetails.setQuantity(String.valueOf(goodsLoan.getQuantity()));
					goodLoanDetails.setUnitPrice(formatdAmount(goodsLoan.getUnitPrice().multiply(new BigDecimal(goodsLoan.getQuantity()))));
					agreement.setMerchantName(goodsLoan.getLovDescSellerID());
					agreement.setMerchantPhone(goodsLoan.getLovDescSellerPhone());
					agreement.setMerchantFax(goodsLoan.getLovDescSellerFax());
					agreement.setQuotationDate(DateUtility.formatUtilDate(goodsLoan.getAddtional6(), PennantConstants.dateFormate));
					agreement.getGoodsLoanDetails().add(goodLoanDetails);
				}
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
		return agreement;
	}

	/**
	 * To get Commodity Loan Detail
	 * @param agreement
	 * @param commdityLoanDetail
	 * @param detail
	 * @return
	 */
	private AgreementDetail getCommodityLoanDetail(AgreementDetail agreement, List<CommidityLoanDetail> commdityLoanDetail, FinanceDetail detail) {
		logger.debug("Entering");
		try {
			if (commdityLoanDetail != null && !commdityLoanDetail.isEmpty()) {
				agreement.setTenureDays(DateUtility.getDaysBetween(detail.getFinScheduleData().getFinanceMain().getFinStartDate(), detail.getFinScheduleData().getFinanceMain().getMaturityDate()));
				agreement.setFinTypeDesc(detail.getFinScheduleData().getFinanceType().getFinTypeDesc());
				agreement.setBrokerName(detail.getCommidityLoanHeader().getBrokerName());
				agreement.setSplInstruction(detail.getCommidityLoanHeader().getSplInstruction());
				agreement.setCommidityLoanDetails(new ArrayList<AgreementDetail.CommidityLoanDetails>());
				for (CommidityLoanDetail commidityLoanDetail : commdityLoanDetail) {
					CommidityLoanDetails details = agreement.new CommidityLoanDetails();
					details.setItemType(commidityLoanDetail.getItemType());
					details.setQuantity(String.valueOf(commidityLoanDetail.getQuantity()));
					details.setUnitBuyPrice(PennantApplicationUtil.formatRate(commidityLoanDetail.getUnitBuyPrice().doubleValue(), 9));
					details.setBuyAmount(PennantApplicationUtil.amountFormate(commidityLoanDetail.getBuyAmount(), detail.getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
					details.setUnitSellPrice(PennantApplicationUtil.formatRate(commidityLoanDetail.getUnitSellPrice().doubleValue(), 9));
					details.setSellAmount(PennantApplicationUtil.amountFormate(commidityLoanDetail.getSellAmount(), detail.getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
					agreement.getCommidityLoanDetails().add(details);
				}
			}else{
				agreement.setCommidityLoanDetails(new ArrayList<AgreementDetail.CommidityLoanDetails>(1));
				agreement.getCommidityLoanDetails().add(agreement.new CommidityLoanDetails());
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
		return agreement;
	}

	/**To get the customer Credit Review Details
	 * @param agreement
	 * @return
	 */
	private AgreementDetail getCustomerCreditReviewDetails(AgreementDetail agreement,String category) {
		logger.debug("Entering");
		try {
			// -----------------Customer Credit Review Details
			// 1 Balance Sheet
			// 2 Income Statement
			// 3 Cash Flow Information
			int noOfYears = 3;
			Map<String, List<FinCreditReviewSummary>> detailedMap = this.creditApplicationReviewService.getListCreditReviewSummaryByCustId2(custid, noOfYears, DateUtility.getYear(DateUtility.getUtilDate()), category, "");
			if (detailedMap.size() > 0) {
				agreement.setAdtYear1(null);
				agreement.setAdtYear2(null);
				agreement.setAdtYear3(null);
				List<FinCreditReviewSummary> listCreditReviewSummaries1 = null;
				List<FinCreditReviewSummary> listCreditReviewSummaries2 = null;
				List<FinCreditReviewSummary> listCreditReviewSummaries3 = null;
				for (Map.Entry<String, List<FinCreditReviewSummary>> entry : detailedMap.entrySet()) {
					if (agreement.getAdtYear1() == null) {
						agreement.setAdtYear1(entry.getKey());
						listCreditReviewSummaries1 = entry.getValue();
					} else if (agreement.getAdtYear2() == null) {
						agreement.setAdtYear2(entry.getKey());
						listCreditReviewSummaries2 = entry.getValue();
					} else if (agreement.getAdtYear3() == null) {
						agreement.setAdtYear3(entry.getKey());
						listCreditReviewSummaries3 = entry.getValue();
					}
				}
				CustomerCreditReview crediReview = null;
				FinCreditReviewSummary finCreditReviewSummary = null;
				for (int i = 0; i < listCreditReviewSummaries1.size(); i++) {
					finCreditReviewSummary = listCreditReviewSummaries1.get(i);
					if (i == 0) {
						agreement.setCreditReviewsBalance(new ArrayList<AgreementDetail.CustomerCreditReview>());
						agreement.setCreditReviewsRatio(new ArrayList<AgreementDetail.CustomerCreditReview>());
						crediReview = agreement.new CustomerCreditReview();
					}
					if (i == 0 || finCreditReviewSummary.getLovDescCategoryID() == listCreditReviewSummaries1.get(i - 1).getLovDescCategoryID()) {
					} else {
						agreement.getCreditReviewsBalance().add(crediReview);
						crediReview = agreement.new CustomerCreditReview();
						crediReview.setCustomerCreditReviewDetails(new ArrayList<AgreementDetail.CustomerCreditReviewDetails>());
					}
					crediReview.setCategoryName(finCreditReviewSummary.getLovDescCategoryDesc());
					CustomerCreditReviewDetails customerCreditReviewDetails = agreement.new CustomerCreditReviewDetails();
					customerCreditReviewDetails.setSubCategoryName(finCreditReviewSummary.getLovDescSubCategoryDesc());
					customerCreditReviewDetails.setYear1(formatdAmount(finCreditReviewSummary.getItemValue()));
					customerCreditReviewDetails.setYear2(formatdAmount(listCreditReviewSummaries2.get(i).getItemValue()));
					customerCreditReviewDetails.setYear3(formatdAmount(listCreditReviewSummaries3.get(i).getItemValue()));
					crediReview.getCustomerCreditReviewDetails().add(customerCreditReviewDetails);
				}
				agreement.getCreditReviewsRatio().add(crediReview);
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
		return agreement;
	}

	/**
	 * To get the check List details 
	 * @param agreement
	 * @param detail
	 * @return
	 */
	private AgreementDetail getCheckListDetails(AgreementDetail agreement, FinanceDetail detail) {
		logger.debug("Entering");
		try {
			// Add the Check List Data To the Agreement object
			List<FinanceReferenceDetail> finRefDetailsList = detail.getFinRefDetailsList();
			if (finRefDetailsList != null && !finRefDetailsList.isEmpty()) {
				agreement.setCheckListDetails(new ArrayList<AgreementDetail.CheckListDetails>());
				for (FinanceReferenceDetail checkListReference : finRefDetailsList) {
					CheckListDetails checkListDetails = agreement.new CheckListDetails();
					checkListDetails.setQuestionId(checkListReference.getFinRefId());
					checkListDetails.setQuestion(checkListReference.getLovDescRefDesc());
					checkListDetails.setListquestionAns(new ArrayList<AgreementDetail.CheckListAnsDetails>());
					for (CheckListDetail checkListDetail : checkListReference.getLovDesccheckListDetail()) {
						CheckListAnsDetails ansDetails = agreement.new CheckListAnsDetails();
						ansDetails.setQuestionId(checkListReference.getFinRefId());
						ansDetails.setQuestionAns(checkListDetail.getAnsDesc());
						if (detail.getFinanceCheckList() != null && !detail.getFinanceCheckList().isEmpty()) {
							for (FinanceCheckListReference financeCheckList : detail.getFinanceCheckList()) {
								if (financeCheckList.getQuestionId() == checkListReference.getFinRefId() && financeCheckList.getAnswer() == checkListDetail.getAnsSeqNo()) {
									ansDetails.setQuestionRem("YES");
									break;
								} else {
									ansDetails.setQuestionRem("");
								}
							}
						}
						checkListDetails.getListquestionAns().add(ansDetails);
					}
					agreement.getCheckListDetails().add(checkListDetails);
				}
			}
			
			if (agreement.getCheckListDetails()==null) {
				agreement.setCheckListDetails(new ArrayList<AgreementDetail.CheckListDetails>());
				CheckListDetails checkListDetails = agreement.new CheckListDetails();
				checkListDetails.setListquestionAns(new ArrayList<AgreementDetail.CheckListAnsDetails>());
				checkListDetails.getListquestionAns().add(agreement.new CheckListAnsDetails());
				agreement.getCheckListDetails().add(checkListDetails);
			}
			
			
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
		return agreement;
	}

	/**
	 * TO get Recommendation form notes table
	 * @param agreement
	 * @param finreference
	 * @return
	 */
	private AgreementDetail getRecommendations(AgreementDetail agreement, String finreference) {
		logger.debug("Entering");
		try {
			Notes note = new Notes();
			note.setModuleName(PennantConstants.NOTES_MODULE_FINANCEMAIN);
			note.setReference(finreference);
			List<Notes> list = getNotesService().getNotesList(note, false);
			if (list != null && !list.isEmpty()) {
				agreement.setRecommendations(new ArrayList<AgreementDetail.Recommendation>(list.size()));
				for (Notes notes : list) {
					Recommendation recommendation = agreement.new Recommendation();
					String noteType = "";
					if (notes.getRemarkType().equals(PennantConstants.NOTES_TYPE_COMMENT)) {
						noteType = Labels.getLabel("common.noteType.Comment");
					} else if (notes.getRemarkType().equals(PennantConstants.NOTES_TYPE_RECOMMEND)) {
						noteType = Labels.getLabel("common.noteType.Recommend");
					}
					recommendation.setNoteType(noteType);
					recommendation.setNoteDesc(notes.getRemarks());
					recommendation.setCommentedDate(DateUtility.formatUtilDate(notes.getInputDate(), PennantConstants.dateTimeAMPMFormat));
					recommendation.setUserName(notes.getUsrLogin().toUpperCase());
					agreement.getRecommendations().add(recommendation);
				}
			}else {
				agreement.setRecommendations(new ArrayList<AgreementDetail.Recommendation>());
				agreement.getRecommendations().add(agreement.new Recommendation());
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
		return agreement;
	}

	/**
	 * To get the overridden item in scoring and eligibility as exception List
	 * @param agreement
	 * @param detail
	 * @return
	 */
	private AgreementDetail getExceptionList(AgreementDetail agreement, FinanceDetail detail) {
		logger.debug("Entering");
		try {
			if (detail != null) {
				List<FinanceEligibilityDetail> eligibilityList = detail.getElgRuleList();
				if (eligibilityList != null && !eligibilityList.isEmpty()) {
					agreement.setExceptionLists(new ArrayList<AgreementDetail.ExceptionList>(eligibilityList.size()));
					for (FinanceEligibilityDetail financeEligibilityDetail : eligibilityList) {
						if (financeEligibilityDetail.isUserOverride()) {
							ExceptionList exceptionList = agreement.new ExceptionList();
							exceptionList.setExceptionItem("Eligibility");
							exceptionList.setExceptionDesc(financeEligibilityDetail.getLovDescElgRuleCodeDesc());
							agreement.getExceptionLists().add(exceptionList);
						}
					}
				}
				List<FinanceScoreHeader> finscoreheader = detail.getFinScoreHeaderList();
				if (finscoreheader != null && !finscoreheader.isEmpty()) {
					if (agreement.getExceptionLists() == null) {
						agreement.setExceptionLists(new ArrayList<AgreementDetail.ExceptionList>(eligibilityList.size()));
					}
					for (FinanceScoreHeader financeScoreHeader : finscoreheader) {
						if (financeScoreHeader.isOverride()) {
							ExceptionList exceptionList = agreement.new ExceptionList();
							exceptionList.setExceptionItem("Scoring");
							exceptionList.setExceptionDesc(financeScoreHeader.getGroupCodeDesc());
							agreement.getExceptionLists().add(exceptionList);
						}
					}
				}
			}
			
			if (agreement.getExceptionLists() == null) {
				agreement.setExceptionLists(new ArrayList<AgreementDetail.ExceptionList>(1));
				agreement.getExceptionLists().add(agreement.new ExceptionList());
			} else if (agreement.getExceptionLists().isEmpty()) {
				agreement.getExceptionLists().add(agreement.new ExceptionList());
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
		return agreement;
	}
	
	private AgreementDetail setGroupRecommendations(AgreementDetail agreement, String finreference) {
		logger.debug("Entering");
		try {
			Notes note = new Notes();
			note.setModuleName(PennantConstants.NOTES_MODULE_FINANCEMAIN);
			note.setReference(finreference);
			List<Notes> list = getNotesService().getNotesListByRole(note, false, null);
			if (list != null && !list.isEmpty()) {
				agreement.setGroupRecommendations(new ArrayList<AgreementDetail.GroupRecommendation>());
				// prepare Grouping
				HashMap<String, ArrayList<Notes>> hashMap = new HashMap<String, ArrayList<Notes>>();
				for (Notes notes : list) {
					if (!notes.getRoleCode().equals("")) {
						if (hashMap.containsKey(notes.getRoleCode())) {
							hashMap.get(notes.getRoleCode()).add(notes);
						} else {
							ArrayList<Notes> arrayList = new ArrayList<Notes>();
							arrayList.add(notes);
							hashMap.put(notes.getRoleCode(), arrayList);
						}
					}
				}
				for (String roleCode : hashMap.keySet()) {
					GroupRecommendation groupRecommendation = agreement.new GroupRecommendation();
					ArrayList<Notes> templist = hashMap.get(roleCode);
					groupRecommendation.setRecommendations(new ArrayList<AgreementDetail.Recommendation>());
					for (Notes notes : templist) {
						groupRecommendation.setUserRole(notes.getRoleDesc());
						Recommendation recommendation = agreement.new Recommendation();
						String noteType = "";
						if (notes.getRemarkType().equals(PennantConstants.NOTES_TYPE_COMMENT)) {
							noteType = Labels.getLabel("common.noteType.Comment");
						} else if (notes.getRemarkType().equals(PennantConstants.NOTES_TYPE_RECOMMEND)) {
							noteType = Labels.getLabel("common.noteType.Recommend");
						}
						recommendation.setNoteType(noteType);
						recommendation.setNoteDesc(notes.getRemarks());
						recommendation.setCommentedDate(DateUtility.formatUtilDate(notes.getInputDate(), PennantConstants.dateTimeAMPMFormat));
						recommendation.setUserName(notes.getUsrLogin());
						recommendation.setUserRole(notes.getRoleDesc());
						groupRecommendation.getRecommendations().add(recommendation);
					}
					agreement.getGroupRecommendations().add(groupRecommendation);
				}
			} else {
				agreement.setGroupRecommendations(new ArrayList<AgreementDetail.GroupRecommendation>());
				GroupRecommendation groupRecommendation = agreement.new GroupRecommendation();
				groupRecommendation.setRecommendations(new ArrayList<AgreementDetail.Recommendation>());
				Recommendation recommendation = agreement.new Recommendation();
				groupRecommendation.getRecommendations().add(recommendation);
				agreement.getGroupRecommendations().add(groupRecommendation);
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
		return agreement;
	}
	
	
}