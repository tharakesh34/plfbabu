/*package com.pff.process;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.pennant.interfaceservice.model.AccountEntries;
import com.pennant.interfaceservice.model.AccountPostingReply;
import com.pennant.interfaceservice.model.AccountPostingRequest;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;

public class AccountPostingProcess {

	private static Log LOG = null;
	public AccountPostingProcess() {
		LOG = LogFactory.getLog(AccountPostingProcess.class);
	}
	public AccountPostingReply fetchDetails(AccountPostingRequest request,
			Connection connection) throws Exception {
		LOG.entering("fetchDetails()");
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		ArrayList<AccountEntries> accountEntriesList= new ArrayList<AccountEntries>();
		AccountPostingReply accposting=null;
		try{
			accposting=new AccountPostingReply();
			
			accposting.setEntryCount(8);
			accposting.setLinkTranId("89337");
			accposting.setValueDate("10/06/2015");
			accposting.setFinType("Murabaha"); 
			accposting.setPostBranchCode("B74674");
			accposting.setTimeStamp(2015100619);
			
			AccountEntries accEntries = new AccountEntries();
			accEntries.setCustId("PC107");
			accEntries.setAcCcy("AED");
			accEntries.setAccountType("L2");
			accEntries.setBranch("1010");
			accEntries.setAccount("54258795452554");
			accEntries.setInternalAc(true);
			accEntries.setTranOrder("TR28276262");
			accEntries.setTranCode("84848");
			accEntries.setRevTranCode("3373737");
			accEntries.setDrOrCr("C");
			accEntries.setShadow(true);
			accEntries.setAmount(new BigDecimal(897989));
			accEntries.setFinEvent("Test");
			accEntries.setFinReference("PF89679879");
			accEntries.setPostRef("R52353");
			
			accountEntriesList.add(accEntries);
			
			
			AccountEntries accEntries1 = new AccountEntries();
			accEntries1.setCustId("PC107");
			accEntries1.setAcCcy("AED");
			accEntries1.setAccountType("L2");
			accEntries1.setBranch("1010");
			accEntries1.setAccount("54258795452554");
			accEntries1.setInternalAc(true);
			accEntries1.setTranOrder("TR28276262");
			accEntries1.setTranCode("84848");
			accEntries1.setRevTranCode("3373737");
			accEntries1.setDrOrCr("C");
			accEntries1.setShadow(true);
			accEntries1.setAmount(new BigDecimal(897989));
			accEntries1.setFinEvent("Test");
			accEntries1.setFinReference("PF89679879");
			accEntries1.setPostRef("R342");
			
			accountEntriesList.add(accEntries1);
			
			accposting.setAccountEntries(accountEntriesList);
		}catch(Exception e){ 	
			LOG.error("fetchDetails()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw new  Exception("");
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
		LOG.exiting("fetchDetails() ");
		return accposting;
		
		
		LOG.entering("fetchAccount()");
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		ArrayList<CoreBankAccountDetail> details= new ArrayList<CoreBankAccountDetail>();
		try{
			pstmt =connection.prepareStatement("select * from dbo.AccountPostingDetails  Where  ValueDate=?,FinType=?,AcNumber=?,");
			pstmt.setString(1, );
			rs = pstmt.executeQuery();

			while(rs.next())
			{
				CoreBankAccountDetail acDetails= new CoreBankAccountDetail();
				acDetails.setAccountNumber(rs.getString("AcNumber"));
				acDetails.setAcCcy(rs.getString("AcCCy"));
				acDetails.setAcBranch(rs.getString("AcBranch"));
				acDetails.setAcCustId(rs.getLong("AcCustId"));
				acDetails.setIBAN(rs.getString("IBAN"));
				acDetails.setCIN(rs.getString("UIN"));
				acDetails.setUIN(rs.getString("CIN"));
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
		LOG.exiting("fetchAccount() "););
		return false;
	}


}
}















*/