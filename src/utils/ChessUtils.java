/**
 * @author Danny Cummings
 */
package utils;

/* IO */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/* Data Structures */
import java.util.Map;

public class ChessUtils {

	/**
	 * Writes content to a file called filename
	 * If file exists, appends to end of file
	 * @param filename
	 * @param content
	 */
	public static void writeToFile(String filename, String content) {
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename, true));
			bufferedWriter.write(content);
			bufferedWriter.newLine();
			
			bufferedWriter.close(); 
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * Reads content from a file named filename and stores information in map
	 * Odd lines represent the key of the map, even lines represent the value
	 * @param filename
	 */
	public static void load(String filename, Map<String, String> map) {
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
			String line, key = null;
			boolean oddline = true;
			while ((line = bufferedReader.readLine()) != null) { /* null marks the end of file */
				if (oddline) {
					key = line;
				} else {
					map.put(key, line);
				}
				oddline = !oddline;
			}
			bufferedReader.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
