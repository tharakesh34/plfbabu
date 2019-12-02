package com.pennant.backend.model.spreadsheet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.JointAccountDetail;

public class SpreadSheet implements Serializable {

	private static final long serialVersionUID = 1L;

	private FinanceMain fm = new FinanceMain();
	private Customer cu = new Customer();
	private List<JointAccountDetail> coappList = new ArrayList<>();
	// Extended fields
	private Map<String, Object> ef = new HashMap<>();
	private Map<String, Object> loanEf = new HashMap<>();

	// Temporary solution to manage Co-applicants
	private Customer cu1 = new Customer();
	private Customer cu2 = new Customer();
	private Customer cu3 = new Customer();
	private Customer cu4 = new Customer();
	private Customer cu5 = new Customer();

	private boolean dataLoaded;

	public FinanceMain getFm() {
		return fm;
	}

	public void setFm(FinanceMain fm) {
		this.fm = fm;
	}

	public Customer getCu() {
		return cu;
	}

	public void setCu(Customer cu) {
		this.cu = cu;
	}

	public List<JointAccountDetail> getJaList() {
		return coappList;
	}

	public void setJaList(List<JointAccountDetail> jaList) {
		this.coappList = jaList;
	}

	public Map<String, Object> getEf() {
		return ef;
	}

	public void setEf(Map<String, Object> ef) {
		this.ef = ef;
	}

	public Customer getCu1() {
		return cu1;
	}

	public void setCu1(Customer cu1) {
		this.cu1 = cu1;
	}

	public List<JointAccountDetail> getCoappList() {
		return coappList;
	}

	public void setCoappList(List<JointAccountDetail> coappList) {
		this.coappList = coappList;
	}

	public Customer getCu2() {
		return cu2;
	}

	public void setCu2(Customer cu2) {
		this.cu2 = cu2;
	}

	public Customer getCu3() {
		return cu3;
	}

	public void setCu3(Customer cu3) {
		this.cu3 = cu3;
	}

	public Customer getCu4() {
		return cu4;
	}

	public void setCu4(Customer cu4) {
		this.cu4 = cu4;
	}

	public Customer getCu5() {
		return cu5;
	}

	public void setCu5(Customer cu5) {
		this.cu5 = cu5;
	}

	public boolean isDataLoaded() {
		return dataLoaded;
	}

	public void setDataLoaded(boolean dataLoaded) {
		this.dataLoaded = dataLoaded;
	}

	public Map<String, Object> getLoanEf() {
		return loanEf;
	}

	public void setLoanEf(Map<String, Object> loanEf) {
		this.loanEf = loanEf;
	}

}
