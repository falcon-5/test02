package org.example.mapviewsample;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

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

        //プライベートクラスとして以降で定義したOverlayの具象クラスを生成
        ConcreteOverlay overlay = new ConcreteOverlay(this);

        //生成したOverlayを追加する
        List<Overlay> overlayList = mapView.getOverlays();
        overlayList.add(overlay);
    }

    /**
     * ルート情報を表示するかどうか
     */
    protected boolean isRouteDisplayed(){
    	return false;
    }

    /**
     * 地図上に表示されるオーバーレイのクラス
     */
    private class ConcreteOverlay extends Overlay
    {
    	//円の半径
    	private static final int CIRCLE_RADIUS = 16;

    	//タップされた位置の緯度経度情報を保持するメンバ
    	GeoPoint mGeoPoint;

    	//円を描画するための色情報
    	Paint mCirclePaint;

    	//緯度経度情報と住所情報を相互変換する
    	Geocoder mGeocoder;

    	/**
    	 * コンストラクタ
    	 */
    	ConcreteOverlay(Context context)
    	{
    		mGeoPoint = null;
    		mCirclePaint = new Paint();
    		//図形を塗りつぶすことを指定
    		mCirclePaint.setStyle(Paint.Style.FILL);
    		//図形を不透明の赤色に指定
    		mCirclePaint.setARGB(255, 255, 0, 0);

    		//Geocoderを日本語環境でセット
    		mGeocoder = new Geocoder(context, Locale.JAPAN);
    	}

    	/**
    	 * MapViewをタップした際に呼び出されるメソッド
    	 * @param point タップされた位置の緯度経度情報
    	 * @param mapView このクラスを保持するMapViewへの参照
    	 */
    	public boolean onTap(GeoPoint point, MapView mapView)
    	{
    		//タップされた位置の経度緯度情報をメンバにセット
    		mGeoPoint = point;

    		try
    		{
    			//画面上のTextViewのインスタンスを取得
    			TextView textView = (TextView)findViewById(R.id.TextView01);

    			//市町村名まで取得できたかどうか
    			boolean success = false;

    			//緯度経度から住所を取得
    			List<Address> addressList = mGeocoder.getFromLocation(point.getLatitudeE6()/1E6, point.getLongitudeE6()/1E6, 5);

    			//検索結果を順に処理
    			for(Iterator<Address> it = addressList.iterator(); it.hasNext();)
    			{
    				Address address = it.next();

    				//国名を取得
    				String country = address.getCountryName();
    				//都道府県
    				String admin = address.getAdminArea();
    				//市区町村
    				String locality = address.getLocality();

    				//市区町村名まで取得できていればTextViewを更新
    				if(country != null && admin != null && locality != null)
    				{
    					textView.setText(country + admin + locality);
    					success = true;
    					break;
    				}
    			}

    			//取得に失敗していればTextViewをエラー表記に変更
    			if(!success) textView.setText("Error");

    			//TextViewの再描画を行う
    			textView.invalidate();
    		}
    		catch(Exception e){}

    		//スーパークラスのonTapを呼び出す
    		return super.onTap(point, mapView);
    	}

    	/**
    	 * このOverlayを保持するMapViewの描画時に呼び出される
    	 * @param canvas
    	 * @param mapView このクラスを保持するMapViewへの参照
    	 * @param shadow 影の描画のためにdrawが呼び出されたかどうか
    	 *
    	 * ※drawメソッドは、通常の描画と影の描画のために2回呼び出される。
    	 *   shadowはそれぞれ、通常の描画ならfalse、影の描画ならtrueとなる。
    	 */
    	public void draw(Canvas canvas, MapView mapView, boolean shadow)
    	{
    		//スーパークラスのdrawメソッドを呼び出す
    		super.draw(canvas, mapView, shadow);

    		//今回は影描写は使わないので、影のない場合のみ処理
    		if(!shadow)
    		{
    			//タップした経度緯度情報が存在する場合のみ処理
    			if(mGeoPoint != null)
    			{
    				//地図上の経度緯度から、Canvasの座標系へと変換する
    				Projection projection = mapView.getProjection();
    				Point point = new Point();
    				projection.toPixels(mGeoPoint, point);

    				//円を描画する
    				canvas.drawCircle(point.x, point.y, CIRCLE_RADIUS, mCirclePaint);
    			}
    		}
    	}
    }
}