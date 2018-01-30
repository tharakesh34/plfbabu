package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennanttech.pennapps.core.resource.Literal;

public class CustomizeFinanceDataValidation {

	private static final Logger logger = Logger.getLogger(CustomizeFinanceDataValidation.class);

	private CustomerDAO					customerDAO;
	private ExtendedFieldDetailsService	extendedFieldDetailsService;
	private BankBranchService			bankBranchService;
	private MandateService				mandateService;
	private BankDetailService			bankDetailService;
	private CustomerDetailsService		customerDetailsService;


	public FinScheduleData financeDataValidation(String vldGroup, FinanceDetail financeDetail, boolean apiFlag) {
		logger.debug(Literal.ENTERING);
		
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceType financeType = finScheduleData.getFinanceType();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<ErrorDetail> errorDetails = new ArrayList<>();

		if (StringUtils.isNotBlank(finMain.getLovDescCustCIF())) {
			Customer customer = customerDAO.getCustomerByCIF(finMain.getLovDescCustCIF(), "");
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getLovDescCustCIF();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90101", valueParm)));
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			} else {
				finScheduleData.getFinanceMain().setCustID(customer.getCustID());
				financeDetail.getCustomerDetails().setCustomer(customer);
			}
		}
		if (finMain.getNumberOfTerms() <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "numberOfTerms";
			valueParm[1] = "0";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));
			finScheduleData.setErrorDetails(errorDetails);
		}
		//Net Loan Amount
		BigDecimal netLoanAmount = finMain.getFinAmount().subtract(finMain.getDownPayment());
		if (netLoanAmount.compareTo(financeType.getFinMinAmount()) < 0) {
			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(financeType.getFinMinAmount());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90132", valueParm)));
			finScheduleData.setErrorDetails(errorDetails);
			return finScheduleData;
		}

		if (financeType.getFinMaxAmount().compareTo(BigDecimal.ZERO) > 0) {
			if (netLoanAmount.compareTo(financeType.getFinMaxAmount()) > 0) {
				String[] valueParm = new String[1];
				valueParm[0] = String.valueOf(financeType.getFinMaxAmount());
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90133", valueParm)));
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}
		}
		//mandate details Validation
		errorDetails = mandateValidation(financeDetail);

		if (!errorDetails.isEmpty()) {
			finScheduleData.setErrorDetails(errorDetails);
			return finScheduleData;
		}

		//Co-Applicant details Validation
		errorDetails = joointAccountDetailsValidation(financeDetail);
		if (!errorDetails.isEmpty()) {
			finScheduleData.setErrorDetails(errorDetails);
			return finScheduleData;
		}

		//ExtendedFieldDetails Validation
		String subModule = financeDetail.getFinScheduleData().getFinanceMain().getFinCategory();
		errorDetails = extendedFieldDetailsService.validateExtendedFieldDetails(financeDetail.getExtendedDetails(),
				ExtendedFieldConstants.MODULE_LOAN, subModule);
		if (!errorDetails.isEmpty()) {
			finScheduleData.setErrorDetails(errorDetails);
			return finScheduleData;
		}

		logger.debug(Literal.LEAVING);
		
		return finScheduleData;
	}

	private List<ErrorDetail> mandateValidation(FinanceDetail financeDetail) {

		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		Mandate mandate = financeDetail.getMandate();
		if (mandate != null) {
			if (mandate.isUseExisting()) {
				if (mandate.getMandateID() == Long.MIN_VALUE) {
					String[] valueParm = new String[1];
					valueParm[0] = "MandateID";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				} else {
					Mandate curMandate = mandateService.getApprovedMandateById(mandate.getMandateID());
					if (curMandate == null) {
						String[] valueParm = new String[1];
						valueParm[0] = String.valueOf(mandate.getMandateID());
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90303", valueParm)));
						return errorDetails;
					} else {
						if (!StringUtils.equalsIgnoreCase(curMandate.getCustCIF(),
								financeDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF())) {
							String[] valueParm = new String[2];
							valueParm[0] = financeDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF();
							valueParm[1] = curMandate.getCustCIF();
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90310", valueParm)));
							return errorDetails;
						}
						if (!StringUtils.equalsIgnoreCase(curMandate.getMandateType(),
								financeDetail.getFinScheduleData().getFinanceMain().getFinRepayMethod())) {
							String[] valueParm = new String[2];
							valueParm[0] = financeDetail.getFinScheduleData().getFinanceMain().getFinRepayMethod();
							valueParm[1] = curMandate.getMandateType();
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90311", valueParm)));
							return errorDetails;
						}
						if (!(curMandate.isOpenMandate() || (curMandate.getOrgReference() == null))) {
							String[] valueParm = new String[1];
							valueParm[0] = String.valueOf(mandate.getMandateID());
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90312", valueParm)));
							return errorDetails;
						}
						if (!curMandate.isActive()) {
							String[] valueParm = new String[2];
							valueParm[0] = "mandate:";
							valueParm[1] = String.valueOf(mandate.getMandateID());
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("81004", valueParm)));
							return errorDetails;
						}
						financeDetail.setMandate(curMandate);
					}
				}
			} else {
				mandate.setMandateID(Long.MIN_VALUE);
				// validate Mandate fields
				if (StringUtils.isBlank(mandate.getMandateType())) {
					String[] valueParm = new String[1];
					valueParm[0] = "mandateType";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				}

				if (StringUtils.isBlank(mandate.getIFSC())) {
					if ((StringUtils.isBlank(mandate.getBankCode()) || StringUtils.isBlank(mandate.getBranchCode()))) {
						String[] valueParm = new String[1];
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90313", valueParm)));
					}
				}
				if (StringUtils.isBlank(mandate.getAccType())) {
					String[] valueParm = new String[1];
					valueParm[0] = "accType";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				}
				if (StringUtils.isBlank(mandate.getAccNumber())) {
					String[] valueParm = new String[1];
					valueParm[0] = "accNumber";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				}
				if (mandate.getAccNumber().length() > 50) {
					String[] valueParm = new String[2];
					valueParm[0] = "accNumber length";
					valueParm[1] = "50";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));
					return errorDetails;
				}
				if (StringUtils.isBlank(mandate.getAccHolderName())) {
					String[] valueParm = new String[1];
					valueParm[0] = "accHolderName";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				}
				if (mandate.getStartDate() == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "startDate";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				}
				if (!mandate.isOpenMandate()) {
					if (mandate.getExpiryDate() == null) {
						String[] valueParm = new String[1];
						valueParm[0] = "expiryDate";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
						return errorDetails;
					}
				} else {
					if (mandate.getExpiryDate() != null) {
						String[] valueParm = new String[2];
						valueParm[0] = "expiryDate";
						valueParm[1] = "open mandate";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90329", valueParm)));
						return errorDetails;
					}
				}

				if (mandate.getMaxLimit() == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "maxLimit";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90242", valueParm)));
					return errorDetails;
				}

				if (mandate.getMaxLimit().compareTo(BigDecimal.ZERO) <= 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "maxLimit";
					valueParm[1] = "0";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));
					return errorDetails;
				}
				if (mandate.getExpiryDate() != null) {
					if (mandate.getExpiryDate().compareTo(mandate.getStartDate()) <= 0
							|| mandate.getExpiryDate().after(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"))) {
						String[] valueParm = new String[3];
						valueParm[0] = "Mandate ExpiryDate";
						valueParm[1] = DateUtility.formatToLongDate(DateUtility.addDays(mandate.getStartDate(), 1));
						valueParm[2] = DateUtility.formatToLongDate(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"));
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90318", valueParm)));
						return errorDetails;
					}
				}
				if (mandate.getStartDate() != null) {
					Date mandbackDate = DateUtility.addDays(DateUtility.getAppDate(),
							-SysParamUtil.getValueAsInt("MANDATE_STARTDATE"));
					if (mandate.getStartDate().before(mandbackDate)
							|| mandate.getStartDate().after(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"))) {
						String[] valueParm = new String[3];
						valueParm[0] = "mandate start date";
						valueParm[1] = DateUtility.formatToLongDate(mandbackDate);
						valueParm[2] = DateUtility.formatToLongDate(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"));
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90318", valueParm)));
					}
				}
				boolean isValidBranch = true;
				if (StringUtils.isNotBlank(mandate.getIFSC())) {
					BankBranch bankBranch = bankBranchService.getBankBrachByIFSC(mandate.getIFSC());
					if (bankBranch == null) {
						String[] valueParm = new String[1];
						valueParm[0] = mandate.getIFSC();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90301", valueParm)));
					} else {
						isValidBranch = validateBranchCode(mandate, isValidBranch, bankBranch);
						mandate.setBankCode(bankBranch.getBankCode());
						if (StringUtils.isBlank(mandate.getMICR())) {
							mandate.setMICR(bankBranch.getMICR());
						} else {
							if (!StringUtils.equals(bankBranch.getMICR(), mandate.getMICR())) {
								String[] valueParm = new String[2];
								valueParm[0] = "MICR";
								valueParm[1] = mandate.getMICR();
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));
							}
						}
					}
				} else if (StringUtils.isNotBlank(mandate.getBankCode())
						&& StringUtils.isNotBlank(mandate.getBranchCode())) {
					BankBranch bankBranch = bankBranchService.getBankBrachByCode(mandate.getBankCode(),
							mandate.getBranchCode());
					if (bankBranch == null) {
						String[] valueParm = new String[2];
						valueParm[0] = mandate.getBankCode();
						valueParm[1] = mandate.getBranchCode();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90302", valueParm)));
					} else {
						isValidBranch = validateBranchCode(mandate, isValidBranch, bankBranch);
						mandate.setBankCode(bankBranch.getBankCode());
						if (StringUtils.isBlank(mandate.getMICR())) {
							mandate.setMICR(bankBranch.getMICR());
						} else {
							if (!StringUtils.equals(bankBranch.getMICR(), mandate.getMICR())) {
								String[] valueParm = new String[2];
								valueParm[0] = "MICR";
								valueParm[1] = mandate.getMICR();
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));
								return errorDetails;
							}
						}

					}
				}
				if (!isValidBranch) {
					String[] valueParm = new String[1];
					valueParm[0] = mandate.getMandateType();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90333", valueParm)));
					return errorDetails;
				}
				//validate AccNumber length
				if (StringUtils.isNotBlank(mandate.getBankCode())) {
					int accNoLength = bankDetailService.getAccNoLengthByCode(mandate.getBankCode());
					if (accNoLength != 0) {
						if (mandate.getAccNumber().length() != accNoLength) {
							String[] valueParm = new String[2];
							valueParm[0] = "AccountNumber(Mandate)";
							valueParm[1] = String.valueOf(accNoLength) + " characters";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30570", valueParm)));
							return errorDetails;
						}
					}
				}
				//validate Phone number
				String mobileNumber = mandate.getPhoneNumber();
				if (StringUtils.isNotBlank(mobileNumber)) {
					if (!(mobileNumber.matches("\\d{10}"))) {
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90278", null)));
					}
				}
				//validate names
				String accHolderName = mandate.getAccHolderName();
				if (StringUtils.isNotBlank(accHolderName)) {
					if (!(accHolderName.matches("^$|^[A-Za-z]+[A-Za-z.\\s]*"))) {
						String[] valueParm = new String[1];
						valueParm[0] = "AccHolderName";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90237", valueParm)));
					}
				}
				String jointAccHolderName = mandate.getJointAccHolderName();
				if (StringUtils.isNotBlank(jointAccHolderName)) {
					if (!(jointAccHolderName.matches("^$|^[A-Za-z]+[A-Za-z.\\s]*"))) {
						String[] valueParm = new String[1];
						valueParm[0] = "JointAccHolderName";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90237", valueParm)));
					}
				}

				// validate MandateType
				if (StringUtils.isNotBlank(mandate.getMandateType())) {
					List<ValueLabel> mandateType = PennantStaticListUtil.getMandateTypeList();
					boolean mandateTypeSts = false;
					for (ValueLabel value : mandateType) {
						if (StringUtils.equals(value.getValue(), mandate.getMandateType())) {
							mandateTypeSts = true;
							break;
						}
					}
					if (!mandateTypeSts) {
						String[] valueParm = new String[1];
						valueParm[0] = mandate.getMandateType();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90307", valueParm)));
					}
				}

				// validate AccType
				if (StringUtils.isNotBlank(mandate.getAccType())) {
					List<ValueLabel> accType = PennantStaticListUtil.getAccTypeList();
					boolean accTypeSts = false;
					for (ValueLabel value : accType) {
						if (StringUtils.equals(value.getValue(), mandate.getAccType())) {
							accTypeSts = true;
							break;
						}
					}
					if (!accTypeSts) {
						String[] valueParm = new String[1];
						valueParm[0] = mandate.getAccType();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90308", valueParm)));
					}
				}

				//validate periodicity
				if (StringUtils.isNotBlank(mandate.getPeriodicity())) {
					ErrorDetail errorDetail = FrequencyUtil.validateFrequency(mandate.getPeriodicity());
					if (errorDetail != null && StringUtils.isNotBlank(errorDetail.getCode())) {
						String[] valueParm = new String[1];
						valueParm[0] = mandate.getPeriodicity();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90207", valueParm)));
					}
				} else {
					mandate.setPeriodicity(MandateConstants.MANDATE_DEFAULT_FRQ);
				}

				//validate status
				if (StringUtils.isNotBlank(mandate.getStatus())) {
					List<ValueLabel> status = PennantStaticListUtil.getStatusTypeList();
					boolean sts = false;
					for (ValueLabel value : status) {
						if (StringUtils.equals(value.getValue(), mandate.getStatus())) {
							sts = true;
							break;
						}
					}
					if (!sts) {
						String[] valueParm = new String[1];
						valueParm[0] = mandate.getStatus();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90309", valueParm)));
					}
				}
				if (!StringUtils.equalsIgnoreCase(mandate.getMandateType(),
						financeDetail.getFinScheduleData().getFinanceMain().getFinRepayMethod())) {
					String[] valueParm = new String[2];
					valueParm[0] = financeDetail.getFinScheduleData().getFinanceMain().getFinRepayMethod();
					valueParm[1] = mandate.getMandateType();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90311", valueParm)));
					return errorDetails;
				}
			}
			
			if (mandate.getDocImage() == null && StringUtils.isBlank(mandate.getExternalRef())) {
				String[] valueParm = new String[2];
				valueParm[0] = "docContent";
				valueParm[1] = "docRefId";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90123", valueParm)));
			} else if(StringUtils.isBlank(mandate.getDocumentName())) {
				String[] valueParm = new String[2];
				valueParm[0] = "Document Name";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
			}
			
			if (StringUtils.isNotBlank(mandate.getDocumentName())) {
				String docName = mandate.getDocumentName().toLowerCase();

				// document name is only extension
				if (StringUtils.isEmpty(docName.substring(0, docName.lastIndexOf(".")))) {
					String[] valueParm = new String[2];
					valueParm[0] = "Document Name";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				}

				// document Name Extension validation
				if (!docName.endsWith(".jpg") && !docName.endsWith(".jpeg") && !docName.endsWith(".png")
						&& !docName.endsWith(".pdf")) {
					String[] valueParm = new String[1];
					valueParm[0] = "Document Extension available ext are:JPG,JPEG,PNG,PDF ";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90122", valueParm)));
				} else if (!docName.contains(".")) {
					//Uploaded document {0} extension should be required.
					String[] valueParm = new String[1];
					valueParm[0] = mandate.getDocumentName();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90291", valueParm)));
				} else {
					//TODO: uploaded document {0} is not supported
					String[] valueParm = new String[1];
					valueParm[0] = mandate.getDocumentName();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90407", valueParm)));
				}
			}
		}
		return errorDetails;
	}

	private boolean validateBranchCode(Mandate mandate, boolean isValidBranch, BankBranch bankBranch) {
		if (StringUtils.equals(MandateConstants.TYPE_ECS, mandate.getMandateType())) {
			if (!bankBranch.isEcs()) {
				isValidBranch = false;
			}
		} else if (StringUtils.equals(MandateConstants.TYPE_DDM, mandate.getMandateType())) {
			if (!bankBranch.isDda()) {
				isValidBranch = false;
			}
		} else if (StringUtils.equals(MandateConstants.TYPE_NACH, mandate.getMandateType())) {
			if (!bankBranch.isNach()) {
				isValidBranch = false;
			}
		}
		return isValidBranch;
	}

	/**
	 * Method for validate the Co-Applicants.
	 * 
	 * @param financeDetail
	 * @return errorDetails
	 */
	private List<ErrorDetail> joointAccountDetailsValidation(FinanceDetail financeDetail) {
		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		List<JointAccountDetail> jountAccountDetails = financeDetail.getJountAccountDetailList();
		if (jountAccountDetails != null) {
			for (JointAccountDetail jointAccDetail : jountAccountDetails) {
				if (jointAccDetail.isIncludeRepay()) {
					if (StringUtils.isBlank(jointAccDetail.getRepayAccountId())) {
						String[] valueParm = new String[2];
						valueParm[0] = "RepayAccountId";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
						return errorDetails;
					}
				}else if(StringUtils.isNotBlank(jointAccDetail.getRepayAccountId())){
					String[] valueParm = new String[2];
					valueParm[0] = "RepayAccountId";
					valueParm[1] = "includeRepay";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90124", valueParm)));
				}
				if (StringUtils.equals(jointAccDetail.getCustCIF(),
						financeDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF())) {
					String[] valueParm = new String[2];
					valueParm[0] = jointAccDetail.getCustCIF();
					valueParm[1] = "co-applicant";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90250", valueParm)));
					return errorDetails;
				}
				Customer coApplicant = customerDetailsService.getCustomerByCIF(jointAccDetail.getCustCIF());
				if (coApplicant == null) {
					String[] valueParm = new String[1];
					valueParm[0] = jointAccDetail.getCustCIF();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90102", valueParm)));
				}

				//for authoritySignatory and sequence		
				if (jointAccDetail.isAuthoritySignatory()) {

					if (jointAccDetail.getSequence() <= 0 || jointAccDetail.getSequence() >= 10) {
						//{0} should between or including {1} and {2}.
						String[] valueParm = new String[3];
						valueParm[0] = "sequence";
						valueParm[1] = "1";
						valueParm[2] = "9";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90282", valueParm)));

					}
				} else if (jointAccDetail.getSequence() != 0) {
					//{0} is only applicable for {1}.
					String[] valueParm = new String[2];
					valueParm[0] = "sequence";
					valueParm[1] = "authoritySignatory";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90298", valueParm)));
				}				
				
				int duplicateSeqCount = 0;
				int duplicateCifCount = 0;
				for (JointAccountDetail detail : jountAccountDetails) {
					if (jointAccDetail.getSequence() == detail.getSequence()) {
						duplicateSeqCount++;
					}
					if (StringUtils.equals(jointAccDetail.getCustCIF(), detail.getCustCIF())) {
						duplicateCifCount++;
					}
				}
				//Duplicate {0} are not allowed.
				if (duplicateSeqCount >= 2) {
					String[] valueParm = new String[1];
					valueParm[0] = "sequence id";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90273", valueParm)));
				}
				//Duplicate {0} are not allowed.
				if (duplicateCifCount >= 2) {
					String[] valueParm = new String[1];
					valueParm[0] = "CIF";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90273", valueParm)));
				}
				

			}
			
		}
		return errorDetails;
	}

	

	/**
	 * Method for validating Finance Schedule details
	 * 
	 * @param vldCrtSchd
	 * @param finScheduleData
	 * @param apiFlag
	 */
	public void financeDataValidation(String vldCrtSchd, FinScheduleData finScheduleData, boolean apiFlag) {
		FinanceDetail financeDetail = new FinanceDetail();
		financeDetail.setFinScheduleData(finScheduleData);
		financeDataValidation(vldCrtSchd, financeDetail, apiFlag);
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	public void setBankBranchService(BankBranchService bankBranchService) {
		this.bankBranchService = bankBranchService;
	}

	public void setMandateService(MandateService mandateService) {
		this.mandateService = mandateService;
	}

	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

}
