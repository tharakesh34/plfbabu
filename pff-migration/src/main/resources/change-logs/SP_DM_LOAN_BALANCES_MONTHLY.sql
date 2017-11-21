create or replace PROCEDURE SP_DM_LOAN_BALANCES_MONTHLY 
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
insert into DM_LOAN_BALANCES_MONTHLY (BATCHID,
  APPLID,AGREEMENTNO,COLL_BOM_BUCKET,DPD,
  DPD_STRING,EMI_OS,TOTAL_RECBL,TOTAL_RECD,NO_OF_EMI_DUE,
  NO_OF_EMI_RECD,NO_OF_EMI_OS,TOTAL_CHQ_BOUNCE,EMI_DUE,PRINCIPAL_DUE,INTEREST_DUE,
  EMI_RECEIVED,PRINCIPAL_RECEIVED,INTEREST_RECEIVED,PRINCIPAL_OS,INTEREST_OS,
  SOH_BALANCE,NO_OF_UNBILLED_EMI,TOTAL_INTEREST,ACCRUED_AMOUNT,BALANCE_UMFC,
  EMI_IN_ADVANCE_UNBILLED,EMI_IN_ADV_BILLED_PRINCOMP,EMI_IN_ADV_BILLED_INTCOMP,
  EMI_IN_ADV_UNBILLED_PRINCOMP,LPP_CHARGES_RECEIVABLE,
  LPP_CHARGES_RECEIVED,BOUNCE_RECEIPT_DAYS,PROCESSED_FLAG,
  AMTFIN,DISBURSEDAMT,DISB_STATUS,GROSS_TENURE,MATURITYDATE,EMI,PRODUCTFLAG,AGREEMENTDATE,
  EXPIRYDATE,BUSINESSDATE,PROCESS_DATE )
 Select BATCHID,substr(fp.finreference,-7,7),Fp.finreference,Fm.DueBucket,Fp.MAXODDAYS,''DPD_STRING,
  (Fp.ODPRINCIPAL + Fp.ODPROFIT)/CCYMINORCCYUNITS,(Fp.TOTALPRIBAL + Fp.TOTALPFTBAL)/CCYMINORCCYUNITS,(Fp.TOTALPFTPAID+ Fp.TOTALPRIPAID)/CCYMINORCCYUNITS,
  Case when FIRSTREPAYAMT >0 then case when (coalesce(fp.ODPRINCIPAL +  fp.ODPROFIT,1)/FIRSTREPAYAMT)/CCYMINORCCYUNITS>999 then 999 end end  NO_OF_EMI_DUE,
  fp.NOPAIDINST, fp.NOINST - fp.NOPAIDINST,0,(fp.TOTALPRIBAL + fp.TOTALPFTBAL)/CCYMINORCCYUNITS,
  fp.ODPRINCIPAL/CCYMINORCCYUNITS, fp.ODPROFIT/CCYMINORCCYUNITS, (fp.TOTALPFTPAID+ fp.TOTALPRIPAID)/CCYMINORCCYUNITS,
  fp.TOTALPRIPAID/CCYMINORCCYUNITS, fp.TOTALPFTPAID/CCYMINORCCYUNITS, fp.TOTALPRIBAL/CCYMINORCCYUNITS , fp.TOTALPFTBAL/CCYMINORCCYUNITS,
  (fp.TOTALPRIBAL - fp.ODPRINCIPAL)/CCYMINORCCYUNITS,(fp.NOINST - fp.NOODINST - fp.NOPAIDINST)/CCYMINORCCYUNITS,
  fp.TOTALPFTSCHD/CCYMINORCCYUNITS,fp.PFTACCRUED/CCYMINORCCYUNITS,(fp.TOTALPFTSCHD - fp.TOTALPFTPAID - fp.ACRTILLLBD)/CCYMINORCCYUNITS,
  0,0,0,0,(PENALTYPAID+PENALTYDUE+PENALTYWAIVED)/CCYMINORCCYUNITS,PENALTYPAID/CCYMINORCCYUNITS,0,'N',
  fm.FINASSETVALUE/CCYMINORCCYUNITS,fm.FINCURRASSETVALUE/CCYMINORCCYUNITS,case when fm.FINASSETVALUE=fm.FINCURRASSETVALUE then 'F' else 'D' end,
  fp.NOINST,fp.MATURITYDATE,fp.FIRSTREPAYAMT/CCYMINORCCYUNITS,fp.fintype,fp.FINSTARTDATE,fp.MATURITYDATE  ,business_date,Process_date
  from FinPftDetails FP  
  inner join Financemain FM on FM.Finreference = FP.Finreference
  inner join Rmtcurrencies RC on RC.ccycode= FM.finccy;
  

  
