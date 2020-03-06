package com.talktiva.pilot.rest;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.talktiva.pilot.helper.AppConstant;
import com.talktiva.pilot.helper.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileUploader {

    private static final int DEFAULT_BUFFER_SIZE = 2048;

    private ApiInterface apiInterface;

    private FileUploaderCallback fileUploaderCallback;

    public FileUploader() {
        apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
    }

    public void setOnUploadListener(FileUploaderCallback fileUploaderCallback) {
        this.fileUploaderCallback = fileUploaderCallback;
    }

    public void uploadFile(Context context, Uri uri, File file, String fileKey, Integer id) {
        MyRequestBody requestBody = new MyRequestBody(context, uri, file);
        MultipartBody.Part part = MultipartBody.Part.createFormData(fileKey, file.getName(), requestBody);
        Call<ResponseBody> call = apiInterface.uploadImage(id, part);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                fileUploaderCallback.onResponse(call, response);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                fileUploaderCallback.onFailure(call, t);
            }
        });
    }

    public void uploadAvatar(Context context, Uri uri, File file, String fileKey) {
        MyRequestBody requestBody = new MyRequestBody(context, uri, file);
        MultipartBody.Part part = MultipartBody.Part.createFormData(fileKey, file.getName(), requestBody);
        Call<ResponseBody> call = apiInterface.uploadAvatar(Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_T_TYPE)).concat(" ").concat(Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_A_TOKEN))), part);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                fileUploaderCallback.onResponse(call, response);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                fileUploaderCallback.onFailure(call, t);
            }
        });
    }

    private class MyRequestBody extends RequestBody {

        private Context context;
        private File file;
        private Uri uri;

        MyRequestBody(Context context, Uri uri, File file) {
            super();
            this.context = context;
            this.uri = uri;
            this.file = file;
        }

        @Override
        public long contentLength() {
            return file.length();
        }

        @Nullable
        @Override
        public MediaType contentType() {
            return MediaType.parse(Objects.requireNonNull(context.getContentResolver().getType(uri)));
        }

        @Override
        public void writeTo(@NotNull BufferedSink sink) throws IOException {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            try (FileInputStream in = new FileInputStream(file)) {
                long uploaded = 0;
                int read;
                Handler handler = new Handler(Looper.getMainLooper());
                while ((read = in.read(buffer)) != -1) {
                    handler.post(new ProgressUpdater(uploaded, contentLength()));
                    uploaded += read;
                    sink.write(buffer, 0, read);
                }
            }
        }
    }

    private class ProgressUpdater implements Runnable {
        private long uploaded;
        private long total;

        ProgressUpdater(long uploaded, long total) {
            this.uploaded = uploaded;
            this.total = total;
        }

        @Override
        public void run() {
            fileUploaderCallback.onProgressUpdate((int) (100 * uploaded / total));
        }
    }
}
