package kr.co.pionmanager.www;

import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

import kr.co.pionmanager.www.Registration.RegisterPage_Bank_Account;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FileUploadUtils extends AppCompatActivity {

    private static final String TAG = "FileUploadUtils";
    public static void send2Server(File file) {
        Log.e(TAG, "send2Server: " + file.getName() );
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("files", file.getName(), RequestBody.create(MultipartBody.FORM, file)).build();
        Request request = new Request.Builder().url(Util.thepion_URL + "AjaxControl/IDCardFileUpload?userNum=" + UserInfo.getUserNum())
                .post(requestBody).build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e(TAG, "onFailure: " + e );
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Log.e("TEST : ", response.body().string());
                Intent intent = new Intent(Util.CURRENT_CONTEXT, RegisterPage_Bank_Account.class);
                Util.CURRENT_CONTEXT.startActivity(intent);

            }
        });
    }
}


class IDCardReturnData{
    int isUpload = 0;
    String dir = "";

    public IDCardReturnData(int isUpload, String dir) {
        this.isUpload = isUpload;
        this.dir = dir;
    }

    public int getIsUpload() {
        return isUpload;
    }

    public void setIsUpload(int isUpload) {
        this.isUpload = isUpload;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }
}

