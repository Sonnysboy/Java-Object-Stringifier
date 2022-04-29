import java.util.*;

public class CollectionStringifier {

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
		Map<String, Object> map = ObjectConverter.convertObject(object);
		// List<Map.Entry<String, Object>> = new LinkedList<>();
		Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();

		Map.Entry<String, Object> entry;
		if ((entry = iterator.next()) != null) {
			this.indent();
			this.lineFeed();
			this.write(entry.getValue().getClass().getSimpleName());
				
			this.writeChar(' ');
			this.write(entry.getKey());
			this.writeChar(':');
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
				this.writeChar(':');
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

	protected static ListIterator<?> getIterator(Object array) {

		String className = array.getClass().getName();
		switch (className) {
			case "[I": { // ints!

				return (ListIterator<?>) Arrays.stream((int[]) array).boxed().iterator();
			}
			case "[J": { // longs!
				return (ListIterator<?>) Arrays.stream((long[]) array).boxed().iterator();
			}
			case "[S": { // shorts!

				List<Short> shorts = new ArrayList<>();
				for (short s : (short[]) array) {
					shorts.add(s);
				}

				return (ListIterator<?>) shorts.iterator();
			}
			case "[F": { // floats!
				List<Float> floats = new ArrayList<>();
				for (float s : (float[]) array) {
					floats.add(s);
				}

				return (ListIterator<?>) floats.iterator();
			}
			case "[D": { // doubles!
				List<Double> doubles = new ArrayList<>();
				for (double s : (double[]) array) {
					doubles.add(s);
				}

				return (ListIterator<?>) doubles.iterator();
			}
			case "[Z": { // booleans!
				List<Boolean> booleans = new ArrayList<>();
				for (boolean s : (boolean[]) array) {
					booleans.add(s);
				}

				return (ListIterator<?>) booleans.iterator();

			}
			case "java/lang/String": {

				return (ListIterator<?>) Arrays.stream((String[]) array);
			}
			default: {
				return (ListIterator<?>) Arrays.stream((Object[]) array);
			}
		}
	}
	// TESTING
	public static void main(String[] args) {

		CollectionStringifier c = new CollectionStringifier();
		// c.writeString("Hello");
		c.writeObject(new Main());
		System.out.println(c.peek());

	}
}