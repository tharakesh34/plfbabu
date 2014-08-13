package com.pennant.equation.dao;

import java.util.List;

import com.pennant.coreinterface.model.EquationAccountType;
import com.pennant.coreinterface.model.EquationCurrency;
import com.pennant.coreinterface.model.EquationCustomerGroup;
import com.pennant.coreinterface.model.EquationCustomerRating;
import com.pennant.coreinterface.model.EquationCustomerType;
import com.pennant.coreinterface.model.EquationDepartment;
import com.pennant.coreinterface.model.EquationRelationshipOfficer;

public interface CoreInterfaceDAO {

	public List<EquationCurrency> fetchCurrecnyDetails() ;
	public List<EquationRelationshipOfficer> fetchRelationshipOfficerDetails(); 
	public List<EquationCustomerType> fetchCustomerTypeDetails(); 
	public List<EquationDepartment> fetchDepartmentDetails(); 
	public List<EquationCustomerGroup> fetchCustomerGroupDetails();
	public List<EquationAccountType> fetchAccountTypeDetails();
	public List<EquationCustomerRating> fetchCustomerRatingDetails();
	public void saveCurrecnyDetails(List<EquationCurrency> currencyList);
	public void saveRelationShipOfficerDetails(List<EquationRelationshipOfficer> relationshipOfficerList);
	public void saveCustomerTypeDetails(List<EquationCustomerType> customerTypes);
	public void saveDepartmentDetails(List<EquationDepartment> departments);
	public void saveCustomerGroupDetails(List<EquationCustomerGroup> customerGroups);
	public void saveAccountTypeDetails(List<EquationAccountType> accountTypes);
	public void saveAccountTypeNatureDetails(List<EquationAccountType> accountTypeNatures);
	public void saveCustomerRatingDetails(List<EquationCustomerRating> customerRatings);
	public void updateCurrecnyDetails(List<EquationCurrency> currencyList);
	public void updateRelationShipOfficerDetails(List<EquationRelationshipOfficer> relationshipOfficerList);
	public void updateCustomerTypeDetails(List<EquationCustomerType> customerTypes);
	public void updateDepartmentDetails(List<EquationDepartment> departments);
	public void updateCustomerGroupDetails(List<EquationCustomerGroup> customerGroups);
	public void updateAccountTypeDetails(List<EquationAccountType> accountTypes);
	public void updateAccountTypeNatureDetails(List<EquationAccountType> accountTypeNatures);
	public void updateCustomerRatingDetails(List<EquationCustomerRating> customerRatings);
	
}
