/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */

package au.com.tyo.android.info;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;

public class Contact {

	public static Cursor getContactCursor(Context context, String name) {
		 ContentResolver cr = context.getContentResolver();
		  Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
		    null, null, null);
		return cursor;
	}
	
	public static Bitmap getContactImage(Context context, Cursor cursor) {
		ContentResolver cr = context.getContentResolver();
		Bitmap bitmap = null;
//		if (cursor.getCount() > 0) {
//			   while (cursor.moveToNext()) {
				   
			    String id = cursor.getString(cursor
			      .getColumnIndex(ContactsContract.Contacts._ID));
			    
			    String name = cursor
			      .getString(cursor
			        .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

			    String imageUrl = cursor
			      .getString(cursor
			        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
//			    if (Integer
//			      .parseInt(cursor.getString(cursor
//			        .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
//
//			     Cursor pCur = cr.query(
//			       ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//			       null,
//			       ContactsContract.CommonDataKinds.Phone.CONTACT_ID
//			         + " = ?", new String[] { id }, null);
//			     while (pCur.moveToNext()) {
//			      phone = pCur
//			        .getString(pCur
//			          .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//			      sb.append("\n Phone number:" + phone);
//			      System.out.println("phone" + phone);
//			     }
//			     pCur.close();


//			    }
			    
			    if (imageUrl != null) {
			     System.out.println(Uri.parse(imageUrl));
			     try {
			      bitmap = MediaStore.Images.Media
			        .getBitmap(cr,
			          Uri.parse(imageUrl));
			      System.out.println(bitmap);

			     } catch (FileNotFoundException e) {
			      e.printStackTrace();
			     } catch (IOException e) {
			      e.printStackTrace();
			     }

			    }
				return bitmap;
//	}
//		}
	}
	
	public static String getContactEmail(Context context, String id) {
		 ContentResolver cr = context.getContentResolver();
		 
		 Cursor emailCur = cr.query(
			       ContactsContract.CommonDataKinds.Email.CONTENT_URI,
			       null,
			       ContactsContract.CommonDataKinds.Email.CONTACT_ID
			         + " = ?", new String[] { id }, null);
	     
	     String email = null;
	     while (emailCur.moveToNext()) {
	      email = emailCur
	        .getString(emailCur
	          .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
//	      emailType = emailCur
//	        .getString(emailCur
//	          .getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

	     }

	     emailCur.close();
		return email;
			     
	}
}
