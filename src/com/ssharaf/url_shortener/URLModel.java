package com.ssharaf.url_shortener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

public class URLModel implements Comparable 
{
	private long id;
	private String original_url;
	private String shortened_url;
	private Date creation_date;
	
	private static final String TAG = "URL Shortener - URLModel";
	
	/**
	 * Default constructor
	 */
	public URLModel()
	{
		
	}
	
	/**
	 * Class constructor
	 * @param id URL record ID.
	 * @param origin_url Original (long) URL.
	 * @param shortened_url Shortened URL.
	 * @param creation_date Shortened URL creation date.
	 */
	public URLModel(long id, String origin_url, String shortened_url, Date creation_date)
	{
		this.id = id;
		this.original_url = origin_url;
		this.shortened_url = shortened_url;
		this.creation_date = creation_date;
	}
	
	public URLModel(String url_record)
	{
		String[] url = url_record.split(",");
		
		id = Integer.parseInt(url[0]);
		original_url = url[1];
		shortened_url = url[2];
		
		try
		{
			creation_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(url[3]);
		}
		catch (ParseException e)
		{
			Log.e(TAG, e.getMessage());
		}
	}
	
	public long getId()
	{
		return id;
	}
	
	public String getOriginalUrl()
	{
		return original_url;
	}
	
	public String getShortenedUrl()
	{
		return shortened_url;
	}
	
	public Date getCreationDate()
	{
		return creation_date;
	}
	
	@Override
	public int compareTo(Object urlModel)
	{
		if (this.id == ((URLModel)urlModel).getId())
			return 0;
		else if (this.id > ((URLModel)urlModel).getId())
			return 1;
		else return -1;
	}
	
	@Override
    public String toString() {
        return "[ ID=" + id + ", URL=" + original_url + ", Shortened URL=" + shortened_url + 
        ", " + "Creation date=" + creation_date + "]";
    }
}