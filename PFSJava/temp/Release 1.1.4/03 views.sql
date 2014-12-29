-- Start : Release Version 1.1.4
USE [PFFLIV]
GO
-- Dashboard Scripts
Create VIEW [dbo].[FinancePendingDetailsByRole_View]
AS
SELECT     T2.MenuRef, T2.MenuCode, T1.FinReference, T1.CustID AS CustCIF, T3.CustShrtName, T2.MenuZulPath, T1.NextRoleCode
FROM         dbo.FinanceMain_Temp AS T1 INNER JOIN
                      dbo.PTMenuDetails AS T2 ON T1.RcdMaintainSts = T2.MenuCode LEFT OUTER JOIN
                      dbo.Customers AS T3 ON T1.CustID = T3.CustID

-- END : Release Version 1.1.4