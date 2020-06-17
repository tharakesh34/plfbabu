DROP TABLE DR_EMIHOLIDAY;
CREATE TABLE DR_EMIHOLIDAY
   (	
    FINREFERENCE VARCHAR2(20 BYTE),
    EHSTARTDATE DATE,
    EHENDDATE DATE,
    EHINST NUMBER(2,0),
    EHMETHOD VARCHAR2(2 BYTE),
    BOUNCEWAIVER NUMBER (1,0),
    APPDATE DATE,
    EHSTATUS VARCHAR2(1 BYTE),
    EHSTATUSREMARKS VARCHAR2(50 BYTE),
    BRANCHCODE VARCHAR2(8 BYTE),
    PRODUCTTYPE VARCHAR2(8 BYTE),
    FINTYPE VARCHAR2(8 BYTE),
    OLDBUCKET NUMBER(3,0),
    NEWBUCKET NUMBER(3,0),
    DPD NUMBER(4,0),
    OLDEMIOS NUMBER(18,0),
    NEWEMIOS NUMBER(18,0),
    OLDBALTENURE NUMBER(3,0),
    NEWBALTENURE NUMBER(3,0),
    OLDMATURITY DATE,
    NEWMATURITY DATE,
    LASTBILLEDDATE DATE,
    LASTBILLEDINSTNO NUMBER(3,0),
    ACTLOANAMOUNT NUMBER(18,0),
    OLDTENURE NUMBER(3,0),
    NEWTENURE NUMBER(3,0),
    OLDINTEREST NUMBER(18,0),
    NEWINTEREST NUMBER(18,0),
    CPZINTEREST NUMBER(18,0),
    OLDMAXUNPLANNEDEMI NUMBER(3,0),
    NEWMAXUNPLANNEDEMI NUMBER(3,0),
    OLDAVAILEDUNPLANEMI NUMBER(3,0),
    NEWAVAILEDUNPLANEMI NUMBER(3,0),
    OLDFINALEMI NUMBER(18,0),
    NEWFINALEMI NUMBER(18,0),
    LASTMNTDATE DATE
   );
   
   CREATE INDEX IDX_ ON DR_EMIHOLIDAY (FINREFERENCE);
  
   
INSERT INTO DR_EMIHOLIDAY (FINREFERENCE, EHSTARTDATE, EHENDDATE, EHINST, EHMETHOD) VALUES ('H4F4RLP0125149', '01-02-20', '31-05-20', 2, 'CT');



DELETE FROM RMTTRANSACTIONENTRY WHERE ACCOUNTSETID IN (Select AccountSetId from RMTAccountingSet WHERE AccountSetCode ='REAGING' AND EVentCode = 'REAGING'); 
DELETE FROM RMTACCOUNTINGSET WHERE AccountSetCode = 'REAGING' AND EventCode = 'REAGING'; 


Delete FROM BMTAMOUNTCODES Where ALLOWEDEVENT  = 'REAGING' And AMOUNTCODE = 'ae_instChg';
Delete FROM BMTAMOUNTCODES Where ALLOWEDEVENT  = 'REAGING' And AMOUNTCODE = 'ae_instIntChg';
Delete FROM BMTAMOUNTCODES Where ALLOWEDEVENT  = 'REAGING' And AMOUNTCODE = 'ae_instPriChg';
Delete FROM BMTAMOUNTCODES Where ALLOWEDEVENT  = 'REAGING' And AMOUNTCODE = 'ae_pastCpzChg';
Insert into BMTAMOUNTCODES values ('REAGING',0,'ae_instChg','Installment Changed now','1',1,1000,Current_TimeStamp,'Approved',null,null,null,null,null,0);
Insert into BMTAMOUNTCODES values ('REAGING',0,'ae_instIntChg','Installment Interest Reverse Amount','1',1,1000,Current_TimeStamp,'Approved',null,null,null,null,null,0);
Insert into BMTAMOUNTCODES values ('REAGING',0,'ae_instPriChg','Installment Principal Reverse Amount','1',1,1000,Current_TimeStamp,'Approved',null,null,null,null,null,0);
Insert into BMTAMOUNTCODES values ('REAGING',0,'ae_pastCpzChg','Overdue Capitalized Amount','1',1,1000,Current_TimeStamp,'Approved',null,null,null,null,null,0);

