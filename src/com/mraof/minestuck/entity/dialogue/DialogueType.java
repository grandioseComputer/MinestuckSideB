package com.mraof.minestuck.entity.dialogue;

import com.mraof.minestuck.entity.consort.EntityConsort;
import com.mraof.minestuck.network.skaianet.SburbConnection;
import com.mraof.minestuck.network.skaianet.SburbHandler;
import com.mraof.minestuck.util.IdentifierHandler;
import com.mraof.minestuck.util.MinestuckPlayerData;
import com.mraof.minestuck.util.Title;
import com.mraof.minestuck.util.IdentifierHandler.PlayerIdentifier;
import com.mraof.minestuck.world.WorldProviderLands;
import com.mraof.minestuck.world.lands.gen.ChunkProviderLands;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public abstract class DialogueType 
{
	
	public abstract String getString();
	
	//what the talkers say
	public abstract ITextComponent getTalkerMessage(IDialoguer talker, EntityPlayer player);
	//what the player says; will show up in tooltips if shift is pressed
	public abstract ITextComponent getPlayerMessage(IDialoguer talker, EntityPlayer player);
	//what the option is actually called
	public abstract ITextComponent getConsoleMessage(IDialoguer talker, EntityPlayer player);
	
	/*
	 * TODO: add a makeMessage() that uses args to replace certain things in sentences
	 */
	
	private static ITextComponent createMessage(IDialoguer talker, EntityPlayer player, String unlocalizedMessage,
			String[] args, int messageRelayer)
	{
		String s = EntityList.getEntityString((Entity) talker);
		if(s == null)
		{
			s = "generic";
		}
		
		Object[] obj = new Object[args.length];
		//SburbConnection c = SburbHandler.getConnectionForDimension(consort.homeDimension);
		//Title title = c == null ? null : MinestuckPlayerData.getData(c.getClientIdentifier()).title;
		for(int i = 0; i < args.length; i++)
		{
			/*
			if(args[i].equals("playerNameLand"))
			{
				if(c != null)
					obj[i] = c.getClientIdentifier().getUsername();
			}
			*/
		}
	
		TextComponentTranslation message = new TextComponentTranslation("dialogue." + (messageRelayer <= 0 ? "talker." : messageRelayer == 1 ? "player." : "console.") + unlocalizedMessage, obj);
		
		return message;
	}
		
	public static class BasicDialogue extends DialogueType
	{
		
		protected String unlocalizedMessage;
		protected String[] args;
		
		public BasicDialogue(String message, String... args)
		{
			this.unlocalizedMessage = message;
			this.args = args;
		}

		@Override
		public String getString()
		{
			return unlocalizedMessage;
		}
		
		@Override
		public ITextComponent getTalkerMessage(IDialoguer talker, EntityPlayer player) {
			return createMessage(talker, player, unlocalizedMessage, args, 0);
		}

		@Override
		public ITextComponent getPlayerMessage(IDialoguer talker, EntityPlayer player) {
			return createMessage(talker, player, unlocalizedMessage, args, 1);
		}

		@Override
		public ITextComponent getConsoleMessage(IDialoguer talker, EntityPlayer player) {
			return createMessage(talker, player, unlocalizedMessage, args, 2);
		}
	}
	
}
