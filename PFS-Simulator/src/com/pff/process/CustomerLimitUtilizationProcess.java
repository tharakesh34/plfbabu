package com.pff.process;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.pennant.interfaceservice.model.CustomerLimitDetailReply;
import com.pennant.interfaceservice.model.CustomerLimitUtilizationReply;
import com.pennant.interfaceservice.model.CustomerLimitUtilizationRequest;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;
import com.pff.framework.util.PFFUtil;

public class CustomerLimitUtilizationProcess {

	private static Log LOG =null;
	private String limitType=null;
	private CustomerLimitDetailReply limitDetailsReply=null;
	private CustomerLimitUtilizationRequest limitRequest;
	private boolean isUpdate;
	
	public CustomerLimitUtilizationProcess() {
		LOG = LogFactory.getLog(CustomerLimitUtilizationProcess.class);
	}
	
	public CustomerLimitUtilizationReply fetchCustomerLimitDetails(CustomerLimitUtilizationRequest request, Connection connection) throws Exception {

		LOG.entering("fetchCustomerLimitDetails()");
		CustomerLimitDetailReply reply=null;
		CustomerLimitUtilizationReply replyDetails=new CustomerLimitUtilizationReply();
		try {

			reply = getCustomerLimitDetails(connection,request.getLimitRef());

			if(request.getDealAmount().compareTo(reply.getAvailableAmt())==1){
				replyDetails.setReferenceNum(request.getReferenceNum());
				replyDetails.setCustomerReference(request.getCustomerReference());
				replyDetails.setDealID(request.getDealID());
				replyDetails.setLimitRef(request.getLimitRef());
				replyDetails.setOverrides("NOGO");
			}
			else{
				replyDetails.setOverrides("GO");

			} 
		} catch (Exception e) {
			LOG.error("fetchCustomerLimitDetails()-->Exception");
			LOG.debug("Leaving");
			throw new Exception("LimitReferene is Not Found in CustomerLimitDetails");
		}
		LOG.exiting("fetchCustomerLimitDetails() ");
		return replyDetails;
	}

