package com.mraof.minestuck.client.gui.components;

import java.io.IOException;
import java.util.ArrayList;

import com.mraof.minestuck.entity.dialogue.IDialoguer.EnumDialoguer;
import com.mraof.minestuck.util.Compute;
import com.mraof.minestuck.util.Debug;
import com.mraof.minestuck.util.Pair;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScrollable extends Gui
{

	public int x;
	public int y;
	public int width;
	public int height;
	public int rows;
	public int columns;
	public int[] stored;
	private int topLine = 0;
	private int hovered;
	public boolean hoverable;
	private Pair<Integer, Integer> mousePos; 
	
	public GuiScrollable(int x, int y, int widthIn, int heightIn, int rows, int columns, int[] stored, boolean hoverable)
	{
		this.x = x;
        this.y = y;
        this.width = widthIn;
        this.height = heightIn;
        this.rows = rows;
        this.columns = columns;
        this.stored = stored;
        this.hoverable = hoverable;
        this.hovered = -1;
	} 
	
	public GuiScrollable(int x, int y, int widthIn, int heightIn, int rows, int columns, int stored, boolean hoverable)
	{
		this.x = x;
        this.y = y;
        this.width = widthIn;
        this.height = heightIn;
        this.rows = rows;
        this.columns = columns;
        this.hoverable = hoverable;
        this.hovered = -1;
        this.stored = new int[stored];
        for(int i = 0; i < stored; i++)
        {
        	this.stored[i] = i;
        }
	}
	
	public void updateSize(int[] stored)
	{
		this.topLine = 0;
		this.stored = stored;
	}
	
	public void updateSize(int stored)
	{
		this.topLine = 0;
		this.stored = new int[stored];
        for(int i = 0; i < stored; i++)
        {
        	this.stored[i] = i;
        }
	}
	
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {	
		if(mouseButton != 0) return false;
		
		//drag talker message
		hovered = optionHovered(mouseX, mouseY);
		if(Compute.isWithin2D(mouseX, mouseY, x, y, x + width, y + height))
		{
			mousePos = new Pair<Integer, Integer>(mouseX, mouseY);
			return true;
		}
		return false;
    }
	
	public int mouseReleased(int mouseX, int mouseY, int state)
    {
		if(state == 0)
			mousePos = null;
		if(hovered != optionHovered(mouseX, mouseY))
			hovered = -1;
		return hovered;
    }
	
	public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
		if(mousePos != null && Math.abs(mouseY - mousePos.object2) > 10)
		{
			int newTopLine = topLine - (mouseY - mousePos.object2) / 10;
			topLine = (int) (newTopLine < 0 ? 0 : newTopLine + rows - 1 <= Math.ceil(stored.length / columns) - 1 ? newTopLine : Math.ceil(stored.length / columns) - rows < 0 ? 0 : Math.ceil(stored.length / columns) - rows);
			
			mousePos.object1 = mouseX;
			mousePos.object2 = mouseY;
		}	
    }
	
	public Pair<Integer, Integer> getPosOfGrid(int pos, boolean center)
	{
		int widthSize = width/columns;
		int heightSize = height/rows;
		return new Pair((x + widthSize*(pos % columns)) + (center ? widthSize/2 : 0), (y + heightSize * (pos / columns)) + (center ? heightSize/2 : 0));
	}
	
	/*
	 * drawing grid isnt really used
	public void drawGrid(FontRenderer renderer, int color)
	{
		
		//Debug.info("drawing grid with " + stored.size() + " items, from " + topLine + " to " + (maxShown));
		for(int i = topLine; i <= topLine + rows - 1 && i < stored.size(); i++)
		{
			if(stored.get(i) instanceof String)
			{ 
				Pair<Integer, Integer> pos = getPosOfGrid(i - topLine, false);
				this.drawString(renderer, (String) stored.get(i), pos.object1, pos.object2, color);
			}
		}
	}
	*/
	
	public void drawScrollBar()
	{
		if(mousePos == null) return;
		int storedRows = (int) Math.ceil(stored.length / columns);
		if(storedRows - this.rows <= 0) return;
		int sectionLength = this.height / storedRows;
		drawVerticalLine(x + width, y, y + height + 1, 0x80AAAAAA);
		for(int i = this.topLine; i < this.topLine + rows; i++)
			drawVerticalLine(x + width, y + (i * sectionLength), y + ((i + 1) * sectionLength) + 1, 0x802D2D2D);
	}
	
	public ArrayList<Pair<Integer, Pair<Integer, Integer>>> getGrid()
	{
		ArrayList<Pair<Integer, Pair<Integer, Integer>>> out = new ArrayList<>();
		for(int i = topLine; i < topLine + rows && i < stored.length; i++)
		{
			out.add(new Pair(i, getPosOfGrid(i - topLine, false)));
		}
		return out;
	}
	
	public int optionHovered(int mouseX, int mouseY)
	{
		if(!hoverable) return -1;
		int row = (int) Math.floor(topLine + 1.0 * (mouseY - this.y) * this.rows / this.height);
		int column = (int) Math.floor(1.0 * (mouseX - this.x) * this.columns / this.width);
		
		if(row >= this.rows || row < 0 || column >= this.columns || column < 0)
			return -1;
		
		int chosen = row * this.columns + column;
		return chosen < stored.length && chosen >= 0 ? stored[chosen] : -1;
	}
}
