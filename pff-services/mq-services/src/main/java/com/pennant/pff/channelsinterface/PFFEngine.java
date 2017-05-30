package com.pennant.pff.channelsinterface;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ibm.mq.MQMessage;

/**
 * The PFF Engine will check for the messages from PFF Queue with polling and
 * reply.
 */
public class PFFEngine extends MessageQueue {
	
	final Logger logger = LoggerFactory.getLogger(PFFEngine.class);
	
	private int threadCount;
	protected PFFDataAccess dataAccess = null;
	public static ApplicationContext context;
	private static boolean stop = false;
	private static boolean start = false;
	private ExecutorService pool;
	public static AtomicLong usedThreads = new AtomicLong(0L);

	/**
	 * The entry point for CS Engine that invoke the required programs.
	 * 
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		String mode;

		if (args.length == 0) {
			mode = "start";
		} else {
			mode = args[0];
		}
		if ("start".equals(mode)) {
			System.out.println("Starting service...");
			start = true;
			loadContext();
		} else if ("stop".equals(mode)) {
			if (start) {
				System.out.println("Stopping service...");
				putDummyMessage();
				Thread.sleep(50);
				stop = true;
			} else {
				System.out.println("Service already in Stop mode");
			}
		}
	}

	private static void loadContext() {
		// Load the definitions from the given XML file.
		context = new ClassPathXmlApplicationContext("conf/PFF-Channels-Interface.xml");
	}

	public void init() {
		logger.info("Initialising PFF Engine...");
		setMQEnvironment();
		start();
	}

	/**
	 * Initializes the CS Engine and wait for transaction messages that were
	 * stop/start/status of the components.
	 */
	public void start() {
		logger.info("Starting PFF Engine...");

		try {
			pool = Executors.newFixedThreadPool(threadCount);

			// Infinite loop to read messages from the message queue
			while (!stop) {
				MQMessage message;

				try {
					logger.debug("Waiting for the message...");
					message = getMessage(queueManagerName, queueName);

					if (stop) {
						break;
					}

					if (message == null) {
						continue;
					}

					while (usedThreads.get() >= threadCount) {
						logger.info("Waiting threads [{}] to be released...",
								usedThreads.get());

						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							logger.error("Exception: {}", e.getMessage());
						}
					}

					usedThreads.incrementAndGet();
					pool.execute(new RequestProcessor(message,
							queueManagerName, dataAccess));

					if (stop) {
						while (usedThreads.get() > 0) {
							logger.info(
									"Waiting threads [{}] to be released...",
									usedThreads.get());

							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								logger.error("Exception: {}", e.getMessage());
							}
						}
					}
				} catch (Exception e) {
					logger.error("Exception: {}", e);
				} finally {
					message = null;
				}
			}
		} catch (Exception e) {
			logger.error("Exception: {}", e);
		} finally {
			logger.info("Closing main thread...");
			closeQueue();
			disconnectManager();

			if (null != pool) {
				pool.shutdown();
				pool = null;
			}

			context = null;
			logger.info("Main thread execution completed.");
		}
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	public PFFDataAccess getDataAccess() {
		return dataAccess;
	}

	public void setDataAccess(PFFDataAccess dataAccess) {
		this.dataAccess = dataAccess;
	}
}
