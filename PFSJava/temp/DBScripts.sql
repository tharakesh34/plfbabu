

-- Create View for CustomerType Module
CREATE VIEW [dbo].[RMTCustTypes_View]
AS
SELECT		CustTypeCode, CustTypeDesc, CustTypeIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	RMTCustTypes_TEMP
UNION ALL
SELECT		CustTypeCode, CustTypeDesc, CustTypeIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	RMTCustTypes
WHERE     NOT EXISTS (SELECT 1 FROM RMTCustTypes_TEMP WHERE CustTypeCode = RMTCustTypes.CustTypeCode)

GO

-- Create View for Country Module
CREATE VIEW [dbo].[BMTCountries_View]
AS
SELECT		CountryCode, CountryDesc, CountryParentLimit, CountryResidenceLimit, CountryRiskLimit, CountryIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTCountries_TEMP
UNION ALL
SELECT		CountryCode, CountryDesc, CountryParentLimit, CountryResidenceLimit, CountryRiskLimit, CountryIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTCountries
WHERE     NOT EXISTS (SELECT 1 FROM BMTCountries_TEMP WHERE CountryCode = BMTCountries.CountryCode)

GO

-- Create View for Branch Module
CREATE VIEW [dbo].[RMTBranches_View]
AS
SELECT		BranchCode, BranchDesc, BranchAddrLine1, BranchAddrLine2, BranchPOBox, BranchCity, BranchProvince, BranchCountry, BranchFax, BranchTel, BranchSwiftBankCde, BranchSwiftCountry, BranchSwiftLocCode, BranchSwiftBrnCde, BranchSortCode, BranchIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	RMTBranches_TEMP
UNION ALL
SELECT		BranchCode, BranchDesc, BranchAddrLine1, BranchAddrLine2, BranchPOBox, BranchCity, BranchProvince, BranchCountry, BranchFax, BranchTel, BranchSwiftBankCde, BranchSwiftCountry, BranchSwiftLocCode, BranchSwiftBrnCde, BranchSortCode, BranchIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	RMTBranches
WHERE     NOT EXISTS (SELECT 1 FROM RMTBranches_TEMP WHERE BranchCode = RMTBranches.BranchCode)

GO

-- Create View for Currency Module
CREATE VIEW [dbo].[RMTCurrencies_View]
AS
SELECT		CcyCode, CcyNumber, CcyDesc, CcySwiftCode, CcyEditField, CcyMinorCcyUnits, CcyDrRateBasisCode, CcyCrRateBasisCode, CcyIsIntRounding, CcySpotRate, CcyIsReceprocal, CcyUserRateBuy, CcyUserRateSell, CcyIsMember, CcyIsGroup, CcyIsAlwForLoans, CcyIsAlwForDepo, CcyIsAlwForAc, CcyIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	RMTCurrencies_TEMP
UNION ALL
SELECT		CcyCode, CcyNumber, CcyDesc, CcySwiftCode, CcyEditField, CcyMinorCcyUnits, CcyDrRateBasisCode, CcyCrRateBasisCode, CcyIsIntRounding, CcySpotRate, CcyIsReceprocal, CcyUserRateBuy, CcyUserRateSell, CcyIsMember, CcyIsGroup, CcyIsAlwForLoans, CcyIsAlwForDepo, CcyIsAlwForAc, CcyIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	RMTCurrencies
WHERE     NOT EXISTS (SELECT 1 FROM RMTCurrencies_TEMP WHERE CcyCode = RMTCurrencies.CcyCode)

GO

-- Create View for BaseRateCode Module
CREATE VIEW [dbo].[RMTBaseRateCodes_View]
AS
SELECT		BRType, BRTypeDesc		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	RMTBaseRateCodes_TEMP
UNION ALL
SELECT		BRType, BRTypeDesc		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	RMTBaseRateCodes
WHERE     NOT EXISTS (SELECT 1 FROM RMTBaseRateCodes_TEMP WHERE BRType = RMTBaseRateCodes.BRType)

GO

-- Create View for BaseRate Module
CREATE VIEW [dbo].[RMTBaseRates_View]
AS
SELECT		BRType, BREffDate, BRRate		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	RMTBaseRates_TEMP
UNION ALL
SELECT		BRType, BREffDate, BRRate		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	RMTBaseRates
WHERE     NOT EXISTS (SELECT 1 FROM RMTBaseRates_TEMP WHERE BRType = RMTBaseRates.BRType)

GO

-- Create View for Gender Module
CREATE VIEW [dbo].[BMTGenders_View]
AS
SELECT		GenderCode, GenderDesc, GenderIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTGenders_TEMP
UNION ALL
SELECT		GenderCode, GenderDesc, GenderIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTGenders
WHERE     NOT EXISTS (SELECT 1 FROM BMTGenders_TEMP WHERE GenderCode = BMTGenders.GenderCode)

GO

-- Create View for Industry Module
CREATE VIEW [dbo].[BMTIndustries_View]
AS
SELECT		IndustryCode, IndustryDesc, IndustryLimit, IndustryIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTIndustries_TEMP
UNION ALL
SELECT		IndustryCode, IndustryDesc, IndustryLimit, IndustryIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTIndustries
WHERE     NOT EXISTS (SELECT 1 FROM BMTIndustries_TEMP WHERE IndustryCode = BMTIndustries.IndustryCode)

GO

-- Create View for NationalityCode Module
CREATE VIEW [dbo].[BMTNationalityCodes_View]
AS
SELECT		NationalityCode, NationalityDesc, NationalityIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTNationalityCodes_TEMP
UNION ALL
SELECT		NationalityCode, NationalityDesc, NationalityIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTNationalityCodes
WHERE     NOT EXISTS (SELECT 1 FROM BMTNationalityCodes_TEMP WHERE NationalityCode = BMTNationalityCodes.NationalityCode)

GO

-- Create View for Profession Module
CREATE VIEW [dbo].[BMTProfessions_View]
AS
SELECT		ProfessionCode, ProfessionDesc, ProfessionIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTProfessions_TEMP
UNION ALL
SELECT		ProfessionCode, ProfessionDesc, ProfessionIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTProfessions
WHERE     NOT EXISTS (SELECT 1 FROM BMTProfessions_TEMP WHERE ProfessionCode = BMTProfessions.ProfessionCode)

GO

-- Create View for Salutation Module
CREATE VIEW [dbo].[BMTSalutations_View]
AS
SELECT		SalutationCode, SaluationDesc, SalutationIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTSalutations_TEMP
UNION ALL
SELECT		SalutationCode, SaluationDesc, SalutationIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTSalutations
WHERE     NOT EXISTS (SELECT 1 FROM BMTSalutations_TEMP WHERE SalutationCode = BMTSalutations.SalutationCode)

GO

-- Create View for MaritalStatusCode Module
CREATE VIEW [dbo].[BMTMaritalStatusCodes_View]
AS
SELECT		MaritalStsCode, MaritalStsDesc, MaritalStsIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTMaritalStatusCodes_TEMP
UNION ALL
SELECT		MaritalStsCode, MaritalStsDesc, MaritalStsIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTMaritalStatusCodes
WHERE     NOT EXISTS (SELECT 1 FROM BMTMaritalStatusCodes_TEMP WHERE MaritalStsCode = BMTMaritalStatusCodes.MaritalStsCode)

GO

-- Create View for Sector Module
CREATE VIEW [dbo].[BMTSectors_View]
AS
SELECT		SectorCode, SectorDesc, SectorLimit, SectorIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTSectors_TEMP
UNION ALL
SELECT		SectorCode, SectorDesc, SectorLimit, SectorIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTSectors
WHERE     NOT EXISTS (SELECT 1 FROM BMTSectors_TEMP WHERE SectorCode = BMTSectors.SectorCode)

GO

-- Create View for Segment Module
CREATE VIEW [dbo].[BMTSegments_View]
AS
SELECT		SegmentCode, SegmentDesc, SegmentIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTSegments_TEMP
UNION ALL
SELECT		SegmentCode, SegmentDesc, SegmentIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTSegments
WHERE     NOT EXISTS (SELECT 1 FROM BMTSegments_TEMP WHERE SegmentCode = BMTSegments.SegmentCode)

GO

-- Create View for SubSector Module
CREATE VIEW [dbo].[BMTSubSectors_View]
AS
SELECT		SectorCode, SubSectorCode, SubSectorDesc, SubSectorIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTSubSectors_TEMP
UNION ALL
SELECT		SectorCode, SubSectorCode, SubSectorDesc, SubSectorIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTSubSectors
WHERE     NOT EXISTS (SELECT 1 FROM BMTSubSectors_TEMP WHERE SectorCode = BMTSubSectors.SectorCode)

GO

-- Create View for SubSegment Module
CREATE VIEW [dbo].[BMTSubSegments_View]
AS
SELECT		SegmentCode, SubSegmentCode, SubSegmentDesc, SubSegmentIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTSubSegments_TEMP
UNION ALL
SELECT		SegmentCode, SubSegmentCode, SubSegmentDesc, SubSegmentIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTSubSegments
WHERE     NOT EXISTS (SELECT 1 FROM BMTSubSegments_TEMP WHERE SegmentCode = BMTSubSegments.SegmentCode)

GO

-- Create View for CustomerStatusCode Module
CREATE VIEW [dbo].[BMTCustStatusCodes_View]
AS
SELECT		CustStsCode, CustStsDescription, CustStsIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTCustStatusCodes_TEMP
UNION ALL
SELECT		CustStsCode, CustStsDescription, CustStsIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTCustStatusCodes
WHERE     NOT EXISTS (SELECT 1 FROM BMTCustStatusCodes_TEMP WHERE CustStsCode = BMTCustStatusCodes.CustStsCode)

GO

-- Create View for CustomerCategory Module
CREATE VIEW [dbo].[BMTCustCategories_View]
AS
SELECT		CustCtgCode, CustCtgDesc, CustCtgIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTCustCategories_TEMP
UNION ALL
SELECT		CustCtgCode, CustCtgDesc, CustCtgIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTCustCategories
WHERE     NOT EXISTS (SELECT 1 FROM BMTCustCategories_TEMP WHERE CustCtgCode = BMTCustCategories.CustCtgCode)

GO

-- Create View for EmpStsCode Module
CREATE VIEW [dbo].[BMTEmpStsCodes_View]
AS
SELECT		EmpStsCode, EmpStsDesc, EmpStsIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTEmpStsCodes_TEMP
UNION ALL
SELECT		EmpStsCode, EmpStsDesc, EmpStsIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTEmpStsCodes
WHERE     NOT EXISTS (SELECT 1 FROM BMTEmpStsCodes_TEMP WHERE EmpStsCode = BMTEmpStsCodes.EmpStsCode)

GO

-- Create View for GroupStatusCode Module
CREATE VIEW [dbo].[BMTGrpStatusCodes_View]
AS
SELECT		GrpStsCode, GrpStsDescription, GrpStsIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTGrpStatusCodes_TEMP
UNION ALL
SELECT		GrpStsCode, GrpStsDescription, GrpStsIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTGrpStatusCodes
WHERE     NOT EXISTS (SELECT 1 FROM BMTGrpStatusCodes_TEMP WHERE GrpStsCode = BMTGrpStatusCodes.GrpStsCode)

GO

-- Create View for GeneralDesignation Module
CREATE VIEW [dbo].[RMTGenDesignations_View]
AS
SELECT		GenDesignation, GenDesgDesc		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	RMTGenDesignations_TEMP
UNION ALL
SELECT		GenDesignation, GenDesgDesc		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	RMTGenDesignations
WHERE     NOT EXISTS (SELECT 1 FROM RMTGenDesignations_TEMP WHERE GenDesignation = RMTGenDesignations.GenDesignation)

GO

-- Create View for GeneralDepartment Module
CREATE VIEW [dbo].[RMTGenDepartments_View]
AS
SELECT		GenDepartment, GenDeptDesc		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	RMTGenDepartments_TEMP
UNION ALL
SELECT		GenDepartment, GenDeptDesc		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	RMTGenDepartments
WHERE     NOT EXISTS (SELECT 1 FROM RMTGenDepartments_TEMP WHERE GenDepartment = RMTGenDepartments.GenDepartment)

GO

-- Create View for EmploymentType Module
CREATE VIEW [dbo].[RMTEmpTypes_View]
AS
SELECT		EmpType, EmpTypeDesc		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	RMTEmpTypes_TEMP
UNION ALL
SELECT		EmpType, EmpTypeDesc		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	RMTEmpTypes
WHERE     NOT EXISTS (SELECT 1 FROM RMTEmpTypes_TEMP WHERE EmpType = RMTEmpTypes.EmpType)

GO

-- Create View for Province Module
CREATE VIEW [dbo].[RMTCountryVsProvince_View]
AS
SELECT		CPCountry, CPProvince, CPProvinceName		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	RMTCountryVsProvince_TEMP
UNION ALL
SELECT		CPCountry, CPProvince, CPProvinceName		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	RMTCountryVsProvince
WHERE     NOT EXISTS (SELECT 1 FROM RMTCountryVsProvince_TEMP WHERE CPCountry = RMTCountryVsProvince.CPCountry)

GO


-- Create View for City Module
CREATE VIEW [dbo].[RMTProvinceVsCity_View]
AS
SELECT		PCCounty, PCProvince, PCCity, PCCityName		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	RMTProvinceVsCity_TEMP
UNION ALL
SELECT		PCCounty, PCProvince, PCCity, PCCityName		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	RMTProvinceVsCity
WHERE     NOT EXISTS (SELECT 1 FROM RMTProvinceVsCity_TEMP WHERE PCCounty = RMTProvinceVsCity.PCCounty)

GO

-- Create View for CustomerGroup Module
CREATE VIEW [dbo].[CustomerGroups_View]
AS
SELECT		CustGrpID,CustGrpCode, CustGrpDesc, CustGrpRO1, CustGrpLimit, CustGrpIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	CustomerGroups_TEMP
UNION ALL
SELECT		CustGrpID,CustGrpCode, CustGrpDesc, CustGrpRO1, CustGrpLimit, CustGrpIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	CustomerGroups
WHERE     NOT EXISTS (SELECT 1 FROM CustomerGroups_TEMP WHERE CustGrpID = CustomerGroups.CustGrpID)

GO

-- Create View for RatingType Module
CREATE VIEW [dbo].[BMTRatingTypes_View]
AS
SELECT		RatingType, RatingTypeDesc, ValueType, ValueLen, RatingIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTRatingTypes_TEMP
UNION ALL
SELECT		RatingType, RatingTypeDesc, ValueType, ValueLen, RatingIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTRatingTypes
WHERE     NOT EXISTS (SELECT 1 FROM BMTRatingTypes_TEMP WHERE RatingType = BMTRatingTypes.RatingType)

GO

