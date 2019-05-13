create or replace FUNCTION "UDF_CONVERTCURRENCY" 
(
  v_Amount IN NUMBER,
  v_SourceCCY IN VARCHAR2,
  v_TargetCCY IN VARCHAR2
)
RETURN NUMBER
AS
   -- Declare Variables
   v_TransAmount NUMBER(23,5);
   v_TargetSpotRate NUMBER(15,9);
   v_SourceSpotRate NUMBER(15,9);
   v_TargetCcyMinorUnits NUMBER(5,0);
   v_SourceCcyMinorUnits NUMBER(5,0);
   v_TargetCcyIsReceprocal VARCHAR2(1);
   v_SourceCcyIsReceprocal VARCHAR2(1);

BEGIN
   -- Take the SpotRate and Minor Unit Values from Database_1
IF v_Amount is null then 
BEGIN
   v_TransAmount := 0;
 END ; 
ELSE
BEGIN
    IF v_SourceCCY != v_TargetCCY THEN
    BEGIN
       SELECT CcySpotRate ,
              CcyMinorCcyUnits ,
              CcyIsReceprocal 
         INTO v_TargetSpotRate,
              v_TargetCcyMinorUnits,
              v_TargetCcyIsReceprocal
         FROM RMTCurrencies 
         WHERE CCYCode = v_TargetCCY;
       SELECT CcySpotRate ,
              CcyMinorCcyUnits ,
              CcyIsReceprocal 
         INTO v_SourceSpotRate,
              v_SourceCcyMinorUnits,
              v_SourceCcyIsReceprocal
         FROM RMTCurrencies 
         WHERE CCYCode = v_SourceCCY;
       IF v_TargetCcyIsReceprocal = '1' THEN   
       BEGIN
          v_TargetSpotRate := (1 / v_TargetSpotRate) ;
       END;
       END IF;
       IF v_SourceCcyIsReceprocal = '1' THEN   
       BEGIN
          v_SourceSpotRate := (1 / v_SourceSpotRate) ;
       END;
       END IF;
       v_TransAmount := ((v_Amount * v_TargetSpotRate * v_TargetCcyMinorUnits) / (v_SourceSpotRate * v_SourceCcyMinorUnits)) / v_TargetCcyMinorUnits ;
     END;
     END IF;  
      
      IF v_SourceCCY = v_TargetCCY THEN
      BEGIN
       SELECT CcyMinorCcyUnits
         INTO v_TargetCcyMinorUnits
         FROM RMTCurrencies 
         WHERE CCYCode = v_TargetCCY;
         v_TransAmount := v_Amount/v_TargetCcyMinorUnits;
      END;
      END IF;
    END ;
END IF; 
   RETURN v_TransAmount;
END;
DonotStop