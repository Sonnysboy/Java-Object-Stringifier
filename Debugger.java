
import java.util.*;

public class Debugger {


	private static final String FORMAT_STR = "[%s:%s] ";
	public static void dbg$(Object object) {


		            StackTraceElement trace = new Throwable().getStackTrace()[1];
		



		// TODO make this not use #peek, it should just have a #compile or something.g
		CollectionStringifier cs = new CollectionStringifier();
		cs.writeObject(object);

		System.out.printf(FORMAT_STR, trace.getFileName(), trace.getLineNumber());
		System.out.println(cs.peek());

	}
}