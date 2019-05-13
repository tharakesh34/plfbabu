package com.pff.service;


import java.sql.SQLException;
import java.text.ParseException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.jaxen.JaxenException;

import com.pennant.interfaceservice.model.CoreBankAccountDetail;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;
import com.pff.framework.util.PFFXmlUtil;
import com.pff.framework.util.SqlConnection;
import com.pff.process.AccountDetailProcess;
import com.pff.vo.PFFMQHeaderVo;

public class AccountDetailService {
	private static Log LOG = null;
	private static String requestPath="/HB_EAI_REQUEST/Request/CustomerAccountsRequest/";
	private PFFMQHeaderVo headerVo=null;

	public AccountDetailService() {
		LOG = LogFactory.getLog(AccountDetailService.class);
	}
	public OMElement processRequest (OMElement requestData,PFFMQHeaderVo headerVo) throws JaxenException {
		LOG.entering("processRequest()");

		OMFactory factory						=	OMAbstractFactory.getOMFactory();
		CoreBankAccountDetail detailsVo		    =   new CoreBankAccountDetail();	
		OMElement responseElement				=	null;	
		OMElement returnElement					= 	null;

		try {
			this.headerVo=headerVo;
			detailsVo = setRequestDetails(detailsVo, requestData);
			responseElement = generateResponseData(detailsVo,factory);

		} catch (Exception e) {
			LOG.info("processRequest()-->Exception");	
			LOG.error(e.getMessage(),e);
			headerVo.setMessageReturnCode("");
			headerVo.setMessageReturnDesc("Error :"+e.getMessage());	
		}
		returnElement = PFFXmlUtil.generateReturnElement(headerVo, factory, responseElement);
		LOG.exiting("processRequest()",returnElement);
		return returnElement;

	}


	private CoreBankAccountDetail setRequestDetails(CoreBankAccountDetail detailsVo,OMElement requestData) {
		
		LOG.entering("setRequestDetails()");
		boolean errorFlag			= 	false;
		String errorMessage			=	null;
		try {  	
			this.headerVo.setRefNumber(PFFXmlUtil.getStringValue(requestData,true, true,"ReferenceNum",requestPath));	
		} catch (Exception e) {
			errorFlag = true;
			errorMessage = e.getMessage();
		}	
		if (!errorFlag){
			AccountDetailProcess process=new AccountDetailProcess();
			
			//Connection to the Database
			SqlConnection con=new SqlConnection();
			try {
				//
				if(("FULL.ACCOUNT.INQUIRY").equalsIgnoreCase(headerVo.getMessageFormat()))
				{
					detailsVo.setCustCIF(PFFXmlUtil.getStringValue(requestData,true, true,"CustomerNumber",requestPath,true,0));
					String acctTypes=PFFXmlUtil.getStringValue(requestData,false, true,"AccountTypes",requestPath);
					String currency=PFFXmlUtil.getStringValue(requestData,false, true,"Currency",requestPath);
					detailsVo=process.fetchAccounts(detailsVo,currency, acctTypes,con.getConnection());
				}
				else{
					detailsVo.setAccountNumber(PFFXmlUtil.getStringValue(requestData,true, true,"AccountNumber",requestPath,true,0));
					detailsVo=process.fetchAccountDetails(detailsVo.getAccountNumber(),con.getConnection());
				}
               if(detailsVo.getAcSummaryList().size()==0)
               {
            	   throw new Exception("Account Summary Not Found");
               }
               else{
				headerVo.setMessageReturnCode("0000");
				headerVo.setMessageReturnDesc("SUCCESS");
               }

			}catch(SQLException e){ 			
				headerVo.setMessageReturnCode("8801");
				headerVo.setMessageReturnDesc("Error :"+e.getMessage());
			} catch (Exception e) {
				headerVo.setMessageReturnCode("9901");
				headerVo.setMessageReturnDesc("Error :"+e.getMessage());
			} 

		} else {
			headerVo.setMessageReturnCode("9902");
			headerVo.setMessageReturnDesc("Error :"+errorMessage);
		}
		LOG.exiting("setRequestDetails", detailsVo);
		return detailsVo;
	}