ERRORSTEP := 'STEP4';
Update DM_LOAN_BALANCES_MONTHLY DLM set PRINCIPAL_WAIVED =  (Select Case when WaivedAmount<SCHDPRIPAID then WaivedAmount/100 else SCHDPRIPAID/100 end  from (
                                                                (Select FH.Reference Finreference,SUM(R.WaivedAmount)WaivedAmount 
                                                               from Finreceiptheader FH inner join
                                                                    Receiptallocationdetail R on R.Receiptid = Fh.Receiptid inner join
                                                                    Financemain FM on FM.Finreference = FH.Reference  
                                                                    where FM.finisactive=0 and R.Allocationtype='PRI' and FM.Finreference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID
                                                                    group by FH.Reference))Main inner join(                                                                
                                                                Select T.Finreference,T1.SCHDPRIPAID from (
                                                                Select Finreference,max(schdate) max_schdate
                                                                from Finscheduledetails FS  where FS.Finreference = DLM.AGREEMENTNO
                                                                group by Finreference)T inner join Finscheduledetails T1
                                                                on T.Finreference =T1.Finreference and T1.schdate = T.max_schdate) sub 
                                                                on Main.Finreference =SUb.Finreference and Main.Finreference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID
                                                                )
                                                               where exists (Select 1  from (
                                                                (Select FH.Reference Finreference,SUM(R.WaivedAmount)WaivedAmount 
                                                               from Finreceiptheader FH inner join
                                                                    Receiptallocationdetail R on R.Receiptid = Fh.Receiptid inner join
                                                                    Financemain FM on FM.Finreference = FH.Reference  and FM.Finreference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID
                                                                    where FM.finisactive=0 and R.Allocationtype='PRI'
                                                                    group by FH.Reference))Main inner join(                                                                
                                                                Select T.Finreference,T1.SCHDPRIPAID from (
                                                                Select Finreference,max(schdate) max_schdate
                                                                from Finscheduledetails FS  where FS.Finreference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID
                                                                group by Finreference)T inner join Finscheduledetails T1
                                                                on T.Finreference =T1.Finreference and T1.schdate = T.max_schdate) sub 
                                                                on Main.Finreference =SUb.Finreference and Main.Finreference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID);

ERRORSTEP := 'STEP5';
Update DM_LOAN_BALANCES_MONTHLY DLM set EMI_IN_ADVANCE_RECEIVED_MAKER = (Select SUM(RECEIPTAMOUNT/100)
                                                               from Finreceiptheader_temp FR where RECEIPTPURPOSE='EarlyPayment' and FR.reference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID)
                                                               where exists (Select 1
                                                               from Finreceiptheader_temp FR where RECEIPTPURPOSE='EarlyPayment' and FR.reference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID);
                                                               
                                                               
ERRORSTEP := 'STEP6';
Update DM_LOAN_BALANCES_MONTHLY DLM set EMI_IN_ADVANCE_BILLED = (Select SUM(RECEIPTAMOUNT/100)
                                                               from Finreceiptheader FR where RECEIPTPURPOSE='EarlyPayment' and FR.reference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID)
                                                               where exists (Select 1
                                                               from Finreceiptheader FR where RECEIPTPURPOSE='EarlyPayment' and FR.reference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID);
                                                               
ERRORSTEP := 'STEP7';                                                               
Update DM_LOAN_BALANCES_MONTHLY DLM set (NET_EXCESS_RECEIVED,NET_EXCESS_ADJUSTED) = (Select Sum(Amount/100),Sum(UTILISEDAMT/100)
                                                               from Finexcessamount FR where FR.Finreference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID)
                                                               where exists (Select 1
                                                               from Finexcessamount FR where FR.Finreference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID);
ERRORSTEP := 'STEP8';
Update DM_LOAN_BALANCES_MONTHLY DLM set (PDC_SWAP_CHARGES_RECEIVABLE,PDC_SWAP_CHARGES_RECEIVED) = (Select SUM(ACTUALAMOUNT/100) PDC_SWAP_CHARGES_RECEIVABLE, 
                                                                          SUM(PAIDAMOUNT/100) PDC_SWAP_CHARGES_RECEIVED from Finfeedetail T1 
                                                                          Inner Join FeeTypes T2 on T1.FeeTypeID = T2.FeeTypeID 
                                                                          where HostFeeTypeCode = '74' and T1.finreference=DLM.AGREEMENTNO and DLM.BATCHID = BATCHID )
                                                               where exists (Select 1
                                                               from Finfeedetail T1 
                                                                          Inner Join FeeTypes T2 on T1.FeeTypeID = T2.FeeTypeID 
                                                                          where HostFeeTypeCode = '74' and T1.finreference=DLM.AGREEMENTNO and DLM.BATCHID = BATCHID);      
  ERRORSTEP := 'STEP9';                                                                        
