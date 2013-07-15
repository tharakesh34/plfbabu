package com.pennant.equation.fileprocessing;


public class EquationFileUploadProcess {
	
	private static String filePath="";
	
	public boolean fileUploadProcess(){
		filePath = "C:/Documents and Settings/s053.PENNANT/Desktop";
		customerTypeUpload();
		return true;
	}
	
	
	public static void customerTypeUpload(){
		
		/*ArrayList<CustomerType> list= new ArrayList<CustomerType>();
		
		FixedWidthReader reader = new FixedWidthReader(new File(filePath+"/CUSTOMERTYPES.txt"));
		//reader.setFieldNamesInFirstRow(true);
		reader.addField("CustTypeCode",2);
		reader.addField("CustTypeDesc",35);
		reader.addField("CustTypeIsInd",1);
		reader.addField("lastMntDay",2);
		reader.addField("lastMntMonth",2);
		reader.addField("lastMntYear",2);
		reader.open();
        try {
            Record record;
            while ((record = reader.read()) != null) {
            	CustomerType customerType = new CustomerType("");
            	customerType.setCustTypeCode(record.getField("CustTypeCode").getValueAsString());
            	list.add(customerType);
            }
        } finally {
            reader.close();
        }*/
	} 

}
