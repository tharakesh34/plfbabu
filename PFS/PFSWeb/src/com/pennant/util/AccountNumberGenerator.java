package com.pennant.util;

import java.util.Scanner;

import org.apache.commons.lang.StringUtils;

public class AccountNumberGenerator {

	private static int length = 8;
	private static String type = "ALPHANUMERIC";
	private static final String ALPHA_NUM ="ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	
	public static void main(String[] args) {
		
		Scanner input = new Scanner(System.in);
		String seqNumber = input.nextLine();
		for (int i = 0; i < 10; i++) {
			seqNumber =generateAcSeqNo(seqNumber);
			System.out.println(seqNumber);	
		}
		
		
		
		//seqNo is exist --> get seqNo from Database and generate A/c number by increment
		
		//if seqNo not exist
		
			//get length of A/c number
		
			//get type of A/C number
				//if type is Numeric generate seqNo in Numeric type depend on Particular length
				//if type is AlphaNumeric generate seqNo in AlphaNumeric type depend on Particular length
	}
	
	private static String generateAcSeqNo(String seqNumber){
		//below 3 lines declared for testing purpose only
		
		if(StringUtils.trimToEmpty(seqNumber).equals("")){
			
			if(type.equals("ALPHANUMERIC")){
				seqNumber=StringUtils.leftPad(seqNumber.toUpperCase(), length,'A');
				return seqNumber;
			}	
		}else{
			seqNumber = seqNumber.trim();
		}
		
		
		length = seqNumber.length();
		if(length <8){
			length = 8;
		}

		
		if(type.equals("ALPHANUMERIC")){
			seqNumber=StringUtils.leftPad(seqNumber.toUpperCase(), length,'A');
		}else if(type.equals("NUMERIC")){
			seqNumber=StringUtils.leftPad( seqNumber, length,'0');
		}
		
		StringBuilder seqNo = new StringBuilder(seqNumber);
			//generate sequence 
		if(type.equals("ALPHANUMERIC")){
			boolean repeatExc = false;
			int len = seqNumber.length();
			int charPos = seqNumber.length()-1;
			for(int i=0 ; i<len;i++){
				char character = seqNo.charAt(charPos);
				int pos = ALPHA_NUM.indexOf(character);
				if(ALPHA_NUM.length()-1 == pos){
					character = ALPHA_NUM.charAt(0);
					repeatExc = true;
				}else{
					character = ALPHA_NUM.charAt(pos+1);
					repeatExc = false;
				}
				seqNo.setCharAt(charPos, character);
				if(repeatExc){
					if(charPos != 0){
						charPos = charPos - 1;
					}else{
						seqNo = new StringBuilder(StringUtils.leftPad(seqNo.toString(), length+1,'A'));
					}
				}else{
					break;
				}
			}
		}else if(type.equals("NUMERIC")){
			
			seqNo = new StringBuilder(StringUtils.leftPad(String.valueOf(Long.valueOf(seqNumber)+1), length, seqNo.toString()));
		}
		return seqNo.toString();
	}
}