Update DM_LOAN_BALANCES_MONTHLY DLM set (FORECLOSURE_CHARGES_DUE,FORECLOSURE_CHARGES_RECEIVED) = (  Select SUM(ACTUALAMOUNT/100) PDC_SWAP_CHARGES_RECEIVABLE, 
                                                                                                     SUM(PAIDAMOUNT/100) PDC_SWAP_CHARGES_RECEIVED from Finfeedetail T1 
                                                                                                     where finevent ='EARLSTL' and T1.finreference=DLM.AGREEMENTNO and DLM.BATCHID = BATCHID )
                                                                                                   where exists (Select 1 from Finfeedetail T1 
                                                                                                     where finevent ='EARLSTL' and T1.finreference=DLM.AGREEMENTNO and DLM.BATCHID = BATCHID );                                                                
     ERRORSTEP := 'STEP10';                                                                     
Update DM_LOAN_BALANCES_MONTHLY DLM set BOUNCE_CHARGES_RECEIVED = FORECLOSURE_CHARGES_RECEIVED;
ERRORSTEP := 'STEP111';
Update DM_LOAN_BALANCES_MONTHLY DLM set FIRST_DUE_DATE = (Select min(SCHDATE)
                                                               from Finscheduledetails FS where REPAYONSCHDATE=1 and REPAYAMOUNT>PARTIALPAIDAMT and FS.Finreference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID)
                                                               where exists (Select 1
                                                               from  Finscheduledetails FS where REPAYONSCHDATE=1 and REPAYAMOUNT>PARTIALPAIDAMT and FS.Finreference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID);
    ERRORSTEP := 'STEP12';                                                           
Update DM_LOAN_BALANCES_MONTHLY DLM set REPAYMENT_MODE = (Select CASE WHEN M.MANDATETYPE='ECS' THEN 'E' WHEN M.MANDATETYPE='NACH' THEN 'Z' WHEN 
			M.MANDATETYPE='DDM' THEN 'A' END 
                                                               from mandates M inner join 
                                                                    financemain FM ON FM.MANDATEID =M.MANDATEID  and FM.Finreference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID)
                                                               where exists (Select 1
                                                               from mandates M inner join 
                                                                    financemain FM ON FM.MANDATEID =M.MANDATEID  and FM.Finreference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID);                                                               
ERRORSTEP := 'STEP13';
Update DM_LOAN_BALANCES_MONTHLY DLM set BOUNCE_CHARGE_WAIVE = (Select SUM(R.WAIVEDAMOUNT/100) 
                                                               from Finreceiptheader FH inner join 
                                                                    Receiptallocationdetail R ON FH.Receiptid =R.Receiptid  
                                                                where r.allocationtype='BOUNCE' and FH.reference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID)
                                                               where exists (Select 1
                                                               from Finreceiptheader FH inner join 
                                                                    Receiptallocationdetail R ON FH.Receiptid =R.Receiptid  
                                                                where r.allocationtype='BOUNCE' and FH.reference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID);  
ERRORSTEP := 'STEP14';                                                                
Update DM_LOAN_BALANCES_MONTHLY DLM set LPP_CHARGE_WAIVE = (Select SUM(R.WAIVEDAMOUNT/100) 
                                                               from Finreceiptheader FH inner join 
                                                                    Receiptallocationdetail R ON FH.Receiptid =R.Receiptid  
                                                                where r.allocationtype='ODC' and FH.reference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID)
                                                               where exists (Select 1
                                                               from Finreceiptheader FH inner join 
                                                                    Receiptallocationdetail R ON FH.Receiptid =R.Receiptid  
                                                          where r.allocationtype='ODC' and FH.reference = DLM.AGREEMENTNO and DLM.BATCHID = BATCHID);  
EXCEPTION  
   WHEN OTHERS THEN
   ERRORDESC := SQLERRM;
   ERRORCODE := SQLCODE; 
END SP_DM_LOAN_BALANCES_MONTHLY;
DonotStop