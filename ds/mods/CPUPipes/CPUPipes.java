package ds.mods.CPUPipes;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import ds.mods.CPUPipes.client.ClientPacketHandler;
import ds.mods.CPUPipes.core.CommonProxy;
import ds.mods.CPUPipes.core.GuiHandler;
import ds.mods.CPUPipes.core.OreGen;
import ds.mods.CPUPipes.core.PacketHandler;
import ds.mods.CPUPipes.core.block.BlockCPU;
import ds.mods.CPUPipes.core.block.BlockConstantItem;
import ds.mods.CPUPipes.core.block.BlockGreyDustOre;
import ds.mods.CPUPipes.core.block.BlockInventoryConnector;
import ds.mods.CPUPipes.core.block.BlockMonitor;
import ds.mods.CPUPipes.core.block.BlockSDCardSlot;
import ds.mods.CPUPipes.core.block.BlockWire;
import ds.mods.CPUPipes.core.block.BlockWriter;
import ds.mods.CPUPipes.core.item.ItemGreyDust;
import ds.mods.CPUPipes.core.item.ItemLabel;
import ds.mods.CPUPipes.core.item.ItemLabelStacked;
import ds.mods.CPUPipes.core.item.ItemSDCard;
import ds.mods.CPUPipes.core.tile.TileEntityCPU;
import ds.mods.CPUPipes.core.tile.TileEntityConstantItem;
import ds.mods.CPUPipes.core.tile.TileEntityInventoryConnector;
import ds.mods.CPUPipes.core.tile.TileEntityMonitor;
import ds.mods.CPUPipes.core.tile.TileEntitySDCardSlot;
import ds.mods.CPUPipes.core.tile.TileEntityWire;
import ds.mods.CPUPipes.core.tile.TileEntityWriter;

@Mod(modid = "CPUPipes", name = "CPUPipes", version = "0.0.1")
@NetworkMod(clientSideRequired = true, serverSideRequired = true, serverPacketHandlerSpec =
@SidedPacketHandler(channels = {"CPUEditLabel", "GUIEditor", "CPUEvent"}, packetHandler = PacketHandler.class), clientPacketHandlerSpec =
@SidedPacketHandler(channels = {"GUIEditor", "LabelHolder", "TermUpdate"}, packetHandler = ClientPacketHandler.class))
public class CPUPipes {

	@Instance("CPUPipes")
	public static CPUPipes instance;
	
	public static BlockInventoryConnector invcon;
	public static int invconID;
	
	public static BlockWire wire;
	public static int wireID;
	
	public static BlockCPU cpu;
	public static int cpuID;
	
	public static BlockConstantItem constItem;
	public static int constItemID;
	
	public static BlockSDCardSlot sdcardSlot;
	public static int sdcardSlotID;
	
	public static BlockWriter writer;
	public static int writerID;
	
	public static BlockGreyDustOre greyDustOre;
	public static int greyDustOreID;
	
	public static BlockMonitor monitor;
	public static int monitorID;
	
	public static ItemLabelStacked emptyLabel;
	public static int emptyLabelID;
	
	public static ItemLabel label;
	public static int labelID;
	
	public static ItemSDCard sdcard;
	public static int sdcardID;
	
	public static ItemGreyDust greyDust;
	public static int greyDustID;
	
	public static CreativeTabs tab;
	
	public static int sdcardSpace = 1024 * 1024 * 1024 * 5;
	
	@SidedProxy(serverSide = "ds.mods.CPUPipes.core.CommonProxy", clientSide = "ds.mods.CPUPipes.client.ClientProxy")
	public static CommonProxy proxy;
	
	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		Configuration conf = new Configuration(event.getSuggestedConfigurationFile());
		conf.load();
		
		Property prop;
		
		prop = conf.getBlock("InventoryDevice", 800, "The block ID for Inventory Devices (Default: 800)");
		invconID = prop.getInt();
		
		prop = conf.getBlock("NetworkWire", 801, "The block ID for Network Wires (Default: 800)");
		wireID = prop.getInt();
		
		prop = conf.getBlock("CPU", 802, "The block ID for CPU (Default: 802)");
		cpuID = prop.getInt();
		
		prop = conf.getBlock("ConstantItemDevice", 803, "The block ID for ConstantItemDevice (Default: 803)");
		constItemID = prop.getInt();
		
		prop = conf.getBlock("SDCardSlot", 804, "The block ID for SDCardSlots (Default: 804)");
		sdcardSlotID = prop.getInt();
		
		prop = conf.getBlock("ProgramWriter", 805, "The block ID for ProgramWriter (Default: 805)");
		writerID = prop.getInt();
		
		prop = conf.getBlock("GreyDustOre", 806, "The block ID for GreyDustOre (Default: 806)");
		greyDustOreID = prop.getInt();
		
		prop = conf.getBlock("Monitor", 807, "The block ID for Monitors (Default: 807)");
		monitorID = prop.getInt();
		
		
		prop = conf.getItem("EmptyLabel", 5000, "The item ID for EmptyLabels (Default: 5000)");
		emptyLabelID = prop.getInt();
		