-- Create View for RatingCode Module
CREATE VIEW [dbo].[BMTRatingCodes_View]
AS
SELECT		RatingType, RatingCode, RatingCodeDesc, RatingIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTRatingCodes_TEMP
UNION ALL
SELECT		RatingType, RatingCode, RatingCodeDesc, RatingIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTRatingCodes
WHERE     NOT EXISTS (SELECT 1 FROM BMTRatingCodes_TEMP WHERE RatingType = BMTRatingCodes.RatingType)

GO

-- Create View for CustomerRating Module
CREATE VIEW [dbo].[CustomerRatings_View]
AS
SELECT		CustID, CustRatingType, CustRatingCode, CustRating		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	CustomerRatings_TEMP
UNION ALL
SELECT		CustID, CustRatingType, CustRatingCode, CustRating		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	CustomerRatings
WHERE     NOT EXISTS (SELECT 1 FROM CustomerRatings_TEMP WHERE CustID = CustomerRatings.CustID)

GO

-- Create View for CustomerPRelation Module
CREATE VIEW [dbo].[CustomersPRelations_View]
AS
SELECT		PRCustID, PRCustPRSNo, PRRelationCode, PRRelationCustID, PRisGuardian, PRFName, PRMName, PRLName, PRSName, PRFNameLclLng, PRMNameLclLng, PRLNameLclLng, PRDOB, PRAddrHNbr, PRAddrFNbr, PRAddrStreet, PRAddrLine1, PRAddrLine2, PRAddrPOBox, PRAddrCity, PRAddrProvince, PRAddrCountry, PRAddrZIP, PRPhone, PRMail		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	CustomersPRelations_TEMP
UNION ALL
SELECT		PRCustID, PRCustPRSNo, PRRelationCode, PRRelationCustID, PRisGuardian, PRFName, PRMName, PRLName, PRSName, PRFNameLclLng, PRMNameLclLng, PRLNameLclLng, PRDOB, PRAddrHNbr, PRAddrFNbr, PRAddrStreet, PRAddrLine1, PRAddrLine2, PRAddrPOBox, PRAddrCity, PRAddrProvince, PRAddrCountry, PRAddrZIP, PRPhone, PRMail		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	CustomersPRelations
WHERE     NOT EXISTS (SELECT 1 FROM CustomersPRelations_TEMP WHERE PRCustID = CustomersPRelations.PRCustID)

GO
--Sequence Tabele
CREATE TABLE [dbo].[SeqCustomersPRelations](
	[SeqNo] [bigint] NOT NULL
) ON [PRIMARY]

INSERT INTO [dbo].[SeqCustomersPRelations]VALUES (0)

-- Create View for PRelationCode Module
CREATE VIEW [dbo].[BMTPRelationCodes_View]
AS
SELECT		PRelationCode, PRelationDesc, RelationCodeIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTPRelationCodes_TEMP
UNION ALL
SELECT		PRelationCode, PRelationDesc, RelationCodeIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTPRelationCodes
WHERE     NOT EXISTS (SELECT 1 FROM BMTPRelationCodes_TEMP WHERE PRelationCode = BMTPRelationCodes.PRelationCode)

GO

-- Create View for Academic Module
CREATE VIEW [dbo].[BMTAcademics_View]
AS
SELECT		AcademicLevel, AcademicDecipline, AcademicDesc		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTAcademics_TEMP
UNION ALL
SELECT		AcademicLevel, AcademicDecipline, AcademicDesc		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTAcademics
WHERE     NOT EXISTS (SELECT 1 FROM BMTAcademics_TEMP WHERE AcademicLevel = BMTAcademics.AcademicLevel)

GO

-- Create View for AddressType Module
CREATE VIEW [dbo].[BMTAddressTypes_View]
AS
SELECT		AddrTypeCode, AddrTypeDesc, AddrTypePriority, AddrTypeIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTAddressTypes_TEMP
UNION ALL
SELECT		AddrTypeCode, AddrTypeDesc, AddrTypePriority, AddrTypeIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTAddressTypes
WHERE     NOT EXISTS (SELECT 1 FROM BMTAddressTypes_TEMP WHERE AddrTypeCode = BMTAddressTypes.AddrTypeCode)

GO

-- Create View for BlackListReasonCode Module
CREATE VIEW [dbo].[BMTBlackListRsnCodes_View]
AS
SELECT		BLRsnCode, BLRsnDesc, BLIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTBlackListRsnCodes_TEMP
UNION ALL
SELECT		BLRsnCode, BLRsnDesc, BLIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTBlackListRsnCodes
WHERE     NOT EXISTS (SELECT 1 FROM BMTBlackListRsnCodes_TEMP WHERE BLRsnCode = BMTBlackListRsnCodes.BLRsnCode)

GO

-- Create View for CorpRelationCode Module
CREATE VIEW [dbo].[BMTCorpRelationCodes_View]
AS
SELECT		CorpRelationCode, CorpRelationDesc, CorpRelationIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTCorpRelationCodes_TEMP
UNION ALL
SELECT		CorpRelationCode, CorpRelationDesc, CorpRelationIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTCorpRelationCodes
WHERE     NOT EXISTS (SELECT 1 FROM BMTCorpRelationCodes_TEMP WHERE CorpRelationCode = BMTCorpRelationCodes.CorpRelationCode)

GO

-- Create View for CustomerNotesType Module
CREATE VIEW [dbo].[BMTCustNotesTypes_View]
AS
SELECT		CustNotesTypeCode, CustNotesTypeDesc, CustNotesTypeIsPerminent, CustNotesTypeArchiveFrq		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTCustNotesTypes_TEMP
UNION ALL
SELECT		CustNotesTypeCode, CustNotesTypeDesc, CustNotesTypeIsPerminent, CustNotesTypeArchiveFrq		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTCustNotesTypes
WHERE     NOT EXISTS (SELECT 1 FROM BMTCustNotesTypes_TEMP WHERE CustNotesTypeCode = BMTCustNotesTypes.CustNotesTypeCode)

GO

-- Create View for Department Module
CREATE VIEW [dbo].[BMTDepartments_View]
AS
SELECT		DeptCode, DeptDesc, DeptIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTDepartments_TEMP
UNION ALL
SELECT		DeptCode, DeptDesc, DeptIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTDepartments
WHERE     NOT EXISTS (SELECT 1 FROM BMTDepartments_TEMP WHERE DeptCode = BMTDepartments.DeptCode)

GO

-- Create View for Designation Module
CREATE VIEW [dbo].[BMTDesignations_View]
AS
SELECT		DesgCode, DesgDesc, DesgIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTDesignations_TEMP
UNION ALL
SELECT		DesgCode, DesgDesc, DesgIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTDesignations
WHERE     NOT EXISTS (SELECT 1 FROM BMTDesignations_TEMP WHERE DesgCode = BMTDesignations.DesgCode)

GO

-- Create View for DocumentType Module
CREATE VIEW [dbo].[BMTDocumentTypes_View]
AS
SELECT		DocTypeCode, DocTypeDesc, DocIsMandatory, DocTypeIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTDocumentTypes_TEMP
UNION ALL
SELECT		DocTypeCode, DocTypeDesc, DocIsMandatory, DocTypeIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTDocumentTypes
WHERE     NOT EXISTS (SELECT 1 FROM BMTDocumentTypes_TEMP WHERE DocTypeCode = BMTDocumentTypes.DocTypeCode)

GO

-- Create View for EMailType Module
CREATE VIEW [dbo].[BMTEMailTypes_View]
AS
SELECT		EmailTypeCode, EmailTypeDesc, EmailTypePriority, EmailTypeIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTEMailTypes_TEMP
UNION ALL
SELECT		EmailTypeCode, EmailTypeDesc, EmailTypePriority, EmailTypeIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTEMailTypes
WHERE     NOT EXISTS (SELECT 1 FROM BMTEMailTypes_TEMP WHERE EmailTypeCode = BMTEMailTypes.EmailTypeCode)

GO

-- Create View for FinanceApplicationCode Module
CREATE VIEW [dbo].[BMTFinanceApplicaitonCodes_View]
AS
SELECT		FinAppType, FinAppDesc, FinAppIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTFinanceApplicaitonCodes_TEMP
UNION ALL
SELECT		FinAppType, FinAppDesc, FinAppIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTFinanceApplicaitonCodes
WHERE     NOT EXISTS (SELECT 1 FROM BMTFinanceApplicaitonCodes_TEMP WHERE FinAppType = BMTFinanceApplicaitonCodes.FinAppType)

GO

-- Create View for Frequency Module
CREATE VIEW [dbo].[BMTFrequencies_View]
AS
SELECT		FrqCode, FrqDesc, FrqIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTFrequencies_TEMP
UNION ALL
SELECT		FrqCode, FrqDesc, FrqIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTFrequencies
WHERE     NOT EXISTS (SELECT 1 FROM BMTFrequencies_TEMP WHERE FrqCode = BMTFrequencies.FrqCode)

GO

-- Create View for IdentityDetails Module
CREATE VIEW [dbo].[BMTIdentityType_View]
AS
SELECT		IdentityType, IdentityDesc		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTIdentityType_TEMP
UNION ALL
SELECT		IdentityType, IdentityDesc		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTIdentityType
WHERE     NOT EXISTS (SELECT 1 FROM BMTIdentityType_TEMP WHERE IdentityType = BMTIdentityType.IdentityType)

GO


-- Create View for IncomeType Module
CREATE VIEW [dbo].[BMTIncomeTypes_View]
AS
SELECT		IncomeTypeCode, IncomeTypeDesc, IncomeTypeIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTIncomeTypes_TEMP
UNION ALL
SELECT		IncomeTypeCode, IncomeTypeDesc, IncomeTypeIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTIncomeTypes
WHERE     NOT EXISTS (SELECT 1 FROM BMTIncomeTypes_TEMP WHERE IncomeTypeCode = BMTIncomeTypes.IncomeTypeCode)

GO

------------------------------------------------------------------------------



-- Create View for InterestRateType Module
CREATE VIEW [dbo].[BMTInterestRateTypes_View]
AS
SELECT		IntRateTypeCode, IntRateTypeDesc, IntRateTypeIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTInterestRateTypes_TEMP
UNION ALL
SELECT		IntRateTypeCode, IntRateTypeDesc, IntRateTypeIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTInterestRateTypes
WHERE     NOT EXISTS (SELECT 1 FROM BMTInterestRateTypes_TEMP WHERE IntRateTypeCode = BMTInterestRateTypes.IntRateTypeCode)

GO

-- Create View for InterestRateBasisCode Module
CREATE VIEW [dbo].[BMTIntRateBasisCodes_View]
AS
SELECT		IntRateBasisCode, IntRateBasisDesc, IntRateBasisIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTIntRateBasisCodes_TEMP
UNION ALL
SELECT		IntRateBasisCode, IntRateBasisDesc, IntRateBasisIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTIntRateBasisCodes
WHERE     NOT EXISTS (SELECT 1 FROM BMTIntRateBasisCodes_TEMP WHERE IntRateBasisCode = BMTIntRateBasisCodes.IntRateBasisCode)

GO

-- Create View for PhoneType Module
CREATE VIEW [dbo].[BMTPhoneTypes_View]
AS
SELECT		PhoneTypeCode, PhoneTypeDesc, PhoneTypePriority, PhoneTypeIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTPhoneTypes_TEMP
UNION ALL
SELECT		PhoneTypeCode, PhoneTypeDesc, PhoneTypePriority, PhoneTypeIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTPhoneTypes
WHERE     NOT EXISTS (SELECT 1 FROM BMTPhoneTypes_TEMP WHERE PhoneTypeCode = BMTPhoneTypes.PhoneTypeCode)

GO

-- Create View for RejectDetail Module
CREATE VIEW [dbo].[BMTRejectCodes_View]
AS
SELECT		RejectCode, RejectDesc, RejectIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTRejectCodes_TEMP
UNION ALL
SELECT		RejectCode, RejectDesc, RejectIsActive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	BMTRejectCodes
WHERE     NOT EXISTS (SELECT 1 FROM BMTRejectCodes_TEMP WHERE RejectCode = BMTRejectCodes.RejectCode)

GO

-- Create View for CustomerAdditionalDetail Module
CREATE VIEW [dbo].[CustAdditionalDetails_View]
AS
SELECT		CustID, CustAcademicLevel, AcademicDecipline, CustRefCustID, CustRefStaffID		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	CustAdditionalDetails_TEMP
UNION ALL
SELECT		CustID, CustAcademicLevel, AcademicDecipline, CustRefCustID, CustRefStaffID		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	CustAdditionalDetails
WHERE     NOT EXISTS (SELECT 1 FROM CustAdditionalDetails_TEMP WHERE CustID = CustAdditionalDetails.CustID)

GO

-- Create View for CustomerIdentity Module
CREATE VIEW [dbo].[CustIdentities_View]
AS
SELECT		IdCustID, IdType, IdIssuedBy, IdRef, IdIssueCountry, IdIssuedOn, IdExpiresOn, IdLocation		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	CustIdentities_TEMP
UNION ALL
SELECT		IdCustID, IdType, IdIssuedBy, IdRef, IdIssueCountry, IdIssuedOn, IdExpiresOn, IdLocation		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	CustIdentities
WHERE     NOT EXISTS (SELECT 1 FROM CustIdentities_TEMP WHERE IdCustID = CustIdentities.IdCustID)

GO

-- Create View for CustomerAddres Module
CREATE VIEW [dbo].[CustomerAddresses_View]
AS
SELECT		CustID, CustAddrType, CustAddrHNbr, CustFlatNbr, CustAddrStreet, CustAddrLine1, CustAddrLine2, CustPOBox, CustAddrCountry, CustAddrProvince, CustAddrCity, CustAddrZIP, CustAddrPhone		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	CustomerAddresses_TEMP
UNION ALL
SELECT		CustID, CustAddrType, CustAddrHNbr, CustFlatNbr, CustAddrStreet, CustAddrLine1, CustAddrLine2, CustPOBox, CustAddrCountry, CustAddrProvince, CustAddrCity, CustAddrZIP, CustAddrPhone		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	CustomerAddresses
WHERE     NOT EXISTS (SELECT 1 FROM CustomerAddresses_TEMP WHERE CustID = CustomerAddresses.CustID)

GO

-- Create View for CustomerDocument Module
CREATE VIEW [dbo].[CustomerDocuments_View]
AS
SELECT		CustID, CustDocType, CustDocTitle, CustDocSysName, CustDocRcvdOn, CustDocExpDate, CustDocIssuedOn, CustDocIssuedCountry, CustDocIsVerified, CustDocVerifiedBy, CustDocIsAcrive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	CustomerDocuments_TEMP
UNION ALL
SELECT		CustID, CustDocType, CustDocTitle, CustDocSysName, CustDocRcvdOn, CustDocExpDate, CustDocIssuedOn, CustDocIssuedCountry, CustDocIsVerified, CustDocVerifiedBy, CustDocIsAcrive		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	CustomerDocuments
WHERE     NOT EXISTS (SELECT 1 FROM CustomerDocuments_TEMP WHERE CustID = CustomerDocuments.CustID)

