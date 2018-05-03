package com.pff.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import com.pennant.interfaceservice.model.InterfaceCustEmployeeDetail;
import com.pennant.interfaceservice.model.InterfaceCustomer;
import com.pennant.interfaceservice.model.InterfaceCustomerAddress;
import com.pennant.interfaceservice.model.InterfaceCustomerDetail;
import com.pennant.interfaceservice.model.InterfaceCustomerDocument;
import com.pennant.interfaceservice.model.InterfaceCustomerEMail;
import com.pennant.interfaceservice.model.InterfaceCustomerPhoneNumber;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;

public class CustomerProcess {
	
	private static Log LOG = null;
	
	public CustomerProcess() {
		LOG = LogFactory.getLog(CustomerProcess.class);
	}
	
	public InterfaceCustomerDetail fetchCustDetails(String cifNo,Connection connection) throws Exception {	

		LOG.entering("fetchCustDetails() ");
		InterfaceCustomerDetail detail=new InterfaceCustomerDetail();
		try
		{
			String custCtgType = getCustomerType(cifNo, connection);
			long custID=getCustId(cifNo,connection,custCtgType);
			detail.setCustCIF(cifNo);
			detail.setCustomer(detailsFetch(custID, connection,custCtgType));
			detail.setAddressList(fetchAddressDetails(custID, connection));
			detail.setCustomerPhoneNumList(fetchPhoneDetails(custID, connection));
			detail.setCustomerEMailList(fetchEmailDetails(custID, connection));
			detail.setCustEmployeeDetail(fetchEmploymeeDetails(custID, connection));
			detail.setCustomerDocumentsList(fetchDocumentDetails(custID, connection));
		}
		catch (Exception e) {
			LOG.error("fetchCustDetails()-->Exception"+e.getMessage());
			LOG.debug("Leaving");;
			throw e;
		}
		return detail;
	}


	//Fetch the customer Details by using custID and CustCtgCode

