package com.dogmaticcentral.bumble;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ResourceDisplayActivity extends AppCompatActivity {
	private View mContentView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resource_display);
		Log.v("blah", "Flavor: local  -  in resource display");
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
			if (imgId==0) continue;
			imgPlaceholder[imgIndex].setImageResource(imgId);
		}

	}
}

