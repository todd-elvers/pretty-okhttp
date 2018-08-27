pretty-okhttp
---------------------------------

Combines the powers of [OkHttp](http://square.github.io/okhttp/), 
[Java 8 interfaces](https://docs.oracle.com/javase/tutorial/java/IandI/defaultmethods.html),
and Google's [GSON](https://github.com/google/gson) to create a thread-safe, simple, 
easy-to-use, and easy-to-test interface with built-in JSON 
serialization/deserialization.


<br/>


## How to use it

* Add `implements HttpRequestHandling` to your class declaration 
* If necessary customize any of the default behavior (headers, etc.) 
* Call the relevant `executeGET`, `executePOST`, or `executeFormPOST` method
    * If you need more customized behavior you can build the `Request` object yourself
    and then call `executeRequest` with it.
* You'll get one of the following back:
    * An `HttpResponse` object with the response of the HTTP request
    * A `Non200ResponseException` if the response returned a non-200 value
    * A `ServiceUnavailableException` if the request failed completely

That's it!

#### Finer details
* The `HttpResponse` also contains the `Request` that was used to get it
* The `Non200ResponseException` class contains the `HttpResponse` from the webservice so we 
can inspect the request & response further if necessary
* The `ServiceUnavailableException` class contains the exception that was thrown and the
`Request` that failed


<br/>

## Examples

I'm a big fan of functional programming and have come to love the functional 
Try from [vavr](http://www.vavr.io/), so all the examples below contain a
traditional Java implementation and then also the vavr equivalent.  

<br/>

##### Performing a health check on a url:

```java
class WebsiteHealthChecker implements HttpRequestHandling {
    
    // Traditional implementation
    public boolean isWebsiteHealthy(String url) {
        try {
            executeGET(url);
            return true;
        } catch(Non200ResponseException | ServiceUnavailableException ex) {
            return false;
        }
    }
    
    // Vavr implementation
    public boolean isWebsiteHealthy(String url) {
        return Try.of(() -> executeGET(url))
                        .toJavaOptional()
                        .isPresent();
    }
    
}
```

<br/>

##### POST a form to a webservice:

```java
class FormUploader implements HttpRequestHandling {
    
    // Traditional implementation
    public Integer uploadForm(String url, Map<String, ?> formData) {
        try {
            HttpResponse httpResponse = executeFormPOST(url, formData);
            
            return httpResponse.getStatusCode();
        } catch(Non200ResponseException | ServiceUnavailableException ex) {
            return null;
        }
    }
    
    // Vavr implementation
    public Integer uploadForm(String url, Map<String, ?> formData) {
        return Try.of(() -> executeFormPOST(url, formData))
                .mapTry(HttpResponse::getStatusCode)
                .getOrNull();
    }
    
}
```

<br/>

##### Making a authenticated request to a webservice & marshalling the response:

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
    
    // Traditional implementation
    public Optional<User> retrieve(String url) {
        User user = null;
        
        try {
            HttpResponse httpResponse = executeGET(url);
            user = fromJson(httpResponse.getBody(), User.class);
        } catch(Non200ResponseException | ServiceUnavailableException ex) {
            // ...
        }
        
        return Optional.ofNullable(user);
    }
    
    // Vavr implementation
    public Optional<User> retrieve(String url) {
        return Try.of(() -> executeGET(url))
                .mapTry(httpResponse -> fromJson(httpResponse.getBody(), User.class))
                .toJavaOptional();
    }
    
    
    static class User {
        public String username;
        
        // Map "website" in JSON to this field
        @SerializedName("website")  
        public String url;
    }
        
}
```


#### Marshalling JSON to a list
Due to Java's type-erasure we have to use `fromJsonList(json, SomeObject.class)` if the 
JSON we have is a list of objects.

<br/>

## Adding this to your project

In your `build.gradle` file:
* Under `repositories`
    * Add `maven { url "https://jitpack.io" }`, making sure it's the _last_ repo declared
* Under `dependencies`
    * Add `compile 'com.github.todd-elvers:pretty-okhttp:1.0.0'`