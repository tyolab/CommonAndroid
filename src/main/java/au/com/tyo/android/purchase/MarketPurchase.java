package au.com.tyo.android.purchase;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import au.com.tyo.android.AndroidMarket;
import au.com.tyo.android.utils.IabHelper;
import au.com.tyo.android.utils.IabResult;
import au.com.tyo.android.utils.Inventory;
import au.com.tyo.android.utils.Purchase;
import au.com.tyo.utils.RandomString;

public abstract class MarketPurchase {
	
    private static final String LOG_TAG = "MarketPurchase";
    
    static final int RC_REQUEST = 10001;
    
	// The helper object
    private IabHelper mHelper;
    
    private AndroidMarket market;
    
    private Context context;
    
    private IabHelper.QueryInventoryFinishedListener mGotInventoryListener;
    private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;
    private IabHelper.OnConsumeFinishedListener mConsumeFinishedListener;
    
    private RandomString randomStr;
    
    private PurchaseController controller;
    
    private Inventory purcahseInventory;
    
    public MarketPurchase(Context context, PurchaseController controller, AndroidMarket market) {
    	this.context = context;
    	this.controller = controller;
    	this.market = market;
    	
    	randomStr = new RandomString(24);

        createTransactionHelper();
        inventoryCheck();
        this.setOnConsumeFinishedListener();
        this.setOnPurchaseFinishListener();
    }
    
    public IabHelper getHelper() {
		return mHelper;
	}

	public void setHelper(IabHelper mHelper) {
		this.mHelper = mHelper;
	}

	public IabHelper.OnConsumeFinishedListener getConsumeFinishedListener() {
		return mConsumeFinishedListener;
	}

	public void setConsumeFinishedListener(IabHelper.OnConsumeFinishedListener mConsumeFinishedListener) {
		this.mConsumeFinishedListener = mConsumeFinishedListener;
	}

	private void createTransactionHelper() {
        // Create the helper, passing it our context and the public key to verify signatures with
//      Log.d(LOG_TAG, "Creating IAB helper.");
      setHelper(new IabHelper(context, market.getBase64PublicKey()));

      // enable debug logging (for a production application, you should set this to false).
      getHelper().enableDebugLogging(false);
      
      // Start setup. This is asynchronous and the specified listener
      // will be called once setup completes.
      Log.d(LOG_TAG, "Starting transaction setup.");
      getHelper().startSetup(new IabHelper.OnIabSetupFinishedListener() {
          public void onIabSetupFinished(IabResult result) {
              Log.d(LOG_TAG, "Transaction setup finished.");

              if (!result.isSuccess()) {
                  // Oh noes, there was a problem.
                  controller.onSetupTransactionError(result);
                  return;
              }

              // Have we been disposed of in the meantime? If so, quit.
              if (getHelper() == null) return;

              // IAB is fully set up. Now, let's get an inventory of stuff we own.
              Log.d(LOG_TAG, "Transaction setup successful. Querying inventory.");
              getHelper().queryInventoryAsync(mGotInventoryListener);
          }
      });
	}

	abstract protected void checkPurchasedItems(Inventory inventory);
    
