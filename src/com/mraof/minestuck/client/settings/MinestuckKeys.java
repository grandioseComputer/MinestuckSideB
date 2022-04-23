package com.mraof.minestuck.client.settings;

import org.lwjgl.input.Keyboard;

import com.mraof.minestuck.client.gui.playerStats.GuiPlayerStats;
import com.mraof.minestuck.inventory.captchalouge.CaptchaDeckHandler;
import com.mraof.minestuck.network.CaptchaDeckPacket;
import com.mraof.minestuck.network.MinestuckChannelHandler;
import com.mraof.minestuck.network.MinestuckPacket;
import com.mraof.minestuck.network.MinestuckPacket.Type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public enum MinestuckKeys 
{
	
	TEST("test", Keyboard.KEY_U, 0, false)
	{
		public void execute(EntityPlayer player)
		{
			if(player.world.isRemote)
				MinestuckChannelHandler.sendToServer(MinestuckPacket.makePacket(Type.MEDIUM_DUNGEON));
        }
	},
	STATS("statsGui", Keyboard.KEY_U, 0, false)
	{
		public void execute(EntityPlayer player)
		{
			if(player.world.isRemote)
				GuiPlayerStats.openGui(false);
        }
	},
	EDIT("exitEdit", Keyboard.KEY_U, 0, false)
	{
		public void execute(EntityPlayer player)
		{
			if(player.world.isRemote)
				MinestuckChannelHandler.sendToServer(MinestuckPacket.makePacket(Type.CLIENT_EDIT));;
        }
	},
	CAPTCHA("catchalouge", Keyboard.KEY_U, 0, false)
	{
		public void execute(EntityPlayer player)
		{
			if(player.world.isRemote)
			{
				if(Minecraft.getMinecraft().currentScreen instanceof GuiContainer && ((GuiContainer) Minecraft.getMinecraft().currentScreen).getSlotUnderMouse() != null && ((GuiContainer) Minecraft.getMinecraft().currentScreen).getSlotUnderMouse().getHasStack())
					MinestuckChannelHandler.sendToServer(MinestuckPacket.makePacket(MinestuckPacket.Type.CAPTCHA, CaptchaDeckPacket.CAPTCHALOUGE_INV, ((GuiContainer) Minecraft.getMinecraft().currentScreen).getSlotUnderMouse().getSlotIndex()));
				else if(Minecraft.getMinecraft().currentScreen == null && !Minecraft.getMinecraft().player.getHeldItemMainhand().isEmpty())
					MinestuckChannelHandler.sendToServer(MinestuckPacket.makePacket(MinestuckPacket.Type.CAPTCHA, CaptchaDeckPacket.CAPTCHALOUGE));
			}
        }
	},
	EFFECT("aspectEffectToggle", Keyboard.KEY_U, 0, false)
	{
		public void execute(EntityPlayer player)
		{
			if(player.world.isRemote)
				MinestuckChannelHandler.sendToServer(MinestuckPacket.makePacket(Type.EFFECT_TOGGLE));

        }
	},
	SYLLADEX("sylladex", Keyboard.KEY_U, 0, false)
	{
		public void execute(EntityPlayer player)
		{
			if(player.world.isRemote && CaptchaDeckHandler.clientSideModus != null)
				Minecraft.getMinecraft().displayGuiScreen(CaptchaDeckHandler.clientSideModus.getGuiHandler());
        }
	}
	;
	
	private MinestuckKeys(String name, int keyCode, int time, boolean repeatable)
	{
		if(FMLCommonHandler.instance().getSide() == Side.CLIENT)
			//keybind = new KeyBinding("key." + name, keyCode, "key.categories.minestuck");
		this.time = time;
		this.repeatable = repeatable;
	}
	
	public abstract void execute(EntityPlayer player);
	
	public KeyBinding getKeybind()
	{
		return keybind;
	}
	
	public boolean down = false;
	
	public KeyBinding keybind;
	public int time;
	public boolean repeatable;

}
