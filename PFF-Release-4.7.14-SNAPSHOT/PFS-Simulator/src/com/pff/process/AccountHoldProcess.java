package com.pff.process;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.pennant.interfaceservice.model.AddHoldRequest;
import com.pennant.interfaceservice.model.RemoveHoldRequest;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;

public class AccountHoldProcess {


private static Log LOG =null;;

public AccountHoldProcess() {
	LOG = LogFactory.getLog(AccountHoldProcess.class);
}

/**
 * Add holds on a specific account
 * 
 */
public boolean blockAccount(AddHoldRequest request, Connection connection) throws SQLException {
	LOG.entering("blockAccount()");
	ResultSet rs = null;
	PreparedStatement pstmt=null;
	int count=0;
	try{
		for(int i=0;i<request.getAccountDetail().size();i++){
		pstmt =connection.prepareStatement("insert into AccountHoldDetails values(?,?,?,?,?,?,?)");
        pstmt.setString(1, request.getReferenceNum());
        pstmt.setString(2, request.getAccountDetail().get(i).getAccNum());
        pstmt.setString(3, request.getAccountDetail().get(i).getDescription());
        pstmt.setBigDecimal(4, request.getAccountDetail().get(i).getInsAmount());      
        java.sql.Date sqlDate=null;
		sqlDate = new  java.sql.Date(request.getAccountDetail().get(i).getBlockingDate().getTime());
        pstmt.setDate(5,sqlDate);
        pstmt.setString(6, request.getBranchCode());
        pstmt.setDate(7, new Date(System.currentTimeMillis()));
       	count = pstmt.executeUpdate();
		}
       	if(count>0){
       		return true;
       	}

	}catch(Exception e){ 	
		LOG.error("blockAccount()-->Exception"+e.getMessage());
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
	LOG.exiting("blockAccount() ");
	return false;
}

/**
 * Deletes the specific hold on the account
 * 
 */
public boolean unblockAccount(RemoveHoldRequest request,Connection connection) throws SQLException {
	
	LOG.entering("unblockAccount()");
	ResultSet rs = null;
	PreparedStatement pstmt=null;
	int count=0;
	try{
		for(int i=0;i<request.getAccountDetail().size();i++){
		pstmt =connection.prepareStatement("Delete From AccountHoldDetails where AccNum=?");
		pstmt.setString(1,request.getAccountDetail().get(0).getAccNum());
		count = pstmt.executeUpdate();
		}
		if(count>0){
			return true;
		}

	}catch(Exception e){ 	
		LOG.error("unblockAccount()-->Exception"+e.getMessage());
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
	LOG.exiting("unblockAccount() ");
	return false;
}

}