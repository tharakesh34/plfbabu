package com.pff.main;


import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;


public class StartAdapter {

	private static Log LOG = LogFactory.getLog(StartAdapter.class);
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LOG.entering("Main Method "); 
		try {
	/*		XmlBeanFactory factory=new XmlBeanFactory(new FileSystemResource("src/com/nt/cfgs/applicationContext.xml"));
			   // get ServiceBean obj
			factory.getBean("service")*/;
			ServiceRequestProcess requestProcess= new ServiceRequestProcess();
			Thread thread= new Thread(requestProcess);
			LOG.info("Before Thread Started");
			thread.start();
			LOG.info("After Thread Started");
		} catch (Exception e) {
			LOG.info("Error"+e);
		}
		LOG.exiting("Main Method ");
	}

}



