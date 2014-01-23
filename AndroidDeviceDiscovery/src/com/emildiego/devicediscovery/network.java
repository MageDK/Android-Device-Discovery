/**
 * Used to query the device for all the networking information supported by this device.
 * 
 * @author Emil Diego
 */
package com.emildiego.devicediscovery;

import java.util.List;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.net.wifi.ScanResult;

/**
 * The main class for querying our network information
 * 
 */
public class network extends discoveryBase {

	private TelephonyManager			m_xTelMananger;
	private ConnectivityManager			m_xCM;
	private WifiManager 				m_xWifiMaanger;
	
	private wifiScanReceiver			m_xWifiScanReceiver;
	

	
	//* Network types
	public static final int				NETWORK_WIFI	= 1;
	public static final int				NETWORK_2G		= 2;
	public static final int				NETWORK_3G		= 3;
	public static final int				NETWORK_4G		= 4;
	
	//* Get the phone type
	private int							m_iPhoneType;
	
	
	//* network information regarding each connection type
	private NetworkInfo					m_xWiFiInfo;
	private NetworkInfo					m_xMobile_2G_3G;
	private NetworkInfo					m_xMobile_4G;
	
	//* Flags for some of the connection types
	private boolean						m_bWifi;
	private boolean						m_bMobile_2G;
	private boolean						m_bMobile_3G;
	private boolean 					m_bMobile_4G;

	//* Network  Information
	private	String						m_sWiFiIpAddress;
	private String						m_sWiFiMacAddress;
	private String						m_sWifiSSID;
	
	private int							m_iWifiChannelFreq;
	private int							m_iWifiLevelIndBM;
	
	private int							m_iWifiLinkSpeed;

	
	//* Cellular Info
	private String						m_sCarrierName;
	private String						m_sIpAddress;
	private String						m_sCellSignalStrength;
	
	//* Mobile Country Code
	private int							m_iMcc;
	//* mobile network code
	private int							m_iMnc;
	
	
	/**
	 * Default Constructor 
	 * @param xContext The Android contrext for the device
	 * @param sLogTag The Tag to be used by the Android message logger 
	 */
	public network(Context xContext, String sLogTag) 
	{
		super(xContext, sLogTag);
		
		this.m_xCM 				= null;
		
	}
	
	/**
	 * Determine if the specified network connection is available.
	 * @param iNetworkType A static int representing the network type we want to check (NETWORK_WIFI, NETWORK_2G, NETWORK_3G, NETWORK_4G).
	 * @return Returns true id the network is available, otherwise false.
	 */
	public boolean isAvailalbe(int iNetworkType)
	{
		if (iNetworkType == NETWORK_WIFI)
			return this.m_bWifi;
		else if (iNetworkType == NETWORK_2G)
			return this.m_bMobile_2G;
		else if (iNetworkType == NETWORK_3G)
			return this.m_bMobile_3G;
		else if (iNetworkType == NETWORK_4G)
			return this.m_bMobile_4G;
		
			
		return false;
	}
	

	/**
	 * Set the channel frequency used by the wifi connection
	 * @param iChannelFreq The channel frequency
	 */
	public void setChannelFrequency(int iChannelFreq)
	{
		this.m_iWifiChannelFreq = iChannelFreq;
	}
	
	/**
	 * Get the channel frequency used by the wifi connection.
	 * @return An integer representing the wifi channel
	 */
	public int getChannelFrequency()
	{
		return this.m_iWifiChannelFreq;
	}
	
	/**
	 * Set the signal strength of the wifi connection
	 * @param iSignalLevel The signal strength of the wifi signal in dbm.
	 */
	public void setSignalLevel(int iSignalLevel)
	{
		this.m_iWifiLevelIndBM = iSignalLevel;
	}
	
	/**
	 * Get the signal strength of the wifi connection
	 * @return The signal strength in dbm.
	 */
	public int getSignalLevel()
	{
		return this.m_iWifiLevelIndBM;
	}
	
	/**
	 * Get the IP address of the specified network connection.
	 * @param iNetworkType A static int representing the network type (NETWORK_WIFI, NETWORK_2G, NETWORK_3G, NETWORK_4G).
	 * @return Return the IP address as a string.
	 */
	public String getIpAddress(int iNetworkType)
	{
		if (iNetworkType == NETWORK_WIFI)
			return this.m_sWiFiIpAddress;
	
		return "";
	}
	
	/*
	 * Get the MAC address of the specified network
	 */
	/**
	 * Get the MAC address of the specified network type.
	 * @param iNetworkType A static int representing the network type (NETWORK_WIFI, NETWORK_2G, NETWORK_3G, NETWORK_4G).
	 * @return The MAC address of the network interface as a string.
	 */
	public String getMacAddress(int iNetworkType)
	{
		if (iNetworkType == NETWORK_WIFI)
			return this.m_sWiFiMacAddress;
		
		return "";
	}
	

