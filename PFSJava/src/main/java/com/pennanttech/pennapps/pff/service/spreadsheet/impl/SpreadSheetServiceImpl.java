package com.pennanttech.pennapps.pff.service.spreadsheet.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.MasterDefUtil;
import com.pennant.app.util.MasterDefUtil.AddressType;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.BankInfoDetail;
import com.pennant.backend.model.customermasters.CustCardSales;
import com.pennant.backend.model.customermasters.CustCardSalesDetails;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerGST;
import com.pennant.backend.model.customermasters.CustomerGSTDetails;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.model.customermasters.ExtLiabilityPaymentdetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.CreditReviewDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennant.backend.model.spreadsheet.SpreadSheet;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.CreditApplicationReviewService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.pff.service.spreadsheet.SpreadSheetService;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pennapps.pff.verification.service.VerificationService;

public class SpreadSheetServiceImpl implements SpreadSheetService {
	private static final Logger logger = Logger.getLogger(SpreadSheetServiceImpl.class);

	public static final BigDecimal LAKH = new BigDecimal(100000);

	private ExtendedFieldDetailsService extendedFieldDetailsService;
	private VerificationService verificationService;
	private CustomerService customerService;
	private CreditApplicationReviewService creditApplicationReviewService;

	@Override
	public Map<String, Object> prepareDataMap(FinanceDetail fd, CreditReviewDetails creditReviewDetail) {
		logger.debug(Literal.ENTERING);
		Map<String, Object> dataMap = new HashMap<>();

		SpreadSheet spreadSheet = new SpreadSheet();
		if (fd == null || fd.isSpreadSheetloaded()) {
			return dataMap;
		}

		try {
			FinanceMain fm = fd.getFinScheduleData().getFinanceMain();
			fm.setFinAmount(PennantApplicationUtil.formateAmount(getAmountInLakhs(fm.getFinAmount().toString()),
					PennantConstants.defaultCCYDecPos));
			spreadSheet.setFm(fm);
			spreadSheet.setCu(fd.getCustomerDetails().getCustomer());
			dataMap.put("finStartDate", fm.getFinStartDate());
			setCustomerAge(fd, dataMap);
			setCustomerName(spreadSheet, spreadSheet.getCu());
			setCustomerAddresses(spreadSheet, fd.getCustomerDetails());
			setCustomerPhoneNumber(spreadSheet, fd);
			setExtendedData(spreadSheet.getCu(), fd, dataMap);
			setKeyFigures(fm, dataMap);

			if (fd.getCustomerDetails().getExtendedFieldRender() != null) {
				spreadSheet.setEf(fd.getCustomerDetails().getExtendedFieldRender().getMapValues());
			}

			if (fd.getExtendedFieldRender() != null) {
				spreadSheet.setLoanEf(fd.getExtendedFieldRender().getMapValues());
			}

			setCoApplicantData(spreadSheet, fd, dataMap);
			setCorporateFinancialData(fd, dataMap);
			setCustomerGstDetails(fd, dataMap);
			setExternalLiabilites(fd, dataMap);
			setBankingDetails(fd, dataMap);
			setCardSalesDetails(fd, dataMap);
			dataMap.put("finStartDate", fm.getFinStartDate());
			fd.setSpreadSheetloaded(true);

			List<FinanceScheduleDetail> scheduleDetails = fd.getFinScheduleData().getFinanceScheduleDetails();
			if (scheduleDetails.size() >= 1) {
				fm.setRepayProfitRate(PennantApplicationUtil.formateAmount((scheduleDetails.get(0).getCalculatedRate()),
						PennantConstants.defaultCCYDecPos));
				spreadSheet.setEmiAmount(PennantApplicationUtil.formateAmount(scheduleDetails.get(2).getRepayAmount(),
						PennantConstants.defaultCCYDecPos)); // setting EMI amount in spreadsheet

			} else {
				fm.setRepayProfitRate(BigDecimal.ZERO);
			}
			String keys = creditReviewDetail.getFieldKeys();
			if (StringUtils.isNotBlank(keys)) {
				List<String> items = Arrays.asList(keys.split("\\s*,\\s*"));
				for (String string : items) {
					if (dataMap.containsKey(string)) {
						// nothing to do
					} else if (string.startsWith("Gst")) {
						dataMap.put(string, "");
					} else if (string.startsWith("Ext_LoanStartDate")) {
						dataMap.put(string, "0");
					}
				}
			}
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		dataMap.put("spreadsheet", spreadSheet);
		logger.debug(Literal.LEAVING);
		return dataMap;
	}

	private void setCustomerAge(FinanceDetail fd, Map<String, Object> dataMap) {
		logger.debug(Literal.ENTERING);
		try {
			Date custDOB = fd.getCustomerDetails().getCustomer().getCustDOB();
			dataMap.put("CustDOB", DateUtil.format(custDOB, "dd/MMM/yy"));
			dataMap.put("CustAge", DateUtil.getYearsBetween(SysParamUtil.getAppDate(), custDOB));
			dataMap.put("CustMatAge",
					DateUtil.getYearsBetween(fd.getFinScheduleData().getFinanceMain().getMaturityDate(), custDOB));
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void setCustomerName(SpreadSheet spreadSheet, Customer customer) {
		logger.debug(Literal.ENTERING);
		try {
			if (customer.getCustCtgCode().equals(PennantConstants.PFF_CUSTCTG_SME)
					|| customer.getCustCtgCode().equals(PennantConstants.PFF_CUSTCTG_CORP)) {
				customer.setCustomerFullName(customer.getCustShrtName());
			} else {
				customer.setCustomerFullName(
						customer.getCustFName().concat(customer.getCustMName().concat(customer.getCustLName())));
			}

		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void setCustomerPhoneNumber(SpreadSheet spreadSheet, FinanceDetail fd) {
		logger.debug(Literal.ENTERING);
		String phoneNo[] = new String[7];
		Customer customer = fd.getCustomerDetails().getCustomer();
		List<CustomerPhoneNumber> mainAppPhoneNumList = fd.getCustomerDetails().getCustomerPhoneNumList();
		List<JointAccountDetail> coApplicantsList = fd.getJountAccountDetailList();
		if (CollectionUtils.isEmpty(mainAppPhoneNumList) || CollectionUtils.isEmpty(coApplicantsList)) {
			return;
		}
		try {
			int i = 0;
			for (CustomerPhoneNumber mainAppPhoneNumber : mainAppPhoneNumList) {
				if (StringUtils.equals(PennantConstants.KYC_PRIORITY_VERY_HIGH,
						String.valueOf(mainAppPhoneNumber.getPhoneTypePriority()))) {
					phoneNo[i] = customer.getCustShrtName() + "-" + mainAppPhoneNumber.getPhoneNumber();
				}
				i++;
				for (JointAccountDetail coApp : coApplicantsList) {
					List<CustomerPhoneNumber> coAppPhoneNums = coApp.getCustomerDetails().getCustomerPhoneNumList();
					for (CustomerPhoneNumber coAppPhoneNumber : coAppPhoneNums) {
						if (StringUtils.equals(PennantConstants.KYC_PRIORITY_VERY_HIGH,
								String.valueOf(coAppPhoneNumber.getPhoneTypePriority()))) {
							phoneNo[i] = coApp.getLovDescCIFName() + "-" + coAppPhoneNumber.getPhoneNumber();
						}
						i++;
					}
				}
			}
			String phoneNumbers = concatStrings(phoneNo);
			spreadSheet.setCustomerPhoneNum(phoneNumbers);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	//setting up address of main applicant
	private void setCustomerAddresses(SpreadSheet spreadSheet, CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);
		String customerAddress = "";
		List<CustomerAddres> addressList = customerDetails.getAddressList();
		if (CollectionUtils.isEmpty(addressList)) {
			return;
		}
		try {
			for (CustomerAddres addr : addressList) {
				if (StringUtils.equalsIgnoreCase(addr.getCustAddrType(),
						MasterDefUtil.getAddressCode(AddressType.BUS))) {
					customerAddress = concatStrings(addr.getCustAddrHNbr(), addr.getCustAddrStreet(),
							addr.getLovDescCustAddrCityName(), addr.getLovDescCustAddrProvinceName(),
							addr.getCustAddrZIP());

					spreadSheet.setCustOffAddr(customerAddress);
				} else if (StringUtils.equalsIgnoreCase(addr.getCustAddrType(),
						MasterDefUtil.getAddressCode(AddressType.PER))) {
					customerAddress = concatStrings(addr.getCustAddrHNbr(), addr.getCustAddrStreet(),
							addr.getLovDescCustAddrCityName(), addr.getLovDescCustAddrProvinceName(),
							addr.getCustAddrZIP());

					spreadSheet.setCustPerAddr(customerAddress);
				}
			}
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	private String concatStrings(String... args) {
		logger.debug(Literal.ENTERING);
		StringBuilder data = new StringBuilder();

		for (String item : args) {
			if (item != null) {
				item = StringUtils.trimToEmpty(item);

				if (data.length() > 0) {
					data.append(",");
				}

				data.append(item);
			}

		}
		logger.debug(Literal.LEAVING);
		return data.toString();
	}

	private void setExtendedData(Customer customer, FinanceDetail fd, Map<String, Object> dataMap) {
		logger.debug(Literal.ENTERING);
		ExtendedFieldRender fieldRender = fd.getCustomerDetails().getExtendedFieldRender();
		if (fieldRender == null) {
			return;
		}
		try {
			if (fieldRender != null) {
				customer.setCustAddlVar8(getExtFieldDesc(fieldRender, "clix_natureofbusiness", "natureofbusiness"));
				customer.setCustAddlVar9(getExtFieldDesc(fieldRender, "clix_industry", "industry"));
				customer.setCustAddlVar10(getExtFieldDesc(fieldRender, "clix_segment", "segment"));
				customer.setCustAddlVar11(getExtFieldDesc(fieldRender, "clix_product", "product"));
				String margin = getExtFieldIndustryMargin("clix_industrymargin", customer.getCustAddlVar8(),
						customer.getCustAddlVar9(), customer.getCustAddlVar10(), customer.getCustAddlVar11());
				customer.setIndustryMargin(PennantApplicationUtil.formateAmount(new BigDecimal(margin),
						PennantConstants.defaultCCYDecPos));
				customer.setSubIndustry(fieldRender.getMapValues().get("custsubindustry").toString());
				setMainApplicantFiStatus(fd, fd.getCustomerDetails().getCustomer().getCustCIF(), dataMap);
			}
			/*
			 * setCustomerShareHoldingPercentage(fd.getCustomerDetails().getCustomerDirectorList(), dataMap,
			 * "MainAppSharePerc");
			 */

		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	// mapping share holding percentage for both main and co-applicants
	private void setCustomerShareHoldingPercentage(List<DirectorDetail> customerDirectorList,
			Map<String, Object> dataMap, String key) {
		logger.debug(Literal.ENTERING);
		if (CollectionUtils.isNotEmpty(customerDirectorList)) {
			for (DirectorDetail directorDetail : customerDirectorList) {
				if (directorDetail != null) {
					dataMap.put(key, PennantApplicationUtil.formateAmount(directorDetail.getSharePerc(),
							PennantConstants.defaultCCYDecPos));
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	//setting up address of coapplicant
	private void setCoAppAddresses(SpreadSheet spreadSheet, CustomerDetails cu) {
		logger.debug(Literal.ENTERING);
		String customerAddress = "";
		List<CustomerAddres> addressList = cu.getAddressList();
		if (CollectionUtils.isEmpty(addressList)) {
			return;
		}
		try {
			for (CustomerAddres addr : addressList) {
				if (StringUtils.equalsIgnoreCase(addr.getCustAddrType(),
						MasterDefUtil.getAddressCode(AddressType.CURRES))
						&& StringUtils.equals(PennantConstants.KYC_PRIORITY_VERY_HIGH,
								String.valueOf(addr.getCustAddrPriority()))) {
					customerAddress = concatStrings(addr.getCustAddrHNbr(), addr.getCustAddrStreet(),
							addr.getLovDescCustAddrCityName(), addr.getLovDescCustAddrProvinceName(),
							addr.getCustAddrZIP());

					spreadSheet.setCustResiAddr(customerAddress);
				} else if (StringUtils.equalsIgnoreCase(addr.getCustAddrType(),
						MasterDefUtil.getAddressCode(AddressType.RESIOFF))
						&& StringUtils.equals(PennantConstants.KYC_PRIORITY_VERY_HIGH,
								String.valueOf(addr.getCustAddrPriority()))) {
					customerAddress = concatStrings(addr.getCustAddrHNbr(), addr.getCustAddrStreet(),
							addr.getLovDescCustAddrCityName(), addr.getLovDescCustAddrProvinceName(),
							addr.getCustAddrZIP());

					spreadSheet.setCustResiAddr(customerAddress);
				}
			}
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	private String getExtFieldDesc(String tableName, String value) {
		logger.debug(Literal.ENTERING);
		try {
			if (StringUtils.trimToNull(tableName) == null) {
				return null;
			}
			return extendedFieldDetailsService.getExtFieldDesc(tableName, value);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	private String getExtFieldDesc(ExtendedFieldRender fieldRender, String tableName, String fieldName) {
		logger.debug(Literal.ENTERING);
		if (StringUtils.trimToNull(tableName) == null) {
			return null;
		}

		try {
			Map<String, Object> mapValues = fieldRender.getMapValues();
			if (mapValues != null) {
				Object value = mapValues.get(fieldName).toString();

				if (value == null) {
					return null;
				}

				return extendedFieldDetailsService.getExtFieldDesc(tableName, value.toString());
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	private String getExtFieldIndustryMargin(String tableName, String type, String industry, String segment,
			String product) {
		logger.debug(Literal.ENTERING);

		if (StringUtils.trimToNull(tableName) == null) {
			return null;
		}

		try {
			return extendedFieldDetailsService.getExtFieldIndustryMargin(tableName, type, industry, segment, product);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	// method for Main Applicant FI status
	private void setMainApplicantFiStatus(FinanceDetail fd, String custCif, Map<String, Object> dataMap) {
		logger.debug(Literal.ENTERING);
		List<CustomerAddres> addressList = fd.getCustomerDetails().getAddressList();

		if (CollectionUtils.isEmpty(addressList)) {
			return;
		}
		try {
			for (CustomerAddres addr : addressList) {
				if (StringUtils.equalsIgnoreCase(addr.getCustAddrType(),
						MasterDefUtil.getAddressCode(AddressType.CURRES))
						|| StringUtils.equalsIgnoreCase(addr.getCustAddrType(),
								MasterDefUtil.getAddressCode(AddressType.RESIOFF))) {
					setFiStatus(fd, addr, "MainAppResiFIStatus", custCif, dataMap);
				}
				if (StringUtils.equalsIgnoreCase(addr.getCustAddrType(),
						MasterDefUtil.getAddressCode(AddressType.BUS))) {
					setFiStatus(fd, addr, "MainAppOfficeFIStatus", custCif, dataMap);
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	// Method for CoApplicant FI status
	private void setCoApplicantFiStatus(FinanceDetail fd, CustomerDetails cu, String custCif, int value,
			Map<String, Object> dataMap) {
		logger.debug(Literal.ENTERING);
		List<CustomerAddres> addressList = cu.getAddressList();
		if (CollectionUtils.isEmpty(addressList)) {
			return;
		}
		try {
			for (CustomerAddres addr : addressList) {
				if (StringUtils.equalsIgnoreCase(addr.getCustAddrType(),
						MasterDefUtil.getAddressCode(AddressType.CURRES))
						|| StringUtils.equalsIgnoreCase(addr.getCustAddrType(),
								MasterDefUtil.getAddressCode(AddressType.RESIOFF))) {
					setFiStatus(fd, addr, "CoApp" + value + "ResiFIStatus", custCif, dataMap);
				}
				if (StringUtils.equalsIgnoreCase(addr.getCustAddrType(),
						MasterDefUtil.getAddressCode(AddressType.BUS))) {
					setFiStatus(fd, addr, "CoApp" + value + "OfficeFIStatus", custCif, dataMap);
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	// method for FI status for both office and residence addresses
	private void setFiStatus(FinanceDetail fd, CustomerAddres addr, String name, String custCif,
			Map<String, Object> dataMap) {
		logger.debug(Literal.ENTERING);
		try {
			Verification verificationForStatus = verificationService.getVerificationStatus(
					fd.getFinScheduleData().getFinanceMain().getFinReference(), VerificationType.FI.getKey(),
					addr.getCustAddrType(), custCif);
			if (verificationForStatus != null) {
				if (verificationForStatus.getStatus() == 1) {
					dataMap.put(name, Labels.getLabel("label_Legal_LegalDecision_Positive"));
				} else if (verificationForStatus.getStatus() == 2) {
					dataMap.put(name, Labels.getLabel("label_Legal_LegalDecision_Negative"));
				} else if (verificationForStatus.getStatus() == 3) {
					dataMap.put(name, Labels.getLabel("label_Legal_LegalDecision_Resubmit"));
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	// method for setting up eligibility method in key figures sheet
	private void setKeyFigures(FinanceMain fm, Map<String, Object> dataMap) {
		logger.debug(Literal.ENTERING);
		if (fm.getLovEligibilityMethod() == null) {
			return;
		}
		try {
			if (fm.getLovEligibilityMethod().equals("0107") || fm.getLovEligibilityMethod().equals("0108")
					|| fm.getLovEligibilityMethod().equals("0514") || fm.getLovEligibilityMethod().equals("0517")) {
				dataMap.put("IncomeProgramme1", "Bureau+Banking");
			} else if (fm.getLovEligibilityMethod().equals("0109")) {
				dataMap.put("IncomeProgramme2", "Bureau+Banking+GST+Financial");
			} else if (fm.getLovEligibilityMethod().equals("0110")) {
				dataMap.put("IncomeProgramme3", "Bureau+Banking+GST");
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void setCoApplicantData(SpreadSheet spreadSheet, FinanceDetail financeDetail, Map<String, Object> dataMap) {
		logger.debug(Literal.ENTERING);
		List<JointAccountDetail> jountAccountDetailList = financeDetail.getJountAccountDetailList();
		List<Integer> cibilScores = new ArrayList<>();
		if (CollectionUtils.isEmpty(jountAccountDetailList)) {
			return;
		}
		try {
			for (int i = 0; i < jountAccountDetailList.size(); i++) {
				List<Map<String, Object>> extendedMapValues = extendedFieldDetailsService.getExtendedFieldMap(
						String.valueOf(jountAccountDetailList.get(i).getCustCIF()),
						"CUSTOMER_" + jountAccountDetailList.get(i).getCustomerDetails().getCustomer().getCustCtgCode()
								+ "_ED",
						"_view");
				if (jountAccountDetailList.get(i) != null && i == 0) {
					spreadSheet.setCu1(customerService.getCustomerDetailForFinancials(
							financeDetail.getJountAccountDetailList().get(i).getCustCIF(), "_View"));
					setCustomerName(spreadSheet, spreadSheet.getCu1());
					dataMap.put("CoApp1DOB", DateUtility.format(spreadSheet.getCu1().getCustDOB(), "dd/MMM/yy"));
					dataMap.put("CoApp1Age",
							DateUtil.getYearsBetween(SysParamUtil.getAppDate(), spreadSheet.getCu1().getCustDOB()));
					dataMap.put("CoApp1MatAge",
							DateUtility.getYearsBetween(
									financeDetail.getFinScheduleData().getFinanceMain().getMaturityDate(),
									spreadSheet.getCu1().getCustDOB()));

					// method call to set share holding percentage
					long custid = jountAccountDetailList.get(i).getCustID();
					for (DirectorDetail details : financeDetail.getCustomerDetails().getCustomerDirectorList()) {
						if (details.getShareHolderCustID() == custid) {
							dataMap.put("CoApp1SharePerc", PennantApplicationUtil.formateAmount(details.getSharePerc(),
									PennantConstants.defaultCCYDecPos));
						}
					}
					//method call to set Co-applicants extended data
					setCoApplicantExtendedData(spreadSheet.getCu1(), extendedMapValues);
					//method call to set Co-applicants FI status
					setCoApplicantFiStatus(financeDetail,
							financeDetail.getJountAccountDetailList().get(i).getCustomerDetails(),
							jountAccountDetailList.get(i).getCustCIF(), i, dataMap);
					// method call to set Co-applicant address. 
					setCoAppAddresses(spreadSheet,
							financeDetail.getJountAccountDetailList().get(i).getCustomerDetails());

					String cibil = spreadSheet.getCu1().getCustAddlVar88();
					if (StringUtils.isNotBlank(cibil)) {
						cibilScores.add(Integer.parseInt(cibil));
					}
				}

				if (jountAccountDetailList.get(i) != null && i == 1) {
					spreadSheet.setCu2(customerService.getCustomerDetailForFinancials(
							financeDetail.getJountAccountDetailList().get(i).getCustCIF(), "_View"));
					setCustomerName(spreadSheet, spreadSheet.getCu2());
					dataMap.put("CoApp2DOB", DateUtility.format(spreadSheet.getCu2().getCustDOB(), "dd/MMM/yy"));
					dataMap.put("CoApp2Age",
							DateUtil.getYearsBetween(SysParamUtil.getAppDate(), spreadSheet.getCu2().getCustDOB()));
					dataMap.put("CoApp2MatAge",
							DateUtility.getYearsBetween(
									financeDetail.getFinScheduleData().getFinanceMain().getMaturityDate(),
									spreadSheet.getCu2().getCustDOB()));
					// method call to set share holding percentage
					long custid = jountAccountDetailList.get(i).getCustID();
					for (DirectorDetail details : financeDetail.getCustomerDetails().getCustomerDirectorList()) {
						if (details.getShareHolderCustID() == custid) {
							dataMap.put("CoApp2SharePerc", PennantApplicationUtil.formateAmount(details.getSharePerc(),
									PennantConstants.defaultCCYDecPos));
						}
					}
					//method call to set Co-applicants extended data
					setCoApplicantExtendedData(spreadSheet.getCu2(), extendedMapValues);
					//method call to set Co-applicants FI status
					setCoApplicantFiStatus(financeDetail,
							financeDetail.getJountAccountDetailList().get(i).getCustomerDetails(),
							jountAccountDetailList.get(i).getCustCIF(), i, dataMap);

					String cibil = spreadSheet.getCu2().getCustAddlVar88();
					if (StringUtils.isNotBlank(cibil)) {
						cibilScores.add(Integer.parseInt(cibil));
					}
				}

				if (jountAccountDetailList.get(i) != null && i == 2) {
					spreadSheet.setCu3(customerService.getCustomerDetailForFinancials(
							financeDetail.getJountAccountDetailList().get(i).getCustCIF(), "_View"));
					setCustomerName(spreadSheet, spreadSheet.getCu3());
					dataMap.put("CoApp3DOB", DateUtility.format(spreadSheet.getCu3().getCustDOB(), "dd/MMM/yy"));
					dataMap.put("CoApp3Age",
							DateUtil.getYearsBetween(SysParamUtil.getAppDate(), spreadSheet.getCu3().getCustDOB()));
					dataMap.put("CoApp3MatAge",
							DateUtility.getYearsBetween(
									financeDetail.getFinScheduleData().getFinanceMain().getMaturityDate(),
									spreadSheet.getCu3().getCustDOB()));
					// method call to set share holding percentage
					long custid = jountAccountDetailList.get(i).getCustID();
					for (DirectorDetail details : financeDetail.getCustomerDetails().getCustomerDirectorList()) {
						if (details.getShareHolderCustID() == custid) {
							dataMap.put("CoApp3SharePerc", PennantApplicationUtil.formateAmount(details.getSharePerc(),
									PennantConstants.defaultCCYDecPos));
						}
					}
					//method call to set Co-applicants extended data
					setCoApplicantExtendedData(spreadSheet.getCu3(), extendedMapValues);
					//method call to set Co-applicants FI status
					setCoApplicantFiStatus(financeDetail,
							financeDetail.getJountAccountDetailList().get(i).getCustomerDetails(),
							jountAccountDetailList.get(i).getCustCIF(), i, dataMap);
					String cibil = spreadSheet.getCu3().getCustAddlVar88();
					if (StringUtils.isNotBlank(cibil)) {
						cibilScores.add(Integer.parseInt(cibil));
					}

				}

				if (jountAccountDetailList.get(i) != null && i == 3) {
					spreadSheet.setCu4(customerService.getCustomerDetailForFinancials(
							financeDetail.getJountAccountDetailList().get(i).getCustCIF(), "_View"));
					setCustomerName(spreadSheet, spreadSheet.getCu4());
					dataMap.put("CoApp4DOB", DateUtility.format(spreadSheet.getCu4().getCustDOB(), "dd/MMM/yy"));
					dataMap.put("CoApp4Age",
							DateUtil.getYearsBetween(SysParamUtil.getAppDate(), spreadSheet.getCu4().getCustDOB()));
					dataMap.put("CoApp4MatAge",
							DateUtility.getYearsBetween(
									financeDetail.getFinScheduleData().getFinanceMain().getMaturityDate(),
									spreadSheet.getCu4().getCustDOB()));
					// method call to set share holding percentage
					long custid = jountAccountDetailList.get(i).getCustID();
					for (DirectorDetail details : financeDetail.getCustomerDetails().getCustomerDirectorList()) {
						if (details.getShareHolderCustID() == custid) {
							dataMap.put("CoApp4SharePerc", PennantApplicationUtil.formateAmount(details.getSharePerc(),
									PennantConstants.defaultCCYDecPos));
						}
					}
					//method call to set Co-applicants extended data
					setCoApplicantExtendedData(spreadSheet.getCu4(), extendedMapValues);
					//method call to set Co-applicants FI status
					setCoApplicantFiStatus(financeDetail,
							financeDetail.getJountAccountDetailList().get(i).getCustomerDetails(),
							jountAccountDetailList.get(i).getCustCIF(), i, dataMap);

					String cibil = spreadSheet.getCu4().getCustAddlVar88();
					if (StringUtils.isNotBlank(cibil)) {
						cibilScores.add(Integer.parseInt(cibil));
					}

				}

				if (jountAccountDetailList.get(i) != null && i == 4) {
					spreadSheet.setCu5(customerService.getCustomerDetailForFinancials(
							financeDetail.getJountAccountDetailList().get(i).getCustCIF(), "_View"));
					setCustomerName(spreadSheet, spreadSheet.getCu5());
					dataMap.put("CoApp5DOB", DateUtility.format(spreadSheet.getCu5().getCustDOB(), "dd/MMM/yy"));
					dataMap.put("CoApp5Age",
							DateUtil.getYearsBetween(SysParamUtil.getAppDate(), spreadSheet.getCu5().getCustDOB()));
					dataMap.put("CoApp5MatAge",
							DateUtility.getYearsBetween(
									financeDetail.getFinScheduleData().getFinanceMain().getMaturityDate(),
									spreadSheet.getCu5().getCustDOB()));
					// method call to set share holding percentage
					long custid = jountAccountDetailList.get(i).getCustID();
					for (DirectorDetail details : financeDetail.getCustomerDetails().getCustomerDirectorList()) {
						if (details.getShareHolderCustID() == custid) {
							dataMap.put("CoApp5SharePerc", PennantApplicationUtil.formateAmount(details.getSharePerc(),
									PennantConstants.defaultCCYDecPos));
						}
					}
					//method call to set Co-applicants extended data
					setCoApplicantExtendedData(spreadSheet.getCu5(), extendedMapValues);
					//method call to set Co-applicants FI status
					setCoApplicantFiStatus(financeDetail,
							financeDetail.getJountAccountDetailList().get(i).getCustomerDetails(),
							jountAccountDetailList.get(i).getCustCIF(), i, dataMap);
					String cibil = spreadSheet.getCu5().getCustAddlVar88();
					if (StringUtils.isNotBlank(cibil)) {
						cibilScores.add(Integer.parseInt(cibil));
					}
				}
			}
			setMinCibilScore(cibilScores, dataMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void setMinCibilScore(List<Integer> cibilScores, Map<String, Object> dataMap) {
		List<Integer> list = new ArrayList<>();
		int count = 0;
		for (Integer cibil : cibilScores) {
			if (cibil <= 299) {
				count++;
			} else {
				list.add(cibil);
			}
		}
		if (count > 0) {
			dataMap.put("minCibil", 0);
		} else {
			Object values[] = new Object[list.size()];
			values = list.toArray(values);
			Arrays.sort(values);
			dataMap.put("minCibil", values[0]);
		}
	}

	// method for setting up CO-Applicants Extended Data
	private void setCoApplicantExtendedData(Customer customer, List<Map<String, Object>> extendedMapValues) {
		logger.debug(Literal.ENTERING);
		if (CollectionUtils.isEmpty(extendedMapValues)) {
			return;
		}
		for (int i = 0; i < extendedMapValues.size(); i++) {
			if (!customer.getCustCtgCode().equals(PennantConstants.PFF_CUSTCTG_INDIV)
					&& extendedMapValues.get(i).get("smecibilscore") != null
					&& extendedMapValues.get(i).get("natureofbusiness") != null
					&& extendedMapValues.get(i).get("industry") != null
					&& extendedMapValues.get(i).get("segment") != null
					&& extendedMapValues.get(i).get("product") != null) {
				customer.setCustAddlVar88(extendedMapValues.get(i).get("smecibilscore").toString());
				customer.setCustAddlVar8(getExtFieldDesc("clix_natureofbusiness",
						extendedMapValues.get(i).get("natureofbusiness").toString()));
				customer.setCustAddlVar9(
						getExtFieldDesc("clix_industry", extendedMapValues.get(i).get("industry").toString()));
				customer.setCustAddlVar10(
						getExtFieldDesc("clix_segment", extendedMapValues.get(i).get("segment").toString()));
				customer.setCustAddlVar11(
						getExtFieldDesc("clix_product", extendedMapValues.get(i).get("product").toString()));
				String margin = getExtFieldIndustryMargin("clix_industrymargin", customer.getCustAddlVar8(),
						customer.getCustAddlVar9(), customer.getCustAddlVar10(), customer.getCustAddlVar11());
				if (StringUtils.isNotBlank(margin)) {
					customer.setIndustryMargin(PennantApplicationUtil.formateAmount(new BigDecimal(margin),
							PennantConstants.defaultCCYDecPos));
				}

				if (extendedMapValues.get(i).get("custSubIndustry") != null) {
					customer.setSubIndustry(extendedMapValues.get(i).get("custSubIndustry").toString());
				}

			} else {
				if (extendedMapValues.get(i).get("cibilscore") != null) {
					customer.setCustAddlVar88(extendedMapValues.get(i).get("cibilscore").toString());
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void setCorporateFinancialData(FinanceDetail fd, Map<String, Object> dataMap) {
		logger.debug(Literal.ENTERING);
		long custId = fd.getCustomerDetails().getCustomer().getCustID();
		List<FinCreditReviewDetails> idList = creditApplicationReviewService.getFinCreditRevDetailIds(custId);

		if (CollectionUtils.isEmpty(idList)) {
			return;
		}

		String maxAuditYear = creditApplicationReviewService.getMaxAuditYearByCustomerId(custId, "_VIEW");

		int year2 = Integer.parseInt(maxAuditYear) - 1;
		int year3 = Integer.parseInt(maxAuditYear) - 2;

		dataMap.put("F1.MAXYEAR", "31-Mar-" + maxAuditYear);
		dataMap.put("F1.MAXYEAR.2", "31-Mar-" + year2);
		dataMap.put("F1.MAXYEAR.1", "31-Mar-" + year3);
		try {
			for (FinCreditReviewDetails id : idList) {
				Map<String, Object> tempMap1 = new HashMap<>();
				tempMap1 = creditApplicationReviewService.getFinCreditRevSummaryDetails(id.getId(), id.getAuditYear());

				for (String str : tempMap1.keySet()) {
					String strTemp = str;
					if (id.getAuditYear().equals(maxAuditYear)) {
						str = "F1." + (str) + "." + ("3");
					} else if (id.getAuditYear().equals(String.valueOf(year2))) {
						str = "F1." + (str) + "." + ("2");
					} else if (id.getAuditYear().equals(String.valueOf(year3))) {
						str = "F1." + (str) + "." + ("1");

					}
					dataMap.put(str, tempMap1.get(strTemp));
					dataMap.put(str, PennantApplicationUtil.formateAmount(
							getAmountInLakhs(tempMap1.get(strTemp).toString()), PennantConstants.defaultCCYDecPos));
				}
			}
			if (fd.getJountAccountDetailList() != null && !fd.getJountAccountDetailList().isEmpty()) {
				int i = 2;
				for (JointAccountDetail accountDetail : fd.getJountAccountDetailList()) {
					List<FinCreditReviewDetails> coAppidList = creditApplicationReviewService
							.getFinCreditRevDetailIds(accountDetail.getCustID());
					String coApp1MaxAuditYear = creditApplicationReviewService
							.getMaxAuditYearByCustomerId(accountDetail.getCustID(), "_VIEW");

					int coApp1year2 = Integer.parseInt(coApp1MaxAuditYear) - 1;
					int coApp1year3 = Integer.parseInt(coApp1MaxAuditYear) - 2;

					dataMap.put("F" + i + ".MAXYEAR", "31-Mar-" + coApp1MaxAuditYear);
					dataMap.put("F" + i + ".MAXYEAR.2", "31-Mar-" + coApp1year2);
					dataMap.put("F" + i + ".MAXYEAR.1", "31-Mar-" + coApp1year3);
					if (CollectionUtils.isNotEmpty(coAppidList)) {
						for (FinCreditReviewDetails id : coAppidList) {
							Map<String, Object> tempMap2 = new HashMap<>();
							tempMap2 = creditApplicationReviewService.getFinCreditRevSummaryDetails(id.getId(),
									id.getAuditYear());

							for (String str : tempMap2.keySet()) {
								String strTemp = str;
								if (id.getAuditYear().equals(coApp1MaxAuditYear)) {
									str = "F" + i + "." + (str) + "." + ("3");
								} else if (id.getAuditYear().equals(String.valueOf(coApp1year2))) {
									str = "F" + i + "." + (str) + "." + ("2");
								} else if (id.getAuditYear().equals(String.valueOf(coApp1year3))) {
									str = "F" + i + "." + (str) + "." + ("1");
								}

								dataMap.put(str, tempMap2.get(strTemp));
								dataMap.put(str,
										PennantApplicationUtil.formateAmount(
												getAmountInLakhs(tempMap2.get(strTemp).toString()),
												PennantConstants.defaultCCYDecPos));
							}
						}
					}
					i = i + 1;
				}
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void setCustomerGstDetails(FinanceDetail fd, Map<String, Object> dataMap) {
		logger.debug(Literal.ENTERING);
		Set<String> keySet = null;
		Iterator<String> iterator = null;
		CustomerDetails customerDetails = fd.getCustomerDetails();
		List<CustomerGST> customerGsts = customerDetails.getCustomerGstList();
		if (CollectionUtils.isEmpty(customerGsts)) {
			return;
		}
		try {
			int k = 1;
			for (int i = 0; i < customerGsts.size(); i++) {
				List<CustomerGSTDetails> customerGSTDetails = customerGsts.get(i).getCustomerGSTDetailslist();
				if (CollectionUtils.isNotEmpty(customerGSTDetails)) {
					Map<String, Object> gstDetailsMap = new HashMap<String, Object>();
					for (CustomerGSTDetails detail : customerGSTDetails) {
						gstDetailsMap.put(detail.getFrequancy() + "-" + (detail.getFinancialYear()),
								PennantApplicationUtil.formateAmount(getAmountInLakhs(detail.getSalAmount().toString()),
										PennantConstants.defaultCCYDecPos));
					}
					if (!customerGsts.get(i).getFrequencytype().equals("Quarterly")) {
						dataMap.put("Gst" + k + "Freq", customerGsts.get(i).getFrequencytype());
						keySet = gstDetailsMap.keySet();
						iterator = keySet.iterator();
						String[] months = new String[] { "", "jan", "feb", "mar", "apr", "may", "jun", "jul", "aug",
								"sep", "oct", "nov", "dec" };
						while (iterator.hasNext()) {
							String gstKey = (String) iterator.next();
							for (int j = 0; j < months.length; j++) {
								if (gstKey.substring(0, 3).equalsIgnoreCase(months[j])) {
									dataMap.put("Gst" + i + "Month" + j, gstDetailsMap.get(gstKey));
								}
							}
						}
					} else {
						dataMap.put("Gst" + k + "Freq", customerGsts.get(i).getFrequencytype());
						keySet = gstDetailsMap.keySet();
						iterator = keySet.iterator();
						String[] quarters = new String[] { "", "Q1", "Q2", "Q3", "Q4" };
						while (iterator.hasNext()) {
							String gstKey = (String) iterator.next();
							for (int m = 0; m < quarters.length; m++) {
								if (gstKey.substring(0, 2).equalsIgnoreCase(quarters[m])) {
									dataMap.put("Gst" + i + "Month" + m, gstDetailsMap.get(gstKey));
								}
							}
						}
					}
				}
				k++;
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void setExternalLiabilites(FinanceDetail fd, Map<String, Object> dataMap) {
		logger.debug(Literal.ENTERING);
		List<CustomerExtLiability> extList = fd.getCustomerDetails().getCustomerExtLiabilityList();
		int format = CurrencyUtil.getFormat(fd.getFinScheduleData().getFinanceMain().getFinCcy());
		if (CollectionUtils.isEmpty(extList)) {
			return;
		}

		try {
			int i = 0;
			for (CustomerExtLiability cel : extList) {
				dataMap.put("Ext_LoanBankName" + i, cel.getLoanBankName());
				dataMap.put("Ext_LoanType" + i, cel.getFinTypeDesc());
				dataMap.put("Ext_LoanCategory" + i, cel.getSecurityDetails());
				if (cel.getFinStatus().equals("M1")) {
					dataMap.put("Ext_LoanStatus" + i, "Live");
				} else if (cel.getFinStatus().equals("M2")) {
					dataMap.put("Ext_LoanStatus" + i, "Closed");
				} else if (cel.getFinStatus().equals("M3")) {
					dataMap.put("Ext_LoanStatus" + i, "Will be Closed");
				} else if (cel.getFinStatus().equals("M4")) {
					dataMap.put("Ext_LoanStatus" + i, "Not to Be Obligated");
				}
				dataMap.put("Ext_LoanAmount" + i, PennantApplicationUtil.formateAmount(
						getAmountInLakhs(cel.getOriginalAmount().toString()), PennantConstants.defaultCCYDecPos));
				dataMap.put("Ext_LoanEMI" + i, PennantApplicationUtil.amountFormate(cel.getInstalmentAmount(), format));
				dataMap.put("Ext_LoanROI" + i, PennantApplicationUtil.formateAmount(cel.getRateOfInterest(),
						PennantConstants.defaultCCYDecPos));
				dataMap.put("Ext_LoanTenure" + i, cel.getTenure());
				dataMap.put("Ext_LoanStartDate" + i, cel.getFinDate());
				// trackCheckFrom - based on the int value
				if (cel.getCheckedBy() == 0) {
					dataMap.put("Ext_LoanTrackCheckFrom" + i, "SOA");
				} else if (cel.getCheckedBy() == 1) {
					dataMap.put("Ext_LoanTrackCheckFrom" + i, "Banking");
				} else if (cel.getCheckedBy() == 2) {
					dataMap.put("Ext_LoanTrackCheckFrom" + i, "Cibil");
				}

				List<ExtLiabilityPaymentdetails> paymentDetails = cel.getExtLiabilitiesPayments();
				if (CollectionUtils.isNotEmpty(paymentDetails)) {
					Map<String, String> paymentDetailsMap = new HashMap<String, String>();
					for (ExtLiabilityPaymentdetails details : paymentDetails) {
						paymentDetailsMap.put(details.getEmiType(), details.getEmiClearance());
					}

					int l = 6;
					for (int k = 6; k > 0; k--) {
						YearMonth date = YearMonth.now().minusMonths(k);
						String monthName = date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
						String month = (monthName + "-" + date.getYear());
						dataMap.put("Month" + l, month);
						if (paymentDetailsMap.get(month) != null && paymentDetailsMap.containsKey(month)) {
							if (paymentDetailsMap.get(month).equals(PennantConstants.WAITING_CLEARANCE)) {
								dataMap.put("Ext" + i + "Mon" + l,
										Labels.getLabel("listheader_ExternalLiability_WaitingClearance.value"));
							} else if (paymentDetailsMap.get(month).equals(PennantConstants.CLEARED)) {
								dataMap.put("Ext" + i + "Mon" + l,
										Labels.getLabel("listheader_ExternalLiability_Cleared.value"));
							} else if (paymentDetailsMap.get(month).equals(PennantConstants.BOUNCED)) {
								dataMap.put("Ext" + i + "Mon" + l,
										Labels.getLabel("listheader_ExternalLiability_Bounced.value"));
							}
							l = l - 1;
						}
					}
				}

				i++;
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void setBankingDetails(FinanceDetail fd, Map<String, Object> dataMap) {
		logger.debug(Literal.ENTERING);
		List<CustomerBankInfo> bankingDetails = fd.getCustomerDetails().getCustomerBankInfoList();
		if (CollectionUtils.isEmpty(bankingDetails)) {
			return;
		}
		try {
			for (int i = 0; i < bankingDetails.size(); i++) {
				dataMap.put("B" + i + ".BankName", bankingDetails.get(i).getLovDescBankName());
				dataMap.put("B" + i + ".AccountNum", bankingDetails.get(i).getAccountNumber());
				if (bankingDetails.get(i).getAccountType().equals(PennantConstants.ACCOUNTTYPE_CA)
						|| bankingDetails.get(i).getAccountType().equals(PennantConstants.ACCOUNTTYPE_SA)) {
					dataMap.put("B" + i + ".TypeofAcc", bankingDetails.get(i).getLovDescAccountType());
				} else {
					dataMap.put("B" + i + ".TypeofAcc", "CC/OD Account");
				}
				dataMap.put("B" + i + ".SanctionedLimit",
						PennantApplicationUtil.formateAmount(
								getAmountInLakhs(bankingDetails.get(i).getCcLimit().toString()),
								PennantConstants.defaultCCYDecPos));
				List<BankInfoDetail> bankInfoDetails = bankingDetails.get(i).getBankInfoDetails();
				if (CollectionUtils.isNotEmpty(bankInfoDetails)) {
					Map<String, BankInfoDetail> bankInfoDetailsMap = new HashMap<String, BankInfoDetail>();

					for (BankInfoDetail detail : bankInfoDetails) {
						Date date = detail.getMonthYear();
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
						String strDate = dateFormat.format(date);
						bankInfoDetailsMap.put(strDate, detail);
					}
					String finType = fd.getFinScheduleData().getFinanceMain().getFinType();
					setBankingSubDetails(dataMap, bankInfoDetails, bankInfoDetailsMap, finType, i);
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void setBankingSubDetails(Map<String, Object> dataMap, List<BankInfoDetail> bankInfoDetails,
			Map<String, BankInfoDetail> bankInfoDetailsMap, String finType, int i) {
		int l = -1;
		int j = 0;
		if (finType.equalsIgnoreCase("LAEP")) {
			j = 12;
		} else {
			j = 6;
		}
		for (int m = j; m > 0; m--) {
			YearMonth date = YearMonth.now().minusMonths(m);
			String monthValue = String.valueOf(date.getMonth().getValue());
			if (monthValue.length() == 1) {
				monthValue = "0" + monthValue;
			}
			String month = date.getYear() + "-" + monthValue;
			if (bankInfoDetailsMap.containsKey(month)) {
				l++;
			}
		}

		for (int k = j; k > 0; k--) {
			YearMonth date = YearMonth.now().minusMonths(k);
			String monthValue = String.valueOf(date.getMonth().getValue());
			if (monthValue.length() == 1) {
				monthValue = "0" + monthValue;
			}
			String month = date.getYear() + "-" + monthValue;

			if (bankInfoDetailsMap.containsKey(month)) {
				dataMap.put("Bank" + i + "Mon" + l, month);
				dataMap.put("Bank" + i + "Mon" + l + "Cr",
						PennantApplicationUtil.formateAmount(
								getAmountInLakhs(bankInfoDetailsMap.get(month).getCreditAmt().toString()),
								PennantConstants.defaultCCYDecPos));
				dataMap.put("Bank" + i + "Mon" + l + "DebitAmt",
						PennantApplicationUtil.formateAmount(
								getAmountInLakhs(bankInfoDetailsMap.get(month).getDebitAmt().toString()),
								PennantConstants.defaultCCYDecPos));
				dataMap.put("Bank" + i + "Mon" + l + "SanctionedLmt",
						PennantApplicationUtil.formateAmount(
								getAmountInLakhs(bankInfoDetailsMap.get(month).getSanctionLimit().toString()),
								PennantConstants.defaultCCYDecPos));
				dataMap.put("Bank" + i + "Mon" + l + "NoOfCr", bankInfoDetailsMap.get(month).getCreditNo());
				dataMap.put("Bank" + i + "Mon" + l + "NoOfDebit", bankInfoDetailsMap.get(month).getDebitNo());
				dataMap.put("Bank" + i + "Mon" + l + "InwBounce", PennantApplicationUtil
						.formateAmount(bankInfoDetailsMap.get(month).getBounceIn(), PennantConstants.defaultCCYDecPos));
				dataMap.put("Bank" + i + "Mon" + l + "OutBounce", PennantApplicationUtil.formateAmount(
						bankInfoDetailsMap.get(month).getBounceOut(), PennantConstants.defaultCCYDecPos));
				if (CollectionUtils.isNotEmpty(bankInfoDetailsMap.get(month).getBankInfoSubDetails())) {
					dataMap.put("Bank" + i + "Mon" + l + "Avgbal",
							PennantApplicationUtil
									.formateAmount(
											getAmountInLakhs(bankInfoDetailsMap.get(month).getBankInfoSubDetails()
													.get(0).getBalance().toString()),
											PennantConstants.defaultCCYDecPos));
				}
				dataMap.put("Bank" + i + "Mon" + l + "PeakUtilLev",
						PennantApplicationUtil.formateAmount(
								getAmountInLakhs(bankInfoDetailsMap.get(month).getPeakUtilizationLevel().toString()),
								PennantConstants.defaultCCYDecPos));
				dataMap.put("Bank" + i + "Mon" + l + "AvgutilizationPerc", PennantApplicationUtil.amountFormate(
						bankInfoDetailsMap.get(month).getAvgUtilization(), PennantConstants.defaultCCYDecPos));
				dataMap.put("Bank" + i + "Mon" + l + "SettlmntNo", bankInfoDetailsMap.get(month).getSettlementNo());
				if (bankInfoDetailsMap.get(month).getSettlementCredits() != null) {
					dataMap.put("Bank" + i + "Mon" + l + "SettlmntCr",
							PennantApplicationUtil.formateAmount(
									getAmountInLakhs(bankInfoDetailsMap.get(month).getSettlementCredits().toString()),
									PennantConstants.defaultCCYDecPos));
				}
				l = l - 1;
			}
		}
	}

	private void setCardSalesDetails(FinanceDetail fd, Map<String, Object> dataMap) {
		logger.debug(Literal.ENTERING);
		List<CustCardSales> custCardSales = fd.getCustomerDetails().getCustCardSales();
		if (CollectionUtils.isEmpty(custCardSales)) {
			return;
		}
		try {
			for (int i = 0; i < custCardSales.size(); i++) {
				List<CustCardSalesDetails> custCardMonthSales = custCardSales.get(i).getCustCardMonthSales();
				if (CollectionUtils.isNotEmpty(custCardMonthSales)) {
					Map<String, CustCardSalesDetails> cardDetailsMap = new HashMap<String, CustCardSalesDetails>();
					for (CustCardSalesDetails custCardSalesDetails : custCardMonthSales) {
						Date date = custCardSalesDetails.getMonth();
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
						String strDate = dateFormat.format(date);
						cardDetailsMap.put(strDate, custCardSalesDetails);
					}
					int l = 0;
					for (int k = 12; k > 0; k--) {
						YearMonth date = YearMonth.now().minusMonths(k);
						String monthValue = String.valueOf(date.getMonth().getValue());
						if (monthValue.length() == 1) {
							monthValue = "0" + monthValue;
						}
						String month = date.getYear() + "-" + monthValue;
						if (cardDetailsMap.containsKey(month)) {
							dataMap.put("SalesMon" + l, month);
							dataMap.put("mon" + l + "Sales",
									PennantApplicationUtil.formateAmount(
											getAmountInLakhs(cardDetailsMap.get(month).getSalesAmount().toString()),
											PennantConstants.defaultCCYDecPos));
							dataMap.put("mon" + l + "Settlements", cardDetailsMap.get(month).getNoOfSettlements());
							dataMap.put("mon" + l + "TotalCredit",
									PennantApplicationUtil.formateAmount(
											getAmountInLakhs(
													cardDetailsMap.get(month).getTotalCreditValue().toString()),
											PennantConstants.defaultCCYDecPos));
							dataMap.put("mon" + l + "TotalDebit",
									PennantApplicationUtil.formateAmount(
											getAmountInLakhs(cardDetailsMap.get(month).getTotalDebitValue().toString()),
											PennantConstants.defaultCCYDecPos));
							dataMap.put("mon" + l + "NoofDebits", cardDetailsMap.get(month).getTotalNoOfDebits());
							dataMap.put("mon" + l + "InwardBounce", cardDetailsMap.get(month).getInwardBounce());
							dataMap.put("mon" + l + "OutwardBounce", cardDetailsMap.get(month).getOutwardBounce());
							dataMap.put("mon" + l + "NoofCredits", cardDetailsMap.get(month).getTotalNoOfCredits());
							l = l + 1;
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	//converting string value into BigDecimal
	private BigDecimal getAmountInLakhs(String value) {
		BigDecimal amount = new BigDecimal(value);
		return amount.divide(LAKH);
	}

	@Autowired
	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	@Autowired
	public void setVerificationService(VerificationService verificationService) {
		this.verificationService = verificationService;
	}

	@Autowired
	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	@Autowired
	public void setCreditApplicationReviewService(CreditApplicationReviewService creditApplicationReviewService) {
		this.creditApplicationReviewService = creditApplicationReviewService;
	}
}
