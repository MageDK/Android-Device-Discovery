/**
 * This interface implements history functionality for some of the discovery objects.  You 
 * can specify the max number of history entries that are stored by the object.
 * 
 * @author Emil Diego
 *
 */

package com.emildiego.devicediscovery;

public interface discoveryHistory 
{
	
	/**
	 * Adds a history entry
	 */
	public void addHistoryEntry(Object xEntry) throws NullPointerException;
	
	/**
	 * Remove a history entry as the specified position
	 * 
	 * @param iEntryIndex The index of the entry we want to remove
	 */
	public void removeHistoryEntry(int iEntryIndex) throws IndexOutOfBoundsException;
	
	/**
	 * Retreive a reference to the history item specified by the index
	 * 
	 * @param iEntryIndex The index of the item we want to retreive
	 */
	public Object getHistoryEntry(int iEntryIndex) throws IndexOutOfBoundsException;
	
	/**
	 * Return the number of history entries curerntly in the list
	 */
	public int getHistorySize();
	
	/**
	 * Get/Set the maximum number of history entries.
	 * 
	 * @param iMaxEntries
	 */
	public void setMaxHistoryEntries(int iMaxEntries);
	public int getMaxHistoryEntries();

}
