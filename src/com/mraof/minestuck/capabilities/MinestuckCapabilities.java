package com.mraof.minestuck.capabilities;

import com.mraof.minestuck.capabilities.api.IMinestuckPlayerData;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MinestuckCapabilities
{
	@CapabilityInject(value=IMinestuckPlayerData.class)
	public static final Capability<IMinestuckPlayerData> PLAYER_DATA = null;
	
	public static void registerCapabilities() {
		MinecraftForge.EVENT_BUS.register(MinestuckCapabilities.class);
		MinecraftForge.EVENT_BUS.register(MinestuckPlayerData.class);
        CapabilityManager.INSTANCE.register(IMinestuckPlayerData.class, new MinestuckCapabilityProvider.Storage(), MinestuckPlayerData::new);
	}
	
	@SubscribeEvent
    public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) 
	{
        if (event.getObject() instanceof EntityLivingBase) {
            //event.addCapability(new ResourceLocation("FFnBB", "effects"), new FFnBBCapabilityProvider<IBadgeEffects, EntityLivingBase>(BADGE_EFFECTS, (EntityLivingBase)event.getObject()));
        }
        if (event.getObject() instanceof EntityPlayer)
        	event.addCapability(new ResourceLocation("Minestuck", "PlayerData"), new MinestuckCapabilityProvider<IMinestuckPlayerData, EntityPlayer>(PLAYER_DATA, (EntityPlayer)event.getObject()));
    }
	
	@SubscribeEvent
    public static void onAttachCapabilitiesWorld(AttachCapabilitiesEvent<World> event) 
	{
		if(event.getObject().provider.getDimension() == 0);
			//event.addCapability(new ResourceLocation("FFnBB", "PartyData"), new FFnBBCapabilityProvider<IFFnBBPartyData, World>(PARTY_DATA, (World)event.getObject()));
    }

}
