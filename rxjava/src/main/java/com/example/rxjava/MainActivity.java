package com.example.rxjava;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;


public class MainActivity extends Activity {
    private static final String TAG = "Rx_Activity";
    private TextView tv_response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_response = findViewById(R.id.tv_response);
        Observer observer = new Observer() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: " + d.toString());

            }

            @Override
            public void onNext(Object o) {
                Log.d(TAG, "onNext: " + o);

            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: ");

            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");

            }
        };
        Subscriber subscriber = new Subscriber() {
            @Override
            public void onSubscribe(Subscription s) {

            }

            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        };

        Observable observable = Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter emitter) throws Exception {
                emitter.onNext("1");
                emitter.onNext("2");
                emitter.onNext("3");
                emitter.onComplete();
            }
        });
        observable
                .subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread())
                .map(new Function() {
                    @Override
                    public Object apply(Object o) throws Exception {
                        int i = Integer.parseInt((String) o);
                        return i + 1;
                    }
                })
                .subscribe(observer);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.weather.com.cn/")
                .build();
        ApiStores apiStores = retrofit.create(ApiStores.class);
        Call<ResponseBody> call = apiStores.getWeather("101010100");
//        Response<ResponseBody> bodyResponse = null;
//        try {
//            bodyResponse = call.execute();
//            String body = bodyResponse.body().string();//获取返回体的字符串
//            Log.i("wxl", "body=" + body);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                Log.d(TAG, "onResponse: " + response.toString());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: ");
            }

        });
        findViewById(R.id.btn_retrofit_and_rxjava).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Retrofit retrofit1 = new Retrofit.Builder()
                        .baseUrl("http://www.weather.com.cn/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();
                ApiStores apiStores1 = retrofit1.create(ApiStores.class);
                Observable<WeatherJson> weatherRxjava = apiStores1.getWeatherRxjava("101010100");
                weatherRxjava.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<WeatherJson>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                Log.d(TAG, "onSubscribe: ");
                            }

                            @Override
                            public void onNext(WeatherJson weatherJson) {

                                tv_response.setText(weatherJson.getWeatherinfo().toString());

                                Log.d(TAG, "onNext: " + weatherJson.getWeatherinfo().toString());

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG, "onError: ");

                            }

                            @Override
                            public void onComplete() {
                                Log.d(TAG, "onComplete: ");

                            }
                        });
            }
        });


        findViewById(R.id.btn_retrofit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a = null;
                Retrofit retrofit2 = new Retrofit.Builder()
                        .baseUrl("http://www.baidu.com/")
                        .build();
                ApiStores apiStores2 = retrofit2.create(ApiStores.class);
                Call<ResponseBody> baidu = apiStores2.getBaidu();
                baidu.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            tv_response.setText(response.body().string());
                            Log.d(TAG, "onResponse: " + response.body().toString()
                                    + " message" + response.message() + " " + response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d(TAG, "onFailure: ");

                    }
                });
            }
        });



    }

    /**
     * Call<T> get();必须是这种形式,这是2.0之后的新形式
     * 如果不需要转换成Json数据,可以用了ResponseBody;
     * 你也可以使用Call<GsonBean> get();这样的话,需要添加Gson转换器
     * https://www.baidu.com/s?wd=斗鱼
     */
    public interface ApiStores {
        @GET("adat/sk/{cityId}.html")
        Call<ResponseBody> getWeather(@Path("cityId") String cityId);

        @GET("adat/sk/{cityId}.html")
        Observable<WeatherJson> getWeatherRxjava(@Path("cityId") String cityId);

        @GET(" ")
        Call<ResponseBody> getBaidu();

    }


}
