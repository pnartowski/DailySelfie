package com.example.DailySelfie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DailySelfie {

    public static final String SELFIE = "SELFIE_";
    public static final String EXTENSION = ".jpg";
    private static final int THUMBNAIL_HEIGHT = 50;
    private static final int THUMBNAIL_WIDTH = 100;
    private final String filePath;
    private final String label;
    private boolean checked;

    private final Bitmap thumbnail;
    public static final String DATE_PARSER_FORMAT = "yyyyMMdd_HHmmss";
    private final static SimpleDateFormat DATE_PARSER = new SimpleDateFormat(DATE_PARSER_FORMAT);
    private final static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private final Context context;

    public DailySelfie(Context context, String filePath) {
        this.context = context;
        this.filePath = filePath;
        this.label = parseLabel(filePath);
        this.thumbnail = loadThumbnail(filePath);
    }

    private String parseLabel(String fileName) {
        try {
            String[] path = fileName.split("/");
            String timeDatePart = path[path.length - 1].substring(SELFIE.length(), SELFIE.length() + DATE_PARSER_FORMAT.length());

            Date dateTimeFromFileName = DATE_PARSER.parse(timeDatePart);
            java.text.DateFormat dateFormat = DateFormat.getDateFormat(context);

            return new StringBuilder(dateFormat.format(dateTimeFromFileName))
                    .append(" ")
                    .append(TIME_FORMAT.format(dateTimeFromFileName))
                    .toString();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    private Bitmap loadThumbnail(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;

        options.inJustDecodeBounds = false;
        options.inSampleSize = Math.min(imageHeight / THUMBNAIL_HEIGHT, imageWidth / THUMBNAIL_WIDTH);
        options.inPurgeable = true;

        return BitmapFactory.decodeFile(filePath, options);
    }

    public String getFilePath() {
        return filePath;
    }

    public String getLabel() {
        return label;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public static String createImageFileName() {
        return new StringBuilder().append(SELFIE)
                .append(DATE_PARSER.format(new Date()))
                .append(EXTENSION)
                .toString();
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
