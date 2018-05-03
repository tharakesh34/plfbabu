package com.pff.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;

import com.pennant.interfaceservice.model.CoreBankAccountDetail;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;

public class CreateAccountProcess {

	private static Log LOG =null;
	CoreBankAccountDetail	actDet=new CoreBankAccountDetail();
	Random randomGenerator = new Random();
	private int randomInt;
	private String accountNo;

	public CreateAccountProcess() {
		LOG = LogFactory.getLog(CreateAccountProcess.class);
	}
	public CoreBankAccountDetail createAccount(CoreBankAccountDetail accountDetail,Connection connection) throws Exception {
		LOG.entering("createAccount() ");
		try
		{
			randomInt = randomGenerator.nextInt(100);
			accountNo= "10102000011"+randomInt;//FIXME
			accountDetail=saveAccountDetails(accountDetail, connection);
			actDet=getAccountDetails(accountNo, connection);
			accountDetail.setIBAN(actDet.getIBAN());
			accountDetail.setCIN(actDet.getCIN());
			accountDetail.setUIN(actDet.getUIN());
		}
		catch(Exception e){
			LOG.error("createAccount()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw e;
		}
		LOG.exiting("createAccount()");
		return accountDetail;		
	}

	// save the Account Details
	public CoreBankAccountDetail saveAccountDetails(CoreBankAccountDetail accountDetail,
			Connection connection) throws Exception {
		LOG.entering("saveAccountDetails() ");
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		try{		
			pstmt =connection.prepareStatement("insert into dbo.Accounts Values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, accountNo);
			pstmt.setString(2, accountDetail.getReferenceNumber());
			pstmt.setString(3, accountDetail.getAcCcy());
			pstmt.setString(4, "SA");
			pstmt.setString(5, accountDetail.getAcBranch());
			pstmt.setString(6, accountDetail.getAcFullName());
			pstmt.setLong(7, Long.parseLong(accountDetail.getCustCIF().replaceAll("PC", "")));	
			pstmt.setString(8, accountDetail.getProductCode());
			pstmt.setString(9, accountDetail.getAccountOfficer());
			pstmt.setString(10, accountDetail.getJointHolderID());				
			pstmt.setString(11, accountDetail.getJointRelationCode());
			pstmt.setString(12, accountDetail.getRelationNotes());
			pstmt.setString(13, accountDetail.getModeOfOperation());
			pstmt.setString(14, accountDetail.getMinNoOfSignatory());
			pstmt.setString(15, accountDetail.getIntroducer());
			pstmt.setBoolean(16, accountDetail.getPowerOfAttorneyFlag());
			pstmt.setString(17, accountDetail.getShoppingCardIssue());
			pstmt.setString(18, "AE070331234567890123456");//IBAN-35 
			pstmt.setString(19, " L25111KL1972PLC002449 ");//CIN-21
			pstmt.setString(20, "3KS355SDA");//UIN-9
			pstmt.executeUpdate();
			accountDetail.setAccountNumber(accountNo);
		}catch(Exception e){
			LOG.error("saveAccountDetails()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw e;
		}finally{
			if(rs!=null){
				rs.close();
				rs=null;
			}
			if(pstmt!=null){
				pstmt.close();
				rs=null;
			}
		}  
		LOG.exiting("saveAccountDetails()");
		return accountDetail;
	}

	//Get the Account Details by using AccountNumber
	private CoreBankAccountDetail getAccountDetails(String acNumber,Connection connection) throws Exception {
		LOG.entering("getAccountDetails() ");
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		CoreBankAccountDetail actDetails=new CoreBankAccountDetail();

		try{
			pstmt =connection.prepareStatement("select * from dbo.Accounts Where AcNumber=?");
			pstmt.setString(1, acNumber);
			rs = pstmt.executeQuery();	
			if(rs.next()){
				actDetails.setReferenceNumber("ReferenceNumber");
				actDetails.setIBAN(rs.getString("IBAN"));
				actDetails.setCIN(rs.getString("CIN"));
				actDetails.setUIN(rs.getString("UIN"));
			}
		}
		catch(Exception e){
			LOG.error("getAccountDetails()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw e;
		}finally{
			if(rs!=null){
				rs.close();
				rs=null;
			}
			if(pstmt!=null){
				pstmt.close();
				rs=null;
			}
		}
		LOG.exiting("getAccountDetails()");
		return actDetails;
	}
}
