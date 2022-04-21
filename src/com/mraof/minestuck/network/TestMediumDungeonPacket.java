package com.mraof.minestuck.network;

import java.util.EnumSet;

import com.mraof.minestuck.world.lands.structure.MediumDungeon;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public class TestMediumDungeonPacket extends MinestuckPacket
{

	@Override
	public MinestuckPacket generatePacket(Object... data) {
		return this;
	}

	@Override
	public MinestuckPacket consumePacket(ByteBuf data) {
		return this;
	}

	@Override
	public void execute(EntityPlayer player) {
		MediumDungeon.T1.generate(player.world, player.world.rand, player.getPosition());
		
	}

	@Override
	public EnumSet<Side> getSenderSide() {
		return EnumSet.of(Side.CLIENT);
	}

}
