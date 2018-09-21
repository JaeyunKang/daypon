package com.daypon.ccswwf.dailycoupon;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.Constants;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;

public class MembershipApplyActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    TextView termText;
    CheckBox termsCheck;
    LinearLayout membershipApplyBtn;
    LinearLayout membershipApplyBtnKakao;

    private BillingProcessor bp;
    SkuDetails skuDetails;
    String productId = "membership3900";

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership_apply);

        bp = new BillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnc70FWx1v+7G9PyBroNtnvniaxwOUpGDJQtv1ASmrLEcngXlNofYZE5ml3nHD2SfunxZHIqMQHKrMPnaHqcDd2uFgzcdX1fcFyMF7YwbRoUG8K7+s/SwUE5vvLwRCN+MYqnHREXwBz4VRmnB730RHwaEvMg+fMOTBqtqyGSgMst9BDJJKIIvfZwH1FDrlHwzCS0UC91pwt9rwwLtNH9Z7YCpmY3aePAJfvat+cyUX8gECHGDgS32D2es96DVBC96/Zzmxxp16xsAYKlEKtSWVoJRSIThOArJig4+UOwneLfPOJM2iVDtNpHL3zzHcGWB7O8XI9GgUg/G9/+b8yPRaQIDAQAB", this);

        SharedPreferences userInformation = getSharedPreferences("user", 0);
        userId = userInformation.getString("user_id", "");

        termText = (TextView) findViewById(R.id.text_term);
        termText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent termsActivity = new Intent(getApplicationContext(), TermsActivity.class);
                startActivity(termsActivity);
            }
        });
        termsCheck = (CheckBox) findViewById(R.id.terms_check);

        membershipApplyBtn = (LinearLayout) findViewById(R.id.membership_apply_btn);
        //membershipApplyBtnKakao = (LinearLayout) findViewById(R.id.membership_apply_btn_kakao);
        membershipApplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!termsCheck.isChecked()) {
                    Toast.makeText(MembershipApplyActivity.this, "동의가 필요합니다.", Toast.LENGTH_LONG).show();
                    return;
                } else if (bp.isPurchased(productId)) {
                    bp.consumePurchase(productId);
                } else {
                    bp.purchase(MembershipApplyActivity.this, productId);
                }
            }
        });
    }

    @Override
    public void onBillingInitialized() {
        skuDetails = bp.getSubscriptionListingDetails(productId);
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("price", skuDetails.priceLong);
        values.put("pay_method", "google");
        MembershipApplyTask task = new MembershipApplyTask(values);
        task.execute();
    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        if (errorCode != Constants.BILLING_RESPONSE_RESULT_USER_CANCELED) {
            String errorMessage = "결제에 실패하였습니다 (" + errorCode + ")";
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    private class MembershipApplyTask extends AsyncTask<Void, Void, String> {

        String url = "http://prography.org/membership-apply";
        ContentValues values;

        MembershipApplyTask(ContentValues values) {
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                Intent completeActivity = new Intent(MembershipApplyActivity.this, MembershipApplyCompleteActivity.class);
                startActivity(completeActivity);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

}