		prop = conf.getItem("UsedLabel", 5001, "The item ID for UsedLabels (Default: 5001)");
		labelID = prop.getInt();
		
		prop = conf.getItem("SDCard", 5002, "The item ID for SDCards (Default: 5002)");
		sdcardID = prop.getInt();
		
		prop = conf.getItem("GreyDust", 5003, "The item ID for GreyDust (Default: 5003)");
		greyDustID = prop.getInt();
		
		conf.save();
	}

	@Init
	public void init(FMLInitializationEvent event) {
		NetworkRegistry.instance().registerGuiHandler(this, new GuiHandler());
		tab = new CreativeTabs("CPUPipes");
		registerBlocks();
		registerItems();
		proxy.registerRenders();
		registerNames();
	}
	
	public void registerItems()
	{
		emptyLabel = new ItemLabelStacked(emptyLabelID - 256);
		GameRegistry.registerItem(emptyLabel, "CPUItemEmptyLabel");
		
		label = new ItemLabel(labelID - 256);
		GameRegistry.registerItem(label, "CPUItemLabel");
		
		sdcard = new ItemSDCard(sdcardID - 256);
		GameRegistry.registerItem(sdcard, "CPUSDCard");
		
		greyDust = new ItemGreyDust(greyDustID - 256);
		GameRegistry.registerItem(greyDust, "CPUGreyDust");
	}
	
	public void registerBlocks()
	{
		invcon = new BlockInventoryConnector(invconID, Material.iron);
		GameRegistry.registerBlock(invcon, "CPUInvCon");
		GameRegistry.registerTileEntity(TileEntityInventoryConnector.class, "CPUTEInvCon");
		
		wire = new BlockWire(wireID, Material.circuits);
		GameRegistry.registerBlock(wire, "CPUWire");
		GameRegistry.registerTileEntity(TileEntityWire.class, "CPUTEWire");
		
		cpu = new BlockCPU(cpuID, Material.iron);
		GameRegistry.registerBlock(cpu, "CPUcpu");
		GameRegistry.registerTileEntity(TileEntityCPU.class, "CPUTEcpu");
		
		constItem = new BlockConstantItem(constItemID, Material.iron);
		GameRegistry.registerBlock(constItem, "CPUConstItem");
		GameRegistry.registerTileEntity(TileEntityConstantItem.class, "CPUTEConstItem");
		
		sdcardSlot = new BlockSDCardSlot(sdcardSlotID, Material.iron);
		GameRegistry.registerBlock(sdcardSlot, "CPUSDSlot");
		GameRegistry.registerTileEntity(TileEntitySDCardSlot.class, "CPUTESDSlot");
		
		writer = new BlockWriter(writerID, Material.iron);
		GameRegistry.registerBlock(writer, "CPUWriter");
		GameRegistry.registerTileEntity(TileEntityWriter.class, "CPUTEWriter");
		
		greyDustOre = new BlockGreyDustOre(greyDustOreID);
		GameRegistry.registerBlock(greyDustOre, "CPUGreyDustOre");
		OreDictionary.registerOre("Grey Dust Ore", greyDustOre);
		MinecraftForge.ORE_GEN_BUS.register(new OreGen());
		
		monitor = new BlockMonitor(monitorID, Material.iron);
		GameRegistry.registerBlock(monitor, "CPUMonitor");
		GameRegistry.registerTileEntity(TileEntityMonitor.class, "CPUTEMonitor");
	}
	
	public void registerNames()
	{
		constItem.setUnlocalizedName("cpupipes.block.constItem");
		LanguageRegistry.addName(constItem, "Constant Item Device");
		
		cpu.setUnlocalizedName("cpupipes.block.cpu");
		LanguageRegistry.addName(cpu, "CPU");
		
		emptyLabel.setUnlocalizedName("cpupipes.item.emptyLabel");
		LanguageRegistry.addName(emptyLabel, "Empty Label");
		
		invcon.setUnlocalizedName("cpupipes.block.invcon");
		LanguageRegistry.addName(invcon, "Inventory Device");
		
		sdcardSlot.setUnlocalizedName("cpupipes.block.sdcardSlot");
		LanguageRegistry.addName(sdcardSlot, "SDCard Slot");
		
		wire.setUnlocalizedName("cpupipes.block.wire");
		LanguageRegistry.addName(wire, "Network Wire");
		
		writer.setUnlocalizedName("cpupipes.block.writer");
		LanguageRegistry.addName(writer, "Program Writer");
		
		greyDustOre.setUnlocalizedName("cpupipes.block.greyDustOre");
		LanguageRegistry.addName(greyDustOre, "Grey Dust Ore");
		
		greyDust.setUnlocalizedName("cpupipes.item.greyDust");
		LanguageRegistry.addName(greyDust, "Grey Dust");
		
		monitor.setUnlocalizedName("cpupipes.block.monitor");
		LanguageRegistry.addName(monitor, "Monitor");
	}
}
