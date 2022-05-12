package com.mraof.minestuck.entity.dialogue;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mraof.minestuck.entity.consort.EntityConsort;
import com.mraof.minestuck.entity.consort.EnumConsort;
import com.mraof.minestuck.entity.dialogue.DialogueType.BasicDialogue;
import com.mraof.minestuck.entity.dialogue.IDialoguer.EnumDialoguer;
import com.mraof.minestuck.util.Debug;
import com.mraof.minestuck.entity.consort.ConsortDialogue.ConsortRequirement;
import com.mraof.minestuck.entity.consort.ConsortDialogue.DialogueWrapper;
import com.mraof.minestuck.entity.consort.EnumConsort.MerchantType;
import com.mraof.minestuck.world.lands.LandAspectRegistry;
import com.mraof.minestuck.world.lands.terrain.TerrainLandAspect;
import com.mraof.minestuck.world.lands.title.TitleLandAspect;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.text.ITextComponent;

public class Dialogue 
{

	private static final Hashtable<String, DialogueContainer> dialogue = new Hashtable<>();
	
	public static void init()
	{
		//default
		addMessage("backup").options("backup", "notAvailable");
		addMessage("notAvailable").options("backup");
	}
	
	public static DialogueContainer addMessage(String message, String... args)
	{
		return addMessage(new BasicDialogue(message, args));
	}
	
	public static DialogueContainer addMessage(DialogueType message)
	{
		DialogueContainer msg = new DialogueContainer();
		msg.dialogue = message;
		dialogue.put(message.getString(), msg);
		
		return msg;
	} 
	
	public static TitleLandAspect[] allExcept(TitleLandAspect... aspects)
	{
		Set<TitleLandAspect> set = new HashSet<>();
		names: for (String name : LandAspectRegistry.getNamesTitle())
		{
			for (TitleLandAspect aspect : aspects)
				if (aspect.getPrimaryName().equals(name))
					continue names;
			set.add(LandAspectRegistry.fromNameTitle(name));
		}
		return set.toArray(new TitleLandAspect[set.size()]);
	}
	
	public static DialogueContainer getMessageFromString(String name)
	{
		return dialogue.get(name);
	}
	
	public static class DialogueContainer extends WeightedRandom.Item
	{

		public DialogueContainer()
		{
			this(10);
		}
		
		public DialogueContainer(int itemWeightIn) 
		{
			super(itemWeightIn);
		}
		
		private DialogueType dialogue;
		private ArrayList<String> leadingDialogues;
		
		private boolean reqLand;
		
		private Set<TerrainLandAspect> terrainTypeRequirement;
		private Set<TitleLandAspect> aspectTypeRequirement;
		private Set<TerrainLandAspect> terrainRequirement;
		private Set<TitleLandAspect> aspectRequirement;
		private EnumSet<EnumDialoguer> consortRequirement;
		private EnumSet<MerchantType> merchantRequirement;
		private AuxRequirement additionalRequirement;
		
		public DialogueContainer landTerrain(TerrainLandAspect... aspects)
		{
			for(TerrainLandAspect aspect : aspects)
				if(aspect == null)
				{
					Debug.warn("Land aspect is null for consort message " + dialogue.getString() + ", this is probably not intended");
					break;
				}
			reqLand = true;
			terrainTypeRequirement = Sets.newHashSet(aspects);
			return this;
		}
		
		public DialogueContainer landTerrainSpecific(TerrainLandAspect... aspects)
		{
			for(TerrainLandAspect aspect : aspects)
				if(aspect == null)
				{
					Debug.warn("Land aspect is null for consort message " + dialogue.getString() + ", this is probably not intended");
					break;
				}
			reqLand = true;
			terrainRequirement = Sets.newHashSet(aspects);
			return this;
		}
		
		public DialogueContainer landTitle(TitleLandAspect... aspects)
		{
			for(TitleLandAspect aspect : aspects)
				if(aspect == null)
				{
					Debug.warn("Land aspect is null for consort message " + dialogue.getString() + ", this is probably not intended");
					break;
				}
			reqLand = true;
			aspectTypeRequirement = Sets.newHashSet(aspects);
			return this;
		}
		
		public DialogueContainer landTitleSpecific(TitleLandAspect... aspects)
		{
			for(TitleLandAspect aspect : aspects)
				if(aspect == null)
				{
					Debug.warn("Land aspect is null for consort message " + dialogue.getString() + ", this is probably not intended");
					break;
				}
			reqLand = true;
			aspectRequirement = Sets.newHashSet(aspects);
			return this;
		}
		
		public DialogueContainer options(String... options)
		{
			leadingDialogues = Lists.newArrayList(options);
			return this;
		}
		
		public DialogueContainer talkers(EnumDialoguer... types)
		{
			consortRequirement = EnumSet.of(types[0], types);
			return this;
		}
		
		//might be unused - actually can make merchants out of prospitians and dersites so idk
		public DialogueContainer type(MerchantType... types)
		{
			merchantRequirement = EnumSet.of(types[0], types);
			return this;
		}
		
		public DialogueContainer auxReq(AuxRequirement req)
		{
			additionalRequirement = req;
			return this;
		}
		
		public DialogueContainer weight(int weight)
		{
			itemWeight = weight;
			return this;
		}
		
		public ITextComponent getTalkerMessage(IDialoguer talker, EntityPlayer player)
		{
			return dialogue.getTalkerMessage(talker, player);
		}
		
		public ITextComponent getPlayerMessage(IDialoguer talker, EntityPlayer player)
		{
			return dialogue.getPlayerMessage(talker, player);
		}
		
		public ITextComponent getConsoleMessage(IDialoguer talker, EntityPlayer player)
		{
			return dialogue.getConsoleMessage(talker, player);
		}
		
		public String getString()
		{
			return dialogue.getString();
		}
	}
	
	public interface AuxRequirement
	{
		boolean apply(IDialoguer talker);
	}
	
}
