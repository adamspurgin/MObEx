package dataStructs;

public class Face extends Vert implements Comparable<Face>
{
	private byte block;
	private byte data;
	
	public Face()
	{
		super();
	}
	
	public Face(int x, int z, int y, byte data, byte block)
	{
		super(x, z, y);
		this.data=data;
		this.block = block;
		//System.out.println("new face at " + x + " " + z + " " + y + " facing direction " + data);
	}
	
	public Face(float x, float z, float y, byte data)
	{
		super(x, z, y);
		this.data=data;
	}
	
	public Face(Face o)
	{
		super(o.getX(), o.getZ(), o.getY());
		this.block = o.block;
		this.data = o.data;
	}
	
	public boolean mergeable(Face o)
	{
		if(block==o.block && data == o.data)
		{
			switch(data & 0x0f)
			{
			case 1:
			case 4:
				return (getX() == o.getX());
			case 2:
			case 5:
				return (getZ() == o.getZ());
			case 3:
			case 6:
				return (getY() == o.getY());
			}
			return false;
		}
		return false;
	}
	
	public Vert[] getPoly()
	{
		switch(data&0x0F)
		{
		case 0x01:
			Vert[] out1 = {new Vert(this.getX()-.5f,this.getY()+.5f,this.getZ()-.5f),
						  new Vert(this.getX()-.5f,this.getY()+.5f,this.getZ()+.5f),
						  new Vert(this.getX()-.5f,this.getY()-.5f,this.getZ()+.5f),
						  new Vert(this.getX()-.5f,this.getY()-.5f,this.getZ()-.5f)};
			return out1;
		case 0x02:
			Vert[] out2 = {new Vert(this.getX()-.5f,this.getY()+.5f,this.getZ()-.5f),
					  	   new Vert(this.getX()+.5f,this.getY()+.5f,this.getZ()-.5f),
					  	   new Vert(this.getX()+.5f,this.getY()-.5f,this.getZ()-.5f),
					  	   new Vert(this.getX()-.5f,this.getY()-.5f,this.getZ()-.5f)};
			return out2;
		case 0x03:
			Vert[] out3 = {new Vert(this.getX()+.5f,this.getY()+.5f,this.getZ()+.5f),
						   new Vert(this.getX()-.5f,this.getY()+.5f,this.getZ()+.5f),
						   new Vert(this.getX()-.5f,this.getY()+.5f,this.getZ()-.5f),
						   new Vert(this.getX()+.5f,this.getY()+.5f,this.getZ()-.5f)};
			return out3;
		case 0x04:
			Vert[] out4 = {new Vert(this.getX()+.5f,this.getY()+.5f,this.getZ()+.5f),
					      new Vert(this.getX()+.5f,this.getY()-.5f,this.getZ()+.5f),
					      new Vert(this.getX()+.5f,this.getY()-.5f,this.getZ()-.5f),
					      new Vert(this.getX()+.5f,this.getY()+.5f,this.getZ()-.5f)};
			return out4;
		case 0x05:
			Vert[] out5 = {new Vert(this.getX()+.5f,this.getY()-.5f,this.getZ()+.5f),
					  	   new Vert(this.getX()-.5f,this.getY()-.5f,this.getZ()+.5f),
					  	   new Vert(this.getX()-.5f,this.getY()+.5f,this.getZ()+.5f),
					  	   new Vert(this.getX()+.5f,this.getY()+.5f,this.getZ()+.5f)};
			return out5;
		case 0x06:
			Vert[] out6 = {new Vert(this.getX()-.5f,this.getY()-.5f,this.getZ()-.5f),
						   new Vert(this.getX()+.5f,this.getY()-.5f,this.getZ()-.5f),
						   new Vert(this.getX()+.5f,this.getY()-.5f,this.getZ()+.5f),
						   new Vert(this.getX()-.5f,this.getY()-.5f,this.getZ()+.5f)};
			return out6;
		default:
			throw new RuntimeException("bad data block");
		}
	}
	
	public byte getCode()
	{
		return block;
	}
	
	public String toString()
	{
		return "type: " + block + " face: " + (data & 0x0F) + " ("+getX()+","+getZ()+","+getY()+")"; 
	}

	@Override
	public int compareTo(Face o) 
	{
		int comp = block-o.block;	//type of face
		if(comp != 0)
			return comp;
		comp = (data & 0xF0) - (o.data & 0xF0);		// extra data
		if(comp != 0)
			return comp;
		comp = (data & 0x0F) - (o.data & 0x0F);		//direction it's facing
		if(comp != 0)
			return comp;
		if((data&0x0F) == 0x01 || (data&0x0F) == 0x04)
		{
			comp = ((Float)this.getX()).compareTo(o.getX());
			if(comp != 0)
				return comp;
			comp = ((Float)this.getY()).compareTo(o.getY());
			if(comp != 0)
				return comp;
			comp = ((Float)this.getZ()).compareTo(o.getZ());
			if(comp != 0)
				return comp;
		}
		else if((data&0x0F) == 0x02 || (data&0x0F) == 0x05)
		{
			comp = ((Float)this.getZ()).compareTo(o.getZ());
			if(comp != 0)
				return comp;
			comp = ((Float)this.getY()).compareTo(o.getY());
			if(comp != 0)
				return comp;
			comp = ((Float)this.getX()).compareTo(o.getX());
			if(comp != 0)
				return comp;
		}
		else if((data&0x0F) == 0x03 || (data&0x0F) == 0x06)
		{
			comp = ((Float)this.getY()).compareTo(o.getY());
			if(comp != 0)
				return comp;
			comp = ((Float)this.getX()).compareTo(o.getX());
			if(comp != 0)
				return comp;
			comp = ((Float)this.getZ()).compareTo(o.getZ());
			if(comp != 0)
				return comp;
		}
		return 0;
	}


}
