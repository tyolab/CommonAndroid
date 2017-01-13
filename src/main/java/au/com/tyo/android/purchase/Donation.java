package au.com.tyo.android.purchase;

import android.content.Context;
import android.util.Log;
import au.com.tyo.android.AndroidMarket;
import au.com.tyo.android.utils.Inventory;
import au.com.tyo.android.utils.Purchase;

public class Donation extends MarketPurchase {

	public static final String SKU_1_DOLLAR = "1dollar";
    
    public static final String SKU_2_DOLLARS = "2dollars";

    public static final String SKU_3_DOLLARS = "3dollars";
    
    public static final String[] SKUS = new String[] {SKU_1_DOLLAR, SKU_2_DOLLARS, SKU_3_DOLLARS};

	private static final String LOG_TAG = "Donation";
    
	public Donation(Context context, PurchaseController controller,
			AndroidMarket market) {
		super(context, controller, market);
		
	}

	@Override
	protected void checkPurchasedItems(Inventory inventory) {
		for (String sku : SKUS) {
	        Purchase purchase = inventory.getPurchase(sku);
	        if (purchase != null && verifyDeveloperPayload(purchase)) {
	            Log.d(LOG_TAG, "Thank you for your generosity.");
	            getHelper().consumeAsync(inventory.getPurchase(sku), getConsumeFinishedListener());
	        }		
		}
	}
    
}
