package ds.mods.CPUPipes.core.network;

public interface INetworkWire {
	public void merge(Network net); //Merges it's network into ours and sets it's network as ours
	public Network getNetwork(); //Gets the network object associated with the wire.
}
