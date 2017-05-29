package com.pff.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.pennant.interfaceservice.model.DDAAmendmentRequest;
import com.pennant.interfaceservice.model.DDARequest;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;

public class DDAProcess {

	private static Log LOG =null;;

	public DDAProcess() {
		LOG = LogFactory.getLog(DDAProcess.class);
	}

	/**
	 * Save the Direct Debit Authority request details for the finance repayment
	 * 
	 */
	public boolean saveUAEDDADetails(DDARequest request, Connection connection) throws SQLException {
		LOG.entering("saveUAEDDADetails()");
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		int count=0;
		try{
			pstmt =connection.prepareStatement("insert into UAEDDSDetails values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			pstmt.setString(1,request.getReferenceNum() );
			pstmt.setString(2,request.getCustCIF());		
			pstmt.setString(3,request.getIdNum());
			pstmt.setString(4,request.getIdType());
			pstmt.setString(5,request.getCustomerType());
			pstmt.setString(6,request.getCustomerName());
			pstmt.setString(7,request.getBankName());
			pstmt.setString(8,request.getAccountType());
			pstmt.setString(9,request.getIban());
			pstmt.setString(10,request.getMobileNum());
			pstmt.setString(11,request.getEmailID());
			pstmt.setString(12,request.getFinRefence());			
			java.sql.Date sqlDate=null;
			sqlDate = new  java.sql.Date(request.getCommenceOn().getTime());
			pstmt.setDate(13,sqlDate);
			pstmt.setInt(14,request.getAllowedInstances());
			pstmt.setBigDecimal(15,request.getMaxAmount());
			pstmt.setString(16,request.getCurrencyCode());
			pstmt.setString(17,request.getPaymentFreq());
			pstmt.setTimestamp(18, new Timestamp(System.currentTimeMillis()));;
			pstmt.setTimestamp(19, new Timestamp(0));

			count = pstmt.executeUpdate();
			if(count>0){
				return true;
			}

		}catch(Exception e){ 	
			LOG.error("saveUAEDDADetails()-->Exception"+e.getMessage());
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
		LOG.exiting("saveUAEDDADetails() ");
		return false;
	}

	/**
	 * Updates the Direct Debit Authority request details whenever a schedule changes
	 * 
	 */
	public boolean updateDDAAmendmentDetails(DDAAmendmentRequest request,
			Connection connection) throws SQLException {

		LOG.entering("updateDDAAmendmentDetails()");
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		int count=0;
		try{
			pstmt =connection.prepareStatement("Update UAEDDSDetails set MobileNo=?,EmailID=?,FinRef=?,CommenceOn=?,ExpiresOn=?,MaxAmount=?,PaymentFreq=?,TimeStamp=? where ReferenceNum='"+request.getDDAReferenceNo()+"'");
			pstmt.setString(1,request.getMobileNum());
			pstmt.setString(2,request.getEmailID());
			pstmt.setString(3,request.getFinRef());			
			java.sql.Date sqlDate=null;
			sqlDate = new  java.sql.Date(request.getCommenceOn().getTime());
			pstmt.setDate(4,sqlDate);
			java.sql.Date sqlDate1=null;
			sqlDate = new  java.sql.Date(request.getExpiresOn().getTime());
			pstmt.setDate(5,sqlDate1);
			pstmt.setBigDecimal(6,request.getMaxAmount());
			pstmt.setString(7,request.getPaymentFreq());
			pstmt.setTimestamp(8, new Timestamp(System.currentTimeMillis()));;

			count = pstmt.executeUpdate();
			if(count>0){
				return true;
			}

		}catch(Exception e){ 	
			LOG.error("updateDDAAmendmentDetails()-->Exception"+e.getMessage());
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
		LOG.exiting("updateDDAAmendmentDetails() ");
		return false;
	}

}