GO

-- Create View for CustomerEMail Module
CREATE VIEW [dbo].[CustomerEMails_View]
AS
SELECT		CustID, CustEMailTypeCode, CustEMailPriority, CustEMail		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	CustomerEMails_TEMP
UNION ALL
SELECT		CustID, CustEMailTypeCode, CustEMailPriority, CustEMail		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	CustomerEMails
WHERE     NOT EXISTS (SELECT 1 FROM CustomerEMails_TEMP WHERE CustID = CustomerEMails.CustID)

GO

-- Create View for CustomerIncome Module
CREATE VIEW [dbo].[CustomerIncomes_View]
AS
SELECT		CustID, CustIncomeType, CustIncome, CustIncomeCountry		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	CustomerIncomes_TEMP
UNION ALL
SELECT		CustID, CustIncomeType, CustIncome, CustIncomeCountry		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	CustomerIncomes
WHERE     NOT EXISTS (SELECT 1 FROM CustomerIncomes_TEMP WHERE CustID = CustomerIncomes.CustID)

GO

-- Create View for CustomerEmploymentDetail Module
CREATE VIEW [dbo].[CustomerEmpDetails_View]
AS
SELECT		CustID, CustEmpName, CustEmpFrom, CustEmpDesg, CustEmpDept, CustEmpID, CustEmpType, CustEmpHNbr, CustEMpFlatNbr, CustEmpAddrStreet, CustEMpAddrLine1, CustEMpAddrLine2, CustEmpPOBox, CustEmpAddrCity, CustEmpAddrProvince, CustEmpAddrCountry, CustEmpAddrZIP, CustEmpAddrPhone		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	CustomerEmpDetails_TEMP
UNION ALL
SELECT		CustID, CustEmpName, CustEmpFrom, CustEmpDesg, CustEmpDept, CustEmpID, CustEmpType, CustEmpHNbr, CustEMpFlatNbr, CustEmpAddrStreet, CustEMpAddrLine1, CustEMpAddrLine2, CustEmpPOBox, CustEmpAddrCity, CustEmpAddrProvince, CustEmpAddrCountry, CustEmpAddrZIP, CustEmpAddrPhone		
			, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId
FROM    	CustomerEmpDetails
WHERE     NOT EXISTS (SELECT 1 FROM CustomerEmpDetails_TEMP WHERE CustID = CustomerEmpDetails.CustID)

GO

-- Create View for CustomerNote Module
CREATE VIEW [dbo].[CustomerNotes_View]
AS
SELECT		T1.CustID, T1.CustNotesType, T1.CustNotesTitle, T1.CustNotes		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	CustomerNotes_TEMP AS T1
UNION ALL
SELECT		T1.CustID, T1.CustNotesType, T1.CustNotesTitle, T1.CustNotes		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	CustomerNotes AS T1
WHERE     NOT EXISTS (SELECT 1 FROM CustomerNotes_TEMP WHERE CustID = T1.CustID)

GO

-- Create View for CustomerPhoneNumber Module
CREATE VIEW [dbo].[CustomerPhoneNumbers_View]
AS
SELECT		T1.PhoneCustID, T1.PhoneTypeCode, T1.PhoneCountryCode, T1.PhoneAreaCode, T1.PhoneNumber		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	CustomerPhoneNumbers_TEMP AS T1
UNION ALL
SELECT		T1.PhoneCustID, T1.PhoneTypeCode, T1.PhoneCountryCode, T1.PhoneAreaCode, T1.PhoneNumber		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	CustomerPhoneNumbers AS T1
WHERE     NOT EXISTS (SELECT 1 FROM CustomerPhoneNumbers_TEMP WHERE PhoneCustID = T1.PhoneCustID)

GO

-- Create View for RelationshipOfficer Module
CREATE VIEW [dbo].[RelationshipOfficers_View]
AS
SELECT		T1.ROfficerCode, T1.ROfficerDesc, T1.ROfficerDeptCode, T1.ROfficerIsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RelationshipOfficers_TEMP AS T1
UNION ALL
SELECT		T1.ROfficerCode, T1.ROfficerDesc, T1.ROfficerDeptCode, T1.ROfficerIsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RelationshipOfficers AS T1
WHERE     NOT EXISTS (SELECT 1 FROM RelationshipOfficers_TEMP WHERE ROfficerCode = T1.ROfficerCode)

GO

-- Create View for AccountType Module
CREATE VIEW [dbo].[RMTAccountTypes_View]
AS
SELECT		T1.AcType, T1.AcTypeDesc, T1.AcPurpose, T1.AcSuffixLen, T1.AcSuffixStrFrom, T1.AcSuffixEndAt, T1.IsInternalAc, T1.AcRetension, T1.AcTypeIsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTAccountTypes_TEMP AS T1
UNION ALL
SELECT		T1.AcType, T1.AcTypeDesc, T1.AcPurpose, T1.AcSuffixLen, T1.AcSuffixStrFrom, T1.AcSuffixEndAt, T1.IsInternalAc, T1.AcRetension, T1.AcTypeIsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTAccountTypes AS T1
WHERE     NOT EXISTS (SELECT 1 FROM RMTAccountTypes_TEMP WHERE AcType = T1.AcType)

GO

-- Create View for BasicFinanceType Module
CREATE VIEW [dbo].[RMTBasicFinanceTypes_View]
AS
SELECT		T1.FinBasicType, T1.FinBasicDesc		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTBasicFinanceTypes_TEMP AS T1
UNION ALL
SELECT		T1.FinBasicType, T1.FinBasicDesc		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTBasicFinanceTypes AS T1
WHERE     NOT EXISTS (SELECT 1 FROM RMTBasicFinanceTypes_TEMP WHERE FinBasicType = T1.FinBasicType)

GO

-- Create View for PenaltyCode Module
CREATE VIEW [dbo].[RMTPenaltyCodes_View]
AS
SELECT		T1.PenaltyType, T1.PenaltyDesc, T1.PenaltyIsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTPenaltyCodes_TEMP AS T1
UNION ALL
SELECT		T1.PenaltyType, T1.PenaltyDesc, T1.PenaltyIsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTPenaltyCodes AS T1
WHERE     NOT EXISTS (SELECT 1 FROM RMTPenaltyCodes_TEMP WHERE PenaltyType = T1.PenaltyType)

GO

-- Create View for Penalty Module
CREATE VIEW [dbo].[RMTPenalties_View]
AS
SELECT		T1.PenaltyType, T1.PenaltyEffDate, T1.IsPenaltyCapitalize, T1.IsPenaltyOnPriOnly, T1.IsPenaltyAftGrace, T1.ODueGraceDays, T1.PenaltyPriRateBasis, T1.PenaltyPriBaseRate, T1.PenaltyPriSplRate, T1.PenaltyPriNetRate, T1.PenaltyIntRateBasis, T1.PenaltyIntBaseRate, T1.PenaltyIntSplRate, T1.PenaltyIntNetRate, T1.PenaltyIsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTPenalties_TEMP AS T1
UNION ALL
SELECT		T1.PenaltyType, T1.PenaltyEffDate, T1.IsPenaltyCapitalize, T1.IsPenaltyOnPriOnly, T1.IsPenaltyAftGrace, T1.ODueGraceDays, T1.PenaltyPriRateBasis, T1.PenaltyPriBaseRate, T1.PenaltyPriSplRate, T1.PenaltyPriNetRate, T1.PenaltyIntRateBasis, T1.PenaltyIntBaseRate, T1.PenaltyIntSplRate, T1.PenaltyIntNetRate, T1.PenaltyIsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTPenalties AS T1
WHERE     NOT EXISTS (SELECT 1 FROM RMTPenalties_TEMP WHERE PenaltyType = T1.PenaltyType)

GO

-- Create View for ProvisionSlab Module
CREATE VIEW [dbo].[RMTProvisionSlabs_View]
AS
SELECT		T1.ProvSlab, T1.ProvSlabDesc, T1.ProvSlabDays		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTProvisionSlabs_TEMP AS T1
UNION ALL
SELECT		T1.ProvSlab, T1.ProvSlabDesc, T1.ProvSlabDays		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTProvisionSlabs AS T1
WHERE     NOT EXISTS (SELECT 1 FROM RMTProvisionSlabs_TEMP WHERE ProvSlab = T1.ProvSlab)

GO

-- Create View for ProvisionCode Module
CREATE VIEW [dbo].[RMTProvisionCodes_View]
AS
SELECT		T1.ProvType, T1.ProvDesc, T1.ProvIsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTProvisionCodes_TEMP AS T1
UNION ALL
SELECT		T1.ProvType, T1.ProvDesc, T1.ProvIsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTProvisionCodes AS T1
WHERE     NOT EXISTS (SELECT 1 FROM RMTProvisionCodes_TEMP WHERE ProvType = T1.ProvType)

GO

-- Create View for Provision Module
CREATE VIEW [dbo].[RMTProvisions_View]
AS
SELECT		T1.ProvType, T1.ProvSlab, T1.ProvPercentOnPri, T1.ProvPercentOnInt, T1.ProvIsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTProvisions_TEMP AS T1
UNION ALL
SELECT		T1.ProvType, T1.ProvSlab, T1.ProvPercentOnPri, T1.ProvPercentOnInt, T1.ProvIsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTProvisions AS T1
WHERE     NOT EXISTS (SELECT 1 FROM RMTProvisions_TEMP WHERE ProvType = T1.ProvType)

GO

-- Create View for SplRateCode Module
CREATE VIEW [dbo].[RMTSplRateCodes_View]
AS
SELECT		T1.SRType, T1.SRTypeDesc, T1.SRIsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTSplRateCodes_TEMP AS T1
UNION ALL
SELECT		T1.SRType, T1.SRTypeDesc, T1.SRIsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTSplRateCodes AS T1
WHERE     NOT EXISTS (SELECT 1 FROM RMTSplRateCodes_TEMP WHERE SRType = T1.SRType)

GO

-- Create View for SplRate Module
CREATE VIEW [dbo].[RMTSplRates_View]
AS
SELECT		T1.SRType, T1.SREffDate, T1.SRRate		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTSplRates_TEMP AS T1
UNION ALL
SELECT		T1.SRType, T1.SREffDate, T1.SRRate		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTSplRates AS T1
WHERE     NOT EXISTS (SELECT 1 FROM RMTSplRates_TEMP WHERE SRType = T1.SRType)

GO

-- Create View for SalesOfficer Module
CREATE VIEW [dbo].[SalesOfficers_View]
AS
SELECT		T1.SalesOffCode, T1.SalesOffFName, T1.SalesOffMName, T1.SalesOffLName, T1.SalesOffShrtName, T1.SalesOffDept, T1.SalesOffIsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	SalesOfficers_TEMP AS T1
UNION ALL
SELECT		T1.SalesOffCode, T1.SalesOffFName, T1.SalesOffMName, T1.SalesOffLName, T1.SalesOffShrtName, T1.SalesOffDept, T1.SalesOffIsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	SalesOfficers AS T1
WHERE     NOT EXISTS (SELECT 1 FROM SalesOfficers_TEMP WHERE SalesOffCode = T1.SalesOffCode)

GO

-- Create View for Customer Module
CREATE VIEW [dbo].[Customers_View]
AS
SELECT		T1.CustID, T1.CustCIF, T1.CustCoreBank, T1.CustCtgCode, T1.CustTypeCode, T1.CustSalutationCode, T1.CustFName, T1.CustMName, T1.CustLName, T1.CustShrtName, T1.CustFNameLclLng, T1.CustMNameLclLng, T1.CustLNameLclLng, T1.CustShrtNameLclLng, T1.CustDftBranch, T1.CustGenderCode, T1.CustDOB, T1.CustPOB, T1.CustCOB, T1.CustPassportNo, T1.CustMotherMaiden, T1.CustIsMinor, T1.CustReferedBy, T1.CustDSA, T1.CustDSADept, T1.CustRO1, T1.CustRO2, T1.CustGroupID, T1.CustSts, T1.CustStsChgDate, T1.CustGroupSts, T1.CustIsBlocked, T1.CustIsActive, T1.CustIsClosed, T1.CustInactiveReason, T1.CustIsDecease, T1.CustIsDormant, T1.CustIsDelinquent, T1.CustIsTradeFinCust, T1.CustIsStaff, T1.CustStaffID, T1.CustIndustry, T1.CustSector, T1.CustSubSector, T1.CustProfession, T1.CustTotalIncome, T1.CustMaritalSts, T1.CustEmpSts, T1.CustSegment, T1.CustSubSegment, T1.CustIsBlackListed, T1.CustBLRsnCode, T1.CustIsRejected, T1.CustRejectedRsn, T1.CustBaseCcy, T1.CustLng, T1.CustParentCountry, T1.CustResdCountry, T1.CustRiskCountry, T1.CustNationality, T1.CustClosedOn, T1.CustStmtFrq, T1.CustStmtFrqDay, T1.CustIsStmtCombined, T1.CustStmtLastDate, T1.CustStmtNextDate, T1.CustStmtDispatchMode, T1.CustFirstBusinessDate, T1.CustAddlVar81, T1.CustAddlVar82, T1.CustAddlVar83, T1.CustAddlVar84, T1.CustAddlVar85, T1.CustAddlVar86, T1.CustAddlVar87, T1.CustAddlVar88, T1.CustAddlVar89, T1.CustAddlDate1, T1.CustAddlDate2, T1.CustAddlDate3, T1.CustAddlDate4, T1.CustAddlDate5, T1.CustAddlVar1, T1.CustAddlVar2, T1.CustAddlVar3, T1.CustAddlVar4, T1.CustAddlVar5, T1.CustAddlVar6, T1.CustAddlVar7, T1.CustAddlVar8, T1.CustAddlVar9, T1.CustAddlVar10, T1.CustAddlVar11, T1.CustAddlDec1, T1.CustAddlDec2, T1.CustAddlDec3, T1.CustAddlDec4, T1.CustAddlDec5, T1.CustAddlInt1, T1.CustAddlInt2, T1.CustAddlInt3, T1.CustAddlInt4, T1.CustAddlInt5		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	Customers_TEMP AS T1
UNION ALL
SELECT		T1.CustID, T1.CustCIF, T1.CustCoreBank, T1.CustCtgCode, T1.CustTypeCode, T1.CustSalutationCode, T1.CustFName, T1.CustMName, T1.CustLName, T1.CustShrtName, T1.CustFNameLclLng, T1.CustMNameLclLng, T1.CustLNameLclLng, T1.CustShrtNameLclLng, T1.CustDftBranch, T1.CustGenderCode, T1.CustDOB, T1.CustPOB, T1.CustCOB, T1.CustPassportNo, T1.CustMotherMaiden, T1.CustIsMinor, T1.CustReferedBy, T1.CustDSA, T1.CustDSADept, T1.CustRO1, T1.CustRO2, T1.CustGroupID, T1.CustSts, T1.CustStsChgDate, T1.CustGroupSts, T1.CustIsBlocked, T1.CustIsActive, T1.CustIsClosed, T1.CustInactiveReason, T1.CustIsDecease, T1.CustIsDormant, T1.CustIsDelinquent, T1.CustIsTradeFinCust, T1.CustIsStaff, T1.CustStaffID, T1.CustIndustry, T1.CustSector, T1.CustSubSector, T1.CustProfession, T1.CustTotalIncome, T1.CustMaritalSts, T1.CustEmpSts, T1.CustSegment, T1.CustSubSegment, T1.CustIsBlackListed, T1.CustBLRsnCode, T1.CustIsRejected, T1.CustRejectedRsn, T1.CustBaseCcy, T1.CustLng, T1.CustParentCountry, T1.CustResdCountry, T1.CustRiskCountry, T1.CustNationality, T1.CustClosedOn, T1.CustStmtFrq, T1.CustStmtFrqDay, T1.CustIsStmtCombined, T1.CustStmtLastDate, T1.CustStmtNextDate, T1.CustStmtDispatchMode, T1.CustFirstBusinessDate, T1.CustAddlVar81, T1.CustAddlVar82, T1.CustAddlVar83, T1.CustAddlVar84, T1.CustAddlVar85, T1.CustAddlVar86, T1.CustAddlVar87, T1.CustAddlVar88, T1.CustAddlVar89, T1.CustAddlDate1, T1.CustAddlDate2, T1.CustAddlDate3, T1.CustAddlDate4, T1.CustAddlDate5, T1.CustAddlVar1, T1.CustAddlVar2, T1.CustAddlVar3, T1.CustAddlVar4, T1.CustAddlVar5, T1.CustAddlVar6, T1.CustAddlVar7, T1.CustAddlVar8, T1.CustAddlVar9, T1.CustAddlVar10, T1.CustAddlVar11, T1.CustAddlDec1, T1.CustAddlDec2, T1.CustAddlDec3, T1.CustAddlDec4, T1.CustAddlDec5, T1.CustAddlInt1, T1.CustAddlInt2, T1.CustAddlInt3, T1.CustAddlInt4, T1.CustAddlInt5		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	Customers AS T1
WHERE     NOT EXISTS (SELECT 1 FROM Customers_TEMP WHERE CustID = T1.CustID)

