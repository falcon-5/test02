package org.example.mapviewsample;

import android.os.Bundle;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class MapViewSample extends MapActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //MapViewインスタンスを取得
        MapView mapView = (MapView)findViewById(R.id.MapView01);

        //MapViewをタッチで操作可能にする
        mapView.setClickable(true);

        //タップするとズームコントローラが表示されるようにする
        mapView.setBuiltInZoomControls(true);
    }

    /**
     * ルート情報を表示するかどうか
     */
    protected boolean isRouteDisplayed(){
    	return false;
    }
}