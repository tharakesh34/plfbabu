create or replace FUNCTION public.FN_CUSTOMERNAME
(
  IN v_CusFirstName VARCHAR,
  IN v_CusMiddleName VARCHAR,
  IN v_CusLastName VARCHAR
)
RETURNS VARCHAR
AS
$$
   DECLARE v_CustomerName VARCHAR(150);

BEGIN
   v_CustomerName := NVL(v_CusFirstName, ' ') || ' ' || NVL(v_CusMiddleName, ' ') || ' ' || NVL(v_CusLastName, ' ') ;
   RETURN v_CustomerName;
END
$$
LANGUAGE 'plpgsql';
DonotStop

create or replace FUNCTION public.FN_TRANTYPE 
(
  IN v_TranTypeCode VARCHAR
)
RETURNS VARCHAR
AS
$$
   DECLARE v_TranTypeDesc VARCHAR(30);

BEGIN
   v_TranTypeDesc := v_TranTypeCode ;
   IF ( NVL(v_TranTypeCode, ' ') = ' ' ) THEN
   
   BEGIN
      RETURN ' ';
   END;
   END IF;
   IF ( v_TranTypeCode = 'A' ) THEN
   
   BEGIN
      RETURN 'Addition';
   END;
   END IF;
   IF ( v_TranTypeCode = 'M' ) THEN
   
   BEGIN
      RETURN 'Maintenance';
   END;
   END IF;
   IF ( v_TranTypeCode = 'D' ) THEN
   
   BEGIN
      RETURN 'Deletion';
   END;
   END IF;
   RETURN v_TranTypeDesc;
END
$$
LANGUAGE 'plpgsql';
DonotStop