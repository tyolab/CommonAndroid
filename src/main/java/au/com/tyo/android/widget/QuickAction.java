package au.com.tyo.android.widget;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.widget.PopupWindow.OnDismissListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import au.com.tyo.android.R;


/**
 * Adapted from
 * 
 * #################################################
 * ActionItemList dialog.
 * 
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 * 
 * Contributors:
 * - Kevin Peck <kevinwpeck@gmail.com>
 * ##################################################
 */
public class QuickAction extends PopupWindows implements OnDismissListener {
//	private ImageView mArrowUp;
//	private ImageView mArrowDown;
	private Animation mTrackAnim;
	private LayoutInflater inflater;
	protected ViewGroup mTrack;
//	private ListView mTrack;
	protected OnActionItemClickListener mItemClickListener;
	private OnDismissListener mDismissListener;
	
	private List<ActionItem> mActionItemList = new ArrayList<ActionItem>();
	
	protected boolean mDidAction;
	private boolean mAnimateTrack;
	
	protected int mChildPos;    
    private int mAnimStyle;
    
	public static final int ANIM_GROW_FROM_LEFT = 1;
	public static final int ANIM_GROW_FROM_RIGHT = 2;
	public static final int ANIM_GROW_FROM_CENTER = 3;
	public static final int ANIM_AUTO = 4;
	
	protected Context context;
	
	
	/**
	 * Constructor.
	 * 
	 * @param context Context
	 */
	public QuickAction(Context context, int resourceId) {
		super(context);
		this.context = context;
		
		inflater 	= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mTrackAnim 	= AnimationUtils.loadAnimation(context, R.anim.rail);
		
		mTrackAnim.setInterpolator(new Interpolator() {
			public float getInterpolation(float t) {
	              // Pushes past the target area, then snaps back into place.
	                // Equation for graphing: 1.2-((x*1.6)-1.1)^2
				final float inner = (t * 1.55f) - 1.1f;
				
	            return 1.2f - inner * inner;
	        }
		});
	        
		setRootViewId(resourceId);
		
		mAnimStyle		= ANIM_AUTO;
		mAnimateTrack	= true;
		mChildPos		= 0;
	}

	public QuickAction(Context context) {
		this(context, R.layout.popup);
	}
	
	/**
     * Get link item at an index
     * 
     * @param index  Index of item (position from callback)
     * 
     * @return  Action Item at the position
     */
    public ActionItem getActionItem(int index) {
        return mActionItemList.get(index);
    }
    
	/**
	 * Set root view.
	 * 
	 * @param id Layout resource id
	 */
	public void setRootViewId(int id) {
		mRootView	= (ViewGroup) inflater.inflate(id, null);
		
		//This was previously defined on show() method, moved here to prevent force close that occured
		//when tapping fastly on a view to show quicklink dialog.
		//Thanx to zammbi (github.com/zammbi)
//		mRootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
		setContentView(mRootView);
		
		setTracksView();
//		setArrowViews();
	}
	
	
//	protected void setArrowViews() {
//		mArrowDown 	= (ImageView) mRootView.findViewById(R.id.quickaction_arrow_down);
//		mArrowUp 	= (ImageView) mRootView.findViewById(R.id.quickaction_arrow_up);	
//	}

	protected void setTracksView() {
		mTrack 		= (ViewGroup) mRootView.findViewById(R.id.quickaction_tracks);
	}
	
	/**
	 * Animate track.
	 * 
	 * @param mAnimateTrack flag to animate track
	 */
	public void mAnimateTrack(boolean mAnimateTrack) {
		this.mAnimateTrack = mAnimateTrack;
	}
	
	/**
	 * Set animation style.
	 * 
	 * @param mAnimStyle animation style, default is set to ANIM_AUTO
	 */
	public void setAnimStyle(int mAnimStyle) {
		this.mAnimStyle = mAnimStyle;
	}
	
	protected View creatActionItemContainer(int resourceId) {
		View container	= (View) inflater.inflate(resourceId, null);
		
//		ImageView img 	= (ImageView) container.findViewById(R.id.iv_icon);

		return container;
	}
	
//	public void addActionItem(ActionItem action) {
//		this.addActionItem(action, R.layout.action_item);
//	}
	
	/**
	 * Add action item
	 * 
	 * @param action  {@link ActionItem}
	 */
	public void addActionItem(ActionItem action, View container) {
		mActionItemList.add(action);
//		action.setActionId(mActionItemList.size() -1);
		
//		String title 	= action.getTitle();
//		Drawable icon 	= action.getIcon();
//		
//		View container	= (View) inflater.inflate(resourceId, null);
//		
//		TextView selected = (TextView) container.findViewById(R.id.tv_selected);
//		action.addChildView(selected);
////		selected.setVisibility(View.GONE);
//		
//		ImageView img 	= (ImageView) container.findViewById(R.id.iv_icon);
//		action.addChildView(img);
//		
//		TextView text 	= (TextView) container.findViewById(R.id.tv_title);
//		action.addChildView(text);
//		
//		if (icon != null) { 
//			img.setImageDrawable(icon);
//		} else {
//			img.setVisibility(View.GONE);
//		}
//		
//		if (title != null) {
//			text.setText(title);
//		} else {
//			text.setVisibility(View.GONE);
//		}
//		
//		final int pos 		=  mChildPos;
//		final int actionId 	= action.getActionId();
		final ItemInfo info = action;
		
		container.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(QuickAction.this, info);
                }
				
