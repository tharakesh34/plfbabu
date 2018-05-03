package com.pennant.util;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StopWatch;

import com.sun.management.OperatingSystemMXBean;

@SuppressWarnings("restriction")
public class PerfLogInterceptor implements MethodInterceptor {
    private final Logger log = Logger.getLogger(PerfLogInterceptor.class);
    
    private long thresholdMs;
    private long warnThresholdMs;
    private long errorThresholdMs;
    
    public Object invoke(MethodInvocation invocation) throws Throwable {
        StopWatch sw = new StopWatch();

        sw.start(invocation.getMethod().getName());
        Object returnValue = invocation.proceed();
        sw.stop();

		if (sw.getTotalTimeMillis() >= thresholdMs) {
			logMethodCall(invocation, sw.getTotalTimeMillis());
		}
        
        return returnValue;
    }
    
	private void logMethodCall(MethodInvocation invocation, long ms)   {
        Method method = invocation.getMethod();
        Object target = invocation.getThis();
        
        try {
        StringBuffer msg = new StringBuffer();
        msg.append("TIMER - Executed Method: ");
        msg.append(target.getClass().getName());
        msg.append('.');
        msg.append(method.getName());
        msg.append(" in (ms): ");
        msg.append(ms);
        if (ms > errorThresholdMs) {
			OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
            msg.append(", FreePhysicalMemory : " + osBean.getFreePhysicalMemorySize()/(1024*1024) + "MB, " );
            msg.append("TotalPhysicalMemory : " + osBean.getTotalPhysicalMemorySize()/(1024*1024) + "MB, " );
            msg.append("ProcessCPULoad : " + osBean.getProcessCpuLoad() + ", " );
            msg.append("SystemCPULoad : " + osBean.getSystemCpuLoad() + ", " );
            msg.append("FreeSwapSize : " + osBean.getFreeSwapSpaceSize()/(1024*1024) + "MB, " );
            log.error(msg);
        } else if (ms > warnThresholdMs) {
			OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
            msg.append(", FreePhysicalMemory : " + osBean.getFreePhysicalMemorySize()/(1024*1024) + "MB, " );
            msg.append("TotalPhysicalMemory : " + osBean.getTotalPhysicalMemorySize()/(1024*1024) + "MB, " );
            msg.append("ProcessCPULoad : " + osBean.getProcessCpuLoad() + ", " );
            msg.append("SystemCPULoad : " + osBean.getSystemCpuLoad() + ", " );
            msg.append("FreeSwapSize : " + osBean.getFreeSwapSpaceSize()/(1024*1024) + "MB, " );
            log.warn(msg);
        } else {
            log.info(msg);
        }
        } catch (Exception ex) {
        	log.error("Error logging performance details : " + ex.getMessage());
        }
    }

    // Spring injection
    @Required
    public void setThresholdMs( long thresholdMs ){
        this.thresholdMs = thresholdMs;
    }

    @Required
    public void setWarnThresholdMs( long warnThresholdMs ) {
        this.warnThresholdMs = warnThresholdMs;
    }

    @Required
    public void setErrorThresholdMs( long errorThresholdMs )  {
        this.errorThresholdMs = errorThresholdMs;
    }
}