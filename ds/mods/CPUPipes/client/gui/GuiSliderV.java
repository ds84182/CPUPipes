package ds.mods.CPUPipes.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

public class GuiSliderV extends GuiButton {
	/** The value of this slider control. */
    public float sliderValue = 1.0F;

    /** Is this slider control being dragged. */
    public boolean dragging = false;

    public GuiSliderV(int par1, int par2, int par3, int w, String par5Str, float par6)
    {
        super(par1, par2, par3, 20, w, par5Str);
        this.sliderValue = par6;
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    protected int getHoverState(boolean par1)
    {
        return 0;
    }

    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    protected void mouseDragged(Minecraft par1Minecraft, int par2, int par3)
    {
        if (this.drawButton)
        {
            if (this.dragging)
            {
                this.sliderValue = (float)(par3 - (this.yPosition + 4)) / (float)(this.height - 8);

                if (this.sliderValue < 0.0F)
                {
                    this.sliderValue = 0.0F;
                }

                if (this.sliderValue > 1.0F)
                {
                    this.sliderValue = 1.0F;
                }
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.xPosition, this.yPosition + (int)(this.sliderValue * (float)(this.height - 8)), 0, 66, this.width/2, 4);
            this.drawTexturedModalRect(this.xPosition, this.yPosition + (int)(this.sliderValue * (float)(this.height - 8)) + 4, 0, 82, this.width/2, 4);
            this.drawTexturedModalRect(this.xPosition+this.width/2, this.yPosition + (int)(this.sliderValue * (float)(this.height - 8)), 200-this.width/2, 66, this.width/2, 4);
            this.drawTexturedModalRect(this.xPosition+this.width/2, this.yPosition + (int)(this.sliderValue * (float)(this.height - 8)) + 4, 200-this.width/2, 82, this.width/2, 4);
        }
    }
    
    @Override
    public void drawButton(Minecraft par1Minecraft, int par2, int par3)
    {
        if (this.drawButton)
        {
            FontRenderer fontrenderer = par1Minecraft.fontRenderer;
            par1Minecraft.renderEngine.bindTexture("/gui/gui.png");
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_82253_i = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
            int k = this.getHoverState(this.field_82253_i);
            for (int i = 0; i<Math.ceil((this.height-1)/18F); i++)
            {
            	this.drawTexturedModalRect(this.xPosition, this.yPosition+(i*18), 0, 47 + k * 20, this.width / 2, Math.min(18,this.height-(i*18)));
            	this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition+(i*18), 200 - this.width / 2, 47 + k * 20, this.width / 2, Math.min(18,this.height-(i*18)));
            }
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + k * 20, this.width / 2, 1);
            this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + k * 20, this.width / 2, 1);
            this.drawTexturedModalRect(this.xPosition, this.yPosition+this.height-1, 0, 65 + k * 20, this.width / 2, 1);
            this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition+this.height-1, 200 - this.width / 2, 65 + k * 20, this.width / 2, 1);
            this.mouseDragged(par1Minecraft, par2, par3);
            int l = 14737632;

            if (!this.enabled)
            {
                l = -6250336;
            }
            else if (this.field_82253_i)
            {
                l = 16777120;
            }

            this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, l);
        }
    }

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
     * e).
     */
    public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3)
    {
        if (super.mousePressed(par1Minecraft, par2, par3))
        {
            this.sliderValue = (float)(par3 - (this.yPosition + 4)) / (float)(this.height - 8);

            if (this.sliderValue < 0.0F)
            {
                this.sliderValue = 0.0F;
            }

            if (this.sliderValue > 1.0F)
            {
                this.sliderValue = 1.0F;
            }
            this.dragging = true;
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    public void mouseReleased(int par1, int par2)
    {
        this.dragging = false;
    }
}
