package com.sharefacebookexample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;

public class MainActivity extends Activity {

	private static final int FACEBOOK_SHARE_REQUEST_CODE = 1;

	private TextView mTvUsername;
	private Button mBtnLogin;

	private UiLifecycleHelper mUiHelper;

	private String mImageUrl = "http://www.epatage-club.ru/wp-content/uploads/2011/09/cat-200x200.jpg";

	/**
	 * Login facebook.
	 */
	private void loginFacebook() {
		// start Facebook Login.
		Session.openActiveSession(MainActivity.this, true, new Session.StatusCallback() {
			// callback when session changes state
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				if (session.isOpened()) { // login successfully, call share something to facebook.
					shareFacebook();
					mTvUsername.setText(session.getAccessToken());
				}
			}
		});
	}

	/**
	 * Share something to facebook via FacebookDialog.
	 */
	private void shareFacebook() {
		FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
				.setApplicationName(getResources().getString(R.string.app_name)).setPicture(mImageUrl)
				.setLink("https://www.facebook.com/").setDescription("").setRequestCode(FACEBOOK_SHARE_REQUEST_CODE)
				.build();
		mUiHelper.trackPendingDialogCall(shareDialog.present());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mUiHelper.onSaveInstanceState(outState);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// View will be used.
		mTvUsername = (TextView) findViewById(R.id.tvUsername);
		mBtnLogin = (Button) findViewById(R.id.btnLoginFacebook);

		mBtnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loginFacebook();
			}
		});

		// For share to facebook.
		mUiHelper = new UiLifecycleHelper(this, new StatusCallback() {
			@Override
			public void call(Session session, SessionState state, Exception exception) {
			}
		});
		mUiHelper.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mUiHelper.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mUiHelper.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mUiHelper.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == FACEBOOK_SHARE_REQUEST_CODE) { // Share to facebook.
			mUiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
				@Override
				public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
					// Share unsuccessfully.
					Toast.makeText(MainActivity.this, R.string.share_facebook_unsuccessfully, Toast.LENGTH_SHORT)
							.show();
				}

				@Override
				public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
					// Share successfully.
					Toast.makeText(MainActivity.this, R.string.share_facebook_successfully, Toast.LENGTH_SHORT).show();
				}
			});
		} else { // Login facebook.
			Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		}
	}

}