GO

-- Create View for SecurityRole Module
CREATE VIEW [dbo].[SecRoles_View]
AS
SELECT		T1.RoleID, T1.RoleApp, T1.RoleCd, T1.RoleDesc, T1.RoleCategory		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	SecRoles_TEMP AS T1
UNION ALL
SELECT		T1.RoleID, T1.RoleApp, T1.RoleCd, T1.RoleDesc, T1.RoleCategory		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	SecRoles AS T1
WHERE     NOT EXISTS (SELECT 1 FROM SecRoles_TEMP WHERE RoleID = T1.RoleID)

GO
--Sequence Tabele
CREATE TABLE [dbo].[SeqSecRoles](
	[SeqNo] [bigint] NOT NULL
) ON [PRIMARY]

INSERT INTO [dbo].[SeqSecRoles]VALUES (0)

-- Create View for SecurityGroup Module
CREATE VIEW [dbo].[SecGroups_View]
AS
SELECT		T1.GrpID, T1.GrpCode, T1.GrpDesc		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	SecGroups_TEMP AS T1
UNION ALL
SELECT		T1.GrpID, T1.GrpCode, T1.GrpDesc		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	SecGroups AS T1
WHERE     NOT EXISTS (SELECT 1 FROM SecGroups_TEMP WHERE GrpID = T1.GrpID)

GO
--Sequence Tabele
CREATE TABLE [dbo].[SeqSecGroups](
	[SeqNo] [bigint] NOT NULL
) ON [PRIMARY]

INSERT INTO [dbo].[SeqSecGroups]VALUES (0)

-- Create View for SecurityUsers Module
CREATE VIEW [dbo].[SecUsers_View]
AS
SELECT		T1.UsrID, T1.UsrLogin, T1.UsrPwd, T1.UserStaffID, T1.UsrFName, T1.UsrMName, T1.UsrLName, T1.UsrMobile, T1.UsrEmail, T1.UsrEnabled, T1.UsrCanSignonFrom, T1.UsrCanSignonTo, T1.UsrCanOverrideLimits, T1.UsrAcExp, T1.UsrCredentialsExp, T1.UsrAcLocked, T1.UsrLanguage, T1.UsrDftAppCode, T1.UsrBranchCode, T1.UsrDeptCode, T1.UsrToken, T1.UsrIsMultiBranch, T1.UsrInvldLoginTries		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	SecUsers_TEMP AS T1
UNION ALL
SELECT		T1.UsrID, T1.UsrLogin, T1.UsrPwd, T1.UserStaffID, T1.UsrFName, T1.UsrMName, T1.UsrLName, T1.UsrMobile, T1.UsrEmail, T1.UsrEnabled, T1.UsrCanSignonFrom, T1.UsrCanSignonTo, T1.UsrCanOverrideLimits, T1.UsrAcExp, T1.UsrCredentialsExp, T1.UsrAcLocked, T1.UsrLanguage, T1.UsrDftAppCode, T1.UsrBranchCode, T1.UsrDeptCode, T1.UsrToken, T1.UsrIsMultiBranch, T1.UsrInvldLoginTries		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	SecUsers AS T1
WHERE     NOT EXISTS (SELECT 1 FROM SecUsers_TEMP WHERE UsrID = T1.UsrID)

GO
--Sequence Tabele
CREATE TABLE [dbo].[SeqSecUsers](
	[SeqNo] [bigint] NOT NULL
) ON [PRIMARY]

INSERT INTO [dbo].[SeqSecUsers]VALUES (0)

-- Create View for Language Module
CREATE VIEW [dbo].[BMTLanguage_View]
AS
SELECT		T1.LngCode, T1.LngDesc, T1.LngNumber		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	BMTLanguage_TEMP AS T1
UNION ALL
SELECT		T1.LngCode, T1.LngDesc, T1.LngNumber		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	BMTLanguage AS T1
WHERE     NOT EXISTS (SELECT 1 FROM BMTLanguage_TEMP WHERE LngCode = T1.LngCode)

GO

-- Create View for AccountEngineEvent Module
CREATE VIEW [dbo].[BMTAEEvents_View]
AS
SELECT		T1.AEEventCode, T1.AEEventCodeDesc		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	BMTAEEvents_TEMP AS T1
UNION ALL
SELECT		T1.AEEventCode, T1.AEEventCodeDesc		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	BMTAEEvents AS T1
WHERE     NOT EXISTS (SELECT 1 FROM BMTAEEvents_TEMP WHERE AEEventCode = T1.AEEventCode)

GO

-- Create View for AccountEngineRule Module
CREATE VIEW [dbo].[RMTAERules_View]
AS
SELECT		T1.AEEvent, T1.AERule, T1.AERuleDesc, T1.AEIsSysDefault		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTAERules_TEMP AS T1
UNION ALL
SELECT		T1.AEEvent, T1.AERule, T1.AERuleDesc, T1.AEIsSysDefault		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTAERules AS T1
WHERE     NOT EXISTS (SELECT 1 FROM RMTAERules_TEMP WHERE AEEvent = T1.AEEvent)

GO

-- Create View for ScheduleMethod Module
CREATE VIEW [dbo].[BMTSchdMethod_View]
AS
SELECT		T1.SchdMethod, T1.SchdMethodDesc		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	BMTSchdMethod_TEMP AS T1
UNION ALL
SELECT		T1.SchdMethod, T1.SchdMethodDesc		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	BMTSchdMethod AS T1
WHERE     NOT EXISTS (SELECT 1 FROM BMTSchdMethod_TEMP WHERE SchdMethod = T1.SchdMethod)

GO

-- Create View for RepaymentMethod Module
CREATE VIEW [dbo].[BMTRepayMethod_View]
AS
SELECT		T1.RepayMethod, T1.RepayMethodDesc		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	BMTRepayMethod_TEMP AS T1
UNION ALL
SELECT		T1.RepayMethod, T1.RepayMethodDesc		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	BMTRepayMethod AS T1
WHERE     NOT EXISTS (SELECT 1 FROM BMTRepayMethod_TEMP WHERE RepayMethod = T1.RepayMethod)

GO

-- Create View for LatePaymentRule Module
CREATE VIEW [dbo].[RMTLPRule_View]
AS
SELECT		T1.LPRule, T1.LPRuleDesc, T1.LPIsSysDefault, T1.LPGraceDays, T1.LPFeeAc, T1.LPCharityAc		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTLPRule_TEMP AS T1
UNION ALL
SELECT		T1.LPRule, T1.LPRuleDesc, T1.LPIsSysDefault, T1.LPGraceDays, T1.LPFeeAc, T1.LPCharityAc		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTLPRule AS T1
WHERE     NOT EXISTS (SELECT 1 FROM RMTLPRule_TEMP WHERE LPRule = T1.LPRule)

GO

-- Create View for LatePaymentRuleSlabs Module
CREATE VIEW [dbo].[RMTLPRuleSlabs_View]
AS
SELECT		T1.LPSRule, T1.LPSDueDays, T1.LPSFeeType, T1.LPSFeeAmount, T1.LPSFeeOn, T1.LPSFeePercent, T1.LPSFeeShare, T1.LPSIsAlwFeeWaiver, T1.LPSWaiverFromFee		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTLPRuleSlabs_TEMP AS T1
UNION ALL
SELECT		T1.LPSRule, T1.LPSDueDays, T1.LPSFeeType, T1.LPSFeeAmount, T1.LPSFeeOn, T1.LPSFeePercent, T1.LPSFeeShare, T1.LPSIsAlwFeeWaiver, T1.LPSWaiverFromFee		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTLPRuleSlabs AS T1
WHERE     NOT EXISTS (SELECT 1 FROM RMTLPRuleSlabs_TEMP WHERE LPSRule = T1.LPSRule)

GO

-- Create View for EarlyPaymentRule Module
CREATE VIEW [dbo].[RMTEPRule_View]
AS
SELECT		T1.EPRule, T1.EPRuleDesc, T1.EPIsSysDefault, T1.EPFeeAc, T1.EPCharityAc		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTEPRule_TEMP AS T1
UNION ALL
SELECT		T1.EPRule, T1.EPRuleDesc, T1.EPIsSysDefault, T1.EPFeeAc, T1.EPCharityAc		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTEPRule AS T1
WHERE     NOT EXISTS (SELECT 1 FROM RMTEPRule_TEMP WHERE EPRule = T1.EPRule)

GO

-- Create View for EarlyPaymentRuleSlabs Module
CREATE VIEW [dbo].[RMTEPRuleSlabs_View]
AS
SELECT		T1.EPSRule, T1.EPSEarlyDays, T1.EPSFlatFee, T1.EPSRefundPercent, T1.EPSFeeShare, T1.EPSIsAlwFeeWaiver, T1.EPSWaiverFromFee, T1.EPSIsAlwRefundChg		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTEPRuleSlabs_TEMP AS T1
UNION ALL
SELECT		T1.EPSRule, T1.EPSEarlyDays, T1.EPSFlatFee, T1.EPSRefundPercent, T1.EPSFeeShare, T1.EPSIsAlwFeeWaiver, T1.EPSWaiverFromFee, T1.EPSIsAlwRefundChg		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTEPRuleSlabs AS T1
WHERE     NOT EXISTS (SELECT 1 FROM RMTEPRuleSlabs_TEMP WHERE EPSRule = T1.EPSRule)

GO

-- Create View for FinanceType Module
CREATE VIEW [dbo].[RMTFinanceTypes_View]
AS
SELECT		T1.FinType, T1.FinTypeDesc, T1.FinCcy, T1.FinBasicType, T1.FinDaysCalType, T1.FinAcType, T1.FinContingentAcType, T1.FinIsGenRef, T1.FinMaxAmount, T1.FinMinAmount, T1.FinDisburseAc, T1.FinIsOpenNewFinAc, T1.FinDftStmtFrq, T1.FinDftStmtFrqDay, T1.FinIsAlwMD, T1.FinSchdMthd, T1.FInIsAlwGrace, T1.FinHistRetension, T1.FinIsInsureReq, T1.FinIsCollateralReq, T1.FinRateType, T1.FinBaseRate, T1.FinSplRate, T1.FinIntRate, T1.FInMinRate, T1.FinMaxRate, T1.FinDftIntFrq, T1.FinDftIntFrqDay, T1.FinIsIntCpz, T1.FinCpzFrq, T1.FinCpzFrqDay, T1.FinIsRvwAlw, T1.FinRvwFrq, T1.FinRvwFrqDay, T1.FinGrcRateType, T1.FinGrcBaseRate, T1.FinGrcSplRate, T1.FinGrcIntRate, T1.FInGrcMinRate, T1.FinGrcMaxRate, T1.FinGrcDftIntFrq, T1.FinGrcDftIntFrqDay, T1.FinGrcIsIntCpz, T1.FinGrcCpzFrq, T1.FinGrcCpzFrqDay, T1.FinGrcIsRvwAlw, T1.FinGrcRvwFrq, T1.FinGrcRvwFrqDay, T1.FinMinTerm, T1.FinMaxTerm, T1.FinDftTerms, T1.FinRpyFrq, T1.FinRpyFrqDay, T1.FInRepayMethod, T1.FinIsAlwPartialRpy, T1.FinIsAlwDifferment, T1.FinIsIncreaseAlw, T1.FinIsAlwEarlyRpy, T1.FinIsAlwEarlySettle, T1.FinODRpyTries, T1.FinLatePayRule, T1.FinEarlyPayRule, T1.FinEarlySettleRule, T1.FinAEAddDsbOD, T1.FinAEAddDsbFD, T1.FinAEAddDsbFDA, T1.FinAEAmzNorm, T1.FinAEAmzSusp, T1.FinAEToNoAmz, T1.FinToAmz, T1.FinAEIncPft, T1.FinAEDecPft, T1.FinAERepay, T1.FinAEEarlyPay, T1.FinAEEarlySettle, T1.FinAEWriteOff, T1.FinIsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTFinanceTypes_TEMP AS T1
UNION ALL
SELECT		T1.FinType, T1.FinTypeDesc, T1.FinCcy, T1.FinBasicType, T1.FinDaysCalType, T1.FinAcType, T1.FinContingentAcType, T1.FinIsGenRef, T1.FinMaxAmount, T1.FinMinAmount, T1.FinDisburseAc, T1.FinIsOpenNewFinAc, T1.FinDftStmtFrq, T1.FinDftStmtFrqDay, T1.FinIsAlwMD, T1.FinSchdMthd, T1.FInIsAlwGrace, T1.FinHistRetension, T1.FinIsInsureReq, T1.FinIsCollateralReq, T1.FinRateType, T1.FinBaseRate, T1.FinSplRate, T1.FinIntRate, T1.FInMinRate, T1.FinMaxRate, T1.FinDftIntFrq, T1.FinDftIntFrqDay, T1.FinIsIntCpz, T1.FinCpzFrq, T1.FinCpzFrqDay, T1.FinIsRvwAlw, T1.FinRvwFrq, T1.FinRvwFrqDay, T1.FinGrcRateType, T1.FinGrcBaseRate, T1.FinGrcSplRate, T1.FinGrcIntRate, T1.FInGrcMinRate, T1.FinGrcMaxRate, T1.FinGrcDftIntFrq, T1.FinGrcDftIntFrqDay, T1.FinGrcIsIntCpz, T1.FinGrcCpzFrq, T1.FinGrcCpzFrqDay, T1.FinGrcIsRvwAlw, T1.FinGrcRvwFrq, T1.FinGrcRvwFrqDay, T1.FinMinTerm, T1.FinMaxTerm, T1.FinDftTerms, T1.FinRpyFrq, T1.FinRpyFrqDay, T1.FInRepayMethod, T1.FinIsAlwPartialRpy, T1.FinIsAlwDifferment, T1.FinIsIncreaseAlw, T1.FinIsAlwEarlyRpy, T1.FinIsAlwEarlySettle, T1.FinODRpyTries, T1.FinLatePayRule, T1.FinEarlyPayRule, T1.FinEarlySettleRule, T1.FinAEAddDsbOD, T1.FinAEAddDsbFD, T1.FinAEAddDsbFDA, T1.FinAEAmzNorm, T1.FinAEAmzSusp, T1.FinAEToNoAmz, T1.FinToAmz, T1.FinAEIncPft, T1.FinAEDecPft, T1.FinAERepay, T1.FinAEEarlyPay, T1.FinAEEarlySettle, T1.FinAEWriteOff, T1.FinIsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTFinanceTypes AS T1
WHERE     NOT EXISTS (SELECT 1 FROM RMTFinanceTypes_TEMP WHERE FinType = T1.FinType)

