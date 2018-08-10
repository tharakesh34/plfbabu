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
 *
 * FileName    		:  AgreementGeneration.java												*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  27-07-2015															*
 *                                                                  
 * Modified Date    :  23-07-2018															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-05-2018       Pennant	                 1.0          Updated as part of Agreements     * 
 * 23-07-2018       Pennant                  1.1          Adding the Collateral Extended    * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.util;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Filedownload;

import com.aspose.words.SaveFormat;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfAction;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.TextField;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.NumberToEnglishWords;
import com.pennant.app.util.RateUtil;
import com.pennant.backend.delegationdeviation.DeviationHelper;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.Property;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.MMAgreement.MMAgreement;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.customermasters.CustEmployeeDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.AgreementDetail;
import com.pennant.backend.model.finance.AgreementDetail.ActivityDetail;
import com.pennant.backend.model.finance.AgreementDetail.AppExpDetail;
import com.pennant.backend.model.finance.AgreementDetail.AppIncDetail;
import com.pennant.backend.model.finance.AgreementDetail.BankingDetail;
import com.pennant.backend.model.finance.AgreementDetail.CoApplicant;
import com.pennant.backend.model.finance.AgreementDetail.ContactDetail;
import com.pennant.backend.model.finance.AgreementDetail.Covenant;
import com.pennant.backend.model.finance.AgreementDetail.CreditReviewEligibilitySummary;
import com.pennant.backend.model.finance.AgreementDetail.CustomerFinance;
import com.pennant.backend.model.finance.AgreementDetail.Disbursement;
import com.pennant.backend.model.finance.AgreementDetail.Document;
import com.pennant.backend.model.finance.AgreementDetail.Eligibility;
import com.pennant.backend.model.finance.AgreementDetail.EmailDetail;
import com.pennant.backend.model.finance.AgreementDetail.ExceptionList;
import com.pennant.backend.model.finance.AgreementDetail.ExtendedDetail;
import com.pennant.backend.model.finance.AgreementDetail.ExtendedDetailCollateral;
import com.pennant.backend.model.finance.AgreementDetail.ExternalLiabilityDetail;
import com.pennant.backend.model.finance.AgreementDetail.GroupRecommendation;
import com.pennant.backend.model.finance.AgreementDetail.InternalLiabilityDetail;
import com.pennant.backend.model.finance.AgreementDetail.IrrDetail;
import com.pennant.backend.model.finance.AgreementDetail.LoanDeviation;
import com.pennant.backend.model.finance.AgreementDetail.LoanQryDetails;
import com.pennant.backend.model.finance.AgreementDetail.Score;
import com.pennant.backend.model.finance.AgreementDetail.SourcingDetail;
import com.pennant.backend.model.finance.AgreementDetail.VerificationDetail;
import com.pennant.backend.model.finance.AgreementFieldDetails;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinAssetEvaluation;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinIRRDetails;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceScoreDetail;
import com.pennant.backend.model.finance.FinanceScoreHeader;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.psl.PSLCategory;
import com.pennant.backend.model.finance.psl.PSLDetail;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevSubCategory;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.loanquery.QueryDetail;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.solutionfactory.DeviationParam;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.service.NotesService;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.administration.SecurityUserService;
import com.pennant.backend.service.applicationmaster.BranchService;
import com.pennant.backend.service.applicationmaster.MMAgreementService;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.PSLDetailService;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.CreditApplicationReviewService;
import com.pennant.backend.service.loanquery.QueryDetailService;
import com.pennant.backend.util.DeviationConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.activity.log.Activity;
import com.pennanttech.activity.log.ActivityLogService;
import com.pennanttech.framework.security.core.User;
import com.pennanttech.pennapps.core.feature.model.ModuleMapping;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.pff.finsampling.service.FinSamplingService;
import com.pennanttech.pennapps.pff.sampling.model.Sampling;
import com.pennanttech.pennapps.pff.sampling.model.SamplingDetail;
import com.pennanttech.pennapps.pff.verification.Decision;
import com.pennanttech.pennapps.pff.verification.RequestType;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.fi.RCUDocStatus;
import com.pennanttech.pennapps.pff.verification.fi.RCUDocVerificationType;
import com.pennanttech.pennapps.pff.verification.model.FieldInvestigation;
import com.pennanttech.pennapps.pff.verification.model.LegalVerification;
import com.pennanttech.pennapps.pff.verification.model.RCUDocument;
import com.pennanttech.pennapps.pff.verification.model.RiskContainmentUnit;
import com.pennanttech.pennapps.pff.verification.model.TechnicalVerification;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pennapps.pff.verification.service.VerificationService;

public class AgreementGeneration implements Serializable {
	private static final long		serialVersionUID	= -2030216591697935342L;
	private static final Logger		logger				= Logger.getLogger(AgreementGeneration.class);

	public static final String		refField			= "SmartForm[0].LPOForm[0].txtrefno[0]";
	public static final String		statusField			= "SmartForm[0].LPOForm[0].ddlIsActive[0]";
	public static final String		dealerId			= "SmartForm[0].LPOForm[0].txtdealerid[0]";
	ArrayList<ValueLabel> interestRateType = PennantStaticListUtil.getInterestRateType(true);

	private NotesService			notesService;
	private FinanceDetailService	financeDetailService;
	private MMAgreementService		mMAgreementService;
	
	@Autowired
	private CustomerDetailsService customerDetailsService;
	@Autowired
	private VerificationService verificationService;
	@Autowired
	private SearchProcessor searchProcessor;
	private CollateralSetupService collateralSetupService;
	private SecurityUserService securityUserService;
	private ActivityLogService activityLogService;
	@Autowired
	private BranchService branchService;
	@Autowired
	private DeviationHelper	deviationHelper;
	@Autowired
	private QueryDetailService queryDetailService;
	@Autowired
	private PSLDetailService pSLDetailService;
	@Autowired
	private FinSamplingService finSamplingService;
	@Autowired
	private CreditApplicationReviewService creditApplicationReviewService;
	
	private  List<DocumentType> documentTypeList;
	
	private List<ValueLabel> listLandHolding=PennantStaticListUtil.getYesNo();
	private List<ValueLabel> subCategoryList=PennantStaticListUtil.getSubCategoryList();
	private List<ValueLabel> landAreaList=PennantStaticListUtil.getLandAreaList();
	private List<ValueLabel> sectorList=PennantStaticListUtil.getPSLSectorList();
	BigDecimal totalIncome=BigDecimal.ZERO;
	BigDecimal totalExpense=BigDecimal.ZERO;
	
	private List<Property> severities = PennantStaticListUtil.getManualDeviationSeverities();

	public AgreementGeneration() {
		super();
	}

	/**
	 * Method for Generating LPO Document
	 * 
	 * @param detail
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public byte[] getAggrementDocToPdf(AgreementDetail agreementDetail, String finPurpose, String aggrementName)
			throws Exception {
		logger.debug(" Entering ");

		AgreementEngine engine = new AgreementEngine(finPurpose);
		engine.setTemplate(aggrementName);
		engine.loadTemplate();
		engine.mergeFields(agreementDetail);

		ByteArrayOutputStream asposePdf = new ByteArrayOutputStream();
		engine.getDocument().save(asposePdf, SaveFormat.PDF);

		ByteArrayOutputStream itesxPdf = new ByteArrayOutputStream();
		PdfStamper stamper = new PdfStamper(new PdfReader(asposePdf.toByteArray()), itesxPdf);
		PdfWriter writer = stamper.getWriter();

		/* Combo box filed Creation */
		TextField approvalStatus = new TextField(writer, new Rectangle(500, 595, 560, 610), statusField);
		String[] selOption = { "Select", "Accept", "Decline" };
		String[] selOptionValue = { "0", "A", "D" };
		approvalStatus.setChoices(selOption);
		approvalStatus.setChoiceExports(selOptionValue);
		approvalStatus.setChoiceSelection(0);
		approvalStatus.setBorderStyle(2);
		approvalStatus.setBorderColor(BaseColor.BLACK);
		approvalStatus.setFontSize(9);
		approvalStatus.setBackgroundColor(BaseColor.ORANGE);
		approvalStatus.setTextColor(BaseColor.BLACK);

		PdfFormField fieldcombobx = approvalStatus.getComboField();
		stamper.addAnnotation(fieldcombobx, 1);
		/* Hidden box filed Creation */
		TextField finref = new TextField(writer, new Rectangle(500, 700, 560, 710), refField);
		finref.setVisibility(TextField.HIDDEN);
		finref.setText(agreementDetail.getFinRef());
		PdfFormField fieldmoreHiddenTextrefNo = finref.getTextField();
		stamper.addAnnotation(fieldmoreHiddenTextrefNo, 1);

		/* Hidden box filed Creation */
		TextField delearid = new TextField(writer, new Rectangle(500, 710, 560, 720), dealerId);
		delearid.setVisibility(TextField.HIDDEN);
		delearid.setText(agreementDetail.getCarDealer());
		PdfFormField fieldhiddentext = delearid.getTextField();
		stamper.addAnnotation(fieldhiddentext, 1);

		PdfAction action = PdfAction.javaScript("if(this.getField('" + statusField + "').value  =='0') { app.alert('"
				+ Labels.getLabel("LPO_ACCEPT_DECLINE") + "',3); }", writer);
		writer.setAdditionalAction(PdfWriter.DOCUMENT_CLOSE, action);
		action = PdfAction.javaScript("", writer);
		stamper.setPageAction(PdfWriter.PAGE_OPEN, action, 1);

		engine.close();
		stamper.close();
		asposePdf.close();
		itesxPdf.close();

