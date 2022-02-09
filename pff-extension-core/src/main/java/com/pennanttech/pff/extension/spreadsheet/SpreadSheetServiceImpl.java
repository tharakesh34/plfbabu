package com.pennanttech.pff.extension.spreadsheet;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.CurrencyUtil;
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
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.customermasters.ExtLiabilityPaymentdetails;
import com.pennant.backend.model.finance.CreditReviewData;
import com.pennant.backend.model.finance.CreditReviewDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennant.backend.model.spreadsheet.SpreadSheet;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.pff.service.spreadsheet.SpreadSheetService;
import com.pennanttech.pennapps.pff.verification.model.Verification;

public class SpreadSheetServiceImpl implements SpreadSheetService {
	private static final Logger logger = LogManager.getLogger(SpreadSheetServiceImpl.class);
	private SpreadSheetDataAccess dataAccess;

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> setSpreadSheetData(Map<String, Object> screenData, FinanceDetail financeDetail) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> dataMap = new HashMap<>();

		CreditReviewDetails crd = new CreditReviewDetails();
		String parameters = SysParamUtil.getValueAsString(SMTParameterConstants.CREDIT_ELG_PARAMS);

		if (parameters == null) {
			return map;
		}

		String finReference = getStringValue("FinReference", screenData);
		BigDecimal finAmount = getDecimalValue("FinAmount", screenData);

		String[] elgParameters = parameters.split(",");

		for (String elgParm : elgParameters) {
			elgParm = elgParm.trim().toUpperCase();
			if ("FINTYPE".equals(elgParm)) {
				crd.setProduct(getStringValue("FinType", screenData));
			} else if ("ELIGIBILITYMETHOD".equals(elgParm)) {
				crd.setEligibilityMethod(getStringValue("EligibilityMethod", screenData));
			} else if ("EMPLOYMENTTYPE".equals(elgParm)) {
				crd.setEmploymentType(getStringValue("EmpType", screenData));
			}
		}

		crd = dataAccess.getCreditReviewDetailsByLoanType(crd);

		if (crd == null) {
			return map;
		}

		CreditReviewData creditReviewData = null;
		creditReviewData = dataAccess.getCreditReviewDataByRef(finReference, crd);

		financeDetail.setCreditReviewData(creditReviewData);

		SpreadSheet spreadSheet = new SpreadSheet();

		FinanceDetail fd = new FinanceDetail();
		BeanUtils.copyProperties(financeDetail, fd);

		if (fd.isSpreadSheetloaded()) {
			return dataMap;
		}

		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();
		CustomerDetails cd = fd.getCustomerDetails();
		Customer cu = cd.getCustomer();

		fm.setFinAmount(finAmount);
		spreadSheet.setFm(fm);
		spreadSheet.setCu(cu);

		dataMap.put("finStartDate", fm.getFinStartDate());
		dataMap.put("CustDOB", cu.getCustDOB());
		dataMap.put("CustAge", DateUtil.getYearsBetween(SysParamUtil.getAppDate(), cu.getCustDOB()));
		dataMap.put("CustMatAge", DateUtil.getYearsBetween(fm.getMaturityDate(), cu.getCustDOB()));

		setCustomerName(cu);

		setCustomerPhoneNumber(spreadSheet, cd);

		setCustomerAddresses(spreadSheet, cd);

		setExtendedData(fd, cu, dataMap);

		setCoApplicantExtendedData(spreadSheet, fd, dataMap);

		setKeyFigures(dataMap, fm);

		if (fd.getCustomerDetails().getExtendedFieldRender() != null) {
			spreadSheet.setEf(fd.getCustomerDetails().getExtendedFieldRender().getMapValues());
		}
		if (fd.getExtendedFieldRender() != null) {
			spreadSheet.setLoanEf(fd.getExtendedFieldRender().getMapValues());
		}

		setCoApplicantData(financeDetail, dataMap, spreadSheet);

		setCorporateFinancialData(dataMap, fd);

		setCustomerGstDetails(dataMap, fd);

		setExternalLiabilites(dataMap, fd);

		setBankingDetails(dataMap, fd);

		setCardSalesDetails(dataMap, fd);

		setAppCoAppIncomeDetails(dataMap, fd);

		dataMap.put("finStartDate", fm.getFinStartDate());
		fd.setSpreadSheetloaded(true);

