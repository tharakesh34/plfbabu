package com.pennanttech.pff.core.util;

public class StaticQueryUtil {
	public static final String CUSTOMER_INSERT_SQL = "Insert Into Customers"
			+ "(CustID, CustCIF, CustCoreBank, CustCtgCode, CustTypeCode, CustSalutationCode, CustFName,"
			+ " CustMName, CustLName, CustShrtName, CustFNameLclLng, CustMNameLclLng, CustLNameLclLng,"
			+ " CustShrtNameLclLng, CustDftBranch, CustGenderCode, CustDOB, CustPOB, CustCOB, CustPassportNo,"
			+ " CustMotherMaiden, CustIsMinor, CustReferedBy, CustDSA, CustDSADept, CustRO1, CustRO2, CustGroupID,"
			+ " CustSts, CustStsChgDate, CustGroupSts, CustIsBlocked, CustIsActive, CustIsClosed, CustInactiveReason,"
			+ " CustIsDecease, CustIsDormant, CustIsDelinquent, CustIsTradeFinCust,CustTradeLicenceNum ,"
			+ " CustTradeLicenceExpiry,CustPassportExpiry,CustVisaNum ,CustVisaExpiry, CustIsStaff, CustStaffID,"
			+ " CustIndustry, CustSector, CustSubSector, CustProfession, CustTotalIncome, CustMaritalSts, CustEmpSts,"
			+ " CustSegment, CustSubSegment, CustIsBlackListed, CustBLRsnCode, CustIsRejected, CustRejectedRsn,"
			+ " CustBaseCcy, CustLng, CustParentCountry, CustResdCountry, CustRiskCountry, CustNationality,"
			+ " CustClosedOn, CustStmtFrq, CustIsStmtCombined, CustStmtLastDate, CustStmtNextDate, CustStmtDispatchMode,"
			+ " CustFirstBusinessDate, CustAddlVar81, CustAddlVar82, CustAddlVar83, CustAddlVar84, CustAddlVar85,"
			+ " CustAddlVar86, CustAddlVar87, CustAddlVar88, CustAddlVar89, CustAddlDate1, CustAddlDate2, CustAddlDate3,"
			+ " CustAddlDate4, CustAddlDate5, CustAddlVar1, CustAddlVar2, CustAddlVar3, CustAddlVar4, CustAddlVar5,"
			+ " CustAddlVar6, CustAddlVar7, CustAddlVar8, CustAddlVar9, CustAddlVar10, CustAddlVar11, CustAddlDec1,"
			+ " CustAddlDec2, CustAddlDec3, CustAddlDec4, CustAddlDec5, CustAddlInt1, CustAddlInt2, CustAddlInt3, CustAddlInt4,CustAddlInt5,"
			+ " DedupFound,SkipDedup,CustTotalExpense,CustBlackListDate,NoOfDependents,CustCRCPR,CustSourceID,"
			+ " JointCust, JointCustName, JointCustDob, custRelation, ContactPersonName, EmailID, PhoneNumber, SalariedCustomer,ApplicationNo, Dnd,"
			+ " OtherCaste, OtherReligion, NatureOfBusiness, EntityType, CustResidentialSts, Qualification,"
			+ " Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId"
			+ " ,CasteId, ReligionId, SubCategory,MarginDeviation, Vip )"
			+ " Values(:CustID, :CustCIF, :CustCoreBank, :CustCtgCode, :CustTypeCode, :CustSalutationCode, :CustFName, :CustMName,"
			+ " :CustLName, :CustShrtName, :CustFNameLclLng, :CustMNameLclLng, :CustLNameLclLng, :CustShrtNameLclLng, :CustDftBranch,"
			+ " :CustGenderCode, :CustDOB, :CustPOB, :CustCOB, :CustPassportNo, :CustMotherMaiden, :CustIsMinor, :CustReferedBy,"
			+ " :CustDSA, :CustDSADept, :CustRO1, :CustRO2, :CustGroupID, :CustSts, :CustStsChgDate, :CustGroupSts, :CustIsBlocked,"
			+ " :CustIsActive, :CustIsClosed, :CustInactiveReason, :CustIsDecease, :CustIsDormant, :CustIsDelinquent,"
			+ " :CustIsTradeFinCust, :CustTradeLicenceNum ,:CustTradeLicenceExpiry, :CustPassportExpiry, :CustVisaNum , :CustVisaExpiry,"
			+ " :CustIsStaff, :CustStaffID, :CustIndustry, :CustSector, :CustSubSector, :CustProfession, :CustTotalIncome,"
			+ " :CustMaritalSts, :CustEmpSts, :CustSegment, :CustSubSegment, :CustIsBlackListed, :CustBLRsnCode, :CustIsRejected,"
			+ " :CustRejectedRsn, :CustBaseCcy, :CustLng, :CustParentCountry, :CustResdCountry, :CustRiskCountry, :CustNationality,"
			+ " :CustClosedOn, :CustStmtFrq, :CustIsStmtCombined, :CustStmtLastDate, :CustStmtNextDate, :CustStmtDispatchMode,"
			+ " :CustFirstBusinessDate, :CustAddlVar81, :CustAddlVar82, :CustAddlVar83, :CustAddlVar84, :CustAddlVar85, :CustAddlVar86,"
			+ " :CustAddlVar87, :CustAddlVar88, :CustAddlVar89, :CustAddlDate1, :CustAddlDate2, :CustAddlDate3, :CustAddlDate4,"
			+ " :CustAddlDate5, :CustAddlVar1, :CustAddlVar2, :CustAddlVar3, :CustAddlVar4, :CustAddlVar5, :CustAddlVar6, :CustAddlVar7,"
			+ " :CustAddlVar8, :CustAddlVar9, :CustAddlVar10, :CustAddlVar11, :CustAddlDec1, :CustAddlDec2, :CustAddlDec3, :CustAddlDec4,"
			+ " :CustAddlDec5, :CustAddlInt1, :CustAddlInt2, :CustAddlInt3, :CustAddlInt4, :CustAddlInt5,"
			+ " :DedupFound,:SkipDedup,:CustTotalExpense,:CustBlackListDate,:NoOfDependents,:CustCRCPR,:CustSourceID,"
			+ " :JointCust, :JointCustName, :JointCustDob, :custRelation, :ContactPersonName, :EmailID, :PhoneNumber, :SalariedCustomer, :ApplicationNo, :Dnd,"
			+ " :OtherCaste, :OtherReligion, :NatureOfBusiness, :EntityType, :CustResidentialSts, :Qualification,"
			+ " :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId"
			+ " ,:CasteId, :ReligionId, :SubCategory, :MarginDeviation, :Vip)";

