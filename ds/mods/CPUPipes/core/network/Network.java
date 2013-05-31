package ds.mods.CPUPipes.core.network;

import java.util.ArrayList;
import java.util.HashMap;

public class Network {
	public ArrayList<INetworkDevice> devices = new ArrayList<INetworkDevice>();
	public HashMap<String,Integer> nextIDlist = new HashMap<String, Integer>();
	
	public void add(INetworkDevice device)
	{
		device.setID(getNextID(device.getType()));
		devices.add(device);
	}
	
	public int getNextID(String s)
	{
		if (!nextIDlist.containsKey(s))
		{
			nextIDlist.put(s, 1);
		}
		int next = nextIDlist.get(s);
		nextIDlist.put(s, next+1);
		return next;
	}
}
