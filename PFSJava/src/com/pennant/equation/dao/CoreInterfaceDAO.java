package com.pennant.equation.dao;

import java.util.List;

import com.pennant.coreinterface.model.EquationAccountType;
import com.pennant.coreinterface.model.EquationCurrency;
import com.pennant.coreinterface.model.EquationCustomerGroup;
import com.pennant.coreinterface.model.EquationCustomerRating;
import com.pennant.coreinterface.model.EquationCustomerType;
import com.pennant.coreinterface.model.EquationDepartment;
import com.pennant.coreinterface.model.EquationRelationshipOfficer;
import com.pennant.coreinterface.model.FinIncomeAccount;
import com.pennant.coreinterface.model.IncomeAccountTransaction;

public interface CoreInterfaceDAO {

	List<EquationCurrency> fetchCurrecnyDetails() ;
	List<EquationRelationshipOfficer> fetchRelationshipOfficerDetails(); 
	List<EquationCustomerType> fetchCustomerTypeDetails(); 
	List<EquationDepartment> fetchDepartmentDetails(); 
	List<EquationCustomerGroup> fetchCustomerGroupDetails();
	List<EquationAccountType> fetchAccountTypeDetails();
	List<EquationCustomerRating> fetchCustomerRatingDetails();
	void saveCurrecnyDetails(List<EquationCurrency> currencyList);
	void saveRelationShipOfficerDetails(List<EquationRelationshipOfficer> relationshipOfficerList);
	void saveCustomerTypeDetails(List<EquationCustomerType> customerTypes);
	void saveDepartmentDetails(List<EquationDepartment> departments);
	void saveCustomerGroupDetails(List<EquationCustomerGroup> customerGroups);
	void saveAccountTypeDetails(List<EquationAccountType> accountTypes);
	void saveAccountTypeNatureDetails(List<EquationAccountType> accountTypeNatures);
	void saveCustomerRatingDetails(List<EquationCustomerRating> customerRatings);
	void updateCurrecnyDetails(List<EquationCurrency> currencyList);
	void updateRelationShipOfficerDetails(List<EquationRelationshipOfficer> relationshipOfficerList);
	void updateCustomerTypeDetails(List<EquationCustomerType> customerTypes);
	void updateDepartmentDetails(List<EquationDepartment> departments);
	void updateCustomerGroupDetails(List<EquationCustomerGroup> customerGroups);
	void updateAccountTypeDetails(List<EquationAccountType> accountTypes);
	void updateAccountTypeNatureDetails(List<EquationAccountType> accountTypeNatures);
	void updateCustomerRatingDetails(List<EquationCustomerRating> customerRatings);
   ////++++++++++++++++++ Month End Downloads  +++++++++++++++++++//
	public void saveIncomeAccounts(FinIncomeAccount finIncomeAccount);
	public List<FinIncomeAccount> fetchIncomeAccountDetails(FinIncomeAccount finIncomeAccount) ;
	public boolean checkIncomeTransactionsExist(IncomeAccountTransaction incomeAccountTransaction);
	public void saveIncomeAccTransactions(List<IncomeAccountTransaction> incomeAccountTransactions);
}
