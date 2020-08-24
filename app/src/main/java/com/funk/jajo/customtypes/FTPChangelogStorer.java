package com.funk.jajo.customtypes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.InetAddress;

public class FTPChangelogStorer {
    private static final String LOCAL_HOST = "fritz.box";
    private static final String HOSTNAME = "92.193.157.137";
    private static final String USERNAME = "JosFun";
    private static final String PASSWORD = "Tacomia77!?";
    private static final String DIR_NAME = "JaJo";
    private static final String FILE_NAME = "jajo_changelog.data";

    private FTPClient ftpClient;
    private ChangelogStorable storable;
    private Context context;
    /**
     * Check whether or not we have an existing internet connection on the phone
     * @param context - The context of the Activity the check is to be made for.
     * @return whether or not an internet connection exists
     */
    public static final boolean checkForInternetConnection ( Context context ) {

        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private void uploadStorable ( ) {
        String jsonFile = new Gson().toJson( storable );

        try{
            this.ftpClient.connect ( InetAddress.getByName(HOSTNAME));
            int reply = ftpClient.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)){
                Toast.makeText(this.context, "Could not sync with the server.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            try {
                this.ftpClient.connect(LOCAL_HOST);
                int reply = ftpClient.getReplyCode();

                if (!FTPReply.isPositiveCompletion(reply)) {
                    Toast.makeText(this.context, "Could not sync with the server.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch ( Exception f ) {
                e.printStackTrace();
                return;
            }
        }
        try {
            this.ftpClient.login ( USERNAME, PASSWORD );
            this.ftpClient.enterLocalPassiveMode();
            this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            FTPFile[] files = this.ftpClient.listDirectories();
            if ( !directoryExists( files, DIR_NAME )) {
                this.ftpClient.makeDirectory( DIR_NAME );
            }

            this.ftpClient.changeWorkingDirectory( DIR_NAME );

            InputStream input  = new ByteArrayInputStream( jsonFile.getBytes());
            this.ftpClient.deleteFile(FILE_NAME);

            this.ftpClient.storeFile( FILE_NAME, input);
            input.close();

            this.ftpClient.logout();
            this.ftpClient.disconnect();

        } catch ( Exception e ) {
            e.printStackTrace();
            Toast.makeText(this.context, "Could not sync with the server.", Toast.LENGTH_SHORT).show();
        }
    }

    private String downloadJSON ( ) {
        String jsonFile = "";

        try{
            this.ftpClient.connect ( InetAddress.getByName(HOSTNAME));
            int reply = ftpClient.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)){
                Toast.makeText(this.context, "Could not sync with the server.", Toast.LENGTH_SHORT).show();
                return "";
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            try {
                this.ftpClient.connect(LOCAL_HOST);
                int reply = ftpClient.getReplyCode();

                if (!FTPReply.isPositiveCompletion(reply)) {
                    Toast.makeText(this.context, "Could not sync with the server.", Toast.LENGTH_SHORT).show();
                    return "";
                }
            } catch ( Exception f ) {
                e.printStackTrace();
                return "";
            }
        }

        try {

            this.ftpClient.login ( USERNAME, PASSWORD );
            this.ftpClient.enterLocalPassiveMode();
            this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            FTPFile[] files = this.ftpClient.listDirectories();
            if ( !directoryExists( files, DIR_NAME )) {
                return "";
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            this.ftpClient.changeWorkingDirectory( DIR_NAME );
            this.ftpClient.retrieveFile( FILE_NAME, out);

            jsonFile = out.toString();

            this.ftpClient.logout();
            this.ftpClient.disconnect();

        } catch ( Exception e ) {
            e.printStackTrace();
            Toast.makeText(this.context, "Could not sync with the server.", Toast.LENGTH_SHORT).show();
        }

        return jsonFile;
    }

    /**
     * Test whether or not the specified directory name exists within the list of directories
     * @param dirs - The list of {@link FTPFile}s, organized as an array.
     * @param dirName  - The name of the {@link FTPFile} we want to search.
     * @return whether or not the specified {@link String} is the name of a directory in the
     *         list of {@link FTPFile}s
     */
    private boolean directoryExists ( FTPFile[] dirs, String dirName ) {
        for ( FTPFile f: dirs ) {
            if ( f.isDirectory() && f.getName().equals ( dirName)) return true;
        }
        return false;
    }


    public FTPChangelogStorer(ChangelogStorable storable, Context context ) {
        this.ftpClient = new FTPClient();
        this.storable = storable;
        this.context = context;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                FTPChangelogStorer.this.uploadStorable();
            }
        });
        t.start();

        try {
            t.join();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public FTPChangelogStorer(Context context ) {
        this.ftpClient = new FTPClient();
    }

    public ChangelogStorable getStorable ( ) {
        String storableString = "";
        FTPRunnable r = new FTPRunnable();

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
            storableString = r.getJsonFile();
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        if ( storableString.equals( "" )) {
            return null;
        } else {
            ChangelogStorable storable = null;
            try {
                storable = new Gson().fromJson ( storableString,
                        new TypeToken<ChangelogStorable>() {}.getType());
            } catch ( Exception e ) {
                e.printStackTrace();
                return null;
            }
            return storable;
        }

    }

    public class FTPRunnable implements Runnable {

        private String jsonFile;
        @Override
        public void run() {
            this.jsonFile = FTPChangelogStorer.this.downloadJSON();
        }

        public String getJsonFile ( ) { return this.jsonFile; }
    }
}
