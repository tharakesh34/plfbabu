package com.pennant.Interface.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.Interface.service.CustomerInterfaceService;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.dedup.DedupParm;
import com.pennant.backend.model.reports.AvailAccount;
import com.pennant.backend.model.reports.AvailCollateral;
import com.pennant.backend.model.reports.AvailCustomerDetail;
import com.pennant.backend.model.reports.AvailLimit;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.CustomerNotFoundException;
import com.pennant.coreinterface.model.CoreBankAvailCustomer;
import com.pennant.coreinterface.model.CoreBankBlackListCustomer;
import com.pennant.coreinterface.model.CoreBankNewCustomer;
import com.pennant.coreinterface.model.CoreBankingCustomer;
import com.pennant.coreinterface.model.CustomerLimit;
import com.pennant.coreinterface.service.CustomerDataProcess;

public class CustomerInterfaceServiceEquationImpl implements CustomerInterfaceService{
	
	private static Logger logger = Logger.getLogger(CustomerInterfaceServiceEquationImpl.class);
	
	private CustomerDataProcess customerDataProcess;
	
	public Customer fetchCustomerDetails(Customer customer) throws CustomerNotFoundException {
		logger.debug("Entering");

		CoreBankingCustomer coreCust = new CoreBankingCustomer();
		coreCust.setCustomerMnemonic(customer.getCustCIF());

		try {
			coreCust = getCustomerDataProcess().fetchInformation(coreCust);
			
			//Fill the customer data using Core Customer Banking Object
			customer.setCustCoreBank(coreCust.getCustomerMnemonic());
			
			// TODO This has To be Changed Based on The PCML
		/*	String[] names  = coreCust.getCustomerFullName().split(" ");
			customer.setCustFName(names[0]);
			if(names.length > 2){
				customer.setCustMName(names[1]);
				customer.setCustLName(names[2]);
			}else{
				if(names.length > 1){
					customer.setCustMName("");
					customer.setCustLName(names[1]);
				}else{
					customer.setCustMName("");
					customer.setCustLName("");
				}
			}			
			customer.setCustShrtName(coreCust.getDefaultAccountShortName());
			customer.setCustTypeCode(coreCust.getCustomerType());
			customer.setCustIsBlocked(coreCust.getCustomerClosed().equals("N")?false:true);
			customer.setCustIsClosed(coreCust.getCustomerClosed().equals("N")?false:true);
			customer.setCustIsDecease(coreCust.getCustomerClosed().equals("N")?false:true);
			customer.setCustIsActive(coreCust.getCustomerInactive().equals("N")?true:false);
			//customer.setCustLng(coreCust.getLanguageCode());
			customer.setCustParentCountry(coreCust.getParentCountry());
			customer.setCustCOB(coreCust.getParentCountry());
			customer.setCustRiskCountry(coreCust.getRiskCountry());
			customer.setCustResdCountry(coreCust.getResidentCountry());
			customer.setCustDftBranch(coreCust.getCustomerBranchMnemonic());
			//customer.setCustGroupSts(coreCust.getGroupStatus());
			//customer.setCustGroupID(coreCust.getGroupName());
			//customer.setCustSegment(coreCust.getSegmentIdentifier());
			customer.setCustSalutationCode(coreCust.getSalutation());
			customer.setCustDOB(coreCust.getCustDOB());
			customer.setCustGenderCode(coreCust.getGenderCode());
			customer.setCustPOB(coreCust.getCustPOB());
			customer.setCustPassportNo(coreCust.getCustPassportNum());
			customer.setCustPassportExpiry(coreCust.getCustPassportExpiry());
			customer.setCustIsMinor(coreCust.getMinor().equals("N")?false:true);
			customer.setCustTradeLicenceNum(coreCust.getTradeLicNumber());
			customer.setCustTradeLicenceExpiry(coreCust.getTradeLicExpiry());
			customer.setCustVisaNum(coreCust.getVisaNumber());
			customer.setCustVisaExpiry(coreCust.getVisaExpiry());
			customer.setCustNationality(coreCust.getNationality());*/
			
			customer.setNewRecord(true);
			
		}catch (Exception e) {
			logger.error("Exception " + e.getMessage());
			throw new CustomerNotFoundException(e);
		}
		logger.debug("Leaving");
		return customer;
	}
	
