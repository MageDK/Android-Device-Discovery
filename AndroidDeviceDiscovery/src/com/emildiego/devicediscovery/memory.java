/**
 * memory
 * 
 * This class will query the device for information about the memory
 * installed on the device.  We do this by reading the /proc/meminfo
 * file that is available on all Linux operating systems and parse 
 * out the relevant information.
 * 
 * @author Emil Diego
 */
package com.emildiego.devicediscovery;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.content.Context;

/**
 * The object used to store the history for this object.
 */
class memoryHistoryEntry
{
	public long						m_lTotalMemory;
	public long						m_lFreeMemory;
	public long						m_lActiveMemory;
	public long						m_lInactiveMemory;
	public long						m_lKernelStack;
	
	public Date						m_dtTimestamp;
	
	/**
	 * Default Constructor.  Initialize the values and set the timestamp.
	 */
	public memoryHistoryEntry()
	{
		m_lTotalMemory 		= 0;
		m_lFreeMemory		= 0;
		m_lActiveMemory		= 0;
		m_lInactiveMemory	= 0;
		m_lKernelStack		= 0;
		m_dtTimestamp = new Date();	
	}
}

/**
 * The main memory class
 */
public class memory extends discoveryBase implements discoveryHistory 
{

//	private ActivityManager.MemoryInfo			m_xMemInfo;
	/** 
	 * the storage for the memory information we are looking for.
	 */
	private long						m_lTotalMemory;
	private long						m_lFreeMemory;
	private long						m_lActiveMemory;
	private long						m_lInactiveMemory;
	private long						m_lKernelStack;
	
	/** 
	 * store the results of reading the /proc/meminfo file
	 */
	private String						m_sRawMemInfo;
	
	/** 
	 * the regular expressions used to search the raw memory info
	 */
	static final String 				TOTAL_MEM_REGEX = "MemTotal:[\\s]+([0-9]+)(\\s)kB";
	static final String 				FREE_MEM_REGEX = "MemFree:[\\s]+([0-9]+)(\\s)kB";
	static final String 				ACTIVE_MEM_REGEX = "Active:[\\s]+([0-9]+)(\\s)kB";
	static final String 				INACTIVE_MEM_REGEX = "Inactive:[\\s]+([0-9]+)(\\s)kB";
	static final String					KERNEL_STACK = "KernelStack:[\\s]+([0-9]+)(\\s)kB";

	/** 
	 * Keep track of our history
	 */
	protected int								m_iMaxHistory = 20;		//* Default to 20
	protected ArrayList<Object>					m_xHistoryList;
	
	/**
	 * Default Constructor.
	 * 
	 * @param xContext The application context
	 * @param sLogTag The Tag to be used by the Android message logger
	 */
	public memory(Context xContext, String sLogTag)
	{
		super(xContext, sLogTag);
		
		this.m_lTotalMemory = 0;
		this.m_lFreeMemory = 0;
		this.m_lActiveMemory = 0;
		this.m_lInactiveMemory = 0;
		
		this.m_sRawMemInfo = null;
		
		//* initialize the history list
		this.m_xHistoryList = new ArrayList<Object>();
		
	}
	
	/**
	 * Retreive the raw memory info by reading the /proc/meminfo file that 
	 * exists on the device.  Part of all Android operating systems.
	 * 
	 */
	private void getRawInfo()
	{
		BufferedReader 		ifp = null;
		
		/** 
		 * empty the buffer
		 */
		this.m_sRawMemInfo = "";
		
		try 
		{
			/** 
			 * execute the shell command 
			 */
			Process process = null;
			process = Runtime.getRuntime().exec("cat /proc/meminfo");
			
			/** 
			 * read the output from the command
			 */
			String sLine = null;
			ifp = new BufferedReader(new InputStreamReader(process.getInputStream()));

			/** 
			 * Read all the available output and store it in the class member
			 */
			while ( (sLine = ifp.readLine()) != null)
			{
            	this.m_sRawMemInfo += sLine + "\n";
			}
		} catch (IOException exp) {
			this.logError("There was an error while trying to execute the shell command.");
			this.logStackTrace(exp.getStackTrace());
		
		}
		finally
		{
			/** 
			 * we need to close the stream once everything is done.
			 */
			try {
				if (ifp != null)
					ifp.close();
			}
			catch (IOException exp)
			{
				this.logError("There was an error while trying close out input stream.");
				this.logStackTrace(exp.getStackTrace());
			}
		}
	}
	
