create or replace PROCEDURE CREATE_CUSTOMER_IN_FINNONE(
  P_FINN_CUSTID IN	VARCHAR2,
P_SOURCE_SYSTEM IN	VARCHAR2,
P_CUSTOMERNAME IN	VARCHAR2,
P_CONSTID IN	NUMBER,
P_INDUSTRYID IN VARCHAR2,
P_CATEGORYID IN	NUMBER,
P_SPOUSENAME IN	VARCHAR2,
P_INDV_CORP_FLAG IN	VARCHAR2,
P_FNAME IN	VARCHAR2,
P_MNAME IN	VARCHAR2,
P_LNAME IN VARCHAR2,
P_DOB	IN DATE,
P_SEX IN	VARCHAR2,
P_P_INCOME_SOURCE IN	VARCHAR2,
P_YEARS_CURR_JOB IN	DATE,
P_COR_DOI IN	DATE,
P_MP_AKERID IN VARCHAR2,
P_MAKERDATE IN DATE,
P_P_AUTHID IN	VARCHAR2,
P_AUTHDATE IN	DATE,
P_ACCOTYPE IN	VARCHAR2,
P_AP_CCOCATG IN	VARCHAR2,
P_DATELASTUPDT IN	DATE,
P_P_NATIONALID IN	VARCHAR2,
P_PASSPORTNO IN	VARCHAR2,
P_NATIONALITY IN	VARCHAR2,
P_PP_AN_NO IN	VARCHAR2,
P_REGIONID IN NUMBER,
P_BANK_TYPE IN	VARCHAR2,
P_ENTITYFLAG IN	VARCHAR2,
P_CONTACT_PERSON IN	VARCHAR2,
P_CUSTSEARCHID IN	VARCHAR2,
P_ECONOMIC_SEC_ID IN	NUMBER,
P_FRAUD_FLAG IN	VARCHAR2,
P_FRAUD_SCORE IN	NUMBER,
P_EMI_CARD_ELIG IN	VARCHAR2,
P_ADDRESS_DTL IN VARCHAR2,
P_BANK_DTL IN	VARCHAR2,
P_N_NAME IN	VARCHAR2,
P_N_ADDRESS IN	VARCHAR2,
P_N_RELATION IN	VARCHAR2,
P_N_FIELD9 IN	VARCHAR2,
P_N_FIELD10 IN	VARCHAR2,
P_INS_UPD_FLAG IN	VARCHAR2,
P_SUCCESS_REJECT OUT VARCHAR2,
P_REJECTION_REASON OUT VARCHAR2,
P_FINN_CUST_ID IN OUT VARCHAR2,
P_SFDC_CUSTOMERID IN	NUMBER,
P_BRANCHID IN	VARCHAR2
)
AS 
BEGIN

Insert Into
GCDCUSTOMERS_TEST
(FinCustId, SOURCESYSTEM, CustomerName, ConstId, IndustryId, CategoryId, Spousename, IndvCorpFlag, FName, MName,
Lname, DOB, Sex, IncomeSource, YearsOfCurrJob, DOI, MpAkerId, MakerDate, AuthId, AuthDate, AccType, ApCcocatg, DateLastUpdate, NationalId, 
PassportNo, Nationality, PanNo, RegionId, BankType, EntityFlag, ContactPerson, CustSearchId, SectorId, FraudFlag, FraudScore,
EmiCardElig, AddressDetail, BankDetail, NomineeName, NomineeAddress, NomineeRelationship, Field9, Field10, InsertUpdateFlag,
StatusFromFinnOne, RejectionReason, FinnCustId, SfdcCustomerId, BranchId)

Values(P_FINN_CUSTID, P_SOURCE_SYSTEM, P_CUSTOMERNAME, P_CONSTID, P_INDUSTRYID, P_CATEGORYID, P_SPOUSENAME, P_INDV_CORP_FLAG, P_FNAME, P_MNAME, P_LNAME,
P_DOB,P_SEX, P_P_INCOME_SOURCE, P_YEARS_CURR_JOB, P_COR_DOI, P_MP_AKERID, P_MAKERDATE, P_P_AUTHID, P_AUTHDATE, P_ACCOTYPE, P_AP_CCOCATG, P_DATELASTUPDT, P_P_NATIONALID,
P_PASSPORTNO, P_NATIONALITY, P_PP_AN_NO, P_REGIONID, P_BANK_TYPE, P_ENTITYFLAG, P_CONTACT_PERSON, P_CUSTSEARCHID, P_ECONOMIC_SEC_ID, P_FRAUD_FLAG, P_FRAUD_SCORE, 
P_EMI_CARD_ELIG, P_ADDRESS_DTL, P_BANK_DTL, P_N_NAME, P_N_ADDRESS, P_N_RELATION, P_N_FIELD9, P_N_FIELD10, P_INS_UPD_FLAG, 
P_SUCCESS_REJECT, P_REJECTION_REASON, P_FINN_CUST_ID, P_SFDC_CUSTOMERID, P_BRANCHID);
COMMIT;

P_SUCCESS_REJECT := 'S';
P_REJECTION_REASON := '';
P_FINN_CUST_ID := 'AKN224T';
 dbms_output.put_line(P_SUCCESS_REJECT);
END CREATE_CUSTOMER_IN_FINNONE;
DonotStop