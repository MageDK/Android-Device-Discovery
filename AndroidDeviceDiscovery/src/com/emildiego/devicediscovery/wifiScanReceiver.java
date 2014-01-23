package com.emildiego.devicediscovery;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;




public class wifiScanReceiver extends BroadcastReceiver 
{
	private network			m_xNetworkInfo;
	
	public wifiScanReceiver(network xNetworkInfo) 
	{
		super();
		
		m_xNetworkInfo	= xNetworkInfo;
	}

	@Override
	public void onReceive(Context c, Intent intent) 
	{
		ScanResult		xTmpEntry;
		
		List<ScanResult> xResults = m_xNetworkInfo.getScanResults();
		
		//* Find the SSID we are connected to
		String sConnectedSSID = m_xNetworkInfo.getWifiSSID();
		
		//* Get the connection information for the matching SSID.
		for (int i=0; i < xResults.size(); i++)
		{
			//* Get the entry
			xTmpEntry = xResults.get(i);
			
			//* Match the SSID
			if (xTmpEntry.SSID.compareTo(sConnectedSSID) == 0)
			{
				//* we have a match.  Let's set the channel and signal level
				this.m_xNetworkInfo.setSignalLevel( xTmpEntry.level );
				this.m_xNetworkInfo.setChannelFrequency( xTmpEntry.frequency );
			}
			
		}
	}
}