		List<FinanceScheduleDetail> schedules = fd.getFinScheduleData().getFinanceScheduleDetails();
		if (schedules.size() > 0) {
			int format = PennantConstants.defaultCCYDecPos;
			fm.setRepayProfitRate(CurrencyUtil.parse(schedules.get(0).getCalculatedRate(), format));
			spreadSheet.setEmiAmount(CurrencyUtil.parse(schedules.get(2).getRepayAmount(), format));

		} else {
			fm.setRepayProfitRate(BigDecimal.ZERO);
		}

		if (screenData.containsKey("JointAccountDetails")) {
			crd.setExtLiabilitiesjointAccDetails((List<JointAccountDetail>) screenData.get("JointAccountDetails"));
		}

		map.put("financeDetail", fd);
		map.put("userRole", getStringValue("UserRole", screenData));
		map.put("creditReviewDetails", crd);
		map.put("isEditable", getBooleanValue("Right_EligibilitySal", screenData));

		map.put("creditReviewData", creditReviewData);
		map.put("financeMainDialogCtrl", this);
		map.put("dataMap", dataMap);

		return map;
	}

	private void setAppCoAppIncomeDetails(Map<String, Object> dataMap, FinanceDetail fd) {
		Map<String, List<CustomerIncome>> incomeMap = new LinkedHashMap<>();
		int i = 1;
		List<CustomerIncome> applicantIncomes = fd.getCustomerDetails().getCustomerIncomeList();

		incomeMap.put("appincomes", applicantIncomes);

		if (CollectionUtils.isNotEmpty(fd.getJointAccountDetailList())) {
			for (JointAccountDetail jointAccountDetails : fd.getJointAccountDetailList()) {
				if (CollectionUtils.isNotEmpty(jointAccountDetails.getCustomerIncomeList())) {
					incomeMap.put("coAppincomes".concat(String.valueOf(i)),
							jointAccountDetails.getCustomerIncomeList());
				}
				i++;
			}
		}

		i = 0;
		String key = "";
		for (List<CustomerIncome> customerIncomes : incomeMap.values()) {
			if (CollectionUtils.isEmpty(customerIncomes)) {
				continue;
			}

			if (i != 0) {
				key = "CoApp".concat(String.valueOf(i));
			}

			int format = PennantConstants.defaultCCYDecPos;
			for (CustomerIncome customerIncome : customerIncomes) {
				BigDecimal income = customerIncome.getIncome();
				String formatedIncome = CurrencyUtil.format(income, format);
				if (PennantConstants.INC_CATEGORY_SALARY.equals(customerIncome.getCategory())) {
					switch (customerIncome.getIncomeType()) {
					case "BS":
						dataMap.put(key.concat("Basic"), formatedIncome);
						break;
					case "GP":
						dataMap.put(key.concat("GP"), formatedIncome);
						break;
					case "DA":
						dataMap.put(key.concat("DA"), formatedIncome);
						break;
					case "HOU21":
						dataMap.put(key.concat("HRA"), formatedIncome);
						break;
					case "CLA":
						dataMap.put(key.concat("CLA"), formatedIncome);
						break;
					case "MED44":
						dataMap.put(key.concat("MEDA"), formatedIncome);
						break;
					case "SA":
						dataMap.put(key.concat("SA"), formatedIncome);
						break;
					case "OA":
						dataMap.put(key.concat("OA"), formatedIncome);
						break;
					case "CV":
						dataMap.put(key.concat("CV"), formatedIncome);
						break;
					case "VP":
						dataMap.put(key.concat("VP"), formatedIncome);
						break;

					default:
						break;
					}
				} else if (StringUtils.equals(customerIncome.getCategory(), PennantConstants.INC_CATEGORY_OTHER)) {
					switch (customerIncome.getIncomeType()) {
					case "RENINC":
						dataMap.put(key.concat("RENINC"), formatedIncome);
						dataMap.put(key.concat("Net_RENINC"), formatedIncome);
						break;
					case "INTINC":
						dataMap.put(key.concat("INTINC"), formatedIncome);
						dataMap.put(key.concat("Net_INTINC"), formatedIncome);
						break;

					default:
						break;
					}

				}
			}
			++i;
		}
	}

	private void setCardSalesDetails(Map<String, Object> dataMap, FinanceDetail fd) {
		List<CustCardSales> custCardSales = fd.getCustomerDetails().getCustCardSales();

		if (CollectionUtils.isEmpty(custCardSales)) {
			return;
		}

		for (int i = 0; i < custCardSales.size(); i++) {
			List<CustCardSalesDetails> custCardMonthSales = custCardSales.get(i).getCustCardMonthSales();

			if (CollectionUtils.isEmpty(custCardMonthSales)) {
				continue;
			}

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

				if (!cardDetailsMap.containsKey(month)) {
					continue;
				}

				dataMap.put("SalesMon" + l, month);
				CustCardSalesDetails csd = cardDetailsMap.get(month);
				int format = PennantConstants.defaultCCYDecPos;
				dataMap.put("mon" + l + "Sales", CurrencyUtil.format(csd.getSalesAmount(), format));
				dataMap.put("mon" + l + "Settlements", csd.getNoOfSettlements());
				dataMap.put("mon" + l + "TotalCredit", CurrencyUtil.format(csd.getTotalCreditValue(), format));
				dataMap.put("mon" + l + "TotalDebit", CurrencyUtil.format(csd.getTotalDebitValue(), format));
				dataMap.put("mon" + l + "NoofDebits", csd.getTotalNoOfDebits());
				dataMap.put("mon" + l + "InwardBounce", csd.getInwardBounce());
				dataMap.put("mon" + l + "OutwardBounce", csd.getOutwardBounce());
				dataMap.put("mon" + l + "NoofCredits", csd.getTotalNoOfCredits());
				l = l + 1;
			}
		}
	}

	private void setBankingDetails(Map<String, Object> dataMap, FinanceDetail fd) {
		List<CustomerBankInfo> bankingDetails = fd.getCustomerDetails().getCustomerBankInfoList();
		int format = CurrencyUtil.getFormat(fd.getFinScheduleData().getFinanceMain().getFinCcy());

		if (CollectionUtils.isEmpty(bankingDetails)) {
			return;
		}

		int i = 0;
		for (CustomerBankInfo bi : bankingDetails) {
			String baseKey = "B" + i++;
			dataMap.put(baseKey + ".BankName", bi.getLovDescBankName());
			dataMap.put(baseKey + ".AccountNum", bi.getAccountNumber());
			dataMap.put(baseKey + ".TypeofAcc", bi.getLovDescAccountType());
			dataMap.put(baseKey + ".SanctionedLimit", bi.getCcLimit());

			List<BankInfoDetail> bankInfoDetails = bi.getBankInfoDetails();

			if (CollectionUtils.isEmpty(bankInfoDetails)) {
				continue;
			}

			Map<String, BankInfoDetail> bankInfoDetailsMap = new HashMap<String, BankInfoDetail>();

			for (BankInfoDetail detail : bankInfoDetails) {
				Date date = detail.getMonthYear();
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
				String strDate = dateFormat.format(date);
				bankInfoDetailsMap.put(strDate, detail);
			}

			int l = 0;
			for (int k = 6; k > 0; k--) {
				YearMonth date = YearMonth.now().minusMonths(k);
				String monthValue = String.valueOf(date.getMonth().getValue());
				if (monthValue.length() == 1) {
					monthValue = "0" + monthValue;
				}
				String month = date.getYear() + "-" + monthValue;

				if (bankInfoDetailsMap.containsKey(month)) {
					BankInfoDetail bd = bankInfoDetailsMap.get(month);
					String key = "Bank" + i + "Mon" + l;
					dataMap.put(key, month);
					dataMap.put(key + "Cr", CurrencyUtil.format(bd.getCreditAmt(), format));
					dataMap.put(key + "DebitAmt", CurrencyUtil.format(bd.getDebitAmt(), format));
					dataMap.put(key + "SanctionedLimit", CurrencyUtil.format(bd.getSanctionLimit(), format));
					dataMap.put(key + "NoOfCr", bd.getCreditNo());
					dataMap.put(key + "NoOfDebit", bd.getDebitNo());
					dataMap.put(key + "InwBounce", CurrencyUtil.format(bd.getBounceIn(), format));
					dataMap.put(key + "OutBounce", CurrencyUtil.format(bd.getBounceOut(), format));

					if (CollectionUtils.isNotEmpty(bd.getBankInfoSubDetails())) {
						dataMap.put(key + "AvgBal",
								CurrencyUtil.format(bd.getBankInfoSubDetails().get(0).getBalance(), format));
					}
					/*
					 * dataMap.put("Bank" + i + "Mon" + l + "PeakUtilLev",
					 * bankInfoDetailsMap.get(month).getPeakUtilizationLevel());
					 */
					dataMap.put(key + "AvgutilizationPerc", bd.getAvgUtilization());

					l = l + 1;

				}

			}

		}
	}

	private void setExternalLiabilites(Map<String, Object> dataMap, FinanceDetail fd) {
		List<CustomerExtLiability> extList = fd.getCustomerDetails().getCustomerExtLiabilityList();
		int format = CurrencyUtil.getFormat(fd.getFinScheduleData().getFinanceMain().getFinCcy());

		if (CollectionUtils.isEmpty(extList)) {
			return;
		}

		for (int i = 0; i < extList.size(); i++) {
			CustomerExtLiability el = extList.get(i);
			dataMap.put("Ext_LoanBankName" + i, el.getLoanBankName());
			dataMap.put("Ext_LoanType" + i, el.getFinTypeDesc());
			dataMap.put("Ext_LoanCategory" + i, el.getSecurityDetails());

			if (el.getFinStatus().equals("M0")) {
				dataMap.put("Ext_LoanStatus" + i, "Live");
			}

			dataMap.put("Ext_LoanAmount" + i, CurrencyUtil.format(el.getOriginalAmount(), format));
			dataMap.put("Ext_LoanEMI" + i, CurrencyUtil.format(el.getInstalmentAmount(), format));
			dataMap.put("Ext_LoanROI" + i, CurrencyUtil.format(el.getRateOfInterest(), format));
			dataMap.put("Ext_LoanTenure" + i, el.getTenure());
			dataMap.put("Ext_LoanStartDate" + i, el.getFinDate());

			// trackCheckFrom - based on the int value
			if (el.getCheckedBy() == 0) {
				dataMap.put("Ext_LoanTrackCheckFrom" + i, "SOA");
			} else if (el.getCheckedBy() == 1) {
				dataMap.put("Ext_LoanTrackCheckFrom" + i, "Banking");
			} else if (el.getCheckedBy() == 2) {
				dataMap.put("Ext_LoanTrackCheckFrom" + i, "Cibil");
			}

			List<ExtLiabilityPaymentdetails> paymentDetails = el.getExtLiabilitiesPayments();
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
					if (paymentDetailsMap.containsKey(month)) {
						if (paymentDetailsMap.get(month).equals(Boolean.TRUE)) {
							dataMap.put("Ext" + i + "Mon" + l, "Cleared");
						} else {
							dataMap.put("Ext" + i + "Mon" + l, "Not Cleared");
						}
						l = l - 1;
					}
				}
			}

		}
	}

	private void setCustomerGstDetails(Map<String, Object> dataMap, FinanceDetail fd) {
		CustomerDetails customerDetails = fd.getCustomerDetails();
		List<CustomerGST> customerGsts = customerDetails.getCustomerGstList();

		if (CollectionUtils.isEmpty(customerGsts)) {
			return;
		}

		for (int i = 0; i < customerGsts.size(); i++) {
			List<CustomerGSTDetails> customerGSTDetails = customerGsts.get(i).getCustomerGSTDetailslist();

			if (CollectionUtils.isEmpty(customerGSTDetails)) {
				continue;
			}

			Map<String, BigDecimal> gstDetailsMap = new HashMap<String, BigDecimal>();
			for (CustomerGSTDetails detail : customerGSTDetails) {
				gstDetailsMap.put(detail.getFrequancy() + "-" + (detail.getFinancialYear()),
						CurrencyUtil.parse(detail.getSalAmount(), 2));
			}
			if (!customerGsts.get(i).getFrequencytype().equals("Quarterly")) {
				int l = 1;
				for (int k = 12; k > 0; k--) {
					YearMonth date = YearMonth.now().minusMonths(k);
					String monthName = date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
					String month = (monthName + "-" + date.getYear());
					if (gstDetailsMap.containsKey(month)) {
						dataMap.put("Gst" + l + "Freq", customerGsts.get(i).getFrequencytype());
						dataMap.put("Gst" + i + "Month" + l, gstDetailsMap.get(month));
						l = l + 1;
					}
				}
			} else {
				int q = 1;
				for (int k = 4; k > 0; k--) {
					YearMonth date = YearMonth.now().minusMonths(k);
					String month = ("Q" + k + "-" + date.getYear());
					dataMap.put("Gst" + q + "Freq", customerGsts.get(i).getFrequencytype());
					dataMap.put("Gst" + i + "Month" + q, gstDetailsMap.get(month));
					q = q + 1;
				}
			}
		}
	}

	private void setCorporateFinancialData(Map<String, Object> dataMap, FinanceDetail fd) {
		long custId = fd.getCustomerDetails().getCustomer().getCustID();
		List<FinCreditReviewDetails> idList = dataAccess.getFinCreditRevDetailIds(custId);

		String maxAuditYear = dataAccess.getMaxAuditYearByCustomerId(custId, "_VIEW");

		int year2 = Integer.parseInt(maxAuditYear) - 1;
		int year3 = Integer.parseInt(maxAuditYear) - 2;

		dataMap.put("F1.MAXYEAR", "31-Mar-" + maxAuditYear);
		dataMap.put("F1.MAXYEAR.1", "31-Mar-" + year2);
		dataMap.put("F1.MAXYEAR.2", "31-Mar-" + year3);

		if (CollectionUtils.isEmpty(idList)) {
			return;
		}

		for (FinCreditReviewDetails id : idList) {
			Map<String, Object> tempMap1 = new HashMap<>();
			// tempMap1 = dataAccess.getFinCreditRevSummaryDetails(id.getId(), id.getAuditYear());
			tempMap1 = dataAccess.getFinCreditRevSummaryDetails(id.getId());

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
				dataMap.put(str, CurrencyUtil.format(tempMap1.get(strTemp).toString(), 2));
			}
		}

		if (fd.getJointAccountDetailList() != null && !fd.getJointAccountDetailList().isEmpty()) {
			for (JointAccountDetail accountDetail : fd.getJointAccountDetailList()) {
				List<FinCreditReviewDetails> coAppidList = dataAccess
						.getFinCreditRevDetailIds(accountDetail.getCustID());
				String coApp1MaxAuditYear = dataAccess.getMaxAuditYearByCustomerId(accountDetail.getCustID(), "_VIEW");

				int coApp1year2 = Integer.parseInt(coApp1MaxAuditYear) - 1;
				int coApp1year3 = Integer.parseInt(coApp1MaxAuditYear) - 2;

				dataMap.put("F2.MAXYEAR", "31-Mar-" + coApp1MaxAuditYear);
				dataMap.put("F2.MAXYEAR.1", "31-Mar-" + coApp1year2);
				dataMap.put("F2.MAXYEAR.2", "31-Mar-" + coApp1year3);
				if (CollectionUtils.isNotEmpty(coAppidList)) {
					for (FinCreditReviewDetails id : coAppidList) {
						Map<String, Object> tempMap2 = new HashMap<>();
						// tempMap2 = dataAccess.getFinCreditRevSummaryDetails(id.getId(),id.getAuditYear());
						tempMap2 = dataAccess.getFinCreditRevSummaryDetails(id.getId());

						for (String str : tempMap2.keySet()) {
							String strTemp = str;
							if (id.getAuditYear().equals(coApp1MaxAuditYear)) {
								str = "F2." + (str) + "." + ("3");
							} else if (id.getAuditYear().equals(String.valueOf(coApp1year2))) {
								str = "F2." + (str) + "." + ("2");
							} else if (id.getAuditYear().equals(String.valueOf(coApp1year3))) {
								str = "F2." + (str) + "." + ("1");
							}

							dataMap.put(str, tempMap2.get(strTemp));
							dataMap.put(str, CurrencyUtil.format(tempMap2.get(strTemp).toString(), 2));
						}
					}
				}
			}
		}
	}

	private void setCoApplicantData(FinanceDetail financeDetail, Map<String, Object> dataMap, SpreadSheet spreadSheet) {

		if (CollectionUtils.isEmpty(financeDetail.getJointAccountDetailList())) {
			return;
		}

		FinanceMain fm = financeDetail.getFinScheduleData().getFinanceMain();
		if (financeDetail.getJointAccountDetailList().get(0) != null) {
			String custCIF = financeDetail.getJointAccountDetailList().get(0).getCustCIF();
			spreadSheet.setCu1(dataAccess.getCustomerDetailForFinancials(custCIF, "_View"));
			Customer customer = spreadSheet.getCu1();
			setCustomerName(customer);
			dataMap.put("CoApp1DOB", customer.getCustDOB());
			dataMap.put("CoApp1Age", DateUtil.getYearsBetween(SysParamUtil.getAppDate(), customer.getCustDOB()));
			dataMap.put("CoApp1MatAge", DateUtil.getYearsBetween(fm.getMaturityDate(), customer.getCustDOB()));

		}

		if (financeDetail.getJointAccountDetailList().size() > 1
				&& financeDetail.getJointAccountDetailList().get(1) != null) {
			String custCIF = financeDetail.getJointAccountDetailList().get(1).getCustCIF();
			spreadSheet.setCu2(dataAccess.getCustomerDetailForFinancials(custCIF, "_View"));
			Customer customer = spreadSheet.getCu2();
			setCustomerName(customer);
			dataMap.put("CoApp2DOB", customer.getCustDOB());
			dataMap.put("CoApp2Age", DateUtil.getYearsBetween(SysParamUtil.getAppDate(), customer.getCustDOB()));
			dataMap.put("CoApp2MatAge", DateUtil.getYearsBetween(fm.getMaturityDate(), customer.getCustDOB()));

		}

		if (financeDetail.getJointAccountDetailList().size() > 2
				&& financeDetail.getJointAccountDetailList().get(2) != null) {
			String custCIF = financeDetail.getJointAccountDetailList().get(2).getCustCIF();
			spreadSheet.setCu3(dataAccess.getCustomerDetailForFinancials(custCIF, "_View"));
			Customer customer = spreadSheet.getCu3();
			setCustomerName(customer);
			dataMap.put("CoApp3DOB", customer.getCustDOB());
			dataMap.put("CoApp3Age", DateUtil.getYearsBetween(SysParamUtil.getAppDate(), customer.getCustDOB()));
			dataMap.put("CoApp3MatAge", DateUtil.getYearsBetween(fm.getMaturityDate(), customer.getCustDOB()));

		}

		if (financeDetail.getJointAccountDetailList().size() > 3
				&& financeDetail.getJointAccountDetailList().get(3) != null) {
			String custCIF = financeDetail.getJointAccountDetailList().get(3).getCustCIF();

			spreadSheet.setCu4(dataAccess.getCustomerDetailForFinancials(custCIF, "_View"));
			Customer customer = spreadSheet.getCu4();
			setCustomerName(customer);
			dataMap.put("CoApp4DOB", customer.getCustDOB());
			dataMap.put("CoApp4Age", DateUtil.getYearsBetween(SysParamUtil.getAppDate(), customer.getCustDOB()));
			dataMap.put("CoApp4MatAge", DateUtil.getYearsBetween(fm.getMaturityDate(), customer.getCustDOB()));

		}

		if (financeDetail.getJointAccountDetailList().size() > 4
				&& financeDetail.getJointAccountDetailList().get(4) != null) {
			String custCIF = financeDetail.getJointAccountDetailList().get(4).getCustCIF();

			spreadSheet.setCu5(dataAccess.getCustomerDetailForFinancials(custCIF, "_View"));
			Customer customer = spreadSheet.getCu5();
			setCustomerName(customer);
			dataMap.put("CoApp5DOB", customer.getCustDOB());
			dataMap.put("CoApp5Age", DateUtil.getYearsBetween(SysParamUtil.getAppDate(), customer.getCustDOB()));
			dataMap.put("CoApp5MatAge", DateUtil.getYearsBetween(fm.getMaturityDate(), customer.getCustDOB()));
		}
	}

	private void setKeyFigures(Map<String, Object> dataMap, FinanceMain fm) {
		if (StringUtils.equals(fm.getLovEligibilityMethod(), "0107")
				|| StringUtils.equals(fm.getLovEligibilityMethod(), "0108")) {
			dataMap.put("IncomeProgramme1", "Bureau+Banking");
		} else if (StringUtils.equals(fm.getLovEligibilityMethod(), "0109")) {
			dataMap.put("IncomeProgramme2", "Bureau+Banking+GST+Financial");
		} else if (StringUtils.equals(fm.getLovEligibilityMethod(), "0110")) {
			dataMap.put("IncomeProgramme3", "Bureau+Banking+GST");
		}
	}

	private void setCoApplicantExtendedData(SpreadSheet spreadSheet, FinanceDetail fd, Map<String, Object> dataMap) {
		for (int i = 0; i < fd.getJointAccountDetailList().size(); i++) {
			// FIXME: Table Name should come from Module and SubModule
			List<Map<String, Object>> extendedMapValues = dataAccess.getExtendedFieldMap(
					String.valueOf(fd.getJointAccountDetailList().get(i).getCustCIF()), "Customer_Sme_Ed", "_view");
			CustomerDetails cu = fd.getJointAccountDetailList().get(i).getCustomerDetails();
			if (CollectionUtils.isNotEmpty(extendedMapValues) && i == 0) {
				spreadSheet.setAddlVar1(getExtFieldDesc("clix_natureofbusiness",
						extendedMapValues.get(0).get("natureofbusiness").toString()));
				spreadSheet.setAddlVar2(
						getExtFieldDesc("clix_industry", extendedMapValues.get(0).get("industry").toString()));
				spreadSheet.setAddlVar3(
						getExtFieldDesc("clix_segment", extendedMapValues.get(0).get("segment").toString()));
				spreadSheet.setAddlVar4(
						getExtFieldDesc("clix_product", extendedMapValues.get(0).get("product").toString()));
				// industry margin
				spreadSheet.setAddlVar5(getExtFieldIndustryMargin("clix_industrymargin", spreadSheet.getAddlVar1(),
						spreadSheet.getAddlVar2(), spreadSheet.getAddlVar3(), spreadSheet.getAddlVar4()));
				setCoApplicantFiStatus(fd, cu, fd.getJointAccountDetailList().get(i).getCustCIF(), i, dataMap);
			}
			if (CollectionUtils.isNotEmpty(extendedMapValues) && i == 1) {
				spreadSheet.setAddlVar6(getExtFieldDesc("clix_natureofbusiness",
						extendedMapValues.get(0).get("natureofbusiness").toString()));
				spreadSheet.setAddlVar7(
						getExtFieldDesc("clix_industry", extendedMapValues.get(0).get("industry").toString()));
				spreadSheet.setAddlVar8(
						getExtFieldDesc("clix_segment", extendedMapValues.get(0).get("segment").toString()));
				spreadSheet.setAddlVar9(
						getExtFieldDesc("clix_product", extendedMapValues.get(0).get("product").toString()));
				// industry margin
				spreadSheet.setAddlVar10(getExtFieldIndustryMargin("clix_industrymargin", spreadSheet.getAddlVar1(),
						spreadSheet.getAddlVar2(), spreadSheet.getAddlVar3(), spreadSheet.getAddlVar4()));
				setCoApplicantFiStatus(fd, cu, fd.getJointAccountDetailList().get(i).getCustCIF(), i, dataMap);
			}

		}
	}

	private void setExtendedData(FinanceDetail fd, Customer cu, Map<String, Object> dataMap) {
		try {
			if (fd.getCustomerDetails().getExtendedFieldRender() != null) {
				Map<String, Object> mapValues = fd.getCustomerDetails().getExtendedFieldRender().getMapValues();
				if (mapValues.containsKey("natureofbusiness")) {
					cu.setCustAddlVar8(
							getExtFieldDesc("clix_natureofbusiness", mapValues.get("natureofbusiness").toString()));
				}
				if (mapValues.containsKey("industry")) {
					cu.setCustAddlVar9(getExtFieldDesc("clix_industry", mapValues.get("industry").toString()));
				}
				if (mapValues.containsKey("segment")) {
					cu.setCustAddlVar10(getExtFieldDesc("clix_segment", mapValues.get("segment").toString()));
				}
				if (mapValues.containsKey("product")) {
					cu.setCustAddlVar11(getExtFieldDesc("clix_product", mapValues.get("product").toString()));
				}
				// industry margin
				cu.setCustAddlVar89(getExtFieldIndustryMargin("clix_industrymargin", cu.getCustAddlVar8(),
						cu.getCustAddlVar9(), cu.getCustAddlVar10(), cu.getCustAddlVar11()));

				setMainApplicantFiStatus(fd, fd.getCustomerDetails().getCustomer().getCustCIF(), dataMap);
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	private void setCustomerAddresses(SpreadSheet spreadSheet, CustomerDetails cd) {
		List<CustomerAddres> addressList = cd.getAddressList();
		if (CollectionUtils.isEmpty(addressList)) {
			return;
		}

		for (CustomerAddres addr : addressList) {
			String addrType = addr.getCustAddrType();
			if (StringUtils.equalsIgnoreCase(addrType, App.getProperty("Customer_Office_Address"))) {
				spreadSheet.setCustOffAddr(buildAddress(addr));
			} else if (StringUtils.equalsIgnoreCase(addrType, App.getProperty("Customer_Resi_Address"))) {
				spreadSheet.setCustResiAddr(buildAddress(addr));
			} else if (StringUtils.equalsIgnoreCase(addrType, App.getProperty("Customer_Per_Address"))) {
				spreadSheet.setCustPerAddr(buildAddress(addr));
			}

		}
	}

	private String buildAddress(CustomerAddres addr) {
		return addr.getCustAddrHNbr().concat(",")
				.concat(addr.getCustAddrStreet().concat(",").concat(addr.getLovDescCustAddrCityName().concat(",")
						.concat(addr.getLovDescCustAddrProvinceName()).concat(",").concat(addr.getCustAddrZIP())));
	}

	private void setCustomerPhoneNumber(SpreadSheet spreadSheet, CustomerDetails cd) {
		List<CustomerPhoneNumber> customerPhoneNumList = cd.getCustomerPhoneNumList();
		if (CollectionUtils.isNotEmpty(customerPhoneNumList)) {
			for (CustomerPhoneNumber cpn : customerPhoneNumList) {
				if (PennantConstants.KYC_PRIORITY_VERY_HIGH.equals(String.valueOf(cpn.getPhoneTypePriority()))) {
					spreadSheet.setCustomerPhoneNum(cpn.getPhoneNumber());
				}
			}
		}
	}

	private void setCustomerName(Customer cu) {
		if (cu.getCustCtgCode().equals("SME") || cu.getCustCtgCode().equals("CORP")) {
			cu.setCustomerFullName(cu.getCustShrtName());
		} else {
			cu.setCustomerFullName(cu.getCustFName().concat(cu.getCustMName().concat(cu.getCustLName())));
		}
	}

	private String getExtFieldDesc(String tableName, String value) {
		logger.debug(Literal.ENTERING);
		try {
			if (StringUtils.trimToNull(tableName) == null) {
				return null;
			}
			return dataAccess.getExtFieldDesc(tableName, value);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	private String getExtFieldIndustryMargin(String tableName, String type, String industry, String segment,
			String product) {
		logger.debug(Literal.ENTERING);
		try {
			if (StringUtils.trimToNull(tableName) == null) {
				return null;
			}
			return dataAccess.getExtFieldIndustryMargin(tableName, type, industry, segment, product);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	private Boolean getBooleanValue(String key, Map<String, Object> screenData) {
		String value = getStringValue(key, screenData);

		if (StringUtils.isEmpty(value)) {
			value = "0";
		}

		return new Boolean(value);
	}

	private BigDecimal getDecimalValue(String key, Map<String, Object> screenData) {
		String value = getStringValue(key, screenData);

		if (StringUtils.isEmpty(value)) {
			value = "0";
		}

		return new BigDecimal(value);
	}

	private String getStringValue(String key, Map<String, Object> screenData) {
		if (!screenData.containsKey(key)) {
			return "";
		}

		return screenData.get(key).toString();
	}

	// method for Main Applicant FI status
	private void setMainApplicantFiStatus(FinanceDetail fd, String custCif, Map<String, Object> dataMap) {
		List<CustomerAddres> addressList = fd.getCustomerDetails().getAddressList();
		if (CollectionUtils.isNotEmpty(addressList)) {
			for (CustomerAddres addr : addressList) {
				if (StringUtils.equalsIgnoreCase(addr.getCustAddrType(), App.getProperty("Customer_Resi_Address"))) {
					setFiStatus(fd, addr, "MainAppResiFIStatus", custCif, dataMap);
				}
				if (StringUtils.equalsIgnoreCase(addr.getCustAddrType(), App.getProperty("Customer_Office_Address"))) {
					setFiStatus(fd, addr, "MainAppOfficeFIStatus", custCif, dataMap);
				}
			}
		}
	}

	// method for Co Applicant FI status
	private void setCoApplicantFiStatus(FinanceDetail fd, CustomerDetails cu, String custCif, int value,
			Map<String, Object> dataMap) {
		List<CustomerAddres> addressList = cu.getAddressList();

		if (CollectionUtils.isEmpty(addressList)) {
			return;
		}

		for (CustomerAddres addr : addressList) {
			if (StringUtils.equalsIgnoreCase(addr.getCustAddrType(), App.getProperty("Customer_Resi_Address"))) {
				setFiStatus(fd, addr, "CoApp" + value + "ResiFIStatus", custCif, dataMap);
			}
			if (StringUtils.equalsIgnoreCase(addr.getCustAddrType(), App.getProperty("Customer_Office_Address"))) {
				setFiStatus(fd, addr, "CoApp" + value + "OfficeFIStatus", custCif, dataMap);
			}
		}
	}

	// method for FI status for both office and residence addresses
	private void setFiStatus(FinanceDetail fd, CustomerAddres addr, String name, String custCif,
			Map<String, Object> dataMap) {
		Verification verificationForStatus = dataAccess.getVerificationStatus(
				fd.getFinScheduleData().getFinanceMain().getFinReference(), 1, addr.getCustAddrType(), custCif);
		if (verificationForStatus != null) {
			if (verificationForStatus.getStatus() == 1) {
				dataMap.put(name, "Positive");
			} else if (verificationForStatus.getStatus() == 2) {
				dataMap.put(name, "Negative");
			} else if (verificationForStatus.getStatus() == 3) {
				dataMap.put(name, "Refer to Credit");
			}
		}
	}

	public SpreadSheetDataAccess getDataAccess() {
		return dataAccess;
	}

	public void setDataAccess(SpreadSheetDataAccess dataAccess) {
		this.dataAccess = dataAccess;
	}

}