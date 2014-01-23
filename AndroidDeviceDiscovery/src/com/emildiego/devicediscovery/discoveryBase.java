/**
 *  This abstract class forms the base of all my device discovery classes.
 * It handles the logging of information to the logcat output as well as
 * defines some abstract classes that must be implemented by each
 * derived class  
 *
 * @author Emil Diego
 * 
 */

package com.emildiego.devicediscovery;

import java.text.DecimalFormat;
import android.util.Log;

import android.content.Context;

/**
 * 
 * The base class for all the discovery classes.  Contains some abstract
 * classes that must be implemented in all sub classes.
 * 
 * @author Emil Diego
 *
 */
public abstract class discoveryBase {
	
	/**
	 * The String TAG to be used to log messages to the Android log.
	 */
	protected String				m_sTag = "deviceDiscovery";
	
	
	/**
	 *  A reference to the application context
	 */
	protected Context				m_xContext;
	
	/**
	 *  These members are used to manage the update thread.
	 */
	protected Boolean				m_bRun;
	protected long					m_lUpdateInterval;
	protected Thread				m_xUpdateThread;
	
	/**
	 * Size Formats
	 */
	static final int		SIZE_NONE	= 0;
	static final int		SIZE_KB		= 1;
	static final int		SIZE_MB		= 2;
	static final int		SIZE_GB		= 3;
	
	/**
	 *  Conversion sizes from byte to each unit.  This is the number of bytes in each unit.
	 */
	static final float		CONVERSION_KB		= 1024f;
	static final float		CONVERSION_MB		= 1048576f;
	static final float		CONVERSION_GB		= 1073741824f;

	/**
	 * Base constructor
	 * 
	 * @param xContext The device context.
	 * @param sLogTag The Tag to be used by the message log.  If null then use default tag.
	 */
	protected discoveryBase(Context xContext, String sLogTag)
	{
		this.m_xContext			= xContext;
		if (sLogTag != null)
			this.m_sTag = sLogTag;
		
		this.m_bRun 			= false;
		this.m_lUpdateInterval	= 0;
		
	}
	
	/**
	 * Write a debug message to the log
	 * 
	 * @param sMessage A debug message we want to write to the log.
	 */
	public void logDebug(String sMessage)
	{
		Log.d(m_sTag, sMessage);
	}
	
	/**
	 * Write a message to the log
	 * 
	 * @param sMessage A message we want to write to the log.
	 */
	public void logMessage(String sMessage)
	{
		
		Log.i(m_sTag, sMessage);
	}
	

	/**
	 * Write a error message to the log
	 * 
	 * @param sMessage A message we want to write to the log.
	 */
	public void logError(String sMessage)
	{
		Log.e(m_sTag, sMessage);
	}
	
	/**
	 * Write a stack trace to the log
	 * 
	 * @param aStackTrace The stack trace that is thrwon by an exception.
	 */
	public void logStackTrace(StackTraceElement[] aStackTrace)
	{
		//* Check to make sure we have a valid pointer
		if (aStackTrace != null)
		{
			int iCount = aStackTrace.length;
			for (int iIndex=0; iIndex < iCount; iIndex++)
			{
				Log.e(m_sTag, aStackTrace[iIndex].toString());
			}
		}	
	}
	

	/**
	 * set the delay interval for updating the information
	 * 
	 * @param lDelay The delay in milliseconds.
	 */
	public void setUpdateInterval(long lDelay)
	{
		this.m_lUpdateInterval = lDelay;
	}
	
	/**
	 * Convert the storage into a more human friendly format
	 * 
	 * @param lSize The size that we want to convert
	 * @param iFormat The format we want to convert to
	 * 
	 * @return a string representing the formatted value.
	 */
	protected String convertStorage(long lSize, int iFormat)
	{
		String 			sResult = "";
		float			fTmpResult = 0f;
		DecimalFormat	dcFormatter;
		
		try 
		{
			
			if (iFormat == storage.SIZE_KB)
			{
				//* We want to convert the number into Kilobytes.
				fTmpResult = (float)lSize / storage.CONVERSION_KB;
				dcFormatter = new DecimalFormat("#.## KB");
				sResult = dcFormatter.format(fTmpResult);
			}
			else if (iFormat == storage.SIZE_MB)
			{
				//* We want to convert the number into Megabytes.
				fTmpResult = (float)lSize / storage.CONVERSION_MB;
				dcFormatter = new DecimalFormat("#.## MB");
				sResult = dcFormatter.format(fTmpResult);
			}
			else if (iFormat == storage.SIZE_GB)
			{
				//* We want to convert the number into Megabytes.
				fTmpResult = (float)lSize / storage.CONVERSION_GB;
				dcFormatter = new DecimalFormat("#.## GB");
				sResult = dcFormatter.format(fTmpResult);
			}
			else
			{
				//* try to determine the most appropriate format
				if (lSize > 0 && lSize <= storage.CONVERSION_KB)
				{
					//* lets convert to kilobytes
					return this.convertStorage(lSize, SIZE_KB);
				}
				else if (lSize > storage.CONVERSION_MB && lSize <= storage.CONVERSION_GB)
				{
					//* lets convert to megabytes
					return this.convertStorage(lSize,  SIZE_MB);
				}
				else
				{
					//* lets convert to gigabytes
					return this.convertStorage(lSize,  SIZE_GB);
				}
			}
			
			return sResult;
			
		}
		catch (Exception exp)
		{
			this.logError("An Exception occured while trying to convert the storage size.");
			return null;
		}
	}
	
	/**
	 * This function must be defined by all child classes.  It's 
	 * job is to query the device for the information represented 
	 * by the class.
	 * 
	 * @return Returns true if the device was queried successfully.
	 */
	public abstract boolean query();

	/**
	 * This function must be implemented in all child classes.  It's 
	 * job is to create a human readable string that summarizes all 
	 * the specific information represented by the class.
	 * 
	 * @return Returns a string representing a summary of the device information.
	 */
	public abstract String summaryString();

}

