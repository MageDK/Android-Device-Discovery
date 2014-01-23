/**
 * Used to query the device about battery information.
 * 
 * @author Emil Diego
 */
package com.emildiego.devicediscovery;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

/**
 * The main class used to query the device for battery information.
 *
 */
public class battery extends discoveryBase 
{
	/**
	 * Batter state.  Is charging.
	 */
	private boolean					m_bIsCharging;
	/**
	 * Battery is charging via USB cable.
	 */
	private boolean 				m_bChargingViaUSB;
	/**
	 * Battery is charging via AC Power.
	 */
	private boolean					m_bChargingViaACPower;
	
	/**
	 * The battery level.
	 */
	private int						m_iBatteryLevel;
	/**
	 * The scale used to determine empty -> full charged battery
	 */
	private int 					m_iBatteryScale;
	/**
	 * The percentage of the batter that is charged.
	 */
	private float					m_fBatteryPercent;

	/**
	 * Default constructor
	 * 
	 * @param xContext The Android contrext for the device
	 * @param sLogTag The Tag to be used by the Android message logger 
	 */
	public battery(Context xContext, String sLogTag) {
		super(xContext, sLogTag);
		
		this.m_bIsCharging			= false;
		this.m_bChargingViaUSB		= false;
		this.m_bChargingViaACPower	= false;
		
	}

	/**
	 * Checks to see if the battery is currently being charged.
	 * @return True is the device is charging.
	 */
	public boolean isCharging()
	{
		return this.m_bIsCharging;
	}
	
	/**
	 * Check to see if the device is charging from a USB connection.
	 * @return True if the device is currently being charged via a USB connection.
	 */
	public boolean isChargingViaUSB()
	{
		return this.m_bChargingViaUSB;
	}
	
	/**
	 * Checks to see if the device is currently being charged from a AC power supply.
	 * @return True is the device is charging from a AC power supply.
	 */
	public boolean isChargingViaACPower()
	{
		return this.m_bChargingViaACPower;
	}
	
	/**
	 * Get the percent of the batter that is currently charged.
	 * @return The percentage of the battery that is charged.
	 */
	public float percentCharged()
	{
		return this.m_fBatteryPercent;
	}
	
	/**
	 * Query the device for information about the battery in the device.
	 * 
	 * @return True if the device was queried successfully, false if it wasn't.
	 */
	@Override
	public boolean query() 
	{
		//* determine the state of the battery
		IntentFilter xIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = this.m_xContext.registerReceiver(null, xIntentFilter);
		
		//* Get the status of the battery
		int iStatus = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		this.m_bIsCharging = iStatus == BatteryManager.BATTERY_STATUS_CHARGING ||
		                     iStatus == BatteryManager.BATTERY_STATUS_FULL;
		
		//* Find out how we are charging the battery
		// How are we charging?
		int iChargePlug	= batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
		this.m_bChargingViaUSB = iChargePlug == BatteryManager.BATTERY_PLUGGED_USB;
		this.m_bChargingViaACPower = iChargePlug == BatteryManager.BATTERY_PLUGGED_AC;
		
		//* Get the battery level
		this.m_iBatteryLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		this.m_iBatteryScale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		this.m_fBatteryPercent = (m_iBatteryLevel / (float)m_iBatteryScale) * 100;
		
		return true;
	}

	/**
	 * Summarize the battery information into a string.
	 * @return A string contaiing all the battery information summarized.
	 */
	@Override
	public String summaryString() {
		String sSummary;
		
		sSummary = "Getting Battery Information: \n";
		sSummary += "Is Charging: " + this.m_bIsCharging + "\n";
		if (this.m_bChargingViaACPower)
			sSummary += "Charging via AC Power \n";
		else if (this.m_bChargingViaUSB)
			sSummary += "Charging via USB Power \n";
		else
			sSummary += "Charging via unknown \n";
	
		//* Battery charged
		sSummary += "The battery is " + this.m_fBatteryPercent + " charged.";
		
		return sSummary;
	}

}