	/**
	 * Get the SSID of the Wifi network
	 * @return The SSID of the wifi connection as a string.
	 */
	public String getWifiSSID()
	{
		return this.m_sWifiSSID;
	}
	
	/**
	 * Get the line speed of the Wifi connection
 	 * @return The link speed of the wifi network as an integer.
	 */
	public int getWifiLinkSpeed()
	{
		return this.m_iWifiLinkSpeed;
	}
	
	/**
	 * Get the units for the link Speed
	 * @return The units used to measure the link speed (Mbps, Kbps, etc) as a string.
	 */
	public String getWifiLinkSpeedUnits()
	{
		return WifiInfo.LINK_SPEED_UNITS;
	}
	
	/**
	 * Get the Mobile Country code
	 * @return The Mobile Country code (mcc).
	 */
	public int getMobileCountryCode()
	{
		return this.m_iMcc;
	}
	
	/** 
	 * Get the mobile network code
	 * @return The Mobile Network Code (mnc).
	 */
	public int getMobileNetworkCode()
	{
		return this.m_iMnc;
	}
	
	/*
	 * Return the integer of the phone type (TelephonyManager.getPhoneType()
	 */
	/**
	 * Get the phone type
	 * @return The phone type
	 */
	public int getPhoneType()
	{
		return this.m_iPhoneType;
	}
	
	/**
	 * Return a string representation of the phone type
	 * @return The phone type as a string
	 */
	public String getPhoneTypeName()
	{
		String sName = "";
		
		//* Load the string array resource
		String[] sPhoneTypes = this.m_xContext.getResources().getStringArray(R.array.telephony_phone_type);
		
		if (sPhoneTypes != null)
		{
			//* the resource was successfull
			sName = sPhoneTypes[this.m_iPhoneType];
		}
		
		return sName;
	}
	
	/**
	 * Make sure we clean up any code here before we shutdown the application.
	 * For instance, the wifiScanner that was registered needs to be unregistered
	 * or else we will leak memory when the app closes
	 */
	public void close()
	{
		if (m_xWifiScanReceiver != null)
		{
			//* We will assume that it was registerd.
			try {
				this.m_xContext.unregisterReceiver(m_xWifiScanReceiver);
			}
			catch (Exception exp)
			{
				return;
			}
			
		}
			
	}
	

	/**
	 * Query the device for all the network information.
	 * 
	 * @return True if the device was queried successfully, otherwise false.
	 */
	@Override
	public boolean query() {
		
		//* Query for the different network connection types
		m_xCM = (ConnectivityManager)this.m_xContext.getSystemService(Context.CONNECTIVITY_SERVICE);

		//* Get the telephony manager
		m_xTelMananger = (TelephonyManager)this.m_xContext.getSystemService(Context.TELEPHONY_SERVICE);
		
		//* Get the phone type
		this.m_iPhoneType = m_xTelMananger.getPhoneType();
		
		//* Get the Wifi connection
		m_xWiFiInfo = m_xCM.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        // Make sure the network is available
        if(m_xWiFiInfo != null && m_xWiFiInfo.isAvailable() && m_xWiFiInfo.isConnectedOrConnecting()) 
        {
        	//* Set the flag
        	m_bWifi = true;
        	
        	//* Get additional information about the wifi
        	m_xWifiMaanger = (WifiManager)this.m_xContext.getSystemService(Context.WIFI_SERVICE);
        	WifiInfo xWifiInfo = m_xWifiMaanger.getConnectionInfo();
        	
        	int ipAddress = xWifiInfo.getIpAddress();
        	
        	//* Get the ip address
        	m_sWiFiIpAddress = String.format("%d.%d.%d.%d", 
        			(ipAddress & 0xff),
        			(ipAddress >> 8 & 0xff),
        			(ipAddress >> 16 & 0xff),
        			(ipAddress >> 24 & 0xff)
        			);
        	
        	//* get the MAC address
        	this.m_sWiFiMacAddress = xWifiInfo.getMacAddress();
        	
        	//* retreive the SSID of the current access point connected to
        	this.m_sWifiSSID = xWifiInfo.getSSID();
        	
        	//* get the wifi link speed
        	m_iWifiLinkSpeed = xWifiInfo.getLinkSpeed();
        	
        	//* Register Broadcast Receiver
    		if (m_xWifiScanReceiver == null)
    			m_xWifiScanReceiver = new wifiScanReceiver(this);
    		
    		//* Register the receiver to receive the scan results
    		this.m_xContext.registerReceiver(m_xWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        	
    		//* Star the wifi scan
    		this.m_xWifiMaanger.startScan();
        }
              
        //* Get the 2G/3G Mobile information
       this.m_xMobile_2G_3G = this.m_xCM.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	   
       //* determine if its 2G or 3G
       if(m_xMobile_2G_3G != null &&
               (m_xMobile_2G_3G.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS ||
                m_xMobile_2G_3G.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE)) 
       {
           //* 2G
    	   this.m_bMobile_2G = true;
    	   
    	   //* Get the network operator name
    	   this.m_sCarrierName = m_xTelMananger.getNetworkOperatorName();
    	   
    	   //* Get the MMC and <NC
    	   this.parseMccMnc();
       }
       else {
           //* 3G
    	   this.m_bMobile_3G = true;
    	   
    	   //* Get the network operator name
    	   this.m_sCarrierName = m_xTelMananger.getNetworkOperatorName();
    	   
    	   //* Get the MMC and <NC
    	   this.parseMccMnc();
       }
       
       /** Check the connection **/
       this.m_xMobile_4G = this.m_xCM.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);

       // Make sure the network is available
       if(m_xMobile_4G != null && m_xMobile_4G.isAvailable() && m_xMobile_4G.isConnectedOrConnecting())
       {
           this.m_bMobile_4G = true;
           
           //* Get the network operator name
    	   this.m_sCarrierName = m_xTelMananger.getNetworkOperatorName();
    	 
    	   //* Get the MMC and <NC
    	   this.parseMccMnc();
       }
        
       
       return true;
        
	}
	

