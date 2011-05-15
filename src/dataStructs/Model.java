package dataStructs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

public class Model 
{
	private Vert[] verts;
	private int[][] faces;
	private String name;
	
	public Model(String objname)
	{
		name = objname;
		File f = new File(objname);
		parse(f);
	}
	public Model(String pathname, String objname)
	{
		name = objname;
		File f = new File(pathname, objname);
		parse(f);
	}
	
	public void print()
	{
		System.out.println(name);
		for(Vert v : verts)
		{
			System.out.println("\tv "+v);
		}
		for(int[] a: faces)
		{
			System.out.print("\tf");
			for(int i:a)
			{
				System.out.print(" " + i);
			}
			System.out.println();
		}
		System.out.println("");
	}
	
	private void parse(File f)
	{
		Scanner s=null;
		try {
			s = new Scanner(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int vertcount = 0, facecount = 0;
		String[] line;
		while(s.hasNextLine())
		{
			line = s.nextLine().split(" ");
			if(line[0].equals("v"))
				vertcount++;
			else if(line[0].equals("f"))
				facecount++;
		}
		try {
			s = new Scanner(f);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		verts = new Vert[vertcount];
		faces = new int[facecount][];
		vertcount = facecount = 0;
		while(s.hasNextLine())
		{
			line = s.nextLine().split(" ");
			if(line[0].equals("v"))
			{
				verts[vertcount++] = new Vert(Float.parseFloat(line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3]));
			}
			else if(line[0].equals("f"))
			{
				faces[facecount] = new int[line.length-1];
				int val;
				for(int i=1; i<line.length; i++)
				{
					try
					{
						val = Integer.parseInt(line[i]);
					}
					catch(NumberFormatException e)
					{
						val = Integer.parseInt(line[i].split("/")[0]);
					}
					faces[facecount][i-1] = val;
				}
				facecount++;
			}
		}
	}
}
