package com.testing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ThreadingNumbers implements Runnable {
	int threadCount = 3;
	public static int count = 1;
	private static Map<String, Thread> threadMap = null;
	private static List<Thread> listThread = null;
	@Override
	public void run() {
		 try {
			callSyncMethod();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
    private void callSyncMethod() throws InterruptedException {
    	System.out.println(Thread.currentThread().getName()+"---->"+count++);
	}

	public static void main(String args[]) {
		ThreadingNumbers thNumbers = new ThreadingNumbers();
		Thread t1 = new Thread(thNumbers, "T1");
		Thread t2 = new Thread(thNumbers, "T2");
		Thread t3 = new Thread(thNumbers, "T3");
		Thread t4 = new Thread(thNumbers, "T4");
		Thread t5 = new Thread(thNumbers, "T5");
/*		threadMap = new HashMap<String, Thread>();
		threadMap.put("T1", t1);
		threadMap.put("T2", t2);
		threadMap.put("T3", t3);
		threadMap.put("T4", t4);
		threadMap.put("T5", t5);
		
		Set setObj = threadMap.entrySet();
		Iterator ite = setObj.iterator();
		while(ite.hasNext()) {
			Map.Entry<String, Thread> entry = (Entry<String, Thread>) ite.next();
			entry.getValue().start();
		}*/
		
		listThread = new ArrayList<Thread>();
		listThread.add(0, t1);
		listThread.add(1, t2);
		listThread.add(2, t3);
		listThread.add(3, t4);
		listThread.add(4, t5);
			for(int i=0; i<listThread.size(); i++) {
				listThread.get(i).start();
			}
	}
}