INSERT INTO RMTAccountingSet VALUES ((Select MAX(AccountSetid)+1 FROM RMTAccountingSet ), 'REAGING', 'REAGING', 'Reaging Accounting', '0', '0', 1, 1001, CURRENT_TIMESTAMP, 'Approved', '', '', '', '', '', 0, '', '', '');
INSERT INTO RMTTransactionEntry VALUES ((Select MAX(AccountSetid) FROM RMTAccountingSet ), 10, 'LOAN A/C', 'D', '0', 'LOA', '', 'BUILD', 'GLPREFIX', '010', '510', 'Result=ae_instPriChg;', NULL, '', 'D', '0', '0', 1, 0, CURRENT_TIMESTAMP, 'Approved', '', '', '', '', '', 0, 'E', 0);
INSERT INTO RMTTransactionEntry VALUES ((Select MAX(AccountSetid) FROM RMTAccountingSet ), 20, 'Interest Receivable Amount', 'D', '0', 'INTRE', '', 'BUILD', 'GLPREFIX', '010', '510', 'Result=ae_instIntChg;', NULL, '', 'D', '0', '0', 1, 0, CURRENT_TIMESTAMP, 'Approved', '', '', '', '', '', 0, 'E', 0);
INSERT INTO RMTTransactionEntry VALUES ((Select MAX(AccountSetid) FROM RMTAccountingSet ), 30, 'EMI Receivable Principal Amount', 'C', '0', 'PRICIBILD', '', 'BUILD', 'GLPREFIX', '510', '010', 'Result=ae_instPriChg;', NULL, '', 'D', '0', '0', 1, 0, CURRENT_TIMESTAMP, 'Approved', '', '', '', '', '', 0, 'E', 0);
INSERT INTO RMTTransactionEntry VALUES ((Select MAX(AccountSetid) FROM RMTAccountingSet ), 40, 'EMI Receivable Interest Amount', 'C', '0', 'INTRBILD', '', 'BUILD', 'GLPREFIX', '510', '010', 'Result=ae_instIntChg;', NULL, '', 'D', '0', '0', 1, 0, CURRENT_TIMESTAMP, 'Approved', '', '', '', '', '', 0, 'E', 0);
INSERT INTO RMTTransactionEntry VALUES ((Select MAX(AccountSetid) FROM RMTAccountingSet ), 50, 'LOAN A/C for Capitalized Amount', 'D', '0', 'LOA', '', 'BUILD', 'GLPREFIX', '010', '510', 'Result=ae_pastCpzChg;', NULL, '', 'D', '0', '0', 1, 0, CURRENT_TIMESTAMP, 'Approved', '', '', '', '', '', 0, 'E', 0);
INSERT INTO RMTTransactionEntry VALUES ((Select MAX(AccountSetid) FROM RMTAccountingSet ), 60, 'Capitalized Amount for Reverse Interest', 'C', '0', 'INTRE', '', 'BUILD', 'GLPREFIX', '510', '010', 'Result=ae_pastCpzChg;', NULL, '', 'D', '0', '0', 1, 0, CURRENT_TIMESTAMP, 'Approved', '', '', '', '', '', 0, 'E', 0);



DELETE FROM RMTTRANSACTIONENTRY WHERE ACCOUNTSETID IN (Select AccountSetId from RMTAccountingSet WHERE AccountSetCode ='INSTDATE' AND EVentCode = 'INSTDATE');
DELETE FROM RMTACCOUNTINGSET WHERE AccountSetCode = 'INSTDATE' AND EventCode = 'INSTDATE'; 

