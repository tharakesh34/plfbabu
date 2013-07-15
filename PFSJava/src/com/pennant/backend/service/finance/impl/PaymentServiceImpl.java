package com.pennant.backend.service.finance.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.PaymentDetails;
import com.pennant.backend.model.finance.PaymentHeader;
import com.pennant.backend.service.finance.PaymentService;

public class PaymentServiceImpl implements PaymentService {

	private FinanceScheduleDetailDAO	financeScheduleDetailDAO;
	private TransactionEntryDAO	     transactionEntryDAO;

	private FinanceRepaymentsDAO	 financeRepaymentsDAO;

	@Override
	public PaymentHeader getPaymentHeader(FinanceMain financeMain, Date date) {
		PaymentHeader paymentHeader = new PaymentHeader();
		//paymentHeader.setPaymentDetails(getFinanceScheduleDetailDAO().getPaymentDetails(financeMain.getFinReference(), date, "_View"));
		paymentHeader.setUnPaidFinSchdDetails(getFinanceScheduleDetailDAO().getRepaySchdByFinRef(financeMain.getFinReference(), "_View",false));
		paymentHeader.setPaidFinSchdDetails(getFinanceScheduleDetailDAO().getRepaySchdByFinRef(financeMain.getFinReference(), "_View",true));		
		paymentHeader.setListLatePayTransEntries(getTransactionEntryDAO().getListFeeTransEntryById(Long.valueOf(financeMain.getLovDescFinLatePayRule()), "_AView"));
		paymentHeader.setListEarlyPayTransEntries(getTransactionEntryDAO().getListFeeTransEntryById(Long.valueOf(financeMain.getLovDescFinAEEarlyPay()), "_AView"));
		paymentHeader.setEarlyPayRule(getTransactionEntryDAO().getListFeeChargeRules(Long.valueOf(financeMain.getLovDescFinAEEarlyPay()), "EARLYPAY", "_AView"));
		paymentHeader.setLatePayRule(getTransactionEntryDAO().getListFeeChargeRules(Long.valueOf(financeMain.getLovDescFinLatePayRule()), "LATEPAY", "_AView"));

		return paymentHeader;

	}
	@Override
	public Map<Date,List<FinanceRepayments>> getFinanceRepaymentsByFinRef(String finRef,String type){
		List<FinanceRepayments> finRepayList=getFinanceRepaymentsDAO().getFinRepayListByFinRef(finRef, type);
		List<FinanceRepayments> financeRepaymentsList=new ArrayList<FinanceRepayments>();
		Map<Date,List<FinanceRepayments>> reapaymentDetails=new HashMap<Date,List<FinanceRepayments>>();
		int size=finRepayList.size();
		for (int i = 0; i < size; i++) {

			if(i!=size-1){
				if(finRepayList.get(i).getFinSchdDate().compareTo(finRepayList.get(i+1).getFinSchdDate())==0){
					if(reapaymentDetails.containsKey(finRepayList.get(i).getFinSchdDate())){
						financeRepaymentsList.add(finRepayList.get(i));
					}else{
						financeRepaymentsList=new ArrayList<FinanceRepayments>();
						financeRepaymentsList.add(finRepayList.get(i));

					}

					reapaymentDetails.put(finRepayList.get(i).getFinSchdDate(), financeRepaymentsList);
				}else{
					if(reapaymentDetails.containsKey(finRepayList.get(i).getFinSchdDate())){
						List<FinanceRepayments> tempList=(List<FinanceRepayments>) reapaymentDetails.get(finRepayList.get(i).getFinSchdDate());
						tempList.add(finRepayList.get(i));
						reapaymentDetails.put(finRepayList.get(i).getFinSchdDate(), tempList);	
					}else{
						financeRepaymentsList=new ArrayList<FinanceRepayments>();
						financeRepaymentsList.add(finRepayList.get(i));
						reapaymentDetails.put(finRepayList.get(i).getFinSchdDate(), financeRepaymentsList);
					}
				}
			}else{
				if(reapaymentDetails.containsKey(finRepayList.get(i).getFinSchdDate())){
					List<FinanceRepayments> tempList=(List<FinanceRepayments>) reapaymentDetails.get(finRepayList.get(i).getFinSchdDate());
					tempList.add(finRepayList.get(i));
					reapaymentDetails.put(finRepayList.get(i).getFinSchdDate(), tempList);	
				}else{
					financeRepaymentsList=new ArrayList<FinanceRepayments>();
					financeRepaymentsList.add(finRepayList.get(i));
					reapaymentDetails.put(finRepayList.get(i).getFinSchdDate(), financeRepaymentsList);
				}
			}
		}
		return reapaymentDetails;

	}

	@Override
	public PaymentDetails getPaymentDetails(String finref, Date date) {
		return getFinanceScheduleDetailDAO().getPaymentDetails(finref, date, "_AView");
	}

	@Override
	public long save(FinanceRepayments financeRepayments, String type) {
		return getFinanceRepaymentsDAO().save(financeRepayments, type);
	}

	@Override
	public void update(FinanceRepayments financeRepayments, String type) {
		getFinanceRepaymentsDAO().update(financeRepayments, type);
	}

	@Override
	public void delete(FinanceRepayments financeRepayments, String type) {
		getFinanceRepaymentsDAO().delete(financeRepayments, type);
	}

	@Override
	public void deleteWorkByFinRef(String finref) {
		getFinanceRepaymentsDAO().deleteByFinRef(finref, "_Work");
	}

	@Override
	public void maintainWorkSchedules(String finReference, long userId, List<FinanceScheduleDetail> financeScheduleDetails) {
		getFinanceScheduleDetailDAO().maintainWorkSchedules(finReference, userId, financeScheduleDetails);
	}

	@Override
	public void updateScheduleDetails(FinanceScheduleDetail financeScheduleDetail) {
		getFinanceScheduleDetailDAO().update(financeScheduleDetail, "_Work", false);
	}
	@Override
	public void updateMainScheduleDetails(FinanceScheduleDetail financeScheduleDetail) {
		getFinanceScheduleDetailDAO().update(financeScheduleDetail, "", false);
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {

		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public void setTransactionEntryDAO(TransactionEntryDAO transactionEntryDAO) {
		this.transactionEntryDAO = transactionEntryDAO;
	}

	public TransactionEntryDAO getTransactionEntryDAO() {
		return transactionEntryDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public FinanceRepaymentsDAO getFinanceRepaymentsDAO() {
		return financeRepaymentsDAO;
	}

}
