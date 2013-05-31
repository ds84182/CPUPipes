package ds.mods.CPUPipes.client.gui;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.network.PacketDispatcher;
import java.util.ArrayList;

import net.minecraft.client.gui.*;
import net.minecraft.util.ChatAllowedCharacters;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import ds.mods.CPUPipes.core.tile.TileEntityWriter;
import net.minecraft.network.packet.Packet250CustomPayload;

//TODO: Make save button work, actualy requests the file to be saved by sending a packet
//TODO: Make open button work, send a packet requesting the file and wait for it to come back
//TODO: Send update packet every second if dirty. Keep the text inside the block when cancel is clicked.
public class GuiEditor extends GuiScreen {

	public ArrayList<String> lines;
	public int charX = 1;
	public int charY = 1;
	public int scrollX = 0;
	public int scrollY = 0;
	private int xSize;
	private int ySize;
	public GuiSliderV vScrollbar;
	public GuiSliderH hScrollbar;
	public boolean showCursor = true;
	public boolean blink = true;
	public int blinkTick = 0;
	public boolean messageShown = false;
	public boolean messageExitAfterShown = true;
	public String message = "Cannot save: No disk found!";
	public GuiButton messageDone;
	public GuiButton cancel;
	public GuiButton save;
	public GuiButton newFile;
	public GuiButton open;
	public GuiTextField filename;
	public TileEntityWriter tile;

	public GuiEditor(TileEntityWriter t) {
		xSize = 222;
		ySize = 227;
		lines = t.lines;
		if (lines.isEmpty()) {
			lines.add("");
		}
		tile = t;
		tile.dirty = true;
	}

