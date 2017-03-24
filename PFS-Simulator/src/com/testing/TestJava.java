package com.testing;

import java.io.IOException;


public class TestJava extends ParentTest {

	public void m1() throws IOException { 
		try {
			super.m1();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("I am Child");
	}
	public static void main(String args[]) throws Exception {
		TestJava ptest = new TestJava();
		ptest.m1();
		String str = "abc";
		String str2 = "abc";
		
		String str3 = new String("abc");
		
		System.out.println(str.equals(str2));
		System.out.println(str.hashCode() +"-"+ str2.hashCode());
		System.out.println(ptest.hashCode());
	}
}
