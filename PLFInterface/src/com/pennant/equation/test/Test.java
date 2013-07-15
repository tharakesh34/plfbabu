package com.pennant.equation.test;
import java.util.List;

import com.pennant.coreinterface.exception.CustomerLimitProcessException;
import com.pennant.coreinterface.vo.CoreBankAccountDetail;
import com.pennant.coreinterface.vo.CoreBankingCustomer;
import com.pennant.coreinterface.vo.CustomerLimit;
import com.pennant.equation.process.CustomerLimitProcess;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		CoreBankingCustomer coreCust = new CoreBankingCustomer();
		CoreBankAccountDetail coreAcct = new CoreBankAccountDetail();
		CustomerLimit custLimit = new CustomerLimit();

		custLimit.setCustMnemonic("080011");
		custLimit.setCustLocation("");
		custLimit.setLimitCategory("LS121");


		try {

			CustomerLimitProcess custLmtprocess = new CustomerLimitProcess();
			List <CustomerLimit> list = custLmtprocess.fetchLimitDetails(custLimit);
			//List <CustomerLimit> list = custLmtprocess.fetchGroupLimitDetails(custLimit) ;

			for(CustomerLimit item :list) {
				System.out.println("====Customer Limits for custId : "+custLimit.getCustMnemonic());

				//System.out.println("Customer mnemonic "+ custLimit.getCustMnemonic());
				//System.out.println("Customer Location "+ custLimit.getCustLocation());
				System.out.println("LmtCategory "+ item.getLimitCategory());
				System.out.println("Group Limit "+ item.getGroupLimit());
				System.out.println("LmtCurrency "+ item.getLimitCurrency());
				System.out.println("LmtExpiry "+ item.getLimitExpiry());
				System.out.println("LmtAmount "+ item.getLimitAmount());
				System.out.println("AvailAmount "+ item.getAvailAmount());
				System.out.println("RiskAmount "+ item.getRiskAmount());
				System.out.println("Error Id:  "+ item.getErrorId());
				System.out.println("Error Msg: "+ item.getErrorMsg());


				System.out.println("\n");
			}



		} catch (CustomerLimitProcessException e) {
			System.out.print("Error: " + e.getMessage());
		}

	}

}
