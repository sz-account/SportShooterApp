package com.example.dsp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.dsp.trainingData.AppDatabase;
import com.example.dsp.trainingData.hit.Hit;
import com.example.dsp.trainingData.Training;
import com.example.dsp.trainingData.TrainingWithHits;
import com.example.dsp.trainingData.enums.TargetSize;
import com.example.dsp.trainingData.enums.TrainingModeEnum;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Synch {

    private Context context;
    private AppDatabase instance;

    public Synch(Context context) {

        this.context = context;
        this.instance = AppDatabase.getInstance(context);

        sendAllNew();
    }

    private void sendAllNew()
    {
        OkHttpClient client = new OkHttpClient();

        // Assuming the server endpoint is "http://localhost:8080/trainings"
        String url = "http://192.168.1.16:8080/training";

        //Gson gson = new Gson();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();

        List<TrainingWithHits> allTrainings = instance.trainingWithHitsDao().getNew(MainActivity.account.getId());

        if (allTrainings.size() != 0) {

            List<TrainingRequest> requestList = new LinkedList<>();

            for (TrainingWithHits v : allTrainings) {
                TrainingRequest request = new TrainingRequest();
                request.date = v.training.date;
                request.distance = v.training.distance;
                request.gameMode = v.training.trainingMode.ordinal();
                request.hits = v.hits;
                request.targets = v.training.targets;
                request.targetSize = v.training.targetSize.ordinal();

                requestList.add(request);
            }

            String json = gson.toJson(requestList);

            // Set the media type as "application/json"
            MediaType mediaType = MediaType.parse("application/json");

            // Create the request body with the JSON string
            RequestBody requestBody = RequestBody.create(mediaType, json);

            // Create a POST request with the URL and request body
            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", String.valueOf("Bearer " + MainActivity.account.getIdToken()))
                    .method("POST", requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    asyncToast("Connection error");
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    asyncToast("OK");
                    deleteAndFetch();
                }
            });
        }
        else
        {
            deleteAndFetch();
        }
    }

    private void deleteAndFetch()
    {
        instance.trainingWithHitsDao().deleteRowsWithNullField(MainActivity.account.getId());

        Training training = instance.trainingDao().getEntityWithBiggerTimestamp(MainActivity.account.getId());
        long timeStamp = 0;

        if (training != null && training.timeStamp != null)
            timeStamp = training.timeStamp;

        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.1.16:8080/training/ts";

        RequestBody formBody = new FormBody.Builder()
                .add("time", String.valueOf(timeStamp))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", String.valueOf("Bearer " + MainActivity.account.getIdToken()))
                .method("POST", formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                String string = response.body().string();

                if (response.isSuccessful()) {

                    try {
                        JSONArray jsonArray = new JSONArray(string);

                        if (jsonArray.length() > 0)
                            addToDataBase(jsonArray);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    private void addToDataBase(JSONArray jsonArray) throws JSONException {

        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject jsonObject = jsonArray.getJSONObject(i);

            Training training = new Training();
            //training.id = jsonObject.getLong("id");
            training.targets = jsonObject.getInt("targets");
            training.targetSize = TargetSize.values()[jsonObject.getInt("targetSize")];
            training.fk_userId = instance.userDao().getIdOfUser(MainActivity.account.getId());

            try {
                training.date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(jsonObject.getString("date"));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            training.trainingMode = TrainingModeEnum.values()[jsonObject.getInt("gameMode")];
            training.timeStamp = jsonObject.getLong("timeStamp");
            training.distance = jsonObject.getInt("distance");

            long trainingId = instance.trainingDao().insertTraining(training);

            JSONArray jsonArray2 = jsonObject.getJSONArray("hits");

            for (int j = 0; j < jsonArray2.length(); j++) {

                JSONObject jsonObject2 = jsonArray2.getJSONObject(j);

                Hit hit = new Hit();
                hit.id = jsonObject2.getLong("id");
                hit.trainingId = trainingId;
                hit.time = jsonObject2.getLong("time");

                instance.hitsDao().insertHit(hit);
            }
        }

    }

    private void asyncToast(String message)
    {
        // Inside your background thread or Runnable
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
