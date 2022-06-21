package com.mraof.minestuck.capabilities.api;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;

public interface IMinestuckPlayerData
extends MinestuckICapabilityBase<EntityPlayer>
{

	int[] getTalkerKnowledge();

	void setTalkerKnowledge(@Nullable int[] knowledge);

}
