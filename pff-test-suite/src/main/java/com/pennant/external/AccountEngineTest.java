package com.pennant.external;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.pennant.TestingUtil;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.finance.FinanceDetailService;

/**
 * Method for testing Account Engine Class for Transaction Entry Details
 */
public class AccountEngineTest extends TestingUtil {

	private static boolean isSuccess = false;
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

	public static boolean RunTestCase(ApplicationContext mainContext, String finReference) {
		try {

			setDetailService((FinanceDetailService)mainContext.getBean("financeDetailService"));
			DataSet dataSet = new DataSet();
			
			//Select Finance Reference for Testing Accounting Engine Calculation
			FinScheduleData data = getDetailService().getFinSchDataByFinRef(finReference, "_View", 0);
			
			dataSet.setFinReference(data.getFinReference());
			dataSet.setCustId(data.getFinanceMain().getCustID());
			dataSet.setFinEvent("ADDDBSP");
			dataSet.setPostDate(new Date());
			dataSet.setValueDate(new Date());
			dataSet.setSchdDate(new Date());
			dataSet.setDisburseAccount(data.getFinanceMain().getDisbAccountId());
			dataSet.setRepayAccount(data.getFinanceMain().getRepayAccountId());
			dataSet.setFinAccount("");
			dataSet.setFinCcy(data.getFinanceMain().getFinCcy());
			dataSet.setFinBranch(data.getFinanceMain().getFinBranch());
			
			//Amount Code details calculation
			AEAmountCodes amountCodeDetail = com.pennant.app.util.AEAmounts.procAEAmounts(data.getFinanceMain(),
					data.getFinanceScheduleDetails(), new FinanceProfitDetail(), new Date());
			
			List<ReturnDataSet> returnDataSets = new ArrayList<ReturnDataSet>();

			// Building Account Entry Details
			returnDataSets = new AccountEngineExecution().getAccEngineExecResults(dataSet,amountCodeDetail,"N", null, false, data.getFinanceType());

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
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return isSuccess;
	}

}
