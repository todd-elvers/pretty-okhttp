pretty-okhttp
---------------------------------

Combines the powers of [OkHttp](http://square.github.io/okhttp/), 
[Java 8 interfaces](https://docs.oracle.com/javase/tutorial/java/IandI/defaultmethods.html),
and Google's [Gson](https://github.com/google/gson) to create a thread-safe,  
easy-to-use, and easy-to-test Java 8 interface for handling HTTP requests.


<br/>

## Why not just use the libraries directly?

### OkHttp

[OkHttp](http://square.github.io/okhttp/) is a great, thread-safe library for handling HTTP requests, 
but it requires a lot of boilerplate

##### Making it better:
Implement OkHttp behind a Java 8 interface, which simultaneously requires 
no configuration while also allowing for a high degree of customization.


### Gson

[Gson](https://github.com/google/gson) is a great library for object serialization to/from JSON, 
but does not support date/time serialization out-of-the-box.

##### Making it better:
Serialization was added for Java's Date, LocalDate, and LocalDateTime classes to the Gson instance
that is available after implementing `HttpRequestHandling` (or just simply `JsonMarshalling`). 

###### Serialization:
During serialization the above date/time classes are sent over the wire as as 
[ISO-8601](https://en.wikipedia.org/wiki/ISO_8601) strings. See the classes in 
[this package](https://github.com/todd-elvers/pretty-okhttp/tree/master/src/main/java/te/http/handling/serialization) 
for more details. To override this behavior entirely override the `getJsonMarshaller()`
method. 

###### Deserialization:
During de-serialization Gson will try the Unix epoch, the ISO-8601 format, and 
then a few other common formats (e.g. "MM/dd/yyyy", etc.).  See the classes in 
[this package](https://github.com/todd-elvers/pretty-okhttp/tree/master/src/main/java/te/http/handling/deserialization) 
for more details. To override this behavior entirely override the `getJsonMarshaller()`
method.

So the **tl;dr** here is that we cover some general cases the original libraries
do not cover, simplify use of those libraries, and then alter some of the behavior
of those libraries.  OkHttp was just ok, this is pretty-ok. 

<br/>

## How to use it

1. Make your class implement `HttpRequestHandling`
    * Override any of the default behavior if necessary (e.g. `getDefaultHeader()`, etc.) 
2. Make a request to your desired endpoint 
    * For GET requests = `executeGET`
    * For POST requests = `executePOST`, or `executeFormPOST`
3. You're done! Optionally you could marshall the resulting JSON into an object or list of objects
    * Marshalling JSON = `toJson`, `fromJson`, or `fromJsonList`

<br/>

## Examples

I'm a big fan of functional programming and have come to love the functional 
Try from [vavr](http://www.vavr.io/), so all the examples below contain a
traditional Java implementation and then also the vavr equivalent.  

<br/>

#### Performing a GET to a URL and passing along the status code:

```java
class WebsiteHealthChecker implements HttpRequestHandling {
    
    // Traditional implementation
    public Optional<Integer> fetchStatusCodeFromService(String url) {
        try {
            HttpResponse httpResponse = executeFormPOST(url, formData);
            
            return Optional.of(httpResponse.getStatusCode());
        } catch(IOException ex) {
            // Do nothing
        }
        
        return Optional.empty();
    }
    
    // Vavr implementation
    public Optional<Integer> fetchStatusCodeFromService(String url) {
        return Try.of(() -> executeGET(url))
                .map(HttpResponse::getStatusCode)
                .toJavaOptional();
    }
    
}
```

And an associated, yet completely bogus, Spock test illustrating how testing works:

```groovy
class WebsiteHealthCheckerTest extends Specification {
    
    void "returns empty optional when service returns non-200"() {
        given:
        def websiteChecker = new WebsiteHealthChecker() {
            @Override
            HttpResponse executeGET(String url) {
                throw new HttpClientException("failed, non-200")
            }   
        }
        
        when:
        Optional<Integer> result = websiteChecker.fetchStatusCodeFromService("some-url")
        
        then: 'the exception was swallowed and we return an empty optional'
        noExceptionsThrown()
        !result.isPresent()
        ...
    }
    
}
```


<br/>

#### Performing a POST of a form to a webservice and retrying on server errors:

```java
class FormUploader implements HttpRequestHandling {
    
    // Traditional implementation
    private Integer uploadFormWithRetry(String url, Map<String, ?> formData, int retryCount) {
        try {
            return executeFormPOST(url, formData).getStatusCode();
        } catch(HttpClientException ex) {
            // 400 error - we don't normally want to retry on these
        } catch(NoResponseException | HttpServerException ex) {
            // 500 error or no response - retry up to 4 times
            handleRetry();
        }
    }
    
    // Vavr implementation
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
    
    // Traditional implementation
    public Optional<User> retrieve(String url) {
        User user = null;

        try {
            HttpResponse httpResponse = executeGET(url);
            user = fromJson(httpResponse.getBodyOrNull(), User.class);
        } catch(HttpClientException ex) {
            handle400();
        } catch(HttpServerException ex) {
            handle500();
        } catch(NoResponseException ex) {
            handleNetworkError();
        }

        return Optional.ofNullable(user);
    }
    
    // Vavr implementation
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
    * Add `compile 'com.github.todd-elvers:pretty-okhttp:3.0.0'`
