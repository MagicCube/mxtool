package org.magiccube.common.io;

import java.io.File;
import java.io.IOException;

public class FileReader
{
	public static String readText(File p_file) throws IOException
	{
		TextStreamReader reader = new TextStreamReader(p_file);
		String result = reader.readToEnd();
		reader.close();
		return result;
	}
	
	public static String[] readLines(File p_file) throws IOException
	{
		TextStreamReader reader = new TextStreamReader(p_file);
		String[] result = reader.readLines();
		reader.close();
		return result;
	}
}
