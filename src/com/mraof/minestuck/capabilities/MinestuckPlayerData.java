package com.mraof.minestuck.capabilities;

import java.util.ArrayList;

import javax.annotation.Nullable;

import com.mraof.minestuck.capabilities.api.IMinestuckPlayerData;
import com.mraof.minestuck.entity.dialogue.Dialogue;

import net.minecraft.nbt.NBTTagCompound;

public class MinestuckPlayerData 
implements IMinestuckPlayerData
{
	int[] currentTalkerKnowledge;
	
	@Override
	public void setTalkerKnowledge(@Nullable int[] knowledge)
	{
		currentTalkerKnowledge = knowledge;
	}
	
	@Override
	public int[] getTalkerKnowledge()
	{ 
		//we need to add dialouge exiter regardless of whats in it
		ArrayList<Integer> knowledge = new ArrayList<Integer>()
				{{
					if(currentTalkerKnowledge != null && currentTalkerKnowledge.length > 0)
						for(int in : currentTalkerKnowledge)
							add(in);
				}};	
		if(knowledge.isEmpty())
			knowledge.add(Dialogue.convertId("backup"));

		knowledge.add(Dialogue.convertId("basicExit"));
		int[] toReturn = new int[knowledge.size()];
		for(int i = 0; i < knowledge.size(); i++)
			toReturn[i] = knowledge.get(i);
		return toReturn;
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
