package com.nextiva.nextivaapp.android.viewmodels;

import android.app.Application;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.nextiva.nextivaapp.android.BuildConfig;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.managers.FormatterManager;
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class LoginPreferencesViewModel extends BaseViewModel {

    private Application nextivaApplication;
    private CalendarManager calendarManager;

    @Inject
    public LoginPreferencesViewModel(@NonNull Application application, CalendarManager calendarManager) {
        super(application);
        this.nextivaApplication = application;
        this.calendarManager = calendarManager;
    }

    public void deleteCurrentZips() {
        try {
            File path2 = new File(getApplication().getExternalFilesDir(null) + "/");
            deleteFilesAt(path2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteFilesAt(File file) {
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            if (listFiles != null) {
                for (File listFile : listFiles) {
                    if(!listFile.getAbsolutePath().contains(".log")) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            Uri contentUri = FileProvider.getUriForFile(nextivaApplication,
                                                                        BuildConfig.APPLICATION_ID + ".provider",
                                                                        listFile);
                            nextivaApplication.getContentResolver().delete(contentUri, null, null);
                        } else {
                            listFile.delete();
                        }
                    }
                }
            }
        }
    }

    public String zipLogs() throws Exception {
        Application application = getApplication();
        FormatterManager formatterManager = FormatterManager.getInstance();
        String filename = String.format(formatterManager.getDateFormatter_logZipFilename(application)
                        .format(calendarManager.getNowInstant()).replace(":", "") + ".zip",
                ((Application) application).getString(R.string.app_name)).replace(" ", "");

        String absoluteFilePath = getApplication().getExternalFilesDir(null) + "/" + filename;

        FileOutputStream fileOutputStream = new FileOutputStream(absoluteFilePath);
        ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
        addDirectoryToZipArchive(zipOutputStream, absoluteFilePath, new File(getApplication().getExternalFilesDir(null) + "/"), null);

        zipOutputStream.flush();
        fileOutputStream.flush();
        zipOutputStream.close();
        fileOutputStream.close();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            absoluteFilePath = moveFile(absoluteFilePath, filename) != null ? absoluteFilePath : moveFile(absoluteFilePath, filename);
        }

        return absoluteFilePath;
    }

    public String moveFile(String currentPath, String filename) {
        File source = new File(currentPath);

        String destinationPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Documents/Nextiva/" + BuildConfig.FLAVOR + "/";
        File destination = new File(destinationPath);
        destination.mkdirs();
        File file = new File(destinationPath, filename);

        try {
            copyFile(source, file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return file.getAbsolutePath();
    }

    private void copyFile(File source, File destination) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            FileUtils.copy(Files.newInputStream(source.toPath()), Files.newOutputStream(destination.toPath()));
        }
    }

    public void addDirectoryToZipArchive(ZipOutputStream zipOutputStream, String filename, File fileToZip, String parentDirectoryName) throws Exception {
        if (fileToZip != null && fileToZip.exists()) {
            String zipEntryName = fileToZip.getName();

            if (parentDirectoryName != null && !parentDirectoryName.isEmpty()) {
                zipEntryName = parentDirectoryName + "/" + fileToZip.getName();
            }

            if (fileToZip.isDirectory()) {
                ArrayList<File> zipsList = new ArrayList<>();

                for (File listFile : fileToZip.listFiles()) {
                    if (listFile.getName().contains(".log")) {
                        addDirectoryToZipArchive(zipOutputStream, listFile.getName(), listFile, zipEntryName);

                    } else if (listFile.getName().contains(".zip") && !listFile.getPath().equals(filename)) {
                        zipsList.add(listFile);
                    }
                }

            } else {
                byte[] buffer = new byte[1024];
                FileInputStream fileInputStream = new FileInputStream(fileToZip);
                zipOutputStream.putNextEntry(new ZipEntry(zipEntryName));
                int length = fileInputStream.read(buffer);

                while (length > 0) {
                    zipOutputStream.write(buffer, 0, length);
                    length = fileInputStream.read(buffer);
                }

                zipOutputStream.closeEntry();
                fileInputStream.close();
            }
        }
    }
}
