public class APIRequest extends AsyncTask<Void, Void, JSONObject> {
    
    /**
     * Interface for calback methods for the pre execute and post execute methods in the AsyncTask
     */
    
    public interface Delegate {
        public void onPreExecute();
        
        public void onPostExecute(JSONObject object);
    }
    
    public APIRequest(Delegate delegate) {
        this.mcallback = delegate;
    }
    
    @Override
    protected JSONObject doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        URL url = null;
        BufferedOutputStream os = null;
        JSONObject object = null;
        InputStream inStream = null;
        String stringUrl = this.getUrl();
        try {
            url = new URL(stringUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(this.getRequestType());
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            Log.e(" URL", stringUrl);
            if (attachJSON) {
                urlConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            }
            urlConnection.connect();
            if (attachJSON) {
            
                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(this.jsonToBeSent);
                writer.flush();

            }
            inStream = urlConnection.getInputStream();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
            String temp, response = "";
            while ((temp = bReader.readLine()) != null) {
                response += temp;
            }
            Log.e(" object", response);
            object = (JSONObject) new JSONTokener(response).nextValue();
        } catch (Exception ignored) {
            Log.e("Exception", ignored.getMessage());
        } finally {
            if (inStream != null) {
                try {
                    // this will close the bReader as well
                    if (os != null)
                        os.close();
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
    
    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
    //Helper Methods
    //Setters
    
    /**
     * Name: setRequestType
     * Sets the request type POST or GET
     *
     * @param type of the request
     */
    public void setRequestType(String type) {
        this.requestType = type;
    }
    
    /**
     * Name: setBaseURL
     * Sets the base URL without adding any parameters. The URL should end with a ? character
     *
     * @param URL
     */
    public APIRequest setBaseURL(String URL) {
        this.baseURL = URL;
        return this;
    }
    
    /**
     * Name: addParam
     * Adds a param as a key value pair
     */
    public APIRequest addParam(String param, String value) {
        this.parameters.put(param, value);
        return this;
    }
    
    //Getters
    
    /**
     * Name: getRequestType
     *
     * @return String of the current type
     */
    public String getRequestType() {
        return this.requestType;
    }
    
    /**
     * Name: getBaseURL
     *
     * @return the base URL without adding any parameters. The URL should end with a ? character
     */
    public String getBaseURL() {
        return this.baseURL;
    }
    
    /**
     * Name: withURL
     * Call it if you want to provide a full url to be used
     */
    public APIRequest withUrl(String url) {
        this.providedURL = url;
        this.urlIsProvided = true;
        return this;
    }
    
    /**
     * Name: executeRequest
     * executes the request in background
     */
    public APIRequest executeRequest() {
        this.execute();
        listOfRequests.add(this);
        return this;
    }
    
    public APIRequest executePoolAndCancelRest() {
        for (int i = 0; i < listOfRequests.size(); i++)
            listOfRequests.remove(i).cancel(false);
        this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        listOfRequests.add(this);
        return this;
    }
    
    /**
     * Method: attachJSON
     * If the client wants to send a JSON in the With a Header Content Type:
     * application/json; charset=utf-8
     *
     * @param jsonToBeSent the JSON to be sent
     * @return
     */
    public APIRequest attachJSON(String jsonToBeSent) {
        this.jsonToBeSent = jsonToBeSent;
        this.attachJSON = true;
        return this;
    }
    
    /**
     * Method: cancelRequest
     * Cancels the current request.
     * @param mayInterrupt true if we can interrupt the request, while cancelling.
     * @return
     */
    public APIRequest cancelRequest(boolean mayInterrupt){
        this.cancel(mayInterrupt);
        return this;
    }
    //
    
    //private methods
    private String getUrl() {
        if (!urlIsProvided) {
            String url = this.baseURL + "?";
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                String param = entry.getKey();
                String value = entry.getValue();
                url = url + param + "=" + value + "&";
            }
            
            //remove the extra &
            return HelperMethods.replaceWhiteSpaces(url.substring(0, url.length() - 1));
        } else
            return this.providedURL;
        
    }
    
    
    
    //Instance Variables
    
    private String requestType = "POST";
    private String baseURL;
    private String URL;
    private HashMap<String, String> parameters = new HashMap<>();
    private Delegate mcallback;
    private boolean urlIsProvided = false;
    private boolean attachJSON = false;
    private String jsonToBeSent;
    private String providedURL;
    private static ArrayList<APIRequest> listOfRequests = new ArrayList<>();
}