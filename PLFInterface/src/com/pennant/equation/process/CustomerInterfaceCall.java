package com.pennant.equation.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.ConnectionPoolException;
import com.ibm.as400.data.ProgramCallDocument;
import com.pennant.coreinterface.exception.AddressNotFoundException;
import com.pennant.coreinterface.exception.CustomerNotFoundException;
import com.pennant.coreinterface.vo.CustomerInterfaceData;
import com.pennant.coreinterface.vo.CustomerInterfaceData.CustomerIdentity;
import com.pennant.coreinterface.vo.CustomerInterfaceData.CustomerRating;
import com.pennant.equation.util.HostConnection;

public class CustomerInterfaceCall  {
	private static Logger logger = Logger.getLogger(CustomerInterfaceCall.class);
	private HostConnection hostConnection;

	public CustomerInterfaceData getCustomerFullDetails(String custCIF,String custLoc) throws CustomerNotFoundException {
		logger.debug("Entering");

		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PFFFNC";
		boolean newConnection = false;

		try {
			// Create Connection
			if (this.hostConnection == null) {
				this.hostConnection = new HostConnection();
				newConnection = true;
			}

			as400 = this.hostConnection.getConnection();
			// create Document
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			// Set Error code to Empty
			pcmlDoc.setValue(pcml + ".@ERCOD", "0000");
			pcmlDoc.setValue(pcml + ".@ERPRM", "");
			// Set Request Data
			pcmlDoc.setValue(pcml + ".@REQDTA.CustCIF", custCIF);
			pcmlDoc.setValue(pcml + ".@REQDTA.CustLoc", custLoc);
			// Call To interface
			this.hostConnection.callAPI(pcmlDoc, pcml);
			// if No Error Read The Response Data
			if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD"))) {
				CustomerInterfaceData customerInterfaceData = new CustomerInterfaceData();
				customerInterfaceData.setCustCIF((String) pcmlDoc.getValue(pcml + ".@RSPDTA.CustMemonic"));
				customerInterfaceData.setCustFName((String) pcmlDoc.getValue(pcml + ".@RSPDTA.CustFName"));
				customerInterfaceData.setDSRSPCPNC((String) pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPCPNC"));
				customerInterfaceData.setDefaultAccountSName((String) pcmlDoc.getValue(pcml + ".@RSPDTA.DefaultAccountSName"));
				customerInterfaceData.setCustTypeCode((String) pcmlDoc.getValue(pcml + ".@RSPDTA.CustTypeCode"));
				customerInterfaceData.setCustIsClosed((String) pcmlDoc.getValue(pcml + ".@RSPDTA.CustIsClosed"));
				customerInterfaceData.setCustIsActive((String) pcmlDoc.getValue(pcml + ".@RSPDTA.CustIsActive"));
				customerInterfaceData.setCustDftBranch((String) pcmlDoc.getValue(pcml + ".@RSPDTA.CustDftBranch"));
				customerInterfaceData.setGroupName((String) pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPGRP"));
				customerInterfaceData.setDSRSPPDAT(String.valueOf(pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPPDAT")));
				customerInterfaceData.setCustParentCountry((String) pcmlDoc.getValue(pcml + ".@RSPDTA.CustParentCountry"));
				customerInterfaceData.setCustRiskCountry((String) pcmlDoc.getValue(pcml + ".@RSPDTA.CustRiskCountry"));
				customerInterfaceData.setCustDOB(String.valueOf(pcmlDoc.getValue(pcml + ".@RSPDTA.CustDOB")));
				customerInterfaceData.setCustSalutationCode((String) pcmlDoc.getValue(pcml + ".@RSPDTA.CustSalutationCode"));
				customerInterfaceData.setCustGenderCode((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustGenderCode"));
				customerInterfaceData.setCustPOB((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustPOB"));
				customerInterfaceData.setCustPassportNo((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustPassportNo"));
				customerInterfaceData.setCustPassportExpiry(String.valueOf(pcmlDoc.getValue(pcml + ".@RSPDTA.CustPassportExpiry")));
				customerInterfaceData.setCustIsMinor((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustIsMinor"));
				customerInterfaceData.setTradeLicensenumber((String)pcmlDoc.getValue(pcml + ".@RSPDTA.TradeLicensenumber"));
				customerInterfaceData.setTradeLicenseExpiry(String.valueOf(pcmlDoc.getValue(pcml + ".@RSPDTA.TradeLicenseExpiry")));
				customerInterfaceData.setVisaNumber((String)pcmlDoc.getValue(pcml + ".@RSPDTA.VisaNumber"));
				customerInterfaceData.setVisaExpirydate(String.valueOf(pcmlDoc.getValue(pcml + ".@RSPDTA.VisaExpirydate")));
				customerInterfaceData.setCustCoreBank((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustCoreBank"));
				customerInterfaceData.setCustCtgCode((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustCtgCode"));
				customerInterfaceData.setCustShrtName((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustShrtName"));
				customerInterfaceData.setCustFNameLclLng((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustFNameLclLng"));
				customerInterfaceData.setCustShrtNameLclLng((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustShrtNameLclLng"));
				customerInterfaceData.setCustCOB((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustCOB"));
				customerInterfaceData.setCustRO1((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustRO1"));
				customerInterfaceData.setCustIsBlocked((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustIsBlocked"));				
				customerInterfaceData.setCustIsDecease((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustIsDecease"));
				customerInterfaceData.setCustIsTradeFinCust((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustIsTradeFinCust"));
				customerInterfaceData.setCustSector((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustSector"));
				customerInterfaceData.setCustSubSector((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustSubSector"));
				customerInterfaceData.setCustProfession((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustProfession"));
				customerInterfaceData.setCustTotalIncome(pcmlDoc.getValue(pcml + ".@RSPDTA.CustTotalIncome"));
				customerInterfaceData.setCustMaritalSts((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustMaritalSts"));
				customerInterfaceData.setCustEmpSts((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustEmpSts"));
				customerInterfaceData.setCustBaseCcy((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustBaseCcy"));
				customerInterfaceData.setCustResdCountry((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustResdCountry"));
				//customerInterfaceData.setCustNationality((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustNationality"));
				customerInterfaceData.setCustClosedOn(pcmlDoc.getValue(pcml + ".@RSPDTA.CustClosedOn"));
				customerInterfaceData.setCustStmtFrq((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustStmtFrq"));
				customerInterfaceData.setCustIsStmtCombined((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustIsStmtCombined"));
				customerInterfaceData.setCustStmtLastDate(pcmlDoc.getValue(pcml + ".@RSPDTA.CustStmtLastDate"));
				customerInterfaceData.setCustStmtNextDate(pcmlDoc.getValue(pcml + ".@RSPDTA.CustStmtNextDate"));
				customerInterfaceData.setCustFirstBusinessDate(pcmlDoc.getValue(pcml + ".@RSPDTA.CustFirstBusinessDate"));
				//<!-- Address Details-->
				customerInterfaceData.setCustAddrType((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustAddrType"));
				customerInterfaceData.setCustAddrHNbr((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustAddrHNbr"));
				customerInterfaceData.setCustFlatNbr((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustFlatNbr"));
				customerInterfaceData.setCustAddrStreet((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustAddrStreet"));
				customerInterfaceData.setCustAddrLine1((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustAddrLine1"));
				customerInterfaceData.setCustAddrLine2((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustAddrLine2"));
				customerInterfaceData.setCustPOBox((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustPOBox"));
				customerInterfaceData.setCustAddrCity((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustAddrCity"));
				customerInterfaceData.setCustAddrProvince((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustAddrProvince"));
				customerInterfaceData.setCustAddrCountry((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustAddrCountry"));
				customerInterfaceData.setCustAddrZIP((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustAddrZIP"));
				customerInterfaceData.setCustAddrPhone((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustAddrPhone"));
				//<!-- customer phone numbers -->	
				customerInterfaceData.setCustOfficePhone((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustOfficePhone"));
				customerInterfaceData.setCustMobile((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustMobile"));
				customerInterfaceData.setCustResPhone((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustResPhone"));
				customerInterfaceData.setCustOtherPhone((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustOtherPhone"));
				//<!-- Email Details-->
				customerInterfaceData.setCustEMailTypeCode1((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustEMailTypeCode1"));
				customerInterfaceData.setCustEMail1((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustEMail1"));
				customerInterfaceData.setCustEMailTypeCode2((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustEMailTypeCode2"));
				customerInterfaceData.setCustEMail2((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustEMail2"));
				//<!-- Employee Details-->
				customerInterfaceData.setCustEmpName((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustEmpName"));
				customerInterfaceData.setCustEmpFrom(pcmlDoc.getValue(pcml + ".@RSPDTA.CustEmpFrom"));
				customerInterfaceData.setCustEmpDesg((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustEmpDesg"));
				//		<!-- customer ratings-->	
				int dsRspCount=0;
				try {
					dsRspCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.CustRatCnt").toString());
				} catch (Exception e) {
					logger.debug(e);
				}
				List<CustomerRating> list=new ArrayList<CustomerRating>(); 
				int[] indices = new int[1]; // Indices for access array value
				for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++) {
					CustomerRating customerRating=	customerInterfaceData.new CustomerRating();
					customerRating.setCustRatingType((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustRatings.CustRatingType",indices));
					customerRating.setCustLongRate((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustRatings.CustLongRate",indices));
					customerRating.setCustShortRate((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustRatings.CustShortRate",indices));
					list.add(customerRating);
				}
				customerInterfaceData.setCustomerRatinglist(list);
				//		<!-- customer Identity-->	
				int custIdCount=0;
				try {
					custIdCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.CustIDCount").toString());
				} catch (Exception e) {
					logger.debug(e);
				}
				List<CustomerIdentity> idlist=new ArrayList<CustomerIdentity>(); 
				int[] indices1 = new int[1]; // Indices for access array value
				for (indices1[0] = 0; indices1[0] < custIdCount; indices1[0]++) {
					CustomerIdentity customerIdentity=	customerInterfaceData.new CustomerIdentity();
					customerIdentity.setCustIDType((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustIdentity.CustIdType",indices1));
					customerIdentity.setCustIDNumber((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustIdentity.CustIdNum",indices1));
					customerIdentity.setCustIDCountry((String)pcmlDoc.getValue(pcml + ".@RSPDTA.CustIdentity.CustIdCna",indices1));
					customerIdentity.setCustIDIssueDate(pcmlDoc.getValue(pcml + ".@RSPDTA.CustIdentity.CustIdIssuDt",indices1));
					customerIdentity.setCustIDExpDate(pcmlDoc.getValue(pcml + ".@RSPDTA.CustIdentity.CustIdExpDt",indices1));
					idlist.add(customerIdentity);
				}
				customerInterfaceData.setCustomerIdentitylist(idlist);
				
//				customerInterfaceData.setCustEmpHNbr(pcmlDoc.getValue(pcml + ".@RSPDTA.CustEmpHNbr"));
//				customerInterfaceData.setCustEMpFlatNbr(pcmlDoc.getValue(pcml + ".@RSPDTA.CustEMpFlatNbr"));
//				customerInterfaceData.setCustEmpAddrStreet(pcmlDoc.getValue(pcml + ".@RSPDTA.CustEmpAddrStreet"));
//				customerInterfaceData.setCustEMpAddrLine1(pcmlDoc.getValue(pcml + ".@RSPDTA.CustEMpAddrLine1"));
//				customerInterfaceData.setCustEMpAddrLine2(pcmlDoc.getValue(pcml + ".@RSPDTA.CustEMpAddrLine2"));
//				customerInterfaceData.setCustEmpPOBox(pcmlDoc.getValue(pcml + ".@RSPDTA.CustEmpPOBox"));
//				customerInterfaceData.setCustEmpAddrCity(pcmlDoc.getValue(pcml + ".@RSPDTA.CustEmpAddrCity"));
//				customerInterfaceData.setCustEmpAddrProvince(pcmlDoc.getValue(pcml + ".@RSPDTA.CustEmpAddrProvince"));
//				customerInterfaceData.setCustEmpAddrCountry(pcmlDoc.getValue(pcml + ".@RSPDTA.CustEmpAddrCountry"));
//				customerInterfaceData.setCustEmpAddrZIP(pcmlDoc.getValue(pcml + ".@RSPDTA.CustEmpAddrZIP"));
//				customerInterfaceData.setCustEmpAddrPhone(pcmlDoc.getValue(pcml + ".@RSPDTA.CustEmpAddrPhone"));
//				//<!-- customer notes-->	
//				customerInterfaceData.setCustNotesTitle(pcmlDoc.getValue(pcml + ".@RSPDTA.CustNotesTitle"));
//				customerInterfaceData.setCustNotes(pcmlDoc.getValue(pcml + ".@RSPDTA.CustNotes"));
//				//<!-- customer Income-->	
//				customerInterfaceData.setCustIncomeType(pcmlDoc.getValue(pcml + ".@RSPDTA.CustIncomeType"));
//				customerInterfaceData.setCustIncome(pcmlDoc.getValue(pcml + ".@RSPDTA.CustIncome"));
				return customerInterfaceData;
			}	

		} catch (ConnectionPoolException e){
			logger.error("Exception " + e);
			throw new CustomerNotFoundException("Host Connection Failed.. Please contact administrator ");
		} catch (Exception e) {
			logger.error("Exception " + e);
			throw new CustomerNotFoundException(e.getMessage());
		} finally {
			if (newConnection) {
				this.hostConnection.closeConnection(as400);
			}
		}
		return null;

	}

}
