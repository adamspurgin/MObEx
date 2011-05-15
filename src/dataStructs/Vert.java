package dataStructs;

public class Vert
{
	private float x, y, z;
	
	public Vert()
	{
	}
	
	public Vert(float x, float z, float y)
	{
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	public void translate(float dx, float dy, float dz)
	{
		this.x+=x;
		this.y+=y;
		this.z+=z;
	}
	
	public Vert(int x, int z, int y)
	{
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	public Vert(Vert o)
	{
		this.x=o.x;
		this.z=o.z;
		this.y=o.y;
	}
	
	public float getX()
	{
		return x;
	}
	
	public float getZ()
	{
		return z;
	}
	
	public float getY()
	{
		return y;
	}

	public String toString()
	{
		return "<"+x+","+z+","+y+">";
	}
	
	public boolean equals(Vert o)
	{
		return(x==o.x && y==o.y && z==o.z);
	}
}
