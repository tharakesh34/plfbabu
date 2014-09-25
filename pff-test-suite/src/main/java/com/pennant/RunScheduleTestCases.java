package com.pennant;

import java.util.Arrays;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import com.pennant.gracepft.SN01_RR_PFT;

public class RunScheduleTestCases {
	static final String[] CONFIG_LOCATIONS = new String[] {
			"../PLFInterface/src/applicationContext-equation-interface.xml",
			"../PFSJava/src/applicationContext-daos.xml",
			"../PFSWeb/src/applicationContext-db.xml",
			"../PFSWeb/src/customize-applicationContext.xml" };
	static ApplicationContext context;

	public static void main(String[] args) {
		BeanDefinition definition = BeanDefinitionBuilder
				.rootBeanDefinition(Arrays.class, "asList")
				.addConstructorArgValue(new String[] {}).getBeanDefinition();

		DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
		factory.registerBeanDefinition("args", definition);

		GenericApplicationContext parent = new GenericApplicationContext(
				factory);
		parent.refresh();

		context = new FileSystemXmlApplicationContext(CONFIG_LOCATIONS, parent);

		try {
			System.out.println("GRACE WITH PAY ...........");

			if (SN01_RR_PFT.RunTestCase()) {
				System.out.println("Result for : SN01_RR_PFT : success");
			} else {
				System.err.println("Result for : SN01_RR_PFT : failed");
			}
			// if (SN01_RR_PRI_REQ.RunTestCase()) {
			// System.out.println("Result for : SN01_RR_PRI_REQ : success");
			// } else {
			// System.err.println("Result for : SN01_RR_PRI_REQ : failed");
			// }
			// if (SN01_RR_PRI.RunTestCase()) {
			// System.out.println("Result for : SN01_RR_PRI : success");
			// } else {
			// System.err.println("Result for : SN01_RR_PRI : failed");
			// }
			// if (SN01_RR_PRIPFT_REQ.RunTestCase()) {
			// System.out.println("Result for : SN01_RR_PRIPFT_REQ : success");
			// } else {
			// System.err.println("Result for : SN01_RR_PRIPFT_REQ : failed");
			// }
			// if (SN01_RR_PRIPFT.RunTestCase()) {
			// System.out.println("Result for : SN01_RR_PRIPFT : success");
			// } else {
			// System.err.println("Result for : SN01_RR_PRIPFT : failed");
			// }
			//
			// System.out
			// .println("----------------------------------------------------------------------------------------------------------------------------");
			//
			// if (SN02_FR_EQUAL_REQ.RunTestCase()) {
			// System.out.println("Result for : SN02_FR_EQUAL_REQ : success");
			// } else {
			// System.err.println("Result for : SN02_FR_EQUAL_REQ : failed");
			// }
			// if (SN02_FR_EQUAL.RunTestCase()) {
			// System.out.println("Result for : SN02_FR_EQUAL : success");
			// } else {
			// System.err.println("Result for : SN02_FR_EQUAL : failed");
			// }
			// if (SN02_FR_PFT.RunTestCase()) {
			// System.out.println("Result for : SN02_FR_PFT : success");
			// } else {
			// System.err.println("Result for : SN02_FR_PFT : failed");
			// }
			// if (SN02_FR_PRI_REQ.RunTestCase()) {
			// System.out.println("Result for : SN02_FR_PRI_REQ : success");
			// } else {
			// System.err.println("Result for : SN02_FR_PRI_REQ : failed");
			// }
			// if (SN02_FR_PRI.RunTestCase()) {
			// System.out.println("Result for : SN02_FR_PRI : success");
			// } else {
			// System.err.println("Result for : SN02_FR_PRI : failed");
			// }
			// if (SN02_FR_PRIPFT_REQ.RunTestCase()) {
			// System.out.println("Result for : SN02_FR_PRIPFT_REQ : success");
			// } else {
			// System.err.println("Result for : SN02_FR_PRIPFT_REQ : failed");
			// }
			// if (SN02_FR_PRIPFT.RunTestCase()) {
			// System.out.println("Result for : SN02_FR_PRIPFT : success");
			// } else {
			// System.err.println("Result for : SN02_FR_PRIPFT : failed");
			// }
			//
			// System.out
			// .println("---------------------------------------------------------------------");
			// System.out.println("GRACE WITH NO_PAY...........");
			// System.out
			// .println("---------------------------------------------------------------------");
			//
			// if (SN03_RR_EQUAL_REQ.RunTestCase()) {
			// System.out.println("Result for : SN03_RR_EQUAL_REQ : success");
			// } else {
			// System.err.println("Result for : SN03_RR_EQUAL_REQ : failed");
			// }
			// if (SN03_RR_EQUAL.RunTestCase()) {
			// System.out.println("Result for : SN03_RR_EQUAL : success");
			// } else {
			// System.err.println("Result for : SN03_RR_EQUAL : failed");
			// }
			// if (SN03_RR_PFT.RunTestCase()) {
			// System.out.println("Result for : SN03_RR_PFT : success");
			// } else {
			// System.err.println("Result for : SN03_RR_PFT : failed");
			// }
			// if (SN03_RR_PRI_REQ.RunTestCase()) {
			// System.out.println("Result for : SN03_RR_PRI_REQ : success");
			// } else {
			// System.err.println("Result for : SN03_RR_PRI_REQ : failed");
			// }
			// if (SN03_RR_PRI.RunTestCase()) {
			// System.out.println("Result for : SN03_RR_PRI : success");
			// } else {
			// System.err.println("Result for : SN03_RR_PRI : failed");
			// }
			// if (SN03_RR_PRIPFT_REQ.RunTestCase()) {
			// System.out.println("Result for : SN03_RR_PRIPFT_REQ : success");
			// } else {
			// System.err.println("Result for : SN03_RR_PRIPFT_REQ : failed");
			// }
			// if (SN03_RR_PRIPFT.RunTestCase()) {
			// System.out.println("Result for : SN03_RR_PRIPFT : success");
			// } else {
			// System.err.println("Result for : SN03_RR_PRIPFT : failed");
			// }
			//
			// System.out
			// .println("----------------------------------------------------------------------------------------------------------------------------");
			//
			// if (SN04_FR_EQUAL_REQ.RunTestCase()) {
			// System.out.println("Result for : SN04_FR_EQUAL_REQ : success");
			// } else {
			// System.err.println("Result for : SN04_FR_EQUAL_REQ : failed");
			// }
			// if (SN04_FR_EQUAL.RunTestCase()) {
			// System.out.println("Result for : SN04_FR_EQUAL : success");
			// } else {
			// System.err.println("Result for : SN04_FR_EQUAL : failed");
			// }
			// if (SN04_FR_PFT.RunTestCase()) {
			// System.out.println("Result for : SN04_FR_PFT : success");
			// } else {
			// System.err.println("Result for : SN04_FR_PFT : failed");
			// }
			// if (SN04_FR_PRI_REQ.RunTestCase()) {
			// System.out.println("Result for : SN04_FR_PRI_REQ : success");
			// } else {
			// System.err.println("Result for : SN04_FR_PRI_REQ : failed");
			// }
			// if (SN04_FR_PRI.RunTestCase()) {
			// System.out.println("Result for : SN04_FR_PRI : success");
			// } else {
			// System.err.println("Result for : SN04_FR_PRI : failed");
			// }
			// if (SN04_FR_PRIPFT_REQ.RunTestCase()) {
			// System.out.println("Result for : SN04_FR_PRIPFT_REQ : success");
			// } else {
			// System.err.println("Result for : SN04_FR_PRIPFT_REQ : failed");
			// }
			// if (SN04_FR_PRIPFT.RunTestCase()) {
			// System.out.println("Result for : SN04_FR_PRIPFT : success");
			// } else {
			// System.err.println("Result for : SN04_FR_PRIPFT : failed");
			// }
			//
			// System.out
			// .println("---------------------------------------------------------------------");
			// System.out.println("GRACE WITH NO REPAY...........");
			// System.out
			// .println("---------------------------------------------------------------------");
			//
			// if (SN05_RR_EQUAL_REQ.RunTestCase()) {
			// System.out.println("Result for : SN05_RR_EQUAL_REQ : success");
			// } else {
			// System.err.println("Result for : SN05_RR_EQUAL_REQ : failed");
			// }
			// if (SN05_RR_EQUAL_REQ.RunTestCase()) {
			// System.out.println("Result for : SN05_RR_EQUAL_REQ : success");
			// } else {
			// System.err.println("Result for : SN05_RR_EQUAL_REQ : failed");
			// }
			// if (SN05_RR_PFT.RunTestCase()) {
			// System.out.println("Result for : SN05_RR_PFT : success");
			// } else {
			// System.err.println("Result for : SN05_RR_PFT : failed");
			// }
			// if (SN05_RR_PRI_REQ.RunTestCase()) {
			// System.out.println("Result for : SN05_RR_PRI_REQ : success");
			// } else {
			// System.err.println("Result for : SN05_RR_PRI_REQ : failed");
			// }
			// if (SN05_RR_PRI.RunTestCase()) {
			// System.out.println("Result for : SN05_RR_PRI : success");
			// } else {
			// System.err.println("Result for : SN05_RR_PRI : failed");
			// }
			// if (SN05_RR_PRIPFT_REQ.RunTestCase()) {
			// System.out.println("Result for : SN05_RR_PRIPFT_REQ : success");
			// } else {
			// System.err.println("Result for : SN05_RR_PRIPFT_REQ : failed");
			// }
			// if (SN05_RR_PRIPFT.RunTestCase()) {
			// System.out.println("Result for : SN05_RR_PRIPFT : success");
			// } else {
			// System.err.println("Result for : SN05_RR_PRIPFT : failed");
			// }
			//
			// System.out
			// .println("----------------------------------------------------------------------------------------------------------------------------");
			//
			// if (SN06_FR_EQUAL_REQ.RunTestCase()) {
			// System.out.println("Result for : SN06_FR_EQUAL_REQ : success");
			// } else {
			// System.err.println("Result for : SN06_FR_EQUAL_REQ : failed");
			// }
			// if (SN06_FR_EQUAL.RunTestCase()) {
			// System.out.println("Result for : SN06_FR_EQUAL : success");
			// } else {
			// System.err.println("Result for : SN06_FR_EQUAL : failed");
			// }
			// if (SN06_FR_PFT.RunTestCase()) {
			// System.out.println("Result for : SN06_FR_PFT : success");
			// } else {
			// System.err.println("Result for : SN06_FR_PFT : failed");
			// }
			// if (SN06_FR_PRI_REQ.RunTestCase()) {
			// System.out.println("Result for : SN06_FR_PRI_REQ : success");
			// } else {
			// System.err.println("Result for : SN06_FR_PRI_REQ : failed");
			// }
			// if (SN06_FR_PRI.RunTestCase()) {
			// System.out.println("Result for : SN06_FR_PRI : success");
			// } else {
			// System.err.println("Result for : SN06_FR_PRI : failed");
			// }
			// if (SN06_FR_PRIPFT_REQ.RunTestCase()) {
			// System.out.println("Result for : SN06_FR_PRIPFT_REQ : success");
			// } else {
			// System.err.println("Result for : SN06_FR_PRIPFT_REQ : failed");
			// }
			// if (SN06_FR_PRIPFT.RunTestCase()) {
			// System.out.println("Result for : SN06_FR_PRIPFT : success");
			// } else {
			// System.err.println("Result for : SN06_FR_PRIPFT : failed");
			// }
			//
			// System.out
			// .println("---------------------------------------------------------------------");
			// System.out.println("CHANGE REPAY WITH NO_PAY & GRACE ...........");
			// System.out
			// .println("---------------------------------------------------------------------");
			//
			// if (SN07_RR_EQUAL_REQ.RunTestCase()) {
			// System.out.println("Result for : SN07_RR_EQUAL_REQ : success");
			// } else {
			// System.err.println("Result for : SN07_RR_EQUAL_REQ : failed");
			// }
			// if (SN07_RR_EQUAL.RunTestCase()) {
			// System.out.println("Result for : SN07_RR_EQUAL : success");
			// } else {
			// System.err.println("Result for : SN07_RR_EQUAL : failed");
			// }
			// if (SN07_RR_PFT.RunTestCase()) {
			// System.out.println("Result for : SN07_RR_PFT : success");
			// } else {
			// System.err.println("Result for : SN07_RR_PFT : failed");
			// }
			// if (SN07_RR_PRI_REQ.RunTestCase()) {
			// System.out.println("Result for : SN07_RR_PRI_REQ : success");
			// } else {
			// System.err.println("Result for : SN07_RR_PRI_REQ : failed");
			// }
			// if (SN07_RR_PRI.RunTestCase()) {
			// System.out.println("Result for : SN07_RR_PRI : success");
			// } else {
			// System.err.println("Result for : SN07_RR_PRI : failed");
			// }
			// if (SN07_RR_PRIPFT_REQ.RunTestCase()) {
			// System.out.println("Result for : SN07_RR_PRIPFT_REQ : success");
			// } else {
			// System.err.println("Result for : SN07_RR_PRIPFT_REQ : failed");
			// }
			// if (SN07_RR_PRIPFT.RunTestCase()) {
			// System.out.println("Result for : SN07_RR_PRIPFT : success");
			// } else {
			// System.err.println("Result for : SN07_RR_PRIPFT : failed");
			// }
			//
			// System.out
			// .println("---------------------------------------------------------------------");
			// System.out.println("NO GRACE ...........");
			// System.out
			// .println("---------------------------------------------------------------------");
			//
			// if (SN08_RR_EQUAL_REQ.RunTestCase()) {
			// System.out.println("Result for : SN08_RR_EQUAL_REQ : success");
			// } else {
			// System.err.println("Result for : SN08_RR_EQUAL_REQ : failed");
			// }
			// if (SN08_RR_EQUAL.RunTestCase()) {
			// System.out.println("Result for : SN08_RR_EQUAL : success");
			// } else {
			// System.err.println("Result for : SN08_RR_EQUAL : failed");
			// }
			// if (SN08_RR_PFT.RunTestCase()) {
			// System.out.println("Result for : SN08_RR_PFT : success");
			// } else {
			// System.err.println("Result for : SN08_RR_PFT : failed");
			// }
			// if (SN08_RR_PRI_REQ.RunTestCase()) {
			// System.out.println("Result for : SN08_RR_PRI_REQ : success");
			// } else {
			// System.err.println("Result for : SN08_RR_PRI_REQ : failed");
			// }
			// if (SN08_RR_PRI.RunTestCase()) {
			// System.out.println("Result for : SN08_RR_PRI : success");
			// } else {
			// System.err.println("Result for : SN08_RR_PRI : failed");
			// }
			// if (SN08_RR_PRIPFT_REQ.RunTestCase()) {
			// System.out.println("Result for : SN08_RR_PRIPFT_REQ : success");
			// } else {
			// System.err.println("Result for : SN08_RR_PRIPFT_REQ : failed");
			// }
			// if (SN08_RR_PRIPFT.RunTestCase()) {
			// System.out.println("Result for : SN08_RR_PRIPFT : success");
			// } else {
			// System.err.println("Result for : SN08_RR_PRIPFT : failed");
			// }
			//
			// System.out
			// .println("---------------------------------------------------------------------");
			// System.out
			// .println("MAINTAIN DISBURSEMENT & CHANGE REPAY BY ADJUSTING TERMS TILL MATURITY DATE...........");
			// System.out
			// .println("---------------------------------------------------------------------");
			//
			// if (SN09_RR_EQUAL_REQ.RunTestCase()) {
			// System.out.println("Result for : SN09_RR_EQUAL_REQ : success");
			// } else {
			// System.err.println("Result for : SN09_RR_EQUAL_REQ : failed");
			// }
			// if (SN09_RR_EQUAL.RunTestCase()) {
			// System.out.println("Result for : SN09_RR_EQUAL : success");
			// } else {
			// System.err.println("Result for : SN09_RR_EQUAL : failed");
			// }
			// if (SN09_RR_PFT.RunTestCase()) {
			// System.out.println("Result for : SN09_RR_PFT : success");
			// } else {
			// System.err.println("Result for : SN09_RR_PFT : failed");
			// }
			// if (SN09_RR_PRI_REQ.RunTestCase()) {
			// System.out.println("Result for : SN09_RR_PRI_REQ : success");
			// } else {
			// System.err.println("Result for : SN09_RR_PRI_REQ : failed");
			// }
			// if (SN09_RR_PRI.RunTestCase()) {
			// System.out.println("Result for : SN09_RR_PRI : success");
			// } else {
			// System.err.println("Result for : SN09_RR_PRI : failed");
			// }
			// if (SN09_RR_PRIPFT_REQ.RunTestCase()) {
			// System.out.println("Result for : SN09_RR_PRIPFT_REQ : success");
			// } else {
			// System.err.println("Result for : SN09_RR_PRIPFT_REQ : failed");
			// }
			// if (SN09_RR_PRIPFT.RunTestCase()) {
			// System.out.println("Result for : SN09_RR_PRIPFT : success");
			// } else {
			// System.err.println("Result for : SN09_RR_PRIPFT : failed");
			// }
			//
			// System.out
			// .println("---------------------------------------------------------------------");
			// System.out
			// .println("MAINTAIN DISBURSEMENT TILL MATURITY & CHANGE RATE WITH CURRENT PERIOD...........");
			// System.out
			// .println("---------------------------------------------------------------------");
			//
			// if (SN10_RR_EQUAL_REQ.RunTestCase()) {
			// System.out.println("Result for : SN10_RR_EQUAL_REQ : success");
			// } else {
			// System.err.println("Result for : SN10_RR_EQUAL_REQ : failed");
			// }
			// if (SN10_RR_EQUAL.RunTestCase()) {
			// System.out.println("Result for : SN10_RR_EQUAL : success");
			// } else {
			// System.err.println("Result for : SN10_RR_EQUAL : failed");
			// }
			// if (SN10_RR_PFT.RunTestCase()) {
			// System.out.println("Result for : SN10_RR_PFT : success");
			// } else {
			// System.err.println("Result for : SN10_RR_PFT : failed");
			// }
			// if (SN10_RR_PRI_REQ.RunTestCase()) {
			// System.out.println("Result for : SN10_RR_PRI_REQ : success");
			// } else {
			// System.err.println("Result for : SN10_RR_PRI_REQ : failed");
			// }
			// if (SN10_RR_PRI.RunTestCase()) {
			// System.out.println("Result for : SN10_RR_PRI : success");
			// } else {
			// System.err.println("Result for : SN10_RR_PRI : failed");
			// }
			// if (SN10_RR_PRIPFT_REQ.RunTestCase()) {
			// System.out.println("Result for : SN10_RR_PRIPFT_REQ : success");
			// } else {
			// System.err.println("Result for : SN10_RR_PRIPFT_REQ : failed");
			// }
			// if (SN10_RR_PRIPFT.RunTestCase()) {
			// System.out.println("Result for : SN10_RR_PRIPFT : success");
			// } else {
			// System.err.println("Result for : SN10_RR_PRIPFT : failed");
			// }
			//
			// System.out
			// .println("---------------------------------------------------------------------");
			// System.out
			// .println("MAINTAIN DISBURSEMENT BY ADJUSTING MATURITY & TERMS WITH -- ADDTERM_AFTMDT METHOD...........");
			// System.out
			// .println("---------------------------------------------------------------------");
			//
			// if (SN11_RR_EQUAL_REQ.RunTestCase()) {
			// System.out.println("Result for : SN11_RR_EQUAL_REQ : success");
			// } else {
			// System.err.println("Result for : SN11_RR_EQUAL_REQ : failed");
			// }
			// if (SN11_RR_EQUAL.RunTestCase()) {
			// System.out.println("Result for : SN11_RR_EQUAL : success");
			// } else {
			// System.err.println("Result for : SN11_RR_EQUAL : failed");
			// }
			// if (SN11_RR_PFT.RunTestCase()) {
			// System.out.println("Result for : SN11_RR_PFT : success");
			// } else {
			// System.err.println("Result for : SN11_RR_PFT : failed");
			// }
			// if (SN11_RR_PRI_REQ.RunTestCase()) {
			// System.out.println("Result for : SN11_RR_PRI_REQ : success");
			// } else {
			// System.err.println("Result for : SN11_RR_PRI_REQ : failed");
			// }
			// if (SN11_RR_PRI.RunTestCase()) {
			// System.out.println("Result for : SN11_RR_PRI : success");
			// } else {
			// System.err.println("Result for : SN11_RR_PRI : failed");
			// }
			// if (SN11_RR_PRIPFT_REQ.RunTestCase()) {
			// System.out.println("Result for : SN11_RR_PRIPFT_REQ : success");
			// } else {
			// System.err.println("Result for : SN11_RR_PRIPFT_REQ : failed");
			// }
			// if (SN11_RR_PRIPFT.RunTestCase()) {
			// System.out.println("Result for : SN11_RR_PRIPFT : success");
			// } else {
			// System.err.println("Result for : SN11_RR_PRIPFT : failed");
			// }
			//
			// System.out
			// .println("---------------------------------------------------------------------");
			// System.out
			// .println("CHANGE REPAY & MAINTAIN DISBURSEMENT BY ADJUSTING TERMS...........");
			// System.out
			// .println("---------------------------------------------------------------------");
			//
			// if (SN12_RR_EQUAL_REQ.RunTestCase()) {
			// System.out.println("Result for : SN12_RR_EQUAL_REQ : success");
			// } else {
			// System.err.println("Result for : SN12_RR_EQUAL_REQ : failed");
			// }
			// if (SN12_RR_EQUAL.RunTestCase()) {
			// System.out.println("Result for : SN12_RR_EQUAL : success");
			// } else {
			// System.err.println("Result for : SN12_RR_EQUAL : failed");
			// }
			// if (SN12_RR_PFT.RunTestCase()) {
			// System.out.println("Result for : SN12_RR_PFT : success");
			// } else {
			// System.err.println("Result for : SN12_RR_PFT : failed");
			// }
			// if (SN12_RR_PRI_REQ.RunTestCase()) {
			// System.out.println("Result for : SN12_RR_PRI_REQ : success");
			// } else {
			// System.err.println("Result for : SN12_RR_PRI_REQ : failed");
			// }
			// if (SN12_RR_PRI.RunTestCase()) {
			// System.out.println("Result for : SN12_RR_PRI : success");
			// } else {
			// System.err.println("Result for : SN12_RR_PRI : failed");
			// }
			// if (SN12_RR_PRIPFT_REQ.RunTestCase()) {
			// System.out.println("Result for : SN12_RR_PRIPFT_REQ : success");
			// } else {
			// System.err.println("Result for : SN12_RR_PRIPFT_REQ : failed");
			// }
			// if (SN12_RR_PRIPFT.RunTestCase()) {
			// System.out.println("Result for : SN12_RR_PRIPFT : success");
			// } else {
			// System.err.println("Result for : SN12_RR_PRIPFT : failed");
			// }
			//
			// System.out
			// .println("---------------------------------------------------------------------");
			// System.out
			// .println("CHANGE REPAY & MAINTAIN DISBURSEMENT BY ADJUSTING TERMS TILL DATE...........");
			// System.out
			// .println("---------------------------------------------------------------------");
			//
			// if (SN13_RR_EQUAL_REQ.RunTestCase()) {
			// System.out.println("Result for : SN13_RR_EQUAL_REQ : success");
			// } else {
			// System.err.println("Result for : SN13_RR_EQUAL_REQ : failed");
			// }
			// if (SN13_RR_EQUAL.RunTestCase()) {
			// System.out.println("Result for : SN13_RR_EQUAL : success");
			// } else {
			// System.err.println("Result for : SN13_RR_EQUAL : failed");
			// }
			// if (SN13_RR_PFT.RunTestCase()) {
			// System.out.println("Result for : SN13_RR_PFT : success");
			// } else {
			// System.err.println("Result for : SN13_RR_PFT : failed");
			// }
			// if (SN13_RR_PRI_REQ.RunTestCase()) {
			// System.out.println("Result for : SN13_RR_PRI_REQ : success");
			// } else {
			// System.err.println("Result for : SN13_RR_PRI_REQ : failed");
			// }
			// if (SN13_RR_PRI.RunTestCase()) {
			// System.out.println("Result for : SN13_RR_PRI : success");
			// } else {
			// System.err.println("Result for : SN13_RR_PRI : failed");
			// }
			// if (SN13_RR_PRIPFT_REQ.RunTestCase()) {
			// System.out.println("Result for : SN13_RR_PRIPFT_REQ : success");
			// } else {
			// System.err.println("Result for : SN13_RR_PRIPFT_REQ : failed");
			// }
			// if (SN13_RR_PRIPFT.RunTestCase()) {
			// System.out.println("Result for : SN13_RR_PRIPFT : success");
			// } else {
			// System.err.println("Result for : SN13_RR_PRIPFT : failed");
			// }
			//
			// System.out
			// .println("---------------------------------------------------------------------");
			// System.out.println("CHANGE RATE WITH CURRENT PERIOD ...........");
			// System.out
			// .println("---------------------------------------------------------------------");
			//
			// if (SN14_RR_EQUAL_REQ.RunTestCase()) {
			// System.out.println("Result for : SN14_RR_EQUAL_REQ : success");
			// } else {
			// System.err.println("Result for : SN14_RR_EQUAL_REQ : failed");
			// }
			// if (SN14_RR_EQUAL.RunTestCase()) {
			// System.out.println("Result for : SN14_RR_EQUAL : success");
			// } else {
			// System.err.println("Result for : SN14_RR_EQUAL : failed");
			// }
			// if (SN14_RR_PFT.RunTestCase()) {
			// System.out.println("Result for : SN14_RR_PFT : success");
			// } else {
			// System.err.println("Result for : SN14_RR_PFT : failed");
			// }
			// if (SN14_RR_PRI_REQ.RunTestCase()) {
			// System.out.println("Result for : SN14_RR_PRI_REQ : success");
			// } else {
			// System.err.println("Result for : SN14_RR_PRI_REQ : failed");
			// }
			// if (SN14_RR_PRI.RunTestCase()) {
			// System.out.println("Result for : SN14_RR_PRI : success");
			// } else {
			// System.err.println("Result for : SN14_RR_PRI : failed");
			// }
			// if (SN14_RR_PRIPFT_REQ.RunTestCase()) {
			// System.out.println("Result for : SN14_RR_PRIPFT_REQ : success");
			// } else {
			// System.err.println("Result for : SN14_RR_PRIPFT_REQ : failed");
			// }
			// if (SN14_RR_PRIPFT.RunTestCase()) {
			// System.out.println("Result for : SN14_RR_PRIPFT : success");
			// } else {
			// System.err.println("Result for : SN14_RR_PRIPFT : failed");
			// }
			//
			// System.out
			// .println("---------------------------------------------------------------------");
			// System.out.println("CHANGE RATE WITH TILL MATURITY ...........");
			// System.out
			// .println("---------------------------------------------------------------------");
			//
			// if (SN15_RR_EQUAL_REQ.RunTestCase()) {
			// System.out.println("Result for : SN15_RR_EQUAL_REQ : success");
			// } else {
			// System.err.println("Result for : SN15_RR_EQUAL_REQ : failed");
			// }
			// if (SN15_RR_EQUAL.RunTestCase()) {
			// System.out.println("Result for : SN15_RR_EQUAL : success");
			// } else {
			// System.err.println("Result for : SN15_RR_EQUAL : failed");
			// }
			// if (SN15_RR_PFT.RunTestCase()) {
			// System.out.println("Result for : SN15_RR_PFT : success");
			// } else {
			// System.err.println("Result for : SN15_RR_PFT : failed");
			// }
			// if (SN15_RR_PRI_REQ.RunTestCase()) {
			// System.out.println("Result for : SN15_RR_PRI_REQ : success");
			// } else {
			// System.err.println("Result for : SN15_RR_PRI_REQ : failed");
			// }
			// if (SN15_RR_PRI.RunTestCase()) {
			// System.out.println("Result for : SN15_RR_PRI : success");
			// } else {
			// System.err.println("Result for : SN15_RR_PRI : failed");
			// }
			// if (SN15_RR_PRIPFT_REQ.RunTestCase()) {
			// System.out.println("Result for : SN15_RR_PRIPFT_REQ : success");
			// } else {
			// System.err.println("Result for : SN15_RR_PRIPFT_REQ : failed");
			// }
			// if (SN15_RR_PRIPFT.RunTestCase()) {
			// System.out.println("Result for : SN15_RR_PRIPFT : success");
			// } else {
			// System.err.println("Result for : SN15_RR_PRIPFT : failed");
			// }
			//
			// System.out
			// .println("---------------------------------------------------------------------");
			// System.out.println("CHANGE RATE WITH ADJUST MATURITY ...........");
			// System.out
			// .println("---------------------------------------------------------------------");
			//
			// if (SN16_RR_EQUAL_REQ.RunTestCase()) {
			// System.out.println("Result for : SN16_RR_EQUAL_REQ : success");
			// } else {
			// System.err.println("Result for : SN16_RR_EQUAL_REQ : failed");
			// }
			// if (SN16_RR_EQUAL.RunTestCase()) {
			// System.out.println("Result for : SN16_RR_EQUAL : success");
			// } else {
			// System.err.println("Result for : SN16_RR_EQUAL : failed");
			// }
			// if (SN16_RR_PFT.RunTestCase()) {
			// System.out.println("Result for : SN16_RR_PFT : success");
			// } else {
			// System.err.println("Result for : SN16_RR_PFT : failed");
			// }
			// if (SN16_RR_PRI_REQ.RunTestCase()) {
			// System.out.println("Result for : SN16_RR_PRI_REQ : success");
			// } else {
			// System.err.println("Result for : SN16_RR_PRI_REQ : failed");
			// }
			// if (SN16_RR_PRI.RunTestCase()) {
			// System.out.println("Result for : SN16_RR_PRI : success");
			// } else {
			// System.err.println("Result for : SN16_RR_PRI : failed");
			// }
			// if (SN16_RR_PRIPFT_REQ.RunTestCase()) {
			// System.out.println("Result for : SN16_RR_PRIPFT_REQ : success");
			// } else {
			// System.err.println("Result for : SN16_RR_PRIPFT_REQ : failed");
			// }
			// if (SN16_RR_PRIPFT.RunTestCase()) {
			// System.out.println("Result for : SN16_RR_PRIPFT : success");
			// } else {
			// System.err.println("Result for : SN16_RR_PRIPFT : failed");
			// }
			//
			// System.out
			// .println("---------------------------------------------------------------------");
			// System.out
			// .println("MAINTAIN DISBURSEMENT BY ADDING 2 TERMS AND EXTEND MATURITY...........");
			// System.out
			// .println("---------------------------------------------------------------------");
			//
			// if (SN17_RR_EQUAL_REQ.RunTestCase()) {
			// System.out.println("Result for : SN17_RR_EQUAL_REQ : success");
			// } else {
			// System.err.println("Result for : SN17_RR_EQUAL_REQ : failed");
			// }
			// if (SN17_RR_EQUAL.RunTestCase()) {
			// System.out.println("Result for : SN17_RR_EQUAL : success");
			// } else {
			// System.err.println("Result for : SN17_RR_EQUAL : failed");
			// }
			// if (SN17_RR_PFT.RunTestCase()) {
			// System.out.println("Result for : SN17_RR_PFT : success");
			// } else {
			// System.err.println("Result for : SN17_RR_PFT : failed");
			// }
			// if (SN17_RR_PRI_REQ.RunTestCase()) {
			// System.out.println("Result for : SN17_RR_PRI_REQ : success");
			// } else {
			// System.err.println("Result for : SN17_RR_PRI_REQ : failed");
			// }
			// if (SN17_RR_PRI.RunTestCase()) {
			// System.out.println("Result for : SN17_RR_PRI : success");
			// } else {
			// System.err.println("Result for : SN17_RR_PRI : failed");
			// }
			// if (SN17_RR_PRIPFT_REQ.RunTestCase()) {
			// System.out.println("Result for : SN17_RR_PRIPFT_REQ : success");
			// } else {
			// System.err.println("Result for : SN17_RR_PRIPFT_REQ : failed");
			// }
			// if (SN17_RR_PRIPFT.RunTestCase()) {
			// System.out.println("Result for : SN17_RR_PRIPFT : success");
			// } else {
			// System.err.println("Result for : SN17_RR_PRIPFT : failed");
			// }
			//
			// System.out
			// .println("---------------------------------------------------------------------");
			// System.out
			// .println("MAINTAIN DISBURSEMENT BY ADDING 4 TERMS AND EXTEND MATURITY...........");
			// System.out
			// .println("---------------------------------------------------------------------");
			//
			// if (SN18_RR_EQUAL_REQ.RunTestCase()) {
			// System.out.println("Result for : SN18_RR_EQUAL_REQ : success");
			// } else {
			// System.err.println("Result for : SN18_RR_EQUAL_REQ : failed");
			// }
			// if (SN18_RR_EQUAL.RunTestCase()) {
			// System.out.println("Result for : SN18_RR_EQUAL : success");
			// } else {
			// System.err.println("Result for : SN18_RR_EQUAL : failed");
			// }
			// if (SN18_RR_PFT.RunTestCase()) {
			// System.out.println("Result for : SN18_RR_PFT : success");
			// } else {
			// System.err.println("Result for : SN18_RR_PFT : failed");
			// }
			// if (SN18_RR_PRI_REQ.RunTestCase()) {
			// System.out.println("Result for : SN18_RR_PRI_REQ : success");
			// } else {
			// System.err.println("Result for : SN18_RR_PRI_REQ : failed");
			// }
			// if (SN18_RR_PRI.RunTestCase()) {
			// System.out.println("Result for : SN18_RR_PRI : success");
			// } else {
			// System.err.println("Result for : SN18_RR_PRI : failed");
			// }
			// if (SN18_RR_PRIPFT_REQ.RunTestCase()) {
			// System.out.println("Result for : SN18_RR_PRIPFT_REQ : success");
			// } else {
			// System.err.println("Result for : SN18_RR_PRIPFT_REQ : failed");
			// }
			// if (SN18_RR_PRIPFT.RunTestCase()) {
			// System.out.println("Result for : SN18_RR_PRIPFT : success");
			// } else {
			// System.err.println("Result for : SN18_RR_PRIPFT : failed");
			// }
			//
			// System.out
			// .println("---------------------------------------------------------------------");
			// System.out.println("ADD DEFERMENT WITH ADJUST MATURITY...........");
			// System.out
			// .println("---------------------------------------------------------------------");
			//
			// if (SN19_RR_EQUAL_REQ.RunTestCase()) {
			// System.out.println("Result for : SN19_RR_EQUAL_REQ : success");
			// } else {
			// System.err.println("Result for : SN19_RR_EQUAL_REQ : failed");
			// }
			// if (SN19_RR_EQUAL.RunTestCase()) {
			// System.out.println("Result for : SN19_RR_EQUAL : success");
			// } else {
			// System.err.println("Result for : SN19_RR_EQUAL : failed");
			// }
			// if (SN19_RR_PFT.RunTestCase()) {
			// System.out.println("Result for : SN19_RR_PFT : success");
			// } else {
			// System.err.println("Result for : SN19_RR_PFT : failed");
			// }
			// if (SN19_RR_PRI_REQ.RunTestCase()) {
			// System.out.println("Result for : SN19_RR_PRI_REQ : success");
			// } else {
			// System.err.println("Result for : SN19_RR_PRI_REQ : failed");
			// }
			// if (SN19_RR_PRI.RunTestCase()) {
			// System.out.println("Result for : SN19_RR_PRI : success");
			// } else {
			// System.err.println("Result for : SN19_RR_PRI : failed");
			// }
			// if (SN19_RR_PRIPFT_REQ.RunTestCase()) {
			// System.out.println("Result for : SN19_RR_PRIPFT_REQ : success");
			// } else {
			// System.err.println("Result for : SN19_RR_PRIPFT_REQ : failed");
			// }
			// if (SN19_RR_PRIPFT.RunTestCase()) {
			// System.out.println("Result for : SN19_RR_PRIPFT : success");
			// } else {
			// System.err.println("Result for : SN19_RR_PRIPFT : failed");
			// }
			//
			// System.out
			// .println("---------------------------------------------------------------------");
			// System.out.println("ADD DEFERMENT WITH TILL DATE...........");
			// System.out
			// .println("---------------------------------------------------------------------");
			//
			// if (SN20_RR_EQUAL_REQ.RunTestCase()) {
			// System.out.println("Result for : SN20_RR_EQUAL_REQ : success");
			// } else {
			// System.err.println("Result for : SN20_RR_EQUAL_REQ : failed");
			// }
			// if (SN20_RR_EQUAL.RunTestCase()) {
			// System.out.println("Result for : SN20_RR_EQUAL : success");
			// } else {
			// System.err.println("Result for : SN20_RR_EQUAL : failed");
			// }
			// if (SN20_RR_PFT.RunTestCase()) {
			// System.out.println("Result for : SN20_RR_PFT : success");
			// } else {
			// System.err.println("Result for : SN20_RR_PFT : failed");
			// }
			// if (SN20_RR_PRI_REQ.RunTestCase()) {
			// System.out.println("Result for : SN20_RR_PRI_REQ : success");
			// } else {
			// System.err.println("Result for : SN20_RR_PRI_REQ : failed");
			// }
			// if (SN20_RR_PRI.RunTestCase()) {
			// System.out.println("Result for : SN20_RR_PRI : success");
			// } else {
			// System.err.println("Result for : SN20_RR_PRI : failed");
			// }
			// if (SN20_RR_PRIPFT_REQ.RunTestCase()) {
			// System.out.println("Result for : SN20_RR_PRIPFT_REQ : success");
			// } else {
			// System.err.println("Result for : SN20_RR_PRIPFT_REQ : failed");
			// }
			// if (SN20_RR_PRIPFT.RunTestCase()) {
			// System.out.println("Result for : SN20_RR_PRIPFT : success");
			// } else {
			// System.err.println("Result for : SN20_RR_PRIPFT : failed");
			// }
			//
			// System.out
			// .println("---------------------------------------------------------------------");
			// System.out.println("ADD DEFERMENT WITH TILL MATURITY...........");
			// System.out
			// .println("---------------------------------------------------------------------");
			//
			// if (SN21_RR_EQUAL_REQ.RunTestCase()) {
			// System.out.println("Result for : SN21_RR_EQUAL_REQ : success");
			// } else {
			// System.err.println("Result for : SN21_RR_EQUAL_REQ : failed");
			// }
			// if (SN21_RR_EQUAL.RunTestCase()) {
			// System.out.println("Result for : SN21_RR_EQUAL : success");
			// } else {
			// System.err.println("Result for : SN21_RR_EQUAL : failed");
			// }
			// if (SN21_RR_PFT.RunTestCase()) {
			// System.out.println("Result for : SN21_RR_PFT : success");
			// } else {
			// System.err.println("Result for : SN21_RR_PFT : failed");
			// }
			// if (SN21_RR_PRI_REQ.RunTestCase()) {
			// System.out.println("Result for : SN21_RR_PRI_REQ : success");
			// } else {
			// System.err.println("Result for : SN21_RR_PRI_REQ : failed");
			// }
			// if (SN21_RR_PRI.RunTestCase()) {
			// System.out.println("Result for : SN21_RR_PRI : success");
			// } else {
			// System.err.println("Result for : SN21_RR_PRI : failed");
			// }
			// if (SN21_RR_PRIPFT_REQ.RunTestCase()) {
			// System.out.println("Result for : SN21_RR_PRIPFT_REQ : success");
			// } else {
			// System.err.println("Result for : SN21_RR_PRIPFT_REQ : failed");
			// }
			// if (SN21_RR_PRIPFT.RunTestCase()) {
			// System.out.println("Result for : SN21_RR_PRIPFT : success");
			// } else {
			// System.err.println("Result for : SN21_RR_PRIPFT : failed");
			// }
			//
			// System.out
			// .println("---------------------------------------------------------------------");
			// System.out.println("ADD DEFERMENT BY ADDING NEW TERM...........");
			// System.out
			// .println("---------------------------------------------------------------------");
			//
			// if (SN22_RR_EQUAL_REQ.RunTestCase()) {
			// System.out.println("Result for : SN22_RR_EQUAL_REQ : success");
			// } else {
			// System.err.println("Result for : SN22_RR_EQUAL_REQ : failed");
			// }
			// if (SN22_RR_EQUAL.RunTestCase()) {
			// System.out.println("Result for : SN22_RR_EQUAL : success");
			// } else {
			// System.err.println("Result for : SN22_RR_EQUAL : failed");
			// }
			// if (SN22_RR_PFT.RunTestCase()) {
			// System.out.println("Result for : SN22_RR_PFT : success");
			// } else {
			// System.err.println("Result for : SN22_RR_PFT : failed");
			// }
			// if (SN22_RR_PRI_REQ.RunTestCase()) {
			// System.out.println("Result for : SN22_RR_PRI_REQ : success");
			// } else {
			// System.err.println("Result for : SN22_RR_PRI_REQ : failed");
			// }
			// if (SN22_RR_PRI.RunTestCase()) {
			// System.out.println("Result for : SN22_RR_PRI : success");
			// } else {
			// System.err.println("Result for : SN22_RR_PRI : failed");
			// }
			// if (SN22_RR_PRIPFT_REQ.RunTestCase()) {
			// System.out.println("Result for : SN22_RR_PRIPFT_REQ : success");
			// } else {
			// System.err.println("Result for : SN22_RR_PRIPFT_REQ : failed");
			// }
			// if (SN22_RR_PRIPFT.RunTestCase()) {
			// System.out.println("Result for : SN22_RR_PRIPFT : success");
			// } else {
			// System.err.println("Result for : SN22_RR_PRIPFT : failed");
			// }
			//
			// System.out
			// .println("---------------------------------------------------------------------");
			// System.out
			// .println("ADD DEFERMENT BY ADDING NEW TERM & ADDING 2 TERMS...........");
			// System.out
			// .println("---------------------------------------------------------------------");
			//
			// if (SN23_RR_EQUAL_REQ.RunTestCase()) {
			// System.out.println("Result for : SN23_RR_EQUAL_REQ : success");
			// } else {
			// System.err.println("Result for : SN23_RR_EQUAL_REQ : failed");
			// }
			// if (SN23_RR_PRI_REQ.RunTestCase()) {
			// System.out.println("Result for : SN23_RR_PRI_REQ : success");
			// } else {
			// System.err.println("Result for : SN23_RR_PRI_REQ : failed");
			// }
			// if (SN23_RR_PRIPFT_REQ.RunTestCase()) {
			// System.out.println("Result for : SN23_RR_PRIPFT_REQ : success");
			// } else {
			// System.err.println("Result for : SN23_RR_PRIPFT_REQ : failed");
			// }
			//
			// System.out
			// .println("---------------------------------------------------------------------");
			// System.out
			// .println("ADD DEFERMENT BY ADDING NEW TERM & ADDING 2 TERMS AFTER LAST REPAY...........");
			// System.out
			// .println("---------------------------------------------------------------------");
			//
			// if (SN24_RR_EQUAL_REQ.RunTestCase()) {
			// System.out.println("Result for : SN24_RR_EQUAL_REQ : success");
			// } else {
			// System.err.println("Result for : SN24_RR_EQUAL_REQ : failed");
			// }
			// if (SN24_RR_PRI_REQ.RunTestCase()) {
			// System.out.println("Result for : SN24_RR_PRI_REQ : success");
			// } else {
			// System.err.println("Result for : SN24_RR_PRI_REQ : failed");
			// }
			// if (SN24_RR_PRIPFT_REQ.RunTestCase()) {
			// System.out.println("Result for : SN24_RR_PRIPFT_REQ : success");
			// } else {
			// System.err.println("Result for : SN24_RR_PRIPFT_REQ : failed");
			// }
			//
			// System.out
			// .println("---------------------------------------------------------------------");
			// System.out
			// .println("ADD DEFERMENT WITH TILL DATE & ADD DEFERMENT BY INCLUDING EXISTING DEFERMENTS...........");
			// System.out
			// .println("---------------------------------------------------------------------");
			//
			// if (SN25_RR_EQUAL_REQ.RunTestCase()) {
			// System.out.println("Result for : SN25_RR_EQUAL_REQ : success");
			// } else {
			// System.err.println("Result for : SN25_RR_EQUAL_REQ : failed");
			// }
			// if (SN25_RR_EQUAL.RunTestCase()) {
			// System.out.println("Result for : SN25_RR_EQUAL : success");
			// } else {
			// System.err.println("Result for : SN25_RR_EQUAL : failed");
			// }
			// if (SN25_RR_PFT.RunTestCase()) {
			// System.out.println("Result for : SN25_RR_PFT : success");
			// } else {
			// System.err.println("Result for : SN25_RR_PFT : failed");
			// }
			// if (SN25_RR_PRI_REQ.RunTestCase()) {
			// System.out.println("Result for : SN25_RR_PRI_REQ : success");
			// } else {
			// System.err.println("Result for : SN25_RR_PRI_REQ : failed");
			// }
			// if (SN25_RR_PRI.RunTestCase()) {
			// System.out.println("Result for : SN25_RR_PRI : success");
			// } else {
			// System.err.println("Result for : SN25_RR_PRI : failed");
			// }
			// if (SN25_RR_PRIPFT_REQ.RunTestCase()) {
			// System.out.println("Result for : SN25_RR_PRIPFT_REQ : success");
			// } else {
			// System.err.println("Result for : SN25_RR_PRIPFT_REQ : failed");
			// }
			// if (SN25_RR_PRIPFT.RunTestCase()) {
			// System.out.println("Result for : SN25_RR_PRIPFT : success");
			// } else {
			// System.err.println("Result for : SN25_RR_PRIPFT : failed");
			// }
			//
			// System.out
			// .println("---------------------------------------------------------------------");
			// System.out
			// .println("CHANGE REPAY & MAINTAIN DISBURSEMENT BY ADJUSTING TERMS TILL DATE...........");
			// System.out
			// .println("---------------------------------------------------------------------");
			//
			// if (SN26_RR_EQUAL_REQ.RunTestCase()) {
			// System.out.println("Result for : SN26_RR_EQUAL_REQ : success");
			// } else {
			// System.err.println("Result for : SN26_RR_EQUAL_REQ : failed");
			// }
			// if (SN26_RR_EQUAL.RunTestCase()) {
			// System.out.println("Result for : SN26_RR_EQUAL : success");
			// } else {
			// System.err.println("Result for : SN26_RR_EQUAL : failed");
			// }
			// if (SN26_RR_PFT.RunTestCase()) {
			// System.out.println("Result for : SN26_RR_PFT : success");
			// } else {
			// System.err.println("Result for : SN26_RR_PFT : failed");
			// }
			// if (SN26_RR_PRI_REQ.RunTestCase()) {
			// System.out.println("Result for : SN26_RR_PRI_REQ : success");
			// } else {
			// System.err.println("Result for : SN26_RR_PRI_REQ : failed");
			// }
			// if (SN26_RR_PRI.RunTestCase()) {
			// System.out.println("Result for : SN26_RR_PRI : success");
			// } else {
			// System.err.println("Result for : SN26_RR_PRI : failed");
			// }
			// if (SN26_RR_PRIPFT_REQ.RunTestCase()) {
			// System.out.println("Result for : SN26_RR_PRIPFT_REQ : success");
			// } else {
			// System.err.println("Result for : SN26_RR_PRIPFT_REQ : failed");
			// }
			// if (SN26_RR_PRIPFT.RunTestCase()) {
			// System.out.println("Result for : SN26_RR_PRIPFT : success");
			// } else {
			// System.err.println("Result for : SN26_RR_PRIPFT : failed");
			// }
			//
			// System.out
			// .println("---------------------------------------------------------------------");
			// System.out
			// .println("ADD DEFERMENT WITH TILL DATE & ADD DEFERMENT BY EXCLUDING EXISTING DEFERMENTS...........");
			// System.out
			// .println("---------------------------------------------------------------------");
			//
			// if (SN27_RR_EQUAL_REQ.RunTestCase()) {
			// System.out.println("Result for : SN27_RR_EQUAL_REQ : success");
			// } else {
			// System.err.println("Result for : SN27_RR_EQUAL_REQ : failed");
			// }
			// if (SN27_RR_EQUAL.RunTestCase()) {
			// System.out.println("Result for : SN27_RR_EQUAL : success");
			// } else {
			// System.err.println("Result for : SN27_RR_EQUAL : failed");
			// }
			// if (SN27_RR_PFT.RunTestCase()) {
			// System.out.println("Result for : SN27_RR_PFT : success");
			// } else {
			// System.err.println("Result for : SN27_RR_PFT : failed");
			// }
			// if (SN27_RR_PRI_REQ.RunTestCase()) {
			// System.out.println("Result for : SN27_RR_PRI_REQ : success");
			// } else {
			// System.err.println("Result for : SN27_RR_PRI_REQ : failed");
			// }
			// if (SN27_RR_PRI.RunTestCase()) {
			// System.out.println("Result for : SN27_RR_PRI : success");
			// } else {
			// System.err.println("Result for : SN27_RR_PRI : failed");
			// }
			// if (SN27_RR_PRIPFT_REQ.RunTestCase()) {
			// System.out.println("Result for : SN27_RR_PRIPFT_REQ : success");
			// } else {
			// System.err.println("Result for : SN27_RR_PRIPFT_REQ : failed");
			// }
			// if (SN27_RR_PRIPFT.RunTestCase()) {
			// System.out.println("Result for : SN27_RR_PRIPFT : success");
			// } else {
			// System.err.println("Result for : SN27_RR_PRIPFT : failed");
			// }
			//
			// System.out
			// .println("---------------------------------------------------------------------");
			// System.out
			// .println("ADD DEFERMENT WITH TILL DATE & ADD DEFERMENT BY EXCLUDING EXISTING DEFERMENTS : FAIL ...........");
			// System.out
			// .println("---------------------------------------------------------------------");
			//
			// if (SN28_RR_EQUAL_REQ.RunTestCase()) {
			// System.out.println("Result for : SN28_RR_EQUAL_REQ : success");
			// } else {
			// System.err.println("Result for : SN28_RR_EQUAL_REQ : failed");
			// }
			// if (SN28_RR_EQUAL.RunTestCase()) {
			// System.out.println("Result for : SN28_RR_EQUAL : success");
			// } else {
			// System.err.println("Result for : SN28_RR_EQUAL : failed");
			// }
			// if (SN28_RR_PFT.RunTestCase()) {
			// System.out.println("Result for : SN28_RR_PFT : success");
			// } else {
			// System.err.println("Result for : SN28_RR_PFT : failed");
			// }
			// if (SN28_RR_PRI_REQ.RunTestCase()) {
			// System.out.println("Result for : SN28_RR_PRI_REQ : success");
			// } else {
			// System.err.println("Result for : SN28_RR_PRI_REQ : failed");
			// }
			// if (SN28_RR_PRI.RunTestCase()) {
			// System.out.println("Result for : SN28_RR_PRI : success");
			// } else {
			// System.err.println("Result for : SN28_RR_PRI : failed");
			// }
			// if (SN28_RR_PRIPFT_REQ.RunTestCase()) {
			// System.out.println("Result for : SN28_RR_PRIPFT_REQ : success");
			// } else {
			// System.err.println("Result for : SN28_RR_PRIPFT_REQ : failed");
			// }
			// if (SN28_RR_PRIPFT.RunTestCase()) {
			// System.out.println("Result for : SN28_RR_PRIPFT : success");
			// } else {
			// System.err.println("Result for : SN28_RR_PRIPFT : failed");
			// }
			//
			// System.out
			// .println("---------------------------------------------------------------------");
			// System.out
			// .println("MAINTAIN DISBURSEMENT BY ADDING 4 TERMS AND EXTEND MATURITY AND REMOVE TERMS...........");
			// System.out
			// .println("---------------------------------------------------------------------");
			//
			// if (SN29_RR_EQUAL_REQ.RunTestCase()) {
			// System.out.println("Result for : SN29_RR_EQUAL_REQ : success");
			// } else {
			// System.err.println("Result for : SN29_RR_EQUAL_REQ : failed");
			// }
			// if (SN29_RR_EQUAL.RunTestCase()) {
			// System.out.println("Result for : SN29_RR_EQUAL : success");
			// } else {
			// System.err.println("Result for : SN29_RR_EQUAL : failed");
			// }
			// if (SN29_RR_PFT.RunTestCase()) {
			// System.out.println("Result for : SN29_RR_PFT : success");
			// } else {
			// System.err.println("Result for : SN29_RR_PFT : failed");
			// }
			// if (SN29_RR_PRI_REQ.RunTestCase()) {
			// System.out.println("Result for : SN29_RR_PRI_REQ : success");
			// } else {
			// System.err.println("Result for : SN29_RR_PRI_REQ : failed");
			// }
			// if (SN29_RR_PRI.RunTestCase()) {
			// System.out.println("Result for : SN29_RR_PRI : success");
			// } else {
			// System.err.println("Result for : SN29_RR_PRI : failed");
			// }
			// if (SN29_RR_PRIPFT_REQ.RunTestCase()) {
			// System.out.println("Result for : SN29_RR_PRIPFT_REQ : success");
			// } else {
			// System.err.println("Result for : SN29_RR_PRIPFT_REQ : failed");
			// }
			// if (SN29_RR_PRIPFT.RunTestCase()) {
			// System.out.println("Result for : SN29_RR_PRIPFT : success");
			// } else {
			// System.err.println("Result for : SN29_RR_PRIPFT : failed");
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