INSERT INTO RMTAccountingSet VALUES ((Select MAX(AccountSetid)+1 FROM RMTAccountingSet ), 'INSTDATE', 'INSTDATE', 'Installment Due date Accounting', '0', '0', 1, 1001, CURRENT_TIMESTAMP, 'Approved', '', '', '', '', '', 0, '', '', '');
INSERT INTO RMTTransactionEntry VALUES ((Select MAX(AccountSetid) FROM RMTAccountingSet ), 50, 'LOAN A/C for Capitalized Amount', 'D', '0', 'LOA', '', 'BUILD', 'GLPREFIX', '010', '510', 'Result=ae_instcpz;', NULL, '', 'D', '0', '0', 1, 0, CURRENT_TIMESTAMP, 'Approved', '', '', '', '', '', 0, 'E', 0);
INSERT INTO RMTTransactionEntry VALUES ((Select MAX(AccountSetid) FROM RMTAccountingSet ), 60, 'Capitalized Amount for Interest', 'C', '0', 'INTRE', '', 'BUILD', 'GLPREFIX', '510', '010', 'Result=ae_instcpz;', NULL, '', 'D', '0', '0', 1, 0, CURRENT_TIMESTAMP, 'Approved', '', '', '', '', '', 0, 'E', 0);


DELETE FROM FINTYPEACCOUNTING WHERE EVENT = 'REAGING';
INSERT INTO FINTYPEACCOUNTING VALUES('TLFLT'	,'REAGING',(Select AccountSetID from RMTAccountingSet where eventcode = 'REAGING' AND AccountSetCode = 'REAGING'),1,'1000',CURRENT_TIMESTAMP,'Approved','','','','','','0',1);
INSERT INTO FINTYPEACCOUNTING VALUES('STFIX'    ,'REAGING',(Select AccountSetID from RMTAccountingSet where eventcode = 'REAGING' AND AccountSetCode = 'REAGING'),1,'1000',CURRENT_TIMESTAMP,'Approved','','','','','','0',1);
INSERT INTO FINTYPEACCOUNTING VALUES('STFLT'    ,'REAGING',(Select AccountSetID from RMTAccountingSet where eventcode = 'REAGING' AND AccountSetCode = 'REAGING'),1,'1000',CURRENT_TIMESTAMP,'Approved','','','','','','0',1);
INSERT INTO FINTYPEACCOUNTING VALUES('LAS'      ,'REAGING',(Select AccountSetID from RMTAccountingSet where eventcode = 'REAGING' AND AccountSetCode = 'REAGING'),1,'1000',CURRENT_TIMESTAMP,'Approved','','','','','','0',1);
INSERT INTO FINTYPEACCOUNTING VALUES('HL'       ,'REAGING',(Select AccountSetID from RMTAccountingSet where eventcode = 'REAGING' AND AccountSetCode = 'REAGING'),1,'1000',CURRENT_TIMESTAMP,'Approved','','','','','','0',1);
INSERT INTO FINTYPEACCOUNTING VALUES('TLFIX'    ,'REAGING',(Select AccountSetID from RMTAccountingSet where eventcode = 'REAGING' AND AccountSetCode = 'REAGING'),1,'1000',CURRENT_TIMESTAMP,'Approved','','','','','','0',1);
INSERT INTO FINTYPEACCOUNTING VALUES('HJO'      ,'REAGING',(Select AccountSetID from RMTAccountingSet where eventcode = 'REAGING' AND AccountSetCode = 'REAGING'),1,'1000',CURRENT_TIMESTAMP,'Approved','','','','','','0',1);
INSERT INTO FINTYPEACCOUNTING VALUES('SANCFLT'  ,'REAGING',(Select AccountSetID from RMTAccountingSet where eventcode = 'REAGING' AND AccountSetCode = 'REAGING'),1,'1000',CURRENT_TIMESTAMP,'Approved','','','','','','0',1);
INSERT INTO FINTYPEACCOUNTING VALUES('KOTAKLTY' ,'REAGING',(Select AccountSetID from RMTAccountingSet where eventcode = 'REAGING' AND AccountSetCode = 'REAGING'),1,'1000',CURRENT_TIMESTAMP,'Approved','','','','','','0',1);
INSERT INTO FINTYPEACCOUNTING VALUES('LDIV'     ,'REAGING',(Select AccountSetID from RMTAccountingSet where eventcode = 'REAGING' AND AccountSetCode = 'REAGING'),1,'1000',CURRENT_TIMESTAMP,'Approved','','','','','','0',1);
INSERT INTO FINTYPEACCOUNTING VALUES('LPAY'     ,'REAGING',(Select AccountSetID from RMTAccountingSet where eventcode = 'REAGING' AND AccountSetCode = 'REAGING'),1,'1000',CURRENT_TIMESTAMP,'Approved','','','','','','0',1);
INSERT INTO FINTYPEACCOUNTING VALUES('SANCFIX'  ,'REAGING',(Select AccountSetID from RMTAccountingSet where eventcode = 'REAGING' AND AccountSetCode = 'REAGING'),1,'1000',CURRENT_TIMESTAMP,'Approved','','','','','','0',1);
INSERT INTO FINTYPEACCOUNTING VALUES('SSJKOTAK' ,'REAGING',(Select AccountSetID from RMTAccountingSet where eventcode = 'REAGING' AND AccountSetCode = 'REAGING'),1,'1000',CURRENT_TIMESTAMP,'Approved','','','','','','0',1);
INSERT INTO FINTYPEACCOUNTING VALUES('LAPC'     ,'REAGING',(Select AccountSetID from RMTAccountingSet where eventcode = 'REAGING' AND AccountSetCode = 'REAGING'),1,'1000',CURRENT_TIMESTAMP,'Approved','','','','','','0',1);
INSERT INTO FINTYPEACCOUNTING VALUES('LRDFLT'   ,'REAGING',(Select AccountSetID from RMTAccountingSet where eventcode = 'REAGING' AND AccountSetCode = 'REAGING'),1,'1000',CURRENT_TIMESTAMP,'Approved','','','','','','0',1);
INSERT INTO FINTYPEACCOUNTING VALUES('BIKE'     ,'REAGING',(Select AccountSetID from RMTAccountingSet where eventcode = 'REAGING' AND AccountSetCode = 'REAGING'),1,'1000',CURRENT_TIMESTAMP,'Approved','','','','','','0',1);
INSERT INTO FINTYPEACCOUNTING VALUES('LDIV1'    ,'REAGING',(Select AccountSetID from RMTAccountingSet where eventcode = 'REAGING' AND AccountSetCode = 'REAGING'),1,'1000',CURRENT_TIMESTAMP,'Approved','','','','','','0',1);
INSERT INTO FINTYPEACCOUNTING VALUES('LAP1'     ,'REAGING',(Select AccountSetID from RMTAccountingSet where eventcode = 'REAGING' AND AccountSetCode = 'REAGING'),1,'1000',CURRENT_TIMESTAMP,'Approved','','','','','','0',1);



   
-- Sample scripts
INSERT INTO DR_EMIHOLIDAY (FINREFERENCE, EHSTARTDATE, EHENDDATE, EHINST, EHMETHOD) VALUES ('PPLLRT6789', '16-06-21', '16-12-21', 1, 'CT');
INSERT INTO DR_EMIHOLIDAY (FINREFERENCE, EHSTARTDATE, EHENDDATE, EHINST, EHMETHOD) VALUES ('PLKT00098', '16-12-21', '16-12-22', 1, 'CT');
INSERT INTO DR_EMIHOLIDAY (FINREFERENCE, EHSTARTDATE, EHENDDATE, EHINST, EHMETHOD) VALUES ('PLKT00099', '16-03-21', '16-09-21', 2, 'CE');


