import java.io.File;
import java.io.IOException;

import objStuff.ObjCacheWriter;
import levelData.SceneCollector;
import dataStructs.*;

public class test 
{
	public static void main(String[] args) throws IOException
	{
		
		SceneCollector sc = new SceneCollector("C:\\Users\\adam\\AppData\\Roaming\\.minecraft\\saves\\museum");
		sc.setDepth(60);
		int count=0;
		int[][] list = ChunkListGenerator.getRad(-25, -43, 50, 0);
		/*for(int x=-50; x<=50; x++)
		{
			for(int z=-50; z<=50; z++)
			{*/
		for(int[] i : list)
		{
				try
				{
					sc.eatChunk(i[0],i[1]);
					System.out.printf("working on chunk (%d,%d)", i[0], i[1]);
					count++;
				}
				catch(Exception e)
				{
					if(e.getLocalizedMessage() != null)
						System.out.printf("%30s\n", e.getLocalizedMessage());
					continue;
				}
				System.out.printf("\n");
		}
			/*}
		}*/
		sc.write(new File("outt.obj"));
		System.out.println(count + " chunks written");
	}
}
