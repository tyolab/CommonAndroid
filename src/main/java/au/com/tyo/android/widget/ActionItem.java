package au.com.tyo.android.widget;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import au.com.tyo.android.widget.QuickAction.ItemInfo;

/**
 * 
 * Adopted from
 * 
 * ##################################################
 * Link item, displayed as menu with icon and text.
 * 
 * @author Lorensius. W. L. T <lorenz@londatiga.net>
 * 
 * Contributors:
 * - Kevin Peck <kevinwpeck@gmail.com>
 * ###################################################
 *
 */

public class ActionItem implements ItemInfo {
	public static final int VIEW_INDEX_ITEM_ICON = 1;
	public static final int VIEW_INDEX_ITEM_TITLE = 2;
	public static final int VIEW_INDEX_ITEM_SELECTED = 0;
	
	private Drawable icon;
	private Bitmap thumb;
	private String title;
	private int actionId = -1;
    private boolean selected;
    private boolean sticky;
    
    protected ArrayList<View> children;
    
    
    
    /**
     * Constructor
     * 
     * @param actionId  Action id for case statements
     * @param title     Title
     * @param icon      Icon to use
     */
    public ActionItem(int actionId, String title, Drawable icon) {
        this.title = title;
        this.icon = icon;
        this.actionId = actionId;
        
        init();
    }

	/**
     * Constructor
     */
    public ActionItem() {
        this(-1, null, null);
    }
    
    /**
     * Constructor
     * 
     * @param actionId  Action id of the item
     * @param title     Text to show for the item
     */
    public ActionItem(int actionId, String title) {
        this(actionId, title, null);
    }
    
    /**
     * Constructor
     * 
     * @param icon {@link Drawable} action icon
     */
    public ActionItem(Drawable icon) {
        this(-1, null, icon);
    }
    
    /**
     * Constructor
     * 
     * @param actionId  Action ID of item
     * @param icon      {@link Drawable} action icon
     */
    public ActionItem(int actionId, Drawable icon) {
        this(actionId, null, icon);
        
    }
    
    private void init() {
    	children = new ArrayList<View>();
	}
    
	/**
	 * Set link title
	 * 
	 * @param title link title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Get link title
	 * 
	 * @return link title
	 */
	public String getTitle() {
		return this.title;
	}
	
	 /**
    * Set link id
    * 
    * @param actionId  Link id for this link
    */
   public void setActionId(int actionId) {
       this.actionId = actionId;
   }
   
   /**
    * @return  Our link id
    */
   public int getActionId() {
       return actionId;
   }
   
	/**
	 * Set link icon
	 * 
	 * @param icon {@link Drawable} link icon
	 */
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	
	/**
	 * Get link icon
	 * @return  {@link Drawable} link icon
	 */
	public Drawable getIcon() {
		return this.icon;
	}
    
    /**
     * Set sticky status of button
     * 
     * @param sticky  true for sticky, pop up sends event but does not disappear
     */
    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }
    
    /**
     * @return  true if button is sticky, menu stays visible after press
     */
    public boolean isSticky() {
        return sticky;
    }
    
	/**
	 * Set selected flag;
	 * 
	 * @param selected Flag to indicate the item is selected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	/**
	 * Check if item is selected
	 * 
	 * @return true or false
	 */
	public boolean isSelected() {
		return this.selected;
	}

	/**
	 * Set thumb
	 * 
	 * @param thumb Thumb image
	 */
	public void setThumb(Bitmap thumb) {
		this.thumb = thumb;
	}
	
	/**
	 * Get thumb image
	 * 
	 * @return Thumb image
	 */
	public Bitmap getThumb() {
		return this.thumb;
	}

	public Object getItemId() {
		return this.actionId;
	}
	
	public void addChildView(View view) {
		this.children.add(view);
	}
}
