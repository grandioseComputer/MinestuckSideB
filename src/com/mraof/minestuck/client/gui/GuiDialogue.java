package com.mraof.minestuck.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nullable;

import com.mraof.minestuck.capabilities.MinestuckCapabilities;
import com.mraof.minestuck.client.gui.components.GuiScrollable;
import com.mraof.minestuck.entity.consort.EntityConsort;
import com.mraof.minestuck.entity.dialogue.Dialogue;
import com.mraof.minestuck.entity.dialogue.Dialogue.DialogueContainer;
import com.mraof.minestuck.entity.dialogue.DialogueType.BackDialogue;
import com.mraof.minestuck.entity.dialogue.DialogueType.ExitDialogue;
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
	private ResourceLocation guiBackground = new ResourceLocation("minestuck", "textures/gui/dialoguetestatt.png");
	private int xSize = 340, ySize = 110;
	private int x = (width - xSize) / 2, y = height - (ySize + 10);//(height - ySize) / 2;
	private IDialoguer talker;
	private ArrayList<GuiScrollable> scrolls = new ArrayList<GuiScrollable>(){
			{
				add(new GuiScrollable(x + 7, y + 7, 325, 30, 3, 1, 0, false));
				add(new GuiScrollable(x + 53, y + 57, 233, 40, 4, 1, 0, true));

			}};
	private int ticks = 0;
	
	private ArrayList<String> storedTalking = new ArrayList<>();
	private int[] startPointer = new int[0];
	private ArrayList<String> storedOptions = new ArrayList<>();
	
	private ArrayList<String> previousOptions = new ArrayList<>();
	
	
	private Pair<Integer, Pair<Integer, Integer>> currentDrag = new Pair(-1, new Pair(0,0));
	private HashMap<Integer, Pair<Long, Pair<Integer, Integer>>> buttonPressed = new HashMap<>();
	
	
	public GuiDialogue ()
	{
		int[] knowledge = mc.player.getCapability(MinestuckCapabilities.PLAYER_DATA, null).getTalkerKnowledge();
		if(knowledge.length <= 0)
			updateMessage("backup");
		else
			updateMessage(null);
	}
	
	public GuiDialogue (IDialoguer talker)
	{
		this();
		this.talker = talker;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		this.mc.getTextureManager().bindTexture(guiBackground);
		
		//maybe make it so that the top is -1072689136, the bottom is -804253680, and 1/3 and 2/3 through is something inbetween
		/*
		//top
		this.drawGradientRect(0, 0, this.width, this.height - (ySize + y) + 4, -1072689136, -1072689136);
        //left
		this.drawGradientRect(0, this.height - (ySize + y) + 4, this.width - (xSize + x) + 4, this.height - (y + 4), -1072689136, -1072689136);
        //bottom
        this.drawGradientRect(0, this.height - (y + 4), this.width, this.height, -1072689136, -1072689136);
        //right
        this.drawGradientRect(this.width - (x + 4), this.height - (ySize + y) + 4, this.width, this.height - (y + 4), -1072689136, -1072689136);
		*/
		
		this.drawModalRectWithCustomSizedTexture(x, y, 0, 0, xSize, ySize, xSize, ySize);

		//talker message
		GuiScrollable scroll = scrolls.get(0);
		ArrayList<Pair<Integer, Pair<Integer, Integer>>> f = scroll.getGrid();
		for(Pair<Integer, Pair<Integer, Integer>> indexLoc : f)
			this.drawString(fontRenderer, storedTalking.get(indexLoc.object1), indexLoc.object2.object1, indexLoc.object2.object2, EnumDialoguer.getType((Entity) talker).getColor());
		scroll.drawScrollBar();

		//TODO: Set this to player's color
		//player options
		scroll = scrolls.get(1);
		f = scroll.getGrid();
		for(Pair<Integer, Pair<Integer, Integer>> indexLoc : f)
		{
			if(indexLoc.object1 < startPointer.length && startPointer[indexLoc.object1] == indexLoc.object1 && (scroll.optionHovered(mouseX, mouseY) != indexLoc.object1 || (ticks/20) % 2 == 0))
				this.drawString(fontRenderer, ">", indexLoc.object2.object1, indexLoc.object2.object2, 10526880);		
			this.drawString(fontRenderer, "  " + storedOptions.get(indexLoc.object1), indexLoc.object2.object1, indexLoc.object2.object2, 10526880);		
		}
		scroll.drawScrollBar();
		
		for(Integer key : buttonPressed.keySet())
		{
			this.drawString(fontRenderer, key + ": " + buttonPressed.get(key).object1, buttonPressed.get(key).object2.object1, buttonPressed.get(key).object2.object2, 1000);
		}
		
		ticks++;	
	}
	
	public void setTalker(Entity talker)
	{
		if(talker instanceof IDialoguer)
		{
			this.talker = (IDialoguer) talker;
			
		}
	}
	
	public void updateMessage(@Nullable String dialogueName)
	{
		//adds dialouge to previous choices
		if(dialogueName == null)
		{
			
			previousOptions.clear();
			updateResponse(Dialogue.getDialogueFromString("generalStarter"));
			ArrayList<String> options = new ArrayList<String>() 
			{{
				for(int id : mc.player.getCapability(MinestuckCapabilities.PLAYER_DATA, null).getTalkerKnowledge())
					add(Dialogue.getDialogueFromId(id).getString());
			}};
			updateOptions(options);
			return;
		}
		if(!previousOptions.contains(dialogueName))
			previousOptions.add(dialogueName);
		else
			for(int i = previousOptions.indexOf(dialogueName) + 1; i < previousOptions.size(); i++)
				previousOptions.remove(i);
		
		DialogueContainer dialogue = Dialogue.getDialogueFromString(dialogueName);
		
		updateResponse(dialogue);
		
		updateOptions(dialogue.getOptions());
	}
	
	//clears response and fills with new response
	public void updateResponse(DialogueContainer dialogue)
	{
		storedTalking.clear();
		String message = dialogue.getTalkerMessage(talker, Minecraft.getMinecraft().player).getFormattedText();
		storedTalking.addAll(Compute.splitMessage(message, 325));
		scrolls.get(0).updateSize(storedTalking.size());
	}
	
	//clears options and what lines point to what option and fills with new options
	public void updateOptions(ArrayList<String> options)
	{
		startPointer = new int[0];
		storedOptions.clear();
		for(String option : options)
		{
			int optionStarts = storedOptions.size();
			storedOptions.addAll(Compute.splitMessage(Dialogue.getDialogueFromString(option).getConsoleMessage(talker, Minecraft.getMinecraft().player).getFormattedText(), 227));
			int[] temp = new int[storedOptions.size()];
			for(int i = 0; i < temp.length; i++)
			{
				if(i < startPointer.length)
					temp[i] = startPointer[i];
				else
					temp[i] = optionStarts;
			}
			startPointer = temp;
		}
		scrolls.get(1).updateSize(startPointer);
	}
	
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		
		for(GuiScrollable scroll : scrolls)
			if(scroll.mouseClicked(mouseX, mouseY, mouseButton))
				break;
		
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

		for(GuiScrollable scroll : scrolls)
		{
			int clicked = scroll.mouseReleased(mouseX, mouseY, state);
			if(clicked == -1) continue;
			DialogueContainer dialogue = null;
			if(previousOptions.size() > 0)
				dialogue = Dialogue.getDialogueFromString(Dialogue.getDialogueFromString(previousOptions.get(previousOptions.size()-1)).getOptions().get(clicked));
			else
			{
				int[] knowledge = mc.player.getCapability(MinestuckCapabilities.PLAYER_DATA, null).getTalkerKnowledge();
				//dialogue = 
			}
			if(dialogue.getDialogue() instanceof ExitDialogue)
				this.mc.setIngameFocus();
			else if(dialogue.getDialogue() instanceof BackDialogue)
				updateMessage(previousOptions.size() > 1 ? previousOptions.get(previousOptions.size() - 2) : null);
			else
				updateMessage(dialogue.getString());
		}
		buttonPressed.remove(state);
		if(state !=0) return;
		currentDrag.object1 = -1;
    }
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
		for(GuiScrollable scroll : scrolls)
			scroll.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		if(currentDrag.object1 == 0 && Math.abs(mouseY - currentDrag.object2.object2) > 10)
		{
			currentDrag.object2.object1 = mouseX;
			currentDrag.object2.object2 = mouseY;
		}
		
		buttonPressed.put(clickedMouseButton, new Pair(timeSinceLastClick, new Pair(mouseX, mouseY)));
    }
	
	@Override
	public void setWorldAndResolution(Minecraft mc, int width, int height)
	{
		super.setWorldAndResolution(mc, width, height);
		int tempX = x;
		int tempY = y;
		x = (width - xSize) / 2;
		y =  height - (ySize + 10);
		for(GuiScrollable scroll : scrolls)
		{
			scroll.x = x + (scroll.x - tempX);
			scroll.y = y + (scroll.y - tempY);
		}
	}
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        //So players cant exit by pressing escape 
    }
}