GO

-- Create View for WeekendMaster Module
CREATE VIEW [dbo].[SMTWeekendMaster_View]
AS
SELECT		T1.WeekendCode, T1.WeekendDesc, T1.Weekend		
			, T1.Version , T1.LastMntBy, T1.LastMntOn
FROM    	SMTWeekendMaster_TEMP AS T1
UNION ALL
SELECT		T1.WeekendCode, T1.WeekendDesc, T1.Weeken		
			, T1.Version , T1.LastMntBy, T1.LastMntOn
FROM    	SMTWeekendMaster AS T1
WHERE     NOT EXISTS (SELECT 1 FROM SMTWeekendMaster_TEMP WHERE WeekendCode = T1.WeekendCode)

GO


-- Create View for PFSParameter Module
CREATE VIEW [dbo].[SMTparameters_View]
AS
SELECT		T1.SysParmCode, T1.SysParmDesc, T1.SysParmType, T1.SysParmMaint, T1.SysParmValue, T1.SysParmLength, T1.SysParmDec, T1.SysParmList, T1.SysParmValdMod, T1.SysParmDescription		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	SMTparameters_TEMP AS T1
UNION ALL
SELECT		T1.SysParmCode, T1.SysParmDesc, T1.SysParmType, T1.SysParmMaint, T1.SysParmValue, T1.SysParmLength, T1.SysParmDec, T1.SysParmList, T1.SysParmValdMod, T1.SysParmDescription		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	SMTparameters AS T1
WHERE     NOT EXISTS (SELECT 1 FROM SMTparameters_TEMP WHERE SysParmCode = T1.SysParmCode)

GO

-- Create View for PFSParameter Module
CREATE VIEW [dbo].[SMTparameters_View]
AS
SELECT		T1.SysParmCode, T1.SysParmDesc, T1.SysParmType, T1.SysParmMaint, T1.SysParmValue, T1.SysParmLength, T1.SysParmDec, T1.SysParmList, T1.SysParmValdMod, T1.SysParmDescription		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	SMTparameters_TEMP AS T1
UNION ALL
SELECT		T1.SysParmCode, T1.SysParmDesc, T1.SysParmType, T1.SysParmMaint, T1.SysParmValue, T1.SysParmLength, T1.SysParmDec, T1.SysParmList, T1.SysParmValdMod, T1.SysParmDescription		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	SMTparameters AS T1
WHERE     NOT EXISTS (SELECT 1 FROM SMTparameters_TEMP WHERE SysParmCode = T1.SysParmCode)

GO

-- Create View for Product Module
CREATE VIEW [dbo].[BMTProduct_View]
AS
SELECT		T1.ProductCode, T1.ProductDesc		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	BMTProduct_TEMP AS T1
UNION ALL
SELECT		T1.ProductCode, T1.ProductDesc		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	BMTProduct AS T1
WHERE     NOT EXISTS (SELECT 1 FROM BMTProduct_TEMP WHERE ProductCode = T1.ProductCode)

GO

-- Create View for DispatchMode Module
CREATE VIEW [dbo].[BMTDispatchModes_View]
AS
SELECT		T1.DispatchModeCode, T1.DispatchModeDesc, T1.DispatchModeIsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	BMTDispatchModes_TEMP AS T1
UNION ALL
SELECT		T1.DispatchModeCode, T1.DispatchModeDesc, T1.DispatchModeIsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	BMTDispatchModes AS T1
WHERE     NOT EXISTS (SELECT 1 FROM BMTDispatchModes_TEMP WHERE DispatchModeCode = T1.DispatchModeCode)

GO


-- Create View for ProductFinanceType Module
CREATE VIEW [dbo].[RMTProductFinanceTypes_View]
AS
SELECT		T1.PrdFinId, T1.ProductCode, T1.FinType		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTProductFinanceTypes_TEMP AS T1
UNION ALL
SELECT		T1.PrdFinId, T1.ProductCode, T1.FinType		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTProductFinanceTypes AS T1
WHERE     NOT EXISTS (SELECT 1 FROM RMTProductFinanceTypes_TEMP WHERE PrdFinId = T1.PrdFinId)

GO
--Sequence Tabele
CREATE TABLE [dbo].[SeqRMTProductFinanceTypes](
	[SeqNo] [bigint] NOT NULL
) ON [PRIMARY]

INSERT INTO [dbo].[SeqRMTProductFinanceTypes]VALUES (0)



-- Create View for DiaryNotes Module
CREATE VIEW [dbo].[DiaryNotes_View]
AS
SELECT		T1.SeqNo, T1.DnType, T1.DnCreatedNo, T1.DnCreatedName, T1.FrqCode, T1.FirstActionDate, T1.NextActionDate, T1.LastActionDate, T1.FinalActionDate, T1.Suspend, T1.SuspendStartDate, T1.SuspendEndDate, T1.RecordDeleted, T1.Narration		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	DiaryNotes_TEMP AS T1
UNION ALL
SELECT		T1.SeqNo, T1.DnType, T1.DnCreatedNo, T1.DnCreatedName, T1.FrqCode, T1.FirstActionDate, T1.NextActionDate, T1.LastActionDate, T1.FinalActionDate, T1.Suspend, T1.SuspendStartDate, T1.SuspendEndDate, T1.RecordDeleted, T1.Narration		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	DiaryNotes AS T1
WHERE     NOT EXISTS (SELECT 1 FROM DiaryNotes_TEMP WHERE SeqNo = T1.SeqNo)

GO
--Sequence Tabele
CREATE TABLE [dbo].[SeqDiaryNotes](
	[SeqNo] [bigint] NOT NULL
) ON [PRIMARY]

INSERT INTO [dbo].[SeqDiaryNotes]VALUES (0)


-- Create View for DedupFields Module
CREATE VIEW [dbo].[DedupFields_View]
AS
SELECT		T1.FieldName, T1.FieldControl		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	DedupFields AS T1
GO

-- Create View for DedupParm Module
CREATE VIEW [dbo].[DedupParams_View]
AS
SELECT		T1.QueryCode, T1.QueryModule, T1.SQLQuery, T1.ActualBlock		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	DedupParams_TEMP AS T1
UNION ALL
SELECT		T1.QueryCode, T1.QueryModule, T1.SQLQuery, T1.ActualBlock		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	DedupParams AS T1
WHERE     NOT EXISTS (SELECT 1 FROM DedupParams_TEMP WHERE QueryCode = T1.QueryCode)

GO

-- Create View for WhatIfInquiry Module
CREATE VIEW [dbo].[RMTWhatIfInquiry_View]
AS
SELECT		T1.WIFinReference, T1.WIFinType, T1.WIFinName, T1.WIFinCcy, T1.WIFinScheduleType, T1.WIFinProfitDaysBasis, T1.WIFinStartDate, T1.WIFinAmount, T1.WIFinGracePeriodEndDate, T1.WIFinBaseRate, T1.WIFinSplRate, T1.WIFinProfitRate, T1.WIFinProfitFrq, T1.WIFinNextProfitDate, T1.WIFinReviewFrq, T1.WIFinNextProfitReviewDate, T1.WIFinCapitalizeFrq, T1.WIFinNextCapitalizeDate, T1.WIFinNumberOfTerms, T1.WIFinRepaymentAmount, T1.WIFinRepaymentFrq, T1.WIFinNextRepaymentDate, T1.WIFinMaturityDate, T1.WIFinRepaymentAccount		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTWhatIfInquiry_TEMP AS T1
UNION ALL
SELECT		T1.WIFinReference, T1.WIFinType, T1.WIFinName, T1.WIFinCcy, T1.WIFinScheduleType, T1.WIFinProfitDaysBasis, T1.WIFinStartDate, T1.WIFinAmount, T1.WIFinGracePeriodEndDate, T1.WIFinBaseRate, T1.WIFinSplRate, T1.WIFinProfitRate, T1.WIFinProfitFrq, T1.WIFinNextProfitDate, T1.WIFinReviewFrq, T1.WIFinNextProfitReviewDate, T1.WIFinCapitalizeFrq, T1.WIFinNextCapitalizeDate, T1.WIFinNumberOfTerms, T1.WIFinRepaymentAmount, T1.WIFinRepaymentFrq, T1.WIFinNextRepaymentDate, T1.WIFinMaturityDate, T1.WIFinRepaymentAccount		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTWhatIfInquiry AS T1
WHERE     NOT EXISTS (SELECT 1 FROM RMTWhatIfInquiry_TEMP WHERE WIFinReference = T1.WIFinReference)

GO
--Sequence Tabele
CREATE TABLE [dbo].[SeqRMTWhatIfInquiry](
	[SeqNo] [bigint] NOT NULL
) ON [PRIMARY]

INSERT INTO [dbo].[SeqRMTWhatIfInquiry]VALUES (20111201)


-- Create View for Fee Module
CREATE VIEW [dbo].[Fees_View]
AS
SELECT		T1.FeeTranEvent, T1.FeeCode, T1.FeeDesc, T1.FeeAmountCode, T1.FeeSplitAmtTier, T1.FeeRestrictMin, T1.FeeRestrictMax, T1.FeeMin, T1.FeeMax, T1.IsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	Fees_TEMP AS T1
UNION ALL
SELECT		T1.FeeTranEvent, T1.FeeCode, T1.FeeDesc, T1.FeeAmountCode, T1.FeeSplitAmtTier, T1.FeeRestrictMin, T1.FeeRestrictMax, T1.FeeMin, T1.FeeMax, T1.IsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	Fees AS T1
WHERE     NOT EXISTS (SELECT 1 FROM Fees_TEMP WHERE FeeTranEvent = T1.FeeTranEvent)

GO

-- Create View for Course Module
CREATE VIEW [dbo].[AMTCourse_View]
AS
SELECT		T1.CourseName, T1.CourseDesc		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	AMTCourse_TEMP AS T1
UNION ALL
SELECT		T1.CourseName, T1.CourseDesc		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	AMTCourse AS T1
WHERE     NOT EXISTS (SELECT 1 FROM AMTCourse_TEMP WHERE CourseName = T1.CourseName)


GO

-- Create View for CourseType Module
CREATE VIEW [dbo].[AMTCourseType_View]
AS
SELECT		T1.CourseTypeCode, T1.CourseTypeDesc		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	AMTCourseType_TEMP AS T1
UNION ALL
SELECT		T1.CourseTypeCode, T1.CourseTypeDesc		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	AMTCourseType AS T1
WHERE     NOT EXISTS (SELECT 1 FROM AMTCourseType_TEMP WHERE CourseTypeCode = T1.CourseTypeCode)

GO

-- Create View for OwnerShipType Module
CREATE VIEW [dbo].[AMTOwnerShipType_View]
AS
SELECT		T1.OwnerShipTypeId, T1.OwnerShipTypeName		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	AMTOwnerShipType_TEMP AS T1
UNION ALL
SELECT		T1.OwnerShipTypeId, T1.OwnerShipTypeName		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	AMTOwnerShipType AS T1
WHERE     NOT EXISTS (SELECT 1 FROM AMTOwnerShipType_TEMP WHERE OwnerShipTypeId = T1.OwnerShipTypeId)

GO
--Sequence Tabele
CREATE TABLE [dbo].[SeqAMTOwnerShipType](
	[SeqNo] [bigint] NOT NULL
) ON [PRIMARY]

INSERT INTO [dbo].[SeqAMTOwnerShipType]VALUES (0)

-- Create View for PropertyRelationType Module
CREATE VIEW [dbo].[AMTPropertyRelationType_View]
AS
SELECT		T1.PropertyRelationTypeId, T1.PropertyRelationTypeName		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	AMTPropertyRelationType_TEMP AS T1
UNION ALL
SELECT		T1.PropertyRelationTypeId, T1.PropertyRelationTypeName		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	AMTPropertyRelationType AS T1
WHERE     NOT EXISTS (SELECT 1 FROM AMTPropertyRelationType_TEMP WHERE PropertyRelationTypeId = T1.PropertyRelationTypeId)

GO

-- Create View for PropertyType Module
CREATE VIEW [dbo].[AMTPropertyType_View]
AS
SELECT		T1.PropertyTypeId, T1.PropertyTypeName		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	AMTPropertyType_TEMP AS T1
UNION ALL
SELECT		T1.PropertyTypeId, T1.PropertyTypeName		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	AMTPropertyType AS T1
WHERE     NOT EXISTS (SELECT 1 FROM AMTPropertyType_TEMP WHERE PropertyTypeId = T1.PropertyTypeId)

GO
--Sequence Tabele
CREATE TABLE [dbo].[SeqAMTPropertyType](
	[SeqNo] [bigint] NOT NULL
) ON [PRIMARY]

INSERT INTO [dbo].[SeqAMTPropertyType]VALUES (0)

