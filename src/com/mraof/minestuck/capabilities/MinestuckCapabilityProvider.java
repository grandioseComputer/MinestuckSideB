package com.mraof.minestuck.capabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mraof.minestuck.capabilities.api.MinestuckICapabilityBase;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class MinestuckCapabilityProvider<HANDLER extends MinestuckICapabilityBase<OWNER>, OWNER>
implements ICapabilitySerializable<NBTTagCompound>
{
	private final Capability<HANDLER> capability;
	private final HANDLER instance;
	
	public MinestuckCapabilityProvider(Capability<HANDLER> capability, OWNER owner)
	{
		this.capability = capability;
        this.instance = (HANDLER)this.getCapability().getDefaultInstance();
        this.instance.setOwner(owner);
		
	}
	
	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability != this.getCapability()) return false;
        return true;
	}

	@Override
	@Nullable
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        Object object = null;
        if (capability == this.getCapability()) {
            object = this.getCapability().cast(this.instance);
            return (T)object;
        }
        return (T)object;
	}

	@Override
	public NBTTagCompound serializeNBT() {
        return (NBTTagCompound)this.getCapability().writeNBT(this.instance, null);
    }

	@Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.getCapability().readNBT(this.instance, null, (NBTBase)nbt);
    }
	
	public Capability<HANDLER> getCapability() {
        return this.capability;
    }
	
	public static class Storage<HANDLER extends MinestuckICapabilityBase>
	implements Capability.IStorage<HANDLER>
	{
		@Nullable
		@Override
		public NBTBase writeNBT(Capability<HANDLER> capability, HANDLER instance, EnumFacing side)
		{
			return instance.writeToNBT();
		}

		@Override
		public void readNBT(Capability<HANDLER> capability, HANDLER instance, EnumFacing side, NBTBase nbt) 
		{
			instance.readFromNBT((NBTTagCompound)nbt);	
		}
	}
}
