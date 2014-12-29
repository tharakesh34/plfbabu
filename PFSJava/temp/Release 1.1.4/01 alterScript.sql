/**  Start : Release Version 1.1.1 **/

USE [PFFLIV]
--Finance Main  
ALTER TABLE dbo.FinanceMain_Temp ALTER COLUMN  Discrepancy nvarchar(100);
ALTER TABLE dbo.FinanceMain ALTER COLUMN  Discrepancy nvarchar(100);

USE [PFFLIVAudit]
ALTER TABLE dbo.AdtFinanceMain ALTER COLUMN  Discrepancy nvarchar(100);

/**  END : Release Version 1.1.1 **/
