package com.mraof.minestuck.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.mraof.minestuck.entity.consort.EntityConsort;
import com.mraof.minestuck.entity.dialogue.Dialogue;
import com.mraof.minestuck.entity.dialogue.Dialogue.DialogueContainer;
import com.mraof.minestuck.entity.dialogue.IDialoguer.EnumDialoguer;
import com.mraof.minestuck.entity.dialogue.IDialoguer;
import com.mraof.minestuck.util.Compute;
import com.mraof.minestuck.util.Debug;
import com.mraof.minestuck.util.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;


public class GuiDialogue extends GuiScreen
{
	private ResourceLocation guiBackground = new ResourceLocation("minestuck", "textures/gui/dialoguetest.png");
	private int xSize = 256, ySize = 202;
	private int x = (width - xSize) / 2, y = (height - ySize) / 2;
	private IDialoguer talker;
	private ArrayList<String> storedTalking = new ArrayList<>();
	private int topLine = 0;
	private Pair<Integer, Pair<Integer, Integer>> currentDrag = new Pair(-1, new Pair(0,0));
	private HashMap<Integer, Pair<Long, Pair<Integer, Integer>>> buttonPressed = new HashMap<>();
	
	
	public GuiDialogue ()
	{
		updateMessage("backup");
	}
	
	public GuiDialogue (IDialoguer talker)
	{
		this.talker = talker;
		updateMessage("backup");
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		this.mc.getTextureManager().bindTexture(guiBackground);
		
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
		
		//this.drawString(fontRenderer, new TextComponentTranslation("consort.hardcore").getFormattedText(), mouseX, mouseY, 1000);
	
		//draw talker message
		
		for(int i = 0; i + topLine < storedTalking.size() && i < 3; i++)
		{
			this.drawString(fontRenderer, storedTalking.get(i + topLine), x + 45, y + 95 + (10 * i), EnumDialoguer.getType((Entity) talker).getColor());
		}
		for(Integer key : buttonPressed.keySet())
		{
			this.drawString(fontRenderer, key + ": " + buttonPressed.get(key).object1, buttonPressed.get(key).object2.object1, buttonPressed.get(key).object2.object2, 1000);
		}
			
	}
	
	public void setTalker(Entity talker)
	{
		if(talker instanceof IDialoguer)
		{
			this.talker = (IDialoguer) talker;
			
		}
	}
	
	public void updateMessage(String dialogueId)
	{
		DialogueContainer dialogue = Dialogue.getMessageFromString(dialogueId);
		String message = dialogue.getTalkerMessage(talker, Minecraft.getMinecraft().player).getUnformattedText();
		while(message != null && !message.contentEquals(""))
		{
			String part = "";
			while(part.length() < 29 && !message.contentEquals(""))
			{
				//can re-arrange to make faster
				int wordEnd = message.indexOf(" ") == -1 ? message.length() : message.indexOf(" ");
				String word = message.substring(0, wordEnd);
				if(word.length() > 30)
				{
					wordEnd = 29;
					word = message.substring(0, wordEnd) + "-";
				}
				if(part.length() + word.length() > 30)
					break;
				part += " " + word;
				message = message.substring(wordEnd == message.length() ? wordEnd : wordEnd + 1);
			}
			if(part.length() >= 1 && part.substring(0, 1).contentEquals(" "))
				part = part.substring(1);
			storedTalking.add(part);
		}
	}
	
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		
		if(mouseButton != 0) return;
			
		int dragType = -1;
		
		//drag talker message
		if(Compute.isWithin2D(mouseX, mouseY, x + 44, y + 94, x + 211, y + 125))
			dragType = 0;
		
		currentDrag = new Pair(dragType, new Pair(mouseX, mouseY));
    }
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state)
    {
		super.mouseReleased(mouseX, mouseY, state);

		buttonPressed.remove(state);
		if(state !=0) return;
		currentDrag.object1 = -1;
    }
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
		if(currentDrag.object1 == 0 && Math.abs(mouseY - currentDrag.object2.object2) > 10)
		{
			int newTopLine = topLine + (mouseY - currentDrag.object2.object2) / 10;
			topLine = newTopLine < 0 ? 0 : newTopLine > storedTalking.size() - 1 ? storedTalking.size() - 1 : newTopLine;
			
			currentDrag.object2.object1 = mouseX;
			currentDrag.object2.object2 = mouseY;
		}
		
		buttonPressed.put(clickedMouseButton, new Pair(timeSinceLastClick, new Pair(mouseX, mouseY)));
    }
	
	@Override
	public void setWorldAndResolution(Minecraft mc, int width, int height)
	{
		super.setWorldAndResolution(mc, width, height);
		x = (width - xSize) / 2;
		y = (height - ySize) / 2;
	}
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
}