create table financemain_EH as select * from financemain where finreference in  (select finreference from dr_emiholiday where ehstatus is null);
create table finpftdetails_EH as select * from finpftdetails where finreference in (select finreference from dr_emiholiday where ehstatus is null);
create table finscheduledetails_EH as select * from finscheduledetails where finreference in  (select finreference from dr_emiholiday where ehstatus is null);

create table finrepayinstruction_EH as select * from finrepayinstruction where finreference in  (select finreference from dr_emiholiday where ehstatus is null);
create table postings_EH as select * from postings   where finreference in (select finreference from dr_emiholiday where ehstatus is null);
create table finoddetails_EH as select * from finoddetails where finreference in  (select finreference from dr_emiholiday where ehstatus is null);




Delete from FinODDetails where FInreference IN (Select FinReference from DR_EMIHOLIDAY);
Delete from FinRepayInstruction where FInreference IN (Select FinReference from DR_EMIHOLIDAY);
Delete from FinScheduleDetails where FInreference IN (Select FinReference from DR_EMIHOLIDAY);
Delete from FinPftDetails where FInreference IN (Select FinReference from DR_EMIHOLIDAY);
MERGE INTO financemain T1
	USING (select 
		 T2.*
         from financemain_EH T2 
      ) T2
	ON (T1.FINREFERENCE = T2.FINREFERENCE) 