		engine = null;
		stamper = null;
		asposePdf = null;
		logger.debug(" Leaving ");
		return itesxPdf.toByteArray();
	}

	/**
	 * @param data
	 * @param list
	 * @param finPurpose
	 */
	public void getGenerateAgreementsAsZIP(FinanceReferenceDetail data, List<AgreementDetail> list, String finPurpose) {
		logger.debug(" Entering ");
		try {

			if (list != null && !list.isEmpty()) {

				AgreementEngine engine = new AgreementEngine(finPurpose);
				engine.setTemplate(data.getLovDescAggReportName());

				ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
				ZipOutputStream out = new ZipOutputStream(arrayOutputStream);

				int count = 0;
				String finReference = list.get(0).getFinRef();

				for (AgreementDetail agreementDetail : list) {
					engine.loadTemplate();
					count++;
					String fileName = "";
					if (StringUtils.equals(data.getAggType(), PennantConstants.DOC_TYPE_PDF)) {
						fileName = finReference + "_" + StringUtils.trimToEmpty(data.getLovDescNamelov()) + (count)
								+ PennantConstants.DOC_TYPE_PDF_EXT;
					} else {
						fileName = finReference + "_" + StringUtils.trimToEmpty(data.getLovDescNamelov()) + (count)
								+ PennantConstants.DOC_TYPE_WORD_EXT;
					}

					engine.mergeFields(agreementDetail);
					ByteArrayOutputStream outputstram = new ByteArrayOutputStream();
					engine.getDocument().save(outputstram, SaveFormat.PDF);
					out.putNextEntry(new ZipEntry(fileName));
					out.write(outputstram.toByteArray());
					out.closeEntry();
				}

				out.close();
				String zipfileName = finReference + StringUtils.trimToEmpty(data.getLovDescNamelov()) + ".zip";

				byte[] tobytes = arrayOutputStream.toByteArray();
				arrayOutputStream.close();
				arrayOutputStream = null;

				Filedownload.save(new AMedia(zipfileName, "zip", "application/*", tobytes));
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);

		}
		logger.debug(" Leaving ");
	}

	/**
	 * @param data
	 * @param list
	 * @param finPurpose
	 */
	public void getGenerateAgreementsWithMultiplePages(FinanceReferenceDetail data, List<AgreementDetail> list,
			String finPurpose) {
		logger.debug(" Entering ");
		try {

			if (list != null && !list.isEmpty()) {

				String finReference = list.get(0).getFinRef();
				AgreementEngine engine = new AgreementEngine(finPurpose);
				engine.setTemplate(data.getLovDescAggReportName());
				engine.loadTemplate();

				for (AgreementDetail agreementDetail : list) {
					engine.mergeFields(agreementDetail);
					engine.appendToMasterDocument();
				}

				StringBuilder fileName = new StringBuilder();
				fileName.append(finReference);
				fileName.append("_");
				fileName.append(StringUtils.trimToEmpty(data.getLovDescNamelov()));

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				if (StringUtils.equals(data.getAggType(), PennantConstants.DOC_TYPE_PDF)) {

					fileName.append(PennantConstants.DOC_TYPE_PDF_EXT);
					engine.getMasterDocument().save(stream, SaveFormat.PDF);

					Filedownload.save(new AMedia(fileName.toString(), "pdf", "application/pdf", stream.toByteArray()));
				} else {
					fileName.append(PennantConstants.DOC_TYPE_WORD_EXT);
					engine.getMasterDocument().save(stream, SaveFormat.DOCX);

					Filedownload.save(new AMedia(fileName.toString(), "msword", "application/msword", stream
							.toByteArray()));
				}

				engine.close();

			}
		} catch (Exception e) {
			logger.error("Exception: ", e);

		}
		logger.debug(" Leaving ");
	}

	// ================================================
	// =============== Agreement Generation============
	// ================================================

	/**
	 * To prepare Agreement Data
	 * 
	 * @param detail
	 * @return
	 */
	public AgreementDetail getAggrementData(FinanceDetail detail, String aggModuleDetails, User userDetails) {
 		logger.debug("Entering");
		
		// Create New Object For The Agreement Detail
		AgreementDetail agreement = new AgreementDetail();
		// Application Date
		Date appldate = DateUtility.getAppDate();
		String appDate = DateUtility.formatToLongDate(appldate);
		agreement.setAppDate(appDate);
		long userId = userDetails.getUserId();
		String usrName = userDetails.getUsername();
		String usrDeptName = userDetails.getSecurityUser().getLovDescUsrDeptCodeName();
		String usrEmail = userDetails.getSecurityUser().getUsrEmail();
		agreement.setUserId(String.valueOf(userId));
		agreement.setUserName(StringUtils.trimToEmpty(usrName));
		agreement.setUserDeptName(StringUtils.trimToEmpty(usrDeptName));
		agreement.setUsrEmailId(StringUtils.trimToEmpty(usrEmail));
		String finRef = detail.getFinScheduleData().getFinanceMain().getFinReference();
		int formatter = CurrencyUtil.getFormat(detail.getFinScheduleData().getFinanceMain().getFinCcy());
		String mMAReference = detail.getFinScheduleData().getFinanceMain().getLovDescMMAReference();
		agreement.setLpoDate(appDate);
		totalIncome=BigDecimal.ZERO;
		totalExpense=BigDecimal.ZERO;

		
		try {

			// ------------------ Customer Details
			if (aggModuleDetails.contains(PennantConstants.AGG_BASICDE)) {

				if (detail.getCustomerDetails() != null && detail.getCustomerDetails().getCustomer() != null) {
					Customer customer = detail.getCustomerDetails().getCustomer();
					agreement.setBranchDesc(StringUtils.trimToEmpty(customer.getLovDescCustDftBranchName()));
					agreement.setCustId(customer.getCustID());
					agreement.setCustCIF(StringUtils.trimToEmpty(customer.getCustCIF()));
					agreement.setCustName(StringUtils.trimToEmpty(customer.getCustShrtName()));
					agreement.setCustArabicName(StringUtils.trimToEmpty(customer.getCustShrtNameLclLng()));
					agreement.setCustPassport(StringUtils.trimToEmpty(customer.getCustPassportNo()));
					agreement.setCustDOB(DateUtility.formatToLongDate(customer.getCustDOB()));
					agreement.setCustRO1(StringUtils.trimToEmpty(customer.getLovDescCustRO1Name()));
					agreement.setCustNationality(StringUtils.trimToEmpty(customer.getLovDescCustNationalityName()));
					agreement.setCustCPRNo(StringUtils.trimToEmpty(customer.getCustCRCPR()));
					agreement.setCustAge(String.valueOf(DateUtility.getYearsBetween(appldate, customer.getCustDOB())));
					agreement.setCustTotIncome(PennantApplicationUtil.amountFormate(customer.getCustTotalIncome(),
							formatter));
					totalIncome=customer.getCustTotalIncome();
					totalExpense=customer.getCustTotalExpense();

					agreement.setCustTotExpense(PennantApplicationUtil.amountFormate(customer.getCustTotalExpense(),
							formatter));
					agreement.setNoOfDependents(String.valueOf(customer.getNoOfDependents()));
					agreement.setCustSector(StringUtils.trimToEmpty(customer.getCustSector()));
					agreement.setCustSubSector(StringUtils.trimToEmpty(StringUtils.trimToEmpty(customer.getCustSubSector())));
					agreement.setCustSegment(StringUtils.trimToEmpty(customer.getCustSegment()));
					agreement.setCustIndustry(StringUtils.trimToEmpty(customer.getLovDescCustIndustryName()));
					agreement.setCustJointDOB(DateUtility.formatToLongDate(customer.getJointCustDob()));
					agreement.setCustJointName(StringUtils.trimToEmpty(customer.getJointCustName()));
					agreement.setCustSalutation(StringUtils.trimToEmpty(customer.getLovDescCustSalutationCodeName()));
					agreement.setCustGender(StringUtils.trimToEmpty(customer.getLovDescCustGenderCodeName()));
					agreement.setCustCtgCode(StringUtils.trimToEmpty(customer.getCustCtgCode()));
					//TODO:: Added as part of CAM
					agreement.setCustCategory(StringUtils.trimToEmpty(customer.getLovDescCustCtgCodeName()));
					agreement.setCustType(StringUtils.trimToEmpty(customer.getLovDescCustTypeCodeName()));
					agreement.setCustMaritalStatus(StringUtils.trimToEmpty(customer.getLovDescCustMaritalStsName()));
					agreement.setCustFatherName(StringUtils.trimToEmpty(customer.getCustMotherMaiden()));

					// Customer Employment Details
					if (detail.getCustomerDetails().getCustEmployeeDetail() != null) {
						CustEmployeeDetail empDetail = detail.getCustomerDetails().getCustEmployeeDetail();
						agreement.setCustEmpName(StringUtils.trimToEmpty(empDetail.getLovDescEmpName()));
						agreement.setCustYearsExp(String.valueOf(DateUtility.getYearsBetween(appldate,
								empDetail.getEmpFrom())));
						agreement.setCustEmpStartDate(DateUtility.formatToLongDate(empDetail.getEmpFrom()));
						agreement.setCustEmpProf(StringUtils.trimToEmpty(empDetail.getLovDescProfession()));
						agreement.setCustEmpStsDesc(StringUtils.trimToEmpty(empDetail.getLovDescEmpStatus()));
						agreement.setCustOccupation(StringUtils.trimToEmpty(empDetail.getLovDescEmpDesg()));
					}

					// Customer Phone Numbers
					/*if (detail.getCustomerDetails().getCustomerPhoneNumList() != null) {
						for (CustomerPhoneNumber phoneNumber : detail.getCustomerDetails().getCustomerPhoneNumList()) {
							if ("HOME".equals(phoneNumber.getPhoneTypeCode())) {
								agreement.setPhoneHome(phoneNumber.getPhoneCountryCode() + "-"
										+ phoneNumber.getPhoneAreaCode() + "-" + phoneNumber.getPhoneNumber());
							}
							if ("MOBILE".equals(phoneNumber.getPhoneTypeCode())) {
								agreement.setCustMobile(phoneNumber.getPhoneCountryCode() + "-"
										+ phoneNumber.getPhoneAreaCode() + "-" + phoneNumber.getPhoneNumber());
							}
							if ("FAX".equals(phoneNumber.getPhoneTypeCode())) {
								agreement.setCustFax(phoneNumber.getPhoneCountryCode() + "-"
										+ phoneNumber.getPhoneAreaCode() + "-" + phoneNumber.getPhoneNumber());
							}
						}
					}*/
					
					//Customer address
					List<CustomerAddres> addressList = detail.getCustomerDetails().getAddressList();
					setCustomerAddress(agreement, addressList);
				

					List<CustomerDocument> customerDocumentsList = detail.getCustomerDetails().getCustomerDocumentsList();
					if (customerDocumentsList != null
							&& !customerDocumentsList.isEmpty()) {
						
						//pan number
						agreement.setPanNumber(PennantApplicationUtil.getPanNumber(customerDocumentsList));
						
						String cusCtg = StringUtils.trimToEmpty(customer.getCustCtgCode());
						boolean corpCust = false;
						if (StringUtils.isNotEmpty(cusCtg) && !cusCtg.equals(PennantConstants.PFF_CUSTCTG_INDIV)) {
							corpCust = true;
						}

						for (CustomerDocument customerDocument : customerDocumentsList) {
							String docCategory = customerDocument.getCustDocCategory();
							if (corpCust) {
								// Trade License for Corporate Customer/ SME Customer
								if (StringUtils.equals(docCategory, PennantConstants.TRADELICENSE)) {
									agreement.setCustIdType(docCategory);
									agreement.setCustDocIdNum(customerDocument.getCustDocTitle());
									agreement.setCustDocExpDate(DateUtility.formatToLongDate(customerDocument
											.getCustDocExpDate()));
									break;
								}
							} else {
								// EID for Retail Customer
								if (StringUtils.equals(docCategory, PennantConstants.CPRCODE)) {
									agreement.setCustIdType(docCategory);
									agreement.setCustIdName(customerDocument.getCustDocName());
									agreement.setCustDocIdNum(customerDocument.getCustDocTitle());
									break;
								}
							}
						}
					}
				}
			}
			
			//Contact Details
			if(CollectionUtils.isEmpty(agreement.getContactDetails())){
				agreement.setContactDetails(new ArrayList<>());
			}
			if (aggModuleDetails.contains(PennantConstants.AGG_CONTACT)
					&& detail.getCustomerDetails().getCustomerPhoneNumList() != null) {
				populateContactDetails(detail, agreement);
			}
			if(CollectionUtils.isEmpty(agreement.getContactDetails())){
				agreement.getContactDetails().add(agreement.new ContactDetail());
			}
			
			//Email Details
			if(CollectionUtils.isEmpty(agreement.getEmailDetails())){
				agreement.setEmailDetails(new ArrayList<>());
			}
			if (aggModuleDetails.contains(PennantConstants.AGG_CONTACT)
					&& CollectionUtils.isNotEmpty(detail.getCustomerDetails().getCustomerEMailList())) {
				int highPriorty=0;
				for (CustomerEMail email : detail.getCustomerDetails().getCustomerEMailList()) {
					if(highPriorty<email.getCustEMailPriority()){
						agreement.setCustEmail(StringUtils.trimToEmpty(email.getCustEMail()));
						highPriorty=email.getCustEMailPriority();
					}
					EmailDetail emailDetails=agreement.new EmailDetail();
					emailDetails.setEmailType(StringUtils.trimToEmpty(email.getLovDescCustEMailTypeCode()));
					emailDetails.setEmailValue(StringUtils.trimToEmpty(email.getCustEMail()));
					agreement.getEmailDetails().add(emailDetails);
				}
			}
			if(CollectionUtils.isEmpty(agreement.getEmailDetails())){
				agreement.getEmailDetails().add(agreement.new EmailDetail());
			}
			
			//Populate Employee Experience Details
			if (null!=detail.getCustomerDetails() && CollectionUtils.isNotEmpty(detail.getCustomerDetails().getEmploymentDetailsList())) {
				setCustomerEmpDetails(detail, agreement);
			}
			
			//Populate Internal Liabilities
			if(CollectionUtils.isEmpty(agreement.getInternalLiabilityDetails())){
				agreement.setInternalLiabilityDetails(new ArrayList<>());
			}
			
			if (aggModuleDetails.contains(PennantConstants.AGG_LIABILI) && null != detail.getCustomerDetails()
					&& CollectionUtils.isNotEmpty(detail.getCustomerDetails().getCustFinanceExposureList())) {
				List<FinanceEnquiry> custFinanceExposureList = detail.getCustomerDetails().getCustFinanceExposureList();
				for(FinanceEnquiry financeEnquiry:custFinanceExposureList){
					if(null!=financeEnquiry){
						InternalLiabilityDetail internalLiabilityDetail=agreement.new InternalLiabilityDetail();
						
						internalLiabilityDetail.setAppType("Primary Applicant");
						if(null!=detail.getCustomerDetails() && null!=detail.getCustomerDetails().getCustomer()){
							Customer customer=detail.getCustomerDetails().getCustomer();
							internalLiabilityDetail.setCustCIF(StringUtils.trimToEmpty(customer.getCustCIF()));
							internalLiabilityDetail.setCustName(StringUtils.trimToEmpty(customer.getCustShrtName()));
						}
						internalLiabilityDetail.setEmiAmt(PennantAppUtil.amountFormate(financeEnquiry.getMaxInstAmount(), formatter));
						internalLiabilityDetail.setLanNumber(StringUtils.trimToEmpty(financeEnquiry.getFinReference()));
						internalLiabilityDetail.setAmt(PennantAppUtil.amountFormate(financeEnquiry.getFinAmount(), formatter));
						internalLiabilityDetail.setStatus(StringUtils.trimToEmpty(financeEnquiry.getFinStatus()));
						internalLiabilityDetail.setBalTerms(Integer.toString(financeEnquiry.getNumberOfTerms()));
						agreement.getInternalLiabilityDetails().add(internalLiabilityDetail);
					}
				}
			}
			
			if(CollectionUtils.isEmpty(agreement.getExtendedDetails())){
				agreement.setExtendedDetails(new ArrayList<>());
			}
			
			if (aggModuleDetails.contains(PennantConstants.AGG_EXTENDE) && null != detail.getCustomerDetails()
					&& null != detail.getCustomerDetails().getExtendedFieldRender()
					&& MapUtils.isNotEmpty(detail.getCustomerDetails().getExtendedFieldRender().getMapValues())) {
				agreement=populateCustomerExtendedDetails(detail, agreement);
			}
			
			//Populate External Liabilities
			if(CollectionUtils.isEmpty(agreement.getExternalLiabilityDetails())){
				agreement.setExternalLiabilityDetails(new ArrayList<>());
			}
			
			if (aggModuleDetails.contains(PennantConstants.AGG_LIABILI) && null != detail.getCustomerDetails()
					&& CollectionUtils.isNotEmpty(detail.getCustomerDetails().getCustomerExtLiabilityList())) {
				
				List<CustomerExtLiability> customerExtLiabilityList = detail.getCustomerDetails().getCustomerExtLiabilityList();
				for(CustomerExtLiability extLiability:customerExtLiabilityList){
					if(null!=extLiability){
						ExternalLiabilityDetail externalLiabilityDetail=agreement.new ExternalLiabilityDetail();
						externalLiabilityDetail.setAppType("Primary Applicant");
						if(null!=detail.getCustomerDetails() && null!=detail.getCustomerDetails().getCustomer()){
							Customer customer=detail.getCustomerDetails().getCustomer();
							externalLiabilityDetail.setCustCIF(StringUtils.trimToEmpty(customer.getCustCIF()));
							externalLiabilityDetail.setCustName(StringUtils.trimToEmpty(customer.getCustShrtName()));
						}
						externalLiabilityDetail.setEmiAmt(PennantAppUtil.amountFormate(extLiability.getInstalmentAmount(), formatter));
						externalLiabilityDetail.setFinInstName(StringUtils.trimToEmpty(extLiability.getLoanBank()));
						externalLiabilityDetail.setFinBranchName(extLiability.getLoanBankName());
						externalLiabilityDetail.setAmt(PennantAppUtil.amountFormate(extLiability.getOriginalAmount(), formatter));
						externalLiabilityDetail.setOutStandingAmt(PennantAppUtil.amountFormate(extLiability.getOutstandingBalance(), formatter));
						externalLiabilityDetail.setLoanDate(DateUtility.formatToLongDate(extLiability.getFinDate()));
						externalLiabilityDetail.setStatus(StringUtils.trimToEmpty(extLiability.getCustStatusDesc()));
						agreement.getExternalLiabilityDetails().add(externalLiabilityDetail);
					}
				}
			}
			
			//Populate Workflow details
			//detail.getFinScheduleData().getFinanceMain().getWorkflowId()
			if(CollectionUtils.isEmpty(agreement.getActivityDetails())){
				agreement.setActivityDetails(new ArrayList<>());
			}
			if (null != detail.getFinScheduleData() && null != detail.getFinScheduleData().getFinanceMain()
					&& null != detail.getFinScheduleData().getFinanceMain().getFinReference()) {

				String loanRef = detail.getFinScheduleData().getFinanceMain().getFinReference();
				List<Activity> activities = getActivityLogService().getActivities("FinanceMain", loanRef);
				if (aggModuleDetails.contains(PennantConstants.AGG_ACTIVIT)&&CollectionUtils.isNotEmpty(activities)) {
					for (Activity activity : activities) {
						if (null != activity) {
							ActivityDetail activityDetail = agreement.new ActivityDetail();
							
							activityDetail.setRole(StringUtils.trimToEmpty(activity.getRoleCode()));
							activityDetail.setActivityDate(DateUtility.formatToLongDate(activity.getAuditDate()));
							if(activity.getWorkflowId()>0){
								WorkFlowDetails workflow = WorkFlowUtil.getWorkflow(activity.getWorkflowId());
								if(null!=workflow){
									activityDetail.setWorkflow(StringUtils.trimToEmpty(workflow.getWorkFlowDesc()));
								}
							}
							activityDetail.setActivity(StringUtils.trimToEmpty(activity.getRecordStatus()));
							activityDetail.setActivityUser(StringUtils.trimToEmpty(activity.getUserLogin()));
							agreement.getActivityDetails().add(activityDetail);
						}
					}
				}
			}
			if(CollectionUtils.isEmpty(agreement.getActivityDetails())){
				agreement.setActivityDetails(new ArrayList<>());
				agreement.getActivityDetails().add(agreement.new ActivityDetail());
			}
			
			//Extended Details
			if(CollectionUtils.isEmpty(agreement.getExtendedDetails())){
				agreement.setExtendedDetails(new ArrayList<>());
			}
			if (aggModuleDetails.contains(PennantConstants.AGG_EXTENDE) && null != detail.getExtendedFieldRender()
					&& MapUtils.isNotEmpty(detail.getExtendedFieldRender().getMapValues())) {
				agreement = populateExtendedDetails(detail, agreement);
			}
			
			//Populate the deviations
			if(CollectionUtils.isEmpty(agreement.getLoanDeviations())){
				agreement.setLoanDeviations(new ArrayList<>());
			}
			
			//Existing Customer
			if (null!=detail.getCustomerDetails() && null!=detail.getCustomerDetails().getCustomer()){
				String custCoreBank = detail.getCustomerDetails().getCustomer().getCustCoreBank();
				agreement.setExistingCustomer((StringUtils.isNotBlank(custCoreBank))?"YES":"NO");
			}
			
			if(aggModuleDetails.contains(PennantConstants.AGG_DEVIATI)){
				List<FinanceDeviations> financeDeviations = detail.getFinanceDeviations();
				setDeviationDetails(agreement, financeDeviations);
				List<FinanceDeviations> manualDeviations = detail.getManualDeviations();
				setDeviationDetails(agreement, manualDeviations);
				List<FinanceDeviations> approvedFinanceDeviations = detail.getApprovedFinanceDeviations();
				setDeviationDetails(agreement, approvedFinanceDeviations);
				List<FinanceDeviations> approvedManualDeviations = detail.getApprovedManualDeviations();
				setDeviationDetails(agreement, approvedManualDeviations);
			}
			
			if(CollectionUtils.isEmpty(agreement.getLoanDeviations())){
				agreement.setLoanDeviations(new ArrayList<>());
				agreement.getLoanDeviations().add(agreement.new LoanDeviation());
			}
			
			//Populate Applicant, CoApplicant Income, Expenses & Banking Details
			if(CollectionUtils.isEmpty(agreement.getAppIncDetails())){
				agreement.setAppIncDetails(new ArrayList<>());
			}
			if(CollectionUtils.isEmpty(agreement.getAppExpDetails())){
				agreement.setAppExpDetails(new ArrayList<>());
			}
			if(CollectionUtils.isEmpty(agreement.getBankingDetails())){
				agreement.setBankingDetails(new ArrayList<>());
			}
			if (aggModuleDetails.contains(PennantConstants.AGG_INCOMDE) && null != detail.getCustomerDetails()
					&& CollectionUtils.isNotEmpty(detail.getCustomerDetails().getCustomerIncomeList())) {
				List<CustomerIncome> customerIncomeList = detail.getCustomerDetails().getCustomerIncomeList();
				populateAppIncExpDetails(customerIncomeList, agreement, formatter, "Primary Applicant");
			}
			
			if (detail.getJountAccountDetailList()!=null && !detail.getJountAccountDetailList().isEmpty()) {
				for (JointAccountDetail jointAccountDetail : detail.getJountAccountDetailList()) {
					CustomerDetails custdetails = customerDetailsService.getCustomerDetailsById(jointAccountDetail.getCustID(), true, "_AView");
					if(aggModuleDetails.contains(PennantConstants.AGG_INCOMDE)&&null!=custdetails&&CollectionUtils.isNotEmpty(custdetails.getCustomerIncomeList())){
						populateAppIncExpDetails(custdetails.getCustomerIncomeList(), agreement, formatter, "Co-Applicant");
					}
					if (aggModuleDetails.contains(PennantConstants.AGG_BANKING) && null != custdetails
							&& CollectionUtils.isNotEmpty(custdetails.getCustomerBankInfoList())) {
						populateBankingDetails(agreement, formatter, "Co-Applicant", custdetails);
					}
					
					if(aggModuleDetails.contains(PennantConstants.AGG_LIABILI)){
						populateInternalLiabilities(detail, agreement, formatter, custdetails, "Co-Applicant");
						
						List<CustomerExtLiability> customerExtLiabilityList = custdetails.getCustomerExtLiabilityList();
						for(CustomerExtLiability extLiability:customerExtLiabilityList){
							if(null!=extLiability){
								ExternalLiabilityDetail externalLiabilityDetail=agreement.new ExternalLiabilityDetail();
								externalLiabilityDetail.setAppType("Co-Applicant");
								if(null!=custdetails.getCustomer()){
									Customer customer=custdetails.getCustomer();
									externalLiabilityDetail.setCustCIF(StringUtils.trimToEmpty(customer.getCustCIF()));
									externalLiabilityDetail.setCustName(StringUtils.trimToEmpty(customer.getCustShrtName()));
								}
								externalLiabilityDetail.setEmiAmt(PennantAppUtil.amountFormate(extLiability.getInstalmentAmount(), formatter));
								externalLiabilityDetail.setFinInstName(StringUtils.trimToEmpty(extLiability.getLoanBank()));
								externalLiabilityDetail.setOutStandingAmt(PennantAppUtil.amountFormate(extLiability.getOutstandingBalance(), formatter));
								externalLiabilityDetail.setLoanDate(DateUtility.formatToLongDate(extLiability.getFinDate()));
								externalLiabilityDetail.setAmt(PennantAppUtil.amountFormate(extLiability.getOriginalAmount(), formatter));
								externalLiabilityDetail.setStatus(StringUtils.trimToEmpty(extLiability.getCustStatusDesc()));
								agreement.getExternalLiabilityDetails().add(externalLiabilityDetail);
							}
						}
					}
				}
			}
			
			agreement.setTotalIncome(PennantAppUtil.amountFormate(totalIncome, formatter));
			agreement.setTotalExpense(PennantAppUtil.amountFormate(totalExpense, formatter));
			
			if(aggModuleDetails.contains(PennantConstants.AGG_BANKING)){
				if (null!=detail.getCustomerDetails() && CollectionUtils.isNotEmpty(detail.getCustomerDetails().getCustomerBankInfoList())) {
					populateBankingDetails(agreement, formatter, "Primary Applicant", detail.getCustomerDetails());
				}
			}
			
			//Size Checks
			//InternalLiability
			if(CollectionUtils.isEmpty(agreement.getInternalLiabilityDetails())){
				agreement.getInternalLiabilityDetails().add(agreement.new InternalLiabilityDetail());
			}
			//ExternalLiability
			if(CollectionUtils.isEmpty(agreement.getExternalLiabilityDetails())){
				agreement.getExternalLiabilityDetails().add(agreement.new ExternalLiabilityDetail());
			}
			//AppInc
			if(CollectionUtils.isEmpty(agreement.getAppIncDetails())){
				agreement.getAppIncDetails().add(agreement.new AppIncDetail());
			}
			//AppExp
			if(CollectionUtils.isEmpty(agreement.getAppExpDetails())){
				agreement.getAppExpDetails().add(agreement.new AppExpDetail());
			}
			//Banking
			if(CollectionUtils.isEmpty(agreement.getBankingDetails())){
				agreement.getBankingDetails().add(agreement.new BankingDetail());
			}
			
			//Setting Sourcing Details
			if(aggModuleDetails.contains(PennantConstants.AGG_SOURCIN)){
				if(CollectionUtils.isEmpty(agreement.getSourcingDetails())){
					agreement.setSourcingDetails(new ArrayList<>());
				}
				if (null!=detail.getFinScheduleData() && null!=detail.getFinScheduleData().getFinanceMain()) {
					SourcingDetail sourcingDetail=agreement.new SourcingDetail();
					FinanceMain financeMain2=detail.getFinScheduleData().getFinanceMain();
					sourcingDetail.setDsaName(StringUtils.trimToEmpty(financeMain2.getDsaCode()));
					sourcingDetail.setDmaCodeDesc(StringUtils.trimToEmpty(financeMain2.getDmaCodeDesc()));
					sourcingDetail.setSourceChannel(StringUtils.trimToEmpty(financeMain2.getSalesDepartmentDesc()));
					sourcingDetail.setSalesManager(StringUtils.trimToEmpty(financeMain2.getReferralId()));
					agreement.getSourcingDetails().add(sourcingDetail);
				}
			}
			if(CollectionUtils.isEmpty(agreement.getSourcingDetails())){
				agreement.setSourcingDetails(new ArrayList<>());
				agreement.getSourcingDetails().add(agreement.new SourcingDetail());
			}
			//-------------- Setting IRR Details
			if(aggModuleDetails.contains(PennantConstants.AGG_IRRDTLS)){
				if(CollectionUtils.isEmpty(agreement.getIrrDetails())){
					agreement.setIrrDetails(new ArrayList<>());
				}
				
				if(null!=detail.getFinScheduleData()&&CollectionUtils.isNotEmpty(detail.getFinScheduleData().getiRRDetails())){
					setIrrDetails(detail, agreement);
				}
			}
			if(CollectionUtils.isEmpty(agreement.getIrrDetails())){
				agreement.setIrrDetails(new ArrayList<>());
				agreement.getIrrDetails().add(agreement.new IrrDetail());
			}
			
			//TODO:: Moratorium
			if(null!=detail.getFinScheduleData()&&null!=detail.getFinScheduleData().getFinanceType()){
				String graceAvailable=(detail.getFinScheduleData().getFinanceType().isFInIsAlwGrace())?"Yes":"No";
				agreement.setGraceAvailable(graceAvailable);
			}
			
			if(null!=detail.getFinScheduleData()&&null!=detail.getFinScheduleData().getFinanceMain()){
				agreement.setNumOfPayGrace(Integer.toString(detail.getFinScheduleData().getFinanceMain().getGraceTerms()));
				agreement.setFirstDisbursementAmt(PennantAppUtil.amountFormate(detail.getFinScheduleData().getFinanceMain().getFinAmount(), formatter));
				if(null!=detail.getFinScheduleData().getFinanceMain().getRepaySpecialRate()){
					agreement.setRepaySplRate(StringUtils.trimToEmpty(detail.getFinScheduleData().getFinanceMain().getRepaySpecialRate()));
				}
				if(null!=detail.getFinScheduleData().getFinanceMain().getRepayMargin()){
					agreement.setRepayMargin(PennantApplicationUtil.formatRate(
							detail.getFinScheduleData().getFinanceMain().getRepayMargin().doubleValue(), 2));
				}
			}
			
			// ----------------Loan Details
			if(null!=detail.getFinScheduleData() && null!=detail.getFinScheduleData().getFinanceScheduleDetails()
					&& CollectionUtils.isNotEmpty(detail.getFinScheduleData().getFinanceScheduleDetails())
					&& null!=detail.getFinScheduleData().getFinanceScheduleDetails().get(0)
					&&null!=detail.getFinScheduleData().getFinanceScheduleDetails().get(0).getCalculatedRate()){
				agreement.setEffDateFltRate(PennantApplicationUtil.formatRate(
						detail.getFinScheduleData().getFinanceScheduleDetails().get(0).getCalculatedRate().doubleValue(), 2));
			}
			
			
			// ----------------Customer Repayment Details
			Mandate mandate = detail.getMandate();
			if(aggModuleDetails.contains(PennantConstants.AGG_REPAYDT)&&null!=mandate){
				setRepaymentDetails(agreement, mandate);
			}
			
			// ----------------Customer Charges Details
			if(aggModuleDetails.contains(PennantConstants.AGG_CHRGDET)){
				if(CollectionUtils.isEmpty(agreement.getCusCharges())){
					agreement.setCusCharges(new ArrayList<>());
				}
				if(null!=detail.getFinScheduleData()&&CollectionUtils.isNotEmpty(detail.getFinScheduleData().getFinFeeDetailList())){
					setFeeChargeDetails(agreement, formatter, detail.getFinScheduleData().getFinFeeDetailList());
				}
			}
			if(CollectionUtils.isEmpty(agreement.getCusCharges())){
				agreement.setCusCharges(new ArrayList<>());
				agreement.getCusCharges().add(agreement.new CusCharge());
			}
			
			// ----------------Disbursement Details
			if(aggModuleDetails.contains(PennantConstants.AGG_DISBURS)){
				if(CollectionUtils.isEmpty(agreement.getDisbursements())){
					agreement.setDisbursements(new ArrayList<>());
				}
				List<FinAdvancePayments> advancePaymentsList = detail.getAdvancePaymentsList();
				if(CollectionUtils.isNotEmpty(advancePaymentsList)){
					setDisbursementDetails(agreement, advancePaymentsList,formatter);
				}
			}
			if(CollectionUtils.isEmpty(agreement.getDisbursements())){
				agreement.setDisbursements(new ArrayList<>());
				agreement.getDisbursements().add(agreement.new Disbursement());
			}
			
			if(CollectionUtils.isEmpty(documentTypeList)){
				JdbcSearchObject<DocumentType> searchObject = new JdbcSearchObject<DocumentType>(DocumentType.class);
				 searchObject.addSortAsc("doctypecode");
				 documentTypeList = searchProcessor.getResults(searchObject);
			}
			
			// ----------------Document Details
			if(aggModuleDetails.contains(PennantConstants.AGG_DOCDTLS)){
				if(CollectionUtils.isEmpty(agreement.getDocuments())){
					agreement.setDocuments(new ArrayList<>());
				}
				List<DocumentDetails> documentDetailsList = detail.getDocumentDetailsList();
				if(CollectionUtils.isNotEmpty(documentDetailsList)){
					setLoanDocuments(agreement, documentDetailsList);
				}
				if(null!=detail.getCustomerDetails()){
					List<CustomerDocument> customerDocuments=detail.getCustomerDetails().getCustomerDocumentsList();
					if(CollectionUtils.isNotEmpty(customerDocuments)){
						setCustomerDocuments(agreement, customerDocuments);
					}
				}
			}
			if(CollectionUtils.isEmpty(agreement.getDocuments())){
				agreement.setDocuments(new ArrayList<>());
				agreement.getDocuments().add(agreement.new Document());
			}
			
			//------------------Covenants Details
			if(CollectionUtils.isEmpty(agreement.getCovenants())){
				agreement.setCovenants(new ArrayList<>());
			}
			List<FinCovenantType> covenantTypeList = detail.getCovenantTypeList();
			if(aggModuleDetails.contains(PennantConstants.AGG_COVENAN)&&CollectionUtils.isNotEmpty(covenantTypeList)){
				setCovenantDetails(agreement,covenantTypeList);
			}
			if(CollectionUtils.isEmpty(agreement.getCovenants())){
				agreement.getCovenants().add(agreement.new Covenant());
			}
			
			//TODO:: Need to get collateral Setup, Need details from collateral setup team.
			// --------------------Collateral Details
			if(CollectionUtils.isEmpty(agreement.getCollateralData())){
				agreement.setCollateralData(new ArrayList<AgreementDetail.FinCollaterals>());
			}
			if (aggModuleDetails.contains(PennantConstants.AGG_COLLTRL)&&CollectionUtils.isNotEmpty(detail.getCollateralAssignmentList())) {
				agreement = getCollateralDetails(agreement, detail, formatter);
			}
			if(CollectionUtils.isEmpty(agreement.getCollateralData())){
				agreement.getCollateralData().add(agreement.new FinCollaterals());
			}
			
			//VAS Details
			
			if (CollectionUtils.isEmpty(agreement.getVasData())) {
				agreement.setVasData(new ArrayList<AgreementDetail.VasDetails>());
			}
			
			if (aggModuleDetails.contains(PennantConstants.AGG_VAS)&&CollectionUtils.isNotEmpty(detail.getFinScheduleData().getVasRecordingList())) {
			getVasRecordingDetails(agreement,detail, formatter);
			}
			if (CollectionUtils.isEmpty(agreement.getVasData())) {
				agreement.getVasData().add(agreement.new VasDetails());
			}
			
			if(null!=detail&&null!=detail.getFinScheduleData()&&null!=detail.getFinScheduleData().getFinanceMain()){
				agreement.setConnectorCode(StringUtils.trimToEmpty(detail.getFinScheduleData().getFinanceMain().getConnectorCode()));
			}
			
			//Director Details
			if (CollectionUtils.isEmpty(agreement.getDirectorDetails())) {
				agreement.setDirectorDetails(new ArrayList<AgreementDetail.DirectorDetail>());
			}
			//TODO: removing aggModuleDetails.contains(PennantConstants.AGG_DIRECDT) to resolve uat issue need to add this once Agreements Autogeneration issue Reslove
			if ( CollectionUtils.isNotEmpty(detail.getCustomerDetails().getCustomerDirectorList())) {
			agreement = getDirectorDetails(agreement, detail, formatter);
			}
			
			if (CollectionUtils.isEmpty(agreement.getDirectorDetails())) {
				agreement.getDirectorDetails().add(agreement.new DirectorDetail());
			}

			if (detail.getFinRepayHeader() != null) {
				agreement = getFinRepayHeaderDetails(agreement, detail.getFinRepayHeader(), formatter);
			}

			if (detail.getAgreementFieldDetails() != null) {
				agreement = getAgreementArabicFieldDetails(agreement, detail.getAgreementFieldDetails(), formatter);
			}


			// -----------------Customer Credit Review Details
			
			if(CollectionUtils.isEmpty(agreement.getCrdRevElgSummaries())){
				agreement.setCrdRevElgSummaries(new ArrayList<>());
			}
			
			if (aggModuleDetails.contains(PennantConstants.AGG_CRDTRVW)) {
				if (null != detail.getFinScheduleData() && null != detail.getFinScheduleData().getFinanceMain()) {
					agreement.setEligibilityMethod(StringUtils
							.trimToEmpty(detail.getFinScheduleData().getFinanceMain().getLovDescEligibilityMethod()));
				}
				if (null != detail.getCustomerDetails()) {
					String maxAuditYear = creditApplicationReviewService
							.getMaxAuditYearByCustomerId(detail.getCustomerDetails().getCustID(), "_VIEW");
					int toYear = 0;
					if (StringUtils.isNotBlank(maxAuditYear)) {
						toYear = Integer.parseInt(maxAuditYear);
					}
					List<FinCreditRevCategory> creditRevCategories = creditApplicationReviewService
							.getCreditRevCategoryByCreditRevCode(
									detail.getCustomerDetails().getCustomer().getCustCtgCode());
					long categoryId = 0;
					if (CollectionUtils.isNotEmpty(creditRevCategories)) {
						for (FinCreditRevCategory finCreditRevCategory : creditRevCategories) {
							if ("Eligibility Summary".contains(finCreditRevCategory.getCategoryDesc())) {
								categoryId = finCreditRevCategory.getCategoryId();
								break;
							}
						}
					}

					List<FinCreditRevSubCategory> creditRevSubCategories = creditApplicationReviewService
							.getFinCreditRevSubCategoryByCategoryId(categoryId);
					if (CollectionUtils.isNotEmpty(creditRevSubCategories)) {
						for (FinCreditRevSubCategory finCreditRevSubCategory : creditRevSubCategories) {
							CreditReviewEligibilitySummary reviewEligibilitySummary = agreement.new CreditReviewEligibilitySummary();
							reviewEligibilitySummary.setSubCategoryCode(finCreditRevSubCategory.getSubCategoryCode());
							reviewEligibilitySummary.setSubCategoryDesc(finCreditRevSubCategory.getSubCategoryDesc());
							reviewEligibilitySummary.setY0Amount("0.00");
							reviewEligibilitySummary.setY1Amount("0.00");
							reviewEligibilitySummary.setY2Amount("0.00");
							agreement.getCrdRevElgSummaries().add(reviewEligibilitySummary);
						}
					}
					if (toYear > 0 && MapUtils.isNotEmpty(detail.getDataMap())
							&& CollectionUtils.isNotEmpty(agreement.getCrdRevElgSummaries())) {
						Map<String, String> dataMap = detail.getDataMap();
						for (FinCreditRevSubCategory finCreditRevSubCategory : creditRevSubCategories) {
							for (String subCategory : dataMap.keySet()) {
								if (null != finCreditRevSubCategory
										&& subCategory.contains(finCreditRevSubCategory.getSubCategoryCode())) {
									for (CreditReviewEligibilitySummary reviewEligibilitySummary : agreement
											.getCrdRevElgSummaries()) {
										if (finCreditRevSubCategory.getSubCategoryCode()
												.equals(reviewEligibilitySummary.getSubCategoryCode())) {
											if (subCategory.startsWith("Y0")) {
												reviewEligibilitySummary
														.setY0Amount(formateAmount(dataMap.get(subCategory), 2));
											} else if (subCategory.startsWith("Y1")) {
												reviewEligibilitySummary
														.setY1Amount(formateAmount(dataMap.get(subCategory), 2));
											} else if (subCategory.startsWith("Y2")) {
												reviewEligibilitySummary
														.setY2Amount(formateAmount(dataMap.get(subCategory), 2));
											}
										}
									}
								}
							}
						}
					}
				}
				
			}
			
			if(CollectionUtils.isEmpty(agreement.getCrdRevElgSummaries())){
				agreement.getCrdRevElgSummaries().add(agreement.new CreditReviewEligibilitySummary());
			}

			// -----------------Scoring Detail
			if (aggModuleDetails.contains(PennantConstants.AGG_SCOREDE)) {
				populateAgreementScoringDetails(detail, agreement);				
			}
			
			if(CollectionUtils.isEmpty(agreement.getScoringDetails())){
				agreement.setScoringDetails(new ArrayList<>());
				agreement.getScoringDetails().add(agreement.new Score());
			}
			
			// -----------------Eligibility Detail
			if (aggModuleDetails.contains(PennantConstants.AGG_ELGBLTY)
					&& CollectionUtils.isNotEmpty(detail.getElgRuleList())) {
				if(CollectionUtils.isEmpty(agreement.getEligibilityList())){
					agreement.setEligibilityList(new ArrayList<>());
				}
				for (FinanceEligibilityDetail eligibility : detail.getElgRuleList()) {
		            Eligibility elg = agreement.new Eligibility();
		            elg.setRuleCode(StringUtils.trimToEmpty(eligibility.getLovDescElgRuleCode()));
		            elg.setDescription(StringUtils.trimToEmpty(eligibility.getLovDescElgRuleCodeDesc()));
		            
		            if(RuleConstants.RETURNTYPE_DECIMAL.equals(eligibility.getRuleResultType())){
		            	
						if("E".equals(eligibility.getRuleResult())){
							 elg.setEligibilityLimit(StringUtils.trimToEmpty(Labels.getLabel("common.InSuffData")));
						}else if(PennantStaticListUtil.getConstElgRules().contains(eligibility.getLovDescElgRuleCode())){
							String result = eligibility.getRuleResult();
							if(RuleConstants.ELGRULE_DSRCAL.equals(eligibility.getLovDescElgRuleCode()) ||
									RuleConstants.ELGRULE_PDDSRCAL.equals(eligibility.getLovDescElgRuleCode())){
								result = result.concat("%");
							}

							elg.setEligibilityLimit(StringUtils.trimToEmpty(result));
						}else{
							elg.setEligibilityLimit(PennantApplicationUtil.amountFormate(new BigDecimal(eligibility.getRuleResult()),
									formatter));
						}
					}else{
						if(eligibility.isEligible()){
							elg.setEligibilityLimit(Labels.getLabel("common.Eligible"));
						}else{
							elg.setEligibilityLimit(Labels.getLabel("common.Ineligible"));
						}					
					}
		           
		            agreement.getEligibilityList().add(elg);
	            }
			}
			
			if(CollectionUtils.isEmpty(agreement.getEligibilityList())){
				agreement.setEligibilityList(new ArrayList<>());
				agreement.getEligibilityList().add(agreement.new Eligibility());
			}
			
			// ----------------Finance Details
			if (aggModuleDetails.contains(PennantConstants.AGG_FNBASIC)) {
				agreement = getFinanceDetails(agreement, detail);
			}

			// -----------------Check List Details
			if (aggModuleDetails.contains(PennantConstants.AGG_CHKLSTD)) {
				agreement = getCheckListDetails(agreement, detail);
			}

			// -------------------Schedule Details
			if (aggModuleDetails.contains(PennantConstants.AGG_SCHEDLD)) {
				agreement = getScheduleDetails(agreement, detail, formatter);
			}

			// -------------------Recommendations and Notes
			if (aggModuleDetails.contains(PennantConstants.AGG_RECOMMD)) {
				agreement = getRecommendations(agreement, finRef);
			}
			if(CollectionUtils.isEmpty(agreement.getRecommendations())){
				agreement.setRecommendations(new ArrayList<AgreementDetail.Recommendation>());
				agreement.getRecommendations().add(agreement.new Recommendation());
			}

			// -------------------Exception List
			if (aggModuleDetails.contains(PennantConstants.AGG_EXCEPTN)) {
				agreement = getExceptionList(agreement, detail);
			}

			// -----------------Customer Finance Details
			if (aggModuleDetails.contains(PennantConstants.AGG_EXSTFIN)) {
				agreement = getCustomerFinanceDetails(agreement, formatter);
			}

			// -------------------Group Recommendations List
			//MMAgreement Details
			agreement = setGroupRecommendations(agreement, finRef);
			if (mMAReference != null && !StringUtils.equals(mMAReference, "")) {
				agreement = setMMAgreementGenDetails(agreement, appDate, formatter, mMAReference);
			}
			// --------------------Asset Evalution Details 
			if (detail.getFinAssetEvaluation() != null) {
				agreement = getFinAssetEvaluationDetails(agreement, detail.getFinAssetEvaluation(), formatter, detail
						.getFinScheduleData().getFinanceMain().getFinCcy());
			}
			// Co-applicant details
			setCoapplicantDetails(detail, agreement, aggModuleDetails);
			//Mandate details
			setMandateDetails(detail, agreement);
			//customer bank info
			setCustomerBankInfo(detail, agreement, aggModuleDetails);
			
			// Verification details
			if (aggModuleDetails.contains(PennantConstants.AGG_VERIFIC)) {
				if(CollectionUtils.isEmpty(agreement.getFiVerification())){
					agreement.setFiVerification(new ArrayList<>());
				}
				if(CollectionUtils.isEmpty(agreement.getLegalVerification())){
					agreement.setLegalVerification(new ArrayList<>());
				}
				if(CollectionUtils.isEmpty(agreement.getRcuVerification())){
					agreement.setRcuVerification(new ArrayList<>());
				}
				if(CollectionUtils.isEmpty(agreement.getTechnicalVerification())){
					agreement.setTechnicalVerification(new ArrayList<>());
				}
				
				populateVerificationDetails(agreement, finRef);
			}
			
			if(CollectionUtils.isEmpty(agreement.getFiVerification())){
				agreement.setFiVerification(new ArrayList<>());
				agreement.getFiVerification().add(agreement.new VerificationDetail());
			}
			if(CollectionUtils.isEmpty(agreement.getLegalVerification())){
				agreement.setLegalVerification(new ArrayList<>());
				agreement.getLegalVerification().add(agreement.new VerificationDetail());
			}
			if(CollectionUtils.isEmpty(agreement.getRcuVerification())){
				agreement.setRcuVerification(new ArrayList<>());
				agreement.getRcuVerification().add(agreement.new VerificationDetail());
			}
			if(CollectionUtils.isEmpty(agreement.getTechnicalVerification())){
				agreement.setTechnicalVerification(new ArrayList<>());
				agreement.getTechnicalVerification().add(agreement.new VerificationDetail());
			}
			
			if(CollectionUtils.isEmpty(agreement.getExtendedDetails())){
				agreement.setExtendedDetails(new ArrayList<>());
				agreement.getExtendedDetails().add(agreement.new ExtendedDetail());
			}
			
			if(CollectionUtils.isEmpty(agreement.getQueryDetails())){
				agreement.setQueryDetails(new ArrayList<>());
			}
			
			if (aggModuleDetails.contains(PennantConstants.AGG_QRYMODL)) {
				populateQueryDetails(agreement, finRef);
			}
			
			if(CollectionUtils.isEmpty(agreement.getQueryDetails())){
				agreement.setQueryDetails(new ArrayList<>());
				agreement.getQueryDetails().add(agreement.new LoanQryDetails());
			}

			Map<String, String> otherMap = new HashMap<>();
			if (CollectionUtils.isNotEmpty(agreement.getExtendedDetails())) {
				for (ExtendedDetail extendedDetail : agreement.getExtendedDetails()) {
					if (null != extendedDetail) {
						if (StringUtils.isNotBlank(extendedDetail.getKey())) {
							switch (extendedDetail.getFieldType()) {
							case "CUSTOMER":
								otherMap.put("CUST_EXT_" + extendedDetail.getKey().toUpperCase(), extendedDetail.getValue());
								break;

							case "COLLATERAL":
								otherMap.put("COLL_EXT_" + extendedDetail.getKey().toUpperCase(), extendedDetail.getValue());
								break;

							case "LOAN":
								otherMap.put("LOAN_EXT_" + extendedDetail.getKey().toUpperCase(), extendedDetail.getValue());
								break;
							}
							otherMap.put(extendedDetail.getKey(), extendedDetail.getValue());
						}
					}
				}
			}
			
			agreement.setOtherMap(otherMap);
			
			//Populate the PSL Details in Agreements
			if (aggModuleDetails.contains(PennantConstants.AGG_PSLMODL)) {
				logger.debug("Start of PSL Details in Agreements");
				PSLDetail pslDetail = pSLDetailService.getPSLDetail(finRef);
				if (null != pslDetail) {
					agreement.setPslAmount(
							PennantAppUtil.amountFormate(new BigDecimal(pslDetail.getAmount()), formatter));
					//categoryList
					PagedListService pagedListService = (PagedListService) SpringBeanUtil.getBean("pagedListService");
					JdbcSearchObject<PSLCategory> searchObject = new JdbcSearchObject<PSLCategory>(
							PSLCategory.class);
					searchObject.addTabelName("Pslcategory");

					List<PSLCategory> categoryList = pagedListService.getBySearchObject(searchObject);
					
					String pslCategoryCodeName = null;
					if (CollectionUtils.isNotEmpty(categoryList)
							&& StringUtils.isNotBlank(pslDetail.getCategoryCode())) {
						for (PSLCategory category : categoryList) {
							if (null != category && category.getCode().equalsIgnoreCase(pslDetail.getCategoryCode())) {
								pslCategoryCodeName = category.getDescription();
								break;
							}
						}
					}
					if (StringUtils.isNotBlank(pslCategoryCodeName)) {
						agreement.setPslCategoryCodeName(pslCategoryCodeName);
					}
					agreement.setPslEligibleAmount(
							PennantAppUtil.amountFormate(pslDetail.getEligibleAmount(), formatter));
					agreement.setPslEndUseName(StringUtils.trimToEmpty(pslDetail.getEndUseName()));
					//landAreaList;
					String pslLandAreaName = null;
					if (CollectionUtils.isNotEmpty(landAreaList) && StringUtils.isNotBlank(pslDetail.getLandArea())) {
						for (ValueLabel landArea : landAreaList) {
							if (null != landArea && landArea.getValue().equalsIgnoreCase(pslDetail.getLandArea())) {
								pslLandAreaName = landArea.getLabel();
								break;
							}
						}
					}
					if (StringUtils.isNotBlank(pslLandAreaName)) {
						agreement.setPslLandAreaName(StringUtils.trimToEmpty(pslLandAreaName));
					}
					//listLandHolding;
					String pslLandHolding = null;
					if (CollectionUtils.isNotEmpty(listLandHolding)
							&& StringUtils.isNotBlank(pslDetail.getLandHolding())) {
						for (ValueLabel landHolding : listLandHolding) {
							if (null != landHolding
									&& landHolding.getValue().equalsIgnoreCase(pslDetail.getLandHolding())) {
								pslLandHolding = landHolding.getLabel();
								break;
							}
						}
					}
					if (StringUtils.isNotBlank(pslLandHolding)) {
						agreement.setPslLandHoldingName(StringUtils.trimToEmpty(pslLandHolding));
					}
					agreement.setPslLoanPurposeName(StringUtils.trimToEmpty(pslDetail.getLoanPurposeName()));
					agreement.setPslPurposeName(StringUtils.trimToEmpty(pslDetail.getPurposeName()));
					//sectorList
					String pslSector = null;
					if (CollectionUtils.isNotEmpty(sectorList) && StringUtils.isNotBlank(pslDetail.getSector())) {
						for (ValueLabel sector : sectorList) {
							if (null != sector && sector.getValue().equalsIgnoreCase(pslDetail.getSector())) {
								pslSector = sector.getLabel();
								break;
							}
						}
					}
					if (StringUtils.isNotBlank(pslSector)) {
						agreement.setPslSectorName(StringUtils.trimToEmpty(pslSector));
					}
					//subCategoryList;
					String pslSubCategoryName = null;
					if (CollectionUtils.isNotEmpty(subCategoryList)
							&& StringUtils.isNotBlank(pslDetail.getSubCategory())) {
						for (ValueLabel subCategory : subCategoryList) {
							if (null != subCategory
									&& subCategory.getValue().equalsIgnoreCase(pslDetail.getSubCategory())) {
								pslSubCategoryName = subCategory.getLabel();
								break;
							}
						}
					}
					if (StringUtils.isNotBlank(pslSubCategoryName)) {
						agreement.setPslSubCategoryName(StringUtils.trimToEmpty(pslSubCategoryName));
					}
					agreement.setPslWeakerSectionName(StringUtils.trimToEmpty(pslDetail.getWeakerSectionName()));
				}
				logger.debug("End of PSL Details in Agreements");
			}
			
			if(aggModuleDetails.contains(PennantConstants.AGG_SMPMODL)){
				Sampling sampling = finSamplingService.getSamplingDetails(finRef, "_aview");
				
				if (sampling == null) {
					sampling = new Sampling();
				}
				
				List<SamplingDetail> samplingDetailsList = sampling.getSamplingDetailsList();
				if(CollectionUtils.isNotEmpty(samplingDetailsList)){
					agreement.setSmplDetails(samplingDetailsList);
				}else{
					agreement.getSmplDetails().add(new SamplingDetail());
				}
				agreement.setSmplTolerance(StringUtils.trimToEmpty(sampling.getSamplingTolerance()));
				agreement.setSmplDecision("");

				try {
					com.pennanttech.pff.sampling.Decision decision = com.pennanttech.pff.sampling.Decision
							.getType(sampling.getDecision());

					if (decision.getKey() != 0) {
						agreement.setSmplDecision(decision.getValue());
					}

				} catch (Exception e) {
				}
				agreement.setSmplTolerance(StringUtils.trimToEmpty(sampling.getSamplingTolerance()));
				agreement.setSmplRecommendedAmount(PennantAppUtil.amountFormate(sampling.getRecommendedAmount(), formatter));
				agreement.setSmplRemarks(StringUtils.trimToEmpty(sampling.getRemarks()));
				agreement.setSmplResubmitReasonDesc(StringUtils.trimToEmpty(sampling.getResubmitReasonDesc()));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		logger.debug("Leaving");
		return agreement;
	}
	
	public static String formateAmount(String amount, int dec) {
		BigDecimal returnAmount = BigDecimal.ZERO;
		if (amount != null) {
			BigDecimal tempAmount = new BigDecimal(amount);
			returnAmount = tempAmount.divide(BigDecimal.valueOf(Math.pow(10, dec)));
		}
		return PennantAppUtil.formatAmount(returnAmount, dec, false);
	}
	
	private AgreementDetail getDirectorDetails(AgreementDetail agreement, FinanceDetail detail, int formatter){
		
		List<DirectorDetail> customerDirectorList = detail.getCustomerDetails().getCustomerDirectorList();
		
		if (CollectionUtils.isNotEmpty(customerDirectorList)) {
			customerDirectorList.forEach(directorDetails->{
				com.pennant.backend.model.finance.AgreementDetail.DirectorDetail directorDetail=agreement.new DirectorDetail();
				directorDetail.setFirstName(StringUtils.trimToEmpty(directorDetails.getFirstName()));
				directorDetail.setLastName(StringUtils.trimToEmpty(directorDetails.getLastName()));
				directorDetail.setMiddleName(StringUtils.trimToEmpty(directorDetails.getMiddleName()));
				directorDetail.setCityName(StringUtils.trimToEmpty(directorDetails.getLovDescCustAddrCityName()));
				directorDetail.setCountryName(StringUtils.trimToEmpty(directorDetails.getLovDescCustAddrCountryName()));
				directorDetail.setProvinceName(StringUtils.trimToEmpty(directorDetails.getLovDescCustAddrProvinceName()));
				directorDetail.setDocCategoryName(StringUtils.trimToEmpty(directorDetails.getLovDescCustDocCategoryName()));
				directorDetail.setGender(StringUtils.trimToEmpty(directorDetails.getLovDescCustGenderCodeName()));
				directorDetail.setDesignationName(StringUtils.trimToEmpty(directorDetails.getLovDescDesignationName()));
				directorDetail.setNationalityName(StringUtils.trimToEmpty(directorDetails.getLovDescNationalityName()));
				directorDetail.setSalutationCodeName(StringUtils.trimToEmpty(directorDetails.getLovDescCustSalutationCodeName()));
				directorDetail.setShortName(StringUtils.trimToEmpty(directorDetails.getShortName()));
				directorDetail.setSharePerc(PennantApplicationUtil.formatRate(directorDetails.getSharePerc().doubleValue(),formatter));
				if(Boolean.TRUE == directorDetails.isDirector()){
					directorDetail.setDirector("Yes");
				}
				if(Boolean.TRUE ==directorDetails.isShareholder()){
					directorDetail.setShareholder("Yes");
				}
				agreement.getDirectorDetails().add(directorDetail);
			});
		}
		
		return agreement;
	}
	
	private AgreementDetail getVasRecordingDetails(AgreementDetail agreement, FinanceDetail detail, int formatter) {
		
		List<VASRecording> vasRecordingList = detail.getFinScheduleData().getVasRecordingList();
		if (CollectionUtils.isNotEmpty(vasRecordingList)) {
			vasRecordingList.forEach((vasDetails -> {
				if (null != vasDetails) {
					com.pennant.backend.model.finance.AgreementDetail.VasDetails vasData = agreement.new VasDetails();
					vasData.setFee(PennantAppUtil.amountFormate(vasDetails.getFee(), formatter));
					vasData.setFeeAccounting(String.valueOf(vasDetails.getFeeAccounting()));
					String feePaymentMode=vasDetails.getFeePaymentMode();
					if(StringUtils.isNotBlank(feePaymentMode)&&!StringUtils.equals(feePaymentMode, "#")){
						vasData.setFeePaymentMode(StringUtils.trimToEmpty(vasDetails.getFeePaymentMode()));
					}vasData.setManufacturerDesc(StringUtils.trimToEmpty(vasDetails.getManufacturerDesc()));
					vasData.setPostingAgainst(StringUtils.trimToEmpty(vasDetails.getPostingAgainst()));
					vasData.setProductCode(StringUtils.trimToEmpty(vasDetails.getProductCode()));
					vasData.setProductCtg(StringUtils.trimToEmpty(vasDetails.getProductCtg()));
					vasData.setProductCtgDesc(StringUtils.trimToEmpty(vasDetails.getProductCtgDesc()));
					vasData.setProductDesc(StringUtils.trimToEmpty(vasDetails.getProductDesc()));
					vasData.setProductType(StringUtils.trimToEmpty(vasDetails.getProductType()));
					vasData.setProductTypeDesc(StringUtils.trimToEmpty(vasDetails.getProductTypeDesc()));
					vasData.setRenewalFee(PennantAppUtil.amountFormate(vasDetails.getRenewalFee(), formatter));
					vasData.setValueDate(vasDetails.getValueDate().toString());
					vasData.setVasReference(StringUtils.trimToEmpty(vasDetails.getVasReference()));
					vasData.setVasStatus(StringUtils.trimToEmpty(vasDetails.getVasStatus()));
					vasData.setVasReference(StringUtils.trimToEmpty(vasDetails.getPrimaryLinkRef()));
					agreement.getVasData().add(vasData);
				}

			}));

		}
		
		return agreement;
		
	}

	/**
	 * Populate the query details associated to the loan by calling the queryDetailsservice by sending the loan reference.
	 * 
	 * @param agreement
	 * @param finRef
	 */
	private void populateQueryDetails(AgreementDetail agreement, String finRef) {
		List<QueryDetail> queryList = queryDetailService.getQueryDetailsforAgreements(finRef);
		if(CollectionUtils.isNotEmpty(queryList)){
			for (QueryDetail queryDetail : queryList) {
				if(null!=queryDetail){
					LoanQryDetails loanQryDetails=agreement.new LoanQryDetails();
					loanQryDetails.setRaisedBy(new StringBuilder().append(queryDetail.getRaisedBy()).append("-").append(queryDetail.getUsrLogin()).toString());
					loanQryDetails.setRaisedOn(DateUtil.formatToShortDate(queryDetail.getRaisedOn()));
					loanQryDetails.setCategory(new StringBuilder().append(queryDetail.getCategoryCode()).append("-").append(queryDetail.getCategoryDescription()).toString());
					loanQryDetails.setDescription(StringUtils.trimToEmpty(queryDetail.getQryNotes()));
					loanQryDetails.setStatus(StringUtils.trimToEmpty(queryDetail.getStatus()));
					agreement.getQueryDetails().add(loanQryDetails);
				}
			}
		}
	}

	/**
	 * Method to populate the verification details
	 * 
	 * @param agreement
	 * @param finRef
	 */
	private void populateVerificationDetails(AgreementDetail agreement, String finRef) {
		if(null!= finRef){
			List<Verification> verifications = verificationService.getVerificationsForAggrement(finRef);
			
			if (CollectionUtils.isNotEmpty(verifications)) {
				for (Verification verification : verifications) {
					if (null != verification) {
						VerificationType type = VerificationType
								.getVerificationType(verification.getVerificationType());
						VerificationDetail verificationData = agreement.new VerificationDetail();
						verificationData.setInitiationDate(DateUtility.formatToLongDate(verification.getCreatedOn()));
						verificationData
								.setCompletionDate(DateUtility.formatToLongDate(verification.getVerificationDate()));
						String initialStatus = (null != RequestType.getType(verification.getRequestType()))
								? RequestType.getType(verification.getRequestType()).getValue() : null;
						verificationData.setInitialStatus(StringUtils.trimToEmpty(initialStatus));
						verificationData
								.setRecommanditionStatus(StringUtils.trimToEmpty(verification.getVerificationStatus()));
						verificationData.setRemarks(StringUtils.trimToEmpty(verification.getDecisionRemarks()));
						String finalDecision = (null != Decision.getType(verification.getDecision())&&0!=verification.getDecision())
								? Decision.getType(verification.getDecision()).getValue() : null;
						verificationData.setFinalDecision(StringUtils.trimToEmpty(finalDecision));
						verificationData.setAgencyName(StringUtils.trimToEmpty(verification.getAgencyName()));

						switch (type) {
						case FI:
							if (3 == verification.getRequestType()) {
								break;
							}
							if (2 == verification.getRequestType()) {
								verificationData.setAddressType(StringUtils.trimToEmpty(verification.getReferenceFor()));
							}
							verificationData.setApplicantName(StringUtils.trimToEmpty(verification.getCustomerName()));
							if (null != verification.getFieldInvestigation()) {
								FieldInvestigation fieldInvestigation = verification.getFieldInvestigation();
								verificationData
										.setAddressType(StringUtils.trimToEmpty(fieldInvestigation.getAddressType()));
								verificationData.setDoneBy(
										StringUtils.trimToEmpty(fieldInvestigation.getAgentCode()).concat("-")
												.concat(StringUtils.trimToEmpty(fieldInvestigation.getAgentName())));
							}
							agreement.getFiVerification().add(verificationData);
							break;
						case TV:
							if (3 == verification.getRequestType()) {
								break;
							}
							if (2 == verification.getRequestType()) {
								verificationData.setCollateralType(StringUtils.trimToEmpty(verification.getReferenceFor()));
							}
							TechnicalVerification technicalVerification = verification.getTechnicalVerification();
							if (null != technicalVerification) {
								verificationData.setCollateralType(
										StringUtils.trimToEmpty(technicalVerification.getCollateralType()));
								verificationData.setCollateralReference(
										StringUtils.trimToEmpty(technicalVerification.getCollateralRef()));
								verificationData.setDoneBy(
										StringUtils.trimToEmpty(technicalVerification.getAgentCode()).concat("-")
												.concat(StringUtils.trimToEmpty(technicalVerification.getAgentName())));
							}
							agreement.getTechnicalVerification().add(verificationData);
							break;
						case LV:
							if (2 == verification.getRequestType()) {
								verificationData.setDocumentName(StringUtils.trimToEmpty(verification.getReferenceFor()));
							}
							LegalVerification legalVerification = verification.getLegalVerification();
							if (null != legalVerification) {
								verificationData.setDoneBy(StringUtils.trimToEmpty(legalVerification.getAgentCode())
										.concat("-").concat(StringUtils.trimToEmpty(legalVerification.getAgentName())));
							}
							agreement.getLegalVerification().add(verificationData);
							break;
						case RCU:
							if (3 == verification.getRequestType()) {
								break;
							}
							if (2 == verification.getRequestType()) {
								verificationData.setDocumentName(StringUtils.trimToEmpty(verification.getReferenceFor()));
								if(null!=verification.getReinitid()&&verification.getReinitid()>0){
									verificationData.setFinalDecision("Re-initiate");
								}
							}
							RiskContainmentUnit riskContainmentUnit = verification.getRcuVerification();
							if (null != riskContainmentUnit) {
								verificationData.setDoneBy(
										StringUtils.trimToEmpty(riskContainmentUnit.getAgentCode()).concat("-")
												.concat(StringUtils.trimToEmpty(riskContainmentUnit.getAgentName())));
							}
							if (null != riskContainmentUnit
									&& CollectionUtils.isNotEmpty(riskContainmentUnit.getRcuDocuments())) {
								VerificationDetail rcuVerificationData=null;
								for (RCUDocument rcuDocument : riskContainmentUnit.getRcuDocuments()) {
									if (null != rcuDocument) {
										rcuVerificationData=agreement.new VerificationDetail();
										rcuVerificationData.setInitiationDate(DateUtility.formatToLongDate(verification.getCreatedOn()));
										rcuVerificationData
												.setCompletionDate(DateUtility.formatToLongDate(verification.getVerificationDate()));
										rcuVerificationData.setInitialStatus(StringUtils.trimToEmpty(initialStatus));
										rcuVerificationData
												.setRecommanditionStatus(StringUtils.trimToEmpty(verification.getVerificationStatus()));
										rcuVerificationData.setRemarks(StringUtils.trimToEmpty(verification.getDecisionRemarks()));
										rcuVerificationData.setAgencyName(StringUtils.trimToEmpty(verification.getAgencyName()));
										String rcuDocVerficationType = null;
										if (rcuDocument.getVerificationType() == 0) {
											rcuDocVerficationType = "RCU Document Verification Not Available";
										} else {
											for (RCUDocVerificationType rcuDocVerification : RCUDocVerificationType
													.values()) {
												if (rcuDocVerification.getKey() == rcuDocument.getVerificationType()) {
													rcuDocVerficationType = rcuDocVerification.getValue();
												}
											}
										}
										String rcuDocStatus = null;
										if (rcuDocument.getStatus() == 0) {
											rcuDocStatus = "RCU Document Status Not Available";
										} else {
											for (RCUDocStatus rcuDoc : RCUDocStatus.values()) {
												if (rcuDoc.getKey() == rcuDocument.getStatus()) {
													rcuDocStatus = rcuDoc.getValue();
												}
											}
										}
										rcuVerificationData
												.setVerificationType(StringUtils.trimToEmpty(rcuDocVerficationType));
										rcuVerificationData
												.setDocumentName(StringUtils.trimToEmpty(rcuDocument.getDocName()));
										rcuVerificationData.setDocumentStatus(StringUtils.trimToEmpty(rcuDocStatus));
										if(null!=rcuDocument.getReinitid()&&rcuDocument.getReinitid()>0){
											rcuVerificationData.setFinalDecision("Re-initiate");
										}else{
											rcuVerificationData.setFinalDecision(StringUtils.trimToEmpty(verificationData.getFinalDecision()));
										}
										rcuVerificationData.setDoneBy(verificationData.getDoneBy());
										agreement.getRcuVerification().add(rcuVerificationData);
									}
									
								}
							} else {
								agreement.getRcuVerification().add(verificationData);
							}
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Method to populate the Scoring Details to AgreementDetails
	 * 
	 * @param detail
	 * @param agreement
	 */
	private void populateAgreementScoringDetails(FinanceDetail detail, AgreementDetail agreement) {
		if(CollectionUtils.isEmpty(agreement.getScoringDetails())){
			agreement.setScoringDetails(new ArrayList<>());
		}
		
		if(MapUtils.isNotEmpty(detail.getScoreDetailListMap())){
			Map<Long, List<FinanceScoreDetail>> scoreDetailListMap=detail.getScoreDetailListMap();
			List<FinanceScoreHeader> finScoreHeaderList = detail.getFinScoreHeaderList();
			if(CollectionUtils.isNotEmpty(finScoreHeaderList)){
				FinanceScoreHeader financeScoreHeader = finScoreHeaderList.get(0);
				if(null!=financeScoreHeader){
					agreement.setCreditWorth(StringUtils.trimToEmpty(financeScoreHeader.getCreditWorth()));
				}
			}
			BigDecimal totScore=BigDecimal.ZERO;
			BigDecimal maxScore=BigDecimal.ZERO;
			for(long key:scoreDetailListMap.keySet()){
				List<FinanceScoreDetail> scoreDetailList = scoreDetailListMap.get(key);
				if(CollectionUtils.isNotEmpty(scoreDetailList)){
					for(FinanceScoreDetail scoreDetail:scoreDetailList){
						if(null!=scoreDetail){
							Score score=agreement.new Score();
							score.setScoringMetrics(StringUtils.trimToEmpty(scoreDetail.getRuleCode()));
							score.setDescription(StringUtils.trimToEmpty(scoreDetail.getRuleCodeDesc()));
							totScore=totScore.add(scoreDetail.getExecScore());
							maxScore=maxScore.add(scoreDetail.getMaxScore());
							score.setMaximumScore(StringUtils.trimToEmpty(String.valueOf(scoreDetail.getMaxScore())));
							score.setActualScore(StringUtils.trimToEmpty(String.valueOf(scoreDetail.getExecScore())));
							agreement.getScoringDetails().add(score);
						}
					}
				}
			}
			agreement.setTotScore(StringUtils.trimToEmpty(String.valueOf(totScore)));
			agreement.setMaxScore(StringUtils.trimToEmpty(String.valueOf(maxScore)));
		}
	}

	private AgreementDetail populateCustomerExtendedDetails(FinanceDetail detail, AgreementDetail agreement) {
		Map<String, Object> mapValues = detail.getCustomerDetails().getExtendedFieldRender().getMapValues();
		int formater = CurrencyUtil.getFormat(detail.getFinScheduleData().getFinanceMain().getFinCcy());
		for (String key : mapValues.keySet()) {
			ExtendedDetail extendedDetail = agreement.new ExtendedDetail();
			ExtendedFieldDetail extendedFieldDetail=null;
			if(null!= detail.getCustomerDetails().getExtendedFieldHeader() && CollectionUtils.isNotEmpty(detail.getCustomerDetails().getExtendedFieldHeader().getExtendedFieldDetails())){
				List<ExtendedFieldDetail> extendedFieldDetails = detail.getCustomerDetails().getExtendedFieldHeader().getExtendedFieldDetails();
				List<ExtendedFieldDetail> requiredExtendedFields = extendedFieldDetails.stream().filter(efd -> StringUtils.equalsIgnoreCase(key, efd.getFieldName())).collect(Collectors.toList());
				if(CollectionUtils.isNotEmpty(requiredExtendedFields)){
					extendedFieldDetail=requiredExtendedFields.get(0);
					extendedDetail.setFieldLabel(StringUtils.trimToEmpty(extendedFieldDetail.getFieldLabel()));
				}
			}
			extendedDetail.setKey(StringUtils.trimToEmpty(key));
			if (null != mapValues.get(key)) {
				if(extendedFieldDetail!=null && StringUtils.equalsIgnoreCase("CURRENCY", extendedFieldDetail.getFieldType())){
					extendedDetail.setValue(PennantAppUtil.amountFormate((BigDecimal) mapValues.get(key), formater));
				}else{
					extendedDetail.setValue(StringUtils.trimToEmpty(mapValues.get(key).toString()));
				}
				
			} else {
				extendedDetail.setValue(StringUtils.EMPTY);
			}
			extendedDetail.setFieldType("CUSTOMER");
			agreement.getExtendedDetails().add(extendedDetail);
		}
		
		return agreement;
	}

	private AgreementDetail populateExtendedDetails(FinanceDetail detail, AgreementDetail agreement) {
		Map<String, Object> mapValues = detail.getExtendedFieldRender().getMapValues();
		int formater = CurrencyUtil.getFormat(detail.getFinScheduleData().getFinanceMain().getFinCcy());
		for (String key : mapValues.keySet()) {
			ExtendedDetail extendedDetail = agreement.new ExtendedDetail();
			ExtendedFieldDetail extendedFieldDetail=null;
			if(null!= detail.getExtendedFieldHeader() && CollectionUtils.isNotEmpty(detail.getExtendedFieldHeader().getExtendedFieldDetails())){
				List<ExtendedFieldDetail> extendedFieldDetails = detail.getExtendedFieldHeader().getExtendedFieldDetails();
				List<ExtendedFieldDetail> requiredExtendedFields = extendedFieldDetails.stream().filter(efd -> StringUtils.equalsIgnoreCase(key, efd.getFieldName())).collect(Collectors.toList());
				if(CollectionUtils.isNotEmpty(requiredExtendedFields)){
					extendedFieldDetail=requiredExtendedFields.get(0);
					extendedDetail.setFieldLabel(StringUtils.trimToEmpty(extendedFieldDetail.getFieldLabel()));
				}
			}
			
			extendedDetail.setKey(StringUtils.trimToEmpty(key));
			if (null != mapValues.get(key)) {
				extendedDetail.setValue(StringUtils.trimToEmpty(mapValues.get(key).toString()));

				if(extendedFieldDetail!=null && StringUtils.equalsIgnoreCase("CURRENCY", extendedFieldDetail.getFieldType())){
					extendedDetail.setValue(PennantAppUtil.amountFormate((BigDecimal) mapValues.get(key), formater));
				}else{
					extendedDetail.setValue(StringUtils.trimToEmpty(mapValues.get(key).toString()));
				}
			} else {
				extendedDetail.setValue(StringUtils.EMPTY);
			}
			extendedDetail.setFieldType("LOAN");
			agreement.getExtendedDetails().add(extendedDetail);
		}
		return agreement;
	}

	private void populateInternalLiabilities(FinanceDetail detail, AgreementDetail agreement, int formatter,
			CustomerDetails custdetails, String applicantType) {
		if(null!=custdetails && CollectionUtils.isNotEmpty(custdetails.getCustFinanceExposureList())){
			List<FinanceEnquiry> custFinanceExposureList = custdetails.getCustFinanceExposureList();
			for(FinanceEnquiry financeEnquiry:custFinanceExposureList){
				if(null!=financeEnquiry){
					InternalLiabilityDetail internalLiabilityDetail=agreement.new InternalLiabilityDetail();
					
					internalLiabilityDetail.setAppType(StringUtils.trimToEmpty(applicantType));
					if(null!=custdetails.getCustomer()){
						Customer customer=custdetails.getCustomer();
						internalLiabilityDetail.setCustCIF(StringUtils.trimToEmpty(customer.getCustCIF()));
						internalLiabilityDetail.setCustName(StringUtils.trimToEmpty(customer.getCustShrtName()));
					}
					internalLiabilityDetail.setEmiAmt(PennantAppUtil.amountFormate(financeEnquiry.getMaxInstAmount(), formatter));
					internalLiabilityDetail.setLanNumber(StringUtils.trimToEmpty(financeEnquiry.getFinReference()));
					internalLiabilityDetail.setAmt(PennantAppUtil.amountFormate(financeEnquiry.getFinAmount(), formatter));
					internalLiabilityDetail.setStatus(StringUtils.trimToEmpty(financeEnquiry.getFinStatus()));
					internalLiabilityDetail.setBalTerms(Integer.toString(financeEnquiry.getNumberOfTerms()));
					agreement.getInternalLiabilityDetails().add(internalLiabilityDetail);
				}
			}
		}
	}

	private void setDeviationDetails(AgreementDetail agreement, List<FinanceDeviations> financeDeviations) {
		if(CollectionUtils.isNotEmpty(financeDeviations)){
			for(FinanceDeviations deviations:financeDeviations){
				if(null!=deviations){
					LoanDeviation loanDeviation=agreement.new LoanDeviation();
					if(null!=deviations.getDeviationDate()){
						loanDeviation.setDeviationRaisedDate(DateUtil.formatToShortDate(new Date(deviations.getDeviationDate().getTime())));
					}
					if (StringUtils.equals(DeviationConstants.CAT_MANUAL, deviations.getDeviationCategory())) {
						loanDeviation.setDeviationType("Manual");
						loanDeviation.setDeviationDescription(StringUtils.trimToEmpty(deviations.getDeviationCodeDesc()));
						loanDeviation.setDeviationCode(StringUtils.trimToEmpty(deviations.getDeviationCodeName()));
						loanDeviation.setRemarks(StringUtils.trimToEmpty(deviations.getRemarks()));
						Optional<Property> severityDetail = severities.stream().filter(severity -> severity.getKey().equals(deviations.getSeverity())).findFirst();
						if(severityDetail.isPresent()){
							loanDeviation.setSeverity(StringUtils.trimToEmpty(severityDetail.get().getValue()));
						}
					}else{
						loanDeviation.setDeviationType("Auto");
						JdbcSearchObject<DeviationParam> searchObject = new JdbcSearchObject<DeviationParam>(DeviationParam.class);
						searchObject.addSortAsc("dataType");
						loanDeviation.setDeviationDescription(StringUtils.trimToEmpty(deviationHelper.getDeviationDesc(deviations, searchProcessor.getResults(searchObject))));
						loanDeviation.setDeviationCode("-");
						loanDeviation.setRemarks("-");
					}
					loanDeviation.setDeviationApprovedBy(StringUtils.trimToEmpty(deviations.getDelegationRole()));
					agreement.getLoanDeviations().add(loanDeviation);
				}
			}
		}
	}

	private void setIrrDetails(FinanceDetail detail, AgreementDetail agreement) {
		List<FinIRRDetails> getiRRDetails = detail.getFinScheduleData().getiRRDetails();
		for(FinIRRDetails finIRRDetails:getiRRDetails){
			if(null!=finIRRDetails){
				IrrDetail irrDetail=agreement.new IrrDetail();
				irrDetail.setIrrCode(StringUtils.trimToEmpty(finIRRDetails.getiRRCode()));
				irrDetail.setIrrDesc(StringUtils.trimToEmpty(finIRRDetails.getIrrCodeDesc()));
				if(null!=finIRRDetails.getIRR()){
					irrDetail.setIrrPercentage(PennantApplicationUtil.formatRate(
							finIRRDetails.getIRR().doubleValue(), 2));
				}else{
					irrDetail.setIrrPercentage(StringUtils.EMPTY);
				}
				agreement.getIrrDetails().add(irrDetail);
			}
		}
	}

	private void populateBankingDetails(AgreementDetail agreement, int formatter,
			String applicantType, CustomerDetails custdetails) {
		List<CustomerBankInfo> customerBankInfoList=custdetails.getCustomerBankInfoList();
		for(CustomerBankInfo customerBankInfo: customerBankInfoList){
			BankingDetail bankingDetail=agreement.new BankingDetail();
			bankingDetail.setApplicantType(StringUtils.trimToEmpty(applicantType));
			if(null!=custdetails && null!=custdetails.getCustomer()){
				bankingDetail.setCustCIF(StringUtils.trimToEmpty(custdetails.getCustomer().getCustCIF()));
				bankingDetail.setCustName(StringUtils.trimToEmpty(custdetails.getCustomer().getCustShrtName()));
			}
			bankingDetail.setAccType(StringUtils.trimToEmpty(customerBankInfo.getLovDescAccountType()));
			bankingDetail.setAccNo(StringUtils.trimToEmpty(customerBankInfo.getAccountNumber()));
			bankingDetail.setBankName(StringUtils.trimToEmpty(customerBankInfo.getLovDescBankName()));
			bankingDetail.setTotCreditAmt(PennantAppUtil.amountFormate(customerBankInfo.getCreditTranAmt(),formatter));
			bankingDetail.setTotDebitAmt(PennantAppUtil.amountFormate(customerBankInfo.getDebitTranAmt(),formatter));
			bankingDetail.setNoCheqBounce(Integer.toString(customerBankInfo.getOutwardChqBounceNo()));
			bankingDetail.setAvgEODBalance(PennantAppUtil.amountFormate(customerBankInfo.getEodBalAvg(),formatter));
			bankingDetail.setTotNoCredTrans(String.valueOf(customerBankInfo.getCreditTranNo()));
			bankingDetail.setAvgCreditAmt(PennantAppUtil.amountFormate(customerBankInfo.getCreditTranAvg(),formatter));
			bankingDetail.setTotNoDebitTrans(String.valueOf(customerBankInfo.getDebitTranNo()));
			bankingDetail.setNoCashDeposit(String.valueOf(customerBankInfo.getCashDepositNo()));
			bankingDetail.setCashDepositAmt(PennantAppUtil.amountFormate(customerBankInfo.getCashDepositAmt(),formatter));
			bankingDetail.setNoCashWithDra(String.valueOf(customerBankInfo.getCashWithdrawalNo()));
			bankingDetail.setCashWithDraAmt(PennantAppUtil.amountFormate(customerBankInfo.getCashWithdrawalAmt(),formatter));
			bankingDetail.setNoCheqDeposit(String.valueOf(customerBankInfo.getChqDepositNo()));
			bankingDetail.setAmtCheqDeposit(PennantAppUtil.amountFormate(customerBankInfo.getChqDepositAmt(),formatter));
			bankingDetail.setNoCheqIssue(String.valueOf(customerBankInfo.getChqIssueNo()));
			bankingDetail.setAmtCheqIssue(PennantAppUtil.amountFormate(customerBankInfo.getChqIssueAmt(),formatter));
			bankingDetail.setNoInwardCheq(String.valueOf(customerBankInfo.getInwardChqBounceNo()));
			bankingDetail.setNoOutwardCheq(String.valueOf(customerBankInfo.getOutwardChqBounceNo()));
			bankingDetail.setMinEodBalance(PennantAppUtil.amountFormate(customerBankInfo.getEodBalMin(),formatter));
			bankingDetail.setMaxEodBalance(PennantAppUtil.amountFormate(customerBankInfo.getEodBalMax(),formatter));
			bankingDetail.setAvgEodBalance(PennantAppUtil.amountFormate(customerBankInfo.getEodBalAvg(),formatter));
			
			agreement.getBankingDetails().add(bankingDetail);
		}
	}

	private void populateAppIncExpDetails(List<CustomerIncome> customerIncomeList, AgreementDetail agreement, int formatter, String applicantType) {
		BigDecimal income= BigDecimal.ZERO;
		BigDecimal expance= BigDecimal.ZERO;
		
			for(CustomerIncome customerIncome:customerIncomeList){
				if(null!=customerIncome){
					if(customerIncome.getIncomeExpense().equalsIgnoreCase("INCOME")){
						AppIncDetail appIncDetail = agreement.new AppIncDetail();
						appIncDetail.setCustCIF(StringUtils.trimToEmpty(customerIncome.getCustCif()));
						appIncDetail.setApplicantType(StringUtils.trimToEmpty(applicantType));
						appIncDetail.setCustName(StringUtils.trimToEmpty(customerIncome.getCustShrtName()));
						appIncDetail.setIncomeCategory(StringUtils.trimToEmpty(customerIncome.getCategoryDesc()));
						appIncDetail.setIncomeType(StringUtils.trimToEmpty(customerIncome.getIncomeTypeDesc()));
						appIncDetail.setAmt(PennantAppUtil.amountFormate(customerIncome.getCalculatedAmount(), formatter));
						appIncDetail.setMargin(customerIncome.getMargin());
						agreement.getAppIncDetails().add(appIncDetail);
						income = income.add(customerIncome.getCalculatedAmount());
					}else if(customerIncome.getIncomeExpense().equalsIgnoreCase("EXPENSE")){
						AppExpDetail appExpDetail = agreement.new AppExpDetail();
						appExpDetail.setCustName(StringUtils.trimToEmpty(customerIncome.getCustShrtName()));
						appExpDetail.setApplicantType(StringUtils.trimToEmpty(applicantType));
						appExpDetail.setExpenseCategory(StringUtils.trimToEmpty(customerIncome.getCategoryDesc()));
						appExpDetail.setExpenseType(StringUtils.trimToEmpty(customerIncome.getIncomeTypeDesc()));
						appExpDetail.setAmt(PennantAppUtil.amountFormate(customerIncome.getCalculatedAmount(), formatter));
						appExpDetail.setMargin(customerIncome.getMargin());
						agreement.getAppExpDetails().add(appExpDetail);
						expance = expance.add(customerIncome.getCalculatedAmount());
					}
					
				}
			}
			if(StringUtils.equalsIgnoreCase("Co-Applicant", applicantType)){
				agreement.setCoAppTotIncome(PennantAppUtil.amountFormate(income, formatter));
				agreement.setCoAppTotExpense(PennantAppUtil.amountFormate(expance, formatter));	
				totalIncome=totalIncome.add(income);
				totalExpense=totalExpense.add(expance);
			}
	}

	private void setCustomerEmpDetails(FinanceDetail detail, AgreementDetail agreement) {
		List<CustomerEmploymentDetail> employmentDetailsList = detail.getCustomerDetails().getEmploymentDetailsList();
		if(null!=employmentDetailsList.get(0)){
			agreement.setCustEmpType(StringUtils.trimToEmpty(employmentDetailsList.get(0).getLovDescCustEmpTypeName()));
			agreement.setCustEmpDesignation(StringUtils.trimToEmpty(employmentDetailsList.get(0).getLovDescCustEmpDesgName()));
		}
		int empExperence=0;
		for(CustomerEmploymentDetail custEmploymentDetail:employmentDetailsList){
			if(null!=custEmploymentDetail){
				Date toDate=(custEmploymentDetail.isCurrentEmployer())?DateUtility.getAppDate():custEmploymentDetail.getCustEmpFrom();
				int tempExperence=DateUtility.getYearsBetween(custEmploymentDetail.getCustEmpTo(),toDate);
				empExperence=(tempExperence>0)?(empExperence+tempExperence):empExperence;
			}
		}
		agreement.setCustYearsExp(Integer.toString(empExperence));
	}

	private void setCustomerDocuments(AgreementDetail agreement, List<CustomerDocument> customerDocuments) {
		customerDocuments.forEach((customerDocument)->{
			Document document=agreement.new Document();
			document.setCusDocName(StringUtils.stripToEmpty(customerDocument.getCustDocCategory())+"-"+StringUtils.stripToEmpty(customerDocument.getLovDescCustDocCategory()));
			document.setReceiveDate(DateUtility.formatToLongDate(customerDocument.getCustDocRcvdOn()));
			document.setDocType("CUSTOMER");
			document.setUserName(StringUtils.stripToEmpty(document.getUserName()));
			agreement.getDocuments().add(document);
		});
	}

	private void populateContactDetails(FinanceDetail detail, AgreementDetail agreement) {
		if(CollectionUtils.isNotEmpty(detail.getCustomerDetails().getCustomerPhoneNumList())){
			detail.getCustomerDetails().getCustomerPhoneNumList().forEach((phoneNumber)->{
				ContactDetail contactDetail=agreement.new ContactDetail();
				contactDetail.setContactType(StringUtils.trimToEmpty(phoneNumber.getLovDescPhoneTypeCodeName()));
				contactDetail.setContactValue(StringUtils.trimToEmpty(phoneNumber.getPhoneNumber()));
				agreement.getContactDetails().add(contactDetail);
			});
		}
	}

	//TODO:: Need more details
	private void setCovenantDetails(AgreementDetail agreement, List<FinCovenantType> covenantTypeList) {
		covenantTypeList.forEach((covenantType)->{
			Covenant covenant=agreement.new Covenant();
			SecurityUser securityUser = getSecurityUserService().getSecurityUserById(covenantType.getLastMntBy());
			if(null!=securityUser){
				covenant.setUserName(StringUtils.trimToEmpty(securityUser.getUsrLogin()));
			}			
			covenant.setRaisedDate(DateUtility.formatToLongDate(covenantType.getLastMntOn()));
			covenant.setCusDocName(StringUtils.trimToEmpty(covenantType.getCovenantTypeDesc()));
			covenant.setRemarks(StringUtils.trimToEmpty(covenantType.getDescription()));
			if(covenantType.isAlwPostpone()){
				covenant.setTargetDate(DateUtility.formatToLongDate(covenantType.getReceivableDate()));
				covenant.setStatus("Pending");
			}else if(covenantType.isAlwWaiver()){
				covenant.setStatus("Waived");
				covenant.setTargetDate(new String());
			}else{
				covenant.setStatus(new String());
				covenant.setTargetDate(new String());
			}
			covenant.setInternalUse(String.valueOf(covenantType.isInternalUse()));
			agreement.getCovenants().add(covenant);
		});
	}

	private void setLoanDocuments(AgreementDetail agreement, List<DocumentDetails> documentDetailsList) {
		documentDetailsList.forEach((documentDetail)->{
			if(null!=documentDetail && StringUtils.equalsIgnoreCase(documentDetail.getDocModule(), "Finance")){
				Document document=agreement.new Document();
				Optional<DocumentType> findFirst = null;
				if (CollectionUtils.isNotEmpty(documentTypeList)) {
					findFirst = documentTypeList.stream().filter(docType -> docType.getDocTypeCode()
							.equals(StringUtils.trimToEmpty(documentDetail.getDocCategory()))).findFirst();
				}
				if (null != findFirst && findFirst.isPresent()) {
					document.setCusDocName(StringUtils.trimToEmpty(documentDetail.getDocCategory()) + "-"
							+ StringUtils.trimToEmpty(findFirst.get().getDocTypeDesc()));
				} else {
					document.setCusDocName(StringUtils.trimToEmpty(documentDetail.getDocCategory()));
				}
				document.setReceiveDate(DateUtility.formatToLongDate(documentDetail.getDocReceivedDate()));
				document.setDocType("LOAN");
				document.setUserName(StringUtils.trimToEmpty(document.getUserName()));
				agreement.getDocuments().add(document);
			}
		});
	}

	private void setDisbursementDetails(AgreementDetail agreement, List<FinAdvancePayments> advancePaymentsList, int formatter) {
		agreement.setDisbursements(new ArrayList<>());
		advancePaymentsList.forEach((advancePayment)->{
			Disbursement disbursement = agreement.new Disbursement();
			disbursement.setDisbursementAmt(PennantAppUtil.amountFormate(advancePayment.getAmtToBeReleased(), formatter));
			disbursement.setAccountHolderName(StringUtils.trimToEmpty(advancePayment.getBeneficiaryName()));
			disbursement.setDisbursementDate(DateUtility.formatToLongDate(advancePayment.getLlDate()));
			disbursement.setBankName(StringUtils.trimToEmpty(advancePayment.getBranchBankName()));
			disbursement.setDisbursementAcct(StringUtils.trimToEmpty(advancePayment.getBeneficiaryAccNo()));
			disbursement.setIfscCode(StringUtils.trimToEmpty(advancePayment.getiFSC()));
			disbursement.setPaymentMode(StringUtils.trimToEmpty(advancePayment.getPaymentType()));
			disbursement.setFavoringName(StringUtils.trimToEmpty(advancePayment.getLiabilityHoldName()));
			disbursement.setIssueBankName(StringUtils.trimToEmpty(advancePayment.getBankName()));
			disbursement.setIirReferenceNo(StringUtils.trimToEmpty(advancePayment.getLlReferenceNo()));
			disbursement.setPaymentModeRef(StringUtils.trimToEmpty(advancePayment.getTransactionRef()));
			
			//additional details
			disbursement.setPaymentId(String.valueOf(advancePayment.getPaymentId()));
			disbursement.setPaymentSeq(String.valueOf(advancePayment.getPaymentSeq()));
			disbursement.setDisbSeq(String.valueOf(advancePayment.getDisbSeq()));
			disbursement.setPaymentDetail(StringUtils.trimToEmpty(advancePayment.getPaymentDetail()));
			disbursement.setCustContribution(PennantAppUtil.amountFormate(advancePayment.getCustContribution(), formatter));
			disbursement.setSellerContribution(PennantAppUtil.amountFormate(advancePayment.getSellerContribution(), formatter));
			disbursement.setRemarks(StringUtils.trimToEmpty(advancePayment.getRemarks()));
			disbursement.setBankCode(StringUtils.trimToEmpty(advancePayment.getBankCode()));
			disbursement.setBranchBankCode(StringUtils.trimToEmpty(advancePayment.getBranchBankCode()));
			disbursement.setBranchCode(StringUtils.trimToEmpty(advancePayment.getBranchCode()));
			disbursement.setBranchDesc(StringUtils.trimToEmpty(advancePayment.getBranchDesc()));
			disbursement.setCity(StringUtils.trimToEmpty(advancePayment.getCity()));
			disbursement.setPayableLoc(StringUtils.trimToEmpty(advancePayment.getPayableLoc()));
			disbursement.setPrintingLoc(StringUtils.trimToEmpty(advancePayment.getPrintingLoc()));
			disbursement.setValueDate(DateUtility.formatToLongDate(advancePayment.getValueDate()));
			disbursement.setBankBranchID(String.valueOf(advancePayment.getBankBranchID()));
			disbursement.setPhoneCountryCode(StringUtils.trimToEmpty(advancePayment.getPhoneCountryCode()));
			disbursement.setPhoneAreaCode(StringUtils.trimToEmpty(advancePayment.getPhoneAreaCode()));
			disbursement.setPhoneNumber(StringUtils.trimToEmpty(advancePayment.getPhoneNumber()));
			disbursement.setClearingDate(DateUtility.formatToLongDate(advancePayment.getClearingDate()));
			disbursement.setStatus(StringUtils.trimToEmpty(advancePayment.getStatus()));
			disbursement.setActive((advancePayment.isActive())?"YES":"NO");
			disbursement.setInputDate(DateUtility.formatToLongDate(advancePayment.getInputDate()));
			disbursement.setDisbCCy(StringUtils.trimToEmpty(advancePayment.getDisbCCy()));
			disbursement.setpOIssued((advancePayment.ispOIssued())?"YES":"NO");
			disbursement.setLovValue(StringUtils.trimToEmpty(advancePayment.getLovValue()));
			disbursement.setPartnerBankID(String.valueOf(advancePayment.getPartnerBankID()));
			disbursement.setPartnerbankCode(StringUtils.trimToEmpty(advancePayment.getPartnerbankCode()));
			disbursement.setPartnerBankName(StringUtils.trimToEmpty(advancePayment.getPartnerBankName()));
			disbursement.setFinType(StringUtils.trimToEmpty(advancePayment.getFinType()));
			disbursement.setCustShrtName(StringUtils.trimToEmpty(advancePayment.getCustShrtName()));
			disbursement.setLinkedTranId(String.valueOf(advancePayment.getLinkedTranId()));
			disbursement.setPartnerBankAcType(StringUtils.trimToEmpty(advancePayment.getPartnerBankAcType()));
			disbursement.setRejectReason(StringUtils.trimToEmpty(advancePayment.getRejectReason()));
			disbursement.setPartnerBankAc(StringUtils.trimToEmpty(advancePayment.getPartnerBankAc()));
			disbursement.setAlwFileDownload((advancePayment.isAlwFileDownload())?"YES":"NO");
			disbursement.setFileNamePrefix(StringUtils.trimToEmpty(advancePayment.getFileNamePrefix()));
			disbursement.setChannel(StringUtils.trimToEmpty(advancePayment.getChannel()));
			disbursement.setEntityCode(StringUtils.trimToEmpty(advancePayment.getEntityCode()));
			
			agreement.getDisbursements().add(disbursement);
		});
	}

	private void setFeeChargeDetails(AgreementDetail agreement, int formatter, List<FinFeeDetail> finFeeDetails) {
		if(null!=finFeeDetails&&finFeeDetails.size()>0){
			finFeeDetails.forEach((finFeeDetail)->{
				com.pennant.backend.model.finance.AgreementDetail.CusCharge charge = agreement.new CusCharge();
				charge.setFeeChargeDesc(StringUtils.trimToEmpty(finFeeDetail.getFeeTypeDesc()));
				charge.setChargeAmt(PennantAppUtil.amountFormate(finFeeDetail.getActualAmount(), formatter));
				charge.setChargeWaver(PennantAppUtil.amountFormate(finFeeDetail.getWaivedAmount(), formatter));
				charge.setChargePaid(PennantAppUtil.amountFormate(finFeeDetail.getPaidAmount(), formatter));
				charge.setRemainingAmount(PennantAppUtil.amountFormate(finFeeDetail.getRemainingFee(), formatter));
				charge.setFeeTreatment(StringUtils.trimToEmpty(finFeeDetail.getFeeScheduleMethod()));
				agreement.getCusCharges().add(charge);
			});
		}
	}

	private void setRepaymentDetails(AgreementDetail agreement, Mandate mandate) {
		agreement.setRepayBankName(StringUtils.trimToEmpty(mandate.getBankName()));
		agreement.setRepayAcctIfscCode(StringUtils.trimToEmpty(mandate.getIFSC()));
		agreement.setBranchName(StringUtils.trimToEmpty(mandate.getBranchDesc()));
		agreement.setRepayAcct(StringUtils.trimToEmpty(mandate.getAccNumber()));
		agreement.setRepayCustName(StringUtils.trimToEmpty(mandate.getAccHolderName()));
		agreement.setRepayMode(StringUtils.trimToEmpty(mandate.getMandateType()));
	}

	private void setCustomerBankInfo(FinanceDetail detail, AgreementDetail agreement, String aggModuleDetails) {
		agreement.setCustomerBankInfos(
				new ArrayList<com.pennant.backend.model.finance.AgreementDetail.CustomerBankInfo>());
		if (aggModuleDetails.contains(PennantConstants.AGG_BANKING) && detail != null
				&& detail.getCustomerDetails() != null
				&& detail.getCustomerDetails().getCustomerBankInfoList() != null) {
			List<CustomerBankInfo> list = detail.getCustomerDetails().getCustomerBankInfoList();
			for (CustomerBankInfo customerBankInfo : list) {
				com.pennant.backend.model.finance.AgreementDetail.CustomerBankInfo custbank = agreement.new CustomerBankInfo();
				custbank.setBankCode(StringUtils.trimToEmpty(customerBankInfo.getBankCode()));
				custbank.setBankName(StringUtils.trimToEmpty(customerBankInfo.getLovDescBankName()));
				custbank.setAccountType(StringUtils.trimToEmpty(customerBankInfo.getAccountType()));
				custbank.setAccountNumber(StringUtils.trimToEmpty(customerBankInfo.getAccountNumber()));
				agreement.getCustomerBankInfos().add(custbank);
			}

		}
		if (CollectionUtils.isEmpty(agreement.getCustomerBankInfos())) {
			agreement.getCustomerBankInfos().add(agreement.new CustomerBankInfo());
		}

	}

	private void setCustomerAddress(AgreementDetail agreement, List<CustomerAddres> addressList) {
		if (addressList != null	&& !addressList.isEmpty()) {
			if (addressList.size()==1) {
				setAddressDetails(agreement, addressList.get(0));
			}else{
				// sort the address based on priority and consider the top priority 
				sortCustomerAdress(addressList);
				for (CustomerAddres customerAddres : addressList) {
					setAddressDetails(agreement, customerAddres);
					break;
				}
				
			}
		}
	}
	
	private void setCoapplicantAddress(CoApplicant coapplicant, List<CustomerAddres> addressList) {
		if (addressList != null	&& !addressList.isEmpty()) {
			if (addressList.size()==1) {
				setAddressDetails(coapplicant, addressList.get(0));
			}else{
				// sort the address based on priority and consider the top priority 
				sortCustomerAdress(addressList);
				for (CustomerAddres customerAddres : addressList) {
					setAddressDetails(coapplicant, customerAddres);
					break;
				}
				
			}
		}
	}
	
	public static void sortCustomerAdress(List<CustomerAddres> list) {

		if (list != null && !list.isEmpty()) {
			Collections.sort(list, new Comparator<CustomerAddres>() {
				@Override
				public int compare(CustomerAddres detail1, CustomerAddres detail2) {
					return detail2.getCustAddrPriority() - detail1.getCustAddrPriority();
				}
			});
		}
	}
	
	private static void sortCustomerPhoneDetails(List<CustomerPhoneNumber> list) {

		if (list != null && !list.isEmpty()) {
			Collections.sort(list, new Comparator<CustomerPhoneNumber>() {
				@Override
				public int compare(CustomerPhoneNumber detail1, CustomerPhoneNumber detail2) {
					return detail2.getPhoneTypePriority() - detail1.getPhoneTypePriority();
				}
			});
		}
	}

	private void setCoapplicantPhoneNumber(CoApplicant coapplicant, List<CustomerPhoneNumber> phoneNumList) {
		if (phoneNumList != null	&& !phoneNumList.isEmpty()) {
			if (phoneNumList.size()==1) {
				setPhoneDetails(coapplicant, phoneNumList.get(0));
			}else{
				// sort the address based on priority and consider the top priority 
				sortCustomerPhoneDetails(phoneNumList);
				for (CustomerPhoneNumber customerPhoneNumber : phoneNumList) {
					setPhoneDetails(coapplicant, customerPhoneNumber);
					break;
				}
				
			}
		}
	}

	private void setPhoneDetails(CoApplicant coapplicant, CustomerPhoneNumber customerPhoneNumber) {
		coapplicant.setCustAddrPhone(StringUtils.trimToEmpty(customerPhoneNumber.getPhoneNumber()));
	}
	
	private void setCoapplicantEMailId(CoApplicant coapplicant, List<CustomerEMail> eMailList) {
		if (eMailList != null	&& !eMailList.isEmpty()) {
			if (eMailList.size()==1) {
				setEMailDetails(coapplicant, eMailList.get(0));
			}else{
				// sort the address based on priority and consider the top priority 
				sortCustomerEMailDetails(eMailList);
				for (CustomerEMail customerEMail : eMailList) {
					setEMailDetails(coapplicant, customerEMail);
					break;
				}
				
			}
		}
	}

	private void sortCustomerEMailDetails(List<CustomerEMail> list){
		if (list != null && !list.isEmpty()) {
			Collections.sort(list, new Comparator<CustomerEMail>() {
				@Override
				public int compare(CustomerEMail detail1, CustomerEMail detail2) {
					return detail2.getCustEMailPriority() - detail1.getCustEMailPriority();
				}
			});
		}
	}

	private void setEMailDetails(CoApplicant coapplicant, CustomerEMail customerEMail) {
		coapplicant.setCustEmail(StringUtils.trimToEmpty(customerEMail.getCustEMail()));
		
	}

	private void setAddressDetails(AgreementDetail agreement, CustomerAddres customerAddres) {
		agreement.setCustAddrHNbr(customerAddres.getCustAddrHNbr());
		agreement.setCustFlatNbr(StringUtils.trimToEmpty(customerAddres.getCustFlatNbr()));
		agreement.setCustPOBox(StringUtils.trimToEmpty(customerAddres.getCustPOBox()));
		agreement.setCustAddrStreet(StringUtils.trimToEmpty(customerAddres.getCustAddrStreet()));
		agreement.setCustAddrCountry(StringUtils.trimToEmpty(customerAddres.getLovDescCustAddrCountryName()));
		agreement.setCustAddrProvince(StringUtils.trimToEmpty(customerAddres.getLovDescCustAddrProvinceName()));
		agreement.setCustAddrCity(StringUtils.trimToEmpty(customerAddres.getLovDescCustAddrCityName()));
		if (!PennantConstants.CITY_FREETEXT) {
			agreement.setCustAddrCity(StringUtils.trimToEmpty(customerAddres.getLovDescCustAddrCityName()));
		}
		agreement.setCustAddrLine1(StringUtils.trimToEmpty(customerAddres.getCustAddrLine1()));
		agreement.setCustAddrLine2(StringUtils.trimToEmpty(customerAddres.getCustAddrLine2()));
		agreement.setCustAddrZIP(StringUtils.trimToEmpty(customerAddres.getCustAddrZIP()));
	}
	
	private void setAddressDetails(CoApplicant coapplicant, CustomerAddres customerAddres) {
		coapplicant.setCustAddrHNbr(customerAddres.getCustAddrHNbr());
		coapplicant.setCustFlatNbr(StringUtils.trimToEmpty(customerAddres.getCustFlatNbr()));
		coapplicant.setCustPOBox(StringUtils.trimToEmpty(customerAddres.getCustPOBox()));
		coapplicant.setCustAddrStreet(StringUtils.trimToEmpty(customerAddres.getCustAddrStreet()));
		coapplicant.setCustAddrCountry(StringUtils.trimToEmpty(customerAddres.getLovDescCustAddrCountryName()));
		coapplicant.setCustAddrProvince(StringUtils.trimToEmpty(customerAddres.getLovDescCustAddrProvinceName()));
		coapplicant.setCustAddrCity(StringUtils.trimToEmpty(customerAddres.getLovDescCustAddrCityName()));
		if (!PennantConstants.CITY_FREETEXT) {
			coapplicant.setCustAddrCity(StringUtils.trimToEmpty(customerAddres.getLovDescCustAddrCityName()));
		}
		coapplicant.setCustAddrLine1(StringUtils.trimToEmpty(customerAddres.getCustAddrLine1()));
		coapplicant.setCustAddrLine2(StringUtils.trimToEmpty(customerAddres.getCustAddrLine2()));
		coapplicant.setCustAddrZIP(StringUtils.trimToEmpty(customerAddres.getCustAddrZIP()));
	}

	private void setMandateDetails(FinanceDetail detail, AgreementDetail agreement) {
		if (detail.getMandate() !=null) {
			agreement.setAccNumberMandate(StringUtils.stripToEmpty(detail.getMandate().getAccNumber()));
		}
		
	}

	private void setCoapplicantDetails(FinanceDetail detail, AgreementDetail agreement, String aggModuleDetails) {
		agreement.setCoApplicants(new ArrayList<>());
		if (aggModuleDetails.contains(PennantConstants.AGG_COAPPDT)&&detail.getJountAccountDetailList()!=null && !detail.getJountAccountDetailList().isEmpty()) {
			for (JointAccountDetail jointAccountDetail : detail.getJountAccountDetailList()) {
				CustomerDetails custdetails = customerDetailsService.getCustomerDetailsById(jointAccountDetail.getCustID(), true, "_AView");
				CoApplicant coapplicant = agreement.new CoApplicant();
				Customer customer = custdetails.getCustomer();
				coapplicant.setCustRelation(StringUtils.trimToEmpty(jointAccountDetail.getCatOfcoApplicant()));
				coapplicant.setCustName(StringUtils.trimToEmpty(customer.getCustShrtName()));
				coapplicant.setCustCIF(StringUtils.trimToEmpty(customer.getCustCIF()));
				//pan number
				List<CustomerDocument> doclist = custdetails.getCustomerDocumentsList();
				coapplicant.setPanNumber(PennantApplicationUtil.getPanNumber(doclist));
				
				List<CustomerAddres> addlist = custdetails.getAddressList();
				if (addlist!=null && !addlist.isEmpty()) {
					setCoapplicantAddress(coapplicant, addlist);
				}
				
				List<CustomerPhoneNumber> phoneNumList = custdetails.getCustomerPhoneNumList();
				if(phoneNumList!=null && !phoneNumList.isEmpty()){
					setCoapplicantPhoneNumber(coapplicant, phoneNumList);
				}
				
				List<CustomerEMail> eMailList = custdetails.getCustomerEMailList();
				if(eMailList!=null && !eMailList.isEmpty()){
					setCoapplicantEMailId(coapplicant, eMailList);
				}
				coapplicant.setApplicantType("Co-Applicant");
				agreement.getCoApplicants().add(coapplicant);
			}
		}
		
		if (aggModuleDetails.contains(PennantConstants.AGG_COAPPDT)&&CollectionUtils.isNotEmpty(detail.getGurantorsDetailList())) {
			detail.getGurantorsDetailList().forEach((guarantorDetail)->{
				CoApplicant coapplicant = agreement.new CoApplicant();
				if(guarantorDetail.getCustID()>0){
					CustomerDetails customerDetails = customerDetailsService.getCustomerDetailsById(guarantorDetail.getCustID(), true, "_AView");
					Customer customer = customerDetails.getCustomer();
					coapplicant.setCustName(StringUtils.trimToEmpty(customer.getCustShrtName()));
					//pan number
					List<CustomerDocument> doclist = customerDetails.getCustomerDocumentsList();
					coapplicant.setPanNumber(PennantApplicationUtil.getPanNumber(doclist));
					
					List<CustomerAddres> addlist = customerDetails.getAddressList();
					if (addlist!=null && !addlist.isEmpty()) {
						setCoapplicantAddress(coapplicant, addlist);
					}
					
					List<CustomerPhoneNumber> phoneNumList = customerDetails.getCustomerPhoneNumList();
					if(phoneNumList!=null && !phoneNumList.isEmpty()){
						setCoapplicantPhoneNumber(coapplicant, phoneNumList);
					}
					
					List<CustomerEMail> eMailList = customerDetails.getCustomerEMailList();
					if(eMailList!=null && !eMailList.isEmpty()){
						setCoapplicantEMailId(coapplicant, eMailList);
					}
				}else{
					coapplicant.setCustName(StringUtils.trimToEmpty(guarantorDetail.getCustShrtName()));
					CustomerAddres customerAddres =new CustomerAddres();
					customerAddres.setCustAddrHNbr(StringUtils.trimToEmpty(guarantorDetail.getAddrHNbr()));
					customerAddres.setCustFlatNbr(StringUtils.trimToEmpty(guarantorDetail.getFlatNbr()));
					customerAddres.setCustPOBox(StringUtils.trimToEmpty(guarantorDetail.getPOBox()));
					customerAddres.setCustAddrStreet(StringUtils.trimToEmpty(guarantorDetail.getAddrStreet()));
					customerAddres.setCustAddrLine1(StringUtils.trimToEmpty(guarantorDetail.getAddrLine1()));
					customerAddres.setCustAddrLine2(StringUtils.trimToEmpty(guarantorDetail.getAddrLine2()));
					customerAddres.setCustAddrCity(StringUtils.trimToEmpty(guarantorDetail.getAddrCity()));
					customerAddres.setCustAddrZIP(StringUtils.trimToEmpty(guarantorDetail.getAddrZIP()));
					setAddressDetails(coapplicant, customerAddres);
					coapplicant.setCustAddrPhone(StringUtils.trimToEmpty(guarantorDetail.getMobileNo()));
					coapplicant.setCustEmail(StringUtils.trimToEmpty(guarantorDetail.getEmailId()));
				}
				coapplicant.setApplicantType("Guarantor");
				agreement.getCoApplicants().add(coapplicant);
			});
		}
		
		if(CollectionUtils.sizeIsEmpty(agreement.getCoApplicants())){
			agreement.getCoApplicants().add(agreement.new CoApplicant());
		}
		
	}

	private AgreementDetail getFinRepayHeaderDetails(AgreementDetail agreement, FinRepayHeader finRepayHeader,
			int formatter) {
		agreement.setEarlySettleAmt(PennantApplicationUtil.amountFormate(finRepayHeader.getRepayAmount(), formatter));
		agreement.setEarlySettleDate(DateUtility.formatToLongDate(finRepayHeader.getEarlyPayDate()));
		return agreement;
	}

	private AgreementDetail getAgreementArabicFieldDetails(AgreementDetail agreement,
			AgreementFieldDetails aAgreementFieldDetails, int formatter) {

		agreement.setCustCityArabic(StringUtils.trimToEmpty(aAgreementFieldDetails.getCustCity()));
		agreement.setBuildUpAreaArabic(StringUtils.trimToEmpty(aAgreementFieldDetails.getBuiltupAreainSqft()));
		agreement.setCustNationalityArabic(StringUtils.trimToEmpty(aAgreementFieldDetails.getCustNationality()));
		agreement.setPlotUnitNumberArabic(StringUtils.trimToEmpty(aAgreementFieldDetails.getPlotOrUnitNo()));
		agreement.setOtherbankNameArabic(StringUtils.trimToEmpty(aAgreementFieldDetails.getOtherbankName()));
		agreement.setPropertyTypeArabic(StringUtils.trimToEmpty(aAgreementFieldDetails.getPropertyType()));
		agreement.setSectorOrCommArabic(StringUtils.trimToEmpty(aAgreementFieldDetails.getSectorOrCommunity()));
		agreement.setFinAmountInArabic(StringUtils.trimToEmpty(aAgreementFieldDetails.getFinAmount()));
		agreement.setUnitAreaInSqftArabic(StringUtils.trimToEmpty(aAgreementFieldDetails.getPlotareainsqft()));
		agreement.setPropertyLocArabic(StringUtils.trimToEmpty(aAgreementFieldDetails.getPropertyLocation()));
		agreement.setSellerInternalArabic(StringUtils.trimToEmpty(aAgreementFieldDetails.getSellerInternal()));
		agreement.setOtherBankAmtArabic(StringUtils.trimToEmpty(aAgreementFieldDetails.getOtherBankAmt()));
		agreement.setCustJointNameArabic(StringUtils.trimToEmpty(aAgreementFieldDetails.getJointApplicant()));
		agreement.setCollateralArabic(StringUtils.trimToEmpty(aAgreementFieldDetails.getCollateral1()));
		agreement.setCollateralAuthArabic(StringUtils.trimToEmpty(aAgreementFieldDetails.getCollateralAuthority()));
		agreement.setPropertyUseArabic(StringUtils.trimToEmpty(aAgreementFieldDetails.getPropertyUse()));
		agreement.setSellerNationalityArabic(StringUtils.trimToEmpty(aAgreementFieldDetails.getSellerNationality()));
		agreement.setSellerCntbAmtArabic(StringUtils.trimToEmpty(aAgreementFieldDetails.getSellerCntbAmt()));
		agreement.setCustCntAmtArabic(StringUtils.trimToEmpty(aAgreementFieldDetails.getCustCntAmt()));
		agreement.setSellerNameArabic(StringUtils.trimToEmpty(aAgreementFieldDetails.getSellerName()));
		agreement.setSellerAddrArabic(StringUtils.trimToEmpty(aAgreementFieldDetails.getSellerPobox()));

		return agreement;
	}

	private AgreementDetail getFinAssetEvaluationDetails(AgreementDetail agreement,
			FinAssetEvaluation finAssetEvaluation, int formatter, String finCCy) throws Exception {
		agreement
				.setMarketValue(PennantApplicationUtil.amountFormate(finAssetEvaluation.getMarketValueAED(), formatter));
		agreement.setMarketValueInWords(NumberToEnglishWords.getAmountInText(
				PennantApplicationUtil.formateAmount(finAssetEvaluation.getMarketValueAED(), formatter), finCCy)
				.toUpperCase());
		return agreement;
	}

	private AgreementDetail getScheduleDetails(AgreementDetail agreement, FinanceDetail detail, int formatter) {
		List<FinanceScheduleDetail> finschdetails = detail.getFinScheduleData().getFinanceScheduleDetails();
		Date nextRepDate = detail.getFinScheduleData().getFinanceMain().getNextRepayDate();
		String defDates = "";
		int seqNO = 0;
		boolean isSchdPftFirstInst = false;
		agreement.setScheduleData(new ArrayList<AgreementDetail.FinanceScheduleDetail>());
		for (FinanceScheduleDetail finSchDetail : finschdetails) {
			com.pennant.backend.model.finance.AgreementDetail.FinanceScheduleDetail scheduleData = agreement.new FinanceScheduleDetail();
			scheduleData.setSchDate(DateUtility.formatToLongDate(finSchDetail.getSchDate()));
			scheduleData.setSchdPft(PennantApplicationUtil.amountFormate(finSchDetail.getProfitSchd(), formatter));
			scheduleData.setSchdPri(PennantApplicationUtil.amountFormate(finSchDetail.getPrincipalSchd(), formatter));
			scheduleData.setSchTotalPriAmt(PennantApplicationUtil.amountFormate(finSchDetail.getRepayAmount(),
					formatter));
			scheduleData.setClosingBalance(PennantApplicationUtil.amountFormate(finSchDetail.getClosingBalance(),
					formatter));
			scheduleData.setSuplRent(PennantApplicationUtil.amountFormate(finSchDetail.getSuplRent(), formatter));
			scheduleData.setSchdSeqNo(String.valueOf(seqNO++));
			scheduleData.setInsSchd(PennantAppUtil.amountFormate(finSchDetail.getInsSchd(), formatter));
			defDates = defDates.concat(DateUtility.formatToLongDate(finSchDetail.getDefSchdDate()) + ",");
			scheduleData.setSchAdvPft(PennantApplicationUtil.amountFormate(finSchDetail.getAdvProfit(), formatter));
			scheduleData
					.setAdvPayment(PennantApplicationUtil.amountFormate(finSchDetail.getAdvRepayAmount(), formatter));
			agreement.getScheduleData().add(scheduleData);
			if (finSchDetail.isRepayOnSchDate() && !isSchdPftFirstInst) {
				agreement.setSchdPftFirstInstl(String.valueOf(PennantApplicationUtil.amountFormate(
						finSchDetail.getProfitSchd(), formatter)));
				agreement.setSchdAdvPftFirstInstl(String.valueOf(PennantApplicationUtil.amountFormate(
						finSchDetail.getAdvProfit(), formatter)));
				isSchdPftFirstInst = true;
			}
			if (seqNO == 2) {
				agreement.setSecondInstDays(String.valueOf(DateUtility.getDaysBetween(nextRepDate,
						finSchDetail.getSchDate())));
			}

		}
		agreement.setDefPayDate(defDates);
		return agreement;
	}

	private AgreementDetail getCollateralDetails(AgreementDetail agreement, FinanceDetail detail, int formatter) {
		/*List<FinCollaterals> finCollateralslist = detail.getFinanceCollaterals();
		agreement.setCollateralData(new ArrayList<AgreementDetail.FinCollaterals>());

		for (FinCollaterals finCollaterals : finCollateralslist) {
			com.pennant.backend.model.finance.AgreementDetail.FinCollaterals collateralData = agreement.new FinCollaterals();
			collateralData.setCollateralType(finCollaterals.getCollateralType());
			collateralData.setReference(finCollaterals.getReference());
			collateralData.setCollateralAmt(PennantAppUtil.amountFormate(finCollaterals.getValue(), formatter));
			agreement.getCollateralData().add(collateralData);

		}*/
		
		//retrieving from CollateralSetup instead of FinanceCollaterals
		List<CollateralAssignment> collateralAssignmentList = detail.getCollateralAssignmentList();
		if(CollectionUtils.isNotEmpty(collateralAssignmentList)){
			collateralAssignmentList.forEach((collateralAssignment)->{
				if(null!=collateralAssignment){
					CollateralSetup collateralSetup = collateralSetupService.getCollateralSetupByRef(collateralAssignment.getCollateralRef(),"", false);
					if(null!=collateralSetup){
						com.pennant.backend.model.finance.AgreementDetail.FinCollaterals collateralData = agreement.new FinCollaterals();
						collateralData.setCollateralType(StringUtils.trimToEmpty(collateralSetup.getCollateralType()));
						collateralData.setDepositorName(StringUtils.trimToEmpty(collateralSetup.getDepositorName()));
						collateralData.setReference(StringUtils.trimToEmpty(collateralSetup.getCollateralRef()));
						collateralData.setCollateralAmt(PennantAppUtil.amountFormate(collateralSetup.getCollateralValue(),formatter));
						if(null!=collateralSetup.getCollateralStructure()){
							collateralData.setColDesc(StringUtils.trimToEmpty(collateralSetup.getCollateralStructure().getCollateralDesc()));
							collateralData.setColLtv(PennantApplicationUtil.formatRate(
									collateralSetup.getCollateralStructure().getLtvPercentage().doubleValue(), 2));
						}
						collateralData.setColAddrCity(StringUtils.trimToEmpty(collateralSetup.getCollateralLoc()));
						collateralData.setCollateralBankAmt(PennantAppUtil.amountFormate(collateralSetup.getBankValuation(),formatter));			
						agreement.getCollateralData().add(collateralData);
						
						if(CollectionUtils.isEmpty(agreement.getExtendedDetails())){
							agreement.setExtendedDetails(new ArrayList<>());
						}
						List<ExtendedDetailCollateral> extendedDetailsList = new ArrayList<>();
						
						if(CollectionUtils.isNotEmpty(collateralSetup.getExtendedFieldRenderList())){
							int colCcyFormat = CurrencyUtil.getFormat(collateralSetup.getCollateralCcy());
							
							for (ExtendedFieldRender extendedFieldRender : collateralSetup.getExtendedFieldRenderList()) {
								if(null!=extendedFieldRender&&MapUtils.isNotEmpty(extendedFieldRender.getMapValues())){
									ExtendedDetailCollateral detailCol=agreement.new ExtendedDetailCollateral();
									detailCol.setId(String.valueOf(extendedFieldRender.getSeqNo()));
									if(null!=collateralSetup.getCollateralStructure()){
										detailCol.setColType(StringUtils.trimToEmpty(collateralSetup.getCollateralStructure().getCollateralDesc()));
									}
									detailCol.setExtDtls(new ArrayList<>());
									Map<String, Object> mapValues = extendedFieldRender.getMapValues();
									for (String key : mapValues.keySet()) {
										ExtendedDetail extendedDetail = agreement.new ExtendedDetail();
										ExtendedFieldDetail extendedFieldDetail=null;
										if(null!= collateralSetup.getCollateralStructure() && null!= collateralSetup.getCollateralStructure().getExtendedFieldHeader() 
												&& CollectionUtils.isNotEmpty(collateralSetup.getCollateralStructure().getExtendedFieldHeader().getExtendedFieldDetails())){
											List<ExtendedFieldDetail> extendedFieldDetails = collateralSetup.getCollateralStructure().getExtendedFieldHeader().getExtendedFieldDetails();
											List<ExtendedFieldDetail> requiredExtendedFields = extendedFieldDetails.stream().filter(efd -> StringUtils.equalsIgnoreCase(key, efd.getFieldName())).collect(Collectors.toList());
											if(CollectionUtils.isNotEmpty(requiredExtendedFields)){
												extendedFieldDetail=requiredExtendedFields.get(0);
												extendedDetail.setFieldLabel(StringUtils.trimToEmpty(extendedFieldDetail.getFieldLabel()));
											}
										}
										
										extendedDetail.setKey(StringUtils.trimToEmpty(key));
										if (null != mapValues.get(key)) {
											extendedDetail.setValue(StringUtils.trimToEmpty(mapValues.get(key).toString()));
											if(extendedFieldDetail!=null && StringUtils.equalsIgnoreCase("CURRENCY", extendedFieldDetail.getFieldType())){
												extendedDetail.setValue(PennantAppUtil.amountFormate((BigDecimal) mapValues.get(key), colCcyFormat));
											}else{
												extendedDetail.setValue(StringUtils.trimToEmpty(mapValues.get(key).toString()));
											}
										} else {
											extendedDetail.setValue(StringUtils.EMPTY);
										}
										extendedDetail.setFieldType("COLLATERAL");
										detailCol.getExtDtls().add(extendedDetail);
										agreement.getExtendedDetails().add(extendedDetail);
									}
									extendedDetailsList.add(detailCol);
								}
							}
						}
						collateralData.setExtendedDetailsList(extendedDetailsList);
					}
				}
			});
		}
		return agreement;
	}

	private AgreementDetail setMMAgreementGenDetails(AgreementDetail agreement, String appDate, int ccyformatt,
			String mMAReference) throws Exception {
		MMAgreement mMAgreement = getmMAgreementService().getMMAgreementByIdMMARef(mMAReference);

		if (agreement != null) {
			if (mMAgreement != null) {
				agreement.setmMADate(DateUtility.formatToLongDate(mMAgreement.getContractDate()));
				agreement.setCustCIF(mMAgreement.getCustCIF());
				agreement.setCustName(mMAgreement.getCustShrtName());
				agreement.setmMAPurchRegOffice(mMAgreement.getLovDescPurchRegOffice());
				agreement.setmMAContractAmt(PennantApplicationUtil.amountFormate(mMAgreement.getContractAmt(),
						ccyformatt));
				agreement.setmMAPurchaddress(mMAgreement.getPurchaddress());
				agreement.setAttention(mMAgreement.getAttention());
				agreement.setmMAFax(mMAgreement.getFax());
				agreement.setmMARate(String.valueOf(mMAgreement.getRate()));
				agreement.setmMAFOLIssueDate(DateUtility.formatToLongDate(mMAgreement.getfOLIssueDate()));
				agreement.setMaturityDate(DateUtility.formatToLongDate(mMAgreement.getMaturityDate()));
				agreement.setmMAFacilityLimit(PennantApplicationUtil.amountFormate(mMAgreement.getFacilityLimit(),
						ccyformatt));
				String word = NumberToEnglishWords.getAmountInText(
						PennantApplicationUtil.formateAmount(mMAgreement.getFacilityLimit(), ccyformatt),
						agreement.getFinCcy());
				agreement.setFacLimitInWords(word);
				agreement.setmMAMinAmount(PennantApplicationUtil.amountFormate(mMAgreement.getMinAmount(), ccyformatt));
				if (mMAgreement.getProfitRate() != null) {
					agreement.setmMAPftRate(PennantApplicationUtil.formatRate(
							mMAgreement.getProfitRate().doubleValue(), 2));
				}
				agreement.setmMAMargin(PennantApplicationUtil.formatRate(mMAgreement.getMargin().doubleValue(), 9));
				agreement.setmMAMinRate(PennantApplicationUtil.formatRate(mMAgreement.getMinRate().doubleValue(), 9));
				agreement.setmMAMLatePayRate(PennantApplicationUtil.formatRate(mMAgreement.getLatePayRate()
						.doubleValue(), 9));
				agreement.setmMANumberOfTerms(String.valueOf(mMAgreement.getNumberOfTerms()));
				agreement.setmMAProfitPeriod(String.valueOf(mMAgreement.getProfitPeriod()));
				agreement.setFolReference(String.valueOf(mMAgreement.getfOlReference()));
				agreement.setAvlPerDays(String.valueOf(mMAgreement.getAvlPerDays()));
				agreement.setMaxCapProfitRate(PennantApplicationUtil.formatRate(mMAgreement.getMaxCapProfitRate()
						.doubleValue(), 9));
				agreement
						.setMinCapRate(PennantApplicationUtil.formatRate(mMAgreement.getMinCapRate().doubleValue(), 9));
				agreement.setFacOfferLetterDate(DateUtility.formatToLongDate(mMAgreement.getFacOfferLetterDate()));
				agreement.setPmaryRelOfficer(String.valueOf(mMAgreement.getPmaryRelOfficer()));
				agreement.setmMAFOLPeriod(String.valueOf(mMAgreement.getProfitPeriod()));
				agreement.setBaseRateCode(mMAgreement.getBaseRateCode());
			}
		}

		return agreement;
	}

	public AgreementDetail setAdvancePaymentDetails(AgreementDetail agreement, FinAdvancePayments finAdvancePayments,
			int format, String finCcy) throws Exception {
		logger.debug(" Entering ");
		if (agreement != null) {

			if (finAdvancePayments != null) {
				agreement.setOtherBankName(StringUtils.trimToEmpty(finAdvancePayments.getBeneficiaryName()));
				agreement.setCustContribution(PennantAppUtil.amountFormate(finAdvancePayments.getCustContribution(),
						format));
				agreement.setSellerContribution(PennantAppUtil.amountFormate(
						finAdvancePayments.getSellerContribution(), format));
				agreement
						.setOtherBankAmt(PennantAppUtil.amountFormate(finAdvancePayments.getAmtToBeReleased(), format));
				agreement.setLiabilityHoldName(StringUtils.trimToEmpty(finAdvancePayments.getLiabilityHoldName()));
				agreement.setSellerContInWords(NumberToEnglishWords.getAmountInText(
						PennantApplicationUtil.formateAmount(finAdvancePayments.getSellerContribution(), format),
						finCcy));
				agreement.setOtherBankAmtInWords(NumberToEnglishWords.getAmountInText(
						PennantApplicationUtil.formateAmount(finAdvancePayments.getAmtToBeReleased(), format), finCcy));
				agreement.setLlReferenceNo(StringUtils.trimToEmpty(finAdvancePayments.getLlReferenceNo()));
				agreement.setLlDate(DateUtility.formatToLongDate(finAdvancePayments.getLlDate()));
				agreement
						.setCustConInWords(NumberToEnglishWords.getAmountInText(
								PennantApplicationUtil.formateAmount(finAdvancePayments.getCustContribution(), format),
								finCcy));
			}
		}
		return agreement;
	}

	public AgreementDetail setJointAccountDetails(AgreementDetail agreement, GuarantorDetail guarantorDetail,
			int format, FinanceDetail detail) throws Exception {
		logger.debug(" Entering ");
		if (agreement != null) {

			if (guarantorDetail != null) {
				agreement.setGuarantName(StringUtils.trimToEmpty(guarantorDetail.getGuarantorCIFName()));
				agreement.setGuarantHouseNo(StringUtils.trimToEmpty(guarantorDetail.getAddrHNbr()));
				agreement.setGuarantStreet(StringUtils.trimToEmpty(guarantorDetail.getAddrStreet()));
				agreement.setGuarantPO(StringUtils.trimToEmpty(guarantorDetail.getAddrZIP()));
				agreement.setGuarantCountry(StringUtils.trimToEmpty(guarantorDetail.getLovDescAddrCountryName()));
				agreement.setGuarantProvince(StringUtils.trimToEmpty(guarantorDetail.getLovDescAddrProvinceName()));
				agreement.setGuranteeNum(StringUtils.trimToEmpty(guarantorDetail.getGuarantorIDNumber()));
				BigDecimal guarnteeAmt = (guarantorDetail.getGuranteePercentage().multiply(detail.getFinScheduleData()
						.getFinanceMain().getFinAmount())).divide(new BigDecimal(100));
				agreement.setGuranteeAmt(PennantApplicationUtil.amountFormate(guarnteeAmt, format));
				agreement.setGuranteeAmtInWords(NumberToEnglishWords.getAmountInText(
						PennantApplicationUtil.formateAmount(guarnteeAmt, format), detail.getFinScheduleData()
								.getFinanceMain().getFinCcy()));
				int days = DateUtility.getDaysBetween(detail.getFinScheduleData().getFinanceMain().getFinStartDate(),
						detail.getFinScheduleData().getFinanceMain().getMaturityDate());
				agreement.setGuranteeDays(String.valueOf(days));
				agreement.setGuranteeEndDate(DateUtility.formatToLongDate(detail.getFinScheduleData().getFinanceMain()
						.getMaturityDate()));
			}
		}
		return agreement;
	}

	/**
	 * Method for Preparing Finance Basic Details Data
	 * 
	 * @param agreement
	 * @param detail
	 * @return
	 */
	private AgreementDetail getFinanceDetails(AgreementDetail agreement, FinanceDetail detail) {
		logger.debug("Entering");

		try {
			FinanceMain main = detail.getFinScheduleData().getFinanceMain();
			FinanceType type = detail.getFinScheduleData().getFinanceType();

			int formatter = CurrencyUtil.getFormat(main.getFinCcy());
			agreement.setFinType(type.getFinType());
			agreement.setFinTypeDesc(StringUtils.trimToEmpty(type.getFinTypeDesc()));
			agreement.setFinDivision(StringUtils.trimToEmpty(type.getFinDivision()));
			agreement.setInitiatedDate(DateUtility.formatToLongDate(main.getInitiateDate()));
			
			agreement.setNextInstDate(DateUtility.formatToLongDate(main.getNextRepayDate()));
			agreement.setRepayAmount(PennantApplicationUtil.amountFormate(main.getFirstRepay(), formatter));
			agreement.setProfitRate(PennantApplicationUtil.formatRate(main.getRepayProfitRate().doubleValue(), 2));
			agreement.setTotPriAmount(PennantApplicationUtil.amountFormate(main.getTotalPriAmt(), formatter));
			agreement.setFinRef(main.getFinReference());
			agreement.setFinCcy(main.getFinCcy());
			agreement.setPftDaysBasis(main.getProfitDaysBasis());
			agreement.setFinBranch(main.getFinBranch());
			agreement.setFinBranchName(main.getLovDescFinBranchName());
			
			
			if (StringUtils.isNotBlank(main.getFinBranch())) {
				if (branchService != null) {
					Branch branchdetails = branchService.getApprovedBranchById(main.getFinBranch());
					if (branchdetails!=null) {
						agreement.setFinBranchAddrHNbr(branchdetails.getBranchAddrHNbr());
						agreement.setFinBranchFlatNbr(branchdetails.getBranchFlatNbr());
						agreement.setFinBranchAddrStreet(branchdetails.getBranchAddrStreet());
						agreement.setFinBranchAddrLine1(branchdetails.getBranchAddrLine1());
						agreement.setFinBranchAddrLine2(branchdetails.getBranchAddrLine2());
						agreement.setFinBranchPOBox(branchdetails.getBranchPOBox());
						agreement.setFinBranchAddrCountry(branchdetails.getBranchCountry());
						agreement.setFinBranchAddrCountryName(branchdetails.getLovDescBranchCountryName());
						agreement.setFinBranchAddrProvince(branchdetails.getBranchProvince());
						agreement.setFinBranchAddrProvinceName(branchdetails.getLovDescBranchProvinceName());
						agreement.setFinBranchAddrCity(branchdetails.getBranchCity());
						agreement.setFinBranchAddrCityName(branchdetails.getLovDescBranchCityName());
						agreement.setFinBranchAddrZIP(branchdetails.getPinCode());
						agreement.setFinBranchAddrPhone(branchdetails.getBranchTel());
					}

				}

			}
			agreement.setRepayRateBasis(PennantStaticListUtil.getlabelDesc(main.getRepayRateBasis(), interestRateType));
			agreement.setStartDate(DateUtility.formatToLongDate(main.getFinStartDate()));
			agreement.setRepayFrqDay(FrequencyUtil.getFrequencyDay(main.getRepayFrq()));
			if (main.getFinStartDate() != null) {
				agreement.setMM(String.valueOf(DateUtility.getMonth(main.getFinStartDate())));
				agreement.setDD(String.valueOf(DateUtility.getDay(main.getFinStartDate())));
				String year = String.valueOf(DateUtility.getYear(main.getFinStartDate()));
				agreement.setYY(year.substring(2, 4));

			}
			agreement.setContractDate(DateUtility.formatToLongDate(main.getFinContractDate()));
			agreement.setFinAmount(PennantApplicationUtil.amountFormate(main.getFinAssetValue(), formatter));
			String finAmount = NumberToEnglishWords.getAmountInText(
					PennantApplicationUtil.formateAmount(main.getFinAmount(), CurrencyUtil.getFormat(main.getFinCcy())),
					main.getFinCcy());
			agreement.setFinAmountInWords(finAmount == null ? "" : finAmount.toUpperCase());
			agreement.setFeeChargeAmt(PennantApplicationUtil.amountFormate(main.getFeeChargeAmt(), formatter));
			agreement.setInsuranceAmt(PennantApplicationUtil.amountFormate(main.getInsuranceAmt(), formatter));
			agreement.setDownPayment(PennantApplicationUtil.amountFormate(main.getDownPayment(), formatter));
			agreement.setDownPayBank(PennantApplicationUtil.amountFormate(main.getDownPayBank(), formatter));
			agreement.setDownPaySupl(PennantApplicationUtil.amountFormate(main.getDownPaySupl(), formatter));
			agreement.setDisbAccount(PennantApplicationUtil.formatAccountNumber(main.getDisbAccountId()));
			agreement.setRepayAccount(PennantApplicationUtil.formatAccountNumber(main.getRepayAccountId()));
			agreement.setDownpayAc(PennantApplicationUtil.formatAccountNumber(main.getDownPayAccount()));
			agreement.setFinPurpose(main.getLovDescFinPurposeName());
			agreement.setFinRpyMethod(main.getFinRepayMethod());
			agreement.setFacilityRef(main.getFinCommitmentRef());
			agreement.setGrcEndDate(DateUtility.formatToLongDate(main.getGrcPeriodEndDate()));
			agreement.setLpoPrice(PennantApplicationUtil.amountFormate(
					main.getFinAmount().subtract(main.getDownPaySupl()), formatter));
			agreement.setFormatter(formatter);
			String word = NumberToEnglishWords.getAmountInText(
					PennantApplicationUtil.formateAmount(main.getFinAmount().subtract(main.getDownPaySupl()),
							CurrencyUtil.getFormat(main.getFinCcy())), main.getFinCcy());
			agreement.setLpoPriceInWords(word.toUpperCase());

			agreement.setNoOfPayments(String.valueOf(main.getNumberOfTerms()));
			String repay = FrequencyUtil.getFrequencyDetail(main.getRepayFrq()).getFrequencyDescription();
			agreement.setRepayFrq(repay.substring(0, repay.indexOf(",")));
			agreement.setRepayFrqCode(main.getRepayFrq().substring(0, 1));
			agreement.setRpyRateBasis(PennantAppUtil.getlabelDesc(main.getRepayRateBasis(),
					PennantStaticListUtil.getAccountTypes()));
			agreement.setFirstInstDate(DateUtility.formatToLongDate(main.getNextRepayDate()));
			agreement.setLastInstDate(DateUtility.formatToLongDate(main.getMaturityDate()));
			agreement.setFirstInstAmount(PennantApplicationUtil.amountFormate(main.getFirstRepay(), formatter));
			agreement.setLastInstAmount(PennantApplicationUtil.amountFormate(main.getLastRepay(), formatter));
			agreement.setSchdMethod(main.getScheduleMethod());
			agreement
					.setEffPftRate(PennantApplicationUtil.formatRate(main.getEffectiveRateOfReturn().doubleValue(), 2));
			agreement.setMaturityDate(DateUtility.formatToLongDate(main.getMaturityDate()));
			agreement.setProfit(String.valueOf(PennantApplicationUtil.amountFormate(main.getTotalProfit(), formatter)));
			agreement.setBankName(StringUtils.trimToEmpty(main.getBankName()));
			agreement.setAccountType(StringUtils.trimToEmpty(main.getAccountType()));
			agreement.setIban(StringUtils.trimToEmpty(main.getIban()));
			agreement.setDdaPurposeCode(PennantConstants.REQ_TYPE_REG);

			agreement.setCustDSR(String.valueOf(main.getCustDSR() == null ? "0.00" : main.getCustDSR()));
			agreement.setAssetValue(PennantApplicationUtil.amountFormate(main.getFinAmount(), formatter));
			agreement.setSchdInst(String.valueOf(main.getNumberOfTerms()));
			agreement.setIfscCode(main.getIfscCode());
			agreement.setSecSixTermAmt(PennantApplicationUtil.amountFormate(
					main.getTotalRepayAmt().divide(new BigDecimal(6), RoundingMode.HALF_DOWN), formatter));
			agreement.setTotalRepayAmt(PennantApplicationUtil.amountFormate(main.getTotalRepayAmt(), formatter));
			agreement.setFinMinRate("");
			if (main.getRpyMinRate() != null) {
				agreement.setFinMinRate(PennantApplicationUtil.formatRate(main.getRpyMinRate().doubleValue(), 2));
			}
			agreement.setFinMaxRate("");
			if (main.getRpyMinRate() != null) {
				agreement.setFinMaxRate(PennantApplicationUtil.formatRate(main.getRpyMaxRate().doubleValue(), 2));
			}
			if(null!=main.getRepayBaseRate()){
				agreement.setRepayBaseRate(main.getRepayBaseRate());
			}
			if(null!=main.getRepayBaseRateVal()){
				agreement.setRepayBaseRateVal(PennantApplicationUtil.formatRate(main.getRepayBaseRateVal().doubleValue(), 2));
			}
			agreement.setFinAmtPertg(PennantApplicationUtil.amountFormate(
					main.getFinAmount().multiply(new BigDecimal(125))
							.divide(new BigDecimal(100), RoundingMode.HALF_DOWN), formatter));
			agreement.setPurchasePrice(PennantApplicationUtil.amountFormate(
					main.getFinAmount().subtract(main.getDownPayment()), formatter));
			agreement.setRepayMargin(PennantApplicationUtil.formatRate(main.getRepayMargin().doubleValue(), 2));
			agreement.setSecDeposit(PennantApplicationUtil.amountFormate(main.getSecurityDeposit(), formatter));
			agreement.setSharePerc(PennantApplicationUtil.amountFormate(
					(main.getFinAmount().subtract(main.getDownPayment())).divide(
							main.getFinAmount().multiply(new BigDecimal(100)), RoundingMode.HALF_DOWN), formatter));
			agreement.setFacilityAmt(PennantApplicationUtil.amountFormate(main.getAvailCommitAmount(), formatter));
			agreement.setTotalPrice(PennantApplicationUtil.amountFormate(
					main.getFinAmount().add(main.getTotalProfit()), formatter));
			List<FeeRule> feeChargesList = detail.getFinScheduleData().getFeeRules();
			BigDecimal totalExpAmt = BigDecimal.ZERO;
			BigDecimal insAmt = BigDecimal.ZERO;
			for (FeeRule fee : feeChargesList) {
				totalExpAmt = totalExpAmt.add(fee.getFeeAmount().subtract(fee.getWaiverAmount())
						.subtract(fee.getPaidAmount()));
				if (StringUtils.equals(fee.getFeeCode(), RuleConstants.TAKAFUL_FEE)
						|| StringUtils.equals(fee.getFeeCode(), RuleConstants.AUTOINS_FEE)) {
					insAmt = (fee.getCalFeeAmount().subtract(fee.getWaiverAmount())).subtract(fee.getPaidAmount());
				}
			}
			agreement.setInsAmt(PennantApplicationUtil.amountFormate(insAmt, formatter));
			agreement.setTotalExpAmt(PennantApplicationUtil.amountFormate(totalExpAmt, formatter));
			agreement.setFirstInstDays(String.valueOf(DateUtility.getDaysBetween(main.getFinStartDate(),
					main.getNextRepayDate())));
			agreement.setReviewDate(DateUtility.formatToLongDate(main.getNextRepayRvwDate()));
			agreement.setFacilityDate(DateUtility.formatToLongDate(main.getFinStartDate()));
			agreement.setProfitRateType(main.getRepayRateBasis());
			int tenor = DateUtility.getMonthsBetween(main.getFinStartDate(),
					main.getMaturityDate(), true);
			agreement.setTenor(String.valueOf(tenor));
			
			if (main.getRepayBaseRate()!=null) {
				RateDetail details = RateUtil.rates(main.getRepayBaseRate(), main.getFinCcy(), main.getRepaySpecialRate(),
						main.getRepayMargin(),main.getRpyMinRate(), main.getRpyMaxRate());
				agreement.setNetRefRateLoan(PennantApplicationUtil.formatRate(details.getNetRefRateLoan().doubleValue(), 2));
			}else{
				agreement.setNetRefRateLoan(PennantApplicationUtil.formatRate(main.getRepayProfitRate().doubleValue(), 2));
			}
			
			int totalTerms=main.getNumberOfTerms()+main.getGraceTerms();
			agreement.setTotalTerms(Integer.toString(totalTerms));
		
			//TODO: Need Confirmation
			agreement.setApplicationNo(main.getApplicationNo());

		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
		return agreement;
	}

	/**
	 * To get the check List details
	 * 
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
					com.pennant.backend.model.finance.AgreementDetail.CheckListDetails checkListDetails = agreement.new CheckListDetails();
					checkListDetails.setQuestionId(checkListReference.getFinRefId());
					checkListDetails.setQuestion(checkListReference.getLovDescRefDesc());
					checkListDetails.setListquestionAns(new ArrayList<AgreementDetail.CheckListAnsDetails>());
					for (CheckListDetail checkListDetail : checkListReference.getLovDesccheckListDetail()) {
						com.pennant.backend.model.finance.AgreementDetail.CheckListAnsDetails ansDetails = agreement.new CheckListAnsDetails();
						ansDetails.setQuestionId(checkListReference.getFinRefId());
						ansDetails.setQuestionAns(checkListDetail.getAnsDesc());
						if (detail.getFinanceCheckList() != null && !detail.getFinanceCheckList().isEmpty()) {
							for (FinanceCheckListReference financeCheckList : detail.getFinanceCheckList()) {
								if (financeCheckList.getQuestionId() == checkListReference.getFinRefId()
										&& financeCheckList.getAnswer() == checkListDetail.getAnsSeqNo()) {
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

			if (agreement.getCheckListDetails() == null) {
				agreement.setCheckListDetails(new ArrayList<AgreementDetail.CheckListDetails>());
				com.pennant.backend.model.finance.AgreementDetail.CheckListDetails checkListDetails = agreement.new CheckListDetails();
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
	 * 
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
			List<Notes> list = getNotesService().getNotesForAgreements(note);
			if (list != null && !list.isEmpty()) {
				agreement.setRecommendations(new ArrayList<AgreementDetail.Recommendation>(list.size()));
				for (Notes notes : list) {
					com.pennant.backend.model.finance.AgreementDetail.Recommendation recommendation = agreement.new Recommendation();
					String noteType = "";
					if (notes.getRemarkType().equals(PennantConstants.NOTES_TYPE_COMMENT)) {
						noteType = Labels.getLabel("common.noteType.Comment");
					} else if (notes.getRemarkType().equals(PennantConstants.NOTES_TYPE_RECOMMEND)) {
						noteType = Labels.getLabel("common.noteType.Recommend");
					}
					recommendation.setNoteType(noteType);
					recommendation.setNoteDesc(notes.getRemarks());
					recommendation.setCommentedDate(DateUtility.formatUtilDate(notes.getInputDate(),
							PennantConstants.dateTimeAMPMFormat));
					recommendation.setUserName(notes.getUsrLogin().toUpperCase());
					agreement.getRecommendations().add(recommendation);
				}
			} else {
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
	 * 
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
						agreement
								.setExceptionLists(new ArrayList<AgreementDetail.ExceptionList>(eligibilityList.size()));
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

	/**
	 * Method for Preparation of Recommendations as Group wise
	 * 
	 * @param agreement
	 * @param finreference
	 * @return
	 */
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
					if (StringUtils.isNotEmpty(notes.getRoleCode())) {
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
						com.pennant.backend.model.finance.AgreementDetail.Recommendation recommendation = agreement.new Recommendation();
						String noteType = "";
						if (notes.getRemarkType().equals(PennantConstants.NOTES_TYPE_COMMENT)) {
							noteType = Labels.getLabel("common.noteType.Comment");
						} else if (notes.getRemarkType().equals(PennantConstants.NOTES_TYPE_RECOMMEND)) {
							noteType = Labels.getLabel("common.noteType.Recommend");
						}
						recommendation.setNoteType(noteType);
						recommendation.setNoteDesc(notes.getRemarks());
						recommendation.setCommentedDate(DateUtility.formatUtilDate(notes.getInputDate(),
								PennantConstants.dateTimeAMPMFormat));
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
				com.pennant.backend.model.finance.AgreementDetail.Recommendation recommendation = agreement.new Recommendation();
				groupRecommendation.getRecommendations().add(recommendation);
				agreement.getGroupRecommendations().add(groupRecommendation);
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
		return agreement;
	}

	/**
	 * To get Customer Finance Details
	 * 
	 * @param agreement
	 * @return
	 */
	private AgreementDetail getCustomerFinanceDetails(AgreementDetail agreement, int formatter) {
		logger.debug("Entering");
		try {
			List<FinanceSummary> financeMains = getFinanceDetailService().getFinExposureByCustId(agreement.getCustId());
			if (financeMains != null && !financeMains.isEmpty()) {
				agreement.setCustomerFinances(new ArrayList<AgreementDetail.CustomerFinance>());
				BigDecimal tot = BigDecimal.ZERO;
				for (FinanceSummary summary : financeMains) {
					int format = CurrencyUtil.getFormat(summary.getFinCcy());
					
					CustomerFinance customerFinance = agreement.new CustomerFinance();
					customerFinance.setDealDate(DateUtility.formatToLongDate(summary.getFinStartDate()));
					customerFinance.setDealType(summary.getFinType() + "-" + summary.getFinReference());
					customerFinance.setOriginalAmount(PennantApplicationUtil.amountFormate(summary.getTotalOriginal(),
							format));
					int installmentMnts = DateUtility.getMonthsBetween(summary.getFinStartDate(),
							summary.getMaturityDate(), true);
					customerFinance.setMonthlyInstalment(PennantApplicationUtil.amountFormate(summary
							.getTotalRepayAmt().divide(new BigDecimal(installmentMnts), RoundingMode.HALF_DOWN),
							format));
					customerFinance.setOutstandingBalance(PennantApplicationUtil.amountFormate(
							summary.getTotalOutStanding(), format));
					tot = tot.add(summary.getTotalOutStanding());
					agreement.getCustomerFinances().add(customerFinance);

				}
				agreement.setTotCustFin(PennantApplicationUtil.amountFormate(tot, formatter));
			} else {
				agreement.setCustomerFinances(new ArrayList<AgreementDetail.CustomerFinance>());
				agreement.getCustomerFinances().add(agreement.new CustomerFinance());
				agreement.setTotCustFin(PennantApplicationUtil.amountFormate(BigDecimal.ZERO, formatter));
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
		return agreement;
	}

	/**
	 * @param detail
	 * @param data
	 * @param userId
	 * @param usrName
	 */
	public void prepareAgreementDataJoinsCust(FinanceDetail detail, FinanceReferenceDetail data, User userDetails) {
		logger.debug(" Entering ");
		try {

			String finpurpose = detail.getFinScheduleData().getFinanceMain().getFinPurpose();
			int formatter = CurrencyUtil.getFormat(detail.getFinScheduleData().getFinanceMain().getFinCcy());

			List<GuarantorDetail> list = detail.getGurantorsDetailList();

			List<AgreementDetail> listtoGenerate = new ArrayList<AgreementDetail>();
			if (list != null && !list.isEmpty()) {
				// to get the other date related to customer and finance
				AgreementDetail agreementDetail = getAggrementData(detail, data.getLovDescAggImage(), userDetails);

				for (GuarantorDetail guarantorDetail : list) {
					// Prepare to new object for each individual record to
					// generate as separate files
					AgreementDetail detailsToSend = new AgreementDetail();
					BeanUtils.copyProperties(agreementDetail, detailsToSend);
					detailsToSend = setJointAccountDetails(detailsToSend, guarantorDetail, formatter, detail);
					listtoGenerate.add(detailsToSend);
				}

			} else {
				AgreementDetail detailsToSend = new AgreementDetail();
				AgreementDetail agreementDetail = getAggrementData(detail, data.getLovDescAggImage(), userDetails);
				detailsToSend = setJointAccountEmptyDetails(agreementDetail);
				listtoGenerate.add(detailsToSend);
			}
			// generate agreement
			getGenerateAgreementsWithMultiplePages(data, listtoGenerate, finpurpose);

		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug(" Leaving ");
	}

	/**
	 * When Advence Payment Detials not Available then Advence Payment Detials Set Empty Data in Agreements
	 * 
	 * @param agreement
	 */
	private AgreementDetail setJointAccountEmptyDetails(AgreementDetail agreement) {
		agreement.setGuarantName("");
		agreement.setGuarantHouseNo("");
		agreement.setGuarantStreet("");
		agreement.setGuarantPO("");
		agreement.setGuarantCountry("");
		agreement.setGuarantProvince("");
		agreement.setGuranteeNum("");
		agreement.setGuranteeAmt("");
		agreement.setGuranteeAmtInWords("");
		agreement.setGuranteeDays("");
		agreement.setGuranteeEndDate("");
		return agreement;
	}

	/**
	 * @param detail
	 * @param data
	 * @param userId
	 * @param usrName
	 */
	public void prepareAdvancePaymentAgreementData(FinanceDetail detail, FinanceReferenceDetail data,
			User userDetails) {
		logger.debug(" Entering ");
		try {

			String finpurpose = detail.getFinScheduleData().getFinanceMain().getFinPurpose();
			int formatter = CurrencyUtil.getFormat(detail.getFinScheduleData().getFinanceMain().getFinCcy());
			String finCcy = detail.getFinScheduleData().getFinanceMain().getFinCcy();
			List<FinAdvancePayments> list = detail.getAdvancePaymentsList();

			List<AgreementDetail> listtoGenerate = new ArrayList<AgreementDetail>();
			if (list != null && !list.isEmpty()) {
				// to get the other date related to customer and finance
				AgreementDetail agreementDetail = getAggrementData(detail, data.getLovDescAggImage(), userDetails);

				for (FinAdvancePayments finAdvancePayments : list) {
					// Prepare to new object for each individual record to
					// generate as separate files

					AgreementDetail detailsToSend = new AgreementDetail();
					BeanUtils.copyProperties(agreementDetail, detailsToSend);
					detailsToSend = setAdvancePaymentDetails(detailsToSend, finAdvancePayments, formatter, finCcy);
					listtoGenerate.add(detailsToSend);

				}

			} else {
				AgreementDetail detailsToSend = new AgreementDetail();
				AgreementDetail agreementDetail = getAggrementData(detail, data.getLovDescAggImage(), userDetails);
				detailsToSend = setAdvancePayEmptyDetails(agreementDetail);
				listtoGenerate.add(detailsToSend);
			}
			// generate agreement
			getGenerateAgreementsWithMultiplePages(data, listtoGenerate, finpurpose);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug(" Leaving ");
	}

	/**
	 * When Advence Payment Detials not Available then Advence Payment Detials Set Empty Data in Agreements
	 * 
	 * @param agreement
	 */

	private AgreementDetail setAdvancePayEmptyDetails(AgreementDetail agreement) {
		agreement.setOtherBankName("");
		agreement.setCustContribution("");
		agreement.setSellerContribution("");
		agreement.setOtherBankAmt("");
		agreement.setLiabilityHoldName("");
		agreement.setSellerContInWords("");
		agreement.setOtherBankAmtInWords("");
		agreement.setLlReferenceNo("");
		agreement.setLlDate("");
		agreement.setCustConInWords("");
		return agreement;
	}
	
	/**
	 * To get the description form extended combobox in Extended details
	 * @param detail
	 * @return
	 */
	public void setExtendedMasterDescription(FinanceDetail detail, AgreementEngine engine) {
		logger.debug(" Entering ");
		try {
			ExtendedFieldHeader header = detail.getExtendedFieldHeader();
			List<ExtendedFieldDetail> list = header.getExtendedFieldDetails();
			Map<String, Object> extendedData = null;
			if(detail.getExtendedFieldRender() != null) {
				extendedData = detail.getExtendedFieldRender().getMapValues();
			} else {
				extendedData = new WeakHashMap<>();
			}

			//extended fields in merge
			String[] keys = extendedData.keySet().toArray(new String[extendedData.size()]);
			Object[] values = extendedData.values().toArray(new Object[extendedData.size()]);
			engine.mergeFields(keys, values);

			Map<String, String> map = new HashMap<>();

			//extended fields description in merge
			for (ExtendedFieldDetail extFieldDetail : list) {
				if (extFieldDetail.getFieldType().equals(ExtendedFieldConstants.FIELDTYPE_EXTENDEDCOMBO)) {

					try {
						ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap(extFieldDetail.getFieldList());
						Search search = new Search();
						search.setSearchClass(moduleMapping.getModuleClass());
						List<Filter> filters = new ArrayList<>();
						if (moduleMapping.getLovFilters() != null) {
							Object[][] condArray = moduleMapping.getLovFilters();
							Filter filter1;
							for (int i = 0; i < condArray.length; i++) {
								String property = (String) condArray[i][0];
								Object value = condArray[i][2];
								int filtertype = Integer.parseInt((String) condArray[i][1]);
								filter1 = new Filter(property, value, filtertype);
								filters.add(filter1);
							}
						}

						if (moduleMapping.getLovFields() != null) {
							String[] condArray = moduleMapping.getLovFields();
							Filter filter1;
							String fieldName = extFieldDetail.getFieldName();
							filter1 = new Filter((String) condArray[0], extendedData.get(fieldName), Filter.OP_EQUAL);
							filters.add(filter1);
							search.setFilters(filters);
							List<Object> result = searchProcessor.getResults(search);
							for (Object object2 : result) {
								String descMethod = "get" + condArray[1];
								String desc = object2.getClass().getMethod(descMethod).invoke(object2).toString();
								map.put(fieldName + "_Desc", StringUtils.trimToEmpty(desc));
							}
						}
					} catch (Exception e) {
						logger.error(e);
					}
				}
			}

			if (!map.isEmpty()) {
				String[] desckeys = map.keySet().toArray(new String[map.size()]);
				Object[] descvalues = map.values().toArray(new String[map.size()]);
				engine.mergeFields(desckeys, descvalues);
			}

		} catch (Exception e) {
			logger.error(e);
		}
		logger.debug(" Leaving ");
	}
	
	public void setFeeDetails(FinanceDetail detail, AgreementEngine engine) throws Exception {
		List<FinFeeDetail> feelist = detail.getFinScheduleData().getFinFeeDetailList();
		String finCcy = detail.getFinScheduleData().getFinanceMain().getFinCcy();
		Map<String, String> map = new HashMap<>();
		if (feelist !=null && !feelist.isEmpty()) {
			for (FinFeeDetail finFeeDetail : feelist) {
				BigDecimal actAmount = finFeeDetail.getActualAmount();
				map.put(finFeeDetail.getFeeTypeCode(), PennantApplicationUtil.amountFormate(actAmount, CurrencyUtil.getFormat(finCcy)));
			}
		}
		
		if (!map.isEmpty()) {
			String[] desckeys = map.keySet().toArray(new String[map.size()]);
			Object[] descvalues = map.values().toArray(new String[map.size()]);
			engine.mergeFields(desckeys, descvalues);
		}
		
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setNotesService(NotesService notesService) {
		this.notesService = notesService;
	}

	public NotesService getNotesService() {
		return notesService;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public MMAgreementService getmMAgreementService() {
		return mMAgreementService;
	}

	public void setmMAgreementService(MMAgreementService mMAgreementService) {
		this.mMAgreementService = mMAgreementService;
	}

	public CollateralSetupService getCollateralSetupService() {
		return collateralSetupService;
	}

	public void setCollateralSetupService(CollateralSetupService collateralSetupService) {
		this.collateralSetupService = collateralSetupService;
	}

	public SecurityUserService getSecurityUserService() {
		return securityUserService;
	}

	public void setSecurityUserService(SecurityUserService securityUserService) {
		this.securityUserService = securityUserService;
	}

	public ActivityLogService getActivityLogService() {
		return activityLogService;
	}

	public void setActivityLogService(ActivityLogService activityLogService) {
		this.activityLogService = activityLogService;
	}
}
