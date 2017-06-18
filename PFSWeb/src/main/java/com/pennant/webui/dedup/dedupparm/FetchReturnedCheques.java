package com.pennant.webui.dedup.dedupparm;

import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.returnedcheques.ReturnedCheques;
import com.pennant.backend.service.applicationmaster.ReturnedChequeService;

public class FetchReturnedCheques {

	private static final Logger logger = Logger
			.getLogger(FetchReturnedCheques.class);

	private int userAction = -1;
	private static ReturnedChequeService returnedChequeService;
	private FinanceDetail financeDetail;
	private List<ReturnedCheques> returnedCheques;

	String ReturnedCheque_List = "chequeNo,returnDate,amount";

	public FetchReturnedCheques() {
		super();
	}

	public static FinanceDetail getReturnedChequeCustomer(
			FinanceDetail aFinanceDetail, Component parent) {
		return new FetchReturnedCheques(aFinanceDetail, parent)
		.getFinanceDetail();
	}

	@SuppressWarnings("unchecked")
	public FetchReturnedCheques(FinanceDetail aFinanceDetail, Component parent) {
		super();
		setFinanceDetail(aFinanceDetail);
		ReturnedCheques returnedCheques = doSetReturnChequeDedup(aFinanceDetail
				.getCustomerDetails().getCustomer());
		setReturnedCheques(getReturnedChequeService().fetchReturnedCheques(returnedCheques));
		ShowReturnedCheques details = null;
		if (getReturnedCheques() != null && getReturnedCheques().size() > 0) {

			Object dataObject = ShowReturnedCheques.show(parent,
					getReturnedCheques(), ReturnedCheque_List, returnedCheques);
			details = (ShowReturnedCheques) dataObject;

			if (details != null) {
				System.out.println("THE ACTIONED VALUE IS ::::"
						+ details.getUserAction());
				logger.debug("The User Action is " + details.getUserAction());
				userAction = details.getUserAction();
				setReturnedCheques((List<ReturnedCheques>) details.getObject());
			}
		} else {
			userAction = -1;
		}

		if (userAction == -1) {
			aFinanceDetail.getFinScheduleData().getFinanceMain().setChequeFound(false);
			aFinanceDetail.getFinScheduleData().getFinanceMain().setChequeOverride(false);
		} else{
			aFinanceDetail.getFinScheduleData().getFinanceMain().setChequeFound(true);
			if (userAction == 1) {
				aFinanceDetail.getFinScheduleData().getFinanceMain().setChequeOverride(true);
			} else {
				aFinanceDetail.getFinScheduleData().getFinanceMain().setChequeOverride(false);
			}
		}
		setFinanceDetail(aFinanceDetail);

	}

	private ReturnedCheques doSetReturnChequeDedup(Customer customer) {
		logger.debug("Entering");
		ReturnedCheques returnedCheques = new ReturnedCheques();
		returnedCheques.setCustCIF(customer.getCustCIF());
		logger.debug("Leaving");
		return returnedCheques;
	}

	public int getUserAction() {
		return userAction;
	}

	public void setUserAction(int userAction) {
		this.userAction = userAction;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public List<ReturnedCheques> getReturnedCheques() {
		return returnedCheques;
	}

	public void setReturnedCheques(List<ReturnedCheques> returnedCheques) {
		this.returnedCheques = returnedCheques;
	}


	public  ReturnedChequeService getReturnedChequeService() {
		return returnedChequeService;
	}

	public  void setReturnedChequeService(ReturnedChequeService returnedChequeService) {
		FetchReturnedCheques.returnedChequeService = returnedChequeService;
	}
}
