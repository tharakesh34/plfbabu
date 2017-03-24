package com.pff.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.interfaceservice.model.CoreBankAccountDetail;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;

public class AccountDetailProcess {
	
	private static Log LOG =null;
	public AccountDetailProcess() {
		LOG = LogFactory.getLog(AccountDetailProcess.class);
	}
	
	public CoreBankAccountDetail fetchAccountDetails(String accountNo,Connection connection) throws Exception {
		LOG.entering("fetchAccountDetails() ");	
		CoreBankAccountDetail  accountDetails=null;
		
		try{
			accountDetails=new CoreBankAccountDetail();
			accountDetails.setAcSummaryList(getAccountDetails(accountNo, connection));	
		}
		catch(Exception e){ 	
			LOG.error("fetchAccountDetails()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw e;
		}		
		LOG.exiting("fetchAccountDetails()");
		return accountDetails;	
	}

	//Method to fetch Account Details
	public ArrayList<CoreBankAccountDetail> getAccountDetails(String accountNo,Connection connection) throws Exception {
		LOG.entering("fetchAccount()");
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		ArrayList<CoreBankAccountDetail> details= new ArrayList<CoreBankAccountDetail>();
		try{
			pstmt =connection.prepareStatement("Select * from CustomerAccounts  Where AccountNumber=?");
			pstmt.setString(1,accountNo );
			rs = pstmt.executeQuery();

			while(rs.next())
			{
				CoreBankAccountDetail acDetails= new CoreBankAccountDetail();
				acDetails.setAccountNumber(rs.getString("AccountNumber"));
				acDetails.setIBAN(rs.getString("IBAN"));
				acDetails.setAcType(rs.getString("AccountType"));
				acDetails.setOpenStatus(rs.getString("AccountSubType"));
				acDetails.setAcBranch(rs.getString("BranchCode"));
				acDetails.setAcFullName(rs.getString("AccountName"));
				acDetails.setAcShrtName(rs.getString("AccountName"));
				acDetails.setAcCcy(rs.getString("Currency"));
				acDetails.setAcBal(rs.getBigDecimal("OpenActualBalance"));
				acDetails.setAcBal(rs.getBigDecimal("WorkingBalance"));
				acDetails.setAcBal(rs.getBigDecimal("CurrentBalance"));
				details.add(acDetails);
			}
		}catch(Exception e){ 	
			LOG.error("fetchAccount()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw new  Exception("Account Not Found Exception");
		}	
		finally{
			if(rs!=null){
				rs.close();
				rs=null;
			}
			if(pstmt!=null){
				pstmt.close();
				rs=null;
			}
		}  
		LOG.exiting("fetchAccount() ");
		return details;
	}

	//Method to fetch Customer Accounts
	public CoreBankAccountDetail fetchAccounts(CoreBankAccountDetail detailsVo, String currency, String acctType,Connection connection) throws Exception {
		LOG.entering("fetchAccounts() ");	
		try{
			detailsVo.setAcSummaryList(getAccounts(detailsVo.getCustCIF(),currency, acctType,connection));	
		}
		catch(Exception e){ 	
			LOG.error("fetchAccounts()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw e;
		}		
		LOG.exiting("fetchAccounts()");
		return detailsVo;	
	}
	
	
	//Fetch the No of Accounts based on Account CustomerID 
	private List<CoreBankAccountDetail> getAccounts(String custCIF,String currency,
			String acctType, Connection connection) throws Exception {
		LOG.entering("getAccounts()");
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		ArrayList<CoreBankAccountDetail> details= new ArrayList<CoreBankAccountDetail>();
		try{
			
			StringBuilder builder = new StringBuilder("Select * from CustomerAccounts  Where CustCIF=? ");
			
			boolean ccyExists = false;
			if(StringUtils.isNotBlank(currency)){
				builder.append(" AND Currency = ? ");
				ccyExists = true;
			}
			
			if(StringUtils.isNotBlank(acctType)){
				String[] accTypesList = acctType.split(",");
				
				String accTypeData = "";
				for (String acType : accTypesList) {
					accTypeData = accTypeData + "'"+acType+"',";
				}
				builder.append(" AND AccountType IN("+accTypeData.substring(0, accTypeData.length()-1)+") ");
			}
			pstmt =connection.prepareStatement(builder.toString());
			pstmt.setString(1,custCIF);
			if(ccyExists){
				pstmt.setString(2,currency);
			}
			rs = pstmt.executeQuery();

			while(rs.next())
			{
				CoreBankAccountDetail acDetails= new CoreBankAccountDetail();
				acDetails.setCustCIF(custCIF);
				acDetails.setAccountNumber(rs.getString("AccountNumber"));
				acDetails.setIBAN(rs.getString("IBAN"));
				acDetails.setAcType(rs.getString("AccountType"));
				acDetails.setOpenStatus(rs.getString("AccountSubType"));
				acDetails.setAcBranch(rs.getString("BranchCode"));
				acDetails.setAcFullName(rs.getString("AccountName"));
				acDetails.setAcCcy(rs.getString("Currency"));
				acDetails.setAcBal(rs.getBigDecimal("OpenActualBalance"));
				acDetails.setAcBal(rs.getBigDecimal("WorkingBalance"));
				acDetails.setAcBal(rs.getBigDecimal("CurrentBalance"));
				details.add(acDetails);
			}
		}catch(Exception e){ 	
			LOG.error("fetchAccount()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw new  Exception("Account Not Found Exception");
		}	
		finally{
			if(rs!=null){
				rs.close();
				rs=null;
			}
			if(pstmt!=null){
				pstmt.close();
				rs=null;
			}
		}  
		LOG.exiting("getAccounts() ");
		return details;
	}

}
