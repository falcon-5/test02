package org.example.mapviewsample;

import com.google.android.maps.MapActivity;
import android.os.Bundle;

public class MapViewSample extends MapActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    /**
     * ルート情報を表示するかどうか
     */
    protected boolean isRouteDisplayed(){
    	return false;
    }
}