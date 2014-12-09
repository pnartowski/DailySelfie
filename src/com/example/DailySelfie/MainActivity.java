package com.example.DailySelfie;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends ListActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String APP_NAME = "DailySelfie";
    public static final String REMOVED_FILES_MESSAGE_FORMAT = "Removed %d files";
    private File imageFile;
    private RowAdapter adapter;
    private int periodValue;
    private TimeUnit periodUnit;
    private int notifyPeriod;
    private boolean notificationEnabled;
    private MenuItem enableDisableLabel;
    private DailySelfiePreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = new DailySelfiePreferences(this);

        adapter = new RowAdapter(this, R.layout.row, new ArrayList<DailySelfie>());
        setListAdapter(adapter);
        loadImages();

        periodValue = preferences.getNotificationPeriodValue();
        periodUnit = preferences.getNotificationPeriodUnit();

        notifyPeriod = preferences.evaluateNotificationPeriod(periodValue, periodUnit);
        notificationEnabled = preferences.isNotificationEnabled();
    }

    private void loadImages() {
        for (File file : listSelfiesFromDirectory(new File(getSelfieDirectory()))) {
            new LoadImageTask().execute(file.getAbsolutePath());
        }
    }

    private File[] listSelfiesFromDirectory(File directory) {
        File[] files = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().startsWith(DailySelfie.SELFIE) && pathname.getName().endsWith(DailySelfie.EXTENSION);
            }
        });
        Arrays.sort(files);
        return files;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent viewImage = new Intent(Intent.ACTION_VIEW);
        viewImage.setDataAndType(Uri.fromFile(new File(adapter.getItem(position).getFilePath())), "image/*");
        startActivity(viewImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        enableDisableLabel = menu.findItem(R.id.enable_disable);
        setEnableDisableMenuLabel();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.take_picture:
                dispatchToTakePhoto();
                return true;
            case R.id.edit_period:
                editNotificationPeriod();
                return true;
            case R.id.enable_disable:
                switchNotification();
                return true;
            case R.id.delete:
                delete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void delete() {
        if (adapter.isCheckbox()) {
            List<DailySelfie> delete = new ArrayList<>();
            for (int idx = 0; idx < adapter.getCount(); idx++) {
                DailySelfie item = adapter.getItem(idx);
                if (item.isChecked()) {
                    delete.add(item);
                }
            }
            if (!delete.isEmpty()) {
                new DeleteFilesTask(delete).execute();
            }
        }
        adapter.switchCheckBoxes();
        adapter.notifyDataSetChanged();
    }

    private void switchNotification() {
        notificationEnabled = !notificationEnabled;
        setEnableDisableMenuLabel();
        preferences.saveNotificationEnabled(notificationEnabled);
        manageAlarm();
        manageNotificationsAfterReboot();
    }

    private void manageAlarm() {
        SelfieAlarmManager alarmManager = new SelfieAlarmManager(this);
        if (notificationEnabled) {
            alarmManager.scheduleAlarm(notifyPeriod);
        } else {
            alarmManager.removeAlarm();
        }
    }

    private void manageNotificationsAfterReboot() {
        getPackageManager().setComponentEnabledSetting(
                new ComponentName(this, ScheduleNotificationAfterReboot.class),
                notificationEnabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    private void setEnableDisableMenuLabel() {
        enableDisableLabel.setTitle(notificationEnabled ? R.string.disable_notification : R.string.enable_notification);
    }

    private void editNotificationPeriod() {

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.edit_period, null);

        final TextView value = (TextView) dialogView.findViewById(R.id.period_value);
        final Spinner unit = (Spinner) dialogView.findViewById(R.id.period_unit);

        value.setText(Integer.toString(periodValue));

        String[] units = getResources().getStringArray(R.array.period_units);
        for (int i = 0; i < units.length; i++) {
            if (units[i].equals(periodUnit.getShortcut())) {
                unit.setSelection(i);
                break;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        periodValue = Integer.parseInt(value.getText().toString());
                        periodUnit = TimeUnit.fromString((String) unit.getSelectedItem());

                        preferences.saveNotificationPeriod(periodValue, periodUnit);

                        notifyPeriod = preferences.evaluateNotificationPeriod(periodValue, periodUnit);
                    }
                })
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
            adapter.add(new DailySelfie(this, imageFile.getAbsolutePath()));
            adapter.notifyDataSetChanged();
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    private void dispatchToTakePhoto() {
        try {
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePicture.resolveActivity(getPackageManager()) != null) {
                imageFile = createImageFile();
                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
            }
        } catch (IOException e) {
            Toast.makeText(this, R.string.unable_to_create_file + e.getMessage(), 8).show();
        }
    }

    private File createImageFile() throws IOException {
        String fileName = DailySelfie.createImageFileName();
        File image = new File(getSelfieDirectory(), fileName);
        return image;
    }

    private String getSelfieDirectory() {
        String directory = preferences.getSelfiesDirectory();
        if (directory == null) {
            directory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + APP_NAME;
            preferences.saveDirectory(directory);
            createDirectoryIfNotExist(directory);
        }
        return directory;
    }

    private void createDirectoryIfNotExist(String path) {
        File directory = new File(path);
        if (!directory.isDirectory()) {
            directory.mkdir();
        }
    }

    class LoadImageTask extends AsyncTask<String, Void, Void> {

        private DailySelfie selfie;

        @Override
        protected Void doInBackground(String... params) {
            if (params.length != 1) {
                cancel(true);
            }
            selfie = new DailySelfie(MainActivity.this, params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            adapter.add(selfie);
            adapter.notifyDataSetChanged();
        }
    }

    private class DeleteFilesTask extends AsyncTask<Void, Void, Integer>{

        private int deletedFiles = 0;
        private List<DailySelfie> delete;

        public DeleteFilesTask(List<DailySelfie> delete) {
            this.delete = delete;
        }

        @Override
        protected Integer doInBackground(Void... params) {

            try {
                for (DailySelfie selfie : delete) {
                    if (new File(selfie.getFilePath()).delete()) {
                        deletedFiles++;
                    }
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            Toast.makeText(MainActivity.this, String.format(REMOVED_FILES_MESSAGE_FORMAT, deletedFiles), 5).show();
            for (DailySelfie one:delete) {
                adapter.remove(one);
            }
        }
    }
}
