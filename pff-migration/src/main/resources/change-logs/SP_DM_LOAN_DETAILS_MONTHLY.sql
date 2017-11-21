create or replace PROCEDURE SP_DM_LOAN_DETAILS_MONTHLY 
(
  BATCHID IN NUMBER 
, ERRORCODE OUT NUMBER 
, ERRORDESC OUT VARCHAR2 
, ERRORSTEP OUT VARCHAR2 
) AS 
 business_date date;
 Process_date date;
 Cur_Year number;
 Cur_month number;
BEGIN
   ERRORCODE := '0000';
    ERRORSTEP := 'STEP1';
 Select to_date(sysparmvalue, 'YYYY-MM-DD')  into business_date from smtparameters where SYSPARMCODE='APP_DATE' ;
  ERRORSTEP := 'STEP2';
 Select to_date(sysparmvalue, 'YYYY-MM-DD')  into Process_date from smtparameters where SYSPARMCODE='APP_VALUEDATE' ;
  ERRORSTEP := 'STEP3';
 Select EXTRACT(year from to_date(sysparmvalue, 'YYYY-MM-DD'))  into Cur_Year from smtparameters where SYSPARMCODE='APP_DATE' ;
  ERRORSTEP := 'STEP4';
 Select EXTRACT(month from to_date(sysparmvalue, 'YYYY-MM-DD')) into Cur_month from smtparameters where SYSPARMCODE='APP_DATE' ;

 ERRORSTEP := 'STEP5';
-- Bulk insert from finance main table columns
insert into DM_LOAN_DETAILS_MONTHLY (BATCHID, APPLID,AGREEMENTNO,DISBURSEMENTDATE,CUSTOMER_YIELD,
TEST_FLAG,STATUS,NAP_STAGE,LMS_BUCKET,COLL_BUCKET,LAST_EMI_COLL_DT,CLOSUREDATE,AMT_COLL_2ND_PRESENT,DPDDAYS,PROCESSED_FLAG,
BUSINESSDATE,PROCESS_DATE)
Select BATCHID ,substr(fm.finreference,-7,7), FM.finreference,FM.Finstartdate,FM.EffectiveRateOfReturn,
0,case when FM.closingstatus='C' then 'Cancelled' when FM.FinIsActive=0 and FM.closingstatus!='C' then 'Closed'
when FM.FinIsActive=1 then 'Active' end STATUS,
case when FM.closingstatus='W' then 'WRITEOFF' else 'REGULAR' end NAP_STAGE,DUEBUCKET,DUEBUCKET,
FP.LATESTRPYDATE LAST_EMI_COLL_DT,Case when FP.FINISACTIVE=0 then FP.LATESTRPYDATE else FP.MaturityDate end CLOSUREDATE,0,FP.MAXODDAYS,
'N' PROCESSED_FLAG,business_date,Process_date
from Financemain FM 
inner join FinPftDetails FP on FM.Finreference = FP.Finreference;
 ERRORSTEP := 'STEP6';
-- Update Presentmentdetails column
Update DM_LOAN_DETAILS_MONTHLY DLM set EMI_PRESENTMENT_FLAG = (Select distinct 'Y' 
                                                               from Presentmentdetails PD where Status in ('I','A') and PD.Finreference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID)
                                                               where exists (Select 1
                                                               from Presentmentdetails PD where Status in ('I','A') and PD.Finreference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID);
   ERRORSTEP := 'STEP7';                                                             
Update DM_LOAN_DETAILS_MONTHLY DLM set EMI_PRESENTMENT_FLAG = 'N' where EMI_PRESENTMENT_FLAG is null and DLM.BATCHID = BATCHID;

 ERRORSTEP := 'STEP8';
Update DM_LOAN_DETAILS_MONTHLY DLM set FIRST_PRESENT_BOUNCE = (Select  distinct 'Y' from Presentmentheader where id in (
                                                                Select min(PH.id) 
                                                               from Presentmentheader PH inner join 
                                                                    Presentmentdetails PD  on PH.id =Pd.Presentmentid                                                               
                                                               where PD.Status in ('B') and PD.Finreference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID)
                                                               and EXTRACT(month from PRESENTMENTDATE)=Cur_month and EXTRACT(year from PRESENTMENTDATE)=Cur_Year)
                                                               
                                                               where exists (Select 1 from Presentmentheader where id in (
                                                                Select min(PH.id) 
                                                               from Presentmentheader PH inner join 
                                                                    Presentmentdetails PD  on PH.id =Pd.Presentmentid                                                               
                                                               where PD.Status in ('B') and PD.Finreference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID)
                                                               and EXTRACT(month from PRESENTMENTDATE)=Cur_month and EXTRACT(year from PRESENTMENTDATE)=Cur_Year);
                                                               
Update DM_LOAN_DETAILS_MONTHLY DLM set FIRST_PRESENT_BOUNCE = 'N' where FIRST_PRESENT_BOUNCE is null;

 ERRORSTEP := 'STEP9';
Update DM_LOAN_DETAILS_MONTHLY DLM set SECOND_PRESENT_BOUNCE = (Select distinct 'Y' from Presentmentheader where id in (
                                                                Select T.id from (
                                                                Select row_number() over(order by PH.id) rownumb,PH.id
                                                               from Presentmentheader PH inner join 
                                                                    Presentmentdetails PD  on PH.id =Pd.Presentmentid                                                               
                                                               where PD.Status in ('B') and PD.Finreference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID)T where rownumb=2 )
                                                               and EXTRACT(month from PRESENTMENTDATE)=Cur_month and EXTRACT(year from PRESENTMENTDATE)=Cur_Year)                                                               
                                                               where exists (Select 1 from Presentmentheader where id in (
                                                                Select T.id from (
                                                                Select row_number() over(order by PH.id) rownumb,PH.id
                                                               from Presentmentheader PH inner join 
                                                                    Presentmentdetails PD  on PH.id =Pd.Presentmentid                                                               
                                                               where PD.Status in ('B') and PD.Finreference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID)T where rownumb=2 )
                                                               and EXTRACT(month from PRESENTMENTDATE)=Cur_month and EXTRACT(year from PRESENTMENTDATE)=Cur_Year);
   ERRORSTEP := 'STEP10';                                                             
