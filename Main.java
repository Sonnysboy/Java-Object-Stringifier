import java.util.*;
class Main {


	private final int i = 4;
	private final int b = 4;
	private final int c = 4;

	
	private final List<String> array = Arrays.asList("Test", "test2", "test3", "test4");

	private static CollectionStringifier c5 = new CollectionStringifier();

	
  public static void main(String[] args) {


	  Main m = new Main();

	  Debugger.dbg$(m);
	  /*OUTPUT: 
	  [Main.java:20] Main {
     Integer b: 4,
     CollectionStringifier c5: CollectionStringifier {
          Integer spacesPerIndent: 5,
          Integer indent: 0,
          StringBuilder buffer: StringBuilder {
               String serialVersionUID: "Field could not be accessed."
          }
     },
     Integer c: 4,
     ArrayList array: {
          "Test",
          "test2",
          "test3",
          "test4"
     },
     Integer i: 4
}
*/



  }
}