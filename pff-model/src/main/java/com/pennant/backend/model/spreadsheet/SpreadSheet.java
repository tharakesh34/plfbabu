package com.pennant.backend.model.spreadsheet;

import java.io.Serializable;
import java.math.BigDecimal;
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

	private String custOffAddr;
	private String custResiAddr;
	private String addlVar1;
	private String addlVar2;
	private String addlVar3;
	private String addlVar4;
	private String addlVar5;
	private String addlVar6;
	private String addlVar7;
	private String addlVar8;
	private String addlVar9;
	private String addlVar10;
	private String finStartDate;
	private String customerPhoneNum;
	private boolean dataLoaded;
	private BigDecimal emiAmount;

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

	public String getCustResiAddr() {
		return custResiAddr;
	}

	public void setCustResiAddr(String custResiAddr) {
		this.custResiAddr = custResiAddr;
	}

	public String getCustOffAddr() {
		return custOffAddr;
	}

	public void setCustOffAddr(String custOffAddr) {
		this.custOffAddr = custOffAddr;
	}

	public String getAddlVar1() {
		return addlVar1;
	}

	public void setAddlVar1(String addlVar1) {
		this.addlVar1 = addlVar1;
	}

	public String getAddlVar2() {
		return addlVar2;
	}

	public void setAddlVar2(String addlVar2) {
		this.addlVar2 = addlVar2;
	}

	public String getAddlVar3() {
		return addlVar3;
	}

	public void setAddlVar3(String addlVar3) {
		this.addlVar3 = addlVar3;
	}

	public String getAddlVar4() {
		return addlVar4;
	}

	public void setAddlVar4(String addlVar4) {
		this.addlVar4 = addlVar4;
	}

	public String getAddlVar5() {
		return addlVar5;
	}

	public void setAddlVar5(String addlVar5) {
		this.addlVar5 = addlVar5;
	}

	public String getAddlVar6() {
		return addlVar6;
	}

	public void setAddlVar6(String addlVar6) {
		this.addlVar6 = addlVar6;
	}

	public String getAddlVar7() {
		return addlVar7;
	}

	public void setAddlVar7(String addlVar7) {
		this.addlVar7 = addlVar7;
	}

	public String getAddlVar8() {
		return addlVar8;
	}

	public void setAddlVar8(String addlVar8) {
		this.addlVar8 = addlVar8;
	}

	public String getAddlVar9() {
		return addlVar9;
	}

	public void setAddlVar9(String addlVar9) {
		this.addlVar9 = addlVar9;
	}

	public String getAddlVar10() {
		return addlVar10;
	}

	public void setAddlVar10(String addlVar10) {
		this.addlVar10 = addlVar10;
	}

	public String getFinStartDate() {
		return finStartDate;
	}

	public void setFinStartDate(String finStartDate) {
		this.finStartDate = finStartDate;
	}

	public String getCustomerPhoneNum() {
		return customerPhoneNum;
	}

	public void setCustomerPhoneNum(String customerPhoneNum) {
		this.customerPhoneNum = customerPhoneNum;
	}

	public BigDecimal getEmiAmount() {
		return emiAmount;
	}

	public void setEmiAmount(BigDecimal emiAmount) {
		this.emiAmount = emiAmount;
	}
}