-- Create View for VehicleDealer Module
CREATE VIEW [dbo].[AMTVehicleDealer_View]
AS
SELECT		T1.DealerId, T1.DealerName		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	AMTVehicleDealer_TEMP AS T1
UNION ALL
SELECT		T1.DealerId, T1.DealerName		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	AMTVehicleDealer AS T1
WHERE     NOT EXISTS (SELECT 1 FROM AMTVehicleDealer_TEMP WHERE DealerId = T1.DealerId)

GO
--Sequence Tabele
CREATE TABLE [dbo].[SeqAMTVehicleDealer](
	[SeqNo] [bigint] NOT NULL
) ON [PRIMARY]

INSERT INTO [dbo].[SeqAMTVehicleDealer]VALUES (0)


-- Create View for VehicleManufacturer Module
CREATE VIEW [dbo].[AMTVehicleManufacturer_View]
AS
SELECT		T1.ManufacturerId, T1.ManufacturerName		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	AMTVehicleManufacturer_TEMP AS T1
UNION ALL
SELECT		T1.ManufacturerId, T1.ManufacturerName		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	AMTVehicleManufacturer AS T1
WHERE     NOT EXISTS (SELECT 1 FROM AMTVehicleManufacturer_TEMP WHERE ManufacturerId = T1.ManufacturerId)

GO
--Sequence Tabele
CREATE TABLE [dbo].[SeqAMTVehicleManufacturer](
	[SeqNo] [bigint] NOT NULL
) ON [PRIMARY]

INSERT INTO [dbo].[SeqAMTVehicleManufacturer]VALUES (0)

-- Create View for VehicleModel Module
CREATE VIEW [dbo].[AMTVehicleModel_View]
AS
SELECT		T1.VehicleModelId, T1.VehicleModelDesc		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	AMTVehicleModel_TEMP AS T1
UNION ALL
SELECT		T1.VehicleModelId, T1.VehicleModelDesc		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	AMTVehicleModel AS T1
WHERE     NOT EXISTS (SELECT 1 FROM AMTVehicleModel_TEMP WHERE VehicleModelId = T1.VehicleModelId)

GO
--Sequence Tabele
CREATE TABLE [dbo].[SeqAMTVehicleModel](
	[SeqNo] [bigint] NOT NULL
) ON [PRIMARY]

INSERT INTO [dbo].[SeqAMTVehicleModel]VALUES (0)


-- Create View for ExpenseType Module
CREATE VIEW [dbo].[AMTExpenseType_View]
AS
SELECT		T1.ExpenceTypeId, T1.ExpenceTypeName		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	AMTExpenseType_TEMP AS T1
UNION ALL
SELECT		T1.ExpenceTypeId, T1.ExpenceTypeName		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	AMTExpenseType AS T1
WHERE     NOT EXISTS (SELECT 1 FROM AMTExpenseType_TEMP WHERE ExpenceTypeId = T1.ExpenceTypeId)

GO
--Sequence Tabele
CREATE TABLE [dbo].[SeqAMTExpenseType](
	[SeqNo] [bigint] NOT NULL
) ON [PRIMARY]

INSERT INTO [dbo].[SeqAMTExpenseType]VALUES (0)

-- Create View for PropertyDetail Module
CREATE VIEW [dbo].[AMTPropertyDetail_View]
AS
SELECT		T1.PropertyDetailId, T1.PropertyDetailDesc		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	AMTPropertyDetail_TEMP AS T1
UNION ALL
SELECT		T1.PropertyDetailId, T1.PropertyDetailDesc		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	AMTPropertyDetail AS T1
WHERE     NOT EXISTS (SELECT 1 FROM AMTPropertyDetail_TEMP WHERE PropertyDetailId = T1.PropertyDetailId)

GO
--Sequence Tabele
CREATE TABLE [dbo].[SeqAMTPropertyDetail](
	[SeqNo] [bigint] NOT NULL
) ON [PRIMARY]

INSERT INTO [dbo].[SeqAMTPropertyDetail]VALUES (0)

-- Create View for LovFieldCode Module
CREATE VIEW [dbo].[BMTLovFieldCode_View]
AS
SELECT		T1.FieldCode, T1.FieldCodeDesc, T1.FieldCodeType, T1.isActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	BMTLovFieldCode_TEMP AS T1
UNION ALL
SELECT		T1.FieldCode, T1.FieldCodeDesc, T1.FieldCodeType, T1.isActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	BMTLovFieldCode AS T1
WHERE     NOT EXISTS (SELECT 1 FROM BMTLovFieldCode_TEMP WHERE FieldCode = T1.FieldCode)

GO

-- Create View for LovFieldDetail Module
CREATE VIEW [dbo].[RMTLovFieldDetail_View]
AS
SELECT		T1.FieldCodeId, T1.FieldCode, T1.FieldCodeValue, T1.isActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTLovFieldDetail_TEMP AS T1
UNION ALL
SELECT		T1.FieldCodeId, T1.FieldCode, T1.FieldCodeValue, T1.isActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTLovFieldDetail AS T1
WHERE     NOT EXISTS (SELECT 1 FROM RMTLovFieldDetail_TEMP WHERE FieldCodeId = T1.FieldCodeId)

GO
--Sequence Tabele
CREATE TABLE [dbo].[SeqRMTLovFieldDetail](
	[SeqNo] [bigint] NOT NULL
) ON [PRIMARY]

INSERT INTO [dbo].[SeqRMTLovFieldDetail]VALUES (0)

-- Create View for VehicleVersion Module
CREATE VIEW [dbo].[AMTVehicleVersion_View]
AS
SELECT		T1.VehicleVersionId, T1.VehicleModelId, T1.VehicleVersionCode		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	AMTVehicleVersion_TEMP AS T1
UNION ALL
SELECT		T1.VehicleVersionId, T1.VehicleModelId, T1.VehicleVersionCode		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	AMTVehicleVersion AS T1
WHERE     NOT EXISTS (SELECT 1 FROM AMTVehicleVersion_TEMP WHERE VehicleVersionId = T1.VehicleVersionId)

GO
--Sequence Tabele
CREATE TABLE [dbo].[SeqAMTVehicleVersion](
	[SeqNo] [bigint] NOT NULL
) ON [PRIMARY]

INSERT INTO [dbo].[SeqAMTVehicleVersion]VALUES (0)

-- Create View for EducationalLoan Module
CREATE VIEW [dbo].[LMTEducationLoanDetail_View]
AS
SELECT		T1.EduLoanId, T1.LoanRefNumber, T1.LoanRefType, T1.EduCourse, T1.EduSpecialization, T1.EduCourseType, T1.EduCourseFrom, T1.EduCourseFromBranch, T1.EduAffiliatedTo, T1.EduCommenceDate, T1.EduCompletionDate, T1.EduExpectedIncome, T1.EduLoanFromBranch		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	LMTEducationLoanDetail_TEMP AS T1
UNION ALL
SELECT		T1.EduLoanId, T1.LoanRefNumber, T1.LoanRefType, T1.EduCourse, T1.EduSpecialization, T1.EduCourseType, T1.EduCourseFrom, T1.EduCourseFromBranch, T1.EduAffiliatedTo, T1.EduCommenceDate, T1.EduCompletionDate, T1.EduExpectedIncome, T1.EduLoanFromBranch		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	LMTEducationLoanDetail AS T1
WHERE     NOT EXISTS (SELECT 1 FROM LMTEducationLoanDetail_TEMP WHERE EduLoanId = T1.EduLoanId)

GO
--Sequence Tabele
CREATE TABLE [dbo].[SeqLMTEducationLoanDetail](
	[SeqNo] [bigint] NOT NULL
) ON [PRIMARY]

INSERT INTO [dbo].[SeqLMTEducationLoanDetail]VALUES (0)

-- Create View for EducationalExpense Module
CREATE VIEW [dbo].[LMTEduExpenseDetail_View]
AS
SELECT		T1.EduExpDetailId, T1.EduLoanId, T1.EduExpDetail, T1.EduExpAmount, T1.EduExpDate		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	LMTEduExpenseDetail_TEMP AS T1
UNION ALL
SELECT		T1.EduExpDetailId, T1.EduLoanId, T1.EduExpDetail, T1.EduExpAmount, T1.EduExpDate		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	LMTEduExpenseDetail AS T1
WHERE     NOT EXISTS (SELECT 1 FROM LMTEduExpenseDetail_TEMP WHERE EduExpDetailId = T1.EduExpDetailId)

GO
--Sequence Tabele
CREATE TABLE [dbo].[SeqLMTEduExpenseDetail](
	[SeqNo] [bigint] NOT NULL
) ON [PRIMARY]

INSERT INTO [dbo].[SeqLMTEduExpenseDetail]VALUES (0)

-- Create View for CarLoanDetail Module
CREATE VIEW [dbo].[LMTCarLoanDetail_View]
AS
SELECT		T1.CarLoanId, T1.LoanRefNumber, T1.LoanRefType, T1.CarLoanFor, T1.CarUsage, T1.CarVersion, T1.CarMakeYear, T1.CarCapacity, T1.CarDealer		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	LMTCarLoanDetail_TEMP AS T1
UNION ALL
SELECT		T1.CarLoanId, T1.LoanRefNumber, T1.LoanRefType, T1.CarLoanFor, T1.CarUsage, T1.CarVersion, T1.CarMakeYear, T1.CarCapacity, T1.CarDealer		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	LMTCarLoanDetail AS T1
WHERE     NOT EXISTS (SELECT 1 FROM LMTCarLoanDetail_TEMP WHERE CarLoanId = T1.CarLoanId)

GO
--Sequence Tabele
CREATE TABLE [dbo].[SeqLMTCarLoanDetail](
	[SeqNo] [bigint] NOT NULL
) ON [PRIMARY]

INSERT INTO [dbo].[SeqLMTCarLoanDetail]VALUES (0)

-- Create View for HomeLoanDetail Module
CREATE VIEW [dbo].[LMTHomeLoanDetail_View]
AS
SELECT		T1.HomeLoanId, T1.LoanRefNumber, T1.LoanRefType, T1.HomeDetails, T1.HomeBuilderName, T1.HomeCostPerFlat, T1.HomeCostOfLand, T1.HomeCostOfConstruction, T1.HomeConstructionStage, T1.HomeDateOfPocession, T1.HomeAreaOfLand, T1.HomeAreaOfFlat, T1.HomePropertyType, T1.HomeOwnerShipType, T1.HomeAddrFlatNbr, T1.HomeAddrStreet, T1.HomeAddrLane1, T1.HomeAddrLane2, T1.HomeAddrPOBox, T1.HomeAddrCountry, T1.HomeAddrProvince, T1.HomeAddrCity, T1.HomeAddrZIP, T1.HomeAddrPhone		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	LMTHomeLoanDetail_TEMP AS T1
UNION ALL
SELECT		T1.HomeLoanId, T1.LoanRefNumber, T1.LoanRefType, T1.HomeDetails, T1.HomeBuilderName, T1.HomeCostPerFlat, T1.HomeCostOfLand, T1.HomeCostOfConstruction, T1.HomeConstructionStage, T1.HomeDateOfPocession, T1.HomeAreaOfLand, T1.HomeAreaOfFlat, T1.HomePropertyType, T1.HomeOwnerShipType, T1.HomeAddrFlatNbr, T1.HomeAddrStreet, T1.HomeAddrLane1, T1.HomeAddrLane2, T1.HomeAddrPOBox, T1.HomeAddrCountry, T1.HomeAddrProvince, T1.HomeAddrCity, T1.HomeAddrZIP, T1.HomeAddrPhone		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	LMTHomeLoanDetail AS T1
WHERE     NOT EXISTS (SELECT 1 FROM LMTHomeLoanDetail_TEMP WHERE HomeLoanId = T1.HomeLoanId)

GO
--Sequence Tabele
CREATE TABLE [dbo].[SeqLMTHomeLoanDetail](
	[SeqNo] [bigint] NOT NULL
) ON [PRIMARY]

INSERT INTO [dbo].[SeqLMTHomeLoanDetail]VALUES (0)

-- Create View for MortgageLoanDetail Module
CREATE VIEW [dbo].[LMTMortgageLoanDetail_View]
AS
SELECT		T1.MortgLoanId, T1.LoanRefNumber, T1.LoanRefType, T1.MortgProperty, T1.MortgCurrentValue, T1.MortgPurposeOfLoan, T1.MortgPropertyRelation, T1.MortgOwnership, T1.MortgAddrHNbr, T1.MortgAddrFlatNbr, T1.MortgAddrStreet, T1.MortgAddrLane1, T1.MortgAddrLane2, T1.MortgAddrPOBox, T1.MortgAddrCountry, T1.MortgAddrProvince, T1.MortgAddrCity, T1.MortgAddrZIP, T1.MortgAddrPhone		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	LMTMortgageLoanDetail_TEMP AS T1
UNION ALL
SELECT		T1.MortgLoanId, T1.LoanRefNumber, T1.LoanRefType, T1.MortgProperty, T1.MortgCurrentValue, T1.MortgPurposeOfLoan, T1.MortgPropertyRelation, T1.MortgOwnership, T1.MortgAddrHNbr, T1.MortgAddrFlatNbr, T1.MortgAddrStreet, T1.MortgAddrLane1, T1.MortgAddrLane2, T1.MortgAddrPOBox, T1.MortgAddrCountry, T1.MortgAddrProvince, T1.MortgAddrCity, T1.MortgAddrZIP, T1.MortgAddrPhone		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	LMTMortgageLoanDetail AS T1
WHERE     NOT EXISTS (SELECT 1 FROM LMTMortgageLoanDetail_TEMP WHERE MortgLoanId = T1.MortgLoanId)

GO
--Sequence Tabele
CREATE TABLE [dbo].[SeqLMTMortgageLoanDetail](
	[SeqNo] [bigint] NOT NULL
) ON [PRIMARY]

INSERT INTO [dbo].[SeqLMTMortgageLoanDetail]VALUES (0)

-- Create View for EligibilityRules Module
CREATE VIEW [dbo].[LMTEligibilityRules_View]
AS
SELECT		T1.ElgRuleType, T1.ElgRuleCode, T1.ElgRuleDesc, T1.ElgRuleValue		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	LMTEligibilityRules_TEMP AS T1
UNION ALL
SELECT		T1.ElgRuleType, T1.ElgRuleCode, T1.ElgRuleDesc, T1.ElgRuleValue		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	LMTEligibilityRules AS T1
WHERE     NOT EXISTS (SELECT 1 FROM LMTEligibilityRules_TEMP WHERE ElgRuleType = T1.ElgRuleType)

GO

