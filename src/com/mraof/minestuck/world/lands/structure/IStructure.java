package com.mraof.minestuck.world.lands.structure;

import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraftforge.fml.common.FMLCommonHandler;

//for things that rotate
public interface IStructure 
{
	//TODO:CHANGE SO THAT IT DOESNT ONLY DO OVERWORLD
	public static final WorldServer worldServer = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0);
	public static final PlacementSettings setNorm = (new PlacementSettings()).setChunk(null).setIgnoreEntities(false).setIgnoreStructureBlock(false).setMirror(Mirror.NONE).setRotation(Rotation.NONE);
	public static final PlacementSettings set90 = (new PlacementSettings()).setChunk(null).setIgnoreEntities(false).setIgnoreStructureBlock(false).setMirror(Mirror.NONE).setRotation(Rotation.CLOCKWISE_90);
	public static final PlacementSettings set180 = (new PlacementSettings()).setChunk(null).setIgnoreEntities(false).setIgnoreStructureBlock(false).setMirror(Mirror.NONE).setRotation(Rotation.CLOCKWISE_180);
	public static final PlacementSettings set270 = (new PlacementSettings()).setChunk(null).setIgnoreEntities(false).setIgnoreStructureBlock(false).setMirror(Mirror.NONE).setRotation(Rotation.COUNTERCLOCKWISE_90);

	public static PlacementSettings getSetting(int rotation)
	{
		switch(rotation)
		{
		case 1:
			return set90;
		case 2:
			return set180;
		case 3:
			return set270;
		default:
			return setNorm;
		}
	}
}
