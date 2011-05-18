package org.example.mapviewsample;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
    	implements OnClickListener
    {
    	//円の半径
    	private static final int CIRCLE_RADIUS = 12;

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

    		//リソースIDからボタンを取得し、
    		Button button = (Button)findViewById(R.id.Button01);
    		//クリック時の処理を行うリスナーとして自身を登録
    		button.setOnClickListener(this);
    	}

    	/**
    	 * ボタンが押されたときに呼び出される
    	 */
    	public void onClick(View v)
    	{
    		switch(v.getId())
    		{
    		//押されたボタンがButton01なら
    		case R.id.Button01:

    			//EditTextのインスタンスを取得
    			EditText editText = (EditText)findViewById(R.id.EditText01);

    			//EditTextの現在の文字列を取得
    			String text = editText.getText().toString();

    			try
    			{
    				//文字列から住所情報を取得
    				List<Address> addressList = mGeocoder.getFromLocationName(text, 1);
    				if(addressList.size() > 0)
    				{
    					Address address =addressList.get(0);

    					//Addressから緯度経度情報を取得しタップ位置にセット
    					setTapPoint(new GeoPoint(
    							(int)(address.getLatitude()*1E6),
    							(int)(address.getLongitude()*1E6)
    					));

    					//MapViewのインスタンスを取得
    					MapView mapView = (MapView)findViewById(R.id.MapView01);

    					//検索結果の位置を画面の中央に
    					mapView.getController().setCenter(mGeoPoint);
    					//MapViewの拡大率を変更する
    					mapView.getController().setZoom(15);
    				}
    			}
    			catch(Exception e){}
    			break;
    		default:
    			break;
    		}
    	}

    	/**
    	 * MapViewをタップした際に呼び出されるメソッド
    	 * @param point タップされた位置の緯度経度情報
    	 * @param mapView このクラスを保持するMapViewへの参照
    	 */
    	public boolean onTap(GeoPoint point, MapView mapView)
    	{
    		//タップ位置をセットする
    		setTapPoint(point);

    		//スーパークラスのonTapを呼び出す
    		return super.onTap(point, mapView);
    	}

    	/**
    	 * タップした位置をセットするメソッド
    	 * @param point タップされた位置の経度緯度情報
    	 */
    	private void setTapPoint(GeoPoint point)
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
						textView.setText(country + " : " + admin + locality);
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
    	
    	/**
    	 * WeatherAPI(http://www.worldweatheronline.com/)を利用して天気情報を取得しビューに反映
    	 * @param point 緯度経度情報
    	 */
    	private void getWeatherXML(GeoPoint point)
    	{
    		byte[] xml_byte = null;
    		
    		try
    		{
    			//10進数のFormatオブジェクト
    			DecimalFormat df = new DecimalFormat();
    			
    			//数値だけを出力するよう指定
    			df.applyPattern("0");
    			//小数第2位まで出力するよう指定
    			df.setMinimumFractionDigits(2);
    			df.setMaximumFractionDigits(2);
    			
    			//緯度と経度を文字列に変換
    			String latitude_str =df.format(point.getLatitudeE6()/1E6);
    			String longitude_str =df.format(point.getLongitudeE6()/1E6);
    			
    			//WeatherAPIのURL
    			String api_key = "10afa2b383153522111705";
    			String weather_url = "http://www.worldweatheronline.com/feed/weather.ashx?q=" + latitude_str + "," + longitude_str + "&format=xml&num_of_days=1&key=" + api_key;
    			
    			//WeatherAPIから天気情報XMLを取得
    			xml_byte = getHttp(weather_url);
    		}
    		catch(Exception e){}
    		
    		if(xml_byte == null) return;
    		//XMLをパースする
    		parseXml(xml_byte);
    	}
    	
    	/**
    	 * HTTP GETでデータを取得し、byte列として返す
    	 * @param url_str URL文字列
    	 * @return 取得したデータ。取得に失敗したらnullが返される
    	 */
    	private byte[] getHttp(String url_str)
    	{
    		//HTTP接続
    		HttpURLConnection connect = null;
    		
    		//入力ストリーム
    		InputStream istream = null;
    		
    		//byte型配列出力ストリーム
    		ByteArrayOutputStream ostream = null;
    		
    		//結果として返すbyte型配列
    		byte[] result = null;
    		
    		try
    		{
    			//URLオブジェクトを生成
    			URL url = new URL(url_str);
    			
    			//HTTP接続用オブジェクトを生成
    			connect = (HttpURLConnection)url.openConnection();
    			
    			//HTTPリクエストをGETにセット
    			connect.setRequestMethod("GET");
    			
    			//入力ストリームオブジェクトを取得
    			istream = connect.getInputStream();
    			
    			//出力ストリームオブジェクトを取得
    			ostream = new ByteArrayOutputStream();
    			
    			byte[] buf = new byte[1024];
    			while(true)
    			{
    				//入力ストリームからデータを取得
    				int size = istream.read(buf);
    				
    				//取得できなくなったらループ終了
    				if(size <= 0) break;
    				
    				//取得したデータを出力ストリームに書き込む
    				ostream.write(buf, 0, size);
    			}
    			//byte型の配列としてデータを取り出す
    			result = ostream.toByteArray();
    		}
    		catch(Exception e){}

    		finally
    		{
    			//HTTP接続を切断
    			if(connect != null) connect.disconnect();
    			
    			try
    			{
    				//入力ストリームを閉じる
    				if(istream != null) istream.close();
    				//出力ストリームを閉じる
    				if(ostream != null) ostream.close();
    			}
    			catch(Exception e){}
    		}
    		return result;
    	}
    	
    	/**
    	 * WeatherAPIから取得した天気情報XMLを解析しビューに反映
    	 * @param xml_byte 天気情報XMLのバイト列
    	 */
    	private void parseXml(byte[] xml_byte)
    	{
    		
    	}
    }
}