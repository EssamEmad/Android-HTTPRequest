package null;


import android.os.AsyncTask;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by essam on 11/14/15.
 * Custom class for API requests using HTTP in an AsyncTask
 */
public class APIRequest extends AsyncTask<Void,Void,JSONObject> {

    /**
     * Interface for calback methods for the pre execute and post execute methods in the AsyncTask
     */

    public  interface Delegate{
        public void onPreExecute();
        public void onPostExecute(JSONObject object);
    }

    public APIRequest(Delegate delegate){
        this.mcallback = delegate;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        URL url = null;
        JSONObject object = null;
        InputStream inStream = null;
        String stringUrl = this.getUrl();
        try {
            url = new URL(stringUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(this.getRequestType());
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.connect();
            inStream = urlConnection.getInputStream();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
            String temp, response = "";
            while ((temp = bReader.readLine()) != null) {
                response += temp;
            }
            object = (JSONObject) new JSONTokener(response).nextValue();
        } catch (Exception ignored) {
        } finally {
            if (inStream != null) {
                try {
                    // this will close the bReader as well
                    inStream.close();
                } catch (IOException ignored) {

                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return object;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mcallback.onPreExecute();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        mcallback.onPostExecute(jsonObject);
    }
    //Helper Methods
    //Setters

    /**
     * Name: setRequestType
     * Sets the request type POST or GET
     * @param type of the request
     */
    public void setRequestType(String type){
        this.requestType = type;
    }
    /**
     * Name: setBaseURL
     * Sets the base URL without adding any parameters. The URL should end with a ? character
     * @param  URL
     */
    public APIRequest setBaseURL(String URL){
        this.baseURL = URL;
        return this;
    }
    /**
     * Name: addParam
     * Adds a param as a key value pair
     */
    public APIRequest addParam(String param, String value){
        this.parameters.put(param,value);
        return this;
    }

    //Getters
    /**
     * Name: getRequestType
     * @return String of the current type
     */
    public String getRequestType(){
        return this.requestType;
    }
    /**
     * Name: getBaseURL
     * @return the base URL without adding any parameters. The URL should end with a ? character
     *
     */
    public String getBaseURL(){
        return this.baseURL;
    }

    /**
     * Name: withURL
     * Call it if you want to provide a full url to be used
     */
    public APIRequest withUrl(String url){
        this.providedURL = url;
        this.urlIsProvided = true;
        return this;
    }

    /**
     * Name: executeRequest
     * executes the request in background
     */
    public  APIRequest executeRequest(){
        this.execute();
        return this;
    }

    //private methods
    private String getUrl(){
        if(!urlIsProvided) {
            String url = this.baseURL + "?";
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                String param = entry.getKey();
                String value = entry.getValue();
                url = url + param +"=" + value + "&";
            }

            //remove the extra &
            return url.substring(0, url.length() - 1);
        } else
            return this.providedURL;

    }



    //Instance Variables

    private String requestType = "POST";
    private String baseURL;
    private String URL;
    private HashMap<String,String> parameters = new HashMap<>();
    private Delegate mcallback;
    private boolean urlIsProvided = false;
    private String providedURL;

}
