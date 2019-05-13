package com.pff.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.interfaceservice.model.CoreCustomerDedup;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;

public class CustomerDedupProcess {
	
	private static Log LOG = LogFactory.getLog(CustomerDedupProcess.class);

	// Fetch the CustomerDetails by using CustCIF
	public List<CoreCustomerDedup> fetchCustDedupDetails(CoreCustomerDedup custDedup,Connection connection) throws Exception {	
		
		LOG.entering("fetchCustDedupDetails() ");	
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		List<CoreCustomerDedup> custDedupList = new ArrayList<CoreCustomerDedup>();
		try{
			pstmt =connection.prepareStatement("select * from dbo.CustomerDedupCheckDetails Where CustCIF=? OR CustCRCPR =? OR MobileNumber=? OR CustPassportNo = ?");
			pstmt.setString(1, custDedup.getCustCIF());
			pstmt.setString(2, custDedup.getCustCRCPR());
			pstmt.setString(3, StringUtils.trimToEmpty(custDedup.getMobileNumber()));
			pstmt.setString(4, StringUtils.trimToEmpty(custDedup.getCustPassportNo()));
			rs = pstmt.executeQuery();
			while(rs.next()) {
				CoreCustomerDedup coreCustomerDedup = new CoreCustomerDedup();
				coreCustomerDedup.setCustCIF(rs.getString("CustCIF"));
/*				coreCustomerDedup.setCustDOB(rs.getDate("CustDOB"));
				coreCustomerDedup.setCustFName(rs.getString("CustName"));
				coreCustomerDedup.setCustLName("");
				coreCustomerDedup.setCustCRCPR(rs.getString("CustCRCPR"));
				coreCustomerDedup.setCustPassportNo(rs.getString("CustPassportNo"));
				coreCustomerDedup.setMobileNumber(rs.getString("MobileNumber"));
				coreCustomerDedup.setCustNationality(rs.getString("Nationality"));
				coreCustomerDedup.setDedupRule(rs.getString("DedupRule"));*/
				custDedupList.add(coreCustomerDedup);
			}
		}
		catch (Exception e) {
			LOG.error("fetchCustDedupDetails()-->Exception"+e.getMessage());
			LOG.debug("Leaving");}
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
		LOG.exiting("fetchCustDedupDetails() ");
		return custDedupList;
	}
}