	/**
	 * This function parses out the data from the results of the shell command 
	 * and populates all the member variables.  We are going to use regular expressions
	 * to find the pieces of data we need.
	 */
	public void parseResults()
	{
		String	sTotalMemory			= null;
		String 	sFreeMemory				= null;
		String 	sActiveMemory			= null;
		String 	sInactiveMemory			= null;
		String	sKernelStack			= null;
		
		Pattern		xRegexSearchPattern 	= null;
		Matcher		xSearch  				= null;
		
		/** 
		 * Check to make sure we have some data to parse
		 */
		if (this.m_sRawMemInfo == null)
		{
			this.logMessage("No results available.");
			return;
		}
		
		/** 
		 * Get the total memory information
		 */
		xRegexSearchPattern = Pattern.compile( memory.TOTAL_MEM_REGEX );
		xSearch = xRegexSearchPattern.matcher(this.m_sRawMemInfo);
		/**
		 *  check to see if we found a match
		 */
		if (xSearch.find())
		{
			sTotalMemory = xSearch.group(0);
			this.logDebug("Found Total Memory: " + sTotalMemory);
			
			/** 
			 * we want to remove the non numeric characters in the string
			 */
			try {
				this.m_lTotalMemory = Long.parseLong( sTotalMemory.replaceAll( "[^\\d]", "" ).trim() );
			}
			catch (NumberFormatException exp)
			{
				this.m_lTotalMemory = 0;
				this.logError("An error occured while trying to parse the memory information.");
				this.logStackTrace(exp.getStackTrace());
			}
		}
		
		/** 
		 * Get the free memory information
		 */
		xRegexSearchPattern = Pattern.compile( memory.FREE_MEM_REGEX );
		xSearch = xRegexSearchPattern.matcher(this.m_sRawMemInfo);
		/** 
		 * check to see if we found a match
		 */
		if (xSearch.find())
		{
			sFreeMemory = xSearch.group(0);
			this.logDebug("Found Free Memory: " + sFreeMemory);
			
			/** 
			 * we want to remove the non numeric characters in the string
			 */
			try {
				this.m_lFreeMemory = Long.parseLong( sFreeMemory.replaceAll( "[^\\d]", "" ).trim() );
			}
			catch (NumberFormatException exp)
			{
				this.logError("An error occured while trying to parse the memory information.");
				this.logStackTrace(exp.getStackTrace());
			}
		}

		/**
		 * Get the active memory information
		 */
		xRegexSearchPattern = Pattern.compile( memory.ACTIVE_MEM_REGEX );
		xSearch = xRegexSearchPattern.matcher(this.m_sRawMemInfo);
		// check to see if we found a match

		if (xSearch.find())
		{
			sActiveMemory = xSearch.group(0);
			this.logDebug("Found Active Memory: " + sActiveMemory);
			
			// we want to remove the non numeric characters in the string
			try {
				this.m_lActiveMemory = Long.parseLong( sActiveMemory.replaceAll( "[^\\d]", "" ).trim() );
			}
			catch (NumberFormatException exp)
			{
				this.m_lActiveMemory = 0;
				this.logError("An error occured while trying to parse the memory information.");
				this.logStackTrace(exp.getStackTrace());
			}
		}

		// Get the inactive memory information
		xRegexSearchPattern = Pattern.compile( memory.INACTIVE_MEM_REGEX );
		xSearch = xRegexSearchPattern.matcher(this.m_sRawMemInfo);
		// check to see if we found a match
		if (xSearch.find())
		{
			sInactiveMemory = xSearch.group(0);
			this.logDebug("Found Inactive Memory: " + sInactiveMemory);
			
			// we want to remove the non numeric characters in the string
			try {
				this.m_lInactiveMemory = Long.parseLong( sInactiveMemory.replaceAll( "[^\\d]", "" ).trim() );
			}
			catch (NumberFormatException exp)
			{
				this.m_lInactiveMemory = 0;
				this.logError("An error occured while trying to parse the memory information.");
				this.logStackTrace(exp.getStackTrace());
			}
		}	
		
		// Get the kernel stack memory information
		xRegexSearchPattern = Pattern.compile( memory.KERNEL_STACK );
		xSearch = xRegexSearchPattern.matcher(this.m_sRawMemInfo);
		//* check to see if we found a match
		if (xSearch.find())
		{
			sKernelStack = xSearch.group(0);
			this.logDebug("Kernel Stack: " + sKernelStack);
			
			//* we want to remove the non numeric characters in the string
			try {
				this.m_lKernelStack = Long.parseLong( sKernelStack.replaceAll( "[^\\d]", "" ).trim() );
			}
			catch (NumberFormatException exp)
			{
				this.m_lKernelStack = 0;
				this.logError("An error occured while trying to parse the memory information.");
				this.logStackTrace(exp.getStackTrace());
			}
		}	
	}
	
