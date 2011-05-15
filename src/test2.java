import java.io.File;
import java.io.IOException;

import objStuff.ObjCacheWriter;
import levelData.SceneCollector;


public class test2 
{
	public static void main(String[] args) throws IOException
	{
		SceneCollector sc = new SceneCollector("C:\\Users\\adam\\AppData\\Roaming\\.minecraft\\saves\\gargamel");
		sc.eatChunk(0, 0);
		ObjCacheWriter ocw = new ObjCacheWriter();
		sc.pour(ocw);
		ocw.write(new File("test.obj"));
	}

}
