package com.xampr.Operations;

/**
 * Created by PCCS on 5/2/2017.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.xampr.MainActivity;
import com.xampr.R;

import javax.net.ssl.HttpsURLConnection;

public abstract class UploadFiles extends AsyncTask<String, Void, String> {
    public static JSONObject json, responseJson;
    public static String response;
    Activity act;
    String fileName, imagePath;
    HttpURLConnection connection = null;
    int serverResponseCode = 0;
    String serverResponseMessage;
    String resultdata = "";
    MainActivity mainActivity;
    ProgressDialog progressDialog;
    Context context;

    File sourceFile;

    public UploadFiles(String fName, String imagePaths, Context context) {
        fileName = fName;
        imagePath = imagePaths;
        mainActivity = new MainActivity();
        this.context = context;

        Log.i("tag", "filename" + fileName);
        Log.i("tag", "image path" + imagePath);
    }

    @Override
    protected String doInBackground(String... urls) {
       /// String result = POST_DATA("http://xamprblob.azurewebsites.net/api/BlobUpload/UploadFileToBlob");
        String result = POST_DATA(mainActivity.SITE_URL + context.getString(R.string.blob_url));


        //String result = POST_DATA(mainActivity.SITE_URL + act.getString(R.string.api_image_upload_blob));
        //String result = POST_DATA("http://xamprrelease02.cloudapp.net/api/BlobUpload/UploadFileToBlob");

        return result;
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        //  QKLogs.e(TAG, "OnPreExecute");
        Log.i("tag", "oncreate");
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

    }


    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        try {
            //  responseJson = new JSONObject(result);

            if (result != null) {
                progressDialog.dismiss();
                try {
                    String val = result.replace("\"", "");
                    onResponseReceived(val);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                progressDialog.dismiss();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Making POST request with given values
    public String POST_DATA(String requestedUrl) {


        String fileName1 = fileName;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;


        sourceFile = new File(imagePath);
        File file = new File(imagePath);
        boolean fileExist = file.exists();
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
                conn.setRequestProperty("random","123");
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

/********************** store images into gallery *************************************************/




                  /*
                        if (fileName.toString().endsWith(".jpg")
                                || fileName.toString().endsWith(".png")
                                || fileName.toString().endsWith(".jpeg")
                                || fileName.toString().endsWith(".gif")) {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 1;
                            try {
                                Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
                                mainActivity.SaveImage(bitmap, null, fileName, imagePath);
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        } else if (fileName.toString().endsWith("pdf")
                                || fileName.toString().endsWith("doc")
                                || fileName.toString().endsWith("docx")
                                || fileName.toString().endsWith("ppt")
                                || fileName.toString().endsWith("pptx")
                                || fileName.toString().endsWith("xls")
                                || fileName.toString().endsWith("xlsx")
                                || fileName.toString().endsWith(".txt")) {
                            File file = new File(imagePath);
                            byte[] imageInByte = FileUtils.readFileToByteArray(file);
                            mainActivity.SaveImage(null, imageInByte, fileName, null);
                        }
*/
                    /************************************end*****************************************************/
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