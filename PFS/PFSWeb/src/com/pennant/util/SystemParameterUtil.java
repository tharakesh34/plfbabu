package com.pennant.util;

import java.util.ArrayList;

import com.pennant.backend.model.ValueLabel;
/*
 * 	Class to define the System parameter utilities
 * */
public class SystemParameterUtil {

	/*
	*   Method to get the Yes or No parameter values. 
	*/
	public static ArrayList<ValueLabel> getMOD_YESNO(){
		return PennantAppUtil.getYesNo();
	}
	
	/*
	 * 	Method to return Language values
	 * */
	public static ArrayList<ValueLabel> getMOD_LANGUAGE(){
		return PennantAppUtil.getLanguage();
		
	}
	/*
	 * Method to return the CoreBank  customer ID parameter values 
	 * */
	public static ArrayList<ValueLabel> getMOD_CBCID(){
		ArrayList<ValueLabel> cbCidList = new ArrayList<ValueLabel>();
		cbCidList.add(new ValueLabel("BFR","BFR"));
		cbCidList.add(new ValueLabel("CRT","CRT"));
		cbCidList.add(new ValueLabel("MAN","MAN"));
		cbCidList.add(new ValueLabel("CIF","CIF"));
		return cbCidList;
	}
}
