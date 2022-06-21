package com.mraof.minestuck.network;

import java.util.ArrayList;
import java.util.EnumSet;

import com.mraof.minestuck.capabilities.MinestuckCapabilities;
import com.mraof.minestuck.world.lands.structure.MediumDungeon;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public class TalkerKnowledgePacket extends MinestuckPacket
{
	int[] dialogue;
	
	@Override
	public MinestuckPacket generatePacket(Object... data) {
		if(data == null || data.length <= 0 || data[0] == null || !(data[0] instanceof ArrayList) || ((ArrayList<Integer>)data[0]).size() <= 0 || !(((ArrayList<Integer>)data[0]).get(0) instanceof Integer)) 
			return this;
		
		this.data.writeInt(((ArrayList<Integer>)data[0]).size());
		for(int dialogue : ((ArrayList<Integer>)data[0]))
		{
			this.data.writeInt((int) dialogue);
		}
		return this;
	}

	@Override
	public MinestuckPacket consumePacket(ByteBuf data) {
		int length = data.readInt();
		int[] dialogueTemp = new int[length];
		for(int i = 0; i < length; i++)
		{
			dialogueTemp[i] = data.readInt();
		}
		dialogue = dialogueTemp;
		return null;
	}

	@Override
	public void execute(EntityPlayer player) {
		player.getCapability(MinestuckCapabilities.PLAYER_DATA, null).setTalkerKnowledge(dialogue);		
	}

	@Override
	public EnumSet<Side> getSenderSide() {
		return EnumSet.of(Side.SERVER);
	}

}