	/**
	 * Retreive the WiFi scan results.  Should not be called directly.
	 * @return The WiFi manager scan results.
	 */
	public List<ScanResult> getScanResults()
	{
		return this.m_xWifiMaanger.getScanResults();
	}
	
	/**
	 * Parse out the Mobile Coutnry Code (MCC) and the Mobile Network Code (MNC) from the phone infromation.
	 */
	private void parseMccMnc()
	{
		String 	sNetworkOperator = m_xTelMananger.getNetworkOperator();
		
		try 
		{
			//* parse out the MCC and MNC
			this.m_iMcc = Integer.parseInt(m_xTelMananger.getNetworkOperator().substring(0, 3));
			this.m_iMnc = Integer.parseInt(m_xTelMananger.getNetworkOperator().substring(3));
		}
		catch (Exception exp)
		{
			this.m_iMcc = 0;
			this.m_iMnc = 0;
		}
	}
	

	/**
	 * Get the mobile phone network operator.
	 * 
	 * @ret A string representing the mobile network operator name.
	 */
	public String getNetworkOperatorName()
	{
		return this.m_xTelMananger.getNetworkOperatorName();
	}
	
	/*
	 * Display a summary of the information collected by the class
	 * @see com.emildiego.systemProfiler.discoveryBase#summaryString()
	 */
	/**
	 * create a summary of all the network information and return it as a string.
	 * 
	 * @ret A string summary of all the network information.
	 */
	@Override
	public String summaryString() {
		String sSummary = "";
		
		sSummary = "Network Information\n";
		sSummary += "Wifi Available: " + this.m_bWifi + "\n";
		sSummary += "ipAddress: " + this.m_sWiFiIpAddress + "\n";
		sSummary += "MAC Address: " + this.m_sWiFiMacAddress + "\n";
		sSummary += "Access Point SSID: " + this.m_sWifiSSID + "\n";
		sSummary += "\n";
		
		sSummary += "2G Cellular Available: " + this.m_bMobile_2G + "\n";
		if (m_bMobile_2G)
		{
			sSummary += "Carrier Name: " + m_sCarrierName + "\n";
			sSummary += "Mobile Country Code (MCC): " + this.m_iMcc + "\n";
			sSummary += "Mobile Netowrk Code (MNC): " + this.m_iMnc + "\n";
					
		}
		sSummary += "\n";

		sSummary += "3G Cellular Available: " + this.m_bMobile_3G + "\n";
		if (m_bMobile_3G)
		{
			sSummary += "Carrier Name: " + m_sCarrierName + "\n";
			sSummary += "Mobile Country Code (MCC): " + this.m_iMcc + "\n";
			sSummary += "Mobile Netowrk Code (MNC): " + this.m_iMnc + "\n";
		}
		sSummary += "\n";
		
		sSummary += "4G Cellular Available: " + this.m_bMobile_4G + "\n";
		if (m_bMobile_4G)
		{
			sSummary += "Carrier Name: " + m_sCarrierName + "\n";
			sSummary += "Mobile Country Code (MCC): " + this.m_iMcc + "\n";
			sSummary += "Mobile Netowrk Code (MNC): " + this.m_iMnc + "\n";
		}
		sSummary += "\n";
		
		return sSummary;
	}

}