	public OMElement generateResponseData(CoreBankAccountDetail detailsVo,OMFactory factory) throws ParseException {
		
		LOG.entering("generateResponseData()",detailsVo,factory);	
		OMElement responseBody=factory.createOMElement("Reply",null);
        OMElement CustomerAccountsReply=PFFXmlUtil.setOMChildElement(factory, responseBody, "CustomerAccountsReply", "");
        PFFXmlUtil.setOMChildElement(factory, CustomerAccountsReply, "ReferenceNum", this.headerVo.getRefNumber());
        PFFXmlUtil.setOMChildElement(factory, CustomerAccountsReply, "CustomerNumber", detailsVo.getCustCIF());
        OMElement OperativeAccountsReply=PFFXmlUtil.setOMChildElement(factory, CustomerAccountsReply, "OperativeAccountsReply","");
        for(CoreBankAccountDetail acDetails:detailsVo.getAcSummaryList())
        {
        OMElement AccountSummaryReply=PFFXmlUtil.setOMChildElement(factory, OperativeAccountsReply, "AccountSummaryReply","");
        PFFXmlUtil.setOMChildElement(factory, AccountSummaryReply, "AccountNumber",acDetails.getAccountNumber());
        PFFXmlUtil.setOMChildElement(factory, AccountSummaryReply, "IBAN",detailsVo.getIBAN());
        PFFXmlUtil.setOMChildElement(factory, AccountSummaryReply, "AccountType",acDetails.getAcType());
        PFFXmlUtil.setOMChildElement(factory, AccountSummaryReply, "AccountSubType","");
        PFFXmlUtil.setOMChildElement(factory, AccountSummaryReply, "AccountStatus",acDetails.getOpenStatus());
        PFFXmlUtil.setOMChildElement(factory, AccountSummaryReply, "BranchCode",acDetails.getAcBranch());
        PFFXmlUtil.setOMChildElement(factory, AccountSummaryReply, "AccountName",acDetails.getAcFullName());
        PFFXmlUtil.setOMChildElement(factory, AccountSummaryReply, "Currency",acDetails.getAcCcy());
        PFFXmlUtil.setOMChildElement(factory, AccountSummaryReply, "OpenActualBalance",acDetails.getAcBal());
        PFFXmlUtil.setOMChildElement(factory, AccountSummaryReply, "WorkingBalance",acDetails.getAcBal());
        PFFXmlUtil.setOMChildElement(factory, AccountSummaryReply, "CurrentBalance",acDetails.getAcBal());
        PFFXmlUtil.setOMChildElement(factory, AccountSummaryReply, "AccountMnemonic","");
        PFFXmlUtil.setOMChildElement(factory, AccountSummaryReply, "AccountShortTitle","");
        PFFXmlUtil.setOMChildElement(factory, AccountSummaryReply, "AccountDAO","");
        PFFXmlUtil.setOMChildElement(factory, AccountSummaryReply, "AccountOtherOffice","");
        PFFXmlUtil.setOMChildElement(factory, AccountSummaryReply, "AccountPassbook","");
        PFFXmlUtil.setOMChildElement(factory, AccountSummaryReply, "AccountJointHolder","");
        PFFXmlUtil.setOMChildElement(factory, AccountSummaryReply, "AccountReleationcode","");
        PFFXmlUtil.setOMChildElement(factory, AccountSummaryReply, "AccountJointNotes","");
        PFFXmlUtil.setOMChildElement(factory, AccountSummaryReply, "AccountMinNoOfSignatory","");
        PFFXmlUtil.setOMChildElement(factory, AccountSummaryReply, "AccountIntroducer","");
        PFFXmlUtil.setOMChildElement(factory, AccountSummaryReply, "POAFlag","");
        PFFXmlUtil.setOMChildElement(factory, AccountSummaryReply, "POACIF","");
        PFFXmlUtil.setOMChildElement(factory, AccountSummaryReply, "POAIssueDate","");
        PFFXmlUtil.setOMChildElement(factory, AccountSummaryReply, "POAExpiryDate","");
        PFFXmlUtil.setOMChildElement(factory, AccountSummaryReply, "NIDExpDate","");
        PFFXmlUtil.setOMChildElement(factory, AccountSummaryReply, "GuradianCIF","");
        }
        CustomerAccountsReply.addChild(OperativeAccountsReply);
        PFFXmlUtil.getResponseStatus(factory, CustomerAccountsReply, this.headerVo.getMessageReturnCode(), this.headerVo.getMessageReturnDesc());
 /*       OMElement InvestmentAccountsReply=PFFXmlUtil.setOMChildElement(factory, CustomerAccountsReply, "InvestmentAccountsReply","");
        OMElement InvestmentContract=PFFXmlUtil.setOMChildElement(factory, InvestmentAccountsReply, "InvestmentContract","");
        PFFXmlUtil.setOMChildElement(factory, InvestmentContract, "InvestmentContractNo","");
        PFFXmlUtil.setOMChildElement(factory, InvestmentContract, "InvestmentHolderName","");
        PFFXmlUtil.setOMChildElement(factory, InvestmentContract, "InvestmentAccountType","");
        PFFXmlUtil.setOMChildElement(factory, InvestmentContract, "InvestmentBranchCode","");
        PFFXmlUtil.setOMChildElement(factory, InvestmentContract, "InvestmentCurrencyCode","");
        PFFXmlUtil.setOMChildElement(factory, InvestmentContract, "InvestmentAmount","");
        PFFXmlUtil.setOMChildElement(factory, InvestmentContract, "InvestmentOpenDate","");
        PFFXmlUtil.setOMChildElement(factory, InvestmentContract, "MaturityDate","");
        PFFXmlUtil.setOMChildElement(factory, InvestmentContract, "DepositTenor","");
        PFFXmlUtil.setOMChildElement(factory, InvestmentContract, "CategoryID","");
   
        OMElement FinanceAccountsReply=PFFXmlUtil.setOMChildElement(factory, CustomerAccountsReply, "FinanceAccountsReply","");
        OMElement FinanceContract=PFFXmlUtil.setOMChildElement(factory, FinanceAccountsReply, "FinanceContract","");
        PFFXmlUtil.setOMChildElement(factory, FinanceContract, "LoanContractNumber","");
        PFFXmlUtil.setOMChildElement(factory, FinanceContract, "FinanceHolderName","");
        PFFXmlUtil.setOMChildElement(factory, FinanceContract, "FinanceBranchCode","");
        PFFXmlUtil.setOMChildElement(factory, FinanceContract, "FinanceCurrencyCode","");
        PFFXmlUtil.setOMChildElement(factory, FinanceContract, "FinanceOpenDate","");
        PFFXmlUtil.setOMChildElement(factory, FinanceContract, "InstallmentAmount","");
        PFFXmlUtil.setOMChildElement(factory, FinanceContract, "OutstandingDueAmount","");
        PFFXmlUtil.setOMChildElement(factory, FinanceContract, "InitialLoanAmount","");
        PFFXmlUtil.setOMChildElement(factory, FinanceContract, "DueDate","");
        PFFXmlUtil.setOMChildElement(factory, FinanceContract, "RemainingNoOfInstallments","");
        PFFXmlUtil.setOMChildElement(factory, FinanceContract, "InitialNoOfInstallments","");
        PFFXmlUtil.setOMChildElement(factory, FinanceContract, "LoanType","");
        PFFXmlUtil.setOMChildElement(factory, FinanceContract, "NumOfDaysPastDuedate","");
 */	
		LOG.exiting("generateResponseData", detailsVo);
		return responseBody;
	}







}
