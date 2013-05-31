package ds.mods.CPUPipes.client.gui;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;

public class GuiEditLabel extends GuiScreen {
	public ItemStack item;
	public NBTTagCompound nbt;
	public String name;
	public GuiTextField textField;
	private int xSize;
	private int ySize;
	
	public GuiEditLabel(ItemStack stack)
	{
		item = stack;
		if (!item.hasTagCompound())
		{
			item.setTagCompound(new NBTTagCompound());
		}
		nbt = item.getTagCompound();
		name = nbt.getString("label");
		xSize = 176;
		ySize = 60;
	}
	
	public void initGui()
    {
        super.initGui();
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.buttonList.add(new GuiButton(0, k+42, l+37, 85, 20, "Done"));
        textField = new GuiTextField(fontRenderer, k+10, l+23, 158, 12);
        textField.setEnableBackgroundDrawing(false);
        textField.setTextColor(-1);
    }
	
	/* (non-Javadoc)
	 * @see net.minecraft.client.gui.GuiScreen#actionPerformed(net.minecraft.client.gui.GuiButton)
	 */
	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		switch(par1GuiButton.id)
		{
		case 0:
		{
			nbt.setString("label", name);
			this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
            Packet250CustomPayload packet = new Packet250CustomPayload("CPUEditLabel", name.getBytes());
            PacketDispatcher.sendPacketToServer(packet);
		}
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		// TODO Auto-generated method stub
		return false;
	}

	protected void keyTyped(char par1, int par2)
    {
		if (textField.textboxKeyTyped(par1, par2))
		{
			name = textField.getText();
		}
		else
		{
			super.keyTyped(par1, par2);
		}
    }
	
	protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        textField.mouseClicked(par1, par2, par3);
    }
	
	public void drawScreen(int par1, int par2, float par3)
    {
		this.mc.renderEngine.bindTexture("/mods/CPUPipes/textures/gui/label.png");
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        super.drawScreen(par1, par2, par3);
        GL11.glDisable(GL11.GL_LIGHTING);
        textField.drawTextBox();
    }
}
