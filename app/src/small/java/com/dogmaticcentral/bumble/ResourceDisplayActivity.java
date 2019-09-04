package com.dogmaticcentral.bumble;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.play.core.splitinstall.SplitInstallException;
import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory;
import com.google.android.play.core.splitinstall.SplitInstallRequest;
import com.google.android.play.core.splitinstall.SplitInstallSessionState;
import com.google.android.play.core.splitinstall.model.SplitInstallErrorCode;
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;

import java.util.List;

public class ResourceDisplayActivity extends AppCompatActivity {
	private View mContentView;
	SplitInstallManager splitInstallManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.v("blah", "Flavor: small  -  in resource display");
		mContentView = findViewById(R.id.outmost_container);
		mContentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				//Remove it here unless you want to get this callback for EVERY
				//layout pass, which can get you into infinite loops if you ever
				//modify the layout from within this method.
				mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				//Now you can get the width and height from content
				// Perhaps something to do with constraints,
				// but the layout is inflated only when there is an image to put there
				display();
			}
		});

	}


	private void display() {
		Resources resources = this.getResources();
		String pname = getPackageName();
		String [] imgNames = {"a","b","c1", "c2"};
		ImageView[] imgPlaceholder = {
			   this.findViewById(R.id.imgA),
			   this.findViewById(R.id.imgB),
			   this.findViewById(R.id.imgC1),
			   this.findViewById(R.id.imgC2)
		};
		for (int imgIndex=0; imgIndex<imgNames.length;  imgIndex++) {
			String imgName = imgNames[imgIndex];
			int imgId= resources.getIdentifier(imgName, "drawable", pname);
			Log.v("blah", String.format("image %s, id = %d ", imgName, imgId));
			if (imgId==0) downloadFeature(imgName);
			imgPlaceholder[imgIndex].setImageResource(imgId);
		}

	}
	//https://developer.android.com/guide/app-bundle/playcore#java
	//When your app requests an on demand module, the Play Core Library employs a
	// “fire-and-forget” strategy. That is, it sends the request to download the module to the platform,
	// but it does not monitor whether the installation succeeded.
	// To move the user journey forward after installation or gracefully handle errors,
	// make sure you monitor the request state.
	private void downloadFeature(String imgName) {
		// Creates an instance of SplitInstallManager.
		splitInstallManager = SplitInstallManagerFactory.create(this);
		String moduleName = imgName.equals("b") ? "feat1" : "feat2";
		// Creates a request to install a module.
		SplitInstallRequest request =
			   SplitInstallRequest
					 .newBuilder()
					 // You can download multiple on demand modules per
					 // request by invoking the following method for each
					 // module you want to install.
					 .addModule(moduleName)
					 .build();

		splitInstallManager
			   // Submits the request to install the module through the
			   // asynchronous startInstall() task. Your app needs to be
			   // in the foreground to submit the request.
			   .startInstall(request)
			   // You should also be able to gracefully handle
			   // request state changes and errors. To learn more, go to
			   // the section about how to Monitor the request state.
			   .addOnSuccessListener(new OnSuccessListener<Integer>() {
				   @Override
				   public void onSuccess(Integer sessionId) {
				   }
			   })
			   .addOnFailureListener(new OnFailureListener() {
				   @Override
				   public void onFailure(Exception exception) {
					   switch (((SplitInstallException) exception).getErrorCode()) {
						   case SplitInstallErrorCode.NETWORK_ERROR:
							   // Display a message that requests the user to establish a
							   // network connection.
							   break;
						   case SplitInstallErrorCode.ACTIVE_SESSIONS_LIMIT_EXCEEDED:
							   ResourceDisplayActivity.this.checkForActiveDownloads();

					   }
				   }
			   });
	}


	void checkForActiveDownloads() {
		splitInstallManager
			   // Returns a SplitInstallSessionState object for each active session as a List.
			   .getSessionStates()
			   .addOnCompleteListener(new OnCompleteListener<List<SplitInstallSessionState>>() {
				   @Override
				   public void onComplete(Task<List<SplitInstallSessionState>> task) {
					   if (task.isSuccessful()) {
						   // Check for active sessions.
						   for (SplitInstallSessionState state : task.getResult()) {
							   if (state.status() == SplitInstallSessionStatus.DOWNLOADING) {
								   // Cancel the request, or request a deferred installation.
							   }
						   }
					   }
				   }
			   });

	}

}