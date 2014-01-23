/**
 * display
 * 
 * This class will query the device for information about
 * the display.
 *
 * @author Emil Diego
 */
package com.emildiego.devicediscovery;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * The main class used to query the device abou the display.
 *
 */
public class display extends discoveryBase 
{
	/** 
	 * The information about the display
	 */
	private DisplayMetrics				m_xDisplayMetrics;
	
	/**
	 *  The windows manager for the current activity.  We need this to get 
	 * a reference to the current display so we can query it for the metrics.
	 */
	private WindowManager				m_xWindowManager;
	
	/** 
	 * The screen category (small, normal, large, xlarge)
	 */
	private int							m_iScreenCategory = Configuration.SCREENLAYOUT_SIZE_UNDEFINED;

	/**
	 * default constructor
 	 * 
	 * @param xContext The application context.
	 * @param xWindowManager The WindowsManager that contains the current display.
	 * @param sLogTag The Tag to be used by the Android message logger
	*/
	public display(Context xContext, WindowManager xWindowManager, String sLogTag)
	{
		super(xContext, sLogTag);
		
		/**
		 * we can't proceed without the windows manager
		 */
		if (xWindowManager == null)
			throw new NullPointerException();
		
		/** 
		 * Grab a reference to the windows manager
		 */
		this.m_xWindowManager 	= xWindowManager;
		
		/** 
		 * initilize the instance of our display metrics class
		 */
		this.m_xDisplayMetrics = new DisplayMetrics();
		
	}
	
	/**
	 * Query the device for the information about our display.
	 * 
	 * @return Returns true id the device was queried successfully, false if it failed.
	 * 
	 */
	@Override
	public boolean query() {
		
		/** 
		 * query the device
		 */
		try 
		{
			this.m_xWindowManager.getDefaultDisplay().getMetrics(m_xDisplayMetrics);
		}
		catch (Exception exp)
		{
			return false;
		}
		
		return false;
	}
	
	/**
	 * The screen density expressed as dots-per-inch. 
	 * 
	 * @return The screen density expresses in dots-per-inch.
	 * 
	 */
	public int getDensity()
	{
		return this.m_xDisplayMetrics.densityDpi;
	}
	
	/**
	 * Return the absolute height of the display in pixels.
	 * 
	 * @return An integer representing the height of the display in pixels.
	 */
	public int getHeightInPixels()
	{
		return this.m_xDisplayMetrics.heightPixels;
	}
	
	/**
	 * Return the absolute width of the display in pixels.
	 * 
	 * @return An integer representing the width of the display in pixels.
	 */
	public int getWidthInPixels()
	{
		return this.m_xDisplayMetrics.widthPixels;
	}
	
	/**
	 * The exact physical pixels per inch of the screen in the X dimension
	 * 
	 * @return A float value representing the pixels per inch of the screen on the X axis.
	 */
	public float getXDpi()
	{
		return this.m_xDisplayMetrics.xdpi;
	}
	
	/**
	 * The exact physical pixels per inch of the screen in the Y dimension
	 * 
	 * @return  A float value representing the pixels per inch of the screen in the Y axis.
	 */
	public float getYDpi()
	{
		return this.m_xDisplayMetrics.ydpi;
	}
	
	/**
	 * Return the category for the display (small, normal, large, xlarge)
	 * 
	 * @return A string representing the screen category (small, normal, large, xlarge)
	 */
	public String getDisplayCategoryS()
	{
		int		iScreenLayout;
		
		//* Get the current configuration
		iScreenLayout = this.m_xContext.getResources().getConfiguration().screenLayout;
		
		try 
    	{
			//Determine screen size
		    if ( (iScreenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) 
		    {     
		        this.m_iScreenCategory = Configuration.SCREENLAYOUT_SIZE_LARGE;
		        return "Large";
	
		    }
		    else if ( (iScreenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) 
		    {     
		    	this.m_iScreenCategory = Configuration.SCREENLAYOUT_SIZE_NORMAL;
		        return "Normal";
	
		    } 
		    else if ( (iScreenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) 
		    {
		    	this.m_iScreenCategory = Configuration.SCREENLAYOUT_SIZE_SMALL;
		        return "Small";
		        
		    }
		    else if ( (iScreenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) 
		    {
	        	this.m_iScreenCategory = Configuration.SCREENLAYOUT_SIZE_XLARGE;
		        return "XLarge";        
		    }
		    else 
		    {
		    	//* The screen category was undefined
		    	this.m_iScreenCategory = Configuration.SCREENLAYOUT_SIZE_UNDEFINED;
		        return "Undefined";   
		    }
    	}
		catch (NoSuchFieldError exp)
    	{
    		//* The SCREENLAYOUT_SIZE_XLARGE screen constant was only avaialbe from API 9 and higher, 
			//* since our SDK min is 8 we need to make sure we dont throw an error.  
			
			this.m_iScreenCategory = Configuration.SCREENLAYOUT_SIZE_UNDEFINED;
	        return "Undefined";   
    	}
	}

	/**
	 * Create a summary of the properties of the display and return it as a String.
	 * 
	 * @return A string summary of the display information.
	 */
	@Override
	public String summaryString() {
		
		String sSummary = "";
		
		sSummary = "Display Information: \n" + 
				"Density: " + this.getDensity() + "\n" + 
				"Size Category: " + this.getDisplayCategoryS() + "\n" + 
				"Width: " + this.getWidthInPixels() + " pixels\n" + 
				"Height: " + this.getHeightInPixels() + " pixels\n" + 
				"x Dpi: " + this.getXDpi() + "\n" + 
				"y Dpi: " + this.getYDpi() + "\n";
		
		return sSummary;
	}

}
