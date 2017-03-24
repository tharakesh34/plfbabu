package com.pff.process;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;

import com.pennant.interfaceservice.model.InterfaceCustomer;
import com.pennant.interfaceservice.model.InterfaceCustomerAddress;
import com.pennant.interfaceservice.model.InterfaceCustomerDetail;
import com.pennant.interfaceservice.model.InterfaceCustomerDocument;
import com.pennant.interfaceservice.model.InterfaceCustomerEMail;
import com.pennant.interfaceservice.model.InterfaceCustomerPhoneNumber;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;

public class CreateCIFProcess {

	private static Log LOG = LogFactory.getLog(CreateCIFProcess.class);

	public boolean createCustomer(InterfaceCustomerDetail custDetails,Connection connection) throws Exception {

		LOG.entering("Entering createCustomer()");
		try
		{
			long CustID=getNextCustID(connection);
			custDetails.getCustomer().setCustID(CustID);
			custDetails.getCustomer().setCustCIF(custDetails.getCustCIF());
			saveCustomer(custDetails.getCustomer(),connection,false,CustID);
			saveDocumentDetails(custDetails.getCustomerDocumentsList(),CustID,connection);
			saveCustomerPhoneNumbers(custDetails.getCustomerPhoneNumList(),connection,CustID);
			saveAddresses(custDetails.getAddressList(),connection,CustID);
			saveCustomerEmailList(custDetails.getCustomerEMailList(),connection,CustID);
		}
		catch(Exception e){			
			LOG.error("createCustomer()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw e;
		}		
		LOG.debug("Leaving createCustomer()");
		return true;
	}



	/* Save the customer Emails */
	private void saveCustomerEmailList(List<InterfaceCustomerEMail> customerEMailList,Connection connection,long CustID) throws Exception {

		LOG.entering("saveCustomerEmailList() ");

		ResultSet rs = null;
		PreparedStatement pstmt=null;
		try{
			for(int i=0;i<customerEMailList.size();i++)
			{
				pstmt =connection.prepareStatement("insert into CustomerEmails Values(?,?,?,?)");
				pstmt.setLong(1, CustID);
				pstmt.setString(2, customerEMailList.get(i).getCustEMailTypeCode());
				pstmt.setInt(3, customerEMailList.get(i).getCustEMailPriority());
				pstmt.setString(4, customerEMailList.get(i).getCustEMail());

				pstmt.execute();
			}

		}catch(Exception e){
			LOG.error("saveCustomerEmailList()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw new Exception("Customer Not Found");
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

		LOG.exiting("saveCustomerEmailList() ");
	}

	//save the Customer PhoneNumbers
	private void saveCustomerPhoneNumbers(List<InterfaceCustomerPhoneNumber> customerPhoneNumList,Connection connection,long CustID) throws Exception {

		LOG.entering("saveCustomerPhoneNumbersList() ");
		ResultSet rs = null;
		PreparedStatement pstmt=null;

		try{
			for(int i=0;i<customerPhoneNumList.size();i++)
			{
				pstmt =connection.prepareStatement("insert into CustomerPhoneNumbers Values(?,?,?,?,?)");
				pstmt.setLong(1,CustID);
				pstmt.setString(2, customerPhoneNumList.get(i).getPhoneTypeCode());
				pstmt.setString(3, customerPhoneNumList.get(i).getPhoneCountryCode());
				pstmt.setString(4, customerPhoneNumList.get(i).getPhoneAreaCode());
				pstmt.setString(5, customerPhoneNumList.get(i).getPhoneNumber());

				pstmt.execute();
			}

		}catch(Exception e){
			LOG.error("saveCustomerPhoneNumbersList()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw new Exception("Customer Not Found");
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

		LOG.exiting("saveCustomerPhoneNumbersList() ");
	}

	// Save the Customer Document Details by the key CustID
	public void saveDocumentDetails(List<InterfaceCustomerDocument> customerDocumentList,long custID,Connection connection) throws Exception {
		LOG.entering("saveDocumentDetails() ");
		ResultSet rs = null;
		PreparedStatement pstmt=null;

		try{
			for(int i=0;i<customerDocumentList.size();i++)
			{

				pstmt =connection.prepareStatement("insert into dbo.CustomerDocumentDetails Values(?,?,?,?,?) ");
				pstmt.setLong(1, custID);
				pstmt.setString(2, customerDocumentList.get(i).getCustDocType());
				pstmt.setString(3,customerDocumentList.get(i).getCustDocTitle());

				java.util.Date issuedOn = customerDocumentList.get(i).getCustDocIssuedOn();
				java.sql.Time sqlDate=null;
				if(!(issuedOn==null))
				{
					sqlDate = new  java.sql.Time(issuedOn.getTime());
				}
				pstmt.setTime(4, sqlDate);


				java.util.Date expDt = customerDocumentList.get(i).getCustDocIssuedOn();
				java.sql.Time sqlDate1=null;
				if(!(expDt==null))
				{
					sqlDate1 = new  java.sql.Time(expDt.getTime());
				}

				pstmt.setTime(5, sqlDate1);
				pstmt.execute();
			}

		}catch(Exception e){ 
			LOG.error("saveDocumentDetails()-->Exception"+e.getMessage());
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
		LOG.exiting("saveDocumentDetails() ");

	}

	//save the Customer Addresses
	public void saveAddresses(List<InterfaceCustomerAddress> addressList,Connection connection,long CustID) throws Exception {

		LOG.entering("saveAddresList() ");
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		try{
			for(int i=0;i<addressList.size();i++)
			{
				pstmt =connection.prepareStatement("insert into customerAddresses Values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				pstmt.setLong(1,CustID);
				pstmt.setString(2,addressList.get(i).getCustAddrType());
				pstmt.setString(3,addressList.get(i).getCustAddrHNbr());
				pstmt.setString(4,addressList.get(i).getCustFlatNbr());
				pstmt.setString(5,addressList.get(i).getCustAddrStreet());
				pstmt.setString(6,addressList.get(i).getCustAddrLine1());
				pstmt.setString(7,"");
				pstmt.setString(8,addressList.get(i).getCustPOBox());
				pstmt.setString(9,addressList.get(i).getCustAddrCity());
				pstmt.setString(10,addressList.get(i).getCustAddrProvince());
				pstmt.setString(11,addressList.get(i).getCustAddrCountry());
				pstmt.setString(12,addressList.get(i).getCustAddrZIP());
				pstmt.setString(13,addressList.get(i).getCustAddrPhone());
				pstmt.setTimestamp(14,addressList.get(i).getCustAddrFrom());
				pstmt.execute();
			}

		}catch(Exception e){
			LOG.error("saveAddresses()-->Exception"+e.getMessage());
			LOG.debug("Leaving ");
			throw new Exception("Customer Not Found");
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
		LOG.exiting("saveAddresList()");
	}


	/* This Method is to get new CustomerID based on already existed records count in Customers Table
	if no record is found in Customers table then CustomerID is intialized to 0 */ 
	private long getNextCustID(Connection connection) throws Exception {

		LOG.entering("getCustID() ");

		ResultSet rs = null;
		PreparedStatement pstmt=null;
		long customerId=0;
		try{
			pstmt =connection.prepareStatement("select max(CustID)+1 from dbo.customers");
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				customerId=rs.getLong(1);
			}

		}catch(Exception e){
			return customerId;
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

		LOG.exiting("getCustID()");
		return customerId;
	}

//This Method is to get Customer ID
	private long getCustomerID(Connection connection,String CustCIF) throws Exception {

		LOG.entering("getCustID() ");

		ResultSet rs = null;
		PreparedStatement pstmt=null;
		long customerId=0;
		try{
			pstmt =connection.prepareStatement("select CustID from dbo.customers where CustCIF=?");
			pstmt.setString(1,CustCIF);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				customerId=rs.getLong(1);
			}

		}catch(Exception e){
			return customerId;
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

		LOG.exiting("getCustID()");
		return customerId;
	}

	//Save the Customer Details
	private String saveCustomer(InterfaceCustomer customer,Connection connection,boolean type,long CustID) throws Exception {

		LOG.entering("saveCustomer()");

		ResultSet rs = null;
		String fieldValue = "";
		int    intValue;
		double doubleValue;
		long longValue;
		Timestamp tsValue=null;
		boolean booleanValue=false;
		BigDecimal bigDecimalValue;

		PreparedStatement pstmt=null;

		pstmt =connection.prepareStatement("insert into customers Values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
				+"?,?,?,?,?,?,?,?,?,?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
				+"?,?,?,?,?,?,?,?,?,?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
				+"?,?,?,?,?,?,?,?,?)");
		int count=0;
		try{
			Field[] fields  =  customer.getClass().getDeclaredFields();	

			for (Field field : fields) 
			{
				String fieldMethod = "get"+ field.getName().substring(0, 1).toUpperCase()+ field.getName().substring(1);
				count=count+1;
				if(field.getType().isAssignableFrom(String.class)){;
				fieldValue = (String) customer.getClass().getMethod(fieldMethod).invoke(customer);
				if(fieldValue==null)
				{
					pstmt.setString(count, "");
				}else{
					pstmt.setString(count, fieldValue);
				}			
				}
				else if(field.getType().isAssignableFrom(java.util.Date.class)){
					java.util.Date dtValue = (java.util.Date) customer.getClass().getMethod(fieldMethod).invoke(customer);
					java.sql.Time sqlDate=null;
					if(!(dtValue==null))
					{
						sqlDate = new  java.sql.Time(dtValue.getTime());
					}

					pstmt.setTime(count, sqlDate);
				}
				else if(field.getType().isAssignableFrom(int.class)){
					intValue = (int) customer.getClass().getMethod(fieldMethod).invoke(customer);
					pstmt.setInt(count, intValue);
				}
				else if(field.getType().isAssignableFrom(double.class)){
					doubleValue = (double) customer.getClass().getMethod(fieldMethod).invoke(customer);
					pstmt.setDouble(count, doubleValue);
				}
				else if(field.getType().isAssignableFrom(long.class)){
					longValue = (long) customer.getClass().getMethod(fieldMethod).invoke(customer);
					pstmt.setDouble(count, longValue);
				}

				else if(field.getType().isAssignableFrom(Timestamp.class)){
					tsValue = (Timestamp) customer.getClass().getMethod(fieldMethod).invoke(customer);
					pstmt.setTimestamp(count,tsValue);
				}
				else if(field.getType().isAssignableFrom(boolean.class)){
					fieldMethod = "is"+ field.getName().substring(0, 1).toUpperCase()+ field.getName().substring(1);
					booleanValue = (boolean) customer.getClass().getMethod(fieldMethod).invoke(customer);
					if(booleanValue)
					{
						pstmt.setString(count, "1");
					}
					else
					{
						pstmt.setString(count, "0");
					}
				}
				else if(field.getType().isAssignableFrom(BigDecimal.class)){
					bigDecimalValue = (BigDecimal) customer.getClass().getMethod(fieldMethod).invoke(customer);
					pstmt.setBigDecimal(count, bigDecimalValue);
				}
			}	
			pstmt.execute();

		}catch(Exception e){
			LOG.error("saveCustomer-->Exception"+e.getMessage());
			LOG.debug("Leaving ");
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

		LOG.exiting("saveCustomer()");

		return customer.getCustCIF();
	}

	// Update the Customer Details
	public boolean updateCustomerDetails(InterfaceCustomerDetail interfaceCustDetail,
			Connection connection) throws Exception {

		LOG.entering("Entering updateCustomerDetails()");
		try
		{
			long CustID=getCustomerID(connection,interfaceCustDetail.getCustCIF());
			interfaceCustDetail.getCustomer().setCustID(CustID);
			interfaceCustDetail.getCustomer().setCustCIF(interfaceCustDetail.getCustCIF());
			updateAddresses(interfaceCustDetail.getAddressList(),connection,CustID);
			updateCustomerEmailList(interfaceCustDetail.getCustomerEMailList(),connection,CustID);
			updateDocumentDetails(interfaceCustDetail.getCustomerDocumentsList(),CustID,connection);
			updateCustomer(interfaceCustDetail.getCustomer(),connection,false,CustID);
			updateCustomerPhoneNumbers(interfaceCustDetail.getCustomerPhoneNumList(),connection,CustID);


		}
		catch(Exception e){			
			LOG.error("updateCustomerDetails()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw e;
		}		
		LOG.debug("Leaving updateCustomerDetails()");
		return true;
	}

	//Update the Customer PhoneNumbers
	private void updateCustomerPhoneNumbers(List<InterfaceCustomerPhoneNumber> customerPhoneNumList,Connection connection,long CustID) throws Exception {

		LOG.entering("updateCustomerPhoneNumbers() ");
		ResultSet rs = null;
		PreparedStatement pstmt=null;

		try{
			for(int i=0;i<customerPhoneNumList.size();i++)
			{
				pstmt =connection.prepareStatement("update CustomerPhoneNumbers set PhoneCountryCode=?,PhoneAreaCode=?,PhoneNumber=? where PhoneCustID=? and PhoneTypeCode=?");


				pstmt.setString(1, customerPhoneNumList.get(i).getPhoneCountryCode());
				pstmt.setString(2, customerPhoneNumList.get(i).getPhoneAreaCode());
				pstmt.setString(3, customerPhoneNumList.get(i).getPhoneNumber());
				pstmt.setLong(4,CustID);
				pstmt.setString(5, customerPhoneNumList.get(i).getPhoneTypeCode());

				pstmt.executeUpdate();
			}

		}catch(Exception e){
			LOG.error("updateCustomerPhoneNumbers()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw new Exception("Customer Not Found");
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

		LOG.exiting("updateCustomerPhoneNumbers() ");
	}

	//update the Customer Addresses
	public void updateAddresses(List<InterfaceCustomerAddress> addressList,Connection connection,long CustID) throws Exception {

		LOG.entering("updateAddresses() ");
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		try{
			for(int i=0;i<addressList.size();i++)
			{
				pstmt =connection.prepareStatement("Update customerAddresses set CustAddrHNbr=?,CustFlatNbr=?,CustAddrStreet=?,CustAddrLine1=?,CustAddrLine2=?,CustPOBox=?,CustAddrCity=?,CustAddrProvince=?,CustAddrCountry=?,CustAddrZIP=?,CustAddrPhone=?,CustAddrFrom=? where CustID=? and CustAddrType=?");

				pstmt.setString(1,addressList.get(i).getCustAddrHNbr());
				pstmt.setString(2,addressList.get(i).getCustFlatNbr());
				pstmt.setString(3,addressList.get(i).getCustAddrStreet());
				pstmt.setString(4,addressList.get(i).getCustAddrLine1());
				pstmt.setString(5,"");
				pstmt.setString(6,addressList.get(i).getCustPOBox());
				pstmt.setString(7,addressList.get(i).getCustAddrCity());
				pstmt.setString(8,addressList.get(i).getCustAddrProvince());
				pstmt.setString(9,addressList.get(i).getCustAddrCountry());
				pstmt.setString(10,addressList.get(i).getCustAddrZIP());
				pstmt.setString(11,addressList.get(i).getCustAddrPhone());
				pstmt.setTimestamp(12,addressList.get(i).getCustAddrFrom());
				pstmt.setLong(13, CustID);
				pstmt.setString(14, addressList.get(i).getCustAddrType());
				pstmt.executeUpdate();
			}

		}catch(Exception e){
			LOG.error("updateAddresses()-->Exception"+e.getMessage());
			LOG.debug("Leaving ");
			throw new Exception("Customer Not Found");
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
		LOG.exiting("updateAddresses()");
	}

	// Update the customer Emails 
	private void updateCustomerEmailList(List<InterfaceCustomerEMail> customerEMailList,Connection connection,long CustID) throws Exception {

		LOG.entering("updateCustomerEmailList() ");

		ResultSet rs = null;
		PreparedStatement pstmt=null;
		try{
			for(int i=0;i<customerEMailList.size();i++)
			{
				pstmt =connection.prepareStatement("update CustomerEmails set CustEMailPriority=?,CustEMail=? where  CustID=? and CustEMailTypeCode=?");
				pstmt.setInt(1, customerEMailList.get(i).getCustEMailPriority());
				pstmt.setString(2, customerEMailList.get(i).getCustEMail());
				pstmt.setLong(3, CustID);
				pstmt.setString(4, customerEMailList.get(i).getCustEMailTypeCode());

				pstmt.executeUpdate();
			}

		}catch(Exception e){
			LOG.error("updateCustomerEmailList()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw new Exception("Customer Not Found");
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

		LOG.exiting("updateCustomerEmailList() ");
	}

	//Update the Customer Details
	private String updateCustomer(InterfaceCustomer customer,Connection connection,boolean type,long CustID) throws Exception {

		LOG.entering("UpdateCustomer()");

		ResultSet rs = null;
		String fieldValue = "";
		int    intValue;
		double doubleValue;
		long longValue;
		Timestamp tsValue=null;
		boolean booleanValue=false;
		BigDecimal bigDecimalValue;
		String queryStmt="";
		String updateQuery="Update Customers set ";
		PreparedStatement pstmt=null;
		int count=0;
		try{
			Field[] fields  =  customer.getClass().getDeclaredFields();	


			for (Field field : fields) 
			{ 
				count=count+1;
				if(count!=1)
				{
					queryStmt =queryStmt+ field.getName()+"=?," ;
				}
			}
			System.out.println(queryStmt);
			queryStmt = queryStmt.subSequence(0, queryStmt.length()-1).toString();
			updateQuery=updateQuery+queryStmt+" where CustID="+CustID;
			pstmt =connection.prepareStatement(updateQuery);
			count=0;
			for (Field field : fields) 
			{  
				count=count+1;
				if(count!=1){
					String fieldMethod = "get"+ field.getName().substring(0, 1).toUpperCase()+ field.getName().substring(1);

					if(field.getType().isAssignableFrom(String.class)){;
					fieldValue = (String) customer.getClass().getMethod(fieldMethod).invoke(customer);
					if(fieldValue==null)
					{
						pstmt.setString(count-1, "");
					}else{
						pstmt.setString(count-1, fieldValue);
					}			
					}
					else if(field.getType().isAssignableFrom(java.util.Date.class)){
						java.util.Date dtValue = (java.util.Date) customer.getClass().getMethod(fieldMethod).invoke(customer);

						java.sql.Date sqlDate=null;
						if(!(dtValue==null))
						{
							sqlDate = new  java.sql.Date(dtValue.getTime());
						}	
						pstmt.setDate(count-1, sqlDate);
					}
					else if(field.getType().isAssignableFrom(int.class)){
						intValue = (int) customer.getClass().getMethod(fieldMethod).invoke(customer);
						pstmt.setInt(count-1, intValue);
					}
					else if(field.getType().isAssignableFrom(double.class)){
						doubleValue = (double) customer.getClass().getMethod(fieldMethod).invoke(customer);
						pstmt.setDouble(count-1, doubleValue);
					}
					else if(field.getType().isAssignableFrom(long.class)){
						longValue = (long) customer.getClass().getMethod(fieldMethod).invoke(customer);
						pstmt.setDouble(count-1, longValue);
					}

					else if(field.getType().isAssignableFrom(Timestamp.class)){
						tsValue = (Timestamp) customer.getClass().getMethod(fieldMethod).invoke(customer);
						pstmt.setTimestamp(count-1,tsValue);
					}
					else if(field.getType().isAssignableFrom(boolean.class)){
						fieldMethod = "is"+ field.getName().substring(0, 1).toUpperCase()+ field.getName().substring(1);
						booleanValue = (boolean) customer.getClass().getMethod(fieldMethod).invoke(customer);
						if(booleanValue)
						{
							pstmt.setString(count-1, "1");
						}
						else
						{
							pstmt.setString(count-1, "0");
						}
					}
					else if(field.getType().isAssignableFrom(BigDecimal.class)){
						bigDecimalValue = (BigDecimal) customer.getClass().getMethod(fieldMethod).invoke(customer);
						pstmt.setBigDecimal(count-1, bigDecimalValue);
					}
				}
			}
			pstmt.executeUpdate();
		}catch(Exception e){
			LOG.error("UpdateCustomer-->Exception"+e.getMessage());
			LOG.debug("Leaving ");
			throw new Exception("Customer Not Found");
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
		LOG.exiting("UpdateCustomer()");				
		return customer.getCustCIF();
	}

	//Update the Customer Document Details 
	public void updateDocumentDetails(List<InterfaceCustomerDocument> customerDocumentList,long custID,Connection connection) throws Exception {
		LOG.entering("updateDocumentDetails() ");
		ResultSet rs = null;
		PreparedStatement pstmt=null;

		try{
			for(int i=0;i<customerDocumentList.size();i++)
			{

				pstmt =connection.prepareStatement("update dbo.CustomerDocumentDetails set CustDocNumber=?,CustDocIssuedOn=?,CustDocExpDate=? where CustID=? and CustDocType=?");

				pstmt.setString(1,customerDocumentList.get(i).getCustDocTitle());

				java.util.Date issuedOn = customerDocumentList.get(i).getCustDocIssuedOn();
				java.sql.Date sqlDate=null;
				if(!(issuedOn==null))
				{
					sqlDate = new  java.sql.Date(issuedOn.getTime());
				}
				pstmt.setDate(2, sqlDate);


				java.util.Date expDt = customerDocumentList.get(i).getCustDocExpDate();
				java.sql.Date sqlDate1=null;
				if(!(expDt==null))
				{
					sqlDate1 = new  java.sql.Date(expDt.getTime());
				}

				pstmt.setDate(3, sqlDate1);

				pstmt.setLong(4, custID);

				pstmt.setString(5, customerDocumentList.get(i).getCustDocType());
				pstmt.execute();
			}

		}catch(Exception e){ 
			LOG.error("updateDocumentDetails()-->Exception"+e.getMessage());
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
		LOG.exiting("updateDocumentDetails() ");

	}

}
