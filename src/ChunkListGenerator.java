import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;



/**
 * contains methods used to generate lists of chunks as desired by the user.
 * always returns an array of size 2 arrays, format [0]=x, [1]=z;
 * @author adam
 *
 */
public abstract class ChunkListGenerator 
{
	/**
	 * returns a rectangle of chunks
	 * @param x smallest value x chunk coordinate
	 * @param z smallest value z chunk coordinate
	 * @param width number of chunks to use in the x direction
	 * @param span number of chunks to use in the z direction
	 * @return list of chunks
	 */
	public static int[][] getRect(int x, int z, int width, int span)
	{
		int[][] out = new int[width*span][2];
		int count=0;
		for(int xx=0; xx<width; xx++)
		{
			for(int zz=0; zz<span; zz++)
			{
				out[count][0] = x+xx;
				out[count][1] = z+zz;
				count++;
			}
		}
		return out;
	}
	
	/**
	 * returns a list of chunks growing outwards from the specified center
	 * mode determines radius(0), or chunk count(1)
	 * @param xcenter
	 * @param zcenter
	 * @param ammount
	 * @param mode
	 * @return
	 */
	public static int[][] getRad(int xcenter, int zcenter, int ammount, int mode)
	{
		int x, z, width, height;
		int[][] pool = getRect(xcenter-ammount, zcenter-ammount, ammount*2, ammount*2);
		ArrayList<Loc> list = new ArrayList<Loc>();
		for(int[] i : pool)
		{
			list.add(new Loc(i[0], i[1], xcenter, zcenter));
		}
		Collections.sort(list);
		ArrayList<Loc> out = new ArrayList<Loc>();
		switch(mode)
		{
		case 0:
			for(Loc l : list)
			{
				if(l.getDist() > ammount)
					break;
				out.add(l);
			}
			break;
		case 1:
			for(Loc l : list)
			{
				if(out.size() > ammount)
					break;
				out.add(l);
			}
		}
		pool = new int[out.size()][2];
		for(int i=0; i<out.size(); i++)
		{
			pool[i][0] = (int)out.get(i).location.getX();
			pool[i][1] = (int)out.get(i).location.getY();
		}
		return pool;
	}
	
	
}

class Loc implements Comparable<Loc>
{
	Point2D location, center;
	public Loc(int x, int z, int xx, int zz)
	{
		this.location = new Point2D.Double(x, z);
		this.center = new Point2D.Double(xx, zz);
	}
	
	public double getDist()
	{
		return location.distance(center);
	}

	public int compareTo(Loc o) 
	{
		return ((Double)location.distance(center)).compareTo((Double) o.location.distance(o.center));
	}
}