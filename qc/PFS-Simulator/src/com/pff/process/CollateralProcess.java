package com.pff.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.pennant.interfaceservice.model.CollateralMarkingRequest;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;

public class CollateralProcess {

	private static Log LOG =null;
	public CollateralProcess() {
		LOG = LogFactory.getLog(CollateralProcess.class);
	}

	//Mark a Collateral on Specific Account
	public Boolean markCollateralAccountDetails(CollateralMarkingRequest request,	Connection connection) throws Exception {
		LOG.entering("markCollateralAccountDetails() ");	

		try{			
			boolean isSaved=saveAccountDetails(request,connection);
			if(isSaved){
				return true;
			}
		}
		catch(Exception e){ 	
			LOG.error("markCollateralAccountDetails()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw e;
		}
		LOG.exiting("markCollateralAccountDetails() ");	
		return false;
	}


	//Method to save Account Details
	private boolean saveAccountDetails(CollateralMarkingRequest request,
			Connection connection) throws Exception {
		LOG.entering("saveAccountDetails()");
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		int result=0;
		try{
			for(int i=0;i<request.getAccountDetail().size();i++){
				pstmt =connection.prepareStatement("insert into CollateralAccountDetails values(?,?,?,?,?,?,?,?)");
				pstmt.setString(1,request.getReferenceNum() );			
				pstmt.setString(2,request.getAccountDetail().get(i).getAccNum());
				pstmt.setString(3,request.getAccountDetail().get(i).getDescription());
				pstmt.setBigDecimal(4,request.getAccountDetail().get(i).getInsAmount());		
				java.sql.Date sqlDate=null;
				sqlDate = new  java.sql.Date(request.getAccountDetail().get(i).getBlockingDate().getTime());
				pstmt.setDate(5,sqlDate);
				pstmt.setString(6,request.getBranchCode());
				pstmt.setDate(7, new java.sql.Date(System.currentTimeMillis()));	
				//pstmt.setString(8,request.getStatus());			
				result=pstmt.executeUpdate();
			}
			if(result>0){
				return true;
			}		
		}catch(Exception e){ 	
			LOG.error("saveAccountDetails()-->Exception"+e.getMessage());
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
		LOG.exiting("saveAccountDetails() ");
		return false;
	}	

	//Mark a Collateral on Specific Deposit
	public Boolean markCollateralDepositDetails(
			CollateralMarkingRequest depositRequest, Connection connection) throws Exception {
		LOG.entering("markCollateralDepositDetails() ");	

		try{
			boolean isSaved=saveDepositDetails(depositRequest,connection);
			if(isSaved){
				return true;
			}
		}
		catch(Exception e){ 	
			LOG.error("markCollateralDepositDetails()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw e;
		}

		LOG.exiting("markCollateralDepositDetails() ");	
		return false;

	}

	//Method to save Deposit Details
	private boolean saveDepositDetails(CollateralMarkingRequest request,
			Connection connection) throws Exception {
		LOG.entering("saveDepositDetails()");
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		//request.setStatus("Blocked");
		int rowCount=0;
		try{
			for(int i=0;i<request.getDepositDetail().size();i++){
				pstmt =connection.prepareStatement("insert into CollateralDepositDetails values(?,?,?,?,?,?,?,?)");
				pstmt.setString(1,request.getReferenceNum() );			
				pstmt.setString(2,request.getDepositDetail().get(i).getDepositID());
				pstmt.setBigDecimal(3,request.getDepositDetail().get(i).getInsAmount());		
				java.sql.Date sqlDate=null;
				sqlDate = new  java.sql.Date(request.getDepositDetail().get(i).getBlockingDate().getTime());
				pstmt.setDate(4,sqlDate);
			//	pstmt.setString(5,request.getDepositDetail().get(i).getReason());
				pstmt.setString(6,request.getBranchCode());
				pstmt.setDate(7, new java.sql.Date(System.currentTimeMillis()));	
				//pstmt.setString(8,request.getStatus());
				rowCount = pstmt.executeUpdate();
			}
			if(rowCount>0){
				return true;
			}		
		}catch(Exception e){ 	
			LOG.error("saveDepositDetails()-->Exception"+e.getMessage());
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
		LOG.exiting("saveDepositDetails() ");
		return false;
	}

}