	public static final String PHONE_NO_INSERT_SQL = " Insert Into CustomerPhoneNumbers"
			+ " (PhoneCustID, PhoneTypeCode, PhoneCountryCode, PhoneAreaCode, PhoneNumber,PhoneTypePriority,"
			+ " Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,"
			+ " TaskId, NextTaskId, RecordType, WorkflowId)"
			+ " Values(:PhoneCustID, :PhoneTypeCode, :PhoneCountryCode,:PhoneAreaCode,:PhoneNumber,:PhoneTypePriority,"
			+ " :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,"
			+ " :TaskId, :NextTaskId, :RecordType, :WorkflowId)";

	public static final String EMAIL_INSERT_SQL = " Insert Into CustomerEMails"
			+ " (CustID, CustEMailTypeCode, CustEMailPriority, CustEMail, DomainCheck"
			+ ", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode"
			+ ", TaskId, NextTaskId, RecordType, WorkflowId)"
			+ " Values(:CustID, :CustEMailTypeCode, :CustEMailPriority, :CustEMail, :DomainCheck"
			+ ", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode"
			+ ", :TaskId, :NextTaskId, :RecordType, :WorkflowId)";

	public static final String ADDRESS_INSERT_SQL = "Insert Into CustomerAddresses"
			+ " (CustAddressId,CustID, CustAddrType, CustAddrHNbr, CustFlatNbr, CustAddrStreet,"
			+ " CustAddrLine1, CustAddrLine2, CustPOBox, CustAddrCountry, CustAddrProvince, CustAddrPriority,"
			+ " CustAddrCity, CustAddrZIP, CustAddrPhone,CustAddrFrom,TypeOfResidence,CustAddrLine3,CustAddrLine4,CustDistrict,"
			+ " Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,"
			+ " NextTaskId, RecordType, WorkflowId)"
			+ " Values(:CustAddressId,:CustID, :CustAddrType, :CustAddrHNbr, :CustFlatNbr, :CustAddrStreet,"
			+ " :CustAddrLine1, :CustAddrLine2, :CustPOBox, :CustAddrCountry, :CustAddrProvince, :CustAddrPriority,"
			+ " :CustAddrCity, :CustAddrZIP, :CustAddrPhone, :CustAddrFrom,:TypeOfResidence,:CustAddrLine3,:CustAddrLine4,:CustDistrict,"
			+ " :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,"
			+ " :TaskId, :NextTaskId, :RecordType, :WorkflowId)";

	public static final String BUILDER_GROUP_INSERT_SQL = "insert into BuilderGroup"
			+ " (id, name, segmentation, pedeveloperid, city, province, pincodeid, expLmtOnAmt, expLmtOnNoOfUnits, currExpUnits, currExpAmt, "
			+ " Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)"
			+ " values(:id, :name, :segmentation, :peDeveloperId, :city, :province, :pinCodeId, :expLmtOnAmt, :expLmtOnNoOfUnits, :currExpUnits, :currExpAmt, "
			+ " :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)";

