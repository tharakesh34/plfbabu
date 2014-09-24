package com.pennant.external;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.pennant.TestingUtil;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FinanceProfitDetailFiller;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.dao.applicationmaster.CustomerStatusCodeDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.PennantConstants;


public class AccrualTest extends TestingUtil {
	private static boolean isSuccess = false;

	private static String getFile() {
		return getFileLoc() + AccrualTest.class.getSimpleName() + ".xls";
	}
	
	static FinanceDetailService detailService;

	public static FinanceDetailService getDetailService() {
		return detailService;
	}
	public static void setDetailService(FinanceDetailService detailService) {
		AccrualTest.detailService = detailService;
	}

	public static boolean RunTestCase(ApplicationContext mainContext) {
		try {

			// Services Initialization
			setDetailService((FinanceDetailService)mainContext.getBean("financeDetailService"));

			//Fetching List of all Finance Reference Details
			List<String> finRefList = getDetailService().getFinanceReferenceList();
				/*new ArrayList<String>();
				finRefList.add("713988");
				finRefList.add("713989");
				finRefList.add("001120");
				finRefList.add("1120812000001");*/
			
			File file = new File(getFile());
			FileWriter txt;
			txt = new FileWriter(file);
			PrintWriter out = new PrintWriter(txt);

			out.print(" WHEN \t FinReference \t CustId \t FinBranch \t FinType \t LastMdfDate \t TotalPftSchd" +
					" \t TotalPftCpz \t TotalPftPaid \t TotalPftBal \t TotalPftPaidInAdv \t TotalPriPaid " +
					"\t TotalPriBal \t TdSchdPft \t TdPftCpz \t TdSchdPftPaid \t TdSchdPftBal \t TdPftAccrued" +
					" \t TdPftAccrueSusp \t TdPftAmortized \t TdPftAmortizedSusp \t TdSchdPri \t TdSchdPriPaid" +
					" \t TdSchdPriBal \t AcrTillNBD \t AcrTillLBD \t AcrTodayToNBD \t AmzTillNBD \t " +
			"AmzTillLBD \t AmzTodayToNBD \t  AccrueTsfd \t PriDepreciation \t");

			FinanceProfitDetailDAO profitDetailsDAO = (FinanceProfitDetailDAO) mainContext.getBean("profitDetailsDAO");
			FinanceProfitDetailFiller detailFiller = (FinanceProfitDetailFiller) mainContext.getBean("financeProfitDetailFiller");
			CustomerStatusCodeDAO customerDAO = (CustomerStatusCodeDAO) mainContext.getBean("customerStatusCodeDAO");
			FinanceScheduleDetailDAO financeScheduleDetailDAO = (FinanceScheduleDetailDAO) mainContext.getBean("financeScheduleDetailDAO");

			for (String finReference : finRefList) {

				FinScheduleData data = getDetailService().getFinSchDataByFinRef(finReference, "_AView",0);

				// Profit Details Fill
				Date curBD = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
				curBD = DateUtility.addDays(curBD, 1);

				FinanceProfitDetail fpd = new FinanceProfitDetail();
				for (int i = 0; i < 1; i++) {

					fpd = profitDetailsDAO.getFinPftDetailForBatch(finReference);
					if(fpd == null){
						fpd = new FinanceProfitDetail();
					}

					//Amount code Details Calculation
					AEAmountCodes aeAmountCodes = AEAmounts.procAccrualAmounts(data.getFinanceMain(), 
							data.getFinanceScheduleDetails(), fpd, curBD);

					// Setting all Profit Detail parameters to Bean					
					fpd = setFinanceDetails(fpd,data);
					fpd = detailFiller.prepareFinPftDetails(aeAmountCodes,fpd, curBD);

					java.util.Date firstRepayDate = financeScheduleDetailDAO.getFirstRepayDate(finReference);
					if(firstRepayDate == null) {
						fpd.setFirstRepayDate(data.getFinanceMain().getFinStartDate());
					} else {
						fpd.setFirstRepayDate(firstRepayDate);
					}

					if(customerDAO != null){
						String worstSts = customerDAO.getFinanceStatus(finReference, true);
						fpd.setFinWorstStatus(worstSts);
					}

					//If Profit Details Need to save directly into DB -- in case on Migration purpose
					profitDetailsDAO.save(fpd,"_Temp");

					out.write("\n"  + "CAL  \t" + fpd.getFinReference()+  "\t" + fpd.getCustId()+  "\t" + fpd.getFinBranch()+  "\t" + fpd.getFinType()+  "\t" + fpd.getLastMdfDate()+  "\t" + fpd.getTotalPftSchd()+  "\t" + fpd.getTotalPftCpz()+  "\t" + fpd.getTotalPftPaid()+  "\t" + fpd.getTotalPftBal()+  "\t" + fpd.getTotalPftPaidInAdv()+  "\t" + fpd.getTotalPriPaid()+  "\t" + fpd.getTotalPriBal()+  "\t" + fpd.getTdSchdPft()+  "\t" + fpd.getTdPftCpz()+  "\t" + fpd.getTdSchdPftPaid()+  "\t" + fpd.getTdSchdPftBal()+  "\t" + fpd.getTdPftAccrued()+  "\t" + fpd.getTdPftAccrueSusp()+  "\t" + fpd.getTdPftAmortized()+  "\t" + fpd.getTdPftAmortizedSusp()+  "\t" + fpd.getTdSchdPri()+  "\t" + fpd.getTdSchdPriPaid()+  "\t" + fpd.getTdSchdPriBal()+  "\t" + fpd.getAcrTillNBD()+  "\t" + fpd.getAcrTillLBD()+  "\t" + fpd.getAcrTodayToNBD()+  "\t" + fpd.getAmzTillNBD()+  "\t" + fpd.getAmzTillLBD()+  "\t" + fpd.getAmzTodayToNBD()+ " \t "+ fpd.getPftAccrueTsfd()+ " \t"+ fpd.getDepreciatePri()+ " \t" );
				}
				isSuccess = true;
			}
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return isSuccess;
	}

	/**
	 * Method for Setting Finance Details to Profit Details Bean
	 * @param fpd
	 * @param finScheduleData
	 * @return
	 */
	private static FinanceProfitDetail setFinanceDetails(FinanceProfitDetail fpd, FinScheduleData finScheduleData) {
		
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();
		List<FeeRule> feeRuleList = finScheduleData.getFeeRules();
		
		fpd.setFinReference(financeMain.getFinReference());
		fpd.setFinBranch(financeMain.getFinBranch());
		fpd.setFinType(financeMain.getFinType());
		fpd.setRepayFrq(financeMain.getRepayFrq());
		fpd.setCustCIF(financeMain.getLovDescCustCIF());
		fpd.setFinCcy(financeMain.getFinCcy());
		fpd.setFinPurpose(financeMain.getFinPurpose());
		fpd.setFinContractDate(financeMain.getFinContractDate());
		fpd.setFinApprovedDate(financeMain.getFinApprovedDate());
		fpd.setFinStartDate(financeMain.getFinStartDate());
		fpd.setMaturityDate(financeMain.getMaturityDate());
		fpd.setFinAmount(financeMain.getFinAmount());
		fpd.setDownPayment(financeMain.getDownPayment());
		fpd.setFinStatus(financeMain.getFinStatus());
		fpd.setFinStsReason(financeMain.getFinStsReason());
		fpd.setCustId(financeMain.getCustID());

		fpd.setTAKAFULPaidAmt(BigDecimal.ZERO);
		fpd.setTAKAFULInsCal(BigDecimal.ZERO);
		fpd.setAdminPaidAmt(BigDecimal.ZERO);
		
		if(feeRuleList != null && feeRuleList.size() > 0){
			for (FeeRule feeRule : feeRuleList) {
				if(feeRule.getFeeCode().equals("TAKAFUL")){
					fpd.setTAKAFULPaidAmt(feeRule.getPaidAmount());
					fpd.setTAKAFULInsCal(feeRule.getFeeAmount());
				}
				if(feeRule.getFeeCode().equals("ADMIN")){
					fpd.setAdminPaidAmt(feeRule.getPaidAmount());
				}
			}
		}
		
		fpd.setFinAccount(financeMain.getFinAccount());	
		fpd.setFinAcType(financeType.getFinAcType());
		fpd.setDisbAccountId(financeMain.getDisbAccountId());	
		fpd.setDisbActCcy(financeMain.getFinCcy());
		fpd.setRepayAccountId(financeMain.getRepayAccountId());	
		fpd.setFinCustPftAccount(financeMain.getFinCustPftAccount());
		fpd.setIncomeAccount("");
		fpd.setUEIncomeSuspAccount("");
		fpd.setFinCommitmentRef(financeMain.getFinCommitmentRef());
		fpd.setFinIsActive(financeMain.isFinIsActive());

		fpd.setAcrTillLBD(fpd.getTdPftAccrued()); 
		fpd.setAmzTillLBD(fpd.getAmzTillNBD());

		return fpd;
	}

}
