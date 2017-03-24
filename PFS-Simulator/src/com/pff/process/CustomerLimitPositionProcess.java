package com.pff.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.pennant.interfaceservice.model.CustomerLimitPositionReply;
import com.pennant.interfaceservice.model.CustomerLimitSummary;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;

public class CustomerLimitPositionProcess {
	private static Log LOG = null;
	
	public CustomerLimitPositionProcess() {
		LOG = LogFactory.getLog(CustomerLimitPositionProcess.class);
	}
	
	//Fetch the CustomerLimitPostion Details based on CustomerReference
	public CustomerLimitPositionReply fetchCustomerLimitPositionDetails(String custRef, Connection connection) throws Exception {

		LOG.entering("CustomerLimitPositionReply()");
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		ArrayList<CustomerLimitSummary> details= new ArrayList<CustomerLimitSummary>();
		CustomerLimitPositionReply custReply=null;
		try{
			pstmt =connection.prepareStatement("select * from dbo.CustomerLimitPositionDetails Where CustRef=?");
			pstmt.setString(1,custRef);
			rs = pstmt.executeQuery();

			while(rs.next())
			{
				//CustomerLimitPosition
				custReply=new CustomerLimitPositionReply();
				custReply.setCustRef(custRef);
				custReply.setGroupRef(rs.getString("GroupRef"));

				//CustomerLimitSummary
				CustomerLimitSummary custLimSummaryDetails= new CustomerLimitSummary();
				custLimSummaryDetails.setLimitReference(rs.getString("LimitReference"));
				custLimSummaryDetails.setLimitDesc(rs.getString("LimitDesc"));;
				custLimSummaryDetails.setControllerUnder(rs.getString("ControllerUnder"));
				custLimSummaryDetails.setRev_Nrev(rs.getString("Rev_Nrev"));
				custLimSummaryDetails.setAppovedAmount(rs.getBigDecimal("AppovedAmount"));
				custLimSummaryDetails.setAppovedAmountCcy(rs.getString("AppovedAmountCcy"));
				custLimSummaryDetails.setOutstanding(rs.getBigDecimal("Outstanding"));
				custLimSummaryDetails.setOutstandingCcy(rs.getString("OutstandingCcy"));
				custLimSummaryDetails.setAvailable(rs.getBigDecimal("Available"));
				custLimSummaryDetails.setAvailableCcy(rs.getString("AvailableCcy"));
				custLimSummaryDetails.setReserved(rs.getBigDecimal("Reserved"));
				custLimSummaryDetails.setReservedCcy(rs.getString("ReservedCcy"));
				custLimSummaryDetails.setBlocked(rs.getBigDecimal("Blocked"));
				custLimSummaryDetails.setBlockedCcy(rs.getString("BlockedCcy"));
				custLimSummaryDetails.setLimitCurrency(rs.getString("LimitCurrency"));				
				details.add(custLimSummaryDetails);
				custReply.setLimitSummary(details);
			}
		}catch(Exception e){ 	
			LOG.error("CustomerLimitPositionReply()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw new  Exception("Customer Summary is Not Found");
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
		LOG.exiting("CustomerLimitPositionReply() ");
		return custReply;
	}

}
