package objStuff;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.TreeSet;

import dataStructs.Vert;

public class ObjCacheWriter 
{
	private TreeSet<Vertex> pointlist = new TreeSet<Vertex>();
	private LinkedList<Face> facelist = new LinkedList<Face>();
	private int V_count=0;
	private int facecount=0;
	private int backups=0;
	private String name;
	
	public ObjCacheWriter()
	{
	}
	
	
	public void add(Vert[] polygon, byte code)
	{
		if(code !=0)
		{
			int[] face = new int[polygon.length];
			for(int i=0; i<polygon.length; i++)
			{
				face[i] = insert(polygon[i]);
			}
			facelist.add(new Face(face, code));
		}
		if(facelist.size() % 1000 == 0)
			System.out.printf("%d faces entered\n", facelist.size());
		/*if(facelist.size()>100000)
		{
			File f = new File(name + "_"+backups+".obj");
			try {
				write(f);
				backups++;
				pointlist.clear();
				facelist.clear();
				V_count = 0;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
	}
	
	public void addAll(Vert[][] polygons, byte code)
	{
		for(Vert[] v : polygons)
		{
			this.add(v, (byte)1);
		}
	}
	
	public void write() throws IOException
	{
		File fo = new File(name + "_" + backups + ".obj");
		System.out.printf("writing file %s\n", fo.getName());
		facecount = facelist.size();
		FileWriter fw = new FileWriter(fo);
		Vertex[] va = pointlist.toArray(new Vertex[1]);
		Arrays.sort(va, new vertcomp());
		for(Vertex v : va)
		{
			if(v != null)
				fw.append("v " + v.point.getX() + " " + v.point.getZ() + " " + v.point.getY() + "\n");
			else
				fw.append(("v 0 0 0\n"));
		}
		fw.append("\n\n\n");
		byte current_code=0;
		Collections.sort(facelist);
		for(Face f : facelist)
		{
			if(current_code != f.code)
			{
				current_code = f.code;
				fw.append("\ng " + current_code + "\n");
			}
			fw.append("f ");
			for(int i : f.verts)
			{
				fw.append(i + " ");
			}
			fw.append("\n");
			facecount--;
			if(facecount %100000 == 0)
				System.out.printf("%d faces left to write\n", facecount);
		}
		fw.close();
	}
	
	public void write(File fi) throws IOException
	{
		System.out.printf("writing file %s\n", fi.getName());
		facecount = facelist.size();
		System.out.println(facecount + " faces");
		FileWriter fw = new FileWriter(fi);
		Vertex[] va = pointlist.toArray(new Vertex[1]);
		Arrays.sort(va, new vertcomp());
		for(Vertex v : va)
		{
			if(v != null)
				fw.append("v " + v.point.getX() + " " + v.point.getZ() + " " + v.point.getY() + "\n");
			else
				fw.append(("v 0 0 0\n"));
		}
		fw.append("\n\n\n");
		byte current_code=0;
		Collections.sort(facelist);
		for(Face f : facelist)
		{
			if(current_code != f.code)
			{
				current_code = f.code;
				fw.append("\ng " + current_code + "\n");
			}
			fw.append("f ");
			for(int i : f.verts)
			{
				fw.append(i + " ");
			}
			fw.append("\n");
			facecount--;
			if(facecount %100000 == 0)
				System.out.printf("%d faces left to write\n", facecount);
		}
		fw.close();
	}
	private int insert(Vert point)
	{
		//System.out.println(V_count);
		Vertex v = new Vertex(point, 0);
		Vertex o = pointlist.ceiling(v);
		if(o != null && v.point.equals(o.point))
		{
			//System.out.println(v + "    " + o);
			return o.val;
		}
		else
		{
			//if(o == null)
			//	System.out.println("nul-0");
			//System.out.println(v + "    " + o);
			V_count = V_count+1;
			pointlist.add(new Vertex(point, V_count));
			return V_count;
		}
	}

	private class Vertex implements Comparable<Vertex>
	{
		public Vert point;
		public int val;
		
		public Vertex(Vert p, int c)
		{
			this.val = c;
			this.point = p;
		}
		
		public boolean equals(Object o)
		{
			return (this != null && o != null && this.point.equals(((Vertex)o).point));
		}
		
		public String toString()
		{
			return "{"+point.getX()+","+point.getZ()+","+point.getY()+"}  " + val;
		}

		public int compareTo(Vertex o) 
		{
			double temp = 0;
			if(this.point.equals(o.point))
				return 0;
			
			temp = this.point.getX() - o.point.getX();
			
			if(temp != 0)
				return (int) (temp);
			
			temp = this.point.getY() - o.point.getY();
			
			if(temp != 0)
				return (int) (temp);
			
			temp = this.point.getZ() - o.point.getZ();
			
			return (int) (temp);
		}
	}
	
	private class vertcomp implements Comparator<Vertex>
	{
		public int compare(Vertex o1, Vertex o2) 
		{
			return ((Integer)o1.val).compareTo(o2.val);
		}
		
	}
	
	private class Face implements Comparable<Face>
	{
		public int[] verts;
		public byte code;
		
		public Face(int[] v, byte c)
		{
			this.verts = v;
			this.code = c;
		}

		@Override
		public int compareTo(Face o) 
		{
			return ((Byte)this.code).compareTo(o.code);
		}
	}
}
