package com.mraof.minestuck.world.lands.structure;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.io.IOUtils;

import com.mraof.minestuck.util.Compute;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class MediumDungeon extends WorldGenerator implements IStructure
{	
	public int tier;
	public static MediumDungeon T1 = new MediumDungeon(0);
	public static MediumDungeon T2 = new MediumDungeon(1);
	public static MediumDungeon T3 = new MediumDungeon(2);
	//<type of room, room shtik>
	public static HashMap<String, HashMap<String, Template>> ROOMS = new HashMap<String, HashMap<String, Template>>() 
	{{
		put("3_way", new HashMap<String, Template>());
		put("4_way", new HashMap<String, Template>());
		put("big", new HashMap<String, Template>());
		put("dead_end", new HashMap<String, Template>());
		put("door", new HashMap<String, Template>());
		put("down", new HashMap<String, Template>());
		put("elbow", new HashMap<String, Template>());
		put("full", new HashMap<String, Template>());
		put("straight", new HashMap<String, Template>());
		put("treasure_1", new HashMap<String, Template>());
		put("treasure_2", new HashMap<String, Template>());
		put("treasure_3", new HashMap<String, Template>());
		put("up", new HashMap<String, Template>());
		put("connector", new HashMap<String, Template>());
	}};
	//TODO: maybe change how dungeons generate based (any tier can be any shape)
	public static HashMap<String, ArrayList<floorPlans>> FLOORS = new HashMap<String, ArrayList<floorPlans>>();
	
	public MediumDungeon(int tier)
	{
		this.tier = tier;
	}
	
	//used to test whether a floor has errors; is not perfect
	//TODO: make this only list floors that are incomplete
	public void testWalkThrough(World world)
	{
		for(floorPlans floor : FLOORS.get("circle"))
		{
			int[] stairs = floor.getStairs().get(floor.getStairs().keySet().toArray()[0])[0];
			HashMap<Integer, boolean[]> listOfRooms = new HashMap<Integer, boolean[]>();
			int amountOfRooms = 0;
			for(int i = 0; i < floor.size[0]; i++) 
			{
				listOfRooms.put(i, new boolean[floor.size[1]]);
				for(int j = 0; j < floor.size[1]; j++)
				{
					if(!(floor.plan[i][j].contains("C") || floor.plan[i][j].contentEquals("")))
						amountOfRooms ++;
				}
			}
			int reachedRooms = runThrough(listOfRooms, floor.getPlan(), stairs[0], stairs[1], stairs[0], stairs[1]);
			
			ArrayList<int[]> connections = (ArrayList<int[]>)floor.connections.clone();
			for(int i = 0; 0 < connections.size() && reachedRooms < amountOfRooms; i++)
			{
				int[] currentConnection = connections.get(0);
				connections.remove(0);
				
				reachedRooms += runThrough(listOfRooms, floor.plan, currentConnection[0], currentConnection[1], currentConnection[0], currentConnection[1]);
			}
			for(EntityPlayer player : world.playerEntities)
			{	
				player.sendMessage(new TextComponentString(floor.getFile().getName() + ": " + reachedRooms + " reached vs " + amountOfRooms + " required"));
			}
		}
	}
	
	//TODO: create shell too and make it work with entrance
	@Override
	public boolean generate(World world, Random rand, BlockPos position) 
	{
		//TODO: set random shtik / boss + select shape based on tier + make multiple shtiks
		String shtik = "basic";
		String shape = "circle";
		int shellRotation = rand.nextInt(4);
		int floorsToMake = rand.nextInt(2) + 3 + 2*this.tier;
		ArrayList<floorPlans> usedFloors = new ArrayList<floorPlans>();
		floorPlans currentFloor = FLOORS.get(shape).get(rand.nextInt(FLOORS.get(shape).size()));
		usedFloors.add(currentFloor);
		ArrayList<int[]> currentStairs = new ArrayList<>();
		if(tier >= 2)
			/*add something for custom structured dungeon*/;
		//TODO: make the first floor generated (the bottom floor) be the boss room if tier 1
		int f = 0;
		while(floorsToMake > f)
		{
			int amountOfRooms = 0;
			for(int i = 0; i < currentFloor.size[0]; i++)
			{
				for(int j = 0; j < currentFloor.size[1]; j++)
				{
					String[] template = readRoom(currentFloor.plan[i][j]);
					int rotation = Integer.parseInt(template[1]);
					if(template[0].contentEquals("entrance"))
						template[0] = f == floorsToMake/2 && rotation == shellRotation ? "straight" : "dead_end";
					if(template[0].contentEquals("connector"))
						template[0] = "";
					
					//TODO: make this randomly get from shtik list
					Template room = ROOMS.get(template[0]) == null || ROOMS.get(template[0]).get("basic") == null ? new Template() : ROOMS.get(template[0]).get("basic");
					BlockPos pos = new BlockPos(position.getX() + (i - currentFloor.size[0]/2) * 7, position.getY() + 5 * (f - floorsToMake/2), position.getZ() + (j - currentFloor.size[1]/2) * 7);
					
					placeTemplate(world, room, pos, rotation);
					amountOfRooms += Math.ceil(room.getSize().getX() / 7) * Math.ceil(room.getSize().getZ() / 7);
				}
			}
			//TODO: make dependent on current shape + make stairs based on shtik
			// place the stairs leading down
			//choose next floor, see if the connections are blocked, if not place 
			//random amount of stairs and log in current stairs, else repeat
			//also run through floor
			
			if(currentStairs.isEmpty() && currentFloor.sections > 1)
				for(int[] connector : currentFloor.connections) 
				{
					
					String[] template = readRoom(currentFloor.plan[connector[0]][connector[1]]);
					int rotation = Integer.parseInt(template[1]);
					//TODO: make this randomly get from shtik list
					Template room = ROOMS.get(template[0]) == null || ROOMS.get(template[0]).get("basic") == null ? new Template() : ROOMS.get(template[0]).get("basic");
					BlockPos pos = new BlockPos(position.getX() + (int)(connector[0] - currentFloor.size[0]/2) * 7, position.getY() + 5 * (f - floorsToMake/2), position.getZ() + (int)(connector[1] - currentFloor.size[1]/2) * 7);
					
					placeTemplate(world, room, pos, rotation);
				}
			HashMap<Integer, boolean[]> listOfRooms = new HashMap<Integer, boolean[]>();
			for(int i = 0; i < currentFloor.size[0]; i++)
				listOfRooms.put(i, new boolean[currentFloor.size[1]]);
			int reachedRooms = 0;
			for(int[] stairs : currentStairs)
			{
				if(currentFloor.sections > 1)
					reachedRooms += runThrough(listOfRooms, currentFloor.plan, stairs[0], stairs[1], stairs[0], stairs[1]);
				Template room = ROOMS.get("down").get("basic");
				int rotation = Integer.parseInt(readRoom(currentFloor.plan[stairs[0]][stairs[1]])[1]);
				BlockPos pos = new BlockPos(position.getX() + (stairs[0] - currentFloor.size[1]/2) * 7, position.getY() + 5 * (f - floorsToMake/2), position.getZ() + (stairs[1] - currentFloor.size[1]/2) * 7);

				placeTemplate(world, room, pos, rotation);
			}
			
			ArrayList<int[]> connections = (ArrayList<int[]>)currentFloor.connections.clone();
			if(currentFloor.sections <= 1)
				connections.clear();
			for(int i = 0; 0 < connections.size() && reachedRooms < amountOfRooms; i++)
			{
				int randomRoom = rand.nextInt(connections.size());
				int[] currentConnection = connections.get(randomRoom);
				connections.remove(randomRoom);
				
				reachedRooms += runThrough(listOfRooms, currentFloor.plan, currentConnection[0], currentConnection[1], currentConnection[0], currentConnection[1]);

				Template room = ROOMS.get("connector").get("basic");
				int rotation = Integer.parseInt(readRoom(currentFloor.plan[currentConnection[0]][currentConnection[1]])[1]);
				BlockPos pos = new BlockPos(position.getX() + (currentConnection[0] - currentFloor.size[1]/2) * 7, position.getY() + 5 * (f - floorsToMake/2), position.getZ() + (currentConnection[1] - currentFloor.size[1]/2) * 7);

				placeTemplate(world, room, pos, rotation);
			}
			while(f < floorsToMake - 1)
			{
				floorPlans nextFloor = (floorPlans) currentFloor.stairs.keySet().toArray()[rand.nextInt(currentFloor.stairs.size())];
				
				if(usedFloors.contains(nextFloor)) continue;
				ArrayList<int[]> stairsAvailable = new ArrayList<int[]>();
				for(int[] stairs : currentFloor.stairs.get(nextFloor))
				{
					boolean isAvailable = true;
					for(int[] usedStairs : currentStairs)
						if(stairs[0] == usedStairs[0] && stairs[1] == usedStairs[1])
							isAvailable = false;
					if(isAvailable)
						stairsAvailable.add(stairs);
				}
				
				if(stairsAvailable.isEmpty()) continue;
				currentStairs.clear();
				for(int i = 1; 0 < stairsAvailable.size() && rand.nextDouble() <= 1.0/i; i++)
				{
					int[] stairs = stairsAvailable.get(rand.nextInt(stairsAvailable.size()));
					currentStairs.add(stairs);
					stairsAvailable.remove(stairs);
					
					Template room = ROOMS.get("up").get("basic");
					int rotation = Integer.parseInt(readRoom(currentFloor.plan[stairs[0]][stairs[1]])[1]);
					BlockPos pos = new BlockPos(position.getX() + (stairs[0] - currentFloor.size[1]/2) * 7, position.getY() + 5 * (f - floorsToMake/2), position.getZ() + (stairs[1] - currentFloor.size[1]/2) * 7);

					placeTemplate(world, room, pos, rotation);
				}
				currentFloor = nextFloor;
				break;
			}
			f++;
		}
		return true;
	}
	
	public static int runThrough(HashMap<Integer, boolean[]> list, String[][] plan, int x, int z, int lastX, int lastZ)
	{
		int reachedRooms = 0;
		if(list.get(x) == null || z >= list.get(x).length || plan[x][z].contentEquals("") ||  list.get(x)[z])
			return reachedRooms;
		
		int[][] connected = getAdjacentRooms(plan[x][z].substring(0,1), plan[x][z].length() >= 2 ? Integer.parseInt(plan[x][z].substring(1)) : 0);
		boolean connectedToLastRoom = (x == lastX && z == lastZ) || plan[lastX][lastZ].contains("C");
		for(int[] coords : connected)
			if(coords[0] + x == lastX && coords[1] + z == lastZ)
				connectedToLastRoom = true;
		if(!connectedToLastRoom)
			return reachedRooms;
		list.get(x)[z] = true;
		reachedRooms++;
		for(int[] coords : connected)
			if(!list.get(coords[0] + x)[coords[1] + z])
				reachedRooms += runThrough(list, plan, coords[0] + x, coords[1] + z, x, z);
		
		return reachedRooms;
	}
	
	public static int[][] getAdjacentRooms(String room, int rotation)
	{
		int[] adjacentList = new int[0];
		switch(room)
		{
		case "T":
			adjacentList = new int[] {1, 2, 3};
			break;
		case "+":
		case "B":
		case "Y":
		case "Z":
		case "*":
			adjacentList = new int[] {0, 1, 2, 3};
			break;
		case "L":
			adjacentList = new int[] {1, 2};
			break;
		case "C":
		case "D":
		case "-":
			adjacentList = new int[] {0, 2};
			break;
		case "E":
		case "V":
		case "^":
		case "S":
		case "X":
			adjacentList = new int[] {2};
			break;
		}
		
		int[][] out = new int[adjacentList.length][2];
		for(int i = 0; i < adjacentList.length; i++)
		{
			int side = (adjacentList[i] + rotation) % 4;
			switch(side)
			{
			case 0:
				out[i] = new int[]{-1, 0};
				break;
			case 1:
				out[i] = new int[]{0, -1};
				break;
			case 2:
				out[i] = new int[]{1, 0};
				break;
			case 3:
				out[i] = new int[]{0, 1};
				break;
			case 4:
				out = new int[][] {{0, -1}, {1, -1}, {2, 0}, {2, 1}, {1, 2}, {0, 2}, {-1, 1}, {-1, 0}};
				break;
			case 5:
				out = new int[][] {{0, -1}, {1, -1}, {2, -1}, {3, 0}, {3, 1}, {3, 2}, {2, 3}, {1, 3}, {0, 3}, {-1, 2}, {-1, 1}, {-1, 0}};
				break;
			}
		}
		return out;
	}
	
	public static void placeTemplate(World world, Template room, BlockPos pos, int rotation)
	{
		int closeX = rotation <= 0 || rotation >= 3 ? -1: 1;
		int closeZ = rotation <= 1 ? -1: 1;
		int xSize = room.getSize().getX()/2;
		int zSize = room.getSize().getZ()/2;
		if(rotation % 2 == 1)
		{
			int temp = xSize;
			xSize = zSize;
			zSize = temp;
		}
		int x = room.getSize().getX() >= 14 ? pos.getX() + closeX * 3 : pos.getX() + closeX * xSize;
		int z = room.getSize().getZ() >= 14 ? pos.getZ() + closeZ * 3 : pos.getZ() + closeZ * zSize;
		
		BlockPos poss = new BlockPos(x, pos.getY(), z);
		
		IBlockState state = world.getBlockState(poss);
		world.notifyBlockUpdate(poss, state, state, 3);
		room.addBlocksToWorldChunk(world, poss, IStructure.getSetting(rotation));
	}
	
	//reads individual room from floor template, not including brackets( [] )
	public static String[] readRoom(String room)
	{
		if(room.length() != 2)
			return new String[] {"", "0"};
		String[] out = new String[] {"", "0"};
		switch(room.substring(0,1))
		{
		case "T":
			out[0] = "3_way";
			break;
		case "+":
			out[0] = "4_way";
			break;
		case "B":
			out[0] = "big";
			break;
		case "C":
			out[0] = "connector";
			break;
		case "E":
			out[0] = "entrance";
			break;
		case "S":
			out[0] = "dead_end";
			break;
		case "D":
			out[0] = "door";
			break;
		case "L":
			out[0] = "elbow";
			break;
		case "^":
			out[0] = "up";
			break;
		case "V":
			out[0] = "down";
			break;
		case "-":
			out[0] = "straight";
			break;
		case "X":
			out[0] = "treasure_1";
			break;
		case "Y":
			out[0] = "treasure_2";
			break;
		case "Z":
			out[0] = "treasure_3";
			break;
		}
		try
		{
			if(Integer.parseInt(room.substring(1,2)) == 0 || true)
				out[1] = room.substring(1,2);
		}
		catch(NumberFormatException e)
		{
			out[1] = "0";
		}
		return out;
	}
	
	
	//semi required semi proof of concept for addable rooms / floors
	public static void setUpRoomsAtServerStart()
	{
		//setting up ROOMS
		
		//DOESNT USE FOLDERS YET SO MAYBE MAKE IT USE FOLDERS IF YOU WANT
		String baseFolder = "/assets/minestuck/structures/dungeons/rooms/";
		HashMap<Boolean, ArrayList<String>> fileNames = Compute.getFileNamesInFolder(baseFolder);
		
		for(String fileName : fileNames.get(true))
		{
			InputStream room = Compute.getFileFromString(baseFolder + fileName);
			
			String shtik = fileName.substring(0, fileName.length()-4);
			NBTTagCompound rooom = new NBTTagCompound();
			try {
				rooom = CompressedStreamTools.readCompressed(room);
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			IOUtils.closeQuietly(room);
			ArrayList<String> types = new ArrayList<>();
			for(String type : ROOMS.keySet())
			{
				if(shtik.contains(type))
				{
					types.add(type);
					shtik = shtik.substring(0, shtik.indexOf(type)) + shtik.substring(shtik.indexOf(type) + type.length());
				}
			}
			while(shtik.contains("_"))
				shtik = shtik.substring(0, shtik.indexOf('_')) + shtik.substring(shtik.indexOf('_') + 1);
			if(shtik.contentEquals("")) shtik = "basic";
			
			Template temp = new Template();
			/*
			Field fixer;
			try 
			{
				fixer = worldServer.getStructureTemplateManager().getClass().getDeclaredField("fixer");
				fixer.setAccessible(true);
				DataFixer fix = (DataFixer)fixer.get(worldServer.getStructureTemplateManager());
				temp.read(fix.process(FixTypes.STRUCTURE, rooom));
				
			} 
			catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) 
			{
				e.printStackTrace();
				temp.read(rooom);
			}
			*/
			temp.read(rooom);
			for(String type : types)
				ROOMS.get(type).put(shtik, temp);
		}
		
		//setting up FLOORS
		baseFolder = "/assets/minestuck/structures/dungeons/floors/";
		fileNames = Compute.getFileNamesInFolder(baseFolder);
				
		for(String fileName : fileNames.get(true))
		{
			floorPlans plan = T1.new floorPlans(baseFolder + fileName);
			if(!FLOORS.containsKey(plan.type))
				FLOORS.put(plan.type, new ArrayList<floorPlans>());
			FLOORS.get(plan.type).add(plan);
		}
		
		for(int i = 0; i < FLOORS.size(); i++)
			for(int j = 0; j < FLOORS.get(FLOORS.keySet().toArray()[i]).size(); j++)
				for(int i2 = i; i2 < FLOORS.size(); i2++)
					for(int j2 = j+1; j2 < FLOORS.get(FLOORS.keySet().toArray()[i2]).size(); j2++)
						FLOORS.get(FLOORS.keySet().toArray()[i]).get(j).makeConnection(FLOORS.get(FLOORS.keySet().toArray()[i2]).get(j2));
	}
	
	public class floorPlans
	{
		private File file;
		private String path;
		private String[][] plan;
		private String type = "";
		private int[] size = new int[] {0,0};
		private HashMap<floorPlans, int[][]> stairs = new HashMap<floorPlans, int[][]>();
		private int sections; 
		private ArrayList<int[]> connections = new ArrayList<int[]>();
		
		private int[] objectSize = new int[] {0, 0};
		
		public floorPlans(String path)
		{
			this.path = path;
			InputStream inputStream = Compute.getFileFromString(path);
			BufferedReader reader = null;
			try
			{
				reader = new BufferedReader(new InputStreamReader(inputStream));
			}
			finally
			{}
			
			String temp = null;
			try
			{
				if(reader != null)
					temp = reader.readLine();
			}
			catch(Exception e)
			{}
			if(temp != null && !temp.contentEquals(""))
			{
				int tempp = temp.contains("DATA") ? 0 : -1;
				while(tempp >= 0 && !temp.contentEquals("") && tempp < 5)
				{
					temp = temp.substring(temp.indexOf(':') + 1);
					int index = temp.indexOf(':');
					switch(tempp)
					{
					case 0:
						this.size[0] = index == -1 ? 0 : Integer.parseInt(temp.substring(0, index));
						break;

					case 1:
						this.size[1] = index == -1 ? 0 : Integer.parseInt(temp.substring(0, index));
						break;
					case 2:
						this.sections = index == -1 ? 0 : Integer.parseInt(temp.substring(0, index));
						break;
					case 3:
						this.type = index == -1 ? temp : temp.substring(temp.lastIndexOf(':') + 1);
						break;
					}
					tempp++;
				}
			}
			try {
				inputStream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.plan = getRooms(path);
		}
		
		public void makeConnection(floorPlans template)
		{	
			
			ArrayList<int[]> stairs = new ArrayList<int[]>();
			for(int i = 0; i < plan.length; i++)
			{
				for(int j = 0; j < plan[i].length; j++)
				{
					if(plan[i][j].contains("S") && template.getPlan()[i][j].contains("S"))
						stairs.add(new int[] {i, j});
				}
			}
			if(!stairs.isEmpty())
			{
				int[][] connectingStairs = new int[stairs.size()][2];
				for(int i = 0; i < stairs.size(); i++)
				{
						connectingStairs[i][0] = stairs.get(i)[0];
						connectingStairs[i][1] = stairs.get(i)[1];
				}
				this.stairs.put(template, connectingStairs);
				template.getStairs().put(this, connectingStairs);
			}
		}
		
		public File getFile() {return file;}
		public String getFileName() {return path.substring(path.lastIndexOf('/') + 1);}
		//public int getTier() {return tier;}
		public String[][] getPlan() {return plan;}
		public String getType() {return type;}
		public int[] getSize() {return size;}
		public HashMap<floorPlans, int[][]> getStairs(){return stairs;}
		public ArrayList<int[]> getConnections(){return connections;}
		public int getSections() {return sections;}
		
		//made to get connections too
		public String[][] getRooms(String path)
		{
			InputStream inputStream = Compute.getFileFromString(path);
			BufferedReader reader = null;
			try
			{
				reader = new BufferedReader(new InputStreamReader(inputStream));
			}
			finally
			{}
			ArrayList<String[]> line = new ArrayList<>();
			
			String linee = null;
			if(reader != null)
			{
				try 
				{
					linee = reader.readLine();
				} 
				catch (IOException e2) {}
			}
			while(linee != null && !linee.contentEquals(""))
			{
				if(linee.contains("DATA")) 
				{
					try {
						linee = reader.readLine();
					} catch (IOException e) {
						linee = "";
					}
					continue;
				} //"SHOULDNT matter" he said despite it mattering
				String[] row = new String[this.size[0]];
				int i = 0;
				while(!linee.contentEquals("") && i < row.length)
				{
					if(!linee.contains("[") || !linee.contains("]")) 
					{
						linee = "";
						continue;
					}
					row[i] = linee.substring(linee.indexOf('[') + 1, linee.indexOf(']'));
					linee = linee.substring(linee.indexOf(']') + 1);
					i++;
				}
				line.add(row);
				try {
					linee = reader.readLine();
				} catch (IOException e) {
					linee = "";
				}
			}
			String[][] floorPlan = new String[size[0]][size[1]];
			for(int i = 0; i < line.size(); i++)
				for(int j = 0; j < line.get(i).length; j++)
				{
					floorPlan[j][i] = line.get(i)[j];
					if(floorPlan[j][i].contains("C"))
						connections.add(new int[] {j, i});
				}
			try {
				inputStream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return floorPlan;
		}
	}
}