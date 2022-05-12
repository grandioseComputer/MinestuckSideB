package com.mraof.minestuck.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.stream.Stream;

/*
import com.blankThought.FFnBB.capabilities.FFnBBCapabilities;
import com.blankThought.FFnBB.fraymotifs.BasicSpell;
import com.blankThought.FFnBB.fraymotifs.SpellEffects;
import com.blankThought.FFnBB.fraymotifs.SpellLocation;
*/

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.DimensionManager;

public class Compute 
{
	public static double getDistanceIn3D(double x1, double y1, double z1, double x2, double y2, double z2)
	{
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2));
	}
	
	public static boolean isWithin2D(int isInX, int isInY, int x1, int y1, int x2, int y2)
	{
		int maxX = x1;
		int maxY = y1;
		int minX = x2;
		int minY = y2;
		if(x1 < x2)
		{
			maxX = x2;
			minX = x1;
		}
		if(y1 < y2)
		{
			maxY = y2;
			minY = y1;
		}
		
		return isInX >= minX && isInX <= maxX && isInY >= minY && isInY <= maxY;
	}
	
	/*TODO: bring this stuff back when you start fraymotifs
	public static boolean isAlly(EntityLivingBase entity, BasicSpell spell)
	{
		if(spell.caster != null && entity.equals(spell.caster)) return true;
    	if(spell.caster == null || spell.caster.getCapability(FFnBBCapabilities.PLAYER_DATA, null).getParty().contentEquals("")) return false;
    	return DimensionManager.getWorld(0).getCapability(FFnBBCapabilities.PARTY_DATA, null).getParties().get(spell.caster.getCapability(FFnBBCapabilities.PLAYER_DATA, null).getParty()).contains(entity.getCachedUniqueIdString());
	}
	
	public static boolean isAlly(EntityLivingBase entity, SpellEffects spell)
	{
		if(spell.caster != null && entity.equals(spell.caster)) return true;
    	if(spell.caster == null || spell.caster.getCapability(FFnBBCapabilities.PLAYER_DATA, null).getParty().contentEquals("")) return false;
    	return DimensionManager.getWorld(0).getCapability(FFnBBCapabilities.PARTY_DATA, null).getParties().get(spell.caster.getCapability(FFnBBCapabilities.PLAYER_DATA, null).getParty()).contains(entity.getCachedUniqueIdString());
	}
	
	public static boolean isAlly(EntityLivingBase entity, SpellLocation spell)
	{
		if(spell.caster != null && entity.equals(spell.caster)) return true;
    	if(spell.caster == null || spell.caster.getCapability(FFnBBCapabilities.PLAYER_DATA, null).getParty().contentEquals("")) return false;
    	return DimensionManager.getWorld(0).getCapability(FFnBBCapabilities.PARTY_DATA, null).getParties().get(spell.caster.getCapability(FFnBBCapabilities.PLAYER_DATA, null).getParty()).contains(entity.getCachedUniqueIdString());
	}
	
	public static double[] getPosInShape(SpellLocation loc)
    {
		Random rand = new Random();

    	switch(loc.shape)
    	{
    		case TRAIL:
    		case BLOCK:
    			return new double[] {rand.nextDouble()*loc.range - (loc.range/2), rand.nextDouble()*loc.range - (loc.range/2), rand.nextDouble()*loc.range - (loc.range/2)};
    		case SPHERE:
    		{
    			double temp = 0;
    			double x = rand.nextDouble() * loc.range - (loc.range/2);
    			temp = Math.sqrt((loc.range * loc.range/4) - (x*x));
    			double y = rand.nextDouble() * temp * 2 - temp;
    			temp = Math.sqrt((loc.range * loc.range/4)- (x*x + y*y));
    			double z = rand.nextDouble() * temp * 2 - temp;
    			
    			double ratio = Compute.getDistanceIn3D(x, y, z, 0, 0, 0) * 2 / loc.range;
    			if(ratio > 1)
    			{
    				ratio = 1 -(ratio - 1);
    				//return new double[]{0, 0, 0};
    			}
    			return new double[] {x, y, z};
    		}
    		case DOWN_PYRAMID:
    		{
    			double temp = 0;
    			double y = rand.nextDouble()*loc.range;
    			temp = y/1.73;
    			double x = rand.nextDouble() * temp - temp/2;
    			temp = Math.sqrt((temp * temp)- x*x);
    			double z = rand.nextDouble() * temp - temp/2;
    			return new double[] {x, y, z};
    		}    		
    		case PYRAMID:
    		{
    			double temp = 0;
    			double y = rand.nextDouble()*loc.range;
    			temp = (loc.range - y)/1.73;
    			double x = rand.nextDouble() * temp * 2- temp;
    			temp = Math.sqrt((temp * temp) - x*x);
    			double z = rand.nextDouble() * temp * 2 - temp;
    			return new double[] {x, y, z};
    		}    
    		//TODO: something with these 2 but not block
    		case SIDE_PYRAMID:
    		case SPECIAL:
    	}
    	return new double[] {0, 0, 0};
    }
    */
	
	public static HashMap<Boolean, ArrayList<String>> getFileNamesInFolder(String folder)
	{
		HashMap<Boolean, ArrayList<String>> fileNames = new HashMap<Boolean, ArrayList<String>>() {{
			put(true, new ArrayList<String>());
			put(false, new ArrayList<String>());
		}};
		
		URI uri;
		try {
			uri = MinecraftServer.class.getResource(folder).toURI();
		} catch (URISyntaxException e) {
			return fileNames;
		}
        Path myPath;
        FileSystem fileSystem = null;
        if (uri.getScheme().equals("jar")) {
        	try 
        	{
        		fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
        		myPath = fileSystem.getPath(folder);
        	}
        	catch (IOException e)
        	{
        		return fileNames;
        	}
        } 
        else
            myPath = Paths.get(uri);
        Stream<Path> walk;
		try 
		{
			walk = Files.walk(myPath, 1);
		}
		catch (IOException e) 
		{
			return fileNames;
		}
		Iterator<Path> walker = walk.iterator();
		while(walker.hasNext())
		{
			Path childFileOrFolder = walker.next();
			boolean isFile = childFileOrFolder.getFileName().toString().contains(".");
        	String name = childFileOrFolder.getFileName().toString();
        	if(isFile || !name.contentEquals(folder.substring(folder.length() - (name.length() + 1), folder.length()-1)))
        		fileNames.get(isFile).add(name);
		}
	    walk.close();
	    try
	    {
	    	if(fileSystem != null)
	    		fileSystem.close();
	    }
	    catch (IOException e1)
	    {}
		return fileNames;
	}
	
	public static InputStream getFileFromString(String path)
	{
		
		InputStream inputStream = null;
		try
		{
            inputStream = MinecraftServer.class.getResourceAsStream(path);
		}
		finally
		{
			
		}
		return inputStream;
		/*
		URL url = MinecraftServer.class.getResource(path);
		String bleh = "";
		try {
			bleh = URLDecoder.decode(url.getFile(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			WorldGenMediumDungeon.unsuported = true;
			bleh = url.getFile();
		}
		bleh = bleh.substring(0, 6).contains("file:/") ? bleh.substring(6) : bleh;
		WorldGenMediumDungeon.filePath = bleh;
		WorldGenMediumDungeon.justFile = new File(bleh);
		return new File(bleh);
		*/
	}
}
