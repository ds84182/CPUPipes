package ds.mods.CPUPipes.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ChatAllowedCharacters;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import ds.mods.CPUPipes.core.Terminal;
import ds.mods.CPUPipes.core.tile.TileEntityMonitor;
import ds.mods.CPUPipes.server.container.ContainerTerminal;

public class GuiTerminal extends GuiContainer {
	
	public Terminal term;
	public int tick;
	public boolean showCursor = false;
	
	public GuiTerminal(TileEntityMonitor mon)
	{
		super(new ContainerTerminal(mon));
		term = mon.term;
		Keyboard.enableRepeatEvents(true);
	}
	
	@Override
	public void updateScreen() {
		tick++;
		if (term.cursorBlink) {
			if (tick % 10 == 0) {
				showCursor = !showCursor;
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor3f(0, 0, 0);
		int k = (int)((width/2D)-((term.size.x*6)/2D));
		int l = (int)((height/2D)-((term.size.y*8)/2D));
		drawTexturedModalRect(k, l, 0, 0, term.size.x*6, term.size.y*8);
		GL11.glColor3f(1, 1, 1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		for (int x = 0; x < term.size.x; x++) {
			for (int y = 0; y < term.size.y; y++) {
				this.fontRenderer.drawString(String.valueOf(term.getChar(x+1, y+1)), k + (x * 6) + ((3 - fontRenderer.getCharWidth(term.getChar(x+1, y+1)) / 2)), l + (y * 8), 0xFFFFFF);
				//System.out.println("Width of "+line.charAt(i)+": "+fontRenderer.getCharWidth(line.charAt(i)));
			}
		}
		if (showCursor && term.cursor.x <= term.size.x && term.cursor.x > 0 && term.cursor.y <= term.size.y && term.cursor.y > 0) {
			this.fontRenderer.drawString("_", k + ((term.cursor.x - 1) * 6), l + ((term.cursor.y - 1) * 8), 0xFFFFFF);
		}
		//this.drawTexturedModalRect(par1, par2, par3, par4, par5, par6)
	}
	
	public void keyEvent(int key)
	{
		term.sendEvent("key", key);
	}
	
	public void charEvent(char c)
	{
		term.sendEvent("char", String.valueOf(c));
	}

	@Override
	protected void keyTyped(char par1, int par2) {
		if (par2 == 1)
		{
			//Exit GUI
			Keyboard.enableRepeatEvents(false);
			this.mc.thePlayer.closeScreen();
			return;
		}
		if (ChatAllowedCharacters.isAllowedCharacter(par1))
		{
			charEvent(par1);
		}
		keyEvent(par2);
	}

}
