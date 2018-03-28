package com.nicolasbahamon.cryptocoins.network;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.nicolasbahamon.cryptocoins.Aplicacion;
import com.nicolasbahamon.cryptocoins.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by Nicolas Bahamon on 10/14/16.
 * Way to connect to web server HttpURLConnection no deprecated
 */

public class HttpClient {

    private class FileProperties{
        public String uri;
        public String idActivity;

        public FileProperties(){}
    }

    /**
     * Constant definitions
     */
    private static final String BIKOURLDOMAIN = "https://client.bikoapp.com/";
    private static final String HEADBIKO = "Biko-User";

    //services
    private static final String LOGIN = "api/v1/user/login/";
    public int responseCode = 0;


    //variables
    public SharedPreferences diskDataBikoUser;
    public SharedPreferences.Editor diskEditor;
    public Aplicacion aplicacion;
    public Context mContext;
//******************************** methods ****************************************
    public HttpClient(Context context, Aplicacion _aplicacion){

        aplicacion = _aplicacion;
        mContext = context;
    }
    public HttpClient(Context context){

        mContext = context;
    }

    /**
     * Transforma el imputstream en string para ser procesado
     * @param inputStream imputstream con respuesta servidor
     * @return string
     * @throws IOException
     */
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line;
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    /**
     * Custom Post methot to request info from a url and return a String to be procesed
     * @param UrlHttp Url del web services a procesar
     * @param objects json con los objetos a enviar al servidor
     * @param fileUpload
     * @return string con lo descargado del servidor
     */
    private String HttpConnectPut(String UrlHttp, JSONObject objects, FileProperties fileUpload){
        InputStream in;
        int resCode;
    //    String headBiko = diskDataBikoUser.getString(Constants.USER_TOKEN,"");
        try {
            URL url = new URL(UrlHttp);
            URLConnection urlConn = url.openConnection();
            System.out.println("URL: "+UrlHttp);
            if (!(urlConn instanceof HttpURLConnection)) {
                throw new IOException("URL is not an Http URL");
            }
            HttpsURLConnection httpConn = (HttpsURLConnection) url.openConnection();
            httpConn.setDoOutput (true);
            String version = BuildConfig.VERSION_NAME;
            String androidOS = Build.VERSION.RELEASE;
            String language = Locale.getDefault().getLanguage();
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            String agent = "PloniexData/"+version+"(Android; "+androidOS+": "+manufacturer+"- "+model+")";
            httpConn.setRequestProperty("User-Agent",agent);
            httpConn.setRequestProperty("Accept", "application/json");
            httpConn.setRequestProperty("Accept-Language", language);
    //        httpConn.setRequestProperty(HEADBIKO, headBiko);
            httpConn.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=*****");

            httpConn.setInstanceFollowRedirects(false);
            httpConn.setRequestMethod("PUT");
            try {

                if(objects != null && fileUpload == null) {
                    OutputStream out = new BufferedOutputStream(httpConn.getOutputStream());
                    out.write(objects.toString().getBytes("UTF-8"));
                    out.close();
                }
                else if(fileUpload != null) {

                    String attachmentName = "photo_biko";
                    String attachmentFileName = "bitmap.jpg";
                    String crlf = "\r\n";
                    String twoHyphens = "--";
                    String boundary =  "*****";

                    InputStream iStream =   mContext.getContentResolver().openInputStream(Uri.parse(fileUpload.uri));
                    byte[] inputData = getBytes(iStream);

                    DataOutputStream request = new DataOutputStream(httpConn.getOutputStream());
                    request.writeBytes(twoHyphens + boundary + crlf);
                    request.writeBytes("Content-Disposition: form-data; name=\"" +
                            attachmentName + "\";filename=\"" +
                            attachmentFileName + "\"" + crlf);
                    request.writeBytes(crlf);
                    request.write(inputData);
                    request.writeBytes(crlf);
                    request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
                    request.flush();
                    request.close();



                }

                resCode = httpConn.getResponseCode();
                System.out.println("ResponseCode: "+UrlHttp+" = "+resCode);


                if (resCode == HttpURLConnection.HTTP_OK) {
                    in = httpConn.getInputStream();
                    String response = convertInputStreamToString(in);
                    System.out.println("Response from: "+UrlHttp+"= "+response);
                    return response;
                }
                else{
                    InputStream error = httpConn.getErrorStream();
                    String response = convertInputStreamToString(error);
                    System.out.println("Response Error from: "+UrlHttp+"= "+response);
                    return response;
                }


            } finally {
                httpConn.disconnect();
            }

        }

        catch (MalformedURLException e) {
            Log.e("MalformedURL",e.toString());
            e.printStackTrace();
        }

        catch (IOException e) {
            Log.e("IOException",e.toString());
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Custom Post methot to request info from a url and return a String to be procesed
     * @param fileUpload if upload image some extra data
     * @param UrlHttp Url del web services a procesar
     * @param objects json con los objetos a enviar al servidor
     * @return string con lo descargado del servidor
     */
    public String HttpsConnectPost(String UrlHttp, JSONObject objects, FileProperties fileUpload){
        InputStream in;
        int resCode;
     //   String headBiko = diskDataBikoUser.getString(Constants.USER_TOKEN,"");
        try {
            URL url = new URL(UrlHttp);
            URLConnection urlConn = url.openConnection();
            System.out.println("URL: "+UrlHttp);
            if (!(urlConn instanceof HttpURLConnection)) {
                throw new IOException("URL is not an Http URL");
            }
            HttpsURLConnection httpConn = (HttpsURLConnection) url.openConnection();
            httpConn.setDoOutput (true);
            String version = BuildConfig.VERSION_NAME;
            String androidOS = Build.VERSION.RELEASE;
            String language = Locale.getDefault().getLanguage();
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
    //         String agent = "Biko/"+version+"(Android; "+androidOS+": "+manufacturer+"- "+model+")";
    //        httpConn.setRequestProperty("User-Agent",agent);
            httpConn.setRequestProperty("Content-Type","application/json");
            httpConn.setRequestProperty("Accept", "application/json");
            httpConn.setRequestProperty("Accept-Language", language);
    //        httpConn.setRequestProperty(HEADBIKO, headBiko);
           /* if(fileUpload != null) {
                httpConn.setDoOutput(true);
                httpConn.setRequestProperty("Activity-Id", fileUpload.idActivity);
                if(objects != null){

                    try {
                        httpConn.setRequestProperty("name", objects.getString("name"));
                        httpConn.setRequestProperty("category", objects.getString("category"));
                        httpConn.setRequestProperty("latitude", objects.getString("latitude"));
                        httpConn.setRequestProperty("longitude", objects.getString("longitude"));
                        httpConn.setRequestProperty("comment", objects.getString("comment"));
                        httpConn.setRequestProperty("owner", objects.getString("owner"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                httpConn.setRequestProperty(
                        "Content-Type", "multipart/form-data;boundary=*****");
            }*/
            httpConn.setInstanceFollowRedirects(false);
            httpConn.setRequestMethod("POST");
            try {

                if(objects != null && fileUpload == null) {
                    OutputStream out = new BufferedOutputStream(httpConn.getOutputStream());
                    out.write(objects.toString().getBytes("UTF-8"));
                    out.close();
                }
                else if(fileUpload != null) {

                    String attachmentName = "photo_biko";
                    String attachmentFileName = "bitmap.jpg";
                    String crlf = "\r\n";
                    String twoHyphens = "--";
                    String boundary =  "*****";

                    InputStream iStream =   mContext.getContentResolver().openInputStream(Uri.parse(fileUpload.uri));
                    byte[] inputData = getBytes(iStream);

                    DataOutputStream request = new DataOutputStream(httpConn.getOutputStream());
                    request.writeBytes(twoHyphens + boundary + crlf);
                    request.writeBytes("Content-Disposition: form-data; name=\"" +
                            attachmentName + "\";filename=\"" +
                            attachmentFileName + "\"" + crlf);
                    request.writeBytes(crlf);
                    request.write(inputData);
                    request.writeBytes(crlf);
                    request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
                    request.flush();
                    request.close();



                }

                resCode = httpConn.getResponseCode();
                System.out.println("ResponseCode: "+UrlHttp+"= "+resCode);

                responseCode = resCode;
                if (resCode == HttpURLConnection.HTTP_OK) {

                    in = httpConn.getInputStream();
                    String response = convertInputStreamToString(in);
                    System.out.println("Response from: "+UrlHttp+"= "+response);
                    return response;
                }
                else{
                    InputStream error = httpConn.getErrorStream();
                    String response = convertInputStreamToString(error);
                    System.out.println("Response Error from: "+UrlHttp+"= "+response);
                    return response;
                }


            } finally {
                httpConn.disconnect();
            }

        }

        catch (MalformedURLException e) {
            Log.e("MalformedURL",e.toString());
            e.printStackTrace();
        }

        catch (IOException e) {
            Log.e("IOException",e.toString());
            e.printStackTrace();
        }

        return null;
    }
    /**
     * Custom Post methot to request info from a url and return a String to be procesed
     * @param fileUpload if upload image some extra data
     * @param UrlHttp Url del web services a procesar
     * @param objects json con los objetos a enviar al servidor
     * @return string con lo descargado del servidor
     */
    public String HttpConnectPost(String UrlHttp, JSONObject objects, FileProperties fileUpload){
        InputStream in;
        int resCode;
        //   String headBiko = diskDataBikoUser.getString(Constants.USER_TOKEN,"");
        try {
            URL url = new URL(UrlHttp);
            URLConnection urlConn = url.openConnection();
            System.out.println("URL: "+UrlHttp);
            if (!(urlConn instanceof HttpURLConnection)) {
                throw new IOException("URL is not an Http URL");
            }
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setDoOutput (true);
            String version = BuildConfig.VERSION_NAME;
            String androidOS = Build.VERSION.RELEASE;
            String language = Locale.getDefault().getLanguage();
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            //         String agent = "Biko/"+version+"(Android; "+androidOS+": "+manufacturer+"- "+model+")";
            //        httpConn.setRequestProperty("User-Agent",agent);
            httpConn.setRequestProperty("Content-Type","application/json");
            httpConn.setRequestProperty("Accept", "application/json");
            httpConn.setRequestProperty("Accept-Language", language);
            //        httpConn.setRequestProperty(HEADBIKO, headBiko);
           /* if(fileUpload != null) {
                httpConn.setDoOutput(true);
                httpConn.setRequestProperty("Activity-Id", fileUpload.idActivity);
                if(objects != null){

                    try {
                        httpConn.setRequestProperty("name", objects.getString("name"));
                        httpConn.setRequestProperty("category", objects.getString("category"));
                        httpConn.setRequestProperty("latitude", objects.getString("latitude"));
                        httpConn.setRequestProperty("longitude", objects.getString("longitude"));
                        httpConn.setRequestProperty("comment", objects.getString("comment"));
                        httpConn.setRequestProperty("owner", objects.getString("owner"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                httpConn.setRequestProperty(
                        "Content-Type", "multipart/form-data;boundary=*****");
            }*/
            httpConn.setInstanceFollowRedirects(false);
            httpConn.setRequestMethod("POST");
            try {

                if(objects != null && fileUpload == null) {
                    OutputStream out = new BufferedOutputStream(httpConn.getOutputStream());
                    out.write(objects.toString().getBytes("UTF-8"));
                    out.close();
                }
                else if(fileUpload != null) {

                    String attachmentName = "photo_biko";
                    String attachmentFileName = "bitmap.jpg";
                    String crlf = "\r\n";
                    String twoHyphens = "--";
                    String boundary =  "*****";

                    InputStream iStream =   mContext.getContentResolver().openInputStream(Uri.parse(fileUpload.uri));
                    byte[] inputData = getBytes(iStream);

                    DataOutputStream request = new DataOutputStream(httpConn.getOutputStream());
                    request.writeBytes(twoHyphens + boundary + crlf);
                    request.writeBytes("Content-Disposition: form-data; name=\"" +
                            attachmentName + "\";filename=\"" +
                            attachmentFileName + "\"" + crlf);
                    request.writeBytes(crlf);
                    request.write(inputData);
                    request.writeBytes(crlf);
                    request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
                    request.flush();
                    request.close();



                }

                resCode = httpConn.getResponseCode();
                System.out.println("ResponseCode: "+UrlHttp+"= "+resCode);

                responseCode = resCode;
                if (resCode == HttpURLConnection.HTTP_OK) {

                    in = httpConn.getInputStream();
                    String response = convertInputStreamToString(in);
                    System.out.println("Response from: "+UrlHttp+"= "+response);
                    return response;
                }
                else{
                    InputStream error = httpConn.getErrorStream();
                    String response = convertInputStreamToString(error);
                    System.out.println("Response Error from: "+UrlHttp+"= "+response);
                    return response;
                }


            } finally {
                httpConn.disconnect();
            }

        }

        catch (MalformedURLException e) {
            Log.e("MalformedURL",e.toString());
            e.printStackTrace();
        }

        catch (IOException e) {
            Log.e("IOException",e.toString());
            e.printStackTrace();
        }

        return null;
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    /**
     * Custom Get methot to request info from a url and return a String to be procesed
     * @param UrlHttp Url for the web Service
     * @param objects Json to send to the web Service
     * @return string from server
     */
    public String HttpsConnectGet(String UrlHttp, JSONObject objects){

        URL url;
    //    String headBiko = diskDataBikoUser.getString(Constants.USER_TOKEN,"");
        int resCode;
        InputStream in;
        try {
            url = new URL(UrlHttp);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            String version = BuildConfig.VERSION_NAME;
            String androidOS = Build.VERSION.RELEASE;
            String language = Locale.getDefault().getLanguage();
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            String agent = "Biko/"+version+"(Android; "+androidOS+": "+manufacturer+"- "+model+")";
            urlConnection.setRequestProperty("User-Agent",agent);
            urlConnection.setRequestProperty("Content-Type","application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Accept-Language", language);
    //        urlConnection.setRequestProperty(HEADBIKO, headBiko);
            urlConnection.setInstanceFollowRedirects(false);
            urlConnection.setRequestMethod("GET");
            try {

                resCode = urlConnection.getResponseCode();
                System.out.println("ResponseCode: "+UrlHttp+"= "+resCode);

                responseCode = resCode;
                if (resCode == HttpURLConnection.HTTP_OK) {

                    in = urlConnection.getInputStream();
                    String response = convertInputStreamToString(in);
                    System.out.println("Response from: "+UrlHttp+"= "+response);
                    return response;
                }
                else{
                    InputStream error = urlConnection.getErrorStream();
                    String response = convertInputStreamToString(error);
                    System.out.println("Response Error from: "+UrlHttp+"= "+response);
                    return response;
                }

            } finally {
                urlConnection.disconnect();
            }
        } catch (MalformedURLException e) {
            Log.e("MalformedURL",e.toString());
            e.printStackTrace();
        }

        catch (IOException e) {
            Log.e("IOException",e.toString());
            e.printStackTrace();
        }


        return null;
    }
    public String HttpConnectGet(String UrlHttp, JSONObject objects){

        URL url;
        //    String headBiko = diskDataBikoUser.getString(Constants.USER_TOKEN,"");
        int resCode;
        InputStream in;
        try {
            url = new URL(UrlHttp);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            String version = BuildConfig.VERSION_NAME;
            String androidOS = Build.VERSION.RELEASE;
            String language = Locale.getDefault().getLanguage();
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            urlConnection.setRequestProperty("Content-Type","application/json");


            //        urlConnection.setRequestProperty(HEADBIKO, headBiko);
            urlConnection.setInstanceFollowRedirects(true);
            urlConnection.setRequestMethod("GET");
            try {

                resCode = urlConnection.getResponseCode();
                System.out.println("ResponseCode: "+UrlHttp+"= "+resCode);

                responseCode = resCode;
                if (resCode == HttpURLConnection.HTTP_OK) {

                    in = urlConnection.getInputStream();
                    String response = convertInputStreamToString(in);
                    System.out.println("Response from: "+UrlHttp+"= "+response);
                    return response;
                }
                else{
                    InputStream error = urlConnection.getErrorStream();
                    String response = convertInputStreamToString(error);
                    System.out.println("Response Error from: "+UrlHttp+"= "+response);
                    return response;
                }

            } finally {
                urlConnection.disconnect();
            }
        } catch (MalformedURLException e) {
            Log.e("MalformedURL",e.toString());
            e.printStackTrace();
        }

        catch (IOException e) {
            Log.e("IOException",e.toString());
            e.printStackTrace();
        }


        return null;
    }

}