-- Create View for ScoringType Module
CREATE VIEW [dbo].[BMTScoringType_View]
AS
SELECT		T1.ScoType, T1.ScoDesc		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	BMTScoringType_TEMP AS T1
UNION ALL
SELECT		T1.ScoType, T1.ScoDesc		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	BMTScoringType AS T1
WHERE     NOT EXISTS (SELECT 1 FROM BMTScoringType_TEMP WHERE ScoType = T1.ScoType)

GO

-- Create View for ScoringCode Module
CREATE VIEW [dbo].[RMTScoringCode_View]
AS
SELECT		T1.ScoType, T1.ScoCode, T1.ScoCodeDesc, T1.ScoRtnValueType, T1.ScoValue		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTScoringCode_TEMP AS T1
UNION ALL
SELECT		T1.ScoType, T1.ScoCode, T1.ScoCodeDesc, T1.ScoRtnValueType, T1.ScoValue		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTScoringCode AS T1
WHERE     NOT EXISTS (SELECT 1 FROM RMTScoringCode_TEMP WHERE ScoCode = T1.ScoCode)

GO

-- Create View for CommodityBrokerDetail Module
CREATE VIEW [dbo].[FCMTBrokerDetail_View]
AS
SELECT		T1.BrokerCode, T1.BrokerCustID, T1.BrokerFrom, T1.BrokerAddrHNbr, T1.BrokerAddrFlatNbr, T1.BrokerAddrStreet, T1.BrokerAddrLane1, T1.BrokerAddrLane2, T1.BrokerAddrPOBox, T1.BrokerAddrCountry, T1.BrokerAddrProvince, T1.BrokerAddrCity, T1.BrokerAddrZIP, T1.BrokerAddrPhone, T1.BrokerAddrFax, T1.BrokerEmail, T1.AgreementRef		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	FCMTBrokerDetail_TEMP AS T1
UNION ALL
SELECT		T1.BrokerCode, T1.BrokerCustID, T1.BrokerFrom, T1.BrokerAddrHNbr, T1.BrokerAddrFlatNbr, T1.BrokerAddrStreet, T1.BrokerAddrLane1, T1.BrokerAddrLane2, T1.BrokerAddrPOBox, T1.BrokerAddrCountry, T1.BrokerAddrProvince, T1.BrokerAddrCity, T1.BrokerAddrZIP, T1.BrokerAddrPhone, T1.BrokerAddrFax, T1.BrokerEmail, T1.AgreementRef		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	FCMTBrokerDetail AS T1
WHERE     NOT EXISTS (SELECT 1 FROM FCMTBrokerDetail_TEMP WHERE BrokerCode = T1.BrokerCode)

GO

-- Create View for CommodityDetail Module
CREATE VIEW [dbo].[FCMTCommodityDetail_View]
AS
SELECT		T1.CommodityCode, T1.CommodityName, T1.CommodityUnitCode, T1.CommodityUnitName		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	FCMTCommodityDetail_TEMP AS T1
UNION ALL
SELECT		T1.CommodityCode, T1.CommodityName, T1.CommodityUnitCode, T1.CommodityUnitName		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	FCMTCommodityDetail AS T1
WHERE     NOT EXISTS (SELECT 1 FROM FCMTCommodityDetail_TEMP WHERE CommodityCode = T1.CommodityCode)

GO

-- Create View for TransactionCode Module
CREATE VIEW [dbo].[BMTTransactionCode_View]
AS
SELECT		T1.TranCode, T1.TranDesc, T1.TranFlag, T1.TranIsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	BMTTransactionCode_TEMP AS T1
UNION ALL
SELECT		T1.TranCode, T1.TranDesc, T1.TranFlag, T1.TranIsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	BMTTransactionCode AS T1
WHERE     NOT EXISTS (SELECT 1 FROM BMTTransactionCode_TEMP WHERE TranCode = T1.TranCode)

GO

-- Create View for WIFFinanceMain Module
CREATE VIEW [dbo].[WIFFinanceMain_View]
AS
SELECT		T1.FinReference, T1.NumberOfTerms, T1.GracePeriodEndDate, T1.AllowGrcPeriod, T1.GraceBaseRate, T1.GraceSpecialRate, T1.GracePftRate, T1.GracePftFrq, T1.NextGrcPftDate, T1.AllowGrcPftRvw, T1.GracePftRvwFrq, T1.NextGrcPftRvwDate, T1.AllowGrcCpz, T1.GraceCpzFrq, T1.NextGrcCpzDate, T1.RepayBaseRate, T1.RepaySpecialRate, T1.ProfitRate, T1.RepayFrq, T1.NextRepayDate, T1.RepayPftFrq, T1.NextRepayPftDate, T1.AllowRepayRvw, T1.RepayRvwFrq, T1.NextRepayRvwDate, T1.AllowRepayCpz, T1.RepayCpzFrq, T1.NextRepayCpzDate, T1.MaturityDate, T1.CpzAtGraceEnd, T1.DownPayment, T1.GraceFlatAmount, T1.ReqRepayAmount, T1.TotalProfit, T1.TotalGrcProfit, T1.GraceRateBasis, T1.RepayRateBasis, T1.FinType, T1.FinRemarks, T1.FinCcy, T1.ScheduleMethod, T1.ProfitDaysBasis, T1.ReqMaturity, T1.CalTerms, T1.CalMaturity, T1.FirstRepay, T1.LastRepay, T1.FinStartDate, T1.FinAmount, T1.FinRepaymentAmount, T1.CustID, T1.Defferments, T1.FinIsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	WIFFinanceMain_TEMP AS T1
UNION ALL
SELECT		T1.FinReference, T1.NumberOfTerms, T1.GracePeriodEndDate, T1.AllowGrcPeriod, T1.GraceBaseRate, T1.GraceSpecialRate, T1.GracePftRate, T1.GracePftFrq, T1.NextGrcPftDate, T1.AllowGrcPftRvw, T1.GracePftRvwFrq, T1.NextGrcPftRvwDate, T1.AllowGrcCpz, T1.GraceCpzFrq, T1.NextGrcCpzDate, T1.RepayBaseRate, T1.RepaySpecialRate, T1.ProfitRate, T1.RepayFrq, T1.NextRepayDate, T1.RepayPftFrq, T1.NextRepayPftDate, T1.AllowRepayRvw, T1.RepayRvwFrq, T1.NextRepayRvwDate, T1.AllowRepayCpz, T1.RepayCpzFrq, T1.NextRepayCpzDate, T1.MaturityDate, T1.CpzAtGraceEnd, T1.DownPayment, T1.GraceFlatAmount, T1.ReqRepayAmount, T1.TotalProfit, T1.TotalGrcProfit, T1.GraceRateBasis, T1.RepayRateBasis, T1.FinType, T1.FinRemarks, T1.FinCcy, T1.ScheduleMethod, T1.ProfitDaysBasis, T1.ReqMaturity, T1.CalTerms, T1.CalMaturity, T1.FirstRepay, T1.LastRepay, T1.FinStartDate, T1.FinAmount, T1.FinRepaymentAmount, T1.CustID, T1.Defferments, T1.FinIsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	WIFFinanceMain AS T1
WHERE     NOT EXISTS (SELECT 1 FROM WIFFinanceMain_TEMP WHERE FinReference = T1.FinReference)

GO

-- Create View for WIFFinanceDisbursement Module
CREATE VIEW [dbo].[WIFFinDisbursementDetails_View]
AS
SELECT		T1.FinReference, T1.DisbDate, T1.DisbSeq, T1.DisbDesc, T1.DisbAmount, T1.DisbActDate, T1.DisbDisbursed, T1.DisbIsActive, T1.DisbRemarks		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	WIFFinDisbursementDetails_TEMP AS T1
UNION ALL
SELECT		T1.FinReference, T1.DisbDate, T1.DisbSeq, T1.DisbDesc, T1.DisbAmount, T1.DisbActDate, T1.DisbDisbursed, T1.DisbIsActive, T1.DisbRemarks		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	WIFFinDisbursementDetails AS T1
WHERE     NOT EXISTS (SELECT 1 FROM WIFFinDisbursementDetails_TEMP WHERE FinReference = T1.FinReference)

GO

-- Create View for WIFFinanceScheduleDetail Module
CREATE VIEW [dbo].[WIFFinScheduleDetails_View]
AS
SELECT		T1.FinReference, T1.SchDate, T1.SchSeq, T1.PftOnSchDate, T1.CpzOnSchDate, T1.RepayOnSchDate, T1.RvwOnSchDate, T1.DisbOnSchDate, T1.DownpaymentOnSchDate, T1.BalanceForPftCal, T1.BaseRate, T1.SplRate, T1.ActRate, T1.AdjRate, T1.NoOfDays, T1.DayFactor, T1.ProfitCalc, T1.ProfitSchd, T1.PrincipalSchd, T1.RepayAmount, T1.ProfitBalance, T1.DisbAmount, T1.DownPaymentAmount, T1.CpzAmount, T1.DiffProfitSchd, T1.DIffPrincipalSchd, T1.ClosingBalance, T1.ProfitFraction, T1.PrvRepayAmount, T1.DeffProfitBal, T1.DiffPrincipalBal, T1.SchdPriPaid, T1.isSchdPftPaid		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	WIFFinScheduleDetails_TEMP AS T1
UNION ALL
SELECT		T1.FinReference, T1.SchDate, T1.SchSeq, T1.PftOnSchDate, T1.CpzOnSchDate, T1.RepayOnSchDate, T1.RvwOnSchDate, T1.DisbOnSchDate, T1.DownpaymentOnSchDate, T1.BalanceForPftCal, T1.BaseRate, T1.SplRate, T1.ActRate, T1.AdjRate, T1.NoOfDays, T1.DayFactor, T1.ProfitCalc, T1.ProfitSchd, T1.PrincipalSchd, T1.RepayAmount, T1.ProfitBalance, T1.DisbAmount, T1.DownPaymentAmount, T1.CpzAmount, T1.DiffProfitSchd, T1.DIffPrincipalSchd, T1.ClosingBalance, T1.ProfitFraction, T1.PrvRepayAmount, T1.DeffProfitBal, T1.DiffPrincipalBal, T1.SchdPriPaid, T1.isSchdPftPaid		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	WIFFinScheduleDetails AS T1
WHERE     NOT EXISTS (SELECT 1 FROM WIFFinScheduleDetails_TEMP WHERE FinReference = T1.FinReference)

GO

-- Create View for FinanceMain Module
CREATE VIEW [dbo].[FinanceMain_View]
AS
SELECT		T1.FinReference, T1.NumberOfTerms, T1.GracePeriodEndDate, T1.AllowGrcPeriod, T1.GraceBaseRate, T1.GraceSpecialRate, T1.GracePftRate, T1.GracePftFrq, T1.NextGrcPftDate, T1.AllowGrcPftRvw, T1.GracePftRvwFrq, T1.NextGrcPftRvwDate, T1.AllowGrcCpz, T1.GraceCpzFrq, T1.NextGrcCpzDate, T1.RepayBaseRate, T1.RepaySpecialRate, T1.ProfitRate, T1.RepayFrq, T1.NextRepayDate, T1.RepayPftFrq, T1.NextRepayPftDate, T1.AllowRepayRvw, T1.RepayRvwFrq, T1.NextRepayRvwDate, T1.AllowRepayCpz, T1.RepayCpzFrq, T1.NextRepayCpzDate, T1.MaturityDate, T1.CpzAtGraceEnd, T1.DownPayment, T1.GraceFlatAmount, T1.ReqRepayAmount, T1.TotalProfit, T1.TotalGrcProfit, T1.GraceRateBasis, T1.RepayRateBasis, T1.FinType, T1.FinRemarks, T1.FinCcy, T1.ScheduleMethod, T1.ProfitDaysBasis, T1.ReqMaturity, T1.CalTerms, T1.CalMaturity, T1.FirstRepay, T1.LastRepay, T1.FinStartDate, T1.FinAmount, T1.FinRepaymentAmount, T1.CustID, T1.Defferments, T1.FinIsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	FinanceMain_TEMP AS T1
UNION ALL
SELECT		T1.FinReference, T1.NumberOfTerms, T1.GracePeriodEndDate, T1.AllowGrcPeriod, T1.GraceBaseRate, T1.GraceSpecialRate, T1.GracePftRate, T1.GracePftFrq, T1.NextGrcPftDate, T1.AllowGrcPftRvw, T1.GracePftRvwFrq, T1.NextGrcPftRvwDate, T1.AllowGrcCpz, T1.GraceCpzFrq, T1.NextGrcCpzDate, T1.RepayBaseRate, T1.RepaySpecialRate, T1.ProfitRate, T1.RepayFrq, T1.NextRepayDate, T1.RepayPftFrq, T1.NextRepayPftDate, T1.AllowRepayRvw, T1.RepayRvwFrq, T1.NextRepayRvwDate, T1.AllowRepayCpz, T1.RepayCpzFrq, T1.NextRepayCpzDate, T1.MaturityDate, T1.CpzAtGraceEnd, T1.DownPayment, T1.GraceFlatAmount, T1.ReqRepayAmount, T1.TotalProfit, T1.TotalGrcProfit, T1.GraceRateBasis, T1.RepayRateBasis, T1.FinType, T1.FinRemarks, T1.FinCcy, T1.ScheduleMethod, T1.ProfitDaysBasis, T1.ReqMaturity, T1.CalTerms, T1.CalMaturity, T1.FirstRepay, T1.LastRepay, T1.FinStartDate, T1.FinAmount, T1.FinRepaymentAmount, T1.CustID, T1.Defferments, T1.FinIsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	FinanceMain AS T1
WHERE     NOT EXISTS (SELECT 1 FROM FinanceMain_TEMP WHERE FinReference = T1.FinReference)

GO

-- Create View for FinanceDisbursement Module
CREATE VIEW [dbo].[FinDisbursementDetails_View]
AS
SELECT		T1.FinReference, T1.DisbDate, T1.DisbSeq, T1.DisbDesc, T1.DisbAmount, T1.DisbActDate, T1.DisbDisbursed, T1.DisbIsActive, T1.DisbRemarks		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	FinDisbursementDetails_TEMP AS T1
UNION ALL
SELECT		T1.FinReference, T1.DisbDate, T1.DisbSeq, T1.DisbDesc, T1.DisbAmount, T1.DisbActDate, T1.DisbDisbursed, T1.DisbIsActive, T1.DisbRemarks		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	FinDisbursementDetails AS T1
WHERE     NOT EXISTS (SELECT 1 FROM FinDisbursementDetails_TEMP WHERE FinReference = T1.FinReference)

GO

-- Create View for FinanceWorkFlow Module
CREATE VIEW [dbo].[LMTFinanceWorkFlowDef_View]
AS
SELECT		T1.FinType, T1.ScreenCode, T1.WorkFlowType		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	LMTFinanceWorkFlowDef_TEMP AS T1
UNION ALL
SELECT		T1.FinType, T1.ScreenCode, T1.WorkFlowType		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	LMTFinanceWorkFlowDef AS T1
WHERE     NOT EXISTS (SELECT 1 FROM LMTFinanceWorkFlowDef_TEMP WHERE FinType = T1.FinType)

