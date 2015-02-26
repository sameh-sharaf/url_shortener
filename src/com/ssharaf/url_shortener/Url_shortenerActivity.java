package com.ssharaf.url_shortener;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Application's core class which handles URL storing
 * and shorten algorithm.
 * 
 * @author SAMEH SHARAF
 *
 */

public class Url_shortenerActivity extends Activity {
    
	// Debug tag name.
	private static final String TAG = "URL Shortener";
	
	// Short link prefix. It is a domain to be applied when hosted to web. (Here, I used a fake one of course!)
	private static final String DOMAIN = "http://short-url.com/";
	
	// Initial URL ID to start with in case application starts the first time.
	private static final long INIT_URL_ID = 1000000;
	
	private long latest_url_id;
	
	// URL list created by application.
	ArrayList<URLModel> urls;
	
	// File manager object: To control reading/writing on log file.
	FileManager fileManager;
	
	/* 
	 * N-based base, here you can choose any format to convert keys. Here, I used 62-based which
	 * refers to number of characters used in shortened key ([0-9][A-Z][a-z]).
	 */
	private static final int format_base = 62;
	
	// Conversion table which has full map of 62-based format.
	private char[] convertTable;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Generate conversion table.
        generate_coversion_table();
        
        // Create file manager object to control log file.
        fileManager = new FileManager();
        
        // Read URL records from log file.
        readURLs();
        
        // Sort URL records by ID.
        Collections.sort(urls);
        
        /*
         * Get latest URL ID after reading stored URLs from log file.
         * In case no URLs stored in log file, it uses INIT_URL_ID as a starting ID.  
         */
        if (urls.size() == 0)
        {
        	latest_url_id = INIT_URL_ID;
        }
        else
        {
        	latest_url_id = urls.get(urls.size() - 1).getId();
        }
        
        for(int i=0; i<urls.size(); i++)
        {
        	Log.i(TAG, urls.get(i).toString());
        }
    }
    
    /**
     * Called when 'Shorten URL' button is clicked.
     * @param view 
     */
    public void shorten_url_button_onClick(View view)
    {
    	EditText editText = (EditText) findViewById(R.id.url_edit);
    	TextView textView = (TextView) findViewById(R.id.short_url_text);
    	
    	// Check user input
    	if (editText.getText().length() == 0)
    	{
    		// If user did not enter URL, show a dialog asking to enter one.
    		DialogManager dialog = new DialogManager();
    		dialog.showDialog(this, "URL not found!", "Please insert a valid URL.", "OK", null);
    		return;
    	}
    	
    	String short_url = new String("");
    	URLModel existing_url = searchURL(editText.getText().toString());
    	
    	if (existing_url != null)
    	{
    		Toast.makeText(getApplicationContext(), "URL already converted", Toast.LENGTH_LONG).show();
    		short_url = existing_url.getShortenedUrl();
    	}
    	else
    	{
	    	// Set ID for new URL
	    	long id = ++latest_url_id;
	    	
	    	/*
	    	 *  Fetch time stamp for new URL, it will be used as creation date
	    	 *  and a factor for generating shortened URL. 
	    	 */
	    	long timestamp = System.currentTimeMillis();
	    	
	    	// Original URL read from user input.
	    	String original_url = editText.getText().toString();
	    	
	    	// Time to generate shortened URL based on both time stamp and unique URL ID.
	    	short_url = shorten_url(editText.getText().toString(), timestamp + id);
	    	
	    	// Creation date based on time stamp.
	    	Date creation_date = new Date(timestamp);
	    	
	    	// Create URL model object to be inserted to URLs' Array List.
	    	URLModel url = new URLModel(id, original_url, short_url, creation_date);
	    	urls.add(url);
	    	
	    	// Write new URL to log file (appending).
	    	writeURL(url);
	    	
	    	Log.i(TAG, "URL shortened to: " + short_url);
    	}
    	
    	// Display shortened URL to screen
    	textView.setText(DOMAIN + short_url);
    }
    
    /**
     * Shorten given URL. It works by giving the URL a unique 
     * @param url URL to be shortened
     * @return Shortened URL
     */
    public String shorten_url(String url, long timestamp)
    {
    	Log.i(TAG, "URL to be shortened: " + url);
    	
    	ArrayList url62 = convert_base(timestamp);
    	
    	String url_code = "";
    	for(int i = 0; i < url62.size(); i++)
    	{
    		char conversion_char = convertTable[Integer.parseInt(url62.get(i).toString())];
    		url_code += conversion_char;
    		
    		Log.i(TAG, "URL 62: " + url62.get(i).toString() + " " + conversion_char);
    		
    	}
    	
    	return url_code;
    }
    
    /**
     * Convert key from decimal format to 62-base format.	
     * @param key Decimal key to be converted
     * @return Array List containing 62-based digits.
     */
    public ArrayList convert_base(long key)
    {
    	Log.i(TAG, "Calling 'convert_base' function");
    	ArrayList digits = new ArrayList();
    	
    	while (key != 0)
    	{
    		digits.add(key % format_base);
    		key = key / format_base;
    	}
    	
    	Log.i(TAG, "Return from 'convert_base' function");
    	return digits;
    }
    
    /**
     * Generate conversion table for shortening URL to 62-based digit.
     */
    public void generate_coversion_table()
    {
    	convertTable = new char[format_base]; 
    	
    	for(int i = 0; i < format_base; i++)
    	{
    		// 0-9
    		if (i < 10)
    		{
    			convertTable[i] = (char)((int) '0' + i);
    		}
    		else if (i >= 10 && i < 36) // A-Z
    		{
    				convertTable[i] = (char)((int) 'A' + i - 10);
    		}
    		else
    		{
    			convertTable[i] = (char)((int) 'a' + i - 36);
    		}
    	}
    }
    
    /**
     * Read URLs previously created by application and stored in log file.
     */
    public void readURLs()
    {
    	urls = new ArrayList<URLModel>();
    	
    	ArrayList<String> url_log = fileManager.readFile();
    	
    	for(int i=0; i<url_log.size(); i++)
    	{
    		String url_record = url_log.get(i);
    		
    		if(url_record.charAt(0) != '#')
    			urls.add(new URLModel(url_record));
    	}
    }
    
    /**
     * Write URL record into log file.
     */
    public void writeURL(URLModel url)
    {
    	String url_record = String.valueOf(url.getId())
    		+ ","
    		+ url.getOriginalUrl()
    		+ ","
    		+ url.getShortenedUrl()
    		+ ","
    		+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(url.getCreationDate());
    	
    	fileManager.writeFile(url_record);
    }
    
    /**
     * Search for URL if already existing in previously converted URLs list.
     * @param url URL to be searched for
     * @return URL model object if found, null if not. 
     */
    public URLModel searchURL(String url)
    {
    	for (int i=0; i<urls.size(); i++)
    	{
    		if (urls.get(i).getOriginalUrl().equals(url))
    			return urls.get(i);
    	}
    	
    	return null;
    }
}