package ds.mods.CPUPipes.core.network;

public interface INetworkDevice {
	public int getX();
	public int getY();
	public int getZ();
	public String getType();
	public int getID();
	public void setID(int id);
	public String getLabel();
	public Object call(int method, Object... args);
}