    private void inventoryCheck() {
    	
	    // Listener that's called when we finish querying the items and subscriptions we own
	    mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
	        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
	            Log.d(LOG_TAG, "Query inventory finished.");
	            
	            purcahseInventory = inventory;
	
	            // Have we been disposed of in the meantime? If so, quit.
	            if (getHelper() == null) return;
	
	            // Is it a failure?
	            if (result.isFailure()) {
	                controller.onInventoryCheckError(result);
	                return;
	            }
	
	            Log.d(LOG_TAG, "Query inventory was successful.");
	
	            /*
	             * Check for items we own. Notice that for each purchase, we check
	             * the developer payload to see if it's correct! See
	             * verifyDeveloperPayload().
	             */
	
	            checkPurchasedItems(inventory);
	            // Do we have the premium upgrade?
	//            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
	//            mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
	//            Log.d(LOG_TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
	//
	//            // Do we have the infinite gas plan?
	//            Purchase infiniteGasPurchase = inventory.getPurchase(SKU_INFINITE_GAS);
	//            mSubscribedToInfiniteGas = (infiniteGasPurchase != null &&
	//                    verifyDeveloperPayload(infiniteGasPurchase));
	//            Log.d(LOG_TAG, "User " + (mSubscribedToInfiniteGas ? "HAS" : "DOES NOT HAVE")
	//                        + " infinite gas subscription.");
	//            if (mSubscribedToInfiniteGas) mTank = TANK_MAX;
	//
	//            // Check for gas delivery -- if we own gas, we should fill up the tank immediately
	//            Purchase gasPurchase = inventory.getPurchase(SKU_GAS);
	//            if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
	//                Log.d(LOG_TAG, "We have gas. Consuming it.");
	//                mHelper.consumeAsync(inventory.getPurchase(SKU_GAS), mConsumeFinishedListener);
	//                return;
	//            }
	
	//            updateUi();
	            controller.setWaitScreen(false);
	            Log.d(LOG_TAG, "Initial inventory query finished;");
	        }
	    };
	    
    }
    
    // Callback for when a purchase is finished
    public void setOnPurchaseFinishListener() {
	    mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
	        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
	            Log.d(LOG_TAG, "Transaction finished: " + result + ", transaction: " + purchase);
	
	            // if we were disposed of in the meantime, quit.
	            if (getHelper() == null) return;
	
	            if (result.isFailure()) {
//	                complain("Error in the transaction: " + result);
	                controller.setWaitScreen(false);
	            	controller.onTransactionError(result);
	                return;
	            }
	            if (!verifyDeveloperPayload(purchase)) {
	                controller.onDeveloperPayLoadVerificationFailed(); //complain("Error in the transaction. Authenticity verification failed.");
	                controller.setWaitScreen(false);
	                return;
	            }
	
	            Log.d(LOG_TAG, "Transaction successful.");
	            
	            controller.onPurchaseFinished(purchase);
	
	            /*
	             * Below are the examples what should be done after successful purchase
	             */
//	            if (purchase.getSku().equals(SKU_GAS)) {
//	                // bought 1/4 tank of gas. So consume it.
//	                Log.d(LOG_TAG, "Purchase is gas. Starting gas consumption.");
//	                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
//	            }Activity activity, 
//	            else if (purchase.getSku().equals(SKU_PREMIUM)) {
//	                // bought the premium upgrade!
//	                Log.d(LOG_TAG, "Purchase is premium upgrade. Congratulating user.");
//	                alert("Thank you for upgrading to premium!");
//	                mIsPremium = true;
//	                updateUi();
//	                controller.setWaitScreen(false);
//	            }
//	            else if (purchase.getSku().equals(SKU_INFINITE_GAS)) {
//	                // bought the infinite gas subscription
//	                Log.d(LOG_TAG, "Infinite gas subscription purchased.");
//	                alert("Thank you for subscribing to infinite gas!");
//	                mSubscribedToInfiniteGas = true;Activity activity, 
//	                mTank = TANK_MAX;
//	                updateUi();
//	                controller.setWaitScreen(false);
//	            }
	        }
	    };
    }

    // Called when consumption is complete
    public void setOnConsumeFinishedListener() {
//    IabHelper.OnConsumeFinishedListener 
	    setConsumeFinishedListener(new IabHelper.OnConsumeFinishedListener() {
	        public void onConsumeFinished(Purchase purchase, IabResult result) {
	            Log.d(LOG_TAG, "Consumption finished. Transaction: " + purchase + ", result: " + result);
	
	            // if we were disposed of in the meantime, quit.
	            if (getHelper() == null) return;
	
	            // We know this is the "gas" sku because it's the only one we consume,
	            // so we don't check which sku was consumed. If you have more than one
	            // sku, you probably should check...
	            if (result.isSuccess()) {
	                // successfully consumed, so we apply the effects of the item in our
	                // game world's logic, which in our case means filling the gas tank a bit
//	                Log.d(LOG_TAG, "Consumption successful. Provisioning.");
//	                mTank = mTank == TANK_MAX ? TANK_MAX : mTank + 1;
//	                saveData();
//	                alert("You filled 1/4 tank. Your tank is now " + String.valueOf(mTank) + "/4 full!");
	            	controller.onConsumeSuccessful(purchase.getSku());
	            }
	            else {
	                controller.onConsumeError();
	            }
	            controller.onConsumeFinished();
	            Log.d(LOG_TAG, "End consumption flow.");
	        }
	    });
    }
    
    public void purchase(Activity activity, String sku) {
    	controller.setWaitScreen(false);
    	
        String payload = randomStr.nextString();

        try {
        	getHelper().flagEndAsync();
        	getHelper().launchPurchaseFlow(activity, sku, RC_REQUEST,
                mPurchaseFinishedListener, payload);
        }
        catch (Exception ex) {
        	Log.e(LOG_TAG, ex.getMessage() != null ? ex.getMessage() : "something wrong when starting transaction.");
        }
    }
    
    public void consume(String sku) {
    	if (purcahseInventory != null)
    		getHelper().consumeAsync(purcahseInventory.getPurchase(sku), getConsumeFinishedListener());
    }
    
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }
    
    public void finish() {
        if (getHelper() != null) {
            getHelper().dispose();
            setHelper(null);
        }
    }
    
//    void complain(String message) {
//        Log.e(LOG_TAG, "Error: " + message);
//        alert("Error: " + message);
//    }
//
//    void alert(String message) {
//        AlertDialog.Builder bld = new AlertDialog.Builder(context);
//        bld.setMessage(message);
//        bld.setNeutralButton("OK", null);
//        Log.d(LOG_TAG, "Showing alert dialog: " + message);
//        bld.create().show();
//    }
}
