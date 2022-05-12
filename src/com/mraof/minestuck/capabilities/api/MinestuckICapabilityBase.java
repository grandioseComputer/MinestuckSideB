package com.mraof.minestuck.capabilities.api;

import net.minecraft.nbt.NBTTagCompound;

public interface MinestuckICapabilityBase<OWNER>
{
	public NBTTagCompound writeToNBT();
	
	public void readFromNBT(NBTTagCompound var1);
	
	default public void setOwner(OWNER owner) {}
}