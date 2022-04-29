import java.util.*;
import java.lang.reflect.*;
import java.lang.ref.*;
import java.util.stream.*;
import java.util.concurrent.*;


// converts an object into a hashmap 
public class ObjectConverter {



	public static Map<String, Object> convertObject(Object object) {

		final Map<String, Object> map = 
			Collections.synchronizedMap(new HashMap<String, Object>());
		for (Field f : object.getClass().getDeclaredFields()) {
			try {
				f.setAccessible(true);
				map.put(f.getName(), f.get(object));

				}
			catch(Exception e) {
				e.printStackTrace();
				map.put(f.getName(), "Field could not be accessed.");

				}
			}

		return map;


		}



}