pretty-okhttp
---------------------------------

Combines the powers of [OkHttp](http://square.github.io/okhttp/), 
[Java 8 interfaces](https://docs.oracle.com/javase/tutorial/java/IandI/defaultmethods.html),
and Google's [GSON](https://github.com/google/gson) to create a thread-safe,  
easy-to-use, and easy-to-test Java 8 interface for handling HTTP requests.


<br/>


## How to use it

1. Implement `HttpRequestHandling` in your class 
    * Customize any of the default behavior if necessary (`getDefaultHeader()`, etc.) 
2. Make a request to your desired endpoint 
    * For GET requests = `executeGET`
    * For POST requests = `executePOST`, or `executeFormPOST`
3. Optionally marshall the resulting JSON into an object or list of objects
    * Marshalling JSON = `toJson`, `fromJson`, or `fromJsonList`

<br/>

## Examples

I'm a big fan of functional programming and have come to love the functional 
Try from [vavr](http://www.vavr.io/), so all the examples below contain a
traditional Java implementation and then also the vavr equivalent.  

<br/>

#### Performing a health check on a url:

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

And an associated, yet completely bogus, Spock test illustrating how testing works:

```groovy
class WebsiteHealthCheckerTest extends Specification {
    
    void "rollback database if service returns a non-200"() {
        given:
        def websiteChecker = new WebsiteHealthChecker() {
            @Override
            HttpResponse executeGET(String url) {
                throw new Non200ResponseException("failed, non-200")
            }   
        }
        
        when:
        websiteChecker.isWebsiteHealthy("some-url")
        
        then:
        thrown(Non200ResponseException)
        ...
    }
    
}
```


<br/>

#### POST a form to a webservice:

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
Due to Java's type-erasure we have to use `fromJsonList(json, YourObject.class)` if the 
JSON we have is a list of objects instead of an object itself.

<br/>

## Defaults

#### Headers
* `Accept` = `application/json` 
* `Content-Type` = `application/json`

Override `getDefaultHeaders()` to change this.

#### OkHttp
* The default OkHttp configuration is used

Override `getHttpClient()` to change this.

#### Gson
* Serialization & de-serialization support added for `Date`, `LocalDate`, and `LocalDateTime`

Override `getJsonMarshaller()` to change this.

<br/>

## Adding this to your project

In your `build.gradle` file:
* Under `repositories`
    * Add `maven { url "https://jitpack.io" }`, making sure it's the _last_ repo declared
* Under `dependencies`
    * Add `compile 'com.github.todd-elvers:pretty-okhttp:2.0.0'`