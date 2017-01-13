/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */

package au.com.tyo.android.widget;

import java.lang.reflect.Method;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import au.com.tyo.android.AndroidUtils;
import au.com.tyo.android.R;

public class FindOnPage implements OnClickListener, OnEditorActionListener {

	private Dialog findOnPageDialog;
	
//	private OnFindOnPageListener listener;
	
	private ImageView btnFindNext;
	
	private ImageView btnFindPrevious;
	
	private EditText etWhatToFind;
	
	private WebView wv;
	
	public FindOnPage(WebView wv) {
//		this.listener = null;
		this.wv = wv;
	}
	
//	public interface OnFindOnPageListener {
//		void onFindNext(String string);
//		void onFindPrevious(String what);
//		void onStart();
//	}
	
	public void create() {
		findOnPageDialog = createDialog(wv.getContext());
		
		btnFindNext = (ImageView) findOnPageDialog.findViewById(R.id.iv_find_on_page_down);
		btnFindPrevious = (ImageView) findOnPageDialog.findViewById(R.id.iv_find_on_page_up);
		
		btnFindNext.setOnClickListener(this);
		btnFindPrevious.setOnClickListener(this);
		
		etWhatToFind = (EditText) findOnPageDialog.findViewById(R.id.tv_find_on_page);
		etWhatToFind.setOnEditorActionListener(this);
	}
	
	public void show() {
		findOnPageDialog.show();
	}

//	public void setListener(OnFindOnPageListener listener) {
//		this.listener = listener;
//	}

	public static Dialog createDialog(Context context) {
		Dialog findOnPageDialog = FindOnPage.createDialog(context);
		
		View view = ((Activity) context).getLayoutInflater().inflate(R.layout.find_on_page, null, false);
		findOnPageDialog.setContentView(view);
		
		Window window = findOnPageDialog.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();

		
		wlp.gravity = Gravity.TOP;
		wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(wlp);
		return findOnPageDialog;
	}

	@Override
	public void onClick(View v) {
//		if (listener != null) {
			String what = etWhatToFind.getText().toString();
			if (v == btnFindNext)
				wv.findNext(true);
			else if (v == btnFindPrevious)
				wv.findNext(false);
//		}
	}

	@SuppressLint("NewApi") @Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//		if((event.getAction() == KeyEvent.ACTION_DOWN) && ((event.ge == KeyEvent.KEYCODE_ENTER))){  
		String what = etWhatToFind.getText().toString();
		if (AndroidUtils.getAndroidVersion() < 11)
			wv.findAll(what);  
		else
			wv.findAllAsync(what);
		
		try{  
			Method m = WebView.class.getMethod("setFindIsUp", Boolean.TYPE);  
			m.invoke(wv, true);  
		}catch(Exception ignored){
		}  

		return false;
	}

}
