package com.teamphoenix.greenfilemanager.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

public class FileOpener {
    public static void openFile(Context context, File file) throws IOException {
        File selectedFile = file;
        Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", selectedFile);

        Intent intent = new Intent(Intent.ACTION_VIEW);

        if (fileUri.toString().contains(".doc")) {
            intent.setDataAndType(fileUri, "application/msword");
        } else if (fileUri.toString().contains(".pdf")) {
            intent.setDataAndType(fileUri, "application/pdf");
        } else if (fileUri.toString().contains(".mp3") || fileUri.toString().contains(".wav")) {
            intent.setDataAndType(fileUri, "audio/x-wav");
        } else if (fileUri.toString().contains(".jpeg") || fileUri.toString().contains(".jpg") || fileUri.toString().contains(".png")) {
            intent.setDataAndType(fileUri, "image/jpeg");
        } else if (fileUri.toString().contains(".mp4")) {
            intent.setDataAndType(fileUri, "video/*");
        } else {
            intent.setDataAndType(fileUri, "*/*");
        }

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }
}
