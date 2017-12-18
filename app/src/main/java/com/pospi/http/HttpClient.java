package com.pospi.http;

import android.os.Handler;
import android.os.Looper;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.pospi.util.App;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.xiaopan.android.net.NetworkUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/3/1.
 */
public class HttpClient {
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static Handler handler = new Handler(Looper.getMainLooper());
    private static final String VALUE = "Value";
    private static final String MESSAGE = "Message";
    private static final String RESULT = "Result";
    private static final String DATA = "data";
    private static final String STATUS_CODE = "status_code";
    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .addInterceptor(new LoggingInterceptor())
            .addNetworkInterceptor(new StethoInterceptor())
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .build();

    /**
     * 上传多张或者单张图片
     *
     * @param tag
     * @param url
     * @param key
     * @param map
     * @param paths
     * @param callBack
     */
    public static void postImage(Object tag, String url, String key, HashMap<String, String> map, List<String> paths, final CallBack callBack) {

        if (!NetworkUtils.isConnectedByState(App.getContext())) {
            callBack.onFailure(5, "网络开小差了！！");
            return;
        }
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (String path : paths) {
            if (!"add".equals(path))
                builder.addFormDataPart(key, path, RequestBody.create(MEDIA_TYPE_PNG, new File(path)));
        }
        for (String s : map.keySet()) {
            builder.addFormDataPart(s, map.get(s));
        }

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .tag(tag)
                .url(url)
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("e.getLocalizedMessage() = " + e.getLocalizedMessage());
                        callBack.onFailure(4, e.getLocalizedMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                json = json.replace("null", "\"\"");
                final String finalJson = json;
                System.out.println("finalJson = " + finalJson);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Logger.json(finalJson);
                            JSONObject jsonObject = new JSONObject(finalJson);

                            if (jsonObject.isNull(RESULT)) {
                                callBack.onFailure(0, "rescode key not exists!");
                                return;
                            }
                            if (jsonObject.isNull(MESSAGE)) {
                                callBack.onFailure(0, "message key not exists!");
                                return;
                            }

                            if (jsonObject.getString(RESULT).equals("0")) {
                                callBack.onFailure(0, jsonObject.getString(MESSAGE));
                                return;
                            }
                            if (jsonObject.getString(RESULT).equals("3")) {
                                callBack.onFailure(3, jsonObject.getString(MESSAGE));
                                return;
                            }
                            if (jsonObject.getString(RESULT).equals("4")) {
                                callBack.onFailure(4, jsonObject.getString(MESSAGE));
                                return;
                            }
                            if (jsonObject.getString(RESULT).equals("2")) {
                                callBack.onFailure(2, jsonObject.getString(MESSAGE));
//                                App.perfectExit();
//                                App.getContext().startActivity(new Intent(App.getContext(), LoginActivity.class));
                                return;
                            }
                            if (jsonObject.getString(RESULT).equals("1")) {
                                if (jsonObject.isNull(RESULT)) {
                                    callBack.onSuccess(jsonObject.getString(MESSAGE));
                                } else {
                                    String result = jsonObject.getString(VALUE);
                                    callBack.onSuccess(new Gson().fromJson(result, callBack.type));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            callBack.onFailure(4, e.getLocalizedMessage());
                        }
                    }
                });
            }
        });
    }

    /**
     * post请求
     *
     * @param tag
     * @param url
     * @param map
     * @param callBack
     */
    public static void post(Object tag, String url, HashMap<String, String> map, final CallBack callBack) {

        if (!NetworkUtils.isConnectedByState(App.getContext())) {
            callBack.onFailure(5, "网络开小差了！！");
            return;
        }
        FormBody.Builder builder = new FormBody.Builder();
        if (map != null) {
            for (String key : map.keySet()) {
                builder.add(key, map.get(key));
            }
        }
        Logger.json(new Gson().toJson(map));
        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .tag(tag)
                .url(url)
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onFailure(4, e.getLocalizedMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                json = json.replace("null", "\"\"");
                System.out.println("json = " + json);
                final String finalJson = json;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            Logger.json(finalJson);
                            JSONObject jsonObject = new JSONObject(finalJson);

                            if (jsonObject.isNull(RESULT)) {
                                if (jsonObject.getString(STATUS_CODE).equals("500")) {
                                    callBack.onFailure(500, jsonObject.getString("请求信息失败！"));
                                }
                                return;
                            }
                            if (jsonObject.isNull(MESSAGE)) {
                                callBack.onFailure(0, "message key not exists!");
                                return;
                            }

                            if (jsonObject.getString(RESULT).equals("0")) {
                                callBack.onFailure(0, jsonObject.getString(MESSAGE));
                                return;
                            }
                            if (jsonObject.getString(RESULT).equals("3")) {
                                callBack.onFailure(3, jsonObject.getString(MESSAGE));
                                return;
                            }
                            if (jsonObject.getString(RESULT).equals("4")) {
                                callBack.onFailure(3, jsonObject.getString(MESSAGE));
                                return;
                            }
                            if (jsonObject.getString(RESULT).equals("2")) {
                                callBack.onFailure(2, jsonObject.getString(MESSAGE));
//                                App.perfectExit();
//                                App.getContext().startActivity(new Intent(App.getContext(), LoginActivity.class));
                                return;
                            }
                            if (jsonObject.getString(RESULT).equals("1")) {
                                if (jsonObject.isNull(VALUE)) {
                                    callBack.onSuccess(jsonObject.getString(MESSAGE));
                                } else {
                                    String result = jsonObject.getString(VALUE);
                                    callBack.onSuccess(new Gson().fromJson(result, callBack.type));
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            callBack.onFailure(4, e.getLocalizedMessage());
                        }

                    }
                });

            }
        });
    }

    public static void get(Object tag, final String url, final CallBack callBack) {

        if (!NetworkUtils.isConnectedByState(App.getContext())) {
            callBack.onFailure(5, "网络开小差了！！");
            return;
        }

        Request request = new Request.Builder()
                .tag(tag)
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onFailure(4, e.getLocalizedMessage());
                    }
                });
            }

            @Override
            public void onResponse(final Call call, Response response) throws IOException {
                String json = response.body().string();
                Logger.json(json);
                json = json.replace("null", "\"\"");
                final String finalJson = json;
                System.out.println("json = " + json);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            callBack.onSuccess(new Gson().fromJson(finalJson, callBack.type));
                        } catch (Exception e) {

                            callBack.onFailure(4, e.getLocalizedMessage());
                        }
                    }
                });

            }
        });

    }

    /**
     * 根据tag取消请求
     *
     * @param tag 标签
     */
    public static void cancelRequest(Object tag) {
        for (Call call : okHttpClient.dispatcher().queuedCalls()) {
            if (call.request().tag().equals(tag)) {
                call.cancel();
            }
        }
        for (Call call : okHttpClient.dispatcher().runningCalls()) {
            if (call.request().tag().equals(tag)) {
                call.cancel();
            }
        }
    }


    /**
     * 请求响应日志信息，方便debug
     */
    public static class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.nanoTime();
            Logger.i(String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));

            Response response = chain.proceed(request);

            long t2 = System.nanoTime();
            Logger.i(String.format("Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));
            return response;
        }
    }
}