	@Override
	public void updateScreen() {
		if (blink) {
			blinkTick++;
			if (blinkTick % 10 == 0) {
				showCursor = !showCursor;
			}
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void initGui() {
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		vScrollbar = new GuiSliderV(0, k + 198, l + 17, 142, "", 0);
		buttonList.add(vScrollbar);
		hScrollbar = new GuiSliderH(1, k + 5, l + 161, 192, "", 0);
		buttonList.add(hScrollbar);
		buttonList.add(save = new GuiButton(2, k + 4, l + 203, 49, 20, "Save"));
		buttonList.add(open = new GuiButton(3, k + 53, l + 203, 49, 20, "Open"));
		buttonList.add(newFile = new GuiButton(4, k + 102 + 5, l + 203, 49, 20, "New File"));
		buttonList.add(cancel = new GuiButton(5, k + 156, l + 203, 49, 20, "Cancel"));
		int messageX = (this.width - 176) / 2;
		int messageY = (this.height - 44) / 2;
		messageDone = new GuiButton(6, messageX + 63, messageY + 21, 50, 20, "Done");
		messageDone.drawButton = false;
		buttonList.add(messageDone);
		filename = new GuiTextField(fontRenderer, k + 5, l + 185, 198, 16);
		filename.setFocused(false);
		filename.setEnableBackgroundDrawing(false);
		Keyboard.enableRepeatEvents(true);
	}

	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		if (messageShown & par1GuiButton == messageDone) {
			messageShown = false;
			messageDone.enabled = false;
			if (messageExitAfterShown) {
				this.mc.displayGuiScreen((GuiScreen) null);
				this.mc.setIngameFocus();
			}
		}
		if (messageShown) {
			return;
		}
		if (par1GuiButton == cancel) {
			this.mc.displayGuiScreen((GuiScreen) null);
			this.mc.setIngameFocus();
		} else if (par1GuiButton == newFile) {
			lines.clear();
			lines.add("");
			charX = 1;
			charY = 1;
			scrollX = 0;
			scrollY = 0;
			hScrollbar.sliderValue = 0;
			vScrollbar.sliderValue = 0;
		} else if (par1GuiButton == save) {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeInt(1);
			out.writeInt(tile.xCoord);
			out.writeInt(tile.yCoord);
			out.writeInt(tile.zCoord);
			out.writeInt(tile.worldObj.provider.dimensionId);
			out.writeUTF(filename.getText());
			Packet250CustomPayload p = new Packet250CustomPayload("GUIEditor", out.toByteArray());
			PacketDispatcher.sendPacketToServer(p);
		} else if (par1GuiButton == open) {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeInt(2);
			out.writeInt(tile.xCoord);
			out.writeInt(tile.yCoord);
			out.writeInt(tile.zCoord);
			out.writeInt(tile.worldObj.provider.dimensionId);
			out.writeUTF(filename.getText());
			Packet250CustomPayload p = new Packet250CustomPayload("GUIEditor", out.toByteArray());
			PacketDispatcher.sendPacketToServer(p);
		}
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	public void setScrollY(int n) {
		scrollY = n;
		int nlines = lines.size() - 18;
		vScrollbar.sliderValue = n / (float) nlines;
	}

	public void incScrollY() {
		setScrollY(scrollY + 1);
	}

	public void decScrollY() {
		setScrollY(scrollY - 1);
	}

	public int getWidth() {
		int w = 0;
		for (String line : lines) {
			w = Math.max(w, line.length());
		}
		return w;
	}

	public void setScrollX(int n) {
		scrollX = n;
		int nlines = getWidth() - 31;
		hScrollbar.sliderValue = n / (float) nlines;
	}

	public void incScrollX() {
		setScrollX(scrollX + 1);
	}

	public void decScrollX() {
		setScrollX(scrollX - 1);
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		this.mc.renderEngine.bindTexture("/mods/CPUPipes/textures/gui/edit.png");
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
		super.drawScreen(par1, par2, par3);
		int lineAt = 0;
		for (int linenum = 0; linenum < Math.min(18, lines.size() - scrollY); linenum++) {
			String line = lines.get(linenum + scrollY);
			for (int i = 0; i < Math.min(32, line.length() - scrollX); i++) {
				this.fontRenderer.drawString(String.valueOf(line.charAt(i + scrollX)), k + 5 + (i * 6) + ((3 - fontRenderer.getCharWidth(line.charAt(i + scrollX)) / 2)), l + 16 + (lineAt * 8), 0xFFFFFF);
				//System.out.println("Width of "+line.charAt(i)+": "+fontRenderer.getCharWidth(line.charAt(i)));
			}
			lineAt++;
		}
		if (showCursor & charX < scrollX + 33 & charX > scrollX & charY < scrollY + 19 & charY > scrollY) {
			this.fontRenderer.drawString("_", k + 5 + (((charX - scrollX) - 1) * 6), l + 16 + (((charY - scrollY) - 1) * 8), 0xFFFFFF);
		}
		if (lines.size() <= 18) {
			vScrollbar.enabled = false;
		} else {
			vScrollbar.enabled = true;
			//Snap the scrollbar value to the nearest line
			int newscrollY = Math.round(vScrollbar.sliderValue * (lines.size() - 18));
			setScrollY(newscrollY);
			/*//Set charY to a value that is between scrollY and scrollY+18
									 charY = Math.max(charY, scrollY);
									 charY = Math.min(charY, scrollY+18);
									 int len = lines.get(charY-1).length();
									 charX = Math.min(charX,len+1);*/
		}
		if (getWidth() <= 32) {
			hScrollbar.enabled = false;
		} else {
			hScrollbar.enabled = true;
			//Snap the scrollbar value to the nearest line
			int newscrollX = Math.round(hScrollbar.sliderValue * (getWidth() - 31));
			setScrollX(newscrollX);
			/*//Set charX to a value that is between scrollX and scrollX+18
									 charX = Math.max(charX, scrollX+1);
									 charX = Math.min(charX, scrollX+32);*/
		}
		filename.drawTextBox();
		if (messageShown) {
			save.enabled = false;
			cancel.enabled = false;
			GL11.glColor4f(1F, 1F, 1F, 1F);
			int messageX = (this.width - 176) / 2;
			int messageY = (this.height - 44) / 2;
			this.mc.renderEngine.bindTexture("/mods/CPUPipes/textures/gui/message.png");
			this.drawDefaultBackground();
			this.drawTexturedModalRect(messageX, messageY, 0, 0, 176, 44);
			this.drawCenteredString(fontRenderer, message, messageX + (176 / 2), messageY + 6, 0xFFFFFF);
			messageDone.enabled = true;
			messageDone.drawButton = true;
			messageDone.drawButton(mc, par1, par2);
			messageDone.drawButton = false;
		} else {
			save.enabled = true;
			cancel.enabled = true;
		}
	}

	public void fixPositions() {
		while (charX > scrollX + 32) {
			incScrollX();
		}
		while (charX - 1 < scrollX) {
			decScrollX();
		}
		while (charY > scrollY + 18) {
			incScrollY();
		}
		while (charY < scrollY) {
			decScrollY();
		}
	}

	@Override
	protected void keyTyped(char par1, int par2) {
		if (messageShown) {
			return;
		}
		if (filename.isFocused()) {
			filename.textboxKeyTyped(par1, par2);
			return;
		}
		if (ChatAllowedCharacters.isAllowedCharacter(par1)) {
			String n = lines.get(charY - 1);
			String before = n.substring(0, Math.min(charX - 1,n.length()));
			String after;
			if (charX - 1 < n.length()) {
				after = n.substring(charX - 1, n.length());
			} else {
				after = "";
			}
			lines.set(charY - 1, before + par1 + after);
			charX += 1;
			fixPositions();
			tile.dirty = true;
		} else {
			if (par2 == Keyboard.KEY_RETURN) {
				String n = lines.get(charY - 1);
				String after;
				if (charX - 1 < n.length()) {
					after = n.substring(charX - 1, n.length());
				} else {
					after = "";
				}
				lines.set(charY - 1, n.substring(0, charX - 1));
				charY += 1;
				charX = 1;
				lines.add(charY - 1, after);
				setScrollX(0);
				fixPositions();
				tile.dirty = true;
			} else if (par2 == Keyboard.KEY_DOWN) {
				if (charY < lines.size()) {
					charY++;
					String s = lines.get(charY - 1);
					charX = Math.min(charX, s.length() + 1);
					fixPositions();
				}
			} else if (par2 == Keyboard.KEY_UP) {
				if (charY > 1) {
					charY--;
					String s = lines.get(charY - 1);
					charX = Math.min(charX, s.length() + 1);
					fixPositions();
				}
			} else if (par2 == Keyboard.KEY_RIGHT) {
				String s = lines.get(charY - 1);
				if (charX < s.length() + 1) {
					charX++;
					fixPositions();
				}
			} else if (par2 == Keyboard.KEY_LEFT) {
				if (charX > 1) {
					charX--;
					fixPositions();
				}
			} else if (par2 == Keyboard.KEY_BACK) {
				if (charX > 1) {
					String n = lines.get(charY - 1);
					String before = n.substring(0, charX - 2);
					String after;
					if (charX - 1 < n.length()) {
						after = n.substring(charX - 1, n.length());
					} else {
						after = "";
					}
					lines.set(charY - 1, before + after);
					charX--;
					fixPositions();
					tile.dirty = true;
				} else if (charY > 1) {
					String l = lines.get(charY - 1);
					lines.remove(charY - 1);
					int otherlen = lines.get(charY - 2).length();
					lines.set(charY - 2, lines.get(charY - 2) + l);
					charY--;
					charX = otherlen + 1;
					fixPositions();
					tile.dirty = true;
				}
			} else if (par2 == Keyboard.KEY_TAB)
			{
				for (int i=0; i<3; i++)
				{
					keyTyped(' ',Keyboard.KEY_SPACE);
				}
			}
		}
		super.keyTyped(par1, par2);
	}

	@Override
	protected void mouseClicked(int par1, int par2, int par3) {
		messageDone.drawButton = messageShown;
		super.mouseClicked(par1, par2, par3);
		messageDone.drawButton = false;
		filename.mouseClicked(par1, par2, par3);
	}
}
