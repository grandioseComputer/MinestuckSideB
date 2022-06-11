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
import com.mraof.minestuck.entity.consort.EnumConsort.MerchantType;
import com.mraof.minestuck.world.MinestuckDimensionHandler;
import com.mraof.minestuck.world.lands.LandAspectRegistry;
import com.mraof.minestuck.world.lands.terrain.TerrainLandAspect;
import com.mraof.minestuck.world.lands.title.TitleLandAspect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.text.ITextComponent;

public class Dialogue 
{
	private static final Hashtable<String, DialogueContainer> dialogue = new Hashtable<>();
	private static int idCounter = 0;
	
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
	
	public static DialogueContainer getDialogueFromString(String name)
	{
		return dialogue.get(name);
	}
	
	public static DialogueContainer getDialogueFromId(int id)
	{
		return dialogue.get(dialogue.keySet().toArray()[id]);
	}
	
	public static String getRandomDialogue(IDialoguer talker, EntityPlayer player, String...except)
	{
		LandAspectRegistry.AspectCombination aspects = MinestuckDimensionHandler.getAspects(talker.getHomeDimension());
		
		List<DialogueContainer> list = new ArrayList<>();
		
		for(String key : dialogue.keySet())
		{
			DialogueContainer message = dialogue.get(key);
			boolean alreadyGotten = false;
			for(String exception : except)
				if(key.contentEquals(exception))
					alreadyGotten = true;
			if(alreadyGotten)
				continue;
			if(message.reqLand && aspects == null)
				continue;
			if(message.dialoguerRequirement != null && !message.dialoguerRequirement.contains(EnumDialoguer.getType((Entity) talker)))
				continue;
			if(message.terrainTypeRequirement != null && !message.terrainTypeRequirement.contains(aspects.aspectTerrain.getPrimaryVariant()))
				continue;
			if(message.aspectTypeRequirement != null && !message.aspectTypeRequirement.contains(aspects.aspectTitle.getPrimaryVariant()))
				continue;
			if(message.terrainRequirement != null && !message.terrainRequirement.contains(aspects.aspectTerrain))
				continue;
			if(message.aspectRequirement != null && !message.aspectRequirement.contains(aspects.aspectTitle))
				continue;
			//if(message.merchantRequirement == null && consort.merchantType != EnumConsort.MerchantType.NONE
			//		|| message.merchantRequirement != null && !message.merchantRequirement.contains(consort.merchantType))
				//continue;
			if(message.additionalRequirement != null && !message.additionalRequirement.apply(talker))
				continue;
			list.add(message);
		}
		return WeightedRandom.getRandomItem(((Entity)talker).world.rand, list).getString();
	}
	
	public static ArrayList<String> getRandomDialogues(IDialoguer talker, EntityPlayer player, int amount)
	{
		ArrayList<String> dialogues = new ArrayList<>();
		for(int i = 0; i < amount; i++)
			dialogues.add(getRandomDialogue(talker, player, (String[]) dialogues.toArray()));
		return dialogues;
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
			dialogueId = idCounter++;
		}
		
		private DialogueType dialogue;
		private ArrayList<String> leadingDialogues;
		private int dialogueId;
		
		private boolean reqLand;
		
		private Set<TerrainLandAspect> terrainTypeRequirement;
		private Set<TitleLandAspect> aspectTypeRequirement;
		private Set<TerrainLandAspect> terrainRequirement;
		private Set<TitleLandAspect> aspectRequirement;
		private EnumSet<EnumDialoguer> dialoguerRequirement;
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
			dialoguerRequirement = EnumSet.of(types[0], types);
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
		
		public ArrayList<String> getOptions()
		{
			return leadingDialogues;
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
		
		public DialogueType getDialogue()
		{
			return dialogue;
		}
		
		public int getDialogueId()
		{
			return dialogueId;
		}
	}
	
	public interface AuxRequirement
	{
		boolean apply(IDialoguer talker);
	}
	
}
