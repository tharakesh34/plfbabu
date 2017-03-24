package com.pff.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.pennant.interfaceservice.model.InterfaceCustomer;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;

public class ReserveCIFProcess {
	
	private static Log LOG = null;
	
	public ReserveCIFProcess() {
		LOG = LogFactory.getLog(ReserveCIFProcess.class);
	}
	
	public boolean reserveCIF(InterfaceCustomer customerDetails,Connection connection) throws Exception {
		
		LOG.entering("reserveCIF()");
		
		boolean isExits=getCustomerByCIF(customerDetails,connection);
		
		try{
			if(isExits){
				throw new Exception("Customer Already Exists");
			}
			else
			{
				boolean isSaved = saveCustomer(customerDetails, connection);
				LOG.exiting("reserveCIF()");
				return  isSaved;
			}	}
		catch(Exception e){
			LOG.error("reserveCIF()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw e;
		}
	}
	
	//Get the customer by using CIF to verify that customer is already exists or not
	public boolean getCustomerByCIF(InterfaceCustomer customerDetails ,Connection connection) throws Exception {
		
		LOG.entering("getCustomerByCIF()");
		
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		try{		
			pstmt =connection.prepareStatement("Select * from  dbo.CustomerStatus  Where CustCIF=?");
			pstmt.setString(1, customerDetails.getCustCIF());		
			rs = pstmt.executeQuery();
			if(rs.next())	{
				return true;
			}
		}
		catch(Exception e){
			LOG.error("getCustomerByCIF()-->Exception"+e.getMessage());
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
		LOG.exiting("getCustomerByCIF()");
		
		return false;

	}
	
	//Reserves the Customer based on refNo and CIFNo
	public boolean saveCustomer(InterfaceCustomer customer, Connection connection) throws Exception {

		LOG.entering("saveCustomer()");

		PreparedStatement pstmt=null;
		int recordCount ;
		boolean insertFlag=false;
		try{		
			pstmt =connection.prepareStatement("insert into CustomerStatus values(?,?,?,? )");
			pstmt.setString(1, customer.getCustCIF());
			pstmt.setString(2, customer.getCustCIF());
			pstmt.setString(3, customer.getCustFName());
			pstmt.setString(4, customer.getCustDftBranch());
			recordCount = pstmt.executeUpdate();
			if(!(recordCount==0)){
				insertFlag=true;
			}
		}
		catch(Exception e){
			LOG.error("saveCustomer()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw e;
		}finally{
			if(pstmt!=null){
				pstmt.close();
				pstmt=null;
			}
		}  
		LOG.exiting("saveCustomer()");
		return insertFlag;
	}
}
