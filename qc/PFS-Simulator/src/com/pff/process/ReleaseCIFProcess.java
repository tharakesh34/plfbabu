package com.pff.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;

public class ReleaseCIFProcess {
	
	private static Log LOG =null;
	
	public ReleaseCIFProcess() {
		LOG = LogFactory.getLog(ReleaseCIFProcess.class);
	}
	
	
	public boolean releaseCIF(String custCIF,Connection connection) throws Exception {	
		
		LOG.entering("releaseCIF()");	
		boolean isDeleted=false;
		try{
			isDeleted = deleteByCustomerCIF(custCIF, connection);	  
		}
		catch(Exception e){
			LOG.error("releaseCIF()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw e;
		}

	    LOG.exiting("releaseCIF()");
	    
		return isDeleted;			
	}

	/*Releases the Customer by using CIFNo and referenceNum */
	public boolean deleteByCustomerCIF(String cifNo,Connection connection) throws Exception {

		LOG.entering("deleteByCustomerCIF()");
		
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		int recordCount=0;
		boolean isDeleted=false;
		try{				
			pstmt =connection.prepareStatement("delete from  dbo.CustomerStatus  Where CustCIF=?");
			pstmt.setString(1, cifNo);						
			recordCount=pstmt.executeUpdate();
			if(!(recordCount==0)){
				isDeleted=true;
			}	
		}
		catch(Exception e){
			LOG.error("deleteByCustomerCIF()-->Exception"+e.getMessage());
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
		LOG.exiting("deleteByCustomerCIF()");
		
		return isDeleted;
	}
}