	/**
	 * Method for Creating Customer CIF in Core Banking System
	 */
	public String generateNewCIF(String operation, Customer customer, String finReference) throws CustomerNotFoundException {
		logger.debug("Entering");

		String custCIF = "";
		CoreBankNewCustomer coreCust = new CoreBankNewCustomer();
		coreCust.setOperation(operation);
		coreCust.setCustCtgType(customer.getLovDescCustCtgType());
		coreCust.setFinReference(finReference);
		
		if("A".equals(operation)){
			coreCust.setCustCIF(customer.getCustCIF());
			coreCust.setCustType(customer.getCustTypeCode());
			coreCust.setShortName(customer.getCustShrtName());
			coreCust.setCountry(customer.getCustParentCountry());
			coreCust.setBranch(customer.getCustDftBranch());
			coreCust.setCurrency(customer.getCustBaseCcy());
		}
		
		try {
			custCIF = getCustomerDataProcess().generateNewCIF(coreCust);
		} catch (CustomerNotFoundException e) {
			logger.error("Exception " + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("Exception " + e.getMessage());
			throw new CustomerNotFoundException(e);
		}
		logger.debug("Leaving");
		return custCIF;
	}
	
	/**
	 * Method for Fetch Customer Availment Ticket Details
	 * @throws CustomerNotFoundException 
	 */
	@Override
    public AvailCustomerDetail fetchAvailCustDetails(AvailCustomerDetail detail, BigDecimal newExposure, String ccy) throws CustomerNotFoundException {
		logger.debug("Entering");

		CoreBankAvailCustomer coreCust = new CoreBankAvailCustomer();
		coreCust.setCustMnemonic(detail.getCustCIF());
		coreCust.setOffBSRequired(detail.isOffBSRequired() ? "Y" : "N");
		coreCust.setAcRcvblRequired(detail.isAcRcvblRequired() ? "Y" : "N");
		coreCust.setAcPayblRequired(detail.isAcPayblRequired() ? "Y" : "N");
		coreCust.setAcUnclsRequired(detail.isAcUnclsRequired() ? "Y" : "N");
		coreCust.setCollateralRequired(detail.isCollateralRequired() ? "Y" : "N");
		
		try {
			
			coreCust = getCustomerDataProcess().fetchAvailInformation(coreCust);
			
			String custRspData = coreCust.getCustRspData();
			String limitCcy = "BHD";
			int limitCcyEdit = 3;
			
			if(coreCust.getCustomerLimit() != null){
				limitCcy = coreCust.getCustomerLimit().getLimitCurrency();
				limitCcyEdit = coreCust.getCustomerLimit().getLimitCcyEdit();
			}
			
			//Preparation of OFF-Balance Sheet Account Details
			int startIndex = 0;
			int acLenth = 51;
			if(coreCust.getOffBSCount() > 0){
				detail.setOffBSAcList(getAccountList(startIndex, coreCust.getOffBSCount(), custRspData, detail, limitCcy, limitCcyEdit));
				startIndex = (acLenth*coreCust.getOffBSCount());
			}
			
			//Preparation of Account Receivable Details
			if(coreCust.getAcRcvblCount() > 0){
				detail.setAcRcvblList(getAccountList(startIndex, coreCust.getAcRcvblCount(), custRspData, detail, limitCcy, limitCcyEdit));
				startIndex = startIndex + (acLenth*coreCust.getAcRcvblCount());
			}
			
			//Preparation of Account Payable Details
			if(coreCust.getAcPayblCount() > 0){
				detail.setAcPayblList(getAccountList(startIndex, coreCust.getAcPayblCount(), custRspData, detail,limitCcy, limitCcyEdit));
				startIndex = startIndex + (acLenth*coreCust.getAcPayblCount());
			}

			//Preparation of Account Unclassified Details
			if(coreCust.getAcUnclsCount() > 0){
				detail.setAcUnclsList(getAccountList(startIndex, coreCust.getAcUnclsCount(), custRspData, detail,limitCcy, limitCcyEdit));
				startIndex = startIndex + (acLenth*coreCust.getAcUnclsCount());
			}

			//Preparation of Collateral Details
			if(coreCust.getCollateralCount() > 0){
				detail.setColList(getCollateralList(startIndex, coreCust.getCollateralCount(), custRspData));
			}
			
			//Finalized Account Balances
			detail.setCustActualBal(coreCust.getCustActualBal());
			detail.setCustBlockedBal(coreCust.getCustBlockedBal());
			detail.setCustDeposit(coreCust.getCustDeposit());
			detail.setCustBlockedDeposit(coreCust.getCustBlockedDeposit());
			detail.setTotalCustBal(coreCust.getTotalCustBal());
			detail.setTotalCustBlockedBal(coreCust.getTotalCustBlockedBal());
			
			//Set Limit Summary Details
			AvailLimit availLimit = null;
			CustomerLimit custLimit = coreCust.getCustomerLimit();
			if(custLimit != null){
				
				BigDecimal curExposure = CalculationUtil.getConvertedAmount(ccy, custLimit.getLimitCurrency(), newExposure);
				
				availLimit = new AvailLimit();
				availLimit.setLimitExpiry(DateUtility.formatDate(custLimit.getLimitExpiry(),PennantConstants.dateFormate));
				availLimit.setLimitAmount(PennantApplicationUtil.amountFormate(custLimit.getLimitAmount(), custLimit.getLimitCcyEdit()));
				availLimit.setRiskAmount(PennantApplicationUtil.amountFormate(custLimit.getRiskAmount(), custLimit.getLimitCcyEdit()));
				availLimit.setLimitAvailAmt(PennantApplicationUtil.amountFormate(custLimit.getAvailAmount(), custLimit.getLimitCcyEdit()));
				availLimit.setLimitCcy(custLimit.getLimitCurrency());
				availLimit.setLimitCcyEdit( custLimit.getLimitCcyEdit());
				availLimit.setCurrentExposureLimit(PennantApplicationUtil.amountFormate(custLimit.getRiskAmount(), custLimit.getLimitCcyEdit()));
				availLimit.setNewExposure(PennantApplicationUtil.amountFormate(custLimit.getRiskAmount().add(curExposure), custLimit.getLimitCcyEdit()));
				availLimit.setAvailableLimit(PennantApplicationUtil.amountFormate(custLimit.getLimitAmount().subtract(custLimit.getRiskAmount().add(curExposure)), custLimit.getLimitCcyEdit()));
				availLimit.setLimitRemarks(custLimit.getRemarks().trim());
				detail.setAvailLimit(availLimit);
			}
			
		} catch (CustomerNotFoundException e) {
			logger.error("Exception " + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("Exception " + e.getMessage());
			throw new CustomerNotFoundException(e);
		}
		logger.debug("Leaving");
		return detail;
    }
	
	private List<AvailAccount> getAccountList(int startIndex, int count, String custRspData, AvailCustomerDetail detail, String limitCcy, int limitCcyEdit){
		
		String accDtlData = custRspData.substring(startIndex, startIndex+(51*count)); 
		ArrayList<AvailAccount> list = new ArrayList<AvailAccount>();
		BigDecimal totAcSum = BigDecimal.ZERO;
		int acLenth = 51;
		int accSize = 0;
		
		for (int i = 0; i < count; i++) {
			
			String accData = accDtlData.substring(0, acLenth);
			if(new BigDecimal(accData.substring(20, 35)).compareTo(BigDecimal.ZERO) == 0){
				accDtlData = accDtlData.substring(acLenth);
				continue;
			}
			
			AvailAccount account = new AvailAccount();
			account.setAccountNum(PennantApplicationUtil.formatAccountNumber(accData.substring(0, 13)));
			account.setAcType(accData.substring(13, 15));
			account.setAccountCcy(accData.substring(15, 18));
			account.setConvertCcy(limitCcy);
			
			int ccyFormatter = Integer.parseInt(accData.substring(18, 19));
			if (accData.substring(19, 20).equals("-")) {
				account.setAcBalance(PennantApplicationUtil.amountFormate(new BigDecimal(accData.substring(20, 35)).negate(),ccyFormatter));
            }else{
            	account.setAcBalance(PennantApplicationUtil.amountFormate(new BigDecimal(accData.substring(20, 35)),ccyFormatter));
            }
			
			BigDecimal actAmount = new BigDecimal(accData.substring(36, 51));
			actAmount = CalculationUtil.getConvertedAmount("BHD", limitCcy, actAmount);
					
			if (accData.substring(35, 36).equals("-")) {
				account.setAcBalBHD(PennantApplicationUtil.amountFormate(actAmount.negate(),limitCcyEdit));
				totAcSum = totAcSum.subtract(actAmount);
            }else{
            	account.setAcBalBHD(PennantApplicationUtil.amountFormate(actAmount,limitCcyEdit));
            	totAcSum = totAcSum.add(actAmount);
            }
			list.add(account);
			accDtlData = accDtlData.substring(acLenth);
			accSize = accSize + 1;
			
			detail.getAccTypeList().add(account.getAcType());
        }
		
		//Add Total Summation 
		if(accSize > 0){
			
			AvailAccount account = new AvailAccount();
			account.setAccountNum("Total");
			account.setAcBalBHD(PennantApplicationUtil.amountFormate(totAcSum,limitCcyEdit));
			list.add(account);
		}
		return list;
	}
	
	private List<AvailCollateral> getCollateralList(int startIndex, int count, String custRspData){
		
		custRspData = StringUtils.rightPad(custRspData,  startIndex+(166*count), " ");
		String colDtlData = custRspData.substring(startIndex, startIndex+(166*count)); 
		ArrayList<AvailCollateral> list = new ArrayList<AvailCollateral>();
		for (int i = 0; i < count; i++) {
			
			String colData = colDtlData.substring(0, 166);
			AvailCollateral collateral = new AvailCollateral();
			collateral.setCollateralReference(colData.substring(0, 35));
			collateral.setCollateralType(colData.substring(35, 38)+"-"+colData.substring(38, 73));
			collateral.setCollateralComplete(colData.substring(73, 74));
			collateral.setCollateralCcy(colData.substring(74, 77));
			int ccyFormatter = Integer.parseInt(colData.substring(77, 78));
			if (!(colData.substring(78, 85).equals("0") || colData.substring(78, 85).equals(""))) {
				if(colData.substring(78, 85).equals("9999999")){
					collateral.setCollateralExpiry("Open");
				}else{
					collateral.setCollateralExpiry(DateUtility.formatDate(DateUtility.convertDateFromAS400(
							new BigDecimal(colData.substring(78, 85))), PennantConstants.dateFormate));
				}
			} 
			if (!(colData.substring(85, 92).equals("0") || colData.substring(85, 92).equals(""))) {
				if(colData.substring(85, 92).equals("9999999")){
					collateral.setCollateralExpiry("Open");
				}else{
					collateral.setLastReview(DateUtility.formatDate(DateUtility.convertDateFromAS400(
							new BigDecimal(colData.substring(85, 92))), PennantConstants.dateFormate));
				}
			} 
			collateral.setCollateralValue(PennantApplicationUtil.amountFormate(new BigDecimal(colData.substring(92,107)), ccyFormatter));
			collateral.setBankValuation(PennantApplicationUtil.amountFormate(new BigDecimal(colData.substring(107,122)), ccyFormatter));
			collateral.setMargin(PennantApplicationUtil.formatRate(PennantApplicationUtil.formateAmount(new BigDecimal(colData.substring(122, 127)), 3).doubleValue(),2)+"%");
			collateral.setCollateralLoc(colData.substring(127,131));
			collateral.setCollateralDesc(colData.substring(131,166));
			
			list.add(collateral);
			colDtlData = colDtlData.substring(166);
        }
		return list;
	}

	/**
	 * Method for Fetching List of BlackListed Customer Data
	 */
	@Override
    public List<BlackListCustomers> fetchBlackListedCustomers(BlackListCustomers customer,
            List<DedupParm> dedupParmList) {

		CoreBankBlackListCustomer coreBankBlackListCustomer = new CoreBankBlackListCustomer();
		try {
	        BeanUtils.copyProperties(coreBankBlackListCustomer, customer);
        } catch (IllegalAccessException e) {
	        e.printStackTrace();
        } catch (InvocationTargetException e) {
	        e.printStackTrace();
        }
		List<BlackListCustomers> blackListCustomerList = new ArrayList<BlackListCustomers>();
		for (DedupParm dedupParm : dedupParmList) {
			List<CoreBankBlackListCustomer> list = getCustomerDataProcess().getBlackListedCustomers(coreBankBlackListCustomer, dedupParm.getSQLQuery());
			
			for (int i = 0; i < list.size(); i++) {
				CoreBankBlackListCustomer coreBankList = list.get(i);
				BlackListCustomers blackListCustomer = new BlackListCustomers();
				try {
	                BeanUtils.copyProperties(blackListCustomer, coreBankList);
                } catch (IllegalAccessException e) {
	                e.printStackTrace();
                } catch (InvocationTargetException e) {
	                e.printStackTrace();
                }
				blackListCustomer.setWatchListRule(dedupParm.getQueryCode());
				blackListCustomerList.add(blackListCustomer);
            }
        }
		return blackListCustomerList;
    }

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setCustomerDataProcess(CustomerDataProcess customerDataProcess) {
	    this.customerDataProcess = customerDataProcess;
    }
	public CustomerDataProcess getCustomerDataProcess() {
	    return customerDataProcess;
    }

}
