package api;
// this is just a version of it that fits in one class, so that if you use it you dont need to put multiple new files in your project.
import java.util.stream.*;

import java.lang.ref.*;
import java.text.Collator;
import java.util.*;
import java.lang.reflect.*;
import java.lang.ref.*;

public class Debugger {

	protected int indent = 0;
	protected int spacesPerIndent = 5;
	protected StringBuilder buffer = new StringBuilder();



	protected void dedent() {
		this.indent--;
	}

	protected void indent() {
		this.indent++;
	}

	protected void lineFeed() {
		this.buffer.append("\n");
		for (int i = 0; i < this.indent * this.spacesPerIndent; i++) {
			buffer.append(" ");
		}
	}

	protected void write(String string) {
		buffer.append(string);
	}

	protected void writeString(String string) {
		writeChar('"');
		write(string);
		writeChar('"');
	}

	protected void writeChar(char c) {
		buffer.append(c);
	}

	protected void writeNumber(Object num) {
		buffer.append(String.valueOf(num));
	}

	// write a class and all its fields.
	protected void writeObject(Object object) {
		write(object.getClass().getSimpleName());
		writeChar(' ');
		writeChar('{');
		Map<String, Object> map = convertObject(object);
		Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();

		Map.Entry<String, Object> entry;
		if ((entry = iterator.next()) != null) {
			this.indent();
			this.lineFeed();
			this.write(entry.getValue().getClass().getSimpleName());

			this.writeChar(' ');
			this.write(entry.getKey());
			this.writeChar(' ');
			this.writeChar('=');
			this.writeChar(' ');
			this.writeValue(entry.getValue());
		} else {
			this.writeChar('}');
			return;
		}
		if (iterator.hasNext()) {
			iterator.forEachRemaining(e -> {
				this.writeChar(',');
				this.lineFeed();
				this.write(e.getValue().getClass().getSimpleName());
				this.writeChar(' ');
				this.write(e.getKey());
				this.writeChar(' ');
				this.writeChar('=');
				this.writeChar(' ');
				this.writeValue(e.getValue());
			});
		}
		this.dedent();
		this.lineFeed();
		this.writeChar('}');
	}

	protected void writeValue(Object object) {
		switch (object.getClass().getName()) {

			case "java.lang.String": {
				writeString((String) object);
				return;
			}
			case "java.lang.Double":
			case "java.lang.Float":
			case "java.lang.Short":
			case "java.lang.Integer": {
				writeNumber(object);
				return;
			}
			case "java.lang.Boolean": {
				write(Boolean.toString((boolean) object));
				return;
			}
			default: { // ignored

			}
		}
		if (object instanceof Collection || object.getClass().isArray()) {

			if (indent > 1) {
			write(object.getClass().getSimpleName());
 // to prevent writing the same type twice. (in the first indent) and to allow for extra type information inside of multi-dimensional lists and arrays.
			writeChar(' ');
			}
			writeChar('{');

			Iterator<?> iterator;
			if (object instanceof Collection)
				iterator = ((Collection<?>) object).iterator();
			else
				iterator = getIterator(object);
			Object entry;
			if ((entry = iterator.next()) != null) {
				
				this.indent();
				this.lineFeed();
				writeValue(entry);
			} else {
				this.writeChar('}');
				return;
			}
			synchronized (iterator) {
				iterator.forEachRemaining(e -> {
					this.writeChar(',');
					this.lineFeed();
					writeValue(e);
				});
			}
			this.dedent();
			this.lineFeed();
			this.writeChar('}');

			return;
		}
		writeObject(object);
		return;
	}

	// DEBUG ONLY
	protected String peek() {
		return buffer.toString();
	}

	protected static Iterator getIterator(Object array) {

		String className = array.getClass().getName();
		switch (className) {
			case "[I": { // ints!
				return Arrays.stream((int[]) array).boxed().iterator();
			}
			case "[J": { // longs!
				return Arrays.stream((long[]) array).boxed().iterator();
			}
			case "[S": { // shorts!

				List<Short> shorts = new ArrayList<>();
				for (short s : (short[]) array) {
					shorts.add(s);
				}

				return  shorts.iterator();
			}
			case "[F": { // floats!
				List<Float> floats = new ArrayList<>();
				for (float s : (float[]) array) {
					floats.add(s);
				}

				return floats.iterator();
			}
			case "[D": { // doubles!
				List<Double> doubles = new ArrayList<>();
				for (double s : (double[]) array) {
					doubles.add(s);
				}

				return doubles.iterator();
			}
			case "[Z": { // booleans!
				List<Boolean> booleans = new ArrayList<>();
				for (boolean s : (boolean[]) array) {
					booleans.add(s);
				}

				return booleans.iterator();

			}
			case "java/lang/String": {

				return Arrays.stream((String[]) array).iterator();
			}
			default: {
				return Arrays.stream((Object[]) array).iterator();
			}
		}
	}

	// TESTING
	public static void main(String[] args) {

		Debugger c = new Debugger();
		// c.writeString("Hello");
		c.writeObject(new Main());
		System.out.println(c.peek());

	}

	private static Map<String, Object> convertObject(Object object) {

		LinkedHashMap<String, Object> fieldMap = new LinkedHashMap<>();

		Comparator<Field> comparator = (o1, o2) -> o1.getType()
				.getSimpleName()
				.compareTo(o2.getType().getSimpleName());
		for (Field f : Arrays.stream(object.getClass()
				.getDeclaredFields())
				.sorted(comparator)
				.collect(Collectors.toList())) {
			try {
				f.setAccessible(true);
				fieldMap.put(f.getName(), f.get(object));

			} catch (Exception e) {
				fieldMap.put(f.getName(), "Field could not be accessed.");

			}
		}

		return fieldMap;

	}

	// wrapped in <> too
	// private static String genericTypeOrBlank(Field f) {
	// 	try {
 //        ParameterizedType type = (ParameterizedType) f.getGenericType();
	// 		System.out.println(type);
	// 		System.out.println(Arrays.toString(type.getActualTypeArguments()));
 //        Class<?> clazz = (Class<?>)type.getActualTypeArguments()[0];
	// 	String[] split = clazz.getName().split("\\.");
	// 		System.out.println(Arrays.toString(split));


	// 	return String.format("<%s>", Arrays.stream(type.getActualTypeArguments()).map(e -> e.toString()).collect(Collectors.joining(","))); 
	
	// 	}catch(Exception e) {
	// 		// e.printStackTrace();
	// 		return ""; 
	// 	} 

	// }




	private static String implode(Object object) {

		Debugger d = new Debugger();
		
		d.writeObject(object);
		return d.create();
	}

	private String create() {
		return buffer.toString();
	}



			
	private static final String FORMAT_STR = "[%s:%s] ";


	/**

Blow up the object and then print it, while also letting you know the file and line number that the method is called with.
i.e 
[Main.java:19] Main {
     ArrayList array = {
          "Test",
          "test2",
          "test3",
          "test4"
     },
     Integer i = 4,
     Integer b = 4,
     Integer c = 4
}
*/
	public static final synchronized void debug(Object object) {

		StackTraceElement trace = new Throwable().getStackTrace()[1];


		System.out.printf(FORMAT_STR, trace.getFileName(), trace.getLineNumber());
		System.out.println(implode(object));

	}

}
