package top.yxgu.utils;

import java.util.Properties;

/**
 * @author blc
 *
 */
public class OSInfo {
	
	private static Properties props = System.getProperties(); 			//获得系统属性集  
	private static Runtime rt = Runtime.getRuntime();					//运行时
	
	/**
	 * 操作系统构架
	 * @return
	 */
	public static String getOSArch() {
		return props.getProperty("os.arch"); 		//操作系统构架
	}
	
	/**
	 * 操作系统名称
	 * @return
	 */
	public static String getOSName() {
		return props.getProperty("os.name"); 		//操作系统名称 
	}
	
	/**
	 * 操作系统版本
	 * @return
	 */
	public static String getOSVersion() {
		return props.getProperty("os.version"); 	//操作系统版本
	}
	
	/**
	 * cpu数
	 * @return
	 */
	public static int getProcessorNum() {
		return rt.availableProcessors();		//cpu数
	}
	
	public static int getJVMTotalMemorySize(){
		return (int)Runtime.getRuntime().totalMemory()/1024 /1024;
	}
	
	public static int getJVMFreeMemorySize(){
		return (int)Runtime.getRuntime().freeMemory() /1024 /1024;
	}
	
}
