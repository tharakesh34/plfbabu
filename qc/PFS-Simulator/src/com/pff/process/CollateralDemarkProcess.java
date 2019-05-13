package com.pff.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import com.pennant.interfaceservice.model.CollateralDeMarkingRequest;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;

public class CollateralDemarkProcess {

	private static Log LOG =null;
	public CollateralDemarkProcess() {
		LOG = LogFactory.getLog(CollateralDemarkProcess.class);
	}

	//DeMark a Collateral on Specific Account
	public Boolean demarkCollateralAccountDetails(CollateralDeMarkingRequest request,	Connection connection) throws Exception {
		LOG.entering("demarkCollateralAccountDetails() ");	
		boolean flag=false;
		try{

			for(int i=0;i<request.getAccountDetail().size();i++){
				boolean isFound=FetchAccount(request.getAccountDetail().get(i).getAccNum(), connection);
				if(isFound){
					boolean isUpdated=updateAccountDetails(request.getAccountDetail().get(i).getAccNum(),connection);
					if(isUpdated){
						flag=true;
					}
				}

				else{
					throw new Exception("Account Not Found Exception");
				}
			}}
		catch(Exception e){ 	
			LOG.error("demarkCollateralAccountDetails()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw e;
		}
		LOG.exiting("demarkCollateralAccountDetails() ");	
		return flag;
	}

	//Method to Update Account Details
	private boolean updateAccountDetails(String actNum,
			Connection connection) throws Exception {
		LOG.entering("updateAccountDetails()");
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		try{
			pstmt =connection.prepareStatement("update  CollateralAccountDetails set status=? where AccNum=?");
			pstmt.setString(1,"Unblocked");			
			pstmt.setString(2,actNum);
			int result=pstmt.executeUpdate();
			if(result>0){
				return true;
			}
			else{
				LOG.debug("updateAccountDetails() ");
				throw new Exception("Account Details is Not Unblocked");
			}


		}catch(Exception e){ 	
			LOG.error("updateAccountDetails()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw new  Exception("Collateral Account Details Not Found");
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
	}

	//Method to fetch Account Details
	public boolean FetchAccount(String accountNo,Connection connection) throws Exception {
		LOG.entering("fetchAccount()");
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		try{
			pstmt =connection.prepareStatement("select * from dbo.collateralAccountDetails  Where AccNum=?");
			pstmt.setString(1,accountNo );
			rs = pstmt.executeQuery();
			while(rs.next()){
				return true;
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
		return false;
	}

	//Method to fetch Deposit Details
	public boolean FetchDepositDetails(String depositId,Date blockingDate,Connection connection) throws Exception {
		LOG.entering("FetchDepositDetails()");
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		try{
			pstmt =connection.prepareStatement("select * from dbo.collateralDepositDetails  where DepositId=? and BlockingDate=? ");
			pstmt.setString(1,depositId);
			java.sql.Date sqlDate=null;
			sqlDate=new java.sql.Date(blockingDate.getTime());
			pstmt.setDate(2, sqlDate);
			rs = pstmt.executeQuery();
			while(rs.next()){
				return true;
			}

		}catch(Exception e){ 	
			LOG.error("FetchDepositDetails()-->Exception"+e.getMessage());
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
		LOG.exiting("FetchDepositDetails() ");
		return false;
	}

	//Method to Update Deposit Details
	private boolean updateDepositDetails(String  depositId,Date blockingDate,
			Connection connection) throws Exception {
		LOG.entering("updateDepositDetails()");
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		try{
			pstmt =connection.prepareStatement("update CollateralDepositDetails set Status=? where DepositId=? and BlockingDate=? ");
			pstmt.setString(1,"Unblocked");			
			pstmt.setString(2,depositId);		
			java.sql.Date sqlDate=null;
			sqlDate = new  java.sql.Date(blockingDate.getTime());
			pstmt.setDate(3,sqlDate);

			int result=pstmt.executeUpdate();
			if(result>0){
				return true;
			}
			else{
				LOG.debug("updateDepositDetails() ");
				throw new Exception("Deposit Details is Not Unblocked");
			}


		}catch(Exception e){ 	
			LOG.error("updateDepositDetails()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw new  Exception("Collateral Deposit Details Not Found");
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

	}

	//Mark a Collateral on Specific Deposit
	public Boolean demarkCollateralDepositDetails(CollateralDeMarkingRequest request, Connection connection) throws Exception {
		LOG.entering("demarkCollateralDepositDetails() ");	
		boolean flag=false;
		try{
			for(int i=0;i<request.getDepositDetail().size();i++){
				boolean isFound=FetchDepositDetails(request.getDepositDetail().get(i).getDepositID(),request.getDepositDetail().get(i).getBlockingDate(), connection);
				if(isFound){
					boolean isUpdated=updateDepositDetails(request.getDepositDetail().get(i).getDepositID(),request.getDepositDetail().get(i).getBlockingDate(),connection);
					if(isUpdated){
						flag=true;
					}
				}
				else{
					throw new Exception("Deposit Details Not Found");
				}
			}
		}
		catch(Exception e){ 	
			LOG.error("demarkCollateralDepositDetails()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw e;
		}

		LOG.exiting("demarkCollateralDepositDetails()");	
		return flag;

	}


}