Update DM_LOAN_DETAILS_MONTHLY DLM set SECOND_PRESENT_BOUNCE = 'N' where SECOND_PRESENT_BOUNCE is null;

 ERRORSTEP := 'STEP11';
Update DM_LOAN_DETAILS_MONTHLY DLM set RETINTHREE = (Select Count(*)
                                                     from Presentmentheader PH inner join 
                                                          Presentmentdetails PD  on PH.id =Pd.Presentmentid                                                               
                                                     where PD.Status in ('B') and PD.Finreference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID
                                                     and PRESENTMENTDATE>='01-AUG-17')                                                               
                                                    where exists (Select 1  from Presentmentheader PH inner join 
                                                                  Presentmentdetails PD  on PH.id =Pd.Presentmentid                                                               
                                                                    where PD.Status in ('B') and PD.Finreference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID
                                                                  and PRESENTMENTDATE>='01-AUG-17');
   ERRORSTEP := 'STEP12';                                                             
Update DM_LOAN_DETAILS_MONTHLY DLM set FIRST_BOUNCE_DATE = (Select min(PRESENTMENTDATE)
                                                     from Presentmentheader PH inner join 
                                                          Presentmentdetails PD  on PH.id =Pd.Presentmentid                                                               
                                                     where PD.Status in ('B') and PD.Finreference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID)                                                               
                                                    where exists (Select 1  from Presentmentheader PH inner join 
                                                          Presentmentdetails PD  on PH.id =Pd.Presentmentid                                                               
                                                     where PD.Status in ('B') and PD.Finreference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID);
                                                     
    ERRORSTEP := 'STEP13';                                                  
Update DM_LOAN_DETAILS_MONTHLY DLM set TOTAL_CHQ_BOUNCE = (Select Count(*)
                                                     from Presentmentheader PH inner join 
                                                          Presentmentdetails PD  on PH.id =Pd.Presentmentid                                                               
                                                     where PD.Status in ('B') and PD.Finreference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID
                                                     and EXTRACT(month from PRESENTMENTDATE)=Cur_month and EXTRACT(year from PRESENTMENTDATE)=Cur_Year)                                                               
                                                    where exists (Select 1  from Presentmentheader PH inner join 
                                                                  Presentmentdetails PD  on PH.id =Pd.Presentmentid                                                               
                                                                    where PD.Status in ('B') and PD.Finreference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID
                                                                  and EXTRACT(month from PRESENTMENTDATE)=Cur_month and EXTRACT(year from PRESENTMENTDATE)=Cur_Year);                                               
                                                     
 ERRORSTEP := 'STEP13';
Update DM_LOAN_DETAILS_MONTHLY DLM set DUEDAY = (Select EXTRACT(day from min(Schdate))
                                                     from Finscheduledetails FS                                                           
                                                     where REPAYONSCHDATE=1 and REPAYAMOUNT>PARTIALPAIDAMT and FS.Finreference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID)                                                               
                                                    where exists (Select 1  from Finscheduledetails FS                                                           
                                                     where REPAYONSCHDATE=1 and REPAYAMOUNT>PARTIALPAIDAMT and FS.Finreference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID);
                                                     
 Update DM_LOAN_DETAILS_MONTHLY DLM set FEMIRET = FIRST_PRESENT_BOUNCE;
  ERRORSTEP := 'STEP14';
 Update DM_LOAN_DETAILS_MONTHLY DLM set LAST_EMI_COLL_AMT = (Select SUM(RECEIPTAMOUNT)RECEIPTAMOUNT
                                                     from Finreceiptheader FH                                                           
                                                       where ReceiptPurpose='SchdlRepayment' and FH.reference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID and 
                                                       EXTRACT(month from REALIZATIONDATE)=Cur_month and EXTRACT(year from REALIZATIONDATE)=Cur_Year)                                                               
                                                    where exists (Select 1  from Finreceiptheader FH                                                           
                                                       where ReceiptPurpose='SchdlRepayment' and FH.reference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID and 
                                                       EXTRACT(month from REALIZATIONDATE)=Cur_month and EXTRACT(year from REALIZATIONDATE)=Cur_Year);
                                                       
   ERRORSTEP := 'STEP15';                                                     
 Update DM_LOAN_DETAILS_MONTHLY DLM set BULK_REFUND = (Select SUM(RECEIPTAMOUNT)RECEIPTAMOUNT
                                                     from Finreceiptheader FH                                                           
                                                       where ReceiptPurpose='EarlyPayment' and FH.reference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID and 
                                                       EXTRACT(month from REALIZATIONDATE)=Cur_month and EXTRACT(year from REALIZATIONDATE)=Cur_Year)                                                               
                                                    where exists (Select 1  from Finreceiptheader FH                                                           
                                                       where ReceiptPurpose='EarlyPayment' and FH.reference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID and 
                                                       EXTRACT(month from REALIZATIONDATE)=Cur_month and EXTRACT(year from REALIZATIONDATE)=Cur_Year);
EXCEPTION  
   WHEN OTHERS THEN
   ERRORDESC := SQLERRM;
   ERRORCODE := SQLCODE; 
END SP_DM_LOAN_DETAILS_MONTHLY;
DonotStop