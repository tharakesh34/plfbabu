create or replace PROCEDURE SP_DM_DISB_DETAILS_MONTHLY
(
  BATCHID IN NUMBER 
, ERRORCODE OUT NUMBER 
, ERRORDESC OUT VARCHAR2 
, ERRORSTEP OUT VARCHAR2 
) AS 
 business_date date;
 Process_date date;

BEGIN
   ERRORCODE := '0000';
   ERRORSTEP := 'STEP1';
 Select to_date(sysparmvalue, 'YYYY-MM-DD')  into business_date from smtparameters where SYSPARMCODE='APP_DATE' ;
  ERRORSTEP := 'STEP2';
 Select to_date(sysparmvalue, 'YYYY-MM-DD')  into Process_date from smtparameters where SYSPARMCODE='APP_VALUEDATE' ;

 ERRORSTEP := 'STEP3';
-- Bulk insert from finance main table columns
insert into DM_DISB_DETAILS_MONTHLY (BATCHID, APPLID,AGREEMENTNO,PARENT_AGREEMENTNO,
NET_AMTFIN,NET_TENURE,SHORT_RECD,EXTND_WARR_DLR,PRE_EMI,COMMITMENT_FEE,BALIC_CHARGES,
BUSINESSDATE,PROCESSED_FLAG,PROCESS_DATE)
Select BATCHID ,substr(FM.finreference,-7,7), FM.finreference,0,fp.TotalPriSchd/100,fp.NOINST ,fm.DeductFeeDisb/100,0,0,0,0,
business_date,'N',Process_date
from Financemain FM 
inner join FinPftDetails FP on FM.Finreference = FP.Finreference;

 ERRORSTEP := 'STEP4';
Update DM_DISB_DETAILS_MONTHLY DLM set (HIRER_BANK,CUST_BANK_CITY,CUST_ACCT_NO) = (Select distinct B.Bankname,Rc.Pccityname,M.Accnumber
                                                     from  Mandates m inner join 
                                                           financemain fm on m.mandateid = fm.mandateid inner join 
                                                           BankBranches bb on bb.BANKBRANCHID = m.BANKBRANCHID inner join 
                                                           Bmtbankdetail b on B.Bankcode = Bb.Bankcode inner join
                                                           Rmtprovincevscity RC on Rc.Pccity = Bb.City
                                                     where  fm.Finreference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID)
                                                     where exists (Select 1
                                                     from  Mandates m inner join 
                                                           financemain fm on m.mandateid = fm.mandateid inner join 
                                                           BankBranches bb on bb.BANKBRANCHID = m.BANKBRANCHID inner join 
                                                           Bmtbankdetail b on B.Bankcode = Bb.Bankcode inner join
                                                           Rmtprovincevscity RC on Rc.Pccity = Bb.City
                                                     where  fm.Finreference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID );
                                                     
    ERRORSTEP := 'STEP5';                                                  
Update DM_DISB_DETAILS_MONTHLY DLM set IMD = (select Sum(ReceiptAmount)/100 
                                              from FinReceiptHeader FH
                                              where ReceiptPurpose = 'FeePayment' and ReceiptModeStatus != 'C' and FH.Reference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID)
                                              where exists (select 1
                                              from FinReceiptHeader FH
                                              where ReceiptPurpose = 'FeePayment' and ReceiptModeStatus != 'C' and FH.Reference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID);
Update DM_DISB_DETAILS_MONTHLY DLM set UPFRONT_FRM_CUST = IMD ;
Update DM_DISB_DETAILS_MONTHLY DLM set MORTGAGE_ORG = IMD ;
                                            
 ERRORSTEP := 'STEP6';
Update DM_DISB_DETAILS_MONTHLY DLM set UPFRONT_CHARGES = (select Sum(PAIDAMOUNT)/100
                                              from Finfeedetail FF
                                              where FINEVENT='ADDDBSP' and FF.fiNReference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID)
                                              where exists (select 1
                                              from Finfeedetail FF
                                              where FINEVENT='ADDDBSP' and FF.fiNReference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID);
                                              
   ERRORSTEP := 'STEP7';
Update DM_DISB_DETAILS_MONTHLY DLM set PF_AMT = (select Sum(ACTUALAMOUNT-WAIVEDAMOUNT)/100 
                                              from Finfeedetail FF INNER JOIN
                                                    FEETYPES f ON F.Feetypeid = Ff.Feetypeid
                                              where FEETYPECODE='PROCFEE' and FF.fiNReference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID)
                                              where exists (select 1
                                              from Finfeedetail FF INNER JOIN
                                                    FEETYPES f ON F.Feetypeid = Ff.Feetypeid
                                              where FEETYPECODE='PROCFEE' and FF.fiNReference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID);
Update DM_DISB_DETAILS_MONTHLY DLM set PF_FROM_CUST = PF_AMT ;                              

 ERRORSTEP := 'STEP8';                        
Update DM_DISB_DETAILS_MONTHLY DLM set SERVICECHARGE = (select Sum(ACTUALAMOUNT-WAIVEDAMOUNT)/100 
                                              from Finfeedetail FF INNER JOIN
                                                    FEETYPES f ON F.Feetypeid = Ff.Feetypeid
                                              where FEETYPECODE='SERVFEE' and FF.fiNReference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID)
                                              where exists (select 1
                                              from Finfeedetail FF INNER JOIN
                                                    FEETYPES f ON F.Feetypeid = Ff.Feetypeid
                                              where FEETYPECODE='SERVFEE' and FF.fiNReference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID);
                                              
 ERRORSTEP := 'STEP9';                                            
Update DM_DISB_DETAILS_MONTHLY DLM set UPFRONT_INTEREST = (select Bpiamount/100 
                                              from FinanceMain FM 
                                              where BPITreatment = 'D' and FM.fiNReference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID)
                                              where exists (select 1
                                              from FinanceMain FM 
                                              where BPITreatment = 'D' and FM.fiNReference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID);

ERRORSTEP := 'STEP10';
EXCEPTION  
   WHEN OTHERS THEN
   ERRORDESC := SQLERRM;
   ERRORCODE := SQLCODE; 
END SP_DM_DISB_DETAILS_MONTHLY;
DonotStop