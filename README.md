# Android-HTTPRequest
## Description
Generic HTTP request class for Android. You can use it for API calls.
## Install
- Download or pull the APIRequest file.
- Put the class in your package.
- Rename the package to be your package name instead of null
## Usage
The request type is by default POST. (No need to call setRequestType if it is POST)
``` java
       new APIRequest(new APIRequest.Delegate() {
            @Override
            public void onPreExecute() {
                // do anything before executing the request. (UI thread)
            }

            @Override
            public void onPostExecute(JSONObject object) {
              // called after the request is executed, with the result object
            }
        }).setBaseURL("https://maps.googleapis.com/maps/api/directions/json?")
        .setRequestType("POST")
        .addParam("origin","torronto")
        .addParam("destination","Montreal")
        .executeRequest();
    ```
    
    
If you have a full url you can call the withUrl method:
``` java
       new APIRequest(new APIRequest.Delegate() {
            @Override
            public void onPreExecute() {
                // do anything before executing the request. (UI thread)
            }

            @Override
            public void onPostExecute(JSONObject object) {
              // called after the request is executed, with the result object
            }
        }).withUrl("https://maps.googleapis.com/maps/api/directions/json?origin=Toronto&destination=Montreal")
        .executeRequest();
    ```
    
