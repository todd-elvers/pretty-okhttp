Vavr examples
-------------------

[Vavr](http://www.vavr.io/) is a great way to make Java more functional,
reusable, and readable.  The following are the equivalent examples from
the main readme re-written using vavr. 


#### Performing a GET to a URL and passing along the status code:

```java
class WebsiteHealthChecker implements HttpRequestHandling {
    public Optional<Integer> fetchStatusCodeFromService(String url) {
        return Try.of(() -> executeGET(url))
                .map(HttpResponse::getStatusCode)
                .toJavaOptional();
    }
}
```

<br/>

#### Performing a POST of a form to a webservice and retrying on server errors:

```java
class FormUploader implements HttpRequestHandling {

    private Integer uploadFormWithRetry(String url, Map<String, ?> formData) {
        return Try.of(() -> executeGET(url))
                .mapTry(httpResponse -> executeFormPOST(url, formData))
                .map(HttpResponse::getStatusCode)
                .recoverWith(r -> Match(r).of(
                        Case($(instanceOf(HttpServerException.class)), handleRetry),
                        Case($(instanceOf(NoResponseException.class)), handleRetry)
                ))
                .get();
    }
    
}
```

<br/>

#### Making a authenticated request to a webservice & marshalling the resulting JSON into an object:

```java
class UserRetriever implements HttpRequestHandling {
    
    // Customize the default headers to include some authentication
    @Override
    public Headers getDefaultHeaders() {
        return new Headers.Builder()
            .addAll(Defaults.jsonHeaders)   
            .add("Authorization", "...")     
            .build();
    }
    
    public Optional<User> retrieve(String url) {
        return Try.of(() -> executeGET(url))
                .mapTry(httpResponse -> fromJson(httpResponse.getBodyOrNull(), User.class))
                .recoverWith(r -> Match(r).of(
                     Case($(instanceOf(HttpClientException.class)), handle400),
                     Case($(instanceOf(HttpServerException.class)), handle500),
                     Case($(instanceOf(NoResponseException.class)), handleNetworkError)
                ))
                .toJavaOptional();
    }
    
    
    static class User {
        private String username;
        
        // Map "website" in JSON to this field
        @SerializedName("website")  
        private  String url;
        
        // Getters/Setters etc.
    }
        
}
```