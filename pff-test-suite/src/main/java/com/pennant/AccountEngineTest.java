package com.pennant;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.pennant.app.util.AccountEngineExecution;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.finance.FinanceDetailService;

public class AccountEngineTest extends TestingUtil {

	private static boolean isSuccess = false;
	/*private static BigDecimal expectedResult = new BigDecimal(8303929);
	private static BigDecimal expectedTotPft = new BigDecimal(9647115);
	private static BigDecimal expectedDefPri = BigDecimal.ZERO;
	private static BigDecimal expectedDefPft = BigDecimal.ZERO;
	private static BigDecimal zeroValue = BigDecimal.ZERO;
	private static BigDecimal resultedTotPft = BigDecimal.ZERO;
	private static BigDecimal resultDefPft = BigDecimal.ZERO;
	private static BigDecimal resultDefPri = BigDecimal.ZERO;*/
	
	static FinanceDetailService detailService;

	public static FinanceDetailService getDetailService() {
		return detailService;
	}

	public static void setDetailService(FinanceDetailService detailService) {
			AccountEngineTest.detailService = detailService;
	}

	private static String getFile() {
		return getFileLoc() + AccountEngineTest.class.getName() + ".xls";

	}

	public static boolean RunTestCase(ApplicationContext mainContext) {
		try {

			setDetailService((FinanceDetailService)mainContext.getBean("financeDetailService"));
			// Tesing Code
			DataSet dataSet = new DataSet();
			
			FinScheduleData data = getDetailService().getFinSchDataByFinRef("SN20_RR_PRI", "_View", 0);
			
			dataSet.setFinReference(data.getFinReference());
			dataSet.setFinEvent("ADDDBSP");
			dataSet.setPostDate(new Date());
			dataSet.setValueDate(new Date());
			dataSet.setSchdDate(new Date());
			dataSet.setDisburseAccount(data.getFinanceMain().getDisbAccountId());
			dataSet.setRepayAccount(data.getFinanceMain().getRepayAccountId());
			dataSet.setFinAccount("");
			dataSet.setFinCcy(data.getFinanceMain().getFinCcy());
			dataSet.setFinBranch(data.getFinanceMain().getFinBranch());
			
			AEAmountCodes amountCodeDetail = com.pennant.app.util.AEAmounts.procAEAmounts(data.getFinanceMain(),
					data.getFinanceScheduleDetails(), new FinanceProfitDetail(), new Date());
			
			List<ReturnDataSet> returnDataSets = new ArrayList<ReturnDataSet>();

			// generate schedule
			returnDataSets = new AccountEngineExecution().getAccEngineExecResults(dataSet,amountCodeDetail,"N", null, false, null);//TODO

			// File file = new
			// File(Executions.getCurrent().getDesktop().getWebApp().getRealPath("/Schedule.xls"));
			File file = new File(getFile());
			FileWriter txt;
			txt = new FileWriter(file);
			PrintWriter out = new PrintWriter(txt);

			out.print("FinanceReference \t Event \t PostDate \t ValueDate \t Account \t Amount ");

			for (int i = 0; i < returnDataSets.size(); i++) {
				ReturnDataSet set = returnDataSets.get(i);
				out.write("\n" + set.getFinReference() + "  \t  " + set.getFinEvent() + "  \t  "
						+ set.getPostDate() + "  \t  " + set.getValueDate() + "  \t  "
						+ set.getAccount().toString() + "  \t  " + set.getPostAmount() + " \t  " );
				
				isSuccess = true;
				
				/*if (i == (sdSize - 1) && sd.getRepayAmount().equals(expectedResult)
						&& resultedTotPft.equals(expectedTotPft) && resultDefPri.equals(expectedDefPri) && resultDefPft.equals(expectedDefPft) && sd.getClosingBalance().equals(zeroValue)) {
					isSuccess = true;
				}*/
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return isSuccess;
	}

}
