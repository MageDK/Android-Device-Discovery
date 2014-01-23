/**
 * This class will detect all the services that are currently 
 * running on the device.  
 * 
 * @author Emil Diego
 */
package com.emildiego.devicediscovery;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;


/**
 * A utility class used to store the service information.
 *
 */
class RunningServiceEntry
{
	int				m_iId;
	int				m_iUidOwner;		//* The UID that owns this service.
	long			m_lActiveSince;
	int				m_iNumClients;
	String			m_sProcessRunsIn;	//* The package that the service runs in.
	ComponentName	m_xComponentName;	//* The service component.
}

/**
 * This class will detect all the currently running services on the device and return a list.
 *
 */
public class services extends discoveryBase {
		
		/**
		 * The activity manager that will be used to query the system for the running services.
		 */
		ActivityManager 					m_xActivityManager;
				
		/** 
		 * The list of services running at the time we query the device
		 */
		private List<RunningServiceEntry>	m_listRunningServices;
		
		/** 
		 * The package manager used to resolve the application names
		 */
		private PackageManager				m_xPackageManager;
		
	
	/**
	 * Default constructor
	 * 
	 * @param xContext The application context
	 * @param sLogTag The Tag to be used by the Android message logger
	 */
	public services(Context xContext, String sLogTag)
	{
		super(xContext, sLogTag);
		
		//* Create the activity manager
		this.m_xActivityManager  = (ActivityManager)this.m_xContext.getSystemService(Activity.ACTIVITY_SERVICE);
				
		//* Get an instance of the package manager so we can resolve names
		this.m_xPackageManager = this.m_xContext.getPackageManager();
		
		//* Initialize our ArrayList
		this.m_listRunningServices = new ArrayList<RunningServiceEntry>();
		
	}
	
	/**
	 * Query the device for all the currently running services.
	 * 
	 * @return True if the device was successfully queried, False if it didn't.
	 */
	@Override
	public boolean query() 
	{
		List<RunningServiceInfo>		xServiceList;
	
		try 
		{	
			//* Get a list of all the currently running services.
			xServiceList = this.m_xActivityManager.getRunningServices(Integer.MAX_VALUE);
			
			for (int i=0; i < xServiceList.size(); i++)
			{
				RunningServiceEntry		xTmpEntry = new RunningServiceEntry();
				
				//* Create the entry
				xTmpEntry.m_iId					= xServiceList.get(i).pid;
				xTmpEntry.m_iUidOwner			= xServiceList.get(i).uid;
				xTmpEntry.m_iNumClients			= xServiceList.get(i).clientCount;
				xTmpEntry.m_lActiveSince		= xServiceList.get(i).lastActivityTime;
				xTmpEntry.m_sProcessRunsIn		= xServiceList.get(i).process;
				xTmpEntry.m_xComponentName		= xServiceList.get(i).service;
				
				//* Add the entry to our list.
				this.m_listRunningServices.add(xTmpEntry);
			}
		}
		catch (SecurityException exp)
		{
			this.logError("Unable to retreive running process list: " + exp.getMessage());
			this.logStackTrace(exp.getStackTrace());
			return false;
		}
		catch (Exception exp)
		{
			this.logError("Unable to retreive running process list: " + exp.getMessage());
			this.logStackTrace(exp.getStackTrace());
			return false;
		}
		
		return true;
	}

	/**
	 * Summarize all the service information and return it in a string.
	 * 
	 * @param summaryString A string that contains all the summary information for the class
	 */
	@Override
	public String summaryString() {
		String sSummary = "";
		
		sSummary += "Running Services: \n";
	
		for (int i = 0; i < this.m_listRunningServices.size(); i++)
		{
			RunningServiceEntry		xTmpInfo = this.m_listRunningServices.get(i);
			sSummary += xTmpInfo.m_iId + " " + xTmpInfo.m_xComponentName.flattenToString() + " " + "\n";		
		}
		
		
		return sSummary;
	}

}
