package com.mraof.minestuck.entity.dialogue;

import java.util.ArrayList;

import com.mraof.minestuck.entity.consort.EntityConsort;
import com.mraof.minestuck.entity.consort.EntityIguana;
import com.mraof.minestuck.entity.consort.EntityNakagator;
import com.mraof.minestuck.entity.consort.EntitySalamander;
import com.mraof.minestuck.entity.consort.EntityTurtle;

import net.minecraft.entity.Entity;
import net.minecraft.util.text.TextFormatting;

public interface IDialoguer 
{
	
	public ArrayList<String> getDialogue();
	
	public int getHomeDimension();

	public enum EnumDialoguer
	{
		SALAMANDER(EntitySalamander.class, "salamander", 16776960),
		TURTLE(EntityTurtle.class, "turtle", 16711935),
		NAKAGATOR(EntityNakagator.class, "nakagator", 16711680),
		IGUANA(EntityIguana.class, "iguana", 65535);

		private final Class<? extends IDialoguer> talkerClass;
		private final String name;
		private final int color;
		
		EnumDialoguer(Class<? extends IDialoguer> talker, String name, int color)
		{
			talkerClass = talker;
			this.color = color;
			this.name = name;
		}
		
		public boolean isDialoguer(Entity talker)
		{
			return talkerClass.isInstance(talker);
		}
		
		public int getColor()
		{
			return color;
		}
		
		public String getName()
		{
			return name;
		}
		
		public Class<? extends IDialoguer> getConsortClass()
		{
			return talkerClass;
		}
		
		public static EnumDialoguer getType(Entity talker)
		{
			for(EnumDialoguer type : EnumDialoguer.values())
				if(type.isDialoguer(talker))
					return type;
			return null;
	
		}
	}
	
}
