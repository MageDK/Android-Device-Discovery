/**
 * storage
 * 
 * This class will query the device for information about
 * internal and external storage available on the device.
  for external storage (SD CARD) also checks if its writable.
  
  @author Emil Diego
 */
package com.emildiego.devicediscovery;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

/**
 * The main storage class used to query the device about the sorage usage on the device.
 *
 */
public class storage extends discoveryBase {
	//* The storage directories
	String				m_sInternalStorageDirectory;
	String				m_sExternalStorageDirectory;
	
	//* Status of external storage
	private boolean		m_bExternalStorageAvailable;
	private boolean		m_bExternalStorageWriteable;
	
	StatFs				m_xInternalStorage;
	StatFs				m_xExternalStorage;

	/**
	 * Default constructor
	 * 
	 * @param xContext The application context
	 * @param sLogTag The Tag to be used by the Android message logger
	 */
	public storage(Context xContext, String sLogTag)
	{
		super(xContext, sLogTag);
		
		//* Initilize the class members
		this.m_sExternalStorageDirectory = Environment.getExternalStorageDirectory().getPath();
		this.m_sInternalStorageDirectory = Environment.getDataDirectory().getPath();
		
		this.m_bExternalStorageAvailable = false;
		this.m_bExternalStorageWriteable = false;
		
		this.m_xInternalStorage	= null;
		this.m_xExternalStorage = null;
	}

	/**
	 * Query the device for the storage information
	 * 
	 * @return True if the query was successfull, false if it wasn't.
	 */
	public boolean query()
	{
		String		sState;
		
		try 
		{

			//*
			//* INTERNAL STORAGE
			//*
			//* Get the information on the internal storage
			this.m_xInternalStorage = new StatFs(this.m_sInternalStorageDirectory);
			
			
			//*
			//* EXTERNAL STORAGE
			//*
			//* Get the state of the external storage
			sState = Environment.getExternalStorageState();
			if (sState == null)
			{
				this.logDebug("Unable to get external storage state.");
				return false;
			}
			else
			{
				if ( Environment.MEDIA_MOUNTED.equals(sState) )
				{
					//* the storage is available and writeable
					this.m_bExternalStorageAvailable = true;
					this.m_bExternalStorageWriteable = true;
				}
				else if ( Environment.MEDIA_MOUNTED_READ_ONLY.equals(sState) )
				{
					//* the storage is available, but not writeable
					this.m_bExternalStorageAvailable = true;
					this.m_bExternalStorageWriteable = false;
				}
				else 
				{
					//* Something else is wrong.  Can't really determine what it is.
					this.m_bExternalStorageAvailable = false;
					this.m_bExternalStorageWriteable = false;
				}
			}
			
			//* if the external storage is available then let's get some additional information
			if (this.isExternalStorageAvailable())
			{
				this.m_xExternalStorage = new StatFs(this.m_sExternalStorageDirectory);
			}
			
			return true;
				
		}
		catch (Exception exp)
		{
			this.logError("An Exception occured while determining the external storage state.");
			return false;
		}
	}
	
	/**
	 * Checks to see if there is external storage available to the device (SD Card).
	 * @return True if there is an SD card or some other external storage.
	 */
	public boolean isExternalStorageAvailable()
	{
		return this.m_bExternalStorageAvailable;
	}
	
	/**
	 * Check to see if the external storage is writable.  In some cases it may only be read only.
	 * @return True if the external storage is writable.
	 */
	public boolean isExternalStorageWriteable()
	{
		return this.m_bExternalStorageWriteable;
	}
	
	/**
	 * Check to see the amount of available internal storage.
	 * @return a Long number representing the amount of availa
	 */
	public long getAvailableInternalStorage()
	{
		long lAvaiableBlocks = 0;
		long lBlockSize = 0;
		
		if (this.m_xInternalStorage != null)
		{
			lAvaiableBlocks = (long)this.m_xInternalStorage.getAvailableBlocks();
			lBlockSize = (long)this.m_xInternalStorage.getBlockSize();
			
			return lAvaiableBlocks * lBlockSize;
			
		}
		
		return 0;
	}
	
	/**
	 * Gets the total amount of internal storage avaiable to the device.
	 * @return A Long number representing the total amount of internal storage.
	 */
	public long getTotalInternalStorage()
	{
		long lTotalBlocks		= 0;
		long lBlockSize			= 0;
		
		if (this.m_xInternalStorage != null)
		{
			lTotalBlocks		= (long)this.m_xInternalStorage.getBlockCount();
			lBlockSize 			= (long)this.m_xInternalStorage.getBlockSize();
		}
		return lTotalBlocks * lBlockSize;
		
	}
	
	/**
	 * Get the amount of available external storage the device has.
	 * @return A Long representing the amount of external storage is available on the device.
	 */
	public long getAvailableExternalStorage()
	{
		long lAvaiableBlocks = 0;
		long lBlockSize = 0;
		
		if (this.m_xExternalStorage != null)
		{
			lAvaiableBlocks = (long)this.m_xExternalStorage.getAvailableBlocks();
			lBlockSize = (long)this.m_xExternalStorage.getBlockSize();
			
			return lAvaiableBlocks * lBlockSize;
			
		}
		return 0;
	}
	
	/**
	 * Return the amount of total external storage is on the device.
	 * 
	 * @return A Long representing the total amount of storage on the device.
	 */
	public long getTotalExternalStorage()
	{
		long lTotalBlocks		= 0;
		long lBlockSize			= 0;
		
		if (this.m_xExternalStorage != null)
		{
			lTotalBlocks		= (long)this.m_xExternalStorage.getBlockCount();
			lBlockSize 			= (long)this.m_xExternalStorage.getBlockSize();
		}
		return lTotalBlocks * lBlockSize;
		
	}
	

	/**
	 * Summarize all the storage information into a string and return it.
	 * 
	 * @return A string wil the summary information in it.
	 */
	public String summaryString()
	{
		String		sSummary = "";
		
		sSummary += "Internal Storage Information: \n";
		sSummary += "Storage Location: " + this.m_sInternalStorageDirectory + "\n";
		sSummary += "Available Storage: " + this.convertStorage(this.getAvailableInternalStorage(), SIZE_NONE) + "\n";
		sSummary += "\n";
		sSummary += "External Storage Information: \n";
		sSummary += "Storage Present: " + this.isExternalStorageAvailable() + "\n";
		sSummary += "Storage Writeable: " + this.isExternalStorageWriteable() + "\n";
		sSummary += "Storage Location: " + this.m_sExternalStorageDirectory + "\n";
		sSummary += "Available Storage: " + this.convertStorage(this.getAvailableExternalStorage(), SIZE_NONE) + "\n";
		
		return sSummary;
	}
}
