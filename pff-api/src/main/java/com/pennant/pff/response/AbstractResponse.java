package com.pennant.pff.response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;

import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.customerdata.CustomerData;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.emiholidays.EMIHolidays;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.gracedetails.GraceDetails;
import com.pennant.backend.model.loanbranch.LoanBranch;
import com.pennant.backend.model.loandetail.LoanDetail;
import com.pennant.backend.model.loanschedules.LoanSchedules;
import com.pennant.backend.model.loansummary.LoanSummary;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.pff.api.controller.AbstractController;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.util.CustomerUtil;
import com.pennanttech.pff.core.util.SchdUtil;

public class AbstractResponse extends AbstractController {

	private BranchDAO branchDAO;
	private MandateDAO mandateDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;

	public AbstractResponse() {
		super();
	}

	public CustomerData prepareCustomerData(CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);

		CustomerData cd = new CustomerData();

		setCustomerBasicDetails(cd, customerDetails);

		logger.debug(Literal.LEAVING);
		return cd;
	}

	public LoanDetail prepareLoanDetail(FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		LoanDetail ld = new LoanDetail();

		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();
		FinanceSummary fs = fd.getFinScheduleData().getFinanceSummary();
		List<FinanceScheduleDetail> schedules = fd.getFinScheduleData().getFinanceScheduleDetails();
		Branch branch = branchDAO.getBranchById(fm.getFinBranch(), "_AView");

		setSchedules(ld, schedules);
		setFinanceData(ld, fm);
		setSummaryDetails(ld, fd);
		setBranchDetails(ld, branch);
		setGraceDetails(ld, fm);
		setEMIHolidayDetails(ld, fm);

		return ld;

	}

	private void setCustomerBasicDetails(CustomerData cd, CustomerDetails details) {
		Customer c = details.getCustomer();

		if (c == null) {
			return;
		}

		cd.setCustCIF(c.getCustCIF());
		cd.setCustShrtName(c.getCustShrtName());
		cd.setCustDOB(c.getCustDOB());
		cd.setCustCRCPR(c.getCustCRCPR());
		cd.setFullName(CustomerUtil.getCustomerFullName(c));
		cd.setCustMotherMaiden(c.getCustMotherMaiden());
		cd.setCustFNameLclLng(c.getCustFNameLclLng());
		cd.setGender(c.getCustGenderCode());
		cd.setMaritalStatus(c.getCustMaritalSts());
		cd.setCustTypeCode(c.getCustTypeCode());
		cd.setCustTypeDesc(c.getLovDescCustTypeCodeName());
		cd.setFirstName(c.getCustFName());
		cd.setMiddleName(c.getCustMName());
		cd.setLastName(c.getCustLName());
		cd.setCoreBankID(c.getCustCoreBank());
		cd.setCustNationality(c.getCustNationality());
		cd.setNatureOfBusiness(c.getNatureOfBusiness());
		cd.setCustCategory(c.getLovDescCustCtgCodeName());
		cd.setCustGroupID(c.getCustGroupID());

		cd.setFullAddress(CustomerUtil.getCustomerFullAddress(details.getAddressList()));
		cd.setCustAddresses(details.getAddressList());
		cd.setCustPhoneNumbers(details.getCustomerPhoneNumList());
		cd.setCustEmails(details.getCustomerEMailList());
		cd.setCustEmployments(details.getEmploymentDetailsList());
		cd.setCustomerDocuments(details.getCustomerDocumentsList());
		cd.setCustomerIncomes(setIncomeDetails(details.getCustomerIncomeList()));
	}

	private List<CustomerIncome> setIncomeDetails(List<CustomerIncome> customerIncomes) {
		if (CollectionUtils.isEmpty(customerIncomes)) {
			return null;
		}

		for (CustomerIncome ci : customerIncomes) {
			ci.setGrossIncome((ci.getIncome().multiply(ci.getMargin())).divide(new BigDecimal(100)));
		}
		return customerIncomes;
	}

	private void setEMIHolidayDetails(LoanDetail ld, FinanceMain fm) {
		if (fm == null) {
			return;
		}

		EMIHolidays emi = new EMIHolidays();

		emi.setPlanEMIHAlw(fm.isPlanEMIHAlw());
		emi.setPlanEMIHAlwInGrace(fm.isPlanEMIHAlwInGrace());
		emi.setPlanEMIHMethod(fm.getPlanEMIHMethod());
		emi.setPlanEMIHMaxPerYear(fm.getPlanEMIHMaxPerYear());
		emi.setPlanEMIHMax(fm.getPlanEMIHMax());
		emi.setPlanEMIHLockPeriod(fm.getPlanEMIHLockPeriod());
		emi.setPlanEMICpz(fm.isPlanEMICpz());
		emi.setUnPlanEMIHLockPeriod(fm.getUnPlanEMIHLockPeriod());
		emi.setUnPlanEMICpz(fm.isUnPlanEMICpz());
		emi.setReAgeCpz(fm.isReAgeCpz());
		emi.setMaxUnplannedEmi(fm.getMaxUnplannedEmi());
		emi.setMaxReAgeHolidays(fm.getMaxReAgeHolidays());

		ld.setEmiHolidays(emi);

	}

	private void setGraceDetails(LoanDetail ld, FinanceMain fm) {
		if (fm == null) {
			return;
		}

		GraceDetails graceDetails = new GraceDetails();

		graceDetails.setAllowGrcPeriod(fm.isAllowGrcPeriod());
		graceDetails.setGraceTerms(fm.getGraceTerms());
		graceDetails.setGrcPeriodEndDate(fm.getGrcPeriodEndDate());
		graceDetails.setGrcRateBasis(fm.getGrcRateBasis());
		graceDetails.setGrcPftRate(fm.getGrcPftRate());
		graceDetails.setGraceBaseRate(fm.getGraceBaseRate());
		graceDetails.setGraceSpecialRate(fm.getGraceSpecialRate());
		graceDetails.setGrcProfitDaysBasis(fm.getGrcProfitDaysBasis());
		graceDetails.setNextGrcPftDate(fm.getNextGrcPftDate());
		graceDetails.setGrcPftRvwFrq(fm.getGrcPftRvwFrq());
		graceDetails.setNextGrcPftRvwDate(fm.getNextGrcPftRvwDate());
		graceDetails.setAllowGrcCpz(fm.isAllowGrcCpz());
		graceDetails.setGrcCpzFrq(fm.getGrcCpzFrq());
		graceDetails.setNextGrcCpzDate(fm.getNextGrcCpzDate());
		graceDetails.setAllowGrcRepay(fm.isAllowGrcRepay());
		graceDetails.setGrcSchdMthd(fm.getGrcSchdMthd());
		graceDetails.setGrcMinRate(fm.getGrcMinRate());
		graceDetails.setGrcMaxRate(fm.getGrcMaxRate());
		graceDetails.setGrcMaxAmount(fm.getGrcMaxAmount());
		graceDetails.setNumberOfTerms(fm.getNumberOfTerms());
		graceDetails.setGrcAdvType(fm.getGrcAdvType());
		graceDetails.setGrcAdvTerms(fm.getGrcAdvTerms());
		graceDetails.setAlwGrcAdj(fm.isAlwGrcAdj());
		graceDetails.setEndGrcPeriodAftrFullDisb(fm.isEndGrcPeriodAftrFullDisb());
		graceDetails.setAutoIncGrcEndDate(fm.isAutoIncGrcEndDate());
		graceDetails.setNoOfGrcSteps(fm.getNoOfGrcSteps());
		if (fm.isAllowGrcPeriod()) {
			graceDetails.setGrcStartDate(fm.getFinStartDate());
		}

		ld.setGraceDetails(graceDetails);
	}

	private void setBranchDetails(LoanDetail ld, Branch branch) {
		if (branch == null) {
			return;
		}

		LoanBranch loanBranch = new LoanBranch();

		loanBranch.setBranchCode(branch.getBranchCode());
		loanBranch.setBranchAddrLine1(branch.getBranchAddrLine1());
		loanBranch.setBranchAddrLine2(branch.getBranchAddrLine2());
		loanBranch.setBranchPOBox(branch.getBranchPOBox());
		loanBranch.setBranchCity(branch.getBranchCity());
		loanBranch.setLovDescBranchCityName(branch.getLovDescBranchCityName());
		loanBranch.setBranchProvince(branch.getBranchProvince());
		loanBranch.setLovDescBranchProvinceName(branch.getLovDescBranchProvinceName());
		loanBranch.setBranchCountry(branch.getBranchCountry());
		loanBranch.setLovDescBranchCountryName(branch.getLovDescBranchCountryName());
		loanBranch.setBranchFax("");
		loanBranch.setBranchTel(branch.getBranchTel());
		loanBranch.setBranchMail("");
		loanBranch.setZipCode(branch.getPinCode());
		loanBranch.setBranchAddrHNbr(branch.getBranchAddrHNbr());
		loanBranch.setBranchAddrStreet(branch.getBranchAddrStreet());
		loanBranch.setBranchAddress(getBranchAddress(branch));

		ld.setLoanBranch(loanBranch);

	}

	private void setSchedules(LoanDetail ld, List<FinanceScheduleDetail> schedules) {

		List<LoanSchedules> ls = new ArrayList<>();

		if (schedules == null) {
			return;
		}

		int count = -1;

		for (FinanceScheduleDetail fsd : schedules) {
			LoanSchedules schd = new LoanSchedules();

			count++;

			schd.setSchDate(fsd.getSchDate());
			schd.setInstNumber(fsd.getInstNumber());
			schd.setNoOfDays(fsd.getNoOfDays());
			schd.setProfitCalc(fsd.getProfitCalc());
			schd.setProfitSchd(fsd.getProfitSchd());
			schd.setPrincipalSchd(fsd.getPrincipalSchd());
			schd.setRepayAmount(fsd.getRepayAmount());
			schd.setClosingBalance(fsd.getClosingBalance());
			schd.setSchdPftPaid(fsd.getSchdPftPaid());
			schd.setSchdPriPaid(fsd.getSchdPriPaid());
			schd.setFeeSchd(fsd.getFeeSchd());
			schd.settDSAmount(fsd.getTDSAmount());
			schd.setLimitDrop(fsd.getLimitDrop());
			schd.setoDLimit(fsd.getODLimit());
			schd.setAvailableLimit(fsd.getAvailableLimit());
			schd.setLoanEMIStatus(SchdUtil.getRepaymentStatus(fsd).repaymentStatus());
			schd.setActRate(fsd.getActRate());
			schd.setOpenBal(fsd.getClosingBalance());
			if (count > 0) {
				schd.setOpenBal(schedules.get(count - 1).getClosingBalance());
			}

			ls.add(schd);
		}

		ld.setLoanSchedules(ls);
	}

	private void setSummaryDetails(LoanDetail ld, FinanceDetail fd) {

		FinScheduleData schData = fd.getFinScheduleData();
		FinanceMain fm = schData.getFinanceMain();
		FinanceSummary fs = schData.getFinanceSummary();

		if (fs == null) {
			return;
		}

		LoanSummary ls = new LoanSummary();

		ls.setTotalPriSchd(fs.getTotalPriSchd());
		ls.setSchdPftPaid(fs.getSchdPftPaid());
		ls.setSchdPriPaid(fs.getSchdPriPaid());
		ls.setTotalCpz(fs.getTotalCpz());
		ls.setFinCurODDays(fs.getFinCurODDays());
		ls.setNextSchDate(fs.getNextSchDate());
		ls.setMaturityDate(fs.getMaturityDate());
		ls.setFinStatus(fs.getFinStatus());
		ls.setFinLastRepayDate(fs.getFinLastRepayDate());
		ls.setOutStandPrincipal(fs.getOutStandPrincipal());
		ls.setOutStandProfit(fs.getOutStandProfit());
		ls.setTotalOutStanding(fs.getTotalOutStanding());
		ls.setOverDuePrincipal(fs.getOverDuePrincipal());
		ls.setOverDueProfit(fs.getOverDueProfit());
		ls.setTotalOverDue(fs.getTotalOverDue());
		ls.setOverDueCharges(fs.getOverDueCharges());
		ls.setTotalOverDueIncCharges(fs.getTotalOverDueIncCharges());
		ls.setOverDueInstlments(fs.getOverDueInstlments());
		ls.setNumberOfTerms(fs.getNumberOfTerms());
		ls.setTotalRepayAmt(fs.getTotalRepayAmt());
		ls.setEffectiveRateOfReturn(fs.getEffectiveRateOfReturn());
		ls.setTotalGracePft(fs.getTotalGracePft());
		ls.setTotalGraceCpz(fs.getTotalGraceCpz());
		ls.setTotalGrossGrcPft(fs.getTotalGrossGrcPft());
		ls.setTotalProfit(fs.getTotalProfit());
		ls.setFeeChargeAmt(fs.getFeeChargeAmt());
		ls.setLoanTenor(fs.getLoanTenor());
		ls.setFirstDisbDate(fs.getFirstDisbDate());
		ls.setLastDisbDate(fs.getLastDisbDate());
		ls.setNextRepayAmount(fs.getNextRepayAmount());
		ls.setFirstEmiAmount(fs.getFirstEmiAmount());
		ls.setFutureInst(fs.getFutureInst());
		ls.setFutureTenor(fs.getFutureTenor());
		ls.setFirstInstDate(fs.getFirstInstDate());
		ls.setPaidTotal(fs.getPaidTotal());
		ls.setAdvPaymentAmount(fs.getAdvPaymentAmount());
		ls.setFinODDetail(fs.getFinODDetail());
		ls.setFullyDisb(fs.isFullyDisb());
		ls.setSanctionAmt(fs.getSanctionAmt());
		ls.setUtilizedAmt(fs.getUtilizedAmt());
		ls.setAvailableAmt(fs.getAvailableAmt());
		ls.setDueCharges(fs.getDueCharges());
		ls.setOverDueAmount(fs.getOverDueAmount());
		ls.setLoanEMI(fs.getLoanEMI());
		ls.setForeClosureAmount(FinanceConstants.CLOSE_STATUS_EARLYSETTLE.equals(fm.getClosingStatus())
				? getForeClosureAmount(fm.getFinID())
				: BigDecimal.ZERO);
		ls.setInstallmentNo(fs.getInstallmentNo());
		ls.setDueDate(fs.getSchDate());
		ls.setLoanTotPrincipal(fs.getLoanTotPrincipal());
		ls.setLoanTotInterest(fs.getLoanTotInterest());
		ls.setOverDueEMI(fs.getOverDueEMI());
		ls.setVehicleNo(fs.getVehicleNo());
		ls.setMigratedNo(fs.getMigratedNo());
		ls.setLastInstDate(fs.getLastInstDate());
		ls.setDPDString(fs.getDPDString());
		ls.setInterestComponent(fs.getInterestComponent());
		ls.setPrincipalComponent(fs.getPrincipalComponent());
		ls.setTotalAmount(fs.getTotalComponent());
		ls.setAssetCost("");
		ls.setAssetModel("");
		ls.setCategoryID("");
		ls.setCategoryDesc("");

		ld.setLoanSummary(ls);
	}

	private void setFinanceData(LoanDetail ld, FinanceMain fm) {
		if (fm == null) {
			return;
		}

		ld.setFinReference(fm.getFinReference());
		ld.setFinType(fm.getFinType());
		ld.setFinTypeDesc(fm.getLovDescFinTypeName());
		ld.setFinCcy(fm.getFinCcy());
		ld.setProfitDaysBasis(fm.getGrcProfitDaysBasis());
		ld.setCif(fm.getLovDescCustCIF());
		ld.setShortName(fm.getLovDescCustShrtName());
		ld.setFinBranch(fm.getFinBranch());
		ld.setFinStartDate(fm.getFinStartDate());
		ld.setFinAmount(fm.getFinAmount());
		ld.setDownPayBank(fm.getDownPayBank());
		ld.setDownPaySupl(fm.getDownPaySupl());
		ld.setFinPurpose(fm.getFinPurpose());
		ld.setFinIsActive(fm.isFinIsActive());
		ld.setAccountsOfficerReference(fm.getAccountsOfficerReference());
		ld.setDsaCode(fm.getDsaCode());
		ld.setTdsApplicable(fm.isTDSApplicable());
		ld.setBaseProduct(fm.getBaseProduct());
		ld.setCustSegmentation(fm.getCustSegmentation());
		ld.setExistingLanRefNo(fm.getExistingLanRefNo());
		ld.setRsa(fm.isRsa());
		ld.setFinRepayMethod(fm.getFinRepayMethod());
		ld.setVerification(fm.getVerification());
		ld.setFinContractDate(fm.getFinContractDate());
		ld.setLeadSource(fm.getLeadSource());
		ld.setPoSource(fm.getPoSource());
		ld.setSourcingBranch(fm.getSourcingBranch());
		ld.setSourChannelCategory(fm.getSourChannelCategory());
		ld.setApplicationNo(fm.getApplicationNo());
		ld.setReqRepayAmount(fm.getReqRepayAmount());
		ld.setRepayRateBasis(fm.getRepayRateBasis());
		ld.setRepayProfitRate(fm.getRepayProfitRate());
		ld.setRepayBaseRate(fm.getRepayBaseRate());
		ld.setRepaySpecialRate(fm.getRepaySpecialRate());
		ld.setRepayMargin(fm.getRepayMargin());
		ld.setScheduleMethod(fm.getScheduleMethod());
		ld.setRepayPftFrq(fm.getRepayPftFrq());
		ld.setNextRepayPftDate(fm.getNextRepayPftDate());
		ld.setRepayRvwFrq(fm.getRepayRvwFrq());
		ld.setNextRepayRvwDate(fm.getNextRepayRvwDate());
		ld.setRepayCpzFrq(fm.getRepayCpzFrq());
		ld.setNextRepayCpzDate(fm.getNextRepayCpzDate());
		ld.setRepayFrq(fm.getRepayFrq());
		ld.setNextRepayDate(fm.getNextRepayDate());
		ld.setMaturityDate(fm.getMaturityDate());
		ld.setFinRepayPftOnFrq(fm.isFinRepayPftOnFrq());
		ld.setRpyMinRate(fm.getRpyMinRate());
		ld.setRpyMaxRate(fm.getRpyMaxRate());
		ld.setAlwBPI(fm.isAlwBPI());
		ld.setBpiTreatment(fm.getBpiTreatment());
		ld.setBpiPftDaysBasis(fm.getBpiPftDaysBasis());
		ld.setReqLoanAmt(fm.getReqLoanAmt());
		ld.setReqLoanTenor(fm.getReqLoanTenor());
		ld.setCalMaturity(fm.getCalMaturity());
		ld.setFinAssetValue(fm.getFinAssetValue());
		ld.setFinCurrAssetValue(fm.getFinCurrAssetValue());
		ld.setProductCategory(fm.getProductCategory());
		ld.setDmaCodeReference(fm.getDmaCodeReference());
		ld.setReferralId(fm.getReferralId());
		ld.setEmployeeName(fm.getEmployeeName());
		ld.setQuickDisb(fm.isQuickDisb());
		ld.setFirstDisbDate(fm.getFirstDisbDate());
		ld.setLastDisbDate(fm.getLastDisbDate());
		ld.setStage(fm.getStage());
		ld.setStatus(fm.getStatus());
		ld.setConnectorReference(fm.getConnectorReference());
		ld.setFixedRateTenor(fm.getFixedRateTenor());
		ld.setFixedTenorRate(fm.getFixedTenorRate());
		ld.setRepayAmount(fm.getRepayAmount());
		ld.setAdvType(fm.getAdvType());
		ld.setAdvTerms(fm.getAdvTerms());
		ld.setClosedDate(fm.getClosedDate());
		ld.setManufacturerDealerId(fm.getManufacturerDealerId());
		ld.setFinOcrRequired(fm.isFinOcrRequired());
		ld.setTdsType(fm.getTdsType());
		ld.setEscrow(fm.isEscrow());
		ld.setCustBankId(fm.getCustBankId());
		ld.setOverdraftTxnChrgReq(fm.isOverdraftTxnChrgReq());
		ld.setOverdraftCalcChrg(fm.getOverdraftCalcChrg());
		ld.setOverdraftChrCalOn(fm.getOverdraftChrCalOn());
		ld.setOverdraftChrgAmtOrPerc(fm.getOverdraftChrgAmtOrPerc());
		ld.setOverdraftTxnChrgFeeType(fm.getOverdraftTxnChrgFeeType());
		if (fm.getMandateID() != null) {
			String accNumber = mandateDAO.getMandateNumber(fm.getMandateID());
			ld.setAccNumber(accNumber != null ? accNumber : "");
		}
		ld.setNoOfMonths(
				DateUtil.getMonthsBetweenInclusive(fm.getFinStartDate(), fm.getMaturityDate()) + fm.getAdvTerms());
		ld.setNetDisbursementAmount(fm.getFinCurrAssetValue().subtract(fm.getDeductFeeDisb().add(fm.getBpiAmount())));
		ld.setLoanName(fm.getLoanName());
		ld.setCustDOB(fm.getCustDOB());
	}

	private String getBranchAddress(Branch branch) {

		StringBuilder address = new StringBuilder();

		String houseNumber = StringUtils.trimToEmpty(branch.getBranchAddrHNbr());
		String street = StringUtils.trimToEmpty(branch.getBranchAddrStreet());
		String addrLine1 = StringUtils.trimToEmpty(branch.getBranchAddrLine1());
		String addrLine2 = StringUtils.trimToEmpty(branch.getBranchAddrLine2());
		String city = StringUtils.trimToEmpty(branch.getLovDescBranchCityName());
		String state = StringUtils.trimToEmpty(branch.getLovDescBranchProvinceName());
		String country = StringUtils.trimToEmpty(branch.getLovDescBranchCountryName());
		String zipCode = StringUtils.trimToEmpty(branch.getPinCode());

		if (StringUtils.isNotBlank(houseNumber)) {
			address.append(Labels.getLabel("label_CustomerAddresDialog_CustAddrHNbr.value") + ":");
			address.append(houseNumber);
		}

		if (StringUtils.isNotBlank(street)) {
			if (address.length() > 0) {
				address.append(", ");
			}
			address.append(Labels.getLabel("label_CustomerAddresDialog_CustAddrStreet.value") + ":");
			address.append(street);
		}

		if (StringUtils.isNotBlank(addrLine1)) {
			if (address.length() > 0) {
				address.append(", ");
			}
			address.append(addrLine1);
		}

		if (StringUtils.isNotBlank(addrLine2)) {
			if (address.length() > 0) {
				address.append(", ");
			}
			address.append(addrLine2);
		}

		if (StringUtils.isNotBlank(city)) {
			if (address.length() > 0) {
				address.append(", ");
			}
			address.append(city);
		}

		if (StringUtils.isNotBlank(state)) {
			if (address.length() > 0) {
				address.append(", ");
			}
			address.append(state);
		}

		if (StringUtils.isNotBlank(country)) {
			if (address.length() > 0) {
				address.append(", ");
			}
			address.append(country);
		}

		if (StringUtils.isNotBlank(zipCode)) {
			if (address.length() > 0) {
				address.append(", ");
			}
			address.append(zipCode);
		}

		return address.toString();
	}

	private BigDecimal getForeClosureAmount(long finID) {
		return finReceiptHeaderDAO.getForeClosureAmount(finID);
	}

	@Autowired
	public void setBranchDAO(BranchDAO branchDAO) {
		this.branchDAO = branchDAO;
	}

	@Autowired
	public void setMandateDAO(MandateDAO mandateDAO) {
		this.mandateDAO = mandateDAO;
	}

	@Autowired
	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}
}
