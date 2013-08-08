package common;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
public class CSVWrite {
	//Map<String,Long> summaryMap =new HashMap<String, Long>();
	public static void generateCsvFile(String sFileName, Map<String,Long> summaryMap)
	{
		try
		{
			FileWriter writer = new FileWriter(sFileName);
			
			//Get Map in Set interface to get key and value
			Set s= summaryMap.entrySet();

			//Move next key and value of Map by iterator
			Iterator it=s.iterator();

			while(it.hasNext())
			{
				// key=value separator this by Map.Entry to get key and value
				Map.Entry m =(Map.Entry)it.next();

				// getKey is used to get key of Map
				String key=(String)m.getKey();			
				// getValue is used to get value of key in Map
				Long value= (Long) m.getValue();
				writer.append(key);
				writer.append(',');
				writer.append(value.toString());
				writer.append('\n');
			}

			//generate whatever data you want

			writer.flush();
			writer.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		} 
	}
	
}

