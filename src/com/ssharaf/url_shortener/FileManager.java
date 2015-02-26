package com.ssharaf.url_shortener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.os.Environment;
import android.util.Log;

/**
 * This class is responsible for reading from and writing 
 * into application's log file.
 * 
 * @author SAMEH SHARAF
 *
 */

public class FileManager {
	
	private String folderPath;
	private String logFilePath;
	
	private static final String FOLDER_NAME = "URL Shortener";
	private static final String FILE_NAME = "url.log";
	private static final String TAG = "URL Shortener File Manager";
	
	/**
	 * Default constructor
	 */
	public FileManager()
	{
		String sd_root = Environment.getExternalStorageDirectory().toString();
		folderPath = sd_root + "/" + FOLDER_NAME;
		Log.i(TAG, folderPath);
		
		File file = new File(folderPath);
		if (!file.exists())
		{
			Log.e(TAG, "Folder Not Found");
			file.mkdir();
		}
		
		logFilePath = folderPath + "/" + FILE_NAME;
		Log.i(TAG, logFilePath);
		file = new File(logFilePath);
		if (!file.exists())
		{
			Log.e(TAG, "Log File Not Found");
			
			try
			{
				Log.i(TAG, "Create log file");
				writeFile("# Here we save URLs which are shortened.");
			} 
			catch(Exception e)
			{
				Log.e(TAG, e.getMessage());
			}
		}
	}

	/**
	 * Read text file
	 * @return Text file content.
	 */
	public ArrayList<String> readFile()
	{
		File file = new File(logFilePath);
		ArrayList<String> file_lines = new ArrayList<String>();
		
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(file));
		    String line;
		    
		    while((line = br.readLine()) != null)
		    {
		    	file_lines.add(line);
		    }
		}
		catch(Exception e)
		{
			Log.e(TAG, e.getMessage());
		}
		
		return file_lines;
	}
	
	public void writeFile(String text)
	{
		File file = new File(logFilePath);
		
		try
		{
			FileWriter writer = new FileWriter(file, true);
	        
			writer.append(text);
			writer.append("\r\n");
			writer.flush();
	        writer.close();
		}
		catch(Exception e)
		{
			Log.e(TAG, e.getMessage());
		}
	}
}