	public static final String BUILDER_COMPANY_INSERT_SQL = "insert into BuilderCompany"
			+ " (id, name, segmentation, CustId , groupId, apfType, peDevId, entityType, emailId, cityType, address1, address2,"
			+ " address3, city, state, code, devavailablity, magnitude, absavailablity, totalProj, approved, remarks, panDetails, benfName, accountNo, bankBranchId,"
			+ " limitOnAmt, limitOnUnits, currentExpUni, currentExpAmt, dateOfInCop, noOfProj, assHLPlayers, onGoingProj, expInBusiness, recommendation, magintudeInLacs, noOfProjCons, active,"
			+ " Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)"
			+ " values(:id, :name, :segmentation, :CustId ,:groupId, :apfType, :peDevId, :entityType, :emailId, :cityType, :address1, :address2,"
			+ " :address3, :city, :state, :code, :devavailablity, :magnitude, :absavailablity, :totalProj, :approved, :remarks, :panDetails, :benfName, :accountNo, :bankBranchId,"
			+ " :limitOnAmt, :limitOnUnits, :currentExpUni, :currentExpAmt, :dateOfInCop, :noOfProj, :assHLPlayers, :onGoingProj, :expInBusiness, :recommendation, :magintudeInLacs, :noOfProjCons, :active,"
			+ " :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)";

	public static final String BUILDER_PROJECT_INSERT_SQL = "insert into BuilderProjcet"
			+ "(id, name, builderId, apfNo, "
			+ "RegistrationNumber, AddressLine1, AddressLine2, AddressLine3, Landmark,"
			+ "AreaOrLocality, City, State, PinCode, ProjectType, TypesOfApf, TotalUnits,"
			+ "NumberOfTowers, NoOfIndependentHouses, ProjectStartDate, ProjectEndDate, Remarks,"
			+ "CommencementCertificateNo, Commencecrtfctissuingauthority, TotalPlotArea, "
			+ "ConstructedArea, TechnicalDone, LegalDone, "
			+ "RcuDone, Constrctincompletionpercentage, DisbursalRecommendedPercentage,"
			+ "BeneficiaryName, BankBranchID, AccountNo, "
			+ " Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)"
			+ " values( :id, :name, :builderId, :apfNo, "
			+ ":RegistrationNumber, :AddressLine1, :AddressLine2, :AddressLine3, :Landmark,"
			+ ":AreaOrLocality, :City, :State, :PinCode, :ProjectType, :TypesOfApf, :TotalUnits,"
			+ ":NumberOfTowers, :NoOfIndependentHouses, :ProjectStartDate, :ProjectEndDate, :Remarks,"
			+ ":CommencementCertificateNo, :Commencecrtfctissuingauthority, :TotalPlotArea, "
			+ ":ConstructedArea, :TechnicalDone, :LegalDone, "
			+ ":RcuDone, :Constrctincompletionpercentage, :DisbursalRecommendedPercentage,"
			+ ":BeneficiaryName, :BankBranchID, :AccountNo, "
			+ " :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)";

	public static final String PROJECT_UNITS_INSERT_SQL = "insert into ProjectUnits"
			+ " (UnitId, UnitType, Tower, FloorNumber, UnitNumber, UnitArea, Rate,"
			+ "Price, OtherCharges, TotalPrice, UnitRpsf, UnitPlotArea, UnitSuperBuiltUp,"
			+ "ProjectId, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, "
			+ "NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,"
			+ "UnitAreaConsidered, CarpetArea, UnitBuiltUpArea, RateConsidered, RateAsPerCarpetArea,"
			+ "RateAsPerBuiltUpArea, RateAsPerSuperBuiltUpArea, RateAsPerBranchAPF,"
			+ "RateAsPerCostSheet, FloorRiseCharges, OpenCarParkingCharges," + "ClosedCarParkingCharges, Gst, Remarks)"
			+ " values(:Id, :UnitType, :Tower, :FloorNumber, :UnitNumber, :UnitArea, :Rate,"
			+ ":Price, :OtherCharges, :TotalPrice, :UnitRpsf, :UnitPlotArea, :UnitSuperBuiltUp,"
			+ ":ProjectId, :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId,"
			+ ":NextTaskId, :RecordType, :WorkflowId,"
			+ ":UnitAreaConsidered, :CarpetArea, :UnitBuiltUpArea, :RateConsidered, :RateAsPerCarpetArea,"
			+ ":RateAsPerBuiltUpArea, :RateAsPerSuperBuiltUpArea, :RateAsPerBranchAPF,"
			+ ":RateAsPerCostSheet, :FloorRiseCharges, :OpenCarParkingCharges,"
			+ ":ClosedCarParkingCharges, :Gst, :Remarks)";

}
