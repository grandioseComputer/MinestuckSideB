package com.mraof.minestuck.capabilities;

import com.mraof.minestuck.capabilities.api.IMinestuckPlayerData;

import net.minecraft.nbt.NBTTagCompound;

public class MinestuckPlayerData 
implements IMinestuckPlayerData
{
	int[] currentTalkerKnowledge;
	
	@Override
	public void setTalkerKnowledge(int[] knowledge)
	{
		currentTalkerKnowledge = knowledge;
	}
	
	@Override
	public int[] getTalkerKnowledge()
	{
		return currentTalkerKnowledge;
	}
	
	@Override
	public NBTTagCompound writeToNBT() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void readFromNBT(NBTTagCompound var1) {
		// TODO Auto-generated method stub
		
	}
}
