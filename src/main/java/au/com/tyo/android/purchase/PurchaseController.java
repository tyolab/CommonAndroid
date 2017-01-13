package au.com.tyo.android.purchase;

import android.app.Activity;
import au.com.tyo.android.utils.IabResult;
import au.com.tyo.android.utils.Purchase;

public interface PurchaseController {

	void setWaitScreen(boolean b);

	void onPurchaseFinished(Purchase purchase);

	void onConsumeSuccessful(String sku);

	void onConsumeError();

	void onConsumeFinished();

	void onSetupTransactionError(IabResult result);

	void onInventoryCheckError(IabResult result);

	void onTransactionError(IabResult result);

	void onDeveloperPayLoadVerificationFailed();

	void purchase(Activity activity, String productId);
}