WHEN MATCHED THEN UPDATE SET T1.	InvestmentRef	=	T2.	InvestmentRef,T1.	CustID	=	T2.	CustID,T1.	FinBranch	=	T2.	FinBranch,T1.	FinSourceID	=	T2.	FinSourceID,T1.	NumberOfTerms	=	T2.	NumberOfTerms,T1.	GrcPeriodEndDate	=	T2.	GrcPeriodEndDate,T1.	AllowGrcPeriod	=	T2.	AllowGrcPeriod,T1.	GraceBaseRate	=	T2.	GraceBaseRate,T1.	GraceSpecialRate	=	T2.	GraceSpecialRate,T1.	GrcMargin	=	T2.	GrcMargin,T1.	GrcPftRate	=	T2.	GrcPftRate,T1.	GrcPftFrq	=	T2.	GrcPftFrq,T1.	NextGrcPftDate	=	T2.	NextGrcPftDate,T1.	AllowGrcPftRvw	=	T2.	AllowGrcPftRvw,T1.	GrcPftRvwFrq	=	T2.	GrcPftRvwFrq,T1.	NextGrcPftRvwDate	=	T2.	NextGrcPftRvwDate,T1.	AllowGrcCpz	=	T2.	AllowGrcCpz,T1.	GrcCpzFrq	=	T2.	GrcCpzFrq,T1.	AllowGrcRepay	=	T2.	AllowGrcRepay,T1.	GrcSchdMthd	=	T2.	GrcSchdMthd,T1.	RecalType	=	T2.	RecalType,T1.	NextGrcCpzDate	=	T2.	NextGrcCpzDate,T1.	RepayBaseRate	=	T2.	RepayBaseRate,T1.	RepaySpecialRate	=	T2.	RepaySpecialRate,T1.	RepayMargin	=	T2.	RepayMargin,T1.	RepayProfitRate	=	T2.	RepayProfitRate,T1.	FinRepayPftOnFrq	=	T2.	FinRepayPftOnFrq,T1.	RepayFrq	=	T2.	RepayFrq,T1.	NextRepayDate	=	T2.	NextRepayDate,T1.	RepayPftFrq	=	T2.	RepayPftFrq,T1.	NextRepayPftDate	=	T2.	NextRepayPftDate,T1.	AllowRepayRvw	=	T2.	AllowRepayRvw,T1.	RepayRvwFrq	=	T2.	RepayRvwFrq,T1.	NextRepayRvwDate	=	T2.	NextRepayRvwDate,T1.	AllowRepayCpz	=	T2.	AllowRepayCpz,T1.	RepayCpzFrq	=	T2.	RepayCpzFrq,T1.	NextRepayCpzDate	=	T2.	NextRepayCpzDate,T1.	MaturityDate	=	T2.	MaturityDate,T1.	CpzAtGraceEnd	=	T2.	CpzAtGraceEnd,T1.	DownPayment	=	T2.	DownPayment,T1.	DownPayBank	=	T2.	DownPayBank,T1.	DownPaySupl	=	T2.	DownPaySupl,T1.	DownPayAccount	=	T2.	DownPayAccount,T1.	GraceFlatAmount	=	T2.	GraceFlatAmount,T1.	ReqRepayAmount	=	T2.	ReqRepayAmount,T1.	TotalProfit	=	T2.	TotalProfit,T1.	TotalCpz	=	T2.	TotalCpz,T1.	TotalGrossPft	=	T2.	TotalGrossPft,T1.	TotalGrossGrcPft	=	T2.	TotalGrossGrcPft,T1.	TotalGracePft	=	T2.	TotalGracePft,T1.	TotalGraceCpz	=	T2.	TotalGraceCpz,T1.	TotalRepayAmt	=	T2.	TotalRepayAmt,T1.	GrcRateBasis	=	T2.	GrcRateBasis,T1.	RepayRateBasis	=	T2.	RepayRateBasis,T1.	FinType	=	T2.	FinType,T1.	FinRemarks	=	T2.	FinRemarks,T1.	FinCcy	=	T2.	FinCcy,T1.	ScheduleMethod	=	T2.	ScheduleMethod,T1.	ProfitDaysBasis	=	T2.	ProfitDaysBasis,T1.	ReqMaturity	=	T2.	ReqMaturity,T1.	CalTerms	=	T2.	CalTerms,T1.	CalMaturity	=	T2.	CalMaturity,T1.	FirstRepay	=	T2.	FirstRepay,T1.	LastRepay	=	T2.	LastRepay,T1.	FinStartDate	=	T2.	FinStartDate,T1.	FinContractDate	=	T2.	FinContractDate,T1.	FinAmount	=	T2.	FinAmount,T1.	FinRepaymentAmount	=	T2.	FinRepaymentAmount,T1.	FeeChargeAmt	=	T2.	FeeChargeAmt,T1.	Defferments	=	T2.	Defferments,T1.	PlanDeferCount	=	T2.	PlanDeferCount,T1.	FinAssetValue	=	T2.	FinAssetValue,T1.	DisbAccountId	=	T2.	DisbAccountId,T1.	RepayAccountId	=	T2.	RepayAccountId,T1.	FinAccount	=	T2.	FinAccount,T1.	FinCustPftAccount	=	T2.	FinCustPftAccount,T1.	FinCurrAssetValue	=	T2.	FinCurrAssetValue,T1.	FinCommitmentRef	=	T2.	FinCommitmentRef,T1.	DepreciationFrq	=	T2.	DepreciationFrq,T1.	NextDepDate	=	T2.	NextDepDate,T1.	LastDepDate	=	T2.	LastDepDate,T1.	AllowedDefRpyChange	=	T2.	AllowedDefRpyChange,T1.	AvailedDefRpyChange	=	T2.	AvailedDefRpyChange,T1.	AllowedDefFrqChange	=	T2.	AllowedDefFrqChange,T1.	AvailedDefFrqChange	=	T2.	AvailedDefFrqChange,T1.	DedupFound	=	T2.	DedupFound,T1.	SkipDedup	=	T2.	SkipDedup,T1.	Blacklisted	=	T2.	Blacklisted,T1.	LastRepayDate	=	T2.	LastRepayDate,T1.	LastRepayPftDate	=	T2.	LastRepayPftDate,T1.	LastRepayRvwDate	=	T2.	LastRepayRvwDate,T1.	LastRepayCpzDate	=	T2.	LastRepayCpzDate,T1.	ClosingStatus	=	T2.	ClosingStatus,T1.	FinApprovedDate	=	T2.	FinApprovedDate,T1.	FinIsActive	=	T2.	FinIsActive,T1.	LimitValid	=	T2.	LimitValid,T1.	OverrideLimit	=	T2.	OverrideLimit,T1.	AnualizedPercRate	=	T2.	AnualizedPercRate,T1.	EffectiveRateOfReturn	=	T2.	EffectiveRateOfReturn,T1.	MigratedFinance	=	T2.	MigratedFinance,T1.	ScheduleMaintained	=	T2.	ScheduleMaintained,T1.	ScheduleRegenerated	=	T2.	ScheduleRegenerated,T1.	FinPurpose	=	T2.	FinPurpose,T1.	FinStatus	=	T2.	FinStatus,T1.	FinStsReason	=	T2.	FinStsReason,T1.	CustDSR	=	T2.	CustDSR,T1.	JointAccount	=	T2.	JointAccount,T1.	JointCustId	=	T2.	JointCustId,T1.	SecurityDeposit	=	T2.	SecurityDeposit,T1.	Version	=	T2.	Version,T1.	LastMntBy	=	T2.	LastMntBy,T1.	LastMntOn	=	T2.	LastMntOn,T1.	RecordStatus	=	T2.	RecordStatus,T1.	RoleCode	=	T2.	RoleCode,T1.	NextRoleCode	=	T2.	NextRoleCode,T1.	TaskId	=	T2.	TaskId,T1.	NextTaskId	=	T2.	NextTaskId,T1.	RecordType	=	T2.	RecordType,T1.	WorkflowId	=	T2.	WorkflowId,T1.	SecurityCollateral	=	T2.	SecurityCollateral,T1.	Approved	=	T2.	Approved,T1.	Discrepancy	=	T2.	Discrepancy,T1.	LimitApproved	=	T2.	LimitApproved,T1.	GraceTerms	=	T2.	GraceTerms,T1.	RcdMaintainSts	=	T2.	RcdMaintainSts,T1.	FinRepayMethod	=	T2.	FinRepayMethod,T1.	GrcProfitDaysBasis	=	T2.	GrcProfitDaysBasis,T1.	StepFinance	=	T2.	StepFinance,T1.	AlwManualSteps	=	T2.	AlwManualSteps,T1.	StepPolicy	=	T2.	StepPolicy,T1.	NoOfSteps	=	T2.	NoOfSteps,T1.	LinkedFinRef	=	T2.	LinkedFinRef,T1.	NextUserId	=	T2.	NextUserId,T1.	Priority	=	T2.	Priority,T1.	GrcMinRate	=	T2.	GrcMinRate,T1.	GrcMaxRate	=	T2.	GrcMaxRate,T1.	RpyMinRate	=	T2.	RpyMinRate,T1.	RpyMaxRate	=	T2.	RpyMaxRate,T1.	DeviationApproval	=	T2.	DeviationApproval,T1.	ManualSchedule	=	T2.	ManualSchedule,T1.	TakeOverFinance	=	T2.	TakeOverFinance,T1.	GrcAdvBaseRate	=	T2.	GrcAdvBaseRate,T1.	GrcAdvMargin	=	T2.	GrcAdvMargin,T1.	GrcAdvPftRate	=	T2.	GrcAdvPftRate,T1.	RpyAdvBaseRate	=	T2.	RpyAdvBaseRate,T1.	RpyAdvMargin	=	T2.	RpyAdvMargin,T1.	RpyAdvPftRate	=	T2.	RpyAdvPftRate,T1.	SupplementRent	=	T2.	SupplementRent,T1.	IncreasedCost	=	T2.	IncreasedCost,T1.	CreditInsAmt	=	T2.	CreditInsAmt,T1.	RolloverFrq	=	T2.	RolloverFrq,T1.	NextRolloverDate	=	T2.	NextRolloverDate,T1.	InitiateUser	=	T2.	InitiateUser,T1.	bankName	=	T2.	bankName,T1.	iban	=	T2.	iban,T1.	accountType	=	T2.	accountType,T1.	DdaReferenceNo	=	T2.	DdaReferenceNo,T1.	ShariaStatus	=	T2.	ShariaStatus,T1.	InitiateDate	=	T2.	InitiateDate,T1.	MMAId	=	T2.	MMAId,T1.	AccountsOfficer	=	T2.	AccountsOfficer,T1.	FinPreApprovedRef	=	T2.	FinPreApprovedRef,T1.	feeAccountId	=	T2.	feeAccountId,T1.	FinCancelAc	=	T2.	FinCancelAc,T1.	DSACode	=	T2.	DSACode,T1.	MinDownpayPerc	=	T2.	MinDownpayPerc,T1.	TDSApplicable	=	T2.	TDSApplicable,T1.	FinLimitRef	=	T2.	FinLimitRef,T1.	MandateID	=	T2.	MandateID,T1.	StepType	=	T2.	StepType,T1.	DroplineFrq	=	T2.	DroplineFrq,T1.	FirstDroplineDate	=	T2.	FirstDroplineDate,T1.	PftServicingODLimit	=	T2.	PftServicingODLimit,T1.	InsuranceAmt	=	T2.	InsuranceAmt,T1.	DeductInsDisb	=	T2.	DeductInsDisb,T1.	AlwBPI	=	T2.	AlwBPI,T1.	BpiTreatment	=	T2.	BpiTreatment,T1.	PlanEMIHAlw	=	T2.	PlanEMIHAlw,T1.	PlanEMIHMethod	=	T2.	PlanEMIHMethod,T1.	PlanEMIHMaxPerYear	=	T2.	PlanEMIHMaxPerYear,T1.	PlanEMIHMax	=	T2.	PlanEMIHMax,T1.	PlanEMIHLockPeriod	=	T2.	PlanEMIHLockPeriod,T1.	PlanEMICpz	=	T2.	PlanEMICpz,T1.	CalRoundingMode	=	T2.	CalRoundingMode,T1.	AlwMultiDisb	=	T2.	AlwMultiDisb,T1.	ApplicationNo	=	T2.	ApplicationNo,T1.	ReferralId	=	T2.	ReferralId,T1.	DmaCode	=	T2.	DmaCode,T1.	SalesDepartment	=	T2.	SalesDepartment,T1.	QuickDisb	=	T2.	QuickDisb,T1.	WifReference	=	T2.	WifReference,T1.	UnPlanEMIHLockPeriod	=	T2.	UnPlanEMIHLockPeriod,T1.	UnPlanEMICpz	=	T2.	UnPlanEMICpz,T1.	ReAgeCpz	=	T2.	ReAgeCpz,T1.	MaxUnplannedEmi	=	T2.	MaxUnplannedEmi,T1.	MaxReAgeHolidays	=	T2.	MaxReAgeHolidays,T1.	AvailedUnPlanEmi	=	T2.	AvailedUnPlanEmi,T1.	AvailedReAgeH	=	T2.	AvailedReAgeH,T1.	BpiAmount	=	T2.	BpiAmount,T1.	DeductFeeDisb	=	T2.	DeductFeeDisb,T1.	PromotionCode	=	T2.	PromotionCode,T1.	SchCalOnRvw	=	T2.	SchCalOnRvw,T1.	PastduePftCalMthd	=	T2.	PastduePftCalMthd,T1.	PastduePftMargin	=	T2.	PastduePftMargin,T1.	DroppingMethod	=	T2.	DroppingMethod,T1.	RateChgAnyDay	=	T2.	RateChgAnyDay,T1.	RvwRateApplFor	=	T2.	RvwRateApplFor,T1.	DueBucket	=	T2.	DueBucket,T1.	ReAgeBucket	=	T2.	ReAgeBucket,T1.	FinCategory	=	T2.	FinCategory,T1.	ProductCategory	=	T2.	ProductCategory,T1.	RestructureLoan	=	T2.	RestructureLoan,T1.	OldFinReference	=	T2.	OldFinReference,T1.	RoundingTarget	=	T2.	RoundingTarget,T1.	EligibilityMethod	=	T2.	EligibilityMethod,T1.	samplingRequired	=	T2.	samplingRequired,T1.	employeeName	=	T2.	employeeName,T1.	GrcMaxAmount	=	T2.	GrcMaxAmount,T1.	Connector	=	T2.	Connector,T1.	AdvanceEMI	=	T2.	AdvanceEMI,T1.	AdvEMITerms	=	T2.	AdvEMITerms,T1.	bpipftdaysbasis	=	T2.	bpipftdaysbasis,T1.	legalrequired	=	T2.	legalrequired,T1.	fixedratetenor	=	T2.	fixedratetenor,T1.	fixedtenorrate	=	T2.	fixedtenorrate,T1.	ProcessAttributes	=	T2.	ProcessAttributes,T1.	businessvertical	=	T2.	businessvertical,T1.	BPI_NoSchd	=	T2.	BPI_NoSchd,T1.	GrcAdvType	=	T2.	GrcAdvType,T1.	GrcAdvTerms	=	T2.	GrcAdvTerms,T1.	AdvType	=	T2.	AdvType,T1.	AdvTerms	=	T2.	AdvTerms,T1.	AdvStage	=	T2.	AdvStage,T1.	TDSPercentage	=	T2.	TDSPercentage,T1.	TDSStartDate	=	T2.	TDSStartDate,T1.	TDSEndDate	=	T2.	TDSEndDate,T1.	TDSLimitAmt	=	T2.	TDSLimitAmt,T1.	ClosedDate	=	T2.	ClosedDate
;

Insert into FinScheduleDetails Select * from FinScheduleDetails_EH;
Insert into FinODDetails Select * from FinODDetails_EH;
Insert into FinRepayInstruction Select * from FinRepayInstruction_EH;
Insert into FinPftDetails Select * from FinPftDetails_EH;

 


