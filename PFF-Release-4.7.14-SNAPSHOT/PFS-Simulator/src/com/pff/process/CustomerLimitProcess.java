package com.pff.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.pennant.interfaceservice.model.CustomerLimitDetailReply;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;
import com.pff.framework.util.DateUtility;

public class CustomerLimitProcess {
	
	private static Log LOG =null;
	
	public CustomerLimitProcess() {
		LOG = LogFactory.getLog(CustomerLimitProcess.class);
	}
	
	/**
	 * This method is used to fetch the customer limit details by using LimitReference 
	 * @return CustomerLimitDetailReply
	 */
	public CustomerLimitDetailReply fetchCustomerLimitDetails(String limitRef,
			Connection connection) throws Exception {
		
		LOG.entering("fetchCustomerLimitDetails() ");
		
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		CustomerLimitDetailReply custLimitDetails=new CustomerLimitDetailReply();;
		try{
			pstmt =connection.prepareStatement("select * from dbo.CustomerLimitDetails  Where LimitReference=? ");
			pstmt.setString(1,limitRef );
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				custLimitDetails= new CustomerLimitDetailReply();
				custLimitDetails.setReferenceNumber(rs.getString(1));	
				custLimitDetails.setLimitRef(limitRef);
				custLimitDetails.setTenor(rs.getInt(3));
				custLimitDetails.setTenorUnit(rs.getString(4));
				custLimitDetails.setRepricingFrequency(rs.getString(5));
				custLimitDetails.setSpread(rs.getBigDecimal(6));
				custLimitDetails.setMinimumPrice(rs.getBigDecimal(7));
				custLimitDetails.setPricingSchema(rs.getString(8));
				custLimitDetails.setCommissionPercent(rs.getBigDecimal(9));
				custLimitDetails.setCommissionAmount(rs.getBigDecimal(10));
				custLimitDetails.setCommissionFreq(rs.getString(11));
				custLimitDetails.setStudyFee(rs.getString(12));
				custLimitDetails.setHamJad((rs.getBigDecimal(13)));
				custLimitDetails.setOtherFeePercent(rs.getBigDecimal(14));
				custLimitDetails.setOtherFeeAmount(rs.getBigDecimal(15));
				custLimitDetails.setCovenant(rs.getString(16));
				custLimitDetails.setTermsConditions(rs.getString(17));
				custLimitDetails.setNotes(rs.getString(18));		
				custLimitDetails.setCustCIF(rs.getString(19));	
				custLimitDetails.setLimitDesc(rs.getString(20));
				custLimitDetails.setLimitExpiryDate(DateUtility.getUtilDate("2015-10-15", "yyyy-MM-dd"));;
				custLimitDetails.setRevolvingType(rs.getString(22));
				custLimitDetails.setLimitCcy(rs.getString(23));
				custLimitDetails.setApprovedLimit(rs.getBigDecimal(24));
				custLimitDetails.setApprovedLimitCcy(rs.getString(25));
				custLimitDetails.setOutstandingAmt(rs.getBigDecimal(26));
				custLimitDetails.setOutstandingAmtCcy(rs.getString(27));
				custLimitDetails.setBlockedAmt(rs.getBigDecimal(28));
				custLimitDetails.setBlockedAmtCcy(rs.getString(29));
				custLimitDetails.setReservedAmt(rs.getBigDecimal(30));
				custLimitDetails.setReservedAmtCcy(rs.getString(31));
				custLimitDetails.setAvailableAmt(rs.getBigDecimal(32));
				custLimitDetails.setAvailableAmtCcy(rs.getString(33));
				custLimitDetails.setMaximumPricing(rs.getBigDecimal(34));
				custLimitDetails.setMargin(rs.getBigDecimal(35));
				custLimitDetails.setRepaymentTerm(rs.getString(36));
				custLimitDetails.setLimitAvailabilityPeriod(rs.getString(37));
				custLimitDetails.setFinalMaturityDate(DateUtility.getUtilDate(rs.getString(38), "dd-MM-yyyy"));
				custLimitDetails.setTimeStamp(String.valueOf(System.currentTimeMillis()));
				//custLimitDetails.setPricingIndex(2);
			}
		}catch(Exception e){
			LOG.error("fetchCustomerLimitDetails()-->Exception"+e.getMessage());
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
		LOG.exiting("fetchCustomerLimitDetails() ");
		return custLimitDetails;
	}
}
