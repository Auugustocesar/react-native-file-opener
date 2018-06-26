package com.fileopener;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import org.json.JSONException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileOpener extends ReactContextBaseJavaModule {

    public FileOpener(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "FileOpener";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        return constants;
    }

    @ReactMethod
    public void open(String fileArg, String contentType, Promise promise) throws JSONException {
        File file = new File(fileArg);

        if (file.exists()) {
            try {
                Uri path = FileProvider.getUriForFile(getCurrentActivity(), this.getReactApplicationContext().getPackageName() + ".provider", new File(fileArg));
                if (Build.VERSION.SDK_INT >= 24) {
                    Intent intent = new Intent(Intent.ACTION_VIEW).setDataAndType(path, contentType);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    PackageManager pm = getCurrentActivity().getPackageManager();
                    if (intent.resolveActivity(pm) != null) {
                        this.getReactApplicationContext().startActivity(intent);
                    }

                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse("file://" + fileArg), contentType).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    this.getReactApplicationContext().startActivity(intent);

                }

                promise.resolve("Open success!!");
            } catch (android.content.ActivityNotFoundException e) {
                promise.reject("Open error!!");
            }
        } else {
            promise.reject("File not found");
        }
    }

}
