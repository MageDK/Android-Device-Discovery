/**
 * This class will detect all the processes that are currently 
 * running on the device.  
 * 
 * @author Emil Diego
 */
package com.emildiego.devicediscovery;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Debug.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;


/**
 * A utility class used to store the Process information we want to display.
 */
class RunningProcessEntry
{
	int				m_iId;
	String			m_sPackageName;
	String 			m_sApplicationName;
	String			m_sStateDescription;
	Drawable		m_dIcon;
	int				m_iImportance;
	int				m_iMemoryUsage;
}

/**
 * The class used to query the device about its processes.
 *
 */
public class processes extends discoveryBase {
	
	/**
	 * The activity manager that will be used to query the system for the running processes.
	 */
	ActivityManager 					m_xActivityManager;
			
	/**
	 * The list of processes that are currently running in the system
	 */
	private List<RunningProcessEntry>	m_listRunningProcesses;
	
	/**
	 * The package manager used to resolve the application names
	 */
	private PackageManager				m_xPackageManager;
	
	/**
	 * The memory information for all the processes in the list.
	 */
	private MemoryInfo[]				m_xProcessMemoryInfo;

	/**
	 * Default constructor
	 * 
	 * @param xContext The application context
	 * @param sLogTag The Tag to be used by the Android message logger 
	 */
	public processes(Context xContext, String sLogTag)
	{
		super(xContext, sLogTag);
		
		//* initlize the members
		//* Create the activity manager
		this.m_xActivityManager  = (ActivityManager)this.m_xContext.getSystemService(Activity.ACTIVITY_SERVICE);
		
		//* Get an instance of the package manager so we can resolve names
		this.m_xPackageManager = this.m_xContext.getPackageManager();
		
		this.m_listRunningProcesses = new ArrayList<RunningProcessEntry>();
	}
	
	/**
	 * Query the device for the inforation about the running processes.
	 * 
	 * @return True if the device was successfully queried, false if it wasn't.
	 */
	@Override
	public boolean query() {
		List<RunningAppProcessInfo>	xRunningTasks;
		int[]						iPidList = null;
		int							iPidListSize = 0;
		
		try 
		{	
			//* Get our currently running tasks
			xRunningTasks = this.m_xActivityManager.getRunningAppProcesses();
			
			//* allocate our list of process id's so we can use this to get the memory information 
			//* for each process
			iPidListSize = xRunningTasks.size();
			if (iPidListSize > 0)
				iPidList = new int[iPidListSize];
			
			//* Now we need to get all the info and create out list
			for (int i=0; i < xRunningTasks.size(); i++)
			{
				RunningProcessEntry 	xTmpEntry = new RunningProcessEntry();
				
				//* setup the varaibles;
				xTmpEntry.m_iId 				= xRunningTasks.get(i).pid;
				xTmpEntry.m_sPackageName		= xRunningTasks.get(i).processName;
				xTmpEntry.m_iImportance			= xRunningTasks.get(i).importance;
				
				//* copy the pid to the iPidList array so we can get the memory info
				iPidList[i] = xTmpEntry.m_iId;
				
				try
				{
					xTmpEntry.m_sApplicationName	= (String)this.m_xPackageManager.getApplicationLabel(this.m_xPackageManager.getApplicationInfo(xTmpEntry.m_sPackageName,PackageManager.GET_META_DATA));
					xTmpEntry.m_dIcon				= this.m_xPackageManager.getApplicationIcon(xTmpEntry.m_sPackageName);
				}
				catch (NameNotFoundException exp)
				{
					this.logMessage("Unable to retreive additional information for package: " + xTmpEntry.m_sPackageName + " | " + exp.getMessage());
				}
				
				//* now that we have all the info lets add it to the list
				this.m_listRunningProcesses.add(xTmpEntry);
			}
			
			//* No we want to get the memory information for all the processes
			try
			{
				this.m_xProcessMemoryInfo = this.m_xActivityManager.getProcessMemoryInfo(iPidList);

			}
			catch (Exception exp)
			{
				this.m_xProcessMemoryInfo = null;
			}
		}
		catch (SecurityException exp)
		{
			this.logError("Unable to retreive running process list: " + exp.getMessage());
			this.logStackTrace(exp.getStackTrace());
			return false;
		}
		
		return false;
	}
	
	/**
	 * Summarize all the process infromation and return it as a string.
	 * 
	 * @return A string with the summary information for all the processes.
	 */
	@Override
	public String summaryString() {
		String sSummary = "";
		
		sSummary += "Running Processes: \n";
	
		for (int i = 0; i < this.m_listRunningProcesses.size(); i++)
		{
			RunningProcessEntry		xTmpInfo = this.m_listRunningProcesses.get(i);
			sSummary += xTmpInfo.m_iId + " " + xTmpInfo.m_sPackageName + " " + xTmpInfo.m_sApplicationName + "\n";		
		}
		
		
		return sSummary;
	}
	
	/**
	 * Return a list of RunningProcessEntries.
	 * @return A List<RunningProcessEntry> for all the detected processes.
	 */
	List<RunningProcessEntry> getList()
	{
		return this.m_listRunningProcesses;
	}

}
