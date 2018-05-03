package com.pennant.util.Constraint;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Constraint;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.PennantConstants;

public class PTDateValidator implements Constraint{

	private String fieldParm;
	private boolean mandatory=false;
	private String dateFormate="("+PennantConstants.dateFormat+")";
	private Date fromDate;
	private Date toDate;
	private boolean equal=false;
	private boolean fromValid=false;
	private boolean toValid=false;
	
	public PTDateValidator (String fieldParm,boolean mandatory){
		setFieldParm(fieldParm);
		setMandatory(mandatory);
		setFromDate(null);
		setToDate(null);
	}

	public PTDateValidator (String fieldParm,boolean mandatory,Date fromDate,Date toDate,boolean equal){
		setFieldParm(fieldParm);
		setMandatory(mandatory);
		setFromDate(fromDate);
		setToDate(toDate);
		setEqual(equal);
	}

	public PTDateValidator (String fieldParm,boolean mandatory,boolean fromSystem,Date toDate,boolean equal){
		setFieldParm(fieldParm);
		setMandatory(mandatory);
		setFromDate(DateUtility.getAppDate());
		setToDate(toDate);
		setEqual(equal);
	}

	
	public PTDateValidator (String fieldParm,boolean mandatory,Date fromDate,boolean toSystemDate,boolean equal){
		setFieldParm(fieldParm);
		setMandatory(mandatory);
		setFromDate(fromDate);
		setToDate(DateUtility.getAppDate());
		setEqual(equal);
	}


	@Override
	public void validate(Component comp, Object value) throws WrongValueException {
		String errorMessage=getErrorMessage(value);
		if(StringUtils.isNotBlank(errorMessage)){
			throw new WrongValueException(comp, errorMessage);
		}
	}

	
	private String getErrorMessage(Object value){

		Date compValue=null;
		
		if(fromDate == null){
			fromDate = DateUtility.addDays(SysParamUtil.getValueAsDate("APP_DFT_START_DATE"), 1);
			fromValid = true;
		}
		if(toDate == null){
			toDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");
			toValid = true;
		}
		
		if(value!=null){
			if(Date.class.isInstance(value)){
				compValue=(Date) value;
			}else{
				return Labels.getLabel("DATE_INVALID", new String[] {fieldParm,dateFormate});
			}	
		}

		if(compValue==null){
			//Mandatory Validation
			if(mandatory){
				return Labels.getLabel("FIELD_IS_MAND", new String[] {fieldParm});		
			}		else{
			
				return "";
			}
		
		}
		
		//Date Range
		if(fromValid && toValid){
			if(equal){
				if(compValue.before(fromDate) || compValue.after(toDate) ){
					return Labels.getLabel("DATE_ALLOWED_RANGE_EQUAL", new String[] {fieldParm,DateUtility.formateDate(fromDate, PennantConstants.dateFormat),DateUtility.formateDate(toDate, PennantConstants.dateFormat)});
				}
			}else{
				if(!compValue.after(fromDate) || !compValue.before(toDate) ){
					return Labels.getLabel("DATE_ALLOWED_RANGE", new String[] {fieldParm,DateUtility.formateDate(fromDate, PennantConstants.dateFormat),DateUtility.formateDate(toDate, PennantConstants.dateFormat)});
				}
			}
		}
		
		
		//From Date Validation 
		if(fromValid){
			int comp = compValue.compareTo(fromDate);
			if(equal){
				if(comp<0){
					return Labels.getLabel("DATE_ALLOWED_MINDATE_EQUAL", new String[] {fieldParm,DateUtility.formateDate(fromDate, PennantConstants.dateFormat)});
				}
			}else{
				if(comp<=0){
					return Labels.getLabel("DATE_ALLOWED_MINDATE", new String[] {fieldParm,DateUtility.formateDate(fromDate, PennantConstants.dateFormat)});
				}				
			}
		}
		
		
		//To Date Validation 
		if(toValid){
			int comp = compValue.compareTo(toDate);

			if(equal){
				if(comp>0){
					return Labels.getLabel("DATE_ALLOWED_MAXDATE_EQUAL", new String[] {fieldParm,DateUtility.formateDate(toDate, PennantConstants.dateFormat)});
				}
			}else{
				if(comp>=0){
					return Labels.getLabel("DATE_ALLOWED_MAXDATE", new String[] {fieldParm,DateUtility.formateDate(toDate, PennantConstants.dateFormat)});
				}				
			}
		}
		return "";
	}

	String getFieldParm() {
		return fieldParm;
	}


	void setFieldParm(String fieldParm) {
		this.fieldParm = fieldParm;
	}


	boolean isMandatory() {
		return mandatory;
	}


	void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}


	String getDateFormate() {
		return dateFormate;
	}


	void setDateFormate(String dateFormate) {
		this.dateFormate = dateFormate;
	}


	Date getFromDate() {
		return fromDate;
	}


	void setFromDate(Date fromDate) {
		if(fromDate!=null){
			fromValid=true;
		}
		this.fromDate = fromDate;
	}


	Date getToDate() {
		return toDate;
	}


	void setToDate(Date toDate) {
		if(toDate!=null){
			toValid=true;
		}
		this.toDate = toDate;
	}

	boolean isEqual() {
		return equal;
	}

	void setEqual(boolean equal) {
		this.equal = equal;
	}
	
}
