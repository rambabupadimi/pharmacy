package com.pharmacy.operations;

/**
 * Created by PCCS on 5/2/2017.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


import com.pharmacy.CommonMethods;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class UploadFiles extends AsyncTask<String, Void, String> {
    Activity act;
    String fileName, imagePath;
   int serverResponseCode = 0;
    String serverResponseMessage;
    String resultdata = "";
    Context context;

    File sourceFile;

    public UploadFiles(String fName, String imagePaths, Context context) {
        fileName = fName;
        imagePath = imagePaths;
        this.context = context;

        Log.i("tag", "filename" + fileName);
        Log.i("tag", "image path" + imagePath);
    }

    @Override
    protected String doInBackground(String... urls) {
        String result = POST_DATA(CommonMethods.MEDIA_UPLOAD);
        return result;
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();

    }


    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        try {
            //  responseJson = new JSONObject(result);

                     onResponseReceived(result);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Making POST request with given values
    public String POST_DATA(String requestedUrl) {


        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;


        sourceFile = new File(imagePath);
        Log.i("source file", "source file" + sourceFile);
        if (!sourceFile.isFile()) {
            Log.i("tag", "it is not filename");
            Toast.makeText(act, "file not exist", Toast.LENGTH_LONG).show();
        } else {

            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(requestedUrl);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);
             //   conn.setRequestProperty("random","123");
                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd); // uploaded_file_name is the Name of the File to be uploaded

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // lastReadTimeTics file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                serverResponseMessage = conn.getResponseMessage();
                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);
                if (serverResponseCode == 200) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        resultdata += line;
                    }

                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                Log.i("upload file ", "error start");
                ex.printStackTrace();
                Log.i("upload file ", "error end");
                Toast.makeText(act, "Upload failed", Toast.LENGTH_LONG).show();
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.i(" UploadFile.java", "Upload Exception" + ex.getMessage());
            }


        } // End else block
        return resultdata;

    }


    public abstract void onResponseReceived(String result);
}