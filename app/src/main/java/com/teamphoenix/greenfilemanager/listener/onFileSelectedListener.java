package com.teamphoenix.greenfilemanager.listener;

import java.io.File;

public interface onFileSelectedListener {
    void onFileClicked(File file);
    void onFileLongClicked(File file, int position);
}
