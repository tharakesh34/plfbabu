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
import java.util.WeakHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.MMAgreement.MMAgreement;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.customermasters.CustEmployeeDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.finance.AgreementDetail;
import com.pennant.backend.model.finance.AgreementDetail.CoApplicant;
import com.pennant.backend.model.finance.AgreementDetail.CustomerFinance;
import com.pennant.backend.model.finance.AgreementDetail.ExceptionList;
import com.pennant.backend.model.finance.AgreementDetail.GroupRecommendation;
import com.pennant.backend.model.finance.AgreementFieldDetails;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinAssetEvaluation;
import com.pennant.backend.model.finance.FinCollaterals;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceScoreHeader;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.service.NotesService;
import com.pennant.backend.service.applicationmaster.MMAgreementService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennanttech.framework.security.core.User;
import com.pennanttech.pennapps.core.feature.model.ModuleMapping;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;

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
	private SearchProcessor searchProcessor;

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

		TemplateEngine engine = new TemplateEngine(finPurpose);
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

				TemplateEngine engine = new TemplateEngine(finPurpose);
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
				TemplateEngine engine = new TemplateEngine(finPurpose);
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

		try {

			// ------------------ Customer Details
			if (aggModuleDetails.contains(PennantConstants.AGG_BASICDE)) {

				if (detail.getCustomerDetails() != null && detail.getCustomerDetails().getCustomer() != null) {
					Customer customer = detail.getCustomerDetails().getCustomer();
					agreement.setCustId(customer.getCustID());
					agreement.setCustCIF(customer.getCustCIF());
					agreement.setCustName(customer.getCustShrtName());
					agreement.setCustArabicName(customer.getCustShrtNameLclLng());
					agreement.setCustPassport(customer.getCustPassportNo());
					agreement.setCustDOB(DateUtility.formatToLongDate(customer.getCustDOB()));
					agreement.setCustRO1(customer.getLovDescCustRO1Name());
					agreement.setCustNationality(customer.getLovDescCustNationalityName());
					agreement.setCustCPRNo(customer.getCustCRCPR());
					agreement.setCustAge(String.valueOf(DateUtility.getYearsBetween(appldate, customer.getCustDOB())));
					agreement.setCustTotIncome(PennantApplicationUtil.amountFormate(customer.getCustTotalIncome(),
							formatter));
					agreement.setCustTotExpense(PennantApplicationUtil.amountFormate(customer.getCustTotalExpense(),
							formatter));
					agreement.setNoOfDependents(String.valueOf(customer.getNoOfDependents()));
					agreement.setCustSector(customer.getCustSector());
					agreement.setCustSubSector(StringUtils.trimToEmpty(customer.getCustSubSector()));
					agreement.setCustSegment(customer.getCustSegment());
					agreement.setCustIndustry(customer.getLovDescCustIndustryName());
					agreement.setCustJointDOB(DateUtility.formatToLongDate(customer.getJointCustDob()));
					agreement.setCustJointName(StringUtils.trimToEmpty(customer.getJointCustName()));
					agreement.setCustSalutation(StringUtils.trimToEmpty(customer.getLovDescCustSalutationCodeName()));
					agreement.setCustGender(StringUtils.trimToEmpty(customer.getLovDescCustGenderCodeName()));
					agreement.setCustCtgCode(StringUtils.trimToEmpty(customer.getCustCtgCode()));

					// Customer Employment Details
					if (detail.getCustomerDetails().getCustEmployeeDetail() != null) {
						CustEmployeeDetail empDetail = detail.getCustomerDetails().getCustEmployeeDetail();
						agreement.setCustEmpName(empDetail.getLovDescEmpName());
						agreement.setCustYearsExp(String.valueOf(DateUtility.getYearsBetween(appldate,
								empDetail.getEmpFrom())));
						agreement.setCustEmpStartDate(DateUtility.formatToLongDate(empDetail.getEmpFrom()));
						agreement.setCustEmpProf(StringUtils.trimToEmpty(empDetail.getLovDescProfession()));
						agreement.setCustEmpStsDesc(StringUtils.trimToEmpty(empDetail.getLovDescEmpStatus()));
						agreement.setCustOccupation(StringUtils.trimToEmpty(empDetail.getLovDescEmpDesg()));
					}

					// Customer Phone Numbers
					if (detail.getCustomerDetails().getCustomerPhoneNumList() != null) {
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
					}

					if (detail.getCustomerDetails().getCustomerEMailList() != null
							&& !detail.getCustomerDetails().getCustomerEMailList().isEmpty()) {
						for (CustomerEMail email : detail.getCustomerDetails().getCustomerEMailList()) {
							if (PennantConstants.PFF_CUSTCTG_CORP.equals(detail.getCustomerDetails().getCustomer()
									.getCustCtgCode())) {
								if ("OFFICE".equals(email.getCustEMailTypeCode())) {
									agreement.setCustEmail(StringUtils.trimToEmpty(email.getCustEMail()));
									break;
								}
							} else {
								if ("PERSON1".equals(email.getCustEMailTypeCode())) {
									agreement.setCustEmail(StringUtils.trimToEmpty(email.getCustEMail()));
									break;
								}
							}
						}
					}
					
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

			// --------------------Collateral Details
			if (detail.getFinanceCollaterals() != null && !detail.getFinanceCollaterals().isEmpty()) {
				agreement = getCollateralDetails(agreement, detail, formatter);
			}
			if (detail.getFinRepayHeader() != null) {
				agreement = getFinRepayHeaderDetails(agreement, detail.getFinRepayHeader(), formatter);
			}

			if (detail.getAgreementFieldDetails() != null) {
				agreement = getAgreementArabicFieldDetails(agreement, detail.getAgreementFieldDetails(), formatter);
			}


			// -----------------Customer Credit Review Details
			if (aggModuleDetails.contains(PennantConstants.AGG_CRDTRVW)) {
			}

			// -----------------Scoring Detail
			if (aggModuleDetails.contains(PennantConstants.AGG_SCOREDE)) {
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

			// -------------------Recommendations
			if (aggModuleDetails.contains(PennantConstants.AGG_RECOMMD)) {
				agreement = getRecommendations(agreement, finRef);
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
			setCoapplicantDetails(detail, agreement);
			//Mandate details
			setMandateDetails(detail, agreement);
			//customer bank info
			setCustomerBankInfo(detail, agreement);
			
			
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
		return agreement;
	}

	private void setCustomerBankInfo(FinanceDetail detail, AgreementDetail agreement) {
		agreement.setCustomerBankInfos(new ArrayList<com.pennant.backend.model.finance.AgreementDetail.CustomerBankInfo>());
		if (detail!=null && detail.getCustomerDetails()!=null && detail.getCustomerDetails().getCustomerBankInfoList()!=null) {
			List<CustomerBankInfo> list = detail.getCustomerDetails().getCustomerBankInfoList();
			for (CustomerBankInfo customerBankInfo : list) {
				com.pennant.backend.model.finance.AgreementDetail.CustomerBankInfo custbank = agreement.new CustomerBankInfo();
				custbank.setBankCode(customerBankInfo.getBankCode());
				custbank.setBankName(customerBankInfo.getLovDescBankName());
				custbank.setAccountType(customerBankInfo.getAccountType());
				custbank.setAccountNumber(customerBankInfo.getAccountNumber());
				agreement.getCustomerBankInfos().add(custbank);
			}
			
		}else{
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
			agreement.setAccNumberMandate(detail.getMandate().getAccNumber());
		}
		
	}

	private void setCoapplicantDetails(FinanceDetail detail, AgreementDetail agreement) {
		agreement.setCoApplicants(new ArrayList<>());
		if (detail.getJountAccountDetailList()!=null && !detail.getJountAccountDetailList().isEmpty()) {
			List<CustomerDetails> custIDs=new ArrayList<CustomerDetails>();
			for (JointAccountDetail jointAccountDetail : detail.getJountAccountDetailList()) {
				CustomerDetails custdetails = customerDetailsService.getCustomerDetailsById(jointAccountDetail.getCustID(), true, "_AView");
				custIDs.add(custdetails);
			}
			
			for (CustomerDetails customerDetails : custIDs) {
				//co applicant
				CoApplicant coapplicant = agreement.new CoApplicant();
				Customer customer = customerDetails.getCustomer();
				coapplicant.setCustName(customer.getCustShrtName());
				//pan number
				List<CustomerDocument> doclist = customerDetails.getCustomerDocumentsList();
				coapplicant.setPanNumber(PennantApplicationUtil.getPanNumber(doclist));
				
				List<CustomerAddres> addlist = customerDetails.getAddressList();
				if (addlist!=null && !addlist.isEmpty()) {
					setCoapplicantAddress(coapplicant, addlist);
				}
				agreement.getCoApplicants().add(coapplicant);
			}
		}else{
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
		List<FinCollaterals> finCollateralslist = detail.getFinanceCollaterals();
		agreement.setCollateralData(new ArrayList<AgreementDetail.FinCollaterals>());

		for (FinCollaterals finCollaterals : finCollateralslist) {
			com.pennant.backend.model.finance.AgreementDetail.FinCollaterals collateralData = agreement.new FinCollaterals();
			collateralData.setCollateralType(finCollaterals.getCollateralType());
			collateralData.setReference(finCollaterals.getReference());
			collateralData.setCollateralAmt(PennantAppUtil.amountFormate(finCollaterals.getValue(), formatter));
			agreement.getCollateralData().add(collateralData);

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
							mMAgreement.getProfitRate().doubleValue(), 9));
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
			agreement.setProfitRate(PennantApplicationUtil.formatRate(main.getRepayProfitRate().doubleValue(), 9));
			agreement.setTotPriAmount(PennantApplicationUtil.amountFormate(main.getTotalPriAmt(), formatter));
			agreement.setFinRef(main.getFinReference());
			agreement.setFinCcy(main.getFinCcy());
			agreement.setPftDaysBasis(main.getProfitDaysBasis());
			agreement.setFinBranch(main.getFinBranch());
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
			agreement.setFinAmount(PennantApplicationUtil.amountFormate(main.getFinAmount(), formatter));
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
					.setEffPftRate(PennantApplicationUtil.formatRate(main.getEffectiveRateOfReturn().doubleValue(), 9));
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
				agreement.setFinMinRate(PennantApplicationUtil.formatRate(main.getRpyMinRate().doubleValue(), 9));
			}
			agreement.setFinMaxRate("");
			if (main.getRpyMinRate() != null) {
				agreement.setFinMaxRate(PennantApplicationUtil.formatRate(main.getRpyMaxRate().doubleValue(), 9));
			}
			agreement.setRepayBaseRate(main.getRepayBaseRate());
			agreement.setFinAmtPertg(PennantApplicationUtil.amountFormate(
					main.getFinAmount().multiply(new BigDecimal(125))
							.divide(new BigDecimal(100), RoundingMode.HALF_DOWN), formatter));
			agreement.setPurchasePrice(PennantApplicationUtil.amountFormate(
					main.getFinAmount().subtract(main.getDownPayment()), formatter));
			agreement.setRepayMargin(PennantApplicationUtil.formatRate(main.getRepayMargin().doubleValue(), 9));
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
				agreement.setNetRefRateLoan(PennantApplicationUtil.formatRate(details.getNetRefRateLoan().doubleValue(), 9));
			}else{
				agreement.setNetRefRateLoan(PennantApplicationUtil.formatRate(main.getRepayProfitRate().doubleValue(), 9));
			}
		

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
			List<Notes> list = getNotesService().getNotesList(note, false);
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
	public void setExtendedMasterDescription(FinanceDetail detail, TemplateEngine engine) {
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
	
	public void setFeeDetails(FinanceDetail detail, TemplateEngine engine) throws Exception {
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



}