	private InterfaceCustomer detailsFetch(long custID,Connection connection, String custType) throws Exception {
		LOG.entering("detailsFetch() ");
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		InterfaceCustomer customer= null;
		String custCtgCode=null;
		if (StringUtils.equalsIgnoreCase(custType, "RETAIL")) {
			custCtgCode = "Retail";
		} else {
			custCtgCode = "SME";
		}

		try{
			customer= new InterfaceCustomer();
			customer.setCustCtgCode(custCtgCode);
			pstmt =connection.prepareStatement("select * from dbo.Customers Where CustID=? and CustCtgCode=?");
			pstmt.setLong(1, custID);
			pstmt.setString(2,custCtgCode );
			customer.setCustCtgCode(custCtgCode);
			rs = pstmt.executeQuery();
			if(rs.next())
			{
				customer.setCustID(custID);
				customer.setCustFName(rs.getString("custFName"));
				customer.setCustMName(rs.getString("CustMName"));
				customer.setCustLName(rs.getString("CustLName"));
				customer.setCustMotherMaiden(rs.getString("CustMotherMaiden"));
				customer.setCustSalutationCode(rs.getString("CustSalutationCode"));
				customer.setCustCoreBank(rs.getString("CustCoreBank"));
				customer.setCustLng(rs.getString("CustLng"));
				customer.setCustRO1(rs.getString("CustRO1"));
				customer.setCustCtgCode(rs.getString("CustCtgCode"));
				customer.setCustTypeCode(rs.getString("CustTypeCode"));
				customer.setCustShrtName(rs.getString("CustShrtName"));
				customer.setCustDOB(rs.getDate("CustDOB"));
				customer.setCustLng(rs.getString("CustLng"));
				customer.setCustSector(rs.getString("CustSector"));
				customer.setCustIndustry(rs.getString("CustIndustry"));
				customer.setCustSegment(rs.getString("CustSegment"));
				customer.setCustGenderCode(rs.getString("CustGenderCode"));
				customer.setCustNationality(rs.getString("CustNationality"));
				customer.setCustCOB(rs.getString("CustCOB"));
				customer.setCustMaritalSts(rs.getString("CustMaritalSts"));
				customer.setCustDftBranch(rs.getString("CustDftBranch"));
				customer.setCustTradeLicenceExpiry(rs.getDate("CustTradeLicenceExpiry"));
				customer.setCustPassportNo(rs.getString("CustPassportNo"));
				customer.setCustVisaNum(rs.getString("CustVisaNum"));				
				customer.setCustEmpSts(rs.getString("CustEmpSts"));
				customer.setCustResdCountry(rs.getString("CustResdCountry"));
				customer.setCustReferedBy(rs.getString("CustReferedBy"));
				customer.setCustAddlVar82(rs.getString("CustAddlVar82"));
			}
			else
			{
				throw new Exception("Customer Not Found");
			}
		} 
		catch(Exception e){
			LOG.error("detailsFetch()-->Exception "+e.getMessage());
			LOG.debug("Leaving");;
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
		LOG.exiting("detailsFetch() ");
		return customer;

	}

	//Fetch the Customer Addresses Details by using CustID
	public ArrayList<InterfaceCustomerAddress> fetchAddressDetails(long custID,Connection connection) throws Exception {
		LOG.entering("fetchAddressDetails() ");
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		ArrayList<InterfaceCustomerAddress> addresses= new ArrayList<InterfaceCustomerAddress>();

		try{
			pstmt =connection.prepareStatement("select * from dbo.CustomerAddresses  Where CustID=?");
			pstmt.setLong(1, custID);
			rs = pstmt.executeQuery();
     
			while(rs.next())
			{
				InterfaceCustomerAddress address= new InterfaceCustomerAddress();
				address.setCustID(rs.getLong("CustID"));
				address.setCustAddrType(rs.getString("CustAddrType"));
				address.setCustAddrHNbr(rs.getString("CustAddrHNbr"));
				address.setCustFlatNbr(rs.getString("CustFlatNbr"));
				address.setCustPOBox(rs.getString("CustPOBox"));
				address.setCustAddrStreet(rs.getString("CustAddrStreet"));
				address.setCustAddrCountry(rs.getString("CustAddrCountry"));
				address.setCustAddrLine1(rs.getString("CustAddrLine1"));			
				address.setCustAddrProvince(rs.getString("CustAddrProvince"));			
				addresses.add(address);
			}
		}catch(Exception e){ 
			LOG.error("fetchAddressDetails()-->Exception "+e.getMessage());
			LOG.debug("Leaving");

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
		LOG.exiting("fetchAddressDetails() ");
		return addresses;

	}

	//Fetch the Customer Phone Details by the key CustID
	public ArrayList<InterfaceCustomerPhoneNumber> fetchPhoneDetails(long custID,Connection connection) throws Exception {
		LOG.entering("fetchPhoneDetails() ");
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		ArrayList<InterfaceCustomerPhoneNumber> phoneNumbers= new ArrayList<InterfaceCustomerPhoneNumber>();

		try{
			pstmt =connection.prepareStatement("select * from dbo.CustomerPhoneNumbers  Where PhoneCustID=?");
			pstmt.setLong(1, custID);
			rs = pstmt.executeQuery();

			while(rs.next())
			{
				InterfaceCustomerPhoneNumber custPhoneNo= new InterfaceCustomerPhoneNumber();
				custPhoneNo.setPhoneCustID(rs.getLong("PhoneCustID"));
				custPhoneNo.setPhoneTypeCode(rs.getString("PhoneTypeCode"));
				custPhoneNo.setPhoneCountryCode(rs.getString("PhoneCountryCode"));
				custPhoneNo.setPhoneAreaCode(rs.getString("PhoneAreaCode"));
				custPhoneNo.setPhoneNumber(rs.getString("PhoneNumber"));
				phoneNumbers.add(custPhoneNo);
			}
		}catch(Exception e){ 
			LOG.error("fetchPhoneDetails()-->Exception"+e.getMessage());
			LOG.debug("Leaving");

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
		LOG.exiting("fetchPhoneDetails() ");
		return phoneNumbers;

	}

	
	// Fetch the Customer Email Details by the key CustID
	public ArrayList<InterfaceCustomerEMail> fetchEmailDetails(long custID,Connection connection) throws Exception {
		LOG.entering("fetchEmailDetails() ");
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		ArrayList<InterfaceCustomerEMail> emailDetails= new ArrayList<InterfaceCustomerEMail>();

		try{
			pstmt =connection.prepareStatement("select * from dbo.CustomerEmails  Where CustID=?");
			pstmt.setLong(1, custID);
			rs = pstmt.executeQuery();

			while(rs.next())
			{
				InterfaceCustomerEMail custEmail= new InterfaceCustomerEMail();
				custEmail.setCustID(rs.getLong("CustID"));
				custEmail.setCustEMailTypeCode(rs.getString("CustEMailTypeCode"));
				custEmail.setCustEMail(rs.getString("CustEMail"));
				custEmail.setCustEMailPriority(rs.getInt("CustEMailPriority"));	
				emailDetails.add(custEmail);
			}
		}catch(Exception e){ 
			LOG.error("fetchEmailDetails()-->Exception"+e.getMessage());
			LOG.debug("Leaving");

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
		LOG.exiting("fetchEmailDetails() ");
		return emailDetails;

	}
	
	// Fetch the Customer Document Details by the key CustID
	public ArrayList<InterfaceCustomerDocument> fetchDocumentDetails(long custID,Connection connection) throws Exception {
		LOG.entering("fetchDocumentDetails() ");
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		ArrayList<InterfaceCustomerDocument> docDetails= new ArrayList<InterfaceCustomerDocument>();
		
		try{
			pstmt =connection.prepareStatement("select * from dbo.CustomerDocumentDetails  Where CustID=?");
			pstmt.setLong(1, custID);
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				InterfaceCustomerDocument custDoc= new InterfaceCustomerDocument();
				custDoc.setCustID(rs.getLong("CustID"));
				custDoc.setCustDocType(rs.getString("CustDocType"));
				custDoc.setCustDocTitle(rs.getString("CustDocNumber"));
				custDoc.setCustDocIssuedOn(rs.getDate("CustDocIssuedOn"));
				custDoc.setCustDocExpDate(rs.getDate("CustDocExpDate"));
				docDetails.add(custDoc);
			}
		}catch(Exception e){ 
			LOG.error("fetchDocumentDetails()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			
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
		LOG.exiting("fetchDocumentDetails() ");
		return docDetails;
		
	}
	
	// Fetch the Customer Employment Details by the key CustID
	public InterfaceCustEmployeeDetail fetchEmploymeeDetails(long custID,Connection connection) throws Exception {
		LOG.entering("fetchEmploymeeDetails() ");
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		InterfaceCustEmployeeDetail custEmpDetails= new InterfaceCustEmployeeDetail();
		try{
			pstmt =connection.prepareStatement("SELECT * from CustomerEmpDetails  Where CustID=?");
			pstmt.setLong(1, custID);
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				custEmpDetails.setCustID(custID);
				custEmpDetails.setEmpStatus(rs.getString("EmpStatus"));
				//FIXME :
				//custEmpDetails.setEmpName(rs.getString("EmpName"));
				custEmpDetails.setEmpDesg(rs.getString("EmpDesg"));
				custEmpDetails.setEmpDept(rs.getString("EmpDept"));
				custEmpDetails.setEmpFrom(rs.getDate("EmpFrom"));
				custEmpDetails.setMonthlyIncome(rs.getBigDecimal("MonthlyIncome"));
				custEmpDetails.setEmpSector(rs.getString("NatureOfBusiness"));
			}
		}catch(Exception e){ 
			LOG.error("fetchEmploymeeDetails()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			
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
		LOG.exiting("fetchEmploymeeDetails() ");
		return custEmpDetails;
		
	}


	//This Method is used to get the custId from Customer table by using CustCIF and CustCtgCode
	private long getCustId(String cifNo, Connection connection, String type) throws Exception {
		
		LOG.entering("getCustId() ");
		
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		long customerId=0;
		String custType=null;
		if(StringUtils.equalsIgnoreCase(type, "RETAIL")){
			custType="Retail";
		} else{
			custType="SME";
		}
		try{
			pstmt =connection.prepareStatement("select CustID from dbo.Customers  Where CustCIF=? and CustCtgCode=?");
			pstmt.setString(1, cifNo);
			pstmt.setString(2, custType);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				customerId=rs.getLong("CustID");
			}

		}catch(Exception e){
			LOG.error("getCustId()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
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
		
		LOG.exiting("getCustId() ");
		return customerId;
	}
	
	private String getCustomerType(String custCIF, Connection connection) throws SQLException {
		LOG.entering("getCustomerType()");
		
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		try{
			pstmt =connection.prepareStatement("SELECT CustCtgCode from Customers  Where CustID=?");
			pstmt.setString(1, custCIF);
			rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				return rs.getString("CustCtgCode");
			}
		}catch(Exception e){ 
			LOG.error("getCustomerType()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			
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
		LOG.exiting("getCustomerType() ");
		return null;
	}

}