	//Reserves the Utilization amount against customer Limit
	public CustomerLimitUtilizationReply reserveUtilization(CustomerLimitUtilizationRequest request, Connection connection) throws Exception {

		LOG.entering("reserveUtilization()");	

		CustomerLimitUtilizationReply replyDetails=new CustomerLimitUtilizationReply();
		try {

			//
			boolean isFound=getCustomerLimitUtilizationDetail(connection,request);
			if(!isFound){
				if(PFFUtil.RESERVE.equalsIgnoreCase(request.getStatus())||PFFUtil.OVERRIDE_RESERVE.equalsIgnoreCase(request.getStatus()))
				{
					limitType="Reserved";
					limitDetailsReply=getCustomerLimitDetails(connection,request.getLimitRef());				
					if(limitDetailsReply==null){
						throw new Exception("Customer Limit is Not Found");
					}
					else{

						limitDetailsReply.setReservedAmt(limitDetailsReply.getReservedAmt().add(request.getDealAmount()));
						if(PFFUtil.OVERRIDE_RESERVE.equalsIgnoreCase(request.getStatus())){
							limitDetailsReply.setOutstandingAmt(request.getDealAmount().subtract(limitDetailsReply.getAvailableAmt()));
						}
						isUpdate=updateCustomerLimitDetails(connection, request,limitType,limitDetailsReply);

						if(isUpdate){
							saveDetails(request, connection); }
					}
				}
			}      
			else
			{
				throw new Exception("Customer Limit is already Reserved");
			}
		} catch (Exception e) {
			LOG.error("reserveUtilization()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw e;
		}
		LOG.exiting("reserveUtilization() ");
		return replyDetails;
	}

	//Confirms the reserved utilization amount against a customer limit
	public CustomerLimitUtilizationReply confirmUtilization(CustomerLimitUtilizationRequest request,Connection connection) throws Exception {

		LOG.entering("confirmUtilization()");
		CustomerLimitUtilizationReply replyDetails=new CustomerLimitUtilizationReply();
		replyDetails.setReferenceNum("963258741");
		replyDetails.setDealID("WB1507500165");
		replyDetails.setCustomerReference("200000"); 
		replyDetails.setLimitRef("LMT001");
		replyDetails.setResponse("0000");
		replyDetails.setErrMsg("Test Error");
		replyDetails.setReturnCode("0000");
		replyDetails.setReturnText("Success");

		/*try {
			limitType="Available";
			if(PFFUtil.CONFIRM.equalsIgnoreCase(request.getStatus())){
				
				limitDetailsReply=getCustomerLimitDetails(connection,request.getLimitRef());	
				limitRequest=getCustomerLimitUtilizationDetails(connection,request);
				if(limitRequest==null){
					throw new Exception("Customer Limit is Not Reserved");
				}else if(PFFUtil.RESERVE.equalsIgnoreCase(limitRequest.getStatus()) || PFFUtil.OVERRIDE_RESERVE.equalsIgnoreCase(limitRequest.getStatus())&& (request.getDealAmount().compareTo(limitRequest.getDealAmount())==0))
				{
					limitDetailsReply.setAvailableAmt(limitDetailsReply.getAvailableAmt().subtract(request.getDealAmount())); 
					isUpdate=updateCustomerLimitDetails(connection, request,limitType,limitDetailsReply);
					if(isUpdate){
						updateStatus(request, connection);
					}
				}			
			}
		} 
		catch (Exception e) {
			LOG.error("confirmUtilization()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw e;
		}*/
		LOG.exiting("confirmUtilization() ");
		return replyDetails;
	}


	//Updates the status of  customerLimitUtilization based on DealID
	private boolean updateStatus(CustomerLimitUtilizationRequest request, Connection connection) throws Exception {

		LOG.entering("updateStatus()");
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		int count=0;
		try{
			pstmt =connection.prepareStatement("update  dbo.Customerlimitutilization set Status=? Where DealID=?" );
			pstmt.setString(1,request.getStatus());
			pstmt.setString(2,request.getDealID());
			count = pstmt.executeUpdate();
			if(count>0){
				return true;
			}
		}catch(Exception e){ 	
			LOG.error("updateStatus()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw new  Exception("");
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
		LOG.exiting("updateStatus() ");
		return false;
	}

	//This Method Cancels the Reserved Utilization amount against a Customer Limit.
	public void cancelReservation(CustomerLimitUtilizationRequest request,Connection connection) throws Exception {

		LOG.entering("cancelReservation()");
		try {
			if(request.getStatus().equalsIgnoreCase(PFFUtil.CANCEL_RESERVE))
			{
				limitDetailsReply=getCustomerLimitDetails(connection,request.getLimitRef());
				limitRequest=getCustomerLimitUtilizationDetails(connection,request);
				if(limitRequest==null){
					throw new Exception("Customer Limit is Not Reserved");
				}
				else if(PFFUtil.RESERVE.equalsIgnoreCase(limitRequest.getStatus())&& (limitRequest.getDealAmount().compareTo(request.getDealAmount())==0)){
					limitDetailsReply.setReservedAmt(limitDetailsReply.getReservedAmt().subtract(request.getDealAmount()));
					boolean isUpdate=updateCustomerLimitDetails(connection, request,"Reserved",limitDetailsReply);
					if(isUpdate)
					{
						updateStatus(request, connection);
					}

				}
			}

		} catch (Exception e) {
			LOG.error("cancelReservation()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw e;
		}
		LOG.exiting("cancelReservation() ");
	}

	//This Method Cancels the  Utilization amount against a Customer Limit. If the User Cancels the Finance after Confirming the Utilization
	public void cancelUtilization(CustomerLimitUtilizationRequest request,Connection connection) throws Exception {
		LOG.entering("cancelUtilization()");

		try {
			if(PFFUtil.CANCEL_UTILIZATION.equalsIgnoreCase(request.getStatus()))
			{
				limitType="Available";
				limitDetailsReply=getCustomerLimitDetails(connection,request.getLimitRef());
				limitRequest=getCustomerLimitUtilizationDetails(connection,request); 
				if(limitRequest==null){
					throw new Exception("Customer Limit is Not Confirmed");
				}
				else if(PFFUtil.CONFIRM.equalsIgnoreCase(limitRequest.getStatus())&& (limitRequest.getDealAmount().compareTo(request.getDealAmount())==0))
				{
					limitDetailsReply.setReservedAmt(limitDetailsReply.getReservedAmt().subtract(request.getDealAmount()));
					limitDetailsReply.setAvailableAmt(limitDetailsReply.getAvailableAmt().add(request.getDealAmount()));
					isUpdate=updateCustomerLimitDetails(connection, request,limitType,limitDetailsReply);
					if(isUpdate){
						updateStatus(request, connection);}
				}
			}
		} catch (Exception e) {
			LOG.error("cancelUtilization()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw e;
		}
		LOG.exiting("cancelUtilization() ");
	}

	//Updates the CustomerLimitDetails
	private boolean updateCustomerLimitDetails(Connection connection,CustomerLimitUtilizationRequest request,String limitType,CustomerLimitDetailReply limitValue) throws Exception {		
		LOG.entering("updateCustomerLimitDetails() ");
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		int updateCount;
		String  limitType1="";
		int i=0;
		try{	
			if(PFFUtil.CANCEL_UTILIZATION.equalsIgnoreCase(request.getStatus())){
				limitType1=",Reserved=?";
				i=2;
			}
			else{
				i=1;
			}
			pstmt =connection.prepareStatement("update CustomerLimitDetails set "+limitType+"=?" +limitType1+" where LimitReference=?");

			if(limitType.equalsIgnoreCase("Reserved")){
				pstmt.setBigDecimal(1,limitValue.getReservedAmt());}
			else if(limitType.equalsIgnoreCase("Available")){
				pstmt.setBigDecimal(1,limitValue.getAvailableAmt());	
			}	
			if(PFFUtil.CANCEL_UTILIZATION.equalsIgnoreCase(request.getStatus())){
				pstmt.setBigDecimal(i,limitValue.getReservedAmt());
			}
			pstmt.setString(i+1,request.getLimitRef());


			updateCount=pstmt.executeUpdate();
			if(updateCount>0)
			{
				return true;
			}

		}catch(Exception e){
			LOG.error("updateCustomerLimitDetails()-->Exception"+e.getMessage());
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

		LOG.exiting("updateCustomerLimitDetails()");
		return false;
	}

	/*	
	Fetch the CustomerLimitUtilization based on LimitReference,Status and DealID
	@return CustomerLimitUtilizationRequest*/
	private CustomerLimitUtilizationRequest getCustomerLimitUtilizationDetails(
			Connection connection, CustomerLimitUtilizationRequest request) throws Exception {

		LOG.entering("getCustomerLimitUtilizationDetails()");
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		CustomerLimitUtilizationRequest limitRequest=null;
		String status="";
		try{
			pstmt =connection.prepareStatement("select * from dbo.Customerlimitutilization  where LimitRef=?  and status=? and DealID=?" );
			pstmt.setString(1,request.getLimitRef());

			if(PFFUtil.CANCEL_UTILIZATION.equalsIgnoreCase(request.getStatus()))
			{
				status=PFFUtil.CONFIRM;
			}
			else if(PFFUtil.RESERVE.equalsIgnoreCase(request.getStatus())){
				status=PFFUtil.RESERVE;
			}
			else{
				status=PFFUtil.OVERRIDE_RESERVE;
			}

			pstmt.setString(2,status);
			pstmt.setString(3, request.getDealID());
			rs = pstmt.executeQuery();

			while(rs.next())
			{
				limitRequest=new CustomerLimitUtilizationRequest();
				limitRequest.setDealAmount(rs.getBigDecimal("DealAmount"));
				limitRequest.setStatus(rs.getString("Status"));
			}
		}catch(Exception e){ 	
			LOG.error("getCustomerLimitUtilizationDetails()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw new  Exception("");
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
		LOG.exiting("getCustomerLimitDetails() ");
		return limitRequest;
	}

	//To Fetch CustomerLimitUtilization Details based on LimitReference ,DealID and Status .If exists it returns true 
	private boolean getCustomerLimitUtilizationDetail(
			Connection connection, CustomerLimitUtilizationRequest request) throws Exception {

		LOG.entering("getCustomerLimitUtilizationDetails()");
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		String status="";
		try{
			pstmt =connection.prepareStatement("select * from dbo.Customerlimitutilization Where LimitRef=? and DealID=? and status=? " );
			pstmt.setString(1,request.getLimitRef());
			pstmt.setString(2,request.getDealID());
			if(("CANCEL_UTILIZATION").equalsIgnoreCase(request.getStatus()))
			{
				status="CONFIRM";
			}
			else{
				status="RESERVE";
			}
			pstmt.setString(3,status);
			rs = pstmt.executeQuery();

			while(rs.next())
			{
				return true;
			}
		}catch(Exception e){ 	
			LOG.error("getCustomerLimitUtilizationDetails()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw new  Exception("");
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
		LOG.exiting("getCustomerLimitUtilizationDetails() ");
		return false;
	}

	//Fetch the CustomerLimitDetails based on LimitReference
	private CustomerLimitDetailReply getCustomerLimitDetails(Connection connection, String limReference) throws Exception {
		LOG.entering("getCustomerLimitDetails()");
		ResultSet rs = null;
		PreparedStatement pstmt=null;
		CustomerLimitDetailReply custLimitdetails=null;
		try{
			pstmt =connection.prepareStatement("select * from dbo.CustomerLimitDetails where ReferenceNumber=?  ");
			pstmt.setString(1,limReference);
			rs = pstmt.executeQuery();

			while(rs.next())
			{
				custLimitdetails= new CustomerLimitDetailReply();
				custLimitdetails.setLimitRef(rs.getString("LimitReference"));
				custLimitdetails.setApprovedLimit(rs.getBigDecimal("AppovedLimit"));
				custLimitdetails.setAvailableAmt(rs.getBigDecimal("Available"));
				custLimitdetails.setReservedAmt(rs.getBigDecimal("Reserved"));
			}
		}catch(Exception e){ 	
			LOG.error("getCustomerLimitDetails()-->Exception"+e.getMessage());
			LOG.debug("Leaving");
			throw new  Exception("");
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
		LOG.exiting("getCustomerLimitDetails() ");
		return custLimitdetails;
	}

	//Save the CustomerLimtUtilization Details
	public void saveDetails(CustomerLimitUtilizationRequest request,Connection connection) throws Exception {
		LOG.entering("saveDetails()");
		ResultSet rs = null;
		String fieldValue = "";
		BigDecimal bigDecimalValue;
		int    intValue;
		PreparedStatement pstmt=null;
		boolean booleanValue=false;
		int count=0;

		try{
			pstmt =connection.prepareStatement("insert into CustomerLimitUtilization Values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			Field[] fields  =  request.getClass().getDeclaredFields();
			for (Field field : fields) 
			{
				String fieldMethod = "get"+ field.getName().substring(0, 1).toUpperCase()+ field.getName().substring(1);
				count=count+1;
				if(field.getType().isAssignableFrom(String.class)){;

				fieldValue = (String) request.getClass().getMethod(fieldMethod).invoke(request);
				if(fieldValue==null)
				{
					pstmt.setString(count, "");
				}
				else{
					pstmt.setString(count, fieldValue);

				}
				}
				else if(field.getType().isAssignableFrom(java.util.Date.class)){
					java.util.Date dtValue = (java.util.Date) request.getClass().getMethod(fieldMethod).invoke(request);
					java.sql.Date sqlDate=null;
					if(!(dtValue==null))
					{
						sqlDate = new  java.sql.Date(dtValue.getTime());
					}

					pstmt.setDate(count, sqlDate);
				}
				else if(field.getType().isAssignableFrom(int.class)){
					intValue = (int) request.getClass().getMethod(fieldMethod).invoke(request);
					pstmt.setInt(count, intValue);
				}
				else if(field.getType().isAssignableFrom(BigDecimal.class)){
					bigDecimalValue = (BigDecimal) request.getClass().getMethod(fieldMethod).invoke(request);
					pstmt.setBigDecimal(count, bigDecimalValue);
				}
				else if(field.getType().isAssignableFrom(boolean.class)){
					fieldMethod = "is"+ field.getName().substring(0, 1).toUpperCase()+ field.getName().substring(1);
					booleanValue = (boolean) request.getClass().getMethod(fieldMethod).invoke(request);
					if(booleanValue)
					{
						pstmt.setString(count, "1");
					}
					else
					{
						pstmt.setString(count, "0");
					}
				}
			}
			pstmt.execute();
		}catch(Exception e){ 	
			LOG.error("saveDetails()-->Exception  "+e.getMessage());
			LOG.debug("Leaving");
			throw new  Exception("Customer Limit is already Reserved");
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
		LOG.exiting("saveDetails() ");
	}


}

