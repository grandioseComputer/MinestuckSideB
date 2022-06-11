package com.mraof.minestuck.capabilities.api;

import net.minecraft.entity.player.EntityPlayer;

public interface IMinestuckPlayerData
extends MinestuckICapabilityBase<EntityPlayer>
{

	int[] getTalkerKnowledge();

	void setTalkerKnowledge(int[] knowledge);

}
