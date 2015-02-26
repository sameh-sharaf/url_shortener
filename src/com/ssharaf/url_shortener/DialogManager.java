package com.ssharaf.url_shortener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Dialog manager class.
 * 
 * @author SAMEH SHARAF
 *
 */

public class DialogManager {
		
	public DialogManager()
	{
	}

	/*
	 * Show dialog message
	 */
	public void showDialog(Activity activity, String title, CharSequence message, String ok_caption, String cancel_caption) 
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        // Set dialog title.
        builder.setTitle(title);

        // Set dialog message.
        builder.setMessage(message);
        
        // Create OK button.
        builder.setPositiveButton(ok_caption, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int whichButton)
            {
            	// Nothing to do now
            }
        });
        
        // Cancel button will be created in case cancel caption is sent.
        if (cancel_caption != null)
	        builder.setNegativeButton(cancel_caption, new DialogInterface.OnClickListener()
	        {
	            @Override
	            public void onClick(DialogInterface dialog, int whichButton)
	            {
	            	// Nothing to do now
	            }
	        });
        
        // Show dialog.
        builder.show();
    }
	
}