				ActionItem item = (ActionItem) info;
                if (!item.isSticky()) {  
                	mDidAction = true;
                	
                	//workaround for transparent background bug
                	//thx to Roman Wozniak <roman.wozniak@gmail.com>
                	v.post(new Runnable() {
                        public void run() {
                            dismiss();
                        }
                    });
                }
			}
		});
		
		container.setFocusable(true);
		container.setClickable(true);
			 
//		mTrack.addView(container, mChildPos);
		if (mChildPos > 0) {
			View border = this.inflater.inflate(R.layout.border, null);
			mTrack.addView(border);
		}
		mTrack.addView(container);
		
		mChildPos++;
	}
	
	public void setOnActionItemClickListener(OnActionItemClickListener listener) {
		mItemClickListener = listener;
	}
	
	/**
	 * Show popup mWindow
	 */
	@SuppressLint("NewApi")
	public void show (View anchor) {
//		if (linkCount > 0) {
			preShow();
	
			int[] location 		= new int[2];
			
			mDidAction 			= false;
			
			anchor.getLocationOnScreen(location);
	
			Rect anchorRect 	= new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1] 
			                	+ anchor.getHeight());
	
//			mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			
			int rootWidth 		= mRootView.getMeasuredWidth();
			int rootHeight 		= mRootView.getMeasuredHeight();
	
			int screenWidth 	= mWindowManager.getDefaultDisplay().getWidth();
			//int screenHeight 	= mWindowManager.getDefaultDisplay().getHeight();
	
			/*
			 * TODO
			 * may adjust this in the feature
			 */
			int mid = screenWidth / 2;
			int xPos 			= 0;
			if (anchorRect.left > mid)
				xPos = anchorRect.left - rootWidth + anchor.getWidth() / 2;
			else
				xPos = anchorRect.left;

			int yPos = anchorRect.top - rootHeight;
	
			boolean onTop		= true;
			
			// display on bottom
			if (rootHeight > anchor.getTop()) {
				yPos 	= anchorRect.bottom;
				onTop	= false;
			}
	
//			showArrow(((onTop) ? R.id.quickaction_arrow_down : R.id.quickaction_arrow_up), anchorRect.centerX());
			
			setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);
		
			mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
//			mWindow.showAsDropDown(anchor, xPos, xPos, Gravity.LEFT);
			
			if (mAnimateTrack) mTrack.startAnimation(mTrackAnim);
//		}
	}

	/**
	 * Set animation style
	 * 
	 * @param screenWidth Screen width
	 * @param requestedX distance from left screen
	 * @param onTop flag to indicate where the popup should be displayed. Set TRUE if displayed on top of anchor and vice versa
	 */
	private void setAnimationStyle(int screenWidth, int requestedX, boolean onTop) {
		int arrowPos = requestedX; // - mArrowUp.getMeasuredWidth()/2;

		switch (mAnimStyle) {
		case ANIM_GROW_FROM_LEFT:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
			break;
					
		case ANIM_GROW_FROM_RIGHT:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right);
			break;
					
		case ANIM_GROW_FROM_CENTER:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
		break;
					
		case ANIM_AUTO:
			if (arrowPos <= screenWidth/4) {
				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
			} else if (arrowPos > screenWidth/4 && arrowPos < 3 * (screenWidth/4)) {
				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
			} else {
				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right);
			}
					
			break;
		}
	}
	
//	/**
//	 * Show arrow
//	 * 
//	 * @param whichArrow arrow type resource id
//	 * @param requestedX distance from left screen
//	 */
//	private void showArrow(int whichArrow, int requestedX) {
//        final View showArrow = (whichArrow == R.id.quickaction_arrow_up) ? mArrowUp : mArrowDown;
//        final View hideArrow = (whichArrow == R.id.quickaction_arrow_up) ? mArrowDown : mArrowUp;
//
//        final int arrowWidth = mArrowUp.getMeasuredWidth();
//
//        showArrow.setVisibility(View.VISIBLE);
//        
//        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)showArrow.getLayoutParams();
//        
//        param.leftMargin = requestedX - arrowWidth / 2;
//      
//        hideArrow.setVisibility(View.INVISIBLE);
//    }
	
	/**
	 * Set listener for window dismissed. This listener will only be fired if the quicakction dialog is dismissed
	 * by clicking outside the dialog or clicking on sticky item.
	 */
	public void setOnDismissListener(QuickAction.OnDismissListener listener) {
		setOnDismissListener(this);
		
		mDismissListener = listener;
	}
	
	@Override
	public void onDismiss() {
		if (!mDidAction && mDismissListener != null) {
			mDismissListener.onDismiss();
		}
	}
	
	protected void addChildView(View group) {
		mTrack.addView(group);
		mChildPos++;
	}

	protected void clear() {
		mActionItemList.clear();
		mChildPos = 0;
	}
	
	/**
	 * Listener for item click
	 *
	 */
	public interface OnActionItemClickListener {
		public abstract void onItemClick(QuickAction source, ItemInfo info);
	}
	
	/**
	 * Listener for window dismiss
	 * 
	 */
	public interface OnDismissListener {
		public abstract void onDismiss();
	}
	
	public interface ItemInfo {
		public Object getItemId();
	}
}