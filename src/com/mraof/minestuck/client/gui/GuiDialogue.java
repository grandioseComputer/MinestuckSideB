package com.mraof.minestuck.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiDialogue extends GuiScreen
{
	private ResourceLocation guiBackground = new ResourceLocation("minestuck", "textures/gui/dialougetest.png");
	private int xSize = 240, ySize = 216;
	
	public GuiDialogue ()
	{
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		this.mc.getTextureManager().bindTexture(guiBackground);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		
		//maybe make it so that the top is -1072689136, the bottom is -804253680, and 1/3 and 2/3 through is something inbetween
		//top
		this.drawGradientRect(0, 0, this.width, this.height - (ySize + y) + 4, -1072689136, -1072689136);
        //left
		this.drawGradientRect(0, this.height - (ySize + y) + 4, this.width - (xSize + x) + 4, this.height - (y + 4), -1072689136, -1072689136);
        //bottom
        this.drawGradientRect(0, this.height - (y + 4), this.width, this.height, -1072689136, -1072689136);
        //right
        this.drawGradientRect(this.width - (x + 4), this.height - (ySize + y) + 4, this.width, this.height - (y + 4), -1072689136, -1072689136);

		this.drawModalRectWithCustomSizedTexture(x, y, 0, 0, xSize, ySize, xSize, ySize);
	}
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
}
