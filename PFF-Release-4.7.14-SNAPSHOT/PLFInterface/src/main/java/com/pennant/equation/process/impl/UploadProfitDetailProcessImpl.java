package com.pennant.equation.process.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.ConnectionPoolException;
import com.ibm.as400.data.ProgramCallDocument;
import com.pennant.coreinterface.model.EodFinProfitDetail;
import com.pennant.coreinterface.process.UploadProfitDetailProcess;
import com.pennant.equation.util.DateUtility;
import com.pennant.equation.util.GenericProcess;
import com.pennant.equation.util.HostConnection;
import com.pennanttech.pennapps.core.InterfaceException;

public class UploadProfitDetailProcessImpl extends GenericProcess implements UploadProfitDetailProcess{

	private static Logger logger = Logger.getLogger(UploadProfitDetailProcessImpl.class);
	
	private HostConnection hostConnection;

	public UploadProfitDetailProcessImpl() {
		super();
	}
	
	@Override
	public void doUploadPftDetails(List<EodFinProfitDetail> profitDetails, boolean isItFirstCall) throws InterfaceException {

		logger.debug("Entering");

		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PTPFF21R"; 		// Upload finance profit Details

		int[] indices = new int[1]; 	// Indices for access array value
		EodFinProfitDetail  pftDetails = null;	
		try {
			as400 = this.hostConnection.getConnection();
			try {
				pcmlDoc = new ProgramCallDocument(as400, pcml);
			}catch (Exception e) {
				logger.error("Exception: ", e);
			}

			pcmlDoc.setValue(pcml + ".@REQDTA.ISFIRSTCALL", isItFirstCall  ? 1 : 0); 		
			pcmlDoc.setValue(pcml + ".@REQDTA.@NOREQ", profitDetails.size()); 	
			pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
			pcmlDoc.setValue(pcml + ".@ERPRM", ""); 

			for (indices[0] = 0; indices[0] < profitDetails.size(); indices[0]++){
				pftDetails = profitDetails.get(indices[0]); 

				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.FinReference", indices, pftDetails.getFinReference()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.FinBranch", indices, pftDetails.getFinBranch()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.FinType", indices, pftDetails.getFinType()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.OldFinType", indices, " "); 		
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.RepayFrq", indices, pftDetails.getRepayFrq()); 
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.CustCIF", indices, pftDetails.getCustCIF()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.FinCcy", indices, pftDetails.getFinCcy()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.FinPurpose", indices, StringUtils.trimToEmpty(pftDetails.getFinPurpose())); 				
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.FinContractDate", indices, DateUtility.formatDate(pftDetails.getFinContractDate(),"ddMMyyyy")); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.FinApprovedDate", indices,  DateUtility.formatDate(pftDetails.getFinApprovedDate(),"ddMMyyyy")); 	 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.FinStartDate", indices, DateUtility.formatDate(pftDetails.getFinStartDate(),"ddMMyyyy")); 				
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.MaturityDate", indices, DateUtility.formatDate(pftDetails.getMaturityDate(),"ddMMyyyy")); 				
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.FullPaidDate", indices, DateUtility.formatDate(pftDetails.getFullPaidDate(),"ddMMyyyy")); 		
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.FinAmount", indices, pftDetails.getFinAmount()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.DownPayment", indices, pftDetails.getDownPayment()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.CurReducingRate", indices, pftDetails.getCurReducingRate()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.curFlatRate", indices, pftDetails.getCurFlatRate()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.TotalpriSchd", indices, pftDetails.getTotalpriSchd()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.TotalPftSchd", indices, pftDetails.getTotalPftSchd()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.TotalPriPaid", indices, pftDetails.getTotalPriPaid()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.TotalPftPaid", indices, pftDetails.getTotalPftPaid()); 
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.EarlyPaidAmt", indices, pftDetails.getEarlyPaidAmt()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.ODPrincipal", indices, pftDetails.getODPrincipal()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.ODProfit", indices, pftDetails.getODProfit()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.PenaltyPaid", indices, pftDetails.getPenaltyPaid()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.PenaltyDue", indices, pftDetails.getPenaltyDue()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.NSchdPriDue", indices, pftDetails.getNSchdPri()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.NSchdPftDue", indices, pftDetails.getNSchdPft()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.AccruePft", indices, pftDetails.getAccruePft()); 	
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.EarnedPft", indices, pftDetails.getEarnedPft()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.Unearned", indices, pftDetails.getUnearned()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.PftInSusp", indices, pftDetails.getPftInSusp() ? 1 : 0); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.SuspPft", indices, pftDetails.getSuspPft()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.PftAccrueTsfd", indices, pftDetails.getPftAccrueTsfd() == null ? BigDecimal.ZERO : pftDetails.getPftAccrueTsfd()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.FinStatus", indices, StringUtils.trimToEmpty(pftDetails.getFinStatus())); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.FinStsReason", indices, StringUtils.trimToEmpty(pftDetails.getFinStsReason())); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.FinWorstStatus", indices, StringUtils.trimToEmpty(pftDetails.getFinWorstStatus())); 
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.TAKAFULPaidAmt", indices, pftDetails.getInsPaidAmt()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.AdminPaidAmt", indices, pftDetails.getAdminPaidAmt()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.TAKAFULInsCal", indices, pftDetails.getInsCal()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.NOInst", indices, pftDetails.getNOInst()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.NOPaidInst", indices, pftDetails.getNOPaidInst()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.NOODInst", indices, pftDetails.getNOODInst()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.FinAccount", indices, StringUtils.trimToEmpty(pftDetails.getFinAccount())); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.FinAcType", indices, pftDetails.getFinAcType());
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.DisbAccountId", indices, pftDetails.getDisbAccountId()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.DisbActCcy", indices, pftDetails.getDisbActCcy()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.RepayAccountId", indices, pftDetails.getRepayAccountId()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.FinCustPftAccount", indices, StringUtils.trimToEmpty(pftDetails.getFinCustPftAccount())); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.IncomeAccount", indices, pftDetails.getIncomeAccount()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.UEIncomeSuspAccount", indices, pftDetails.getUEIncomeSuspAccount()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.FinCommitmentRef", indices, StringUtils.trimToEmpty(pftDetails.getFinCommitmentRef())); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.FinIsActive", indices, pftDetails.getFinIsActive() ? 1 : 0); 
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.NORepayments", indices, pftDetails.getNORepayments()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.FirstRepay", indices, DateUtility.formatDate(pftDetails.getFirstRepayDate(),"ddMMyyyy")); 	 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.FirstRepayAmt", indices, pftDetails.getFirstRepayAmt()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.LastRepayAmt", indices, pftDetails.getLastRepayAmt()); 
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.ODDays", indices, pftDetails.getoDDays()); 

				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.ClosingSts", indices, StringUtils.trimToEmpty(pftDetails.getClosingStatus())); 
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.ProductCtg", indices, StringUtils.trimToEmpty(pftDetails.getFinCategory())); 
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.PrvRepayDate", indices, pftDetails.getLastRpySchDate() != null ? DateUtility.formatDate(pftDetails.getLastRpySchDate(),"ddMMyyyy") : 0); 
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.NextRepayDate", indices, pftDetails.getNextRpySchDate() != null ? DateUtility.formatDate(pftDetails.getNextRpySchDate(),"ddMMyyyy") : 0); 
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.PreviousRepayPri", indices, pftDetails.getLastRpySchPri() != null ? pftDetails.getLastRpySchPri() : 0); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.PreviousRepayPft", indices, pftDetails.getLastRpySchPft() != null ? pftDetails.getLastRpySchPft() : 0); 			

				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.LtstPayDate", indices, pftDetails.getLatestRpyDate() != null ? DateUtility.formatDate(pftDetails.getLatestRpyDate(),"ddMMyyyy") : 0); 
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.LtstPriPay", indices, pftDetails.getLatestRpyPri() != null ? pftDetails.getLatestRpyPri() : 0); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.LtstPftPay", indices, pftDetails.getLatestRpyPft() != null ? pftDetails.getLatestRpyPft() : 0); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.WODate", indices, pftDetails.getLatestWriteOffDate() != null ? DateUtility.formatDate(pftDetails.getLatestWriteOffDate(),"ddMMyyyy") : 0); 
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.WOAmt", indices, pftDetails.getTotalWriteoff() != null ? pftDetails.getTotalWriteoff() : 0); 			

				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.SalariedCustomer", indices, pftDetails.isSalariedCustomer() ? "Y" : "N"); 

				if(pftDetails.getFirstODDate() != null) {
					pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.FirstODDate", indices, DateUtility.formatDate(pftDetails.getFirstODDate(),"ddMMyyyy"));
				} else {
					pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.FirstODDate", indices, "0");
				}

				if(pftDetails.getLastODDate() != null) {
					pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.LastODDate", indices, DateUtility.formatDate(pftDetails.getLastODDate(),"ddMMyyyy"));
				} else {
					pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.LastODDate", indices, "0");
				}
				if(pftDetails.getCRBFirstODDate() != null) {
					pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.CRBFirstODDate", indices, DateUtility.formatDate(pftDetails.getCRBFirstODDate(),"ddMMyyyy"));
				} else {
					pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.CRBFirstODDate", indices, "0");
				}

				if(pftDetails.getCRBLastODDate() != null) {
					pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.CRBLastODDate", indices, DateUtility.formatDate(pftDetails.getCRBLastODDate(),"ddMMyyyy"));
				} else {
					pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.CRBLastODDate", indices, "0");
				}
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.CRBODDays", indices, pftDetails.getCRBODDays()); 
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.CRBODInst", indices, pftDetails.getCRBODInst());
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.CRBODPrincipal", indices, pftDetails.getCRBODPrincipal() == null? BigDecimal.ZERO : pftDetails.getCRBODPrincipal()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.CRBODProfit", indices, pftDetails.getCRBODProfit() == null? BigDecimal.ZERO : pftDetails.getCRBODProfit());

			}

			pcmlDoc.setValue(pcml + ".@RSPDTA.@NORES", 0);
			logger.debug(" Before PCML Call");
			getHostConnection().callAPI(pcmlDoc, pcml);

			logger.debug(" After PCML Call");

		}catch (ConnectionPoolException e){
			logger.error("Exception: ", e);
			throw new InterfaceException("9999","Host Connection Failed.. Please contact administrator ");
		}catch (Exception e) {
			logger.debug("FinReference :"+ pftDetails.getFinReference());
			logger.error("Exception: ", e);
			throw new InterfaceException("9999",e.getMessage());
		}  finally {			
			getHostConnection().closeConnection(as400);
		}

		logger.debug("Leaving");

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public void setHostConnection(HostConnection hostConnection) {
		this.hostConnection = hostConnection;
	}
	public HostConnection getHostConnection() {
		return hostConnection;
	}
		
}
