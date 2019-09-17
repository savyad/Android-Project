package com.cypress.academy.ble101_robot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.LoaderManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

import static com.cypress.academy.ble101_robot.DeviceAdapter.SINGLE_DEV_DATA;

public class DfuActivityMain extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public DevData data;
    public int mfiletype = DfuService.TYPE_AUTO;
    public int mfiletypetmp;
    private boolean mStatusOk;
    private static final int SELECT_FILE_REQ = 1;
    private Integer mscope;

    private String mFilePath;
    private Uri mFileStreamUri;
    private String mInitFilePath;
    private Uri mInitFileStreamUri;
    public Button selFile,uploadFile;
    private ProgressBar uploadprogress;
    private TextView showpercent,dfustat;
    private static final String EXTRA_URI = "uri";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dfu_main);
        final Intent intent = getIntent();
        data = (DevData) intent.getSerializableExtra(SINGLE_DEV_DATA);
        selFile = findViewById(R.id.sel_file);
        uploadFile = findViewById(R.id.upld);
        uploadFile.setEnabled(false);
        uploadprogress = findViewById(R.id.uploadprogress);
        //uploadprogress.setMin(0);

        uploadprogress.setMax(100);
        uploadprogress.setVisibility(View.GONE);


        showpercent = findViewById(R.id.percentview);
        dfustat = findViewById(R.id.dfu_stat);
        showpercent.setVisibility(View.GONE);
        dfustat.setVisibility(View.GONE);

        selFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openfilechooser();
            }
        });

        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUploadClicked();
            }
        });

        DfuServiceListenerHelper.registerProgressListener(this, mDfuProgressListener);

    }

    public void onUploadClicked(){
/*

        final boolean keepBond = preferences.getBoolean(SettingsFragment.SETTINGS_KEEP_BOND, false);
        final boolean forceDfu = preferences.getBoolean(SettingsFragment.SETTINGS_ASSUME_DFU_NODE, false);
        final boolean enablePRNs = preferences.getBoolean(SettingsFragment.SETTINGS_PACKET_RECEIPT_NOTIFICATION_ENABLED, Build.VERSION.SDK_INT < Build.VERSION_CODES.M);
        String value = preferences.getString(SettingsFragment.SETTINGS_NUMBER_OF_PACKETS, String.valueOf(DfuServiceInitiator.DEFAULT_PRN_VALUE));
        int numberOfPackets;
        try {
            numberOfPackets = Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            numberOfPackets = DfuServiceInitiator.DEFAULT_PRN_VALUE;
        }
*/
        uploadprogress.setIndeterminate(true);
        uploadprogress.setVisibility(View.VISIBLE);
        showpercent.setVisibility(View.VISIBLE);
        showpercent.setText(R.string.init_message_dfu);
        dfustat.setVisibility(View.VISIBLE);
        final DfuServiceInitiator starter = new DfuServiceInitiator(data.getMac_address())
                .setDeviceName(data.getName())
                .setKeepBond(false)
                .setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true)
                .setForceDfu(false);
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //starter.createDfuNotificationChannel(this);
        }
        //if(mfiletype == DfuService.TYPE_AUTO)
        //{
            starter.setZip(mFileStreamUri,mFilePath);
        //}
        starter.start(this, DfuService.class);

    }

    public void openfilechooser()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(mfiletypetmp == DfuService.TYPE_AUTO ? DfuService.MIME_TYPE_ZIP : DfuService.MIME_TYPE_OCTET_STREAM);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if(intent.resolveActivity(getPackageManager())!=null)
        {
            startActivityForResult(intent,SELECT_FILE_REQ);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if(resultCode!=RESULT_OK)
        {
            return;
        }

        switch(requestCode)
        {
            case SELECT_FILE_REQ:
            {
                mfiletype = mfiletypetmp;
                mFilePath = null;
                mFileStreamUri = null;

                final Uri uri = data.getData();
                //System.out.println("here "+uri.getScheme());

                if(uri.getScheme().equals("file"))
                {
                    final String path = uri.getPath();
                    final File file =new File(path);
                    mFilePath = path;
                    //System.out.println("here "+mFilePath);

                    updateFileInfo(file.getName(), file.length(), mfiletype);
                }
                else if(uri.getScheme().equals("content"))
                {
                    mFileStreamUri = uri;
                    mFilePath = uri.getPath();
                    final Bundle extras = data.getExtras();
                    if(extras!=null && extras.containsKey(Intent.EXTRA_STREAM))
                            mFileStreamUri = extras.getParcelable(Intent.EXTRA_STREAM);

                    final Bundle bundle = new Bundle();
                    bundle.putParcelable(EXTRA_URI,uri);
                    //System.out.println("here "+mFileStreamUri +" "+ mFilePath);

                    getLoaderManager().restartLoader(SELECT_FILE_REQ,bundle,  DfuActivityMain.this);

                }
                break;

            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateFileInfo(String name, long length, int filetype) {


        final String extension = mfiletype == DfuService.TYPE_AUTO ? "(?i)ZIP" : "(?i)HEX|BIN"; // (?i) =  case insensitive
        final boolean statusOk = mStatusOk = MimeTypeMap.getFileExtensionFromUrl(name).matches(extension);
        dfustat.setVisibility(View.VISIBLE);
        dfustat.setText(statusOk ? R.string.dfu_file_status_ok : R.string.dfu_file_status_invalid);
        if(statusOk)
        {
            uploadFile.setEnabled(true);

            if(filetype==DfuService.TYPE_AUTO)
            {
                mscope= null;
            }
        }

    }

    private final DfuProgressListener mDfuProgressListener = new DfuProgressListenerAdapter() {
        @Override
        public void onDeviceConnecting(final String deviceAddress) {
            //mProgressBar.setIndeterminate(true);
            //mTextPercentage.setText(R.string.dfu_status_connecting);
            Log.d("Connecting",deviceAddress);
        }

        @Override
        public void onDfuProcessStarting(final String deviceAddress) {
           // mProgressBar.setIndeterminate(true);
           // mTextPercentage.setText(R.string.dfu_status_starting);
            Log.d("DfuProcessStarting",deviceAddress);

        }

        @Override
        public void onEnablingDfuMode(final String deviceAddress) {
           // mProgressBar.setIndeterminate(true);
           // mTextPercentage.setText(R.string.dfu_status_switching_to_dfu);
            Log.d("Enabling Dfu Mode",deviceAddress);

        }

        @Override
        public void onFirmwareValidating(final String deviceAddress) {
            //mProgressBar.setIndeterminate(true);
           // mTextPercentage.setText(R.string.dfu_status_validating);
            Log.d("Validating Firmware",deviceAddress);

        }

        @Override
        public void onDeviceDisconnecting(final String deviceAddress) {
           // mProgressBar.setIndeterminate(true);
           // mTextPercentage.setText(R.string.dfu_status_disconnecting);
            Log.d("Device Disconnected",deviceAddress);

        }

        @Override
        public void onDfuCompleted(final String deviceAddress) {
           /* mTextPercentage.setText(R.string.dfu_status_completed);
            if (mResumed) {
                // let's wait a bit until we cancel the notification. When canceled immediately it will be recreated by service again.
                new Handler().postDelayed(() -> {
                    onTransferCompleted();

                    // if this activity is still open and upload process was completed, cancel the notification
                    final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(DfuService.NOTIFICATION_ID);
                }, 200);
            } else {
                // Save that the DFU process has finished
                mDfuCompleted = true;
            }*/
            uploadFile.setEnabled(false);
            showpercent.setText(getString(R.string.dfu_status_completed_msg));
            dfustat.setText(getString(R.string.dfu_status_completed));
            Log.d("Dfu Completed",deviceAddress);

        }

        @Override
        public void onDfuAborted(final String deviceAddress) {
            /*mTextPercentage.setText(R.string.dfu_status_aborted);
            // let's wait a bit until we cancel the notification. When canceled immediately it will be recreated by service again.
            new Handler().postDelayed(() -> {
                onUploadCanceled();

                // if this activity is still open and upload process was completed, cancel the notification
                final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(DfuService.NOTIFICATION_ID);
            }, 200);*/
            Log.d("dfu Aborted",deviceAddress);

        }

        @Override
        public void onProgressChanged(final String deviceAddress, final int percent, final float speed, final float avgSpeed, final int currentPart, final int partsTotal) {
            /*mProgressBar.setIndeterminate(false);
            mProgressBar.setProgress(percent);
            mTextPercentage.setText(getString(R.string.dfu_uploading_percentage, percent));*/
            if (partsTotal > 1)
                dfustat.setText(getString(R.string.dfu_status_uploading_part, currentPart, partsTotal));
            else
                dfustat.setText(R.string.dfu_status_uploading);
            Log.d("IN Process(Percent)",String.valueOf(percent));
            uploadprogress.setIndeterminate(false);
            uploadprogress.setProgress(percent);
            showpercent.setText(percent+getString(R.string.percent));


        }

        @Override
        public void onError(final String deviceAddress, final int error, final int errorType, final String message) {
            /*if (mResumed) {
                showErrorMessage(message);

                // We have to wait a bit before canceling notification. This is called before DfuService creates the last notification.
                new Handler().postDelayed(() -> {
                    // if this activity is still open and upload process was completed, cancel the notification
                    final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(DfuService.NOTIFICATION_ID);
                }, 200);
            } else {
                mDfuError = message;
            }*/
            uploadFile.setEnabled(false);
            uploadprogress.setIndeterminate(false);
            switch(errorType)
            {
                case 0:
                    uploadprogress.setIndeterminate(false);
                    showpercent.setText(getString(R.string.dfu_device_disconnected));
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    showpercent.setText(getString(R.string.dfu_sd_version_failure_error));
                    break;
            }
            //showpercent.setText(getString());
            Log.d("Error in DFU",message+" "+errorType);

        }
    };


    private boolean isDfuServiceRunning() {
        final ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (DfuService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final Uri uri = args.getParcelable(EXTRA_URI);
        /*
         * Some apps, f.e. Google Drive allow to select file that is not on the device. There is no "_data" column handled by that provider. Let's try to obtain
         * all columns and than check which columns are present.
         */
        // final String[] projection = new String[] { MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.SIZE, MediaStore.MediaColumns.DATA };
        return new CursorLoader(this, uri, null /* all columns, instead of projection */, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToNext()) {
            /*
             * Here we have to check the column indexes by name as we have requested for all. The order may be different.
             */
            final String fileName = data.getString(data.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)/* 0 DISPLAY_NAME */);
            final int fileSize = data.getInt(data.getColumnIndex(MediaStore.MediaColumns.SIZE) /* 1 SIZE */);
            String filePath = null;
            final int dataIndex = data.getColumnIndex(MediaStore.MediaColumns.DATA);
            if (dataIndex != -1)
                filePath = data.getString(dataIndex /* 2 DATA */);
            if (!TextUtils.isEmpty(filePath))
                mFilePath = filePath;

            updateFileInfo(fileName, fileSize, mfiletype);
        } else {
            //mFileNameView.setText(null);
            //mFileTypeView.setText(null);
            //mFileSizeView.setText(null);
            mFilePath = null;
            mFileStreamUri = null;
            dfustat.setText(R.string.dfu_file_status_invalid);
            mStatusOk = false;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //mFileNameView.setText(null);
        //mFileTypeView.setText(null);
        //mFileSizeView.setText(null);
        mFilePath = null;
        mFileStreamUri = null;
        mStatusOk = false;
    }
}
