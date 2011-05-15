package levelData;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import objStuff.ObjCacheWriter;
import Tag.Tag;
import dataStructs.*;


public class SceneCollector 
{
	int[] behavior = new int[Byte.MAX_VALUE+1];
	File rootpath;
	private ArrayList<Face> faces;
	private ArrayList<Block> block;
	byte[] blocks;
	byte[] data;
	ObjCacheWriter ocw;
	int dump=0, depth;
	public SceneCollector(String pathname)
	{
		faces = new ArrayList<Face>();
		block = new ArrayList<Block>();
		rootpath = new File(pathname);
		this.ocw = new ObjCacheWriter();
		init();
	}
	
	public void setDepth(int i)
	{
		depth = i;
	}
	
	public void eatChunk(int chunkX, int chunkZ)
	{	
		if((float)Runtime.getRuntime().totalMemory()/Runtime.getRuntime().maxMemory()==1)
		{
			this.write(new File("emergency_" + ++dump + ".obj"));
			ocw = new ObjCacheWriter();
			while((float)Runtime.getRuntime().totalMemory()/Runtime.getRuntime().maxMemory()==1)
				Runtime.getRuntime().gc();
		}
		InputStream is = RegionFileCache.getChunkDataInputStream(rootpath, chunkX, chunkZ); // current chunk
		
		InputStream is_x = RegionFileCache.getChunkDataInputStream(rootpath, chunkX+1, chunkZ);
		InputStream x_is = RegionFileCache.getChunkDataInputStream(rootpath, chunkX-1, chunkZ);
		InputStream is_z = RegionFileCache.getChunkDataInputStream(rootpath, chunkX, chunkZ+1);
		InputStream z_is = RegionFileCache.getChunkDataInputStream(rootpath, chunkX, chunkZ-1);
		
		try {
			Tag t = Tag.readFrom(is);
			Tag t_c = t.findTagByName("Blocks");
			
			t = Tag.readFrom(is_x);
			Tag t_x = t.findTagByName("Blocks");
			
			t = Tag.readFrom(x_is);
			Tag x_t = t.findTagByName("Blocks");
			
			t = Tag.readFrom(is_z);
			Tag t_z = t.findTagByName("Blocks");
			
			t = Tag.readFrom(z_is);
			Tag z_t = t.findTagByName("Blocks");
			blocks = (byte[])t_c.getValue();
			
			byte[] blocks_x = (byte[])t_x.getValue();
			byte[] x_blocks = (byte[])x_t.getValue();
			byte[] blocks_z = (byte[])t_z.getValue();
			byte[] z_blocks = (byte[])z_t.getValue();
			
			for(int x=0; x<16; x++)
			{
				for(int z=0; z<16; z++)
				{
					for(int y=depth; y<128; y++)
					{
						try{
						//ACTION(x, z, y, chunkX, chunkZ);
						if(x<15) doFace(x+1, z, y, chunkX, chunkZ, blocks[getOffset(x, z, y)], blocks[getOffset(x+1, z, y)], (byte) 0x01);
						if(x>0) doFace(x-1, z, y, chunkX, chunkZ, blocks[getOffset(x, z, y)], blocks[getOffset(x-1, z, y)], (byte) 0x04);
						if(x==0) doFace(x, z, y, chunkX, chunkZ, blocks[getOffset(x, z, y)], x_blocks[getOffset(15, z, y)], (byte) 0x01);
						else if(x==15) doFace(x, z, y, chunkX, chunkZ, blocks[getOffset(x, z, y)], blocks_x[getOffset(0, z, y)], (byte) 0x04);
						
						if(z<15)doFace(x, z+1, y, chunkX, chunkZ, blocks[getOffset(x, z, y)], blocks[getOffset(x, z+1, y)], (byte) 0x02);
						if(z>0)doFace(x, z-1, y, chunkX, chunkZ, blocks[getOffset(x, z, y)], blocks[getOffset(x, z-1, y)], (byte) 0x05);
						if(z==0)doFace(x, z, y, chunkX, chunkZ, blocks[getOffset(x, z, y)], z_blocks[getOffset(x, 15, y)], (byte) 0x02);
						else if(z==15) doFace(x, z, y, chunkX, chunkZ, blocks[getOffset(x, z, y)], blocks_z[getOffset(x, 0, y)], (byte) 0x05);
						
						if(y<127)doFace(x, z, y+1, chunkX, chunkZ, blocks[getOffset(x, z, y)], blocks[getOffset(x, z, y+1)], (byte) 0x06);
						if(y>0)doFace(x, z, y-1, chunkX, chunkZ, blocks[getOffset(x, z, y)], blocks[getOffset(x, z, y-1)], (byte) 0x03);
						
						}catch(Exception e){e.printStackTrace();}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		System.out.printf("%d faces\n", faces.size());
		if(faces.size()<300)
			System.out.println("------------------------------------------");
		try{
		pour(ocw);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		try {
			is.close();
			is_x.close();
			x_is.close();
			is_z.close();
			z_is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Runtime.getRuntime().gc();
		//System.out.println("After garbace collect: " + Runtime.getRuntime().maxMemory());
	}
	
	public void write(File filename)
	{
		try {
			ocw.write(filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void doFace(int x, int z, int y, int chunkX, int chunkZ, byte from, byte to, byte dir)
	{
		//if(from == 0 && to != 0)
			//faces.add(new Face(x+16*chunkX, z+16*chunkZ, y, dir, to));
		if((behavior[from] & 0x01) != 0)
		{
			if((behavior[to] & 0x08) != 0)
			{
				faces.add(new Face(x+16*chunkX, z+16*chunkZ, y, dir, to));
				//System.out.println("adding a face from " + from + " to " + to);
			}
		}
		if((behavior[from] & 0x02) !=0)
		{
			if(from != to)
				faces.add(new Face(x+16*chunkX, z+16*chunkZ, y, dir, to));
			throw new RuntimeException();
		}
		if ((behavior[from] & 0x04) != 0)
		{
			if(dir == 0x03)
				faces.add(new Face(x+16*chunkX, z+16*chunkZ, y, dir, from));
			throw new RuntimeException();
		}
	}
	
	private void ACTION(int x, int z, int y, int chunkX, int chunkZ)
	{
		if(behavior[blocks[getOffset(x, z, y)]] == 0)
		{
			if(x<=0)
				faces.add(new Face(x+16*chunkX, z+16*chunkZ, y, (byte) 0x01, blocks[getOffset(x, z, y)]));
			if(x>=15)
				faces.add(new Face(x+16*chunkX, z+16*chunkZ, y, (byte) 0x04, blocks[getOffset(x, z, y)]));
			if(z<=0)
				faces.add(new Face(x+16*chunkX, z+16*chunkZ, y, (byte) 0x02, blocks[getOffset(x, z, y)]));
			if(z>=15)
				faces.add(new Face(x+16*chunkX, z+16*chunkZ, y, (byte) 0x05, blocks[getOffset(x, z, y)]));
			if(y>=127)
				faces.add(new Face(x+16*chunkX, z+16*chunkZ, y, (byte) 0x03, blocks[getOffset(x, z, y)]));
		}
		if(behavior[blocks[getOffset(x, z, y)]] == 1)
		{
			if(x<15 && behavior[blocks[getOffset(x+1, z, y)]] != 1)
				faces.add(new Face(x+1+16*chunkX, z+16*chunkZ, y, (byte) 0x01, blocks[getOffset(x+1, z, y)]));
			
			if(x>0 && behavior[blocks[getOffset(x-1, z, y)]] != 1)
				faces.add(new Face(x-1+16*chunkX, z+16*chunkZ, y, (byte) 0x04, blocks[getOffset(x-1, z, y)]));
			
			if(z<15 && behavior[blocks[getOffset(x, z+1, y)]] != 1)
				faces.add(new Face(x+16*chunkX, z+1+16*chunkZ, y, (byte) 0x02, blocks[getOffset(x, z+1, y)]));
			
			if(z>0 && behavior[blocks[getOffset(x, z-1, y)]] != 1)
				faces.add(new Face(x+16*chunkX, z-1+16*chunkZ, y, (byte) 0x05, blocks[getOffset(x, z-1, y)]));
			
			if(y<127 && behavior[blocks[getOffset(x, z, y+1)]] != 1)
				faces.add(new Face(x+16*chunkX, z+16*chunkZ, y+1, (byte) 0x06, blocks[getOffset(x, z, y+1)]));
			
			if(y>0 && behavior[blocks[getOffset(x, z, y-1)]] != 1)
				faces.add(new Face(x+16*chunkX, z+16*chunkZ, y-1, (byte) 0x03, blocks[getOffset(x, z, y-1)]));
			
		}
		else if(behavior[blocks[getOffset(x, z, y)]] == 2)
		{
			//System.out.println("type 2 block at "  + x + " " + z + " " + y);
			if(x<15 && behavior[blocks[getOffset(x+1, z, y)]] != 1)
				faces.add(new Face(x+1+16*chunkX, z+16*chunkZ, y, (byte) 0x01, blocks[getOffset(x+1, z, y)]));
			
			if(x>0 && behavior[blocks[getOffset(x-1, z, y)]] != 1)
				faces.add(new Face(x-1+16*chunkX, z+16*chunkZ, y, (byte) 0x04, blocks[getOffset(x-1, z, y)]));
			
			if(z<15 && behavior[blocks[getOffset(x, z+1, y)]] != 1)
				faces.add(new Face(x+16*chunkX, z+1+16*chunkZ, y, (byte) 0x02, blocks[getOffset(x, z+1, y)]));
			
			if(z>0 && behavior[blocks[getOffset(x, z-1, y)]] != 1)
				faces.add(new Face(x+16*chunkX, z-1+16*chunkZ, y, (byte) 0x05, blocks[getOffset(x, z-1, y)]));
			
			if(y<127 &&behavior[blocks[getOffset(x, z, y+1)]] != 1)
				faces.add(new Face(x+16*chunkX, z+16*chunkZ, y+1, (byte) 0x06, blocks[getOffset(x, z, y+1)]));
			
			if(y>0 &&behavior[blocks[getOffset(x, z, y-1)]] != 1)
				faces.add(new Face(x+16*chunkX, z+16*chunkZ, y-1, (byte) 0x03, blocks[getOffset(x, z, y-1)]));
		}
		
		/*else if(behavior[blocks[getOffset(x, z, y)]] == 3) // this needs work
		{
			byte current = blocks[getOffset(x, z, y)];
			//System.out.println("type 2 block at "  + x + " " + z + " " + y);
			if(blocks[getOffset(x+1, z, y)] != current)
				faces.add(new Face(x+1+16*chunkX, z+16*chunkZ, y, (byte) 0x01, blocks[getOffset(x+1, z, y)]));
			
			if(blocks[getOffset(x-1, z, y)] != current)
				faces.add(new Face(x-1+16*chunkX, z+16*chunkZ, y, (byte) 0x04, blocks[getOffset(x-1, z, y)]));
			
			if(blocks[getOffset(x, z+1, y)] != current)
				faces.add(new Face(x+16*chunkX, z+1+16*chunkZ, y, (byte) 0x02, blocks[getOffset(x, z+1, y)]));
			
			if(blocks[getOffset(x, z-1, y)] != current)
				faces.add(new Face(x+16*chunkX, z-1+16*chunkZ, y, (byte) 0x05, blocks[getOffset(x, z-1, y)]));
			
			//if(behavior[blocks[getOffset(x, z, y+1)]] != 1)
				faces.add(new Face(x+16*chunkX, z+16*chunkZ, y+1, (byte) 0x06, blocks[getOffset(x, z, y+1)]));
			
			if(behavior[blocks[getOffset(x, z, y-1)]] != current)
				faces.add(new Face(x+16*chunkX, z+16*chunkZ, y-1, (byte) 0x03, blocks[getOffset(x, z, y-1)]));
		}	*/
	}
	
	public Vert[][] dump()
	{
		LinkedList<Vert[]> out = new LinkedList<Vert[]>();
		for(Face f : faces)
		{
			out.add(f.getPoly());
		}
		return out.toArray(new Vert[1][]);
	}
	
	public void pour(ObjCacheWriter o)
	{
		Collections.sort(faces);
		
		Face comp = faces.get(0);
		ArrayList<Vert[]> tmp = new ArrayList<Vert[]>();
		//System.out.println("optimizing block type #" + comp.getCode());
		for(Face f : faces)
		{
			if(!f.mergeable(comp))
			{
				//if(f.getCode()!=comp.getCode())
				//System.out.println("optimizing block type #" + f.getCode());
				
				for(Vert[] v : proccess(tmp))
				{
					//System.out.println("level 1");
					o.add(v, comp.getCode());
					//System.out.println(comp.getCode());
				}
				//System.out.println("-------------------- " + tmp.size());
				comp = f;
				tmp.clear();
			}
			//System.out.println(f);
			tmp.add(f.getPoly());
		}
		for(Vert[] v : proccess(tmp))
		{
			//System.out.println("level 1");
			o.add(v, comp.getCode());
			//System.out.println(comp.getCode());
		}
		faces.clear();
		//System.out.println(tmp.size() + " + " + tmp.get(0)[0]);
	}
	
	private Vert[] merge(Vert[] a, Vert[] b)
	{
		Vert[] c = new Vert[4];
		int idcount=0;
		boolean found = false;
		for(int i=0; i<4; i++)
		{
			found = false;
			for(int j=0; j<4; j++)
			{
				if(a[i].equals(b[j]))
				{
					if(i==j)
						return null;
					found = true;
					idcount++;
					c[i] = b[i];
				}
			}
			if(!found)
				c[i] = a[i];
		}
		if(idcount != 2)
			return null;
		return c;
	}
	

	
	private Vert[][] proccess(ArrayList<Vert[]> in)
	{
		//System.out.println("PROCCESING!!!");
		//System.out.println("level 2");
		//System.out.println(in.size());
		LinkedList<Vert[]> set = new LinkedList<Vert[]>(in);
		LinkedList<Vert[]> hold = new LinkedList<Vert[]>();
		Vert[] V1, V2, V3;
		boolean changed = true;
		while (changed)
		{
			changed = false;
			while(!set.isEmpty())
			{
				V1=set.removeFirst();
				try
				{
					V2=set.removeFirst();
				}catch(NoSuchElementException e)
				{
					hold.add(V1);
					continue;
				}
				V3 = merge(V1, V2);
				if(V3 != null)
				{
					hold.add(V3);
					changed = true;
				}
				else
				{
					hold.add(V1);
					hold.add(V2);
				}
			}
			set.addAll(hold);
			hold.clear();
		}
		set.addAll(hold);
		//System.out.println("merged to " + set.size() + " polygons");
		
		return set.toArray(new Vert[1][]);
	}
	
	private void init()
	{
		File f = new File("config.txt");
		try {
			Scanner s = new Scanner(f);
			String[] line;
			while(s.hasNextLine())
			{
				line = s.nextLine().split(" ");
				if(!line[0].startsWith("/"))
				{
					behavior[Integer.valueOf(line[0])] = Integer.valueOf(line[1]);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static int getOffset(int x, int z, int y)
	{
		return y+128*z+128*16*x;
	}
}