	/**
	 * query
	 * 
	 * Query the device about its memory information
	 * 
	 * @return Returns true if the query was successfull, false if it wasn't.
	 */
	public boolean query()
	{
		//* Get the raw memory information from the system
		this.getRawInfo();
		
		//* now parse out the values we need
		this.parseResults();
		
		//* Every time we query the device we want to write the latest results into 
		//* the history list.
		memoryHistoryEntry	xTmpEntry = new memoryHistoryEntry();
		xTmpEntry.m_lTotalMemory		= this.getTotalMemory();
		xTmpEntry.m_lActiveMemory		= this.getActiveMemory();
		xTmpEntry.m_lInactiveMemory		= this.getInactiveMemory();
		xTmpEntry.m_lFreeMemory			= this.getAvailableMemory();
		xTmpEntry.m_lKernelStack		= this.getKernelStackMemory();
		
		//* Add the history entry
		this.addHistoryEntry(xTmpEntry);
		
		return true;
	}
	
	/**
	 * Retreive the total available memory on the system.  This number should not 
	 * be considered absolute: due to the nature of the kernel, a 
	 * significant portion of this memory is actually in use and needed 
	 * for the overall system to run well.
	 * 
	 * @return A long value representing the total available memory at the time of the last query.
	 */
	public long getAvailableMemory()
	{
		return this.m_lFreeMemory;
	}
	
	/**
	 * Retrieve the total memory on the device.
	 * 
	 * @return A long value representing the total memory at the time of the last query.
	 */
	public long getTotalMemory()
	{
		return this.m_lTotalMemory;
	}
	
	/**
	 * Retreive the total active memory on the system.  
	 * 
	 * @return A long value representing the total active memory at the time of the last query.
	 */
	public long getActiveMemory()
	{
		return this.m_lActiveMemory;
	}
	
	/**
	 * Retreive the total inactive memory on the system. 
	 * 
	 * @return A long value representing the total inactive memory at the time of the last query.
	 */
	public long getInactiveMemory()
	{
		return this.m_lInactiveMemory;
	}
	
	public long getKernelStackMemory()
	{
		return this.m_lKernelStack;
	}

	/**
	 * Summarize the memory information of the device and return it as a string
	 * 
	 * @return A string with the memory summary information.
	 */
	public String summaryString()
	{
		
		String sSummary = "";
		
		sSummary = "Memory Information: \n" + this.m_sRawMemInfo;
	
		return sSummary;
		
	}
	
	/**
	 * Add an entry to the history list and make sure we don't exceed 
	 * the maximum count.
	 * 
	 * @param xEntry A history entry we want to add to the history
	 */
	public void addHistoryEntry(Object xEntry) throws NullPointerException
	{
		//* Make sure we have a non null entry
		if (xEntry == null)
			throw new NullPointerException();
		
		//* We don't.  Add it to the list
		this.m_xHistoryList.add(xEntry);
		
		//* Make sure we haven't exceeded our max history.  Remove any entries that exceed our max.
		int iSize = this.m_xHistoryList.size();
		for (int i = this.m_iMaxHistory; i < iSize; i++)
			this.m_xHistoryList.remove(i);
		
		return;
	}
	
	/**
	 * Retreive the specified entry from the history list.
	 * 
	 * @param iEntryIndex The index of the entry we want to retreive
	 */
	public Object getHistoryEntry(int iEntryIndex) throws IndexOutOfBoundsException
	{
		int			iSize = this.m_xHistoryList.size();
		
		//* make sure we have a valid index
		if (iEntryIndex < 0 || iEntryIndex >= iSize)
		{
			throw new IndexOutOfBoundsException();
		}
		
		//* We have a valid index.
		return this.m_xHistoryList.get(iEntryIndex);
	}
	
	/**
	 * Remove the history entry specified by the index.
	 * 
	 * @param iEntryIndex The index of the entry we want to remove.
	 */
	public void removeHistoryEntry(int iEntryIndex) throws IndexOutOfBoundsException
	{
		int			iSize = this.m_xHistoryList.size();
		
		//* make sure we have a valid index
		if (iEntryIndex < 0 || iEntryIndex >= iSize)
		{
			throw new IndexOutOfBoundsException();
		}
		
		//* we have a valid index
		this.m_xHistoryList.remove(iEntryIndex);
	}
	
	/**
	 * Get/Set the maximum number of history entries.
	 * 
	 * @param iMaxEntries The max number of entries we want to save
	 */
	public void setMaxHistoryEntries(int iMaxEntries)
	{
		this.m_iMaxHistory = iMaxEntries;
	}
	public int getMaxHistoryEntries()
	{
		return this.m_iMaxHistory;
	}
	
	/**
	 * Retrun the number of history entries currently in the list
	 */
	public int getHistorySize()
	{
		return this.m_xHistoryList.size();
	}
}
