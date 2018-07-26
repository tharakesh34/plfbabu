package com.pennant.backend.dao.customermasters.impl;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.customermasters.CoreCustomerDAO;
import com.pennant.backend.model.customermasters.CoreCustomer;
import com.pennant.backend.model.customermasters.Customer;
import com.pennanttech.pennapps.core.jdbc.BasicDao;

public class CoreCustomerDAOImpl extends BasicDao<Customer> implements CoreCustomerDAO {

	private static Logger logger = Logger.getLogger(CoreCustomerDAOImpl.class);
	
	public CoreCustomerDAOImpl() {
		super();
	}
	
	/**
	 * Method for saving the Core Customer data(Not Available in PFF) receiving from Interface
	 * 
	 */
	@Override
    public void save(CoreCustomer coreCustomer) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into CoreCustomer" );
		insertSql.append("(CustID, internalRating, dateOfInternalRating, relationCode, relationShipCIF, faxIndemity, emailIndemity, kycRiskLevel," );
		insertSql.append(" foreignPolicyExposed, pliticalyExposed, monthlyTurnover, introducer, referenceName, purposeOfRelationShip, sourceOfIncome," );
		insertSql.append(" expectedTypeOfTrans, monthlyOutageVolume, monthlyIncomeVolume, maximumSingleDeposit, maximumSingleWithdrawal, annualIncome," );
		insertSql.append(" countryOfOriginOfFunds, countryOfSourceOfIncome, sourceOfWealth, isKYCUptoDate, listedOnStockExchange, nameOfExchange, stockCodeOfCustomer," );
		insertSql.append(" customerVisitReport, initialDeposit, futureDeposit, annualTurnOver, parentCompanyDetails, nameOfParentCompany, parentCompanyPlaceOfIncorp," );
		insertSql.append(" emirateOfIncop, nameOfApexCompany, noOfEmployees, noOfUAEBranches, noOfOverseasBranches, overSeasbranches, nameOfAuditors, financialHighlights," );
		insertSql.append(" bankingRelationShip, pFFICertfication, pOAFlag, pOACIF, pOAHoldersname, passportNumber, emiratesIDNumber, nationality, pOAIssuancedate, " );
		insertSql.append(" pOAExpirydate, passportExpiryDate, emiratesIDExpiryDate, empName, issueCheque," );
		insertSql.append(" TotalNoOfPartners, ModeOfOperation, PowerOfAttorney, AuditedFinancials, FaxOfIndemity, IndemityEmailAddress, ChequeBookRequest," );
		insertSql.append(" CurrencyOfFinancials, GrossProfit, NetProfit, ShareCapital, ThroughputAmount, ThroughputFrequency, ThroughputAccount, HaveBranchInUS, " );
		insertSql.append(" SalaryCurrency, Salary, SalaryDateFreq, BusinessType, NameOfBusiness)" );

		insertSql.append(" Values(:CustID, :internalRating, :dateOfInternalRating, :relationCode, :relationShipCIF, :faxIndemity, :emailIndemity, :kycRiskLevel," );
		insertSql.append(" :foreignPolicyExposed, :pliticalyExposed, :monthlyTurnover, :introducer, :referenceName, :purposeOfRelationShip, :sourceOfIncome," );
		insertSql.append(" 	:expectedTypeOfTrans, :monthlyOutageVolume, :monthlyIncomeVolume, :maximumSingleDeposit, :maximumSingleWithdrawal, :annualIncome," );
		insertSql.append(" 	:countryOfOriginOfFunds, :countryOfSourceOfIncome, :sourceOfWealth, :isKYCUptoDate, :listedOnStockExchange, :nameOfExchange, :stockCodeOfCustomer," );
		insertSql.append("  :customerVisitReport, :initialDeposit, :futureDeposit, :annualTurnOver, :parentCompanyDetails, :nameOfParentCompany, :parentCompanyPlaceOfIncorp," );
		insertSql.append("  :emirateOfIncop, :nameOfApexCompany, :noOfEmployees, :noOfUAEBranches, :noOfOverseasBranches, :overSeasbranches, :nameOfAuditors, :financialHighlights," );
		insertSql.append("  :bankingRelationShip, :pFFICertfication, :pOAFlag, :pOACIF, :pOAHoldersname, :passportNumber, :emiratesIDNumber, :nationality, :pOAIssuancedate, " );
		insertSql.append("  :pOAExpirydate, :passportExpiryDate, :emiratesIDExpiryDate, :empName, :issueCheque," );
		insertSql.append("  :TotalNoOfPartners, :ModeOfOperation, :PowerOfAttorney, :AuditedFinancials, :FaxOfIndemity, :IndemityEmailAddress, :ChequeBookRequest," );
		insertSql.append("  :CurrencyOfFinancials, :GrossProfit, :NetProfit, :ShareCapital, :ThroughputAmount, :ThroughputFrequency, :ThroughputAccount, :HaveBranchInUS, " );
		insertSql.append("  :SalaryCurrency, :Salary, :SalaryDateFreq, :BusinessType, :NameOfBusiness)" );
		
        logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(coreCustomer);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
	}

	@Override
    public void update(CoreCustomer coreCustomer) {
	    // TODO Auto-generated method stub
	    
    }

	
}