GO

-- Create View for Question Module
CREATE VIEW [dbo].[BMTQuestion_View]
AS
SELECT		T1.QuestionId, T1.QuestionDesc, T1.AnswerA, T1.AnswerB, T1.AnswerC, T1.AnswerD, T1.CorrectAnswer, T1.QuestionIsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	BMTQuestion_TEMP AS T1
UNION ALL
SELECT		T1.QuestionId, T1.QuestionDesc, T1.AnswerA, T1.AnswerB, T1.AnswerC, T1.AnswerD, T1.CorrectAnswer, T1.QuestionIsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	BMTQuestion AS T1
WHERE     NOT EXISTS (SELECT 1 FROM BMTQuestion_TEMP WHERE QuestionId = T1.QuestionId)

GO
--Sequence Tabele
CREATE TABLE [dbo].[SeqBMTQuestion](
	[SeqNo] [bigint] NOT NULL
) ON [PRIMARY]

INSERT INTO [dbo].[SeqBMTQuestion]VALUES (0)



-- Create View for AgreementDefinition Module
CREATE VIEW [dbo].[BMTAggrementDef_View]
AS
SELECT		T1.AggCode, T1.AggName, T1.AggDesc, T1.AggReportName, T1.AggReportPath, T1.AggIsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	BMTAggrementDef_TEMP AS T1
UNION ALL
SELECT		T1.AggCode, T1.AggName, T1.AggDesc, T1.AggReportName, T1.AggReportPath, T1.AggIsActive		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	BMTAggrementDef AS T1
WHERE     NOT EXISTS (SELECT 1 FROM BMTAggrementDef_TEMP WHERE AggCode = T1.AggCode)

GO

-- Create View for FinanceAgreementList Module
CREATE VIEW [dbo].[LMTFinAggrementList_View]
AS
SELECT		T1.FinAggId, T1.FinType, T1.AggCode, T1.LinkageActive, T1.ShowInStage, T1.MandInputInStage, T1.AllowInputInStage		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	LMTFinAggrementList_TEMP AS T1
UNION ALL
SELECT		T1.FinAggId, T1.FinType, T1.AggCode, T1.LinkageActive, T1.ShowInStage, T1.MandInputInStage, T1.AllowInputInStage		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	LMTFinAggrementList AS T1
WHERE     NOT EXISTS (SELECT 1 FROM LMTFinAggrementList_TEMP WHERE FinAggId = T1.FinAggId)

GO
--Sequence Tabele
CREATE TABLE [dbo].[SeqLMTFinAggrementList](
	[SeqNo] [bigint] NOT NULL
) ON [PRIMARY]

INSERT INTO [dbo].[SeqLMTFinAggrementList]VALUES (0)

-- Create View for FinanceReferenceDetail Module
CREATE VIEW [dbo].[LMTFinRefDetail_View]
AS
SELECT		T1.FinRefDetailId, T1.FinType, T1.FinRefType, T1.FinRefId, T1.IsActive, T1.ShowInStage, T1.MandInputInStage, T1.AllowInputInStage		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	LMTFinRefDetail_TEMP AS T1
UNION ALL
SELECT		T1.FinRefDetailId, T1.FinType, T1.FinRefType, T1.FinRefId, T1.IsActive, T1.ShowInStage, T1.MandInputInStage, T1.AllowInputInStage		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	LMTFinRefDetail AS T1
WHERE     NOT EXISTS (SELECT 1 FROM LMTFinRefDetail_TEMP WHERE FinRefDetailId = T1.FinRefDetailId)

GO
--Sequence Tabele
CREATE TABLE [dbo].[SeqLMTFinRefDetail](
	[SeqNo] [bigint] NOT NULL
) ON [PRIMARY]

INSERT INTO [dbo].[SeqLMTFinRefDetail]VALUES (0)

-- Create View for ScoringGroup Module
CREATE VIEW [dbo].[RMTScoringGroup_View]
AS
SELECT		T1.ScoreGroupId, T1.ScoreGroupCode, T1.ScoreGroupName, T1.MinScore, T1.Isoverride, T1.OverrideScore		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTScoringGroup_TEMP AS T1
UNION ALL
SELECT		T1.ScoreGroupId, T1.ScoreGroupCode, T1.ScoreGroupName, T1.MinScore, T1.Isoverride, T1.OverrideScore		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTScoringGroup AS T1
WHERE     NOT EXISTS (SELECT 1 FROM RMTScoringGroup_TEMP WHERE ScoreGroupId = T1.ScoreGroupId)

GO
--Sequence Tabele
CREATE TABLE [dbo].[SeqRMTScoringGroup](
	[SeqNo] [bigint] NOT NULL
) ON [PRIMARY]

INSERT INTO [dbo].[SeqRMTScoringGroup]VALUES (0)

-- Create View for SubHeadRule Module
CREATE VIEW [dbo].[RMTSubHeadRule_View]
AS
SELECT		T1.SubheadRuleCode, T1.SubheadRuleName, T1.SubheadRuleValue		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTSubHeadRule_TEMP AS T1
UNION ALL
SELECT		T1.SubheadRuleCode, T1.SubheadRuleName, T1.SubheadRuleValue		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTSubHeadRule AS T1
WHERE     NOT EXISTS (SELECT 1 FROM RMTSubHeadRule_TEMP WHERE SubheadRuleCode = T1.SubheadRuleCode)

GO

-- Create View for SystemInternalAccountDefinition Module
CREATE VIEW [dbo].[SystemInternalAccountDef_View]
AS
SELECT		T1.SIACode, T1.SIAName, T1.SIAShortName, T1.SIAAcType, T1.SIANumber		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	SystemInternalAccountDef_TEMP AS T1
UNION ALL
SELECT		T1.SIACode, T1.SIAName, T1.SIAShortName, T1.SIAAcType, T1.SIANumber		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	SystemInternalAccountDef AS T1
WHERE     NOT EXISTS (SELECT 1 FROM SystemInternalAccountDef_TEMP WHERE SIACode = T1.SIACode)

GO



-- Create View for ExtendedFieldHeader Module
CREATE VIEW [dbo].[ExtendedFieldHeader_View]
AS
SELECT		T1.ModuleId, T1.ModuleName, T1.SubModuleName, T1.TabHeading, T1.NumberOfColumns		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	ExtendedFieldHeader_TEMP AS T1
UNION ALL
SELECT		T1.ModuleId, T1.ModuleName, T1.SubModuleName, T1.TabHeading, T1.NumberOfColumns		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	ExtendedFieldHeader AS T1
WHERE     NOT EXISTS (SELECT 1 FROM ExtendedFieldHeader_TEMP WHERE ModuleId = T1.ModuleId)

GO
--Sequence Tabele
CREATE TABLE [dbo].[SeqExtendedFieldHeader](
	[SeqNo] [bigint] NOT NULL
) ON [PRIMARY]

INSERT INTO [dbo].[SeqExtendedFieldHeader]VALUES (0)

-- Create View for ExtendedFieldDetail Module
CREATE VIEW [dbo].[ExtendedFieldDetail_View]
AS
SELECT		T1.ModuleId, T1.FieldName, T1.FieldType, T1.FieldLength, T1.FieldPrec, T1.FieldLabel, T1.FieldMandatory, T1.FieldConstraint, T1.FieldSeqOrder, T1.FieldColumn, T1.FieldList, T1.FieldDefaultValue, T1.FieldMinValue, T1.FieldMaxValue, T1.FieldUnique, T1.FieldExternalScript		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	ExtendedFieldDetail_TEMP AS T1
UNION ALL
SELECT		T1.ModuleId, T1.FieldName, T1.FieldType, T1.FieldLength, T1.FieldPrec, T1.FieldLabel, T1.FieldMandatory, T1.FieldConstraint, T1.FieldSeqOrder, T1.FieldColumn, T1.FieldList, T1.FieldDefaultValue, T1.FieldMinValue, T1.FieldMaxValue, T1.FieldUnique, T1.FieldExternalScript		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	ExtendedFieldDetail AS T1
WHERE     NOT EXISTS (SELECT 1 FROM ExtendedFieldDetail_TEMP WHERE ModuleId = T1.ModuleId)

GO

-- Create View for FinanceCampaign Module
CREATE VIEW [dbo].[RMTFinCampaign_View]
AS
SELECT		T1.FCCode, T1.FCDesc, T1.FCFinType, T1.FCIsAlwMD, T1.FCIsAlwGrace, T1.FCOrgPrfUnchanged, T1.FCRateType, T1.FCBaseRate, T1.FCSplRate, T1.FCIntRate, T1.FCDftIntFrq, T1.FCIsIntCpz, T1.FCCpzFrq, T1.FCIsRvwAlw, T1.FCRvwFrq, T1.FCGrcRateType, T1.FCGrcBaseRate, T1.FCGrcSplRate, T1.FCGrcIntRate, T1.FCGrcDftIntFrq, T1.FCGrcIsIntCpz, T1.FCGrcCpzFrq, T1.FCGrcIsRvwAlw, T1.FCGrcRvwFrq, T1.FCMinTerm, T1.FCMaxTerm, T1.FCDftTerms, T1.FCRpyFrq, T1.FCRepayMethod, T1.FCIsAlwPartialRpy, T1.FCIsAlwDifferment, T1.FCMaxDifferment, T1.FCIsAlwFrqDifferment, T1.FCMaxFrqDifferment, T1.FCIsAlwEarlyRpy, T1.FCIsAlwEarlySettle, T1.FCIsDwPayRequired, T1.FCRvwRateApplFor, T1.FCAlwRateChangeAnyDate, T1.FCGrcRvwRateApplFor, T1.FCIsIntCpzAtGrcEnd, T1.FCGrcAlwRateChgAnyDate, T1.FCMinDownPayAmount, T1.FCSchCalCodeOnRvw		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTFinCampaign_TEMP AS T1
UNION ALL
SELECT		T1.FCCode, T1.FCDesc, T1.FCFinType, T1.FCIsAlwMD, T1.FCIsAlwGrace, T1.FCOrgPrfUnchanged, T1.FCRateType, T1.FCBaseRate, T1.FCSplRate, T1.FCIntRate, T1.FCDftIntFrq, T1.FCIsIntCpz, T1.FCCpzFrq, T1.FCIsRvwAlw, T1.FCRvwFrq, T1.FCGrcRateType, T1.FCGrcBaseRate, T1.FCGrcSplRate, T1.FCGrcIntRate, T1.FCGrcDftIntFrq, T1.FCGrcIsIntCpz, T1.FCGrcCpzFrq, T1.FCGrcIsRvwAlw, T1.FCGrcRvwFrq, T1.FCMinTerm, T1.FCMaxTerm, T1.FCDftTerms, T1.FCRpyFrq, T1.FCRepayMethod, T1.FCIsAlwPartialRpy, T1.FCIsAlwDifferment, T1.FCMaxDifferment, T1.FCIsAlwFrqDifferment, T1.FCMaxFrqDifferment, T1.FCIsAlwEarlyRpy, T1.FCIsAlwEarlySettle, T1.FCIsDwPayRequired, T1.FCRvwRateApplFor, T1.FCAlwRateChangeAnyDate, T1.FCGrcRvwRateApplFor, T1.FCIsIntCpzAtGrcEnd, T1.FCGrcAlwRateChgAnyDate, T1.FCMinDownPayAmount, T1.FCSchCalCodeOnRvw		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	RMTFinCampaign AS T1
WHERE     NOT EXISTS (SELECT 1 FROM RMTFinCampaign_TEMP WHERE FCCode = T1.FCCode)

GO

-- Create View for FinanceRepayPriority Module
CREATE VIEW [dbo].[FinRpyPriority_View]
AS
SELECT		T1.FinType, T1.FinPriority		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	FinRpyPriority_TEMP AS T1
UNION ALL
SELECT		T1.FinType, T1.FinPriority		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	FinRpyPriority AS T1
WHERE     NOT EXISTS (SELECT 1 FROM FinRpyPriority_TEMP WHERE FinType = T1.FinType)

GO

-- Create View for OverdueCharge Module
CREATE VIEW [dbo].[FinODCHeader_View]
AS
SELECT		T1.ODCRuleCode, T1.ODCPLAccount, T1.ODCCharityAccount, T1.ODCPLShare, T1.ODCSweepCharges		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	FinODCHeader_TEMP AS T1
UNION ALL
SELECT		T1.ODCRuleCode, T1.ODCPLAccount, T1.ODCCharityAccount, T1.ODCPLShare, T1.ODCSweepCharges		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	FinODCHeader AS T1
WHERE     NOT EXISTS (SELECT 1 FROM FinODCHeader_TEMP WHERE ODCRuleCode = T1.ODCRuleCode)

GO

-- Create View for OverdueChargeRecovery Module
CREATE VIEW [dbo].[FinODCRecovery_View]
AS
SELECT		T1.FinReference, T1.FinSchdDate, T1.FinODFor, T1.FinBrnm, T1.FinType, T1.FinCustId, T1.FinCcy, T1.FinODDate, T1.FinODPri, T1.FinODPft, T1.FinODTot, T1.FinODCRuleCode, T1.FinODCPLAc, T1.FinODCCAc, T1.FinODCPLShare, T1.FinODCSweep, T1.FinODCCustCtg, T1.FinODCType, T1.FinODCOn, T1.FinODC, T1.FinODCGraceDays, T1.FinODCAlwWaiver, T1.FinODCMaxWaiver, T1.FinODCPenalty, T1.FinODCWaived, T1.FinODCPLPenalty, T1.FinODCCPenalty, T1.FinODCPaid, T1.FinODCLastPaidDate, T1.FinODCRecoverySts		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	FinODCRecovery_TEMP AS T1
UNION ALL
SELECT		T1.FinReference, T1.FinSchdDate, T1.FinODFor, T1.FinBrnm, T1.FinType, T1.FinCustId, T1.FinCcy, T1.FinODDate, T1.FinODPri, T1.FinODPft, T1.FinODTot, T1.FinODCRuleCode, T1.FinODCPLAc, T1.FinODCCAc, T1.FinODCPLShare, T1.FinODCSweep, T1.FinODCCustCtg, T1.FinODCType, T1.FinODCOn, T1.FinODC, T1.FinODCGraceDays, T1.FinODCAlwWaiver, T1.FinODCMaxWaiver, T1.FinODCPenalty, T1.FinODCWaived, T1.FinODCPLPenalty, T1.FinODCCPenalty, T1.FinODCPaid, T1.FinODCLastPaidDate, T1.FinODCRecoverySts		
			, T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId
FROM    	FinODCRecovery AS T1
WHERE     NOT EXISTS (SELECT 1 FROM FinODCRecovery_TEMP WHERE FinReference = T1.FinReference)

GO
<!--//APPEND AFTER THIS//-->

