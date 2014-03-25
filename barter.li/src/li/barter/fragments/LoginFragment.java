/*******************************************************************************
 * Copyright 2014, barter.li
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package li.barter.fragments;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.facebook.Session;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import li.barter.R;
import li.barter.http.BlRequest;
import li.barter.http.HttpConstants;
import li.barter.http.HttpConstants.ApiEndpoints;
import li.barter.http.HttpConstants.RequestId;
import li.barter.http.ResponseInfo;
import li.barter.utils.AppConstants;
import li.barter.utils.AppConstants.Keys;
import li.barter.utils.Logger;

@FragmentTransition(enterAnimation = R.anim.activity_slide_in_right, exitAnimation = R.anim.activity_scale_out, popEnterAnimation = R.anim.activity_scale_in, popExitAnimation = R.anim.activity_slide_out_right)
public class LoginFragment extends AbstractBarterLiFragment implements
                OnClickListener, Listener<ResponseInfo>, ErrorListener {

    private static final String TAG                = "LoginFragment";

    /**
     * Minimum length of the entered password
     */
    private final int           mMinPasswordLength = 6;

    private Button              mFacebookLoginButton;
    private Button              mGoogleLoginButton;
    private Button              mSubmitButton;
    private EditText            mEmailEditText;
    private EditText            mPasswordEditText;

    @Override
    public View onCreateView(final LayoutInflater inflater,
                    final ViewGroup container, final Bundle savedInstanceState) {
        init(container);
        final View view = inflater.inflate(R.layout.fragment_login, null);

        mFacebookLoginButton = (Button) view
                        .findViewById(R.id.button_facebook_login);
        mGoogleLoginButton = (Button) view
                        .findViewById(R.id.button_google_login);
        mSubmitButton = (Button) view.findViewById(R.id.button_submit);
        mEmailEditText = (EditText) view.findViewById(R.id.edit_text_email);
        mPasswordEditText = (EditText) view
                        .findViewById(R.id.edit_text_password);

        mFacebookLoginButton.setOnClickListener(this);
        mGoogleLoginButton.setOnClickListener(this);
        mSubmitButton.setOnClickListener(this);
        setActionBarDrawerToggleEnabled(false);
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            popBackStack();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected Object getVolleyTag() {
        return TAG;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode,
                    final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession()
                        .onActivityResult(getActivity(), requestCode, resultCode, data);
    }

    @Override
    public void onClick(final View v) {

        switch (v.getId()) {

            case R.id.button_facebook_login: {
                // TODO FacebookLoggerin
                break;
            }

            case R.id.button_google_login: {
                // TODO GoogleLoggerin
                break;
            }

            case R.id.button_submit: {
                if (isInputValid()) {
                    login(mEmailEditText.getText().toString(), mPasswordEditText
                                    .getText().toString());
                }
                break;
            }
        }
    }

    /**
     * Call the login Api
     * 
     * @param email The entered email
     * @param password The entered password
     */
    private void login(final String email, final String password) {

        final JSONObject requestObject = new JSONObject();

        try {
            requestObject.put(HttpConstants.PROVIDER, AppConstants.MANUAL);
            requestObject.put(HttpConstants.EMAIL, email);
            requestObject.put(HttpConstants.PASSWORD, password);
            final BlRequest request = new BlRequest(Method.POST, RequestId.CREATE_USER, HttpConstants
                            .getApiBaseUrl() + ApiEndpoints.CREATE_USER, requestObject
                            .toString(), this, this);
            addRequestToQueue(request, true, 0);
        } catch(JSONException e) {
            // Should never happen
            Logger.e(TAG, e, "Error building create user json");
        }
        
    }

    /**
     * Validates the text fields for creating a user. Automatically sets the
     * error messages for the text fields
     * 
     * @return <code>true</code> If the input is valid, <code>false</code>
     *         otherwise
     */
    private boolean isInputValid() {

        final String email = mEmailEditText.getText().toString();
        boolean isValid = !TextUtils.isEmpty(email);

        if (!isValid) {
            mEmailEditText.setError(getString(R.string.error_enter_email));
        } else {
            //Using a regex for email comparison is pointless
            isValid &= email.contains("@");
            if (!isValid) {
                mEmailEditText.setError(getString(R.string.error_invalid_email));
            }
        }

        if (isValid) {
            final String password = mPasswordEditText.getText().toString();
            isValid &= !TextUtils.isEmpty(password);

            if (!isValid) {
                mPasswordEditText
                                .setError(getString(R.string.error_enter_password));
            } else {
                isValid &= (password.length() >= mMinPasswordLength);
                if (!isValid) {
                    mPasswordEditText
                                    .setError(getString(R.string.error_password_minimum_length, mMinPasswordLength));
                }
            }
        }

        return isValid;
    }

    @Override
    public void onErrorResponse(VolleyError error, Request<?> request) {
        onRequestFinished();
        Logger.e(TAG, error, "Error Response");

    }

    @Override
    public void onResponse(ResponseInfo response, Request<ResponseInfo> request) {
        onRequestFinished();
        Logger.d(TAG, "Response received");
    }

}