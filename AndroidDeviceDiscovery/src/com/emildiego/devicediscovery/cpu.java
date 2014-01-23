/**
 *  This class will detect the CPU utilization of the device 
 * and provide us with some basic statistics.  THe CPU information
 * is gathered by running the TOP command and parsing out the 
 * relevant information. 
 *
 * Since we might want to see the cpu information in real time the
 * class allows the option to set a sample rate and can be used to 
 * determine how often the information is refreshed.
 * 
 * @author Emil Diego
 */
package com.emildiego.devicediscovery;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

/**
 * The main class used to query the device about the CPU information.
 *
 */
public class cpu extends discoveryBase {

	/**
	 * The results from executing the top command are stored here to be parsed out.
	 */
	private String		m_sTopResults;
	
	/**
	 * Store the user CPU usage
	 */
	private	float			m_fUserCpuUsage;
	/**
	 * Store the system CPU usage
	 */
	private float			m_fSystemCpuUsage;
	/**
	 * Store the idle CPU
	 */
	private float			m_fIdleCpuUsage;
	

	/**
	 * A regular expression used to parse out the CPU information from the top command output.
	 */
	static final String USER_CPU_REGEX = "((?i)(use?r\\s+[0-9]{1,2}(\\.[0-9]{1,2})?\\%))|([0-9]{1,2}\\.[0-9]{1,2}\\%\\s+(?i)(use?r))";

	/**
	 * A regular expression used to parse out the system cpu usage from the top command output.
	 * Will parse out a variety of different formats
	 * 19.3% sys
	 * System 21%
	 * 33.3% sys 
	 */
	static final String SYSTEM_CPU_REGEX = "((?i)(sys(tem)?\\s+[0-9]{1,2}(\\.[0-9]{1,2})?\\%))|([0-9]{1,2}\\.[0-9]{1,2}\\%\\s+(?i)(sys(tem)?))";

	
	/*
	 * Default constructor
	 * 
	 * @param xContext
	 * @param xLogger
	 */
	/**
	 * Default Constructor
	 * 
	 * @param xContext The device context
	 * @param sLogTag 
	 */
	public cpu(Context xContext, String sLogTag)
	{
		super(xContext, sLogTag);
	
		//* initilize the members
		this.m_sTopResults			= null;
		this.m_fUserCpuUsage		= 0F;
		this.m_fSystemCpuUsage		= 0F;
		this.m_fIdleCpuUsage		= 0F;
	}
	
	/*
	 * Retrieve the information from the TOP program that contains our CPU
	 * utilization information.
	 */
	/**
	 * Retreive the information from the top command output that contains our CPU utilization info.
	 */
	private void getCPUInfo()
	{
		BufferedReader 		ifp = null;
		
		this.logDebug("getCPUInfo()");
		
		//* empty the raw results buffer
		this.m_sTopResults = "";
		
		try 
		{
			//* execute the top command 
			Process process = null;
			process = Runtime.getRuntime().exec("top -n 1 -d 1");
			//* we only want to retrieve the top few lines
			
			//* read the output from the command
			String sLine = new String();
			ifp = new BufferedReader(new InputStreamReader(process.getInputStream()));

			//* Read all the available output and store it in the class member
			while ( (sLine = ifp.readLine()) != null)
			{
            	this.m_sTopResults += sLine + "\n";
			}
		} catch (IOException exp) {
			this.logError("There was an error while trying to execute the TOP command.");
			this.logStackTrace(exp.getStackTrace());
		
		}
		finally
		{
			//* we need to close the stream once everything is done.
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
	 * This function parses out the data from the results of the top command and populates all of the member
	 * variables.  We will use regular expressions to find the pieces of data we need.
	 */
	public void parseTopResults()
	{
		String	sUserCpuUsage		= null;
		String 	sSystemCpuUsage		= null;
		
		Pattern		xRegexSearchPattern 	= null;
		Matcher		xSearch  				= null;
		
		//* Check to make sure we have some data to parse
		if (this.m_sTopResults == null)
		{
			this.logMessage("No top results available.");
			return;
		}
		
		//* search for the user CPU information
		xRegexSearchPattern = Pattern.compile( cpu.USER_CPU_REGEX );
		xSearch = xRegexSearchPattern.matcher(this.m_sTopResults);
		//* check to see if we found a match
		if (xSearch.find())
		{
			sUserCpuUsage = xSearch.group(0);
			this.logDebug("Found User CPU USage: " + sUserCpuUsage);
			
			//* we want to remove the non numeric characters in the string
			try {
				this.m_fUserCpuUsage = Float.parseFloat( sUserCpuUsage.replaceAll( "[^\\d.]", "" ).trim() );
			}
			catch (NumberFormatException exp)
			{
				this.m_fUserCpuUsage = 0.0F;
				this.logError("An error occured while trying to parse the user cpu usage.");
				this.logStackTrace(exp.getStackTrace());
			}
		}
		
		//* search for the system CPU information
		xRegexSearchPattern = Pattern.compile( cpu.SYSTEM_CPU_REGEX );
		xSearch = xRegexSearchPattern.matcher(this.m_sTopResults);
		//* check to see if we found a match
		if (xSearch.find())
		{
			sSystemCpuUsage = xSearch.group(0);
			this.logDebug("Found System CPU USage: " + sSystemCpuUsage);
			
			//* we want to remove the non numeric characters in the string
			try {
				this.m_fSystemCpuUsage = Float.parseFloat( sSystemCpuUsage.replaceAll( "[^\\d.]", "" ).trim() );
			}
			catch (NumberFormatException exp)
			{
				this.m_fUserCpuUsage = 0.0F;
				this.logError("An error occured while trying to parse the system cpu usage.");
				this.logStackTrace(exp.getStackTrace());
			}
			
		}
		
		//* now we compute the idle CPU usage based on the other two
		this.m_fIdleCpuUsage = 100.0F - this.m_fSystemCpuUsage - this.m_fUserCpuUsage;
		
		
	}
	
	/**
	 * Query the device for the CPU usage infromation.
	 */
	@Override
	public boolean query() 
	{
		//* Execue the TOP command and get the CPU information from 
		//* the results.
		this.getCPUInfo();
		
		//* now parse out the CPU information from the string
		this.parseTopResults();
		
		return true;
	}

	/**
	 * Summarize the CPU information into a string and return it
	 * 
	 * @return A String with the CPU summary.
	 */
	@Override
	public String summaryString() {
		String sSummary = "";
		
		sSummary += "CPU Information: \n";
		sSummary += "User CPU utilized: " + this.m_fUserCpuUsage + "%\n";
		sSummary += "System CPU utilized: " + this.m_fSystemCpuUsage + "%\n";
		sSummary += "Idle CPU: " + this.m_fIdleCpuUsage + "%\n";
		
		return sSummary;
	}
	
	/**
	 * Return the percent of the CPU utilized by the system processes.
	 * @return The percentage of CPU utilized by system processes.
	 */
	public float getSystemUsage()
	{
		return this.m_fSystemCpuUsage;
	}
	

	/**
	 * Return the percent of the CPU that is being utilized by user processes.
	 * @return The percentage of CPU utilized by user processes.
	 */
	public float getUserUsage()
	{
		return this.m_fUserCpuUsage;
	}
	
	/**
	 * Return the percentage of the CPU that is idle
	 * @return The percentage of idle CPU
	 */
	public float getIdle()
	{
		return this.m_fIdleCpuUsage;
	}

}
