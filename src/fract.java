import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import dataStructs.Vert;
import objStuff.ObjCacheWriter;


public class fract 
{
	static ObjCacheWriter ocw = new ObjCacheWriter();
	public static void main(String[] args) throws IOException
	{
		Vert[] verts = {new Vert(0,0,0), new Vert(10, 0, 0), new Vert(10, 10, 0), new Vert(0, 10, 0)};
		Polygon p = new Polygon(verts);
		recurse(p,0);
		ocw.write(new File("fractaltest.obj"));
	}
	
	public static void recurse(Polygon p, int depth)
	{
		Polygon newpoly = new Polygon(p);
		//Polygon newpoly2 = new Polygon(p);
		newpoly.translate(0,depth,0);
		newpoly.scale((float) .9);
		ocw.add(p.verts, (byte) (depth+1));
		if(depth>10)
			return;
		recurse(newpoly, depth+1);
	}

}

class Polygon
{
	public Vert[] verts;
	
	public Polygon(Vert[] in)
	{
		verts = in;
	}
	
	public Polygon(Polygon old)
	{
		this.verts= Arrays.copyOf(old.verts, old.verts.length);
	}
	
	public void translate(float dx, float dy, float dz)
	{
		for(int i=0; i<verts.length; i++)
		{
			Vert newvert = new Vert(verts[i].getX()+dx,verts[i].getZ()+dz,verts[i].getY()+dy);
			verts[i]=newvert;
		}
	}
	
	public void fieldShift()
	{
		for(int i=0; i<verts.length; i++)
		{
			verts[i] = new Vert(-verts[i].getY(),verts[i].getZ(),verts[i].getX());
		}
	}
	
	public void scale(float scale)
	{
		Vert center = getCenter();
		float x=0, y=0, z=0;
		for(int i=0; i<verts.length; i++)
		{
			x=center.getX()+scale*(verts[i].getX()-center.getX());
			y=center.getY()+scale*(verts[i].getY()-center.getY());
			z=center.getZ()+scale*(verts[i].getZ()-center.getZ());
			
			if(x==y && y==z && z==0)
				System.out.printf("suspicious: scale=%d, center at %s\n", scale, center.toString());
			verts[i]=new Vert(x, z, y);
		}
	}
	
	public Vert getCenter()
	{
		float x=0, z=0, y=0;
		for(Vert v : verts)
		{
			x+=v.getX();
			z+=v.getZ();
			y+=v.getY();
		}
		x/=verts.length;
		z/=verts.length;
		y/=verts.length;
		return new Vert(x, z, y);
	}
	
	public Vert getNormal()
	{
		float x=0, z=0, y=0;
		
		float ax=0, ay=0, az=0;
		float bx=0, by=0, bz=0;
		
		ax = verts[1].getX()-verts[0].getX();
		ay = verts[1].getY()-verts[0].getY();
		az = verts[1].getZ()-verts[0].getZ();
		
		bx = verts[2].getX()-verts[1].getX();
		by = verts[2].getY()-verts[1].getY();
		bz = verts[2].getZ()-verts[1].getZ();
		
		x = ay*bz - az*by;
		y = az*bx - ax*bz;
		z = ax*by - ay*bx;
		
		float length = (float) Math.sqrt((x*x)+(y*y)+(z*z));
		
		return new Vert(x/length, y/length, z/length);
	}
}